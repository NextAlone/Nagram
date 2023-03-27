/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package org.telegram.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.v2ray.ang.V2RayConfig;

import org.json.JSONArray;
import org.json.JSONObject;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.ProxyRotationController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SlideChooseView;
import org.telegram.ui.Components.URLSpanNoUnderline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import kotlin.Unit;
import okhttp3.HttpUrl;
import tw.nekomimi.nekogram.ui.BottomBuilder;
import tw.nekomimi.nekogram.proxy.ShadowsocksRSettingsActivity;
import tw.nekomimi.nekogram.proxy.ShadowsocksSettingsActivity;
import tw.nekomimi.nekogram.proxy.SubSettingsActivity;
import tw.nekomimi.nekogram.proxy.TrojanSettingsActivity;
import tw.nekomimi.nekogram.proxy.VmessSettingsActivity;
import tw.nekomimi.nekogram.proxy.WsSettingsActivity;
import tw.nekomimi.nekogram.parts.ProxyChecksKt;
import tw.nekomimi.nekogram.proxy.SubInfo;
import tw.nekomimi.nekogram.proxy.SubManager;
import tw.nekomimi.nekogram.utils.AlertUtil;
import tw.nekomimi.nekogram.utils.FileUtil;
import tw.nekomimi.nekogram.utils.ProxyUtil;
import tw.nekomimi.nekogram.utils.UIUtil;
import tw.nekomimi.nekogram.NekoConfig;

