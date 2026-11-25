/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package tw.nekomimi.nekogram.helpers;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.LongSparseArray;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.TranscribeButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import xyz.nextalone.nagram.NaConfig;

public class AyuFilter {

    /**
     * 单条消息参与匹配的最大字符数。
     * <p>
     * 取 32768 是为了覆盖富文本消息的长度上限（见 AppGlobalConfig.richMessageLengthLimit），
     * 便于将来适配长消息。但窗口越大，回溯风暴的破坏力也越强，
     * 因此必须配合 {@link #MATCH_BUDGET_MS} 的自适应收窄与线程池隔离来保证安全。
     */
    public static final int MAX_MATCH_WINDOW = 32768;
    /** 匹配窗口下限 */
    private static final int MIN_MATCH_WINDOW = 256;
    /** 单次正则匹配允许的最长耗时，超过则收窄窗口，让后续匹配保持可接受的开销 */
    private static final long MATCH_BUDGET_MS = 5;
    /** 单次正则匹配的硬超时，防止工作线程被永久占用 */
    private static final long MATCH_TIMEOUT_MS = 50;
    /** 保存前复杂度检查：探测文本长度 */
    private static final int PROBE_LENGTH = 8192;
    /** 保存前复杂度检查：每个长度的重复测量次数，取最小值以排除 JIT 预热干扰 */
    private static final int PROBE_REPEAT = 2;
    /** 保存前复杂度检查：判定为“过慢”的绝对耗时下限（毫秒） */
    private static final long PROBE_SLOW_MS = 10;
    /** 保存前复杂度检查：长度翻倍时耗时增长的倍率上限 */
    private static final int PROBE_GROWTH_LIMIT = 3;
    /** 保存前复杂度检查的单次测量时间预算（毫秒） */
    private static final long PROBE_TIMEOUT_MS = 400;
    /** 探测线程池繁忙时的返回值：本次无法测量，不能据此判定正则有问题 */
    private static final long PROBE_BUSY = -2;
    /** 探测资源繁忙的判定标记 */
    private static final String PROBE_BUSY_STR = "busy";

