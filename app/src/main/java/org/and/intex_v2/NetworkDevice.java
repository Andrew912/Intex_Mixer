package org.and.intex_v2;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.and.intex_v2.NetworkDevice.CommList.C1_IncreaseFreq;
import static org.and.intex_v2.NetworkDevice.CommList.C2_StartPing;
import static org.and.intex_v2.NetworkDevice.CommList.C3_StartSearch;
import static org.and.intex_v2.NetworkDevice.CommList.C4_DropWaitPingResult;
import static org.and.intex_v2.NetworkDevice.CommList.C5_DropWaitPingTimeout;
import static org.and.intex_v2.NetworkDevice.CommList.C6_DropWaitSearchResult;
import static org.and.intex_v2.NetworkDevice.CommList.C7_DropWaitSearchTimeout;
import static org.and.intex_v2.NetworkDevice.CommList.C8_StartOperation;
import static org.and.intex_v2.NetworkDevice.CommList.C9_SearchErrorReport;

/**
 * Created by and on 26.09.2018.
 * <p>
 * Управление поиском и подключением сетевого устройства:
 * сначала пытемся определить, есть ли устройство в сети по заданному адресу, если его по этому
 * адресу нет, то проводится поиск во всей подсети.
 * <p>
 * Параметры запуска:
 *
 * @param activity         - mainActivity
 * @param devParam         - параметры поиска (mask, addr, port, name, start, stop)
 * @param btnCallbackParam - кнопки возврата результата
 */

public class NetworkDevice {

    /* Разные служебные объекты */
    MainActivity
            mainActivity;
    String
            logTag = "NetworkDevice";
    static long
            timerPeriod = 500;
    Thread
            mainCycleThread;
    Timer
            mainCycleTimer;
    MainCycleTimerTask
            mainCycleTimerTask;
    NetworkDeviceActionClass
            networkDeviceActionClass;

    /* Параметры запуска сетевого поиска */
    String netMask;
    String devAddr;
    String devPort;
    String devName;
    String addrStart;
    String addrStop;

    /* Массив "Команды" */
    boolean Command[];

    /* Константы описания системы команд */
    public static final class CommList {
        static final int
                commandsNo = 10;
        static final int
                C1_IncreaseFreq = 0,
                C2_StartPing = 1,
                C3_StartSearch = 2,
                C4_DropWaitPingResult = 3,
                C5_DropWaitPingTimeout = 4,
                C6_DropWaitSearchResult = 5,
                C7_DropWaitSearchTimeout = 6,
                C8_StartOperation = 7,
                C9_SearchErrorReport = 8,
                C10_AbortReport = 9;
    }

    /* "Состояния" */
    public boolean States[];

    /* Индексы массива Состояний */
    public static final class StateList {
        static final int
                statesNo = 10;
        static final int
                S0_CheckStatus = 0,
                S2_WaitPingResult = 1,
                S3_CheckPingTimeout = 2,
                S4_WaitSearchResult = 3,
                S5_CheckSearchTimeout = 4,
                S6_Positive = 5,
                S7_Error = 6,
                S8_Busy = 7,
                S9_WaitTimerClock = 8,
                S10_OperationAborted = 9;
    }

    /* "Сообщения" */
    public boolean Messages[];

    /* Индексы массива Сообщений */
    public static final class MessageList {
        static final int
                messageNo = 15;
        static final int
                M0_PingNow = 0,
                M1_DropWaitPingTimeout = 1,
                M2_DropWaitPingResult = 2,
                M3_PingNone = 3,
                M4_SearchNow = 4,
                M5_DropWaitSearchTimeout = 5,
                M6_DropWaitSearchResult = 6,
                M7_SearchNone = 7,
                M8_CheckStatus = 8,
                M9_BeginCommandIncome = 9,
                M10_TimerSignal = 10,
                M11_BeginRequest = 11,
                M12_DropBusyStatus = 12,
                M13_AbortRequest = 13,
                M14_DropPingAndSearch = 14;
    }

    /* Индексы массива Переходов */
    public static final int
            T6_RunCheck = 0,
            T7_BeginRequest = 1,
            T0_NoCommand = 2,
            T1_GetBeginCommand = 3,
            T2_PingResultNow = 4,
            T3_PingResultNone = 5,
            T4_SearchResultNow = 6,
            T5_SearchResultNone = 7,
            E0_PingNow = 8,                 // Получен ответ от устройства
            E1_PingTimeout = 9,             // Таймаут получения пинга от устройства
            E2_SearchNow = 10,              // Устройство найдено
            E3_SearchTimeout = 11,          // Таймаут поиска устройства
            D0_DropWaitPing = 12,
            D1_DropWaitPingTimeout = 13,
            D2_DropWaitSearch = 14,
            D3_DropWaitSearchTimeout = 15,
            D4_DropBusyStatus = 16,
            D5_AbortPing = 17,
            D6_AbortSearch = 18,
            T8_AbortRequest = 19;

    /* Наименования переходов для всяких там логов и пр. */
    public static final String[] transitionNames = {
            "T6_RunCheck",
            "T7_BeginRequest",
            "T0_NoCommand",
            "T1_GetBeginCommand",
            "T2_PingResultNow",
            "T3_PingResultNone",
            "T4_SearchResultNow",
            "T5_SearchResultNone",
            "E0_PingNow",
            "E1_PingTimeout",
            "E2_SearchNow",
            "E3_SearchTimeout",
            "D0_DropWaitPing",
            "D1_DropWaitPingTimeout",
            "D2_DropWaitSearch",
            "D3_DropWaitSearchTimeout",
            "D4_DropBusyStatus",
            "D5_AbortPing",
            "D6_AbortSearch",
            "T8_AbortRequest"
    };

    /* Последнее значение Индекса переходов используется для определения размерности
    массива индексов переходов */
    public static final int transitionsNumber = T8_AbortRequest + 1;

    /**
     * События.
     * Переходы (Transitions), которые срабатывают по прошествии некоторого времени.
     * Процедура перехода запускается командой (С).
     * Срабатывание перехода фиксируется изменением значения элемента массива функцией,
     * вызываемой для обработки перехода.
     * Логическое значение, определяется индексом в массиве. Переменная-индекс именуется так же,
     * как имена переходов (Transitions)
     */
    public boolean Events[];

    /* Константы описания массива событий */
    public static final class EventList {
        static final int
                eventsNo = 4;
        public static final int
                E0_PingNow = 0,         // Получен ответ от устройства
                E1_PingTimeout = 1,     // Таймаут получения пинга от устройства
                E2_SearchNow = 2,       // Устройство найдено
                E3_SearchTimeOut = 3;   // Таймаут поиска устройства
    }

    /**
     * Сбросы.
     * Переходы (Т), предназначенные для сброса состяний (S).
     * Запускаются командой (С).
     */
    public boolean Drops[];

