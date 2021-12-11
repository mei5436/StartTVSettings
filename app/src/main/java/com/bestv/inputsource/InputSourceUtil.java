package com.bestv.inputsource;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.bestv.*;

import java.util.List;

public class InputSourceUtil {
    public static final String SCREEN_ART_MODEL = "536D4006CF101";
    public static final String SCREEN_MODEL = "V400HJ6_PE1";

    private static final String MODEL = Build.MODEL;
    protected static Typeface frontType = null;
    private static final boolean is938;
    private static int mIsWd = -1;
    private static int mPanelType = -1;
    private static String sBrand = null;
    private static String sDisplayName = null;
    private static DisplayPortInfo sDisplayPortInfo;
    private static String sScreenModeFactory = null;
    private static String sSystemVersion = null;

    static {
        boolean z;
        if (Build.VERSION.SDK_INT >= 23) {
            z = true;
        } else {
            z = false;
        }
        is938 = z;
    }

    public static Typeface getFont(Context context) {
        synchronized (InputSourceUtil.class) {
            if (frontType == null) {
                frontType = Typeface.createFromAsset(context.getAssets(), "font/iconfont.ttf");
            }
        }
        return frontType;
    }

    public static String getCurrentActivity() {
        ActivityManager.RunningTaskInfo info;
        ComponentName component;
        List<ActivityManager.RunningTaskInfo> list = ((ActivityManager) TvSystemApp.getInstance().getSystemService("activity")).getRunningTasks(1);
        if (list == null || list.size() <= 0 || (info = list.get(0)) == null || (component = info.topActivity) == null) {
            return null;
        }
        return component.getClassName();
    }

    public static String getCurrentPackage() {
        ActivityManager.RunningTaskInfo info;
        ComponentName component;
        List<ActivityManager.RunningTaskInfo> list = ((ActivityManager) TvSystemApp.getInstance().getSystemService("activity")).getRunningTasks(1);
        if (list == null || list.size() <= 0 || (info = list.get(0)) == null || (component = info.topActivity) == null) {
            return null;
        }
        return component.getPackageName();
    }

    public static DisplayPortInfo getPortInfo(Context context) {
        if (sDisplayPortInfo != null) {
            return sDisplayPortInfo;
        }
        sDisplayPortInfo = new DisplayPortInfo();
        String MODEL2 = Build.MODEL;
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://" + "model" + "/device_info"), new String[]{"display_port", "real_port", "av_type", "ypbpr_type", "multiline"}, "device_model= ? and panel_model = ? and panel_class = ? ", new String[]{MODEL2, SCREEN_MODEL, SCREEN_ART_MODEL}, null);
        if (cursor == null) {
            return sDisplayPortInfo;
        }
        try {
            if (cursor.moveToFirst()) {
                sDisplayPortInfo.setDisplayPort(cursor.getString(cursor.getColumnIndexOrThrow("display_port")));
                sDisplayPortInfo.setRealPort(cursor.getString(cursor.getColumnIndexOrThrow("real_port")));
                sDisplayPortInfo.setAvPortType(cursor.getInt(cursor.getColumnIndexOrThrow("av_type")));
                sDisplayPortInfo.setYuvPortType(cursor.getInt(cursor.getColumnIndexOrThrow("ypbpr_type")));
                sDisplayPortInfo.setMultiLine(cursor.getInt(cursor.getColumnIndexOrThrow("multiline")) == 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return sDisplayPortInfo;
    }

    public static String getDeviceBrand(Context context) {
        if (sBrand != null) {
            return sBrand;
        }
        String MODEL2 = Build.MODEL;
        Uri CONTENT_URI = Uri.parse("content://" + "model" + "/device_info");
        Cursor cursor = context.getContentResolver().query(CONTENT_URI, new String[]{"brand"}, "device_model= ? and panel_model = ? and panel_class = ? ", new String[]{MODEL2, SCREEN_MODEL, SCREEN_ART_MODEL}, null);
        if (cursor == null) {
            return sBrand;
        }
        try {
            if (cursor.moveToFirst()) {
                sBrand = cursor.getString(cursor.getColumnIndex("brand"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return sBrand;
    }

    public static boolean hiddenInputSource() {
        if (-1 == mIsWd) {
            initModel(TvSystemApp.getInstance());
        }
        if (mIsWd == 1) {
            return true;
        }
        return false;
    }

    public static void initModel(Context context) {
        Uri CONTENT_URI = Uri.parse("content://" + "model" + "/device_info");
        Cursor cursor = context.getContentResolver().query(CONTENT_URI, new String[]{"panel_4k", "is_wd"}, "device_model= ? and panel_model = ? and panel_class = ? ", new String[]{MODEL, SCREEN_MODEL, SCREEN_ART_MODEL}, null);
        if (cursor == null) {
            mPanelType = 2;
            mIsWd = 0;
            return;
        }
        try {
            if (cursor.moveToFirst()) {
                mPanelType = cursor.getInt(cursor.getColumnIndex("panel_4k"));
                mIsWd = cursor.getInt(cursor.getColumnIndex("is_wd"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        if (mPanelType == -1) {
            mPanelType = 2;
        }
        if (mIsWd == -1) {
            mIsWd = 0;
        }
    }

    public static boolean isTCL(String brand) {
        Log.d("DeviceUtil", "brand = " + brand);
        if (TextUtils.isEmpty(brand) || !brand.equals("tcl")) {
            return false;
        }
        return true;
    }
}
