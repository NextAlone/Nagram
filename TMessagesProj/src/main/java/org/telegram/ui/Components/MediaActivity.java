package org.telegram.ui.Components;

import static org.telegram.messenger.AndroidUtilities.dp;
import static org.telegram.messenger.AndroidUtilities.dpf2;
import static org.telegram.messenger.AndroidUtilities.lerp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import org.checkerframework.checker.units.qual.Angle;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugController;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugProvider;
import org.telegram.ui.Components.Paint.ShapeDetector;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.Stories.recorder.StoryRecorder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MediaActivity extends BaseFragment implements SharedMediaLayout.SharedMediaPreloaderDelegate, FloatingDebugProvider, NotificationCenter.NotificationCenterDelegate {

    public static final int TYPE_MEDIA = 0;
    public static final int TYPE_STORIES = 1;

    private int type;

    private SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader;
    private TLRPC.ChatFull currentChatInfo;
    private TLRPC.UserFull currentUserInfo;
    private long dialogId;
    private FrameLayout titlesContainer;
    private FrameLayout[] titles = new FrameLayout[2];
    private SimpleTextView[] nameTextView = new SimpleTextView[2];
    private AnimatedTextView[] subtitleTextView = new AnimatedTextView[2];
    ProfileActivity.AvatarImageView avatarImageView;
    private BackDrawable backDrawable;
    private AnimatedTextView selectedTextView;
    private ActionBarMenuItem optionsItem;
    private ActionBarMenuItem deleteItem;
    private SparseArray<MessageObject> actionModeMessageObjects;
    private ActionBarMenuSubItem showPhotosItem, showVideosItem;
    private boolean filterPhotos = true, filterVideos = true;
    private int shiftDp = -12;
    private ActionBarMenuSubItem calendarItem, zoomInItem, zoomOutItem;

    private ImageView floatingButton;
    private FrameLayout floatingButtonContainer;

    private StoriesTabsView tabsView;
    private FrameLayout buttonContainer;
    private ButtonWithCounterView button;

    private Runnable applyBulletin;

    SharedMediaLayout sharedMediaLayout;
    private int initialTab;

    public MediaActivity(Bundle args, SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader) {
        super(args);
        this.sharedMediaPreloader = sharedMediaPreloader;
    }

    @Override
    public boolean onFragmentCreate() {
        type = getArguments().getInt("type", TYPE_MEDIA);
        dialogId = getArguments().getLong("dialog_id");
        initialTab = getArguments().getInt("start_from", type == TYPE_MEDIA ? SharedMediaLayout.TAB_PHOTOVIDEO : SharedMediaLayout.TAB_STORIES);
        getNotificationCenter().addObserver(this, NotificationCenter.userInfoDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.storiesEnabledUpdate);
        if (DialogObject.isUserDialog(dialogId)) {
            TLRPC.User user = getMessagesController().getUser(dialogId);
            if (UserObject.isUserSelf(user)) {
                getMessagesController().loadUserInfo(user, false, this.classGuid);
                currentUserInfo = getMessagesController().getUserFull(dialogId);
            }
        }
        if (this.sharedMediaPreloader == null) {
            this.sharedMediaPreloader = new SharedMediaLayout.SharedMediaPreloader(this);
            this.sharedMediaPreloader.addDelegate(this);
        }
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.userInfoDidLoad);
        getNotificationCenter().removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        getNotificationCenter().removeObserver(this, NotificationCenter.storiesEnabledUpdate);
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.userInfoDidLoad) {
            long uid = (long) args[0];
            if (uid == dialogId) {
                currentUserInfo = (TLRPC.UserFull) args[1];
                if (sharedMediaLayout != null) {
                    sharedMediaLayout.setUserInfo(currentUserInfo);
                }
            }
        } else if (id == NotificationCenter.currentUserPremiumStatusChanged || id == NotificationCenter.storiesEnabledUpdate) {
            if (!getMessagesController().storiesEnabled()) {
                hideFloatingButton(true, true);
            }
        }
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonDrawable(backDrawable = new BackDrawable(false));
        backDrawable.setAnimationTime(240);
        actionBar.setCastShadows(false);
        actionBar.setAddToContainer(false);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    if (sharedMediaLayout.closeActionMode(true)) {
                        return;
                    }
                    finishFragment();
                } else if (id == 2) {
                    if (actionModeMessageObjects != null) {
                        ArrayList<TLRPC.StoryItem> storyItems = new ArrayList<>();
                        for (int i = 0; i < actionModeMessageObjects.size(); ++i) {
                            MessageObject messageObject = actionModeMessageObjects.valueAt(i);
                            if (messageObject.storyItem != null) {
                                storyItems.add(messageObject.storyItem);
                            }
                        }

                        if (!storyItems.isEmpty()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), getResourceProvider());
                            builder.setTitle(storyItems.size() > 1 ? LocaleController.getString("DeleteStoriesTitle", R.string.DeleteStoriesTitle) : LocaleController.getString("DeleteStoryTitle", R.string.DeleteStoryTitle));
                            builder.setMessage(LocaleController.formatPluralString("DeleteStoriesSubtitle", storyItems.size()));
                            builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getMessagesController().getStoriesController().deleteStories(storyItems);
                                    sharedMediaLayout.closeActionMode(false);
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) (dialog, which) -> {
                                dialog.dismiss();
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            dialog.redPositive();
                        }
                    }
                } else if (id == 10) {
                    sharedMediaLayout.showMediaCalendar(sharedMediaLayout.getClosestTab(), false);
                }
            }
        });
        actionBar.setColorFilterMode(PorterDuff.Mode.SRC_IN);
        FrameLayout avatarContainer = new FrameLayout(context);
        SizeNotifierFrameLayout fragmentView = new SizeNotifierFrameLayout(context) {

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                FrameLayout.LayoutParams lp = (LayoutParams) sharedMediaLayout.getLayoutParams();
                lp.topMargin = ActionBar.getCurrentActionBarHeight() + (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);

                lp = (LayoutParams) avatarContainer.getLayoutParams();
                lp.topMargin = actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0;
                lp.height = ActionBar.getCurrentActionBarHeight();

                int textTop;
                for (int i = 0; i < 2; ++i) {
                    if (nameTextView[i] != null) {
                        textTop = (ActionBar.getCurrentActionBarHeight() / 2 - dp(22)) / 2 + dp(!AndroidUtilities.isTablet() && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 4 : 5);
                        lp = (LayoutParams) nameTextView[i].getLayoutParams();
                        lp.topMargin = textTop;
                    }

                    if (subtitleTextView[i] != null) {
                        textTop = ActionBar.getCurrentActionBarHeight() / 2 + (ActionBar.getCurrentActionBarHeight() / 2 - dp(19)) / 2 - dp(3 + 4);
                        lp = (LayoutParams) subtitleTextView[i].getLayoutParams();
                        lp.topMargin = textTop;
                    }
                }

                lp = (LayoutParams) avatarImageView.getLayoutParams();
                lp.topMargin = (ActionBar.getCurrentActionBarHeight() - dp(42)) / 2;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            @Override
            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (sharedMediaLayout != null && sharedMediaLayout.isInFastScroll()) {
                    return sharedMediaLayout.dispatchFastScrollEvent(ev);
                }
                if (sharedMediaLayout != null && sharedMediaLayout.checkPinchToZoom(ev)) {
                    return true;
                }
                return super.dispatchTouchEvent(ev);
            }

            @Override
            protected void drawList(Canvas blurCanvas, boolean top) {
                sharedMediaLayout.drawListForBlur(blurCanvas);
            }
        };
        fragmentView.needBlur = true;
        this.fragmentView = fragmentView;

        ActionBarMenu menu2 = actionBar.createMenu();
        if (type == TYPE_STORIES) {
            FrameLayout menu = new FrameLayout(context);
            actionBar.addView(menu, LayoutHelper.createFrame(56, 56, Gravity.RIGHT | Gravity.BOTTOM));

            deleteItem = new ActionBarMenuItem(context, menu2, getThemedColor(Theme.key_actionBarActionModeDefaultSelector), getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            deleteItem.setIcon(R.drawable.msg_delete);
            deleteItem.setVisibility(View.GONE);
            deleteItem.setAlpha(0f);
            deleteItem.setOnClickListener(v -> menu2.onItemClick(2));
            menu.addView(deleteItem);

            optionsItem = new ActionBarMenuItem(context, menu2, getThemedColor(Theme.key_actionBarActionModeDefaultSelector), getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            optionsItem.setIcon(R.drawable.ic_ab_other);
            optionsItem.setOnClickListener(v -> optionsItem.toggleSubMenu());
            optionsItem.setVisibility(View.GONE);
            optionsItem.setAlpha(0f);
            menu.addView(optionsItem);
            zoomInItem = optionsItem.addSubItem(8, R.drawable.msg_zoomin, LocaleController.getString("MediaZoomIn", R.string.MediaZoomIn));
            zoomInItem.setOnClickListener(v -> {
                boolean canZoomOut, canZoomIn;
                canZoomOut = true;
                Boolean r = sharedMediaLayout.zoomIn();
                if (r == null) {
                    return;
                }
                canZoomIn = r;
                zoomOutItem.setEnabled(canZoomOut);
                zoomOutItem.animate().alpha(zoomOutItem.isEnabled() ? 1f : .5f).start();
                zoomInItem.setEnabled(canZoomIn);
                zoomInItem.animate().alpha(zoomInItem.isEnabled() ? 1f : .5f).start();
            });
            zoomOutItem = optionsItem.addSubItem(9, R.drawable.msg_zoomout, LocaleController.getString("MediaZoomOut", R.string.MediaZoomOut));
            zoomOutItem.setOnClickListener(v -> {
                boolean canZoomOut, canZoomIn;
                canZoomIn = true;
                Boolean r = sharedMediaLayout.zoomOut();
                if (r == null) {
                    return;
                }
                canZoomOut = r;
                zoomOutItem.setEnabled(canZoomOut);
                zoomOutItem.animate().alpha(zoomOutItem.isEnabled() ? 1f : .5f).start();
                zoomInItem.setEnabled(canZoomIn);
                zoomInItem.animate().alpha(zoomInItem.isEnabled() ? 1f : .5f).start();
            });
            calendarItem = optionsItem.addSubItem(10, R.drawable.msg_calendar2, LocaleController.getString("Calendar", R.string.Calendar));
            calendarItem.setEnabled(false);
            calendarItem.setAlpha(.5f);
            optionsItem.addColoredGap();
            showPhotosItem = optionsItem.addSubItem(6, 0, LocaleController.getString("MediaShowPhotos", R.string.MediaShowPhotos), true);
            showPhotosItem.setChecked(filterPhotos);
            showPhotosItem.setOnClickListener(e -> {
                if (filterPhotos && !filterVideos) {
                    BotWebViewVibrationEffect.APP_ERROR.vibrate();
                    AndroidUtilities.shakeViewSpring(showPhotosItem, shiftDp = -shiftDp);
                    return;
                }
                showPhotosItem.setChecked(filterPhotos = !filterPhotos);
                sharedMediaLayout.setStoriesFilter(filterPhotos, filterVideos);
            });
            showVideosItem = optionsItem.addSubItem(7, 0, LocaleController.getString("MediaShowVideos", R.string.MediaShowVideos), true);
            showVideosItem.setChecked(filterVideos);
            showVideosItem.setOnClickListener(e -> {
                if (filterVideos && !filterPhotos) {
                    BotWebViewVibrationEffect.APP_ERROR.vibrate();
                    AndroidUtilities.shakeViewSpring(showVideosItem, shiftDp = -shiftDp);
                    return;
                }
                showVideosItem.setChecked(filterVideos = !filterVideos);
                sharedMediaLayout.setStoriesFilter(filterPhotos, filterVideos);
            });
        }

        boolean hasAvatar = type == TYPE_MEDIA;

        titlesContainer = new FrameLayout(context);
        avatarContainer.addView(titlesContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.FILL));
        for (int i = 0; i < (type == TYPE_STORIES ? 2 : 1); ++i) {
            titles[i] = new FrameLayout(context);
            titlesContainer.addView(titles[i], LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.FILL));

            nameTextView[i] = new SimpleTextView(context);
            nameTextView[i].setPivotX(0);
            nameTextView[i].setPivotY(dp(9));

            nameTextView[i].setTextSize(18);
            nameTextView[i].setGravity(Gravity.LEFT);
            nameTextView[i].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            nameTextView[i].setLeftDrawableTopPadding(-dp(1.3f));
            nameTextView[i].setScrollNonFitText(true);
            nameTextView[i].setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            titles[i].addView(nameTextView[i], LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, hasAvatar ? 118 : 72, 0, 56, 0));

            subtitleTextView[i] = new AnimatedTextView(context, true, true, true);
            subtitleTextView[i].setAnimationProperties(.4f, 0, 320, CubicBezierInterpolator.EASE_OUT_QUINT);
            subtitleTextView[i].setTextSize(AndroidUtilities.dp(14));
            subtitleTextView[i].setTextColor(Theme.getColor(Theme.key_player_actionBarSubtitle));
            titles[i].addView(subtitleTextView[i], LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, hasAvatar ? 118 : 72, 0, 56, 0));

            if (i != 0) {
                titles[i].setAlpha(0f);
            }
        }

        avatarImageView = new ProfileActivity.AvatarImageView(context) {
            @Override
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                if (getImageReceiver().hasNotThumb()) {
                    info.setText(LocaleController.getString("AccDescrProfilePicture", R.string.AccDescrProfilePicture));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        info.addAction(new AccessibilityNodeInfo.AccessibilityAction(AccessibilityNodeInfo.ACTION_CLICK, LocaleController.getString("Open", R.string.Open)));
                        info.addAction(new AccessibilityNodeInfo.AccessibilityAction(AccessibilityNodeInfo.ACTION_LONG_CLICK, LocaleController.getString("AccDescrOpenInPhotoViewer", R.string.AccDescrOpenInPhotoViewer)));
                    }
                } else {
                    info.setVisibleToUser(false);
                }
            }
        };
        avatarImageView.getImageReceiver().setAllowDecodeSingleFrame(true);
        avatarImageView.setRoundRadius(dp(21));
        avatarImageView.setPivotX(0);
        avatarImageView.setPivotY(0);
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setProfile(true);
        avatarImageView.setVisibility(hasAvatar ? View.VISIBLE : View.GONE);

        avatarImageView.setImageDrawable(avatarDrawable);
        avatarContainer.addView(avatarImageView, LayoutHelper.createFrame(42, 42, Gravity.TOP | Gravity.LEFT, 64, 0, 0, 0));

        selectedTextView = new AnimatedTextView(context, true, true, true);
        selectedTextView.setAnimationProperties(.4f, 0, 320, CubicBezierInterpolator.EASE_OUT_QUINT);
        selectedTextView.setTextSize(dp(20));
        selectedTextView.setGravity(Gravity.LEFT);
        selectedTextView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        selectedTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        avatarContainer.addView(selectedTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.FILL_HORIZONTAL | Gravity.CENTER_VERTICAL, 72 + (hasAvatar ? 48 : 0), -2, 72, 0));

        if (type == TYPE_STORIES) {
            tabsView = new StoriesTabsView(context, getResourceProvider());
            tabsView.setOnTabClick(i -> {
                sharedMediaLayout.scrollToPage(SharedMediaLayout.TAB_STORIES + i);
            });

            buttonContainer = new FrameLayout(context);
            buttonContainer.setPadding(dp(10), dp(8), dp(10), dp(8));
            buttonContainer.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
            button = new ButtonWithCounterView(context, getResourceProvider());
            button.setText(LocaleController.getString("SaveToProfile", R.string.SaveToProfile), false);
            button.setShowZero(true);
            button.setCount(0, false);
            button.setEnabled(false);
            button.setOnClickListener(v -> {
                if (applyBulletin != null) {
                    applyBulletin.run();
                    applyBulletin = null;
                }
                Bulletin.hideVisible();
                boolean pin = sharedMediaLayout.getClosestTab() == SharedMediaLayout.TAB_ARCHIVED_STORIES;
                int count = 0;
                ArrayList<TLRPC.StoryItem> storyItems = new ArrayList<>();
                if (actionModeMessageObjects != null) {
                    for (int i = 0; i < actionModeMessageObjects.size(); ++i) {
                        MessageObject messageObject = actionModeMessageObjects.valueAt(i);
                        if (messageObject.storyItem != null) {
                            storyItems.add(messageObject.storyItem);
                            count++;
                        }
                    }
                }
                sharedMediaLayout.closeActionMode(false);
                sharedMediaLayout.disableScroll(false);
                if (pin) {
                    sharedMediaLayout.scrollToPage(SharedMediaLayout.TAB_STORIES);
                }
                if (storyItems.isEmpty()) {
                    return;
                }
                boolean[] pastValues = new boolean[storyItems.size()];
                for (int i = 0; i < storyItems.size(); ++i) {
                    TLRPC.StoryItem storyItem = storyItems.get(i);
                    pastValues[i] = storyItem.pinned;
                    storyItem.pinned = pin;
                }
                getMessagesController().getStoriesController().updateStoriesInLists(dialogId, storyItems);
                final boolean[] undone = new boolean[] { false };
                applyBulletin = () -> {
                    getMessagesController().getStoriesController().updateStoriesPinned(storyItems, pin, null);
                };
                final Runnable undo = () -> {
                    undone[0] = true;
                    AndroidUtilities.cancelRunOnUIThread(applyBulletin);
                    for (int i = 0; i < storyItems.size(); ++i) {
                        TLRPC.StoryItem storyItem = storyItems.get(i);
                        storyItem.pinned = pastValues[i];
                    }
                    getMessagesController().getStoriesController().updateStoriesInLists(dialogId, storyItems);
                };
                Bulletin bulletin;
                if (pin) {
                    bulletin = BulletinFactory.of(this).createSimpleBulletin(R.raw.contact_check, LocaleController.formatPluralString("StorySavedTitle", count), LocaleController.getString("StorySavedSubtitle"), LocaleController.getString("Undo"), undo).show();
                } else {
                    bulletin = BulletinFactory.of(this).createSimpleBulletin(R.raw.chats_archived, LocaleController.formatPluralString("StoryArchived", count), LocaleController.getString("Undo"), Bulletin.DURATION_PROLONG, undo).show();
                }
                bulletin.setOnHideListener(() -> {
                    if (!undone[0] && applyBulletin != null) {
                        applyBulletin.run();
                    }
                    applyBulletin = null;
                });
            });
            buttonContainer.addView(button);
            buttonContainer.setAlpha(0f);
            buttonContainer.setTranslationY(dp(100));

            Bulletin.addDelegate(this, new Bulletin.Delegate() {
                @Override
                public int getBottomOffset(int tag) {
                    return AndroidUtilities.dp(64);
                }
            });

            floatingButtonContainer = new FrameLayout(context);
            floatingButtonContainer.setVisibility(View.VISIBLE);
            floatingButtonContainer.setOnClickListener(v -> {
                StoryRecorder.getInstance(getParentActivity(), getCurrentAccount())
                        .open(StoryRecorder.SourceView.fromFloatingButton(floatingButtonContainer));
            });

            floatingButton = new RLottieImageView(context);
            floatingButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), PorterDuff.Mode.MULTIPLY));
            floatingButton.setImageResource(R.drawable.story_camera);
            floatingButtonContainer.setContentDescription(LocaleController.getString("AccDescrCaptureStory", R.string.AccDescrCaptureStory));
