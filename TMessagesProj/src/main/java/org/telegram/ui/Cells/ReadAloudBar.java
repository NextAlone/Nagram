package org.telegram.ui.Cells;

import static org.telegram.messenger.AndroidUtilities.dp;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.LongSparseArray;

import org.telegram.messenger.Emoji;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.utils.tlutils.TLKeyboardHelper;
import org.telegram.tgnet.tl.TL_keyboard;
import org.telegram.ui.Components.Text;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Smooth progress for a Read Aloud player keyboard (a message whose first rows are a time button
 * "🔊 0:07 / 4:36" and a bar of 16 emoji cells, both with callback data "tts:pos").
 * The sender edits that keyboard only every few seconds, so the cell moves the time and a solid bar
 * on its own between edits, counting from the moment it saw the last label.
 */
class ReadAloudBar {

    private static final byte[] POS = "tts:pos".getBytes(StandardCharsets.UTF_8);
    private static final Pattern TIME = Pattern.compile("^(\\S+)\\s+(\\d+):(\\d\\d)\\s*/\\s*(\\d+):(\\d\\d)(\\+?)$");
    // the sender edits about every 8 s; past this the cell stops guessing and waits for the next edit
    private static final long MAX_AHEAD_MS = 12_000;
    private static final int COLOR_PLAYING = 0xFF55ACEE;
    private static final int COLOR_PAUSED = 0xFFF4900C;
    private static final int COLOR_REST = 0x66000000;

    private static class Anchor {
        String label;
        long seenAt;
    }

    private static final LongSparseArray<Anchor> anchors = new LongSparseArray<>();

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF bar = new RectF();
    private Text timeText;
    private Text emptyText;
    private String timeTextValue;

    BotButton timeButton;
    BotButton barButton;
    private boolean playing;
    private float fraction;
    private String label;

    private static boolean isPos(BotButton b) {
        if (b == null || b.button == null) {
            return false;
        }
        TL_keyboard.TL_inlineButtonTypeCallback cb = TLKeyboardHelper.getType(b.button, TL_keyboard.TL_inlineButtonTypeCallback.class);
        return cb != null && cb.data != null && java.util.Arrays.equals(cb.data, POS);
    }

    private static String mmss(int s) {
        return (s / 60) + ":" + (s % 60 < 10 ? "0" : "") + (s % 60);
    }

    /** Finds the player rows and works out where the read is right now. False when this is not a player. */
    boolean update(MessageObject message, ArrayList<BotButton> buttons) {
        timeButton = barButton = null;
        if (message == null || buttons == null) {
            return false;
        }
        for (int i = 0; i < buttons.size(); i++) {
            BotButton b = buttons.get(i);
            if (!isPos(b)) {
                continue;
            }
            if (timeButton == null) {
                timeButton = b;
            } else {
                barButton = b;
                break;
            }
        }
        if (timeButton == null || barButton == null || timeButton.button == null) {
            return false;
        }
        String text = timeButton.button.text == null ? "" : timeButton.button.text.trim();
        Matcher m = TIME.matcher(text);
        if (!m.matches()) {
            // "🔊 Reading…", "✅ Done", "⏹ Stopped": nothing to move, draw as sent
            timeButton = barButton = null;
            return false;
        }
        String head = m.group(1);
        int pos = Integer.parseInt(m.group(2)) * 60 + Integer.parseInt(m.group(3));
        int dur = Integer.parseInt(m.group(4)) * 60 + Integer.parseInt(m.group(5));
        boolean lengthKnown = TextUtils.isEmpty(m.group(6));
        playing = head.startsWith("🔊");

        long key = message.getDialogId() * 31 + message.getId();
        long now = SystemClock.elapsedRealtime();
        Anchor a = anchors.get(key);
        if (a == null || !TextUtils.equals(a.label, text)) {
            if (a == null) {
                a = new Anchor();
                anchors.put(key, a);
                if (anchors.size() > 64) {
                    anchors.removeAt(0);
                }
            }
            a.label = text;
            a.seenAt = now;
        }
        float ahead = playing ? Math.min(MAX_AHEAD_MS, now - a.seenAt) / 1000f : 0;
        float at = dur > 0 ? Math.min(dur, pos + ahead) : pos + ahead;
        fraction = dur > 0 ? Math.max(0, Math.min(1, at / dur)) : 0;
        label = head + " " + mmss((int) at) + " / " + mmss(dur) + (lengthKnown ? "" : "+");
        return true;
    }

    /** True while the read is moving, so the cell keeps redrawing. */
    boolean isMoving() {
        return timeButton != null && playing && fraction < 1;
    }

    boolean handles(BotButton b) {
        return b != null && (b == timeButton || b == barButton);
    }

    /** Draws the time button with the live time, or the bar button as one solid bar. */
    void draw(Canvas canvas, RectF rect, BotButton b, TextPaint textPaint, org.telegram.ui.ActionBar.Theme.ResourcesProvider resourcesProvider, boolean drawProgress) {
        if (b == timeButton) {
            if (!TextUtils.equals(timeTextValue, label)) {
                timeTextValue = label;
                timeText = new Text(Emoji.replaceEmoji(label, textPaint.getFontMetricsInt(), false), textPaint);
            }
            Text sent = b.title;
            b.title = timeText;
            b.draw(canvas, rect, drawProgress, false, resourcesProvider);
            b.title = sent;
            return;
        }
        Text sent = b.title;
        if (emptyText == null) {
            emptyText = new Text("", textPaint);
        }
        b.title = emptyText;
        b.draw(canvas, rect, false, false, resourcesProvider);
        b.title = sent;

        float h = dp(10);
        float left = rect.left + dp(12), right = rect.right - dp(12);
        float cy = rect.centerY();
        bar.set(left, cy - h / 2, right, cy + h / 2);
        paint.setColor(COLOR_REST);
        canvas.drawRoundRect(bar, h / 2, h / 2, paint);
        if (fraction > 0) {
            bar.right = left + Math.max(h, (right - left) * fraction);
            paint.setColor(playing ? COLOR_PLAYING : COLOR_PAUSED);
            canvas.drawRoundRect(bar, h / 2, h / 2, paint);
        }
    }
}
