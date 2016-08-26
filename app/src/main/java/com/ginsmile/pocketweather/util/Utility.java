package com.ginsmile.pocketweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xujin on 15/12/13.
 */
public class Utility {


    /**
     * 解析服务器返回的JSON数据,并将解析出的数据存储到本地。 */
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray weatherInfoArray = jsonObject.getJSONArray("HeWeather data service 3.0");
            JSONObject weatherInfo = weatherInfoArray.getJSONObject(0);

            /**
             * "now": {
             "cond": {
             "code": "502",
             "txt": "霾"
             },
             "fl": "-5",
             "hum": "68",
             "pcpn": "0",
             "pres": "1027",
             "tmp": "-2",
             "vis": "2",
             "wind": {
             "deg": "290",
             "dir": "北风",
             "sc": "4-5",
             "spd": "23"
             }
             },
             */
            JSONObject now = weatherInfo.getJSONObject("now");
            String now_hum = now.getString("hum");//湿度
            JSONObject cond = now.getJSONObject("cond");
            String now_cond_txt = cond.getString("txt");//现在的天气
            JSONObject wind = now.getJSONObject("wind");
            String now_wind_dir = wind.getString("dir");//风向
            String now_wind_sc = wind.getString("sc");//风的等级
            String now_tmp = now.getString("tmp");//温度



            /**
             * "hourly_forecast": [
             {
             "date": "2015-12-13 13:00",
             "hum": "48",
             "pop": "0",
             "pres": "1028",
             "tmp": "-1",
             "wind": {
             "deg": "8",
             "dir": "北风",
             "sc": "微风",
             "spd": "13"
             }
             },
             {
             "date": "2015-12-13 16:00",
             "hum": "51",
             "pop": "0",
             "pres": "1028",
             "tmp": "-1",
             "wind": {
             "deg": "13",
             "dir": "东北风",
             "sc": "微风",
             "spd": "12"
             }
             },
             {
             "date": "2015-12-13 19:00",
             "hum": "60",
             "pop": "0",
             "pres": "1028",
             "tmp": "-3",
             "wind": {
             "deg": "20",
             "dir": "东北风",
             "sc": "微风",
             "spd": "10"
             }
             },
             {
             "date": "2015-12-13 22:00",
             "hum": "67",
             "pop": "0",
             "pres": "1027",
             "tmp": "-4",
             "wind": {
             "deg": "245",
             "dir": "西南风",
             "sc": "微风",
             "spd": "10"
             }
             }
             ],
             */
            JSONArray hourly_forecast = weatherInfo.getJSONArray("hourly_forecast");
            String[] hourly_forecast_date = new String[hourly_forecast.length()];
            String[] hourly_forecast_tem = new String[hourly_forecast.length()];
            for(int i = 0; i < hourly_forecast.length();i++){
                JSONObject item = hourly_forecast.getJSONObject(i);
                hourly_forecast_date[i] = item.getString("date");//时间
                hourly_forecast_tem[i] = item.getString("tmp");//当时的温度
            }




            /**
             * "basic": {
             "city": "沈阳",
             "cnty": "中国",
             "id": "CN101070101",
             "lat": "41.799000",
             "lon": "123.418000",
             "update": {
             "loc": "2015-12-13 13:51",
             "utc": "2015-12-13 05:51"
             }
             },
             */
            JSONObject basic = weatherInfo.getJSONObject("basic");
            String basic_city = basic.getString("city");//城市名
            String basic_id = basic.getString("id");//城市id



            /**7天天气预报，今天，以及后面6天
             *
             * 我需要的只有：今天的日出sr日落ss时间，每一天白天的天气txt_d和温度max，min
             */
            JSONArray dailyForecastArray = weatherInfo.getJSONArray("daily_forecast");
            String[] daily_forecast_con_txt_d = new String[dailyForecastArray.length()];
            String sr = "";
            String ss = "";
            String[] daily_forecast_tmp_max = new String[dailyForecastArray.length()];
            String[] daily_forecast_tmp_min = new String[dailyForecastArray.length()];
            for(int i = 0; i < dailyForecastArray.length(); i++){
                JSONObject item = dailyForecastArray.getJSONObject(i);
                if(i == 0){
                    sr = item.getJSONObject("astro").getString("sr");
                    ss = item.getJSONObject("astro").getString("ss");
                }

                JSONObject dailyForecastArrayCond = item.getJSONObject("cond");
                daily_forecast_con_txt_d[i] = dailyForecastArrayCond.getString("txt_d");

                JSONObject dailyForecastArrayTmp = item.getJSONObject("tmp");
                daily_forecast_tmp_max[i] = dailyForecastArrayTmp.getString("max");
                daily_forecast_tmp_min[i] = dailyForecastArrayTmp.getString("min");


            }

            saveWeatherInfo(context,
                    now_hum, now_cond_txt, now_wind_dir, now_wind_sc,now_tmp,
                    hourly_forecast_date, hourly_forecast_tem,
                    basic_city,basic_id,
                    sr, ss,
                    daily_forecast_con_txt_d,daily_forecast_tmp_max,daily_forecast_tmp_min);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中。 */

    private static void saveWeatherInfo(Context context,
                                        String now_hum, String now_cond_txt, String now_wind_dir, String now_wind_sc, String now_tmp,
                                        String[] hourly_forecast_date, String[] hourly_forecast_tem,
                                        String basic_city, String basic_id,String sr, String ss,
                                        String[] daily_forecast_con_txt_d, String[] daily_forecast_tmp_max, String[] daily_forecast_tmp_min) {

        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);

        editor.putString("now_hum", now_hum);
        editor.putString("now_cond_txt", now_cond_txt);
        editor.putString("now_wind_sc", now_wind_sc);
        editor.putString("now_wind_dir", now_wind_dir);
        editor.putString("now_tmp", now_tmp);

        editor.putString("hour_0", hourly_forecast_date[0] + " " + hourly_forecast_tem[0] + "°");
        editor.putString("hour_1", hourly_forecast_date[1] + " " + hourly_forecast_tem[1] + "°");
        editor.putString("hour_2", hourly_forecast_date[2] + " " + hourly_forecast_tem[2] + "°");
        editor.putString("hour_3", hourly_forecast_date[3] + " " + hourly_forecast_tem[3] + "°");

        editor.putString("daily_0", daily_forecast_con_txt_d[0] + daily_forecast_tmp_min[0] + "° ~" + daily_forecast_tmp_max[0] + "°");
        editor.putString("daily_1", daily_forecast_con_txt_d[1] + daily_forecast_tmp_min[1] + "° ~" + daily_forecast_tmp_max[1] + "°");
        editor.putString("daily_2", daily_forecast_con_txt_d[2] + daily_forecast_tmp_min[2] + "° ~" + daily_forecast_tmp_max[2] + "°");
        editor.putString("daily_3", daily_forecast_con_txt_d[3] + daily_forecast_tmp_min[3] + "° ~" + daily_forecast_tmp_max[3] + "°");

        editor.putString("basic_city", basic_city);
        editor.putString("basic_id", basic_id);
        editor.putString("sr", sr);
        editor.putString("ss", ss);

        Log.v("TAG","存入成功～" + ss);
        editor.commit();
    }

}