/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.ui;

import static org.telegram.messenger.ContactsController.PRIVACY_RULES_TYPE_ADDED_BY_PHONE;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebStorage;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.jakewharton.processphoenix.ProcessPhoenix;

import org.apache.commons.lang3.StringUtils;

import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AboutLinkCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.SettingsSearchCell;
import org.telegram.ui.Cells.SettingsSuggestionCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.AutoDeletePopupWrapper;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackButtonMenu;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CanvasButton;
import org.telegram.ui.Components.ChatActivityInterface;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ChatNotificationsPopupWrapper;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.DotDividerSpan;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmojiPacksAlert;
import org.telegram.ui.Components.EmptyStubSpan;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugController;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.IdenticonDrawable;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.InstantCameraView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet;
import org.telegram.ui.Components.Premium.ProfilePremiumCell;
import org.telegram.ui.Components.ProfileGalleryView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScamDrawable;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.TimerDrawable;
import org.telegram.ui.Components.TranslateAlert2;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.VectorAvatarThumbDrawable;
import org.telegram.ui.Components.voip.VoIPHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import java.util.concurrent.atomic.AtomicReference;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import kotlin.Unit;
import libv2ray.Libv2ray;
import tw.nekomimi.nekogram.transtale.popupwrapper.AutoTranslatePopupWrapper;
import tw.nekomimi.nekogram.ui.BottomBuilder;
import tw.nekomimi.nekogram.InternalUpdater;
import tw.nekomimi.nekogram.DatacenterActivity;
import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.NekoXConfig;
import tw.nekomimi.nekogram.settings.NekoXSettingActivity;
import tw.nekomimi.nekogram.parts.DialogTransKt;
import tw.nekomimi.nekogram.settings.NekoSettingsActivity;
import tw.nekomimi.nekogram.utils.AlertUtil;
import tw.nekomimi.nekogram.utils.EnvUtil;
import tw.nekomimi.nekogram.utils.FileUtil;
import tw.nekomimi.nekogram.utils.LangsKt;
import tw.nekomimi.nekogram.utils.ProxyUtil;
import tw.nekomimi.nekogram.utils.ShareUtil;
import tw.nekomimi.nekogram.utils.UIUtil;
import xyz.nextalone.nagram.helper.MessageHelper;

public class ProfileActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate, SharedMediaLayout.SharedMediaPreloaderDelegate, ImageUpdater.ImageUpdaterDelegate, SharedMediaLayout.Delegate {
    private final static int PHONE_OPTION_CALL = 0,
            PHONE_OPTION_COPY = 1,
            PHONE_OPTION_TELEGRAM_CALL = 2,
            PHONE_OPTION_TELEGRAM_VIDEO_CALL = 3;

    private RecyclerListView listView;
    private RecyclerListView searchListView;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private SearchAdapter searchAdapter;
    private SimpleTextView[] nameTextView = new SimpleTextView[2];
    private String nameTextViewRightDrawableContentDescription = null;
    private SimpleTextView[] onlineTextView = new SimpleTextView[4];
    private AudioPlayerAlert.ClippingTextViewSwitcher mediaCounterTextView;
    private SimpleTextView idTextView;
    private RLottieImageView writeButton;
    private AnimatorSet writeButtonAnimation;
    //    private AnimatorSet qrItemAnimation;
    private Drawable lockIconDrawable;
    private Drawable verifiedDrawable;
    private Drawable premiumStarDrawable;
    private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] emojiStatusDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[2];
    private Drawable verifiedCheckDrawable;
    private CrossfadeDrawable verifiedCrossfadeDrawable;
    private CrossfadeDrawable premiumCrossfadeDrawable;
    private ScamDrawable scamDrawable;
    private UndoView undoView;
    private OverlaysView overlaysView;
    private SharedMediaLayout sharedMediaLayout;
    private StickerEmptyView emptyView;
    private boolean sharedMediaLayoutAttached;
    private SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader;

    private RLottieDrawable cameraDrawable;
    private RLottieDrawable cellCameraDrawable;

    private HintView fwdRestrictedHint;
    private FrameLayout avatarContainer;
    private FrameLayout avatarContainer2;
    private DrawerProfileCell.AnimatedStatusView animatedStatusView;
    private AvatarImageView avatarImage;
    private View avatarOverlay;
    private AnimatorSet avatarAnimation;
    private RadialProgressView avatarProgressView;
    private ImageView timeItem;
    private TimerDrawable timerDrawable;
    private ProfileGalleryView avatarsViewPager;
    private PagerIndicatorView avatarsViewPagerIndicatorView;
    private AvatarDrawable avatarDrawable;
    private ImageUpdater imageUpdater;
    private int avatarColor;
    TimerDrawable autoDeleteItemDrawable;

    private View scrimView = null;
    private Paint scrimPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        @Override
        public void setAlpha(int a) {
            super.setAlpha(a);
            fragmentView.invalidate();
        }
    };
    private Paint actionBarBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ActionBarPopupWindow scrimPopupWindow;
    private Theme.ResourcesProvider resourcesProvider;

    private int overlayCountVisible;

    private ImageLocation prevLoadedImageLocation;

    private int lastMeasuredContentWidth;
    private int lastMeasuredContentHeight;
    private int listContentHeight;
    private boolean openingAvatar;
    private boolean fragmentViewAttached;

    private boolean doNotSetForeground;

    private boolean[] isOnline = new boolean[1];

    private boolean callItemVisible;
    private boolean videoCallItemVisible;
    private boolean editItemVisible;
    private ActionBarMenuItem animatingItem;
    private ActionBarMenuItem callItem;
    private ActionBarMenuItem videoCallItem;
    private ActionBarMenuItem editItem;
    private ActionBarMenuItem otherItem;
    private ActionBarMenuItem searchItem;
    private ImageView ttlIconView;
    //    private ActionBarMenuItem qrItem;
    private ActionBarMenuSubItem autoDeleteItem;
    AutoDeletePopupWrapper autoDeletePopupWrapper;
    protected float headerShadowAlpha = 1.0f;
    private TopView topView;
    private long userId;
    private long chatId;
    private int topicId;
    private long dialogId;
    private boolean creatingChat;
    private boolean userBlocked;
    private boolean reportSpam;
    private long mergeDialogId;
    private boolean expandPhoto;
    private boolean needSendMessage;
    private boolean hasVoiceChatItem;
    private boolean isTopic;

    private boolean scrolling;

    private boolean canSearchMembers;

    private boolean loadingUsers;
    private LongSparseArray<TLRPC.ChatParticipant> participantsMap = new LongSparseArray<>();
    private boolean usersEndReached;

    private long banFromGroup;
    private boolean openAnimationInProgress;
    private boolean transitionAnimationInProress;
    private boolean recreateMenuAfterAnimation;
    private int playProfileAnimation;
    private boolean needTimerImage;
    private boolean allowProfileAnimation = true;
    private boolean disableProfileAnimation = false;
    private float extraHeight;
    private float initialAnimationExtraHeight;
    private float avatarAnimationProgress;

    private int searchTransitionOffset;
    private float searchTransitionProgress;
    private Animator searchViewTransition;
    private boolean searchMode;

    private HashMap<Integer, Integer> positionToOffset = new HashMap<>();

    private float avatarX;
    private float avatarY;
    private float avatarScale;
    private float nameX;
    private float nameY;
    private float onlineX;
    private float onlineY;
    private float idX;
    private float idY;
    private float expandProgress;
    private float listViewVelocityY;
    private ValueAnimator expandAnimator;
    private float currentExpandAnimatorValue;
    private float currentExpanAnimatorFracture;
    private float[] expandAnimatorValues = new float[]{0f, 1f};
    private boolean isInLandscapeMode;
    private boolean allowPullingDown;
    private boolean isPulledDown;

    private Paint whitePaint = new Paint();

    private boolean isBot;

    private TLRPC.ChatFull chatInfo;
    private TLRPC.UserFull userInfo;

    private CharSequence currentBio;

    private long selectedUser;
    private int onlineCount = -1;
    private ArrayList<Integer> sortedUsers;

    private TLRPC.EncryptedChat currentEncryptedChat;
    private TLRPC.Chat currentChat;
    private TLRPC.BotInfo botInfo;
    private TLRPC.ChannelParticipant currentChannelParticipant;
    private TLRPC.TL_account_password currentPassword;

    private TLRPC.FileLocation avatar;
    private TLRPC.FileLocation avatarBig;
    private ImageLocation uploadingImageLocation;

    private final static int add_contact = 1;
    private final static int block_contact = 2;
    private final static int share_contact = 3;
    private final static int edit_contact = 4;
    private final static int delete_contact = 5;
    private final static int leave_group = 7;
    private final static int invite_to_group = 9;
    private final static int share = 10;
    private final static int qr_code = 11;
    private final static int edit_channel = 12;
    private final static int add_shortcut = 14;
    private final static int call_item = 15;
    private final static int video_call_item = 16;
    private final static int search_members = 17;
    private final static int add_member = 18;
    private final static int statistics = 19;
    private final static int start_secret_chat = 20;
    private final static int gallery_menu_save = 21;
    private final static int event_log = 42;
    private final static int view_discussion = 22;
    private final static int delete_topic = 23;

    private final static int aliasChannelName = 43;
    private final static int edit_name = 30;
    private final static int logout = 31;
    private final static int search_button = 32;
    private final static int set_as_main = 33;
    private final static int edit_avatar = 34;
    private final static int delete_avatar = 35;
    private final static int add_photo = 36;
    private final static int qr_button = 37;
//    private final static int gift_premium = 38;

    private Rect rect = new Rect();

    private TextCell setAvatarCell;

    private int rowCount;

    private int setAvatarRow;
    private int setAvatarSectionRow;
    private int numberSectionRow;
    private int numberRow;
    private int setUsernameRow;
    private int bioRow;
    private int phoneSuggestionSectionRow;
    private int phoneSuggestionRow;
    private int passwordSuggestionSectionRow;
    private int passwordSuggestionRow;
    private int settingsSectionRow;
    private int settingsSectionRow2;
    private int notificationRow;
    private int nekoRow;
    private int languageRow;
    private int privacyRow;
    private int dataRow;
    private int chatRow;
    private int filtersRow;
    private int liteModeRow;
    private int stickersRow;
    private int devicesRow;
    private int devicesSectionRow;
    private int helpHeaderRow;
    private int questionRow;
    private int faqRow;
    private int policyRow;
    private int helpSectionCell;
    private int debugHeaderRow;
    private int sendLogsRow;
    private int sendLastLogsRow;
    private int clearLogsRow;
    private int switchBackendRow;
    private int versionRow;
    private int emptyRow;
    private int bottomPaddingRow;
    private int infoHeaderRow;
    private int phoneRow;
    private int locationRow;
    private int userInfoRow;
    private int channelInfoRow;
    private int usernameRow;
    private int restrictionReasonRow;
    private int notificationsDividerRow;
    private int notificationsRow;
    private int notificationsSimpleRow;
    private int infoSectionRow;
    private int sendMessageRow;
    private int reportRow;
    private int reportReactionRow;
    private int reportDividerRow;
    private int addToContactsRow;
    private int addToGroupButtonRow;
    private int addToGroupInfoRow;
    private int premiumRow;
    private int premiumSectionsRow;

    private int settingsTimerRow;
    private int settingsKeyRow;
    private int secretSettingsSectionRow;

    private int membersHeaderRow;
    private int membersStartRow;
    private int membersEndRow;
    private int addMemberRow;
    private int subscribersRow;
    private int subscribersRequestsRow;
    private int administratorsRow;
    private int blockedUsersRow;
    private int membersSectionRow;

    private int sharedMediaRow;

    private int unblockRow;
    private int joinRow;
    private int lastSectionRow;

    private int transitionIndex;
    private final ArrayList<TLRPC.ChatParticipant> visibleChatParticipants = new ArrayList<>();
    private final ArrayList<Integer> visibleSortedUsers = new ArrayList<>();
    private int usersForceShowingIn = 0;

    private boolean hideNumber;

    private boolean firstLayout = true;
    private boolean invalidateScroll = true;
    private boolean isQrItemVisible = true;

    PinchToZoomHelper pinchToZoomHelper;

    private View transitionOnlineText;
    private int actionBarAnimationColorFrom = 0;
    private int navigationBarAnimationColorFrom = 0;
    private int reportReactionMessageId = 0;
    private long reportReactionFromDialogId = 0;

    private boolean isFragmentPhoneNumber;

    private boolean showAddToContacts;
    private String vcardPhone;
    private String vcardFirstName;
    private String vcardLastName;

    ChatActivityInterface previousTransitionFragment;

    HashSet<Integer> notificationsExceptionTopics = new HashSet<>();

    private final Property<ProfileActivity, Float> HEADER_SHADOW = new AnimationProperties.FloatProperty<ProfileActivity>("headerShadow") {
        @Override
        public void setValue(ProfileActivity object, float value) {
            headerShadowAlpha = value;
            topView.invalidate();
        }

        @Override
        public Float get(ProfileActivity object) {
            return headerShadowAlpha;
        }
    };

    public PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {

        @Override
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            if (fileLocation == null) {
                return null;
            }

            TLRPC.FileLocation photoBig = null;
            if (userId != 0) {
                TLRPC.User user = getMessagesController().getUser(userId);
                if (user != null && user.photo != null && user.photo.photo_big != null) {
                    photoBig = user.photo.photo_big;
                }
            } else if (chatId != 0) {
                TLRPC.Chat chat = getMessagesController().getChat(chatId);
                if (chat != null && chat.photo != null && chat.photo.photo_big != null) {
                    photoBig = chat.photo.photo_big;
                }
            }

            if (photoBig != null && photoBig.local_id == fileLocation.local_id && photoBig.volume_id == fileLocation.volume_id && photoBig.dc_id == fileLocation.dc_id) {
                int[] coords = new int[2];
                avatarImage.getLocationInWindow(coords);
                PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                object.viewX = coords[0];
                object.viewY = coords[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                object.parentView = avatarImage;
                object.imageReceiver = avatarImage.getImageReceiver();
                if (userId != 0) {
                    object.dialogId = userId;
                } else if (chatId != 0) {
                    object.dialogId = -chatId;
                }
                object.thumb = object.imageReceiver.getBitmapSafe();
                object.size = -1;
                object.radius = avatarImage.getImageReceiver().getRoundRadius();
                object.scale = avatarContainer.getScaleX();
                object.canEdit = userId == getUserConfig().clientUserId;
                return object;
            }
            return null;
        }

        @Override
        public void willHidePhotoViewer() {
            avatarImage.getImageReceiver().setVisible(true, true);
        }

        @Override
        public void openPhotoForEdit(String file, String thumb, boolean isVideo) {
            imageUpdater.openPhotoForEdit(file, thumb, 0, isVideo);
        }
    };
    private boolean fragmentOpened;
    private NestedFrameLayout contentView;
    private float titleAnimationsYDiff;
    private float customAvatarProgress;
    private float customPhotoOffset;
    private boolean hasFallbackPhoto;
    private boolean hasCustomPhoto;
    private ImageReceiver fallbackImage;

    public int getTopicId() {
        return topicId;
    }

    public static class AvatarImageView extends BackupImageView {

        private final RectF rect = new RectF();
        private final Paint placeholderPaint;

        private ImageReceiver foregroundImageReceiver;
        private float foregroundAlpha;
        private ImageReceiver.BitmapHolder drawableHolder;
        boolean drawForeground = true;

        ProfileGalleryView avatarsViewPager;

        public void setAvatarsViewPager(ProfileGalleryView avatarsViewPager) {
            this.avatarsViewPager = avatarsViewPager;
        }

        public AvatarImageView(Context context) {
            super(context);
            foregroundImageReceiver = new ImageReceiver(this);
            placeholderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            placeholderPaint.setColor(Color.BLACK);
        }

        public void setForegroundImage(ImageLocation imageLocation, String imageFilter, Drawable thumb) {
            foregroundImageReceiver.setImage(imageLocation, imageFilter, thumb, 0, null, null, 0);
            if (drawableHolder != null) {
                drawableHolder.release();
                drawableHolder = null;
            }
        }

        public void setForegroundImageDrawable(ImageReceiver.BitmapHolder holder) {
            if (holder != null) {
                foregroundImageReceiver.setImageBitmap(holder.drawable);
            }
            if (drawableHolder != null) {
                drawableHolder.release();
                drawableHolder = null;
            }
            drawableHolder = holder;
        }

        public float getForegroundAlpha() {
            return foregroundAlpha;
        }

        public void setForegroundAlpha(float value) {
            foregroundAlpha = value;
            invalidate();
        }

        public void clearForeground() {
            AnimatedFileDrawable drawable = foregroundImageReceiver.getAnimation();
            if (drawable != null) {
                drawable.removeSecondParentView(this);
            }
            foregroundImageReceiver.clearImage();
            if (drawableHolder != null) {
                drawableHolder.release();
                drawableHolder = null;
            }
            foregroundAlpha = 0f;
            invalidate();
        }

        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            foregroundImageReceiver.onDetachedFromWindow();
            if (drawableHolder != null) {
                drawableHolder.release();
                drawableHolder = null;
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            foregroundImageReceiver.onAttachedToWindow();
        }

        @Override
        public void setRoundRadius(int value) {
            super.setRoundRadius(value);
            foregroundImageReceiver.setRoundRadius(value);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            ImageReceiver imageReceiver = animatedEmojiDrawable != null ? animatedEmojiDrawable.getImageReceiver() : this.imageReceiver;
            if (imageReceiver != null && (foregroundAlpha < 1f || !drawForeground)) {
                imageReceiver.setImageCoords(0, 0, getMeasuredWidth(), getMeasuredHeight());
                imageReceiver.draw(canvas);
            }
            if (foregroundAlpha > 0f && drawForeground) {
                if (foregroundImageReceiver.getDrawable() != null) {
                    foregroundImageReceiver.setImageCoords(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    foregroundImageReceiver.setAlpha(foregroundAlpha);
                    foregroundImageReceiver.draw(canvas);
                } else {
                    rect.set(0f, 0f, getMeasuredWidth(), getMeasuredHeight());
                    placeholderPaint.setAlpha((int) (foregroundAlpha * 255f));
                    final int radius = foregroundImageReceiver.getRoundRadius()[0];
                    canvas.drawRoundRect(rect, radius, radius, placeholderPaint);
                }
            }
        }

        @Override
        public void invalidate() {
            super.invalidate();
            if (avatarsViewPager != null) {
                avatarsViewPager.invalidate();
            }
        }

        public void drawForeground(boolean drawForeground) {
            this.drawForeground = drawForeground;
        }
    }

    private class TopView extends View {

        private int currentColor;
        private Paint paint = new Paint();

        public TopView(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(widthMeasureSpec) + AndroidUtilities.dp(3));
        }

        @Override
        public void setBackgroundColor(int color) {
            if (color != currentColor) {
                currentColor = color;
                paint.setColor(color);
                invalidate();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            final int height = ActionBar.getCurrentActionBarHeight() + (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
            final float v = extraHeight + height + searchTransitionOffset;

            int y1 = (int) (v * (1.0f - mediaHeaderAnimationProgress));

            if (y1 != 0) {
                if (previousTransitionFragment != null && previousTransitionFragment.getContentView() != null) {
                    AndroidUtilities.rectTmp2.set(0, 0, getMeasuredWidth(), y1);
                    if (previousTransitionFragment.getActionBar() != null && !previousTransitionFragment.getContentView().blurWasDrawn() && previousTransitionFragment.getActionBar().getBackground() == null) {
                        paint.setColor(Theme.getColor(Theme.key_actionBarDefault, previousTransitionFragment.getResourceProvider()));
                        canvas.drawRect(AndroidUtilities.rectTmp2, paint);
                    } else {
                        previousTransitionFragment.getContentView().drawBlurRect(canvas, getY(), AndroidUtilities.rectTmp2, previousTransitionFragment.getActionBar().blurScrimPaint, true);
                    }
                }
                paint.setColor(currentColor);
                canvas.drawRect(0, 0, getMeasuredWidth(), y1, paint);
                if (previousTransitionFragment != null) {
                    ActionBar actionBar = previousTransitionFragment.getActionBar();
                    ActionBarMenu menu = actionBar.menu;
                    int restoreCount = canvas.save();
                    canvas.translate(actionBar.getX() + menu.getX(), actionBar.getY() + menu.getY());
                    canvas.saveLayerAlpha(0, 0, menu.getMeasuredWidth(), menu.getMeasuredHeight(), (int) (255 * (1f - avatarAnimationProgress)), Canvas.ALL_SAVE_FLAG);
                    menu.draw(canvas);
                    canvas.restoreToCount(restoreCount);
                }
            }
            if (y1 != v) {
                int color = getThemedColor(Theme.key_windowBackgroundWhite);
                paint.setColor(color);
                AndroidUtilities.rectTmp2.set(0, y1, getMeasuredWidth(), (int) v);
                contentView.drawBlurRect(canvas, getY(), AndroidUtilities.rectTmp2, paint, true);
            }

            if (parentLayout != null) {
                parentLayout.drawHeaderShadow(canvas, (int) (headerShadowAlpha * 255), (int) v);
            }
        }
    }

    private class OverlaysView extends View implements ProfileGalleryView.Callback {

        private final int statusBarHeight = actionBar.getOccupyStatusBar() && !inBubbleMode ? AndroidUtilities.statusBarHeight : 0;

        private final Rect topOverlayRect = new Rect();
        private final Rect bottomOverlayRect = new Rect();
        private final RectF rect = new RectF();

        private final GradientDrawable topOverlayGradient;
        private final GradientDrawable bottomOverlayGradient;
        private final ValueAnimator animator;
        private final float[] animatorValues = new float[]{0f, 1f};
        private final Paint backgroundPaint;
        private final Paint barPaint;
        private final Paint selectedBarPaint;

        private final GradientDrawable[] pressedOverlayGradient = new GradientDrawable[2];
        private final boolean[] pressedOverlayVisible = new boolean[2];
        private final float[] pressedOverlayAlpha = new float[2];

        private boolean isOverlaysVisible;
        private float currentAnimationValue;
        private float alpha = 0f;
        private float[] alphas = null;
        private long lastTime;
        private float previousSelectedProgress;
        private int previousSelectedPotision = -1;
        private float currentProgress;
        private int selectedPosition;

        private float currentLoadingAnimationProgress;
        private int currentLoadingAnimationDirection = 1;

        public OverlaysView(Context context) {
            super(context);
            setVisibility(GONE);

            barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            barPaint.setColor(0x55ffffff);
            selectedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            selectedBarPaint.setColor(0xffffffff);

            topOverlayGradient = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0x42000000, 0});
            topOverlayGradient.setShape(GradientDrawable.RECTANGLE);

            bottomOverlayGradient = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0x42000000, 0});
            bottomOverlayGradient.setShape(GradientDrawable.RECTANGLE);

            for (int i = 0; i < 2; i++) {
                final GradientDrawable.Orientation orientation = i == 0 ? GradientDrawable.Orientation.LEFT_RIGHT : GradientDrawable.Orientation.RIGHT_LEFT;
                pressedOverlayGradient[i] = new GradientDrawable(orientation, new int[]{0x32000000, 0});
                pressedOverlayGradient[i].setShape(GradientDrawable.RECTANGLE);
            }

            backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            backgroundPaint.setColor(Color.BLACK);
            backgroundPaint.setAlpha(66);
            animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(250);
            animator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            animator.addUpdateListener(anim -> {
                float value = AndroidUtilities.lerp(animatorValues, currentAnimationValue = anim.getAnimatedFraction());
                setAlphaValue(value, true);
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!isOverlaysVisible) {
                        setVisibility(GONE);
                    }
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    setVisibility(VISIBLE);
                }
            });
        }

        public void saveCurrentPageProgress() {
            previousSelectedProgress = currentProgress;
            previousSelectedPotision = selectedPosition;
            currentLoadingAnimationProgress = 0.0f;
            currentLoadingAnimationDirection = 1;
        }

        public void setAlphaValue(float value, boolean self) {
            if (Build.VERSION.SDK_INT > 18) {
                int alpha = (int) (255 * value);
                topOverlayGradient.setAlpha(alpha);
                bottomOverlayGradient.setAlpha(alpha);
                backgroundPaint.setAlpha((int) (66 * value));
                barPaint.setAlpha((int) (0x55 * value));
                selectedBarPaint.setAlpha(alpha);
                this.alpha = value;
            } else {
                setAlpha(value);
            }
            if (!self) {
                currentAnimationValue = value;
            }
            invalidate();
        }

        public boolean isOverlaysVisible() {
            return isOverlaysVisible;
        }

        public void setOverlaysVisible() {
            isOverlaysVisible = true;
            setVisibility(VISIBLE);
        }

        public void setOverlaysVisible(boolean overlaysVisible, float durationFactor) {
            if (overlaysVisible != isOverlaysVisible) {
                isOverlaysVisible = overlaysVisible;
                animator.cancel();
                final float value = AndroidUtilities.lerp(animatorValues, currentAnimationValue);
                if (overlaysVisible) {
                    animator.setDuration((long) ((1f - value) * 250f / durationFactor));
                } else {
                    animator.setDuration((long) (value * 250f / durationFactor));
                }
                animatorValues[0] = value;
                animatorValues[1] = overlaysVisible ? 1f : 0f;
                animator.start();
            }
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            final int actionBarHeight = statusBarHeight + ActionBar.getCurrentActionBarHeight();
            final float k = 0.5f;
            topOverlayRect.set(0, 0, w, (int) (actionBarHeight * k));
            bottomOverlayRect.set(0, (int) (h - AndroidUtilities.dp(72f) * k), w, h);
            topOverlayGradient.setBounds(0, topOverlayRect.bottom, w, actionBarHeight + AndroidUtilities.dp(16f));
            bottomOverlayGradient.setBounds(0, h - AndroidUtilities.dp(72f) - AndroidUtilities.dp(24f), w, bottomOverlayRect.top);
            pressedOverlayGradient[0].setBounds(0, 0, w / 5, h);
            pressedOverlayGradient[1].setBounds(w - (w / 5), 0, w, h);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            for (int i = 0; i < 2; i++) {
                if (pressedOverlayAlpha[i] > 0f) {
                    pressedOverlayGradient[i].setAlpha((int) (pressedOverlayAlpha[i] * 255));
                    pressedOverlayGradient[i].draw(canvas);
                }
            }

            topOverlayGradient.draw(canvas);
            bottomOverlayGradient.draw(canvas);
            canvas.drawRect(topOverlayRect, backgroundPaint);
            canvas.drawRect(bottomOverlayRect, backgroundPaint);

            int count = avatarsViewPager.getRealCount();
            selectedPosition = avatarsViewPager.getRealPosition();

            if (alphas == null || alphas.length != count) {
                alphas = new float[count];
                Arrays.fill(alphas, 0.0f);
            }

            boolean invalidate = false;

            long newTime = SystemClock.elapsedRealtime();
            long dt = (newTime - lastTime);
            if (dt < 0 || dt > 20) {
                dt = 17;
            }
            lastTime = newTime;

            if (count > 1 && count <= 20) {
                if (overlayCountVisible == 0) {
                    alpha = 0.0f;
                    overlayCountVisible = 3;
                } else if (overlayCountVisible == 1) {
                    alpha = 0.0f;
                    overlayCountVisible = 2;
                }
                if (overlayCountVisible == 2) {
                    barPaint.setAlpha((int) (0x55 * alpha));
                    selectedBarPaint.setAlpha((int) (0xff * alpha));
                }
                int width = (getMeasuredWidth() - AndroidUtilities.dp(5 * 2) - AndroidUtilities.dp(2 * (count - 1))) / count;
                int y = AndroidUtilities.dp(4) + (Build.VERSION.SDK_INT >= 21 && !inBubbleMode ? AndroidUtilities.statusBarHeight : 0);
                for (int a = 0; a < count; a++) {
                    int x = AndroidUtilities.dp(5 + a * 2) + width * a;
                    float progress;
                    int baseAlpha = 0x55;
                    if (a == previousSelectedPotision && Math.abs(previousSelectedProgress - 1.0f) > 0.0001f) {
                        progress = previousSelectedProgress;
                        canvas.save();
                        canvas.clipRect(x + width * progress, y, x + width, y + AndroidUtilities.dp(2));
                        rect.set(x, y, x + width, y + AndroidUtilities.dp(2));
                        barPaint.setAlpha((int) (0x55 * alpha));
                        canvas.drawRoundRect(rect, AndroidUtilities.dp(1), AndroidUtilities.dp(1), barPaint);
                        baseAlpha = 0x50;
                        canvas.restore();
                        invalidate = true;
                    } else if (a == selectedPosition) {
                        if (avatarsViewPager.isCurrentItemVideo()) {
                            progress = currentProgress = avatarsViewPager.getCurrentItemProgress();
                            if (progress <= 0 && avatarsViewPager.isLoadingCurrentVideo() || currentLoadingAnimationProgress > 0.0f) {
                                currentLoadingAnimationProgress += currentLoadingAnimationDirection * dt / 500.0f;
                                if (currentLoadingAnimationProgress > 1.0f) {
                                    currentLoadingAnimationProgress = 1.0f;
                                    currentLoadingAnimationDirection *= -1;
                                } else if (currentLoadingAnimationProgress <= 0) {
                                    currentLoadingAnimationProgress = 0.0f;
                                    currentLoadingAnimationDirection *= -1;
                                }
                            }
                            rect.set(x, y, x + width, y + AndroidUtilities.dp(2));
                            barPaint.setAlpha((int) ((0x55 + 0x30 * currentLoadingAnimationProgress) * alpha));
                            canvas.drawRoundRect(rect, AndroidUtilities.dp(1), AndroidUtilities.dp(1), barPaint);
                            invalidate = true;
                            baseAlpha = 0x50;
                        } else {
                            progress = currentProgress = 1.0f;
                        }
                    } else {
                        progress = 1.0f;
                    }
                    rect.set(x, y, x + width * progress, y + AndroidUtilities.dp(2));

                    if (a != selectedPosition) {
                        if (overlayCountVisible == 3) {
                            barPaint.setAlpha((int) (AndroidUtilities.lerp(baseAlpha, 0xff, CubicBezierInterpolator.EASE_BOTH.getInterpolation(alphas[a])) * alpha));
                        }
                    } else {
                        alphas[a] = 0.75f;
                    }

                    canvas.drawRoundRect(rect, AndroidUtilities.dp(1), AndroidUtilities.dp(1), a == selectedPosition ? selectedBarPaint : barPaint);
                }

                if (overlayCountVisible == 2) {
                    if (alpha < 1.0f) {
                        alpha += dt / 180.0f;
                        if (alpha > 1.0f) {
                            alpha = 1.0f;
                        }
                        invalidate = true;
                    } else {
                        overlayCountVisible = 3;
                    }
                } else if (overlayCountVisible == 3) {
                    for (int i = 0; i < alphas.length; i++) {
                        if (i != selectedPosition && alphas[i] > 0.0f) {
                            alphas[i] -= dt / 500.0f;
                            if (alphas[i] <= 0.0f) {
                                alphas[i] = 0.0f;
                                if (i == previousSelectedPotision) {
                                    previousSelectedPotision = -1;
                                }
                            }
                            invalidate = true;
                        } else if (i == previousSelectedPotision) {
                            previousSelectedPotision = -1;
                        }
                    }
                }
            }

            for (int i = 0; i < 2; i++) {
                if (pressedOverlayVisible[i]) {
                    if (pressedOverlayAlpha[i] < 1f) {
                        pressedOverlayAlpha[i] += dt / 180.0f;
                        if (pressedOverlayAlpha[i] > 1f) {
                            pressedOverlayAlpha[i] = 1f;
                        }
                        invalidate = true;
                    }
                } else {
                    if (pressedOverlayAlpha[i] > 0f) {
                        pressedOverlayAlpha[i] -= dt / 180.0f;
                        if (pressedOverlayAlpha[i] < 0f) {
                            pressedOverlayAlpha[i] = 0f;
                        }
                        invalidate = true;
                    }
                }
            }

            if (invalidate) {
                postInvalidateOnAnimation();
            }
        }

        @Override
        public void onDown(boolean left) {
            pressedOverlayVisible[left ? 0 : 1] = true;
            postInvalidateOnAnimation();
        }

        @Override
        public void onRelease() {
            Arrays.fill(pressedOverlayVisible, false);
            postInvalidateOnAnimation();
        }

        @Override
        public void onClick() {
            if (imageUpdater != null) {
                TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(UserConfig.getInstance(currentAccount).getClientUserId());
                if (user == null) {
                    user = UserConfig.getInstance(currentAccount).getCurrentUser();
                }
                if (user == null) {
                    return;
                }
                imageUpdater.openMenu(
                        user.photo != null && user.photo.photo_big != null && !(user.photo instanceof TLRPC.TL_userProfilePhotoEmpty),
                        () -> MessagesController.getInstance(currentAccount).deleteUserPhoto(null), dialog -> {},
                        ImageUpdater.TYPE_DEFAULT);
            } else {
                openAvatar();
            }
        }

        @Override
        public void onPhotosLoaded() {
            updateProfileData(false);
        }

        @Override
        public void onVideoSet() {
            invalidate();
        }
    }

    private class NestedFrameLayout extends SizeNotifierFrameLayout implements NestedScrollingParent3 {

        private NestedScrollingParentHelper nestedScrollingParentHelper;

        public NestedFrameLayout(Context context) {
            super(context);
            nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        }

        @Override
        public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, int[] consumed) {
            try {
                if (target == listView && sharedMediaLayoutAttached) {
                    RecyclerListView innerListView = sharedMediaLayout.getCurrentListView();
                    int top = sharedMediaLayout.getTop();
                    if (top == 0) {
                        consumed[1] = dyUnconsumed;
                        innerListView.scrollBy(0, dyUnconsumed);
                    }
                }
            } catch (Throwable e) {
                FileLog.e(e);
                AndroidUtilities.runOnUIThread(() -> {
                    try {
                        RecyclerListView innerListView = sharedMediaLayout.getCurrentListView();
                        if (innerListView != null && innerListView.getAdapter() != null) {
                            innerListView.getAdapter().notifyDataSetChanged();
                        }
                    } catch (Throwable e2) {

                    }
                });
            }
        }

        @Override
        public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {

        }

        @Override
        public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
            return super.onNestedPreFling(target, velocityX, velocityY);
        }

        @Override
        public void onNestedPreScroll(View target, int dx, int dy, int[] consumed, int type) {
            if (target == listView && sharedMediaRow != -1 && sharedMediaLayoutAttached) {
                boolean searchVisible = actionBar.isSearchFieldVisible();
                int t = sharedMediaLayout.getTop();
                if (dy < 0) {
                    boolean scrolledInner = false;
                    if (t <= 0) {
                        RecyclerListView innerListView = sharedMediaLayout.getCurrentListView();
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) innerListView.getLayoutManager();
                        int pos = linearLayoutManager.findFirstVisibleItemPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            RecyclerView.ViewHolder holder = innerListView.findViewHolderForAdapterPosition(pos);
                            int top = holder != null ? holder.itemView.getTop() : -1;
                            int paddingTop = innerListView.getPaddingTop();
                            if (top != paddingTop || pos != 0) {
                                consumed[1] = pos != 0 ? dy : Math.max(dy, (top - paddingTop));
                                innerListView.scrollBy(0, dy);
                                scrolledInner = true;
                            }
                        }
                    }
                    if (searchVisible) {
                        if (!scrolledInner && t < 0) {
                            consumed[1] = dy - Math.max(t, dy);
                        } else {
                            consumed[1] = dy;
                        }
                    }
                } else {
                    if (searchVisible) {
                        RecyclerListView innerListView = sharedMediaLayout.getCurrentListView();
                        consumed[1] = dy;
                        if (t > 0) {
                            consumed[1] -= dy;
                        }
                        if (consumed[1] > 0) {
                            innerListView.scrollBy(0, consumed[1]);
                        }
                    }
                }
            }
        }

        @Override
        public boolean onStartNestedScroll(View child, View target, int axes, int type) {
            return sharedMediaRow != -1 && axes == ViewCompat.SCROLL_AXIS_VERTICAL;
        }

        @Override
        public void onNestedScrollAccepted(View child, View target, int axes, int type) {
            nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        }

        @Override
        public void onStopNestedScroll(View target, int type) {
            nestedScrollingParentHelper.onStopNestedScroll(target);
        }

        @Override
        public void onStopNestedScroll(View child) {

        }

        @Override
        protected void drawList(Canvas blurCanvas, boolean top) {
            super.drawList(blurCanvas, top);
            blurCanvas.save();
            blurCanvas.translate(0, listView.getY());
            sharedMediaLayout.drawListForBlur(blurCanvas);
            blurCanvas.restore();
        }
    }

    private class PagerIndicatorView extends View {

        private final RectF indicatorRect = new RectF();

        private final TextPaint textPaint;
        private final Paint backgroundPaint;

        private final ValueAnimator animator;
        private final float[] animatorValues = new float[]{0f, 1f};

        private final PagerAdapter adapter = avatarsViewPager.getAdapter();

        private boolean isIndicatorVisible;

        public PagerIndicatorView(Context context) {
            super(context);
            setVisibility(GONE);

            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(Color.WHITE);
            textPaint.setTypeface(Typeface.SANS_SERIF);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(AndroidUtilities.dpf2(15f));
            backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            backgroundPaint.setColor(0x26000000);
            animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            animator.addUpdateListener(a -> {
                final float value = AndroidUtilities.lerp(animatorValues, a.getAnimatedFraction());
                if (searchItem != null && !isPulledDown) {
                    searchItem.setScaleX(1f - value);
                    searchItem.setScaleY(1f - value);
                    searchItem.setAlpha(1f - value);
                }
                if (editItemVisible) {
                    editItem.setScaleX(1f - value);
                    editItem.setScaleY(1f - value);
                    editItem.setAlpha(1f - value);
                }
                if (callItemVisible) {
                    callItem.setScaleX(1f - value);
                    callItem.setScaleY(1f - value);
                    callItem.setAlpha(1f - value);
                }
                if (videoCallItemVisible) {
                    videoCallItem.setScaleX(1f - value);
                    videoCallItem.setScaleY(1f - value);
                    videoCallItem.setAlpha(1f - value);
                }
                setScaleX(value);
                setScaleY(value);
                setAlpha(value);
            });
            boolean expanded = expandPhoto;
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (isIndicatorVisible) {
                        if (searchItem != null) {
                            searchItem.setClickable(false);
                        }
                        if (editItemVisible) {
                            editItem.setVisibility(GONE);
                        }
                        if (callItemVisible) {
                            callItem.setVisibility(GONE);
                        }
                        if (videoCallItemVisible) {
                            videoCallItem.setVisibility(GONE);
                        }
                    } else {
                        setVisibility(GONE);
                    }
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    if (searchItem != null && !expanded) {
                        searchItem.setClickable(true);
                    }
                    if (editItemVisible) {
                        editItem.setVisibility(VISIBLE);
                    }
                    if (callItemVisible) {
                        callItem.setVisibility(VISIBLE);
                    }
                    if (videoCallItemVisible) {
                        videoCallItem.setVisibility(VISIBLE);
                    }
                    setVisibility(VISIBLE);
                }
            });
            avatarsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                private int prevPage;

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    int realPosition = avatarsViewPager.getRealPosition(position);
                    invalidateIndicatorRect(prevPage != realPosition);
                    prevPage = realPosition;
                    updateAvatarItems();
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            adapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    int count = avatarsViewPager.getRealCount();
                    if (overlayCountVisible == 0 && count > 1 && count <= 20 && overlaysView.isOverlaysVisible()) {
                        overlayCountVisible = 1;
                    }
                    invalidateIndicatorRect(false);
                    refreshVisibility(1f);
                    updateAvatarItems();
                }
            });
        }

        private void updateAvatarItemsInternal() {
            /*if (otherItem == null || avatarsViewPager == null) {
                return;
            }
            if (isPulledDown) {
                int position = avatarsViewPager.getRealPosition();
                if (position == 0) {
                    otherItem.hideSubItem(set_as_main);
                    otherItem.showSubItem(add_photo);
                } else {
                    otherItem.showSubItem(set_as_main);
                    otherItem.hideSubItem(add_photo);
                }
            }*/
        }

        private void updateAvatarItems() {
            if (imageUpdater == null) {
                return;
            }
            if (otherItem.isSubMenuShowing()) {
                AndroidUtilities.runOnUIThread(this::updateAvatarItemsInternal, 500);
            } else {
                updateAvatarItemsInternal();
            }
        }

        public boolean isIndicatorVisible() {
            return isIndicatorVisible;
        }

        public boolean isIndicatorFullyVisible() {
            return isIndicatorVisible && !animator.isRunning();
        }

        public void setIndicatorVisible(boolean indicatorVisible, float durationFactor) {
            if (indicatorVisible != isIndicatorVisible) {
                isIndicatorVisible = indicatorVisible;
                animator.cancel();
                final float value = AndroidUtilities.lerp(animatorValues, animator.getAnimatedFraction());
                if (durationFactor <= 0f) {
                    animator.setDuration(0);
                } else if (indicatorVisible) {
                    animator.setDuration((long) ((1f - value) * 250f / durationFactor));
                } else {
                    animator.setDuration((long) (value * 250f / durationFactor));
                }
                animatorValues[0] = value;
                animatorValues[1] = indicatorVisible ? 1f : 0f;
                animator.start();
            }
        }

        public void refreshVisibility(float durationFactor) {
            setIndicatorVisible(isPulledDown && avatarsViewPager.getRealCount() > 20, durationFactor);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            invalidateIndicatorRect(false);
        }

        private void invalidateIndicatorRect(boolean pageChanged) {
            if (pageChanged) {
                overlaysView.saveCurrentPageProgress();
            }
            overlaysView.invalidate();
            final float textWidth = textPaint.measureText(getCurrentTitle());
            indicatorRect.right = getMeasuredWidth() - AndroidUtilities.dp(54f);
            indicatorRect.left = indicatorRect.right - (textWidth + AndroidUtilities.dpf2(16f));
            indicatorRect.top = (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.dp(15f);
            indicatorRect.bottom = indicatorRect.top + AndroidUtilities.dp(26);
            setPivotX(indicatorRect.centerX());
            setPivotY(indicatorRect.centerY());
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            final float radius = AndroidUtilities.dpf2(12);
            canvas.drawRoundRect(indicatorRect, radius, radius, backgroundPaint);
            canvas.drawText(getCurrentTitle(), indicatorRect.centerX(), indicatorRect.top + AndroidUtilities.dpf2(18.5f), textPaint);
        }

        private String getCurrentTitle() {
            return adapter.getPageTitle(avatarsViewPager.getCurrentItem()).toString();
        }

        private ActionBarMenuItem getSecondaryMenuItem() {
            if (callItemVisible) {
                return callItem;
            } else if (editItemVisible) {
                return editItem;
            } else if (searchItem != null) {
                return searchItem;
            } else {
                return null;
            }
        }
    }

    public ProfileActivity(Bundle args) {
        this(args, null);
    }

    public ProfileActivity(Bundle args, SharedMediaLayout.SharedMediaPreloader preloader) {
        super(args);
        sharedMediaPreloader = preloader;
    }

    @Override
    public boolean onFragmentCreate() {
        userId = arguments.getLong("user_id", 0);
        chatId = arguments.getLong("chat_id", 0);
        topicId = arguments.getInt("topic_id", 0);
        isTopic = topicId != 0;
        banFromGroup = arguments.getLong("ban_chat_id", 0);
        reportReactionMessageId = arguments.getInt("report_reaction_message_id", 0);
        reportReactionFromDialogId = arguments.getLong("report_reaction_from_dialog_id", 0);
        showAddToContacts = arguments.getBoolean("show_add_to_contacts");
        vcardPhone = PhoneFormat.stripExceptNumbers(arguments.getString("vcard_phone"));
        vcardFirstName = arguments.getString("vcard_first_name");
        vcardLastName = arguments.getString("vcard_last_name");
        reportSpam = arguments.getBoolean("reportSpam", false);
        if (!expandPhoto) {
            expandPhoto = arguments.getBoolean("expandPhoto", false);
            if (expandPhoto) {
                currentExpandAnimatorValue = 1f;
                needSendMessage = true;
            }
        }
        if (userId != 0) {
            dialogId = arguments.getLong("dialog_id", 0);
            if (dialogId != 0) {
                currentEncryptedChat = getMessagesController().getEncryptedChat(DialogObject.getEncryptedChatId(dialogId));
            }
            TLRPC.User user = getMessagesController().getUser(userId);
            if (user == null) {
                return false;
            }

            getNotificationCenter().addObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.newSuggestionsAvailable);
            getNotificationCenter().addObserver(this, NotificationCenter.encryptedChatCreated);
            getNotificationCenter().addObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.blockedUsersDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.botInfoDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.userInfoDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.privacyRulesUpdated);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.reloadInterface);

            userBlocked = getMessagesController().blockePeers.indexOfKey(userId) >= 0;
            if (user.bot) {
                isBot = true;
                getMediaDataController().loadBotInfo(user.id, user.id, true, classGuid);
            }
            userInfo = getMessagesController().getUserFull(userId);
            getMessagesController().loadFullUser(getMessagesController().getUser(userId), classGuid, true);
            participantsMap = null;

            if (UserObject.isUserSelf(user)) {
                imageUpdater = new ImageUpdater(true, ImageUpdater.FOR_TYPE_USER, true);
                imageUpdater.setOpenWithFrontfaceCamera(true);
                imageUpdater.parentFragment = this;
                imageUpdater.setDelegate(this);
                getMediaDataController().checkFeaturedStickers();
                getMessagesController().loadSuggestedFilters();
                getMessagesController().loadUserInfo(getUserConfig().getCurrentUser(), true, classGuid);
            }
            actionBarAnimationColorFrom = arguments.getInt("actionBarColor", 0);
        } else if (chatId != 0) {
            currentChat = getMessagesController().getChat(chatId);
            if (currentChat == null) {
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                getMessagesStorage().getStorageQueue().postRunnable(() -> {
                    currentChat = getMessagesStorage().getChat(chatId);
                    countDownLatch.countDown();
                });
                try {
                    countDownLatch.await();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (currentChat != null) {
                    getMessagesController().putChat(currentChat, true);
                } else {
                    return false;
                }
            }

            if (currentChat.megagroup) {
                getChannelParticipants(true);
            } else {
                participantsMap = null;
            }
            getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.chatOnlineCountDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.groupCallUpdated);

            sortedUsers = new ArrayList<>();
            updateOnlineCount(true);
            if (chatInfo == null) {
                chatInfo = getMessagesController().getChatFull(chatId);
            }
            if (ChatObject.isChannel(currentChat)) {
                getMessagesController().loadFullChat(chatId, classGuid, true);
            } else if (chatInfo == null) {
                chatInfo = getMessagesStorage().loadChatInfo(chatId, false, null, false, false);
            }

            updateExceptions();
        } else {
            return false;
        }
        if (sharedMediaPreloader == null) {
            sharedMediaPreloader = new SharedMediaLayout.SharedMediaPreloader(this);
        }
        sharedMediaPreloader.addDelegate(this);

        getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().addObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().addObserver(this, NotificationCenter.topicsDidLoaded);
        getNotificationCenter().addObserver(this, NotificationCenter.updateSearchSettings);
        getNotificationCenter().addObserver(this, NotificationCenter.reloadDialogPhotos);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        updateRowsIds();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }

        if (arguments.containsKey("preload_messages")) {
            getMessagesController().ensureMessagesLoaded(userId, 0, null);
        }

        if (userId != 0) {
            TLRPC.User user = getMessagesController().getUser(userId);

            if (UserObject.isUserSelf(user)) {
                TLRPC.TL_account_getPassword req = new TLRPC.TL_account_getPassword();
                getConnectionsManager().sendRequest(req, (response, error) -> {
                    if (response instanceof TLRPC.TL_account_password) {
                        currentPassword = (TLRPC.TL_account_password) response;
                    }
                });
            }
        }
        return true;
    }

    private void updateExceptions() {
        if (!isTopic && ChatObject.isForum(currentChat)) {
            getNotificationsController().loadTopicsNotificationsExceptions(-chatId, (topics) -> {
                ArrayList<Integer> arrayList = new ArrayList<>(topics);
                for (int i = 0; i < arrayList.size(); i++) {
                    if (getMessagesController().getTopicsController().findTopic(chatId, arrayList.get(i)) == null) {
                        arrayList.remove(i);
                        i--;
                    }
                }
                notificationsExceptionTopics.clear();
                notificationsExceptionTopics.addAll(arrayList);

                if (notificationsRow >= 0 && listAdapter != null) {
                    listAdapter.notifyItemChanged(notificationsRow);
                }
            });
        }
    }

    @Override
    public boolean isActionBarCrossfadeEnabled() {
        return !isPulledDown;
    }

    private void generateLink() {
        TLRPC.TL_messages_exportChatInvite req = new TLRPC.TL_messages_exportChatInvite();
        req.peer = getMessagesController().getInputPeer(-chatId);
        final int reqId = getConnectionsManager().sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
            if (error == null) {
                TLRPC.TL_chatInviteExported invite = (TLRPC.TL_chatInviteExported) response;
                ProxyUtil.showQrDialog(getParentActivity(), invite.link, imageSize -> Bitmap.createScaledBitmap(avatarImage.getImageReceiver().getBitmap(), imageSize, imageSize, true));
            } else {
                AlertUtil.showToast(error);
            }
        }));
        getConnectionsManager().bindRequestToGuid(reqId, classGuid);
    }

    //    @Override
    protected void setParentLayout(ActionBarLayout layout) {
        super.setParentLayout(layout);
        Activity activity = getParentActivity();
        if (activity != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            navigationBarAnimationColorFrom = activity.getWindow().getNavigationBarColor();
        }
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (sharedMediaLayout != null) {
            sharedMediaLayout.onDestroy();
        }
        if (sharedMediaPreloader != null) {
            sharedMediaPreloader.onDestroy(this);
        }
        if (sharedMediaPreloader != null) {
            sharedMediaPreloader.removeDelegate(this);
        }

        getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().removeObserver(this, NotificationCenter.topicsDidLoaded);
        getNotificationCenter().removeObserver(this, NotificationCenter.updateSearchSettings);
        getNotificationCenter().removeObserver(this, NotificationCenter.reloadDialogPhotos);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        if (avatarsViewPager != null) {
            avatarsViewPager.onDestroy();
        }
        if (userId != 0) {
            getNotificationCenter().removeObserver(this, NotificationCenter.newSuggestionsAvailable);
            getNotificationCenter().removeObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.encryptedChatCreated);
            getNotificationCenter().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().removeObserver(this, NotificationCenter.blockedUsersDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.botInfoDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.userInfoDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.privacyRulesUpdated);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.reloadInterface);
            getMessagesController().cancelLoadFullUser(userId);
        } else if (chatId != 0) {
            getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.chatOnlineCountDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.groupCallUpdated);
        }
        if (avatarImage != null) {
            avatarImage.setImageDrawable(null);
        }
        if (imageUpdater != null) {
            imageUpdater.clear();
        }
        if (pinchToZoomHelper != null) {
            pinchToZoomHelper.clear();
        }
    }

    @Override
    public ActionBar createActionBar(Context context) {
        BaseFragment lastFragment = parentLayout.getLastFragment();
        if (lastFragment instanceof ChatActivity && ((ChatActivity) lastFragment).themeDelegate != null && ((ChatActivity) lastFragment).themeDelegate.getCurrentTheme() != null) {
            resourcesProvider = lastFragment.getResourceProvider();
        }
        ActionBar actionBar = new ActionBar(context, resourcesProvider) {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                avatarContainer.getHitRect(rect);
                if (rect.contains((int) event.getX(), (int) event.getY())) {
                    return false;
                }
                return super.onTouchEvent(event);
            }

            @Override
            public void setItemsColor(int color, boolean isActionMode) {
                super.setItemsColor(color, isActionMode);
                if (!isActionMode && ttlIconView != null) {
                    ttlIconView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                }
            }
        };
        actionBar.setColorFilterMode(PorterDuff.Mode.SRC_IN);
        actionBar.setForceSkipTouches(true);
        actionBar.setBackgroundColor(Color.TRANSPARENT);
        actionBar.setItemsBackgroundColor(getThemedColor(Theme.key_avatar_actionBarSelectorBlue), false);
        actionBar.setItemsColor(getThemedColor(Theme.key_actionBarDefaultIcon), false);
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setCastShadows(false);
        actionBar.setAddToContainer(false);
        actionBar.setClipContent(true);
        actionBar.setOccupyStatusBar(Build.VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet() && !inBubbleMode);
        ImageView backButton = actionBar.getBackButton();
        backButton.setOnLongClickListener(e -> {
            ActionBarPopupWindow menu = BackButtonMenu.show(this, backButton, getDialogId(), getTopicId(), resourcesProvider);
            if (menu != null) {
                menu.setOnDismissListener(() -> dimBehindView(false));
                dimBehindView(backButton, 0.3f);
                if (undoView != null) {
                    undoView.hide(true, 1);
                }
                return true;
            } else {
                return false;
            }
        });
        return actionBar;
    }

    @Override
    public View createView(Context context) {
        Theme.createProfileResources(context);
        Theme.createChatResources(context, false);
        BaseFragment lastFragment = parentLayout.getLastFragment();
        if (lastFragment instanceof ChatActivity && ((ChatActivity) lastFragment).themeDelegate != null && ((ChatActivity) lastFragment).themeDelegate.getCurrentTheme() != null) {
            resourcesProvider = lastFragment.getResourceProvider();
        }
        searchTransitionOffset = 0;
        searchTransitionProgress = 1f;
        searchMode = false;
        hasOwnBackground = true;
        extraHeight = AndroidUtilities.dp(88f);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(final int id) {
                if (getParentActivity() == null) {
                    return;
                }
                if (id == -1) {
                    finishFragment();
                } else if (id == block_contact) {
                    TLRPC.User user = getMessagesController().getUser(userId);
                    if (user == null) {
                        return;
                    }
                    if (!isBot || MessagesController.isSupportUser(user)) {
                        if (userBlocked) {
                            getMessagesController().unblockPeer(userId);
                            if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                                BulletinFactory.createBanBulletin(ProfileActivity.this, false).show();
                            }
                        } else {
                            if (reportSpam) {
                                AlertsCreator.showBlockReportSpamAlert(ProfileActivity.this, userId, user, null, currentEncryptedChat, false, null, param -> {
                                    if (param == 1) {
                                        getNotificationCenter().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                                        getNotificationCenter().postNotificationName(NotificationCenter.closeChats);
                                        playProfileAnimation = 0;
                                        finishFragment();
                                    } else {
                                        getNotificationCenter().postNotificationName(NotificationCenter.peerSettingsDidLoad, userId);
                                    }
                                }, resourcesProvider);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), resourcesProvider);
                                builder.setTitle(LocaleController.getString("BlockUser", R.string.BlockUser));
                                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureBlockContact2", R.string.AreYouSureBlockContact2, ContactsController.formatName(user.first_name, user.last_name))));
                                builder.setPositiveButton(LocaleController.getString("BlockContact", R.string.BlockContact), (dialogInterface, i) -> {
                                    getMessagesController().blockPeer(userId);
                                    if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                                        BulletinFactory.createBanBulletin(ProfileActivity.this, true).show();
                                    }
                                });
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                AlertDialog dialog = builder.create();
                                showDialog(dialog);
                                TextView button = (TextView) dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                if (button != null) {
                                    button.setTextColor(getThemedColor(Theme.key_dialogTextRed));
                                }
                            }
                        }
                    } else {
                        if (!userBlocked) {
                            AlertsCreator.createClearOrDeleteDialogAlert(ProfileActivity.this, false, currentChat, user, currentEncryptedChat != null, true, true, (param) -> {
                                if (getParentLayout().getFragmentStack().get(getParentLayout().getFragmentStack().size() - 2) instanceof ChatActivity) {
                                    getParentLayout().removeFragmentFromStack(getParentLayout().getFragmentStack().size() - 2);
                                }
                                finishFragment();
                                getNotificationCenter().postNotificationName(NotificationCenter.needDeleteDialog, dialogId, user, currentChat, param);
                            }, getResourceProvider());
                        } else {
                            getMessagesController().unblockPeer(userId, ()-> getSendMessagesHelper().sendMessage("/start", userId, null, null, null, false, null, null, null, true, 0, null, false));
                            finishFragment();
                        }
                    }
                } else if (id == add_contact) {
                    TLRPC.User user = getMessagesController().getUser(userId);
                    Bundle args = new Bundle();
                    args.putLong("user_id", user.id);
                    args.putBoolean("addContact", true);
                    presentFragment(new ContactAddActivity(args, resourcesProvider));
                } else if (id == share_contact) {
                    Bundle args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    args.putInt("dialogsType", DialogsActivity.DIALOGS_TYPE_FORWARD);
                    args.putString("selectAlertString", LocaleController.getString("SendContactToText", R.string.SendContactToText));
                    args.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroupText", R.string.SendContactToGroupText));
                    DialogsActivity fragment = new DialogsActivity(args);
                    fragment.setDelegate(ProfileActivity.this);
                    presentFragment(fragment);
                } else if (id == edit_contact) {
                    Bundle args = new Bundle();
                    args.putLong("user_id", userId);
                    presentFragment(new ContactAddActivity(args, resourcesProvider));
                } else if (id == delete_contact) {
                    final TLRPC.User user = getMessagesController().getUser(userId);
                    if (user == null || getParentActivity() == null) {
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), resourcesProvider);
                    builder.setTitle(LocaleController.getString("DeleteContact", R.string.DeleteContact));
                    builder.setMessage(LocaleController.getString("AreYouSureDeleteContact", R.string.AreYouSureDeleteContact));
                    builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), (dialogInterface, i) -> {
                        ArrayList<TLRPC.User> arrayList = new ArrayList<>();
                        arrayList.add(user);
                        getContactsController().deleteContact(arrayList, true);
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    AlertDialog dialog = builder.create();
                    showDialog(dialog);
                    TextView button = (TextView) dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    if (button != null) {
                        button.setTextColor(getThemedColor(Theme.key_dialogTextRed));
                    }
                } else if (id == leave_group) {
                    leaveChatPressed();
                } else if (id == event_log) {
                    presentFragment(new ChannelAdminLogActivity(currentChat));
                } else if (id == aliasChannelName) {
                    setChannelAlias();
                } else if (id == delete_topic) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(LocaleController.getPluralString("DeleteTopics", 1));
                    TLRPC.TL_forumTopic topic = MessagesController.getInstance(currentAccount).getTopicsController().findTopic(chatId, topicId);
                    builder.setMessage(LocaleController.formatString("DeleteSelectedTopic", R.string.DeleteSelectedTopic, topic == null ? "topic" : topic.title));
                    builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ArrayList<Integer> topicIds = new ArrayList<>();
                            topicIds.add(topicId);
                            getMessagesController().getTopicsController().deleteTopics(chatId, topicIds);
                            playProfileAnimation = 0;
                            if (parentLayout != null && parentLayout.getFragmentStack() != null) {
                                for (int i = 0; i < parentLayout.getFragmentStack().size(); ++i) {
                                    BaseFragment fragment = parentLayout.getFragmentStack().get(i);
                                    if (fragment instanceof ChatActivity && ((ChatActivity) fragment).getTopicId() == topicId) {
                                        fragment.removeSelfFromStack();
                                    }
                                }
                            }
                            finishFragment();

                            Context context = getContext();
                            if (context != null) {
                                BulletinFactory.of(Bulletin.BulletinWindow.make(context), resourcesProvider).createSimpleBulletin(R.raw.ic_delete, LocaleController.getPluralString("TopicsDeleted", 1)).show();
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    TextView button = (TextView) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    if (button != null) {
                        button.setTextColor(Theme.getColor(Theme.key_dialogTextRed));
                    }
                } else if (id == edit_channel) {
                    if (isTopic) {
                        Bundle args = new Bundle();
                        args.putLong("chat_id", chatId);
                        TopicCreateFragment fragment = TopicCreateFragment.create(chatId, topicId);
                        presentFragment(fragment);
                    } else {
                        Bundle args = new Bundle();
                        args.putLong("chat_id", chatId);
                        ChatEditActivity fragment = new ChatEditActivity(args);
                        fragment.setInfo(chatInfo);
                        presentFragment(fragment);
                    }
                } else if (id == invite_to_group) {
                    final TLRPC.User user = getMessagesController().getUser(userId);
                    if (user == null) {
                        return;
                    }
                    Bundle args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    args.putInt("dialogsType", DialogsActivity.DIALOGS_TYPE_ADD_USERS_TO);
                    args.putBoolean("resetDelegate", false);
                    args.putBoolean("closeFragment", false);
//                    args.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupAlertText", R.string.AddToTheGroupAlertText, UserObject.getUserName(user), "%1$s"));
                    DialogsActivity fragment = new DialogsActivity(args);
                    fragment.setDelegate((fragment1, dids, message, param, topicsFragment) -> {
                        long did = dids.get(0).dialogId;

                        TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(-did);
                        if (chat != null && (chat.creator || chat.admin_rights != null && chat.admin_rights.add_admins)) {
                            getMessagesController().checkIsInChat(false, chat, user, (isInChatAlready, rightsAdmin, currentRank) -> AndroidUtilities.runOnUIThread(() -> {
                                ChatRightsEditActivity editRightsActivity = new ChatRightsEditActivity(userId, -did, rightsAdmin, null, null, currentRank, ChatRightsEditActivity.TYPE_ADD_BOT, true, !isInChatAlready, null);
                                editRightsActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
                                    @Override
                                    public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                                        disableProfileAnimation = true;
                                        fragment.removeSelfFromStack();
                                        getNotificationCenter().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                                        getNotificationCenter().postNotificationName(NotificationCenter.closeChats);
                                    }

                                    @Override
                                    public void didChangeOwner(TLRPC.User user) {
                                    }
                                });
                                presentFragment(editRightsActivity);
                            }));
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), resourcesProvider);
                            builder.setTitle(LocaleController.getString("AddBot", R.string.AddBot));
                            String chatName = chat == null ? "" : MessageHelper.INSTANCE.zalgoFilter(chat.title);
                            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", R.string.AddMembersAlertNamesText, UserObject.getUserName(user), chatName)));
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            builder.setPositiveButton(LocaleController.getString("AddBot", R.string.AddBot), (di, i) -> {
                                disableProfileAnimation = true;

                                Bundle args1 = new Bundle();
                                args1.putBoolean("scrollToTopOnResume", true);
                                args1.putLong("chat_id", -did);
                                if (!getMessagesController().checkCanOpenChat(args1, fragment1)) {
                                    return;
                                }
                                ChatActivity chatActivity = new ChatActivity(args1);
                                getNotificationCenter().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                                getNotificationCenter().postNotificationName(NotificationCenter.closeChats);
                                getMessagesController().addUserToChat(-did, user, 0, null, chatActivity, true, null, null);
                                presentFragment(chatActivity, true);
                            });
                            showDialog(builder.create());
                        }
                        return true;
                    });
                    presentFragment(fragment);
                } else if (id == share || id == qr_code) {
                    try {
                        String text = null;
                        if (userId != 0) {
                            TLRPC.User user = getMessagesController().getUser(userId);
                            if (user == null || StrUtil.isBlank(user.username)) {
                                return;
                            }
                            if (botInfo != null && userInfo != null && !TextUtils.isEmpty(userInfo.about) && id == share) {
                                text = String.format("%s https://" + getMessagesController().linkPrefix + "/%s", userInfo.about, UserObject.getPublicUsername(user));
                            } else {
                                text = String.format("https://" + getMessagesController().linkPrefix + "/%s", UserObject.getPublicUsername(user));
                            }
                        } else if (chatId != 0) {
                            TLRPC.Chat chat = getMessagesController().getChat(chatId);
                            if (chat == null) {
                                return;
                            }
                            if (chatInfo != null && !TextUtils.isEmpty(chatInfo.about) && id == share) {
                                text = String.format("%s\nhttps://" + getMessagesController().linkPrefix + "/%s", chatInfo.about, ChatObject.getPublicUsername(chat));
                            } else if (StrUtil.isNotBlank(chat.username)) {
                                text = String.format("https://" + getMessagesController().linkPrefix + "/%s", ChatObject.getPublicUsername(chat));
                            } else if (id == qr_code && ChatObject.canUserDoAdminAction(chat, ChatObject.ACTION_INVITE)) {
                                if (chatInfo != null && chatInfo.exported_invite != null) {
                                    text = chatInfo.exported_invite.link;
                                } else {
                                    generateLink();
                                    return;
                                }
                            }
                        }
                        if (TextUtils.isEmpty(text)) {
                            return;
                        }
                        if (id == share) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, text);
                            startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", R.string.BotShare)), 500);
                        } else {
                            ProxyUtil.showQrDialog(getParentActivity(), text, avatarImage.getImageReceiver().getBitmap() == null ? null : imageSize -> Bitmap.createScaledBitmap(avatarImage.getImageReceiver().getBitmap(), imageSize, imageSize, true));
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else if (id == add_shortcut) {
                    try {
                        long did;
                        if (currentEncryptedChat != null) {
                            did = DialogObject.makeEncryptedDialogId(currentEncryptedChat.id);
                        } else if (userId != 0) {
                            did = userId;
                        } else if (chatId != 0) {
                            did = -chatId;
                        } else {
                            return;
                        }
                        getMediaDataController().installShortcut(did);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else if (id == call_item || id == video_call_item) {
                    if (userId != 0) {
                        TLRPC.User user = getMessagesController().getUser(userId);
                        if (user != null) {
                            VoIPHelper.startCall(user, id == video_call_item, userInfo != null && userInfo.video_calls_available, getParentActivity(), userInfo, getAccountInstance());
                        }
                    } else if (chatId != 0) {
                        ChatObject.Call call = getMessagesController().getGroupCall(chatId, false);
                        if (call == null) {
                            VoIPHelper.showGroupCallAlert(ProfileActivity.this, currentChat, null, false, getAccountInstance());
                        } else {
                            VoIPHelper.startCall(currentChat, null, null, false, getParentActivity(), ProfileActivity.this, getAccountInstance());
                        }
                    }
                } else if (id == search_members) {
                    Bundle args = new Bundle();
                    args.putLong("chat_id", chatId);
                    args.putInt("type", ChatUsersActivity.TYPE_USERS);
                    args.putBoolean("open_search", true);
                    ChatUsersActivity fragment = new ChatUsersActivity(args);
                    fragment.setInfo(chatInfo);
                    presentFragment(fragment);
                } else if (id == add_member) {
                    openAddMember();
                } else if (id == statistics) {
                    TLRPC.Chat chat = getMessagesController().getChat(chatId);
                    Bundle args = new Bundle();
                    args.putLong("chat_id", chatId);
                    args.putBoolean("is_megagroup", chat.megagroup);
                    StatisticActivity fragment = new StatisticActivity(args);
                    presentFragment(fragment);
                } else if (id == view_discussion) {
                    openDiscussion();
                } /*else if (id == gift_premium) {
                    showDialog(new GiftPremiumBottomSheet(ProfileActivity.this, getMessagesController().getUser(userId)));
                } */ else if (id == start_secret_chat) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), resourcesProvider);
                    builder.setTitle(LocaleController.getString("AreYouSureSecretChatTitle", R.string.AreYouSureSecretChatTitle));
                    builder.setMessage(LocaleController.getString("AreYouSureSecretChat", R.string.AreYouSureSecretChat));
                    builder.setPositiveButton(LocaleController.getString("Start", R.string.Start), (dialogInterface, i) -> {
                        creatingChat = true;
                        getSecretChatHelper().startSecretChat(getParentActivity(), getMessagesController().getUser(userId));
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    showDialog(builder.create());
                } else if (id == gallery_menu_save) {
                    if (getParentActivity() == null) {
                        return;
                    }
                    if (Build.VERSION.SDK_INT >= 23 && (Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && getParentActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        getParentActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, BasePermissionsActivity.REQUEST_CODE_EXTERNAL_STORAGE);
                        return;
                    }
                    ImageLocation location = avatarsViewPager.getImageLocation(avatarsViewPager.getRealPosition());
                    if (location == null) {
                        return;
                    }
                    final boolean isVideo = location.imageType == FileLoader.IMAGE_TYPE_ANIMATION;
                    File f = FileLoader.getInstance(currentAccount).getPathToAttach(location.location, isVideo ? "mp4" : null, true);
                    if (f.exists()) {
                        MediaController.saveFile(f.toString(), getParentActivity(), 0, null, null, () -> {
                            if (getParentActivity() == null) {
                                return;
                            }
                            BulletinFactory.createSaveToGalleryBulletin(ProfileActivity.this, isVideo, null).show();
                        });
                    }
                } else if (id == edit_name) {
                    presentFragment(new ChangeNameActivity(resourcesProvider));
                } else if (id == logout) {
                    presentFragment(new LogoutActivity());
                } else if (id == set_as_main) {
                    int position = avatarsViewPager.getRealPosition();
                    TLRPC.Photo photo = avatarsViewPager.getPhoto(position);
                    if (photo == null) {
                        return;
                    }
                    avatarsViewPager.startMovePhotoToBegin(position);

                    TLRPC.TL_photos_updateProfilePhoto req = new TLRPC.TL_photos_updateProfilePhoto();
                    req.id = new TLRPC.TL_inputPhoto();
                    req.id.id = photo.id;
                    req.id.access_hash = photo.access_hash;
                    req.id.file_reference = photo.file_reference;
                    UserConfig userConfig = getUserConfig();
                    getConnectionsManager().sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
                        avatarsViewPager.finishSettingMainPhoto();
                        if (response instanceof TLRPC.TL_photos_photo) {
                            TLRPC.TL_photos_photo photos_photo = (TLRPC.TL_photos_photo) response;
                            getMessagesController().putUsers(photos_photo.users, false);
                            TLRPC.User user = getMessagesController().getUser(userConfig.clientUserId);
                            if (photos_photo.photo instanceof TLRPC.TL_photo) {
                                avatarsViewPager.replaceFirstPhoto(photo, photos_photo.photo);
                                if (user != null) {
                                    user.photo.photo_id = photos_photo.photo.id;
                                    userConfig.setCurrentUser(user);
                                    userConfig.saveConfig(true);
                                }
                            }
                        }
                    }));
                    undoView.showWithAction(userId, UndoView.ACTION_PROFILE_PHOTO_CHANGED, photo.video_sizes.isEmpty() ? null : 1);
                    TLRPC.User user = getMessagesController().getUser(userConfig.clientUserId);

                    TLRPC.PhotoSize bigSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 800);
                    if (user != null) {
                        TLRPC.PhotoSize smallSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 90);
                        user.photo.photo_id = photo.id;
                        user.photo.photo_small = smallSize.location;
                        user.photo.photo_big = bigSize.location;
                        userConfig.setCurrentUser(user);
                        userConfig.saveConfig(true);
                        NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged);
                        updateProfileData(true);
                    }
                    avatarsViewPager.commitMoveToBegin();
                } else if (id == edit_avatar) {
                    int position = avatarsViewPager.getRealPosition();
                    ImageLocation location = avatarsViewPager.getImageLocation(position);
                    if (location == null) {
                        return;
                    }

                    File f = FileLoader.getInstance(currentAccount).getPathToAttach(PhotoViewer.getFileLocation(location), PhotoViewer.getFileLocationExt(location), true);
                    boolean isVideo = location.imageType == FileLoader.IMAGE_TYPE_ANIMATION;
                    String thumb;
                    if (isVideo) {
                        ImageLocation imageLocation = avatarsViewPager.getRealImageLocation(position);
                        thumb = FileLoader.getInstance(currentAccount).getPathToAttach(PhotoViewer.getFileLocation(imageLocation), PhotoViewer.getFileLocationExt(imageLocation), true).getAbsolutePath();
                    } else {
                        thumb = null;
                    }
                    imageUpdater.openPhotoForEdit(f.getAbsolutePath(), thumb, 0, isVideo);
                } else if (id == delete_avatar) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), resourcesProvider);
                    ImageLocation location = avatarsViewPager.getImageLocation(avatarsViewPager.getRealPosition());
                    if (location == null) {
                        return;
                    }
                    if (location.imageType == FileLoader.IMAGE_TYPE_ANIMATION) {
                        builder.setTitle(LocaleController.getString("AreYouSureDeleteVideoTitle", R.string.AreYouSureDeleteVideoTitle));
                        builder.setMessage(LocaleController.formatString("AreYouSureDeleteVideo", R.string.AreYouSureDeleteVideo));
                    } else {
                        builder.setTitle(LocaleController.getString("AreYouSureDeletePhotoTitle", R.string.AreYouSureDeletePhotoTitle));
                        builder.setMessage(LocaleController.formatString("AreYouSureDeletePhoto", R.string.AreYouSureDeletePhoto));
                    }
                    builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), (dialogInterface, i) -> {
                        int position = avatarsViewPager.getRealPosition();
                        TLRPC.Photo photo = avatarsViewPager.getPhoto(position);
                        TLRPC.UserFull userFull = getUserInfo();
                        if (avatar != null && position == 0) {
                            imageUpdater.cancel();
                            if (avatarUploadingRequest != 0) {
                                getConnectionsManager().cancelRequest(avatarUploadingRequest, true);
                            }
                            allowPullingDown = !AndroidUtilities.isTablet() && !isInLandscapeMode && avatarImage.getImageReceiver().hasNotThumb() && !AndroidUtilities.isAccessibilityScreenReaderEnabled();
                            avatar = null;
                            avatarBig = null;
                            avatarsViewPager.scrolledByUser = true;
                            avatarsViewPager.removeUploadingImage(uploadingImageLocation);
                            avatarsViewPager.setCreateThumbFromParent(false);
                            updateProfileData(true);
                            showAvatarProgress(false, true);
                            getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, MessagesController.UPDATE_MASK_ALL);
                            getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
                            getUserConfig().saveConfig(true);
                            return;
                        }
                        if (hasFallbackPhoto && photo != null && userFull != null && userFull.fallback_photo != null && userFull.fallback_photo.id == photo.id) {
                            userFull.fallback_photo = null;
                            userFull.flags &= ~4194304;
                            getMessagesStorage().updateUserInfo(userFull, true);
                            updateProfileData(false);
                        }
                        if (avatarsViewPager.getRealCount() == 1) {
                            setForegroundImage(true);
                        }
                        if (photo == null || avatarsViewPager.getRealPosition() == 0) {
                            TLRPC.Photo nextPhoto = avatarsViewPager.getPhoto(1);
                            if (nextPhoto != null) {
                                getUserConfig().getCurrentUser().photo =new TLRPC.TL_userProfilePhoto();
                                TLRPC.PhotoSize smallSize = FileLoader.getClosestPhotoSizeWithSize(nextPhoto.sizes, 90);
                                TLRPC.PhotoSize bigSize = FileLoader.getClosestPhotoSizeWithSize(nextPhoto.sizes, 1000);
                                if (smallSize != null && bigSize != null) {
                                    getUserConfig().getCurrentUser().photo.photo_small = smallSize.location;
                                    getUserConfig().getCurrentUser().photo.photo_big = bigSize.location;
                                }
                            } else {
                                getUserConfig().getCurrentUser().photo = new TLRPC.TL_userProfilePhotoEmpty();
                            }
                            getMessagesController().deleteUserPhoto(null);
                        } else {
                            TLRPC.TL_inputPhoto inputPhoto = new TLRPC.TL_inputPhoto();
                            inputPhoto.id = photo.id;
                            inputPhoto.access_hash = photo.access_hash;
                            inputPhoto.file_reference = photo.file_reference;
                            if (inputPhoto.file_reference == null) {
                                inputPhoto.file_reference = new byte[0];
                            }
                            getMessagesController().deleteUserPhoto(inputPhoto);
                            getMessagesStorage().clearUserPhoto(userId, photo.id);
                        }
                        if (avatarsViewPager.removePhotoAtIndex(position)) {
                            avatarsViewPager.setVisibility(View.GONE);
                            avatarImage.setForegroundAlpha(1f);
                            avatarContainer.setVisibility(View.VISIBLE);
                            doNotSetForeground = true;
                            final View view = layoutManager.findViewByPosition(0);
                            if (view != null) {
                                listView.smoothScrollBy(0, view.getTop() - AndroidUtilities.dp(88), CubicBezierInterpolator.EASE_OUT_QUINT);
                            }
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    AlertDialog alertDialog = builder.create();
                    showDialog(alertDialog);
                    TextView button = (TextView) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    if (button != null) {
                        button.setTextColor(getThemedColor(Theme.key_dialogTextRed));
                    }
                } else if (id == add_photo) {
                    onWriteButtonClick();
                } else if (id == qr_button) {
//                    if (qrItem != null && qrItem.getAlpha() > 0) {
//                        Bundle args = new Bundle();
//                        args.putLong("chat_id", chatId);
//                        args.putLong("user_id", userId);
//                        presentFragment(new QrActivity(args));
//                    }
                }
            }
        });

        if (sharedMediaLayout != null) {
            sharedMediaLayout.onDestroy();
        }
        final long did;
        if (dialogId != 0) {
            did = dialogId;
        } else if (userId != 0) {
            did = userId;
        } else {
            did = -chatId;
        }

        fragmentView = new NestedFrameLayout(context) {

            @Override
            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (pinchToZoomHelper.isInOverlayMode()) {
                    return pinchToZoomHelper.onTouchEvent(ev);
                }
                if (sharedMediaLayout != null && sharedMediaLayout.isInFastScroll() && sharedMediaLayout.isPinnedToTop()) {
                    return sharedMediaLayout.dispatchFastScrollEvent(ev);
                }
                if (sharedMediaLayout != null && sharedMediaLayout.checkPinchToZoom(ev)) {
                    return true;
                }
                return super.dispatchTouchEvent(ev);
            }

            private boolean ignoreLayout;
            private Paint grayPaint = new Paint();

            @Override
            public boolean hasOverlappingRendering() {
                return false;
            }

            private boolean wasPortrait;

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                final int actionBarHeight = ActionBar.getCurrentActionBarHeight() + (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
                if (listView != null) {
                    LayoutParams layoutParams = (LayoutParams) listView.getLayoutParams();
                    if (layoutParams.topMargin != actionBarHeight) {
                        layoutParams.topMargin = actionBarHeight;
                    }
                }
                if (searchListView != null) {
                    LayoutParams layoutParams = (LayoutParams) searchListView.getLayoutParams();
                    if (layoutParams.topMargin != actionBarHeight) {
                        layoutParams.topMargin = actionBarHeight;
                    }
                }

                int height = MeasureSpec.getSize(heightMeasureSpec);
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));

                boolean changed = false;
                if (lastMeasuredContentWidth != getMeasuredWidth() || lastMeasuredContentHeight != getMeasuredHeight()) {
                    changed = lastMeasuredContentWidth != 0 && lastMeasuredContentWidth != getMeasuredWidth();
                    listContentHeight = 0;
                    int count = listAdapter.getItemCount();
                    lastMeasuredContentWidth = getMeasuredWidth();
                    lastMeasuredContentHeight = getMeasuredHeight();
                    int ws = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
                    int hs = MeasureSpec.makeMeasureSpec(listView.getMeasuredHeight(), MeasureSpec.UNSPECIFIED);
                    positionToOffset.clear();
                    for (int i = 0; i < count; i++) {
                        int type = listAdapter.getItemViewType(i);
                        positionToOffset.put(i, listContentHeight);
                        if (type == ListAdapter.VIEW_TYPE_SHARED_MEDIA) {
                            listContentHeight += listView.getMeasuredHeight();
                        } else {
                            RecyclerView.ViewHolder holder = listAdapter.createViewHolder(null, type);
                            listAdapter.onBindViewHolder(holder, i);
                            holder.itemView.measure(ws, hs);
                            listContentHeight += holder.itemView.getMeasuredHeight();
                        }
                    }

                    if (emptyView != null) {
                        ((LayoutParams) emptyView.getLayoutParams()).topMargin = AndroidUtilities.dp(88) + AndroidUtilities.statusBarHeight;
                    }
                }

                if (previousTransitionFragment != null) {
                    nameTextView[0].setRightPadding(nameTextView[0].getMeasuredWidth() - previousTransitionFragment.getAvatarContainer().getTitleTextView().getMeasuredWidth());
                }

                if (!fragmentOpened && (expandPhoto || openAnimationInProgress && playProfileAnimation == 2)) {
                    ignoreLayout = true;

                    if (expandPhoto) {
                        if (searchItem != null) {
                            searchItem.setAlpha(0.0f);
                            searchItem.setEnabled(false);
                            searchItem.setVisibility(GONE);
                        }
                        nameTextView[1].setTextColor(Color.WHITE);
                        nameTextView[1].setPivotY(nameTextView[1].getMeasuredHeight());
                        nameTextView[1].setScaleX(1.67f);
                        nameTextView[1].setScaleY(1.67f);
                        if (scamDrawable != null) {
                            scamDrawable.setColor(Color.argb(179, 255, 255, 255));
                        }
                        if (lockIconDrawable != null) {
                            lockIconDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                        }
                        if (verifiedCrossfadeDrawable != null) {
                            verifiedCrossfadeDrawable.setProgress(1f);
                        }
                        if (premiumCrossfadeDrawable != null) {
                            premiumCrossfadeDrawable.setProgress(1f);
                        }
                        updateEmojiStatusDrawableColor(1f);
                        onlineTextView[1].setTextColor(Color.argb(179, 255, 255, 255));
                        idTextView.setTextColor(Color.argb(179, 255, 255, 255));
                        actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, false);
                        actionBar.setItemsColor(Color.WHITE, false);
                        overlaysView.setOverlaysVisible();
                        overlaysView.setAlphaValue(1.0f, false);
                        avatarImage.setForegroundAlpha(1.0f);
                        avatarContainer.setVisibility(View.GONE);
                        avatarsViewPager.resetCurrentItem();
                        avatarsViewPager.setVisibility(View.VISIBLE);
                        expandPhoto = false;
                    }

                    allowPullingDown = true;
                    isPulledDown = true;
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, true);
                    /*if (otherItem != null) {
                        if (!getMessagesController().isChatNoForwards(currentChat)) {
                            otherItem.showSubItem(gallery_menu_save);
                        } else {
                            otherItem.hideSubItem(gallery_menu_save);
                        }
                        if (imageUpdater != null) {
                            otherItem.showSubItem(edit_avatar);
                            otherItem.showSubItem(delete_avatar);
                            otherItem.hideSubItem(logout);
                        }
                    }*/
                    currentExpanAnimatorFracture = 1.0f;

                    int paddingTop;
                    int paddingBottom;
                    if (isInLandscapeMode) {
                        paddingTop = AndroidUtilities.dp(88f);
                        paddingBottom = 0;
                    } else {
                        paddingTop = listView.getMeasuredWidth();
                        paddingBottom = Math.max(0, getMeasuredHeight() - (listContentHeight + AndroidUtilities.dp(88) + actionBarHeight));
                    }
                    if (banFromGroup != 0) {
                        paddingBottom += AndroidUtilities.dp(48);
                        listView.setBottomGlowOffset(AndroidUtilities.dp(48));
                    } else {
                        listView.setBottomGlowOffset(0);
                    }
                    initialAnimationExtraHeight = paddingTop - actionBarHeight;
                    if (playProfileAnimation == 0) {
                        extraHeight = initialAnimationExtraHeight;
                    }
                    layoutManager.scrollToPositionWithOffset(0, -actionBarHeight);
                    listView.setPadding(0, paddingTop, 0, paddingBottom);
                    measureChildWithMargins(listView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    listView.layout(0, actionBarHeight, listView.getMeasuredWidth(), actionBarHeight + listView.getMeasuredHeight());
                    ignoreLayout = false;
                } else if (fragmentOpened && !openAnimationInProgress && !firstLayout) {
                    ignoreLayout = true;

                    int paddingTop;
                    int paddingBottom;
                    if (isInLandscapeMode || AndroidUtilities.isTablet()) {
                        paddingTop = AndroidUtilities.dp(88f);
                        paddingBottom = 0;
                    } else {
                        paddingTop = listView.getMeasuredWidth();
                        paddingBottom = Math.max(0, getMeasuredHeight() - (listContentHeight + AndroidUtilities.dp(88) + actionBarHeight));
                    }
                    if (banFromGroup != 0) {
                        paddingBottom += AndroidUtilities.dp(48);
                        listView.setBottomGlowOffset(AndroidUtilities.dp(48));
                    } else {
                        listView.setBottomGlowOffset(0);
                    }
                    int currentPaddingTop = listView.getPaddingTop();
                    View view = null;
                    int pos = RecyclerView.NO_POSITION;
                    for (int i = 0; i < listView.getChildCount(); i++) {
                        int p = listView.getChildAdapterPosition(listView.getChildAt(i));
                        if (p != RecyclerView.NO_POSITION) {
                            view = listView.getChildAt(i);
                            pos = p;
                            break;
                        }
                    }
                    if (view == null) {
                        view = listView.getChildAt(0);
                        if (view != null) {
                            RecyclerView.ViewHolder holder = listView.findContainingViewHolder(view);
                            pos = holder.getAdapterPosition();
                            if (pos == RecyclerView.NO_POSITION) {
                                pos = holder.getPosition();
                            }
                        }
                    }

                    int top = paddingTop;
                    if (view != null) {
                        top = view.getTop();
                    }
                    boolean layout = false;
                    if (actionBar.isSearchFieldVisible() && sharedMediaRow >= 0) {
                        layoutManager.scrollToPositionWithOffset(sharedMediaRow, -paddingTop);
                        layout = true;
                    } else if (invalidateScroll || currentPaddingTop != paddingTop) {
                        if (savedScrollPosition >= 0) {
                            layoutManager.scrollToPositionWithOffset(savedScrollPosition, savedScrollOffset - paddingTop);
                        } else if ((!changed || !allowPullingDown) && view != null) {
                            if (pos == 0 && !allowPullingDown && top > AndroidUtilities.dp(88)) {
                                top = AndroidUtilities.dp(88);
                            }
                            layoutManager.scrollToPositionWithOffset(pos, top - paddingTop);
                            layout = true;
                        } else {
                            layoutManager.scrollToPositionWithOffset(0, AndroidUtilities.dp(88) - paddingTop);
                        }
                    }
                    if (currentPaddingTop != paddingTop || listView.getPaddingBottom() != paddingBottom) {
                        listView.setPadding(0, paddingTop, 0, paddingBottom);
                        layout = true;
                    }
                    if (layout) {
                        measureChildWithMargins(listView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        try {
                            listView.layout(0, actionBarHeight, listView.getMeasuredWidth(), actionBarHeight + listView.getMeasuredHeight());
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                    ignoreLayout = false;
                }

                boolean portrait = height > MeasureSpec.getSize(widthMeasureSpec);
                if (portrait != wasPortrait) {
                    post(() -> {
                        if (selectAnimatedEmojiDialog != null) {
                            selectAnimatedEmojiDialog.dismiss();
                            selectAnimatedEmojiDialog = null;
                        }
                    });
                    wasPortrait = portrait;
                }

//                if (searchItem != null && qrItem != null) {
//                    float translation = AndroidUtilities.dp(48) * currentExpandAnimatorValue;
//                   // qrItem.setTranslationX(translation);
//                }
            }

            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                savedScrollPosition = -1;
                firstLayout = false;
                invalidateScroll = false;
                checkListViewScroll();
            }

            @Override
            public void requestLayout() {
                if (ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            private final ArrayList<View> sortedChildren = new ArrayList<>();
            private final Comparator<View> viewComparator = (view, view2) -> (int) (view.getY() - view2.getY());


            @Override
            protected void dispatchDraw(Canvas canvas) {
                whitePaint.setColor(getThemedColor(Theme.key_windowBackgroundWhite));
                if (listView.getVisibility() == VISIBLE) {
                    grayPaint.setColor(getThemedColor(Theme.key_windowBackgroundGray));
                    if (transitionAnimationInProress) {
                        whitePaint.setAlpha((int) (255 * listView.getAlpha()));
                    }
                    if (transitionAnimationInProress) {
                        grayPaint.setAlpha((int) (255 * listView.getAlpha()));
                    }

                    int count = listView.getChildCount();
                    sortedChildren.clear();
                    boolean hasRemovingItems = false;
                    for (int i = 0; i < count; i++) {
                        View child = listView.getChildAt(i);
                        if (listView.getChildAdapterPosition(child) != RecyclerView.NO_POSITION) {
                            sortedChildren.add(listView.getChildAt(i));
                        } else {
                            hasRemovingItems = true;
                        }
                    }
                    Collections.sort(sortedChildren, viewComparator);
                    boolean hasBackground = false;
                    float lastY = listView.getY();
                    count = sortedChildren.size();
                    if (!openAnimationInProgress && count > 0 && !hasRemovingItems) {
                        lastY += sortedChildren.get(0).getY();
                    }
                    float alpha = 1f;
                    for (int i = 0; i < count; i++) {
                        View child = sortedChildren.get(i);
                        boolean currentHasBackground = child.getBackground() != null;
                        int currentY = (int) (listView.getY() + child.getY());
                        if (hasBackground == currentHasBackground) {
                            if (child.getAlpha() == 1f) {
                                alpha = 1f;
                            }
                            continue;
                        }
                        if (hasBackground) {
                            canvas.drawRect(listView.getX(), lastY, listView.getX() + listView.getMeasuredWidth(), currentY, grayPaint);
                        } else {
                            if (alpha != 1f) {
                                canvas.drawRect(listView.getX(), lastY, listView.getX() + listView.getMeasuredWidth(), currentY, grayPaint);
                                whitePaint.setAlpha((int) (255 * alpha));
                                canvas.drawRect(listView.getX(), lastY, listView.getX() + listView.getMeasuredWidth(), currentY, whitePaint);
                                whitePaint.setAlpha(255);
                            } else {
                                canvas.drawRect(listView.getX(), lastY, listView.getX() + listView.getMeasuredWidth(), currentY, whitePaint);
                            }
                        }
                        hasBackground = currentHasBackground;
                        lastY = currentY;
                        alpha = child.getAlpha();
                    }

                    if (hasBackground) {
                        canvas.drawRect(listView.getX(), lastY, listView.getX() + listView.getMeasuredWidth(), listView.getBottom(), grayPaint);
                    } else {
                        if (alpha != 1f) {
                            canvas.drawRect(listView.getX(), lastY, listView.getX() + listView.getMeasuredWidth(), listView.getBottom(), grayPaint);
                            whitePaint.setAlpha((int) (255 * alpha));
                            canvas.drawRect(listView.getX(), lastY, listView.getX() + listView.getMeasuredWidth(), listView.getBottom(), whitePaint);
                            whitePaint.setAlpha(255);
                        } else {
                            canvas.drawRect(listView.getX(), lastY, listView.getX() + listView.getMeasuredWidth(), listView.getBottom(), whitePaint);
                        }
                    }
                } else {
                    int top = searchListView.getTop();
                    canvas.drawRect(0, top + extraHeight + searchTransitionOffset, getMeasuredWidth(), top + getMeasuredHeight(), whitePaint);
                }
                super.dispatchDraw(canvas);
                if (profileTransitionInProgress && parentLayout.getFragmentStack().size() > 1) {
                    BaseFragment fragment = parentLayout.getFragmentStack().get(parentLayout.getFragmentStack().size() - 2);
                    if (fragment instanceof ChatActivity) {
                        ChatActivity chatActivity = (ChatActivity) fragment;
                        FragmentContextView fragmentContextView = chatActivity.getFragmentContextView();

                        if (fragmentContextView != null && fragmentContextView.isCallStyle()) {
                            float progress = extraHeight / AndroidUtilities.dpf2(fragmentContextView.getStyleHeight());
                            if (progress > 1f) {
                                progress = 1f;
                            }
                            canvas.save();
                            canvas.translate(fragmentContextView.getX(), fragmentContextView.getY());
                            fragmentContextView.setDrawOverlay(true);
                            fragmentContextView.setCollapseTransition(true, extraHeight, progress);
                            fragmentContextView.draw(canvas);
                            fragmentContextView.setCollapseTransition(false, extraHeight, progress);
                            fragmentContextView.setDrawOverlay(false);
                            canvas.restore();
                        }
                    }
                }

                if (scrimPaint.getAlpha() > 0) {
                    canvas.drawRect(0, 0, getWidth(), getHeight(), scrimPaint);
                }
                if (scrimView != null) {
                    int c = canvas.save();
                    canvas.translate(scrimView.getLeft(), scrimView.getTop());
                    if (scrimView == actionBar.getBackButton()) {
                        int r = Math.max(scrimView.getMeasuredWidth(), scrimView.getMeasuredHeight()) / 2;
                        int wasAlpha = actionBarBackgroundPaint.getAlpha();
                        actionBarBackgroundPaint.setAlpha((int) (wasAlpha * (scrimPaint.getAlpha() / 255f) / 0.3f));
                        canvas.drawCircle(r, r, r * 0.7f, actionBarBackgroundPaint);
                        actionBarBackgroundPaint.setAlpha(wasAlpha);
                    }
                    scrimView.draw(canvas);
                    canvas.restoreToCount(c);
                }
            }

            @Override
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (pinchToZoomHelper.isInOverlayMode() && (child == avatarContainer2 || child == actionBar || child == writeButton)) {
                    return true;
                }
                return super.drawChild(canvas, child, drawingTime);
            }

            @Override
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                fragmentViewAttached = true;
                for (int i = 0; i < emojiStatusDrawable.length; i++) {
                    if (emojiStatusDrawable[i] != null) {
                        emojiStatusDrawable[i].attach();
                    }
                }
            }

            @Override
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                fragmentViewAttached = false;
                for (int i = 0; i < emojiStatusDrawable.length; i++) {
                    if (emojiStatusDrawable[i] != null) {
                        emojiStatusDrawable[i].detach();
                    }
                }
            }
        };

        ArrayList<Integer> users = chatInfo != null && chatInfo.participants != null && chatInfo.participants.participants.size() > 5 ? sortedUsers : null;
        sharedMediaLayout = new SharedMediaLayout(context, did, sharedMediaPreloader, userInfo != null ? userInfo.common_chats_count : 0, sortedUsers, chatInfo, users != null, this, this, SharedMediaLayout.VIEW_TYPE_PROFILE_ACTIVITY, resourcesProvider) {
            @Override
            protected void onSelectedTabChanged() {
                updateSelectedMediaTabText();
            }

            @Override
            protected boolean canShowSearchItem() {
                return mediaHeaderVisible;
            }

            @Override
            protected void onSearchStateChanged(boolean expanded) {
                AndroidUtilities.removeAdjustResize(getParentActivity(), classGuid);

                listView.stopScroll();
                avatarContainer2.setPivotY(avatarContainer.getPivotY() + avatarContainer.getMeasuredHeight() / 2f);
                avatarContainer2.setPivotX(avatarContainer2.getMeasuredWidth() / 2f);
                AndroidUtilities.updateViewVisibilityAnimated(avatarContainer2, !expanded, 0.95f, true);

                if (Math.min(1f, extraHeight / AndroidUtilities.dp(88f)) > 0.85 && !searchMode && NekoConfig.showIdAndDc.Bool())
                    idTextView.setVisibility(expanded ? INVISIBLE : VISIBLE);

                callItem.setVisibility(expanded || !callItemVisible ? GONE : INVISIBLE);
                videoCallItem.setVisibility(expanded || !videoCallItemVisible ? GONE : INVISIBLE);
                editItem.setVisibility(expanded || !editItemVisible ? GONE : INVISIBLE);
                otherItem.setVisibility(expanded ? GONE : INVISIBLE);
//                if (qrItem != null) {
//                    qrItem.setVisibility(expanded ? GONE : INVISIBLE);
//                }
            }

            @Override
            protected boolean onMemberClick(TLRPC.ChatParticipant participant, boolean isLong) {
                return ProfileActivity.this.onMemberClick(participant, isLong);
            }

            @Override
            protected void drawBackgroundWithBlur(Canvas canvas, float y, Rect rectTmp2, Paint backgroundPaint) {
                contentView.drawBlurRect(canvas, listView.getY() + getY() + y, rectTmp2, backgroundPaint, true);
            }

            @Override
            protected void invalidateBlur() {
                contentView.invalidateBlur();
            }
        };
        sharedMediaLayout.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));

        ActionBarMenu menu = actionBar.createMenu();

//        if (userId == getUserConfig().clientUserId) {
//            qrItem = menu.addItem(qr_button, R.drawable.msg_qr_mini, getResourceProvider());
//            qrItem.setContentDescription(LocaleController.getString("GetQRCode", R.string.GetQRCode));
//            updateQrItemVisibility(false);
//            if (ContactsController.getInstance(currentAccount).getPrivacyRules(PRIVACY_RULES_TYPE_ADDED_BY_PHONE) == null) {
//                ContactsController.getInstance(currentAccount).loadPrivacySettings();
//            }
//        }

        if (imageUpdater != null) {
            searchItem = menu.addItem(search_button, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {

                @Override
                public Animator getCustomToggleTransition() {
                    searchMode = !searchMode;
                    if (!searchMode) {
                        searchItem.clearFocusOnSearchView();
                    }
                    if (searchMode) {
                        searchItem.getSearchField().setText("");
                    }
                    return searchExpandTransition(searchMode);
                }

                @Override
                public void onTextChanged(EditText editText) {
                    searchAdapter.search(editText.getText().toString().toLowerCase());
                }
            });
            searchItem.setContentDescription(LocaleController.getString("SearchInSettings", R.string.SearchInSettings));
            searchItem.setSearchFieldHint(LocaleController.getString("SearchInSettings", R.string.SearchInSettings));
            sharedMediaLayout.getSearchItem().setVisibility(View.GONE);
            if (expandPhoto) {
                searchItem.setVisibility(View.GONE);
            }
        }

        videoCallItem = menu.addItem(video_call_item, R.drawable.profile_video);
        videoCallItem.setContentDescription(LocaleController.getString("VideoCall", R.string.VideoCall));
        if (chatId != 0) {
            callItem = menu.addItem(call_item, R.drawable.msg_voicechat2);
            if (ChatObject.isChannelOrGiga(currentChat)) {
                callItem.setContentDescription(LocaleController.getString("VoipChannelVoiceChat", R.string.VoipChannelVoiceChat));
            } else {
                callItem.setContentDescription(LocaleController.getString("VoipGroupVoiceChat", R.string.VoipGroupVoiceChat));
            }
        } else {
            callItem = menu.addItem(call_item, R.drawable.ic_call);
            callItem.setContentDescription(LocaleController.getString("Call", R.string.Call));
        }
        editItem = menu.addItem(edit_channel, R.drawable.group_edit_profile);
        editItem.setContentDescription(LocaleController.getString("Edit", R.string.Edit));
        otherItem = menu.addItem(10, R.drawable.ic_ab_other, resourcesProvider);
        ttlIconView = new ImageView(context);
        ttlIconView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_actionBarDefaultIcon), PorterDuff.Mode.MULTIPLY));
        AndroidUtilities.updateViewVisibilityAnimated(ttlIconView, false, 0.8f, false);
        ttlIconView.setImageResource(R.drawable.msg_mini_autodelete_timer);
        otherItem.addView(ttlIconView, LayoutHelper.createFrame(12, 12, Gravity.CENTER_VERTICAL | Gravity.LEFT, 8, 2, 0, 0));
        otherItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));

        int scrollTo;
        int scrollToPosition = 0;
        Object writeButtonTag = null;
        if (listView != null && imageUpdater != null) {
            scrollTo = layoutManager.findFirstVisibleItemPosition();
            View topView = layoutManager.findViewByPosition(scrollTo);
            if (topView != null) {
                scrollToPosition = topView.getTop() - listView.getPaddingTop();
            } else {
                scrollTo = -1;
            }
            writeButtonTag = writeButton.getTag();
        } else {
            scrollTo = -1;
        }

        createActionBarMenu(false);

        listAdapter = new ListAdapter(context);
        searchAdapter = new SearchAdapter(context);
        avatarDrawable = new AvatarDrawable();
        avatarDrawable.setProfile(true);

        fragmentView.setWillNotDraw(false);
        contentView = ((NestedFrameLayout) fragmentView);
        contentView.needBlur = true;
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        listView = new RecyclerListView(context) {

            private VelocityTracker velocityTracker;

            @Override
            protected boolean canHighlightChildAt(View child, float x, float y) {
                return !(child instanceof AboutLinkCell);
            }

            @Override
            protected boolean allowSelectChildAtPosition(View child) {
                return child != sharedMediaLayout;
            }

            @Override
            public boolean hasOverlappingRendering() {
                return false;
            }

            @Override
            protected void requestChildOnScreen(View child, View focused) {

            }

            @Override
            public void invalidate() {
                super.invalidate();
                if (fragmentView != null) {
                    fragmentView.invalidate();
                }
            }

            @Override
            public boolean onTouchEvent(MotionEvent e) {
                final int action = e.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    if (velocityTracker == null) {
                        velocityTracker = VelocityTracker.obtain();
                    } else {
                        velocityTracker.clear();
                    }
                    velocityTracker.addMovement(e);
                } else if (action == MotionEvent.ACTION_MOVE) {
                    if (velocityTracker != null) {
                        velocityTracker.addMovement(e);
                        velocityTracker.computeCurrentVelocity(1000);
                        listViewVelocityY = velocityTracker.getYVelocity(e.getPointerId(e.getActionIndex()));
                    }
                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        velocityTracker = null;
                    }
                }
                final boolean result = super.onTouchEvent(e);
                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    if (allowPullingDown) {
                        final View view = layoutManager.findViewByPosition(0);
                        if (view != null) {
                            if (isPulledDown) {
                                final int actionBarHeight = ActionBar.getCurrentActionBarHeight() + (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
                                listView.smoothScrollBy(0, view.getTop() - listView.getMeasuredWidth() + actionBarHeight, CubicBezierInterpolator.EASE_OUT_QUINT);
                            } else {
                                listView.smoothScrollBy(0, view.getTop() - AndroidUtilities.dp(88), CubicBezierInterpolator.EASE_OUT_QUINT);
                            }
                        }
                    }
                }
                return result;
            }

            @Override
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (getItemAnimator().isRunning() && child.getBackground() == null && child.getTranslationY() != 0) {
                    boolean useAlpha = listView.getChildAdapterPosition(child) == sharedMediaRow && child.getAlpha() != 1f;
                    if (useAlpha) {
                        whitePaint.setAlpha((int) (255 * listView.getAlpha() * child.getAlpha()));
                    }
                    canvas.drawRect(listView.getX(), child.getY(), listView.getX() + listView.getMeasuredWidth(), child.getY() + child.getHeight(), whitePaint);
                    if (useAlpha) {
                        whitePaint.setAlpha((int) (255 * listView.getAlpha()));
                    }
                }
                return super.drawChild(canvas, child, drawingTime);
            }
        };
        listView.setVerticalScrollBarEnabled(false);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator() {

            int animationIndex = -1;

            @Override
            protected void onAllAnimationsDone() {
                super.onAllAnimationsDone();
                AndroidUtilities.runOnUIThread(() -> {
                    getNotificationCenter().onAnimationFinish(animationIndex);
                });
            }

            @Override
            public void runPendingAnimations() {
                boolean removalsPending = !mPendingRemovals.isEmpty();
                boolean movesPending = !mPendingMoves.isEmpty();
                boolean changesPending = !mPendingChanges.isEmpty();
                boolean additionsPending = !mPendingAdditions.isEmpty();
                if (removalsPending || movesPending || additionsPending || changesPending) {
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1f);
                    valueAnimator.addUpdateListener(valueAnimator1 -> listView.invalidate());
                    valueAnimator.setDuration(getMoveDuration());
                    valueAnimator.start();
                    animationIndex = getNotificationCenter().setAnimationInProgress(animationIndex, null);
                }
                super.runPendingAnimations();
            }

            @Override
            protected long getAddAnimationDelay(long removeDuration, long moveDuration, long changeDuration) {
                return 0;
            }

            @Override
            protected long getMoveAnimationDelay() {
                return 0;
            }

            @Override
            public long getMoveDuration() {
                return 220;
            }

            @Override
            public long getRemoveDuration() {
                return 220;
            }

            @Override
            public long getAddDuration() {
                return 220;
            }
        };
        listView.setItemAnimator(defaultItemAnimator);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        defaultItemAnimator.setDelayAnimations(false);
        listView.setClipToPadding(false);
        listView.setHideIfEmpty(false);

        layoutManager = new LinearLayoutManager(context) {

            @Override
            public boolean supportsPredictiveItemAnimations() {
                return imageUpdater != null;
            }

            @Override
            public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
                final View view = layoutManager.findViewByPosition(0);
                if (view != null && !openingAvatar) {
                    final int canScroll = view.getTop() - AndroidUtilities.dp(88);
                    if (!allowPullingDown && canScroll > dy) {
                        dy = canScroll;
                        if (avatarsViewPager.hasImages() && avatarImage.getImageReceiver().hasNotThumb() && !AndroidUtilities.isAccessibilityScreenReaderEnabled() && !isInLandscapeMode && !AndroidUtilities.isTablet()) {
                            allowPullingDown = avatarBig == null;
                        }
                    } else if (allowPullingDown) {
                        if (dy >= canScroll) {
                            dy = canScroll;
                            allowPullingDown = false;
                        } else if (listView.getScrollState() == RecyclerListView.SCROLL_STATE_DRAGGING) {
                            if (!isPulledDown) {
                                dy /= 2;
                            }
                        }
                    }
                }
                return super.scrollVerticallyBy(dy, recycler, state);
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.mIgnoreTopPadding = false;
        listView.setLayoutManager(layoutManager);
        listView.setGlowColor(0);
        listView.setAdapter(listAdapter);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (getParentActivity() == null) {
                return;
            }
            listView.stopScroll();
            if (position == notificationsSimpleRow) {
                boolean muted = getMessagesController().isDialogMuted(did, topicId);
                getNotificationsController().muteDialog(did, topicId, !muted);
                BulletinFactory.createMuteBulletin(ProfileActivity.this, !muted, null).show();
                updateExceptions();
                if (notificationsSimpleRow >= 0 && listAdapter != null) {
                    listAdapter.notifyItemChanged(notificationsSimpleRow);
                }
            } else if (position == addToContactsRow) {
                TLRPC.User user = getMessagesController().getUser(userId);
                Bundle args = new Bundle();
                args.putLong("user_id", user.id);
                args.putBoolean("addContact", true);
                args.putString("phone", vcardPhone);
                args.putString("first_name_card", vcardFirstName);
                args.putString("last_name_card", vcardLastName);
                presentFragment(new ContactAddActivity(args, resourcesProvider));
            } else if (position == reportReactionRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), resourcesProvider);
                builder.setTitle(LocaleController.getString("ReportReaction", R.string.ReportReaction));
                builder.setMessage(LocaleController.getString("ReportAlertReaction", R.string.ReportAlertReaction));

                TLRPC.Chat chat = getMessagesController().getChat(-reportReactionFromDialogId);
                CheckBoxCell[] cells = new CheckBoxCell[1];
                if (chat != null && ChatObject.canBlockUsers(chat)) {
                    LinearLayout linearLayout = new LinearLayout(getParentActivity());
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    cells[0] = new CheckBoxCell(getParentActivity(), 1, resourcesProvider);
                    cells[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    cells[0].setText(LocaleController.getString("BanUser", R.string.BanUser), "", true, false);
                    cells[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16) : AndroidUtilities.dp(8), 0, LocaleController.isRTL ? AndroidUtilities.dp(8) : AndroidUtilities.dp(16), 0);
                    linearLayout.addView(cells[0], LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
                    cells[0].setOnClickListener(v -> {
                        cells[0].setChecked(!cells[0].isChecked(), true);
                    });
                    builder.setView(linearLayout);
                }

                builder.setPositiveButton(LocaleController.getString("ReportChat", R.string.ReportChat), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TLRPC.TL_messages_reportReaction req = new TLRPC.TL_messages_reportReaction();
                        req.user_id = getMessagesController().getInputUser(userId);
                        req.peer = getMessagesController().getInputPeer(reportReactionFromDialogId);
                        req.id = reportReactionMessageId;
                        ConnectionsManager.getInstance(currentAccount).sendRequest(req, (response, error) -> {

                        });

                        if (cells[0] != null && cells[0].isChecked()) {
                            TLRPC.User user = getMessagesController().getUser(userId);
                            getMessagesController().deleteParticipantFromChat(-reportReactionFromDialogId, user);
                        }

                        reportReactionMessageId = 0;
                        updateListAnimated(false);
                        BulletinFactory.of(ProfileActivity.this).createReportSent(resourcesProvider).show();
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.show();
                TextView button = (TextView) dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed));
                }
            } if (position == settingsKeyRow) {
                Bundle args = new Bundle();
                args.putInt("chat_id", DialogObject.getEncryptedChatId(dialogId));
                presentFragment(new IdenticonActivity(args));
            } else if (position == settingsTimerRow) {
                showDialog(AlertsCreator.createTTLAlert(getParentActivity(), currentEncryptedChat, resourcesProvider).create());
            } else if (position == notificationsRow) {
                if (LocaleController.isRTL && x <= AndroidUtilities.dp(76) || !LocaleController.isRTL && x >= view.getMeasuredWidth() - AndroidUtilities.dp(76)) {
                    NotificationsCheckCell checkCell = (NotificationsCheckCell) view;
                    boolean checked = !checkCell.isChecked();

                    boolean defaultEnabled = getNotificationsController().isGlobalNotificationsEnabled(did);

                    String key = NotificationsController.getSharedPrefKey(did, topicId);
                    if (checked) {
                        SharedPreferences preferences = MessagesController.getNotificationsSettings(currentAccount);
                        SharedPreferences.Editor editor = preferences.edit();
                        if (defaultEnabled) {
                            editor.remove("notify2_" + key);
                        } else {
                            editor.putInt("notify2_" + key, 0);
                        }
                        if (topicId == 0) {
                            getMessagesStorage().setDialogFlags(did, 0);
                            TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(did);
                            if (dialog != null) {
                                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                            }
                        }
                        editor.apply();
                    } else {
                        int untilTime = Integer.MAX_VALUE;
                        SharedPreferences preferences = MessagesController.getNotificationsSettings(currentAccount);
                        SharedPreferences.Editor editor = preferences.edit();
                        long flags;
                        if (!defaultEnabled) {
                            editor.remove("notify2_" + key);
                            flags = 0;
                        } else {
                            editor.putInt("notify2_" + key, 2);
                            flags = 1;
                        }
                        getNotificationsController().removeNotificationsForDialog(did);
                        if (topicId == 0) {
                            getMessagesStorage().setDialogFlags(did, flags);
                            TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(did);
                            if (dialog != null) {
                                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                                if (defaultEnabled) {
                                    dialog.notify_settings.mute_until = untilTime;
                                }
                            }
                        }
                        editor.apply();
                    }
                    updateExceptions();
                    getNotificationsController().updateServerNotificationsSettings(did, topicId);
                    checkCell.setChecked(checked);
                    RecyclerListView.Holder holder = (RecyclerListView.Holder) listView.findViewHolderForPosition(notificationsRow);
                    if (holder != null) {
                        listAdapter.onBindViewHolder(holder, notificationsRow);
                    }
                    return;
                }
                ChatNotificationsPopupWrapper chatNotificationsPopupWrapper = new ChatNotificationsPopupWrapper(context, currentAccount, null, true, true, new ChatNotificationsPopupWrapper.Callback() {
                    @Override
                    public void toggleSound() {
                        SharedPreferences preferences = MessagesController.getNotificationsSettings(currentAccount);
                        boolean enabled = !preferences.getBoolean("sound_enabled_" + NotificationsController.getSharedPrefKey(did, topicId), true);
                        preferences.edit().putBoolean("sound_enabled_" + NotificationsController.getSharedPrefKey(did, topicId), enabled).apply();
                        if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                            BulletinFactory.createSoundEnabledBulletin(ProfileActivity.this, enabled ? NotificationsController.SETTING_SOUND_ON : NotificationsController.SETTING_SOUND_OFF, getResourceProvider()).show();
                        }
                    }

                    @Override
                    public void muteFor(int timeInSeconds) {
                        if (timeInSeconds == 0) {
                            if (getMessagesController().isDialogMuted(did, topicId)) {
                                toggleMute();
                            }
                            if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                                BulletinFactory.createMuteBulletin(ProfileActivity.this, NotificationsController.SETTING_MUTE_UNMUTE, timeInSeconds, getResourceProvider()).show();
                            }
                        } else {
                            getNotificationsController().muteUntil(did, topicId, timeInSeconds);
                            if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                                BulletinFactory.createMuteBulletin(ProfileActivity.this, NotificationsController.SETTING_MUTE_CUSTOM, timeInSeconds, getResourceProvider()).show();
                            }
                            updateExceptions();
                            if (notificationsRow >= 0 && listAdapter != null) {
                                listAdapter.notifyItemChanged(notificationsRow);
                            }
                        }
                    }

                    @Override
                    public void showCustomize() {
                        if (did != 0) {
                            Bundle args = new Bundle();
                            args.putLong("dialog_id", did);
                            args.putInt("topic_id", topicId);
                            presentFragment(new ProfileNotificationsActivity(args, resourcesProvider));
                        }
                    }

                    @Override
                    public void toggleMute() {
                        boolean muted = getMessagesController().isDialogMuted(did, topicId);
                        getNotificationsController().muteDialog(did, topicId, !muted);
                        if (ProfileActivity.this.fragmentView != null) {
                            BulletinFactory.createMuteBulletin(ProfileActivity.this, !muted, null).show();
                        }
                        updateExceptions();
                        if (notificationsRow >= 0 && listAdapter != null) {
                            listAdapter.notifyItemChanged(notificationsRow);
                        }
                    }

                    @Override
                    public void openExceptions() {
                        Bundle bundle = new Bundle();
                        bundle.putLong("dialog_id", did);
                        TopicsNotifySettingsFragments notifySettings = new TopicsNotifySettingsFragments(bundle);
                        notifySettings.setExceptions(notificationsExceptionTopics);
                        presentFragment(notifySettings);
                    }
                }, getResourceProvider());
                chatNotificationsPopupWrapper.update(did, topicId, notificationsExceptionTopics);
                if (AndroidUtilities.isTablet()) {
                    View v = parentLayout.getView();
                    x += v.getX() + v.getPaddingLeft();
                    y += v.getY() + v.getPaddingTop();
                }
                chatNotificationsPopupWrapper.showAsOptions(ProfileActivity.this, view, x, y);
            } else if (position == unblockRow) {
                getMessagesController().unblockPeer(userId);
                if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                    BulletinFactory.createBanBulletin(ProfileActivity.this, false).show();
                }
            } else if (position == addToGroupButtonRow) {
                try {
                    actionBar.getActionBarMenuOnItemClick().onItemClick(invite_to_group);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } else if (position == sendMessageRow) {
                onWriteButtonClick();
            } else if (position == reportRow) {
                AlertsCreator.createReportAlert(getParentActivity(), getDialogId(), 0, ProfileActivity.this, resourcesProvider, null);
            } else if (position >= membersStartRow && position < membersEndRow) {
                TLRPC.ChatParticipant participant;
                if (!sortedUsers.isEmpty()) {
                    participant = chatInfo.participants.participants.get(sortedUsers.get(position - membersStartRow));
                } else {
                    participant = chatInfo.participants.participants.get(position - membersStartRow);
                }
                onMemberClick(participant, false);
            } else if (position == addMemberRow) {
                openAddMember();
            } else if (position == usernameRow) {
                processOnClickOrPress(position, view, x, y);
            } else if (position == locationRow) {
                if (chatInfo.location instanceof TLRPC.TL_channelLocation) {
                    LocationActivity fragment = new LocationActivity(LocationActivity.LOCATION_TYPE_GROUP_VIEW);
                    fragment.setChatLocation(chatId, (TLRPC.TL_channelLocation) chatInfo.location);
                    presentFragment(fragment);
                }
            } else if (position == joinRow) {
                getMessagesController().addUserToChat(currentChat.id, getUserConfig().getCurrentUser(), 0, null, ProfileActivity.this, null);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeSearchByActiveAction);
            } else if (position == subscribersRow) {
                Bundle args = new Bundle();
                args.putLong("chat_id", chatId);
                args.putInt("type", ChatUsersActivity.TYPE_USERS);
                ChatUsersActivity fragment = new ChatUsersActivity(args);
                fragment.setInfo(chatInfo);
                presentFragment(fragment);
            } else if (position == administratorsRow) {
                Bundle args = new Bundle();
                args.putLong("chat_id", chatId);
                args.putInt("type", ChatUsersActivity.TYPE_ADMIN);
                ChatUsersActivity fragment = new ChatUsersActivity(args);
                fragment.setInfo(chatInfo);
                presentFragment(fragment);
            } else if (position == blockedUsersRow) {
                Bundle args = new Bundle();
                args.putLong("chat_id", chatId);
                args.putInt("type", ChatUsersActivity.TYPE_BANNED);
                ChatUsersActivity fragment = new ChatUsersActivity(args);
                fragment.setInfo(chatInfo);
                presentFragment(fragment);
            } else if (position == notificationRow) {
                presentFragment(new NotificationsSettingsActivity());
            } else if (position == privacyRow) {
                presentFragment(new PrivacySettingsActivity().setCurrentPassword(currentPassword));
            } else if (position == dataRow) {
                presentFragment(new DataSettingsActivity());
            } else if (position == chatRow) {
                presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC));
            } else if (position == stickersRow) {
                presentFragment(new StickersActivity(MediaDataController.TYPE_IMAGE));
            } else if (position == filtersRow) {
                presentFragment(new FiltersSetupActivity());
            } else if (position == stickersRow) {
                presentFragment(new StickersActivity(MediaDataController.TYPE_IMAGE, null));
            } else if (position == liteModeRow) {
                presentFragment(new LiteModeSettingsActivity());
            } else if (position == devicesRow) {
                presentFragment(new SessionsActivity(0));
            } else if (position == nekoRow) {
                presentFragment(new NekoSettingsActivity());
            } else if (position == questionRow) {
                Browser.openUrl(getParentActivity(), "https://t.me/NekogramX");
            } else if (position == faqRow) {
                Browser.openUrl(getParentActivity(), NekoXConfig.FAQ_URL);
            } else if (position == policyRow) {
                Browser.openUrl(getParentActivity(), "https://github.com/NekoX-Dev/NekoX/wiki/Privacy-Policy");
            } else if (position == sendLogsRow) {
                sendLogs(getParentActivity(), false);
            } else if (position == sendLastLogsRow) {
                sendLogs(getParentActivity(), true);
            } else if (position == clearLogsRow) {
                AlertDialog pro = AlertUtil.showProgress(getParentActivity());
                pro.show();
                UIUtil.runOnIoDispatcher(() -> {
                    FileUtil.delete(AndroidUtilities.getLogsDir());
                    ThreadUtil.sleep(100L);
                    LangsKt.uDismiss(pro);
                });
            } else if (position == switchBackendRow) {
                if (getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getParentActivity(), resourcesProvider);
                builder1.setMessage(LocaleController.getString("AreYouSure", R.string.AreYouSure));
                builder1.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder1.setPositiveButton(LocaleController.getString("OK", R.string.OK), (dialogInterface, i) -> {
                    SharedConfig.pushAuthKey = null;
                    SharedConfig.pushAuthKeyId = null;
                    SharedConfig.saveConfig();
                    ConnectionsManager.getInstance(currentAccount).switchBackend(false);
                });
                builder1.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder1.create());
            } else if (position == languageRow) {
                presentFragment(new LanguageSelectActivity());
            } else if (position == setUsernameRow) {

                TLRPC.User user = UserConfig.getInstance(currentAccount).getCurrentUser();

                if (user == null || TextUtils.isEmpty(user.username)) {
                    presentFragment(new ChangeUsernameActivity());
                    return;
                }
                String username = UserObject.getPublicUsername(user);
                BottomBuilder builder = new BottomBuilder(getParentActivity());
                builder.addTitle("@" + username);

                if (userId == getUserConfig().clientUserId && isQrNeedVisible()) {
                    builder.addItem(LocaleController.getString("QrCode", R.string.QrCode), R.drawable.msg_qrcode, __ -> {
                        Bundle args = new Bundle();
                        args.putLong("chat_id", chatId);
                        args.putLong("user_id", userId);
                        presentFragment(new QrActivity(args));
                        return Unit.INSTANCE;
                    });
                }

                builder.addItem(LocaleController.getString("Edit", R.string.Edit), R.drawable.msg_edit, __ -> {
                    presentFragment(new ChangeUsernameActivity());
                    return Unit.INSTANCE;
                });

                builder.addItem(LocaleController.getString("Copy", R.string.Copy), R.drawable.msg_copy, __ -> {
                    AlertUtil.copyAndAlert("@" + username);
                    return Unit.INSTANCE;
                });

                builder.addItem(LocaleController.getString("CopyLink", R.string.CopyLink), R.drawable.msg_link, __ -> {
                    AlertUtil.copyAndAlert("https://t.me/" + username);
                    return Unit.INSTANCE;
                });

                builder.show();

            } else if (position == bioRow) {
                if (userInfo != null) {
                    presentFragment(new ChangeBioActivity());
                }
            } else if (position == numberRow) {
                TLRPC.User user = UserConfig.getInstance(currentAccount).getCurrentUser();
                if (user == null || StrUtil.isBlank(user.phone)) {
                    return;
                }
                String number = PhoneFormat.getInstance().format("+" + user.phone);
                BottomBuilder builder = new BottomBuilder(getParentActivity());
                builder.addTitle(number);
                builder.addItem(LocaleController.getString("Edit", R.string.Edit), R.drawable.msg_edit, __ -> {
                    presentFragment(new ActionIntroActivity(ActionIntroActivity.ACTION_TYPE_CHANGE_PHONE_NUMBER));
                    return Unit.INSTANCE;
                });
                builder.addItem(LocaleController.getString("Call", R.string.Call), R.drawable.msg_calls, __ -> {
                    AlertUtil.call(user.phone);
                    return Unit.INSTANCE;
                });
                builder.addItem(LocaleController.getString("Copy", R.string.Copy), R.drawable.msg_copy, __ -> {
                    AlertUtil.copyAndAlert(number);
                    return Unit.INSTANCE;
                });
                builder.addItem(LocaleController.getString("ShareContact", R.string.ShareContact), R.drawable.msg_share, __ -> {
                    Bundle args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    args.putInt("dialogsType", 3);
                    args.putString("selectAlertString", LocaleController.getString("SendContactToText", R.string.SendContactToText));
                    args.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroupText", R.string.SendContactToGroupText));
                    DialogsActivity fragment = new DialogsActivity(args);
                    fragment.setDelegate(ProfileActivity.this);
                    presentFragment(fragment);
                    return Unit.INSTANCE;
                });
                builder.addItem(LocaleController.getString("Hide", R.string.Hide), R.drawable.msg_disable, __ -> {
                    hideNumber = true;
                    updateListAnimated(false);
                    return Unit.INSTANCE;
                });
                showDialog(builder.create());
            } else if (position == phoneRow) {
                final TLRPC.User user = getMessagesController().getUser(userId);
                if (user == null || StrUtil.isBlank(user.phone)) {
                    return;
                }
                String number = PhoneFormat.getInstance().format("+" + user.phone);
                BottomBuilder builder = new BottomBuilder(getParentActivity());
                builder.addTitle(number);
                builder.addItem(LocaleController.getString("Call", R.string.Call), R.drawable.msg_calls, __ -> {
                    AlertUtil.call(user.phone);
                    return Unit.INSTANCE;
                });
                builder.addItem(LocaleController.getString("Copy", R.string.Copy), R.drawable.msg_copy, __ -> {
                    AlertUtil.copyAndAlert(number);
                    return Unit.INSTANCE;
                });
                builder.addItem(LocaleController.getString("ShareContact", R.string.ShareContact), R.drawable.msg_share,
                        __ -> {
                            Bundle args = new Bundle();
                            args.putBoolean("onlySelect", true);
                            args.putInt("dialogsType", 3);
                            args.putString("selectAlertString", LocaleController.getString("SendContactToText", R.string.SendContactToText));
                            args.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroupText", R.string.SendContactToGroupText));
                            DialogsActivity fragment = new DialogsActivity(args);
                            fragment.setDelegate(ProfileActivity.this);
                            presentFragment(fragment);
                            return Unit.INSTANCE;
                        });
                builder.addItem(LocaleController.getString("Hide", R.string.Hide), R.drawable.msg_disable, __ -> {
                    hideNumber = true;
                    updateListAnimated(false);
                    return Unit.INSTANCE;
                });
                showDialog(builder.create());
            } else if (position == setAvatarRow) {
                onWriteButtonClick();
            } else if (position == versionRow) {
                TextInfoPrivacyCell cell = (TextInfoPrivacyCell) view;

                BottomBuilder builder = new BottomBuilder(getParentActivity());
                String message = cell.getTextView().getText().toString();
                try {
                    if (!BuildVars.isMini) {
                        message += "\n" + Libv2ray.checkVersionX()
                                .replace("Lib", "AndroidLibV2rayLite")
                                .replace("Core", "v2ray-core");
                    }
                } catch (Exception ignored) {
                }
                builder.addTitle(message);
                String finalMessage = message;
                builder.addItem(LocaleController.getString("Copy", R.string.Copy), R.drawable.msg_copy, (it) -> {
                    AndroidUtilities.addToClipboard(finalMessage);
                    AlertUtil.showToast(LocaleController.getString("TextCopied", R.string.TextCopied));
                    return Unit.INSTANCE;
                });
                builder.addItem(BuildVars.LOGS_ENABLED ? LocaleController.getString("DebugMenuDisableLogs", R.string.DebugMenuDisableLogs) : LocaleController.getString("DebugMenuEnableLogs", R.string.DebugMenuEnableLogs), R.drawable.baseline_bug_report_24, (it) -> {
                    BuildVars.LOGS_ENABLED = BuildVars.DEBUG_VERSION = !BuildVars.LOGS_ENABLED;
                    SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean("logsEnabled", BuildVars.LOGS_ENABLED).apply();

                    updateListAnimated(false);
                    return Unit.INSTANCE;
                });
                builder.addItem(LocaleController.getString("SwitchVersion", R.string.SwitchVersion), R.drawable.msg_retry,
                        (it) -> {
                            Browser.openUrl(ProfileActivity.this.getParentActivity(), "https://github.com/NextAlone/Nagram/releases");
                            return Unit.INSTANCE;
                        });

                if (!BuildVars.isFdroid && !BuildVars.isPlay) {
                    builder.addItem(LocaleController.getString("CheckUpdate", R.string.CheckUpdate), R.drawable.msg_search,
                            (it) -> {
                                Browser.openUrl(context, "tg://update");
                                return Unit.INSTANCE;
                            });

                    String currentChannel = " - ";
                    switch (NekoXConfig.autoUpdateReleaseChannel) {
                        case 0:
                            currentChannel += LocaleController.getString("AutoCheckUpdateOFF", R.string.AutoCheckUpdateOFF);
                            break;
                        case 1:
                            currentChannel += LocaleController.getString("AutoCheckUpdateStable", R.string.AutoCheckUpdateStable);
                            break;
                        case 2:
                            currentChannel += LocaleController.getString("AutoCheckUpdateRc", R.string.AutoCheckUpdateRc);
                            break;
                        case 3:
                            currentChannel += LocaleController.getString("AutoCheckUpdatePreview", R.string.AutoCheckUpdatePreview);
                            break;
                    }

                    builder.addItem(LocaleController.getString("AutoCheckUpdateSwitch", R.string.AutoCheckUpdateSwitch) + currentChannel, R.drawable.update_black_24, (it) -> {
                        BottomBuilder switchBuilder = new BottomBuilder(getParentActivity());
                        switchBuilder.addTitle(LocaleController.getString("AutoCheckUpdateSwitch", R.string.AutoCheckUpdateSwitch));
                        switchBuilder.addRadioItem(LocaleController.getString("AutoCheckUpdateOFF", R.string.AutoCheckUpdateOFF), NekoXConfig.autoUpdateReleaseChannel == 0, (radioButtonCell) -> {
                            NekoXConfig.setAutoUpdateReleaseChannel(0);
                            switchBuilder.doRadioCheck(radioButtonCell);
                            return Unit.INSTANCE;
                        });
                        switchBuilder.addRadioItem(LocaleController.getString("AutoCheckUpdateStable", R.string.AutoCheckUpdateStable), NekoXConfig.autoUpdateReleaseChannel == 1, (radioButtonCell) -> {
                            NekoXConfig.setAutoUpdateReleaseChannel(1);
                            switchBuilder.doRadioCheck(radioButtonCell);
                            return Unit.INSTANCE;
                        });
                        switchBuilder.addRadioItem(LocaleController.getString("AutoCheckUpdateRc", R.string.AutoCheckUpdateRc), NekoXConfig.autoUpdateReleaseChannel == 2, (radioButtonCell) -> {
                            NekoXConfig.setAutoUpdateReleaseChannel(2);
                            switchBuilder.doRadioCheck(radioButtonCell);
                            return Unit.INSTANCE;
                        });
                        switchBuilder.addRadioItem(LocaleController.getString("AutoCheckUpdatePreview", R.string.AutoCheckUpdatePreview), NekoXConfig.autoUpdateReleaseChannel == 3, (radioButtonCell) -> {
                            NekoXConfig.setAutoUpdateReleaseChannel(3);
                            switchBuilder.doRadioCheck(radioButtonCell);
                            return Unit.INSTANCE;
                        });
                        showDialog(switchBuilder.create());
                        return Unit.INSTANCE;
                    });
                }

                if (NekoXConfig.isDeveloper()) {
                    builder.addItem(LocaleController.getString("DeveloperSettings", R.string.DeveloperSettings), R.drawable.baseline_developer_mode_24, (it) -> {
                        BottomBuilder devBuilder = new BottomBuilder(ProfileActivity.this.getParentActivity());
                        devBuilder.addTitle(LocaleController.getString("DevModeTitle", R.string.DevModeTitle), LocaleController.getString("DevModeNotice", R.string.DevModeNotice));
                        devBuilder.addItem(LocaleController.getString("Continue", R.string.Continue), R.drawable.baseline_warning_24, true, (__) -> {
                            ProfileActivity.this.presentFragment(new NekoXSettingActivity());
                            return Unit.INSTANCE;
                        });
                        devBuilder.addCancelItem();
                        devBuilder.show();
                        return Unit.INSTANCE;
                    });
                }
                builder.show();
            } else if (position == premiumRow) {
                presentFragment(new PremiumPreviewFragment("settings"));
            } else {
                ProfileActivity.this.processOnClickOrPress(position, view, x, y);
            }
        });

        listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {

            private int pressCount = 0;

            @Override
            public boolean onItemClick(View view, int position) {
                if (position == versionRow) {
                    pressCount++;
                    if (pressCount >= 2 || BuildVars.DEBUG_PRIVATE_VERSION) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), resourcesProvider);
                        builder.setTitle(LocaleController.getString("DebugMenu", R.string.DebugMenu));
                        CharSequence[] items;
                        items = new CharSequence[]{
                                LocaleController.getString("DebugMenuImportContacts", R.string.DebugMenuImportContacts),
                                LocaleController.getString("DebugMenuReloadContacts", R.string.DebugMenuReloadContacts),
                                LocaleController.getString("DebugMenuResetContacts", R.string.DebugMenuResetContacts),
                                LocaleController.getString("DebugMenuResetDialogs", R.string.DebugMenuResetDialogs),
                                BuildVars.DEBUG_VERSION ? null : (BuildVars.LOGS_ENABLED ? LocaleController.getString("DebugMenuDisableLogs", R.string.DebugMenuDisableLogs) : LocaleController.getString("DebugMenuEnableLogs", R.string.DebugMenuEnableLogs)),
                                SharedConfig.inappCamera ? LocaleController.getString("DebugMenuDisableCamera", R.string.DebugMenuDisableCamera) : LocaleController.getString("DebugMenuEnableCamera", R.string.DebugMenuEnableCamera),
                                LocaleController.getString("DebugMenuClearMediaCache", R.string.DebugMenuClearMediaCache),
                                LocaleController.getString("DebugMenuCallSettings", R.string.DebugMenuCallSettings),
                                null,
                                BuildVars.DEBUG_PRIVATE_VERSION || AndroidUtilities.isStandaloneApp() ? LocaleController.getString("DebugMenuCheckAppUpdate", R.string.DebugMenuCheckAppUpdate) : null,
                                LocaleController.getString("DebugMenuReadAllDialogs", R.string.DebugMenuReadAllDialogs), /* No. 10 */
                                SharedConfig.pauseMusicOnRecord ? LocaleController.getString("DebugMenuDisablePauseMusic", R.string.DebugMenuDisablePauseMusic) : LocaleController.getString("DebugMenuEnablePauseMusic", R.string.DebugMenuEnablePauseMusic),
                                null,
                                BuildVars.DEBUG_PRIVATE_VERSION ? (SharedConfig.disableVoiceAudioEffects ? "Enable voip audio effects" : "Disable voip audio effects") : null,
//                                Build.VERSION.SDK_INT >= 21 ? (SharedConfig.noStatusBar ? "Show status bar background" : "Hide status bar background") : null,
                                null,
                                "Scan accounts", /* No. 15 */
                                BuildVars.DEBUG_PRIVATE_VERSION ? "Clean app update" : null,
                                BuildVars.DEBUG_PRIVATE_VERSION ? "Reset suggestions" : null,
                                "Reset all notification channels",
                                null,
                                BuildVars.DEBUG_PRIVATE_VERSION ? LocaleController.getString(R.string.DebugMenuClearWebViewCache) : null, /* No. 20 */
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? LocaleController.getString(SharedConfig.debugWebView ? R.string.DebugMenuDisableWebViewDebug : R.string.DebugMenuEnableWebViewDebug) : null,
                                (AndroidUtilities.isTabletInternal() && BuildVars.DEBUG_PRIVATE_VERSION) ? (SharedConfig.forceDisableTabletMode ? "Enable tablet mode" : "Disable tablet mode") : null,
                                BuildVars.DEBUG_PRIVATE_VERSION ? LocaleController.getString(SharedConfig.isFloatingDebugActive ? R.string.FloatingDebugDisable : R.string.FloatingDebugEnable) : null,
                                BuildVars.DEBUG_PRIVATE_VERSION ? "Force remove premium suggestions" : null,
                                BuildVars.DEBUG_PRIVATE_VERSION ? "Share device info" : null, /* No. 25 */
                                "Force performance class",
                                BuildVars.DEBUG_PRIVATE_VERSION && !InstantCameraView.allowBigSizeCameraDebug() ? (!SharedConfig.bigCameraForRound ? "Force big camera for round" : "Disable big camera for round") : null
                        };

                        builder.setItems(items, (dialog, which) -> {
                            if (which == 0) {
                                getUserConfig().syncContacts = true;
                                getUserConfig().saveConfig(false);
                                getContactsController().forceImportContacts();
                            } else if (which == 1) {
                                getContactsController().loadContacts(false, 0);
                            } else if (which == 2) {
                                getContactsController().resetImportedContacts();
                            } else if (which == 3) {
                                getMessagesController().forceResetDialogs();
                            } else if (which == 4) {
                                BuildVars.LOGS_ENABLED = BuildVars.DEBUG_VERSION = !BuildVars.LOGS_ENABLED;
                                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", Context.MODE_PRIVATE);
                                sharedPreferences.edit().putBoolean("logsEnabled", BuildVars.LOGS_ENABLED).apply();
                                updateListAnimated(false);
                            } else if (which == 5) {
                                SharedConfig.toggleInappCamera();
                            } else if (which == 6) {
                                getMessagesStorage().clearSentMedia();
                                SharedConfig.setNoSoundHintShowed(false);
                                SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
                                editor.remove("archivehint").remove("proximityhint").remove("archivehint_l").remove("speedhint").remove("gifhint").remove("reminderhint").remove("soundHint").remove("themehint").remove("bganimationhint").remove("filterhint").commit();
                                MessagesController.getEmojiSettings(currentAccount).edit().remove("featured_hidden").remove("emoji_featured_hidden").commit();
                                SharedConfig.textSelectionHintShows = 0;
                                SharedConfig.lockRecordAudioVideoHint = 0;
                                SharedConfig.stickersReorderingHintUsed = false;
                                SharedConfig.forwardingOptionsHintShown = false;
                                SharedConfig.messageSeenHintCount = 3;
                                SharedConfig.emojiInteractionsHintCount = 3;
                                SharedConfig.dayNightThemeSwitchHintCount = 3;
                                SharedConfig.fastScrollHintCount = 3;
                                ChatThemeController.getInstance(currentAccount).clearCache();
                                getNotificationCenter().postNotificationName(NotificationCenter.newSuggestionsAvailable);
                                RestrictedLanguagesSelectActivity.cleanup();
                            } else if (which == 7) {
                                VoIPHelper.showCallDebugSettings(getParentActivity());
                            } else if (which == 8) {
                                SharedConfig.toggleRoundCamera16to9();
                            } else if (which == 9) {
                                ((LaunchActivity) getParentActivity()).checkAppUpdate(true);
                            } else if (which == 10) {
                                getMessagesStorage().readAllDialogs(-1);
                            } else if (which == 11) {
                                SharedConfig.toggleDisableVoiceAudioEffects();
                            } else if (which == 12) {
//                                SharedConfig.toggleSmoothKeyboard();
//                                if (SharedConfig.smoothKeyboard && getParentActivity() != null) {
//                                    getParentActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//                                }
                            } else if (which == 13) {
                                SharedConfig.toggleDisableVoiceAudioEffects();
                            } else if (which == 14) {
//                                SharedConfig.toggleNoStatusBar();
//                                if (getParentActivity() != null && Build.VERSION.SDK_INT >= 21) {
//                                    if (SharedConfig.noStatusBar) {
//                                        getParentActivity().getWindow().setStatusBarColor(0);
//                                    } else {
//                                        getParentActivity().getWindow().setStatusBarColor(0x33000000);
//                                    }
//                                }
                            } else if (which == 15) {
                                SharedConfig.activeAccounts.clear();
                                int maxAccounts;

                                File filesDir = ApplicationLoader.applicationContext.getFilesDir();
                                if (new File(filesDir, "account31").isDirectory()) {
                                    maxAccounts = 32;
                                } else {
                                    maxAccounts = 16;
                                }

                                for (int i = 0; i < maxAccounts; i++) {
                                    SharedPreferences perf;
                                    if (i == 0) {
                                        perf = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", Context.MODE_PRIVATE);
                                    } else {
                                        perf = ApplicationLoader.applicationContext.getSharedPreferences("userconfig" + i, Context.MODE_PRIVATE);
                                    }
                                    if (StrUtil.isNotBlank(perf.getString("user", null))) {
                                        SharedConfig.activeAccounts.add(i);
                                    }
                                }

                                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE).edit()
                                        .putString("active_accounts", StringUtils.join(SharedConfig.activeAccounts, ","))
                                        .apply();

                                ProcessPhoenix.triggerRebirth(getParentActivity(), new Intent(getParentActivity(), LaunchActivity.class));
                            } else if (which == 16) {
                                SharedConfig.pendingAppUpdate = null;
                                SharedConfig.saveConfig();
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.appUpdateAvailable);
                            } else if (which == 17) {
                                Set<String> suggestions = getMessagesController().pendingSuggestions;
                                suggestions.add("VALIDATE_PHONE_NUMBER");
                                suggestions.add("VALIDATE_PASSWORD");
                                getNotificationCenter().postNotificationName(NotificationCenter.newSuggestionsAvailable);
                            } else if (which == 18) {
                                getNotificationsController().cleanupNotificationChannels();
                            } else if (which == 19) {
//                                SharedConfig.toggleForceRTMPStream();
                            } else if (which == 20) {
                                ApplicationLoader.applicationContext.deleteDatabase("webview.db");
                                ApplicationLoader.applicationContext.deleteDatabase("webviewCache.db");
                                WebStorage.getInstance().deleteAllData();
                            } else if (which == 21) {
                                SharedConfig.toggleDebugWebView();
                                Toast.makeText(getParentActivity(), LocaleController.getString(SharedConfig.debugWebView ? R.string.DebugMenuWebViewDebugEnabled : R.string.DebugMenuWebViewDebugDisabled), Toast.LENGTH_SHORT).show();
                            } else if (which == 22) {
                                SharedConfig.toggleForceDisableTabletMode();

                                Activity activity = AndroidUtilities.findActivity(context);
                                final PackageManager pm = activity.getPackageManager();
                                final Intent intent = pm.getLaunchIntentForPackage(activity.getPackageName());
                                activity.finishAffinity(); // Finishes all activities.
                                activity.startActivity(intent);    // Start the launch activity
                                System.exit(0);
                            } else if (which == 23) {
                                FloatingDebugController.setActive((LaunchActivity) getParentActivity(), !FloatingDebugController.isActive());
                            } else if (which == 24) {
                                getMessagesController().loadAppConfig();
                                TLRPC.TL_help_dismissSuggestion req = new TLRPC.TL_help_dismissSuggestion();
                                req.suggestion = "VALIDATE_PHONE_NUMBER";
                                req.peer = new TLRPC.TL_inputPeerEmpty();
                                getConnectionsManager().sendRequest(req, (response, error) -> {
                                    TLRPC.TL_help_dismissSuggestion req2 = new TLRPC.TL_help_dismissSuggestion();
                                    req2.suggestion = "VALIDATE_PASSWORD";
                                    req2.peer = new TLRPC.TL_inputPeerEmpty();
                                    getConnectionsManager().sendRequest(req2, (res2, err2) -> {
                                        getMessagesController().loadAppConfig();
                                    });
                                });
                            } else if (which == 25) {
                                int androidVersion = Build.VERSION.SDK_INT;
                                int cpuCount = ConnectionsManager.CPU_COUNT;
                                int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
                                long minFreqSum = 0, minFreqCount = 0;
                                long maxFreqSum = 0, maxFreqCount = 0;
                                long curFreqSum = 0, curFreqCount = 0;
                                long capacitySum = 0, capacityCount = 0;
                                StringBuilder cpusInfo = new StringBuilder();
                                for (int i = 0; i < cpuCount; i++) {
                                    Long minFreq = AndroidUtilities.getSysInfoLong("/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_min_freq");
                                    Long curFreq = AndroidUtilities.getSysInfoLong("/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_cur_freq");
                                    Long maxFreq = AndroidUtilities.getSysInfoLong("/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_max_freq");
                                    Long capacity = AndroidUtilities.getSysInfoLong("/sys/devices/system/cpu/cpu" + i + "/cpu_capacity");
                                    cpusInfo.append("#").append(i).append(" ");
                                    if (minFreq != null) {
                                        cpusInfo.append("min=").append(minFreq / 1000L).append(" ");
                                        minFreqSum += (minFreq / 1000L);
                                        minFreqCount++;
                                    }
                                    if (curFreq != null) {
                                        cpusInfo.append("cur=").append(curFreq / 1000L).append(" ");
                                        curFreqSum += (curFreq / 1000L);
                                        curFreqCount++;
                                    }
                                    if (maxFreq != null) {
                                        cpusInfo.append("max=").append(maxFreq / 1000L).append(" ");
                                        maxFreqSum += (maxFreq / 1000L);
                                        maxFreqCount++;
                                    }
                                    if (capacity != null) {
                                        cpusInfo.append("cpc=").append(capacity).append(" ");
                                        capacitySum += capacity;
                                        capacityCount++;
                                    }
                                    cpusInfo.append("\n");
                                }
                                StringBuilder info = new StringBuilder();
                                info.append(Build.MANUFACTURER).append(", ").append(Build.MODEL).append(" (").append(Build.PRODUCT).append(", ").append(Build.DEVICE).append(") ").append(" (android ").append(Build.VERSION.SDK_INT).append(")\n");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    info.append("SoC: ").append(Build.SOC_MANUFACTURER).append(", ").append(Build.SOC_MODEL).append("\n");
                                }
                                String gpuModel = AndroidUtilities.getSysInfoString("/sys/kernel/gpu/gpu_model");
                                if (gpuModel != null) {
                                    info.append("GPU: ").append(gpuModel);
                                    Long minClock = AndroidUtilities.getSysInfoLong("/sys/kernel/gpu/gpu_min_clock");
                                    Long mminClock = AndroidUtilities.getSysInfoLong("/sys/kernel/gpu/gpu_mm_min_clock");
                                    Long maxClock = AndroidUtilities.getSysInfoLong("/sys/kernel/gpu/gpu_max_clock");
                                    if (minClock != null) {
                                        info.append(", min=").append(minClock / 1000L);
                                    }
                                    if (mminClock != null) {
                                        info.append(", mmin=").append(mminClock / 1000L);
                                    }
                                    if (maxClock != null) {
                                        info.append(", max=").append(maxClock / 1000L);
                                    }
                                    info.append("\n");
                                }
                                ConfigurationInfo configurationInfo = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService(Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo();
                                info.append("GLES Version: ").append(configurationInfo.getGlEsVersion()).append("\n");
                                info.append("Memory: class=").append(AndroidUtilities.formatFileSize(memoryClass * 1024L * 1024L));
                                ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                                ((ActivityManager) ApplicationLoader.applicationContext.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(memoryInfo);
                                info.append(", total=").append(AndroidUtilities.formatFileSize(memoryInfo.totalMem));
                                info.append(", avail=").append(AndroidUtilities.formatFileSize(memoryInfo.availMem));
                                info.append(", low?=").append(memoryInfo.lowMemory);
                                info.append(" (threshold=").append(AndroidUtilities.formatFileSize(memoryInfo.threshold)).append(")");
                                info.append("\n");
                                info.append("Current class: ").append(SharedConfig.performanceClassName(SharedConfig.getDevicePerformanceClass())).append(", measured: ").append(SharedConfig.performanceClassName(SharedConfig.measureDevicePerformanceClass()));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    info.append(", suggest=").append(Build.VERSION.MEDIA_PERFORMANCE_CLASS);
                                }
                                info.append("\n");
                                info.append(cpuCount).append(" CPUs");
                                if (minFreqCount > 0) {
                                    info.append(", avgMinFreq=").append(minFreqSum / minFreqCount);
                                }
                                if (curFreqCount > 0) {
                                    info.append(", avgCurFreq=").append(curFreqSum / curFreqCount);
                                }
                                if (maxFreqCount > 0) {
                                    info.append(", avgMaxFreq=").append(maxFreqSum / maxFreqCount);
                                }
                                if (capacityCount > 0) {
                                    info.append(", avgCapacity=").append(capacitySum / capacityCount);
                                }
                                info.append("\n").append(cpusInfo);

                                showDialog(new ShareAlert(getParentActivity(), null, info.toString(), false, null, false) {
                                    @Override
                                    protected void onSend(LongSparseArray<TLRPC.Dialog> dids, int count, TLRPC.TL_forumTopic topic) {
                                        AndroidUtilities.runOnUIThread(() -> {
                                            BulletinFactory.createInviteSentBulletin(getParentActivity(), contentView, dids.size(), dids.size() == 1 ? dids.valueAt(0).id : 0, count, getThemedColor(Theme.key_undo_background), getThemedColor(Theme.key_undo_infoColor)).show();
                                        }, 250);
                                    }
                                });
                            } else if (which == 26) {
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity(), resourcesProvider);
                                builder2.setTitle("Force performance class");
                                int currentClass = SharedConfig.getDevicePerformanceClass();
                                int trueClass = SharedConfig.measureDevicePerformanceClass();
                                builder2.setItems(new CharSequence[] {
                                    AndroidUtilities.replaceTags((currentClass == SharedConfig.PERFORMANCE_CLASS_HIGH ? "**HIGH**" : "HIGH") + (trueClass == SharedConfig.PERFORMANCE_CLASS_HIGH ? " (measured)" : "")),
                                    AndroidUtilities.replaceTags((currentClass == SharedConfig.PERFORMANCE_CLASS_AVERAGE ? "**AVERAGE**" : "AVERAGE") + (trueClass == SharedConfig.PERFORMANCE_CLASS_AVERAGE ? " (measured)" : "")),
                                    AndroidUtilities.replaceTags((currentClass == SharedConfig.PERFORMANCE_CLASS_LOW ? "**LOW**" : "LOW") + (trueClass == SharedConfig.PERFORMANCE_CLASS_LOW ? " (measured)" : ""))
                                }, (dialog2, which2) -> {
                                    int newClass = 2 - which2;
                                    if (newClass == trueClass) {
                                        SharedConfig.overrideDevicePerformanceClass(-1);
                                    } else {
                                        SharedConfig.overrideDevicePerformanceClass(newClass);
                                    }
                                });
                                builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                builder2.show();
                            } else if (which == 27) {
                                SharedConfig.toggleRoundCamera();
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        showDialog(builder.create());
                    } else {
                        try {
                            Toast.makeText(getParentActivity(), LocaleController.getString("DebugMenuLongPress", R.string.DebugMenuLongPress), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                    return true;
                } else if (position >= membersStartRow && position < membersEndRow) {
                    final TLRPC.ChatParticipant participant;
                    if (!sortedUsers.isEmpty()) {
                        participant = visibleChatParticipants.get(sortedUsers.get(position - membersStartRow));
                    } else {
                        participant = visibleChatParticipants.get(position - membersStartRow);
                    }
                    return onMemberClick(participant, true);
                } else {
                    return processOnClickOrPress(position, view, view.getWidth() / 2f, (int) (view.getHeight() * .75f));
                }
            }
        });

        if (searchItem != null) {
            searchListView = new RecyclerListView(context);
            searchListView.setVerticalScrollBarEnabled(false);
            searchListView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            searchListView.setGlowColor(getThemedColor(Theme.key_avatar_backgroundActionBarBlue));
            searchListView.setAdapter(searchAdapter);
            searchListView.setItemAnimator(null);
            searchListView.setVisibility(View.GONE);
            searchListView.setLayoutAnimation(null);
            searchListView.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
            frameLayout.addView(searchListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
            searchListView.setOnItemClickListener((view, position) -> {
                if (position < 0) {
                    return;
                }
                Object object = numberRow;
                boolean add = true;
                if (searchAdapter.searchWas) {
                    if (position < searchAdapter.searchResults.size()) {
                        object = searchAdapter.searchResults.get(position);
                    } else {
                        position -= searchAdapter.searchResults.size() + 1;
                        if (position >= 0 && position < searchAdapter.faqSearchResults.size()) {
                            object = searchAdapter.faqSearchResults.get(position);
                        }
                    }
                } else {
                    if (!searchAdapter.recentSearches.isEmpty()) {
                        position--;
                    }
                    if (position >= 0 && position < searchAdapter.recentSearches.size()) {
                        object = searchAdapter.recentSearches.get(position);
                    } else {
                        position -= searchAdapter.recentSearches.size() + 1;
                        if (position >= 0 && position < searchAdapter.faqSearchArray.size()) {
                            object = searchAdapter.faqSearchArray.get(position);
                            add = false;
                        }
                    }
                }
                if (object instanceof SearchAdapter.SearchResult) {
                    SearchAdapter.SearchResult result = (SearchAdapter.SearchResult) object;
                    result.open();
                } else if (object instanceof MessagesController.FaqSearchResult) {
                    MessagesController.FaqSearchResult result = (MessagesController.FaqSearchResult) object;
                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.openArticle, searchAdapter.faqWebPage, result.url);
                }
                if (add && object != null) {
                    searchAdapter.addRecent(object);
                }
            });
            searchListView.setOnItemLongClickListener((view, position) -> {
                if (searchAdapter.isSearchWas() || searchAdapter.recentSearches.isEmpty()) {
                    return false;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), resourcesProvider);
                builder.setTitle(LocaleController.getString(R.string.ClearSearchAlertTitle));
                builder.setMessage(LocaleController.getString(R.string.ClearSearchAlert));
                builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton), (dialogInterface, i) -> searchAdapter.clearRecent());
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog dialog = builder.create();
                showDialog(dialog);
                TextView button = (TextView) dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed));
                }
                return true;
            });
            searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                    }
                }
            });
            searchListView.setAnimateEmptyView(true, RecyclerListView.EMPTY_VIEW_ANIMATION_TYPE_ALPHA_SCALE);

            emptyView = new StickerEmptyView(context, null, 1);
            emptyView.setAnimateLayoutChange(true);
            emptyView.subtitle.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            frameLayout.addView(emptyView);

            searchAdapter.loadFaqWebPage();
        }

        if (banFromGroup != 0) {
            TLRPC.Chat chat = getMessagesController().getChat(banFromGroup);
            if (currentChannelParticipant == null) {
                TLRPC.TL_channels_getParticipant req = new TLRPC.TL_channels_getParticipant();
                req.channel = MessagesController.getInputChannel(chat);
                req.participant = getMessagesController().getInputPeer(userId);
                getConnectionsManager().sendRequest(req, (response, error) -> {
                    if (response != null) {
                        AndroidUtilities.runOnUIThread(() -> currentChannelParticipant = ((TLRPC.TL_channels_channelParticipant) response).participant);
                    }
                });
            }
            FrameLayout frameLayout1 = new FrameLayout(context) {
                @Override
                protected void onDraw(Canvas canvas) {
                    int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                    Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                    Theme.chat_composeShadowDrawable.draw(canvas);
                    canvas.drawRect(0, bottom, getMeasuredWidth(), getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
                }
            };
            frameLayout1.setWillNotDraw(false);

            frameLayout.addView(frameLayout1, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 51, Gravity.LEFT | Gravity.BOTTOM));
            frameLayout1.setOnClickListener(v -> {
                ChatRightsEditActivity fragment = new ChatRightsEditActivity(userId, banFromGroup, null, chat.default_banned_rights, currentChannelParticipant != null ? currentChannelParticipant.banned_rights : null, "", ChatRightsEditActivity.TYPE_BANNED, true, false, null);
                fragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
                    @Override
                    public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                        removeSelfFromStack();
                    }

                    @Override
                    public void didChangeOwner(TLRPC.User user) {
                        undoView.showWithAction(-chatId, currentChat.megagroup ? UndoView.ACTION_OWNER_TRANSFERED_GROUP : UndoView.ACTION_OWNER_TRANSFERED_CHANNEL, user);
                    }
                });
                presentFragment(fragment);
            });

            TextView textView = new TextView(context);
            textView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteRedText));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setText(LocaleController.getString("BanFromTheGroup", R.string.BanFromTheGroup));
            frameLayout1.addView(textView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 0, 1, 0, 0));

            listView.setPadding(0, AndroidUtilities.dp(88), 0, AndroidUtilities.dp(48));
            listView.setBottomGlowOffset(AndroidUtilities.dp(48));
        } else {
            listView.setPadding(0, AndroidUtilities.dp(88), 0, 0);
        }

        topView = new TopView(context);
        topView.setBackgroundColor(getThemedColor(Theme.key_avatar_backgroundActionBarBlue));
        frameLayout.addView(topView);
        contentView.blurBehindViews.add(topView);

        animatedStatusView = new DrawerProfileCell.AnimatedStatusView(context, 20, 60);
        animatedStatusView.setPivotX(AndroidUtilities.dp(30));
        animatedStatusView.setPivotY(AndroidUtilities.dp(30));

        avatarContainer = new FrameLayout(context);
        avatarContainer2 = new FrameLayout(context) {

            CanvasButton canvasButton;

            @Override
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (transitionOnlineText != null) {
                    canvas.save();
                    canvas.translate(onlineTextView[0].getX(), onlineTextView[0].getY());
                    canvas.saveLayerAlpha(0, 0, transitionOnlineText.getMeasuredWidth(), transitionOnlineText.getMeasuredHeight(), (int) (255 * (1f - avatarAnimationProgress)), Canvas.ALL_SAVE_FLAG);
                    transitionOnlineText.draw(canvas);
                    canvas.restore();
                    canvas.restore();
                    invalidate();
                }
                if (hasFallbackPhoto && photoDescriptionProgress != 0 && customAvatarProgress != 1f) {
                    float cy =  onlineTextView[1].getY() + onlineTextView[1].getMeasuredHeight() / 2f;
                    float size = AndroidUtilities.dp(22);
                    float x = AndroidUtilities.dp(28) - customPhotoOffset + onlineTextView[1].getX() - size;

                    fallbackImage.setImageCoords(x, cy - size / 2f, size, size);
                    fallbackImage.setAlpha(photoDescriptionProgress);
                    canvas.save();
                    float s = photoDescriptionProgress;
                    canvas.scale(s, s, fallbackImage.getCenterX(), fallbackImage.getCenterY());
                    fallbackImage.draw(canvas);
                    canvas.restore();

                    if (customAvatarProgress == 0) {
                        if (canvasButton == null) {
                            canvasButton = new CanvasButton(this);
                            canvasButton.setDelegate(() -> {
                                if (customAvatarProgress != 1f) {
                                    avatarsViewPager.scrollToLastItem();
                                }
                            });
                        }
                        AndroidUtilities.rectTmp.set(x - AndroidUtilities.dp(4), cy - AndroidUtilities.dp(14), x + onlineTextView[2].getTextWidth() + AndroidUtilities.dp(28) * (1f - customAvatarProgress) + AndroidUtilities.dp(4), cy + AndroidUtilities.dp(14));
                        canvasButton.setRect(AndroidUtilities.rectTmp);
                        canvasButton.setRounded(true);
                        canvasButton.setColor(Color.TRANSPARENT, ColorUtils.setAlphaComponent(Color.WHITE, 50));
                        canvasButton.draw(canvas);
                    } else {
                        if (canvasButton != null) {
                            canvasButton.cancelRipple();
                        }
                    }
                }
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return (canvasButton != null && canvasButton.checkTouchEvent(ev)) || super.onInterceptTouchEvent(ev);
            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return (canvasButton != null && canvasButton.checkTouchEvent(event)) || super.onTouchEvent(event);
            }

            @Override
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                fallbackImage.onAttachedToWindow();
            }

            @Override
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                fallbackImage.onDetachedFromWindow();
            }
        };
        fallbackImage = new ImageReceiver(avatarContainer2);
        fallbackImage.setRoundRadius(AndroidUtilities.dp(11));
        AndroidUtilities.updateViewVisibilityAnimated(avatarContainer2, true, 1f, false);
        frameLayout.addView(avatarContainer2, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.START, 0, 0, 0, 0));
        avatarContainer.setPivotX(0);
        avatarContainer.setPivotY(0);
        avatarContainer2.addView(avatarContainer, LayoutHelper.createFrame(42, 42, Gravity.TOP | Gravity.LEFT, 64, 0, 0, 0));
        avatarImage = new AvatarImageView(context) {
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

            @Override
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (animatedEmojiDrawable != null && animatedEmojiDrawable.getImageReceiver() != null) {
                    animatedEmojiDrawable.getImageReceiver().startAnimation();
                }
            }
        };
        avatarImage.getImageReceiver().setAllowDecodeSingleFrame(true);
        avatarImage.setRoundRadius(getSmallAvatarRoundRadius());
        avatarImage.setPivotX(0);
        avatarImage.setPivotY(0);
        avatarContainer.addView(avatarImage, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        avatarImage.setOnClickListener(v -> {
            if (avatarBig != null) {
                return;
            }
            if (isTopic && !getMessagesController().premiumLocked) {
                ArrayList<TLRPC.TL_forumTopic> topics = getMessagesController().getTopicsController().getTopics(chatId);
                if (topics != null) {
                    TLRPC.TL_forumTopic currentTopic = null;
                    for (int i = 0; currentTopic == null && i < topics.size(); ++i) {
                        TLRPC.TL_forumTopic topic = topics.get(i);
                        if (topic != null && topic.id == topicId) {
                            currentTopic = topic;
                        }
                    }
                    if (currentTopic != null && currentTopic.icon_emoji_id != 0) {
                        long documentId = currentTopic.icon_emoji_id;
                        TLRPC.Document document = AnimatedEmojiDrawable.findDocument(currentAccount, documentId);
                        if (document == null) {
                            return;
                        }
                        Bulletin bulletin = BulletinFactory.of(ProfileActivity.this).createContainsEmojiBulletin(document, true, set -> {
                            ArrayList<TLRPC.InputStickerSet> inputSets = new ArrayList<>(1);
                            inputSets.add(set);
                            EmojiPacksAlert alert = new EmojiPacksAlert(ProfileActivity.this, getParentActivity(), resourcesProvider, inputSets);
                            showDialog(alert);
                        });
                        if (bulletin != null) {
                            bulletin.show();
                        }
                    }
                }
                return;
            }
            if (!AndroidUtilities.isTablet() && !isInLandscapeMode && avatarImage.getImageReceiver().hasNotThumb() && !AndroidUtilities.isAccessibilityScreenReaderEnabled()) {
                openingAvatar = true;
                allowPullingDown = true;
                View child = null;
                for (int i = 0; i < listView.getChildCount(); i++) {
                    if (listView.getChildAdapterPosition(listView.getChildAt(i)) == 0) {
                        child = listView.getChildAt(i);
                        break;
                    }
                }
                if (child != null) {
                    RecyclerView.ViewHolder holder = listView.findContainingViewHolder(child);
                    if (holder != null) {
                        Integer offset = positionToOffset.get(holder.getAdapterPosition());
                        if (offset != null) {
                            listView.smoothScrollBy(0, -(offset + (listView.getPaddingTop() - child.getTop() - actionBar.getMeasuredHeight())), CubicBezierInterpolator.EASE_OUT_QUINT);
                            return;
                        }
                    }
                }
            }
            openAvatar();
        });
        avatarImage.setOnLongClickListener(v -> {
            if (avatarBig != null || isTopic) {
                return false;
            }
            openAvatar();
            return false;
        });

        avatarProgressView = new RadialProgressView(context) {
            private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            {
                paint.setColor(0x55000000);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                if (avatarImage != null && avatarImage.getImageReceiver().hasNotThumb()) {
                    paint.setAlpha((int) (0x55 * avatarImage.getImageReceiver().getCurrentAlpha()));
                    canvas.drawCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, getMeasuredWidth() / 2.0f, paint);
                }
                super.onDraw(canvas);
            }
        };
        avatarProgressView.setSize(AndroidUtilities.dp(26));
        avatarProgressView.setProgressColor(0xffffffff);
        avatarProgressView.setNoProgress(false);
        avatarContainer.addView(avatarProgressView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        timeItem = new ImageView(context);
        timeItem.setPadding(AndroidUtilities.dp(10), AndroidUtilities.dp(10), AndroidUtilities.dp(5), AndroidUtilities.dp(5));
        timeItem.setScaleType(ImageView.ScaleType.CENTER);
        timeItem.setAlpha(0.0f);
        timeItem.setImageDrawable(timerDrawable = new TimerDrawable(context, null));
        timeItem.setTranslationY(-1);
        frameLayout.addView(timeItem, LayoutHelper.createFrame(34, 34, Gravity.TOP | Gravity.LEFT));

        showAvatarProgress(false, false);

        if (avatarsViewPager != null) {
            avatarsViewPager.onDestroy();
        }
        overlaysView = new OverlaysView(context);
        avatarsViewPager = new ProfileGalleryView(context, userId != 0 ? userId : -chatId, actionBar, listView, avatarImage, getClassGuid(), overlaysView) {
            @Override
            protected void setCustomAvatarProgress(float progress) {
                customAvatarProgress = progress;
                checkPhotoDescriptionAlpha();
            }
        };
        if (userId != getUserConfig().clientUserId && userInfo != null) {
            customAvatarProgress = userInfo.profile_photo == null ? 0 : 1;
        }
        if (!isTopic) {
            avatarsViewPager.setChatInfo(chatInfo);
        }
        avatarContainer2.addView(avatarsViewPager);
        avatarContainer2.addView(overlaysView);
        avatarImage.setAvatarsViewPager(avatarsViewPager);

        avatarsViewPagerIndicatorView = new PagerIndicatorView(context);
        avatarContainer2.addView(avatarsViewPagerIndicatorView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        frameLayout.addView(actionBar);

        float rightMargin = (54 + ((callItemVisible && userId != 0) ? 54 : 0));
        boolean hasTitleExpanded = false;
        int initialTitleWidth = LayoutHelper.WRAP_CONTENT;
        if (parentLayout != null && parentLayout.getLastFragment() instanceof ChatActivity) {
            ChatAvatarContainer avatarContainer = ((ChatActivity) parentLayout.getLastFragment()).getAvatarContainer();
            if (avatarContainer != null) {
                hasTitleExpanded = avatarContainer.getTitleTextView().getPaddingRight() != 0;
                if (avatarContainer.getLayoutParams() != null && avatarContainer.getTitleTextView() != null) {
                    rightMargin =
                            (((ViewGroup.MarginLayoutParams) avatarContainer.getLayoutParams()).rightMargin +
                                    (avatarContainer.getWidth() - avatarContainer.getTitleTextView().getRight())) / AndroidUtilities.density;
//                initialTitleWidth = (int) (avatarContainer.getTitleTextView().getWidth() / AndroidUtilities.density);
                }
            }
        }

        for (int a = 0; a < nameTextView.length; a++) {
            if (playProfileAnimation == 0 && a == 0) {
                continue;
            }
            nameTextView[a] = new SimpleTextView(context) {
                @Override
                public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                    super.onInitializeAccessibilityNodeInfo(info);
                    if (isFocusable() && nameTextViewRightDrawableContentDescription != null) {
                        info.setText(getText() + ", " + nameTextViewRightDrawableContentDescription);
                    }
                }
            };
            if (a == 1) {
                nameTextView[a].setTextColor(getThemedColor(Theme.key_profile_title));
            } else {
                nameTextView[a].setTextColor(getThemedColor(Theme.key_actionBarDefaultTitle));
            }
            nameTextView[a].setPadding(0, AndroidUtilities.dp(6), 0, AndroidUtilities.dp(a == 0 ? 12 : 4));
            nameTextView[a].setTextSize(18);
            nameTextView[a].setGravity(Gravity.LEFT);
            nameTextView[a].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            nameTextView[a].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
            nameTextView[a].setPivotX(0);
            nameTextView[a].setPivotY(0);
            nameTextView[a].setAlpha(a == 0 ? 0.0f : 1.0f);
            if (a == 1) {
                nameTextView[a].setScrollNonFitText(true);
                nameTextView[a].setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
                nameTextView[a].setOnLongClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setItems(new CharSequence[]{LocaleController.getString("Copy", R.string.Copy)}, (dialogInterface, i) -> {
                        if (i == 0) {
                            try {
                                AndroidUtilities.addToClipboard(((SimpleTextView) v).getText());
                                Toast.makeText(getParentActivity(), LocaleController.getString("TextCopied", R.string.TextCopied), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }
                    });
                    showDialog(builder.create());
                    return false;
                });
            }
            nameTextView[a].setFocusable(a == 0);
            nameTextView[a].setEllipsizeByGradient(true);
            nameTextView[a].setRightDrawableOutside(a == 0);
            avatarContainer2.addView(nameTextView[a], LayoutHelper.createFrame(a == 0 ? initialTitleWidth : LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 118, -6, (a == 0 ? rightMargin - (hasTitleExpanded ? 10 : 0) : 0), 0));
        }
        for (int a = 0; a < onlineTextView.length; a++) {
            if (a == 1) {
                onlineTextView[a] = new LinkSpanDrawable.ClickableSmallTextView(context) {

                    @Override
                    public void setAlpha(float alpha) {
                        super.setAlpha(alpha);
                        checkPhotoDescriptionAlpha();
                    }

                    @Override
                    public void setTranslationY(float translationY) {
                        super.setTranslationY(translationY);
                        onlineTextView[2].setTranslationY(translationY);
                        onlineTextView[3].setTranslationY(translationY);
                    }

                    @Override
                    public void setTranslationX(float translationX) {
                        super.setTranslationX(translationX);
                        onlineTextView[2].setTranslationX(translationX);
                        onlineTextView[3].setTranslationX(translationX);
                    }

                    @Override
                    public void setTextColor(int color) {
                        super.setTextColor(color);
                        if (onlineTextView[2] != null) {
                            onlineTextView[2].setTextColor(color);
                            onlineTextView[3].setTextColor(color);
                        }
                    }
                };
            } else {
                onlineTextView[a] = new LinkSpanDrawable.ClickableSmallTextView(context);
            }

            onlineTextView[a].setEllipsizeByGradient(true);
            onlineTextView[a].setTextColor(getThemedColor(Theme.key_avatar_subtitleInProfileBlue));
            onlineTextView[a].setTextSize(14);
            onlineTextView[a].setGravity(Gravity.LEFT);
            onlineTextView[a].setAlpha(a == 0 ? 0.0f : 1.0f);
            if (a == 1 || a == 2 || a == 3) {
                onlineTextView[a].setPadding(AndroidUtilities.dp(4), AndroidUtilities.dp(2), AndroidUtilities.dp(4), AndroidUtilities.dp(2));
            }
            if (a > 0) {
                onlineTextView[a].setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            }
            onlineTextView[a].setFocusable(a == 0);
            avatarContainer2.addView(onlineTextView[a], LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 118 - (a == 1 || a == 2 || a == 3? 4 : 0), (a == 1 || a == 2 || a == 3 ? -2 : 0), (a == 0 ? rightMargin - (hasTitleExpanded ? 10 : 0) : 8) - (a == 1 || a == 2 || a == 3 ? 4 : 0), 0));
        }
        checkPhotoDescriptionAlpha();
        avatarContainer2.addView(animatedStatusView);

        idTextView = new SimpleTextView(context);
        idTextView.setTextColor(Theme.getColor(Theme.key_avatar_subtitleInProfileBlue));
        idTextView.setTextSize(14);
        idTextView.setGravity(Gravity.LEFT);
        idTextView.setAlpha(1.0f);

        frameLayout.addView(idTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 118, 0, 48, 0));

        mediaCounterTextView = new AudioPlayerAlert.ClippingTextViewSwitcher(context) {
            @Override
            protected TextView createTextView() {
                TextView textView = new TextView(context);
                textView.setTextColor(getThemedColor(Theme.key_player_actionBarSubtitle));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                textView.setSingleLine(true);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setGravity(Gravity.LEFT);
                return textView;
            }
        };
        mediaCounterTextView.setAlpha(0.0f);
        avatarContainer2.addView(mediaCounterTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 118, 0, 8, 0));
        updateProfileData(true);

        writeButton = new RLottieImageView(context);

        Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
        shadowDrawable.setColorFilter(new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN));
        CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable,
                Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56), getThemedColor(Theme.key_profile_actionBackground), getThemedColor(Theme.key_profile_actionPressedBackground)),
                0, 0);
        combinedDrawable.setIconSize(AndroidUtilities.dp(56), AndroidUtilities.dp(56));
        writeButton.setBackground(combinedDrawable);
        if (userId != 0) {
            if (imageUpdater != null) {
                cameraDrawable = new RLottieDrawable(R.raw.camera_outline, String.valueOf(R.raw.camera_outline), AndroidUtilities.dp(56), AndroidUtilities.dp(56), false, null);
                cellCameraDrawable = new RLottieDrawable(R.raw.camera_outline, R.raw.camera_outline + "_cell", AndroidUtilities.dp(42), AndroidUtilities.dp(42), false, null);

                writeButton.setImageResource(R.drawable.msg_edit);
                writeButton.setContentDescription(LocaleController.getString("AccDescrChangeProfilePicture", R.string.AccDescrChangeProfilePicture));
                writeButton.setPadding(AndroidUtilities.dp(2), 0, 0, AndroidUtilities.dp(2));
            } else {
                writeButton.setImageResource(R.drawable.profile_newmsg);
                writeButton.setContentDescription(LocaleController.getString("AccDescrOpenChat", R.string.AccDescrOpenChat));
            }
        } else {
            writeButton.setImageResource(R.drawable.profile_discuss);
            writeButton.setContentDescription(LocaleController.getString("ViewDiscussion", R.string.ViewDiscussion));
        }
        writeButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_profile_actionIcon), PorterDuff.Mode.SRC_IN));
        writeButton.setScaleType(ImageView.ScaleType.CENTER);

        frameLayout.addView(writeButton, LayoutHelper.createFrame(60, 60, Gravity.RIGHT | Gravity.TOP, 0, 0, 16, 0));
        writeButton.setOnClickListener(v -> {
            if (writeButton.getTag() != null) {
                return;
            }
            onWriteButtonClick();
        });
        needLayout(false);

        if (scrollTo != -1) {
            if (writeButtonTag != null) {
                writeButton.setTag(0);
                writeButton.setScaleX(0.2f);
                writeButton.setScaleY(0.2f);
                writeButton.setAlpha(0.0f);
            }
        }

        listView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                }
                if (openingAvatar && newState != RecyclerView.SCROLL_STATE_SETTLING) {
                    openingAvatar = false;
                }
                if (searchItem != null) {
                    scrolling = newState != RecyclerView.SCROLL_STATE_IDLE;
                    searchItem.setEnabled(!scrolling && !isPulledDown);
                }
                sharedMediaLayout.scrollingByUser = listView.scrollingByUser;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (fwdRestrictedHint != null) {
                    fwdRestrictedHint.hide();
                }
                checkListViewScroll();
                if (participantsMap != null && !usersEndReached && layoutManager.findLastVisibleItemPosition() > membersEndRow - 8) {
                    getChannelParticipants(false);
                }
                sharedMediaLayout.setPinnedToTop(sharedMediaLayout.getY() == 0);
            }
        });

        undoView = new UndoView(context, null, false, resourcesProvider);
        frameLayout.addView(undoView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM | Gravity.LEFT, 8, 0, 8, 8));

        expandAnimator = ValueAnimator.ofFloat(0f, 1f);
        expandAnimator.addUpdateListener(anim -> {
            final int newTop = ActionBar.getCurrentActionBarHeight() + (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
            final float value = currentExpandAnimatorValue = AndroidUtilities.lerp(expandAnimatorValues, currentExpanAnimatorFracture = anim.getAnimatedFraction());

            avatarContainer.setScaleX(avatarScale);
            avatarContainer.setScaleY(avatarScale);
            avatarContainer.setTranslationX(AndroidUtilities.lerp(avatarX, 0f, value));
            avatarContainer.setTranslationY(AndroidUtilities.lerp((float) Math.ceil(avatarY), 0f, value));
            avatarImage.setRoundRadius((int) AndroidUtilities.lerp(getSmallAvatarRoundRadius(), 0f, value));
            if (searchItem != null) {
                searchItem.setAlpha(1.0f - value);
                searchItem.setScaleY(1.0f - value);
                searchItem.setVisibility(View.VISIBLE);
                searchItem.setClickable(searchItem.getAlpha() > .5f);
                // NekoX: Move official qrItem into bottom menu when click id
//                if (qrItem != null) {
//                    float translation = AndroidUtilities.dp(48) * value;
//                    if (searchItem.getVisibility() == View.VISIBLE)
//                        translation += AndroidUtilities.dp(48);
//                    qrItem.setTranslationX(translation);
//                    avatarsViewPagerIndicatorView.setTranslationX(translation - AndroidUtilities.dp(48));
//                }
            }

            if (extraHeight > AndroidUtilities.dp(88f) && expandProgress < 0.33f) {
                refreshNameAndOnlineXY();
            }

            if (scamDrawable != null) {
                scamDrawable.setColor(ColorUtils.blendARGB(getThemedColor(Theme.key_avatar_subtitleInProfileBlue), Color.argb(179, 255, 255, 255), value));
            }

            if (lockIconDrawable != null) {
                lockIconDrawable.setColorFilter(ColorUtils.blendARGB(getThemedColor(Theme.key_chat_lockIcon), Color.WHITE, value), PorterDuff.Mode.SRC_IN);
            }

            if (verifiedCrossfadeDrawable != null) {
                verifiedCrossfadeDrawable.setProgress(value);
            }

            if (premiumCrossfadeDrawable != null) {
                premiumCrossfadeDrawable.setProgress(value);
            }

            updateEmojiStatusDrawableColor(value);

            final float k = AndroidUtilities.dpf2(8f);

            final float nameTextViewXEnd = AndroidUtilities.dpf2(18f) - nameTextView[1].getLeft();
            final float nameTextViewYEnd = newTop + extraHeight - AndroidUtilities.dpf2(38f) - nameTextView[1].getBottom();
            final float nameTextViewCx = k + nameX + (nameTextViewXEnd - nameX) / 2f;
            final float nameTextViewCy = k + nameY + (nameTextViewYEnd - nameY) / 2f;
            final float nameTextViewX = (1 - value) * (1 - value) * nameX + 2 * (1 - value) * value * nameTextViewCx + value * value * nameTextViewXEnd;
            final float nameTextViewY = (1 - value) * (1 - value) * nameY + 2 * (1 - value) * value * nameTextViewCy + value * value * nameTextViewYEnd;

            final float onlineTextViewXEnd = AndroidUtilities.dpf2(16f) - onlineTextView[1].getLeft();
            final float onlineTextViewYEnd = newTop + extraHeight - AndroidUtilities.dpf2(18f) - onlineTextView[1].getBottom();
            final float onlineTextViewCx = k + onlineX + (onlineTextViewXEnd - onlineX) / 2f;
            final float onlineTextViewCy = k + onlineY + (onlineTextViewYEnd - onlineY) / 2f;
            final float onlineTextViewX = (1 - value) * (1 - value) * onlineX + 2 * (1 - value) * value * onlineTextViewCx + value * value * onlineTextViewXEnd;
            final float onlineTextViewY = (1 - value) * (1 - value) * onlineY + 2 * (1 - value) * value * onlineTextViewCy + value * value * onlineTextViewYEnd;

            final float idTextViewXEnd = AndroidUtilities.dpf2(16f) - idTextView.getLeft();
            final float idTextViewYEnd = newTop + extraHeight - AndroidUtilities.dpf2(3f) - idTextView.getBottom();
            final float idTextViewCx = k + idX + (idTextViewXEnd - idX) / 2f;
            final float idTextViewCy = k + idY + (idTextViewYEnd - idY) / 2f;
            final float idTextViewX = (1 - value) * (1 - value) * idX + 2 * (1 - value) * value * idTextViewCx + value * value * idTextViewXEnd;
            final float idTextViewY = (1 - value) * (1 - value) * idY + 2 * (1 - value) * value * idTextViewCy + value * value * idTextViewYEnd;

            nameTextView[1].setTranslationX(nameTextViewX);
            nameTextView[1].setTranslationY(nameTextViewY);
            onlineTextView[1].setTranslationX(onlineTextViewX);
            onlineTextView[1].setTranslationY(onlineTextViewY);
            mediaCounterTextView.setTranslationX(onlineTextViewX);
            mediaCounterTextView.setTranslationY(onlineTextViewY);
            idTextView.setTranslationX(idTextViewX);
            idTextView.setTranslationY(idTextViewY);
            final Object onlineTextViewTag = onlineTextView[1].getTag();
            int statusColor;
            if (onlineTextViewTag instanceof String) {
                statusColor = getThemedColor((String) onlineTextViewTag);
            } else {
                statusColor = getThemedColor(Theme.key_avatar_subtitleInProfileBlue);
            }
            onlineTextView[1].setTextColor(ColorUtils.blendARGB(statusColor, Color.argb(179, 255, 255, 255), value));
            idTextView.setTextColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_avatar_subtitleInProfileBlue), Color.argb(179, 255, 255, 255), value));
            if (extraHeight > AndroidUtilities.dp(88f)) {
                nameTextView[1].setPivotY(AndroidUtilities.lerp(0, nameTextView[1].getMeasuredHeight(), value));
                nameTextView[1].setScaleX(AndroidUtilities.lerp(1.12f, 1.67f, value));
                nameTextView[1].setScaleY(AndroidUtilities.lerp(1.12f, 1.67f, value));
            }

            needLayoutText(Math.min(1f, extraHeight / AndroidUtilities.dp(88f)));

            nameTextView[1].setTextColor(ColorUtils.blendARGB(getThemedColor(Theme.key_profile_title), Color.WHITE, value));
            actionBar.setItemsColor(ColorUtils.blendARGB(getThemedColor(Theme.key_actionBarDefaultIcon), Color.WHITE, value), false);

            avatarImage.setForegroundAlpha(value);

            final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) avatarContainer.getLayoutParams();
            params.width = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42f), listView.getMeasuredWidth() / avatarScale, value);
            params.height = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42f), (extraHeight + newTop) / avatarScale, value);
            params.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64f), 0f, value);
            avatarContainer.requestLayout();
            setAvatarExpandProgress(anim.getAnimatedFraction());
        });
        expandAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
        expandAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                actionBar.setItemsBackgroundColor(isPulledDown ? Theme.ACTION_BAR_WHITE_SELECTOR_COLOR : getThemedColor(Theme.key_avatar_actionBarSelectorBlue), false);
                avatarImage.clearForeground();
                doNotSetForeground = false;
            }
        });
        updateRowsIds();

        updateSelectedMediaTabText();

        fwdRestrictedHint = new HintView(getParentActivity(), 9);
        fwdRestrictedHint.setAlpha(0);
        frameLayout.addView(fwdRestrictedHint, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 12, 0, 12, 0));
        sharedMediaLayout.setForwardRestrictedHint(fwdRestrictedHint);

        ViewGroup decorView;
        if (Build.VERSION.SDK_INT >= 21) {
            decorView = (ViewGroup) getParentActivity().getWindow().getDecorView();
        } else {
            decorView = frameLayout;
        }
        pinchToZoomHelper = new PinchToZoomHelper(decorView, frameLayout) {

            Paint statusBarPaint;

            @Override
            protected void invalidateViews() {
                super.invalidateViews();
                fragmentView.invalidate();
                for (int i = 0; i < avatarsViewPager.getChildCount(); i++) {
                    avatarsViewPager.getChildAt(i).invalidate();
                }
                if (writeButton != null) {
                    writeButton.invalidate();
                }
            }

            @Override
            protected void drawOverlays(Canvas canvas, float alpha, float parentOffsetX, float parentOffsetY, float clipTop, float clipBottom) {
                if (alpha > 0) {
                    AndroidUtilities.rectTmp.set(0, 0, avatarsViewPager.getMeasuredWidth(), avatarsViewPager.getMeasuredHeight() + AndroidUtilities.dp(30));
                    canvas.saveLayerAlpha(AndroidUtilities.rectTmp, (int) (255 * alpha), Canvas.ALL_SAVE_FLAG);

                    avatarContainer2.draw(canvas);

                    if (actionBar.getOccupyStatusBar() && !SharedConfig.noStatusBar) {
                        if (statusBarPaint == null) {
                            statusBarPaint = new Paint();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                statusBarPaint.setColor(getParentActivity().getWindow().getStatusBarColor());
                            } else {
                                statusBarPaint.setColor(ColorUtils.setAlphaComponent(Color.BLACK, (int) (255 * 0.2f)));
                            }
                        }
                        canvas.drawRect(actionBar.getX(), actionBar.getY(), actionBar.getX() + actionBar.getMeasuredWidth(), actionBar.getY() + AndroidUtilities.statusBarHeight, statusBarPaint);
                    }
                    canvas.save();
                    canvas.translate(actionBar.getX(), actionBar.getY());
                    actionBar.draw(canvas);
                    canvas.restore();

                    if (writeButton != null && writeButton.getVisibility() == View.VISIBLE && writeButton.getAlpha() > 0) {
                        canvas.save();
                        float s = 0.5f + 0.5f * alpha;
                        canvas.scale(s, s, writeButton.getX() + writeButton.getMeasuredWidth() / 2f, writeButton.getY() + writeButton.getMeasuredHeight() / 2f);
                        canvas.translate(writeButton.getX(), writeButton.getY());
                        writeButton.draw(canvas);
                        canvas.restore();
                    }
                    canvas.restore();
                }
            }

            @Override
            protected boolean zoomEnabled(View child, ImageReceiver receiver) {
                if (!super.zoomEnabled(child, receiver)) {
                    return false;
                }
                return listView.getScrollState() != RecyclerView.SCROLL_STATE_DRAGGING;
            }
        };
        pinchToZoomHelper.setCallback(new PinchToZoomHelper.Callback() {
            @Override
            public void onZoomStarted(MessageObject messageObject) {
                listView.cancelClickRunnables(true);
                if (sharedMediaLayout != null && sharedMediaLayout.getCurrentListView() != null) {
                    sharedMediaLayout.getCurrentListView().cancelClickRunnables(true);
                }
                topView.setBackgroundColor(ColorUtils.blendARGB(getAverageColor(pinchToZoomHelper.getPhotoImage()), getThemedColor(Theme.key_windowBackgroundWhite), 0.1f));
            }
        });
        avatarsViewPager.setPinchToZoomHelper(pinchToZoomHelper);
        scrimPaint.setAlpha(0);
        actionBarBackgroundPaint.setColor(getThemedColor(Theme.key_listSelector));
        contentView.blurBehindViews.add(sharedMediaLayout);
        updateTtlIcon();
        return fragmentView;
    }

    private void setAvatarExpandProgress(float animatedFracture) {
        final int newTop = ActionBar.getCurrentActionBarHeight() + (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
        final float value = currentExpandAnimatorValue = AndroidUtilities.lerp(expandAnimatorValues, currentExpanAnimatorFracture = animatedFracture);
        checkPhotoDescriptionAlpha();
        avatarContainer.setScaleX(avatarScale);
        avatarContainer.setScaleY(avatarScale);
        avatarContainer.setTranslationX(AndroidUtilities.lerp(avatarX, 0f, value));
        avatarContainer.setTranslationY(AndroidUtilities.lerp((float) Math.ceil(avatarY), 0f, value));
        avatarImage.setRoundRadius((int) AndroidUtilities.lerp(getSmallAvatarRoundRadius(), 0f, value));
        if (searchItem != null) {
            searchItem.setAlpha(1.0f - value);
            searchItem.setScaleY(1.0f - value);
            searchItem.setVisibility(View.VISIBLE);
            searchItem.setClickable(searchItem.getAlpha() > .5f);
//            if (qrItem != null) {
//                float translation = AndroidUtilities.dp(48) * value;
////                    if (searchItem.getVisibility() == View.VISIBLE)
////                        translation += AndroidUtilities.dp(48);
//                qrItem.setTranslationX(translation);
//                avatarsViewPagerIndicatorView.setTranslationX(translation - AndroidUtilities.dp(48));
//            }
        }

        if (extraHeight > AndroidUtilities.dp(88f) && expandProgress < 0.33f) {
            refreshNameAndOnlineXY();
        }

        if (scamDrawable != null) {
            scamDrawable.setColor(ColorUtils.blendARGB(getThemedColor(Theme.key_avatar_subtitleInProfileBlue), Color.argb(179, 255, 255, 255), value));
        }

        if (lockIconDrawable != null) {
            lockIconDrawable.setColorFilter(ColorUtils.blendARGB(getThemedColor(Theme.key_chat_lockIcon), Color.WHITE, value), PorterDuff.Mode.MULTIPLY);
        }

        if (verifiedCrossfadeDrawable != null) {
            verifiedCrossfadeDrawable.setProgress(value);
        }

        if (premiumCrossfadeDrawable != null) {
            premiumCrossfadeDrawable.setProgress(value);
        }

        updateEmojiStatusDrawableColor(value);

        final float k = AndroidUtilities.dpf2(8f);

        final float nameTextViewXEnd = AndroidUtilities.dpf2(18f) - nameTextView[1].getLeft();
        final float nameTextViewYEnd = newTop + extraHeight - AndroidUtilities.dpf2(38f) - nameTextView[1].getBottom();
        final float nameTextViewCx = k + nameX + (nameTextViewXEnd - nameX) / 2f;
        final float nameTextViewCy = k + nameY + (nameTextViewYEnd - nameY) / 2f;
        final float nameTextViewX = (1 - value) * (1 - value) * nameX + 2 * (1 - value) * value * nameTextViewCx + value * value * nameTextViewXEnd;
        final float nameTextViewY = (1 - value) * (1 - value) * nameY + 2 * (1 - value) * value * nameTextViewCy + value * value * nameTextViewYEnd;

        final float onlineTextViewXEnd = AndroidUtilities.dpf2(16f) - onlineTextView[1].getLeft();
        final float onlineTextViewYEnd = newTop + extraHeight - AndroidUtilities.dpf2(18f) - onlineTextView[1].getBottom();
        final float onlineTextViewCx = k + onlineX + (onlineTextViewXEnd - onlineX) / 2f;
        final float onlineTextViewCy = k + onlineY + (onlineTextViewYEnd - onlineY) / 2f;
        final float onlineTextViewX = (1 - value) * (1 - value) * onlineX + 2 * (1 - value) * value * onlineTextViewCx + value * value * onlineTextViewXEnd;
        final float onlineTextViewY = (1 - value) * (1 - value) * onlineY + 2 * (1 - value) * value * onlineTextViewCy + value * value * onlineTextViewYEnd;

        nameTextView[1].setTranslationX(nameTextViewX);
        nameTextView[1].setTranslationY(nameTextViewY);
        onlineTextView[1].setTranslationX(onlineTextViewX + customPhotoOffset);
        onlineTextView[1].setTranslationY(onlineTextViewY);
        mediaCounterTextView.setTranslationX(onlineTextViewX);
        mediaCounterTextView.setTranslationY(onlineTextViewY);
        final Object onlineTextViewTag = onlineTextView[1].getTag();
        int statusColor;
        if (onlineTextViewTag instanceof String) {
            statusColor = getThemedColor((String) onlineTextViewTag);
        } else {
            statusColor = getThemedColor(Theme.key_avatar_subtitleInProfileBlue);
        }
        onlineTextView[1].setTextColor(ColorUtils.blendARGB(statusColor, Color.argb(179, 255, 255, 255), value));
        if (extraHeight > AndroidUtilities.dp(88f)) {
            nameTextView[1].setPivotY(AndroidUtilities.lerp(0, nameTextView[1].getMeasuredHeight(), value));
            nameTextView[1].setScaleX(AndroidUtilities.lerp(1.12f, 1.67f, value));
            nameTextView[1].setScaleY(AndroidUtilities.lerp(1.12f, 1.67f, value));
        }

        needLayoutText(Math.min(1f, extraHeight / AndroidUtilities.dp(88f)));

        nameTextView[1].setTextColor(ColorUtils.blendARGB(getThemedColor(Theme.key_profile_title), Color.WHITE, value));
        actionBar.setItemsColor(ColorUtils.blendARGB(getThemedColor(Theme.key_actionBarDefaultIcon), Color.WHITE, value), false);
        actionBar.setMenuOffsetSuppressed(true);

        avatarImage.setForegroundAlpha(value);

        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) avatarContainer.getLayoutParams();
        params.width = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42f), listView.getMeasuredWidth() / avatarScale, value);
        params.height = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42f), (extraHeight + newTop) / avatarScale, value);
        params.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64f), 0f, value);
        avatarContainer.requestLayout();
    }

    private int getSmallAvatarRoundRadius() {
        if (chatId != 0) {
            TLRPC.Chat chatLocal = getMessagesController().getChat(chatId);
            if (ChatObject.isForum(chatLocal)) {
                return AndroidUtilities.dp(16);
            }
        }
        return AndroidUtilities.dp(21);
    }

    private void updateTtlIcon() {
        if (ttlIconView == null) {
            return;
        }
        boolean visible = false;
        if (currentEncryptedChat == null) {
            if (userInfo != null && userInfo.ttl_period > 0) {
                visible = true;
            } else if (chatInfo != null && ChatObject.canUserDoAdminAction(currentChat, ChatObject.ACTION_DELETE_MESSAGES) && chatInfo.ttl_period > 0) {
                visible = true;
            }
        }
        AndroidUtilities.updateViewVisibilityAnimated(ttlIconView, visible, 0.8f, fragmentOpened);
    }

    public long getDialogId() {
        if (dialogId != 0) {
            return dialogId;
        } else if (userId != 0) {
            return userId;
        } else {
            return -chatId;
        }
    }

    public void getEmojiStatusLocation(Rect rect) {
        if (nameTextView[1] == null) {
            return;
        }
        if (nameTextView[1].getRightDrawable() == null) {
            rect.set(nameTextView[1].getWidth() - 1, nameTextView[1].getHeight() / 2 - 1, nameTextView[1].getWidth() + 1, nameTextView[1].getHeight() / 2 + 1);
            return;
        }
        rect.set(nameTextView[1].getRightDrawable().getBounds());
        rect.offset((int) (rect.centerX() * (nameTextView[1].getScaleX() - 1f)), 0);
        rect.offset((int) nameTextView[1].getX(), (int) nameTextView[1].getY());
    }

    public void goToForum() {
        if (getParentLayout() != null && getParentLayout().getFragmentStack() != null) {
            for (int i = 0; i < getParentLayout().getFragmentStack().size(); ++i) {
                BaseFragment fragment = getParentLayout().getFragmentStack().get(i);
                if (fragment instanceof DialogsActivity) {
                    if (((DialogsActivity) fragment).rightSlidingDialogContainer != null) {
                        BaseFragment previewFragment = ((DialogsActivity) fragment).rightSlidingDialogContainer.getFragment();
                        if (previewFragment instanceof TopicsFragment && ((TopicsFragment) previewFragment).getDialogId() == getDialogId()) {
                            ((DialogsActivity) fragment).rightSlidingDialogContainer.finishPreview();
                        }
                    }
                } else if (fragment instanceof ChatActivity) {
                    if (((ChatActivity) fragment).getDialogId() == getDialogId()) {
                        getParentLayout().removeFragmentFromStack(fragment);
                        i--;
                    }
                } else if (fragment instanceof TopicsFragment) {
                    if (((TopicsFragment) fragment).getDialogId() == getDialogId()) {
                        getParentLayout().removeFragmentFromStack(fragment);
                        i--;
                    }
                } else if (fragment instanceof ProfileActivity) {
                    if (fragment != this && ((ProfileActivity) fragment).getDialogId() == getDialogId() && ((ProfileActivity) fragment).isTopic) {
                        getParentLayout().removeFragmentFromStack(fragment);
                        i--;
                    }
                }
            }
        }

        playProfileAnimation = 0;

        Bundle args = new Bundle();
        args.putLong("chat_id", chatId);
        presentFragment(new TopicsFragment(args));
    }

    private SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialog;
    public void showStatusSelect() {
        if (selectAnimatedEmojiDialog != null) {
            return;
        }
        final SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] popup = new SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[1];
        int xoff, yoff;
        getEmojiStatusLocation(AndroidUtilities.rectTmp2);
        int topMarginDp = nameTextView[1].getScaleX() < 1.5f ? 16 : 32;
        yoff = -(avatarContainer2.getHeight() - AndroidUtilities.rectTmp2.centerY()) - AndroidUtilities.dp(topMarginDp);
        int popupWidth = (int) Math.min(AndroidUtilities.dp(340 - 16), AndroidUtilities.displaySize.x * .95f);
        int ecenter = AndroidUtilities.rectTmp2.centerX();
        xoff = MathUtils.clamp(ecenter - popupWidth / 2, 0, AndroidUtilities.displaySize.x - popupWidth);
        ecenter -= xoff;
        boolean hasEmoji = emojiStatusDrawable[1] != null && emojiStatusDrawable[1].getDrawable() instanceof AnimatedEmojiDrawable;
        SelectAnimatedEmojiDialog popupLayout = new SelectAnimatedEmojiDialog(this, getContext(), true, Math.max(0, ecenter), SelectAnimatedEmojiDialog.TYPE_EMOJI_STATUS, resourcesProvider, topMarginDp) {
            @Override
            protected void onEmojiSelected(View emojiView, Long documentId, TLRPC.Document document, Integer until) {
                TLRPC.TL_account_updateEmojiStatus req = new TLRPC.TL_account_updateEmojiStatus();
                if (documentId == null) {
                    req.emoji_status = new TLRPC.TL_emojiStatusEmpty();
                } else if (until != null) {
                    req.emoji_status = new TLRPC.TL_emojiStatusUntil();
                    ((TLRPC.TL_emojiStatusUntil) req.emoji_status).document_id = documentId;
                    ((TLRPC.TL_emojiStatusUntil) req.emoji_status).until = until;
                } else {
                    req.emoji_status = new TLRPC.TL_emojiStatus();
                    ((TLRPC.TL_emojiStatus) req.emoji_status).document_id = documentId;
                }
                TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(UserConfig.getInstance(currentAccount).getClientUserId());
                if (user != null) {
                    user.emoji_status = req.emoji_status;
                    MessagesController.getInstance(currentAccount).updateEmojiStatusUntilUpdate(user.id, user.emoji_status);
                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.userEmojiStatusUpdated, user);
                }
                for (int a = 0; a < 2; ++a) {
                    if (emojiStatusDrawable[a] != null) {
                        if (documentId == null) {
                            emojiStatusDrawable[a].set(getPremiumCrossfadeDrawable(), true);
                        } else {
                            emojiStatusDrawable[a].set(documentId, true);
                        }
                    }
                }
                if (documentId != null) {
                    animatedStatusView.animateChange(ReactionsLayoutInBubble.VisibleReaction.fromCustomEmoji(documentId));
                }
                updateEmojiStatusDrawableColor();
                updateEmojiStatusEffectPosition();
                ConnectionsManager.getInstance(currentAccount).sendRequest(req, (res, err) -> {
                    if (!(res instanceof TLRPC.TL_boolTrue)) {
                        // TODO: reject
                    }
                });
                if (popup[0] != null) {
                    selectAnimatedEmojiDialog = null;
                    popup[0].dismiss();
                }
            }
        };
        TLRPC.User user = getMessagesController().getUser(userId);
        if (user != null && user.emoji_status instanceof TLRPC.TL_emojiStatusUntil && ((TLRPC.TL_emojiStatusUntil) user.emoji_status).until > (int) (System.currentTimeMillis() / 1000)) {
            popupLayout.setExpireDateHint(((TLRPC.TL_emojiStatusUntil) user.emoji_status).until);
        }
        popupLayout.setSelected(emojiStatusDrawable[1] != null && emojiStatusDrawable[1].getDrawable() instanceof AnimatedEmojiDrawable ? ((AnimatedEmojiDrawable) emojiStatusDrawable[1].getDrawable()).getDocumentId() : null);
        popupLayout.setSaveState(3);
        popupLayout.setScrimDrawable(emojiStatusDrawable[1], nameTextView[1]);
        popup[0] = selectAnimatedEmojiDialog = new SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow(popupLayout, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT) {
            @Override
            public void dismiss() {
                super.dismiss();
                selectAnimatedEmojiDialog = null;
            }
        };
        int[] loc = new int[2];
        if (nameTextView[1] != null) {
            nameTextView[1].getLocationOnScreen(loc);
        }
        popup[0].showAsDropDown(fragmentView, xoff, yoff, Gravity.TOP | Gravity.LEFT);
        popup[0].dimBehind();
    }

    public TLRPC.Chat getCurrentChat() {
        return currentChat;
    }

    public TLRPC.UserFull getUserInfo() {
        return userInfo;
    }

    @Override
    public boolean isFragmentOpened() {
        return isFragmentOpened;
    }

    private void openAvatar() {
        if (listView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING) {
            return;
        }
        if (userId != 0) {
            TLRPC.User user = getMessagesController().getUser(userId);
            if (user.photo != null && user.photo.photo_big != null) {
                PhotoViewer.getInstance().setParentActivity(ProfileActivity.this);
                if (user.photo.dc_id != 0) {
                    user.photo.photo_big.dc_id = user.photo.dc_id;
                }
                PhotoViewer.getInstance().openPhoto(user.photo.photo_big, provider);
            } else {
                // NekoX: move openMenu from avatarImage.setOnClickListener to here.
                // avatarImage's onClick event should call this openAvatar method.
                if (userId == UserConfig.getInstance(currentAccount).getClientUserId() && imageUpdater != null) {
                    imageUpdater.openMenu(false, () -> MessagesController.getInstance(currentAccount).deleteUserPhoto(null), dialog -> {
                    }, ImageUpdater.TYPE_DEFAULT);
                }
            }
        } else if (chatId != 0) {
            TLRPC.Chat chat = getMessagesController().getChat(chatId);
            if (chat.photo != null && chat.photo.photo_big != null) {
                PhotoViewer.getInstance().setParentActivity(ProfileActivity.this);
                if (chat.photo.dc_id != 0) {
                    chat.photo.photo_big.dc_id = chat.photo.dc_id;
                }
                ImageLocation videoLocation;
                if (chatInfo != null && (chatInfo.chat_photo instanceof TLRPC.TL_photo) && !chatInfo.chat_photo.video_sizes.isEmpty()) {
                    videoLocation = ImageLocation.getForPhoto(chatInfo.chat_photo.video_sizes.get(0), chatInfo.chat_photo);
                } else {
                    videoLocation = null;
                }
                PhotoViewer.getInstance().openPhotoWithVideo(chat.photo.photo_big, videoLocation, provider);
            }
        }
    }

    private void onWriteButtonClick() {
        if (userId != 0) {
            if (imageUpdater != null) {
                presentFragment(new ChangeNameActivity(resourcesProvider));
            } else {
                if (playProfileAnimation != 0 && parentLayout.getFragmentStack().get(parentLayout.getFragmentStack().size() - 2) instanceof ChatActivity) {
                    finishFragment();
                } else {
                    TLRPC.User user = getMessagesController().getUser(userId);
                    if (user == null || user instanceof TLRPC.TL_userEmpty) {
                        return;
                    }
                    Bundle args = new Bundle();
                    args.putLong("user_id", userId);
                    if (!getMessagesController().checkCanOpenChat(args, ProfileActivity.this)) {
                        return;
                    }
                    boolean removeFragment = arguments.getBoolean("removeFragmentOnChatOpen", true);
                    if (!AndroidUtilities.isTablet() && removeFragment) {
                        getNotificationCenter().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                        getNotificationCenter().postNotificationName(NotificationCenter.closeChats);
                    }
                    int distance = getArguments().getInt("nearby_distance", -1);
                    if (distance >= 0) {
                        args.putInt("nearby_distance", distance);
                    }
                    ChatActivity chatActivity = new ChatActivity(args);
                    chatActivity.setPreloadedSticker(getMediaDataController().getGreetingsSticker(), false);
                    presentFragment(chatActivity, removeFragment);
                    if (AndroidUtilities.isTablet()) {
                        finishFragment();
                    }
                }
            }
        } else {
            openDiscussion();
        }
    }

    private void openDiscussion() {
        if (chatInfo == null || chatInfo.linked_chat_id == 0) {
            return;
        }
        Bundle args = new Bundle();
        args.putLong("chat_id", chatInfo.linked_chat_id);
        if (!getMessagesController().checkCanOpenChat(args, ProfileActivity.this)) {
            return;
        }
        presentFragment(new ChatActivity(args));
    }

    public boolean onMemberClick(TLRPC.ChatParticipant participant, boolean isLong) {
        return onMemberClick(participant, isLong, false);
    }

    public boolean onMemberClick(TLRPC.ChatParticipant participant, boolean isLong, boolean resultOnly) {
        if (getParentActivity() == null) {
            return false;
        }
        if (isLong) {
            TLRPC.User user = getMessagesController().getUser(participant.user_id);
            if (user == null || participant.user_id == getUserConfig().getClientUserId()) {
                return false;
            }
            selectedUser = participant.user_id;
            boolean allowKick;
            boolean canEditAdmin;
            boolean canRestrict;
            boolean editingAdmin;
            final TLRPC.ChannelParticipant channelParticipant;

            if (ChatObject.isChannel(currentChat)) {
                channelParticipant = ((TLRPC.TL_chatChannelParticipant) participant).channelParticipant;
                TLRPC.User u = getMessagesController().getUser(participant.user_id);
                canEditAdmin = ChatObject.canAddAdmins(currentChat);
                if (canEditAdmin && (channelParticipant instanceof TLRPC.TL_channelParticipantCreator || channelParticipant instanceof TLRPC.TL_channelParticipantAdmin && !channelParticipant.can_edit)) {
                    canEditAdmin = false;
                }
                allowKick = canRestrict = ChatObject.canBlockUsers(currentChat) && (!(channelParticipant instanceof TLRPC.TL_channelParticipantAdmin || channelParticipant instanceof TLRPC.TL_channelParticipantCreator) || channelParticipant.can_edit);
                if (currentChat.gigagroup) {
                    canRestrict = false;
                }
                editingAdmin = channelParticipant instanceof TLRPC.TL_channelParticipantAdmin;
            } else {
                channelParticipant = null;
                allowKick = currentChat.creator || participant instanceof TLRPC.TL_chatParticipant && (ChatObject.canBlockUsers(currentChat) || participant.inviter_id == getUserConfig().getClientUserId());
                canEditAdmin = currentChat.creator;
                canRestrict = currentChat.creator;
                editingAdmin = participant instanceof TLRPC.TL_chatParticipantAdmin;
            }

            ArrayList<String> items = resultOnly ? null : new ArrayList<>();
            ArrayList<Integer> icons = resultOnly ? null : new ArrayList<>();
            final ArrayList<Integer> actions = resultOnly ? null : new ArrayList<>();
            boolean hasRemove = false;

            if (canEditAdmin) {
                if (resultOnly) {
                    return true;
                }
                items.add(editingAdmin ? LocaleController.getString("EditAdminRights", R.string.EditAdminRights) : LocaleController.getString("SetAsAdmin", R.string.SetAsAdmin));
                icons.add(R.drawable.msg_admins);
                actions.add(0);
            }
            if (canRestrict) {
                if (resultOnly) {
                    return true;
                }
                items.add(LocaleController.getString("ChangePermissions", R.string.ChangePermissions));
                icons.add(R.drawable.msg_permissions);
                actions.add(1);
            }
            if (allowKick) {
                if (resultOnly) {
                    return true;
                }
                items.add(LocaleController.getString("KickFromGroup", R.string.KickFromGroup));
                icons.add(R.drawable.msg_remove);
                actions.add(2);
                hasRemove = true;
            }
            if (resultOnly) {
                return false;
            }

            if (items.isEmpty()) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), resourcesProvider);
            builder.setItems(items.toArray(new CharSequence[0]), AndroidUtilities.toIntArray(icons), (dialogInterface, i) -> {
                if (actions.get(i) == 2) {
                    kickUser(selectedUser, participant);
                } else {
                    int action = actions.get(i);
                    if (action == 1 && (channelParticipant instanceof TLRPC.TL_channelParticipantAdmin || participant instanceof TLRPC.TL_chatParticipantAdmin)) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity(), resourcesProvider);
                        builder2.setTitle(LocaleController.getString("NekoX", R.string.NekoX));
                        builder2.setMessage(LocaleController.formatString("AdminWillBeRemoved", R.string.AdminWillBeRemoved, ContactsController.formatName(user.first_name, user.last_name)));
                        builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), (dialog, which) -> {
                            if (channelParticipant != null) {
                                openRightsEdit(action, user, participant, channelParticipant.admin_rights, channelParticipant.banned_rights, channelParticipant.rank, editingAdmin);
                            } else {
                                openRightsEdit(action, user, participant, null, null, "", editingAdmin);
                            }
                        });
                        builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        showDialog(builder2.create());
                    } else {
                        if (channelParticipant != null) {
                            openRightsEdit(action, user, participant, channelParticipant.admin_rights, channelParticipant.banned_rights, channelParticipant.rank, editingAdmin);
                        } else {
                            openRightsEdit(action, user, participant, null, null, "", editingAdmin);
                        }
                    }
                }
            });
            AlertDialog alertDialog = builder.create();
            showDialog(alertDialog);
            if (hasRemove) {
                alertDialog.setItemColor(items.size() - 1, getThemedColor(Theme.key_dialogTextRed), getThemedColor(Theme.key_dialogRedIcon));
            }
        } else {
            if (participant.user_id == getUserConfig().getClientUserId()) {
                return false;
            }
            Bundle args = new Bundle();
            args.putLong("user_id", participant.user_id);
            args.putBoolean("preload_messages", true);
            presentFragment(new ProfileActivity(args));
        }
        return true;
    }

    private void openRightsEdit(int action, TLRPC.User user, TLRPC.ChatParticipant participant, TLRPC.TL_chatAdminRights adminRights, TLRPC.TL_chatBannedRights bannedRights, String rank, boolean editingAdmin) {
        boolean[] needShowBulletin = new boolean[1];
        ChatRightsEditActivity fragment = new ChatRightsEditActivity(user.id, chatId, adminRights, currentChat.default_banned_rights, bannedRights, rank, action, true, false, null, participant) {
            @Override
            public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
                if (!isOpen && backward && needShowBulletin[0] && BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                    BulletinFactory.createPromoteToAdminBulletin(ProfileActivity.this, user.first_name).show();
                }
            }
        };
        fragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            @Override
            public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                if (action == 0) {
                    if (participant instanceof TLRPC.TL_chatChannelParticipant) {
                        TLRPC.TL_chatChannelParticipant channelParticipant1 = ((TLRPC.TL_chatChannelParticipant) participant);
                        if (rights == 1) {
                            channelParticipant1.channelParticipant = new TLRPC.TL_channelParticipantAdmin();
                            channelParticipant1.channelParticipant.flags |= 4;
                        } else {
                            channelParticipant1.channelParticipant = new TLRPC.TL_channelParticipant();
                        }
                        channelParticipant1.channelParticipant.inviter_id = getUserConfig().getClientUserId();
                        channelParticipant1.channelParticipant.peer = new TLRPC.TL_peerUser();
                        channelParticipant1.channelParticipant.peer.user_id = participant.user_id;
                        channelParticipant1.channelParticipant.date = participant.date;
                        channelParticipant1.channelParticipant.banned_rights = rightsBanned;
                        channelParticipant1.channelParticipant.admin_rights = rightsAdmin;
                        channelParticipant1.channelParticipant.rank = rank;
                    } else if (participant != null) {
                        TLRPC.ChatParticipant newParticipant;
                        if (rights == 1) {
                            newParticipant = new TLRPC.TL_chatParticipantAdmin();
                        } else {
                            newParticipant = new TLRPC.TL_chatParticipant();
                        }
                        newParticipant.user_id = participant.user_id;
                        newParticipant.date = participant.date;
                        newParticipant.inviter_id = participant.inviter_id;
                        int index = chatInfo.participants.participants.indexOf(participant);
                        if (index >= 0) {
                            chatInfo.participants.participants.set(index, newParticipant);
                        }
                    }
                    if (rights == 1 && !editingAdmin) {
                        needShowBulletin[0] = true;
                    }
                } else if (action == 1) {
                    if (rights == 0) {
                        if (currentChat.megagroup && chatInfo != null && chatInfo.participants != null) {
                            boolean changed = false;
                            for (int a = 0; a < chatInfo.participants.participants.size(); a++) {
                                TLRPC.ChannelParticipant p = ((TLRPC.TL_chatChannelParticipant) chatInfo.participants.participants.get(a)).channelParticipant;
                                if (MessageObject.getPeerId(p.peer) == participant.user_id) {
                                    chatInfo.participants_count--;
                                    chatInfo.participants.participants.remove(a);
                                    changed = true;
                                    break;
                                }
                            }
                            if (chatInfo != null && chatInfo.participants != null) {
                                for (int a = 0; a < chatInfo.participants.participants.size(); a++) {
                                    TLRPC.ChatParticipant p = chatInfo.participants.participants.get(a);
                                    if (p.user_id == participant.user_id) {
                                        chatInfo.participants.participants.remove(a);
                                        changed = true;
                                        break;
                                    }
                                }
                            }
                            if (changed) {
                                updateOnlineCount(true);
                                updateRowsIds();
                                listAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            @Override
            public void didChangeOwner(TLRPC.User user) {
                undoView.showWithAction(-chatId, currentChat.megagroup ? UndoView.ACTION_OWNER_TRANSFERED_GROUP : UndoView.ACTION_OWNER_TRANSFERED_CHANNEL, user);
            }
        });
        presentFragment(fragment);
    }

    private boolean processOnClickOrPress(final int position, final View view, final float x, final float y) {
        if (position == usernameRow || position == setUsernameRow) {
            final String username;
            if (userId != 0) {
                final TLRPC.User user = getMessagesController().getUser(userId);
                String username1 = UserObject.getPublicUsername(user);
                if (user == null || username1 == null) {
                    return false;
                }
                username = username1;
            } else if (chatId != 0) {
                final TLRPC.Chat chat = getMessagesController().getChat(chatId);
                if (chat == null || !ChatObject.isPublic(chat)) {
                    return false;
                }
                username = ChatObject.getPublicUsername(chat);
            } else {
                return false;
            }

            BottomBuilder builder = new BottomBuilder(getParentActivity());
            builder.addTitle("@" + username);

            builder.addItem(LocaleController.getString("QrCode", R.string.QrCode), R.drawable.msg_qrcode, __ -> {
                Bundle args = new Bundle();
                args.putLong("chat_id", chatId);
                args.putLong("user_id", userId);
                presentFragment(new QrActivity(args));
                return Unit.INSTANCE;
            });

            if (chatInfo != null && chatInfo.can_set_username) {
                builder.addItem(LocaleController.getString("Edit", R.string.Edit), R.drawable.msg_edit, __ -> {
                    ChatEditTypeActivity fragment = new ChatEditTypeActivity(chatId, chatInfo.can_set_location);
                    fragment.setInfo(chatInfo);
                    presentFragment(fragment);
                    return Unit.INSTANCE;
                });
            }

            builder.addItem(LocaleController.getString("Copy", R.string.Copy), R.drawable.msg_copy, __ -> {
                AlertUtil.copyAndAlert("@" + username);
                return Unit.INSTANCE;
            });

            builder.addItem(LocaleController.getString("CopyLink", R.string.CopyLink), R.drawable.msg_link, __ -> {
                AlertUtil.copyAndAlert("https://t.me/" + username);
                return Unit.INSTANCE;
            });

            builder.show();
            return true;
        } else if (position == restrictionReasonRow) {
            ArrayList<TLRPC.TL_restrictionReason> reasons = new ArrayList<>();
            if (userId != 0) {
                final TLRPC.User user = getMessagesController().getUser(userId);
                if (user != null) {
                    reasons = user.restriction_reason;
                }
            } else if (currentChat != null) {
                TLRPC.Chat chat = getMessagesController().getChat(chatId);
                reasons = chat.restriction_reason;
            }
            Context context = getParentActivity();
            LinearLayout ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.VERTICAL);

            AlertDialog dialog = new AlertDialog.Builder(context, resourcesProvider)
                    .setView(ll)
                    .create();

            for (TLRPC.TL_restrictionReason reason : reasons) {
                TextDetailSettingsCell cell = new TextDetailSettingsCell(context);
                cell.setBackground(Theme.getSelectorDrawable(false));
                cell.setMultilineDetail(true);
                cell.setOnClickListener(v1 -> {
                    dialog.dismiss();
                    AndroidUtilities.addToClipboard(cell.getValueTextView().getText());
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.formatString("TextCopied", R.string.TextCopied)).show();
                });
                cell.setTextAndValue(reason.reason + "-" + reason.platform, reason.text, false);

                ll.addView(cell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            }

            showDialog(dialog);
        } else if (position == phoneRow || position == numberRow) {
            final TLRPC.User user = getMessagesController().getUser(userId);
            if (user == null || user.phone == null || user.phone.length() == 0 || getParentActivity() == null) {
                return false;
            }

            ArrayList<CharSequence> items = new ArrayList<>();
            ArrayList<Integer> actions = new ArrayList<>();
            List<Integer> icons = new ArrayList<>();
            if (position == phoneRow) {
                if (userInfo != null && userInfo.phone_calls_available) {
                    icons.add(R.drawable.msg_calls);
                    items.add(LocaleController.getString("CallViaTelegram", R.string.CallViaTelegram));
                    actions.add(PHONE_OPTION_TELEGRAM_CALL);
                    if (Build.VERSION.SDK_INT >= 18 && userInfo.video_calls_available) {
                        icons.add(R.drawable.msg_videocall);
                        items.add(LocaleController.getString("VideoCallViaTelegram", R.string.VideoCallViaTelegram));
                        actions.add(PHONE_OPTION_TELEGRAM_VIDEO_CALL);
                    }
                }
                if (!isFragmentPhoneNumber) {
                    icons.add(R.drawable.msg_calls_regular);
                    items.add(LocaleController.getString("Call", R.string.Call));
                    actions.add(PHONE_OPTION_CALL);
                }
            }
            icons.add(R.drawable.msg_copy);
            items.add(LocaleController.getString("Copy", R.string.Copy));
            actions.add(PHONE_OPTION_COPY);

            AtomicReference<ActionBarPopupWindow> popupWindowRef = new AtomicReference<>();
            ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext(), R.drawable.popup_fixed_alert, resourcesProvider) {
                Path path = new Path();

                @Override
                protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                    canvas.save();
                    path.rewind();
                    AndroidUtilities.rectTmp.set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
                    path.addRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(6), AndroidUtilities.dp(6), Path.Direction.CW);
                    canvas.clipPath(path);
                    boolean draw = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return draw;
                }
            };
            popupLayout.setFitItems(true);

            for (int i = 0; i < icons.size(); i++) {
                int action = actions.get(i);
                ActionBarMenuItem.addItem(popupLayout, icons.get(i), items.get(i), false, resourcesProvider).setOnClickListener(v -> {
                    popupWindowRef.get().dismiss();
                    switch (action) {
                        case PHONE_OPTION_CALL:
                            try {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+" + user.phone));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getParentActivity().startActivityForResult(intent, 500);
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                            break;
                        case PHONE_OPTION_COPY:
                            try {
                                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) ApplicationLoader.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                android.content.ClipData clip = android.content.ClipData.newPlainText("label", "+" + user.phone);
                                clipboard.setPrimaryClip(clip);
                                if (AndroidUtilities.shouldShowClipboardToast()) {
                                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("PhoneCopied", R.string.PhoneCopied)).show();
                                }
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                            break;
                        case PHONE_OPTION_TELEGRAM_CALL:
                        case PHONE_OPTION_TELEGRAM_VIDEO_CALL:
                            if (getParentActivity() == null) {
                                return;
                            }
                            VoIPHelper.startCall(user, action == PHONE_OPTION_TELEGRAM_VIDEO_CALL, userInfo != null && userInfo.video_calls_available, getParentActivity(), userInfo, getAccountInstance());
                            break;
                    }
                });
            }
            if (isFragmentPhoneNumber) {
                FrameLayout gap = new FrameLayout(getContext());
                gap.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuSeparator, resourcesProvider));
                popupLayout.addView(gap, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 8));

                TextView fragmentInfoView = new TextView(getContext());
                fragmentInfoView.setPadding(AndroidUtilities.dp(13), AndroidUtilities.dp(8), AndroidUtilities.dp(13), AndroidUtilities.dp(8));
                fragmentInfoView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                fragmentInfoView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, resourcesProvider));
                fragmentInfoView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText, resourcesProvider));
                fragmentInfoView.setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector, resourcesProvider), 0,6));

                SpannableStringBuilder spanned = new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.getString(R.string.AnonymousNumberNotice)));

                int startIndex = TextUtils.indexOf(spanned, '*');
                int lastIndex = TextUtils.lastIndexOf(spanned, '*');
                if (startIndex != -1 && lastIndex != -1 && startIndex != lastIndex) {
                    spanned.replace(lastIndex, lastIndex + 1, "");
                    spanned.replace(startIndex, startIndex + 1, "");
                    spanned.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM)), startIndex, lastIndex - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanned.setSpan(new ForegroundColorSpan(fragmentInfoView.getLinkTextColors().getDefaultColor()), startIndex, lastIndex - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                fragmentInfoView.setText(spanned);
                fragmentInfoView.setOnClickListener(v -> {
                    try {
                        v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://fragment.com")));
                    } catch (ActivityNotFoundException e) {
                        FileLog.e(e);
                    }
                });

                gap.setTag(R.id.fit_width_tag, 1);
                fragmentInfoView.setTag(R.id.fit_width_tag, 1);
                popupLayout.addView(fragmentInfoView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
            }

            ActionBarPopupWindow popupWindow = new ActionBarPopupWindow(popupLayout, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
            popupWindow.setPauseNotifications(true);
            popupWindow.setDismissAnimationDuration(220);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setClippingEnabled(true);
            popupWindow.setAnimationStyle(R.style.PopupContextAnimation);
            popupWindow.setFocusable(true);
            popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000), View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000), View.MeasureSpec.AT_MOST));
            popupWindow.setInputMethodMode(ActionBarPopupWindow.INPUT_METHOD_NOT_NEEDED);
            popupWindow.getContentView().setFocusableInTouchMode(true);
            popupWindowRef.set(popupWindow);

            float px = x, py = y;
            View v = view;
            while (v != getFragmentView()) {
                px += v.getX();
                py += v.getY();
                v = (View) v.getParent();
            }
            if (AndroidUtilities.isTablet()) {
                View pv = parentLayout.getView();
                px += pv.getX() + pv.getPaddingLeft();
                py += pv.getY() + pv.getPaddingTop();
            }
            px -= popupLayout.getMeasuredWidth() / 2f;
            popupWindow.showAtLocation(getFragmentView(), 0, (int) px, (int) py);
            popupWindow.dimBehind();
            return true;
        } else if (position == channelInfoRow || position == userInfoRow || position == locationRow || position == bioRow) {
            if (position == bioRow && (userInfo == null || TextUtils.isEmpty(userInfo.about))) {
                return false;
            }
            if (view instanceof AboutLinkCell && ((AboutLinkCell) view).onClick()) {
                return false;
            }
            BottomBuilder builder = new BottomBuilder(getParentActivity());
            builder.addItem(LocaleController.getString("Copy", R.string.Copy), R.drawable.msg_copy, __ -> {
                try {
                    String about;
                    if (position == locationRow) {
                        about = chatInfo != null && chatInfo.location instanceof TLRPC.TL_channelLocation ? ((TLRPC.TL_channelLocation) chatInfo.location).address : null;
                    } else if (position == channelInfoRow) {
                        about = chatInfo != null ? chatInfo.about : null;
                    } else {
                        about = userInfo != null ? userInfo.about : null;
                    }
                    if (!TextUtils.isEmpty(about)) {
                        AlertUtil.copyAndAlert(about);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                return Unit.INSTANCE;
            });
            builder.addItem(LocaleController.getString("Translate", R.string.Translate), R.drawable.ic_translate, __ -> {
                try {
                    String about;
                    if (position == locationRow) {
                        about = chatInfo != null && chatInfo.location instanceof TLRPC.TL_channelLocation ? ((TLRPC.TL_channelLocation) chatInfo.location).address : null;
                    } else if (position == channelInfoRow) {
                        about = chatInfo != null ? chatInfo.about : null;
                    } else {
                        about = userInfo != null ? userInfo.about : null;
                    }
                    if (!TextUtils.isEmpty(about)) {
                        DialogTransKt.startTrans(getParentActivity(), about);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                return Unit.INSTANCE;
            });
            builder.show();
            return !(view instanceof AboutLinkCell);
        }
        return false;
    }

    private void leaveChatPressed() {
        boolean isForum = ChatObject.isForum(currentChat);
        AlertsCreator.createClearOrDeleteDialogAlert(ProfileActivity.this, false, currentChat, null, false, isForum, !isForum, (param) -> {
            playProfileAnimation = 0;
            getNotificationCenter().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats);
            finishFragment();
            getNotificationCenter().postNotificationName(NotificationCenter.needDeleteDialog, -currentChat.id, null, currentChat, param);
        }, resourcesProvider);
    }

    private void setChannelAlias() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("setChannelAliasName", R.string.setChannelAliasName));

        final EditTextBoldCursor editText = new EditTextBoldCursor(getParentActivity()) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64), MeasureSpec.EXACTLY));
            }
        };
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        editText.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        editText.setHintText(
                LocaleController.getString("Name", R.string.Name));
        if (NekoXConfig.getChannelAlias(getCurrentChat().id) != null) {
            editText.setText(NekoXConfig.getChannelAlias(getCurrentChat().id));
        }
        editText.setHeaderHintColor(getThemedColor(Theme.key_windowBackgroundWhiteBlueHeader));
        editText.setSingleLine(true);
        editText.setFocusable(true);
        editText.setTransformHintToHeader(true);
        editText.setLineColors(getThemedColor(Theme.key_windowBackgroundWhiteInputField),
                getThemedColor(Theme.key_windowBackgroundWhiteInputFieldActivated),
                getThemedColor(Theme.key_windowBackgroundWhiteRedText3));
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setBackgroundDrawable(null);
        editText.requestFocus();
        editText.setPadding(0, 0, 0, 0);
        builder.setView(editText);

        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK),
                (dialogInterface, i) -> {
                    if (editText.getText().toString().trim().equals("")) {
                        NekoXConfig.emptyChannelAlias(getCurrentChat().id);
                    } else {
                        NekoXConfig.setChannelAlias(getCurrentChat().id, editText.getText().toString());
                    }
                });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.show().setOnShowListener(dialog -> {
            editText.requestFocus();
            AndroidUtilities.showKeyboard(editText);
        });
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) editText.getLayoutParams();
        if (layoutParams != null) {
            if (layoutParams instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) layoutParams).gravity = Gravity.CENTER_HORIZONTAL;
            }
            layoutParams.rightMargin = layoutParams.leftMargin = AndroidUtilities.dp(24);
            layoutParams.height = AndroidUtilities.dp(36);
            editText.setLayoutParams(layoutParams);
        }
        editText.setSelection(0, editText.getText().length());
    }

    private void getChannelParticipants(boolean reload) {
        if (loadingUsers || participantsMap == null || chatInfo == null) {
            return;
        }
        loadingUsers = true;
        final int delay = participantsMap.size() != 0 && reload ? 300 : 0;

        final TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
        req.channel = getMessagesController().getInputChannel(chatId);
        req.filter = new TLRPC.TL_channelParticipantsRecent();
        req.offset = reload ? 0 : participantsMap.size();
        req.limit = 200;
        int reqId = getConnectionsManager().sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> getNotificationCenter().doOnIdle(() -> {
            if (error == null) {
                TLRPC.TL_channels_channelParticipants res = (TLRPC.TL_channels_channelParticipants) response;
                getMessagesController().putUsers(res.users, false);
                getMessagesController().putChats(res.chats, false);
                if (res.users.size() < 200) {
                    usersEndReached = true;
                }
                if (req.offset == 0) {
                    participantsMap.clear();
                    chatInfo.participants = new TLRPC.TL_chatParticipants();
                    getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
                    getMessagesStorage().updateChannelUsers(chatId, res.participants);
                }
                for (int a = 0; a < res.participants.size(); a++) {
                    TLRPC.TL_chatChannelParticipant participant = new TLRPC.TL_chatChannelParticipant();
                    participant.channelParticipant = res.participants.get(a);
                    participant.inviter_id = participant.channelParticipant.inviter_id;
                    participant.user_id = MessageObject.getPeerId(participant.channelParticipant.peer);
                    participant.date = participant.channelParticipant.date;
                    if (participantsMap.indexOfKey(participant.user_id) < 0) {
                        if (chatInfo.participants == null) {
                            chatInfo.participants = new TLRPC.TL_chatParticipants();
                        }
                        chatInfo.participants.participants.add(participant);
                        participantsMap.put(participant.user_id, participant);
                    }
                }
            }
            loadingUsers = false;
            saveScrollPosition();
            updateListAnimated(true);
        }), delay));
        getConnectionsManager().bindRequestToGuid(reqId, classGuid);
    }

    private AnimatorSet headerAnimatorSet;
    private AnimatorSet headerShadowAnimatorSet;
    private float mediaHeaderAnimationProgress;
    private boolean mediaHeaderVisible;
    private Property<ActionBar, Float> ACTIONBAR_HEADER_PROGRESS = new AnimationProperties.FloatProperty<ActionBar>("avatarAnimationProgress") {
        @Override
        public void setValue(ActionBar object, float value) {
            mediaHeaderAnimationProgress = value;
            topView.invalidate();

            int color1 = getThemedColor(Theme.key_profile_title);
            int color2 = getThemedColor(Theme.key_player_actionBarTitle);
            int c = AndroidUtilities.getOffsetColor(color1, color2, value, 1.0f);
            nameTextView[1].setTextColor(c);
            if (lockIconDrawable != null) {
                lockIconDrawable.setColorFilter(c, PorterDuff.Mode.SRC_IN);
            }
            if (scamDrawable != null) {
                color1 = getThemedColor(Theme.key_avatar_subtitleInProfileBlue);
                scamDrawable.setColor(AndroidUtilities.getOffsetColor(color1, color2, value, 1.0f));
            }

            color1 = getThemedColor(Theme.key_actionBarDefaultIcon);
            color2 = getThemedColor(Theme.key_actionBarActionModeDefaultIcon);
            actionBar.setItemsColor(AndroidUtilities.getOffsetColor(color1, color2, value, 1.0f), false);

            color1 = getThemedColor(Theme.key_avatar_actionBarSelectorBlue);
            color2 = getThemedColor(Theme.key_actionBarActionModeDefaultSelector);
            actionBar.setItemsBackgroundColor(AndroidUtilities.getOffsetColor(color1, color2, value, 1.0f), false);

            topView.invalidate();
            otherItem.setIconColor(getThemedColor(Theme.key_actionBarDefaultIcon));
            callItem.setIconColor(getThemedColor(Theme.key_actionBarDefaultIcon));
            videoCallItem.setIconColor(getThemedColor(Theme.key_actionBarDefaultIcon));
            editItem.setIconColor(getThemedColor(Theme.key_actionBarDefaultIcon));

            if (verifiedDrawable != null) {
                color1 = getThemedColor(Theme.key_profile_verifiedBackground);
                color2 = getThemedColor(Theme.key_player_actionBarTitle);
                verifiedDrawable.setColorFilter(AndroidUtilities.getOffsetColor(color1, color2, value, 1.0f), PorterDuff.Mode.SRC_IN);
            }

            if (verifiedCheckDrawable != null) {
                color1 = getThemedColor(Theme.key_profile_verifiedCheck);
                color2 = getThemedColor(Theme.key_windowBackgroundWhite);
                verifiedCheckDrawable.setColorFilter(AndroidUtilities.getOffsetColor(color1, color2, value, 1.0f), PorterDuff.Mode.SRC_IN);
            }

            if (premiumStarDrawable != null) {
                color1 = getThemedColor(Theme.key_profile_verifiedBackground);
                color2 = getThemedColor(Theme.key_player_actionBarTitle);
                premiumStarDrawable.setColorFilter(AndroidUtilities.getOffsetColor(color1, color2, value, 1.0f), PorterDuff.Mode.MULTIPLY);
            }

            updateEmojiStatusDrawableColor();

            if (avatarsViewPagerIndicatorView.getSecondaryMenuItem() != null && (videoCallItemVisible || editItemVisible || callItemVisible)) {
                needLayoutText(Math.min(1f, extraHeight / AndroidUtilities.dp(88f)));
            }
        }

        @Override
        public Float get(ActionBar object) {
            return mediaHeaderAnimationProgress;
        }
    };

    private void setMediaHeaderVisible(boolean visible) {
        if (mediaHeaderVisible == visible) {
            return;
        }
        mediaHeaderVisible = visible;
        if (headerAnimatorSet != null) {
            headerAnimatorSet.cancel();
        }
        if (headerShadowAnimatorSet != null) {
            headerShadowAnimatorSet.cancel();
        }
        ActionBarMenuItem mediaSearchItem = sharedMediaLayout.getSearchItem();
        if (!mediaHeaderVisible) {
            if (callItemVisible) {
                callItem.setVisibility(View.VISIBLE);
            }
            if (videoCallItemVisible) {
                videoCallItem.setVisibility(View.VISIBLE);
            }
            if (editItemVisible) {
                editItem.setVisibility(View.VISIBLE);
            }
            otherItem.setVisibility(View.VISIBLE);
        } else {
            if (sharedMediaLayout.isSearchItemVisible()) {
                mediaSearchItem.setVisibility(View.VISIBLE);
            }
            if (sharedMediaLayout.isCalendarItemVisible()) {
                sharedMediaLayout.photoVideoOptionsItem.setVisibility(View.VISIBLE);
            } else {
                sharedMediaLayout.photoVideoOptionsItem.setVisibility(View.INVISIBLE);
            }
        }

        if (actionBar != null) {
            actionBar.createMenu().requestLayout();
        }

        ArrayList<Animator> animators = new ArrayList<>();

        animators.add(ObjectAnimator.ofFloat(callItem, View.ALPHA, visible ? 0.0f : 1.0f));
        animators.add(ObjectAnimator.ofFloat(videoCallItem, View.ALPHA, visible ? 0.0f : 1.0f));
        animators.add(ObjectAnimator.ofFloat(otherItem, View.ALPHA, visible ? 0.0f : 1.0f));
        animators.add(ObjectAnimator.ofFloat(editItem, View.ALPHA, visible ? 0.0f : 1.0f));
        animators.add(ObjectAnimator.ofFloat(callItem, View.TRANSLATION_Y, visible ? -AndroidUtilities.dp(10) : 0.0f));
        animators.add(ObjectAnimator.ofFloat(videoCallItem, View.TRANSLATION_Y, visible ? -AndroidUtilities.dp(10) : 0.0f));
        animators.add(ObjectAnimator.ofFloat(otherItem, View.TRANSLATION_Y, visible ? -AndroidUtilities.dp(10) : 0.0f));
        animators.add(ObjectAnimator.ofFloat(editItem, View.TRANSLATION_Y, visible ? -AndroidUtilities.dp(10) : 0.0f));
        animators.add(ObjectAnimator.ofFloat(mediaSearchItem, View.ALPHA, visible ? 1.0f : 0.0f));
        animators.add(ObjectAnimator.ofFloat(mediaSearchItem, View.TRANSLATION_Y, visible ? 0.0f : AndroidUtilities.dp(10)));
        animators.add(ObjectAnimator.ofFloat(sharedMediaLayout.photoVideoOptionsItem, View.ALPHA, visible ? 1.0f : 0.0f));
        animators.add(ObjectAnimator.ofFloat(sharedMediaLayout.photoVideoOptionsItem, View.TRANSLATION_Y, visible ? 0.0f : AndroidUtilities.dp(10)));
        animators.add(ObjectAnimator.ofFloat(actionBar, ACTIONBAR_HEADER_PROGRESS, visible ? 1.0f : 0.0f));
        animators.add(ObjectAnimator.ofFloat(onlineTextView[1], View.ALPHA, visible ? 0.0f : 1.0f));
        animators.add(ObjectAnimator.ofFloat(mediaCounterTextView, View.ALPHA, visible ? 1.0f : 0.0f));
        if (visible) {
            animators.add(ObjectAnimator.ofFloat(this, HEADER_SHADOW, 0.0f));
        }

        headerAnimatorSet = new AnimatorSet();
        headerAnimatorSet.playTogether(animators);
        headerAnimatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        headerAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (headerAnimatorSet != null) {
                    if (mediaHeaderVisible) {
                        if (callItemVisible) {
                            callItem.setVisibility(View.GONE);
                        }
                        if (videoCallItemVisible) {
                            videoCallItem.setVisibility(View.GONE);
                        }
                        if (editItemVisible) {
                            editItem.setVisibility(View.GONE);
                        }
                        otherItem.setVisibility(View.GONE);
                    } else {
                        if (sharedMediaLayout.isSearchItemVisible()) {
                            mediaSearchItem.setVisibility(View.VISIBLE);
                        }

                        sharedMediaLayout.photoVideoOptionsItem.setVisibility(View.INVISIBLE);

                        headerShadowAnimatorSet = new AnimatorSet();
                        headerShadowAnimatorSet.playTogether(ObjectAnimator.ofFloat(ProfileActivity.this, HEADER_SHADOW, 1.0f));
                        headerShadowAnimatorSet.setDuration(100);
                        headerShadowAnimatorSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                headerShadowAnimatorSet = null;
                            }
                        });
                        headerShadowAnimatorSet.start();
                    }
                }
                headerAnimatorSet = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                headerAnimatorSet = null;
            }
        });
        headerAnimatorSet.setDuration(150);
        headerAnimatorSet.start();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, true);
    }

    private void openAddMember() {
        Bundle args = new Bundle();
        args.putBoolean("addToGroup", true);
        args.putLong("chatId", currentChat.id);
        GroupCreateActivity fragment = new GroupCreateActivity(args);
        fragment.setInfo(chatInfo);
        if (chatInfo != null && chatInfo.participants != null) {
            LongSparseArray<TLObject> users = new LongSparseArray<>();
            for (int a = 0; a < chatInfo.participants.participants.size(); a++) {
                users.put(chatInfo.participants.participants.get(a).user_id, null);
            }
            fragment.setIgnoreUsers(users);
        }
        fragment.setDelegate((users, fwdCount) -> {
            HashSet<Long> currentParticipants = new HashSet<>();
            ArrayList<TLRPC.User> addedUsers = new ArrayList<>();
            if (chatInfo.participants.participants != null) {
                for (int i = 0; i < chatInfo.participants.participants.size(); i++) {
                    currentParticipants.add(chatInfo.participants.participants.get(i).user_id);
                }
            }
            getMessagesController().addUsersToChat(currentChat, ProfileActivity.this, users, fwdCount, user -> {
                addedUsers.add(user);
            }, restrictedUser -> {
                for (int i = 0; i < chatInfo.participants.participants.size(); i++) {
                    if (chatInfo.participants.participants.get(i).user_id == restrictedUser.id) {
                        chatInfo.participants.participants.remove(i);
                        updateListAnimated(true);
                        break;
                    }
                }
            }, () -> {
                int N = addedUsers.size();
                int[] finished = new int[1];
                for (int a = 0; a < N; a++) {
                    TLRPC.User user = addedUsers.get(a);
                    if (!currentParticipants.contains(user.id)) {
                        if (chatInfo.participants == null) {
                            chatInfo.participants = new TLRPC.TL_chatParticipants();
                        }
                        if (ChatObject.isChannel(currentChat)) {
                            TLRPC.TL_chatChannelParticipant channelParticipant1 = new TLRPC.TL_chatChannelParticipant();
                            channelParticipant1.channelParticipant = new TLRPC.TL_channelParticipant();
                            channelParticipant1.channelParticipant.inviter_id = getUserConfig().getClientUserId();
                            channelParticipant1.channelParticipant.peer = new TLRPC.TL_peerUser();
                            channelParticipant1.channelParticipant.peer.user_id = user.id;
                            channelParticipant1.channelParticipant.date = getConnectionsManager().getCurrentTime();
                            channelParticipant1.user_id = user.id;
                            chatInfo.participants.participants.add(channelParticipant1);
                        } else {
                            TLRPC.ChatParticipant participant = new TLRPC.TL_chatParticipant();
                            participant.user_id = user.id;
                            participant.inviter_id = getAccountInstance().getUserConfig().clientUserId;
                            chatInfo.participants.participants.add(participant);
                        }
                        chatInfo.participants_count++;
                        getMessagesController().putUser(user, false);
                    }
                }
                updateListAnimated(true);
            });

        });
        presentFragment(fragment);
    }

    private void checkListViewScroll() {
        if (listView.getVisibility() != View.VISIBLE) {
            return;
        }
        if (sharedMediaLayoutAttached) {
            sharedMediaLayout.setVisibleHeight(listView.getMeasuredHeight() - sharedMediaLayout.getTop());
        }

        if (listView.getChildCount() <= 0 || openAnimationInProgress) {
            return;
        }

        int newOffset = 0;
        View child = null;
        for (int i = 0; i < listView.getChildCount(); i++) {
            if (listView.getChildAdapterPosition(listView.getChildAt(i)) == 0) {
                child = listView.getChildAt(i);
                break;
            }
        }
        RecyclerListView.Holder holder = child == null ? null : (RecyclerListView.Holder) listView.findContainingViewHolder(child);
        int top = child == null ? 0 : child.getTop();
        int adapterPosition = holder != null ? holder.getAdapterPosition() : RecyclerView.NO_POSITION;
        if (top >= 0 && adapterPosition == 0) {
            newOffset = top;
        }
        boolean mediaHeaderVisible;
        boolean searchVisible = imageUpdater == null && actionBar.isSearchFieldVisible();
        if (sharedMediaRow != -1 && !searchVisible) {
            holder = (RecyclerListView.Holder) listView.findViewHolderForAdapterPosition(sharedMediaRow);
            mediaHeaderVisible = holder != null && holder.itemView.getTop() <= 0;
        } else {
            mediaHeaderVisible = searchVisible;
        }
        setMediaHeaderVisible(mediaHeaderVisible);

        if (extraHeight != newOffset && !transitionAnimationInProress) {
            extraHeight = newOffset;
            topView.invalidate();
            if (playProfileAnimation != 0) {
                allowProfileAnimation = extraHeight != 0;
            }
            needLayout(true);
        }
    }

    public void updateSelectedMediaTabText() {
        if (sharedMediaLayout == null || mediaCounterTextView == null) {
            return;
        }
        int id = sharedMediaLayout.getClosestTab();
        int[] mediaCount = sharedMediaPreloader.getLastMediaCount();
        if (id == 0) {
            if (mediaCount[MediaDataController.MEDIA_VIDEOS_ONLY] == 0 && mediaCount[MediaDataController.MEDIA_PHOTOS_ONLY] == 0) {
                mediaCounterTextView.setText(LocaleController.formatPluralString("Media", mediaCount[MediaDataController.MEDIA_PHOTOVIDEO]));
            } else if (sharedMediaLayout.getPhotosVideosTypeFilter() == SharedMediaLayout.FILTER_PHOTOS_ONLY || mediaCount[MediaDataController.MEDIA_VIDEOS_ONLY] == 0) {
                mediaCounterTextView.setText(LocaleController.formatPluralString("Photos", mediaCount[MediaDataController.MEDIA_PHOTOS_ONLY]));
            } else if (sharedMediaLayout.getPhotosVideosTypeFilter() == SharedMediaLayout.FILTER_VIDEOS_ONLY || mediaCount[MediaDataController.MEDIA_PHOTOS_ONLY] == 0) {
                mediaCounterTextView.setText(LocaleController.formatPluralString("Videos", mediaCount[MediaDataController.MEDIA_VIDEOS_ONLY]));
            } else {
                String str = String.format("%s, %s", LocaleController.formatPluralString("Photos", mediaCount[MediaDataController.MEDIA_PHOTOS_ONLY]), LocaleController.formatPluralString("Videos", mediaCount[MediaDataController.MEDIA_VIDEOS_ONLY]));
                mediaCounterTextView.setText(str);
            }
        } else if (id == 1) {
            mediaCounterTextView.setText(LocaleController.formatPluralString("Files", mediaCount[MediaDataController.MEDIA_FILE]));
        } else if (id == 2) {
            mediaCounterTextView.setText(LocaleController.formatPluralString("Voice", mediaCount[MediaDataController.MEDIA_AUDIO]));
        } else if (id == 3) {
            mediaCounterTextView.setText(LocaleController.formatPluralString("Links", mediaCount[MediaDataController.MEDIA_URL]));
        } else if (id == 4) {
            mediaCounterTextView.setText(LocaleController.formatPluralString("MusicFiles", mediaCount[MediaDataController.MEDIA_MUSIC]));
        } else if (id == 5) {
            mediaCounterTextView.setText(LocaleController.formatPluralString("GIFs", mediaCount[MediaDataController.MEDIA_GIF]));
        } else if (id == 6) {
            mediaCounterTextView.setText(LocaleController.formatPluralString("CommonGroups", userInfo.common_chats_count));
        } else if (id == 7) {
            mediaCounterTextView.setText(onlineTextView[1].getText());
        }
    }

    private void needLayout(boolean animated) {
        final int newTop = (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();

        FrameLayout.LayoutParams layoutParams;
        if (listView != null && !openAnimationInProgress) {
            layoutParams = (FrameLayout.LayoutParams) listView.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                layoutParams.topMargin = newTop;
                listView.setLayoutParams(layoutParams);
            }
        }

        if (avatarContainer != null) {
            final float diff = Math.min(1f, extraHeight / AndroidUtilities.dp(88f));

            listView.setTopGlowOffset((int) extraHeight);

            listView.setOverScrollMode(extraHeight > AndroidUtilities.dp(88f) && extraHeight < listView.getMeasuredWidth() - newTop ? View.OVER_SCROLL_NEVER : View.OVER_SCROLL_ALWAYS);

            if (writeButton != null) {
                writeButton.setTranslationY((actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight() + extraHeight + searchTransitionOffset - AndroidUtilities.dp(29.5f));

                if (!openAnimationInProgress) {
                    boolean setVisible = diff > 0.2f && !searchMode && (imageUpdater == null || setAvatarRow == -1);
                    if (setVisible && chatId != 0) {
                        setVisible = ChatObject.isChannel(currentChat) && !currentChat.megagroup && chatInfo != null && chatInfo.linked_chat_id != 0 && infoHeaderRow != -1;
                    }
                    boolean currentVisible = writeButton.getTag() == null;
                    if (setVisible != currentVisible) {
                        if (setVisible) {
                            writeButton.setTag(null);
                        } else {
                            writeButton.setTag(0);
                        }
                        if (writeButtonAnimation != null) {
                            AnimatorSet old = writeButtonAnimation;
                            writeButtonAnimation = null;
                            old.cancel();
                        }
                        if (animated) {
                            writeButtonAnimation = new AnimatorSet();
                            if (setVisible) {
                                writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
                                writeButtonAnimation.playTogether(
                                        ObjectAnimator.ofFloat(writeButton, View.SCALE_X, 1.0f),
                                        ObjectAnimator.ofFloat(writeButton, View.SCALE_Y, 1.0f),
                                        ObjectAnimator.ofFloat(writeButton, View.ALPHA, 1.0f)
                                );
                            } else {
                                writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
                                writeButtonAnimation.playTogether(
                                        ObjectAnimator.ofFloat(writeButton, View.SCALE_X, 0.2f),
                                        ObjectAnimator.ofFloat(writeButton, View.SCALE_Y, 0.2f),
                                        ObjectAnimator.ofFloat(writeButton, View.ALPHA, 0.0f)
                                );
                            }
                            writeButtonAnimation.setDuration(150);
                            writeButtonAnimation.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (writeButtonAnimation != null && writeButtonAnimation.equals(animation)) {
                                        writeButtonAnimation = null;
                                    }
                                }
                            });
                            writeButtonAnimation.start();
                        } else {
                            writeButton.setScaleX(setVisible ? 1.0f : 0.2f);
                            writeButton.setScaleY(setVisible ? 1.0f : 0.2f);
                            writeButton.setAlpha(setVisible ? 1.0f : 0.0f);
                        }
                    }

//                    if (qrItem != null) {
//
//                    }
                }
            }

            avatarX = -AndroidUtilities.dpf2(47f) * diff;
            avatarY = (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight() / 2.0f * (1.0f + diff) - 21 * AndroidUtilities.density + 27 * AndroidUtilities.density * diff + actionBar.getTranslationY();

            float h = openAnimationInProgress ? initialAnimationExtraHeight : extraHeight;
            if (h > AndroidUtilities.dp(88f) || isPulledDown) {
                expandProgress = Math.max(0f, Math.min(1f, (h - AndroidUtilities.dp(88f)) / (listView.getMeasuredWidth() - newTop - AndroidUtilities.dp(88f))));
                avatarScale = AndroidUtilities.lerp((42f + 18f) / 42f, (42f + 42f + 18f) / 42f, Math.min(1f, expandProgress * 3f));

                final float durationFactor = Math.min(AndroidUtilities.dpf2(2000f), Math.max(AndroidUtilities.dpf2(1100f), Math.abs(listViewVelocityY))) / AndroidUtilities.dpf2(1100f);

                if (allowPullingDown && (openingAvatar || expandProgress >= 0.33f)) {
                    if (!isPulledDown) {
                        /*if (otherItem != null) {
                            if (!getMessagesController().isChatNoForwards(currentChat)) {
                                otherItem.showSubItem(gallery_menu_save);
                            } else {
                                otherItem.hideSubItem(gallery_menu_save);
                            }
                            if (imageUpdater != null) {
                                otherItem.showSubItem(add_photo);
                                otherItem.showSubItem(edit_avatar);
                                otherItem.showSubItem(delete_avatar);
                                otherItem.hideSubItem(set_as_main);
                                otherItem.hideSubItem(logout);
                            }
                        }*/
                        if (searchItem != null) {
                            searchItem.setEnabled(false);
                        }
                        isPulledDown = true;
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, true);
                        overlaysView.setOverlaysVisible(true, durationFactor);
                        avatarsViewPagerIndicatorView.refreshVisibility(durationFactor);
                        avatarsViewPager.setCreateThumbFromParent(true);
                        avatarsViewPager.getAdapter().notifyDataSetChanged();
                        expandAnimator.cancel();
                        float value = AndroidUtilities.lerp(expandAnimatorValues, currentExpanAnimatorFracture);
                        expandAnimatorValues[0] = value;
                        expandAnimatorValues[1] = 1f;
                        expandAnimator.setDuration((long) ((1f - value) * 250f / durationFactor));
                        expandAnimator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                setForegroundImage(false);
                                avatarsViewPager.setAnimatedFileMaybe(avatarImage.getImageReceiver().getAnimation());
                                avatarsViewPager.resetCurrentItem();
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                expandAnimator.removeListener(this);
                                topView.setBackgroundColor(Color.BLACK);
                                avatarContainer.setVisibility(View.GONE);
                                avatarsViewPager.setVisibility(View.VISIBLE);
                            }
                        });
                        expandAnimator.start();
                    }
                    ViewGroup.LayoutParams params = avatarsViewPager.getLayoutParams();
                    params.width = listView.getMeasuredWidth();
                    params.height = (int) (h + newTop);
                    avatarsViewPager.requestLayout();
                    if (!expandAnimator.isRunning()) {
                        float additionalTranslationY = 0;
                        if (openAnimationInProgress && playProfileAnimation == 2) {
                            additionalTranslationY = -(1.0f - avatarAnimationProgress) * AndroidUtilities.dp(50);
                        }
                        onlineX = AndroidUtilities.dpf2(16f) - onlineTextView[1].getLeft();
                        nameTextView[1].setTranslationX(AndroidUtilities.dpf2(18f) - nameTextView[1].getLeft());
                        nameTextView[1].setTranslationY(newTop + h - AndroidUtilities.dpf2(38f) - nameTextView[1].getBottom() + additionalTranslationY);
                        onlineTextView[1].setTranslationX(onlineX + customPhotoOffset);
                        onlineTextView[1].setTranslationY(newTop + h - AndroidUtilities.dpf2(18f) - onlineTextView[1].getBottom() + additionalTranslationY);
                        mediaCounterTextView.setTranslationX(onlineTextView[1].getTranslationX());
                        mediaCounterTextView.setTranslationY(onlineTextView[1].getTranslationY());
                        idTextView.setTranslationX(AndroidUtilities.dpf2(16f) - onlineTextView[1].getLeft());
                        idTextView.setTranslationY(newTop + h - AndroidUtilities.dpf2(3f) - idTextView.getBottom() + additionalTranslationY);
                    }
                } else {
                    if (isPulledDown) {
                        isPulledDown = false;
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, true);
                        if (otherItem != null) {
                            otherItem.hideSubItem(gallery_menu_save);
                            if (imageUpdater != null) {
                                otherItem.hideSubItem(set_as_main);
                                otherItem.hideSubItem(edit_avatar);
                                otherItem.hideSubItem(delete_avatar);
                                otherItem.showSubItem(add_photo);
                                otherItem.showSubItem(logout);
                                otherItem.showSubItem(edit_name);
                            }
                        }
                        if (searchItem != null) {
                            searchItem.setEnabled(!scrolling);
                        }
                        overlaysView.setOverlaysVisible(false, durationFactor);
                        avatarsViewPagerIndicatorView.refreshVisibility(durationFactor);
                        expandAnimator.cancel();
                        avatarImage.getImageReceiver().setAllowStartAnimation(true);
                        avatarImage.getImageReceiver().startAnimation();

                        float value = AndroidUtilities.lerp(expandAnimatorValues, currentExpanAnimatorFracture);
                        expandAnimatorValues[0] = value;
                        expandAnimatorValues[1] = 0f;
                        if (!isInLandscapeMode) {
                            expandAnimator.setDuration((long) (value * 250f / durationFactor));
                        } else {
                            expandAnimator.setDuration(0);
                        }
                        topView.setBackgroundColor(getThemedColor(Theme.key_avatar_backgroundActionBarBlue));

                        if (!doNotSetForeground) {
                            BackupImageView imageView = avatarsViewPager.getCurrentItemView();
                            if (imageView != null) {
                                if (imageView.getImageReceiver().getDrawable() instanceof VectorAvatarThumbDrawable) {
                                    avatarImage.drawForeground(false);
                                } else {
                                    avatarImage.drawForeground(true);
                                    avatarImage.setForegroundImageDrawable(imageView.getImageReceiver().getDrawableSafe());
                                }
                            }
                        }
                        avatarImage.setForegroundAlpha(1f);
                        avatarContainer.setVisibility(View.VISIBLE);
                        avatarsViewPager.setVisibility(View.GONE);
                        expandAnimator.start();
                    }

                    avatarContainer.setScaleX(avatarScale);
                    avatarContainer.setScaleY(avatarScale);

                    if (expandAnimator == null || !expandAnimator.isRunning()) {
                        refreshNameAndOnlineXY();
                        nameTextView[1].setTranslationX(nameX);
                        nameTextView[1].setTranslationY(nameY);
                        onlineTextView[1].setTranslationX(onlineX + customPhotoOffset);
                        onlineTextView[1].setTranslationY(onlineY);
                        mediaCounterTextView.setTranslationX(onlineX);
                        mediaCounterTextView.setTranslationY(onlineY);
                        idTextView.setTranslationX(idX);
                        idTextView.setTranslationY(idY);
                    }
                }
            }

            if (openAnimationInProgress && playProfileAnimation == 2) {
                float avX = 0;
                float avY = (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight() / 2.0f - 21 * AndroidUtilities.density + actionBar.getTranslationY();

                nameTextView[0].setTranslationX(0);
                nameTextView[0].setTranslationY((float) Math.floor(avY) + AndroidUtilities.dp(1.3f));
                onlineTextView[0].setTranslationX(0);
                onlineTextView[0].setTranslationY((float) Math.floor(avY) + AndroidUtilities.dp(24));
                nameTextView[0].setScaleX(1.0f);
                nameTextView[0].setScaleY(1.0f);

                nameTextView[1].setPivotY(nameTextView[1].getMeasuredHeight());
                nameTextView[1].setScaleX(1.67f);
                nameTextView[1].setScaleY(1.67f);

                avatarScale = AndroidUtilities.lerp(1.0f, (42f + 42f + 18f) / 42f, avatarAnimationProgress);

                avatarImage.setRoundRadius((int) AndroidUtilities.lerp(getSmallAvatarRoundRadius(), 0f, avatarAnimationProgress));
                avatarContainer.setTranslationX(AndroidUtilities.lerp(avX, 0, avatarAnimationProgress));
                avatarContainer.setTranslationY(AndroidUtilities.lerp((float) Math.ceil(avY), 0f, avatarAnimationProgress));
                float extra = (avatarContainer.getMeasuredWidth() - AndroidUtilities.dp(42)) * avatarScale;
                timeItem.setTranslationX(avatarContainer.getX() + AndroidUtilities.dp(16) + extra);
                timeItem.setTranslationY(avatarContainer.getY() + AndroidUtilities.dp(15) + extra);
                avatarContainer.setScaleX(avatarScale);
                avatarContainer.setScaleY(avatarScale);

                overlaysView.setAlphaValue(avatarAnimationProgress, false);
                actionBar.setItemsColor(ColorUtils.blendARGB(getThemedColor(Theme.key_actionBarDefaultIcon), Color.WHITE, avatarAnimationProgress), false);

                if (scamDrawable != null) {
                    scamDrawable.setColor(ColorUtils.blendARGB(getThemedColor(Theme.key_avatar_subtitleInProfileBlue), Color.argb(179, 255, 255, 255), avatarAnimationProgress));
                }
                if (lockIconDrawable != null) {
                    lockIconDrawable.setColorFilter(ColorUtils.blendARGB(getThemedColor(Theme.key_chat_lockIcon), Color.WHITE, avatarAnimationProgress), PorterDuff.Mode.MULTIPLY);
                }
                if (verifiedCrossfadeDrawable != null) {
                    verifiedCrossfadeDrawable.setProgress(avatarAnimationProgress);
                    nameTextView[1].invalidate();
                }
                if (premiumCrossfadeDrawable != null) {
                    premiumCrossfadeDrawable.setProgress(avatarAnimationProgress);
                    nameTextView[1].invalidate();
                }
                updateEmojiStatusDrawableColor(avatarAnimationProgress);

                final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) avatarContainer.getLayoutParams();
                params.width = params.height = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42f), (extraHeight + newTop) / avatarScale, avatarAnimationProgress);
                params.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64f), 0f, avatarAnimationProgress);
                avatarContainer.requestLayout();
            } else if (extraHeight <= AndroidUtilities.dp(88f)) {
                avatarScale = (42 + 18 * diff) / 42.0f;
                float nameScale = 1.0f + 0.12f * diff;
                if (expandAnimator == null || !expandAnimator.isRunning()) {
                    avatarContainer.setScaleX(avatarScale);
                    avatarContainer.setScaleY(avatarScale);
                    avatarContainer.setTranslationX(avatarX);
                    avatarContainer.setTranslationY((float) Math.ceil(avatarY));
                    float extra = AndroidUtilities.dp(42) * avatarScale - AndroidUtilities.dp(42);
                    timeItem.setTranslationX(avatarContainer.getX() + AndroidUtilities.dp(16) + extra);
                    timeItem.setTranslationY(avatarContainer.getY() + AndroidUtilities.dp(15) + extra);
                }
                nameX = -21 * AndroidUtilities.density * diff;
                nameY = (float) Math.floor(avatarY) + AndroidUtilities.dp(1.3f) + AndroidUtilities.dp(7) * diff + titleAnimationsYDiff * (1f - avatarAnimationProgress);
                onlineX = -21 * AndroidUtilities.density * diff;
                onlineY = (float) Math.floor(avatarY) + AndroidUtilities.dp(24) + (float) Math.floor(11 * AndroidUtilities.density) * diff;
                idX = -21 * AndroidUtilities.density * diff;
                idY = (float) Math.floor(avatarY) + AndroidUtilities.dp(32) + (float) Math.floor(22 * AndroidUtilities.density) * diff;
                for (int a = 0; a < nameTextView.length; a++) {
                    if (nameTextView[a] == null) {
                        continue;
                    }
                    if (expandAnimator == null || !expandAnimator.isRunning()) {
                        nameTextView[a].setTranslationX(nameX);
                        nameTextView[a].setTranslationY(nameY);

                        onlineTextView[a].setTranslationX(onlineX + customPhotoOffset);
                        onlineTextView[a].setTranslationY(onlineY);
                        idTextView.setTranslationX(idX);
                        idTextView.setTranslationY(idY);
                        if (a == 1) {
                            mediaCounterTextView.setTranslationX(onlineX);
                            mediaCounterTextView.setTranslationY(onlineY);
                        }
                    }
                    nameTextView[a].setScaleX(nameScale);
                    nameTextView[a].setScaleY(nameScale);
                }

                if (diff > 0.85 && !searchMode && NekoConfig.showIdAndDc.Bool()) {
                    idTextView.setVisibility(View.VISIBLE);
                } else {
                    idTextView.setVisibility(View.GONE);
                }
            }

            if (!openAnimationInProgress && (expandAnimator == null || !expandAnimator.isRunning())) {
                needLayoutText(diff);
            }
        }

        if (isPulledDown || (overlaysView != null && overlaysView.animator != null && overlaysView.animator.isRunning())) {
            final ViewGroup.LayoutParams overlaysLp = overlaysView.getLayoutParams();
            overlaysLp.width = listView.getMeasuredWidth();
            overlaysLp.height = (int) (extraHeight + newTop);
            overlaysView.requestLayout();
        }

        updateEmojiStatusEffectPosition();
    }

    public void updateQrItemVisibility(boolean animated) {
        // NekoX: removed
    }

    private void setForegroundImage(boolean secondParent) {
        Drawable drawable = avatarImage.getImageReceiver().getDrawable();
        if (drawable instanceof VectorAvatarThumbDrawable) {
            avatarImage.setForegroundImage(null, null, drawable);
        } else if (drawable instanceof AnimatedFileDrawable) {
            AnimatedFileDrawable fileDrawable = (AnimatedFileDrawable) drawable;
            avatarImage.setForegroundImage(null, null, fileDrawable);
            if (secondParent) {
                fileDrawable.addSecondParentView(avatarImage);
            }
        } else {
            ImageLocation location = avatarsViewPager.getImageLocation(0);
            String filter;
            if (location != null && location.imageType == FileLoader.IMAGE_TYPE_ANIMATION) {
                filter = "avatar";
            } else {
                filter = null;
            }
            avatarImage.setForegroundImage(location, filter, drawable);
        }
    }

    private void refreshNameAndOnlineXY() {
        nameX = AndroidUtilities.dp(-21f) + avatarContainer.getMeasuredWidth() * (avatarScale - (42f + 18f) / 42f);
        nameY = (float) Math.floor(avatarY) + AndroidUtilities.dp(1.3f) + AndroidUtilities.dp(7f) + avatarContainer.getMeasuredHeight() * (avatarScale - (42f + 18f) / 42f) / 2f;
        onlineX = AndroidUtilities.dp(-21f) + avatarContainer.getMeasuredWidth() * (avatarScale - (42f + 18f) / 42f);
        onlineY = (float) Math.floor(avatarY) + AndroidUtilities.dp(24) + (float) Math.floor(11 * AndroidUtilities.density) + avatarContainer.getMeasuredHeight() * (avatarScale - (42f + 18f) / 42f) / 2f;
        idX = AndroidUtilities.dp(-21f) + avatarImage.getMeasuredWidth() * (avatarScale - (42f + 18f) / 42f);
        idY = (float) Math.floor(avatarY) + AndroidUtilities.dp(32) + (float) Math.floor(22 * AndroidUtilities.density) + avatarImage.getMeasuredHeight() * (avatarScale - (42f + 18f) / 42f) / 2f;
    }

    public RecyclerListView getListView() {
        return listView;
    }

    private void needLayoutText(float diff) {
        FrameLayout.LayoutParams layoutParams;
        float scale = nameTextView[1].getScaleX();
        float maxScale = extraHeight > AndroidUtilities.dp(88f) ? 1.67f : 1.12f;

        if (extraHeight > AndroidUtilities.dp(88f) && scale != maxScale) {
            return;
        }

        int viewWidth = AndroidUtilities.isTablet() ? AndroidUtilities.dp(490) : AndroidUtilities.displaySize.x;
        ActionBarMenuItem item = avatarsViewPagerIndicatorView.getSecondaryMenuItem();
        int extra = 0;
        if (editItemVisible) {
            extra += 48;
        }
        if (callItemVisible) {
            extra += 48;
        }
        if (videoCallItemVisible) {
            extra += 48;
        }
        if (searchItem != null) {
            extra += 48;
        }
        int buttonsWidth = AndroidUtilities.dp(118 + 8 + (40 + extra * (1.0f - mediaHeaderAnimationProgress)));
        int minWidth = viewWidth - buttonsWidth;

        int width = (int) (viewWidth - buttonsWidth * Math.max(0.0f, 1.0f - (diff != 1.0f ? diff * 0.15f / (1.0f - diff) : 1.0f)) - nameTextView[1].getTranslationX());
        float width2 = nameTextView[1].getPaint().measureText(nameTextView[1].getText().toString()) * scale + nameTextView[1].getSideDrawablesSize();
        layoutParams = (FrameLayout.LayoutParams) nameTextView[1].getLayoutParams();
        int prevWidth = layoutParams.width;
        if (width < width2) {
            layoutParams.width = Math.max(minWidth, (int) Math.ceil((width - AndroidUtilities.dp(24)) / (scale + ((maxScale - scale) * 7.0f))));
        } else {
            layoutParams.width = (int) Math.ceil(width2);
        }
        layoutParams.width = (int) Math.min((viewWidth - nameTextView[1].getX()) / scale - AndroidUtilities.dp(8), layoutParams.width);
        if (layoutParams.width != prevWidth) {
            nameTextView[1].requestLayout();
        }

        width2 = onlineTextView[1].getPaint().measureText(onlineTextView[1].getText().toString());
        layoutParams = (FrameLayout.LayoutParams) onlineTextView[1].getLayoutParams();
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) mediaCounterTextView.getLayoutParams();
        prevWidth = layoutParams.width;
        layoutParams2.rightMargin = layoutParams.rightMargin = (int) Math.ceil(onlineTextView[1].getTranslationX() + AndroidUtilities.dp(8) + AndroidUtilities.dp(40) * (1.0f - diff));
        if (width < width2) {
            layoutParams2.width = layoutParams.width = (int) Math.ceil(width);
        } else {
            layoutParams2.width = layoutParams.width = LayoutHelper.WRAP_CONTENT;
        }
        if (prevWidth != layoutParams.width) {
            onlineTextView[2].getLayoutParams().width = layoutParams.width;
            onlineTextView[2].requestLayout();
            onlineTextView[3].getLayoutParams().width = layoutParams.width;
            onlineTextView[3].requestLayout();
            onlineTextView[1].requestLayout();
            mediaCounterTextView.requestLayout();
        }

        width2 = idTextView.getPaint().measureText(idTextView.getText().toString());
        layoutParams = (FrameLayout.LayoutParams) idTextView.getLayoutParams();
        prevWidth = layoutParams.width;
        layoutParams.rightMargin = (int) Math.ceil(idTextView.getTranslationX() + AndroidUtilities.dp(8) + AndroidUtilities.dp(40) * (1.0f - diff));
        if (width < width2) {
            layoutParams.width = (int) Math.ceil(width);
        } else {
            layoutParams.width = LayoutHelper.WRAP_CONTENT;
        }
        if (prevWidth != layoutParams.width) {
            idTextView.requestLayout();
        }
    }

    private void fixLayout() {
        if (fragmentView == null) {
            return;
        }
        fragmentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (fragmentView != null) {
                    checkListViewScroll();
                    needLayout(true);
                    fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return true;
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (sharedMediaLayout != null) {
            sharedMediaLayout.onConfigurationChanged(newConfig);
        }
        invalidateIsInLandscapeMode();
        if (isInLandscapeMode && isPulledDown) {
            final View view = layoutManager.findViewByPosition(0);
            if (view != null) {
                listView.scrollBy(0, view.getTop() - AndroidUtilities.dp(88));
            }
        }
        fixLayout();
    }

    private void invalidateIsInLandscapeMode() {
        final Point size = new Point();
        final Display display = getParentActivity().getWindowManager().getDefaultDisplay();
        display.getSize(size);
        isInLandscapeMode = size.x > size.y;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void didReceivedNotification(int id, int account, final Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            int mask = (Integer) args[0];
            boolean infoChanged = (mask & MessagesController.UPDATE_MASK_AVATAR) != 0 || (mask & MessagesController.UPDATE_MASK_NAME) != 0 || (mask & MessagesController.UPDATE_MASK_STATUS) != 0 || (mask & MessagesController.UPDATE_MASK_EMOJI_STATUS) != 0;
            if (userId != 0) {
                if (infoChanged) {
                    updateProfileData(true);
                }
                if ((mask & MessagesController.UPDATE_MASK_PHONE) != 0) {
                    if (listView != null) {
                        RecyclerListView.Holder holder = (RecyclerListView.Holder) listView.findViewHolderForPosition(phoneRow);
                        if (holder != null) {
                            listAdapter.onBindViewHolder(holder, phoneRow);
                        }
                    }
                }
            } else if (chatId != 0) {
                if ((mask & MessagesController.UPDATE_MASK_CHAT) != 0 || (mask & MessagesController.UPDATE_MASK_CHAT_AVATAR) != 0 || (mask & MessagesController.UPDATE_MASK_CHAT_NAME) != 0 || (mask & MessagesController.UPDATE_MASK_CHAT_MEMBERS) != 0 || (mask & MessagesController.UPDATE_MASK_STATUS) != 0) {
                    if ((mask & MessagesController.UPDATE_MASK_CHAT) != 0) {
                        updateListAnimated(true);
                    } else {
                        updateOnlineCount(true);
                    }
                    updateProfileData(true);
                }
                if (infoChanged) {
                    if (listView != null) {
                        int count = listView.getChildCount();
                        for (int a = 0; a < count; a++) {
                            View child = listView.getChildAt(a);
                            if (child instanceof UserCell) {
                                ((UserCell) child).update(mask);
                            }
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.chatOnlineCountDidLoad) {
            Long chatId = (Long) args[0];
            if (chatInfo == null || currentChat == null || currentChat.id != chatId) {
                return;
            }
            chatInfo.online_count = (Integer) args[1];
            updateOnlineCount(true);
            updateProfileData(false);
        } else if (id == NotificationCenter.contactsDidLoad) {
            createActionBarMenu(true);
        } else if (id == NotificationCenter.encryptedChatCreated) {
            if (creatingChat) {
                AndroidUtilities.runOnUIThread(() -> {
                    getNotificationCenter().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                    getNotificationCenter().postNotificationName(NotificationCenter.closeChats);
                    TLRPC.EncryptedChat encryptedChat = (TLRPC.EncryptedChat) args[0];
                    Bundle args2 = new Bundle();
                    args2.putInt("enc_id", encryptedChat.id);
                    if (MessagesController.getInstance(currentAccount).checkCanOpenChat(args2, ProfileActivity.this)) {
                        presentFragment(new ChatActivity(args2), true);
                    }
                });
            }
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            TLRPC.EncryptedChat chat = (TLRPC.EncryptedChat) args[0];
            if (currentEncryptedChat != null && chat.id == currentEncryptedChat.id) {
                currentEncryptedChat = chat;
                updateListAnimated(false);
            }
        } else if (id == NotificationCenter.blockedUsersDidLoad) {
            boolean oldValue = userBlocked;
            userBlocked = getMessagesController().blockePeers.indexOfKey(userId) >= 0;
            if (oldValue != userBlocked) {
                createActionBarMenu(true);
                updateListAnimated(false);
            }
        } else if (id == NotificationCenter.groupCallUpdated) {
            Long chatId = (Long) args[0];
            if (currentChat != null && chatId == currentChat.id && ChatObject.canManageCalls(currentChat)) {
                TLRPC.ChatFull chatFull = MessagesController.getInstance(currentAccount).getChatFull(chatId);
                if (chatFull != null) {
                    if (chatInfo != null) {
                        chatFull.participants = chatInfo.participants;
                    }
                    chatInfo = chatFull;
                }
                if (chatInfo != null && (chatInfo.call == null && !hasVoiceChatItem || chatInfo.call != null && hasVoiceChatItem)) {
                    createActionBarMenu(false);
                }
            }
        } else if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = (TLRPC.ChatFull) args[0];
            if (chatFull.id == chatId) {
                boolean byChannelUsers = (Boolean) args[2];
                if (chatInfo instanceof TLRPC.TL_channelFull) {
                    if (chatFull.participants == null) {
                        chatFull.participants = chatInfo.participants;
                    }
                }
                boolean loadChannelParticipants = chatInfo == null && chatFull instanceof TLRPC.TL_channelFull;
                chatInfo = chatFull;
                if (mergeDialogId == 0 && chatInfo.migrated_from_chat_id != 0) {
                    mergeDialogId = -chatInfo.migrated_from_chat_id;
                    getMediaDataController().getMediaCount(mergeDialogId, topicId, MediaDataController.MEDIA_PHOTOVIDEO, classGuid, true);
                }
                fetchUsersFromChannelInfo();
                if (avatarsViewPager != null && !isTopic) {
                    avatarsViewPager.setChatInfo(chatInfo);
                }
                updateListAnimated(true);
                TLRPC.Chat newChat = getMessagesController().getChat(chatId);
                if (newChat != null) {
                    currentChat = newChat;
                    createActionBarMenu(true);
                }
                if (currentChat.megagroup && (loadChannelParticipants || !byChannelUsers)) {
                    getChannelParticipants(true);
                }
                updateAutoDeleteItem();
                updateTtlIcon();
            }
        } else if (id == NotificationCenter.closeChats) {
            removeSelfFromStack(true);
        } else if (id == NotificationCenter.botInfoDidLoad) {
            TLRPC.BotInfo info = (TLRPC.BotInfo) args[0];
            if (info.user_id == userId) {
                botInfo = info;
                updateListAnimated(false);
            }
        } else if (id == NotificationCenter.userInfoDidLoad) {
            long uid = (Long) args[0];
            if (uid == userId) {
                userInfo = (TLRPC.UserFull) args[1];
                if (imageUpdater != null) {
                    if (listAdapter != null && !TextUtils.equals(userInfo.about, currentBio)) {
                        listAdapter.notifyItemChanged(bioRow);
                    }
                } else {
                    if (!openAnimationInProgress && !callItemVisible) {
                        createActionBarMenu(true);
                    } else {
                        recreateMenuAfterAnimation = true;
                    }
                    updateListAnimated(false);
                    if (sharedMediaLayout != null) {
                        sharedMediaLayout.setCommonGroupsCount(userInfo.common_chats_count);
                        updateSelectedMediaTabText();
                        if (sharedMediaPreloader == null || sharedMediaPreloader.isMediaWasLoaded()) {
                            resumeDelayedFragmentAnimation();
                            needLayout(true);
                        }
                    }
                }
                updateAutoDeleteItem();
                updateTtlIcon();
            }
        } else if (id == NotificationCenter.privacyRulesUpdated) {
//            if (qrItem != null) {
//                updateQrItemVisibility(true);
//            }
        } else if (id == NotificationCenter.didReceiveNewMessages) {
            boolean scheduled = (Boolean) args[2];
            if (scheduled) {
                return;
            }
            long did = getDialogId();
            if (did == (Long) args[0]) {
                boolean enc = DialogObject.isEncryptedDialog(did);
                ArrayList<MessageObject> arr = (ArrayList<MessageObject>) args[1];
                for (int a = 0; a < arr.size(); a++) {
                    MessageObject obj = arr.get(a);
                    if (currentEncryptedChat != null && obj.messageOwner.action instanceof TLRPC.TL_messageEncryptedAction && obj.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL) {
                        TLRPC.TL_decryptedMessageActionSetMessageTTL action = (TLRPC.TL_decryptedMessageActionSetMessageTTL) obj.messageOwner.action.encryptedAction;
                        if (listAdapter != null) {
                            listAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.emojiLoaded) {
            if (listView != null) {
                listView.invalidateViews();
            }
        } else if (id == NotificationCenter.reloadInterface) {
            int prevEmptyRow = emptyRow;
            updateListAnimated(false);
        } else if (id == NotificationCenter.newSuggestionsAvailable) {
            int prevRow1 = passwordSuggestionRow;
            int prevRow2 = phoneSuggestionRow;
            updateRowsIds();
            if (prevRow1 != passwordSuggestionRow || prevRow2 != phoneSuggestionRow) {
                listAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.topicsDidLoaded) {
            if (isTopic) {
                updateProfileData(false);
            }
        } else if (id == NotificationCenter.updateSearchSettings) {
            if (searchAdapter != null) {
                searchAdapter.searchArray = searchAdapter.onCreateSearchArray();
                searchAdapter.recentSearches.clear();
                searchAdapter.updateSearchArray();
                searchAdapter.search(searchAdapter.lastSearchString);
            }
        } else if (id == NotificationCenter.reloadDialogPhotos) {
            updateProfileData(false);
        }
    }

    private void updateAutoDeleteItem() {
        if (autoDeleteItem == null || autoDeletePopupWrapper == null) {
            return;
        }
        int ttl = 0;
        if (userInfo != null || chatInfo != null) {
            ttl = userInfo != null ? userInfo.ttl_period : chatInfo.ttl_period;
        }
        autoDeleteItemDrawable.setTime(ttl);
        autoDeletePopupWrapper.updateItems(ttl);
    }

    private void updateTimeItem() {
        if (timerDrawable == null) {
            return;
        }
        if (currentEncryptedChat != null) {
            timerDrawable.setTime(currentEncryptedChat.ttl);
            timeItem.setTag(1);
            timeItem.setVisibility(View.VISIBLE);
        } else if (userInfo != null) {
            timerDrawable.setTime(userInfo.ttl_period);
            if (needTimerImage && userInfo.ttl_period != 0) {
                timeItem.setTag(1);
                timeItem.setVisibility(View.VISIBLE);
            } else {
                timeItem.setTag(null);
                timeItem.setVisibility(View.GONE);
            }
        } else if (chatInfo != null) {
            timerDrawable.setTime(chatInfo.ttl_period);
            if (needTimerImage && chatInfo.ttl_period != 0) {
                timeItem.setTag(1);
                timeItem.setVisibility(View.VISIBLE);
            } else {
                timeItem.setTag(null);
                timeItem.setVisibility(View.GONE);
            }
        } else {
            timeItem.setTag(null);
            timeItem.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean needDelayOpenAnimation() {
        if (playProfileAnimation == 0) {
            return true;
        }
        return false;
    }

    @Override
    public void mediaCountUpdated() {
        if (sharedMediaLayout != null && sharedMediaPreloader != null) {
            sharedMediaLayout.setNewMediaCounts(sharedMediaPreloader.getLastMediaCount());
        }
        updateSharedMediaRows();
        updateSelectedMediaTabText();

        if (userInfo != null) {
            resumeDelayedFragmentAnimation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sharedMediaLayout != null) {
            sharedMediaLayout.onResume();
        }
        invalidateIsInLandscapeMode();
        if (listAdapter != null) {
            // saveScrollPosition();
            firstLayout = true;
            listAdapter.notifyDataSetChanged();
        }

        if (imageUpdater != null) {
            imageUpdater.onResume();
            setParentActivityTitle(LocaleController.getString("Settings", R.string.Settings));
        }

        updateProfileData(true);
        fixLayout();
        if (nameTextView[1] != null) {
            setParentActivityTitle(nameTextView[1].getText());
        }
        if (userId != 0) {
            final TLRPC.User user = getMessagesController().getUser(userId);
            if (user != null && user.photo == null) {
                if (extraHeight >= AndroidUtilities.dp(88f)) {
                    expandAnimator.cancel();
                    expandAnimatorValues[0] = 1f;
                    expandAnimatorValues[1] = 0f;
                    setAvatarExpandProgress(1f);
                    avatarsViewPager.setVisibility(View.GONE);
                    extraHeight = AndroidUtilities.dp(88f);
                    allowPullingDown = false;
                    layoutManager.scrollToPositionWithOffset(0, AndroidUtilities.dp(88) - listView.getPaddingTop());
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (undoView != null) {
            undoView.hide(true, 0);
        }
        if (imageUpdater != null) {
            imageUpdater.onPause();
        }
    }

    @Override
    public boolean isSwipeBackEnabled(MotionEvent event) {
        if (avatarsViewPager != null && avatarsViewPager.getVisibility() == View.VISIBLE && avatarsViewPager.getRealCount() > 1) {
            avatarsViewPager.getHitRect(rect);
            if (rect.contains((int) event.getX(), (int) event.getY() - actionBar.getMeasuredHeight())) {
                return false;
            }
        }
        if (sharedMediaRow == -1 || sharedMediaLayout == null) {
            return true;
        }
        if (!sharedMediaLayout.isSwipeBackEnabled()) {
            return false;
        }
        sharedMediaLayout.getHitRect(rect);
        if (!rect.contains((int) event.getX(), (int) event.getY() - actionBar.getMeasuredHeight())) {
            return true;
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

    public UndoView getUndoView() {
        return undoView;
    }

    public boolean onBackPressed() {
        return actionBar.isEnabled() && (sharedMediaRow == -1 || sharedMediaLayout == null || !sharedMediaLayout.closeActionMode());
    }

    public boolean isSettings() {
        return imageUpdater != null;
    }

    @Override
    public void onBecomeFullyHidden() {
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    public void setPlayProfileAnimation(int type) {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        if (!AndroidUtilities.isTablet()) {
            needTimerImage = type != 0;
            if (preferences.getBoolean("view_animations", true)) {
                playProfileAnimation = type;
            } else if (type == 2) {
                expandPhoto = true;
            }
        }
    }

    private void updateSharedMediaRows() {
        if (listAdapter == null) {
            return;
        }
        updateListAnimated(false);
    }

    public boolean isFragmentOpened;

    @Override
    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        isFragmentOpened = isOpen;
        if ((!isOpen && backward || isOpen && !backward) && playProfileAnimation != 0 && allowProfileAnimation && !isPulledDown) {
            openAnimationInProgress = true;
        }
        if (isOpen) {
            if (imageUpdater != null) {
                transitionIndex = getNotificationCenter().setAnimationInProgress(transitionIndex, new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoad, NotificationCenter.mediaCountsDidLoad, NotificationCenter.userInfoDidLoad});
            } else {
                transitionIndex = getNotificationCenter().setAnimationInProgress(transitionIndex, new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoad, NotificationCenter.mediaCountsDidLoad});
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !backward && getParentActivity() != null) {
                navigationBarAnimationColorFrom = getParentActivity().getWindow().getNavigationBarColor();
            }
        }
        transitionAnimationInProress = true;
        checkPhotoDescriptionAlpha();
    }

    @Override
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            if (!backward) {
                if (playProfileAnimation != 0 && allowProfileAnimation) {
                    if (playProfileAnimation == 1) {
                        currentExpandAnimatorValue = 0f;
                    }
                    openAnimationInProgress = false;
                    checkListViewScroll();
                    if (recreateMenuAfterAnimation) {
                        createActionBarMenu(true);
                    }
                }
                if (!fragmentOpened) {
                    fragmentOpened = true;
                    invalidateScroll = true;
                    fragmentView.requestLayout();
                }
            }
            getNotificationCenter().onAnimationFinish(transitionIndex);
        }
        transitionAnimationInProress = false;
        checkPhotoDescriptionAlpha();
    }

    @Keep
    public float getAvatarAnimationProgress() {
        return avatarAnimationProgress;
    }

    private AboutLinkCell aboutLinkCell;

    @Keep
    public void setAvatarAnimationProgress(float progress) {
        avatarAnimationProgress = currentExpandAnimatorValue = progress;
        checkPhotoDescriptionAlpha();

        listView.setAlpha(progress);

        listView.setTranslationX(AndroidUtilities.dp(48) - AndroidUtilities.dp(48) * progress);

        int color;
        if (playProfileAnimation == 2 && avatarColor != 0) {
            color = avatarColor;
        } else {
            color = AvatarDrawable.getProfileBackColorForId(userId != 0 || ChatObject.isChannel(chatId, currentAccount) && !currentChat.megagroup ? 5 : chatId, resourcesProvider);
        }

        int actionBarColor = actionBarAnimationColorFrom != 0 ? actionBarAnimationColorFrom : getThemedColor(Theme.key_actionBarDefault);
        int actionBarColor2 = actionBarColor;
        if (SharedConfig.chatBlurEnabled()) {
            actionBarColor = ColorUtils.setAlphaComponent(actionBarColor, 0);
        }
        topView.setBackgroundColor(ColorUtils.blendARGB(actionBarColor, color, progress));
        timerDrawable.setBackgroundColor(ColorUtils.blendARGB(actionBarColor2, color, progress));

        color = AvatarDrawable.getIconColorForId(userId != 0 || ChatObject.isChannel(chatId, currentAccount) && !currentChat.megagroup ? 5 : chatId, resourcesProvider);
        int iconColor = getThemedColor(Theme.key_actionBarDefaultIcon);
        actionBar.setItemsColor(ColorUtils.blendARGB(iconColor, color, progress), false);

        color = getThemedColor(Theme.key_profile_title);
        int titleColor = getThemedColor(Theme.key_actionBarDefaultTitle);
        for (int i = 0; i < 2; i++) {
            if (nameTextView[i] == null || i == 1 && playProfileAnimation == 2) {
                continue;
            }
            nameTextView[i].setTextColor(ColorUtils.blendARGB(titleColor, color, progress));
        }

        color = isOnline[0] ? getThemedColor(Theme.key_profile_status) : AvatarDrawable.getProfileTextColorForId(userId != 0 || ChatObject.isChannel(chatId, currentAccount) && !currentChat.megagroup ? 5 : chatId, resourcesProvider);
        int subtitleColor = getThemedColor(isOnline[0] ? Theme.key_chat_status : Theme.key_actionBarDefaultSubtitle);

        for (int i = 0; i < 3; i++) {
            if (onlineTextView[i] == null || i == 1 || i == 2 && playProfileAnimation == 2) {
                continue;
            }
            onlineTextView[i].setTextColor(ColorUtils.blendARGB(subtitleColor, color, progress));
        }
        extraHeight = initialAnimationExtraHeight * progress;
        color = AvatarDrawable.getProfileColorForId(userId != 0 ? userId : chatId, resourcesProvider);
        int color2 = AvatarDrawable.getColorForId(userId != 0 ? userId : chatId);
        if (color != color2) {
            avatarDrawable.setColor(ColorUtils.blendARGB(color2, color, progress));
            avatarImage.invalidate();
        }

        if (navigationBarAnimationColorFrom != 0) {
            color = ColorUtils.blendARGB(navigationBarAnimationColorFrom, getNavigationBarColor(), progress);
            setNavigationBarColor(color);
        }

        topView.invalidate();

        needLayout(true);
        if (fragmentView != null) {
            fragmentView.invalidate();
        }

        if (aboutLinkCell != null) {
            aboutLinkCell.invalidate();
        }
    }

    @Override
    public int getNavigationBarColor() {
        return Theme.getColor(Theme.key_windowBackgroundGray, resourcesProvider);
    }

    boolean profileTransitionInProgress;

    @Override
    public AnimatorSet onCustomTransitionAnimation(final boolean isOpen, final Runnable callback) {
        if (playProfileAnimation != 0 && allowProfileAnimation && !isPulledDown && !disableProfileAnimation) {
            if (timeItem != null) {
                timeItem.setAlpha(1.0f);
            }
            if (parentLayout != null && parentLayout.getFragmentStack().size() >= 2) {
                BaseFragment fragment = parentLayout.getFragmentStack().get(parentLayout.getFragmentStack().size() - 2);
                if (fragment instanceof ChatActivityInterface) {
                    previousTransitionFragment = (ChatActivityInterface) fragment;
                }
                if (fragment instanceof DialogsActivity) {
                    DialogsActivity dialogsActivity = (DialogsActivity) fragment;
                    if (dialogsActivity.rightSlidingDialogContainer != null && dialogsActivity.rightSlidingDialogContainer.currentFragment instanceof ChatActivityInterface) {
                        previousTransitionFragment = (ChatActivityInterface) dialogsActivity.rightSlidingDialogContainer.currentFragment;
                    }
                }
            }
            if (previousTransitionFragment != null) {
                updateTimeItem();
            }
            final AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(playProfileAnimation == 2 ? 250 : 180);
            listView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            ActionBarMenu menu = actionBar.createMenu();
            if (menu.getItem(10) == null) {
                if (animatingItem == null) {
                    animatingItem = menu.addItem(10, R.drawable.ic_ab_other);
                }
            }
            if (isOpen) {
                for (int i = 0; i < 2; i++) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) onlineTextView[i + 1].getLayoutParams();
                    layoutParams.rightMargin = (int) (-21 * AndroidUtilities.density + AndroidUtilities.dp(8));
                    onlineTextView[i + 1].setLayoutParams(layoutParams);
                }


                if (playProfileAnimation != 2) {
                    int width = (int) Math.ceil(AndroidUtilities.displaySize.x - AndroidUtilities.dp(118 + 8) + 21 * AndroidUtilities.density);
                    float width2 = nameTextView[1].getPaint().measureText(nameTextView[1].getText().toString()) * 1.12f + nameTextView[1].getSideDrawablesSize();
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) nameTextView[1].getLayoutParams();
                    if (width < width2) {
                        layoutParams.width = (int) Math.ceil(width / 1.12f);
                    } else {
                        layoutParams.width = LayoutHelper.WRAP_CONTENT;
                    }
                    nameTextView[1].setLayoutParams(layoutParams);

                    initialAnimationExtraHeight = AndroidUtilities.dp(88f);
                } else {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) nameTextView[1].getLayoutParams();
                    layoutParams.width = (int) ((AndroidUtilities.displaySize.x - AndroidUtilities.dp(32)) / 1.67f);
                    nameTextView[1].setLayoutParams(layoutParams);
                }
                fragmentView.setBackgroundColor(0);
                setAvatarAnimationProgress(0);
                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofFloat(this, "avatarAnimationProgress", 0.0f, 1.0f));
                if (writeButton != null && writeButton.getTag() == null) {
                    writeButton.setScaleX(0.2f);
                    writeButton.setScaleY(0.2f);
                    writeButton.setAlpha(0.0f);
                    animators.add(ObjectAnimator.ofFloat(writeButton, View.SCALE_X, 1.0f));
                    animators.add(ObjectAnimator.ofFloat(writeButton, View.SCALE_Y, 1.0f));
                    animators.add(ObjectAnimator.ofFloat(writeButton, View.ALPHA, 1.0f));
                }
                if (playProfileAnimation == 2) {
                    avatarColor = getAverageColor(avatarImage.getImageReceiver());
                    nameTextView[1].setTextColor(Color.WHITE);
                    onlineTextView[1].setTextColor(Color.argb(179, 255, 255, 255));
                    idTextView.setAlpha(0);
                    idTextView.setTextColor(Color.argb(179, 255, 255, 255));
                    actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR, false);
                    overlaysView.setOverlaysVisible();
                    animators.add(ObjectAnimator.ofFloat(idTextView, View.ALPHA, 0.0f, 1.0f));
                }
                for (int a = 0; a < 2; a++) {
                    nameTextView[a].setAlpha(a == 0 ? 1.0f : 0.0f);
                    animators.add(ObjectAnimator.ofFloat(nameTextView[a], View.ALPHA, a == 0 ? 0.0f : 1.0f));
                }
                if (timeItem.getTag() != null) {
                    animators.add(ObjectAnimator.ofFloat(timeItem, View.ALPHA, 1.0f, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(timeItem, View.SCALE_X, 1.0f, 0.0f));
                    animators.add(ObjectAnimator.ofFloat(timeItem, View.SCALE_Y, 1.0f, 0.0f));
                }
                if (animatingItem != null) {
                    animatingItem.setAlpha(1.0f);
                    animators.add(ObjectAnimator.ofFloat(animatingItem, View.ALPHA, 0.0f));
                }
                if (callItemVisible && chatId != 0) {
                    callItem.setAlpha(0.0f);
                    animators.add(ObjectAnimator.ofFloat(callItem, View.ALPHA, 1.0f));
                }
                if (videoCallItemVisible) {
                    videoCallItem.setAlpha(0.0f);
                    animators.add(ObjectAnimator.ofFloat(videoCallItem, View.ALPHA, 1.0f));
                }
                if (editItemVisible) {
                    editItem.setAlpha(0.0f);
                    animators.add(ObjectAnimator.ofFloat(editItem, View.ALPHA, 1.0f));
                }
                if (ttlIconView.getTag() != null) {
                    ttlIconView.setAlpha(0f);
                    animators.add(ObjectAnimator.ofFloat(ttlIconView, View.ALPHA, 1.0f));
                }

                boolean onlineTextCrosafade = false;

                if (previousTransitionFragment != null) {
                    ChatAvatarContainer avatarContainer = previousTransitionFragment.getAvatarContainer();
                    if (avatarContainer != null && avatarContainer.getSubtitleTextView().getLeftDrawable() != null || avatarContainer.statusMadeShorter[0]) {
                        transitionOnlineText = avatarContainer.getSubtitleTextView();
                        avatarContainer2.invalidate();
                        onlineTextCrosafade = true;
                        onlineTextView[0].setAlpha(0f);
                        onlineTextView[1].setAlpha(0f);
                        animators.add(ObjectAnimator.ofFloat(onlineTextView[1], View.ALPHA, 1.0f));
                    }
                }

                if (!onlineTextCrosafade) {
                    for (int a = 0; a < 2; a++) {
                        onlineTextView[a].setAlpha(a == 0 ? 1.0f : 0.0f);
                        animators.add(ObjectAnimator.ofFloat(onlineTextView[a], View.ALPHA, a == 0 ? 0.0f : 1.0f));
                    }
                }
                animatorSet.playTogether(animators);
            } else {
                initialAnimationExtraHeight = extraHeight;
                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofFloat(this, "avatarAnimationProgress", 1.0f, 0.0f));
                if (writeButton != null) {
                    animators.add(ObjectAnimator.ofFloat(writeButton, View.SCALE_X, 0.2f));
                    animators.add(ObjectAnimator.ofFloat(writeButton, View.SCALE_Y, 0.2f));
                    animators.add(ObjectAnimator.ofFloat(writeButton, View.ALPHA, 0.0f));
                }
                for (int a = 0; a < 2; a++) {
                    animators.add(ObjectAnimator.ofFloat(nameTextView[a], View.ALPHA, a == 0 ? 1.0f : 0.0f));
                }
                if (timeItem.getTag() != null) {
                    timeItem.setAlpha(0f);
                    animators.add(ObjectAnimator.ofFloat(timeItem, View.ALPHA, 0.0f, 1.0f));
                    animators.add(ObjectAnimator.ofFloat(timeItem, View.SCALE_X, 0.0f, 1.0f));
                    animators.add(ObjectAnimator.ofFloat(timeItem, View.SCALE_Y, 0.0f, 1.0f));
                }
                if (animatingItem != null) {
                    animatingItem.setAlpha(0.0f);
                    animators.add(ObjectAnimator.ofFloat(animatingItem, View.ALPHA, 1.0f));
                }
                if (callItemVisible && chatId != 0) {
                    callItem.setAlpha(1.0f);
                    animators.add(ObjectAnimator.ofFloat(callItem, View.ALPHA, 0.0f));
                }
                if (videoCallItemVisible) {
                    videoCallItem.setAlpha(1.0f);
                    animators.add(ObjectAnimator.ofFloat(videoCallItem, View.ALPHA, 0.0f));
                }
                if (editItemVisible) {
                    editItem.setAlpha(1.0f);
                    animators.add(ObjectAnimator.ofFloat(editItem, View.ALPHA, 0.0f));
                }
                if (ttlIconView != null) {
                    animators.add(ObjectAnimator.ofFloat(ttlIconView, View.ALPHA, ttlIconView.getAlpha(), 0.0f));
                }
                boolean crossfadeOnlineText = false;
                BaseFragment previousFragment = parentLayout.getFragmentStack().size() > 1 ? parentLayout.getFragmentStack().get(parentLayout.getFragmentStack().size() - 2) : null;
                if (previousFragment instanceof ChatActivity) {
                    ChatAvatarContainer avatarContainer = ((ChatActivity) previousFragment).getAvatarContainer();
                    if (avatarContainer.getSubtitleTextView().getLeftDrawable() != null || avatarContainer.statusMadeShorter[0]) {
                        transitionOnlineText = avatarContainer.getSubtitleTextView();
                        avatarContainer2.invalidate();
                        crossfadeOnlineText = true;
                        animators.add(ObjectAnimator.ofFloat(onlineTextView[0], View.ALPHA, 0.0f));
                        animators.add(ObjectAnimator.ofFloat(onlineTextView[1], View.ALPHA, 0.0f));
                    }
                }
                if (!crossfadeOnlineText) {
                    for (int a = 0; a < 2; a++) {
                        animators.add(ObjectAnimator.ofFloat(onlineTextView[a], View.ALPHA, a == 0 ? 1.0f : 0.0f));
                    }
                }
                animatorSet.playTogether(animators);
            }
            profileTransitionInProgress = true;
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1f);
            valueAnimator.addUpdateListener(valueAnimator1 -> {
                if (fragmentView != null) {
                    fragmentView.invalidate();
                }
            });
            animatorSet.playTogether(valueAnimator);

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (fragmentView == null) {
                        callback.run();
                        return;
                    }
                    listView.setLayerType(View.LAYER_TYPE_NONE, null);
                    if (animatingItem != null) {
                        ActionBarMenu menu = actionBar.createMenu();
                        menu.clearItems();
                        animatingItem = null;
                    }
                    callback.run();
                    if (playProfileAnimation == 2) {
                        playProfileAnimation = 1;
                        avatarImage.setForegroundAlpha(1.0f);
                        avatarContainer.setVisibility(View.GONE);
                        avatarsViewPager.resetCurrentItem();
                        avatarsViewPager.setVisibility(View.VISIBLE);
                        idTextView.setAlpha(1.0f);
                    }
                    transitionOnlineText = null;
                    avatarContainer2.invalidate();
                    profileTransitionInProgress = false;
                    previousTransitionFragment = null;
                    fragmentView.invalidate();
                }
            });
            animatorSet.setInterpolator(playProfileAnimation == 2 ? CubicBezierInterpolator.DEFAULT : new DecelerateInterpolator());

            AndroidUtilities.runOnUIThread(animatorSet::start, 50);
            return animatorSet;
        }
        return null;
    }

    private int getAverageColor(ImageReceiver imageReceiver) {
        if (imageReceiver.getDrawable() instanceof VectorAvatarThumbDrawable) {
            return ((VectorAvatarThumbDrawable)imageReceiver.getDrawable()).gradientTools.getAverageColor();
        }
        return AndroidUtilities.calcBitmapColor(avatarImage.getImageReceiver().getBitmap());
    }

    private void updateOnlineCount(boolean notify) {
        onlineCount = 0;
        int currentTime = getConnectionsManager().getCurrentTime();
        sortedUsers.clear();
        if (chatInfo instanceof TLRPC.TL_chatFull || chatInfo instanceof TLRPC.TL_channelFull && chatInfo.participants_count <= 200 && chatInfo.participants != null) {
            for (int a = 0; a < chatInfo.participants.participants.size(); a++) {
                TLRPC.ChatParticipant participant = chatInfo.participants.participants.get(a);
                TLRPC.User user = getMessagesController().getUser(participant.user_id);
                if (user != null && user.status != null && (user.status.expires > currentTime || user.id == getUserConfig().getClientUserId()) && user.status.expires > 10000) {
                    onlineCount++;
                }
                sortedUsers.add(a);
            }

            try {
                Collections.sort(sortedUsers, (lhs, rhs) -> {
                    TLRPC.User user1 = getMessagesController().getUser(chatInfo.participants.participants.get(rhs).user_id);
                    TLRPC.User user2 = getMessagesController().getUser(chatInfo.participants.participants.get(lhs).user_id);
                    int status1 = 0;
                    int status2 = 0;
                    if (user1 != null) {
                        if (user1.bot) {
                            status1 = -110;
                        } else if (user1.self) {
                            status1 = currentTime + 50000;
                        } else if (user1.status != null) {
                            status1 = user1.status.expires;
                        }
                    }
                    if (user2 != null) {
                        if (user2.bot) {
                            status2 = -110;
                        } else if (user2.self) {
                            status2 = currentTime + 50000;
                        } else if (user2.status != null) {
                            status2 = user2.status.expires;
                        }
                    }
                    if (status1 > 0 && status2 > 0) {
                        if (status1 > status2) {
                            return 1;
                        } else if (status1 < status2) {
                            return -1;
                        }
                        return 0;
                    } else if (status1 < 0 && status2 < 0) {
                        if (status1 > status2) {
                            return 1;
                        } else if (status1 < status2) {
                            return -1;
                        }
                        return 0;
                    } else if (status1 < 0 && status2 > 0 || status1 == 0 && status2 != 0) {
                        return -1;
                    } else if (status2 < 0 && status1 > 0 || status2 == 0 && status1 != 0) {
                        return 1;
                    }
                    return 0;
                });
            } catch (Exception e) {
                FileLog.e(e);
            }

            if (notify && listAdapter != null && membersStartRow > 0) {
                AndroidUtilities.updateVisibleRows(listView);
            }
            if (sharedMediaLayout != null && sharedMediaRow != -1 && (sortedUsers.size() > 5 || usersForceShowingIn == 2) && usersForceShowingIn != 1) {
                sharedMediaLayout.setChatUsers(sortedUsers, chatInfo);
            }
        } else if (chatInfo instanceof TLRPC.TL_channelFull && chatInfo.participants_count > 200) {
            onlineCount = chatInfo.online_count;
        }
    }

    public void setChatInfo(TLRPC.ChatFull value) {
        chatInfo = value;
        if (chatInfo != null && chatInfo.migrated_from_chat_id != 0 && mergeDialogId == 0) {
            mergeDialogId = -chatInfo.migrated_from_chat_id;
            getMediaDataController().getMediaCounts(mergeDialogId, topicId, classGuid);
        }
        if (sharedMediaLayout != null) {
            sharedMediaLayout.setChatInfo(chatInfo);
        }
        if (avatarsViewPager != null && !isTopic) {
            avatarsViewPager.setChatInfo(chatInfo);
        }
        fetchUsersFromChannelInfo();
    }

    public void setUserInfo(TLRPC.UserFull value) {
        userInfo = value;
    }

    public boolean canSearchMembers() {
        return canSearchMembers;
    }

    private void fetchUsersFromChannelInfo() {
        if (currentChat == null || !currentChat.megagroup) {
            return;
        }
        if (chatInfo instanceof TLRPC.TL_channelFull && chatInfo.participants != null) {
            for (int a = 0; a < chatInfo.participants.participants.size(); a++) {
                TLRPC.ChatParticipant chatParticipant = chatInfo.participants.participants.get(a);
                participantsMap.put(chatParticipant.user_id, chatParticipant);
            }
        }
    }

    private void kickUser(long uid, TLRPC.ChatParticipant participant) {
        if (uid != 0) {
            TLRPC.User user = getMessagesController().getUser(uid);
            getMessagesController().deleteParticipantFromChat(chatId, user);
            if (currentChat != null && user != null && BulletinFactory.canShowBulletin(this)) {
                BulletinFactory.createRemoveFromChatBulletin(this, user, currentChat.title).show();
            }
            if (chatInfo.participants.participants.remove(participant)) {
                updateListAnimated(true);
            }
        } else {
            getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
            if (AndroidUtilities.isTablet()) {
                getNotificationCenter().postNotificationName(NotificationCenter.closeChats, -chatId);
            } else {
                getNotificationCenter().postNotificationName(NotificationCenter.closeChats);
            }
            getMessagesController().deleteParticipantFromChat(chatId, getMessagesController().getUser(getUserConfig().getClientUserId()));
            playProfileAnimation = 0;
            finishFragment();
        }
    }

    public boolean isChat() {
        return chatId != 0;
    }

    private void updateRowsIds() {
        int prevRowsCount = rowCount;
        rowCount = 0;

        setAvatarRow = -1;
        setAvatarSectionRow = -1;
        numberSectionRow = -1;
        numberRow = -1;
        setUsernameRow = -1;
        bioRow = -1;
        phoneSuggestionSectionRow = -1;
        phoneSuggestionRow = -1;
        passwordSuggestionSectionRow = -1;
        passwordSuggestionRow = -1;
        settingsSectionRow = -1;
        settingsSectionRow2 = -1;
        notificationRow = -1;
        nekoRow = -1;
        languageRow = -1;
        premiumRow = -1;
        premiumSectionsRow = -1;
        privacyRow = -1;
        dataRow = -1;
        chatRow = -1;
        filtersRow = -1;
        liteModeRow = -1;
        stickersRow = -1;
        devicesRow = -1;
        devicesSectionRow = -1;
        helpHeaderRow = -1;
        questionRow = -1;
        faqRow = -1;
        policyRow = -1;
        helpSectionCell = -1;
        debugHeaderRow = -1;
        sendLogsRow = -1;
        sendLastLogsRow = -1;
        clearLogsRow = -1;
        switchBackendRow = -1;
        versionRow = -1;

        sendMessageRow = -1;
        reportRow = -1;
        reportReactionRow = -1;
        addToContactsRow = -1;
        emptyRow = -1;
        infoHeaderRow = -1;
        phoneRow = -1;
        userInfoRow = -1;
        locationRow = -1;
        channelInfoRow = -1;
        usernameRow = -1;
        restrictionReasonRow = -1;
        settingsTimerRow = -1;
        settingsKeyRow = -1;
        notificationsDividerRow = -1;
        reportDividerRow = -1;
        notificationsRow = -1;
        infoSectionRow = -1;
        secretSettingsSectionRow = -1;
        bottomPaddingRow = -1;
        addToGroupButtonRow = -1;
        addToGroupInfoRow = -1;

        membersHeaderRow = -1;
        membersStartRow = -1;
        membersEndRow = -1;
        addMemberRow = -1;
        subscribersRow = -1;
        subscribersRequestsRow = -1;
        administratorsRow = -1;
        blockedUsersRow = -1;
        membersSectionRow = -1;
        sharedMediaRow = -1;
        notificationsSimpleRow = -1;

        unblockRow = -1;
        joinRow = -1;
        lastSectionRow = -1;
        visibleChatParticipants.clear();
        visibleSortedUsers.clear();

        boolean hasMedia = false;
        if (sharedMediaPreloader != null) {
            int[] lastMediaCount = sharedMediaPreloader.getLastMediaCount();
            for (int a = 0; a < lastMediaCount.length; a++) {
                if (lastMediaCount[a] > 0) {
                    hasMedia = true;
                    break;
                }
            }
        }

        if (userId != 0) {
            if (LocaleController.isRTL) {
                emptyRow = rowCount++;
            }
            TLRPC.User user = getMessagesController().getUser(userId);

            if (UserObject.isUserSelf(user)) {
                if (avatarBig == null && (user.photo == null || !(user.photo.photo_big instanceof TLRPC.TL_fileLocation_layer97) && !(user.photo.photo_big instanceof TLRPC.TL_fileLocationToBeDeprecated)) && (avatarsViewPager == null || avatarsViewPager.getRealCount() == 0)) {
                    setAvatarRow = rowCount++;
                    setAvatarSectionRow = rowCount++;
                }
                numberSectionRow = rowCount++;
                setUsernameRow = rowCount++;
                if (!hideNumber) {
                    numberRow = rowCount++;
                }
                bioRow = rowCount++;

                settingsSectionRow = rowCount++;

                Set<String> suggestions = getMessagesController().pendingSuggestions;
                if (suggestions.contains("VALIDATE_PHONE_NUMBER")) {
                    phoneSuggestionRow = rowCount++;
                    phoneSuggestionSectionRow = rowCount++;
                }
                if (suggestions.contains("VALIDATE_PASSWORD")) {
                    passwordSuggestionRow = rowCount++;
                    passwordSuggestionSectionRow = rowCount++;
                }

                settingsSectionRow2 = rowCount++;
                notificationRow = rowCount++;
                dataRow = rowCount++;
                privacyRow = rowCount++;
                chatRow = rowCount++;
                stickersRow = rowCount++;
                devicesRow = rowCount++;
                filtersRow = rowCount++;
                liteModeRow = rowCount++;
                nekoRow = rowCount++;
                languageRow = rowCount++;
                devicesSectionRow = rowCount++;
                if (!getMessagesController().premiumLocked) {
                    premiumRow = rowCount++;
                    premiumSectionsRow = rowCount++;
                }
                helpHeaderRow = rowCount++;
                faqRow = rowCount++;
                policyRow = rowCount++;
                if (BuildVars.LOGS_ENABLED) {
                    helpSectionCell = rowCount++;
                    debugHeaderRow = rowCount++;
                }
                if (BuildVars.LOGS_ENABLED) {
                    sendLogsRow = rowCount++;
                    sendLastLogsRow = -1;
                    // disable send last logs
                    clearLogsRow = rowCount++;
                }
                versionRow = rowCount++;
            } else {
                String username = UserObject.getPublicUsername(user);
                boolean hasInfo = userInfo != null && !TextUtils.isEmpty(userInfo.about) || user != null && !TextUtils.isEmpty(username);
                boolean hasPhone = user != null && (!TextUtils.isEmpty(user.phone) || !TextUtils.isEmpty(vcardPhone));

                infoHeaderRow = rowCount++;
                if (username != null) {
                    usernameRow = rowCount++;
                }
                if (user != null && !user.restriction_reason.isEmpty()) {
                    restrictionReasonRow = rowCount++;
                }
                if (!isBot && (hasPhone || !hasInfo) && !hideNumber) {
                    phoneRow = rowCount++;
                }
                if (userInfo != null && !TextUtils.isEmpty(userInfo.about)) {
                    userInfoRow = rowCount++;
                }
                if (phoneRow != -1 || userInfoRow != -1 || usernameRow != -1 || restrictionReasonRow != -1) {
                    notificationsDividerRow = rowCount++;
                }
                if (userId != getUserConfig().getClientUserId()) {
                    notificationsRow = rowCount++;
                }
                infoSectionRow = rowCount++;

                if (currentEncryptedChat instanceof TLRPC.TL_encryptedChat) {
                    settingsTimerRow = rowCount++;
                    settingsKeyRow = rowCount++;
                    secretSettingsSectionRow = rowCount++;
                }

                if (user != null && !isBot && currentEncryptedChat == null && user.id != getUserConfig().getClientUserId()) {
                    if (userBlocked) {
                        unblockRow = rowCount++;
                        lastSectionRow = rowCount++;
                    }
                }

                if (user != null && isBot && !user.bot_nochats) {
                    addToGroupButtonRow = rowCount++;
                    addToGroupInfoRow = rowCount++;
                }

                if (reportReactionMessageId != 0 && !ContactsController.getInstance(currentAccount).isContact(userId)) {
                    reportReactionRow = rowCount++;
                    reportDividerRow = rowCount++;
                }

                if (showAddToContacts) {
                    addToContactsRow = rowCount++;
                    reportDividerRow = rowCount++;
                }

                if (hasMedia || userInfo != null && userInfo.common_chats_count != 0) {
                    sharedMediaRow = rowCount++;
                } else if (lastSectionRow == -1 && needSendMessage) {
                    sendMessageRow = rowCount++;
                    reportRow = rowCount++;
                    lastSectionRow = rowCount++;
                }
            }
        } else if (isTopic) {
            infoHeaderRow = rowCount++;
            if (ChatObject.isPublic(currentChat)) {
                usernameRow = rowCount++;
            }
            if (!currentChat.restriction_reason.isEmpty()) {
                restrictionReasonRow = rowCount++;
            }
            notificationsSimpleRow = rowCount++;
            infoSectionRow = rowCount++;
            if (hasMedia) {
                sharedMediaRow = rowCount++;
            }
        } else if (chatId != 0) {
            if (chatInfo != null && (!TextUtils.isEmpty(chatInfo.about) || chatInfo.location instanceof TLRPC.TL_channelLocation) || ChatObject.isPublic(currentChat)) {
                if (LocaleController.isRTL && ChatObject.isChannel(currentChat) && chatInfo != null && !currentChat.megagroup && chatInfo.linked_chat_id != 0) {
                    emptyRow = rowCount++;
                }
                infoHeaderRow = rowCount++;
                if (chatInfo != null) {
                    if (!TextUtils.isEmpty(chatInfo.about)) {
                        channelInfoRow = rowCount++;
                    }
                    if (chatInfo.location instanceof TLRPC.TL_channelLocation) {
                        locationRow = rowCount++;
                    }
                }
                if (ChatObject.isPublic(currentChat)) {
                    usernameRow = rowCount++;
                }
                if (!currentChat.restriction_reason.isEmpty()) {
                    restrictionReasonRow = rowCount++;
                }
            }
            if (infoHeaderRow != -1) {
                notificationsDividerRow = rowCount++;
            }
            notificationsRow = rowCount++;
            infoSectionRow = rowCount++;

            if (ChatObject.isChannel(currentChat) && !currentChat.megagroup) {
                if (chatInfo != null && (currentChat.creator || chatInfo.can_view_participants)) {
                    membersHeaderRow = rowCount++;
                    subscribersRow = rowCount++;
                    if (chatInfo.requests_pending > 0) {
                        subscribersRequestsRow = rowCount++;
                    }
                    administratorsRow = rowCount++;
                    if (chatInfo.banned_count != 0 || chatInfo.kicked_count != 0) {
                        blockedUsersRow = rowCount++;
                    }
                    membersSectionRow = rowCount++;
                }
            }

            if (ChatObject.isChannel(currentChat)) {
                if (!isTopic && chatInfo != null && currentChat.megagroup && chatInfo.participants != null && chatInfo.participants.participants != null && !chatInfo.participants.participants.isEmpty()) {
                    if (!ChatObject.isNotInChat(currentChat) && ChatObject.canAddUsers(currentChat) && chatInfo.participants_count < getMessagesController().maxMegagroupCount) {
                        addMemberRow = rowCount++;
                    }
                    int count = chatInfo.participants.participants.size();
                    if ((count <= 5 || !hasMedia || usersForceShowingIn == 1) && usersForceShowingIn != 2) {
                        if (addMemberRow == -1) {
                            membersHeaderRow = rowCount++;
                        }
                        membersStartRow = rowCount;
                        rowCount += count;
                        membersEndRow = rowCount;
                        membersSectionRow = rowCount++;
                        visibleChatParticipants.addAll(chatInfo.participants.participants);
                        if (sortedUsers != null) {
                            visibleSortedUsers.addAll(sortedUsers);
                        }
                        usersForceShowingIn = 1;
                        if (sharedMediaLayout != null) {
                            sharedMediaLayout.setChatUsers(null, null);
                        }
                    } else {
                        if (addMemberRow != -1) {
                            membersSectionRow = rowCount++;
                        }
                        if (sharedMediaLayout != null) {
                            if (!sortedUsers.isEmpty()) {
                                usersForceShowingIn = 2;
                            }
                            sharedMediaLayout.setChatUsers(sortedUsers, chatInfo);
                        }
                    }
                } else {
                    if (!ChatObject.isNotInChat(currentChat) && ChatObject.canAddUsers(currentChat) && chatInfo != null && chatInfo.participants_hidden) {
                        addMemberRow = rowCount++;
                        membersSectionRow = rowCount++;
                    }
                    if (sharedMediaLayout != null) {
                        sharedMediaLayout.updateAdapters();
                    }
                }

                if (lastSectionRow == -1 && currentChat.left && !currentChat.kicked) {
                    joinRow = rowCount++;
                    lastSectionRow = rowCount++;
                }
            } else if (chatInfo != null) {
                if (!isTopic && chatInfo.participants != null && chatInfo.participants.participants != null && !(chatInfo.participants instanceof TLRPC.TL_chatParticipantsForbidden)) {
                    if (ChatObject.canAddUsers(currentChat) || currentChat.default_banned_rights == null || !currentChat.default_banned_rights.invite_users) {
                        addMemberRow = rowCount++;
                    }
                    int count = chatInfo.participants.participants.size();
                    if (count <= 5 || !hasMedia) {
                        if (addMemberRow == -1) {
                            membersHeaderRow = rowCount++;
                        }
                        membersStartRow = rowCount;
                        rowCount += chatInfo.participants.participants.size();
                        membersEndRow = rowCount;
                        membersSectionRow = rowCount++;
                        visibleChatParticipants.addAll(chatInfo.participants.participants);
                        if (sortedUsers != null) {
                            visibleSortedUsers.addAll(sortedUsers);
                        }
                        if (sharedMediaLayout != null) {
                            sharedMediaLayout.setChatUsers(null, null);
                        }
                    } else {
                        if (addMemberRow != -1) {
                            membersSectionRow = rowCount++;
                        }
                        if (sharedMediaLayout != null) {
                            sharedMediaLayout.setChatUsers(sortedUsers, chatInfo);
                        }
                    }
                } else {
                    if (!ChatObject.isNotInChat(currentChat) && ChatObject.canAddUsers(currentChat) && chatInfo.participants_hidden) {
                        addMemberRow = rowCount++;
                        membersSectionRow = rowCount++;
                    }
                    if (sharedMediaLayout != null) {
                        sharedMediaLayout.updateAdapters();
                    }
                }
            }

            if (hasMedia) {
                sharedMediaRow = rowCount++;
            }
        }
        if (sharedMediaRow == -1) {
            bottomPaddingRow = rowCount++;
        }
        final int actionBarHeight = actionBar != null ? ActionBar.getCurrentActionBarHeight() + (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) : 0;
        if (listView == null || prevRowsCount > rowCount || listContentHeight != 0 && listContentHeight + actionBarHeight + AndroidUtilities.dp(88) < listView.getMeasuredHeight()) {
            lastMeasuredContentWidth = 0;
        }
    }

    private Drawable getScamDrawable(int type) {
        if (scamDrawable == null) {
            scamDrawable = new ScamDrawable(11, type);
            scamDrawable.setColor(getThemedColor(Theme.key_avatar_subtitleInProfileBlue));
        }
        return scamDrawable;
    }

    private Drawable getLockIconDrawable() {
        if (lockIconDrawable == null) {
            lockIconDrawable = Theme.chat_lockIconDrawable.getConstantState().newDrawable().mutate();
        }
        return lockIconDrawable;
    }

    private Drawable getVerifiedCrossfadeDrawable() {
        if (verifiedCrossfadeDrawable == null) {
            verifiedDrawable = Theme.profile_verifiedDrawable.getConstantState().newDrawable().mutate();
            verifiedCheckDrawable = Theme.profile_verifiedCheckDrawable.getConstantState().newDrawable().mutate();
            verifiedCrossfadeDrawable = new CrossfadeDrawable(new CombinedDrawable(verifiedDrawable, verifiedCheckDrawable), ContextCompat.getDrawable(getParentActivity(), R.drawable.verified_profile));
        }
        return verifiedCrossfadeDrawable;
    }

    private Drawable getPremiumCrossfadeDrawable() {
        if (premiumCrossfadeDrawable == null) {
            premiumStarDrawable = ContextCompat.getDrawable(getParentActivity(), R.drawable.msg_premium_liststar).mutate();
            premiumStarDrawable.setColorFilter(getThemedColor(Theme.key_profile_verifiedBackground), PorterDuff.Mode.MULTIPLY);
            premiumCrossfadeDrawable = new CrossfadeDrawable(premiumStarDrawable, ContextCompat.getDrawable(getParentActivity(), R.drawable.msg_premium_prolfilestar).mutate());
        }
        return premiumCrossfadeDrawable;
    }

    private Drawable getEmojiStatusDrawable(TLRPC.EmojiStatus emojiStatus, boolean switchable, boolean animated, int a) {
        if (emojiStatusDrawable[a] == null) {
            emojiStatusDrawable[a] = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(nameTextView[a], AndroidUtilities.dp(24), a == 0 ? AnimatedEmojiDrawable.CACHE_TYPE_EMOJI_STATUS : AnimatedEmojiDrawable.CACHE_TYPE_KEYBOARD);
            if (fragmentViewAttached) {
                emojiStatusDrawable[a].attach();
            }
        }
        if (emojiStatus instanceof TLRPC.TL_emojiStatus) {
            emojiStatusDrawable[a].set(((TLRPC.TL_emojiStatus) emojiStatus).document_id, animated);
        } else if (emojiStatus instanceof TLRPC.TL_emojiStatusUntil && ((TLRPC.TL_emojiStatusUntil) emojiStatus).until > (int) (System.currentTimeMillis() / 1000) && !reportSpam) {
            emojiStatusDrawable[a].set(((TLRPC.TL_emojiStatusUntil) emojiStatus).document_id, animated);
        } else {
            emojiStatusDrawable[a].set(getPremiumCrossfadeDrawable(), animated);
        }
        updateEmojiStatusDrawableColor();
        return emojiStatusDrawable[a];
    }

    private float lastEmojiStatusProgress;
    private void updateEmojiStatusDrawableColor() {
        updateEmojiStatusDrawableColor(lastEmojiStatusProgress);
    }
    private void updateEmojiStatusDrawableColor(float progress) {
        final int color =
                ColorUtils.blendARGB(
                        AndroidUtilities.getOffsetColor(getThemedColor(Theme.key_profile_verifiedBackground), getThemedColor(Theme.key_player_actionBarTitle), mediaHeaderAnimationProgress, 1.0f),
                        0xffffffff,
                        progress
                );
        for (int a = 0; a < 2; ++a) {
            if (emojiStatusDrawable[a] != null) {
                emojiStatusDrawable[a].setColor(color);
            }
        }
        animatedStatusView.setColor(color);
        lastEmojiStatusProgress = progress;
    }

    private void updateEmojiStatusEffectPosition() {
        animatedStatusView.setScaleX(nameTextView[1].getScaleX());
        animatedStatusView.setScaleY(nameTextView[1].getScaleY());
        animatedStatusView.translate(
                nameTextView[1].getX() + nameTextView[1].getRightDrawableX() * nameTextView[1].getScaleX(),
                nameTextView[1].getY() + (nameTextView[1].getHeight() - (nameTextView[1].getHeight() - nameTextView[1].getRightDrawableY()) * nameTextView[1].getScaleY())
        );
    }

    private void updateProfileData(boolean reload) {
        if (avatarContainer == null || nameTextView == null || getParentActivity() == null) {
            return;
        }
        String onlineTextOverride;
        int currentConnectionState = getConnectionsManager().getConnectionState();
        if (currentConnectionState == ConnectionsManager.ConnectionStateWaitingForNetwork) {
            onlineTextOverride = LocaleController.getString("WaitingForNetwork", R.string.WaitingForNetwork);
        } else if (currentConnectionState == ConnectionsManager.ConnectionStateConnecting) {
            onlineTextOverride = LocaleController.getString("Connecting", R.string.Connecting);
        } else if (currentConnectionState == ConnectionsManager.ConnectionStateUpdating) {
            onlineTextOverride = LocaleController.getString("Updating", R.string.Updating);
        } else if (currentConnectionState == ConnectionsManager.ConnectionStateConnectingToProxy) {
            onlineTextOverride = LocaleController.getString("ConnectingToProxy", R.string.ConnectingToProxy);
        } else {
            onlineTextOverride = null;
        }

        TLRPC.TL_forumTopic topic = null;
        long id = 0;
        int dc = 0;
        boolean shortStatus;

        hasFallbackPhoto = false;
        hasCustomPhoto = false;
        if (userId != 0) {
            TLRPC.User user = getMessagesController().getUser(userId);
            if (user == null) {
                return;
            }
            shortStatus = user.photo != null && user.photo.personal;
            TLRPC.FileLocation photoBig = null;
            if (user.photo != null) {
                photoBig = user.photo.photo_big;
            }
            avatarDrawable.setInfo(user);

            final ImageLocation imageLocation = ImageLocation.getForUserOrChat(user, ImageLocation.TYPE_BIG);
            final ImageLocation thumbLocation = ImageLocation.getForUserOrChat(user, ImageLocation.TYPE_SMALL);
            final ImageLocation videoThumbLocation = ImageLocation.getForUserOrChat(user, ImageLocation.TYPE_VIDEO_BIG);
            VectorAvatarThumbDrawable vectorAvatarThumbDrawable = null;
            TLRPC.VideoSize vectorAvatar = null;
            if (userInfo != null) {
                vectorAvatar = FileLoader.getVectorMarkupVideoSize(user.photo != null && user.photo.personal ? userInfo.personal_photo : userInfo.profile_photo);
                if (vectorAvatar != null) {
                    vectorAvatarThumbDrawable = new VectorAvatarThumbDrawable(vectorAvatar, user.premium, VectorAvatarThumbDrawable.TYPE_PROFILE);
                }
            }
            final ImageLocation videoLocation = avatarsViewPager.getCurrentVideoLocation(thumbLocation, imageLocation);
            if (avatar == null) {
                avatarsViewPager.initIfEmpty(vectorAvatarThumbDrawable, imageLocation, thumbLocation, reload);
            }
            if (avatarBig == null) {
                if (vectorAvatar != null) {
                    avatarImage.setImageDrawable(vectorAvatarThumbDrawable);
                } else if (videoThumbLocation != null && !user.photo.personal) {
                    avatarImage.getImageReceiver().setVideoThumbIsSame(true);
                    avatarImage.setImage(videoThumbLocation, "avatar", thumbLocation, "50_50", avatarDrawable, user);
                } else {
                    avatarImage.setImage(videoLocation, ImageLoader.AUTOPLAY_FILTER, thumbLocation, "50_50", avatarDrawable, user);
                }
            }

            if (thumbLocation != null && setAvatarRow != -1 || thumbLocation == null && setAvatarRow == -1) {
                updateListAnimated(false);
                needLayout(true);
            }
            if (imageLocation != null && (prevLoadedImageLocation == null || imageLocation.photoId != prevLoadedImageLocation.photoId)) {
                prevLoadedImageLocation = imageLocation;
                getFileLoader().loadFile(imageLocation, user, null, FileLoader.PRIORITY_LOW, 1);
            }

            CharSequence newString = UserObject.getUserName(user);
            String newString2;
            if (user.id == getUserConfig().getClientUserId()) {
                if (UserObject.hasFallbackPhoto(getUserInfo())) {
                    newString2 = "";
                    hasFallbackPhoto = true;
                    TLRPC.PhotoSize smallSize = FileLoader.getClosestPhotoSizeWithSize(getUserInfo().fallback_photo.sizes, 1000);
                    if (smallSize != null) {
                        fallbackImage.setImage(ImageLocation.getForPhoto(smallSize, getUserInfo().fallback_photo), "50_50", (Drawable) null, 0, null, UserConfig.getInstance(currentAccount).getCurrentUser(), 0);
                    }
                } else {
                    newString2 = LocaleController.getString("Online", R.string.Online);
                }
            } else if (user.id == 333000 || user.id == 777000 || user.id == 42777) {
                newString2 = LocaleController.getString("ServiceNotifications", R.string.ServiceNotifications);
            } else if (MessagesController.isSupportUser(user)) {
                newString2 = LocaleController.getString("SupportStatus", R.string.SupportStatus);
            } else if (isBot) {
                newString2 = LocaleController.getString("Bot", R.string.Bot);
            } else {
                isOnline[0] = false;
                newString2 = LocaleController.formatUserStatus(currentAccount, user, isOnline, shortStatus ? new boolean[1] : null);
                if (onlineTextView[1] != null && !mediaHeaderVisible) {
                    String key = isOnline[0] ? Theme.key_profile_status : Theme.key_avatar_subtitleInProfileBlue;
                    onlineTextView[1].setTag(key);
                    if (!isPulledDown) {
                        onlineTextView[1].setTextColor(getThemedColor(key));
                    }
                }
            }
            hasCustomPhoto = user.photo != null && user.photo.personal;
            try {
                newString = Emoji.replaceEmoji(newString, nameTextView[1].getPaint().getFontMetricsInt(), AndroidUtilities.dp(24), false);
            } catch (Exception ignore) {
            }
            for (int a = 0; a < 2; a++) {
                if (nameTextView[a] == null) {
                    continue;
                }
                if (a == 0 && user.id != getUserConfig().getClientUserId() && user.id / 1000 != 777 && user.id / 1000 != 333 && user.phone != null && user.phone.length() != 0 && getContactsController().contactsDict.get(user.id) == null &&
                        (getContactsController().contactsDict.size() != 0 || !getContactsController().isLoadingContacts())) {
                    String phoneString = PhoneFormat.getInstance().format("+" + user.phone);
                    nameTextView[a].setText(phoneString);
                } else {
                    nameTextView[a].setText(newString);
                }
                if (a == 0 && onlineTextOverride != null) {
                    onlineTextView[a].setText(onlineTextOverride);
                } else {
                    onlineTextView[a].setText(newString2);
                }
                Drawable leftIcon = currentEncryptedChat != null ? getLockIconDrawable() : null;
                Drawable rightIcon = null;
                boolean rightIconIsPremium = false, rightIconIsStatus = false;
                nameTextView[a].setRightDrawableOutside(a == 0);
                if (a == 0) {
                    if (user.scam || user.fake) {
                        rightIcon = getScamDrawable(user.scam ? 0 : 1);
                        nameTextViewRightDrawableContentDescription = LocaleController.getString("ScamMessage", R.string.ScamMessage);
                    } else if (user.verified) {
                        rightIcon = getVerifiedCrossfadeDrawable();
                        nameTextViewRightDrawableContentDescription = LocaleController.getString("AccDescrVerified", R.string.AccDescrVerified);
                    } else if (user.emoji_status instanceof TLRPC.TL_emojiStatus || user.emoji_status instanceof TLRPC.TL_emojiStatusUntil && ((TLRPC.TL_emojiStatusUntil) user.emoji_status).until > (int) (System.currentTimeMillis() / 1000)) {
                        rightIconIsStatus = true;
                        rightIconIsPremium = false;
                        rightIcon = getEmojiStatusDrawable(user.emoji_status, false, false, a);
                        nameTextViewRightDrawableContentDescription = LocaleController.getString("AccDescrPremium", R.string.AccDescrPremium);
                    } else if (getMessagesController().isPremiumUser(user)) {
                        rightIconIsStatus = false;
                        rightIconIsPremium = true;
                        rightIcon = getEmojiStatusDrawable(null, false, false, a);
                        nameTextViewRightDrawableContentDescription = LocaleController.getString("AccDescrPremium", R.string.AccDescrPremium);
                    } else if (getMessagesController().isDialogMuted(dialogId != 0 ? dialogId : userId, topicId)) {
                        rightIcon = getThemedDrawable(Theme.key_drawable_muteIconDrawable);
                        nameTextViewRightDrawableContentDescription = LocaleController.getString("NotificationsMuted", R.string.NotificationsMuted);
                    } else {
                        rightIcon = null;
                        nameTextViewRightDrawableContentDescription = null;
                    }
                } else if (a == 1) {
                    if (user.scam || user.fake) {
                        rightIcon = getScamDrawable(user.scam ? 0 : 1);
                    } else if (user.verifiedExtended()) {
                        rightIcon = getVerifiedCrossfadeDrawable();
                    } else if (user.emoji_status instanceof TLRPC.TL_emojiStatus || user.emoji_status instanceof TLRPC.TL_emojiStatusUntil && ((TLRPC.TL_emojiStatusUntil) user.emoji_status).until > (int) (System.currentTimeMillis() / 1000)) {
                        rightIconIsStatus = true;
                        rightIconIsPremium = false;
                        rightIcon = getEmojiStatusDrawable(user.emoji_status, true, true, a);
                    } else if (getMessagesController().isPremiumUser(user)) {
                        rightIconIsStatus = false;
                        rightIconIsPremium = true;
                        rightIcon = getEmojiStatusDrawable(null, true, true, a);
                    }
                }
                nameTextView[a].setLeftDrawable(leftIcon);
                nameTextView[a].setRightDrawable(rightIcon);
                if (a == 1 && (rightIconIsStatus || rightIconIsPremium)) {
                    nameTextView[a].setRightDrawableOutside(true);
                }
                if (user.self && getMessagesController().isPremiumUser(user)) {
                    final SimpleTextView textView = nameTextView[a];
                    nameTextView[a].setRightDrawableOnClick(v -> {
                        showStatusSelect();
                    });
                }
                if (!user.self && getMessagesController().isPremiumUser(user)) {
                    final SimpleTextView textView = nameTextView[a];
                    nameTextView[a].setRightDrawableOnClick(v -> {
                        PremiumPreviewBottomSheet premiumPreviewBottomSheet = new PremiumPreviewBottomSheet(ProfileActivity.this, currentAccount, user, resourcesProvider);
                        int[] coords = new int[2];
                        textView.getLocationOnScreen(coords);
                        premiumPreviewBottomSheet.startEnterFromX = textView.rightDrawableX;
                        premiumPreviewBottomSheet.startEnterFromY = textView.rightDrawableY;
                        premiumPreviewBottomSheet.startEnterFromScale = textView.getScaleX();
                        premiumPreviewBottomSheet.startEnterFromX1 = textView.getLeft();
                        premiumPreviewBottomSheet.startEnterFromY1 = textView.getTop();
                        premiumPreviewBottomSheet.startEnterFromView = textView;
                        if (textView.getRightDrawable() == emojiStatusDrawable[1] && emojiStatusDrawable[1] != null && emojiStatusDrawable[1].getDrawable() instanceof AnimatedEmojiDrawable) {
                            premiumPreviewBottomSheet.startEnterFromScale *= 0.98f;
                            TLRPC.Document document = ((AnimatedEmojiDrawable) emojiStatusDrawable[1].getDrawable()).getDocument();
                            if (document != null) {
                                BackupImageView icon = new BackupImageView(getContext());
                                String filter = "160_160";
                                ImageLocation mediaLocation;
                                String mediaFilter;
                                SvgHelper.SvgDrawable thumbDrawable = DocumentObject.getSvgThumb(document.thumbs, Theme.key_windowBackgroundWhiteGrayIcon, 0.2f);
                                TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                                if ("video/webm".equals(document.mime_type)) {
                                    mediaLocation = ImageLocation.getForDocument(document);
                                    mediaFilter = filter + "_" + ImageLoader.AUTOPLAY_FILTER;
                                    if (thumbDrawable != null) {
                                        thumbDrawable.overrideWidthAndHeight(512, 512);
                                    }
                                } else {
                                    if (thumbDrawable != null && MessageObject.isAnimatedStickerDocument(document, false)) {
                                        thumbDrawable.overrideWidthAndHeight(512, 512);
                                    }
                                    mediaLocation = ImageLocation.getForDocument(document);
                                    mediaFilter = filter;
                                }
                                icon.setLayerNum(7);
                                icon.setRoundRadius(AndroidUtilities.dp(4));
                                icon.setImage(mediaLocation, mediaFilter, ImageLocation.getForDocument(thumb, document), "140_140", thumbDrawable, document);
                                if (AnimatedEmojiDrawable.isDefaultStatusEmoji(emojiStatusDrawable[1].getDrawable())) {
                                    icon.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_windowBackgroundWhiteBlueIcon), PorterDuff.Mode.SRC_IN));
                                } else if (((AnimatedEmojiDrawable) emojiStatusDrawable[1].getDrawable()).canOverrideColor()) {
                                    icon.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_windowBackgroundWhiteBlackText), PorterDuff.Mode.SRC_IN));
                                    premiumPreviewBottomSheet.statusStickerSet = MessageObject.getInputStickerSet(document);
                                } else {
                                    premiumPreviewBottomSheet.statusStickerSet = MessageObject.getInputStickerSet(document);
                                }
                                premiumPreviewBottomSheet.overrideTitleIcon = icon;
                                premiumPreviewBottomSheet.isEmojiStatus = true;
                            }
                        }
                        showDialog(premiumPreviewBottomSheet);
                    });
                }
            }

            if (userId == UserConfig.getInstance(currentAccount).clientUserId) {
                onlineTextView[2].setText(LocaleController.getString("FallbackTooltip", R.string.FallbackTooltip));
                onlineTextView[3].setText(LocaleController.getString("Online", R.string.Online));
            } else {
                if (user.photo != null && user.photo.personal && user.photo.has_video) {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(newString2);
                    spannableStringBuilder.setSpan(new EmptyStubSpan(), 0, newString2.length(), 0);
                    spannableStringBuilder.append(" d ");
                    spannableStringBuilder.append(LocaleController.getString("CustomAvatarTooltipVideo", R.string.CustomAvatarTooltipVideo));
                    spannableStringBuilder.setSpan(new DotDividerSpan(), newString2.length() + 1, newString2.length() + 2, 0);
                    onlineTextView[2].setText(spannableStringBuilder);
                } else {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(newString2);
                    spannableStringBuilder.setSpan(new EmptyStubSpan(), 0, newString2.length(), 0);
                    spannableStringBuilder.append(" d ");
                    spannableStringBuilder.append(LocaleController.getString("CustomAvatarTooltip", R.string.CustomAvatarTooltip));
                    spannableStringBuilder.setSpan(new DotDividerSpan(), newString2.length() + 1, newString2.length() + 2, 0);
                    onlineTextView[2].setText(spannableStringBuilder);
                }
            }

            onlineTextView[2].setVisibility(View.VISIBLE);
            onlineTextView[3].setVisibility(View.VISIBLE);

            if (previousTransitionFragment != null) {
                previousTransitionFragment.checkAndUpdateAvatar();
            }

            dc = user.photo != null && user.photo.dc_id != 0 ? user.photo.dc_id : (UserObject.isUserSelf(user) && getMessagesController().thisDc > 0) ? getMessagesController().thisDc : 0;
            if (dc != 0) {
                idTextView.setText("ID: " + userId + ", DC: " + dc);
            } else {
                idTextView.setText("ID: " + userId);
            }
            id = userId;
            avatarImage.getImageReceiver().setVisible(!PhotoViewer.isShowingImage(photoBig), false);
        } else if (chatId != 0) {
            TLRPC.Chat chat = getMessagesController().getChat(chatId);
            if (chat != null) {
                currentChat = chat;
            } else {
                chat = currentChat;
            }

            if (isTopic) {
                topic = getMessagesController().getTopicsController().findTopic(chatId, topicId);
            }

            CharSequence statusString;
            CharSequence profileStatusString;
            boolean profileStatusIsButton = false;
            if (ChatObject.isChannel(chat)) {
                if (!isTopic && (chatInfo == null || !currentChat.megagroup && (chatInfo.participants_count == 0 || ChatObject.hasAdminRights(currentChat) || chatInfo.can_view_participants))) {
                    if (currentChat.megagroup) {
                        statusString = profileStatusString = LocaleController.getString("Loading", R.string.Loading).toLowerCase();
                    } else {
                        if (ChatObject.isPublic(chat)) {
                            statusString = profileStatusString = LocaleController.getString("ChannelPublic", R.string.ChannelPublic).toLowerCase();
                        } else {
                            statusString = profileStatusString = LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate).toLowerCase();
                        }
                    }
                } else {
                    if (isTopic) {
                        int count = 0;
                        if (topic != null) {
                            count = topic.totalMessagesCount - 1;
                        }
                        if (count > 0) {
                            statusString = LocaleController.formatPluralString("messages", count, count);
                        } else {
                            statusString = LocaleController.formatString("TopicProfileStatus", R.string.TopicProfileStatus, chat.title);
                        }
                        SpannableString arrowString = new SpannableString(">");
                        arrowString.setSpan(new ColoredImageSpan(R.drawable.arrow_newchat), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        profileStatusString = new SpannableStringBuilder(chat.title).append(' ').append(arrowString);
                        profileStatusIsButton = true;
                    } else if (currentChat.megagroup) {
                        if (onlineCount > 1 && chatInfo.participants_count != 0) {
                            statusString = String.format("%s, %s", LocaleController.formatPluralString("Members", chatInfo.participants_count), LocaleController.formatPluralString("OnlineCount", Math.min(onlineCount, chatInfo.participants_count)));
                            profileStatusString = String.format("%s, %s", LocaleController.formatPluralStringComma("Members", chatInfo.participants_count), LocaleController.formatPluralStringComma("OnlineCount", Math.min(onlineCount, chatInfo.participants_count)));
                        } else {
                            if (chatInfo.participants_count == 0) {
                                if (chat.has_geo) {
                                    statusString = profileStatusString = LocaleController.getString("MegaLocation", R.string.MegaLocation).toLowerCase();
                                } else if (ChatObject.isPublic(chat)) {
                                    statusString = profileStatusString = LocaleController.getString("MegaPublic", R.string.MegaPublic).toLowerCase();
                                } else {
                                    statusString = profileStatusString = LocaleController.getString("MegaPrivate", R.string.MegaPrivate).toLowerCase();
                                }
                            } else {
                                statusString = LocaleController.formatPluralString("Members", chatInfo.participants_count);
                                profileStatusString = LocaleController.formatPluralStringComma("Members", chatInfo.participants_count);
                            }
                        }
                    } else {
                        int[] result = new int[1];
                        String shortNumber = LocaleController.formatShortNumber(chatInfo.participants_count, result);
                        if (currentChat.megagroup) {
                            statusString = LocaleController.formatPluralString("Members", chatInfo.participants_count);
                            profileStatusString = LocaleController.formatPluralStringComma("Members", chatInfo.participants_count);
                        } else {
                            statusString = LocaleController.formatPluralString("Subscribers", chatInfo.participants_count);
                            profileStatusString = LocaleController.formatPluralStringComma("Subscribers", chatInfo.participants_count);
                        }
                    }
                }
            } else {
                if (ChatObject.isKickedFromChat(chat)) {
                    statusString = profileStatusString = LocaleController.getString("YouWereKicked", R.string.YouWereKicked);
                } else if (ChatObject.isLeftFromChat(chat)) {
                    statusString = profileStatusString = LocaleController.getString("YouLeft", R.string.YouLeft);
                } else {
                    int count = chat.participants_count;
                    if (chatInfo != null && chatInfo.participants != null) {
                        count = chatInfo.participants.participants.size();
                    }
                    if (count != 0 && onlineCount > 1) {
                        statusString = profileStatusString = String.format("%s, %s", LocaleController.formatPluralString("Members", count), LocaleController.formatPluralString("OnlineCount", onlineCount));
                    } else {
                        statusString = profileStatusString = LocaleController.formatPluralString("Members", count);
                    }
                }
            }

            boolean changed = false;
            for (int a = 0; a < 2; a++) {
                if (nameTextView[a] == null) {
                    continue;
                }
                if (isTopic) {
                    CharSequence title = topic == null ? "" : topic.title;
                    try {
                        title = Emoji.replaceEmoji(title, nameTextView[a].getPaint().getFontMetricsInt(), AndroidUtilities.dp(24), false);
                    } catch (Exception ignore) {
                    }
                    if (nameTextView[a].setText(title)) {
                        changed = true;
                    }
                } else if (chat.title != null) {
                    CharSequence title = chat.title;
                    try {
                        title = Emoji.replaceEmoji(title, nameTextView[a].getPaint().getFontMetricsInt(), AndroidUtilities.dp(24), false);
                    } catch (Exception ignore) {
                    }
                    if (nameTextView[a].setText(MessageHelper.INSTANCE.zalgoFilter(title))) {
                        changed = true;
                    }
                }
                nameTextView[a].setLeftDrawable(null);
                nameTextView[a].setRightDrawableOutside(a == 0);
                if (a != 0) {
                    if (chat.scam || chat.fake) {
                        nameTextView[a].setRightDrawable(getScamDrawable(chat.scam ? 0 : 1));
                        nameTextViewRightDrawableContentDescription = LocaleController.getString("ScamMessage", R.string.ScamMessage);
                    } else if (chat.verifiedExtended()) {
                        nameTextView[a].setRightDrawable(getVerifiedCrossfadeDrawable());
                        nameTextViewRightDrawableContentDescription = LocaleController.getString("AccDescrVerified", R.string.AccDescrVerified);
                    } else {
                        nameTextView[a].setRightDrawable(null);
                        nameTextViewRightDrawableContentDescription = null;
                    }
                } else {
                    if (chat.scam || chat.fake) {
                        nameTextView[a].setRightDrawable(getScamDrawable(chat.scam ? 0 : 1));
                    } else if (chat.verified) {
                        nameTextView[a].setRightDrawable(getVerifiedCrossfadeDrawable());
                    } else if (getMessagesController().isDialogMuted(-chatId, topicId)) {
                        nameTextView[a].setRightDrawable(getThemedDrawable(Theme.key_drawable_muteIconDrawable));
                    } else {
                        nameTextView[a].setRightDrawable(null);
                    }
                }
                if (a == 0 && onlineTextOverride != null) {
                    onlineTextView[a].setText(onlineTextOverride);
                } else {
                    if ((currentChat.megagroup && chatInfo != null && onlineCount > 0) || isTopic) {
                        onlineTextView[a].setText(a == 0 ? statusString : profileStatusString);
                    } else if (a == 0 && ChatObject.isChannel(currentChat) && chatInfo != null && chatInfo.participants_count != 0 && (currentChat.megagroup || currentChat.broadcast)) {
                        int[] result = new int[1];
                        boolean ignoreShort = AndroidUtilities.isAccessibilityScreenReaderEnabled();
                        String shortNumber = ignoreShort ? String.valueOf(result[0] = chatInfo.participants_count) : LocaleController.formatShortNumber(chatInfo.participants_count, result);
                        if (currentChat.megagroup) {
                            if (chatInfo.participants_count == 0) {
                                if (chat.has_geo) {
                                    onlineTextView[a].setText(LocaleController.getString("MegaLocation", R.string.MegaLocation).toLowerCase());
                                } else if (ChatObject.isPublic(chat)) {
                                    onlineTextView[a].setText(LocaleController.getString("MegaPublic", R.string.MegaPublic).toLowerCase());
                                } else {
                                    onlineTextView[a].setText(LocaleController.getString("MegaPrivate", R.string.MegaPrivate).toLowerCase());
                                }
                            } else {
                                onlineTextView[a].setText(LocaleController.formatPluralString("Members", result[0]).replace(String.format("%d", result[0]), shortNumber));
                            }
                        } else {
                            onlineTextView[a].setText(LocaleController.formatPluralString("Subscribers", result[0]).replace(String.format("%d", result[0]), shortNumber));
                        }
                    } else {
                        onlineTextView[a].setText(a == 0 ? statusString : profileStatusString);
                    }
                }
                if (a == 1 && isTopic) {
                    if (profileStatusIsButton) {
                        onlineTextView[a].setOnClickListener(e -> goToForum());
                    } else {
                        onlineTextView[a].setOnClickListener(null);
                        onlineTextView[a].setClickable(false);
                    }
                }
            }
            id = chatId;
            if (changed) {
                needLayout(true);
            }

            TLRPC.FileLocation photoBig = null;
            if (chat.photo != null && !isTopic) {
                photoBig = chat.photo.photo_big;
            }

            final ImageLocation imageLocation;
            final ImageLocation thumbLocation;
            final ImageLocation videoLocation;
            if (isTopic) {
                imageLocation = null;
                thumbLocation = null;
                videoLocation = null;
                ForumUtilities.setTopicIcon(avatarImage, topic, true, true, resourcesProvider);
            } else {
                avatarDrawable.setInfo(chat);
                imageLocation = ImageLocation.getForUserOrChat(chat, ImageLocation.TYPE_BIG);
                thumbLocation = ImageLocation.getForUserOrChat(chat, ImageLocation.TYPE_SMALL);
                videoLocation = avatarsViewPager.getCurrentVideoLocation(thumbLocation, imageLocation);
            }

            boolean initied = avatarsViewPager.initIfEmpty(null, imageLocation, thumbLocation, reload);
            if ((imageLocation == null || initied) && isPulledDown) {
                final View view = layoutManager.findViewByPosition(0);
                if (view != null) {
                    listView.smoothScrollBy(0, view.getTop() - AndroidUtilities.dp(88), CubicBezierInterpolator.EASE_OUT_QUINT);
                }
            }
            String filter;
            if (videoLocation != null && videoLocation.imageType == FileLoader.IMAGE_TYPE_ANIMATION) {
                filter = ImageLoader.AUTOPLAY_FILTER;
            } else {
                filter = null;
            }
            if (avatarBig == null && !isTopic) {
                avatarImage.setImage(videoLocation, filter, thumbLocation, "50_50", avatarDrawable, chat);
            }
            if (imageLocation != null && (prevLoadedImageLocation == null || imageLocation.photoId != prevLoadedImageLocation.photoId)) {
                prevLoadedImageLocation = imageLocation;
                getFileLoader().loadFile(imageLocation, chat, null, FileLoader.PRIORITY_LOW, 1);
            }
            avatarImage.getImageReceiver().setVisible(!PhotoViewer.isShowingImage(photoBig), false);
            if (chat.photo != null && chat.photo.dc_id != 0) {
                dc = chat.photo.dc_id;
                idTextView.setText("ID: " + chatId + ", DC: " + dc);
            } else {
                idTextView.setText("ID: " + chatId);
            }
        }
        if (id != 0) {
            long finalId = id;
            int finalDc = dc;
            idTextView.setOnClickListener(v -> {
                BottomBuilder builder = new BottomBuilder(getParentActivity());
                builder.addTitle(finalId + "");
                builder.addItem(LocaleController.getString("Copy", R.string.Copy), R.drawable.msg_copy, __ -> {
                    AlertUtil.copyAndAlert(finalId + "");
                    return Unit.INSTANCE;
                });
                if (finalId == userId) {
                    builder.addItem(LocaleController.getString("CopyLink", R.string.CopyLink), R.drawable.profile_link, __ -> {
                        AlertUtil.copyLinkAndAlert("tg://user?id=" + finalId);
                        return Unit.INSTANCE;
                    });
                    builder.addItem(LocaleController.getString("CopyLink", R.string.CopyLink) + " (Android)", R.drawable.profile_link, __ -> {
                        AlertUtil.copyLinkAndAlert("tg://openmessage?user_id=" + finalId);
                        return Unit.INSTANCE;
                    });
                    builder.addItem(LocaleController.getString("CopyLink", R.string.CopyLink) + " (IOS)", R.drawable.profile_link, __ -> {
                        AlertUtil.copyLinkAndAlert("https://t.me/@id" + finalId);
                        return Unit.INSTANCE;
                    });
                } else {
                    builder.addItem(LocaleController.getString("CopyLink", R.string.CopyLink) + " (Android)", R.drawable.profile_link, __ -> {
                        AlertUtil.copyLinkAndAlert("tg://openmessage?chat_id=" + finalId);
                        return Unit.INSTANCE;
                    });
                }
                if (finalDc != 0) {
                    builder.addItem(LocaleController.getString("DatacenterStatus", R.string.DatacenterStatus), R.drawable.msg_stats, __ -> {
                        idTextView.setVisibility(View.GONE);
                        presentFragment(new DatacenterActivity(finalDc));
                        return Unit.INSTANCE;
                    });
                }
                builder.addItem(LocaleController.getString("Hide", R.string.Hide), R.drawable.msg_disable, __ -> {
                    idTextView.setVisibility(View.GONE);
                    return Unit.INSTANCE;
                });
                builder.show();
            });

            // NekoX: Move official qrItem into bottom menu when click id
//        if (qrItem != null) {
//            updateQrItemVisibility(true);
//        }
        }
        AndroidUtilities.runOnUIThread(this::updateEmojiStatusEffectPosition);
    }

    private void createActionBarMenu(boolean animated) {
        if (actionBar == null || otherItem == null) {
            return;
        }
        Context context = actionBar.getContext();
        otherItem.removeAllSubItems();
        animatingItem = null;

        editItemVisible = false;
        callItemVisible = false;
        videoCallItemVisible = false;
        canSearchMembers = false;
        boolean selfUser = false;
        if (userId != 0) {
            TLRPC.User user = getMessagesController().getUser(userId);
            if (user == null) {
                return;
            }
            if (UserObject.isUserSelf(user)) {
                otherItem.addSubItem(edit_name, R.drawable.msg_edit, LocaleController.getString("EditName", R.string.EditName));
                selfUser = true;
            } else {
                if (userInfo != null && userInfo.phone_calls_available) {
                    callItemVisible = true;
                    videoCallItemVisible = Build.VERSION.SDK_INT >= 18 && userInfo.video_calls_available;
                }
                if (isBot || getContactsController().contactsDict.get(userId) == null) {
                    if (MessagesController.isSupportUser(user)) {
                        createAutoTranslateItem(userId);
                        if (userBlocked) {
                            otherItem.addSubItem(block_contact, R.drawable.msg_block, LocaleController.getString("Unblock", R.string.Unblock));
                        }
                        otherItem.addSubItem(add_shortcut, R.drawable.msg_home, LocaleController.getString("AddShortcut", R.string.AddShortcut));
                    } else {
                        if (currentEncryptedChat == null) {
                            createAutoDeleteItem(context);
                        }
                        createAutoTranslateItem(userId);
                        otherItem.addSubItem(add_shortcut, R.drawable.msg_home, LocaleController.getString("AddShortcut", R.string.AddShortcut));
                        if (isBot) {
                            otherItem.addSubItem(share, R.drawable.msg_share, LocaleController.getString("BotShare", R.string.BotShare));
                        } else {
                            otherItem.addSubItem(add_contact, R.drawable.msg_addcontact, LocaleController.getString("AddContact", R.string.AddContact));
                        }
                        if (!TextUtils.isEmpty(user.phone)) {
                            otherItem.addSubItem(share_contact, R.drawable.msg_share, LocaleController.getString("ShareContact", R.string.ShareContact));
                        }
                        if (isBot) {
                            if (!userBlocked) {
                                otherItem.addSubItem(block_contact, R.drawable.msg_block,  LocaleController.getString("BotStop", R.string.BotStop)).setColors(getThemedColor(Theme.key_dialogTextRed), getThemedColor(Theme.key_dialogTextRed));
                            } else {
                                otherItem.addSubItem(block_contact, R.drawable.msg_retry, LocaleController.getString("BotRestart", R.string.BotRestart));
                            }
                        } else {
                            otherItem.addSubItem(block_contact, !userBlocked ? R.drawable.msg_block : R.drawable.msg_block, !userBlocked ? LocaleController.getString("BlockContact", R.string.BlockContact) : LocaleController.getString("Unblock", R.string.Unblock));
                        }
                    }
                } else {
                    if (currentEncryptedChat == null) {
                        createAutoDeleteItem(context);
                    }
                    createAutoTranslateItem(userId);

                    if (!TextUtils.isEmpty(user.phone)) {
                        otherItem.addSubItem(share_contact, R.drawable.msg_share, LocaleController.getString("ShareContact", R.string.ShareContact));
                    }
                    otherItem.addSubItem(block_contact, !userBlocked ? R.drawable.msg_block : R.drawable.msg_block, !userBlocked ? LocaleController.getString("BlockContact", R.string.BlockContact) : LocaleController.getString("Unblock", R.string.Unblock));
                    otherItem.addSubItem(edit_contact, R.drawable.msg_edit, LocaleController.getString("EditContact", R.string.EditContact));
                    otherItem.addSubItem(delete_contact, R.drawable.msg_delete, LocaleController.getString("DeleteContact", R.string.DeleteContact));
                }
                if (!UserObject.isDeleted(user) && !isBot && currentEncryptedChat == null && !userBlocked && userId != 333000 && userId != 777000 && userId != 42777) {
//                    if (!user.premium && !BuildVars.IS_BILLING_UNAVAILABLE && !user.self && userInfo != null && !getMessagesController().premiumLocked && !userInfo.premium_gifts.isEmpty()) {
//                        otherItem.addSubItem(gift_premium, R.drawable.msg_gift_premium, LocaleController.getString(R.string.GiftPremium));
//                    }
                    otherItem.addSubItem(start_secret_chat, R.drawable.menu_secret, LocaleController.getString("StartEncryptedChat", R.string.StartEncryptedChat));
                }
                if (StrUtil.isNotBlank(user.username)) {
                    otherItem.addSubItem(qr_code, R.drawable.msg_qrcode, LocaleController.getString("ShareQRCode", R.string.ShareQRCode));
                }
                if (!isBot && getContactsController().contactsDict.get(userId) != null) {
                    otherItem.addSubItem(add_shortcut, R.drawable.msg_home, LocaleController.getString("AddShortcut", R.string.AddShortcut));
                }
            }
        } else if (chatId != 0) {
            TLRPC.Chat chat = getMessagesController().getChat(chatId);
            hasVoiceChatItem = false;
            if (topicId == 0 && ChatObject.canUserDoAdminAction(chat, ChatObject.ACTION_DELETE_MESSAGES)) {
                createAutoDeleteItem(context);
            }
            createAutoTranslateItem(-chatId, topicId, !isTopic || chatInfo == null || !chatInfo.participants_hidden || ChatObject.hasAdminRights(chat));
            if (chat != null && (chat.has_link || (chatInfo != null && chatInfo.linked_chat_id != 0))) {
                String text;
                if (!chat.megagroup) {
                    text = LocaleController.getString("LinkedGroupChat", R.string.LinkedGroupChat);
                    otherItem.addSubItem(view_discussion, R.drawable.msg_discussion, text);
                } else {
                    text = LocaleController.getString("LinkedChannelChat", R.string.LinkedChannelChat);
                    otherItem.addSubItem(view_discussion, R.drawable.msg_channel, text);
                }
            }
            if (ChatObject.isChannel(chat)) {
                if (isTopic) {
                    if (ChatObject.canManageTopic(currentAccount, chat, topicId)) {
                        editItemVisible = true;
                    }
                } else {
                    if (ChatObject.hasAdminRights(chat) || chat.megagroup) {
                        editItemVisible = true;
                    }
                }
                if (chatInfo != null) {
                    if (ChatObject.canManageCalls(chat) && chatInfo.call == null) {
                        otherItem.addSubItem(call_item, R.drawable.msg_voicechat, chat.megagroup && !chat.gigagroup ? LocaleController.getString("StartVoipChat", R.string.StartVoipChat) : LocaleController.getString("StartVoipChannel", R.string.StartVoipChannel));
                        hasVoiceChatItem = true;
                    }
                    if (chatInfo.can_view_stats && topicId == 0) {
                        otherItem.addSubItem(statistics, R.drawable.msg_stats, LocaleController.getString("Statistics", R.string.Statistics));
                    }
                    ChatObject.Call call = getMessagesController().getGroupCall(chatId, false);
                    callItemVisible = call != null;
                }
                if (chat.megagroup) {
                    if (chatInfo == null || !chatInfo.participants_hidden || ChatObject.hasAdminRights(chat)) {
                        canSearchMembers = true;
                        otherItem.addSubItem(search_members, R.drawable.msg_search, LocaleController.getString("SearchMembers", R.string.SearchMembers));
                    }
                    if (!chat.creator && !chat.left && !chat.kicked && !isTopic) {
                        otherItem.addSubItem(leave_group, R.drawable.msg_leave, LocaleController.getString("LeaveMegaMenu", R.string.LeaveMegaMenu));
                    }
                    if (isTopic && ChatObject.canDeleteTopic(currentAccount, chat, topicId)) {
                        otherItem.addSubItem(delete_topic, R.drawable.msg_delete, LocaleController.getPluralString("DeleteTopics", 1));
                    }
                } else {
                    if (ChatObject.isPublic(chat)) {
                        otherItem.addSubItem(share, R.drawable.msg_shareout, LocaleController.getString("BotShare", R.string.BotShare));
                    }
                    if (NekoConfig.channelAlias.Bool()){
                        otherItem.addSubItem(aliasChannelName, R.drawable.profile_admin, LocaleController.getString("setChannelAliasName", R.string.setChannelAliasName));
                    }
                    if (!currentChat.creator && !currentChat.left && !currentChat.kicked) {
                        otherItem.addSubItem(leave_group, R.drawable.msg_leave, LocaleController.getString("LeaveChannelMenu", R.string.LeaveChannelMenu));
                    }
                }
                if (ChatObject.hasAdminRights(currentChat)) {
                    otherItem.addSubItem(event_log, R.drawable.group_log, LocaleController.getString("EventLog", R.string.EventLog));
                }
            } else {
                if (chatInfo != null) {
                    if (ChatObject.canManageCalls(chat) && chatInfo.call == null) {
                        otherItem.addSubItem(call_item, R.drawable.msg_voicechat, LocaleController.getString("StartVoipChat", R.string.StartVoipChat));
                        hasVoiceChatItem = true;
                    }
                    ChatObject.Call call = getMessagesController().getGroupCall(chatId, false);
                    callItemVisible = call != null;
                }
                if (ChatObject.canChangeChatInfo(chat)) {
                    editItemVisible = true;
                }
                if (!ChatObject.isKickedFromChat(chat) && !ChatObject.isLeftFromChat(chat)) {
                    if (chatInfo == null || !chatInfo.participants_hidden || ChatObject.hasAdminRights(chat)) {
                        canSearchMembers = true;
                        otherItem.addSubItem(search_members, R.drawable.msg_search, LocaleController.getString("SearchMembers", R.string.SearchMembers));
                    }
                }
                otherItem.addSubItem(leave_group, R.drawable.msg_leave, LocaleController.getString("DeleteAndExit", R.string.DeleteAndExit));
            }
            if (StrUtil.isNotBlank(chat.username) || ChatObject.canUserDoAdminAction(chat, ChatObject.ACTION_INVITE)) {
                otherItem.addSubItem(qr_code, R.drawable.msg_qrcode, LocaleController.getString("ShareQRCode", R.string.ShareQRCode));
            }
            if (topicId == 0) {
                otherItem.addSubItem(add_shortcut, R.drawable.msg_home, LocaleController.getString("AddShortcut", R.string.AddShortcut));
            }
        }

        // NekoX-TODO: Check Chnage

        if (imageUpdater != null) {
            otherItem.addSubItem(set_as_main, R.drawable.msg_openprofile, LocaleController.getString("SetAsMain", R.string.SetAsMain));
            otherItem.addSubItem(gallery_menu_save, R.drawable.msg_gallery, LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
            //otherItem.addSubItem(edit_avatar, R.drawable.photo_paint, LocaleController.getString("EditPhoto", R.string.EditPhoto));
            otherItem.addSubItem(delete_avatar, R.drawable.msg_delete, LocaleController.getString("Delete", R.string.Delete));
        }
        if (getMessagesController().isChatNoForwardsWithOverride(currentChat)) {
            otherItem.hideSubItem(gallery_menu_save);
        }

        if (selfUser) {
            otherItem.addSubItem(logout, R.drawable.msg_leave, LocaleController.getString("LogOut", R.string.LogOut));
        }
        if (!isPulledDown) {
            otherItem.hideSubItem(gallery_menu_save);
            otherItem.hideSubItem(set_as_main);
            otherItem.showSubItem(add_photo);
            otherItem.hideSubItem(edit_avatar);
            otherItem.hideSubItem(delete_avatar);
        }
        if (!mediaHeaderVisible) {
            if (callItemVisible) {
                if (callItem.getVisibility() != View.VISIBLE) {
                    callItem.setVisibility(View.VISIBLE);
                    if (animated) {
                        callItem.setAlpha(0);
                        callItem.animate().alpha(1f).setDuration(150).start();
                    }
                }
            } else {
                if (callItem.getVisibility() != View.GONE) {
                    callItem.setVisibility(View.GONE);
                }
            }
            if (videoCallItemVisible) {
                if (videoCallItem.getVisibility() != View.VISIBLE) {
                    videoCallItem.setVisibility(View.VISIBLE);
                    if (animated) {
                        videoCallItem.setAlpha(0);
                        videoCallItem.animate().alpha(1f).setDuration(150).start();
                    }
                }
            } else {
                if (videoCallItem.getVisibility() != View.GONE) {
                    videoCallItem.setVisibility(View.GONE);
                }
            }
            if (editItemVisible) {
                if (editItem.getVisibility() != View.VISIBLE) {
                    editItem.setVisibility(View.VISIBLE);
                    if (animated) {
                        editItem.setAlpha(0);
                        editItem.animate().alpha(1f).setDuration(150).start();
                    }
                }
            } else {
                if (editItem.getVisibility() != View.GONE) {
                    editItem.setVisibility(View.GONE);
                }
            }
        }
        if (avatarsViewPagerIndicatorView != null) {
            if (avatarsViewPagerIndicatorView.isIndicatorFullyVisible()) {
                if (editItemVisible) {
                    editItem.setVisibility(View.GONE);
                    editItem.animate().cancel();
                    editItem.setAlpha(1f);
                }
                if (callItemVisible) {
                    callItem.setVisibility(View.GONE);
                    callItem.animate().cancel();
                    callItem.setAlpha(1f);
                }
                if (videoCallItemVisible) {
                    videoCallItem.setVisibility(View.GONE);
                    videoCallItem.animate().cancel();
                    videoCallItem.setAlpha(1f);
                }
            }
        }
        if (sharedMediaLayout != null) {
            sharedMediaLayout.getSearchItem().requestLayout();
        }
    }

    private void createAutoDeleteItem(Context context) {
        autoDeletePopupWrapper = new AutoDeletePopupWrapper(context, otherItem.getPopupLayout().getSwipeBack(), new AutoDeletePopupWrapper.Callback() {

            @Override
            public void dismiss() {
                otherItem.toggleSubMenu();
            }

            @Override
            public void setAutoDeleteHistory(int time, int action) {
                ProfileActivity.this.setAutoDeleteHistory(time, action);
            }

            @Override
            public void showGlobalAutoDeleteScreen() {
                presentFragment(new AutoDeleteMessagesActivity());
                dismiss();
            }
        }, false, 0, resourcesProvider);
        if (dialogId > 0 || userId > 0) {
            autoDeletePopupWrapper.allowExtenededHint();
        }
        int ttl = 0;
        if (userInfo != null || chatInfo != null) {
            ttl = userInfo != null ? userInfo.ttl_period : chatInfo.ttl_period;
        }
        autoDeleteItemDrawable = TimerDrawable.getTtlIcon(ttl);
        autoDeleteItem = otherItem.addSwipeBackItem(0, autoDeleteItemDrawable, LocaleController.getString("AutoDeletePopupTitle", R.string.AutoDeletePopupTitle), autoDeletePopupWrapper.windowLayout);
        otherItem.addColoredGap();
        updateAutoDeleteItem();
    }

    @Override
    public Theme.ResourcesProvider getResourceProvider() {
        return resourcesProvider;
    }

    public int getThemedColor(String key) {
        return Theme.getColor(key, resourcesProvider);
    }

    @Override
    public Drawable getThemedDrawable(String drawableKey) {
        Drawable drawable = resourcesProvider != null ? resourcesProvider.getDrawable(drawableKey) : null;
        return drawable != null ? drawable : super.getThemedDrawable(drawableKey);
    }

    private void createAutoTranslateItem(long dialogId) {
        createAutoTranslateItem(dialogId, 0, true);
    }

    private void createAutoTranslateItem(long dialogId, int topicId, boolean gap) {
        var autoTranslatePopupWrapper = new AutoTranslatePopupWrapper(ProfileActivity.this, otherItem.getPopupLayout().getSwipeBack(), dialogId, topicId, getResourceProvider());
        otherItem.addSwipeBackItem(R.drawable.msg_translate, null, LocaleController.getString("AutoTranslate", R.string.AutoTranslate), autoTranslatePopupWrapper.windowLayout);
        if (gap) otherItem.addColoredGap();
    }

    private void setAutoDeleteHistory(int time, int action) {
        long did = getDialogId();
        getMessagesController().setDialogHistoryTTL(did, time);
        if (userInfo != null || chatInfo != null) {
            undoView.showWithAction(did, action, getMessagesController().getUser(did), userInfo != null ? userInfo.ttl_period : chatInfo.ttl_period, null, null);
        }
    }

    @Override
    protected void onDialogDismiss(Dialog dialog) {
        if (listView != null) {
            listView.invalidateViews();
        }
    }

    @Override
    public boolean didSelectDialogs(DialogsActivity
                                         fragment, ArrayList<MessagesStorage.TopicKey> dids, CharSequence message, boolean param, TopicsFragment topicsFragment) {
        long did = dids.get(0).dialogId;
        Bundle args = new Bundle();
        args.putBoolean("scrollToTopOnResume", true);
        if (DialogObject.isEncryptedDialog(did)) {
            args.putInt("enc_id", DialogObject.getEncryptedChatId(did));
        } else if (DialogObject.isUserDialog(did)) {
            args.putLong("user_id", did);
        } else if (DialogObject.isChatDialog(did)) {
            args.putLong("chat_id", -did);
        }
        if (!getMessagesController().checkCanOpenChat(args, fragment)) {
            return false;
        }

        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().postNotificationName(NotificationCenter.closeChats);
        presentFragment(new ChatActivity(args), true);
        removeSelfFromStack();
        TLRPC.User user = getMessagesController().getUser(userId);
        getSendMessagesHelper().sendMessage(user, did, null, null, null, null, true, 0);
        if (!TextUtils.isEmpty(message)) {
            AccountInstance accountInstance = AccountInstance.getInstance(currentAccount);
            SendMessagesHelper.prepareSendingText(accountInstance, message.toString(), did, true, 0);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (imageUpdater != null) {
            imageUpdater.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
        }
        if (requestCode == 101 || requestCode == 102) {
            final TLRPC.User user = getMessagesController().getUser(userId);
            if (user == null) {
                return;
            }
            boolean allGranted = true;
            for (int a = 0; a < grantResults.length; a++) {
                if (grantResults[a] != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (grantResults.length > 0 && allGranted) {
                VoIPHelper.startCall(user, requestCode == 102, userInfo != null && userInfo.video_calls_available, getParentActivity(), userInfo, getAccountInstance());
            } else {
                VoIPHelper.permissionDenied(getParentActivity(), null, requestCode);
            }
        } else if (requestCode == 103) {
            if (currentChat == null) {
                return;
            }
            boolean allGranted = true;
            for (int a = 0; a < grantResults.length; a++) {
                if (grantResults[a] != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (grantResults.length > 0 && allGranted) {
                ChatObject.Call call = getMessagesController().getGroupCall(chatId, false);
                VoIPHelper.startCall(currentChat, null, null, call == null, getParentActivity(), ProfileActivity.this, getAccountInstance());
            } else {
                VoIPHelper.permissionDenied(getParentActivity(), null, requestCode);
            }
        }
    }

    @Override
    public void dismissCurrentDialog() {
        if (imageUpdater != null && imageUpdater.dismissCurrentDialog(visibleDialog)) {
            return;
        }
        super.dismissCurrentDialog();
    }

    @Override
    public boolean dismissDialogOnPause(Dialog dialog) {
        return (imageUpdater == null || imageUpdater.dismissDialogOnPause(dialog)) && super.dismissDialogOnPause(dialog);
    }

    private Animator searchExpandTransition(boolean enter) {
        if (enter) {
            AndroidUtilities.requestAdjustResize(getParentActivity(), classGuid);
            AndroidUtilities.setAdjustResizeToNothing(getParentActivity(), classGuid);
        }
        if (searchViewTransition != null) {
            searchViewTransition.removeAllListeners();
            searchViewTransition.cancel();
        }
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(searchTransitionProgress, enter ? 0f : 1f);
        float offset = extraHeight;
        searchListView.setTranslationY(offset);
        searchListView.setVisibility(View.VISIBLE);
        searchItem.setVisibility(View.VISIBLE);

        listView.setVisibility(View.VISIBLE);

        needLayout(true);

        avatarContainer.setVisibility(View.VISIBLE);
        nameTextView[1].setVisibility(View.VISIBLE);
        onlineTextView[1].setVisibility(View.VISIBLE);
        if (Math.min(1f, extraHeight / AndroidUtilities.dp(88f)) > 0.85 && !searchMode && NekoConfig.showIdAndDc.Bool())
            idTextView.setVisibility(View.VISIBLE);

        actionBar.onSearchFieldVisibilityChanged(searchTransitionProgress > 0.5f);
        int itemVisibility = searchTransitionProgress > 0.5f ? View.VISIBLE : View.GONE;
        if (otherItem != null) {
            otherItem.setVisibility(itemVisibility);
        }
//        if (qrItem != null) {

        //        }
        searchItem.setVisibility(itemVisibility);

        searchItem.getSearchContainer().setVisibility(searchTransitionProgress > 0.5f ? View.GONE : View.VISIBLE);
        searchListView.setEmptyView(emptyView);
        avatarContainer.setClickable(false);

        valueAnimator.addUpdateListener(animation -> {
            searchTransitionProgress = (float) valueAnimator.getAnimatedValue();
            float progressHalf = (searchTransitionProgress - 0.5f) / 0.5f;
            float progressHalfEnd = (0.5f - searchTransitionProgress) / 0.5f;
            if (progressHalf < 0) {
                progressHalf = 0f;
            }
            if (progressHalfEnd < 0) {
                progressHalfEnd = 0f;
            }

            searchTransitionOffset = (int) (-offset * (1f - searchTransitionProgress));
            searchListView.setTranslationY(offset * searchTransitionProgress);
            emptyView.setTranslationY(offset * searchTransitionProgress);
            listView.setTranslationY(-offset * (1f - searchTransitionProgress));

            listView.setScaleX(1f - 0.01f * (1f - searchTransitionProgress));
            listView.setScaleY(1f - 0.01f * (1f - searchTransitionProgress));
            listView.setAlpha(searchTransitionProgress);
            needLayout(true);

            listView.setAlpha(progressHalf);

            searchListView.setAlpha(1f - searchTransitionProgress);
            searchListView.setScaleX(1f + 0.05f * searchTransitionProgress);
            searchListView.setScaleY(1f + 0.05f * searchTransitionProgress);
            emptyView.setAlpha(1f - progressHalf);

            avatarContainer.setAlpha(progressHalf);
            nameTextView[1].setAlpha(progressHalf);
            onlineTextView[1].setAlpha(progressHalf);
            idTextView.setAlpha(progressHalf);

            searchItem.getSearchField().setAlpha(progressHalfEnd);
            if (enter && searchTransitionProgress < 0.7f) {
                searchItem.requestFocusOnSearchView();
            }

            searchItem.getSearchContainer().setVisibility(searchTransitionProgress < 0.5f ? View.VISIBLE : View.GONE);
            int visibility = searchTransitionProgress > 0.5f ? View.VISIBLE : View.GONE;
            if (otherItem != null) {
                otherItem.setVisibility(visibility);
                otherItem.setAlpha(progressHalf);
            }
//            if (qrItem != null) {

//            }
            searchItem.setVisibility(visibility);

            actionBar.onSearchFieldVisibilityChanged(searchTransitionProgress < 0.5f);

            if (otherItem != null) {
                otherItem.setAlpha(progressHalf);
            }
//            if (qrItem != null) {
//                qrItem.setAlpha(progressHalf);
//            }
            searchItem.setAlpha(progressHalf);
            topView.invalidate();
            fragmentView.invalidate();
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                updateSearchViewState(enter);
                avatarContainer.setClickable(true);
                if (enter) {
                    searchItem.requestFocusOnSearchView();
                }
                needLayout(true);
                searchViewTransition = null;
                fragmentView.invalidate();

                if (enter) {
                    invalidateScroll = true;
                    saveScrollPosition();
                    AndroidUtilities.requestAdjustResize(getParentActivity(), classGuid);
                    emptyView.setPreventMoving(false);
                }
            }
        });

        if (!enter) {
            invalidateScroll = true;
            saveScrollPosition();
            AndroidUtilities.requestAdjustNothing(getParentActivity(), classGuid);
            emptyView.setPreventMoving(true);
        }

        valueAnimator.setDuration(220);
        valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        searchViewTransition = valueAnimator;
        return valueAnimator;
    }

    private void updateSearchViewState(boolean enter) {
        int hide = enter ? View.GONE : View.VISIBLE;
        listView.setVisibility(hide);
        searchListView.setVisibility(enter ? View.VISIBLE : View.GONE);
        searchItem.getSearchContainer().setVisibility(enter ? View.VISIBLE : View.GONE);

        actionBar.onSearchFieldVisibilityChanged(enter);

        avatarContainer.setVisibility(hide);
        nameTextView[1].setVisibility(hide);
        onlineTextView[1].setVisibility(hide);
        idTextView.setVisibility(hide);

        if (otherItem != null) {
            otherItem.setAlpha(1f);
            otherItem.setVisibility(hide);
        }
//        if (qrItem != null) {

//        }
        searchItem.setVisibility(hide);

        avatarContainer.setAlpha(1f);
        nameTextView[1].setAlpha(1f);
        onlineTextView[1].setAlpha(1f);
        idTextView.setAlpha(1f);
        searchItem.setAlpha(1f);
        listView.setAlpha(1f);
        searchListView.setAlpha(1f);
        emptyView.setAlpha(1f);
        if (enter) {
            searchListView.setEmptyView(emptyView);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUploadProgressChanged(float progress) {
        if (avatarProgressView == null) {
            return;
        }
        avatarProgressView.setProgress(progress);
        avatarsViewPager.setUploadProgress(uploadingImageLocation, progress);
    }

    @Override
    public void didStartUpload(boolean isVideo) {
        if (avatarProgressView == null) {
            return;
        }
        avatarProgressView.setProgress(0.0f);
    }

    int avatarUploadingRequest;
    @Override
    public void didUploadPhoto(final TLRPC.InputFile photo, final TLRPC.InputFile video, double videoStartTimestamp, String videoPath, TLRPC.PhotoSize bigSize, final TLRPC.PhotoSize smallSize, boolean isVideo, TLRPC.VideoSize emojiMarkup) {
        AndroidUtilities.runOnUIThread(() -> {
            if (photo != null || video != null || emojiMarkup != null) {
                if (avatar == null) {
                    return;
                }
                TLRPC.TL_photos_uploadProfilePhoto req = new TLRPC.TL_photos_uploadProfilePhoto();
                if (photo != null) {
                    req.file = photo;
                    req.flags |= 1;
                }
                if (video != null) {
                    req.video = video;
                    req.flags |= 2;
                    req.video_start_ts = videoStartTimestamp;
                    req.flags |= 4;
                }
                if (emojiMarkup != null) {
                    req.video_emoji_markup = emojiMarkup;
                    req.flags |= 16;
                }
                avatarUploadingRequest = getConnectionsManager().sendRequest(req, (response, error) -> AndroidUtilities.runOnUIThread(() -> {
                    if (error == null) {
                        TLRPC.User user = getMessagesController().getUser(getUserConfig().getClientUserId());
                        if (user == null) {
                            user = getUserConfig().getCurrentUser();
                            if (user == null) {
                                return;
                            }
                            getMessagesController().putUser(user, false);
                        } else {
                            getUserConfig().setCurrentUser(user);
                        }

                        TLRPC.TL_photos_photo photos_photo = (TLRPC.TL_photos_photo) response;
                        ArrayList<TLRPC.PhotoSize> sizes = photos_photo.photo.sizes;
                        TLRPC.PhotoSize small = FileLoader.getClosestPhotoSizeWithSize(sizes, 150);
                        TLRPC.PhotoSize big = FileLoader.getClosestPhotoSizeWithSize(sizes, 800);
                        TLRPC.VideoSize videoSize = photos_photo.photo.video_sizes.isEmpty() ? null : FileLoader.getClosestVideoSizeWithSize(photos_photo.photo.video_sizes, 1000);
                        user.photo = new TLRPC.TL_userProfilePhoto();
                        user.photo.photo_id = photos_photo.photo.id;
                        if (small != null) {
                            user.photo.photo_small = small.location;
                        }
                        if (big != null) {
                            user.photo.photo_big = big.location;
                        }

                        if (small != null && avatar != null) {
                            File destFile = FileLoader.getInstance(currentAccount).getPathToAttach(small, true);
                            File src = FileLoader.getInstance(currentAccount).getPathToAttach(avatar, true);
                            src.renameTo(destFile);
                            String oldKey = avatar.volume_id + "_" + avatar.local_id + "@50_50";
                            String newKey = small.location.volume_id + "_" + small.location.local_id + "@50_50";
                            ImageLoader.getInstance().replaceImageInCache(oldKey, newKey, ImageLocation.getForUserOrChat(user, ImageLocation.TYPE_SMALL), false);
                        }

                        if (videoSize != null && videoPath != null) {
                            File destFile = FileLoader.getInstance(currentAccount).getPathToAttach(videoSize, "mp4", true);
                            File src = new File(videoPath);
                            src.renameTo(destFile);
                        } else if (big != null && avatarBig != null) {
                            File destFile = FileLoader.getInstance(currentAccount).getPathToAttach(big, true);
                            File src = FileLoader.getInstance(currentAccount).getPathToAttach(avatarBig, true);
                            src.renameTo(destFile);
                        }
                        getMessagesStorage().addDialogPhoto(user.id, ((TLRPC.TL_photos_photo) response).photo);
                        ArrayList<TLRPC.User> users = new ArrayList<>();
                        users.add(user);
                        getMessagesStorage().putUsersAndChats(users, null, false, true);
                        TLRPC.UserFull userFull = getMessagesController().getUserFull(userId);
                        userFull.profile_photo = photos_photo.photo;
                        getMessagesStorage().updateUserInfo(userFull, false);
                    }

                    allowPullingDown = !AndroidUtilities.isTablet() && !isInLandscapeMode && avatarImage.getImageReceiver().hasNotThumb() && !AndroidUtilities.isAccessibilityScreenReaderEnabled();
                    avatar = null;
                    avatarBig = null;
                    avatarsViewPager.scrolledByUser = true;
                    avatarsViewPager.removeUploadingImage(uploadingImageLocation);
                    avatarsViewPager.setCreateThumbFromParent(false);
                    updateProfileData(true);
                    showAvatarProgress(false, true);
                    getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, MessagesController.UPDATE_MASK_ALL);
                    getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged);
                    getUserConfig().saveConfig(true);

                }));
            } else {
                avatar = smallSize.location;
                avatarBig = bigSize.location;
                avatarImage.setImage(ImageLocation.getForLocal(avatar), "50_50", avatarDrawable, null);
                if (setAvatarRow != -1) {
                    updateRowsIds();
                    if (listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
                    }
                    needLayout(true);
                }
                avatarsViewPager.addUploadingImage(uploadingImageLocation = ImageLocation.getForLocal(avatarBig), ImageLocation.getForLocal(avatar));
                showAvatarProgress(true, false);
            }
            actionBar.createMenu().requestLayout();
        });
    }

    private void showAvatarProgress(boolean show, boolean animated) {
        if (avatarProgressView == null) {
            return;
        }
        if (avatarAnimation != null) {
            avatarAnimation.cancel();
            avatarAnimation = null;
        }

        avatarProgressView.setAlpha(0.0f);
        avatarProgressView.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (imageUpdater != null) {
            imageUpdater.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void saveSelfArgs(Bundle args) {
        if (imageUpdater != null && imageUpdater.currentPicturePath != null) {
            args.putString("path", imageUpdater.currentPicturePath);
        }
    }

    @Override
    public void restoreSelfArgs(Bundle args) {
        if (imageUpdater != null) {
            imageUpdater.currentPicturePath = args.getString("path");
        }
    }

    public static void sendLogs(Activity activity, boolean last) {
        if (activity == null) {
            return;
        }
        // always send last logs
        AlertDialog progressDialog = new AlertDialog(activity, AlertDialog.ALERT_TYPE_SPINNER);
        progressDialog.setCanCancel(false);
        progressDialog.show();
        Utilities.globalQueue.postRunnable(() -> {
            try {
                File dir = AndroidUtilities.getLogsDir();
                if (dir == null) {
                    AndroidUtilities.runOnUIThread(progressDialog::dismiss);
                    return;
                }

                File logcatFile = new File(dir, "NekoX-" + System.currentTimeMillis() + ".log");
                try {
                    RuntimeUtil.exec("logcat", "-df", logcatFile.getPath()).waitFor();
                    RuntimeUtil.exec("logcat", "-c").waitFor();
                } catch (Exception e) {
                    AlertUtil.showToast(e);
                }

                File zipFile = new File(dir, "logs.zip");
                if (zipFile.exists()) {
                    zipFile.delete();
                }

                ArrayList<File> files = new ArrayList<>();

                files.addAll(Arrays.asList(dir.listFiles()));

                File filesDir = ApplicationLoader.getFilesDirFixed();
                filesDir = new File(filesDir, "malformed_database/");
                if (filesDir.exists() && filesDir.isDirectory()) {
                    File[] malformedDatabaseFiles = filesDir.listFiles();
                    files.addAll(Arrays.asList(malformedDatabaseFiles));
                }

                boolean[] finished = new boolean[1];
                long currentDate = System.currentTimeMillis();

                BufferedInputStream origin = null;
                ZipOutputStream out = null;
                try {
                    FileOutputStream dest = new FileOutputStream(zipFile);
                    out = new ZipOutputStream(new BufferedOutputStream(dest));
                    byte[] data = new byte[1024 * 64];

                    for (int i = 0; i < files.size(); i++) {
                        File file = files.get(i);
                        if (!file.getName().contains("cache4") && (file.getName().contains("_mtproto")) && (currentDate - file.lastModified()) > 24 * 60 * 60 * 1000) {
                            continue;
                        }
                        if (!file.exists()) {
                            continue;
                        }
                        FileInputStream fi = new FileInputStream(file);
                        origin = new BufferedInputStream(fi, data.length);

                        ZipEntry entry = new ZipEntry(file.getName());
                        out.putNextEntry(entry);
                        int count;
                        while ((count = origin.read(data, 0, data.length)) != -1) {
                            out.write(data, 0, count);
                        }
                        origin.close();
                        origin = null;
                    }
                    finished[0] = true;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (origin != null) {
                        origin.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                }

                AndroidUtilities.runOnUIThread(() -> {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception ignore) {

                    }
                    if (finished[0]) {
                        ShareUtil.shareFile(activity, zipFile);
                    } else {
                        if (activity != null) {
                            Toast.makeText(activity, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private final static int VIEW_TYPE_HEADER = 1,
                VIEW_TYPE_TEXT_DETAIL = 2,
                VIEW_TYPE_ABOUT_LINK = 3,
                VIEW_TYPE_TEXT = 4,
                VIEW_TYPE_DIVIDER = 5,
                VIEW_TYPE_NOTIFICATIONS_CHECK = 6,
                VIEW_TYPE_SHADOW = 7,
                VIEW_TYPE_USER = 8,
                VIEW_TYPE_EMPTY = 11,
                VIEW_TYPE_BOTTOM_PADDING = 12,
                VIEW_TYPE_SHARED_MEDIA = 13,
                VIEW_TYPE_VERSION = 14,
                VIEW_TYPE_SUGGESTION = 15,
                VIEW_TYPE_ADDTOGROUP_INFO = 17,
                VIEW_TYPE_PREMIUM_TEXT_CELL = 18,
                VIEW_TYPE_TEXT_DETAIL_MULTILINE = 19,
                VIEW_TYPE_NOTIFICATIONS_CHECK_SIMPLE = 20;

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case VIEW_TYPE_HEADER: {
                    view = new HeaderCell(mContext, 23, resourcesProvider);
                    break;
                }
                case VIEW_TYPE_TEXT_DETAIL_MULTILINE:
                case VIEW_TYPE_TEXT_DETAIL:
                    final TextDetailCell textDetailCell = new TextDetailCell(mContext, resourcesProvider, viewType == VIEW_TYPE_TEXT_DETAIL_MULTILINE);
                    textDetailCell.setContentDescriptionValueFirst(true);
                    view = textDetailCell;
                    break;
                case VIEW_TYPE_ABOUT_LINK: {
                    view = aboutLinkCell = new AboutLinkCell(mContext, ProfileActivity.this, resourcesProvider) {
                        @Override
                        protected void didPressUrl(String url, Browser.Progress progress) {
                            openUrl(url, progress);
                        }

                        @Override
                        protected void didResizeEnd() {
                            layoutManager.mIgnoreTopPadding = false;
                        }

                        @Override
                        protected void didResizeStart() {
                            layoutManager.mIgnoreTopPadding = true;
                        }
                    };
                    break;
                }
                case VIEW_TYPE_TEXT: {
                    view = new TextCell(mContext, resourcesProvider);
                    break;
                }
                case VIEW_TYPE_DIVIDER: {
                    view = new DividerCell(mContext, resourcesProvider);
                    view.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(4), 0, 0);
                    break;
                }
                case VIEW_TYPE_NOTIFICATIONS_CHECK: {
                    view = new NotificationsCheckCell(mContext, 23, 70, false, resourcesProvider);
                    break;
                }
                case VIEW_TYPE_NOTIFICATIONS_CHECK_SIMPLE: {
                    view = new TextCheckCell(mContext, resourcesProvider);
                    break;
                }
                case VIEW_TYPE_SHADOW: {
                    view = new ShadowSectionCell(mContext, resourcesProvider);
                    break;
                }
                case VIEW_TYPE_USER: {
                    view = new UserCell(mContext, addMemberRow == -1 ? 9 : 6, 0, true, resourcesProvider);
                    break;
                }
                case VIEW_TYPE_EMPTY: {
                    view = new View(mContext) {
                        @Override
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32), MeasureSpec.EXACTLY));
                        }
                    };
                    break;
                }
                case VIEW_TYPE_BOTTOM_PADDING: {
                    view = new View(mContext) {

                        private int lastPaddingHeight = 0;
                        private int lastListViewHeight = 0;

                        @Override
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            if (lastListViewHeight != listView.getMeasuredHeight()) {
                                lastPaddingHeight = 0;
                            }
                            lastListViewHeight = listView.getMeasuredHeight();
                            int n = listView.getChildCount();
                            if (n == listAdapter.getItemCount()) {
                                int totalHeight = 0;
                                for (int i = 0; i < n; i++) {
                                    View view = listView.getChildAt(i);
                                    int p = listView.getChildAdapterPosition(view);
                                    if (p >= 0 && p != bottomPaddingRow) {
                                        totalHeight += listView.getChildAt(i).getMeasuredHeight();
                                    }
                                }
                                int paddingHeight = fragmentView.getMeasuredHeight() - ActionBar.getCurrentActionBarHeight() - AndroidUtilities.statusBarHeight - totalHeight;
                                if (paddingHeight > AndroidUtilities.dp(88)) {
                                    paddingHeight = 0;
                                }
                                if (paddingHeight <= 0) {
                                    paddingHeight = 0;
                                }
                                setMeasuredDimension(listView.getMeasuredWidth(), lastPaddingHeight = paddingHeight);
                            } else {
                                setMeasuredDimension(listView.getMeasuredWidth(), lastPaddingHeight);
                            }
                        }
                    };
                    view.setBackground(new ColorDrawable(Color.TRANSPARENT));
                    break;
                }
                case VIEW_TYPE_SHARED_MEDIA: {
                    if (sharedMediaLayout.getParent() != null) {
                        ((ViewGroup) sharedMediaLayout.getParent()).removeView(sharedMediaLayout);
                    }
                    view = sharedMediaLayout;
                    break;
                }
                case VIEW_TYPE_ADDTOGROUP_INFO: {
                    view = new TextInfoPrivacyCell(mContext, resourcesProvider);
                    break;
                }
                case VIEW_TYPE_VERSION:
                default: {
                    TextInfoPrivacyCell cell = new TextInfoPrivacyCell(mContext, 10, resourcesProvider);
                    cell.getTextView().setGravity(Gravity.CENTER_HORIZONTAL);
                    cell.getTextView().setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText3));
                    cell.getTextView().setMovementMethod(null);
                    cell.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));

                    cell.setText("Nagram v" + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ") " + Build.SUPPORTED_ABIS[0].toLowerCase(Locale.ROOT) + " " + BuildConfig.FLAVOR + " " + BuildConfig.BUILD_TYPE);

                    cell.getTextView().setPadding(0, AndroidUtilities.dp(14), 0, AndroidUtilities.dp(14));
                    view = cell;
                    Drawable drawable = Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, getThemedColor(Theme.key_windowBackgroundGrayShadow));
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), drawable);
                    combinedDrawable.setFullsize(true);
                    view.setBackground(combinedDrawable);
                    break;
                }
                case VIEW_TYPE_SUGGESTION: {
                    view = new SettingsSuggestionCell(mContext, resourcesProvider) {
                        @Override
                        protected void onYesClick(int type) {
                            getNotificationCenter().removeObserver(ProfileActivity.this, NotificationCenter.newSuggestionsAvailable);
                            getMessagesController().removeSuggestion(0, type == SettingsSuggestionCell.TYPE_PHONE ? "VALIDATE_PHONE_NUMBER" : "VALIDATE_PASSWORD");
                            getNotificationCenter().addObserver(ProfileActivity.this, NotificationCenter.newSuggestionsAvailable);
                            int oldRow = type == SettingsSuggestionCell.TYPE_PHONE ? phoneSuggestionRow : passwordSuggestionRow;
                            updateListAnimated(false);
                        }

                        @Override
                        protected void onNoClick(int type) {
                            if (type == SettingsSuggestionCell.TYPE_PHONE) {
                                presentFragment(new ActionIntroActivity(ActionIntroActivity.ACTION_TYPE_CHANGE_PHONE_NUMBER));
                            } else {
                                presentFragment(new TwoStepVerificationSetupActivity(TwoStepVerificationSetupActivity.TYPE_VERIFY, null));
                            }
                        }
                    };
                    break;
                }
                case VIEW_TYPE_PREMIUM_TEXT_CELL:
                    view = new ProfilePremiumCell(mContext, resourcesProvider);
                    break;
            }
            if (viewType != VIEW_TYPE_SHARED_MEDIA) {
                view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.itemView == sharedMediaLayout) {
                sharedMediaLayoutAttached = true;
            }
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            if (holder.itemView == sharedMediaLayout) {
                sharedMediaLayoutAttached = false;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case VIEW_TYPE_HEADER:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == infoHeaderRow) {
                        if (ChatObject.isChannel(currentChat) && !currentChat.megagroup && channelInfoRow != -1) {
                            headerCell.setText(LocaleController.getString("ReportChatDescription", R.string.ReportChatDescription));
                        } else {
                            headerCell.setText(LocaleController.getString("Info", R.string.Info));
                        }
                    } else if (position == membersHeaderRow) {
                        headerCell.setText(LocaleController.getString("ChannelMembers", R.string.ChannelMembers));
                    } else if (position == settingsSectionRow2) {
                        headerCell.setText(LocaleController.getString("SETTINGS", R.string.SETTINGS));
                    } else if (position == numberSectionRow) {
                        headerCell.setText(LocaleController.getString("Account", R.string.Account));
                    } else if (position == helpHeaderRow) {
                        headerCell.setText(LocaleController.getString("SettingsHelp", R.string.SettingsHelp));
                    } else if (position == debugHeaderRow) {
                        headerCell.setText(LocaleController.getString("SettingsDebug", R.string.SettingsDebug));
                    }
                    break;
                case VIEW_TYPE_TEXT_DETAIL_MULTILINE:
                case VIEW_TYPE_TEXT_DETAIL:
                    TextDetailCell detailCell = (TextDetailCell) holder.itemView;
                    if (position == usernameRow) {
                        Drawable drawable = ContextCompat.getDrawable(detailCell.getContext(), R.drawable.msg_qr_mini);
                        drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_switch2TrackChecked), PorterDuff.Mode.MULTIPLY));
                        detailCell.setImage(drawable, LocaleController.getString("GetQRCode", R.string.GetQRCode));
                        detailCell.setImageClickListener(ProfileActivity.this::onTextDetailCellImageClicked);
                    } else {
                        detailCell.setImage(null);
                        detailCell.setImageClickListener(null);
                    }
                    if (position == phoneRow) {
                        String text;
                        TLRPC.User user = getMessagesController().getUser(userId);
                        String phoneNumber;
                        if (user != null && !TextUtils.isEmpty(vcardPhone)) {
                            text = PhoneFormat.getInstance().format("+" + vcardPhone);
                            phoneNumber = vcardPhone;
                        } else if (user != null && !TextUtils.isEmpty(user.phone)) {
                            text = PhoneFormat.getInstance().format("+" + user.phone);
                            phoneNumber = user.phone;
                        } else {
                            text = LocaleController.getString("PhoneHidden", R.string.PhoneHidden);
                            phoneNumber = null;
                        }
                        isFragmentPhoneNumber = phoneNumber != null && phoneNumber.matches("888\\d{8}");
                        detailCell.setTextAndValue(text, LocaleController.getString(isFragmentPhoneNumber ? R.string.AnonymousNumber : R.string.PhoneMobile), false);
                    } else if (position == usernameRow) {
                        String text, username = null;
                        CharSequence value;
                        boolean isUser = false;
                        ArrayList<TLRPC.TL_username> usernames = new ArrayList<>();
                        if (userId != 0) {
                            isUser = true;
                            final TLRPC.User user = getMessagesController().getUser(userId);
                            if (user != null) {
                                usernames.addAll(user.usernames);
                            }
                            if (user != null && !TextUtils.isEmpty(user.username)) {
                                username = user.username;
                            }
                            usernames = user == null ? new ArrayList<>() : new ArrayList<>(user.usernames);
                            value = LocaleController.getString("Username", R.string.Username);
                        } else if (currentChat != null) {
                            TLRPC.Chat chat = getMessagesController().getChat(chatId);
                            username = chat.username;
                            usernames.addAll(chat.usernames);
                            value = LocaleController.getString("InviteLink", R.string.InviteLink);
                        } else {
                            text = "";
                            value = "";
                            usernames = new ArrayList<>();
                        }
                        if (TextUtils.isEmpty(username) && usernames != null) {
                            for (int i = 0; i < usernames.size(); ++i) {
                                TLRPC.TL_username u = usernames.get(i);
                                if (u != null && u.active && !TextUtils.isEmpty(u.username)) {
                                    username = u.username;
                                    break;
                                }
                            }
                        }
                        if (isUser) {
                            if (username != null) {
                                text = "@" + username;
                            } else {
                                text = "—";
                            }
                        } else {
                            text = getLink(username, topicId);
                        }
                        detailCell.setTextAndValue(text, alsoUsernamesString(username, usernames, value), false);
                    } else if (position == restrictionReasonRow) {
                        ArrayList<TLRPC.TL_restrictionReason> reasons = new ArrayList<>();
                        if (userId != 0) {
                            final TLRPC.User user = getMessagesController().getUser(userId);
                            if (user != null) {
                                reasons = user.restriction_reason;
                            }
                        } else if (currentChat != null) {
                            TLRPC.Chat chat = getMessagesController().getChat(chatId);
                            reasons = chat.restriction_reason;
                        }
                        StringBuilder value = new StringBuilder();
                        for (TLRPC.TL_restrictionReason reason : reasons) {
                            value.append(reason.reason);
                            value.append("-");
                            value.append(reason.platform);
                            if (reasons.indexOf(reason) != reasons.size() - 1) {
                                value.append(", ");
                            }
                        }
                        detailCell.setTextAndValue(value.toString(), LocaleController.getString("RestrictionReason", R.string.RestrictionReason), false);
                    } else if (position == locationRow) {
                        if (chatInfo != null && chatInfo.location instanceof TLRPC.TL_channelLocation) {
                            TLRPC.TL_channelLocation location = (TLRPC.TL_channelLocation) chatInfo.location;
                            detailCell.setTextAndValue(location.address, LocaleController.getString("AttachLocation", R.string.AttachLocation), false);
                        }
                    } else if (position == numberRow) {
                        TLRPC.User user = UserConfig.getInstance(currentAccount).getCurrentUser();
                        String value;
                        if (user != null && user.phone != null && user.phone.length() != 0) {
                            value = PhoneFormat.getInstance().format("+" + user.phone);
                        } else {
                            value = LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
                        }
                        detailCell.setTextAndValue(value, LocaleController.getString("TapToChangePhone", R.string.TapToChangePhone), true);
                        detailCell.setContentDescriptionValueFirst(false);
                    } else if (position == setUsernameRow) {
                        TLRPC.User user = UserConfig.getInstance(currentAccount).getCurrentUser();
                        String text = "";
                        CharSequence value = LocaleController.getString("Username", R.string.Username);
                        String username = null;
                        if (user != null && user.usernames.size() > 0) {
                            for (int i = 0; i < user.usernames.size(); ++i) {
                                TLRPC.TL_username u = user.usernames.get(i);
                                if (u != null && u.active && !TextUtils.isEmpty(u.username)) {
                                    username = u.username;
                                    break;
                                }
                            }
                            if (username == null) {
                                username = user.username;
                            }
                            if (username == null || TextUtils.isEmpty(username)) {
                                text = LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty);
                            } else {
                                text = "@" + username;
                            }
                            value = alsoUsernamesString(username, user.usernames, value);
                        } else {
                            username = UserObject.getPublicUsername(user);
                            if (user != null && !TextUtils.isEmpty(username)) {
                                text = "@" + username;
                            } else {
                                text = LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty);
                            }
                        }
                        detailCell.setTextAndValue(text, value, true);
                        detailCell.setContentDescriptionValueFirst(true);
                    }
                    detailCell.setTag(position);
                    break;
                case VIEW_TYPE_ABOUT_LINK:
                    AboutLinkCell aboutLinkCell = (AboutLinkCell) holder.itemView;
                    if (position == userInfoRow) {
                        aboutLinkCell.setTextAndValue(MessageHelper.INSTANCE.zalgoFilter(userInfo.about), LocaleController.getString("UserBio", R.string.UserBio), true);
                    } else if (position == channelInfoRow) {
                        String text = chatInfo.about;
                        while (text.contains("\n\n\n")) {
                            text = text.replace("\n\n\n", "\n\n");
                        }
                        aboutLinkCell.setText(MessageHelper.INSTANCE.zalgoFilter(text), true);
                    }
                    if (position == bioRow) {
                        String value;
                        if (userInfo == null || !TextUtils.isEmpty(userInfo.about)) {
                            value = userInfo == null ? LocaleController.getString("Loading", R.string.Loading) : userInfo.about;
                            aboutLinkCell.setTextAndValue(MessageHelper.INSTANCE.zalgoFilter(value), LocaleController.getString("UserBio", R.string.UserBio), false);
                            currentBio = userInfo != null ? userInfo.about : null;
                        } else {
                            aboutLinkCell.setTextAndValue(LocaleController.getString("UserBioDetail", R.string.UserBioDetail), LocaleController.getString("UserBio", R.string.UserBio), false);
                            currentBio = null;
                        }
                        aboutLinkCell.setMoreButtonDisabled(true);
                    }
                    break;
                case VIEW_TYPE_PREMIUM_TEXT_CELL:
                case VIEW_TYPE_TEXT:
                    TextCell textCell = (TextCell) holder.itemView;
                    textCell.setColors(Theme.key_windowBackgroundWhiteGrayIcon, Theme.key_windowBackgroundWhiteBlackText);
                    textCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                    if (position == settingsTimerRow) {
                        TLRPC.EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(DialogObject.getEncryptedChatId(dialogId));
                        String value;
                        if (encryptedChat.ttl == 0) {
                            value = LocaleController.getString("ShortMessageLifetimeForever", R.string.ShortMessageLifetimeForever);
                        } else {
                            value = LocaleController.formatTTLString(encryptedChat.ttl);
                        }
                        textCell.setTextAndValue(LocaleController.getString("MessageLifetime", R.string.MessageLifetime), value, false,false);
                    } else if (position == unblockRow) {
                        textCell.setText(LocaleController.getString("Unblock", R.string.Unblock), false);
                        textCell.setColors(null, Theme.key_windowBackgroundWhiteRedText5);
                    } else if (position == settingsKeyRow) {
                        IdenticonDrawable identiconDrawable = new IdenticonDrawable();
                        TLRPC.EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(DialogObject.getEncryptedChatId(dialogId));
                        identiconDrawable.setEncryptedChat(encryptedChat);
                        textCell.setTextAndValueDrawable(LocaleController.getString("EncryptionKey", R.string.EncryptionKey), identiconDrawable, false);
                    } else if (position == joinRow) {
                        textCell.setColors(null, Theme.key_windowBackgroundWhiteBlueText2);
                        if (currentChat.megagroup) {
                            textCell.setText(LocaleController.getString("ProfileJoinGroup", R.string.ProfileJoinGroup), false);
                        } else {
                            textCell.setText(LocaleController.getString("ProfileJoinChannel", R.string.ProfileJoinChannel), false);
                        }
                    } else if (position == subscribersRow) {
                        if (chatInfo != null) {
                            if (ChatObject.isChannel(currentChat) && !currentChat.megagroup) {
                                textCell.setTextAndValueAndIcon(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers), String.format("%d", chatInfo.participants_count), R.drawable.msg_groups, position != membersSectionRow - 1);
                            } else {
                                textCell.setTextAndValueAndIcon(LocaleController.getString("ChannelMembers", R.string.ChannelMembers), String.format("%d", chatInfo.participants_count), R.drawable.msg_groups, position != membersSectionRow - 1);
                            }
                        } else {
                            if (ChatObject.isChannel(currentChat) && !currentChat.megagroup) {
                                textCell.setTextAndIcon(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers), R.drawable.msg_groups, position != membersSectionRow - 1);
                            } else {
                                textCell.setTextAndIcon(LocaleController.getString("ChannelMembers", R.string.ChannelMembers), R.drawable.msg_groups, position != membersSectionRow - 1);
                            }
                        }
                    } else if (position == subscribersRequestsRow) {
                        if (chatInfo != null) {
                            textCell.setTextAndValueAndIcon(LocaleController.getString("SubscribeRequests", R.string.SubscribeRequests), String.format("%d", chatInfo.requests_pending), R.drawable.msg_requests, position != membersSectionRow - 1);
                        }
                    } else if (position == administratorsRow) {
                        if (chatInfo != null) {
                            textCell.setTextAndValueAndIcon(LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators), String.format("%d", chatInfo.admins_count), R.drawable.msg_admins, position != membersSectionRow - 1);
                        } else {
                            textCell.setTextAndIcon(LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators), R.drawable.msg_admins, position != membersSectionRow - 1);
                        }
                    } else if (position == blockedUsersRow) {
                        if (chatInfo != null) {
                            textCell.setTextAndValueAndIcon(LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist), String.format("%d", Math.max(chatInfo.banned_count, chatInfo.kicked_count)), R.drawable.msg_user_remove, position != membersSectionRow - 1);
                        } else {
                            textCell.setTextAndIcon(LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist), R.drawable.msg_user_remove, position != membersSectionRow - 1);
                        }
                    } else if (position == addMemberRow) {
                        textCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                        textCell.setTextAndIcon(LocaleController.getString("AddMember", R.string.AddMember), R.drawable.msg_contact_add, membersSectionRow == -1);
                    } else if (position == sendMessageRow) {
                        textCell.setText(LocaleController.getString("SendMessageLocation", R.string.SendMessageLocation), true);
                    } else if (position == addToContactsRow) {
                        textCell.setText(LocaleController.getString(R.string.AddToContacts), false);
                        textCell.setColors(null, Theme.key_windowBackgroundWhiteBlackText);
                    } else if (position == reportReactionRow) {
                        TLRPC.Chat chat = getMessagesController().getChat(-reportReactionFromDialogId);
                        if (chat != null && ChatObject.canBlockUsers(chat)) {
                            textCell.setText(LocaleController.getString("ReportReactionAndBan", R.string.ReportReactionAndBan), false);
                        } else {
                            textCell.setText(LocaleController.getString("ReportReaction", R.string.ReportReaction), false);
                        }

                        textCell.setColors(null, Theme.key_windowBackgroundWhiteRedText5);
                        textCell.setColors(null, Theme.key_windowBackgroundWhiteRedText5);
                    } else if (position == reportRow) {
                        textCell.setText(LocaleController.getString("ReportUserLocation", R.string.ReportUserLocation), false);
                        textCell.setColors(null, Theme.key_windowBackgroundWhiteRedText5);
                        textCell.setColors(null, Theme.key_windowBackgroundWhiteRedText5);
                    } else if (position == languageRow) {
                        textCell.setTextAndIcon(LocaleController.getString("Language", R.string.Language), R.drawable.msg_language, false);
                    } else if (position == notificationRow) {
                        textCell.setTextAndIcon(LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, true);
                    } else if (position == privacyRow) {
                        textCell.setTextAndIcon(LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, true);
                    } else if (position == dataRow) {
                        textCell.setTextAndIcon(LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.baseline_data_usage_24, true);
                    } else if (position == chatRow) {
                        textCell.setTextAndIcon(LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg_msgbubble3, true);
                    } else if (position == stickersRow) {
                        textCell.setTextAndIcon(LocaleController.getString("StickersAndMasks", R.string.StickersAndMasks), R.drawable.msg_sticker, true);
                    } else if (position == nekoRow) {
                        textCell.setTextAndIcon(LocaleController.getString("NekoSettings", R.string.NekoSettings), R.drawable.menu_settings, true);
                    } else if (position == filtersRow) {
                        textCell.setTextAndIcon(LocaleController.getString("Filters", R.string.Filters), R.drawable.msg_folders, true);
                    } else if (position == liteModeRow) {
                        textCell.setTextAndIcon(LocaleController.getString(R.string.PowerUsage), R.drawable.msg2_battery, true);
                    } else if (position == questionRow) {
                        textCell.setTextAndIcon(LocaleController.getString("NekoXUpdatesChannel", R.string.NekoXUpdatesChannel), R.drawable.menu_channel_ny, true);
                    } else if (position == faqRow) {
                        textCell.setTextAndIcon(LocaleController.getString("NekoXFaq", R.string.NekoXFaq), R.drawable.msg_help, true);
                    } else if (position == policyRow) {
                        textCell.setTextAndIcon(LocaleController.getString("PrivacyPolicy", R.string.PrivacyPolicy), R.drawable.msg_policy, true);
                    } else if (position == sendLogsRow) {
                        textCell.setTextAndIcon(LocaleController.getString("DebugSendLogs", R.string.DebugSendLogs), R.drawable.menu_support2, true);
                    } else if (position == sendLastLogsRow) {
                        textCell.setTextAndIcon(LocaleController.getString("DebugSendLastLogs", R.string.DebugSendLastLogs), R.drawable.baseline_bug_report_24 ,true);
                    } else if (position == clearLogsRow) {
                        textCell.setTextAndIcon(LocaleController.getString("DebugClearLogs", R.string.DebugClearLogs), R.drawable.menu_clearcache, switchBackendRow != -1);
                    } else if (position == switchBackendRow) {
                        textCell.setText("Switch Backend", false);
                    } else if (position == devicesRow) {
                        textCell.setTextAndIcon(LocaleController.getString("Devices", R.string.Devices), R.drawable.msg2_devices, true);
                    } else if (position == setAvatarRow) {
                        cellCameraDrawable.setCustomEndFrame(86);
                        cellCameraDrawable.setCurrentFrame(85, false);
                        textCell.setTextAndIcon(LocaleController.getString("EditName", R.string.EditName), R.drawable.msg_edit, false);
                        textCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                        textCell.getImageView().setPadding(0, 0, 0, AndroidUtilities.dp(8));
                        textCell.setImageLeft(12);
                        setAvatarCell = textCell;
                    } else if (position == addToGroupButtonRow) {
                        textCell.setTextAndIcon(LocaleController.getString("AddToGroupOrChannel", R.string.AddToGroupOrChannel), R.drawable.msg_groups_create, false);
                        textCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                    } else if (position == premiumRow) {
                        textCell.setTextAndIcon(LocaleController.getString("TelegramPremium", R.string.TelegramPremium), new AnimatedEmojiDrawable.WrapSizeDrawable(PremiumGradient.getInstance().premiumStarMenuDrawable, AndroidUtilities.dp(24), AndroidUtilities.dp(24)), false);
                        textCell.setImageLeft(23);
                    }
                    break;
                case VIEW_TYPE_NOTIFICATIONS_CHECK:
                    NotificationsCheckCell checkCell = (NotificationsCheckCell) holder.itemView;
                    if (position == notificationsRow) {
                        SharedPreferences preferences = MessagesController.getNotificationsSettings(currentAccount);
                        long did;
                        if (dialogId != 0) {
                            did = dialogId;
                        } else if (userId != 0) {
                            did = userId;
                        } else {
                            did = -chatId;
                        }
                        String key = NotificationsController.getSharedPrefKey(did, topicId);
                        boolean enabled = false;
                        boolean custom = preferences.getBoolean("custom_" + key, false);
                        boolean hasOverride = preferences.contains("notify2_" + key);
                        int value = preferences.getInt("notify2_" + key, 0);
                        int delta = preferences.getInt("notifyuntil_" + key, 0);
                        String val;
                        if (value == 3 && delta != Integer.MAX_VALUE) {
                            delta -= getConnectionsManager().getCurrentTime();
                            if (delta <= 0) {
                                if (custom) {
                                    val = LocaleController.getString("NotificationsCustom", R.string.NotificationsCustom);
                                } else {
                                    val = LocaleController.getString("NotificationsOn", R.string.NotificationsOn);
                                }
                                enabled = true;
                            } else if (delta < 60 * 60) {
                                val = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Minutes", delta / 60));
                            } else if (delta < 60 * 60 * 24) {
                                val = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Hours", (int) Math.ceil(delta / 60.0f / 60)));
                            } else if (delta < 60 * 60 * 24 * 365) {
                                val = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Days", (int) Math.ceil(delta / 60.0f / 60 / 24)));
                            } else {
                                val = null;
                            }
                        } else {
                            if (value == 0) {
                                if (hasOverride) {
                                    enabled = true;
                                } else {
                                    enabled = getNotificationsController().isGlobalNotificationsEnabled(did);
                                }
                            } else if (value == 1) {
                                enabled = true;
                            }
                            if (enabled && custom) {
                                val = LocaleController.getString("NotificationsCustom", R.string.NotificationsCustom);
                            } else {
                                val = enabled ? LocaleController.getString("NotificationsOn", R.string.NotificationsOn) : LocaleController.getString("NotificationsOff", R.string.NotificationsOff);
                            }
                        }
                        if (val == null) {
                            val = LocaleController.getString("NotificationsOff", R.string.NotificationsOff);
                        }
                        if (notificationsExceptionTopics != null && !notificationsExceptionTopics.isEmpty()) {
                            val = String.format(Locale.US, LocaleController.getPluralString("NotificationTopicExceptionsDesctription", notificationsExceptionTopics.size()), val, notificationsExceptionTopics.size());
                        }
                        checkCell.setAnimationsEnabled(fragmentOpened);
                        checkCell.setTextAndValueAndCheck(LocaleController.getString("Notifications", R.string.Notifications), val, enabled, false);
                    }
                    break;
                case VIEW_TYPE_SHADOW:
                    View sectionCell = holder.itemView;
                    sectionCell.setTag(position);
                    Drawable drawable;
                    if (position == infoSectionRow && lastSectionRow == -1 && secretSettingsSectionRow == -1 && sharedMediaRow == -1 && membersSectionRow == -1 || position == secretSettingsSectionRow || position == lastSectionRow || position == membersSectionRow && lastSectionRow == -1 && sharedMediaRow == -1) {
                        sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, getThemedColor(Theme.key_windowBackgroundGrayShadow)));
                    } else {
                        sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(mContext, R.drawable.greydivider, getThemedColor(Theme.key_windowBackgroundGrayShadow)));
                    }
                    break;
                case VIEW_TYPE_USER:
                    UserCell userCell = (UserCell) holder.itemView;
                    TLRPC.ChatParticipant part;
                    try {
                        if (!visibleSortedUsers.isEmpty()) {
                            part = visibleChatParticipants.get(visibleSortedUsers.get(position - membersStartRow));
                        } else {
                            part = visibleChatParticipants.get(position - membersStartRow);
                        }
                    } catch (Exception e) {
                        part = null;
                        FileLog.e(e);
                    }
                    if (part != null) {
                        String role;
                        if (part instanceof TLRPC.TL_chatChannelParticipant) {
                            TLRPC.ChannelParticipant channelParticipant = ((TLRPC.TL_chatChannelParticipant) part).channelParticipant;
                            if (!TextUtils.isEmpty(channelParticipant.rank)) {
                                role = channelParticipant.rank;
                            } else {
                                if (channelParticipant instanceof TLRPC.TL_channelParticipantCreator) {
                                    role = LocaleController.getString("ChannelCreator", R.string.ChannelCreator);
                                } else if (channelParticipant instanceof TLRPC.TL_channelParticipantAdmin) {
                                    role = LocaleController.getString("ChannelAdmin", R.string.ChannelAdmin);
                                } else {
                                    role = null;
                                }
                            }
                        } else {
                            if (part instanceof TLRPC.TL_chatParticipantCreator) {
                                role = LocaleController.getString("ChannelCreator", R.string.ChannelCreator);
                            } else if (part instanceof TLRPC.TL_chatParticipantAdmin) {
                                role = LocaleController.getString("ChannelAdmin", R.string.ChannelAdmin);
                            } else {
                                role = null;
                            }
                        }
                        userCell.setAdminRole(role);
                        userCell.setData(getMessagesController().getUser(part.user_id), null, null, 0, position != membersEndRow - 1);
                    }
                    break;
                case VIEW_TYPE_BOTTOM_PADDING:
                    holder.itemView.requestLayout();
                    break;
                case VIEW_TYPE_SUGGESTION:
                    SettingsSuggestionCell suggestionCell = (SettingsSuggestionCell) holder.itemView;
                    suggestionCell.setType(position == passwordSuggestionRow ? SettingsSuggestionCell.TYPE_PASSWORD : SettingsSuggestionCell.TYPE_PHONE);
                    break;
                case VIEW_TYPE_ADDTOGROUP_INFO:
                    TextInfoPrivacyCell addToGroupInfo = (TextInfoPrivacyCell) holder.itemView;
                    addToGroupInfo.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, getThemedColor(Theme.key_windowBackgroundGrayShadow)));
                    addToGroupInfo.setText(LocaleController.getString("BotAddToGroupOrChannelInfo", R.string.BotAddToGroupOrChannelInfo));
                    break;
                case VIEW_TYPE_NOTIFICATIONS_CHECK_SIMPLE:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    textCheckCell.setTextAndCheck(LocaleController.getString("Notifications", R.string.Notifications), !getMessagesController().isDialogMuted(getDialogId(), topicId), false);
                    break;
            }
        }

        private CharSequence alsoUsernamesString(String originalUsername, ArrayList<TLRPC.TL_username> alsoUsernames, CharSequence fallback) {
            if (alsoUsernames == null) {
                return fallback;
            }
            alsoUsernames = new ArrayList<>(alsoUsernames);
            for (int i = 0; i < alsoUsernames.size(); ++i) {
                if (
                        !alsoUsernames.get(i).active ||
                                originalUsername != null && originalUsername.equals(alsoUsernames.get(i).username)
                ) {
                    alsoUsernames.remove(i--);
                }
            }
            if (alsoUsernames.size() > 0) {
                SpannableStringBuilder usernames = new SpannableStringBuilder();
                for (int i = 0; i < alsoUsernames.size(); ++i) {
                    final String usernameRaw = alsoUsernames.get(i).username;
                    SpannableString username = new SpannableString("@" + usernameRaw);
                    username.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View view) {
                            String urlFinal = getMessagesController().linkPrefix + "/" + usernameRaw;
                            if (currentChat == null || !currentChat.noforwards) {
                                AndroidUtilities.addToClipboard(urlFinal);
                                undoView.showWithAction(0, UndoView.ACTION_USERNAME_COPIED, null);
                            }
                        }

                        @Override
                        public void updateDrawState(@NonNull TextPaint ds) {
                            ds.setUnderlineText(false);
                        }
                    }, 0, username.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    username.setSpan(new ForegroundColorSpan(getThemedColor(Theme.key_chat_messageLinkIn)), 0, username.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    usernames.append(username);
                    if (i < alsoUsernames.size() - 1) {
                        usernames.append(", ");
                    }
                }
                String string = LocaleController.getString("UsernameAlso", R.string.UsernameAlso);
                SpannableStringBuilder finalString = new SpannableStringBuilder(string);
                final String toFind = "%1$s";
                int index = string.indexOf(toFind);
                if (index >= 0) {
                    finalString.replace(index, index + toFind.length(), usernames);
                }
                return finalString;
            } else {
                return fallback;
            }
        }

        @Override
        public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
            if (holder.getAdapterPosition() == setAvatarRow) {
                setAvatarCell = null;
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (notificationRow != -1) {
                int position = holder.getAdapterPosition();
                return position == notificationRow || position == numberRow || position == privacyRow ||
                        position == languageRow || position == setUsernameRow || position == bioRow ||
                        position == versionRow || position == dataRow || position == chatRow ||
                        position == questionRow || position == devicesRow || position == filtersRow || position == stickersRow ||
                        position == faqRow || position == policyRow || position == sendLogsRow || position == sendLastLogsRow ||
                        position == clearLogsRow || position == switchBackendRow || position == setAvatarRow ||
                        position == addToGroupButtonRow || position == premiumRow || position == liteModeRow;
            }
            if (holder.itemView instanceof UserCell) {
                UserCell userCell = (UserCell) holder.itemView;
                Object object = userCell.getCurrentObject();
                if (object instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) object;
                    if (UserObject.isUserSelf(user)) {
                        return false;
                    }
                }
            }
            int type = holder.getItemViewType();
            return type != VIEW_TYPE_HEADER && type != VIEW_TYPE_DIVIDER && type != VIEW_TYPE_SHADOW &&
                    type != VIEW_TYPE_EMPTY && type != VIEW_TYPE_BOTTOM_PADDING && type != VIEW_TYPE_SHARED_MEDIA &&
                    type != 9 && type != 10; // These are legacy ones, left for compatibility
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == infoHeaderRow || position == membersHeaderRow || position == settingsSectionRow2 ||
                    position == numberSectionRow || position == helpHeaderRow || position == debugHeaderRow) {
                return VIEW_TYPE_HEADER;
            } else if (position == phoneRow || position == locationRow || position == numberRow || position == restrictionReasonRow) {
                return VIEW_TYPE_TEXT_DETAIL;
            } else if (position == usernameRow || position == setUsernameRow) {
                return VIEW_TYPE_TEXT_DETAIL_MULTILINE;
            } else if (position == userInfoRow || position == channelInfoRow || position == bioRow) {
                return VIEW_TYPE_ABOUT_LINK;
            } else if (position == settingsTimerRow || position == settingsKeyRow || position == reportRow || position == reportReactionRow ||
                    position == subscribersRow || position == subscribersRequestsRow || position == administratorsRow || position == blockedUsersRow ||
                    position == addMemberRow || position == joinRow || position == unblockRow ||
                    position == sendMessageRow || position == notificationRow || position == privacyRow ||
                    position == languageRow || position == dataRow || position == chatRow ||
                    position == questionRow || position == devicesRow || position == filtersRow || position == stickersRow ||
                    position == faqRow || position == policyRow || position == sendLogsRow || position == sendLastLogsRow ||
                    position == clearLogsRow || position == switchBackendRow || position == setAvatarRow || position == addToGroupButtonRow ||
                    position == addToContactsRow || position == liteModeRow) {
                return VIEW_TYPE_TEXT;
            } else if (position == notificationsDividerRow) {
                return VIEW_TYPE_DIVIDER;
            } else if (position == notificationsRow) {
                return VIEW_TYPE_NOTIFICATIONS_CHECK;
            } else if (position == notificationsSimpleRow) {
                return VIEW_TYPE_NOTIFICATIONS_CHECK_SIMPLE;
            }else if (position == infoSectionRow || position == lastSectionRow || position == membersSectionRow ||
                    position == secretSettingsSectionRow || position == settingsSectionRow || position == devicesSectionRow ||
                    position == helpSectionCell || position == setAvatarSectionRow || position == passwordSuggestionSectionRow ||
                    position == phoneSuggestionSectionRow || position == premiumSectionsRow || position == reportDividerRow) {
                return VIEW_TYPE_SHADOW;
            } else if (position >= membersStartRow && position < membersEndRow) {
                return VIEW_TYPE_USER;
            } else if (position == emptyRow) {
                return VIEW_TYPE_EMPTY;
            } else if (position == bottomPaddingRow) {
                return VIEW_TYPE_BOTTOM_PADDING;
            } else if (position == sharedMediaRow) {
                return VIEW_TYPE_SHARED_MEDIA;
            } else if (position == versionRow) {
                return VIEW_TYPE_VERSION;
            } else if (position == passwordSuggestionRow || position == phoneSuggestionRow) {
                return VIEW_TYPE_SUGGESTION;
            } else if (position == addToGroupInfoRow) {
                return VIEW_TYPE_ADDTOGROUP_INFO;
            } else if (position == premiumRow) {
                return VIEW_TYPE_PREMIUM_TEXT_CELL;
            }
            return 4;
        }
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {

        private class SearchResult {

            private String searchTitle;
            private Runnable openRunnable;
            private String rowName;
            private String[] path;
            private int iconResId;
            private int guid;
            private int num;

            public SearchResult(int g, String search, int icon, Runnable open) {
                this(g, search, null, null, null, icon, open);
            }

            public SearchResult(int g, String search, String pathArg1, int icon, Runnable open) {
                this(g, search, null, pathArg1, null, icon, open);
            }

            public SearchResult(int g, String search, String row, String pathArg1, int icon, Runnable open) {
                this(g, search, row, pathArg1, null, icon, open);
            }

            public SearchResult(int g, String search, String row, String pathArg1, String pathArg2, int icon, Runnable open) {
                guid = g;
                searchTitle = search;
                rowName = row;
                openRunnable = open;
                iconResId = icon;
                if (pathArg1 != null && pathArg2 != null) {
                    path = new String[]{pathArg1, pathArg2};
                } else if (pathArg1 != null) {
                    path = new String[]{pathArg1};
                }
            }

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof SearchResult)) {
                    return false;
                }
                SearchResult result = (SearchResult) obj;
                return guid == result.guid;
            }

            @Override
            public String toString() {
                SerializedData data = new SerializedData();
                data.writeInt32(num);
                data.writeInt32(1);
                data.writeInt32(guid);
                return Utilities.bytesToHex(data.toByteArray());
            }

            private void open() {
                openRunnable.run();
                AndroidUtilities.scrollToFragmentRow(parentLayout, rowName);
            }
        }

        private SearchResult[] searchArray = onCreateSearchArray();
        private ArrayList<MessagesController.FaqSearchResult> faqSearchArray = new ArrayList<>();

        private Context mContext;
        private ArrayList<CharSequence> resultNames = new ArrayList<>();
        private ArrayList<SearchResult> searchResults = new ArrayList<>();
        private ArrayList<MessagesController.FaqSearchResult> faqSearchResults = new ArrayList<>();
        private ArrayList<Object> recentSearches = new ArrayList<>();
        private boolean searchWas;
        private Runnable searchRunnable;
        private String lastSearchString;
        private TLRPC.WebPage faqWebPage;
        private boolean loadingFaqPage;

        public SearchAdapter(Context context) {
            mContext = context;

            updateSearchArray();
        }

        private void updateSearchArray() {
            HashMap<Integer, SearchResult> resultHashMap = new HashMap<>();
            for (int a = 0; a < searchArray.length; a++) {
                if (searchArray[a] == null) {
                    continue;
                }
                resultHashMap.put(searchArray[a].guid, searchArray[a]);
            }
            Set<String> set = MessagesController.getGlobalMainSettings().getStringSet("settingsSearchRecent2", null);
            if (set != null) {
                for (String value : set) {
                    try {
                        SerializedData data = new SerializedData(Utilities.hexToBytes(value));
                        int num = data.readInt32(false);
                        int type = data.readInt32(false);
                        if (type == 0) {
                            String title = data.readString(false);
                            int count = data.readInt32(false);
                            String[] path = null;
                            if (count > 0) {
                                path = new String[count];
                                for (int a = 0; a < count; a++) {
                                    path[a] = data.readString(false);
                                }
                            }
                            String url = data.readString(false);
                            MessagesController.FaqSearchResult result = new MessagesController.FaqSearchResult(title, path, url);
                            result.num = num;
                            recentSearches.add(result);
                        } else if (type == 1) {
                            SearchResult result = resultHashMap.get(data.readInt32(false));
                            if (result != null) {
                                result.num = num;
                                recentSearches.add(result);
                            }
                        }
                    } catch (Exception ignore) {

                    }
                }
            }
            Collections.sort(recentSearches, (o1, o2) -> {
                int n1 = getNum(o1);
                int n2 = getNum(o2);
                if (n1 < n2) {
                    return -1;
                } else if (n1 > n2) {
                    return 1;
                }
                return 0;
            });
        }

        private SearchResult[] onCreateSearchArray() {
            return new SearchResult[]{
                    new SearchResult(500, LocaleController.getString("EditName", R.string.EditName), 0, () -> presentFragment(new ChangeNameActivity(resourcesProvider))),
                    new SearchResult(501, LocaleController.getString("ChangePhoneNumber", R.string.ChangePhoneNumber), 0, () -> presentFragment(new ActionIntroActivity(ActionIntroActivity.ACTION_TYPE_CHANGE_PHONE_NUMBER))),
                    new SearchResult(502, LocaleController.getString("AddAnotherAccount", R.string.AddAnotherAccount), 0, () -> {
                        int freeAccount;
                        for (int account = 0; ; account++) {
                            if (!SharedConfig.activeAccounts.contains(account)) {
                                freeAccount = account;
                                break;
                            }
                        }
                        if (freeAccount >= 0) {
                            presentFragment(new LoginActivity(freeAccount));
                        }
                }),
                new SearchResult(503, LocaleController.getString("UserBio", R.string.UserBio), 0, () -> {
                    if (userInfo != null) {
                        presentFragment(new ChangeBioActivity());
                    }
                }),
                new SearchResult(504, LocaleController.getString(R.string.AddPhoto), 0, ProfileActivity.this::onWriteButtonClick),

                new SearchResult(1, LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, () -> presentFragment(new NotificationsSettingsActivity())),
                new SearchResult(2, LocaleController.getString("NotificationsPrivateChats", R.string.NotificationsPrivateChats), LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, () -> presentFragment(new NotificationsCustomSettingsActivity(NotificationsController.TYPE_PRIVATE, new ArrayList<>(), true))),
                new SearchResult(3, LocaleController.getString("NotificationsGroups", R.string.NotificationsGroups), LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, () -> presentFragment(new NotificationsCustomSettingsActivity(NotificationsController.TYPE_GROUP, new ArrayList<>(), true))),
                new SearchResult(4, LocaleController.getString("NotificationsChannels", R.string.NotificationsChannels), LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, () -> presentFragment(new NotificationsCustomSettingsActivity(NotificationsController.TYPE_CHANNEL, new ArrayList<>(), true))),
                new SearchResult(5, LocaleController.getString("VoipNotificationSettings", R.string.VoipNotificationSettings), "callsSectionRow", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, () -> presentFragment(new NotificationsSettingsActivity())),
                new SearchResult(6, LocaleController.getString("BadgeNumber", R.string.BadgeNumber), "badgeNumberSection", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, () -> presentFragment(new NotificationsSettingsActivity())),
                new SearchResult(7, LocaleController.getString("InAppNotifications", R.string.InAppNotifications), "inappSectionRow", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, () -> presentFragment(new NotificationsSettingsActivity())),
                new SearchResult(8, LocaleController.getString("ContactJoined", R.string.ContactJoined), "contactJoinedRow", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, () -> presentFragment(new NotificationsSettingsActivity())),
                new SearchResult(9, LocaleController.getString("PinnedMessages", R.string.PinnedMessages), "pinnedMessageRow", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, () -> presentFragment(new NotificationsSettingsActivity())),
                new SearchResult(10, LocaleController.getString("ResetAllNotifications", R.string.ResetAllNotifications), "resetNotificationsRow", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, () -> presentFragment(new NotificationsSettingsActivity())),
                new SearchResult(11, LocaleController.getString(R.string.NotificationsService), "notificationsServiceRow", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, () -> presentFragment(new NotificationsSettingsActivity())),
                new SearchResult(12, LocaleController.getString(R.string.NotificationsServiceConnection), "notificationsServiceConnectionRow", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, () -> presentFragment(new NotificationsSettingsActivity())),
                new SearchResult(13, LocaleController.getString(R.string.RepeatNotifications), "repeatRow", LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg_notifications, () -> presentFragment(new NotificationsSettingsActivity())),

                new SearchResult(100, LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, () -> presentFragment(new PrivacySettingsActivity())),
                new SearchResult(101, LocaleController.getString("BlockedUsers", R.string.BlockedUsers), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, () -> presentFragment(new PrivacyUsersActivity())),
                new SearchResult(105, LocaleController.getString("PrivacyPhone", R.string.PrivacyPhone), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, () -> presentFragment(new PrivacyControlActivity(ContactsController.PRIVACY_RULES_TYPE_PHONE, true))),
                new SearchResult(102, LocaleController.getString("PrivacyLastSeen", R.string.PrivacyLastSeen), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, () -> presentFragment(new PrivacyControlActivity(ContactsController.PRIVACY_RULES_TYPE_LASTSEEN, true))),
                new SearchResult(103, LocaleController.getString("PrivacyProfilePhoto", R.string.PrivacyProfilePhoto), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, () -> presentFragment(new PrivacyControlActivity(ContactsController.PRIVACY_RULES_TYPE_PHOTO, true))),
                new SearchResult(104, LocaleController.getString("PrivacyForwards", R.string.PrivacyForwards), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, () -> presentFragment(new PrivacyControlActivity(ContactsController.PRIVACY_RULES_TYPE_FORWARDS, true))),
                new SearchResult(122, LocaleController.getString("PrivacyP2P", R.string.PrivacyP2P), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, () -> presentFragment(new PrivacyControlActivity(ContactsController.PRIVACY_RULES_TYPE_P2P, true))),
                new SearchResult(106, LocaleController.getString("Calls", R.string.Calls), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, () -> presentFragment(new PrivacyControlActivity(ContactsController.PRIVACY_RULES_TYPE_CALLS, true))),
                new SearchResult(107, LocaleController.getString("GroupsAndChannels", R.string.GroupsAndChannels), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, () -> presentFragment(new PrivacyControlActivity(ContactsController.PRIVACY_RULES_TYPE_INVITE, true))),
                new SearchResult(123, LocaleController.getString("PrivacyVoiceMessages", R.string.PrivacyVoiceMessages), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg_secret, () -> {
                    if (!getUserConfig().isPremium()) {
                        try {
                            if (!NekoConfig.disableVibration.Bool())
                            fragmentView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                        BulletinFactory.of(ProfileActivity.this).createRestrictVoiceMessagesPremiumBulletin().show();
                        return;
                    }
                    presentFragment(new PrivacyControlActivity(ContactsController.PRIVACY_RULES_TYPE_VOICE_MESSAGES, true));
                }),
                new SearchResult(108, LocaleController.getString("Passcode", R.string.Passcode), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg2_secret, () -> presentFragment(PasscodeActivity.determineOpenFragment())),
                new SearchResult(109, LocaleController.getString("TwoStepVerification", R.string.TwoStepVerification), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg2_secret, () -> presentFragment(new TwoStepVerificationActivity())),
                new SearchResult(110, LocaleController.getString("SessionsTitle", R.string.SessionsTitle), R.drawable.msg2_secret, () -> presentFragment(new SessionsActivity(0))),
                getMessagesController().autoarchiveAvailable ? new SearchResult(121, LocaleController.getString("ArchiveAndMute", R.string.ArchiveAndMute), "newChatsRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg2_secret, () -> presentFragment(new PrivacySettingsActivity())) : null,
                new SearchResult(112, LocaleController.getString("DeleteAccountIfAwayFor2", R.string.DeleteAccountIfAwayFor2), "deleteAccountRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg2_secret, () -> presentFragment(new PrivacySettingsActivity())),
                new SearchResult(113, LocaleController.getString("PrivacyPaymentsClear", R.string.PrivacyPaymentsClear), "paymentsClearRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg2_secret, () -> presentFragment(new PrivacySettingsActivity())),
                new SearchResult(114, LocaleController.getString("WebSessionsTitle", R.string.WebSessionsTitle), LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg2_secret, () -> presentFragment(new SessionsActivity(1))),
                new SearchResult(115, LocaleController.getString("SyncContactsDelete", R.string.SyncContactsDelete), "contactsDeleteRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg2_secret, () -> presentFragment(new PrivacySettingsActivity())),
                new SearchResult(116, LocaleController.getString("SyncContacts", R.string.SyncContacts), "contactsSyncRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg2_secret, () -> presentFragment(new PrivacySettingsActivity())),
                new SearchResult(117, LocaleController.getString("SuggestContacts", R.string.SuggestContacts), "contactsSuggestRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg2_secret, () -> presentFragment(new PrivacySettingsActivity())),
                new SearchResult(118, LocaleController.getString("MapPreviewProvider", R.string.MapPreviewProvider), "secretMapRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg2_secret, () -> presentFragment(new PrivacySettingsActivity())),
                new SearchResult(119, LocaleController.getString("SecretWebPage", R.string.SecretWebPage), "secretWebpageRow", LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg2_secret, () -> presentFragment(new PrivacySettingsActivity())),

                new SearchResult(120, LocaleController.getString(R.string.Devices), R.drawable.msg2_devices, () -> presentFragment(new SessionsActivity(0))),
                new SearchResult(121, LocaleController.getString(R.string.TerminateAllSessions), "terminateAllSessionsRow", LocaleController.getString(R.string.Devices), R.drawable.msg2_devices, () -> presentFragment(new SessionsActivity(0))),
                new SearchResult(122, LocaleController.getString(R.string.LinkDesktopDevice), LocaleController.getString(R.string.Devices), R.drawable.msg2_devices, () -> presentFragment(new SessionsActivity(0).setHighlightLinkDesktopDevice())),

                new SearchResult(200, LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new DataSettingsActivity())),
                new SearchResult(201, LocaleController.getString("DataUsage", R.string.DataUsage), "usageSectionRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new DataSettingsActivity())),
                new SearchResult(202, LocaleController.getString("StorageUsage", R.string.StorageUsage), LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new CacheControlActivity())),
                new SearchResult(203, LocaleController.getString("KeepMedia", R.string.KeepMedia), "keepMediaRow", LocaleController.getString("DataSettings", R.string.DataSettings), LocaleController.getString("StorageUsage", R.string.StorageUsage), R.drawable.msg2_data, () -> presentFragment(new CacheControlActivity())),
                new SearchResult(204, LocaleController.getString("ClearMediaCache", R.string.ClearMediaCache), "cacheRow", LocaleController.getString("DataSettings", R.string.DataSettings), LocaleController.getString("StorageUsage", R.string.StorageUsage), R.drawable.msg2_data, () -> presentFragment(new CacheControlActivity())),
                new SearchResult(205, LocaleController.getString("LocalDatabase", R.string.LocalDatabase), "databaseRow", LocaleController.getString("DataSettings", R.string.DataSettings), LocaleController.getString("StorageUsage", R.string.StorageUsage), R.drawable.msg2_data, () -> presentFragment(new CacheControlActivity())),
                new SearchResult(206, LocaleController.getString("NetworkUsage", R.string.NetworkUsage), LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new DataUsage2Activity())),
                new SearchResult(207, LocaleController.getString("AutomaticMediaDownload", R.string.AutomaticMediaDownload), "mediaDownloadSectionRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new DataSettingsActivity())),
                new SearchResult(208, LocaleController.getString("WhenUsingMobileData", R.string.WhenUsingMobileData), LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new DataAutoDownloadActivity(0))),
                new SearchResult(209, LocaleController.getString("WhenConnectedOnWiFi", R.string.WhenConnectedOnWiFi), LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new DataAutoDownloadActivity(1))),
                new SearchResult(210, LocaleController.getString("WhenRoaming", R.string.WhenRoaming), LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new DataAutoDownloadActivity(2))),
                new SearchResult(211, LocaleController.getString("ResetAutomaticMediaDownload", R.string.ResetAutomaticMediaDownload), "resetDownloadRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new DataSettingsActivity())),
                new SearchResult(212, LocaleController.getString("AutoplayMedia", R.string.AutoplayMedia), "autoplayHeaderRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new DataSettingsActivity())),
                new SearchResult(213, LocaleController.getString("AutoplayGIF", R.string.AutoplayGIF), "autoplayGifsRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new DataSettingsActivity())),
                new SearchResult(214, LocaleController.getString("AutoplayVideo", R.string.AutoplayVideo), "autoplayVideoRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new DataSettingsActivity())),
                new SearchResult(215, LocaleController.getString("Streaming", R.string.Streaming), "streamSectionRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new DataSettingsActivity())),
                new SearchResult(216, LocaleController.getString("EnableStreaming", R.string.EnableStreaming), "enableStreamRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new DataSettingsActivity())),
                new SearchResult(217, LocaleController.getString("Calls", R.string.Calls), "callsSectionRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new DataSettingsActivity())),
                new SearchResult(218, LocaleController.getString("VoipUseLessData", R.string.VoipUseLessData), "useLessDataForCallsRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new DataSettingsActivity())),
                new SearchResult(219, LocaleController.getString("VoipQuickReplies", R.string.VoipQuickReplies), "quickRepliesRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new DataSettingsActivity())),
                new SearchResult(220, LocaleController.getString("ProxySettings", R.string.ProxySettings), LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new ProxyListActivity())),
                new SearchResult(221, LocaleController.getString("UseProxyForCalls", R.string.UseProxyForCalls), "callsRow", LocaleController.getString("DataSettings", R.string.DataSettings), LocaleController.getString("ProxySettings", R.string.ProxySettings), R.drawable.msg2_data, () -> presentFragment(new ProxyListActivity())),
                new SearchResult(111, LocaleController.getString("PrivacyDeleteCloudDrafts", R.string.PrivacyDeleteCloudDrafts), "clearDraftsRow", LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new DataSettingsActivity())),
                new SearchResult(222, LocaleController.getString(R.string.SaveToGallery), "saveToGallerySectionRow", LocaleController.getString(R.string.DataSettings), R.drawable.msg2_data, () -> presentFragment(new DataSettingsActivity())),
                new SearchResult(223, LocaleController.getString(R.string.SaveToGalleryPrivate), "saveToGalleryPeerRow", LocaleController.getString(R.string.DataSettings), LocaleController.getString(R.string.SaveToGallery), R.drawable.msg2_data, () -> presentFragment(new DataSettingsActivity())),
                new SearchResult(224, LocaleController.getString(R.string.SaveToGalleryGroups), "saveToGalleryGroupsRow", LocaleController.getString(R.string.DataSettings), LocaleController.getString(R.string.SaveToGallery), R.drawable.msg2_data, () -> presentFragment(new DataSettingsActivity())),
                new SearchResult(225, LocaleController.getString(R.string.SaveToGalleryChannels), "saveToGalleryChannelsRow", LocaleController.getString(R.string.DataSettings), LocaleController.getString(R.string.SaveToGallery), R.drawable.msg2_data, () -> presentFragment(new DataSettingsActivity())),

                new SearchResult(300, LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg2_discussion, () -> presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC))),
                new SearchResult(301, LocaleController.getString("TextSizeHeader", R.string.TextSizeHeader), "textSizeHeaderRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg2_discussion, () -> presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC))),
                new SearchResult(302, LocaleController.getString(R.string.ChangeChatBackground), LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg2_discussion, () -> presentFragment(new WallpapersListActivity(WallpapersListActivity.TYPE_ALL))),
                new SearchResult(303, LocaleController.getString("SetColor", R.string.SetColor), null, LocaleController.getString("ChatSettings", R.string.ChatSettings), LocaleController.getString("ChatBackground", R.string.ChatBackground), R.drawable.msg2_discussion, () -> presentFragment(new WallpapersListActivity(WallpapersListActivity.TYPE_COLOR))),
                new SearchResult(304, LocaleController.getString("ResetChatBackgrounds", R.string.ResetChatBackgrounds), "resetRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), LocaleController.getString("ChatBackground", R.string.ChatBackground), R.drawable.msg2_discussion, () -> presentFragment(new WallpapersListActivity(WallpapersListActivity.TYPE_ALL))),
                new SearchResult(306, LocaleController.getString("ColorTheme", R.string.ColorTheme), "themeHeaderRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg2_discussion, () -> presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC))),
                new SearchResult(319, LocaleController.getString(R.string.BrowseThemes), null, LocaleController.getString(R.string.ChatSettings), R.drawable.msg2_discussion, () -> presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_THEMES_BROWSER))),
                new SearchResult(320, LocaleController.getString(R.string.CreateNewTheme), "createNewThemeRow", LocaleController.getString(R.string.ChatSettings), LocaleController.getString(R.string.BrowseThemes), R.drawable.msg2_discussion, () -> presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_THEMES_BROWSER))),
                new SearchResult(321, LocaleController.getString(R.string.BubbleRadius), "bubbleRadiusHeaderRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg2_discussion, () -> presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC))),
                new SearchResult(322, LocaleController.getString(R.string.ChatList), "chatListHeaderRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg2_discussion, () -> presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC))),
                new SearchResult(323, LocaleController.getString(R.string.ChatListSwipeGesture), "swipeGestureHeaderRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg2_discussion, () -> presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC))),
                new SearchResult(324, LocaleController.getString(R.string.AppIcon), "appIconHeaderRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg2_discussion, () -> presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC))),
                new SearchResult(305, LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg2_discussion, () -> presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_NIGHT))),
                new SearchResult(307, LocaleController.getString("ChromeCustomTabs", R.string.ChromeCustomTabs), "customTabsRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg2_discussion, () -> presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC))),
                new SearchResult(308, LocaleController.getString("DirectShare", R.string.DirectShare), "directShareRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg2_discussion, () -> presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC))),
                new SearchResult(309, LocaleController.getString("EnableAnimations", R.string.EnableAnimations), "enableAnimationsRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg2_discussion, () -> presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC))),
                new SearchResult(310, LocaleController.getString("RaiseToSpeak", R.string.RaiseToSpeak), "raiseToSpeakRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg2_discussion, () -> presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC))),
                new SearchResult(325, LocaleController.getString(R.string.MicrophoneForVoiceMessages), "bluetoothScoRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg2_discussion, () -> presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC))),
                new SearchResult(311, LocaleController.getString("SendByEnter", R.string.SendByEnter), "sendByEnterRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg2_discussion, () -> presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC))),
                SharedConfig.canBlurChat() ? new SearchResult(326, LocaleController.getString(R.string.BlurInChat), "chatBlurRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg2_discussion, () -> presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC))) : null,
                new SearchResult(318, LocaleController.getString("DistanceUnits", R.string.DistanceUnits), "distanceRow", LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.msg2_discussion, () -> presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC))),

                new SearchResult(600, LocaleController.getString(R.string.StickersName), R.drawable.msg2_sticker, () -> presentFragment(new StickersActivity(MediaDataController.TYPE_IMAGE, null))),
                new SearchResult(601, LocaleController.getString("SuggestStickers", R.string.SuggestStickers), "suggestRow", LocaleController.getString(R.string.StickersName), R.drawable.msg2_sticker, () -> presentFragment(new StickersActivity(MediaDataController.TYPE_IMAGE, null))),
                new SearchResult(602, LocaleController.getString("FeaturedStickers", R.string.FeaturedStickers), "featuredStickersHeaderRow", LocaleController.getString(R.string.StickersName), R.drawable.msg2_sticker, () -> presentFragment(new StickersActivity(MediaDataController.TYPE_IMAGE, null))),
                new SearchResult(603, LocaleController.getString("Masks", R.string.Masks), null, LocaleController.getString(R.string.StickersName), R.drawable.msg2_sticker, () -> presentFragment(new StickersActivity(MediaDataController.TYPE_MASK, null))),
                new SearchResult(604, LocaleController.getString("ArchivedStickers", R.string.ArchivedStickers), null, LocaleController.getString(R.string.StickersName), R.drawable.msg2_sticker, () -> presentFragment(new ArchivedStickersActivity(MediaDataController.TYPE_IMAGE))),
                new SearchResult(605, LocaleController.getString("ArchivedMasks", R.string.ArchivedMasks), null, LocaleController.getString(R.string.StickersName), R.drawable.msg2_sticker, () -> presentFragment(new ArchivedStickersActivity(MediaDataController.TYPE_MASK))),
                new SearchResult(606, LocaleController.getString(R.string.LargeEmoji), "largeEmojiRow", LocaleController.getString(R.string.StickersName), R.drawable.msg2_sticker, () -> presentFragment(new StickersActivity(MediaDataController.TYPE_IMAGE, null))),
                new SearchResult(607, LocaleController.getString(R.string.LoopAnimatedStickers), "loopRow", LocaleController.getString(R.string.StickersName), R.drawable.msg2_sticker, () -> presentFragment(new StickersActivity(MediaDataController.TYPE_IMAGE, null))),
                new SearchResult(608, LocaleController.getString(R.string.Emoji), null, LocaleController.getString(R.string.StickersName), R.drawable.input_smile, () -> presentFragment(new StickersActivity(MediaDataController.TYPE_EMOJIPACKS, null))),
                new SearchResult(609, LocaleController.getString(R.string.SuggestAnimatedEmoji), "suggestAnimatedEmojiRow", LocaleController.getString(R.string.StickersName), LocaleController.getString(R.string.Emoji), R.drawable.input_smile, () -> presentFragment(new StickersActivity(MediaDataController.TYPE_EMOJIPACKS, null))),
                new SearchResult(610, LocaleController.getString(R.string.FeaturedEmojiPacks), "featuredStickersHeaderRow", LocaleController.getString(R.string.StickersName), LocaleController.getString(R.string.Emoji), R.drawable.input_smile, () -> presentFragment(new StickersActivity(MediaDataController.TYPE_EMOJIPACKS, null))),
                new SearchResult(611, LocaleController.getString(R.string.DoubleTapSetting), null, LocaleController.getString(R.string.StickersName), R.drawable.msg2_sticker, () -> presentFragment(new ReactionsDoubleTapManageActivity())),

                new SearchResult(700, LocaleController.getString(R.string.Filters), null, R.drawable.msg2_folder, () -> presentFragment(new FiltersSetupActivity())),
                new SearchResult(701, LocaleController.getString(R.string.CreateNewFilter), "createFilterRow", LocaleController.getString(R.string.Filters), R.drawable.msg2_folder, () -> presentFragment(new FiltersSetupActivity())),

                    new SearchResult(600, LocaleController.getString(R.string.StickersName), R.drawable.msg_sticker, () -> presentFragment(new StickersActivity(MediaDataController.TYPE_IMAGE, null))),
                    new SearchResult(601, LocaleController.getString("SuggestStickers", R.string.SuggestStickers), "suggestRow", LocaleController.getString(R.string.StickersName), R.drawable.msg_sticker, () -> presentFragment(new StickersActivity(MediaDataController.TYPE_IMAGE, null))),
                    new SearchResult(602, LocaleController.getString("FeaturedStickers", R.string.FeaturedStickers), "featuredStickersHeaderRow", LocaleController.getString(R.string.StickersName), R.drawable.msg_sticker, () -> presentFragment(new StickersActivity(MediaDataController.TYPE_IMAGE, null))),
                    new SearchResult(603, LocaleController.getString("Masks", R.string.Masks), null, LocaleController.getString(R.string.StickersName), R.drawable.msg_sticker, () -> presentFragment(new StickersActivity(MediaDataController.TYPE_MASK, null))),
                    new SearchResult(604, LocaleController.getString("ArchivedStickers", R.string.ArchivedStickers), null, LocaleController.getString(R.string.StickersName), R.drawable.msg_sticker, () -> presentFragment(new ArchivedStickersActivity(MediaDataController.TYPE_IMAGE))),
                    new SearchResult(605, LocaleController.getString("ArchivedMasks", R.string.ArchivedMasks), null, LocaleController.getString(R.string.StickersName), R.drawable.msg_sticker, () -> presentFragment(new ArchivedStickersActivity(MediaDataController.TYPE_MASK))),
                    new SearchResult(606, LocaleController.getString(R.string.LargeEmoji), "largeEmojiRow", LocaleController.getString(R.string.StickersName), R.drawable.msg_sticker, () -> presentFragment(new StickersActivity(MediaDataController.TYPE_IMAGE, null))),
                    new SearchResult(607, LocaleController.getString(R.string.LoopAnimatedStickers), "loopRow", LocaleController.getString(R.string.StickersName), R.drawable.msg_sticker, () -> presentFragment(new StickersActivity(MediaDataController.TYPE_IMAGE, null))),
                    new SearchResult(608, LocaleController.getString(R.string.Emoji), null, LocaleController.getString(R.string.StickersName), R.drawable.input_smile, () -> presentFragment(new StickersActivity(MediaDataController.TYPE_EMOJIPACKS, null))),
                    new SearchResult(609, LocaleController.getString(R.string.SuggestAnimatedEmoji), "suggestAnimatedEmojiRow", LocaleController.getString(R.string.StickersName), LocaleController.getString(R.string.Emoji), R.drawable.input_smile, () -> presentFragment(new StickersActivity(MediaDataController.TYPE_EMOJIPACKS, null))),
                    new SearchResult(610, LocaleController.getString(R.string.FeaturedEmojiPacks), "featuredStickersHeaderRow", LocaleController.getString(R.string.StickersName), LocaleController.getString(R.string.Emoji), R.drawable.input_smile, () -> presentFragment(new StickersActivity(MediaDataController.TYPE_EMOJIPACKS, null))),
                    new SearchResult(611, LocaleController.getString(R.string.DoubleTapSetting), null, LocaleController.getString(R.string.StickersName), R.drawable.msg_sticker, () -> presentFragment(new ReactionsDoubleTapManageActivity())),

                new SearchResult(400, LocaleController.getString("Language", R.string.Language), R.drawable.msg2_language, () -> presentFragment(new LanguageSelectActivity())),
                new SearchResult(405, LocaleController.getString(R.string.ShowTranslateButton), LocaleController.getString(R.string.Language), R.drawable.msg2_language, () -> presentFragment(new LanguageSelectActivity())),
                MessagesController.getInstance(currentAccount).getTranslateController().isContextTranslateEnabled() ? new SearchResult(406, LocaleController.getString(R.string.DoNotTranslate), LocaleController.getString(R.string.Language), R.drawable.msg2_language, () -> presentFragment(new LanguageSelectActivity())) : null,

                new SearchResult(402, LocaleController.getString("AskAQuestion", R.string.AskAQuestion), LocaleController.getString("SettingsHelp", R.string.SettingsHelp), R.drawable.msg2_help, () -> showDialog(AlertsCreator.createSupportAlert(ProfileActivity.this, null))),
                new SearchResult(403, LocaleController.getString("TelegramFAQ", R.string.TelegramFAQ), LocaleController.getString("SettingsHelp", R.string.SettingsHelp), R.drawable.msg2_help, () -> Browser.openUrl(getParentActivity(), LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl))),
                new SearchResult(404, LocaleController.getString("PrivacyPolicy", R.string.PrivacyPolicy), LocaleController.getString("SettingsHelp", R.string.SettingsHelp), R.drawable.msg2_help, () -> Browser.openUrl(getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", R.string.PrivacyPolicyUrl))),
            };
        }

        private boolean isPremiumFeatureAvailable(int feature) {
            if (getMessagesController().premiumLocked && !getUserConfig().isPremium()) {
                return false;
            }

            if (feature == -1) {
                return true;
            }
            return getMessagesController().premiumFeaturesTypesToPosition.get(feature, -1) != -1;
        }

        private void loadFaqWebPage() {
            faqWebPage = getMessagesController().faqWebPage;
            if (faqWebPage != null) {
                faqSearchArray.addAll(getMessagesController().faqSearchArray);
            }
            if (faqWebPage != null || loadingFaqPage) {
                return;
            }
            loadingFaqPage = true;
            final TLRPC.TL_messages_getWebPage req2 = new TLRPC.TL_messages_getWebPage();
            req2.url = LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl);
            req2.hash = 0;
            getConnectionsManager().sendRequest(req2, (response2, error2) -> {
                if (response2 instanceof TLRPC.WebPage) {
                    ArrayList<MessagesController.FaqSearchResult> arrayList = new ArrayList<>();
                    TLRPC.WebPage page = (TLRPC.WebPage) response2;
                    if (page.cached_page != null) {
                        for (int a = 0, N = page.cached_page.blocks.size(); a < N; a++) {
                            TLRPC.PageBlock block = page.cached_page.blocks.get(a);
                            if (block instanceof TLRPC.TL_pageBlockList) {
                                String paragraph = null;
                                if (a != 0) {
                                    TLRPC.PageBlock prevBlock = page.cached_page.blocks.get(a - 1);
                                    if (prevBlock instanceof TLRPC.TL_pageBlockParagraph) {
                                        TLRPC.TL_pageBlockParagraph pageBlockParagraph = (TLRPC.TL_pageBlockParagraph) prevBlock;
                                        paragraph = ArticleViewer.getPlainText(pageBlockParagraph.text).toString();
                                    }
                                }
                                TLRPC.TL_pageBlockList list = (TLRPC.TL_pageBlockList) block;
                                for (int b = 0, N2 = list.items.size(); b < N2; b++) {
                                    TLRPC.PageListItem item = list.items.get(b);
                                    if (item instanceof TLRPC.TL_pageListItemText) {
                                        TLRPC.TL_pageListItemText itemText = (TLRPC.TL_pageListItemText) item;
                                        String url = ArticleViewer.getUrl(itemText.text);
                                        String text = ArticleViewer.getPlainText(itemText.text).toString();
                                        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(text)) {
                                            continue;
                                        }
                                        String[] path;
                                        if (paragraph != null) {
                                            path = new String[]{LocaleController.getString("SettingsSearchFaq", R.string.SettingsSearchFaq), paragraph};
                                        } else {
                                            path = new String[]{LocaleController.getString("SettingsSearchFaq", R.string.SettingsSearchFaq)};
                                        }
                                        arrayList.add(new MessagesController.FaqSearchResult(text, path, url));
                                    }
                                }
                            } else if (block instanceof TLRPC.TL_pageBlockAnchor) {
                                break;
                            }
                        }
                        faqWebPage = page;
                    }
                    AndroidUtilities.runOnUIThread(() -> {
                        faqSearchArray.addAll(arrayList);
                        getMessagesController().faqSearchArray = arrayList;
                        getMessagesController().faqWebPage = faqWebPage;
                        if (!searchWas) {
                            notifyDataSetChanged();
                        }
                    });
                }
                loadingFaqPage = false;
            });
        }

        @Override
        public int getItemCount() {
            if (searchWas) {
                return searchResults.size() + (faqSearchResults.isEmpty() ? 0 : 1 + faqSearchResults.size());
            }
            return (recentSearches.isEmpty() ? 0 : recentSearches.size() + 1) + (faqSearchArray.isEmpty() ? 0 : faqSearchArray.size() + 1);
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0: {
                    SettingsSearchCell searchCell = (SettingsSearchCell) holder.itemView;
                    if (searchWas) {
                        if (position < searchResults.size()) {
                            SearchResult result = searchResults.get(position);
                            SearchResult prevResult = position > 0 ? searchResults.get(position - 1) : null;
                            int icon;
                            if (prevResult != null && prevResult.iconResId == result.iconResId) {
                                icon = 0;
                            } else {
                                icon = result.iconResId;
                            }
                            searchCell.setTextAndValueAndIcon(resultNames.get(position), result.path, icon, position < searchResults.size() - 1);
                        } else {
                            position -= searchResults.size() + 1;
                            MessagesController.FaqSearchResult result = faqSearchResults.get(position);
                            searchCell.setTextAndValue(resultNames.get(position + searchResults.size()), result.path, true, position < searchResults.size() - 1);
                        }
                    } else {
                        if (!recentSearches.isEmpty()) {
                            position--;
                        }
                        if (position < recentSearches.size()) {
                            Object object = recentSearches.get(position);
                            if (object instanceof SearchResult) {
                                SearchResult result = (SearchResult) object;
                                searchCell.setTextAndValue(result.searchTitle, result.path, false, position < recentSearches.size() - 1);
                            } else if (object instanceof MessagesController.FaqSearchResult) {
                                MessagesController.FaqSearchResult result = (MessagesController.FaqSearchResult) object;
                                searchCell.setTextAndValue(result.title, result.path, true, position < recentSearches.size() - 1);
                            }
                        } else {
                            position -= recentSearches.size() + 1;
                            MessagesController.FaqSearchResult result = faqSearchArray.get(position);
                            searchCell.setTextAndValue(result.title, result.path, true, position < recentSearches.size() - 1);
                        }
                    }
                    break;
                }
                case 1: {
                    GraySectionCell sectionCell = (GraySectionCell) holder.itemView;
                    sectionCell.setText(LocaleController.getString("SettingsFaqSearchTitle", R.string.SettingsFaqSearchTitle));
                    break;
                }
                case 2: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    headerCell.setText(LocaleController.getString("SettingsRecent", R.string.SettingsRecent));
                    break;
                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new SettingsSearchCell(mContext);
                    break;
                case 1:
                    view = new GraySectionCell(mContext);
                    break;
                case 2:
                default:
                    view = new HeaderCell(mContext, 16);
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public int getItemViewType(int position) {
            if (searchWas) {
                if (position < searchResults.size()) {
                    return 0;
                } else if (position == searchResults.size()) {
                    return 1;
                }
            } else {
                if (position == 0) {
                    if (!recentSearches.isEmpty()) {
                        return 2;
                    } else {
                        return 1;
                    }
                } else if (!recentSearches.isEmpty() && position == recentSearches.size() + 1) {
                    return 1;
                }
            }
            return 0;
        }

        public void addRecent(Object object) {
            int index = recentSearches.indexOf(object);
            if (index >= 0) {
                recentSearches.remove(index);
            }
            recentSearches.add(0, object);
            if (!searchWas) {
                notifyDataSetChanged();
            }
            if (recentSearches.size() > 20) {
                recentSearches.remove(recentSearches.size() - 1);
            }
            LinkedHashSet<String> toSave = new LinkedHashSet<>();
            for (int a = 0, N = recentSearches.size(); a < N; a++) {
                Object o = recentSearches.get(a);
                if (o instanceof SearchResult) {
                    ((SearchResult) o).num = a;
                } else if (o instanceof MessagesController.FaqSearchResult) {
                    ((MessagesController.FaqSearchResult) o).num = a;
                }
                toSave.add(o.toString());
            }
            MessagesController.getGlobalMainSettings().edit().putStringSet("settingsSearchRecent2", toSave).commit();
        }

        public void clearRecent() {
            recentSearches.clear();
            MessagesController.getGlobalMainSettings().edit().remove("settingsSearchRecent2").commit();
            notifyDataSetChanged();
        }

        private int getNum(Object o) {
            if (o instanceof SearchResult) {
                return ((SearchResult) o).num;
            } else if (o instanceof MessagesController.FaqSearchResult) {
                return ((MessagesController.FaqSearchResult) o).num;
            }
            return 0;
        }

        public void search(String text) {
            lastSearchString = text;
            if (searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(searchRunnable);
                searchRunnable = null;
            }
            if (TextUtils.isEmpty(text)) {
                searchWas = false;
                searchResults.clear();
                faqSearchResults.clear();
                resultNames.clear();
                emptyView.stickerView.getImageReceiver().startAnimation();
                emptyView.title.setText(LocaleController.getString("SettingsNoRecent", R.string.SettingsNoRecent));
                notifyDataSetChanged();
                return;
            }
            Utilities.searchQueue.postRunnable(searchRunnable = () -> {
                ArrayList<SearchResult> results = new ArrayList<>();
                ArrayList<MessagesController.FaqSearchResult> faqResults = new ArrayList<>();
                ArrayList<CharSequence> names = new ArrayList<>();
                String[] searchArgs = text.split(" ");
                String[] translitArgs = new String[searchArgs.length];
                for (int a = 0; a < searchArgs.length; a++) {
                    translitArgs[a] = LocaleController.getInstance().getTranslitString(searchArgs[a]);
                    if (translitArgs[a].equals(searchArgs[a])) {
                        translitArgs[a] = null;
                    }
                }

                for (int a = 0; a < searchArray.length; a++) {
                    SearchResult result = searchArray[a];
                    if (result == null) {
                        continue;
                    }
                    String title = " " + result.searchTitle.toLowerCase();
                    SpannableStringBuilder stringBuilder = null;
                    for (int i = 0; i < searchArgs.length; i++) {
                        if (searchArgs[i].length() != 0) {
                            String searchString = searchArgs[i];
                            int index = title.indexOf(" " + searchString);
                            if (index < 0 && translitArgs[i] != null) {
                                searchString = translitArgs[i];
                                index = title.indexOf(" " + searchString);
                            }
                            if (index >= 0) {
                                if (stringBuilder == null) {
                                    stringBuilder = new SpannableStringBuilder(result.searchTitle);
                                }
                                stringBuilder.setSpan(new ForegroundColorSpan(getThemedColor(Theme.key_windowBackgroundWhiteBlueText4)), index, index + searchString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else {
                                break;
                            }
                        }
                        if (stringBuilder != null && i == searchArgs.length - 1) {
                            if (result.guid == 502) {
                                int freeAccount;
                                for (int account = 0; ; account++) {
                                    if (!SharedConfig.activeAccounts.contains(account)) {
                                        freeAccount = account;
                                        break;
                                    }
                                }
                                if (freeAccount < 0) {
                                    continue;
                                }
                            }
                            results.add(result);
                            names.add(stringBuilder);
                        }
                    }
                }
                if (faqWebPage != null) {
                    for (int a = 0, N = faqSearchArray.size(); a < N; a++) {
                        MessagesController.FaqSearchResult result = faqSearchArray.get(a);
                        String title = " " + result.title.toLowerCase();
                        SpannableStringBuilder stringBuilder = null;
                        for (int i = 0; i < searchArgs.length; i++) {
                            if (searchArgs[i].length() != 0) {
                                String searchString = searchArgs[i];
                                int index = title.indexOf(" " + searchString);
                                if (index < 0 && translitArgs[i] != null) {
                                    searchString = translitArgs[i];
                                    index = title.indexOf(" " + searchString);
                                }
                                if (index >= 0) {
                                    if (stringBuilder == null) {
                                        stringBuilder = new SpannableStringBuilder(result.title);
                                    }
                                    stringBuilder.setSpan(new ForegroundColorSpan(getThemedColor(Theme.key_windowBackgroundWhiteBlueText4)), index, index + searchString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                } else {
                                    break;
                                }
                            }
                            if (stringBuilder != null && i == searchArgs.length - 1) {
                                faqResults.add(result);
                                names.add(stringBuilder);
                            }
                        }
                    }
                }

                AndroidUtilities.runOnUIThread(() -> {
                    if (!text.equals(lastSearchString)) {
                        return;
                    }
                    if (!searchWas) {
                        emptyView.stickerView.getImageReceiver().startAnimation();
                        emptyView.title.setText(LocaleController.getString("SettingsNoResults", R.string.SettingsNoResults));
                    }
                    searchWas = true;
                    searchResults = results;
                    faqSearchResults = faqResults;
                    resultNames = names;
                    notifyDataSetChanged();
                    emptyView.stickerView.getImageReceiver().startAnimation();
                });
            }, 300);
        }

        public boolean isSearchWas() {
            return searchWas;
        }
    }

    private void openUrl(String url, Browser.Progress progress) {
        if (url.startsWith("@")) {
            getMessagesController().openByUserName(url.substring(1), ProfileActivity.this, 0, progress);
        } else if (url.startsWith("#")) {
            DialogsActivity fragment = new DialogsActivity(null);
            fragment.setSearchString(url);
            presentFragment(fragment);
        } else if (url.startsWith("/")) {
            if (parentLayout.getFragmentStack().size() > 1) {
                BaseFragment previousFragment = parentLayout.getFragmentStack().get(parentLayout.getFragmentStack().size() - 2);
                if (previousFragment instanceof ChatActivity) {
                    finishFragment();
                    ((ChatActivity) previousFragment).chatActivityEnterView.setCommand(null, url, false, false);
                }
            }
        }
    }

    private void dimBehindView(View view, boolean enable) {
        scrimView = view;
        dimBehindView(enable);
    }

    private void dimBehindView(View view, float value) {
        scrimView = view;
        dimBehindView(value);
    }

    private void dimBehindView(boolean enable) {
        dimBehindView(enable ? 0.2f : 0);
    }

    private AnimatorSet scrimAnimatorSet = null;

    private void dimBehindView(float value) {
        boolean enable = value > 0;
        fragmentView.invalidate();
        if (scrimAnimatorSet != null) {
            scrimAnimatorSet.cancel();
        }
        scrimAnimatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();
        ValueAnimator scrimPaintAlphaAnimator;
        if (enable) {
            animators.add(scrimPaintAlphaAnimator = ValueAnimator.ofFloat(0, value));
        } else {
            animators.add(scrimPaintAlphaAnimator = ValueAnimator.ofFloat(scrimPaint.getAlpha() / 255f, 0));
        }
        scrimPaintAlphaAnimator.addUpdateListener(a -> {
            scrimPaint.setAlpha((int) (255 * (float) a.getAnimatedValue()));
        });
        scrimAnimatorSet.playTogether(animators);
        scrimAnimatorSet.setDuration(enable ? 150 : 220);
        if (!enable) {
            scrimAnimatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    scrimView = null;
                    fragmentView.invalidate();
                }
            });
        }
        scrimAnimatorSet.start();
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        if (resourcesProvider != null) {
            return null;
        }
        ThemeDescription.ThemeDescriptionDelegate themeDelegate = () -> {
            if (listView != null) {
                int count = listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = listView.getChildAt(a);
                    if (child instanceof UserCell) {
                        ((UserCell) child).update(0);
                    }
                }
            }
            if (!isPulledDown) {
                if (onlineTextView[1] != null) {
                    final Object onlineTextViewTag = onlineTextView[1].getTag();
                    for (int i = 0; i < 2; i++) {
                        if (onlineTextViewTag instanceof String) {
                            onlineTextView[i + 1].setTextColor(getThemedColor((String) onlineTextViewTag));
                        } else {
                            onlineTextView[i + 1].setTextColor(getThemedColor(Theme.key_avatar_subtitleInProfileBlue));
                        }
                    }
                }
                if (lockIconDrawable != null) {
                    lockIconDrawable.setColorFilter(getThemedColor(Theme.key_chat_lockIcon), PorterDuff.Mode.SRC_IN);
                }
                if (scamDrawable != null) {
                    scamDrawable.setColor(getThemedColor(Theme.key_avatar_subtitleInProfileBlue));
                }
                if (nameTextView[1] != null) {
                    nameTextView[1].setTextColor(getThemedColor(Theme.key_profile_title));
                    idTextView.setTextColor(Theme.getColor(Theme.key_avatar_subtitleInProfileBlue));
                }
                if (actionBar != null) {
                    actionBar.setItemsColor(getThemedColor(Theme.key_actionBarDefaultIcon), false);
                    actionBar.setItemsBackgroundColor(getThemedColor(Theme.key_avatar_actionBarSelectorBlue), false);
                }
            }
            updateEmojiStatusDrawableColor();
        };
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        if (sharedMediaLayout != null) {
            arrayList.addAll(sharedMediaLayout.getThemeDescriptions());
        }

        arrayList.add(new ThemeDescription(listView, 0, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(searchListView, 0, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(listView, 0, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
        arrayList.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));
        arrayList.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_actionBarDefaultSubmenuItemIcon));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_actionBarSelectorBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_chat_lockIcon));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_subtitleInProfileBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundActionBarBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_profile_title));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_profile_status));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_subtitleInProfileBlue));

        if (mediaCounterTextView != null) {
            arrayList.add(new ThemeDescription(mediaCounterTextView.getTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, themeDelegate, Theme.key_player_actionBarSubtitle));
            arrayList.add(new ThemeDescription(mediaCounterTextView.getNextTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, themeDelegate, Theme.key_player_actionBarSubtitle));
        }

        arrayList.add(new ThemeDescription(topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue));
        arrayList.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        arrayList.add(new ThemeDescription(avatarImage, 0, null, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        arrayList.add(new ThemeDescription(avatarImage, 0, null, null, new Drawable[]{avatarDrawable}, null, Theme.key_avatar_backgroundInProfileBlue));

        arrayList.add(new ThemeDescription(writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_profile_actionIcon));
        arrayList.add(new ThemeDescription(writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_profile_actionBackground));
        arrayList.add(new ThemeDescription(writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_profile_actionPressedBackground));

        arrayList.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGreenText2));
        arrayList.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText5));
        arrayList.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueText2));
        arrayList.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueButton));
        arrayList.add(new ThemeDescription(listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText));
        arrayList.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon));
        arrayList.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueIcon));

        arrayList.add(new ThemeDescription(listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));

        arrayList.add(new ThemeDescription(listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));

        arrayList.add(new ThemeDescription(listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
        arrayList.add(new ThemeDescription(listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"detailTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{SettingsSuggestionCell.class}, new String[]{"detailTextView"}, null, null, null, Theme.key_windowBackgroundWhiteLinkText));
        arrayList.add(new ThemeDescription(listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"yesButton"}, null, null, null, Theme.key_featuredStickers_buttonText));
        arrayList.add(new ThemeDescription(listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{SettingsSuggestionCell.class}, new String[]{"yesButton"}, null, null, null, Theme.key_featuredStickers_addButton));
        arrayList.add(new ThemeDescription(listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{SettingsSuggestionCell.class}, new String[]{"yesButton"}, null, null, null, Theme.key_featuredStickers_addButtonPressed));
        arrayList.add(new ThemeDescription(listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"noButton"}, null, null, null, Theme.key_featuredStickers_buttonText));
        arrayList.add(new ThemeDescription(listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{SettingsSuggestionCell.class}, new String[]{"noButton"}, null, null, null, Theme.key_featuredStickers_addButton));
        arrayList.add(new ThemeDescription(listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{SettingsSuggestionCell.class}, new String[]{"noButton"}, null, null, null, Theme.key_featuredStickers_addButtonPressed));

        arrayList.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        arrayList.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        arrayList.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, null, null, null, Theme.key_profile_creatorIcon));
        arrayList.add(new ThemeDescription(listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon));
        arrayList.add(new ThemeDescription(listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, themeDelegate, Theme.key_windowBackgroundWhiteGrayText));
        arrayList.add(new ThemeDescription(listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDelegate, Theme.key_windowBackgroundWhiteBlueText));
        arrayList.add(new ThemeDescription(listView, 0, new Class[]{UserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundRed));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundOrange));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundViolet));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundGreen));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundCyan));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundPink));

        arrayList.add(new ThemeDescription(undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_undo_background));
        arrayList.add(new ThemeDescription(undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, null, null, null, Theme.key_undo_cancelColor));
        arrayList.add(new ThemeDescription(undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, null, null, null, Theme.key_undo_cancelColor));
        arrayList.add(new ThemeDescription(undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, null, null, null, Theme.key_undo_infoColor));
        arrayList.add(new ThemeDescription(undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, null, null, null, Theme.key_undo_infoColor));
        arrayList.add(new ThemeDescription(undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, null, null, null, Theme.key_undo_infoColor));
        arrayList.add(new ThemeDescription(undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, null, null, null, Theme.key_undo_infoColor));

        arrayList.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, null, null, Theme.key_windowBackgroundWhiteLinkText));
        arrayList.add(new ThemeDescription(listView, 0, new Class[]{AboutLinkCell.class}, Theme.linkSelectionPaint, null, null, Theme.key_windowBackgroundWhiteLinkSelection));

        arrayList.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));

        arrayList.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));

        arrayList.add(new ThemeDescription(searchListView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));

        arrayList.add(new ThemeDescription(searchListView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_graySectionText));
        arrayList.add(new ThemeDescription(searchListView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection));

        arrayList.add(new ThemeDescription(searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription(searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon));

        if (mediaHeaderVisible) {
            arrayList.add(new ThemeDescription(nameTextView[1], 0, null, null, new Drawable[]{verifiedCheckDrawable}, null, Theme.key_player_actionBarTitle));
            arrayList.add(new ThemeDescription(nameTextView[1], 0, null, null, new Drawable[]{verifiedDrawable}, null, Theme.key_windowBackgroundWhite));
        } else {
            arrayList.add(new ThemeDescription(nameTextView[1], 0, null, null, new Drawable[]{verifiedCheckDrawable}, null, Theme.key_profile_verifiedCheck));
            arrayList.add(new ThemeDescription(nameTextView[1], 0, null, null, new Drawable[]{verifiedDrawable}, null, Theme.key_profile_verifiedBackground));
        }

        return arrayList;
    }

    public void updateListAnimated(boolean updateOnlineCount) {
        if (listAdapter == null) {
            if (updateOnlineCount) {
                updateOnlineCount(false);
            }
            updateRowsIds();
            return;
        }

        DiffCallback diffCallback = new DiffCallback();
        diffCallback.oldRowCount = rowCount;
        diffCallback.fillPositions(diffCallback.oldPositionToItem);
        diffCallback.oldChatParticipant.clear();
        diffCallback.oldChatParticipantSorted.clear();
        diffCallback.oldChatParticipant.addAll(visibleChatParticipants);
        diffCallback.oldChatParticipantSorted.addAll(visibleSortedUsers);
        diffCallback.oldMembersStartRow = membersStartRow;
        diffCallback.oldMembersEndRow = membersEndRow;
        if (updateOnlineCount) {
            updateOnlineCount(false);
        }
        saveScrollPosition();
        updateRowsIds();
        diffCallback.fillPositions(diffCallback.newPositionToItem);
        try {
            DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(listAdapter);
        } catch (Exception e) {
            listAdapter.notifyDataSetChanged();
        }
        if (savedScrollPosition >= 0) {
            layoutManager.scrollToPositionWithOffset(savedScrollPosition, savedScrollOffset - listView.getPaddingTop());
        }
        AndroidUtilities.updateVisibleRows(listView);
    }

    int savedScrollPosition = -1;
    int savedScrollOffset;

    private void saveScrollPosition() {
        if (listView != null && layoutManager != null && listView.getChildCount() > 0) {
            View view = null;
            int position = -1;
            int top = Integer.MAX_VALUE;
            for (int i = 0; i < listView.getChildCount(); i++) {
                int childPosition = listView.getChildAdapterPosition(listView.getChildAt(i));
                View child = listView.getChildAt(i);
                if (childPosition != RecyclerListView.NO_POSITION && child.getTop() < top) {
                    view = child;
                    position = childPosition;
                    top = child.getTop();
                }
            }
            if (view != null) {
                savedScrollPosition = position;
                savedScrollOffset = view.getTop();
                if (savedScrollPosition == 0 && !allowPullingDown && savedScrollOffset > AndroidUtilities.dp(88)) {
                    savedScrollOffset = AndroidUtilities.dp(88);
                }

                layoutManager.scrollToPositionWithOffset(position, view.getTop() - listView.getPaddingTop());
            }
        }
    }

    public void scrollToSharedMedia() {
        layoutManager.scrollToPositionWithOffset(sharedMediaRow, -listView.getPaddingTop());
    }

    private void onTextDetailCellImageClicked(View view) {
        View parent = (View) view.getParent();
        if (parent.getTag() != null && ((int) parent.getTag()) == usernameRow) {
            Bundle args = new Bundle();
            args.putLong("chat_id", chatId);
            args.putLong("user_id", userId);
            presentFragment(new QrActivity(args));
        }
    }

    @Override
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();

        try {
            Drawable shadowDrawable = fragmentView.getContext().getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable,
                    Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56), getThemedColor(Theme.key_profile_actionBackground), getThemedColor(Theme.key_profile_actionPressedBackground)),
                    0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56), AndroidUtilities.dp(56));
            writeButton.setBackground(combinedDrawable);
        } catch (Exception e) {}
    }

    private boolean isQrNeedVisible() {
        return true;
    }

    private class DiffCallback extends DiffUtil.Callback {

        int oldRowCount;

        SparseIntArray oldPositionToItem = new SparseIntArray();
        SparseIntArray newPositionToItem = new SparseIntArray();
        ArrayList<TLRPC.ChatParticipant> oldChatParticipant = new ArrayList<>();
        ArrayList<Integer> oldChatParticipantSorted = new ArrayList<>();
        int oldMembersStartRow;
        int oldMembersEndRow;

        @Override
        public int getOldListSize() {
            return oldRowCount;
        }

        @Override
        public int getNewListSize() {
            return rowCount;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if (newItemPosition >= membersStartRow && newItemPosition < membersEndRow) {
                if (oldItemPosition >= oldMembersStartRow && oldItemPosition < oldMembersEndRow) {
                    TLRPC.ChatParticipant oldItem;
                    TLRPC.ChatParticipant newItem;
                    if (!oldChatParticipantSorted.isEmpty()) {
                        oldItem = oldChatParticipant.get(oldChatParticipantSorted.get(oldItemPosition - oldMembersStartRow));
                    } else {
                        oldItem = oldChatParticipant.get(oldItemPosition - oldMembersStartRow);
                    }

                    if (!sortedUsers.isEmpty()) {
                        newItem = visibleChatParticipants.get(visibleSortedUsers.get(newItemPosition - membersStartRow));
                    } else {
                        newItem = visibleChatParticipants.get(newItemPosition - membersStartRow);
                    }
                    return oldItem.user_id == newItem.user_id;
                }
            }
            int oldIndex = oldPositionToItem.get(oldItemPosition, -1);
            int newIndex = newPositionToItem.get(newItemPosition, -1);
            return oldIndex == newIndex && oldIndex >= 0;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return areItemsTheSame(oldItemPosition, newItemPosition);
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            int pointer = 0;
            put(++pointer, setAvatarRow, sparseIntArray);
            put(++pointer, setAvatarSectionRow, sparseIntArray);
            put(++pointer, numberSectionRow, sparseIntArray);
            put(++pointer, numberRow, sparseIntArray);
            put(++pointer, setUsernameRow, sparseIntArray);
            put(++pointer, bioRow, sparseIntArray);
            put(++pointer, phoneSuggestionRow, sparseIntArray);
            put(++pointer, phoneSuggestionSectionRow, sparseIntArray);
            put(++pointer, passwordSuggestionRow, sparseIntArray);
            put(++pointer, passwordSuggestionSectionRow, sparseIntArray);
            put(++pointer, settingsSectionRow, sparseIntArray);
            put(++pointer, settingsSectionRow2, sparseIntArray);
            put(++pointer, notificationRow, sparseIntArray);
            put(++pointer, languageRow, sparseIntArray);
            put(++pointer, premiumRow, sparseIntArray);
            put(++pointer, premiumSectionsRow, sparseIntArray);
            put(++pointer, privacyRow, sparseIntArray);
            put(++pointer, dataRow, sparseIntArray);
            put(++pointer, liteModeRow, sparseIntArray);
            put(++pointer, chatRow, sparseIntArray);
            put(++pointer, filtersRow, sparseIntArray);
            put(++pointer, stickersRow, sparseIntArray);
            put(++pointer, devicesRow, sparseIntArray);
            put(++pointer, devicesSectionRow, sparseIntArray);
            put(++pointer, helpHeaderRow, sparseIntArray);
            put(++pointer, questionRow, sparseIntArray);
            put(++pointer, faqRow, sparseIntArray);
            put(++pointer, policyRow, sparseIntArray);
            put(++pointer, helpSectionCell, sparseIntArray);
            put(++pointer, debugHeaderRow, sparseIntArray);
            put(++pointer, sendLogsRow, sparseIntArray);
            put(++pointer, sendLastLogsRow, sparseIntArray);
            put(++pointer, clearLogsRow, sparseIntArray);
            put(++pointer, switchBackendRow, sparseIntArray);
            put(++pointer, versionRow, sparseIntArray);
            put(++pointer, emptyRow, sparseIntArray);
            put(++pointer, bottomPaddingRow, sparseIntArray);
            put(++pointer, infoHeaderRow, sparseIntArray);
            put(++pointer, phoneRow, sparseIntArray);
            put(++pointer, locationRow, sparseIntArray);
            put(++pointer, userInfoRow, sparseIntArray);
            put(++pointer, channelInfoRow, sparseIntArray);
            put(++pointer, usernameRow, sparseIntArray);
            put(++pointer, restrictionReasonRow, sparseIntArray);
            put(++pointer, notificationsDividerRow, sparseIntArray);
            put(++pointer, reportDividerRow, sparseIntArray);
            put(++pointer, notificationsRow, sparseIntArray);
            put(++pointer, infoSectionRow, sparseIntArray);
            put(++pointer, sendMessageRow, sparseIntArray);
            put(++pointer, reportRow, sparseIntArray);
            put(++pointer, reportReactionRow, sparseIntArray);
            put(++pointer, addToContactsRow, sparseIntArray);
            put(++pointer, settingsTimerRow, sparseIntArray);
            put(++pointer, settingsKeyRow, sparseIntArray);
            put(++pointer, secretSettingsSectionRow, sparseIntArray);
            put(++pointer, membersHeaderRow, sparseIntArray);
            put(++pointer, addMemberRow, sparseIntArray);
            put(++pointer, subscribersRow, sparseIntArray);
            put(++pointer, subscribersRequestsRow, sparseIntArray);
            put(++pointer, administratorsRow, sparseIntArray);
            put(++pointer, blockedUsersRow, sparseIntArray);
            put(++pointer, membersSectionRow, sparseIntArray);
            put(++pointer, sharedMediaRow, sparseIntArray);
            put(++pointer, unblockRow, sparseIntArray);
            put(++pointer, addToGroupButtonRow, sparseIntArray);
            put(++pointer, addToGroupInfoRow, sparseIntArray);
            put(++pointer, joinRow, sparseIntArray);
            put(++pointer, lastSectionRow, sparseIntArray);
            put(++pointer, notificationsSimpleRow, sparseIntArray);
        }

        private void put(int id, int position, SparseIntArray sparseIntArray) {
            if (position >= 0) {
                sparseIntArray.put(position, id);
            }
        }
    }

    @Override
    public boolean isLightStatusBar() {
        int color;
        if (isPulledDown) {
            return false;
        }
        if (actionBar.isActionModeShowed()) {
            color = getThemedColor(Theme.key_actionBarActionModeDefault);
        } else if (mediaHeaderVisible) {
            color = getThemedColor(Theme.key_windowBackgroundWhite);
        } else {
            color = getThemedColor(Theme.key_actionBarDefault);
        }
        return ColorUtils.calculateLuminance(color) > 0.7f;
    }

    public String getLink(String username, int topicId) {
        String link = getMessagesController().linkPrefix + "/" + username;
        if (topicId != 0) {
            link += "/" + topicId;
        }
        return link;
    }

    float photoDescriptionProgress = -1;
    private void checkPhotoDescriptionAlpha() {
        float p = photoDescriptionProgress;
        if (playProfileAnimation == 1 && (!fragmentOpened || openAnimationInProgress)) {
            photoDescriptionProgress = 0;
        } else if (playProfileAnimation == 2 && (!fragmentOpened || openAnimationInProgress)) {
            photoDescriptionProgress = onlineTextView[1].getAlpha();
        } else {
            if (userId == UserConfig.getInstance(currentAccount).clientUserId) {
                photoDescriptionProgress = currentExpandAnimatorValue * (1f - customAvatarProgress);
            } else {
                photoDescriptionProgress = currentExpandAnimatorValue * customAvatarProgress;
            }
        }
//        if (p == photoDescriptionProgress) {
//            return;
//        }
        if (userId == UserConfig.getInstance(currentAccount).clientUserId) {
            if (hasFallbackPhoto) {
                customPhotoOffset = AndroidUtilities.dp(28) * photoDescriptionProgress;
                if (onlineTextView[2] != null) {
                    onlineTextView[2].setAlpha(currentExpandAnimatorValue);
                    onlineTextView[3].setAlpha(1f - currentExpandAnimatorValue);
                    //  onlineTextView[1].setAlpha(1f - expandProgress);
                    onlineTextView[1].setTranslationX(onlineX + customPhotoOffset);
                    avatarContainer2.invalidate();
                }
            } else {
                if (onlineTextView[2] != null) {
                    onlineTextView[2].setAlpha(0);
                    onlineTextView[3].setAlpha(0);
                }
            }

        } else {
            if (hasCustomPhoto) {
                if (onlineTextView[2] != null) {
                    onlineTextView[2].setAlpha(photoDescriptionProgress);
                }
            } else {
                if (onlineTextView[2] != null) {
                    onlineTextView[2].setAlpha(0);
                }
            }

        }

    }
}
