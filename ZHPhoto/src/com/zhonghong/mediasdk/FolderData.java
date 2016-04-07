package com.zhonghong.mediasdk;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class FolderData implements Parcelable{
	private ArrayList<BaseData> mCurrFolderValidFileList = null;
	private String mCurrAbsolutePath = null;
	
	/**
	 * 
	 */
	public FolderData(){
		mCurrFolderValidFileList = new ArrayList<BaseData>();
		mCurrFolderValidFileList.clear();
	}
	
	/*
	 * 
	 */
	public boolean setCurrentFolderPath(String path){
		mCurrAbsolutePath = path;
		return true;
	}
	
	/*
	 * 
	 */
	public boolean insertFileToCurrentFolder(BaseData file){
		mCurrFolderValidFileList.add(file);
		return true;
	}
	
	/*
	 * 
	 */
	public String getCurrentFolderPathStr(){
		return mCurrAbsolutePath;
	}
	
	/*
	 * 
	 */
	public ArrayList<BaseData> getCurrentFolderFileList(){
		return mCurrFolderValidFileList;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeList(mCurrFolderValidFileList);
		dest.writeString(mCurrAbsolutePath);
	}
	
    public static final Parcelable.Creator<FolderData> CREATOR = new Parcelable.Creator<FolderData>() {
        @Override
        public FolderData createFromParcel(Parcel source) {
        	FolderData fd = new FolderData();
        	fd.mCurrFolderValidFileList = new ArrayList<BaseData>();
        	source.readList(fd.mCurrFolderValidFileList,getClass().getClassLoader());
        	fd.mCurrAbsolutePath = source.readString();

            return fd;
        }

        @Override
        public FolderData[] newArray(int size) {
            return new FolderData[size];
        }
    };
}
