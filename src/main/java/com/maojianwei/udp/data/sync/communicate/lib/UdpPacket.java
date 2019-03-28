package com.maojianwei.udp.data.sync.communicate.lib;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UdpPacket<D> {
    D data;
    InetSocketAddress addr;


    public UdpPacket(D data, InetSocketAddress addr) {
        this.data = data;
        this.addr = addr;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }

    public InetSocketAddress getAddr() {
        return addr;
    }

    public void setAddr(InetSocketAddress addr) {
        this.addr = addr;
    }
}
