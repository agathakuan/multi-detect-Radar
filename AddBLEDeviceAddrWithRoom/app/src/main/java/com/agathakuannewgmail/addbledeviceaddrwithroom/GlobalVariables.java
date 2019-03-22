package com.agathakuannewgmail.addbledeviceaddrwithroom;

//http://learnexp.tw/%E3%80%90android%E3%80%91global-variable-%E5%85%B1%E7%94%A8%E8%AE%8A%E6%95%B8-%E5%BE%9E-3%E5%88%B04/
import android.app.Application;
import android.util.Log;

import java.util.List;
import java.util.ArrayList;


public class GlobalVariables extends Application{
    private List<roomAttributes> mRoomListAttributes = new ArrayList<>();
    private Double mSpeedThershold = 100.0;
    private String mDeretctionThershold = "null";
    private Double mFreqThershold = 100.0;

    private final static String TAG = "GV.RoomListAttributes";

    public List<String> getNameList()
    {
        List<String> nameList = new ArrayList<String>();
        if(mRoomListAttributes.size() == 0)
            return nameList;

        for (int i = 0; i<mRoomListAttributes.size();i++)
        {
            nameList.add(mRoomListAttributes.get(i).getRoomName());
        }

        return nameList;
    }

    public boolean addRoom(String roomName)
    {
        for (int i =0;i <mRoomListAttributes.size();i++)
        {
            if (roomName.equals(mRoomListAttributes.get(i).getRoomName()))
            {
                Log.d(TAG,"already have this room");
                return false;
            }
        }

        roomAttributes newRoom = new roomAttributes();
        newRoom.setRoomName(roomName);

        mRoomListAttributes.add(newRoom);

        return true;
    }

    public boolean deleteRoom(String roomName)
    {

        int roomIndex = 0xff;
        for (int i =0;i <mRoomListAttributes.size();i++)
        {
            if(roomName.equals(mRoomListAttributes.get(i).getRoomName()))
            {
                roomIndex = i;
                break;
            }
        }

        if(roomIndex > mRoomListAttributes.size())
        {
            return false;
        }
        mRoomListAttributes.remove(roomIndex);
        return true;
    }

    public String getRoomName(String roomName)
    {
        int roomIndex = 0xff;
        for (int i =0;i <mRoomListAttributes.size();i++)
        {
            if(roomName.equals(mRoomListAttributes.get(i).getRoomName()))
            {
                roomIndex = i;
                break;
            }
        }
        if(0xff== roomIndex)
            return "no this room";

        return mRoomListAttributes.get(roomIndex).getRoomName();
    }

    public boolean setRoomAddr(String roomName, String addr)
    {
        int roomIndex = 0xff;
        for (int i =0;i <mRoomListAttributes.size();i++)
        {
            if(roomName.equals(mRoomListAttributes.get(i).getRoomName()))
            {
                roomIndex = i;
                break;
            }
        }
        if(roomIndex > mRoomListAttributes.size())
        {
            return false;
        }

        mRoomListAttributes.get(roomIndex).setDeviceAddr(addr);
        return true;
    }



    public String getRoomAddr(String roomName)
    {
        int roomIndex = 0xff;
        for (int i =0;i <mRoomListAttributes.size();i++)
        {
            if(roomName.equals(mRoomListAttributes.get(i).getRoomName()))
            {
                roomIndex = i;
                break;
            }
        }

        if (0xff == roomIndex)
            return "no this room";

        return mRoomListAttributes.get(roomIndex).getDeviceAddr();
    }

    public void setSpeedThershold(Double speed)
    {
        mSpeedThershold = speed;
    }

    public void setFreqThershold(Double freq){mFreqThershold = freq;}

    public boolean setDirectionThershold(String s)
    {
        switch(s)
        {
            case "apart":
                mDeretctionThershold = s;
                return true;
            case "depart":
                mDeretctionThershold = s;
                return true;
            case "all":
                mDeretctionThershold = s;
                return  true;
        }
        return false;
    }

    public String getDeretctionThershold()
    {
        return mDeretctionThershold;
    }

    public Double getSpeedThershold()
    {
        return mSpeedThershold;
    }

    public Double getFreqThershold() {return mFreqThershold;}
}
