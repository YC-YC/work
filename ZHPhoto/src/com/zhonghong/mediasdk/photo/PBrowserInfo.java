package com.zhonghong.mediasdk.photo;

import java.util.List;
import com.zhonghong.mediasdk.BaseData;
import com.zhonghong.mediasdk.Constants;
import com.zhonghong.mediasdk.FolderData;

public class PBrowserInfo {
		public int iDevId = -1;
		public int iDevDBStatus = Constants.DB_NOINITITAL;
		public int iFocusFolderFileNum = 0;
		public int iFolderTotal = 0;
		public int iFocusFolderIndex = 0;
		public int iFocusFileIndex = 0;
		public List<FolderData> mPhotoFolderList = null;
		public List<BaseData> mPhotoTotalList = null;
}
