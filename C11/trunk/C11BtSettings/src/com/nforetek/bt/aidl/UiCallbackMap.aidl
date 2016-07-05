/** 
 * nForeTek MAP Callbacks Interface for Android 4.3
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

/**
 * The callback interface for Message Access Profile (MAP).
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
 * @see INfCommandMap
 */
  
interface UiCallbackMap {

	/** 
	 * Callback to inform MAP Service is ready.
	 */
	void onMapServiceReady();

	/**
	 * Callback to inform change in MAP state of remote device with given Bluetooth hardware address.
	 * 
	 * The possible values of state in this profile are 
	 * 		<p><blockquote>STATE_NOT_INITIALIZED		(int) 100
	 * 		<br>STATE_READY								(int) 110
	 * 		<br>STATE_CONNECTED_REGISTERED				(int) 150
	 * 		<br>STATE_DOWNLOADING						(int) 160</blockquote>
	 * 
	 * On state STATE_NOT_INITIALIZED and STATE_READY, parameter address might contain unavailable content or 
	 * DEFAULT_ADDRESS , which is 00:00:00:00:00:00.
	 * STATE_CONNECTED implies the message downloading or database updating.
	 *
	 * @param address: Bluetooth MAC address of remote device which involves state changed. Only valid for states greater than STATE_READY.
	 * @param prevState: the previous state. 
	 * @param newState: the new state.
	 * @param reason: the reason of state changed. Possible values are
	 *		<br>MAP_REASON_NONE  											(int) 0
	 * 		<br>MAP_REASON_BAD_PARAMS  					                    (int) 1
	 * 		<br>MAP_REASON_DISCONNECT_FROM_LOCAL					        (int) 2
	 * 		<br>MAP_REASON_DISCONNECT_BY_PEER				                (int) 3	
	 * 		<br>MAP_REASON_INTERRUPT									    (int) 4
	 *		<br>MAP_REASON_DOWNLOAD_FINISH									(int) 5
 	 *		<br>MAP_REASON_PEER_NO_MAP_SERVICE								(int) 6
	 *		<br>MAP_REASON_TIMEOUT								            (int) 7
	 *		<br>MAP_REASON_DOWNLOAD_FAIL								    (int) 8
	 * Otherwise, the reason is -1.
     */
    void onMapStateChanged(String address, int prevState, int newState, int reason);    
    
    /**
	 * Callback to inform response to {@link INfCallbackMap#reqMapDownloadAllMessages reqMapDownloadAllMessages} or
	 * {@link INfCallbackMap#reqMapDownloadSingleMessage reqMapDownloadSingleMessage}
	 * for remote connected device with given Bluetooth hardware address.
	 *
     * @param address Bluetooth MAC address of remote device.
     * @param handle MAP message handle, use this to download detail message. Change when disconnected.
     * @param senderName sender name.
     * @param senderNumber sender number.
     * @param recipientNumber recipient number.
     * @param date message receive date, ex: 20101010T101010Z means 2015/10/10 10:10:10
     * @param type message type.
     * @param folder which folder message download from. Possible values are
     *		<br>MAP_FOLDER_STRUCTURE_INBOX					(int) 0
	 * 		<br>MAP_FOLDER_STRUCTURE_SENT					(int) 1
	 * @param isReadStatus indicates the message is read (=true) or unread (=false).
     * @param subject message subject.
     * @param message message content.
     */
	void retMapDownloadedMessage(String address, String handle, String senderName, String senderNumber, String recipientNumber, String date, int type, int folder, boolean isReadStatus, String subject, String message);
    
    /**
	 * Callback to inform the new message received on the remote device.
	 * This callback might be invocated due to {@link INfCommandMap#reqMapRegisterNotification reqMapRegisterNotification}.
	 * 
	 * If parameter downloadNewMessage of {@link INfCommandMap#reqMapRegisterNotification reqMapRegisterNotification} is 
	 * set to false, the parameter sender and message would be empty string.
	 *
	 * @param address Bluetooth MAC address of remote device.
     * @param handle MAP message handle.
     * @param sender Sender of the message. 
     * @param message Message content. 
     * The parameters "sender" and "message" are only available if parameter downloadNewMessage of {@link INfCommandMap#reqMapRegisterNotification reqMapRegisterNotification} is set to true. 
     */
    void onMapNewMessageReceivedEvent(String address, String handle, String sender, String message);
      
