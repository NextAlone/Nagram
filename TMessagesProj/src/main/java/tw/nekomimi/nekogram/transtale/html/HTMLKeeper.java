/*
 * This is the source code of OctoGram for Android
 * It is licensed under GNU GPL v2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright OctoGram, 2023-2025.
 */

package tw.nekomimi.nekogram.transtale.html;

import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;

import androidx.core.text.HtmlCompat;

import cn.hutool.http.HtmlUtil;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MediaDataController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.TextStyleSpan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Converts between Telegram rich text (text + {@link TLRPC.MessageEntity}) and an HTML
 * representation that survives a round trip through a machine translation engine.
 */
public class HTMLKeeper {
    private static final String[] list_html_params = new String[]{"b", "i", "u", "s", "tt", "a", "q", "tg-emoji", "blockquote", "tg-pre"};

    private static final Pattern PATTERN_A_HREF = Pattern.compile("<a href=\".*?\">");
    private static final Pattern PATTERN_SPAN_COLOR_TO_Q = Pattern.compile("<span style=\"color:.*?;\">(.*?)</span>");
    private static final Pattern PATTERN_Q_TO_SPAN_COLOR = Pattern.compile("<q>(.*?)</q>");
    private static final Pattern PATTERN_TRAILING_NEWLINE = Pattern.compile("[\\n\\r]$");
    private static final Pattern PATTERN_NEWLINE_TO_BR = Pattern.compile("\\n");

    private static void toHtml(StringBuilder out, Spanned text, int end) {
        ArrayList<CharacterStyle> lastActiveSpans = new ArrayList<>();

        int next;
        for (int i = 0; i < end; i = next) {
            next = text.nextSpanTransition(i, end, CharacterStyle.class);
            CharacterStyle[] spans = text.getSpans(i, next, CharacterStyle.class);
            Arrays.sort(spans, (o1, o2) -> {
                int priority1 = getPriority(o1);
                int priority2 = getPriority(o2);
                return Integer.compare(priority2, priority1);
            });

            ArrayList<CharacterStyle> currentActiveSpans = new ArrayList<>(Arrays.asList(spans));

            for (int j = lastActiveSpans.size() - 1; j >= 0; j--) {
                CharacterStyle lastSpan = lastActiveSpans.get(j);
                if (!currentActiveSpans.contains(lastSpan)) {
                    closeTagFor(out, lastSpan);
                }
            }

            for (CharacterStyle currentSpan : currentActiveSpans) {
                if (!lastActiveSpans.contains(currentSpan)) {
                    openTagFor(out, currentSpan);
                }
            }

            out.append(HtmlUtil.escape(text.subSequence(i, next).toString()));

            lastActiveSpans = currentActiveSpans;
        }

        for (int j = lastActiveSpans.size() - 1; j >= 0; j--) {
            closeTagFor(out, lastActiveSpans.get(j));
        }
    }

    private static int getPriority(CharacterStyle span) {
        if (span instanceof PreSpan || span instanceof BlockquoteSpan || span instanceof AnimatedEmojiSpan) {
            return 2;
        } else if (span instanceof URLSpan) {
            return 1;
        }
        return 0;
    }

    private static void openTagFor(StringBuilder out, CharacterStyle span) {
        if (span instanceof StyleSpan) {
            int style = ((StyleSpan) span).getStyle();
            if ((style & Typeface.BOLD) != 0) {
                out.append("<b>");
            }
            if ((style & Typeface.ITALIC) != 0) {
                out.append("<i>");
            }
        } else if (span instanceof TypefaceSpan) {
            out.append("<tt>");
        } else if (span instanceof UnderlineSpan) {
            out.append("<u>");
        } else if (span instanceof StrikethroughSpan) {
            out.append("<s>");
        } else if (span instanceof URLSpan) {
            out.append("<a href=\"").append(HtmlUtil.escape(((URLSpan) span).getURL())).append("\">");
        } else if (span instanceof SpoilerSpan || span instanceof ForegroundColorSpan) {
            out.append("<q>");
        } else if (span instanceof BlockquoteSpan) {
            out.append("<blockquote>");
        } else if (span instanceof PreSpan) {
            String language = ((PreSpan) span).language;
            out.append("<tg-pre language=\"").append(HtmlUtil.escape(language != null ? language : "")).append("\">");
        } else if (span instanceof AnimatedEmojiSpan) {
            long documentId = ((AnimatedEmojiSpan) span).documentId;
            out.append("<tg-emoji emoji-id=\"").append(documentId).append("\">");
        }
    }

