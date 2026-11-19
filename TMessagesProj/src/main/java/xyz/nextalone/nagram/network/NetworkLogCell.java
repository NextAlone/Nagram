package xyz.nextalone.nagram.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeBind;
import org.telegram.ui.Components.LayoutHelper;

import tw.nekomimi.nekogram.database.NetworkLogItem;

public class NetworkLogCell extends FrameLayout {

    private TextView timestampText;
    private TextView methodText;
    private TextView urlText;
    private TextView statusText;
    private TextView responseTimeText;
    private View divider;

    private Paint dividerPaint;

    private int padding = AndroidUtilities.dp(16);
    private int smallPadding = AndroidUtilities.dp(12);

    public NetworkLogCell(@NonNull Context context) {
        super(context);

        dividerPaint = new Paint();
        ThemeBind.custom(this, 100, Theme.key_divider, null, () -> dividerPaint.getColor(), dividerPaint::setColor);

        setWillNotDraw(false);
        setupLayout();
    }

    private void setupLayout() {
        LinearLayout mainLayout = new LinearLayout(getContext());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(padding, smallPadding, padding, smallPadding);
        ThemeBind.backgroundColor(mainLayout, Theme.key_windowBackgroundWhite);

        LinearLayout topRow = new LinearLayout(getContext());
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        timestampText = new TextView(getContext());
        timestampText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        ThemeBind.textColor(timestampText, Theme.key_windowBackgroundWhiteGrayText3);
        topRow.addView(timestampText);

        methodText = new TextView(getContext());
        methodText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        methodText.setTypeface(Typeface.DEFAULT_BOLD);
        methodText.setPadding(AndroidUtilities.dp(8), 0, 0, 0);
        topRow.addView(methodText);

        statusText = new TextView(getContext());
        statusText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        statusText.setTypeface(Typeface.DEFAULT_BOLD);
        statusText.setPadding(AndroidUtilities.dp(8), 0, 0, 0);
        topRow.addView(statusText, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1.0f, Gravity.RIGHT | Gravity.CENTER_VERTICAL));

        responseTimeText = new TextView(getContext());
        responseTimeText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        ThemeBind.textColor(responseTimeText, Theme.key_windowBackgroundWhiteGrayText3);
        responseTimeText.setPadding(AndroidUtilities.dp(8), 0, 0, 0);
        topRow.addView(responseTimeText);

        mainLayout.addView(topRow);

        urlText = new TextView(getContext());
        urlText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        ThemeBind.textColor(urlText, Theme.key_windowBackgroundWhiteBlackText);
        urlText.setMaxLines(2);
        urlText.setEllipsize(TextUtils.TruncateAt.END);
        urlText.setPadding(0, AndroidUtilities.dp(4), 0, 0);
        mainLayout.addView(urlText);

        divider = new View(getContext());
        ThemeBind.backgroundColor(divider, Theme.key_divider);
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(LayoutHelper.MATCH_PARENT, 1);
        dividerParams.topMargin = AndroidUtilities.dp(8);
        mainLayout.addView(divider, dividerParams);

        addView(mainLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.START));
    }

    @SuppressLint("SetTextI18n")
    public void bind(NetworkLogItem item) {
        timestampText.setText("[" + NetworkLoggingInterceptor.formatTimestamp(item.timestamp) + "]");

        methodText.setText(item.method);
        methodText.setTextColor(NetworkLoggingInterceptor.getMethodColor(item.method));

        urlText.setText(item.url);

        if (item.statusCode > 0) {
            statusText.setText(" " + item.statusCode + " ");
            statusText.setTextColor(NetworkLoggingInterceptor.getStatusCodeColor(item.statusCode));
            statusText.setBackground(null);
        } else {
            statusText.setText(" -- ");
            ThemeBind.textColor(statusText, Theme.key_windowBackgroundWhiteGrayText3);
        }

        responseTimeText.setText(NetworkLoggingInterceptor.formatResponseTime(item.responseTime));

        setContentDescription(item.method + " " + item.url + " " + (item.statusCode > 0 ? item.statusCode : "failed"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, getHeight() - 1, getWidth(), getHeight(), dividerPaint);
        super.onDraw(canvas);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
    }
}
