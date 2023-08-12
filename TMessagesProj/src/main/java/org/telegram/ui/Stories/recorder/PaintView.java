package org.telegram.ui.Stories.recorder;

import static org.telegram.messenger.AndroidUtilities.lerp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Looper;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

//import com.google.android.gms.vision.Frame;
//import com.google.android.gms.vision.face.Face;
//import com.google.android.gms.vision.face.FaceDetector;

import org.checkerframework.checker.units.qual.A;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.BubbleActivity;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.IPhotoPaintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.Brush;
import org.telegram.ui.Components.Paint.ColorPickerBottomSheet;
import org.telegram.ui.Components.Paint.PaintTypeface;
import org.telegram.ui.Components.Paint.Painting;
import org.telegram.ui.Components.Paint.PersistColorPalette;
//import org.telegram.ui.Components.Paint.PhotoFace;
import org.telegram.ui.Components.Paint.RenderView;
import org.telegram.ui.Components.Paint.Swatch;
import org.telegram.ui.Components.Paint.UndoStore;
import org.telegram.ui.Components.Paint.Views.EditTextOutline;
import org.telegram.ui.Components.Paint.Views.EntitiesContainerView;
import org.telegram.ui.Components.Paint.Views.EntityView;
import org.telegram.ui.Components.Paint.Views.PaintCancelView;
import org.telegram.ui.Components.Paint.Views.PaintColorsListView;
import org.telegram.ui.Components.Paint.Views.PaintDoneView;
import org.telegram.ui.Components.Paint.Views.PaintTextOptionsView;
import org.telegram.ui.Components.Paint.Views.PaintToolsView;
import org.telegram.ui.Components.Paint.Views.PaintTypefaceListView;
import org.telegram.ui.Components.Paint.Views.PaintWeightChooserView;
import org.telegram.ui.Components.Paint.Views.PhotoView;
import org.telegram.ui.Components.Paint.Views.StickerView;
import org.telegram.ui.Components.Paint.Views.TextPaintView;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.Size;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto;
import org.telegram.ui.Components.StickerMasksAlert;
import org.telegram.ui.PhotoViewer;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaintView extends SizeNotifierFrameLayoutPhoto implements IPhotoPaintView, PaintToolsView.Delegate, EntityView.EntityViewDelegate, PaintTextOptionsView.Delegate, SizeNotifierFrameLayoutPhoto.SizeNotifierFrameLayoutPhotoDelegate, StoryRecorder.Touchable {
    private PaintCancelView cancelButton;
    private PaintDoneView doneButton;
    private float offsetTranslationY;

    private Bitmap bitmapToEdit;
    private Bitmap facesBitmap;
    private UndoStore undoStore;

    private DispatchQueue queue;

    private MediaController.CropState currentCropState;
    private float panTranslationProgress;
    private float panTranslationY, scale, inputTransformX, inputTransformY, transformX, transformY, imageWidth, imageHeight;
    private boolean ignoreLayout;
    private float baseScale;
    private Size paintingSize;

    private EntityView currentEntityView;
    private boolean editingText;
    private int selectedTextType;
    public boolean enteredThroughText;

    private boolean inBubbleMode;

    private RenderView renderView;
    private View renderInputView;
    private FrameLayout selectionContainerView;
    public EntitiesContainerView entitiesView;
    private FrameLayout topLayout;
    private FrameLayout bottomLayout;
    private FrameLayout overlayLayout;
    private FrameLayout pipetteContainerLayout;
    private LinearLayout tabsLayout;
    private View textDim;

    private int tabsSelectedIndex = 0;
    private int tabsNewSelectedIndex = -1;
    private float tabsSelectionProgress;
    private ValueAnimator tabsSelectionAnimator;

    private boolean ignoreToolChangeAnimationOnce;

    private PaintWeightChooserView weightChooserView;
    private PaintWeightChooserView.ValueOverride weightDefaultValueOverride = new PaintWeightChooserView.ValueOverride() {
        @Override
        public float get() {
            Brush brush = renderView.getCurrentBrush();
            if (brush == null) {
                return PersistColorPalette.getInstance(currentAccount).getCurrentWeight();
            }
            return PersistColorPalette.getInstance(currentAccount).getWeight(String.valueOf(Brush.BRUSHES_LIST.indexOf(brush)), brush.getDefaultWeight());
        }

        @Override
        public void set(float val) {
            PersistColorPalette.getInstance(currentAccount).setWeight(String.valueOf(Brush.BRUSHES_LIST.indexOf(renderView.getCurrentBrush())), val);
            colorSwatch.brushWeight = val;
            setCurrentSwatch(colorSwatch, true);
        }
    };

//    private ArrayList<PhotoFace> faces;
    private int originalBitmapRotation;
    private BigInteger lcm;

    private TextView drawTab;
    private TextView stickerTab;
    private TextView textTab;

    private PaintToolsView paintToolsView;
    private PaintTextOptionsView textOptionsView;
    private PaintTypefaceListView typefaceListView;
    private ImageView undoButton;
    private LinearLayout zoomOutButton;
    private ImageView zoomOutImage;
    private TextView zoomOutText;
    private TextView undoAllButton;
    private TextView cancelTextButton;
    private TextView doneTextButton;

    private Paint typefaceMenuOutlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint typefaceMenuBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float typefaceMenuTransformProgress;
    private boolean isTypefaceMenuShown;
    private SpringAnimation typefaceMenuTransformAnimation;

    private PaintColorsListView colorsListView;
    private Paint colorPickerRainbowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint colorSwatchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint colorSwatchOutlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Swatch colorSwatch = new Swatch(Color.WHITE, 1f, 0.016773745f);
    private boolean fillShapes = false;

    private boolean isColorListShown;
    private SpringAnimation toolsTransformAnimation;
    private float toolsTransformProgress;
    private Paint toolsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int currentAccount;
    private Theme.ResourcesProvider resourcesProvider;

    private ActionBarPopupWindow popupWindow;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    private Rect popupRect;

    private Runnable onDoneButtonClickedListener;
    private Runnable onCancelButtonClickedListener;

    private StoryRecorder.WindowView parent;

    private AnimatorSet keyboardAnimator;
    public final KeyboardNotifier keyboardNotifier;

    private ArrayList<VideoEditedInfo.MediaEntity> initialEntities;
    private int w, h;

    private ColorPickerBottomSheet colorPickerBottomSheet;

    @SuppressLint("NotifyDataSetChanged")
    public PaintView(Context context, StoryRecorder.WindowView parent, Activity activity, int currentAccount, Bitmap bitmap, Bitmap originalBitmap, int originalRotation, ArrayList<VideoEditedInfo.MediaEntity> entities, int viewWidth, int viewHeight, MediaController.CropState cropState, Runnable onInit, Theme.ResourcesProvider resourcesProvider) {
        super(context, activity, true);
        setDelegate(this);
        this.parent = parent;
        this.initialEntities = entities;
        this.w = viewWidth;
        this.h = viewHeight;

        this.currentAccount = currentAccount;
        this.resourcesProvider = new Theme.ResourcesProvider() {
            @Override
            public int getColor(int key) {
                if (key == Theme.key_actionBarDefaultSubmenuBackground) {
                    return 0xFF282829;
                } else if (key == Theme.key_actionBarDefaultSubmenuItem) {
                    return 0xFFFFFFFF;
                } else if (key == Theme.key_dialogBackground) {
                    return 0xFF1F1F1F;
                } else if (key == Theme.key_dialogTextBlack) {
                    return -592138;
                } else if (key == Theme.key_dialogTextGray3) {
                    return -8553091;
                } else if (key == Theme.key_chat_emojiPanelBackground) {
                    return 0xFF000000;
                } else if (key == Theme.key_chat_emojiPanelShadowLine) {
                    return -1610612736;
                } else if (key == Theme.key_chat_emojiBottomPanelIcon) {
                    return -9539985;
                } else if (key == Theme.key_chat_emojiPanelBackspace) {
                    return -9539985;
                } else if (key == Theme.key_chat_emojiPanelIcon) {
                    return -9539985;
//                } else if (key == Theme.key_chat_emojiPanelIconSelected) {
//                    return -10177041;
                } else if (key == Theme.key_windowBackgroundWhiteBlackText) {
                    return -1;
                } else if (key == Theme.key_featuredStickers_addedIcon) {
                    return -11754001;
                } else if (key == Theme.key_listSelector) {
                    return 0x1FFFFFFF;
                } else if (key == Theme.key_profile_tabSelectedText) {
                    return 0xFFFFFFFF;
                } else if (key == Theme.key_profile_tabText) {
                    return 0xFFFFFFFF;
                } else if (key == Theme.key_profile_tabSelectedLine) {
                    return 0xFFFFFFFF;
                } else if (key == Theme.key_profile_tabSelector) {
                    return 0x14FFFFFF;
                } else if (key == Theme.key_chat_emojiSearchIcon || key == Theme.key_featuredStickers_addedIcon) {
                    return 0xFF878787;
                } else if (key == Theme.key_chat_emojiSearchBackground) {
                    return 0x2E878787;
                }


                if (resourcesProvider != null) {
                    return resourcesProvider.getColor(key);
                } else {
                    return Theme.getColor(key);
                }
            }

            private ColorFilter animatedEmojiColorFilter;

            @Override
            public ColorFilter getAnimatedEmojiColorFilter() {
                if (animatedEmojiColorFilter == null) {
                    animatedEmojiColorFilter = new PorterDuffColorFilter(0xFFFFFFFF, PorterDuff.Mode.SRC_IN);
                }
                return animatedEmojiColorFilter;
            }
        };
        this.currentCropState = cropState;

        inBubbleMode = context instanceof BubbleActivity;

        PersistColorPalette palette = PersistColorPalette.getInstance(currentAccount);
        colorSwatch.color = palette.getColor(0);
        colorSwatch.brushWeight = palette.getCurrentWeight();

        queue = new DispatchQueue("Paint");

        bitmapToEdit = bitmap;
        facesBitmap = originalBitmap;
        originalBitmapRotation = originalRotation;
        undoStore = new UndoStore();
        undoStore.setDelegate(() -> {
            boolean canUndo = undoStore.canUndo();
            undoButton.animate().cancel();
            undoButton.animate().alpha(canUndo ? 1f : 0.6f).translationY(0).setDuration(150).start();
            undoButton.setClickable(canUndo);
            undoAllButton.animate().cancel();
            undoAllButton.animate().alpha(canUndo ? 1f : 0.6f).translationY(0).setDuration(150).start();
            undoAllButton.setClickable(canUndo);
        });

        textDim = new View(context);
        textDim.setVisibility(View.GONE);
        textDim.setBackgroundColor(0x4d000000);
        textDim.setAlpha(0f);

        renderView = new RenderView(context, new Painting(getPaintingSize(), originalBitmap, originalRotation), bitmapToEdit) {
            @Override
            public void selectBrush(Brush brush) {
                int index = 1 + Brush.BRUSHES_LIST.indexOf(brush);
                if (index > 1 && originalBitmap == null) {
                    index--;
                }
                paintToolsView.select(index);
                onBrushSelected(brush);
            }
        };
        renderView.setDelegate(new RenderView.RenderViewDelegate() {

            @Override
            public void onFirstDraw() {
                if (onInit != null) {
                    onInit.run();
                }
            }

            @Override
            public void onBeganDrawing() {
                if (currentEntityView != null) {
                    selectEntity(null);
                }
                weightChooserView.setViewHidden(true);
            }

            @Override
            public void onFinishedDrawing(boolean moved) {
                undoStore.getDelegate().historyChanged();
                weightChooserView.setViewHidden(false);
            }

            @Override
            public boolean shouldDraw() {
                boolean draw = currentEntityView == null;
                if (!draw) {
                    selectEntity(null);
                }
                return draw;
            }

            @Override
            public void invalidateInputView() {
                if (renderInputView != null) {
                    renderInputView.invalidate();
                }
            }

            @Override
            public void resetBrush() {
                if (ignoreToolChangeAnimationOnce) {
                    ignoreToolChangeAnimationOnce = false;
                    return;
                }
                paintToolsView.select(1);
                onBrushSelected(Brush.BRUSHES_LIST.get(0));
            }
        });
        renderView.setUndoStore(undoStore);
        renderView.setQueue(queue);
        renderView.setVisibility(View.INVISIBLE);
//        addView(renderView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));

        renderInputView = new View(context) {
            @Override
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (renderView != null) {
                    renderView.onDrawForInput(canvas);
                }
            }
        };
        renderInputView.setVisibility(View.INVISIBLE);
//        addView(renderInputView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));

        entitiesView = new EntitiesContainerView(context, new EntitiesContainerView.EntitiesContainerViewDelegate() {
            @Override
            public boolean shouldReceiveTouches() {
                return true;
            }

            @Override
            public EntityView onSelectedEntityRequest() {
                return currentEntityView;
            }

            @Override
            public void onEntityDeselect() {
                selectEntity(null);
                if (enteredThroughText) {
                    dismiss();
                    enteredThroughText = false;
                }
            }
        }) {
            Paint linePaint = new Paint();

            long lastUpdate;
            float stickyXAlpha, stickyYAlpha;

            {
                setWillNotDraw(false);
                linePaint.setStrokeWidth(AndroidUtilities.dp(2));
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setColor(Color.WHITE);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                long dt = Math.min(16, System.currentTimeMillis() - lastUpdate);
                lastUpdate = System.currentTimeMillis();
                boolean drawStickyX = false, drawStickyY = false;

                if (currentEntityView != null && currentEntityView.hasTouchDown() && currentEntityView.hasPanned()) {
                    drawStickyX = currentEntityView.hasStickyX();
                    drawStickyY = currentEntityView.hasStickyY();
                }

                if (drawStickyX && stickyXAlpha != 1f) {
                    stickyXAlpha = Math.min(1f, stickyXAlpha + dt / 150f);
                    invalidate();
                } else if (!drawStickyX && stickyXAlpha != 0f) {
                    stickyXAlpha = Math.max(0f, stickyXAlpha - dt / 150f);
                    invalidate();
                }

                if (drawStickyY && stickyYAlpha != 1f) {
                    stickyYAlpha = Math.min(1f, stickyYAlpha + dt / 150f);
                    invalidate();
                } else if (!drawStickyY && stickyYAlpha != 0f) {
                    stickyYAlpha = Math.max(0f, stickyYAlpha - dt / 150f);
                    invalidate();
                }

                if (stickyYAlpha != 0f) {
                    linePaint.setAlpha((int) (stickyYAlpha * 0xFF));
                    float y = getMeasuredHeight() / 2f;
                    canvas.drawLine(0, y, getMeasuredWidth(), y, linePaint);
                }
                if (stickyXAlpha != 0f) {
                    linePaint.setAlpha((int) (stickyXAlpha * 0xFF));
                    float x = getMeasuredWidth() / 2f;
                    canvas.drawLine(x, 0, x, getMeasuredHeight(), linePaint);
                }
            }

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (w <= 0) {
                    w = entitiesView.getMeasuredWidth();
                }
                if (h <= 0) {
                    h = entitiesView.getMeasuredWidth();
                }
            }
        };
//        addView(entitiesView);
        setupEntities();

        entitiesView.setVisibility(INVISIBLE);

        selectionContainerView = new FrameLayout(context) {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return false;
            }
        };
