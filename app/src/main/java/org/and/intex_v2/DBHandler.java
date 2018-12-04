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
import static org.and.intex_v2.DBHelper.FIELD_IS_NAME;
import static org.and.intex_v2.DBHelper.FIELD_IS_TYPE;
import static org.and.intex_v2.DBHelper.FIELD_IS_VALUE;
import static org.and.intex_v2.DBHelper.FIELD_PROP_NAME;
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

        database.execSQL(
                "PRAGMA temp_store = 2;" +
                        "PRAGMA synchronous = OFF;");
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
                activity.dbHandler.database.delete(
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
    public String[] getDeviceAddrfromDB(
            String devNetMask,
            String devName,
            String devNameStartAddr) {

        Log.i(logTag, "=====================================================================");
        Log.i(logTag, "getDeviceAddrfromDB: devNetMask=" + devNetMask + ", devName=" + devName + ", Start=" + devNameStartAddr);

        String[] retVar =
                {
                        devName,
                        devNetMask,
                        null,       // Указывает на наличие текущей записи в таблице OBJECT: null - нет, "found" - есть
                        null
                };

        /* Если стартовый адрес не пустой */
        if (devNameStartAddr != null) {
            retVar[activity.net.NET_DEVICE_ADDR]
                    = retVar[activity.net.NET_DEVICE_ADDR] +
                    activity.extractPatternFromString(devNameStartAddr, "\\d+$");
            return retVar;
        }
        /* Если пустой, то дергаем адрес из параметров */
        else {
            retVar[activity.net.NET_DEVICE_ADDR]
                    = retVar[activity.net.NET_DEVICE_ADDR] +
                    paramGet("MixerTermAddr");
        }

        /*  */
        Log.i(logTag, "Assign values to retVar =============================================");
        Log.i(logTag, "getDeviceAddrfromDB: retVar[0]=" + retVar[activity.net.NET_DEVICE_NAME]);
        Log.i(logTag, "getDeviceAddrfromDB: retVar[1]=" + retVar[activity.net.NET_DEVICE_ADDR]);
        Log.i(logTag, "=====================================================================");

        Cursor c
                = database.query(
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

            /* Если запись есть, то третье значение - не null */
            retVar[2]
                    = "found";
            retVar[activity.net.NET_DEVICE_ADDR]
                    = c.getString(c.getColumnIndex(table_OBJECTS.NETMASK)) +
                    c.getString(c.getColumnIndex(table_OBJECTS.ADDRESS));
            Log.i(logTag,
                    "Data for " + devName + " at " + devNetMask + " FOUND!!!");
        } else {
            Log.i(logTag,
                    "Data for " + devName + " at " + devNetMask + " not found");
        }

        /* Отрезаем лишнее у IP-адреса */
        retVar[activity.net.NET_DEVICE_ADDR]
                = activity.extractPatternFromString(retVar[activity.net.NET_DEVICE_ADDR], "" +
                "");

        Log.i(logTag, "getDeviceAddrfromDB: retVar[0]=" + retVar[activity.net.NET_DEVICE_NAME]);
        Log.i(logTag, "getDeviceAddrfromDB: retVar[1]=" + retVar[activity.net.NET_DEVICE_ADDR]);
        Log.i(logTag, "getDeviceAddrfromDB: retVar[2]=" + retVar[2]);
        Log.i(logTag, "=====================================================================");

        return
                retVar;
    }

    /**
     * Получаем из БД последний сохраненный адрес устройства
     * Добавлено: 19.11.2018
     *
     * @param devNetMask маска
     * @param devName    имя устройства
     * @return полный IP-адрес данного устройства для данной подсети
     */
    public String readDevAddrfromDB(
            String devNetMask,
            String devName) {

        Log.i(logTag, "readDevAddrfromDB: devNetMask=" + devNetMask + ", devName=" + devName);
        String retVar = null;

        Cursor cursor = database.query(
                table_OBJECTS.TABLE_NAME,
                new String[]{
                        table_OBJECTS.NAME,
                        table_OBJECTS.NETMASK,
                        table_OBJECTS.ADDRESS},
                "(" + table_OBJECTS.NETMASK + "=?) AND (" + table_OBJECTS.NAME + "=?)",
                new String[]{devNetMask, devName},
                null, null, null);
        if (cursor.moveToFirst()) {
            retVar = cursor.getString(cursor.getColumnIndex(table_OBJECTS.ADDRESS));
            Log.i(logTag, "Data " + devName + "@" + devNetMask + "=" + retVar);
        } else {
            Log.i(logTag, "Data for " + devName + "@" + devNetMask + " not found");
        }
        return retVar;
    }

    /**
     * Сохраняет данные об адресе устройства в БД
     *
     * @param devNetMask
     * @param devName
     * @param devAddr
     */
    public void saveDevAddrToDB(
            String devNetMask,
            String devName,
            String devAddr) {

        Log.i(logTag, "SAVE: devNetMask=" + devNetMask + ", devName=" + devName + ", devAddr=" + devAddr);

        /* Если адрес устройства в БД для данной подсети не совпадает с заданным, то перезаписать */
        String tryReadAddress = readDevAddrfromDB(devNetMask, devName);
        if (tryReadAddress != null) {
            if (!tryReadAddress.equals(devAddr))
                updateDevAddrInDB(devNetMask, devName, devAddr);
        } else
            appendDevAddrToDB(devNetMask, devName, devAddr);
//        /* Распечатать таблицу OBJECTS */
//        printTableData_OBJECTS();
    }

    /**
     * Добавляет данные об адресе в БД
     *
     * @param devNetMask
     * @param devName
     * @param devAddr
     */
    public void appendDevAddrToDB(
            String devNetMask,
            String devName,
            String devAddr) {

        Log.i(logTag, "APPEND: devNetMask=" + devNetMask + ", devName=" + devName + ", devAddr=" + devAddr);

        ContentValues newValues = new ContentValues();
        newValues.put(table_OBJECTS.NETMASK, devNetMask);
        newValues.put(table_OBJECTS.NAME, devName);
        newValues.put(table_OBJECTS.ADDRESS, devAddr);

        database.insert(
                table_OBJECTS.TABLE_NAME,
                null,
                newValues
        );
    }

    /**
     * Заменяет данные адреса в таблице БД
     *
     * @param devNetMask
     * @param devName
     * @param devAddr
     */
    public void updateDevAddrInDB(
            String devNetMask,
            String devName,
            String devAddr) {

        Log.i(logTag, "UPDATE: mask=" + devNetMask + ", devName=" + devName + "=" + devAddr);

        ContentValues newValues = new ContentValues();
        newValues.put(table_OBJECTS.ADDRESS, devAddr);

        database.update(
                table_OBJECTS.TABLE_NAME,
                newValues,
                "(" + table_OBJECTS.NETMASK + "=?) AND (" + table_OBJECTS.NAME + "=?)",
                new String[]{devNetMask, devName}
        );
//         /* Надо подумать... */
//        paramStore("MixerTermAddr", devAddr, null);
    }

    /**
     * Сохраняет адрес устройства (ключ: Имя + Маска сети -> Адрес)
     *
     * @param devNetMask
     * @param devName
     * @param devAddr
     */
    public void store_Device_Addr_to_DB(String devNetMask, String devName, String devAddr) {
        Log.i("store_Device_Addr_to_DB", "СТАРТ: devAddr=" + devAddr);

        /* НЕ Выделяем последнюю группу цифр из адреса */
        String
                newAddr;
        newAddr
                = extractLastDigitGroup(devAddr);

//        newAddr = devAddr;
//        Log.i("store_Device_Addr_to_DB", "getDevAddrFrimDB = " + getDeviceAddrfromDB(devNetMask, devName, mainActivity.conf.terminalAddress)[1]);

        if (deviceDataNowInDB(devName, devNetMask) > 0) {
            update_Device_Addr_in_DB(devNetMask, devName, newAddr);
        } else {
            append_Device_Addr_in_DB(devNetMask, devName, newAddr);
        }
//        printTableData_OBJECTS();
//        printTableData("OBJECTS");
    }

    /**
     * Пытаемся найти в БД информацию об адресе устройства с заданным именем в указанной подсети.
     * Если такая пара нашлась, то возвращаем идентификатор записи.
     * Если данных нет, возвращаем ноль.
     *
     * @return
     */
    long deviceDataNowInDB(String devName, String devNetMask) {

        Cursor c
                = database.query(
                Column_OBJECTS.TABLE_NAME,
                new String[]{
                        Column_OBJECTS.ID,
                        Column_OBJECTS.NAME,
                        Column_OBJECTS.NETMASK
                },
                "( " +
                        Column_OBJECTS.NAME + "=? AND " +
                        Column_OBJECTS.NETMASK + "=? " +
                        ")",
                new String[]{devName, devNetMask},
                null, null, null);

        /* Если нашли, то возвращаем идентификатор первой записи */
        if (c.moveToFirst())
            return
                    c.getLong(c.getColumnIndex(dbHelper.DBRecord[OBJECTS][FIELDINFO][FIELD_IS_ID][FIELD_PROP_NAME]));

        /* Нет записей, все хорошо */
        return 0;
    }

    /**
     * Пытаемся найти запись в БД с заданными параметрами: Имя устройства, Маска сети, Адрес устройства
     * Если записей нет, возвращается 0.
     * Если запись одна, то ничего не происходит.
     * Если записей больше одной, то все они удаляются и добавляется одна с нужными параметрами.
     *
     * @return
     */
    int readDeviceAddrFromDB(String devNetMask, String devName, String pDevAddr) {
        String devAddr
                = extractLastDigitGroup(pDevAddr);
        Cursor c
                = database.query(
                Column_OBJECTS.TABLE_NAME,
                new String[]{
                        Column_OBJECTS.ID,
                        Column_OBJECTS.NAME,
                        Column_OBJECTS.ADDRESS,
                        Column_OBJECTS.NETMASK
                },
                "( " +
                        Column_OBJECTS.NAME + "=? AND " +
                        Column_OBJECTS.ADDRESS + "=? AND " +
                        Column_OBJECTS.NETMASK + "=? " +
                        ")",
                new String[]{devName, devAddr, devNetMask},
                null, null, null);

        /* Нет записей, все хорошо */
        if (!c.moveToFirst())
            return 0;

        /* Запись одна, все хорошо */
        if (c.getCount() == 1)
            return c.getCount();

        /* Если записей больше, чем одна, надо их удалить и вписать вместо них одну */
        database.execSQL(
                "delete from objects where " +
                        "name=? AND " +
                        "netmask=? AND " +
                        "address=?",
                new String[]{
                        devName, devNetMask, devAddr
                });

        database.execSQL(
                "insert into objects (name,netmask,address) " +
                        "values (" +
                        "?,?,?" +
                        ")",
                new String[]{
                        devName, devNetMask, devAddr
                });

//        Log.i("*** 1",printTableData("objects"));

        /* И повторно считываем количество записей */
        c
                = database.query(
                Column_OBJECTS.TABLE_NAME,
                new String[]{
                        Column_OBJECTS.ID,
                        Column_OBJECTS.NAME,
                        Column_OBJECTS.ADDRESS,
                        Column_OBJECTS.NETMASK
                },
                "( " +
                        Column_OBJECTS.NAME + "=? AND " +
                        Column_OBJECTS.ADDRESS + "=? AND " +
                        Column_OBJECTS.NETMASK + "=? " +
                        ")",
                new String[]{devName, devAddr, devNetMask},
                null, null, null);

//        Log.i("*** 2",printTableData("objects"));

        return c.getCount();
    }

    /**
     * Ищет объект с такими ключами (Маска сети + Имя устройства + Полный адрес)
     *
     * @param devNetMask
     * @param devName
     * @param devAddr
     * @return - количество записей с указанными параметрами
     */
    int numOfRecordsWithThatKeys(String devNetMask, String devName, String devAddr) {

        return 0;
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

        Log.i(logTag, "append_Device_Addr_in_DB: " + devName + "=" + devAddr);

        ContentValues newValues
                = new ContentValues();
        newValues
                .put(table_OBJECTS.NAME, devName);
        newValues
                .put(table_OBJECTS.ADDRESS, devAddr);
        newValues
                .put(table_OBJECTS.NETMASK, devNetMask);

        activity.dbHandler.database.insert(
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
        int
                records;

        /* Попа */
        Log.i(logTag, "update_Device_Addr_in_DB: " + devName + "=" + devAddr + ", mask=" + devNetMask);
//
        ContentValues newValues
                = new ContentValues();
        newValues
                .put(table_OBJECTS.ADDRESS, devAddr);
        records =
                activity.dbHandler.database.update(
                        table_OBJECTS.TABLE_NAME,
                        newValues,
                        "(" + table_OBJECTS.NETMASK + "=?) AND (" + table_OBJECTS.NAME + "=?)",
                        new String[]{devNetMask, devName});

        Log.i(logTag, "update_Device_Addr_in_DB: Store: " + devAddr);
        Log.i(logTag, "update_Device_Addr_in_DB: Updated: " + records + " records");

        /* Надо подумать... */
        paramStore("MixerTermAddr", devAddr, null);
//        Log.i(logTag, "update_Device_Addr_in_DB: Store: " + paramGet("MixerTermAddr"));
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
        activity.dbHandler.database.delete(
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
        String[] retVar
                = {null, null, null};
        Cursor c
                = database.query(
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
    void printTableData_OBJECTS() {
        Cursor cursor
                = activity.dbHandler.database.query(
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
            cursor.moveToFirst();
            recCount = cursor.getCount();
        } catch (Exception e) {
            recCount = 0;
        }
        // Записей явно больше нуля
        Log.i("dbTableList_Objects", "===========================================================================");
        Log.i("dbTableList_Objects", "printTableData_OBJECTS");
        Log.i("dbTableList_Objects", "===========================================================================");
        if (recCount > 0) {
            cursor.moveToFirst();
            do {
                res
                        = new String[]{
                        cursor.getString(cursor.getColumnIndex(dbHelper.DBRecord[OBJECTS][FIELDINFO][FIELD_IS_ID][FIELD_PROP_NAME])),
                        cursor.getString(cursor.getColumnIndex(dbHelper.DBRecord[OBJECTS][FIELDINFO][FIELD_IS_NAME][FIELD_PROP_NAME])),
                        cursor.getString(cursor.getColumnIndex(dbHelper.DBRecord[OBJECTS][FIELDINFO][FIELD_IS_TYPE][FIELD_PROP_NAME])),
                        cursor.getString(cursor.getColumnIndex(dbHelper.DBRecord[OBJECTS][FIELDINFO][3][FIELD_PROP_NAME])),             // Address
                        cursor.getString(cursor.getColumnIndex(dbHelper.DBRecord[OBJECTS][FIELDINFO][4][FIELD_PROP_NAME])),             // Netmask
                        cursor.getString(cursor.getColumnIndex(dbHelper.DBRecord[OBJECTS][FIELDINFO][5][FIELD_PROP_NAME]))              // Port
                };
                Log.i("dbTableList_Objects", "id=" + res[0] + ", name=" + res[1] + ", type=" + res[2] + ", address=" + res[3] + ", netmask=" + res[4] + ", port=" + res[5]);
            } while (cursor.moveToNext());
            Log.i("dbTableList_Objects", "Table " + dbHelper.DBRecord[OBJECTS][TABLENAME][NONE][NONE] + " has " + cursor.getCount() + " records");
        } else {
            Log.i("dbTableList_Objects", "Table " + dbHelper.DBRecord[OBJECTS][TABLENAME][NONE][NONE] + " has no records");
        }
        Log.i("dbTableList_Objects", "===========================================================================");
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
        cursor = activity.dbHandler.database.query(
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
        String[]
                retVar;
        Cursor
                cursor;
        int
                recNo;
        cursor = activity.dbHandler.database.query(
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
            retVar =
                    new String[]{
                            cursor.getString(cursor.getColumnIndex(cParam.VALUE)),
                            cursor.getString(cursor.getColumnIndex(cParam.NAME)),
                            cursor.getString(cursor.getColumnIndex(cParam.TYPE)),
                            cursor.getString(cursor.getColumnIndex(cParam.ID))
                    };
            Log.i("paramGet",
                    "VALUE=" + retVar[0] + ", " +
                            "NAME=" + retVar[1] + ", " +
                            "TYPE=" + retVar[2] + ", " +
                            "ID=" + retVar[3]);
            return
                    retVar;
        } else {
            return null;
        }
    }

    /**
     * Список столбцов таблицы
     *
     * @param tableName
     * @return
     */
    public String[] getTableColumns(String tableName) {
        String[] retVar;
        String query = "pragma table_info('" + tableName + "')";
        Cursor catCursor = activity.dbHandler.database.rawQuery(query, null);
//        Log.i("getTableColumns", "==================");
//        Log.i("getTableColumns", "Table: " + tableName);
//        Log.i("getTableColumns", "==================");
//
        boolean nonStop = false;
        if (catCursor.moveToFirst()) {
            nonStop = true;
        }
        retVar = new String[catCursor.getCount()];
//        Log.i("getTableColumns", "catCursor.getCount() = " + catCursor.getCount() );
        int i = 0;

//        Log.i("getTableColumns", "Columns of table " + tableName);
//        Log.i("getTableColumns", "===========================");
//
        while (nonStop) {
            retVar[i] = catCursor.getString(catCursor.getColumnIndex("name"));
            nonStop = catCursor.moveToNext();
//            Log.i("", retVar[i] + " nonStop=" + nonStop);
            i++;
        }
//        Log.i("getTableColumns", "==================");
//        Log.i("getTableColumns", catCursor.getCount() + " intems total");
        return retVar;
    }

    /**
     * Распечатывает данные таблицы БД
     *
     * @param tableName
     */
    public String tableDataPrint(String tableName) {
        Log.i("printTableData", "** PREPARE to print table " + tableName);
        String delimiter = "==============================";
        String retVar = "\n" + delimiter + "\nTable: " + tableName + "\n" + delimiter + "\n";
        String[] tableColumns = getTableColumns(tableName);

        String query = "select * from " + tableName;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            int i, j;
            j = 1;
            do {
                retVar = retVar + j + ". ";
                i = 0;
                while (i < tableColumns.length) {
                    retVar = retVar +
                            tableColumns[i] + "=[" +
                            cursor.getString(cursor.getColumnIndex(tableColumns[i])) + "] ";
                    i++;
                }
                j++;
                retVar = retVar + "\n";
            } while (cursor.moveToNext());
        }
        retVar = retVar +
                delimiter + "\nTotal " + cursor.getCount() + " records" + "\n" + delimiter;
        return retVar;
    }

    public String printTableData(final String tableName) {
        final String[] resultat = {""};
        Thread thread;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                resultat[0] = tableDataPrint(tableName);
            }
        };
        thread = new Thread(runnable);
        thread.start();
        return resultat[0];
    }

}
