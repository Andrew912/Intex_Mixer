package org.and.intex_v2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static org.and.intex_v2.DBHelper.KEY_MAIL_ANSWER;
import static org.and.intex_v2.DBHelper.KEY_MAIL_COMPLETE;
import static org.and.intex_v2.DBHelper.KEY_MAIL_ID;
import static org.and.intex_v2.DBHelper.KEY_MAIL_MESSAGE;
import static org.and.intex_v2.DBHelper.KEY_MAIL_RECIPIENT;
import static org.and.intex_v2.DBHelper.KEY_MAIL_REPORTED;
import static org.and.intex_v2.DBHelper.KEY_MAIL_TIME;
import static org.and.intex_v2.DBHelper.KEY_MAIL_TO_DELETE;
import static org.and.intex_v2.DBHelper.KEY_OPER_COMPLETE;
import static org.and.intex_v2.DBHelper.KEY_OPER_ID;
import static org.and.intex_v2.DBHelper.KEY_OPER_IS_CURRENT;
import static org.and.intex_v2.DBHelper.KEY_OPER_NAME;
import static org.and.intex_v2.DBHelper.KEY_OPER_STATUS;
import static org.and.intex_v2.DBHelper.KEY_OPER_TASK_ID;
import static org.and.intex_v2.DBHelper.KEY_OPER_TO_DELETE;
import static org.and.intex_v2.DBHelper.KEY_OPER_TYPE;
import static org.and.intex_v2.DBHelper.KEY_OPPA_ID;
import static org.and.intex_v2.DBHelper.KEY_OPPA_OPER_ID;
import static org.and.intex_v2.DBHelper.KEY_OPPA_PARAM_NAME;
import static org.and.intex_v2.DBHelper.KEY_OPPA_PARAM_VALUE;
import static org.and.intex_v2.DBHelper.KEY_OPPA_TO_DELETE;
import static org.and.intex_v2.DBHelper.KEY_TASK_COMMENT;
import static org.and.intex_v2.DBHelper.KEY_TASK_COMPLETE;
import static org.and.intex_v2.DBHelper.KEY_TASK_ID;
import static org.and.intex_v2.DBHelper.KEY_TASK_IS_CURRENT;
import static org.and.intex_v2.DBHelper.KEY_TASK_STATUS;
import static org.and.intex_v2.DBHelper.KEY_TASK_TO_DELETE;
import static org.and.intex_v2.DBHelper.NONE;
import static org.and.intex_v2.DBHelper.OPER_PARAM;
import static org.and.intex_v2.DBHelper.TABLENAME;
import static org.and.intex_v2.DBHelper.TABLE_MAIL;
import static org.and.intex_v2.DBHelper.TABLE_OPER;
import static org.and.intex_v2.DBHelper.TABLE_OPER_PARAM;
import static org.and.intex_v2.DBHelper.TABLE_TASK;


/**
 * Created by Андрей on 16.07.2017.
 */

public class DBFunctions {

    MainActivity activity;
    DBHelper dbHelper;
    SQLiteDatabase db;

    public DBFunctions(MainActivity mainActivity) {
        activity =
                mainActivity;
        dbHelper =
                new DBHelper(activity);
        db =
                dbHelper.getWritableDatabase();
    }

    String dns() {
        activity.log("DNS begin");
        String s = "Список задач\n\n";
        Cursor c = db.query(
                dbHelper.TABLE_TASK,
                new String[]{
                        KEY_TASK_ID,
                        KEY_TASK_COMMENT,
                        KEY_TASK_STATUS,
                        KEY_TASK_IS_CURRENT,
                        KEY_TASK_COMPLETE,
                        KEY_TASK_TO_DELETE},
                null, null, null, null, null);
        s = s + "Записей: " + c.getCount() + "\n\n";

        if (c.moveToFirst()) {
            do {
                s = s +
                        "taskId=" + c.getString(c.getColumnIndex(KEY_TASK_ID))
                        + "\ntaskComment=" + c.getString(c.getColumnIndex(KEY_TASK_COMMENT))
                        + "\ncurrent=" + c.getString(c.getColumnIndex(KEY_TASK_IS_CURRENT))
                        + "\noperStatus=" + c.getString(c.getColumnIndex(KEY_TASK_STATUS))
                        + "\ncomplete=" + c.getString(c.getColumnIndex(KEY_TASK_COMPLETE))
                        + "\ndelete=" + c.getString(c.getColumnIndex(KEY_TASK_TO_DELETE))
                        + "\n\n";
            } while (c.moveToNext() == true);
            activity.log(s);
            c.close();
        }
        return s;
    }

