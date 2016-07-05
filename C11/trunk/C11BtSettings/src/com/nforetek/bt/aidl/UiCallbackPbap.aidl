/** 
 * nForeTek PAN Callbacks Interface for Android 4.3
 *
 * Copyright (C) 2011-2019  nForeTek Corporation
 *
 * Zeus 0.0.0 on 20140613
 * @author KC Huang	<kchuang@nforetek.com>
 * @author Piggy	<piggylee@nforetek.com>
 * @version 0.0.0
 *
 */
 
package com.nforetek.bt.aidl;

import com.nforetek.bt.aidl.NfPbapContact;

/**
 * The callback interface for Phone Book Access Profile (PBAP).
 * <br>UI program should implement all methods of this interface 
 * for receiving possible callbacks from nFore service.
 * <br>The naming principle of callback in this Doc is as below,
 *		<blockquote><b>retXXX()</b> : must be the callback of requested API.
 *		<br>		<b>onXXX()</b>  : could be the callback for updated values or the callback from requested API.</blockquote>
 *
 * <p> The constant variables in this Doc could be found and referred by importing
 * 		<br><blockquote>com.nforetek.bt.res.NfDef</blockquote>
 * <p> with prefix NfDef class name. Ex : <code>NfDef.DEFAULT_ADDRESS</code>
 *
 * <p> Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
 *
 * @see INfCommandPbap
 */
 
 
interface UiCallbackPbap {

	/** 
	 * Callback to inform PBAP Service is ready.
	 */
	void onPbapServiceReady();
    
	/**
	 * Callback to inform change in PBAP state of remote connected device with given Bluetooth hardware address.
	 * <p>The possible values of state in this profile are:
	 * 		<p><blockquote><b>STATE_NOT_INITIALIZED</b>		(int) 100
	 * 		<br><b>STATE_READY</b>							(int) 110
	 *		<br><b>STATE_DOWNLOADING</b>					(int) 140</blockquote>
	 * 
	 * <br>Parameter address is not valid for <b>STATE_NOT_INITIALIZED</b> and <b>STATE_READY</b>.
	 * <br>It might contain unavailable content or <b>DEFAULT_ADDRESS</b> , which is 00:00:00:00:00:00.
	 *
	 * @param address Bluetooth MAC address of remote device which involves state changed.
	 * @param prevState the previous state. 
	 * @param newState the new state.
	 * @param reason the reason of other states changed back to <b>STATE_READY</b>, which means download finished. Possible values are:
	 * 		<p><blockquote><b>REASON_DOWNLOAD_FULL_CONTENT_COMPLETED</b>	(int) 1
	 * 		<br><b>REASON_DOWNLOAD_FAILED</b>								(int) 2
	 * 		<br><b>REASON_DOWNLOAD_TIMEOUT</b>								(int) 3
	 * 		<br><b>REASON_DOWNLOAD_USER_REJECT</b>							(int) 4</blockquote>
	 * Otherwise, the reason is -1.
	 * @param counts the number of vCards downloaded or updated.
	 * This parameter is only available and meaningful when state changed from <b>STATE_DOWNLOADING</b> back to <b>STATE_READY</b>.
	 * Otherwise, the counts is -1.
     */
    void onPbapStateChanged(String address, int prevState, int newState, int reason, int counts);

    /**
	 * Callback to inform response to {@link INfCommandPbap#reqPbapDownload reqPbapDownload}
	 * for remote connected device with given Bluetooth hardware address.
	 *
     * @param address Bluetooth MAC address of remote device.
     * @param firstName means first name of this contact.
     * @param middleName contact middle name.
     * @param lastName contact last name.
     * @param numbers numbers mean a number array of the total number of this contact.
     * @param types types mean a number type array of above numbers.
     * @param photoBytes mean a byte array of contact photo.
     * @param photoType possible photo type are:
     *		<blockquote><b>PBAP_PHOTO_TYPE_NULL</b>	(int) 0
	 *		<br><b>PBAP_PHOTO_TYPE_URI</b>			(int) 1
	 *		<br><b>PBAP_PHOTO_TYPE_URL</b>			(int) 2
	 *		<br><b>PBAP_PHOTO_TYPE_JPEG</b>			(int) 3
	 *		<br><b>PBAP_PHOTO_TYPE_GIF</b>			(int) 4</blockquote>
     */
	void retPbapDownloadedContact(in NfPbapContact contact);

	/**
	 * Callback to inform response to {@link INfCommandPbap#reqPbapDownload reqPbapDownload}
	 * for remote connected device with given Bluetooth hardware address.
	 *
     * @param address Bluetooth MAC address of remote device.
     * @param firstName means first name of this contact.
     * @param middleName contact middle name.
     * @param lastName contact last name.
     * @param number number mean the number of this call log.
     * @param type possible storage type are:
	 *		<blockquote><b>PBAP_STORAGE_SIM</b>			(int) 1
	 *		<br><b>PBAP_STORAGE_PHONE_MEMORY</b>		(int) 2
	 *		<br><b>PBAP_STORAGE_MISSED_CALLS</b>		(int) 3
	 *		<br><b>PBAP_STORAGE_RECEIVED_CALLS</b>		(int) 4
	 *		<br><b>PBAP_STORAGE_DIALED_CALLS</b>		(int) 5
	 *		<br><b>PBAP_STORAGE_CALL_LOGS</b>			(int) 6</blockquote>	
     * @param timestamp call log timestamp ex: 20101010T101010Z means 2015/10/10 10:10:10
     */
	void retPbapDownloadedCallLog(String address, String firstName, String middleName, String lastName, String number, int type, String timestamp);

