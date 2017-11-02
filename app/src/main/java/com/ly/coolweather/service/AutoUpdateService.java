package com.ly.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.ly.coolweather.WeatherActivity;
import com.ly.coolweather.gson.WeatherBean;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    public AutoUpdateService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return  null;
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager  manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8*60*60*1000;//8小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime()+anHour;
        Intent i  = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气
     */
    private void updateBingPic() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if (weatherString!=null){
            //有缓存的时候直接解析天气数据
            WeatherBean weatherBean = JSON.parseObject(weatherString, WeatherBean.class);
            List<WeatherBean.HeWeatherBean> heWeatherBean0 = weatherBean.getHeWeather();
            WeatherBean.HeWeatherBean heWeatherBean = heWeatherBean0.get(0);
            WeatherBean.HeWeatherBean.BasicBean basicBean = heWeatherBean.getBasic();
            String weather_id = basicBean.getId();

            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weather_id + "&key=37c8f456b826469f8f581716e3ca04f0";

            OkGo.get(weatherUrl)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            WeatherBean weatherBean = JSON.parseObject(s, WeatherBean.class);
                            List<WeatherBean.HeWeatherBean> heWeatherBean = weatherBean.getHeWeather();
                            if (weatherBean.toString() != null && "ok".equals(heWeatherBean.get(0).getStatus())) {

                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                                editor.putString("weather", s);
                                editor.apply();
                            }

                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);

                        }
                    });
        }


    }

    /**
     * 更新必应图片
     */
    private void updateWeather() {
        String  bingPicUrl = "http://guolin.tech/api/bing_pic";
        OkGo.get(bingPicUrl)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        // Log.e(TAG, "onSuccess: 必应图片数据==="+s);

                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("bing_pic",s);
                        editor.apply();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });

    }
}
