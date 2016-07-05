/**
 * 
 */
/**
 * @author xbgu
 * 与设置相关的蓝牙状态监听
 */
package com.zh.bt.listener;


public interface BtStateChangedListener{	
	/**蓝牙设备开关状态*/
    void onAdapterStateChanged(int prevState, int newState);
	/**蓝牙设备名称*/
    void onAdapterNameChanged(String name);
    
    /**Hfp协议连接状态*/
    void onHfpStateChanged(String address, int prevState, int newState);
    /**A2dp协议连接状态*/
    void onA2dpStateChanged(String address, int prevState, int newState);
    /**扫描状态*/
    void onScanStateChanged(int state);
    /**可见状态*/
    void onDiscoveryStateChanged(int state);
    /**蓝牙列表*/
    void onListChanged(int state);
    /**蓝牙可见状态*/
    void onAdapterDiscoverableModeChanged(int prevState, int newState);
    /**蓝牙可见开始*/
    void onAdapterDiscoveryStarted();
    /**蓝牙可见结束*/
    void onAdapterDiscoveryFinished();
}




