package com.zh.bt.service;


import java.util.ArrayList;
import java.util.List;

import com.nforetek.bt.aidl.UiCallbackBluetooth;
import com.nforetek.bt.aidl.UiCommand;
import com.nforetek.bt.res.NfDef;
import com.zh.bt.entry.BtDevice;
import com.zh.bt.listener.BtStateChangedListener;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/**
 * 蓝牙通讯service管理
 * @author xbgu
 *
 */
public class BtSettingsService extends Service{
    private static final String TAG = "BtSettingsService";
    public static final String BT_SERVICE = "BtSettingsService";
    // 实例化自定义的Binder类
    private final IBinder mBinder = new LocalBinder();

    private BluetoothAdapter mBluetoothAdapter = null;

    IntentFilter mFilter;
    public boolean bStartService = false;
    private UiCommand mCommand;
    private static final int HANDLER_EVENT_UPDATE_NEW_DEVICE_ARRAY_ADAPTER = 15;
    private static final int HANDLER_EVENT_UPDATE_PAIRED_DEVICE_ARRAY_ADAPTER = 16;
    
    private ArrayList<BtDevice> device_founded_list = new ArrayList<BtDevice>();
    private ArrayList<BtDevice> pairedDeviceList = new ArrayList<BtDevice>();
    
    private int mBtRoleMode = -1;
    
    private String mConnectedAddress;
    private int mBtState;
    private int mHfpState;
    private int mA2dpState;
    private int mAvrcpState;
    
    BtStateChangedListener mBtStateChangedListener;
    
    /**
     * 自定义的Binder类，这个是一个内部类，所以可以知道其外围类的对象，通过这个类，让Activity知道其Service的对象
     */
    public class LocalBinder extends Binder {
    	public BtSettingsService getService() {
            // 返回Activity所关联的Service对象，这样在Activity里，就可调用Service里的一些公用方法和公用属性
    	   	Log.d(TAG, "LocalBinder...");
    		return BtSettingsService.this;
        }
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
    	Log.d(TAG, "TransactionService onBind()");
		//return null;

        return mBinder;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
    	Log.d(TAG, "BtSettingsService onCreate()");

        bindService( new Intent( "com.nforetek.bt.START_UI_SERVICE" ), this.mConnection, BIND_AUTO_CREATE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
    	Log.d(TAG, "BtSettingsService onStartCommand()");
		if(intent != null)
			onNewIntent(intent, startId);
		return START_STICKY;
	}

    @Override
	public void onDestroy() {
		// TODO Auto-generated method stub
    	Log.d(TAG, "BtSettingsService onDestroy()");
        disableService();
        bStartService = false;
        
		super.onDestroy();
	}
    
    @Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
    	Log.d(TAG, "TransactionService onUnbind()");
		return super.onUnbind(intent);
	}

    public void onNewIntent(Intent intent, int serviceId) {
    	String action = intent.getAction();
    	//boolean bEnable = intent.getBooleanExtra("bluetooth is enable", false);
    	Log.d(TAG, "onNewIntent: " + action);
    	Log.d(TAG, "bStartService: " + bStartService);
    	if(action.equals(BtSettingsInfo.Action.START_SERVICE)){
            //if (bEnable) {
    		if(!bStartService && mBluetoothAdapter.isEnabled()) {
             	enableService();
            }
    		bStartService = true;
    		
    	}  else if(action.equals(BtSettingsInfo.Action.RESTART_SERVICE)){
       		reStartService();
       		
    	} else if (action.equals(BtSettingsInfo.Action.STOP_SERVICE)){
    		disableService();
            bStartService = false;
    		
    	} 
    }  
    private ServiceConnection mConnection = new ServiceConnection() 
    {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.e(TAG, "ready  onServiceConnected");
            mCommand = UiCommand.Stub.asInterface(service);
            if (mCommand == null) {
                Log.e(TAG,"mCommand is null!!");
                //Toast.makeText(getApplicationContext(), "UiService is null!", Toast.LENGTH_SHORT).show();
                //finish();
            }
            try {
                mCommand.registerBtCallback(mCallbackBluetooth);
                mCommand.reqBtPairedDevices();
                String adapterName = mCommand.getBtLocalName();
                Log.e(TAG,"nFore service version: " + mCommand.getNfServiceVersionName());
                updateConnectState();

            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            Log.e(TAG, "end  onServiceConnected");
        }

        public void onServiceDisconnected(ComponentName	className) {
            Log.e(TAG, "onServiceDisconnected");	
            mCommand = null;
        }
    };