//        addView(selectionContainerView);

        topLayout = new FrameLayout(context);
        topLayout.setPadding(AndroidUtilities.dp(4), AndroidUtilities.dp(12), AndroidUtilities.dp(4), AndroidUtilities.dp(12));
        topLayout.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int [] {0x40000000, 0x00000000} ));
        addView(topLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP));

        undoButton = new ImageView(context);
        undoButton.setImageResource(R.drawable.photo_undo2);
        undoButton.setPadding(AndroidUtilities.dp(3), AndroidUtilities.dp(3), AndroidUtilities.dp(3), AndroidUtilities.dp(3));
        undoButton.setBackground(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        undoButton.setOnClickListener(v -> {
            if (renderView != null && renderView.getCurrentBrush() instanceof Brush.Shape) {
                renderView.clearShape();
                paintToolsView.setSelectedIndex(1);
                onBrushSelected(Brush.BRUSHES_LIST.get(0));
            } else {
                undoStore.undo();
            }
        });
        undoButton.setAlpha(0.6f);
        undoButton.setClickable(false);
        topLayout.addView(undoButton, LayoutHelper.createFrame(32, 32, Gravity.TOP | Gravity.LEFT, 12, 0, 0, 0));

        zoomOutButton = new LinearLayout(context);
        zoomOutButton.setOrientation(LinearLayout.HORIZONTAL);
        zoomOutButton.setBackground(Theme.createSelectorDrawable(0x30ffffff, Theme.RIPPLE_MASK_ROUNDRECT_6DP));
        zoomOutButton.setPadding(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), 0);
        zoomOutText = new TextView(context);
        zoomOutText.setTextColor(Color.WHITE);
        zoomOutText.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        zoomOutText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        zoomOutText.setText(LocaleController.getString(R.string.PhotoEditorZoomOut));
        zoomOutImage = new ImageView(context);
        zoomOutImage.setImageResource(R.drawable.photo_zoomout);
        zoomOutButton.addView(zoomOutImage, LayoutHelper.createLinear(24, 24, Gravity.CENTER_VERTICAL, 0, 0, 8, 0));
        zoomOutButton.addView(zoomOutText, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
        zoomOutButton.setAlpha(0);
        zoomOutButton.setOnClickListener(e -> {
            PhotoViewer.getInstance().zoomOut();
        });
        topLayout.addView(zoomOutButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 32, Gravity.CENTER));

        undoAllButton = new TextView(context);
        undoAllButton.setBackground(Theme.createSelectorDrawable(0x30ffffff, Theme.RIPPLE_MASK_ROUNDRECT_6DP));
        undoAllButton.setPadding(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), 0);
        undoAllButton.setText(LocaleController.getString(R.string.PhotoEditorClearAll));
        undoAllButton.setGravity(Gravity.CENTER_VERTICAL);
        undoAllButton.setTextColor(Color.WHITE);
        undoAllButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        undoAllButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        undoAllButton.setOnClickListener(v -> clearAll());
        undoAllButton.setAlpha(0.6f);
        topLayout.addView(undoAllButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 32, Gravity.RIGHT, 0, 0, 4, 0));

        cancelTextButton = new TextView(context);
        cancelTextButton.setBackground(Theme.createSelectorDrawable(0x30ffffff, Theme.RIPPLE_MASK_ROUNDRECT_6DP));
        cancelTextButton.setText(LocaleController.getString(R.string.Clear));
        cancelTextButton.setPadding(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), 0);
        cancelTextButton.setGravity(Gravity.CENTER_VERTICAL);
        cancelTextButton.setTextColor(Color.WHITE);
        cancelTextButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        cancelTextButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        cancelTextButton.setOnClickListener(v -> {
            if (currentEntityView instanceof TextPaintView) {
                AndroidUtilities.hideKeyboard(((TextPaintView) currentEntityView).getFocusedView());
            }
            if (emojiViewVisible) {
                hideEmojiPopup(false);
            }
            removeEntity(currentEntityView);
            selectEntity(null);
        });
        cancelTextButton.setAlpha(0);
        cancelTextButton.setVisibility(View.GONE);
        topLayout.addView(cancelTextButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 32, Gravity.LEFT | Gravity.TOP, 4, 0, 0, 0));

        doneTextButton = new TextView(context);
        doneTextButton.setBackground(Theme.createSelectorDrawable(0x30ffffff, Theme.RIPPLE_MASK_ROUNDRECT_6DP));
        doneTextButton.setText(LocaleController.getString(R.string.Done));
        doneTextButton.setPadding(AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8), 0);
        doneTextButton.setGravity(Gravity.CENTER_VERTICAL);
        doneTextButton.setTextColor(Color.WHITE);
        doneTextButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        doneTextButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        doneTextButton.setOnClickListener(v -> {
            selectEntity(null);
        });
        doneTextButton.setAlpha(0);
        doneTextButton.setVisibility(View.GONE);
        topLayout.addView(doneTextButton, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, 32, Gravity.RIGHT, 0, 0, 4, 0));

        bottomLayout = new FrameLayout(context) {
            private float lastRainbowX, lastRainbowY;
            private Path path = new Path();

            {
                setWillNotDraw(false);
                colorPickerRainbowPaint.setStyle(Paint.Style.STROKE);
                colorPickerRainbowPaint.setStrokeWidth(AndroidUtilities.dp(2));
            }

            private void checkRainbow(float cx, float cy) {
                if (cx != lastRainbowX || cy != lastRainbowY) {
                    lastRainbowX = cx;
                    lastRainbowY = cy;

                    int[] colors = {
                            0xffeb4b4b,
                            0xffee82ee,
                            0xff6080e4,
                            Color.CYAN,
                            0xff8fce00,
                            Color.YELLOW,
                            0xffffa500,
                            0xffeb4b4b
                    };
                    colorPickerRainbowPaint.setShader(new SweepGradient(cx, cy, colors, null));
                }
            }

            @Override
            public void setTranslationY(float translationY) {
                super.setTranslationY(translationY);
                if (overlayLayout != null) {
                    overlayLayout.invalidate();
                }
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                ViewGroup barView = getBarView();
                AndroidUtilities.rectTmp.set(
                        lerp(barView.getLeft(), colorsListView.getLeft(), toolsTransformProgress),
                        lerp(barView.getTop(), colorsListView.getTop(), toolsTransformProgress),
                        lerp(barView.getRight(), colorsListView.getRight(), toolsTransformProgress),
                        lerp(barView.getBottom(), colorsListView.getBottom(), toolsTransformProgress)
                );
                final float radius = lerp(AndroidUtilities.dp(32), AndroidUtilities.dp(24), toolsTransformProgress);
                canvas.drawRoundRect(AndroidUtilities.rectTmp, radius, radius, toolsPaint);

                if (barView != null && barView.getChildCount() >= 1 && toolsTransformProgress != 1f) {
                    canvas.save();
                    canvas.translate(barView.getLeft(), barView.getTop());

                    View child = barView.getChildAt(0);
                    if (barView instanceof PaintTextOptionsView) {
                        child = ((PaintTextOptionsView) barView).getColorClickableView();
                    }

                    if (child.getAlpha() != 0f) {
                        canvas.scale(child.getScaleX(), child.getScaleY(), child.getPivotX(), child.getPivotY());

                        colorPickerRainbowPaint.setAlpha((int) ((1f - toolsTransformProgress) * child.getAlpha() * 0xFF));

                        int childWidth = child.getWidth() - child.getPaddingLeft() - child.getPaddingRight();
                        int childHeight = child.getHeight() - child.getPaddingTop() - child.getPaddingBottom();
                        float cx = child.getX() + child.getPaddingLeft() + childWidth / 2f, cy = child.getY() + child.getPaddingTop() + childHeight / 2f;
                        if (tabsNewSelectedIndex != -1) {
                            ViewGroup barView2 = (ViewGroup) getBarView(tabsNewSelectedIndex);
                            View newView = (barView2 == null ? barView : barView2).getChildAt(0);
                            if (barView2 instanceof PaintTextOptionsView) {
                                newView = ((PaintTextOptionsView) barView2).getColorClickableView();
                            }
                            cx = lerp(cx, newView.getX() + newView.getPaddingLeft() + (newView.getWidth() - newView.getPaddingLeft() - newView.getPaddingRight()) / 2f, tabsSelectionProgress);
                            cy = lerp(cy, newView.getY() + newView.getPaddingTop() + (newView.getHeight() - newView.getPaddingTop() - newView.getPaddingBottom()) / 2f, tabsSelectionProgress);
                        }
                        if (colorsListView != null && colorsListView.getChildCount() > 0) {
                            View animateToView = colorsListView.getChildAt(0);
                            cx = lerp(cx, colorsListView.getX() - barView.getLeft() + animateToView.getX() + animateToView.getWidth() / 2f, toolsTransformProgress);
                            cy = lerp(cy, colorsListView.getY() - barView.getTop() + animateToView.getY() + animateToView.getHeight() / 2f, toolsTransformProgress);
                        }
                        checkRainbow(cx, cy);

                        float rad = Math.min(childWidth, childHeight) / 2f - AndroidUtilities.dp(0.5f);
                        if (colorsListView != null && colorsListView.getChildCount() > 0) {
                            View animateToView = colorsListView.getChildAt(0);
                            rad = lerp(rad, Math.min(animateToView.getWidth() - animateToView.getPaddingLeft() - animateToView.getPaddingRight(), animateToView.getHeight() - animateToView.getPaddingTop() - animateToView.getPaddingBottom()) / 2f - AndroidUtilities.dp(2f), toolsTransformProgress);
                        }
                        AndroidUtilities.rectTmp.set(cx - rad, cy - rad, cx + rad, cy + rad);
                        canvas.drawArc(AndroidUtilities.rectTmp, 0, 360, false, colorPickerRainbowPaint);

                        colorSwatchPaint.setColor(colorSwatch.color);
                        colorSwatchPaint.setAlpha((int) (colorSwatchPaint.getAlpha() * child.getAlpha()));
                        colorSwatchOutlinePaint.setColor(colorSwatch.color);
                        colorSwatchOutlinePaint.setAlpha((int) (0xFF * child.getAlpha()));

                        float rad2 = rad - AndroidUtilities.dp(3f);
                        PaintColorsListView.drawColorCircle(canvas, cx, cy, rad2, colorSwatchPaint.getColor());

                        colorSwatchOutlinePaint.setAlpha((int) (colorSwatchOutlinePaint.getAlpha() * toolsTransformProgress * child.getAlpha()));
                        canvas.drawCircle(cx, cy, rad - (AndroidUtilities.dp(3f) + colorSwatchOutlinePaint.getStrokeWidth()) * (1f - toolsTransformProgress), colorSwatchOutlinePaint);
                    }

                    canvas.restore();
                }
            }
        };
        bottomLayout.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), 0);
        bottomLayout.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int [] {0x00000000, 0x80000000} ));
        addView(bottomLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 44 + 60, Gravity.BOTTOM));

        paintToolsView = new PaintToolsView(context, originalBitmap != null);
        paintToolsView.setPadding(AndroidUtilities.dp(16), 0, AndroidUtilities.dp(16), 0);
        paintToolsView.setDelegate(this);
