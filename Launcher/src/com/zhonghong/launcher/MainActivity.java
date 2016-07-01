package com.zhonghong.launcher;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhonghong.aidl.CanInfoParcel;
import com.zhonghong.can.CanManager;
import com.zhonghong.leftitem.LeftItemsCtrl;
import com.zhonghong.menuitem.BtCommand;
import com.zhonghong.menuitem.BtMusicCommand;
import com.zhonghong.menuitem.ItemControl;
import com.zhonghong.menuitem.RadioCommand;
import com.zhonghong.menuitem.SettingsCommand;
import com.zhonghong.menuitem.USBCommand;
import com.zhonghong.utils.FontsUtils;
import com.zhonghong.utils.T;
import com.zhonghong.utils.WeatherUtils;
import com.zhonghong.weather.ReceiveCityList;
import com.zhonghong.weather.ReceiveJson;
import com.zhonghong.weather.WeatherInfo;
import com.zhonghong.weather.WeatherInterface;
import com.zhonghong.weather.WeatherLoc;
import com.zhonghong.widget.CircleMenu;
import com.zhonghong.widget.CircleMenu.OnMenuItemClickListener;
import com.zhonghong.widget.CircleMenu.OnMenuItemLongClickListener;
import com.zhonghong.widget.CircleMenu.OnPageChangeListener;
import com.zhonghong.widget.OverlapLayout;

public class MainActivity extends Activity implements OnClickListener {

	private final String TAG = getClass().getSimpleName();
	private LinearLayout mLayoutBg;	//背景图层
	/**左边三个图标*/
	private List<OverlapLayout> mLeftItems = new ArrayList<OverlapLayout>();
	private LeftItemsCtrl mLeftItemCtrl;
	private ImageView mImgWeather;
	private TextView mTvWeather;
	private TextView mTvDegree;
	
	private TextView mTvCanAuto, mTvCanPower, mTvLeftTemperature, mTvAirWindLevel;
	private ImageView mImgAirCircurlationMode, mImgBlowMode;
	private CircleMenu mCircleMenuLayout;
	private List<ImageView> mImgPageIndexs = new ArrayList<ImageView>();
	
	private TextView mTvTimeData, mTvTimeTime;
	
	private WeatherUtils mWeatherUtils;
	
	private WeatherInterface mWeatherInterface;
	
	private ItemControl mItemControl;
	
	/**记录长按时Menu项*/
	private int mLongClickItem;
	/**记录左边三个按键的状态*/
	private boolean[] mLeftItemActives = new boolean[]{
		false, false, false	
	};
	
