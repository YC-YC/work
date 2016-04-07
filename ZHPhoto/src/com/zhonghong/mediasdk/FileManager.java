package com.zhonghong.mediasdk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import com.zhonghong.mediasdk.music.MusicServiceHelper;
import com.zhonghong.mediasdk.photo.PhotoServiceHelper;
//import com.zhonghong.mediasdk.video.VideoServiceHelper;

import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

public class FileManager {
	private static final String TAG = "FileManager";
	private BaseData mSrcMultiSelectData = null;
	private FolderData mSrcFolderData = null;
	private List<FolderData> mSrcFolderDataList = null;
	private FileManagerItemInfo mFMItemInfo= null;
	private boolean bIsFileCopy = true;
	private int iSrcOpType = NONE_TYPE;
	private String targetPath = null;
	
	private static final int BUFFER = 2048;
	private static final int NONE_TYPE = 0x00;
	public static final int COPY_TYPE = Constants.FM_OP_COPY;
	public static final int DEL_TYPE = Constants.FM_OP_DEL;
	public static final int CUT_TYPE = Constants.FM_OP_CUT;
	private int mFileType = Constants.FILE_TYPE_FILE;
	private long iCopyingSumSize = 0;
	private long iCopyedCurrentSize = 0;
	
	/**
	 * 
	 * @author Administrator
	 *
	 */
	public interface fileManagerCallback{
			void OnStatus(String strStatus,int para);
			void OnProgress(String name,int icurrindex,int itotal,int percent);
			void UpdateBufferData(int iOPType);
	};
	
	/**
	 * 
	 * @param iFileType
	 */
	public FileManager(int iFileType){
		mFileType = iFileType;
		mFMItemInfo = new FileManagerItemInfo();
		Log.d(TAG,"FileManager "+" mFileType "+mFileType);
	}
	
	/**
	 * 
	 * @param icmd
	 * @param arg1
	 * @param arg2
	 * @param idelay
	 */
	private void sendMessageObjShutCut(int icmd,int arg1,int arg2,Object obj,int idelay){
		if(mFileType == Constants.FILE_TYPE_SONG){
//			MusicServiceHelper.sendMessageObjShutCut(icmd,arg1,arg2,obj,idelay);
		}else if(mFileType == Constants.FILE_TYPE_VIDEO){
//			VideoServiceHelper.sendMessageObjShutCut(icmd,arg1,arg2,obj,idelay);
		}else if(mFileType == Constants.FILE_TYPE_PICTURE){
			PhotoServiceHelper.sendMessageObjShutCut(icmd,arg1,arg2,obj,idelay);
		}else{
			Log.d(TAG,"sendMessageObjShutCut [FAIL].");
		}
	}
	
	/**
	 * 
	 * @param bd
	 */
	private void addNewBDToFocusFolderData(BaseData bd){
		if(mFileType == Constants.FILE_TYPE_SONG){
//			MusicServiceHelper.addNewBDToFocusFolderData(bd);
		}else if(mFileType == Constants.FILE_TYPE_VIDEO){
//			VideoServiceHelper.addNewBDToFocusFolderData(bd);
		}else if(mFileType == Constants.FILE_TYPE_PICTURE){
			PhotoServiceHelper.addNewBDToFocusFolderData(bd);
		}else{
			Log.d(TAG,"sendMessageObjShutCut [FAIL].");
		}
	}

	/**
	 * 
	 * @param bd
	 */
	private void addNewFDToFolderDataList(FolderData fd){
		if(mFileType == Constants.FILE_TYPE_SONG){
//			MusicServiceHelper.addNewFDToFolderDataList(fd);
		}else if(mFileType == Constants.FILE_TYPE_VIDEO){
//			VideoServiceHelper.addNewFDToFolderDataList(fd);
		}else if(mFileType == Constants.FILE_TYPE_PICTURE){
			PhotoServiceHelper.addNewFDToFolderDataList(fd);
		}else{
			Log.d(TAG,"addNewFDToFolderDataList [FAIL].");
		}
	}
	
