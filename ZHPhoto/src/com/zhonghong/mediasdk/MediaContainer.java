
package com.zhonghong.mediasdk;

import java.io.File;
import java.math.BigInteger;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;



import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
/*
import com.jrm.localmm.business.data.BaseData;
import com.jrm.localmm.business.data.FolderData;
import com.jrm.localmm.util.Constants;
*/
import android.view.View;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.TextView;

import com.zhonghong.mediasdk.BaseData;
import com.zhonghong.mediasdk.Constants;
import com.zhonghong.mediasdk.FolderData;
import com.zhonghong.mediasdk.Tools;

/**
 * Container of media data.
 * 
 * @author victor.chen
 * 
 */
public class MediaContainer extends Application{
	private final static String TAG = "MediaContainer";
	// all mounted storage list 
	private List<DeviceInfo> mountedDevList = null;
    // all file in current path
    private List<BaseData> allFileList = null;
    // all folder in current path
    private List<BaseData> allFolderList = null;
    // all picture in current path
    private List<BaseData> allPictureFileList = null;
    // all song in current path
    private List<BaseData> allSongFileList = null;
    // all video in current path
    private List<BaseData> allVideoFileList = null;
    // all device list
    private List<BaseData> deviceList = null; 	
	//storage manager
    private StorageManager storageManager = null;
    private MediaSavedInfo mMusicSavedInfo = null;
    private MediaSavedInfo mVideoSavedInfo = null;
    private MediaSavedInfo mPhotoSavedInfo = null;
	private static MediaContainer mContext = null;
	private static String mscandev_mutx = "SCAN_DEV_MUTX";
	private static long MAX_SCANNING_TIMES	= 2*60*1000;  //2min
	private final String ZUISERVICE_ACTION = "com.zhonghong.service.ZUIAppService";
	
	/**
	 * 
	 * @author Administrator
	 *
	 */
	public interface DataLoadCompleteListenser{
		public void notifyComplete(int idevid);
		public void notifyFailed(String reason);
	}
	/**
	 * 
	 * @author Administrator
	 *
	 */
	public interface FinishedDevLoadCallback {
	    public void finishedDevLoaded(DeviceInfo devinfo,List<FolderData> mfolderlist,List<BaseData> mfilelist);
	}
	
/*	private ServiceConnection mZuiAppserviceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName classname, IBinder obj) {	
			mZUIService = IZUIAppService.Stub.asInterface(obj);
				Log.d(TAG, "mZUIService:connect...");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {					
			mZUIService = null;
			Log.d(TAG, "mZUIService:disconnect...");
		}
	};*/
	
    @Override
    public void onCreate() {
        super.onCreate();
        
        //save Context
    	mContext = this;
    	
        // init all arrayList
        allFileList = new ArrayList<BaseData>();
        allFolderList = new ArrayList<BaseData>();
        allPictureFileList = new ArrayList<BaseData>();
        allSongFileList = new ArrayList<BaseData>();
        allVideoFileList = new ArrayList<BaseData>();
        deviceList = new ArrayList<BaseData>();
    	mountedDevList = new ArrayList<DeviceInfo>();
    	//get storage manager
        storageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
		//CALL ZUI service
		Intent intent0 = new Intent(ZUISERVICE_ACTION);
		startService(intent0);
//		bindService(intent0,mZuiAppserviceConnection,Context.BIND_AUTO_CREATE);
		
        //init muisc playback variable
        mMusicSavedInfo = new MediaSavedInfo(mContext,Constants.MEDIA_TYPE_MUSIC);
        mVideoSavedInfo = new MediaSavedInfo(mContext,Constants.MEDIA_TYPE_VIDEO);
        mPhotoSavedInfo = new MediaSavedInfo(mContext,Constants.MEDIA_TYPE_PHOTO);
        
        //set first run flag
        setFirstRunFlag(true);
    }

    public static MediaContainer getInstance() {
        return mContext;
    }
   
