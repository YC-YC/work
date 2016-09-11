package com.zhonghong.launcher;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.zhonghong.mcuservice.McuHardKeyInfo;
import android.zhonghong.mcuservice.McuHardKeyProxy;
import android.zhonghong.mcuservice.RegistManager.IMcuHardKeyChangedListener;

import com.yc.external.PostFromBtMusic;
import com.yc.external.PostFromMusic;
import com.yc.external.PostFromRadio;
import com.zhonghong.base.UpdateUiBaseActivity;
import com.zhonghong.data.GlobalData;
import com.zhonghong.leftitem.LeftItemsCtrl;
import com.zhonghong.menuitem.AppstoreCommand;
import com.zhonghong.menuitem.BtCommand;
import com.zhonghong.menuitem.BtMusicCommand;
import com.zhonghong.menuitem.CarlifeCommand;
import com.zhonghong.menuitem.ExtendCommand;
import com.zhonghong.menuitem.ICallCommand;
import com.zhonghong.menuitem.ItemControl;
import com.zhonghong.menuitem.LEDCommand;
import com.zhonghong.menuitem.NaviCommand;
import com.zhonghong.menuitem.RadioCommand;
import com.zhonghong.menuitem.RecorderCommand;
import com.zhonghong.menuitem.SettingsCommand;
import com.zhonghong.menuitem.USBCommand;
import com.zhonghong.rate.OnStatusChangeListener;
import com.zhonghong.rate.RateInfo;
import com.zhonghong.rate.RateManager;
import com.zhonghong.rate.RateResultInfo;
import com.zhonghong.sdk.android.utils.PreferenceUtils;
import com.zhonghong.sublauncher.Page1Fragment;
import com.zhonghong.sublauncher.Page2Fragment;
import com.zhonghong.sublauncher.PageFragmentAdapter;
import com.zhonghong.utils.DialogManager;
import com.zhonghong.utils.FontsUtils;
import com.zhonghong.utils.L;
import com.zhonghong.utils.LoadID3Pic;
import com.zhonghong.utils.UpdateUiManager;
import com.zhonghong.utils.UpdateUiManager.UpdateViewCallback;
import com.zhonghong.widget.CircleMenu;
import com.zhonghong.widget.CircleMenu.OnMenuItemClickListener;
import com.zhonghong.widget.CircleMenu.OnMenuItemLongClickListener;
import com.zhonghong.widget.CircleMenu.OnPageChangeListener;
import com.zhonghong.widget.OverlapLayout;
import com.zhonghong.widget.RoundProgressbar;

public class MainActivity extends UpdateUiBaseActivity implements OnClickListener {

	private final String TAG = getClass().getSimpleName();
	
	
	private LinearLayout mLayoutBg;	//背景图层
	/**左边三个图标*/
	private List<OverlapLayout> mLeftItems = new ArrayList<OverlapLayout>();
	private LeftItemsCtrl mLeftItemCtrl;
	
	
	private CircleMenu mCircleMenuLayout;
	private ItemControl mItemControl;
	/**记录长按时Menu项*/
	private int mLongClickItem;
	/**记录左边三个按键的状态*/
	private boolean[] mLeftItemActives = new boolean[]{
		false, false, false	
	};
	
	/**主屏页面索引*/
	private List<ImageView> mMainScreenPageIndexs = new ArrayList<ImageView>();
	
	/**加锁、解锁*/
	private Button mLock;
	
	/**心率状态*/
	private ImageView mRateState;
	
	/**副屏*/
	private List<ImageView> mSubScreenPageIndexs = new ArrayList<ImageView>();
	private ViewPager mSubScreenPager;
	private List<Fragment> mSubScreenFragments;
	
	/**时间widget*/
	private TextView mTimeData, mTimeTime;
	
	/**媒体信息widget*/
	private PostFromMusic mPostFromMusic;
	private PostFromRadio mPostFromRadio;
	private PostFromBtMusic mPostFromBtMusic;
	
