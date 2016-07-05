/** 
 * nForeTek Settings Commands Interface for Android 4.3
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

import com.nforetek.bt.aidl.UiCallbackA2dp;
import com.nforetek.bt.aidl.UiCallbackAvrcp;
import com.nforetek.bt.aidl.UiCallbackBluetooth;
import com.nforetek.bt.aidl.UiCallbackHfp;
import com.nforetek.bt.aidl.UiCallbackPbap;
import com.nforetek.bt.aidl.UiCallbackSpp;
import com.nforetek.bt.aidl.UiCallbackHid;
import com.nforetek.bt.aidl.UiCallbackMap;
import com.nforetek.bt.aidl.UiCallbackOpp;
import com.nforetek.bt.aidl.UiCallbackGattServer;
import com.nforetek.bt.aidl.NfHfpClientCall;

/**
 * The API interface is for Bluetooth Settings Service.
 * <br>UI program may use these specific APIs to access nFore service.
 * <br>The naming principle of API in this doc is as follows,
 *		<blockquote><b>setXXX()</b> : 	set attributes to specific functions of nFore service.
 *		<br><b>reqXXX()</b> : 				request nFore service to implement specific function. It is an Asynchronized mode.
 *		<br><b>isXXX()</b> : 				check the current status from nFore service. It is a Synchronized mode.
 *		<br><b>getXXX()</b> : 				get the current result from nFore service. It is a Synchronized mode.</blockquote>
 *
 * <p>The constant variables in this Doc could be found and referred by importing
 * 		<br><blockquote>com.nforetek.bt.res.NfDef</blockquote>
 * <p>with prefix NfDef class name. Ex : <code>NfDef.DEFAULT_ADDRESS</code>
 *
 * <p>Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
 * 
 * @see UiCallbackBluetooth
 */
 
 interface UiCommand {

 	String getUiServiceVersionName();

 	/** 
	 * Check if AVRCP service is ready.
	 */
	boolean isAvrcpServiceReady();

	/** 
	 * Check if A2DP service is ready.
	 */
	boolean isA2dpServiceReady();

	/** 
	 * Check if SPP service is ready.
	 */
	boolean isSppServiceReady();

	/** 
	 * Check if Bluetooth service is ready.
	 */
	boolean isBluetoothServiceReady();

	/** 
	 * Check if HFP service is ready.
	 */
	boolean isHfpServiceReady();

	/** 
	 * Check if HID service is ready.
	 */
	boolean isHidServiceReady();

	/** 
	 * Check if PBAP service is ready.
	 */
	boolean isPbapServiceReady();

	/** 
	 * Check if Opp service is ready.
	 */
	boolean isOppServiceReady();

	/** 
	 * Check if MAP service is ready.
	 */
	boolean isMapServiceReady();

 	// A2DP

  	/** 
	 * Register callback functions for A2DP.
	 * <br>Call this function to register callback functions for A2DP.
	 * <br>Allow nFore service to call back to its registered clients, which is usually the UI application.
	 *
	 * @param cb callback interface instance.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 
	boolean registerA2dpCallback(UiCallbackA2dp cb);
	
	/** 
	 * Remove callback functions from A2DP.
     * <br>Call this function to remove previously registered callback interface for A2DP.
     * 
     * @param cb callback interface instance.
	 * @return true to indicate the operation is successful, or false erroneous.
     */ 
	boolean unregisterA2dpCallback(UiCallbackA2dp cb);

	/** 
	 * Get current connection state of the remote device.
	 *
	 * @return current state of A2DP profile service.
	 */ 
	int getA2dpConnectionState();

    /** 
	 * Check if local device is A2DP connected with remote device.
	 *
	 * @return true to indicate A2DP is connected, or false disconnected.
	 */
    boolean isA2dpConnected();

	/** 
	 * Get the Bluetooth hardware address of A2DP connected remote device.
	 *
	 * @return Bluetooth hardware address as string if there is a connected A2DP device, or 
	 * <code>DEFAULT_ADDRESS</code> 00:00:00:00:00:00.
	 */ 
	String getA2dpConnectedAddress();

	/** 
	 * Request to connect A2DP to the remote device.
	 * <br>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackA2dp#onA2dpStateChanged onA2dpStateChanged} to be notified of subsequent profile state changes.
	 * 
	 * <p>There is no guarantee that A2DP will be connected and the sequence of state changed callback of profiles! 
	 * <br>This depends on the behavior of connected device.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 	
	boolean reqA2dpConnect(String address);
	
	/** 
	 * Request to disconnect A2DP to the remote device.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackA2dp#onA2dpStateChanged onA2dpStateChanged} to be notified of subsequent profile state changes.
	 * 
	 * <p>There is no guarantee that A2DP will be disconnected and the sequence of state changed callback of profiles! 
	 * <br>This depends on the behavior of connected device.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 	
	boolean reqA2dpDisconnect(String address);

	/**
	 * Stop send A2DP stream data to audio track.
	 */
	void pauseA2dpRender();

	/**
	 * Start send A2DP stream data to audio track.
	 */
	void startA2dpRender();

	/** 
	 * Request to set the volume of A2DP streaming music locally.
	 * <br>This is an asynchronous call: it will return immediately.
	 *
	 * @param vol volumn level to set. The possible values are from 0.0f to 1.0f.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 	
	boolean setA2dpLocalVolume(float vol);


	// AVRCP

	/** 
	 * Register callback functions for AVRCP.
	 * <br>Call this function to register callback functions for AVRCP.
	 * <br>Allow nFore service to call back to its registered clients, which is usually the UI application.
	 *
	 * @param cb callback interface instance.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 
	boolean registerAvrcpCallback(UiCallbackAvrcp cb);
	
	/** 
	 * Remove callback functions from AVRCP.
     * <br>Call this function to remove previously registered callback interface for AVRCP.
     * 
     * @param cb callback interface instance.
	 * @return true to indicate the operation is successful, or false erroneous.
     */ 
	boolean unregisterAvrcpCallback(UiCallbackAvrcp cb);

	/** 
	 * Get current connection state of the remote device.
	 *
	 * @return current state of AVRCP profile service.
	 */ 
	int getAvrcpConnectionState();

    /** 
	 * Check if local device is AVRCP connected with remote device.
	 *
	 * @return true to indicate AVRCP is connected, or false disconnected.
	 */
    boolean isAvrcpConnected();

	/** 
	 * Get the Bluetooth hardware address of AVRCP connected remote device.
	 * 
	 */ 
	String getAvrcpConnectedAddress();

	/** 
	 * Request to connect AVRCP to the remote device.
	 * <br>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackAvrcp#onAvrcpStateChanged onAvrcpStateChanged} to be notified of subsequent profile state changes.
	 * 
	 * @param address valid Bluetooth MAC address.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 	
	boolean reqAvrcpConnect(String address);
	
	/** 
	 * Request to disconnect AVRCP to the remote device.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackAvrcp#onAvrcpStateChanged onAvrcpStateChanged} to be notified of subsequent profile state changes.
	 * 	 
	 * @param address valid Bluetooth MAC address.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 	
	boolean reqAvrcpDisconnect(String address);
	
	/** 
	 * Request if AVRCP 1.3 is supported by remote device.
	 *
	 * <p>The requested address must be the paired device and is connected currently.	 
	 *
	 * @param address valid Bluetooth MAC address of paired and connected AVRCP device.
	 * @return false if the device dose not support version 1.3 or is not connected currently.  
	 */
	boolean isAvrcp13Supported(String address);
	
	/** 
	 * Request if AVRCP 1.4 is supported by remote device.
	 *
	 * <p>The requested address must be the paired device and is connected currently.	 
	 *
	 * @param address valid Bluetooth MAC address of paired and connected AVRCP device.
	 * @return false if the device dose not support version 1.4 or is not connected currently.  
	 */
	boolean isAvrcp14Supported(String address);

	/* ==================================================================================================================================== 
	 * AVRCP v1.0
	 */	
	
	/** 
	 * Request A2DP/AVRCP connected remote device to do the "Play" operation.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link UiCallbackA2dp#onA2dpStateChanged onA2dpStateChanged} 
	 * to be notified of subsequent profile state changes.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 	
	boolean reqAvrcpPlay();
	
	/** 
	 * Request A2DP/AVRCP connected remote device to do the "Stop" operation.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link UiCallbackA2dp#onA2dpStateChanged onA2dpStateChanged} 
	 * to be notified of subsequent profile state changes.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 	
	boolean reqAvrcpStop();
	
	/** 
	 * Request A2DP/AVRCP connected remote device to do the "Pause" operation.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link UiCallbackA2dp#onA2dpStateChanged onA2dpStateChanged} 
	 * to be notified of subsequent profile state changes.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 	
	boolean reqAvrcpPause();
	
	/** 
	 * Request A2DP/AVRCP connected remote device to do the "Forward" operation.
	 * <p>This is an asynchronous call: it will return immediately.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 	
	boolean reqAvrcpForward();
	
	/** 
	 * Request A2DP/AVRCP connected remote device to do the "Backward" operation.
	 * <p>This is an asynchronous call: it will return immediately.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 	
	boolean reqAvrcpBackward();
	
	/** 
	 * Request A2DP/AVRCP connected remote device to do the "Volume Up" operation.
	 * <p>This is an asynchronous call: it will return immediately.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 	
	boolean reqAvrcpVolumeUp();
	
	/** 
	 * Request A2DP/AVRCP connected remote device to do the "Volume Down" operation.
	 * <p>This is an asynchronous call: it will return immediately.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */	
	boolean reqAvrcpVolumeDown();

	/** 
	 * Request A2DP/AVRCP connected remote device to start the "Fast Forward" operation.
	 * <p>This is an asynchronous call: it will return immediately.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */		
	boolean reqAvrcpStartFastForward();

	/** 
	 * Request A2DP/AVRCP connected remote device to stop the "Fast Forward" operation.
	 * <p>This is an asynchronous call: it will return immediately.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */		
	boolean reqAvrcpStopFastForward();

	/** 
	 * Request A2DP/AVRCP connected remote device to start the "Rewind" operation.
	 * <p>This is an asynchronous call: it will return immediately.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */		
	boolean reqAvrcpStartRewind();

	/** 
	 * Request A2DP/AVRCP connected remote device to stop the "Rewind" operation.
	 * <p>This is an asynchronous call: it will return immediately.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */		
	boolean reqAvrcpStopRewind();
	
	
	/* ==================================================================================================================================== 
	 * AVRCP v1.3
	 */		
	 
	/** 
	 * Request to get the supported events of capabilities from A2DP/AVRCP connected remote device. 
	 * This is sent by CT to inquire capabilities of the peer device.
	 *
	 * <p>This requests the list of events supported by the remote device. Remote device is expected to respond with all the events supported 
	 * including the mandatory events defined in the AVRCP v1.3 specification.	 
	 *
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackAvrcp#retAvrcp13CapabilitiesSupportEvent retAvrcp13CapabilitiesSupportEvent} and 
	 * {@link UiCallbackAvrcp#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */	 
	boolean reqAvrcp13GetCapabilitiesSupportEvent();	 
	 
	/** 
	 * Request to get the supported player application setting attributes from A2DP/AVRCP connected remote device. 
	 *
	 * <p>The list of reserved player application setting attributes is provided in Appendix F of AVRCP v1.3 specification. 
	 * <br>It is expected that a target device may have additional attributes not defined as part of the specification.
	 *
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackAvrcp#retAvrcp13PlayerSettingAttributesList retAvrcp13PlayerSettingAttributesList} and 
	 * {@link UiCallbackAvrcp#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */		  
	boolean reqAvrcp13GetPlayerSettingAttributesList();
	 
	/** 
	 * Request to get the set of possible values for the requested player application setting attribute 
	 * from A2DP/AVRCP connected remote device. 
	 *
	 * <p>The list of reserved player application setting attributes and their values are provided in Appendix F of AVRCP v1.3 specification. 
	 * <br>It is expected that a target device may have additional attribute values not defined as part of the specification.
	 *
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackAvrcp#retAvrcp13PlayerSettingValuesList retAvrcp13PlayerSettingValuesList} and 
	 * {@link UiCallbackAvrcp#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param attributeId the requesting attribute ID. Possible values are:
	 * 		<blockquote><b>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER</b>	(byte) 0x01
	 *		<br><b>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE</b>			(byte) 0x02
	 *		<br><b>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE</b>				(byte) 0x03
	 *		<br><b>AVRCP_SETTING_ATTRIBUTE_ID_SCAN</b>					(byte) 0x04</blockquote>
	 * @return true to indicate the operation is successful, or false erroneous.
	 */		 
	boolean reqAvrcp13GetPlayerSettingValuesList(byte attributeId);
	 
	/** 
	 * Request to get the current set values from A2DP/AVRCP connected remote device
	 * for the provided player application setting attribute. 
	 * 
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackAvrcp#retAvrcp13PlayerSettingCurrentValues retAvrcp13PlayerSettingCurrentValues} and 
	 * {@link UiCallbackAvrcp#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */		 
	boolean reqAvrcp13GetPlayerSettingCurrentValues();
	 
	/** 
	 * Request to set the player application setting of player application setting value on A2DP/AVRCP connected remote device 
	 * for the corresponding defined PlayerApplicationSettingAttribute. 
	 * 
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackAvrcp#retAvrcp13SetPlayerSettingValueSuccess retAvrcp13SetPlayerSettingValueSuccess} and 
	 * {@link UiCallbackAvrcp#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param attributeId the requesting attribute ID. Possible values are:
	 * 		<blockquote><b>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER</b>	(byte) 0x01
	 *		<br><b>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE</b>			(byte) 0x02
	 *		<br><b>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE</b>				(byte) 0x03
	 *		<br><b>AVRCP_SETTING_ATTRIBUTE_ID_SCAN</b>					(byte) 0x04</blockquote>
	 * @param valueId the setting value. Possible values are:
	 * 		<blockquote>For <b>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER</b></blockquote>
	 *			<blockquote><blockquote><b>AVRCP_SETTING_VALUE_ID_EQUALIZER_OFF</b>	(byte) 0x01
	 *			<br><b>AVRCP_SETTING_VALUE_ID_EQUALIZER_ON</b>						(byte) 0x02</blockquote></blockquote>
	 *		<blockquote>For <b>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE</b></blockquote>
	 *			<blockquote><blockquote><b>AVRCP_SETTING_VALUE_ID_REPEAT_OFF</b>	(byte) 0x01
	 *			<br><b>AVRCP_SETTING_VALUE_ID_REPEAT_SINGLE</b>						(byte) 0x02
	 *			<br><b>AVRCP_SETTING_VALUE_ID_REPEAT_ALL</b>						(byte) 0x03
	 *			<br><b>AVRCP_SETTING_VALUE_ID_REPEAT_GROUP</b>						(byte) 0x04</blockquote></blockquote>
	 *		<blockquote>For <b>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE</b></blockquote>
	 *			<blockquote><blockquote><b>AVRCP_SETTING_VALUE_ID_SHUFFLE_OFF</b>	(byte) 0x01
	 *			<br><b>AVRCP_SETTING_VALUE_ID_SHUFFLE_ALL</b>						(byte) 0x02
	 *			<br><b>AVRCP_SETTING_VALUE_ID_SHUFFLE_GROUP</b>						(byte) 0x03</blockquote></blockquote>
	 *		<blockquote>For <b>AVRCP_SETTING_ATTRIBUTE_ID_SCAN</b></blockquote>
	 *			<blockquote><blockquote><b>AVRCP_SETTING_VALUE_ID_SCAN_OFF</b>		(byte) 0x01
	 *			<br><b>AVRCP_SETTING_VALUE_ID_SCAN_ALL</b>							(byte) 0x02
	 *			<br><b>AVRCP_SETTING_VALUE_ID_SCAN_GROUP</b>						(byte) 0x03</blockquote></blockquote>
	 * @return true to indicate the operation is successful, or false erroneous.
	 */		 
	boolean reqAvrcp13SetPlayerSettingValue(byte attributeId, byte valueId);
	 
	/** 
	 * Request to get the attributes of the element specified in the parameter 
	 * from A2DP/AVRCP connected remote device
	 * 
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackAvrcp#retAvrcp13ElementAttributesPlaying retAvrcp13ElementAttributesPlaying} and
	 * {@link UiCallbackAvrcp#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 * 
	 * @return true to indicate the operation is successful, or false erroneous.
	 */		 
	boolean reqAvrcp13GetElementAttributesPlaying();
	 
	/** 
	 * Request to get the status of the currently playing media  
	 * from A2DP/AVRCP connected remote device
	 *
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackAvrcp#retAvrcp13PlayStatus retAvrcp13PlayStatus} and
	 * {@link UiCallbackAvrcp#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *	 
	 * @return true to indicate the operation is successful, or false erroneous.
	 */		 
	boolean reqAvrcp13GetPlayStatus();
	 
	/** 
	 * Request to register with A2DP/AVRCP connected remote device
	 * to receive notifications asynchronously based on specific events occurring. 
	 * 
	 * <p>The events registered would be kept on remote device until another
	 * {@link UiCommandAvrcp#reqAvrcpUnregisterEventWatcher reqAvrcpUnregisterEventWatcher} is called.
	 * 
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * <blockquote>{@link UiCallbackAvrcp#onAvrcp13EventPlaybackStatusChanged onAvrcp13EventPlaybackStatusChanged},
	 * <br>{@link UiCallbackAvrcp#onAvrcp13EventTrackChanged onAvrcp13EventTrackChanged},
	 * <br>{@link UiCallbackAvrcp#onAvrcp13EventTrackReachedEnd onAvrcp13EventTrackReachedEnd},
	 * <br>{@link UiCallbackAvrcp#onAvrcp13EventTrackReachedStart onAvrcp13EventTrackReachedStart},
	 * <br>{@link UiCallbackAvrcp#onAvrcp13EventPlaybackPosChanged onAvrcp13EventPlaybackPosChanged},
 	 * <br>{@link UiCallbackAvrcp#onAvrcp13EventBatteryStatusChanged onAvrcp13EventBatteryStatusChanged},
 	 * <br>{@link UiCallbackAvrcp#onAvrcp13EventSystemStatusChanged onAvrcp13EventSystemStatusChanged},
 	 * <br>{@link UiCallbackAvrcp#onAvrcp13EventPlayerSettingChanged onAvrcp13EventPlayerSettingChanged},
 	 * <br>v1.4
 	 * <br>{@link UiCallbackAvrcp#onAvrcp14EventNowPlayingContentChanged onAvrcp14EventNowPlayingContentChanged},
 	 * <br>{@link UiCallbackAvrcp#onAvrcp14EventAvailablePlayerChanged onAvrcp14EventAvailablePlayerChanged},
 	 * <br>{@link UiCallbackAvrcp#onAvrcp14EventAddressedPlayerChanged onAvrcp14EventAddressedPlayerChanged},
 	 * <br>{@link UiCallbackAvrcp#onAvrcp14EventUidsChanged onAvrcp14EventUidsChanged},
 	 * <br>{@link UiCallbackAvrcp#onAvrcp14EventVolumeChanged onAvrcp14EventVolumeChanged}, and
	 * <br>{@link UiCallbackAvrcp#onAvrcpErrorResponse onAvrcpErrorResponse} </blockquote>
	 * to be notified of subsequent result.
	 * <br>Each corresponding callback would be invoked once immediately after the event has been registered successfully. 
	 *
	 *  
	 * @param eventId the registering event ID. Possible values are:
	 *		<blockquote><b>AVRCP_EVENT_ID_PLAYBACK_STATUS_CHANGED</b>		(byte) 0x01
	 *		<br><b>AVRCP_EVENT_ID_TRACK_CHANGED</b>							(byte) 0x02
	 *		<br><b>AVRCP_EVENT_ID_TRACK_REACHED_END</b>						(byte) 0x03
	 *		<br><b>AVRCP_EVENT_ID_TRACK_REACHED_START</b>					(byte) 0x04
	 *		<br><b>AVRCP_EVENT_ID_PLAYBACK_POS_CHANGED</b>					(byte) 0x05
	 *		<br><b>AVRCP_EVENT_ID_BATT_STATUS_CHANGED</b>					(byte) 0x06
	 *		<br><b>AVRCP_EVENT_ID_SYSTEM_STATUS_CHANGED</b>					(byte) 0x07
	 *		<br><b>AVRCP_EVENT_ID_PLAYER_APPLICATION_SETTING_CHANGED</b>	(byte) 0x08
	 *		<br>v1.4
	 *		<br><b>AVRCP_EVENT_ID_NOW_PLAYING_CONTENT_CHANGED</b>			(byte) 0x09
	 *		<br><b>AVRCP_EVENT_ID_AVAILABLE_PLAYERS_CHANGED</b>				(byte) 0x0a
	 *		<br><b>AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED</b>				(byte) 0x0b
	 *		<br><b>AVRCP_EVENT_ID_UIDS_CHANGED</b>							(byte) 0x0c
	 *		<br><b>AVRCP_EVENT_ID_VOLUME_CHANGED</b>						(byte) 0x0d</blockquote>		
	 * @param interval the update interval in second. 
	 * <br>This parameter applicable only for <b>AVRCP_EVENT_ID_PLAYBACK_POS_CHANGED</b>. 
	 * For other events, this parameter is <b>ignored</b> !
	 * @return true to indicate the operation is successful, or false erroneous.
	 */		 
	boolean reqAvrcpRegisterEventWatcher(byte eventId, long interval);
	 
	/** 
	 * Request to unregister the specific events with A2DP/AVRCP connected remote device.
	 *
	 * <p>This is an asynchronous call: it will return immediately.
	 *
	 * @param eventId the unregistering event ID. Possible values are
	 *		<blockquote><b>AVRCP_EVENT_ID_PLAYBACK_STATUS_CHANGED</b>		(byte) 0x01
	 *		<br><b>AVRCP_EVENT_ID_TRACK_CHANGED</b>							(byte) 0x02
	 *		<br><b>AVRCP_EVENT_ID_TRACK_REACHED_END</b>						(byte) 0x03
	 *		<br><b>AVRCP_EVENT_ID_TRACK_REACHED_START</b>					(byte) 0x04
	 *		<br><b>AVRCP_EVENT_ID_PLAYBACK_POS_CHANGED</b>					(byte) 0x05
	 *		<br><b>AVRCP_EVENT_ID_BATT_STATUS_CHANGED</b>					(byte) 0x06
	 *		<br><b>AVRCP_EVENT_ID_SYSTEM_STATUS_CHANGED</b>					(byte) 0x07
	 *		<br><b>AVRCP_EVENT_ID_PLAYER_APPLICATION_SETTING_CHANGED</b>	(byte) 0x08
	 *		<br>v1.4	 
	 *		<br><b>AVRCP_EVENT_ID_NOW_PLAYING_CONTENT_CHANGED</b>			(byte) 0x09
	 *		<br><b>AVRCP_EVENT_ID_AVAILABLE_PLAYERS_CHANGED</b>				(byte) 0x0a
	 *		<br><b>AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED</b>				(byte) 0x0b
	 *		<br><b>AVRCP_EVENT_ID_UIDS_CHANGED</b>							(byte) 0x0c
	 *		<br><b>AVRCP_EVENT_ID_VOLUME_CHANGED</b>						(byte) 0x0d</blockquote>		
	 * @return true to indicate the operation is successful, or false erroneous.
	 */		 
	boolean reqAvrcpUnregisterEventWatcher(byte eventId);
	 	 
	/** 
	 * Request A2DP/AVRCP connected remote device to move to the first song in the next group.
	 * 
	 * <p>This is an asynchronous call: it will return immediately.
	 * 
	 * @return true to indicate the operation is successful, or false erroneous.
	 */
 	boolean reqAvrcp13NextGroup();
	 
	/** 
	 * Request A2DP/AVRCP connected remote device to move to the first song in the previous group.
	 *
	 * <p>This is an asynchronous call: it will return immediately.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 	 
	boolean reqAvrcp13PreviousGroup();
		
	 
	/* ==================================================================================================================================== 
	 * AVRCP v1.4
	 */


	/** 
	 * Request if A2DP/AVRCP connected remote device has browsing channel established. 
	 *
	 * @return true to indicate the remote device has browsing channel.
	 */			 	 	 
	boolean isAvrcp14BrowsingChannelEstablished();
	 
	/** 
	 * Request to Uiorm the A2DP/AVRCP connected remote device of which media player we wishes to control.
	 * <p>The player is selected by its "Player Id".
	 * <br>When the addressed player is changed, whether locally on the remote device or explicitly by us, 
	 * the remote device shall complete notifications following the mechanism described in section 6.9.2 of AVRCP v1.4 specification. 
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackAvrcp#retAvrcp14SetAddressedPlayerSuccess retAvrcp14SetAddressedPlayerSuccess} and
	 * {@link UiCallbackAvrcp#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param playerId the selected player ID of 2 octets. 
	 * @return true to indicate the operation is successful, or false erroneous.
	 */		 
	boolean reqAvrcp14SetAddressedPlayer(int playerId);
	
	/** 
	 * Request the A2DP/AVRCP connected remote device to route browsing commands to which player.
	 *
	 * <p>The player to which AVRCP shall route browsing commands is the browsed player. 
	 * <br>This command shall be sent successfully before any other commands are sent on the browsing channel except 
	 * {@link UiCommandAvrcp#reqAvrcp14GetFolderItems reqAvrcp14GetFolderItems}
	 * in the Media Player List scope. 
	 * <br>If the browsed player has become unavailable this command shall be sent successfully again before further commands are sent on the browsing channel. 
	 * <br>Some players may support browsing only when set as the Addressed Player.
	 * <p>The player is selected by its "Player Id".
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackAvrcp#retAvrcp14SetBrowsedPlayerSuccess retAvrcp14SetBrowsedPlayerSuccess} and
	 * {@link UiCallbackAvrcp#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param playerId the selected player ID of 2 octets. 
	 * @return true to indicate the operation is successful, or false erroneous.
	 */				
	boolean reqAvrcp14SetBrowsedPlayer(int playerId);
	
	/** 
	 * Request to retrieve a listing of the contents of a folder on A2DP/AVRCP connected remote device.
	 *
	 * <p>The folder is the representation of available media players, virtual file system, the last searching result, or the playing list.
	 * Should not issue this command to an empty folder.
	 *
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackAvrcp#retAvrcp14FolderItems retAvrcp14FolderItems} and
	 * {@link UiCallbackAvrcp#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param scopeId the requesting folder ID. Possible values are:
	 * 		<blockquote><b>AVRCP_SCOPE_ID_MEDIA_PLAYER</b>	(byte) 0x00
	 *		<br><b>AVRCP_SCOPE_ID_VFS</b>						(byte) 0x01
	 *		<br><b>AVRCP_SCOPE_ID_SEARCH</b>					(byte) 0x02
	 *		<br><b>AVRCP_SCOPE_ID_NOW_PLAYING</b>				(byte) 0x03</blockquote>
	 * @return true to indicate the operation is successful, or false erroneous.
	 */	
	boolean reqAvrcp14GetFolderItems(byte scopeId);
	
	/** 
	 * Request to navigate the virtual filesystem on A2DP/AVRCP connected remote device. 
	 * <p>This command allows us to navigate one level up or down in the virtual filesystem.
	 * <p>Uid counters parameter is used to make sure that our uid cache is consistent with what remote device has currently. 
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackAvrcp#retAvrcp14ChangePathSuccess retAvrcp14ChangePathSuccess} and
	 * {@link UiCallbackAvrcp#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param uidCounter the value of uid counter we have.
	 * @param uid The UID of the folder to navigate to. This may be retrieved via a 
	 * {@link UiCommandAvrcp#reqAvrcp14GetFolderItems reqAvrcp14GetFolderItems} command. 
	 * <br>If the navigation command is "Folder Up" this field is <b>reserved</b>.	 
	 * @param direction the requesting operation on selested UID. Possible values are:
	 * 		<blockquote><b>AVRCP_FOLDER_DIRECTION_ID_UP</b>		(byte) 0x00
	 *		<br><b>AVRCP_FOLDER_DIRECTION_ID_DOWN</b>			(byte) 0x01</blockquote>
	 * @return true to indicate the operation is successful, or false erroneous.
	 */		
	boolean reqAvrcp14ChangePath(int uidCounter, long uid, byte direction);
	
	/** 
	 * Request to retrieve the metadata attributes for a particular media element item or folder item 
	 * on A2DP/AVRCP connected remote device. 
	 * <p>When the remote device supports this command, we shall use this command and not {@link #reqAvrcp13GetElementAttributesPlaying reqAvrcp13GetElementAttributesPlaying}. 
	 * <br>To retrieve the Metadata for the currently playing track we should register to receive Track Changed Notifications. 
	 * <br>This shall then provide the UID of the currently playing track, which can be used in the scope of the Now Playing folder.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackAvrcp#retAvrcp14ItemAttributes retAvrcp14ItemAttributes} and
	 * {@link UiCallbackAvrcp#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param scopeId the requesting folder ID. Possible values are
	 *		<blockquote><b>AVRCP_SCOPE_ID_VFS</b>		(byte) 0x01
	 *		<br><b>AVRCP_SCOPE_ID_SEARCH</b>			(byte) 0x02
	 *		<br><b>AVRCP_SCOPE_ID_NOW_PLAYING</b>		(byte) 0x03</blockquote>
	 * @param uidCounter the value of uid counter we have.
	 * @param uid The UID of the media element item or folder item to return the attributes for. UID 0 shall not be used.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */			
	boolean reqAvrcp14GetItemAttributes(byte scopeId, int uidCounter, long uid);
				
	/** 
	 * Request A2DP/AVRCP connected remote device to starts playing an item indicated by the UID.
	 * It is routed to the Addressed Player. 
	 * <p>If a UID changed event has happened but not received by local yet, the previous UID counter may be sent. 
	 * In this case a failure status shall be returned.
	 * <p>Request this command with the scope parameter of 
	 *		<blockquote><b>AVRCP_SCOPE_ID_VFS</b>			(byte) 0x01 or 
	 * 		<br><b>AVRCP_SCOPE_ID_SEARCH</b>				(byte) 0x02</blockquote>
	 * shall result in the NowPlaying folder being invalidated. 
	 * <br>The old content may not be valid any more or may contain additional items. 
	 * <p>What is put in the NowPlaying folder depends on both the media player and its state, however the item selected by us shall be included.
	 * <p>Request this command with the scope parameter of 
	 * 		<blockquote><b>AVRCP_SCOPE_ID_NOW_PLAYING</b>	(byte) 0x03</blockquote>
	 * should not change the contents of the NowPlaying Folder, the only effect is that the new item is played.
	 * <p>Never request this command with the scope parameter 
	 * 		<blockquote><b>AVRCP_SCOPE_ID_MEDIA_PLAYER</b>	(byte) 0x00.</blockquote>
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackAvrcp#retAvrcp14PlaySelectedItemSuccess retAvrcp14PlaySelectedItemSuccess} and
	 * {@link UiCallbackAvrcp#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param scopeId The scope in which the UID of the media element item or folder item, if supported, is valid. Possible values are
	 *		<blockquote><b>AVRCP_SCOPE_ID_VFS</b>		(byte) 0x01
	 *		<br><b>AVRCP_SCOPE_ID_SEARCH</b>			(byte) 0x02
	 *		<br><b>AVRCP_SCOPE_ID_NOW_PLAYING</b>		(byte) 0x03</blockquote>
	 * @param uidCounter the value of uid counter we have.
	 * @param uid The UID of the media element item or folder item, if supported, to be played.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */		
	boolean reqAvrcp14PlaySelectedItem(byte scopeId, int uidCounter,long uid);
	
	/** 
	 * Request to perform the basic search from the current folder and all folders below the Browsed Players virtual filesystem. 
	 * Regular expressions shall not be supported. 
	 * <br>Search results are valid until
	 * 		<blockquote>Another search request is performed or
	 *		<br>A UIDs changed notification response is received
	 * 		<br>The Browsed player is changed</blockquote>
	 * <p>The search result would contain only media element items.
	 * <br>Searching may not be supported by all players. Furthermore, searching may only be possible on some players 
	 * when they are set as the Addressed Player.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackAvrcp#retAvrcp14SearchResult retAvrcp14SearchResult} and
	 * {@link UiCallbackAvrcp#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param text The string to search on in the specified character set.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */		
	boolean reqAvrcp14Search(in String text);
	
	/** 
	 * Request A2DP/AVRCP connected remote device to add an item indicated by the UID to the NowPlaying queue. 
	 * <p>If a UID changed event has happened but not received by local yet, the previous UID counter may be sent. 
	 * In this case a failure status shall be returned.
	 * <p>Request this command with the scope parameter of 
	 *		<blockquote><b>AVRCP_SCOPE_ID_VFS</b>			(byte) 0x01
	 * 		<br><b>AVRCP_SCOPE_ID_SEARCH</b>				(byte) 0x02 or
	 * 		<br><b>AVRCP_SCOPE_ID_NOW_PLAYING</b>			(byte) 0x03</blockquote>
	 * shall result in the item being added to the NowPlaying folder on media players that support this command.
	 * <p>Never request this command with the scope parameter 
	 * 		<blockquote><b>AVRCP_SCOPE_ID_MEDIA_PLAYER</b>	(byte) 0x00.</blockquote>
	 * This command could be requested with the UID of a Folder Item if the folder is playable. 
	 * <p>The media items of that folder are added to the NowPlaying list, not the folder itself.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackAvrcp#retAvrcp14AddToNowPlayingSuccess retAvrcp14AddToNowPlayingSuccess} and
	 * {@link UiCallbackAvrcp#onAvrcpErrorResponse onAvrcpErrorResponse}
	 * to be notified of subsequent result.
	 *
	 * @param scopeId The scope in which the UID of the media element item or folder item, if supported, is valid. Possible values are
	 *		<blockquote><b>AVRCP_SCOPE_ID_VFS</b>		(byte) 0x01
	 *		<br><b>AVRCP_SCOPE_ID_SEARCH</b>			(byte) 0x02
	 *		<br><b>AVRCP_SCOPE_ID_NOW_PLAYING</b>		(byte) 0x03</blockquote>
	 * @param uidCounter the value of uid counter we have.
	 * @param uid The UID of the media element item or folder item, if supported, to be played.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */		
	boolean reqAvrcp14AddToNowPlaying(byte scopeId, int uidCounter, long uid);	
	
	/** 
	 * By the AVRCP v1.4 specification, this command is used to set an absolute volume to be used by the rendering device. 
	 * This is in addition to the relative volume commands. 
	 * <p>It is expected that the audio sink will perform as the TG role for this command.
	 * <br>As this command specifies a percentage rather than an absolute dB level, the CT should exercise caution when sending this command.
	 * <p>It should be noted that this command is intended to alter the rendering volume on the audio sink. 
	 * <br>It is not intended to alter the volume within the audio stream. The volume level which has actually been set on the TG is returned in the response. 
	 * This is to enable the CT to deal with whatever granularity of volume control the TG provides.
	 * <p>The setting volume is represented in one octet. The top bit (bit 7) is reserved for future definition. 
	 * <br>The volume is specified as a percentage of the maximum. The value 0x0 corresponds to 0%. The value 0x7F corresponds to 100%.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackAvrcp#retAvrcp14SetAbsoluteVolumeSuccess retAvrcp14SetAbsoluteVolumeSuccess} and 
	 * {@link UiCallbackAvrcp#onAvrcpErrorResponse onAvrcpErrorResponse} 
	 * to be notified of subsequent result.
	 *
	 * @param volume the setting volume value of octet from 0x00 to 0x7F.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */		
	boolean reqAvrcp14SetAbsoluteVolume(byte volume);


	// Bluetooth

 	/** 
	 * Register callback functions for basic Settings functions.
	 * <br>Call this function to register callback functions for nFore service.
	 * <br>Allow nFore service to call back to its registered clients, which is usually the UI application.
	 *
	 * @param cb callback interface instance.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 
	boolean registerBtCallback(UiCallbackBluetooth cb);
	
	/** 
	 * Remove callback functions from nFore service.
     * <br>Call this function to remove previously registered callback interface for nFore Settings service.
     * 
     * @param cb callback interface instance.
	 * @return true to indicate the operation is successful, or false erroneous.
     */ 
	boolean unregisterBtCallback(UiCallbackBluetooth cb);

	/** 
	 * Get nFore bluetooth service version name.
	 *
	 * @return nFore Service version name.
	 */
	String getNfServiceVersionName();

	/** 
	 * Set local Bluetooth adapter to enable or disable .
	 * <br>This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link UiCallbackBluetooth#onAdapterStateChanged onAdapterStateChanged}
	 * to be notified of subsequent adapter state changes.
	 *
	 * @param enable true to enable Bluetooth adapter or false to disable.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */
	boolean setBtEnable(boolean enable);

	/** 
	 * Set local Bluetooth adapter discoverable for specific duration in seconds.
	 * <br>The system default duration for discoverable might differentiate from each other in different platforms.
	 * However, it is 120 seconds in default.
	 * <br>If the duration is negative value, discoverable would be disabled.	 
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link UiCallbackBluetooth#onAdapterDiscoverableModeChanged onAdapterDiscoverableModeChanged}
	 * to be notified of subsequent adapter state changes.
	 * The outcomes of this setting will be:
	 * <blockquote><p><b>enabled</b> with timeout, 
	 * <br><b>disabled</b> timeout = -1
	 * <br><b>DEFAULT_DISCOVERABLE_TIMEOUT</b> timeout = null</blockquote>
	 * The <b>DEFAULT_DISCOVERABLE_TIMEOUT</b> is 120s and the maximum timeout is 300s.
	 *
	 * @param timeout the duration of discoverable in seconds
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 	
	boolean setBtDiscoverableTimeout(int timeout);
	
	/** 
	 * Start scan process for remote devices.
	 * <br>Client should not request to start scanning twice or more in 12 seconds.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackBluetooth#onAdapterDiscoveryStarted onAdapterDiscoveryStarted}, {@link UiCallbackBluetooth#onDeviceFound onDeviceFound}
	 * and {@link UiCallbackBluetooth#onAdapterDiscoveryFinished onAdapterDiscoveryFinished}
	 * to be notified of subsequent adapter state changes.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 	
	boolean startBtDiscovery();

	/** 
	 * Stop scanning process for remote devices.
	 * <br>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackBluetooth#onAdapterDiscoveryFinished onAdapterDiscoveryFinished}
	 * to be notified of subsequent adapter state changes.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 	
	boolean cancelBtDiscovery();

	/** 
	 * Request to pair with given Bluetooth hardware address.
	 * <br>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link UiCallbackBluetooth#onDeviceBondStateChanged onDeviceBondStateChanged} and {@link UiCallbackBluetooth#onDeviceUuidsUpdated onDeviceUuidsUpdated}
	 * to be notified if pairing is successful.
	 *
	 * @attention The Android system only supports 7 paired devices maximal. System would delete the first paired device automatically when
	 * the limit is reached. 
	 * @param address valid Bluetooth MAC address.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 		
	boolean reqBtPair(String address);

	/** 
	 * Request to unpair with given Bluetooth hardware address.
	 * <br>This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link UiCallbackBluetooth#onDeviceBondStateChanged onDeviceBondStateChanged}
	 * to be notified if unpairing is successful.
	 * However, this operation only removes pairing record locally. Remote device would not be notified until pairing again.
	 * All connected or connecting profiles should be terminated before unpairing.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 		
	boolean reqBtUnpair(String address);	
	
	/** 
	 * Request to get the paired device list.
	 * <br>This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link UiCallbackBluetooth#retPairedDevices retPairedDevices}
	 * to be notified of subsequent result.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 		
	boolean reqBtPairedDevices();

	/** 
	 * Get the name of local Bluetooth adapter.
	 * <br>If there is an error, the string "UNKNOWN" would be returned.
	 *
	 * @return the String type name of local Bluetooth adapter.
	 */ 		
	String getBtLocalName();
	
	/** 
	 * Request the name of remote Bluetooth device with given address.
	 * <br>This method would return the name announced by remote device in String type only if this remote device
	 * has been scanned before. Otherwise the empty string would be returned.
	 *	 	 
	 * @param address valid Bluetooth MAC address.	 
	 * @return the real String type name of remote device or the empty string.
	 */ 		
	String getBtRemoteDeviceName(String address);

	/** 
	 * Get the address of local Bluetooth adapter.
	 *
	 * @return the String type address of local Bluetooth adapter.
	 */
	String getBtLocalAddress();

	/** 
	 * Set the name of local Bluetooth adapter.
	 *
	 * @param name the name to set.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 		
	boolean setBtLocalName(String name);

	/**
	 * Check if Bluetooth is currently enabled.
	 *
	 * @return true if the local adapter is turned on.
	 */
	boolean isBtEnabled();
	
	/** 
	 * Get the current state of local Bluetooth adapter.
	 *
	 * @return int value to denote the current state. Possible values are:
	 *		<p><blockquote><b>ERROR</b>			(int) -1
	 *		<br><b>BT_STATE_OFF</b>				(int) 300
	 *		<br><b>BT_STATE_TURNING_ON</b>		(int) 301
	 *		<br><b>BT_STATE_ON</b>				(int) 302
	 *		<br><b>BT_STATE_TURNING_OFF</b>		(int) 303</blockquote>	
	 */ 		
	int getBtState();

	/** 
	 * Check if Bluetooth is currently scanning remote devices.
	 *
	 * @return true if scanning.
	 */
	boolean isBtDiscovering();

	/** 
	 * Check if Bluetooth is currently discoverable from remote devices.
	 *
	 * @return true if discoverable.
	 */
	boolean isBtDiscoverable();
	
	/** 
	 * Set auto connect mode as enabled or disabled.
	 * <br>Auto-connect only applies to HSP, HFP, A2DP and AVRCP services when out of range and ACC on/off events take place.
	 * Besides, only one of HSP or HFP would be re-connected, and HFP is preferred to HSP.
	 * <br>Auto-connect only applies to the following conditions: 
	 *		<blockquote><p>there is an out of range event, or 	
	 *		<br>there is a connected phone before ACC off and then ACC on </blockquote>
	 *
	 * @param enable true to enable or false to disable.
	 */
	void setBtAutoConnectMode(boolean enable);
		
	/** 
	 * Check if auto-connect is currently enabled.
	 *
	 * @return true if auto-connect is enabled.
	 */
	boolean isBtAutoConnectEnable();
	
	/** 
	 * Request to connect HSP, HFP, A2DP, and AVRCP to remote device.
	 * <br>If remote device supports both HSP and HFP, nFore service connects HFP instead of HSP.
	 * <br>This is an asynchronous call: it will return immediately with int returned value, 
	 * which denotes the profiles nFore service plans to connect to, and 
	 * clients should register and implement callback functions
	 * {@link UiCallbackHsp#onHspStateChanged onHspStateChanged}, 
	 * {@link UiCallbackHfp#onHfpStateChanged onHfpStateChanged}, 
	 * {@link UiCallbackA2dp#onA2dpStateChanged onA2dpStateChanged}, and
	 * {@link UiCallbackAvrcp#onAvrcpStateChanged onAvrcpStateChanged}  
	 * to be notified of subsequent profile state changes.
	 * 
	 *
	 * @param address valid Bluetooth MAC address.
	 * @return int value to denote the profiles planned to connect. Possible values are:
	 *		<blockquote><b>ERROR</b>	(int) -1
	 *		<br><b>PROFILE_HSP</b>		(int) 1
	 *		<br><b>PROFILE_HFP</b>		(int) (1 << 1)
	 *		<br><b>PROFILE_A2DP</b>		(int) (1 << 2)</blockquote>	 
	 * For example, value 6 (0000 0110) means that HFP and A2DP would be connected.	 	 
	 */ 	
	int reqBtConnectHfpA2dp(String address);

	/** 
	 * Request to disconnect all connected profiles.
	 * <br>This is an asynchronous call: it will return immediately with int returned value, 
	 * which denotes the profiles nFore service plans to disconnect to, and 
	 * clients should register and implement callback functions
	 * {@link UiCallbackHsp#onHspStateChanged onHspStateChanged}, 
	 * {@link UiCallbackHfp#onHfpStateChanged onHfpStateChanged}, 
	 * {@link UiCallbackA2dp#onA2dpStateChanged onA2dpStateChanged}, and
	 * {@link UiCallbackAvrcp#onAvrcpStateChanged onAvrcpStateChanged}  
	 * to be notified of subsequent profile state changes.
	 * 
	 * <br>If there is no connection currently, this request would return ERROR.
	 *
	 * @return int value to denote the profiles planned to disconnect. Possible values are:
	 *		<p><blockquote><b>ERROR</b>		(int) -1
	 *		<br><b>PROFILE_HSP</b>			(int) 1
	 *		<br><b>PROFILE_HFP</b>			(int) (1 << 1)
	 *		<br><b>PROFILE_A2DP</b>			(int) (1 << 2)
	 * 		<br><b>PROFILE_SPP</b>			(int) (1 << 3)
	 * 		<br><b>PROFILE_PBAP</b>			(int) (1 << 4)
	 *		<br><b>PROFILE_AVRCP</b>		(int) (1 << 5)
	 *		<br><b>PROFILE_FTP</b>			(int) (1 << 6)
	 *		<br><b>PROFILE_MAP</b>			(int) (1 << 7)
	 *		<br><b>PROFILE_AVRCP_13</b>		(int) (1 << 8)
	 *		<br><b>PROFILE_AVRCP_14</b>		(int) (1 << 9)
	 *		<br><b>PROFILE_PANU</b>			(int) (1 << 10)	
	 *		<br><b>PROFILE_NAP</b>			(int) (1 << 11)
	 *		<br><b>PROFILE_DUN</b>			(int) (1 << 12)</blockquote>	  
	 * For example, value 6 (0000 0110) means that HFP and A2DP would be disconnected.	 	 
	 */
	int reqBtDisconnectAll();
	
	/** 
	 * Get the supported profiles of remote device.
	 * The requested address must be the paired device.
	 *
	 * This command will return with an integer value immediately, which represents the supported profiles.
	 * If 0x00 is returned, that means UUID fetched from system framework is null and nFore service will request
	 * SDP query to this device automatically. Clients should register and implement callback functions
	 * {@link IServiceCallback#onDeviceUuidsGot onDeviceUuidsGot} 
	 * to be notified of subsequent result.
	 *
	 * @param address valid Bluetooth MAC address of paired device.
	 * @return the supported profiles of paired device in int type. Possible values are:
	 *		<p><blockquote><b>ERROR</b>		(int) -1
	 *		<br><b>PROFILE_HSP</b>			(int) 1
	 *		<br><b>PROFILE_HFP</b>			(int) (1 << 1)
	 *		<br><b>PROFILE_A2DP</b>			(int) (1 << 2)
	 * 		<br><b>PROFILE_SPP</b>			(int) (1 << 3)
	 * 		<br><b>PROFILE_PBAP</b>			(int) (1 << 4)
	 *		<br><b>PROFILE_AVRCP</b>		(int) (1 << 5)
	 *		<br><b>PROFILE_FTP</b>			(int) (1 << 6)
	 *		<br><b>PROFILE_MAP</b>			(int) (1 << 7)
	 *		<br><b>PROFILE_AVRCP_13</b>		(int) (1 << 8)
	 *		<br><b>PROFILE_AVRCP_14</b>		(int) (1 << 9)
	 *		<br><b>PROFILE_PANU</b>			(int) (1 << 10)
	 *		<br><b>PROFILE_NAP</b>			(int) (1 << 11)
	 *		<br><b>PROFILE_DUN</b>			(int) (1 << 12)
	 *		<br><b>PROFILE_IAP</b>			(int) (1 << 13)</blockquote>
	 * For example, value 7 (0000 0111) means it supports HSP, HFP and A2DP.
	 */
	int getBtRemoteUuids(String address);

	/**
	 * Switch Bluetooth mode to other role.
	 *
	 * @param mode the mode of Bluetooth role in integer type
	 * 		<p><blockquote><b>MODE_CAR</b>		(int) 1
	 *		<br><b>MODE_TABLET</b>				(int) 2
	 */
	boolean switchBtRoleMode(int mode);

	/**
	 * Get Bluetooth role mode in integer.
	 *
	 * @return The mode of Bluetooth role in integer type
	 * 		<p><blockquote><b>MODE_CAR</b>		(int) 1
	 *		<br><b>MODE_TABLET</b>				(int) 2
	 */
	int getBtRoleMode();


	// HFP


	/** 
	 * Register callback functions for HFP.
	 * <br>Call this function to register callback functions for HFP.
	 * <br>Allow nFore service to call back to its registered clients, which is usually the UI application.
	 *
	 * @param cb callback interface instance.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 
    boolean registerHfpCallback(UiCallbackHfp cb);
    
	/** 
	 * Remove callback functions from HFP.
     * <br>Call this function to remove previously registered callback interface for HFP.
     * 
     * @param cb callback interface instance.
	 * @return true to indicate the operation is successful, or false erroneous.
     */ 
    boolean unregisterHfpCallback(UiCallbackHfp cb);

	/** 
	 * Get current connection state of the remote device.
	 * 
	 * @return current state of HFP profile service.
	 */ 
    int getHfpConnectionState();

    /** 
	 * Check if local device is HFP connected with remote device.
	 *
	 * @return true to indicate HFP is connected, or false disconnected.
	 */
    boolean isHfpConnected();

    /** 
	 * Get the Bluetooth hardware address of HFP connected remote device.
	 *
	 * @return Bluetooth hardware address as string if there is a connected HFP device, or 
	 * <code>DEFAULT_ADDRESS</code> 00:00:00:00:00:00.
	 */ 
    String getHfpConnectedAddress();

    /** 
	 * Get the current audio state of HFP connected remote device.
	 * 
	 * @return current HFP audio state.
	 */ 
    int getHfpAudioConnectionState();

	/** 
	 * Request to connect HFP to the remote device.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link UiCallbackHfp#onHfpStateChanged onHfpStateChanged} to be notified of subsequent profile state changes.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 
    boolean reqHfpConnect(String address);
    
	/** 
	 * Request to disconnect HFP to the remote device.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback function 
	 * {@link UiCallbackHfp#onHfpStateChanged onHfpStateChanged} to be notified of subsequent profile state changes.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 
    boolean reqHfpDisconnect(String address);

    /** 
	 * Get the signal strength of HFP connected remote device.
	 *	 	 
	 * @return the current signal strength of remote device.	 
	 */
    int getHfpRemoteSignalStrength();

    /** 
	 * Get the active phone number information of HFP connected remote device.
	 *	 	 
	 * @return the phone number information list of remote device.	 
	 */
    List<NfHfpClientCall> getHfpCallList();

    /** 
	 * Check if the HFP connected remote device is on roaming.
	 *	 	 
	 * @return true to indicate the remote device is current on roaming.	 
	 */ 
    boolean isHfpRemoteOnRoaming();

    /** 
	 * Get the battery indicator of HFP connected remote device.
	 *
	 * @return the current battery indicator of remote device.
	 */ 
    int getHfpRemoteBatteryIndicator();

    /** 
	 * Check if the HFP connected remote device has telecom service.
	 *	 	 
	 * @param address valid Bluetooth MAC address.
	 * @return true to indicate remote device has telecom service.
	 */ 
    boolean isHfpRemoteTelecomServiceOn();

    /** 
	 * Check if the voice dial of HFP connected remote device is activated.
	 *
	 * @param address valid Bluetooth MAC address of connected device.	 
	 * @return true to indicate voice dial is activated for remote device.
	 */ 
    boolean isHfpRemoteVoiceDialOn();

	/** 
	 * Request HFP connected remote device to dial a call with given phone number.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link UiCallbackHfp#onHfpCallChanged onHfpCallChanged} and {@link UiCallbackHfp#onHfpAudioStateChanged onHfpAudioStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @param number the outgoing call phone number.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 
    boolean reqHfpDialCall(String number);    
    
	/** 
	 * Request HFP connected remote device to re-dial the last outgoing call.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link UiCallbackHfp#onHfpCallChanged onHfpCallChanged} and {@link UiCallbackHfp#onHfpAudioStateChanged onHfpAudioStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 
    boolean reqHfpReDial();
    
	/** 
	 * Request HFP connected remote device to do memory dialing.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link UiCallbackHfp#onHfpCallChanged onHfpCallChanged} and {@link UiCallbackHfp#onHfpAudioStateChanged onHfpAudioStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
 	 * @param index the memory index on mobile phone. The phone number with the memory index will be dialed out, for example: 1-9
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 
    boolean reqHfpMemoryDial(String index);         
    
	/** 
	 * Request HFP connected remote device to answer the incoming call.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link UiCallbackHfp#onHfpCallChanged onHfpCallChanged} and {@link UiCallbackHfp#onHfpAudioStateChanged onHfpAudioStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @return true if the command is sent successfully
	 */ 
    boolean reqHfpAnswerCall(int flag);
    
	/** 
	 * Request HFP connected remote device to reject the incoming call.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link UiCallbackHfp#onHfpCallChanged onHfpCallChanged} and {@link UiCallbackHfp#onHfpAudioStateChanged onHfpAudioStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @return true if the command is sent successfully
	 */ 
    boolean reqHfpRejectIncomingCall();
    
	/** 
	 * Request HFP connected remote device to terminate the ongoing call.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link INfCallbackHfp#onHfpCallChanged onHfpCallChanged} and {@link INfCallbackHfp#onHfpAudioStateChanged onHfpAudioStateChanged}
	 * to be notified of subsequent profile state changes.
	 *
	 * @return true if the command is sent successfully
	 */ 
    boolean reqHfpTerminateCurrentCall();
    
	/** 
	 * Request HFP connected remote device to send the DTMF.
	 * <p>Due to the compatibility, please request this API with single DTMF number each time.
	 * <p>Avoid requesting with serial DTMF numbers. 
	 *
 	 * @param number DTMF number.
 	 * @return true if the command is sent successfully
	 */ 
    boolean reqHfpSendDtmf(String number);
    
	/** 
	 * Request HFP connected remote device to transfer the audio to Carkit.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link UiCallbackHfp#onHfpAudioStateChanged onHfpAudioStateChanged} to be notified of subsequent state changes.
	 *
	 * @return true if the command is sent successfully	 
	 */
    boolean reqHfpAudioTransferToCarkit();

    /** 
	 * Request HFP connected remote device to transfer the audio to Phone.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link UiCallbackHfp#onHfpAudioStateChanged onHfpAudioStateChanged} to be notified of subsequent state changes.
	 *
	 * @return true if the command is sent successfully	 
	 */
    boolean reqHfpAudioTransferToPhone();
    
	/** 
	 * Get the network operator of HFP connected remote device.
	 *	 	 
	 * @return network operator
	 */ 
    String getHfpRemoteNetworkOperator();
    
	/** 
	 * Get the subscriber number of HFP connected remote device.
	 *	 	 
	 * @return subscriber number
	 */ 
    String getHfpRemoteSubscriberNumber();

    /**
	 * Request HFP connected remote device to do the voice dialing.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link UiCallbackHfp#onHfpCallChanged onHfpCallChanged} and {@link UiCallbackHfp#onHfpAudioStateChanged onHfpAudioStateChanged}
	 * to be notified of subsequent profile state changes.
	 * 
 	 * @param enable true to start the voice dialing.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */
    boolean reqHfpVoiceDial(boolean enable);

    /**
	 * Stop sending HFP stream data to audio track.
	 */
	void pauseHfpRender();

	/**
	 * Start sending HFP stream data to audio track.
	 */
	void startHfpRender();

	/**
	 * Check if mic is mute.
	 */
	boolean isHfpMicMute();

	/**
	 * Request HFP Mute Mic during call
	 * @param mute true to mute the microphone
	 */
	void muteHfpMic(boolean mute);

	boolean isHfpInBandRingtoneSupport();
    
    // PBAP

	/** 
	 * Register callback functions for PBAP.
	 * <br>Call this function to register callback functions for PBAP.
	 * <br>Allow nFore service to call back to its registered clients, which is usually the UI application.
	 *
	 * @param cb callback interface instance.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */
	boolean registerPbapCallback(UiCallbackPbap cb);
	
	/** 
	 * Remove callback functions from PBAP.
     * <br>Call this function to remove previously registered callback interface for PBAP.
     * 
     * @param cb callback interface instance.
	 * @return true to indicate the operation is successful, or false erroneous.
     */
	boolean unregisterPbapCallback(UiCallbackPbap cb);

	/** 
	 * Get current connection state of the remote device.
	 *
	 * @return current state of PBAP profile service.
	 */
	int getPbapConnectionState();

    /** 
	 * Check if local device is downloading phonebook from remote device.
	 *
	 * @return true to indicate PBAP is downloading, or false disconnected.
	 */
    boolean isPbapDownloading();

	/** 
	 * Get the Bluetooth hardware address of PBAP downloading remote device.
	 *
	 * @return Bluetooth hardware address as string if there is a downloading PBAP device, 
	 * or otherwise <code>DEFAULT_ADDRESS</code> 00:00:00:00:00:00.
	 */
	String getPbapDownloadingAddress();

	/**
	 * Request to download phonebook with vCard from remote device and by pass callback to user.
	 * 
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link INfCallbackPbap#retPbapDownloadedContact retPbapDownloadedContact} 
	 * and {@link INfCallbackPbap#retPbapDownloadedCallLog retPbapDownloadedCallLog}
	 * and {@link INfCallbackPbap#onPbapStateChanged onPbapStateChanged} to be notified of subsequent result.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @param storage possible storage type are:
	 *		<blockquote><b>PBAP_STORAGE_SIM</b>			(int) 1
	 *		<br><b>PBAP_STORAGE_PHONE_MEMORY</b>		(int) 2
	 *		<br><b>PBAP_STORAGE_MISSED_CALLS</b>		(int) 3
	 *		<br><b>PBAP_STORAGE_RECEIVED_CALLS</b>		(int) 4
	 *		<br><b>PBAP_STORAGE_DIALED_CALLS</b>		(int) 5
	 *		<br><b>PBAP_STORAGE_CALL_LOGS</b>			(int) 6
	 *		<br><b>PBAP_STORAGE_SPEEDDIAL</b>			(int) 7
	 *		<br><b>PBAP_STORAGE_FAVORITE</b>			(int) 8</blockquote>
	 * @param property download property mask. Possible storage type are:
	 *		<blockquote><b>PBAP_PROPERTY_MASK_VERSION</b>	(int) (1&lt;&lt;0)
	 *		<br><b>PBAP_PROPERTY_MASK_FN</b>		(int) (1&lt;&lt;1)
	 *		<br><b>PBAP_PROPERTY_MASK_N</b>			(int) (1&lt;&lt;2)
	 *		<br><b>PBAP_PROPERTY_MASK_PHOTO</b>		(int) (1&lt;&lt;3)
	 *		<br><b>PBAP_PROPERTY_MASK_BDAY</b>		(int) (1&lt;&lt;4)
	 *		<br><b>PBAP_PROPERTY_MASK_ADR</b>		(int) (1&lt;&lt;5)
	 *		<br><b>PBAP_PROPERTY_MASK_LABEL</b>		(int) (1&lt;&lt;6)
	 *		<br><b>PBAP_PROPERTY_MASK_TEL</b>		(int) (1&lt;&lt;7)
	 *		<br><b>PBAP_PROPERTY_MASK_EMAIL</b>		(int) (1&lt;&lt;8)
	 *		<br><b>PBAP_PROPERTY_MASK_MAILER</b>	(int) (1&lt;&lt;9)
	 *		<br><b>PBAP_PROPERTY_MASK_TZ</b>		(int) (1&lt;&lt;10)
	 *		<br><b>PBAP_PROPERTY_MASK_GEO</b>		(int) (1&lt;&lt;11)
	 *		<br><b>PBAP_PROPERTY_MASK_TITLE</b>		(int) (1&lt;&lt;12)
	 *		<br><b>PBAP_PROPERTY_MASK_ROLE</b>		(int) (1&lt;&lt;13)
	 *		<br><b>PBAP_PROPERTY_MASK_LOGO</b>		(int) (1&lt;&lt;14)
	 *		<br><b>PBAP_PROPERTY_MASK_AGENT</b>		(int) (1&lt;&lt;15)
	 *		<br><b>PBAP_PROPERTY_MASK_ORG</b>		(int) (1&lt;&lt;16)
	 *		<br><b>PBAP_PROPERTY_MASK_NOTE</b>		(int) (1&lt;&lt;17)
	 *		<br><b>PBAP_PROPERTY_MASK_REV</b>		(int) (1&lt;&lt;18)
	 *		<br><b>PBAP_PROPERTY_MASK_SOUND</b>		(int) (1&lt;&lt;19)
	 *		<br><b>PBAP_PROPERTY_MASK_URL</b>		(int) (1&lt;&lt;20)
	 *		<br><b>PBAP_PROPERTY_MASK_UID</b>		(int) (1&lt;&lt;21)
	 *		<br><b>PBAP_PROPERTY_MASK_KEY</b>		(int) (1&lt;&lt;22)
	 *		<br><b>PBAP_PROPERTY_MASK_NICKNAME</b>	(int) (1&lt;&lt;23)
	 *		<br><b>PBAP_PROPERTY_MASK_CATEGORIES</b>(int) (1&lt;&lt;24)
	 *		<br><b>PBAP_PROPERTY_MASK_PROID</b>		(int) (1&lt;&lt;25)
	 *		<br><b>PBAP_PROPERTY_MASK_CLASS</b>		(int) (1&lt;&lt;26)
	 *		<br><b>PBAP_PROPERTY_MASK_SORT_STRING</b>(int) (1&lt;&lt;27)
	 *		<br><b>PBAP_PROPERTY_MASK_TIME_STAMP</b>(int) (1&lt;&lt;28)</blockquote>
	 * @return true to indicate the operation is successful, or false erroneous.
	 */
    boolean reqPbapDownload(String address, int storage, int property);

	/**
	 * Request to download phonebook with vCard from remote device and by pass callback to user.
	 * 
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link INfCallbackPbap#retPbapDownloadedContact retPbapDownloadedContact} 
	 * and {@link INfCallbackPbap#retPbapDownloadedCallLog retPbapDownloadedCallLog}
	 * and {@link INfCallbackPbap#onPbapStateChanged onPbapStateChanged} to be notified of subsequent result.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @param storage possible storage type are:
	 *		<blockquote><b>PBAP_STORAGE_SIM</b>			(int) 1
	 *		<br><b>PBAP_STORAGE_PHONE_MEMORY</b>		(int) 2
	 *		<br><b>PBAP_STORAGE_MISSED_CALLS</b>		(int) 3
	 *		<br><b>PBAP_STORAGE_RECEIVED_CALLS</b>		(int) 4
	 *		<br><b>PBAP_STORAGE_DIALED_CALLS</b>		(int) 5
	 *		<br><b>PBAP_STORAGE_CALL_LOGS</b>			(int) 6
	 *		<br><b>PBAP_STORAGE_SPEEDDIAL</b>			(int) 7
	 *		<br><b>PBAP_STORAGE_FAVORITE</b>			(int) 8</blockquote>
	 * @param property download property mask. Possible storage type are:
	 *		<blockquote><b>PBAP_PROPERTY_MASK_VERSION</b>	(int) (1&lt;&lt;0)
	 *		<br><b>PBAP_PROPERTY_MASK_FN</b>		(int) (1&lt;&lt;1)
	 *		<br><b>PBAP_PROPERTY_MASK_N</b>			(int) (1&lt;&lt;2)
	 *		<br><b>PBAP_PROPERTY_MASK_PHOTO</b>		(int) (1&lt;&lt;3)
	 *		<br><b>PBAP_PROPERTY_MASK_BDAY</b>		(int) (1&lt;&lt;4)
	 *		<br><b>PBAP_PROPERTY_MASK_ADR</b>		(int) (1&lt;&lt;5)
	 *		<br><b>PBAP_PROPERTY_MASK_LABEL</b>		(int) (1&lt;&lt;6)
	 *		<br><b>PBAP_PROPERTY_MASK_TEL</b>		(int) (1&lt;&lt;7)
	 *		<br><b>PBAP_PROPERTY_MASK_EMAIL</b>		(int) (1&lt;&lt;8)
	 *		<br><b>PBAP_PROPERTY_MASK_MAILER</b>	(int) (1&lt;&lt;9)
	 *		<br><b>PBAP_PROPERTY_MASK_TZ</b>		(int) (1&lt;&lt;10)
	 *		<br><b>PBAP_PROPERTY_MASK_GEO</b>		(int) (1&lt;&lt;11)
	 *		<br><b>PBAP_PROPERTY_MASK_TITLE</b>		(int) (1&lt;&lt;12)
	 *		<br><b>PBAP_PROPERTY_MASK_ROLE</b>		(int) (1&lt;&lt;13)
	 *		<br><b>PBAP_PROPERTY_MASK_LOGO</b>		(int) (1&lt;&lt;14)
	 *		<br><b>PBAP_PROPERTY_MASK_AGENT</b>		(int) (1&lt;&lt;15)
	 *		<br><b>PBAP_PROPERTY_MASK_ORG</b>		(int) (1&lt;&lt;16)
	 *		<br><b>PBAP_PROPERTY_MASK_NOTE</b>		(int) (1&lt;&lt;17)
	 *		<br><b>PBAP_PROPERTY_MASK_REV</b>		(int) (1&lt;&lt;18)
	 *		<br><b>PBAP_PROPERTY_MASK_SOUND</b>		(int) (1&lt;&lt;19)
	 *		<br><b>PBAP_PROPERTY_MASK_URL</b>		(int) (1&lt;&lt;20)
	 *		<br><b>PBAP_PROPERTY_MASK_UID</b>		(int) (1&lt;&lt;21)
	 *		<br><b>PBAP_PROPERTY_MASK_KEY</b>		(int) (1&lt;&lt;22)
	 *		<br><b>PBAP_PROPERTY_MASK_NICKNAME</b>	(int) (1&lt;&lt;23)
	 *		<br><b>PBAP_PROPERTY_MASK_CATEGORIES</b>(int) (1&lt;&lt;24)
	 *		<br><b>PBAP_PROPERTY_MASK_PROID</b>		(int) (1&lt;&lt;25)
	 *		<br><b>PBAP_PROPERTY_MASK_CLASS</b>		(int) (1&lt;&lt;26)
	 *		<br><b>PBAP_PROPERTY_MASK_SORT_STRING</b>(int) (1&lt;&lt;27)
	 *		<br><b>PBAP_PROPERTY_MASK_TIME_STAMP</b>(int) (1&lt;&lt;28)</blockquote>
	 * @param startPos download start position.
	 * @param offset download offset.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */
    boolean reqPbapDownloadRange(String address, int storage, int property, int startPos, int offset);

	/**
	 * Request to download phonebook with vCard from remote device and save to local database.
	 * 
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link INfCallbackPbap#onPbapStateChanged onPbapStateChanged} to be notified of subsequent result.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @param storage possible storage type are:
	 *		<blockquote><b>PBAP_STORAGE_SIM</b>			(int) 1
	 *		<br><b>PBAP_STORAGE_PHONE_MEMORY</b>		(int) 2
	 *		<br><b>PBAP_STORAGE_MISSED_CALLS</b>		(int) 3
	 *		<br><b>PBAP_STORAGE_RECEIVED_CALLS</b>		(int) 4
	 *		<br><b>PBAP_STORAGE_DIALED_CALLS</b>		(int) 5
	 *		<br><b>PBAP_STORAGE_CALL_LOGS</b>			(int) 6
	 *		<br><b>PBAP_STORAGE_SPEEDDIAL</b>			(int) 7
	 *		<br><b>PBAP_STORAGE_FAVORITE</b>			(int) 8</blockquote>	
	 * @param property download property mask. Possible storage type are:
	 *		<blockquote><b>PBAP_PROPERTY_MASK_VERSION</b>	(int) (1&lt;&lt;0)
	 *		<br><b>PBAP_PROPERTY_MASK_FN</b>		(int) (1&lt;&lt;1)
	 *		<br><b>PBAP_PROPERTY_MASK_N</b>			(int) (1&lt;&lt;2)
	 *		<br><b>PBAP_PROPERTY_MASK_PHOTO</b>		(int) (1&lt;&lt;3)
	 *		<br><b>PBAP_PROPERTY_MASK_BDAY</b>		(int) (1&lt;&lt;4)
	 *		<br><b>PBAP_PROPERTY_MASK_ADR</b>		(int) (1&lt;&lt;5)
	 *		<br><b>PBAP_PROPERTY_MASK_LABEL</b>		(int) (1&lt;&lt;6)
	 *		<br><b>PBAP_PROPERTY_MASK_TEL</b>		(int) (1&lt;&lt;7)
	 *		<br><b>PBAP_PROPERTY_MASK_EMAIL</b>		(int) (1&lt;&lt;8)
	 *		<br><b>PBAP_PROPERTY_MASK_MAILER</b>	(int) (1&lt;&lt;9)
	 *		<br><b>PBAP_PROPERTY_MASK_TZ</b>		(int) (1&lt;&lt;10)
	 *		<br><b>PBAP_PROPERTY_MASK_GEO</b>		(int) (1&lt;&lt;11)
	 *		<br><b>PBAP_PROPERTY_MASK_TITLE</b>		(int) (1&lt;&lt;12)
	 *		<br><b>PBAP_PROPERTY_MASK_ROLE</b>		(int) (1&lt;&lt;13)
	 *		<br><b>PBAP_PROPERTY_MASK_LOGO</b>		(int) (1&lt;&lt;14)
	 *		<br><b>PBAP_PROPERTY_MASK_AGENT</b>		(int) (1&lt;&lt;15)
	 *		<br><b>PBAP_PROPERTY_MASK_ORG</b>		(int) (1&lt;&lt;16)
	 *		<br><b>PBAP_PROPERTY_MASK_NOTE</b>		(int) (1&lt;&lt;17)
	 *		<br><b>PBAP_PROPERTY_MASK_REV</b>		(int) (1&lt;&lt;18)
	 *		<br><b>PBAP_PROPERTY_MASK_SOUND</b>		(int) (1&lt;&lt;19)
	 *		<br><b>PBAP_PROPERTY_MASK_URL</b>		(int) (1&lt;&lt;20)
	 *		<br><b>PBAP_PROPERTY_MASK_UID</b>		(int) (1&lt;&lt;21)
	 *		<br><b>PBAP_PROPERTY_MASK_KEY</b>		(int) (1&lt;&lt;22)
	 *		<br><b>PBAP_PROPERTY_MASK_NICKNAME</b>	(int) (1&lt;&lt;23)
	 *		<br><b>PBAP_PROPERTY_MASK_CATEGORIES</b>(int) (1&lt;&lt;24)
	 *		<br><b>PBAP_PROPERTY_MASK_PROID</b>		(int) (1&lt;&lt;25)
	 *		<br><b>PBAP_PROPERTY_MASK_CLASS</b>		(int) (1&lt;&lt;26)
	 *		<br><b>PBAP_PROPERTY_MASK_SORT_STRING</b>(int) (1&lt;&lt;27)
	 *		<br><b>PBAP_PROPERTY_MASK_TIME_STAMP</b>(int) (1&lt;&lt;28)</blockquote>
	 * @return true to indicate the operation is successful, or false erroneous.
	 */
    boolean reqPbapDownloadToDatabase(String address, int storage, int property);

	/**
	 * Request to download phonebook with vCard from remote device and save to local Contacts Provider.
	 * 
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link INfCallbackPbap#onPbapStateChanged onPbapStateChanged} to be notified of subsequent result.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @param storage possible storage type are:
	 *		<blockquote><b>PBAP_STORAGE_SIM</b>			(int) 1
	 *		<br><b>PBAP_STORAGE_PHONE_MEMORY</b>		(int) 2
	 *		<br><b>PBAP_STORAGE_MISSED_CALLS</b>		(int) 3
	 *		<br><b>PBAP_STORAGE_RECEIVED_CALLS</b>		(int) 4
	 *		<br><b>PBAP_STORAGE_DIALED_CALLS</b>		(int) 5
	 *		<br><b>PBAP_STORAGE_CALL_LOGS</b>			(int) 6
	 *		<br><b>PBAP_STORAGE_SPEEDDIAL</b>			(int) 7
	 *		<br><b>PBAP_STORAGE_FAVORITE</b>			(int) 8</blockquote>
	 * @param property download property mask. Possible storage type are:
	 *		<blockquote><b>PBAP_PROPERTY_MASK_VERSION</b>	(int) (1&lt;&lt;0)
	 *		<br><b>PBAP_PROPERTY_MASK_FN</b>		(int) (1&lt;&lt;1)
	 *		<br><b>PBAP_PROPERTY_MASK_N</b>			(int) (1&lt;&lt;2)
	 *		<br><b>PBAP_PROPERTY_MASK_PHOTO</b>		(int) (1&lt;&lt;3)
	 *		<br><b>PBAP_PROPERTY_MASK_BDAY</b>		(int) (1&lt;&lt;4)
	 *		<br><b>PBAP_PROPERTY_MASK_ADR</b>		(int) (1&lt;&lt;5)
	 *		<br><b>PBAP_PROPERTY_MASK_LABEL</b>		(int) (1&lt;&lt;6)
	 *		<br><b>PBAP_PROPERTY_MASK_TEL</b>		(int) (1&lt;&lt;7)
	 *		<br><b>PBAP_PROPERTY_MASK_EMAIL</b>		(int) (1&lt;&lt;8)
	 *		<br><b>PBAP_PROPERTY_MASK_MAILER</b>	(int) (1&lt;&lt;9)
	 *		<br><b>PBAP_PROPERTY_MASK_TZ</b>		(int) (1&lt;&lt;10)
	 *		<br><b>PBAP_PROPERTY_MASK_GEO</b>		(int) (1&lt;&lt;11)
	 *		<br><b>PBAP_PROPERTY_MASK_TITLE</b>		(int) (1&lt;&lt;12)
	 *		<br><b>PBAP_PROPERTY_MASK_ROLE</b>		(int) (1&lt;&lt;13)
	 *		<br><b>PBAP_PROPERTY_MASK_LOGO</b>		(int) (1&lt;&lt;14)
	 *		<br><b>PBAP_PROPERTY_MASK_AGENT</b>		(int) (1&lt;&lt;15)
	 *		<br><b>PBAP_PROPERTY_MASK_ORG</b>		(int) (1&lt;&lt;16)
	 *		<br><b>PBAP_PROPERTY_MASK_NOTE</b>		(int) (1&lt;&lt;17)
	 *		<br><b>PBAP_PROPERTY_MASK_REV</b>		(int) (1&lt;&lt;18)
	 *		<br><b>PBAP_PROPERTY_MASK_SOUND</b>		(int) (1&lt;&lt;19)
	 *		<br><b>PBAP_PROPERTY_MASK_URL</b>		(int) (1&lt;&lt;20)
	 *		<br><b>PBAP_PROPERTY_MASK_UID</b>		(int) (1&lt;&lt;21)
	 *		<br><b>PBAP_PROPERTY_MASK_KEY</b>		(int) (1&lt;&lt;22)
	 *		<br><b>PBAP_PROPERTY_MASK_NICKNAME</b>	(int) (1&lt;&lt;23)
	 *		<br><b>PBAP_PROPERTY_MASK_CATEGORIES</b>(int) (1&lt;&lt;24)
	 *		<br><b>PBAP_PROPERTY_MASK_PROID</b>		(int) (1&lt;&lt;25)
	 *		<br><b>PBAP_PROPERTY_MASK_CLASS</b>		(int) (1&lt;&lt;26)
	 *		<br><b>PBAP_PROPERTY_MASK_SORT_STRING</b>(int) (1&lt;&lt;27)
	 *		<br><b>PBAP_PROPERTY_MASK_TIME_STAMP</b>(int) (1&lt;&lt;28)</blockquote>
	 * @param startPos download start position.
	 * @param offset download offset.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */
    boolean reqPbapDownloadRangeToDatabase(String address, int storage, int property, int startPos, int offset);

	/**
	 * Request to download phonebook with vCard from remote device and save to local Contacts Provider.
	 * 
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link INfCallbackPbap#onPbapStateChanged onPbapStateChanged} to be notified of subsequent result.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @param storage possible storage type are:
	 *		<blockquote><b>PBAP_STORAGE_SIM</b>			(int) 1
	 *		<br><b>PBAP_STORAGE_PHONE_MEMORY</b>		(int) 2
	 *		<br><b>PBAP_STORAGE_MISSED_CALLS</b>		(int) 3
	 *		<br><b>PBAP_STORAGE_RECEIVED_CALLS</b>		(int) 4
	 *		<br><b>PBAP_STORAGE_DIALED_CALLS</b>		(int) 5
	 *		<br><b>PBAP_STORAGE_CALL_LOGS</b>			(int) 6
	 *		<br><b>PBAP_STORAGE_SPEEDDIAL</b>			(int) 7
	 *		<br><b>PBAP_STORAGE_FAVORITE</b>			(int) 8</blockquote>
	 * @param property download property mask. Possible storage type are:
	 *		<blockquote><b>PBAP_PROPERTY_MASK_VERSION</b>	(int) (1&lt;&lt;0)
	 *		<br><b>PBAP_PROPERTY_MASK_FN</b>		(int) (1&lt;&lt;1)
	 *		<br><b>PBAP_PROPERTY_MASK_N</b>			(int) (1&lt;&lt;2)
	 *		<br><b>PBAP_PROPERTY_MASK_PHOTO</b>		(int) (1&lt;&lt;3)
	 *		<br><b>PBAP_PROPERTY_MASK_BDAY</b>		(int) (1&lt;&lt;4)
	 *		<br><b>PBAP_PROPERTY_MASK_ADR</b>		(int) (1&lt;&lt;5)
	 *		<br><b>PBAP_PROPERTY_MASK_LABEL</b>		(int) (1&lt;&lt;6)
	 *		<br><b>PBAP_PROPERTY_MASK_TEL</b>		(int) (1&lt;&lt;7)
	 *		<br><b>PBAP_PROPERTY_MASK_EMAIL</b>		(int) (1&lt;&lt;8)
	 *		<br><b>PBAP_PROPERTY_MASK_MAILER</b>	(int) (1&lt;&lt;9)
	 *		<br><b>PBAP_PROPERTY_MASK_TZ</b>		(int) (1&lt;&lt;10)
	 *		<br><b>PBAP_PROPERTY_MASK_GEO</b>		(int) (1&lt;&lt;11)
	 *		<br><b>PBAP_PROPERTY_MASK_TITLE</b>		(int) (1&lt;&lt;12)
	 *		<br><b>PBAP_PROPERTY_MASK_ROLE</b>		(int) (1&lt;&lt;13)
	 *		<br><b>PBAP_PROPERTY_MASK_LOGO</b>		(int) (1&lt;&lt;14)
	 *		<br><b>PBAP_PROPERTY_MASK_AGENT</b>		(int) (1&lt;&lt;15)
	 *		<br><b>PBAP_PROPERTY_MASK_ORG</b>		(int) (1&lt;&lt;16)
	 *		<br><b>PBAP_PROPERTY_MASK_NOTE</b>		(int) (1&lt;&lt;17)
	 *		<br><b>PBAP_PROPERTY_MASK_REV</b>		(int) (1&lt;&lt;18)
	 *		<br><b>PBAP_PROPERTY_MASK_SOUND</b>		(int) (1&lt;&lt;19)
	 *		<br><b>PBAP_PROPERTY_MASK_URL</b>		(int) (1&lt;&lt;20)
	 *		<br><b>PBAP_PROPERTY_MASK_UID</b>		(int) (1&lt;&lt;21)
	 *		<br><b>PBAP_PROPERTY_MASK_KEY</b>		(int) (1&lt;&lt;22)
	 *		<br><b>PBAP_PROPERTY_MASK_NICKNAME</b>	(int) (1&lt;&lt;23)
	 *		<br><b>PBAP_PROPERTY_MASK_CATEGORIES</b>(int) (1&lt;&lt;24)
	 *		<br><b>PBAP_PROPERTY_MASK_PROID</b>		(int) (1&lt;&lt;25)
	 *		<br><b>PBAP_PROPERTY_MASK_CLASS</b>		(int) (1&lt;&lt;26)
	 *		<br><b>PBAP_PROPERTY_MASK_SORT_STRING</b>(int) (1&lt;&lt;27)
	 *		<br><b>PBAP_PROPERTY_MASK_TIME_STAMP</b>(int) (1&lt;&lt;28)</blockquote>
	 * @return true to indicate the operation is successful, or false erroneous.
	 */
    boolean reqPbapDownloadToContactsProvider(String address, int storage, int property);

	/**
	 * Request to download phonebook with vCard from remote device and save to local Contacts Provider.
	 * 
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions
	 * {@link INfCallbackPbap#onPbapStateChanged onPbapStateChanged} to be notified of subsequent result.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @param storage possible storage type are:
	 *		<blockquote><b>PBAP_STORAGE_SIM</b>			(int) 1
	 *		<br><b>PBAP_STORAGE_PHONE_MEMORY</b>		(int) 2
	 *		<br><b>PBAP_STORAGE_MISSED_CALLS</b>		(int) 3
	 *		<br><b>PBAP_STORAGE_RECEIVED_CALLS</b>		(int) 4
	 *		<br><b>PBAP_STORAGE_DIALED_CALLS</b>		(int) 5
	 *		<br><b>PBAP_STORAGE_CALL_LOGS</b>			(int) 6
	 *		<br><b>PBAP_STORAGE_SPEEDDIAL</b>			(int) 7
	 *		<br><b>PBAP_STORAGE_FAVORITE</b>			(int) 8</blockquote>
	 * @param property download property mask. Possible storage type are:
	 *		<blockquote><b>PBAP_PROPERTY_MASK_VERSION</b>	(int) (1&lt;&lt;0)
	 *		<br><b>PBAP_PROPERTY_MASK_FN</b>		(int) (1&lt;&lt;1)
	 *		<br><b>PBAP_PROPERTY_MASK_N</b>			(int) (1&lt;&lt;2)
	 *		<br><b>PBAP_PROPERTY_MASK_PHOTO</b>		(int) (1&lt;&lt;3)
	 *		<br><b>PBAP_PROPERTY_MASK_BDAY</b>		(int) (1&lt;&lt;4)
	 *		<br><b>PBAP_PROPERTY_MASK_ADR</b>		(int) (1&lt;&lt;5)
	 *		<br><b>PBAP_PROPERTY_MASK_LABEL</b>		(int) (1&lt;&lt;6)
	 *		<br><b>PBAP_PROPERTY_MASK_TEL</b>		(int) (1&lt;&lt;7)
	 *		<br><b>PBAP_PROPERTY_MASK_EMAIL</b>		(int) (1&lt;&lt;8)
	 *		<br><b>PBAP_PROPERTY_MASK_MAILER</b>	(int) (1&lt;&lt;9)
	 *		<br><b>PBAP_PROPERTY_MASK_TZ</b>		(int) (1&lt;&lt;10)
	 *		<br><b>PBAP_PROPERTY_MASK_GEO</b>		(int) (1&lt;&lt;11)
	 *		<br><b>PBAP_PROPERTY_MASK_TITLE</b>		(int) (1&lt;&lt;12)
	 *		<br><b>PBAP_PROPERTY_MASK_ROLE</b>		(int) (1&lt;&lt;13)
	 *		<br><b>PBAP_PROPERTY_MASK_LOGO</b>		(int) (1&lt;&lt;14)
	 *		<br><b>PBAP_PROPERTY_MASK_AGENT</b>		(int) (1&lt;&lt;15)
	 *		<br><b>PBAP_PROPERTY_MASK_ORG</b>		(int) (1&lt;&lt;16)
	 *		<br><b>PBAP_PROPERTY_MASK_NOTE</b>		(int) (1&lt;&lt;17)
	 *		<br><b>PBAP_PROPERTY_MASK_REV</b>		(int) (1&lt;&lt;18)
	 *		<br><b>PBAP_PROPERTY_MASK_SOUND</b>		(int) (1&lt;&lt;19)
	 *		<br><b>PBAP_PROPERTY_MASK_URL</b>		(int) (1&lt;&lt;20)
	 *		<br><b>PBAP_PROPERTY_MASK_UID</b>		(int) (1&lt;&lt;21)
	 *		<br><b>PBAP_PROPERTY_MASK_KEY</b>		(int) (1&lt;&lt;22)
	 *		<br><b>PBAP_PROPERTY_MASK_NICKNAME</b>	(int) (1&lt;&lt;23)
	 *		<br><b>PBAP_PROPERTY_MASK_CATEGORIES</b>(int) (1&lt;&lt;24)
	 *		<br><b>PBAP_PROPERTY_MASK_PROID</b>		(int) (1&lt;&lt;25)
	 *		<br><b>PBAP_PROPERTY_MASK_CLASS</b>		(int) (1&lt;&lt;26)
	 *		<br><b>PBAP_PROPERTY_MASK_SORT_STRING</b>(int) (1&lt;&lt;27)
	 *		<br><b>PBAP_PROPERTY_MASK_TIME_STAMP</b>(int) (1&lt;&lt;28)</blockquote>
	 * @param startPos download start position.
	 * @param offset download offset.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */
    boolean reqPbapDownloadRangeToContactsProvider(String address, int storage, int property, int startPos, int offset);

    /**
	 * Request to query the corresponding name by a given phone number from database.
	 *
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link UiCallbackPbap#retPbapDatabaseQueryNameByNumber retPbapDatabaseQueryNameByNumber} to be notified of subsequent result.
	 *
	 * @param address valid Bluetooth MAC address.
     * @param target the phone number given to database query.
     */
	void reqPbapDatabaseQueryNameByNumber(String address, String target);

	/**
	 * Request to querythe corresponding name by a given partial phone number from database.
	 *
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link UiCallbackPbap#retPbapDatabaseQueryNameByPartialNumber retPbapDatabaseQueryNameByPartialNumber} to be notified of subsequent result.
	 *
	 * @param address valid Bluetooth MAC address.
     * @param target the phone number given to database query like number.
     * @param findPosition decide which target position you want to find. Possible value are:
     *		<br>For example: target is 123.
     *		<blockquote><b>SQL_QUERY_FIND_CONTAIN</b>		(int) 0 ex. XXX<b>123</b>XXX
	 *		<br><b>SQL_QUERY_FIND_HEAD</b>					(int) 1 ex. <b>123</b>XXXXXX
	 *		<br><b>SQL_QUERY_FIND_TAIL</b>					(int) 2 ex. XXXXXX<b>123</b></blockquote>
     */
	void reqPbapDatabaseQueryNameByPartialNumber(String address, String target, int findPosition);
	
    /**
	 * Request to check if nFore's PBAP database is available for query.
	 *
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link UiCallbackPbap#retPbapDatabaseAvailable retPbapDatabaseAvailable} to be notified when database is available.
	 *
	 * <p>When nFore's PBAP database is required to access, this command has to be issued in advanced and wait for 
	 * the callback {@link UiCallbackPbap#retPbapDatabaseAvailable retPbapDatabaseAvailable}. Or the database may be crashed!
	 * <br>After nFore's PBAP database is done accessing, the database resource needs to be released. And should <b>never</b> use commands
	 * {@link UiCommandPbap#reqPbapDownloadToDatabase reqPbapDownloadToDatabase}, 
	 * {@link UiCommandPbap#reqPbapDownloadToContactsProvider reqPbapDownloadToContactsProvider},
	 * {@link UiCommandPbap#reqPbapDatabaseQueryNameByNumber reqPbapDatabaseQueryNameByNumber} or 
	 * {@link UiCommandPbap#reqPbapDatabaseQueryNameByPartialNumber reqPbapDatabaseQueryNameByPartialNumber} 
	 * before the database resource is released. 
	 * 
	 * @param address valid Bluetooth MAC address.
	 */
    void reqPbapDatabaseAvailable(String address);

	/**
	 * Request to delete phonebook data of specific address from database.
	 * 
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link UiCallbackPbap#retPbapDeleteDatabaseByAddressCompleted retPbapDeleteDatabaseByAddressCompleted} to be notified when database has been deleted.
	 *
	 * @param address valid Bluetooth MAC address.
	 */
    void reqPbapDeleteDatabaseByAddress(String address);

    /**
	 * Request to clean whole PBAP database.
	 *
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link UiCallbackPbap#retPbapCleanDatabaseCompleted retPbapCleanDatabaseCompleted} to be notified when database has been cleaned.
	 */
    void reqPbapCleanDatabase();

    /**
	 * Request to interrupt the ongoing download from remote device.
	 *
	 * <br>Clients should register and implement callback function {@link UiCallbackPbap#onPbapStateChanged onPbapStateChanged} to be notified of subsequent result.    
	 *
	 * @param address valid Bluetooth MAC address.
	 * @return true if really try to interrupt.
	 */
    boolean reqPbapDownloadInterrupt(String address);

    /**
	 * Set PBAP download notify frequency. Will set to default value when ServiceManager restart.
	 * Default value is 0 means don't callback download notify. For example, if frequency is set to 5, every 5 contacts onPbapDownloadNofity will be notified.
	 *
	 * <br>Clients should register and implement callback function {@link INfCallbackPbap#onPbapDownloadNotify onPbapDownloadNotify} to be notified of subsequent result.
	 * Callback will be invoked if below commands are issued     
	 * {@link INfCommandPbap#reqPbapDownload reqPbapDownload}, 
	 * {@link INfCommandPbap#reqPbapDownloadRange reqPbapDownloadRange},
	 * {@link INfCommandPbap#reqPbapDownloadToDatabase reqPbapDownloadToDatabase},
	 * {@link INfCommandPbap#reqPbapDownloadRangeToDatabase reqPbapDownloadRangeToDatabase},
	 * {@link INfCommandPbap#reqPbapDownloadToContactsProvider reqPbapDownloadToContactsProvider} or 
	 * {@link INfCommandPbap#reqPbapDownloadRangeToContactsProvider reqPbapDownloadRangeToContactsProvider} 
	 *
	 * @param frequency define the callback frequency.
     *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */
    boolean setPbapDownloadNotify(int frequency);

    // SPP

     	/** 
	 * Register callback functions for SPP profile.
	 * Call this function to register callback functions for SPP profile.
	 * Allow nFore service to call back to its registered clients, which might often be UI application.
	 *
	 * @param cb callback interface instance.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 
    boolean registerSppCallback(UiCallbackSpp cb);   
    
	/** 
	 * Remove callback functions for SPP profile.
     * Call this function to remove previously registered callback interface for SPP profile.
     * 
     * @param cb callback interface instance.
	 * @return true to indicate the operation is successful, or false erroneous.
     */ 
    boolean unregisterSppCallback(UiCallbackSpp cb);    
    
	/** 
	 * Request to connect SPP to the remote device.
	 *
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link INfCallbackSpp#onSppStateChanged onSppStateChanged} to be notified of subsequent profile state changes.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */
    boolean reqSppConnect(String address);
    
	/** 
	 * Request to disconnect the connected SPP connection to the remote device.
	 *
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link INfCallbackSpp#onSppStateChanged onSppStateChanged} to be notified of subsequent profile state changes.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 
    boolean reqSppDisconnect(String address);

	/** 
	 * Request for the hardware Bluetooth address of the remote SPP devices.
	 * For example, "00:11:22:AA:BB:CC".
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link INfCallbackSpp#retSppConnectedDeviceAddressList retSppConnectedDeviceAddressList} to be notified of subsequent result.
	 */
    void reqSppConnectedDeviceAddressList();    
    
	/** 
	 * Check if local device is SPP connected with the remote device. 	 
	 *
	 * @param address valid Bluetooth MAC address.	 
	 * @return true if device with given address is currently connected.
	 */ 
    boolean isSppConnected(String address);    
    
	/** 
	 * Request to send given data to the remote SPP device.
	 * 
	 * Data size should not be greater than 512 bytes each time. 
	 *
	 * @param address valid Bluetooth MAC address of connected device.
	 * @param sppData the data to be sent.
	 */
    void reqSppSendData(String address, in byte[] sppData);


/** 
	 * Register callback functions for HID.
	 * <br>Call this function to register callback functions for HID.
	 * <br>Allow nFore service to call back to its registered clients, which is usually the UI application.
	 *
	 * @param cb callback interface instance.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */
	boolean registerHidCallback(UiCallbackHid cb);
	
	/** 
	 * Remove callback functions from HID.
     * <br>Call this function to remove previously registered callback interface for HID.
     * 
     * @param cb callback interface instance.
	 * @return true to indicate the operation is successful, or false erroneous.
     */
	boolean unregisterHidCallback(UiCallbackHid cb);
	
	/** 
	 * Get current connection state of the remote device.
	 *
	 * @return current state of HID profile service.
	 */
	int getHidConnectionState();
	
	/** 
	 * Check if local device is HID connected with remote device.
	 *
	 * @return true to indicate HID is connected, or false disconnected.
	 */
	boolean isHidConnected();
	
	/** 
	 * Get the Bluetooth hardware address of HID connected remote device.
	 *
	 * @return Bluetooth hardware address as string if there is a connected HID device, or 
	 * <code>DEFAULT_ADDRESS</code> 00:00:00:00:00:00.
	 */
	String getHidConnectedAddress();
	
	/** 
	 * Request to connect HID to the remote device.
	 * <br>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackHid#onHidStateChanged onHidStateChanged} to be notified of subsequent profile state changes.
	 * 
	 * <p>There is no guarantee that Hid will be connected and the sequence of state changed callback of profiles! 
	 * <br>This depends on the behavior of connected device.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */
	boolean reqHidConnect(String address);
	
	/** 
	 * Request to disconnect HID to the remote device.
	 * <p>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link UiCallbackHid#onHidStateChanged onHidStateChanged} to be notified of subsequent profile state changes.
	 * 
	 * <p>There is no guarantee that HID will be disconnected and the sequence of state changed callback of profiles! 
	 * <br>This depends on the behavior of connected device.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */
	boolean reqHidDisconnect(String address);
	
	/** 
	 * Request to send HID mouse command to the remote device.
	 * 
	 * It should be noticed that the function is available after HID connect success.
	 * The return value is an integer means this function has sent data to the remote device. 0 means fail.
	 * @param button : You should put the correct value to this parameter. For exmple : 0x01 means left button. 0x02 means right button.	
	 *				   Please refers to "USB HID Usage Tables, v1.12, page 67". 		   
	 * @param offset_x : You should put x-direction of mouse offset on this parameter. The range should be in (32768 ~ -32768).
	 *				   The parameter is the relative value of last position.
	 * @param offset_y : You should put y-direction of mouse offset on this parameter. The range should be in (32768 ~ -32768).
	 *				   The parameter is the relative value of last position.
	 * @param wheel : You should put wheel information of mouse offset on this parameter. The range should be in (127 ~ -127).
	 *				   The parameter is the relative value of last position.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 
	boolean reqSendHidMouseCommand(int button, int offset_x, int offset_y, int wheel);	
	
	/** 
	 * Request to send virtual key event to the remote device.
	 * 
	 * It should be noticed that the function is available after HID connect success.
	 * The return value is an integer means this function has sent data to the remote device. 0 means fail.
	 * About the key_1 and key_2 value, please refers to "USB HID Usage Tables, v1.12, page 75-102".
	 * @param key_1 : You should put virtual key command on this parameter. For example, 0x223 means home key. 0x224 means back button.
	 * 					The range should be in (1 ~ 652).
	 * @param key_2 : You should put virtual key command on this parameter. For example, 0x223 means home key. 0x224 means back button.
	 * 					The range should be in (1 ~ 652).
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 
	boolean reqSendHidVirtualKeyCommand(int key_1, int key_2);

	// MAP

	/** 
	 * Register callback functions for MAP.
	 * <br>Call this function to register callback functions for MAP.
	 * <br>Allow nFore service to call back to its registered clients, which is usually the UI application.
	 *
	 * @param cb callback interface instance.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 
    boolean registerMapCallback(UiCallbackMap cb);   
    
	/** 
	 * Remove callback functions from MAP.
     * <br>Call this function to remove previously registered callback interface for MAP.
     * 
     * @param cb callback interface instance.
	 * @return true to indicate the operation is successful, or false erroneous.
     */ 
    boolean unregisterMapCallback(UiCallbackMap cb);    
    
	/** 
	 * Request to download all messages from remote device.
	 * <br>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link INfCallbackMap#onMapStateChanged onMapStateChanged} to be notified of subsequent profile state changes.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @param folder
	 *		<p><blockquote>MAP_INBOX_OUTBOX		(int) 0
	 *		<br>MAP_INBOX_ONLY					(int) 1
	 *		<br>MAP_OUTBOX_ONLY					(int) 2</blockquote>
	 * @param isContentDownload
	 * <p><value>=false, download message list only
	 * <p><value>=true, download all messages including the contents, but this will set all messages to "read"
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 	    
    boolean reqMapDownloadAllMessages(String address, int folder, boolean isContentDownload);    
    
    /**
	 * Request to download single message from remote device.
	 * 
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link INfCallbackMap#onMapStateChanged onMapStateChanged} to be notified of subsequent result.
	 *
	 * @param address valid Bluetooth MAC address.	
	 * @param folder
	 *		<p><blockquote>MAP_INBOX_ONLY		(int) 1
	 *		<br>MAP_OUTBOX_ONLY					(int) 2</blockquote>
	 * @param handle MAP handle
	 * @return true to indicate the operation is successful, or false erroneous.
	 */
	boolean reqMapDownloadSingleMessage(String address, int folder, String handle);

	/** 
	 * Request to download all messages from remote device and save to local database.
	 * <br>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link INfCallbackMap#onMapStateChanged onMapStateChanged} to be notified of subsequent profile state changes.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @param folder
	 *		<p><blockquote>MAP_INBOX_OUTBOX		(int) 0
	 *		<br>MAP_INBOX_ONLY					(int) 1
	 *		<br>MAP_OUTBOX_ONLY					(int) 2</blockquote>	
	 * @param isContentDownload
	 * <p><value>=false, download message list only
	 * <p><value>=true, download all messages including the contents, but this will set all messages to "read"
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 	    
    boolean reqMapDownloadAllMessagesToDatabase(String address, int folder, boolean isContentDownload);    
    
    /**
	 * Request to download single message from remote device and save to local database.
	 * 
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link INfCallbackMap#onMapStateChanged onMapStateChanged} to be notified of subsequent result.
	 *
	 * @param address valid Bluetooth MAC address.	
	 * @param folder
	 *		<p><blockquote>MAP_INBOX_ONLY		(int) 1
	 *		<br>MAP_OUTBOX_ONLY					(int) 2</blockquote>
	 * @param handle MAP handle
	 * @return true to indicate the operation is successful, or false erroneous.		 
	 */
	boolean reqMapDownloadSingleMessageToDatabase(String address, int folder, String handle);
     
    /**
	 * Request to register notification when there is new message on remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".
	 * <br>This is an asynchronous call: it will return immediately, and clients should register and implement callback functions 
	 * {@link INfCallbackMap#onMapStateChanged onMapStateChanged} to be notified if register notification success.
	 * and implement callback function {@link INfCallbackMap#onMapNewMessageReceivedEvent onMapNewMessageReceivedEvent}
	 * , {@link INfCallbackMap#onMapMemoryAvailableEvent onMapMemoryAvailableEvent}
	 * , {@link INfCallbackMap#onMapMessageSendingStatusEvent onMapMessageSendingStatusEvent}
	 * , {@link INfCallbackMap#onMapMessageDeliverStatusEvent onMapMessageDeliverStatusEvent}
	 * , {@link INfCallbackMap#onMapMessageShiftedEvent onMapMessageShiftedEvent}
	 * , {@link INfCallbackMap#onMapMessageDeletedEvent onMapMessageDeletedEvent}
	 * to be notified of receivced new message.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @param downloadNewMessage if true, download the new message, including sender and message contents; if false, only notification will be sent
	 */
    boolean reqMapRegisterNotification(String address, boolean downloadNewMessage);
    
    /**
	 * Request to unregister new message notification on remote device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB". 
	 *
	 * @param address valid Bluetooth MAC address.
	 */
    void reqMapUnregisterNotification(String address); 
    
	/** 
	 * Return true if the new message notification is registered on device with given address.
	 *
	 * @param address valid Bluetooth MAC address.	 
	 * @return true if registered.
	 */ 
    boolean isMapNotificationRegistered(String address);

    /**
	 * Request to interrupt the ongoing download on remote device.
	 * 
	 * Clients should register and implement callback function {@link INfCallbackMap#onMapStateChanged onMapStateChanged} to be notified of subsequent result.    
	 *
	 * @param address valid Bluetooth MAC address.
	 * @return true if really try to interrupt.
	 */
    boolean reqMapDownloadInterrupt(String address);

	/**
	 * Request to check if database is available for query
	 * Client should register and implement {@link INfCallbackMap#retMapDatabaseAvailable retMapDatabaseAvailable} 
	 * to be notified when database is available.
	 *
	 * @param address valid Bluetooth MAC address.	 
	 */ 
    void reqMapDatabaseAvailable();

    /**
	 * Request to delete MAP data by specific address
	 * Client should register and implement {@link INfCallbackMap#retMapDeleteDatabaseByAddressCompleted retMapDeleteDatabaseByAddressCompleted} 
	 * to be notified when database is available.
	 *
	 * @param address valid Bluetooth MAC address.	 
	 */
	void reqMapDeleteDatabaseByAddress(String address);

    /**
	 * Request to clean database of MAP.
	 * This is an asynchronous call: it will return immediately, and clients should register and implement callback function
	 * {@link INfCallbackMap#retMapCleanDatabaseCompleted retMapCleanDatabaseCompleted} to be notified when database has been cleaned.	 
	 */ 
	void reqMapCleanDatabase();

	/** 
	 * Request for the current download state of remote connected MAP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".	 
	 *
	 * @param address valid Bluetooth MAC address of connected device.	 
	 * @return current state of MAP profile service.
	 */ 
    int getMapCurrentState(String address);

    /** 
	 * Request for the current register state of remote connected MAP device with given Bluetooth hardware address.
	 * Valid Bluetooth hardware addresses must be in a format such as "00:11:22:33:AA:BB".	 
	 *
	 * @param address valid Bluetooth MAC address of connected device.	 
	 * @return current state of MAP profile service.
	 */ 
    int getMapRegisterState(String address);  
    
    /**
	 * Request to send message on remote device.
	 * 
	 * Clients should register and implement callback function {@link INfCallbackMap#retMapSendMessageCompleted retMapSendMessageCompleted} to be notified of subsequent result.    
	 *
	 * @param address valid Bluetooth MAC address.
	 * @param message send context.
	 * @param target phone number of target.
	 */
    boolean reqMapSendMessage(String address, String message, String target);
    
   /**
	 * Request to delete message on remote device.
	 * 
	 * Clients should register and implement callback function {@link INfCallbackMap#retMapDeleteMessageCompleted retMapDeleteMessageCompleted} to delete a message in remote device.
	 * Suggest that the message handle should be updated by downloading message listing before deleting a message.
	 *
	 * @param address valid Bluetooth MAC address.
	 * @param folder which folder message download from. Possible values are
     *		<br>FOLDER_STRUCTURE_INBOX					(int) 0
	 * 		<br>FOLDER_STRUCTURE_SENT					(int) 1
	 * @param handle MAP handle
	 */
    boolean reqMapDeleteMessage(String address, int folder, String handle);
    
    /**
	 * Request to change read status of message.
	 * 
	 * Clients should register and implement callback function {@link INfCallbackMap#retMapChangeReadStatusCompleted retMapChangeReadStatusCompleted} to modify a read status in remote device.    
	 *
	 * @param address valid Bluetooth MAC address.
	 * @param folder which folder message download from. Possible values are
     *		<br>FOLDER_STRUCTURE_INBOX					(int) 0
	 * 		<br>FOLDER_STRUCTURE_SENT					(int) 1
	 * @param handle MAP handle
	 * @param isReadStatus that "true" (=read) or "false" (=unread) for the "readStatus" indicator
	 */
    boolean reqMapChangeReadStatus(String address, int folder, String handle, boolean isReadStatus);

    /**
	 * Set MAP download notify frequency. Will set to default value when ServiceManager restart.
	 * Default value is 0 means don't callback download notify. For example, if frequency is set to 5, every 5 contacts onPbapDownloadNofity will be notified.
	 *
	 * <br>Clients should register and implement callback function {@link INfCallbackMap#onMapDownloadNotify onMapDownloadNotify} to be notified of subsequent result.
	 * Callback will be invoked if below commands are issued     
	 * {@link INfCommandMap#reqMapDownloadAllMessages reqMapDownloadAllMessages} or
	 * {@link INfCommandMap#reqMapDownloadAllMessagesToDatabase reqMapDownloadAllMessagesToDatabase}
	 *
	 * @param frequency define the callback frequency.
	 * <p><value>=0 all messages would be downloaded first, and inserted to database. Only one callback would be invocated.
	 * <p><value>>0 Callbacks would be invocated every "frequency" messages have been downloaded and inserted to database. 
	 *
	 * @return true to indicate the operation is successful, or false erroneous.
	 */
    boolean setMapDownloadNotify(int frequency);

    // OPP
    
    /**
	 * Register callback functions for OPP.
	 * <br>Call this function to register callback functions for OPP.
	 * <br>Allow nFore service to call back to its registered clients, which is usually the UI application.
	 *
	 * @param cb callback interface instance.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 
	boolean registerOppCallback(UiCallbackOpp cb);
	
	/** 
	 * Remove callback functions from OPP.
     * <br>Call this function to remove previously registered callback interface for OPP.
     * 
     * @param cb callback interface instance.
	 * @return true to indicate the operation is successful, or false erroneous.
     */ 
	boolean unregisterOppCallback(UiCallbackOpp cb);
	
    /** 
	 * Request to set Opp file storage path.
	 * <br>This is an asynchronous call: it will return immediately. 	 
	 *
	 * @param path is under sdcard path. For example, if path is set as /nfore, the actual path would be /sdcard/nfore.
	 * @return true to indicate the operation is successful, or false erroneous.
	 */ 	
	boolean setOppFilePath(String path);

	/** 
	 * Get Opp file storage path.
	 *
	 * @return Opp file storage path
	 */ 	
	String getOppFilePath();
	
	/** 
	 * Accept to receive file.
     * <br>Call this function to accept file transfer from remote device.
     * 
     * @param accept true or false to accept file transfer.
	 * @return true to indicate the operation is successful, or false erroneous.
     */ 
	boolean reqOppAcceptReceiveFile(boolean accept);
	
	/** 
	 * Accept to receive file.
     * <br>Call this function to accept file transfer from remote device.
     * 
     * @param accept true or false to accept file transfer.
	 * @return true to indicate the operation is successful, or false erroneous.
     */ 
	boolean reqOppInterruptReceiveFile();

    void setTargetAddress(String address);

    String getTargetAddress();

    void reqAvrcpUpdateSongStatus();

    // Gatt

    /** 
     * Check if Gatt Server service is ready.
     */
    boolean isGattServiceReady();

    /** 
     * Register callback functions for Gatt Server.
     * <br>Call this function to register callback functions for Gatt Server.
     * <br>Allow nFore service to call back to its registered clients, which is usually the UI application.
     *
     * @param cb callback interface instance.
     * @return true to indicate the operation is successful, or false erroneous.
     */ 
    boolean registerGattServerCallback(UiCallbackGattServer cb);

    /** 
     * Remove callback functions from Gatt Server.
     * <br>Call this function to remove previously registered callback interface for Gatt Server.
     * 
     * @param cb callback interface instance.
     * @return true to indicate the operation is successful, or false erroneous.
     */
    boolean unregisterGattServerCallback(UiCallbackGattServer cb);

    /** 
     * Get current connection state of the remote device.
     * 
     * @return current state of Gatt Server profile service.
     */ 
    int getGattServerConnectionState();

    /** 
     * Disconnects an established connection, or cancels a connection attempt
     * currently in progress.
     * <br>This is an asynchronous call: it will return immediately, and clients should register 
     * and implement callback functions {@link INfCallbackGattServer#onGattServerStateChanged onGattServerStateChanged} 
     * to be notified of subsequent profile state changes.
     * 
     * @param address Remote device address
     * @return true to indicate the operation is successful, or false erroneous.
     */ 
    boolean reqGattServerDisconnect(String address);

    /** 
     * Request to add Gatt service with service type and UUID.
     *
     * @param srvcType service type. Possible values are:
     *      <br>GATT_SERVICE_TYPE_PRIMARY                (int) 0
     *      <br>GATT_SERVICE_TYPE_SECONDARY              (int) 1
     * 
     * @param srvcUuid service UUID.
     *
     * @return true to indicate the operation is successful, or false erroneous.
     */ 
    boolean reqGattServerBeginServiceDeclaration(int srvcType, in ParcelUuid srvcUuid); 

    /** 
     * Request to add Gatt characteristic with UUID, properties and permissions with 
     * defined service.
     *
     * @param charUuid characteristic UUID.
     * @param properties Characteristic property. Possible values are:
     *      <br>GATT_CHARACTERISTIC_PROPERTY_BROADCAST              (int) 0x01
     *      <br>Characteristic is broadcastable.
     *
     *      <br>GATT_CHARACTERISTIC_PROPERTY_READ                   (int) 0x02
     *      <br>Characteristic is readable.
     *
     *      <br>GATT_CHARACTERISTIC_PROPERTY_WRITE_NO_RESPONSE      (int) 0x04
     *      <br>Characteristic can be written without response.
     *
     *      <br>GATT_CHARACTERISTIC_PROPERTY_WRITE                  (int) 0x08
     *      <br>Characteristic can be written.
     *
     *      <br>GATT_CHARACTERISTIC_PROPERTY_NOTIFY                 (int) 0x10
     *      <br>Characteristic supports notification
     *
     *      <br>GATT_CHARACTERISTIC_PROPERTY_INDICATE               (int) 0x20
     *      <br>Characteristic supports indication
     *
     *      <br>GATT_CHARACTERISTIC_PROPERTY_SIGNED_WRITE           (int) 0x30
     *      <br>Characteristic supports write with signature
     *
     *      <br>GATT_CHARACTERISTIC_PROPERTY_EXTENDED_PROPS         (int) 0x40
     *      <br>Characteristic has extended properties
     *
     * @param permissions Characteristic permission. Attribute permissions 
     *  are a combination of access permissions, authentication
     *  permissions and authorization permissions. Possible values are:
     *      <br>GATT_CHARACTERISTIC_PERMISSION_READ                 (int) 0x01
     *      <br>Characteristic read permission
     *
     *      <br>GATT_CHARACTERISTIC_PERMISSION_READ_ENCRYPTED        (int) 0x02
     *      <br>Allow encrypted read operations
     *
     *      <br>GATT_CHARACTERISTIC_PERMISSION_READ_ENCRYPTED_MITM    (int) 0x04
     *      <br>Allow reading with man-in-the-middle protection
     *
     *      <br>GATT_CHARACTERISTIC_PERMISSION_WRITE                  (int) 0x10
     *      <br>Characteristic write permission
     *
     *      <br>GATT_CHARACTERISTIC_PERMISSION_WRITE_ENCRYPTED         (int) 0x20
     *      <br>Allow encrypted writes
     *
     *      <br>GATT_CHARACTERISTIC_PERMISSION_WRITE_ENCRYPTED_MITM    (int) 0x40
     *      <br>Allow encrypted writes with man-in-the-middle protection
     *
     *      <br>GATT_CHARACTERISTIC_PERMISSION_WRITE_SIGNED             (int) 0x80
     *      <br>Allow signed write operations
     *
     *      <br>GATT_CHARACTERISTIC_PERMISSION_WRITE_SIGNED_MITM        (int) 0x100
     *      <br>Allow signed write operations with man-in-the-middle protection
     *
     *  For example, if this characteristic could be read/write with authentication permission,
     *  this value could be set as 0x22. (read encrypted and write encrypted)
     *
     * @return true to indicate the operation is successful, or false erroneous.
     */ 
    boolean reqGattServerAddCharacteristic(in ParcelUuid charUuid, int properties,
                           int permissions);

    /** 
     * Request to add Gatt characteristic descriptor with UUID and permissions with 
     * defined characteristic.
     *
     * @param descUuid descriptor UUID.
     * @param permissions Descriptor permission. Attribute descriptor permissions 
     *  are a combination of access permissions, authentication
     *  permissions and authorization permissions. Possible values are:
     *      <br>GATT_DESCRIPTOR_PERMISSION_READ                 (int) 0x01
     *      <br>Descriptor read permission
     *
     *      <br>GATT_DESCRIPTOR_PERMISSION_READ_ENCRYPTED        (int) 0x02
     *      <br>Allow encrypted read operations
     *
     *      <br>GATT_DESCRIPTOR_PERMISSION_READ_ENCRYPTED_MITM    (int) 0x04
     *      <br>Allow reading with man-in-the-middle protection
     *
     *      <br>GATT_DESCRIPTOR_PERMISSION_WRITE                  (int) 0x10
     *      <br>Descriptor write permission
     *
     *      <br>GATT_DESCRIPTOR_PERMISSION_WRITE_ENCRYPTED         (int) 0x20
     *      <br>Allow encrypted writes
     *
     *      <br>GATT_DESCRIPTOR_PERMISSION_WRITE_ENCRYPTED_MITM    (int) 0x40
     *      <br>Allow encrypted writes with man-in-the-middle protection
     *
     *      <br>GATT_DESCRIPTOR_PERMISSION_WRITE_SIGNED             (int) 0x80
     *      <br>Allow signed write operations
     *
     *      <br>GATT_DESCRIPTOR_PERMISSION_WRITE_SIGNED_MITM        (int) 0x100
     *      <br>Allow signed write operations with man-in-the-middle protection
     *
     *  For example, if this characteristic descriptor could be read/write with authentication permission,
     *  this value could be set as 0x22. (read encrypted and write encrypted)
     *
     * @return true to indicate the operation is successful, or false erroneous.
     */ 
    boolean reqGattServerAddDescriptor(in ParcelUuid descUuid, int permissions);

    /** 
     * Finish Service declaration. The service and related characteristics and descriptors 
     * are registered after end service declaration.
     *
     * @return true to indicate the operation is successful, or false erroneous.
     */ 
    boolean reqGattServerEndServiceDeclaration();

    /** 
     * Request to remove registered Gatt service with service type and UUID.
     * <br>This is an asynchronous call: it will return immediately, and clients should register 
     * and implement callback functions {@link INfCallbackGattServer#onGattServerServiceDeleted onGattServerServiceDeleted} 
     * to be notified of services deleted.
     *
     * @param srvcType service type.
     * @param srvcUuid service UUID.
     *
     * @return true to indicate the operation is successful, or false erroneous.
     */ 
    boolean reqGattServerRemoveService(int srvcType, in ParcelUuid srvcUuid);

    /** 
     * Request to remove all registered Gatt services.
     * <br>This is an asynchronous call: it will return immediately, and clients should register 
     * and implement callback functions {@link INfCallbackGattServer#onGattServerServiceDeleted onGattServerServiceDeleted} 
     * to be notified of services deleted.
     *    
     *
     * @return true to indicate the operation is successful, or false erroneous.
     */ 
    boolean reqGattServerClearServices();

    /** 
     * Request to start or finish advertisement broadcast
     * 
     * <br>This is an asynchronous call: it will return immediately, and clients should register 
     * and implement callback functions {@link INfCallbackGattServer#onGattServerStateChanged onGattServerStateChanged} 
     * to be notified of subsequent profile state changes.
     * @param listen start listen or not.
     *
     * @return true to indicate the operation is successful, or false erroneous.
     */ 
    boolean reqGattServerListen(boolean listen);

    /** 
     * Request to send response with read and write characteristic request to Gatt clients.
     *  
     * @param address valid Bluetooth MAC address.
     * @param requestId The ID of the request that was received with the callback
     * @param status The status of the request to be sent to the remote devices
     *               Possible values are:
     *      <br>GATT_STATUS_SUCCESS                (int) 0x00
     *      <br>Operation success.
     *
     *      <br>GATT_STATUS_INVALID_HANDLE         (int) 0x01
     *      <br>Invalid Handle
     *
     *      <br>GATT_STATUS_READ_NOT_PERMIT        (int) 0x02
     *      <br>Read Not Permitted
     *
     *      <br>GATT_STATUS_WRITE_NOT_PERMIT       (int) 0x03
     *      <br>Write Not Permitted
     *
     *      <br>GATT_STATUS_INVALID_PDU            (int) 0x04
     *      <br>Invalid PDU
     *
     *      <br>GATT_STATUS_INSUF_AUTHENTICATION   (int) 0x05
     *      <br>Insufficient Authentication
     *
     *      <br>GATT_STATUS_REQ_NOT_SUPPORTED      (int) 0x06
     *      <br>Request Not Supported
     *
     *      <br>GATT_STATUS_INVALID_OFFSET         (int) 0x07
     *      <br>Invalid Offset
     *
     *      <br>GATT_STATUS_INSUF_AUTHORIZATION    (int) 0x08
     *      <br>Insufficient Authorization
     *
     *      <br>GATT_STATUS_PREPARE_Q_FULL         (int) 0x09
     *      <br>Prepare Queue Full
     *
     *      <br>GATT_STATUS_NOT_FOUND              (int) 0x0a
     *      <br>Attribute Not Found
     *
     *      <br>GATT_STATUS_NOT_LONG               (int) 0x0b
     *      <br>Attribute Not Long
     *
     *      <br>GATT_STATUS_INSUF_KEY_SIZE         (int) 0x0c
     *      <br>Insufficient Encryption Key Size
     *
     *      <br>GATT_STATUS_INVALID_ATTR_LEN       (int) 0x0d
     *      <br>Invalid Attribute Value Length
     *
     *      <br>GATT_STATUS_ERR_UNLIKELY           (int) 0x0e
     *      <br>Unlikely Error
     *
     *      <br>GATT_STATUS_INSUF_ENCRYPTION       (int) 0x0f
     *      <br>Insufficient Encryption
     *
     *      <br>GATT_STATUS_UNSUPPORT_GRP_TYPE     (int) 0x10
     *      <br>Unsupported Group Type.
     *
     *      <br>GATT_STATUS_INSUF_RESOURCE         (int) 0x11
     *      <br>Insufficient Resources
     *
     * @param offset Value offset for partial read/write response
     * @param value The value of the attribute that was read/written (optional)
     *
     * @return true to indicate the operation is successful, or false erroneous.
     */ 
    boolean reqGattServerSendResponse(String address, int requestId,
                      int status, int offset, in byte[] value);

    /** 
     * Request to send characteristic notification values to Gatt clients.
     *  
     * @param address valid Bluetooth MAC address.
     * @param srvcType service type
     * @param srvcUuid service UUID
     * @param charUuid characteristic UUID
     * @param confirm true to request confirmation from the client (indication),
     *                false to send a notification
     * @param value The value to notify remote device.
     *
     * @return true to indicate the operation is successful, or false erroneous.
     */                   
    boolean reqGattServerSendNotification(String address, int srvcType,
                                 in ParcelUuid srvcUuid,
                                 in ParcelUuid charUuid,
                                 boolean confirm, in byte[] value);

    /** 
     * Get GATT added service UUID list.
     *
     * @return Added Gatt service UUID list
     */ 
    List<ParcelUuid> getGattAddedGattServiceUuidList();

    /** 
     * Get GATT added characteristic UUID list.
     *
     * @param srvcUuid service UUID
     *
     * @return Added Gatt characteristic UUID list
     */ 
    List<ParcelUuid> getGattAddedGattCharacteristicUuidList(in ParcelUuid srvcUuid);

    /** 
     * Get GATT added descriptor UUID list.
     *
     * @param srvcUuid service UUID
     * @param charUuid characteristic UUID
     *
     * @return Added Gatt descriptor UUID list
     */ 
    List<ParcelUuid> getGattAddedGattDescriptorUuidList(in ParcelUuid srvcUuid, in ParcelUuid charUuid);
 
    

}