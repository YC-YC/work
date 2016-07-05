package com.nforetek.bt.demo.ui;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.nforetek.bt.aidl.UiCallbackBluetooth;
import com.nforetek.bt.aidl.UiCommand;
import com.nforetek.bt.res.NfDef;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;


import android.view.View;
import android.view.View.OnClickListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingPageActivity extends Activity {

    private static String TAG = "NfDemo_SettingPage";
    private static boolean D = true;

    private Handler mHandler = null;

    private UiCommand mCommand;

    // Member fields
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    private TextView text_bt;
    private TextView text_discoverable;
    private TextView text_state_hfp;  
    private TextView text_state_a2dp;
    private TextView text_state_avrcp;

    private TextView text_address_hfp;  
    private TextView text_address_a2dp;
    private TextView text_address_avrcp;

    private TextView text_state_role_mode;  

    private Button button_bt_on;
    private Button button_bt_off;
    private Button button_discoverable_on;
    private Button button_discoverable_off;    	
    private Button button_connect;
    private Button button_disconnect;
    private Button button_pair;
    private Button button_unpair;
    private Button button_role_car;
    private Button button_role_tablet;
    private Button button_scan;
    private Button button_back;

    private String selected_address = "";

    private String[] pairedDeviceArray = new String[]{};

    private ArrayList<String> device_founded_list = new ArrayList<String>();

    private static final int HANDLER_EVENT_UPDATE_ALL_STATE = 1;
    private static final int HANDLER_EVENT_UPDATE_BT_STATE = 2;
    private static final int HANDLER_EVENT_UPDATE_BT_DISCOVERABLE	 = 3;
    private static final int HANDLER_EVENT_UPDATE_BT_AUTO_RECONNECT = 4;
    private static final int HANDLER_EVENT_UPDATE_STATE_HFP = 5;
    private static final int HANDLER_EVENT_UPDATE_STATE_A2DP = 6;
    private static final int HANDLER_EVENT_UPDATE_STATE_AVRCP	 = 7;
    private static final int HANDLER_EVENT_SCAN_FINISHED = 11;
    private static final int HANDLER_EVENT_CLEAR_ALL_ARRAY_ADAPTER = 12;
    private static final int HANDLER_EVENT_CLEAR_NEW_ARRAY_ADAPTER = 13;
    private static final int HANDLER_EVENT_CLEAR_PAIRED_ARRAY_ADAPTER = 14;
    private static final int HANDLER_EVENT_UPDATE_NEW_DEVICE_ARRAY_ADAPTER = 15;
    private static final int HANDLER_EVENT_UPDATE_PAIRED_DEVICE_ARRAY_ADAPTER = 16;
    private static final int HANDLER_EVENT_ENABLE_BT_BUTTON = 17;

    private static String BT_STATE = "BT_STATE";
    private static String BT_DISCOVERABLE = "BT_DISCOVERABLE";
    private static String BT_AUTO_RECONNECT = "BT_AUTO_RECONNECT";
    private static String STATE_HFP = "STATE_HFP";
    private static String STATE_A2DP = "STATE_A2DP";
    private static String STATE_AVRCP = "STATE_AVRCP";
    private static String ADDRESS_HFP = "ADDRESS_HFP";
    private static String ADDRESS_A2DP = "ADDRESS_A2DP";
    private static String ADDRESS_AVRCP = "ADDRESS_AVRCP";
    private static String ENABLE_BT_BUTTON = "ENABLE_BT_BUTTON";
    private static String PAIRED_ELEMENTS = "PAIRED_ELEMENTS";
    private static String PAIRED_NAMES = "PAIRED_NAMES";
    private static String PAIRED_ADDRESSES = "PAIRED_ADDRESSES";
    private static String NEW_NAME = "NEW_NAME";
    private static String NEW_ADDRESSES = "NEW_ADDRESSES";

    private boolean isAutoAcceptPairingRequest = true;
    
    private int mBtRoleMode = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        setContentView(R.layout.setting_page);

        initHandler();
        initView();		

        bindService( new Intent( "com.nforetek.bt.START_UI_SERVICE" ), this.mConnection, BIND_AUTO_CREATE);

        IntentFilter filter = new IntentFilter();

        filter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");

        registerReceiver(mReceiver, filter);

    }	

    @Override
    public void onStart() 
    {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");
    }

    @Override
    public synchronized void onResume() 
    {
        super.onResume();
        if(D) Log.e(TAG, "++ ON Resume ++");
    }

    @Override
    public synchronized void onPause() 
    {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() 
    {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() 
    {
        try {
            mCommand.unregisterBtCallback(mCallbackBluetooth);
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }		
        unbindService(mConnection);
        unregisterReceiver(mReceiver);   
        mHandler = null;
        if(D) Log.e(TAG, "--- ON DESTROY ---");
        super.onDestroy();
    }

    private ServiceConnection mConnection = new ServiceConnection() 
    {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.e(TAG, "ready  onServiceConnected");
            mCommand = UiCommand.Stub.asInterface(service);
            if (mCommand == null) {
                Log.e(TAG,"mCommand is null!!");
                Toast.makeText(getApplicationContext(), "UiService is null!", Toast.LENGTH_SHORT).show();
                finish();
            }
            try {
                mCommand.registerBtCallback(mCallbackBluetooth);
                mCommand.reqBtPairedDevices();
                String adapterName = mCommand.getBtLocalName();
                Log.e(TAG,"nFore service version: " + mCommand.getNfServiceVersionName());
                updateState();

            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            Log.e(TAG, "end  onServiceConnected");
        }

        public void onServiceDisconnected(ComponentName	className) {
            Log.e(TAG, "onServiceDisconnected");	
            mCommand = null;
        }
    };

    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            v.setSelected(true);
            ((TextView)v).setPressed(true);
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            selected_address = info.substring(info.length() - 17);
            Log.e(TAG,"selected address : " + selected_address);
            if (mCommand != null) {
                try {
                    mCommand.setTargetAddress(selected_address);
                } 
                catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void initView()
    {
        // link with UI element

        text_bt = (TextView) findViewById(R.id.text_bt);
        text_discoverable = (TextView) findViewById(R.id.text_discoverable);
        text_state_hfp = (TextView) findViewById(R.id.text_state_hfp);
        text_state_a2dp = (TextView) findViewById(R.id.text_state_a2dp);
        text_state_avrcp = (TextView) findViewById(R.id.text_state_avrcp);
        text_state_role_mode = (TextView) findViewById(R.id.text_state_role_mode);

        text_address_hfp = (TextView) findViewById(R.id.text_address_hfp);
        text_address_a2dp = (TextView) findViewById(R.id.text_address_a2dp);
        text_address_avrcp = (TextView) findViewById(R.id.text_address_avrcp);

        button_bt_on = (Button) findViewById(R.id.button_bt_on);
        button_bt_off = (Button) findViewById(R.id.button_bt_off);
        button_discoverable_on = (Button) findViewById(R.id.button_discoverable_on);
        button_discoverable_off = (Button) findViewById(R.id.button_discoverable_off);	
        button_connect = (Button) findViewById(R.id.button_connect);
        button_disconnect = (Button) findViewById(R.id.button_disconnect);
        button_pair = (Button) findViewById(R.id.button_pair);
        button_unpair = (Button) findViewById(R.id.button_unpair);
        button_role_car = (Button) findViewById(R.id.button_role_car);
        button_role_tablet = (Button) findViewById(R.id.button_role_tablet);
        button_scan = (Button) findViewById(R.id.button_scan);
        button_back = (Button) findViewById(R.id.button_back);

        button_bt_on.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"button_bt_on onClicked");
                try {
                    mCommand.setBtEnable(true);

                } 
                catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        button_bt_off.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"button_bt_off onClicked");
                try {					
                    mCommand.setBtEnable(false);									
                } 
                catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        button_discoverable_on.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"button_discoverable_on onClicked");
                try {					
                    mCommand.setBtDiscoverableTimeout(0);									
                } 
                catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        button_discoverable_off.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"button_discoverable_off onClicked");
                try {					
                    mCommand.setBtDiscoverableTimeout(-1);							
                } 
                catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        button_connect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"button_connect onClicked");
                try {
                    if (!selected_address.equals("")) {
                        mCommand.reqBtConnectHfpA2dp(selected_address);
                    }					
                } 
                catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        button_disconnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"button_disconnect onClicked");
                try {					
                    mCommand.reqBtDisconnectAll();	
                } 
                catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });


        button_pair.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"button_pair onClicked");
                try {
                    if (!selected_address.equals("")) {
                        mCommand.reqBtPair(selected_address);
                    }					
                } 
                catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        button_unpair.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"button_unpair onClicked");				
                try {
                    if (!selected_address.equals("")) {
                        mCommand.reqBtUnpair(selected_address);
                    }					
                } 
                catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        button_role_car.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"button_role_car onClicked");
