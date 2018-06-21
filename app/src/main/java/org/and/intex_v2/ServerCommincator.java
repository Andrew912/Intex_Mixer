package org.and.intex_v2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.and.intex_v2.DBHelper.KEY_MAIL_COMPLETE;
import static org.and.intex_v2.DBHelper.KEY_MAIL_MESSAGE;
import static org.and.intex_v2.DBHelper.KEY_TASK_COMMENT;
import static org.and.intex_v2.DBHelper.KEY_TASK_COMPLETE;
import static org.and.intex_v2.DBHelper.KEY_TASK_ID;
import static org.and.intex_v2.DBHelper.KEY_TASK_IS_CURRENT;
import static org.and.intex_v2.DBHelper.KEY_TASK_STATUS;
import static org.and.intex_v2.DBHelper.KEY_TASK_TO_DELETE;
import static org.and.intex_v2.DBHelper.TABLE_MAIL;


/**
 * Created by Андрей on 03.08.2017.
 */

public class ServerCommincator {

    String
            logTAG = "Server Communicator";
    MainActivity
            mainActivity;
    String
            rs;
    String
            socketAddr;
    int
            socketPort;
    int
            buffSize;
    String
            eol = "\r\n\r\n";

    boolean
            dataFromServerReaded;

    /**
     * Конструктор
     *
     * @param activity
     */
    public ServerCommincator(MainActivity activity) {
        this.mainActivity
                = activity;

        /* Установка параметров сервера управления */
        socketAddr
                = mainActivity.conf.controlServer.socketAddr;
        socketPort
                = mainActivity.conf.controlServer.socketPort;
        buffSize
                = mainActivity.conf.controlServer.buffSize;

//        socketAddr
//                = "91.218.229.25";
//        socketPort
//                = 60001;
//        buffSize
//                = 1024;
    }

    // Главная запускалка - отсюда запускаем все, что надо
    public void main() {
//        new DBFunctions(mainActivity).mailClear();
//        insertIntoMailTable_test();
//        Log.i(logTAG, "reccount=" + dbMailRecCount());
        readTask();
//        sendMail();
//        readTask();
//        readOper();
    }

    // Получение списка операций
    public void readOper() {
        new ServerExchangeClass_getOperations().execute(new String[]{getCurrentTaskIdFromDB()});
    }

    // Класс получения списка операций
    class ServerExchangeClass_getOperations extends AsyncTask<String, Void, Void> {
        int bufferSize = 30;
        CycleBuffer sourceInputLineBuffer = new CycleBuffer(bufferSize);

        @Override
        protected Void doInBackground(String... params) {
            Log.i(logTAG, "ServerExchangeClass_getOperations: params=" + params[0]);
            Socket socket;
            InputStream is;
            OutputStream os;
            rs = "";
            // Формирование запроса на список операций
            String o = new MessageMaker(mainActivity).request_OperList(params[0]);
            try {
                InetAddress serverAddr = InetAddress.getByName(socketAddr);
                socket = new Socket(serverAddr, socketPort);
                if (socket.isConnected() == true) {
                    is = socket.getInputStream();
                    os = socket.getOutputStream();
                    byte[] buffer = o.getBytes();
                    Log.i(logTAG, "\n" + o);
                    os.write(buffer);
                    os.flush();
                    buffer = new byte[buffSize];
                    boolean
                            stopRead = false;
                    int
                            read = 0;
                    while (stopRead == false) {
                        read = is.read(buffer, 0, buffSize);
                        Log.i(logTAG, "read=" + read);
                        byte[] b = new byte[read];
                        System.arraycopy(buffer, 0, b, 0, read);
                        rs = rs + new String(b);
                        if (read < buffSize) {
                            stopRead = true;
                        }
                    }
//                    rs = new String(b).replace("\"", "--");
                    Log.i(logTAG, "-----------------------------");
                    Log.i(logTAG, "read=" + read);
                    Log.i(logTAG, "-----------------------------");
                    Log.i(logTAG, rs);
                    Log.i(logTAG, "-----------------------------");
                    socket.close();
                    dataFromServerReaded = true;
                }
            } catch (Exception e) {
                Log.i(logTAG, "Ошибка подключения: " + e);
                dataFromServerReaded = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Если данные с сервера прочитались
            if (dataFromServerReaded == true) {
                sourceInputLineBuffer = ExtractToSourceInputLineBuffer(rs, bufferSize);          // Разобрать поток на строки
                // Определение статуса сообщения St=?
                String messageStatus = null;
                if (sourceInputLineBuffer.getLen() > 0) {
                    messageStatus = ExtractMessageStatus(sourceInputLineBuffer.get());
                }
                //
                if (messageStatus.equals("0")) {
                    while (sourceInputLineBuffer.getLen() > 0) {
                        ExtractParametersForEachOperation(ExtractParametersOfCommand(sourceInputLineBuffer.get()));
                    }
                }
            }
            // А вот если не прочитались, хз что делать...
            else {
                mainActivity.errorReadDataFromServer();
            }
        }
    }

