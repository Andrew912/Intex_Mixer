package org.and.intex_v2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by and on 21.06.2018.
 * <p>
 * Класс управляет структурой данных для отправки протоколов на сервер управления
 */

public class MailToSend {

    MainActivity mainActivity;
    SQLiteDatabase database;
    Cursor cursor;
    boolean cursorReadable;
    long cursorPosition;
    long recordsToSend;
    String currentId;

    /**
     * Индексы полей курсора (для скорости, чтобы без преобразований туда-сюда)
     */
    static final int
            CURSOR_FIELDKEY_ID = 0,
            CURSOR_FIELDKEY_MESSAGE = 1;

    /**
     * Количество записей, считываемых в таблицу mailtosend из mail за один цикл оправки почты.
     * Необходимо для ограничения объема памяти, выделяемой для параметра - массива строк,
     * передаваемого в асинхронную процедуру.
     */
    final String mailTableReadLimit = "100";

    /**
     * Конструктор
     *
     * @param activity
     * @param pDatabase
     */
    public MailToSend(MainActivity activity, SQLiteDatabase pDatabase) {
        mainActivity = activity;
        database = pDatabase;
    }

    /**
     * Копирует записи из mail в mailtosend и подготавливает курсор
     *
     * @return количество доступных для обработки записей
     */
    public long prepareMail() {

        database.execSQL(
                "delete from mailtosend");

        database.execSQL(
                "insert into mailtosend (mailid,message) " +
                        "select _id as mailid,message from mail where to_delete=0");

        mainActivity.dbHandler.printTableData("mail");
        mainActivity.dbHandler.printTableData("mailtosend");

        cursor = database.rawQuery(
                "select mailid,message " +
                        "from mailtosend limit ?", new String[]{mailTableReadLimit});

        if (cursor.moveToFirst()) {
            cursorReadable = true;
            cursorPosition = cursor.getPosition();
            recordsToSend = cursor.getCount();
            currentId = cursor.getString(CURSOR_FIELDKEY_ID);
            return recordsToSend;
        } else {
            cursorReadable = false;
            cursorPosition = 0;
            currentId = null;
            return 0;
        }
//        database
//                .execSQL("delete from mailtosend");
//        database
//                .execSQL("insert into mailtosend (mailid,message) select _id as mailid,message from mail where to_delete=0");
//        cursor
//                = database.rawQuery("select mailid,message from mailtosend limit ?", new String[]{mailTableReadLimit});
//
//        if (cursor.moveToFirst()) {
//            cursorReadable = true;
//            cursorPosition = cursor.getPosition();
//            recordsToSend = cursor.getCount();
//            currentId = cursor.getString(CURSOR_FIELDKEY_ID);
//            return recordsToSend;
//        } else {
//            cursorReadable = false;
//            cursorPosition = 0;
//            currentId = null;
//            return 0;
//        }
    }

    /**
     * Удаление из MAIL записей уже отправленных (MAILTOSEND.TO_DELETE=1)
     */
    public void deleteSent() {
        Thread thread;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                database.execSQL(
                        "delete from mail " +
                                "where _id in (select mailid from mailtosend where to_delete=1)");
            }
        };
        thread = new Thread(runnable);
        thread.start();
//        database.execSQL("delete from mail where _id in (select mailid from mailtosend where to_delete=1)");
    }

    /**
     * Пометка на удаление текущей записи
     */
    public void deleteCurrent() {
        if (currentId != null)
            database.execSQL("update mailtosend set to_delete=? where mailid=?", new String[]{"1", currentId});
    }

    /**
     * Устанавливает курсор в начало
     *
     * @return
     */
    public boolean moveFirst() {
        if (cursor.moveToFirst()) {
            cursorReadable = true;
            cursorPosition = cursor.getPosition();
            currentId = cursor.getString(CURSOR_FIELDKEY_ID);
            return true;
        } else {
            cursorReadable = false;
            cursorPosition = 0;
            currentId = null;
            return false;
        }
    }

    /**
     * Сдвигает курсор вперед
     *
     * @return
     */
    public boolean moveNext() {
        if (cursor.moveToNext()) {
            cursorReadable = true;
            cursorPosition = cursor.getPosition();
            currentId = cursor.getString(CURSOR_FIELDKEY_ID);
            return true;
        } else {
            cursorReadable = false;
            cursorPosition = 0;
            currentId = null;
            return false;
        }
    }

    /**
     * Читает значение поля MAILID из текущей строки курсора
     *
     * @return
     */
    public long readID() {
        if (cursorReadable) return cursor.getLong(CURSOR_FIELDKEY_ID);
        return 0;
    }

    /**
     * Читает значение поля MESSAGE из текущей строки курсора
     *
     * @return
     */
    public String readMessage() {
        if (cursorReadable) return cursor.getString(CURSOR_FIELDKEY_MESSAGE);
        return null;
    }

    /**
     * Есть возможность прочитать строку курсора
     *
     * @return
     */
    public boolean readable() {
        if (cursorReadable) return true;
        return false;
    }
}
