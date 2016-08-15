/**
 * 
 */
package com.yc.conn;

import java.util.HashSet;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.yc.external.CallFromService;
import com.yc.external.ExternalConn;
import com.yc.external.IGetFromClient;

/**
 * @author YC
 * @time 2016-7-15 上午11:48:23 
 * TODO:aild连接
 */
public class ConnExternal {

	private static ConnExternal instance;

	private Context mContext;
	private ExternalConn mExternalConn;
	private HashSet<IGetFromClient> mClients;

	public static ConnExternal getInstance() {
		if (instance == null) {
			synchronized (ConnExternal.class) {
				if (instance == null) {
					instance = new ConnExternal();
				}
			}
		}
		return instance;
	}

	public void bindService(Context context) {
		mContext = context;
		conn();
	}
	
	public void unbindService(){
		unConn();
	}
	
	
	

	public boolean isConn(){
		return mExternalConn != null;
	}

	
	public boolean postInfo(int cmd, String val){
		boolean result = false;
		if (isConn())
			try {
				result = mExternalConn.postInfo(cmd, val);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		return result;
	}
	
	public void register(IGetFromClient client){
		synchronized (mClients) {
			mClients.add(client);
		}
	}
	
	public void unregister(IGetFromClient client){
		synchronized (mClients) {
			mClients.remove(client);
		}
	}
	
	
	private ConnExternal() {
		mClients = new HashSet<IGetFromClient>();
	}

	private void conn() {
		Intent intent = new Intent();
		intent.setAction("com.yc.external.AidlService");
		mContext.bindService(intent, conn, 1);
	}

	private void unConn() {
//		if (conn != null){
		if (isConn()){
			mContext.unbindService(conn);	
			mExternalConn = null;
		}
	}
	
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mExternalConn = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mExternalConn = ExternalConn.Stub.asInterface(service);
			if (mExternalConn != null)
				try {
					mExternalConn.registerCallFromService(new CallFromService.Stub() {
						
						@Override
						public String getInfo(int cmd) throws RemoteException {
							String result = null;
							for (IGetFromClient client: mClients){
								if ((result = client.getInfo(cmd)) != null){
									return result;
								}
							}
							return null;
						}
					});
				} catch (RemoteException e) {
					e.printStackTrace();
				}
		}
	};
}
