/**
 * 
 */
package com.zhcl.media;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zh.dao.Album;
import com.zh.dao.Singer;
import com.zh.dao.SongInfo;
import com.zh.uitls.L;
import com.zh.uitls.NetResource;
import com.zh.uitls.NetResource.LrcNet;
import com.zh.uitls.Utils;
import com.zhcl.filescanner.LocalFileCacheManager;

/**
 * 图片处理 ，包括歌手，歌曲，专辑图片的缓存处理
 * 歌词图片需完全对接自己的后台！
 * @author zhonghong.chenli
 * @date 2015-11-24 下午11:10:57
 */
public class ImageManager {
	
	public interface NetWorkLrcAndImageListener{
		/**
		 * 歌词下载结束回调，失败返回空
		 * @param lrcPath
		 */
		public void completeLRC(String lrcPath);
		
		/**
		 * 图片下载结束回调，失败返回空
		 * @param imagePath
		 */
		public void completeImage(String imagePath);
	}
	
	private static final String tag = "ImageManager";
	public static final String CACHEDIR_IMAGE = "/mnt/sdcard/zhmusic/image";
	public static final String CACHEDIR_LRC = "/mnt/sdcard/zhmusic/lrc";
	private static ImageManager mImageManager;
	
	public static ImageManager getInstance() {
		if (mImageManager == null) {
			mImageManager = new ImageManager();
		}
		return mImageManager;
	}
	Handler handlerImage;
	/** 避免堵塞扫描线程，加此handler*/
	HandlerThread downLoadThread;
	Handler handlerDownLoad;
	private ImageManager() {
		File imageDir = new File(CACHEDIR_IMAGE);
		File lrcDir = new File(CACHEDIR_LRC);
		if(!imageDir.exists()){
			imageDir.mkdirs();
		}
		if(!lrcDir.exists()){
			lrcDir.mkdirs();
		}
		
		//使用加载线程是为了避免多线程操作songinfo异常
		handlerImage = new Handler(LocalFileCacheManager.getInstance().getHandlerThread().getLooper()/*handlerThread.getLooper()*/){
			@Override
			public void handleMessage(Message msg) {
				handleMessageDo(msg);
			}
		};
		
		
		downLoadThread = new HandlerThread("download");
		downLoadThread.start(); //创建HandlerThread后一定要记得start()
		handlerDownLoad = new Handler(downLoadThread.getLooper()){
			@Override
			public void handleMessage(Message msg) {
				handleMessageDo(msg);
			}
		};
	}

	
	/**
	 * 销毁
	 */
	public void onActivityDestroy(){
		if(handlerDownLoad != null){
			handlerDownLoad.removeMessages(1);
		}
		if(handlerImage != null){
			handlerImage.removeMessages(1);
		}
		L.e(tag, "onActivityDestroy");
	}
	/**
	 * 消息队列处理
	 * @param msg
	 * @return
	 */
	private void handleMessageDo(Message msg){
		if(msg.obj != null){
			if(msg.obj instanceof Object[]){
				Object[] objs = (Object[])msg.obj;
				if(objs[0] instanceof Singer){
					loadWriteSingerImage((Singer)objs[0], (ImageView)objs[1], (Handler)objs[2]);	//加载并写入图片
				}else if(objs[0] instanceof Album){
					loadWriteAlbumImage((Album)objs[0], (ImageView)objs[1], (Handler)objs[2]);
				}else if(objs[0] instanceof SongInfo){
					loadWriteNetworkLrcAndImage((SongInfo)objs[0], (NetWorkLrcAndImageListener)objs[1], (Handler)objs[2], (Context)objs[3]);
				}
			}
		}
	}
	
