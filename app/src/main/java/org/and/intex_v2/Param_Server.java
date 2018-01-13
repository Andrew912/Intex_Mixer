package org.and.intex_v2;

/**
 * Created by Андрей on 18.07.2017.
 */

public class Param_Server {

    int CONTROL = 0;
    int LOADER = 1;
    int DEVICE = 2;

    // Адрес сервера
    public String[] listIP = new String[]{
            "91.218.229.25",
            "192.168.1.150",
            "192.168.1.113"};

    // Порт сервера
    public int[] listPort = new int[]{
            60000,
            18080,
            18080};

    // Размер буфера приема сообщений
    public int[] listBufferSize = new int[]{
            4096,
            512,
            256};

    // Таймаут опроса сервера
    public int[] listTimes = new int[]{
            1000,
            500,
            500};

    // Режим обмена данными с сервером (?)
    public int[] listBufferMode = new int[]{
            0,
            0,
            1};

    public String IP;
    public int port;
    public int buff;
    public int mode;
    public int time;

    // Constructor
    public Param_Server(int currentServer) {
        IP = listIP[currentServer];
        port = listPort[currentServer];
        buff = listBufferSize[currentServer];
        mode = listBufferMode[currentServer];
        time = listTimes[currentServer];

    }

    public String getIP() {
        return IP;
    }

    public int getPort() {
        return port;
    }

    public int getBuffSize() {
        return buff;
    }

    public int getTime() {
        return time;
    }

    public int getMode() {
        return mode;
    }

    public int getPing() {
        return 2000;
    }


}