	private final Handler mHandler = new Handler();
	private final Handler mUpdateUiHandler = new UiHandle();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//设置不受状态栏，导航栏影响
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		
		initData();
		initMainViews();
		initWeather();
		initTimeView();
		initCanViews();
	}

	/**初始化数据*/
	private void initData() {
		
		mWeatherUtils = WeatherUtils.getInstanse(this);
		
		mLeftItemCtrl= new LeftItemsCtrl(this);
		
		mItemControl = new ItemControl();
		mItemControl.setCommand(1, new BtMusicCommand());
		mItemControl.setCommand(2, new RadioCommand());
		mItemControl.setCommand(3, new USBCommand());
		mItemControl.setCommand(4, new BtCommand());
		mItemControl.setCommand(11, new SettingsCommand());
		
		CanManager.getInstace().setHandle(mUpdateUiHandler);
	}

	/** 天气相关 */
	private void initWeather() {
		mImgWeather = (ImageView) findViewById(R.id.img_weather);
		mTvWeather = (TextView) findViewById(R.id.tv_weather);
		mTvWeather.setTypeface(FontsUtils.getRuiZiBiGerTypeface(this));
		mTvDegree = (TextView) findViewById(R.id.tv_degree);
		mTvDegree.setTypeface(FontsUtils.getExpansivaTypeface(this));
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
		})/*.start()*/;
		
	}
	
	private void initTimeView() {
		mTvTimeData = (TextView) findViewById(R.id.time_data);
		mTvTimeData.setTypeface(FontsUtils.getExpansivaTypeface(this));
		mTvTimeTime = (TextView) findViewById(R.id.time_time);
		mTvTimeTime.setTypeface(FontsUtils.getExpansivaTypeface(this));
		refreshTimeView();
		mUpdateUiHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				refreshTimeView();
				mUpdateUiHandler.postDelayed(this, 1000);
			}
		}, 1000);
	}
	
	/**刷新时间*/
	private void refreshTimeView(){
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd-HH:mm");       
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
		String str = formatter.format(curDate);
		String[] strs = str.split("-");
		if (mTvTimeData != null){
			mTvTimeData.setText(strs[0]);
		}
		if (mTvTimeTime != null){
			mTvTimeTime.setText(strs[1]);
		}
	}
	
	/**Can相关*/
	private void initCanViews() {
		mTvCanAuto = (TextView) findViewById(R.id.tv_auto);
		mTvCanAuto.setTypeface(FontsUtils.getExpansivaTypeface(this));
		mTvCanAuto.setSelected(false);
		
		mTvCanPower = (TextView) findViewById(R.id.tv_power);
		mTvCanPower.setTypeface(FontsUtils.getExpansivaTypeface(this));
		
		mTvLeftTemperature = (TextView) findViewById(R.id.tv_air_left_temperature);
		mTvLeftTemperature.setTypeface(FontsUtils.getExpansivaTypeface(this));
		
		mTvAirWindLevel = (TextView) findViewById(R.id.tv_air_wind_level);
		mTvAirWindLevel.setTypeface(FontsUtils.getExpansivaTypeface(this));
		
		mImgAirCircurlationMode = (ImageView) findViewById(R.id.img_air_circurlation_mode);
		mImgBlowMode = (ImageView) findViewById(R.id.img_blow_mode);
		
	}
	
	/**更新Can相关UI*/
	private void refreshCanViews() {
		CanInfoParcel canInfo = CanManager.getInstace().getCanInfo();
		mTvCanAuto.setSelected(canInfo.isAutoHighWind());
		
		mTvLeftTemperature.setText("" + canInfo.getTemperature());
		mTvAirWindLevel.setText("" + canInfo.getMwindSpeed());
		mTvAirWindLevel.setText("" + canInfo.getMwindSpeed());
		mImgAirCircurlationMode.setBackgroundResource(getAirCircurlationModeImgId(canInfo.getAirCircurlationMode()));
		mImgBlowMode.setBackgroundResource(getBlowModeImgId(canInfo.getMblowMode()));
	}
	
		// 车内空气循环模式