    private static void closeTagFor(StringBuilder out, CharacterStyle span) {
        if (span instanceof StyleSpan) {
            int style = ((StyleSpan) span).getStyle();
            if ((style & Typeface.BOLD) != 0) {
                out.append("</b>");
            }
            if ((style & Typeface.ITALIC) != 0) {
                out.append("</i>");
            }
        } else if (span instanceof TypefaceSpan) {
            out.append("</tt>");
        } else if (span instanceof UnderlineSpan) {
            out.append("</u>");
        } else if (span instanceof StrikethroughSpan) {
            out.append("</s>");
        } else if (span instanceof URLSpan) {
            out.append("</a>");
        } else if (span instanceof SpoilerSpan || span instanceof ForegroundColorSpan) {
            out.append("</q>");
        } else if (span instanceof BlockquoteSpan) {
            out.append("</blockquote>");
        } else if (span instanceof PreSpan) {
            out.append("</tg-pre>");
        } else if (span instanceof AnimatedEmojiSpan) {
            out.append("</tg-emoji>");
        }
    }

    public static String entitiesToHtml(String text, ArrayList<TLRPC.MessageEntity> entities, boolean includeLink) {
        if (text == null || entities == null || entities.isEmpty()) {
            return text;
        }
        text = text.replace("\n", "\u2029");
        if (!includeLink) {
            text = text.replace("<", "\u2027");
        }
        SpannableStringBuilder messSpan = SpannableStringBuilder.valueOf(text);

        ArrayList<TLRPC.MessageEntity> entitiesForOldLogic = new ArrayList<>();
        ArrayList<TLRPC.MessageEntity> entitiesForNewLogic = new ArrayList<>();

        for (TLRPC.MessageEntity entity : entities) {
            if (entity instanceof TLRPC.TL_messageEntityBlockquote ||
                    entity instanceof TLRPC.TL_messageEntityPre ||
                    entity instanceof TLRPC.TL_messageEntityCustomEmoji) {
                entitiesForNewLogic.add(entity);
            } else {
                entitiesForOldLogic.add(entity);
            }
        }

        MediaDataController.addTextStyleRuns(entitiesForOldLogic, text, messSpan);
        applyLegacyStyleRuns(messSpan, includeLink);
        applyModernEntities(messSpan, entitiesForNewLogic);

        StringBuilder out = new StringBuilder();
        toHtml(out, messSpan, messSpan.length());
        return normalizeHtml(out.toString(), includeLink);
    }

    private static String normalizeHtml(String html, boolean includeLink) {
        if (!includeLink) {
            html = PATTERN_A_HREF.matcher(html).replaceAll("<a>");
            html = PATTERN_SPAN_COLOR_TO_Q.matcher(html).replaceAll("<q>$1</q>");
            html = HtmlUtil.unescape(html);
        } else {
            html = html.replace("&#8233;", "\u2029");
        }
        return html.replace("\u2029", "\n");
    }