	/**
	 * 
	 */
	private void delBDFromSrcFolderDataList(List<FolderData> fdlist,FolderData fd,BaseData bd){
		if(mFileType == Constants.FILE_TYPE_SONG){
//			MusicServiceHelper.delBDFromSrcFolderDataList(fdlist,fd,bd);
		}else if(mFileType == Constants.FILE_TYPE_VIDEO){
//			VideoServiceHelper.delBDFromSrcFolderDataList(fdlist,fd,bd);
		}else if(mFileType == Constants.FILE_TYPE_PICTURE){
			PhotoServiceHelper.delBDFromSrcFolderDataList(fdlist,fd,bd);
		}else{
			Log.d(TAG,"addNewFDToFolderDataList [FAIL].");
		}
	}
	/**
	 * 
	 */
	private void delFDFromSrcFolderDataList(List<FolderData> fdlist,FolderData fd){
		if(mFileType == Constants.FILE_TYPE_SONG){
//			MusicServiceHelper.delFDFromSrcFolderDataList(fdlist,fd);
		}else if(mFileType == Constants.FILE_TYPE_VIDEO){
//			VideoServiceHelper.delFDFromSrcFolderDataList(fdlist,fd);
		}else if(mFileType == Constants.FILE_TYPE_PICTURE){
			PhotoServiceHelper.delFDFromSrcFolderDataList(fdlist,fd);
		}else{
			Log.d(TAG,"addNewFDToFolderDataList [FAIL].");
		}
	}
	/**
	 * 
	 */
	private void updateTargetTotalList(){
		if(mFileType == Constants.FILE_TYPE_SONG){
//			MusicServiceHelper.update_TotalList();
		}else if(mFileType == Constants.FILE_TYPE_VIDEO){
//			VideoServiceHelper.update_TotalList();
		}else if(mFileType == Constants.FILE_TYPE_PICTURE){
			PhotoServiceHelper.update_TotalList();
		}else{
			Log.d(TAG,"addNewFDToFolderDataList [FAIL].");
		}
	}
	
	/**
	 * 
	 */
	public void resetFileManagerData(){
		mSrcMultiSelectData = null;
		mSrcFolderData = null;
		mSrcFolderDataList = null;
		targetPath = null;
		bIsFileCopy = true;
		iSrcOpType = NONE_TYPE;
	}
	/**
	 * 
	 * @param fdlist
	 * @param fd
	 * @param bd
	 */
	public void setFileManagerSourceData(List<FolderData> fdlist,FolderData fd,BaseData bd,int iOpType){
		mSrcMultiSelectData = bd;
		mSrcFolderData = fd;
		mSrcFolderDataList = fdlist;
		iSrcOpType = iOpType;
		if(bd == null){
			bIsFileCopy =false;
		}else{
			bIsFileCopy =true;
		}
	}
	
	/**
	 * 
	 * @param fdlist
	 * @param fd
	 * @param bd
	 */
	public void setFileManagerDestData(String path){
		targetPath = path;
	}
	
