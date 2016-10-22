package com.example.tainc.sateco_1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tainc.sateco_1.noimportant.DemoBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static com.example.tainc.sateco_1.SatecoComponent.ALIGNMENT_H_KEY;
import static com.example.tainc.sateco_1.SatecoComponent.ALIGNMENT_V_KEY;
import static com.example.tainc.sateco_1.SatecoComponent.APP_FILE_DATA;
import static com.example.tainc.sateco_1.SatecoComponent.DISTANCE_H_KEY;
import static com.example.tainc.sateco_1.SatecoComponent.DISTANCE_V_KEY;
import static com.example.tainc.sateco_1.SatecoComponent.IP_SERVER_TCPCLIENT;
import static com.example.tainc.sateco_1.SatecoComponent.PRESSURE_KEY;
import static com.example.tainc.sateco_1.SatecoComponent.SOCKET_CLIENT_PORT;
import static com.example.tainc.sateco_1.SatecoComponent.SPLIT_STRING;
import static com.example.tainc.sateco_1.SatecoComponent.UDP_CLIENT_PORT;
import static com.example.tainc.sateco_1.SatecoComponent.UDP_SERVER_PORT;
import static com.example.tainc.sateco_1.SatecoComponent.WIFI_PASSWORD;
import static com.example.tainc.sateco_1.SatecoComponent.WIFI_SSID;

/**
 * Created by project on 10/10/16.
 */

public class LineChartTime extends DemoBase {

    //    Start UDP and TCP
    private String MyIpAddress;
    private String IpServer;

    private Thread serverThread = null;
    private Handler updateConversationHandler;

    private DatagramSocket UdpClient;
    private String UdpClientAdr;
    private DatagramPacket UdpClientPacket;
    private InetAddress UdpClientIp;

    private DatagramSocket UdpServer;
    private String UdpServerAdr = "0.0.0.0";
    private InetAddress UdpServerIp;
    private DatagramPacket UdpServerPacket;

    private byte[] receiveData = new byte[1024];
    private byte[] send_data = new byte[1024];
    private udpReadThread mtcpReadThread;
    private String str = "Hello ! ";
//    public boolean connectOk;

    private int second = 0;
//    End UDP and TCP

    //    Start Line Chart
    private LineChart mChart;
    ArrayList<Entry> valuesLineChart;
//    End Line Chart

    private TextView tvDistHValue;
    private TextView tvDistVValue;
    private TextView tvAligHValue;
    private TextView tvAligVValue;
    private TextView tvPressVal;
    private TextView tvPressMeasure;
    private TextView tvAHMeasure;
    private TextView tvAVMeasure;
    private TextView tvDHMeasure;
    private TextView tvDVMeasure;

    private Button btnAligHCheck;
    private Button btnAligVCheck;

    Double msgData = null;
    boolean isConnected = false;

    private ProgressDialog progressDialog;
    //    private Handler handlerProgress;
    private Thread progressThread;
    private int counterTimeAppStart = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_linechart_time);

//        handlerProgress = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                progressDoalog.incrementProgressBy(10);
//            }
//        };

        progressDialog = new ProgressDialog(this);
