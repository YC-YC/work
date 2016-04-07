package com.zhonghong.mediasdk.photo;

import java.util.HashMap;
import java.util.List;

import com.zhonghong.mediasdk.photo.PhotoItemInfo;
import com.zhonghong.mediasdk.photo.PhotoAsyncDataLoader.FinishedCallback;
import com.zhonghong.mediasdk.photo.IPhotoStatusCB;
import com.zhonghong.mediasdk.photo.PhotoService.LocalBinder;
import com.zhonghong.mediasdk.BaseData;
import com.zhonghong.mediasdk.Constants;
import com.zhonghong.mediasdk.DeviceInfo;
import com.zhonghong.mediasdk.FileManager;
import com.zhonghong.mediasdk.FolderData;
import com.zhonghong.mediasdk.FolderItemInfo;
import com.zhonghong.mediasdk.PlaybackStatus;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.widget.Toast;


public class PhotoServiceHelper {
	static private final String TAG ="Photohelp";
	static final String PHOTO_APP_NAME = "com.zhonghong.newphoto";
	static final String SERVICE_NAME = "com.zhonghong.mediasdk.photo.PhotoService";
  	
	static Context context = null;
	static Handler mUserHandler = null;	
	static PPlaybackInfo mPBInfo = new PPlaybackInfo();
	static PBrowserInfo mBrowserInfo = new PBrowserInfo();
	static private PhotoService mService = null;
	static private PhotoAsyncDataLoader mAsyncDataLoader = new PhotoAsyncDataLoader();
	static private FileManager mFileManager = new FileManager(Constants.FILE_TYPE_PICTURE);
	static private ServiceConnection mConnection = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			 Log.i(TAG,"onServiceConnected");
			 LocalBinder binder = (LocalBinder)arg1;
	         mService = binder.getService(); 	         
			 mService.setPhotoStatusCB(PhotoStatusListener);

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			Log.i(TAG,"onServiceDisconnected");			
			mService.unsetPhotoStatusCB();
			mService = null;
		}	
	};
	
	/**
	 * please invoke it when activity onCreate(),start service
	 */
	static public void HelperCreate(Context ct,Handler handler){			
		context = ct;
		mUserHandler = handler;
		
		if(context == null || mUserHandler == null){
			Log.e(TAG,"HelperCreate [FAIL],Please pass valid Context and handler.");
			return;
		}else{
			Log.i(TAG,"HelperCreate [OK]");
		}
		if(context != null){
			Intent intent = new Intent(SERVICE_NAME);
			context.startService(intent);
		}
	}
	
	/**
	 * please invoke it when activity onResume(),bind service,and send MP3 state to carui
	 */
	static  public void HelperResume(){
		if((context != null)){
			ComponentName name = new ComponentName(PHOTO_APP_NAME, SERVICE_NAME);
			Intent intent = new Intent();
			intent.setComponent(name);
			context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 
			Log.i(TAG,"HelperResume [OK]");
		}else{
			Log.e(TAG,"HelperResume [FAIL],context null");
		}
	}
	
	/**
	 * please invoke it when activity onStop(),unbind service,and send state to carui
	 */
	static  public void HelperStop(){
		Log.i(TAG,"HelperStop");
		try{
			if(context != null && mService != null){
				context.unbindService(mConnection);
				mService = null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * please invoke it when activity onDestory(),else have memory leak issue
	 */
	static public void HelperDestory(){
		context = null;
		mService = null;
		mUserHandler = null;
		Log.i(TAG,"HelperDestory");
	}
	
	/**
	 * 
	 * @param msg
	 */
	public static void userhandleMessage(Message msg) {
		
		switch(msg.what){
		case Constants.USYS_MSG_TIMER:
			checkBrowserDevStatus();
			sendMessageShutCut(Constants.USYS_MSG_TIMER, 0, 0, 1000);
			break;		
		case Constants.CB_MSG_PB_STATUS:
			checkPBDevStatus();
			break;
		case Constants.CB_MSG_PB_ID3UPDATE:
			checkPBItemID3();
			break;
		case Constants.CB_MSG_PB_TIME:
			break;
		case Constants.CB_MSG_PLAYER_READY:
			initialBrowserDevInfo();
			//send update all message
			removeMessageShutCut(Constants.CB_MSG_PB_STATUS);
			sendMessageShutCut(Constants.CB_MSG_PB_STATUS, 0, 0, 1000);
			break;
			
		default:
			break;
		}
	}
	
	/**
	 * Play next item
	 */
	static public void nextPhoto() {
		try {
			if (mService != null){
				//sendMessageShutCut(Constants.CB_MSG_DATA_NOREADY, 0, 0, 0);
				mService.next();
			}
		} catch (Exception e) {
					e.printStackTrace();
		}
	}
	
	/**
	 * play previous item
	 */
	static public void prevPhoto(){
		try {
			if (mService != null){
				//sendMessageShutCut(Constants.CB_MSG_DATA_NOREADY, 0, 0, 0);
				mService.prev();
			}
		} catch (Exception e) {
				e.printStackTrace();
		}	
	}
	
	/**
	 * repeator the single click event
	 */
	static public void singleClickEvent(){
		removeMessageShutCut(Constants.CB_MSG_SINGLECLICK_EVENT);
		sendMessageShutCut(Constants.CB_MSG_SINGLECLICK_EVENT, 0, 0, 10);
	}
	
	/**
	 * play or pause Photo
	 */	
	static public void playpausePhoto() {
		Log.d(TAG,"playpausePhoto: "+mPBInfo.mPBStatus.iPlayStatus);
			if (mPBInfo.mPBStatus.iPlayStatus == Constants.PB_PLAY) {
				pausePhoto();
			} else if (mPBInfo.mPBStatus.iPlayStatus == Constants.PB_PAUSE) {
				continuingPlayPhoto();
			}
	}
	
	/**
	 * Continuing play Photo
	 */
	static public void continuingPlayPhoto(){
		if (mService != null) {
			mService.play();
		}
	}
	
	/**
	 * pause Photo
	 */
	static public void pausePhoto(){
		try {
			if (mService != null ){}
				mService.pause();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @author: victor.chen
	 * @param: set play mode,repeat single/all/random
	 */
	static public void changePlayMode() {

		if (mPBInfo.mPBStatus.iPlayMode == Constants.PM_REPSINGLE) {
			setPlayMode(Constants.PM_REPALL);
		} else if (mPBInfo.mPBStatus.iPlayMode == Constants.PM_REPALL) {
			setPlayMode(Constants.PM_RANDOM);
		} else if (mPBInfo.mPBStatus.iPlayMode == Constants.PM_RANDOM) {
			setPlayMode(Constants.PM_REPSINGLE);
		}
	}
	
	/**
	 * @author: victor.chen
	 * @param: set play mode,repeat single/all/random
	 */
	static private void setPlayMode(int mode) {
		if (mService != null) {
			mService.setRepeatMode(mode);
		}
	}
	
	/**
	 * @author chendz
	 * @param
	 * 
	 */
	static protected BaseData getPhotoItemInfo(int idevid,int index){
		BaseData bd = null;
		if (mService != null) {
			bd = mService.getDetailBaseData(idevid,index);//getItemIndexForDev(idevid,fdpos,fpos));		
		}
		return bd;
	}
		
	/**
	 * 
	 */
	static boolean playPhoto(int idevid,int index,String path){
		boolean bresult = false;
		
		if (mService != null) {
			bresult = mService.playPhoto(idevid,index,path);
		}
		
		return bresult;
	}
		
	/**
	 * @author chendz
	 * 
	 */
	static private List<BaseData> getBrowserFolderPhotoList(int idevid,int fdpos){
		if(fdpos < 0 || fdpos > mBrowserInfo.iFolderTotal){
			Log.e(TAG,"getBrowserFolderPhotoList [FAIL] fdpos:"+fdpos);
			return null;
		}
		return mBrowserInfo.mPhotoFolderList.get(fdpos).getCurrentFolderFileList();
	}
	
	/**
	 * @author chendz
	 * 
	 */
	static private FolderData getBrowserFolderData(int idevid,int fdpos){
		if(fdpos < 0 || fdpos > mBrowserInfo.iFolderTotal){
			Log.e(TAG,"getBrowserFolderData [FAIL], fdpos:"+fdpos);
			return null;
		}
		return mBrowserInfo.mPhotoFolderList.get(fdpos);
	}
	
	/**
	 * @author chendz
	 * 
	 */
	static protected int getItemIndexForDev(int idevid,int fdpos,int fpos){
		int iCount = 0;
		DeviceInfo devinfo = null;
		String strFolderPath = null;
		List<FolderData> mfdlist = null;
		
		if(idevid < 0){
			idevid = mBrowserInfo.iDevId;
		}
		
		devinfo = getDeviceInfo(idevid);
		if(devinfo != null){
			mfdlist = devinfo.mPhotoFolderList;
			if(mfdlist != null){
				if(fdpos < 0 || fdpos >= mfdlist.size()){
					Log.e(TAG,"getItemIndexForDev [FAIL], fdpos:"+fdpos+" fpos: "+fpos);
					return -1;
				}
				strFolderPath = mfdlist.get(fdpos).getCurrentFolderPathStr();
				for (FolderData fd : mfdlist) {
					if (fd.getCurrentFolderPathStr().equals(
							strFolderPath)) {
						iCount += fpos;
						return iCount;
					} else {
						iCount += fd.getCurrentFolderFileList().size();
					}
				}
			}
		}
		Log.e(TAG,"getItemIndexForDev [FAIL], fdpos:"+fdpos+" fpos: "+fpos);
		return -1;
	}
			
	/**
	 * 
	 * @param idevid
	 * @param ifocusfolder
	 * @param ifocusfile
	 * @return
	 */
	static private boolean initialDevBrowserInfo(int idevid,int ifocusfolder,int ifocusfile){
		try{
			if(mService != null){
				//check status db status
				if((mService.checkDevDBStatus(idevid) == Constants.DB_PROCESSING)){
					sendMessageShutCut(Constants.CB_MSG_DATA_NOREADY, 0, 0, 0);
				}else{
					sendMessageShutCut(Constants.CB_MSG_DATA_READY, 0, 0, 0);
				}
				mBrowserInfo.iDevId = idevid;
				mBrowserInfo.mPhotoFolderList = mService.getDevFolderData(idevid);
				mBrowserInfo.mPhotoTotalList = mService.getDevBaseData(idevid);
				if((mBrowserInfo.mPhotoFolderList != null)&&(mBrowserInfo.mPhotoFolderList.size()>0) && (mBrowserInfo.mPhotoTotalList != null)&&(mBrowserInfo.mPhotoTotalList.size() > 0)){
					mBrowserInfo.iFolderTotal = mBrowserInfo.mPhotoFolderList.size();
					if(ifocusfolder < mBrowserInfo.iFolderTotal){
						mBrowserInfo.iFocusFolderIndex = ifocusfolder;
					}else{
						mBrowserInfo.iFocusFolderIndex = 0;
					}
					mBrowserInfo.iFocusFolderFileNum = mBrowserInfo.mPhotoFolderList.get(mBrowserInfo.iFocusFolderIndex).getCurrentFolderFileList().size();
					
					if(ifocusfile < mBrowserInfo.iFocusFolderFileNum){
						mBrowserInfo.iFocusFileIndex = ifocusfile;
					}else{
						mBrowserInfo.iFocusFileIndex = 0;
					}
				}else{   //dev no file
					mBrowserInfo.mPhotoFolderList = null;
					mBrowserInfo.mPhotoTotalList = null;
					mBrowserInfo.iDevDBStatus = Constants.DB_NOINITITAL;
					mBrowserInfo.iFocusFolderIndex = -1;
					mBrowserInfo.iFocusFileIndex = -1;
					mBrowserInfo.iFocusFolderFileNum = 0;
					mBrowserInfo.iFolderTotal = 0;
					sendMessageShutCut(Constants.CB_MSG_BR_DEVNOFILE,mBrowserInfo.iDevId,0,0);
				}
			}
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
	
	
	/**
	 * @author chendz
	 * @param invoke it,please initial it on player ready
	 */
	static private void initialBrowserDevInfo(){
		try{
			if(mBrowserInfo != null){
				selectBrowserDevice(mPBInfo.mPBStatus.iCurrFBDev);
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		Log.d(TAG,"initialBrowserDevInfo.");
	}
	
	/**
	 * @author chendz
	 */
	static private void checkBrowserDevStatus(){
		try{
				if((mService != null) && (mBrowserInfo.iDevId != Constants.INVALID_DEVICE)){
					int iCurrDBStatus = mService.checkDevDBStatus(mBrowserInfo.iDevId);
					if(iCurrDBStatus != mBrowserInfo.iDevDBStatus){
						selectBrowserDevice(mBrowserInfo.iDevId);
						mBrowserInfo.iDevDBStatus = iCurrDBStatus;
						Log.d(TAG,"dev "+mBrowserInfo.iDevId+" database status change,reload data.");
					}/*else if((mBrowserInfo.iFocusFileIndex != mPBInfo.iCurrPBFileIndex)||(mBrowserInfo.iFocusFolderIndex != mPBInfo.iCurrPBFolderIndex )){
						mBrowserInfo.iFocusFileIndex = mPBInfo.iCurrPBFileIndex;
						mBrowserInfo.iFocusFolderIndex = mPBInfo.iCurrPBFolderIndex;
						//send update ui
						//mUserHandler.sendEmptyMessageDelayed(Constants.CB_MSG_FDLIST_REFRESH,100);
						//mUserHandler.sendEmptyMessageDelayed(Constants.CB_MSG_FLLIST_REFRESH,150);
						Log.d(TAG,"update browser list");
					}*/
				}
			}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
	}
	
	/**
     * @author victorchen
     * setInitFolderIndex and song Index
     *  
     */
   static private void updatePBFolderAndSongIndexByBD(List<FolderData> fdd,BaseData bd){
    	boolean bFoundFocusObj = false;
    	int iInitSongIndex = 0;
    	int iInitFolderIndex = 0;
    	if(fdd == null || bd == null){
    		Log.d(TAG,"setInitFodlerIndexAndSongIndex,invalid paramter fdd/bd!");
    		return;
    	}
    	
    	for(FolderData fd : fdd){
    		//Log.d(TAG,"fd.PathStr() "+fd.getCurrentFolderPathStr()+" getParentPath "+bd.getParentPath());
    		if((fd.getCurrentFolderPathStr()).equals(bd.getParentPath())){
    			for(BaseData _bd : fd.getCurrentFolderFileList()){
    				//Log.d(TAG,"_bd.getName() "+_bd.getName()+" bd.getName() "+bd.getName());
    				if((_bd.getName()).equals(bd.getName())){
    					bFoundFocusObj = true;
    					break;
    				}else{
    					iInitSongIndex++;
    				}
    			}
    		}
    		else{
    			iInitFolderIndex++;
    		}
    		
    		//if ready found
    		if(bFoundFocusObj){
    			break;
    		}
    	}
    	//if fail
    	if(bFoundFocusObj == false){
    		iInitSongIndex = -1;
    		iInitFolderIndex= -1;
    	}
    	
		mPBInfo.iCurrPBFileIndex = iInitSongIndex;
		mPBInfo.iCurrPBFolderIndex = iInitFolderIndex;
    }
   
	/**
	 * @author chendz
	 */
	static private void checkPBDevStatus(){

		try{
			if(mService != null){				
				mPBInfo.mPhotoFolderList = mService.getDevFolderData(mPBInfo.mPBStatus.iCurrFBDev);
				mPBInfo.mPhotoTotalList = mService.getDevBaseData(mPBInfo.mPBStatus.iCurrFBDev);
				mPBInfo.sItemInfo.sName = mService.getTrackName(); //first update track name
				if((mPBInfo.mPhotoFolderList != null) && (mPBInfo.mPhotoTotalList != null)){
					updatePBFolderAndSongIndexByBD(mPBInfo.mPhotoFolderList,mPBInfo.mPhotoTotalList.get(mPBInfo.mPBStatus.iCurrPBIndexOnTotal));
				}
				Log.d(TAG,"checkPBDevStatus dev:"+mPBInfo.mPBStatus.iCurrFBDev+ " pos: "+mPBInfo.mPBStatus.iCurrPBIndexOnTotal+" flindex:"+mPBInfo.iCurrPBFileIndex+" fdindex:"+mPBInfo.iCurrPBFolderIndex);
			}
		}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
	}
	

	/**
	 * @author chendz
	 */
	static private void checkPBItemID3(){
		Log.d(TAG,"checkPBItemID3");
		try{
			if(mService != null){				
				mPBInfo.sItemInfo.aPhotoThumbnail = mService.getPhotoThumbnail();
				mPBInfo.sItemInfo.sName = mService.getTrackName();
			}
		}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
	}
	/**
	 * @author victorchen
	 * 
	 */
	static private DeviceInfo getDeviceInfo(int idevid){
		DeviceInfo devinfo = null;
		try{
			if(mService != null){
				devinfo = mService.getDeviceInfo(idevid);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return devinfo;
	}
	//==================================================================================//
	/**
	 * 
	 * @return
	 */
	static public PPlaybackInfo getPlaybackInfo(){
		return mPBInfo;
	}
	
	/**
	 * 
	 * @return
	 */
	static public PlaybackStatus getPlaybackStatusInfo(){
		return mPBInfo.mPBStatus;
	}
	
	/**
	 * 
	 */
	static public PhotoItemInfo getPlaybackPhotoItemInfo(){
		return mPBInfo.sItemInfo;
	}
	/**
	 * @author chendz
	 * @return
	 */
	static public int getBrowserDeviceID(){
		if(mBrowserInfo != null){
			return mBrowserInfo.iDevId;
		}
		return Constants.INTERNAL_DEVICE;
	}
	
	/**
	 * @author  victor.chen
	 * Sync broswer info with playback info
	 */
	static public void syncBRPBInfo(){
		initialBrowserDevInfo();
	}
	
	/**
	 * 
	 * @return
	 */
	static public String getPlaybackFilePath(){
		if (mService != null) {
			return mService.getPlaybackFilePath();
		}
		Log.d(TAG,"getPlaybackFilePath NULL");
		return null;
	}
	
	/**
	 * @author victorchen
	 */
	static public void setPMRepeatALL1(){
		if(mPBInfo.mPBStatus.iPlayMode == Constants.PM_REPALL){
			setPlayMode(Constants.PM_REPSINGLE);
		}else{
			setPlayMode(Constants.PM_REPALL);
		}	
	}
	
	/**
	 * @author victorchen
	 */
	static public void setPMRandomOnOff(){
		if(mPBInfo.mPBStatus.iPlayMode == Constants.PM_RANDOM){
			setPlayMode(Constants.PM_REPALL);
		}else{
			setPlayMode(Constants.PM_RANDOM);
		}
	}
	
	/**
	 * @author victorchen
	 */
	static public void setSystemMute(){
		try{
			if(mService != null){
				if(mPBInfo.mPBStatus.iMute == Constants.VOL_MUTE){
					mService.setMute(Constants.VOL_UNMUTE);
				}else{
					mService.setMute(Constants.VOL_MUTE);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	/**
	 * 
	 */
	static public void deletePlaybackFile(){
		try{
			//del
			mFileManager.setFileManagerSourceData(mPBInfo.mPhotoFolderList,mPBInfo.mPhotoFolderList.get(mPBInfo.iCurrPBFolderIndex),mPBInfo.mPhotoTotalList.get(getPlaybackDevPBIndex()),FileManager.DEL_TYPE);				
			mFileManager.delFile();
			nextPhoto();
		}catch(Exception e){
			e.printStackTrace();
			mFileManager.resetFileManagerData();
			Log.d(TAG,"deletePlaybackFile [FAIL]");
		}	
	}
	
	/**
	 * @author victor.chen
	 * @param idevid:device id -1 use browserid,pos:on list position cb:callback interface
	 * @return  item info
	 */
	static public PhotoItemInfo loadPhotoItemInfoOnPos(int idevid,int pos,Object vh,FinishedCallback cb){
		PhotoItemInfo miinfo = new PhotoItemInfo();
		if((pos <0) ||(pos > mBrowserInfo.iFocusFolderFileNum) || (cb == null)){
			Log.e(TAG,"getFolderItemInfo [FAIL] pos:"+pos+" cb "+cb);
			return null;
		}
		
		if(idevid < 0){
			idevid = mBrowserInfo.iDevId;
		}
		Log.d(TAG,"loadPhotoItemInfoOnPos pos:"+pos);
		try{
			BaseData bd = getBrowserFolderPhotoList(idevid, mBrowserInfo.iFocusFolderIndex).get(pos);
			int index = getItemIndexForDev(idevid, mBrowserInfo.iFocusFolderIndex, pos);
			if(bd != null){
					PhotoItemInfo _miinfo = mAsyncDataLoader.asyncLoadPhotoItemInfo(idevid,vh,index, cb);
					if(_miinfo != null){
						miinfo.aPhotoThumbnail = _miinfo.aPhotoThumbnail;
					}else{
						miinfo.aPhotoThumbnail = null;
					}
				    miinfo.iItemIndex = index;
					miinfo.sName = bd.getName();
			}else{
				Log.e(TAG,"loadPhotoItemInfoOnPos,bd == null invalid index:"+mBrowserInfo.iFocusFolderIndex);
			}
		}catch(IndexOutOfBoundsException e){
			Log.e(TAG,"loadPhotoItemInfoOnPos catch IndexOutOfBoundsException");
		}catch(Exception e){
			e.printStackTrace();
		}

		return miinfo;
	}
	
	/**
	 * @author victor.chen
	 * @param idevid:device id -1 use browserid,pos:on list position cb:callback interface
	 * @return music item info
	 */
	static public PhotoItemInfo loadPlaybackDevPhotoItemInfoOnIndex(int iindex,Object vh,FinishedCallback cb){
		PhotoItemInfo miinfo = new PhotoItemInfo();
		if((iindex <0) ||(iindex >= mPBInfo.mPBStatus.iTotalNum) || (cb == null)){
			Log.e(TAG,"getFolderItemInfo [FAIL] pos:"+iindex+" cb "+cb);
			return null;
		}
			
		try{
			BaseData bd = mPBInfo.mPhotoTotalList.get(iindex);
			if(bd != null){
					PhotoItemInfo _miinfo = mAsyncDataLoader.asyncLoadPhotoItemInfo(mPBInfo.mPBStatus.iCurrFBDev,vh,iindex, cb);
					if(_miinfo != null){
						miinfo.aPhotoThumbnail = _miinfo.aPhotoThumbnail;
					}else{
						miinfo.aPhotoThumbnail = null;
					}
				    miinfo.iItemIndex = iindex;
					miinfo.sName = bd.getName();
			}else{
				Log.e(TAG,"loadPhotoItemInfoOnPos,bd == null invalid index:"+mBrowserInfo.iFocusFolderIndex);
			}
		}catch(IndexOutOfBoundsException e){
			Log.e(TAG,"loadPhotoItemInfoOnPos catch IndexOutOfBoundsException");
		}catch(Exception e){
			e.printStackTrace();
		}

		return miinfo;
	}
	/**
	 * @author victor.chen
	 * @param idevid:device id, pos:folder offset index
	 */
	static public FolderItemInfo LoadFolderItemOnPos(int idevid,int pos){
		int ilastbashpos =0;
		if((pos <0) ||(pos > mBrowserInfo.iFolderTotal)){
			Log.e(TAG,"LoadFolderItemOnPos [FAIL] pos:"+pos);
			return null;
		}
		FolderItemInfo fiinfo = new FolderItemInfo();
		fiinfo.iFolderIndex = pos;
		fiinfo.iIncludeFileNum = getBrowserFolderPhotoList(idevid,pos).size();
		fiinfo.sFolderPathName = getBrowserFolderData(idevid,pos).getCurrentFolderPathStr();
		ilastbashpos = fiinfo.sFolderPathName.lastIndexOf("/");
		fiinfo.sFolderName = fiinfo.sFolderPathName.substring(ilastbashpos+1);
		return fiinfo;
	}
	
	/**
	 * @author victor.chen
	 * @param get current foucs folder file count
	 */
	static public int getBrowserFocusFolderItemCount(){
		return mBrowserInfo.iFocusFolderFileNum;
	}
	/**
	 * @author victor.chen
	 * @param void
	 */
	static public int getBrowserFolderItemCount(){
		return mBrowserInfo.iFolderTotal;
	}
	
	/**
	 * @author victor.chen
	 * @param void
	 */
	static public int getBrowserFocusFolderIndex(){
		return mBrowserInfo.iFocusFolderIndex;
	}
	
	/**
	 * @author victor.chen
	 * @param void
	 */
	static public int getBrowserFocusFileIndex(){
		return mBrowserInfo.iFocusFileIndex;
	}
	
	/**
	 * @author chendz
	 * @param
	 */
	static public int getBrowerDevID(){
		return mBrowserInfo.iDevId;
	}
	
	/**
	 * 
	 * @return
	 */
	static public int getPlaybackDevID(){
		return mPBInfo.mPBStatus.iCurrFBDev;
	}
	
	/**
	 * 
	 * @return
	 */
	static public int getPlaybackDevTotalNum(){
		return mPBInfo.mPBStatus.iTotalNum;
	}
	
	/**
	 * @author chendz
	 * @return
	 */
	static public int getPlaybackDevPBIndex(){
		return mPBInfo.mPBStatus.iCurrPBIndexOnTotal;
	}
	
	/**
	 * @author chendz
	 * @param
	 */
	static public boolean setSelectedFolderIndex(int idevid,int pos){
		if((pos <0) ||(pos > mBrowserInfo.iFolderTotal)){
			Log.e(TAG,"setSelectedFolderIndex [FAIL] pos:"+pos);
			return false;
		}
		//update index and focus folder file num
		mBrowserInfo.iFocusFolderIndex = pos;
		mBrowserInfo.iFocusFolderFileNum = getBrowserFolderPhotoList(-1,pos).size();
		sendMessageShutCut(Constants.CB_MSG_FDLIST_REFRESH, 0, 0, 50);
		sendMessageShutCut(Constants.CB_MSG_FLLIST_REFRESH, 0, 0, 100);
		return true;
	}
	
	/**
	 * @author chendz
	 * @param
	 */
	static public boolean setSelectedFileIndex(int idevid,int pos){
		int iTotalindex = -1;
		if((pos <0) ||(pos > mBrowserInfo.iFocusFolderFileNum)){
			Log.e(TAG,"setSelectedFileIndex [FAIL] pos:"+pos);
			return false;
		}
		
		//play it
		mBrowserInfo.iFocusFileIndex = pos;
		iTotalindex = getItemIndexForDev(mBrowserInfo.iDevId,mBrowserInfo.iFocusFolderIndex,pos);
		//if((mBrowserInfo.iDevId == mPBInfo.mPBStatus.iCurrFBDev) && (iTotalindex == mPBInfo.mPBStatus.iCurrPBIndexOnTotal)){
		//	Log.d(TAG,"request index is equals playing index,do nothing. ");
		//}else{
			playPhoto(mBrowserInfo.iDevId,iTotalindex,null);
		//}
		return true;
	}
	/**
	 * @author wendan
	 * @param idevid
	 * @param iTotalindex  ËùÓÐPhotoµÄÄÄÒ»¸öindex
	 * @return
	 */
	static public boolean setSelectedTotalIndex(int idevid,int iTotalindex){
		if((iTotalindex <0) ||(iTotalindex >= mPBInfo.mPhotoTotalList.size())){
			Log.e(TAG,"setSelectedFileIndex [FAIL] iTotalindex:"+iTotalindex);
			return false;
		}
		Log.d(TAG, "###################iTotalindex="+iTotalindex);
		if((mBrowserInfo.iDevId == mPBInfo.mPBStatus.iCurrFBDev) && (iTotalindex == mPBInfo.mPBStatus.iCurrPBIndexOnTotal)){
			Log.d(TAG,"request index is equals playing index,do nothing. ");
		}else{
			playPhoto(mPBInfo.mPBStatus.iCurrFBDev,iTotalindex,null);
		}
		return true;
	}
	
	/**
	 * 
	 * @param idevid
	 * @param idevid: sd/usb/hdm(internal flash)
	 * @return
	 */
	static public boolean setPlaybackDevice(int idevid){
		return true;
	}
	
	/**
	 * 
	 */
	/**
	 * @author victor.chen
	 * @param idevid: sd/usb/hdm(internal flash)
	 * @return
	 */
	static public boolean selectBrowserDevice(int idevid){
		int irdevid = -1;
		if(mService != null){
			irdevid = mService.checkValidDeviceID(idevid);
		}
		if(irdevid != idevid){
			Log.w(TAG,"selectBrowserDevice [FAIL],irdevid "+irdevid+" idevid "+idevid+" mService "+mService);
		}else{
			if(idevid == mPBInfo.mPBStatus.iCurrFBDev){
				initialDevBrowserInfo(idevid,mPBInfo.iCurrPBFolderIndex,mPBInfo.iCurrPBFileIndex);
			}else{
				initialDevBrowserInfo(idevid,0,0);
			}
			//send update ui msg
			sendMessageShutCut(Constants.CB_MSG_DEVLIST_REFRESH,idevid,0,0);
			sendMessageShutCut(Constants.CB_MSG_FDLIST_REFRESH, 0, 0, 100);
			sendMessageShutCut(Constants.CB_MSG_FLLIST_REFRESH, 0, 0, 150);
	
			//monitor current selected devices
			sendMessageShutCut(Constants.USYS_MSG_TIMER, 0, 0, 1000);
		}
		return true;
	}
	
	/**
	 * 
	 * @param icmd
	 */
	static private void removeMessageShutCut(int icmd){
		if(mUserHandler != null){
			mUserHandler.removeMessages(icmd);
		}
	}
	
	/**
	 * 
	 */
	public static void sendMessageShutCut(int icmd,int arg1,int arg2,int idelay){
		if(mUserHandler != null){
			Message msg = mUserHandler.obtainMessage();
			msg.what = icmd;
			msg.arg1 = arg1;
			msg.arg2 = arg2;
			if(idelay == 0){
				mUserHandler.sendMessage(msg);
			}else{
				mUserHandler.sendMessageDelayed(msg, idelay);
			}
		}
	}
	
	/**
	 * 
	 * @param icmd
	 * @param arg1
	 * @param arg2
	 * @param idelay
	 */
	static public void sendMessageObjShutCut(int icmd,int arg1,int arg2,Object obj,int idelay){
		if(mUserHandler != null){
			Message msg = mUserHandler.obtainMessage();
			msg.what = icmd;
			msg.obj = obj;
			msg.arg1 = arg1;
			msg.arg2 = arg2;
			if(idelay == 0){
				mUserHandler.sendMessage(msg);
			}else{
				mUserHandler.sendMessageDelayed(msg, idelay);
			}
		}
	}
	/**
	 * Service callback interface
	 */
	private static IPhotoStatusCB PhotoStatusListener = new IPhotoStatusCB() {

		@Override
		public void photoPrepared() {
			sendMessageShutCut(Constants.CB_MSG_DATA_READY, 0, 0, 200);
		}

		@Override
		public void photoPlayErrorWithMsg(String errMessage) {
			Log.d(TAG, "IPhotoStatusListener.Stub, PhotoPlayErrorWithMsg");
			
			PhotoServiceHelper.nextPhoto();
		}
		
		@Override
		public void photoCompleted() {
			Log.d(TAG, "IPhotoStatusListener.Stub, PhotoCompleted");
			PhotoServiceHelper.nextPhoto();
		}
	
		@Override
		public void updatePlaybackStatus(PlaybackStatus pbstatus) {
			mPBInfo.mPBStatus = pbstatus;
			
			if(!(mPBInfo.mPBStatus.equals(mPBInfo.mPBStatusOld))){
				
				if(mPBInfo.mPBStatus.iCurrentTime != mPBInfo.mPBStatusOld.iCurrentTime 
				   ||mPBInfo.mPBStatus.iTotalTime != mPBInfo.mPBStatusOld.iTotalTime){
					//Log.d(TAG, "updatePlaybackStatus time");
					sendMessageShutCut(Constants.CB_MSG_PB_TIME, 0, 0, 0);
				}
				
				if(mPBInfo.mPBStatus.iCurrFBDev != mPBInfo.mPBStatusOld.iCurrFBDev
				||mPBInfo.mPBStatus.iCurrPBIndexOnTotal != mPBInfo.mPBStatusOld.iCurrPBIndexOnTotal){
					sendMessageShutCut(Constants.CB_MSG_PB_STATUS, 0, 0, 0);
				}
				
				if(mPBInfo.mPBStatus.iMute != mPBInfo.mPBStatusOld.iMute){
					sendMessageShutCut(Constants.CB_MSG_MUTE_STATUS, mPBInfo.mPBStatus.iMute, 0, 0);
				}
				
				if(mPBInfo.mPBStatus.iPlayMode != mPBInfo.mPBStatusOld.iPlayMode){
					sendMessageShutCut(Constants.CB_MSG_PLAYMODE_STATUS, mPBInfo.mPBStatus.iPlayMode, 0, 0);
				}
				
				if(mPBInfo.mPBStatus.iPlayStatus != mPBInfo.mPBStatusOld.iPlayStatus){
					sendMessageShutCut(Constants.CB_MSG_PLAYPAUSE_STATUS, mPBInfo.mPBStatus.iPlayStatus, 0, 0);
				}
				
				if(mPBInfo.mPBStatus.iEQMode != mPBInfo.mPBStatusOld.iEQMode){
					sendMessageShutCut(Constants.CB_MSG_EQMODE, 0, 0, 0);
				}
				
				PlaybackStatus.CopyValue(mPBInfo.mPBStatus,mPBInfo.mPBStatusOld);
			}
			//Log.d(TAG, "IPhotoStatusListener.Stub, updatePlaybackStatus");
		}
		
		@Override
		public void handleMessageInfo(String strMessage){
			
			if(strMessage.equals(Constants.NO_PHOTO_FILE)){
				sendMessageShutCut(Constants.CB_MSG_NO_FILE, 0, 0, 0);
				Log.d(TAG,"switch to no file");
			}else if(strMessage.equals(Constants.PLAYER_READY)){
				sendMessageShutCut(Constants.CB_MSG_PLAYER_READY, 0, 0, 500);			
				Log.d(TAG,"player ready");
			}else if(strMessage.equals(Constants.PB_ID3UPDATE)){
				sendMessageShutCut(Constants.CB_MSG_PB_ID3UPDATE, 0, 0, 0);
				Log.d(TAG,"ID3 update");
			}else if(strMessage.equals(Constants.CLOSE_DRIVING_WARNING)){
				if(mPBInfo.bShowDrivingWarning){
					removeMessageShutCut(Constants.CB_MSG_DRIVING_WARNING);
					sendMessageShutCut(Constants.CB_MSG_DRIVING_WARNING, 0, 0, 500);
					mPBInfo.bShowDrivingWarning = false;
					Log.d(TAG,"close driving warning msg");
				}
			}else if(strMessage.equals(Constants.SHOW_DRIVING_WARNING)){
				if(!mPBInfo.bShowDrivingWarning){
					removeMessageShutCut(Constants.CB_MSG_DRIVING_WARNING);
					sendMessageShutCut(Constants.CB_MSG_DRIVING_WARNING, 1, 0, 500);
					mPBInfo.bShowDrivingWarning = true;
					Log.d(TAG,"show driving warning msg");
				}
			}else if(strMessage.equals(Constants.RESCAN_MEDIA_DEV)){
				sendMessageShutCut(Constants.CB_MSG_SCAN_LOADING,0,0,0);
				Log.d(TAG,"Rescan media device");
			}else{
				Log.d(TAG, "IPhotoStatusListener.Stub,loss handleMessageInfo "+strMessage);
			}
		}
		
		@Override
		public void photoFinish(){
			sendMessageShutCut(Constants.CB_MSG_PB_EXIT, 0, 0, 0);
		}
	};
	
	/*********************************************************************************************/
	/**   File manager interface  Start    *******************************************************/
	/*********************************************************************************************/
	/**
	 * @author victorchen
	 */
	static public boolean addNewFDToFolderDataList(FolderData fd){
		if(fd == null){
			Log.e(TAG,"addNewFDToFolderDataList [FAIL],fd:"+fd);
			return false;
		}
		Log.d(TAG,"addNewFDToFolderDataList,fd:"+fd);
		
		if(mBrowserInfo.mPhotoFolderList != null){
			mBrowserInfo.mPhotoFolderList.add(fd);
			mBrowserInfo.iFolderTotal = mBrowserInfo.mPhotoFolderList.size();
		}else{
			Log.d(TAG,"addNewFDToFolderDataList [FAIL],mMusicFolderList:"+mBrowserInfo.mPhotoFolderList);
			return false;
		}
		return true;
	}
	
	/** 
	 * @param bd
	 */
	static public boolean addNewBDToFocusFolderData(BaseData bd){

		FolderData fd = getBrowserFolderData(-1,mBrowserInfo.iFocusFolderIndex);
		
		Log.d(TAG,"addNewBDToFocusFolderData,bd:"+bd);
		if(fd != null && bd != null ){
			fd.insertFileToCurrentFolder(bd);
		}else{
			Log.d(TAG,"addNewBDToFocusFolderData [FAIL],fd:"+fd+" bd:"+bd);
			return false;
		}
		mBrowserInfo.iFocusFolderFileNum = fd.getCurrentFolderFileList().size();
		return true;
	}
	
	/**
	 * 
	 */
	static public void delBDFromSrcFolderDataList(List<FolderData> fdlist,FolderData fd,BaseData bd){
		if((fd == null) || (bd == null)){
			Log.d(TAG,"delBDFromSrcFolderDataList [FAIL],fd "+fd+" bd "+bd);
		}
		try{
			if(fd.getCurrentFolderFileList() != null){
				fd.getCurrentFolderFileList().remove(bd);
				mBrowserInfo.iFocusFolderFileNum = fd.getCurrentFolderFileList().size();
				if(fd.getCurrentFolderFileList().size() == 0){
					if(fdlist != null && fdlist.size()>0){
						fdlist.remove(fd);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			mFileManager.resetFileManagerData();
		}
		mBrowserInfo.iFolderTotal = fdlist.size();
	}
	
	/**
	 * 
	 */
	static public void delFDFromSrcFolderDataList(List<FolderData> fdlist,FolderData fd){
		if((fd == null)){
			Log.d(TAG,"delBDFromSrcFolderDataList [FAIL],fd "+fd);
		}
		
		try{
			if(fdlist != null && fdlist.size()>0){
				fdlist.remove(fd);
			}
		}catch(Exception e){
			e.printStackTrace();
			mFileManager.resetFileManagerData();
		}
		mBrowserInfo.iFolderTotal = fdlist.size();
		mBrowserInfo.iFocusFolderFileNum = 0;
	}
	
	/**
	 * @author wendan
	 */
	static public void copyfile(int position ,boolean bIsFile){
		try{
			if(bIsFile){ // file
				int iTotalindex = -1;
				if((position <0) ||(position > mBrowserInfo.iFocusFolderFileNum)){
					Log.e(TAG,"setSelectedFileIndex [FAIL] position:"+position);
				}
				
				//copy
				iTotalindex = getItemIndexForDev(mBrowserInfo.iDevId,mBrowserInfo.iFocusFolderIndex,position);
				mFileManager.setFileManagerSourceData(mBrowserInfo.mPhotoFolderList,getBrowserFolderData(-1,mBrowserInfo.iFocusFolderIndex),mBrowserInfo.mPhotoTotalList.get(iTotalindex),FileManager.COPY_TYPE);
			}else{//Folder
				mFileManager.setFileManagerSourceData(mBrowserInfo.mPhotoFolderList,mBrowserInfo.mPhotoFolderList.get(position),null,FileManager.COPY_TYPE);
			}
		}catch(Exception e){
			e.printStackTrace();
			mFileManager.resetFileManagerData();
			Log.e(TAG,"copyfile [FAIL].");
		}
	}
	
	/**
	 * @author wendan
	 */
	static public void delfile(int position,boolean bIsFile){
		Log.d(TAG, "##########delfile##position=="+position+"bisfile=="+bIsFile);
		try{
			if(bIsFile){ //file
				int iTotalindex = -1;
				if((position <0) ||(position > mBrowserInfo.iFocusFolderFileNum)){
					Log.e(TAG,"setSelectedFileIndex [FAIL] position:"+position);
				}
				//del
				iTotalindex = getItemIndexForDev(mBrowserInfo.iDevId,mBrowserInfo.iFocusFolderIndex,position);
				mFileManager.setFileManagerSourceData(mBrowserInfo.mPhotoFolderList,getBrowserFolderData(-1,mBrowserInfo.iFocusFolderIndex),mBrowserInfo.mPhotoTotalList.get(iTotalindex),FileManager.DEL_TYPE);
				
			}else{
				mFileManager.setFileManagerSourceData(mBrowserInfo.mPhotoFolderList,mBrowserInfo.mPhotoFolderList.get(position),null,FileManager.DEL_TYPE);
			}
			mFileManager.delFile();
		}catch(Exception e){
			e.printStackTrace();
			mFileManager.resetFileManagerData();
			Log.d(TAG,"delfile [FAIL]");
		}
	}
	
	/**
	 * @author wendan
	 */
	static public void pastefile(int position,boolean bIsFile){
		String parentpath="";
		if(bIsFile){
			parentpath = getBrowserFolderData(-1,mBrowserInfo.iFocusFolderIndex).getCurrentFolderPathStr();
		}else{
			parentpath = getBrowserFolderData(-1,position).getCurrentFolderPathStr();
		}
		//mFileManager.targetPath = parentpath;
		mFileManager.setFileManagerDestData(parentpath);
		mFileManager.pasteFile(false);
	}
	
	/**
	 * @author wendan
	 */
	static public void cutfile(int position,boolean bIsFile){
		try{
			if(bIsFile){ // file
				int iTotalindex = -1;
				if((position <0) ||(position > mBrowserInfo.iFocusFolderFileNum)){
					Log.e(TAG,"setSelectedFileIndex [FAIL] position:"+position);
				}				
				//copy
				iTotalindex = getItemIndexForDev(mBrowserInfo.iDevId,mBrowserInfo.iFocusFolderIndex,position);
				mFileManager.setFileManagerSourceData(mBrowserInfo.mPhotoFolderList,getBrowserFolderData(-1,mBrowserInfo.iFocusFolderIndex),mBrowserInfo.mPhotoTotalList.get(iTotalindex),FileManager.CUT_TYPE);
			}else{//Folder
				mFileManager.setFileManagerSourceData(mBrowserInfo.mPhotoFolderList,mBrowserInfo.mPhotoFolderList.get(position),null,FileManager.CUT_TYPE);
			}
		}catch(Exception e){
			e.printStackTrace();
			Log.e(TAG,"cutfile [FAIL].");
		}
	}
	
	/**
	 * 
	 * @param position
	 */
	static public void forcePasteFileFromUserCmd(int position){
		Log.d(TAG, "##########forcePasteFileFromUserCmd###############");
		mFileManager.pasteFile(true);
	}
	
	/**
	 * @author wendan
	 * update total list
	 */
	static public void update_TotalList(){
		Log.d(TAG, "##########update_TotalList###############");
		mBrowserInfo.mPhotoTotalList.clear();
		for(FolderData fdd : mBrowserInfo.mPhotoFolderList){
			mBrowserInfo.mPhotoTotalList.addAll(fdd.getCurrentFolderFileList());
		}

		if(mBrowserInfo.iFocusFolderIndex >= mBrowserInfo.iFolderTotal){
			mBrowserInfo.iFocusFolderIndex = mBrowserInfo.iFolderTotal-1;
		}
		if(mBrowserInfo.iFocusFileIndex >= mBrowserInfo.iFocusFolderFileNum){
			mBrowserInfo.iFocusFileIndex = mBrowserInfo.iFocusFolderFileNum-1;
		}
		
		setSelectedFolderIndex(-1,mBrowserInfo.iFocusFolderIndex);
	}
	
	/*********************************************************************************************/
	/**   File manager interface  End      *******************************************************/
	/*********************************************************************************************/
}
