/**
 * 
 */
package com.zhonghong.weather;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.location.Location;
import android.util.Log;
import android.util.Xml;

import com.zhonghong.utils.HttpCallback;
import com.zhonghong.utils.HttpUtils;
import com.zhonghong.weather.WeatherInfo.WeatherObject;

/**
 * @author YC
 * @time 2016-3-24 上午11:38:32
 */
public class WeatherLoc implements WeatherAbs {

	private static final String tag = "WeatherLoc";
	
	private static final String FINDCITY_URL = "http://res.c-escort.com.cn/query_city.jspx?esn=001&mdn=13111111111&pwd=002&key=3cc0b731abc72fa15d78b704ef61666b&queryType=query_city_by_cityname&cityName=";
	private static final String WEATHER_LONLAT_URL = "http://res.c-escort.com.cn/query_weather.jspx?esn=001&mdn=13111111111&pwd=002&key=3cc0b731abc72fa15d78b704ef61666b&queryType=query_weather_by_lonlat&longitude=";
	private static final String WEATHER_CITY_URL = "http://res.c-escort.com.cn/query_weather_more.jspx?esn=001&mdn=13111111111&pwd=002&key=3cc0b731abc72fa15d78b704ef61666b&queryType=query_weather_by_cityname&cityName=";

	
	/**是否使用定位查询*/
	private boolean mUselocation = true;
	/**查询城市名*/
	private String mCityName = "";
	/**定位信息*/
	private Location mLocation;
	/**定位到的城市名*/
	private String mLocationCityName = "";
	
	private ReceiveJson mReceiveJson;
	private ReceiveCityList mReceiveCityList;
	
	private WeatherInfo mWeatherInfo = new WeatherInfo();
	
	
	
	public WeatherLoc() {

	}

	@Override
	public void SetCity(String city) {
		mCityName = city;
		mUselocation = (city != null)? false:true;
	}

	@Override
	public void SetLocation(Location location) {
		mLocation = location;
	}

	@Override
	public int Refresh() {
		HttpUtils http = new HttpUtils();
		String reqUrl = "";
		if (mUselocation)
		{
			if (mLocation == null)
			{
				return ERROR_LOCATION_FAIL;
			}
			reqUrl = WEATHER_LONLAT_URL + mLocation.getLongitude() +"&latitude=" + mLocation.getLatitude();
		}
		else
		{
			reqUrl = WEATHER_CITY_URL + mCityName;
		}
		
		http.post(reqUrl, mHttpCallback);
		return 0;
	}

	@Override
	public String GetLocationCity() {
		return mUselocation ? mLocationCityName : mCityName;
	}

	@Override
	public boolean getIfUseLocation() {
		return mUselocation;
	}

	@Override
	public int SearchCityName(String city, ReceiveCityList receiveCityList) {
		mReceiveCityList = receiveCityList;
		HttpUtils http = new HttpUtils();
		String reqUrl = FINDCITY_URL + city;
		http.post(reqUrl, mQureyCityCallback);
		return 0;
	}

	@Override
	public void SetOnReceiveJson(ReceiveJson receiveJson) {
		mReceiveJson = receiveJson;
	}
	
	private HttpCallback mHttpCallback = new HttpCallback() {

		@Override
		synchronized public void onReceive(String response) {

			XmlPullParser parser = Xml.newPullParser();
			WeatherObject object = null;
			int days = 0;

			try {
				parser.setInput(new StringReader(response));
				int type = parser.getEventType();
//				Log.i(tag, "Paser type = " + type);
				while (type != XmlPullParser.END_DOCUMENT) {

					switch (type) {
					case XmlPullParser.START_DOCUMENT:
						break;

					case XmlPullParser.START_TAG:
						String name = parser.getName();
//						Log.i(tag, "Paser name = " + name);
						if ("TransContent".equals(name)
								|| "TeleTMSPRsp".equals(name)
								|| "NightWeather".equals(name)
								|| "WindDirection1".equals(name)
								|| "WindDirection2".equals(name)
								|| "DayWeatherImage".equals(name)
								|| "NightWeatherImage".equals(name)
								|| "WeatherIndexList".equals(name))

						{
							break;
						} else if ("WeatherList".equals(name)) {
							object = new WeatherObject();
							break;
						}
						String value = parser.nextText();
						if (name == null || value == null || value.equals("")) {
							break;
						}
						if ("Region".equals(name)) 
						{
							mWeatherInfo.location = value;
							if (mUselocation) 
							{
								mWeatherInfo.isUseGps = true;
								mLocationCityName = value;
							} 
							else 
							{
								mWeatherInfo.isUseGps = false;
							}
						} 
						else if ("PublishTime".equals(name)) 
						{
							mWeatherInfo.PublishDay = value.substring(0, 10);
							mWeatherInfo.PublishTime = value.substring(11);
						} 
						else if ("ForcastDate".equals(name)) 
						{
							object.forcastDay = value.substring(0, 10);
							object.forcastTime = value.substring(11);
						} 
						else if ("DayWeather".equals(name)) 
						{
							object.weatherStr = value;
							object.WeatherIcon = WeatherImage.GetImage(value);
						} 
						else if ("MaxTemperature".equals(name)) 
						{
							object.Temperaturehi = Integer.parseInt(value);
						} 
						else if ("MinTemperature".equals(name)) 
						{
							object.TemperatureLo = Integer.parseInt(value);
						} 
						else if ("WindLevel1".equals(name)) 
						{
							object.Fan = value;
							days++;
							if (days == 1) {
								mWeatherInfo.mWeather = object;
								mWeatherInfo.mWeatherFeatures.clear();
							} else {
								mWeatherInfo.mWeatherFeatures.add(object);
							}
						}
						break;
					case XmlPullParser.END_TAG:
						break;
					}
					type = parser.next();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (mReceiveJson != null) {
				mReceiveJson.OnReceive(mWeatherInfo);
			}

		}
	};
	
	private HttpCallback mQureyCityCallback = new HttpCallback(){

		@Override
		public void onReceive(String response) {
			
			XmlPullParser parser = Xml.newPullParser();
			List<String> cities = new ArrayList<String>();
			cities.clear();
			boolean bGetName = false;
			
			try {
				parser.setInput(new StringReader(response));
				int type = parser.getEventType();
					while (type != XmlPullParser.END_DOCUMENT) {
					switch (type) {
					case XmlPullParser.START_DOCUMENT:
						break;
	
					case XmlPullParser.START_TAG:
						String name = parser.getName();
						if ("TransContent".equals(name)
								|| "TeleTMSPRsp".equals(name)
								|| "CityList".equals(name)
								|| "CityCode".equals(name) )

						{
							break;
						} 
						else if ("ParentCity".equals(name))
						{
							bGetName = false;
							break;
						}
						else if ("ChildCity".equals(name))
						{
							bGetName = true;
							break;
						}
						String value = parser.nextText();
//						Log.i(tag, "name = " + name + ", value = " + value);
						if (name == null || value == null || value.equals(""))
						{
							break;
						}
						if ("CityName".equals(name))
						{
							if (bGetName)
							{
								cities.add(value);
							}
						}
						break;
					case XmlPullParser.END_TAG:
						break;
					}
					type = parser.next();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		
			if (mReceiveCityList != null)
			{
				mReceiveCityList.OnReceiveCityList(cities);
			}
		}
		
	};
	
	

	

	
	
}
