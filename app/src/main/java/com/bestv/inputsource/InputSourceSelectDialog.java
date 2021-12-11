package com.bestv.inputsource;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mstar.android.tv.TvCommonManager;
import java.util.ArrayList;
import java.util.List;
import com.bestv.*;

public class InputSourceSelectDialog extends Dialog {
    private static final int[] COMBO_FACTORY_MODE = {21, 21, 19, 22};
    private static final int[] COMBO_TCL_MODE = {20, 20, 20, 20};
    private static final String TAG = InputSourceSelectDialog.class.getSimpleName();
    private static CloseDlgBroadcastReceiver mBroadcastReceiver;
    private static InputSourceSelectDialog sDialog = null;
    private static final Boolean sLock = Boolean.TRUE;
    private View lastFocusInSourceView = null;
    private InputSourceService.IOnInputSourceChangeCallback mCallback;
    private Context mContext;
    private int mCurStartPos = 0;
    private List<InputSourceObject> mDisplaySourceList;
    private int mFactoryCombo = 0;
    private long mFactoryLastTime = 0;
    private View mFocusedView = null;
    private boolean mHasLauncher;
    private int mHiddenTclCombo = 0;
    private long mHiddenTclLastTime = 0;
    LayoutInflater mInflater;
    private int mInputSource;
    private RelativeLayout mLauncherView;
    private TextView mLeftShadowView;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        /* class com.android.systemui.inputsource.InputSourceSelectDialog.AnonymousClass1 */

