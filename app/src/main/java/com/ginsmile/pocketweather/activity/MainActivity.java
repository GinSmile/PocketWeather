package com.ginsmile.pocketweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ginsmile.pocketweather.R;
import com.ginsmile.pocketweather.util.HttpCallbackListener;
import com.ginsmile.pocketweather.util.HttpUtil;
import com.ginsmile.pocketweather.util.Key;
import com.ginsmile.pocketweather.util.Utility;

public class MainActivity extends AppCompatActivity {
    TextView city;
    TextView tmpNow;
    TextView weatherInfo;
    TextView weatherWind;
    TextView weatherHourforecast;
    TextView dailyForecast;
    Button refreshButton;
    Button chooseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = (TextView)findViewById(R.id.city);
        tmpNow = (TextView)findViewById(R.id.tmp_now);
        weatherInfo = (TextView)findViewById(R.id.weather_info);
        weatherWind = (TextView)findViewById(R.id.weather_wind);
        weatherHourforecast = (TextView)findViewById(R.id.weather_hourforcast);

        dailyForecast = (TextView)findViewById(R.id.daily_forecast);
        refreshButton = (Button) findViewById(R.id.refresh);
        chooseButton = (Button)findViewById(R.id.choose_button);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                String basic_id = prefs.getString("basic_id", "");//得到城市在和风天气中的id

                if(!TextUtils.isEmpty(basic_id)){
                    queryWeatherCode(basic_id);
                    weatherInfo.setText("同步中～请稍后");
                }else{
                    weatherInfo.setText("请先选择城市！");
                }
            }
        });

        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
            }
        });



        String countyCode = getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode)){
            queryWeatherCode(countyCode);
        }
    }

    private void queryWeatherCode(String countyCode){
        String address = "https://api.heweather.com/x3/weather?cityid=" + countyCode + "&key=" + Key.key;
        queryFromServer(address);
    }

    /**
     * 从和风天气API查询天气信息
     * @param address API地址
     */
    private void queryFromServer(String address) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                //Log.v("TAG", response);
                Utility.handleWeatherResponse(MainActivity.this, response);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeather();
                    } });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weatherInfo.setText("同步失败");
                    } });
            } });


    }

    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Log.v("TAG",prefs.getString("now_cond_txt","-") + prefs.getString("current_date","-:-:-"));

        city.setText(prefs.getString("basic_city","-"));
        tmpNow.setText(prefs.getString("now_tmp","")  + "°");
        weatherInfo.setText(prefs.getString("now_cond_txt",""));
        weatherWind.setText(prefs.getString("now_wind_dir","") + prefs.getString("now_wind_sc","") + "级");
        weatherHourforecast.setText(prefs.getString("hour_1",""));
        dailyForecast.setText(prefs.getString("daily_1",""));

    }
}
