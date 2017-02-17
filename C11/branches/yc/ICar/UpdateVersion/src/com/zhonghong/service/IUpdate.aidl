package com.zhonghong.service;
import com.zhonghong.service.IUpdateCallBack;

interface IUpdate{
	void updateItem(int type, int value);
	
	void registerUpdateCallBack(IUpdateCallBack callback);
}