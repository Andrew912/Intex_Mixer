package org.and.intex_v2;

import android.content.Context;
import android.database.Cursor;
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
import android.widget.EditText;
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
import static org.and.intex_v2.NetworkHandler.NET_DEVICE_NAME;
import static org.and.intex_v2.NetworkHandler.NET_DEVICE_NOW;

public class MainActivity extends AppCompatActivity {
    /* Паараметры лога */
    String
            logTAG = "MAIN",
            logMessage;

    /* Классы */
    Context
            context;
    DBHelper
            dbHelper;
    static DBHandler
            dbHandler;
    Controller
            controller;
    Configurator
            conf;
    StatusLine
            statusLine;
    static DBFunctions
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
    ServerFindControlClass
            sfc;
    static SendMailToControlServer
            sendMailToControlServer;
    static MailToSend
            mailToSend;
//    GPSTracker
//            gps;

    /* Лайауты */
    LinearLayout[]
            layout;

    LayoutStatusClass
            layoutStatus;

    LayoutClass
            L[],
            L0,
            L1;

    /* Блок кнопок (L4.1) */
    LinearLayout
            layout_4_1;

    int
            CurrentLayout = 0,                  // Текущий номер экрана
            savedCurrentLayout = 0;             // Сохраненный номер экрана

    /* ListView: Выбор задачи из списка: Task Select */
    ListView taskSelect_ListView;
    String[] taskSelect_ListItems;
    ArrayAdapter<String> taskSelect_ListAdapter;
    String taskSelect_SelectedValue;                                    // Значение списка при выборе
    TextView textView1;

    /* ListView: Выбор задачи из списка: CurrentOper Select */
    ListView operSelect_ListView;
    String[] operSelect_ListItems;
    ArrayAdapter<String> operSelect_ListAdapter;
    String operSelect_SelectedValue;                                    // Значение списка при выборе
    TextView textView2;

    /* Кнопки */
    Button
            btn_1_Done,
            btn_go_Clearing,
            btn_ClearDB,
            btn_ClearDNS,
            btn_ClearData,
            btn_ToDB;
    Button
            btn_0_DNS,
            btn_0_Back,
            btn_00_Back,
            btn_0_Task,
            btn_0_Mail,                                                // Печать протокола работы
            btn_0_Oper,
            btn_0_SendMail,
            btn_1_Begin,
            btn_2_TaskGet, btn_2_Cancel,
            btn_3_Cancel, btn_3_Continue,
            btn_4_Cancel, btn_4_Accept,
            btn_5_Cancel, btn_5_Accept,
            btn_6_Cancel, btn_6_Complete,
            btn_7_Cancel, btn_7_Complete, btn_7_Start,
            btn_71_Cancel, btn_71_Complete, btn_71_Start,
            btn_8_OK,
            btn_9_Cancel, btn_9_Accept, btn_9_Refresh, btn_9_Reject,
            btn_11_Next, btn_11_LoaderFound;

    /* Кнопки служебных экранов поиска */
    Button
            b_1000_0,
            b_1000_1,
            b_1010_0,
            b_1010_1;

    /* Кнопка сохранения параметров */
    Button
            b_ParamSave;

    /* Какие-то параметры кнопок */
    boolean[] buttonStatus;
    static final int NUMBER_OF_BUTTONS = 4;

    /* Коды - События нажатия клавиш, списков и пр. */
    static final int
            L00_BUTTON_DNS = 1103,
            L00_BUTTON_BACK = 1100,
            L0_TO_CLEARING = 1101,
            L00_DATA_CLEAR = 1102,
            L__BUTTON_START = 0,
            L00_BUTTON_SENDMAIL = 1001,
            L0_BUTTON_BACK = 1,
            L00_BUTTON_TASK = 2,
            L00_BUTTON_MAIL = 3,
            L00_BUTTON_OPER = 4,
            L0_BUTTON_PARAM_SAVE = 5,
            L1_BUTTON_TO_PARAMS = 100,
            L1_BUTTON_BEGIN_JOB = 101,
            L11_BUTTON_BEGIN_JOB_NEXT = 102,
            L2_BUTTON_TASK_SELECT = 200,
            L2_BUTTON_CANCEL = 201,
            L3_BUTTON_TASK_CONTINUE = 300,
            L3_BUTTON_CANCEL = 301,
            L4_BUTTON_CANCEL = 400,
            L4_BUTTON_ACCEPT = 401,
            L4_LIST_TASK_SELECT = 402,
            L5_BUTTON_CANCEL = 500,
            L5_PRE_BUTTON_ACCEPT = 501,
            L5_BUTTON_ACCEPT = 502,
            L5_LIST_OPER_SELECT = 503,
            L6_BUTTON_COMPLETE = 600,
            L6_BUTTON_CANCEL = 601,
            L7_BUTTON_COMPLETE = 701,
            L7_BUTTON_CANCEL = 702,
            L7_BUTTON_START = 703,
            L71_BUTTON_COMPLETE = 7101,
            L71_BUTTON_CANCEL = 7102,
            L71_BUTTON_START = 7103,
            L8_BUTTON_OK = 801,
            L9_BUTTON_CANCEL = 901,
            L9_BUTTON_ACCEPT = 902,
            L9_BUTTON_REJECT = 903,
            L9_BUTTON_REFRESH = 904;

    /* Коды - Ссылки на лайауты для выборки из массива */
    static final int
            LAYOUT_0_PARAMS = 0,
            LAYOUT_1_BEGIN = 1,
            LAYOUT_2_NO_TASK = 2,
            LAYOUT_3_DO_TASK = 3,
            LAYOUT_4_TASK_SELECT = 4,
            LAYOUT_5_OPER_SELECT = 5,
            LAYOUT_6_SIMPLE_OPER = 6,
            LAYOUT_7_COMPLEX_OPER = 7,
            LAYOUT_8_TASK_COMPLETE = 8,
            LAYOUT_9_SERV_REQUEST = 9,
            LAYOUT_71_LOAD_OPER = 10,
            LAYOUT_11_EMPTY = 11,
            LAYOUT_00_CLEARING = 12;

    /* Текст экрана в лайауте */
    TextView
            textView[];

    /* Текст статусной строки */
    TextView
            textView_StatusLine;

    /* Таймер переключения статусной строки */
    Timer
            statusLineOnOffTimer;
    MyTimerTask_StatusLineOnOff
            myTimerTask_statusLineOnOff;        // Задача таймера переключения статусной строки

