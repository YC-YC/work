package com.zhonghong.media.photo;

import com.zhonghong.media.photo.PhotoBrowserFragment;
import com.zhonghong.media.photo.PhotoLoadingFragment;
import com.zhonghong.media.photo.PhotoPlaybackFragment;
import com.zhonghong.media.util.CustomProgressDialog;
import com.zhonghong.mediasdk.Constants;
import com.zhonghong.mediasdk.PlaybackStatus;
import com.zhonghong.mediasdk.photo.PhotoActivity;
import com.zhonghong.mediasdk.photo.PhotoItemInfo;
import com.zhonghong.mediasdk.photo.PhotoServiceHelper;
//import com.zhonghong.mediasdk.video.VideoServiceHelper;
import com.zhonghong.media.photo.setwallpaper.SetWallPaperDialog;
import com.zhonghong.newphoto.R;

import android.R.bool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class PhotoLauncher extends PhotoActivity {
	protected static final String TAG = "PhotoLauncher";
	/**
	 *  fragment标示
	 */
	public static final int LOADING_FRAGMENT = 0;				//图片加载界面
	public static final int PLAYBACK_FRAGMENT= 1;				//图片回放界面
	public static final int BROWSER_FRAGMENT = 2;				//图片列表界面
	      
	private PhotoLoadingFragment mLoadingFragment = null;   		//LAUNCHER界面
	private PhotoBrowserFragment mBrowserFragment = null;      //
	private PhotoPlaybackFragment mPlaybackFragment = null;
	private CustomProgressDialog dlgProgress = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState,mHandler);
		requestWindowFeature(Window.FEATURE_NO_TITLE);    // 隐藏标题
		setContentView(R.layout.activity_photo_main);		
		switchFragment(LOADING_FRAGMENT);	
		
