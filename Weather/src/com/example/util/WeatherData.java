package com.example.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.example.entity.Weather;

/**
 * @author Mark
 * 
 */
public class WeatherData {
	private Activity activity;

	public WeatherData(Activity activity) {
		this.activity = activity;
	}

	public Weather getData(String strUrl) {
		Log.i("TAG","Url: "+strUrl);
		return parseJson(connServerForResult(strUrl));
	}

	private String connServerForResult(String strUrl) {
		HttpGet httpRequest = new HttpGet(strUrl);
		String strResult = "";
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("rresult" + strResult);
		return strResult;
	}

	private Weather parseJson(String strResult) {
		Weather weather = null;
		try {
			JSONObject jsonObj = new JSONObject(strResult)
					.getJSONObject("weatherinfo");
			weather = new Weather();
			int ftime = jsonObj.getInt("fchh");
			int temp = 0;
			if (ftime >= 18 || ftime < 8) {
				weather.setNight(true);
				temp = 1;
			}
			weather.setCity(jsonObj.getString("city"));
			weather.setComfortable(jsonObj.getString("index"));

			weather.setRefreshDate(getDate());
			weather.setRefreshTime(getTime());
			weather.setRefreshWeek(getWeek());
			weather.setPicIndex(jsonObj.getInt("img1"));

			List<Integer> topPic = new ArrayList<Integer>();
			if (temp == 1) {
				topPic.add(getSavePic(activity));
			} else {
				topPic.add(getJsonPic(jsonObj, "img", 1 + temp));
				savePic(activity, topPic.get(0));
			}
			topPic.add(getJsonPic(jsonObj, "img", 3 - temp));
			topPic.add(getJsonPic(jsonObj, "img", 5 - temp));
			topPic.add(getJsonPic(jsonObj, "img", 7 - temp));
			topPic.add(getJsonPic(jsonObj, "img", 9 - temp));
			weather.setTopPic(topPic);

			List<Integer> lowPic = new ArrayList<Integer>();
			lowPic.add(getJsonPic(jsonObj, "img", 2 - temp));
			lowPic.add(getJsonPic(jsonObj, "img", 4 - temp));
			lowPic.add(getJsonPic(jsonObj, "img", 6 - temp));
			lowPic.add(getJsonPic(jsonObj, "img", 8 - temp));
			lowPic.add(getJsonPic(jsonObj, "img", 10 - temp));
			weather.setLowPic(lowPic);

			List<String> tempList = new ArrayList<String>();
			tempList.add(jsonObj.getString("temp1"));
			tempList.add(jsonObj.getString("temp2"));
			tempList.add(jsonObj.getString("temp3"));
			tempList.add(jsonObj.getString("temp4"));
			tempList.add(jsonObj.getString("temp5"));

			List<String> tempListMax = new ArrayList<String>();
			if (temp == 1) {
				tempListMax.add(getSaveTemperature(activity));
			} else {
				tempListMax
						.add(getTemperatureMaxAndMin(tempList.get(0))[0 + temp]);
				saveTemperature(activity, tempListMax.get(0));
			}
			tempListMax
					.add(getTemperatureMaxAndMin(tempList.get(1 - temp))[0 + temp]);
			tempListMax
					.add(getTemperatureMaxAndMin(tempList.get(2 - temp))[0 + temp]);
			tempListMax
					.add(getTemperatureMaxAndMin(tempList.get(3 - temp))[0 + temp]);
			tempListMax
					.add(getTemperatureMaxAndMin(tempList.get(4 - temp))[0 + temp]);
			weather.setTemperatureMax(tempListMax);

			weather.setTodayTemperature(getTemperatureMaxAndMin(tempList.get(0))[0]);
			weather.setTodayWeather(jsonObj.getString("img_title1"));

			List<String> tempListMin = new ArrayList<String>();
			tempListMin.add(getTemperatureMaxAndMin(tempList.get(0))[1 - temp]);
			tempListMin.add(getTemperatureMaxAndMin(tempList.get(1))[1 - temp]);
			tempListMin.add(getTemperatureMaxAndMin(tempList.get(2))[1 - temp]);
			tempListMin.add(getTemperatureMaxAndMin(tempList.get(3))[1 - temp]);
			tempListMin.add(getTemperatureMaxAndMin(tempList.get(4))[1 - temp]);
			weather.setTemperatureMin(tempListMin);

			weather.setTomorrowTemperature(tempList.get(1));

			List<String> weatherList = new ArrayList<String>();
			if (temp == 1) {
				weatherList.add(getSaveWeather(activity));
			} else {
				weatherList.add(jsonObj.getString("weather" + 1));
				saveWeather(activity, weatherList.get(0));
			}
			weatherList.add(jsonObj.getString("weather" + (2 - temp)));
			weatherList.add(jsonObj.getString("weather" + (3 - temp)));
			weatherList.add(jsonObj.getString("weather" + (4 - temp)));
			weatherList.add(jsonObj.getString("weather" + (5 - temp)));
			weather.setWeather(weatherList);
			weather.setTomorrowWeather(weatherList.get(1));

			List<String> windList = new ArrayList<String>();
			windList.add(jsonObj.getString("wind1"));
			windList.add(jsonObj.getString("wind2"));
			windList.add(jsonObj.getString("wind3"));
			windList.add(jsonObj.getString("wind4"));
			windList.add(jsonObj.getString("wind5"));
			weather.setWind(windList);

			weather.setMaxlist(transplate(tempListMax));
			weather.setMinlist(transplate(tempListMin));

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return weather;
	}

	private String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 EEE",
				Locale.CHINA);
		String date = sdf.format(new java.util.Date());
		System.out.println(date);
		return date;
	}

	private String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
		String time = sdf.format(new java.util.Date()) + " " + "更新";
		System.out.println(time);
		return time;
	}

	private String getWeek() {
		return null;
	}

	private String[] getTemperatureMaxAndMin(String str) {
		return str.split("~");
	}

	private List<Integer> transplate(List<String> strList) {
		List<Integer> intList = new ArrayList<Integer>();
		for (String temp : strList) {
			intList.add(Integer.valueOf(temp.split("℃")[0]));
		}
		return intList;
	}

	private int getJsonPic(JSONObject jsonObj, String str, int index)
			throws JSONException {
		int result = jsonObj.getInt(str + index);
		if (result == 99 && index > 1) {
			index--;
			result = jsonObj.getInt(str + index);
		}
		return result;
	}

	private void saveTemperature(Activity activity, String value) {
		// MySharedPreferences mp = new MySharedPreferences(activity);
		// mp.writeMessage("temperature", value);
	}

	private String getSaveTemperature(Activity activity) {
		MySharedPreferences mp = new MySharedPreferences(activity);
		return mp.readMessage("temperature", "100");
	}

	private void saveWeather(Activity activity, String value) {
		// MySharedPreferences mp = new MySharedPreferences(activity);
		// mp.writeMessage("weather", value);
	}

	private String getSaveWeather(Activity activity) {
		MySharedPreferences mp = new MySharedPreferences(activity);
		return mp.readMessage("weather", "");
	}

	private void savePic(Activity activity, int value) {
		// MySharedPreferences mp = new MySharedPreferences(activity);
		// mp.writeMessage("pic", value);
	}

	private int getSavePic(Activity activity) {
		MySharedPreferences mp = new MySharedPreferences(activity);
		return mp.readMessage("pic", 99);
	}
}
