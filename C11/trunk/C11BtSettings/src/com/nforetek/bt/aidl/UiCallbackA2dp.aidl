/** 
 * nForeTek A2DP Callbacks Interface for Android 4.3
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
 * The callback interface for Advanced Audio Distribution Profile (A2DP).
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
 * @see INfCommandA2dp
 */
 

interface UiCallbackA2dp {

	/** 
	 * Callback to inform A2DP Service is ready.
	 */
	void onA2dpServiceReady();
	
	/** 
	 * Callback to inform state change of A2DP connected remote device.
	 * <p>The possible values of state in this profile are: 
	 * 		<p><blockquote><b>STATE_NOT_INITIALIZED</b>	(int) 100
	 * 		<br><b>STATE_READY</b>				(int) 110
	 * 		<br><b>STATE_CONNECTING</b>			(int) 120
	 * 		<br><b>STATE_DISCONNECTING</b>		(int) 125
	 *		<br><b>STATE_CONNECTED</b>			(int) 140
	 *		<br><b>STATE_STREAMING</b>			(int) 150</blockquote>
	 * <br>The state <b>STATE_STREAMING</b> implies connected and playing.
	 * <br>Parameter address is not valid for <b>STATE_NOT_INITIALIZED</b> and <b>STATE_READY</b>.
	 * <br>It might contain unavailable content or <b>DEFAULT_ADDRESS</b> , which is 00:00:00:00:00:00.
	 *
	 * @param address Bluetooth MAC address of remote device which involves state changed.
	 * @param prevState the previous state. 
	 * @param newState the new state.
	 */
	void onA2dpStateChanged(String address, int prevState, int newState);

}