//        paintToolsView.setSelectedIndex(MathUtils.clamp(palette.getCurrentBrush(), 0, Brush.BRUSHES_LIST.size()) + 1);
        paintToolsView.setSelectedIndex(1);
        bottomLayout.addView(paintToolsView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48));

        textOptionsView = new PaintTextOptionsView(context);
        textOptionsView.setPadding(AndroidUtilities.dp(16), 0, AndroidUtilities.dp(8), 0);
        textOptionsView.setVisibility(GONE);
        textOptionsView.setDelegate(this);
        post(() -> textOptionsView.setTypeface(PersistColorPalette.getInstance(currentAccount).getCurrentTypeface()));
        textOptionsView.setAlignment(PersistColorPalette.getInstance(currentAccount).getCurrentAlignment());
        bottomLayout.addView(textOptionsView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48));

        overlayLayout = new FrameLayout(context) {
            {
                setWillNotDraw(false);
            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN && isTypefaceMenuShown) {
                    showTypefaceMenu(false);
                    return true;
                }
                return super.onTouchEvent(event);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                typefaceMenuOutlinePaint.setAlpha((int) (0x14 * textOptionsView.getAlpha() * (1f - typefaceMenuTransformProgress)));

                textOptionsView.getTypefaceCellBounds(AndroidUtilities.rectTmp);
                float yOffset = bottomLayout.getTop() + textOptionsView.getTop() + bottomLayout.getTranslationY() + textOptionsView.getTranslationY();
                AndroidUtilities.rectTmp.set(
                    lerp(AndroidUtilities.rectTmp.left, typefaceListView.getLeft(), typefaceMenuTransformProgress),
                    lerp(yOffset + AndroidUtilities.rectTmp.top, typefaceListView.getTop() - typefaceListView.getTranslationY(), typefaceMenuTransformProgress),
                    lerp(AndroidUtilities.rectTmp.right, typefaceListView.getRight(), typefaceMenuTransformProgress),
                    lerp(yOffset + AndroidUtilities.rectTmp.bottom, typefaceListView.getBottom() - typefaceListView.getTranslationY(), typefaceMenuTransformProgress)
                );
                float rad = AndroidUtilities.dp(lerp(32, 16, typefaceMenuTransformProgress));

                int alpha = typefaceMenuBackgroundPaint.getAlpha();
                typefaceMenuBackgroundPaint.setAlpha((int) (alpha * typefaceMenuTransformProgress));
                canvas.drawRoundRect(AndroidUtilities.rectTmp, rad, rad, typefaceMenuBackgroundPaint);
                typefaceMenuBackgroundPaint.setAlpha(alpha);

                canvas.drawRoundRect(AndroidUtilities.rectTmp, rad, rad, typefaceMenuOutlinePaint);
            }
        };
        addView(overlayLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        typefaceListView = new PaintTypefaceListView(context);
        typefaceListView.setVisibility(GONE);
        typefaceListView.setOnItemClickListener((view, position) -> {
            PaintTypeface typeface = PaintTypeface.get().get(position);
            textOptionsView.setTypeface(typeface.getKey());
            onTypefaceSelected(typeface);
            showTypefaceMenu(false);
        });
        textOptionsView.setTypefaceListView(typefaceListView);
        overlayLayout.addView(typefaceListView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT | Gravity.BOTTOM, 0, 0, 8, 8));

        typefaceMenuOutlinePaint.setStyle(Paint.Style.FILL);
        typefaceMenuOutlinePaint.setColor(0x14ffffff);

        typefaceMenuBackgroundPaint.setColor(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));

        colorsListView = new PaintColorsListView(context) {
            private Path path = new Path();

            @Override
            public void draw(Canvas c) {
                ViewGroup barView = getBarView();
                AndroidUtilities.rectTmp.set(
                        lerp(barView.getLeft() - getLeft(), 0, toolsTransformProgress),
                        lerp(barView.getTop() - getTop(), 0, toolsTransformProgress),
                        lerp(barView.getRight() - getLeft(), getWidth(), toolsTransformProgress),
                        lerp(barView.getBottom() - getTop(), getHeight(), toolsTransformProgress)
                );

                path.rewind();
                path.addRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(32), AndroidUtilities.dp(32), Path.Direction.CW);

                c.save();
                c.clipPath(path);
                super.draw(c);
                c.restore();
            }
        };
        colorsListView.setVisibility(GONE);
        colorsListView.setColorPalette(PersistColorPalette.getInstance(currentAccount));
        colorsListView.setColorListener(color -> {
            setNewColor(color);
            showColorList(false);
        });
        bottomLayout.addView(colorsListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 84, Gravity.TOP, 56, 0, 56, 6));

        setupTabsLayout(context);

        cancelButton = new PaintCancelView(context);
        cancelButton.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        cancelButton.setBackground(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        bottomLayout.addView(cancelButton, LayoutHelper.createFrame(32, 32, Gravity.BOTTOM | Gravity.LEFT, 12, 0, 0, 4));
        cancelButton.setOnClickListener(e -> {
            if (isColorListShown) {
                showColorList(false);
                return;
            }
            if (emojiViewVisible) {
                hideEmojiPopup(true);
                return;
            }
            if (editingText) {
                selectEntity(null);
                return;
            }
//            clearAll();
            if (onCancelButtonClickedListener != null) {
                onCancelButtonClickedListener.run();
            }
        });

        doneButton = new PaintDoneView(context);
        doneButton.setPadding(AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8), AndroidUtilities.dp(8));
        doneButton.setBackground(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        doneButton.setOnClickListener(v -> {
            if (isColorListShown) {
                colorPickerBottomSheet = new ColorPickerBottomSheet(context, this.resourcesProvider);
                colorPickerBottomSheet.setColor(colorSwatch.color).setPipetteDelegate(new ColorPickerBottomSheet.PipetteDelegate() {
                    private boolean hasPipette;

                    @Override
                    public void onStartColorPipette() {
                        hasPipette = true;
                    }

                    @Override
                    public void onStopColorPipette() {
                        hasPipette = false;
                    }

                    @Override
                    public ViewGroup getContainerView() {
                        return pipetteContainerLayout;
                    }

                    @Override
                    public View getSnapshotDrawingView() {
                        return PaintView.this;
                    }

                    @Override
                    public void onDrawImageOverCanvas(Bitmap bitmap, Canvas canvas) {
                        Matrix matrix = renderView.getMatrix();
                        canvas.save();
                        canvas.translate(renderView.getX(), renderView.getY());
                        canvas.concat(matrix);
                        canvas.scale(renderView.getWidth() / (float) originalBitmap.getWidth(), renderView.getHeight() / (float) originalBitmap.getHeight(), 0, 0);
                        canvas.drawBitmap(originalBitmap, 0, 0, null);
                        canvas.restore();
                    }

                    @Override
                    public boolean isPipetteVisible() {
                        return hasPipette;
                    }

                    @Override
                    public boolean isPipetteAvailable() {
                        // TODO: Get bitmap from VideoPlayer to support videos
                        return originalBitmap != null;
                    }

                    @Override
                    public void onColorSelected(int color) {
                        showColorList(false);

                        PersistColorPalette.getInstance(currentAccount).selectColor(color);
                        PersistColorPalette.getInstance(currentAccount).saveColors();
                        setNewColor(color);
                        colorsListView.getAdapter().notifyDataSetChanged();
                    }
                }).setColorListener(color -> {
                    PersistColorPalette.getInstance(currentAccount).selectColor(color);
                    PersistColorPalette.getInstance(currentAccount).saveColors();
                    setNewColor(color);
                    colorsListView.getAdapter().notifyDataSetChanged();
                    colorPickerBottomSheet = null;
                }).show();
                return;
            }
            if (onDoneButtonClickedListener != null) {
                onDoneButtonClickedListener.run();
            }
        });
        bottomLayout.addView(doneButton, LayoutHelper.createFrame(32, 32, Gravity.BOTTOM | Gravity.RIGHT, 0, 0, 12, 4));

        weightChooserView = new PaintWeightChooserView(context);
        weightChooserView.setColorSwatch(colorSwatch);
        weightChooserView.setRenderView(renderView);
        weightChooserView.setValueOverride(weightDefaultValueOverride);
        colorSwatch.brushWeight = weightDefaultValueOverride.get();
        weightChooserView.setOnUpdate(()-> {
            setCurrentSwatch(colorSwatch, true);
            PersistColorPalette.getInstance(currentAccount).setCurrentWeight(colorSwatch.brushWeight);
        });
        addView(weightChooserView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        pipetteContainerLayout = new FrameLayout(context);
        addView(pipetteContainerLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        colorSwatchOutlinePaint.setStyle(Paint.Style.STROKE);
        colorSwatchOutlinePaint.setStrokeWidth(AndroidUtilities.dp(2));

        setCurrentSwatch(colorSwatch, true);
//        onBrushSelected(Brush.BRUSHES_LIST.get(MathUtils.clamp(palette.getCurrentBrush(), 0, Brush.BRUSHES_LIST.size())));
        onBrushSelected(Brush.BRUSHES_LIST.get(0));
        updateColors();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setSystemGestureExclusionRects(Arrays.asList(new Rect(0, (int) (AndroidUtilities.displaySize.y * .35f), AndroidUtilities.dp(100), (int) (AndroidUtilities.displaySize.y * .65))));
        }

        keyboardNotifier = new KeyboardNotifier(parent, keyboardHeight -> {
            keyboardHeight = Math.max(keyboardHeight - parent.getBottomPadding(false), emojiPadding - parent.getPaddingUnderContainer());
            keyboardHeight = Math.max(0, keyboardHeight);

            notifyHeightChanged();
            final boolean keyboardVisible = keyboardHeight > 0 && currentEntityView instanceof TextPaintView && ((TextPaintView) currentEntityView).getEditText().isFocused();

            if (keyboardAnimator != null) {
                keyboardAnimator.cancel();
            }
            keyboardAnimator = new AnimatorSet();
            final ArrayList<Animator> animators = new ArrayList<>();
            animators.add(ObjectAnimator.ofFloat(weightChooserView, View.TRANSLATION_Y, keyboardHeight > 0 ? Math.min(0, -keyboardHeight / 2f - AndroidUtilities.dp(8)) : 0));
            animators.add(ObjectAnimator.ofFloat(bottomLayout, View.TRANSLATION_Y, (keyboardHeight > 0 ? Math.min(0, -keyboardHeight + AndroidUtilities.dp(40)) : 0)/* - (isColorListShown && keyboardVisible ? AndroidUtilities.dp(39) : 0)*/));
            animators.add(ObjectAnimator.ofFloat(tabsLayout, View.ALPHA, keyboardVisible ? 0f : 1f));
            animators.add(ObjectAnimator.ofFloat(doneButton, View.ALPHA, keyboardVisible && !isColorListShown ? 0f : 1f));
            animators.add(ObjectAnimator.ofFloat(cancelButton, View.ALPHA, keyboardVisible && !isColorListShown ? 0f : 1f));
            updatePreviewViewTranslationY();

            keyboardAnimator.playTogether(animators);
            if (keyboardVisible) {
                keyboardAnimator.setDuration(AdjustPanLayoutHelper.keyboardDuration);
                keyboardAnimator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
            } else {
                keyboardAnimator.setDuration(350);
                keyboardAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            }
            keyboardAnimator.start();

            for (int i = 0; i < animators.size(); ++i) {
                animators.get(i).setDuration(keyboardVisible ? 350 : AdjustPanLayoutHelper.keyboardDuration);
                animators.get(i).setInterpolator(keyboardVisible ? CubicBezierInterpolator.EASE_OUT_QUINT : AdjustPanLayoutHelper.keyboardInterpolator);
                animators.get(i).start();
            }

            if (!keyboardVisible) {
                showTypefaceMenu(false);
            }
        }) {
            @Override
            public void ignore(boolean ignore) {
                super.ignore(ignore);
                if (ignore) {
                    showTypefaceMenu(false);
                }
            }
        };
    }

    private ObjectAnimator previewViewTranslationAnimator;
    private void updatePreviewViewTranslationY() {
        if (previewViewTranslationAnimator != null) {
            previewViewTranslationAnimator.cancel();
        }
        View previewView = (View) (renderView.getParent());
        if (previewView == null) {
            return;
        }
        previewViewTranslationAnimator = ObjectAnimator.ofFloat(previewView, View.TRANSLATION_Y,
            !(keyboardNotifier.keyboardVisible() && !keyboardNotifier.ignoring || emojiPadding > 0) || currentEntityView == null ? 0 :
            -(currentEntityView.getPosition().y - previewView.getMeasuredHeight() * .3f) * (previewView.getScaleY())
        );
        previewViewTranslationAnimator.setDuration(350);
        previewViewTranslationAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        previewViewTranslationAnimator.start();
    }

    @Override
    public void onAnimationStateChanged(boolean isStart) {
        weightChooserView.setLayerType(isStart ? LAYER_TYPE_HARDWARE : LAYER_TYPE_NONE, null);
    }

    public View getWeightChooserView() {
        return weightChooserView;
    }

    public View getTopLayout() {
        return topLayout;
    }

    public View getBottomLayout() {
        return bottomLayout;
    }

    private void setNewColor(int color) {
        int wasColor = colorSwatch.color;
        colorSwatch.color = color;
        setCurrentSwatch(colorSwatch, true);

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1).setDuration(150);
        animator.addUpdateListener(animation -> {
            float val = (float) animation.getAnimatedValue();

            colorSwatch.color = ColorUtils.blendARGB(wasColor, color, val);
            bottomLayout.invalidate();
        });
        animator.start();
    }

    private TextPaintView createText(boolean select) {
        onTextAdd();

        Size paintingSize = getPaintingSize();
        Point position = startPositionRelativeToEntity(null);
        TextPaintView view = new TextPaintView(getContext(), position, (int) (paintingSize.width / 9), "", colorSwatch, selectedTextType);
        view.getEditText().betterFraming = true;
        if (position.x == entitiesView.getMeasuredWidth() / 2f) {
            view.setHasStickyX(true);
        }
        if (position.y == entitiesView.getMeasuredHeight() / 2f) {
            view.setHasStickyY(true);
        }
        view.setDelegate(this);
        view.setMaxWidth(w - AndroidUtilities.dp(7 + 7 + 18));
        view.setTypeface(PersistColorPalette.getInstance(currentAccount).getCurrentTypeface());
        view.setType(PersistColorPalette.getInstance(currentAccount).getCurrentTextType());
        entitiesView.addView(view, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        if (currentCropState != null) {
            view.scale(1.0f / currentCropState.cropScale);
            view.rotate(-(currentCropState.transformRotation + currentCropState.cropRotate));
        }

        if (select) {
            registerRemovalUndo(view);

            view.beginEditing();
            selectEntity(view, false);
            view.getFocusedView().requestFocus();
            AndroidUtilities.showKeyboard(view.getFocusedView());
            editingText = true;
            textOptionsView.setAlignment(PersistColorPalette.getInstance(currentAccount).getCurrentAlignment(), true);
            textOptionsView.setOutlineType(PersistColorPalette.getInstance(currentAccount).getCurrentTextType());
        }
        return view;
    }

    public void clearAll() {
        if (!undoStore.canUndo()) {
            return;
        }
        if (renderView != null && renderView.getCurrentBrush() instanceof Brush.Shape) {
            renderView.clearShape();
            paintToolsView.setSelectedIndex(1);
            onBrushSelected(Brush.BRUSHES_LIST.get(0));
        }
        if (renderView != null) {
            renderView.clearAll();
        }
        undoStore.reset();
        entitiesView.removeAllViews();
    }

    @Override
    public void setOnDoneButtonClickedListener(Runnable callback) {
        onDoneButtonClickedListener = callback;
    }

    public void setOnCancelButtonClickedListener(Runnable callback) {
        onCancelButtonClickedListener = callback;
    }

    protected void dismiss() {

    }

    protected void editSelectedTextEntity() {
        if (!(currentEntityView instanceof TextPaintView) || editingText) {
            return;
        }

        TextPaintView textPaintView = (TextPaintView) currentEntityView;
        editingText = true;

        textPaintView.beginEditing();
        View view = textPaintView.getFocusedView();
        view.requestFocus();
        AndroidUtilities.showKeyboard(view);
    }

    private boolean zoomOutVisible = false;
    @Override
    public void updateZoom(boolean zoomedOut) {
        boolean shouldBeVisible = !zoomedOut;
        if (zoomOutVisible != shouldBeVisible) {
            zoomOutVisible = shouldBeVisible;
            zoomOutButton.animate().cancel();
            zoomOutButton.animate().alpha(zoomedOut ? 0f : 1f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(240).start();
        }
    }

    private boolean selectEntity(EntityView entityView) {
        return selectEntity(entityView, true);
    }

    private boolean selectEntity(EntityView entityView, boolean changeOptions) {
        boolean changed = false;

        boolean animatingToTextTab = false;
        if (entityView instanceof TextPaintView && (tabsNewSelectedIndex == -1 && tabsSelectedIndex != 2 || tabsNewSelectedIndex != -1 && tabsNewSelectedIndex != 2)) {
            if (tabsSelectionAnimator != null && tabsNewSelectedIndex != 2) {
                tabsSelectionAnimator.cancel();
            }
            if (isColorListShown) {
                showColorList(false);
            }
            switchTab(2);
            animatingToTextTab = true;
        }

        if (entityView instanceof TextPaintView && changeOptions) {
            int align;
            switch (((TextPaintView) entityView).getEditText().getGravity()) {
                default:
                case Gravity.LEFT | Gravity.CENTER_VERTICAL:
                    align = PaintTextOptionsView.ALIGN_LEFT;
                    break;
                case Gravity.CENTER:
                    align = PaintTextOptionsView.ALIGN_CENTER;
                    break;
                case Gravity.RIGHT | Gravity.CENTER_VERTICAL:
                    align = PaintTextOptionsView.ALIGN_RIGHT;
                    break;
            }
            textOptionsView.setAlignment(align);
            PaintTypeface typeface = ((TextPaintView) entityView).getTypeface();
            if (typeface != null) {
                textOptionsView.setTypeface(typeface.getKey());
            }
            textOptionsView.setOutlineType(((TextPaintView) entityView).getType(), true);
            overlayLayout.invalidate();
        }

        if (currentEntityView != null) {
            if (currentEntityView == entityView) {
                if (!editingText) {
                    if (entityView instanceof TextPaintView) {
                        enteredThroughText = true;
                        editSelectedTextEntity();
                    } else {
                        showMenuForEntity(currentEntityView);
                    }
                } else if (currentEntityView instanceof TextPaintView) {
                    AndroidUtilities.showKeyboard(((TextPaintView) currentEntityView).getFocusedView());
                    hideEmojiPopup(false);
                }
                return true;
            } else {
                currentEntityView.deselect();
                if (currentEntityView instanceof TextPaintView) {
                    ((TextPaintView) currentEntityView).endEditing();
                    if (!(entityView instanceof TextPaintView)) {
                        editingText = false;
                        AndroidUtilities.hideKeyboard(((TextPaintView) currentEntityView).getFocusedView());
                        hideEmojiPopup(false);
                    }
                }
            }
            changed = true;
        }

        EntityView oldEntity = currentEntityView;
        currentEntityView = entityView;
        if (oldEntity instanceof TextPaintView) {
            TextPaintView textPaintView = (TextPaintView) oldEntity;
            if (TextUtils.isEmpty(textPaintView.getText())) {
                removeEntity(oldEntity);
            }
        }

        if (currentEntityView != null) {
            currentEntityView.select(selectionContainerView);
            entitiesView.bringChildToFront(currentEntityView);

            if (currentEntityView instanceof TextPaintView) {
                TextPaintView textPaintView = (TextPaintView) currentEntityView;
                textPaintView.getSwatch().brushWeight = colorSwatch.brushWeight;
                setCurrentSwatch(textPaintView.getSwatch(), true);

                float base = (int) (paintingSize.width / 9);
                weightChooserView.setValueOverride(new PaintWeightChooserView.ValueOverride() {
                    @Override
                    public float get() {
                        return textPaintView.getBaseFontSize() / base;
                    }

                    @Override
                    public void set(float val) {
                        textPaintView.setBaseFontSize((int) (base * val));
                    }
                });
                weightChooserView.setShowPreview(false);
            } else {
                weightChooserView.setValueOverride(weightDefaultValueOverride);
                weightChooserView.setShowPreview(true);
                colorSwatch.brushWeight = weightDefaultValueOverride.get();
                setCurrentSwatch(colorSwatch, true);
            }

            changed = true;
        } else {
            if (tabsSelectionAnimator != null && tabsNewSelectedIndex != 0) {
                tabsSelectionAnimator.cancel();
            }
            if (isColorListShown) {
                showColorList(false);
            }
            switchTab(0);

            weightChooserView.setValueOverride(weightDefaultValueOverride);
            weightChooserView.setShowPreview(true);
            colorSwatch.brushWeight = weightDefaultValueOverride.get();
            setCurrentSwatch(colorSwatch, true);
        }
        updateTextDim();

        return changed;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean restore = false;
        if ((child == renderView || child == renderInputView || child == entitiesView || child == selectionContainerView) && currentCropState != null) {
            canvas.save();

            int status = (Build.VERSION.SDK_INT >= 21 && !inBubbleMode ? AndroidUtilities.statusBarHeight : 0);
            int actionBarHeight = ActionBar.getCurrentActionBarHeight();
            int actionBarHeight2 = actionBarHeight + status;

            int vw = child.getMeasuredWidth();
            int vh = child.getMeasuredHeight();
            int tr = currentCropState.transformRotation;
            if (tr == 90 || tr == 270) {
                int temp = vw;
                vw = vh;
                vh = temp;
            }

            int w = (int) (vw * currentCropState.cropPw * child.getScaleX() / currentCropState.cropScale);
            int h = (int) (vh * currentCropState.cropPh * child.getScaleY() / currentCropState.cropScale);
            float x = (float) Math.ceil((getMeasuredWidth() - w) / 2f) + transformX;
            float y = (getMeasuredHeight() - actionBarHeight2 - AndroidUtilities.dp(48) + getAdditionalBottom() - h) / 2f + AndroidUtilities.dp(8) + status + transformY;

            canvas.clipRect(Math.max(0, x), Math.max(0, y), Math.min(x + w, getMeasuredWidth()), Math.min(getMeasuredHeight(), y + h));
            restore = true;
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (restore) {
            canvas.restore();
        }
        return result;
    }

    private ViewGroup getBarView() {
        return tabsSelectedIndex == 2 ? textOptionsView : paintToolsView;
    }

    private void setupTabsLayout(Context context) {
        tabsLayout = new LinearLayout(context) {
            Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            {
                linePaint.setStrokeWidth(AndroidUtilities.dp(2));
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeCap(Paint.Cap.ROUND);

                setWillNotDraw(false);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                TextView selectedTab = (TextView) getChildAt(tabsSelectedIndex);
                TextView newSelectedTab = tabsNewSelectedIndex != -1 ? (TextView) getChildAt(tabsNewSelectedIndex) : null;
                linePaint.setColor(selectedTab.getCurrentTextColor());
                float y = selectedTab.getY() + selectedTab.getHeight() - selectedTab.getPaddingBottom() + AndroidUtilities.dp(3);
                Layout layout = selectedTab.getLayout();
                if (layout == null) {
                    return;
                }
                Layout newLayout = newSelectedTab != null ? newSelectedTab.getLayout() : null;
                float pr = newLayout == null ? 0 : CubicBezierInterpolator.DEFAULT.getInterpolation(tabsSelectionProgress);
                float x = lerp(selectedTab.getX() + layout.getPrimaryHorizontal(layout.getLineStart(0)), newLayout != null ? newSelectedTab.getX() + newLayout.getPrimaryHorizontal(layout.getLineStart(0)) : 0, pr);
                float width = lerp(layout.getPrimaryHorizontal(layout.getLineEnd(0)) - layout.getPrimaryHorizontal(layout.getLineStart(0)), newLayout != null ? newLayout.getPrimaryHorizontal(newLayout.getLineEnd(0)) - newLayout.getPrimaryHorizontal(newLayout.getLineStart(0)) : 0, pr);
                canvas.drawLine(x, y, x + width, y, linePaint);
            }
        };
        tabsLayout.setClipToPadding(false);
        tabsLayout.setOrientation(LinearLayout.HORIZONTAL);
        bottomLayout.addView(tabsLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 32 + 8, Gravity.BOTTOM, 52, 0, 52, 0));

        drawTab = new TextView(context);
        drawTab.setText(LocaleController.getString(R.string.PhotoEditorDraw).toUpperCase());
        drawTab.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector), Theme.RIPPLE_MASK_ROUNDRECT_6DP));
        drawTab.setPadding(0, AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8));
        drawTab.setTextColor(Color.WHITE);
        drawTab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        drawTab.setGravity(Gravity.CENTER_HORIZONTAL);
        drawTab.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        drawTab.setSingleLine();
        drawTab.setOnClickListener(v -> {
            if (editingText) {
                selectEntity(null);
            } else {
                switchTab(0);
            }
        });
        tabsLayout.addView(drawTab, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1f));

        stickerTab = new TextView(context);
        stickerTab.setText(LocaleController.getString(R.string.PhotoEditorSticker).toUpperCase());
        stickerTab.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector), Theme.RIPPLE_MASK_ROUNDRECT_6DP));
        stickerTab.setPadding(0, AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8));
        stickerTab.setOnClickListener(v -> openStickersView());
        stickerTab.setTextColor(Color.WHITE);
        stickerTab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        stickerTab.setGravity(Gravity.CENTER_HORIZONTAL);
        stickerTab.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        stickerTab.setAlpha(0.6f);
        stickerTab.setSingleLine();
        tabsLayout.addView(stickerTab, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1f));

        textTab = new TextView(context);
        textTab.setText(LocaleController.getString(R.string.PhotoEditorText).toUpperCase());
        textTab.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector), Theme.RIPPLE_MASK_ROUNDRECT_6DP));
        textTab.setPadding(0, AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8));
        textTab.setTextColor(Color.WHITE);
        textTab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        textTab.setGravity(Gravity.CENTER_HORIZONTAL);
        textTab.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textTab.setAlpha(0.6f);
        textTab.setSingleLine();
        textTab.setOnClickListener(v -> {
            switchTab(2);
            if (!(currentEntityView instanceof TextPaintView)) {
                forceChanges = true;
                createText(true);
            }
        });
        tabsLayout.addView(textTab, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1f));
    }

    private View getBarView(int index) {
        if (index == 0)
            return paintToolsView;
        if (index == 2)
            return textOptionsView;
        return null;
    }

    private void switchTab(int index) {
        if (tabsSelectedIndex == index || tabsNewSelectedIndex == index) {
            return;
        }
        if (tabsSelectionAnimator != null) {
            tabsSelectionAnimator.cancel();
        }

        final View view = getBarView(tabsSelectedIndex);
        tabsNewSelectedIndex = index;
        final View newView = getBarView(tabsNewSelectedIndex);

        tabsSelectionAnimator = ValueAnimator.ofFloat(0, 1).setDuration(300);
        tabsSelectionAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        tabsSelectionAnimator.addUpdateListener(animation -> {
            tabsSelectionProgress = (float) animation.getAnimatedValue();
            tabsLayout.invalidate();
            bottomLayout.invalidate();
            overlayLayout.invalidate();

            for (int i = 0; i < tabsLayout.getChildCount(); i++) {
                tabsLayout.getChildAt(i).setAlpha(0.6f + 0.4f * (i == tabsNewSelectedIndex ? tabsSelectionProgress : i == tabsSelectedIndex ? 1f - tabsSelectionProgress : 0f));
            }
            float pr = CubicBezierInterpolator.DEFAULT.getInterpolation(tabsSelectionProgress);

            if (view != null && newView != null) {
                float scale = 0.6f + 0.4f * (1f - pr);
                view.setScaleX(scale);
                view.setScaleY(scale);
                view.setTranslationY(AndroidUtilities.dp(16) * Math.min(pr, 0.25f) / 0.25f);
                view.setAlpha(1f - Math.min(pr, 0.25f) / 0.25f);

                scale = 0.6f + 0.4f * pr;
                newView.setScaleX(scale);
                newView.setScaleY(scale);
                newView.setTranslationY(-AndroidUtilities.dp(16) * Math.min(1f - pr, 0.25f) / 0.25f);
                newView.setAlpha(1f - Math.min(1f - pr, 0.25f) / 0.25f);
            }
        });
        tabsSelectionAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (view != null && newView != null) {
                    newView.setVisibility(VISIBLE);
                }
                if (index == 2) {
                    weightChooserView.setMinMax(0.5f, 2f);
                } else {
                    Brush brush = renderView.getCurrentBrush();
                    if (brush instanceof Brush.Blurer || brush instanceof Brush.Eraser) {
                        weightChooserView.setMinMax(0.4f, 1.75f);
                    } else {
                        weightChooserView.setMinMax(0.05f, 1f);
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tabsSelectedIndex = tabsNewSelectedIndex;
                tabsNewSelectedIndex = -1;
                tabsLayout.invalidate();

                if (view != null && newView != null) {
                    view.setVisibility(GONE);
                }

                if (animation == tabsSelectionAnimator) {
                    tabsSelectionAnimator = null;
                }
            }
        });
        tabsSelectionAnimator.start();
    }

    private EmojiBottomSheet emojiPopup;

    private void openStickersView() {
        final int wasSelectedIndex = tabsSelectedIndex;
        switchTab(1);
        postDelayed(() -> {
            if (facesBitmap != null) {
                detectFaces();
            }
        }, 350);
        EmojiBottomSheet alert = emojiPopup = new EmojiBottomSheet(getContext(), resourcesProvider) {
            @Override
            public void onDismissAnimationStart() {
                super.onDismissAnimationStart();
                switchTab(wasSelectedIndex);
            }
        };
        alert.setBlurDelegate(parent::drawBlurBitmap);
        alert.setOnGalleryClick(v -> {
            alert.dismiss();
            onGalleryClick();
        });
        alert.setOnDismissListener(di -> {
            emojiPopup = null;
            onOpenCloseStickersAlert(false);
            switchTab(wasSelectedIndex);
        });
        alert.whenSelected(document -> {
            forceChanges = true;
            appearAnimation(createSticker(null, document, false));
        });
        alert.show();
        onOpenCloseStickersAlert(true);
    }

    protected void onOpenCloseStickersAlert(boolean open) {}

    protected void onTextAdd() {}

    @Override
    public void requestLayout() {
        if (ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ignoreLayout = true;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);

        float bitmapW;
        float bitmapH;
        int fullHeight = AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight() - getAdditionalTop() - getAdditionalBottom();
        int maxHeight = fullHeight - AndroidUtilities.dp(48);
        if (bitmapToEdit != null) {
            bitmapW = bitmapToEdit.getWidth();
            bitmapH = bitmapToEdit.getHeight();
        } else {
            bitmapW = width;
            bitmapH = height - ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(48);
        }

        float renderWidth = width;
        float renderHeight = (float) Math.floor(renderWidth * bitmapH / bitmapW);
        if (renderHeight > maxHeight) {
            renderHeight = maxHeight;
            renderWidth = (float) Math.floor(renderHeight * bitmapW / bitmapH);
        }

//        renderView.measure(MeasureSpec.makeMeasureSpec((int) renderWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec((int) renderHeight, MeasureSpec.EXACTLY));
//        renderInputView.measure(MeasureSpec.makeMeasureSpec((int) renderWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec((int) renderHeight, MeasureSpec.EXACTLY));

        baseScale = renderWidth / paintingSize.width;
//        entitiesView.setScaleX(baseScale);
//        entitiesView.setScaleY(baseScale);
//        entitiesView.measure(MeasureSpec.makeMeasureSpec((int) paintingSize.width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec((int) paintingSize.height, MeasureSpec.EXACTLY));
        if (currentEntityView != null) {
            currentEntityView.updateSelectionView();
        }
//        selectionContainerView.measure(MeasureSpec.makeMeasureSpec((int) renderWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec((int) renderHeight, MeasureSpec.EXACTLY));
        measureChild(bottomLayout, widthMeasureSpec, heightMeasureSpec);
        measureChild(weightChooserView, widthMeasureSpec, heightMeasureSpec);
        measureChild(pipetteContainerLayout, widthMeasureSpec, heightMeasureSpec);
        int keyboardPad = Math.max(emojiPadding - parent.getPaddingUnderContainer(), measureKeyboardHeight());
        measureChild(overlayLayout, widthMeasureSpec, MeasureSpec.makeMeasureSpec(height - keyboardPad, MeasureSpec.EXACTLY));

        topLayout.setPadding(topLayout.getPaddingLeft(), AndroidUtilities.dp(12), topLayout.getPaddingRight(), topLayout.getPaddingBottom());
        measureChild(topLayout, widthMeasureSpec, heightMeasureSpec);
        ignoreLayout = false;

        int keyboardSize = 0;
        if (!waitingForKeyboardOpen && keyboardSize <= AndroidUtilities.dp(20) && !emojiViewVisible && !isAnimatePopupClosing) {
            ignoreLayout = true;
            hideEmojiView();
            ignoreLayout = false;
        }

        if (keyboardSize <= AndroidUtilities.dp(20)) {

        } else {
            hideEmojiView();
        }

//        if (emojiView != null) {
//            measureChild(emojiView, widthMeasureSpec, heightMeasureSpec);
//        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int width = right - left;
        int height = bottom - top;

//        if (emojiView != null) {
//            emojiView.layout(0, height - emojiView.getMeasuredHeight(), emojiView.getMeasuredWidth(), height);
//        }

//        int status = (Build.VERSION.SDK_INT >= 21 && !inBubbleMode ? AndroidUtilities.statusBarHeight : 0);
//        int actionBarHeight = ActionBar.getCurrentActionBarHeight();
//        int actionBarHeight2 = actionBarHeight + status;
//
//        int x = (int) Math.ceil((width - renderView.getMeasuredWidth()) / 2f);
//        int y = (height - actionBarHeight2 - AndroidUtilities.dp(48) - renderView.getMeasuredHeight()) / 2 + AndroidUtilities.dp(8) + status + (getAdditionalTop() - getAdditionalBottom()) / 2;

//        renderView.layout(x, y, x + renderView.getMeasuredWidth(), y + renderView.getMeasuredHeight());
//        renderInputView.layout(x, y, x + renderInputView.getMeasuredWidth(), y + renderInputView.getMeasuredHeight());
//        int x2 = x + (renderView.getMeasuredWidth() - entitiesView.getMeasuredWidth()) / 2;
//        int y2 = y + (renderView.getMeasuredHeight() - entitiesView.getMeasuredHeight()) / 2;
//        entitiesView.layout(x2, y2, x2 + entitiesView.getMeasuredWidth(), y2 + entitiesView.getMeasuredHeight());
//        selectionContainerView.layout(x, y, x + selectionContainerView.getMeasuredWidth(), y + selectionContainerView.getMeasuredHeight());
    }

    private Size getPaintingSize() {
        if (paintingSize != null) {
            return paintingSize;
        }
        return paintingSize = new Size(1080, 1920);
    }

    @Override
    public void init() {
        entitiesView.setVisibility(VISIBLE);
        renderView.setVisibility(View.VISIBLE);
        renderInputView.setVisibility(View.VISIBLE);
    }

    private void setupEntities() {
        if (initialEntities != null) {
            for (int a = 0, N = initialEntities.size(); a < N; a++) {
                VideoEditedInfo.MediaEntity entity = initialEntities.get(a);
                EntityView view;
                if (entity.type == VideoEditedInfo.MediaEntity.TYPE_STICKER) {
                    StickerView stickerView = createSticker(entity.parentObject, entity.document, false);
                    if ((entity.subType & 2) != 0) {
                        stickerView.mirror();
                    }
                    view = stickerView;
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.width = entity.viewWidth;
                    layoutParams.height = entity.viewHeight;
                } else if (entity.type == VideoEditedInfo.MediaEntity.TYPE_TEXT) {
                    TextPaintView textPaintView = createText(false);
                    textPaintView.setType(entity.subType);
                    textPaintView.setTypeface(entity.textTypeface);
                    textPaintView.setBaseFontSize(entity.fontSize);
                    SpannableString text = new SpannableString(entity.text);
                    for (VideoEditedInfo.EmojiEntity e : entity.entities) {
                        text.setSpan(new AnimatedEmojiSpan(e.document_id, 1f, textPaintView.getFontMetricsInt()), e.offset, e.offset + e.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    CharSequence charSequence = text;
                    charSequence = Emoji.replaceEmoji(charSequence, textPaintView.getFontMetricsInt(), (int) (textPaintView.getFontSize() * .8f), false);
                    if (charSequence instanceof Spanned) {
                        Emoji.EmojiSpan[] spans = ((Spanned) charSequence).getSpans(0, charSequence.length(), Emoji.EmojiSpan.class);
                        if (spans != null) {
                            for (int i = 0; i < spans.length; ++i) {
                                spans[i].scale = .85f;
                            }
                        }
                    }
                    textPaintView.setText(charSequence);
                    setTextAlignment(textPaintView, entity.textAlign);
                    Swatch swatch = textPaintView.getSwatch();
                    swatch.color = entity.color;
                    textPaintView.setSwatch(swatch);
                    view = textPaintView;
                } else if (entity.type == VideoEditedInfo.MediaEntity.TYPE_PHOTO) {
                    PhotoView photoView = createPhoto(entity.text, false);
                    if ((entity.subType & 2) != 0) {
                        photoView.mirror();
                    }
                    view = photoView;
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.width = entity.viewWidth;
                    layoutParams.height = entity.viewHeight;
                } else {
                    continue;
                }
                view.setX(entity.x * w - entity.viewWidth * (1 - entity.scale) / 2);
                view.setY(entity.y * h - entity.viewHeight * (1 - entity.scale) / 2);
                view.setPosition(new Point(view.getX() + entity.viewWidth / 2f, view.getY() + entity.viewHeight / 2f));
                view.setScaleX(entity.scale);
                view.setScaleY(entity.scale);
                view.setRotation((float) (-entity.rotation / Math.PI * 180));
            }
            initialEntities = null;
            entitiesView.setVisibility(View.VISIBLE);
        }
    }

//    private int getFrameRotation() {
//        switch (originalBitmapRotation) {
//            case 90: return Frame.ROTATION_90;
//            case 180: return Frame.ROTATION_180;
//            case 270: return Frame.ROTATION_270;
//            default: return Frame.ROTATION_0;
//        }
//    }

    private boolean isSidewardOrientation() {
        return originalBitmapRotation % 360 == 90 || originalBitmapRotation % 360 == 270;
    }

    private void detectFaces() {
//        queue.postRunnable(() -> {
//            FaceDetector faceDetector = null;
//            try {
//                faceDetector = new FaceDetector.Builder(getContext())
//                        .setMode(FaceDetector.ACCURATE_MODE)
//                        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
//                        .setTrackingEnabled(false).build();
//                if (!faceDetector.isOperational()) {
//                    if (BuildVars.LOGS_ENABLED) {
//                        FileLog.e("face detection is not operational");
//                    }
//                    return;
//                }
//
//                Frame frame = new Frame.Builder().setBitmap(facesBitmap).setRotation(getFrameRotation()).build();
//                SparseArray<Face> faces;
//                try {
//                    faces = faceDetector.detect(frame);
//                } catch (Throwable e) {
//                    FileLog.e(e);
//                    return;
//                }
//                ArrayList<PhotoFace> result = new ArrayList<>();
//                Size targetSize = getPaintingSize();
//                for (int i = 0; i < faces.size(); i++) {
//                    int key = faces.keyAt(i);
//                    Face f = faces.get(key);
//                    PhotoFace face = new PhotoFace(f, facesBitmap, targetSize, isSidewardOrientation());
//                    if (face.isSufficient()) {
//                        result.add(face);
//                    }
//                }
//                PaintView.this.faces = result;
//            } catch (Exception e) {
//                FileLog.e(e);
//            } finally {
//                if (faceDetector != null) {
//                    faceDetector.release();
//                }
//            }
//        }, 200);
    }

    @Override
    public void shutdown() {
        renderView.shutdown();
        entitiesView.setVisibility(GONE);
        selectionContainerView.setVisibility(GONE);

        queue.postRunnable(() -> {
            Looper looper = Looper.myLooper();
            if (looper != null) {
                looper.quit();
            }
        });

        if (emojiPopup != null) {
            emojiPopup.dismiss();
        }
        if (colorPickerBottomSheet != null) {
            colorPickerBottomSheet.dismiss();
        }
    }

    @Override
    public void onResume() {
        renderView.redraw();
    }

    @Override
    public void setOffsetTranslationY(float y, float progress, int keyboardHeight, boolean isPan) {

    }

    @Override
    public float getOffsetTranslationY() {
        return offsetTranslationY;
    }

    @Override
    public void updateColors() {
        toolsPaint.setColor(0xff191919);
    }

    private boolean forceChanges;

    @Override
    public boolean hasChanges() {
        return undoStore.canUndo() || forceChanges;
    }

    @Override
    public Bitmap getBitmap(ArrayList<VideoEditedInfo.MediaEntity> entities, Bitmap[] thumbBitmap) {
        return getBitmap(entities, (int) paintingSize.width, (int) paintingSize.height, true, true);
    }

    public Bitmap getBitmap(ArrayList<VideoEditedInfo.MediaEntity> entities, int resultWidth, int resultHeight, boolean drawPaint, boolean drawEntities) {
        Bitmap bitmap = drawPaint ? renderView.getResultBitmap() : null;
        lcm = BigInteger.ONE;
        if ((bitmap != null || !drawEntities) && entitiesView.entitiesCount() > 0) {
            Canvas canvas;
            Canvas thumbCanvas = null;
            int count = entitiesView.getChildCount();
            for (int i = 0; i < count; i++) {
                boolean skipDrawToBitmap = false;
                View v = entitiesView.getChildAt(i);
                if (!(v instanceof EntityView)) {
                    continue;
                }
                EntityView entity = (EntityView) v;
                Point position = entity.getPosition();
                VideoEditedInfo.MediaEntity mediaEntity = new VideoEditedInfo.MediaEntity();
                if (entities != null) {
                    if (entity instanceof TextPaintView) {
                        mediaEntity.type = VideoEditedInfo.MediaEntity.TYPE_TEXT;
                        TextPaintView textPaintView = (TextPaintView) entity;
                        CharSequence text = textPaintView.getText();
                        if (text instanceof Spanned) {
                            Spanned spanned = (Spanned) text;
                            AnimatedEmojiSpan[] spans = spanned.getSpans(0, text.length(), AnimatedEmojiSpan.class);
                            if (spans != null) {
                                for (int j = 0; j < spans.length; ++j) {
                                    AnimatedEmojiSpan span = spans[j];
                                    TLRPC.Document document = span.document;
                                    if (document == null) {
                                        document = AnimatedEmojiDrawable.findDocument(currentAccount, span.getDocumentId());
                                    }
                                    if (document != null) {
                                        AnimatedEmojiDrawable.getDocumentFetcher(currentAccount).putDocument(document);
                                    }

                                    VideoEditedInfo.EmojiEntity tlentity = new VideoEditedInfo.EmojiEntity();
                                    tlentity.document_id = span.getDocumentId();
                                    tlentity.document = document;
                                    tlentity.offset = spanned.getSpanStart(span);
                                    tlentity.length = spanned.getSpanEnd(span) - tlentity.offset;
                                    tlentity.documentAbsolutePath = FileLoader.getInstance(currentAccount).getPathToAttach(document, true).getAbsolutePath();
                                    int p = 0;
                                    while (document != null && document.thumbs != null && !document.thumbs.isEmpty() && !new File(tlentity.documentAbsolutePath).exists()) {
                                        tlentity.documentAbsolutePath = FileLoader.getInstance(currentAccount).getPathToAttach(document.thumbs.get(p), true).getAbsolutePath();
                                        p++;
                                        if (p >= document.thumbs.size()) {
                                            break;
                                        }
                                    }
                                    boolean isAnimatedSticker = MessageObject.isAnimatedStickerDocument(tlentity.document, true);
                                    if (isAnimatedSticker || MessageObject.isVideoStickerDocument(tlentity.document)) {
                                        tlentity.subType |= isAnimatedSticker ? 1 : 4;
                                    }
                                    if (MessageObject.isTextColorEmoji(tlentity.document)) {
                                        tlentity.subType |= 8;
                                    }
                                    mediaEntity.entities.add(tlentity);

                                    if (document != null) {
                                        long duration = 5000; // TODO(dkaraush)
                                        if (duration != 0) {
                                            BigInteger x = BigInteger.valueOf(duration);
                                            lcm = lcm.multiply(x).divide(lcm.gcd(x));
                                        }
                                    }
                                }
                            }
                        }
                        mediaEntity.text = text.toString();
                        mediaEntity.subType = (byte) textPaintView.getType();
                        mediaEntity.color = textPaintView.getSwatch().color;
                        mediaEntity.fontSize = textPaintView.getTextSize();
                        mediaEntity.textTypeface = textPaintView.getTypeface();
                        mediaEntity.textAlign = textPaintView.getAlign();
                    } else if (entity instanceof StickerView) {
                        mediaEntity.type = VideoEditedInfo.MediaEntity.TYPE_STICKER;
                        StickerView stickerView = (StickerView) entity;
                        Size size = stickerView.getBaseSize();
                        mediaEntity.width = size.width;
                        mediaEntity.height = size.height;
                        mediaEntity.document = stickerView.getSticker();
                        mediaEntity.parentObject = stickerView.getParentObject();
                        TLRPC.Document document = stickerView.getSticker();
                        mediaEntity.text = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(document, true).getAbsolutePath();
                        if (MessageObject.isAnimatedStickerDocument(document, true) || MessageObject.isVideoStickerDocument(document)) {
                            boolean isAnimatedSticker = MessageObject.isAnimatedStickerDocument(document, true);
                            mediaEntity.subType |= isAnimatedSticker ? 1 : 4;
                            long duration;
                            if (isAnimatedSticker) {
                                duration = stickerView.getDuration();
                            } else {
                                duration = 5000;
                            }
                            if (duration != 0) {
                                BigInteger x = BigInteger.valueOf(duration);
                                lcm = lcm.multiply(x).divide(lcm.gcd(x));
                            }
                        }
                        if (MessageObject.isTextColorEmoji(document)) {
                            mediaEntity.color = 0xFFFFFFFF;
                            mediaEntity.subType |= 8;
                        }
                        if (stickerView.isMirrored()) {
                            mediaEntity.subType |= 2;
                        }
                    } else if (entity instanceof PhotoView) {
                        PhotoView photoView = (PhotoView) entity;
                        mediaEntity.type = VideoEditedInfo.MediaEntity.TYPE_PHOTO;
                        Size size = photoView.getBaseSize();
                        mediaEntity.width = size.width;
                        mediaEntity.height = size.height;
                        mediaEntity.text = photoView.getPath();
                        if (photoView.isMirrored()) {
                            mediaEntity.subType |= 2;
                        }
                    } else {
                        continue;
                    }
                    entities.add(mediaEntity);
                    float scaleX = v.getScaleX();
                    float scaleY = v.getScaleY();
                    float x = v.getX();
                    float y = v.getY();
                    mediaEntity.viewWidth = v.getWidth();
                    mediaEntity.viewHeight = v.getHeight();
                    mediaEntity.width = v.getWidth() * scaleX / (float) entitiesView.getMeasuredWidth();
                    mediaEntity.height = v.getHeight() * scaleY / (float) entitiesView.getMeasuredHeight();
                    mediaEntity.x = (x + v.getWidth() * (1 - scaleX) / 2) / entitiesView.getMeasuredWidth();
                    mediaEntity.y = (y + v.getHeight() * (1 - scaleY) / 2) / entitiesView.getMeasuredHeight();
                    mediaEntity.rotation = (float) (-v.getRotation() * (Math.PI / 180));

                    mediaEntity.textViewX = (x + v.getWidth() / 2f) / (float) entitiesView.getMeasuredWidth();
                    mediaEntity.textViewY = (y + v.getHeight() / 2f) / (float) entitiesView.getMeasuredHeight();
                    mediaEntity.textViewWidth = mediaEntity.viewWidth / (float) entitiesView.getMeasuredWidth();
                    mediaEntity.textViewHeight = mediaEntity.viewHeight / (float) entitiesView.getMeasuredHeight();
                    mediaEntity.scale = scaleX;

                    if (entity instanceof StickerView) {
                        final float a = ((StickerView) entity).centerImage.getImageAspectRatio();
                        final float cx = mediaEntity.x + mediaEntity.width / 2f;
                        final float cy = mediaEntity.y + mediaEntity.height / 2f;
                        final float A = (float) entitiesView.getMeasuredWidth() / (float) entitiesView.getMeasuredHeight();
                        if (a > 1) {
                            // a = width / height
                            mediaEntity.height = mediaEntity.width * A / a;
                            mediaEntity.viewHeight = (int) (mediaEntity.viewWidth / a);
                            mediaEntity.y = cy - mediaEntity.height / 2f;
                        } else if (a < 1) {
                            mediaEntity.width = mediaEntity.height / A * a;
                            mediaEntity.viewWidth = (int) (mediaEntity.viewHeight * a);
                            mediaEntity.x = cx - mediaEntity.width / 2f;
                        }
                    }
                }
                if (drawEntities && bitmap != null) {
                    canvas = new Canvas(bitmap);
                    final float s = bitmap.getWidth() / (float) entitiesView.getMeasuredWidth();
                    for (int k = 0; k < 2; k++) {
                        Canvas currentCanvas = k == 0 ? canvas : thumbCanvas;
                        if (currentCanvas == null || (k == 0 && skipDrawToBitmap)) {
                            continue;
                        }
                        currentCanvas.save();
                        currentCanvas.scale(s, s);
                        currentCanvas.translate(mediaEntity.x * entitiesView.getMeasuredWidth(), mediaEntity.y * entitiesView.getMeasuredHeight());
                        currentCanvas.scale(v.getScaleX(), v.getScaleY());
                        currentCanvas.rotate(v.getRotation(), mediaEntity.width / 2f / v.getScaleX() * entitiesView.getMeasuredWidth(), mediaEntity.height / 2f / v.getScaleY() * entitiesView.getMeasuredHeight());
//                        currentCanvas.translate(-entity.getWidth() / (float) entitiesView.getMeasuredWidth() * bitmap.getWidth() / 2f, -entity.getHeight() / (float) entitiesView.getMeasuredHeight() * bitmap.getHeight() / 2f);
                        if (v instanceof TextPaintView && v.getHeight() > 0 && v.getWidth() > 0) {
                            Bitmap b = Bitmaps.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas c = new Canvas(b);
                            v.draw(c);
                            currentCanvas.drawBitmap(b, null, new Rect(0, 0, b.getWidth(), b.getHeight()), null);
                            try {
                                c.setBitmap(null);
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                            b.recycle();
                        } else {
                            v.draw(currentCanvas);
                        }
                        currentCanvas.restore();
                    }
                }
            }
        }
        return bitmap;
    }

    @Override
    public void onCleanupEntities() {
        entitiesView.removeAllViews();
    }

    @Override
    public long getLcm() {
        return lcm.longValue();
    }

    @Override
    public View getDoneView() {
        return doneButton;
    }

    @Override
    public View getCancelView() {
        return cancelButton;
    }

    @Override
    public void maybeShowDismissalAlert(PhotoViewer photoViewer, Activity parentActivity, Runnable okRunnable) {
        if (isColorListShown) {
            showColorList(false);
            return;
        }

        if (emojiViewVisible) {
            hideEmojiPopup(true);
            return;
        }

        if (editingText) {
            selectEntity(null);
            return;
        }

        if (hasChanges()) {
            if (parentActivity == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity, resourcesProvider);
            builder.setMessage(LocaleController.getString("PhotoEditorDiscardAlert", R.string.PhotoEditorDiscardAlert));
            builder.setTitle(LocaleController.getString("DiscardChanges", R.string.DiscardChanges));
            builder.setPositiveButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), (dialogInterface, i) -> okRunnable.run());
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            photoViewer.showAlertDialog(builder);
        } else {
            okRunnable.run();
        }
    }

    public void maybeDismiss(Activity parentActivity, Runnable okRunnable) {
        if (isColorListShown) {
            showColorList(false);
            return;
        }

        if (emojiViewVisible) {
            hideEmojiPopup(true);
            return;
        }

        if (editingText) {
            selectEntity(null);
            return;
        }

        if (hasChanges()) {
            if (parentActivity == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity, resourcesProvider);
            builder.setMessage(LocaleController.getString("PhotoEditorDiscardAlert", R.string.PhotoEditorDiscardAlert));
            builder.setTitle(LocaleController.getString("DiscardChanges", R.string.DiscardChanges));
            builder.setPositiveButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), (dialogInterface, i) -> okRunnable.run());
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            builder.show();
        } else {
            okRunnable.run();
        }
    }

    @Override
    public boolean onTouch(MotionEvent ev) {
        if (currentEntityView != null) {
            selectEntity(null);
        }

//        float x2 = (ev.getX() - renderView.getTranslationX() - getMeasuredWidth() / 2f) / renderView.getScaleX();
//        float y2 = (ev.getY() - renderView.getTranslationY() - getMeasuredHeight() / 2f + AndroidUtilities.dp(32) - (getAdditionalTop() - getAdditionalBottom()) / 2f) / renderView.getScaleY();
//        float rotation = (float) Math.toRadians(-renderView.getRotation());
//        float x = (float) (x2 * Math.cos(rotation) - y2 * Math.sin(rotation)) + renderView.getMeasuredWidth() / 2f;
//        float y = (float) (x2 * Math.sin(rotation) + y2 * Math.cos(rotation)) + renderView.getMeasuredHeight() / 2f;

        float x = ev.getX(), y = ev.getY();

        MotionEvent event = MotionEvent.obtain(ev);
        event.setLocation(x, y);
        renderView.onTouch(event);
        event.recycle();
        return true;
    }

    public List<View> getPreviewViews() {
        return Arrays.asList(
            renderView,
            renderInputView,
            entitiesView,
            selectionContainerView
        );
    }

    public void clearSelection() {
        selectEntity(null);
    }

    public void openPaint() {
        switchTab(0);
        clearSelection();
    }

    public void openText() {
        switchTab(2);
        forceChanges = true;
        createText(true);
    }

    public void openStickers() {
        switchTab(1);
        openStickersView();
    }

    @Override
    public int getAdditionalTop() {
        return AndroidUtilities.dp(48);
    }

    @Override
    public int getAdditionalBottom() {
        return AndroidUtilities.dp(24);
    }

    @Override
    public RenderView getRenderView() {
        return renderView;
    }

    public View getTextDimView() {
        return textDim;
    }

    public View getRenderInputView() {
        return renderInputView;
    }

    public View getEntitiesView() {
        return entitiesView;
    }

    public View getSelectionEntitiesView() {
        return selectionContainerView;
    }

    @Override
    public void setTransform(float scale, float trX, float trY, float imageWidth, float imageHeight) {
//        this.scale = scale;
//        this.imageWidth = imageWidth;
//        this.imageHeight = imageHeight;
//        inputTransformX = trX;
//        inputTransformY = trY;
//        transformX = trX;
//        trY += panTranslationY;
//        transformY = trY;
//        for (int a = 0; a < 4; a++) {
//            View view;
//            float additionlScale = 1.0f;
//            if (a == 0) {
//                view = entitiesView;
//            } else if (a == 1) {
//                view = selectionContainerView;
//            } else if (a == 2) {
//                view = renderView;
//            } else {
//                view = renderInputView;
//            }
//            float tx;
//            float ty;
//            float rotation = 0;
//            if (currentCropState != null) {
//                additionlScale *= currentCropState.cropScale;
//
//                int w = view.getMeasuredWidth();
//                int h = view.getMeasuredHeight();
//                if (w == 0 || h == 0) {
//                    return;
//                }
//                int tr = currentCropState.transformRotation;
//                int fw = w, rotatedW = w;
//                int fh = h, rotatedH = h;
//                if (tr == 90 || tr == 270) {
//                    int temp = fw;
//                    fw = rotatedW = fh;
//                    fh = rotatedH = temp;
//                }
//                fw *= currentCropState.cropPw;
//                fh *= currentCropState.cropPh;
//
//                float sc = Math.max(imageWidth / fw, imageHeight / fh);
//                additionlScale *= sc;
//
//                tx = trX + currentCropState.cropPx * rotatedW * scale * sc * currentCropState.cropScale;
//                ty = trY + currentCropState.cropPy * rotatedH * scale * sc * currentCropState.cropScale;
//                rotation = currentCropState.cropRotate + tr;
//            } else {
//                if (a == 0) {
//                    additionlScale *= baseScale;
//                }
//                tx = trX;
//                ty = trY;
//            }
//            float finalScale = scale * additionlScale;
//            if (Float.isNaN(finalScale)) {
//                finalScale = 1f;
//            }
//            view.setScaleX(finalScale);
//            view.setScaleY(finalScale);
//            view.setTranslationX(tx);
//            view.setTranslationY(ty);
//            view.setRotation(rotation);
//            view.invalidate();
//        }
//        invalidate();
    }

    @Override
    public List<TLRPC.InputDocument> getMasks() {
        ArrayList<TLRPC.InputDocument> result = null;
        int count = entitiesView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = entitiesView.getChildAt(a);
            if (child instanceof StickerView) {
                TLRPC.Document document = ((StickerView) child).getSticker();
                if (result == null) {
                    result = new ArrayList<>();
                }
                TLRPC.TL_inputDocument inputDocument = new TLRPC.TL_inputDocument();
                inputDocument.id = document.id;
                inputDocument.access_hash = document.access_hash;
                inputDocument.file_reference = document.file_reference;
                if (inputDocument.file_reference == null) {
                    inputDocument.file_reference = new byte[0];
                }
                result.add(inputDocument);
            } else if (child instanceof TextPaintView) {
                TextPaintView textPaintView = (TextPaintView) child;
                CharSequence text = textPaintView.getText();
                if (text instanceof Spanned) {
                    AnimatedEmojiSpan[] spans = ((Spanned) text).getSpans(0, text.length(), AnimatedEmojiSpan.class);
                    if (spans != null) {
                        for (int i = 0; i < spans.length; ++i) {
                            AnimatedEmojiSpan span = spans[i];
                            if (span != null) {
                                TLRPC.Document document;
                                if (span.document != null) {
                                    document = span.document;
                                } else {
                                    document = AnimatedEmojiDrawable.findDocument(currentAccount, span.getDocumentId());
                                }

                                if (document != null) {
                                    if (result == null) {
                                        result = new ArrayList<>();
                                    }
                                    TLRPC.TL_inputDocument inputDocument = new TLRPC.TL_inputDocument();
                                    inputDocument.id = document.id;
                                    inputDocument.access_hash = document.access_hash;
                                    inputDocument.file_reference = document.file_reference;
                                    if (inputDocument.file_reference == null) {
                                        inputDocument.file_reference = new byte[0];
                                    }
                                    result.add(inputDocument);
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void onBrushSelected(Brush brush) {
        if (brush instanceof Brush.Blurer || brush instanceof Brush.Eraser) {
            weightChooserView.setMinMax(0.4f, 1.75f);
        } else {
            weightChooserView.setMinMax(0.05f, 1f);
        }
        weightChooserView.setDrawCenter(!(brush instanceof Brush.Shape));
        if (renderView.getCurrentBrush() instanceof Brush.Shape) {
            ignoreToolChangeAnimationOnce = true;
        }
        renderView.setBrush(brush);
        colorSwatch.brushWeight = weightDefaultValueOverride.get();
        setCurrentSwatch(colorSwatch, true);
        renderInputView.invalidate();
    }

    @Override
    public void onTypefaceButtonClicked() {
        showTypefaceMenu(true);
    }

    private void showTypefaceMenu(boolean show) {
        if (isTypefaceMenuShown != show) {
            isTypefaceMenuShown = show;

            if (typefaceMenuTransformAnimation != null) {
                typefaceMenuTransformAnimation.cancel();
            }

            typefaceMenuTransformAnimation = new SpringAnimation(new FloatValueHolder(show ? 0 : 1000f));
            typefaceMenuTransformAnimation.setSpring(new SpringForce()
                    .setFinalPosition(show ? 1000f : 0f)
                    .setStiffness(1250f)
                    .setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY));

            if (show) {
                typefaceListView.setAlpha(0f);
                typefaceListView.setVisibility(VISIBLE);
            }

            typefaceMenuTransformAnimation.addUpdateListener((animation, value, velocity) -> {
                typefaceMenuTransformProgress = value / 1000f;
                typefaceListView.setAlpha(typefaceMenuTransformProgress);
                typefaceListView.invalidate();
                overlayLayout.invalidate();

                textOptionsView.getTypefaceCell().setAlpha(1f - typefaceMenuTransformProgress);
            });
            typefaceMenuTransformAnimation.addEndListener((animation, canceled, value, velocity) -> {
                if (animation == typefaceMenuTransformAnimation) {
                    typefaceMenuTransformAnimation = null;

                    if (!show) {
                        typefaceListView.setVisibility(GONE);
                    }
                    typefaceListView.setMaskProvider(null);
                }
            });
            typefaceMenuTransformAnimation.start();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showColorList(boolean show) {
        if (isColorListShown != show) {
            isColorListShown = show;

            if (toolsTransformAnimation != null) {
                toolsTransformAnimation.cancel();
            }

            toolsTransformAnimation = new SpringAnimation(new FloatValueHolder(show ? 0 : 1000f));
            toolsTransformAnimation.setSpring(new SpringForce()
                    .setFinalPosition(show ? 1000f : 0f)
                    .setStiffness(1250f)
                    .setDampingRatio(SpringForce.DAMPING_RATIO_NO_BOUNCY));

            boolean[] moveBottomLayout = new boolean[] { keyboardNotifier.keyboardVisible() || emojiPadding > 0 };
            float bottomLayoutTranslationY = bottomLayout.getTranslationY();
            float doneButtonAlpha = doneButton.getAlpha();

            View barView = getBarView();
            toolsTransformAnimation.addUpdateListener((animation, value, velocity) -> {
                toolsTransformProgress = value / 1000f;

                float scale = 0.6f + 0.4f * (1f - toolsTransformProgress);
                barView.setScaleX(scale);
                barView.setScaleY(scale);
                barView.setTranslationY(AndroidUtilities.dp(16) * Math.min(toolsTransformProgress, 0.25f) / 0.25f);
                barView.setAlpha(1f - Math.min(toolsTransformProgress, 0.25f) / 0.25f);

                colorsListView.setProgress(toolsTransformProgress, show);

                doneButton.setProgress(toolsTransformProgress);
                cancelButton.setProgress(toolsTransformProgress);

                tabsLayout.setTranslationY(AndroidUtilities.dp(32) * toolsTransformProgress);
                if (keyboardAnimator != null && keyboardAnimator.isRunning()) {
                    moveBottomLayout[0] = false;
                }
                if (moveBottomLayout[0]) {
                    float progress = show ? toolsTransformProgress : 1f - toolsTransformProgress;
                    doneButton.setAlpha(lerp(doneButtonAlpha, show ? 1f : 0f, progress));
                    cancelButton.setAlpha(lerp(doneButtonAlpha, show ? 1f : 0f, progress));
                    bottomLayout.setTranslationY(bottomLayoutTranslationY - (AndroidUtilities.dp(39)) * progress * (show ? 1 : -1));
                }
                bottomLayout.invalidate();

                if (barView == textOptionsView) {
                    overlayLayout.invalidate();
                }
            });
            toolsTransformAnimation.addEndListener((animation, canceled, value, velocity) -> {
                if (animation == toolsTransformAnimation) {
                    toolsTransformAnimation = null;

                    if (!show) {
                        colorsListView.setVisibility(GONE);
                        PersistColorPalette.getInstance(currentAccount).saveColors();
                        colorsListView.getAdapter().notifyDataSetChanged();
                    }
                }
            });
            toolsTransformAnimation.start();

            if (show) {
                colorsListView.setVisibility(VISIBLE);
                colorsListView.setSelectedColorIndex(0);
            }
        }
    }

    private void setCurrentSwatch(Swatch swatch, boolean updateInterface) {
        if (colorSwatch != swatch) {
            colorSwatch.color = swatch.color;
            colorSwatch.colorLocation = swatch.colorLocation;
            colorSwatch.brushWeight = swatch.brushWeight;

            PersistColorPalette.getInstance(currentAccount).selectColor(swatch.color);
            PersistColorPalette.getInstance(currentAccount).setCurrentWeight(swatch.brushWeight);
        }

        renderView.setColor(swatch.color);
        renderView.setBrushSize(swatch.brushWeight);

        if (updateInterface) {
            if (bottomLayout != null) {
                bottomLayout.invalidate();
            }
        }

        if (currentEntityView instanceof TextPaintView) {
            ((TextPaintView) currentEntityView).setSwatch(new Swatch(swatch.color, swatch.colorLocation, swatch.brushWeight));
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isColorListShown) {
            showColorList(false);
            return true;
        }

        if (emojiViewVisible) {
            hideEmojiPopup(true);
            return true;
        }

        if (editingText) {
            if (enteredThroughText) {
                enteredThroughText = false;
                keyboardNotifier.ignore(true);
                return false;
            }
            selectEntity(null);
            return true;
        }

        return false;
    }

    @Override
    public void onColorPickerSelected() {
        showColorList(true);
    }

    @Override
    public void onTextOutlineSelected(View v) {
        setTextType((selectedTextType + 1) % 4);
    }

    private PaintView.PopupButton buttonForPopup(String text, int icon, boolean selected, Runnable onClick) {
        PaintView.PopupButton button = new PaintView.PopupButton(getContext());
        button.setIcon(icon);
        button.setText(text);
        button.setSelected(selected);
        if (onClick != null) {
            button.setOnClickListener(e -> onClick.run());
        }
        return button;
    }

    public class PopupButton extends LinearLayout {

        public TextView textView;
        FrameLayout imagesView;
        ImageView imageView;
        ImageView image2View;

        float imageSwitchT;
        boolean imageSwitchFill;
        ValueAnimator imageSwitchAnimator;

        ImageView checkView;

        public PopupButton(Context context) {
            super(context);
            setOrientation(LinearLayout.HORIZONTAL);
            setBackground(Theme.getSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider), false));

            imagesView = new FrameLayout(context) {
                Path path = new Path();
                @Override
                protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                    if (imageSwitchAnimator != null) {
                        if (imageSwitchFill && child == image2View || !imageSwitchFill && child == imageView) {
                            float r = (imageSwitchFill ? imageSwitchT : 1f - imageSwitchT) * getMeasuredWidth() / 2f;
                            canvas.save();
                            path.rewind();
                            path.addCircle(getMeasuredWidth() / 2f, getMeasuredHeight() / 2f, r, Path.Direction.CW);
                            canvas.clipPath(path);
                            boolean res = super.drawChild(canvas, child, drawingTime);
                            canvas.restore();
                            return res;
                        }
                    }
                    return super.drawChild(canvas, child, drawingTime);
                }
            };

            addView(imagesView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 16, 0, 16, 0));

            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setColorFilter(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
            imagesView.addView(imageView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
            image2View = new ImageView(context);
            image2View.setScaleType(ImageView.ScaleType.CENTER);
            image2View.setColorFilter(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
            image2View.setVisibility(View.GONE);
            imagesView.addView(image2View, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

            textView = new TextView(context);
            textView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            addView(textView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));

            checkView = new ImageView(context);
            checkView.setImageResource(R.drawable.msg_text_check);
            checkView.setScaleType(ImageView.ScaleType.CENTER);
            checkView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_radioBackgroundChecked), PorterDuff.Mode.MULTIPLY));
            checkView.setVisibility(View.GONE);
            addView(checkView, LayoutHelper.createLinear(50, LayoutHelper.MATCH_PARENT));
        }

        public void setSelected(boolean selected) {
            checkView.setVisibility(selected ? View.VISIBLE : View.GONE);
        }

        public void setText(CharSequence text) {
            textView.setText(text);
        }

        public void setIcon(int resId) {
            setIcon(resId, true,false);
        }

        public void setIcon(int resId, boolean fillup, boolean animated) {
            if (animated) {
                if (imageSwitchAnimator != null) {
                    imageSwitchAnimator.cancel();
                    imageSwitchAnimator = null;
                    setIcon(resId, false, false);
                    return;
                }
                imageSwitchFill = fillup;
                image2View.setImageResource(resId);
                image2View.setVisibility(View.VISIBLE);
                image2View.setAlpha(1f);
                imageSwitchAnimator = ValueAnimator.ofFloat(0, 1);
                imageSwitchAnimator.addUpdateListener(anm -> {
                    imageSwitchT = (float) anm.getAnimatedValue();
                    if (!fillup) {
                        imageView.setAlpha(1f - imageSwitchT);
                    }
                    imagesView.invalidate();
                });
                imageSwitchAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ImageView buff = imageView;
                        imageView = image2View;
                        image2View = buff;
                        image2View.bringToFront();
                        image2View.setVisibility(View.GONE);
                        imageSwitchAnimator = null;
                    }
                });
                imageSwitchAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                imageSwitchAnimator.setDuration(420);
                imageSwitchAnimator.start();
            } else {
                imageView.setImageResource(resId);
            }
        }

        @Override
        public boolean performClick() {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss(true);
            }
            return super.performClick();
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return true;
        }
    }

    private void setTextType(int type) {
        selectedTextType = type;
        if (currentEntityView instanceof TextPaintView) {
            ((TextPaintView) currentEntityView).setType(type);
        }
        PersistColorPalette.getInstance(currentAccount).setCurrentTextType(type);
        textOptionsView.setOutlineType(type, true);
    }

    @Override
    public void onNewTextSelected() {
        if (keyboardVisible || emojiViewVisible) {
            onEmojiButtonClick();
        } else {
            forceChanges = true;
            createText(true);
        }
    }

    @Override
    public void onTypefaceSelected(PaintTypeface typeface) {
        PersistColorPalette.getInstance(currentAccount).setCurrentTypeface(typeface.getKey());
        if (currentEntityView instanceof TextPaintView) {
            ((TextPaintView) currentEntityView).setTypeface(typeface);
        }
    }

    @Override
    public void onTextAlignmentSelected(int align) {
        if (currentEntityView instanceof TextPaintView) {
            setTextAlignment((TextPaintView) currentEntityView, align);
            PersistColorPalette.getInstance(currentAccount).setCurrentAlignment(align);
        }
    }

    private void setTextAlignment(TextPaintView textPaintView, int align) {
        textPaintView.setAlign(align);

        int gravity;
        switch (align) {
            default:
            case PaintTextOptionsView.ALIGN_LEFT:
                gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                break;
            case PaintTextOptionsView.ALIGN_CENTER:
                gravity = Gravity.CENTER;
                break;
            case PaintTextOptionsView.ALIGN_RIGHT:
                gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                break;
        }

        textPaintView.getEditText().setGravity(gravity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            int textAlign;
            switch (align) {
                default:
                case PaintTextOptionsView.ALIGN_LEFT:
                    textAlign = LocaleController.isRTL ? TEXT_ALIGNMENT_TEXT_END : TEXT_ALIGNMENT_TEXT_START;
                    break;
                case PaintTextOptionsView.ALIGN_CENTER:
                    textAlign = TEXT_ALIGNMENT_CENTER;
                    break;
                case PaintTextOptionsView.ALIGN_RIGHT:
                    textAlign = LocaleController.isRTL ? TEXT_ALIGNMENT_TEXT_START : TEXT_ALIGNMENT_TEXT_END;
                    break;
            }
            textPaintView.getEditText().setTextAlignment(textAlign);
        }
    }

    @Override
    public void onAddButtonPressed(View btn) {
        showPopup(() -> {
            boolean fill = PersistColorPalette.getInstance(currentAccount).getFillShapes();
            for (int a = 0; a < Brush.Shape.SHAPES_LIST.size(); a++) {
                final Brush.Shape shape = Brush.Shape.SHAPES_LIST.get(a);
                int icon = fill ? shape.getFilledIconRes() : shape.getIconRes();
                PaintView.PopupButton button = buttonForPopup(shape.getShapeName(), icon, false, () -> {
                    if (renderView.getCurrentBrush() instanceof Brush.Shape) {
                        ignoreToolChangeAnimationOnce = true;
                    }
                    onBrushSelected(shape);
                    paintToolsView.animatePlusToIcon(icon);
                });
                button.setOnLongClickListener(e -> {
                    if (popupLayout != null) {
                        PersistColorPalette.getInstance(currentAccount).toggleFillShapes();
                        boolean newfill = PersistColorPalette.getInstance(currentAccount).getFillShapes();
                        for (int i = 0; i < popupLayout.getItemsCount(); ++i) {
                            View child = popupLayout.getItemAt(i);
                            if (child instanceof PaintView.PopupButton) {
                                final Brush.Shape thisshape = Brush.Shape.SHAPES_LIST.get(i);
                                int thisicon = newfill ? thisshape.getFilledIconRes() : thisshape.getIconRes();
                                ((PaintView.PopupButton) child).setIcon(thisicon, newfill, true);
                            }
                        }
                    }
                    return true;
                });
                popupLayout.addView(button, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48));
            }
        }, this, Gravity.RIGHT | Gravity.TOP, 0, getHeight());
    }

    private void showMenuForEntity(final EntityView entityView) {
        int[] pos = getCenterLocationInWindow(entityView);
        int x = pos[0];
        int y = pos[1] - AndroidUtilities.dp(32);

        showPopup(() -> {
            LinearLayout parent = new LinearLayout(getContext());
            parent.setOrientation(LinearLayout.HORIZONTAL);

            TextView deleteView = new TextView(getContext());
            deleteView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
            deleteView.setBackground(Theme.getSelectorDrawable(false));
            deleteView.setGravity(Gravity.CENTER_VERTICAL);
            deleteView.setEllipsize(TextUtils.TruncateAt.END);
            deleteView.setPadding(AndroidUtilities.dp(16), 0, AndroidUtilities.dp(14), 0);
            deleteView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            deleteView.setTag(0);
            deleteView.setText(LocaleController.getString("PaintDelete", R.string.PaintDelete));
            deleteView.setOnClickListener(v -> {
                removeEntity(entityView);

                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss(true);
                }
            });
            parent.addView(deleteView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, 48));

            if (entityView instanceof TextPaintView) {
                TextView editView = new TextView(getContext());
                editView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
                editView.setBackground(Theme.getSelectorDrawable(false));
                editView.setGravity(Gravity.CENTER_VERTICAL);
                editView.setEllipsize(TextUtils.TruncateAt.END);
                editView.setPadding(AndroidUtilities.dp(16), 0, AndroidUtilities.dp(16), 0);
                editView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                if ((keyboardNotifier.keyboardVisible() && !keyboardNotifier.ignoring) || emojiPadding > 0) {
                    editView.setTag(3);
                    editView.setText(LocaleController.getString("Paste", R.string.Paste));
                    editView.setOnClickListener(v -> {
                        try {
                            EditText editText = ((TextPaintView) entityView).getEditText();
                            editText.onTextContextMenuItem(android.R.id.pasteAsPlainText);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                        if (popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss(true);
                        }
                    });
                } else {
                    editView.setTag(1);
                    editView.setText(LocaleController.getString("PaintEdit", R.string.PaintEdit));
                    editView.setOnClickListener(v -> {
                        selectEntity(entityView);
                        editSelectedTextEntity();
                        if (popupWindow != null && popupWindow.isShowing()) {
                            popupWindow.dismiss(true);
                        }
                    });
                }
                parent.addView(editView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, 48));
            }

            if (entityView instanceof StickerView || entityView instanceof PhotoView) {
                TextView flipView = new TextView(getContext());
                flipView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
                flipView.setBackground(Theme.getSelectorDrawable(false));
                flipView.setEllipsize(TextUtils.TruncateAt.END);
                flipView.setGravity(Gravity.CENTER_VERTICAL);
                flipView.setPadding(AndroidUtilities.dp(16), 0, AndroidUtilities.dp(16), 0);
                flipView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                flipView.setTag(4);
                flipView.setText(LocaleController.getString(R.string.Flip));
                flipView.setOnClickListener(v -> {
                    if (entityView instanceof StickerView) {
                        ((StickerView) entityView).mirror(true);
                    } else {
                        ((PhotoView) entityView).mirror(true);
                    }
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss(true);
                    }
                });
                parent.addView(flipView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, 48));
            }

            if (!(entityView instanceof PhotoView)) {
                TextView duplicateView = new TextView(getContext());
                duplicateView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
                duplicateView.setBackground(Theme.getSelectorDrawable(false));
                duplicateView.setEllipsize(TextUtils.TruncateAt.END);
                duplicateView.setGravity(Gravity.CENTER_VERTICAL);
                duplicateView.setPadding(AndroidUtilities.dp(14), 0, AndroidUtilities.dp(16), 0);
                duplicateView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                duplicateView.setTag(2);
                duplicateView.setText(LocaleController.getString("PaintDuplicate", R.string.PaintDuplicate));
                duplicateView.setOnClickListener(v -> {
                    duplicateEntity(entityView);

                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss(true);
                    }
                });
                parent.addView(duplicateView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, 48));
            }

            popupLayout.addView(parent);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) parent.getLayoutParams();
            params.width = LayoutHelper.WRAP_CONTENT;
            params.height = LayoutHelper.WRAP_CONTENT;
            parent.setLayoutParams(params);
        }, this, Gravity.LEFT | Gravity.TOP, x, y);
    }

    private void duplicateEntity(EntityView thisEntityView) {
        if (thisEntityView == null) {
            return;
        }

        EntityView entityView = null;
        Point position = startPositionRelativeToEntity(thisEntityView);

        if (thisEntityView instanceof StickerView) {
            StickerView newStickerView = new StickerView(getContext(), (StickerView) thisEntityView, position);
            newStickerView.setDelegate(this);
            entitiesView.addView(newStickerView);
            entityView = newStickerView;
        } else if (thisEntityView instanceof TextPaintView) {
            TextPaintView newTextPaintView = new TextPaintView(getContext(), (TextPaintView) thisEntityView, position);
            newTextPaintView.getEditText().betterFraming = true;
            newTextPaintView.setDelegate(this);
            newTextPaintView.setMaxWidth(w - AndroidUtilities.dp(7 + 7 + 18));
            entitiesView.addView(newTextPaintView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
            entityView = newTextPaintView;
        } else {
            return;
        }

        registerRemovalUndo(entityView);
        selectEntity(entityView);
    }

    private Point startPositionRelativeToEntity(EntityView entityView) {
        float offset = 200.0f;
        if (currentCropState != null) {
            offset /= currentCropState.cropScale;
        }

        if (entityView != null) {
            Point position = entityView.getPosition();
            return new Point(position.x, position.y + entityView.getHeight());
        } else {
            float minimalDistance = 100.0f;
            if (currentCropState != null) {
                minimalDistance /= currentCropState.cropScale;
            }
            Point position = centerPositionForEntity();
            while (true) {
                boolean occupied = false;
                for (int index = 0; index < entitiesView.getChildCount(); index++) {
                    View view = entitiesView.getChildAt(index);
                    if (!(view instanceof EntityView))
                        continue;

                    Point location = ((EntityView) view).getPosition();
                    float distance = (float) Math.sqrt(Math.pow(location.x - position.x, 2) + Math.pow(location.y - position.y, 2));
                    if (distance < minimalDistance) {
                        offset = view.getHeight();
                        occupied = true;
                    }
                }

                if (!occupied) {
                    break;
                } else {
                    position = new Point(position.x, position.y + offset);
                }
            }
            return position;
        }
    }

    private void showPopup(Runnable setupRunnable, View parent, int gravity, int x, int y) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return;
        }

        if (popupLayout == null) {
            popupRect = new android.graphics.Rect();
            popupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext(), resourcesProvider);
            popupLayout.setAnimationEnabled(true);
            popupLayout.setBackgroundColor(-14145495);
            popupLayout.setOnTouchListener((v, event) -> {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        v.getHitRect(popupRect);
                        if (!popupRect.contains((int) event.getX(), (int) event.getY())) {
                            popupWindow.dismiss();
                        }
                    }
                }
                return false;
            });
            popupLayout.setDispatchKeyEventListener(keyEvent -> {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0 && popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            });
            popupLayout.setShownFromBottom(true);
        }

        popupLayout.removeInnerViews();
        setupRunnable.run();

        if (popupWindow == null) {
            popupWindow = new ActionBarPopupWindow(popupLayout, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
            popupWindow.setAnimationEnabled(true);
            popupWindow.setAnimationStyle(R.style.PopupAnimation);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setClippingEnabled(true);
            popupWindow.setInputMethodMode(ActionBarPopupWindow.INPUT_METHOD_NOT_NEEDED);
            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
            popupWindow.getContentView().setFocusableInTouchMode(true);
            popupWindow.setOnDismissListener(() -> popupLayout.removeInnerViews());
        }

        popupLayout.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000), MeasureSpec.AT_MOST));

        popupWindow.setFocusable(true);

        if ((gravity & Gravity.TOP) != 0) {
            x -= popupLayout.getMeasuredWidth() / 2;
            y -= popupLayout.getMeasuredHeight();
        }
        popupWindow.showAtLocation(parent, gravity, x, y);
        popupWindow.startAnimation(popupLayout);
    }

    private int getThemedColor(int key) {
        return Theme.getColor(key, resourcesProvider);
    }

    @Override
    public PersistColorPalette onGetPalette() {
        return PersistColorPalette.getInstance(currentAccount);
    }

    private Size baseStickerSize() {
        float side = (float) Math.floor(getPaintingSize().width * 0.5);
        return new Size(side, side);
    }

    private Size basePhotoSize(String path) {
        float a = 1f;
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
            a = (float) opts.outWidth / opts.outHeight;
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (a > 1) {
            float side = (float) Math.floor(Math.max(w, entitiesView.getMeasuredWidth()) * 0.5);
            return new Size(side, side / a);
        } else {
            float side = (float) Math.floor(Math.max(h, entitiesView.getMeasuredHeight()) * 0.5);
            return new Size(side * a, side);
        }
    }

    public void appearAnimation(View view) {
        float scaleX = view.getScaleX(), scaleY = view.getScaleY();
        view.setScaleX(scaleX * .5f);
        view.setScaleY(scaleY * .5f);
        view.setAlpha(0f);
        view.animate().scaleX(scaleX).scaleY(scaleY).alpha(1f).setInterpolator(new OvershootInterpolator(3f)).setDuration(240).withEndAction(() -> {
            if (view instanceof EntityView) {
                ((EntityView) view).updateSelectionView();
                selectEntity((EntityView) view);
            }
        }).start();
    }

    private Point centerPositionForEntity() {
        int w = entitiesView.getMeasuredWidth(), h = entitiesView.getMeasuredHeight();
        if (w <= 0) w = this.w;
        if (h <= 0) h = this.h;
        float x = w / 2.0f;
        float y = h / 2.0f;
        return new Point(x, y);
    }

    private PaintView.StickerPosition calculateStickerPosition(TLRPC.Document document) {
        TLRPC.TL_maskCoords maskCoords = null;

        for (int a = 0; a < document.attributes.size(); a++) {
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeSticker) {
                maskCoords = attribute.mask_coords;
                break;
            }
        }

        float rotation;
        float baseScale;
        if (currentCropState != null) {
            rotation = -(currentCropState.transformRotation + currentCropState.cropRotate);
            baseScale = 0.75f / currentCropState.cropScale;
        } else {
            rotation = 0.0f;
            baseScale = 0.75f;
        }
        PaintView.StickerPosition defaultPosition = new PaintView.StickerPosition(centerPositionForEntity(), baseScale, rotation);
        return defaultPosition;
