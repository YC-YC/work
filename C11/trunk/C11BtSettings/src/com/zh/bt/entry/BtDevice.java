package com.zh.bt.entry;

import java.io.Serializable;

public class BtDevice { //implements Serializable, Comparable<BtDevice>{

	/**蓝牙设备名称*/
	private String name;
	
	/**设备地址*/
	private String address;

	/**支持的profile*/
	private String supportProfile;
	
	/**角色类型*/
	private byte category;
	
	/**Hfp是否已连接*/
	private boolean isHfpConnected;
	
	/**A2dp是否已连接*/
	private boolean isA2dpConnected;
	
	public static final byte CAT_COMPUTER = 1;
	public static final byte CAT_PHONE = 1 << 1;
	public static final byte CAT_STEREO = 1 << 2;
	public static final byte CAT_MONO_AUDIO = 1 << 3;

	
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
	
	public void setAddress(String address){
		this.address = address;
	}
	public String getAddress(){
		return this.address;
	}
	
	public void setSupportProfile(String supportProfile){
		this.supportProfile = supportProfile;
	}
	public String getSupportProfile(){
		return this.supportProfile;
	}
	
	public void setCategory(byte category){
		this.category = category;
	}
	public byte getCategory(){
		return this.category;
	}
	
	public void setIsHfpConnected(boolean isHfpConnected){
		this.isHfpConnected = isHfpConnected;
	}
	public boolean getIsHfpConnected(){
		return this.isHfpConnected;
	}
	
	public void setIsA2dpConnected(boolean isA2dpConnected){
		this.isA2dpConnected = isA2dpConnected;
	}
	public boolean getIsA2dpConnected(){
		return this.isHfpConnected;
	}
	
	/*@Override
	public int compareTo(BtDevice another) {
		// TODO Auto-generated method stub
		return 0;
	}*/

}
