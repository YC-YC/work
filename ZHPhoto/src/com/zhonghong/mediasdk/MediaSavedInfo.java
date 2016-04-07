package com.zhonghong.mediasdk;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class MediaSavedInfo {
	private static final String TAG = "MediaSavedInfo";
	private int iMediaType = -1;
	private int iCurrPlaybackDeviceID = Constants.INVALID_DEVICE; //save current player device id
	private int iCurrPlayMode = Constants.PM_REPALL;
	private int iCurrPlayIndex = 0;
	private int iCurrPlayPosition = 0;
	private int iCurrPlayEQ = Constants.EQ_POP;
	private int iCurrBgIndex = 0;
	private String strPrefName = "";
	private String strCurrPlayFilePath = "";
	private Context mContext = null;
	
	/**
	 * 
	 * @param imediatype
	 */
	public MediaSavedInfo(Context ctx,int imediatype){
		mContext = ctx;
		iMediaType = imediatype;
		switch(iMediaType){
		case Constants.MEDIA_TYPE_MUSIC:
			strPrefName = Constants.ZHMUSIC_PREF;
			break;
		case Constants.MEDIA_TYPE_VIDEO:
			strPrefName = Constants.ZHVIDEO_PREF;
			break;
		case Constants.MEDIA_TYPE_PHOTO:
			strPrefName = Constants.ZHPHOTO_PREF;
			break;
		default:
			break;
		}
		
		//initial media saved information
		initMediaSavedInfo();
	}
	
    /**
     * @author chendz
     */
    private void initMediaSavedInfo(){  	
		SharedPreferences preference = mContext.getSharedPreferences(strPrefName,mContext.MODE_PRIVATE);		
		iCurrPlayEQ = preference.getInt(Constants.EQMODE,Constants.EQ_POP);
		iCurrPlayMode = preference.getInt(Constants.PLAYMODE,Constants.PM_REPALL);
		iCurrPlayIndex = preference.getInt(Constants.PLAYINDEX,0);
		iCurrPlaybackDeviceID = preference.getInt(Constants.PLAYBACK_DEV_ID,Constants.INVALID_DEVICE);
		iCurrPlayPosition = preference.getInt(Constants.PLAYPOS,0);
		strCurrPlayFilePath = preference.getString(Constants.PLAYMUSICFILEPATH,"");
		iCurrBgIndex = preference.getInt(Constants.BGPICINDEX,0);
		Log.d(TAG,"initMediaSavedInfo "+strPrefName+" "+iCurrPlaybackDeviceID+" "+iCurrPlayEQ+" "+iCurrPlayMode+" "+iCurrPlayIndex+" "+iCurrPlayPosition+" "+iCurrBgIndex+" "+strCurrPlayFilePath);
    }
    
	/**
	 * 
	 */
	public void resetMediaSavedInfo(){
		setCurrentPlayPosition(0);
		setCurrentPlayIndex(0);
		setCurrentPlaybackFilePath("");
		
		//save the reset info to file
		saveMediaSavedInfo();
	}
    /**
     * 
     * 
     */
	public boolean checkValidPlaybackAddress(List<BaseData> _MediaList)
	{
		int _iPlayIndex = -1;
		int _iPlayIndexTotalTime = -1;
		int _iPlaybackPos = -1;
		String _strLastPlaybackPath = null;
		String _strPlaybackPath = null;
		
		Log.i(TAG,"checkValidPlaybackAddress _MediaList:"+_MediaList.size());
		if(_MediaList != null && _MediaList.size() > 0){
			_iPlayIndex = getCurrentPlayIndex();
			if(_iPlayIndex < _MediaList.size()){
				_iPlaybackPos = getCurrentPlayPosition();
				//_iPlayIndexTotalTime = _MusicList.get(_iPlayIndex).getDuration();
				if(/*_iPlaybackPos <= _iPlayIndexTotalTime &&*/ _iPlaybackPos >= 0){
					_strLastPlaybackPath = getCurrentPlaybackFilePath();
					_strPlaybackPath = _MediaList.get(_iPlayIndex).getPath();
					if((_strPlaybackPath != null) && (_strPlaybackPath.equals(_strLastPlaybackPath))){
						Log.d(TAG,"checkValidPlaybackAddress valid last playback info.");
						return true;
					}else{
						Log.i(TAG,"checkValidPlaybackAddress [FAIL], _strPlaybackPath:"+_strPlaybackPath+" _strLastPlaybackPath: "+_strLastPlaybackPath);
					}
				}else{
					Log.i(TAG,"checkValidPlaybackAddress [FAIL], _iPlaybackPos:"+_iPlaybackPos);
				}
			}else{
				Log.i(TAG,"checkValidPlaybackAddress [FAIL], _iPlayIndex:"+_iPlayIndex+" _MediaList.size(): "+_MediaList.size());
			}
		}else{
			Log.i(TAG,"checkValidPlaybackAddress [FAIL], _MediaList:"+_MediaList);
		}
		return false;
	}
    /**
     * @author chendz
     */
    public void saveMediaSavedInfo(){
		SharedPreferences preference = mContext.getSharedPreferences(strPrefName,
				mContext.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor.putInt(Constants.PLAYBACK_DEV_ID, iCurrPlaybackDeviceID);
		editor.putInt(Constants.EQMODE, iCurrPlayEQ);
		editor.putInt(Constants.PLAYMODE, iCurrPlayMode);
		editor.putInt(Constants.PLAYINDEX, iCurrPlayIndex);
		editor.putInt(Constants.PLAYPOS, iCurrPlayPosition);
		editor.putInt(Constants.BGPICINDEX, iCurrBgIndex);
		editor.putString(Constants.PLAYMUSICFILEPATH, strCurrPlayFilePath);
		editor.commit();
		Log.d(TAG,"saveMediaSavedInfo "+strPrefName+" "+iCurrPlaybackDeviceID+" "+iCurrPlayEQ+" "+iCurrPlayMode+" "+iCurrPlayIndex+" "+iCurrPlayPosition+" "+iCurrBgIndex+" "+strCurrPlayFilePath);
    }
    
    /**
     * get playback paramter method
     */
    public void setCurrentPlayMode(int _iCurrPlayMode){
    	iCurrPlayMode = _iCurrPlayMode;
    }
    
    /**
     * 
     * @param _iCurrPlayEQ
     */
    public void setCurrentEQMode(int _iCurrPlayEQ){
    	iCurrPlayEQ = _iCurrPlayEQ;
    }
    
    /**
     * 
     * @param _iCurrPlayIndex
     */
    public void setCurrentPlayIndex(int _iCurrPlayIndex){
    	iCurrPlayIndex = _iCurrPlayIndex;
    }
  
    /**
     * 
     * @param _iCurrPlaybackDeviceID
     */
    public void setCurrentPlaybackDeviceID(int _iCurrPlaybackDeviceID){
    	iCurrPlaybackDeviceID = _iCurrPlaybackDeviceID;
    }
    
    /**
     * 
     * @param iPos
     */
    public void setCurrentPlayPosition(int iPos){
    	iCurrPlayPosition = iPos;
    }
    
    /**
     * 
     * @param _strCurrPlayFilePath
     */
    public void setCurrentPlaybackFilePath(String _strCurrPlayFilePath){
    	strCurrPlayFilePath = _strCurrPlayFilePath;
    }
    
    /**
     * 
     * @param _iCurrBgIndex
     */
    public void setCurrentBgIndex(int _iCurrBgIndex){
    	iCurrBgIndex = _iCurrBgIndex;
    }
    
    
    /**
     * 
     * get playback paramter method
     */
    public int getCurrentPlayMode(){
    	return iCurrPlayMode;
    }
    /**
     * 
     * @return
     */
    public int getCurrentEQMode(){
    	return iCurrPlayEQ;
    }
    
    /**
     * 
     * @return
     */
    public int getCurrentPlayIndex(){
    	return iCurrPlayIndex;
    }
    
    /**
     * 
     * @return
     */
    public int getCurrentPlayPosition(){
    	return iCurrPlayPosition;
    }
      
    /**
     * 
     * @param 
     */
    public int getCurrentPlaybackDeviceID(){
    	return iCurrPlaybackDeviceID;
    }
    
    
    /**
     * 
     * @return
     */
    public String getCurrentPlaybackFilePath(){
    	return strCurrPlayFilePath;
    }
    
    /**
     * 
     * @return
     */
    public int getCurrentBgIndex(){
    	return iCurrBgIndex;
    }
}
