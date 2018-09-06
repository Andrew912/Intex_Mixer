package org.and.intex_v2;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.and.intex_v2.DBHelper.KEY_MAIL_MESSAGE;
import static org.and.intex_v2.DBHelper.KEY_MAIL_RECIPIENT;
import static org.and.intex_v2.DBHelper.KEY_OPER_COMPLETE;
import static org.and.intex_v2.DBHelper.KEY_OPER_ID;
import static org.and.intex_v2.DBHelper.KEY_OPER_IS_CURRENT;
import static org.and.intex_v2.DBHelper.KEY_OPER_NAME;
import static org.and.intex_v2.DBHelper.KEY_OPER_REPORTED;
import static org.and.intex_v2.DBHelper.KEY_OPER_STATUS;
import static org.and.intex_v2.DBHelper.KEY_OPER_TASK_ID;
import static org.and.intex_v2.DBHelper.KEY_OPER_TO_DELETE;
import static org.and.intex_v2.DBHelper.KEY_OPER_TYPE;
import static org.and.intex_v2.DBHelper.KEY_OPPA_OPER_ID;
import static org.and.intex_v2.DBHelper.KEY_OPPA_PARAM_NAME;
import static org.and.intex_v2.DBHelper.KEY_OPPA_PARAM_VALUE;
import static org.and.intex_v2.DBHelper.KEY_TASK_COMMENT;
import static org.and.intex_v2.DBHelper.KEY_TASK_COMPLETE;
import static org.and.intex_v2.DBHelper.KEY_TASK_ID;
import static org.and.intex_v2.DBHelper.KEY_TASK_IS_CURRENT;
import static org.and.intex_v2.DBHelper.KEY_TASK_REPORTED;
import static org.and.intex_v2.DBHelper.KEY_TASK_STATUS;
import static org.and.intex_v2.DBHelper.KEY_TASK_TO_DELETE;

/**
 * Created by Андрей on 14.07.2017.
 */

public class Storer {

    MainActivity
            activity;
    String
            logTAG = "STORER: ";
    String
            weightIndicatorS = "";
    int
            weightCurrent = 0;
    int
            currentWeight = 0;          // Текущее хранимое значение показаний весов
    boolean
            weightIndicatorNow = false;
    int
            weightStart,
            weightLoaded,
            weightTarget,
            weightRemain,
            tolerancePlus,
            toleranceMinus;
    int
            currentTaskNumber = 0;

    /* Конструктор */
    public Storer(MainActivity mainActivity) {
        activity
                = mainActivity;
        mainActivity
                .log("DATABASE: " + activity.dbHandler.database.toString() + ", " + activity.dbHandler.database.getPath());
        currentTaskNumber
                = getRecCount_Task();
    }

    public void storeCurrentWeightToProtocol(String cv) {
        int newWeight
                = Integer.parseInt(cv);

        Log.i("storeToProtocol", "cv=" + cv);

        if (currentWeight != newWeight) {
            currentWeight = newWeight;
            /* Запись в протокол показаний весового терминала */
            sendMessageToControlServer(activity.messengerClass.msg_Protocol(currentWeight, 0, 0));
        }
    }

    /**
     * Количество незавершенных задач в списке
     *
     * @return
     */
    public int getNumberTaskForExecution() {
        Cursor c = activity.dbHandler.database.query(
                activity.dbHelper.TABLE_TASK,
                new String[]{
                        KEY_TASK_ID,
                        KEY_TASK_COMMENT,
                        KEY_TASK_COMPLETE},
                KEY_TASK_COMPLETE + "=?",
                new String[]{String.valueOf(0)},
                null,
                null,
                null);
        activity.log("getNumberTaskForExecution=" + c.getCount());
        return c.getCount();
    }

    /**
     * Количество незавершенных задач в списке
     *
     * @return
     */
    public int getNumberOperForExecution() {
        Cursor c = activity.dbHandler.database.query(
                activity.dbHelper.TABLE_OPER,
                new String[]{
                        KEY_OPER_ID,
                        KEY_OPER_NAME,
                        KEY_OPER_TYPE,
                        KEY_OPER_COMPLETE,
                        KEY_OPER_IS_CURRENT,
                        KEY_OPER_TASK_ID,
                        KEY_OPER_TO_DELETE
                },
                "(" + KEY_OPER_TASK_ID + "=?) AND (" + KEY_OPER_COMPLETE + "=?)",
                new String[]{
                        String.valueOf(activity.currentTask.taskId),
                        String.valueOf(0)},
                null,
                null,
                null);
        activity.log("getNumberOperForExecution=" + c.getCount());
        return c.getCount();
    }

