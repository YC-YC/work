package com.zhonghong.mediasdk.photo;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import com.zhonghong.mediasdk.BaseData;
import com.zhonghong.mediasdk.photo.PhotoItemInfo;

public class PhotoAsyncDataLoader {
	private static final String TAG = "PhotoAsyncDataLoader";
	private static  int UNCONSTRAINED = -1;
	private static final int LRUCACHE_SIZE = 100;
	private static final int ENTRY_SIZE  = 1;
	private static final int MINSIDELENGTH = 300;
	private static final int MAXNUMOFPIXELS = 300*320;
	private LruCache<String, PhotoItemInfo> photoItemCache = null;

	public interface FinishedCallback {
	    public void finishedLoaded(Object vh,PhotoItemInfo mItemInfo);
	}
	
	public PhotoAsyncDataLoader(){
		photoItemCache = new LruCache<String, PhotoItemInfo>(LRUCACHE_SIZE){
			@Override
			protected int sizeOf(String key,PhotoItemInfo mItem) {
				return ENTRY_SIZE;
			}
		};	
	}
	
	public void cleanCached(){
		if(photoItemCache != null){
			photoItemCache.evictAll();
		}
	}
	
	public static Bitmap loadImageFromPath(String imagePath,int _minSideLenght,int _maxNumOfPixels) {
		return makeBitmap(_minSideLenght, _maxNumOfPixels, imagePath,
				new BitmapFactory.Options());
	}
 
    public static Bitmap makeBitmap(int minSideLength, int maxNumOfPixels,
            String path, BitmapFactory.Options options) {
        try {
            
            if (options == null) options = new BitmapFactory.Options();

            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            if (options.mCancel || options.outWidth == -1
                    || options.outHeight == -1) {
                return null;
            }
            options.inSampleSize = computeSampleSize(
                    options, minSideLength, maxNumOfPixels);
            options.inJustDecodeBounds = false;

            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeFile(path, options);
        } catch (OutOfMemoryError ex) {
            Log.e("Malvina", "Got oom exception ", ex);
            return null;
        }
    }
    
    public static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }
        
    private static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED) &&
                (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
    
    public PhotoItemInfo asyncLoadPhotoItemInfo(final int idevid,final Object vh,final int index, final FinishedCallback Callback) {
    	Map<String,PhotoItemInfo> mMap = null;
    	PhotoItemInfo mItemInfo = null;
    	
    	if(idevid < 0 || index <0) {
    		Log.d(TAG,"asyncLoadPhotoItemInfo [FAIL],idevid "+idevid+" index "+index);
    		return null;
    	}
    	
    	final String key = PhotoServiceHelper.getPhotoItemInfo(idevid,index).getPath();
    	mMap =  photoItemCache.snapshot();
    	
        if (mMap.containsKey(key)) {
        	mItemInfo =(PhotoItemInfo)photoItemCache.get(key);
        	Log.d(TAG,"get data from cached");
            return mItemInfo;
        }
        
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
            	Callback.finishedLoaded(vh,(PhotoItemInfo) message.obj);
            }
        };
        
        new Thread() {
            @Override
            public void run() {
            	PhotoItemInfo _mItemInfo = null;
            	BaseData bd = null;
            	_mItemInfo = new PhotoItemInfo(); 
            	bd = PhotoServiceHelper.getPhotoItemInfo(idevid,index);
            	if(bd != null){
            		try{
		            	_mItemInfo.iItemIndex = index;
		            	_mItemInfo.aPhotoThumbnail = loadImageFromPath(bd.getPath(),MINSIDELENGTH,MAXNUMOFPIXELS);
		            	_mItemInfo.sName = bd.getName();
		            	photoItemCache.put(key, _mItemInfo);
		            	Log.d(TAG,"reload data.");
		                Message message = handler.obtainMessage(0, _mItemInfo);
		                handler.sendMessage(message);
            		}catch(NullPointerException e){
            			Log.d(TAG,"catch NullPointerException error.");
            		}catch(Exception e){
            			e.printStackTrace();
            		}
            	}else{
            		Log.d(TAG,"asyncLoadPhotoItemInfo [FAIL],bd "+bd);
            	}
            }
        }.start();
        return null;
    }	
}
