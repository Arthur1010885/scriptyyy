package com.p001s;

import adrt.ADRTLogCatReader;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.HashMap;

/* JADX INFO: loaded from: /home/ubuntu/apk_source_work/raw/classes.dex */
public class OverlayService extends Service {
    private static final String[] ICONS = {"SKULL", "GEAR"};
    private View bubble;
    private WindowManager.LayoutParams bubbleParams;
    private FrameLayout menu;
    private WindowManager.LayoutParams menuParams;
    private TextView statusTextView;
    private WindowManager windowManager;
    private int activeTab = 0;
    private String currentStatusText = "STATUS: CONNECTED.";
    private int currentStatusColor = Color.parseColor("#666666");
    private HashMap<String, Integer> featureStates = new HashMap<>();

    public interface OnOptionSelected {
        void onSelect(int i);
    }

    private int cBg() {
        return Color.parseColor("#121212");
    }

    private int cSidebar() {
        return Color.parseColor("#191919");
    }

    private int cStroke() {
        return Color.parseColor("#262626");
    }

    private int cTextLight() {
        return Color.parseColor("#D4D4D4");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int cTextMuted() {
        return Color.parseColor("#666666");
    }

    private int cActiveIcon() {
        return Color.parseColor("#932929");
    }

    private int cInactiveIcon() {
        return Color.parseColor("#555555");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int cBtnRed() {
        return Color.parseColor("#8C2222");
    }

    private int cBtnRedText() {
        return Color.parseColor("#EAEAEA");
    }

    private int cBtnLoading() {
        return Color.parseColor("#171717");
    }

    private int cBtnLoadingText() {
        return Color.parseColor("#292929");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int cBtnGreen() {
        return Color.parseColor("#4A8B47");
    }

    private int cBtnGreenText() {
        return Color.parseColor("#121212");
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        ADRTLogCatReader.onContext(this, "aidepro.top");
        super.onCreate();
        this.windowManager = (WindowManager) getSystemService("window");
        buildAndShowMenu();
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        safeRemove(this.menu);
        safeRemove(this.bubble);
        this.menu = null;
        this.bubble = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void safeRemove(View view) {
        WindowManager windowManager;
        if (view == null || (windowManager = this.windowManager) == null) {
            return;
        }
        try {
            windowManager.removeView(view);
        } catch (Exception e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void buildAndShowMenu() {
        int iM1dp = m1dp(20);
        int iM1dp2 = m1dp(50);
        WindowManager.LayoutParams layoutParams = this.menuParams;
        if (layoutParams != null) {
            iM1dp = layoutParams.x;
            iM1dp2 = this.menuParams.y;
        }
        safeRemove(this.menu);
        this.menu = buildMenu(this);
        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams(m1dp(440), m1dp(280), 2038, 8, -3);
        this.menuParams = layoutParams2;
        layoutParams2.gravity = 51;
        this.menuParams.x = iM1dp;
        this.menuParams.y = iM1dp2;
        attachDrag(this.menu, this.menuParams, true);
        this.windowManager.addView(this.menu, this.menuParams);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeMenu() {
        safeRemove(this.menu);
        showBubble();
    }

    private void showBubble() {
        this.bubble = buildBubble(this);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(m1dp(48), m1dp(48), 2038, 8, -3);
        this.bubbleParams = layoutParams;
        layoutParams.gravity = 51;
        this.bubbleParams.x = this.menuParams.x;
        this.bubbleParams.y = this.menuParams.y;
        attachDrag(this.bubble, this.bubbleParams, false);
        this.windowManager.addView(this.bubble, this.bubbleParams);
    }

    public static class FovDrawView extends View {
        public int color;

        /* JADX INFO: renamed from: p */
        private final Paint f11p;
        public float radius;
        public float thickness;

        public FovDrawView(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.f11p = paint;
            paint.setStyle(Paint.Style.STROKE);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            this.f11p.setColor(this.color);
            this.f11p.setStrokeWidth(this.thickness);
            float width = getWidth() / 2.0f;
            float height = getHeight() / 2.0f;
            canvas.drawCircle(width, height, this.radius, this.f11p);
            this.f11p.setStyle(Paint.Style.FILL);
            canvas.drawCircle(width, height, this.thickness, this.f11p);
            this.f11p.setStyle(Paint.Style.STROKE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openMenu() {
        if (this.bubble != null) {
            this.menuParams.x = this.bubbleParams.x;
            this.menuParams.y = this.bubbleParams.y;
            safeRemove(this.bubble);
            this.bubble = null;
        }
        this.windowManager.addView(this.menu, this.menuParams);
    }

    private void attachDrag(final View view, final WindowManager.LayoutParams layoutParams, final boolean z) {
        view.setOnTouchListener(new View.OnTouchListener(this) { // from class: com.s.OverlayService.100000000
            boolean moved;

            /* JADX INFO: renamed from: sx */
            int f0sx;

            /* JADX INFO: renamed from: sy */
            int f1sy;
            private final OverlayService this$0;

            /* JADX INFO: renamed from: tx */
            float f2tx;

            /* JADX INFO: renamed from: ty */
            float f3ty;

            {
                this.this$0 = this;
            }

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view2, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case 0:
                        this.f0sx = layoutParams.x;
                        this.f1sy = layoutParams.y;
                        this.f2tx = motionEvent.getRawX();
                        this.f3ty = motionEvent.getRawY();
                        this.moved = false;
                        return true;
                    case 1:
                        if (!this.moved && !z) {
                            this.this$0.openMenu();
                        }
                        return true;
                    case 2:
                        int rawX = this.f0sx + ((int) (motionEvent.getRawX() - this.f2tx));
                        int rawY = this.f1sy + ((int) (motionEvent.getRawY() - this.f3ty));
                        if (Math.abs(rawX - this.f0sx) > this.this$0.m1dp(4) || Math.abs(rawY - this.f1sy) > this.this$0.m1dp(4)) {
                            this.moved = true;
                        }
                        layoutParams.x = rawX;
                        layoutParams.y = rawY;
                        this.this$0.windowManager.updateViewLayout(view, layoutParams);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void attachDragWithTap(final View view, final WindowManager.LayoutParams layoutParams, final Runnable runnable) {
        view.setOnTouchListener(new View.OnTouchListener(this) { // from class: com.s.OverlayService.100000001
            boolean moved;

            /* JADX INFO: renamed from: sx */
            int f4sx;

            /* JADX INFO: renamed from: sy */
            int f5sy;
            private final OverlayService this$0;

            /* JADX INFO: renamed from: tx */
            float f6tx;

            /* JADX INFO: renamed from: ty */
            float f7ty;

            {
                this.this$0 = this;
            }

            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view2, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case 0:
                        this.f4sx = layoutParams.x;
                        this.f5sy = layoutParams.y;
                        this.f6tx = motionEvent.getRawX();
                        this.f7ty = motionEvent.getRawY();
                        this.moved = false;
                        return true;
                    case 1:
                        if (!this.moved) {
                            runnable.run();
                        }
                        return true;
                    case 2:
                        int rawX = this.f4sx + ((int) (motionEvent.getRawX() - this.f6tx));
                        int rawY = this.f5sy + ((int) (motionEvent.getRawY() - this.f7ty));
                        if (Math.abs(rawX - this.f4sx) > this.this$0.m1dp(4) || Math.abs(rawY - this.f5sy) > this.this$0.m1dp(4)) {
                            this.moved = true;
                        }
                        layoutParams.x = rawX;
                        layoutParams.y = rawY;
                        this.this$0.windowManager.updateViewLayout(view, layoutParams);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private View buildBubble(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(1);
        gradientDrawable.setColor(cSidebar());
        gradientDrawable.setStroke(m1dp(1), cStroke());
        frameLayout.setBackground(gradientDrawable);
        ImageView imageView = new ImageView(context);
        int identifier = context.getResources().getIdentifier("logo", "drawable", context.getPackageName());
        if (identifier != 0) {
            imageView.setImageResource(identifier);
        }
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        frameLayout.addView(imageView, new FrameLayout.LayoutParams(m1dp(30), m1dp(30), 17));
        View viewBuildWhiteCloseButton = buildWhiteCloseButton(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(m1dp(16), m1dp(16), 53);
        ((ViewGroup.MarginLayoutParams) layoutParams).topMargin = m1dp(2);
        ((ViewGroup.MarginLayoutParams) layoutParams).rightMargin = m1dp(2);
        viewBuildWhiteCloseButton.setOnClickListener(new View.OnClickListener(this) { // from class: com.s.OverlayService.100000002
            private final OverlayService this$0;

            {
                this.this$0 = this;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                OverlayService overlayService = this.this$0;
                overlayService.safeRemove(overlayService.bubble);
                this.this$0.bubble = null;
                this.this$0.showBigLogoBanner();
            }
        });
        frameLayout.addView(viewBuildWhiteCloseButton, layoutParams);
        return frameLayout;
    }

    private View buildWhiteCloseButton(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(1);
        gradientDrawable.setColor(Color.parseColor("#40000000"));
        frameLayout.setBackground(gradientDrawable);
        frameLayout.addView(new IconView(context, "CLOSE", false, -1, -1), new FrameLayout.LayoutParams(m1dp(10), m1dp(10), 17));
        return frameLayout;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showBigLogoBanner() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(m1dp(110), m1dp(110), 2038, 8, -3);
        layoutParams.gravity = 51;
        WindowManager.LayoutParams layoutParams2 = this.bubbleParams;
        if (layoutParams2 != null) {
            layoutParams.x = layoutParams2.x;
            layoutParams.y = this.bubbleParams.y;
        } else {
            WindowManager.LayoutParams layoutParams3 = this.menuParams;
            if (layoutParams3 != null) {
                layoutParams.x = layoutParams3.x;
                layoutParams.y = this.menuParams.y;
            } else {
                layoutParams.x = m1dp(20);
                layoutParams.y = m1dp(50);
            }
        }
        final FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setBackgroundColor(0);
        ImageView imageView = new ImageView(this);
        int identifier = getResources().getIdentifier("logo", "drawable", getPackageName());
        if (identifier != 0) {
            imageView.setImageResource(identifier);
        }
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        frameLayout.addView(imageView, new FrameLayout.LayoutParams(-1, -1));
        View viewBuildWhiteCloseButton = buildWhiteCloseButton(this);
        FrameLayout.LayoutParams layoutParams4 = new FrameLayout.LayoutParams(m1dp(20), m1dp(20), 53);
        ((ViewGroup.MarginLayoutParams) layoutParams4).topMargin = m1dp(4);
        ((ViewGroup.MarginLayoutParams) layoutParams4).rightMargin = m1dp(4);
        viewBuildWhiteCloseButton.setOnClickListener(new View.OnClickListener(this) { // from class: com.s.OverlayService.100000003
            private final OverlayService this$0;

            {
                this.this$0 = this;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                this.this$0.safeRemove(frameLayout);
            }
        });
        frameLayout.addView(viewBuildWhiteCloseButton, layoutParams4);
        attachDragWithTap(frameLayout, layoutParams, new AnonymousClass100000004(this, frameLayout));
        this.windowManager.addView(frameLayout, layoutParams);
    }

    /* JADX INFO: renamed from: com.s.OverlayService$100000004, reason: invalid class name */
    class AnonymousClass100000004 implements Runnable {
        private final OverlayService this$0;
        private final /* synthetic */ FrameLayout val$bigLogoView;

        AnonymousClass100000004(OverlayService overlayService, FrameLayout frameLayout) {
            this.this$0 = overlayService;
            this.val$bigLogoView = frameLayout;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.this$0.safeRemove(this.val$bigLogoView);
            this.this$0.buildAndShowMenu();
        }

        /* JADX INFO: renamed from: com.s.OverlayService$100000004$100000003, reason: invalid class name */
        class AnonymousClass100000003 implements Runnable {
            private final AnonymousClass100000004 this$0;
            private final /* synthetic */ GradientDrawable val$bg;
            private final /* synthetic */ TextView val$btn;
            private final /* synthetic */ String val$title;

            AnonymousClass100000003(AnonymousClass100000004 anonymousClass100000004, String str, TextView textView, GradientDrawable gradientDrawable) {
                this.this$0 = anonymousClass100000004;
                this.val$title = str;
                this.val$btn = textView;
                this.val$bg = gradientDrawable;
            }

            @Override // java.lang.Runnable
            public void run() {
                if (this.this$0.this$0.featureStates.containsKey(this.val$title) && ((Integer) this.this$0.this$0.featureStates.get(this.val$title)).intValue() == 1) {
                    this.this$0.this$0.featureStates.put(this.val$title, new Integer(2));
                    OverlayService.access$1000048(this.this$0.this$0, this.val$btn, this.val$bg, 2);
                    OverlayService.access$1000049(this.this$0.this$0, new StringBuffer().append(this.val$title).append(" SUCCESS").toString(), this.this$0.this$0.cBtnGreen());
                }
            }
        }
    }

    private FrameLayout buildMenu(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(cBg());
        gradientDrawable.setCornerRadius(m1dp(2));
        gradientDrawable.setStroke(m1dp(1), cStroke());
        frameLayout.setBackground(gradientDrawable);
        frameLayout.setClipToOutline(true);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        linearLayout.addView(buildSidebar(context));
        View view = new View(context);
        view.setBackgroundColor(Color.parseColor("#151515"));
        linearLayout.addView(view, new LinearLayout.LayoutParams(m1dp(1), -1));
        FrameLayout frameLayout2 = new FrameLayout(context);
        frameLayout2.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1.0f));
        frameLayout2.addView(buildContentArea(context));
        TextView textView = new TextView(context);
        this.statusTextView = textView;
        textView.setText(this.currentStatusText);
        this.statusTextView.setTextColor(this.currentStatusColor);
        this.statusTextView.setTextSize(2, 9);
        this.statusTextView.setTypeface(Typeface.DEFAULT_BOLD);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 83;
        ((ViewGroup.MarginLayoutParams) layoutParams).leftMargin = m1dp(16);
        ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = m1dp(12);
        frameLayout2.addView(this.statusTextView, layoutParams);
        linearLayout.addView(frameLayout2);
        frameLayout.addView(linearLayout);
        return frameLayout;
    }

    private LinearLayout buildSidebar(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setGravity(1);
        linearLayout.setPadding(m1dp(0), m1dp(16), m1dp(0), m1dp(16));
        linearLayout.setBackgroundColor(cSidebar());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(m1dp(64), -1));
        ImageView imageView = new ImageView(context);
        int identifier = context.getResources().getIdentifier("logo", "drawable", context.getPackageName());
        if (identifier != 0) {
            imageView.setImageResource(identifier);
        }
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(m1dp(28), m1dp(28));
        ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = m1dp(24);
        linearLayout.addView(imageView, layoutParams);
        final int i = 0;
        while (true) {
            String[] strArr = ICONS;
            if (i < strArr.length) {
                View viewBuildSideTab = buildSideTab(context, strArr[i], i == this.activeTab);
                viewBuildSideTab.setOnClickListener(new View.OnClickListener(this) { // from class: com.s.OverlayService.100000005
                    private final OverlayService this$0;

                    {
                        this.this$0 = this;
                    }

                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        int i2 = this.this$0.activeTab;
                        int i3 = i;
                        if (i2 == i3) {
                            return;
                        }
                        this.this$0.activeTab = i3;
                        this.this$0.buildAndShowMenu();
                    }
                });
                linearLayout.addView(viewBuildSideTab);
                i++;
            } else {
                linearLayout.addView(new View(context), new LinearLayout.LayoutParams(-1, 0, 1.0f));
                View viewBuildSideTab2 = buildSideTab(context, "CLOSE", false);
                viewBuildSideTab2.setOnClickListener(new AnonymousClass100000006(this));
                linearLayout.addView(viewBuildSideTab2);
                return linearLayout;
            }
        }
    }

    /* JADX INFO: renamed from: com.s.OverlayService$100000006, reason: invalid class name */
    class AnonymousClass100000006 implements View.OnClickListener {
        private final OverlayService this$0;

        AnonymousClass100000006(OverlayService overlayService) {
            this.this$0 = overlayService;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            this.this$0.closeMenu();
        }

        /* JADX INFO: renamed from: com.s.OverlayService$100000006$100000005, reason: invalid class name */
        class AnonymousClass100000005 implements Runnable {
            private final AnonymousClass100000006 this$0;
            private final /* synthetic */ GradientDrawable val$bg;
            private final /* synthetic */ TextView val$btn;
            private final /* synthetic */ String val$title;

            AnonymousClass100000005(AnonymousClass100000006 anonymousClass100000006, String str, TextView textView, GradientDrawable gradientDrawable) {
                this.this$0 = anonymousClass100000006;
                this.val$title = str;
                this.val$btn = textView;
                this.val$bg = gradientDrawable;
            }

            @Override // java.lang.Runnable
            public void run() {
                if (this.this$0.this$0.featureStates.containsKey(this.val$title) && ((Integer) this.this$0.this$0.featureStates.get(this.val$title)).intValue() == 1) {
                    this.this$0.this$0.featureStates.put(this.val$title, new Integer(2));
                    OverlayService.access$1000052(this.this$0.this$0, this.val$btn, this.val$bg, 2);
                    OverlayService.access$1000053(this.this$0.this$0, new StringBuffer().append(this.val$title).append(" SUCCESS").toString(), this.this$0.this$0.cBtnGreen());
                }
            }
        }
    }

    private View buildSideTab(Context context, String str, boolean z) {
        FrameLayout frameLayout = new FrameLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(m1dp(44), m1dp(44));
        ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = m1dp(8);
        frameLayout.setLayoutParams(layoutParams);
        if (z) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setCornerRadius(m1dp(8));
            gradientDrawable.setColor(Color.parseColor("#151515"));
            gradientDrawable.setStroke(m1dp(1), Color.parseColor("#333333"));
            frameLayout.setBackground(gradientDrawable);
        }
        int iCActiveIcon = z ? cActiveIcon() : cInactiveIcon();
        if (str.equals("CLOSE")) {
            iCActiveIcon = cInactiveIcon();
        }
        int i = iCActiveIcon;
        IconView iconView = new IconView(context, str, z, i, i);
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(m1dp(20), m1dp(20));
        layoutParams2.gravity = 17;
        frameLayout.addView(iconView, layoutParams2);
        return frameLayout;
    }

    private View buildContentArea(Context context) {
        ScrollView scrollView = new ScrollView(context);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setOverScrollMode(2);
        scrollView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(m1dp(16), m1dp(16), m1dp(16), m1dp(40));
        if (this.activeTab == 0) {
            linearLayout.addView(buildHeaderBox(context, "FEATURES"));
            linearLayout.addView(buildFeatureButton(context, "SYNCHRONIZE FILE DATE"));
            linearLayout.addView(buildFeatureButton(context, "IMPROVE AIMING PRECISION"));
        } else {
            linearLayout.addView(buildHeaderBox(context, "SETTINGS"));
            linearLayout.addView(buildFeatureButton(context, "BLOCK INTERNET CONNECTION"));
            linearLayout.addView(buildFeatureButton(context, "ENABLE ANTIBAN FEATURE"));
            linearLayout.addView(buildInfoBox(context));
            linearLayout.addView(buildFeatureButton(context, "BYPASS CLIENT"));
        }
        scrollView.addView(linearLayout);
        return scrollView;
    }

    /* JADX INFO: renamed from: com.s.OverlayService$100000007, reason: invalid class name */
    class AnonymousClass100000007 implements ImGuiColorPicker.OnColorChanged {
        private final OverlayService this$0;
        private final /* synthetic */ GradientDrawable val$previewBg;

        AnonymousClass100000007(OverlayService overlayService, GradientDrawable gradientDrawable) {
            this.this$0 = overlayService;
            this.val$previewBg = gradientDrawable;
        }

        @Override // com.s.OverlayService.ImGuiColorPicker.OnColorChanged
        public void onChanged(int i) {
            OverlayService.access$S1000008(this.this$0, i);
            this.val$previewBg.setColor(i);
            if (this.this$0.activeTab != null) {
                this.this$0.activeTab.color = i;
                this.this$0.activeTab.invalidate();
            }
        }
    }

    private View buildHeaderBox(Context context, String str) {
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextColor(cTextLight());
        textView.setTextSize(2, 13);
        textView.setTypeface(Typeface.DEFAULT);
        textView.setGravity(17);
        textView.setPadding(0, m1dp(12), 0, m1dp(12));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setStroke(m1dp(1), cStroke());
        gradientDrawable.setCornerRadius(m1dp(2));
        textView.setBackground(gradientDrawable);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = m1dp(16);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    /* JADX INFO: renamed from: com.s.OverlayService$100000009, reason: invalid class name */
    class AnonymousClass100000009 implements View.OnClickListener {
        private final OverlayService this$0;
        private final /* synthetic */ CheckboxView val$check;

        AnonymousClass100000009(OverlayService overlayService, CheckboxView checkboxView) {
            this.this$0 = overlayService;
            this.val$check = checkboxView;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            this.val$check.toggle();
            OverlayService overlayService = this.this$0;
            OverlayService.access$S1000009(overlayService, !OverlayService.access$L1000009(overlayService));
            if (this.this$0.activeTab != null) {
                this.this$0.activeTab.setVisibility(OverlayService.access$L1000009(this.this$0) ? 0 : 8);
            }
        }
    }

    private View buildInfoBox(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SilentPrefs", 0);
        String string = sharedPreferences.getString("user_name", "Dexter_Dev");
        String string2 = sharedPreferences.getString("discord_id", "N/A");
        String string3 = sharedPreferences.getString("user_expire", "N/A");
        String string4 = sharedPreferences.getString("user_days", "0");
        TextView textView = new TextView(context);
        StringBuilder sb = new StringBuilder();
        sb.append("USERNAME: ").append(string.toUpperCase()).append("\n");
        sb.append("DISCORD ID: ").append(string2).append("\n");
        sb.append("EXPIRY: ").append(string3).append(" (").append(string4).append(" DAYS)\n");
        sb.append("SUBSCRIPTION: SILENT HEADTRICK");
        textView.setText(sb.toString());
        textView.setTextColor(cTextMuted());
        textView.setTextSize(2, 10);
        textView.setLineSpacing(m1dp(6), 1.0f);
        textView.setPadding(m1dp(12), m1dp(12), m1dp(12), m1dp(12));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#101010"));
        gradientDrawable.setStroke(m1dp(1), Color.parseColor("#1F1F1F"));
        gradientDrawable.setCornerRadius(m1dp(2));
        textView.setBackground(gradientDrawable);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        ((ViewGroup.MarginLayoutParams) layoutParams).topMargin = m1dp(8);
        ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = m1dp(16);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    public static class ImGuiColorPicker extends View {
        private float hue;
        private RectF hueRect;
        public OnColorChanged listener;

        /* JADX INFO: renamed from: p */
        private Paint f13p;
        private float sat;
        private boolean slidingHue;
        private RectF svRect;
        private float val;

        public interface OnColorChanged {
            void onChanged(int i);
        }

        public ImGuiColorPicker(Context context, int i) {
            super(context);
            this.f13p = new Paint(1);
            this.svRect = new RectF();
            this.hueRect = new RectF();
            this.slidingHue = false;
            float[] fArr = new float[3];
            Color.colorToHSV(i, fArr);
            this.hue = fArr[0];
            this.sat = fArr[1];
            this.val = fArr[2];
        }

        private float dpVal(float f) {
            return TypedValue.applyDimension(1, f, getResources().getDisplayMetrics());
        }

        @Override // android.view.View
        protected void onSizeChanged(int i, int i2, int i3, int i4) {
            super.onSizeChanged(i, i2, i3, i4);
            float f = 0;
            float f2 = i;
            float f3 = i2;
            float fDpVal = f3 - dpVal(16);
            this.svRect.set(f, f, f2, fDpVal - dpVal(8));
            this.hueRect.set(f, fDpVal, f2, f3);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            this.f13p.setShader(new LinearGradient(this.svRect.left, this.svRect.top, this.svRect.right, this.svRect.top, -1, Color.HSVToColor(new float[]{this.hue, 1.0f, 1.0f}), Shader.TileMode.CLAMP));
            canvas.drawRect(this.svRect, this.f13p);
            this.f13p.setShader(new LinearGradient(this.svRect.left, this.svRect.top, this.svRect.left, this.svRect.bottom, 0, -16777216, Shader.TileMode.CLAMP));
            canvas.drawRect(this.svRect, this.f13p);
            float fWidth = this.svRect.left + (this.sat * this.svRect.width());
            float fHeight = this.svRect.top + ((1.0f - this.val) * this.svRect.height());
            this.f13p.setShader(null);
            this.f13p.setStyle(Paint.Style.STROKE);
            this.f13p.setColor(-1);
            float f = 2;
            this.f13p.setStrokeWidth(dpVal(f));
            float f2 = 5;
            canvas.drawCircle(fWidth, fHeight, dpVal(f2), this.f13p);
            this.f13p.setColor(-16777216);
            this.f13p.setStrokeWidth(dpVal(1));
            canvas.drawCircle(fWidth, fHeight, dpVal(f2), this.f13p);
            this.f13p.setStyle(Paint.Style.FILL);
            this.f13p.setShader(new LinearGradient(this.hueRect.left, this.hueRect.top, this.hueRect.right, this.hueRect.top, new int[]{-65536, -256, -16711936, -16711681, -16776961, -65281, -65536}, (float[]) null, Shader.TileMode.CLAMP));
            float f3 = 4;
            canvas.drawRoundRect(this.hueRect, dpVal(f3), dpVal(f3), this.f13p);
            float fWidth2 = this.hueRect.left + ((this.hue / 360.0f) * this.hueRect.width());
            this.f13p.setShader(null);
            this.f13p.setColor(-1);
            canvas.drawRect(fWidth2 - dpVal(f), this.hueRect.top - dpVal(f), fWidth2 + dpVal(f), dpVal(f) + this.hueRect.bottom, this.f13p);
            this.f13p.setColor(-16777216);
            this.f13p.setStyle(Paint.Style.STROKE);
            this.f13p.setStrokeWidth(1.0f);
            canvas.drawRect(fWidth2 - dpVal(f), this.hueRect.top - dpVal(f), fWidth2 + dpVal(f), dpVal(f) + this.hueRect.bottom, this.f13p);
            this.f13p.setStyle(Paint.Style.FILL);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            switch (motionEvent.getAction()) {
                case 0:
                    this.slidingHue = this.hueRect.contains(x, y) || y > this.svRect.bottom;
                    break;
                case 1:
                default:
                    return super.onTouchEvent(motionEvent);
                case 2:
                    break;
            }
            if (this.slidingHue) {
                float fMax = Math.max(0, Math.min(1, x / getWidth())) * 360.0f;
                this.hue = fMax;
                if (fMax >= 360.0f) {
                    this.hue = 359.99f;
                }
            } else {
                float f = 0;
                float f2 = 1;
                this.sat = Math.max(f, Math.min(f2, x / getWidth()));
                this.val = 1.0f - Math.max(f, Math.min(f2, y / this.svRect.height()));
            }
            OnColorChanged onColorChanged = this.listener;
            if (onColorChanged != null) {
                onColorChanged.onChanged(Color.HSVToColor(new float[]{this.hue, this.sat, this.val}));
            }
            invalidate();
            return true;
        }
    }

    private View buildFeatureButton(Context context, String str) {
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextSize(2, 11);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setGravity(17);
        textView.setPadding(0, m1dp(12), 0, m1dp(12));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = m1dp(8);
        textView.setLayoutParams(layoutParams);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(m1dp(2));
        applyButtonVisuals(textView, gradientDrawable, this.featureStates.containsKey(str) ? this.featureStates.get(str).intValue() : 0);
        textView.setOnClickListener(new AnonymousClass100000008(this, str, textView, gradientDrawable));
        return textView;
    }

    /* JADX INFO: renamed from: com.s.OverlayService$100000008, reason: invalid class name */
    class AnonymousClass100000008 implements View.OnClickListener {
        private final OverlayService this$0;
        private final /* synthetic */ GradientDrawable val$bg;
        private final /* synthetic */ TextView val$btn;
        private final /* synthetic */ String val$title;

        AnonymousClass100000008(OverlayService overlayService, String str, TextView textView, GradientDrawable gradientDrawable) {
            this.this$0 = overlayService;
            this.val$title = str;
            this.val$btn = textView;
            this.val$bg = gradientDrawable;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            int iIntValue = this.this$0.featureStates.containsKey(this.val$title) ? ((Integer) this.this$0.featureStates.get(this.val$title)).intValue() : 0;
            if (iIntValue == 0) {
                this.this$0.featureStates.put(this.val$title, new Integer(1));
                this.this$0.applyButtonVisuals(this.val$btn, this.val$bg, 1);
                this.this$0.setGlobalStatus(new StringBuffer().append(new StringBuffer().append("INJECTING ").append(this.val$title).toString()).append("...").toString(), this.this$0.cBtnRed());
                Handler handler = new Handler(Looper.getMainLooper());
                final String str = this.val$title;
                final TextView textView = this.val$btn;
                final GradientDrawable gradientDrawable = this.val$bg;
                handler.postDelayed(new Runnable(this) { // from class: com.s.OverlayService.100000008.100000007
                    private final AnonymousClass100000008 this$0;

                    {
                        this.this$0 = this;
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        if (this.this$0.this$0.featureStates.containsKey(str) && ((Integer) this.this$0.this$0.featureStates.get(str)).intValue() == 1) {
                            this.this$0.this$0.featureStates.put(str, new Integer(2));
                            this.this$0.this$0.applyButtonVisuals(textView, gradientDrawable, 2);
                            this.this$0.this$0.setGlobalStatus(new StringBuffer().append(str).append(" SUCCESS").toString(), this.this$0.this$0.cBtnGreen());
                        }
                    }
                }, 2000);
                return;
            }
            if (iIntValue == 2 || iIntValue == 1) {
                this.this$0.featureStates.put(this.val$title, new Integer(0));
                this.this$0.applyButtonVisuals(this.val$btn, this.val$bg, 0);
                this.this$0.setGlobalStatus(new StringBuffer().append("DISABLED ").append(this.val$title).toString(), this.this$0.cTextMuted());
            }
        }
    }

    public static class CheckboxView extends View {
        private int cAccent;
        private boolean isLight;

        /* JADX INFO: renamed from: on */
        private boolean f8on;

        /* JADX INFO: renamed from: p */
        private final Paint f9p;
        private float prog;

        /* JADX INFO: renamed from: com.s.OverlayService$CheckboxView$100000006, reason: invalid class name */
        class AnonymousClass100000006 implements View.OnClickListener {
            private final CheckboxView this$0;

            AnonymousClass100000006(CheckboxView checkboxView) {
                this.this$0 = checkboxView;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                this.this$0.toggle();
            }
        }

        /* JADX INFO: renamed from: com.s.OverlayService$CheckboxView$100000007, reason: invalid class name */
        class AnonymousClass100000007 implements ValueAnimator.AnimatorUpdateListener {
            private final CheckboxView this$0;

            AnonymousClass100000007(CheckboxView checkboxView) {
                this.this$0 = checkboxView;
            }

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                CheckboxView.access$S1000048(this.this$0, ((Float) valueAnimator.getAnimatedValue()).floatValue());
                this.this$0.invalidate();
            }
        }

        /* JADX INFO: renamed from: com.s.OverlayService$CheckboxView$100000013, reason: invalid class name */
        class AnonymousClass100000013 implements ValueAnimator.AnimatorUpdateListener {
            private final CheckboxView this$0;

            AnonymousClass100000013(CheckboxView checkboxView) {
                this.this$0 = checkboxView;
            }

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                CheckboxView.access$S1000086(this.this$0, ((Float) valueAnimator.getAnimatedValue()).floatValue());
                this.this$0.invalidate();
            }
        }

        /* JADX INFO: renamed from: com.s.OverlayService$CheckboxView$100000015, reason: invalid class name */
        class AnonymousClass100000015 implements ValueAnimator.AnimatorUpdateListener {
            private final CheckboxView this$0;

            AnonymousClass100000015(CheckboxView checkboxView) {
                this.this$0 = checkboxView;
            }

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                CheckboxView.access$S1000095(this.this$0, ((Float) valueAnimator.getAnimatedValue()).floatValue());
                this.this$0.invalidate();
            }
        }

        public CheckboxView(Context context, int i, boolean z) {
            super(context);
            this.f8on = false;
            this.prog = 0.0f;
            this.f9p = new Paint(1);
            this.cAccent = i;
            this.isLight = z;
            setClickable(true);
        }

        /* JADX INFO: renamed from: com.s.OverlayService$CheckboxView$100000009, reason: invalid class name */
        class AnonymousClass100000009 implements ValueAnimator.AnimatorUpdateListener {
            private final CheckboxView this$0;

            AnonymousClass100000009(CheckboxView checkboxView) {
                this.this$0 = checkboxView;
            }

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                CheckboxView.access$S1000064(this.this$0, ((Float) valueAnimator.getAnimatedValue()).floatValue());
                this.this$0.invalidate();
            }
        }

        public void forceState() {
            this.f8on = true;
            this.prog = 1.0f;
            invalidate();
        }

        public void toggle() {
            boolean z = !this.f8on;
            this.f8on = z;
            float[] fArr = new float[2];
            fArr[0] = this.prog;
            fArr[1] = z ? 1.0f : 0.0f;
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(fArr);
            valueAnimatorOfFloat.setDuration(150);
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(this) { // from class: com.s.OverlayService.CheckboxView.100000016
                private final CheckboxView this$0;

                {
                    this.this$0 = this;
                }

                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.this$0.prog = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    this.this$0.invalidate();
                }
            });
            valueAnimatorOfFloat.start();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float width = getWidth();
            float height = getHeight();
            float f = width / 2.0f;
            float f2 = height / 2.0f;
            float fMin = Math.min(width, height) / 2.0f;
            this.f9p.setColor(blend(Color.parseColor(this.isLight ? "#E5E5EA" : "#26282E"), this.cAccent, this.prog));
            this.f9p.setStyle(Paint.Style.FILL);
            canvas.drawCircle(f, f2, fMin, this.f9p);
            if (this.prog > 0.1f) {
                this.f9p.setColor(-1);
                this.f9p.setStyle(Paint.Style.STROKE);
                this.f9p.setStrokeWidth(width * 0.12f);
                this.f9p.setStrokeCap(Paint.Cap.ROUND);
                this.f9p.setStrokeJoin(Paint.Join.ROUND);
                Path path = new Path();
                float f3 = f - (0.35f * fMin);
                float f4 = f2 + (0.05f * fMin);
                path.moveTo(f3, f4);
                float f5 = 0.25f * fMin;
                path.lineTo(f3 + f5, f5 + f4);
                path.lineTo(f3 + (0.8f * fMin), f4 - (fMin * 0.4f));
                this.f9p.setAlpha((int) (255 * this.prog));
                canvas.drawPath(path, this.f9p);
            }
        }

        private int blend(int i, int i2, float f) {
            int i3 = (i >> 16) & 255;
            int i4 = (i >> 8) & 255;
            int i5 = i & 255;
            return ((int) (i5 + (((i2 & 255) - i5) * f))) | (-16777216) | (((int) (i3 + ((((i2 >> 16) & 255) - i3) * f))) << 16) | (((int) (i4 + ((((i2 >> 8) & 255) - i4) * f))) << 8);
        }

        /* JADX INFO: renamed from: com.s.OverlayService$CheckboxView$100000010, reason: invalid class name */
        class AnonymousClass100000010 implements ValueAnimator.AnimatorUpdateListener {
            private final CheckboxView this$0;

            AnonymousClass100000010(CheckboxView checkboxView) {
                this.this$0 = checkboxView;
            }

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                CheckboxView.access$S1000071(this.this$0, ((Float) valueAnimator.getAnimatedValue()).floatValue());
                this.this$0.invalidate();
            }
        }

        /* JADX INFO: renamed from: com.s.OverlayService$CheckboxView$100000012, reason: invalid class name */
        class AnonymousClass100000012 implements ValueAnimator.AnimatorUpdateListener {
            private final CheckboxView this$0;

            AnonymousClass100000012(CheckboxView checkboxView) {
                this.this$0 = checkboxView;
            }

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                CheckboxView.access$S1000078(this.this$0, ((Float) valueAnimator.getAnimatedValue()).floatValue());
                this.this$0.invalidate();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyButtonVisuals(TextView textView, GradientDrawable gradientDrawable, int i) {
        if (i == 0) {
            gradientDrawable.setColor(cBtnRed());
            textView.setTextColor(cBtnRedText());
        } else if (i == 1) {
            gradientDrawable.setColor(cBtnLoading());
            textView.setTextColor(cBtnLoadingText());
        } else if (i == 2) {
            gradientDrawable.setColor(cBtnGreen());
            textView.setTextColor(cBtnGreenText());
        }
        textView.setBackground(gradientDrawable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setGlobalStatus(String str, int i) {
        String string = new StringBuffer().append("STATUS: ").append(str.toUpperCase()).toString();
        this.currentStatusText = string;
        this.currentStatusColor = i;
        TextView textView = this.statusTextView;
        if (textView != null) {
            textView.setText(string);
            this.statusTextView.setTextColor(this.currentStatusColor);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: dp */
    public int m1dp(int i) {
        return (int) TypedValue.applyDimension(1, i, getResources().getDisplayMetrics());
    }

    /* JADX INFO: renamed from: com.s.OverlayService$100000010, reason: invalid class name */
    class AnonymousClass100000010 implements View.OnClickListener {
        private final OverlayService this$0;
        private final /* synthetic */ int val$idx;
        private final /* synthetic */ LinearLayout[] val$optionViews;
        private final /* synthetic */ String[] val$triggers;

        AnonymousClass100000010(OverlayService overlayService, int i, String[] strArr, LinearLayout[] linearLayoutArr) {
            this.this$0 = overlayService;
            this.val$idx = i;
            this.val$triggers = strArr;
            this.val$optionViews = linearLayoutArr;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            OverlayService.access$S1000010(this.this$0, this.val$idx);
            int i = 0;
            while (i < this.val$triggers.length) {
                OverlayService overlayService = this.this$0;
                LinearLayout linearLayout = this.val$optionViews[i];
                OverlayService.access$1000070(overlayService, linearLayout, (TextView) linearLayout.getChildAt(0), i == this.this$0.featureStates);
                i++;
            }
        }
    }

    public static class IconView extends View {
        private final int color;
        private final String kind;

        /* JADX INFO: renamed from: p */
        private final Paint f12p;

        public IconView(Context context, String str, boolean z, int i, int i2) {
            super(context);
            Paint paint = new Paint(1);
            this.f12p = paint;
            this.kind = str;
            this.color = z ? i : i2;
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float width = getWidth();
            float height = getHeight();
            float f = width / 2.0f;
            float f2 = height / 2.0f;
            float fMin = (Math.min(width, height) / 2.0f) - (getResources().getDisplayMetrics().density * 1.0f);
            this.f12p.setColor(this.color);
            String str = this.kind;
            if (!str.equals("SKULL")) {
                if (str.equals("GEAR")) {
                    this.f12p.setStyle(Paint.Style.STROKE);
                    this.f12p.setStrokeWidth(Math.max(2.0f, width * 0.08f));
                    float f3 = fMin * 0.5f;
                    canvas.drawCircle(f, f2, f3, this.f12p);
                    for (int i = 0; i < 8; i++) {
                        canvas.save();
                        canvas.rotate(i * 45, f, f2);
                        canvas.drawLine(f, f2 - f3, f, f2 - (0.85f * fMin), this.f12p);
                        canvas.restore();
                    }
                    return;
                }
                if (str.equals("CLOSE")) {
                    this.f12p.setStyle(Paint.Style.STROKE);
                    this.f12p.setStrokeWidth(Math.max(2.0f, width * 0.12f));
                    float f4 = 0.3f * fMin;
                    float f5 = (f - fMin) + f4;
                    float f6 = (f2 - fMin) + f4;
                    float f7 = (f + fMin) - f4;
                    float f8 = (f2 + fMin) - f4;
                    canvas.drawLine(f5, f6, f7, f8, this.f12p);
                    canvas.drawLine(f7, f6, f5, f8, this.f12p);
                    return;
                }
                return;
            }
            this.f12p.setStyle(Paint.Style.FILL);
            float f9 = fMin * 0.15f;
            canvas.drawCircle(f, f2 - f9, 0.6f * fMin, this.f12p);
            float f10 = 0.4f * fMin;
            float f11 = f2 + (0.7f * fMin);
            canvas.drawRect(f - f10, f2, f + f10, f11, this.f12p);
            this.f12p.setColor(Color.parseColor("#191919"));
            float f12 = 0.25f * fMin;
            float f13 = 0.1f * fMin;
            float f14 = f2 - f13;
            float f15 = 0.2f * fMin;
            canvas.drawCircle(f - f12, f14, f15, this.f12p);
            canvas.drawCircle(f12 + f, f14, f15, this.f12p);
            Path path = new Path();
            path.moveTo(f, f2 + f13);
            float f16 = (0.35f * fMin) + f2;
            path.lineTo(f - f13, f16);
            path.lineTo(f13 + f, f16);
            path.close();
            canvas.drawPath(path, this.f12p);
            this.f12p.setColor(this.color);
            this.f12p.setStyle(Paint.Style.STROKE);
            this.f12p.setStrokeWidth(2.0f);
            float f17 = f - f9;
            float f18 = f2 + (fMin * 0.45f);
            canvas.drawLine(f17, f18, f17, f11, this.f12p);
            canvas.drawLine(f, f18, f, f11, this.f12p);
            float f19 = f + f9;
            canvas.drawLine(f19, f18, f19, f11, this.f12p);
        }
    }

    public static class CrossView extends View {

        /* JADX INFO: renamed from: p */
        private final Paint f10p;

        public CrossView(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.f10p = paint;
            paint.setColor(Color.parseColor("#C8C8CE"));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float width = getWidth();
            float height = getHeight();
            this.f10p.setStrokeWidth(Math.max(2.0f, 0.14f * width));
            float f = 0.18f * width;
            float f2 = width - f;
            float f3 = height - f;
            canvas.drawLine(f, f, f2, f3, this.f10p);
            canvas.drawLine(f2, f, f, f3, this.f10p);
        }
    }

    /* JADX INFO: renamed from: com.s.OverlayService$100000011, reason: invalid class name */
    class AnonymousClass100000011 implements SliderBar.OnValueChangedListener {
        private final OverlayService this$0;
        private final /* synthetic */ boolean val$isRadius;
        private final /* synthetic */ TextView val$valText;

        AnonymousClass100000011(OverlayService overlayService, TextView textView, boolean z) {
            this.this$0 = overlayService;
            this.val$valText = textView;
            this.val$isRadius = z;
        }

        @Override // com.s.OverlayService.SliderBar.OnValueChangedListener
        public void onValueChanged(int i) {
            this.val$valText.setText(String.valueOf(i));
            if (this.val$isRadius) {
                OverlayService.access$S1000006(this.this$0, i);
                if (this.this$0.activeTab != null) {
                    this.this$0.activeTab.radius = OverlayService.access$1000082(this.this$0, i);
                    this.this$0.activeTab.invalidate();
                    return;
                }
                return;
            }
            OverlayService.access$S1000007(this.this$0, i);
            if (this.this$0.activeTab != null) {
                this.this$0.activeTab.thickness = OverlayService.access$1000082(this.this$0, i);
                this.this$0.activeTab.invalidate();
            }
        }
    }

    /* JADX INFO: renamed from: com.s.OverlayService$100000012, reason: invalid class name */
    class AnonymousClass100000012 implements SliderBar.OnValueChangedListener {
        private final OverlayService this$0;
        private final /* synthetic */ TextView val$valText;

        AnonymousClass100000012(OverlayService overlayService, TextView textView) {
            this.this$0 = overlayService;
            this.val$valText = textView;
        }

        @Override // com.s.OverlayService.SliderBar.OnValueChangedListener
        public void onValueChanged(int i) {
            this.val$valText.setText(String.valueOf(i));
        }
    }

    /* JADX INFO: renamed from: com.s.OverlayService$100000013, reason: invalid class name */
    class AnonymousClass100000013 implements View.OnClickListener {
        private final OverlayService this$0;
        private final /* synthetic */ CheckboxView val$check;

        AnonymousClass100000013(OverlayService overlayService, CheckboxView checkboxView) {
            this.this$0 = overlayService;
            this.val$check = checkboxView;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            this.val$check.toggle();
        }
    }

    public static class SliderBarFloat extends View {
        private int cActive;
        private int cTrack;
        private OnValueChangedListener listener;
        private float max;
        private float min;

        /* JADX INFO: renamed from: p */
        private final Paint f15p;
        private float thumbX;
        private float val;

        public interface OnValueChangedListener {
            void onValueChanged(float f);
        }

        public SliderBarFloat(Context context, float f, float f2, float f3, int i, int i2) {
            super(context);
            this.f15p = new Paint(1);
            this.thumbX = 0.0f;
            this.min = f;
            this.max = f2;
            this.val = f3;
            this.cActive = i;
            this.cTrack = i2;
        }

        public void setListener(OnValueChangedListener onValueChangedListener) {
            this.listener = onValueChangedListener;
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float width = getWidth();
            float height = getHeight() / 2.0f;
            float f = this.val;
            float f2 = this.min;
            this.thumbX = ((width - (2 * height)) * ((f - f2) / (this.max - f2))) + height;
            this.f15p.setStrokeCap(Paint.Cap.ROUND);
            this.f15p.setStrokeWidth(0.015f * width);
            this.f15p.setColor(this.cTrack);
            canvas.drawLine(height, height, width - height, height, this.f15p);
            this.f15p.setColor(this.cActive);
            canvas.drawLine(height, height, this.thumbX, height, this.f15p);
            this.f15p.setStyle(Paint.Style.FILL);
            canvas.drawCircle(this.thumbX, height, 0.6f * height, this.f15p);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            float width = getWidth();
            float height = getHeight() / 2.0f;
            float f = width - (2 * height);
            float x = motionEvent.getX() - height;
            float f2 = 0;
            if (x < f2) {
                x = f2;
            }
            if (x > f) {
                x = f;
            }
            float f3 = x / f;
            float f4 = this.min;
            float f5 = f4 + (f3 * (this.max - f4));
            if (f5 != this.val) {
                this.val = f5;
                OnValueChangedListener onValueChangedListener = this.listener;
                if (onValueChangedListener != null) {
                    onValueChangedListener.onValueChanged(f5);
                }
                invalidate();
                return true;
            }
            return true;
        }
    }

    /* JADX INFO: renamed from: com.s.OverlayService$100000014, reason: invalid class name */
    class AnonymousClass100000014 implements View.OnClickListener {
        private final OverlayService this$0;
        private final /* synthetic */ LinearLayout val$bodyLayout;
        private final /* synthetic */ IconView val$checkIcon;
        private final /* synthetic */ LinearLayout val$headerBtn;
        private final /* synthetic */ int val$idx;
        private final /* synthetic */ OnOptionSelected val$listener;
        private final /* synthetic */ LinearLayout val$optRow;
        private final /* synthetic */ TextView val$optT;
        private final /* synthetic */ View[] val$optionViews;
        private final /* synthetic */ String[] val$options;
        private final /* synthetic */ TextView val$selectedText;

        AnonymousClass100000014(OverlayService overlayService, TextView textView, String[] strArr, int i, LinearLayout linearLayout, LinearLayout linearLayout2, View[] viewArr, LinearLayout linearLayout3, TextView textView2, IconView iconView, OnOptionSelected onOptionSelected) {
            this.this$0 = overlayService;
            this.val$selectedText = textView;
            this.val$options = strArr;
            this.val$idx = i;
            this.val$bodyLayout = linearLayout;
            this.val$headerBtn = linearLayout2;
            this.val$optionViews = viewArr;
            this.val$optRow = linearLayout3;
            this.val$optT = textView2;
            this.val$checkIcon = iconView;
            this.val$listener = onOptionSelected;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            this.val$selectedText.setText(this.val$options[this.val$idx]);
            this.val$bodyLayout.setVisibility(8);
            this.val$headerBtn.setVisibility(0);
            for (int i = 0; i < this.val$options.length; i++) {
                LinearLayout linearLayout = (LinearLayout) this.val$optionViews[i];
                TextView textView = (TextView) linearLayout.getChildAt(0);
                View childAt = linearLayout.getChildAt(1);
                linearLayout.setBackground(null);
                textView.setTextColor(OverlayService.access$1000021(this.this$0));
                childAt.setVisibility(8);
            }
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(this.this$0.buildAndShowMenu());
            gradientDrawable.setCornerRadius(OverlayService.access$1000082(this.this$0, 8));
            this.val$optRow.setBackground(gradientDrawable);
            this.val$optT.setTextColor(this.this$0.closeMenu());
            this.val$checkIcon.setVisibility(0);
            OnOptionSelected onOptionSelected = this.val$listener;
            if (onOptionSelected != null) {
                onOptionSelected.onSelect(this.val$idx);
            }
        }
    }

    public static class ToggleView extends View {

        /* JADX INFO: renamed from: on */
        private boolean f16on;

        /* JADX INFO: renamed from: p */
        private final Paint f17p;
        private float prog;

        /* JADX INFO: renamed from: r */
        private final RectF f18r;

        public ToggleView(Context context) {
            super(context);
            this.f16on = false;
            this.prog = 0.0f;
            this.f17p = new Paint(1);
            this.f18r = new RectF();
            setClickable(true);
            setOnClickListener(new View.OnClickListener() { // from class: com.s.OverlayService.ToggleView.100000005
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ToggleView.this.toggle();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void toggle() {
            boolean z = !this.f16on;
            this.f16on = z;
            float[] fArr = new float[2];
            fArr[0] = this.prog;
            fArr[1] = z ? 1.0f : 0.0f;
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(fArr);
            valueAnimatorOfFloat.setDuration(180);
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.s.OverlayService.ToggleView.100000006
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ToggleView.this.prog = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    ToggleView.this.invalidate();
                }
            });
            valueAnimatorOfFloat.start();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float width = getWidth();
            float height = getHeight();
            float f = 0;
            this.f18r.set(f, f, width, height);
            this.f17p.setColor(blend(Color.parseColor("#1E1E22"), Color.parseColor("#1F3A6B"), this.prog));
            float f2 = height / 2.0f;
            canvas.drawRoundRect(this.f18r, f2, f2, this.f17p);
            this.f17p.setStyle(Paint.Style.STROKE);
            this.f17p.setStrokeWidth(1.0f);
            this.f17p.setColor(Color.parseColor("#2A2A30"));
            this.f18r.inset(0.5f, 0.5f);
            canvas.drawRoundRect(this.f18r, f2, f2, this.f17p);
            this.f17p.setStyle(Paint.Style.FILL);
            this.f18r.inset(-0.5f, -0.5f);
            float f3 = 0.18f * height;
            float f4 = 2;
            float f5 = f3 * f4;
            float f6 = (height - f5) / 2.0f;
            float f7 = f3 + f6 + (((width - f5) - (f4 * f6)) * this.prog);
            this.f17p.setColor(Color.parseColor("#00000055"));
            canvas.drawCircle(f7, 1.5f + f2, f6, this.f17p);
            this.f17p.setColor(blend(Color.parseColor("#9A9AA2"), Color.parseColor("#E6E6EA"), this.prog));
            canvas.drawCircle(f7, f2, f6, this.f17p);
        }

        private int blend(int i, int i2, float f) {
            int i3 = (i >> 16) & 255;
            int i4 = (i >> 8) & 255;
            int i5 = i & 255;
            return ((int) (i5 + (((i2 & 255) - i5) * f))) | (-16777216) | (((int) (i3 + ((((i2 >> 16) & 255) - i3) * f))) << 16) | (((int) (i4 + ((((i2 >> 8) & 255) - i4) * f))) << 8);
        }
    }

    /* JADX INFO: renamed from: com.s.OverlayService$100000015, reason: invalid class name */
    class AnonymousClass100000015 implements View.OnClickListener {
        private final OverlayService this$0;
        private final /* synthetic */ LinearLayout val$bodyLayout;
        private final /* synthetic */ LinearLayout val$headerBtn;

        AnonymousClass100000015(OverlayService overlayService, LinearLayout linearLayout, LinearLayout linearLayout2) {
            this.this$0 = overlayService;
            this.val$headerBtn = linearLayout;
            this.val$bodyLayout = linearLayout2;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            this.val$headerBtn.setVisibility(8);
            this.val$bodyLayout.setVisibility(0);
        }
    }

    public static class SliderBar extends View {
        private int cActive;
        private int cTrack;
        private OnValueChangedListener listener;
        private int max;
        private int min;

        /* JADX INFO: renamed from: p */
        private final Paint f14p;
        private float thumbX;
        private int val;

        public interface OnValueChangedListener {
            void onValueChanged(int i);
        }

        public SliderBar(Context context, int i, int i2, int i3, int i4, int i5) {
            super(context);
            this.f14p = new Paint(1);
            this.thumbX = 0.0f;
            this.min = i;
            this.max = i2;
            this.val = i3;
            this.cActive = i4;
            this.cTrack = i5;
        }

        public void setListener(OnValueChangedListener onValueChangedListener) {
            this.listener = onValueChangedListener;
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float width = getWidth();
            float height = getHeight() / 2.0f;
            int i = this.val;
            int i2 = this.min;
            this.thumbX = ((width - (2 * height)) * ((i - i2) / (this.max - i2))) + height;
            this.f14p.setStrokeCap(Paint.Cap.ROUND);
            this.f14p.setStrokeWidth(0.015f * width);
            this.f14p.setColor(this.cTrack);
            canvas.drawLine(height, height, width - height, height, this.f14p);
            this.f14p.setColor(this.cActive);
            canvas.drawLine(height, height, this.thumbX, height, this.f14p);
            this.f14p.setStyle(Paint.Style.FILL);
            canvas.drawCircle(this.thumbX, height, 0.6f * height, this.f14p);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            float width = getWidth();
            float height = getHeight() / 2.0f;
            float f = width - (2 * height);
            float x = motionEvent.getX() - height;
            float f2 = 0;
            if (x < f2) {
                x = f2;
            }
            if (x > f) {
                x = f;
            }
            int i = this.min;
            int iRound = Math.round(i + ((x / f) * (this.max - i)));
            if (iRound != this.val) {
                this.val = iRound;
                OnValueChangedListener onValueChangedListener = this.listener;
                if (onValueChangedListener != null) {
                    onValueChangedListener.onValueChanged(iRound);
                }
                invalidate();
                return true;
            }
            return true;
        }
    }

    public static class LogoView extends View {
        private final Path clip;
        private final Paint paint;
        private final Paint text;

        public LogoView(Context context) {
            super(context);
            this.paint = new Paint(1);
            Paint paint = new Paint(1);
            this.text = paint;
            this.clip = new Path();
            paint.setColor(-1);
            paint.setFakeBoldText(true);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            int width = getWidth();
            int height = getHeight();
            float fMin = Math.min(width, height) / 2.0f;
            float f = width;
            float f2 = f / 2.0f;
            float f3 = height;
            float f4 = f3 / 2.0f;
            this.clip.reset();
            this.clip.addCircle(f2, f4, fMin, Path.Direction.CW);
            canvas.save();
            canvas.clipPath(this.clip);
            this.paint.setColor(Color.parseColor("#0A0A0C"));
            float f5 = 0;
            canvas.drawRect(f5, f5, f, f3, this.paint);
            this.paint.setShader(new LinearGradient(f5, f3, f, f5, Color.parseColor("#FF3B30"), Color.parseColor("#3B82F6"), Shader.TileMode.CLAMP));
            Path path = new Path();
            path.moveTo(f, f5);
            path.lineTo(f, f3);
            path.lineTo(f5, f3);
            path.close();
            canvas.drawPath(path, this.paint);
            this.paint.setShader(null);
            this.paint.setColor(-1);
            this.paint.setStrokeWidth(2.0f);
            canvas.drawLine(f5, f3, f, f5, this.paint);
            this.text.setTextSize(0.36f * fMin);
            this.text.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("ONIONDLC", f2, f4 + (this.text.getTextSize() / 3.0f), this.text);
            canvas.restore();
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth(1.5f);
            this.paint.setColor(Color.parseColor("#2A2A30"));
            canvas.drawCircle(f2, f4, fMin - 1, this.paint);
            this.paint.setStyle(Paint.Style.FILL);
        }
    }
}
