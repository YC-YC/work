/**
 * 
 */
package com.zhonghong.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.zhonghong.launcher.R;

/**
 * @author YC
 * @time 2016-3-23 下午3:11:28
 */
public class WeatherUtils {
	
	public static final String WEATHER_SUNNY = "sunny";	//晴
	public static final String WEATHER_SUNNY_NIGHT = "sunny_night";	//晴_夜晚
	public static final String WEATHER_CLOUDY = "cloudy";	//多云
	public static final String WEATHER_CLOUDY_NIGHT = "cloudy_night";	//多云_夜晚
	public static final String WEATHER_FOG = "fog";	//雾
	public static final String WEATHER_HAZE = "haze";	//霾
	public static final String WEATHER_OVERCAST = "overcast";	//多云
	public static final String WEATHER_RAIN = "rain";	//雨
	
	public static WeatherUtils getInstanse(Context context)
	{
		mContext = context;
		return ((instanse == null)? (instanse = new WeatherUtils()) : instanse);
	}
	
	/**
	 * @param weather See {@code WeatherUtils.WEATHER_SUNNY}...
	 * @return
	 */
	public static int getImgResourceId(String key)
	{
		for (int i = 0; i < mWeatherList.size(); i++)
		{
			WeatherBean bean = mWeatherList.get(i);
			if (bean.equals(key))
			{
				return bean.getImgId();
			}
		}
		return 0;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public WeatherBean getWeatherInfo(String key)
	{
		for (int i = 0; i < mWeatherList.size(); i++)
		{
			WeatherBean bean = mWeatherList.get(i);
			if (bean.getKey().equals(key))
			{
				return bean;
			}
		}
		return null;
	}
	
	private static Context mContext;
	private static WeatherUtils instanse = null;
	private static List<WeatherBean> mWeatherList;
	private WeatherUtils() {
		
		mWeatherList = new ArrayList<WeatherBean>();
		mWeatherList.clear();
		mWeatherList.add(new WeatherBean(WEATHER_SUNNY, R.drawable.weather_sunny, 
				mContext.getResources().getString(R.string.weather_sunny)));
		
		mWeatherList.add(new WeatherBean(WEATHER_SUNNY_NIGHT, R.drawable.weather_sunny_night, 
				mContext.getResources().getString(R.string.weather_sunny_night)));
		
		mWeatherList.add(new WeatherBean(WEATHER_CLOUDY, R.drawable.weather_cloudy, 
				mContext.getResources().getString(R.string.weather_cloudy)));
		
		mWeatherList.add(new WeatherBean(WEATHER_CLOUDY_NIGHT, R.drawable.weather_cloudy_night, 
				mContext.getResources().getString(R.string.weather_cloudy_night)));
		
		mWeatherList.add(new WeatherBean(WEATHER_FOG, R.drawable.weather_fog, 
				mContext.getResources().getString(R.string.weather_fog)));
		
		mWeatherList.add(new WeatherBean(WEATHER_HAZE, R.drawable.weather_haze, 
				mContext.getResources().getString(R.string.weather_haze)));
		
		mWeatherList.add(new WeatherBean(WEATHER_OVERCAST, R.drawable.weather_overcast, 
				mContext.getResources().getString(R.string.weather_overcast)));
		
		mWeatherList.add(new WeatherBean(WEATHER_RAIN, R.drawable.weather_rain, 
				mContext.getResources().getString(R.string.weather_rain)));
	
	}
	

}
