package com.bestv.startsettings;

import static com.bestv.startsettings.IntentUtil.*;

import android.content.ComponentName;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bestv.ott.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private static final int[] strs = {R.string.home_network_setting,
            R.string.home_general_setting, R.string.home_picture_sound,
            R.string.home_signal_source, R.string.system_settings};
    private Context context;

    private MyAdapter() {
        super();
    }

    public MyAdapter(Context context) {
        this();
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_item, parent, false);
        MyViewHolder vh = null;
        if (view.getTag() instanceof MyViewHolder) {
            vh = (MyViewHolder) view.getTag();
        } else {
            vh = new MyViewHolder(view);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.text.setText(strs[position]);
    }

    @Override
    public int getItemCount() {
        return strs.length;
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView text;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textView);
            text.setOnClickListener(this);
        }


        public void onClick(View view) {
            Context context = view.getContext();
            switch (getLayoutPosition()) {
                case 0:
                    startActivity(context, "android.settings.WIFI_SETTINGS", "from_source", 1);
                    return;
                case 1:
                    startActivity(context, "android.settings.SETTINGS", "from_source", 1);
                    return;
                case 2:
                    startActivity(context, "tv.fun.settings.action.picture", "from_source", 1);
                    return;
                case 3:
                    startInputSourceSelectDialog(context);
                    return;
                case 4:
                    startApplication(new ComponentName("com.android.settings", "com.android.settings.Settings"), context);
                case 5:
                    startHome(context);
                default:
                    return;
            }
        }


    }


}
