package com.bestv.inputsource;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class InputSourceAdapter extends BaseAdapter {
    private boolean isSingleLine;
    private Context mContext;
    private int mCurrentSouce = 44;
    private int mDisabledColor;
    private int mEnabledColor;
    private LayoutInflater mInflater;
    private List<InputSourceObject> mInputSrcs;

    public InputSourceAdapter(Context context, List<InputSourceObject> inputSrcs, boolean singleLine) {
        this.mInputSrcs = inputSrcs;
        this.mContext = context;
        this.isSingleLine = singleLine;
        this.mInflater = LayoutInflater.from(context);
        Resources res = context.getResources();
        this.mDisabledColor = res.getColor(2131296273);
        this.mEnabledColor = res.getColor(2131296272);
    }

    public int getCount() {
        if (this.mInputSrcs != null) {
            return this.mInputSrcs.size();
        }
        return 0;
    }

    public InputSourceObject getItem(int i) {
        if (this.mInputSrcs == null || i >= this.mInputSrcs.size()) {
            return null;
        }
        return this.mInputSrcs.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int position, View convertView, ViewGroup viewgroup) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.mInflater.inflate(this.isSingleLine ? 2130968590 : 2130968588, viewgroup, false);
            holder.icon = (TextView) convertView.findViewById(2131230747);
            holder.title = (TextView) convertView.findViewById(2131230748);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        bindViewWithInfo(holder, getItem(position));
        return convertView;
    }

    public void setCurrentSource(int inputSource) {
        this.mCurrentSouce = inputSource;
    }

    private void bindViewWithInfo(ViewHolder holder, InputSourceObject item) {
        holder.icon.setTypeface(InputSourceUtil.getFont(this.mContext));
        holder.icon.setText(item.getIcon());
        holder.title.setText(item.getTitle());
        if (item.getInputSrc() == this.mCurrentSouce) {
            holder.icon.setTextColor(this.mEnabledColor);
            holder.title.setTextColor(this.mEnabledColor);
            return;
        }
        holder.icon.setTextColor(this.mDisabledColor);
        holder.title.setTextColor(this.mDisabledColor);
    }

    /* access modifiers changed from: package-private */
    public static class ViewHolder {
        TextView icon;
        TextView title;

        ViewHolder() {
        }
    }
}
