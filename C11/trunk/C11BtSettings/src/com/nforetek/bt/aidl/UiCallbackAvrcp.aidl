/** 
 * nForeTek AVRCP Callbacks Interface for Android 4.3
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
 * The callback interface for Audio Video Remote Control Profile (AVRCP).
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
 * <p>If the callback has prefix number 13 or 14, it means the callback supports AVRCP 1.3 or 1.4.
 * <br>For example, like {@link #reqAvrcp14GetFolderItems reqAvrcp14GetFolderItems} is means it is available in AVRCP 1.4.
 *
 * @see INfCommandAvrcp
 */

interface UiCallbackAvrcp {

	/** 
	 * Callback to inform AVRCP Service is ready.
	 */
	void onAvrcpServiceReady();

	/** 
	 * Callback to inform state change of AVRCP connected remote device.
	 * <p>The possible values of state in this callback are 
	 * 		<blockquote><b>STATE_NOT_INITIALIZED</b>		(int) 100
	 * 		<br><b>STATE_READY</b>							(int) 110
	 *		<br><b>STATE_CONNECTED</b>						(int) 140
 	 *		<br><b>STATE_BROWSING</b>						(int) 145</blockquote>
	 * <br>The state STATE_BROWSING implies connected and browsing.
	 * <br>If applications only requires functionalities of AVRCP on version 1.3 or lower, 
	 * the STATE_CONNECTED is sufficient for corresponding APIs.
	 * <br>However, if the functionalities of AVRCP on version 1.4 are needed, the STATE_BROWSING is necessary.
	 *
	 * <br>Parameter address is not valid for <b>STATE_NOT_INITIALIZED</b> and <b>STATE_READY</b>.
	 * <br>It might contain unavailable content or <b>DEFAULT_ADDRESS</b> , which is 00:00:00:00:00:00.
	 *
	 * @param address Bluetooth MAC address of remote device which involves state changed.
	 * @param prevState the previous state. 
	 * @param newState the new state.
	 */	
	void onAvrcpStateChanged(String address, int prevState, int newState);
	
	
	/* =============================================================================================================================== 
	 * Callback function of AVRCP v1.3 related operations
	 */	
	
	/** 
	 * Callback to inform response to {@link INfCommandAvrcp#reqAvrcp13GetCapabilitiesSupportEvent reqAvrcp13GetCapabilitiesSupportEvent}
	 * from A2DP/AVRCP connected remote device.
	 *
	 * @param eventIds the events supported on remote device. Possible values are:
	 *		<blockquote><b>AVRCP_EVENT_ID_PLAYBACK_STATUS_CHANGED</b>		(byte) 0x01
	 *		<br><b>AVRCP_EVENT_ID_TRACK_CHANGED</b>							(byte) 0x02
	 *		<br><b>AVRCP_EVENT_ID_TRACK_REACHED_END</b>						(byte) 0x03
	 *		<br><b>AVRCP_EVENT_ID_TRACK_REACHED_START</b>					(byte) 0x04
	 *		<br><b>AVRCP_EVENT_ID_PLAYBACK_POS_CHANGED</b>					(byte) 0x05
	 *		<br><b>AVRCP_EVENT_ID_BATT_STATUS_CHANGED</b>					(byte) 0x06
	 *		<br><b>AVRCP_EVENT_ID_SYSTEM_STATUS_CHANGED</b>					(byte) 0x07
	 *		<br><b>AVRCP_EVENT_ID_PLAYER_APPLICATION_SETTING_CHANGED</b>	(byte) 0x08
	 *		<br><b>AVRCP_EVENT_ID_NOW_PLAYING_CONTENT_CHANGED</b>			(byte) 0x09
	 *		<br><b>AVRCP_EVENT_ID_AVAILABLE_PLAYERS_CHANGED</b>				(byte) 0x0a
	 *		<br><b>AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED</b>				(byte) 0x0b
	 *		<br><b>AVRCP_EVENT_ID_UIDS_CHANGED</b>							(byte) 0x0c
	 *		<br><b>AVRCP_EVENT_ID_VOLUME_CHANGED</b>						(byte) 0x0d</blockquote>
	 */		
	void retAvrcp13CapabilitiesSupportEvent(in byte[] eventIds);
	
