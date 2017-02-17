/**
 * 
 */
package com.zhonghong.autotest.bean;

/**
 * @author YC
 * @time 2016-11-29 下午4:56:10 TODO:常量定义 type:0x1000,依次增加;
 * 
 */
public interface ATConst {

	interface Service {
		String ACTION = "com.zhonghong.autotestservice.ACTION";
	}

	/** 广播相关 */
	interface Broadcast {
		/** 自动化测试广播Action */
		String ACTION = "com.zhonghong.autotest.ACTION";
		/** 开始自动化测试 */
		String KEY_START_AT = "start_autotest";
		/** 结束自动化测试 */
		String KEY_END_AT = "end_autotest";
	}

	interface Logic {
		int TYPE_LOGIC = 0x1000;

		int CMD_LOGIC_SET_SOURCE = 0x102;
		int CMD_LOGIC_SET_VOL = 0x103;

	}

	interface System {
		int TYPE_SYSTEM = 0x2000;

		int CMD_SYSTEM_SET_SN = 0x101;

		int CMD_SYSTEM_GET_SN = 0x201;

	}

	interface Radio {
		int TYPE_RADIO = 0x3000;

		int CMD_RADIO_SET_BAND = 0x101;
		int CMD_RADIO_SET_FREQ = 0x102;

	}

	interface Settings {
		int TYPE_SETTINGS = 0x4000;
		/** 装饰灯 */
		int CMD_SETTINGS_SET_ILLUMINAITON_ON_OFF = 0x101;
		int CMD_SETTINGS_SET_WIFI_ON_OFF = 0x102;
		int CMD_SETTINGS_SET_WIFI_CLIENT_MODE = 0x103;
		int CMD_SETTINGS_SET_TBOX_ON_OFF = 0x104;
		int CMD_SETTINGS_RESET_TO_FACTORY = 0x105;
		int CMD_SETTINGS_SET_WIFI_AP_MODE = 0x106;

		int CMD_SETTINGS_GET_SW_VER = 0x201;
		int CMD_SETTINGS_GET_NAVI_VER = 0x202;
		int CMD_SETTINGS_GET_HW_VER = 0x203;
		int CMD_SETTINGS_GET_WIFI_ADDR = 0x204;

	}

	interface BT {
		int TYPE_BT = 0x5000;

		int CMD_BT_SET_ON_OFF = 0x101;
		int CMD_BT_SET_AUTO_CONN = 0x102;

		int CMD_BT_GET_BT_ADDR = 0x201;

	}

	interface Media {
		int TYPE_MEDIA = 0x6000;

		int CMD_MEDIA_SET_PLAY_MODE = 0x101;

		int CMD_MEDIA_GET_MEDIA_STATE = 0x201;

	}

	interface GPS {
		int TYPE_GPS = 0x7000;

		int CMD_GPS_SET_GPS_ON_OFF = 0x101;
		
		int CMD_GPS_GET_GPS_NUMBER = 0x201;
		int CMD_GPS_GET_GPS_TIME = 0x202;
		int CMD_GPS_GET_GPS_INFO = 0x203;
	}

}