    public static final class DropList {
        static final int
                dropsNo = 7;
        static final int
                D0_DropWaitPing = 0,
                D1_DropWaitPingTimeout = 1,
                D2_DropWaitSearch = 2,
                D3_DropWaitSearchTimeout = 3,
                D4_DropBusyStatus = 4,
                D5_AbortPing = 5,
                D6_AbortSerch = 6;
    }

    /* Счетчик открываемых объектов-переходов */
    int currentTransitionId = 0;

    /* Массив переходов */
    TransitionClass transition[];

    /* Массив кнопок-колбэков */
    Button[]
            btnCallback;

    /* Массив тектовых полей статусной строки для вывода сообщений */
    TextView[]
            statusLine;

    /* Итог всего - параметры (имя, адрес, порт) устройства, по которым к нему
    можно подключиться */
    public String[]
            realDeviceParam = {null, null, null};

    /**
     * ПЕРЕХОДЫ
     * Переходы (Transitions) опеределяют изменение состояний при соблюдении заданных условий.
     * <p>
     * Условия:
     * ВХОДЯЩИЕ СОСТОЯНИЯ - состсяния, из которых доступен данный переход. Вообще-то входящее состояние
     * для перехода может быть только одно, но на всякий случай делаем их несколько, массив. Причем
     * сами состояния хранятся в массиве состояний States. Для анализа берется список индексов
     * (номеров) тех состояний, которые должны быть активны для срабатывания перехода. Поэтому
     * вычисление достаточно просто: взять массив индексов и проверить, чтобы состояния с этими
     * индексами все были True. Если так, то условие срабатывания перехода по состояниям выполнено.
     * <p>
     * ВХОДЯЩИЕ КОМАНДЫ - массив команд, при наличии которых сработает переход. Обработка аналогична
     * сотсояниям. Но в отличие от состояний команда после срабатывания перехода сбрасывается.
     * <p>
     * УСТАНОВИТЬ СОСТОЯНИЯ - состояния, которые должны быть установлены в результате срабатывания
     * перехода. Состояния устанавливаются явно для конкретного перехода. Массив индексов.
     * <p>
     * СБРОСИТЬ СОСТОЯНИЯ - состояния, которые должны быть сброшены. Массив индексов.
     * <p>
     * ЗАДАТЬ КОМАНДЫ - команды для выполнения по результатам перехода.
     */

    /**
     * TransitionClass - описание параметров перехода. Концепция - см. выше.
     */
    class TransitionClass {

        int id;                     // Идентификатор объекта
        int no;                     // Номер перехода (для отладки, потом можно будет удалить)
        boolean invisible;          // Не рассчитывается

        int priority;               // Приоритет расчета, число от 0 до 100. Больше приоритет - раньше исполнение
        int[] stateGetIndex;        // Массив индексов массива Состояний (States) для анализа
        int[] stateIngIndex;        // Массив индексов массива Состояний (States) - ингибиторов
        int[] messageGetIndex;      // Массив индексов массива Сообщений (Message) для анализа
        int[] eventGetIndex;        // Массив индексов массива Событий   (Events) для анализа
        int[] stateSetIndex;        // Массив индексов массива Состояний (States) для установки
        int[] stateDropIndex;       // Массив индексов массива Состояний (States) для сброса
        int[] messageSetIndex;      // Массив индексов массива Сообщений (Message) для отправки
        int[] commandDoIndex;       // Массив индексов массива Команд    (Command) для исполнения

        /* Классы PING и FIND устройства */
        Thread
                pingDeviceThread;

        /* Таймеры таймаута PING и FIND */
        Timer
                pingTimeoutTimer,
                findTimeoutTimer;


        /**
         * Конструктор 0 (по умолчанию)
         * <p>
         * Приоритет по умолчанию = 0
         *
         * @param StateGet
         * @param MessageGet
         * @param EventGet
         * @param StateSet
         * @param StateDrop
         * @param MessageSet
         * @param CommandDo
         */
        public TransitionClass(
                int[] StateGet,
                int[] StateIng,
                int[] EventGet,
                int[] MessageGet,
                int[] StateSet,
                int[] StateDrop,
                int[] MessageSet,
                int[] CommandDo,
                int pNo
        ) {
            /* Задать начальные условия */
            id
                    = currentTransitionId++;
            no
                    = pNo;
            priority
                    = 0;
            invisible
                    = false;
            stateGetIndex
                    = StateGet;
            stateIngIndex
                    = StateIng;
            messageGetIndex
                    = MessageGet;
            eventGetIndex
                    = EventGet;
            stateSetIndex
                    = StateSet;
            stateDropIndex
                    = StateDrop;
            messageSetIndex
                    = MessageSet;
            commandDoIndex
                    = CommandDo;
        }

        /**
         * Конструктор 2 (c признаком отмены вычисления перехода)
         * <p>
         * Приоритет по умолчанию = 0
         *
         * @param StateGet
         * @param MessageGet
         * @param EventGet
         * @param StateSet
         * @param StateDrop
         * @param MessageSet
         * @param CommandDo
         * @param pNo
         * @param needed
         */
        public TransitionClass(
                int[] StateGet,
                int[] StateIng,
                int[] EventGet,
                int[] MessageGet,
                int[] StateSet,
                int[] StateDrop,
                int[] MessageSet,
                int[] CommandDo,
                int pNo,
                boolean needed
        ) {
            /* Задать начальные условия */
            id
                    = currentTransitionId++;
            no
                    = pNo;
            priority
                    = 0;
            invisible
                    = needed;
            stateGetIndex
                    = StateGet;
            stateIngIndex
                    = StateIng;
            messageGetIndex
                    = MessageGet;
            eventGetIndex
                    = EventGet;
            stateSetIndex
                    = StateSet;
            stateDropIndex
                    = StateDrop;
            messageSetIndex
                    = MessageSet;
            commandDoIndex
                    = CommandDo;
        }

        /**
         * Конструктор 1 (Приоритет расчета задается явно)
         *
         * @param StateGet
         * @param MessageGet
         * @param EventGet
         * @param StateSet
         * @param StateDrop
         * @param MessageSet
         * @param CommandDo
         * @param Priority   приоритет расчета
         */
        public TransitionClass(
                int[] StateGet,
                int[] StateIng,
                int[] EventGet,
                int[] MessageGet,
                int[] StateSet,
                int[] StateDrop,
                int[] MessageSet,
                int[] CommandDo,
                int Priority,
                int pNo
        ) {
            /* Задать начальные условия */
            id
                    = currentTransitionId++;
            no
                    = pNo;
            priority
                    = Priority;
            invisible
                    = false;
            stateGetIndex
                    = StateGet;
            stateIngIndex
                    = StateIng;
            messageGetIndex
                    = MessageGet;
            eventGetIndex
                    = EventGet;
            stateSetIndex
                    = StateSet;
            stateDropIndex
                    = StateDrop;
            messageSetIndex
                    = MessageSet;
            commandDoIndex
                    = CommandDo;
        }