	/**
	 * Callback to inform the specified number of vcards have been downloaded from the remote device and save to local database.
	 * 
	 * This callback might be invocated due to 
	 * {@link INfCommandPbap#reqPbapDownload reqPbapDownload}, 
	 * {@link INfCommandPbap#reqPbapDownloadRange reqPbapDownloadRange},
	 * {@link INfCommandPbap#reqPbapDownloadToDatabase reqPbapDownloadToDatabase},
	 * {@link INfCommandPbap#reqPbapDownloadRangeToDatabase reqPbapDownloadRangeToDatabase},
	 * {@link INfCommandPbap#reqPbapDownloadToContactsProvider reqPbapDownloadToContactsProvider} or 
	 * {@link INfCommandPbap#reqPbapDownloadRangeToContactsProvider reqPbapDownloadRangeToContactsProvider}  
	 * and {@link INfCommandPbap#setPbapDownloadNotify setPbapDownloadNotify} with non-zero parameter frequency set.
     * 
	 * @param address Bluetooth MAC address of remote device.
	 * @param storage possible storage type are:
	 *		<blockquote><b>PBAP_STORAGE_SIM</b>			(int) 1
	 *		<br><b>PBAP_STORAGE_PHONE_MEMORY</b>		(int) 2
	 *		<br><b>PBAP_STORAGE_MISSED_CALLS</b>		(int) 3
	 *		<br><b>PBAP_STORAGE_RECEIVED_CALLS</b>		(int) 4
	 *		<br><b>PBAP_STORAGE_DIALED_CALLS</b>		(int) 5
	 *		<br><b>PBAP_STORAGE_CALL_LOGS</b>			(int) 6</blockquote>	
	 * @param totalContacts total number of contacts would be downloaded. If not support, number will be zero.
	 * @param downloadedContacts the number of contacts have been downloaded
     */
    void onPbapDownloadNotify(String address, int storage, int totalContacts, int downloadedContacts);
        
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

	/**
	 * Callback to inform response to {@link INfCommandPbap#reqPbapDatabaseQueryNameByPartialNumber reqPbapDatabaseQueryNameByPartialNumber}
	 * for remote connected device with given Bluetooth hardware address.
	 *
     * @param address Bluetooth MAC address of remote device.
     * @param target the queried phone number.     
     * @param names the corresponding name of specified phone name array. This name is meaningful only if isSuccessed is true.
     * @param numbers the corresponding number of specified phone number array. This number is meaningful only if isSuccessed is true.
     * @param isSuccess indicates that if the corresponding name is retrieved. 
     */
	void retPbapDatabaseQueryNameByPartialNumber(String address, String target, in String[] names, in String[] numbers, boolean isSuccess);

	/**
	 * Callback to inform response to {@link INfCommandPbap#reqPbapDatabaseAvailable reqPbapDatabaseAvailable}
	 * for remote connected device.
	 * <p>When this callback is invocated, it means nFore's PBAP database is available for query.
	 * 
	 * <p>When nFore's PBAP database is required to access, this command has to be issued in advanced and wait for 
	 * the callback {@link INfCallbackPbap#retPbapDatabaseAvailable retPbapDatabaseAvailable}. Or the database may be crashed!
	 * <br>After nFore's PBAP database is done accessing, the database resource needs to be released. And should <b>never</b> use commands
	 * {@link INfCommandPbap#reqPbapDownloadToDatabase reqPbapDownloadToDatabase}, 
	 * {@link INfCommandPbap#reqPbapDownloadToContactsProvider reqPbapDownloadToContactsProvider},
	 * {@link INfCommandPbap#reqPbapDatabaseQueryNameByNumber reqPbapDatabaseQueryNameByNumber} or 
	 * {@link INfCommandPbap#reqPbapDatabaseQueryNameByPartialNumber reqPbapDatabaseQueryNameByPartialNumber} 
	 * before the database resource is released. 
	 *
	 * @param address Bluetooth MAC address of remote device.
     */
    void retPbapDatabaseAvailable(String address);
    
	/**
	 * Callback to inform response to {@link INfCommandPbap#reqPbapDeleteDatabaseByAddress reqPbapDeleteDatabaseByAddress}
	 * when database has been deleted.
	 *
	 * @param address Bluetooth MAC address of remote device which just completed deleted 
	 * @param isSuccess indicate that if deletion is completed successfully, or false erroneous.
	 */	
	void retPbapDeleteDatabaseByAddressCompleted(String address, boolean isSuccess);

    /**
	 * Callback to inform response to {@link INfCommandPbap#reqPbapCleanDatabase reqPbapCleanDatabase}
	 * when database has been cleaned.
	 *
	 * @param isSuccess indicate that if database is cleaned successfully, or false erroneous.
	 */	
	void retPbapCleanDatabaseCompleted(boolean isSuccess);

}