    /**
     * 执行用户正则的线程池。
     * <p>
     * ICU 原生正则一旦陷入回溯风暴就无法被中断，线程会一直被占用。
     * 因此这里使用较大的线程数 + SynchronousQueue：
     * - SynchronousQueue 不排队，避免堆积已经过期的任务；
     * - 线程数足够多时，个别失控的正则不会把正常的规则一起拖死；
     * - 全部线程都被病态正则占满时，提交会被拒绝，此时按“超时”处理并收窄窗口，
     *   不会把正常规则误判为不可用。
     */
    private static final ExecutorService matcherExecutor = new ThreadPoolExecutor(
            4, 4, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), r -> {
                Thread thread = new Thread(r, "AyuFilterMatcher");
                thread.setDaemon(true);
                thread.setPriority(Thread.MIN_PRIORITY);
                return thread;
            }, new ThreadPoolExecutor.AbortPolicy());

    /**
     * 保存前复杂度检查使用的线程池。
     * <p>
     * 同样使用固定线程数 + SynchronousQueue：探测病态正则时该线程会陷入回溯且无法中断，
     * 若用单线程池，一条病态规则就会把后续所有探测全部堵死（正常规则被误判为 timeout）。
     */
    private static final ExecutorService probeExecutor = new ThreadPoolExecutor(
            4, 4, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), r -> {
                Thread thread = new Thread(r, "AyuFilterProbe");
                thread.setDaemon(true);
                thread.setPriority(Thread.MIN_PRIORITY);
                return thread;
            }, new ThreadPoolExecutor.AbortPolicy());
    public static ArrayList<FilterModel> getRegexFilters() {
        var str = NaConfig.INSTANCE.getRegexFiltersData().String();

        FilterModel[] arr = new Gson().fromJson(str, FilterModel[].class);

        return new ArrayList<>(Arrays.stream(arr).filter(f -> f != null && f.regex != null).collect(Collectors.toList()));
    }

    public static void addFilter(String text, boolean caseInsensitive) {
        var list = getRegexFilters();

        FilterModel filterModel = new FilterModel();
        filterModel.regex = text;
        filterModel.caseInsensitive = caseInsensitive;
        filterModel.enabledGroups = new ArrayList<>();
        filterModel.disabledGroups = new ArrayList<>();
        filterModel.enabledGroups.add(0L);
        list.add(0, filterModel);
        var str = new Gson().toJson(list);
        NaConfig.INSTANCE.getRegexFiltersData().setConfigString(str);

        AyuFilter.rebuildCache();
    }

    public static void editFilter(int filterIdx, String text, boolean caseInsensitive) {
        var list = getRegexFilters();

        FilterModel filterModel = list.get(filterIdx);
        filterModel.regex = text;
        filterModel.caseInsensitive = caseInsensitive;

        var str = new Gson().toJson(list);
        NaConfig.INSTANCE.getRegexFiltersData().setConfigString(str);

        AyuFilter.rebuildCache();
    }

    public static void saveFilter(ArrayList<FilterModel> filterModels1) {
        var str = new Gson().toJson(filterModels1);
        NaConfig.INSTANCE.getRegexFiltersData().setConfigString(str);

        AyuFilter.rebuildCache();
    }

    public static void removeFilter(int filterIdx) {
        var list = getRegexFilters();
        list.remove(filterIdx);

        var str = new Gson().toJson(list);
        NaConfig.INSTANCE.getRegexFiltersData().setConfigString(str);

        AyuFilter.rebuildCache();
    }

    /**
     * 检查用户填写的正则是否属于会拖垮界面的病态写法。
     * <p>
     * 典型例子是 {@code (?:(?:[\s\S]*?关键词A|关键词B|...))}：
     * 前缀 {@code [\s\S]*?} 会让匹配在最坏情况下退化为平方级复杂度，
     * 文本越长耗时越久，长消息上足以卡住主线程数秒。
     * <p>
     * 判定依据是「耗时随文本长度的增长倍率」：正常正则呈线性增长，
     * 灾难性回溯则接近平方增长（长度翻倍耗时约四倍）或指数增长。
     *
     * @return 返回 null 表示该正则可以安全使用，否则返回给用户看的原因
     */
    public static String detectPathologicalRegex(String regex, boolean caseInsensitive) {
        if (TextUtils.isEmpty(regex)) {
            return null;
        }

        Pattern pattern;
        try {
            var flags = Pattern.MULTILINE;
            if (caseInsensitive) {
                flags |= Pattern.CASE_INSENSITIVE;
            }
            pattern = Pattern.compile(regex, flags);
        } catch (Exception e) {
            // 语法错误由调用方单独处理
            return null;
        }

        // 使用多种探测串，取最坏结果：
        // 单一探测串可能被正则中的字面量提前命中，掩盖掉回溯问题。
        for (var probe : buildProbeTexts(regex, PROBE_LENGTH)) {
            String reason = detectWithProbe(pattern, probe);
            if (reason == null) {
                continue;
            }
            if (PROBE_BUSY_STR.equals(reason)) {
                // 探测资源繁忙，本次无法判定：保守放行，交由运行时兜底
                return null;
            }
            return reason;
        }

        return null;
    }

    /**
     * 用一组探测文本（长度分别为 len 与 2*len）检测该正则是否病态。
     *
     * @return null 表示安全，"timeout" 表示超时，"slow" 表示增长过快，
     * {@link #PROBE_BUSY_STR} 表示探测资源繁忙、本次无法判定
     */
    private static String detectWithProbe(Pattern pattern, String probeText) {
        long cost1 = measureMatchCost(pattern, probeText);
        if (cost1 == PROBE_BUSY) {
            return PROBE_BUSY_STR;
        }
        if (cost1 < 0) {
            return "timeout";
        }
        long cost2 = measureMatchCost(pattern, probeText + probeText);
        if (cost2 == PROBE_BUSY) {
            return PROBE_BUSY_STR;
        }
        if (cost2 < 0) {
            return "timeout";
        }
        if (cost2 >= PROBE_SLOW_MS && cost2 > cost1 * PROBE_GROWTH_LIMIT) {
            return "slow";
        }
        return null;
    }

    /**
     * 构造多种用于探测的最坏情况文本。
     * <p>
     * 之所以要多种：单一探测串可能被正则里的字面量提前命中，
     * 导致回溯根本没展开就被短路，从而漏判病态正则。
     * 这里覆盖「混合文本」「重复单字符」「重复单字符+干扰尾巴」三种形态，
     * 并额外加入「由正则自身字面量构成的重复串」，用于暴露指数级回溯
     * （例如 {@code (?:a|a)*b}，只有在全是 a 且没有 b 的文本上才会真正展开回溯）。
     */
    private static String[] buildProbeTexts(String regex, int length) {
        StringBuilder mixed = new StringBuilder(length);
        String unit = "abcdefghijklmnopqrstuvwxyz0123456789 \n测试内容不含任何关键词";
        while (mixed.length() < length) {
            mixed.append(unit);
        }
        String mixedText = mixed.substring(0, length);

        // 重复同一个字符：绝大多数交替分支无法提前命中，回溯会完整展开
        StringBuilder repeated = new StringBuilder(length);
        while (repeated.length() < length) {
            repeated.append('x');
        }
        String repeatedText = repeated.toString();

        // 重复字符 + 结尾放一个必然失败的断言，进一步逼出回溯
        StringBuilder tail = new StringBuilder(repeatedText);
        tail.setLength(length - 1);
        tail.append('￿');

        java.util.ArrayList<String> probes = new java.util.ArrayList<>();
        probes.add(mixedText);
        probes.add(repeatedText);
        probes.add(tail.toString());

        // 用正则自身出现的字面量重复成串，覆盖指数级回溯
        for (char c : extractLiteralChars(regex)) {
            StringBuilder sb = new StringBuilder(length);
            while (sb.length() < length) {
                sb.append(c);
            }
            probes.add(sb.toString());
            break; // 只取第一个字面量，避免探测次数过多
        }

        return probes.toArray(new String[0]);
    }

    /**
     * 从正则表达式中提取字面量字符（排除元字符与转义序列），
     * 用于构造「能触发该正则自身回溯」的探测文本。
     */
    private static char[] extractLiteralChars(String regex) {
        java.util.LinkedHashSet<Character> chars = new java.util.LinkedHashSet<>();
        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            if (c == '\\') {
                i++; // 跳过转义序列
                continue;
            }
            if ("()[]{}.*+?|^$".indexOf(c) >= 0) {
                continue;
            }
            if (Character.isLetterOrDigit(c)) {
                chars.add(c);
                if (chars.size() >= 8) {
                    break;
                }
            }
        }
        char[] result = new char[chars.size()];
        int index = 0;
        for (char c : chars) {
            result[index++] = c;
        }
        return result;
    }

    /**
     * 测量一次匹配耗时（毫秒），超时返回 -1。
     * <p>
     * 取多次测量中的最小值：首次执行包含 JIT 预热开销，会显著抬高耗时并破坏增长倍率的判断，
     * 预热后的运行结果才反映真实的回溯代价。
     */
    private static long measureMatchCost(Pattern pattern, String text) {
        long best = Long.MAX_VALUE;
        for (int i = 0; i < PROBE_REPEAT; i++) {
            long cost = measureMatchCostOnce(pattern, text);
            if (cost == PROBE_BUSY) {
                // 探测资源繁忙，无法测量：跳过本条探测串，而不是判定为病态
                return PROBE_BUSY;
            }
            if (cost < 0) {
                return -1;
            }
            best = Math.min(best, cost);
        }
        return best == Long.MAX_VALUE ? 0 : best;
    }

    /** 执行一次匹配并返回耗时（毫秒），超时返回 -1，线程池繁忙返回 -2 */
    private static long measureMatchCostOnce(Pattern pattern, String text) {
        Future<Long> future;
        try {
            future = probeExecutor.submit(() -> {
                long start = System.nanoTime();
                pattern.matcher(text).find();
                return (System.nanoTime() - start) / 1_000_000;
            });
        } catch (RejectedExecutionException e) {
            // 探测线程被其他失控的正则占满：本次无法测量，不能据此判定本正则有问题
            return PROBE_BUSY;
        }
        try {
            return future.get(PROBE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            return -1;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1;
        } catch (ExecutionException e) {
            return -1;
        }
    }

    public static CharSequence getMessageText(MessageObject selectedObject, MessageObject.GroupedMessages selectedObjectGroup) {
        CharSequence messageTextToTranslate = null;
        if (selectedObject.type != MessageObject.TYPE_EMOJIS && selectedObject.type != MessageObject.TYPE_ANIMATED_STICKER && selectedObject.type != MessageObject.TYPE_STICKER) {
            messageTextToTranslate = getMessageCaption(selectedObject, selectedObjectGroup);
            if (messageTextToTranslate == null && selectedObject.isPoll()) {
                try {
                    TLRPC.Poll poll = ((TLRPC.TL_messageMediaPoll) selectedObject.messageOwner.media).poll;
                    StringBuilder pollText = new StringBuilder(poll.question.text).append("\n");
                    for (TLRPC.PollAnswer answer : poll.answers)
                        pollText.append("\n\uD83D\uDD18 ").append(answer.text.text);
                    messageTextToTranslate = pollText.toString();
                } catch (Exception ignored) {
                }
            }
            if (messageTextToTranslate == null && MessageObject.isMediaEmpty(selectedObject.messageOwner)) {
                messageTextToTranslate = getMessageContent(selectedObject);
            }
            if (messageTextToTranslate != null && Emoji.fullyConsistsOfEmojis(messageTextToTranslate)) {
                messageTextToTranslate = null;
            }
        }
        if (selectedObject.translated || selectedObject.isRestrictedMessage) {
            messageTextToTranslate = null;
        }
        return messageTextToTranslate;
    }

    private static CharSequence getMessageCaption(MessageObject messageObject, MessageObject.GroupedMessages group) {
        String restrictionReason = MessagesController.getInstance(UserConfig.selectedAccount).getRestrictionReason(messageObject.messageOwner.restriction_reason);
        if (!TextUtils.isEmpty(restrictionReason)) {
            return restrictionReason;
        }
        if (messageObject.isVoiceTranscriptionOpen() && !TranscribeButton.isTranscribing(messageObject)) {
            return messageObject.getVoiceTranscription();
        }
        if (messageObject.caption != null) {
            return messageObject.caption;
        }
        if (group == null) {
            return null;
        }
        CharSequence caption = null;
        for (int a = 0, N = group.messages.size(); a < N; a++) {
            MessageObject message = group.messages.get(a);
            if (message.caption != null) {
                if (caption != null) {
                    return null;
                }
                caption = message.caption;
            }
        }
        return caption;
    }

    private static CharSequence getMessageContent(MessageObject messageObject) {
        SpannableStringBuilder str = new SpannableStringBuilder();
        String restrictionReason = MessagesController.getInstance(UserConfig.selectedAccount).getRestrictionReason(messageObject.messageOwner.restriction_reason);
        if (!TextUtils.isEmpty(restrictionReason)) {
            str.append(restrictionReason);
        } else if (messageObject.caption != null) {
            str.append(messageObject.caption);
        } else {
            str.append(messageObject.messageText);
        }
        return str.toString();
    }

    private static ArrayList<FilterModel> filterModels;
    private static LongSparseArray<HashMap<Integer, Boolean>> filteredCache;

    public static void rebuildCache() {
        ArrayList<FilterModel> loaded = getRegexFilters();
        ArrayList<FilterModel> ready = new ArrayList<>(loaded.size());

        for (var filter : loaded) {
            if (filter == null || filter.regex == null) {
                continue;
            }
            try {
                filter.buildPattern();
                ready.add(filter);
            } catch (Exception ignored) {
            }
        }

        filterModels = ready;
        filteredCache = new LongSparseArray<>();
    }

    /**
     * 判断文本是否命中任意一条规则。
     *
     * @return {@link Boolean#TRUE} 命中，{@link Boolean#FALSE} 未命中，
     * {@code null} 表示结果不确定（有规则因线程池繁忙未能执行），此时不应缓存结果
     */
    private static Boolean isFiltered(CharSequence text, long dialogId) {
        if (!NaConfig.INSTANCE.getRegexFiltersEnabled().Bool()) {
            return false;
        }

        if (TextUtils.isEmpty(text)) {
            return false;
        }

        boolean uncertain = false;
        for (var pattern : filterModels) {
            if (!pattern.isEnabled(dialogId)) {
                continue;
            }
            Boolean matched = pattern.matches(text);
            if (matched == null) {
                // 该规则本次未真正执行，结果不确定
                uncertain = true;
                continue;
            }
            if (matched) {
                return true;
            }
        }

        return uncertain ? null : false;
    }

    /**
     * 执行一次带超时的正则匹配，并返回实际耗时。
     * <p>
     * 用户自定义的正则可能是灾难性回溯（例如 {@code (?:[\s\S]*?A|B|C)} 这类写法），
     * 在长文本上耗时随长度呈平方级增长，直接在主线程调用 find() 会卡住整帧并最终触发 ANR。
     * 这里把匹配放到后台线程执行，主线程最多只等待 {@link #MATCH_TIMEOUT_MS}。
     */
    private static MatchResult matchWithTimeout(Pattern pattern, CharSequence text) {
        Future<MatchResult> future;
        try {
            future = matcherExecutor.submit(() -> {
                long start = System.nanoTime();
                boolean found = pattern.matcher(text).find();
                return new MatchResult(found, (System.nanoTime() - start) / 1_000_000);
            });
        } catch (RejectedExecutionException e) {
            // 线程池已被其他失控的正则占满：这不代表本条规则有问题，
            // 只是暂时排不上队，不能据此判定本规则不可用。
            return MatchResult.busy();
        }
        try {
            return future.get(MATCH_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            return MatchResult.timeout();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return MatchResult.timeout();
        } catch (ExecutionException e) {
            return MatchResult.timeout();
        }
    }

    private static class MatchResult {
        final boolean found;
        final long elapsedMs;
        final boolean timedOut;
        /** 线程池被其他失控正则占满，本次未真正执行 */
        final boolean busy;

        MatchResult(boolean found, long elapsedMs) {
            this.found = found;
            this.elapsedMs = elapsedMs;
            this.timedOut = false;
            this.busy = false;
        }

        private MatchResult(boolean found, long elapsedMs, boolean timedOut, boolean busy) {
            this.found = found;
            this.elapsedMs = elapsedMs;
            this.timedOut = timedOut;
            this.busy = busy;
        }

        static MatchResult timeout() {
            return new MatchResult(false, MATCH_TIMEOUT_MS, true, false);
        }

        static MatchResult busy() {
            return new MatchResult(false, 0, false, true);
        }
    }

    public static boolean isFiltered(MessageObject msg, MessageObject.GroupedMessages group) {
        if (!NaConfig.INSTANCE.getRegexFiltersEnabled().Bool()) {
            return false;
        }

        if (msg == null) {
            return false;
        }

        if (filterModels == null) {
            rebuildCache();
        }

        Boolean res;

        long dialogId = msg.getDialogId();
        var cached = filteredCache.get(dialogId);
        if (cached != null) {
            res = cached.get(msg.getId());
            if (res != null) {
                return res;
            }
        }

        res = isFiltered(getMessageText(msg, group), dialogId);
        if (res == null) {
            // 结果不确定（线程池繁忙），不写入缓存，避免把“未过滤”当成最终结论
            return false;
        }

        if (cached == null) {
            cached = new HashMap<>();
            filteredCache.put(dialogId, cached);
        }

        cached.put(msg.getId(), res);

        if (group != null && group.messages != null && !group.messages.isEmpty()) {
            for (var m : group.messages) {
                cached.put(m.getId(), res);
            }
        }

        return res;
    }

    public static class FilterModel {
        @Expose
        public String regex;
        @Expose
        public boolean caseInsensitive;
        @Expose
        public ArrayList<Long> enabledGroups;
        @Expose
        public ArrayList<Long> disabledGroups;
        public Pattern pattern;

        /** 本条规则当前允许匹配的最大文本长度，耗时超预算时逐步收窄 */
        transient int matchWindow = MAX_MATCH_WINDOW;
        /** 该规则是否已确认过于缓慢而被停用 */
        transient boolean disabled;

        public void buildPattern() {
            var flags = Pattern.MULTILINE;
            if (caseInsensitive) {
                flags |= Pattern.CASE_INSENSITIVE;
            }
            pattern = Pattern.compile(regex, flags);
            matchWindow = MAX_MATCH_WINDOW;
            disabled = false;
        }

        /**
         * 在受控的文本窗口内匹配本条规则。
         * <p>
         * 灾难性回溯的代价随输入长度急剧上升，这里先用较大的窗口尝试，
         * 一旦发现单次耗时超预算就收窄窗口，让后续匹配始终维持在可接受的开销内，
         * 保证主线程永远不会因为一条用户正则而卡死。
         *
         * @return {@link Boolean#TRUE} 命中，{@link Boolean#FALSE} 未命中，
         * {@code null} 表示本次因线程池繁忙未能执行，结果不确定
         */
        Boolean matches(CharSequence text) {
            if (pattern == null || disabled) {
                return false;
            }
            if (matchWindow <= 0) {
                matchWindow = MAX_MATCH_WINDOW;
            }
            CharSequence input = text.length() > matchWindow ? text.subSequence(0, matchWindow) : text;
            MatchResult result = matchWithTimeout(pattern, input);
            if (result.busy) {
                // 线程池被其他失控正则占满，本次未执行，不能据此判定本规则异常
                return null;
            }
            if (result.timedOut) {
                // 超时说明这条正则回溯发散，收窄窗口后重试
                if (matchWindow > MIN_MATCH_WINDOW) {
                    matchWindow = Math.max(MIN_MATCH_WINDOW, matchWindow / 4);
                    return matches(text);
                }
                FileLog.d("AyuFilter: regex disabled, it is too slow to match: " + regex);
                disabled = true;
                return false;
            }
            if (result.elapsedMs > MATCH_BUDGET_MS && matchWindow > MIN_MATCH_WINDOW) {
                // 单次匹配偏慢，后续收窄窗口以控制总开销
                matchWindow = Math.max(MIN_MATCH_WINDOW, matchWindow / 4);
            }
            return result.found;
        }

        public boolean defaultStatus() {
            return enabledGroups.contains(0L);
        }

        public boolean isEnabled(Long id) {
            boolean status = defaultStatus();
            if (id == 0L) return status;
            if (status) {
                if (disabledGroups.contains(id)) {
                    return false;
                }
            } else {
                if (enabledGroups.contains(id)) {
                    return true;
                }
            }
            return status;
        }

        public void setEnabled(boolean enabled, Long id) {
            enabledGroups.remove(id);
            disabledGroups.remove(id);
            if (enabled) {
                enabledGroups.add(id);
            } else {
                disabledGroups.add(id);
            }
        }
    }
}
