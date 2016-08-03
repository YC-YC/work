/**
 * 
 */
package com.zhonghong.rate;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * @author YC
 * @time 2016-7-27 下午5:47:13
 * TODO:心率处理
 */
public class RateManager implements IStatus{

	private final String TAG = getClass().getSimpleName();

	/**缓存的RateInfo个数*/
	private final int CACHE_RATEINFO_COUNT = 5;
	
	public static final int STATUS_IDLE = 1;
	public static final int STATUS_PREPARE = 2;
	public static final int STATUS_READY = 3;
	public static final int STATUS_CHECKING = 4;
	public static final int STATUS_CHECKED = 5;
	
	private static RateManager mRateManager;
	
	private IdleStatus mIdleStatus;
	private PrepareStatus mPrepareStatus;
	private ReadyStatus mReadyStatus;
	private CheckingStatus mCheckingStatus;
	private CheckedStatus mCheckedStatus;

	private IStatus mCurStatus;
	
	private List<RateInfo> mRateInfos;
	
	private OnStatusChangeListener mOnStatusChangeListener;
	
	public void setOnStatusChangeListener(OnStatusChangeListener listener){
		mOnStatusChangeListener = listener;
	}
	
	private RateManager(){
		mIdleStatus = new IdleStatus();
		mPrepareStatus = new PrepareStatus();
		mReadyStatus = new ReadyStatus();
		mCheckingStatus = new CheckingStatus();
		mCheckedStatus = new CheckedStatus();
		mRateInfos = new ArrayList<RateInfo>();
		setCurStatus(mIdleStatus);
	}
	
	public static RateManager getRateManager(){
		if (mRateManager == null){
			synchronized (RateManager.class) {
				if (mRateManager == null){
					mRateManager = new RateManager();
				}
			}
		}
		return mRateManager;
	}


	public IdleStatus getIdleStatus() {
		return mIdleStatus;
	}

	public PrepareStatus getPrepareStatus() {
		return mPrepareStatus;
	}

	public ReadyStatus getReadyStatus() {
		return mReadyStatus;
	}

	public CheckingStatus getCheckingStatus() {
		return mCheckingStatus;
	}

	public CheckedStatus getCheckedStatus() {
		return mCheckedStatus;
	}

	public IStatus getCurStatus() {
		return mCurStatus;
	}
	
	public int getCurStatusInt(){
		return genStatus(getCurStatus());
	}
	
	public boolean isValued(){
		return (genStatus(getCurStatus()) >= STATUS_READY ? true: false);
	}
	
	public void setCurStatus(IStatus status){
		IStatus oldStatus = mCurStatus;
		mCurStatus = status;
		Log.i(TAG, "setCurStatus = " + status.getClass().getName());
		reset();
		if (mCurStatus != oldStatus){
			if (mOnStatusChangeListener != null){
				mOnStatusChangeListener.onStatusChange(genStatus(oldStatus), genStatus(mCurStatus));
			}
		}
	}
	

	@Override
	public void inputRate(RateInfo info) {
		
		cacheRateInfo(info);
		
		if (isValuedRateInfo(info)){
			mCurStatus.inputRate(info);
		}
		
		if (needToReset()){
			setCurStatus(mIdleStatus);
		}
	}
	
	/**
	 * 缓存数据
	 * @param info
	 */
	private void cacheRateInfo(RateInfo info){
		mRateInfos.add(info);
		if (mRateInfos.size() > CACHE_RATEINFO_COUNT){
			mRateInfos = mRateInfos.subList(mRateInfos.size() - CACHE_RATEINFO_COUNT, mRateInfos.size()-1);
		}
	}

	/**
	 * 是否是有效的心率数据
	 * @param info
	 * @return
	 */
	private boolean isValuedRateInfo(RateInfo info){
		if (info.getRateVal() > 0 && info.getRateVal() < 255){
//			Log.i(TAG, "isValuedRateInfo");
			return true;
		}
		return false;
	}
	
	/**
	 * 是否重新处理
	 * @return
	 */
	private boolean needToReset(){
		if (isValuedRateInfo(mRateInfos.get(mRateInfos.size()-1))){
			return false;
		}
		else{
//			Log.i(TAG, "needToReset");
			return true;
		}
	}
	
	
	@Override
	public void reset() {
		mCurStatus.reset();
	}

	/*@Override
	public int getMin() {
		return mCurStatus.getMin();
	}

	@Override
	public int getMax() {
		return mCurStatus.getMax();
	}

	@Override
	public int getAverage() {
		return mCurStatus.getAverage();
	}*/
	
	/**
	 * 将接口类转成int型
	 * @param status
	 * @return
	 */
	private int genStatus(IStatus status){
		if (status instanceof IdleStatus){
			return STATUS_IDLE;
		}
		if (status instanceof PrepareStatus){
			return STATUS_PREPARE;
		}
		if (status instanceof ReadyStatus){
			return STATUS_READY;
		}
		if (status instanceof CheckingStatus){
			return STATUS_CHECKING;
		}
		if (status instanceof CheckedStatus){
			return STATUS_CHECKED;
		}
		return STATUS_IDLE;
	}
	
}