    /*protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			//监听蓝牙开启/关闭状态变化
			if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
	            //int previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.STATE_OFF);
	            int newState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
	            //Log.d(TAG, "Previous state: " + previousState + " New state: " + newState);
	            Log.d(TAG, " New state: " + newState);
	            if (newState == BluetoothAdapter.STATE_OFF) {
	            	//节省资源
	            	disableService();
	            }
	            else if(newState==BluetoothAdapter.STATE_ON){
	                
	            	//开启后台
	            	enableService();
	            }
			}


		}
	};*/
	
    public void registerBtStateListener(BtStateChangedListener listener ){
    	mBtStateChangedListener = listener;
    }
    
	public void initService()
	{
		Log.d(TAG, "initService() ");
		
	}
	public void enableService(){
		
		Log.d(TAG, ">>-------------->>enableService() ");
		
        bindService( new Intent( "com.nforetek.bt.START_UI_SERVICE" ), this.mConnection, BIND_AUTO_CREATE);
	}
	
	public void disableService() {
		
		Log.d(TAG, ">>-------------->>disableService() ");
		
		
	}
	
	public void reStartService(){
		
		Log.d(TAG, ">>-------------->>reStartService() ");
		
	}

	public void showInfo(String info, int duration){
    	
		Toast.makeText(this, info, duration).show();
    }
	
