package org.and.intex_v2;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by and on 02.07.2018.
 * <p>
 * Класс обмена данными с весовым терминалом миксера
 */

public class TerminalCommunicator {

    MainActivity
            mainActivity;
    String logTAG
            = "Terminal";

    static final int statusWatchPeriod          // Период анализа семафоров
            = 5000;
    boolean
            beignReadData;                      // Продолжаем читать данные терминала

    /* Таймеры */
    private Timer
            myTerminalDataReadTimer;            // Таймер опроса весового терминала
    private TerminalDataReadTimerTask
            myTerminalDataReadTask;             // Задача для опроса весового терминала

    /* Список состояний системы */
    static final int
            WAIT = 0,
            CONNECT = 1,
            READ = 2,
            DISCONNECT = 3;

    /* Текущее состояние системы */
    int status
            = WAIT;

    /* Действия, которые необходимо выполнить системе */
    static final int
            NONE = 0,
            START = 1,
            STOP = 2;

    /* Команда для системы выполнить действие */
    int action
            = NONE;

    /**
     * Конструктор
     */
    public TerminalCommunicator(MainActivity activity) {
        mainActivity
                = activity;
        /**
         * Запуск отдельного потока
         */
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    /* Постоянно в цикле вызываем контроллер */
                    terminalDataReadController();
                    /* Пытаемся усыпить поток на время задержки */
                    try {
                        Thread.sleep(statusWatchPeriod);
                    } catch (InterruptedException e) {
                        Log.i(
                                "****** Terminal ******", "ОШИБКА при попытке усыпить поток");
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Функция управления получением данных с терминала
     */
    void terminalDataReadController() {
        switch (status) {
            case WAIT:                      // Действия из состояния ожидания
                switch (action) {
                    case START:             // Начать чтение данных
                        weightDataFromDeviceReader_Start();
                        action = NONE;
                        status = READ;
                        break;
                }
                break;
            case READ:                      // Действия при активной загрузке
                switch (action) {
                    case STOP:              // Остановить чтение данных
                        weightDataFromDeviceReader_Stop();
                        action = NONE;
                        status = WAIT;
                        break;
                }
                break;
            case CONNECT:                   // Типа подключено, но чтение не начато
                break;
            case DISCONNECT:                // Команда на отключение (???)
                break;
        }

    }

    /**
     * Задача для опроса весового терминала
     */
    class TerminalDataReadTimerTask extends TimerTask {
        @Override
        public void run() {
            Socket socket;
            String socketAddr
                    = mainActivity.conf.terminalAddress;
            int socketPort
                    = 18080;
            InputStream is;
            OutputStream os;
            String o
                    = "ping\n";
            String res
                    = null;
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
                    Log.i(logTAG, "FROM DEVICE=" + res + " at " + Calendar.getInstance().getTime());
                    res = extractDigits(res);
//                    Log.i(logTAG, "FROM DEVICE=" + res);
                    socket.close();
                    /* Пытаемся сохранить показания терминала в протокол, как - см. описание функции */
                    mainActivity.storer.storeCurrentWeightToProtocol(extractData(res));
                }
            } catch (Exception e) {
                Log.i(logTAG, "Not connected: " + e);
            }
            mainActivity.storer.setWeightIndicatorData(res);

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Обновить данные о загрузке на экране
                    mainActivity.displayWeightParameters();
                    mainActivity.displayWeightParameters1();
                }
            });
        }
    }

    /**
     * Старт получения данных от весового терминала
     */

    public boolean weightDataFromDeviceReader_Start() {

        if (mainActivity.ifServerFound(mainActivity.conf.terminalName)==false) return false;

        final int dataReadPeriod
                = 2000;
        Log
                .i(logTAG + ": weightData: ", "start");
        myTerminalDataReadTimer
                = new Timer();
        myTerminalDataReadTask
                = new TerminalDataReadTimerTask();
        myTerminalDataReadTimer
                .schedule(myTerminalDataReadTask, dataReadPeriod, dataReadPeriod);
        return true;
    }

    public void weightDataFromDeviceReader_Stop() {
        Log.i(logTAG + ": weightData: ", "stop");
        if (myTerminalDataReadTimer != null) {
            Log.i(logTAG + ": weightData", "timer myTerminalDataReadTimer not null");
            myTerminalDataReadTimer
                    .purge();
            myTerminalDataReadTimer
                    .cancel();
            myTerminalDataReadTimer
                    = null;
        } else {
            Log.i(logTAG + ": weightData", "timer myTerminalDataReadTimer IS NULL");
        }
    }

    /**
     * Начинаем читать данне из терминала
     */
    void readDataStart() {
        action = START;
    }

    /**
     * Остановить чтение данных из терминала
     */
    void readDataStop() {
        action = STOP;
    }


    /**
     * @param srcString
     * @return
     */
    public String extractDigits(String srcString) {
        String r = "0";
        Pattern pattern
                = Pattern.compile("data=\'\\d+\'");
        Matcher matcher
                = pattern.matcher(srcString);
        if (matcher.find()) {
            r = (matcher.group());
            Log.i(logTAG, "!!!! matcher.find()=" + r);
        }
        return r;
    }

    /**
     * Получение показаний терминала из получемой от него строки
     *
     * @param srcString
     * @return
     */
    public String extractData(String srcString) {
        String r = "0";
        Pattern pattern
                = Pattern.compile("data=\'\\d+\'");
        Matcher matcher
                = pattern.matcher(srcString);
        if (matcher.find()) {
            r = (matcher.group());
//            Log.i(logTAG, "!!!! matcher.find()=" + r);
        }
        pattern
                = Pattern.compile("\\d+");
        matcher
                = pattern.matcher(r);
        if (matcher.find()) {
            r = (matcher.group());
//            Log.i(logTAG, "find()=" + r);
        }
        return r;
    }


}
