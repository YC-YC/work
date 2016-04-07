package com.zhonghong.mediasdk;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class DeviceInfo implements Parcelable{
	private static final String TAG = "DeviceInfo";
	public int iDevID = Constants.INVALID_DEVICE;
	public String sName = null;
	public String sMountPoint = null;
	public long iDevCapacity = 0;
	public long iDevAvailableSpace = 0;
	public int iDevStatus = Constants.DB_NOINITITAL;
	public List<FolderData> mMusicFolderList = null;
	public List<BaseData> mMusicTotalList = null;
	public List<FolderData> mVideoFolderList = null;
	public List<BaseData> mVideoTotalList = null;
	public List<FolderData> mPhotoFolderList = null;
	public List<BaseData> mPhotoTotalList = null;
	
	public DeviceInfo(){
		Log.d(TAG,"New DeviceInfo.");
		mMusicFolderList = new ArrayList<FolderData>();
		mMusicTotalList = new ArrayList<BaseData>();
		mVideoFolderList = new ArrayList<FolderData>();
		mVideoTotalList = new ArrayList<BaseData>();
		mPhotoFolderList = new ArrayList<FolderData>();
		mPhotoTotalList = new ArrayList<BaseData>();
		mMusicFolderList.clear();
		mMusicTotalList.clear();
		mVideoFolderList.clear();
		mVideoTotalList.clear();
		mPhotoFolderList.clear();
		mPhotoTotalList.clear();
	}
	/**
	 * 
	 * @return
	 */
	public String getPath(){
		return sMountPoint;
	}
	
	/**
	 * 
	 */
	public String getName(){
		return sName;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(iDevID);
		dest.writeString(sName);
		dest.writeString(sMountPoint);
		dest.writeLong(iDevCapacity);
		dest.writeLong(iDevAvailableSpace);
		dest.writeInt(iDevStatus);
		dest.writeList(mMusicFolderList);
		dest.writeList(mMusicTotalList);
		dest.writeList(mVideoFolderList);
		dest.writeList(mVideoTotalList);
		dest.writeList(mPhotoFolderList);
		dest.writeList(mPhotoTotalList);
	}
	
    public static final Parcelable.Creator<DeviceInfo> CREATOR = new Parcelable.Creator<DeviceInfo>() {
        @Override
        public DeviceInfo createFromParcel(Parcel source) {
        	DeviceInfo devinfo = new DeviceInfo();
        	devinfo.iDevID = source.readInt();
        	devinfo.sName = source.readString();
        	devinfo.sMountPoint = source.readString();
        	devinfo.iDevCapacity = source.readLong();
        	devinfo.iDevAvailableSpace = source.readLong();
        	devinfo.iDevStatus = source.readInt();
        	devinfo.mMusicFolderList = new ArrayList<FolderData>();
        	source.readList(devinfo.mMusicFolderList,getClass().getClassLoader());
        	devinfo.mMusicTotalList = new ArrayList<BaseData>();
        	source.readList(devinfo.mMusicTotalList,getClass().getClassLoader());
        	devinfo.mVideoFolderList = new ArrayList<FolderData>();
        	source.readList(devinfo.mVideoFolderList,getClass().getClassLoader());
        	devinfo.mVideoTotalList = new ArrayList<BaseData>();
        	source.readList(devinfo.mVideoTotalList,getClass().getClassLoader());
        	devinfo.mPhotoFolderList = new ArrayList<FolderData>();
        	source.readList(devinfo.mPhotoFolderList,getClass().getClassLoader());
        	devinfo.mPhotoTotalList = new ArrayList<BaseData>();
        	source.readList(devinfo.mPhotoTotalList,getClass().getClassLoader());
            return devinfo;
        }

        @Override
        public DeviceInfo[] newArray(int size) {
            return new DeviceInfo[size];
        }
    };
}