        /**
         * Расчет параметров перехода и обработка событий перехода
         */
        void calc(int tName) {
            if (statesAreOk() && messagesAreOk(tName) && eventsAreOk() && ingibitorStatesAreOk()) {

                if (tName == T1_GetBeginCommand) {
                    Log.i("Calc", "== T1 ==");
                }

                Log.i("CALC", "Transiton=" + transitionNames[tName]);

                /* Установить состояния */
                setTargetStates();

                /* Сбросить состояния */
                dropTargetStates();

                /* Установить сообщения */
                setTargetMessages();

                /* Выполнить команды */
                runCommands();

                /* Очистить события и сообщения */
                clearEventsNMessages();
            }
            Log.i("CALC", "\n");
        }

        /**
         * Анализ состояний-ингибиторов. Все должны быть "ложь", чтобы сработал переход
         *
         * @return
         */
        boolean ingibitorStatesAreOk() {
            if (stateIngIndex == null) return true;
            int i = 0;
            while (i < stateIngIndex.length) {
                if (States[stateIngIndex[i]] == false) {
                    i++;
                } else
                    return false;
            }
            return true;
        }

        /**
         * Анализ входящих состояний, Все указанные состояни должны быть True
         *
         * @return
         */
        boolean statesAreOk() {
            if (stateGetIndex == null) return true;
            for (int i = 0; i < stateGetIndex.length; i++)
                if (!States[stateGetIndex[i]]) return false;
            return true;
        }

        /**
         * Анализ входящих сообщений, Все указанные сообщения должны быть True
         *
         * @return
         */
        boolean messagesAreOk(int tName) {
//            if (tName == T1_GetBeginCommand) {
//                Log.i("Calc", "T1");
//            }
//
            if (messageGetIndex == null) return true;
            int i;
            for (i = 0; i < messageGetIndex.length; i++)
                if (!Messages[messageGetIndex[i]]) return false;
//            /* Сбросить отработанные сообщения */
//            for (i = 0; i < messageGetIndex.length; i++) Messages[messageGetIndex[i]] = false;
            return true;
        }

        /**
         * Анализ событий, все указанные события должны быть True
         *
         * @return
         */
        boolean eventsAreOk() {
            if (eventGetIndex == null) return true;
            int i;
            for (i = 0; i < eventGetIndex.length; i++)
                if (!Events[eventGetIndex[i]]) return false;
//            /* Сбросить отработанное состояние */
//            for (i = 0; i < eventGetIndex.length; i++) Events[eventGetIndex[i]] = false;
            return true;
        }

        /**
         * Устанавливает указанные Сообщения
         * <p>
         * Messages        - массив Сообщений
         * MessageSetIndex - массив индексов Сообщений в Messages, которые должны быть установлены
         */
        void setTargetMessages() {
            if (messageSetIndex != null)
                for (int i = 0; i < messageSetIndex.length; i++) {
                    Messages[messageSetIndex[i]] = true;
                }
        }

        /**
         * Устанавливает указанные состояния
         * <p>
         * States   - массив состояний
         * stateSetIndex - массив (список) состояний (индексов в States), которые должны быть установлены
         */
        void setTargetStates() {
            if (stateSetIndex != null)
                for (int i = 0; i < stateSetIndex.length; i++) {
                    States[stateSetIndex[i]] = true;
                }
        }

        /**
         * Сбрасывает указанные состояния
         */
        void dropTargetStates() {
            if (stateDropIndex != null)
                for (int i = 0; i < stateDropIndex.length; i++) {
                    States[stateDropIndex[i]] = false;
                }
        }

        /**
         * Выполняет команды, заданные для перехода
         */
        void runCommands() {
            if (commandDoIndex != null)
                for (int i = 0; i < commandDoIndex.length; i++) {
                    runCommand(commandDoIndex[i]);
                }
        }

        /**
         * Выполняет конкретную команду
         */
        void runCommand(int commandId) {

            Command[commandId] = true;

            switch (commandId) {
                case C1_IncreaseFreq:
                    break;
                case C2_StartPing:
                    /* Запущен таймер таймаута пинга */
                    pingTimeoutTimer = new Timer();
                    pingTimeoutTimer.schedule(new PingTimeoutTimerTask(), 5000);
                    /* Запускаем ping устройства */
                    Log.i(logTag, "\nPING:\n" + devName + "\n" + netMask + devAddr + "\n" + devPort + "\n");
                    pingDeviceThread = networkDeviceActionClass.pingDevice(
                            new String[]{
                                    netMask + devAddr,
                                    devPort,
                                    devName});
                    break;
                case C3_StartSearch:
                    /* Запускаем поиск устройства */
                    Log.i(logTag, "\nFIND:\n" + devName + "\n" + netMask + devAddr + "\n" + devPort + "\n");
                    networkDeviceActionClass.findDevice(
                            new String[]{
                                    netMask,
                                    devPort,
                                    devName,
                                    addrStart,
                                    addrStop});
                    break;
                case C4_DropWaitPingResult:
                    /* Останавливаем задачу PING */
                    stopPingTask(pingDeviceThread);
                    break;
                case C5_DropWaitPingTimeout:
                    /* Останавливаем таймер таймаута пинга */
                    if (pingTimeoutTimer != null) pingTimeoutTimer.cancel();
                    break;
                case C6_DropWaitSearchResult:
                    /* Останавливаем задачу FIND */

                    break;
                case C7_DropWaitSearchTimeout:
                    /* Останавливаем таймер таймаута FIND */

                    break;
                case C8_StartOperation:
                    /* Устройство найдено так или иначе, вернуть адрес для дальнейшей обработки */
                    pressCallbackButton(0);
                    break;
                case C9_SearchErrorReport:
                    /* Устройство не найдено, реакция дложна быть соответственная */
                    pressCallbackButton(1);
                    break;
            }
        }

        /* Останавливаем (убиваем) задачу PING */
        void stopPingTask(Thread threadToKill) {
            if (pingTimeoutTimer != null) {
                pingTimeoutTimer.purge();
                pingTimeoutTimer.cancel();
            }
            if (threadToKill != null) {
                Thread dummy = threadToKill;
                threadToKill = null;
                dummy.interrupt();
            }
            if (threadToKill == null) {
                Log.i(logTag, "=== Thread killed ===");
            } else
                Log.i(logTag, "=== Thread LIVE ===");
        }

        /* Действия по таймауту PING */
        class PingTimeoutTimerTask extends TimerTask {

            @Override
            public void run() {
                setEvent(NetworkDevice.EventList.E1_PingTimeout);
            }
        }

