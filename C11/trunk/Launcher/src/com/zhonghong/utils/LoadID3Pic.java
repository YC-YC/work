/**
 * 
 */
package com.zhonghong.utils;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.zhonghong.sdk.android.utils.BitmapUtils;

/**
 * @author YC
 * @time 2016-7-19 下午2:51:51
 * TODO:获取ID3专辑封面信息
 */
public class LoadID3Pic {

	private static final String TAG = "GetID3Pic";
	private MediaMetadataRetriever mRetriever;
	private String cachePath;
	private Bitmap cacheBitmap;
	
	public void displayImage(String path, ImageView imageview, Bitmap defaultImgId){
		
//		imageview.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(getID3Pic(path)));
		new LoadID3Asynctask(path, imageview, defaultImgId).execute(0);
	}
	
	private Bitmap getID3Pic(String path){
		
//		if (mRetriever == null){
//			mRetriever = new MediaMetadataRetriever();
//		}
		
		if (path.equals(cachePath) && cacheBitmap != null){
			Log.i(TAG, "is Same");
			return cacheBitmap;
		}
		cachePath = path;
		byte[] pic = ID3Jni.parseID3Pic(cachePath);
		if (pic != null){
			cacheBitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
		}
		else{
			cacheBitmap = null;
		}
//		mRetriever.setDataSource(path);
//		byte[] picture = mRetriever.getEmbeddedPicture();
//		cacheBitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
		return cacheBitmap;
	}
	
	
	private class LoadID3Asynctask extends AsyncTask<Integer, Integer, Bitmap>{

		private String path;
		private ImageView imageview;
		private Bitmap defaultBitmap;
		
		public LoadID3Asynctask(String path, ImageView imageview, Bitmap defaultImg) {
			super();
			this.path = path;
			this.imageview = imageview;
			this.defaultBitmap = defaultImg;
		}

		@Override
		protected Bitmap doInBackground(Integer... params) {
			L.startTime("解释ID3图片");
			Bitmap pic;
			if (path != null && new File(path).exists()){
				pic = getID3Pic(path);
			}
			else{
				pic = defaultBitmap;
			}
			L.endUseTime("解释ID3图片");
			return pic;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result == null){
				result = defaultBitmap;
			}
			Log.i(TAG, "onPostExecute");
			imageview.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(result));
		}

		
	}
	
}
