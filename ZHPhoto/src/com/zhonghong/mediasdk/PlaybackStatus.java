package com.zhonghong.mediasdk;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class PlaybackStatus implements Parcelable,Cloneable{
		//Status info
		public int iPlayStatus = 0;
		public int iPlayMode = 0;
		public int iMute = 0;
		public int iEQMode = 0;
		
		//Time info
		public long iCurrentTime =0;
		public long iStartTime = 0;
		public long iTotalTime = 0;
		
		//Playing info	
		public int iCurrFBDev = -1;
		public int iTotalNum = -1;		//for total music file number
		public int iCurrPBIndexOnTotal = -1;   //for total file index
		
		public Object Clone(){
			PlaybackStatus pbobj = null;
			try{			
				pbobj = (PlaybackStatus)super.clone();
			}catch(CloneNotSupportedException e){
				e.printStackTrace();
			}
			return pbobj;
		}
		
		/**
		 * 
		 * @return
		 */
		static public boolean CopyValue(PlaybackStatus srcobj1,PlaybackStatus desobj2){
			if(srcobj1== null || desobj2 == null){
				return false;
			}
			
			desobj2.iPlayStatus = srcobj1.iPlayStatus;
			desobj2.iCurrFBDev = srcobj1.iCurrFBDev;
			desobj2.iCurrPBIndexOnTotal = srcobj1.iCurrPBIndexOnTotal;
			desobj2.iMute = srcobj1.iMute;
			desobj2.iEQMode = srcobj1.iEQMode;
			desobj2.iPlayMode = srcobj1.iPlayMode;
			desobj2.iStartTime = srcobj1.iStartTime;
			desobj2.iCurrentTime = srcobj1.iCurrentTime;
			desobj2.iTotalTime = srcobj1.iTotalTime;
			desobj2.iTotalNum = srcobj1.iTotalNum;
			
			return true;
		}
		
	    @Override
	    public void writeToParcel(Parcel dest, int flags) {
	        dest.writeInt(iPlayStatus);
	        dest.writeInt(iPlayMode);
	        dest.writeInt(iMute);
	        dest.writeLong(iCurrentTime);
	        dest.writeLong(iStartTime);
	        dest.writeLong(iTotalTime);
	        dest.writeInt(iCurrFBDev);
	        dest.writeInt(iTotalNum);
	        dest.writeInt(iCurrPBIndexOnTotal);
	    }

	    public static final Parcelable.Creator<PlaybackStatus> CREATOR = new Parcelable.Creator<PlaybackStatus>() {
	        @Override
	        public PlaybackStatus createFromParcel(Parcel source) {
	        	PlaybackStatus pbstatus = new PlaybackStatus();
	        	pbstatus.iPlayStatus = source.readInt();
	        	pbstatus.iPlayMode = source.readInt();
	        	pbstatus.iMute = source.readInt();
	        	pbstatus.iCurrentTime = source.readLong();
	        	pbstatus.iStartTime = source.readLong();
	        	pbstatus.iTotalTime = source.readLong();
	        	pbstatus.iCurrFBDev = source.readInt();
	        	pbstatus.iTotalNum = source.readInt();
	        	pbstatus.iCurrPBIndexOnTotal = source.readInt();

	            return pbstatus;
	        }

	        @Override
	        public PlaybackStatus[] newArray(int size) {
	            return new PlaybackStatus[size];
	        }
	    };

		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

}
