package com.bestv.startsettings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
//                    try {
//                        context.startActivity(new Intent("android.settings.SETTINGS"));
//                    } catch (ActivityNotFoundException e) {
//                        Toast.makeText(context, "打开设置失败", 1).show();
//                    }
                    return;
                case 2:
                    startActivity(context, "tv.fun.settings.action.picture", "from_source", 1);
                    return;

                case 3:
                    try {
                        Intent intent2 = new Intent("com.android.systemui.action.inputsource");
                        intent2.setComponent(new ComponentName("com.android.systemui", "com.android.systemui.inputsource.InputSourceService"));
                        intent2.putExtra("sendFrom", 2);
                        context.startService(intent2);
                        return;
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        Toast.makeText(context, "没有找到应用!", Toast.LENGTH_LONG).show();
                        return;
                    }
                case 4:
                    try {
                        startApplication(new ComponentName("com.android.settings", "com.android.settings.Settings"), context);
                        return;
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        Toast.makeText(context, "没有找到应用!", Toast.LENGTH_LONG).show();
                        return;
                    }
                case 5:
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
                default:
                    return;
            }
        }


    }

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
            ActivityInfo activityInfo = context.getPackageManager().getActivityInfo(componentName, 0);

        } catch (PackageManager.NameNotFoundException e) {
            Log.d("SCH20150821", "startApplication the packagename:" + componentName.getPackageName() + " is error");
            e.printStackTrace();
        }
        Intent localIntent = new Intent("android.intent.action.MAIN");
        localIntent.addCategory("android.intent.category.LAUNCHER");
        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setComponent(componentName);
        context.startActivity(localIntent);
    }
}
