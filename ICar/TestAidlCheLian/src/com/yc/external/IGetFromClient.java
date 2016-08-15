/**
 * 
 */
package com.yc.external;

/**
 * @author YC
 * @time 2016-7-15 下午3:47:46
 * TODO:
 */
public interface IGetFromClient {
	/**
	 * 从远程客户端获取数据
	 * @param cmd
	 * @return 远程客户端返回的值
	 */
	String getInfo(int cmd);
}