        /**
         * Очистка событий и сообщений
         */
        void clearEventsNMessages() {
            int i;

            /**/
            if (eventGetIndex != null)
                for (i = 0; i < eventGetIndex.length; i++)
                    Events[eventGetIndex[i]] = false;

            /**/
            if (messageGetIndex != null)
                for (i = 0; i < messageGetIndex.length; i++)
                    Messages[messageGetIndex[i]] = false;

            /* Сбросить входящие команды */
            if (commandDoIndex != null)
                for (i = 0; i < commandDoIndex.length; i++)
                    Command[commandDoIndex[i]] = false;
        }

    }

    public static class MessageMakerLocal {
        /**
         * Сообщения весовому терминалу
         */
        final class Terminal {
            /**
             * PING
             *
             * @return
             */
            public String ping() {
                return "who\n";
            }
        }
    }


    /**
     * Created by and on 06.09.2018.
     * <p>
     * Для поиска устройства в сети.
     * <p>
     * Задается:
     * имя устройства,
     * подсеть,
     * порт.
     * <p>
     * Определяется:
     * IP-адрес устройства.
     * <p>
     * Определяется путем пребора адресов в подсети.
     * Адрес, с которого придет ответ, содержащий звдвнное имя устройства, является адресом устройства.
     */
    public class NetworkDeviceActionClass {
        /* Параметры */
        String logTag
                = "Device Ping Class";
        MainActivity
                mainActivity;
        LinearLayout
                mainSwitch;

        //        /* Тип сообщения - неизвестен, ответ устройства на пинг и пр. */
//        enum MsgType {
//            unknown,
//            devicePingResponse
//        }
//
        /* Имя найденного устройства */
        public String
                name;

        /* Адрес устройства */
        public String
                address;

        /*  */
        MyMessageReader
                myMsgReader;

        /**
         * Конструктор класса NetworkDeviceActionClass
         *
         * @param activity
         */
        public NetworkDeviceActionClass(MainActivity activity) {
            mainActivity = activity;
            myMsgReader = new MyMessageReader();
        }

