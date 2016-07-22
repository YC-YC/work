package com.zhonghong.weather;

import android.location.Location;

public abstract interface WeatherAbs
{
  public static final int ERROR_LOCATION_FAIL = -1;

  /** 设按城市查询 */
  public abstract void SetCity(String city);
  
  /** 设按定位查询 */
  public abstract void SetLocation(Location location);

  /** 刷新 */
  public abstract int Refresh();

  /**获取定位到的城市 */
  public abstract String GetLocationCity();
  
  /** 是否使用定位  */
  public abstract boolean getIfUseLocation();
  
  /** 查询城市*/
  public abstract int SearchCityName(String city, ReceiveCityList receiveCityList);

  /** 查询结果转成自定义格式 */
  public abstract void SetOnReceiveJson(ReceiveJson receiveJson);
}