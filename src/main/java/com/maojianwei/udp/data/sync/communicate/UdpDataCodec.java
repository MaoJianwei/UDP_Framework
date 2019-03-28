package com.maojianwei.udp.data.sync.communicate;

import com.maojianwei.udp.data.sync.api.DeviceController;
import com.maojianwei.udp.data.sync.communicate.api.UdpController;
import com.maojianwei.udp.data.sync.communicate.lib.UdpData;
import com.maojianwei.udp.data.sync.communicate.lib.UdpPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;

import static com.maojianwei.udp.data.sync.communicate.lib.UdpConst.UTF_8;

public class UdpDataCodec extends MessageToMessageCodec<DatagramPacket, UdpPacket<UdpData>> {

    private UdpController controller;
    public UdpDataCodec(UdpController udpController) {
        controller = udpController;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, UdpPacket<UdpData> pkt, List<Object> out) throws Exception {

        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(controller.getLocalDeviceId());
        buf.writeInt(pkt.getData().getMsgId());
        buf.writeBytes(pkt.getData().getMsg().getBytes(UTF_8));

        out.add(new DatagramPacket(buf, pkt.getAddr()));
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, DatagramPacket dgPkt, List<Object> list) throws Exception {

        ByteBuf byteBuf = dgPkt.content();

        UdpData data = new UdpData();
        data.setDeviceId(byteBuf.readInt());
        data.setMsgId(byteBuf.readInt());
        data.setMsg(new String(ByteBufUtil.getBytes(byteBuf)));


        InetSocketAddress sender = dgPkt.sender();
        InetSocketAddress recipient = dgPkt.recipient();

        UdpPacket<UdpData> pkt = new UdpPacket<>(data, dgPkt.sender());

        list.add(pkt);
    }
}