	/** 
	 * Callback to inform response to {@link INfCommandAvrcp#reqAvrcp13GetPlayerSettingAttributesList reqAvrcp13GetPlayerSettingAttributesList}
	 * from A2DP/AVRCP connected remote device.
	 *
	 * @param attributeIds the attributes supported on remote device. Possible values are:
	 * 		<blockquote><b>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER</b>		(byte) 0x01
	 *		<br><b>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE</b>			(byte) 0x02
	 *		<br><b>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE</b>				(byte) 0x03
	 *		<br><b>AVRCP_SETTING_ATTRIBUTE_ID_SCAN</b>					(byte) 0x04</blockquote>
	 */		
	void retAvrcp13PlayerSettingAttributesList(in byte[] attributeIds);
	
	/** 
	 * Callback to inform response to {@link INfCommandAvrcp#reqAvrcp13GetPlayerSettingValuesList reqAvrcp13GetPlayerSettingValuesList}
	 * from A2DP/AVRCP connected remote device.
	 *
	 * @param attributeId the attribute to which the valueIds belong. Possible values are:
	 * 		<blockquote><b>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER</b>					(byte) 0x01
	 *		<br><b>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE</b>						(byte) 0x02
	 *		<br><b>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE</b>							(byte) 0x03
	 *		<br><b>AVRCP_SETTING_ATTRIBUTE_ID_SCAN</b>								(byte) 0x04</blockquote>
	 * @param valueIds the values supported by attributeId on remote device. Possible values are
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
	 */		
	void retAvrcp13PlayerSettingValuesList(byte attributeId, in byte[] valueIds);
	
	/** 
	 * Callback to inform response to {@link INfCommandAvrcp#reqAvrcp13GetPlayerSettingCurrentValues reqAvrcp13GetPlayerSettingCurrentValues}
	 * from A2DP/AVRCP connected remote devices and
	 * an attribute attributeIds's n-th value is placed at valueIds's n-th element.
	 *
	 * @param attributeIds the attributes to which each valueId of valueIds belongs. Possible values are:
	 * 		<blockquote><b>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER</b></b>				(byte) 0x01
	 *		<br><b>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE</b>						(byte) 0x02
	 *		<br><b>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE</b>							(byte) 0x03
	 *		<br><b>AVRCP_SETTING_ATTRIBUTE_ID_SCAN</b>								(byte) 0x04</blockquote>
	 * @param valueIds the current values of attributeId on remote device. Possible values are
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
	 */	
	void retAvrcp13PlayerSettingCurrentValues(in byte[] attributeIds, in byte[] valueIds);
	
	/** 
	 * Callback to inform response to {@link INfCommandAvrcp#reqAvrcp13SetPlayerSettingValue reqAvrcp13SetPlayerSettingValue}
	 * from A2DP/AVRCP connected remote device.
	 *
	 * <P>This means that the previously requested command {@link INfCommandAvrcp#reqAvrcp13SetPlayerSettingValue reqAvrcp13SetPlayerSettingValue}
	 * is accepted successfully by remote device.
	 *
	 */		
	void retAvrcp13SetPlayerSettingValueSuccess();
	
	/** 
	 * Callback to inform response to {@link INfCommandAvrcp#reqAvrcp13GetElementAttributesPlaying reqAvrcp13GetElementAttributesPlaying}
	 * from A2DP/AVRCP connected remote device.
	 *
 	 * @param metadataAtrributeIds list of media attributes IDs. These IDs are used to uniquely identify media information. Possible values are:
 	 *		<blockquote><b>AVRCP_META_ATTRIBUTE_ID_TITLE</b>		(int) 0x01 : title of the media
     *		<br><b>AVRCP_META_ATTRIBUTE_ID_ARTIST</b> 				(int) 0x02 : name of the artist
     *		<br><b>AVRCP_META_ATTRIBUTE_ID_ALBUM</b> 				(int) 0x03 : name of the album
     *		<br><b>AVRCP_META_ATTRIBUTE_ID_NUMBER_OF_MEDIA</b> 		(int) 0x04 : number of the media, i.e. track number of the CD
     *		<br><b>AVRCP_META_ATTRIBUTE_ID_TOTAL_NUMBER_OF_MEDIA</b>(int) 0x05 : total number of the media, i.e. Total track number of the CD
     *		<br><b>AVRCP_META_ATTRIBUTE_ID_GENRE</b> 				(int) 0x06 : genre
     *		<br><b>AVRCP_META_ATTRIBUTE_ID_SONG_LENGTH</b> 			(int) 0x07 : playing time in milliseconds</blockquote>
  	 * @param texts the result of string type to corresponding metadata attribute ID.
	 */			
	void retAvrcp13ElementAttributesPlaying(in int[] metadataAtrributeIds, in String[] texts);
	
