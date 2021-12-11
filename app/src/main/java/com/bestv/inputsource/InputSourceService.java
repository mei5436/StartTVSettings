package com.bestv.inputsource;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.bestv.*;
import com.mstar.android.tv.TvCommonManager;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;

public class InputSourceService extends Service {
    private static final String TAG = InputSourceService.class.getSimpleName();
    private MyHandler handler = new MyHandler(this);
    private OnInputSourceChangeCallback mCallback = new OnInputSourceChangeCallback();
    private int mInputSource;
    private boolean mIsSourceNeedSwitch = false;
    private ThreeDModeObserver mObserverThreeDMode = null;
    private TvCommonManager mTvCommonmanager;

    public interface IOnInputSourceChangeCallback {
        void onDialogDismiss();

        void onSourceChange(int i);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getIntExtra("sendFrom", 0) != 1) {
            Bundle result = getContentResolver().call(Uri.parse("content://tv.fun.children.provider/childinfo"), "isChildrenMode", (String) null, (Bundle) null);
            if (result == null || !result.getBoolean("mode", false)) {
                Log.i(TAG, "showSourceSelectDialog, 2");
                showSourceSelectDialog();
            } else {
                String name = InputSourceUtil.getCurrentActivity();
                Log.i(TAG, "name=" + name);
                if (TextUtils.isEmpty(name) || !name.startsWith("com.android.systemui")) {
                    Log.i(TAG, "start service: ChildrenService");
                    Intent serviceIntent = new Intent("com.android.systemui.action.inputsource");
                    serviceIntent.setComponent(new ComponentName("tv.fun.children", "tv.fun.children.ChildrenService"));
                    startService(serviceIntent);
                } else {
                    Log.i(TAG, "showSourceSelectDialog, 1");
                    showSourceSelectDialog();
                }
            }
        } else {
            Log.i(TAG, "showSourceSelectDialog, 0");
            showSourceSelectDialog();
        }
        stopSelf();
        return 2;
    }

    private void showSourceSelectDialog() {
        InputSourceSelectDialog dialog = InputSourceSelectDialog.getInstance(TvSystemApp.getInstance());
        Log.i(TAG, "showSourceSelectDialog, dialog.isShowing(): " + dialog.isShowing());
        if (dialog.isShowing()) {
            dialog.dismiss();
            return;
        }
        this.mTvCommonmanager = TvCommonManager.getInstance();
        this.mObserverThreeDMode = new ThreeDModeObserver(this.handler);
        new AsyncTask<Void, Void, Integer>() {
            /* class com.android.systemui.inputsource.InputSourceService.AnonymousClass1 */

            /* access modifiers changed from: protected */
            public Integer doInBackground(Void... params) {
                int inputSource = Settings.System.getInt(InputSourceService.this.getContentResolver(), "input_source", 44);
                Log.i(InputSourceService.TAG, "doInBackground(), inputSource=" + inputSource);
                List<InputSourceObject> sourceList = InputSourceSelectDialog.getInputSouceObjectList(TvSystemApp.getInstance(), InputSourceUtil.getPortInfo(TvSystemApp.getInstance()));
                InputSourceObject lastSelectSrc = null;
                Iterator iterator = sourceList.iterator();
                while (true) {
                    if (!iterator.hasNext()) {
                        break;
                    }
                    InputSourceObject obj = (InputSourceObject) iterator.next();
                    if (obj.getInputSrc() == inputSource) {
                        lastSelectSrc = obj;
                        break;
                    }
                }
                if (lastSelectSrc != null) {
                    sourceList.remove(lastSelectSrc);
                    sourceList.add(0, lastSelectSrc);
                }
                InputSourceSelectDialog dialog = InputSourceSelectDialog.getInstance(TvSystemApp.getInstance());
                dialog.setInputSource(inputSource);
                dialog.setSourceList(sourceList);
                Log.i(InputSourceService.TAG, "doInBackground(), source init complete. ");
                try {
                    Bundle result = TvSystemApp.getInstance().getContentResolver().call(Uri.parse("content://tv.fun.children.provider/childinfo"), "inChildrenMode", (String) null, (Bundle) null);
                    if (result != null && result.getBoolean("mode", false)) {
                        return 3;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Bundle bundle = InputSourceService.this.getContentResolver().call(Uri.parse("content://com.bestv.ott.contentprovider.CommonProvider"), "getLauncherMode", (String) null, (Bundle) null);
                    if (bundle != null && bundle.getBoolean("isparent", false)) {
                        return 5;
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                return 1;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Integer result) {
                boolean z;
                boolean z2 = false;
                Log.i(InputSourceService.TAG, "onPostExecute()");
                InputSourceSelectDialog dialog = InputSourceSelectDialog.getInstance(TvSystemApp.getInstance());
                if ((result.intValue() & 2) != 0) {
                    z = true;
                } else {
                    z = false;
                }
                if ((result.intValue() & 4) != 0) {
                    z2 = true;
                }
                dialog.initUI(true, z, z2, InputSourceService.this.mCallback);
                dialog.show();
            }
        }.executeOnExecutor(Executors.newCachedThreadPool(), new Void[0]);
        getContentResolver().registerContentObserver(Uri.parse("content://mstar.tv.usersetting/systemsetting"), true, this.mObserverThreeDMode);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    /* access modifiers changed from: private */
    public class OnInputSourceChangeCallback implements IOnInputSourceChangeCallback {
        private OnInputSourceChangeCallback() {
        }

        @Override // com.android.systemui.inputsource.InputSourceService.IOnInputSourceChangeCallback
        public void onSourceChange(int inputSource) {
            InputSourceService.this.mInputSource = inputSource;
            InputSourceService.this.changeInputSource(inputSource);
        }

        @Override // com.android.systemui.inputsource.InputSourceService.IOnInputSourceChangeCallback
        public void onDialogDismiss() {
            if (InputSourceService.this.mObserverThreeDMode != null) {
                InputSourceService.this.getContentResolver().unregisterContentObserver(InputSourceService.this.mObserverThreeDMode);
                InputSourceService.this.mObserverThreeDMode = null;
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void changeInputSource(final int selectedInputSource) {
        int curIntSource = this.mTvCommonmanager.getCurrentTvInputSource();
        if (curIntSource == selectedInputSource && "com.mstar.tv.tvplayer.ui".equals(InputSourceUtil.getCurrentPackage())) {
            Log.i(TAG, "same source select, mInputSource=" + this.mInputSource);
            this.handler.sendEmptyMessage(-101);
        } else if (curIntSource < 34 || curIntSource == 42 || curIntSource == 43) {
            new Thread(new Runnable() {
                /* class com.android.systemui.inputsource.InputSourceService.AnonymousClass2 */

                public void run() {
                    InputSourceService.this.handler.sendEmptyMessage(-100);
                    InputSourceService.this.mIsSourceNeedSwitch = true;
                    InputSourceService.this.updateSourceInputType(selectedInputSource);
                }
            }).start();
        } else {
            sendBroadcast(new Intent("source.switch.from.storage"));
            this.handler.sendEmptyMessage(-100);
            executePreviousTask(selectedInputSource);
        }
    }

    private void executePreviousTask(final int selectedInputSource) {
        new Thread(new Runnable() {
            /* class com.android.systemui.inputsource.InputSourceService.AnonymousClass3 */

            public void run() {
                int inputSource = selectedInputSource;
                Log.i(InputSourceService.TAG, "executePreviousTask, startActivity SOURCE_CHANGE intent: inputSource = " + inputSource);
                Intent intent = new Intent("com.mstar.android.intent.action.START_TV_PLAYER");
                intent.putExtra("inputSrc", inputSource);
                intent.putExtra("inputAntennaType", 0);
                intent.addFlags(268435456);
                InputSourceService.this.startActivity(intent);
                try {
                    Intent targetIntent = new Intent("mstar.tvsetting.ui.intent.action.RootActivity");
                    targetIntent.putExtra("task_tag", "input_source_changed");
                    targetIntent.putExtra("no_change_source", true);
                    targetIntent.addFlags(268435456);
                    InputSourceService.this.startActivity(targetIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    InputSourceService.this.handler.sendEmptyMessage(-102);
                }
            }
        }).start();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateSourceInputType(int inputSourceTypeIdex) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("enInputSourceType", Integer.valueOf(inputSourceTypeIdex));
        try {
            ret = (long) getContentResolver().update(Uri.parse("content://mstar.tv.usersetting/systemsetting"), vals, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
            this.handler.sendEmptyMessage(-102);
        }
        if (ret == -1) {
            Log.i(TAG, "update tbl_PicMode_Setting ignored");
        }
    }

    /* access modifiers changed from: private */
    public class ThreeDModeObserver extends ContentObserver {
        private int systemAutoTime = 0;

        public ThreeDModeObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange) {
            if (!InputSourceService.this.mIsSourceNeedSwitch) {
                InputSourceService.this.handler.sendEmptyMessage(-102);
                return;
            }
            InputSourceService.this.mIsSourceNeedSwitch = false;
            if (this.systemAutoTime > 0) {
                Settings.Global.putInt(InputSourceService.this.getContentResolver(), "auto_time", this.systemAutoTime);
            }
            new Thread(new Runnable() {
                /* class com.android.systemui.inputsource.InputSourceService.ThreeDModeObserver.AnonymousClass1 */

                public void run() {
                    if (InputSourceService.this.mInputSource == 28) {
                        try {
                            ThreeDModeObserver.this.systemAutoTime = Settings.Global.getInt(InputSourceService.this.getContentResolver(), "auto_time");
                        } catch (Settings.SettingNotFoundException e) {
                            ThreeDModeObserver.this.systemAutoTime = 0;
                        }
                        if (ThreeDModeObserver.this.systemAutoTime > 0) {
                            Settings.Global.putInt(InputSourceService.this.getContentResolver(), "auto_time", 0);
                        }
                    }
                    Log.i(InputSourceService.TAG, "ThreeDModeObserver, startActivity SOURCE_CHANGE intent: mInputSource = " + InputSourceService.this.mInputSource);
                    Intent intent = new Intent("com.mstar.android.intent.action.START_TV_PLAYER");
                    intent.putExtra("inputSrc", InputSourceService.this.mInputSource);
                    intent.putExtra("inputAntennaType", 0);
                    intent.addFlags(268435456);
                    InputSourceService.this.startActivity(intent);
                    InputSourceService.this.handler.sendEmptyMessage(-101);
                }
            }).start();
        }
    }

    /* access modifiers changed from: private */
    public static class MyHandler extends Handler {
        private WeakReference<Context> mContextRefs;

        MyHandler(Context context) {
            super(Looper.getMainLooper());
            this.mContextRefs = new WeakReference<>(context);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Context context = this.mContextRefs.get();
            if (context != null) {
                synchronized (context) {
                    switch (msg.what) {
                        case -102:
                            InputSourceSelectDialog.getInstance(context).dismiss();
                            break;
                        case -101:
                            Intent intent = new Intent("mstar.tvsetting.ui.intent.action.RootActivity");
                            intent.addFlags(16384);
                            intent.putExtra("task_tag", "input_source_changed");
                            intent.putExtra("inputAntennaType", 0);
                            intent.addFlags(268435456);
                            context.startActivity(intent);
                            sendEmptyMessage(-102);
                            break;
                    }
                }
            }
        }
    }
}
