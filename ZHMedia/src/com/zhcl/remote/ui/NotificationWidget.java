/**
 * 
 */
package com.zhcl.remote.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.RemoteViews;

import com.zh.dao.SongInfo;
import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.media.CurrentPlayManager;
import com.zhonghong.zhmedia.LocalMusicActivity;
import com.zhonghong.zhmedia.R;

/**
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author zhonghong.chenli
 * @date 2016-1-14 下午9:45:53
 */
public class NotificationWidget implements IRemote {
	private static final String tag = NotificationWidget.class.getSimpleName();
	private static NotificationWidget mNotificationWidget;
	private NotificationWidget() {
	}
	public static NotificationWidget getInstance() {
		if (mNotificationWidget == null) {
			mNotificationWidget = new NotificationWidget();
		}
		return mNotificationWidget;
	}

	@Override
	public void requestRemoteCtrl(int cmd) {

	}

	@Override
	public void requestUpdateRemoteUI(Context context) {
		notifyction(context);
	}
	private static final int NOTIFICATION_FLAG = 1;  

	private void notifyction(Context context) {
		L.e(tag, "notifyction!!!");
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);  
		// "自定义通知：您有新短信息了，请注意查收！", System.currentTimeMillis());
		Notification myNotify = new Notification();
		myNotify.icon = R.drawable.widget_qqmusic_default_album_small;
//		myNotify.tickerText = "TickerText:您有新短消息，请注意查收！";
		myNotify.when = System.currentTimeMillis();
		myNotify.flags = Notification.FLAG_NO_CLEAR;// 不能够自动清除
		RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.remote_notify_music_layout);
		updataUI(context, remoteView);// 更新UI
		myNotify.contentView = remoteView;
		PendingIntent contentIntent = getPendingIntentToActivity(context, "main");
		myNotify.contentIntent = contentIntent;
		manager.notify(NOTIFICATION_FLAG, myNotify);

	}
	
	
	public void updataUI(Context context, RemoteViews remoteView){
		 SongInfo songInfo = CurrentPlayManager.getInstance().getSongInfo();
         if(songInfo == null){
         	remoteView.setTextViewText(R.id.name, "未选中歌曲");
             remoteView.setTextViewText(R.id.singer, "未选中歌曲");
         }else{
         	remoteView.setTextViewText(R.id.name, songInfo.getTitle());
             remoteView.setTextViewText(R.id.singer, songInfo.getSinger());
         }
         
         //更新播放暂停键
         if(CurrentPlayManager.getInstance().isPlay()){
         	remoteView.setImageViewResource(R.id.playPauseBut, R.drawable.landscape_player_btn_pause_xml);
         }else{
         	remoteView.setImageViewResource(R.id.playPauseBut, R.drawable.landscape_player_btn_play_xml);
         }
         // 设置显示图片
         Bitmap bitmap = CurrentPlayManager.getInstance().getCurrentMusicImage();
         if(bitmap == null){
         	remoteView.setImageViewResource(R.id.widgetPhoto, R.drawable.widget_qqmusic_default_album_large);
//         	remoteView.setImageViewResource(R.id.remoteLayout, context.getResources().getColor(R.color.black));
         }else{
				remoteView.setImageViewBitmap(R.id.widgetPhoto, Utils.getInstance().getRoundedCornerBitmap(bitmap));
//				bitmap = Utils.getInstance().blurBitmap(bitmap , 30, 6/*9*/);
//				remoteView.setImageViewBitmap(R.id.remoteLayout, bitmap);
			}
         
         // 设置点击按钮对应的PendingIntent：即点击按钮时，发送广播。
         remoteView.setOnClickPendingIntent(R.id.preBut, getPendingIntent(context, IRemote.MUSIC_PLAYER_PRE));
         remoteView.setOnClickPendingIntent(R.id.playPauseBut, getPendingIntent(context, IRemote.MUSIC_PLAYER_PLAY_PAUSE));
         remoteView.setOnClickPendingIntent(R.id.nextBut, getPendingIntent(context, IRemote.MUSIC_PLAYER_NEXT));
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
}
