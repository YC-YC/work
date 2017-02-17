/**
 * 
 */
package com.zhonghong.autotestservice.service;

import android.text.TextUtils;
import android.util.Log;

import com.zhonghong.autotestlib.bean.ATConst;
import com.zhonghong.autotestservice.bean.ATEvent;

/**
 * @author YC
 * @time 2016-12-6 上午9:30:01
 * TODO:数据处理
 */
public class ProcessManager {
	
	/**正响应*/
	public static final int TYPE_POSITIVE_RESPONSE = 1;
	/**负响应*/
	public static final int TYPE_NAGATIVE_RESPONSE = 2;
	private static final String TAG = "ProcessManager";
	
	interface AT_ID{
		
		int ID_TRUN_OFF = 0x0002;
		int ID_READ_SW_VERSION = 0x0003;
		int ID_READ_NAVI_VERSION = 0x0004;
		int ID_SET_GENERAL_ON_OFF = 0x0005;
		int ID_DISPLAY_OUTPUT = 0x0006;
		int ID_RESET_TO_FACTORY = 0x0007;
		int ID_SWITCH_MODE = 0x0008;
		int ID_HARDKEY = 0x0009;
		int ID_READ_FILE = 0x000A;
		int ID_WRITE_FILE = 0x000B;
		int ID_READ_HARDWARE_VERSION = 0x000C;
		int ID_QUICK_RESET = 0x000D;
		int ID_GET_3D_STATUS = 0x000E;
		int ID_SET_RADIO_BAND = 0x000F;
		int ID_SET_RADIO_FREQ = 0x0010;
		int ID_GET_RADIO_SENSITIVITY = 0x0011;
		int ID_NAVI_DEMO_TEST = 0x0012;
		int ID_GET_GPS_NUMBER = 0x0013;
		int ID_GET_GPS_TIME = 0x0014;
		int ID_GET_GPS_INFO = 0x0015;
		int ID_SET_BT_ADDR = 0x0016;
		int ID_SET_BT_AUTO_CONNECT = 0x0017;
		int ID_SET_SYSTEM_SOURCE = 0x0018;
		int ID_SET_VOLUME = 0x0019;
		int ID_SET_MEDIA_PLAY_MODE = 0x001A;
		int ID_SET_BT_TONE_TO_SPEAKER = 0x001B;
		int ID_SET_MID_TO_SPEAKER = 0x001C;
		int ID_SET_CAMARA_OUTPUT = 0x001D;
		int ID_GET_MEDIA_STATE = 0x001E;
		int ID_GET_BT_MAC = 0x001F;
		int ID_WRITE_SN = 0x0020;
		int ID_READ_SN = 0x0021;
		int ID_WIFI_AP_MODE = 0x0022;
		int ID_WIFI_CLIENT_MODE = 0x0023;
		int ID_READ_WIFI_MAC = 0x0024;
		int ID_ETHERNET = 0x0025;
		int ID_SET_PWM_OUT_PUT = 0x0026;
	}
	

	private ATService mService;

	public ProcessManager(ATService service) {
		super();
		this.mService = service;
	}
	
	/**
	 * 生成CAN数据
	 * @param data：数据内容
	 * @param len：数据长度
	 * @param type：数据类型
	 * @return
	 */
	public byte[] genCANData(byte[] data, int len, int type){
		byte[] canData = new byte[len+4];
		canData[0] = 0x2E;
		switch (type) {
		case TYPE_POSITIVE_RESPONSE:
			canData[1] = (byte) 0xFB;
			break;
		default:
			canData[1] = 0x7F;
			break;
		}
		canData[2] = (byte) len;
		for(int i = 0; i < len; i++){
			canData[3+i] = data[i];
		}
		canData[len+3] = getCheckSun(canData, 1, len+3);
		return canData;
	}
	
	/**
	 * 获取数据checkSun
	 * @param data
	 * @param start:
	 * @param end
	 * @return
	 */
	private byte getCheckSun(byte[] data, int start, int end){
		int checkSun = 0;
		for (int i = start; i <= end; i++){
			checkSun += (data[i]&0xFF);
		}
		return (byte) ((checkSun&0xFF)^0xFF);
	}
	