    String task() {
        activity.log("TASK begin");
        String s = "Список задач\n\n";
        Cursor c = db.query(
                dbHelper.TABLE_TASK,
                new String[]{
                        KEY_TASK_ID,
                        KEY_TASK_COMMENT,
                        KEY_TASK_STATUS,
                        KEY_TASK_IS_CURRENT,
                        KEY_TASK_COMPLETE,
                        KEY_TASK_TO_DELETE},
                null, null, null, null, null);
        s = s + "Записей: " + c.getCount() + "\n\n";

        if (c.moveToFirst()) {
            do {
                s = s +
                        "taskId=" + c.getString(c.getColumnIndex(KEY_TASK_ID))
                        + "\ntaskComment=" + c.getString(c.getColumnIndex(KEY_TASK_COMMENT))
                        + "\ncurrent=" + c.getString(c.getColumnIndex(KEY_TASK_IS_CURRENT))
                        + "\noperStatus=" + c.getString(c.getColumnIndex(KEY_TASK_STATUS))
                        + "\ncomplete=" + c.getString(c.getColumnIndex(KEY_TASK_COMPLETE))
                        + "\ndelete=" + c.getString(c.getColumnIndex(KEY_TASK_TO_DELETE))
                        + "\n\n";
            } while (c.moveToNext() == true);
            activity.log(s);
            c.close();
        }
        return s;
    }

    String task1() {
        activity.log("TASK begin");
        String s = "";
        Cursor c = db.query(
                dbHelper.TABLE_TASK,
                new String[]{
                        KEY_TASK_ID,
                        KEY_TASK_COMMENT,
                        KEY_TASK_STATUS,
                        KEY_TASK_IS_CURRENT,
                        KEY_TASK_COMPLETE,
                        KEY_TASK_TO_DELETE},
                "(" + KEY_TASK_IS_CURRENT + "=?) AND (" + KEY_TASK_COMPLETE + "=?)",
                new String[]{String.valueOf(1), String.valueOf(0)},
                null, null, null);
        s = s + "Записей: " + c.getCount() + "\n\n";
        if (c.moveToFirst()) {
            do {
                s = s +
                        "operId=" + c.getString(c.getColumnIndex(KEY_TASK_ID))
                        + "\naskComment=" + c.getString(c.getColumnIndex(KEY_TASK_COMMENT))
                        + "\nurrent=" + c.getString(c.getColumnIndex(KEY_TASK_IS_CURRENT))
                        + "\nperStatus=" + c.getString(c.getColumnIndex(KEY_TASK_STATUS))
                        + "\nomplete=" + c.getString(c.getColumnIndex(KEY_TASK_COMPLETE))
                        + "\nelete=" + c.getString(c.getColumnIndex(KEY_TASK_TO_DELETE))
                        + "\n\n";
            } while (c.moveToNext() == true);

            activity.log(s);
//            activity.textView[0].setText(s);
        }
        return s;
    }

    String oper() {
        activity.log("OPER begin");
        String s = "Список операций\n\n";
        Cursor c = db.query(
                dbHelper.TABLE_OPER,
                new String[]{
                        KEY_OPER_ID,
                        KEY_OPER_TYPE,
                        KEY_OPER_NAME,
                        KEY_OPER_STATUS,
                        KEY_OPER_COMPLETE,
                        KEY_OPER_IS_CURRENT,
                        KEY_OPER_TASK_ID,
                        KEY_OPER_TO_DELETE
                },
                null, null, null, null, null);
        s = s + "Записей: " + c.getCount() + "\n\n";
        if (c.moveToFirst()) {
            do {
                s = s +
                        "operId=" + c.getString(c.getColumnIndex(KEY_OPER_ID))
                        + "\ntask=" + c.getString(c.getColumnIndex(KEY_OPER_TASK_ID))
                        + "\ncomment=" + c.getString(c.getColumnIndex(KEY_OPER_NAME))
                        + "\ntype=" + c.getString(c.getColumnIndex(KEY_OPER_TYPE))
                        + "\nstatus=" + c.getString(c.getColumnIndex(KEY_OPER_STATUS))
                        + "\ncomplete=" + c.getString(c.getColumnIndex(KEY_OPER_COMPLETE))
                        + "\ncurrent=" + c.getString(c.getColumnIndex(KEY_OPER_IS_CURRENT))
                        + "\nlete=" + c.getString(c.getColumnIndex(KEY_OPER_TO_DELETE))
                        + "\n\n";
            } while (c.moveToNext() == true);
            activity.log(s);
//            activity.textView[0].setText(s);
            c.close();
        }
        return s;
    }

