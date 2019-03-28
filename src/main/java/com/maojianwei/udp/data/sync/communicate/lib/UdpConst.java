package com.maojianwei.udp.data.sync.communicate.lib;

import java.nio.charset.Charset;

public final class UdpConst {

    public static final Charset UTF_8 = Charset.forName("utf-8");

    public static final int BROADCAST_DEVICE_ID = 255;
    public static final String BROADCAST_IPV4_ADDR = "255.255.255.255";
    public static final int DEFAULT_UDP_PORT = 9988;


    public static final String HELLO = "Hello, BigMao~";
}