	/** 
	 * Callback to inform response to {@link INfCommandAvrcp#reqAvrcp13GetPlayStatus reqAvrcp13GetPlayStatus}
	 * from A2DP/AVRCP connected remote device.
	 *
 	 * @param songLen the total length of the playing song in milliseconds. Values allowed are from 0 to (2^32 - 1).
  	 * @param songPos the current position of the playing in milliseconds elapsed. Values allowed are from 0 to (2^32 - 1).
  	 * @param statusId the current status of playing. Possible values are:
  	 *		<blockquote><b>AVRCP_PLAYING_STATUS_ID_STOPPED</b> 		(byte) 0x00 : stopped
	 *		<br><b>AVRCP_PLAYING_STATUS_ID_PLAYING</b> 				(byte) 0x01 : playing
	 *		<br><b>AVRCP_PLAYING_STATUS_ID_PAUSED</b> 				(byte) 0x02 : paused
	 *		<br><b>AVRCP_PLAYING_STATUS_ID_FWD_SEEK</b> 			(byte) 0x03 : fwd seek
	 *		<br><b>AVRCP_PLAYING_STATUS_ID_REW_SEEK</b> 			(byte) 0x04 : rev seek
	 *		<br><b>AVRCP_PLAYING_STATUS_ID_ERROR</b> 				(byte) 0xFF : error</blockquote> 	 
	 */		
	void retAvrcp13PlayStatus(long songLen, long songPos, byte statusId);

	/** 
	 * Callback to inform register event watcher success.
	 *
	 * <p>This callback would be invoked only if register event watcher by using
	 * {@link INfCommandAvrcp#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
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
	 */		
	void onAvrcp13RegisterEventWatcherSuccess(byte eventId);

	/** 
	 * Callback to inform register event watcher fail.
	 *
	 * <p>This callback would be invoked only if register event watcher by using
	 * {@link INfCommandAvrcp#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
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
	 */		
	void onAvrcp13RegisterEventWatcherFail(byte eventId);
	
	/** 
	 * Callback to inform change in playback status of A2DP/AVRCP connected remote device.
	 *
	 * <p>This callback would be invoked only if event AVRCP_EVENT_ID_PLAYBACK_STATUS_CHANGED was registered before by using
	 * {@link INfCommandAvrcp#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 *
  	 * @param statusId the current status of playback. Possible values are
  	 *		<blockquote><b>AVRCP_PLAYING_STATUS_ID_STOPPED</b> 		(byte) 0x00 : stopped
	 *		<br><b>AVRCP_PLAYING_STATUS_ID_PLAYING</b> 				(byte) 0x01 : playing
	 *		<br><b>AVRCP_PLAYING_STATUS_ID_PAUSED</b> 				(byte) 0x02 : paused
	 *		<br><b>AVRCP_PLAYING_STATUS_ID_FWD_SEEK</b> 			(byte) 0x03 : fwd seek
	 *		<br><b>AVRCP_PLAYING_STATUS_ID_REW_SEEK</b> 			(byte) 0x04 : rev seek
	 *		<br><b>AVRCP_PLAYING_STATUS_ID_ERROR</b> 				(byte) 0xFF : error</blockquote>  	 
	 */		
	void onAvrcp13EventPlaybackStatusChanged(byte statusId);

