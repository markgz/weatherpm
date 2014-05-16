package com.example.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DB {
	private static final String filename = android.os.Environment.getExternalStorageDirectory()+"/weather/database.db";
	//查询省
	public static List<Map<String, String>> getProvince(){
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(filename, null);
		String SQL = "select DISTINCT province from addressIdTbl";
		Cursor cursor = database.rawQuery(SQL, null);
        cursor.moveToFirst();
        do{
        	Map<String, String> map = new HashMap<String, String>();
        	map.put("address", cursor.getString(0));
        	list.add(map);
        }while(cursor.moveToNext());
        cursor.close();
        database.close();
        return list;
	}
	
	//get city pingying
	public static String getCityPY(String id){
		String cityPY = "";
		
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(filename, null);
		
		String SQL = "select city  from addressIdTbl where addressID = ?";
		
		Cursor cursor = database.rawQuery(SQL, new String[]{id});
		
        if(cursor.moveToFirst()){
        	
        	
        	String city = cursor.getString(0);
        	Log.i("CITY", "city:::: "+city);
        	String queryPYSQL = "select AllNamePin from NT_areapin where Name = ?";
        	
        	cursor = database.rawQuery(queryPYSQL, new String[]{city});
        	
        	if(cursor.moveToFirst()){
        		
        		cityPY = cursor.getString(0);
        		
        		Log.i("CITY", "cityPY:::: "+cityPY);
        		
        	}else{
        		cityPY ="guangzhou";
        	}
        	
        }else{
        	cityPY ="guangzhou";
        }
		
		
		return cityPY;
		
	}
	//查询市
	public static List<Map<String, String>> getCity(String province){
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(filename, null);
		String SQL = "select DISTINCT city from addressIdTbl where province = "+"'"+province+"'";
		Cursor cursor = database.rawQuery(SQL, null);
        cursor.moveToFirst();
		do {
			Map<String, String> map = new HashMap<String, String>();
        	map.put("address", cursor.getString(0));
        	list.add(map);
		} while (cursor.moveToNext());
        cursor.close();
        database.close();
        return list;
	}
	//查询县（区）
	public static List<Map<String, String>> getCountry(String city){
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(filename, null);
		String SQL = "select country from addressIdTbl where city = "+"'"+city+"'";
		Cursor cursor = database.rawQuery(SQL, null);
        cursor.moveToFirst();
        do{
        	Map<String, String> map = new HashMap<String, String>();
        	map.put("address", cursor.getString(0));
        	list.add(map);
        }while(cursor.moveToNext());
        cursor.close();
        database.close();
        return list;
	}
	//查询地址id号
	public static String getAddressId(String country){
		String result = "";
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(filename, null);
		String SQL = "select addressId from addressIdTbl where country = "+"'"+country+"'";
		Cursor cursor = database.rawQuery(SQL, null);
        cursor.moveToFirst();
        do{
        	result = cursor.getString(0);
        }while(cursor.moveToNext());
        cursor.close();
        database.close();
        return result;
	}
	//保存添加的地区和id号
	public static void saveCityAndId(String city, String id){
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(filename, null);
		ContentValues values = new ContentValues();
		values.put("city", city);
		values.put("addressId", id);
		database.insert("cityTbl", null, values);
		database.close();
	}
	//查询添加的地区和id号
	public static List<Map<String, String>> getCityAndId(){
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(filename, null);
		String SQL = "select city,addressId from cityTbl";
		Cursor cursor = database.rawQuery(SQL, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
	        do{
	        	String city = cursor.getString(0);
	        	String id = cursor.getString(1);
	        	Map<String, String> map = new HashMap<String, String>();
	        	map.put("address", city);
	        	map.put("id", id);
	        	list.add(map);
	        }while(cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return list;
	}
	//删除指定id的地区记录
	public static void deleteCityAndId(String id){
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(filename, null);
		String SQL = "delete from cityTbl where addressId = '" + id + "'";
		database.execSQL(SQL);
        database.close();
	}
}