    /* Таймер наблюдения за событиями вывода в статустную строку */
    Timer
            statusLineLookOnTimer;
    MyTimerTask_LookAtStatusLine
            myTimerTask_lookAtServerPingClass;  //

    /* Таймер задачи наблюдения за поиском сервера в сети */
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
            MAX_NUM_OF_DEVICES = 1;

    static int                                  // Индекс ServerPingClass для различных устройств
            DEVICE_IS_TERMINAL = 0,             // Весовой терминал
            DEVICE_IS_LOADER = 1;               // Погрузчик

    /* Переменные, определяющие работу при поиске серверов в сети */
    public ArrayList<String[]>
            serverFound;                       // Параметры найденного сервера
    public ArrayList<Boolean>
            endServerFindCondition;            // Условие выхода из цикла при поиске серверов

    /* Дополнительный текст */
    TextView
            text_7_target, text_71_target;

    /* Таймеры */
    private Timer
            myTerminalDataReadTimer;        // Таймер опроса весового терминала
    private TerminalDataReadTimerTask
            myTerminalDataReadTask;            // Задача для опроса весового терминала

    /* Параметры конфигурации */
    String
            WiFiNet,                // Сеть WiFi
            WiFiPass,               // Пароль досступа к сети WiFi
            MixerName,              // имя миксера
            MixerPass,              // Пароль миксера
            MixerTermName,          // имя весового терминала
            MixerTermAddr;          // стартовый адрес весового терминала (для быстрого поиска в сети)
    // Поля для редактирования
    EditText
            et_WiFiNet,
            et_WiFiPass,
            et_MixerName,
            et_MixerPass,
            et_MixerTermName,
            et_MixerTermAddr;

    /**
     * Конструктор
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /********************************
         * Объявление объектов - классов
         ********************************/
        context = MainActivity.this;
        logTAG = "MAIN: ";
        logMessage = "";
        statusLine
                = new StatusLine();
        conf
                = new Configurator(this);
        messenger
                = new Messenger(this);
        dbHelper
                = new DBHelper(this.context);
        dbHandler
                = new DBHandler(this, this);
        controller
                = new Controller(this);
        storer
                = new Storer(this);
        currentTask
                = new CurrentTask(this);
        currentOper
                = new CurrentOper(this);
        dbFunctions
                = new DBFunctions(this, dbHelper.getWritableDatabase());
        server
                = new ServerCommincator(this);
        loader
                = new LoaderCommunicator(this);
        net
                = new NetworkHandler(this);
        sfc
                = new ServerFindControlClass();
        sendMailToControlServer
                = new SendMailToControlServer(this);
        mailToSend
                = new MailToSend(this, dbHelper.getWritableDatabase());
//        gps
//                = new GPSTracker(this);
//
        /***********************
         * Объявление Лайауты
         ***********************/
        // ???
        layoutStatus
                = new LayoutStatusClass(Integer.valueOf(getString(R.string.NUMBER_OF_LAYOUTS)));
        // Старый вариант формирования экранов
        layout
                = new LinearLayout[layoutStatus.numberOfLayouts];
        layout[LAYOUT_11_EMPTY]
                = (LinearLayout) findViewById(R.id.LL11_Empty);
        layout[LAYOUT_00_CLEARING]
                = (LinearLayout) findViewById(R.id.LL0_Clearing);
        layout[LAYOUT_0_PARAMS]
                = (LinearLayout) findViewById(R.id.LL0_Dialog);
        layout[LAYOUT_1_BEGIN]
                = (LinearLayout) findViewById(R.id.LL1_Begin);
        layout[LAYOUT_2_NO_TASK]
                = (LinearLayout) findViewById(R.id.LL2_No_Task);
        layout[LAYOUT_3_DO_TASK]
                = (LinearLayout) findViewById(R.id.LL3_Do_Task);
        layout[LAYOUT_4_TASK_SELECT]
                = (LinearLayout) findViewById(R.id.LL4_Task_Select);
        layout[LAYOUT_5_OPER_SELECT]
                = (LinearLayout) findViewById(R.id.LL5_Oper_select);
        layout[LAYOUT_6_SIMPLE_OPER]
                = (LinearLayout) findViewById(R.id.LL6_Simple_Task);
        layout[LAYOUT_7_COMPLEX_OPER]
                = (LinearLayout) findViewById(R.id.LL7_Complex_Task);
        layout[LAYOUT_8_TASK_COMPLETE]
                = (LinearLayout) findViewById(R.id.LL8_Task_Complete);
        layout[LAYOUT_9_SERV_REQUEST]
                = (LinearLayout) findViewById(R.id.LL9_ServiceRequest);
        layout[LAYOUT_71_LOAD_OPER]
                = (LinearLayout) findViewById(R.id.LL71_Load_Task);
        layout_4_1
                = (LinearLayout) findViewById(R.id.LL4_1_Buttons);

        // Новый вариант формирования экранов
        L
                = new LayoutClass[Integer.valueOf(getString(R.string.NUMBER_OF_LAYOUTS))];

        // Массив экранов. Объявляем только те, которые нам будут нужны, остальные - null
        L[LAYOUT_4_TASK_SELECT] =
                new LayoutClass(
                        (LinearLayout) findViewById(R.id.LL1_Begin),
                        new Timer[]
                                {},
                        new TextView[]
                                {
                                        (TextView) findViewById(R.id.text_1_Info)
                                },
                        new String[]
                                {
                                        "Терминал миксера"
                                },
                        new Button[]
                                {
                                        (Button) findViewById(R.id.button_1_BeginJob)
                                },
                        new String[]
                                {
                                        "Начать"
                                }
                );

        L[LAYOUT_1_BEGIN] =
                new LayoutClass(
                        (LinearLayout) findViewById(R.id.LL1_Begin),
                        new Timer[]
                                {},
                        new TextView[]
                                {
                                        (TextView) findViewById(R.id.text_1_Info)
                                },
                        new String[]
                                {
                                        "Терминал миксера"
                                },
                        new Button[]
                                {
                                        (Button) findViewById(R.id.button_1_BeginJob)
                                },
                        new String[]
                                {
                                        "Начать"
                                }
                );

        L[LAYOUT_9_SERV_REQUEST] =
                new LayoutClass(
                        (LinearLayout) findViewById(R.id.LL9_ServiceRequest),
                        new Timer[]
                                {},
                        new TextView[]
                                {
                                        (TextView) findViewById(R.id.text_1_Info)
                                },
                        new String[]
                                {
                                        "Запрос на обслуживание"
                                },
                        new Button[]
                                {
                                        (Button) findViewById(R.id.button_9_Accept)
                                },
                        new String[]
                                {
                                        "Начать"
                                }
                );