    /**
	 * Callback to inform the specified number of messages have been downloaded from the remote device.
	 * 
	 * This callback might be invocated due to {@link INfCommandMap#setMapDownloadNotify setMapDownloadNotify} with 
	 * non-zero parameter notifyFreq set.
     * 
	 * @param address Bluetooth MAC address of remote device.
	 * @param folder which folder message download from. Possible values are
     *		<br>MAP_FOLDER_STRUCTURE_INBOX					(int) 0
	 * 		<br>MAP_FOLDER_STRUCTURE_SENT					(int) 1
	 * @param totalMessages total number of messages would be downloaded
	 * @param currentMessages the number of messages have been downloaded
     */
    void onMapDownloadNotify(String address, int folder, int totalMessages, int currentMessages);
    
     /**
	 * Callback to inform response to {@link INfCommandMap#reqMapDatabaseAvailable reqMapDatabaseAvailable}
	 * 
	 * When this callback is invocated, it means database is available.
	 *
     */
    void retMapDatabaseAvailable();

    /**
     *  Callback to inform response to {@link INfCommandMap#reqMapDeleteDatabaseByAddress reqMapDeleteDatabaseByAddress}
     *
     *  @param address which address delete from table
     *  @param isSuccess means success or not
     */
    void retMapDeleteDatabaseByAddressCompleted(String address, boolean isSuccess);        
    
    /**
	 * Callback to inform response to {@link INfCommandMap#reqMapCleanDatabase reqMapCleanDatabase}
	 * when database has been cleaned.
	 *
	 *  @param isSuccess means success or not
	 */	
	void retMapCleanDatabaseCompleted(boolean isSuccess);
    
    /**
	 * Callback to inform response to {@link INfCommandMap#reqMapSendMessage reqMapSendMessage}
	 *
	 *  @param address Bluetooth MAC address of remote device.
	 *  @param target phone number of target.
	 *  @param state Success or failed reason.
	 */	
	void retMapSendMessageCompleted(String address, String target, int state);
    
    /**
	 * Callback to inform response to {@link INfCommandMap#reqMapDeleteMessage reqMapDeleteMessage}
	 *
	 *  @param address Bluetooth MAC address of remote device.
	 *  @param handle MAP handle.
	 *  @param reason the reason of state changed. Possible values are
	 *		<br>REASON_SUCCESS											(int) 0
	 * 		<br>REASON_BAD_HANDLE										(int) 1
	 * 		<br>REASON_TIMEOUT											(int) 2
	 * 		<br>REASON_DELETE_FAIL										(int) 3
	 * Otherwise, the reason is -1.
	 */	
	void retMapDeleteMessageCompleted(String address, String handle, int reason);
    
    /**
	 * Callback to inform response to {@link INfCommandMap#reqChangeMapReadStatus reqChangeMapReadStatus}
	 *
	 * @param address Bluetooth MAC address of remote device.
	 * @param handle MAP handle.
	 * @param reason the reason of state changed. Possible values are
	 *		<br>REASON_SUCCESS											(int) 0
	 * 		<br>REASON_BAD_HANDLE										(int) 1
	 * 		<br>REASON_TIMEOUT											(int) 2
	 * 		<br>REASON_CHANGE_STATUS_FAIL								(int) 3
	 * Otherwise, the reason is -1.
	 */	
	void retMapChangeReadStatusCompleted(String address, String handle, int reason);

	/**
	 * Callback to inform the memory available on the remote device.
	 * This callback might be invocated due to {@link INfCommandMap#reqMapRegisterNotification reqMapRegisterNotification}.
	 * 
	 *
	 * @param address Bluetooth MAC address of remote device.
     * @param structure which folder is available. Possible values are
	 *		<br>MAP_FOLDER_STRUCTURE_INBOX										(int) 0
	 * 		<br>MAP_FOLDER_STRUCTURE_SENT										(int) 1
	 * 		<br>MAP_FOLDER_STRUCTURE_DELETED									(int) 2
	 * 		<br>MAP_FOLDER_STRUCTURE_OUTBOX										(int) 3
	 * 		<br>MAP_FOLDER_STRUCTURE_DRAFT										(int) 4
     * @param available true means memory available, and false means memory full.
     */
    void onMapMemoryAvailableEvent(String address, int structure, boolean available);

