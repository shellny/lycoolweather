package com.ly.coolweather;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        String weather_str = prefs.getString("weather",null);
//        if (weather_str!=null){
//            Intent intent = new Intent(this,WeatherActivity.class);
//            startActivity(intent);
//            finish();
//        }
    }
}
