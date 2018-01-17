package org.and.intex_v2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Андрей on 10.07.2017.
 */


public class DBHelper extends SQLiteOpenHelper {

    Context mContext;

    // Тег для вывода отладочной информации в монитор
    String logTag
            = "DBHelper";

    // Данный параметр надо исправить для автоматического обновления структуры БД
    public static final int DATABASE_VERSION
            = 2;

    // Наименование БД
    public static final String DATABASE_NAME
            = "config.db";

    /**
     * Параметры таблиц данных (обратная совместимость)
     */
    // Table "TASK"
    public static final String TABLE_TASK = "task";
    public static final String KEY_TASK_ID = "_id";
    public static final String KEY_TASK_COMMENT = "comment";
    public static final String KEY_TASK_STATUS = "status";
    public static final String KEY_TASK_COMPLETE = "complete";
    public static final String KEY_TASK_TO_DELETE = "to_delete";
    public static final String KEY_TASK_IS_CURRENT = "is_current";
    public static final String KEY_TASK_REPORTED = "reported";

    // Table "OPERATION"
    public static final String TABLE_OPERATION = "operation";
    public static final String KEY_OPER_ID = "_id";
    public static final String KEY_OPER_TASK_ID = "task_id";
    public static final String KEY_OPER_TYPE = "type";
    public static final String KEY_OPER_NAME = "comment";
    public static final String KEY_OPER_STATUS = "status";
    public static final String KEY_OPER_IS_CURRENT = "is_current";
    public static final String KEY_OPER_COMPLETE = "complete";
    public static final String KEY_OPER_REPORTED = "reported";
    public static final String KEY_OPER_TO_DELETE = "to_delete";

    // Table "OPER_PARAM"
    public static final String TABLE_OPER_PARAM = "oper_param";
    public static final String KEY_OPPA_OPER_ID = "oper_id";
    public static final String KEY_OPPA_PARAM_NAME = "param_name";
    public static final String KEY_OPPA_PARAM_VALUE = "param_value";
    public static final String KEY_OPPA_TO_DELETE = "to_delete";

    // Table "MAIL"
    public static final String TABLE_MAIL = "mail";
    public static final String KEY_MAIL_ID = "_id";
    public static final String KEY_MAIL_RECIPIENT = "recipient";
    public static final String KEY_MAIL_MESSAGE = "message";
    public static final String KEY_MAIL_ANSWER = "answer";
    public static final String KEY_MAIL_TIME = "time";
    public static final String KEY_MAIL_REPORTED = "reported";
    public static final String KEY_MAIL_COMPLETE = "complete";
    public static final String KEY_MAIL_TO_DELETE = "to_delete";

    /**
     * Ключ со значением 0
     * Применяется в зависимости от контекста там, где надо подставить 0 в параметре
     */
    public static final int NONE
            = 0;
    public static final int FIRST
            = 0;

    /**
     * Индекс: Имя таблицы в БД
     */
    public static final int PARAMETERS
            = 0;
    public static final int PARAMTYPES
            = 1;
    public static final int OBJECTS
            = 2;
    public static final int OBJTYPES
            = 3;
    public static final int TASK
            = 4;
    public static final int OPERATION
            = 5;
    public static final int OPER_PARAM
            = 6;
    public static final int MAIL
            = 7;

    /**
     * Разделы информации о таблице
     */
    public static final int TABLENAME
            = 0;
    public static final int FIELDINFO
            = 1;
    public static final int TABLEINFO
            = 2;

    /**
     * Выбор строки в FIELDINFO для таблицы PARAMETERS
     * Для других таблиц может быть другим
     */
    public static final int FLD_ID
            = 0;
    public static final int FLD_NAME
            = 1;
    public static final int FLD_TYPE
            = 2;
    public static final int FLD_VALUE
            = 3;

