package com.zhonghong.autotestlib;

import com.zhonghong.autotestlib.ATCallback;
interface ATConn{
	void registerATClient(int type, ATCallback callback);
	void unregisterATClient(int type, ATCallback callback);
	void onResponse(int type, int cmd, String info);
}