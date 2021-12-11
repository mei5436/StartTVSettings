package com.bestv.inputsource;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestv.ott.R;

public class LauncherItemView extends LinearLayout {
    private boolean curActived;
    private int focusedIconId;
    private ImageView mIconView;
    private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
        /* class com.android.systemui.inputsource.LauncherItemView.AnonymousClass1 */

        public void onFocusChange(View v, boolean hasFocus) {
            if (LauncherItemView.this.curActived) {
                Log.i(LauncherItemView.class.getSimpleName(), "onFocusChange, curActived = true");
            } else if (hasFocus) {
                LauncherItemView.this.mIconView.setImageResource(LauncherItemView.this.focusedIconId);
                LauncherItemView.this.mTextView.setTextColor(LauncherItemView.this.textColorFocused);
            } else {
                LauncherItemView.this.mIconView.setImageResource(LauncherItemView.this.normalIconId);
                LauncherItemView.this.mTextView.setTextColor(LauncherItemView.this.textColorNormal);
            }
        }
    };
    private TextView mTextTipsView;
    private TextView mTextView;
    private int normalIconId;
    private int textColorCurActived;
    private int textColorFocused;
    private int textColorNormal;

    public LauncherItemView(Context context) {
        super(context, null, 0);
    }

    public LauncherItemView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public LauncherItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFocusable(true);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        Resources res = getResources();
        this.textColorCurActived = res.getColor(R.color.cardview_dark_background);
        this.textColorFocused = res.getColor(R.color.input_source_state_color);
        this.textColorNormal = res.getColor(R.color.app_ad_time_text_color);
        this.mIconView = (ImageView) findViewById(R.id.icon);
        this.mIconView.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.mTextView = (TextView) findViewById(R.id.title);
        this.mTextTipsView = (TextView) findViewById(R.id.title_tips);
    }

    private void init(int id, int focusedIconId2, int normalIconId2, int textId, boolean curActived2, OnClickListener listener) {
        this.focusedIconId = focusedIconId2;
        this.normalIconId = normalIconId2;
        this.curActived = curActived2;
        setId(id);
        this.mTextView.setText(textId);
        this.mIconView.setTag(Integer.valueOf(id));
        this.mIconView.setOnClickListener(listener);
        if (curActived2) {
            this.mIconView.setImageResource(focusedIconId2);
            this.mTextView.setTextColor(this.textColorCurActived);
            this.mTextTipsView.setTextColor(this.textColorCurActived);
            this.mTextTipsView.setVisibility(VISIBLE);
            return;
        }
        this.mIconView.setImageResource(normalIconId2);
        this.mTextView.setTextColor(this.textColorNormal);
        this.mTextTipsView.setVisibility(View.GONE);
    }

    public static LauncherItemView buildLauncherItemView(LayoutInflater inflater, int id, int focusedIconId2, int normalIconId2, int textId, boolean curActived2, OnClickListener listener) {
        LauncherItemView itemView = (LauncherItemView) inflater.inflate(R.layout.item_launcher_singleline, (ViewGroup) null);
        itemView.init(id, focusedIconId2, normalIconId2, textId, curActived2, listener);
        return itemView;
    }

    public void setLauncherItemName(String name) {
        this.mTextView.setText(name);
    }
}
