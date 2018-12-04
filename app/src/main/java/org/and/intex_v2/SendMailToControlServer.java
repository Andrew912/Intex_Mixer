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

    MainActivity mainActivity;
    String logTAG = "SendMailToControlServer";

    /* Для простоты объявляем класс прямо здесь, хотя
       он объявлен как static в MainActivity.
       При желании можно работать с тем экземпляром */
    MailToSend mailToSend;

    long mailRecordsToSend;                      // Количество записей для отправки

    /* Задержка выполнения потока - 1 минута */
    static final int mailSendDelay = 60000;

    /* Маркер конца строки */
    static final String eol = "\r\n\r\n";

    ServerSendMailClass serverSendMailClass;

    /**
     * Конструктор
     *
     * @param activity
     */
    public SendMailToControlServer(MainActivity activity) {
        mainActivity = activity;
//        mailToSend = new MailToSend(activity, mainActivity.dbHandler.database);
    }

    /**
     * Отправка протокола на сервер управления - в отдельном потоке
     */
    public void sendMail() {
//        /* Подготовить таблицу с данными почты к отправке */
//        if (mailToSend.prepareMail() == 0) return;

        /* Если сервер недоступен - выход */
        if (connectionToServerCheck() == false) return;

        /* Подготовка к запуску в отдельном потоке */
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                /*  Тут типа происходит все действие */
                while (true) {

                    /* Подготовка данных для отправки */
                    mailToSend = new MailToSend(mainActivity, mainActivity.dbHandler.database);

                    /* Если данные для отправки есть */
                    if (mailToSend.prepareMail() != 0) {
                        serverSendMailClass = new ServerSendMailClass();
                        serverSendMailClass.execute();
                    }

                   /* Задержка выполнения потока на указанное время */
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
        mainActivity.dbHandler.printTableData("mail");
        /* Если отправлять нечего - выход */
//        mailRecordsToSend = mailToSend.prepareMail();
        if (mailToSend.prepareMail() == 0) {
            return;
        }
        /* Если сервер недоступен - выход */
        if (connectionToServerCheck() == false) {
            return;
        }
//        mailToSend.test_test();
        /* Если все в порядке - отправка */
        serverSendMailClass
                = new ServerSendMailClass();
        serverSendMailClass
                .execute();
    }

    /**
     * Класс передачи сообщений на сервер
     */
    class ServerSendMailClass extends AsyncTask<Void, Void, Void> {
        String[] o;

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(logTAG, "=== Server SendMail MessagesClass: run ===");
            Socket socket;
            InputStream is;
            OutputStream os;
            long oSize = mailToSend.recordsToSend;
            try {
                InetAddress serverAddr
                        = InetAddress.getByName(mainActivity.conf.controlServer.socketAddr);
                socket
                        = new Socket(serverAddr, mainActivity.conf.controlServer.socketPort);
                if (socket.isConnected() == true) {
                    is = socket.getInputStream();
                    os = socket.getOutputStream();
                    for (int i = 0; i < oSize; i++) {
//                        Log.i(logTAG, "message to send= " + mailToSend.readMessage());
                        byte[] buffer = (mailToSend.readMessage() + eol).getBytes();
                        os.write(buffer);
                        os.flush();
                        buffer = new byte[mainActivity.conf.controlServer.buffSize];
                        int read = is.read(buffer, 0, mainActivity.conf.controlServer.buffSize);
                        byte[] b = new byte[read];
                        System.arraycopy(buffer, 0, b, 0, read);
                        String rs = new String(b);
                        String status = statusMessageTransfer(rs);
                        Log.i(logTAG, "-----------------------------");
                        Log.i(logTAG, "read=" + read + ", rs=" + rs + ", operStatus=" + status);
                        Log.i(logTAG, "-----------------------------");
                        /* Помечаем на удаление успешно отправленные записи */
                        if (status.equals("0")) {
//                            Log.i(logTAG, "message to delete=" + mailToSend.readID() + "," + mailToSend.readMessage());
                            mailToSend.deleteCurrent();
//                            o[i] = null;
                        }
                        /* Перемещаемся на следующую запись в курсоре */
                        mailToSend.moveNext();
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
            Log.i(logTAG, "Удаляем из MAIL записи, помеченные на удаление");
            mailToSend.deleteSent();
            mainActivity.dbHandler.printTableData("mail");
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
     * TEST
     */
    public void test() {
//        Log.i("SendMailToControlServer", mainActivity.dbHandler.printTableData("mailtosend"));
        while (mailToSend.readable()) {
//            Log.i("**** ***** ****", "Id=" + mailToSend.readID() + " Msg=" + mailToSend.readMessage());
            mailToSend.moveNext();
        }

    }

    boolean connectionToServerCheck() {
        return true;
    }

}
