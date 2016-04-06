/**
 * Package com.chenli.widgettest
 * File Name:MyWidget.java
 * Date:2013-11-26����10:22:13
 * Copyright (c) 2013, jy10210409102@163.com All Rights Reserved.
 */
package com.zhcl.remote.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.zh.dao.SongInfo;
import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.media.CurrentPlayManager;
import com.zhcl.service.service.client.AudioPlayerClient;
import com.zhonghong.zhmedia.LocalMusicActivity;
import com.zhonghong.zhmedia.R;

/**
 * ClassName:MyWidget <br/> 
 * @author zhonghong.chenli        
 */
public class MusicWidget extends AppWidgetProvider{
	private static final String tag = MusicWidget.class.getSimpleName();
	private static final boolean DEBUG = true;
	/** 更新音乐小部件 action */
	public static final String UPDATE_MUSIC = "com.zhonghong.widget.UPDATE_MUSIC";
	/** 音乐操作action*/
	public static final String DO_MUSIC = "com.zhonghong.widget.DO_MUSIC";
 // 保存 widget 的id的HashSet，每新建一个 widget 都会为该 widget 分配一个 id。
    private static Set<Integer> idsSet = new HashSet<Integer>();
	Context context;
	@Override
	public void onReceive(final Context context, Intent intent) {
		super.onReceive(context, intent);
		String action = intent.getAction();
//		Log.e(tag, "onReceive:" + action);
//		Log.e(tag, "intent:" + intent);
		 if (UPDATE_MUSIC.equals(action)) {
//			 Log.e(tag, "数据改变更新");
            updateAllAppWidgets(context, AppWidgetManager.getInstance(context), idsSet);
            if(idsSet.isEmpty()){
//            	 Set<Integer> idsSetTemp = (Set<Integer>) readObjectFromPath("/mnt/sdcard/zhmusic/widgetID.db");
//                 if(idsSetTemp != null){
//                 	idsSet.addAll(idsSetTemp);
//                 }
                 int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context.getPackageName(), MusicWidget.class.getName()));
                 for(int i = 0; i < ids.length; i++){
                 	idsSet.add(ids[i]);
                 }
            }
        }else if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
            // “按钮点击”广播
            Uri data = intent.getData();
            int key = Integer.parseInt(data.getSchemeSpecificPart());
            MusicRemoteCtrl.getInstance().requestRemoteCtrl(key);
            L.e(tag, "收到消息");
        }
	}
	
	/**
	 * 读取widgetids
	 */
	
	// onUpdate() 在更新 widget 时，被执行，
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		L.e(tag, "onUpdate :" + appWidgetIds);
		for (int appWidgetId : appWidgetIds) {
			L.e(tag, "appWidgetId :" + appWidgetId);
            idsSet.add(Integer.valueOf(appWidgetId));
        }
//		writeObjectToPath(idsSet, "/mnt/sdcard/zhmusic/widgetID.db");
        prtSet();
        updateAllAppWidgets(context, AppWidgetManager.getInstance(context), idsSet);
	} 
	
	// 调试用：遍历set
    private void prtSet() {
        if (DEBUG) {
            int index = 0;
            int size = idsSet.size();
            Iterator<Integer> it = idsSet.iterator();
            Log.d(tag, "total:"+size);
            while (it.hasNext()) {
                Log.d(tag, index + " -- " + ((Integer)it.next()).intValue());
                index++;
            }
        }
    }
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// 当 widget 被删除时，对应的删除set中保存的widget的id
        for (int appWidgetId : appWidgetIds) {
        	L.e(tag, "appWidgetId :" + appWidgetId);
            idsSet.remove(Integer.valueOf(appWidgetId));
        }
//        writeObjectToPath(idsSet, "/mnt/sdcard/zhmusic/widgetID.db");
        prtSet();
//        T.Thread.radio = false;
		super.onDeleted(context, appWidgetIds);
		Log.e(tag, "onDeleted");
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Log.e(tag, "onDisabled");
		MusicRemoteCtrl.getInstance().requestRemoteCtrl(IRemote.MUSIC_WIDGET_DISENABLE);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.e(tag, "onEnabled");
		MusicRemoteCtrl.getInstance().requestRemoteCtrl(IRemote.MUSIC_WIDGET_ENABLE);
