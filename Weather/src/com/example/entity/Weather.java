package com.example.entity;

import java.util.List;

public class Weather {
	private boolean isNight;
	private int picIndex;
	private String city;
	private String refreshDate;
	private String refreshTime;
	private String refreshWeek;
	private String comfortable;
	private String tomorrowTemperature;
	private String tomorrowWeather;
	private String todayTemperature;
	private String todayWeather;
	
	private List<String> temperatureMax;
	private List<String> temperatureMin;
	private List<String> weather;
	private List<String> wind;
	
	private List<Integer> maxlist;
	private List<Integer> minlist;
	
	private List<Integer> topPic;
	private List<Integer> lowPic;
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getRefreshDate() {
		return refreshDate;
	}
	public void setRefreshDate(String refreshDate) {
		this.refreshDate = refreshDate;
	}
	public String getRefreshTime() {
		return refreshTime;
	}
	public List<String> getWeather() {
		return weather;
	}
	public void setWeather(List<String> weather) {
		this.weather = weather;
	}
	public List<String> getWind() {
		return wind;
	}
	public void setWind(List<String> wind) {
		this.wind = wind;
	}
	public void setRefreshTime(String refreshTime) {
		this.refreshTime = refreshTime;
	}
	/*public String getRefreshWeek() {
		return refreshWeek;
	}*/
	public void setRefreshWeek(String refreshWeek) {
		this.refreshWeek = refreshWeek;
	}
	public String getTomorrowTemperature() {
		return tomorrowTemperature;
	}
	public void setTomorrowTemperature(String tomorrowTemperature) {
		this.tomorrowTemperature = tomorrowTemperature;
	}
	public String getTomorrowWeather() {
		return tomorrowWeather;
	}
	public void setTomorrowWeather(String tomorrowWeather) {
		this.tomorrowWeather = tomorrowWeather;
	}
	public String getComfortable() {
		return comfortable;
	}
	public void setComfortable(String comfortable) {
		this.comfortable = comfortable;
	}
	public List<String> getTemperatureMax() {
		return temperatureMax;
	}
	public void setTemperatureMax(List<String> temperatureMax) {
		this.temperatureMax = temperatureMax;
	}
	public List<String> getTemperatureMin() {
		return temperatureMin;
	}
	public void setTemperatureMin(List<String> temperatureMin) {
		this.temperatureMin = temperatureMin;
	}
	public List<Integer> getMaxlist() {
		return maxlist;
	}
	public void setMaxlist(List<Integer> maxlist) {
		this.maxlist = maxlist;
	}
	public List<Integer> getMinlist() {
		return minlist;
	}
	public void setMinlist(List<Integer> minlist) {
		this.minlist = minlist;
	}
	public int getPicIndex() {
		return picIndex;
	}
	public void setPicIndex(int picIndex) {
		this.picIndex = picIndex;
	}
	public List<Integer> getLowPic() {
		return lowPic;
	}
	public void setLowPic(List<Integer> lowPic) {
		this.lowPic = lowPic;
	}
	public List<Integer> getTopPic() {
		return topPic;
	}
	public void setTopPic(List<Integer> topPic) {
		this.topPic = topPic;
	}
	public boolean isNight() {
		return isNight;
	}
	public void setNight(boolean isNight) {
		this.isNight = isNight;
	}
	public String getTodayTemperature() {
		return todayTemperature;
	}
	public void setTodayTemperature(String todayTemperature) {
		this.todayTemperature = todayTemperature;
	}
	public String getTodayWeather() {
		return todayWeather;
	}
	public void setTodayWeather(String todayWeather) {
		this.todayWeather = todayWeather;
	}
	
}
