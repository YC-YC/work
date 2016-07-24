package com.yc.external;

import com.yc.external.CallFromService;
interface ExternalConn{
	void registerCallFromService(CallFromService callback);
	void unregisterCallFromService(CallFromService callback);
	boolean postInfo(int cmd, String val);
	String getInfo(int cmd);
}