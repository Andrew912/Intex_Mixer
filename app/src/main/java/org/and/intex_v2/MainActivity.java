package org.and.intex_v2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.View.VISIBLE;
import static org.and.intex_v2.DBHelper.KEY_OPPA_OPER_ID;
import static org.and.intex_v2.DBHelper.KEY_OPPA_PARAM_NAME;
import static org.and.intex_v2.DBHelper.KEY_OPPA_PARAM_VALUE;
import static org.and.intex_v2.DBHelper.KEY_OPPA_TO_DELETE;

public class MainActivity extends AppCompatActivity {

    String
            logTAG = "MAIN",
            logMessage;

    // Классы
    String
            ogMessage;
    Context
            context;
    public DBHelper
            dbHelper;
    public DBHandler
            db;
    SQLiteDatabase
            database;
    Controller
            controller;
    Configuration
            conf;
    Configurator
            configurator;
    StatusLine
            statusLine;
    DBFunctions
            dbFunctions;
    CurrentTask
            currentTask;
    CurrentOper
            currentOper;
    Storer
            storer;
    Messenger
            messenger;
    ServerCommincator
            server;
    LoaderCommunicator
            loader;
    NetworkHandler
            net;
    //    TestDataLoader
//            testDataLoader;

    // Лайауты
    LinearLayout layout[];
    LayoutStatusClass layoutStatus;

    // Блок кнопок (L4.1)
    LinearLayout layout_4_1;

    int
            CurrentLayout = 0,                  // Текущий номер экрана
            savedCurrentLayout = 0;             // Сохраненный номер экрана

    // ListView: Выбор задачи из списка: Task Select
    ListView taskSelect_ListView;
    String[] taskSelect_ListItems;
    ArrayAdapter<String> taskSelect_ListAdapter;
    String taskSelect_SelectedValue;                                    // Значение списка при выборе
    TextView textView1;

    // ListView: Выбор задачи из списка: CurrentOper Select
    ListView operSelect_ListView;
    String[] operSelect_ListItems;
    ArrayAdapter<String> operSelect_ListAdapter;
    String operSelect_SelectedValue;                                    // Значение списка при выборе
    TextView textView2;

    // Кнопки
    Button btn_1_Done, btn_ClearDB, btn_ToDB;
    Button btn_0_Back, btn_0_Task, btn_0_Task1, btn_0_Oper, btn_0_SendMail,
            btn_1_Begin,
            btn_2_TaskGet, btn_2_Cancel,
            btn_3_Cancel, btn_3_Continue,
            btn_4_Cancel, btn_4_Accept,
            btn_5_Cancel, btn_5_Accept,
            btn_6_Cancel, btn_6_Complete,
            btn_7_Cancel, btn_7_Complete, btn_7_Start,
            btn_71_Cancel, btn_71_Complete, btn_71_Start,
            btn_8_OK,
            btn_9_Cancel, btn_9_Accept, btn_9_Refresh, btn_9_Reject;

    // Какие-то параметры кнопок
    boolean[] buttonStatus;
    static final int NUMBER_OF_BUTTONS = 4;

    // События нажатия клавиш, списков и пр.
    static final int L__BUTTON_START = 0;
    static final int L0_BUTTON_SENDMAIL = 1001;
    static final int L0_BUTTON_BACK = 1;
    static final int L0_BUTTON_TASK = 2;
    static final int L0_BUTTON_TASK1 = 3;
    static final int L0_BUTTON_OPER = 4;
    static final int L1_BUTTON_TO_DB = 100;
    static final int L1_BUTTON_BEGIN_JOB = 101;
    static final int L2_BUTTON_TASK_SELECT = 200;
    static final int L2_BUTTON_CANCEL = 201;
    static final int L3_BUTTON_TASK_CONTINUE = 300;
    static final int L3_BUTTON_CANCEL = 301;
    static final int L4_BUTTON_CANCEL = 400;
    static final int L4_BUTTON_ACCEPT = 401;
    static final int L4_LIST_TASK_SELECT = 402;
    static final int L5_BUTTON_CANCEL = 500;
    static final int L5_BUTTON_ACCEPT = 501;
    static final int L5_LIST_OPER_SELECT = 502;
    static final int L6_BUTTON_COMPLETE = 600;
    static final int L6_BUTTON_CANCEL = 601;
    static final int L7_BUTTON_COMPLETE = 701;
    static final int L7_BUTTON_CANCEL = 702;
    static final int L7_BUTTON_START = 703;
    static final int L71_BUTTON_COMPLETE = 7101;
    static final int L71_BUTTON_CANCEL = 7102;
    static final int L71_BUTTON_START = 7103;
    static final int L8_BUTTON_OK = 801;
    static final int L9_BUTTON_CANCEL = 901;
    static final int L9_BUTTON_ACCEPT = 902;
    static final int L9_BUTTON_REJECT = 903;
    static final int L9_BUTTON_REFRESH = 904;

    // Ссылки на лайауты для выборки из массива
    static final int LAYOUT_0_DB = 0;
    static final int LAYOUT_1_BEGIN = 1;
    static final int LAYOUT_2_NO_TASK = 2;
    static final int LAYOUT_3_DO_TASK = 3;
    static final int LAYOUT_4_TASK_SELECT = 4;
    static final int LAYOUT_5_OPER_SELECT = 5;
    static final int LAYOUT_6_SIMPLE_OPER = 6;
    static final int LAYOUT_7_COMPLEX_OPER = 7;
    static final int LAYOUT_71_LOAD_OPER = 10;
    static final int LAYOUT_8_TASK_COMPLETE = 8;
    static final int LAYOUT_9_SERV_REQUEST = 9;

    // Текст экрана в лайауте
    TextView textView[];

    // Текст статусной строки
    TextView
            textView_StatusLine;

    // Таймер переключения статусной строки
    Timer
            statusLineOnOffTimer;
    MyTimerTask_StatusLineOnOff
            myTimerTask_statusLineOnOff;        // Задача таймера переключения статусной строки

    // Таймер наблюдения за событиями вывода в статустную строку
    Timer
            statusLineLookOnTimer;
    MyTimerTask_LookAtStatusLine
            myTimerTask_lookAtServerPingClass;  //

