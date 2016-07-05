/** 
 * nForeTek Gatt Server Callbacks Interface for Android 4.4.2
 *
 * Copyright (C) 2011-2019  nForeTek Corporation
 *
 * Zeus 3.0.0 on 20160321
 * @author KC Huang <kchuang@nforetek.com>
 * @author Piggy    <piggylee@nforetek.com>
 * @version 3.0.0
 * 
 */

package com.nforetek.bt.aidl;

import android.os.ParcelUuid;


/**
 * The callback interface for Gatt Server (BLE Peripheral)
 * <br>UI program should implement all methods of this interface 
 * for receiving possible callbacks from nFore service.
 * <br>The naming principle of callback in this Doc is as below,
 *      <blockquote>    <b>retXXX()</b> : must be the callback of requested API.
 *      <br>            <b>onXXX()</b>  : could be the callback for updated values or the callback from requested API.</blockquote>
 *
 * <p> The constant variables in this Doc could be found and referred by importing
 *      <br><blockquote>com.nforetek.bt.res.NfDef</blockquote>
 * <p> with prefix NfDef class name. Ex : <code>NfDef.DEFAULT_ADDRESS</code>
 *
 * <p> Bluetooth hardware addresses would be in a format such as "00:11:22:33:AA:BB".
 *
 * @see UiCommand
 */
interface UiCallbackGattServer {

    /** 
     * Callback to inform Gatt Server Service is ready.
     */
    void onGattServiceReady();

    /** 
     * Callback to inform state change of Gatt server connected remote device.
     * <p>The possible values of state in this callback are 
     *      <blockquote><b>STATE_NOT_INITIALIZED</b>        (int) 100
     *      <br><b>STATE_READY</b>                          (int) 110
     *      <br><b>STATE_LISTENING</b>                       (int) 130
     *      <br><b>STATE_CONNECTED</b>                      (int) 140</blockquote>
     *
     * <br>Parameter address is not valid for <b>STATE_NOT_INITIALIZED</b>, <b>STATE_READY</b> and <b>STATE_LISTENING</b>.
     * <br>It might contain unavailable content or <b>DEFAULT_ADDRESS</b> , which is 00:00:00:00:00:00.
     *
     * @param address Bluetooth MAC address of remote device which involves state changed.
     * @param state the new state.
     */ 
    void onGattServerStateChanged(String address, int state);
    
    /** 
     * Callback to inform service added successfully in Gatt server.    
     *
     * @param status Returns GATT_SUCCESS (0) if the service
     *               was added successfully.
     * @param srvcType service type
     * @param srvcUuid The service UUID that has been added
     */ 
    void onGattServerServiceAdded(in int status, in int srvcType,
                        in ParcelUuid srvcUuid);

    /** 
     * Callback to inform service deleted successfully in Gatt server.    
     *
     * @param status Returns GATT_SUCCESS (0) if the service
     *               was deleted successfully.
     * @param srvcType service type
     * @param srvcUuid The service UUID that has been deleted.
     */ 
    void onGattServerServiceDeleted(in int status, in int srvcType,
                        in ParcelUuid srvcUuid);
    
    /** 
     * A remote client has requested to read a local characteristic.
     * Callback to inform Gatt Client requests to read characteristic. Send Response is required.    
     *
     * <p>An application must call {@link INfCommandGattServer#reqGattServerSendResponse reqGattServerSendResponse}
     * to complete the request.
     *
     * @param address Bluetooth MAC address of remote device
     * @param requestId The ID of the request
     * @param offset Offset into the value of the characteristic
     * @param isLong true if have multiple read request
     * @param srvcType service type
     * @param srvcUuid sevice UUID
     * @param charUuid Characteristic UUID to be read
     */     
    void onGattServerCharacteristicReadRequest(in String address, in int requestId,
                                     in int offset, in boolean isLong,
                                     in int srvcType,
                                     in ParcelUuid srvcUuid,
                                     in ParcelUuid charUuid);
                       