    /**
     * Свойства конкретного поля в FIELDINFO
     */
    public static final int FIELD_PROP_NAME
            = 0;
    public static final int FIELD_PROP_TYPE
            = 1;
    public static final int FIELD_PROP_DEFAULT
            = 2;

    /**
     * Свойства конкретного поля в TABLEINFO
     */
    public static final int TABLE_PROP_NAME
            = 0;
    public static final int TABLE_PROP_VALUE
            = 1;
    public static final int TABLE_PROP_BRACKETS
            = 2;

    // Описание структуры базы данных
    static String DBRecord[][][][] = {
            /** ==============================================================================
             * Чтобы добавить описание новой таблицы, необходимо скопировать структуру от этой
             * метки и до следующей метки в конец описания структуры базы данных и
             * исправить название таблицы и ее параметры.
             * Также необходимо создать класс вида "Table_ИМЯ_ТАБЛИЦЫ_column_names" для
             * хранения наименований полей.
             * */

            /**
             * PARAMETERS - Параметры
            */
            {
                    {
                            /** Имя таблицы
                             * DBRecord[PARAMETERS][TABLENAME][NONE][NONE]
                            */
                            {"PARAMETERS"}
                    },
                    {
                            /** FIELDINFO: Описание полей таблицы
                             * Определить количество записей (полей) можно
                             * DBRecord[PARAMETERS][FIELDINFO].length
                             * Третий параметр - явное задание значения по умолчанию
                            */
                            {"_id", "INTEGER", ""},
                            {"name", "TEXT", "\'\'"},
                            {"type", "TEXT", ""},
                            {"value", "TEXT", ""}
                    },
                    {
                            /** TABLEINFO: Описание связей и ключей таблицы
                             * Определить количество записей:
                             * String.valueOf(DBRecord[PARAMETERS][TABLEINFO].length)
                             * Третий параметр - "1" - необходимо вывести в скобках
                            */
                            {"PRIMARY KEY", "_id", "1"},
                            {"CONSTRAINT", "fk_param_paramtypes", "0"},
                            {"FOREIGN KEY", "type", "1"},
                            {"REFERENCES", "paramtypes", "0"},
                            {"", "_id", "1"}
                    }},
            /**
             * ===============================================================================
             * Это - метка окончания записи об одной таблице в структуре базы данных
             * ===============================================================================
            */

            /**
             * PARAMTYPES - Типы данных параметров
            */
            {
                    {
                            {"PARAMTYPES"}
                    },
                    {
                            {"_id", "INTEGER", ""},
                            {"name", "TEXT", ""}
                    },
                    {
                            {"PRIMARY KEY", "_id", "1"}
                    }},
            /**
             * OBJECTS - Объекты локальной сети
            */
            {
                    {
                            /** Имя таблицы
                             * DBRecord[OBJECTS][TABLENAME][NONE][NONE]
                            */
                            {"OBJECTS"}
                    },
                    {
                            /** FIELDINFO: Описание полей таблицы
                             * Определить количество записей (полей) можно
                             * DBRecord[OBJECTS][FIELDINFO].length
                             * Третий параметр - явное задание значения по умолчанию
                             *
                             * "Сетевой адрес" - адрес, по которому устройство было обнаружено в
                             * последний раз
                            */
                            {"_id", "INTEGER", ""},     // Идентификатор записи
                            {"name", "TEXT", "\'\'"},   // Имя устройства в системе
                            {"type", "TEXT", ""},       // Тип устройства
                            {"address", "TEXT", ""},    // Сетевой адрес
                            {"port", "TEXT", ""}        // Сетевой порт
                    },
                    {
                            /** TABLEINFO: Описание связей и ключей таблицы
                             * Определить количество записей:
                             * String.valueOf(DBRecord[PARAMETERS][TABLEINFO].length)
                             * Третий параметр - "1" - необходимо вывести в скобках
                            */
                            {"PRIMARY KEY", "_id", "1"},
                            {"CONSTRAINT", "fk_param_paramtypes", "0"},
                            {"FOREIGN KEY", "type", "1"},
                            {"REFERENCES", "paramtypes", "0"},
                            {"", "_id", "1"}
                    }},
            /**
             * OBJTYPES - Типы устройств
            */
            {
                    {
                            {"OBJTYPES"}
                    },
                    {
                            {"_id", "INTEGER", ""},         // Идентификатор типа
                            {"name", "TEXT", ""}            // Наименование типа
                    },
                    {
                            {"PRIMARY KEY", "_id", "1"}     // Первичный ключ
                    }},
            /**
             * OPER_PARAM - Параметры операций
            */
            {
                    {
                            {"OPER_PARAM"}
                    },
                    {
                            {"_id", "INTEGER", ""},         // Идентификатор типа
                            {"oper_id", "INTEGER", ""},     // Идентификатор операции
                            {"param_name", "TEXT", ""},     // Название параметра
                            {"param_value", "TEXT", ""},    // Значение параметра
                            {"to_delete", "INTEGER", "0"}   // Пометка для удаления
                    },
                    {
                            {"PRIMARY KEY", "_id", "1"}     // Первичный ключ
                    }},
            /**
             * OPERATION - Операции
            */
            {
                    {
                            {"OPERATION"}
                    },
                    {
                            {"_id", "INTEGER", ""},         // Идентификатор записи
                            {"task_id", "INTEGER", ""},     // Идентификатор операции
                            {"type", "TEXT", ""},           // Кейворд команды операции
                            {"comment", "TEXT", ""},        // Название команды операции
                            {"status", "TEXT", "undef"},    // Текущий статус операции
                            {"complete", "INTEGER", "0"},   // Статус - завершено
                            {"to_delete", "INTEGER", "0"},  // Пометка на удаление
                            {"is_current", "INTEGER", "0"}, // Операция - текущая
                            {"reported", "INTEGER", "0"}    // По данной операции отчитались
                    },
                    {
                            {"PRIMARY KEY", "_id", "1"},
                            {"CONSTRAINT", "fk_operation_oper_param_1", "0"},
                            {"FOREIGN KEY", "_id", "1"},
                            {"REFERENCES", "OPER_PARAM", "0"},
                            {"", "oper_id", "1"}
                    }},
            /**
             * TASK - Задачи
            */
            {
                    {
                            {"TASK"}
                    },
                    {
                            {"_id", "INTEGER", ""},         // Идентификатор записи
                            {"comment", "TEXT", ""},        // Название команды операции
                            {"status", "TEXT", "undef"},    // Текущий статус операции
                            {"complete", "INTEGER", "0"},   // Статус - завершено
                            {"to_delete", "INTEGER", "0"},  // Пометка на удаление
                            {"is_current", "INTEGER", "0"}, // Операция - текущая
                            {"reported", "INTEGER", "0"}    // По данной операции отчитались
                    },
                    {
                            {"PRIMARY KEY", "_id", "1"},
                            {"CONSTRAINT", "fk_task_operation_1", "0"},
                            {"FOREIGN KEY", "_id", "1"},
                            {"REFERENCES", "OPERATION", "0"},
                            {"", "task_id", "1"}
                    }},
            /**
             * MAIL - Данные о выполнении задач для отправки на сервер
            */
            {
                    {
                            {"MAIL"}
                    },
                    {
                            {"_id", "INTEGER", ""},         // Идентификатор записи
                            {"recipient", "TEXT", ""},      // Получатель
                            {"message", "TEXT", ""},        // Текст сообщения
                            {"answer", "TEXT", ""},         // Ответ??
                            {"time", "TEXT", ""},           // Время
                            {"reported", "INTEGER", "0"},   //
                            {"complete", "INTEGER", "0"},   //
                            {"to_delete", "INTEGER", "0"}   // Пометка на удаление
                    },
                    {
                            {"PRIMARY KEY", "_id", "1"}
                    }}
    };


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (int tableNo = DBRecord.length - 1; tableNo >= 0; tableNo--) {
            Log.i(logTag, "Create: " + makeSQLforTableCreate(tableNo));
            db.execSQL(makeSQLforTableCreate(tableNo));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int tableNo = 0; tableNo < DBRecord.length; tableNo++) {
            Log.i(logTag, "Drop: " + makeSQLforTableDrop(tableNo));
            db.execSQL(makeSQLforTableDrop(tableNo));
        }
        onCreate(db);
    }

    /**
     * Формирует строку SQL для удаления таблицы
     *
     * @param tableIndex - номер строки массива DBRecord, в которой содержатся данные о таблице
     * @return строка SQL для удаления таблицы
     */
    String makeSQLforTableDrop(int tableIndex) {
        return
                "drop table if exists " + DBRecord[tableIndex][TABLENAME][NONE][NONE];
    }

    /**
     * Формирует строку SQL для создания таблицы
     *
     * @param tableIndex - номер строки массива DBRecord, в которой содержатся данные о таблице
     * @return строка SQL для создания таблицы
     */
    String makeSQLforTableCreate(int tableIndex) {
        String returnString =
                "CREATE TABLE \"" +
                        DBRecord[tableIndex][TABLENAME][NONE][NONE] + "\" (";

        for (int fieldNo = 0; fieldNo < DBRecord[tableIndex][FIELDINFO].length; fieldNo++) {
            returnString =
                    returnString + "\"" +
                            DBRecord[tableIndex][FIELDINFO][fieldNo][FIELD_PROP_NAME] + "\" " +
                            DBRecord[tableIndex][FIELDINFO][fieldNo][FIELD_PROP_TYPE];

            if (DBRecord[tableIndex][FIELDINFO][fieldNo][FIELD_PROP_DEFAULT].length() > 0) {
                returnString =
                        returnString +
                                " DEFAULT " + DBRecord[tableIndex][FIELDINFO][fieldNo][FIELD_PROP_DEFAULT];
            }
            returnString =
                    returnString + ",";
        }

        /** Проверка количества строк в TABLEINFO
         * Если строка одна, то задаем только PRIMARY KEY
         * Если строк больше, то проверяем и задаем отношения
         */

        if (DBRecord[tableIndex][TABLEINFO].length == 1) {
            returnString =
                    returnString +
                            DBRecord[tableIndex][TABLEINFO][FIRST][TABLE_PROP_NAME] + " " +
                            makeBrackets(
                                    DBRecord[tableIndex][TABLEINFO][FIRST][TABLE_PROP_BRACKETS],
                                    DBRecord[tableIndex][TABLEINFO][FIRST][TABLE_PROP_VALUE]);
        } else {
            for (int fieldNo = 0; fieldNo < DBRecord[tableIndex][TABLEINFO].length; fieldNo++) {
                returnString =
                        returnString +
                                DBRecord[tableIndex][TABLEINFO][fieldNo][TABLE_PROP_NAME] + " " +
                                makeBrackets(
                                        DBRecord[tableIndex][TABLEINFO][fieldNo][TABLE_PROP_BRACKETS],
                                        DBRecord[tableIndex][TABLEINFO][fieldNo][TABLE_PROP_VALUE]);
                if (fieldNo == 0) {
                    returnString = returnString + ",";
                }
            }
        }
        returnString =
                returnString + ");\n";
        return returnString;
    }

    /**
     * Помещает текст в скобки при необходимости
     *
     * @param bracketSign
     * @param valueTo
     * @return
     */
    String makeBrackets(String bracketSign, String valueTo) {
        if (bracketSign.equals("1")) {
            return "(\"" + valueTo + "\") ";
        } else {
            return "\"" + valueTo + "\" ";
        }
    }
}
