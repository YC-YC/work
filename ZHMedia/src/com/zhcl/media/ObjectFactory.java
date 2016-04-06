/**
 * 
 */
package com.zhcl.media;

/**
 * @author chenli
 *
 */
public class ObjectFactory {
	/** 构造songinfo对象 */
	public static final int INSTANCE_SONGINFO = 53;
	
	/**
	 * 获得对应对象的构造工厂
	 */
	public static Object getInstance(int instanceID){
		switch(instanceID){
		case INSTANCE_SONGINFO:
			break;
		}
		return null;
	}
}
