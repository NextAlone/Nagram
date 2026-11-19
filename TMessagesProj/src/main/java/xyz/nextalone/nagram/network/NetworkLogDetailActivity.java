package xyz.nextalone.nagram.network;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeBind;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.List;

import tw.nekomimi.nekogram.database.NetworkLogItem;

public class NetworkLogDetailActivity extends BaseFragment {

    private NetworkLogItem logItem;
    private RecyclerListView listView;
    private DetailAdapter adapter;

    public NetworkLogDetailActivity(NetworkLogItem item) {
        this.logItem = item;
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        return true;
    }

    @Override
    protected boolean useSimpleActionBar() {
        return true;
    }

    @Override
    public SimpleActionBar createSimpleActionBar(Context context) {
        simpleActionBar = super.createSimpleActionBar(context);
        simpleActionBar.setTitle(LocaleController.getString(R.string.NetworkLog));
        simpleActionBar.setBackButtonImage(R.drawable.ic_ab_back);
        simpleActionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });
        return simpleActionBar;
    }

    @Override
    public View createView(Context context) {
        setHasOwnBackground(true);

        fragmentView = new FrameLayout(context) {
            @Override
            protected void dispatchDraw(@NonNull Canvas canvas) {
                super.dispatchDraw(canvas);
                glassEngine.invalidate();
            }
        };
        ThemeBind.backgroundColor(fragmentView, Theme.key_windowBackgroundGray, getResourceProvider());

        listView = new RecyclerListView(context);
        listView.setSections();
        listView.setSectionsDrawBackground(true);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        listView.setHasFixedSize(false);
        listView.setNestedScrollingEnabled(true);

        adapter = new DetailAdapter(context);
        listView.setAdapter(adapter);

        ((FrameLayout) fragmentView).addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP));

        return fragmentView;
    }

    @Override
    protected RecyclerListView getListViewForSimpleGlass() {
        return listView;
    }

    private class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailViewHolder> {

        private static final int TYPE_INFO = 0;
        private static final int TYPE_CONTENT = 1;
        private static final int TYPE_ERROR = 2;
        private static final int TYPE_DIVIDER = 3;

        private final List<DetailItem> items = new ArrayList<>();
        private Context context;

        public DetailAdapter(Context context) {
            this.context = context;
            buildItems();
        }

        private void buildItems() {
            items.add(new DetailItem(TYPE_INFO, 0, null));

            if (!TextUtils.isEmpty(logItem.requestParams)) {
                items.add(new DetailItem(TYPE_DIVIDER));
                items.add(new DetailItem(TYPE_CONTENT, R.string.RequestParams, logItem.requestParams));
            }
            if (!TextUtils.isEmpty(logItem.requestHeaders)) {
                items.add(new DetailItem(TYPE_DIVIDER));
                items.add(new DetailItem(TYPE_CONTENT, R.string.RequestHeaders, logItem.requestHeaders));
            }
            if (!TextUtils.isEmpty(logItem.requestBody)) {
                items.add(new DetailItem(TYPE_DIVIDER));
                items.add(new DetailItem(TYPE_CONTENT, R.string.RequestBody, logItem.requestBody));
            }
            if (!TextUtils.isEmpty(logItem.responseHeaders)) {
                items.add(new DetailItem(TYPE_DIVIDER));
                items.add(new DetailItem(TYPE_CONTENT, R.string.ResponseHeaders, logItem.responseHeaders));
            }
            if (!TextUtils.isEmpty(logItem.responseBody)) {
                items.add(new DetailItem(TYPE_DIVIDER));
                items.add(new DetailItem(TYPE_CONTENT, R.string.ResponseBody, logItem.responseBody));
            }
            if (!TextUtils.isEmpty(logItem.errorMessage)) {
                items.add(new DetailItem(TYPE_DIVIDER));
                items.add(new DetailItem(TYPE_ERROR, R.string.Error, logItem.errorMessage));
            }
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position).type;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @NonNull
        @Override
        public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_INFO) {
                InfoView view = new InfoView(context);
                view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                return new DetailViewHolder(view, viewType);
            } else if (viewType == TYPE_ERROR) {
                ContentView view = new ContentView(context, true);
                view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                return new DetailViewHolder(view, viewType);
            } else if (viewType == TYPE_DIVIDER) {
                ShadowSectionCell view = new ShadowSectionCell(context);
                return new DetailViewHolder(view, viewType);
            } else {
                ContentView view = new ContentView(context, false);
                view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                return new DetailViewHolder(view, viewType);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
            DetailItem item = items.get(position);
            if (holder.viewType == TYPE_INFO && holder.itemView instanceof InfoView) {
                ((InfoView) holder.itemView).bind(logItem);
            } else if (holder.itemView instanceof ContentView) {
                ((ContentView) holder.itemView).bind(item.titleRes, item.content);
            }
        }

        class DetailViewHolder extends RecyclerView.ViewHolder {
            int viewType;

            public DetailViewHolder(@NonNull View itemView, int viewType) {
                super(itemView);
                this.viewType = viewType;
            }
        }

        class DetailItem {
            int type;
            int titleRes;
            String content;

            public DetailItem(int type) {
                this.type = type;
            }

            public DetailItem(int type, int titleRes, String content) {
                this.type = type;
                this.titleRes = titleRes;
                this.content = content;
            }
        }

        class InfoView extends LinearLayout {
            public InfoView(Context context) {
                super(context);
                setOrientation(VERTICAL);
                ThemeBind.backgroundColor(this, Theme.key_windowBackgroundWhite, getResourceProvider());
                setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16));
            }

            public void bind(NetworkLogItem item) {
                removeAllViews();
                addInfoRow(R.string.Method, item.method);
                addUrlRow(item.url);
                String statusCodeStr = item.statusCode > 0 ? String.valueOf(item.statusCode) : LocaleController.getString(R.string.Error);
                addInfoRow(R.string.StatusCode, statusCodeStr);
                addInfoRow(R.string.ResponseTime, NetworkLoggingInterceptor.formatResponseTime(item.responseTime));
                addInfoRow(R.string.Timestamp, NetworkLoggingInterceptor.formatTimestamp(item.timestamp));
            }

            private void addInfoRow(int titleRes, String value) {
                LinearLayout row = new LinearLayout(getContext());
                row.setOrientation(HORIZONTAL);
                row.setPadding(0, AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8));

                TextView labelView = new TextView(getContext());
                labelView.setText(titleRes);
                labelView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                labelView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                ThemeBind.textColor(labelView, Theme.key_windowBackgroundWhiteValueText, getResourceProvider());
                row.addView(labelView);

                TextView valueView = new TextView(getContext());
                valueView.setText(value);
                valueView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(0, LayoutHelper.WRAP_CONTENT, 1.0f);
                valueParams.gravity = Gravity.END;
                valueView.setLayoutParams(valueParams);
                valueView.setGravity(Gravity.END);

                if (titleRes == R.string.Method) {
                    valueView.setTextColor(NetworkLoggingInterceptor.getMethodColor(logItem.method));
                } else if (titleRes == R.string.StatusCode) {
                    if (logItem.statusCode > 0) {
                        valueView.setTextColor(NetworkLoggingInterceptor.getStatusCodeColor(logItem.statusCode));
                    } else {
                        ThemeBind.textColor(valueView, Theme.key_text_RedBold, getResourceProvider());
                    }
                } else {
                    ThemeBind.textColor(valueView, Theme.key_windowBackgroundWhiteBlackText, getResourceProvider());
                }

                row.addView(valueView);
                addView(row);
            }

            private void addUrlRow(String url) {
                LinearLayout row = new LinearLayout(getContext());
                row.setOrientation(VERTICAL);
                row.setPadding(0, AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8));

                TextView labelView = new TextView(getContext());
                labelView.setText("URL");
                labelView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                labelView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                ThemeBind.textColor(labelView, Theme.key_windowBackgroundWhiteValueText, getResourceProvider());
                row.addView(labelView);

                TextView urlView = new TextView(getContext());
                urlView.setText(url);
                urlView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                ThemeBind.textColor(urlView, Theme.key_windowBackgroundWhiteBlackText, getResourceProvider());
                urlView.setTextIsSelectable(true);
                LinearLayout.LayoutParams urlParams = new LinearLayout.LayoutParams(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
                urlParams.topMargin = AndroidUtilities.dp(4);
                urlView.setLayoutParams(urlParams);
                row.addView(urlView);

                addView(row);
            }
        }

        class ContentView extends LinearLayout {
            private final TextView titleView;
            private final TextView contentView;

            public ContentView(Context context, boolean isError) {
                super(context);
                setOrientation(VERTICAL);
                ThemeBind.backgroundColor(this, Theme.key_windowBackgroundWhite, getResourceProvider());
                setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16));

                titleView = new TextView(context);
                titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                titleView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                if (isError) {
                    ThemeBind.textColor(titleView, Theme.key_text_RedBold, getResourceProvider());
                } else {
                    ThemeBind.textColor(titleView, Theme.key_windowBackgroundWhiteBlackText, getResourceProvider());
                }
                addView(titleView);

                contentView = new TextView(context);
                contentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                if (isError) {
                    ThemeBind.textColor(contentView, Theme.key_text_RedBold, getResourceProvider());
                } else {
                    ThemeBind.textColor(contentView, Theme.key_windowBackgroundWhiteGrayText2, getResourceProvider());
                }
                contentView.setTextIsSelectable(true);
                LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
                contentParams.topMargin = AndroidUtilities.dp(8);
                contentView.setLayoutParams(contentParams);
                addView(contentView);
            }

            public void bind(int titleRes, String content) {
                titleView.setText(titleRes);
                contentView.setText(content);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean isSupportEdgeToEdge() {
        return true;
    }
}