	/**
	 * 协议处理
	 * @param id
	 * @param caninfo
	 * @return
	 */
	public ATEvent onProcessConvert(final byte[] caninfo){
		//TODO 协议处理
		int id = (caninfo[3]<<8)&0xFF00 + (caninfo[4]&0xFF);
		byte param0 = caninfo[5];
		switch (id) {
		case AT_ID.ID_TRUN_OFF: {
			mService.sendATBroadcast(ATConst.Broadcast.KEY_END_AT, "true");
			byte[] data = { 0x00, 0x02 };
			mService.sendData(genCANData(data, data.length, TYPE_POSITIVE_RESPONSE));
		}
			break;
		case AT_ID.ID_READ_SW_VERSION: {
			ATEvent event = new ATEvent();
			event.type = ATConst.Settings.TYPE_SETTINGS;
			event.cmd = ATConst.Settings.CMD_SETTINGS_GET_SW_VER;
			return event;
		}
		case AT_ID.ID_READ_NAVI_VERSION: {
			ATEvent event = new ATEvent();
			event.type = ATConst.Settings.TYPE_SETTINGS;
			event.cmd = ATConst.Settings.CMD_SETTINGS_GET_NAVI_VER;
			return event;
		}
		case AT_ID.ID_SET_GENERAL_ON_OFF: {
			int type = caninfo[5]&0xFF;
			boolean bOn = (caninfo[6] == 0x01) ? true:false;
			switch (type) {
			case 0x01:{
				//BT
				ATEvent event = new ATEvent();
				event.type = ATConst.BT.TYPE_BT;
				event.cmd = ATConst.BT.CMD_BT_SET_ON_OFF;
				event.info = bOn? "on":"off";
				return event;
			}
			case 0x02:{
				//主屏装饰灯
				ATEvent event = new ATEvent();
				event.type = ATConst.Settings.TYPE_SETTINGS;
				event.cmd = ATConst.Settings.CMD_SETTINGS_SET_ILLUMINAITON_ON_OFF;
				event.info = "main::" + (bOn? "on":"off");
				return event;
			}
			case 0x03:{
				//副屏装饰灯
				ATEvent event = new ATEvent();
				event.type = ATConst.Settings.TYPE_SETTINGS;
				event.cmd = ATConst.Settings.CMD_SETTINGS_SET_ILLUMINAITON_ON_OFF;
				event.info = "vice::" + (bOn? "on":"off");
				return event;
			}
			case 0x04:
			case 0x05:{
				//LED
				byte[] data = { 0x00, 0x05, 0x00};
				mService.sendData(genCANData(data, data.length, TYPE_POSITIVE_RESPONSE));
				return null;
			}
			case 0x06:{
				ATEvent event = new ATEvent();
				event.type = ATConst.Settings.TYPE_SETTINGS;
				event.cmd = ATConst.Settings.CMD_SETTINGS_SET_WIFI_ON_OFF;
				event.info = bOn? "on":"off";
				return event;
			}
			case 0x07:{
				ATEvent event = new ATEvent();
				event.type = ATConst.GPS.TYPE_GPS;
				event.cmd = ATConst.GPS.CMD_GPS_SET_GPS_ON_OFF;
				event.info = bOn? "on":"off";
				return event;
			}
			case 0x08:{
				ATEvent event = new ATEvent();
				event.type = ATConst.Settings.TYPE_SETTINGS;
				event.cmd = ATConst.Settings.CMD_SETTINGS_SET_TBOX_ON_OFF;
				event.info = bOn? "on":"off";
				return event;
			}
				
			default:
				byte[] data = { 0x00, 0x05, 0x00};
				mService.sendData(genCANData(data, data.length, TYPE_POSITIVE_RESPONSE));
				return null;
			}
		}
		case AT_ID.ID_DISPLAY_OUTPUT: {
			int type = caninfo[5]&0xFF;
			boolean bOn = (caninfo[6] == 0x01) ? true:false;
			switch (type) {
			//TODO 没实现
			case 0x01:{
				//display white
			}
			case 0x02:{
				//display black
			}
				
			byte[] data = { 0x00, 0x06, 0x00};
			mService.sendData(genCANData(data, data.length, TYPE_POSITIVE_RESPONSE));
			return null;
			}
		}
			break;
		case AT_ID.ID_RESET_TO_FACTORY: {
			if (caninfo[6] == 0x01){
				
				ATEvent event = new ATEvent();
				event.type = ATConst.Settings.TYPE_SETTINGS;
				event.cmd = ATConst.Settings.CMD_SETTINGS_RESET_TO_FACTORY;
				return event;
			}
			else{
				byte[] data = { 0x00, 0x07, 0x00};
				mService.sendData(genCANData(data, data.length, TYPE_POSITIVE_RESPONSE));
				return null;
			}
		}
		case AT_ID.ID_SWITCH_MODE: {
			//TODO 没实现
			byte[] data = { 0x00, 0x08};
			mService.sendData(genCANData(data, data.length, TYPE_POSITIVE_RESPONSE));
			return null;
		}
		case AT_ID.ID_HARDKEY: {
			//TODO 没实现
			byte[] data = { 0x00, 0x09, 0x00};
			mService.sendData(genCANData(data, data.length, TYPE_POSITIVE_RESPONSE));
			return null;
		}
		case AT_ID.ID_READ_FILE: {
			//TODO 没实现
			byte[] data = { 0x00, 0x0A};
			mService.sendData(genCANData(data, data.length, TYPE_NAGATIVE_RESPONSE));
			return null;
		}
		case AT_ID.ID_WRITE_FILE: {
			//TODO 没实现
			byte[] data = { 0x00, 0x0B};
			mService.sendData(genCANData(data, data.length, TYPE_POSITIVE_RESPONSE));
			return null;
		}
		case AT_ID.ID_READ_HARDWARE_VERSION: {
			ATEvent event = new ATEvent();
			event.type = ATConst.Settings.TYPE_SETTINGS;
			event.cmd = ATConst.Settings.CMD_SETTINGS_GET_HW_VER;
			return event;
		}
		case AT_ID.ID_QUICK_RESET: {
			//TODO 没实现
			byte[] data = { 0x00, 0x0D};
			mService.sendData(genCANData(data, data.length, TYPE_POSITIVE_RESPONSE));
			return null;
		}
		case AT_ID.ID_GET_3D_STATUS: {
			//TODO 没实现
			byte[] data = { 0x00, 0x0E, 0x01};
			mService.sendData(genCANData(data, data.length, TYPE_POSITIVE_RESPONSE));
			return null;
		}
		case AT_ID.ID_SET_RADIO_BAND: {
			ATEvent event = new ATEvent();
			event.type = ATConst.Radio.TYPE_RADIO;
			event.cmd = ATConst.Radio.CMD_RADIO_SET_BAND;
			event.info = getRadioBand(caninfo[5]);
			return event;
		}
		case AT_ID.ID_SET_RADIO_FREQ: {
			int freq = (caninfo[5]<<16)&0xFF00 + (caninfo[6]<<8)&0xFF00 + (caninfo[7]&0xFF);
			ATEvent event = new ATEvent();
			event.type = ATConst.Radio.TYPE_RADIO;
			event.cmd = ATConst.Radio.CMD_RADIO_SET_FREQ;
			event.info = "" + freq;
			return event;
		}
		case AT_ID.ID_GET_RADIO_SENSITIVITY: {
			//TODO 没实现
			byte[] data = { 0x00, 0x11, 0x01, 0x00, 0x00, 0x00};
			mService.sendData(genCANData(data, data.length, TYPE_POSITIVE_RESPONSE));
			return null;
		}
		case AT_ID.ID_NAVI_DEMO_TEST: {
			//TODO 没实现
			byte[] data = { 0x00, 0x12, 0x01};
			mService.sendData(genCANData(data, data.length, TYPE_POSITIVE_RESPONSE));
			return null;
		}
		case AT_ID.ID_GET_GPS_NUMBER: {
			ATEvent event = new ATEvent();
			event.type = ATConst.GPS.TYPE_GPS;
			event.cmd = ATConst.GPS.CMD_GPS_GET_GPS_NUMBER;
			byte param = caninfo[5];
			if (param == 0x01){
				event.info = "used_num";
			}
			else{
				event.info = "visible_num";
			}
			return event;
		}
		case AT_ID.ID_GET_GPS_TIME: {
			ATEvent event = new ATEvent();
			event.type = ATConst.GPS.TYPE_GPS;
			event.cmd = ATConst.GPS.CMD_GPS_GET_GPS_TIME;
			return event;
		}
		case AT_ID.ID_GET_GPS_INFO: {
			ATEvent event = new ATEvent();
			event.type = ATConst.GPS.TYPE_GPS;
			event.cmd = ATConst.GPS.CMD_GPS_GET_GPS_INFO;
			byte param = caninfo[5];
			if (param == 0x01){
				event.info = "direction";
			}
			else{
				event.info = "speed";
			}
			return event;
		}
		case AT_ID.ID_SET_BT_ADDR: {
			//TODO 没实现
			byte[] data = { 0x00, 0x16, 0x00};
			mService.sendData(genCANData(data, data.length, TYPE_POSITIVE_RESPONSE));
			return null;
		}
		case AT_ID.ID_SET_BT_AUTO_CONNECT: {
			ATEvent event = new ATEvent();
			event.type = ATConst.BT.TYPE_BT;
			event.cmd = ATConst.BT.CMD_BT_SET_AUTO_CONN;
			event.info = String.format("%02x%02x%02x%02x%02x%02x", 
					caninfo[5]&0xFF, caninfo[6]&0xFF, 
					caninfo[7]&0xFF, caninfo[8]&0xFF,
					caninfo[9]&0xFF,caninfo[10]&0xFF);
			return event;
		}
		case AT_ID.ID_SET_SYSTEM_SOURCE: {
			ATEvent event = new ATEvent();
			event.type = ATConst.Logic.TYPE_LOGIC;
			event.cmd = ATConst.Logic.CMD_LOGIC_SET_SOURCE;
			event.info = ((caninfo[6] == 0x02) ? "main::":"vice::") + getSource(caninfo[5]);
			return event;
		}
		case AT_ID.ID_SET_VOLUME: {
			ATEvent event = new ATEvent();
			event.type = ATConst.Logic.TYPE_LOGIC;
			event.cmd = ATConst.Logic.CMD_LOGIC_SET_VOL;
			int vol = caninfo[5] & 0xFF;
			event.info = ((caninfo[6] == 0x02) ? "main::":"vice::") 
					+ (vol == 0xFF ? "mute": ("" + vol));
			return event;
		}
		case AT_ID.ID_SET_MEDIA_PLAY_MODE: {
			ATEvent event = new ATEvent();
			event.type = ATConst.Media.TYPE_MEDIA;
			event.cmd = ATConst.Media.CMD_MEDIA_SET_PLAY_MODE;
			event.info = getPlayMode(caninfo[5]);
			return event;
		}
		case AT_ID.ID_SET_BT_TONE_TO_SPEAKER: {
			//TODO 没实现
			byte[] data = { 0x00, 0x1b, 0x00};
			mService.sendData(genCANData(data, data.length, TYPE_POSITIVE_RESPONSE));
			return null;
		}
		case AT_ID.ID_SET_MID_TO_SPEAKER: {
			//TODO 没实现
			byte[] data = { 0x00, 0x1c, 0x00};
			mService.sendData(genCANData(data, data.length, TYPE_POSITIVE_RESPONSE));
			return null;
		}
		case AT_ID.ID_SET_CAMARA_OUTPUT: {
			//TODO 没实现
			byte[] data = { 0x00, 0x1d, 0x00};
			mService.sendData(genCANData(data, data.length, TYPE_POSITIVE_RESPONSE));
			return null;
		}
		case AT_ID.ID_GET_MEDIA_STATE: {
			ATEvent event = new ATEvent();
			event.type = ATConst.Media.TYPE_MEDIA;
			event.cmd = ATConst.Media.CMD_MEDIA_GET_MEDIA_STATE;
			event.info = (caninfo[5] == 0x01) ? "total_tracks":"active_device";
			return event;
		}
		case AT_ID.ID_GET_BT_MAC: {
			ATEvent event = new ATEvent();
			event.type = ATConst.BT.TYPE_BT;
			event.cmd = ATConst.BT.CMD_BT_GET_BT_ADDR;
			return event;
		}
		case AT_ID.ID_WRITE_SN: {
			ATEvent event = new ATEvent();
			event.type = ATConst.System.TYPE_SYSTEM;
			event.cmd = ATConst.System.CMD_SYSTEM_SET_SN;
			event.info = String.format("%02x%02x%02x%02x%02x%02x%02x%02x%02x", 
					caninfo[5]&0xFF, caninfo[6]&0xFF, caninfo[7]&0xFF,
					caninfo[8]&0xFF, caninfo[9]&0xFF, caninfo[10]&0xFF,
					caninfo[11]&0xFF, caninfo[12]&0xFF, caninfo[13]&0xFF);
			return event;
		}
		case AT_ID.ID_READ_SN: {
			ATEvent event = new ATEvent();
			event.type = ATConst.System.TYPE_SYSTEM;
			event.cmd = ATConst.System.CMD_SYSTEM_GET_SN;
			return event;
		}
		case AT_ID.ID_WIFI_AP_MODE: {
			ATEvent event = new ATEvent();
			event.type = ATConst.Settings.TYPE_SETTINGS;
			event.cmd = ATConst.Settings.CMD_SETTINGS_SET_WIFI_AP_MODE;
			return event;
		}
		case AT_ID.ID_WIFI_CLIENT_MODE: {
			ATEvent event = new ATEvent();
			event.type = ATConst.Settings.TYPE_SETTINGS;
			event.cmd = ATConst.Settings.CMD_SETTINGS_SET_WIFI_CLIENT_MODE;
			return event;
		}
		case AT_ID.ID_READ_WIFI_MAC: {
			ATEvent event = new ATEvent();
			event.type = ATConst.Settings.TYPE_SETTINGS;
			event.cmd = ATConst.Settings.CMD_SETTINGS_GET_WIFI_ADDR;
			return event;
		}
		case AT_ID.ID_ETHERNET: {
			//TODO 没实现
			byte[] data = { 0x00, 0x25, 0x00};
			mService.sendData(genCANData(data, data.length, TYPE_POSITIVE_RESPONSE));
			return null;
		}
		case AT_ID.ID_SET_PWM_OUT_PUT: {
			//TODO 没实现
			byte[] data = { 0x00, 0x26, 0x00};
			mService.sendData(genCANData(data, data.length, TYPE_POSITIVE_RESPONSE));
			return null;
		}
		default:
			break;
		}
		return null;
	}
	
	
	public byte[] onProcessRevert(final int type, final int cmd, final String info) {
		Log.i(TAG, "Revert type=" + type + ", cmd = " + cmd + "info = " + info);
		switch (type) {
		case ATConst.Logic.TYPE_LOGIC: {
			switch (cmd) {
			case ATConst.Logic.CMD_LOGIC_SET_SOURCE: {
				byte[] data = { 0x00, 0x18, 0x00 };
				if ("ok".equals(info)) {
					data[2] = 0x01;
				}
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			case ATConst.Logic.CMD_LOGIC_SET_VOL: {
				byte[] data = { 0x00, 0x19, 0x00 };
				if ("ok".equals(info)) {
					data[2] = 0x01;
				}
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			}
		}
			break;
		case ATConst.System.TYPE_SYSTEM: {
			switch (cmd) {
			case ATConst.System.CMD_SYSTEM_SET_SN: {
				byte[] data = { 0x00, 0x20};
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			case ATConst.System.CMD_SYSTEM_GET_SN: {
				byte[] data = { 0x00, 0x21, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
				if (!TextUtils.isEmpty(info)){
					byte[] bytes = info.getBytes();
					for (int i = 0; i < bytes.length && i < 18; i++){
						int index = i/2;
						if (i%2 == 1){
							data[2+index] = (byte) ((data[2+index] << 4) + getByteToInt(bytes[i]));
						}
						else{
							data[2+index] = (byte) getByteToInt(bytes[i]);
						}
					}
				}
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			}

		}
			break;
		case ATConst.Radio.TYPE_RADIO: {
			switch (cmd) {
			case ATConst.Radio.CMD_RADIO_SET_BAND: {
				byte[] data = { 0x00, 0x0F, 0x00 };
				if ("ok".equals(info)) {
					data[2] = 0x01;
				}
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			case ATConst.Radio.CMD_RADIO_SET_FREQ: {
				byte[] data = { 0x00, 0x10, 0x00 };
				if ("ok".equals(info)) {
					data[2] = 0x01;
				}
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			}
		}
			break;
		case ATConst.Settings.TYPE_SETTINGS: {
			switch (cmd) {
			case ATConst.Settings.CMD_SETTINGS_SET_ILLUMINAITON_ON_OFF: {
				byte[] data = { 0x00, 0x05, 0x00, 0x00};
				if (!TextUtils.isEmpty(info) && info.contains("::")){
					String[] split = info.split("::");
					if ("vice".equals(split[0])){
						data[2] = 0x03;
					}
					else{
						data[2] = 0x02;
					}
					if ("ok".equals(split[1])) {
						data[3] = 0x01;
					}
				}
				else{
					Log.w(TAG, "Illegal info");
				}
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			
			case ATConst.Settings.CMD_SETTINGS_SET_TBOX_ON_OFF: {
				byte[] data = { 0x00, 0x05, 0x08, 0x00};
				if ("ok".equals(info)) {
					data[3] = 0x01;
				}
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			case ATConst.Settings.CMD_SETTINGS_SET_WIFI_ON_OFF: {
				byte[] data = { 0x00, 0x05, 0x06, 0x00};
				if ("ok".equals(info)) {
					data[3] = 0x01;
				}
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			case ATConst.Settings.CMD_SETTINGS_SET_WIFI_AP_MODE: {
				byte[] data = { 0x00, 0x22};
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			case ATConst.Settings.CMD_SETTINGS_SET_WIFI_CLIENT_MODE: {
				byte[] data = { 0x00, 0x23};
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			case ATConst.Settings.CMD_SETTINGS_GET_HW_VER: {
				//TODO 没实现
			}
				break;
			case ATConst.Settings.CMD_SETTINGS_GET_NAVI_VER: {
				//TODO 没实现
			}
				break;
			case ATConst.Settings.CMD_SETTINGS_GET_SW_VER: {
				//TODO 没实现
			}
				break;
			case ATConst.Settings.CMD_SETTINGS_GET_WIFI_ADDR: {
				byte[] data = { 0x00, 0x24, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
				if (!TextUtils.isEmpty(info)){
					byte[] bytes = info.getBytes();
					for (int i = 0; i < bytes.length && i < 12; i++){
						int index = i/2;
						if (i%2 == 1){
							data[2+index] = (byte) ((data[2+index] << 4) + getByteToInt(bytes[i]));
						}
						else{
							data[2+index] = (byte) getByteToInt(bytes[i]);
						}
					}
				}
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			}
		}
			break;
		case ATConst.BT.TYPE_BT: {
			switch (cmd) {
			case ATConst.BT.CMD_BT_SET_ON_OFF: {
				byte[] data = { 0x00, 0x05, 0x01, 0x00};
				if ("ok".equals(info)) {
					data[3] = 0x01;
				}
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			case ATConst.BT.CMD_BT_SET_AUTO_CONN: {
				byte[] data = { 0x00, 0x17, 0x00};
				if ("ok".equals(info)) {
					data[2] = 0x01;
				}
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			case ATConst.BT.CMD_BT_GET_BT_ADDR: {
				byte[] data = { 0x00, 0x1F, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
				if (!TextUtils.isEmpty(info)){
					byte[] bytes = info.getBytes();
					for (int i = 0; i < bytes.length && i < 12; i++){
						int index = i/2;
						if (i%2 == 1){
							data[2+index] = (byte) ((data[2+index] << 4) + getByteToInt(bytes[i]));
						}
						else{
							data[2+index] = (byte) getByteToInt(bytes[i]);
						}
					}
				}
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			}
		}
			break;
		case ATConst.GPS.TYPE_GPS: {
			switch (cmd) {
			
			case ATConst.GPS.CMD_GPS_SET_GPS_ON_OFF: {
				byte[] data = { 0x00, 0x05, 0x07, 0x00};
				if ("ok".equals(info)) {
					data[3] = 0x01;
				}
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			
			case ATConst.GPS.CMD_GPS_GET_GPS_NUMBER: {
				byte[] data = { 0x00, 0x13, 0x01, 0x00};
				if (!TextUtils.isEmpty(info) && info.contains("::")){
					String[] split = info.split("::");
					if ("used_num".equals(split[0])){
						data[2] = 0x01;
					}
					else if ("visible_num".equals(split[0])){
						data[2] = 0x02;
					}
					try {
						data[3] = (byte) Integer.parseInt(split[1]);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else{
					Log.w(TAG, "Illegal info");
				}
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			case ATConst.GPS.CMD_GPS_GET_GPS_TIME: {
				byte[] data = { 0x00, 0x14, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
				if (!TextUtils.isEmpty(info) && info.contains("-")){
					String[] split = info.split("-");
					try {
						data[2] = (byte) (Integer.parseInt(split[0])-2000);
						data[3] = (byte) Integer.parseInt(split[1]);
						data[4] = (byte) Integer.parseInt(split[2]);
						data[5] = (byte) Integer.parseInt(split[3]);
						data[6] = (byte) Integer.parseInt(split[4]);
						data[7] = (byte) Integer.parseInt(split[5]);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else{
					Log.w(TAG, "Illegal info");
				}
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			case ATConst.GPS.CMD_GPS_GET_GPS_INFO: {
				//TODO 没实现
			}
				break;
			}
		}
			break;
		case ATConst.Media.TYPE_MEDIA: {
			switch (cmd) {
			case ATConst.Media.CMD_MEDIA_SET_PLAY_MODE: {
				byte[] data = { 0x00, 0x1A, 0x00};
				if ("ok".equals(info)) {
					data[2] = 0x01;
				}
				return genCANData(data, data.length, TYPE_POSITIVE_RESPONSE);
			}
			case ATConst.Media.CMD_MEDIA_GET_MEDIA_STATE: {
				//TODO 没实现
			}
				break;
			}
		}
			break;
		}
		return null;
	}
	
	
	
	private String getRadioBand(int code){
		switch (code) {
		case 0x01: return "fm1";
		case 0x02: return "fm2";
		case 0x03: return "fm3";
		case 0x04: return "am1";
		case 0x05: return "am2";
		default: return "fm1";
		}
	}
	
	private String getSource(int code){
		switch (code) {
		case 0x01: return "usb1";
		case 0x02: return "usb2";
		case 0x03: return "a2dp";
		case 0x04: return "aux";
		case 0x05: return "radio";
		case 0x06: return "mic";
		case 0x07: return "carlife";
		default:return "radio";
		}
	}
	
	
	private String getPlayMode(int code){
		switch (code) {
		case 0x01: return "normal";
		case 0x02: return "forward";
		case 0x03: return "reward";
		case 0x04: return "next";
		case 0x05: return "previous";
		case 0x06: return "random";
		case 0x07: return "cyclic";
		default:return "normal";
		}
	}
	
	/**字符byte转成数字byte*/
	private int getByteToInt(byte data){
		if (data >= '0' && data <= '9'){
			return (data - '0');
		}
		if (data >= 'a' && data <= 'f'){
			return (data - 'a' + 10);
		}
		if (data >= 'A' && data <= 'F'){
			return (data - 'A' + 10);
		}
		return 0;
	}
	
	
}