    String mail() {
        activity.log("MAIL begin");
        String s = "Список сообщений\n\n";
        Cursor c = db.query(dbHelper.TABLE_MAIL,
                new String[]{
                        KEY_MAIL_ID,
                        KEY_MAIL_RECIPIENT,
                        KEY_MAIL_MESSAGE,
                        KEY_MAIL_TIME,
                        KEY_MAIL_ANSWER,
                        KEY_MAIL_COMPLETE,
                        KEY_MAIL_REPORTED,
                        KEY_MAIL_TO_DELETE
                },
                null, null, null, null, null);
        s = s + "Записей: " + c.getCount() + "\n\n";
        if (c.moveToFirst()) {
            do {
                s =
                        "operId=" + c.getString(c.getColumnIndex(KEY_MAIL_ID))
                                + "\nrecipient=" + c.getString(c.getColumnIndex(KEY_MAIL_RECIPIENT))
                                + "\nmessage=" + c.getString(c.getColumnIndex(KEY_MAIL_MESSAGE))
                                + "\ncomment=" + c.getString(c.getColumnIndex(KEY_MAIL_TIME))
                                + "\nanswer=" + c.getString(c.getColumnIndex(KEY_MAIL_ANSWER))
                                + "\nreport=" + c.getString(c.getColumnIndex(KEY_MAIL_REPORTED))
                                + "\ncomplete=" + c.getString(c.getColumnIndex(KEY_MAIL_COMPLETE))
                                + "\ndelete=" + c.getString(c.getColumnIndex(KEY_MAIL_TO_DELETE))
                                + "\n\n";
                activity.log(s);
            } while (c.moveToNext() == true);
            c.close();
        }
        return s;
    }

    void operList() {
        activity.log(true, "OPER begin");
        String s = "Task: ALL\n";
        Cursor c = db.query(
                dbHelper.TABLE_OPER,
                new String[]{
                        KEY_OPER_ID,
                        KEY_OPER_NAME,
                        KEY_OPER_TYPE,
                        KEY_OPER_STATUS,
                        KEY_OPER_COMPLETE,
                        KEY_OPER_IS_CURRENT,
                        KEY_OPER_TASK_ID,
                        KEY_OPER_TO_DELETE
                },
                "(" + KEY_OPER_COMPLETE + "=?)",
                new String[]{String.valueOf(0)},
                null, null, null);
        s = s + "Записей: " + c.getCount() + "\n";
        if (c.moveToFirst()) {
            do {
                s = s +
                        "operId=" + c.getString(c.getColumnIndex(KEY_OPER_ID))
                        + "\ntask=" + c.getString(c.getColumnIndex(KEY_OPER_TASK_ID))
                        + "\ncomment=" + c.getString(c.getColumnIndex(KEY_OPER_NAME))
                        + "\ntype=" + c.getString(c.getColumnIndex(KEY_OPER_TYPE))
                        + "\nstatus=" + c.getString(c.getColumnIndex(KEY_OPER_STATUS))
                        + "\ncomplete=" + c.getString(c.getColumnIndex(KEY_OPER_COMPLETE))
                        + "\ncurrent=" + c.getString(c.getColumnIndex(KEY_OPER_IS_CURRENT))
                        + "\ndelete=" + c.getString(c.getColumnIndex(KEY_OPER_TO_DELETE))
                        + "\n";
            } while (c.moveToNext() == true);

            activity.log(true, s);
            c.close();
        }
    }

