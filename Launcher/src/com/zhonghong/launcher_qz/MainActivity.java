package com.zhonghong.launcher_qz;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhonghong.utils.WeatherUtils;
import com.zhonghong.utils.WeatherBean;
import com.zhonghong.view.circlemenu.CircleMenuLayout;
import com.zhonghong.view.circlemenu.CircleMenuLayout.OnMenuItemClickListener;
import com.zhonghong.weather.ReceiveCityList;
import com.zhonghong.weather.ReceiveJson;
import com.zhonghong.weather.WeatherInfo;
import com.zhonghong.weather.WeatherInterface;
import com.zhonghong.weather.WeatherLoc;

public class MainActivity extends Activity implements OnClickListener {

	private static final String tag = "Launcher";
	
	private LinearLayout mLayoutBg;	//背景图层
	private Button mBtnNavi, mBtnPhone, mBtnBtMusic;
	
	private ImageView mImgWeather;
	private TextView mTvWeather;
	private TextView mTvDegree;
	
	private CircleMenuLayout mCircleMenuLayout;
	
	private WeatherUtils mWeatherUtils;
	
	private WeatherInterface mWeatherInterface;
	
	private String[] mItemTexts = new String[] { 
			"收音机", 
			"设置", 
			"USB",
			"电话", 
			"音乐 "};
	private int[] mItemImgs = new int[] { 
			R.drawable.radio_selector,
			R.drawable.setup_selector, 
			R.drawable.usb_selector,
			R.drawable.bt_selector, 
			R.drawable.music_selector};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mWeatherUtils = WeatherUtils.getInstanse(this);
		initViews();
		initWeather();
		test();
	}


	private void test() {
		String str = "我是abc123";
		for (int i = 0; i < str.length(); i++) {
			Log.i(tag, "get = " + str.charAt(i));
		}
		
	}


	/**
	 * 天气相关
	 */
	private void initWeather() {
		mImgWeather = (ImageView) findViewById(R.id.img_weather);
		mTvWeather = (TextView) findViewById(R.id.tv_weather);
		mTvDegree = (TextView) findViewById(R.id.tv_degree);
		
//		WeatherBean info = mWeatherUtils.getWeatherInfo(WeatherUtils.WEATHER_CLOUDY);
//		if (info != null)
//		{
			mImgWeather.setBackgroundResource(R.drawable.weather_sunny);
			mTvWeather.setText("未知");
			mTvDegree.setText("未知");
//		}
		
		mWeatherInterface = new WeatherLoc();
		new Thread(new Runnable() {
			@Override
			public void run() {
				long lastCheckTime = 0;
				while (true)
				{
					long nowTime = SystemClock.elapsedRealtime();
					long timeDiff = nowTime - lastCheckTime;
					if (timeDiff > 30*1000)
					{
						lastCheckTime = SystemClock.elapsedRealtime();
//						Log.i(tag, "请求天气信息 ");
						mWeatherInterface.SetCity("深圳");
						mWeatherInterface.Refresh();
						mWeatherInterface.SetOnReceiveJson(new ReceiveJson() {
							
							@Override
							public void OnReceive(final WeatherInfo info) {
								mHandler.post(new Runnable() {
									
									@Override
									public void run() {
										String weatherStr = info.mWeather.weatherStr;
//										Log.i(tag, "weather = " + weatherStr);
										if (info.mWeather.weatherStr != null)
										{
											mTvWeather.setText(weatherStr);
											mImgWeather.setBackgroundResource(info.mWeather.WeatherIcon);
											mTvDegree.setText(info.mWeather.TemperatureLo + "。C");
										}
									}
								});
							}
						});
						
						mWeatherInterface.SearchCityName("广", new ReceiveCityList() {
							
							@Override
							public void OnReceiveCityList(List<String> paramList) {
								for (int i = 0; i < paramList.size(); i++)
								{
//									Log.i(tag, "getCityList = " + paramList.get(i));
								}
							}
						});
					}
				}
			}
		}).start();
		
	}

	private final Handler mHandler = new Handler();
	
	/**
	 * 
	 */
	private void initViews() {
		
		mLayoutBg = (LinearLayout) findViewById(R.id.layout_bg);
		mLayoutBg.setSelected(false);
		
		mBtnNavi = (Button) findViewById(R.id.btn_navi);
		mBtnNavi.setOnClickListener(this);
		mBtnPhone = (Button) findViewById(R.id.btn_phone);
		mBtnPhone.setOnClickListener(this);
		mBtnBtMusic = (Button) findViewById(R.id.btn_bt_musice);
		mBtnBtMusic.setOnClickListener(this);
		
		mCircleMenuLayout = (CircleMenuLayout) findViewById(R.id.id_menulayout);
		mCircleMenuLayout.setMenuAttr(mItemImgs, mItemTexts, 60, 600);
		mCircleMenuLayout.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public void itemClick(View view, int pos) {
				Toast.makeText(getApplicationContext(), mItemTexts[pos],
						Toast.LENGTH_SHORT).show();

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_navi:
			Toast.makeText(this, "导航", 1).show();
			break;
		case R.id.btn_phone:
			Toast.makeText(this, "电话", 1).show();
			break;
		case R.id.btn_bt_musice:
			Toast.makeText(this, "蓝牙音乐", 1).show();
			break;
		default:
			break;
		}
	}

}
