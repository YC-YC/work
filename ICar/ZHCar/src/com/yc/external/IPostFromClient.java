/**
 * 
 */
package com.yc.external;

/**
 * @author YC
 * @time 2016-7-15 下午2:39:54
 * TODO:
 */
public interface IPostFromClient {
	/**
	 * 客户端主动向服务端post数据
	 * @param cmd
	 * @param val
	 * @return 若处理返回true,不处理返回false
	 */
	boolean postInfo(int cmd, String val);
	
}