	private McuHardKeyProxy mMcuHardKeyProxy;
	private McuHardKeyChangedListener mHardKeyChangedListener;
	
	private LinearLayout mLayoutRadio, mLayoutMusic, mLayoutLoading, mLayoutRate, mLayoutBtMusic;
	private TextView mRadioTitle, mRadioCurFreq;
	private TextView mMusicTitle, mMusicArtist;
	private ImageView mMusicID3Pic;
	private LoadID3Pic mLoadID3Pic;
	private TextView mBtMusicTitle;
	
	private LinearLayout mLayoutRateReady, mLayoutRateChecking, mLayoutRateChecked;
	private TextView mRateReadyRestTime;
	private TextView mRateCheckedResult;
	private Button mRateCheckedRecheck, mRateCheckedMore;
	private RoundProgressbar mRateCheckingProgressbar;
	
	private final Handler mHandler = new Handler();
	
/*********生命周期************/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//设置不受状态栏，导航栏影响
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		
		initData();
		initMainViews();
		initSubMainViews();
		initLockViews();
		initWidgetViews();
		initTimeWidgetView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setLockState(PreferenceUtils.getBoolean(GlobalData.KEY_LOCK, false), false);
		refreshWidget(GlobalData.MediaWidgetType);
		refreshRateState();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		mMcuHardKeyProxy.unregistKeyInfoChangedListener(mHardKeyChangedListener);
		super.onDestroy();
	}
	
