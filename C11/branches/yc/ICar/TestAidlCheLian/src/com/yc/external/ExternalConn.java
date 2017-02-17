/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\GitHub\\TestApp\\TestAildService\\src\\com\\yc\\external\\ExternalConn.aidl
 */
package com.yc.external;
public interface ExternalConn extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.yc.external.ExternalConn
{
private static final java.lang.String DESCRIPTOR = "com.yc.external.ExternalConn";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.yc.external.ExternalConn interface,
 * generating a proxy if needed.
 */
public static com.yc.external.ExternalConn asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.yc.external.ExternalConn))) {
return ((com.yc.external.ExternalConn)iin);
}
return new com.yc.external.ExternalConn.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_registerCallFromService:
{
data.enforceInterface(DESCRIPTOR);
com.yc.external.CallFromService _arg0;
_arg0 = com.yc.external.CallFromService.Stub.asInterface(data.readStrongBinder());
this.registerCallFromService(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallFromService:
{
data.enforceInterface(DESCRIPTOR);
com.yc.external.CallFromService _arg0;
_arg0 = com.yc.external.CallFromService.Stub.asInterface(data.readStrongBinder());
this.unregisterCallFromService(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_postInfo:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _result = this.postInfo(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getInfo:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _result = this.getInfo(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.yc.external.ExternalConn
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void registerCallFromService(com.yc.external.CallFromService callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallFromService, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterCallFromService(com.yc.external.CallFromService callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallFromService, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean postInfo(int cmd, java.lang.String val) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(cmd);
_data.writeString(val);
mRemote.transact(Stub.TRANSACTION_postInfo, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getInfo(int cmd) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(cmd);
mRemote.transact(Stub.TRANSACTION_getInfo, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_registerCallFromService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_unregisterCallFromService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_postInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void registerCallFromService(com.yc.external.CallFromService callback) throws android.os.RemoteException;
public void unregisterCallFromService(com.yc.external.CallFromService callback) throws android.os.RemoteException;
public boolean postInfo(int cmd, java.lang.String val) throws android.os.RemoteException;
public java.lang.String getInfo(int cmd) throws android.os.RemoteException;
}