	/** 
	 * Callback to inform change in track of A2DP/AVRCP connected remote device.
	 * <p>This callback would be invoked only if event <b>AVRCP_EVENT_ID_TRACK_CHANGED</b> was registered before by using
	 * {@link INfCommandAvrcp#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 *
  	 * @param statusId the index of current track. 	 
	 */		
	void onAvrcp13EventTrackChanged(long elementId);

	/** 
	 * Callback to inform end of the current track of A2DP/AVRCP connected remote device.
	 *
	 * <p>This callback would be invoked only if event <b>AVRCP_EVENT_ID_TRACK_REACHED_END</b> was registered before by using
	 * {@link INfCommandAvrcp#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 * 
	 */		
	void onAvrcp13EventTrackReachedEnd();
	
	/** 
	 * Callback to inform start of the current track of A2DP/AVRCP connected remote device.
	 *
	 * <p>This callback would be invoked only if event <b>AVRCP_EVENT_ID_TRACK_REACHED_START</b> was registered before by using
	 * {@link INfCommandAvrcp#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 *	 
	 */		
	void onAvrcp13EventTrackReachedStart();

	/** 
	 * Callback to inform change in playback position of A2DP/AVRCP connected remote device.
	 *
	 * <p>This callback would be invoked only if event <b>AVRCP_EVENT_ID_PLAYBACK_POS_CHANGED</b> was registered before by using
	 * {@link INfCommandAvrcp#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 * Besides, this callback shall be notified in the following conditions:
	 * 		<blockquote>remote device has reached the registered playback interval time
	 * 		<br>change in play status.
	 * 		<br>change in current track.
	 *		<br>reached end or beginning of track.</blockquote>
	 *
 	 * @param songPos current playback position in millisecond.	 
	 */			
	void onAvrcp13EventPlaybackPosChanged(long songPos);

	/** 
	 * Callback to inform change in battery status of A2DP/AVRCP connected remote device.
	 *
	 * <p>This callback would be invoked only if event <b>AVRCP_EVENT_ID_BATT_STATUS_CHANGED</b> was registered before by using
	 * {@link INfCommandAvrcp#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 *
 	 * @param statusId battery status. possible values are:
	 *		<blockquote><b>AVRCP_BATTERY_STATUS_ID_NORMAL</b>		(byte) 0x00 : normal 	 
 	 *		<br><b>AVRCP_BATTERY_STATUS_ID_WARNING</b>					(byte) 0x01 : warning
	 *		<br><b>AVRCP_BATTERY_STATUS_ID_CRITICAL</b>					(byte) 0x02 : critical
	 *		<br><b>AVRCP_BATTERY_STATUS_ID_EXTERNAL</b>					(byte) 0x03 : external
	 *		<br><b>AVRCP_BATTERY_STATUS_ID_FULL</b>						(byte) 0x04 : full charged</blockquote>
	 */		
	void onAvrcp13EventBatteryStatusChanged(byte statusId);

	/** 
	 * Callback to inform change in system status of A2DP/AVRCP connected remote device.
	 *
	 * <p>This callback would be invoked only if event <b>AVRCP_EVENT_ID_SYSTEM_STATUS_CHANGED</b> was registered before by using
	 * {@link INfCommandAvrcp#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 *
 	 * @param statusId system status. possible values are:
	 *		<blockquote><b>AVRCP_SYSTEM_STATUS_ID_POWER_ON</b> 		(byte) 0x00 : power on
	 *      <br><b>AVRCP_SYSTEM_STATUS_ID_POWER_OFF</b> 			(byte) 0x01 : power off
	 *      <br><b>AVRCP_SYSTEM_STATUS_ID_POWER_UNPLUGGED</b> 		(byte) 0x02 : unplugged</blockquote>
	 */		
	void onAvrcp13EventSystemStatusChanged(byte statusId);