//                isAutoAcceptPairingRequest = true;
//                text_state_role_mode.setText("Car");
                try {
                    mCommand.switchBtRoleMode(1);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        button_role_tablet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"button_role_tablet onClicked");
//                isAutoAcceptPairingRequest = false;
//                text_state_role_mode.setText("Tablet");
                try {
                    mCommand.switchBtRoleMode(2);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });		

        button_scan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"button_scan onClicked");
                device_founded_list.clear();
                try {
                    mCommand.startBtDiscovery();

                    mNewDevicesArrayAdapter.clear();
                    setProgressBarIndeterminateVisibility(true);

                } 
                catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        button_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"button_back onClicked");
                try {
                    mCommand.cancelBtDiscovery();

                } 
                catch (RemoteException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setSelected(true);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setSelected(true);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);  

    }

    @SuppressLint("HandlerLeak")
    private void initHandler()
    {
        mHandler = new Handler(){
            public synchronized void handleMessage(Message msg) {
                Bundle bundle = msg.getData();;

                Log.v(TAG,"handleMessage : " + getHandlerEventName(msg.what));

                switch (msg.what) {
                    case HANDLER_EVENT_UPDATE_ALL_STATE:

                        text_bt.setText(bundle.getString(BT_STATE));
                        text_discoverable.setText(bundle.getString(BT_DISCOVERABLE));
                        //                        text_state_auto_accept.setText(bundle.getString(BT_AUTO_RECONNECT));
                        //                        text_state_auto_accept.setText(isAutoAcceptPairingRequest ? "ON" : "OFF");
                        text_state_hfp.setText(bundle.getString(STATE_HFP));
                        text_state_a2dp.setText(bundle.getString(STATE_A2DP));
                        text_state_avrcp.setText(bundle.getString(STATE_AVRCP));

                        text_address_hfp.setText(bundle.getString(ADDRESS_HFP));
                        text_address_a2dp.setText(bundle.getString(ADDRESS_A2DP));
                        text_address_avrcp.setText(bundle.getString(ADDRESS_AVRCP));					

                        break;
                    case HANDLER_EVENT_UPDATE_BT_STATE :
                        text_bt.setText(bundle.getString(BT_STATE));
                        break;
                    case HANDLER_EVENT_UPDATE_BT_DISCOVERABLE :
                        text_discoverable.setText(bundle.getString(BT_DISCOVERABLE));
                        break;
                        //                    case HANDLER_EVENT_UPDATE_BT_AUTO_RECONNECT :
                        //                        text_state_autoreconnect.setText(bundle.getString(BT_AUTO_RECONNECT));
                        //                        break;
                    case HANDLER_EVENT_UPDATE_STATE_HFP :
                        text_state_hfp.setText("HFP : " + bundle.getString(STATE_HFP));
                        text_address_hfp.setText(bundle.getString(ADDRESS_HFP));
                        break;
                    case HANDLER_EVENT_UPDATE_STATE_A2DP :
                        text_state_a2dp.setText("A2DP : " + bundle.getString(STATE_A2DP));
                        text_address_a2dp.setText(bundle.getString(ADDRESS_A2DP));
                        break;
                    case HANDLER_EVENT_UPDATE_STATE_AVRCP :
                        text_state_avrcp.setText("AVRCP : " + bundle.getString(STATE_AVRCP));
                        text_address_avrcp.setText(bundle.getString(ADDRESS_AVRCP));
                        break;
                    case HANDLER_EVENT_SCAN_FINISHED :
                        setProgressBarIndeterminateVisibility(false);
                        if (mNewDevicesArrayAdapter.getCount() == 0) {
                            mNewDevicesArrayAdapter.add("No Device");
                        }
                        break;
                    case HANDLER_EVENT_CLEAR_ALL_ARRAY_ADAPTER :
                        mPairedDevicesArrayAdapter.clear();
                        mNewDevicesArrayAdapter.clear();
                        break;
                    case HANDLER_EVENT_CLEAR_NEW_ARRAY_ADAPTER :
                        mNewDevicesArrayAdapter.clear();
                        break;
                    case HANDLER_EVENT_CLEAR_PAIRED_ARRAY_ADAPTER :
                        mPairedDevicesArrayAdapter.clear();
                        break;
                    case HANDLER_EVENT_UPDATE_NEW_DEVICE_ARRAY_ADAPTER :
                        String name = bundle.getString(NEW_NAME);
                        String address = bundle.getString(NEW_ADDRESSES);
                        mNewDevicesArrayAdapter.add(name + "\n" + address);
                        break;
                    case HANDLER_EVENT_UPDATE_PAIRED_DEVICE_ARRAY_ADAPTER :
                        int elements = bundle.getInt(PAIRED_ELEMENTS);
                        String[] names = bundle.getStringArray(PAIRED_NAMES);
                        String[] addresses = bundle.getStringArray(PAIRED_ADDRESSES);
                        mPairedDevicesArrayAdapter.clear();
                        if (elements > 0) {
                            for (int i = 0 ; i < elements ; i++) {
                                mPairedDevicesArrayAdapter.add(names[i] + "\n" + addresses[i]);
                            }
                        } 
                        else {
                            mPairedDevicesArrayAdapter.add("No Device");
                        }
                        break;
                    case HANDLER_EVENT_ENABLE_BT_BUTTON :
                        button_bt_on.setEnabled(bundle.getString(ENABLE_BT_BUTTON).equals("true"));
                        break;

                };
            }
        };
    }

    private void sendHandlerMessage(int what, String[] keys, String[] values)
    {
        Message msg = Message.obtain(mHandler, what);
        if (keys != null && values != null) {
            Bundle b = new Bundle();
            for (int i = 0 ; i < keys.length ; i++) {
                b.putString(keys[i], values[i]);
            }
            msg.setData(b);
        }
        mHandler.sendMessage(msg);
    }

    private String getHandlerEventName(int what)
    {		
        switch (what) {
            case HANDLER_EVENT_UPDATE_ALL_STATE:
                return "HANDLER_EVENT_UPDATE_ALL_STATE";
            case HANDLER_EVENT_UPDATE_BT_STATE:
                return "HANDLER_EVENT_UPDATE_BT_STATE";
            case HANDLER_EVENT_UPDATE_BT_DISCOVERABLE:
                return "HANDLER_EVENT_UPDATE_BT_DISCOVERABLE";
            case HANDLER_EVENT_UPDATE_BT_AUTO_RECONNECT:
                return "HANDLER_EVENT_UPDATE_BT_AUTO_RECONNECT";
            case HANDLER_EVENT_UPDATE_STATE_HFP:
                return "HANDLER_EVENT_UPDATE_STATE_HFP";
            case HANDLER_EVENT_UPDATE_STATE_A2DP:
                return "HANDLER_EVENT_UPDATE_STATE_A2DP";
            case HANDLER_EVENT_UPDATE_STATE_AVRCP:
                return "HANDLER_EVENT_UPDATE_STATE_AVRCP";
            case HANDLER_EVENT_SCAN_FINISHED:
                return "HANDLER_EVENT_SCAN_FINISHED";
            case HANDLER_EVENT_CLEAR_ALL_ARRAY_ADAPTER:
                return "HANDLER_EVENT_CLEAR_ALL_ARRAY_ADAPTER";
            case HANDLER_EVENT_CLEAR_NEW_ARRAY_ADAPTER:
                return "HANDLER_EVENT_CLEAR_NEW_ARRAY_ADAPTER";
            case HANDLER_EVENT_CLEAR_PAIRED_ARRAY_ADAPTER:
                return "HANDLER_EVENT_CLEAR_PAIRED_ARRAY_ADAPTER";
            case HANDLER_EVENT_UPDATE_NEW_DEVICE_ARRAY_ADAPTER:
                return "HANDLER_EVENT_UPDATE_NEW_DEVICE_ARRAY_ADAPTER";
            case HANDLER_EVENT_UPDATE_PAIRED_DEVICE_ARRAY_ADAPTER:
                return "HANDLER_EVENT_UPDATE_PAIRED_DEVICE_ARRAY_ADAPTER";
            case HANDLER_EVENT_ENABLE_BT_BUTTON:
                return "HANDLER_EVENT_ENABLE_BT_BUTTON";
        }
        return "Unknown Event !!";
    }

    /**
     * Service callback
     * 
     */
    private UiCallbackBluetooth mCallbackBluetooth = new UiCallbackBluetooth.Stub() 
    {

        @Override
        public void onBluetoothServiceReady() throws RemoteException {
            Log.i(TAG,"onBluetoothServiceReady");

        };

        @Override
        public void onAdapterStateChanged(int prevState, int newState) throws RemoteException {
            Log.i(TAG,"onAdapterStateChanged() state: " + prevState + "->" + newState);
            String bluetoothStateString = "";

            if (newState == NfDef.BT_STATE_OFF) {
                bluetoothStateString = "Off";
                sendHandlerMessage(HANDLER_EVENT_CLEAR_NEW_ARRAY_ADAPTER, null, null);
            }
            else if (newState == NfDef.BT_STATE_TURNING_ON) {
                bluetoothStateString = "Turning On";
            }
            else if (newState == NfDef.BT_STATE_ON) {
                bluetoothStateString = "On";
                mCommand.reqBtPairedDevices();
            }
            else if (newState == NfDef.BT_STATE_TURNING_OFF) {

                bluetoothStateString = "Turning Off";
                sendHandlerMessage(HANDLER_EVENT_CLEAR_ALL_ARRAY_ADAPTER, null, null);

            }

            if (newState == NfDef.BT_STATE_OFF || newState == NfDef.BT_STATE_ON) {

                sendHandlerMessage(HANDLER_EVENT_ENABLE_BT_BUTTON, new String[]{ENABLE_BT_BUTTON}, new String[]{"true"});
                updateState();
            }
            else if (newState == NfDef.BT_STATE_TURNING_ON || newState == NfDef.BT_STATE_TURNING_OFF) {

                sendHandlerMessage(HANDLER_EVENT_ENABLE_BT_BUTTON, new String[]{ENABLE_BT_BUTTON}, new String[]{"false"});
            }

            if (newState >= NfDef.BT_STATE_OFF && newState <= NfDef.BT_STATE_TURNING_OFF) {

                sendHandlerMessage(HANDLER_EVENT_UPDATE_BT_STATE, new String[]{BT_STATE}, new String[]{bluetoothStateString});
            }

            if (newState == NfDef.BT_SCAN_FINISH) {

                sendHandlerMessage(HANDLER_EVENT_SCAN_FINISHED, null, null);
            }
                        
        }

        @Override
        public void onAdapterDiscoverableModeChanged(int prevState, int newState)
                throws RemoteException {
            Log.i(TAG,"onAdapterDiscoverableModeChanged() " + prevState + "->" + newState);
            String bluetoothDiscoverableString = "";
            if (newState == NfDef.BT_MODE_NONE) {
                bluetoothDiscoverableString = "None";
            }
            else if (newState == NfDef.BT_MODE_CONNECTABLE) {
                bluetoothDiscoverableString = "Connectable";
            }
            else if (newState == NfDef.BT_MODE_CONNECTABLE_DISCOVERABLE) {
                bluetoothDiscoverableString = "Connectable\nDisconnectable";
            }
            sendHandlerMessage(HANDLER_EVENT_UPDATE_BT_DISCOVERABLE, new String[]{BT_DISCOVERABLE}, new String[]{bluetoothDiscoverableString});
        }

        @Override
        public void onAdapterDiscoveryStarted() throws RemoteException {
            Log.i(TAG,"onAdapterDiscoveryStarted()");

        }

        @Override
        public void onAdapterDiscoveryFinished() throws RemoteException {
            Log.i(TAG,"onAdapterDiscoveryFinished()");

        }

        @Override
        public void retPairedDevices(int elements, String[] address, String[] name,
                int[] supportProfile, byte[] category) throws RemoteException {
            Log.i(TAG,"retPairedDevices() elements: " + elements);

            for(int i=0; i<supportProfile.length; i++) {
                Log.e(TAG,"name: " + name[i] + " supportProfile: " + supportProfile[i]);
                if (supportProfile[i] > 0) {
                    name[i] += "  (";
                }
                if ((supportProfile[i]& NfDef.PROFILE_HFP) > 0) {
                    name[i] += " HFP";
                }
                if ((supportProfile[i]& NfDef.PROFILE_A2DP) > 0) {
                    name[i] += " A2DP";
                }
                if ((supportProfile[i]& NfDef.PROFILE_AVRCP_14) > 0) {
                    name[i] += " AVRCP1.4";
                }
                else if ((supportProfile[i]& NfDef.PROFILE_AVRCP_13) > 0) {
                    name[i] += " AVRCP1.3";
                }
                else if ((supportProfile[i]& NfDef.PROFILE_AVRCP) > 0) {
                    name[i] += " AVRCP";
                }
                if ((supportProfile[i]& NfDef.PROFILE_PBAP) > 0) {
                    name[i] += " PBAP";
                }
                if ((supportProfile[i]& NfDef.PROFILE_MAP) > 0) {
                    name[i] += " MAP";
                }
                if ((supportProfile[i]& NfDef.PROFILE_IAP) > 0) {
                    name[i] += " iAP";
                }
                if (supportProfile[i] > 0) {
                    name[i] += " )";
                }


            }

            // If there are paired devices, add each one to the ArrayAdapter

            Message msg = Message.obtain(mHandler, HANDLER_EVENT_UPDATE_PAIRED_DEVICE_ARRAY_ADAPTER);

            Bundle b = new Bundle();

            b.putInt(PAIRED_ELEMENTS, elements);
            b.putStringArray(PAIRED_NAMES, name);
            b.putStringArray(PAIRED_ADDRESSES, address);

            msg.setData(b);         
            mHandler.sendMessage(msg);

            pairedDeviceArray = address;


        }

        @Override
        public void onDeviceFound(String address, String name, byte category)
                throws RemoteException {
            Log.i(TAG,"onDeviceFound() " + address + " name: " + name);
            // When discovery finds a device
            boolean isPairedDevice = false;
            for (int i = 0 ; i < pairedDeviceArray.length ; i ++) {
                if (address.equals(pairedDeviceArray[i])) {
                    isPairedDevice = true;
                    break;
                }
            }

            if (!isPairedDevice) {
                if (!device_founded_list.contains(address)) {

                    device_founded_list.add(address);

                    sendHandlerMessage(HANDLER_EVENT_UPDATE_NEW_DEVICE_ARRAY_ADAPTER, new String[]{NEW_NAME, NEW_ADDRESSES}, new String[]{name, address});

                }
            }   
        }

        @Override
        public void onDeviceBondStateChanged(String address, String name, int prevState,
                int newState) throws RemoteException {
            Log.i(TAG,"onDeviceBondStateChanged() " + address + " name: " + name + " state: " + prevState + "->" + newState);
            mCommand.reqBtPairedDevices();
        }

        @Override
        public void onDeviceUuidsUpdated(String address, String name, int supportProfile)
                throws RemoteException {
            Log.i(TAG,"onDeviceUuidsUpdated() " + address + " name: " + name + " supportProfile: " + supportProfile);
            mCommand.reqBtPairedDevices();
        }

        @Override
        public void onLocalAdapterNameChanged(String name) throws RemoteException {
            Log.i(TAG,"onLocalAdapterNameChanged() name: " + name);

        }

        @Override
        public void onHfpStateChanged(String address, int prevState, int newState)
                throws RemoteException {
            Log.i(TAG,"onHfpStateChanged() " + address + " state: " + prevState + "->" + newState);
            String[] keyArray = new String[] {
                    STATE_HFP,
                    ADDRESS_HFP
            };
            String stateString = "?";
            if (newState >= NfDef.STATE_CONNECTED) {
                stateString = "On";
            }
            else if (newState == NfDef.STATE_CONNECTING || newState == NfDef.STATE_DISCONNECTING) {
                stateString = "Ing";
            }
            else if (newState == NfDef.STATE_READY) {
                stateString = "Off";
            }
            String[] valueArray = new String[] {
                    stateString, 
                    newState >= NfDef.STATE_CONNECTED ? address.substring(9, 17) : "00:00:00"
            };
            sendHandlerMessage(HANDLER_EVENT_UPDATE_STATE_HFP, keyArray, valueArray);

            keyArray = null;
            valueArray = null;           
        }

        @Override
        public void onA2dpStateChanged(String address, int prevState, int newState)
                throws RemoteException {
            Log.i(TAG,"onA2dpStateChanged() " + address + " state: " + prevState + "->" + newState);
            String[] keyArray = new String[] {
                    STATE_A2DP,
                    ADDRESS_A2DP
            };
            String stateString = "?";
            if (newState >= NfDef.STATE_CONNECTED) {
                stateString = "On";
            }
            else if (newState == NfDef.STATE_CONNECTING || newState == NfDef.STATE_DISCONNECTING) {
                stateString = "Ing";
            }
            else if (newState == NfDef.STATE_READY) {
                stateString = "Off";
            }
            String[] valueArray = new String[] {
                    stateString, 
                    newState >= NfDef.STATE_CONNECTED ? address.substring(9, 17) : "00:00:00"
            };
            sendHandlerMessage(HANDLER_EVENT_UPDATE_STATE_A2DP, keyArray, valueArray);

            keyArray = null;
            valueArray = null;            
        }

        @Override
        public void onAvrcpStateChanged(String address, int prevState, int newState)
                throws RemoteException {
            Log.i(TAG,"onAvrcpStateChanged() " + address + " state: " + prevState + "->" + newState);
            String[] keyArray = new String[] {
                    STATE_AVRCP,
                    ADDRESS_AVRCP
            };
            String stateString = "?";
            if (newState >= NfDef.STATE_CONNECTED) {
                stateString = "On";
            }
            else if (newState == NfDef.STATE_CONNECTING || newState == NfDef.STATE_DISCONNECTING) {
                stateString = "Ing";
            }
            else if (newState == NfDef.STATE_READY) {
                stateString = "Off";
            }
            String[] valueArray = new String[] {
                    stateString, 
                    newState >= NfDef.STATE_CONNECTED ? address.substring(9, 17) : "00:00:00"
            };
            sendHandlerMessage(HANDLER_EVENT_UPDATE_STATE_AVRCP, keyArray, valueArray);

            keyArray = null;
            valueArray = null;
        }

        @Override
        public void onDeviceOutOfRange(String address) throws RemoteException {
            Log.i(TAG,"onDeviceOutOfRange() " + address);

        }
        
        @Override
        public void onBtRoleModeChanged(int mode) throws RemoteException {
            Log.i(TAG,"onBtRoleModeChanged() " + mode);
            mBtRoleMode = mode;
            runOnUiThread(new Runnable() {
                
                @Override
                public void run() {
                    switch(mBtRoleMode) {
                        case 1:
                            text_state_role_mode.setText("Car");
                            break;
                        case 2:
                            text_state_role_mode.setText("Tablet");
                            break;
                        default:
                            text_state_role_mode.setText("Unknown");
                    }
                }
            });
        }
        
    };


    private void updateState() {
        if (mCommand == null) {
            return;
        }
        try {
            String hfpConnectedAddress = mCommand.getHfpConnectedAddress();
            String a2dpConnectedAddress = mCommand.getA2dpConnectedAddress();
            String avrcpConnectedAddress = mCommand.getAvrcpConnectedAddress();

            int hfpState = mCommand.getHfpConnectionState();
            int a2dpState = mCommand.getA2dpConnectionState();
            int avrcpState = mCommand.getAvrcpConnectionState();

            String hfpStateString = "";
            String a2dpStateString = "";
            String avrcpStateString = "";

            Log.e(TAG,"Piggy HFP address: " + hfpConnectedAddress);
            Log.e(TAG,"Piggy A2DP address: " + a2dpConnectedAddress);
            Log.e(TAG,"Piggy AVRCP address: " + avrcpConnectedAddress);

            Log.e(TAG,"Piggy hfpState: " + hfpState);
            Log.e(TAG,"Piggy a2dpState: " + a2dpState);
            Log.e(TAG,"Piggy avrcpState: " + avrcpState);

            if (hfpState >= NfDef.STATE_CONNECTED) {
                hfpStateString = "HFP : On";
            }
            else if (hfpState == NfDef.STATE_CONNECTING) {
                hfpStateString = "HFP : Ing";
            }
            else if (hfpState == NfDef.STATE_READY) {
                hfpStateString = "HFP : Off";
            }
            else {
                hfpStateString = "HFP : " + hfpState;
                Log.e(TAG,"Unknown HFP state: " + hfpState);
            }

            if (a2dpState >= NfDef.STATE_CONNECTED) {
                a2dpStateString = "A2DP : On";
            }
            else if (a2dpState == NfDef.STATE_CONNECTING) {
                a2dpStateString = "A2DP : Ing";
            }
            else if (a2dpState == NfDef.STATE_READY) {
                a2dpStateString = "A2DP : Off";
            }
            else {
                a2dpStateString = "A2DP : " + a2dpState;
                Log.e(TAG,"Unknown A2DP state: " + a2dpState);
            }

            if (avrcpState >= NfDef.STATE_CONNECTED) {
                avrcpStateString = "AVRCP : On";
            }
            else if (avrcpState == NfDef.STATE_CONNECTING) {
                avrcpStateString = "AVRCP : Ing";
            }
            else if (avrcpState == NfDef.STATE_READY) {
                avrcpStateString = "AVRCP : Off";
            }
            else {
                avrcpStateString = "AVRCP : " + avrcpState;
                Log.e(TAG,"Unknown AVRCP state: " + avrcpState);
            }


            String[] eventArray = new String[]{
                    BT_STATE,
                    BT_DISCOVERABLE,
                    BT_AUTO_RECONNECT,
                    STATE_HFP,
                    ADDRESS_HFP,
                    STATE_A2DP,
                    ADDRESS_A2DP,
                    STATE_AVRCP,
                    ADDRESS_AVRCP

            };

            String[] valueArray =  new String[]{
                    mCommand.isBtEnabled() ? "On" : "Off",
                            mCommand.isBtDiscoverable() ? "On" : "Off",
                                    mCommand.isBtAutoConnectEnable() ? "On" : "Off",
                                            hfpStateString,
                                            hfpConnectedAddress.substring(9, 17),
                                            a2dpStateString,
                                            a2dpConnectedAddress.substring(9, 17),
                                            avrcpStateString,
                                            avrcpConnectedAddress.substring(9, 17)
            };

            sendHandlerMessage(HANDLER_EVENT_UPDATE_ALL_STATE, eventArray, valueArray);

            mBtRoleMode = mCommand.getBtRoleMode();
            runOnUiThread(new Runnable() {
                
                @Override
                public void run() {
                    switch(mBtRoleMode) {
                        case 1:
                            text_state_role_mode.setText("Car");
                            break;
                        case 2:
                            text_state_role_mode.setText("Tablet");
                            break;
                        default:
                            text_state_role_mode.setText("Unknown");
                    }
                }
            });
            
            eventArray = null;
            valueArray = null;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction(); 
            Log.e(TAG,"Piggy Check action: " + action);
            if (action.equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
                BluetoothDevice device =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int type = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT,
                        BluetoothDevice.ERROR);
                Log.e(TAG,"Piggy Check type: " + type);
                if (isAutoAcceptPairingRequest) {
                    if (type == BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION) {
                        Log.e(TAG,"PAIRING_VARIANT_PASSKEY_CONFIRMATION");
                        try {
                            device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
                            device.getClass().getMethod("cancelPairingUserInput", boolean.class).invoke(device);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    else if (type == BluetoothDevice.PAIRING_VARIANT_PIN){
                        Log.e(TAG,"PAIRING_VARIANT_PIN");
                        try {
                            setPin(device.getClass(), device, strPsw);    
                            createBond(device.getClass(), device);
                            device.getClass().getMethod("cancelPairingUserInput", boolean.class).invoke(device);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Log.e(TAG,"Unkown paring type" + type);
                    }
                }
            }
        }
    };

    private static final String strPsw = "0000";

    static public boolean setPin(Class btClass, BluetoothDevice btDevice,  
            String str) throws Exception {  
        try {             
            Method setPinMethod = btClass.getDeclaredMethod("setPin",new Class[] { byte[].class });  
            Boolean returnValue = (Boolean) setPinMethod.invoke(btDevice,  
                    new Object[] { str.getBytes() });  
            if(D) Log.e("returnValue", "" + returnValue);  
        } catch (SecurityException e) {  
            // throw new RuntimeException(e.getMessage());  
            e.printStackTrace();  
        } catch (IllegalArgumentException e) {  
            // throw new RuntimeException(e.getMessage());  
            e.printStackTrace();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return true;  
    }

    static public boolean createBond(Class btClass,BluetoothDevice btDevice) throws Exception {  
        Method createBondMethod = btClass.getMethod("createBond");  
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);  
        return returnValue.booleanValue();
    } 
}
