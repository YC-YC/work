package com.zhonghong.chelianupdate.utils;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.zhonghong.chelianupdate.bean.GroupVersionVo;
import com.zhonghong.chelianupdate.bean.UpdateStatusInfo;
import com.zhonghong.chelianupdate.bean.UpdateVo;


public class JSONParser {
	
	//private static final String APP_NAME_OS="os";
	private static final String APP_NAME_AIR="air";
	private static final String APP_NAME_ANDROID="android";
	//private static final String APP_NAME_APP="app";
	private static final String APP_NAME_MCU="mcu";
	private static final String APP_NAME_8836="8836";
	private static final String APP_NAME_CAN="can";
	
	private String jsonString;	//JSON字符串
	private UpdateStatusInfo statusInfo;		//JSON字符串是否有效
	private JSONObject root;	//从JSON字符串解析出的根元素
	
	public JSONParser(String jsonString)
	{
		this.jsonString=jsonString;
		//检查JSONString的状态，包括检查其是否符合JSON规范以及JSON中的"status"字段是否为"SUCCEED"
		//并尝试将JSONSting解析为私有的JSONObject对象
		this.statusInfo=initRootObject();			
	}
	
	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	
	/**
	 * 获取该JSON字符串的状态
	 * 
	 * @return 返回UpdateStatusInfo类型的对象，可以通过其isOK方法检测字符串状态，如果isOK方法返回false，
	 * 可以通过getErrorMessage方法获取错误信息，通过getErrorCode方法获取错误代码
	 * */
	public UpdateStatusInfo getStatus() {
		return statusInfo;
	}

