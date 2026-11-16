package tw.nekomimi.nekogram;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.MenuOverlayHost;
import org.telegram.ui.ActionBar.SimpleActionBarMenuOverlay;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProvider;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.TopicsFragment;
import org.telegram.utils.glass.source.GlassSourceCrossfade;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BackButtonMenuRecent {

    private static final int MAX_RECENT_DIALOGS = 25;
    private static final int MAX_DISK_RECENT_DIALOGS = 1000;

    private static final SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("nekorecentdialogs", Context.MODE_PRIVATE);
    private static final SparseArray<LinkedList<Long>> recentDialogs = new SparseArray<>();

    /**
     * The recent chats in the menu overlay of the screen, growing out of the glass of the back button as the menus of
     * the header's buttons. Null when the screen has no overlay or there are no recent chats to show.
     *
     * @param glassFactory the glass of the menu, as the header's menus; null without glass
     * @param glassSource the glass going over to the frosted one as the menu opens; null without a crossfade
     */
    @Nullable
    public static SimpleActionBarMenuOverlay showInOverlay(int currentAccount, BaseFragment fragment, View backButton,
                                                           @Nullable BlurredBackgroundDrawableViewFactory glassFactory,
                                                           @Nullable GlassSourceCrossfade glassSource,
                                                           @Nullable BlurredBackgroundColorProvider glassColors,
                                                           @Nullable Runnable onClosed) {
        if (fragment == null || backButton == null) {
            return null;
        }
        final Context context = fragment.getParentActivity();
        final ActionBar actionBar = fragment.getActionBar();
        if (context == null || actionBar == null) {
            return null;
        }
        final List<Long> dialogs = getDialogsToShow(fragment.getCurrentAccount());
        if (dialogs.isEmpty()) {
            return null;
        }
        final SimpleActionBarMenuOverlay overlay = fragment.getSimpleMenuOverlay();
        if (overlay == null) {
            return null;
        }
        final RecentChatsMenuHost host = new RecentChatsMenuHost(actionBar, backButton, glassFactory, glassSource, glassColors, fragment.getResourceProvider(), overlay);
        final ActionBarPopupWindow.ActionBarPopupWindowLayout layout = buildLayout(context, fragment, currentAccount, dialogs,
                () -> overlay.close(true));
        overlay.open(host, layout);
        overlay.setOnClosed(onClosed);
        return overlay;
    }

    /**
     * The back button as the host of the menu: the menu grows out of its glass, the overlay draws the button
     * inside the growing menu.
     */
    private static class RecentChatsMenuHost implements MenuOverlayHost {
        private final ActionBar actionBar;
        private final View backButton;
        @Nullable
        private final BlurredBackgroundDrawableViewFactory glassFactory;
        @Nullable
        private final GlassSourceCrossfade glassSource;
        @Nullable
        private final BlurredBackgroundColorProvider glassColors;
        @Nullable
        private final Theme.ResourcesProvider resourcesProvider;
        private final SimpleActionBarMenuOverlay overlay;

        private boolean shown;
        private float buttonAlpha = 1f;

        RecentChatsMenuHost(ActionBar actionBar, View backButton,
                            @Nullable BlurredBackgroundDrawableViewFactory glassFactory,
                            @Nullable GlassSourceCrossfade glassSource,
                            @Nullable BlurredBackgroundColorProvider glassColors,
                            @Nullable Theme.ResourcesProvider resourcesProvider,
                            SimpleActionBarMenuOverlay overlay) {
            this.actionBar = actionBar;
            this.backButton = backButton;
            this.glassFactory = glassFactory;
            this.glassSource = glassSource;
            this.glassColors = glassColors;
            this.resourcesProvider = resourcesProvider;
            this.overlay = overlay;
        }

        @Nullable
        @Override
        public BlurredBackgroundDrawable createMenuGlass(View owner) {
            if (glassFactory == null) {
                return null;
            }
            return glassFactory.create(owner).setColorProvider(glassColors).setRadius(AndroidUtilities.dp(23)).setPadding(AndroidUtilities.dp(6));
        }

        @Nullable
        @Override
        public BlurredBackgroundColorProvider getMenuGlassColorProvider() {
            return glassColors;
        }

        @Override
        public int getButtonsGlassBackgroundColor() {
            return glassColors != null ? glassColors.getBackgroundColor() : 0;
        }

        @Nullable
        @Override
        public BlurredBackgroundColorProvider getButtonsGlassColorProvider() {
            return glassColors;
        }

        @Override
        public boolean hasMenuGlassStroke() {
            return true;
        }

        @Override
        public void setMenuGlassFrostedProgress(float progress) {
            if (glassSource != null) {
                glassSource.setProgress(progress);
            }
        }

        /**
         * The glass of the back button as it is drawn by the bar: its pill has a padding for the shadow, so the visible
         * shape is {@code s} inside it, round by its half. The menu starts from exactly that, not a pixel bigger.
         */
        @Override
        public boolean getMenuItemGlassRect(ActionBarMenuItem item, RectF out) {
            final int s = AndroidUtilities.dp(46);
            final int p = AndroidUtilities.dp(6);
            final int top = actionBar.getHeight() - actionBar.getExtraHeight() - (ActionBar.getCurrentActionBarHeight() + s) / 2;
            out.set(p, top, p + s, top + s);
            return true;
        }

        @Override
        public View getMenuView() {
            return backButton;
        }

        @NonNull
        @Override
        public View getMenuHostView() {
            return actionBar;
        }

        @Override
        public void setMenuOverlay(@Nullable SimpleActionBarMenuOverlay overlay, @Nullable ActionBarMenuItem item) {
            final boolean shown = overlay != null;
            if (this.shown == shown) {
                return;
            }
            this.shown = shown;
            // the overlay draws the button inside the growing menu, the bar keeps its place meanwhile
            if (shown) {
                buttonAlpha = backButton.getAlpha();
                backButton.setAlpha(0f);
            } else {
                backButton.setAlpha(buttonAlpha);
            }
        }

        @Nullable
        @Override
        public SimpleActionBarMenuOverlay getOpenMenuOverlay() {
            return shown ? overlay : null;
        }

        @Nullable
        @Override
        public Theme.ResourcesProvider getMenuResourcesProvider() {
            return resourcesProvider;
        }

        /** As the menus of the menu items: the menu starts a little above the button. */
        @Override
        public int getMenuRise() {
            return actionBar.getMenuRise();
        }
    }

    public static ActionBarPopupWindow show(int currentAccount, BaseFragment fragment, View backButton) {
        if (fragment == null) {
            return null;
        }
        final Context context = fragment.getParentActivity();
        final View fragmentView = fragment.getFragmentView();
        if (context == null || fragmentView == null) {
            return null;
        }
        final List<Long> dialogs = getDialogsToShow(fragment.getCurrentAccount());
        if (dialogs.isEmpty()) {
            return null;
        }

        final AtomicReference<ActionBarPopupWindow> scrimPopupWindowRef = new AtomicReference<>();
        final ActionBarPopupWindow.ActionBarPopupWindowLayout layout = buildLayout(context, fragment, currentAccount, dialogs, () -> {
            final ActionBarPopupWindow window = scrimPopupWindowRef.getAndSet(null);
            if (window != null) {
                window.dismiss();
            }
        });

        Rect backgroundPaddings = new Rect();
        Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.popup_fixed_alert2).mutate();
        shadowDrawable.getPadding(backgroundPaddings);

        ActionBarPopupWindow scrimPopupWindow = new ActionBarPopupWindow(layout, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
        scrimPopupWindowRef.set(scrimPopupWindow);
        scrimPopupWindow.setPauseNotifications(true);
        scrimPopupWindow.setDismissAnimationDuration(220);
        scrimPopupWindow.setOutsideTouchable(true);
        scrimPopupWindow.setClippingEnabled(true);
        scrimPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
        scrimPopupWindow.setFocusable(true);
        layout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000), View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000), View.MeasureSpec.AT_MOST));
        scrimPopupWindow.setInputMethodMode(ActionBarPopupWindow.INPUT_METHOD_NOT_NEEDED);
        scrimPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        scrimPopupWindow.getContentView().setFocusableInTouchMode(true);

        int popupX = AndroidUtilities.dp(8) - backgroundPaddings.left;
        if (AndroidUtilities.isTablet()) {
            int[] location = new int[2];
            fragmentView.getLocationInWindow(location);
            popupX += location[0];
        }
        int popupY = backButton.getBottom() - backgroundPaddings.top - AndroidUtilities.dp(8);
        scrimPopupWindow.showAtLocation(fragmentView, Gravity.LEFT | Gravity.BOTTOM, popupX, popupY);
        scrimPopupWindow.dimBehind();
        return scrimPopupWindow;
    }

    /** The rows of the recent chats in a menu layout; {@code closeMenu} closes the menu they are shown in. */
    private static ActionBarPopupWindow.ActionBarPopupWindowLayout buildLayout(Context context, BaseFragment fragment, int currentAccount, List<Long> dialogs, Runnable closeMenu) {
        final Theme.ResourcesProvider resourcesProvider = fragment.getResourceProvider();
        ActionBarPopupWindow.ActionBarPopupWindowLayout layout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context) {
            final Path path = new Path();

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
        layout.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground, resourcesProvider));
        layout.setFitItems(true);

        FrameLayout headerView = new FrameLayout(context);

        TextView titleTextView = new TextView(context);
        titleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue, resourcesProvider));
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        titleTextView.setText(LocaleController.getString(R.string.RecentChats));
        titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        headerView.addView(titleTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 24, Gravity.LEFT));

        ImageView clearImageView = new ImageView(context);
        clearImageView.setScaleType(ImageView.ScaleType.CENTER);
        clearImageView.setColorFilter(Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon, resourcesProvider));
        clearImageView.setImageResource(R.drawable.msg_close);
        clearImageView.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider)));
        clearImageView.setOnClickListener(e2 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, resourcesProvider);
            builder.setTitle(LocaleController.getString(R.string.ClearRecentChats));
            builder.setMessage(LocaleController.getString(R.string.ClearRecentChatAlert));
            builder.setPositiveButton(LocaleController.getString(R.string.ClearButton).toUpperCase(), (dialogInterface, i) -> {
                closeMenu.run();
                clearRecentDialogs(currentAccount);
            });
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            fragment.showDialog(builder.create());
        });
        headerView.addView(clearImageView, LayoutHelper.createFrame(24, 24, Gravity.RIGHT | Gravity.CENTER_VERTICAL));

        headerView.setPadding(AndroidUtilities.dp(9), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        layout.addView(headerView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 4, 4, 4, 4));

        for (Long dialogId : dialogs) {
            final TLRPC.Chat chat;
            final TLRPC.User user;
            if (dialogId < 0) {
                chat = MessagesController.getInstance(currentAccount).getChat(-dialogId);
                user = null;
            } else {
                chat = null;
                user = MessagesController.getInstance(currentAccount).getUser(dialogId);
            }
            if (chat == null && user == null) {
                continue;
            }
            FrameLayout cell = new FrameLayout(context);
            cell.setMinimumWidth(AndroidUtilities.dp(200));

            BackupImageView imageView = new BackupImageView(context);
            imageView.setRoundRadius(chat != null && chat.forum ? AndroidUtilities.dp(8) : AndroidUtilities.dp(16));
            cell.addView(imageView, LayoutHelper.createFrameRelatively(32, 32, Gravity.START | Gravity.CENTER_VERTICAL, 13, 0, 0, 0));

            TextView titleView = new TextView(context);
            titleView.setLines(1);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            titleView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, resourcesProvider));
            titleView.setEllipsize(TextUtils.TruncateAt.END);
            cell.addView(titleView, LayoutHelper.createFrameRelatively(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.CENTER_VERTICAL, 59, 0, 12, 0));

            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setScaleSize(.8f);
            Drawable thumb = avatarDrawable;

            if (chat != null) {
                avatarDrawable.setInfo(chat);
                if (chat.photo != null && chat.photo.strippedBitmap != null) {
                    thumb = chat.photo.strippedBitmap;
                }
                imageView.setImage(ImageLocation.getForChat(chat, ImageLocation.TYPE_SMALL), "50_50", thumb, chat);
                titleView.setText(chat.title);
            } else {
                String name;
                if (user.photo != null && user.photo.strippedBitmap != null) {
                    thumb = user.photo.strippedBitmap;
                }
                if (UserObject.isReplyUser(user)) {
                    name = LocaleController.getString(R.string.RepliesTitle);
                    avatarDrawable.setAvatarType(AvatarDrawable.AVATAR_TYPE_REPLIES);
                    imageView.setImageDrawable(avatarDrawable);
                } else if (UserObject.isDeleted(user)) {
                    name = LocaleController.getString(R.string.HiddenName);
                    avatarDrawable.setInfo(user);
                    imageView.setImage(ImageLocation.getForUser(user, ImageLocation.TYPE_SMALL), "50_50", avatarDrawable, user);
                } else {
                    name = UserObject.getUserName(user);
                    avatarDrawable.setInfo(user);
                    imageView.setImage(ImageLocation.getForUser(user, ImageLocation.TYPE_SMALL), "50_50", thumb, user);
                }
                titleView.setText(name);
            }

            cell.setBackground(Theme.getSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider), false));
            cell.setOnClickListener(e2 -> {
                closeMenu.run();
                Bundle bundle = new Bundle();
                if (dialogId < 0) {
                    bundle.putLong("chat_id", -dialogId);
                    if (MessagesController.getInstance(currentAccount).isForum(dialogId)) {
                        fragment.presentFragment(new TopicsFragment(bundle));
                    } else {
                        fragment.presentFragment(new ChatActivity(bundle));
                    }
                } else {
                    bundle.putLong("user_id", dialogId);
                    fragment.presentFragment(new ChatActivity(bundle));
                }
            });
            cell.setOnLongClickListener(e2 -> {
                closeMenu.run();
                Bundle bundle = new Bundle();
                if (dialogId < 0) {
                    bundle.putLong("chat_id", -dialogId);
                } else {
                    bundle.putLong("user_id", dialogId);
                }
                fragment.presentFragment(new ProfileActivity(bundle));
                return true;
            });
            layout.addView(cell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48));
        }
        return layout;
    }

    private static List<Long> getDialogsToShow(int currentAccount) {
        List<Long> dialogs = getRecentDialogs(currentAccount);
        if (dialogs.size() > MAX_RECENT_DIALOGS) {
            dialogs = dialogs.subList(0, MAX_RECENT_DIALOGS);
        }
        return dialogs;
    }

    public static LinkedList<Long> getRecentDialogs(int currentAccount) {
        LinkedList<Long> recentDialog = recentDialogs.get(currentAccount);
        if (recentDialog == null) {
            recentDialog = new LinkedList<>();
            String list = preferences.getString("recents_" + currentAccount, null);
            if (!TextUtils.isEmpty(list)) {
                byte[] bytes = Base64.decode(list, Base64.NO_WRAP | Base64.NO_PADDING);
                SerializedData data = new SerializedData(bytes);
                int count = data.readInt32(false);
                for (int a = 0; a < count; a++) {
                    recentDialog.add(data.readInt64(false));
                }
                data.cleanup();
            }
            recentDialogs.put(currentAccount, recentDialog);
        }
        return recentDialog;
    }

    public static void addToRecentDialogs(int currentAccount, long dialogId) {
        LinkedList<Long> recentDialog = getRecentDialogs(currentAccount);
        for (int i = 0; i < recentDialog.size(); i++) {
            if (recentDialog.get(i) == dialogId) {
                recentDialog.remove(i);
                break;
            }
        }

        if (recentDialog.size() >= MAX_DISK_RECENT_DIALOGS) {
            recentDialog.removeLast();
        }
        recentDialog.addFirst(dialogId);
        LinkedList<Long> finalRecentDialog = new LinkedList<>(recentDialog);
        Utilities.globalQueue.postRunnable(() -> saveRecentDialogs(currentAccount, finalRecentDialog));
    }

    public static void deleteFromRecentDialogs(int currentAccount, long dialogId) {
        LinkedList<Long> recentDialog = getRecentDialogs(currentAccount);
        recentDialog.remove(dialogId);
        Utilities.globalQueue.postRunnable(() -> saveRecentDialogs(currentAccount, recentDialog));
    }

    public static void saveRecentDialogs(int currentAccount, LinkedList<Long> recentDialog) {
        SerializedData serializedData = new SerializedData();
        int count = recentDialog.size();
        serializedData.writeInt32(count);
        for (Long dialog : recentDialog) {
            serializedData.writeInt64(dialog);
        }
        preferences.edit().putString("recents_" + currentAccount, Base64.encodeToString(serializedData.toByteArray(), Base64.NO_WRAP | Base64.NO_PADDING)).apply();
        serializedData.cleanup();
    }

    public static void clearRecentDialogs(int currentAccount) {
        getRecentDialogs(currentAccount).clear();
        preferences.edit().putString("recents_" + currentAccount, "").apply();
    }
}
