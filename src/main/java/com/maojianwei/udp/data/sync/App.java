package com.maojianwei.udp.data.sync;

import com.maojianwei.udp.data.sync.api.DeviceController;
import com.maojianwei.udp.data.sync.communicate.api.UdpController;
import com.maojianwei.udp.data.sync.communicate.UdpServer;
import com.maojianwei.udp.data.sync.communicate.lib.UdpData;

import java.util.Scanner;
import java.util.concurrent.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        App app = new App();

        app.activate();

        Scanner in =new Scanner(System.in);
        while(true) {
            System.out.println(app.getAllDevicesInfo());

            int deviceId = Integer.valueOf(in.nextLine());
            if (deviceId == 0) {
                break;
            }
            String msgToSend = in.nextLine();
            app.sendMsg(deviceId, msgToSend);
        }

        app.deactivate();
    }

    BlockingQueue<UdpData> queue;
    DeviceController controller;
    ExecutorService pool;

    public void activate() {
        queue = new LinkedBlockingQueue<>();

        controller = new DeviceControllerImpl(queue);
        if (!controller.initLocalDevice()) {
            System.out.println("App - Fail to init DeviceController!");
            return;
        }

        pool = Executors.newCachedThreadPool();
        pool.submit(new ReadMsgTask(queue));
        pool.submit(new UdpServer(controller));
    }

    public void deactivate() {
        pool.shutdownNow();
        try {
            pool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("App - deactivate - InterruptedException");
        }

        controller.releaseLocalDevice();
        queue.clear();
    }

    private void sendMsg(int deviceId, String msg) {
        if (!controller.sendMsg(deviceId, msg)) {
            System.out.println("sendMsg - Fail");
        } else {
            System.out.println("sendMsg - Success");
        }
    }

    private String getAllDevicesInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Input 0 to quit ===\n");
        controller.getRemoteDevices().forEach(
                d -> sb.append(String.format("Id=%d, IP=%s, Port=%d\n",
                        d.getDeviceId(), d.getAddr().getAddress().getHostAddress(), d.getAddr().getPort()))
        );
        return sb.toString();
    }

    private class ReadMsgTask implements Runnable {

        BlockingQueue<UdpData> queue;
        public ReadMsgTask(BlockingQueue<UdpData> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    UdpData data = queue.poll(100, TimeUnit.MILLISECONDS);
                    if (data != null) {
                        System.out.println(String.format("Device: %d - Id: %d - Msg: %s",
                                data.getDeviceId(), data.getMsgId(), data.getMsg()));
                    }
                } catch (InterruptedException e) {
                    System.out.println("ReadMsgTask - InterruptedException");
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ReadMsgTask - unknown Exception");
                }
            }
        }
    }
}