        /**
         * Делает один пинг устройства с заданным адресом и портом.
         * Параметры:
         * 1. IP-адрес
         * 2. Порт
         * 3. Имя устройства
         *
         * @param deviceParams
         * @return DeviceSinglePingTaskClass - чтобы можно было его остановить
         */
        public Thread pingDevice(
                final String[] deviceParams) {
            Thread thread;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    /* Вызов того, что надо вызвать */
                    new DeviceSinglePingTaskClass().execute(deviceParams);
                }
            };
            thread = new Thread(runnable);
            thread.start();
            return thread;
        }

        /**
         * Делает поиск устройства с заданным адресом и портом.
         * Параметры:
         * 1. IP-адрес
         * 2. Порт
         * 3. Имя устройства
         *
         * @param deviceParams
         */
        public void findDevice(
                final String[] deviceParams) {

            Thread thread;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                /* Вызов того, что надо вызвать */
                    new DeviceFindTaskClass().execute(deviceParams);

//                    mainActivity.networkDevice.setEvent(NetworkDevice.EventList.E3_SearchTimeOut);
                }
            };
            thread = new Thread(runnable);
            thread.start();
        }

        /**
         * Остановить поток
         *
         * @param thread
         */
        void threadStop(Thread thread) {
        /* Остановили поток выполнения, чтобы не мешал */
            if (thread != null) {
                Thread dummy = thread;
                thread = null;
                dummy.interrupt();
            }
        }

        /**
         * Непосредственно выполняет одиночный пинг.
         * Параметры:
         * 1. Маска подсети.
         * 2. Порт.
         * 3. Имя устройства, которое ищем.
         */
        class DeviceFindTaskClass extends AsyncTask<String, Void, String> {
            int buffSize = 128;

            /**
             * Действия в фоновом режиме
             *
             * @param params
             * @return
             */
            @Override
            protected String doInBackground(String... params) {
                Socket
                        socket;
                InputStream
                        inputStream;
                OutputStream
                        outputStream;
                boolean                         // Параметр для цикла: Продолжать поиск
                        whileContinue = true;
                String                          // Текущий адрес, по которому ведем поиск
                        ipAddress;
                /* Блок с поиском "вверх" */
//            int                             // Счетчик адресов при поиске, задаем из параметра 3
//                    addressCounter = Integer.parseInt(params[3]);
//            int                             // До какого адреса смотреть, задаем из параметра 4
//                    addressLimit = Integer.parseInt(params[4]);
                /* Блок с поиском "вниз" */
                int                             // Счетчик адресов при поиске, задаем из параметра 4
                        addressCounter = Integer.parseInt(params[4]);
                int                             // До какого адреса смотреть, задаем из параметра 3
                        addressLimit = Integer.parseInt(params[3]);
                while (whileContinue) {
                    ipAddress = params[0] + Integer.toString(addressCounter);
                    final String finalIpAddress = ipAddress;
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /* Выводим строку сообщения на главный экран */
                            statusLine[0].setText(finalIpAddress);
                        }
                    });
                    /* Пытаемся открыть соединение и провести обмен строками */
                    try {
                        socket
                                = new Socket(ipAddress, Integer.parseInt(params[1]));
                        inputStream
                                = socket.getInputStream();
                        outputStream
                                = socket.getOutputStream();
                        /* Отправка строки байт. Строку берем прямо из MessageMakerLocal */
                        byte[] buffer = ping().getBytes();
                        outputStream.write(buffer);
                        outputStream.flush();
                        /* Получение строки байт */
                        buffer = new byte[buffSize];
                        int read = inputStream.read(buffer, 0, buffSize);
                        byte[] b = new byte[read];
                        System.arraycopy(buffer, 0, b, 0, read);
                        /* Получили строку - ответ устройтства */
                        myMsgReader.readMsg(new String(b));
                        Log.i(logTag, "myMsgReader.name=" + myMsgReader.name + ", params[2]=" + params[2]);
                        /* Если имя устройства совпало с тем, которое ищем, то запишем адрес устройства */
                        if (myMsgReader.name.equals(params[2].toString())) {
                            /* И тут сразу записываем данные для возврата */
                            realDeviceParam[0] = name = myMsgReader.name;
                            realDeviceParam[1] = address = ipAddress;
                            realDeviceParam[2] = params[1];
                            whileContinue = false;
                            Log.i(logTag, ipAddress + ": " + name);
                            /* Возвращаем для onPostExecute имя устройства */
                            return myMsgReader.name;
                        } else {
//                            return null;
                            Log.i(logTag, ipAddress + ": not found");
                        }
                    } catch (Exception e) {
                        Log.i(logTag, ipAddress + ": not found");
                    }
                    if (addressCounter-- == addressLimit) return null;
                }
            /* Возвращаем для onPostExecute имя устройства */
                return null;
            }

            /**
             * Действия после выполнения обмена данными
             *
             * @param result
             */
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                /* Если найдено необходимое устройство */
                if (name != null)
                    setEvent(NetworkDevice.EventList.E2_SearchNow);
                else
                    setEvent(NetworkDevice.EventList.E3_SearchTimeOut);

                /* Фиксируем параметры найденного устройства */

                Log.i("***** onPostExecute", "name= " + name);

            }
        }

        /**
         * Непосредственно выполняет одиночный пинг
         */
        class DeviceSinglePingTaskClass extends AsyncTask<String, Void, String> {
            int buffSize = 128;

            /**
             * Действия в фоновом режиме
             *
             * @param params
             * @return
             */
            @Override
            protected String doInBackground(String... params) {
                Socket
                        socket;
                InputStream
                        inputStream;
                OutputStream
                        outputStream;
                /* Пытаемся открыть соединение и провести обмен строками */
                try {
                    socket
                            = new Socket(params[0], Integer.parseInt(params[1]));
                    inputStream
                            = socket.getInputStream();
                    outputStream
                            = socket.getOutputStream();
                    /* Отправка строки байт. Строку берем прямо из MessageMakerLocal */
                    byte[] buffer = ping().getBytes();
                    outputStream.write(buffer);
                    outputStream.flush();
                    /* Получение строки байт */
                    buffer = new byte[buffSize];
                    int read = inputStream.read(buffer, 0, buffSize);
                    byte[] b = new byte[read];
                    System.arraycopy(buffer, 0, b, 0, read);
                    /* Получили строку - ответ устройтства */
                    myMsgReader.readMsg(new String(b));
                    Log.i(logTag, "myMsgReader.name=" + myMsgReader.name + ", params[2]=" + params[2]);
                    /* Если имя устройства совпало с тем, которое ищем, то запишем адрес устройства */
                    if (myMsgReader.name.equals(params[2].toString())) {
                        /* Данные для возврата */
                        realDeviceParam[0] = name = myMsgReader.name;
                        realDeviceParam[1] = address = params[0];
                        realDeviceParam[2] = params[1];
                        return myMsgReader.name;
                    } else
                        return null;
                } catch (Exception e) {
                    Log.i(logTag, "NOT connected!!!");
                    return null;
                }
            /* Возвращаем для onPostExecute имя устройства */
            }

            /**
             * Действия после выполнения обмена данными
             *
             * @param result
             */
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (name != null)
                    setEvent(NetworkDevice.EventList.E0_PingNow);
                else
                    setEvent(NetworkDevice.EventList.E1_PingTimeout);
                Log.i("*****", "name= " + name);
            }
        }

        /**
         *
         */
        public final class MyMessageReader {

            private String logTag = "Class MyMessageReader";
            /* Сообщение */
            Msg msg;

            /*  */
            String name;

            /* Паттерны разбора входящего сообщения */
            String patternCmdValue = "[\\w\\s\\@.:;%#/\\^\\?\\(\\)\\[\\]\\|\\/\\+\\-\\*]+";
            String patternEndOfLine = "\n";
            String patternCmdName = "\\w+";

            /**
             * Пара "ключ-значение" (КЗ) из сообщения
             */
            class MsgPair {
                String key;
                String value;

                public MsgPair(String key, String value) {
                    this.key = key;
                    this.value = value;
                }
            }

            /**
             * Сообщение, которое подвергается обработке. Со всеми его приблудами.
             * При создании класса параметром передается строка - исходное сообщение.
             * Она разбирается на пары "КЗ" в MsgBody.
             */
            class Msg {
                boolean
                        empty;          /* Пустое собщение */
                //                MsgType
//                        msgType;        /* Тип сообщения */
                ArrayList<MsgPair>
                        msgBody;        /* Здесь будут после разбора содержаться пары "КЗ" конкретного сообщения */
                String
                        deviceName;     /* Имя устройства (если устройство его сообщило) */

                /**
                 * Конструктор
                 *
                 * @param source
                 */
                public Msg(String source) {
                    msgBody = new ArrayList<>();
                    parceMsg(source);
                    empty = msgBody.isEmpty();
                    if (!empty) analizeMsg();
                }

                /**
                 * Анализирует сообщение на предмет его назначения, типа и пр.
                 * на основании имен ключей и их значений
                 */
                void analizeMsg() {
//                    msgType = MsgType.unknown;
                    deviceName = null;
                    for (MyMessageReader.MsgPair msgPair : msgBody
                            ) {
                        switch (msgPair.key) {
                            case "server":
//                                msgType = MsgType.devicePingResponse;
                                name = msgPair.value;
                                break;
                            default:
                                break;
                        }

                        Log.i(logTag, "key=" + msgPair.key + ", value=" + msgPair.value);
                    }
                }

                /**
                 * Разбирает строку на строки с парами "КЗ".
                 */
                void parceMsg(String source) {
                    // Выделяем из исходной строки отдельные команды
                    Pattern pattern
                            = Pattern.compile(patternCmdName + "=\'" + patternCmdValue + "\'");
                    Matcher matcher
                            = pattern.matcher(source);
                    while (matcher.find()) {
                        msgBody.add(extractParam(matcher.group()));
                    }
                }

                /**
                 * Извлекает пары "КЗ"
                 *
                 * @param inputString
                 * @return
                 */
                private MsgPair extractParam(String inputString) {
                    MsgPair returnValue
                            = null;
                    Pattern patternOfName
                            = Pattern.compile("^" + patternCmdName);
                    Matcher matcherOfName
                            = patternOfName.matcher(inputString);
                    if (matcherOfName.find()) {
                        Pattern patternOfValue
                                = Pattern.compile("=\'" + patternCmdValue + "\'$");
                        Matcher matcherOfValue
                                = patternOfValue.matcher(inputString);
                        if (matcherOfValue.find()) {
                            returnValue = new MsgPair(matcherOfName.group(), matcherOfValue.group().replace("'", "").replace("=", ""));
                        }
                    }
                    return returnValue;
                }

                /**
                 * Добавляет пару "КЗ" в массив для данного сообщения
                 */
                void addPair(String key, String value) {
                    msgBody.add(new MsgPair(key, value));
                }
            }
    /*
     * Блок "специальных" функций, которые возвращают конкретные параметры, если они есть в
     * полученном сообщении - имя сервера, показания терминала, версия протокола и пр.
     */

            /**
             * "Читает" новую строку. И проводит ее разбор.
             * При чтении старая строка затирается.
             * Тут надо подумать, как с транслятором будут работать несколько абонентов.
             */
            void readMsg(String source) {
                msg = new Msg(source);
            }
        }

        /* Формирует строку PING для отправки */
        public String ping() {
            return "who\n";
        }
    }

    /**
     * Конструктор 0 - все параметры отдельно
     *
     * @param activity
     * @param pNetMask
     * @param pDevAddr
     * @param pDevPort
     * @param pDevName
     * @param pAddrStart
     * @param pAddrStop
     */
    public NetworkDevice(
            MainActivity activity,
            String pNetMask,
            String pDevAddr,
            String pDevPort,
            String pDevName,
            String pAddrStart,
            String pAddrStop
    ) {
        mainActivity = activity;

        /* Заполнение параметров поиска сетевых устройств */
        netMask
                = pNetMask;
        devAddr
                = pDevAddr;
        devPort
                = pDevPort;
        devName
                = pDevName;
        addrStart
                = pAddrStart;
        addrStop
                = pAddrStop;

        /* Инициализация всего */
        init();

        /* Теперь из MainActivity в нужный момент надо запустить startProc()... */
    }

    /**
     * Конструктор 1 - параметры передаются массивами (строк и других объектов)
     *
     * @param activity         - mainActivity
     * @param devParam         - параметры поиска (mask, addr, port, name, start, stop)
     * @param btnCallbackParam - кнопки возврата результата
     */
    public NetworkDevice(
            MainActivity activity,
            String[] devParam,          // Параметры поиска устройства: Маска, Адрес, Порт, Имя, Старт, Стоп
            TextView[] pStatusLine,     // Вывод сообщений на главный экран
            Button[] btnCallbackParam   // Кнопки команд на главном экране - самый простой колбэк
    ) {
        mainActivity = activity;

        /* Заполнение параметров поиска сетевых устройств */
        netMask
                = devParam[0];
        devAddr
                = devParam[1];
        devPort
                = devParam[2];
        devName
                = devParam[3];
        addrStart
                = devParam[4];
        addrStop
                = devParam[5];

        /* Инициализация массива кнопок-колбэков для возврата управления в вызывающий модуль */
        btnCallback = btnCallbackParam;

        /* Инициализация массива TextView для вывода на главный экран */
        statusLine = pStatusLine;

        /* Инициализация всего */
        init();

        /* Объект существует и живет своей жизнью */
        /* Теперь из MainActivity в нужный момент надо запустить startProc()... */
    }

    /**
     * "Нажимает" кнопку колбэка
     */
    void pressCallbackButton(final int btnNo) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnCallback[btnNo].callOnClick();
            }
        });
    }

    /**
     * Запуск главного потока (из MainActivity) - непосредственно процедура поиска
     */
    public void startProc() {
        /* Подготовка основного цикла */
        mainCycleTimer
                = new Timer();
        mainCycleTimerTask
                = new MainCycleTimerTask();

        mainCycleThread
                = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                    /* Запуск главного цикла */
                        Log.i("NetworkDevice", "Start main cycle");
                        mainCycleTimer.schedule(mainCycleTimerTask, 0, timerPeriod);
                    }
                });
        /* Стартуем сразу */
        setMessage(MessageList.M11_BeginRequest);
        mainCycleThread.start();
    }

    /**
     * Инициализация различных параметров и переменных
     */
    void init() {
        /* Инициализация массива команд */
        Command = new boolean[CommList.commandsNo];
        for (int i = 0; i < CommList.commandsNo; i++)
            Command[i] = false;

        /* Инициализация массива состояний */
        States = new boolean[StateList.statesNo];
        for (int i = 0; i < StateList.statesNo; i++)
            States[i] = false;
        /* Начальное состояние - ожидание сигнала таймера */
        States[StateList.S9_WaitTimerClock] = true;

        /* Инициализация массива Сообщений */
        Messages = new boolean[MessageList.messageNo];
        for (int i = 0; i < MessageList.messageNo; i++)
            Messages[i] = false;

        /* Инициализация массива событий */
        Events = new boolean[EventList.eventsNo];
        for (int i = 0; i < EventList.eventsNo; i++)
            Events[i] = false;

        /* Инициализация массива сбросов ??? */
        Drops = new boolean[DropList.dropsNo];
        for (int i = 0; i < DropList.dropsNo; i++)
            Drops[i] = false;

        /* Загрузка параметров переходов */
        transition = new TransitionClass[transitionsNumber];

        /* Загрузка данных для расчета переходов */
        transitionsLoad();

        /* Установка старотовых значений параметров*/
        setInitialValues();

        /* Класс обработки взаимодействия с сетевым устройством */
        networkDeviceActionClass =
                new NetworkDeviceActionClass(mainActivity);
    }

    /**
     * Задача твймера главного цикла
     */
    class MainCycleTimerTask extends TimerTask {

        @Override
        public void run() {
            /* Главная вызываемая процедура */
            main();
        }
    }

    /**
     * Главная вызываемая процедура - Вызывается из задачи таймера главного цикла
     */
    void main() {

        Log.i("MAIN", "*** STEP ***");

        setMessage(MessageList.M10_TimerSignal);

        for (int i = 0; i < transitionsNumber; i++) {
            transition[i].calc(i);
        }
    }

    /**
     * Устанавливает значения парметров найденного устройства для возврата вызывающему
     */
    void setRealDeviceParam(String[] devParameters) {
        realDeviceParam[0] = devParameters[0];
        realDeviceParam[1] = devParameters[1];
        realDeviceParam[2] = devParameters[2];
    }

    /**
     * Вызывается вместо класса задачи таймера при нажатии кнопки Степ руками. Для отладки
     */
    void debugStep() {
        main();
    }

    /**
     * Загрузка стартовых параметров
     */
    void setInitialValues() {
    }

    /**
     * Загрузка данных для расчета переходов
     */
    void transitionsLoad() {

         /* S.0 -> T0_NoCommand */
        transition[T0_NoCommand] = new TransitionClass(
                new int[]{                                  // check    S.0
                        StateList.S0_CheckStatus},
                null,                                       // check    S ingibitors
                null,                                       // check    E'
                new int[]{                                  // check    M'
                        MessageList.M8_CheckStatus},
                new int[]{                                  // set      S'
                        StateList.S9_WaitTimerClock},
                new int[]{                                  // drop     S'
                        StateList.S0_CheckStatus},
                null,                                       // set      M'
                null,                                       // set      C'
                50,
                T0_NoCommand
        );

       /* S.9 -> T6_RunCheck */
        transition[T6_RunCheck] = new TransitionClass(
                new int[]{                                  // check    S'
                        StateList.S9_WaitTimerClock},
                null,                                       // check    S ingibitors
                null,                                       // check    E'
                new int[]{                                  // check    M'
                        MessageList.M10_TimerSignal},
                new int[]{                                  // set      S'
                        StateList.S0_CheckStatus},
                new int[]{                                  // drop     S'
                        StateList.S9_WaitTimerClock},
                new int[]{                                  // set      M'
                        MessageList.M8_CheckStatus},
                null,                                       // set      C'
                T6_RunCheck
        );

        /* S.8 -> T7_BeginRequest */
        transition[T7_BeginRequest] = new TransitionClass(
                null,                                       // check    S'
                new int[]{                                  // check    S ingibitors
                        StateList.S8_Busy},
                null,                                       // check    E'
                new int[]{                                  // check    M'
                        MessageList.M11_BeginRequest},
                null,                                       // set      S'
                null,                                       // drop     S'
                new int[]{                                  // set      M'
                        MessageList.M9_BeginCommandIncome},
                new int[]{                                  // set      C'
                        C1_IncreaseFreq},
                T7_BeginRequest
        );

        /*  -> T8_AbortRequest */
        transition[T8_AbortRequest] = new TransitionClass(
                null,                                       // check    S'
                null,                                       // check    S ingibitors
                null,                                       // check    E'
                new int[]{                                  // check    M'
                        MessageList.M13_AbortRequest},
                null,                                       // set      S'
                null,                                       // drop     S'
                new int[]{                                  // set      M'
                        MessageList.M12_DropBusyStatus,
                        MessageList.M14_DropPingAndSearch},
                new int[]{                                  // set      C'
                        CommList.C10_AbortReport},
                T8_AbortRequest
        );

        /* S.0 -> T1_GetBeginCommand */
        transition[T1_GetBeginCommand] = new TransitionClass(
                new int[]{                                  // check    S'
                        StateList.S0_CheckStatus},
                null,                                       // check    S ingibitors
                null,                                       // check    E'
                new int[]{                                  // check    M'
                        MessageList.M8_CheckStatus,
                        MessageList.M9_BeginCommandIncome},
                new int[]{                                  // set      S'
                        StateList.S9_WaitTimerClock,
                        StateList.S8_Busy,
                        StateList.S2_WaitPingResult,
                        StateList.S3_CheckPingTimeout},
                new int[]{                                  // drop     S'
                        StateList.S0_CheckStatus},
                null,                                       // set      M'
                new int[]{                                  // set      C'
                        C2_StartPing},
                T1_GetBeginCommand
        );

        /* S.0 -> T2_PingResultNow */
        transition[T2_PingResultNow] = new TransitionClass(
                new int[]{                                  // check    S'
                        StateList.S0_CheckStatus},
                null,                                       // check    S ingibitors
                null,                                       // check    E'
                new int[]{                                  // check    M'
                        MessageList.M8_CheckStatus,
                        MessageList.M0_PingNow},
                new int[]{                                  // set      S'
                        StateList.S6_Positive,
                        StateList.S9_WaitTimerClock},
                new int[]{                                  // drop     S'
                        StateList.S0_CheckStatus},
                new int[]{                                  // set      M'
                        MessageList.M12_DropBusyStatus},
                new int[]{                                  // set      C'
                        C8_StartOperation},
                T2_PingResultNow
        );

        /* S.0 -> T3_PingResultNone */
        transition[T3_PingResultNone] = new TransitionClass(
                new int[]{                                  // check    S'
                        StateList.S0_CheckStatus},
                null,                                       // check    S ingibitors
                null,                                       // check    E'
                new int[]{                                  // check    M'
                        MessageList.M3_PingNone,
                        MessageList.M8_CheckStatus},
                new int[]{                                  // set      S'
                        StateList.S9_WaitTimerClock,
                        StateList.S4_WaitSearchResult,
                        StateList.S5_CheckSearchTimeout},
                new int[]{                                  // drop     S'
                        StateList.S0_CheckStatus},
                null,                                       // set      M'
                new int[]{                                  // set      C'
                        C3_StartSearch},
                T3_PingResultNone
        );

        /* S.0 -> T4_SearchResultNow */
        transition[T4_SearchResultNow] = new TransitionClass(
                new int[]{                                  // check    S'
                        StateList.S0_CheckStatus},
                null,                                       // check    S ingibitors
                null,                                       // check    E'
                new int[]{                                  // check    M'
                        MessageList.M4_SearchNow,
                        MessageList.M8_CheckStatus},
                new int[]{                                  // set      S'
//                        StateList.S9_WaitTimerClock,
                        StateList.S6_Positive},
                new int[]{                                  // drop     S'
                        StateList.S0_CheckStatus},
                new int[]{                                  // set      M'
                        MessageList.M12_DropBusyStatus},
                new int[]{                                  // set      C'
                        C8_StartOperation},
                T4_SearchResultNow
        );

        /* S.0 -> T5_SearchResultNone */
        transition[T5_SearchResultNone] = new TransitionClass(
                new int[]{                                  // check    S'
                        StateList.S0_CheckStatus},
                null,                                       // check    S ingibitors
                null,                                       // check    E'
                new int[]{                                  // check    M'
                        MessageList.M7_SearchNone,
                        MessageList.M8_CheckStatus},
                new int[]{                                  // set      S'
//                        StateList.S9_WaitTimerClock,
                        StateList.S7_Error},
                new int[]{                                  // drop     S'
                        StateList.S0_CheckStatus},
                new int[]{                                  // set      M'
                        MessageList.M12_DropBusyStatus},
                new int[]{                                  // set      C'
                        C9_SearchErrorReport},
                T5_SearchResultNone
        );

        /* S.2 -> E0_PingNow */
        transition[E0_PingNow] = new TransitionClass(
                new int[]{                                  // check    S'
                        StateList.S2_WaitPingResult},
                null,                                       // check    S ingibitors
                new int[]{                                  // check    E'
                        EventList.E0_PingNow},
                null,                                       // check    M'
                null,                                       // set      S'
                new int[]{                                  // drop     S'
                        StateList.S2_WaitPingResult},
                new int[]{                                  // set      M'
                        MessageList.M0_PingNow,
                        MessageList.M1_DropWaitPingTimeout},
                null,                                       // set      C'
                E0_PingNow
        );

        /* S.3 -> E1_PingTimeout */
        transition[E1_PingTimeout] = new TransitionClass(
                new int[]{                                  // check    S'
                        StateList.S3_CheckPingTimeout},
                null,                                       // check    S ingibitors
                new int[]{                                  // check    E'
                        EventList.E1_PingTimeout},
                null,                                       // check    M'
                null,                                       // set      S'
                new int[]{                                  // drop     S'
                        StateList.S3_CheckPingTimeout},
                new int[]{                                  // set      M'
                        MessageList.M2_DropWaitPingResult,
                        MessageList.M3_PingNone},
                null,                                       // set      C'
                E1_PingTimeout
        );

        /* S.4 -> E2_SearchNow */
        transition[E2_SearchNow] = new TransitionClass(
                new int[]{                                  // check    S'
                        StateList.S4_WaitSearchResult},
                null,                                       // check    S ingibitors
                new int[]{                                  // check    E'
                        EventList.E2_SearchNow},
                null,                                       // check    M'
                null,                                       // set      S'
                new int[]{                                  // drop     S'
                        StateList.S4_WaitSearchResult},
                new int[]{                                  // set      M'
                        MessageList.M4_SearchNow,
                        MessageList.M5_DropWaitSearchTimeout},
                null,                                       // set      C'
                E2_SearchNow
        );

        /* S.5 -> E3_SearchTimeout */
        transition[E3_SearchTimeout] = new TransitionClass(
                new int[]{                                  // check    S'
                        StateList.S5_CheckSearchTimeout},
                null,                                       // check    S ingibitors
                new int[]{                                  // check    E'
                        EventList.E3_SearchTimeOut},
                null,                                       // check    M'
                null,                                       // set      S'
                new int[]{                                  // drop     S'
                        StateList.S5_CheckSearchTimeout},
                new int[]{                                  // set      M'
                        MessageList.M6_DropWaitSearchResult,
                        MessageList.M7_SearchNone},
                null,                                       // set      C'
                E3_SearchTimeout
        );

        /* S.2 -> D0_DropWaitPing */
        transition[D0_DropWaitPing] = new TransitionClass(
                new int[]{                                  // check    S'
                        StateList.S2_WaitPingResult},
                null,                                       // check    S ingibitors
                null,                                       // check    E'
                new int[]{                                  // check    M'
                        MessageList.M2_DropWaitPingResult},
                null,                                       // set      S'
                new int[]{                                  // drop     S'
                        StateList.S2_WaitPingResult},
                null,                                       // set      M'
                new int[]{                                  // set      C'
                        C4_DropWaitPingResult},
                D0_DropWaitPing
        );

        /* S.3 -> D1_DropWaitPingTimeout */
        transition[D1_DropWaitPingTimeout] = new TransitionClass(
                new int[]{                                  // check    S'
                        StateList.S3_CheckPingTimeout},
                null,                                       // check    S ingibitors
                null,                                       // check    E'
                new int[]{                                  // check    M'
                        MessageList.M1_DropWaitPingTimeout},
                null,                                       // set      S'
                new int[]{                                  // drop     S'
                        StateList.S3_CheckPingTimeout},
                null,                                       // set      M'
                new int[]{                                  // set      C'
                        C5_DropWaitPingTimeout},
                D1_DropWaitPingTimeout
        );

        /* S.4 -> D2_DropWaitSearch */
        transition[D2_DropWaitSearch] = new TransitionClass(
                new int[]{                                  // check    S'
                        StateList.S4_WaitSearchResult},
                null,                                       // check    S ingibitors
                null,                                       // check    E'
                new int[]{                                  // check    M'
                        MessageList.M6_DropWaitSearchResult},
                null,                                       // set      S'
                new int[]{                                  // drop     S'
                        StateList.S4_WaitSearchResult},
                null,                                       // set      M'
                new int[]{                                  // set      C'
                        C6_DropWaitSearchResult},
                D2_DropWaitSearch
        );

        /* S.5 -> D3_DropWaitSearchTimeout */
        transition[D3_DropWaitSearchTimeout] = new TransitionClass(
                new int[]{                                  // check    S'
                        StateList.S5_CheckSearchTimeout},
                null,                                       // check    S ingibitors
                null,                                       // check    E'
                new int[]{                                  // check    M'
                        MessageList.M5_DropWaitSearchTimeout},
                null,                                       // set      S'
                new int[]{                                  // drop     S'
                        StateList.S5_CheckSearchTimeout},
                null,                                       // set      M'
                new int[]{                                  // set      C'
                        C7_DropWaitSearchTimeout},
                D3_DropWaitSearchTimeout
        );

        /* S.8 -> D4_DropBusyStatus */
        transition[D4_DropBusyStatus] = new TransitionClass(
                new int[]{                                  // check    S'
                        StateList.S8_Busy},
                null,                                       // check    S ingibitors
                null,                                       // check    E'
                new int[]{                                  // check    M'
                        MessageList.M12_DropBusyStatus},
                null,                                       // set      S'
                new int[]{                                  // drop     S'
                        StateList.S8_Busy},
                null,                                       // set      M'
                null,                                       // set      C'
                D4_DropBusyStatus
        );

        /*  -> D5_AbortPing */
        transition[D5_AbortPing] = new TransitionClass(
                new int[]{                                  // check    S'
                        StateList.S2_WaitPingResult,
                        StateList.S3_CheckPingTimeout},
                null,                                       // check    S ingibitors
                null,                                       // check    E'
                new int[]{                                  // check    M'
                        MessageList.M14_DropPingAndSearch},
                new int[]{                                  // set      S'
                        StateList.S10_OperationAborted},
                new int[]{                                  // drop     S'
                        StateList.S2_WaitPingResult,
                        StateList.S3_CheckPingTimeout},
                null,                                       // set      M'
                null,                                       // set      C'
                D5_AbortPing
        );

        /*  -> D6_AbortSearch */
        transition[D6_AbortSearch] = new TransitionClass(
                new int[]{                                  // check    S'
                        StateList.S4_WaitSearchResult,
                        StateList.S5_CheckSearchTimeout},
                null,                                       // check    S ingibitors
                null,                                       // check    E'
                new int[]{                                  // check    M'
                        MessageList.M14_DropPingAndSearch},
                new int[]{                                  // set      S'
                        StateList.S10_OperationAborted},
                new int[]{                                  // drop     S'
                        StateList.S4_WaitSearchResult,
                        StateList.S5_CheckSearchTimeout},
                null,                                       // set      M'
                null,                                       // set      C'
                D6_AbortSearch
        );

        /* Сортировка массива переходов по возрастанию приоритета исполнения */
        sort();
    }

    /**
     * Сортировка объектов переходов в зависимости от приоритета
     */
    void sort() {
        TransitionClass mediator;

        for (int i = 0; i < transition.length - 1; i++) {
            for (int j = i + 1; j < transition.length; j++) {
                if (transition[i].priority < transition[j].priority) {
                    mediator = transition[i];
                    transition[i] = transition[j];
                    transition[j] = mediator;
                }
            }
        }
    }

    /**
     * Устанавливает состояние сообщения в TRUE
     *
     * @param messageToSet
     */
    public void setMessage(int messageToSet) {
        Messages[messageToSet] = true;
    }

    /**
     * Устанавливает состояние сообщения в FALSE
     *
     * @param messageToSet
     */
    public void dropMessage(int messageToSet) {
        Messages[messageToSet] = false;
    }

    /**
     * Устанавливает семафор события
     *
     * @param eventToSet
     */
    public void setEvent(int eventToSet) {
        Events[eventToSet] = true;
    }

    /**
     * Сбрасывает семафор события
     *
     * @param eventToSet
     */
    void dropEvent(int eventToSet) {
        Events[eventToSet] = false;
    }

    /**
     * Устанавливает в "Истина" указанный элемент массива состояний
     *
     * @param stateToSet
     */
    void setState(int stateToSet) {
        States[stateToSet] = true;
    }

    /**
     * Устанавливает в "Ложь" указанный элемент массива состояний
     *
     * @param stateToSet
     */
    void dropState(int stateToSet) {
        States[stateToSet] = false;
    }

    /**
     * Возвращает параметры устройства - имя, адрес, порт
     *
     * @return
     */
    String[] getRealDeviceParam() {
        return
                realDeviceParam;
    }

    /**
     * Завершает работу объекта
     */
    void stop() {
        mainCycleThread.interrupt();
        mainCycleTimer.cancel();
    }

}
