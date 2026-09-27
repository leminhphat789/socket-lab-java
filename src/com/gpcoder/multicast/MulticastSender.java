package com.gpcoder.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class MulticastSender {

    public static final String GROUP_ADDRESS = "224.0.0.1";
    public static final int PORT = 8888;

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("java.net.preferIPv4Stack", "true");

        MulticastSocket socket = null;
        try {
            InetAddress address = InetAddress.getByName(GROUP_ADDRESS);
            socket = new MulticastSocket();

            NetworkInterface netIf = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            if (netIf != null) {
                socket.setNetworkInterface(netIf);
            }

            long counter = 0;
            while (true) {
                String msg = "Sent message No. " + counter;
                counter++;
                byte[] data = msg.getBytes();

                DatagramPacket outPacket = new DatagramPacket(data, data.length, address, PORT);
                socket.send(outPacket);

                System.out.println("Server sent packet with msg: " + msg);
                Thread.sleep(1000);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}