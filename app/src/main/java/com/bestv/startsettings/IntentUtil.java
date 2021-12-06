package com.bestv.startsettings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

public class IntentUtil {

    public static void startActivity(Context context, String act, String str2, int i) {
        try {
            Intent intent = new Intent(act);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(str2, i);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "没有找到应用!", Toast.LENGTH_LONG).show();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    public static void startApplication(ComponentName componentName, Context context) {
        try {
            context.getPackageManager().getActivityInfo(componentName, 0);
            Intent localIntent = new Intent("android.intent.action.MAIN");
            localIntent.addCategory("android.intent.category.LAUNCHER");
            localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            localIntent.setComponent(componentName);
            context.startActivity(localIntent);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, "没有找到应用!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
    public static void startHome(Context context) {
        try {
            Intent intent = new Intent("tv.fun.intent.action.ACTION_HOME");
            intent.putExtra("isLongPressed", 2);
            intent.setClassName("com.bestv.ott", "com.bestv.ott.HomeTransferService");
            intent.putExtra("sendFrom", 1);
            context.startService(intent);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
    public static void startInputSourceSelectDialog(Context context) {
        try {
            Intent intent2 = new Intent("com.android.systemui.action.inputsource");
            intent2.setComponent(new ComponentName("com.android.systemui", "com.android.systemui.inputsource.InputSourceService"));
            intent2.putExtra("sendFrom", 2);
            context.startService(intent2);
        } catch (Exception e2) {
            e2.printStackTrace();
            Toast.makeText(context, "没有找到应用!", Toast.LENGTH_LONG).show();
        }
    }
}
