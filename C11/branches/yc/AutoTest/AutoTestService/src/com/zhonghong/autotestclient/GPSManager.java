/**
 * 
 */
package com.zhonghong.autotestclient;

import java.util.Iterator;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationManager;

/**
 * @author YC
 * @time 2016-12-7 上午10:33:11
 * TODO:
 */
public class GPSManager {

	private Context mContext;
	private LocationManager mLocationManager;

	public GPSManager(Context context) {
		super();
		this.mContext = context;
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);  
	}
	
	public boolean isGPSOpen(){
		return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	public void turnOnOffGPS(boolean on){
		if (on){
			if (!isGPSOpen()){
				mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
			}
		}
		else{
			if (isGPSOpen()){
				mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false);
			}
		}
	}
	
	public int getSatelliteTotalNum(){
		int count = 0;
		Iterator<GpsSatellite> iterator = mLocationManager.getGpsStatus(null).getSatellites().iterator();
		while(iterator.hasNext()){
	     	count++;
	     }
		return count;
	}
	
	public int getStatelliteUsedNum(){
		int count = 0;
		Iterator<GpsSatellite> iterator = mLocationManager.getGpsStatus(null).getSatellites().iterator();
		while(iterator.hasNext()){
			GpsSatellite satellite = iterator.next();
			if (satellite.usedInFix()){
				count++;
			}
	     }
		return count;
	}
	
	/**
	 * get UTC Time since 1970.1.1
	 * @return
	 */
	public long getGPSTime(){
		Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		return location.getTime();
	}
	
	/**
	 * m/s
	 * @return
	 */
	public float getGPSSpeed(){
		Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		return location.getSpeed();
	}
	
}