    // Таймер задачи наблюдения за поиском сервера в сети
    ArrayList<Timer>
            findServerTimer;
    ArrayList<myTimerTask_WatchOnServerFind>
            myTimerTask_watchOnServerFind;

    int
            statusLineIsVisible;
    public String
            toWriteInStatusLine;                // Для вывода в статусную строку
    public int
            numOfCallStatusLineBlink = 0;       // Кол-во вызовов мигания статусной строки
    public ArrayList<Integer>
            numOfServerPingClasses;             // Количество открытых в данный момент ServerPingClass
    int whatFindCurrent
            = 0;                                // Индекс открытых в данный момент ServerPingClass

    static int                                  // Предельное количество устройств для поиска в сети
            MAX_NUM_OF_DEVICES = 2;

    static int                                  // Индекс ServerPingClass для различных устройств
            DEVICE_IS_TERMINAL = 0,             // Весовой терминал
            DEVICE_IS_LOADER = 1;               // Погрузчик

    /**
     * Переменные, определяющие работу при поиске серверов в сети
     */
    public ArrayList<String[]>
            serverFound;                        // Параметры найденного сервера
    public ArrayList<Boolean>
            endServerFindCondition;            // Условие выхода из цикла при поиске серверов

    // Дополнительный текст
    TextView
            text_7_target, text_71_target;

    // Таймеры
    private Timer
            deviceReadTimer;        // Таймер опроса весового терминала
    private MyTimerTask
            myTimerTask;            // Задача для опроса весового терминала

    /**
     * Конструктор
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /***********************
         * Объявление объектов
         ***********************/
        context = MainActivity.this;
        logTAG = "MAIN: ";
        logMessage = "";
        statusLine
                = new StatusLine();
        configurator
                = new Configurator(this);
        conf
                = new Configuration(this);
        messenger
                = new Messenger(this);
        dbHelper
                = new DBHelper(this.context);
        database
                = dbHelper.getWritableDatabase();
        controller
                = new Controller(this);
//        testDataLoader = new TestDataLoader();
        storer =
                new Storer(this);
        currentTask =
                new CurrentTask(this);
        currentOper =
                new CurrentOper(this);
        dbFunctions =
                new DBFunctions(this);
        server =
                new ServerCommincator(this);
        loader =
                new LoaderCommunicator(this);

        /***********************
         * Лайауты (экраны)
         ***********************/
        layoutStatus = new LayoutStatusClass(Integer.valueOf(getString(R.string.NUMBER_OF_LAYOUTS)));
        layout = new LinearLayout[layoutStatus.numberOfLayouts];
        layout[LAYOUT_0_DB] =
                (LinearLayout) findViewById(R.id.LL0_Dialog);
        layout[LAYOUT_1_BEGIN] =
                (LinearLayout) findViewById(R.id.LL1_Begin);
        layout[LAYOUT_2_NO_TASK] =
                (LinearLayout) findViewById(R.id.LL2_No_Task);
        layout[LAYOUT_3_DO_TASK] =
                (LinearLayout) findViewById(R.id.LL3_Do_Task);
        layout[LAYOUT_4_TASK_SELECT] =
                (LinearLayout) findViewById(R.id.LL4_Task_Select);
        layout[LAYOUT_5_OPER_SELECT] =
                (LinearLayout) findViewById(R.id.LL5_Oper_select);
        layout[LAYOUT_6_SIMPLE_OPER] =
                (LinearLayout) findViewById(R.id.LL6_Simple_Task);
        layout[LAYOUT_7_COMPLEX_OPER] =
                (LinearLayout) findViewById(R.id.LL7_Complex_Task);
        layout[LAYOUT_8_TASK_COMPLETE] =
                (LinearLayout) findViewById(R.id.LL8_Task_Complete);
        layout[LAYOUT_9_SERV_REQUEST] =
                (LinearLayout) findViewById(R.id.LL9_ServiceRequest);
        layout[LAYOUT_71_LOAD_OPER] =
                (LinearLayout) findViewById(R.id.LL71_Load_Task);

        layout_4_1 =
                (LinearLayout) findViewById(R.id.LL4_1_Buttons);

        /***********************
         * Таймеры
         ***********************/
        int
                Layout_0 = 0,                       // Номера экранов
                Layout_1 = 1,
                findServerLayout_900 = 2,           // Экраны поиска сетевых устройств
                findServerLayout_901 = 3,
                findServerLayout_902 = 4;

        int
                CurrentLayout = 0,                  // Текущий номер экрана
                savedCurrentLayout = 0;             // Сохраненный номер экрана

        /***********************
         * TextViews
         ***********************/
        textView = new TextView[layoutStatus.numberOfLayouts];
        textView[LAYOUT_0_DB] =
                (TextView) findViewById(R.id.text_0_Info);
        textView[LAYOUT_1_BEGIN] =
                (TextView) findViewById(R.id.text_1_Info);
        textView[LAYOUT_2_NO_TASK] =
                (TextView) findViewById(R.id.text_2_info);
        textView[LAYOUT_3_DO_TASK] =
                (TextView) findViewById(R.id.text_3_Info);
        textView[LAYOUT_4_TASK_SELECT] =
                (TextView) findViewById(R.id.text_4_Info);
        textView[LAYOUT_5_OPER_SELECT] =
                (TextView) findViewById(R.id.text_5_Info);
        textView[LAYOUT_6_SIMPLE_OPER] =
                (TextView) findViewById(R.id.text_6_info);
        textView[LAYOUT_7_COMPLEX_OPER] =
                (TextView) findViewById(R.id.text_7_info);
        textView[LAYOUT_8_TASK_COMPLETE] =
                (TextView) findViewById(R.id.text_8_info);
        textView[LAYOUT_9_SERV_REQUEST] =
                (TextView) findViewById(R.id.text_9_info);
        textView[LAYOUT_71_LOAD_OPER] =
                (TextView) findViewById(R.id.text_71_info);

        text_7_target = (TextView) findViewById(R.id.text_7_target);
        text_71_target = (TextView) findViewById(R.id.text_71_target);

