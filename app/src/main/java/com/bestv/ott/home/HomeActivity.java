package com.bestv.ott.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;



import com.bestv.ott.R;

public class HomeActivity extends Activity {

    private AppBarConfiguration appBarConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Intent intent = new Intent("tv.fun.intent.action.ACTION_HOME");
            intent.putExtra("isLongPressed", 2);
            intent.setClassName("com.bestv.ott", "com.bestv.ott.HomeTransferService");
            intent.putExtra("sendFrom", 1);
            startService(intent);

            finish();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

}