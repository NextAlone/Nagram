package tw.nekomimi.nekogram.settings.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.Gravity;
import android.widget.FrameLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;

import tw.nekomimi.nekogram.config.CellGroup;
import xyz.nextalone.nagram.NaConfig;

@SuppressLint("ViewConstructor")
public class AvatarCornersSeekBar extends FrameLayout {
    protected CellGroup cellGroup;
    private final SeekBarView bar;
    private final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    public AvatarCornersSeekBar(Context context, CellGroup cellGroup) {
        super(context);
        setWillNotDraw(false);
        textPaint.setTextSize(AndroidUtilities.dp(14));
        bar = new SeekBarView(context);
        bar.setReportChanges(true);
        bar.setDelegate((stop, progress) -> {
            if (stop) {
                int value = Math.round(progress * 100f);
                if (value == NaConfig.INSTANCE.getAvatarCorners().Int()) {
                    return;
                }
                NaConfig.INSTANCE.getAvatarCorners().setConfigInt(value);
                if (cellGroup != null) {
                    cellGroup.runCallback(NaConfig.INSTANCE.getAvatarCorners().getKey(), value);
                }
            }
        });
        addView(bar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 38, Gravity.LEFT | Gravity.TOP, 12, 23, 52, 5));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        canvas.drawText(LocaleController.getString(R.string.AvatarCorners), AndroidUtilities.dp(21), AndroidUtilities.dp(20), textPaint);
        textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
        String value = NaConfig.INSTANCE.getAvatarCorners().Int() + "%";
        canvas.drawText(value, getMeasuredWidth() - AndroidUtilities.dp(47), AndroidUtilities.dp(47), textPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(66), MeasureSpec.EXACTLY));
        float value = NaConfig.INSTANCE.getAvatarCorners().Int() / 100f;
        bar.setProgress(value);
    }
}