    /**
     * 
     * @return
     */
/*    public IZUIAppService getZUIServiceObj(){
    	
    	if(mZUIService == null){
    		Log.e(TAG,"getZUIServiceObj [FAIL],mZUIService:"+mZUIService);
    	}
    	return mZUIService;
    }
    */
    /**
     * @author chendz
     */
    public void setFirstRunFlag(boolean bvalue){
		SharedPreferences preference = getSharedPreferences(Constants.ZHMEDIA_PREF,
				MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putBoolean(Constants.FIRST_RUN, bvalue);
		editor.commit();
		Log.d(TAG,"setFirtRunFlag "+bvalue);
    }
    
    /**
     * @author chendz
     * 
     */
    public boolean IsFirstRunFlag(){
    	boolean bvalue;
		SharedPreferences preference = getSharedPreferences(Constants.ZHMEDIA_PREF,
				MODE_PRIVATE);
		bvalue = preference.getBoolean(Constants.FIRST_RUN, false);
		//after read,clear value.
		Editor editor = preference.edit();
		editor.putBoolean(Constants.FIRST_RUN, false);
		editor.commit();
		return bvalue;
    }
    
    /**
     * 
     */
    public String getActivityLabelString(ComponentName cn){
    	String slable = null;
    	
    	if(mContext != null){
    		try {
    			PackageManager pkgm= mContext.getPackageManager();
    			ActivityInfo actInfo = pkgm.getActivityInfo(cn, PackageManager.GET_ACTIVITIES);
    			slable = actInfo.loadLabel(pkgm).toString();
    	    	Log.d(TAG,"getActivityLabelString [OK],Name:"+slable);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				Log.e(TAG,"getActivityLabelString [FAIL],pkg: "+cn.getPackageName()+" cls:"+cn.getClassName());
				e.printStackTrace();
			}
    	}
    	return slable;
    }
    
    /**
     * 
     * get playback paramter method
     */
    public int getCurrentPlayMode(int imtype){
    	if(imtype == Constants.MEDIA_TYPE_MUSIC){
    		return mMusicSavedInfo.getCurrentPlayMode();
    	}else if(imtype == Constants.MEDIA_TYPE_VIDEO){
    		return mVideoSavedInfo.getCurrentPlayMode();
    	}else if(imtype == Constants.MEDIA_TYPE_PHOTO){
    		return mPhotoSavedInfo.getCurrentPlayMode();
    	}
    	Log.d(TAG,"getCurrentPlayMode [FAIL],imtype:"+imtype);
    	return Constants.INVALID_VALUE;
    }
    
    /**
     * @author victor
     * 
     */
    public int getCurrentEQMode(int imtype){
    	if(imtype == Constants.MEDIA_TYPE_MUSIC){
    		return mMusicSavedInfo.getCurrentEQMode();
    	}else if(imtype == Constants.MEDIA_TYPE_VIDEO){
    		return mVideoSavedInfo.getCurrentEQMode();
    	}else if(imtype == Constants.MEDIA_TYPE_PHOTO){
    		return mPhotoSavedInfo.getCurrentEQMode();
    	}
    	Log.d(TAG,"getCurrentEQMode [FAIL],imtype:"+imtype);
    	return Constants.INVALID_VALUE;
    }
    
    /**
     * @author victor
     * 
     */
    public int getCurrentPlayIndex(int imtype){
    	if(imtype == Constants.MEDIA_TYPE_MUSIC){
    		return mMusicSavedInfo.getCurrentPlayIndex();
    	}else if(imtype == Constants.MEDIA_TYPE_VIDEO){
    		return mVideoSavedInfo.getCurrentPlayIndex();
    	}else if(imtype == Constants.MEDIA_TYPE_PHOTO){
    		return mPhotoSavedInfo.getCurrentPlayIndex();
    	}
    	Log.d(TAG,"getCurrentPlayIndex [FAIL],imtype:"+imtype);
    	return Constants.INVALID_VALUE;
    }
    
    /**
     * @author victor
     * 
     */
    public int getCurrentPlayPosition(int imtype){
    	if(imtype == Constants.MEDIA_TYPE_MUSIC){
    		return mMusicSavedInfo.getCurrentPlayPosition();
    	}else if(imtype == Constants.MEDIA_TYPE_VIDEO){
    		return mVideoSavedInfo.getCurrentPlayPosition();
    	}else if(imtype == Constants.MEDIA_TYPE_PHOTO){
    		return mPhotoSavedInfo.getCurrentPlayPosition();
    	}
    	Log.d(TAG,"getCurrentPlayPosition [FAIL],imtype:"+imtype);
    	return Constants.INVALID_VALUE;
    }
    
    /**
     * 
     * @param imtype
     * @return
     */
    public int getCurrentPlaybackDeviceID(int imtype){
    	if(imtype == Constants.MEDIA_TYPE_MUSIC){
    		return mMusicSavedInfo.getCurrentPlaybackDeviceID();
    	}else if(imtype == Constants.MEDIA_TYPE_VIDEO){
    		return mVideoSavedInfo.getCurrentPlaybackDeviceID();
    	}else if(imtype == Constants.MEDIA_TYPE_PHOTO){
    		return mPhotoSavedInfo.getCurrentPlaybackDeviceID();
    	}
    	Log.d(TAG,"getCurrentPlayPosition [FAIL],imtype:"+imtype);
    	return Constants.INVALID_VALUE;
    }
    /**
     * @author victor
     * 
     */
    public String getCurrentPlaybackFilePath(int imtype){
    	if(imtype == Constants.MEDIA_TYPE_MUSIC){
    		return mMusicSavedInfo.getCurrentPlaybackFilePath();
    	}else if(imtype == Constants.MEDIA_TYPE_VIDEO){
    		return mVideoSavedInfo.getCurrentPlaybackFilePath();
    	}else if(imtype == Constants.MEDIA_TYPE_PHOTO){
    		return mPhotoSavedInfo.getCurrentPlaybackFilePath();
    	}
    	Log.d(TAG,"getCurrentPlaybackFilePath [FAIL],imtype:"+imtype);
    	return null;
    }
    
    /**
     * @author victor
     * 
     */
    public int getCurrentBgIndex(int imtype){
    	if(imtype == Constants.MEDIA_TYPE_MUSIC){
    		return mMusicSavedInfo.getCurrentBgIndex();
    	}else if(imtype == Constants.MEDIA_TYPE_VIDEO){
    		return mVideoSavedInfo.getCurrentBgIndex();
    	}else if(imtype == Constants.MEDIA_TYPE_PHOTO){
    		return mPhotoSavedInfo.getCurrentBgIndex();
    	}
    	Log.d(TAG,"getCurrentBgIndex [FAIL],imtype:"+imtype);
    	return Constants.INVALID_VALUE;
    }
    
    /**
     * @author victor
     * 
     */
	public boolean checkValidPlaybackAddress(int imtype,List<BaseData> _MediaList){	
    	if(imtype == Constants.MEDIA_TYPE_MUSIC){
    		return mMusicSavedInfo.checkValidPlaybackAddress(_MediaList);
    	}else if(imtype == Constants.MEDIA_TYPE_VIDEO){
    		return mVideoSavedInfo.checkValidPlaybackAddress(_MediaList);
    	}else if(imtype == Constants.MEDIA_TYPE_PHOTO){
    		return mPhotoSavedInfo.checkValidPlaybackAddress(_MediaList);
    	}
    	Log.d(TAG,"checkValidPlaybackAddress [FAIL],imtype:"+imtype);
		return false;
	}
	
	/**
	 * 
	 */
	public void resetPlaybackInfo(int imtype){
    	if(imtype == Constants.MEDIA_TYPE_MUSIC){
    		mMusicSavedInfo.resetMediaSavedInfo();
    	}else if(imtype == Constants.MEDIA_TYPE_VIDEO){
    		mVideoSavedInfo.resetMediaSavedInfo();
    	}else if(imtype == Constants.MEDIA_TYPE_PHOTO){
    		mPhotoSavedInfo.resetMediaSavedInfo();
    	}else{
    		Log.d(TAG,"resetPlaybackInfo [FAIL],imtype:"+imtype);
    	}
	}
	
    /**
     * get playback paramter method
     */
    public void setCurrentPlayMode(int imtype,int _iCurrPlayMode){
    	if(imtype == Constants.MEDIA_TYPE_MUSIC){
    		mMusicSavedInfo.setCurrentPlayMode(_iCurrPlayMode);
    	}else if(imtype == Constants.MEDIA_TYPE_VIDEO){
    		mVideoSavedInfo.setCurrentPlayMode(_iCurrPlayMode);
    	}else if(imtype == Constants.MEDIA_TYPE_PHOTO){
    		mPhotoSavedInfo.setCurrentPlayMode(_iCurrPlayMode);
    	}else{
    		Log.d(TAG,"setCurrentPlayMode [FAIL],imtype:"+imtype);
    	}
    }
    
    /**
     * 
     * @param imtype
     * @param _iCurrPlayEQ
     */
    public void setCurrentEQMode(int imtype,int _iCurrPlayEQ){
    	if(imtype == Constants.MEDIA_TYPE_MUSIC){
    		mMusicSavedInfo.setCurrentEQMode(_iCurrPlayEQ);
    	}else if(imtype == Constants.MEDIA_TYPE_VIDEO){
    		mVideoSavedInfo.setCurrentEQMode(_iCurrPlayEQ);
    	}else if(imtype == Constants.MEDIA_TYPE_PHOTO){
    		mPhotoSavedInfo.setCurrentEQMode(_iCurrPlayEQ);
    	}else{
    		Log.d(TAG,"setCurrentEQMode [FAIL],imtype:"+imtype);
    	}
    }
    
    /**
     * 
     * @param imtype
     * @param _iCurrPlayIndex
     */
    public void setCurrentPlayIndex(int imtype,int _iCurrPlayIndex){
    	if(imtype == Constants.MEDIA_TYPE_MUSIC){
    		mMusicSavedInfo.setCurrentPlayIndex(_iCurrPlayIndex);
    	}else if(imtype == Constants.MEDIA_TYPE_VIDEO){
    		mVideoSavedInfo.setCurrentPlayIndex(_iCurrPlayIndex);
    	}else if(imtype == Constants.MEDIA_TYPE_PHOTO){
    		mPhotoSavedInfo.setCurrentPlayIndex(_iCurrPlayIndex);
    	}else{
    		Log.d(TAG,"setCurrentPlayIndex [FAIL],imtype:"+imtype);
    	}
    }
  
    /**
     * 
     * @param imtype
     * @param _iCurrPlaybackDeviceID
     */
    public void setCurrentPlaybackDeviceID(int imtype,int _iCurrPlaybackDeviceID){
    	if(imtype == Constants.MEDIA_TYPE_MUSIC){
    		mMusicSavedInfo.setCurrentPlaybackDeviceID(_iCurrPlaybackDeviceID);
    	}else if(imtype == Constants.MEDIA_TYPE_VIDEO){
    		mVideoSavedInfo.setCurrentPlaybackDeviceID(_iCurrPlaybackDeviceID);
    	}else if(imtype == Constants.MEDIA_TYPE_PHOTO){
    		mPhotoSavedInfo.setCurrentPlaybackDeviceID(_iCurrPlaybackDeviceID);
    	}else{
    		Log.d(TAG,"setCurrentPlaybackDeviceID [FAIL],imtype:"+imtype);
    	}
    }
    
    /**
     * 
     * @param imtype
     * @param iPos
     */
    public void setCurrentPlayPosition(int imtype,int iPos){
    	if(imtype == Constants.MEDIA_TYPE_MUSIC){
    		mMusicSavedInfo.setCurrentPlayPosition(iPos);
    	}else if(imtype == Constants.MEDIA_TYPE_VIDEO){
    		mVideoSavedInfo.setCurrentPlayPosition(iPos);
    	}else if(imtype == Constants.MEDIA_TYPE_PHOTO){
    		mPhotoSavedInfo.setCurrentPlayPosition(iPos);
    	}else{
    		Log.d(TAG,"setCurrentPlayPosition [FAIL],imtype:"+imtype);
    	}
    }
    
    /**
     * 
     * @param imtype
     * @param _strCurrPlayFilePath
     */
    public void setCurrentPlaybackFilePath(int imtype,String _strCurrPlayFilePath){
    	if(imtype == Constants.MEDIA_TYPE_MUSIC){
    		mMusicSavedInfo.setCurrentPlaybackFilePath(_strCurrPlayFilePath);
    	}else if(imtype == Constants.MEDIA_TYPE_VIDEO){
    		mVideoSavedInfo.setCurrentPlaybackFilePath(_strCurrPlayFilePath);
    	}else if(imtype == Constants.MEDIA_TYPE_PHOTO){
    		mPhotoSavedInfo.setCurrentPlaybackFilePath(_strCurrPlayFilePath);
    	}else{
    		Log.d(TAG,"setCurrentPlaybackFilePath [FAIL],imtype:"+imtype);
    	}
    }
    
    /**
     * 
     * @param imtype
     * @param _iCurrBgIndex
     */
    public void setCurrentBgIndex(int imtype,int _iCurrBgIndex){
    	if(imtype == Constants.MEDIA_TYPE_MUSIC){
    		mMusicSavedInfo.setCurrentBgIndex(_iCurrBgIndex);
    	}else if(imtype == Constants.MEDIA_TYPE_VIDEO){
    		mVideoSavedInfo.setCurrentBgIndex(_iCurrBgIndex);
    	}else if(imtype == Constants.MEDIA_TYPE_PHOTO){
    		mPhotoSavedInfo.setCurrentBgIndex(_iCurrBgIndex);
    	}else{
    		Log.d(TAG,"setCurrentBgIndex [FAIL],imtype:"+imtype);
    	}
    }
    
	/**
	 * 
	 * @param imtype
	 */
    public void saveMediaInfoToPerf(int imtype){
    	if(imtype == Constants.MEDIA_TYPE_MUSIC){
    		mMusicSavedInfo.saveMediaSavedInfo();
    	}else if(imtype == Constants.MEDIA_TYPE_VIDEO){
    		mVideoSavedInfo.saveMediaSavedInfo();
    	}else if(imtype == Constants.MEDIA_TYPE_PHOTO){
    		mPhotoSavedInfo.saveMediaSavedInfo();
    	}else{
    		mPhotoSavedInfo.saveMediaSavedInfo();
    		mVideoSavedInfo.saveMediaSavedInfo();
    		mMusicSavedInfo.saveMediaSavedInfo();
    		Log.d(TAG,"saveMediaInfoToPerf [OK],imtype:"+imtype);
    	}
    }
       
    /**
     * @author chendz
     * @param
     * 
     */
    public boolean checkDeviceDBFinishedByDevId(int deviceid){
    	if((checkValidDeviceID(deviceid)) && (checkMountedDeviceId(deviceid))){
    		if(getDeviceInfoByDevID(deviceid).iDevStatus == Constants.DB_FINISHED){
    			return true;
    		}
    	}
    	
    	return false;
    }
   
    /**
     * @author chendz
     * @param
     * 
     */
    public boolean checkDeviceScanningByDevId(int deviceid){
    	if((checkValidDeviceID(deviceid)) && (checkMountedDeviceId(deviceid))){
    		if(getDeviceInfoByDevID(deviceid).iDevStatus == Constants.DB_PROCESSING){
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    /**
     * 
     * @param deviceid
     * @return
     */
    public boolean checkDeviceCacheFailByDevId(int deviceid){
    	final int CONFIRM_TIMES = 10;
    	int confirmtimes = 0;
    	if(checkPlaybackStorageCardMounted(deviceid)){
    		if(!checkMountedDeviceId(deviceid)){		
    			while(confirmtimes++ < CONFIRM_TIMES){
    				if(checkMountedDeviceId(deviceid)){
    					break;
    				}
    				
    				try{
    					Thread.sleep(100);
    				}catch(Exception e){
    					e.printStackTrace();
    				}
    			}
    			
    			if(confirmtimes > CONFIRM_TIMES){
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }
    
    /**
     * @author chendz
     * 
     */
    public String translateDeviceIDToMountPath(int deviceid){	
    	if(deviceid == Constants.SDCARD_DEVICE){
    		return Constants.DEFSD_STORAGE_PATH;
    	}
    	else if(deviceid == Constants.USB_DEVICE){
    		return Constants.DEFUSB_STORAGE_PATH;
    	}
    	else if(deviceid == Constants.INTERNAL_DEVICE){
    		return Constants.DEFINTERNAL_STORAGE_PATH;
    	}
    	
    	Log.e(TAG,"translateDeviceIDToMountPath InvalidID:"+deviceid);
    	return "error";
    }
    
    /**
     * 
     */
    public int translateMountPathToDeviceID(String strpath)
    {	
    	if(strpath != null)
    	{
	    	if(strpath.equals(Constants.DEFSD_STORAGE_PATH))
	    	{
	    		return Constants.SDCARD_DEVICE;
	    	}
	    	else if(strpath.equals(Constants.DEFUSB_STORAGE_PATH))
	    	{
	    		return Constants.USB_DEVICE;
	    	}
	    	else  if(strpath.equals(Constants.DEFINTERNAL_STORAGE_PATH))
	    	{
	    		return Constants.INTERNAL_DEVICE;
	    	}
    	}
    	Log.e(TAG,"translateMountPathToDeviceID MountPath:"+strpath);
    	return Constants.INVALID_DEVICE;
    } 
    
    /**
     * @author victorchen
     */
    public String getCurrentPlaybackDevicePath(int imediatype)
    {
    	return translateDeviceIDToMountPath(getCurrentPlaybackDeviceID(imediatype));
    }
       
    /**
     * Clear all media data in memory.
     */
    public final void clearAll() {
        allFileList.clear();
        allFolderList.clear();
        allPictureFileList.clear();
        allSongFileList.clear();
        allVideoFileList.clear();
        mountedDevList.clear();
        deviceList.clear();
    }

    /**
     * 
     * @param devid
     * @param type
     * @return
     */
    public ArrayList<?> getMediaDataByDevId(final int devid, final int type){
    	ArrayList<?> tmplist = null;
    	if(!checkValidDeviceID(devid)){
    		Log.d(TAG,"getMediaDataByDevId,invalid id "+devid+" type "+type);
    		return null;
    	}
    	
        switch (type) {
        case Constants.FILE_TYPE_PICTURE:
            break;
        case Constants.FILE_TYPE_TTPHOTOS: 
        	tmplist = (ArrayList<?>) getDeviceInfoByDevID(devid).mPhotoTotalList;
        	break;
        	
        case Constants.FILE_TYPE_PHOTODIR:
        	tmplist = (ArrayList<?>) getDeviceInfoByDevID(devid).mPhotoFolderList;
        	break;
        	
        case Constants.FILE_TYPE_SONG:
        	break;
        	
        case Constants.FILE_TYPE_TTSONGS:
        	tmplist = (ArrayList<?>) getDeviceInfoByDevID(devid).mMusicTotalList;
        	break;
        	
        case Constants.FILE_TYPE_SONGDIR:
            // whether has song or not
        	tmplist = (ArrayList<?>) getDeviceInfoByDevID(devid).mMusicFolderList;
            break;
            
        case Constants.FILE_TYPE_VIDEO:
        	break;
        	
        case Constants.FILE_TYPE_VIDEODIR:
        	tmplist = (ArrayList<?>) getDeviceInfoByDevID(devid).mVideoFolderList;
        	break;
        	
        case Constants.FILE_TYPE_TTVIDEOS:
            // whether has song or not
        	tmplist = (ArrayList<?>) getDeviceInfoByDevID(devid).mVideoTotalList;
            break;
        default:
            break;
        }
        
    	return tmplist;
    }
    /**
     * Check whether has specified media data.
     * 
     * @param type the type of media, such as image.
     * @return true if has specified media data in container.
     */
    public final boolean hasMedia(final DeviceInfo devinfo,final int type) {
        boolean flag = false;

        switch (type) {
            case Constants.FILE_TYPE_PICTURE:
                // whether has song or not
            	if((devinfo != null) && (devinfo.mPhotoTotalList != null) && (devinfo.mPhotoTotalList.size() >0)){
            		flag = true;
            	}
                break;
                
            case Constants.FILE_TYPE_SONG:             
            case Constants.FILE_TYPE_TTSONGS:
            case Constants.FILE_TYPE_SONGDIR:
                // whether has song or not
            	if((devinfo != null) && (devinfo.mMusicTotalList != null) && (devinfo.mMusicTotalList.size() >0)){
            		flag = true;
            	}
                break;
                
            case Constants.FILE_TYPE_VIDEO:
	        case Constants.FILE_TYPE_VIDEODIR:
            case Constants.FILE_TYPE_TTVIDEOS:
                // whether has video or not
            	if((devinfo != null) && (devinfo.mVideoTotalList != null) && (devinfo.mVideoTotalList.size() >0)){
            		flag = true;
            	}
                break;
            default:
                break;
        }

        return flag;
    }
    
    /**
     * @author chendz
     * 
     */
    private class asyncLoadDevData {
        private FinishedDevLoadCallback listener = null;
        private int iScanType =0;
        private String strDevPath = null;
        private DeviceInfo devinfo = null;
        private List<FolderData> tempMusicFolderDatalist = null;
        private List<BaseData>  tempMusicBaseDatalist = null;
        private List<FolderData> tempVideoFolderDatalist = null;
        private List<BaseData>  tempVideoBaseDatalist = null;
        private List<FolderData> tempPhotoFolderDatalist = null;
        private List<BaseData>  tempPhotoBaseDatalist = null;
   	    private ArrayList<String> path = new ArrayList<String>();
   	    private DataLoadCompleteListenser listener1 = null;
   	    private int iScanType1 = 0;
        /**
         * @author chendz
         * 
         */
        public void loadDevThread(DeviceInfo _devinfo,int _iScanType, FinishedDevLoadCallback _ll){
        	devinfo = _devinfo;
        	strDevPath = _devinfo.sMountPoint;
        	listener = _ll;
            iScanType = _iScanType;
            tempMusicFolderDatalist = devinfo.mMusicFolderList;
            tempMusicBaseDatalist = devinfo.mMusicTotalList;
            tempVideoFolderDatalist = devinfo.mVideoFolderList;
            tempVideoBaseDatalist = devinfo.mVideoTotalList;
            tempPhotoFolderDatalist = devinfo.mPhotoFolderList;
            tempPhotoBaseDatalist = devinfo.mPhotoTotalList;
            
        	new Thread(){
                /* (non-Javadoc)
                 * @see java.lang.Thread#run()
                 */
                public void run() {
                	synchronized(listener){
                		File file = new File(strDevPath);
                        if (file.isDirectory()) {
                            if (file.list() != null && file.list().length > 0) {
                            	scanAVFolder1(file.listFiles(),iScanType);
                            }
                        } 
                        if(listener != null){
                        	listener.finishedDevLoaded(devinfo,tempMusicFolderDatalist, tempMusicBaseDatalist);
                        }
                        if(devinfo != null){
                        	synchronized(devinfo){
                        		devinfo.notifyAll(); //notify wait thread wakeup
                        	}
                        }
                	}
            	 }
        	}.start();
        }
        
        
        /**
         * @author chendz
         * 
         */
        public void loadDevListThread(ArrayList<String> _path,final int _iScanType, DataLoadCompleteListenser _ll){
        	path.clear();
        	path.addAll(_path);
            iScanType1 = _iScanType;
            listener1 = _ll;
            //tempMusicFolderDatalist = null;
           // tempMusicBaseDatalist = null;
            
        	new Thread(){
                /* (non-Javadoc)
                 * @see java.lang.Thread#run()
                 */
                public void run() {
                	synchronized(mscandev_mutx){
                		boolean bScanedMedia = false;
                		String strDevPath = null;
                		int iscandevid = -1;
                		int ifirstvaliddevid = -1;
                		int iretryTimes = 10;
                		DeviceInfo devinfo = null;
                		
                		for(String str : path){
                			Log.d(TAG,"loadDevListThread DEV: "+str);
                			iretryTimes = 10;
                			iscandevid = translateMountPathToDeviceID(str);
                			while(--iretryTimes > 0){
	                			if(checkDeviceDBFinishedByDevId(iscandevid)){
	                				if(hasMedia(getDeviceInfoByDevID(iscandevid),iScanType1)){
	                					if(!bScanedMedia){
	                						listener1.notifyComplete(iscandevid);
		                					bScanedMedia = true;
	                					}
	                					Log.d(TAG,"loadDevListThread DEV: "+str+" db ready cached,skip it.");
	                				}else{
	                					Log.d(TAG,"loadDevListThread DEV: "+str+" no valid files.");
	                				}
	                				break;
	                			}else if((checkDeviceScanningByDevId(iscandevid))){	 
	                				    devinfo = getDeviceInfoByDevID(iscandevid);
	                				    synchronized (devinfo) {
			                				try {
			                					if(devinfo != null){
			                						devinfo.wait(MAX_SCANNING_TIMES);
			                					}
											} catch (InterruptedException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
												iretryTimes = 0;
												Log.d(TAG,"checkDeviceScanningByDevId wait awake.");
											}
	                				    }
	                			}else{
	                				if(checkDeviceCacheFailByDevId(iscandevid)){
	                					Log.d(TAG,"DEV: "+str+" mount,loss cache data,force mount it.");
	                					addMountedDeviceToList(str);
	                					iretryTimes = 10;
	                					continue;
	                				}else{
	                					Log.d(TAG,"loadDevListThread DEV: "+str+" no mount,scanning next dev.");
	                					break;
	                				}	                				
	                			}
	                		}
                		}
                			                		
                		//if scaned all device,and no found valid file
                		if(!bScanedMedia){
                        	listener1.notifyFailed(Constants.DB_ERROR_NOFILE);
                        }               		
                	}
                }
        	}.start();
        }
        /**
         * @author chendz
         */
        private boolean scanAVFolder1(final File[] files,int iScanType)
        {  	

        	scanAVFolder11(files,iScanType);
    	    Collections.sort(tempMusicFolderDatalist,fdcomparator);
    	    Collections.sort(tempVideoFolderDatalist,fdcomparator);
    	    Collections.sort(tempPhotoFolderDatalist,fdcomparator);
    		for(FolderData fdd : tempMusicFolderDatalist){
    			Collections.sort(fdd.getCurrentFolderFileList(),flcomparator);
    			tempMusicBaseDatalist.addAll(fdd.getCurrentFolderFileList());
    		}
    		for(FolderData fdd : tempVideoFolderDatalist){
    			Collections.sort(fdd.getCurrentFolderFileList(),flcomparator);
    			tempVideoBaseDatalist.addAll(fdd.getCurrentFolderFileList());
    		}
    		for(FolderData fdd : tempPhotoFolderDatalist){
    			Collections.sort(fdd.getCurrentFolderFileList(),flcomparator);
    			tempPhotoBaseDatalist.addAll(fdd.getCurrentFolderFileList());
    		}
            return true;
        }      

        /**
         * Scan av files11
         */
        private void scanAVFolder11(final File[] files,int iScanType)
    	{
    		FolderData mfdd = new FolderData();
    		FolderData vfdd = new FolderData();
    		FolderData pfdd = new FolderData();
    		mfdd.setCurrentFolderPath(files[0].getParent());
    		vfdd.setCurrentFolderPath(files[0].getParent());
    		pfdd.setCurrentFolderPath(files[0].getParent());
    		
            for (File f : files) 
            {	          	
                if (f.isDirectory()) 
                {
                	  if(f.list() != null && f.list().length > 0)
                	  {
                		  scanAVFolder11(f.listFiles(),iScanType);
    				  }
                } 
                else 
                {                  	
            		BaseData file = new BaseData();     		
            		String name = f.getName();	
    	            file.setPath(f.getAbsolutePath());
    	            file.setParentPath(f.getParent());
    	            file.setName(name);
    	            String formatSize = Tools.formatSize(BigInteger.valueOf(f.length()));
                    file.setSize(formatSize);
                    file.setDescription(formatSize);
                    file.setModifyTime(f.lastModified());           
                    int pos = name.lastIndexOf(".");
                    String extension = "";
                    if (pos > 0)
                    {
                        extension = name.toLowerCase().substring(pos + 1);
                        file.setFormat(extension);
                    }
                         		
                	if(check(name,Constants.audio_filter))  //get audio type 
	                {
	                    file.setType(Constants.FILE_TYPE_SONG);
	                    mfdd.insertFileToCurrentFolder(file);
	                    if(!tempMusicFolderDatalist.contains(mfdd))
	                    {
	                    	tempMusicFolderDatalist.add(mfdd);
	                    }
	                }
                	else if(check(name,Constants.video_filter)) // video
          			{
	                    file.setType(Constants.FILE_TYPE_VIDEO);
	                    vfdd.insertFileToCurrentFolder(file);
	                    if(!tempVideoFolderDatalist.contains(vfdd))
	                    {
	                    	tempVideoFolderDatalist.add(vfdd);
	                    }
	                }
                	else if(check(name,Constants.photo_filter)) // photo
                	{  
	                    file.setType(Constants.FILE_TYPE_PICTURE);
	                    pfdd.insertFileToCurrentFolder(file);
	                    if(!tempPhotoFolderDatalist.contains(pfdd))
	                    {
	                    	tempPhotoFolderDatalist.add(pfdd);
	                    }
                	}
                  	file = null;
                }
            }
    	}
    }
	   
	/**
	 * 
	 */
    private boolean check(final String name, final String[] extensions) {
        for (String end : extensions) {
            if (name.toLowerCase().endsWith(end)) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * folder sort
     */
    private Comparator<FolderData> fdcomparator = new Comparator<FolderData>() {

        @Override
        public int compare(FolderData lData, FolderData rData) {

            String lName = lData.getCurrentFolderPathStr();
            String rName = rData.getCurrentFolderPathStr();
            if (lName != null && rName != null) {
                Collator collator = Collator.getInstance(Locale.CHINA);
                return collator.compare(lName.toLowerCase(), rName.toLowerCase());

            } else {
                Log.e("comparator", "lName != null && rName != null is false");
                return 0;
            }

        }
    };
    
    /**
     * file sort
     */
    private Comparator<BaseData> flcomparator = new Comparator<BaseData>() {

        @Override
        public int compare(BaseData lData, BaseData rData) {
            String lName = lData.getName();
            String rName = rData.getName();
            if (lName != null && rName != null) {
                Collator collator = Collator.getInstance(Locale.CHINA);
                return collator.compare(lName.toLowerCase(), rName.toLowerCase());

            } else {
                Log.e("comparator", "lName != null && rName != null is false");
                return 0;
            }

        }
    };
    
    /**
     * 
     */
    private DeviceInfo checkDeviceMounted(String mountedpoint){
    	DeviceInfo devinfo = null;
    	if(mountedpoint == null){
    		Log.e(TAG,"checkDeviceMounted [FAIL],"+mountedpoint);
    		return null;
    	}
    	Iterator<DeviceInfo> lt = mountedDevList.iterator();
    	while(lt.hasNext()){
    		devinfo = lt.next();
    		if(devinfo.sMountPoint.equals(mountedpoint)){
    			return devinfo;
    		}
    	}
    	
    	return null;
    }
    
    /**
     * @param
     * @return 
     */
    private long getPathTotalCapacity(String path){
    	long blocksize =0;
     	long totalblocks =0;
    	if(path == null){
    		Log.e(TAG,"getPathTotalCapacity [FAIL],path:"+path);
    		return -1;
    	}
    	try{
	    	StatFs stat= new StatFs(path);
	    	blocksize = stat.getBlockSize();
	    	totalblocks = stat.getBlockCount();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	Log.d(TAG,"getPathTotalCapacity,path:"+path+" Totalsize: "+(blocksize*totalblocks)/(1024*1024)+"MB");
    	return (blocksize*totalblocks);
    }
    
    /**
     * 
     * @param path
     * @return
     */
    private long getPathAvailableSpace(String path){
    	long blocksize = 0;
    	long availableblocks = 0;
    	if(path == null){
    		Log.e(TAG,"getPathAvailableSpace [FAIL],path:"+path);
    		return -1;
    	}
    	try{
	    	StatFs stat= new StatFs(path);
	    	blocksize = stat.getBlockSize();
	    	availableblocks = stat.getAvailableBlocks();    	
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	Log.d(TAG,"getPathTotalCapacity,path:"+path+" Freesize: "+(blocksize*availableblocks)/(1024*1024)+"MB");
    	return (blocksize*availableblocks);
    }
  
    /**
     * @author chendz
     * @param
     */
    public boolean addManualDeviceToList(DeviceInfo devinfo){
    	DeviceInfo _devinfo = null;
    	if(devinfo == null){
    		Log.d(TAG,"addManualDeviceToList [FAIL],"+devinfo);
    		return false;
    	}
    	_devinfo = checkDeviceMounted(devinfo.sMountPoint);
    	if(_devinfo != null){
    		mountedDevList.remove(_devinfo);
    	}
    	mountedDevList.add(devinfo);
    	Log.d(TAG,"addManualDeviceToList: "+devinfo.sMountPoint);
    	return true;
    }
    
    /**
     * @author chendz
     * @param
     */
    public void addMountedDeviceToList(String mountedpoint){
    	DeviceInfo devinfo = null;
    	//send start scanning broadcasts
    	Intent intent = new Intent(Constants.ACTION_ZHMEDIA_SCANNER_STARTED);	
    	intent.putExtra(Constants.SCANNING_DEVID, translateMountPathToDeviceID(mountedpoint));
    	sendBroadcast(intent);
    	
    	devinfo = checkDeviceMounted(mountedpoint);
    	if(devinfo == null){
    		devinfo = new DeviceInfo();
    		mountedDevList.add(devinfo);
    	}
    	
    	if(devinfo != null){
    		devinfo.iDevStatus = Constants.DB_NOINITITAL;
    		devinfo.iDevID = translateMountPathToDeviceID(mountedpoint);
    		devinfo.sMountPoint = mountedpoint;
    		devinfo.sName = mountedpoint.substring(mountedpoint.lastIndexOf("/") + 1, mountedpoint.length());
    		devinfo.iDevAvailableSpace = getPathAvailableSpace(mountedpoint);
    		devinfo.iDevCapacity = getPathTotalCapacity(mountedpoint);
    		devinfo.iDevStatus = Constants.DB_PROCESSING;
    		new asyncLoadDevData().loadDevThread(devinfo,Constants.FILE_TYPE_MEDIADIR,new FinishedDevLoadCallback() {
				
				@Override
				public void finishedDevLoaded(DeviceInfo _devinfo,List<FolderData> mfolderlist,
						List<BaseData> mfilelist) {
					// TODO Auto-generated method stub
					try{
						if(_devinfo != null){
							if(checkValidDeviceInfo(_devinfo)){
								_devinfo.iDevStatus = Constants.DB_FINISHED;
								//_devinfo.mMusicFolderList = mfolderlist;
								//_devinfo.mMusicTotalList = mfilelist;
								Log.d(TAG,"addMountedDeviceToList [OK]"+ _devinfo.sMountPoint);
							}else{
								_devinfo.iDevStatus = Constants.DB_NOINITITAL;
								_devinfo.mMusicFolderList = null;
								_devinfo.mMusicTotalList = null;
								_devinfo.mVideoFolderList = null;
								_devinfo.mVideoTotalList = null;
								_devinfo.mPhotoFolderList = null;
								_devinfo.mPhotoTotalList = null;
								Log.d(TAG,"addMountedDeviceToList [FAIL] "+_devinfo.sMountPoint);
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					//send scanning finished broadcast
			    	Intent intent = new Intent(Constants.ACTION_ZHMEDIA_SCANNER_FINISHED);	
			    	intent.putExtra(Constants.SCANNING_DEVID,_devinfo!=null?_devinfo.iDevID:Constants.INVALID_DEVICE);
			    	sendBroadcast(intent);
				}
			});
    	}
    }
    
    /**
     * @author chendz
     */
    public void delUnmountedDeviceFromList(String mountedpoint){
    	DeviceInfo devinfo = null;
    	devinfo = checkDeviceMounted(mountedpoint);
    	if(devinfo != null){
    		synchronized(devinfo) {
    			devinfo.notifyAll();
    		}
    		mountedDevList.remove(devinfo);
    		devinfo = null;
    	}
    }
    
   /**
    *  @author chendz
    *  @param
    */
    public boolean checkValidDeviceInfo(DeviceInfo devinfo){
    	if(mountedDevList != null){
    		if(mountedDevList.contains(devinfo)){
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     *  @author chendz
     *  @param
     */
     public boolean checkMountedDeviceId(int devid){
    	if( getDeviceInfoByDevID(devid) != null){
    		return true;
    	}
     	return false;
     }
    
     /**
      * 
      */
     
    /**
     * @author chendz
     */
    public DeviceInfo getDeviceInfoByDevID(int devid){
    	if(checkValidDeviceID(devid) && (mountedDevList != null)){
    		for(DeviceInfo di : mountedDevList){
    			if(di.iDevID == devid){
    				return di;
    			}
    		}
    	}
    	return null;
    }
    
    /**
     * @author chendz
     */
    public int getDeviceDBStatusByDevID(int devid){
    	DeviceInfo devinfo = getDeviceInfoByDevID(devid);
    	if(devinfo != null){
    		return devinfo.iDevStatus;
    	}
    	
    	return Constants.DB_NOINITITAL;
    }
    
    /**
     * @author chendz
     * 
     */
    public void resetMountedDevList(){
    	mountedDevList.clear();
    }
    
	/**
	 * @author chendz
	 */  
   private void updateDeviceList(){
		//update device list
       deviceList.clear();
       StorageVolume[] volumes = storageManager.getVolumeList();
       if (volumes == null || volumes.length == 0) {
           return;
       }

       String path = "";
       for (StorageVolume item : volumes) {
           path = item.getPath();
           String state = storageManager.getVolumeState(path);
           if (state == null || !state.equals(Environment.MEDIA_MOUNTED)) {
               continue;
           } else {
               BaseData bd = new BaseData(Constants.FILE_TYPE_DIR);
               String name = path.substring(path.lastIndexOf("/") + 1, path.length());
               bd.setName(name);
               bd.setPath(path);
               deviceList.add(bd);
           }
       }   
   }
   
    /**
     * 
     * 
     */
    public List<BaseData> getDeviceList()
    {
    	updateDeviceList();
    	return deviceList;
    }
    
    /**
     * 
     */
    public boolean checkDefaultUserStorageCardMounted()
    {
    	updateDeviceList();
    	if(deviceList.size() > 0)
    	{
	    	for(BaseData bd : deviceList)
	    	{
	    		if(bd.getPath().equals(Constants.DEFUSER_STORAGE_PATH))
	    		{
	    			return true;
	    		}
	    	}
    	}
    	return false;
    }
    
    /**
     * @author chendz
     * 
     */
    public boolean checkPlaybackStorageCardMounted(int iDeviceID)
    {
    	String strDevicePath = translateDeviceIDToMountPath(iDeviceID);
    	updateDeviceList();
    	if(deviceList.size() > 0)
    	{
	    	for(BaseData bd : deviceList)
	    	{
	    		if(bd.getPath().equals(strDevicePath))
	    		{
	    			return true;
	    		}
	    	}
    	}
    	return false;
    }
    
    /**
     * @author chendz
     * 
     */
    public void resetDatabase()
    {		
    	
    }
    
    /**
     * 
     * 
     */
    public boolean checkValidDeviceID(int devid)
    {
    	if(devid >= 0 && devid <= Constants.MAX_DEVICEID){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    /**
     * 
     * @param ipriorID
     * @return
     */
    private ArrayList<String> buildScanDeviceQueue(int ipriorID){
    	ArrayList<String> scanedlist = new ArrayList<String>();
    	if(checkValidDeviceID(ipriorID)){
    		scanedlist.add(translateDeviceIDToMountPath(ipriorID));
    	}
    	
    	for(int i=0;i <= Constants.MAX_DEVICEID;i++){
    		if(i == ipriorID){
    			continue;
    		}else{
    			scanedlist.add(translateDeviceIDToMountPath(i));
    		}
    	}
    	
    	return scanedlist;
    }
    
    /**
     * 
     * @param iscantype
     * @return
     */
    private int translateScantypeToMediaType(int iscantype){
        switch (iscantype) {
        case Constants.FILE_TYPE_PICTURE:
        	return Constants.MEDIA_TYPE_PHOTO;          
        case Constants.FILE_TYPE_SONG:             
        case Constants.FILE_TYPE_TTSONGS:
        case Constants.FILE_TYPE_SONGDIR:
        	return Constants.MEDIA_TYPE_MUSIC;          
        case Constants.FILE_TYPE_VIDEO:
        case Constants.FILE_TYPE_VIDEODIR:
        case Constants.FILE_TYPE_TTVIDEOS:
        	return Constants.MEDIA_TYPE_VIDEO;      	
        default:
        	return Constants.MEDIA_TYPE_UNKOWN;
        }
    }
    
    /**
     * @author chendz
     * @param
     * @return
     */
    public synchronized void startScanDeviceData(int devid,final int iscantype,final DataLoadCompleteListenser listenser){
    	int iScanDevid = -1;
    	DeviceInfo devinfo = null;
    	ArrayList<String> scaningDevList = null;
    	//resetDatabase();
		
		//check device id from user select
		if(checkValidDeviceID(devid)){
			iScanDevid = devid;
		}else {//select device from last playback device
			iScanDevid = getCurrentPlaybackDeviceID(translateScantypeToMediaType(iscantype));
		}
	
		scaningDevList = buildScanDeviceQueue(iScanDevid);
		new asyncLoadDevData().loadDevListThread(scaningDevList,iscantype,listenser);
		
		//scan all device no file
		//listenser.notifyFailed(Constants.DB_ERROR_NOFILE);
    }
}