        // Служебный экран - акивируется в момент выполнения поиска сервера в подсети
        L1 = new LayoutClass(
                (LinearLayout) findViewById(R.id.layout_1000),
                new Timer[]
                        {
                                new Timer()                                             // Btn OK
                        },
                new TextView[]
                        {
                                (TextView) findViewById(R.id.textView_Header_1001),     // HEADER
                                (TextView) findViewById(R.id.textView_Header_1002)      // INFO
                        },
                new String[]
                        {
                                "Поиск сервера",
                                "Адрес сервера"
                        },
                new Button[]
                        {
                                (Button) findViewById(R.id.b_1000_0),                   // OK
                                (Button) findViewById(R.id.b_1000_1),                   // CANCEL
                        },
                new String[]
                        {
                                "OK",                                                   // Btn OK
                                "ОТМЕНА"                                                // Btn CANCEL
                        }
        );

        /***********************
         * Таймеры
         ***********************/
        statusLineOnOffTimer
                = new Timer();
        myTimerTask_statusLineOnOff
                = new MyTimerTask_StatusLineOnOff();

        int
                Layout_0 = 0,                       // Номера экранов
                Layout_1 = 1,
                findServerLayout_900 = 2,           // Экраны поиска сетевых устройств
                findServerLayout_901 = 3,
                findServerLayout_902 = 4;

        int
                CurrentLayout = 0,                  // Текущий номер экрана
                savedCurrentLayout = 0;             // Сохраненный номер экрана

        /**************************************************
         * Переменные, управляющие поиском устройств в сети
         **************************************************/
//        findServerTimer
//                = new ArrayList<>();
//        myTimerTask_watchOnServerFind
//                = new ArrayList<>();
//        numOfServerPingClasses
//                = new ArrayList<>();
//        serverFound
//                = new ArrayList<>();
//        endServerFindCondition
//                = new ArrayList<>();
//
        /* Вот тут я не уверен, что именно так должно все быть:
           Наверное, лучше все эти присваивания перенести в CheckConnection для случая, когда
           нам попадается для поиска новое устройство */
//        for (int i = 0; i < MAX_NUM_OF_DEVICES; i++) {
//            findServerTimer
//                    .add(i, new Timer());
//            numOfServerPingClasses
//                    .add(i, new Integer(0));
//            serverFound
//                    .add(i, new String[3]);
//            endServerFindCondition
//                    .add(i, false);
//        }
        conf.is_Connected_to_network
                = net.terminalConnectedToNetwork();
        if (conf.is_Connected_to_network) {
            conf.ipAddress = net.get_My_IP();
            conf.networkMask = net.get_Net_Mask_from_IP(conf.ipAddress);
        }

        /*******************************
         * TextViews
         *******************************/
        textView
                = new TextView[layoutStatus.numberOfLayouts];
        textView[LAYOUT_0_PARAMS]
                = null;
        textView[LAYOUT_1_BEGIN]
                = (TextView) findViewById(R.id.text_1_Info);
        textView[LAYOUT_2_NO_TASK]
                = (TextView) findViewById(R.id.text_2_info);
        textView[LAYOUT_3_DO_TASK]
                = (TextView) findViewById(R.id.text_3_Info);
        textView[LAYOUT_4_TASK_SELECT]
                = (TextView) findViewById(R.id.text_4_Info);
        textView[LAYOUT_5_OPER_SELECT]
                = (TextView) findViewById(R.id.text_5_Info);
        textView[LAYOUT_6_SIMPLE_OPER]
                = (TextView) findViewById(R.id.text_6_info);
        textView[LAYOUT_7_COMPLEX_OPER]
                = (TextView) findViewById(R.id.text_7_info);
        textView[LAYOUT_8_TASK_COMPLETE]
                = (TextView) findViewById(R.id.text_8_info);
        textView[LAYOUT_9_SERV_REQUEST]
                = (TextView) findViewById(R.id.text_9_info);
        textView[LAYOUT_71_LOAD_OPER]
                = (TextView) findViewById(R.id.text_71_info);
        textView[LAYOUT_11_EMPTY]
                = null;
        textView[LAYOUT_00_CLEARING]
                = (TextView) findViewById(R.id.text_0_Info);

        text_7_target
                = (TextView) findViewById(R.id.text_7_target);
        text_71_target
                = (TextView) findViewById(R.id.text_71_target);

        /*******************************
         * ListView: Task select
         *******************************/
        taskSelect_ListView
                = (ListView) findViewById(R.id.list_Task_Select);
        taskSelect_SelectedValue
                = "";
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

