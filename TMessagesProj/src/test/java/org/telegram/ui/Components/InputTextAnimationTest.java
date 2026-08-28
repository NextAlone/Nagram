package org.telegram.ui.Components;

import static org.junit.Assert.*;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;

import java.lang.reflect.Field;
import java.util.List;

import xyz.nextalone.nagram.NaConfig;
import xyz.nextalone.nagram.ui.Components.InputTextAnimation;

@RunWith(RobolectricTestRunner.class)
@Config(application = android.app.Application.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class InputTextAnimationTest {
    private EditTextEffects view;
    private InputTextAnimation animation;

    @Before
    public void setup() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().get();
        ApplicationLoader.applicationContext = activity.getApplicationContext();
        AndroidUtilities.density = 1;
        NaConfig.INSTANCE.getInputTextAnimations().value = false;
        view = new PlainInput(activity);
        activity.setContentView(view);
        view.requestFocus();
        layout();
        Field field = EditTextEffects.class.getDeclaredField("inputTextAnimation");
        field.setAccessible(true);
        animation = (InputTextAnimation) field.get(view);
    }

    @After
    public void cleanup() {
        NaConfig.INSTANCE.getInputTextAnimations().value = false;
        if (animation != null) animation.clear();
    }

    @Test
    public void disabledDoesNotAllocateOrChangeText() throws Exception {
        assertEquals(false, NaConfig.INSTANCE.getInputTextAnimations().defaultValue);
        view.getText().append("中文 hello");
        draw();
        assertNull(field("insertion"));
        assertEquals("中文 hello", view.getText().toString());
        assertTrue(((List<?>) field("particles")).isEmpty());
    }

    @Test
    public void insertionAndDeletionPreserveMultilineUnicode() throws Exception {
        NaConfig.INSTANCE.getInputTextAnimations().value = true;
        view.getText().append("中文\nمرحبا 👋");
        draw();
        assertNotNull(field("insertion"));
        assertEquals("中文\nمرحبا 👋", view.getText().toString());
        view.getText().delete(0, 2);
        draw();
        assertFalse(((List<?>) field("particles")).isEmpty());
        assertEquals("\nمرحبا 👋", view.getText().toString());
        NaConfig.INSTANCE.getInputTextAnimations().value = false;
        draw();
        assertNull(field("insertion"));
        assertTrue(((List<?>) field("particles")).isEmpty());
    }

    @Test
    public void compositionAnimatesButPasswordAndStyledTextAreNotCaptured() throws Exception {
        NaConfig.INSTANCE.getInputTextAnimations().value = true;
        view.setTransformationMethod(PasswordTransformationMethod.getInstance());
        view.getText().append("secret");
        draw();
        assertNull(field("insertion"));
        view.setTransformationMethod(null);
        BaseInputConnection.setComposingSpans(view.getText());
        view.getText().append("候选");
        draw();
        assertNotNull(field("insertion"));
        BaseInputConnection.removeComposingSpans(view.getText());
        view.getText().setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.getText().delete(0, 1);
        draw();
        assertTrue(((List<?>) field("particles")).isEmpty());
    }

    private Object field(String name) throws Exception {
        Field field = InputTextAnimation.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(animation);
    }

    private void layout() {
        view.measure(View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(160, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, 400, 160);
    }

    private void draw() {
        layout();
        Bitmap bitmap = Bitmap.createBitmap(400, 160, Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        bitmap.recycle();
    }

    // Keep these tests scoped to editing/layout; Telegram emoji/quote rendering needs JNI.
    private static class PlainInput extends EditTextEffects {
        PlainInput(Context context) { super(context); }
        @Override public void updateAnimatedEmoji(boolean force) { }
        @Override public void invalidateQuotes(boolean force) { }
        @Override public void invalidateEffects() { }
    }
}