	/** 
	 * Callback to inform change in player application settings of A2DP/AVRCP connected remote device.
	 * 
	 * <p>This callback would be invoked only if event <b>AVRCP_EVENT_ID_PLAYER_APPLICATION_SETTING_CHANGED</b> was registered before by using
	 * {@link INfCommandAvrcp#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 *
	 * @param attributeIds the attributes to which each valueId of valueIds belongs. Possible values are:
	 * 		<blockquote><b>AVRCP_SETTING_ATTRIBUTE_ID_EQUALIZER</b>					(byte) 0x01
	 *		<br><b>AVRCP_SETTING_ATTRIBUTE_ID_REPEAT_MODE</b>						(byte) 0x02
	 *		<br><b>AVRCP_SETTING_ATTRIBUTE_ID_SHUFFLE</b>							(byte) 0x03
	 *		<br><b>AVRCP_SETTING_ATTRIBUTE_ID_SCAN</b>								(byte) 0x04</blockquote>
	 * @param valueIds the current values of attributeId on remote device. Possible values are:
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
	 */		
	void onAvrcp13EventPlayerSettingChanged(in byte[] attributeIds, in byte[] valueIds);
	
	
	/* ======================================================================================================================================================== 
	 * Callback function of AVRCP v1.4 related operations
	 */	
	 
	/** 
	 * Callback to inform change in the content of the NowPlaying folder for the AddressedPlayer 
	 * on A2DP/AVRCP connected remote device.
	 *
	 * <p>The notification should not be completed if only the track has changed or the order of the tracks on the now playing list has changed.
	 *
	 * <p>This callback would be invoked only if event <b>AVRCP_EVENT_ID_NOW_PLAYING_CONTENT_CHANGED</b> was registered before by using
	 * {@link INfCommandAvrcp#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 *
	 */		
	void onAvrcp14EventNowPlayingContentChanged();
	
	/** 
	 * Callback to inform change in the available player on A2DP/AVRCP connected remote device.
	 *
	 * <p>That is if a new player becomes available to be addressed (for instance started, or installed) or if a player ceases to be available.
	 *
	 * <p>This callback would be invoked only if event <b>AVRCP_EVENT_ID_AVAILABLE_PLAYERS_CHANGED</b> was registered before by using
	 * {@link INfCommandAvrcp#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 *
	 */			
	void onAvrcp14EventAvailablePlayerChanged();

	/** 
	 * Callback to inform change in the AddressedPlayer on A2DP/AVRCP connected remote device.
	 *
	 * <p>The interim response to registration of <b>AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED</b> shall contain the PlayerId of the current AddressedPlayer. 
	 *
	 * <p>If the registration of <b>AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED</b> was done before sending a 
	 * {@link INfCommandAvrcp#reqAvrcp14SetAddressedPlayer reqAvrcp14SetAddressedPlayer} command,
	 * the interim response contains the PlayerId of the default player on the remote device.
	 * <p>This callback would be invoked only if event <b>AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED</b> was registered before by using
	 * {@link INfCommandAvrcp#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 *
	 * @param playerId the PlayerId of the AddressedPlayer.
 	 * @param uidCounter UID counter.
	 */	
	void onAvrcp14EventAddressedPlayerChanged(int playerId, int uidCounter);
	
	/** 
	 * Callback to inform change in UIDs on A2DP/AVRCP connected remote device.
	 * 
	 * <p>Note that to refresh UID information after having received this callback, the Media Player Virtual Filesystem may be browsed.
	 * <br>When this happend, the {@link INfCommandAvrcp#reqAvrcp14GetFolderItems reqAvrcp14GetFolderItems} is required to update UIDs.
	 * <p>This callback would be invoked only if event <b>AVRCP_EVENT_ID_UIDS_CHANGED</b> was registered before by using
	 * {@link INfCommandAvrcp#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher}.
	 *
 	 * @param uidCounter UID Counter.
	 */		
	void onAvrcp14EventUidsChanged(int uidCounter);
	
	/** 
	 * Callback to inform change in the volume on A2DP/AVRCP connected remote device.
	 * <p>This callback would be invoked only if event <b>AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED</b> was registered before by using
	 * {@link INfCommandAvrcp#reqAvrcpRegisterEventWatcher reqAvrcp14RegisterEventWatcher}.	 
	 *
	 * @param volume the setting volume value of octet from 0x00 to 0x7F.
	 */		 	
	void onAvrcp14EventVolumeChanged(byte volume);
	
