package com.example.tainc.sateco_1;

import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by tainc on 10/14/16.
 */

public class UdpManager {
    public DatagramPacket UdpServerPacket;
    public DatagramSocket UdpServer;

    public String MyIpAddress;

    private DatagramSocket UdpClient;
    private int UdpClientPort = 16102;
    private String UdpClientAdr;
    private DatagramPacket UdpClientPacket;
    private InetAddress UdpClientIp;

    private int UdpServerPort = 16101;
    private String UdpServerAdr = "0.0.0.0";
    private InetAddress UdpServerIp;

    private byte[] receiveData = new byte[1024];
    private byte[] send_data = new byte[1024];
//    private udpReadThread mtcpReadThread;
    private String str="Hello ! ";

    public UdpManager(){
        Log.d("hello", "!");
    }

    public void startUdpServer() throws SocketException, UnknownHostException {
        getIpAddr();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        UdpServer = new DatagramSocket(UdpServerPort);
        UdpServerIp = InetAddress.getByName(UdpServerAdr);

        UdpServerPacket = new DatagramPacket(receiveData, receiveData.length, UdpServerIp, UdpServerPort);
    }

    public void closeUdpServer(){
        UdpServer.close();
    }

    public void startUdpClient(String data)  {
        getIpAddr();
        if ( data == null && data.isEmpty() )
            data = str;
        try {
            UdpClient = new DatagramSocket(UdpClientPort);
            UdpClient.setSoTimeout(10);
            UdpClientIp =  InetAddress.getByName(UdpClientAdr);
            Log.d("data", data);
            send_data = data.getBytes();
            UdpClient.connect(UdpClientIp, UdpClientPort);
            UdpClient.setSoTimeout(10);
            UdpClientPacket = new DatagramPacket(send_data, data.length(), UdpClientIp, UdpClientPort);
            UdpClient.send(UdpClientPacket);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void closeUdpClient(){
        UdpClient.close();
    }

    private void getIpAddr(){
        MyIpAddress = getIpAddress();
        Log.d("my ip ", MyIpAddress);
        String[] ipAr = MyIpAddress.split("\\.");
        UdpClientAdr = (ipAr[0] + "." + ipAr[1] + "." + ipAr[2] + "." + "255").trim();
        Log.d("Udp Client Addr", UdpClientAdr);
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip = inetAddress.getHostAddress();
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("Something Wrong! ",e.toString() + "\n");
        }

        return ip;
    }

}