//		public final static List list = Arrays.asList("elment1", "element2");    
		
		/**吹风模式*/
		private final Map<Integer, Integer> mBlowMode = new HashMap<Integer, Integer>(){
			//匿名构造函数
			{
				put(0, R.drawable.blow_mode_head);
				put(1, R.drawable.blow_mode_all);
				put(2, R.drawable.blow_mode_foot);
			}
		};
		
		/**车内空气循环模式*/
		private final Map<Integer, Integer> AirCircurlationMode = new HashMap<Integer, Integer>(){
			//匿名构造函数
			{
				put(0, R.drawable.air_circurlation_mode_in);
				put(1, R.drawable.air_circurlation_mode_out);
			}
		};
	
	private int getAirCircurlationModeImgId(int circurlationMode) {
		Integer value = AirCircurlationMode.get(circurlationMode);
		if (value != null){
			return value;
		}
		return R.drawable.air_circurlation_mode_in;
	}
	
	private int getBlowModeImgId(int blowMode) {
		Integer value = mBlowMode.get(blowMode);
		if (value != null){
			return value;
		}
		return R.drawable.blow_mode_head;
	}

	/**初始化Activity Views*/
	private void initMainViews() {
		
		mLayoutBg = (LinearLayout) findViewById(R.id.layout_bg);
		mLayoutBg.setSelected(false);
		
		mLeftItems.add((OverlapLayout) findViewById(R.id.left_item1));
		mLeftItems.add((OverlapLayout) findViewById(R.id.left_item2));
		mLeftItems.add((OverlapLayout) findViewById(R.id.left_item3));
		for (int i = 0; i < mLeftItems.size();i ++){
			final int position = i;
			mLeftItems.get(i).setOnClickListener(this);
			mLeftItems.get(i).setOnDragListener(new OnDragListener() {
				
				@Override
				public boolean onDrag(View v, DragEvent event) {
					
//					Log.i(TAG1, "event.getAction() = " + event.getAction());
					switch (event.getAction()) {
					case DragEvent.ACTION_DRAG_STARTED:	//开始drag的时候
//						Log.i(TAG1, "DragEvent.ACTION_DRAG_STARTED");
						break;
					case DragEvent.ACTION_DRAG_ENTERED:	//drag进入时
						mLeftItemActives[position] = true;
						break;
					case DragEvent.ACTION_DRAG_EXITED:	//drag退出时
						mLeftItemActives[position] = false;
						break;
					case DragEvent.ACTION_DRAG_ENDED:	//完成drag时
						if (mLeftItemActives[position]){
							mLeftItemCtrl.setItemPosition(position, mLongClickItem);
							refreshLeftItemViews();
						}
//						Log.i(TAG1, "DragEvent.ACTION_DRAG_ENDED");
						break;
					/*case DragEvent.ACTION_DRAG_LOCATION: //drag在View里移动时
						break;
					case DragEvent.ACTION_DROP:
//						Log.i(TAG1, "DragEvent.ACTION_DRAG_ENDED");
						break;*/
					default:
						break;
					}
					return true;
					
				}
			});
		}
		refreshLeftItemViews();
		
		mImgPageIndexs.add((ImageView) findViewById(R.id.page_index1));
		mImgPageIndexs.add((ImageView) findViewById(R.id.page_index2));
		mImgPageIndexs.add((ImageView) findViewById(R.id.page_index3));
		
		mCircleMenuLayout = (CircleMenu) findViewById(R.id.id_menulayout);
		mCircleMenuLayout.setMenuAttr(mItemControl.getItemInfos(), 40, 350);
		mCircleMenuLayout.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public void itemClick(View view, int pos) {
				if (!mItemControl.onItemKeyDown(MainActivity.this, pos))
				{
					Toast.makeText(getApplicationContext(), mItemControl.getItemInfos().get(pos).getItemText(),
							Toast.LENGTH_SHORT).show();	
				}
			}

		});
		mCircleMenuLayout.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int page) {
				refreshPageIndexViews(page);
			}
		});
		
		mCircleMenuLayout.setOnMenuItemLongClickListener(new OnMenuItemLongClickListener() {
			
			@Override
			public void onItemLongClick(View view, int pos) {
				mLongClickItem = pos;
				for (int i = 0; i < mLeftItemActives.length; i++){
					mLeftItemActives[i] = false;
				}
			}
		});
		
	}

	/**更新页码指示*/
	private void refreshPageIndexViews(int page){
		if (mImgPageIndexs == null){
			return;
		}
		ImageView view;
		for (int i = 0; i < mImgPageIndexs.size(); i++) {
			view = mImgPageIndexs.get(i);
			if (i == page){
				view.setSelected(true);
			}else{
				view.setSelected(false);
			}
		}
	}
	
	/**更新左边按键图标*/
	private void refreshLeftItemViews(){
		List<Integer> itemInfo = mLeftItemCtrl.getLeftItemInfo();
		for(int i = 0; i < mLeftItems.size(); i++){
			mLeftItems.get(i).setImageResource(
					mItemControl.getItemInfos().get(itemInfo.get(i)).getItemImgId());
		}
	}
	
	@Override
	public void onClick(View v) {
		int item = 0;
		switch (v.getId()) {
		case R.id.left_item1:
			item = mLeftItemCtrl.getLeftItemInfo().get(0);
			if (!mItemControl.onItemKeyDown(MainActivity.this, item))
			{
				Toast.makeText(getApplicationContext(), mItemControl.getItemInfos().get(item).getItemText(),
						Toast.LENGTH_SHORT).show();	
			}
			break;
		case R.id.left_item2:
			item = mLeftItemCtrl.getLeftItemInfo().get(1);
			if (!mItemControl.onItemKeyDown(MainActivity.this, item))
			{
				Toast.makeText(getApplicationContext(), mItemControl.getItemInfos().get(item).getItemText(),
						Toast.LENGTH_SHORT).show();	
			}
			break;
		case R.id.left_item3:
			item = mLeftItemCtrl.getLeftItemInfo().get(2);
			if (!mItemControl.onItemKeyDown(MainActivity.this, item))
			{
				Toast.makeText(getApplicationContext(), mItemControl.getItemInfos().get(item).getItemText(),
						Toast.LENGTH_SHORT).show();	
			}
		default:
			break;
		}
	}

	class UiHandle extends Handler{
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case T.UpdateUiCmd.UPDATE_ALL:
				Log.i(TAG, "Launcher更新Can信息" + CanManager.getInstace().getCanInfo());
				refreshCanViews();
				break;
			}
		}
	}
	
}
