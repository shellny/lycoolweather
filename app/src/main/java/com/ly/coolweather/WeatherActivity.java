package com.ly.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.ly.coolweather.gson.WeatherBean;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {


    @BindView(R.id.weather_layout)
    ScrollView weatherLayout;
    @BindView(R.id.title_city)
    TextView titleCity;
    @BindView(R.id.title_update_time)
    TextView titleUpdateTime;
    @BindView(R.id.degree_text)
    TextView degreeText;
    @BindView(R.id.weather_info_text)
    TextView weatherInfoText;
    @BindView(R.id.forecast_layout)
    LinearLayout forecastLayout;
    @BindView(R.id.aqi_text)
    TextView aqiText;
    @BindView(R.id.pm25_text)
    TextView pm25Text;
    @BindView(R.id.comfort_text)
    TextView comfortText;
    @BindView(R.id.car_wash_text)
    TextView carWashText;
    @BindView(R.id.sport_text)
    TextView sportText;

    @BindView(R.id.bing_pic_img)
    ImageView iv_bg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * 为了使背景图和状态栏融为一起
         * 可以使用DesignSupport库
         * 也可以使用如下方法
         *
         * Android5.0及以上才支持，所以首先做一个系统版本的判断
         */
        if (Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);



//
//        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
//        titleCity = (TextView) findViewById(R.id.title_city);
//        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
//        degreeText = (TextView) findViewById(R.id.degree_text);
//        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
//        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
//        aqiText = (TextView) findViewById(R.id.aqi_text);
//        pm25Text = (TextView) findViewById(R.id.pm25_text);
//        comfortText = (TextView) findViewById(R.id.comfort_text);
//        carWashText = (TextView) findViewById(R.id.car_wash_text);
//        sportText = (TextView) findViewById(R.id.sport_text);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);


        String bingPic = prefs.getString("bing_pic",null);
        if (bingPic!=null){
            Glide.with(this).load(bingPic).into(iv_bg);
        }else{
            loadBingPic();
        }

        if (weatherString != null) {
            //有缓存时直接解析数据
            //showWeatherInfo(weatherString);

            String weather_id = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weather_id);
        } else {
            //没有缓存时去服务器查询数据
            String weather_id = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weather_id);
        }
    }

    private static final String TAG = "WeatherActivity";


    private void loadBingPic() {

        String  bingPicUrl = "http://guolin.tech/api/bing_pic";
        OkGo.get(bingPicUrl)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                       // Log.e(TAG, "onSuccess: 必应图片数据==="+s);

                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("bing_pic",s);
                        editor.apply();
                        Glide.with(WeatherActivity.this).load(s).into(iv_bg);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });


    }

    private void requestWeather(String weather_id) {
        String url = "http://guolin.tech/api/weather?cityid=CN101190401&key=37c8f456b826469f8f581716e3ca04f0";
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weather_id + "&key=37c8f456b826469f8f581716e3ca04f0";
        String key = "37c8f456b826469f8f581716e3ca04f0";
        OkGo.get(weatherUrl)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                        WeatherBean weatherBean = JSON.parseObject(s, WeatherBean.class);
                        List<WeatherBean.HeWeatherBean> heWeatherBean = weatherBean.getHeWeather();
                        if (weatherBean.toString() != null && "ok".equals(heWeatherBean.get(0).getStatus())) {

                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", s);
                            editor.apply();
                            showWeatherInfo(s);


                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败1", Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败2", Toast.LENGTH_SHORT).show();
                    }
                });

        loadBingPic();
    }

    /**
     * 处理并展示实体类中的数据
     *
     * @param s
     */
    private void showWeatherInfo(String s) {

        WeatherBean weatherBean = JSON.parseObject(s, WeatherBean.class);
        List<WeatherBean.HeWeatherBean> heWeatherBean0 = weatherBean.getHeWeather();
        WeatherBean.HeWeatherBean heWeatherBean = heWeatherBean0.get(0);


        WeatherBean.HeWeatherBean.BasicBean basicBean = heWeatherBean.getBasic();
        WeatherBean.HeWeatherBean.NowBean nowBean = heWeatherBean.getNow();

        String cityName = basicBean.getCity();
        String updateTime = basicBean.getUpdate().getLoc();
        String degree = nowBean.getTmp() + "℃";
        String weatherInfo = nowBean.getCond().getTxt();

        List<WeatherBean.HeWeatherBean.DailyForecastBean> forecastList = heWeatherBean.getDaily_forecast();


        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();

        for (WeatherBean.HeWeatherBean.DailyForecastBean dailyForecastBean : forecastList) {

            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);

            dateText.setText(dailyForecastBean.getDate());
            infoText.setText(dailyForecastBean.getCond().getTxt_d());
            maxText.setText(dailyForecastBean.getTmp().getMax());
            minText.setText(dailyForecastBean.getTmp().getMin());
            forecastLayout.addView(view);
        }

        WeatherBean.HeWeatherBean.AqiBean aqiBean = heWeatherBean.getAqi();

        if (aqiBean != null) {
            aqiText.setText(aqiBean.getCity().getAqi());
            pm25Text.setText(aqiBean.getCity().getPm25());
        } else {
            aqiText.setText("╥﹏╥");
            pm25Text.setText("╥﹏╥");
        }

        WeatherBean.HeWeatherBean.SuggestionBean suggestionBean = heWeatherBean.getSuggestion();
        String comfort = "舒适度：" + suggestionBean.getComf().getTxt();
        String carWash = "洗车指数：" + suggestionBean.getCw().getTxt();
        String sport = "运行建议：" + suggestionBean.getSport().getTxt();
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }
}
