/**
 * 
 */
package com.zhonghong.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author YC
 * @time 2016-4-12 上午9:59:08
 * Aidl供外部使用
 */
public class CanInfoParcel implements Parcelable {

	private int mwindSpeed;// 风速
	private int mblowMode; // 风向模式
	private int AirCircurlationMode;//车内空气循环模式
	private byte leftTemperature;
	private boolean AutoHighWind;
	
	
	public CanInfoParcel() {
		super();
	}
	
	public int getMwindSpeed() {
		return mwindSpeed;
	}

	public int getMblowMode() {
		return mblowMode;
	}

	public int getAirCircurlationMode() {
		return AirCircurlationMode;
	}


	public byte getLeftTemperature() {
		return leftTemperature;
	}


	public void setMwindSpeed(int mwindSpeed) {
		this.mwindSpeed = mwindSpeed;
	}

	public void setMblowMode(int mblowMode) {
		this.mblowMode = mblowMode;
	}

	public void setAirCircurlationMode(int airCircurlationMode) {
		AirCircurlationMode = airCircurlationMode;
	}

	public void setLeftTemperature(byte leftTemperature) {
		this.leftTemperature = leftTemperature;
	}


	public boolean isAutoHighWind() {
		return AutoHighWind;
	}

	public void setAutoHighWind(boolean autoHighWind) {
		AutoHighWind = autoHighWind;
	}

	public CanInfoParcel(int mwindSpeed, int mblowMode,
			int airCircurlationMode, byte leftTemperature, boolean autoWind) {
		super();
		this.mwindSpeed = mwindSpeed;
		this.mblowMode = mblowMode;
		AirCircurlationMode = airCircurlationMode;
		this.leftTemperature = leftTemperature;
		AutoHighWind = autoWind;
	}

	public CanInfoParcel(Parcel in) {
		readFromParcel(in);
	}

	private void readFromParcel(Parcel in) {
		//TODO 顺序要和Write一样
		mwindSpeed = in.readInt();
		mblowMode = in.readInt();
		AirCircurlationMode = in.readInt();
		leftTemperature = in.readByte();
		AutoHighWind = (in.readByte()!=0);
	}

	@Override
	public String toString() {
		return "CanInfoParcel [mwindSpeed=" + mwindSpeed + ", mblowMode="
				+ mblowMode + ", AirCircurlationMode=" + AirCircurlationMode
				+ ", leftTemperature=" + leftTemperature + ", AutoHighWind="
				+ AutoHighWind + "]";
	}

	public static final Creator<CanInfoParcel> CREATOR = new Creator<CanInfoParcel>() {

		@Override
		public CanInfoParcel createFromParcel(Parcel source) {
			return new CanInfoParcel(source);
		}

		@Override
		public CanInfoParcel[] newArray(int size) {
			return new CanInfoParcel[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		//TODO 要按顺序写
		dest.writeInt(mwindSpeed);
		dest.writeInt(mblowMode);
		dest.writeInt(AirCircurlationMode);
		dest.writeByte(leftTemperature);
		dest.writeByte((byte)(AutoHighWind?1:0));
	}

	
	
	
}