	/** 
	 * Callback to inform response to {@link INfCommandAvrcp#reqAvrcp14SetAddressedPlayer reqAvrcp14SetAddressedPlayer}
	 * from A2DP/AVRCP connected remote device.
	 *
	 * <p>This means that the previously requested command {@link INfCommandAvrcp#reqAvrcp14SetAddressedPlayer reqAvrcp14SetAddressedPlayer}
	 * is accepted successfully by remote device.
	 *
	 */			 	
	void retAvrcp14SetAddressedPlayerSuccess();
	
	/** 
	 * Callback to inform response to {@link INfCommandAvrcp#reqAvrcp14SetBrowsedPlayer reqAvrcp14SetBrowsedPlayer}
	 * from A2DP/AVRCP connected remote device.
	 *
	 * <p>This means that the previously requested command {@link INfCommandAvrcp#reqAvrcp14SetBrowsedPlayer reqAvrcp14SetBrowsedPlayer}
	 * is accepted successfully by remote device.
	 *
	 * @param paths the path of player browsed folder path.
	 * @param uidCounter UID counter.
 	 * @param itemCount the number of items in the current folder.
	 */		
	void retAvrcp14SetBrowsedPlayerSuccess(in String[] path, int uidCounter, long itemCount);
	
	/** 
	 * Callback to inform response to {@link INfCommandAvrcp#reqAvrcp14GetFolderItems reqAvrcp14GetFolderItems}
	 * from A2DP/AVRCP connected remote device.
	 * 
	 * <p>This means that the previously requested command {@link INfCommandAvrcp#reqAvrcp14GetFolderItems reqAvrcp14GetFolderItems}
	 * is accepted successfully by remote device, and the system would invocate this with parameter itemCount 
	 * which is the real number of items after attributes of all items in this folder have been received.
	 *
	 * @param uidCounter UID counter.
 	 * @param itemCount the number of items in the current folder.
	 */		
	void retAvrcp14FolderItems(int uidCounter, long itemCount);

	/** 
	 * Callback to inform response to {@link INfCommandAvrcp#reqAvrcp14GetFolderItems reqAvrcp14GetFolderItems}
	 * from A2DP/AVRCP connected remote device.
	 * 
	 * <p>This means that the previously requested command {@link INfCommandAvrcp#reqAvrcp14GetFolderItems reqAvrcp14GetFolderItems}
	 * is accepted successfully by remote device, and the system would invocate this with parameter itemCount 
	 * which is the real number of items after attributes of all items in this folder have been received.
	 *
	 * @param uidCounter UID counter.
 	 * @param itemCount the number of items in the current folder.
	 */		
	void retAvrcp14MediaItems(int uidCounter, long itemCount);
	
	/** 
	 * Callback to inform response to {@link INfCommandAvrcp#reqAvrcp14ChangePath reqAvrcp14ChangePath}
	 * from A2DP/AVRCP connected remote device.
	 *
	 * <p>This means that the previously requested command {@link INfCommandAvrcp#reqAvrcp14ChangePath reqAvrcp14ChangePath}
	 * is accepted successfully by remote device.
	 *
 	 * @param itemCount the number of items in the current folder.
	 */		
	void retAvrcp14ChangePathSuccess(long itemCount);
	
	/** 
	 * Callback to inform response to {@link INfCommandAvrcp#reqAvrcp14GetItemAttributes reqAvrcp14GetItemAttributes}
	 * from A2DP/AVRCP connected remote device.
	 *
 	 * @param metadataAtrributeIds list of media attributes IDs. These IDs are used to uniquely identify media information. Possible values are
 	 *		<blockquote><b>AVRCP_META_ATTRIBUTE_ID_TITLE</b> 			(int) 0x01 : title of the media
     *		<br><b>AVRCP_META_ATTRIBUTE_ID_ARTIST</b> 					(int) 0x02 : name of the artist
     *		<br><b>AVRCP_META_ATTRIBUTE_ID_ALBUM</b> 					(int) 0x03 : name of the album
     *		<br><b>AVRCP_META_ATTRIBUTE_ID_NUMBER_OF_MEDIA</b> 			(int) 0x04 : number of the media, i.e. track number of the CD
     *		<br><b>AVRCP_META_ATTRIBUTE_ID_TOTAL_NUMBER_OF_MEDIA</b> 	(int) 0x05 : total number of the media, i.e. Total track number of the CD
     *		<br><b>AVRCP_META_ATTRIBUTE_ID_GENRE</b> 					(int) 0x06 : genre
     *		<br><b>AVRCP_META_ATTRIBUTE_ID_SONG_LENGTH</b> 				(int) 0x07 : playing time in milliseconds</blockquote>
  	 * @param texts the result of string type to corresponding metadata attribute ID.
	 */	
	void retAvrcp14ItemAttributes(in int[] metadataAtrributeIds, in String[] texts);
	