    /**
     * Список операций для выполнения в рамках текущей задачи
     *
     * @return
     */
    public String[] getListOperationsForExecution() {
        activity.log("getListOperationsForExecution");
        Cursor c = activity.dbHandler.database.query(
                activity.dbHelper.TABLE_OPER,
                new String[]{
                        KEY_OPER_ID,
                        KEY_OPER_NAME,
                        KEY_OPER_TYPE,
                        KEY_OPER_COMPLETE,
                        KEY_OPER_IS_CURRENT,
                        KEY_OPER_TASK_ID,
                        KEY_OPER_TO_DELETE
                },
                "(" + KEY_OPER_TASK_ID + "=?) AND (" + KEY_OPER_COMPLETE + "=?)",
                new String[]{
                        String.valueOf(activity.currentTask.taskId),
                        String.valueOf(0)
                },
                null,
                null,
                KEY_OPER_ID + " ASC");
        // Если записей нет, возвращаем НУЛЬ
        //
        if (c.getCount() == 0) {
            return null;
        }
        String[] retVar = new String[c.getCount()];
        c.moveToFirst();

        //
        int i = 0;
        String keyOperName = "";
        String s = "";

        /* *************************************************************************
        /* Здесь задается вормат вывода параметров задачи в строку списка ListView *
         ***************************************************************************/
        do {
            s
                    = "";
            keyOperName =
                    c.getString(c.getColumnIndex(KEY_OPER_TYPE));
            retVar[i] =
                    c.getString(c.getColumnIndex(KEY_OPER_NAME)) + ": ";

            Log.i(logTAG, "KEY_OPER_TYPE = " + keyOperName);

            switch (keyOperName) {
                case "load":
                    Log.i(logTAG, "keyOperName = " + keyOperName);
                    s
                            = s + getOperParam(c.getString(c.getColumnIndex(KEY_OPER_ID)), "feedn") + " ";
                    s
                            = s + getOperParam(c.getString(c.getColumnIndex(KEY_OPER_ID)), "value") + " ";
                    s
                            = s + getOperParam(c.getString(c.getColumnIndex(KEY_OPER_ID)), "unit") + " ";
                    s
                            = s + getOperParam(c.getString(c.getColumnIndex(KEY_OPER_ID)), "servern");
                    break;
                case "move":
                    Log.i(logTAG, "keyOperName = " + keyOperName);
                    s
                            = s + getOperParam(c.getString(c.getColumnIndex(KEY_OPER_ID)), "pointn");
                    break;
                case "deploy":
                    Log.i(logTAG, "keyOperName = " + keyOperName);
                    s
                            = s + getOperParam(c.getString(c.getColumnIndex(KEY_OPER_ID)), "value") + " ";
                    s
                            = s + getOperParam(c.getString(c.getColumnIndex(KEY_OPER_ID)), "unit") + " ";
                    break;
            }

            retVar[i] =
                    retVar[i] +
                            s +
                            " [" +
                            c.getString(c.getColumnIndex(KEY_OPER_ID)) +
                            "]";
            i++;
        } while (c.moveToNext());
        /* *************************************************************************
         *                                                                         *
         ***************************************************************************/

        return retVar;
    }

    /**
     * Получить параметр операции
     *
     * @param operId
     * @param paramName
     * @return
     */
    public String getOperParam(String operId, String paramName) {
        Log.i("getOperParam", "operId=" + operId + ", paramName=" + paramName);
        Cursor c = activity.dbHandler.database.query(
                activity.dbHelper.TABLE_OPER_PARAM,
                new String[]{
                        KEY_OPPA_PARAM_VALUE
                },
                "(" + KEY_OPPA_OPER_ID + "=?) AND (" + KEY_OPPA_PARAM_NAME + "=?)",
                new String[]{operId, paramName},
                null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            return c.getString(c.getColumnIndex(KEY_OPPA_PARAM_VALUE));
        }
        return "";
    }

