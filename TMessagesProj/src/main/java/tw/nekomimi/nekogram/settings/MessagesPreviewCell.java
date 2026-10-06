package tw.nekomimi.nekogram.settings;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;

import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.helpers.TimeStringHelper;
import tw.nekomimi.nekogram.utils.UpdateUtil;
import xyz.nextalone.nagram.NaConfig;

@SuppressLint("ViewConstructor")
public class MessagesPreviewCell extends LinearLayout {

    private final INavigationLayout parentLayout;
    private final TLRPC.TL_messageMediaWebPage webPageMedia;
    private final ChatMessageCell[] cells = new ChatMessageCell[3];
    private final MessageObject[] messages = new MessageObject[3];
    private final Runnable refreshRunnable = this::refreshMessages;
    private final SharedPreferences.OnSharedPreferenceChangeListener preferencesListener = (preferences, key) -> {
        if (isPreviewSetting(key)) {
            removeCallbacks(refreshRunnable);
            post(refreshRunnable);
        }
    };
    private Boolean showSeconds;
    private Drawable backgroundDrawable;
    private Drawable oldBackgroundDrawable;
    private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
    private BackgroundGradientDrawable.Disposable oldBackgroundGradientDisposable;
    private final ValueAnimator heightAnimator = new ValueAnimator();
    // 内容变化导致的高度过渡：>= 0 时使用该高度参与测量，让列表逐帧平滑重新布局
    private int animatingHeight = -1;
    private int currentHeight;
    private int naturalHeight;
    private int targetHeight = -1;
    private boolean animateNextMeasure;

