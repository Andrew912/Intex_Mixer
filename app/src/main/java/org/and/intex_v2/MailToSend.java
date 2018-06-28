package org.and.intex_v2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static org.and.intex_v2.DBHelper.KEY_MAILTOSEND_ID;
import static org.and.intex_v2.DBHelper.KEY_MAILTOSEND_MESSAGE;

/**
 * Created by and on 21.06.2018.
 * <p>
 * Класс управляет структурой данных для отправки протоколов на сервер управления
 */

public class MailToSend {

    MainActivity
            mainActivity;
    SQLiteDatabase
            database;
    Cursor
            cursor;
    boolean
            cursorReadable;
    long
            cursorPosition;

    /**
     * Количество записей, считываемых в таблицу mailtosend из mail за один цикл оправки почты.
     * Необходимо для ограничения объема памяти, выделяемой для параметра - массива строк,
     * передаваемого в асинхронную процедуру.
     */
    final String
            mailTableReadLimit = "50";

    /**
     * Конструктор
     *
     * @param activity
     * @param pDatabase
     */
    public MailToSend(MainActivity activity, SQLiteDatabase pDatabase) {
        mainActivity
                = activity;
        database
                = pDatabase;
    }

    /**
     * Копирует записи из mail в mailtosend и подготавливает курсор
     *
     * @return количество доступных для обработки записей
     */
    public long prepareMail() {
        database
                .execSQL("delete from mailtosend");
        database
                .execSQL("insert into mailtosend (mailid,message) select _id as mailid,message from mail where to_delete=0");
        cursor
                = database.rawQuery("select mailid,message from mailtosend limit ?", new String[]{mailTableReadLimit});

        if (cursor.moveToFirst()) {
            cursorReadable = true;
            cursorPosition = cursor.getPosition();
            return cursor.getCount();
        } else {
            cursorReadable = false;
            cursorPosition = 0;
            return 0;
        }
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
            return true;
        } else {
            cursorReadable = false;
            cursorPosition = 0;
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
            return true;
        } else {
            cursorReadable = false;
            cursorPosition = 0;
            return false;
        }
    }

    /**
     * Читает значение поля MAILID из текущей строки курсора
     *
     * @return
     */
    public long readID() {
        if (cursorReadable) return cursor.getLong(cursor.getColumnIndex(KEY_MAILTOSEND_ID));
        return 0;
    }

    /**
     * Читает значение поля MESSAGE из текущей строки курсора
     *
     * @return
     */
    public String readMessage() {
        if (cursorReadable) return cursor.getString(cursor.getColumnIndex(KEY_MAILTOSEND_MESSAGE));
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

    /**
     * Закрывает курсор
     */
    public void cursorClose() {
        cursor.close();
        cursorReadable = false;
        cursorPosition = 0;
    }
}
