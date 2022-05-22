package top.qwq2333.nullgram.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextCheckbox2Cell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextRadioCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.URLSpanNoUnderline;

import java.util.HashMap;
import java.util.Locale;

import top.qwq2333.nullgram.utils.MessageUtils;


public class DatacenterActivity extends BaseFragment {
    private final int dcToHighlight;

    private int headerRow;
    private int header2Row;

    private int datacentersRow;
    private int datacenters2Row;

    public DatacenterActivity(int dcToHighlight) {
        this.dcToHighlight = dcToHighlight;
    }


    protected BlurredRecyclerView listView;
    protected BaseListAdapter listAdapter;
    protected LinearLayoutManager layoutManager;

    protected int rowCount;
    protected HashMap<String, Integer> rowMap = new HashMap<>(20);
    protected HashMap<Integer, String> rowMapReverse = new HashMap<>(20);


    @Override
    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    protected void onItemClick(View view, int position, float x, float y) {
        if (position > datacentersRow && position < datacenters2Row) {
            var datacenterInfo = MessageUtils.datacenterInfos.get(position - datacentersRow - 1);
            if (datacenterInfo.checking) {
                return;
            }
            checkDatacenter(datacenterInfo, true);
        }
    }

    private class ListAdapter extends BaseListAdapter {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 1: {
                    if (position == datacenters2Row) {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    } else {
                        holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
                }
                case 4: {
                    HeaderCell cell = (HeaderCell) holder.itemView;
                    cell.setText(LocaleController.getString("DatacenterStatus", R.string.DatacenterStatus));
                    break;
                }
                case Integer.MAX_VALUE: {
                    DatacenterCell cell = (DatacenterCell) holder.itemView;
                    cell.setDC(MessageUtils.datacenterInfos.get(position - datacentersRow - 1), position + 1 != datacenters2Row);
                    break;
                }
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == Integer.MAX_VALUE - 1) {
                var headerCell = new DatacenterHeaderCell(mContext);
                headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                headerCell.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
                return new RecyclerListView.Holder(headerCell);
            } else if (viewType == Integer.MAX_VALUE) {
                var dcCell = new DatacenterCell(mContext);
                dcCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                dcCell.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
                return new RecyclerListView.Holder(dcCell);
            } else {
                return super.onCreateViewHolder(parent, viewType);
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            var position = holder.getAdapterPosition();
            if (position > datacentersRow && position < datacenters2Row) {
                var datacenterInfo = MessageUtils.datacenterInfos.get(position - datacentersRow - 1);
                return !datacenterInfo.checking;
            } else {
                return super.isEnabled(holder);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == header2Row || position == datacenters2Row) {
                return 1;
            } else if (position == datacentersRow) {
                return 4;
            } else if (position == headerRow) {
                return Integer.MAX_VALUE - 1;
            }
            return Integer.MAX_VALUE;
        }
    }

    private class DatacenterHeaderCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {

        BackupImageView imageView;
        TextView textView;

        public DatacenterHeaderCell(@NonNull Context context) {
            super(context);
            imageView = new BackupImageView(context);
            addView(imageView, LayoutHelper.createFrame(120, 120, Gravity.CENTER_HORIZONTAL, 0, 16, 0, 0));

            imageView.setOnClickListener(view -> {
                if (imageView.getImageReceiver().getLottieAnimation() != null && !imageView.getImageReceiver().getLottieAnimation().isRunning()) {
                    imageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                    imageView.getImageReceiver().getLottieAnimation().restart();
                }
            });
            imageView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);