	/** 
	 * Callback to inform response to {@link INfCommandAvrcp#reqAvrcp14PlaySelectedItem reqAvrcp14PlaySelectedItem}
	 * from A2DP/AVRCP connected remote device.
	 * 
	 * <p>This means that the previously requested command {@link INfCommandAvrcp#reqAvrcp14PlaySelectedItem reqAvrcp14PlaySelectedItem}
	 * is accepted successfully by remote device.
	 *
	 */			
	void retAvrcp14PlaySelectedItemSuccess();
	
	/** 
	 * Callback to inform response to {@link INfCommandAvrcp#reqAvrcp14Search reqAvrcp14Search}
	 * from A2DP/AVRCP connected remote device.
	 * 
	 * <p>This means that the previously requested command {@link INfCommandAvrcp#reqAvrcp14Search reqAvrcp14Search}
	 * is accepted successfully by remote device.
	 *
	 * @param uidCounter UID counter.
 	 * @param itemCount the number of items in the current folder.
	 */		
	void retAvrcp14SearchResult(int uidCounter, long itemCount);
	
	/** 
	 * Callback to inform response to {@link INfCommandAvrcp#reqAvrcp14AddToNowPlaying reqAvrcp14AddToNowPlaying}
	 * from A2DP/AVRCP connected remote device.
	 * 
	 * <p>This means that the previously requested command {@link INfCommandAvrcp#reqAvrcp14AddToNowPlaying reqAvrcp14AddToNowPlaying}
	 * is accepted successfully by remote device.
	 *
	 */		
	void retAvrcp14AddToNowPlayingSuccess();
	
	/** 
	 * Callback to inform response to {@link INfCommandAvrcp#reqAvrcp14SetAbsoluteVolume reqAvrcp14SetAbsoluteVolume}
	 * from A2DP/AVRCP connected remote device.
	 * 
	 * <p>This means that the previously requested command {@link INfCommandAvrcp#reqAvrcp14SetAbsoluteVolume reqAvrcp14SetAbsoluteVolume}
	 * is accepted successfully by remote device.
	 *
	 * @param volume volume which has actually been set.	 
	 */		
	void retAvrcp14SetAbsoluteVolumeSuccess(byte volume);
	
