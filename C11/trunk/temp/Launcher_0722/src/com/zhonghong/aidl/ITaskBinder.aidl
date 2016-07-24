package com.zhonghong.aidl;

import com.zhonghong.aidl.ICanDataChangeCallback;
import com.zhonghong.aidl.CanInfoParcel;
interface ITaskBinder{
	void registerDataChangeCallback(ICanDataChangeCallback callback);
	void unregisterDataChangeCallback(ICanDataChangeCallback callback);
	CanInfoParcel getCanInfo();
	void writecmdCan(in byte[] data);
}