//        if (maskCoords == null || faces == null || faces.size() == 0) {
//            return defaultPosition;
//        } else {
//            int anchor = maskCoords.n;
//
//            PhotoFace face = getRandomFaceWithVacantAnchor(anchor, document.id, maskCoords);
//            if (face == null) {
//                return defaultPosition;
//            }
//
//            Point referencePoint = face.getPointForAnchor(anchor);
//            float referenceWidth = face.getWidthForAnchor(anchor);
//            float angle = face.getAngle();
//            Size baseSize = baseStickerSize();
//
//            float scale = (float) (referenceWidth / baseSize.width * maskCoords.zoom);
//
////            float radAngle = (float) Math.toRadians(angle);
////            float xCompX = (float) (Math.sin(Math.PI / 2.0f - radAngle) * referenceWidth * maskCoords.x);
////            float xCompY = (float) (Math.cos(Math.PI / 2.0f - radAngle) * referenceWidth * maskCoords.x);
////
////            float yCompX = (float) (Math.cos(Math.PI / 2.0f + radAngle) * referenceWidth * maskCoords.y);
////            float yCompY = (float) (Math.sin(Math.PI / 2.0f + radAngle) * referenceWidth * maskCoords.y);
//
//            float x = referencePoint.x;
//            float y = referencePoint.y;
//
//            return new PaintView.StickerPosition(new Point(x, y), scale, angle);
//        }
    }

