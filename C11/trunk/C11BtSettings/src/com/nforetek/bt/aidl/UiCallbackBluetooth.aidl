/** 
 * nForeTek Settings Callbacks Interface for Android 4.3
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

/**
 * The callback interface for Bluetooth Settings Service.
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
 * @see INfCommandBluetooth
 */
 

interface UiCallbackBluetooth {

	/** 
	 * Callback to inform Bluetooth Service is ready.
	 */
	void onBluetoothServiceReady();

	/** 
	 * Indicate the state of the local Bluetooth adapter has changed.
     * 
	 * For Bluetooth enable and disable
	 * 		<p><blockquote><b>BT_STATE_OFF</b>	(int) 300
	 * 		<br><b>BT_STATE_TURNING_ON</b>		(int) 301
	 * 		<br><b>BT_STATE_ON</b>				(int) 302
	 *		<br><b>BT_STATE_TURNING_OFF</b>		(int) 303</blockquote>
	 * @param prevState the previous state.
	 * @param newState the new state.
	 */
	void onAdapterStateChanged(int prevState, int newState);

	/** 
	 * Indicate the scan mode of the local Bluetooth adapter has changed.
     * 
	 * <p>Scan mode value are:
	 *		<p><blockquote><b>BT_MODE_NONE</b>			(int) 310
	 *		<br><b>BT_MODE_CONNECTABLE</b>				(int) 311
	 *		<br><b>BT_MODE_CONNECTABLE_DISCOVERABLE</b>	(int) 312</blockquote>
	 *
	 * @param prevState the previous scan state.
	 * @param newState the new scan state.
	 */
	void onAdapterDiscoverableModeChanged(int prevState, int newState);

	/** 
	 * The local Bluetooth adapter has started the remote
     * device discovery process.
     * <p>This usually involves an inquiry scan of about 12 seconds, followed
     * by a page scan of each new device to retrieve its Bluetooth name.
     * <p>Register for {@link onDeviceFound} to be notified as
     * remote Bluetooth devices are found.
     * <p>Device discovery is a heavyweight procedure. New connections to
     * remote Bluetooth devices should not be attempted while discovery is in
     * progress, and existing connections will experience limited bandwidth
     * and high latency. Use {@link INfCommandBluetooth#cancelDiscovery} to cancel an ongoing
     * discovery.
	 */
	void onAdapterDiscoveryStarted();

	/** 
	 * The local Bluetooth adapter has finished the device
     * discovery process.
	 */
	void onAdapterDiscoveryFinished();

	/** 
	 * This callback is the response to {@link INfCommandBluetooth#reqBtPairedDevices reqBtPairedDevices}.
	 *
	 * @param elements the number of paired devices returned. Since Android system only supports 7 paired devices maximal,
	 * this value would be equal to or less than 7.
	 * @param address the address of device in String type. The name, supported profiles, and possible category of n-th device of address 
	 * are placed at n-th element of name, supportProfile, and category correspondingly.
	 * @param name the name of device in String type.
	 * @param supportProfile : the supported profiles of device in int type. Possible values are
	 *		<p><blockquote><b>PROFILE_HSP</b>	(int) 1
	 *		<br><b>PROFILE_HFP</b>				(int) (1 << 1)
	 *		<br><b>PROFILE_A2DP</b>				(int) (1 << 2)
	 * 		<br><b>PROFILE_SPP</b>				(int) (1 << 3)
	 * 		<br><b>PROFILE_PBAP</b>				(int) (1 << 4)
	 *		<br><b>PROFILE_AVRCP</b>			(int) (1 << 5)
	 *		<br><b>PROFILE_FTP</b>				(int) (1 << 6)
	 *		<br><b>PROFILE_MAP</b>				(int) (1 << 7)
	 *		<br><b>PROFILE_AVRCP_13</b>			(int) (1 << 8)
	 *		<br><b>PROFILE_AVRCP_14</b>			(int) (1 << 9)
	 *		<br><b>PROFILE_PANU</b>     		(int) (1 << 10)</blockquote>
	 *	 	 	 
	 * For example, value 7 (0000 0111) means it supports HSP, HFP and A2DP. However, this parameter might be null at the first time 
	 * this device is paired because the records from local cache might be none. In this situation, system would start SDP query
	 * automatically and notify users when SDP records are gotten successfully by calling {@link INfCallbackBluetooth#onDeviceUuidsUpdated onDeviceUuidsUpdated}.
	 * User application needs to register and implement this callback function, too.     
	 * @param category the possible category or class of device. 
	 * <p>Possible values are:
	 *		<p><blockquote><b>CAT_COMPUTER</b>		(byte) 1
	 *		<br><b>CAT_PHONE</b>					(byte) (1 << 1)
	 *		<br><b>CAT_STEREO_AUDIO</b>				(byte) (1 << 2)
	 *		<br><b>CAT_MONO_AUDIO</b>				(byte) (1 << 3)</blockquote>
	 */
	void retPairedDevices(int elements, in String[] address, in String[] name, in int[] supportProfile, in byte[] category);

