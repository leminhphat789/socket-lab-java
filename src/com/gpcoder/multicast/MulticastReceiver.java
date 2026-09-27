package com.gpcoder.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class MulticastReceiver {

    public static final byte[] BUFFER = new byte[4096];

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");

        MulticastSocket socket = null;
        try {
            InetAddress group = InetAddress.getByName(MulticastSender.GROUP_ADDRESS);
            socket = new MulticastSocket(MulticastSender.PORT);

            NetworkInterface netIf = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            if (netIf == null) {
                netIf = NetworkInterface.getNetworkInterfaces().nextElement();
            }

            socket.joinGroup(new InetSocketAddress(group, MulticastSender.PORT), netIf);

            while (true) {
                DatagramPacket inPacket = new DatagramPacket(BUFFER, BUFFER.length);
                socket.receive(inPacket);

                String msg = new String(inPacket.getData(), 0, inPacket.getLength());
                System.out.println("From " + inPacket.getAddress() + " Msg : " + msg);
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