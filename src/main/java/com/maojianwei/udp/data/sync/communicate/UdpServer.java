package com.maojianwei.udp.data.sync.communicate;

import com.maojianwei.udp.data.sync.communicate.api.UdpController;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static com.maojianwei.udp.data.sync.communicate.lib.UdpConst.DEFAULT_UDP_PORT;
import static com.maojianwei.udp.data.sync.communicate.lib.UdpConst.UTF_8;

public class UdpServer implements Runnable {

    private UdpController controllerPass;

    public UdpServer(UdpController udpController) {
        controllerPass = udpController;
    }

    @Override
    public void run() {
        System.out.println(Charset.isSupported(UTF_8.name()));
        System.out.println(Charset.defaultCharset());
        if (!checkUtf8Support()) {
            System.out.println("Fail to start UdpServer, system doesn't support utf-8, or defaultCharset is not utf-8.");
            return;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        Channel listening = null;
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel datagramChannel) {
                            datagramChannel.pipeline()
                                    .addLast(new UdpDataCodec(controllerPass))
                                    .addLast(new UdpKaHandler(controllerPass));
                        }
                    });

            listening = b.bind(DEFAULT_UDP_PORT).sync().channel();
            listening.closeFuture().await();
        } catch (InterruptedException e) {
            if (listening != null) {
                try {
                    listening.close().await(1, TimeUnit.SECONDS);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        group.shutdownGracefully().awaitUninterruptibly(5, TimeUnit.SECONDS);
    }

    private boolean checkUtf8Support() {
        return Charset.isSupported(UTF_8.name()) && Charset.defaultCharset().equals(UTF_8);
    }
}
