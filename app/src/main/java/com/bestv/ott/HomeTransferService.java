package com.bestv.ott;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;

public class HomeTransferService extends Service {
    public HomeTransferService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int i3;
        boolean z = false;
        if (intent == null) {
            Log.i("HomeTransferService", "onStartCommand, intent is null");
        } else {
            String action = intent.getAction();
            int intExtra = intent.getIntExtra("sendFrom", -1);
            boolean booleanExtra = intent.getBooleanExtra("isLongPressed", false);
            Log.i("HomeTransferService", "onStartCommand, action:" + action + ", sendFrom:" + intExtra + ", isLongPressed:" + booleanExtra);
            if ("tv.fun.intent.action.ACTION_HOME".equalsIgnoreCase(action)) {

                try {
                    Log.i("HomeTransferService", "CHILDREN_INFO_URL call 1111");
                    Bundle a = a(Uri.parse("content://tv.fun.children.provider/childinfo"), "isChildrenMode");
                    if (a != null) {
                        z = a.getBoolean("mode", false);
                    } else {
                        Bundle a2 = a(Uri.parse("content://tv.fun.children.provider/childinfo"), "isChildrenMode");
                        Log.i("HomeTransferService", "CHILDREN_INFO_URL is null 11111");
                        if (a2 != null) {
                            z = a2.getBoolean("mode", false);
                        } else {
                            Log.i("HomeTransferService", "CHILDREN_INFO_URL is null 222222");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i("HomeTransferService", "onStartCommand, isChildrenMode:" + z);
                if (z) {
                    Intent intent2 = new Intent(action);
                    intent2.setComponent(new ComponentName("tv.fun.children", "tv.fun.children.ChildrenService"));
                    intent2.putExtra("sendFrom", intExtra);
                    intent2.putExtra("isLongPressed", booleanExtra);
                    startService(intent2);
                } else {
                    Intent intent3 = new Intent("android.intent.action.MAIN", (Uri) null);
                    intent3.addCategory("android.intent.category.HOME");
                    intent3.addFlags(270532608);
                    startActivity(intent3);
                    Log.i("HomeTransferService", "start home Activity");
                }

            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Bundle a(Uri uri, String str) {
        return getContentResolver().call(uri, "str", null, null);
    }

}