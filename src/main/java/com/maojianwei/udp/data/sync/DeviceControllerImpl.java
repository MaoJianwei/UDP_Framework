package com.maojianwei.udp.data.sync;

import com.maojianwei.udp.data.sync.api.DeviceController;
import com.maojianwei.udp.data.sync.communicate.UdpServer;
import com.maojianwei.udp.data.sync.communicate.api.UdpController;
import com.maojianwei.udp.data.sync.communicate.UdpDataCodec;
import com.maojianwei.udp.data.sync.communicate.UdpKaHandler;
import com.maojianwei.udp.data.sync.communicate.lib.UdpData;
import com.maojianwei.udp.data.sync.communicate.lib.UdpPacket;
import com.maojianwei.udp.data.sync.lib.Device;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SocketUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.maojianwei.udp.data.sync.communicate.lib.UdpConst.*;

public class DeviceControllerImpl implements DeviceController {

    private BlockingQueue<UdpData> comQueue;
    private Map<Integer, Device> remoteDevices;

    private LocalDevice localDevice;

    private UdpServer udpServer;

    public DeviceControllerImpl(BlockingQueue queue) {
        comQueue = queue;
        remoteDevices = new HashMap<>();
        remoteDevices.put(BROADCAST_DEVICE_ID, new Device(BROADCAST_DEVICE_ID, BROADCAST_IPV4_ADDR, DEFAULT_UDP_PORT));
    }


    @Override
    public boolean initLocalDevice() {

        if (localDevice == null) {

            localDevice = new LocalDevice((int) System.currentTimeMillis() & 0x000000ff);
            udpServer = new UdpServer(this);

            if (udpServer.start()) {
                return true;
            } else {
                localDevice = null;
                udpServer = null;
            }
        }
        return false;
    }

    @Override
    public void releaseLocalDevice() {
        if (localDevice != null) {
            udpServer.stop();
            udpServer = null;
            localDevice = null;
        }
    }


    @Override
    public Set<Device> getRemoteDevices() {
        return new HashSet<>(remoteDevices.values());
    }

    @Override
    public InetSocketAddress getRemoteDeviceAddr(int deviceId) {
        Device remote = remoteDevices.getOrDefault(deviceId, null);
        return remote != null ? remote.getAddr() : null;
    }

    @Override
    public int getLocalDeviceId() {
        return localDevice.getDeviceId();
    }

    @Override
    public boolean sendMsg(int deviceId, String msg) {

        if (localDevice == null) {
            return false;
        }

        Device remote = remoteDevices.getOrDefault(deviceId, null);
        if (remote == null) {
            return false;
        }

        UdpData udpMsg = new UdpData(deviceId, localDevice.getNextMsgId(), msg);
        InetSocketAddress remoteAddr = remote.getAddr();

        try {
            udpServer.getLocalChannel().writeAndFlush(UdpPacket.of(udpMsg, remoteAddr)).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    private class LocalDevice {
        private final int localDeviceId;
        private final AtomicInteger msgId;

        private LocalDevice(int deviceId) {
            this.localDeviceId = deviceId;
            this.msgId = new AtomicInteger(0);
        }
        private int getDeviceId() {
            return localDeviceId;
        }
        private int getNextMsgId() {
            return msgId.getAndIncrement();
        }
    }







    @Override
    public void reportDeviceOnline(int deviceId, InetSocketAddress ipv4Addr) {
        Device device = remoteDevices.getOrDefault(deviceId, null);
        if (device != null) {
            device.updateAddr(ipv4Addr);
            System.out.println(String.format("id=%d ,update addr=%s", deviceId, ipv4Addr.toString()));
        } else {
            remoteDevices.put(deviceId, new Device(deviceId, ipv4Addr));
            System.out.println(String.format("id=%d , addr=%s, up!", deviceId, ipv4Addr.toString()));
        }
    }

    @Override
    public void dataReceived(UdpData data) {




        int count = 0;
        while (count < 3) {
            try {
                if (comQueue.offer(data, 1, TimeUnit.SECONDS)) {
                    return;
                }
            } catch (InterruptedException e) {
                System.out.println("DeviceControllerImpl - InterruptedException");
                break;
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            count++;
        }
        System.out.println("DeviceControllerImpl - Fail to offer");
    }
}
