package com.maojianwei.udp.data.sync.communicate.lib;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UdpPacket {
    UdpData data;
    InetSocketAddress addr;

    public static UdpPacket of(UdpData data, InetSocketAddress addr) {
        return new UdpPacket(data, addr);
    }

    private UdpPacket(UdpData data, InetSocketAddress addr) {
        this.data = data;
        this.addr = addr;
    }

    public UdpData getData() {
        return data;
    }

    public void setData(UdpData data) {
        this.data = data;
    }

    public InetSocketAddress getAddr() {
        return addr;
    }

    public void setAddr(InetSocketAddress addr) {
        this.addr = addr;
    }
}
