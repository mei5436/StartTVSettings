package com.bestv.inputsource;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestv.ott.R;


public class SourceItemView extends LinearLayout {
    private boolean curActived;
    private TextView mIconView;
    private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
        /* class com.android.systemui.inputsource.SourceItemView.AnonymousClass1 */

        public void onFocusChange(View v, boolean hasFocus) {
            if (SourceItemView.this.curActived) {
                Log.i(SourceItemView.class.getSimpleName(), "onFocusChange, curActived = true");
            } else if (hasFocus) {
                SourceItemView.this.mIconView.setTextColor(SourceItemView.this.textColorFocused);
                SourceItemView.this.mTextView.setTextColor(SourceItemView.this.textColorFocused);
            } else {
                SourceItemView.this.mIconView.setTextColor(SourceItemView.this.textColorNormal);
                SourceItemView.this.mTextView.setTextColor(SourceItemView.this.textColorNormal);
            }
        }
    };
    private TextView mStateView;
    private TextView mTextView;
    private int textColorCurActived;
    private int textColorFocused;
    private int textColorNormal;

    public SourceItemView(Context context) {
        super(context, null, 0);
    }

    public SourceItemView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public SourceItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFocusable(true);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        Resources res = getResources();
        this.textColorCurActived = res.getColor(R.color.input_source_enabled_color);
        this.textColorFocused = res.getColor(R.color.app_ad_time_text_color);
        this.textColorNormal = res.getColor(R.color.cardview_dark_background);
        this.mIconView = (TextView) findViewById(R.id.icon);
        this.mTextView = (TextView) findViewById(R.id.title);
        this.mStateView = (TextView) findViewById(R.id.state);
        setOnFocusChangeListener(this.mOnFocusChangeListener);
    }

    public static SourceItemView buildSourceItemView(LayoutInflater inflater, InputSourceObject object, boolean curActived2, OnClickListener l) {
        SourceItemView itemView = (SourceItemView) inflater.inflate(R.layout.item_inputsource_singleline, (ViewGroup) null);
        itemView.init(object, curActived2);
        itemView.setOnClickListener(l);
        return itemView;
    }

    public void init(InputSourceObject object, boolean curActived2) {
        this.curActived = curActived2;
        this.mIconView.setTypeface(InputSourceUtil.getFont(getContext()));
        this.mIconView.setText(object.getIcon());
        this.mTextView.setText(object.getTitle());
        setId(object.getId());
        setTag(object);
        if (curActived2) {
            this.mIconView.setTextColor(this.textColorCurActived);
            this.mTextView.setTextColor(this.textColorCurActived);
            return;
        }
        this.mIconView.setTextColor(this.textColorNormal);
        this.mTextView.setTextColor(this.textColorNormal);
    }
}
