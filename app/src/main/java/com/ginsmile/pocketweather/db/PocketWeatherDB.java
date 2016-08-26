package com.ginsmile.pocketweather.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.ginsmile.pocketweather.R;
import com.ginsmile.pocketweather.model.City;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujin on 15/12/14.
 */
public class PocketWeatherDB {




    private static SQLiteDatabase database;
    public static final String DATABASE_FILENAME = "pocket_weather.db"; // 这个是DB文件名字
    public static final String PACKAGE_NAME = "com.ginsmile.pocketweather"; // 这个是自己项目包路径
    public static final String DATABASE_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + PACKAGE_NAME; // 获取存储位置地址

    public static SQLiteDatabase openDatabase(Context context) {
        try {
            String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
            File dir = new File(DATABASE_PATH);
            if (!dir.exists()) {
                dir.mkdir();
            }
            if (!(new File(databaseFilename)).exists()) {
                InputStream is = context.getResources().openRawResource(R.raw.pocket_weather);
                FileOutputStream fos = new FileOutputStream(databaseFilename);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }

                fos.close();
                is.close();
            }
            database = SQLiteDatabase.openOrCreateDatabase(
                    databaseFilename, null);
            return database;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 从数据库中取出所有的省份名称
     * @return
     */
    public static List<City> loadProvinces(SQLiteDatabase db){
        List<City> list = new ArrayList<City>();


        String sql = "select DISTINCT province from CityTable";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            City city = new City();
            city.setProvince(cursor.getString(cursor.getColumnIndex("province")));
            list.add(city);
            cursor.moveToNext();
        }



        if(cursor != null){
            cursor.close();
        }
        return list;
    }

    /**
     * 从数据库中取出该省所有的城市名称
     * @return
     */
    public static List<City> loadCities(SQLiteDatabase db, String selectedProvince){
        List<City> list = new ArrayList<City>();


        String sql = "select DISTINCT city from CityTable where province = \"" + selectedProvince + "\"";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            City city = new City();
            city.setCity(cursor.getString(cursor.getColumnIndex("city")));
            list.add(city);
            cursor.moveToNext();
        }



        if(cursor != null){
            cursor.close();
        }
        return list;
    }

    /**
     * 从数据库中取出该市所有的县名称
     * @return
     */
    public static List<City> loadcounties(SQLiteDatabase db, String selectedCity){
        List<City> list = new ArrayList<City>();


        String sql = "select DISTINCT * from CityTable where city = \"" + selectedCity + "\"";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            City city = new City();
            city.setCounty(cursor.getString(cursor.getColumnIndex("county")));
            city.setCode_id(cursor.getString(cursor.getColumnIndex("code_id")));
            list.add(city);
            cursor.moveToNext();
        }



        if(cursor != null){
            cursor.close();
        }
        return list;
    }
}
