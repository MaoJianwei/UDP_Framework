package com.maojianwei.udp.data.sync.api;

import com.maojianwei.udp.data.sync.communicate.api.UdpController;
import com.maojianwei.udp.data.sync.lib.Device;

import java.net.SocketAddress;
import java.util.Set;

public interface DeviceController extends UdpController {
    boolean initLocalDevice();
    void releaseLocalDevice();

    Set<Device> getRemoteDevices();

    boolean sendMsg(int deviceId, String msg);
}
