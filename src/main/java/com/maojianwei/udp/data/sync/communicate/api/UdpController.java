package com.maojianwei.udp.data.sync.communicate.api;

import com.maojianwei.udp.data.sync.communicate.lib.UdpData;

import java.net.InetSocketAddress;

public interface UdpController {
    int getLocalDeviceId();
    InetSocketAddress getRemoteDeviceAddr(int deviceId);

    void reportDeviceOnline(int deviceId, InetSocketAddress ipv4Addr);
    void dataReceived(UdpData data);
}
