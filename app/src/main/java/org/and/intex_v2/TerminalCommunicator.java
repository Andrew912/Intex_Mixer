package org.and.intex_v2;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.TimerTask;

/**
 * Created by and on 21.04.2018.
 */

public class TerminalCommunicator {
    MainActivity
            activity;
    String logTAG
            = "TerminalCommunicator";

    /* Конструктор */
    public TerminalCommunicator(MainActivity mainActivity) {
        activity = mainActivity;

    }

    // Задача для опроса весового терминала
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Socket socket;
            String socketAddr = conf.terminalAddress;
//            String socketAddr = "192.168.1.113";
            int socketPort = 18080;
            InputStream is;
            OutputStream os;
            String o = "ping\n";
            String res = null;
            try {
                InetAddress serverAddr = InetAddress.getByName(socketAddr);
                socket = new Socket(serverAddr, socketPort);
                if (socket.isConnected() == true) {
                    is = socket.getInputStream();
                    os = socket.getOutputStream();
                    byte[] buffer = o.getBytes();
                    os.write(buffer);
                    os.flush();
                    buffer = new byte[256];
                    int read = is.read(buffer, 0, 256);
                    res = new String(buffer).substring(0, read);
                    Log.i(logTAG, "FROM DEVICE=" + res);
                    res = extractDigits(res);
                    socket.close();
//                    storer.setWeightIndicatorData(res);
                }
            } catch (Exception e) {
                Log.i(logTAG, "Not connected: " + e);
            }
            storer.setWeightIndicatorData(res);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Обновить данные о загрузке на экране
                    displayWeightParameters();
                    displayWeightParameters1();
                }
            });
        }
    }
}