//		View decorView = getWindow().getDecorView(); //获取顶层窗口
//		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
//		decorView.setSystemUiVisibility(uiOptions);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if(dlgProgress != null){
			dlgProgress.cancel();
			dlgProgress = null;
		}
	}
	
	public Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			//dispacher msg to PhotoServiceHelper
			PhotoServiceHelper.userhandleMessage(msg);
			
			switch(msg.what){
			/**************************************************/
			/**      CONTROL COMMAND UI --->SERVICE          **/
			/**************************************************/
				case Constants.CMD_MSG_NEXT_ITEM:
					PhotoServiceHelper.nextPhoto();
				break;
				case Constants.CMD_MSG_PREV_ITEM:
					PhotoServiceHelper.prevPhoto();
				break;
				case Constants.CMD_MSG_PLAY_MODE:
					PhotoServiceHelper.changePlayMode();
				break;
				case Constants.CMD_MSG_PLAY_PAUSE:
					PhotoServiceHelper.playpausePhoto();
				break;
				case Constants.CMD_MSG_PAUSE:
					PhotoServiceHelper.pausePhoto();
				break;
				case Constants.CMD_MSG_SEEK:
				break;
				case Constants.CMD_MSG_SEL_DEV:
					PhotoServiceHelper.selectBrowserDevice(msg.arg1);
				break;
				case Constants.CMD_MSG_SEL_FOLDER:
					PhotoServiceHelper.setSelectedFolderIndex(-1,msg.arg1);
				break;
				case Constants.CMD_MSG_SEL_FILE:
					PhotoServiceHelper.setSelectedFileIndex(-1,msg.arg1);
				break;
				case Constants.CMD_MSG_SYNC_BRPB_INFO:
					PhotoServiceHelper.syncBRPBInfo(); //sync broswer info with playback info
				break;
				case Constants.CMD_MSG_MUTE:
					PhotoServiceHelper.setSystemMute();
				break;
				case Constants.CMD_MSG_RANDOM:
					PhotoServiceHelper.setPMRandomOnOff();
				break;
				case Constants.CMD_MSG_REPEAT_ALL_1:
					PhotoServiceHelper.setPMRepeatALL1();
				break;
				case Constants.CMD_MSG_ZOOM_IN:
					setPhotoZoomIn();
					break;
				case Constants.CMD_MSG_ZOOM_OUT:
					setPhotoZoomOut();
					break;
				case Constants.CMD_MSG_LEFT_ROTATE:
					setPhotoLeftRotate();
					break;
				case Constants.CMD_MSG_RIGHT_ROTATE:
					setPhotoRightRotate();
					break;
				case Constants.CMD_MSG_DELETE:
					break;
				case Constants.CMD_MSG_PHOTO_SETTING:
					setPhotoSetting();
					break;
			/**************************************************/
			/**      CALLBACK COMMAND SERVICE --->UI         **/
			/**************************************************/
				case Constants.CB_MSG_NO_FILE:
					if(checkVaildUpdateEvent(LOADING_FRAGMENT)){
						updateLoadingFragmentShowNoFile();
					}else{
						Log.w(TAG,"Don't on music loading fragment.");
					}
				break;
				case Constants.CB_MSG_PB_EXIT:
					finish();
				break;
				case Constants.CB_MSG_PB_INFO:
				break;
				case Constants.CB_MSG_PB_LRC:
				break;
				case Constants.CB_MSG_PB_ID3UPDATE:
				break;
				case Constants.CB_MSG_PB_STATUS:
					if(checkVaildUpdateEvent(PLAYBACK_FRAGMENT)){
						updatePBStatusInfo();
					}
				break;
				case Constants.CB_MSG_PB_TIME:
				break;
				case Constants.CB_MSG_PLAYER_READY:
						updateSwitchToPlayback();
				break;
				case Constants.CB_MSG_DATA_NOREADY:
						updateDisplayLoadingAnimation(true);
				break;
				case Constants.CB_MSG_DATA_READY:
						updateDisplayLoadingAnimation(false);
				break;
				case Constants.CB_MSG_DEVLIST_REFRESH:
					if(checkVaildUpdateEvent(BROWSER_FRAGMENT)){
						updateBrowserDeviceListRefresh(msg.arg1);
					}
				break;
				case Constants.CB_MSG_FDLIST_REFRESH:
					if(checkVaildUpdateEvent(BROWSER_FRAGMENT)){
						updateBrowserFolderListRefresh();
					}
				break;
				case Constants.CB_MSG_FLLIST_REFRESH:
					if(checkVaildUpdateEvent(BROWSER_FRAGMENT)){
						updateBrowserFileListRefresh();
					}
				break;
				case Constants.CB_MSG_BR_DEVNOFILE:
					if(checkVaildUpdateEvent(BROWSER_FRAGMENT)){
						updateBrowserDevNoFile(msg.arg1);
					}
				break;
				case Constants.CB_MSG_PLAYPAUSE_STATUS:
					if(checkVaildUpdateEvent(PLAYBACK_FRAGMENT)){
						updatePlaypauseStatus(msg.arg1);
					}
				break;
				case Constants.CB_MSG_MUTE_STATUS:
					break;
				case Constants.CB_MSG_PLAYMODE_STATUS:
					break;
				case Constants.CB_MSG_EQMODE:
					break;
				case Constants.CB_MSG_SCAN_LOADING:
					if(checkVaildUpdateEvent(LOADING_FRAGMENT)){
						updateLoadingFragmentShowScanning();
					}
				case Constants.CB_MSG_DRIVING_WARNING:
					break;
				case Constants.CB_MSG_SINGLECLICK_EVENT:
					if(checkVaildUpdateEvent(PLAYBACK_FRAGMENT)){
						updateControlBarStatus();
					}
					break;
				default:
					break;
			}
		}
	};
	
	/**
	 * @param iCmd:send command
	 * @param iparam0 iparam1 obj
	 * @param bRemoveOldMsg: true,remove old same msg;false do nothing
	 */
	public void sendCmdToService(int iCmd,int iparam0,int iparam1,Object obj,boolean bRemoveOldMsg){
		if(mHandler != null){
			Message msg = mHandler.obtainMessage();
			msg.what = iCmd;
			msg.arg1 = iparam0;
			msg.arg2 = iparam1;
			msg.obj = obj;
			if(bRemoveOldMsg){
				mHandler.removeMessages(msg.what);
			}
			mHandler.sendMessage(msg);
		}
	}
	
	/**
	 * 
	 */
	public void updateControlBarStatus(){
		if(mPlaybackFragment != null){
			mPlaybackFragment.updateUIControlBarStatus();
		}
	}
	
	/**
	 * @author victorchen
	 */
	public void updatePlaypauseStatus(int istatus){
		if(mPlaybackFragment != null){
			mPlaybackFragment.updateUIPlaypauseIcon(istatus);
		}
	}
	/**
	 * 
	 * @param bshow
	 */
	public void updateDisplayLoadingAnimation(boolean bshow){
		if(bshow){
			if(dlgProgress == null){
				dlgProgress = CustomProgressDialog.createDialog(this);
			}
			if(dlgProgress != null){
				dlgProgress.show();
			}
		}else{
			if(dlgProgress != null){
				dlgProgress.dismiss();
			}
		}
	}

	/**
	 * update playback status info
	 */
	public void updatePBStatusInfo(){
		if(mPlaybackFragment != null){
			mPlaybackFragment.showImg();
		}
	}
	/**
	 * @author victorchen
	 * @param show no file
	 */
	public void updateLoadingFragmentShowNoFile(){
		if(mLoadingFragment != null){
			mLoadingFragment.displayNoMusicPrompt();
		}
	}
	
	/**
	 * @author victorchen
	 */
	public void updateSwitchToPlayback(){
		if(mLoadingFragment != null && mLoadingFragment.isVisible()){
			mLoadingFragment.stopScanningAnimation();		
		}
		switchFragment(PLAYBACK_FRAGMENT);
	}
	/**
	 * @author victorchen
	 * @param show no file
	 */
	public void updateLoadingFragmentShowScanning(){
		if(mLoadingFragment != null){
			mLoadingFragment.displayScanningLoadPrompt();
		}
	}
	
	/**
	 * @author victorchen
	 * {@code} update broswer folder list data
	 */
	public void updateBrowserDevNoFile(int idevid){
		if(mBrowserFragment != null){
			mBrowserFragment.updateUIBrowserDevNoFile(idevid);
		}
	}
	
	/**
	 * @author victorchen
	 * {@code} update broswer device list data
	 */
	public void updateBrowserDeviceListRefresh(int idevid){
		if(mBrowserFragment != null){
			mBrowserFragment.updateUIBrowserDeviceListRefresh(idevid);
		}
	}
	/**
	 * @author victorchen
	 * {@code} update broswer folder list data
	 */
	public void updateBrowserFolderListRefresh(){
		if(mBrowserFragment != null){
			mBrowserFragment.updateUIBrowserFolderListRefresh(PhotoServiceHelper.getBrowserFocusFolderIndex());
		}
	}
	
	/**
	 * @author victorchen
	 * @param
	 * {@code} update broswer file list data
	 */
	public void updateBrowserFileListRefresh(){
		if(mBrowserFragment != null){
			mBrowserFragment.updateUIBrowserFileListRefresh(PhotoServiceHelper.getBrowserFocusFileIndex());
		}
	}
	
	/**
	 * update playback info
	 */
	public void updatePBInfo(){
		PhotoItemInfo pbinfo= PhotoServiceHelper.getPlaybackPhotoItemInfo();
		Log.d(TAG,"updatePBInfo");
		if(mPlaybackFragment != null && pbinfo!=null){
			//mPlaybackFragment.updateUIPBInfo(pbinfo.sName);
		}
	}
		
	/**
	 * update driving warning msg
	 */
	public void updateDrivingWarning(int ishow){
		if(mPlaybackFragment != null){
			//mPlaybackFragment.updateUIDrivingWarning(ishow);
		}
	}
	
	/**
	 * 
	 */
	public void setPhotoZoomIn(){
		if(checkVaildUpdateEvent(PLAYBACK_FRAGMENT)){
			PhotoServiceHelper.pausePhoto();
			mPlaybackFragment.ZoomIn();
		}
	}
	
	/**
	 * 
	 */
	public void setPhotoZoomOut(){
		if(checkVaildUpdateEvent(PLAYBACK_FRAGMENT)){
			PhotoServiceHelper.pausePhoto();
			mPlaybackFragment.ZoomOut();
		}
	}
	
	/**
	 * 
	 */
	public void setPhotoLeftRotate(){
		if(checkVaildUpdateEvent(PLAYBACK_FRAGMENT)){
			PhotoServiceHelper.pausePhoto();
			mPlaybackFragment.rotateImage(-90);
		}
	}
	
	/**
	 * 
	 */
	public void setPhotoRightRotate(){
		if(checkVaildUpdateEvent(PLAYBACK_FRAGMENT)){
			PhotoServiceHelper.pausePhoto();
			mPlaybackFragment.rotateImage(90);
		}
	}
	
	/**
	 * 
	 */
	private void showSetWallPaperDialog(String path){
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();

		SetWallPaperDialog dialog_setwallpaper = new SetWallPaperDialog(this,
				path,
				width, height);
		dialog_setwallpaper.show();
	}

	/**
	 * 
	 */
	public void setPhotoSetting(){
		if(checkVaildUpdateEvent(PLAYBACK_FRAGMENT)){
			PhotoServiceHelper.pausePhoto();
			showSetWallPaperDialog(PhotoServiceHelper.getPlaybackFilePath());
		}
	}
	/**
	 * check valid update event
	 */
	public boolean checkVaildUpdateEvent(int fragindex){
		boolean value =false;
		switch(fragindex){
		case LOADING_FRAGMENT:
			if(mLoadingFragment != null && mLoadingFragment.isVisible()){
				value = true;
			}
			break;
		case PLAYBACK_FRAGMENT:
			if(mPlaybackFragment != null && mPlaybackFragment.isVisible()){
				value = true;
			}
			break;
		case BROWSER_FRAGMENT:
			if(mBrowserFragment != null && mBrowserFragment.isVisible()){
				value = true;
			}
			break;
		default:
			break;
		}
		return value;
	}
	/**************************************************************************************/
	/**
	 * 
	 * @param index
	 */
	public void switchFragment(int index) {
		// TODO Auto-generated method stub
		switch(index){
			case LOADING_FRAGMENT:
				replaceFragment(LOADING_FRAGMENT);
				break;
			
			case PLAYBACK_FRAGMENT:								
				replaceFragment(PLAYBACK_FRAGMENT);
				break;
			case BROWSER_FRAGMENT:
				replaceFragment(BROWSER_FRAGMENT);
				break;
			default:
				break;
		}
	}

	/**
	 * 
	 * @param index
	 */
	private void replaceFragment(int index){
		Log.v(TAG , "addFragment :"+index);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		switch(index){
		case LOADING_FRAGMENT:
			if(mLoadingFragment == null ){
				mLoadingFragment = new PhotoLoadingFragment();
				
			}
			ft.replace(R.id.content, mLoadingFragment);
			ft.commitAllowingStateLoss();
			break;
			
		case PLAYBACK_FRAGMENT:
			if(mPlaybackFragment == null ){
				mPlaybackFragment = new PhotoPlaybackFragment();							
			}
			if(getFragmentManager().getBackStackEntryCount() > 0){
				getFragmentManager().popBackStack();
			}
			ft.replace(R.id.content, mPlaybackFragment);
			ft.commitAllowingStateLoss();
			break;
		case BROWSER_FRAGMENT:
			if(mBrowserFragment == null){
				mBrowserFragment = new PhotoBrowserFragment();			
			}
			ft.replace(R.id.content, mBrowserFragment);
			ft.addToBackStack(null);
			ft.commitAllowingStateLoss();
			break;
		default:
			break;
		}
	}
}
