package com.agathakuannewgmail.addbledeviceaddrwithroom;

//icon web: https://icons8.com/icon/pack/plants/color

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.view.View;

import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

public class MainActivity extends AppCompatActivity{
    private ImageButton mAddToiletBT, mAddOfficeBT, mAddBabysBT, mAddBedroomBT;
    private ImageButton mNotifychannel,mNotifySetting;
    private ImageButton mSearchRadar;
    private ImageButton mThersSetting;

    private TextView mShowIDInfo;
    private ListView mRoomListView;
    private ListView mRoomListViewTest;

    //alex
    private final static String TAG = MainActivity.class.getSimpleName();
    private NotificationHelper mNotificationHelper;

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String REGISTER_ROOM = "REGISTER_THIS_ROOM";
    private String mRegisteredRoom;
    private String mRegisterAddr;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final GlobalVariables gv = (GlobalVariables)getApplicationContext();

        Log.d(TAG, "created main activity");


        mNotificationHelper = new NotificationHelper(this);
        mNotifychannel = (ImageButton)findViewById(R.id.notifychannel);
        mNotifySetting = (ImageButton)findViewById(R.id.notifysetting);

        mThersSetting = (ImageButton)findViewById(R.id.setThers_bt);
        mThersSetting.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ThersholdActivity.class);
                startActivity(intent);
            }
        });
        Log.d(TAG, "gv thers "+gv.getSpeedThershold()+" "+gv.getDeretctionThershold());

        mRoomListView =(ListView)findViewById(R.id.RoomListView);
        //https://android--code.blogspot.com/2015/08/android-listview-add-items.html


        final List<String> roomList = gv.getNameList();
        final ArrayAdapter<String> arrayAdapterTest = new ArrayAdapter<String>
                (MainActivity.this, android.R.layout.simple_list_item_1,roomList);
        mRoomListView.setAdapter(arrayAdapterTest);
        mRoomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if("null".equals(gv.getRoomAddr(roomList.get(position))))
                {
                    mShowIDInfo.setText("add addr to "+gv.getRoomName(roomList.get(position)));

                    final Intent intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                    intent.putExtra(REGISTER_ROOM,String.valueOf(position));

                    startActivity(intent);
                }
                else
                {
                    final Intent intent = new Intent(MainActivity.this, RadarMultiDetectActivity.class);
                    intent.putExtra(EXTRAS_DEVICE_NAME, gv.getRoomName(roomList.get(position)));
                    intent.putExtra(EXTRAS_DEVICE_ADDRESS, gv.getRoomAddr(roomList.get(position)));
                    startActivity(intent);
                }
            }
        });
        mRoomListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String test_name = roomList.get(position);

                gv.deleteRoom(roomList.get(position));
                roomList.remove(position);
                arrayAdapterTest.notifyDataSetChanged();

                Log.d(TAG, "check room "+gv.getRoomName(test_name));
                return false;
            }
        });


        final Intent intent = getIntent();
        try {
            mRegisteredRoom = intent.getStringExtra(DeviceScanActivity.REGISTER_ROOM);
            mRegisterAddr = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
            Log.d(TAG, "mRegistered room = "+ mRegisteredRoom+" at "+mRegisterAddr);

            /*
            if("null".equals(mRegisterAddr))
            {}else
            {
                String targetRoom = roomList.get(Integer.parseInt(mRegisteredRoom));
                gv.setRoomAddr(targetRoom, mRegisterAddr);
                //Log.d(TAG,targetRoom+" gv getRoomAddr= "+
                        //gv.getRoomAddr(roomList.get(Integer.parseInt(mRegisteredRoom))));
            }
            * */


            if("null".equals(gv.getRoomAddr(roomList.get(Integer.parseInt(mRegisteredRoom)))))
            {
                if(!"null".equals(mRegisterAddr))
                {
                    String targetRoom = roomList.get(Integer.parseInt(mRegisteredRoom));
                    gv.setRoomAddr(targetRoom, mRegisterAddr);
                }
            }

        } catch (NumberFormatException e) { }


        mAddToiletBT = (ImageButton)findViewById(R.id.toilet_image);
        mAddToiletBT.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                if(gv.addRoom("Toilet"))
                {
                    roomList.add("Toilet");
                    arrayAdapterTest.notifyDataSetChanged();
                }

            }
        });
        mAddBabysBT =(ImageButton)findViewById(R.id.babys_image);
        mAddBabysBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(gv.addRoom("babyCare"))
            {
                roomList.add("babyCare");
                arrayAdapterTest.notifyDataSetChanged();
            }
            }
        });
        mShowIDInfo = (TextView)findViewById(R.id.show_id);
        mAddOfficeBT = (ImageButton)findViewById(R.id.office_image);
        mAddOfficeBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(gv.addRoom("office"))
            {
                roomList.add("office");
                arrayAdapterTest.notifyDataSetChanged();
            }
            }
        });
        mAddBedroomBT =(ImageButton)findViewById(R.id.bedroom_image);
        mAddBedroomBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(gv.addRoom("Bedroom"))
            {
                roomList.add("Bedroom");
                arrayAdapterTest.notifyDataSetChanged();
            }
            }
        });

        mSearchRadar =(ImageButton)findViewById(R.id.searchRadar);
        mSearchRadar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowIDInfo.setText("go to SearchActivity");
            }
        });
    }

    public void openChannelSetting(View view)
    {
        mNotificationHelper.openChannelSetting(NotificationHelper.CHANNEL_ID);
    }

    public void openNotificationSetting(View view)
    {
        mNotificationHelper.openNotificationSetting();
    }


}
