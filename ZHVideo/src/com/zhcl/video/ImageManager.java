/**
 * 
 */
package com.zhcl.video;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zh.uitls.L;
import com.zh.uitls.Utils;
import com.zhcl.dao.VideoInfo;
import com.zhcl.filescanner.LocalFileCacheManager;

/**
 * 图片处理 ，包括歌手，歌曲，专辑图片的缓存处理
 * 
 * @author zhonghong.chenli
 * @date 2015-11-24 下午11:10:57
 */
public class ImageManager {
	
	private static final String tag = "ImageManager";
	public static final String CACHEDIR = "/mnt/sdcard/zhvideo/image";
	private static ImageManager mImageManager;
	
	public static ImageManager getInstance() {
		if (mImageManager == null) {
			mImageManager = new ImageManager();
		}
		return mImageManager;
	}
	Handler handlerImage;
	HandlerThread handlerThread;
	private ImageManager() {
		handlerThread = new HandlerThread("ImageManager");
		handlerThread.start(); //创建HandlerThread后一定要记得start()
		handlerImage = new Handler(LocalFileCacheManager.getInstance().getHandlerThread().getLooper()/*handlerThread.getLooper()*/){
			@Override
			public void handleMessage(Message msg) {
				handleMessageDo(msg);
			}
		};
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
				if(objs[0] instanceof VideoInfo){
					loadWriteVedioImage((VideoInfo)objs[0], (ImageView)objs[1], (Handler)objs[2]);	//加载并写入图片
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
	private boolean loadWriteVedioImage(final VideoInfo video, final ImageView imageView,  final Handler mtHandler){
		String path = getVideoInfoCachePath(video);
		boolean isexists = new File(path).exists();
		Bitmap bitmap = null;
		try { 
			if(!isexists){			//如果缩略图存在就不加载了
				int duration = video.getDuration();
//				if(duration == 0){ 			//测试添加
//					duration = VideoInfoFactory.getVideoDuration(video.getFileName());
//					video.setDuration(duration);
//				}
				if(duration <= 0){
					L.w(tag, "视频长度异常");
				}else{
					int shortTime = 5;
					if(duration < 5000){
						shortTime = 1;
					}
					bitmap = Utils.getInstance().getBitmapsFromVideo(video.getFileName(), shortTime);
				}
				if (bitmap != null) {
					L.e(tag, "准备生成歌手缩略图path ：" + path);
					//缩小图片,目前粗略按帧宽来判断
					final int maxW = 300;
					int scale = 1;
					if(bitmap.getWidth() > maxW){
						scale = bitmap.getWidth() / maxW;
						if(scale == 0){
							scale = 1;
						}
					}
					Bitmap targetBitmap = Utils.getInstance().small(bitmap, scale);
					bitmap.recycle();
					saveFile(targetBitmap, path);
					targetBitmap.recycle();
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
	 * 获得视频缓存路径
	 */
	private String getVideoInfoCachePath(VideoInfo video){
		return CACHEDIR + File.separator + video.getImageCacheName();
	}
	

	
	/**
	 * 歌手封面缓存 策略
	 */
	public String loadVideoInfoImage(VideoInfo video, ImageView imageView, Handler handler){
		L.e(tag, "loadVideoInfoImage");
		Message msg = handlerImage.obtainMessage();
		msg.what = 1;
		msg.obj = new Object[]{video, imageView, handler};
		handlerImage.sendMessage(msg);
		return getVideoInfoCachePath(video);
	}
	
	
	/**
	 * 销毁
	 */
	public void destroy(){
		handlerThread.quit();
	}
	
}
