package com.zhonghong.weather;
public abstract interface ReceiveJson
{
	/**对http返回的数据封装成需要的WeatherInfo格式*/
  public abstract void OnReceive(WeatherInfo info);
}