    /**
	 * Callback to inform the message sending status on the remote device.
	 * This callback might be invocated due to {@link INfCommandMap#reqMapRegisterNotification reqMapRegisterNotification}.
	 * 
	 *
	 * @param address Bluetooth MAC address of remote device.
     * @param handle MAP handle.
     * @param isSuccess sending success or not.
     */
    void onMapMessageSendingStatusEvent(String address, String handle, boolean isSuccess);

    /**
	 * Callback to inform the message delivery status on the remote device.
	 * This callback might be invocated due to {@link INfCommandMap#reqMapRegisterNotification reqMapRegisterNotification}.
	 * 
	 *
	 * @param address Bluetooth MAC address of remote device.
     * @param handle MAP handle.
     * @param isSuccess deliver success or not
     */
    void onMapMessageDeliverStatusEvent(String address, String handle, boolean isSuccess);

    /**
	 * Callback to inform the message shifted on the remote device.
	 * This callback might be invocated due to {@link INfCommandMap#reqMapRegisterNotification reqMapRegisterNotification}.
	 * 
	 *
	 * @param address Bluetooth MAC address of remote device.
	 * @param handle MAP handle.
     * @param type Possible values are
	 *		<br>MAP_TYPE_SMS_GSM										(int) 0
	 * 		<br>MAP_TYPE_SMS_CDMA										(int) 1
	 * 		<br>MAP_TYPE_SMS_MMS										(int) 2
	 * 		<br>MAP_TYPE_SMS_EMAIL										(int) 3
     * @param newFolder shift to which folder. Possible values are
	 *		<br>MAP_FOLDER_STRUCTURE_INBOX										(int) 0
	 * 		<br>MAP_FOLDER_STRUCTURE_SENT										(int) 1
	 * 		<br>MAP_FOLDER_STRUCTURE_DELETED									(int) 2
	 * 		<br>MAP_FOLDER_STRUCTURE_OUTBOX										(int) 3
	 * 		<br>MAP_FOLDER_STRUCTURE_DRAFT										(int) 4
	 * @param oldFolder shift from which folder. Possible values are
	 *		<br>MAP_FOLDER_STRUCTURE_INBOX										(int) 0
	 * 		<br>MAP_FOLDER_STRUCTURE_SENT										(int) 1
	 * 		<br>MAP_FOLDER_STRUCTURE_DELETED									(int) 2
	 * 		<br>MAP_FOLDER_STRUCTURE_OUTBOX										(int) 3
	 * 		<br>MAP_FOLDER_STRUCTURE_DRAFT										(int) 4
     */
    void onMapMessageShiftedEvent(String address, String handle, int type, int newFolder, int oldFolder);

    /**
	 * Callback to inform the message deleted on the remote device.
	 * This callback might be invocated due to {@link INfCommandMap#reqMapRegisterNotification reqMapRegisterNotification}.
	 * 
	 *
	 * @param address Bluetooth MAC address of remote device.
	 * @param handle MAP handle.
     * @param type Possible values are
	 *		<br>MAP_TYPE_SMS_GSM										(int) 0
	 * 		<br>MAP_TYPE_SMS_CDMA										(int) 1
	 * 		<br>MAP_TYPE_SMS_MMS										(int) 2
	 * 		<br>MAP_TYPE_SMS_EMAIL										(int) 3
     * @param folder delete from which folder. Possible values are
	 *		<br>MAP_FOLDER_STRUCTURE_INBOX										(int) 0
	 * 		<br>MAP_FOLDER_STRUCTURE_SENT										(int) 1
	 * 		<br>MAP_FOLDER_STRUCTURE_DELETED									(int) 2
	 * 		<br>MAP_FOLDER_STRUCTURE_OUTBOX										(int) 3
	 * 		<br>MAP_FOLDER_STRUCTURE_DRAFT										(int) 4
     */
    void onMapMessageDeletedEvent(String address, String handle, int type, int folder);
	
}