    /**
     * Значение конкретного параметра для указанной операции
     *
     * @param operId        - операция
     * @param parameterName - имя параметра
     * @return - строка-значение параметра, иначе NULL
     */
    String getOperParameter(String operId, String parameterName) {
        activity.log(true, "getOperParameter: operId=" + operId + ", parameterName=" + parameterName + ", table=" + dbHelper.DBRecord[OPER_PARAM][TABLENAME][NONE][NONE]);
        Cursor c = db.query(
                //dbHelper.TABLE_OPER_PARAM,
                dbHelper.DBRecord[OPER_PARAM][TABLENAME][NONE][NONE],
                new String[]{
//                        dbHelper.DBRecord[OPER_PARAM][FIELDINFO][INDEX_OPPA_ID][FIELD_PROP_NAME],
//                        dbHelper.DBRecord[OPER_PARAM][FIELDINFO][INDEX_OPPA_OPER_ID][FIELD_PROP_NAME],
//                        dbHelper.DBRecord[OPER_PARAM][FIELDINFO][INDEX_OPPA_PARAM_NAME][FIELD_PROP_NAME],
//                        dbHelper.DBRecord[OPER_PARAM][FIELDINFO][INDEX_OPPA_PARAM_VALUE][FIELD_PROP_NAME],
//                        dbHelper.DBRecord[OPER_PARAM][FIELDINFO][INDEX_OPPA_TO_DELETE][FIELD_PROP_NAME],
                        KEY_OPPA_ID,
                        KEY_OPPA_OPER_ID,
                        KEY_OPPA_PARAM_NAME,
                        KEY_OPPA_PARAM_VALUE,
                        KEY_OPPA_TO_DELETE
                },
                "(" + KEY_OPPA_OPER_ID + "=?) AND (" + KEY_OPPA_PARAM_NAME + "=?)",
                new String[]{operId, parameterName},
                null, null, null);
        if (c.moveToFirst()) {
            activity.log(true, "get Oper Parameter: name=" +
                    c.getString(c.getColumnIndex(KEY_OPPA_PARAM_NAME)) + ", value=" +
                    c.getString(c.getColumnIndex(KEY_OPPA_PARAM_VALUE))
            );
            return c.getString(c.getColumnIndex(KEY_OPPA_PARAM_VALUE));
        } else {
            return null;
        }
//        c.close();
    }

    void oper1(String id) {
        activity.log(true, "OPER begin");

        String s = "Task:" + id + ")\n";

        Cursor c = db.query(
                dbHelper.TABLE_OPER,
                new String[]{
                        KEY_OPER_ID,
                        KEY_OPER_NAME,
                        KEY_OPER_TYPE,
                        KEY_OPER_STATUS,
                        KEY_OPER_COMPLETE,
                        KEY_OPER_IS_CURRENT,
                        KEY_OPER_TASK_ID,
                        KEY_OPER_TO_DELETE
                },
                "(" + KEY_OPER_TASK_ID + "=?) AND (" + KEY_OPER_COMPLETE + "=?)",
                new String[]{String.valueOf(id), String.valueOf(0)},
                null,
                null,
                null);
        s = s + "Cursor: " + c.getCount() + "\n";
        if (c.moveToFirst()) {
            do {
                s = s +
                        "operId=" + c.getString(c.getColumnIndex(KEY_OPER_ID))
                        + ", task=" + c.getString(c.getColumnIndex(KEY_OPER_TASK_ID))
                        + ", operName=" + c.getString(c.getColumnIndex(KEY_OPER_NAME))
                        + ", operType=" + c.getString(c.getColumnIndex(KEY_OPER_TYPE))
                        + ", operStatus=" + c.getString(c.getColumnIndex(KEY_OPER_STATUS))
                        + ", complete=" + c.getString(c.getColumnIndex(KEY_OPER_COMPLETE))
                        + ", current=" + c.getString(c.getColumnIndex(KEY_OPER_IS_CURRENT))
                        + ", delete=" + c.getString(c.getColumnIndex(KEY_OPER_TO_DELETE))
                        + "\n";
            } while (c.moveToNext() == true);

            activity.log(true, s);
        }
    }

    /**
     * Очистка ОПЕРАЦИЙ
     */
    void clearTask () {
        activity.db.database
                .delete(TABLE_TASK,null,null);
    }

    /**
     * Очистка ОПЕРАЦИЙ
     */
    void clearOper () {
        activity.db.database
                .delete(TABLE_OPER_PARAM,null,null);
        activity.db.database
                .delete(TABLE_OPER,null,null);
    }

    /**
     * Очистка ОПЕРАЦИЙ
     */
    void clearMail () {
        activity.db.database
                .delete(TABLE_MAIL,null,null);
    }

}