        /***********************
         * ListView: Task select
         ***********************/
        taskSelect_ListView = (ListView) findViewById(R.id.list_Task_Select);
        taskSelect_SelectedValue = "";
        taskSelect_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textView1 = (TextView) view;
                taskSelect_SelectedValue = textView1.getText().toString();
                statusLine.set(taskSelect_SelectedValue);
                setTextInLayout(LAYOUT_4_TASK_SELECT, taskSelect_SelectedValue);
                controller.controller(L4_LIST_TASK_SELECT);
            }
        });

        /************************
         * ListView: CurrentOper select
         ***********************/
        operSelect_ListView = (ListView) findViewById(R.id.list_Oper_Select);
        operSelect_SelectedValue = "";
        operSelect_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textView2 = (TextView) view;
                operSelect_SelectedValue = textView2.getText().toString();
                statusLine.set(operSelect_SelectedValue);
                setTextInLayout(LAYOUT_5_OPER_SELECT, operSelect_SelectedValue);
                controller.controller(L5_LIST_OPER_SELECT);
            }
        });

        /***********************
         * Кнопки
         ***********************/
        buttonStatus = new boolean[NUMBER_OF_BUTTONS];
        buttonStatusDrop();

        // btn_0_SendMail
        btn_0_SendMail = (Button) findViewById(R.id.button_0_SendMail);
        btn_0_SendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                server.sendMail();
            }
        });

        // Кнопка btn_ToDB
        btn_ToDB = (Button) findViewById(R.id.button_LoadDB);
        btn_ToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L1_BUTTON_TO_DB);
            }
        });

        // Кнопка btn_ClearDB
        btn_ClearDB = (Button) findViewById(R.id.button_Clear_DB);
        btn_ClearDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storer.clearDB();
                controller.controller(L__BUTTON_START);
            }
        });

        // Кнопка btn_0_Back (LL0)
        btn_0_Back = (Button) findViewById(R.id.button_0_Back);         // Response Yes
        btn_0_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L0_BUTTON_BACK);
            }
        });

        // Кнопка "Печать таблицы TASK"
        btn_0_Task = (Button) findViewById(R.id.button_0_Task);           // Response No
        btn_0_Task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L0_BUTTON_TASK);
            }
        });

        // Кнопка "Печать таблицы TASK"
        btn_0_Task1 = (Button) findViewById(R.id.button_0_Task1);           // Response No
        btn_0_Task1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L0_BUTTON_TASK1);
            }
        });

        // Кнопка "Печать таблицы CurrentOper"
        btn_0_Oper = (Button) findViewById(R.id.button_0_Oper);
        btn_0_Oper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L0_BUTTON_OPER);
            }
        });

        // Кнопка btn_1_Begin (LL1)
        btn_1_Begin = (Button) findViewById(R.id.button_1_BeginJob);
        btn_1_Begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L1_BUTTON_BEGIN_JOB);
            }
        });

        // Кнопка button_Done (LL1)
        btn_1_Done = (Button) findViewById(R.id.button_Done);
        btn_1_Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutsVisiblityRestore();
                MainActivity.this.finish();
            }
        });

        // Кнопка btn_2_TaskGet
        btn_2_TaskGet = (Button) findViewById(R.id.button_2_TaskGet);
        btn_2_TaskGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L2_BUTTON_TASK_SELECT);
            }
        });

        // Кнопка btn_2_Cancel
        btn_2_Cancel = (Button) findViewById(R.id.button_2_Cancel);
        btn_2_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L2_BUTTON_CANCEL);
            }
        });

        // Btn_3_Continue
        btn_3_Continue = (Button) findViewById(R.id.button_3_TaskContinue);
        btn_3_Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L3_BUTTON_TASK_CONTINUE);
            }
        });

        // Btn_3_Cancel
        btn_3_Cancel = (Button) findViewById(R.id.button_3_Cancel);
        btn_3_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L3_BUTTON_CANCEL);
            }
        });

        // Кнопка Accept (LL4)
        btn_4_Accept = (Button) findViewById(R.id.button_4_Accept);
        btn_4_Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L4_BUTTON_ACCEPT);
            }
        });

        // Кнопка Cancel (LL4)
        btn_4_Cancel = (Button) findViewById(R.id.button_4_Cancel);
        btn_4_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L4_BUTTON_CANCEL);
            }
        });

        // Кнопка Accept (LL5)
        btn_5_Accept = (Button) findViewById(R.id.button_5_Accept);
        btn_5_Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L5_BUTTON_ACCEPT);
            }
        });

        // Кнопка Cancel (LL5)
        btn_5_Cancel = (Button) findViewById(R.id.button_5_Cancel);
        btn_5_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L5_BUTTON_CANCEL);
            }
        });

        // Кнопка Cancel (LL6)
        btn_6_Cancel = (Button) findViewById(R.id.button_6_Cancel);
        btn_6_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L6_BUTTON_CANCEL);
            }
        });

        // Кнопка Accept (LL6)
        btn_6_Complete = (Button) findViewById(R.id.button_6_Complete);
        btn_6_Complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L6_BUTTON_COMPLETE);
            }
        });

        // btn_7_Complete
        btn_7_Complete = (Button) findViewById(R.id.button_7_Complete);
        btn_7_Complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L7_BUTTON_COMPLETE);
            }
        });

        // btn_7_Start
        btn_7_Start = (Button) findViewById(R.id.button_7_Start);
        btn_7_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L7_BUTTON_START);
            }
        });

        // btn_71_Complete
        btn_71_Complete = (Button) findViewById(R.id.button_71_Complete);
        btn_71_Complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L71_BUTTON_COMPLETE);
            }
        });

        // btn_71_Start
        btn_71_Start = (Button) findViewById(R.id.button_71_Start);
        btn_71_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L71_BUTTON_START);
            }
        });

        // btn_8_OK
        btn_8_OK = (Button) findViewById(R.id.button_8_ok);
        btn_8_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L8_BUTTON_OK);
            }
        });

        // btn_9_Cancel
        btn_9_Cancel = (Button) findViewById(R.id.button_9_Cancel);
        btn_9_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L9_BUTTON_CANCEL);
            }
        });

        // btn_9_Accept
        btn_9_Accept = (Button) findViewById(R.id.button_9_Accept);
        btn_9_Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L9_BUTTON_ACCEPT);
            }
        });

        // btn_9_Reject
        btn_9_Reject = (Button) findViewById(R.id.button_9_Reject);
        btn_9_Reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L9_BUTTON_REJECT);
            }
        });

        // btn_9_Refresh
        btn_9_Refresh = (Button) findViewById(R.id.button_9_Refresh);
        btn_9_Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L9_BUTTON_REFRESH);
            }
        });

        /***********************
         * ЗАПУСК!!!
         ***********************/