//		T.Thread.radio = true;
//		Intent it = new Intent(context, LoadServer.class);
//		context.startService(it);
	}

	private String lastMusicName;
	private boolean isHasPhoto;
	
	/**
	 * 是否允许更新非状态的UI
	 * 歌曲名字改变则允许更新
	 */
	private boolean isAllowUpdataDate(){
		SongInfo songInfo = CurrentPlayManager.getInstance().getSongInfo();
		String songPath = null;
		if (songInfo != null) {
			songPath = songInfo.getFileName();
		}
		if((lastMusicName != null && !lastMusicName.equals(songPath)) || (songPath != null && !songPath.equals(lastMusicName))){
			return true;
		}
		lastMusicName = songPath;
		return false;
	}
	/**
	 * 更新所有的 widget 
	 * 此函数待功能全后整改
	 */
    private void updateAllAppWidgets(Context context, AppWidgetManager appWidgetManager, Set set) {
        Log.d(tag, "updateAllAppWidgets(): size="+set.size());
        // widget 的id
        int appID;
        // 迭代器，用于遍历所有保存的widget的id
        Iterator<Integer> it = set.iterator();
        Utils.getInstance().startTime("updateAllAppWidgets");
        boolean isAllowUpdataSongUI = isAllowUpdataDate();
	        // 设置显示图片
        Bitmap bitmap = CurrentPlayManager.getInstance().getCurrentMusicImage();
        Bitmap targetBitmap = null;
        if(CurrentPlayManager.getInstance().isPlay()){
	        if(bitmap == null){
	        }else{
	        	if (bitmap != null) {
					final int maxW = 300;
					int scale = 1;
					if(bitmap.getWidth() > maxW){
						scale = bitmap.getWidth() / maxW;
						if(scale == 0){
							scale = 1;
						}
					}
					bitmap = Utils.getInstance().small(bitmap, scale);
					targetBitmap = Utils.getInstance().getRoundedCornerBitmap(bitmap);
	//				bitmap.recycle(); 
				} 
			}
        }
        Utils.getInstance().endUseTime("updateAllAppWidgets");
        SongInfo songInfo = CurrentPlayManager.getInstance().getSongInfo();
        int current = AudioPlayerClient.getInstance().getCurrentPosition();
        int end = AudioPlayerClient.getInstance().getDuration();
        String currentTime = Utils.getInstance().formatLongToTimeStr(current);
        String endTime = Utils.getInstance().formatLongToTimeStr(end);
        while (it.hasNext()) {
            appID = ((Integer)it.next()).intValue();    
            // 获取 example_appwidget.xml 对应的RemoteViews            
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.remote_widget_music_layout_zui/*remote_widget_music_layout*/);
            if(isAllowUpdataSongUI){
	            if(songInfo == null){
	            	remoteView.setTextViewText(R.id.name, "未选中歌曲");
	                remoteView.setTextViewText(R.id.singer, "未选中歌曲");
	            }else{
	            	remoteView.setTextViewText(R.id.name, songInfo.getTitle());
	                remoteView.setTextViewText(R.id.singer, songInfo.getSinger());
	            }
            }
            
            //更新播放暂停键
            if(CurrentPlayManager.getInstance().isPlay()){
            	remoteView.setImageViewResource(R.id.playPauseBut, R.drawable.widget_pause_xml);
            	//更新时间
            	remoteView.setTextViewText(R.id.widgetCurrentTime, currentTime);
            	remoteView.setTextViewText(R.id.widgetEndTime, endTime);
            	int currentPos = (int)(current * 1000 / end);
            	remoteView.setInt(R.id.widgetProgressBar, "setProgress", currentPos);
            }else{
            	remoteView.setImageViewResource(R.id.playPauseBut, R.drawable.widget_play_xml);
            	remoteView.setInt(R.id.widgetProgressBar, "setProgress", 0);
            }
            if(CurrentPlayManager.getInstance().isPlay()){
	            // 设置显示图片
	            if(targetBitmap == null){
	            	remoteView.setImageViewResource(R.id.widgetPhoto, R.drawable.widget_default_icon);
	//            	remoteView.setImageViewResource(R.id.remoteLayout, context.getResources().getColor(R.color.black));
	            }else{
					remoteView.setImageViewBitmap(R.id.widgetPhoto, targetBitmap);
	//				bitmap = Utils.getInstance().blurBitmap(bitmap , 30, 6/*9*/);
	//				remoteView.setImageViewBitmap(R.id.remoteLayout, bitmap);
				}
            }
            // 设置点击按钮对应的PendingIntent：即点击按钮时，发送广播。
            remoteView.setOnClickPendingIntent(R.id.preBut, getPendingIntent(context, IRemote.MUSIC_PLAYER_PRE));
            remoteView.setOnClickPendingIntent(R.id.playPauseBut, getPendingIntent(context, IRemote.MUSIC_PLAYER_PLAY_PAUSE));
            remoteView.setOnClickPendingIntent(R.id.nextBut, getPendingIntent(context, IRemote.MUSIC_PLAYER_NEXT));
            remoteView.setOnClickPendingIntent(R.id.widget_list, getPendingIntent(context, IRemote.PAGE_ENTER_LIST_PAGE)/*getPendingIntentToActivity(context, "list")*/); //列表
            remoteView.setOnClickPendingIntent(R.id.remoteLayout, getPendingIntent(context, IRemote.PAGE_ENTER_DEFAULT_PAGE)/*getPendingIntentToActivity(context, "main""main")*/);
            // 更新 widget
            appWidgetManager.updateAppWidget(appID, remoteView);    
        }        
    }

    /**
     * 广播事件
     */
    private PendingIntent getPendingIntent(Context context, int key) {
        Intent intent = new Intent();
        intent.setClass(context, MusicWidget.class);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        intent.setData(Uri.parse("custom:" + key));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        return pi;
    }
	
    /**
     * 页面进入
     */
    private PendingIntent getPendingIntentToActivity(Context context, String page) {
        Intent intent = new Intent(context, LocalMusicActivity.class);
        intent.putExtra("page", page);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        return pi;
    }
    
    
    /**
	 * 写入对象至文件
	 * 
	 * @param o
	 * @param path
	 * @return
	 */
	private boolean writeObjectToPath(Object o, String path) {
		ObjectOutputStream oo = null;
		try {
			oo = new ObjectOutputStream(new FileOutputStream(new File(path)));
			oo.writeObject(o);
			oo.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				oo.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * 从文件中读出对象
	 * 
	 * @param path
	 * @return
	 */
	private Object readObjectFromPath(String path) {
		Object result = null;
		ObjectInputStream oi = null;
		File file = new File(path);
		if(!file.exists()){
			return null;
		}
		try {
			oi = new ObjectInputStream(new FileInputStream(file));
			result = oi.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(oi != null){
					oi.close();
				}
			} catch (IOException e) { 
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
}