	/** 
	 * Callback to inform the error for requested command specified by parameter opId.
	 * 
	 * <p>This means there is an error for requested command, and the possible reason is specified as below.
	 *	
	 * @param opId the operation which causes the error response callback. Possible values are:
	 * 		<blockquote><b>REQ_AVRCP_13_GET_CAPABILITIES_SUPPORT_EVENT</b>	(short) 0x10
	 *		<br><b>REQ_AVRCP_13_GET_PLAYER_SETTING_ATTRIBUTES_LIST</b>		(short) 0x11
	 *		<br><b>REQ_AVRCP_13_GET_PLAYER_SETTING_CURRENT_VALUES</b>		(short) 0x12
	 *		<br><b>REQ_AVRCP_13_GET_PLAYER_SETTING_VALUE</b>				(short) 0x13 
	 *		<br><b>REQ_AVRCP_13_GET_ELEMENT_ATTRIBUTES_PLAYING</b>			(short) 0x20	 	 
	 *		<br><b>REQ_AVRCP_13_GET_PLAY_STATUS</b>							(short) 0x30
	 *		<br><b>REQ_AVRCP_13_NEXT_GROUP</b>								(short) 0x21
	 *		<br><b>REQ_AVRCP_13_PREVIOUS_GROUP</b>							(short) 0x22
 	 *		<br><b>REQ_AVRCP_REGISTER_EVENT_WATCHER</b>						(short) 0x31
 	 *		<br><b>REQ_AVRCP_UNREGISTER_EVENT_WATCHER</b>					(short) 0x100	The value of PDU ID used in AVRCP v1.4 is from 0x00 to 0xff of octet.
	 *<br>
	 *		<br><b>REQ_AVRCP_14_SET_ADDRESSED_PLAYER</b>					(short) 0x60
	 *		<br><b>REQ_AVRCP_14_SET_BROWSED_PLAYER</b>						(short) 0x70
	 *		<br><b>REQ_AVRCP_14_GET_FOLDER_ITEMS</b>						(short) 0x71	 	 
	 *		<br><b>REQ_AVRCP_14_CHANGE_PATH</b>								(short) 0x72
	 *		<br><b>REQ_AVRCP_14_GET_ITEM_ATTRIBUTES</b>						(short) 0x73
	 *		<br><b>REQ_AVRCP_14_PLAY_SELECTED_ITEM</b>						(short) 0x74
	 *		<br><b>REQ_AVRCP_14_SEARCH</b>									(short) 0x80	 
	 *		<br><b>REQ_AVRCP_14_ADD_TO_NOW_PLAYING</b>						(short) 0x90
	 *<br>	 
	 *		<br><b>REQ_AVRCP_14_SET_ABSOLUTE_VOLUME</b>						(short) 0x50</blockquote>
	 *
	 * @param reason the possible reason of error. We only support the following two reansons in this version.
	 * 		<blockquote><b>AVRCP_ERROR_CODE_INVALID_COMMAND</b>				(short) 0x00
	 *		<br><b>AVRCP_ERROR_CODE_BUSY</b>								(short) 0x100	The value of error code used in AVRCP v1.4 is from 0x00 to 0xff of octet.</blockquote>
	 * @param eventId the event ID of {@link INfCommandAvrcp#reqAvrcpRegisterEventWatcher reqAvrcpRegisterEventWatcher} which failed to register. 
	 * This parameter is <b>ONLY</b> valid when the opId is <b>REQ_AVRCP_CT_REGISTER_EVENT_WATCHER</b>.
	 * <br>In other cases, it would have value of 0xFF. 
	 * Possible values are:
	 *		<blockquote><b>AVRCP_EVENT_ID_PLAYBACK_STATUS_CHANGED</b>		(byte) 0x01
	 *		<br><b>AVRCP_EVENT_ID_TRACK_CHANGED</b>							(byte) 0x02
	 *		<br><b>AVRCP_EVENT_ID_TRACK_REACHED_END</b>						(byte) 0x03
	 *		<br><b>AVRCP_EVENT_ID_TRACK_REACHED_START</b>					(byte) 0x04
	 *		<br><b>AVRCP_EVENT_ID_PLAYBACK_POS_CHANGED</b>					(byte) 0x05
	 *		<br><b>AVRCP_EVENT_ID_BATT_STATUS_CHANGED</b>					(byte) 0x06
	 *		<br><b>AVRCP_EVENT_ID_SYSTEM_STATUS_CHANGED</b>					(byte) 0x07
	 *		<br><b>AVRCP_EVENT_ID_PLAYER_APPLICATION_SETTING_CHANGED</b>	(byte) 0x08
	 *		<br><b>AVRCP_EVENT_ID_NOW_PLAYING_CONTENT_CHANGED</b>			(byte) 0x09
	 *		<br><b>AVRCP_EVENT_ID_AVAILABLE_PLAYERS_CHANGED</b>				(byte) 0x0a
	 *		<br><b>AVRCP_EVENT_ID_ADDRESSED_PLAYER_CHANGED</b>				(byte) 0x0b
	 *		<br><b>AVRCP_EVENT_ID_UIDS_CHANGED</b>							(byte) 0x0c
	 *		<br><b>AVRCP_EVENT_ID_VOLUME_CHANGED</b>						(byte) 0x0d	</blockquote>
	 */	
	void onAvrcpErrorResponse(int opId, int reason, byte eventId);

	void retAvrcpUpdateSongStatus(String artist, String album, String title);

}
