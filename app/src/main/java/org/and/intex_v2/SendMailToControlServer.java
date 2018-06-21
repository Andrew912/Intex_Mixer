package org.and.intex_v2;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by and on 19.06.2018.
 */

public class SendMailToControlServer {

    MainActivity
            mainActivity;

    static final int mailSendDelay
            = 3000;                   // Задержка выполнения потока - 10 минут

    /**
     * Конструктор
     *
     * @param activity
     */
    public SendMailToControlServer(MainActivity activity) {
        mainActivity
                = activity;

    }

    /**
     * Отправка протокола на сервер управления - в отдельном потоке
     */
    public void sendMail() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                /*  Тут типа происходит все действие */
                while (true) {
                    Log.i(
                            "****** sendMail ******",
                            "Address=" + mainActivity.conf.controlServer.socketAddr +
                                    ", Port=" + mainActivity.conf.controlServer.socketPort
                    );

                /* Задержка выполнения потока на 10 минут */
                    try {
                        Thread.sleep(mailSendDelay);
//                        Log.i(
//                                "****** sendMail ******",
//                                "Поток был остановлен");
                    } catch (InterruptedException e) {
                        Log.i(
                                "****** sendMail ******",
                                "ОШИБКА при попытке усыпить поток");
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread
                = new Thread(runnable);
//        thread
//                .setDaemon(true);
        thread
                .start();
    }


    /**
     * test of "insert into"
     */
    public void test() {
//        dbHandler = mainActivity.dbFunctions.dbHandler;
//        Log.i("SendMailToControlServer", mainActivity.dbHandler.printTableData("mail"));
//        Log.i(
//                "SendMailToControlServer",
//                "readDeviceAddrFromDB=" +
//                        mainActivity.dbHandler.readDeviceAddrFromDB("192.168.100.", "mixerterm.001", "9"));
//        Log.i(
//                "SendMailToControlServer",
//                "readDeviceAddrFromDB=" +
//                        mainActivity.dbHandler.deviceDataNowInDB("mixerterm.001", "192.168.100."));
//        Log.i(
//                "SendMailToControlServer",mainActivity.dbHandler.printTableData("objects"));


    }


}