public class ProxyListActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private final static boolean IS_PROXY_ROTATION_AVAILABLE = true;
    private static final int MENU_DELETE = 0;
    private static final int MENU_SHARE = 1;

    private ListAdapter listAdapter;
    private RecyclerListView listView;
    @SuppressWarnings("FieldCanBeLocal")
    private LinearLayoutManager layoutManager;

    private int currentConnectionState;

    private boolean useProxySettings;
    private boolean useProxyForCalls;

    private int rowCount;
    private int useProxyRow;
    private int enablePublicProxyRow;
    private int useProxyShadowRow;
    private int connectionsHeaderRow;
    private int proxyStartRow;
    private int proxyEndRow;
    private int proxyAddRow;
    private int proxyShadowRow;
    private int callsRow;
    private int rotationRow;
    private int rotationTimeoutRow;
    private int rotationTimeoutInfoRow;
    private int callsDetailRow;
    private int deleteAllRow;

    private ItemTouchHelper itemTouchHelper;
    private NumberTextView selectedCountTextView;
    private ActionBarMenuItem shareMenuItem;
    private ActionBarMenuItem deleteMenuItem;

    private List<SharedConfig.ProxyInfo> selectedItems = new ArrayList<>();
    private List<SharedConfig.ProxyInfo> proxyList = new ArrayList<>();
    private boolean wasCheckedAllList;

    private ActionBarMenuItem otherItem;

    public class TextDetailProxyCell extends FrameLayout {

        private TextView textView;
        private TextView valueTextView;
        private ImageView checkImageView;
        private SharedConfig.ProxyInfo currentInfo;
        private Drawable checkDrawable;

        private CheckBox2 checkBox;
        private boolean isSelected;
        private boolean isSelectionEnabled;

        private int color;
        private Pattern urlPattern;

        public TextDetailProxyCell(Context context) {
            super(context);

            textView = new TextView(context);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            textView.setLines(1);
            textView.setMaxLines(1);
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setGravity((LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
            addView(textView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, (LocaleController.isRTL ? 56 : 21), 10, (LocaleController.isRTL ? 21 : 56), 0));

            valueTextView = new TextView(context);
            valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            valueTextView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
            valueTextView.setLines(1);
            valueTextView.setMaxLines(1);
            valueTextView.setSingleLine(true);
            valueTextView.setCompoundDrawablePadding(AndroidUtilities.dp(6));
            valueTextView.setEllipsize(TextUtils.TruncateAt.END);
            valueTextView.setPadding(0, 0, 0, 0);
            addView(valueTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, (LocaleController.isRTL ? 56 : 21), 35, (LocaleController.isRTL ? 21 : 56), 0));

            checkImageView = new ImageView(context);
            checkImageView.setImageResource(R.drawable.msg_info);
            checkImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3), PorterDuff.Mode.MULTIPLY));
            checkImageView.setScaleType(ImageView.ScaleType.CENTER);
            checkImageView.setContentDescription(LocaleController.getString("Edit", R.string.Edit));
            addView(checkImageView, LayoutHelper.createFrame(48, 48, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.TOP, 8, 8, 8, 0));
            checkImageView.setOnClickListener(v -> {
                SharedConfig.ProxyInfo info = currentInfo;
                if (info instanceof SharedConfig.VmessProxy) {
                    if (((SharedConfig.VmessProxy) info).bean.getConfigType() == V2RayConfig.EConfigType.Trojan) {
                        presentFragment(new TrojanSettingsActivity((SharedConfig.VmessProxy) info));
                    } else {
                        presentFragment(new VmessSettingsActivity((SharedConfig.VmessProxy) info));
                    }
                } else if (info instanceof SharedConfig.ShadowsocksProxy) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        presentFragment(new ShadowsocksSettingsActivity((SharedConfig.ShadowsocksProxy) info));
                    }
                } else if (info instanceof SharedConfig.ShadowsocksRProxy) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        presentFragment(new ShadowsocksRSettingsActivity((SharedConfig.ShadowsocksRProxy) info));
                    }
                } else if (info instanceof SharedConfig.WsProxy) {
                    presentFragment(new WsSettingsActivity((SharedConfig.WsProxy) info));
                } else {
                    presentFragment(new ProxySettingsActivity(info));
                }
            });

            checkBox = new CheckBox2(context, 21);
            checkBox.setColor(Theme.key_checkbox, Theme.key_radioBackground, Theme.key_checkboxCheck);
            checkBox.setDrawBackgroundAsArc(14);
            checkBox.setVisibility(GONE);
            addView(checkBox, LayoutHelper.createFrame(24, 24, (LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL, 16, 0, 8, 0));

            setWillNotDraw(false);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64) + 1, MeasureSpec.EXACTLY));
        }

        @SuppressLint("SetTextI18n")
        public void setProxy(SharedConfig.ProxyInfo proxyInfo) {

            String title = proxyInfo.getTitle();

            SpannableStringBuilder stringBuilder = null;
            try {
                if (urlPattern == null) {
                    urlPattern = Pattern.compile("@[a-zA-Z\\d_]{1,32}");
                }
                Matcher matcher = urlPattern.matcher(title);
                while (matcher.find()) {
                    if (stringBuilder == null) {
                        stringBuilder = new SpannableStringBuilder(title);
                        textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                    }
                    int start = matcher.start();
                    int end = matcher.end();
                    if (title.charAt(start) != '@') {
                        start++;
                    }
                    URLSpanNoUnderline url = new URLSpanNoUnderline(title.subSequence(start + 1, end).toString()) {
                        @Override
                        public void onClick(View widget) {
                            MessagesController.getInstance(currentAccount).openByUserName(getURL(), ProxyListActivity.this, 1);
                        }
                    };
                    stringBuilder.setSpan(url, start, end, 0);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }

            textView.setText(stringBuilder == null ? title : stringBuilder);
            currentInfo = proxyInfo;
        }

        public void updateStatus() {
            String colorKey;
            if (SharedConfig.currentProxy == currentInfo && useProxySettings) {
                if (currentConnectionState == ConnectionsManager.ConnectionStateConnected || currentConnectionState == ConnectionsManager.ConnectionStateUpdating) {
                    colorKey = Theme.key_windowBackgroundWhiteBlueText6;
                    if (currentInfo.ping != 0) {
                        valueTextView.setText(LocaleController.getString("Connected", R.string.Connected) + ", " + LocaleController.formatString("Ping", R.string.Ping, currentInfo.ping));
                    } else {
                        valueTextView.setText(LocaleController.getString("Connected", R.string.Connected));
                    }
                    if (!currentInfo.checking && !currentInfo.available) {
                        currentInfo.availableCheckTime = 0;
                    }
                } else {
                    colorKey = Theme.key_windowBackgroundWhiteGrayText2;
                    valueTextView.setText(LocaleController.getString("Connecting", R.string.Connecting));
                }
            } else {
                if (currentInfo.checking) {
                    valueTextView.setText(LocaleController.getString("Checking", R.string.Checking));
                    colorKey = Theme.key_windowBackgroundWhiteGrayText2;
                } else if (currentInfo.available) {
                    if (currentInfo.ping != 0) {
                        valueTextView.setText(LocaleController.getString("Available", R.string.Available) + ", " + LocaleController.formatString("Ping", R.string.Ping, currentInfo.ping));
                    } else {
                        valueTextView.setText(LocaleController.getString("Available", R.string.Available));
                    }
                    colorKey = Theme.key_windowBackgroundWhiteGreenText;
                } else {
                    valueTextView.setText(LocaleController.getString("Unavailable", R.string.Unavailable));
                    colorKey = Theme.key_windowBackgroundWhiteRedText4;
                }
            }
            color = Theme.getColor(colorKey);
            valueTextView.setTag(colorKey);
            valueTextView.setTextColor(color);
            if (checkDrawable != null) {
                checkDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }

        }

        public void setSelectionEnabled(boolean enabled, boolean animated) {
            if (isSelectionEnabled == enabled && animated) {
                return;
            }
            isSelectionEnabled = enabled;

            float fromX = 0, toX = LocaleController.isRTL ? -AndroidUtilities.dp(32) : AndroidUtilities.dp(32);
            if (!animated) {
                float x = enabled ? toX : fromX;
                textView.setTranslationX(x);
                valueTextView.setTranslationX(x);
                checkImageView.setTranslationX(x);
                checkBox.setTranslationX((LocaleController.isRTL ? AndroidUtilities.dp(32) : -AndroidUtilities.dp(32)) + x);
                checkImageView.setVisibility(enabled ? GONE : VISIBLE);
                checkImageView.setAlpha(1f);
                checkImageView.setScaleX(1f);
                checkImageView.setScaleY(1f);
                checkBox.setVisibility(enabled ? VISIBLE : GONE);
                checkBox.setAlpha(1f);
                checkBox.setScaleX(1f);
                checkBox.setScaleY(1f);
            } else {
                ValueAnimator animator = ValueAnimator.ofFloat(enabled ? 0 : 1, enabled ? 1 : 0).setDuration(200);
                animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animator.addUpdateListener(animation -> {
                    float val = (float) animation.getAnimatedValue();
                    float x = AndroidUtilities.lerp(fromX, toX, val);
                    textView.setTranslationX(x);
                    valueTextView.setTranslationX(x);
                    checkImageView.setTranslationX(x);
                    checkBox.setTranslationX((LocaleController.isRTL ? AndroidUtilities.dp(32) : -AndroidUtilities.dp(32)) + x);

                    float scale = 0.5f + val * 0.5f;
                    checkBox.setScaleX(scale);
                    checkBox.setScaleY(scale);
                    checkBox.setAlpha(val);

                    scale = 0.5f + (1f - val) * 0.5f;
                    checkImageView.setScaleX(scale);
                    checkImageView.setScaleY(scale);
                    checkImageView.setAlpha(1f - val);
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (enabled) {
                            checkBox.setAlpha(0f);
                            checkBox.setVisibility(VISIBLE);
                        } else {
                            checkImageView.setAlpha(0f);
                            checkImageView.setVisibility(VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (enabled) {
                            checkImageView.setVisibility(GONE);
                        } else {
                            checkBox.setVisibility(GONE);
                        }
                    }
                });
                animator.start();
            }
        }

        public void setItemSelected(boolean selected, boolean animated) {
            if (selected == isSelected && animated) {
                return;
            }
            isSelected = selected;
            checkBox.setChecked(selected, animated);
        }

        public void setChecked(boolean checked) {
            if (checked) {
                if (checkDrawable == null) {
                    checkDrawable = getResources().getDrawable(R.drawable.proxy_check).mutate();
                }
                if (checkDrawable != null) {
                    checkDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
                }
                if (LocaleController.isRTL) {
                    valueTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, checkDrawable, null);
                } else {
                    valueTextView.setCompoundDrawablesWithIntrinsicBounds(checkDrawable, null, null, null);
                }
            } else {
                valueTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
        }

        public void setValue(CharSequence value) {
            valueTextView.setText(value);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateStatus();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawLine(0, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    public ProxyListActivity() {
    }

    private String alert;

    public ProxyListActivity(String alert) {
        this.alert = alert;
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        currentConnectionState = ConnectionsManager.getInstance(currentAccount).getConnectionState();

        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxyChangedByRotation);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxyCheckDone);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.didUpdateConnectionState);

        final SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        useProxySettings = SharedConfig.proxyEnabled && !proxyList.isEmpty();
        useProxyForCalls = preferences.getBoolean("proxy_enabled_calls", false);

        updateRows(true);

        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxyChangedByRotation);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxyCheckDone);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);

        if (currentCheck != null) currentCheck.shutdownNow();

    }

    private int menu_add = 1;
    private int menu_add_input_socks = 2;
    private int menu_add_input_telegram = 3;
    private int menu_add_input_vmess = 4;
    private int menu_add_input_ss = 7;
    private int menu_add_input_ssr = 8;
    private int menu_add_input_ws = 9;
    private int menu_add_input_rb = 17;

    private int menu_add_import_from_clipboard = 5;
    private int menu_add_scan_qr = 6;
    private int menu_other = 9;
    private int menu_retest_ping = 10;
    private int menu_reorder_by_ping = 11;
    private int menu_export_json = 12;
    private int menu_import_json = 13;
    private int menu_delete_all = 14;
    private int menu_delete_unavailable = 15;
    private int menu_sub = 16;

    public void processProxyList(ArrayList<String> files) {

        for (String proxyListFilePath : files) {

            File proxyListFile = new File(proxyListFilePath);

            processProxyListFile(getParentActivity(), proxyListFile);

        }

    }

    public static String processProxyListFile(Context ctx, File proxyListFile) {

        try {

            if (proxyListFile.length() > 2 * 1024 * 1024L) {

                throw new IllegalArgumentException("file too large.");

            }

            JSONObject proxyRootObject = new JSONObject(FileUtil.readUtf8String(proxyListFile));

            int version = proxyRootObject.optInt("nekox_proxy_list_version", 1);

            if (version == 1) {

                if (proxyRootObject.isNull("proxies")) {

                    throw new IllegalArgumentException("proxies array not found.");

                }

                JSONArray proxyArray = proxyRootObject.getJSONArray("proxies");

                if (proxyArray.length() == 0) {

                    throw new IllegalArgumentException("Empty proxy list.");

                }

                LinkedList<String> imported = new LinkedList<>();
                LinkedHashMap<String, String> errors = new LinkedHashMap<>();

                for (int index = 0; index < proxyArray.length(); index++) {

                    String proxyUrl = proxyArray.getString(index);

                    try {

                        imported.add(ProxyUtil.importInBackground(proxyUrl).getTitle());

                    } catch (Exception ex) {

                        errors.put(proxyUrl.length() < 15 ? proxyUrl : (proxyUrl.substring(0, 15) + "..."), ex.getMessage());

                    }

                }

                StringBuilder status = new StringBuilder();

                if (!imported.isEmpty()) {

                    status.append(LocaleController.getString("ImportedProxies", R.string.ImportedProxies));

                    for (String success : imported) {

                        status.append("\n").append(success);

                    }


                    if (!errors.isEmpty()) {

                        status.append("\n\n");

                    }

                }

                if (!errors.isEmpty()) {

                    status.append(LocaleController.getString("ErrorsInImport", R.string.ErrorsInImport));

                    for (Map.Entry<String, String> error : errors.entrySet()) {

                        status.append("\n").append(error.getKey()).append(": ").append(error.getValue());

                    }

                }

                if (imported.isEmpty()) {

                    AlertUtil.showSimpleAlert(ctx, status.toString());

                } else {

                    return status.toString();

                }

            } else {

                throw new IllegalArgumentException("invalid proxy list version " + version + ".");

            }

        } catch (Exception e) {

            AlertUtil.showSimpleAlert(ctx, LocaleController.getString("InvalidProxyFile", R.string.InvalidProxyFile) + proxyListFile.getPath() + "\n\n" + e.getMessage());

        }

        return null;

    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("ProxySettings", R.string.ProxySettings));
        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }
        actionBar.setAllowOverlayTitle(false);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == menu_retest_ping) {
                    checkProxyList(true);
                } else if (id == menu_reorder_by_ping) {
                    SharedConfig.proxyList = new LinkedList<>(new TreeSet<>(SharedConfig.getProxyList()));
                    SharedConfig.saveProxyList();
                    updateRows(true);
                } else if (id == menu_export_json) {
                    File cacheFile = new File(ApplicationLoader.applicationContext.getExternalCacheDir(), "Proxy-List-" + new Date().toLocaleString() + ".nekox.json");

                    try {

                        JSONObject listRoot = new JSONObject();

                        listRoot.put("nekox_proxy_list_version", 1);

                        JSONArray proxyArray = new JSONArray();

                        for (SharedConfig.ProxyInfo info : SharedConfig.getProxyList()) {

                            if (info.subId <= 1) {

                                continue;

                            }

                            proxyArray.put(info.toUrl());

                        }

                        if (proxyArray.length() == 0) {
                            AlertUtil.showSimpleAlert(getParentActivity(), LocaleController.getString("NoProxy", R.string.NoProxy));
                            return;
                        }

                        listRoot.put("proxies", proxyArray);

                        FileUtil.writeUtf8String(listRoot.toString(4), cacheFile);
                    } catch (Exception e) {
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("*/*");
                    if (Build.VERSION.SDK_INT >= 24) {
                        try {
                            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getParentActivity(), BuildConfig.APPLICATION_ID + ".provider", cacheFile));
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (Exception ignore) {
                            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(cacheFile));
                        }
                    } else {
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(cacheFile));
                    }
                    getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
                } else if (id == menu_import_json) {
                    try {
                        if (Build.VERSION.SDK_INT >= 23 && getParentActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                            getParentActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 4);
                            return;
                        }
                    } catch (Throwable ignore) {
                    }
                    DocumentSelectActivity fragment = new DocumentSelectActivity(false);
                    fragment.setMaxSelectedFiles(-1);
                    fragment.setAllowPhoto(false);
                    fragment.setDelegate(new DocumentSelectActivity.DocumentSelectActivityDelegate() {

                        @Override
                        public void didSelectFiles(DocumentSelectActivity activity, ArrayList<String> files, String caption, boolean notify, int scheduleDate) {
                            activity.finishFragment();
                            processProxyList(files);
                        }

                        @Override
                        public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos, boolean notify, int scheduleDate) {
                        }

                        @Override
                        public void startDocumentSelectActivity() {
                        }
                    });
                    presentFragment(fragment);
                } else if (id == menu_delete_all) {
                    AlertUtil.showConfirm(getParentActivity(),
                            LocaleController.getString("DeleteAllServer", R.string.DeleteAllServer),
                            R.drawable.baseline_delete_24, LocaleController.getString("Delete", R.string.Delete),
                            true, () -> {
                                SharedConfig.deleteAllProxy();
                                updateRows(true);
                            });
                } else if (id == menu_delete_unavailable) {
                    AlertUtil.showConfirm(getParentActivity(),
                            LocaleController.getString("DeleteUnavailableServer", R.string.DeleteUnavailableServer),
                            R.drawable.baseline_delete_24, LocaleController.getString("Delete", R.string.Delete),
                            true, () -> {
                                deleteUnavailableProxy();
                            });
                } else if (id == menu_sub) {
                    showSubDialog();
                }
            }
        });

        ActionBarMenu menu = actionBar.createMenu();

        ActionBarMenuItem addItem = menu.addItem(menu_add, R.drawable.add);

        addItem.addSubItem(menu_add_import_from_clipboard, LocaleController.getString("ImportProxyFromClipboard", R.string.ImportProxyFromClipboard)).setOnClickListener((v) -> {

            ProxyUtil.importFromClipboard(getParentActivity());

        });

        addItem.addSubItem(menu_add_scan_qr, LocaleController.getString("ScanQRCode", R.string.ScanQRCode)).setOnClickListener((v) -> {

            if (Build.VERSION.SDK_INT >= 23) {
                if (getParentActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    getParentActivity().requestPermissions(new String[]{Manifest.permission.CAMERA}, 22);
                    return;
                }
            }

            CameraScanActivity.showAsSheet(this, new CameraScanActivity.CameraScanActivityDelegate() {

                @Override
                public void didFindQr(String text) {

                    try {
                        HttpUrl.parse(text);
                        Browser.openUrl(getParentActivity(), text);
                        return;
                    } catch (Exception ignored) {
                    }

                    AlertUtil.showCopyAlert(getParentActivity(), text);

                }

            });

        });

        addItem.addSubItem(menu_add_input_socks, LocaleController.getString("AddProxySocks5", R.string.AddProxySocks5)).setOnClickListener((v) -> presentFragment(new ProxySettingsActivity(0)));
        addItem.addSubItem(menu_add_input_telegram, LocaleController.getString("AddProxyTelegram", R.string.AddProxyTelegram)).setOnClickListener((v) -> presentFragment(new ProxySettingsActivity(1)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addItem.addSubItem(menu_add_input_ws, LocaleController.getString("AddProxyWs", R.string.AddProxyWs)).setOnClickListener((v) -> presentFragment(new WsSettingsActivity()));
        }

        if (!BuildVars.isMini) {

            addItem.addSubItem(menu_add_input_vmess, LocaleController.getString("AddProxyVmess", R.string.AddProxyVmess)).setOnClickListener((v) -> presentFragment(new VmessSettingsActivity()));
            addItem.addSubItem(menu_add_input_vmess, LocaleController.getString("AddProxyVmess", R.string.AddProxyTrojan)).setOnClickListener((v) -> presentFragment(new TrojanSettingsActivity()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                addItem.addSubItem(menu_add_input_ss, LocaleController.getString("AddProxySS", R.string.AddProxySS)).setOnClickListener((v) -> presentFragment(new ShadowsocksSettingsActivity()));
                addItem.addSubItem(menu_add_input_ssr, LocaleController.getString("AddProxySSR", R.string.AddProxySSR)).setOnClickListener((v) -> presentFragment(new ShadowsocksRSettingsActivity()));
            }
            // addItem.addSubItem(menu_add_input_rb, LocaleController.getString("AddProxyRB", R.string.AddProxyRB)).setOnClickListener((v) -> presentFragment(new RelayBatonSettingsActivity()));

        }

        menu.addItem(menu_sub, R.drawable.msg_list);

        otherItem = menu.addItem(menu_other, R.drawable.ic_ab_other);
        otherItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        otherItem.addSubItem(menu_retest_ping, LocaleController.getString("RetestPing", R.string.RetestPing));
        otherItem.addSubItem(menu_reorder_by_ping, LocaleController.getString("ReorderByPing", R.string.ReorderByPing));
        otherItem.addSubItem(menu_delete_all, LocaleController.getString("DeleteAllServer", R.string.DeleteAllServer));
        otherItem.addSubItem(menu_delete_unavailable, LocaleController.getString("DeleteUnavailableServer", R.string.DeleteUnavailableServer));

        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        listView = new RecyclerListView(context) {
            @Override
            protected void dispatchDraw(Canvas canvas) {
                drawSectionBackground(canvas, proxyStartRow, proxyEndRow, Theme.getColor(Theme.key_windowBackgroundWhite));
                super.dispatchDraw(canvas);
            }
        };
        ((DefaultItemAnimator) listView.getItemAnimator()).setDelayAnimations(false);
        ((DefaultItemAnimator) listView.getItemAnimator()).setTranslationInterpolator(CubicBezierInterpolator.DEFAULT);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((view, position) -> {
            if (position == useProxyRow) {
                if (SharedConfig.currentProxy == null) {
                    if (!SharedConfig.proxyList.isEmpty()) {
                        SharedConfig.setCurrentProxy(SharedConfig.proxyList.get(0));
                    } else {
                        addProxy();
                        return;
                    }
                }

                useProxySettings = !useProxySettings;
                updateRows(true);

                TextCheckCell textCheckCell = (TextCheckCell) view;
                textCheckCell.setChecked(useProxySettings);

                NotificationCenter.getGlobalInstance().removeObserver(ProxyListActivity.this, NotificationCenter.proxySettingsChanged);
                SharedConfig.setProxyEnable(useProxySettings);
                NotificationCenter.getGlobalInstance().addObserver(ProxyListActivity.this, NotificationCenter.proxySettingsChanged);

                updateRows(true);
            } else if (position == enablePublicProxyRow) {
                final boolean enabled = NekoConfig.enablePublicProxy.toggleConfigBool();
                TextCheckCell cell = (TextCheckCell) view;
                cell.setChecked(enabled);
                UIUtil.runOnIoDispatcher(() -> {
                    SharedPreferences pref = MessagesController.getGlobalMainSettings();
                    for (SubInfo subInfo : SubManager.getSubList().find()) {
                        if (subInfo.id != SubManager.publicProxySubID) continue;
                        subInfo.enable = enabled;
                        if (enabled) {
                            try {
                                subInfo.proxies = subInfo.reloadProxies();
                                subInfo.lastFetch = System.currentTimeMillis();
                            } catch (Exception ignored) {
                            }
                        }
                        SubManager.getSubList().update(subInfo, true);
                        break;
                    }
                    // clear proxy id
                    useProxySettings = false;
                    SharedConfig.setCurrentProxy(null);
                    // reload list & UI
                    AndroidUtilities.runOnUIThread(() -> {
                        SharedConfig.reloadProxyList();
                        updateRows(true);
                    });
                });

                updateRows(true);
            } else if (position == rotationRow) {
                SharedConfig.proxyRotationEnabled = !SharedConfig.proxyRotationEnabled;
                TextCheckCell textCheckCell = (TextCheckCell) view;
                textCheckCell.setChecked(SharedConfig.proxyRotationEnabled);
                SharedConfig.saveConfig();

                updateRows(true);
            } else if (position == callsRow) {
                useProxyForCalls = !useProxyForCalls;
                TextCheckCell textCheckCell = (TextCheckCell) view;
                textCheckCell.setChecked(useProxyForCalls);
                SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
                editor.putBoolean("proxy_enabled_calls", useProxyForCalls);
                editor.apply();
            } else if (position >= proxyStartRow && position < proxyEndRow) {
                if (!selectedItems.isEmpty()) {
                    listAdapter.toggleSelected(position);
                    return;
                }
                SharedConfig.ProxyInfo info = proxyList.get(position - proxyStartRow);
                useProxySettings = true;
                SharedConfig.setCurrentProxy(info);
                updateRows(true);
                RecyclerListView.Holder holder = (RecyclerListView.Holder) listView.findViewHolderForAdapterPosition(useProxyRow);
                if (holder != null) {
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    textCheckCell.setChecked(true);
                }
            }
        });
        listView.setOnItemLongClickListener((view, position) -> {
            if (position >= proxyStartRow && position < proxyEndRow) {
                final SharedConfig.ProxyInfo info = SharedConfig.proxyList.get(position - proxyStartRow);
                BottomBuilder builder = new BottomBuilder(context);
                builder.addItems(new String[]{

                        info.subId == 1 ? null : LocaleController.getString("EditProxy", R.string.EditProxy),
                        info.subId == 1 && info instanceof SharedConfig.WsProxy ? null : LocaleController.getString("ShareProxy", R.string.ShareProxy),
                        info.subId == 1 && info instanceof SharedConfig.WsProxy ? null : LocaleController.getString("ShareQRCode", R.string.ShareQRCode),
                        info.subId == 1 && info instanceof SharedConfig.WsProxy ? null : LocaleController.getString("CopyLink", R.string.CopyLink),
                        info.subId == 1 ? null : LocaleController.getString("ProxyDelete", R.string.ProxyDelete),
                        LocaleController.getString("Cancel", R.string.Cancel)

                }, new int[]{

                        R.drawable.group_edit,
                        R.drawable.baseline_share_24,
                        R.drawable.wallet_qr,
                        R.drawable.baseline_link_24,
                        R.drawable.baseline_delete_24,
                        R.drawable.baseline_cancel_24

                }, (i, text, cell) -> {

                    if (i == 0) {
                        if (info instanceof SharedConfig.VmessProxy) {
                            if (((SharedConfig.VmessProxy) info).bean.getConfigType() == V2RayConfig.EConfigType.Trojan) {
                                presentFragment(new TrojanSettingsActivity((SharedConfig.VmessProxy) info));
                            } else {
                                presentFragment(new VmessSettingsActivity((SharedConfig.VmessProxy) info));
                            }
                        } else if (info instanceof SharedConfig.ShadowsocksProxy) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                presentFragment(new ShadowsocksSettingsActivity((SharedConfig.ShadowsocksProxy) info));
                            }
                        } else if (info instanceof SharedConfig.ShadowsocksRProxy) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                presentFragment(new ShadowsocksRSettingsActivity((SharedConfig.ShadowsocksRProxy) info));
                            }
                        } else if (info instanceof SharedConfig.WsProxy) {
                            presentFragment(new WsSettingsActivity((SharedConfig.WsProxy) info));
                        } else {
                            presentFragment(new ProxySettingsActivity(info));
                        }
                    } else if (i == 1) {
                        ProxyUtil.shareProxy(getParentActivity(), info, 0);
                    } else if (i == 2) {
                        ProxyUtil.shareProxy(getParentActivity(), info, 2);
                    } else if (i == 3) {
                        ProxyUtil.shareProxy(getParentActivity(), info, 1);
                    } else if (i == 4) {
                        AlertUtil.showConfirm(getParentActivity(),
                                LocaleController.getString("DeleteProxy", R.string.DeleteProxy),
                                R.drawable.baseline_delete_24, LocaleController.getString("Delete", R.string.Delete),
                                true, () -> {

                                    SharedConfig.deleteProxy(info);
                                    if (SharedConfig.currentProxy == null) {
                                        SharedConfig.setProxyEnable(false);
                                    }
                                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged);
                                });
                    }
                    return Unit.INSTANCE;
                });
                showDialog(builder.create());
                return true;
            }
            return false;
        });

        if (alert != null) {
            AlertUtil.showSimpleAlert(context, alert);
            alert = null;
        }
        return fragmentView;
    }

    @Override
    public boolean onBackPressed() {
        if (!selectedItems.isEmpty()) {
            listAdapter.clearSelected();
            return false;
        }
        return true;
    }

    @SuppressLint("NewApi")
    private void addProxy() {
        BottomBuilder builder = new BottomBuilder(getParentActivity());
        builder.addItems(new String[]{

                LocaleController.getString("AddProxySocks5", R.string.AddProxySocks5),
                LocaleController.getString("AddProxyTelegram", R.string.AddProxyTelegram),
                LocaleController.getString("AddProxyWs", R.string.AddProxyWs),
                BuildVars.isMini ? null : LocaleController.getString("AddProxyVmess", R.string.AddProxyVmess),
                BuildVars.isMini ? null : LocaleController.getString("AddProxyTrojan", R.string.AddProxyTrojan),
                BuildVars.isMini || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ? null : LocaleController.getString("AddProxySS", R.string.AddProxySS),
                BuildVars.isMini || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ? null : LocaleController.getString("AddProxySSR", R.string.AddProxySSR),
                LocaleController.getString("ImportProxyFromClipboard", R.string.ImportProxyFromClipboard),
                LocaleController.getString("ScanQRCode", R.string.ScanQRCode)

        }, null, (i, t, c) -> {

            if (i == 0) {
                presentFragment(new ProxySettingsActivity(0));
            } else if (i == 1) {
                presentFragment(new ProxySettingsActivity(1));
            } else if (i == 2) {
                presentFragment(new WsSettingsActivity());
            } else if (i == 3) {
                presentFragment(new VmessSettingsActivity());
            } else if (i == 4) {
                presentFragment(new TrojanSettingsActivity());
            } else if (i == 5) {
                presentFragment(new ShadowsocksSettingsActivity());
            } else if (i == 6) {
                presentFragment(new ShadowsocksRSettingsActivity());
            } else if (i == 7) {
                ProxyUtil.importFromClipboard(getParentActivity());
            } else {

                if (Build.VERSION.SDK_INT >= 23) {
                    if (getParentActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        getParentActivity().requestPermissions(new String[]{Manifest.permission.CAMERA}, 22);
                        return Unit.INSTANCE;
                    }
                }

                CameraScanActivity.showAsSheet(this, new CameraScanActivity.CameraScanActivityDelegate() {

                    @Override
                    public void didFindQr(String text) {

                        try {
                            HttpUrl.parse(text);
                            Browser.openUrl(getParentActivity(), text);
                            return;
                        } catch (Exception ignored) {
                        }

                        AlertUtil.showCopyAlert(getParentActivity(), text);

                    }

                });
            }
            return Unit.INSTANCE;
        });
        builder.show();
    }

    private void updateRows(boolean notify) {
        proxyList = SharedConfig.getProxyList();
        rowCount = 0;
        useProxyRow = rowCount++;
        if (useProxySettings && SharedConfig.currentProxy != null && SharedConfig.proxyList.size() > 1 && IS_PROXY_ROTATION_AVAILABLE) {
            rotationRow = rowCount++;
            if (SharedConfig.proxyRotationEnabled) {
                rotationTimeoutRow = rowCount++;
                rotationTimeoutInfoRow = rowCount++;
            } else {
                rotationTimeoutRow = -1;
                rotationTimeoutInfoRow = -1;
            }
        } else {
            rotationRow = -1;
            rotationTimeoutRow = -1;
            rotationTimeoutInfoRow = -1;
        }
        if (rotationTimeoutInfoRow == -1) {
            useProxyShadowRow = rowCount++;
        } else {
            useProxyShadowRow = -1;
        }
        connectionsHeaderRow = rowCount++;

        if (notify) {
            proxyList.clear();
            proxyList.addAll(SharedConfig.proxyList);

            boolean checking = false;
            if (!wasCheckedAllList) {
                for (SharedConfig.ProxyInfo info : proxyList) {
                    if (info.checking || info.availableCheckTime == 0) {
                        checking = true;
                        break;
                    }
                }
                if (!checking) {
                    wasCheckedAllList = true;
                }
            }

            boolean isChecking = checking;
            Collections.sort(proxyList, (o1, o2) -> {
                long bias1 = SharedConfig.currentProxy == o1 ? -200000 : 0;
                if (!o1.available) {
                    bias1 += 100000;
                }
                long bias2 = SharedConfig.currentProxy == o2 ? -200000 : 0;
                if (!o2.available) {
                    bias2 += 100000;
                }
                return Long.compare(isChecking && o1 != SharedConfig.currentProxy ? SharedConfig.proxyList.indexOf(o1) * 10000L : o1.ping + bias1,
                        isChecking && o2 != SharedConfig.currentProxy ? SharedConfig.proxyList.indexOf(o2) * 10000L : o2.ping + bias2);
            });
        }

        enablePublicProxyRow = rowCount++;
        if (!proxyList.isEmpty()) {
            proxyStartRow = rowCount;
            rowCount += proxyList.size();
            proxyEndRow = rowCount;
        } else {
            proxyStartRow = -1;
            proxyEndRow = -1;
        }
        proxyAddRow = rowCount++;
        proxyShadowRow = rowCount++;
        if (SharedConfig.currentProxy == null || SharedConfig.currentProxy.secret.isEmpty()) {
            boolean change = callsRow == -1;
            callsRow = rowCount++;
            callsDetailRow = rowCount++;
            UIUtil.runOnUIThread(() -> {
                if (!notify && change) {
                    listAdapter.notifyItemChanged(proxyShadowRow);
                    listAdapter.notifyItemRangeInserted(proxyShadowRow + 1, 2);
                }
            });
        } else {
            boolean change = callsRow != -1;
            callsRow = -1;
            callsDetailRow = -1;
            if (!notify && change) {
                UIUtil.runOnUIThread(() -> {
                    listAdapter.notifyItemChanged(proxyShadowRow);
                    listAdapter.notifyItemRangeRemoved(proxyShadowRow + 1, 2);
                });
            }
        }
        if (proxyList.size() >= 10) {
            deleteAllRow = rowCount++;
        } else {
            deleteAllRow = -1;
        }
        checkProxyList(false);
        if (notify && listAdapter != null) {
            UIUtil.runOnUIThread(() -> {
                try {
                    listView.clearAnimation();
                    listView.getRecycledViewPool().clear();
                    listAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            });
        }
    }

    private ExecutorService currentCheck;

    private void checkProxyList(boolean force) {
        if (currentCheck == null) {
            currentCheck = Executors.newFixedThreadPool(3);
        }
        ProxyChecksKt.checkProxyList(this, force, currentCheck);
    }

    private void deleteUnavailableProxy() {
        for (SharedConfig.ProxyInfo info : SharedConfig.getProxyList()) {
            if (info.subId != 0) continue;
            checkSingleProxy(info, 1, () -> {
                deleteUnavailableProxy(info);
            });
        }
    }

    private void deleteUnavailableProxy(SharedConfig.ProxyInfo proxyInfo) {
        if (!proxyInfo.available) {
            SharedConfig.deleteProxy(proxyInfo);
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxyCheckDone);
        }
    }

    public void checkSingleProxy(SharedConfig.ProxyInfo proxyInfo, int repeat, Runnable callback) {

        if (SharedConfig.activeAccounts.isEmpty() && proxyInfo instanceof SharedConfig.WsProxy) {
            proxyInfo.availableCheckTime = SystemClock.elapsedRealtime();
            proxyInfo.checking = false;
            proxyInfo.available = false;
            proxyInfo.ping = 0;
            callback.run();
            return;
        }

        UIUtil.runOnIoDispatcher(() -> {
            if (proxyInfo instanceof SharedConfig.ExternalSocks5Proxy && !((SharedConfig.ExternalSocks5Proxy) proxyInfo).isStarted()) {
                try {
                    ((SharedConfig.ExternalSocks5Proxy) proxyInfo).start();
                } catch (Exception e) {
                    FileLog.e(e);
                    AlertUtil.showToast(e);
                }
                ThreadUtil.sleep(233L);
            }
            proxyInfo.proxyCheckPingId = ConnectionsManager.getInstance(currentAccount).checkProxy(proxyInfo.address, proxyInfo.port, proxyInfo.username, proxyInfo.password, proxyInfo.secret, time -> AndroidUtilities.runOnUIThread(() -> {
                if (time == -1) {
                    if (repeat > 0) {
                        checkSingleProxy(proxyInfo, repeat - 1, callback);
                    } else {
                        proxyInfo.availableCheckTime = SystemClock.elapsedRealtime();
                        proxyInfo.checking = false;
                        proxyInfo.available = false;
                        proxyInfo.ping = 0;
                        if (proxyInfo instanceof SharedConfig.ExternalSocks5Proxy && proxyInfo != SharedConfig.currentProxy) {
                            ((SharedConfig.ExternalSocks5Proxy) proxyInfo).stop();
                        }
                        if (callback != null) {
                            UIUtil.runOnUIThread(callback);
                        }
                    }
                } else {
                    proxyInfo.availableCheckTime = SystemClock.elapsedRealtime();
                    proxyInfo.checking = false;
                    proxyInfo.ping = time;
                    proxyInfo.available = true;
                    if (proxyInfo instanceof SharedConfig.ExternalSocks5Proxy && proxyInfo != SharedConfig.currentProxy) {
                        ((SharedConfig.ExternalSocks5Proxy) proxyInfo).stop();
                    }
                    if (callback != null) {
                        UIUtil.runOnUIThread(callback);
                    }
                }
            }));
        });
    }

    private void showSubDialog() {
        BottomBuilder builder = new BottomBuilder(getParentActivity());
        builder.addTitle(LocaleController.getString("ProxySubscription", R.string.ProxySubscription));
        HashMap<SubInfo, Boolean> toChange = new HashMap<>();
        for (SubInfo sub : SubManager.getSubList().find()) {
            TextCheckCell subItem = builder.addCheckItem(sub.name, sub.enable, true, (it, target) -> {
                if (target == sub.enable) {
                    toChange.remove(sub);
                } else {
                    toChange.put(sub, target);
                }
                return Unit.INSTANCE;
            });

            subItem.setOnLongClickListener((it) -> {
                if (sub.internal) return false;
                builder.dismiss();
                presentFragment(new SubSettingsActivity(sub));
                return true;
            });

        }

        builder.addButton(LocaleController.getString("Add", R.string.Add), false, true, (it) -> {
            presentFragment(new SubSettingsActivity());
            return Unit.INSTANCE;
        });

        String updateStr = LocaleController.getString("Update", R.string.Update);
        updateStr = updateStr.toLowerCase();
        updateStr = StrUtil.upperFirst(updateStr);

        builder.addButton(updateStr, (it) -> {
            AlertDialog pro = AlertUtil.showProgress(getParentActivity(), LocaleController.getString("SubscriptionUpdating", R.string.SubscriptionUpdating));
            AtomicBoolean canceled = new AtomicBoolean();
            pro.setOnCancelListener((__) -> {
                canceled.set(true);
            });
            pro.show();

            UIUtil.runOnIoDispatcher(() -> {
                for (SubInfo subInfo : SubManager.getSubList().find()) {
                    if (!subInfo.enable) continue;
                    try {
                        subInfo.proxies = subInfo.reloadProxies();
                        subInfo.lastFetch = System.currentTimeMillis();
                    } catch (IOException allTriesFailed) {
                        if (canceled.get()) return;
                        AlertUtil.showSimpleAlert(getParentActivity(), "All tries failed: " + allTriesFailed.toString().trim());
                        continue;
                    }
                    SubManager.getSubList().update(subInfo, true);
                    if (canceled.get()) return;
                }
                SharedConfig.reloadProxyList();
                updateRows(true);
                UIUtil.runOnUIThread(pro::dismiss);
            });
            return Unit.INSTANCE;
        });

        builder.addButton(LocaleController.getString("OK", R.string.OK), (it) -> {
            if (!toChange.isEmpty()) {
                AlertDialog pro = AlertUtil.showProgress(getParentActivity());
                pro.setCanCancel(false);
                pro.show();

                UIUtil.runOnIoDispatcher(() -> {
                    for (Map.Entry<SubInfo, Boolean> toChangeE : toChange.entrySet()) {
                        toChangeE.getKey().enable = toChangeE.getValue();
                        SubManager.getSubList().update(toChangeE.getKey(), true);
                    }
                    SharedConfig.reloadProxyList();
                    UIUtil.runOnUIThread(() -> updateRows(true));
                    ThreadUtil.sleep(233L);
                    UIUtil.runOnUIThread(pro::dismiss);
                });
            }
            return Unit.INSTANCE;
        });
        builder.show();
    }

    @Override
    protected void onDialogDismiss(Dialog dialog) {
        DownloadController.getInstance(currentAccount).checkAutodownloadSettings();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRows(true);
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.proxyChangedByRotation) {
            listView.forAllChild(view -> {
                RecyclerView.ViewHolder holder = listView.getChildViewHolder(view);
                if (holder.itemView instanceof TextDetailProxyCell) {
                    TextDetailProxyCell cell = (TextDetailProxyCell) holder.itemView;
                    cell.setChecked(cell.currentInfo == SharedConfig.currentProxy);
                    cell.updateStatus();
                }
            });

            updateRows(false);
        } else if (id == NotificationCenter.proxySettingsChanged) {
            updateRows(true);
        } else if (id == NotificationCenter.didUpdateConnectionState) {
            int state = ConnectionsManager.getInstance(account).getConnectionState();
            if (currentConnectionState != state) {
                currentConnectionState = state;
                if (listView != null && SharedConfig.currentProxy != null) {
                    int idx = proxyList.indexOf(SharedConfig.currentProxy);
                    if (idx >= 0) {
                        RecyclerListView.Holder holder = (RecyclerListView.Holder) listView.findViewHolderForAdapterPosition(idx + proxyStartRow);
                        if (holder != null && holder.itemView instanceof TextDetailProxyCell) {
                            TextDetailProxyCell cell = (TextDetailProxyCell) holder.itemView;
                            cell.updateStatus();
                        }
                    }

                    if (currentConnectionState == ConnectionsManager.ConnectionStateConnected) {
                        updateRows(true);
                    }
                }
            }
        } else if (id == NotificationCenter.proxyCheckDone) {
            if (listView != null) {
                if (args.length == 0) {
                    updateRows(true);
                } else {
                    SharedConfig.ProxyInfo proxyInfo = (SharedConfig.ProxyInfo) args[0];
                    int idx = proxyList.indexOf(proxyInfo);
                    if (idx >= 0) {
                        RecyclerListView.Holder holder = (RecyclerListView.Holder) listView.findViewHolderForAdapterPosition(idx + proxyStartRow);
                        if (holder != null && holder.itemView instanceof TextDetailProxyCell) {
                            TextDetailProxyCell cell = (TextDetailProxyCell) holder.itemView;
                            cell.updateStatus();
                        }
                    }
                }

                boolean checking = false;
                if (!wasCheckedAllList) {
                    for (SharedConfig.ProxyInfo info : proxyList) {
                        if (info.checking || info.availableCheckTime == 0) {
                            checking = true;
                            break;
                        }
                    }
                    if (!checking) {
                        wasCheckedAllList = true;
                    }
                }
                if (!checking) {
                    updateRows(true);
                }
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private final static int VIEW_TYPE_SHADOW = 0,
            VIEW_TYPE_TEXT_SETTING = 1,
            VIEW_TYPE_HEADER = 2,
            VIEW_TYPE_TEXT_CHECK = 3,
            VIEW_TYPE_INFO = 4,
            VIEW_TYPE_PROXY_DETAIL = 5,
            VIEW_TYPE_SLIDE_CHOOSER = 6;

        public static final int PAYLOAD_CHECKED_CHANGED = 0;
        public static final int PAYLOAD_SELECTION_CHANGED = 1;
        public static final int PAYLOAD_SELECTION_MODE_CHANGED = 2;

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;

            setHasStableIds(true);
        }

        public void toggleSelected(int position) {
            if (position < proxyStartRow || position >= proxyEndRow) {
                return;
            }
            SharedConfig.ProxyInfo info = proxyList.get(position - proxyStartRow);
            if (selectedItems.contains(info)) {
                selectedItems.remove(info);
            } else {
                selectedItems.add(info);
            }
            notifyItemChanged(position, PAYLOAD_SELECTION_CHANGED);
            checkActionMode();
        }

        public void clearSelected() {
            selectedItems.clear();
            notifyItemRangeChanged(proxyStartRow, proxyEndRow - proxyStartRow, PAYLOAD_SELECTION_CHANGED);
            checkActionMode();
        }

        private void checkActionMode() {
            int selectedCount = selectedItems.size();
            boolean actionModeShowed = actionBar.isActionModeShowed();
            if (selectedCount > 0) {
                selectedCountTextView.setNumber(selectedCount, actionModeShowed);
                if (!actionModeShowed) {
                    actionBar.showActionMode();
                    notifyItemRangeChanged(proxyStartRow, proxyEndRow - proxyStartRow, PAYLOAD_SELECTION_MODE_CHANGED);
                }
            } else if (actionModeShowed) {
                actionBar.hideActionMode();
                notifyItemRangeChanged(proxyStartRow, proxyEndRow - proxyStartRow, PAYLOAD_SELECTION_MODE_CHANGED);
            }
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case VIEW_TYPE_SHADOW: {
                    if (position == proxyShadowRow && callsRow == -1) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
                }
                case VIEW_TYPE_TEXT_SETTING: {
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    if (position == proxyAddRow) {
                        textCell.setText(LocaleController.getString("AddProxy", R.string.AddProxy), deleteAllRow != -1);
                    } else if (position == deleteAllRow) {
                        textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                        textCell.setText(LocaleController.getString(R.string.DeleteAllProxies), false);
                    }
                    break;
                }
                case VIEW_TYPE_HEADER: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == connectionsHeaderRow) {
                        headerCell.setText(LocaleController.getString("ProxyConnections", R.string.ProxyConnections));
                    }
                    break;
                }
                case VIEW_TYPE_TEXT_CHECK: {
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position == useProxyRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UseProxySettings", R.string.UseProxySettings), useProxySettings, rotationRow != -1);
                    } else if (position == callsRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UseProxyForCalls", R.string.UseProxyForCalls), useProxyForCalls, false);
                    } else if (position == enablePublicProxyRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("enablePublicProxy", R.string.enablePublicProxy), NekoConfig.enablePublicProxy.Bool(), false);
                    } else if (position == rotationRow) {
                        checkCell.setTextAndCheck(LocaleController.getString(R.string.UseProxyRotation), SharedConfig.proxyRotationEnabled, true);
                    }
                    break;
                }
                case VIEW_TYPE_INFO: {
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == callsDetailRow) {
                        cell.setText(LocaleController.getString("UseProxyForCallsInfo", R.string.UseProxyForCallsInfo));
                        cell.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    } else if (position == rotationTimeoutInfoRow) {
                        cell.setText(LocaleController.getString(R.string.ProxyRotationTimeoutInfo));
                        cell.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
                }
                case VIEW_TYPE_PROXY_DETAIL: {
                    TextDetailProxyCell cell = (TextDetailProxyCell) holder.itemView;
                    try {
                        SharedConfig.ProxyInfo info = proxyList.get(position - proxyStartRow);
                        cell.setProxy(info);
                        cell.setChecked(SharedConfig.currentProxy == info);
                    } catch (IndexOutOfBoundsException e) {
                    }
                    break;
                }
                case VIEW_TYPE_SLIDE_CHOOSER: {
                    if (position == rotationTimeoutRow) {
                        SlideChooseView chooseView = (SlideChooseView) holder.itemView;
                        ArrayList<Integer> options = new ArrayList<>(ProxyRotationController.ROTATION_TIMEOUTS);
                        String[] values = new String[options.size()];
                        for (int i = 0; i < options.size(); i++) {
                            values[i] = LocaleController.formatString(R.string.ProxyRotationTimeoutSeconds, options.get(i));
                        }
                        chooseView.setCallback(i -> {
                            SharedConfig.proxyRotationTimeout = i;
                            SharedConfig.saveConfig();
                        });
                        chooseView.setOptions(SharedConfig.proxyRotationTimeout, values);
                    }
                    break;
                }
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
            if (holder.getItemViewType() == VIEW_TYPE_PROXY_DETAIL && !payloads.isEmpty()) {
                TextDetailProxyCell cell = (TextDetailProxyCell) holder.itemView;
                if (payloads.contains(PAYLOAD_SELECTION_CHANGED)) {
                    cell.setItemSelected(selectedItems.contains(proxyList.get(position - proxyStartRow)), true);
                }
                if (payloads.contains(PAYLOAD_SELECTION_MODE_CHANGED)) {
                    cell.setSelectionEnabled(!selectedItems.isEmpty(), true);
                }
            } else if (holder.getItemViewType() == VIEW_TYPE_TEXT_CHECK && payloads.contains(PAYLOAD_CHECKED_CHANGED)) {
                TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                if (position == useProxyRow) {
                    checkCell.setChecked(useProxySettings);
                } else if (position == callsRow) {
                    checkCell.setChecked(useProxyForCalls);
                } else if (position == enablePublicProxyRow) {
                    checkCell.setChecked(NekoConfig.enablePublicProxy.Bool());
                } else if (position == rotationRow) {
                    checkCell.setChecked(SharedConfig.proxyRotationEnabled);
                }
            } else {
                super.onBindViewHolder(holder, position, payloads);
            }
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            if (viewType == VIEW_TYPE_TEXT_CHECK) {
                TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                int position = holder.getAdapterPosition();
                if (position == useProxyRow) {
                    checkCell.setChecked(useProxySettings);
                } else if (position == callsRow) {
                    checkCell.setChecked(useProxyForCalls);
                } else if (position == enablePublicProxyRow) {
                    checkCell.setChecked(NekoConfig.enablePublicProxy.Bool());
                } else if (position == rotationRow) {
                    checkCell.setChecked(SharedConfig.proxyRotationEnabled);
                }
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == useProxyRow || position == rotationRow || position == callsRow || position == enablePublicProxyRow || position == deleteAllRow || position >= proxyStartRow && position < proxyEndRow;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case VIEW_TYPE_SHADOW:
                    view = new ShadowSectionCell(mContext);
                    break;
                case VIEW_TYPE_TEXT_SETTING:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case VIEW_TYPE_HEADER:
                    view = new HeaderCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case VIEW_TYPE_TEXT_CHECK:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case VIEW_TYPE_INFO:
                    view = new TextInfoPrivacyCell(mContext);
                    view.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case VIEW_TYPE_SLIDE_CHOOSER:
                    view = new SlideChooseView(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case VIEW_TYPE_PROXY_DETAIL:
                default:
                    view = new TextDetailProxyCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public long getItemId(int position) {
            // Random stable ids, could be anything non-repeating
            if (position == useProxyShadowRow) {
                return -1;
            } else if (position == proxyShadowRow) {
                return -2;
//            } else if (position == proxyAddRow) {
//                return -3;
            } else if (position == useProxyRow) {
                return -4;
            } else if (position == callsRow) {
                return -5;
            } else if (position == connectionsHeaderRow) {
                return -6;
            } else if (position == deleteAllRow) {
                return -8;
            } else if (position == rotationRow) {
                return -9;
            } else if (position == rotationTimeoutRow) {
                return -10;
            } else if (position == rotationTimeoutInfoRow) {
                return -11;
            } else if (position >= proxyStartRow && position < proxyEndRow) {
                return proxyList.get(position - proxyStartRow).hashCode();
            } else {
                return -7;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == useProxyShadowRow || position == proxyShadowRow) {
                return VIEW_TYPE_SHADOW;
            } else if (position == proxyAddRow || position == deleteAllRow) {
                return VIEW_TYPE_TEXT_SETTING;
            } else if (position == useProxyRow || position == rotationRow || position == callsRow || position == enablePublicProxyRow) {
                return VIEW_TYPE_TEXT_CHECK;
            } else if (position == connectionsHeaderRow) {
                return VIEW_TYPE_HEADER;
            } else if (position == rotationTimeoutRow) {
                return VIEW_TYPE_SLIDE_CHOOSER;
            } else if (position >= proxyStartRow && position < proxyEndRow) {
                return VIEW_TYPE_PROXY_DETAIL;
            } else {
                return VIEW_TYPE_INFO;
            }
        }

    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, TextDetailProxyCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));

        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextDetailProxyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueText6));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGreenText));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText4));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TextDetailProxyCell.class}, new String[]{"checkImageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));

        return themeDescriptions;
    }
}
