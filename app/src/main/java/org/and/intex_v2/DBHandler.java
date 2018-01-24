package org.and.intex_v2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Андрей on 19.12.2017.
 */

public class DBHandler {

    String
            logTag = "DBHandler";
    Context
            mContext;
    MainActivity
            activity;
    public DBHelper
            dbHelper;
    public SQLiteDatabase
            db;
    Column_OBJECTS
            cObjects;           // Названия столбцов таблицы OBJECTS
    Column_OBJTYPES
            cObjecttypes;       // Названия столбцов таблицы OBJECTTYPES
    Column_PARAMETERS
            cParam;             // Названия столбцов таблицы PARAMETERS
    Column_PARAMTYPES
            cPtype;             // Названия столбцов таблицы PARAMTYPES

    /**
     * Описание класса, содержащего названия полей для таблицы OBJECTS
     */
    public static class table_OBJECTS {
        public static final String
                TABLE_NAME = "OBJECTS";
        public static final String
                ID = "_id";
        public static final String
                NAME = "name";
        public static final String
                TYPE = "type";
        public static final String
                ADDRESS = "address";
        public static final String
                NETMASK = "netmask";
        public static final String
                PORT = "port";
    }

    /**
     * Описание класса, содержащего названия полей для таблицы OBJECTTYPES
     */
    public static class Column_OBJTYPES {
        public static final String
                TABLE_NAME = "OBJTYPES";
        public static final String
                ID = "_id";
        public static final String
                NAME = "name";
    }

    /**
     * Конструктор
     *
     * @param context
     */
    public DBHandler(MainActivity mainActivity, Context context) {
        this.activity = mainActivity;
        dbHelper = new DBHelper(context);

        db =
                dbHelper.getWritableDatabase();
        cObjects =
                new Column_OBJECTS();
        cObjecttypes =
                new Column_OBJTYPES();
        cParam =
                new Column_PARAMETERS();
        cPtype =
                new Column_PARAMTYPES();
    }

    /**
     * Описание класса, содержащего названия полей для таблицы OBJECTS
     */
    public static class Column_OBJECTS {
        public static final String
                TABLE_NAME = "OBJECTS";
        public static final String
                ID = "_id";
        public static final String
                NAME = "name";
        public static final String
                TYPE = "type";
        public static final String
                ADDRESS = "address";
        public static final String
                NETMASK = "netmask";
        public static final String
                PORT = "port";
    }

    /**
     * Описание класса, содержащего названия полей для таблицы PARAMETERS
     */
    public static class Column_PARAMETERS {
        public static final String
                TABLE_NAME = "PARAMETERS";
        public static final String
                ID = "_id";
        public static final String
                NAME = "name";
        public static final String
                TYPE = "type";
        public static final String
                VALUE = "value";
    }

    /**
     * Описание класса, содержащего названия полей для таблицы PARAMTYPES
     */
    public static class Column_PARAMTYPES {
        public static final String
                TABLE_NAME = "PARAMTYPES";
        public static final String
                ID = "_id";
        public static final String
                NAME = "name";
    }

    /**
     * Получить адрес и порт указанного устройства
     *
     * @param devName - имя устройства
     * @return String[2]
     * [0] - если найдено, то имя, иначе - null
     * [1] - адрес
     */
    public String[] get_Device_Addr_from_DB(String devNetMask, String devName) {
//
//        Log.i(logTag, "get_Device_Addr_from_DB");
//
        String[] retVar = {null, null};
        Cursor c = db.query(
                table_OBJECTS.TABLE_NAME,
                new String[]{
                        table_OBJECTS.ID,
                        table_OBJECTS.NAME,
                        table_OBJECTS.ADDRESS,
                        table_OBJECTS.NETMASK
                },
                "(" + table_OBJECTS.NETMASK + "=?) AND (" + table_OBJECTS.NAME + "=?)",
                new String[]{devNetMask, devName},
                null, null, null);
        if (c.moveToFirst()) {
            retVar[activity.net.SRV_NAME] = devName;
            retVar[activity.net.SRV_ADDR] = c.getString(c.getColumnIndex(table_OBJECTS.ADDRESS));
        }
        return retVar;
    }

    /**
     * Сохраняет текущий адрес устройства в БД.
     * Если он есть - заменяет адрес,
     * Если нет - добавляет адрес с таблицу.
     *
     * @param devNetMask
     * @param devName
     * @param devAddr
     */
    public void store_Device_Addr_to_DB(String devNetMask, String devName, String devAddr) {
        if (get_Device_Addr_from_DB(devNetMask, devName)[1] != null) {
            update_Device_Addr_in_DB(devNetMask, devName, devAddr);
        } else {
            append_Device_Addr_in_DB(devNetMask, devName, devAddr);
        }
    }

