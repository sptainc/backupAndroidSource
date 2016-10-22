package com.example.tainc.sateco_1;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import static com.example.tainc.sateco_1.SatecoComponent.*;

/**
 * Created by tainc on 10/16/16.
 */

public class SocketManager extends Activity {
    private String IpServer;
    public boolean isConnected;
    private Handler updateConversationHandler;
    private Thread serverThread = null;

    SocketManager(String IpServer) {
        this.IpServer = IpServer;
        serverThread = new Thread(new ServerThread());
        serverThread.start();
    }

    public Socket connectAndReconnectSocket() {
        Socket socketClient = new Socket();
        while (!isConnected) {
            //                serverSocket = new ServerSocket(SERVERPORT);
            try {
                socketClient = new Socket();
                socketClient.connect(new InetSocketAddress(IpServer, SOCKET_CLIENT_PORT), 1000);
                isConnected = true;
            } catch (IOException e) {
                isConnected = false;
                e.printStackTrace();
            }
        }
        if (isConnected)
            return socketClient;
        return null;
    }

    public class ServerThread extends Thread implements Runnable {
        CommunicationThread commThread;
        public void run() {
            Socket socket = connectAndReconnectSocket();

            if (isConnected) {
//            while (!Thread.currentThread().isInterrupted()) {

//                try {
//                    socket = serverSocket.accept();
                if (commThread != null){
                    commThread.interrupt();
                    commThread = null;
                }

                commThread = new CommunicationThread(socket);
                commThread.start();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
            }
        }

    }

    class CommunicationThread extends Thread implements Runnable {

        private Socket clientSocket;

        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;

            try {
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            String readings;
            try {
                readings = input.readLine();
                while (readings != null && !readings.isEmpty()) {
                    updateConversationHandler.post(new updateUIThread(readings));
                    readings = input.readLine();
                }
                if (readings == null) {
                    isConnected = false;
                    if (serverThread != null) {
                        serverThread.interrupt();
                        serverThread = null;
                    }
                    serverThread = new Thread(new ServerThread());
                    serverThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    class updateUIThread extends Thread implements Runnable {
        private String msg;

        public updateUIThread(String str) {
            this.msg = str;
        }

        @Override
        public void run() {
            if (msg != null) {
                Log.d("data", msg);
            }
        }

    }
}