        /*******************************
         * ListView: CurrentOper select
         *******************************/
        operSelect_ListView
                = (ListView) findViewById(R.id.list_Oper_Select);
        operSelect_SelectedValue
                = "";
        operSelect_ListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        textView2
                                = (TextView) view;
                        operSelect_SelectedValue
                                = textView2.getText().toString();
                        statusLine
                                .set(operSelect_SelectedValue);
                        setTextInLayout
                                (LAYOUT_5_OPER_SELECT, operSelect_SelectedValue);
                        controller
                                .controller(L5_LIST_OPER_SELECT);
                    }
                });

        /*******************************
         * Кнопки - экран поиска сервера
         *******************************/
        /* Сервер найден - возврат к предыдущему экрану */
        b_1000_0
                = (Button) findViewById(R.id.b_1000_0);
        b_1000_0
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        L1.Deactivate(
                                layout);
                        // Экран возврата
                        L[LAYOUT_1_BEGIN].Activate(
                                null,
                                null,
                                layout,
                                null,
                                null);
                    }
                });
        /* ОТМЕНА - надо остановить поиск и отменить операции предыдущего экрана */
        b_1000_1
                = (Button) findViewById(R.id.b_1000_1);
        b_1000_1
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (endServerFindCondition != null) {
                            endServerFindCondition.set(0, true);
                        }
                        toStatusLineNoBlink("Поиск отменен");
                        L1.Deactivate(
                                layout);
                        // Экран возврата
                        L[LAYOUT_1_BEGIN].Activate(
                                null,
                                null,
                                layout,
                                null,
                                btn_1_Done);
                    }
                });

        /*******************************
         * Кнопка сохранения параметров
         *******************************/
        b_ParamSave
                = (Button) findViewById(R.id.button_0_2);
        b_ParamSave
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        controller.controller(L0_BUTTON_PARAM_SAVE);
                    }
                });

        /*******************************
         * Кнопки ВСЕ
         *******************************/

        /* Очистка текущих данных */
        btn_ClearData
                = (Button) findViewById(R.id.button_Clear_Data);
        btn_ClearData
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("btn_ClearData", "pressed");
                        controller.controller(L00_DATA_CLEAR);
                    }
                });

        /* Вход в экран очистки параметров */
        btn_go_Clearing
                = (Button) findViewById(R.id.button_to_Clearing);
        btn_go_Clearing
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("btn_go_Clearing", "pressed");
                        controller.controller(L0_TO_CLEARING);
                    }
                });

        /* Возврат из второго экрана параметров */
        btn_00_Back
                = (Button) findViewById(R.id.button_00_Back);
        btn_00_Back
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("btn_00_Back", "pressed");
                        controller.controller(L1_BUTTON_TO_PARAMS);
                    }
                });

        buttonStatus
                = new boolean[NUMBER_OF_BUTTONS];
        buttonStatusDrop();

        // btn_11_Next
        btn_11_Next
                = (Button) findViewById(R.id.btn_11_00_Begin_job);
        btn_11_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("btn_11_Next", "pressed");
                controller.controller(L11_BUTTON_BEGIN_JOB_NEXT);
            }
        });

        // btn_11_Next
        btn_11_LoaderFound
                = (Button) findViewById(R.id.btn_11_00_Begin_job);
        btn_11_LoaderFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("btn_11_LoaderFound", "pressed");
                controller.controller(L5_BUTTON_ACCEPT);
            }
        });

        // btn_0_SendMail
        btn_0_SendMail
                = (Button) findViewById(R.id.button_0_SendMail);
        btn_0_SendMail
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        server.sendMail();
                    }
                });

        // Кнопка btn_ToDB
        btn_ToDB
                = (Button) findViewById(R.id.button_LoadDB);
        btn_ToDB
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        controller.controller(L1_BUTTON_TO_PARAMS);
                    }
                });

        // Кнопка btn_ClearDB
        btn_ClearDB
                = (Button) findViewById(R.id.button_Clear_DB);
        btn_ClearDB
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        storer.clearDB();
                        controller.controller(L__BUTTON_START);
                    }
                });

        // Кнопка btn_ClearDNS
        btn_ClearDNS
                = (Button) findViewById(R.id.button_Clear_DNS);
        btn_ClearDNS
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbHandler.dbTableList_OBJECTS();
                        dbHandler.clearTableObjects();
                        dbHandler.dbTableList_OBJECTS();
                    }
                });

        // Кнопка btn_0_Back (LL0)
        btn_0_Back = (Button) findViewById(R.id.button_0_Back);         // Response Yes
        btn_0_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(LAYOUT_1_BEGIN);
            }
        });


        btn_0_DNS = (Button) findViewById(R.id.button_0_DNS);
        btn_0_DNS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.controller(L00_BUTTON_DNS);
            }
        });

        // Кнопка "Печать таблицы TASK"
        btn_0_Task = (Button) findViewById(R.id.button_0_Task);           // Response No
        btn_0_Task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L00_BUTTON_TASK);
            }
        });

        // Кнопка "Печать таблицы TASK"
        btn_0_Mail = (Button) findViewById(R.id.button_0_Task1);           // Response No
        btn_0_Mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L00_BUTTON_MAIL);
            }
        });

        // Кнопка "Печать таблицы CurrentOper"
        btn_0_Oper = (Button) findViewById(R.id.button_0_Oper);
        btn_0_Oper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.controller(L00_BUTTON_OPER);
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
//                layoutsVisiblityRestore();
//                Toast.makeText(getApplicationContext(),"Работа завершена",Toast.LENGTH_LONG).show();
//                MainActivity.this.finish();
                Log.i("STOP", dbHandler.printTableData("mail"));
                System.exit(0);
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
        /*******************************
         * Редактирование параметров
         *******************************/
        et_WiFiNet
                = (EditText) findViewById(R.id.editText_0_2_5);
        et_WiFiPass
                = (EditText) findViewById(R.id.editText_0_2_4);
        et_MixerName
                = (EditText) findViewById(R.id.editText_0_2_0);
        et_MixerPass
                = (EditText) findViewById(R.id.editText_0_2_6);
        et_MixerTermName
                = (EditText) findViewById(R.id.editText_0_2_1);
        et_MixerTermAddr
                = (EditText) findViewById(R.id.editText_0_2_2);

        /*******************************
         * ЗАПУСК!!!
         *******************************/

        paramInit();

        conf
                .setSystemParameters();

        printServerFound();

        currentTask
                .setTaskData();


        /* ========== */