	/** 
	 * Callback to inform a new device is found. This callback might be the response to 
	 * {@link INfCommandBluetooth#startBtDiscovery startBtDiscovery}.
	 *
	 * @param address the address of found device in String type.
	 * @param name the name of found device in String type if it is available, otherwise return null. Maybe the later updated callback will return the found name.
	 * @param category the possible category or class of found device. 
	 * <p>Possible values are
	 *		<p><blockquote><b>CAT_COMPUTER</b>		(byte) 1
	 *		<br><b>CAT_PHONE</b>					(byte) (1 << 1)
	 *		<br><b>CAT_STEREO_AUDIO</b>				(byte) (1 << 2)
	 *		<br><b>CAT_MONO_AUDIO</b>				(byte) (1 << 3)</blockquote>
	 */
	void onDeviceFound(String address, String name, byte category);
	
	/** 
	 * Callback to inform bonding (pairing) state change of remote device. This callback might be the response to 
	 * {@link INfCommandBluetooth#reqBtPair reqBtPair}.
	 * 
	 * @param address the address of remote bonding device in String type.
	 * @param name the name of remote bonding device in String type.
	 * @param prevState the previous bond state.
	 * @param newState the new bond state.
	 * <p>Bond state possible values are:
	 * <blockquote><b>BOND_NONE</b> 		(int) 330
	 * <br><b>BOND_BONDING</b> 				(int) 331
	 * <br><b>BOND_BONDED</b> 				(int) 332</blockquote>
	 *
	 * <p>Possible situation are:
	 * <br>State changed from <b>BOND_NONE</b> to <b>BOND_BONDING</b> means device is pairing.
	 * <br>State changed from <b>BOND_BONDING</b> to <b>BOND_BONDED</b> means device is paired.
	 * <br>State changed from <b>BOND_BONDING</b> to <b>BOND_NONE</b> means device is pair failed.
	 * <br>State changed from <b>BOND_BONDED</b> to <b>BOND_NONE</b> means device is unpaired.	 
	 */
	void onDeviceBondStateChanged(String address, String name, int prevState, int newState);
	
	/** 
	 * Callback to inform the supported profiles of remote device.
	 *
	 * @param address : the address of bonded device in String type.
	 * @param name : the name of bonded device in String type.
	 * @param supportProfile : the supported profiles of paired device in int type. Possible values are
	 *		<p><blockquote><b>PROFILE_HSP</b>	(int) 1
	 *		<br><b>PROFILE_HFP</b>				(int) (1 << 1)
	 *		<br><b>PROFILE_A2DP</b>				(int) (1 << 2)
	 * 		<br><b>PROFILE_SPP</b>				(int) (1 << 3)
	 * 		<br><b>PROFILE_PBAP</b>				(int) (1 << 4)
	 *		<br><b>PROFILE_AVRCP</b>			(int) (1 << 5)
	 *		<br><b>PROFILE_FTP</b>				(int) (1 << 6)
	 *		<br><b>PROFILE_MAP</b>				(int) (1 << 7)
	 *		<br><b>PROFILE_AVRCP_13</b>			(int) (1 << 8)
	 *		<br><b>PROFILE_AVRCP_14</b>			(int) (1 << 9)
	 *		<br><b>PROFILE_PANU</b>				(int) (1 << 10)</blockquote>	 	 
	 * For example, value 7 (0000 0111) means it supports HSP, HFP and A2DP.
	 */
	void onDeviceUuidsUpdated(String address, String name, int supportProfile);	

	/**
     * The local Bluetooth adapter has changed its friendly
     * Bluetooth name.
     * <p>This name is visible to remote Bluetooth devices.
     * @param name Adapter name
     */
	void onLocalAdapterNameChanged(String name);

	/**
	 * Callback to inform the remote device is moving out of communication range.
	 *
	 * @param address the address of remote bonding device in String type.
	 */
	void onDeviceOutOfRange(String address);

	// Customize
	void onHfpStateChanged(String address, int prevState, int newState);
	void onA2dpStateChanged(String address, int prevState, int newState);
	void onAvrcpStateChanged(String address, int prevState, int newState);

	/**
	 * Callback to inform the local bluetooth role changed.
	 *
	 * @param mode the mode of Bluetooth role in integer type
	 * 		<p><blockquote><b>MODE_CAR</b>		(int) 1
	 *		<br><b>MODE_TABLET</b>				(int) 2
	 */
	void onBtRoleModeChanged(int mode);

}
