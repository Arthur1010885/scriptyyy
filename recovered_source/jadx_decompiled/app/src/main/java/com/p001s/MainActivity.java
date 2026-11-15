package com.p001s;

import adrt.ADRTLogCatReader;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.json.JSONObject;

/* JADX INFO: loaded from: /home/ubuntu/apk_source_work/raw/classes.dex */
public class MainActivity extends Activity {
    private static final String API_VERIFY_URL = "https://silent-clent.squareweb.app/api/verify";
    private SharedPreferences prefs;
    private FrameLayout root;
    private int screenHeight;
    private int screenWidth;
    private String userName = "";
    private String userExpireDate = "";
    private String userDaysLeft = "";
    private String userId = "";
    private String userAvatarUrl = "";
    private String userBannerUrl = "";
    private final String DEFAULT_AVATAR = "https://i.pinimg.com/736x/8f/1a/0b/8f1a0b3552dcbf78fcad855737bc28f4.jpg";
    private final String DEFAULT_BANNER = "https://i.pinimg.com/736x/f6/88/cb/f688cbd931bb0436d40df351536b3b55.jpg";
    private boolean exploitConfirmado = false;
    private final String COLOR_BG = "#000000";
    private final String COLOR_CARD = "#151515";
    private final String COLOR_TEXT_PRIMARY = "#FFFFFF";
    private final String COLOR_TEXT_SECONDARY = "#8E8E93";
    private final String COLOR_HIGHLIGHT = "#FFD60A";
    private final String COLOR_BLUE = "#0A84FF";
    private final String COLOR_GREEN = "#32D74B";
    private final String COLOR_SEPARATOR = "#2C2C2E";

