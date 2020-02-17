package com.example.mainapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.HashMap;

public class Splash extends AppCompatActivity {
    private int SLEEP_TIMER = 3;
    SharedPrefManager sharedPrefManager;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext = this;
        sharedPrefManager = new SharedPrefManager(this);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        LogoLauncher logoLauncher = new LogoLauncher();
        logoLauncher.start();
    }
    private class LogoLauncher extends Thread {
        public void run(){
            try{
                sleep(1000 * SLEEP_TIMER);
            }catch(InterruptedException e){
                e.printStackTrace();
            }

            Boolean login = sharedPrefManager.getSPSudahLogin();
            if(login==true){
                startActivity(new Intent(mContext, MainActivity.class));
            }else{
                startActivity(new Intent(mContext, LoginActivity.class));
            }
            Splash.this.finish();
        }
    }
}