	/**
	 * 
	 */
	private void resetFileManagerItemInfo(){
		mFMItemInfo.iCurrIndex = -1;
		mFMItemInfo.iTotalCount = -1;
		mFMItemInfo.iSizePercent = 0;
		mFMItemInfo.strOPName = null;
		mFMItemInfo.iFMType = Constants.FM_OP_NONE;
		
	}
	/**
	 * 
	 * @param old
	 *            the file to be copied
	 * @param newDir
	 *            the directory to move the file to
	 * @return
	 */
	public int copyToDirectory(BaseData mBaseData ,String newDir) {		
		byte[] data = new byte[BUFFER];
		int read = 0;
		Log.d(TAG,"copyToDirectory newDir: "+newDir+" mBaseData "+mBaseData);
		
		if(newDir == null){
			Log.d(TAG,"copyToDirectory [FAIL],newDir: "+newDir+" mBaseData "+mBaseData);
			return -1;
		}
		
		File temp_dir = new File(newDir);
		if (mBaseData!=null && temp_dir.isDirectory() && temp_dir.canWrite()) {
//			String file_name = old
//					.substring(old.lastIndexOf("/"), old.length());
//			
			String file_name = mBaseData.getName();			
			File old_file = new File(mBaseData.getPath());		
			File cp_file = null;
			Log.d(TAG,"copyToDirectory file mode ");
			if(mBaseData.getParentPath().equals(newDir)){  //目录相同要更换名字
				for(int i =0;i<10;i++){
					cp_file = new File(newDir + "/" + file_name);
					
					if(cp_file.exists()){
						file_name = "copy_"+file_name;
						continue;
					}else{
						break;
					}
				}
			}else{
				cp_file = new File(newDir + "/" + file_name);
			}
					
			try {
				BufferedOutputStream o_stream = new BufferedOutputStream(
						new FileOutputStream(cp_file));
				BufferedInputStream i_stream = new BufferedInputStream(
						new FileInputStream(old_file));
				mFMItemInfo.strOPName = newDir + "/" + file_name;
				while ((read = i_stream.read(data, 0, BUFFER)) != -1){
					o_stream.write(data, 0, read);
					iCopyedCurrentSize+=read;
					mFMItemInfo.iSizePercent = (int)(100*iCopyedCurrentSize/iCopyingSumSize);
					sendMessageObjShutCut(Constants.CB_MSG_UPDATE_PROGRESS,(int)(100*iCopyedCurrentSize/iCopyingSumSize),0,mFMItemInfo,0);
				}
				if(bIsFileCopy){
					if(mSrcMultiSelectData != null){
						BaseData bb1 = (BaseData) mSrcMultiSelectData.clone(); 
						bb1.setPath(newDir+"/"+cp_file.getName());
						bb1.setName(cp_file.getName());
						bb1.setParentPath(newDir);
						addNewBDToFocusFolderData(bb1);
					}else{
						Log.d(TAG,"copyToDirectory [FAIL],mSrcMultiSelectData == NULL");
					}
				}
				
				o_stream.flush();
				i_stream.close();
				o_stream.close();

			} catch (FileNotFoundException e) {
				Log.e("FileNotFoundException", e.getMessage());
				return -1;
			} catch (IOException e) {
				Log.e("IOException", e.getMessage());
	            if("write failed: ENOSPC (No space left on device)".equals(e.getMessage())){
	            	sendMessageObjShutCut(Constants.CB_MSG_SHOW_DIALOG,Constants.FM_ERROR_NOSPACE, 0, null,0);
	            }
				return -1;
			} 
		} else if (mBaseData == null && mSrcFolderData !=null && temp_dir.isDirectory()
				&& temp_dir.canWrite()) {
			int iRetryTimes =0;
			ArrayList<BaseData> files = mSrcFolderData.getCurrentFolderFileList();
			String tmp = mSrcFolderData.getCurrentFolderPathStr();
			String foldername = tmp.substring(tmp.lastIndexOf("/")+1, tmp.length());
			String dir = newDir +"/"+ foldername;
			int iFileNum = files.size();
			Log.d(TAG,"copyToDirectory folder mode,folder size: "+ iFileNum+" dir "+dir);
/*
			while(true){
				File dirFile = new File(dir);
				if(dirFile.exists()&&dirFile.isDirectory()){					
					if(++iRetryTimes < 10){
						foldername="copy_"+foldername;
						dir = newDir+"/"+foldername;
						continue;
					}else{
						Log.d(TAG,"copyToDirectory [FAIL],new dir error:"+dir+",retry > 10");
						return -1;	
					}
				}
				
				if(!dirFile.mkdir()){
					Log.e(TAG,"copyToDirectory [FAIL],new dir unkown error!");
					sendMessageObjShutCut(Constants.CB_MSG_SHOW_DIALOG,Constants.FM_ERROR_UNKOWN, 0, null,0);
					return -2;
				}else{
					break;
				}
			}
*/
			File dirFile = new File(dir);
			if(!dirFile.exists()){
				if(!dirFile.mkdir()){
					Log.e(TAG,"copyToDirectory [FAIL],new dir unkown error!");
					sendMessageObjShutCut(Constants.CB_MSG_SHOW_DIALOG,Constants.FM_ERROR_UNKOWN, 0, null,0);
					return -2;
				}
			}
			
			FolderData ff1 = new FolderData();
			ff1.setCurrentFolderPath(dir);
			for (int i = 0; i < iFileNum; i++){	
					mFMItemInfo.iCurrIndex = i+1;
				if(copyToDirectory(files.get(i), dir) == 0){
					BaseData bb1 = null;
					bb1 = (BaseData) mSrcFolderData.getCurrentFolderFileList().get(i).clone();
					bb1.setPath(dir+"/"+bb1.getName());
					bb1.setParentPath(dir);
					ff1.insertFileToCurrentFolder(bb1);
				}else{
					Log.e(TAG,"Coping file fail.");
				}
			}
			
			addNewFDToFolderDataList(ff1);
		} else if (!temp_dir.canWrite())
			return -1;

		return 0;
	}