    // Получение списка задач
    public void readTask() {
        new ServerExchangeClass_getTasks().execute();
    }

    // Класс получения списка задач
    class ServerExchangeClass_getTasks extends AsyncTask<Void, Void, Void> {
        int bufferSize = 5;
        CycleBuffer sourceInputLineBuffer = new CycleBuffer(bufferSize);

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(logTAG, "ServerExchangeClass_getTasks: run");
            Socket socket;
            InputStream is;
            OutputStream os;
            String o = new MessageMaker(mainActivity).request_TaskLisk();
            try {
                InetAddress serverAddr = InetAddress.getByName(socketAddr);
                socket = new Socket(serverAddr, socketPort);
                if (socket.isConnected() == true) {
                    is = socket.getInputStream();
                    os = socket.getOutputStream();
                    byte[] buffer = o.getBytes();
                    Log.i(logTAG, "\n" + o);
                    os.write(buffer);
                    os.flush();
                    buffer = new byte[buffSize];
                    int read = is.read(buffer, 0, buffSize);
                    byte[] b = new byte[read];
                    System.arraycopy(buffer, 0, b, 0, read);
                    rs = new String(b);
                    Log.i(logTAG, "-----------------------------");
                    Log.i(logTAG, "read=" + read);
                    Log.i(logTAG, "-----------------------------");
                    Log.i(logTAG, rs);
                    Log.i(logTAG, "-----------------------------");
                    socket.close();
                    dataFromServerReaded = true;
                }
            } catch (Exception e) {
                Log.i(logTAG, "Ошибка подключения: " + e);
                dataFromServerReaded = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Если данные сервера прочитались
            if (dataFromServerReaded == true) {
                sourceInputLineBuffer = ExtractToSourceInputLineBuffer(rs, bufferSize);          // Разобрать поток на строки
                // Определение статуса сообщения St=?
                String messageStatus = null;
                if (sourceInputLineBuffer.getLen() > 0) {
                    messageStatus = ExtractMessageStatus(sourceInputLineBuffer.get());
                }
                //
                if (messageStatus.equals("0")) {
                    while (sourceInputLineBuffer.getLen() > 0) {
                        ExtractParametersForEachTask(ExtractParametersOfCommand(sourceInputLineBuffer.get().replace("\"", "--")));
                    }
                    // Операции читаем только если есть свежая задача
                    readOper();
                }
            }
            // А вот если не прочитались, хз что делать...
            else {
                mainActivity.errorReadDataFromServer();
            }
        }
    }


    /**
     * Передача сообщений на сервер (из таблицы MAIL)
     */
    public void sendMail() {

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
        new ServerSendMailMessagesClass().execute(dbMailGetMessages());
    }