	   /**
     * Service callback
     * 
     */
    private UiCallbackBluetooth mCallbackBluetooth = new UiCallbackBluetooth.Stub() 
    {

        @Override
        public void onBluetoothServiceReady() throws RemoteException {
            Log.i(TAG,"onBluetoothServiceReady");

        };

        @Override
        public void onAdapterStateChanged(int prevState, int newState) throws RemoteException {
            Log.i(TAG,"onAdapterStateChanged() state: " + prevState + "->" + newState);
            //String bluetoothStateString = "";
            updateBtState(prevState, newState);
            if(mBtStateChangedListener != null){
            	mBtStateChangedListener.onAdapterStateChanged(prevState, newState);
            }
        }

        @Override
        public void onAdapterDiscoverableModeChanged(int prevState, int newState)
                throws RemoteException {
            Log.i(TAG,"onAdapterDiscoverableModeChanged() " + prevState + "->" + newState);
            if(mBtStateChangedListener != null){
            	mBtStateChangedListener.onAdapterDiscoverableModeChanged(prevState, newState);
            }
        }

        @Override
        public void onAdapterDiscoveryStarted() throws RemoteException {
            Log.i(TAG,"onAdapterDiscoveryStarted()");
        	device_founded_list.clear();
        	pairedDeviceList.clear();

            if(mBtStateChangedListener != null){
            	mBtStateChangedListener.onAdapterDiscoveryStarted();
            }
        }

        @Override
        public void onAdapterDiscoveryFinished() throws RemoteException {
            Log.i(TAG,"onAdapterDiscoveryFinished()");
            if(mBtStateChangedListener != null){
            	mBtStateChangedListener.onAdapterDiscoveryFinished();
            }
        }

        @Override
        public void retPairedDevices(int elements, String[] address, String[] name,
                int[] supportProfile, byte[] category) throws RemoteException {
            Log.i(TAG,"retPairedDevices() elements: " + elements);

            /*for(int i=0; i<supportProfile.length; i++) {
                Log.e(TAG,"name: " + name[i] + " supportProfile: " + supportProfile[i]);
                if (supportProfile[i] > 0) {
                    name[i] += "  (";
                }
                if ((supportProfile[i]& NfDef.PROFILE_HFP) > 0) {
                    name[i] += " HFP";
                }
                if ((supportProfile[i]& NfDef.PROFILE_A2DP) > 0) {
                    name[i] += " A2DP";
                }
                if ((supportProfile[i]& NfDef.PROFILE_AVRCP_14) > 0) {
                    name[i] += " AVRCP1.4";
                }
                else if ((supportProfile[i]& NfDef.PROFILE_AVRCP_13) > 0) {
                    name[i] += " AVRCP1.3";
                }
                else if ((supportProfile[i]& NfDef.PROFILE_AVRCP) > 0) {
                    name[i] += " AVRCP";
                }
                if ((supportProfile[i]& NfDef.PROFILE_PBAP) > 0) {
                    name[i] += " PBAP";
                }
                if ((supportProfile[i]& NfDef.PROFILE_MAP) > 0) {
                    name[i] += " MAP";
                }
                if ((supportProfile[i]& NfDef.PROFILE_IAP) > 0) {
                    name[i] += " iAP";
                }
                if (supportProfile[i] > 0) {
                    name[i] += " )";
                }
            }*/

            // If there are paired devices, add each one to the ArrayAdapter
            pairedDeviceList.clear();
            if (elements > 0) {
                for (int i = 0 ; i < elements ; i++) {
    	            BtDevice device = new BtDevice();
    	            device.setAddress(address[i]);
    	            device.setName(name[i]);
    	            device.setCategory(category[i]);
    	            pairedDeviceList.add(device);
                }
            } 
        }

        @Override
        public void onDeviceFound(String address, String name, byte category)
                throws RemoteException {
            Log.i(TAG,"onDeviceFound() " + address + " name: " + name);
            // When discovery finds a device
            boolean isPairedDevice = false;
            BtDevice device = new BtDevice();
            device.setAddress(address);
            device.setName(name);
            device.setCategory(category);
            for (int i = 0 ; i < pairedDeviceList.size() ; i ++) {
                if (address.equals(pairedDeviceList.get(i).getAddress())) {
                    isPairedDevice = true;
                    break;
                }
            }

            if (!isPairedDevice) {
                if (!device_founded_list.contains(device)) {
                    device_founded_list.add(device);
                }
            }  
        }

        @Override
        public void onDeviceBondStateChanged(String address, String name, int prevState,
                int newState) throws RemoteException {
            Log.i(TAG,"onDeviceBondStateChanged() " + address + " name: " + name + " state: " + prevState + "->" + newState);
            mCommand.reqBtPairedDevices();
        }

        @Override
        public void onDeviceUuidsUpdated(String address, String name, int supportProfile)
                throws RemoteException {
            Log.i(TAG,"onDeviceUuidsUpdated() " + address + " name: " + name + " supportProfile: " + supportProfile);
            mCommand.reqBtPairedDevices();
        }

        @Override
        public void onLocalAdapterNameChanged(String name) throws RemoteException {
            Log.i(TAG,"onLocalAdapterNameChanged() name: " + name);
            
            if(mBtStateChangedListener != null){
            	mBtStateChangedListener.onAdapterNameChanged(name);
            }
        }

        @Override
        public void onHfpStateChanged(String address, int prevState, int newState)
                throws RemoteException {
            Log.i(TAG,"onHfpStateChanged() " + address + " state: " + prevState + "->" + newState);
            updateConnectState();
            if(mBtStateChangedListener != null){
            	mBtStateChangedListener.onHfpStateChanged(address, prevState, newState);
            }
        }

        @Override
        public void onA2dpStateChanged(String address, int prevState, int newState)
                throws RemoteException {
            Log.i(TAG,"onA2dpStateChanged() " + address + " state: " + prevState + "->" + newState);
            updateConnectState();
            if(mBtStateChangedListener != null){
            	mBtStateChangedListener.onA2dpStateChanged(address, prevState, newState);
            }
        }

        @Override
        public void onAvrcpStateChanged(String address, int prevState, int newState)
                throws RemoteException {
            Log.i(TAG,"onAvrcpStateChanged() " + address + " state: " + prevState + "->" + newState);
            updateConnectState();
        }

        @Override
        public void onDeviceOutOfRange(String address) throws RemoteException {
            Log.i(TAG,"onDeviceOutOfRange() " + address);

        }
        
        @Override
        public void onBtRoleModeChanged(int mode) throws RemoteException {
            Log.i(TAG,"onBtRoleModeChanged() " + mode);
            mBtRoleMode = mode;
            
        }
        
    };
    
    
    /************************************************************************************************************************
     * addb by gu
     ************************************************************************************************************************/
    