//        currentTask
//                .setTaskData();
//        controller
//                .controller(L__BUTTON_START);
        /**
         * Поиск сервера в сети
         */
        String serverToFind
                = "mixerterm.001";
        int whatDevice
                = DEVICE_IS_TERMINAL;

        myTimerTask_watchOnServerFind
                .add(whatDevice, new myTimerTask_WatchOnServerFind(serverToFind, whatDevice));

        // Сохраняем номер экрана, с которого произошел вызов
        savedCurrentLayout
                = CurrentLayout;
        CurrentLayout
                = findServerLayout_900;

        layout[savedCurrentLayout]
                .setVisibility(View.INVISIBLE);
        layout[CurrentLayout]
                .setVisibility(View.VISIBLE);

        toTextView(
                "Find server: " + serverToFind + "@" + conf.networkMask);
        toStatusLineBlink(
                "Find server: " + serverToFind + "@" + conf.networkMask);

        Log.i(logTAG, "serverToFind=" + serverToFind + ", netmask=" + conf.networkMask);

        // Пытаемся опеределить параметры устройства, сохраненные в БД
        String[] terminalAddressFromDB =
                db.get_Device_Addr_from_DB(conf.networkMask, serverToFind);

        Log.i(logTAG, "Find=" + terminalAddressFromDB[0] + ", addr=" + terminalAddressFromDB[1]);

        // Запускаем поиск интересующего нас сервера
        try {
            net.findServerInNetwork(serverToFind, terminalAddressFromDB[1], conf.terminalPort, whatDevice);
        } catch (Exception e) {
            e.printStackTrace();
        }
        findServerTimer.get(whatDevice)
                .schedule(myTimerTask_watchOnServerFind.get(whatDevice), 100, 100);

        /***********************
         * КОНЕЦ
         ***********************/
    }// end of onCreate

    // Сброс статуса кнопок
    void buttonStatusDrop() {
        for (int i = 0; i < buttonStatus.length; i++) {
            buttonStatus[i] = false;
        }
    }

    // Сохраняет текущее состояние видимости в массив объекта
    void layoutsVisiblitySave() {
        for (int i = 0; i < layoutStatus.numberOfLayouts; i++) {
            layoutStatus.status[i] = layout[i].getVisibility() == VISIBLE;
        }
    }

    // Устанавливает состояние видимости в соответствии с массивом
    void layoutsVisiblityRestore() {
        for (int i = 0; i < layoutStatus.numberOfLayouts; i++) {
            if (layoutStatus.getStatus(i) != true) {
                layout[i].setVisibility(View.INVISIBLE);
            } else {
                layout[i].setVisibility(VISIBLE);
            }
        }
    }

    // Устанавливает видимость экрана по номеру
    void layoutVisiblitySet(int layoutToSet) {
        layoutsVisiblitySave();
        for (int i = 0; i < layoutStatus.numberOfLayouts; i++) {
            if (i == layoutToSet) {
                layout[i].setVisibility(View.VISIBLE);
            } else {
                layout[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    // Лог
    public void log(String s) {
        Log.i("MAIN.LOG: ", s);
    }

    void setTextInLayout(int n, String s) {
        textView[n].setText(s);
    }

    // Вычисление значения при выборе ListView Task select
    String getTaskSelect_SelectedValue() {
        return taskSelect_SelectedValue;
    }

    // Параметры текущей задачи
    public class CurrentTask {
        MainActivity a;
        String logTAG;
        boolean now = false;                // Имеется текущая активная задача
        String taskId;                     // Идентификатор задачи
        String taskComment;
        String taskStatus;

        /**
         * При вызове конструктора надо бы загрузить данные из БД о текущей задаче
         */
        public CurrentTask(MainActivity activity) {
            this.a = activity;
            logTAG = a.logTAG + "CurrentTask: Start";
            taskStatus = a.getString(R.string.STATUS_UNDEF);
            setTaskData();
//            operId = storer.getCurrentTask();
//            if (operId != null) {
//                operNow = true;
//            } else {
//                operNow = false;
//            }
        }

        void setTaskData() {
            String[] s = storer.takeCurrentTaskData();
            if (s == null) {
                set(null, null, "undef");
            } else {
                set(s[0], s[1], s[2]);
            }
        }

        public void set(String pId, String pComment, String pStatus) {
            taskStatus = pStatus;
            if (pId == null) {
                now = false;
                taskId = null;
                taskComment = null;
            } else {
                taskId = pId;
                taskComment = pComment;
                now = true;
//                storer.setTaskProperty_Current(operId, 1);
            }
        }

        // Текущая задача начинает/продолжает выполняться
        public void setToActive() {
            String logTAG = a.currentTask.logTAG + "setToActive";
            switch (taskStatus) {
                case "undef":
                    taskStatus = "begin";
                    storer.messageSendTo_CServer(messenger.msg_TaskReport_begin(taskId));
                    Log.i(logTAG, "Status: undef->begin");
                    break;
                case "begin":
                    taskStatus = "begin";
                    //storer.messageSendTo_CServer(messendger.msg_TaskReport_resume(operId));
                    Log.i(logTAG, "Status: begin->begin");
                    break;
                case "end":
                    Log.i(logTAG, "Status: end->");
                    break;
                case "cancel":
                    Log.i(logTAG, "Status: cancel->");
                    break;
                case "suspend":
                    taskStatus = "resume";
                    storer.messageSendTo_CServer(messenger.msg_TaskReport_resume(taskId));
                    Log.i(logTAG, "Status: suspend->resume");
                    break;
                case "resume":
                    taskStatus = "resume";
                    storer.messageSendTo_CServer(messenger.msg_TaskReport_resume(taskId));
                    Log.i(logTAG, "Status: resume->resume");
                    break;
            }
            storer.setTaskProperty_Status(taskId, taskStatus);
        }

        // Текущая задача прерывается/останавливается
        public void setToUnactive() {
            String logTAG = a.currentTask.logTAG + "setToUnActive";
            switch (taskStatus) {
                case "undef":
                    taskStatus = "undef";
//                    storer.messageSendTo_CServer(messendger.msg_TaskReport_suspend(operId));
                    Log.i(logTAG, "Status: undef->undef");
                    break;
                case "begin":
                    taskStatus = "suspend";
                    storer.messageSendTo_CServer(messenger.msg_TaskReport_suspend(taskId));
                    Log.i(logTAG, "Status: begin->suspend");
                    break;
                case "end":
                    Log.i(logTAG, "Status: end->");
                    break;
                case "cancel":
                    Log.i(logTAG, "Status: cancel->");
                    break;
                case "suspend":
                    Log.i(logTAG, "Status: suspend->suspend");
                    break;
                case "resume":
                    taskStatus = "suspend";
                    storer.messageSendTo_CServer(messenger.msg_TaskReport_suspend(taskId));
                    Log.i(logTAG, "Status: resume->suspend");
                    break;
            }
            storer.setTaskProperty_Status(taskId, taskStatus);
        }

        // Текущая задача завершена
        public void setToComplete() {
            String logTAG = a.currentTask.logTAG + "setToUnActive";
            switch (taskStatus) {
                case "undef":
                    taskStatus = "undef";
                    Log.i(logTAG, "Status: undef->undef");
                    break;
                case "begin":
                    taskStatus = "end";
                    storer.messageSendTo_CServer(messenger.msg_TaskReport_end(taskId));
                    Log.i(logTAG, "Status: begin->end");
                    break;
                case "end":
                    Log.i(logTAG, "Status: end->");
                    break;
                case "cancel":
                    Log.i(logTAG, "Status: cancel->");
                    break;
                case "suspend":
                    Log.i(logTAG, "Status: suspend->suspend");
                    break;
                case "resume":
                    taskStatus = "suspend";
                    storer.messageSendTo_CServer(messenger.msg_TaskReport_end(taskId));
                    Log.i(logTAG, "Status: resume->end");
                    break;
            }
            storer.setTaskProperty_Status(taskId, taskStatus);
            storer.setTaskProperty_Complete(a.currentTask.taskId);
            storer.setTaskProperty_Current(a.currentTask.taskId, 0);

        }
    }

    public class CurrentOper {
        /**
         * Описывает операцию - ид, имя и тип (экранное имя)
         */
        MainActivity
                mainActivity;
        String
                logTAG;
        String
                operStatus;
        boolean
                operNow;
        public boolean
                loadNoLoader;                   // Загрузка будет без погрузчика
        String
                operId, operName, operType;
        int
                loadValue;
        ArrayList<OperationParameter>
                operationParameters;

        public CurrentOper(MainActivity activity) {
            this.mainActivity = activity;
            operStatus = mainActivity.getString(R.string.STATUS_UNDEF);
            logTAG = mainActivity.logTAG + "CurrentOper: Щас будет установка setOperData()";
            setOperData();
        }

        void setCurrent() {
            storer.setTaskProperty_Current(operId, 1);
        }

        void setOperData() {
            logTAG = mainActivity.logTAG + "setOperData()";
            String[] s = storer.takeCurrentOperData(currentTask.taskId);
            if (s == null) {
                set(null, null, null, "undef");
            } else {
                set(s[0], s[1], s[2], s[3]);

            }
        }

        /**
         * Операция - погрузка
         *
         * @return
         */
        boolean operIsLoad() {
            operType = operType.toLowerCase();
            Log.i(logTAG, "operIsLoad(): operType=" + operType);
            if (operType.equals(getString(R.string.MSG_COMMAND_IS_LOADING)) == true) {
                // Получить и сохранить значение веса, который необходимо загрузить
                loadValue = (int) Float.parseFloat(getParam("value"));
                return true;
            } else {
                loadValue = 0;
                return false;
            }
        }

        /**
         * Операция - погрузка, но без погрузчика
         *
         * @return
         */
        boolean operIsLoadNoLoader() {
            if (operType.toLowerCase().equals(getString(R.string.MSG_COMMAND_IS_LOADING)) == true) {
                // Получить и сохранить значение веса, который необходимо загрузить
                loadValue = (int) Float.parseFloat(getParam("value"));
                if (getParamFromDB(operId, mainActivity.getString(R.string.MSG_SERVER_ADDR_KEYWORD)) == null) {
                    Log.i(logTAG, "oper Is Load NO LOADER");
                    return true;
                } else {
                    return false;
                }
            } else {
                loadValue = 0;
                return false;
            }
        }

        void set(String pId, String pType, String pName, String pStatus) {
            operStatus = pStatus;
            if (pId == null) {
                operId = null;
                operType = null;
                operNow = false;
            } else {
                operId = pId;
                operType = pType.toLowerCase();
                operName = pName;
                operNow = true;
                setParamInClass();
            }
        }

        /**
         * Устанавливает параметры операции на основании данных в БД
         */
        public void setParamInClass() {
            String s;
            operationParameters = getOperationParameter(operId);
            s = getString(R.string.MESSAGE_COMMAND_NAME);
            operName = getParam0(s, operationParameters);
            s = getString(R.string.MESSAGE_COMMAND_TYPE);
            operType = getParam0(s, operationParameters);
        }

        // Текущая операция начинает/продолжает выполняться
        public void setToActive() {
            String logTAG = mainActivity.currentOper.logTAG + "setToActive";
            switch (operStatus) {
                case "undef":
                    operStatus = "begin";
                    storer.messageSendTo_CServer(messenger.msg_OperReport_begin(operId));
                    Log.i(logTAG, "Status: undef->begin");
                    break;
                case "begin":
                    operStatus = "resume";
                    storer.messageSendTo_CServer(messenger.msg_OperReport_resume(operId));
                    Log.i(logTAG, "Status: begin->resume");
                    break;
                case "end":
                    Log.i(logTAG, "Status: end->");
                    break;
                case "cancel":
                    Log.i(logTAG, "Status: cancel->");
                    break;
                case "suspend":
                    operStatus = "resume";
                    storer.messageSendTo_CServer(messenger.msg_OperReport_resume(operId));
                    Log.i(logTAG, "Status: suspend->resume");
                    break;
                case "resume":
                    Log.i(logTAG, "Status: resume->");
                    break;
            }
            storer.setOperProperty_Status(operId, operStatus);
        }

        // Текущая операция прерывается/останавливается
        public void setToUnactive() {
            String logTAG = mainActivity.currentOper.logTAG + "setToUnactive";
            switch (operStatus) {
                case "undef":
                    Log.i(logTAG, "Status: undef->undef");
                    break;
                case "begin":
                    operStatus = "suspend";
                    storer.messageSendTo_CServer(messenger.msg_OperReport_suspend(operId));
                    Log.i(logTAG, "Status: begin->suspend");
                    break;
                case "end":
                    Log.i(logTAG, "Status: end->");
                    break;
                case "cancel":
                    Log.i(logTAG, "Status: cancel->");
                    break;
                case "suspend":
                    Log.i(logTAG, "Status: suspend->suspend");
                    break;
                case "resume":
                    operStatus = "suspend";
                    storer.messageSendTo_CServer(messenger.msg_OperReport_suspend(operId));
                    Log.i(logTAG, "Status: resume->suspend");
                    break;
            }
            storer.setOperProperty_Status(operId, operStatus);
        }

        // Текущая операция завершается
        public void setToComplete() {
            String logTAG = mainActivity.currentOper.logTAG + "setToComplete";
            switch (operStatus) {
                case "undef":
                    Log.i(logTAG, "Status: undef->");
                    break;
                case "begin":
                    operStatus = "end";
                    storer.messageSendTo_CServer(messenger.msg_OperReport_end(operId));
                    Log.i(logTAG, "Status: begin->end");
                    break;
                case "end":
                    Log.i(logTAG, "Status: end->");
                    break;
                case "cancel":
                    Log.i(logTAG, "Status: cancel->");
                    break;
                case "suspend":
                    Log.i(logTAG, "Status: suspend->");
                    break;
                case "resume":
                    operStatus = "end";
                    storer.messageSendTo_CServer(messenger.msg_OperReport_end(operId));
                    Log.i(logTAG, "Status: resume->end");
                    break;
            }
            storer.setOperProperty_Status(operId, operStatus);
            storer.setOperProperty_Current(operId, 0);
            storer.setOperProperty_Complete(operId);
        }

        // Получение данных о текущей операции
        public String now() {
            return mainActivity.storer.getCurrentOperId(currentTask.taskId);
        }

        // Информация об операции для вывода на экран
        public String getOperationInfoForView() {
            // Тут надо будет поработать с выводом параметров операции
            Log.i(logTAG, "getOperationInfoForView(): START");
            Log.i(logTAG, "getOperationInfoForView(): operType=" + operType);
            String s = "Операция: \n" + operName + "\n";
            switch (operType.toLowerCase()) {
                case "load":
                    s = s
                            + getParam("feedn") + "\n"
                            + getParam("value") + "\n"
                            + getParam("unit") + "\n";
                    break;
                case "move":
                    s = s
                            + getParam("pointn") + "\n";
                    break;
                case "deploy":
                    s = s
                            + getParam("pointn") + "\n"
                            + getParam("value") + "\n"
                            + getParam("unit") + "\n";
                    break;
                case "mix":
                    s = s
                            + getParam("time") + "\n";
                    break;
//            +getParam(getString(R.string.MESSAGE_FEED_NAME_KEYWORD)) + "\n";
            }
            Log.i(logTAG, "getOperationInfoForView(): server=" + s);
            return s;
        }

        // Значение параметра операции по его имени из БД
        String getParamFromDB(String operId, String parameterName) {
            return dbFunctions.getOperParameter(operId, parameterName);
        }

        // Значение параметра операции по его имени
        String getParam(String parameterName) {
            String retVar = null;
            if (operationParameters.size() != 0) {
                for (OperationParameter p : operationParameters) {
                    if (p.name.equals(parameterName)) {
                        retVar = p.value;
                        break;
                    }
                }
            }
            return retVar;
        }

        // Значение параметра операции по его имени
        String getParam0(String pName, ArrayList<OperationParameter> op) {
            String retVar = null;
            if (op.size() != 0) {
                for (int i = 0; i < op.size(); i++) {
                    if (op.get(i).name.equals(pName)) {
                        retVar = op.get(i).value;
                        break;
                    }
                }
//                for (OperationParameter p0 : op) {
//                    if (p0.name.equals(pName)) {
//                        retVar = p0.value;
//                        break;
//                    }
//                }
            }
            return retVar;
        }

        public class OperationParameter {
            String name, value, deleted;

            public OperationParameter() {
                name = "";
                value = "";
                deleted = "";
            }

            public OperationParameter(String pName, String pValue, String pDeleted) {
                name = pName;
                value = pValue;
                deleted = pDeleted;
            }

            public void setParam(String pName, String pValue, String pDeleted) {
                name = pName;
                value = pValue;
                deleted = pDeleted;
            }
        }

        /**
         * Извлекает из БД параметры операции
         *
         * @param pId - идентификатор операции
         * @return список параметров операции
         */
        public ArrayList<OperationParameter> getOperationParameter(String pId) {
            ArrayList<OperationParameter> retVar = new ArrayList<>();
            Cursor c = database.query(
                    DBHelper.TABLE_OPER_PARAM,
                    new String[]{
                            KEY_OPPA_OPER_ID,
                            KEY_OPPA_PARAM_NAME,
                            KEY_OPPA_PARAM_VALUE,
                            KEY_OPPA_TO_DELETE},
                    KEY_OPPA_OPER_ID + "=?",
                    new String[]{pId},
                    null, null, null);
            if (c.getCount() <= 0) {
                return null;
            }
            c.moveToFirst();
            do {
                retVar.add(
                        new OperationParameter(
                                c.getString(c.getColumnIndex(KEY_OPPA_PARAM_NAME)),
                                c.getString(c.getColumnIndex(KEY_OPPA_PARAM_VALUE)),
                                c.getString(c.getColumnIndex(KEY_OPPA_TO_DELETE))));
            } while (c.moveToNext() == true);
            return retVar;
        }
    }

    // Установка текущей операции
    public void setCurrentOper(String operId) {
        Log.i(logTAG, "setCurrentOper: start: " + operId);
        currentOper.setParamInClass();
    }

    // Текущее значение времени
    public Date getCurrentTime() {
        Date cal = Calendar.getInstance().getTime();
        return cal;
    }

    public void makeOperation_Load_with_Loader() {
//        weightDataFromDeviceReader_Start(); // Получение показаний весов от терминала
        weightDataToLoaderSender_Start();   // Передача показаний весов на терминал погрузчика
    }

    public void makeOperation_Load_No_Loader() {
//        weightDataFromDeviceReader_Start(); // Получение показаний весов от терминала
//        weightDataToLoaderSender_Start();   // Передача показаний весов на терминал погрузчика
    }

    public void weightDataToLoaderSender_Start() {
        Log.i(logTAG + ": weightData: ", "start");
    }

    public void weightDataToLoaderSender_Stop() {
        Log.i(logTAG + ": weightData: ", "stop");
        loader.serverSendWeightStop();
    }

    public void weightDataFromDeviceReader_Start() {
        Log.i(logTAG + ": weightData: ", "start");
        deviceReadTimer = new Timer();
        myTimerTask = new MyTimerTask();
        deviceReadTimer.schedule(myTimerTask, 500, 500);
    }

    public void weightDataFromDeviceReader_Stop() {
        Log.i(logTAG + ": weightData: ", "stop");
        if (deviceReadTimer != null) {
            Log.i(logTAG + ": weightData", "timer deviceReadTimer not null");
            deviceReadTimer.purge();
            deviceReadTimer.cancel();
        } else {
            Log.i(logTAG + ": weightData", "timer deviceReadTimer IS NULL");
        }
    }

    // Вывод данных в статусную строку
    public class StatusLine {
        private String statusLine;

        public StatusLine() {
            textView_StatusLine = (TextView) findViewById(R.id.text_StatusLine);
            statusLine = getString(R.string.STATUS_LINE_TEXT_INITIAL);
            textView_StatusLine.setText(statusLine);
        }

        public void set(String pS) {
            textView_StatusLine.setText(pS);
        }
    }

    private class LayoutStatusClass {
        public boolean[] status;
        public int numberOfLayouts;

        public LayoutStatusClass(int LayoutNumberParameter) {

            // Задали количество Л
            numberOfLayouts = LayoutNumberParameter;

            // Объявили массив статусов
            status = new boolean[numberOfLayouts];

            // Установка видимости для всех layout
            for (int i = 0; i < LayoutNumberParameter; i++) {
                status[i] = false;
            }

            // При старте фидим всегда первый Л
            status[1] = true;

        }

        public boolean getStatus(int p) {
            return status[p];
        }
    }

    // Задача для опроса весового терминала
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Socket socket;
            String socketAddr = "192.168.1.113";
            int socketPort = 18080;
            InputStream is;
            OutputStream os;
            String o = "ping\n";
            String res = null;
            try {
                InetAddress serverAddr = InetAddress.getByName(socketAddr);
                socket = new Socket(serverAddr, socketPort);
                if (socket.isConnected() == true) {
                    is = socket.getInputStream();
                    os = socket.getOutputStream();
                    byte[] buffer = o.getBytes();
                    os.write(buffer);
                    os.flush();
                    buffer = new byte[256];
                    int read = is.read(buffer, 0, 256);
                    res = new String(buffer).substring(0, read);
                    Log.i(logTAG, "FROM DEVICE=" + res);
                    res = extractDigits(res);
                    socket.close();
//                    storer.setWeightIndicatorData(res);
                }
            } catch (Exception e) {
                Log.i(logTAG, "Not connected: " + e);
            }
            storer.setWeightIndicatorData(res);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Обновить данные о загрузке на экране
                    displayWeightParameters();
                }
            });

        }
    }

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

    void displayWeightParameters() {

        // Вычислить отставщийся вес
        storer.weightRemain = storer.weightTarget - storer.weightCurrent;

        // ПОказания весов
        text_7_target.setText(String.valueOf(storer.weightCurrent));
        // Остаток для погрузки
        textView[LAYOUT_7_COMPLEX_OPER].setText(String.valueOf(storer.weightRemain));
    }

    void displayWeightParameters1() {

        // Вычислить отставщийся вес
        storer.weightRemain = storer.weightTarget - storer.weightCurrent;

        // ПОказания весов
        text_71_target.setText(String.valueOf(storer.weightCurrent));
        // Остаток для погрузки
        textView[LAYOUT_71_LOAD_OPER].setText(String.valueOf(storer.weightRemain));
    }

    void setTextView(int layout, String text) {
    }

    public void log(boolean needLog, String toLog) {
        if (needLog) {
            logMessage += toLog + "\n";
            Log.i(logTAG, toLog);
        }
    }

    void Beep() {
        /**
         * Звуковой сигнал о запросе на погрузку
         */
        try {
            Uri notify = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notify);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*********************
     * Переключение на слой
     *********************/
    void gotoLayout(int newLayout, String pTextToInfo) {
        String textToInfo = pTextToInfo;
        layoutVisiblitySet(newLayout);                          // Установить видимость слоя
        switch (newLayout) {
            case LAYOUT_0_DB:
                break;

            case LAYOUT_1_BEGIN:

                // Установка кнопок в 7 экране
                btn_7_Start.setVisibility(VISIBLE);
                btn_7_Complete.setVisibility(View.INVISIBLE);

                weightDataFromDeviceReader_Stop();  // Остановить получение показаний весов
                weightDataToLoaderSender_Stop();

                break;

            case LAYOUT_2_NO_TASK:
                break;

            case LAYOUT_3_DO_TASK:
                break;

            case LAYOUT_4_TASK_SELECT:
                if (storer.getNumberTaskForExecution() > 0) {
                    taskSelect_ListItems = storer.getListTasksForExecution();
                    taskSelect_ListAdapter = new ArrayAdapter<String>(
                            context,
                            android.R.layout.simple_list_item_1,
                            taskSelect_ListItems);
                    taskSelect_ListView.setAdapter(taskSelect_ListAdapter);
                }
                break;

            case LAYOUT_5_OPER_SELECT:
                /**
                 * Переход в экран списка операций для данной задачи, подготовка списка операций.
                 * При выборе операции из списка ее параметры отображаются на экране, но
                 * для исполнения всегда выбирается первая из списка операция
                 */
                if (storer.getNumberOperForExecution() > 0) {
                    operSelect_ListItems = storer.getListOperationsForExecution();
                    operSelect_ListAdapter = new ArrayAdapter<String>(
                            context,
                            android.R.layout.simple_list_item_1,
                            operSelect_ListItems);
                    operSelect_ListView.setAdapter(operSelect_ListAdapter);
                    textToInfo = operSelect_ListItems[0];
                }
                break;

            case LAYOUT_6_SIMPLE_OPER:

                break;

            case LAYOUT_7_COMPLEX_OPER:
                loader.serverSendWeight();
                break;

            case LAYOUT_71_LOAD_OPER:

                break;

            case LAYOUT_8_TASK_COMPLETE:

                break;

            case LAYOUT_9_SERV_REQUEST:
                loader.serverServiceRequest();
//                messenger.msg_ToLoader_ServiceRequest();
                break;

        }
        setTextInLayout(newLayout, textToInfo);     // Сообщение в поле инфо
    }

    /**
     * Вывод "тоста"
     *
     * @param whatSay
     */
    public void sayToast(String whatSay) {
        Beep();
        Toast toast = Toast.makeText(getApplicationContext(), whatSay, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Вывод текста в статусную строку: непосредственно установка значения текстовой строки
     *
     * @param message
     */
    public void toStatusLine(String message) {
        textView_StatusLine.setText(message);
    }

    /**
     * Вывод текста из переменной "toWriteInStatusLine" в статусную строку.
     * Функция может быть вызвана из других классов активности и других активностей
     */
    public void writeStringToStatusLine() {
        toStatusLine(toWriteInStatusLine);
    }

    /**
     * Таймер переключения видимости статусной строки
     */
    class MyTimerTask_StatusLineOnOff extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    statusLineSetVisiblity(-1);
                }
            });
        }
    }

    /**
     * Вывод текста в статусную строку с включением мигания
     *
     * @param message
     */
    public void toStatusLineBlink(String message) {
        // Включаем таймер мигания статусной строки только если он еще не включен
        if (numOfCallStatusLineBlink == 0) {
            statusLineOnOffTimer.schedule(myTimerTask_statusLineOnOff, 500, 500);
            numOfCallStatusLineBlink++;
        }
        textView_StatusLine.setText(message);
    }

    /**
     * Вывод текста в статусную строку без мигания
     *
     * @param message
     */
    public void toStatusLineNoBlink(String message) {
        // Если есть таймер мигания, выключаем таймер
        if (statusLineOnOffTimer != null) {
            statusLineOnOffTimer.cancel();
        }
        // Статусная строка - видна
        statusLineSetVisiblity(View.VISIBLE);
        // Скидываем данные в строку
        textView_StatusLine.setText(message);
    }

    /**
     * Вывод в реальном времени значения переменной "toWriteInStatusLine" в статусную строку
     */
    class MyTimerTask_LookAtStatusLine extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    writeStringToStatusLine();
                }
            });
        }
    }

    /**
     * Уствновка видимости статусной строки
     */
    public void statusLineSetVisiblity(int visible) {
        /** Если статус не определен, то просто меняем значение на противоположное
         * иначе присваиваем переменной видимости значение параметра
         */
        if (visible == -1) {
            if (statusLineIsVisible == View.VISIBLE) {
                statusLineIsVisible = View.INVISIBLE;
            } else {
                statusLineIsVisible = View.VISIBLE;
            }
        } else {
            statusLineIsVisible = visible;
        }
        if (statusLineIsVisible == View.VISIBLE) {
            textView_StatusLine.setVisibility(View.VISIBLE);
        } else {
            textView_StatusLine.setVisibility(View.INVISIBLE);
        }
    }

    /**
     *
     */
    class myTimerTask_WatchOnServerFind extends TimerTask {
        String
                serverName;
        int
                serverIndex;

        public myTimerTask_WatchOnServerFind(String pServerName, int pServerIndex) {
            serverName = pServerName;
            serverIndex = pServerIndex;
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sub1(serverName, serverIndex);
                }
            });
        }
    }

    void sub1(String serverToFind, int wishServerIsFind) {
        logTAG = "Find server " + serverToFind;
        /**
         * Условие выхода из цикла:
         * 1. Найден интересующий нас сервер.
         * 2. Закончился пул просматриваемых адресов.
         * 3. Абстрактный тайм-аут.
         */
        // 1. Найден сервер
        if (serverFound.get(wishServerIsFind)[net.SRV_NAME] != null) {
            Log.i(logTAG, "serverFound=" + serverFound.get(wishServerIsFind)[net.SRV_NAME]);
            if (serverFound.get(wishServerIsFind)[net.SRV_NAME].equals(serverToFind)) {
                // Если сервер - тот, который мы ищем
                // Прекратить дальнейший поиск
                Log.i(getClass().getSimpleName(), "serverFound!!!" + serverFound.get(wishServerIsFind)[net.SRV_NAME]);
                endServerFindCondition.set(wishServerIsFind, true);
                // Сохраняем данные в БД
                db.store_Device_Addr_to_DB(
                        conf.networkMask,
                        serverFound.get(wishServerIsFind)[net.SRV_NAME],
                        serverFound.get(wishServerIsFind)[net.SRV_ADDR]
                );
                // Адрес переносим в конфигурацию
                conf.ipAddress = serverFound.get(wishServerIsFind)[net.SRV_ADDR];
            } else {
                // Сервер оказался не тот, который нужен, сбрасываем результат
                serverFound.get(wishServerIsFind)[net.SRV_NAME] = null;
            }
        } else {
            // Сервера пока вообще нет
            // Проверяем, есть ли еще активные процессы срединения с сервером
            if (numOfServerPingClasses.get(wishServerIsFind) > 0) {
                // Пока поиск продолжаем
            } else {
                // Прекратить дальнейший поиск, т.к. все равно больше ничего не найдется
                endServerFindCondition.set(wishServerIsFind, true);
            }
        }
    }

    void toTextView(String textToTextView) {
        textView[CurrentLayout].setText(textToTextView);
    }

    void serverFindResultToStatusLine(String serverFindResult) {
        toStatusLineNoBlink(serverFindResult);
    }

}
