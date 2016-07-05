package com.zh.bt.service;


/**
 * 信息类
 * @author xbgu
 *
 */
public class BtSettingsInfo {

	public class Action{

		public static final String START_SERVICE = "com.zh.bt.service.START_SERVICE";
		
		//public static final String BUILD_CONNECT_SERVICE = "com.zh.bubut.BluetoothBLE.BUILD_CONNECT_SERVICE";
		public static final String RETRY_BUILD_SERVICE = "com.zh.bt.service.RETRY_BUILD_SERVICE";
		public static final String SERVICE_RESUME = "com.zh.bt.service.SERVICE_RESUME";

		public static final String RESTART_SERVICE = "com.zh.bt.service.RESTART_SERVICE";

		public static final String STOP_SERVICE = "com.zh.bt.service.STOP_SERVICE";

	}
	public class IntentKey{
		
		public static final String BT_CONNECT_STATE = "bt_connect_state";
		public static final String BT_REQ_CHANGE = "req_change";
		public static final String BT_SOURCE_CHANGE = "source_change";
		public static final String BT_PLAYSTATE_CHANGE = "playstate_change";
		
		public static final String BT_REMOTE_NAME = "bt_remote_name";
		public static final String TRY_TO_CONNECT_TARGET = "target_device";
		
	}
	public class MessageID{
		
		public static final int BUILD_CONNECT_SERVICE = 0x01;
		public static final int RETRY_BUILD_SERVICE = 0x02;
		public static final int SERVICE_RESUME = 0x03;
		public static final int TRY_TO_CONNECT_TARGET = 0x04;
		
		public static final int DO_ACTION = 0x05;
		
		public static final int HANDLER_MSG_RECEIVE_PACKAGE = 0x01;
		public static final int HANDLER_MSG_RECEIVER_CMD = 0x02;
		public static final int HANDLER_MSG_CMD_HANDED = 0x03;
	    
		public static final int onGattServerCharacteristicWriteRequest = 6;
	}

	public class doActions{
		public static final int SOURCE_CHANGE = 0x01;
	}
}
