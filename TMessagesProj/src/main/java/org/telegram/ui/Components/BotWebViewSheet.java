package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Locale;

public class BotWebViewSheet extends Dialog implements NotificationCenter.NotificationCenterDelegate {
    public final static int TYPE_WEB_VIEW_BUTTON = 0, TYPE_SIMPLE_WEB_VIEW_BUTTON = 1, TYPE_BOT_MENU_BUTTON = 2, TYPE_WEB_VIEW_BOT_APP = 3;

    public final static int FLAG_FROM_INLINE_SWITCH = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {
            TYPE_WEB_VIEW_BUTTON,
            TYPE_SIMPLE_WEB_VIEW_BUTTON,
            TYPE_BOT_MENU_BUTTON,
            TYPE_WEB_VIEW_BOT_APP
    })
    public @interface WebViewType {}

    private final static int POLL_PERIOD = 60000;

    private final static SimpleFloatPropertyCompat<BotWebViewSheet> ACTION_BAR_TRANSITION_PROGRESS_VALUE = new SimpleFloatPropertyCompat<BotWebViewSheet>("actionBarTransitionProgress", obj -> obj.actionBarTransitionProgress, (obj, value) -> {
        obj.actionBarTransitionProgress = value;
        obj.frameLayout.invalidate();

        obj.actionBar.setAlpha(value);

        obj.updateLightStatusBar();
    }).setMultiplier(100f);
    private float actionBarTransitionProgress = 0f;
    private SpringAnimation springAnimation;

    private Boolean wasLightStatusBar;

    private SizeNotifierFrameLayout frameLayout;

    private long lastSwipeTime;

    private ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer swipeContainer;
    private BotWebViewContainer webViewContainer;
    private ChatAttachAlertBotWebViewLayout.WebProgressView progressView;
    private Theme.ResourcesProvider resourcesProvider;
    private boolean ignoreLayout;

    private int currentAccount;
    private long botId;
    private long peerId;
    private long queryId;
    private int replyToMsgId;
    private boolean silent;
    private String buttonText;

    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint dimPaint = new Paint();
    private Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int actionBarColor;
    private Paint actionBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean overrideBackgroundColor;

    private ActionBar actionBar;
    private Drawable actionBarShadow;
    private ActionBarMenuSubItem settingsItem;

    private boolean dismissed;

    private Activity parentActivity;

    private boolean mainButtonWasVisible, mainButtonProgressWasVisible;
    private TextView mainButton;
    private RadialProgressView radialProgressView;

    private boolean needCloseConfirmation;

    private VerticalPositionAutoAnimator mainButtonAutoAnimator, radialProgressAutoAnimator;

    private PasscodeView passcodeView;

    private Runnable pollRunnable = () -> {
        if (!dismissed) {
            TLRPC.TL_messages_prolongWebView prolongWebView = new TLRPC.TL_messages_prolongWebView();
            prolongWebView.bot = MessagesController.getInstance(currentAccount).getInputUser(botId);
            prolongWebView.peer = MessagesController.getInstance(currentAccount).getInputPeer(peerId);
            prolongWebView.query_id = queryId;
            prolongWebView.silent = silent;
            if (replyToMsgId != 0) {
                prolongWebView.reply_to_msg_id = replyToMsgId;
                prolongWebView.flags |= 1;
            }
            ConnectionsManager.getInstance(currentAccount).sendRequest(prolongWebView, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
                if (dismissed) {
                    return;
                }
                if (error != null) {
                    dismiss();
                } else {
                    AndroidUtilities.runOnUIThread(this.pollRunnable, POLL_PERIOD);
                }
            }));
        }
    };

    public BotWebViewSheet(@NonNull Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, R.style.TransparentDialog);
        this.resourcesProvider = resourcesProvider;

        swipeContainer = new ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer(context) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int availableHeight = MeasureSpec.getSize(heightMeasureSpec);

                int padding;
                if (!AndroidUtilities.isTablet() && AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    padding = (int) (availableHeight / 3.5f);
                } else {
                    padding = (availableHeight / 5 * 2);
                }
                if (padding < 0) {
                    padding = 0;
                }

                if (getOffsetY() != padding && !dismissed) {
                    ignoreLayout = true;
                    setOffsetY(padding);
                    ignoreLayout = false;
                }

                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.8f), MeasureSpec.EXACTLY);
                }
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) - ActionBar.getCurrentActionBarHeight() - AndroidUtilities.statusBarHeight + AndroidUtilities.dp(24) - (mainButtonWasVisible ? mainButton.getLayoutParams().height : 0), MeasureSpec.EXACTLY));
            }

            @Override
            public void requestLayout() {
                if (ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        webViewContainer = new BotWebViewContainer(context, resourcesProvider, getColor(Theme.key_windowBackgroundWhite));
        webViewContainer.setDelegate(new BotWebViewContainer.Delegate() {
            private boolean sentWebViewData;

            @Override
            public void onCloseRequested(Runnable callback) {
                dismiss(callback);
            }

            @Override
            public void onWebAppSetupClosingBehavior(boolean needConfirmation) {
                BotWebViewSheet.this.needCloseConfirmation = needConfirmation;
            }

            @Override
            public void onSendWebViewData(String data) {
                if (queryId != 0 || sentWebViewData) {
                    return;
                }
                sentWebViewData = true;

                TLRPC.TL_messages_sendWebViewData sendWebViewData = new TLRPC.TL_messages_sendWebViewData();
                sendWebViewData.bot = MessagesController.getInstance(currentAccount).getInputUser(botId);
                sendWebViewData.random_id = Utilities.random.nextLong();
                sendWebViewData.button_text = buttonText;
                sendWebViewData.data = data;
                ConnectionsManager.getInstance(currentAccount).sendRequest(sendWebViewData, (response, error) -> {
                    if (response instanceof TLRPC.TL_updates) {
                        MessagesController.getInstance(currentAccount).processUpdates((TLRPC.TL_updates) response, false);
                    }
                    AndroidUtilities.runOnUIThread(BotWebViewSheet.this::dismiss);
                });
            }

            @Override
            public void onWebAppSetActionBarColor(String colorKey) {
                int from = actionBarColor;
                int to = getColor(colorKey);

                ValueAnimator animator = ValueAnimator.ofFloat(0, 1).setDuration(200);
                animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animator.addUpdateListener(animation -> {
                    actionBarColor = ColorUtils.blendARGB(from, to, (Float) animation.getAnimatedValue());
                    frameLayout.invalidate();
                });
                animator.start();
            }

            @Override
            public void onWebAppSetBackgroundColor(int color) {
                overrideBackgroundColor = true;

                int from = backgroundPaint.getColor();
                ValueAnimator animator = ValueAnimator.ofFloat(0, 1).setDuration(200);
                animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animator.addUpdateListener(animation -> {
                    backgroundPaint.setColor(ColorUtils.blendARGB(from, color, (Float) animation.getAnimatedValue()));
                    frameLayout.invalidate();
                });
                animator.start();
            }

            @Override
            public void onSetBackButtonVisible(boolean visible) {
                AndroidUtilities.updateImageViewImageAnimated(actionBar.getBackButton(), visible ? R.drawable.ic_ab_back : R.drawable.ic_close_white);
            }

            @Override
            public void onWebAppOpenInvoice(String slug, TLObject response) {
                Toast.makeText(getContext(), LocaleController.getString("nekoXPaymentRemovedToast", R.string.nekoXPaymentRemovedToast), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onWebAppExpand() {
                if (/* System.currentTimeMillis() - lastSwipeTime <= 1000 || */ swipeContainer.isSwipeInProgress()) {
                    return;
                }
                swipeContainer.stickTo(-swipeContainer.getOffsetY() + swipeContainer.getTopActionBarOffsetY());
            }

            @Override
            public void onWebAppSwitchInlineQuery(TLRPC.User botUser, String query, List<String> chatTypes) {
                if (chatTypes.isEmpty()) {
                    if (parentActivity instanceof LaunchActivity) {
                        BaseFragment lastFragment = ((LaunchActivity) parentActivity).getActionBarLayout().getLastFragment();
                        if (lastFragment instanceof ChatActivity) {
                            ((ChatActivity) lastFragment).getChatActivityEnterView().setFieldText("@" + UserObject.getPublicUsername(botUser) + " " + query);
                            dismiss();
                        }
                    }
                } else {
                    Bundle args = new Bundle();
                    args.putInt("dialogsType", DialogsActivity.DIALOGS_TYPE_START_ATTACH_BOT);
                    args.putBoolean("onlySelect", true);

                    args.putBoolean("allowGroups", chatTypes.contains("groups"));
                    args.putBoolean("allowUsers", chatTypes.contains("users"));
                    args.putBoolean("allowChannels", chatTypes.contains("channels"));
                    args.putBoolean("allowBots", chatTypes.contains("bots"));

                    DialogsActivity dialogsActivity = new DialogsActivity(args);
                    AndroidUtilities.hideKeyboard(frameLayout);
                    OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(context, resourcesProvider);
                    dialogsActivity.setDelegate((fragment, dids, message1, param, topicsFragment) -> {
                        long did = dids.get(0).dialogId;

                        Bundle args1 = new Bundle();
                        args1.putBoolean("scrollToTopOnResume", true);
                        if (DialogObject.isEncryptedDialog(did)) {
                            args1.putInt("enc_id", DialogObject.getEncryptedChatId(did));
                        } else if (DialogObject.isUserDialog(did)) {
                            args1.putLong("user_id", did);
                        } else {
                            args1.putLong("chat_id", -did);
                        }
                        args1.putString("inline_query_input", "@" + UserObject.getPublicUsername(botUser) + " " + query);

                        if (parentActivity instanceof LaunchActivity) {
                            BaseFragment lastFragment = ((LaunchActivity) parentActivity).getActionBarLayout().getLastFragment();
                            if (MessagesController.getInstance(currentAccount).checkCanOpenChat(args1, lastFragment)) {
                                overlayActionBarLayoutDialog.dismiss();

                                dismissed = true;
                                AndroidUtilities.cancelRunOnUIThread(pollRunnable);

                                webViewContainer.destroyWebView();
                                NotificationCenter.getInstance(currentAccount).removeObserver(BotWebViewSheet.this, NotificationCenter.webViewResultSent);
                                NotificationCenter.getGlobalInstance().removeObserver(BotWebViewSheet.this, NotificationCenter.didSetNewTheme);
                                BotWebViewSheet.super.dismiss();

                                lastFragment.presentFragment(new INavigationLayout.NavigationParams(new ChatActivity(args1)).setRemoveLast(true));
                            }
                        }
                        return true;
                    });
                    overlayActionBarLayoutDialog.show();
                    overlayActionBarLayoutDialog.addFragment(dialogsActivity);
                }
            }

            @Override
            public void onSetupMainButton(boolean isVisible, boolean isActive, String text, int color, int textColor, boolean isProgressVisible) {
                mainButton.setClickable(isActive);
                mainButton.setText(text);
                mainButton.setTextColor(textColor);
                mainButton.setBackground(BotWebViewContainer.getMainButtonRippleDrawable(color));
                if (isVisible != mainButtonWasVisible) {
                    mainButtonWasVisible = isVisible;
                    mainButton.animate().cancel();
                    if (isVisible) {
                        mainButton.setAlpha(0f);
                        mainButton.setVisibility(View.VISIBLE);
                    }
                    mainButton.animate().alpha(isVisible ? 1f : 0f).setDuration(150).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (!isVisible) {
                                mainButton.setVisibility(View.GONE);
                            }
                            swipeContainer.requestLayout();
                        }
                    }).start();
                }
                radialProgressView.setProgressColor(textColor);
                if (isProgressVisible != mainButtonProgressWasVisible) {
                    mainButtonProgressWasVisible = isProgressVisible;
                    radialProgressView.animate().cancel();
                    if (isProgressVisible) {
                        radialProgressView.setAlpha(0f);
                        radialProgressView.setVisibility(View.VISIBLE);
                    }
                    radialProgressView.animate().alpha(isProgressVisible ? 1f : 0f)
                            .scaleX(isProgressVisible ? 1f : 0.1f)
                            .scaleY(isProgressVisible ? 1f : 0.1f)
                            .setDuration(250)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (!isProgressVisible) {
                                        radialProgressView.setVisibility(View.GONE);
                                    }
                                }
                            }).start();
                }
            }
        });

        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setStrokeWidth(AndroidUtilities.dp(4));
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        dimPaint.setColor(0x40000000);
        actionBarColor = getColor(Theme.key_windowBackgroundWhite);
        frameLayout = new SizeNotifierFrameLayout(context) {
            {
                setWillNotDraw(false);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                if (passcodeView.getVisibility() != View.VISIBLE) {
                    if (!overrideBackgroundColor) {
                        backgroundPaint.setColor(getColor(Theme.key_windowBackgroundWhite));
                    }
                    AndroidUtilities.rectTmp.set(0, 0, getWidth(), getHeight());
                    canvas.drawRect(AndroidUtilities.rectTmp, dimPaint);

                    actionBarPaint.setColor(ColorUtils.blendARGB(actionBarColor, getColor(Theme.key_windowBackgroundWhite), actionBarTransitionProgress));
                    float radius = AndroidUtilities.dp(16) * (AndroidUtilities.isTablet() ? 1f : 1f - actionBarTransitionProgress);
                    AndroidUtilities.rectTmp.set(swipeContainer.getLeft(), AndroidUtilities.lerp(swipeContainer.getTranslationY(), 0, actionBarTransitionProgress), swipeContainer.getRight(), swipeContainer.getTranslationY() + AndroidUtilities.dp(24) + radius);
                    canvas.drawRoundRect(AndroidUtilities.rectTmp, radius, radius, actionBarPaint);

                    AndroidUtilities.rectTmp.set(swipeContainer.getLeft(), swipeContainer.getTranslationY() + AndroidUtilities.dp(24), swipeContainer.getRight(), getHeight());
                    canvas.drawRect(AndroidUtilities.rectTmp, backgroundPaint);
                }
            }

            @Override
            public void draw(Canvas canvas) {
                super.draw(canvas);

                float transitionProgress = AndroidUtilities.isTablet() ? 0 : actionBarTransitionProgress;
                linePaint.setColor(Theme.getColor(Theme.key_sheet_scrollUp));
                linePaint.setAlpha((int) (linePaint.getAlpha() * (1f - Math.min(0.5f, transitionProgress) / 0.5f)));

                canvas.save();
                float scale = 1f - transitionProgress;
                float y = AndroidUtilities.isTablet() ? AndroidUtilities.lerp(swipeContainer.getTranslationY() + AndroidUtilities.dp(12), AndroidUtilities.statusBarHeight / 2f, actionBarTransitionProgress) :
                        (AndroidUtilities.lerp(swipeContainer.getTranslationY(), AndroidUtilities.statusBarHeight + ActionBar.getCurrentActionBarHeight() / 2f, transitionProgress) + AndroidUtilities.dp(12));
                canvas.scale(scale, scale, getWidth() / 2f, y);
                canvas.drawLine(getWidth() / 2f - AndroidUtilities.dp(16), y, getWidth() / 2f + AndroidUtilities.dp(16), y, linePaint);
                canvas.restore();

                actionBarShadow.setAlpha((int) (actionBar.getAlpha() * 0xFF));
                y = actionBar.getY() + actionBar.getTranslationY() + actionBar.getHeight();
                actionBarShadow.setBounds(0, (int)y, getWidth(), (int)(y + actionBarShadow.getIntrinsicHeight()));
                actionBarShadow.draw(canvas);
            }

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && (event.getY() <= AndroidUtilities.lerp(swipeContainer.getTranslationY() + AndroidUtilities.dp(24), 0, actionBarTransitionProgress) ||
                        event.getX() > swipeContainer.getRight() || event.getX() < swipeContainer.getLeft())) {
                    onCheckDismissByUser();
                    return true;
                }
                return super.onTouchEvent(event);
            }
        };
        frameLayout.setDelegate((keyboardHeight, isWidthGreater) -> {
            if (keyboardHeight > AndroidUtilities.dp(20)) {
                swipeContainer.stickTo(-swipeContainer.getOffsetY() + swipeContainer.getTopActionBarOffsetY());
            }
        });
        frameLayout.addView(swipeContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 24, 0, 0));

        mainButton = new TextView(context) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.8f), MeasureSpec.EXACTLY);
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };
        mainButton.setVisibility(View.GONE);
        mainButton.setAlpha(0f);
        mainButton.setSingleLine();
        mainButton.setGravity(Gravity.CENTER);
        mainButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        int padding = AndroidUtilities.dp(16);
        mainButton.setPadding(padding, 0, padding, 0);
        mainButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        mainButton.setOnClickListener(v -> webViewContainer.onMainButtonPressed());
        frameLayout.addView(mainButton, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL));
        mainButtonAutoAnimator = VerticalPositionAutoAnimator.attach(mainButton);

        radialProgressView = new RadialProgressView(context) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getLayoutParams();
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    params.rightMargin = (int) (AndroidUtilities.dp(10) + Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.1f);
                } else {
                    params.rightMargin = AndroidUtilities.dp(10);
                }
            }
        };
        radialProgressView.setSize(AndroidUtilities.dp(18));
        radialProgressView.setAlpha(0f);
        radialProgressView.setScaleX(0.1f);
        radialProgressView.setScaleY(0.1f);
        radialProgressView.setVisibility(View.GONE);
        frameLayout.addView(radialProgressView, LayoutHelper.createFrame(28, 28, Gravity.BOTTOM | Gravity.RIGHT, 0, 0, 10, 10));
        radialProgressAutoAnimator = VerticalPositionAutoAnimator.attach(radialProgressView);

        actionBarShadow = ContextCompat.getDrawable(getContext(), R.drawable.header_shadow).mutate();

        actionBar = new ActionBar(context, resourcesProvider) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.8f), MeasureSpec.EXACTLY);
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };
        actionBar.setBackgroundColor(Color.TRANSPARENT);
        actionBar.setBackButtonImage(R.drawable.ic_close_white);
        updateActionBarColors();
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    onCheckDismissByUser();
                }
            }
        });
        actionBar.setAlpha(0f);
        frameLayout.addView(actionBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER_HORIZONTAL));

        frameLayout.addView(progressView = new ChatAttachAlertBotWebViewLayout.WebProgressView(context, resourcesProvider) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (AndroidUtilities.isTablet() && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isSmallTablet()) {
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.8f), MeasureSpec.EXACTLY);
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0, 0, 0));
        webViewContainer.setWebViewProgressListener(progress -> {
            progressView.setLoadProgressAnimated(progress);
            if (progress == 1f) {
                ValueAnimator animator = ValueAnimator.ofFloat(1, 0).setDuration(200);
                animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animator.addUpdateListener(animation -> progressView.setAlpha((Float) animation.getAnimatedValue()));
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressView.setVisibility(View.GONE);
                    }
                });
                animator.start();
            }
        });

        swipeContainer.addView(webViewContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        swipeContainer.setScrollListener(()->{
            if (swipeContainer.getSwipeOffsetY() > 0) {
                dimPaint.setAlpha((int) (0x40 * (1f - MathUtils.clamp(swipeContainer.getSwipeOffsetY() / (float)swipeContainer.getHeight(), 0, 1))));
            } else {
                dimPaint.setAlpha(0x40);
            }
            frameLayout.invalidate();
            webViewContainer.invalidateViewPortHeight();

            if (springAnimation != null) {
                float progress = (1f - Math.min(swipeContainer.getTopActionBarOffsetY(), swipeContainer.getTranslationY() - swipeContainer.getTopActionBarOffsetY()) / swipeContainer.getTopActionBarOffsetY());
                float newPos = (progress > 0.5f ? 1 : 0) * 100f;
                if (springAnimation.getSpring().getFinalPosition() != newPos) {
                    springAnimation.getSpring().setFinalPosition(newPos);
                    springAnimation.start();
                }
            }
            float offsetY = Math.max(0, swipeContainer.getSwipeOffsetY());
            mainButtonAutoAnimator.setOffsetY(offsetY);
            radialProgressAutoAnimator.setOffsetY(offsetY);
            lastSwipeTime = System.currentTimeMillis();
        });
        swipeContainer.setScrollEndListener(()-> webViewContainer.invalidateViewPortHeight(true));
        swipeContainer.setDelegate(() -> {
            if (!onCheckDismissByUser()) {
                swipeContainer.stickTo(0);
            }
        });
        swipeContainer.setTopActionBarOffsetY(ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight - AndroidUtilities.dp(24));
        swipeContainer.setIsKeyboardVisible(obj -> frameLayout.getKeyboardHeight() >= AndroidUtilities.dp(20));

        passcodeView = new PasscodeView(context);
        frameLayout.addView(passcodeView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        setContentView(frameLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onStart() {
        super.onStart();

        Context context = getContext();
        if (context instanceof ContextWrapper && !(context instanceof LaunchActivity)) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context instanceof LaunchActivity) {
            ((LaunchActivity) context).addOverlayPasscodeView(passcodeView);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        Context context = getContext();
        if (context instanceof ContextWrapper && !(context instanceof LaunchActivity)) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context instanceof LaunchActivity) {
            ((LaunchActivity) context).removeOverlayPasscodeView(passcodeView);
        }
    }

    public void setParentActivity(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }

    private void updateActionBarColors() {
        actionBar.setTitleColor(getColor(Theme.key_windowBackgroundWhiteBlackText));
        actionBar.setItemsColor(getColor(Theme.key_windowBackgroundWhiteBlackText), false);
        actionBar.setItemsBackgroundColor(getColor(Theme.key_actionBarWhiteSelector), false);
        actionBar.setPopupBackgroundColor(getColor(Theme.key_actionBarDefaultSubmenuBackground), false);
        actionBar.setPopupItemsColor(getColor(Theme.key_actionBarDefaultSubmenuItem), false, false);
        actionBar.setPopupItemsColor(getColor(Theme.key_actionBarDefaultSubmenuItemIcon), true, false);
        actionBar.setPopupItemsSelectorColor(getColor(Theme.key_dialogButtonSelector), false);
    }

    private void updateLightStatusBar() {
        int color = Theme.getColor(Theme.key_windowBackgroundWhite, null, true);
        boolean lightStatusBar = !AndroidUtilities.isTablet() && ColorUtils.calculateLuminance(color) >= 0.9 && actionBarTransitionProgress >= 0.85f;

        if (wasLightStatusBar != null && wasLightStatusBar == lightStatusBar) {
            return;
        }
        wasLightStatusBar = lightStatusBar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = frameLayout.getSystemUiVisibility();
            if (lightStatusBar) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            frameLayout.setSystemUiVisibility(flags);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        window.setWindowAnimations(R.style.DialogNoAnimation);

        WindowManager.LayoutParams params = window.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.dimAmount = 0;
        params.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        window.setAttributes(params);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        frameLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            frameLayout.setOnApplyWindowInsetsListener((v, insets) -> {
                v.setPadding(0, 0, 0, insets.getSystemWindowInsetBottom());
                return insets;
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int color = Theme.getColor(Theme.key_windowBackgroundWhite, null, true);
            AndroidUtilities.setLightNavigationBar(window, ColorUtils.calculateLuminance(color) >= 0.9);
        }

        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (springAnimation == null) {
            springAnimation = new SpringAnimation(this, ACTION_BAR_TRANSITION_PROGRESS_VALUE)
                .setSpring(new SpringForce()
                    .setStiffness(1200f)
                    .setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY)
                );
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (springAnimation != null) {
            springAnimation.cancel();
            springAnimation = null;
        }
    }

    public void requestWebView(int currentAccount, long peerId, long botId, String buttonText, String buttonUrl, @WebViewType int type, int replyToMsgId, boolean silent, int flags) {
        requestWebView(currentAccount, peerId, botId, buttonText, buttonUrl, type, replyToMsgId, silent, null, null, false, null, null, flags);
    }

    public void requestWebView(int currentAccount, long peerId, long botId, String buttonText, String buttonUrl, @WebViewType int type, int replyToMsgId, boolean silent) {
        requestWebView(currentAccount, peerId, botId, buttonText, buttonUrl, type, replyToMsgId, silent, null, null, false, null, null, 0);
    }

    public void requestWebView(int currentAccount, long peerId, long botId, String buttonText, String buttonUrl, @WebViewType int type, int replyToMsgId, boolean silent, BaseFragment lastFragment, TLRPC.BotApp app, boolean allowWrite, String startParam, TLRPC.User botUser) {
        requestWebView(currentAccount, peerId, botId, buttonText, buttonUrl, type, replyToMsgId, silent, lastFragment, app, allowWrite, startParam, botUser, 0);
    }

    public void requestWebView(int currentAccount, long peerId, long botId, String buttonText, String buttonUrl, @WebViewType int type, int replyToMsgId, boolean silent, BaseFragment lastFragment, TLRPC.BotApp app, boolean allowWrite, String startParam, TLRPC.User botUser, int flags) {
        this.currentAccount = currentAccount;
        this.peerId = peerId;
        this.botId = botId;
        this.replyToMsgId = replyToMsgId;
        this.silent = silent;
        this.buttonText = buttonText;

        actionBar.setTitle(UserObject.getUserName(MessagesController.getInstance(currentAccount).getUser(botId)));
        ActionBarMenu menu = actionBar.createMenu();
        menu.removeAllViews();

        ActionBarMenuItem otherItem = menu.addItem(0, R.drawable.ic_ab_other);
        otherItem.addSubItem(R.id.menu_open_bot, R.drawable.msg_bot, LocaleController.getString(R.string.BotWebViewOpenBot));
        otherItem.addSubItem(R.id.menu_reload_page, R.drawable.msg_retry, LocaleController.getString(R.string.BotWebViewReloadPage));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    if (!webViewContainer.onBackPressed()) {
                        onCheckDismissByUser();
                    }
                } else if (id == R.id.menu_open_bot) {
                    Bundle bundle = new Bundle();
                    bundle.putLong("user_id", botId);
                    if (parentActivity instanceof LaunchActivity) {
                        ((LaunchActivity) parentActivity).presentFragment(new ChatActivity(bundle));
                    }
                    dismiss();
                } else if (id == R.id.menu_reload_page) {
                    if (webViewContainer.getWebView() != null) {
                        webViewContainer.getWebView().animate().cancel();
                        webViewContainer.getWebView().animate().alpha(0).start();
                    }

                    progressView.setLoadProgress(0);
                    progressView.setAlpha(1f);
                    progressView.setVisibility(View.VISIBLE);

                    webViewContainer.setBotUser(MessagesController.getInstance(currentAccount).getUser(botId));
                    webViewContainer.loadFlickerAndSettingsItem(currentAccount, botId, settingsItem);
                    webViewContainer.reload();
                } else if (id == R.id.menu_settings) {
                    webViewContainer.onSettingsButtonPressed();
                }
            }
        });

        boolean hasThemeParams = true;
        String themeParams = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("bg_color", getColor(Theme.key_windowBackgroundWhite));
            jsonObject.put("secondary_bg_color", getColor(Theme.key_windowBackgroundGray));
            jsonObject.put("text_color", getColor(Theme.key_windowBackgroundWhiteBlackText));
            jsonObject.put("hint_color", getColor(Theme.key_windowBackgroundWhiteHintText));
            jsonObject.put("link_color", getColor(Theme.key_windowBackgroundWhiteLinkText));
            jsonObject.put("button_color", getColor(Theme.key_featuredStickers_addButton));
            jsonObject.put("button_text_color", getColor(Theme.key_featuredStickers_buttonText));
            themeParams = jsonObject.toString();
        } catch (Exception e) {
            FileLog.e(e);
            hasThemeParams = false;
        }

        webViewContainer.setBotUser(MessagesController.getInstance(currentAccount).getUser(botId));
        webViewContainer.loadFlickerAndSettingsItem(currentAccount, botId, settingsItem);
        switch (type) {
            case TYPE_BOT_MENU_BUTTON: {
                TLRPC.TL_messages_requestWebView req = new TLRPC.TL_messages_requestWebView();
                req.bot = MessagesController.getInstance(currentAccount).getInputUser(botId);
                req.peer = MessagesController.getInstance(currentAccount).getInputPeer(botId);
                req.platform = "android";

                req.url = buttonUrl;
                req.flags |= 2;

                if (hasThemeParams) {
                    req.theme_params = new TLRPC.TL_dataJSON();
                    req.theme_params.data = themeParams;
                    req.flags |= 4;
                }

                ConnectionsManager.getInstance(currentAccount).sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
                    if (response instanceof TLRPC.TL_webViewResultUrl) {
                        TLRPC.TL_webViewResultUrl resultUrl = (TLRPC.TL_webViewResultUrl) response;
                        queryId = resultUrl.query_id;
                        webViewContainer.loadUrl(currentAccount, resultUrl.url);
                        swipeContainer.setWebView(webViewContainer.getWebView());

                        AndroidUtilities.runOnUIThread(pollRunnable, POLL_PERIOD);
                    }
                }));
                NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.webViewResultSent);

                break;
            }
            case TYPE_SIMPLE_WEB_VIEW_BUTTON: {
                TLRPC.TL_messages_requestSimpleWebView req = new TLRPC.TL_messages_requestSimpleWebView();
                req.from_switch_webview = (flags & FLAG_FROM_INLINE_SWITCH) != 0;
                req.bot = MessagesController.getInstance(currentAccount).getInputUser(botId);
                req.platform = "android";
                if (hasThemeParams) {
                    req.theme_params = new TLRPC.TL_dataJSON();
                    req.theme_params.data = themeParams;
                    req.flags |= 1;
                }
                req.url = buttonUrl;

                ConnectionsManager.getInstance(currentAccount).sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
                    if (response instanceof TLRPC.TL_simpleWebViewResultUrl) {
                        TLRPC.TL_simpleWebViewResultUrl resultUrl = (TLRPC.TL_simpleWebViewResultUrl) response;
                        queryId = 0;
                        webViewContainer.loadUrl(currentAccount, resultUrl.url);
                        swipeContainer.setWebView(webViewContainer.getWebView());
                    }
                }));
                break;
            }
            case TYPE_WEB_VIEW_BUTTON: {
                TLRPC.TL_messages_requestWebView req = new TLRPC.TL_messages_requestWebView();
                req.peer = MessagesController.getInstance(currentAccount).getInputPeer(peerId);
                req.bot = MessagesController.getInstance(currentAccount).getInputUser(botId);
                req.platform = "android";
                if (buttonUrl != null) {
                    req.url = buttonUrl;
                    req.flags |= 2;
                }

                if (replyToMsgId != 0) {
                    req.reply_to_msg_id = replyToMsgId;
                    req.flags |= 1;
                }

                if (hasThemeParams) {
                    req.theme_params = new TLRPC.TL_dataJSON();
                    req.theme_params.data = themeParams;
                    req.flags |= 4;
                }

                ConnectionsManager.getInstance(currentAccount).sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
                    if (response instanceof TLRPC.TL_webViewResultUrl) {
                        TLRPC.TL_webViewResultUrl resultUrl = (TLRPC.TL_webViewResultUrl) response;
                        queryId = resultUrl.query_id;
                        webViewContainer.loadUrl(currentAccount, resultUrl.url);
                        swipeContainer.setWebView(webViewContainer.getWebView());

                        AndroidUtilities.runOnUIThread(pollRunnable, POLL_PERIOD);
                    }
                }));
                NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.webViewResultSent);
                break;
            }
            case TYPE_WEB_VIEW_BOT_APP: {
                TLRPC.TL_messages_requestAppWebView req = new TLRPC.TL_messages_requestAppWebView();
                TLRPC.TL_inputBotAppID botApp = new TLRPC.TL_inputBotAppID();
                botApp.id = app.id;
                botApp.access_hash = app.access_hash;

                req.app = botApp;
                req.write_allowed = allowWrite;
                req.platform = "android";
                req.peer = lastFragment instanceof ChatActivity ? ((ChatActivity) lastFragment).getCurrentUser() != null ? MessagesController.getInputPeer(((ChatActivity) lastFragment).getCurrentUser()) : MessagesController.getInputPeer(((ChatActivity) lastFragment).getCurrentChat())
                        : MessagesController.getInputPeer(botUser);

                if (!TextUtils.isEmpty(startParam)) {
                    req.start_param = startParam;
                    req.flags |= 2;
                }

                if (hasThemeParams) {
                    req.theme_params = new TLRPC.TL_dataJSON();
                    req.theme_params.data = themeParams;
                    req.flags |= 4;
                }

                ConnectionsManager.getInstance(currentAccount).sendRequest(req, (response2, error2) -> AndroidUtilities.runOnUIThread(() -> {
                    if (error2 == null) {
                        TLRPC.TL_appWebViewResultUrl result = (TLRPC.TL_appWebViewResultUrl) response2;
                        queryId = 0;
                        webViewContainer.loadUrl(currentAccount, result.url);
                        swipeContainer.setWebView(webViewContainer.getWebView());

                        AndroidUtilities.runOnUIThread(pollRunnable, POLL_PERIOD);
                    }
                }), ConnectionsManager.RequestFlagInvokeAfter | ConnectionsManager.RequestFlagFailOnServerErrors);
            }
        }
    }

    private int getColor(String key) {
        Integer color;
        if (resourcesProvider != null) {
            color = resourcesProvider.getColor(key);
        } else {
            color = Theme.getColor(key);
        }
        return color != null ? color : Theme.getColor(key);
    }

    @Override
    public void show() {
        frameLayout.setAlpha(0f);
        frameLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);

                swipeContainer.setSwipeOffsetY(swipeContainer.getHeight());
                frameLayout.setAlpha(1f);

                new SpringAnimation(swipeContainer, ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.SWIPE_OFFSET_Y, 0)
                        .setSpring(new SpringForce(0)
                                .setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY)
                                .setStiffness(500.0f)
                        ).start();
            }
        });
        super.show();
    }

    @Override
    public void onBackPressed() {
        if (passcodeView.getVisibility() == View.VISIBLE) {
            if (getOwnerActivity() != null) {
                getOwnerActivity().finish();
            }
            return;
        }
        if (webViewContainer.onBackPressed()) {
            return;
        }
        onCheckDismissByUser();
    }

    @Override
    public void dismiss() {
        dismiss(null);
    }

    public boolean onCheckDismissByUser() {
        if (needCloseConfirmation) {
            String botName = null;
            TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(botId);
            if (user != null) {
                botName = ContactsController.formatName(user.first_name, user.last_name);
            }

            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setTitle(botName)
                    .setMessage(LocaleController.getString(R.string.BotWebViewChangesMayNotBeSaved))
                    .setPositiveButton(LocaleController.getString(R.string.BotWebViewCloseAnyway), (dialog2, which) -> dismiss())
                    .setNegativeButton(LocaleController.getString(R.string.Cancel), null)
                    .create();
            dialog.show();
            TextView textView = (TextView) dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            textView.setTextColor(getColor(Theme.key_dialogTextRed));
            return false;
        } else {
            dismiss();
            return true;
        }
    }

    public void dismiss(Runnable callback) {
        if (dismissed) {
            return;
        }
        dismissed = true;
        AndroidUtilities.cancelRunOnUIThread(pollRunnable);

        webViewContainer.destroyWebView();
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);

        swipeContainer.stickTo(swipeContainer.getHeight() + frameLayout.measureKeyboardHeight(), ()->{
            super.dismiss();
            if (callback != null) {
                callback.run();
            }
        });
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.webViewResultSent) {
            long queryId = (long) args[0];

            if (this.queryId == queryId) {
                dismiss();
            }
        } else if (id == NotificationCenter.didSetNewTheme) {
            frameLayout.invalidate();
            webViewContainer.updateFlickerBackgroundColor(getColor(Theme.key_windowBackgroundWhite));
            updateActionBarColors();
            updateLightStatusBar();
        }
    }
}
