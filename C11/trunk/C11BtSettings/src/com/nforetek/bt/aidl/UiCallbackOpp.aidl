/** 
 * nForeTek OPP Callbacks Interface for Android 4.3
 *
 * Copyright (C) 2011-2019  nForeTek Corporation
 *
 * Zeus 0.0.0 on 20140606
 * @author KC Huang	<kchuang@nforetek.com>
 * @author Piggy	<piggylee@nforetek.com>
 * @version 0.0.0
 *
 */
 
package com.nforetek.bt.aidl;

/**
 * The callback interface for Objext Push Profile (OPP).
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
 * @see INfCommandOpp
 */

interface UiCallbackOpp {

	/** 
	 * Callback to inform Opp Service is ready.
	 */
	void onOppServiceReady();

	/** 
	 * Callback to inform state change of OPP connected remote device.
	 * <p>The possible values of state in this callback are 
	 * 	<blockquote><b>STATE_NOT_INITIALIZED</b>		(int) 100
	 * 	<br><b>STATE_READY</b>							(int) 110
	 *		<br><b>STATE_CONNECTED</b>						(int) 140
 	 *		<br><b>STATE_UPLOADING</b>						(int) 170</blockquote>
	 *
	 * <br>Parameter address is not valid for <b>STATE_NOT_INITIALIZED</b> and <b>STATE_READY</b>.
	 * <br>It might contain unavailable content or <b>DEFAULT_ADDRESS</b> , which is 00:00:00:00:00:00.
	 *
	 * @param address Bluetooth MAC address of remote device which involves state changed.
	 * @param prevState the previous state. 
	 * @param currentState the current state.
	 * @param reason: the reason of state changed. Possible values are
	 *		<br>REASON_OPP_SUCCESS											(int) 0
	 * 	<br>REASON_OPP_DOWNLOAD_REJECT					(int) 1
	 * 	<br>REASON_OPP_DOWNLOAD_INTERRUPTED					(int) 2
	 * 	<br>REASON_OPP_DOWNLOAD_FILEPATH_ERROR				(int) 3	
	 *
	 */	
	void onOppStateChanged(String address, int preState, int currentState, int reason);
	
	/** 
	 * Callback to inform receive file information from remote device.
	 *
  	 * @param fileName the received file name
  	 * @param  fileSize the received file size
  	 * @param  deviceName the remote device name
  	 * @param  savePath the file storage path
	 */		
	void onOppReceiveFileInfo(String fileName, int fileSize, String deviceName, String savePath);

	/** 
	 * Callback to inform the receive progress.
	 *
  	 * @param receivedOffset the received file offset. 	 
	 */		
	void onOppReceiveProgress(int receivedOffset);

}
