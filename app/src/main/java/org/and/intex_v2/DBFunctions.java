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
import static org.and.intex_v2.DBHelper.KEY_TASK_COMMENT;
import static org.and.intex_v2.DBHelper.KEY_TASK_COMPLETE;
import static org.and.intex_v2.DBHelper.KEY_TASK_ID;
import static org.and.intex_v2.DBHelper.KEY_TASK_IS_CURRENT;
import static org.and.intex_v2.DBHelper.KEY_TASK_STATUS;
import static org.and.intex_v2.DBHelper.KEY_TASK_TO_DELETE;


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

    void task() {
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
                null, null, null, null, null);
        s = s + "Cursor: " + c.getCount() + "\n";

        if (c.moveToFirst()) {
//        if (controller.getCount()>0) {
//            controller.moveToFirst();
            do {
                s = s +
                        "operId=" + c.getString(c.getColumnIndex(KEY_TASK_ID))
                        + ", taskComment=" + c.getString(c.getColumnIndex(KEY_TASK_COMMENT))
                        + ", current=" + c.getString(c.getColumnIndex(KEY_TASK_IS_CURRENT))
                        + ", operStatus=" + c.getString(c.getColumnIndex(KEY_TASK_STATUS))
                        + ", complete=" + c.getString(c.getColumnIndex(KEY_TASK_COMPLETE))
                        + ", delete=" + c.getString(c.getColumnIndex(KEY_TASK_TO_DELETE))
                        + "\n";
            } while (c.moveToNext() == true);

            activity.log(s);
            activity.textView[0].setText(s);
            c.close();
        }
    }

    void task1() {
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
        s = s + "Cursor: " + c.getCount() + "\n";
        if (c.moveToFirst()) {
            do {
                s = s +
                        "operId=" + c.getString(c.getColumnIndex(KEY_TASK_ID))
                        + ", taskComment=" + c.getString(c.getColumnIndex(KEY_TASK_COMMENT))
                        + ", current=" + c.getString(c.getColumnIndex(KEY_TASK_IS_CURRENT))
                        + ", operStatus=" + c.getString(c.getColumnIndex(KEY_TASK_STATUS))
                        + ", complete=" + c.getString(c.getColumnIndex(KEY_TASK_COMPLETE))
                        + ", delete=" + c.getString(c.getColumnIndex(KEY_TASK_TO_DELETE))
                        + "\n";
            } while (c.moveToNext() == true);

            activity.log(s);
            activity.textView[0].setText(s);
        }
    }

    void oper() {
        activity.log("OPER begin");
        String s = "Task:" + activity.currentTask.taskId + " (" + activity.currentTask.taskComment + ")\n";
        Cursor c = db.query(
                dbHelper.TABLE_OPERATION,
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
                null,
                null,
                null,
                null,
                null);
        s = s + "Cursor: " + c.getCount() + "\n";
        if (c.moveToFirst()) {
            do {
                s = s +
                        "operId=" + c.getString(c.getColumnIndex(KEY_OPER_ID))
                        + ", task=" + c.getString(c.getColumnIndex(KEY_OPER_TASK_ID))
                        + ", comment=" + c.getString(c.getColumnIndex(KEY_OPER_NAME))
                        + ", type=" + c.getString(c.getColumnIndex(KEY_OPER_TYPE))
                        + ", status=" + c.getString(c.getColumnIndex(KEY_OPER_STATUS))
                        + ", complete=" + c.getString(c.getColumnIndex(KEY_OPER_COMPLETE))
                        + ", current=" + c.getString(c.getColumnIndex(KEY_OPER_IS_CURRENT))
                        + ", delete=" + c.getString(c.getColumnIndex(KEY_OPER_TO_DELETE))
                        + "\n";
            } while (c.moveToNext() == true);

            activity.log(s);
            activity.textView[0].setText(s);
            c.close();
        }
    }

    void mail() {
        activity.log("MAIL begin");
        String s = "";
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
                null,
                null,
                null, null, null);
        s = s + "Cursor: " + c.getCount() + "\n";
        if (c.moveToFirst()) {
            do {
                s =
                        "operId=" + c.getString(c.getColumnIndex(KEY_MAIL_ID))
                                + ", recipient=" + c.getString(c.getColumnIndex(KEY_MAIL_RECIPIENT))
                                + ", message=" + c.getString(c.getColumnIndex(KEY_MAIL_MESSAGE))
                                + ", comment=" + c.getString(c.getColumnIndex(KEY_MAIL_TIME))
                                + ", answer=" + c.getString(c.getColumnIndex(KEY_MAIL_ANSWER))
                                + ", report=" + c.getString(c.getColumnIndex(KEY_MAIL_REPORTED))
                                + ", complete=" + c.getString(c.getColumnIndex(KEY_MAIL_COMPLETE))
                                + ", delete=" + c.getString(c.getColumnIndex(KEY_MAIL_TO_DELETE))
                                + "\n";
                activity.log(s);
            } while (c.moveToNext() == true);


//            mainActivity.textView[0].setText(s);
            c.close();
        }
    }

    void taskClear() {
        activity.log(true, "TASK clear begin");
        db.delete(dbHelper.TABLE_TASK, null, null);
    }

    void operClear() {
        activity.log(true, "OPER clear begin");
        db.delete(dbHelper.TABLE_OPERATION, null, null);
        db.delete(dbHelper.TABLE_OPER_PARAM, null, null);
    }

    void operList() {
        activity.log(true, "OPER begin");
        String s = "Task: ALL\n";
        Cursor c = db.query(
                dbHelper.TABLE_OPERATION,
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
                null,
                null,
                null);
        s = s + "Cursor: " + c.getCount() + "\n";
        if (c.moveToFirst()) {
            do {
                s = s +
                        "operId=" + c.getString(c.getColumnIndex(KEY_OPER_ID))
                        + ", task=" + c.getString(c.getColumnIndex(KEY_OPER_TASK_ID))
                        + ", comment=" + c.getString(c.getColumnIndex(KEY_OPER_NAME))
                        + ", type=" + c.getString(c.getColumnIndex(KEY_OPER_TYPE))
                        + ", status=" + c.getString(c.getColumnIndex(KEY_OPER_STATUS))
                        + ", complete=" + c.getString(c.getColumnIndex(KEY_OPER_COMPLETE))
                        + ", current=" + c.getString(c.getColumnIndex(KEY_OPER_IS_CURRENT))
                        + ", delete=" + c.getString(c.getColumnIndex(KEY_OPER_TO_DELETE))
                        + "\n";
            } while (c.moveToNext() == true);

            activity.log(true, s);
            c.close();
        }
    }

    void oper1(String id) {
        activity.log(true, "OPER begin");

        String s = "Task:" + id + ")\n";

        Cursor c = db.query(
                dbHelper.TABLE_OPERATION,
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

    void mailClear() {
        activity.log(true, "MAIL clear begin");
        db.delete(dbHelper.TABLE_MAIL, null, null);
    }

}
