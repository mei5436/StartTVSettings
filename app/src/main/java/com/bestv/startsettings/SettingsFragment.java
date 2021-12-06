package com.bestv.startsettings;


import android.content.ComponentName;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.bestv.ott.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "SettingsFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case "network_setting":
                IntentUtil.startActivity(getContext(), "android.settings.WIFI_SETTINGS", "from_source", 1);
                return true;
            case "general_setting":
                IntentUtil.startActivity(getContext(), "android.settings.SETTINGS", "from_source", 1);
                return true;
            case "picture_sound":
                IntentUtil.startActivity(getContext(), "tv.fun.settings.action.picture", "from_source", 1);
                return true;
            case "system_settings":
                IntentUtil.startApplication(new ComponentName("com.android.settings", "com.android.settings.Settings"), getContext());
                return true;
            case "signal_source":
                IntentUtil.startInputSourceSelectDialog(getContext());
                return true;
            case "back_home":
                IntentUtil.startHome(getContext());
                return true;
            default:
                Log.d(TAG, "onPreferenceTreeClick: " + preference);
                break;
        }
        return super.onPreferenceTreeClick(preference);
    }
}