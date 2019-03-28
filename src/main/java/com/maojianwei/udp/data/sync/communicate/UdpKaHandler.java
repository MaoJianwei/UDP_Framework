package com.maojianwei.udp.data.sync.communicate;

import com.maojianwei.udp.data.sync.communicate.api.UdpController;
import com.maojianwei.udp.data.sync.communicate.lib.UdpData;
import com.maojianwei.udp.data.sync.communicate.lib.UdpPacket;
import io.netty.channel.*;

import static com.maojianwei.udp.data.sync.communicate.lib.UdpConst.HELLO;

public class UdpKaHandler extends ChannelDuplexHandler {

    private UdpController controller;
    public UdpKaHandler(UdpController udpController) {
        controller = udpController;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof UdpPacket) {
            UdpPacket pkt = (UdpPacket) msg;
            if (pkt.getData().getMsg().equals(HELLO)) {
                controller.reportDeviceOnline(pkt.getData().getDeviceId(), pkt.getAddr());
                return;
            }

            controller.dataReceived(pkt.getData());
        }
    }

//    @Override
//    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        if (msg instanceof UdpData) {
//            controller.
//            controller.dataReceived((UdpData)msg);
//            ctx.write(msg, promise);
//        }
//    }
}
