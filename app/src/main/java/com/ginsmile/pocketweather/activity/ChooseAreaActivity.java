package com.ginsmile.pocketweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ginsmile.pocketweather.R;
import com.ginsmile.pocketweather.db.PocketWeatherDB;
import com.ginsmile.pocketweather.model.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujin on 15/12/13.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private SQLiteDatabase db;
    private List<String> dataList = new ArrayList<String>();

    /**
     * city list,保存从数据库中提取的数据，元素为City，最终转换为dataList来显示
     */
    private List<City> myList;

    private String selectedProvince;
    private String selectedCity;

    private int currentLevel;

    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //已经选择了城市，并且不是从weatheractivity跳过来的，那么直接进入weatheractivity
        if(prefs.getBoolean("city_selected", false) && !isFromWeatherActivity){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);

        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = myList.get(position).getProvince();
                    queryCities();
                }
                else if(currentLevel == LEVEL_CITY){
                    selectedCity = myList.get(position).getCity();
                    queryCounties();
                }
                else if(currentLevel == LEVEL_COUNTY){
                    Toast.makeText(getApplicationContext(), myList.get(position).getCode_id() + "you clicked" + myList.get(position).getCounty(), Toast.LENGTH_SHORT).show();

                    String countyCode = myList.get(position).getCode_id();
                    Intent intent = new Intent(ChooseAreaActivity.this, MainActivity.class);
                    intent.putExtra("county_code", countyCode);
                    startActivity(intent);
                    finish();
                }

            }
        });

        db = PocketWeatherDB.openDatabase(this);

        queryProvinces();
    }

    /**
     * 查询省份
     */
    private void queryProvinces() {
        myList = PocketWeatherDB.loadProvinces(db);

        //Log.v("loadProvince...done...", "size" + myList.size());
        if (myList.size() > 0) {
            dataList.clear();
            for (City city : myList) {
                dataList.add(city.getProvince());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;

        } else {
            Toast.makeText(ChooseAreaActivity.this, "载入出错", Toast.LENGTH_SHORT).show();
        }



    }



    /**
     * 查询城市
     */
    private void queryCities() {
        myList.clear();
        myList = PocketWeatherDB.loadCities(db, selectedProvince);

        if (myList.size() > 0) {
            dataList.clear();
            for (City city : myList) {
                dataList.add(city.getCity());
            }

            adapter.notifyDataSetChanged();

            listView.setSelection(0);
            titleText.setText(selectedProvince);
            currentLevel = LEVEL_CITY;
        } else {
            Toast.makeText(ChooseAreaActivity.this, "载入出错", Toast.LENGTH_SHORT).show();
        }


    }


    /**
     * 查询县
     */
    private void queryCounties() {
        myList.clear();
        myList = PocketWeatherDB.loadcounties(db, selectedCity);


        if (myList.size() > 0) {
            dataList.clear();
            for (City city : myList) {
                dataList.add(city.getCounty());
            }

            adapter.notifyDataSetChanged();

            listView.setSelection(0);
            titleText.setText(selectedCity);
            currentLevel = LEVEL_COUNTY;
        } else {
            Toast.makeText(ChooseAreaActivity.this, "载入出错", Toast.LENGTH_SHORT).show();
        }


    }



    @Override
    public void onBackPressed(){
        if(currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel == LEVEL_CITY){
            queryProvinces();
        }else{
            if(isFromWeatherActivity){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

}