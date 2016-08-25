package com.ziv.wificlient;

import android.app.Activity;
import android.os.Bundle;

import com.sensetime.utils.CrashHandler;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CrashHandler.getInstance().init(getApplicationContext());

    }
}