package com.zhonghong.mediasdk.photo;
import java.util.ArrayList;
import java.util.List;

import com.zhonghong.mediasdk.BaseData;
import com.zhonghong.mediasdk.Constants;
import com.zhonghong.mediasdk.FolderData;
import com.zhonghong.mediasdk.PlaybackStatus;

public class PPlaybackInfo{
		public PlaybackStatus mPBStatus;
		public PlaybackStatus mPBStatusOld = new PlaybackStatus();
		//Playing info
		public PhotoItemInfo sItemInfo = new PhotoItemInfo();
		public boolean bShowDrivingWarning = false;
		public int iCurrPBFolderIndex = 0; //playback folder index
		public int iCurrPBFileIndex = 0;   //file index for playback folder
		public List<FolderData> mPhotoFolderList = null;
		public List<BaseData> mPhotoTotalList = null;
}
