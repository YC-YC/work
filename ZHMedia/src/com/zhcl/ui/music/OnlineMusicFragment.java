/**
 * 
 */
package com.zhcl.ui.music;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zh.uitls.L;
import com.zhonghong.zhmedia.R;

/** 
 * @author zhonghong.chenli
 * @date 2015-11-19 下午10:01:24 
 */
public class OnlineMusicFragment extends AudioListFragmentABS {
	private final String tag = "OnlineMusicFragment";
	Context context;
	View view;
	WebView onlineMusic;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		L.e(tag, "onCreateView");
		if(view != null){
			ViewGroup parent = (ViewGroup) view.getParent();
			if(parent != null){
				parent.removeView(view);
			}
			loadAllSong("go");
			return view;
		}
		context = inflater.getContext();
		view = inflater.inflate(R.layout.online_music_fragment, container, false);
		initView(view); 
		loadAllSong("go");
		return view;
	} 
	
	private void initView(View view){ 
		onlineMusic = (WebView) view.findViewById(R.id.onlineMusic);
//		onlineMusic.setLayerType(View.LAYER_TYPE_SOFTWARE, null);  //设置不支持硬件加速
		WebSettings webSetting = onlineMusic.getSettings();// 得到浏览器设置
		onlineMusic.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		onlineMusic.getSettings().setAppCachePath("/mnt/sdcard/zhmusic/online");
		onlineMusic.getSettings().setAppCacheEnabled(true);
		onlineMusic.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				down(url);
				Log.e(tag, "onPageFinished url = " + url + " time = " + SystemClock.uptimeMillis());
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				Log.e(tag, "onPageStarted url = " + url + " time = " + SystemClock.uptimeMillis());
			}
		});
		webSetting.setJavaScriptEnabled(true);
		String url = "http://www.xiami.com/";
		onlineMusic.loadUrl(url); 
	}  
	
	
	private void down(String url){
		if(!url.contains("download")){
			return;
		}
		L.e(tag, "down!!");
		Uri resource = Uri.parse(encodeGB(url)); 
        DownloadManager.Request request = new DownloadManager.Request(resource); 
        request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI); 
        request.setAllowedOverRoaming(false); 
        //设置文件类型
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);
        //在通知栏中显示 
        request.setShowRunningNotification(true);
        request.setVisibleInDownloadsUi(true);
        //sdcard的目录下的download文件夹
        request.setDestinationInExternalPublicDir("/mnt/sdcard/zhmusic/download", "G3.mp4");
        request.setTitle("移动G3广告"); 
	}
	
	/**
	 * 如果服务器不支持中文路径的情况下需要转换url的编码。
	 * @param string
	 * @return
	 */
	public String encodeGB(String string)
	{
		//转换中文编码
		String split[] = string.split("/");
		for (int i = 1; i < split.length; i++) {
			try {
				split[i] = URLEncoder.encode(split[i], "GB2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			split[0] = split[0]+"/"+split[i];
		}
		split[0] = split[0].replaceAll("\\+", "%20");//处理空格
		return split[0];
	}

	
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if ((keyCode == KeyEvent.KEYCODE_BACK) && onlineMusic.canGoBack()) {
//			onlineMusic.goBack(); // goBack()表示返回WebView的上一页面
//			return true; 
//		}
//		return super.onKeyDown(keyCode, event);
//	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		L.e(tag, "doInBackground");
		return null;
	}

	@Override
	protected void onPostExecute(String result) { 
		// TODO Auto-generated method stub
		L.i(tag, "onPostExecute");
	}
}