    private static class FeatureState {
        boolean active = false;
        boolean injecting = false;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        ADRTLogCatReader.onContext(this, "aidepro.top");
        super.onCreate(bundle);
        trustAllCertificates();
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        getWindow().getDecorView().setSystemUiVisibility(5380);
        this.prefs = getSharedPreferences("SilentPrefs", 0);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.screenWidth = displayMetrics.widthPixels;
        this.screenHeight = displayMetrics.heightPixels;
        FrameLayout frameLayout = new FrameLayout(this);
        this.root = frameLayout;
        frameLayout.setBackgroundColor(Color.parseColor("#000000"));
        this.root.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        setContentView(this.root);
        checkOverlayPermission();
        this.userId = this.prefs.getString("discord_id", "");
        this.userName = this.prefs.getString("user_name", "");
        this.userExpireDate = this.prefs.getString("user_expire", "");
        this.userDaysLeft = this.prefs.getString("user_days", "");
        this.userAvatarUrl = this.prefs.getString("avatar_url", "https://i.pinimg.com/736x/8f/1a/0b/8f1a0b3552dcbf78fcad855737bc28f4.jpg");
        this.userBannerUrl = this.prefs.getString("banner_url", "https://i.pinimg.com/736x/f6/88/cb/f688cbd931bb0436d40df351536b3b55.jpg");
        if (!this.userId.isEmpty() && !this.userName.isEmpty()) {
            startOverlayService();
            showMainPanel();
        } else {
            showLoginScreen(this.userId, false);
        }
    }

    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this)) {
            return;
        }
        startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse(new StringBuffer().append("package:").append(getPackageName()).toString())));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startOverlayService() {
        try {
            try {
                startService(new Intent(this, Class.forName("com.s.OverlayService")));
            } catch (ClassNotFoundException e) {
                throw new NoClassDefFoundError(e.getMessage());
            }
        } catch (Exception e2) {
            Toast.makeText(this, "Permissão necessária.", 0).show();
        }
    }

    private void limparRoot() {
        this.root.removeAllViews();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showLoginScreen(String str, boolean z) {
        limparRoot();
        final FrameLayout frameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(m0dp(320), m0dp(300));
        layoutParams.gravity = 17;
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#181818"));
        gradientDrawable.setCornerRadius(m0dp(4));
        frameLayout.setBackground(gradientDrawable);
        frameLayout.setLayoutParams(layoutParams);
        TextView textView = new TextView(this);
        textView.setText("✕");
        textView.setTextColor(Color.parseColor("#555555"));
        float f = 14;
        textView.setTextSize(2, f);
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(-2, -2);
        layoutParams2.gravity = 53;
        float f2 = 15;
        ((ViewGroup.MarginLayoutParams) layoutParams2).topMargin = m0dp(f2);
        ((ViewGroup.MarginLayoutParams) layoutParams2).rightMargin = m0dp(f2);
        textView.setLayoutParams(layoutParams2);
        textView.setOnClickListener(new View.OnClickListener(this) { // from class: com.s.MainActivity.100000000
            private final MainActivity this$0;

            {
                this.this$0 = this;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                this.this$0.finish();
            }
        });
        frameLayout.addView(textView);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setGravity(17);
        float f3 = 30;
        float f4 = 0;
        linearLayout.setPadding(m0dp(f3), m0dp(f4), m0dp(f3), m0dp(f4));
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        TextView textView2 = new TextView(this);
        textView2.setText("SILENT");
        textView2.setTextColor(Color.parseColor("#9B3333"));
        textView2.setTextSize(2, 42);
        textView2.setTypeface(Typeface.create(Typeface.DEFAULT, 3));
        textView2.setTextScaleX(1.1f);
        textView2.setGravity(17);
        linearLayout.addView(textView2);
        TextView textView3 = new TextView(this);
        textView3.setText("tired of missing? your problems are over!");
        textView3.setTextColor(Color.parseColor("#888888"));
        textView3.setTextSize(2, 12);
        textView3.setGravity(17);
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(-1, -2);
        ((ViewGroup.MarginLayoutParams) layoutParams3).topMargin = m0dp(35);
        ((ViewGroup.MarginLayoutParams) layoutParams3).bottomMargin = m0dp(f2);
        textView3.setLayoutParams(layoutParams3);
        linearLayout.addView(textView3);
        final EditText editText = new EditText(this);
        editText.setHint("Discord ID");
        editText.setHintTextColor(Color.parseColor("#666666"));
        editText.setTextColor(-1);
        editText.setTextSize(2, f);
        float f5 = 20;
        editText.setPadding(m0dp(f5), m0dp(f2), m0dp(f5), m0dp(f2));
        editText.setSingleLine(true);
        editText.setGravity(17);
        editText.setImeOptions(6);
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setColor(Color.parseColor("#121212"));
        gradientDrawable2.setCornerRadius(m0dp(f3));
        gradientDrawable2.setStroke(m0dp(1), Color.parseColor("#2A2A2A"));
        editText.setBackground(gradientDrawable2);
        editText.setLayoutParams(new LinearLayout.LayoutParams(-1, m0dp(50)));
        if (!str.isEmpty()) {
            editText.setText(str);
        }
        linearLayout.addView(editText);
        final ProgressBar progressBar = new ProgressBar(this);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#9B3333"), PorterDuff.Mode.SRC_IN);
        LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(m0dp(f3), m0dp(f3));
        ((ViewGroup.MarginLayoutParams) layoutParams4).topMargin = m0dp(f2);
        progressBar.setLayoutParams(layoutParams4);
        progressBar.setVisibility(8);
        linearLayout.addView(progressBar);
        frameLayout.addView(linearLayout);
        final TextView textView4 = new TextView(this);
        textView4.setTextColor(Color.parseColor("#555555"));
        textView4.setTextSize(2, 11);
        textView4.setTypeface(Typeface.DEFAULT_BOLD);
        FrameLayout.LayoutParams layoutParams5 = new FrameLayout.LayoutParams(-2, -2);
        layoutParams5.gravity = 83;
        ((ViewGroup.MarginLayoutParams) layoutParams5).leftMargin = m0dp(f5);
        ((ViewGroup.MarginLayoutParams) layoutParams5).bottomMargin = m0dp(f5);
        textView4.setLayoutParams(layoutParams5);
        textView4.setText("STATUS:");
        frameLayout.addView(textView4);
        this.root.addView(frameLayout);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener(this) { // from class: com.s.MainActivity.100000001
            private final MainActivity this$0;

            {
                this.this$0 = this;
            }

            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView textView5, int i, KeyEvent keyEvent) {
                if (i == 6 || (keyEvent != null && keyEvent.getAction() == 0 && keyEvent.getKeyCode() == 66)) {
                    String strTrim = editText.getText().toString().trim();
                    if (!strTrim.isEmpty()) {
                        this.this$0.iniciarFluxoDeLogin(strTrim, textView4, editText, progressBar, frameLayout);
                        return true;
                    }
                    this.this$0.animacaoShake(editText);
                    return true;
                }
                return false;
            }
        });
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        frameLayout.startAnimation(alphaAnimation);
        if (z) {
            iniciarFluxoDeLogin(str, textView4, editText, progressBar, frameLayout);
        }
    }

    /* JADX INFO: renamed from: com.s.MainActivity$100000003, reason: invalid class name */
    class AnonymousClass100000003 implements View.OnClickListener {
        private final MainActivity this$0;
        private final /* synthetic */ String val$packageName;

        AnonymousClass100000003(MainActivity mainActivity, String str) {
            this.this$0 = mainActivity;
            this.val$packageName = str;
        }

        /* JADX INFO: renamed from: com.s.MainActivity$100000003$100000001, reason: invalid class name */
        class AnonymousClass100000001 implements Runnable {
            private final AnonymousClass100000003 this$0;
            private final /* synthetic */ String val$key;
            private final /* synthetic */ EditText val$keyInput;
            private final /* synthetic */ TextView val$loginBtn;
            private final /* synthetic */ String val$message;
            private final /* synthetic */ String val$status;
            private final /* synthetic */ TextView val$statusText;

            AnonymousClass100000001(AnonymousClass100000003 anonymousClass100000003, String str, String str2, TextView textView, String str3, TextView textView2, EditText editText) {
                this.this$0 = anonymousClass100000003;
                this.val$status = str;
                this.val$key = str2;
                this.val$statusText = textView;
                this.val$message = str3;
                this.val$loginBtn = textView2;
                this.val$keyInput = editText;
            }

            @Override // java.lang.Runnable
            public void run() {
                if ("valid".equals(this.val$status)) {
                    this.this$0.this$0.prefs.edit().putString("user_key", this.val$key).apply();
                    MainActivity.access$1000017(this.this$0.this$0);
                } else {
                    this.this$0.this$0.prefs.edit().remove("user_key").apply();
                    this.val$statusText.setTextColor(this.this$0.this$0.DEFAULT_AVATAR);
                    this.val$statusText.setText(this.val$message.isEmpty() ? "Chave inválida." : this.val$message);
                    MainActivity.access$1000015(this.this$0.this$0, this.val$loginBtn, this.val$keyInput);
                }
            }
        }

        /* JADX INFO: renamed from: com.s.MainActivity$100000003$100000002, reason: invalid class name */
        class AnonymousClass100000002 implements Runnable {
            private final AnonymousClass100000003 this$0;
            private final /* synthetic */ EditText val$keyInput;
            private final /* synthetic */ TextView val$loginBtn;
            private final /* synthetic */ TextView val$statusText;

            AnonymousClass100000002(AnonymousClass100000003 anonymousClass100000003, TextView textView, TextView textView2, EditText editText) {
                this.this$0 = anonymousClass100000003;
                this.val$statusText = textView;
                this.val$loginBtn = textView2;
                this.val$keyInput = editText;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.val$statusText.setTextColor(this.this$0.this$0.DEFAULT_AVATAR);
                this.val$statusText.setText("Erro de conexão com a API.");
                MainActivity.access$1000015(this.this$0.this$0, this.val$loginBtn, this.val$keyInput);
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (!this.this$0.userId) {
                MainActivity.access$1000033(this.this$0, this.val$packageName);
            } else {
                this.this$0.mostrarDialogoConfirmacao(this.val$packageName);
            }
        }
    }

    /* JADX INFO: renamed from: com.s.MainActivity$100000002, reason: invalid class name */
    class AnonymousClass100000002 implements Runnable {
        private final MainActivity this$0;
        private final /* synthetic */ String val$discordId;
        private final /* synthetic */ TextView val$statusText;

        AnonymousClass100000002(MainActivity mainActivity, String str, TextView textView) {
            this.this$0 = mainActivity;
            this.val$discordId = str;
            this.val$statusText = textView;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.this$0.prefs.edit().putString("discord_id", this.val$discordId).putString("user_name", this.this$0.userName).putString("user_days", this.this$0.userDaysLeft).apply();
            this.val$statusText.setText("STATUS: SUCESSO!");
            this.val$statusText.setTextColor(Color.parseColor("#32D74B"));
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable(this) { // from class: com.s.MainActivity.100000002.100000001
                private final AnonymousClass100000002 this$0;

                {
                    this.this$0 = this;
                }

                @Override // java.lang.Runnable
                public void run() {
                    MainActivity.access$1000025(this.this$0.this$0);
                }
            }, 600);
        }
    }

    /* JADX INFO: renamed from: com.s.MainActivity$100000004, reason: invalid class name */
    class AnonymousClass100000004 implements Runnable {
        private final MainActivity this$0;
        private final /* synthetic */ TextView val$btn;
        private final /* synthetic */ String val$discordId;
        private final /* synthetic */ EditText val$input;
        private final /* synthetic */ ProgressBar val$spinner;
        private final /* synthetic */ TextView val$statusText;

        AnonymousClass100000004(MainActivity mainActivity, String str, TextView textView, EditText editText, TextView textView2, ProgressBar progressBar) {
            this.this$0 = mainActivity;
            this.val$discordId = str;
            this.val$statusText = textView;
            this.val$input = editText;
            this.val$btn = textView2;
            this.val$spinner = progressBar;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("discord_id", this.val$discordId);
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("https://silent-clent.squareweb.app/api/verify").openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setConnectTimeout(8000);
                httpURLConnection.setReadTimeout(8000);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(jSONObject.toString().getBytes("UTF-8"));
                outputStream.close();
                int responseCode = httpURLConnection.getResponseCode();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((responseCode < 200 || responseCode > 299) ? httpURLConnection.getErrorStream() : httpURLConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line != null) {
                        sb.append(line);
                    } else {
                        bufferedReader.close();
                        httpURLConnection.disconnect();
                        JSONObject jSONObject2 = new JSONObject(sb.toString());
                        this.this$0.runOnUiThread(new AnonymousClass100000002(this, jSONObject2.optBoolean("authorized", false), this.val$discordId, jSONObject2, this.val$statusText, this.val$input, this.val$btn, this.val$spinner, jSONObject2.optString("reason", "")));
                        return;
                    }
                }
            } catch (Exception e) {
                MainActivity mainActivity = this.this$0;
                final EditText editText = this.val$input;
                final TextView textView = this.val$btn;
                final ProgressBar progressBar = this.val$spinner;
                final TextView textView2 = this.val$statusText;
                mainActivity.runOnUiThread(new Runnable(this) { // from class: com.s.MainActivity.100000004.100000003
                    private final AnonymousClass100000004 this$0;

                    {
                        this.this$0 = this;
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        editText.setEnabled(true);
                        textView.setVisibility(0);
                        progressBar.setVisibility(8);
                        textView2.setTextColor(Color.parseColor("#FF453A"));
                        textView2.setText("STATUS: ERRO DE CONEXÃO");
                        MainActivity.access$1000056(this.this$0.this$0, editText);
                    }
                });
            }
        }

        /* JADX INFO: renamed from: com.s.MainActivity$100000004$100000002, reason: invalid class name */
        class AnonymousClass100000002 implements Runnable {
            private final AnonymousClass100000004 this$0;
            private final /* synthetic */ boolean val$authorized;
            private final /* synthetic */ TextView val$btn;
            private final /* synthetic */ String val$discordId;
            private final /* synthetic */ EditText val$input;
            private final /* synthetic */ JSONObject val$jsonResponse;
            private final /* synthetic */ String val$reason;
            private final /* synthetic */ ProgressBar val$spinner;
            private final /* synthetic */ TextView val$statusText;

            AnonymousClass100000002(AnonymousClass100000004 anonymousClass100000004, boolean z, String str, JSONObject jSONObject, TextView textView, EditText editText, TextView textView2, ProgressBar progressBar, String str2) {
                this.this$0 = anonymousClass100000004;
                this.val$authorized = z;
                this.val$discordId = str;
                this.val$jsonResponse = jSONObject;
                this.val$statusText = textView;
                this.val$input = editText;
                this.val$btn = textView2;
                this.val$spinner = progressBar;
                this.val$reason = str2;
            }

            @Override // java.lang.Runnable
            public void run() {
                JSONObject jSONObjectOptJSONObject;
                JSONObject jSONObjectOptJSONObject2;
                if (this.val$authorized) {
                    this.this$0.this$0.userId = this.val$discordId;
                    this.this$0.this$0.userName = this.val$jsonResponse.optString("username", "Dexter");
                    this.this$0.this$0.userExpireDate = this.val$jsonResponse.optString("expire_date", "12/31/2026");
                    this.this$0.this$0.userDaysLeft = this.val$jsonResponse.optString("days_left", "99");
                    this.this$0.this$0.userAvatarUrl = this.val$jsonResponse.optString("avatar_url", "https://i.pinimg.com/736x/8f/1a/0b/8f1a0b3552dcbf78fcad855737bc28f4.jpg");
                    this.this$0.this$0.userBannerUrl = this.val$jsonResponse.optString("banner_url", "https://i.pinimg.com/736x/f6/88/cb/f688cbd931bb0436d40df351536b3b55.jpg");
                    try {
                        JSONObject jSONObjectOptJSONObject3 = this.val$jsonResponse.optJSONObject("users");
                        if (jSONObjectOptJSONObject3 != null && (jSONObjectOptJSONObject = jSONObjectOptJSONObject3.optJSONObject(this.val$discordId)) != null && (jSONObjectOptJSONObject2 = jSONObjectOptJSONObject.optJSONObject("profile")) != null) {
                            this.this$0.this$0.userName = jSONObjectOptJSONObject2.optString("username", this.this$0.this$0.userName);
                            this.this$0.this$0.userDaysLeft = jSONObjectOptJSONObject2.optString("days_left", this.this$0.this$0.userDaysLeft);
                            this.this$0.this$0.userAvatarUrl = jSONObjectOptJSONObject2.optString("avatar_url", this.this$0.this$0.userAvatarUrl);
                            this.this$0.this$0.userBannerUrl = jSONObjectOptJSONObject2.optString("banner_url", this.this$0.this$0.userBannerUrl);
                        }
                    } catch (Exception e) {
                    }
                    this.this$0.this$0.prefs.edit().putString("discord_id", this.this$0.this$0.userId).putString("user_name", this.this$0.this$0.userName).putString("user_expire", this.this$0.this$0.userExpireDate).putString("user_days", this.this$0.this$0.userDaysLeft).putString("avatar_url", this.this$0.this$0.userAvatarUrl).putString("banner_url", this.this$0.this$0.userBannerUrl).apply();
                    this.val$statusText.setText("STATUS: SUCESSO!");
                    this.val$statusText.setTextColor(Color.parseColor("#32D74B"));
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable(this) { // from class: com.s.MainActivity.100000004.100000002.100000001
                        private final AnonymousClass100000002 this$0;

                        {
                            this.this$0 = this;
                        }

                        @Override // java.lang.Runnable
                        public void run() {
                            this.this$0.this$0.this$0.startOverlayService();
                            MainActivity.access$1000032(this.this$0.this$0.this$0);
                        }
                    }, 600);
                    return;
                }
                this.val$input.setEnabled(true);
                this.val$btn.setVisibility(0);
                this.val$spinner.setVisibility(8);
                this.val$statusText.setTextColor(Color.parseColor("#FF453A"));
                this.val$statusText.setText(new StringBuffer().append(new StringBuffer().append("STATUS: INVÁLIDO (").append(this.val$reason).toString()).append(")").toString());
                MainActivity.access$1000056(this.this$0.this$0, this.val$input);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void iniciarFluxoDeLogin(String str, TextView textView, EditText editText, ProgressBar progressBar, View view) {
        editText.setEnabled(false);
        progressBar.setVisibility(0);
        textView.setText("STATUS: AUTENTICANDO...");
        textView.setTextColor(Color.parseColor("#9B3333"));
        new Thread(new AnonymousClass100000005(this, str, textView, editText, progressBar)).start();
    }

    /* JADX INFO: renamed from: com.s.MainActivity$100000006, reason: invalid class name */
    class AnonymousClass100000006 implements View.OnClickListener {
        private final MainActivity this$0;
        private final /* synthetic */ String val$packageName;

        AnonymousClass100000006(MainActivity mainActivity, String str) {
            this.this$0 = mainActivity;
            this.val$packageName = str;
        }

        /* JADX INFO: renamed from: com.s.MainActivity$100000006$100000004, reason: invalid class name */
        class AnonymousClass100000004 implements Runnable {
            private final AnonymousClass100000006 this$0;
            private final /* synthetic */ boolean val$authorized;
            private final /* synthetic */ TextView val$btn;
            private final /* synthetic */ View val$container;
            private final /* synthetic */ String val$discordId;
            private final /* synthetic */ EditText val$input;
            private final /* synthetic */ JSONObject val$jsonResponse;
            private final /* synthetic */ String val$reason;
            private final /* synthetic */ ProgressBar val$spinner;
            private final /* synthetic */ TextView val$statusText;

            AnonymousClass100000004(AnonymousClass100000006 anonymousClass100000006, boolean z, JSONObject jSONObject, String str, TextView textView, View view, EditText editText, TextView textView2, ProgressBar progressBar, String str2) {
                this.this$0 = anonymousClass100000006;
                this.val$authorized = z;
                this.val$jsonResponse = jSONObject;
                this.val$discordId = str;
                this.val$statusText = textView;
                this.val$container = view;
                this.val$input = editText;
                this.val$btn = textView2;
                this.val$spinner = progressBar;
                this.val$reason = str2;
            }

            @Override // java.lang.Runnable
            public void run() {
                JSONObject jSONObjectOptJSONObject;
                if (this.val$authorized) {
                    this.this$0.this$0.userName = this.val$jsonResponse.optString("username", "Dexter_Dev");
                    this.this$0.this$0.userExpireDate = this.val$jsonResponse.optString("expire_date", "12/31/2026");
                    this.this$0.this$0.userDaysLeft = this.val$jsonResponse.optString("days_left", "999");
                    this.this$0.this$0.userId = this.val$jsonResponse.optString("avatar_url", "https://i.pinimg.com/736x/8f/1a/0b/8f1a0b3552dcbf78fcad855737bc28f4.jpg");
                    this.this$0.this$0.userAvatarUrl = this.val$jsonResponse.optString("banner_url", "https://i.pinimg.com/736x/f6/88/cb/f688cbd931bb0436d40df351536b3b55.jpg");
                    try {
                        JSONObject jSONObjectOptJSONObject2 = this.val$jsonResponse.optJSONObject("users");
                        if (jSONObjectOptJSONObject2 == null) {
                            JSONObject jSONObjectOptJSONObject3 = this.val$jsonResponse.optJSONObject("profile");
                            if (jSONObjectOptJSONObject3 != null) {
                                this.this$0.this$0.userName = jSONObjectOptJSONObject3.optString("username", this.this$0.this$0.userName);
                                this.this$0.this$0.userExpireDate = jSONObjectOptJSONObject3.optString("expire_date", this.this$0.this$0.userExpireDate);
                                this.this$0.this$0.userId = jSONObjectOptJSONObject3.optString("avatar_url", this.this$0.this$0.userId);
                                this.this$0.this$0.userAvatarUrl = jSONObjectOptJSONObject3.optString("banner_url", this.this$0.this$0.userAvatarUrl);
                            }
                        } else {
                            JSONObject jSONObjectOptJSONObject4 = jSONObjectOptJSONObject2.optJSONObject(this.val$discordId);
                            if (jSONObjectOptJSONObject4 != null && (jSONObjectOptJSONObject = jSONObjectOptJSONObject4.optJSONObject("profile")) != null) {
                                this.this$0.this$0.userName = jSONObjectOptJSONObject.optString("username", this.this$0.this$0.userName);
                                this.this$0.this$0.userExpireDate = jSONObjectOptJSONObject.optString("expire_date", this.this$0.this$0.userExpireDate);
                                this.this$0.this$0.userId = jSONObjectOptJSONObject.optString("avatar_url", this.this$0.this$0.userId);
                                this.this$0.this$0.userAvatarUrl = jSONObjectOptJSONObject.optString("banner_url", this.this$0.this$0.userAvatarUrl);
                            }
                        }
                    } catch (Exception e) {
                    }
                    this.this$0.this$0.prefs.edit().putString("discord_id", this.val$discordId).putString("user_name", this.this$0.this$0.userName).putString("user_expire", this.this$0.this$0.userExpireDate).putString("user_days", this.this$0.this$0.userDaysLeft).putString("avatar_url", this.this$0.this$0.userId).putString("banner_url", this.this$0.this$0.userAvatarUrl).apply();
                    this.val$statusText.setText("STATUS: BAIXANDO PERFIL...");
                    this.val$statusText.setTextColor(Color.parseColor("#00AA00"));
                    new Handler(Looper.getMainLooper()).postDelayed(new AnonymousClass100000003(this, this.val$statusText, this.val$container), 1200);
                    return;
                }
                MainActivity.access$1000025(this.this$0.this$0, this.val$input, this.val$btn, this.val$spinner, this.val$statusText, this.val$reason);
            }

            /* JADX INFO: renamed from: com.s.MainActivity$100000006$100000004$100000003, reason: invalid class name */
            class AnonymousClass100000003 implements Runnable {
                private final AnonymousClass100000004 this$0;
                private final /* synthetic */ View val$container;
                private final /* synthetic */ TextView val$statusText;

                AnonymousClass100000003(AnonymousClass100000004 anonymousClass100000004, TextView textView, View view) {
                    this.this$0 = anonymousClass100000004;
                    this.val$statusText = textView;
                    this.val$container = view;
                }

                @Override // java.lang.Runnable
                public void run() {
                    this.val$statusText.setText("STATUS: SUCESSO!");
                    MainActivity.access$1000014(this.this$0.this$0.this$0);
                    AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                    long j = 400;
                    alphaAnimation.setDuration(j);
                    this.val$container.startAnimation(alphaAnimation);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable(this) { // from class: com.s.MainActivity.100000006.100000004.100000003.100000002
                        private final AnonymousClass100000003 this$0;

                        {
                            this.this$0 = this;
                        }

                        @Override // java.lang.Runnable
                        public void run() {
                            MainActivity.access$1000029(this.this$0.this$0.this$0.this$0);
                        }
                    }, j);
                }
            }
        }

        /* JADX INFO: renamed from: com.s.MainActivity$100000006$100000005, reason: invalid class name */
        class AnonymousClass100000005 implements Runnable {
            private final AnonymousClass100000006 this$0;
            private final /* synthetic */ TextView val$btn;
            private final /* synthetic */ EditText val$input;
            private final /* synthetic */ ProgressBar val$spinner;
            private final /* synthetic */ TextView val$statusText;

            AnonymousClass100000005(AnonymousClass100000006 anonymousClass100000006, EditText editText, TextView textView, ProgressBar progressBar, TextView textView2) {
                this.this$0 = anonymousClass100000006;
                this.val$input = editText;
                this.val$btn = textView;
                this.val$spinner = progressBar;
                this.val$statusText = textView2;
            }

            @Override // java.lang.Runnable
            public void run() {
                MainActivity.access$1000025(this.this$0.this$0, this.val$input, this.val$btn, this.val$spinner, this.val$statusText, "erro_conexao");
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (!this.this$0.exploitConfirmado) {
                this.this$0.mostrarDialogoConfirmacao(this.val$packageName);
            } else {
                this.this$0.mostrarCarregamentoEIniciar(this.val$packageName);
            }
        }
    }

    /* JADX INFO: renamed from: com.s.MainActivity$100000005, reason: invalid class name */
    class AnonymousClass100000005 implements Runnable {
        private final MainActivity this$0;
        private final /* synthetic */ String val$discordId;
        private final /* synthetic */ EditText val$input;
        private final /* synthetic */ ProgressBar val$spinner;
        private final /* synthetic */ TextView val$statusText;

        AnonymousClass100000005(MainActivity mainActivity, String str, TextView textView, EditText editText, ProgressBar progressBar) {
            this.this$0 = mainActivity;
            this.val$discordId = str;
            this.val$statusText = textView;
            this.val$input = editText;
            this.val$spinner = progressBar;
        }

        @Override // java.lang.Runnable
        public void run() throws Throwable {
            HttpURLConnection httpURLConnection;
            Throwable th;
            final Exception exc;
            InputStream errorStream;
            HttpURLConnection httpURLConnection2 = null;
            try {
                try {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("discord_id", this.val$discordId);
                    httpURLConnection = (HttpURLConnection) new URL("https://silent-clent.squareweb.app/api/verify").openConnection();
                    try {
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        httpURLConnection.setConnectTimeout(8000);
                        httpURLConnection.setReadTimeout(8000);
                        httpURLConnection.setDoOutput(true);
                        OutputStream outputStream = httpURLConnection.getOutputStream();
                        outputStream.write(jSONObject.toString().getBytes("UTF-8"));
                        outputStream.close();
                        int responseCode = httpURLConnection.getResponseCode();
                        if (responseCode >= 200 && responseCode <= 299) {
                            errorStream = httpURLConnection.getInputStream();
                        } else {
                            errorStream = httpURLConnection.getErrorStream();
                        }
                        if (errorStream == null) {
                            throw new Exception("Resposta vazia do servidor.");
                        }
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
                        StringBuilder sb = new StringBuilder();
                        while (true) {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            } else {
                                sb.append(line);
                            }
                        }
                        bufferedReader.close();
                        String string = sb.toString();
                        if (!string.startsWith("{")) {
                            throw new Exception("Resposta inválida do servidor.");
                        }
                        JSONObject jSONObject2 = new JSONObject(string);
                        this.this$0.runOnUiThread(new AnonymousClass100000003(this, jSONObject2.optBoolean("authorized", false), this.val$discordId, jSONObject2, this.val$statusText, this.val$input, this.val$spinner, jSONObject2.optString("reason", "Motivo desconhecido")));
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                    } catch (Exception e) {
                        exc = e;
                        httpURLConnection2 = httpURLConnection;
                        MainActivity mainActivity = this.this$0;
                        final EditText editText = this.val$input;
                        final ProgressBar progressBar = this.val$spinner;
                        final TextView textView = this.val$statusText;
                        mainActivity.runOnUiThread(new Runnable(this) { // from class: com.s.MainActivity.100000005.100000004
                            private final AnonymousClass100000005 this$0;

                            {
                                this.this$0 = this;
                            }

                            @Override // java.lang.Runnable
                            public void run() {
                                editText.setEnabled(true);
                                progressBar.setVisibility(8);
                                textView.setTextColor(Color.parseColor("#FF453A"));
                                textView.setText(new StringBuffer().append(new StringBuffer().append("STATUS: ERRO (").append(exc.getMessage()).toString()).append(")").toString());
                                this.this$0.this$0.animacaoShake(editText);
                            }
                        });
                        httpURLConnection = httpURLConnection2;
                    } catch (Throwable th2) {
                        th = th2;
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                        throw th;
                    }
                } catch (Exception e2) {
                    exc = e2;
                }
            } catch (Throwable th3) {
                httpURLConnection = httpURLConnection2;
                th = th3;
            }
        }

        /* JADX INFO: renamed from: com.s.MainActivity$100000005$100000003, reason: invalid class name */
        class AnonymousClass100000003 implements Runnable {
            private final AnonymousClass100000005 this$0;
            private final /* synthetic */ boolean val$authorized;
            private final /* synthetic */ String val$discordId;
            private final /* synthetic */ EditText val$input;
            private final /* synthetic */ JSONObject val$jsonResponse;
            private final /* synthetic */ String val$reason;
            private final /* synthetic */ ProgressBar val$spinner;
            private final /* synthetic */ TextView val$statusText;

            AnonymousClass100000003(AnonymousClass100000005 anonymousClass100000005, boolean z, String str, JSONObject jSONObject, TextView textView, EditText editText, ProgressBar progressBar, String str2) {
                this.this$0 = anonymousClass100000005;
                this.val$authorized = z;
                this.val$discordId = str;
                this.val$jsonResponse = jSONObject;
                this.val$statusText = textView;
                this.val$input = editText;
                this.val$spinner = progressBar;
                this.val$reason = str2;
            }

            @Override // java.lang.Runnable
            public void run() {
                JSONObject jSONObjectOptJSONObject;
                JSONObject jSONObjectOptJSONObject2;
                if (this.val$authorized) {
                    this.this$0.this$0.userId = this.val$discordId;
                    this.this$0.this$0.userName = this.val$jsonResponse.optString("username", "Dexter");
                    this.this$0.this$0.userExpireDate = this.val$jsonResponse.optString("expire_date", "12/31/2026");
                    this.this$0.this$0.userDaysLeft = this.val$jsonResponse.optString("days_left", "99");
                    this.this$0.this$0.userAvatarUrl = this.val$jsonResponse.optString("avatar_url", "https://i.pinimg.com/736x/8f/1a/0b/8f1a0b3552dcbf78fcad855737bc28f4.jpg");
                    this.this$0.this$0.userBannerUrl = this.val$jsonResponse.optString("banner_url", "https://i.pinimg.com/736x/f6/88/cb/f688cbd931bb0436d40df351536b3b55.jpg");
                    try {
                        JSONObject jSONObjectOptJSONObject3 = this.val$jsonResponse.optJSONObject("users");
                        if (jSONObjectOptJSONObject3 != null && (jSONObjectOptJSONObject = jSONObjectOptJSONObject3.optJSONObject(this.val$discordId)) != null && (jSONObjectOptJSONObject2 = jSONObjectOptJSONObject.optJSONObject("profile")) != null) {
                            this.this$0.this$0.userName = jSONObjectOptJSONObject2.optString("username", this.this$0.this$0.userName);
                            this.this$0.this$0.userDaysLeft = jSONObjectOptJSONObject2.optString("days_left", this.this$0.this$0.userDaysLeft);
                            this.this$0.this$0.userAvatarUrl = jSONObjectOptJSONObject2.optString("avatar_url", this.this$0.this$0.userAvatarUrl);
                            this.this$0.this$0.userBannerUrl = jSONObjectOptJSONObject2.optString("banner_url", this.this$0.this$0.userBannerUrl);
                        }
                    } catch (Exception e) {
                    }
                    this.this$0.this$0.prefs.edit().putString("discord_id", this.this$0.this$0.userId).putString("user_name", this.this$0.this$0.userName).putString("user_expire", this.this$0.this$0.userExpireDate).putString("user_days", this.this$0.this$0.userDaysLeft).putString("avatar_url", this.this$0.this$0.userAvatarUrl).putString("banner_url", this.this$0.this$0.userBannerUrl).apply();
                    this.val$statusText.setText("STATUS: SUCESSO!");
                    this.val$statusText.setTextColor(Color.parseColor("#32D74B"));
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable(this) { // from class: com.s.MainActivity.100000005.100000003.100000002
                        private final AnonymousClass100000003 this$0;

                        {
                            this.this$0 = this;
                        }

                        @Override // java.lang.Runnable
                        public void run() {
                            this.this$0.this$0.this$0.startOverlayService();
                            this.this$0.this$0.this$0.showMainPanel();
                        }
                    }, 600);
                    return;
                }
                this.val$input.setEnabled(true);
                this.val$spinner.setVisibility(8);
                this.val$statusText.setTextColor(Color.parseColor("#FF453A"));
                this.val$statusText.setText(new StringBuffer().append(new StringBuffer().append("STATUS: INVÁLIDO (").append(this.val$reason).toString()).append(")").toString());
                this.this$0.this$0.animacaoShake(this.val$input);
            }
        }
    }

    /* JADX INFO: renamed from: com.s.MainActivity$100000007, reason: invalid class name */
    class AnonymousClass100000007 implements View.OnClickListener {
        private final MainActivity this$0;
        private final /* synthetic */ Dialog val$dialog;

        AnonymousClass100000007(MainActivity mainActivity, Dialog dialog) {
            this.this$0 = mainActivity;
            this.val$dialog = dialog;
        }

        /* JADX INFO: renamed from: com.s.MainActivity$100000007$100000005, reason: invalid class name */
        class AnonymousClass100000005 implements Runnable {
            private final AnonymousClass100000007 this$0;
            private final /* synthetic */ boolean val$authorized;
            private final /* synthetic */ TextView val$btn;
            private final /* synthetic */ String val$discordId;
            private final /* synthetic */ EditText val$input;
            private final /* synthetic */ JSONObject val$jsonResponse;
            private final /* synthetic */ String val$reason;
            private final /* synthetic */ TextView val$statusText;

            AnonymousClass100000005(AnonymousClass100000007 anonymousClass100000007, EditText editText, TextView textView, boolean z, JSONObject jSONObject, String str, TextView textView2, String str2) {
                this.this$0 = anonymousClass100000007;
                this.val$input = editText;
                this.val$btn = textView;
                this.val$authorized = z;
                this.val$jsonResponse = jSONObject;
                this.val$discordId = str;
                this.val$statusText = textView2;
                this.val$reason = str2;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.val$input.setEnabled(true);
                this.val$btn.setEnabled(true);
                this.val$btn.setAlpha(1.0f);
                this.val$btn.setText("ENTRAR");
                if (this.val$authorized) {
                    this.this$0.this$0.userName = this.val$jsonResponse.optString("username", "Membro");
                    this.this$0.this$0.userExpireDate = this.val$jsonResponse.optString("expire_date", "");
                    this.this$0.this$0.userDaysLeft = this.val$jsonResponse.optString("days_left", "");
                    this.this$0.this$0.prefs.edit().putString("discord_id", this.val$discordId).putString("user_name", this.this$0.this$0.userName).putString("user_expire", this.this$0.this$0.userExpireDate).putString("user_days", this.this$0.this$0.userDaysLeft).apply();
                    this.val$statusText.setText("STATUS: SUCCESS!");
                    this.val$statusText.setTextColor(Color.parseColor("#43B581"));
                    MainActivity.access$1000011(this.this$0.this$0);
                    new Handler().postDelayed(new Runnable(this) { // from class: com.s.MainActivity.100000007.100000005.100000004
                        private final AnonymousClass100000005 this$0;

                        {
                            this.this$0 = this;
                        }

                        @Override // java.lang.Runnable
                        public void run() {
                            MainActivity.access$1000024(this.this$0.this$0.this$0);
                        }
                    }, 800);
                    return;
                }
                this.this$0.this$0.prefs.edit().remove("discord_id").apply();
                this.val$statusText.setTextColor(Color.parseColor("#F04747"));
                if ("banned".equals(this.val$reason)) {
                    this.val$statusText.setText("STATUS: BANIDO");
                } else if ("expired".equals(this.val$reason)) {
                    this.val$statusText.setText("STATUS: EXPIRADO");
                } else if ("user_not_found".equals(this.val$reason)) {
                    this.val$statusText.setText("STATUS: ID NÃO ENCONTRADO");
                } else {
                    this.val$statusText.setText("STATUS: INVÁLIDO");
                }
                MainActivity.access$1000025(this.this$0.this$0, this.val$input);
                MainActivity.access$1000025(this.this$0.this$0, this.val$btn);
            }
        }

        /* JADX INFO: renamed from: com.s.MainActivity$100000007$100000006, reason: invalid class name */
        class AnonymousClass100000006 implements Runnable {
            private final AnonymousClass100000007 this$0;
            private final /* synthetic */ TextView val$btn;
            private final /* synthetic */ EditText val$input;
            private final /* synthetic */ TextView val$statusText;

            AnonymousClass100000006(AnonymousClass100000007 anonymousClass100000007, EditText editText, TextView textView, TextView textView2) {
                this.this$0 = anonymousClass100000007;
                this.val$input = editText;
                this.val$btn = textView;
                this.val$statusText = textView2;
            }

            @Override // java.lang.Runnable
            public void run() {
                this.val$input.setEnabled(true);
                this.val$btn.setEnabled(true);
                this.val$btn.setAlpha(1.0f);
                this.val$btn.setText("ENTRAR");
                this.val$statusText.setText("STATUS: ERRO DE CONEXÃO");
                this.val$statusText.setTextColor(Color.parseColor("#F04747"));
                MainActivity.access$1000025(this.this$0.this$0, this.val$input);
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            this.val$dialog.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showMainPanel() {
        limparRoot();
        this.exploitConfirmado = false;
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setGravity(1);
        float f = 20;
        float f2 = 40;
        linearLayout.setPadding(m0dp(f), m0dp(f2), m0dp(f), m0dp(f2));
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(16);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        float f3 = 25;
        ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = m0dp(f3);
        linearLayout2.setLayoutParams(layoutParams);
        TextView textView = new TextView(this);
        textView.setText("SILENT HDTRICK");
        textView.setTextColor(Color.parseColor("#FFFFFF"));
        textView.setTextSize(2, 26);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        linearLayout2.addView(textView);
        TextView textView2 = new TextView(this);
        textView2.setText("❖");
        textView2.setTextColor(Color.parseColor("#0A84FF"));
        textView2.setTextSize(2, 18);
        linearLayout2.addView(textView2);
        linearLayout.addView(linearLayout2);
        LinearLayout linearLayout3 = new LinearLayout(this);
        linearLayout3.setOrientation(0);
        linearLayout3.setGravity(17);
        float f4 = 14;
        linearLayout3.setPadding(m0dp(f), m0dp(f4), m0dp(f), m0dp(f4));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#151515"));
        float f5 = 30;
        gradientDrawable.setCornerRadius(m0dp(f5));
        linearLayout3.setBackground(gradientDrawable);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
        ((ViewGroup.MarginLayoutParams) layoutParams2).bottomMargin = m0dp(f3);
        linearLayout3.setLayoutParams(layoutParams2);
        TextView textView3 = new TextView(this);
        textView3.setText(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append("Android ").append(Build.VERSION.RELEASE).toString()).append("  •  ").toString()).append(Build.MODEL).toString());
        textView3.setTextColor(Color.parseColor("#8E8E93"));
        textView3.setTextSize(2, 13);
        linearLayout3.addView(textView3);
        linearLayout.addView(linearLayout3);
        linearLayout.addView(createProfileCard());
        linearLayout.addView(createSectionLabel("⚡", "EXPLOIT"));
        linearLayout.addView(createExploitCard());
        linearLayout.addView(createSectionLabel("🎮", "GAMES"));
        linearLayout.addView(createGamesCard());
        View view = new View(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(-1, m0dp(f5)));
        linearLayout.addView(view);
        linearLayout.addView(createSectionLabel("👥", "SISTEMA"));
        linearLayout.addView(createLogoutCard());
        scrollView.addView(linearLayout);
        this.root.addView(scrollView);
    }

    private View createProfileCard() {
        FrameLayout frameLayout = new FrameLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, m0dp(110));
        ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = m0dp(35);
        frameLayout.setLayoutParams(layoutParams);
        frameLayout.setClipToOutline(true);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#151515"));
        float f = 16;
        gradientDrawable.setCornerRadius(m0dp(f));
        frameLayout.setBackground(gradientDrawable);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        carregarImagemDaUrl(this.userBannerUrl, imageView);
        frameLayout.addView(imageView);
        View view = new View(this);
        view.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        view.setBackgroundColor(Color.parseColor("#99000000"));
        frameLayout.addView(view);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        float f2 = 18;
        float f3 = 0;
        linearLayout.setPadding(m0dp(f2), m0dp(f3), m0dp(f2), m0dp(f3));
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        FrameLayout frameLayout2 = new FrameLayout(this);
        float f4 = 56;
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(m0dp(f4), m0dp(f4));
        ((ViewGroup.MarginLayoutParams) layoutParams2).rightMargin = m0dp(f);
        frameLayout2.setLayoutParams(layoutParams2);
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setShape(1);
        gradientDrawable2.setColor(Color.parseColor("#222222"));
        gradientDrawable2.setStroke(m0dp(2), Color.parseColor("#0A84FF"));
        frameLayout2.setBackground(gradientDrawable2);
        frameLayout2.setClipToOutline(true);
        ImageView imageView2 = new ImageView(this);
        imageView2.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        imageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        carregarImagemDaUrl(this.userAvatarUrl, imageView2);
        frameLayout2.addView(imageView2);
        linearLayout.addView(frameLayout2);
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(1);
        linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView textView = new TextView(this);
        textView.setText(new StringBuffer().append("ID: ").append(this.userId.isEmpty() ? "0000000000" : this.userId).toString());
        textView.setTextColor(Color.parseColor("#AAAAAA"));
        textView.setTextSize(2, 11);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        linearLayout2.addView(textView);
        TextView textView2 = new TextView(this);
        String str = this.userName;
        textView2.setText((str == null || str.trim().isEmpty()) ? "Dexter" : this.userName);
        textView2.setTextColor(-1);
        textView2.setTextSize(2, f2);
        textView2.setTypeface(Typeface.DEFAULT_BOLD);
        linearLayout2.addView(textView2);
        linearLayout.addView(linearLayout2);
        TextView textView3 = new TextView(this);
        textView3.setText(new StringBuffer().append(this.userDaysLeft).append("d").toString());
        textView3.setTextColor(Color.parseColor("#FFD60A"));
        float f5 = 12;
        textView3.setTextSize(2, f5);
        textView3.setTypeface(Typeface.DEFAULT_BOLD);
        float f6 = 5;
        textView3.setPadding(m0dp(f5), m0dp(f6), m0dp(f5), m0dp(f6));
        GradientDrawable gradientDrawable3 = new GradientDrawable();
        gradientDrawable3.setColor(Color.parseColor("#33FFFFFF"));
        gradientDrawable3.setCornerRadius(m0dp(8));
        gradientDrawable3.setStroke(m0dp(1), Color.parseColor("#4DFFD60A"));
        textView3.setBackground(gradientDrawable3);
        linearLayout.addView(textView3);
        frameLayout.addView(linearLayout);
        return frameLayout;
    }

    private View createSectionLabel(String str, String str2) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        float f = 12;
        ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = m0dp(f);
        ((ViewGroup.MarginLayoutParams) layoutParams).leftMargin = m0dp(5);
        linearLayout.setLayoutParams(layoutParams);
        TextView textView = new TextView(this);
        textView.setText("◆");
        textView.setTextColor(Color.parseColor("#8E8E93"));
        textView.setTextSize(2, 10);
        textView.setPadding(0, 0, m0dp(8), 0);
        linearLayout.addView(textView);
        TextView textView2 = new TextView(this);
        textView2.setText(str2);
        textView2.setTextColor(Color.parseColor("#8E8E93"));
        textView2.setTextSize(2, f);
        textView2.setTypeface(Typeface.DEFAULT_BOLD);
        linearLayout.addView(textView2);
        return linearLayout;
    }

    /* JADX INFO: renamed from: com.s.MainActivity$100000011, reason: invalid class name */
    class AnonymousClass100000011 implements ValueAnimator.AnimatorUpdateListener {
        private final MainActivity this$0;
        private final /* synthetic */ TextView[] val$dots;
        private final /* synthetic */ TextView[] val$mods;
        private final /* synthetic */ TextView val$progressText;

        AnonymousClass100000011(MainActivity mainActivity, TextView textView, TextView[] textViewArr, TextView[] textViewArr2) {
            this.this$0 = mainActivity;
            this.val$progressText = textView;
            this.val$dots = textViewArr;
            this.val$mods = textViewArr2;
        }

        /* JADX INFO: renamed from: com.s.MainActivity$100000011$100000010, reason: invalid class name */
        class AnonymousClass100000010 implements Runnable {
            private final AnonymousClass100000011 this$0;
            private final /* synthetic */ Bitmap val$bmp;
            private final /* synthetic */ ImageView val$imageView;

            AnonymousClass100000010(AnonymousClass100000011 anonymousClass100000011, Bitmap bitmap, ImageView imageView) {
                this.this$0 = anonymousClass100000011;
                this.val$bmp = bitmap;
                this.val$imageView = imageView;
            }

            @Override // java.lang.Runnable
            public void run() {
                Bitmap bitmap = this.val$bmp;
                if (bitmap != null) {
                    this.val$imageView.setImageBitmap(bitmap);
                }
            }
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            int iIntValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
            this.val$progressText.setText(new StringBuffer().append(new StringBuffer().append("Exploit ").append(iIntValue).toString()).append("%...").toString());
            if (iIntValue > 25) {
                this.val$dots[0].setTextColor(Color.parseColor("#FFD60A"));
                this.val$mods[0].setTextColor(Color.parseColor("#FFD60A"));
            }
            if (iIntValue > 50) {
                this.val$dots[1].setTextColor(Color.parseColor("#FFD60A"));
                this.val$mods[1].setTextColor(Color.parseColor("#FFD60A"));
            }
            if (iIntValue > 75) {
                this.val$dots[2].setTextColor(Color.parseColor("#FFD60A"));
                this.val$mods[2].setTextColor(Color.parseColor("#FFD60A"));
            }
            if (iIntValue == 100) {
                this.val$dots[3].setTextColor(Color.parseColor("#FFD60A"));
                this.val$mods[3].setTextColor(Color.parseColor("#FFD60A"));
            }
        }
    }

    private View createGamesCard() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#151515"));
        gradientDrawable.setCornerRadius(m0dp(16));
        linearLayout.setBackground(gradientDrawable);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        linearLayout.addView(createGameItem("FreeFire Normal", "Injetar Bypass & ESP", "com.dts.freefireth", true));
        View view = new View(this);
        view.setBackgroundColor(Color.parseColor("#2C2C2E"));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, m0dp(1));
        ((ViewGroup.MarginLayoutParams) layoutParams).leftMargin = m0dp(70);
        view.setLayoutParams(layoutParams);
        linearLayout.addView(view);
        linearLayout.addView(createGameItem("FreeFire MAX", "Injetar Bypass & ESP", "com.dts.freefiremax", false));
        return linearLayout;
    }

    private View createGameItem(String str, String str2, String str3, boolean z) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        float f = 15;
        linearLayout.setPadding(m0dp(f), m0dp(z ? 18 : 14), m0dp(f), m0dp(z ? 14 : 18));
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        TextView textView = new TextView(this);
        textView.setText("FF");
        textView.setTextColor(Color.parseColor("#BBBBBB"));
        float f2 = 12;
        textView.setTextSize(2, f2);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setGravity(17);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#1F1F1F"));
        gradientDrawable.setCornerRadius(m0dp(f2));
        gradientDrawable.setStroke(m0dp(1), Color.parseColor("#2A2A2A"));
        textView.setBackground(gradientDrawable);
        float f3 = 45;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(m0dp(f3), m0dp(f3));
        ((ViewGroup.MarginLayoutParams) layoutParams).rightMargin = m0dp(f);
        textView.setLayoutParams(layoutParams);
        linearLayout.addView(textView);
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(1);
        linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView textView2 = new TextView(this);
        textView2.setText(str);
        textView2.setTextColor(Color.parseColor("#FFFFFF"));
        textView2.setTextSize(2, 16);
        textView2.setTypeface(Typeface.DEFAULT_BOLD);
        linearLayout2.addView(textView2);
        TextView textView3 = new TextView(this);
        textView3.setText(str2);
        textView3.setTextColor(Color.parseColor("#8E8E93"));
        textView3.setTextSize(2, f2);
        textView3.setPadding(0, m0dp(2), 0, 0);
        linearLayout2.addView(textView3);
        linearLayout.addView(linearLayout2);
        TextView textView4 = new TextView(this);
        textView4.setText("›");
        textView4.setTextColor(Color.parseColor("#666666"));
        textView4.setTextSize(2, 22);
        textView4.setPadding(0, 0, m0dp(5), m0dp(4));
        linearLayout.addView(textView4);
        linearLayout.setOnClickListener(new AnonymousClass100000006(this, str3));
        return linearLayout;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void mostrarDialogoConfirmacao(final String str) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(false);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        float f = 25;
        float f2 = 20;
        linearLayout.setPadding(m0dp(f), m0dp(f), m0dp(f), m0dp(f2));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#1C1C1E"));
        gradientDrawable.setCornerRadius(m0dp(f2));
        gradientDrawable.setStroke(m0dp(1), Color.parseColor("#333333"));
        linearLayout.setBackground(gradientDrawable);
        TextView textView = new TextView(this);
        textView.setText("▲");
        textView.setTextColor(Color.parseColor("#FFD60A"));
        textView.setTextSize(2, 24);
        textView.setGravity(17);
        float f3 = 10;
        textView.setPadding(0, 0, 0, m0dp(f3));
        linearLayout.addView(textView);
        TextView textView2 = new TextView(this);
        textView2.setText("Confirmação de Exploit");
        textView2.setTextColor(-1);
        textView2.setTextSize(2, 18);
        textView2.setTypeface(Typeface.DEFAULT_BOLD);
        textView2.setGravity(17);
        linearLayout.addView(textView2);
        TextView textView3 = new TextView(this);
        textView3.setText("Para injetar as modificações, o aplicativo utilizará exploração no nível do Kernel do Android. Deseja confirmar a execução?");
        textView3.setTextColor(Color.parseColor("#AAAAAA"));
        textView3.setTextSize(2, 14);
        textView3.setGravity(17);
        textView3.setPadding(0, m0dp(15), 0, m0dp(f));
        linearLayout.addView(textView3);
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(0);
        linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        TextView textView4 = new TextView(this);
        textView4.setText("CANCELAR");
        textView4.setTextColor(-1);
        float f4 = 12;
        textView4.setTextSize(2, f4);
        textView4.setTypeface(Typeface.DEFAULT_BOLD);
        textView4.setGravity(17);
        textView4.setPadding(0, m0dp(f4), 0, m0dp(f4));
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setColor(Color.parseColor("#2C2C2E"));
        gradientDrawable2.setCornerRadius(m0dp(f3));
        textView4.setBackground(gradientDrawable2);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        ((ViewGroup.MarginLayoutParams) layoutParams).rightMargin = m0dp(f3);
        textView4.setLayoutParams(layoutParams);
        TextView textView5 = new TextView(this);
        textView5.setText("CONFIRMAR");
        textView5.setTextColor(-16777216);
        textView5.setTextSize(2, f4);
        textView5.setTypeface(Typeface.DEFAULT_BOLD);
        textView5.setGravity(17);
        textView5.setPadding(0, m0dp(f4), 0, m0dp(f4));
        GradientDrawable gradientDrawable3 = new GradientDrawable();
        gradientDrawable3.setColor(Color.parseColor("#FFD60A"));
        gradientDrawable3.setCornerRadius(m0dp(f3));
        textView5.setBackground(gradientDrawable3);
        textView5.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        linearLayout2.addView(textView4);
        linearLayout2.addView(textView5);
        linearLayout.addView(linearLayout2);
        textView4.setOnClickListener(new AnonymousClass100000007(this, dialog));
        textView5.setOnClickListener(new View.OnClickListener(this) { // from class: com.s.MainActivity.100000008
            private final MainActivity this$0;

            {
                this.this$0 = this;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                this.this$0.exploitConfirmado = true;
                dialog.dismiss();
                this.this$0.mostrarCarregamentoEIniciar(str);
            }
        });
        dialog.setContentView(linearLayout);
        dialog.getWindow().setLayout(m0dp(300), -2);
        dialog.show();
    }

    /* JADX INFO: renamed from: com.s.MainActivity$100000012, reason: invalid class name */
    class AnonymousClass100000012 implements Runnable {
        private final MainActivity this$0;
        private final /* synthetic */ Handler val$handler;
        private final /* synthetic */ FrameLayout val$loadingOverlay;
        private final /* synthetic */ String val$packageName;
        private final /* synthetic */ TextView val$statusText;

        AnonymousClass100000012(MainActivity mainActivity, TextView textView, Handler handler, String str, FrameLayout frameLayout) {
            this.this$0 = mainActivity;
            this.val$statusText = textView;
            this.val$handler = handler;
            this.val$packageName = str;
            this.val$loadingOverlay = frameLayout;
        }

        /* JADX INFO: renamed from: com.s.MainActivity$100000012$100000010, reason: invalid class name */
        class AnonymousClass100000010 implements ValueAnimator.AnimatorUpdateListener {
            private final AnonymousClass100000012 this$0;
            private final /* synthetic */ String val$label;
            private final /* synthetic */ View val$progressOverlay;
            private final /* synthetic */ TextView val$text;
            private final /* synthetic */ View val$v;

            AnonymousClass100000010(AnonymousClass100000012 anonymousClass100000012, View view, View view2, TextView textView, String str) {
                this.this$0 = anonymousClass100000012;
                this.val$v = view;
                this.val$progressOverlay = view2;
                this.val$text = textView;
                this.val$label = str;
            }

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int iIntValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((this.val$v.getWidth() * iIntValue) / 100, -1);
                layoutParams.addRule(9);
                this.val$progressOverlay.setLayoutParams(layoutParams);
                this.val$text.setText(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(this.val$label.toUpperCase()).append(" (").toString()).append(iIntValue).toString()).append("%)").toString());
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            this.val$statusText.setText("Iniciando Jogo!");
            this.val$statusText.setTextColor(Color.parseColor("#32D74B"));
            this.val$handler.postDelayed(new AnonymousClass100000011(this, this.val$packageName, this.val$loadingOverlay), 800);
        }

        /* JADX INFO: renamed from: com.s.MainActivity$100000012$100000011, reason: invalid class name */
        class AnonymousClass100000011 implements Runnable {
            private final AnonymousClass100000012 this$0;
            private final /* synthetic */ FrameLayout val$loadingOverlay;
            private final /* synthetic */ String val$packageName;

            AnonymousClass100000011(AnonymousClass100000012 anonymousClass100000012, String str, FrameLayout frameLayout) {
                this.this$0 = anonymousClass100000012;
                this.val$packageName = str;
                this.val$loadingOverlay = frameLayout;
            }

            @Override // java.lang.Runnable
            public void run() {
                Intent launchIntentForPackage = this.this$0.this$0.getPackageManager().getLaunchIntentForPackage(this.val$packageName);
                if (launchIntentForPackage != null) {
                    this.this$0.this$0.startActivity(launchIntentForPackage);
                } else {
                    Toast.makeText(this.this$0.this$0, "Jogo não instalado no aparelho.", 1).show();
                }
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                alphaAnimation.setDuration(300);
                final FrameLayout frameLayout = this.val$loadingOverlay;
                alphaAnimation.setAnimationListener(new Animation.AnimationListener(this) { // from class: com.s.MainActivity.100000012.100000011.100000010
                    private final AnonymousClass100000011 this$0;

                    {
                        this.this$0 = this;
                    }

                    @Override // android.view.animation.Animation.AnimationListener
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override // android.view.animation.Animation.AnimationListener
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override // android.view.animation.Animation.AnimationListener
                    public void onAnimationEnd(Animation animation) {
                        this.this$0.this$0.this$0.root.removeView(frameLayout);
                    }
                });
                this.val$loadingOverlay.startAnimation(alphaAnimation);
            }
        }
    }

    /* JADX INFO: renamed from: com.s.MainActivity$100000010, reason: invalid class name */
    class AnonymousClass100000010 implements Runnable {
        private final MainActivity this$0;
        private final /* synthetic */ TextView val$statusText;

        AnonymousClass100000010(MainActivity mainActivity, TextView textView) {
            this.this$0 = mainActivity;
            this.val$statusText = textView;
        }

        /* JADX INFO: renamed from: com.s.MainActivity$100000010$100000009, reason: invalid class name */
        class AnonymousClass100000009 implements Runnable {
            private final AnonymousClass100000010 this$0;
            private final /* synthetic */ FrameLayout val$loadingOverlay;
            private final /* synthetic */ String val$packageName;

            AnonymousClass100000009(AnonymousClass100000010 anonymousClass100000010, String str, FrameLayout frameLayout) {
                this.this$0 = anonymousClass100000010;
                this.val$packageName = str;
                this.val$loadingOverlay = frameLayout;
            }

            @Override // java.lang.Runnable
            public void run() {
                Intent launchIntentForPackage = this.this$0.this$0.getPackageManager().getLaunchIntentForPackage(this.val$packageName);
                if (launchIntentForPackage != null) {
                    this.this$0.this$0.startActivity(launchIntentForPackage);
                } else {
                    Toast.makeText(this.this$0.this$0, "Jogo não instalado no aparelho.", 1).show();
                }
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                alphaAnimation.setDuration(300);
                final FrameLayout frameLayout = this.val$loadingOverlay;
                alphaAnimation.setAnimationListener(new Animation.AnimationListener(this) { // from class: com.s.MainActivity.100000010.100000009.100000008
                    private final AnonymousClass100000009 this$0;

                    {
                        this.this$0 = this;
                    }

                    @Override // android.view.animation.Animation.AnimationListener
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override // android.view.animation.Animation.AnimationListener
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override // android.view.animation.Animation.AnimationListener
                    public void onAnimationEnd(Animation animation) {
                        this.this$0.this$0.this$0.root.removeView(frameLayout);
                    }
                });
                this.val$loadingOverlay.startAnimation(alphaAnimation);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            this.val$statusText.setText("Modificando Memória...");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void mostrarCarregamentoEIniciar(String str) {
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        frameLayout.setBackgroundColor(Color.parseColor("#E6000000"));
        frameLayout.setClickable(true);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setGravity(17);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 17;
        linearLayout.setLayoutParams(layoutParams);
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FFD60A"), PorterDuff.Mode.SRC_IN);
        linearLayout.addView(progressBar);
        final TextView textView = new TextView(this);
        textView.setText("Preparando Injeção...");
        textView.setTextColor(-1);
        textView.setTextSize(2, 16);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setPadding(0, m0dp(20), 0, 0);
        linearLayout.addView(textView);
        frameLayout.addView(linearLayout);
        this.root.addView(frameLayout);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(400);
        frameLayout.startAnimation(alphaAnimation);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable(this) { // from class: com.s.MainActivity.100000009
            private final MainActivity this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                textView.setText("Bypass no Kernel ativo...");
            }
        }, 1200);
        handler.postDelayed(new AnonymousClass100000010(this, textView), 2500);
        handler.postDelayed(new AnonymousClass100000013(this, textView, handler, str, frameLayout), 3800);
    }

    /* JADX INFO: renamed from: com.s.MainActivity$100000013, reason: invalid class name */
    class AnonymousClass100000013 implements Runnable {
        private final MainActivity this$0;
        private final /* synthetic */ Handler val$handler;
        private final /* synthetic */ FrameLayout val$loadingOverlay;
        private final /* synthetic */ String val$packageName;
        private final /* synthetic */ TextView val$statusText;

        AnonymousClass100000013(MainActivity mainActivity, TextView textView, Handler handler, String str, FrameLayout frameLayout) {
            this.this$0 = mainActivity;
            this.val$statusText = textView;
            this.val$handler = handler;
            this.val$packageName = str;
            this.val$loadingOverlay = frameLayout;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.val$statusText.setText("Iniciando Jogo!");
            this.val$statusText.setTextColor(Color.parseColor("#32D74B"));
            this.val$handler.postDelayed(new AnonymousClass100000012(this, this.val$packageName, this.val$loadingOverlay), 800);
        }

        /* JADX INFO: renamed from: com.s.MainActivity$100000013$100000012, reason: invalid class name */
        class AnonymousClass100000012 implements Runnable {
            private final AnonymousClass100000013 this$0;
            private final /* synthetic */ FrameLayout val$loadingOverlay;
            private final /* synthetic */ String val$packageName;

            AnonymousClass100000012(AnonymousClass100000013 anonymousClass100000013, String str, FrameLayout frameLayout) {
                this.this$0 = anonymousClass100000013;
                this.val$packageName = str;
                this.val$loadingOverlay = frameLayout;
            }

            @Override // java.lang.Runnable
            public void run() {
                Intent launchIntentForPackage = this.this$0.this$0.getPackageManager().getLaunchIntentForPackage(this.val$packageName);
                if (launchIntentForPackage != null) {
                    this.this$0.this$0.startActivity(launchIntentForPackage);
                } else {
                    Toast.makeText(this.this$0.this$0, "Jogo não instalado no aparelho.", 1).show();
                }
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                alphaAnimation.setDuration(300);
                final FrameLayout frameLayout = this.val$loadingOverlay;
                alphaAnimation.setAnimationListener(new Animation.AnimationListener(this) { // from class: com.s.MainActivity.100000013.100000012.100000011
                    private final AnonymousClass100000012 this$0;

                    {
                        this.this$0 = this;
                    }

                    @Override // android.view.animation.Animation.AnimationListener
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override // android.view.animation.Animation.AnimationListener
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override // android.view.animation.Animation.AnimationListener
                    public void onAnimationEnd(Animation animation) {
                        this.this$0.this$0.this$0.root.removeView(frameLayout);
                    }
                });
                this.val$loadingOverlay.startAnimation(alphaAnimation);
            }
        }
    }

    private View createExploitCard() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#151515"));
        gradientDrawable.setCornerRadius(m0dp(16));
        linearLayout.setBackground(gradientDrawable);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = m0dp(35);
        linearLayout.setLayoutParams(layoutParams);
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(16);
        float f = 20;
        linearLayout2.setPadding(m0dp(f), m0dp(f), m0dp(f), m0dp(f));
        ImageView imageView = new ImageView(this);
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setShape(1);
        gradientDrawable2.setColor(0);
        float f2 = 2;
        float f3 = 8;
        gradientDrawable2.setStroke(m0dp(f2), Color.parseColor("#FFD60A"), m0dp(f3), m0dp(6));
        imageView.setImageDrawable(gradientDrawable2);
        float f4 = 25;
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(m0dp(f4), m0dp(f4));
        float f5 = 15;
        ((ViewGroup.MarginLayoutParams) layoutParams2).rightMargin = m0dp(f5);
        imageView.setLayoutParams(layoutParams2);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, 1, 0.5f, 1, 0.5f);
        rotateAnimation.setDuration(3000);
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        imageView.startAnimation(rotateAnimation);
        linearLayout2.addView(imageView);
        LinearLayout linearLayout3 = new LinearLayout(this);
        linearLayout3.setOrientation(1);
        linearLayout3.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        final TextView textView = new TextView(this);
        textView.setText("Exploit 0%...");
        textView.setTextColor(Color.parseColor("#FFFFFF"));
        textView.setTextSize(2, f5);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        linearLayout3.addView(textView);
        LinearLayout linearLayout4 = new LinearLayout(this);
        linearLayout4.setOrientation(0);
        float f6 = 4;
        linearLayout4.setPadding(0, m0dp(f6), 0, 0);
        final TextView[] textViewArr = new TextView[4];
        final TextView[] textViewArr2 = new TextView[4];
        float f7 = f6;
        float f8 = f2;
        String[] strArr = {"KRW", "SBX", "VFS", "RC"};
        int i = 0;
        while (i < 4) {
            TextView textView2 = new TextView(this);
            textViewArr[i] = textView2;
            LinearLayout linearLayout5 = linearLayout;
            textView2.setText("●");
            textViewArr[i].setTextColor(Color.parseColor("#8E8E93"));
            textViewArr[i].setTextSize(2, f3);
            float f9 = f8;
            LinearLayout linearLayout6 = linearLayout3;
            float f10 = f7;
            textViewArr[i].setPadding(0, m0dp(f9), m0dp(f10), 0);
            TextView textView3 = new TextView(this);
            textViewArr2[i] = textView3;
            textView3.setText(strArr[i]);
            textViewArr2[i].setTextColor(Color.parseColor("#8E8E93"));
            textViewArr2[i].setTextSize(2, 11);
            textViewArr2[i].setTypeface(Typeface.DEFAULT_BOLD);
            textViewArr2[i].setPadding(0, 0, m0dp(10), 0);
            linearLayout4.addView(textViewArr[i]);
            linearLayout4.addView(textViewArr2[i]);
            i++;
            f = f;
            linearLayout2 = linearLayout2;
            linearLayout = linearLayout5;
            linearLayout3 = linearLayout6;
            f7 = f10;
            f8 = f9;
        }
        linearLayout3.addView(linearLayout4);
        linearLayout2.addView(linearLayout3);
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#8E8E93"), PorterDuff.Mode.SRC_IN);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(m0dp(f), m0dp(f)));
        linearLayout2.addView(progressBar);
        linearLayout.addView(linearLayout2);
        ValueAnimator valueAnimatorOfInt = ValueAnimator.ofInt(0, 100);
        valueAnimatorOfInt.setDuration(4500);
        valueAnimatorOfInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(this) { // from class: com.s.MainActivity.100000014
            private final MainActivity this$0;

            {
                this.this$0 = this;
            }

            /* JADX INFO: renamed from: com.s.MainActivity$100000014$100000013, reason: invalid class name */
            class AnonymousClass100000013 implements Runnable {
                private final AnonymousClass100000014 this$0;
                private final /* synthetic */ Bitmap val$bmp;
                private final /* synthetic */ ImageView val$imageView;

                AnonymousClass100000013(AnonymousClass100000014 anonymousClass100000014, Bitmap bitmap, ImageView imageView) {
                    this.this$0 = anonymousClass100000014;
                    this.val$bmp = bitmap;
                    this.val$imageView = imageView;
                }

                @Override // java.lang.Runnable
                public void run() {
                    Bitmap bitmap = this.val$bmp;
                    if (bitmap != null) {
                        this.val$imageView.setImageBitmap(bitmap);
                    }
                }
            }

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int iIntValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                textView.setText(new StringBuffer().append(new StringBuffer().append("Exploit ").append(iIntValue).toString()).append("%...").toString());
                if (iIntValue > 25) {
                    textViewArr[0].setTextColor(Color.parseColor("#FFD60A"));
                    textViewArr2[0].setTextColor(Color.parseColor("#FFD60A"));
                }
                if (iIntValue > 50) {
                    textViewArr[1].setTextColor(Color.parseColor("#FFD60A"));
                    textViewArr2[1].setTextColor(Color.parseColor("#FFD60A"));
                }
                if (iIntValue > 75) {
                    textViewArr[2].setTextColor(Color.parseColor("#FFD60A"));
                    textViewArr2[2].setTextColor(Color.parseColor("#FFD60A"));
                }
                if (iIntValue == 100) {
                    textViewArr[3].setTextColor(Color.parseColor("#FFD60A"));
                    textViewArr2[3].setTextColor(Color.parseColor("#FFD60A"));
                }
            }
        });
        valueAnimatorOfInt.start();
        View view = new View(this);
        view.setBackgroundColor(Color.parseColor("#2C2C2E"));
        view.setLayoutParams(new LinearLayout.LayoutParams(-1, m0dp(1)));
        linearLayout.addView(view);
        LinearLayout linearLayout7 = new LinearLayout(this);
        linearLayout7.setOrientation(0);
        linearLayout7.setGravity(16);
        float f11 = 18;
        linearLayout7.setPadding(m0dp(f), m0dp(f11), m0dp(f), m0dp(f11));
        TextView textView4 = new TextView(this);
        textView4.setText("License Key");
        textView4.setTextColor(Color.parseColor("#FFFFFF"));
        textView4.setTextSize(2, f5);
        textView4.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        linearLayout7.addView(textView4);
        TextView textView5 = new TextView(this);
        textView5.setText(new StringBuffer().append(new StringBuffer().append("Faltam ").append(this.userDaysLeft).toString()).append(" dias").toString());
        textView5.setTextColor(Color.parseColor("#32D74B"));
        textView5.setTextSize(2, 14);
        textView5.setTypeface(Typeface.DEFAULT_BOLD);
        linearLayout7.addView(textView5);
        linearLayout.addView(linearLayout7);
        return linearLayout;
    }

    private View createLogoutCard() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        float f = 15;
        float f2 = 18;
        linearLayout.setPadding(m0dp(f), m0dp(f2), m0dp(f), m0dp(f2));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#151515"));
        float f3 = 16;
        gradientDrawable.setCornerRadius(m0dp(f3));
        linearLayout.setBackground(gradientDrawable);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        TextView textView = new TextView(this);
        textView.setText("⏻");
        textView.setTextColor(-1);
        textView.setTextSize(2, f3);
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setColor(Color.parseColor("#0A84FF"));
        float f4 = 12;
        gradientDrawable2.setCornerRadius(m0dp(f4));
        textView.setBackground(gradientDrawable2);
        textView.setGravity(17);
        float f5 = 45;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(m0dp(f5), m0dp(f5));
        ((ViewGroup.MarginLayoutParams) layoutParams).rightMargin = m0dp(f);
        textView.setLayoutParams(layoutParams);
        linearLayout.addView(textView);
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(1);
        linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView textView2 = new TextView(this);
        textView2.setText("Remover Licença");
        textView2.setTextColor(Color.parseColor("#FFFFFF"));
        textView2.setTextSize(2, f3);
        textView2.setTypeface(Typeface.DEFAULT_BOLD);
        linearLayout2.addView(textView2);
        TextView textView3 = new TextView(this);
        textView3.setText("Desconectar do aparelho atual");
        textView3.setTextColor(Color.parseColor("#8E8E93"));
        textView3.setTextSize(2, f4);
        textView3.setPadding(0, m0dp(2), 0, 0);
        linearLayout2.addView(textView3);
        linearLayout.addView(linearLayout2);
        TextView textView4 = new TextView(this);
        textView4.setText("↗");
        textView4.setTextColor(Color.parseColor("#666666"));
        textView4.setTextSize(2, f2);
        linearLayout.addView(textView4);
        linearLayout.setOnClickListener(new View.OnClickListener(this) { // from class: com.s.MainActivity.100000015
            private final MainActivity this$0;

            {
                this.this$0 = this;
            }

            /* JADX INFO: renamed from: com.s.MainActivity$100000015$100000014, reason: invalid class name */
            class AnonymousClass100000014 implements Runnable {
                private final AnonymousClass100000015 this$0;
                private final /* synthetic */ Bitmap val$bitmap;
                private final /* synthetic */ ImageView val$imageView;

                AnonymousClass100000014(AnonymousClass100000015 anonymousClass100000015, Bitmap bitmap, ImageView imageView) {
                    this.this$0 = anonymousClass100000015;
                    this.val$bitmap = bitmap;
                    this.val$imageView = imageView;
                }

                @Override // java.lang.Runnable
                public void run() {
                    Bitmap bitmap = this.val$bitmap;
                    if (bitmap != null) {
                        this.val$imageView.setImageBitmap(bitmap);
                    }
                }
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                    this.this$0.stopService(new Intent(this.this$0, Class.forName("com.s.OverlayService")));
                    this.this$0.prefs.edit().clear().apply();
                    this.this$0.userName = "";
                    this.this$0.userId = "";
                    this.this$0.showLoginScreen("", false);
                } catch (ClassNotFoundException e) {
                    throw new NoClassDefFoundError(e.getMessage());
                }
            }
        });
        return linearLayout;
    }

    /* JADX INFO: renamed from: com.s.MainActivity$100000016, reason: invalid class name */
    class AnonymousClass100000016 implements Runnable {
        private final MainActivity this$0;
        private final /* synthetic */ ImageView val$imageView;
        private final /* synthetic */ String val$urlString;

        AnonymousClass100000016(MainActivity mainActivity, String str, ImageView imageView) {
            this.this$0 = mainActivity;
            this.val$urlString = str;
            this.val$imageView = imageView;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                final Bitmap bitmapDecodeStream = BitmapFactory.decodeStream(new URL(this.val$urlString).openStream());
                if (bitmapDecodeStream != null) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    final ImageView imageView = this.val$imageView;
                    handler.post(new Runnable(this) { // from class: com.s.MainActivity.100000016.100000015
                        private final AnonymousClass100000016 this$0;

                        {
                            this.this$0 = this;
                        }

                        @Override // java.lang.Runnable
                        public void run() {
                            imageView.setImageBitmap(bitmapDecodeStream);
                        }
                    });
                }
            } catch (Exception e) {
            }
        }
    }

    private void carregarImagemDaUrl(final String str, final ImageView imageView) {
        new Thread(new Runnable(this) { // from class: com.s.MainActivity.100000017
            private final MainActivity this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                try {
                    final Bitmap bitmapDecodeStream = BitmapFactory.decodeStream(new URL(str).openStream());
                    if (bitmapDecodeStream != null) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        final ImageView imageView2 = imageView;
                        handler.post(new Runnable(this) { // from class: com.s.MainActivity.100000017.100000016
                            private final AnonymousClass100000017 this$0;

                            {
                                this.this$0 = this;
                            }

                            @Override // java.lang.Runnable
                            public void run() {
                                imageView2.setImageBitmap(bitmapDecodeStream);
                            }
                        });
                    }
                } catch (Exception e) {
                }
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animacaoShake(View view) {
        float f = 0;
        TranslateAnimation translateAnimation = new TranslateAnimation(f, 15, f, f);
        translateAnimation.setDuration(50);
        translateAnimation.setRepeatCount(3);
        view.startAnimation(translateAnimation);
    }

    /* JADX INFO: renamed from: dp */
    private int m0dp(float f) {
        return Math.round(TypedValue.applyDimension(1, f, getResources().getDisplayMetrics()));
    }

    private void trustAllCertificates() {
        try {
            TrustManager[] trustManagerArr = {new X509TrustManager(this) { // from class: com.s.MainActivity.100000018
                private final MainActivity this$0;

                {
                    this.this$0 = this;
                }

                @Override // javax.net.ssl.X509TrustManager
                public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) {
                }

                @Override // javax.net.ssl.X509TrustManager
                public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) {
                }

                @Override // javax.net.ssl.X509TrustManager
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
            SSLContext sSLContext = SSLContext.getInstance("TLS");
            sSLContext.init(null, trustManagerArr, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sSLContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(this) { // from class: com.s.MainActivity.100000019
                private final MainActivity this$0;

                {
                    this.this$0 = this;
                }

                @Override // javax.net.ssl.HostnameVerifier
                public boolean verify(String str, SSLSession sSLSession) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