//    private PhotoFace getRandomFaceWithVacantAnchor(int anchor, long documentId, TLRPC.TL_maskCoords maskCoords) {
//        if (anchor < 0 || anchor > 3 || faces.isEmpty()) {
//            return null;
//        }
//
//        int count = faces.size();
//        int randomIndex = Utilities.random.nextInt(count);
//        int remaining = count;
//
//        PhotoFace selectedFace = null;
//        for (int i = randomIndex; remaining > 0; i = (i + 1) % count, remaining--) {
//            PhotoFace face = faces.get(i);
//            if (!isFaceAnchorOccupied(face, anchor, documentId, maskCoords)) {
//                return face;
//            }
//        }
//
//        return selectedFace;
//    }

//    private boolean isFaceAnchorOccupied(PhotoFace face, int anchor, long documentId, TLRPC.TL_maskCoords maskCoords) {
//        Point anchorPoint = face.getPointForAnchor(anchor);
//        if (anchorPoint == null) {
//            return true;
//        }
//
//        float minDistance = face.getWidthForAnchor(0) * 1.1f;
//
//        for (int index = 0; index < entitiesView.getChildCount(); index++) {
//            View view = entitiesView.getChildAt(index);
//            if (!(view instanceof StickerView)) {
//                continue;
//            }
//
//            StickerView stickerView = (StickerView) view;
//            if (stickerView.getAnchor() != anchor) {
//                continue;
//            }
//
//            Point location = stickerView.getPosition();
//            float distance = (float)Math.hypot(location.x - anchorPoint.x, location.y - anchorPoint.y);
//            if ((documentId == stickerView.getSticker().id || faces.size() > 1) && distance < minDistance) {
//                return true;
//            }
//        }
//
//        return false;
//    }

    public PhotoView createPhoto(String path, boolean select) {
        Size size = basePhotoSize(path);
        Pair<Integer, Integer> orientation = AndroidUtilities.getImageOrientation(path);
        if ((orientation.first / 90 % 2) == 1) {
            float w = size.width;
            size.width = size.height;
            size.height = w;
        }
        PhotoView view = new PhotoView(getContext(), centerPositionForEntity(), 0, 1f, size, path, orientation.first, orientation.second);
        view.centerImage.setLayerNum(4 + 8);
//        view.setHasStickyX(true);
//        view.setHasStickyY(true);
        view.setDelegate(this);
        entitiesView.addView(view);
        if (select) {
            registerRemovalUndo(view);
            selectEntity(view);
        }
        return view;
    }

    private StickerView createSticker(Object parentObject, TLRPC.Document sticker, boolean select) {
        PaintView.StickerPosition position = calculateStickerPosition(sticker);
        StickerView view = new StickerView(getContext(), position.position, position.angle, position.scale, baseStickerSize(), sticker, parentObject) {
            @Override
            protected void didSetAnimatedSticker(RLottieDrawable drawable) {
                PaintView.this.didSetAnimatedSticker(drawable);
            }
        };
        if (MessageObject.isTextColorEmoji(sticker)) {
            view.centerImage.setColorFilter(new PorterDuffColorFilter(0xffffffff, PorterDuff.Mode.SRC_IN));
        }
        view.centerImage.setLayerNum(4 + 8);
//        if (position.position.x == entitiesView.getMeasuredWidth() / 2f) {
//            view.setHasStickyX(true);
//        }
//        if (position.position.y == entitiesView.getMeasuredHeight() / 2f) {
//            view.setHasStickyY(true);
//        }
        view.setDelegate(this);
        entitiesView.addView(view);
        if (select) {
            registerRemovalUndo(view);
            selectEntity(view);
        }
        return view;
    }

    public void removeCurrentEntity() {
        if (currentEntityView != null) {
            removeEntity(currentEntityView);
        }
    }

    private void removeEntity(EntityView entityView) {
        if (entityView == currentEntityView && currentEntityView != null) {
            currentEntityView.deselect();
            selectEntity(null);

            if (entityView instanceof TextPaintView) {
                if (tabsSelectionAnimator != null && tabsNewSelectedIndex != 0) {
                    tabsSelectionAnimator.cancel();
                }
                switchTab(0);
            }
        }
        entitiesView.removeView(entityView);
        if (entityView != null) {
            undoStore.unregisterUndo(entityView.getUUID());
        }

        weightChooserView.setValueOverride(weightDefaultValueOverride);
        weightChooserView.setShowPreview(true);
        colorSwatch.brushWeight = weightDefaultValueOverride.get();
        setCurrentSwatch(colorSwatch, true);
    }

    private void registerRemovalUndo(final EntityView entityView) {
        if (entityView == null) {
            return;
        }
        undoStore.registerUndo(entityView.getUUID(), () -> removeEntity(entityView));
    }

    protected void didSetAnimatedSticker(RLottieDrawable drawable) {}

    @Override
    public boolean onEntitySelected(EntityView entityView) {
        return selectEntity(entityView);
    }

    @Override
    public void onEntityDragEnd(boolean delete) {
        updatePreviewViewTranslationY();
    }

    @Override
    public void onEntityDragStart() {

    }

    @Override
    public boolean onEntityLongClicked(EntityView entityView) {
        showMenuForEntity(entityView);
        return true;
    }

    private final float[] temp = new float[2];
    private final int[] loc = new int[2];

    @Override
    public float[] getTransformedTouch(MotionEvent e, float rawX, float rawY) {
        View previewView = (View) renderView.getParent();
        View containerView = (View) previewView.getParent();
        float x = rawX - previewView.getX() - containerView.getLeft();
        float y = rawY - previewView.getY() - containerView.getTop();
        x = previewView.getPivotX() + (x - previewView.getPivotX()) / previewView.getScaleX();
        y = previewView.getPivotY() + (y - previewView.getPivotY()) / previewView.getScaleY();
        temp[0] = x;
        temp[1] = y;
        return temp;
    }

    @Override
    public int[] getCenterLocation(EntityView entityView) {
        pos[0] = (int) entityView.getPosition().x;
        pos[1] = (int) entityView.getPosition().y;
        return pos;
    }

    private int[] pos = new int[2];
    private int[] getCenterLocationInWindow(View view) {
        view.getLocationInWindow(pos);
//        float rotation = (float) Math.toRadians(view.getRotation() + (currentCropState != null ? currentCropState.cropRotate + currentCropState.transformRotation : 0));
        float width = view.getWidth() * view.getScaleX() * entitiesView.getScaleX();
        float height = view.getHeight() * view.getScaleY() * entitiesView.getScaleY();
//        float px = (float) (width * Math.cos(rotation) - height * Math.sin(rotation));
//        float py = (float) (width * Math.sin(rotation) + height * Math.cos(rotation));
        pos[0] += width / 2;
        pos[1] += height / 2;
        return pos;
    }

    @Override
    public boolean allowInteraction(EntityView entityView) {
        return !editingText;
    }

    @Override
    public float getCropRotation() {
        return currentCropState != null ? currentCropState.cropRotate + currentCropState.transformRotation : 0;
    }

    private static class StickerPosition {
        private Point position;
        private float scale;
        private float angle;

        StickerPosition(Point position, float scale, float angle) {
            this.position = position;
            this.scale = scale;
            this.angle = angle;
        }
    }

    /* === emoji keyboard support === */

    public EmojiView emojiView;
    private boolean emojiViewVisible, emojiViewWasVisible;
    private boolean keyboardVisible, isAnimatePopupClosing;
    private int emojiPadding, emojiWasPadding;
    private boolean translateBottomPanelAfterResize;
    private int keyboardHeight, keyboardHeightLand;
    private boolean waitingForKeyboardOpen;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private boolean destroyed;

    private Runnable openKeyboardRunnable = new Runnable() {
        @Override
        public void run() {
            if (!(currentEntityView instanceof TextPaintView)) {
                return;
            }
            final EditTextOutline editText = ((TextPaintView) currentEntityView).getEditText();
            if (!destroyed && editText != null && waitingForKeyboardOpen && !keyboardVisible && !AndroidUtilities.usingHardwareInput && !AndroidUtilities.isInMultiwindow && AndroidUtilities.isTablet()) {
                editText.requestFocus();
                AndroidUtilities.showKeyboard(editText);
                AndroidUtilities.cancelRunOnUIThread(openKeyboardRunnable);
                AndroidUtilities.runOnUIThread(openKeyboardRunnable, 100);
            }
        }
    };

    @Override
    public void onEmojiButtonClick() {
        boolean emojiViewWasVisible = emojiViewVisible;
        if (emojiViewWasVisible && currentEntityView instanceof TextPaintView) {
            keyboardNotifier.awaitKeyboard();
            final EditTextOutline editText = ((TextPaintView) currentEntityView).getEditText();
            AndroidUtilities.showKeyboard(editText);
        }
        showEmojiPopup(emojiViewVisible ? 0 : 1);
    }

    private void showEmojiPopup(int show) {
        if (show == 1) {
            boolean emojiWasVisible = emojiView != null && emojiView.getVisibility() == View.VISIBLE;
            createEmojiView();

            emojiView.setVisibility(VISIBLE);
            emojiViewWasVisible = emojiViewVisible;
            emojiViewVisible = true;
            View currentView = emojiView;

            if (keyboardHeight <= 0) {
                if (AndroidUtilities.isTablet()) {
                    keyboardHeight = AndroidUtilities.dp(150);
                } else {
                    keyboardHeight = MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200));
                }
            }
            if (keyboardHeightLand <= 0) {
                if (AndroidUtilities.isTablet()) {
                    keyboardHeightLand = AndroidUtilities.dp(150);
                } else {
                    keyboardHeightLand = MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(200));
                }
            }
            int currentHeight = (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y ? keyboardHeightLand : keyboardHeight) + parent.getPaddingUnderContainer();

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) currentView.getLayoutParams();
            layoutParams.height = currentHeight;
            currentView.setLayoutParams(layoutParams);
            if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet() && currentEntityView instanceof TextPaintView) {
                final EditTextOutline editText = ((TextPaintView) currentEntityView).getEditText();
                AndroidUtilities.hideKeyboard(editText);
            }

            emojiPadding = emojiWasPadding = currentHeight;
            keyboardNotifier.fire();
            requestLayout();

            ChatActivityEnterViewAnimatedIconView emojiButton = textOptionsView.getEmojiButton();
            if (emojiButton != null) {
                emojiButton.setState(ChatActivityEnterViewAnimatedIconView.State.KEYBOARD, true);
            }

            if (!emojiWasVisible) {
                if (keyboardVisible) {
                    translateBottomPanelAfterResize = true;
                } else {
                    ValueAnimator animator = ValueAnimator.ofFloat(emojiPadding, 0);
                    animator.addUpdateListener(animation -> {
                        float v = (float) animation.getAnimatedValue();
                        emojiView.setTranslationY(v);
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            emojiView.setTranslationY(0);
                        }
                    });
                    animator.setDuration(AdjustPanLayoutHelper.keyboardDuration);
                    animator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                    animator.start();
                }

            }
        } else {
            ChatActivityEnterViewAnimatedIconView emojiButton = textOptionsView.getEmojiButton();
            if (emojiButton != null) {
                emojiButton.setState(ChatActivityEnterViewAnimatedIconView.State.SMILE, true);
            }
            if (emojiView != null) {
                emojiViewWasVisible = emojiViewVisible;
                emojiViewVisible = false;
                if (AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                    emojiView.setVisibility(GONE);
                }
            }
            if (show == 0) {
                emojiPadding = 0;
                keyboardNotifier.fire();
            }
            requestLayout();
        }

        updatePlusEmojiKeyboardButton();
    }

    private void hideEmojiPopup(boolean byBackButton) {
        if (emojiViewVisible) {
            showEmojiPopup(0);
        }
        if (byBackButton) {
            if (emojiView != null && emojiView.getVisibility() == View.VISIBLE && !waitingForKeyboardOpen) {
                int height = emojiView.getMeasuredHeight();
                ValueAnimator animator = ValueAnimator.ofFloat(0, height);
                animator.addUpdateListener(animation -> {
                    float v = (float) animation.getAnimatedValue();
                    emojiView.setTranslationY(v);
                });
                isAnimatePopupClosing = true;
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isAnimatePopupClosing = false;
                        emojiView.setTranslationY(0);
                        hideEmojiView();
                    }
                });
                animator.setDuration(AdjustPanLayoutHelper.keyboardDuration);
                animator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                animator.start();
            } else {
                hideEmojiView();
            }
        }
    }

    @Override
    public int getEmojiPadding(boolean panned) {
        return emojiPadding;
    }

    private void hideEmojiView() {
        if (!emojiViewVisible && emojiView != null && emojiView.getVisibility() != GONE) {
            emojiView.setVisibility(GONE);
        }
        int wasEmojiPadding = emojiPadding;
        emojiPadding = 0;
        if (wasEmojiPadding != emojiPadding) {
            keyboardNotifier.fire();
        }
    }

    @Override
    public int measureKeyboardHeight() {
        return keyboardNotifier.getKeyboardHeight() - parent.getBottomPadding(false);
    }

    @Override
    public void onSizeChanged(int height, boolean isWidthGreater) {
        if (height > AndroidUtilities.dp(50) && keyboardVisible && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
            if (isWidthGreater) {
                keyboardHeightLand = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height_land3", keyboardHeightLand).commit();
            } else {
                keyboardHeight = height;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height", keyboardHeight).commit();
            }
        }

        if (emojiViewVisible) {
            int newHeight = (isWidthGreater ? keyboardHeightLand : keyboardHeight) + parent.getPaddingUnderContainer();

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) emojiView.getLayoutParams();
            if (layoutParams.width != AndroidUtilities.displaySize.x || layoutParams.height != newHeight) {
                layoutParams.width = AndroidUtilities.displaySize.x;
                layoutParams.height = newHeight;
                emojiView.setLayoutParams(layoutParams);

                emojiPadding = emojiWasPadding = layoutParams.height;
                keyboardNotifier.fire();
                requestLayout();
            }
        }

        if (lastSizeChangeValue1 == height && lastSizeChangeValue2 == isWidthGreater) {
            return;
        }
        lastSizeChangeValue1 = height;
        lastSizeChangeValue2 = isWidthGreater;

        boolean oldValue = keyboardVisible;
        if (currentEntityView instanceof TextPaintView) {
            final EditTextOutline editText = ((TextPaintView) currentEntityView).getEditText();
            keyboardVisible = editText.isFocused() && keyboardNotifier.keyboardVisible();
        } else {
            keyboardVisible = false;
        }
        if (keyboardVisible && emojiViewVisible) {
            showEmojiPopup(0);
        }
        if (emojiPadding != 0 && !keyboardVisible && keyboardVisible != oldValue && !emojiViewVisible) {
            emojiPadding = 0;
            keyboardNotifier.fire();
            requestLayout();
        }
        updateTextDim();
        if (oldValue && !keyboardVisible && emojiPadding > 0 && translateBottomPanelAfterResize) {
            translateBottomPanelAfterResize = false;
        }
        if (keyboardVisible && waitingForKeyboardOpen) {
            waitingForKeyboardOpen = false;
            AndroidUtilities.cancelRunOnUIThread(openKeyboardRunnable);
        }

        updatePlusEmojiKeyboardButton();
    }

    private void updateTextDim() {
        final boolean show = currentEntityView instanceof TextPaintView && (keyboardNotifier.keyboardVisible() || emojiPadding > 0) && !keyboardNotifier.ignoring;
        textDim.animate().cancel();
        textDim.setVisibility(View.VISIBLE);
        textDim.animate().alpha(show ? 1f : 0f).withEndAction(() -> {
            if (!show) {
                textDim.setVisibility(View.GONE);
            }
        }).start();
    }

    private void updatePlusEmojiKeyboardButton() {
        if (textOptionsView != null) {
            if (keyboardNotifier.keyboardVisible()) {
                textOptionsView.animatePlusToIcon(R.drawable.input_smile);
            } else if (emojiViewVisible) {
                textOptionsView.animatePlusToIcon(R.drawable.input_keyboard);
            } else {
                textOptionsView.animatePlusToIcon(R.drawable.msg_add);
            }
        }

        final boolean text = keyboardNotifier.keyboardVisible() || emojiViewVisible;

        AndroidUtilities.updateViewShow(undoAllButton, !text, false, 1, true, null);
        AndroidUtilities.updateViewShow(undoButton, !text, false, 1, true, null);

        AndroidUtilities.updateViewShow(doneTextButton, text, false, 1, true, null);
        AndroidUtilities.updateViewShow(cancelTextButton, text, false, 1, true, null);
    }

    protected void createEmojiView() {
        if (emojiView != null && emojiView.currentAccount != UserConfig.selectedAccount) {
            parent.removeView(emojiView);
            emojiView = null;
        }
        if (emojiView != null) {
            return;
        }
        emojiView = new EmojiView(null, true, false, false, getContext(), false, null, null, true, resourcesProvider);
        emojiView.fixBottomTabContainerTranslation = false;
        emojiView.allowEmojisForNonPremium(true);
        emojiView.setVisibility(GONE);
        if (AndroidUtilities.isTablet()) {
            emojiView.setForseMultiwindowLayout(true);
        }
        emojiView.setDelegate(new EmojiView.EmojiViewDelegate() {
            int innerTextChange;

            @Override
            public boolean onBackspace() {
                final EditTextOutline editText = ((TextPaintView) currentEntityView).getEditText();
                if (editText == null || editText.length() == 0) {
                    return false;
                }
                editText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                return true;
            }

            @Override
            public void onAnimatedEmojiUnlockClick() {
//                BaseFragment fragment = parentFragment;
//                if (fragment == null) {
//                    fragment = new BaseFragment() {
//                        @Override
//                        public int getCurrentAccount() {
//                            return currentAccount;
//                        }
//
//                        @Override
//                        public Context getContext() {
//                            return EditTextEmoji.this.getContext();
//                        }
//
//                        @Override
//                        public Activity getParentActivity() {
//                            Context context = getContext();
//                            while (context instanceof ContextWrapper) {
//                                if (context instanceof Activity) {
//                                    return (Activity) context;
//                                }
//                                context = ((ContextWrapper) context).getBaseContext();
//                            }
//                            return null;
//                        }
//
//                        @Override
//                        public Dialog getVisibleDialog() {
//                            return new Dialog(getContext()) {
//                                @Override
//                                public void dismiss() {
//                                    hidePopup(false);
//                                    closeParent();
//                                }
//                            };
//                        }
//                    };
//                    new PremiumFeatureBottomSheet(fragment, PremiumPreviewFragment.PREMIUM_FEATURE_ANIMATED_EMOJI, false).show();
//                } else {
//                    fragment.showDialog(new PremiumFeatureBottomSheet(fragment, PremiumPreviewFragment.PREMIUM_FEATURE_ANIMATED_EMOJI, false));
//                }
            }

            @Override
            public void onEmojiSelected(String symbol) {
                if (!(currentEntityView instanceof TextPaintView)) {
                    return;
                }
                TextPaintView textPaintView = (TextPaintView) currentEntityView;
                final EditTextOutline editText = textPaintView.getEditText();
                if (editText == null) {
                    return;
                }
                int i = editText.getSelectionEnd();
                if (i < 0) {
                    i = 0;
                }
                try {
                    innerTextChange = 2;
                    CharSequence localCharSequence = Emoji.replaceEmoji(symbol, textPaintView.getFontMetricsInt(), (int) (textPaintView.getFontSize() * .8f), false);
                    if (localCharSequence instanceof Spanned) {
                        Emoji.EmojiSpan[] spans = ((Spanned) localCharSequence).getSpans(0, localCharSequence.length(), Emoji.EmojiSpan.class);
                        if (spans != null) {
                            for (int a = 0; a < spans.length; ++a) {
                                spans[a].scale = .85f;
                            }
                        }
                    }
                    editText.setText(editText.getText().insert(i, localCharSequence));
                    int j = i + localCharSequence.length();
                    editText.setSelection(j, j);
                } catch (Exception e) {
                    FileLog.e(e);
                } finally {
                    innerTextChange = 0;
                }
            }

            @Override
            public void onCustomEmojiSelected(long documentId, TLRPC.Document document, String emoticon, boolean isRecent) {
                final EditTextOutline editText = ((TextPaintView) currentEntityView).getEditText();
                if (editText == null) {
                    return;
                }
                int i = editText.getSelectionEnd();
                if (i < 0) {
                    i = 0;
                }
                try {
                    innerTextChange = 2;
                    SpannableString spannable = new SpannableString(emoticon);
                    AnimatedEmojiSpan span;
                    if (document != null) {
                        span = new AnimatedEmojiSpan(document, 1f, editText.getPaint().getFontMetricsInt());
                    } else {
                        span = new AnimatedEmojiSpan(documentId, 1f, editText.getPaint().getFontMetricsInt());
                    }
                    spannable.setSpan(span, 0, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    editText.setText(editText.getText().insert(i, spannable));
                    int j = i + spannable.length();
                    editText.setSelection(j, j);
                } catch (Exception e) {
                    FileLog.e(e);
                } finally {
                    innerTextChange = 0;
                }
            }

            @Override
            public void onClearEmojiRecent() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), resourcesProvider);
                builder.setTitle(LocaleController.getString("ClearRecentEmojiTitle", R.string.ClearRecentEmojiTitle));
                builder.setMessage(LocaleController.getString("ClearRecentEmojiText", R.string.ClearRecentEmojiText));
                builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton), (dialogInterface, i) -> emojiView.clearRecentEmoji());
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                builder.show();
            }
        });
        parent.addView(emojiView);
    }

    @Override
    public float adjustPanLayoutHelperProgress() {
        return 0;
    }

    @Override
    protected void onAttachedToWindow() {
        destroyed = false;
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        destroyed = true;
        super.onDetachedFromWindow();
    }

    protected void onGalleryClick() {
    }
}