    private void updateConnectState() {
        if (mCommand == null) {
            return;
        }
        try {
            String hfpConnectedAddress = mCommand.getHfpConnectedAddress();
            String a2dpConnectedAddress = mCommand.getA2dpConnectedAddress();
            String avrcpConnectedAddress = mCommand.getAvrcpConnectedAddress();

            mHfpState = mCommand.getHfpConnectionState();
            mA2dpState = mCommand.getA2dpConnectionState();
            mAvrcpState = mCommand.getAvrcpConnectionState();
            
            mConnectedAddress = hfpConnectedAddress;
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
    
    private void updateBtState(int prevState, int newState) {
    	if(mBtState != newState){
    		mBtState = newState;
    	}
    }
    /**
     * 蓝牙是否开启
     */
    public boolean isBtEnabled(){
    	return mBtState == NfDef.BT_STATE_ON;
    }
     
    /**
     * Hfp是否已连接
     */
    public boolean isHfpConnected(){
    	return mHfpState == NfDef.STATE_CONNECTED;
    }
    /**
     * A2dp是否已连接
     */
    public boolean isA2dpConnected(){
    	return mA2dpState == NfDef.STATE_CONNECTED;
    }
    /**
     * Avrcp是否已连接
     */
    public boolean isAvrcpConnected(){
    	return mAvrcpState == NfDef.STATE_CONNECTED;
    }
    /**
     * 已配对列表数据
     */
    public List<BtDevice> getPairedDeviceList(){
    	return pairedDeviceList;
    }
    /**
     * 清除已配对列表数据
     */
    public void clearPairedDeviceList(){
    	pairedDeviceList.clear();
    }
    /**
     * 搜寻到的可见列表数据
     */
    public List<BtDevice> getFoundedList(){
    	return device_founded_list;
    }
    /**
     * 清除搜寻到的可见列表数据
     */
    public void clearFoundedList(){
    	device_founded_list.clear();
    }
    
    /**
     * 开启蓝牙
     */
    public void setBtEnable(){
    	Log.d(TAG, "setBtEnable()");
    	if (mCommand == null) {
            Log.e(TAG, "mCommand is null!!");
            
        } else {
        	
			try {
				
				mCommand.setBtEnable(true);
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    /**
     * 关闭蓝牙
     */
    public void setBtDisable(){
    	Log.d(TAG, "setBtDisable()");
    	if (mCommand == null) {
            Log.e(TAG, "mCommand is null!!");
            
        } else {
        	
			try {
				
				mCommand.setBtEnable(false);
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         }
    }    
    //
    /**
     * 获取可见状态
     * 
     */
    public boolean isBtDiscoverable(){
    	Log.d(TAG, "isBtDiscoverable()");

    	if (mCommand == null) {
            Log.e(TAG, "mCommand is null!!");
            
        } else {
        	
			try {
				return mCommand.isBtDiscoverable();
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
    	return true;
    }
    
    /**
     * 请求扫描搜寻设备
     * 
     */
    public void requestDiscoveryDevice(){
    	Log.d(TAG, "requestDiscoveryDevice: isBtEnabled? " + isBtEnabled());

    	if (mCommand == null) {
            Log.e(TAG, "mCommand is null!!");
            
        } else {
        	if(isBtEnabled()){
        		try {
						if(mCommand.isBtDiscovering()){
							return;
						}else{
							mCommand.startBtDiscovery();
						}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.e(TAG, "error: " + e.toString());
				}
        	}
        }
    }
    /**
     * 请求取消扫描蓝牙设备
     */
    public void requestCancelDiscoveryDevice(){
    	Log.d(TAG, "requestCancelDiscoveryDevice");
    	if (mCommand == null) {
            Log.e(TAG, "mCommand is null!!");
            
        }else{
        	try {
        		if(mCommand.isBtDiscovering()){
        			mCommand.cancelBtDiscovery();
        		}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG, "error: " + e.toString());
			}
        }
    }    
    
    /**
     * 请求连接蓝牙设备
     */
    public void requestToConectDevice(String address){
    	Log.d(TAG, "appRequestToConectDevice: " + address);
    	if (mCommand == null) {
            Log.e(TAG, "mCommand is null!!");
            
        }else{
        	try {
				mCommand.reqBtConnectHfpA2dp(address);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG, "error: " + e.toString());
			}
        }
    }
    
    /**
     * 请求断开蓝牙设备
     */
    public void requestToDisconectDevice(String address){
    	Log.d(TAG, "appRequestToDisconectDevice: " + address);
    	if (mCommand == null) {
            Log.e(TAG, "mCommand is null!!");
            
        }else{
        	try {
				mCommand.reqBtDisconnectAll();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(TAG, "error: " + e.toString());
			}
        }
    }
    
}
