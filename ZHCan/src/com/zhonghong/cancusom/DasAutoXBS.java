package com.zhonghong.cancusom;

import android.os.Parcel;
import android.os.Parcelable;

public class DasAutoXBS implements Cloneable{
	public boolean AC = false;
	public boolean canOff = true;
	public int windSpeed = 0;
	public byte windIntensity = 0 ;
	public int carSpeed = 0;
	public int blowMode =0; //风向模式
	public byte leftTemperature = 0;
	public byte rightTemperature = 0;
	public boolean bAutoLowWind = false;
	public boolean bAutoHighWind = false;
	public boolean RearLock = false;
	public boolean FWRmFlog = false;
	public boolean Dual = false;
	public int rightSeatWarn = 0;
	public int leftSeatWarn = 0;
	public byte rightSeatWind = 0;
	public byte leftSeatWind = 0;
	public byte outdoorTemperature = 0;
	public int AirCircurlationMode = 0; //车内空气循环模式
	public boolean WinMax = false;
	public boolean AcMax = false;
	public boolean Temp = false;
	public boolean Amb = false;
	public boolean Eco = false;
	
	public boolean FroutLeftDoor = false;
	public boolean FrontRightDoor = false;
	public boolean RearLeftDoor = false;
	public boolean RearRightDoor = false;
	public boolean RearBox = false;
	public boolean FrontTop = false;
	public boolean iSafetyBelt = false;
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		DasAutoXBS sc = null;  
        try  
        {  
            sc = (DasAutoXBS) super.clone();  
        } catch (CloneNotSupportedException e){  
            e.printStackTrace();  
        }  
        return sc;  
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DasAutoXBS other = (DasAutoXBS) obj;
		if (AC != other.AC)
			return false;
		if (AcMax != other.AcMax)
			return false;
		if (AirCircurlationMode != other.AirCircurlationMode)
			return false;
		if (Amb != other.Amb)
			return false;
		if (Dual != other.Dual)
			return false;
		if (Eco != other.Eco)
			return false;
		if (FWRmFlog != other.FWRmFlog)
			return false;
		if (FrontRightDoor != other.FrontRightDoor)
			return false;
		if (FrontTop != other.FrontTop)
			return false;
		if (FroutLeftDoor != other.FroutLeftDoor)
			return false;
		if (RearBox != other.RearBox)
			return false;
		if (RearLeftDoor != other.RearLeftDoor)
			return false;
		if (RearLock != other.RearLock)
			return false;
		if (RearRightDoor != other.RearRightDoor)
			return false;
		if (Temp != other.Temp)
			return false;
		if (WinMax != other.WinMax)
			return false;
		if (bAutoHighWind != other.bAutoHighWind)
			return false;
		if (bAutoLowWind != other.bAutoLowWind)
			return false;
		if (blowMode != other.blowMode)
			return false;
		if (canOff != other.canOff)
			return false;
		if (carSpeed != other.carSpeed)
			return false;
		if (iSafetyBelt != other.iSafetyBelt)
			return false;
		if (leftSeatWarn != other.leftSeatWarn)
			return false;
		if (leftSeatWind != other.leftSeatWind)
			return false;
		if (leftTemperature != other.leftTemperature)
			return false;
		if (outdoorTemperature != other.outdoorTemperature)
			return false;
		if (rightSeatWarn != other.rightSeatWarn)
			return false;
		if (rightSeatWind != other.rightSeatWind)
			return false;
		if (rightTemperature != other.rightTemperature)
			return false;
		if (windIntensity != other.windIntensity)
			return false;
		if (windSpeed != other.windSpeed)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DasAutoXBS [AC=" + AC + ", canOff=" + canOff + ", windSpeed="
				+ windSpeed + ", windIntensity=" + windIntensity
				+ ", carSpeed=" + carSpeed + ", blowMode=" + blowMode
				+ ", leftTemperature=" + leftTemperature
				+ ", rightTemperature=" + rightTemperature + ", bAutoLowWind="
				+ bAutoLowWind + ", bAutoHighWind=" + bAutoHighWind
				+ ", RearLock=" + RearLock + ", FWRmFlog=" + FWRmFlog
				+ ", Dual=" + Dual + ", rightSeatWarn=" + rightSeatWarn
				+ ", leftSeatWarn=" + leftSeatWarn + ", rightSeatWind="
				+ rightSeatWind + ", leftSeatWind=" + leftSeatWind
				+ ", outdoorTemperature=" + outdoorTemperature
				+ ", AirCircurlationMode=" + AirCircurlationMode + ", WinMax="
				+ WinMax + ", AcMax=" + AcMax + ", Temp=" + Temp + ", Amb="
				+ Amb + ", Eco=" + Eco + ", FroutLeftDoor=" + FroutLeftDoor
				+ ", FrontRightDoor=" + FrontRightDoor + ", RearLeftDoor="
				+ RearLeftDoor + ", RearRightDoor=" + RearRightDoor
				+ ", RearBox=" + RearBox + ", FrontTop=" + FrontTop
				+ ", iSafetyBelt=" + iSafetyBelt + "]";
	}

	
}
