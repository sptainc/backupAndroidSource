package com.example.tainc.sateco_1;

/**
 * Created by tainc on 10/17/2016.
 */

interface SatecoComponent {
    public static final String DISTANCE_H_KEY = "dh";
    public static final String DISTANCE_V_KEY = "dv";
    public static final String ALIGNMENT_H_KEY = "ah";
    public static final String ALIGNMENT_V_KEY = "av";
    public static final String PRESSURE_KEY = "p";
    public static final String SPLIT_STRING = "\\s+";

    public static final int SOCKET_CLIENT_PORT = 16103;
    public static final int SOCKET_SERVER_PORT = 16103;
    public static final int UDP_CLIENT_PORT = 16102;
    public static final int UDP_SERVER_PORT = 16101;

    public static final String IP_SERVER_TCPCLIENT = "192.168.1.1";
    public static final String WIFI_SSID = "SATECODAQ";
    public static final String WIFI_PASSWORD = "dfm1610!";

    public static final String APP_FILE_DATA = "sateco_data.txt";
    public static final int WRITE_DATA = 0;
    public static final int READ_DATA = 1;

}