	/** 
     * 保存文件 
     * @param bm 
     * @param fileName 
     * @throws IOException 
     */  
    public void saveFile(Bitmap bm, String fileName) throws IOException { 
        File dirFile = new File(fileName).getParentFile();  
        if(!dirFile.exists()){  
            dirFile.mkdirs();
        }  
        File myCaptureFile = new File(fileName);  
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));  
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);  
        bos.flush();  
        bos.close();  
    }
	
    
    /**
 	 * 异步加载歌手图片
 	 * @param path
 	 * @param imageView
 	 * @param mtHandler
 	 */
 	private void asyncLoadImage(final String path, final ImageView imageView, final Handler mtHandler){
 		if(path != null){
 			mtHandler.post(new Runnable() {
 				@Override
 				public void run() {
 					// TODO Auto-generated method stub
 					ImageLoader.getInstance().displayImage("file:///" + path, imageView, animateFirstListener);
 				}
 			});
 		}
 	}
 	
 	 private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
			static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				if (loadedImage != null) {
					ImageView imageView = (ImageView) view;
					boolean firstDisplay = !displayedImages.contains(imageUri);
					if (firstDisplay) {
						FadeInBitmapDisplayer.animate(imageView, 500);
						displayedImages.add(imageUri);
					}
				}
			}
		}
    
	/**
	 * 歌手封面处理
	 * 只允许在消息队列中处理
	 */
	private boolean loadWriteSingerImage(final Singer singer, final ImageView imageView,  final Handler mtHandler){
		String path = getSingerCachePath(singer);
		boolean isexists = new File(path).exists();
		Bitmap bitmap = null;
		try {
			if(!isexists){			//如果缩略图存在就不加载了
				Set<SongInfo> songSet = singer.getmSongSet();
					for (SongInfo mSongInfo : songSet) {
						bitmap = Utils.getInstance().createAlbumArt(mSongInfo.getFileName());
						if (bitmap != null) {
							break;
						}
					}

					if (bitmap != null) {
						L.e(tag, "准备生成歌手缩略图path ：" + path);
						saveFile(bitmap, path);
						bitmap.recycle();
						isexists = true;
						asyncLoadImage(path, imageView, mtHandler);
					} else {
						return false;
					}
				}
		} catch (IOException e) {
			e.printStackTrace();
			return false; 
		}
		return true;
	} 
	
    /**
	 * 歌手封面处理
	 * 只允许在消息队列中处理
	 */
	private boolean loadWriteAlbumImage(Album album, final ImageView imageView,  final Handler mtHandler){
		String path = getAlbumCachePath(album);
		boolean isexists = new File(path).exists();
		Bitmap bitmap = null; 
		try {
			if(!isexists){			//如果缩略图存在就不加载了
				Set<SongInfo> songSet = album.getmSongSet();
				for(SongInfo mSongInfo : songSet){
					bitmap = Utils.getInstance().createAlbumArt(mSongInfo.getFileName());
					if(bitmap != null){
						break;
					}
				}
				
				if(bitmap != null){
					L.e(tag, "准备生成歌手缩略图path ：" + path);
					saveFile(bitmap, path);
					bitmap.recycle();
					isexists = true;
					asyncLoadImage(path, imageView, mtHandler);
				}else{
					return false;
				}
			}
			//asyncLoadImage(path, imageView, mtHandler);
		} catch (IOException e) {
			e.printStackTrace();
			return false; 
		}
		return true;
	} 
	
	/**
	 * 加载网络歌词和图片
	 */
	private boolean loadWriteNetworkLrcAndImage(SongInfo songInfo, NetWorkLrcAndImageListener mNetWorkLrcAndImageListener, Handler handler, Context context){
		loadLrcAndImage(context, songInfo, mNetWorkLrcAndImageListener, handler);
		return false;
	}
	
	/**
	 * 获得歌手缓存路径
	 */
	private String getSingerCachePath(Singer singer){
		return CACHEDIR_IMAGE + File.separator + singer.getImageCacheName();
	}
	
	 /**
     * 获取专辑封面缓存路径
     */
    private String getAlbumCachePath(Album album){
		return CACHEDIR_IMAGE + File.separator + album.getImageCacheName();
	}
    
    /**
     * 获取lrc缓存路径
     */
    public String getLrcCachePath(SongInfo songInfo){
    	return CACHEDIR_LRC + File.separator + songInfo.getLrcCacheName();
    }
	
    /**
     * 获取歌曲缓存文件
     */
    private String getSongInfoImageCachePath(SongInfo songInfo){
    	return CACHEDIR_IMAGE + File.separator + songInfo.getImageCacheName();
    	
    }
    
	/**
	 * 歌手封面缓存 策略
	 */
	public String loadSingerImage(Singer singer, ImageView imageView, Handler handler){
		L.e(tag, "loadSingerImage");
		Message msg = handlerImage.obtainMessage();
		msg.what = 1;
		msg.obj = new Object[]{singer, imageView, handler};
		handlerImage.sendMessage(msg);
		return getSingerCachePath(singer);
	}
	
	
	
	
	/**
	 * 歌手封面缓存 策略
	 */
	public String loadAlbumImage(Album album, ImageView imageView, Handler handler){
		L.e(tag, "loadAlbumImage");
		Message msg = handlerImage.obtainMessage();
		msg.what = 1;
		msg.obj = new Object[]{album, imageView, handler};
		handlerImage.sendMessage(msg);
		return getAlbumCachePath(album);
	}
	
	/**
	 * 网络加载歌词和图片
	 * @param songInfo
	 * @param mNetWorkLrcAndImageListener
	 * @return 【0】为歌词 【1】为图片
	 */
	public String[] loadNetWorkLRCAndImage(SongInfo songInfo, NetWorkLrcAndImageListener mNetWorkLrcAndImageListener, Handler handler, Context context){
		L.e(tag, "loadNetWorkLRCAndImage");
		Message msg = handlerDownLoad.obtainMessage();
		msg.what = 1;
		msg.obj = new Object[]{songInfo, mNetWorkLrcAndImageListener, handler, context};
		handlerDownLoad.sendMessage(msg);
		return new String[]{getLrcCachePath(songInfo), getSongInfoImageCachePath(songInfo)};
	}
	
	
	/**
	 * 销毁
	 */
	public void destroy(){
		downLoadThread.quit();
	}
	
	
	
	/**
	 * 歌词读写逻辑
	 */
	private void loadLrcAndImage(Context context, SongInfo songInfo, final NetWorkLrcAndImageListener mNetWorkLrcAndImageListener, Handler handler){
		L.i(tag, "loadLrcAndImage:" + songInfo.getTitle());
		
		//当前歌曲是否有歌词
		String lrcPath = Utils.getInstance().serchLrc(songInfo);  //先在同目录下查找
		NetResource mNetResource = new NetResource();
		LrcNet lrcNet= null;
		try {
			if(new File(lrcPath).exists()){
				L.i(tag, "存在歌词");
			}else{
				L.w(tag, "不存在歌词");
				String lrcCachePath = getLrcCachePath(songInfo);
				do{
					//网络判断
					if(!Utils.getInstance().isEnableNetwork(context)){
						L.w(tag, "网络不佳, 下载歌词失败");
						break;
					}
					//通知开始加载lrc
					LrcNet[] lrcNets = mNetResource.getLrcURL(songInfo.getTitle(), songInfo.getSinger());
					//暂时默认只取第一个
					if(lrcNets != null){
						lrcNet = lrcNets[0];
						if(mNetResource.netReadWriteFile(lrcNet.getUrl(), lrcCachePath)){
							L.i(tag, "歌词写入成功");
							//通知歌词加载成功
						}else{
							lrcCachePath = null;
						}
						final String resultLrcPath = lrcCachePath;
						if(handler != null){
							handler.post(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									if(mNetWorkLrcAndImageListener != null){
										mNetWorkLrcAndImageListener.completeLRC(resultLrcPath);
									}
								}
							});
						}else{
							if(mNetWorkLrcAndImageListener != null){
								mNetWorkLrcAndImageListener.completeLRC(resultLrcPath);
							}
						}
					}
				}while(false);
			}
			
			//判断歌曲缩略图是否存在
			do{
				//判断ID3 图片是否存在
					
				
				//判断歌手图片是否存在
				
				//判断专辑图片是否存在
				
				//如果都不存在则下载图片，作为背景以及歌手专辑共用的图片
				
			}while(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