    /** 
     * A remote client has requested to write to a local characteristic.
     * Callback to inform Gatt Client requests to write characteristic. Send Response is required
     * or not depends on responseNeeded.
     *
     * <p>An application must call {@link INfCommandGattServer#reqGattServerSendResponse reqGattServerSendResponse}
     * to complete the request.
     *
     * @param address Bluetooth MAC address of remote device
     * @param requestId The ID of the request
     * @param offset Value offset for partial request
     * @param preparedWrite true if this write operation should be queued for
     *                      later execution.
     * @param responseNeeded true if the remote device requires a response
     * @param srvcType Service type
     * @param srvcUuid Service UUID
     * @param charUuid Characteristic UUID to be written to.
     * @param value The value the client wants to assign to the characteristic
     */                                  
    void onGattServerCharacteristicWriteRequest(in String address, in int requestId,
                                     in int offset,
                                     in boolean preparedWrite,
                                     in boolean responseNeeded,
                                     in int srvcType,
                                     in ParcelUuid srvcUuid,
                                     in ParcelUuid charUuid,
                                     in byte[] value);
    
    /** 
     * A remote client has requested to read a local descriptor.
     * Callback to inform Gatt Client requests to read characteristic descriptor. Send Response is required.    
     *
     * <p>An application must call {@link INfCommandGattServer#reqGattServerSendResponse reqGattServerSendResponse}
     * to complete the request.
     *
     * @param address Bluetooth MAC address of remote device
     * @param requestId The ID of the request
     * @param offset Offset into the value of the descriptor
     * @param isLong true if have multiple read request
     * @param srvcType Service type
     * @param srvcUuid Service UUID
     * @param charUuid Characteristic UUID
     * @param descrUuid Descriptor UUID
     */     
    void onGattServerDescriptorReadRequest(in String address, in int requestId,
                                     in int offset, in boolean isLong,
                                     in int srvcType,
                                     in ParcelUuid srvcUuid,
                                     in ParcelUuid charUuid,
                                     in ParcelUuid descrUuid);
                                     
    /** 
     * A remote client has requested to write to a local descriptor.
     * Callback to inform Gatt Client requests to write characteristic descriptor. Send Response is required
     * or not depends on responseNeeded.
     *
     * <p>An application must call {@link INfCommandGattServer#reqGattServerSendResponse reqGattServerSendResponse}
     * to complete the request.
     *
     * @param address Bluetooth MAC address of remote device
     * @param requestId The ID of the request
     * @param offset The offset given for the value
     * @param preparedWrite true, if this write operation should be queued for
     *                      later execution.
     * @param responseNeeded true, if the remote device requires a response
     * @param srvcType Service type
     * @param srvcUuid Service UUID
     * @param charUuid Characteristic UUID
     * @param descrUuid Descriptor UUID to be written to.
     * @param value The value the client wants to assign to the descriptor
     */                                  
    void onGattServerDescriptorWriteRequest(in String address, in int requestId,
                                     in int offset,
                                     in boolean isPrep,
                                     in boolean responseNeeded,
                                     in int srvcType,
                                     in ParcelUuid srvcUuid,
                                     in ParcelUuid charUuid,
                                     in ParcelUuid descrUuid,
                                     in byte[] value);
                                     
    /** 
     * Execute all pending write operations for this device.
     * Callback to inform Gatt Client write request finishes only if multiple write requests are issued.
     *
     * <p>An application must call {@link INfCommandGattServer#reqGattServerSendResponse reqGattServerSendResponse}
     * to complete the request.
     *
     * @param address Bluetooth MAC address of remote device
     * @param requestId The ID of the request
     * @param execute Whether the pending writes should be executed (true) or
     *                cancelled (false)
     */                                                  
    void onGattServerExecuteWrite(in String address, in int requestId, in boolean execute);

}