    /**
     * Список операций для выполнения в рамках текущей задачи
     *
     * @return
     */
    public String[] getFirstOperationForExecution() {
        activity.log("getFirstOperationForExecution");
        Cursor c = activity.dbHandler.database.query(
                activity.dbHelper.TABLE_OPER,
                new String[]{
                        KEY_OPER_ID,
                        KEY_OPER_TYPE,
                        KEY_OPER_NAME,
                        KEY_OPER_TASK_ID,
                        KEY_OPER_STATUS,
                        KEY_OPER_IS_CURRENT,
                        KEY_OPER_COMPLETE,
                        KEY_OPER_REPORTED,
                        KEY_OPER_TO_DELETE},
                "(" + KEY_OPER_TASK_ID + "=?) AND (" + KEY_OPER_COMPLETE + "=?)",
                new String[]{
                        String.valueOf(activity.currentTask.taskId),
                        String.valueOf(0)},
                null,
                null,
                KEY_OPER_ID + " ASC");
        // Если записей нет, возвращаем НУЛЬ
        //
        if (c.getCount() == 0) {
            return null;
        }
        String[] retVar = new String[4];
        c.moveToFirst();
        retVar[0] = c.getString(c.getColumnIndex(KEY_OPER_ID));
        retVar[1] = c.getString(c.getColumnIndex(KEY_OPER_TYPE));
        retVar[2] = c.getString(c.getColumnIndex(KEY_OPER_NAME));
        retVar[3] = c.getString(c.getColumnIndex(KEY_OPER_STATUS));
        return retVar;
    }

    /**
     * Список задач для выполнения
     *
     * @return
     */
    public String[] getListTasksForExecution() {
        Cursor c = activity.dbHandler.database.query(
                activity.dbHelper.TABLE_TASK,
                new String[]{
                        KEY_TASK_ID,
                        KEY_TASK_COMMENT,
                        KEY_TASK_COMPLETE
                },
                KEY_TASK_COMPLETE + "=?",
                new String[]{
                        String.valueOf(0)
                },
                null,
                null,
                null);

        /* Если записей нет, возвращаем НУЛЬ */
        if (c.getCount() == 0) {
            return null;
        }

        /* Заполняем массив */
        String[] retVar = new String[c.getCount()];
        c.moveToFirst();
        int i = 0;

        // Здесь задается вормат вывода параметров задачи в строку списка ListView
        // Далее из этой строки надо вытащить код задачи KEY_TASK_ID
        do {
            retVar[i] = "" +
                    c.getString(c.getColumnIndex(KEY_TASK_COMMENT)) + " [" +
                    c.getString(c.getColumnIndex(KEY_TASK_ID)) + "]";
            i++;
        } while (c.moveToNext());
//        for (i = 0; i < retVar.length; i++) {
//            mainActivity.log("retVar[" + i + "]=" + retVar[i]);
//        }
        return retVar;
    }

    /**
     * Установить текущую активную операцию
     */
    public void setCurrentOper() {
        setOperProperty_Current(activity.currentOper.operId, 1);
        Log.i(logTAG, "mainActivity.currentOper.operId=" + activity.currentOper.operId);
//        takeCurrentTaskData();
    }

    /**
     * Установить текущую активную задачу
     */
    public void setCurrentTask() {
        String taskId = activity.controller.getKeyTaskIdFromListView();
        if (taskId != null) {
            ContentValues newValues = new ContentValues();
            newValues.put(KEY_TASK_IS_CURRENT, String.valueOf(1));
            activity.dbHandler.database.update(activity.dbHelper.TABLE_TASK,
                    newValues,
                    KEY_TASK_ID + "=" + taskId,
                    null);
        } else {
//            mainActivity.log("Achtung! setCurrentTask: parameter \"Current task Id\" is NULL");
        }
        takeCurrentTaskData();
    }