/*********View处理************/	
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		
		mLeftItemCtrl= new LeftItemsCtrl(this);
	//TODO 添加按键项处理	
		mItemControl = new ItemControl();
		mItemControl.setCommand(0, new NaviCommand());
		mItemControl.setCommand(1, new BtMusicCommand());
		mItemControl.setCommand(2, new RadioCommand());
		mItemControl.setCommand(3, new USBCommand());
		mItemControl.setCommand(4, new BtCommand());
		mItemControl.setCommand(5, new LEDCommand());
		mItemControl.setCommand(6, new ExtendCommand());
		mItemControl.setCommand(7, new CarlifeCommand());
		mItemControl.setCommand(8, new AppstoreCommand());
		mItemControl.setCommand(9, new RecorderCommand());
		mItemControl.setCommand(10, new ICallCommand());
		mItemControl.setCommand(11, new SettingsCommand());
		
		RateManager.getRateManager().setOnStatusChangeListener(new OnStatusChangeListener() {
			
			@Override
			public void onStatusChange(int oldStatus, int newStatus) {
				Log.i(TAG, "onStatusChange oldStatus = " + oldStatus + ", newStatus = " + newStatus);
				refreshWidget(GlobalData.MediaWidgetType);
				refreshRateState();
				mHandler.removeCallbacks(getRestTimeRunnable);
				mHandler.removeCallbacks(getCheckingRateRunnable);
				switch (newStatus) {
				case RateManager.STATUS_READY:
					mHandler.post(getRestTimeRunnable);
					break;
				case RateManager.STATUS_CHECKING:
					mRateCheckingProgressbar.setMax(RateManager.getRateManager().getCheckingStatus().getStatusRestTime());
					mHandler.post(getCheckingRateRunnable);
					break;
				case RateManager.STATUS_CHECKED:
					mRateCheckedResult.setText("" + RateManager.getRateManager().getCheckedStatus().getResultInfo().getAdverage());
					break;
				default:
					break;
				}
			}
		});
		
		mPostFromMusic = new PostFromMusic();
		mPostFromRadio = new PostFromRadio();
		mPostFromBtMusic = new PostFromBtMusic();
		
		mMcuHardKeyProxy = new McuHardKeyProxy();
		mHardKeyChangedListener = new McuHardKeyChangedListener();
		mMcuHardKeyProxy.registKeyInfoChangedListener(mHardKeyChangedListener);
	}

	private Runnable getRestTimeRunnable = new Runnable() {
		public void run() {
			int restTime = RateManager.getRateManager().getReadyStatus().getStatusRestTime();
			Log.i(TAG, "onStatusChange restTime = " + restTime);
			mRateReadyRestTime.setText("" + restTime/1000);
			mHandler.postDelayed(this, 1000);
		}
	};
	
	private Runnable getCheckingRateRunnable = new Runnable() {
		public void run() {
			int value = RateManager.getRateManager().getCheckingStatus().getLastRateValue();
			
			Log.i(TAG, "getLastRateValue = " + value);
			int progress = mRateCheckingProgressbar.getMax() - RateManager.getRateManager().getCheckingStatus().getStatusRestTime();
			mRateCheckingProgressbar.setProgress(progress);
			mRateCheckingProgressbar.setText("" + value);
			mHandler.postDelayed(this, 1000);
		}
	};
	
	/**
	 * 初始化主屏布局
	 */
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
		
		mMainScreenPageIndexs.add((ImageView) findViewById(R.id.page_index1));
		mMainScreenPageIndexs.add((ImageView) findViewById(R.id.page_index2));
		mMainScreenPageIndexs.add((ImageView) findViewById(R.id.page_index3));
		
		mCircleMenuLayout = (CircleMenu) findViewById(R.id.id_menulayout);
		mCircleMenuLayout.setMenuAttr(mItemControl.getItemInfos(), 40, 350);
		mCircleMenuLayout.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public void itemClick(View view, int pos) {
				if (!mItemControl.onItemKeyDown(MainActivity.this, pos))
				{
//					Toast.makeText(getApplicationContext(), mItemControl.getItemInfos().get(pos).getItemText(),
//							Toast.LENGTH_SHORT).show();	
				}
			}

		});
		mCircleMenuLayout.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int page) {
				refreshMainScreenPageIndexViews(page);
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
	
	/**
	 * 初始化副屏 Views
	 */
	private void initSubMainViews() {
		
		mSubScreenPageIndexs.add((ImageView) findViewById(R.id.sub_main_page_index1));
		mSubScreenPageIndexs.add((ImageView) findViewById(R.id.sub_main_page_index2));
		
		refreshSubScreenPageIndexViews(0);
		
		mSubScreenPager = (ViewPager) findViewById(R.id.viewpager_sub);
		mSubScreenFragments = new ArrayList<Fragment>();
		mSubScreenFragments.add(new Page1Fragment());
		mSubScreenFragments.add(new Page2Fragment());
		
		mSubScreenPager.setAdapter(new PageFragmentAdapter(getSupportFragmentManager(), mSubScreenFragments));
		mSubScreenPager.setOnPageChangeListener(mPageChangeListener);
	}
	
	/**
	 * 锁屏功能
	 */
	private void initLockViews() {
		mLock = (Button) findViewById(R.id.lock);
		setLockState(PreferenceUtils.getBoolean(GlobalData.KEY_LOCK, false), true);
		
		mLock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toggleLock();
			}
		});
