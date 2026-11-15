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
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;

public class MainActivity extends Activity {
    private static final String API_VALIDATE_URL = "https://original-key-control.vercel.app/api/public/validate";
    private static final String APPLICATION_ID   = "5e321ec1-345e-49d1-9241-667534cb30c7";
    private static final String APP_CREDENTIAL   = "app_4aa1c75a5ff9de3ebc70a117f74038f5d79d488d";

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
        
        this.userId = this.prefs.getString("key_user_id", "");
        this.userName = this.prefs.getString("user_name", "");
        this.userExpireDate = this.prefs.getString("user_expire", "");
        this.userDaysLeft = this.prefs.getString("user_days", "");
        this.userAvatarUrl = this.prefs.getString("avatar_url", DEFAULT_AVATAR);
        this.userBannerUrl = this.prefs.getString("banner_url", DEFAULT_BANNER);
        
        String savedKey = this.prefs.getString("user_key", "");
        if (!savedKey.isEmpty()) {
            validateCachedKey(savedKey);
        } else {
            showLoginScreen("");
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
    public void showLoginScreen(String str) {
        limparRoot();
        final FrameLayout frameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dp(320), dp(300));
        layoutParams.gravity = 17;
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#181818"));
        gradientDrawable.setCornerRadius(dp(4));
        frameLayout.setBackground(gradientDrawable);
        frameLayout.setLayoutParams(layoutParams);
        
        TextView closeBtn = new TextView(this);
        closeBtn.setText("✕");
        closeBtn.setTextColor(Color.parseColor("#555555"));
        closeBtn.setTextSize(2, 14);
        FrameLayout.LayoutParams closeLp = new FrameLayout.LayoutParams(-2, -2);
        closeLp.gravity = 53;
        closeLp.topMargin = dp(15);
        closeLp.rightMargin = dp(15);
        closeBtn.setLayoutParams(closeLp);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
        frameLayout.addView(closeBtn);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setGravity(17);
        linearLayout.setPadding(dp(30), 0, dp(30), 0);
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        
        TextView title = new TextView(this);
        title.setText("PRECISION");
        title.setTextColor(Color.parseColor("#9B3333"));
        title.setTextSize(2, 42);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, 3));
        title.setTextScaleX(1.1f);
        title.setGravity(17);
        linearLayout.addView(title);
        
        TextView subtitle = new TextView(this);
        subtitle.setText("tired of missing? your problems are over!");
        subtitle.setTextColor(Color.parseColor("#888888"));
        subtitle.setTextSize(2, 12);
        subtitle.setGravity(17);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(-1, -2);
        subLp.topMargin = dp(35);
        subLp.bottomMargin = dp(15);
        subtitle.setLayoutParams(subLp);
        linearLayout.addView(subtitle);

        final EditText editText = new EditText(this);
        editText.setHint("License Key");
        editText.setHintTextColor(Color.parseColor("#666666"));
        editText.setTextColor(-1);
        editText.setTextSize(2, 14);
        editText.setPadding(dp(20), dp(15), dp(20), dp(15));
        editText.setSingleLine(true);
        editText.setGravity(17);
        editText.setImeOptions(6);
        GradientDrawable fieldBg = new GradientDrawable();
        fieldBg.setColor(Color.parseColor("#121212"));
        fieldBg.setCornerRadius(dp(30));
        fieldBg.setStroke(dp(1), Color.parseColor("#2A2A2A"));
        editText.setBackground(fieldBg);
        editText.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(50)));
        if (!str.isEmpty()) {
            editText.setText(str);
        }
        linearLayout.addView(editText);

        final ProgressBar progressBar = new ProgressBar(this);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#9B3333"), PorterDuff.Mode.SRC_IN);
        LinearLayout.LayoutParams spinnerLp = new LinearLayout.LayoutParams(dp(30), dp(30));
        spinnerLp.topMargin = dp(15);
        progressBar.setLayoutParams(spinnerLp);
        progressBar.setVisibility(8);
        linearLayout.addView(progressBar);
        
        frameLayout.addView(linearLayout);

        final TextView statusText = new TextView(this);
        statusText.setTextColor(Color.parseColor("#555555"));
        statusText.setTextSize(2, 11);
        statusText.setTypeface(Typeface.DEFAULT_BOLD);
        FrameLayout.LayoutParams statusLp = new FrameLayout.LayoutParams(-2, -2);
        statusLp.gravity = 83;
        statusLp.leftMargin = dp(20);
        statusLp.bottomMargin = dp(20);
        statusText.setLayoutParams(statusLp);
        statusText.setText("STATUS:");
        frameLayout.addView(statusText);

        this.root.addView(frameLayout);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == 6 || (keyEvent != null && keyEvent.getAction() == 0 && keyEvent.getKeyCode() == 66)) {
                    String key = editText.getText().toString().trim();
                    if (!key.isEmpty()) {
                        iniciarFluxoDeLogin(key, statusText, editText, progressBar, frameLayout);
                        return true;
                    }
                    animacaoShake(editText);
                    return true;
                }
                return false;
            }
        });

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        frameLayout.startAnimation(alphaAnimation);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void iniciarFluxoDeLogin(final String key, final TextView statusText, final EditText input, final ProgressBar spinner, final View container) {
        input.setEnabled(false);
        spinner.setVisibility(0);
        statusText.setText("STATUS: VALIDANDO...");
        statusText.setTextColor(Color.parseColor("#9B3333"));

        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject body = new JSONObject();
                    body.put("key", key);
                    body.put("hwid", getHwid());
                    body.put("application_id", APPLICATION_ID);

                    HttpURLConnection conn = (HttpURLConnection) new URL(API_VALIDATE_URL).openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setRequestProperty("X-Application-Credential", APP_CREDENTIAL);
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();
                    os.write(body.toString().getBytes("UTF-8"));
                    os.close();

                    int code = conn.getResponseCode();
                    InputStream is = (code >= 200 && code <= 299) ? conn.getInputStream() : conn.getErrorStream();
                    StringBuilder sb = new StringBuilder();
                    if (is != null) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        String line;
                        while ((line = br.readLine()) != null) sb.append(line);
                        br.close();
                    }
                    conn.disconnect();

                    String raw = sb.toString().trim();
                    JSONObject rootJson = null;
                    try {
                        rootJson = new JSONObject(raw);
                    } catch (Exception ignored) {}

                    boolean valid = false;
                    String message = null;
                    JSONObject data = null;

                    if (rootJson != null) {
                        data = rootJson.optJSONObject("data");
                        if (data == null) data = rootJson;
                        valid = data.optBoolean("valid", false) || data.optBoolean("success", false) || data.optBoolean("authenticated", false) || rootJson.optBoolean("authorized", false);
                        String st = data.optString("status", "").toLowerCase();
                        if (!valid && (st.equals("ok") || st.equals("active") || st.equals("valid"))) valid = true;
                        message = data.optString("message", data.optString("error", rootJson.optString("message", rootJson.optString("error", null))));
                    } else if (code == 200 && (raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("ok"))) {
                        valid = true;
                    }

                    final boolean isOk = (code == 200 && valid);
                    final String msg = message;
                    final JSONObject finalData = data;

                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (isOk) {
                                String uname = "Precision";
                                String uexp = "-";
                                String udays = "0";
                                String uavatar = DEFAULT_AVATAR;
                                String ubanner = DEFAULT_BANNER;

                                if (finalData != null) {
                                    JSONObject profile = finalData.optJSONObject("profile");
                                    if (profile == null) profile = finalData;
                                    uname = profile.optString("username", uname);
                                    udays = profile.optString("days_left", udays);
                                    uexp = profile.optString("expire_date", uexp);
                                    uavatar = profile.optString("avatar_url", uavatar);
                                    ubanner = profile.optString("banner_url", ubanner);
                                }

                                userId = "k_" + Integer.toHexString(key.hashCode());
                                userName = uname;
                                userExpireDate = uexp;
                                userDaysLeft = udays;
                                userAvatarUrl = uavatar;
                                userBannerUrl = ubanner;

                                prefs.edit()
                                    .putString("user_key", key)
                                    .putString("key_user_id", userId)
                                    .putString("user_name", userName)
                                    .putString("user_expire", userExpireDate)
                                    .putString("user_days", userDaysLeft)
                                    .putString("avatar_url", userAvatarUrl)
                                    .putString("banner_url", userBannerUrl)
                                    .apply();

                                statusText.setText("STATUS: SUCESSO!");
                                statusText.setTextColor(Color.parseColor("#32D74B"));
                                spinner.setVisibility(8);
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    public void run() {
                                        startOverlayService();
                                        showMainPanel();
                                    }
                                }, 600);
                            } else {
                                input.setEnabled(true);
                                spinner.setVisibility(8);
                                statusText.setTextColor(Color.parseColor("#FF453A"));
                                statusText.setText("STATUS: " + (msg != null ? msg.toUpperCase() : "CHAVE INVALIDA"));
                                animacaoShake(input);
                            }
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            input.setEnabled(true);
                            spinner.setVisibility(8);
                            statusText.setTextColor(Color.parseColor("#FF453A"));
                            statusText.setText("STATUS: ERRO DE CONEXAO");
                            animacaoShake(input);
                        }
                    });
                }
            }
        }).start();
    }

    private void validateCachedKey(final String key) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject body = new JSONObject();
                    body.put("key", key);
                    body.put("hwid", getHwid());
                    body.put("application_id", APPLICATION_ID);

                    HttpURLConnection conn = (HttpURLConnection) new URL(API_VALIDATE_URL).openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setRequestProperty("X-Application-Credential", APP_CREDENTIAL);
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();
                    os.write(body.toString().getBytes("UTF-8"));
                    os.close();

                    int code = conn.getResponseCode();
                    InputStream is = (code >= 200 && code <= 299) ? conn.getInputStream() : conn.getErrorStream();
                    StringBuilder sb = new StringBuilder();
                    if (is != null) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        String line;
                        while ((line = br.readLine()) != null) sb.append(line);
                        br.close();
                    }
                    conn.disconnect();

                    String raw = sb.toString().trim();
                    JSONObject rootJson = null;
                    try {
                        rootJson = new JSONObject(raw);
                    } catch (Exception ignored) {}

                    boolean valid = false;
                    JSONObject data = null;

                    if (rootJson != null) {
                        data = rootJson.optJSONObject("data");
                        if (data == null) data = rootJson;
                        valid = data.optBoolean("valid", false) || data.optBoolean("success", false) || data.optBoolean("authenticated", false) || rootJson.optBoolean("authorized", false);
                        String st = data.optString("status", "").toLowerCase();
                        if (!valid && (st.equals("ok") || st.equals("active") || st.equals("valid"))) valid = true;
                    } else if (code == 200 && (raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("ok"))) {
                        valid = true;
                    }

                    final boolean isOk = (code == 200 && valid);
                    final JSONObject finalData = data;

                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (isOk) {
                                if (finalData != null) {
                                    JSONObject profile = finalData.optJSONObject("profile");
                                    if (profile == null) profile = finalData;
                                    userName = profile.optString("username", userName.isEmpty() ? "Precision" : userName);
                                    userExpireDate = profile.optString("expire_date", userExpireDate);
                                    userDaysLeft = profile.optString("days_left", userDaysLeft);
                                    userAvatarUrl = profile.optString("avatar_url", userAvatarUrl);
                                    userBannerUrl = profile.optString("banner_url", userBannerUrl);
                                }
                                startOverlayService();
                                showMainPanel();
                            } else {
                                prefs.edit().remove("user_key").apply();
                                showLoginScreen("");
                            }
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            prefs.edit().remove("user_key").apply();
                            showLoginScreen("");
                        }
                    });
                }
            }
        }).start();
    }

    private String getHwid() {
        String cached = prefs.getString("hwid", "");
        if (!cached.isEmpty()) return cached;
        String id = null;
        try {
            id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Throwable ignored) {}
        if (id == null || id.isEmpty()) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                byte[] bytes = (Build.MANUFACTURER + "|" + Build.MODEL + "|" + Build.FINGERPRINT).getBytes("UTF-8");
                byte[] digest = md.digest(bytes);
                StringBuilder sb = new StringBuilder();
                for (byte b : digest) sb.append(String.format("%02x", b));
                id = sb.toString();
            } catch (Exception e) {
                id = "unknown";
            }
        }
        prefs.edit().putString("hwid", id).apply();
        return id;
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
        linearLayout.setPadding(dp(20), dp(40), dp(20), dp(40));
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(16);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.bottomMargin = dp(25);
        linearLayout2.setLayoutParams(layoutParams);
        TextView textView = new TextView(this);
        textView.setText("PRECISION HDTRICK");
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
        linearLayout3.setPadding(dp(20), dp(14), dp(20), dp(14));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#151515"));
        gradientDrawable.setCornerRadius(dp(30));
        linearLayout3.setBackground(gradientDrawable);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
        layoutParams2.bottomMargin = dp(25);
        linearLayout3.setLayoutParams(layoutParams2);
        TextView textView3 = new TextView(this);
        textView3.setText("Android " + Build.VERSION.RELEASE + "  •  " + Build.MODEL);
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
        view.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(30)));
        linearLayout.addView(view);
        linearLayout.addView(createSectionLabel("👥", "SISTEMA"));
        linearLayout.addView(createLogoutCard());
        scrollView.addView(linearLayout);
        this.root.addView(scrollView);
    }

    private View createProfileCard() {
        FrameLayout frameLayout = new FrameLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, dp(110));
        layoutParams.bottomMargin = dp(35);
        frameLayout.setLayoutParams(layoutParams);
        frameLayout.setClipToOutline(true);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#151515"));
        gradientDrawable.setCornerRadius(dp(16));
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
        linearLayout.setPadding(dp(18), 0, dp(18), 0);
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        FrameLayout frameLayout2 = new FrameLayout(this);
        LinearLayout.LayoutParams avatarLp = new LinearLayout.LayoutParams(dp(56), dp(56));
        avatarLp.rightMargin = dp(16);
        frameLayout2.setLayoutParams(avatarLp);
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setShape(1);
        gradientDrawable2.setColor(Color.parseColor("#222222"));
        gradientDrawable2.setStroke(dp(2), Color.parseColor("#0A84FF"));
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
        textView.setText("ID: " + (this.userId.isEmpty() ? "0000000000" : this.userId));
        textView.setTextColor(Color.parseColor("#AAAAAA"));
        textView.setTextSize(2, 11);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        linearLayout2.addView(textView);
        TextView textView2 = new TextView(this);
        String str = this.userName;
        textView2.setText((str == null || str.trim().isEmpty()) ? "Precision" : this.userName);
        textView2.setTextColor(-1);
        textView2.setTextSize(2, 18);
        textView2.setTypeface(Typeface.DEFAULT_BOLD);
        linearLayout2.addView(textView2);
        linearLayout.addView(linearLayout2);
        TextView textView3 = new TextView(this);
        textView3.setText(this.userDaysLeft + "d");
        textView3.setTextColor(Color.parseColor("#FFD60A"));
        textView3.setTextSize(2, 12);
        textView3.setTypeface(Typeface.DEFAULT_BOLD);
        textView3.setPadding(dp(12), dp(5), dp(12), dp(5));
        GradientDrawable gradientDrawable3 = new GradientDrawable();
        gradientDrawable3.setColor(Color.parseColor("#33FFFFFF"));
        gradientDrawable3.setCornerRadius(dp(8));
        gradientDrawable3.setStroke(dp(1), Color.parseColor("#4DFFD60A"));
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
        layoutParams.bottomMargin = dp(12);
        layoutParams.leftMargin = dp(5);
        linearLayout.setLayoutParams(layoutParams);
        TextView textView = new TextView(this);
        textView.setText("◆");
        textView.setTextColor(Color.parseColor("#8E8E93"));
        textView.setTextSize(2, 10);
        textView.setPadding(0, 0, dp(8), 0);
        linearLayout.addView(textView);
        TextView textView2 = new TextView(this);
        textView2.setText(str2);
        textView2.setTextColor(Color.parseColor("#8E8E93"));
        textView2.setTextSize(2, 12);
        textView2.setTypeface(Typeface.DEFAULT_BOLD);
        linearLayout.addView(textView2);
        return linearLayout;
    }

    private View createGamesCard() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#151515"));
        gradientDrawable.setCornerRadius(dp(16));
        linearLayout.setBackground(gradientDrawable);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        linearLayout.addView(createGameItem("FreeFire Normal", "Injetar Bypass & ESP", "com.dts.freefireth", true));
        View view = new View(this);
        view.setBackgroundColor(Color.parseColor("#2C2C2E"));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, dp(1));
        layoutParams.leftMargin = dp(70);
        view.setLayoutParams(layoutParams);
        linearLayout.addView(view);
        linearLayout.addView(createGameItem("FreeFire MAX", "Injetar Bypass & ESP", "com.dts.freefiremax", false));
        return linearLayout;
    }

    private View createGameItem(String str, String str2, final String str3, boolean z) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        linearLayout.setPadding(dp(15), dp(z ? 18 : 14), dp(15), dp(z ? 14 : 18));
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        TextView textView = new TextView(this);
        textView.setText("FF");
        textView.setTextColor(Color.parseColor("#BBBBBB"));
        textView.setTextSize(2, 12);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setGravity(17);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#1F1F1F"));
        gradientDrawable.setCornerRadius(dp(12));
        gradientDrawable.setStroke(dp(1), Color.parseColor("#2A2A2A"));
        textView.setBackground(gradientDrawable);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dp(45), dp(45));
        layoutParams.rightMargin = dp(15);
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
        textView3.setTextSize(2, 12);
        textView3.setPadding(0, dp(2), 0, 0);
        linearLayout2.addView(textView3);
        linearLayout.addView(linearLayout2);
        TextView textView4 = new TextView(this);
        textView4.setText("›");
        textView4.setTextColor(Color.parseColor("#666666"));
        textView4.setTextSize(2, 22);
        textView4.setPadding(0, 0, dp(5), dp(4));
        linearLayout.addView(textView4);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!exploitConfirmado) {
                    mostrarDialogoConfirmacao(str3);
                } else {
                    mostrarCarregamentoEIniciar(str3);
                }
            }
        });
        return linearLayout;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void mostrarDialogoConfirmacao(final String packageName) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(false);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(dp(25), dp(25), dp(25), dp(20));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#1C1C1E"));
        gradientDrawable.setCornerRadius(dp(20));
        gradientDrawable.setStroke(dp(1), Color.parseColor("#333333"));
        linearLayout.setBackground(gradientDrawable);
        TextView textView = new TextView(this);
        textView.setText("▲");
        textView.setTextColor(Color.parseColor("#FFD60A"));
        textView.setTextSize(2, 24);
        textView.setGravity(17);
        textView.setPadding(0, 0, 0, dp(10));
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
        textView3.setPadding(0, dp(15), 0, dp(25));
        linearLayout.addView(textView3);
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(0);
        linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        TextView textView4 = new TextView(this);
        textView4.setText("CANCELAR");
        textView4.setTextColor(-1);
        textView4.setTextSize(2, 12);
        textView4.setTypeface(Typeface.DEFAULT_BOLD);
        textView4.setGravity(17);
        textView4.setPadding(0, dp(12), 0, dp(12));
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setColor(Color.parseColor("#2C2C2E"));
        gradientDrawable2.setCornerRadius(dp(10));
        textView4.setBackground(gradientDrawable2);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        layoutParams.rightMargin = dp(10);
        textView4.setLayoutParams(layoutParams);
        TextView textView5 = new TextView(this);
        textView5.setText("CONFIRMAR");
        textView5.setTextColor(-16777216);
        textView5.setTextSize(2, 12);
        textView5.setTypeface(Typeface.DEFAULT_BOLD);
        textView5.setGravity(17);
        textView5.setPadding(0, dp(12), 0, dp(12));
        GradientDrawable gradientDrawable3 = new GradientDrawable();
        gradientDrawable3.setColor(Color.parseColor("#FFD60A"));
        gradientDrawable3.setCornerRadius(dp(10));
        textView5.setBackground(gradientDrawable3);
        textView5.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        linearLayout2.addView(textView4);
        linearLayout2.addView(textView5);
        linearLayout.addView(linearLayout2);
        textView4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        textView5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                exploitConfirmado = true;
                dialog.dismiss();
                mostrarCarregamentoEIniciar(packageName);
            }
        });
        dialog.setContentView(linearLayout);
        dialog.getWindow().setLayout(dp(300), -2);
        dialog.show();
    }

    private void mostrarCarregamentoEIniciar(final String packageName) {
        final FrameLayout loadingOverlay = new FrameLayout(this);
        loadingOverlay.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        loadingOverlay.setBackgroundColor(Color.parseColor("#E6000000"));
        loadingOverlay.setClickable(true);
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
        textView.setPadding(0, dp(20), 0, 0);
        linearLayout.addView(textView);
        loadingOverlay.addView(linearLayout);
        this.root.addView(loadingOverlay);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(400);
        loadingOverlay.startAnimation(alphaAnimation);
        
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            public void run() {
                textView.setText("Bypass no Kernel ativo...");
            }
        }, 1200);
        handler.postDelayed(new Runnable() {
            public void run() {
                textView.setText("Modificando Memória...");
            }
        }, 2500);
        handler.postDelayed(new Runnable() {
            public void run() {
                textView.setText("Iniciando Jogo!");
                textView.setTextColor(Color.parseColor("#32D74B"));
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                        if (launchIntent != null) {
                            startActivity(launchIntent);
                        } else {
                            Toast.makeText(MainActivity.this, "Jogo não instalado no aparelho.", Toast.LENGTH_SHORT).show();
                        }
                        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                        fadeOut.setDuration(300);
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            public void onAnimationEnd(Animation animation) {
                                root.removeView(loadingOverlay);
                            }
                            public void onAnimationRepeat(Animation animation) {}
                            public void onAnimationStart(Animation animation) {}
                        });
                        loadingOverlay.startAnimation(fadeOut);
                    }
                }, 800);
            }
        }, 3800);
    }

    private View createExploitCard() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#151515"));
        gradientDrawable.setCornerRadius(dp(16));
        linearLayout.setBackground(gradientDrawable);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.bottomMargin = dp(35);
        linearLayout.setLayoutParams(layoutParams);
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(16);
        linearLayout2.setPadding(dp(20), dp(20), dp(20), dp(20));
        ImageView imageView = new ImageView(this);
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setShape(1);
        gradientDrawable2.setColor(0);
        gradientDrawable2.setStroke(dp(2), Color.parseColor("#FFD60A"), dp(8), dp(6));
        imageView.setImageDrawable(gradientDrawable2);
        LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(dp(25), dp(25));
        imgLp.rightMargin = dp(15);
        imageView.setLayoutParams(imgLp);
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
        textView.setTextSize(2, 15);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        linearLayout3.addView(textView);
        LinearLayout linearLayout4 = new LinearLayout(this);
        linearLayout4.setOrientation(0);
        linearLayout4.setPadding(0, dp(4), 0, 0);
        final TextView[] textViewArr = new TextView[4];
        final TextView[] textViewArr2 = new TextView[4];
        String[] strArr = {"KRW", "SBX", "VFS", "RC"};
        for (int i = 0; i < 4; i++) {
            TextView textView2 = new TextView(this);
            textViewArr[i] = textView2;
            textView2.setText("●");
            textViewArr[i].setTextColor(Color.parseColor("#8E8E93"));
            textViewArr[i].setTextSize(2, 8);
            textViewArr[i].setPadding(0, dp(2), dp(4), 0);
            
            TextView textView3 = new TextView(this);
            textViewArr2[i] = textView3;
            textView3.setText(strArr[i]);
            textViewArr2[i].setTextColor(Color.parseColor("#8E8E93"));
            textViewArr2[i].setTextSize(2, 11);
            textViewArr2[i].setTypeface(Typeface.DEFAULT_BOLD);
            textViewArr2[i].setPadding(0, 0, dp(10), 0);
            
            linearLayout4.addView(textViewArr[i]);
            linearLayout4.addView(textViewArr2[i]);
        }
        linearLayout3.addView(linearLayout4);
        linearLayout2.addView(linearLayout3);
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#8E8E93"), PorterDuff.Mode.SRC_IN);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(dp(20), dp(20)));
        linearLayout2.addView(progressBar);
        linearLayout.addView(linearLayout2);
        
        ValueAnimator valueAnimatorOfInt = ValueAnimator.ofInt(0, 100);
        valueAnimatorOfInt.setDuration(4500);
        valueAnimatorOfInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int iIntValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                textView.setText("Exploit " + iIntValue + "%...");
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
        
        View divider = new View(this);
        divider.setBackgroundColor(Color.parseColor("#2C2C2E"));
        divider.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(1)));
        linearLayout.addView(divider);
        
        LinearLayout linearLayout7 = new LinearLayout(this);
        linearLayout7.setOrientation(0);
        linearLayout7.setGravity(16);
        linearLayout7.setPadding(dp(20), dp(18), dp(20), dp(18));
        TextView textView4 = new TextView(this);
        textView4.setText("License Key");
        textView4.setTextColor(Color.parseColor("#FFFFFF"));
        textView4.setTextSize(2, 15);
        textView4.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        linearLayout7.addView(textView4);
        TextView textView5 = new TextView(this);
        textView5.setText("Faltam " + this.userDaysLeft + " dias");
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
        linearLayout.setPadding(dp(15), dp(18), dp(15), dp(18));
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#151515"));
        gradientDrawable.setCornerRadius(dp(16));
        linearLayout.setBackground(gradientDrawable);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        TextView textView = new TextView(this);
        textView.setText("⏻");
        textView.setTextColor(-1);
        textView.setTextSize(2, 16);
        GradientDrawable gradientDrawable2 = new GradientDrawable();
        gradientDrawable2.setColor(Color.parseColor("#0A84FF"));
        gradientDrawable2.setCornerRadius(dp(12));
        textView.setBackground(gradientDrawable2);
        textView.setGravity(17);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(45), dp(45));
        lp.rightMargin = dp(15);
        textView.setLayoutParams(lp);
        linearLayout.addView(textView);
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(1);
        linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView textView2 = new TextView(this);
        textView2.setText("Remover Licença");
        textView2.setTextColor(Color.parseColor("#FFFFFF"));
        textView2.setTextSize(2, 16);
        textView2.setTypeface(Typeface.DEFAULT_BOLD);
        linearLayout2.addView(textView2);
        TextView textView3 = new TextView(this);
        textView3.setText("Desconectar do aparelho atual");
        textView3.setTextColor(Color.parseColor("#8E8E93"));
        textView3.setTextSize(2, 12);
        textView3.setPadding(0, dp(2), 0, 0);
        linearLayout2.addView(textView3);
        linearLayout.addView(linearLayout2);
        TextView textView4 = new TextView(this);
        textView4.setText("↗");
        textView4.setTextColor(Color.parseColor("#666666"));
        textView4.setTextSize(2, 20);
        linearLayout.addView(textView4);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    stopService(new Intent(MainActivity.this, Class.forName("com.s.OverlayService")));
                    prefs.edit().clear().apply();
                    userName = "";
                    userId = "";
                    showLoginScreen("");
                } catch (ClassNotFoundException e) {
                    throw new NoClassDefFoundError(e.getMessage());
                }
            }
        });
        return linearLayout;
    }

    private void carregarImagemDaUrl(final String url, final ImageView imageView) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    final Bitmap bmp = BitmapFactory.decodeStream(new URL(url).openStream());
                    if (bmp != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                imageView.setImageBitmap(bmp);
                            }
                        });
                    }
                } catch (Exception e) {}
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animacaoShake(View view) {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 15, 0, 0);
        translateAnimation.setDuration(50);
        translateAnimation.setRepeatCount(3);
        view.startAnimation(translateAnimation);
    }

    private int dp(float f) {
        return Math.round(TypedValue.applyDimension(1, f, getResources().getDisplayMetrics()));
    }

    private void trustAllCertificates() {
        try {
            TrustManager[] trustManagerArr = {new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) {}
                public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) {}
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            }};
            SSLContext sSLContext = SSLContext.getInstance("TLS");
            sSLContext.init(null, trustManagerArr, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sSLContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String str, SSLSession sSLSession) { return true; }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}