    /**
     * Текущая активная задача
     *
     * @return
     */
    public String getCurrentTask() {
        Cursor c = activity.dbHandler.database.query(
                activity.dbHelper.TABLE_TASK,
                new String[]{
                        KEY_TASK_ID
                },
                "(" + KEY_TASK_IS_CURRENT + "=?) AND (" + KEY_TASK_COMPLETE + "=?)",
                new String[]{String.valueOf(1), String.valueOf(0)},
                null, null, null);
        Log.i(logTAG, "controller.getCount()=" + c.getCount());
        if (c.getCount() == 0) {
            return null;
        } else {
            c.moveToFirst();
            return c.getString(c.getColumnIndex(KEY_TASK_ID));
        }
    }

    /**
     * Текущая активная задача: Data
     *
     * @return
     */

    public String[] takeCurrentTaskData() {
        /* А неплохо бы еще сразу проверить и на наличие текущей активной (приторможенной) операции */
        Cursor c = activity.dbHandler.database.query(
                activity.dbHelper.TABLE_TASK,
                new String[]{
                        KEY_TASK_ID,
                        KEY_TASK_COMMENT,
                        KEY_TASK_STATUS
                },
                "(" + KEY_TASK_IS_CURRENT + "=?) AND (" + KEY_TASK_COMPLETE + "=?)",
                new String[]{String.valueOf(1), String.valueOf(0)},
                null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            return new String[]{
                    c.getString(c.getColumnIndex(KEY_TASK_ID)),
                    c.getString(c.getColumnIndex(KEY_TASK_COMMENT)),
                    c.getString(c.getColumnIndex(KEY_TASK_STATUS))};
        } else {
            return null;
        }
    }

    /**
     * Текущая активная операция
     *
     * @param taskId
     * @return
     */

    public String[] takeCurrentOperData(String taskId) {
        if (taskId == null) {
            return null;
        }
        Cursor c = activity.dbHandler.database.query(
                activity.dbHelper.TABLE_OPER,
                new String[]{
                        KEY_OPER_ID,
                        KEY_OPER_NAME,
                        KEY_OPER_TYPE,
                        KEY_OPER_STATUS},
                "(" + KEY_OPER_IS_CURRENT + "=?) AND (" + KEY_OPER_TASK_ID + "=?)",
                new String[]{String.valueOf(1), taskId},
                null, null, null);
        Log.i(logTAG, "controller.getCount()=" + c.getCount());
        if (c.getCount() > 0) {
            c.moveToFirst();
            return new String[]{
                    c.getString(c.getColumnIndex(KEY_OPER_ID)),
                    c.getString(c.getColumnIndex(KEY_OPER_NAME)),
                    c.getString(c.getColumnIndex(KEY_OPER_TYPE)),
                    c.getString(c.getColumnIndex(KEY_OPER_STATUS))
            };
        } else {
            return null;
        }
    }

    /**
     * Получить данные задачи с указанным ID
     *
     * @param taskId
     * @return
     */
    public String[] getTaskData(String taskId) {
        Cursor c = activity.dbHandler.database.query(
                activity.dbHelper.TABLE_TASK,
                new String[]{
                        KEY_TASK_ID,
                        KEY_TASK_COMMENT,
                        KEY_TASK_STATUS,
                        KEY_TASK_IS_CURRENT,
                        KEY_TASK_COMPLETE,
                        KEY_TASK_REPORTED,
                        KEY_TASK_TO_DELETE},
                KEY_TASK_ID + "=?)",
                new String[]{taskId},
                null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            return new String[]{
                    c.getString(c.getColumnIndex(KEY_TASK_ID)),
                    c.getString(c.getColumnIndex(KEY_TASK_COMMENT)),
                    c.getString(c.getColumnIndex(KEY_TASK_STATUS)),
                    c.getString(c.getColumnIndex(KEY_TASK_IS_CURRENT)),
                    c.getString(c.getColumnIndex(KEY_TASK_COMPLETE)),
                    c.getString(c.getColumnIndex(KEY_TASK_REPORTED)),
                    c.getString(c.getColumnIndex(KEY_TASK_TO_DELETE))};
        } else {
            return null;
        }
    }

    /**
     * Получить данные операции с указанным ID
     *
     * @param operId
     * @return
     */

