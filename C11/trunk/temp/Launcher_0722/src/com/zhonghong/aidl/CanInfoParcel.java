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
	private int Temperature; //温度
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


	public int getTemperature() {
		return Temperature;
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

	public void setTemperature(int Temperature) {
		this.Temperature = Temperature;
	}


	public boolean isAutoHighWind() {
		return AutoHighWind;
	}

	public void setAutoHighWind(boolean autoHighWind) {
		AutoHighWind = autoHighWind;
	}

	public CanInfoParcel(int mwindSpeed, int mblowMode,
			int airCircurlationMode, byte Temperature, boolean autoWind) {
		super();
		this.mwindSpeed = mwindSpeed;
		this.mblowMode = mblowMode;
		AirCircurlationMode = airCircurlationMode;
		this.Temperature = Temperature;
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
		Temperature = in.readInt();
		AutoHighWind = (in.readByte()!=0);
	}

	@Override
	public String toString() {
		return "CanInfoParcel [mwindSpeed=" + mwindSpeed + ", mblowMode="
				+ mblowMode + ", AirCircurlationMode=" + AirCircurlationMode
				+ ", leftTemperature=" + Temperature + ", AutoHighWind="
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
		dest.writeInt(Temperature);
		dest.writeByte((byte)(AutoHighWind?1:0));
	}

	
	
	
}
