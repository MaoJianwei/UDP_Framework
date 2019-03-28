package com.maojianwei.udp.data.sync.communicate.lib;

public class UdpData {

    private static final String EMPTY = "";

    int deviceId;
    int msgId;
    String msg;

    public UdpData() {
        this.deviceId = 0;
        this.msgId = 0;
        this.msg = EMPTY;
    }

    public UdpData(int deviceId, int msgId, String msg) {
        this.deviceId = deviceId;
        this.msgId = msgId;
        this.msg = msg;
    }


    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