//            if (Build.VERSION.SDK_INT >= 21) {
//                StateListAnimator animator = new StateListAnimator();
//                animator.addState(new int[]{android.R.attr.state_pressed}, ObjectAnimator.ofFloat(floatingButtonContainer, View.TRANSLATION_Z, AndroidUtilities.dp(2), AndroidUtilities.dp(4)).setDuration(200));
//                animator.addState(new int[]{}, ObjectAnimator.ofFloat(floatingButtonContainer, View.TRANSLATION_Z, AndroidUtilities.dp(4), AndroidUtilities.dp(2)).setDuration(200));
//                floatingButtonContainer.setStateListAnimator(animator);
//                floatingButtonContainer.setOutlineProvider(new ViewOutlineProvider() {
//                    @SuppressLint("NewApi")
//                    @Override
//                    public void getOutline(View view, Outline outline) {
//                        outline.setOval(0, 0, AndroidUtilities.dp(56), AndroidUtilities.dp(56));
//                    }
//                });
//            }
            Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
            if (Build.VERSION.SDK_INT < 21) {
                Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
                shadowDrawable.setColorFilter(new PorterDuffColorFilter(0xff000000, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56), AndroidUtilities.dp(56));
                drawable = combinedDrawable;
            }
            floatingButtonContainer.addView(floatingButton, LayoutHelper.createFrame(56, 56, Gravity.CENTER));
            if (floatingButtonContainer != null) {
                drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
                if (Build.VERSION.SDK_INT < 21) {
                    Drawable shadowDrawable = ContextCompat.getDrawable(getParentActivity(), R.drawable.floating_shadow).mutate();
                    shadowDrawable.setColorFilter(new PorterDuffColorFilter(0xff000000, PorterDuff.Mode.MULTIPLY));
                    CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
                    combinedDrawable.setIconSize(AndroidUtilities.dp(56), AndroidUtilities.dp(56));
                    drawable = combinedDrawable;
                }
                floatingButtonContainer.setBackground(drawable);
            }

            hideFloatingButton(true, false);
        }

        sharedMediaLayout = new SharedMediaLayout(context, dialogId, sharedMediaPreloader, 0, null, currentChatInfo, currentUserInfo, false, this, new SharedMediaLayout.Delegate() {
            @Override
            public void scrollToSharedMedia() {

            }

            @Override
            public boolean onMemberClick(TLRPC.ChatParticipant participant, boolean b, boolean resultOnly, View vi) {
                return false;
            }

            @Override
            public TLRPC.Chat getCurrentChat() {
                return null;
            }

            @Override
            public boolean isFragmentOpened() {
                return true;
            }

            @Override
            public RecyclerListView getListView() {
                return null;
            }

            @Override
            public boolean canSearchMembers() {
                return false;
            }

            @Override
            public void updateSelectedMediaTabText() {
                updateMediaCount();
            }

        }, SharedMediaLayout.VIEW_TYPE_MEDIA_ACTIVITY, getResourceProvider()) {
            @Override
            protected void onSelectedTabChanged() {
                updateMediaCount();
            }

            @Override
            protected boolean canShowSearchItem() {
                return false;
            }

            @Override
            protected void onSearchStateChanged(boolean expanded) {
                AndroidUtilities.removeAdjustResize(getParentActivity(), classGuid);
                AndroidUtilities.updateViewVisibilityAnimated(avatarContainer, !expanded, 0.95f, true);
            }

            @Override
            protected void drawBackgroundWithBlur(Canvas canvas, float y, Rect rectTmp2, Paint backgroundPaint) {
                fragmentView.drawBlurRect(canvas, getY() + y, rectTmp2, backgroundPaint, true);
            }

            @Override
            protected void invalidateBlur() {
                fragmentView.invalidateBlur();
            }

            @Override
            protected boolean isStoriesView() {
                return type == TYPE_STORIES;
            }

            @Override
            protected boolean includeStories() {
                return type == TYPE_STORIES;
            }

            @Override
            protected int getInitialTab() {
                return initialTab;
            }

            private AnimatorSet actionModeAnimation;

            @Override
            protected void showActionMode(boolean show) {
                if (isActionModeShowed == show) {
                    return;
                }
                isActionModeShowed = show;
                if (actionModeAnimation != null) {
                    actionModeAnimation.cancel();
                }
                if (type == TYPE_STORIES) {
                    disableScroll(show);
                }
                if (show) {
                    selectedTextView.setVisibility(VISIBLE);
                    if (buttonContainer != null) {
                        buttonContainer.setVisibility(VISIBLE);
                    }
                } else {
                    titlesContainer.setVisibility(VISIBLE);
                }
                backDrawable.setRotation(show ? 1f : 0f, true);
                actionModeAnimation = new AnimatorSet();
                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofFloat(selectedTextView, View.ALPHA, show ? 1.0f : 0.0f));
                animators.add(ObjectAnimator.ofFloat(titlesContainer, View.ALPHA, show ? 0.0f : 1.0f));
                if (buttonContainer != null) {
                    boolean showButton = show;
                    animators.add(ObjectAnimator.ofFloat(buttonContainer, View.ALPHA, showButton ? 1.0f : 0.0f));
                    animators.add(ObjectAnimator.ofFloat(buttonContainer, View.TRANSLATION_Y, showButton ? 0.0f : buttonContainer.getMeasuredHeight()));
                }
                if (deleteItem != null) {
                    deleteItem.setVisibility(View.VISIBLE);
                    animators.add(ObjectAnimator.ofFloat(deleteItem, View.ALPHA, show ? 1.0f : 0.0f));
                }
                final boolean empty = getStoriesCount(getClosestTab()) == 0;
                if (optionsItem != null) {
                    optionsItem.setVisibility(View.VISIBLE);
                    animators.add(ObjectAnimator.ofFloat(optionsItem, View.ALPHA, show || empty ? 0.0f : 1.0f));
                }
                if (tabsView != null) {
                    animators.add(ObjectAnimator.ofFloat(tabsView, View.ALPHA, show ? 0.4f : 1.0f));
                }
                actionModeAnimation.playTogether(animators);
                actionModeAnimation.setDuration(300);
                actionModeAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                actionModeAnimation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        actionModeAnimation = null;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (actionModeAnimation == null) {
                            return;
                        }
                        actionModeAnimation = null;
                        if (!show) {
                            selectedTextView.setVisibility(INVISIBLE);
                            if (buttonContainer != null) {
                                buttonContainer.setVisibility(INVISIBLE);
                            }
                            if (deleteItem != null) {
                                deleteItem.setVisibility(View.GONE);
                            }
                            if (empty && optionsItem != null) {
                                optionsItem.setVisibility(View.GONE);
                            }
                        } else {
                            titlesContainer.setVisibility(INVISIBLE);
                            if (optionsItem != null) {
                                optionsItem.setVisibility(View.GONE);
                            }
                        }
                    }
                });
                actionModeAnimation.start();
            }

            @Override
            protected void onActionModeSelectedUpdate(SparseArray<MessageObject> messageObjects) {
                final int count = messageObjects.size();
                actionModeMessageObjects = messageObjects;
                selectedTextView.cancelAnimation();
                selectedTextView.setText(LocaleController.formatPluralString("StoriesSelected", count), !LocaleController.isRTL);
                if (button != null) {
                    button.setEnabled(count > 0);
                    button.setCount(count, true);
                    if (sharedMediaLayout.getClosestTab() == SharedMediaLayout.TAB_STORIES) {
                        button.setText(LocaleController.formatPluralString("ArchiveStories", count), true);
                    }
                }
            }

            @Override
            protected void onTabProgress(float progress) {
                if (type != TYPE_STORIES)
                    return;
                float t = progress - TAB_STORIES;
                if (tabsView != null) {
                    tabsView.setProgress(t);
                }
                titles[0].setAlpha(1f - t);
                titles[0].setTranslationX(AndroidUtilities.dp(-12) * t);
                titles[1].setAlpha(t);
                titles[1].setTranslationX(AndroidUtilities.dp(12) * (1f - t));
            }

            @Override
            protected void onTabScroll(boolean scrolling) {
                if (tabsView != null) {
                    tabsView.setScrolling(scrolling);
                }
            }
        };
        sharedMediaLayout.setPinnedToTop(true);
        sharedMediaLayout.getSearchItem().setTranslationY(0);
        sharedMediaLayout.photoVideoOptionsItem.setTranslationY(0);

        if (type == TYPE_STORIES) {
            fragmentView.addView(sharedMediaLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.FILL, 0, 0, 0, 64));
        } else {
            fragmentView.addView(sharedMediaLayout);
        }
        fragmentView.addView(actionBar);
        fragmentView.addView(avatarContainer);
        fragmentView.blurBehindViews.add(sharedMediaLayout);
        if (type == TYPE_STORIES) {
            showSubtitle(0, false, false);
            showSubtitle(1, false, false);
        }

        if (floatingButtonContainer != null) {
            fragmentView.addView(floatingButtonContainer, LayoutHelper.createFrame((Build.VERSION.SDK_INT >= 21 ? 56 : 60), (Build.VERSION.SDK_INT >= 21 ? 56 : 60), (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.BOTTOM, LocaleController.isRTL ? 14 : 0, 0, LocaleController.isRTL ? 0 : 14, 14 + 64));
        }
        if (tabsView != null) {
            fragmentView.addView(tabsView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.FILL_HORIZONTAL));
        }
        if (buttonContainer != null) {
            fragmentView.addView(buttonContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 64, Gravity.BOTTOM | Gravity.FILL_HORIZONTAL));
        }

        TLObject avatarObject = null;
        if (type == TYPE_STORIES) {
            nameTextView[0].setText(LocaleController.getString("ProfileMyStories"));
            nameTextView[1].setText(LocaleController.getString("ProfileStoriesArchive"));
        } else if (DialogObject.isEncryptedDialog(dialogId)) {
            TLRPC.EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(DialogObject.getEncryptedChatId(dialogId));
            if (encryptedChat != null) {
                TLRPC.User user = getMessagesController().getUser(encryptedChat.user_id);
                if (user != null) {
                    nameTextView[0].setText(ContactsController.formatName(user.first_name, user.last_name));
                    avatarDrawable.setInfo(user);
                    avatarObject = user;
                }
            }
        } else if (DialogObject.isUserDialog(dialogId)) {
            TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(dialogId);
            if (user != null) {
                if (user.self) {
                    nameTextView[0].setText(LocaleController.getString("SavedMessages", R.string.SavedMessages));
                    avatarDrawable.setAvatarType(AvatarDrawable.AVATAR_TYPE_SAVED);
                    avatarDrawable.setScaleSize(.8f);
                } else {
                    nameTextView[0].setText(ContactsController.formatName(user.first_name, user.last_name));
                    avatarDrawable.setInfo(user);
                    avatarObject = user;
                }
            }
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(-dialogId);
            if (chat != null) {
                nameTextView[0].setText(chat.title);
                avatarDrawable.setInfo(chat);
                avatarObject = chat;
            }
        }

        final ImageLocation thumbLocation = ImageLocation.getForUserOrChat(avatarObject, ImageLocation.TYPE_SMALL);
        avatarImageView.setImage(thumbLocation, "50_50", avatarDrawable, avatarObject);

        if (TextUtils.isEmpty(nameTextView[0].getText())) {
            nameTextView[0].setText(LocaleController.getString("SharedContentTitle", R.string.SharedContentTitle));
        }

        if (sharedMediaLayout.isSearchItemVisible() && type != TYPE_STORIES) {
            sharedMediaLayout.getSearchItem().setVisibility(View.VISIBLE);
        }
        if (sharedMediaLayout.isCalendarItemVisible() && type != TYPE_STORIES) {
            sharedMediaLayout.photoVideoOptionsItem.setVisibility(View.VISIBLE);
        } else {
            sharedMediaLayout.photoVideoOptionsItem.setVisibility(View.INVISIBLE);
        }

        actionBar.setDrawBlurBackground(fragmentView);
        AndroidUtilities.updateViewVisibilityAnimated(avatarContainer, true, 1, false);
        updateMediaCount();
        updateColors();

        if (type == TYPE_STORIES && initialTab == SharedMediaLayout.TAB_ARCHIVED_STORIES) {
            sharedMediaLayout.onTabProgress(9f);
        }
        return fragmentView;
    }

    @Override
    public boolean onBackPressed() {
        if (closeStoryViewer()) {
            return false;
        }
        if (sharedMediaLayout.isActionModeShown()) {
            sharedMediaLayout.closeActionMode(false);
            return false;
        }
        return super.onBackPressed();
    }

    @Override
    public boolean isSwipeBackEnabled(MotionEvent event) {
        if (!sharedMediaLayout.isSwipeBackEnabled()) {
            return false;
        }
        return sharedMediaLayout.isCurrentTabFirst();
    }

    @Override
    public boolean canBeginSlide() {
        if (!sharedMediaLayout.isSwipeBackEnabled()) {
            return false;
        }
        return super.canBeginSlide();
    }

    private int lastTab;
    private void updateMediaCount() {
        int id = sharedMediaLayout.getClosestTab();
        int[] mediaCount = sharedMediaPreloader.getLastMediaCount();
        final boolean animated = !LocaleController.isRTL;
        int i;
        if (type != TYPE_STORIES) {
            i = 0;
        } else {
            i = id == SharedMediaLayout.TAB_STORIES ? 0 : 1;
        }
        if (id == SharedMediaLayout.TAB_STORIES || id == SharedMediaLayout.TAB_ARCHIVED_STORIES) {
            if (zoomOutItem != null) {
                zoomOutItem.setEnabled(sharedMediaLayout.canZoomOut());
                zoomOutItem.setAlpha(zoomOutItem.isEnabled() ? 1f : .5f);
            }
            if (zoomInItem != null) {
                zoomInItem.setEnabled(sharedMediaLayout.canZoomIn());
                zoomInItem.setAlpha(zoomInItem.isEnabled() ? 1f : .5f);
            }

            int count = sharedMediaLayout.getStoriesCount(SharedMediaLayout.TAB_STORIES);
            if (count > 0) {
                showSubtitle(0, true, true);
                subtitleTextView[0].setText(LocaleController.formatPluralString("ProfileMyStoriesCount", count), animated);
            } else {
                showSubtitle(0, false, true);
            }

            if (type == TYPE_STORIES) {
                count = sharedMediaLayout.getStoriesCount(SharedMediaLayout.TAB_ARCHIVED_STORIES);
                if (count > 0) {
                    showSubtitle(1, true, true);
                    subtitleTextView[1].setText(LocaleController.formatPluralString("ProfileStoriesArchiveCount", count), animated);
                } else {
                    showSubtitle(1, false, true);
                }
                hideFloatingButton(id != SharedMediaLayout.TAB_ARCHIVED_STORIES || sharedMediaLayout.getStoriesCount(SharedMediaLayout.TAB_ARCHIVED_STORIES) > 0, true);
            }

            if (optionsItem != null) {
                final boolean empty = sharedMediaLayout.getStoriesCount(sharedMediaLayout.getClosestTab()) <= 0;
                if (!empty) {
                    optionsItem.setVisibility(View.VISIBLE);
                }
                optionsItem.animate().alpha(empty ? 0f : 1f).withEndAction(() -> {
                    if (empty) {
                        optionsItem.setVisibility(View.GONE);
                    }
                }).setDuration(220).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
            }

            if (button != null) {
                boolean animated2 = animated && lastTab == id;
                if (id == SharedMediaLayout.TAB_STORIES) {
                    button.setText(LocaleController.formatPluralString("ArchiveStories", actionModeMessageObjects == null ? 0 : actionModeMessageObjects.size()), animated2);
                } else {
                    button.setText(LocaleController.getString("SaveToProfile", R.string.SaveToProfile), animated2);
                }
                lastTab = id;
            }

            if (calendarItem != null) {
                boolean calendarAvailable = sharedMediaLayout.getStoriesCount(id) > 0;
                calendarItem.setEnabled(calendarAvailable);
                calendarItem.setAlpha(calendarAvailable ? 1f : .5f);
            }

            return;
        }
        if (id < 0 || mediaCount[id] < 0) {
            return;
        }
        if (id == SharedMediaLayout.TAB_PHOTOVIDEO) {
            showSubtitle(i, true, true);
            if (sharedMediaLayout.getPhotosVideosTypeFilter() == SharedMediaLayout.FILTER_PHOTOS_ONLY) {
                subtitleTextView[i].setText(LocaleController.formatPluralString("Photos", mediaCount[MediaDataController.MEDIA_PHOTOS_ONLY]), animated);
            } else if (sharedMediaLayout.getPhotosVideosTypeFilter() == SharedMediaLayout.FILTER_VIDEOS_ONLY) {
                subtitleTextView[i].setText(LocaleController.formatPluralString("Videos", mediaCount[MediaDataController.MEDIA_VIDEOS_ONLY]), animated);
            } else {
                subtitleTextView[i].setText(LocaleController.formatPluralString("Media", mediaCount[MediaDataController.MEDIA_PHOTOVIDEO]), animated);
            }
        } else if (id == SharedMediaLayout.TAB_FILES) {
            showSubtitle(i, true, true);
            subtitleTextView[i].setText(LocaleController.formatPluralString("Files", mediaCount[MediaDataController.MEDIA_FILE]), animated);
        } else if (id == SharedMediaLayout.TAB_VOICE) {
            showSubtitle(i, true, true);
            subtitleTextView[i].setText(LocaleController.formatPluralString("Voice", mediaCount[MediaDataController.MEDIA_AUDIO]), animated);
        } else if (id == SharedMediaLayout.TAB_LINKS) {
            showSubtitle(i, true, true);
            subtitleTextView[i].setText(LocaleController.formatPluralString("Links", mediaCount[MediaDataController.MEDIA_URL]), animated);
        } else if (id == SharedMediaLayout.TAB_AUDIO) {
            showSubtitle(i, true, true);
            subtitleTextView[i].setText(LocaleController.formatPluralString("MusicFiles", mediaCount[MediaDataController.MEDIA_MUSIC]), animated);
        } else if (id == SharedMediaLayout.TAB_GIF) {
            showSubtitle(i, true, true);
            subtitleTextView[i].setText(LocaleController.formatPluralString("GIFs", mediaCount[MediaDataController.MEDIA_GIF]), animated);
        }
    }

    public void setChatInfo(TLRPC.ChatFull currentChatInfo) {
        this.currentChatInfo = currentChatInfo;
    }

    public long getDialogId() {
        return dialogId;
    }

    private float floatingButtonTranslation1;
    private float floatingButtonTranslation2;
    private void updateFloatingButtonOffset() {
        if (floatingButtonContainer == null) {
            return;
        }
        floatingButtonContainer.setTranslationY(floatingButtonTranslation + floatingButtonTranslation1 + floatingButtonTranslation2);
    }

    private boolean floatingHidden;
    private AnimatorSet floatingAnimator;
    private float floatingButtonHideProgress, floatingButtonTranslation;
    private void hideFloatingButton(boolean hide, boolean animated) {
        if (floatingButtonContainer == null) {
            return;
        }
        if (!getMessagesController().storiesEnabled()) {
            hide = true;
        }
        if (floatingHidden == hide) {
            return;
        }
        floatingHidden = hide;
        if (floatingAnimator != null) {
            floatingAnimator.cancel();
        }
        if (animated) {
            floatingButtonContainer.setVisibility(View.VISIBLE);
            floatingAnimator = new AnimatorSet();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(floatingButtonHideProgress, floatingHidden ? 1f : 0f);
            valueAnimator.addUpdateListener(animation -> {
                floatingButtonHideProgress = (float) animation.getAnimatedValue();
                floatingButtonTranslation = dp(100) * floatingButtonHideProgress;
                updateFloatingButtonOffset();
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (floatingHidden) {
                        floatingButtonContainer.setVisibility(View.GONE);
                    }
                }
            });
            floatingAnimator.playTogether(valueAnimator);
            floatingAnimator.setDuration(300);
            floatingAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            floatingButtonContainer.setClickable(!hide);
            floatingAnimator.start();
        } else {
            floatingButtonHideProgress = hide ? 1f : 0f;
            floatingButtonTranslation = dp(100) * floatingButtonHideProgress;
            updateFloatingButtonOffset();
            floatingButtonContainer.setVisibility(hide ? View.GONE : View.VISIBLE);
        }
    }

    private final boolean[] subtitleShown = new boolean[2];
    private final float[] subtitleT = new float[2];
    private final boolean[] firstSubtitleCheck = new boolean[] { true, true };
    private final ValueAnimator[] subtitleAnimator = new ValueAnimator[2];
    private void showSubtitle(int i, boolean show, boolean animated) {
        if (subtitleShown[i] == show && !firstSubtitleCheck[i]) {
            return;
        }
        animated = !firstSubtitleCheck[i] && animated;
        firstSubtitleCheck[i] = false;
        subtitleShown[i] = show;
        if (subtitleAnimator[i] != null) {
            subtitleAnimator[i].cancel();
            subtitleAnimator[i] = null;
        }
        if (animated) {
            subtitleTextView[i].setVisibility(View.VISIBLE);
            subtitleAnimator[i] = ValueAnimator.ofFloat(subtitleT[i], show ? 1f : 0f);
            subtitleAnimator[i].addUpdateListener(anm -> {
                subtitleT[i] = (float) anm.getAnimatedValue();
                nameTextView[i].setScaleX(lerp(1.111f, 1f, subtitleT[i]));
                nameTextView[i].setScaleY(lerp(1.111f, 1f, subtitleT[i]));
                nameTextView[i].setTranslationY(lerp(dp(8), 0, subtitleT[i]));
                subtitleTextView[i].setAlpha(subtitleT[i]);
            });
            subtitleAnimator[i].addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    subtitleT[i] = show ? 1f : 0f;
                    nameTextView[i].setScaleX(show ? 1f : 1.111f);
                    nameTextView[i].setScaleY(show ? 1f : 1.111f);
                    nameTextView[i].setTranslationY(show ? 0 : dp(8));
                    subtitleTextView[i].setAlpha(show ? 1f : 0f);

                    if (!show) {
                        subtitleTextView[i].setVisibility(View.GONE);
                    }
                }
            });
            subtitleAnimator[i].setDuration(320);
            subtitleAnimator[i].setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            subtitleAnimator[i].start();
        } else {
            subtitleT[i] = show ? 1f : 0f;
            nameTextView[i].setScaleX(show ? 1f : 1.111f);
            nameTextView[i].setScaleY(show ? 1f : 1.111f);
            nameTextView[i].setTranslationY(show ? 0 : dp(8));
            subtitleTextView[i].setAlpha(show ? 1f : 0f);
            subtitleTextView[i].setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void mediaCountUpdated() {
        if (sharedMediaLayout != null && sharedMediaPreloader != null) {
            sharedMediaLayout.setNewMediaCounts(sharedMediaPreloader.getLastMediaCount());
        }
        updateMediaCount();
    }


    private void updateColors() {
        actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), false);
        actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), false);
        actionBar.setTitleColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        nameTextView[0].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        if (nameTextView[1] != null) {
            nameTextView[1].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate themeDelegate = this::updateColors;
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_actionBarActionModeDefaultSelector));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.addAll(sharedMediaLayout.getThemeDescriptions());
        return arrayList;
    }

    @Override
    public boolean isLightStatusBar() {
        int color = Theme.getColor(Theme.key_windowBackgroundWhite);
        if (actionBar.isActionModeShowed()) {
            color = Theme.getColor(Theme.key_actionBarActionModeDefault);
        }
        return ColorUtils.calculateLuminance(color) > 0.7f;
    }

    @Override
    public List<FloatingDebugController.DebugItem> onGetDebugItems() {
        return Arrays.asList(
            new FloatingDebugController.DebugItem(
                (ShapeDetector.isLearning(getContext()) ? "Disable" : "Enable") + " shape detector learning debug",
                () -> {
                    ShapeDetector.setLearning(getContext(), !ShapeDetector.isLearning(getContext()));
                }
            )
        );
    }

    private class StoriesTabsView extends View {

        private final Theme.ResourcesProvider resourcesProvider;
        private final Tab[] tabs = new Tab[2];

        class Tab {
            final int i;
            final RLottieDrawable drawable;
            final Drawable ripple;

            final TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            final StaticLayout layout;
            final float layoutWidth, layoutLeft;

            final RectF clickRect = new RectF();

            final AnimatedFloat nonscrollingT = new AnimatedFloat(StoriesTabsView.this, 0, 200, CubicBezierInterpolator.EASE_OUT_QUINT);

            public Tab(int i, int resId, CharSequence text) {
                this.i = i;

                drawable = new RLottieDrawable(resId, "" + resId, dp(29), dp(29));
                drawable.setMasterParent(StoriesTabsView.this);
                drawable.setAllowDecodeSingleFrame(true);
                drawable.setPlayInDirectionOfCustomEndFrame(true);
                drawable.setAutoRepeat(0);

                paint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                paint.setTextSize(dp(12));
                paint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
                layout = new StaticLayout(text, paint, AndroidUtilities.displaySize.x, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);
                layoutWidth = layout.getLineCount() > 0 ? layout.getLineWidth(0) : 0;
                layoutLeft = layout.getLineCount() > 0 ? layout.getLineLeft(0) : 0;

                ripple = Theme.createSelectorDrawable(Theme.multAlpha(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider), .1f), Theme.RIPPLE_MASK_ROUNDRECT_6DP, dp(16));
            }

            private boolean active;
            public void setActive(boolean active, boolean animated) {
                if (this.active == active) {
                    return;
                }

                if (i == 0) {
                    // 0 - 20
                    // 20 - 40
                    if (active) {
                        drawable.setCustomEndFrame(20);
                        if (drawable.getCurrentFrame() >= 38) {
                            drawable.setCurrentFrame(0, false);
                        }
                        if (drawable.getCurrentFrame() <= 20) {
                            drawable.start();
                        } else {
                            drawable.setCurrentFrame(20);
                        }
                    } else {
                        if (drawable.getCurrentFrame() >= 19) {
                            drawable.setCustomEndFrame(39);
                            drawable.start();
                        } else {
                            drawable.setCustomEndFrame(0);
                            drawable.setCurrentFrame(0);
                        }
                    }
                } else if (i == 1 && active) {
                    drawable.setCurrentFrame(0);
                    if (animated) {
                        drawable.start();
                    }
                }
                this.active = active;
            }

            private int drawableColor = -1;
            public void setColor(int color) {
                paint.setColor(color);
                if (drawableColor != color) {
                    drawable.setColorFilter(new PorterDuffColorFilter(drawableColor = color, PorterDuff.Mode.SRC_IN));
                }
            }
        }

        private final Paint selectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private float progress;
        private int value;

        private boolean scrolling;
        private AnimatedFloat scrollingT = new AnimatedFloat(this, 0, 210, CubicBezierInterpolator.EASE_OUT_QUINT);

        public StoriesTabsView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;

            tabs[0] = new Tab(0, R.raw.msg_stories_saved, LocaleController.getString("ProfileMyStoriesTab", R.string.ProfileMyStoriesTab));
            tabs[1] = new Tab(1, R.raw.msg_stories_archive, LocaleController.getString("ProfileStoriesArchiveTab", R.string.ProfileStoriesArchiveTab));

            setPadding(dp(12), 0, dp(12), 0);

            setProgress(0, false);
        }

        public void setScrolling(boolean scrolling) {
            if (this.scrolling == scrolling) {
                return;
            }
            this.scrolling = scrolling;
            invalidate();
        }

        public void setProgress(float progress) {
            setProgress(progress, true);
        }

        private void setProgress(float progress, boolean animated) {
            this.value = Math.round(this.progress = Utilities.clamp(progress, tabs.length, 0));
            for (int i = 0; i < tabs.length; ++i) {
                tabs[i].setActive(Math.abs(value - i) < (tabs[i].active ? .25f : .35f), animated);
            }
            invalidate();
        }

        private Utilities.Callback<Integer> onTabClick;
        public void setOnTabClick(Utilities.Callback<Integer> listener) {
            onTabClick = listener;
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            canvas.drawColor(Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider));

            canvas.drawRect(0, 0, getWidth(), AndroidUtilities.getShadowHeight(), Theme.dividerPaint);

            int tabFullWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / tabs.length;
            int tabWidth = Math.min(dp(64), tabFullWidth);

            float scrollingT = this.scrollingT.set(scrolling);

            if (scrollingT > 0) {
                double halfT = .4f + 2 * (1 - .4f) * Math.abs(.5f + Math.floor(progress) - progress);
                selectPaint.setColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider), (int) (0x12 * halfT * scrollingT)));
                float sx = getPaddingLeft() + lerp(tabFullWidth * (float) Math.floor(progress) + tabFullWidth / 2f, tabFullWidth * (float) Math.ceil(progress) + tabFullWidth / 2f, progress - (int) progress);
                AndroidUtilities.rectTmp.set(
                    sx - tabWidth / 2f,
                    dp(9),
                    sx + tabWidth / 2f,
                    dp(9 + 32)
                );
                canvas.drawRoundRect(AndroidUtilities.rectTmp, dp(16), dp(16), selectPaint);
            }

            for (int i = 0; i < tabs.length; ++i) {
                Tab tab = tabs[i];
                final int x = getPaddingLeft() + i * tabFullWidth;
                tab.clickRect.set(x, 0, x + tabFullWidth, getHeight());

                float t = 1f - Math.min(1, Math.abs(progress - i));
                tab.setColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6, resourcesProvider), Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider), t));

                AndroidUtilities.rectTmp2.set(
                    (int) (tab.clickRect.centerX() - tabWidth / 2f),
                    dp(9),
                    (int) (tab.clickRect.centerX() + tabWidth / 2f),
                    dp(9 + 32)
                );
                final float T = tab.nonscrollingT.set(t > .6f);
                if (scrollingT < 1) {
                    selectPaint.setColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider), (int) (0x12 * T * (1f - scrollingT))));
                    AndroidUtilities.rectTmp.set(AndroidUtilities.rectTmp2);
                    canvas.drawRoundRect(AndroidUtilities.rectTmp, dp(16), dp(16), selectPaint);
                }

                tab.ripple.setBounds(AndroidUtilities.rectTmp2);
                tab.ripple.draw(canvas);

                final int drawableSize = dp(29);
                AndroidUtilities.rectTmp2.set(
                    (int) (tab.clickRect.centerX() - drawableSize / 2f),
                    (int) (dpf2(24.66f) - drawableSize / 2f),
                    (int) (tab.clickRect.centerX() + drawableSize / 2f),
                    (int) (dpf2(24.66f) + drawableSize / 2f)
                );

                tab.drawable.setBounds(AndroidUtilities.rectTmp2);
                tab.drawable.draw(canvas);

                canvas.save();
                canvas.translate(tab.clickRect.centerX() - tab.layoutWidth / 2f - tab.layoutLeft, dp(50) - tab.layout.getHeight() / 2f);
                tab.layout.draw(canvas);
                canvas.restore();
            }
        }

        private boolean touchDown;
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                touchDown = true;
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_MOVE) {
                int index = -1;
                final float x = event.getX();
                for (int i = 0; i < tabs.length; ++i) {
                    if (tabs[i].clickRect.left < x && tabs[i].clickRect.right > x) {
                        if (event.getAction() != MotionEvent.ACTION_UP) {
                            if (touchDown) {
                                tabs[i].ripple.setState(new int[]{});
                            }
                            tabs[i].ripple.setState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled});
                        }
                        index = i;
                        break;
                    }
                }
                for (int i = 0; i < tabs.length; ++i) {
                    if (i != index || event.getAction() == MotionEvent.ACTION_UP) {
                        tabs[i].ripple.setState(new int[] {});
                    }
                }
                if (index >= 0 && value != index && onTabClick != null) {
                    onTabClick.run(index);
                }
                touchDown = false;
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    for (int i = 0; i < tabs.length; ++i) {
                        tabs[i].ripple.setState(new int[] {});
                    }
                }
                touchDown = false;
                return true;
            }
            return super.onTouchEvent(event);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec),
                dp(64) + AndroidUtilities.getShadowHeight()
            );
        }

        @Override
        protected boolean verifyDrawable(@NonNull Drawable who) {
            for (int i = 0; i < tabs.length; ++i) {
                if (tabs[i].ripple == who) {
                    return true;
                }
            }
            return super.verifyDrawable(who);
        }
    }
}
