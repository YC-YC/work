/** 
 * nForeTek HFP Callbacks Interface for Android 4.3
 *
 * Copyright (C) 2011-2019  nForeTek Corporation
 *
 * Zeus 0.0.0 on 20140530
 * @author KC Huang	<kchuang@nforetek.com>
 * @author Piggy	<piggylee@nforetek.com>
 * @version 0.0.0
 *
 */
 
package com.nforetek.bt.aidl;

import com.nforetek.bt.aidl.NfHfpClientCall;

/**
 * The callback interface for HandsFree profiles (HFP).
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
 * @see INfCommandHfp
 */
 
 
interface UiCallbackHfp { 

	/** 
	 * Callback to inform HFP Service is ready.
	 */
	void onHfpServiceReady(); 
    
	/** 
	 * Callback to inform state change of HFP connected remote device.
	 * <p>The possible values of state in this profile are: 
	 * 		<p><blockquote><b>STATE_NOT_INITIALIZED</b>			(int) 100
	 * 		<br><b>STATE_READY</b>								(int) 110
	 * 		<br><b>STATE_CONNECTING</b>							(int) 120
	 *		<br><b>STATE_CONNECTED</b>							(int) 140
	 * <br>Parameter address is not valid for <b>STATE_NOT_INITIALIZED</b> and <b>STATE_READY</b>.
	 * <br>It might contain unavailable content or <b>DEFAULT_ADDRESS</b> , which is 00:00:00:00:00:00.
	 *
	 * @param address Bluetooth MAC address of remote device which involves state changed.
	 * @param prevState the previous state. 
	 * @param newState the new state.
	 * 
     */
    void onHfpStateChanged(String address, int prevState, int newState);
    
	/** 
	 * Callback to inform audio state change of HFP connected remote device.
	 * <p>The possible values of state in this profile are: 
	 * 		<p><blockquote><b>STATE_NOT_INITIALIZED</b>		(int) 100
	 * 		<br><b>STATE_READY</b>							(int) 110
	 *		<br><b>STATE_CONNECTED</b>						(int) 140</blockquote>
	 * <br>Parameter address is not valid for <b>STATE_NOT_INITIALIZED</b> and <b>STATE_READY</b>.
	 * <br>It might contain unavailable content or DEFAULT_ADDRESS , which is 00:00:00:00:00:00.
	 *
	 * @param address Bluetooth MAC address of remote device.
	 * @param prevState the previous state. 
	 * @param newState the new state.
     */
    void onHfpAudioStateChanged(String address, int prevState, int newState);

    /**
	 * Callback to inform voice dial status of HFP connected remote device.
	 *
	 * @param address Bluetooth MAC address of remote device.
	 * @param isVoiceDialOn return voice dial status (True/False).
     */ 
    void onHfpVoiceDial(String address,boolean isVoiceDialOn);
    
    /**
	 * Callback to inform the error response of HFP connected remote device.
	 *
	 * @param address Bluetooth MAC address of remote device.
     * @param code HFP error information
     */
    void onHfpErrorResponse(String address, int code);
    
    /**
	 * Callback to inform the change on "Telecom Service" status from HFP connected remote device.
	 *
	 * @param address Bluetooth MAC address of remote device.
     * @param isTelecomServiceOn possible value are:
	 * <p><value>=false implies no service. No Home/Roam network available.
     * <p><value>=true implies presence of service. Home/Roam network available.  
     */
    void onHfpRemoteTelecomService(String address, boolean isTelecomServiceOn);    
    
    /**
	 * Callback to inform the change on "Roaming" status from HFP connected remote device.
	 *
	 * @param address Bluetooth MAC address of remote device which involves state changed.
     * @param isRoamingOn possible value are:
     *<p><value>=false means roaming is not active.
     *<p><value>=true means a roaming is active.  
     */
    void onHfpRemoteRoamingStatus(String address, boolean isRoamingOn); 
    
    /**
	 * Callback to inform the change on "Battery" indicator from HFP connected remote device.
	 *
	 * @param address Bluetooth MAC address of remote device.
     * @param currentValue current battery value
     * @param maxValue maximum battery value 
     * @param minValue minimum battery value
     */
    void onHfpRemoteBatteryIndicator(String address, int currentValue, int maxValue, int minValue);    
        
    /**
	 * Callback to inform the change on "Signal" strength from HFP connected remote device.
	 *
	 * @param address Bluetooth MAC address of remote device.
     * @param currentStrength current signal strength
     * @param maxStrength maximum signal strength  
     * @param minStrength minimum signal strength
     */
    void onHfpRemoteSignalStrength(String address, int currentStrength, int maxStrength, int minStrength);

    /**
     * Callback to update active call number or/and hold number if there are any changes no matter it is multi-call or single-call.
     *
     * @param address Bluetooth MAC address of remote device which involves state changed.
     * @param call changed call
     */
    void onHfpCallChanged(String address, in NfHfpClientCall call);   

    // Customize
    /**
	 * Callback to inform response to {@link INfCommandPbap#reqPbapDatabaseQueryNameByNumber reqPbapDatabaseQueryNameByNumber}
	 * for remote connected device with given Bluetooth hardware address.
	 *
     * @param address Bluetooth MAC address of remote device.
     * @param target the queried phone number.     
     * @param name the corresponding name of specified phone number. This name is meaningful only if isSuccessed is true.
     * @param isSuccess indicates that if the corresponding name is retrieved. 
     */
	void retPbapDatabaseQueryNameByNumber(String address, String target, String name, boolean isSuccess);       
    
}