    public MessagesPreviewCell(Context context, INavigationLayout parentLayout, int account) {
        super(context);
        this.parentLayout = parentLayout;
        setWillNotDraw(false);
        setOrientation(VERTICAL);
        setPadding(0, AndroidUtilities.dp(11), 0, AndroidUtilities.dp(11));

        int date = (int) (System.currentTimeMillis() / 1000) - 3600;
        TLRPC.TL_message text = createMessage(42, date);
        String markedText = LocaleController.getString(R.string.MessagePreviewText);
        int spoilerStart = markedText.indexOf("||");
        int spoilerEnd = spoilerStart < 0 ? -1 : markedText.indexOf("||", spoilerStart + 2);
        if (spoilerEnd > spoilerStart) {
            text.message = markedText.substring(0, spoilerStart)
                    + markedText.substring(spoilerStart + 2, spoilerEnd)
                    + markedText.substring(spoilerEnd + 2);
            TLRPC.TL_messageEntitySpoiler spoiler = new TLRPC.TL_messageEntitySpoiler();
            spoiler.offset = spoilerStart;
            spoiler.length = spoilerEnd - spoilerStart - 2;
            text.entities.add(spoiler);
            text.flags |= TLRPC.MESSAGE_FLAG_HAS_ENTITIES;
        } else {
            text.message = markedText;
        }
        text.flags |= TLRPC.MESSAGE_FLAG_EDITED | TLRPC.MESSAGE_FLAG_FWD | TLRPC.MESSAGE_FLAG_HAS_VIEWS;
        text.edit_date = date + 60;
        text.views = 128;
        text.forwards = 12;
        text.fwd_from = new TLRPC.TL_messageFwdHeader();
        text.fwd_from.flags = 32;
        text.fwd_from.from_name = "monk 🥺";
        text.fwd_from.date = date - 86400;
        messages[0] = new MessageObject(account, text, true, false);

        TLRPC.TL_message pollMessage = createMessage(43, date + 120);
        pollMessage.flags |= TLRPC.MESSAGE_FLAG_HAS_MEDIA;
        TLRPC.TL_messageMediaPoll media = new TLRPC.TL_messageMediaPoll();
        media.poll = new TLRPC.TL_poll();
        media.poll.id = 1;
        media.poll.question.text = LocaleController.getString(R.string.MessagePreviewPollQuestion);
        media.results = new TLRPC.TL_pollResults();
        media.results.flags = 2 | 4;
        media.results.total_voters = 16;
        int[] answers = {R.string.MessagePreviewPollAnswerTerrible, R.string.MessagePreviewPollAnswerWorse};
        for (int i = 0; i < answers.length; i++) {
            TLRPC.TL_pollAnswer answer = new TLRPC.TL_pollAnswer();
            answer.text = new TLRPC.TL_textWithEntities();
            answer.text.text = LocaleController.getString(answers[i]);
            answer.option = new byte[]{(byte) i};
            media.poll.answers.add(answer);
            TLRPC.TL_pollAnswerVoters voters = new TLRPC.TL_pollAnswerVoters();
            voters.option = answer.option;
            voters.voters = i == 0 ? 11 : 5;
            media.results.results.add(voters);
        }
        pollMessage.media = media;
        messages[1] = new MessageObject(account, pollMessage, true, false);

        TLRPC.TL_message linkMessage = createMessage(44, date + 240);
        String url = "https://nagram.app";
        String appName = LocaleController.getString(R.string.NekoX);
        linkMessage.message = url;
        TLRPC.TL_messageEntityUrl urlEntity = new TLRPC.TL_messageEntityUrl();
        urlEntity.offset = 0;
        urlEntity.length = linkMessage.message.length();
        linkMessage.entities.add(urlEntity);
        linkMessage.flags |= TLRPC.MESSAGE_FLAG_HAS_ENTITIES;

        var webPage = new TLRPC.TL_webPage();
        webPage.description = "Hello " + appName;
        webPage.display_url = url;
        webPage.site_name = appName;
        webPage.title = appName + " App";
        webPage.type = "article";
        webPage.url = url;
        webPageMedia = new TLRPC.TL_messageMediaWebPage();
        webPageMedia.webpage = webPage;

        messages[2] = new MessageObject(account, linkMessage, true, false);

        TimeStringHelper.getForwardsDrawable();
        for (int i = 0; i < cells.length; i++) {
            messages[i].customName = "Nagram";
            messages[i].forceAvatar = true;
            cells[i] = new ChatMessageCell(context, account) {
                @Override
                public void onAttachedToWindow() {
                    super.onAttachedToWindow();
                    if (getAvatarImage() != null) {
                        getAvatarImage().setParentView(this);
                        getAvatarImage().setInvalidateAll(true);
                        getAvatarImage().setAllowLoadingOnAttachedOnly(false);
                    }
                }

                @Override
                protected void dispatchDraw(@NonNull Canvas canvas) {
                    if (getAvatarImage() != null && getAvatarImage().getImageHeight() != 0) {
                        getAvatarImage().setParentView(this);
                        getAvatarImage().setInvalidateAll(true);
                        getAvatarImage().setImageCoords(getAvatarImage().getImageX(), getMeasuredHeight() - getAvatarImage().getImageHeight() - AndroidUtilities.dp(4), getAvatarImage().getImageWidth(), getAvatarImage().getImageHeight());
                        getAvatarImage().setRoundRadius((int) (getAvatarImage().getImageHeight() / 2f));
                        getAvatarImage().draw(canvas);
                    }
                    super.dispatchDraw(canvas);
                }
            };
            if (cells[i].getAvatarImage() != null) {
                cells[i].getAvatarImage().setParentView(cells[i]);
                cells[i].getAvatarImage().setInvalidateAll(true);
                cells[i].getAvatarImage().setAllowLoadingOnAttachedOnly(false);
                cells[i].getAvatarImage().setDelegate((imageReceiver, set, thumb, memCache) -> {
                    invalidate();
                });
            }
            cells[i].setDelegate(new ChatMessageCell.ChatMessageCellDelegate() {});
            cells[i].isChat = true;
            cells[i].setFullyDraw(true);
            addView(cells[i], LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }

        heightAnimator.setDuration(200);
        heightAnimator.setInterpolator(AndroidUtilities.decelerateInterpolator);
        heightAnimator.addUpdateListener(animation -> {
            animatingHeight = Math.round((float) animation.getAnimatedValue());
            requestLayout();
            invalidate();
        });
        heightAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatingHeight = -1;
                requestLayout();
                invalidate();
            }
        });

        refreshMessages();
        loadPreviewChannel(account);
    }

    private void loadPreviewChannel(int account) {
        MessagesController controller = MessagesController.getInstance(account);
        TLObject cachedChannel = controller.getUserOrChat(UpdateUtil.channelUsername);
        if (cachedChannel instanceof TLRPC.Chat) {
            setPreviewChannel((TLRPC.Chat) cachedChannel);
        }
        controller.getUserNameResolver().resolve(UpdateUtil.channelUsername, peerId -> {
            if (peerId != null && peerId < 0) {
                TLRPC.Chat channel = controller.getChat(-peerId);
                if (channel != null) {
                    setPreviewChannel(channel);
                }
            }
        });
    }

    private void setPreviewChannel(TLRPC.Chat channel) {
        if (channel.photo != null) {
            FileLoader.getInstance(messages[0].currentAccount).loadFile(ImageLocation.getForChat(messages[0].currentAccount, channel, ImageLocation.TYPE_SMALL), channel, null, 1, 1);
        }
        for (MessageObject message : messages) {
            message.messageOwner.from_id.channel_id = channel.id;
        }
        refreshMessages();
    }

    private static TLRPC.TL_message createMessage(int id, int date) {
        TLRPC.TL_message message = new TLRPC.TL_message();
        message.id = id;
        message.date = date;
        message.dialog_id = 1;
        message.flags = TLRPC.MESSAGE_FLAG_HAS_FROM_ID;
        message.from_id = new TLRPC.TL_peerChannel();
        message.peer_id = new TLRPC.TL_peerUser();
        message.media = new TLRPC.TL_messageMediaEmpty();
        message.message = "";
        return message;
    }

    private static boolean isPreviewSetting(String key) {
        return NekoConfig.showSeconds.getKey().equals(key)
                || NaConfig.INSTANCE.getShowMessageID().getKey().equals(key)
                || NaConfig.INSTANCE.getDateOfForwardedMsg().getKey().equals(key)
                || NaConfig.INSTANCE.getShowForwardCount().getKey().equals(key)
                || NaConfig.INSTANCE.getShowEditedIcon().getKey().equals(key)
                || NaConfig.INSTANCE.getCustomEditedMessage().getKey().equals(key)
                || NaConfig.INSTANCE.getShowVoteCountBeforeVote().getKey().equals(key)
                || NekoConfig.showSpoilersDirectly.getKey().equals(key)
                || NaConfig.INSTANCE.getGlobalDisableLinkPreviews().getKey().equals(key);
    }

    public void refreshMessages() {
        if (showSeconds == null || showSeconds != NekoConfig.showSeconds.Bool()) {
            showSeconds = NekoConfig.showSeconds.Bool();
            LocaleController.getInstance().recreateFormatters();
        }
        if (NaConfig.INSTANCE.getGlobalDisableLinkPreviews().Bool()) {
            messages[2].messageOwner.flags &= ~TLRPC.MESSAGE_FLAG_HAS_MEDIA;
            messages[2].messageOwner.media = new TLRPC.TL_messageMediaEmpty();
        } else {
            messages[2].messageOwner.flags |= TLRPC.MESSAGE_FLAG_HAS_MEDIA;
            messages[2].messageOwner.media = webPageMedia;
        }
        for (int i = 0; i < cells.length; i++) {
            messages[i].isSpoilersRevealed = NekoConfig.showSpoilersDirectly.Bool();
            messages[i].resetLayout();
            messages[i].forceUpdate = true;
            cells[i].setMessageObject(messages[i], null, false, false, false);
            if (cells[i].getAvatarImage() != null) {
                cells[i].getAvatarImage().setParentView(cells[i]);
                cells[i].getAvatarImage().setInvalidateAll(true);
                cells[i].getAvatarImage().setAllowLoadingOnAttachedOnly(false);
                cells[i].getAvatarImage().setDelegate((imageReceiver, set, thumb, memCache) -> {
                    invalidate();
                });
            }
            cells[i].requestLayout();
        }
        // 下一次测量时按新内容计算高度，若与当前高度不同则做过渡动画
        animateNextMeasure = true;
        requestLayout();
        invalidate();
    }

    private void startHeightAnimation(int from, int to) {
        heightAnimator.cancel();
        heightAnimator.setFloatValues(from, to);
        targetHeight = to;
        animatingHeight = from;
        heightAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        naturalHeight = getMeasuredHeight();
        if (animatingHeight >= 0) {
            // 动画进行中，若内容再次变化则从当前动画高度继续过渡到新的自然高度
            setMeasuredDimension(getMeasuredWidth(), animatingHeight);
            if (animateNextMeasure) {
                animateNextMeasure = false;
                if (naturalHeight != targetHeight) {
                    startHeightAnimation(animatingHeight, naturalHeight);
                }
            }
            return;
        }
        if (animateNextMeasure) {
            animateNextMeasure = false;
            if (currentHeight > 0 && currentHeight != naturalHeight) {
                startHeightAnimation(currentHeight, naturalHeight);
                setMeasuredDimension(getMeasuredWidth(), currentHeight);
                return;
            }
        }
        currentHeight = naturalHeight;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (cells != null) {
            for (ChatMessageCell cell : cells) {
                if (cell != null) {
                    cell.invalidate();
                }
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NekoConfig.preferences.registerOnSharedPreferenceChangeListener(preferencesListener);
        refreshMessages();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable newDrawable = Theme.getCachedWallpaperNonBlocking();
        if (newDrawable != null && newDrawable != backgroundDrawable) {
            if (oldBackgroundGradientDisposable != null) {
                oldBackgroundGradientDisposable.dispose();
                oldBackgroundGradientDisposable = null;
            }
            oldBackgroundDrawable = null;
            if (Theme.isAnimatingColor()) {
                oldBackgroundDrawable = backgroundDrawable;
                oldBackgroundGradientDisposable = backgroundGradientDisposable;
            } else if (backgroundGradientDisposable != null) {
                backgroundGradientDisposable.dispose();
            }
            backgroundGradientDisposable = null;
            backgroundDrawable = newDrawable;
        }

        float progress = parentLayout == null ? 1f : parentLayout.getThemeAnimationValue();
        for (int i = 0; i < 2; i++) {
            Drawable drawable = i == 0 ? oldBackgroundDrawable : backgroundDrawable;
            if (drawable == null) {
                continue;
            }
            int alpha = drawable.getAlpha();
            drawable.setAlpha(i == 1 && oldBackgroundDrawable != null ? (int) (255 * progress) : 255);
            if (drawable instanceof ColorDrawable || drawable instanceof GradientDrawable || drawable instanceof MotionBackgroundDrawable) {
                drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                if (drawable instanceof BackgroundGradientDrawable) {
                    BackgroundGradientDrawable.Disposable disposable = ((BackgroundGradientDrawable) drawable).drawExactBoundsSize(canvas, this);
                    if (i == 0) {
                        oldBackgroundGradientDisposable = disposable;
                    } else {
                        backgroundGradientDisposable = disposable;
                    }
                } else {
                    drawable.draw(canvas);
                }
            } else if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmap = (BitmapDrawable) drawable;
                canvas.save();
                if (bitmap.getTileModeX() == Shader.TileMode.REPEAT) {
                    float scale = 2f / AndroidUtilities.density;
                    canvas.scale(scale, scale);
                    drawable.setBounds(0, 0, (int) Math.ceil(getMeasuredWidth() / scale), (int) Math.ceil(getMeasuredHeight() / scale));
                } else {
                    float scale = Math.max((float) getMeasuredWidth() / drawable.getIntrinsicWidth(), (float) getMeasuredHeight() / drawable.getIntrinsicHeight());
                    int width = (int) Math.ceil(drawable.getIntrinsicWidth() * scale);
                    int height = (int) Math.ceil(drawable.getIntrinsicHeight() * scale);
                    int x = (getMeasuredWidth() - width) / 2;
                    int y = (getMeasuredHeight() - height) / 2;
                    canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    drawable.setBounds(x, y, x + width, y + height);
                }
                drawable.draw(canvas);
                canvas.restore();
            }
            drawable.setAlpha(alpha);
        }
        if (oldBackgroundDrawable != null && progress >= 1f) {
            oldBackgroundDrawable = null;
            if (oldBackgroundGradientDisposable != null) {
                oldBackgroundGradientDisposable.dispose();
                oldBackgroundGradientDisposable = null;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        NekoConfig.preferences.unregisterOnSharedPreferenceChangeListener(preferencesListener);
        removeCallbacks(refreshRunnable);
        heightAnimator.cancel();
        animatingHeight = -1;
        if (backgroundGradientDisposable != null) {
            backgroundGradientDisposable.dispose();
            backgroundGradientDisposable = null;
        }
        if (oldBackgroundGradientDisposable != null) {
            oldBackgroundGradientDisposable.dispose();
            oldBackgroundGradientDisposable = null;
        }
        oldBackgroundDrawable = null;
        super.onDetachedFromWindow();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
    }
}