    private static void applyLegacyStyleRuns(SpannableStringBuilder messSpan, boolean includeLink) {
        CharacterStyle[] mSpans = messSpan.getSpans(0, messSpan.length(), CharacterStyle.class);
        for (CharacterStyle mSpan : mSpans) {
            if (!(mSpan instanceof TextStyleSpan)) {
                continue;
            }
            int start = messSpan.getSpanStart(mSpan);
            int end = messSpan.getSpanEnd(mSpan);
            int flags = ((TextStyleSpan) mSpan).getStyleFlags();
            boolean isBold = (flags & TextStyleSpan.FLAG_STYLE_BOLD) > 0;
            boolean isItalic = (flags & TextStyleSpan.FLAG_STYLE_ITALIC) > 0;
            if (isBold && !isItalic || isBold && !includeLink) {
                messSpan.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (!isBold && isItalic || isItalic && !includeLink) {
                messSpan.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (isBold && isItalic && includeLink) {
                messSpan.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if ((flags & TextStyleSpan.FLAG_STYLE_MONO) > 0) {
                messSpan.setSpan(new TypefaceSpan("monospace"), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if ((flags & TextStyleSpan.FLAG_STYLE_UNDERLINE) > 0) {
                messSpan.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if ((flags & TextStyleSpan.FLAG_STYLE_STRIKE) > 0) {
                messSpan.setSpan(new StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if ((flags & TextStyleSpan.FLAG_STYLE_SPOILER) > 0) {
                messSpan.setSpan(new SpoilerSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if ((flags & TextStyleSpan.FLAG_STYLE_URL) > 0) {
                String url = ((TextStyleSpan) mSpan).getTextStyleRun().urlEntity.url;
                if (url != null || !includeLink) {
                    messSpan.setSpan(new URLSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            if ((flags & TextStyleSpan.FLAG_STYLE_MENTION) > 0) {
                if (((TextStyleSpan) mSpan).getTextStyleRun().urlEntity instanceof TLRPC.TL_messageEntityMentionName) {
                    long id = ((TLRPC.TL_messageEntityMentionName) ((TextStyleSpan) mSpan).getTextStyleRun().urlEntity).user_id;
                    messSpan.setSpan(new URLSpan("tg://user?id=" + id), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }

    private static void applyModernEntities(SpannableStringBuilder messSpan, ArrayList<TLRPC.MessageEntity> entities) {
        for (TLRPC.MessageEntity entity : entities) {
            if (entity == null || entity.length <= 0 || entity.offset < 0 || entity.offset >= messSpan.length() || (entity.offset + entity.length) > messSpan.length()) {
                continue;
            }
            int start = entity.offset;
            int end = entity.offset + entity.length;
            if (entity instanceof TLRPC.TL_messageEntityBlockquote) {
                messSpan.setSpan(new BlockquoteSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (entity instanceof TLRPC.TL_messageEntityPre) {
                messSpan.setSpan(new PreSpan(entity.language), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (entity instanceof TLRPC.TL_messageEntityCustomEmoji) {
                messSpan.setSpan(new AnimatedEmojiSpan(((TLRPC.TL_messageEntityCustomEmoji) entity).document_id, null), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public static TLRPC.TL_textWithEntities htmlToEntities(String text, ArrayList<TLRPC.MessageEntity> entities, boolean internalLinks) {
        return htmlToEntities(text, entities, internalLinks, true);
    }

    public static TLRPC.TL_textWithEntities htmlToEntities(String text, ArrayList<TLRPC.MessageEntity> entities, boolean internalLinks, boolean withFixes) {
        if (text == null) {
            TLRPC.TL_textWithEntities empty = new TLRPC.TL_textWithEntities();
            empty.text = "";
            empty.entities = new ArrayList<>();
            return empty;
        }
        ArrayList<TLRPC.MessageEntity> returnEntities = new ArrayList<>();
        ArrayList<TLRPC.MessageEntity> copyEntities = entities != null ? new ArrayList<>(entities) : null;
        if (withFixes) {
            text = applyFixes(text);
        }
        text = text.replace("\u2027", "&lt;");
        text = text.replace("\u0327", "<");
        text = PATTERN_TRAILING_NEWLINE.matcher(text).replaceAll("");
        text = text.replace("\n", "<br/>");
        text = PATTERN_NEWLINE_TO_BR.matcher(text).replaceAll("<br/>");
        SpannableString htmlParsed = new SpannableString(fromHtml("<inject>" + text + "</inject>", new HTMLTagAttributesHandler(new CustomElementHandler())));
        if (internalLinks) {
            AndroidUtilities.addLinksSafe(htmlParsed, Linkify.ALL, false, true);
        }
        collectEntities(htmlParsed, copyEntities, returnEntities);

        Collections.sort(returnEntities, (o1, o2) -> Integer.compare(o1.offset, o2.offset));

        TLRPC.TL_textWithEntities result = new TLRPC.TL_textWithEntities();
        result.text = htmlParsed.toString();
        result.entities = returnEntities;
        return result;
    }

    private static String applyFixes(String text) {
        text = fixDoubleSpace(text);
        text = fixDoubleHtmlElement(text);
        text = fixStrangeSpace(text);
        text = fixHtmlCorrupted(text);
        text = text.replace("<a>", "<a href=\"https://telegram.org/\">");
        return PATTERN_Q_TO_SPAN_COLOR.matcher(text).replaceAll("<span style=\"color:#000000;\">$1</span>");
    }

    private static void collectEntities(SpannableString htmlParsed, ArrayList<TLRPC.MessageEntity> copyEntities, ArrayList<TLRPC.MessageEntity> returnEntities) {
        CharacterStyle[] mSpans = htmlParsed.getSpans(0, htmlParsed.length(), CharacterStyle.class);
        for (CharacterStyle mSpan : mSpans) {
            int start = htmlParsed.getSpanStart(mSpan);
            int end = htmlParsed.getSpanEnd(mSpan);
            if (start < 0 || end <= start) {
                continue;
            }
            TLRPC.MessageEntity entity = null;
            if (mSpan instanceof URLSpan) {
                entity = matchUrlEntity((URLSpan) mSpan, copyEntities);
            } else if (mSpan instanceof StyleSpan) {
                int style = ((StyleSpan) mSpan).getStyle();
                if ((style & Typeface.BOLD) != 0) {
                    TLRPC.TL_messageEntityBold bold = new TLRPC.TL_messageEntityBold();
                    bold.offset = start;
                    bold.length = end - start;
                    returnEntities.add(bold);
                }
                if ((style & Typeface.ITALIC) != 0) {
                    TLRPC.TL_messageEntityItalic italic = new TLRPC.TL_messageEntityItalic();
                    italic.offset = start;
                    italic.length = end - start;
                    returnEntities.add(italic);
                }
            } else if (mSpan instanceof TypefaceSpan) {
                entity = new TLRPC.TL_messageEntityCode();
            } else if (mSpan instanceof UnderlineSpan) {
                entity = new TLRPC.TL_messageEntityUnderline();
            } else if (mSpan instanceof StrikethroughSpan) {
                entity = new TLRPC.TL_messageEntityStrike();
            } else if (mSpan instanceof BlockquoteSpan) {
                entity = new TLRPC.TL_messageEntityBlockquote();
                entity.collapsed = true;
            } else if (mSpan instanceof PreSpan) {
                entity = new TLRPC.TL_messageEntityPre();
                entity.language = ((PreSpan) mSpan).language;
            } else if (mSpan instanceof SpoilerSpan || mSpan instanceof ForegroundColorSpan) {
                entity = new TLRPC.TL_messageEntitySpoiler();
            } else if (mSpan instanceof AnimatedEmojiSpan) {
                TLRPC.TL_messageEntityCustomEmoji customEmoji = new TLRPC.TL_messageEntityCustomEmoji();
                customEmoji.document_id = ((AnimatedEmojiSpan) mSpan).documentId;
                entity = customEmoji;
            }
            if (entity != null) {
                entity.offset = start;
                entity.length = end - start;
                returnEntities.add(entity);
            }
        }
    }

    private static TLRPC.MessageEntity matchUrlEntity(URLSpan urlSpan, ArrayList<TLRPC.MessageEntity> copyEntities) {
        if (copyEntities != null) {
            for (int i = 0; i < copyEntities.size(); i++) {
                TLRPC.MessageEntity oldEntity = copyEntities.get(i);
                TLRPC.MessageEntity entity = copyEntityKind(oldEntity);
                if (entity != null) {
                    copyEntities.remove(i);
                    return entity;
                }
            }
            return null;
        }
        TLRPC.TL_messageEntityTextUrl textUrl = new TLRPC.TL_messageEntityTextUrl();
        textUrl.url = urlSpan.getURL();
        return textUrl;
    }

    private static TLRPC.MessageEntity copyEntityKind(TLRPC.MessageEntity oldEntity) {
        if (oldEntity instanceof TLRPC.TL_messageEntityMentionName) {
            TLRPC.TL_messageEntityMentionName mentionName = new TLRPC.TL_messageEntityMentionName();
            mentionName.user_id = ((TLRPC.TL_messageEntityMentionName) oldEntity).user_id;
            return mentionName;
        } else if (oldEntity instanceof TLRPC.TL_inputMessageEntityMentionName) {
            TLRPC.TL_inputMessageEntityMentionName mentionName = new TLRPC.TL_inputMessageEntityMentionName();
            mentionName.user_id = ((TLRPC.TL_inputMessageEntityMentionName) oldEntity).user_id;
            return mentionName;
        } else if (oldEntity instanceof TLRPC.TL_messageEntityTextUrl) {
            TLRPC.TL_messageEntityTextUrl textUrl = new TLRPC.TL_messageEntityTextUrl();
            textUrl.url = oldEntity.url;
            return textUrl;
        } else if (oldEntity instanceof TLRPC.TL_messageEntityUrl) {
            return new TLRPC.TL_messageEntityUrl();
        } else if (oldEntity instanceof TLRPC.TL_messageEntityMention) {
            return new TLRPC.TL_messageEntityMention();
        } else if (oldEntity instanceof TLRPC.TL_messageEntityBotCommand) {
            return new TLRPC.TL_messageEntityBotCommand();
        } else if (oldEntity instanceof TLRPC.TL_messageEntityHashtag) {
            return new TLRPC.TL_messageEntityHashtag();
        } else if (oldEntity instanceof TLRPC.TL_messageEntityCashtag) {
            return new TLRPC.TL_messageEntityCashtag();
        } else if (oldEntity instanceof TLRPC.TL_messageEntityEmail) {
            return new TLRPC.TL_messageEntityEmail();
        } else if (oldEntity instanceof TLRPC.TL_messageEntityBankCard) {
            return new TLRPC.TL_messageEntityBankCard();
        } else if (oldEntity instanceof TLRPC.TL_messageEntityPhone) {
            return new TLRPC.TL_messageEntityPhone();
        }
        return null;
    }

    // VARIOUS HTML FIXERS
    private static String fixStrangeSpace(String string) {
        for (String list_param : list_html_params) {
            String fixedStart = String.format(Locale.US, "<%s>", list_param);
            String fixedEnd = String.format(Locale.US, "</%s>", list_param);
            string = string.replace(String.format(Locale.US, "< %s>", list_param), fixedStart);
            string = string.replace(String.format(Locale.US, "<%s >", list_param), fixedStart);
            string = string.replace(String.format(Locale.US, "< %s >", list_param), fixedStart);
            string = string.replace(String.format(Locale.US, "</ %s>", list_param), fixedEnd);
            string = string.replace(String.format(Locale.US, "< / %s>", list_param), fixedEnd);
            string = string.replace(String.format(Locale.US, "< /%s>", list_param), fixedEnd);
            string = string.replace(String.format(Locale.US, "< /%s >", list_param), fixedEnd);
            string = string.replace(String.format(Locale.US, "</%s >", list_param), fixedEnd);
            string = string.replace(String.format(Locale.US, "< / %s >", list_param), fixedEnd);
        }
        return string;
    }

    private static String fixDoubleSpace(String string) {
        for (String list_param : list_html_params) {
            string = string.replace(" <" + list_param + "> ", " <" + list_param + ">");
            string = string.replace(" </" + list_param + "> ", "</" + list_param + "> ");
        }
        string = string.replace("<a> ", "<a>");
        string = string.replace(" </a>", "</a> ");
        return string;
    }

    private static String fixDoubleHtmlElement(String string) {
        for (String list_param : list_html_params) {
            for (String list_param2 : list_html_params) {
                string = string.replace("<" + list_param + "-" + list_param2 + ">", "<" + list_param + "><" + list_param2 + ">");
                string = string.replace("</" + list_param + "-" + list_param2 + ">", "</" + list_param2 + "></" + list_param + ">");
            }
        }
        return string;
    }

    private static String fixHtmlCorrupted(String string) {
        try {
            ArrayList<String> listUnclosedTags = new ArrayList<>();
            ArrayList<String> listUnopenedTags = new ArrayList<>();
            HTMLTagStack stack = new HTMLTagStack();
            stack.parse(string);
            for (int i = 0; i < stack.stack.size(); i++) {
                HTMLTagPosition tagPosition = stack.stack.get(i);
                String rawTag = tagPosition.tag().replace("<", "").replace(">", "").trim();
                boolean isClosing = rawTag.startsWith("/");
                if (isClosing) {
                    rawTag = rawTag.substring(1).trim();
                }
                int spaceIdx = rawTag.indexOf(' ');
                String tagName = (spaceIdx != -1 ? rawTag.substring(0, spaceIdx) : rawTag).toLowerCase(Locale.US);

                if (!isClosing) {
                    listUnclosedTags.add(0, tagName);
                    listUnopenedTags.add(0, tagName);
                } else {
                    if (listUnclosedTags.contains(tagName)) {
                        listUnclosedTags.remove(tagName);
                        listUnopenedTags.remove(tagName);
                    } else if (!listUnclosedTags.isEmpty()) {
                        boolean isValidData = Arrays.asList(list_html_params).contains(tagName);
                        String tagToReplace;
                        if (!listUnclosedTags.isEmpty()) {
                            tagToReplace = "/" + listUnclosedTags.get(0);
                            listUnclosedTags.remove(0);
                        } else if (!listUnopenedTags.isEmpty() && isValidData) {
                            tagToReplace = listUnopenedTags.get(0);
                            listUnopenedTags.remove(0);
                        } else {
                            continue;
                        }
                        stack.replace(tagPosition.start(), tagPosition.end(), "<" + tagToReplace + ">");
                    }
                }
            }
            return stack.text;
        } catch (Throwable ignored) {
            return string;
        }
    }

    public static Spanned fromHtml(String source) {
        return fromHtml(source, null);
    }

    public static Spanned fromHtml(String source, Html.TagHandler tagHandler) {
        return HtmlCompat.fromHtml(source, HtmlCompat.FROM_HTML_MODE_LEGACY, null, tagHandler);
    }

    public static class BlockquoteSpan extends CharacterStyle {
        @Override
        public void updateDrawState(TextPaint ds) {
        }
    }

    public static class PreSpan extends CharacterStyle {
        public final String language;

        public PreSpan(String language) {
            this.language = language;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
        }
    }

    public static class SpoilerSpan extends CharacterStyle {
        @Override
        public void updateDrawState(TextPaint ds) {
        }
    }

    private static class HTMLTagPosition {
        private final int start;
        private final int end;
        private final String tag;

        HTMLTagPosition(int start, int end, String tag) {
            this.start = start;
            this.end = end;
            this.tag = tag;
        }

        int start() { return start; }
        int end() { return end; }
        String tag() { return tag; }
    }

    private static class HTMLTagStack {
        private final ArrayList<HTMLTagPosition> stack = new ArrayList<>();
        private String text;

        public void parse(String string) {
            stack.clear();
            int start;
            int end = 0;
            while (true) {
                start = string.indexOf("<", end);
                if (start == -1) {
                    break;
                }
                end = string.indexOf(">", start);
                if (end == -1) {
                    break;
                }
                String tag = string.substring(start + 1, end);
                stack.add(new HTMLTagPosition(start, end + 1, tag));
            }
            text = string;
        }

        public void replace(int start, int end, String string) {
            text = text.substring(0, start) + string + text.substring(end);
            parse(text);
        }
    }
}