            textView = new TextView(context);
            addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 36, 152, 36, 0));
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            textView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
            textView.setHighlightColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection));
            textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
            textView.setText(getSpannedString("DatacenterStatusAbout", R.string.DatacenterStatusAbout, "https://core.telegram.org/api/datacenter"));

            setSticker();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(196), MeasureSpec.EXACTLY));
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            setSticker();
            NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
        }

        @Override
        public void didReceivedNotification(int id, int account, Object... args) {
            if (id == NotificationCenter.diceStickersDidLoad) {
                String name = (String) args[0];
                if (AndroidUtilities.STICKERS_PLACEHOLDER_PACK_NAME.equals(name)) {
                    setSticker();
                }
            }
        }

        private void setSticker() {
            TLRPC.Document document = null;
            TLRPC.TL_messages_stickerSet set;

            set = MediaDataController.getInstance(currentAccount).getStickerSetByName(AndroidUtilities.STICKERS_PLACEHOLDER_PACK_NAME);
            if (set == null) {
                set = MediaDataController.getInstance(currentAccount).getStickerSetByEmojiOrName(AndroidUtilities.STICKERS_PLACEHOLDER_PACK_NAME);
            }
            if (set != null && set.documents.size() >= 3) {
                document = set.documents.get(2);
            }

            SvgHelper.SvgDrawable svgThumb = null;
            if (document != null) {
                svgThumb = DocumentObject.getSvgThumb(document.thumbs, Theme.key_emptyListPlaceholder, 0.2f);
            }
            if (svgThumb != null) {
                svgThumb.overrideWidthAndHeight(512, 512);
            }

            if (document != null) {
                ImageLocation imageLocation = ImageLocation.getForDocument(document);
                imageView.setImage(imageLocation, "130_130", "tgs", svgThumb, set);
                imageView.getImageReceiver().setAutoRepeat(2);
            } else {
                MediaDataController.getInstance(currentAccount).loadStickersByEmojiOrName(AndroidUtilities.STICKERS_PLACEHOLDER_PACK_NAME, false, set == null);
            }
        }
    }

    public static class DatacenterCell extends FrameLayout {

        private final TextView textView;
        private final TextView valueTextView;
        private MessageUtils.DatacenterInfo currentInfo;

        private boolean needDivider;

        public DatacenterCell(Context context) {
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
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
        }

        public void setDC(MessageUtils.DatacenterInfo info, boolean divider) {
            textView.setText(String.format(Locale.US, "DC%d %s, %s", info.id, MessageUtils.getDCName(info.id), MessageUtils.getDCLocation(info.id)));
            currentInfo = info;
            needDivider = divider;
            setWillNotDraw(!needDivider);
            updateStatus();
        }

        public void updateStatus() {
            String colorKey;
            if (currentInfo.checking) {
                valueTextView.setText(LocaleController.getString("Checking", R.string.Checking));
                colorKey = Theme.key_windowBackgroundWhiteGrayText2;
            } else if (currentInfo.available) {
                if (currentInfo.ping >= 1000) {
                    valueTextView.setText(String.format("%s, %s", LocaleController.getString("SpeedSlow", R.string.SpeedSlow), LocaleController.formatString("Ping", R.string.Ping, currentInfo.ping)));
                    colorKey = Theme.key_windowBackgroundWhiteRedText4;
                } else if (currentInfo.ping != 0) {
                    valueTextView.setText(String.format("%s, %s", LocaleController.getString("Available", R.string.Available), LocaleController.formatString("Ping", R.string.Ping, currentInfo.ping)));
                    colorKey = Theme.key_windowBackgroundWhiteGreenText;
                } else {
                    valueTextView.setText(LocaleController.getString("Available", R.string.Available));
                    colorKey = Theme.key_windowBackgroundWhiteGreenText;
                }
            } else {
                valueTextView.setText(LocaleController.getString("Unavailable", R.string.Unavailable));
                colorKey = Theme.key_windowBackgroundWhiteRedText4;
            }
            valueTextView.setTag(colorKey);
            valueTextView.setTextColor(Theme.getColor(colorKey));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0 : AndroidUtilities.dp(20), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
        }
    }


    @Override
    public View createView(Context context) {
        fragmentView = new BlurContentView(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        SizeNotifierFrameLayout frameLayout = (SizeNotifierFrameLayout) fragmentView;

        actionBar.setDrawBlurBackground(frameLayout);

        listView = new BlurredRecyclerView(context);
        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                frameLayout.invalidateBlur();
            }
        });
        listView.additionalClipBottom = AndroidUtilities.dp(200);
        listView.setLayoutManager(layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        //noinspection ConstantConditions
        ((DefaultItemAnimator) listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listAdapter = createAdapter(context);

        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this::onItemClick);
        listView.setOnItemLongClickListener((view, position, x, y) -> {
            if (onItemLongClick(view, position, x, y)) {
                return true;
            }
            var holder = listView.findViewHolderForAdapterPosition(position);
            var key = getKey();

            return false;
        });
        if (dcToHighlight != 0) {
            listView.highlightRow(() -> {
                layoutManager.scrollToPositionWithOffset(datacentersRow + dcToHighlight, AndroidUtilities.dp(60));
                return datacentersRow + dcToHighlight;
            });
        }
        return fragmentView;
    }

    protected BaseListAdapter createAdapter(Context context) {
        return new ListAdapter(context);
    }


    @Override
    protected ActionBar createActionBar(Context context) {
        ActionBar actionBar;
        if (!hasWhiteActionBar()) {
            actionBar = super.createActionBar(context);
        } else {
            actionBar = new ActionBar(context);
            actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), false);
            actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarWhiteSelector), false);
            actionBar.setTitleColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            actionBar.setCastShadows(false);
        }
        actionBar.setTitle(getActionBarTitle());
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }
        return actionBar;
    }

    protected String getKey() {
        return null;
    }


    protected boolean onItemLongClick(View view, int position, float x, float y) {
        return false;
    }


    protected void showRestartBulletin() {
        BulletinFactory.of(this).createErrorBulletin(LocaleController.formatString("RestartAppToTakeEffect", R.string.RestartAppToTakeEffect)).show();
    }

    private class BlurContentView extends SizeNotifierFrameLayout {

        public BlurContentView(Context context) {
            super(context);
            needBlur = hasWhiteActionBar();
            blurBehindViews.add(this);
        }

        @Override
        protected void drawList(Canvas blurCanvas, boolean top) {
            for (int j = 0; j < listView.getChildCount(); j++) {
                View child = listView.getChildAt(j);
                if (child.getY() < listView.blurTopPadding + AndroidUtilities.dp(100)) {
                    int restore = blurCanvas.save();
                    blurCanvas.translate(getX() + child.getX(), getY() + listView.getY() + child.getY());
                    child.draw(blurCanvas);
                    blurCanvas.restoreToCount(restore);
                }
            }
        }

        public Paint blurScrimPaint = new Paint();
        Rect rectTmp = new Rect();

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (hasWhiteActionBar() && listView.canScrollVertically(-1)) {
                rectTmp.set(0, 0, getMeasuredWidth(), 1);
                blurScrimPaint.setColor(Theme.getColor(Theme.key_divider));
                drawBlurRect(canvas, getY(), rectTmp, blurScrimPaint, true);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    protected boolean hasWhiteActionBar() {
        return false;
    }

    protected CharSequence getSpannedString(String key, int id, String url) {
        var text = LocaleController.getString(key, id);
        var builder = new SpannableStringBuilder(text);
        int index1 = text.indexOf("**");
        int index2 = text.lastIndexOf("**");
        if (index1 >= 0 && index2 >= 0 && index1 != index2) {
            builder.replace(index2, index2 + 2, "");
            builder.replace(index1, index1 + 2, "");
            builder.setSpan(new URLSpanNoUnderline(url), index1, index2 - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    @Override
    public boolean isLightStatusBar() {
        if (!hasWhiteActionBar()) return super.isLightStatusBar();
        int color = Theme.getColor(Theme.key_windowBackgroundWhite, null, true);
        return ColorUtils.calculateLuminance(color) > 0.7f;
    }

    public void scrollToRow(String key, Runnable unknown) {
        if (rowMap.containsKey(key)) {
            listView.highlightRow(() -> {
                //noinspection ConstantConditions
                int position = rowMap.get(key);
                layoutManager.scrollToPositionWithOffset(position, AndroidUtilities.dp(60));
                return position;
            });
        } else {
            unknown.run();
        }
    }

    protected void updateRows() {
        rowCount = 0;
        rowMap.clear();

        headerRow = rowCount++;
        header2Row = rowCount++;

        datacentersRow = rowCount++;
        rowCount += 5;
        datacenters2Row = rowCount++;

        for (var datacenterInfo : MessageUtils.datacenterInfos) {
            checkDatacenter(datacenterInfo, false);
        }
    }

    private void checkDatacenter(MessageUtils.DatacenterInfo datacenterInfo, boolean force) {
        if (datacenterInfo.checking) {
            return;
        }
        if (!force && SystemClock.elapsedRealtime() - datacenterInfo.availableCheckTime < 2 * 60 * 1000) {
            return;
        }
        datacenterInfo.checking = true;
        int position = datacentersRow + MessageUtils.datacenterInfos.indexOf(datacenterInfo) + 1;
        if (force) {
            listAdapter.notifyItemChanged(position);
        }
        datacenterInfo.pingId = ConnectionsManager.getInstance(currentAccount).checkProxy("ping-test", datacenterInfo.id, null, null, null, time -> AndroidUtilities.runOnUIThread(() -> {
            datacenterInfo.availableCheckTime = SystemClock.elapsedRealtime();
            datacenterInfo.checking = false;
            if (time == -1) {
                datacenterInfo.available = false;
                datacenterInfo.ping = 0;
            } else {
                datacenterInfo.ping = time;
                datacenterInfo.available = true;
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxyCheckDone, null, datacenterInfo.id);
        }));
    }

    protected String getActionBarTitle() {
        return LocaleController.getString("DatacenterStatus", R.string.DatacenterStatus);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.proxyCheckDone) {
            if (listAdapter != null && args.length > 1) {
                listAdapter.notifyItemChanged(datacentersRow + ((int) args[1]));
            }
        }
    }


    protected abstract class BaseListAdapter extends RecyclerListView.SelectionAdapter {

        protected final Context mContext;

        public BaseListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 2 || type == 3 || type == 5 || type == 6 || type == 8 | type == 9 || type == 10 || type == 11 || type == 12;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 1:
                    view = new ShadowSectionCell(mContext);
                    break;
                case 2:
                    view = new TextSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new TextCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new HeaderCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                    view = new NotificationsCheckCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 6:
                    view = new TextDetailSettingsCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 7:
                    view = new TextInfoPrivacyCell(mContext);
                    view.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 8:
                    view = new TextCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 9:
                    view = new TextCheckbox2Cell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 10:
                    view = new TextRadioCell(mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            //noinspection ConstantConditions
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }
    }
}
