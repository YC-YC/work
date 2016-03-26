/**
 * 
 */
package com.zhonghong.weather;

import java.util.ArrayList;

/**
 * @author YC
 * @time 2016-3-24 上午11:14:19
 */
public class WeatherInfo {

	/** 发布日期 */
	public String PublishDay;
	/** 发布时间*/
	public String PublishTime;
	/** 是否GPS定位 */
	public boolean isUseGps = false;
	/** 城市*/
	public String location;
	/** 今天天气*/
	public WeatherObject mWeather = null;
	/** 未来几天天气 */
	public ArrayList<WeatherObject> mWeatherFeatures = new ArrayList();
	
	public static class WeatherObject
	{
	  public String Fan = "";
	  public String Info = "";
	  public int Temperature = 0;
	  public int TemperatureLo = 0;
	  public int Temperaturehi = 0;
	  public int WeatherIcon = 2130837602;
	  public String Week = "";
	  public String day = "";
	  public String forcastDay = "";
	  public String forcastTime = "";
	  public int humidity = 0;
	  public int pm25 = 0;
	  public String weatherStr = "";
	};
}
