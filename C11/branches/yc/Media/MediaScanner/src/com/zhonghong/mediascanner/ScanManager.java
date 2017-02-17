/**
 * 
 */
package com.zhonghong.mediascanner;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.RemoteException;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.zhonghong.mediascanerlib.ScannerConst;
import com.zhonghong.mediascanerlib.aidl.ScannerCallback;
import com.zhonghong.mediascanerlib.config.Config;
import com.zhonghong.mediascanerlib.filebean.AudioInfo;
import com.zhonghong.mediascanerlib.filebean.ImageInfo;
import com.zhonghong.mediascanerlib.filebean.OtherInfo;
import com.zhonghong.mediascanerlib.filebean.VideoInfo;
import com.zhonghong.mediascanner.bean.DeviceInfo;
import com.zhonghong.mediascanner.saver.DevicePool;
import com.zhonghong.mediascanner.saver.FileRecorder;
import com.zhonghong.mediascanner.scan.ScanPool;
import com.zhonghong.mediascanner.utils.CountManager;
import com.zhonghong.mediascanner.utils.FileUtil;
import com.zhonghong.mediascanner.utils.ILifeCycle;
import com.zhonghong.mediascanner.utils.L;

/**
 * @author YC
 * @time 2016-12-9 下午12:04:47
 * TODO:
 */
public class ScanManager implements ILifeCycle, IDataMode{
	
	private static final String TAG = "ScanManager";
	
	private DevicePool mDevicePool = null;
	private ScanPool mScanPool = null;
	/**还在请求的设备信息*/
	private CountManager mReqDevices = null;
	
	public ScanManager(){
		checkDBUpdate();
		mDevicePool = new DevicePool();
		mScanPool = new ScanPool(this);
		mReqDevices = new CountManager();
	}
	
