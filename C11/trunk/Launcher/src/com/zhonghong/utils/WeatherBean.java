/**
 * 
 */
package com.zhonghong.utils;

/**
 * @author YC
 * @time 2016-3-23 下午5:57:23
 */
public class WeatherBean {

	private String key;
	private int imgId;
	private String name;
	
	/**
	 * @param key
	 * @param imgId
	 * @param name
	 */
	public WeatherBean(String key, int imgId, String name) {
		super();
		this.key = key;
		this.imgId = imgId;
		this.name = name;
	}
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @return the imgId
	 */
	public int getImgId() {
		return imgId;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	

}
