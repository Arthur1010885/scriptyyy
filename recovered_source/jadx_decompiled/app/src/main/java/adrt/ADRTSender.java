package adrt;

import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;

/* JADX INFO: loaded from: /home/ubuntu/apk_source_work/raw/classes.dex */
public class ADRTSender {
    private static Context context;
    private static String debuggerPackageName;

    public static void onContext(Context context2, String debuggerPackageName2) {
        context = context2;
        debuggerPackageName = debuggerPackageName2;
    }

    public static void sendConnect(String packageName) {
        Intent intent = new Intent();
        intent.setPackage(debuggerPackageName);
        intent.setAction("com.adrt.CONNECT");
        intent.putExtra("package", packageName);
        context.sendBroadcast(intent);
    }

    public static void sendStop(String packageName) {
        Intent intent = new Intent();
        intent.setPackage(debuggerPackageName);
        intent.setAction("com.adrt.STOP");
        intent.putExtra("package", packageName);
        context.sendBroadcast(intent);
    }

    public static void sendBreakpointHit(String packageName, ArrayList<String> variables, ArrayList<String> variableValues, ArrayList<String> variableKinds, ArrayList<String> stackMethods, ArrayList<String> stackLocations, ArrayList<String> stackLocationKinds) {
        Intent intent = new Intent();
        intent.setPackage(debuggerPackageName);
        intent.setAction("com.adrt.BREAKPOINT_HIT");
        intent.putExtra("package", packageName);
        intent.putExtra("variables", variables);
        intent.putExtra("variableValues", variableValues);
        intent.putExtra("variableKinds", variableKinds);
        intent.putExtra("stackMethods", stackMethods);
        intent.putExtra("stackLocations", stackLocations);
        intent.putExtra("stackLocationKinds", stackLocationKinds);
        context.sendBroadcast(intent);
    }

    public static void sendFields(String packageName, String path, ArrayList<String> fields, ArrayList<String> fieldValues, ArrayList<String> fieldKinds) {
        Intent intent = new Intent();
        intent.setPackage(debuggerPackageName);
        intent.setAction("com.adrt.FIELDS");
        intent.putExtra("package", packageName);
        intent.putExtra("path", path);
        intent.putExtra("fields", fields);
        intent.putExtra("fieldValues", fieldValues);
        intent.putExtra("fieldKinds", fieldKinds);
        context.sendBroadcast(intent);
    }

    public static void sendLogcatLines(String[] lines) {
        Intent intent = new Intent();
        intent.setPackage(debuggerPackageName);
        intent.setAction("com.adrt.LOGCAT_ENTRIES");
        intent.putExtra("lines", lines);
        context.sendBroadcast(intent);
    }
}
