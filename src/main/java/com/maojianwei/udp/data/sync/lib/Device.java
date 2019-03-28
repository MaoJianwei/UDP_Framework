package com.maojianwei.udp.data.sync.lib;

import io.netty.util.internal.SocketUtils;

import java.net.InetSocketAddress;

import static com.maojianwei.udp.data.sync.communicate.lib.UdpConst.DEFAULT_UDP_PORT;

public class Device {
    int deviceId;
    InetSocketAddress ipv4Addr;

    public Device(int deviceId, InetSocketAddress ipv4Addr) {
        this(deviceId, ipv4Addr.getAddress().getHostAddress(), ipv4Addr.getPort());
        String test = ipv4Addr.getAddress().getHostAddress();
    }

    public Device(int deviceId, String ipv4Addr) {
        this(deviceId, ipv4Addr, DEFAULT_UDP_PORT);
    }

    public Device(int deviceId, String ipv4Addr, int port) {
        this.deviceId = deviceId;
        this.ipv4Addr = SocketUtils.socketAddress(ipv4Addr, port);
    }


    public int getDeviceId() {
        return deviceId;
    }
    public InetSocketAddress getAddr() {
        return ipv4Addr;
    }

    public void updateAddr(InetSocketAddress ipv4Addr) {
        this.ipv4Addr = ipv4Addr;
    }
}
