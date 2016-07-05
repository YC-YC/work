
/** 
 * nForeTek Spp callback interface for Android
 *
 * Copyright (C) 2011-2019  nForeTek Corporation
 *
 * Zeus 0.0.0 on 20150127
 * @author KC Huang	<kchuang@nforetek.com>
 * @author Piggy	<piggylee@nforetek.com>
 * @version 0.1.6
 *
 */

package com.nforetek.bt.aidl;

 /**
 * The callback interface for Serial Port profile (SPP).
 * <br>UI program should implement all methods of this interface 
 * for receiving possible callbacks from nFore service.
 * <br>The naming principle of callback in this Doc is as below,
 *		<blockquote>	<b>retXXX()</b> : must be the callback of requested API.
 *		<br>			<b>onXXX()</b>  : could be the callback for updated values or the callback from requested API.</blockquote>
 *
 * <p> The constant variables in this Doc could be found and referred by importing
 * 		<br><blockquote>com.nforetek.bt.res.NfDef</blockquote>
 * <p> with prefix NfDef class name. Ex : <code>NfDef.DEFAULT_ADDRESS</code>
 *
 * <p> Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
 *
 * @see INfCommandSpp
 */
 
interface UiCallbackSpp{

	/* ======================================================================================================================================================== 
	 * Callback function of state changed event to Spp
	 */

	 /** 
	 * Callback to inform SPP Service is ready.
	 */
	void onSppServiceReady();
	 
    /**
	 * Callback to inform state change of SPP connected remote device.
	 * 
	 * The possible values of state in this profile are 
	 * 		<p><blockquote>STATE_NOT_INITIALIZED		(int) 100
	 * 		<br>STATE_READY								(int) 110
	 * 		<br>STATE_CONNECTING						(int) 120
	 * 		<br>STATE_DISCONNECTING						(int) 125
	 *		<br>STATE_CONNECTED							(int) 140</blockquote>
	 * Parameter address is only valid in state greater than STATE_READY.
	 * In state STATE_NOT_INITIALIZED and STATE_READY, parameter address might contain unavailable content or 
	 * DEFAULT_ADDRESS , which is 00:00:00:00:00:00.
	 *
	 * @param address Bluetooth MAC address of remote device which involves state changed.
	 * @param deviceName the name of remote device.
	 * @param prevState the previous state. 
	 * The value of this parameter would be the same as newState if the new state is STATE_NOT_INITIALIZED.
	 * @param newState the new state.
     */
    void onSppStateChanged(String address, String deviceName, int prevState, int newState);

    /**
	 * Callback to inform the error response of SPP connected remote device.
	 *
	 * @param address Bluetooth MAC address of remote device.
	 * @param errorCode the possible reason of error. 
     */
    void onSppErrorResponse(String address, int errorCode);
    
    /**
	 * Callback to inform response to {@link INforeService#reqSppConnectedDeviceAddressList reqSppConnectedDeviceAddressList}
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
     * @param totalNum total number of device addresses in connected list 
     * @param addressList connected Bluetooth device address
     * @param nameList connected Bluetooth device name
     */
    void retSppConnectedDeviceAddressList(int totalNum, in String[] addressList, in String[] nameList);    

    /**
	 * Callback to inform the data have been received from device with SPP connected remote device.	 
	 *
	 * @param address Bluetooth MAC address of remote device.
     * @param receivedData the data received
     */
    void onSppDataReceived(String address, in byte[] receivedData);
    
    /**
	 * Callback to inform the data have been sent to SPP connected remote device.
	 *
	 * @param address Bluetooth MAC address of remote device.
     * @param length The length of data have been sent.
     */
    void onSppSendData(String address, int length);

    /**
	 * Callback to inform this apple device need to do iAP authentication.
	 * Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
	 *
	 * @param address Bluetooth MAC address of remote Apple device.
     */
    void onSppAppleIapAuthenticationRequest(String address);
}
