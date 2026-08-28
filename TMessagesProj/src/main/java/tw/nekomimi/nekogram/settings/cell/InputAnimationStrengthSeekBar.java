package tw.nekomimi.nekogram.settings.cell;

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

import xyz.nextalone.nagram.NaConfig;

public class InputAnimationStrengthSeekBar extends FrameLayout {
    private final SeekBarView bar;
    private final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final int[] labels = {R.string.InputAnimationWeakest, R.string.InputAnimationWeak,
            R.string.InputAnimationModerate, R.string.InputAnimationStrong, R.string.InputAnimationStrongest};

    public InputAnimationStrengthSeekBar(Context context) {
        super(context);
        setWillNotDraw(false);
        textPaint.setTextSize(AndroidUtilities.dp(14));
        bar = new SeekBarView(context);
        bar.setSeparatorsCount(5);
        bar.setReportChanges(true);
        bar.setDelegate((stop, progress) -> {
            int level = Math.max(0, Math.min(4, Math.round(progress * 4)));
            if (level != level())
                NaConfig.INSTANCE.getInputAnimationStrength().setConfigInt(level);
            if (stop) bar.setProgress(level / 4f);
            updateDescription();
            invalidate();
        });
        addView(bar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 38, Gravity.TOP, 12, 23, 12, 0));
        updateDescription();
    }

    private int level() {
        return Math.max(0, Math.min(4, NaConfig.INSTANCE.getInputAnimationStrength().Int()));
    }

    private void updateDescription() {
        bar.setContentDescription(LocaleController.getString(R.string.InputAnimationStrength)
                + ": " + LocaleController.getString(labels[level()]));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float margin = AndroidUtilities.dp(21);
        textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        canvas.drawText(LocaleController.getString(R.string.InputAnimationStrength), margin, AndroidUtilities.dp(20), textPaint);
        textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
        String value = LocaleController.getString(labels[level()]);
        canvas.drawText(value, getWidth() - margin - textPaint.measureText(value), AndroidUtilities.dp(20), textPaint);
        textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        for (int i = 0; i < 3; i++) {
            String label = LocaleController.getString(labels[i * 2]);
            float x = i == 0 ? margin : i == 1 ? (getWidth() - textPaint.measureText(label)) / 2f
                                        : getWidth() - margin - textPaint.measureText(label);
            canvas.drawText(label, x, AndroidUtilities.dp(80), textPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(92), MeasureSpec.EXACTLY));
        bar.setProgress(level() / 4f);
    }
}