        public void onClick(View v) {
            Log.i(InputSourceSelectDialog.TAG, "onClick().");
            switch (v.getId()) {
                case 2131230725:
                case 2131230726:
                case 2131230727:
                case 2131230728:
                case 2131230729:
                case 2131230730:
                case 2131230731:
                case 2131230732:
                    InputSourceSelectDialog.this.onSourceItemClick((InputSourceObject) v.getTag());
                    return;
                default:
                    Object tag = v.getTag();
                    if (tag instanceof Integer) {
                        InputSourceSelectDialog.this.onLaucherItemClick(((Integer) tag).intValue());
                        return;
                    }
                    return;
            }
        }
    };
    private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
        /* class com.android.systemui.inputsource.InputSourceSelectDialog.AnonymousClass3 */

        public void onFocusChange(View view, boolean focused) {
            if (focused) {
                InputSourceSelectDialog.this.mFocusedView = view;
            } else {
                InputSourceSelectDialog.this.mFocusedView = null;
            }
        }
    };
    private AdapterView.OnItemClickListener mOnSourceItemClickListener = new AdapterView.OnItemClickListener() {
        /* class com.android.systemui.inputsource.InputSourceSelectDialog.AnonymousClass2 */

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            InputSourceObject inputSource = InputSourceSelectDialog.this.mSourceAdapter.getItem(i);
            if (inputSource != null) {
                InputSourceSelectDialog.this.mInputSource = inputSource.getInputSrc();
                InputSourceSelectDialog.this.onSourceItemClick(inputSource);
            }
        }
    };
    private List<InputSourceObject> mOriginSourceList;
    private TextView mRightShadowView;
    private boolean mShowADTV;
    private InputSourceAdapter mSourceAdapter;
    private GridView mSourceGridView;
    private RelativeLayout mSourceView;

    private class CloseDlgBroadcastReceiver extends BroadcastReceiver {
        private CloseDlgBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction())) {
                InputSourceSelectDialog.this.dismiss();
            }
        }
    }

    public static InputSourceSelectDialog getInstance(Context context) {
        if (sDialog == null) {
            synchronized (InputSourceSelectDialog.class) {
                if (sDialog == null) {
                    sDialog = new InputSourceSelectDialog(context);
                }
            }
        }
        return sDialog;
    }

    public static void resetDialog() {
        sDialog = null;
    }

    public InputSourceSelectDialog(Context context) {
        super(context, 2131689514);
        getWindow().setType(2003);
        requestWindowFeature(1);
        setTitle(2131427558);
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
    }

    public void setInputSource(int inputSource) {
        this.mInputSource = inputSource;
    }

    public void setSourceList(List<InputSourceObject> sourceList) {
        this.mOriginSourceList = sourceList;
    }

    public void initUI(boolean hasLauncher, boolean inChildrenMode, boolean inParentMode, InputSourceService.IOnInputSourceChangeCallback callback) {
        View view;
        boolean z = true;
        this.mCallback = callback;
        this.mHasLauncher = hasLauncher;
        if (InputSourceUtil.isTCL(InputSourceUtil.getDeviceBrand(this.mContext)) && InputSourceUtil.hiddenInputSource() && Settings.System.getInt(this.mContext.getContentResolver(), "adtv_show", 0) != 1) {
            z = false;
        }
        this.mShowADTV = z;
        if (this.mHasLauncher) {
            setContentView(2130968577);
            this.mLeftShadowView = (TextView) findViewById(2131230736);
            this.mRightShadowView = (TextView) findViewById(2131230737);
            Typeface font = InputSourceUtil.getFont(com.bestv.TvSystemApp.getInstance().getApplicationContext());
            this.mLeftShadowView.setTypeface(font);
            this.mLeftShadowView.setText("");
            this.mRightShadowView.setTypeface(font);
            this.mRightShadowView.setText("");
            LauncherItemView focusLaucherView = initLauncherView(inChildrenMode, inParentMode);
            initSourceView(this.mCurStartPos);
            if (this.mInputSource == 34) {
                focusLaucherView.requestFocus();
            } else if (this.mSourceView != null && (view = this.mSourceView.getChildAt(0)) != null) {
                view.requestFocus();
            }
        } else {
            initSourceViewWithoutLauncher(this.mCurStartPos);
        }
    }

    private LauncherItemView initLauncherView(boolean inChildrenMode, boolean inParentMode) {
        this.mLauncherView = (RelativeLayout) findViewById(2131230735);
        Resources res = this.mContext.getResources();
        RelativeLayout.LayoutParams defaultItemParams = new RelativeLayout.LayoutParams(-2, -2);
        defaultItemParams.leftMargin = res.getDimensionPixelSize(2131558561);
        defaultItemParams.addRule(9);
        LauncherItemView defaultItem = LauncherItemView.buildLauncherItemView(this.mInflater, 2131230722, 2130837638, 2130837637, 2131427555, !inChildrenMode && !inParentMode, this.mOnClickListener);
        String name = Settings.System.getString(this.mContext.getContentResolver(), "standard_desktop_name");
        if (!TextUtils.isEmpty(name)) {
            defaultItem.setLauncherItemName(name);
        }
        this.mLauncherView.addView(defaultItem, defaultItemParams);
        defaultItem.requestFocus();
        RelativeLayout.LayoutParams childrenItemParams = new RelativeLayout.LayoutParams(-2, -2);
        childrenItemParams.leftMargin = res.getDimensionPixelSize(2131558578);
        childrenItemParams.addRule(1, 2131230722);
        LauncherItemView childrenItem = LauncherItemView.buildLauncherItemView(this.mInflater, 2131230723, 2130837636, 2130837635, 2131427556, inChildrenMode, this.mOnClickListener);
        this.mLauncherView.addView(childrenItem, childrenItemParams);
        RelativeLayout.LayoutParams parentItemParams = new RelativeLayout.LayoutParams(-2, -2);
        parentItemParams.leftMargin = res.getDimensionPixelSize(2131558578);
        parentItemParams.addRule(1, 2131230723);
        LauncherItemView parentItem = LauncherItemView.buildLauncherItemView(this.mInflater, 2131230724, 2130837640, 2130837639, 2131427557, inParentMode, this.mOnClickListener);
        String name2 = Settings.System.getString(this.mContext.getContentResolver(), "parents_desktop_name");
        if (!TextUtils.isEmpty(name2)) {
            parentItem.setLauncherItemName(name2);
        }
        this.mLauncherView.addView(parentItem, parentItemParams);
        if (inChildrenMode) {
            return childrenItem;
        }
        return inParentMode ? parentItem : defaultItem;
    }

    private void initSourceView(int pos) {
        this.mDisplaySourceList = new ArrayList(this.mOriginSourceList);
        if (!this.mShowADTV) {
            for (int i = this.mDisplaySourceList.size() - 1; i >= 0; i--) {
                InputSourceObject source = this.mDisplaySourceList.get(i);
                if (source.getInputSrc() == 1 || source.getInputSrc() == 28) {
                    this.mDisplaySourceList.remove(i);
                }
            }
        }
        List<InputSourceObject> sourceList = new ArrayList<>();
        if (this.mDisplaySourceList != null) {
            int i2 = pos;
            while (i2 < pos + 5 && i2 < this.mDisplaySourceList.size()) {
                sourceList.add(this.mDisplaySourceList.get(i2));
                i2++;
            }
        }
        if (sourceList.isEmpty()) {
            Log.i(TAG, "initSourceView(), sourceList.isEmpty().");
            return;
        }
        View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
            /* class com.android.systemui.inputsource.InputSourceSelectDialog.AnonymousClass4 */

            public void onFocusChange(View view, boolean focused) {
                if (focused) {
                    InputSourceSelectDialog.this.lastFocusInSourceView = view;
                }
            }
        };
        this.mSourceView = (RelativeLayout) findViewById(2131230734);
        int width = this.mContext.getResources().getDimensionPixelSize(2131558580);
        RelativeLayout.LayoutParams firstItemParams = new RelativeLayout.LayoutParams(width, width);
        firstItemParams.addRule(9);
        SourceItemView firstSourceItem = SourceItemView.buildSourceItemView(this.mInflater, sourceList.get(0), this.mInputSource == sourceList.get(0).getInputSrc(), this.mOnClickListener);
        firstSourceItem.setOnFocusChangeListener(focusListener);
        firstSourceItem.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.mSourceView.addView(firstSourceItem, firstItemParams);
        int leftId = firstSourceItem.getId();
        for (int i3 = 1; i3 < sourceList.size(); i3++) {
            InputSourceObject obj = sourceList.get(i3);
            SourceItemView item = SourceItemView.buildSourceItemView(this.mInflater, obj, this.mInputSource == obj.getInputSrc(), this.mOnClickListener);
            item.setOnFocusChangeListener(focusListener);
            RelativeLayout.LayoutParams remainedItemParams = new RelativeLayout.LayoutParams(width, width);
            remainedItemParams.leftMargin = 7;
            remainedItemParams.addRule(1, leftId);
            this.mSourceView.addView(item, remainedItemParams);
            leftId = item.getId();
        }
    }

    private void updateSourceViewTcl() {
        Log.i(TAG, "updateSourceViewTcl()");
        this.mShowADTV = true;
        Settings.System.putInt(this.mContext.getContentResolver(), "adtv_show", 1);
        if (this.mHasLauncher) {
            this.mSourceView.removeAllViews();
            initSourceView(this.mCurStartPos);
            this.mSourceView.invalidate();
            if ((this.mCurStartPos + this.mSourceView.getChildCount()) - 1 < this.mDisplaySourceList.size() - 1) {
                this.mRightShadowView.setVisibility(0);
            } else {
                this.mRightShadowView.setVisibility(8);
            }
        } else {
            initSourceViewWithoutLauncher(this.mCurStartPos);
        }
    }

    private void initSourceViewWithoutLauncher(int pos) {
        this.mDisplaySourceList = new ArrayList(this.mOriginSourceList);
        if (!this.mShowADTV) {
            for (int i = this.mDisplaySourceList.size() - 1; i >= 0; i--) {
                InputSourceObject source = this.mDisplaySourceList.get(i);
                if (source.getInputSrc() == 1 || source.getInputSrc() == 28) {
                    this.mDisplaySourceList.remove(i);
                }
            }
        }
        List<InputSourceObject> sourceList = this.mDisplaySourceList;
        boolean mutliLine = InputSourceUtil.getPortInfo(this.mContext).getMultiLine();
        if (sourceList.size() <= 5) {
            setContentView(2130968578);
            this.mSourceAdapter = new InputSourceAdapter(this.mContext, sourceList, true);
        } else if (mutliLine) {
            setContentView(2130968576);
            this.mSourceAdapter = new InputSourceAdapter(this.mContext, sourceList, false);
        } else {
            setContentView(2130968579);
            this.mSourceAdapter = new InputSourceAdapter(this.mContext, sourceList, true);
        }
        this.mSourceGridView = (GridView) findViewById(2131230734);
        this.mSourceGridView.setAdapter((ListAdapter) this.mSourceAdapter);
        this.mSourceGridView.setOnItemClickListener(this.mOnSourceItemClickListener);
        this.mInputSource = Settings.System.getInt(this.mContext.getContentResolver(), "input_source", 44);
        if (this.mInputSource != 44) {
            int i2 = 0;
            while (true) {
                if (i2 >= sourceList.size()) {
                    break;
                } else if (this.mInputSource == sourceList.get(i2).getInputSrc()) {
                    this.mSourceAdapter.setCurrentSource(this.mInputSource);
                    this.mSourceGridView.setSelection(i2);
                    break;
                } else {
                    i2++;
                }
            }
        }
        Log.i(TAG, "mInputSource=" + this.mInputSource);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onSourceItemClick(InputSourceObject inputSource) {
        if (inputSource == null) {
            Log.i(TAG, "onItemClick(), inputSource == null");
            return;
        }
        this.mInputSource = inputSource.getInputSrc();
        TvCommonManager tvCommonmanager = TvCommonManager.getInstance();
        ContentValues values = new ContentValues();
        if (tvCommonmanager != null) {
            values.put("informt", Integer.valueOf(tvCommonmanager.getCurrentTvInputSource()));
        }
        values.put("to_inform", Integer.valueOf(this.mInputSource));
        if (this.mCallback != null) {
            this.mCallback.onSourceChange(this.mInputSource);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onLaucherItemClick(int itemId) {
        if (Settings.System.getInt(this.mContext.getContentResolver(), "auth_result", 1) != 1) {
            Toast.makeText(this.mContext.getApplicationContext(), "auth failed.", 1).show();
            return;
        }
        switch (itemId) {
            case 2131230722:
                Intent defaultItemIntent = new Intent();
                defaultItemIntent.setClassName("com.bestv.ott", "com.bestv.ott.home.HomeActivity");
                defaultItemIntent.putExtra("desktop_mode", 1);
                defaultItemIntent.addFlags(268435456);
                this.mContext.startActivity(defaultItemIntent);
                break;
            case 2131230723:
                Intent childrenItemIntent = new Intent();
                childrenItemIntent.setClassName("tv.fun.children", "tv.fun.children.ui.ChildrenActivity");
                childrenItemIntent.addFlags(268435456);
                this.mContext.startActivity(childrenItemIntent);
                Intent intent = new Intent("com.bestv.ott.intent.action.MODE_LAUNCHER");
                intent.putExtra("packageName", "tv.fun.children");
                this.mContext.sendBroadcast(intent);
                break;
            case 2131230724:
                Intent parentItemIntent = new Intent();
                parentItemIntent.setClassName("com.bestv.ott", "com.bestv.ott.home.HomeActivity");
                parentItemIntent.putExtra("desktop_mode", 2);
                parentItemIntent.addFlags(268435456);
                this.mContext.startActivity(parentItemIntent);
                break;
        }
        dismiss();
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.i(TAG, "onAttachedToWindow()");
        synchronized (sLock) {
            if (mBroadcastReceiver == null) {
                CloseDlgBroadcastReceiver receiver = new CloseDlgBroadcastReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
                TvSystemApp.getInstance().registerReceiver(receiver, intentFilter);
                mBroadcastReceiver = receiver;
            }
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(TAG, "onDetachedFromWindow()");
        synchronized (sLock) {
            if (mBroadcastReceiver != null) {
                TvSystemApp.getInstance().unregisterReceiver(mBroadcastReceiver);
                mBroadcastReceiver = null;
            }
        }
    }

    public void dismiss() {
        super.dismiss();
        if (this.mCallback != null) {
            this.mCallback.onDialogDismiss();
        }
        sDialog = null;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getAction() == 0) {
            if (this.mHasLauncher) {
                if (this.mFactoryCombo == 0) {
                    Log.i("yinwei", "mFactoryCombo == 0");
                    if (focusedOnFirstSource()) {
                        handleFactoryCombo(event.getKeyCode());
                    }
                } else {
                    handleFactoryCombo(event.getKeyCode());
                }
                if (this.mHiddenTclCombo == 0) {
                    Log.i("yinwei", "mHiddenTclCombo == 0");
                    if (focusedOnFirstSource()) {
                        handleHiddenTclCombo(event.getKeyCode());
                    }
                } else {
                    handleHiddenTclCombo(event.getKeyCode());
                }
            } else {
                if (!(this.mSourceGridView == null || this.mSourceAdapter == null)) {
                    if (this.mFactoryCombo != 0) {
                        handleFactoryCombo(event.getKeyCode());
                    } else if (this.mSourceGridView.getSelectedItemPosition() == 0) {
                        handleFactoryCombo(event.getKeyCode());
                    }
                    if (this.mHiddenTclCombo == 0) {
                        Log.i("yinwei", "mHiddenTclCombo == 0");
                        if (this.mSourceGridView.getSelectedItemPosition() == 0) {
                            handleHiddenTclCombo(event.getKeyCode());
                        }
                    } else {
                        handleHiddenTclCombo(event.getKeyCode());
                    }
                }
                return super.dispatchKeyEvent(event);
            }
        }
        if (event.getAction() == 0) {
            int curFocus = findFocusInSourceView();
            Log.i(TAG, "dispatchKeyEvent(), keyCode = " + event.getKeyCode() + ", curFocus = " + curFocus + ", lastFocusInSourceView = " + this.lastFocusInSourceView);
            switch (keyCode) {
                case 20:
                    if (curFocus == -1) {
                        if (this.lastFocusInSourceView != null) {
                            this.lastFocusInSourceView.requestFocus();
                        } else {
                            this.mSourceView.getChildAt(0).requestFocus();
                        }
                        this.mSourceView.playSoundEffect(2);
                        break;
                    }
                    break;
                case 21:
                    if (curFocus == -1 && this.mLauncherView.getFocusedChild().getId() == this.mLauncherView.getChildAt(0).getId()) {
                        return true;
                    }
                    if (curFocus == 2) {
                        boolean withFixedFocus = updateSourceViewWithFixedFocus1(true);
                        if (this.mCurStartPos > 0) {
                            this.mLeftShadowView.setVisibility(0);
                        } else {
                            this.mLeftShadowView.setVisibility(8);
                        }
                        if ((this.mCurStartPos + this.mSourceView.getChildCount()) - 1 < this.mDisplaySourceList.size() - 1) {
                            this.mRightShadowView.setVisibility(0);
                        } else {
                            this.mRightShadowView.setVisibility(8);
                        }
                        if (withFixedFocus) {
                            this.mSourceView.playSoundEffect(2);
                            return true;
                        }
                    }
                    break;
                case 22:
                    if (curFocus == -1 && this.mLauncherView.getFocusedChild().getId() == this.mLauncherView.getChildAt(this.mLauncherView.getChildCount() - 1).getId()) {
                        return true;
                    }
                    if (curFocus == 2) {
                        boolean withFixedFocus2 = updateSourceViewWithFixedFocus1(false);
                        if (this.mCurStartPos > 0) {
                            this.mLeftShadowView.setVisibility(0);
                        } else {
                            this.mLeftShadowView.setVisibility(8);
                        }
                        if ((this.mCurStartPos + this.mSourceView.getChildCount()) - 1 < this.mDisplaySourceList.size() - 1) {
                            this.mRightShadowView.setVisibility(0);
                        } else {
                            this.mRightShadowView.setVisibility(8);
                        }
                        if (withFixedFocus2) {
                            this.mSourceView.playSoundEffect(2);
                            return true;
                        }
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private int findFocusInSourceView() {
        int count = this.mSourceView.getChildCount();
        for (int i = 0; i < count; i++) {
            if (this.mSourceView.getChildAt(i).isFocused()) {
                return i;
            }
        }
        return -1;
    }

    private boolean updateSourceViewWithFixedFocus1(boolean left) {
        int count = this.mSourceView.getChildCount();
        Log.i(TAG, "left = " + left + ", count=" + count);
        if (left) {
            if (this.mCurStartPos > 0) {
                this.mCurStartPos--;
                this.mSourceView.removeAllViews();
                initSourceView(this.mCurStartPos);
                this.mSourceView.getChildAt(2).requestFocus();
                return true;
            }
        } else if ((this.mCurStartPos + count) - 1 < this.mDisplaySourceList.size() - 1) {
            this.mCurStartPos++;
            this.mSourceView.removeAllViews();
            initSourceView(this.mCurStartPos);
            this.mSourceView.getChildAt(2).requestFocus();
            this.mSourceView.invalidate();
            return true;
        }
        return false;
    }

    private boolean focusedOnFirstSource() {
        View view;
        if (this.mSourceView == null || (view = this.mSourceView.findFocus()) == null || view != this.mFocusedView) {
            return false;
        }
        return true;
    }

    public static List<InputSourceObject> getInputSouceObjectList(Context context, DisplayPortInfo portInfo) {
        List<InputSourceObject> sourceObjects = new ArrayList<>();
        String displayPortStr = portInfo.getDisplayPort();
        String realPortStr = portInfo.getRealPort();
        if (!TextUtils.isEmpty(displayPortStr) && !TextUtils.isEmpty(realPortStr)) {
            String[] displayArray = displayPortStr.split("-");
            String[] realArray = realPortStr.split("-");
            if (displayArray.length == realArray.length) {
                for (int i = 0; i < displayArray.length; i++) {
                    InputSourceObject object = getInputSourceObject(context, displayArray[i], realArray[i], portInfo.getAvPortType(), portInfo.getYuvPortType());
                    if (object != null) {
                        sourceObjects.add(object);
                    }
                }
            }
        }
        return sourceObjects;
    }

    private static InputSourceObject getInputSourceObject(Context context, String displayPort, String realPort, int avPortType, int yuvPortType) {
        Resources res = context.getResources();
        int realInputSourceIndex = getInputSourceIndex(realPort);
        if ("atv".equalsIgnoreCase(displayPort)) {
            return new InputSourceObject(realInputSourceIndex, "", res.getString(2131427561), 2131230725);
        }
        if ("dtv".equalsIgnoreCase(displayPort)) {
            return new InputSourceObject(realInputSourceIndex, "", res.getString(2131427560), 2131230726);
        }
        if ("av".equalsIgnoreCase(displayPort)) {
            return new InputSourceObject(realInputSourceIndex, avPortType == 1 ? "" : "", res.getString(2131427562), 2131230727);
        } else if ("av1".equalsIgnoreCase(displayPort)) {
            return new InputSourceObject(realInputSourceIndex, avPortType == 1 ? "" : "", res.getString(2131427562) + 1, 2131230727);
        } else if ("av2".equalsIgnoreCase(displayPort)) {
            return new InputSourceObject(realInputSourceIndex, avPortType == 1 ? "" : "", res.getString(2131427562) + 2, 2131230727);
        } else if ("ypbpr".equalsIgnoreCase(displayPort)) {
            return new InputSourceObject(realInputSourceIndex, yuvPortType == 1 ? "" : "", res.getString(2131427563), 2131230728);
        } else if ("ypbpr1".equalsIgnoreCase(displayPort)) {
            return new InputSourceObject(realInputSourceIndex, yuvPortType == 1 ? "" : "", res.getString(2131427563) + 1, 2131230728);
        } else if ("ypbpr2".equalsIgnoreCase(displayPort)) {
            return new InputSourceObject(realInputSourceIndex, yuvPortType == 1 ? "" : "", res.getString(2131427563) + 2, 2131230728);
        } else if ("hdmi1".equalsIgnoreCase(displayPort)) {
            return new InputSourceObject(realInputSourceIndex, "", res.getString(2131427564) + 1, 2131230729);
        } else {
            if ("hdmi2".equalsIgnoreCase(displayPort)) {
                return new InputSourceObject(realInputSourceIndex, "", res.getString(2131427564) + 2, 2131230730);
            }
            if ("hdmi3".equalsIgnoreCase(displayPort)) {
                return new InputSourceObject(realInputSourceIndex, "", res.getString(2131427564) + 3, 2131230731);
            }
            if ("vga".equalsIgnoreCase(displayPort)) {
                return new InputSourceObject(realInputSourceIndex, "", res.getString(2131427565), 2131230732);
            }
            return null;
        }
    }

    private static int getInputSourceIndex(String portStr) {
        if ("atv".equalsIgnoreCase(portStr)) {
            return 1;
        }
        if ("dtv".equalsIgnoreCase(portStr)) {
            return 28;
        }
        if ("av".equalsIgnoreCase(portStr) || "av1".equalsIgnoreCase(portStr)) {
            return 2;
        }
        if ("av2".equalsIgnoreCase(portStr)) {
            return 3;
        }
        if ("ypbpr".equalsIgnoreCase(portStr)) {
            return 16;
        }
        if ("ypbpr1".equalsIgnoreCase(portStr)) {
            return 16;
        }
        if ("ypbpr2".equalsIgnoreCase(portStr)) {
            return 17;
        }
        if ("hdmi1".equalsIgnoreCase(portStr)) {
            return 23;
        }
        if ("hdmi2".equalsIgnoreCase(portStr)) {
            return 24;
        }
        if ("hdmi3".equalsIgnoreCase(portStr)) {
            return 25;
        }
        if ("vga".equalsIgnoreCase(portStr)) {
            return 0;
        }
        return -1;
    }

    private void handleFactoryCombo(int keyCode) {
        long now = System.currentTimeMillis();
        if (now - this.mFactoryLastTime >= 1000) {
            this.mFactoryCombo = 0;
        }
        if (keyCode == COMBO_FACTORY_MODE[this.mFactoryCombo]) {
            this.mFactoryCombo++;
            this.mFactoryLastTime = now;
            if (this.mFactoryCombo == COMBO_FACTORY_MODE.length) {
                this.mFactoryCombo = 0;
                Intent intent = new Intent();
                intent.setClassName("mstar.factorymenu.ui", "mstar.tvsetting.factory.ui.designmenu.DesignMenuActivity");
                intent.addFlags(268435456);
                this.mContext.startActivity(intent);
                dismiss();
                return;
            }
            return;
        }
        this.mFactoryCombo = 0;
    }

    private void handleHiddenTclCombo(int keyCode) {
        if (this.mShowADTV) {
            Log.i(TAG, "handleFactoryCombo, mShowADTV == true.");
            return;
        }
        Log.i("yinwei", "keyCode = " + keyCode + ", mHiddenTclCombo = " + this.mHiddenTclCombo);
        long now = System.currentTimeMillis();
        if (now - this.mHiddenTclLastTime >= 1000) {
            this.mHiddenTclCombo = 0;
        }
        if (keyCode == COMBO_TCL_MODE[this.mHiddenTclCombo]) {
            this.mHiddenTclCombo++;
            this.mHiddenTclLastTime = now;
            if (this.mHiddenTclCombo == COMBO_TCL_MODE.length) {
                this.mHiddenTclCombo = 0;
                updateSourceViewTcl();
                return;
            }
            return;
        }
        this.mHiddenTclCombo = 0;
    }
}