	/**
	 * 
	 * @param bd
	 * @param newDir
	 */
	private int cutFileOrDirectory(BaseData bd,String newDir){
		copyToDirectory(bd,newDir);
		delFileOrDirectory();
		return 0;
	}
	
	/**
	 * 
	 */
	private void delFileOrDirectory(){
		
		mFMItemInfo.iCurrIndex = 1;
		mFMItemInfo.iFMType = iSrcOpType;
		if(mSrcMultiSelectData != null){
				File f =new File(mSrcMultiSelectData.getPath());
				if(f.exists()){
					f.delete();
					//handler ui ^
					Log.d(TAG, "######111");		
				}else{
					Log.d(TAG, "######222");				
				}
				delBDFromSrcFolderDataList(mSrcFolderDataList,mSrcFolderData,mSrcMultiSelectData);
				mFMItemInfo.iTotalCount = 1;
		}else if(mSrcFolderData != null && (mSrcMultiSelectData == null)){
			mFMItemInfo.iTotalCount = mSrcFolderData.getCurrentFolderFileList().size();
			for(int i = 0;i< mSrcFolderData.getCurrentFolderFileList().size();i++){
				BaseData bd = mSrcFolderData.getCurrentFolderFileList().get(i);
				File f = new File(bd.getPath());
				if(f.exists()){
					f.delete();
				}
			}
			//delete folder
//			File f = new File(mSrcFolderData.getCurrentFolderPathStr());
//			if(f.exists()){
//				f.delete();
//			}
			
			delFDFromSrcFolderDataList(mSrcFolderDataList,mSrcFolderData);
		}
	}
	
	/**
	 * Will copy a file or folder to another location.
	 * 
	 * @param oldLocation
	 *            from location
	 * @param newLocation
	 *            to location
	 */
	private long getfilelength(String path){
		File f = new File(path);
		long index = 0;
		if(f.exists()){
			index = f.length();
		}else
		{
			index = -1;
		}
		return index;
	}
	
	/**
	 * 
	 * @param newLocation
	 */
	public void pasteFile(boolean bIsNoCheck) {
		String[] data = {targetPath};
		iCopyedCurrentSize = 0;
		iCopyingSumSize = 0;
		
		if((targetPath == null)||((mSrcMultiSelectData == null)&&(mSrcFolderData == null))){
			Log.d(TAG,"pasteFile [FAIL],targetpath:"+targetPath+" mSrcMultiSelectData "+mSrcMultiSelectData+" mSrcFolderData "+mSrcFolderData);
			sendMessageObjShutCut(Constants.CB_MSG_SHOW_DIALOG,Constants.FM_ERROR_INVALIDPASTE, 0, null,0);
			return;
		}
		
		mFMItemInfo.iCurrIndex = 1;
		mFMItemInfo.iFMType = iSrcOpType;
		
		if(bIsNoCheck == false){
			if(mSrcMultiSelectData != null){
				File f = new File(targetPath+"/"+mSrcMultiSelectData.getName());
				if(!mSrcMultiSelectData.getParentPath().equals(targetPath)&&f.exists()){
					mFMItemInfo.strOPName = mSrcMultiSelectData.getPath();
					sendMessageObjShutCut(Constants.CB_MSG_SHOW_DIALOG,Constants.FM_WARN_SAMEFILE, 0,null, 0);
					Log.d(TAG,"pasteFile [FAIL],hava same file,wait user resp");
					return;
				}
			}
		}

		if(mSrcMultiSelectData == null){
			mFMItemInfo.iTotalCount = mSrcFolderData.getCurrentFolderFileList().size();
			for(int i = 0;i< mSrcFolderData.getCurrentFolderFileList().size();i++){
				iCopyingSumSize+=getfilelength(mSrcFolderData.getCurrentFolderFileList().get(i).getPath());
			}
			mFMItemInfo.strOPName = mSrcFolderData.getCurrentFolderPathStr();
		}else{
			iCopyingSumSize += getfilelength(mSrcMultiSelectData.getPath());
			mFMItemInfo.iTotalCount = 1;
			mFMItemInfo.strOPName = mSrcMultiSelectData.getPath();
		}
		if(iSrcOpType == COPY_TYPE){
			new BackgroundWork(COPY_TYPE).execute(data);
		}else if(iSrcOpType == CUT_TYPE){
			new BackgroundWork(CUT_TYPE).execute(data);
		}else{
			Log.d(TAG,"pasteFile [FAIL],unknown iSrcOpType "+iSrcOpType);
		}
	}

