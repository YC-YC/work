/**
 * 
 */
package com.zhcl.ui.music;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zh.broadcast.MountBroadcast.MountListener;
import com.zh.uitls.DelayExecute;
import com.zh.uitls.DelayExecute.Execute;
import com.zh.uitls.ThreadForeverManage;
import com.zh.uitls.ThreadForeverManage.ThreadForever;
import com.zhcl.filescanner.FileScanner;
import com.zhcl.media.CurrentPlayManager;
import com.zhcl.media.SongManager;
import com.zhonghong.zhmedia.R;

/** 
 * 进度显示类
 * @author zhonghong.chenli
 * @date 2015-11-21 下午9:09:06 
 */
public class ScanProgressView extends RelativeLayout implements ThreadForever{
	
	public ScanProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		init(context); 
	}
	
	public ScanProgressView(Context context) {
		super(context);
		this.context = context;
		init(context);
	}

	private static final String tag = ScanProgressView.class.getSimpleName();
	private TextView type;
	private TextView info;
	private Context context;
	private DelayExecute mDelayExecute;
	/** 多长时间执行一次 */
	private final int DELAY = 100;
	Handler handler;
	final int UPDATE = 1;
	/** 最后一次设备改变类型*/
	private String lastMountEvent;
	/** 最后一次设备改变路径 */
	private String lastMountEventPath;
	
	private void init(Context context){
		 LayoutInflater.from(context).inflate(R.layout.scan_progress_view, this);
		 initView();
		 mDelayExecute = new DelayExecute(DELAY, mExecute);
		 initHandler();
		 CurrentPlayManager.getInstance().addMountListener(mMountListener);
	}
	
	
	/**
	 * 设备状态监听
	 */
	private MountListener mMountListener = new MountListener(){
		@Override
		public void notifyMountInfo(String action, String path){
			lastMountEvent = action;
			lastMountEventPath = path;
		}
	};
	
	private void initView(){
		type = (TextView)findViewById(R.id.type);
		info = (TextView)findViewById(R.id.info);
	}
	
	@SuppressLint("HandlerLeak")
	private void initHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case UPDATE:
					uiDo();
					break;
				}
			}
		};
	}

	@Override
	public void doThread() {
		// TODO Auto-generated method stub
		if(mDelayExecute != null){
			mDelayExecute.execute();
		}
	}
	
	/**
	 * UI现实控制函数（如果随着逻辑变的复杂，则将逻辑部分抽出重写管理类）
	 */
	private void uiDo(){
		int scale = FileScanner.getInstance(null).getScanedDirPercent(null);
		int scanMode = FileScanner.getInstance(null).getScanMode();
		if(scanMode == FileScanner.SCANMODE_ADD || scanMode == FileScanner.SCANMODE_DEFUALT){			//增量扫描
			type.setText("查找更新");
			info.setText("······");
		}else if(scanMode == FileScanner.SCANMODE_MOUNT){	//设备变动扫描
			if(Intent.ACTION_MEDIA_MOUNTED.equals(lastMountEvent)){
				uIShow(mountEvent(lastMountEvent));
			}else{
				type.setText("移除设备");
				info.setText("······");
			}
		}else if(scanMode == FileScanner.SCANMODE_ALL){
			uIShow("全局扫描");
		}
	}
	
	private void uIShow(String tip){
		int scale = FileScanner.getInstance(null).getScanedDirPercent(null);
		if(scale < 100){
			type.setText("[" + tip + "] 扫描文件夹；下一步：歌曲信息处理");
			info.setText("进度：" + scale + "%" + "处理文件夹" + FileScanner.getInstance(null).getCurrentScanDir());
			Log.e(tag, "当前扫描类型：" + getScanModeStr(FileScanner.getInstance(null).getScanMode()));
			Log.e(tag, "正在扫描进度：" + scale);
			Log.e(tag, "正在扫描处理的文件夹：" + FileScanner.getInstance(null).getCurrentScanDir());
		}else{
//			Log.e(tag, "分类处理中····");
			type.setText("[" + tip + "] 歌曲信息处理中···");  
			int scale2 = SongManager.getInstance().getCurrentPercent();
			info.setText("进度：" + scale2 + "%" + " 处理文件：" + getSimpleName(SongManager.getInstance().getCurrentSongPath()));
		}
	}
	
	private String getSimpleName(String path){
		return new File(path).getName();
	}
	
	private Execute mExecute = new Execute() {
		@Override
		public void go() {
			handler.sendEmptyMessage(UPDATE);
		}
	};
	
	private String getScanModeStr(int mode){
		String result = null;
		switch(mode){
		case FileScanner.SCANMODE_ADD:
			result = "增量扫描";
			break;
		case FileScanner.SCANMODE_ALL:
			result = "全局扫描";
			break;
		case FileScanner.SCANMODE_DEFUALT:
			result = "默认扫描";
			break;
		case FileScanner.SCANMODE_MOUNT:
			result = "设备扫描";
			break;
		}
		return result;
	}
	
	
	private String mountEvent(String action){
		String result = null;
		if(Intent.ACTION_MEDIA_EJECT.equals(action)){
			result = "移除设备";
		}else if(Intent.ACTION_MEDIA_MOUNTED.equals(action)){
			result = "挂载设备";
		}
		return result;
	}
	
	/**
	 * 显示时
	 */
	public void show(){
		ThreadForeverManage.getInstance().register(this);	//注册自己
	}
	
	/**
	 * 隐藏时
	 */
	public void hide(){
		ThreadForeverManage.getInstance().cancel(this);	
	}


	@Override
	public void setVisibility(int visibility) {
		// TODO Auto-generated method stub
		super.setVisibility(visibility);
		if(visibility == View.VISIBLE){
			show();
		}else{
			hide();
		}
	}
}