//		LockWndHelper.getInstaces().createDrawerLayoutView(this);
//		LockWndHelper.getInstaces().setOnLockClickListener(new OnLockClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				toggleLock();
//			}
//		});
	}
	
	/**
	 * 初始化媒体信息Widget布局
	 */
	private void initWidgetViews(){
		mLayoutRadio = (LinearLayout) findViewById(R.id.layout_radio);
		mLayoutRadio.setOnClickListener(this);
		mRadioTitle = (TextView) findViewById(R.id.radio_title);
		mRadioCurFreq = (TextView) findViewById(R.id.radio_curfreq);
		
		mLayoutMusic = (LinearLayout) findViewById(R.id.layout_music);
		mLayoutMusic.setOnClickListener(this);
		mMusicTitle = (TextView) findViewById(R.id.music_title);
		mMusicArtist = (TextView) findViewById(R.id.music_artist);
		mMusicID3Pic = (ImageView) findViewById(R.id.music_id3pic);
		
		mLayoutBtMusic = (LinearLayout) findViewById(R.id.layout_btmusic);
		mLayoutBtMusic.setOnClickListener(this);
		mBtMusicTitle = (TextView) findViewById(R.id.btmusic_title);
		
		mLayoutLoading = (LinearLayout) findViewById(R.id.layout_media_loading);
		
		mLayoutRate = (LinearLayout) findViewById(R.id.layout_rate);
		mRateState = (ImageView) findViewById(R.id.rate_state);
		mLayoutRateReady = (LinearLayout) findViewById(R.id.layout_rate_ready);
		mLayoutRateChecking = (LinearLayout) findViewById(R.id.layout_rate_checking);
		mLayoutRateChecked = (LinearLayout) findViewById(R.id.layout_rate_checked);

		mRateReadyRestTime = (TextView) findViewById(R.id.rate_ready_resttime);
		mRateCheckedResult = (TextView) findViewById(R.id.rate_checked_result);
	
		mRateCheckedRecheck = (Button) findViewById(R.id.rate_checked_recheck);
		mRateCheckedRecheck.setOnClickListener(this);
		mRateCheckedMore = (Button) findViewById(R.id.rate_checked_more);
		mRateCheckedMore.setOnClickListener(this);
		mRateCheckingProgressbar = (RoundProgressbar) findViewById(R.id.rate_checking);
	}

	/**
	 * 初始化时间widget布局
	 */
	private void initTimeWidgetView() {
		mTimeData = (TextView) findViewById(R.id.time_data);
		mTimeData.setTypeface(FontsUtils.getExpansivaTypeface(this));
		mTimeTime = (TextView) findViewById(R.id.time_time);
		mTimeTime.setTypeface(FontsUtils.getExpansivaTypeface(this));
		refreshTimeView();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				refreshTimeView();
				mHandler.postDelayed(this, 1000);
			}
		}, 1000);
	}

	/**更新主屏页码指示*/
	private void refreshMainScreenPageIndexViews(int page){
		if (mMainScreenPageIndexs == null){
			return;
		}
		ImageView view;
		for (int i = 0; i < mMainScreenPageIndexs.size(); i++) {
			view = mMainScreenPageIndexs.get(i);
			if (i == page){
				view.setSelected(true);
			}else{
				view.setSelected(false);
			}
		}
	}
	
	/**更新副屏页码指示*/
	private void refreshSubScreenPageIndexViews(int page){
		if (mSubScreenPageIndexs == null){
			return;
		}
		ImageView view;
		for (int i = 0; i < mSubScreenPageIndexs.size(); i++) {
			view = mSubScreenPageIndexs.get(i);
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
	
	/**
	 * 更新媒体widget信息
	 * @param type 
	 */
	private void refreshWidget(int type){
		Log.i(TAG, "refreshWidget type = " + type);
		clearAllWidget();
		if (!RateManager.getRateManager().isValued()){
			GlobalData.MediaWidgetType = type;
			switch (type) {
			case GlobalData.MEDIA_WIDGET_TYPE_RADIO:
				setWidgetRadio();
				break;
			case GlobalData.MEDIA_WIDGET_TYPE_MUSIC:
				
				setWidgetMusic();
				break;
			case GlobalData.MEDIA_WIDGET_TYPE_BTMUSIC:
				setWidgetBtMusic();
				break;
			default:
				setWidgetLoading();
				break;
			}
		}
		else{
			setMediaWidgetRate();
		}
	}
	
	private void clearAllWidget(){
		mLayoutMusic.setVisibility(View.GONE);
		mLayoutLoading.setVisibility(View.GONE);
		mLayoutRadio.setVisibility(View.GONE);
		mLayoutBtMusic.setVisibility(View.GONE);
		
		mLayoutRate.setVisibility(View.GONE);
	}
	private void setWidgetRadio(){
		mLayoutRadio.setVisibility(View.VISIBLE);
		mRadioTitle.setText(GlobalData.Radio.TITLE);
		mRadioCurFreq.setText(getRadioFreqStr(GlobalData.Radio.CUR_FREQ));
	}
	
	private String getRadioFreqStr(String freqStr){
		String result = null;
		try {
			Integer freq = Integer.valueOf(freqStr);
			if (freq > 1700){
				result = "" + freq/100 + "." + (freq%100)/10 + "" + freq%10 + "MHz";
			}
			else{
				result = "" + freq + "KHz";
			}
		} catch (Exception e) {
		}
			return result;
	}
	
	private void setWidgetMusic(){
		mLayoutMusic.setVisibility(View.VISIBLE);
		mMusicTitle.setText(GlobalData.Music.TITLE);
		mMusicArtist.setText(GlobalData.Music.ARTISE);
		if (mLoadID3Pic == null){
			mLoadID3Pic = new LoadID3Pic();
		}
		L.startTime("解析ID3信息");
		mLoadID3Pic.displayImage(GlobalData.Music.CUR_PLAY_PATH, mMusicID3Pic, BitmapFactory.decodeResource(getResources(), R.drawable.music_default_album));
		L.endUseTime("解析ID3信息");
	}
	
	private void setWidgetBtMusic(){
		mLayoutBtMusic.setVisibility(View.VISIBLE);
		mBtMusicTitle.setText(GlobalData.BtMusic.TITLE);
	}
	
	private void setWidgetLoading(){
		mLayoutLoading.setVisibility(View.VISIBLE);
	}
	
	private void setMediaWidgetRate(){
		mLayoutRate.setVisibility(View.VISIBLE);
		
		mLayoutRateReady.setVisibility(View.GONE);
		mLayoutRateChecking.setVisibility(View.GONE);
		mLayoutRateChecked.setVisibility(View.GONE);
		switch (RateManager.getRateManager().getCurStatusInt()) {
		case RateManager.STATUS_READY:
			mLayoutRateReady.setVisibility(View.VISIBLE);
			break;
		case RateManager.STATUS_CHECKING:
			mLayoutRateChecking.setVisibility(View.VISIBLE);
			break;
		case RateManager.STATUS_CHECKED:
			mLayoutRateChecked.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	
	/**
	 * 刷新时间widget
	 */
	private void refreshTimeView(){
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd-HH:mm");       
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
		String str = formatter.format(curDate);
		String[] strs = str.split("-");
		if (mTimeData != null){
			mTimeData.setText(strs[0]);
		}
		if (mTimeTime != null){
			mTimeTime.setText(strs[1]);
		}
	}
	
	private void refreshRateState(){
		if (RateManager.getRateManager().isValued()){
			mRateState.setSelected(true);
		}
		else{
			mRateState.setSelected(false);
		}
	}
	
/**********事件处理***********/	
	
	@Override
	public void onClick(View v) {
		//TODO 按键处理
		int item = 0;
		switch (v.getId()) {
		case R.id.left_item1:
			item = mLeftItemCtrl.getLeftItemInfo().get(0);
			if (!mItemControl.onItemKeyDown(MainActivity.this, item))
			{
//				Toast.makeText(getApplicationContext(), mItemControl.getItemInfos().get(item).getItemText(),
//						Toast.LENGTH_SHORT).show();	
			}
			break;
		case R.id.left_item2:
			item = mLeftItemCtrl.getLeftItemInfo().get(1);
			if (!mItemControl.onItemKeyDown(MainActivity.this, item))
			{
//				Toast.makeText(getApplicationContext(), mItemControl.getItemInfos().get(item).getItemText(),
//						Toast.LENGTH_SHORT).show();	
			}
			break;
		case R.id.left_item3:
			item = mLeftItemCtrl.getLeftItemInfo().get(2);
			if (!mItemControl.onItemKeyDown(MainActivity.this, item))
			{
//				Toast.makeText(getApplicationContext(), mItemControl.getItemInfos().get(item).getItemText(),
//						Toast.LENGTH_SHORT).show();	
			}
		case R.id.layout_radio:
			new RadioCommand().execute(this);
			break;
		case R.id.layout_music:
			new USBCommand().execute(this);
			break;
		case R.id.layout_btmusic:
			new BtMusicCommand().execute(this);
			break;
		case R.id.rate_checked_recheck:
			RateManager.getRateManager().setCurStatus(RateManager.getRateManager().getReadyStatus());
			break;
		case R.id.rate_checked_more:
			RateResultInfo info = RateManager.getRateManager().getCheckedStatus().getResultInfo();
			Log.i(TAG, "getRateResultInfo = " + info.toString());
			DialogManager.getInstance().showRateResultDialog(this, info);
			break;
		default:
			break;
		}
	}
	

	
	private void toggleLock(){
		if (mLock.isSelected()){
			setLockState(false, true);
		}
		else{
			setLockState(true, true);
		}
	}
	
	/**
	 * @param lockState	加锁状态
	 * @param save 是否保存（加广播）
	 */
	private void setLockState(boolean lockState, boolean save){
		if (mLock != null){
			mLock.setSelected(lockState);
		}
		if (save){
			PreferenceUtils.putBoolean(GlobalData.KEY_LOCK, lockState);
			sendLockBroadcast(lockState);
		}
	}
	
	/**
	 * 发送加锁广播
	 * @param lock
	 */
	private void sendLockBroadcast(boolean lock){
		Intent it = new Intent();
		it.setAction(GlobalData.ACTION_LOCK);
		it.putExtra("lock", lock);
		this.sendBroadcast(it);
	}

/**********私有类***********/
	
	private android.support.v4.view.ViewPager.OnPageChangeListener mPageChangeListener = new android.support.v4.view.ViewPager.OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int page) {
			refreshSubScreenPageIndexViews(page);
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}
	};
	
	private UpdateViewCallback mUpdateViewCallback = new UpdateViewCallback() {
		
		@Override
		public void onUpdate(final int cmd) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					
					switch (cmd) {
					case UpdateUiManager.CMD_UPDATE_DEFAULT:
						refreshWidget(GlobalData.MEDIA_WIDGET_TYPE_DEFAULT);
						break;
					case UpdateUiManager.CMD_UPDATE_RADIO_INFO:
						refreshWidget(GlobalData.MEDIA_WIDGET_TYPE_RADIO);
						break;
					case UpdateUiManager.CMD_UPDATE_MUSIC_INFO:
						refreshWidget(GlobalData.MEDIA_WIDGET_TYPE_MUSIC);
						break;
					case UpdateUiManager.CMD_UPDATE_BTMUSIC_INFO:
						refreshWidget(GlobalData.MEDIA_WIDGET_TYPE_BTMUSIC);
						break;
					}
				}
			});
		}
	};

	@Override
	protected UpdateViewCallback getUpdateViewCallback() {
		return mUpdateViewCallback;
	}
	
	/**MCU按键监听*/
	private class McuHardKeyChangedListener implements IMcuHardKeyChangedListener{

		@Override
		public void notify(int[] changeCMDs, final McuHardKeyInfo hardkey) {
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					int keycode = hardkey.getKeyCode();
					Log.i(TAG, "mcu info keycode = " + keycode);
//					Log.i(TAG, "mcu info KeyStatus = " + hardkey.getKeyStatus());
					if (hardkey.getKeyStatus() == 1){
						RateManager.getRateManager().inputRate(new RateInfo(hardkey.getKeyCode(), SystemClock.elapsedRealtime()));
					}
				}
			});
		}
		
	}
	
}