    /**
     * Класс передачи сообщений на сервер
     */
    class ServerSendMailMessagesClass extends AsyncTask<String, Void, Void> {
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
                InetAddress serverAddr = InetAddress.getByName(socketAddr);
                socket = new Socket(serverAddr, socketPort);
                if (socket.isConnected() == true) {
                    is = socket.getInputStream();
                    os = socket.getOutputStream();

                    for (int i = 0; i < oSize; i++) {
                        Log.i(logTAG, "message to send= " + o[i]);
                        byte[] buffer = (o[i] + eol).getBytes();
                        os.write(buffer);
                        os.flush();
                        buffer = new byte[buffSize];
                        int read = is.read(buffer, 0, buffSize);
                        byte[] b = new byte[read];
                        System.arraycopy(buffer, 0, b, 0, read);
                        rs = new String(b);
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

    // Выделяем отдельные команды
    public String ExtractMessageStatus(String inS) {
        Pattern pattern = Pattern.compile(mainActivity.getString(R.string.pattern_Cmd_Name) + "=\'" + mainActivity.getString(R.string.pattern_Cmd_Value) + "\'");
        Matcher matcher = pattern.matcher(inS);
        IncomingMessageLineParamsClass t = new IncomingMessageLineParamsClass();
        if (matcher.find()) {
            t = extractParam(matcher.group());
        }
        return t.getValue();
    }

    // Выделяем отдельные команды
    public ArrayList<IncomingMessageLineParamsClass> ExtractParametersOfCommand(String inS) {
        ArrayList<IncomingMessageLineParamsClass> parameters;
        parameters = new ArrayList<>();
        Pattern pattern = Pattern.compile(mainActivity.getString(R.string.pattern_Cmd_Name) + "=\'" + mainActivity.getString(R.string.pattern_Cmd_Value) + "\'");
        Matcher matcher = pattern.matcher(inS);
        IncomingMessageLineParamsClass t = new IncomingMessageLineParamsClass();
        while (matcher.find()) {
//            t = new IncomingMessageLineParamsClass();
            t = extractParam(matcher.group());
            parameters.add(t);
        }
        return parameters;
    }

    // Разбор входящего потока на строки
    public CycleBuffer ExtractToSourceInputLineBuffer(String inBuffer, int outBufferSize) {
        if (dataFromServerReaded == true) {
            CycleBuffer sourceInputLineBuffer = new CycleBuffer(outBufferSize);
            int i = 0;
            String regEx = mainActivity.getString(R.string.pattern_EndOfLine);
            Pattern ptnLine = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
            String[] lines = ptnLine.split(inBuffer);
            for (String line : lines) {
                if (line != null) {
                    sourceInputLineBuffer.put(line);
                }
            }
            sourceInputLineBuffer.get();
            return sourceInputLineBuffer;
        } else {
            return null;
        }
    }

    // Извлекаем параметры команды для каждой строки и  
    public void ExtractParametersForEachOperation(ArrayList<IncomingMessageLineParamsClass> mp) {
        // Параметры операции для сохранения в БД
        String operId = null;
        String taskId = null;
        String type = null;
        String comment = null;
        // Находим параметры операции
        for (int i = 0; i < mp.size(); i++) {
            // Код операции
            if (mp.get(i).getName().equals(mainActivity.getString(R.string.MESSAGE_OPER_ID_KEYWORD))) {
                operId = mp.get(i).getValue();
            }
            // Код задачи
            if (mp.get(i).getName().equals(mainActivity.getString(R.string.MESSAGE_TASK_ID_KEYWORD))) {
                taskId = mp.get(i).getValue();
            }
            // Тип операции
            if (mp.get(i).getName().equals(mainActivity.getString(R.string.MESSAGE_OPER_TYPE_KEYWORD))) {
                type = mp.get(i).getValue();
            }
            // Комментарий
            if (mp.get(i).getName().equals(mainActivity.getString(R.string.MESSAGE_OPER_COMMENT_KEYWORD))) {
                comment = mp.get(i).getValue();
            }
        }
        // Запись в БД
        putToDB_Operation(new String[]{taskId, operId, type, comment});
        // Запись в БД параметров операции
        for (IncomingMessageLineParamsClass p : mp) {
            putToDB_OperationParameters(new String[]{operId, p.getName(), p.getValue()});
        }
        new DBFunctions(mainActivity, mainActivity.dbHelper.getWritableDatabase()).operList();
    }

    // Извлекаем параметры команды для каждой строки и
    public void ExtractParametersForEachTask(ArrayList<IncomingMessageLineParamsClass> mp) {
        // Параметры операции для сохранения в БД
        String taskId = null;
        String name = null;
        Log.i(logTAG, "ExtractParametersForEachTask(): start");
        // Находим параметры задачи
        for (int i = 0; i < mp.size(); i++) {
            // Код задачи
            if (mp.get(i).getName().equals(mainActivity.getString(R.string.MESSAGE_TASK_ID_KEYWORD))) {
                taskId = mp.get(i).getValue();
                Log.i(logTAG, "ExtractParametersForEachTask(): taskId" + taskId);
            }
            // Наименование задачи
            if (mp.get(i).getName().equals(mainActivity.getString(R.string.MESSAGE_TASK_NAME_KEYWORD))) {
                name = mp.get(i).getValue();
                Log.i(logTAG, "ExtractParametersForEachTask(): operName" + name);
            }
        }
        // Запись в БД
        putToDB_Task(new String[]{taskId, name});
        new DBFunctions(mainActivity, mainActivity.dbHelper.getWritableDatabase()).task();
    }

    // Запись в БД данных операций
    void putToDB_Task(String[] s) {
        Log.i(logTAG, "putToDB_Task(): taskId=" + s[0] + ", operName=" + s[1]);
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_TASK_ID, s[0]);
        newValues.put(KEY_TASK_COMMENT, s[1]);
        DBHelper dbh = new DBHelper(mainActivity.context);
        SQLiteDatabase db = dbh.getWritableDatabase();
        db.insert(dbh.TABLE_TASK, null, newValues);
        db.close();
        dbh.close();
    }

    // Запись в БД данных операций
    void putToDB_Operation(String[] s) {
        Log.i(logTAG, "taskId=" + s[0] + ", operId=" + s[1] + ", operType=" + s[2] + ", taskComment=" + s[3]);
        if (s[3] == null) {
            s[3] = s[2];
        }
        ContentValues newValues = new ContentValues();
        newValues.put(DBHelper.KEY_OPER_TASK_ID, s[0]);
        newValues.put(DBHelper.KEY_OPER_ID, s[1]);
        newValues.put(DBHelper.KEY_OPER_TYPE, s[2].toLowerCase());
        newValues.put(DBHelper.KEY_OPER_NAME, s[3]);
        DBHelper dbh = new DBHelper(mainActivity.context);
        SQLiteDatabase db = dbh.getWritableDatabase();
        db.insert(dbh.TABLE_OPER, null, newValues);
        db.close();
        dbh.close();
    }

    // Запись в БД данных параметров операции
    void putToDB_OperationParameters(String[] s) {
        Log.i(logTAG, "operId=" + s[0] + ", operName=" + s[1] + ", value=" + s[2]);

        ContentValues newValues = new ContentValues();

        newValues.put(DBHelper.KEY_OPPA_OPER_ID, s[0]);
        newValues.put(DBHelper.KEY_OPPA_PARAM_NAME, s[1]);
        newValues.put(DBHelper.KEY_OPPA_PARAM_VALUE, s[2]);

        DBHelper dbh = new DBHelper(mainActivity.context);
        SQLiteDatabase db = dbh.getWritableDatabase();
        db.insert(dbh.TABLE_OPER_PARAM, null, newValues);
        db.close();
        dbh.close();
    }

    private IncomingMessageLineParamsClass extractParam(String inS) {
        IncomingMessageLineParamsClass retV = null;
        Pattern patternOfName = Pattern.compile("^" + mainActivity.getString(R.string.pattern_Cmd_Name));
        Matcher matcherOfName = patternOfName.matcher(inS);
        if (matcherOfName.find()) {
            Pattern patternOfValue = Pattern.compile("=\'" + mainActivity.getString(R.string.pattern_Cmd_Value) + "\'$");
            Matcher matcherOfValue = patternOfValue.matcher(inS);
            if (matcherOfValue.find()) {
                retV = new IncomingMessageLineParamsClass(matcherOfName.group(), matcherOfValue.group().replace("'", "").replace("=", ""));
            }
        }
        return retV;
    }

    // Список сообщений из таблицы mail, которые еще не отправлены
    String[] dbMailGetMessages() {
        String[]
                r;
        DBHelper dbh
                = new DBHelper(mainActivity.context);
        SQLiteDatabase db
                = dbh.getWritableDatabase();
        Cursor cursor
                = db.query(TABLE_MAIL,
                new String[]{KEY_MAIL_MESSAGE},
                KEY_MAIL_COMPLETE + "=?",
                new String[]{String.valueOf(0)},
                null, null, null);

        if (cursor.getCount() > 0) {
            r = new String[cursor.getCount()];
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                r[i] = cursor.getString(cursor.getColumnIndex(KEY_MAIL_MESSAGE));
                cursor.moveToNext();
            }
            cursor.close();
        } else {
            r = null;
        }
        Log.i(logTAG, "message to send=" + r);
        db.close();
        dbh.close();
        return r;
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

    //
    int dbMailRecCount() {
        int r;
        DBHelper dbh = new DBHelper(mainActivity.context);
        Cursor cursor = dbh.getReadableDatabase().query(
                TABLE_MAIL,
                new String[]{"COUNT (_id) AS counter"},
                KEY_MAIL_COMPLETE + "=?",
                new String[]{String.valueOf(0)},
                null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            r = Integer.parseInt(cursor.getString(0));
        } else {
            r = 0;
        }
        dbh.close();
        Log.i(logTAG, "dbMailRecCount()=" + r);
        return r;
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

    // Получить данные задачи из БД
    String getCurrentTaskIdFromDB() {
        String r = null;
        Log.i(logTAG, "getCurrentTaskIdFromDB: start find taskId");
        DBHelper dbh = new DBHelper(mainActivity.context);
        Cursor c = dbh.getWritableDatabase().query(
                dbh.TABLE_TASK,
                new String[]{
                        KEY_TASK_ID,
                        KEY_TASK_COMMENT,
                        KEY_TASK_STATUS,
                        KEY_TASK_IS_CURRENT,
                        KEY_TASK_COMPLETE,
                        KEY_TASK_TO_DELETE},
                "(" + KEY_TASK_COMPLETE + "=? AND " + KEY_TASK_TO_DELETE + "=?)",
                new String[]{String.valueOf(0), String.valueOf(0)},
                null, null, null);

        if (c.moveToFirst()) {
            r = c.getString(c.getColumnIndex(KEY_TASK_ID));
        }
        dbh.close();
        Log.i(logTAG, "getCurrentTaskIdFromDB: taskId=" + r);
        return r;
    }

    // TEST insert records into table MAIL
    void insertIntoMailTable_test() {

        DBHelper dbh = new DBHelper(mainActivity.context);
        SQLiteDatabase db = dbh.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put(KEY_MAIL_MESSAGE, new MessageMaker(mainActivity).report_OperBegin("101"));
        db.insert(TABLE_MAIL, null, newValues);

        newValues = new ContentValues();
        newValues.put(KEY_MAIL_MESSAGE, new MessageMaker(mainActivity).report_OperBegin("102"));
        db.insert(TABLE_MAIL, null, newValues);

        newValues = new ContentValues();
        newValues.put(KEY_MAIL_MESSAGE, new MessageMaker(mainActivity).report_OperBegin("103"));
        db.insert(TABLE_MAIL, null, newValues);
        db.close();
        dbh.close();
    }

    //
    boolean connectionToServerCheck() {
        return true;
    }

}
