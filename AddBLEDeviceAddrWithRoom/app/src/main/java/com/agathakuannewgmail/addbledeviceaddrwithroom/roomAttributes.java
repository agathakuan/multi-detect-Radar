package com.agathakuannewgmail.addbledeviceaddrwithroom;

public class roomAttributes {
    private String mRoomName="null";
    private String mDeviceAddr="null";

    public void setRoomName(String name)
    {
        this.mRoomName = name;
    }

    public void setDeviceAddr(String addr)
    {
        this.mDeviceAddr = addr;
    }

    public String getRoomName()
    {
        return this.mRoomName;
    }

    public String getDeviceAddr()
    {
        return this.mDeviceAddr;
    }
}
