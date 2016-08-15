/**
 * 
 */
package com.yc.external;

import com.yc.conn.ConnExternal;

/**
 * @author YC
 * @time 2016-7-24 上午10:17:43
 * TODO: 获取信息的基类，只需要实现getClientInfo即可
 */
public abstract class BaseGetInfo implements IGetFromClient {

	@Override
	public String getInfo(int cmd) {
		return getClientInfo(cmd);
	}
	
	public abstract String getClientInfo(int cmd);

	public BaseGetInfo(){
		ConnExternal.getInstance().register(this);
	}
	
	@Override
	protected void finalize() throws Throwable {
		ConnExternal.getInstance().unregister(this);
		super.finalize();
	}
}