    public String[] getOperData(String operId) {
        Cursor c = activity.dbHandler.database.query(
                activity.dbHelper.TABLE_OPER,
                new String[]{
                        KEY_OPER_ID,
                        KEY_OPER_TASK_ID,
                        KEY_OPER_TYPE,
                        KEY_OPER_NAME,
                        KEY_OPER_STATUS,
                        KEY_OPER_IS_CURRENT,
                        KEY_OPER_COMPLETE,
                        KEY_OPER_REPORTED,
                        KEY_OPER_TO_DELETE
                },
                KEY_OPER_ID + "=?",
                new String[]{operId},
                null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            return new String[]{
                    c.getString(c.getColumnIndex(KEY_OPER_ID)),
                    c.getString(c.getColumnIndex(KEY_OPER_TASK_ID)),
                    c.getString(c.getColumnIndex(KEY_OPER_TYPE)),
                    c.getString(c.getColumnIndex(KEY_OPER_NAME)).toLowerCase(),
                    c.getString(c.getColumnIndex(KEY_OPER_STATUS)),
                    c.getString(c.getColumnIndex(KEY_OPER_IS_CURRENT)),
                    c.getString(c.getColumnIndex(KEY_OPER_COMPLETE)),
                    c.getString(c.getColumnIndex(KEY_OPER_REPORTED)),
                    c.getString(c.getColumnIndex(KEY_OPER_TO_DELETE))
            };
        } else {
            return null;
        }
    }

    /**
     * Получить ID текущей операции (для заданной задачи)
     *
     * @param taskId
     * @return
     */

    public String getCurrentOperId(String taskId) {
        /*
         * Находит при наличии в БД запись о текущей операции.
         * Ограничение - задача, которой должна принадлежать данная операция.
         */
        Cursor c = activity.dbHandler.database.query(
                activity.dbHelper.TABLE_OPER,
                new String[]{
                        KEY_OPER_ID},
                "(" + KEY_OPER_IS_CURRENT + "=?) AND (" + KEY_OPER_TASK_ID + "=?)",
                new String[]{String.valueOf(1), taskId},
                null, null, null);
        Log.i(logTAG, "controller.getCount()=" + c.getCount());
        if (c.getCount() > 0) {
            c.moveToFirst();
            return c.getString(c.getColumnIndex(KEY_OPER_ID));
        } else {
            return null;
        }
    }

    /**
     * Задача - завершена
     *
     * @param taskId
     */