	/** 
	 * 根据创建对象时传入的JSON字符串初始化JSON根元素
	 * 
	 * @return UpdateStatusInfo类型的对象，可以通过其isOk方法判断返回的结果是否是有效的升级信息，如果升级信息无效，并且附有errorCode和errorMessage，
	 * 则会将它们赋到UpdateStatusInfo的对象属性中
	 **/
	private UpdateStatusInfo initRootObject()
	{
		UpdateStatusInfo statusInfo=new UpdateStatusInfo();
		if (jsonString == null)
		{
			return statusInfo;
		}
		boolean status = false;
		try {
			root = JSON.parseObject(jsonString);
			status = (root.getString("status")!=null)&&root.getString("status").equals("SUCCEED");
			statusInfo.setStatus(status);
			if(!status)
			{
				statusInfo.setErrorMessage(root.getString("errorMessage"));
				statusInfo.setErrorCode(root.getString("errorCode"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			statusInfo.setStatus(false);
			return statusInfo;
		}
		return statusInfo;
	}
	
	/**
	 * 从私有的JSONString对象中解析出GroupVersionVo对象,并且判断版本号是否有效
	 * 
	 * @return 当status为false也就是该JSON字符串描述的数据无效或者result对象不存在于JSON串中时返回null，否则返回一个GroupVersionVo类型的对象
	 * */
	public GroupVersionVo getFullInfoCheckValid(Context context)
	{
		//判断JSON字符串的状态，如果不符合JSON规范或者其中的status字段不是"SUCCEED"时直接返回null
		if (!statusInfo.isOk()) {
			return null;
		}
		
		//从JSON文件的根元素中获取result元素，如果result元素为空则返回null
		JSONObject resultObject = root.getJSONObject("result");
		if (resultObject == null) {
			return null;
		}

		GroupVersionVo groupVersionVo = new GroupVersionVo();
		groupVersionVo.setId(resultObject.getIntValue("id"));
		groupVersionVo.setMaxSize(resultObject.getIntValue("maxSize"));
		groupVersionVo.setFileSize(resultObject.getIntValue("fileSize"));
		groupVersionVo.setType(resultObject.getString("type"));
		groupVersionVo.setStatus(resultObject.getString("status"));
		groupVersionVo.setDetail(resultObject.getString("detail"));
		groupVersionVo.setVersionCode(resultObject.getString("versionCode"));
		groupVersionVo.setCreatedTime(resultObject.getString("createdTime"));
		
		
		//因为需求中不包含Android的升级，所以这里不把Android加入到groupVersionVo中
		UpdateVo air=getUpdateVo(resultObject, APP_NAME_AIR);
		UpdateVo can=getUpdateVo(resultObject,APP_NAME_CAN);
		UpdateVo mo8836=getUpdateVo(resultObject,APP_NAME_8836);
		UpdateVo mcu=getUpdateVo(resultObject, APP_NAME_MCU);
			
		if(groupVersionVo.containsUpdate(APP_NAME_AIR)&&air!=null)
			groupVersionVo.setAir(VersionUtils.isAIRVersionValid(context, air.getVersionCode())?air:null);
		if(groupVersionVo.containsUpdate(APP_NAME_CAN)&&can!=null)
			groupVersionVo.setCan(VersionUtils.isCANVersionValid(context, can.getVersionCode())?can:null);
		if(groupVersionVo.containsUpdate(APP_NAME_8836)&&mo8836!=null)
			groupVersionVo.setMo8836(VersionUtils.is8836VersionValid(context, mo8836.getVersionCode())?mo8836:null);
		if(groupVersionVo.containsUpdate(APP_NAME_MCU)&&mcu!=null)
			groupVersionVo.setMcu(VersionUtils.isMCUVersionValid(context, mcu.getVersionCode())?mcu:null);

		return groupVersionVo;
	}
	
	/**
	 * 从私有的JSONString对象中解析出GroupVersionVo对象
	 * 
	 * @return 当status为false也就是该JSON字符串描述的数据无效或者result对象不存在于JSON串中时返回null，否则返回一个GroupVersionVo类型的对象
	 * */
	public GroupVersionVo getFullInfo()
	{
		//判断JSON字符串的状态，如果不符合JSON规范或者其中的status字段不是"SUCCEED"时直接返回null
		if (!statusInfo.isOk()) {
			return null;
		}
		
		//从JSON文件的根元素中获取result元素，如果result元素为空则返回null
		JSONObject resultObject = root.getJSONObject("result");
		if (resultObject == null) {
			return null;
		}

		GroupVersionVo groupVersionVo = new GroupVersionVo();
		groupVersionVo.setId(resultObject.getIntValue("id"));
		groupVersionVo.setMaxSize(resultObject.getIntValue("maxSize"));
		groupVersionVo.setFileSize(resultObject.getIntValue("fileSize"));
		groupVersionVo.setType(resultObject.getString("type"));
		groupVersionVo.setStatus(resultObject.getString("status"));
		groupVersionVo.setDetail(resultObject.getString("detail"));
		groupVersionVo.setVersionCode(resultObject.getString("versionCode"));
		groupVersionVo.setCreatedTime(resultObject.getString("createdTime"));
		groupVersionVo.setAir(getUpdateVo(resultObject, APP_NAME_AIR));
		groupVersionVo.setAndroid(getUpdateVo(resultObject, APP_NAME_ANDROID));
		groupVersionVo.setMcu(getUpdateVo(resultObject, APP_NAME_MCU));
		groupVersionVo.setCan(getUpdateVo(resultObject, APP_NAME_CAN));
		groupVersionVo.setMo8836(getUpdateVo(resultObject, APP_NAME_8836));
		
		return groupVersionVo;
	}
	
	/**
	 * 从JSONObject中解析出文件描述对象
	 * 
	 * @param object  JSONObject类型的对象，代表根元素中的"result"元素
	 * @param appName 文件描述对象的对象名，为在JSONParser中定义的常量，"air"、"android"和"mcu"的其中一个
	 * */
	private UpdateVo getUpdateVo(JSONObject object,String appName)
	{
		JSONObject item=object.getJSONObject(appName);
		if(item==null)
			return null;
		
		UpdateVo updateVo=new UpdateVo();
		updateVo.setAppId(item.getString("appId"));
		updateVo.setAppName(item.getString("appName"));
		updateVo.setFileName(item.getString("fileName"));
		updateVo.setFileSize(item.getIntValue("fileSize"));
		updateVo.setFileUrl(item.getString("fileUrl"));
		updateVo.setVersionCode(item.getString("versionCode"));
		updateVo.setVersionId(item.getIntValue("versionId"));
		return updateVo;
	}
}
