package org.and.intex_v2;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.and.intex_v2.DBHelper.KEY_MAIL_COMPLETE;
import static org.and.intex_v2.DBHelper.KEY_MAIL_MESSAGE;
import static org.and.intex_v2.DBHelper.TABLE_MAIL;

/**
 * Created by and on 19.06.2018.
 */

public class SendMailToControlServer {

    MainActivity
            mainActivity;
    String
            logTAG = "SendMailToControlServer";
    static final int mailSendDelay
            = 3000;                             // Задержка выполнения потока - 10 минут
    String
            eol = "\r\n\r\n";
    ServerSendMailClass
            serverSendMailClass;

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

                    /* Отправка */
//                    send();

                    Log.i(
                            "****** sendMail ******",
                            "\nAddress=" + mainActivity.conf.controlServer.socketAddr +
                                    ", Port=" + mainActivity.conf.controlServer.socketPort + "\n"
                    );
                /* Задержка выполнения потока на 10 минут */
                    try {
                        Thread.sleep(mailSendDelay);
                    } catch (InterruptedException e) {
                        Log.i(
                                "****** sendMail ******", "ОШИБКА при попытке усыпить поток");
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
     * Передача сообщений на сервер (из таблицы MAIL)
     */
    public void send() {

        Log.i(logTAG, "SEND mail");
        // Если отправлять нечего - выход
        if (dbMailRecCount() == 0) {
            return;
        }
        // Если сервер недоступен - выход
        if (connectionToServerCheck() == false) {
            return;
        }
        // Если все в порядке - отправка
        serverSendMailClass.execute(dbMailGetMessages());
    }

    String dbMailGetMessages() {
        return
                new String();
    }

    int dbMailRecCount() {
        return 1;
    }

    boolean connectionToServerCheck() {
        return true;
    }

    /**
     * Класс передачи сообщений на сервер
     */
    class ServerSendMailClass extends AsyncTask<String, Void, Void> {
        String[]
                o;

        @Override
        protected Void doInBackground(String... params) {
            Log.i(logTAG, "ServerSendMailMessagesClass: run");
            Socket socket;
            InputStream is;
            OutputStream os;

            o = params;
            int oSize = o.length;

            Log.i(logTAG, "===========================================");
            for (int i = 0; i < oSize; i++) {
                Log.i(logTAG, "PARAMS(o)=" + o[i]);
            }
            Log.i(logTAG, "===========================================");
            try {
                InetAddress serverAddr = InetAddress.getByName(mainActivity.conf.controlServer.socketAddr);
                socket = new Socket(serverAddr, mainActivity.conf.controlServer.socketPort);
                if (socket.isConnected() == true) {
                    is = socket.getInputStream();
                    os = socket.getOutputStream();

                    for (int i = 0; i < oSize; i++) {
                        Log.i(logTAG, "message to send= " + o[i]);
                        byte[] buffer = (o[i] + eol).getBytes();
                        os.write(buffer);
                        os.flush();
                        buffer = new byte[mainActivity.conf.controlServer.buffSize];
                        int read = is.read(buffer, 0, mainActivity.conf.controlServer.buffSize);
                        byte[] b = new byte[read];
                        System.arraycopy(buffer, 0, b, 0, read);
                        String rs = new String(b);
                        String status = statusMessageTransfer(rs);
                        Log.i(logTAG, "-----------------------------");
                        Log.i(logTAG, "read=" + read);
                        Log.i(logTAG, "-----------------------------");
                        Log.i(logTAG, rs);
                        Log.i(logTAG, "-----------------------------");
                        Log.i(logTAG, "operStatus=" + status);
                        if (status.equals("0") == false) {
                            o[i] = null;
                        }
                    }
                    socket.close();
                }
            } catch (Exception e) {
                Log.i(logTAG, "Ошибка подключения: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.i(logTAG, "Значение массива o[]");
            for (int i = 0; i < o.length; i++) {
                Log.i(logTAG, "o[]=" + o[i]);
                if (o[i] != null) {
                    // Пометить записи, как отправленные
                    setMailRecordAsSended(o[i]);
                }
            }
            Log.i(logTAG, "============================ Список почты");
            new DBFunctions(mainActivity, mainActivity.dbHelper.getWritableDatabase()).mail();
            dbMailDeleteSended();
            Log.i(logTAG, "============================ Список почты  после отправки");
            new DBFunctions(mainActivity, mainActivity.dbHelper.getWritableDatabase()).mail();
        }
    }

    // Пометить запись как отправленную (complete)
    void setMailRecordAsSended(String key) {
        DBHelper dbh = new DBHelper(mainActivity.context);
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_MAIL_COMPLETE, 1);
        SQLiteDatabase db = dbh.getWritableDatabase();

        int res = db.update(TABLE_MAIL,
                newValues,
                KEY_MAIL_MESSAGE + "=?",
                new String[]{key}
        );

        Log.i(logTAG, "setMailRecordAsSended(\"" + key + "\"), res=" + res);

        db.close();
        dbh.close();
    }

    // Очистка MAIL от отправленных сообщений
    int dbMailDeleteSended() {
        String[] r;
        DBHelper dbh = new DBHelper(mainActivity.context);
        SQLiteDatabase db = dbh.getWritableDatabase();
        int res = db.delete(TABLE_MAIL,
                KEY_MAIL_COMPLETE + "=?",
                new String[]{String.valueOf(1)}
        );
        Log.i(logTAG, res + " message(s) deleted");
        db.close();
        dbh.close();
        return res;
    }

    /**
     * Статус отправки сообщения
     *
     * @param resivedString
     * @return
     */
    String statusMessageTransfer(String resivedString) {
        String r = "0";
        Pattern pattern = Pattern.compile("st=\'\\d+\'");
        Matcher matcher = pattern.matcher(resivedString);
        while (matcher.find()) {
            r = matcher.group().substring(4, 5);
        }

        return r;
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

        mainActivity.dbHandler.copyMail();

        Log.i("SendMailToControlServer", mainActivity.dbHandler.printTableData("mailtosend"));


    }


}