    public void setTaskProperty_Complete(String taskId) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_TASK_COMPLETE, String.valueOf(1));
        int c = activity.dbHandler.database.update(
                activity.dbHelper.TABLE_TASK,
                cv,
                KEY_TASK_ID + "=?",
                new String[]{taskId}
        );
    }

    /**
     * Задача - запись на удаление
     *
     * @param taskId
     */

    public void setTaskProperty_Delete(String taskId) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_TASK_TO_DELETE, String.valueOf(1));
        int c = activity.dbHandler.database.update(
                activity.dbHelper.TABLE_TASK,
                cv,
                KEY_TASK_ID + "=?",
                new String[]{taskId}
        );
    }

    /**
     * Задача - сформирован отчет
     *
     * @param taskId
     */

    public void setTaskProperty_Reported(String taskId) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_TASK_REPORTED, String.valueOf(1));
        int c = activity.dbHandler.database.update(
                activity.dbHelper.TABLE_TASK,
                cv,
                KEY_TASK_ID + "=?",
                new String[]{taskId}
        );
    }

    /**
     * Задача - установить текущей
     *
     * @param taskId
     * @param value
     */

    public void setTaskProperty_Current(String taskId, int value) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_TASK_IS_CURRENT, String.valueOf(value));
        int c = activity.dbHandler.database.update(
                activity.dbHelper.TABLE_TASK,
                cv,
                KEY_TASK_ID + "=?",
                new String[]{taskId}
        );
    }

    /**
     * Задача - установить статус
     *
     * @param taskId
     * @param status
     */

    public void setTaskProperty_Status(String taskId, String status) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_TASK_STATUS, status);
        int c = activity.dbHandler.database.update(
                activity.dbHelper.TABLE_TASK,
                cv,
                KEY_TASK_ID + "=?",
                new String[]{taskId}
        );
    }

    /**
     * Операция - завершена
     *
     * @param operId
     */
    public void setOperProperty_Complete(String operId) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_OPER_COMPLETE, String.valueOf(1));
        int c = activity.dbHandler.database.update(
                activity.dbHelper.TABLE_OPER,
                cv,
                KEY_TASK_ID + "=?",
                new String[]{operId}
        );
    }

    /**
     * Операция - запись на удаление
     *
     * @param operId
     */
    public void setOperProperty_Delete(String operId) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_OPER_TO_DELETE, String.valueOf(1));
        int c = activity.dbHandler.database.update(
                activity.dbHelper.TABLE_OPER,
                cv,
                KEY_TASK_ID + "=?",
                new String[]{operId}
        );
    }

    /**
     * Операция - сформирован отчет
     *
     * @param operId
     */
    public void setOperProperty_Reported(String operId) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_OPER_REPORTED, String.valueOf(1));
        int c = activity.dbHandler.database.update(activity.dbHelper.TABLE_OPER,
                cv,
                KEY_TASK_ID + "=?",
                new String[]{operId}
        );
    }

    /**
     * Операция - установить текущей
     *
     * @param operId
     * @param value
     */
    public void setOperProperty_Current(String operId, int value) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_OPER_IS_CURRENT, String.valueOf(value));
        int c = activity.dbHandler.database.update(
                activity.dbHelper.TABLE_OPER,
                cv,
                KEY_TASK_ID + "=?",
                new String[]{operId}
        );
    }

    /**
     * Операция - установить статус
     *
     * @param operId
     * @param status
     */
    public void setOperProperty_Status(String operId, String status) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_OPER_STATUS, status);
        int c = activity.dbHandler.database.update(
                activity.dbHelper.TABLE_OPER,
                cv,
                KEY_TASK_ID + "=?",
                new String[]{operId}
        );
    }

    //
    int getRecCount_Task() {
        Cursor cursor = activity.dbHandler.database.rawQuery("select * from task", null);
//        mainActivity.log("TASK rec=" + cursor.getCount());
        return cursor.getCount();
    }

    int getRecCount_Oper() {
        Cursor cursor = activity.dbHandler.database.rawQuery("select * from operation", null);
//        mainActivity.log("OPER rec=" + cursor.getCount());
        return cursor.getCount();
    }

    int getRecCount_OperPar() {
        Cursor cursor = activity.dbHandler.database.rawQuery("select * from oper_param", null);
//        mainActivity.log("OPER_PAR rec=" + cursor.getCount());
        return cursor.getCount();
    }

    /**
     * Запись сообщения для отправки серверу управления
     *
     * @param message
     */
    void sendMessageToControlServer(String message) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_MAIL_RECIPIENT, "CONTROL");
        cv.put(KEY_MAIL_MESSAGE, message);
        activity.dbHandler.database.insert(activity.dbHelper.TABLE_MAIL, null, cv);
    }

    //
    public String extractDigits(String srcString) {
        String r = "0";
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(srcString);
        if (matcher.find()) {
            r = (matcher.group());
            Log.i(logTAG, "!!!! matcher.find()=" + r);
        }
        return r;
    }

    /**
     * Сохранить данные показаний весов
     *
     * @param pWeightIndicatorData
     */
    public void setWeightIndicatorData(String pWeightIndicatorData) {

        boolean badData = (pWeightIndicatorData == null);
        String weightIndicatorData;

        if (badData == false) {
            weightIndicatorData = extractDigits(pWeightIndicatorData);
            badData = weightIndicatorData.equals("no data");
        } else {
            weightIndicatorData = null;
        }

        if (badData == true) {
//            weightCurrent = 0;
            weightIndicatorS = "----";
            weightIndicatorNow = true;
            Log.i("Storer", "weightIndicatorS =" + weightIndicatorS);
            return;
        } else {
            weightIndicatorS = weightIndicatorData;
            weightCurrent = Integer.parseInt(weightIndicatorData);
            weightIndicatorNow = true;
            weightLoaded = weightCurrent - weightStart;
        }
        Log.i("Storer", "weightIndicatorS =" + weightIndicatorS + ", Start=" + weightStart + ", Current=" + weightCurrent);
    }

    /**
     * Жуткий способ очистить ВСЮ базу данных путем перезаписи всех таблиц
     */
    void clearDB() {
        activity.dbHelper.onUpgrade(activity.dbHandler.database, 2, 1);
    }


}
