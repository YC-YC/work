/**
 * 
 */
package com.yc.external;

import java.util.HashSet;

/**
 * @author YC
 * @time 2016-7-15 下午2:44:24
 * TODO:处理远程数据处理的中心管理类
 */
public class ConnManager implements IPostFromClient {

	private static ConnManager instance;
	
	private HashSet<IPostFromClient> mExternals;
	private IGetFromClient mClient;
	
	public static ConnManager getInstance() {
		if (instance == null) {
			synchronized (ConnManager.class) {
				if (instance == null) {
					instance = new ConnManager();
				}
			}
		}
		return instance;
	}
	
	@Override
	public boolean postInfo(int cmd, String val) {
		for (IPostFromClient external: mExternals){
			if (external.postInfo(cmd, val))
				return true;
		}
		return false;
	}

	/**
	 * 从远程客户端获取数据
	 * @param cmd
	 * @return
	 */
	public String getInfo(int cmd){
		if (mClient != null)
			return mClient.getInfo(cmd);
		return null;
	}

	/**
	 * 只在Service调用
	 * @param client
	 */
	public void setGetFromClient(IGetFromClient client){
		mClient = client;
	}
	
	public void register(IPostFromClient external){
		synchronized (mExternals) {
			mExternals.add(external);
		}
	}
	
	public void unregister(IPostFromClient external){
		synchronized (mExternals) {
			mExternals.remove(external);
		}
	}
	
	private ConnManager(){
		mExternals = new HashSet<IPostFromClient>();
	}
	
}
