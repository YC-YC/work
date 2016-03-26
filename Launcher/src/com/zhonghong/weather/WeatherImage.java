package com.zhonghong.weather;

import com.zhonghong.launcher_qz.R;

public class WeatherImage
{
  public static int GetImage(String paramString)
  {
    String[] strs = new String[] { "晴", "晴" };
    if (paramString.contains("转"))
    {
    	strs = paramString.split("转");
    	paramString = strs[0];
    }
    if (paramString.equals("晴")) return R.drawable.weather_sunny;
    if (paramString.equals("多云")) return R.drawable.weather_cloudy;
    if (paramString.equals("阴")) return R.drawable.weather_overcast;
    if (paramString.equals("阵雨")) return R.drawable.weather_rain;
    if (paramString.equals("雷阵雨")) return R.drawable.weather_rain;
    if (paramString.equals("雷阵雨伴有冰雹")) return R.drawable.weather_rain;
    if (paramString.equals("雨夹雪")) return R.drawable.weather_rain;
    if (paramString.equals("小雨")) return R.drawable.weather_rain;
    if (paramString.equals("中雨")) return R.drawable.weather_rain;
    if (paramString.equals("大雨")) return R.drawable.weather_rain;
    if (paramString.equals("暴雨")) return R.drawable.weather_rain;
    if (paramString.equals("大暴雨")) return R.drawable.weather_rain;
    if (paramString.equals("特大暴雨")) return R.drawable.weather_rain;
    if (paramString.equals("阵雪")) return R.drawable.weather_rain;
    if (paramString.equals("小雪")) return R.drawable.weather_rain;
    if (paramString.equals("中雪")) return R.drawable.weather_rain;
    if (paramString.equals("大雪")) return R.drawable.weather_rain;
    if (paramString.equals("暴雪")) return R.drawable.weather_rain;
    if (paramString.equals("雾")) return R.drawable.weather_fog;
    if (paramString.equals("冻雨")) return R.drawable.weather_rain;
    if (paramString.equals("沙尘暴")) return R.drawable.weather_haze;
    if (paramString.equals("小到中雨")) return R.drawable.weather_rain;
    if (paramString.equals("中到大雨")) return R.drawable.weather_rain;
    if (paramString.equals("大到暴雨")) return R.drawable.weather_rain;
    if (paramString.equals("暴雨到大暴雨")) return R.drawable.weather_rain;
    if (paramString.equals("大暴雨到特大暴雨")) return R.drawable.weather_rain;
    if (paramString.equals("小到中雪")) return R.drawable.weather_rain;
    if (paramString.equals("中到大雪")) return R.drawable.weather_rain;
    if (paramString.equals("大到暴雪")) return R.drawable.weather_rain;
    if (paramString.equals("浮尘")) return R.drawable.weather_haze;
    if (paramString.equals("扬沙")) return R.drawable.weather_haze;
    if (paramString.equals("强沙尘暴")) return R.drawable.weather_haze;
    if (paramString.equals("霾")) return R.drawable.weather_haze;
    return R.drawable.weather_sunny;
  }
}