	/**
	 * 
	 * @param newLocation
	 */
	public void delFile() {
		iCopyedCurrentSize = 0;
		iCopyingSumSize = 0;
		if(mSrcMultiSelectData == null){
			for(int i = 0;i< mSrcFolderData.getCurrentFolderFileList().size();i++){
				iCopyingSumSize+=getfilelength(mSrcFolderData.getCurrentFolderFileList().get(i).getPath());
			}
			mFMItemInfo.iTotalCount = mSrcFolderData.getCurrentFolderFileList().size();
			mFMItemInfo.strOPName = mSrcFolderData.getCurrentFolderPathStr();
		}else{
			iCopyingSumSize += getfilelength(mSrcMultiSelectData.getPath());
			mFMItemInfo.strOPName = mSrcMultiSelectData.getPath();
			mFMItemInfo.iTotalCount = 1;
		}

		new BackgroundWork(DEL_TYPE).execute();
	}

	/**
	 * 
	 * @author victorchen
	 *
	 */
	private class BackgroundWork extends
			AsyncTask<String, Void, ArrayList<String>> {

		private int type;
		private int copy_rtn;

		private BackgroundWork(int type) {
			this.type = type;
		}

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			// TODO Auto-generated method stub
			switch (type) {
			case COPY_TYPE:
				Log.d(TAG,"iSrcOpType COPY_TYPE,params[0]:"+params[0]);
				copyToDirectory(mSrcMultiSelectData, params[0]);				
				break;
			case DEL_TYPE:
				delFileOrDirectory();
				break;
			case CUT_TYPE:
				cutFileOrDirectory(mSrcMultiSelectData, params[0]);
				break;
			default:
				break;
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			// TODO Auto-generated method stub
			boolean bValidOp = false;
			switch (type) {
			case COPY_TYPE:
				//copy end
				bValidOp = true;
				Log.d("################", "copy end #############");
				updateTargetTotalList();
				resetFileManagerItemInfo();
				break;
			case DEL_TYPE:
				bValidOp = true;
				Log.d("################", "del end #############");
				resetFileManagerData();
				resetFileManagerItemInfo();
				updateTargetTotalList();
				break;
			case CUT_TYPE:
				bValidOp = true;
				Log.d("################", "cut end #############");
				resetFileManagerData();
				resetFileManagerItemInfo();
				updateTargetTotalList();
				break;
			}
			
			if(bValidOp){
				sendMessageObjShutCut(Constants.CB_MSG_FC_PROGRESS,0,0,null,0);
			}
			
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			boolean bValidOp = false;
			switch (type) {
			case COPY_TYPE:
				bValidOp = true;
				mFMItemInfo.iFMType = COPY_TYPE;
				break;
				
			case DEL_TYPE:
				bValidOp = true;
				mFMItemInfo.iFMType = DEL_TYPE;
				break;
				
			case CUT_TYPE:
				bValidOp = true;
				mFMItemInfo.iFMType = CUT_TYPE;		
				break;
			default:
				break;
			}
			/**
			 * 
			 */
			if(bValidOp){
				sendMessageObjShutCut(Constants.CB_MSG_START_PROGRESS,0,0,mFMItemInfo,0);
			}
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

	}

}