//        sendMailToControlServer.sendMail();
        sendMailToControlServer.send();

        controller
                .controller(L__BUTTON_START);

        /*******************************
         * КОНЕЦ
         *******************************/
    } /* end of onCreate */

    class ClickButton_btn_1_Begin extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_1_Begin.callOnClick();
                }
            });
        }
    }

    /**
     * Класс управления поиском сетевых устройств
     */
    public class ServerFindControlClass {
        ArrayList<String>
                serverName;                         // Имя сервера
        ArrayList<Timer>
                findServerTimer;                    // Таймер поиска сервера
        ArrayList<myTimerTask_WatchOnServerFind>
                findServerTimerTask;                // Задача таймера поиска сервера
        public ArrayList<Integer>
                numOfServerPingClasses;             // Количество открытых в данный момент ServerPingClass
        public ArrayList<String[]>
                serverFound;                        // Параметры найденного сервера
        public ArrayList<Boolean>
                endServerFindCondition;             // Условие выхода из цикла при поиске серверов

        /**
         * Конструктор
         */
        public ServerFindControlClass() {
            serverName
                    = new ArrayList<>();
            findServerTimer
                    = new ArrayList<>();
            findServerTimerTask
                    = new ArrayList<>();
            numOfServerPingClasses
                    = new ArrayList<>();
            serverFound
                    = new ArrayList<>();
            endServerFindCondition
                    = new ArrayList<>();
        }

        /**
         * Инициализация структуры под новый поиск устройства
         *
         * @param pServerName
         */
        public int init(String pServerName) {
            boolean
                    resB = false;
            int
                    resI = 0;
            // Смотрим, нет ли в структуре записи с таким именем сервера
            for (int i = 0; i < serverName.size(); i++) {
                if (serverName.get(i).equals(pServerName) == true) {
                    resI
                            = i + 1;
                    resB
                            = true;
                    break;
                }
            }
            // Если в структуре нашлась запись для данного сервера, то удаляем эту запись
            if (resB == true) {
                findServerTimer
                        .remove(resI);
                findServerTimerTask
                        .remove(resI);
                numOfServerPingClasses
                        .remove(resI);
                serverFound
                        .remove(resI);
                endServerFindCondition
                        .remove(resI);
                serverName
                        .remove(resI);
            }
            // И создаем новую с таким же индексом
            findServerTimer
                    .add(resI, new Timer());
            findServerTimerTask
                    .add(resI, new myTimerTask_WatchOnServerFind(pServerName, resI));
            numOfServerPingClasses
                    .add(resI, 0);
            serverFound
                    .add(resI, new String[]{pServerName, null, null, null, null, null});
            endServerFindCondition
                    .add(resI, false);
            serverName
                    .add(resI, pServerName);

            // Возвращаем индекс структуры
            return resI;
        }

        /**
         * Возвращает индекс структуры, соответствующий заданному имени сервера
         *
         * @param pServerName
         * @return
         */
        public int getIndex(String pServerName) {
            // Если записей в структуре нет вообще
            if (serverName.size() == 0) {
                return -1;
            }
            // Ищем подходящую запись
            for (int i = 0; i < serverName.size(); i++) {
                if (serverName.get(i).equals(pServerName) == true) {
                    return i;
                }
            }
            return -1;
        }

    }

    /**
     * Сохраняет все параметры конфигурации
     */

    /* Надо переписать в виде процедуры.
     * Или класса, в котором будет все */

    void paramSave() {
        Log.i(logTAG, "SAVE params");
        if (MixerTermName.equals(et_MixerTermName.getText()) == false) {
            MixerTermName
                    = et_MixerTermName.getText().toString();
            dbHandler.paramStore
                    ("MixerTermName", MixerTermName, null);
        }
        if (MixerTermAddr.equals(et_MixerTermAddr.getText()) == false) {
            MixerTermAddr
                    = et_MixerTermAddr.getText().toString();
            dbHandler.paramStore
                    ("MixerTermAddr", MixerTermAddr, null);
        }

        if (MixerName.equals(et_MixerName.getText()) == false) {
            MixerName
                    = et_MixerName.getText().toString();
            dbHandler.paramStore
                    ("MixerName", MixerName, null);
        }
        if (MixerPass.equals(et_MixerPass.getText()) == false) {
            MixerPass
                    = et_MixerPass.getText().toString();
            dbHandler.paramStore
                    ("MixerPass", MixerPass, null);
        }
        if (WiFiNet.equals(et_WiFiNet.getText()) == false) {
            WiFiNet
                    = et_WiFiNet.getText().toString();
            dbHandler.paramStore
                    ("WiFiNet", WiFiNet, null);
        }
        if (WiFiPass.equals(et_WiFiPass.getText()) == false) {
            WiFiPass
                    = et_WiFiPass.getText().toString();
            dbHandler.paramStore
                    ("WiFiPass", WiFiPass, null);
        }
//        if (MixerTermAddr.equals(et_MixerName.getText()) == false) {
//            MixerName
//                    = et_MixerName.getText().toString();
//            dbHandler.paramStore
//                    ("MixerName", MixerName, null);
//        }
        // Обновляет параметры из конфигурации
        conf.setSystemParameters();
    }

    /**
     * Загрузка параметров конфигурации
     * <p>
     * Параметры конфигурации:
     * 1. MixerTermName     имя весового терминала
     * 2. MixerTermAddr     стартовый адрес весового терминала (для быстрого поиска в сети)
     */

    void paramInit() {
        if (dbHandler.paramNow("WiFiNet")) {
            WiFiNet = dbHandler.paramGet("WiFiNet")[dbHandler.PARAMETER_VALUE];
        } else {
            WiFiNet = "intex";
        }
        et_WiFiNet.setText(WiFiNet);

        if (dbHandler.paramNow("WiFiPass")) {
            WiFiPass = dbHandler.paramGet("WiFiPass")[dbHandler.PARAMETER_VALUE];
        } else {
            WiFiPass = "9210603060";
        }
        et_WiFiPass.setText(WiFiPass);

        if (dbHandler.paramNow("MixerName")) {
            MixerName = dbHandler.paramGet("MixerName")[dbHandler.PARAMETER_VALUE];
        } else {
            MixerName = "mixer.001";
        }
        et_MixerName.setText(MixerName);

        /* MixerPass */
        if (dbHandler.paramNow("MixerPass")) {
            MixerPass = dbHandler.paramGet("MixerPass")[dbHandler.PARAMETER_VALUE];
        } else {
            MixerPass = "mixer.001";
        }
        et_MixerPass.setText(MixerPass);

        /* MixerTermName */
        if (dbHandler.paramNow("MixerTermName")) {
            MixerTermName = dbHandler.paramGet("MixerTermName")[dbHandler.PARAMETER_VALUE];
        } else {
            MixerTermName = "mixerterm.001";
        }
        et_MixerTermName.setText(MixerTermName);

        if (dbHandler.paramNow("MixerTermAddr")) {
            MixerTermAddr = dbHandler.paramGet("MixerTermAddr")[dbHandler.PARAMETER_VALUE];
        } else {
            MixerTermAddr = "20";
        }
        et_MixerTermAddr.setText(MixerTermAddr);
    }

    /**
     * Проверка возможности подключения к конкретному серверу.
     * Проверка производится в текущей подсети (networkMask)
     *
     * @param serverToFind - имя сервера
     */

    public boolean CheckConnection(
            String
                    serverToFind,
            String
                    serverStartAddress,
            LayoutClass
                    pLayoutToReturn,
            Button
                    btnToReturn) {

        // Гасим тот экран, на который будем возвращаться
        pLayoutToReturn.myLayout.setVisibility(View.INVISIBLE);

        /**
         * В результате поиска адрес найденного сервера записывается в serverFound[]
         *
         * Сейчас попробуем менять значение whatDeviceWeFind в зависимости от того, есть ли запись о
         * таком сервере в serverFound или нет.
         * Если мы такую запись нашли, то whatDeviceWeFind равно номеру найденной записи.
         * Если записи с указанным именем нет, то ее надо моздать и whatDeviceWeFind должно быть
         * равно номеру вновь созданной записи
         */
        int
                i;
        int
                Result_Empty = 0,
                Result_Found = 1,
                Result_New = 2;
        int
                serverFoudFindResult = Result_Empty;
        int
                whatDeviceWeFind = 0;

        // Если массив пока вообще пустой, то первым будет элемент с индексом 0

//        if (serverFound.size() > 0) {
//            for (i = 0; i < serverFound.size(); i++) {
//                if (serverFound.get(i)[NET_DEVICE_NAME].equals(serverToFind) == true) {
//                    // Если мы такую запись нашли, то чистим все для нее
//                    whatDeviceWeFind
//                            = i;
//                    serverFoudFindResult
//                            = Result_Found;
//                    break;
//                } else {
//                    // Если такой записи нет, то создаем все по-новой
//                    whatDeviceWeFind
//                            = serverFound.size() + 1;
//                    serverFoudFindResult
//                            = Result_New;
//                }
//            }
//        }
//
//        // Добавлять связанные записи в аррайлисты будем как в случае пустого массива, так и в случае
//        // отсутствия нужной нам записи в непустом массиве
//        if (serverFoudFindResult == Result_Empty) {
//            serverFoudFindResult = Result_New;
//        }
//
//        // Если запись нашлась, то найти записи для данного сервера и уничтожить их
//        if (serverFoudFindResult == Result_Found) {
//            for (i = 0; i < serverFound.size(); i++) {
//                if (myTimerTask_watchOnServerFind.get(i).serverName == serverToFind) {
//                    myTimerTask_watchOnServerFind.remove(i);
//                    whatDeviceWeFind = i;
//                    break;
//                }
//            }
//            findServerTimer
//                    .remove(whatDeviceWeFind);
//            numOfServerPingClasses
//                    .remove(whatDeviceWeFind);
//            serverFound
//                    .remove(whatDeviceWeFind);
//            endServerFindCondition
//                    .remove(whatDeviceWeFind);
//        }
//
//        // Cоздать по-новой
//        findServerTimer
//                .add(whatDeviceWeFind, new Timer());
//        numOfServerPingClasses
//                .add(whatDeviceWeFind, new Integer(0));
//        serverFound
//                .add(whatDeviceWeFind, new String[3]);
//        endServerFindCondition
//                .add(whatDeviceWeFind, false);

        // Новая задача таймера - потом создать по-новой

        Log.i("==CheckConnection==", "=============================");
        Log.i("==CheckConnection==", "serverToFind=" + serverToFind);

        whatDeviceWeFind
                = sfc.init(serverToFind);

        Log.i("==CheckConnection==", "whatDeviceWeFind=" + whatDeviceWeFind);
        Log.i("==CheckConnection==", "serverToFind=" + serverToFind + ", netmask=" + conf.networkMask);

        // Переходим в экран поиска
        L1.Activate(
                new String[]{null, serverToFind},
                null,
                layout,
                pLayoutToReturn,
                null
        );

        // Пытаемся определить параметры устройства, сохраненные в БД
        String[] terminalAddressFromDB
                = dbHandler.getDeviceAddrfromDB(conf.networkMask, serverToFind, conf.terminalAddress);

        /* Где-то тут будем обрабатывать начальный адрес искомого сервера */

        Log.i(logTAG, "Find=" + terminalAddressFromDB[net.NET_DEVICE_NAME] + ", addr=" + terminalAddressFromDB[net.NET_DEVICE_ADDR]);

        // Запускаем поиск интересующего нас сервера
        try {
            net.findServerInNetwork(serverToFind, terminalAddressFromDB[net.NET_DEVICE_ADDR], conf.terminalPort, whatDeviceWeFind);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Запускаем опрос по таймеру на предмет того, что сервер найден
        sfc.findServerTimer.get(whatDeviceWeFind)
                .schedule(sfc.findServerTimerTask.get(whatDeviceWeFind), 100, 100);
        //
        Log.i(logTAG, "L1.res=" + L1.findResult);
        //
        return true;
    }

    // "Нажимает" кнопку b_1000_0

    void b_1000_0_Press() {
        b_1000_0.callOnClick();
    }

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

    /**
     * Установка текста в лайауте
     *
     * @param n
     * @param s
     */
    void setTextInLayout(int n, String s) {
        if (textView[n] != null) {
            textView[n].setText(s);
        }
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
                    storer.sendMessageToControlServer(messenger.msg_TaskReport_begin(taskId));
                    Log.i(logTAG, "Status: undef->begin");
                    break;
                case "begin":
                    taskStatus = "begin";
                    //storer.sendMessageToControlServer(messendger.msg_TaskReport_resume(operId));
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
                    storer.sendMessageToControlServer(messenger.msg_TaskReport_resume(taskId));
                    Log.i(logTAG, "Status: suspend->resume");
                    break;
                case "resume":
                    taskStatus = "resume";
                    storer.sendMessageToControlServer(messenger.msg_TaskReport_resume(taskId));
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
//                    storer.sendMessageToControlServer(messendger.msg_TaskReport_suspend(operId));
                    Log.i(logTAG, "Status: undef->undef");
                    break;
                case "begin":
                    taskStatus = "suspend";
                    storer.sendMessageToControlServer(messenger.msg_TaskReport_suspend(taskId));
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
                    storer.sendMessageToControlServer(messenger.msg_TaskReport_suspend(taskId));
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
                    storer.sendMessageToControlServer(messenger.msg_TaskReport_end(taskId));
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
                    storer.sendMessageToControlServer(messenger.msg_TaskReport_end(taskId));
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
                    storer.sendMessageToControlServer(messenger.msg_OperReport_begin(operId));
                    Log.i(logTAG, "Status: undef->begin");
                    break;
                case "begin":
                    operStatus = "resume";
                    storer.sendMessageToControlServer(messenger.msg_OperReport_resume(operId));
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
                    storer.sendMessageToControlServer(messenger.msg_OperReport_resume(operId));
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
                    storer.sendMessageToControlServer(messenger.msg_OperReport_suspend(operId));
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
                    storer.sendMessageToControlServer(messenger.msg_OperReport_suspend(operId));
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
                    storer.sendMessageToControlServer(messenger.msg_OperReport_end(operId));
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
                    storer.sendMessageToControlServer(messenger.msg_OperReport_end(operId));
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

        /**
         * Информация об операции для вывода на экран непосредственно при погрузке
         *
         * @return
         */
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

        /**
         * Значение параметра операции по его имени из БД
         *
         * @param operId
         * @param parameterName
         * @return
         */
        String getParamFromDB(String operId, String parameterName) {
            return dbFunctions.getOperParameter(operId, parameterName);
        }

        /**
         * Значение параметра операции по его имени
         *
         * @param parameterName
         * @return
         */
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

        /**
         * Значение параметра операции по его имени
         *
         * @param pName
         * @param op
         * @return
         */
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
            Cursor c = dbHandler.database.query(
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

    /**
     * Установка текущей операции
     *
     * @param operId
     */
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
        loader
                .serverSendWeightStop();
    }

    public void weightDataFromDeviceReader_Start() {
        Log.i(logTAG + ": weightData: ", "start");
        myTerminalDataReadTimer
                = new Timer();
        myTerminalDataReadTask
                = new TerminalDataReadTimerTask();
        myTerminalDataReadTimer
                .schedule(myTerminalDataReadTask, 500, 500);
    }

    public void weightDataFromDeviceReader_Stop() {
        Log.i(logTAG + ": weightData: ", "stop");
        if (myTerminalDataReadTimer != null) {
            Log.i(logTAG + ": weightData", "timer myTerminalDataReadTimer not null");
            myTerminalDataReadTimer
                    .purge();
            myTerminalDataReadTimer
                    .cancel();
            myTerminalDataReadTimer
                    = null;
        } else {
            Log.i(logTAG + ": weightData", "timer myTerminalDataReadTimer IS NULL");
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

    /**
     * Задача для опроса весового терминала
     */
    class TerminalDataReadTimerTask extends TimerTask {
        @Override
        public void run() {
            Socket socket;
            String socketAddr = conf.terminalAddress;
//            String socketAddr = "192.168.1.113";
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
                    Log.i(logTAG, "FROM DEVICE=" + res);
                    socket.close();
                    /* Пытаемся сохранить показания терминала в протокол, как - см. описание функции */
                    storer.storeCurrentWeightToProtocol(extractData(res));
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
                    displayWeightParameters1();
                }
            });
        }
    }

    public String extractData(String srcString) {
        String r = "0";
        Pattern pattern
                = Pattern.compile("data=\'\\d+\'");
        Matcher matcher
                = pattern.matcher(srcString);
        if (matcher.find()) {
            r = (matcher.group());
            Log.i(logTAG, "!!!! matcher.find()=" + r);
        }

        pattern
                = Pattern.compile("\\d+");
        matcher
                = pattern.matcher(r);
        if (matcher.find()) {
            r = (matcher.group());
            Log.i(logTAG, "find()=" + r);
        }

        return r;
    }

    /**
     * @param srcString
     * @return
     */
    public String extractDigits(String srcString) {
        String r = "0";
        Pattern pattern
                = Pattern.compile("data=\'\\d+\'");
        Matcher matcher
                = pattern.matcher(srcString);
        if (matcher.find()) {
            r = (matcher.group());
            Log.i(logTAG, "!!!! matcher.find()=" + r);
        }
        return r;
    }

    /**
     *
     */
    void displayWeightParameters() {

//        /* Вычислить оставщийся вес */
//        storer.weightRemain = storer.weightTarget - storer.weightCurrent;

        // ПОказания весов
        text_7_target.setText(String.valueOf(storer.weightCurrent));
        // Остаток для погрузки
//        textView[LAYOUT_7_COMPLEX_OPER].setText(String.valueOf(storer.weightRemain));
    }

    void displayWeightParameters1() {

        // Вычислить отставщийся вес
//        storer.weightRemain = storer.weightTarget - storer.weightCurrent;

        // ПОказания весов
        text_71_target.setText(String.valueOf(storer.weightCurrent));
        // Остаток для погрузки
//        textView[LAYOUT_71_LOAD_OPER].setText(String.valueOf(storer.weightRemain));
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

    /**
     * Переключение на слой
     */
    void gotoLayout(int newLayout, String pTextToInfo) {
        String textToInfo = pTextToInfo;
        layoutVisiblitySet(newLayout);                          // Установить видимость слоя
        toStatusLine("Layout=" + newLayout);
        switch (newLayout) {
            case LAYOUT_00_CLEARING:

                break;
            case LAYOUT_11_EMPTY:
                /**
                 * "Пустой" слой для различных операций, связанных с длительным выполнением чего-нибудь
                 */

                break;

            case LAYOUT_0_PARAMS:
                /**
                 *  Заполнение экранных значений параметров
                 */
                paramInit();
                et_MixerTermName
                        .setText(MixerTermName);
                et_MixerTermAddr
                        .setText(MixerTermAddr);
                et_MixerName
                        .setText(MixerName);
                et_MixerPass
                        .setText(MixerPass);
                et_WiFiNet
                        .setText(WiFiNet);
                et_WiFiPass
                        .setText(WiFiPass);
                break;

            case LAYOUT_1_BEGIN:

                /**
                 * Непонятно, какого хрена вот так сделано, но надо будет потом разобраться...
                 */
                // Установка кнопок в 7 экране
                btn_7_Start.setVisibility(VISIBLE);
                btn_7_Complete.setVisibility(View.INVISIBLE);
                // Остановить получение показаний весов
                weightDataFromDeviceReader_Stop();
                weightDataToLoaderSender_Stop();

                // Запуск автонажатия кнопки
//                Timer timer_Click_btn_1_Begin
//                        = new Timer();
//                ClickButton_btn_1_Begin clickButton_btn_1_begin
//                        = new ClickButton_btn_1_Begin();
//                timer_Click_btn_1_Begin
//                        .schedule(clickButton_btn_1_begin,3000);

                break;

            case LAYOUT_2_NO_TASK:
                break;

            case LAYOUT_3_DO_TASK:
                break;

            case LAYOUT_4_TASK_SELECT:
                if (storer.getNumberTaskForExecution() > 0) {
                    taskSelect_ListItems
                            = storer.getListTasksForExecution();
                    taskSelect_ListAdapter
                            = new ArrayAdapter<String>(
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
                    operSelect_ListItems
                            = storer.getListOperationsForExecution();
                    operSelect_ListAdapter
                            = new ArrayAdapter<String>(
                            context,
                            android.R.layout.simple_list_item_1,
                            operSelect_ListItems);
                    operSelect_ListView
                            .setAdapter(operSelect_ListAdapter);
                    textToInfo
                            = operSelect_ListItems[0];
                }
                break;

            case LAYOUT_6_SIMPLE_OPER:

                break;

            case LAYOUT_7_COMPLEX_OPER:
                loader.serverSendWeight();
                break;

            case LAYOUT_71_LOAD_OPER:
                /*  */
                break;

            case LAYOUT_8_TASK_COMPLETE:
                /**
                 * Пока ставим выход из программы полностью при завершении задачи, т.к. есть
                 * какой-то косяк с повторным обращением к погрузчику - погрузчик "не слышит"
                 * запросов на обслуживание.
                 * При перезапуске все нормально.
                 * Надо будет найти причину этой фигни и потом выход из системы здесь можно
                 * будет убрать.
                 */
                Log.i("STOP", dbHandler.printTableData("mail"));
                System.exit(0);
                break;

            case LAYOUT_9_SERV_REQUEST:
                // Проверяем наличие погрузчика в сети


                // Посылаем погрузчику запрос на обслуживание
                // Запускаем Получение показаний весов от терминала
//                weightDataFromDeviceReader_Start();
                loader.serverServiceRequest();
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
        Toast toast
                = Toast.makeText(this, whatSay, Toast.LENGTH_LONG);
        toast
                .show();
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

    /**
     * @param serverToFind
     * @param wishServerIsFind
     */
    void sub1(String serverToFind, int wishServerIsFind) {
        logTAG = "sub1 ";
        // Закончить
        if (sfc.endServerFindCondition.get(wishServerIsFind) == true) {
            Beep();
            sfc.findServerTimer.get(wishServerIsFind).cancel();
        }
        /**
         * Условие выхода из цикла:
         * 1. Найден интересующий нас сервер.
         * 2. Закончился пул просматриваемых адресов.
         * 3. Абстрактный тайм-аут.
         */
        // 1. Найден сервер
        if (sfc.serverFound.get(wishServerIsFind)[NET_DEVICE_NOW] != null) {
            Log.i(logTAG, "serverFound=" + sfc.serverFound.get(wishServerIsFind)[NET_DEVICE_NAME]);
            if (sfc.serverFound.get(wishServerIsFind)[NET_DEVICE_NAME].equals(serverToFind)) {
                // Если сервер - тот, который мы ищем
                // Прекратить дальнейший поиск
                Log.i(getClass().getSimpleName(), "serverFound!!!" + sfc.serverFound.get(wishServerIsFind)[NET_DEVICE_NAME]);
                sfc.endServerFindCondition.set(wishServerIsFind, true);
                // Сохраняем данные в БД
                dbHandler.store_Device_Addr_to_DB(
                        conf.networkMask,
                        sfc.serverFound.get(wishServerIsFind)[NET_DEVICE_NAME],
                        sfc.serverFound.get(wishServerIsFind)[net.NET_DEVICE_ADDR]
                );
                // Адрес переносим в конфигурацию
                conf.ipAddress = sfc.serverFound.get(wishServerIsFind)[net.NET_DEVICE_ADDR];

                Log.i(getClass().getSimpleName(), "ПОИСК СЕРВЕРА ОСТАНОВЛЕН: " + wishServerIsFind);

                b_1000_0_Press();

            } else {
                // Сервер оказался не тот, который нужен, сбрасываем результат
                sfc.serverFound.get(wishServerIsFind)[NET_DEVICE_NOW] = null;
            }
        } else {
            // Сервера пока вообще нет
            // Проверяем, есть ли еще активные процессы срединения с сервером
            if (sfc.numOfServerPingClasses.get(wishServerIsFind) > 0) {
                // Пока поиск продолжаем
            } else {
                // Прекратить дальнейший поиск, т.к. все равно больше ничего не найдется
                sfc.endServerFindCondition.set(wishServerIsFind, true);
            }
        }
    }

    void toTextView(String textToTextView) {
        Log.i("toTextView", "CurrentLayout=" + CurrentLayout);
        if (textView[CurrentLayout] != null) {
            textView[CurrentLayout].setText(textToTextView);
        }
    }

    void serverFindResultToStatusLine(String serverFindResult) {
        toStatusLineNoBlink(serverFindResult);
    }

    /**
     * Заполняет массив лайаутов по новому образцу
     */
    void setNewLayout() {
        //
        L[0] = new LayoutClass(
                (LinearLayout) findViewById(R.id.layout_1010),
                new Timer[]
                        {
                                new Timer()                                             // Btn OK
                        },
                new TextView[]
                        {
                                (TextView) findViewById(R.id.textView_Header_1011),     // HEADER
                                (TextView) findViewById(R.id.textView_Header_1012)      // INFO
                        },
                new String[]
                        {
                                "L0: Первая строка",
                                "L0: Вторая строка"
                        },
                new Button[]
                        {
                                (Button) findViewById(R.id.b_1010_0),                   // NO
                                (Button) findViewById(R.id.b_1010_1),                   // YES
                        },
                new String[]
                        {
                                "OK",                                                   // Btn OK
                                "CANCEL"                                                // Btn CANCEL
                        }
        );


    }

    /**
     * Ситуация, когда не смогли получить данные с сервера
     */
    void errorReadDataFromServer() {
        sayToast("Нет подключения к серверу");
//        Beep();
//        toStatusLineBlink("Нет подключения к серверу");
        btn_4_Cancel.callOnClick();
    }

    /**
     * Распечатать содержимое "serverFound"
     */
    void printServerFound() {
        Log.i(logTAG, "PRINT serverFound");
        if (sfc.serverFound != null) {
            for (int i = 0; i < sfc.serverFound.size(); i++) {
                Log.i(logTAG, "serverFound[" + i + "]=" + sfc.serverFound.get(i)[NET_DEVICE_NAME]);
            }
        }
    }

    /**
     * Проверяет в serverFound есть ли в числе найденных указанный сервер
     *
     * @param serverName
     * @return
     */
    boolean ifServerFound(String serverName) {
        Log.i("ifServerFound", "serverFound.size()=" + sfc.serverFound.size());
        if (sfc.serverFound.size() > 0) {
            for (int i = 0; i < sfc.serverFound.size(); i++) {
                if (sfc.serverFound.get(i)[0] != null && sfc.serverFound.get(i)[NET_DEVICE_NAME].equals(serverName)) {
                    Log.i("ifServerFound", "serverFound[" + i + "]=" + sfc.serverFound.get(i)[NET_DEVICE_NAME]);
                    return true;
                }
            }
        }
        return false;
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


}