	/**
	 * 检测数据版本更新（通过修改Config.DB_VERSION）
	 */
	private void checkDBUpdate(){
		String path = Config.DIR_PATH+"record_info.cfg";
		FileRecorder recorder = new FileRecorder(path);
		String oldVersionStr = recorder.getValue("db_version", "xxxx");
		int oldVersion = 0;
		try {
			oldVersion = Integer.parseInt(oldVersionStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (Config.DB_VERSION != oldVersion){
			L.i(TAG, "DB update");
			File dir = new File(Config.DIR_PATH);
			if (dir != null && dir.exists() && dir.isDirectory()){
				File[] listFiles = dir.listFiles();
				if (listFiles != null){
					for (File file: listFiles){
						if (file.isFile() && file.getName().startsWith("device")){
							FileUtil.deleteFile(file);
						}
					}
				}
			}
			recorder.putValue("db_version", ""+ Config.DB_VERSION);
		}
		else{
			L.i(TAG, "DB same");
		}
	}
	
	public void startScanDevice(String device){
		mScanPool.startScanDevice(device);
	}
	
	public void unMountDevice(String device){
		mDevicePool.unMountDevice(device);
	}
	
	
	public void getFileInfo(String device, int type, ScannerCallback callback){
		mReqDevices.push(device);
		new GetFileAsyncTask(device, type, callback).execute();
	}
	
	@Override
	public void onObjectCreate() {
		mScanPool.onObjectCreate();
	}

	@Override
	public void onObjectDestory() {
		mScanPool.onObjectDestory();
	}
	
	@Override
	public DeviceInfo getLoadedDeviceInfo(String uuid) {
		return mDevicePool.getLoadedDeviceInfo(uuid);
	}

	@Override
	public void setLoadedDeviceInfo(DeviceInfo info) {
		mDevicePool.setLoadedDeviceInfo(info);
	}
	
	@Override
	public boolean isReqDeviceInfo(String device) {
		return mReqDevices.isValid(device);
	}
	

	private class GetFileAsyncTask extends AsyncTask<String, Integer, Integer>{

		private String mDevice;
		private int mFileType;
		private ScannerCallback callback;
		
		public GetFileAsyncTask(String device, int type, ScannerCallback callback){
			this.mDevice = device;
			this.mFileType = type;
			this.callback = callback;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			callOnStart();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			return onGetFile();
		}
		
		@Override
		protected void onPostExecute(Integer result) {

			callOnEnd(result);

			mReqDevices.pop(mDevice);
			
			super.onPostExecute(result);
		}
		
		private void callOnStart(){
			L.i(TAG, "callOnStart");
			if (callback != null){
				try {
					callback.onStart(mDevice, mFileType);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void callOnEnd(int code){
			L.i(TAG, "callOnEnd");
			if (callback != null){
				try {
					callback.onEnd(mDevice, mFileType, code);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void callOnGetFileInfo(String fileInfo){
//			L.i(TAG, "callOnGetFileInfo");
			if (callback != null){
				try {
					L.i(TAG, "GetFileInfo = " + fileInfo);
					callback.onGetFileInfo(mDevice, mFileType, fileInfo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		private int onGetFile() {
			if (mScanPool.isDeviceScanning(mDevice)) {
				L.i(TAG, "onGetScanning file");
				return getScannindFile();
			}
			else{
				L.i(TAG, "onGetLoaded file");
				return getLoadedFile();
			}
		}
		
		/**
		 * 获取扫描中的数据
		 * @return
		 */
		private int getScannindFile(){

			DeviceInfo deviceInfo = mScanPool.getScanDeviceInfo(mDevice);
			//由于扫描是异步的，此时可能已经扫描完了
			if (deviceInfo == null){
				return getLoadedFile();
			}
			switch (mFileType) {
			case ScannerConst.FileType.FILE_TYPE_AUDIO:{
				ArrayList<AudioInfo> infos = deviceInfo.getAudioInfos();
				int index = 0;
				boolean hasNext = false;
				while((hasNext = (infos.size() > index)) || mScanPool.isDeviceScanning(mDevice)){
					if (!FileUtil.isDeviceMounted(mDevice)){
						return ScannerConst.OnEndCode.CODE_UNMOUNT;
					}
					if (hasNext){
						callOnGetFileInfo(new Gson().toJson(infos.get(index)));
						index++;
					}
				}
				return ScannerConst.OnEndCode.CODE_FINISH;
			}
			case ScannerConst.FileType.FILE_TYPE_VIDEO:{
				ArrayList<VideoInfo> infos = deviceInfo.getVideoInfos();
				int index = 0;
				boolean hasNext = false;
				while((hasNext = (infos.size() > index)) || mScanPool.isDeviceScanning(mDevice)){
					if (!FileUtil.isDeviceMounted(mDevice)){
						return ScannerConst.OnEndCode.CODE_UNMOUNT;
					}
					if (hasNext){
						callOnGetFileInfo(new Gson().toJson(infos.get(index)));
						index++;
					}
				}
				return ScannerConst.OnEndCode.CODE_FINISH;
			}
			case ScannerConst.FileType.FILE_TYPE_IMAGE:{
				ArrayList<ImageInfo> infos = deviceInfo.getImageInfos();
				int index = 0;
				boolean hasNext = false;
				while((hasNext = (infos.size() > index)) || mScanPool.isDeviceScanning(mDevice)){
					if (!FileUtil.isDeviceMounted(mDevice)){
						return ScannerConst.OnEndCode.CODE_UNMOUNT;
					}
					if (hasNext){
						callOnGetFileInfo(new Gson().toJson(infos.get(index)));
						index++;
					}
				}
				return ScannerConst.OnEndCode.CODE_FINISH;
			}
			case ScannerConst.FileType.FILE_TYPE_OTHER:{
				ArrayList<OtherInfo> infos = deviceInfo.getOtherInfos();
				int index = 0;
				boolean hasNext = false;
				while((hasNext = (infos.size() > index)) || mScanPool.isDeviceScanning(mDevice)){
					if (!FileUtil.isDeviceMounted(mDevice)){
						return ScannerConst.OnEndCode.CODE_UNMOUNT;
					}
					if (hasNext){
						callOnGetFileInfo(new Gson().toJson(infos.get(index)));
						index++;
					}
				}
				return ScannerConst.OnEndCode.CODE_FINISH;
			}
			default:
				return ScannerConst.OnEndCode.CODE_UNKNOWN;
			}
		
		}
		
		
		/**
		 * 获取加载到的数据
		 * @return
		 */
		private int getLoadedFile(){
			if (!FileUtil.isDeviceMounted(mDevice)){
				L.i(TAG, "device had unmounted");
				return ScannerConst.OnEndCode.CODE_UNMOUNT;
			}
			String uuid = FileUtil.getDeviceUuid(BaseApplication.getApplication(), mDevice);
			if (TextUtils.isEmpty(uuid)){
				L.i(TAG, "had no uuid");
				return ScannerConst.OnEndCode.CODE_NO_DATA;
			}
			DeviceInfo deviceInfo = mDevicePool.getLoadedDeviceInfo(uuid);
			if (deviceInfo == null){
				L.i(TAG, "had no data");
				return ScannerConst.OnEndCode.CODE_NO_DATA;
			}
			
			switch (mFileType) {
			case ScannerConst.FileType.FILE_TYPE_AUDIO:{
				ArrayList<AudioInfo> infos = deviceInfo.getAudioInfos();
				int index = 0;
				while(infos.size() > index){
					if (!FileUtil.isDeviceMounted(mDevice)){
						L.i(TAG, "device had unmounted");
						return ScannerConst.OnEndCode.CODE_UNMOUNT;
					}
					
					callOnGetFileInfo(new Gson().toJson(infos.get(index)));
					index++;
				}
				return ScannerConst.OnEndCode.CODE_FINISH;
			}
			case ScannerConst.FileType.FILE_TYPE_VIDEO:{
				ArrayList<VideoInfo> infos = deviceInfo.getVideoInfos();
				int index = 0;
				while(infos.size() > index){
					if (!FileUtil.isDeviceMounted(mDevice)){
						L.i(TAG, "device had unmounted");
						return ScannerConst.OnEndCode.CODE_UNMOUNT;
					}
					
					callOnGetFileInfo(new Gson().toJson(infos.get(index)));
					index++;
				}
				return ScannerConst.OnEndCode.CODE_FINISH;
			}
			case ScannerConst.FileType.FILE_TYPE_IMAGE:{
				ArrayList<ImageInfo> infos = deviceInfo.getImageInfos();
				int index = 0;
				while(infos.size() > index){
					if (!FileUtil.isDeviceMounted(mDevice)){
						L.i(TAG, "device had unmounted");
						return ScannerConst.OnEndCode.CODE_UNMOUNT;
					}
					
					callOnGetFileInfo(new Gson().toJson(infos.get(index)));
					index++;
				}
				return ScannerConst.OnEndCode.CODE_FINISH;
			}
			case ScannerConst.FileType.FILE_TYPE_OTHER:{
				ArrayList<OtherInfo> infos = deviceInfo.getOtherInfos();
				int index = 0;
				while(infos.size() > index){
					if (!FileUtil.isDeviceMounted(mDevice)){
						L.i(TAG, "device had unmounted");
						return ScannerConst.OnEndCode.CODE_UNMOUNT;
					}
					
					callOnGetFileInfo(new Gson().toJson(infos.get(index)));
					index++;
				}
				return ScannerConst.OnEndCode.CODE_FINISH;
			}
			default:
				return ScannerConst.OnEndCode.CODE_UNKNOWN;
			}
		}
		
	}

}
