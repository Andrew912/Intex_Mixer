package org.and.intex_v2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.and.intex_v2.DBHelper.FIELDINFO;
import static org.and.intex_v2.DBHelper.FIELD_IS_ID;
import static org.and.intex_v2.DBHelper.FIELD_IS_TYPE;
import static org.and.intex_v2.DBHelper.FIELD_PROP_NAME;
import static org.and.intex_v2.DBHelper.FIELD_IS_NAME;
import static org.and.intex_v2.DBHelper.FIELD_IS_VALUE;
import static org.and.intex_v2.DBHelper.NONE;
import static org.and.intex_v2.DBHelper.OBJECTS;
import static org.and.intex_v2.DBHelper.PARAMETERS;
import static org.and.intex_v2.DBHelper.TABLENAME;

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
            database;
    Column_OBJECTS
            cObjects;           // Названия столбцов таблицы OBJECTS
    Column_OBJTYPES
            cObjecttypes;       // Названия столбцов таблицы OBJECTTYPES
    Column_PARAMETERS
            cParam;             // Названия столбцов таблицы PARAMETERS
    Column_PARAMTYPES
            cPtype;             // Названия столбцов таблицы PARAMTYPES

    /* Номер Свойства параметра, которые возвращает paramGet() */
    public static int
            PARAMETER_VALUE = 0,
            PARAMETER_NAME = 1,
            PARAMETER_TYPE = 2,
            PARAMETER_ID = 3;

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
        this.activity
                = mainActivity;
        dbHelper
                = new DBHelper(context);
        database =
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
     * Очистка таблицы объектов
     */
    public void clearTableObjects() {
        Log.i(logTag, "=====================================================================");
        Log.i(logTag, "clearTableObjects. Deleted=" +
                activity.db.database.delete(
                        table_OBJECTS.TABLE_NAME,
                        null,
                        null
                )
        );
    }

    /**
     * Получить адрес и порт указанного устройства
     *
     * @param devName - имя устройства
     * @return String[2]
     * [0] - если найдено, то имя, иначе - null
     * [1] - адрес
     */

    public String[] getDeviceAddrfromDB(String devNetMask, String devName, String devNameStartAddr) {

        Log.i(logTag, "=====================================================================");
        Log.i(logTag, "getDeviceAddrfromDB: devNetMask=" + devNetMask + ", devName=" + devName);

        /*  */
        String[] retVar
                = {devName, devNetMask + devNameStartAddr, null, null};
        Cursor c
                = activity.db.database.query(
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
            Log.i(logTag, "\ngetDeviceAddrfromDB:");
            Log.i(logTag, "========================");
            while (c.moveToNext()) {
                Log.i(logTag,
                        "retVar[0]=" + c.getString(c.getColumnIndex(table_OBJECTS.NAME)) + " " +
                                "retVar[1]=" + c.getString(c.getColumnIndex(table_OBJECTS.ADDRESS)));
            }
        }
        if (c.moveToFirst()) {
            retVar[activity.net.NET_DEVICE_ADDR]
                    = extractPatternFromString(c.getString(c.getColumnIndex(table_OBJECTS.ADDRESS)),"\\d+.\\d+.\\d+.\\d+$");
        }

        /* Отрезаем лишнее у IP-адреса */
        retVar[activity.net.NET_DEVICE_ADDR]
                = extractPatternFromString(retVar[activity.net.NET_DEVICE_ADDR],"\\d+.\\d+.\\d+.\\d+$");

        Log.i(logTag, "getDeviceAddrfromDB: retVar[0]=" + retVar[activity.net.NET_DEVICE_NAME]);
        Log.i(logTag, "getDeviceAddrfromDB: retVar[1]=" + retVar[activity.net.NET_DEVICE_ADDR]);
        Log.i(logTag, "=====================================================================");
        return retVar;
    }

    /**
     * Выделяет из строки подстроку по паттерну
     *
     * @return
     */
    String extractPatternFromString(String sourceAddr, String findPattern) {
        String rv = "";
        Pattern pattern = Pattern.compile(findPattern);
        Matcher m = pattern.matcher(sourceAddr);
        if (m.find()) {
            rv = m.group();
        }
        return rv;
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
        Log.i("store_Device_Addr_to_DB", "СТАРТ: devAddr=" + devAddr);
        /* Выделяем последнюю группу цифр из адреса */
        String newAddr;
        newAddr = extractLastDigitGroup(devAddr);

        if (getDeviceAddrfromDB(devNetMask, devName, activity.conf.terminalAddress)[1] != null) {
            update_Device_Addr_in_DB(devNetMask, devName, newAddr);
        } else {
            append_Device_Addr_in_DB(devNetMask, devName, newAddr);
        }
    }

    /**
     * Выделяет в IP-адресе последнюю группу цифр
     *
     * @return
     */
    String extractLastDigitGroup(String sourceAddr) {
        String rv = "0";
        Pattern pattern = Pattern.compile("\\d+$");
        Matcher m = pattern.matcher(sourceAddr);
        if (m.find()) {
            rv = m.group();
        }
        return rv;
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

        activity.db.database.insert(
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
        /* Попа */
        Log.i(logTag, "update_Device_Addr_in_DB: " + devName + "=" + devAddr);
//
        ContentValues newValues
                = new ContentValues();
        newValues
                .put(table_OBJECTS.ADDRESS, devAddr);
        activity.db.database
                .update(
                        table_OBJECTS.TABLE_NAME,
                        newValues,
                        "(" + table_OBJECTS.NETMASK + "=?) AND (" + table_OBJECTS.NAME + "=?)",
                        new String[]{devNetMask, devName});

        paramStore("MixerTermAddr", devAddr, null);
        Log.i(logTag, "update_Device_Addr_in_DB: Store: " + paramGet("MixerTermAddr"));
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
        activity.db.database.delete(
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
        Cursor c = activity.db.database.query(
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
            retVar[activity.net.NET_DEVICE_NAME] = devName;
            retVar[activity.net.NET_DEVICE_ADDR] = c.getColumnName(c.getColumnIndex(Column_OBJECTS.ADDRESS));
            retVar[activity.net.NET_DEVICE_PORT] = c.getColumnName(c.getColumnIndex(Column_OBJECTS.PORT));
            retVar[activity.net.NET_DEVICE_STAT] = c.getColumnName(c.getColumnIndex(null));
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
     * Сохраняет в базу значение конкретного параметра
     *
     * @param paramName
     * @param paramValue
     */

    public void paramStore(String paramName, String paramValue, String paramType) {
        Log.i("paramStore", "paramName=" + paramName + ", paramValue=" + paramValue);
        // Если паратера в БД нет, то добавляем его
        if (paramNow(paramName) == false) {
            paramAppend(paramName, paramValue, null);
        } else {
            paramUpdate(paramName, paramValue, null);
        }
        // Листинг таблицы PARAMETERS
        dbTableList_Parameters();
    }

    /**
     * Добавляет значение параметра в таблицу
     *
     * @param paramName
     * @param paramValue
     * @param paramType
     */

    public void paramAppend(String paramName, String paramValue, String paramType) {
        ContentValues newValues =
                new ContentValues();
        newValues
                .put(dbHelper.DBRecord[PARAMETERS][FIELDINFO][FIELD_IS_NAME][NONE], paramName);
        newValues
                .put(dbHelper.DBRecord[PARAMETERS][FIELDINFO][FIELD_IS_VALUE][NONE], paramValue);
        database.insert(
                dbHelper.DBRecord[PARAMETERS][TABLENAME][NONE][NONE],
                null,
                newValues
        );
    }

    /**
     * Распечатка значений в таблице PARAMETERS
     */

    void dbTableList_Parameters() {
        Cursor cursor =
                database.query(
                        dbHelper.DBRecord[PARAMETERS][TABLENAME][NONE][NONE],
                        new String[]{
                                dbHelper.DBRecord[PARAMETERS][FIELDINFO][FIELD_IS_ID][FIELD_PROP_NAME],
                                dbHelper.DBRecord[PARAMETERS][FIELDINFO][FIELD_IS_NAME][FIELD_PROP_NAME],
                                dbHelper.DBRecord[PARAMETERS][FIELDINFO][FIELD_IS_TYPE][FIELD_PROP_NAME],
                                dbHelper.DBRecord[PARAMETERS][FIELDINFO][FIELD_IS_VALUE][FIELD_PROP_NAME]
                        },
                        null, null, null, null, null
                );
        int
                recCount;
        String[]
                res;
        try {
            recCount = cursor.getCount();
        } catch (Exception e) {
            recCount = 0;
        }
        // Записей явно больше нуля
        if (recCount > 0) {
            cursor.moveToFirst();
            do {
                res = new String[]{
                        cursor.getString(cursor.getColumnIndex(dbHelper.DBRecord[PARAMETERS][FIELDINFO][FIELD_IS_ID][FIELD_PROP_NAME])),
                        cursor.getString(cursor.getColumnIndex(dbHelper.DBRecord[PARAMETERS][FIELDINFO][FIELD_IS_NAME][FIELD_PROP_NAME])),
                        cursor.getString(cursor.getColumnIndex(dbHelper.DBRecord[PARAMETERS][FIELDINFO][FIELD_IS_TYPE][FIELD_PROP_NAME])),
                        cursor.getString(cursor.getColumnIndex(dbHelper.DBRecord[PARAMETERS][FIELDINFO][FIELD_IS_VALUE][FIELD_PROP_NAME]))
                };
                Log.i("dbTableList_Parameters", "id=" + res[0] + ", name=" + res[1] + ", value=" + res[3]);
            } while (cursor.moveToNext());
            Log.i("dbTableList_Parameters", "Table " + dbHelper.DBRecord[PARAMETERS][TABLENAME][NONE][NONE] + " has " + cursor.getCount() + " records");
        } else {
            Log.i("dbTableList_Parameters", "Table " + dbHelper.DBRecord[PARAMETERS][TABLENAME][NONE][NONE] + " has no records");
        }
    }

    /**
     * Распечатка таблицы OBJECTS
     */

    void dbTableList_OBJECTS() {
        Cursor cursor =
                activity.db.database.query(
                        dbHelper.DBRecord[OBJECTS][TABLENAME][NONE][NONE],
                        new String[]{
                                dbHelper.DBRecord[OBJECTS][FIELDINFO][FIELD_IS_ID][FIELD_PROP_NAME],
                                dbHelper.DBRecord[OBJECTS][FIELDINFO][FIELD_IS_NAME][FIELD_PROP_NAME],
                                dbHelper.DBRecord[OBJECTS][FIELDINFO][FIELD_IS_TYPE][FIELD_PROP_NAME],
                                dbHelper.DBRecord[OBJECTS][FIELDINFO][3][FIELD_PROP_NAME],
                                dbHelper.DBRecord[OBJECTS][FIELDINFO][4][FIELD_PROP_NAME],
                                dbHelper.DBRecord[OBJECTS][FIELDINFO][5][FIELD_PROP_NAME]
                        },
                        null, null, null, null, null
                );
        int
                recCount;
        String[]
                res;
        try {
            recCount = cursor.getCount();
        } catch (Exception e) {
            recCount = 0;
        }
        // Записей явно больше нуля
        Log.i("dbTableList_Parameters", "===========================================================================");
        if (recCount > 0) {
            cursor.moveToFirst();
            do {
                res = new String[]{
                        cursor.getString(cursor.getColumnIndex(dbHelper.DBRecord[OBJECTS][FIELDINFO][FIELD_IS_ID][FIELD_PROP_NAME])),
                        cursor.getString(cursor.getColumnIndex(dbHelper.DBRecord[OBJECTS][FIELDINFO][FIELD_IS_NAME][FIELD_PROP_NAME])),
                        cursor.getString(cursor.getColumnIndex(dbHelper.DBRecord[OBJECTS][FIELDINFO][FIELD_IS_TYPE][FIELD_PROP_NAME])),
                        cursor.getString(cursor.getColumnIndex(dbHelper.DBRecord[OBJECTS][FIELDINFO][3][FIELD_PROP_NAME])),
                        cursor.getString(cursor.getColumnIndex(dbHelper.DBRecord[OBJECTS][FIELDINFO][4][FIELD_PROP_NAME])),
                        cursor.getString(cursor.getColumnIndex(dbHelper.DBRecord[OBJECTS][FIELDINFO][5][FIELD_PROP_NAME]))
                };
                Log.i("dbTableList_Parameters", "id=" + res[0] + ", name=" + res[1] + ", type=" + res[2] + ", address=" + res[3] + ", netmask=" + res[4] + ", port=" + res[5]);
            } while (cursor.moveToNext());
            Log.i("dbTableList_Parameters", "Table " + dbHelper.DBRecord[OBJECTS][TABLENAME][NONE][NONE] + " has " + cursor.getCount() + " records");
        } else {
            Log.i("dbTableList_Parameters", "Table " + dbHelper.DBRecord[OBJECTS][TABLENAME][NONE][NONE] + " has no records");
        }
        Log.i("dbTableList_Parameters", "===========================================================================");
    }

    /**
     * Заменяет значение параметра в таблице
     *
     * @param paramName
     * @param paramValue
     * @param paramType
     */

    public void paramUpdate(String paramName, String paramValue, String paramType) {
        ContentValues newValues =
                new ContentValues();
        newValues
                .put(dbHelper.DBRecord[PARAMETERS][FIELDINFO][FIELD_IS_VALUE][NONE], paramValue);
        database.update(
                dbHelper.DBRecord[PARAMETERS][TABLENAME][NONE][NONE],
                newValues,
                dbHelper.DBRecord[PARAMETERS][FIELDINFO][FIELD_IS_NAME][FIELD_PROP_NAME] + "=?",
                new String[]{paramName}
        );
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
        cursor = activity.db.database.query(
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
     * Получение значения параметра по его имени
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
        cursor = activity.db.database.query(
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
