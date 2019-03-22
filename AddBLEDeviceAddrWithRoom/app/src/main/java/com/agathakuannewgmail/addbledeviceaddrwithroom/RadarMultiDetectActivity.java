package com.agathakuannewgmail.addbledeviceaddrwithroom;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ImageButton;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RadarMultiDetectActivity extends AppCompatActivity {
    private final static String TAG = "RadarMultiDetectActivity";

    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private boolean mConnectedtoRead = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private String mDeviceName;
    private String mDeviceAddress;
    private List<String> roomName;
    private GlobalVariables gv;
    private String mConnectedDeviceAddr = "null";
    private Boolean mIsNotice = false;

    private TextView mShowRoom1,mShowRoom2,mShowRoom3,mShowRoom4;

    private ImageButton mReceivedThread1,mStopReceivedThread1;
    private Handler mHandler = new Handler();;

    private TextView mPrintfTextView;

    private NotificationHelper mNotificationHelper;
    private static final int    DEFAULT_ID = 1001;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            //開始輪巡不同的裝置
            mHandler.post(mRecvTimer);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;

                updateConnectionState(R.string.connected);

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                mConnectedtoRead = false;
                updateConnectionState(R.string.disconnected);
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };
    private void clearUI() {}

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mConnectionState.setText(resourceId);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar_multi_detect);
        gv = (GlobalVariables)getApplicationContext();

        mShowRoom1 = (TextView)findViewById(R.id.show_room1);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(MainActivity.EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(MainActivity.EXTRAS_DEVICE_ADDRESS);

        mShowRoom1.setText(mDeviceName+" at "+mDeviceAddress);

        mReceivedThread1 = (ImageButton)findViewById(R.id.receive_bt);
        mStopReceivedThread1 =(ImageButton)findViewById(R.id.stop_recv);
        mReceivedThread1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsNotice = true;
            }
        });
        mStopReceivedThread1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsNotice = false;
            }
        });


        roomName = gv.getNameList();
        mShowRoom2 = (TextView)findViewById(R.id.show_room2);
        mShowRoom3 = (TextView)findViewById(R.id.show_room3);
        mShowRoom4 = (TextView)findViewById(R.id.show_room4);

        if(roomName.size()>1)
            mShowRoom2.setText(roomName.get(1)+" at "+gv.getRoomAddr(roomName.get(1)));
        if(roomName.size()>2)
            mShowRoom3.setText(roomName.get(2)+" at "+gv.getRoomAddr(roomName.get(2)));
        if(roomName.size()>3)
            mShowRoom4.setText(roomName.get(3)+" at "+gv.getRoomAddr(roomName.get(3)));

        mPrintfTextView = (TextView)findViewById(R.id.printfTextView);


        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        //https://blog.csdn.net/jdsjlzx/article/details/84327815
        mNotificationHelper = new NotificationHelper(this);
    }

    Runnable mRecvTimer = new Runnable() {
        @Override
        public void run() {
            mHandler.post(room1receivedThread);
        }
    };

    Runnable room1receivedThread = new Runnable()
    {
        @Override
        public void run()
        {
            printf("in received thread 1");
            List<String> roomList = gv.getNameList();
            int deviceToReceive = roomList.size();

            if((roomList.size()>0)&&(!"null".equals(gv.getRoomAddr(roomList.get(0)))))
            {
                if((mConnectedtoRead == false)&&(mConnected== false))
                {
                    mConnectedDeviceAddr = gv.getRoomAddr(roomList.get(0));
                    mBluetoothLeService.connect(gv.getRoomAddr(roomList.get(0)));
                    mConnectedtoRead = true;
                    mHandler.postDelayed(room1receivedThread,3000);

                    printf("start connect");
                }
                else if((mConnectedtoRead == true)&&(mConnected== false))
                {
                    mHandler.postDelayed(room1receivedThread,200);
                    printf("wait for connect");
                }
                else if((mConnectedtoRead == true)&&(mConnected== true))
                {
                    startNotify(0,3,0);
                    mHandler.postDelayed(room1receivedThread,4000);
                    printf("is reading");
                }
                else if((mConnectedtoRead == false)&&(mConnected== true))
                {
                    mBluetoothLeService.disconnect();
                    printf("disconnect");

                    mHandler.postDelayed(room2receivedThread,3000);
                }
            }else
            {
                mShowRoom1.setText("no room 1");
                mConnectedDeviceAddr = "null";
                mHandler.postDelayed(room2receivedThread,3000);
            }

        }
    };

    Runnable room2receivedThread = new Runnable()
    {
        @Override
        public void run()
        {
            printf("in received thread 2");
            List<String> roomList = gv.getNameList();
            int deviceToReceive = roomList.size();

            if(deviceToReceive>1)
            {
                if((mConnectedtoRead == false)&&(mConnected== false))
                {
                    mConnectedDeviceAddr = gv.getRoomAddr(roomList.get(1));
                    mBluetoothLeService.connect(gv.getRoomAddr(roomList.get(1)));
                    mConnectedtoRead = true;
                    mHandler.postDelayed(room2receivedThread,3000);

                    printf("start connect");
                }
                else if((mConnectedtoRead == true)&&(mConnected== false))
                {
                    mHandler.postDelayed(room2receivedThread,500);
                    printf("wait for connect");
                }
                else if((mConnectedtoRead == true)&&(mConnected== true))
                {
                    startNotify(1,3,0);
                    mHandler.postDelayed(room2receivedThread,4000);
                    printf("is reading");
                }
                else if((mConnectedtoRead == false)&&(mConnected== true))
                {
                    mBluetoothLeService.disconnect();
                    printf("disconnect");

                    mHandler.postDelayed(room3receivedThread,3000);
                }
            }else
            {
                mShowRoom2.setText("no room 2");
                mConnectedDeviceAddr = "null";
                mHandler.postDelayed(room3receivedThread,3000);
            }

        }
    };

    Runnable room3receivedThread = new Runnable()
    {
        @Override
        public void run()
        {
            printf("in received thread 3");
            List<String> roomList = gv.getNameList();
            int deviceToReceive = roomList.size();

            if(deviceToReceive>2)
            {
                if((mConnectedtoRead == false)&&(mConnected== false))
                {

                    mConnectedDeviceAddr = gv.getRoomAddr(roomList.get(2));
                    mBluetoothLeService.connect(gv.getRoomAddr(roomList.get(2)));
                    mConnectedtoRead = true;
                    mHandler.postDelayed(room3receivedThread,3000);

                    printf("start connect");
                }
                else if((mConnectedtoRead == true)&&(mConnected== false))
                {
                    mHandler.postDelayed(room3receivedThread,500);
                    printf("wait for connect");
                }
                else if((mConnectedtoRead == true)&&(mConnected== true))
                {
                    startNotify(2,3,0);
                    mHandler.postDelayed(room3receivedThread,4000);
                    printf("is reading");
                }
                else if((mConnectedtoRead == false)&&(mConnected== true))
                {
                    mBluetoothLeService.disconnect();
                    printf("disconnect");

                    mHandler.postDelayed(room4receivedThread,3000);
                }
            }else
            {
                mShowRoom3.setText("no room 3");
                mConnectedDeviceAddr = "null";
                mHandler.postDelayed(room4receivedThread,3000);
            }

        }
    };

    Runnable room4receivedThread = new Runnable()
    {
        @Override
        public void run()
        {
            printf("in received thread 4");
            List<String> roomList = gv.getNameList();

            if(roomList.size()>3)
            {
                if((mConnectedtoRead == false)&&(mConnected== false))
                {
                    mConnectedDeviceAddr = gv.getRoomAddr(roomList.get(3));
                    mBluetoothLeService.connect(gv.getRoomAddr(roomList.get(3)));
                    mConnectedtoRead = true;
                    mHandler.postDelayed(room4receivedThread,3000);

                    printf("start connect");
                }
                else if((mConnectedtoRead == true)&&(mConnected== false))
                {
                    mHandler.postDelayed(room4receivedThread,500);
                    printf("wait for connect");
                }
                else if((mConnectedtoRead == true)&&(mConnected== true))
                {
                    startNotify(3,3,0);

                    mHandler.postDelayed(room4receivedThread,4000);
                    printf("is reading");
                }
                else if((mConnectedtoRead == false)&&(mConnected== true))
                {
                    mBluetoothLeService.disconnect();
                    printf("disconnect");

                    mHandler.postDelayed(room1receivedThread,3000);
                }
            }else
            {
                mConnectedDeviceAddr = "null";
                mShowRoom4.setText("no room 4");
                mHandler.postDelayed(room1receivedThread,3000);
            }

        }
    };

    private void startNotify(int roomNum, int serviceNum, int charNum)
    {
        List<String> roomList = gv.getNameList();
;
        if (mGattCharacteristics != null)
        {
            //Alex:第三service 的第0 characteristic　這個日後要再優化
            final BluetoothGattCharacteristic characteristic=
                    mGattCharacteristics.get(serviceNum).get(charNum);

            final int charaProp = characteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0)
            {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService.setCharacteristicNotification(
                            mNotifyCharacteristic, false);
                    mNotifyCharacteristic = null;
                }
                mBluetoothLeService.readCharacteristic(characteristic);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0)
            {
                mNotifyCharacteristic = characteristic;
                mBluetoothLeService.setCharacteristicNotification(
                        characteristic, true);
            }
        }
        mConnectedtoRead = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        Log.d(TAG,"multidetect onPause");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        Log.d(TAG,"multidetect onResume");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothLeService.disconnect();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        mHandler.removeCallbacksAndMessages(null);
        Log.d(TAG,"multidetect onDestroy");
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void displayData(String data)
    {
        if ((data != null)&&(mIsNotice == true))
        {

            String[] tokens = data.split(",");
            if("@".equals(tokens[0]))
            {
                List<String> roomList = gv.getNameList();
                Double detectSpeed = tryParseDouble(tokens[2])/100.0;
                String dir = "";

                if("a".equals(tokens[1]))
                    dir = "apart";
                else if("b".equals(tokens[1]))
                    dir="depart";
                else if("c".equals(tokens[1]))
                    dir="no detect";

                for (int i =0;i<roomList.size();i++)
                {
                    switch(i)
                    {
                      case 0:
                        if(mConnectedDeviceAddr.equals(gv.getRoomAddr(roomList.get(0))))
                             setDisplayNotification(detectSpeed,dir,mShowRoom1,1);
                          break;
                      case 1:
                          if(mConnectedDeviceAddr.equals(gv.getRoomAddr(roomList.get(1))))
                              setDisplayNotification(detectSpeed,dir,mShowRoom2,2);
                          break;
                     case 2:
                         if(mConnectedDeviceAddr.equals(gv.getRoomAddr(roomList.get(2))))
                            setDisplayNotification(detectSpeed,dir,mShowRoom3,3);
                          break;
                      case 3:
                          if(mConnectedDeviceAddr.equals(gv.getRoomAddr(roomList.get(3))))
                              setDisplayNotification(detectSpeed,dir,mShowRoom4,4);
                         break;
                     default:
                         break;
                    }
                }
            }

        }
    }

    public void setDisplayNotification(Double speedDetect, String dirDetect, TextView displayView, int roomnum)
    {
        Double speedThers = gv.getSpeedThershold();
        String targetDir = gv.getDeretctionThershold();

        if(speedThers<= speedDetect)
        {
            displayView.setTextColor(Color.rgb(255, 0, 0));
            showNotification("From "+String.valueOf(roomnum)+" room","speed = "+String.valueOf(speedDetect));
        }
        else
            displayView.setTextColor(Color.rgb(0, 0, 255));

        if(targetDir.equals("all")){}else if(dirDetect.equals(targetDir))
        {
            displayView.setBackgroundColor(Color.DKGRAY);
        }
        else
            displayView.setBackgroundColor(Color.WHITE);

        displayView.setText("current speed :"+speedDetect+"\r\n"+"direction:"+dirDetect);
    }

    public static Double tryParseDouble(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );

        printf(String.valueOf(gattServiceData)+String.valueOf(gattCharacteristicData));
    }

    private void printf(String s)
    {
        s = s+"\r\n";
        Log.d("PRINTF",s);
        mPrintfTextView.setText(s);
    }

    public void showNotification(String title, String content)
    {
        NotificationCompat.Builder builder = mNotificationHelper.getNotification(title, content);
        mNotificationHelper.notify(DEFAULT_ID, builder);
    }
}