//        progressDoalog.setMax(500);
//        progressDoalog.setMessage("Its loading....");
//        progressDoalog.setTitle("ProgressDialog bar example");
//        progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDoalog.show();
        progressDialog = progressDialog.show(this, "Attendez, s' il vous plaît!", "De liaison", true);

        progressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (counterTimeAppStart <= 4) {
                        Thread.sleep(1000);
                        counterTimeAppStart++;
//                        handlerProgress.sendMessage(handlerProgress.obtainMessage());
                        if (counterTimeAppStart == 4) {
                            progressDialog.dismiss();
//                            handlerProgress.removeCallbacksAndMessages(null);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    loadScreen();
                                }

                            });
                            Log.d("Continue", "not dismiss");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        progressThread.start();

    }

    private void loadScreen() {

        loadTextViewValue();

        loadButton();

        loadChart();

        loadTCP();

        valuesLineChart = new ArrayList<Entry>();
    }

    private void loadButton() {
        btnAligHCheck = (Button) findViewById(R.id.btnAligHCheck);
        btnAligVCheck = (Button) findViewById(R.id.btnAligVCheck);
    }

    private void loadTextViewValue() {
        tvDistHValue = (TextView) findViewById(R.id.tvDistHVal);
        tvDistVValue = (TextView) findViewById(R.id.tvDistVVal);

        tvAligHValue = (TextView) findViewById(R.id.tvAligHVal);
        tvAligVValue = (TextView) findViewById(R.id.tvAligVVAl);

        tvPressVal = (TextView) findViewById(R.id.tvPressVal);

        tvPressMeasure = (TextView) findViewById(R.id.tvPressMeasure);
        tvAHMeasure = (TextView) findViewById(R.id.tvAHMeasure);
        tvAVMeasure = (TextView) findViewById(R.id.tvAVMeasure);
        tvDHMeasure = (TextView) findViewById(R.id.tvDHMeasure);
        tvDVMeasure = (TextView) findViewById(R.id.tvDVMeasure);

        loadFont();
    }

    private void loadFont() {
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "LED.Font.ttf");
        tvDistHValue.setTypeface(custom_font);
        tvDistVValue.setTypeface(custom_font);

        tvAligHValue.setTypeface(custom_font);
        tvAligVValue.setTypeface(custom_font);
        tvPressVal.setTypeface(custom_font);

        tvDHMeasure.setTypeface(custom_font);
        tvDVMeasure.setTypeface(custom_font);
        tvPressMeasure.setTypeface(custom_font);
    }

    private void loadChart() {
        mChart = (LineChart) findViewById(R.id.chart1);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.BLACK);
        mChart.setViewPortOffsets(0f, 0f, 0f, 0f);

        Legend l = mChart.getLegend();
        l.setEnabled(false);

        loadChartX();
        loadChartY();
    }

    private void loadChartX() {
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return formatDataChart(value);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
    }

    private void loadChartY() {
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
//        leftAxis.setAxisMaximum(32000f);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setDrawValues(!set.isDrawValuesEnabled());
                }

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if (mChart.getData() != null) {
                    mChart.getData().setHighlightEnabled(!mChart.getData().isHighlightEnabled());
                    mChart.invalidate();
                }
                break;
            }
            case R.id.actionToggleFilled: {

                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawFilledEnabled())
                        set.setDrawFilled(false);
                    else
                        set.setDrawFilled(true);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleCircles: {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawCirclesEnabled())
                        set.setDrawCircles(false);
                    else
                        set.setDrawCircles(true);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleCubic: {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.getMode() == LineDataSet.Mode.CUBIC_BEZIER)
                        set.setMode(LineDataSet.Mode.LINEAR);
                    else
                        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleStepped: {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.getMode() == LineDataSet.Mode.STEPPED)
                        set.setMode(LineDataSet.Mode.LINEAR);
                    else
                        set.setMode(LineDataSet.Mode.STEPPED);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionTogglePinch: {
                if (mChart.isPinchZoomEnabled())
                    mChart.setPinchZoom(false);
                else
                    mChart.setPinchZoom(true);

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleAutoScaleMinMax: {
                mChart.setAutoScaleMinMaxEnabled(!mChart.isAutoScaleMinMaxEnabled());
                mChart.notifyDataSetChanged();
                break;
            }
            case R.id.animateX: {
                mChart.animateX(3000);
                break;
            }
            case R.id.animateY: {
                mChart.animateY(3000);
                break;
            }
            case R.id.animateXY: {
                mChart.animateXY(3000, 3000);
                break;
            }

            case R.id.actionSave: {
                if (mChart.saveToPath("title" + System.currentTimeMillis(), "")) {
                    Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                            .show();

                // mChart.saveToGallery("title"+System.currentTimeMillis())
                break;
            }
        }
        return true;
    }

    private String formatDataChart(float data) {
        int valueInt = (int) data;

        String valueStr = String.format("%02d", valueInt);
        //00:00:00
        String result = "00:00:" + valueStr;
        if (data > 60) {
            if (data > 6000) {
                result = valueStr;
            } else {
                result = "00:" + valueStr;
            }
        }
        return result;
    }

    private void setData(float range) {
        float y = range;//getRandom(range, 50);
        valuesLineChart.add(new Entry(second, y)); // add one entry per hour
        Log.d("valuesLineChart : ", valuesLineChart.toString());

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(valuesLineChart, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
        mChart.invalidate();

        saveFile(valuesLineChart.toString());
        readData();
    }

    private void saveFile(String data) {
        try {
            FileOutputStream fOut = openFileOutput(APP_FILE_DATA, Context.MODE_PRIVATE);
            fOut.write(data.getBytes());
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read text from file
    private void readData() {
        //reading text from file
        try {
            FileInputStream fileIn = openFileInput(APP_FILE_DATA);
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[100];
            String s = "";
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
            }
            InputRead.close();
            Log.d("data ", s);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTCP(){
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        boolean isConnectWifi = loadIpAddress();

        if (!isConnectWifi) {
            Toast.makeText(getBaseContext(), "error", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder erCon = new AlertDialog.Builder(this);
            erCon.setTitle("Erreur de connexion");
            erCon.setMessage(" Erreur de connexion avec l'équipement d'acquisition de données \n " +
                    "Connectez wifi: \"SATECODAQ\" (mot de passe: \"dfm1610!\") !");
            erCon.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                connect2SSIDWifi();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            erCon.setCancelable(true);
            erCon.create().show();
        }

        startTCP();
    }

    //    Start Udp and Tcp
    private void loadUDPAndTCP() {
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        boolean isConnectWifi = loadIpAddress();
        if (!isConnectWifi) {
            Toast.makeText(getBaseContext(), "error", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder erCon = new AlertDialog.Builder(this);
            erCon.setTitle("Erreur de connexion");
            erCon.setMessage(" Erreur de connexion avec l'équipement d'acquisition de données \n " +
                    "Connectez wifi: \"SATECODAQ\" (mot de passe: \"dfm1610!\") !");
            erCon.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                connect2SSIDWifi();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            erCon.setCancelable(true);
            erCon.create().show();
        }

        startUdpAndTcp();
    }

    private void connect2SSIDWifi() throws Exception {


        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifi.getConnectionInfo();


//        try{
//            if(wifiInfo.getSSID().equals(publicClass.ssid)) return;
//        }catch(Exception ex){}
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {}


        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = WIFI_SSID;
        wc.preSharedKey = WIFI_PASSWORD;
        wc.hiddenSSID = true;
        wc.status = WifiConfiguration.Status.ENABLED;

        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

        int netid = wifi.addNetwork(wc);
        wifi.enableNetwork(netid, true);
        wifi.reconnect();
    }

    private boolean loadIpAddress() {
        boolean isConnectWifi = false;
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
            Log.d("Something Wrong! ", e.toString() + "\n");
        }

        MyIpAddress = ip;
        String[] ipAr = MyIpAddress.split("\\.");
        Log.d("my ip", MyIpAddress);

        if (ipAr.length > 2) {
//            UdpClientAdr = (ipAr[0] + "." + ipAr[1] + "." + ipAr[2] + "." + "255").trim();
//            UdpClientAdr = "255.255.255.255";
//            Log.d("Udp Client Address : ", UdpClientAdr);
            isConnectWifi = true;
        }

        return isConnectWifi;
    }

    private void startUdpAndTcp() {
        try {
            listenDataUdpServer();

            Thread.sleep(1000);

            Log.d("start listen ", "ok");
            // listen udp data
            this.mtcpReadThread = new udpReadThread();
            this.mtcpReadThread.start();

            sendDataUdpClient(false);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startTCP(){
        //Get and set ip server
        //Open TCP Server
        serverThread = new Thread(new ServerThread());
        serverThread.start();
    }

    private void initUdpClient() {
        try {
            UdpClient = new DatagramSocket(UDP_CLIENT_PORT);
            UdpClient.setSoTimeout(10);
            UdpClientIp = InetAddress.getByName(UdpClientAdr);

            send_data = str.getBytes();
            UdpClient.connect(UdpClientIp, UDP_CLIENT_PORT);
            UdpClient.setSoTimeout(10);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void sendDataUdpClient(boolean isConnected) {
        if (!isConnected)
            initUdpClient();
        Log.d("sending data", str);
        UdpClientPacket = new DatagramPacket(send_data, str.length(), UdpClientIp, UDP_CLIENT_PORT);
        try {
            UdpClient.send(UdpClientPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeUdpClient() {
        UdpClient.disconnect();
    }

    private void listenDataUdpServer() throws SocketException, UnknownHostException {
        UdpServer = new DatagramSocket(UDP_SERVER_PORT);
        UdpServerIp = InetAddress.getByName(UdpServerAdr);
        UdpServerPacket = new DatagramPacket(receiveData, receiveData.length, UdpServerIp, UDP_SERVER_PORT);
    }

    private class udpReadThread extends Thread {
        public udpReadThread() {
            updateConversationHandler = new Handler();
        }

        public void run() {
            super.run();
            int runIndex = 0;
            String udpMsg = "";

            Log.d("listen data", "listening");
            try {
//                UdpServer.setSoTimeout(1000);
                Log.d("start listen ", "listen ok");
                UdpServer.receive(UdpServerPacket);
                udpMsg = new String(receiveData, 0, UdpServerPacket.getLength());
                Log.d("time out ", "is Time out ");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("udp msg ", udpMsg);

            while (!udpMsg.isEmpty() && !Thread.currentThread().isInterrupted()) {

                if (!udpMsg.isEmpty() && runIndex == 0) {
                    closeUdpClient();
                    UdpServer.disconnect();
                    UdpServer.close();

                    //Get and set ip server
                    IpServer = UdpServerPacket.getAddress().toString();
                    IpServer = IpServer.substring(IpServer.lastIndexOf("/") + 1);

                    Log.d("socket address ", IpServer);

                    //Open TCP Server
                    serverThread = new Thread(new ServerThread());
                    serverThread.start();

                    runIndex++;
                }
            }
        }
    }

    @Nullable
    private Socket connectAndReconnectSocket() {

        Socket socketClient = new Socket();
        while (!isConnected) {
            //                serverSocket = new ServerSocket(SERVERPORT);
            try {
                socketClient = new Socket();
                socketClient.connect(new InetSocketAddress(IP_SERVER_TCPCLIENT, SOCKET_CLIENT_PORT), 1000);
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

    class ServerThread extends Thread implements Runnable {
        CommunicationThread commThread;

        private ServerThread(){
            updateConversationHandler = new Handler();
        }
        public void run() {
            Socket socket = connectAndReconnectSocket();
            if (isConnected) {
                PrintWriter output;
                try {
                    output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    output.println("aip="+MyIpAddress);
                    output.flush();
                    Log.d("send tcp ", "aip="+MyIpAddress);
                } catch (IOException e) {
                    e.printStackTrace();
                }


//            while (!Thread.currentThread().isInterrupted()) {

//                try {
//                    socket = serverSocket.accept();
                if (commThread != null) {
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


        public CommunicationThread(Socket clientSocket)  {

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
                loadData(msg);
            }
        }

    }

    //    Load data to form
    private void loadData(String msg) {
        Log.d("msg : ", msg);
        if (msg.contains(" ")) {
            String[] msgList = msg.split(SPLIT_STRING);

            String msgKey = msgList[msgList.length - 2];
            String msgValue = msgList[msgList.length - 1];
            Log.d("msg key ", msgKey);
            Log.d("msg value ", msgValue);

            switch (msgKey.toLowerCase()) {
                case DISTANCE_H_KEY:
                    try {
                        Integer msgDataInt = Integer.parseInt(msgValue);
                        this.tvDistHValue.setText(String.valueOf(msgDataInt));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    break;
                case DISTANCE_V_KEY:
                    try {
                        Integer msgDataInt = Integer.parseInt(msgValue);
                        tvDistVValue.setText(String.valueOf(msgDataInt));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    break;
                case ALIGNMENT_H_KEY:
                    try {
                        msgData = Double.parseDouble(msgValue);
                        msgData = Math.floor(msgData * 10) / 10;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    checkAvaliableAlig(msgData, msgKey);
                    tvAligHValue.setText(String.valueOf(msgData));
                    break;
                case ALIGNMENT_V_KEY:
                    try {
                        msgData = Double.parseDouble(msgValue);
                        msgData = Math.floor(msgData * 10) / 10;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    checkAvaliableAlig(msgData, msgKey);
                    tvAligVValue.setText(String.valueOf(msgData));
                    break;
                case PRESSURE_KEY:
                    try {
                        float result = Float.parseFloat(msgValue) / 1000;
                        tvPressVal.setText(String.valueOf(result));
                        this.setData(result);

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    timerHandler.postDelayed(timerRunnable, 0);
                    break;
            }
        }
    }
//    End load data to form

    private void checkAvaliableAlig(Double value, String msgKey) {
        Button btnCheck = null;
        if (msgKey.equals(ALIGNMENT_V_KEY)) {
            btnCheck = btnAligVCheck;
        } else {
            btnCheck = btnAligHCheck;
        }
        if (Double.valueOf(value) == 0) {
            btnCheck.setBackgroundResource(R.drawable.ic_checkon);
        } else {
            btnCheck.setBackgroundResource(R.drawable.ic_checkoff);
        }
    }

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            second++;
            timerHandler.postDelayed(this, 10000);
        }
    };
//    End Udp and Tcp
}
