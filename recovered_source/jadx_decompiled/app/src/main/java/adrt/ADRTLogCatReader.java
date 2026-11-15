package adrt;

import android.content.Context;
import android.content.pm.PackageManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/* JADX INFO: loaded from: /home/ubuntu/apk_source_work/raw/classes.dex */
public class ADRTLogCatReader implements Runnable {
    private static Context context;

    public static void onContext(Context context2, String debuggerPackageName) {
        if (context != null) {
            return;
        }
        context = context2.getApplicationContext();
        boolean isDebuggable = (context2.getApplicationInfo().flags & 2) != 0;
        if (!isDebuggable) {
            return;
        }
        try {
            PackageManager pm = context2.getPackageManager();
            pm.getPackageInfo(debuggerPackageName, 128);
            ADRTSender.onContext(context, debuggerPackageName);
            Thread thread = new Thread(new ADRTLogCatReader(), "LogCat");
            thread.start();
        } catch (PackageManager.NameNotFoundException e) {
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            Process process = Runtime.getRuntime().exec("logcat -v threadtime");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()), 20);
            while (true) {
                String line = bufferedReader.readLine();
                if (line != null) {
                    ADRTSender.sendLogcatLines(new String[]{line});
                } else {
                    return;
                }
            }
        } catch (IOException e) {
        }
    }
}
