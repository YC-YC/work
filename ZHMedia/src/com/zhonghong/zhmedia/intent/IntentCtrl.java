/**
 * 
 */
package com.zhonghong.zhmedia.intent;


import java.util.Collections;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.zh.dao.SongInfo;
import com.zh.uitls.Utils;
import com.zhcl.media.CurrentPlayManager;
import com.zhcl.media.SongInfoFactory;
import com.zhcl.media.SongManager;
import com.zhcl.ui.music.HostCallBack;
import com.zhonghong.log.L;

/**
 * 此类需要在activity中初始化
 * @author chen
 */
public class IntentCtrl {
	private static final String tag = IntentCtrl.class.getSimpleName();
	private IntentCtrl(){}
	private static IntentCtrl mIntentCtrl;
	public static IntentCtrl getInstance(){
		if(mIntentCtrl == null){
			mIntentCtrl = new IntentCtrl();
		}
		return mIntentCtrl;
	}
	
	/**
	 * Intent处理
	 * @param activity
	 */
	public void intentDo(Activity activity){
		if(activity == null){
			L.w(tag, "activity == null");
			return ;
		}
		do{
			if(getDatePath(activity)){		//通过intent打开文件
				break;
			}
		}while(false);
	}
	
	/**
	 * 获得媒体播放路径
	 * @return
	 */
	private boolean getDatePath(Activity activity){
		Intent it = activity.getIntent();
		Uri uri = it.getData();
		if(uri == null){
			return false;
		}
		String filePath = getUriValidPathOnScheme(uri);
		if(filePath == null){
			Log.i(tag, "文件路径为空"); 
			return false;
		}
		Log.i(tag, "文件路径：" + filePath);
		
		if(isWebMedia(filePath)){  //如果是网络音频
			L.i(tag, "是网络资源");
		}else{
			//请求播放音频
			playMusic(activity, filePath);
		}
		return true;
	}
	
	/**
	 * 播放歌曲
	 * 如果没扫描过，直播选中的这一首，如果已经扫描过，则播放文件夹下的歌曲
	 */
	private void playMusic(Activity activity, String path){
		//搜索当前歌曲
		if(path == null || "".equals(path.trim())){
			L.w(tag, "path = " + path);
		}
		L.e(tag, "path : " + Environment.getExternalStorageDirectory().getAbsolutePath());
		//可能还未扫描出目录结构
		//先判断是否存在指定目录，如果存在则取出所有歌曲，并从集合中找出指定歌曲
		path = path.replace(Environment.getExternalStorageDirectory().getAbsolutePath(), "/mnt/sdcard");
		L.e(tag, "menu: " + Utils.getInstance().getDirPathFromfile(path));
		Vector<SongInfo> songMenu = SongManager.getInstance().getSonginfosFromDirPath(Utils.getInstance().getDirPathFromfile(path));
		SongInfo song = null;
		if(songMenu == null){	//只播这一首
			L.i(tag, "songMenu is null");
			song = SongInfoFactory.CreateSongInfo(path);
			songMenu = new Vector<SongInfo>();
			songMenu.add(song);
		}else{
			song = SongManager.getInstance().querySongFromList(path, songMenu);
			if(song == null){
				L.i(tag, "song is null");
				song = SongInfoFactory.CreateSongInfo(path);
				songMenu.add(song);
				Collections.sort(songMenu);
			}
		}
		CurrentPlayManager.getInstance().playFromSongMenu(song, songMenu);
		//进入当前播放界面
		enterCurrentPage(activity);
	}
	
	
	/**
	 * 进入当前播放界面
	 */ 
	private void enterCurrentPage(Activity activity){
		if(!(activity instanceof HostCallBack)){
			L.w(tag, "acitivity 异常");
			return ;
		}
		((HostCallBack)activity).connString(HostCallBack.REQUESY_SHOW_CURRENT_PLAYPAGE, null);
	}
	
	/**
	 * 返回路劲
	 */
	private String getUriValidPathOnScheme(Uri uri){
		String scheme = uri.getScheme();
		String result = null;
		if(scheme != null){
			if(scheme.equals("file")){
				result = uri.getPath();
			}else if(scheme.equals("http") || scheme.equals("https")){
				result = uri.toString();
			}
		}
		return result;
	}
	
	/**
	 * 是否是网路资源
	 */
	private boolean isWebMedia(String path){
		return (path.length() > 5 && path.substring(0, 5).contains("http"));
	}
	
}