    /**
     * Сохраняет адрес и порт указанного устройства в новой записи
     *
     * @param devName
     * @param devAddr
     */
    public void append_Device_Addr_in_DB(String devNetMask, String devName, String devAddr) {
//
        Log.i(logTag, "append_Device_Addr_in_DB: " + devName + "=" + devAddr);
//
        ContentValues newValues = new ContentValues();
        newValues.put(table_OBJECTS.NAME, devName);
        newValues.put(table_OBJECTS.ADDRESS, devAddr);
        newValues.put(table_OBJECTS.NETMASK, devNetMask);

        db.insert(
                table_OBJECTS.TABLE_NAME,
                null,
                newValues
        );
    }

    /**
     * Замена значения адреса устройства в существующей записи
     *
     * @param devName
     * @param devAddr
     */
    public void update_Device_Addr_in_DB(String devNetMask, String devName, String devAddr) {
//
        Log.i(logTag, "update_Device_Addr_in_DB: " + devName + "=" + devAddr);
//
        ContentValues newValues = new ContentValues();
        newValues.put(table_OBJECTS.ADDRESS, devAddr);

        db.update(
                table_OBJECTS.TABLE_NAME,
                newValues,
                "(" + table_OBJECTS.NETMASK + "=?) AND (" + table_OBJECTS.NAME + "=?)",
                new String[]{devNetMask, devName});

    }

    /**
     * Удаление записи об адресе устройства
     *
     * @param devNetMask
     * @param devName
     */
    public void delete_Device_Addr_in_DB(String devNetMask, String devName) {
//
        Log.i(logTag, "delete_Device_Addr_in_DB: " + devName);
//
        db.delete(
                table_OBJECTS.TABLE_NAME,
                "(" + table_OBJECTS.NETMASK + "=?) AND (" + table_OBJECTS.NAME + "=?)",
                new String[]{devNetMask, devName});
    }



    /**
     * Получить адрес и порт указанного устройства
     *
     * @param devName - имя устройства
     * @return String[2]
     * [0] - если найдено, то адрес, иначе - null
     * [1] - порт
     */
    public String[] get_Device_Addr_n_Port(String devNetMask, String devName) {
        String[] retVar = {null, null, null};
        Cursor c = db.query(
                Column_OBJECTS.TABLE_NAME,
                new String[]{
                        Column_OBJECTS.ID,
                        Column_OBJECTS.NAME,
                        Column_OBJECTS.ADDRESS,
                        Column_OBJECTS.PORT
                },
                "(" + Column_OBJECTS.NAME + "=?)",
                new String[]{devName},
                null, null, null);
        if (c.moveToFirst()) {
            retVar[activity.net.SRV_NAME] = devName;
            retVar[activity.net.SRV_ADDR] = c.getColumnName(c.getColumnIndex(Column_OBJECTS.ADDRESS));
            retVar[activity.net.SRV_PORT] = c.getColumnName(c.getColumnIndex(Column_OBJECTS.PORT));
        }
        return retVar;
    }

    /**
     * Сохраняет
     *
     * @param devName
     * @param devAddr
     * @param devPort
     */
    public void store_Device_Addr_n_Port(String devName, String devAddr, String devPort) {

    }

    /**
     * @param devName
     * @param devAddr
     * @param devPort
     */
    public void update_Device_Addr_n_Port(String devName, String devAddr, String devPort) {

    }

    /**
     * Наличие параметра с таким именем
     *
     * @param paramName - имя параметра
     * @return - параметр существует
     */
    public boolean paramNow(String paramName) {
        Cursor
                cursor;
        int
                recNo;
        cursor = db.query(
                cParam.TABLE_NAME,
                new String[]{cParam.NAME},
                cParam.NAME + "=?",
                new String[]{paramName},
                null, null, null
        );
        try {
            recNo = cursor.getCount();
        } catch (Exception e) {
            recNo = 0;
        }
        if (recNo > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Значение параметра по его имени
     *
     * @param paramName
     * @return String[]
     * Возвращает массив строк:
     * 0 - значение параметра
     * 1 - имя параметра
     * 2 - тип данных параметра
     * 3 - _id записи таблицы
     */
    public String[] paramGet(String paramName) {
        Cursor
                cursor;
        int
                recNo;
        cursor = db.query(
                cParam.TABLE_NAME,
                new String[]{
                        cParam.ID,
                        cParam.NAME,
                        cParam.TYPE,
                        cParam.VALUE},
                cParam.NAME + "=?",
                new String[]{paramName},
                null, null, null
        );
        try {
            recNo = cursor.getCount();
        } catch (Exception e) {
            recNo = 0;
        }
        if (recNo > 0) {
            cursor.moveToFirst();
            return
                    new String[]{
                            cursor.getString(cursor.getColumnIndex(cParam.VALUE)),
                            cursor.getString(cursor.getColumnIndex(cParam.NAME)),
                            cursor.getString(cursor.getColumnIndex(cParam.TYPE)),
                            cursor.getString(cursor.getColumnIndex(cParam.ID)),
                    };
        } else {
            return null;
        }
    }


}
