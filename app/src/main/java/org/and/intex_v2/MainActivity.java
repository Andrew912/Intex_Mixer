package org.and.intex_v2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
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

    /******************************************************************************
     * Разные переменные
     ******************************************************************************/
    String
            logTAG = "MAIN";

    /******************************************************************************
     * Объявление классов
     ******************************************************************************/
    Configurator
            configurator;
    Messenger
            messenger;
    DBFunctions
            dbFunctions;
    CurrentTask
            currentTask;
    CurrentOper
            currentOper;
    Storer
            storer;
    DBHelper
            dbHelper;
    SQLiteDatabase
            db;
    LoaderCommunicator
            loader;


    /******************************************************************************
     * Константы
     ******************************************************************************/

    /**
     * События нажатия клавиш, списков и пр. **************************************
     */
    static final int L__BUTTON_START
            = 0;
    static final int L0_BUTTON_SENDMAIL
            = 1001;
    static final int L0_BUTTON_BACK
            = 1;
    static final int L0_BUTTON_TASK
            = 2;
    static final int L0_BUTTON_TASK1
            = 3;
    static final int L0_BUTTON_OPER
            = 4;
    static final int L1_BUTTON_TO_DB
            = 100;
    static final int L1_BUTTON_BEGIN_JOB
            = 101;
    static final int L2_BUTTON_TASK_SELECT
            = 200;
    static final int L2_BUTTON_CANCEL
            = 201;
    static final int L3_BUTTON_TASK_CONTINUE
            = 300;
    static final int L3_BUTTON_CANCEL
            = 301;
    static final int L4_BUTTON_CANCEL
            = 400;
    static final int L4_BUTTON_ACCEPT
            = 401;
    static final int L4_LIST_TASK_SELECT
            = 402;
    static final int L5_BUTTON_CANCEL
            = 500;
    static final int L5_BUTTON_ACCEPT
            = 501;
    static final int L5_LIST_OPER_SELECT
            = 502;
    static final int L6_BUTTON_COMPLETE
            = 600;
    static final int L6_BUTTON_CANCEL
            = 601;
    static final int L7_BUTTON_COMPLETE
            = 701;
    static final int L7_BUTTON_CANCEL
            = 702;
    static final int L7_BUTTON_START
            = 703;
    static final int L8_BUTTON_OK
            = 801;
    static final int L9_BUTTON_CANCEL
            = 901;
    static final int L9_BUTTON_ACCEPT
            = 902;
    static final int L9_BUTTON_REJECT
            = 903;
    static final int L9_BUTTON_REFRESH
            = 904;

    /**
     * Ссылки на лайауты для выборки из массива ***********************************
     */
    static final int LAYOUT_0_DB
            = 0;
    static final int LAYOUT_1_BEGIN
            = 1;
    static final int LAYOUT_2_NO_TASK
            = 2;
    static final int LAYOUT_3_DO_TASK
            = 3;
    static final int LAYOUT_4_TASK_SELECT
            = 4;
    static final int LAYOUT_5_OPER_SELECT
            = 5;
    static final int LAYOUT_6_SIMPLE_OPER
            = 6;
    static final int LAYOUT_7_COMPLEX_OPER
            = 7;
    static final int LAYOUT_8_TASK_COMPLETE
            = 8;
    static final int LAYOUT_9_SERV_REQUEST
            = 9;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Создание классов
         */
        configurator =
                new Configurator(this);
        messenger =
                new Messenger(this);
        storer =
                new Storer(this);
        currentTask =
                new CurrentTask(this);
        currentOper =
                new CurrentOper(this);
        dbFunctions =
                new DBFunctions(this);
        dbHelper =
                new DBHelper(this);
        loader=
                new LoaderCommunicator(this);
//        db =
//                dbHelper.getWritableDatabase();


    }

    /**
     * Параметры текущей задачи
     */
    public class CurrentTask {
        MainActivity
                mainActivity;
        String
                logTAG;
        boolean
                now = false;                // Имеется текущая активная задача
        String
                taskId;                     // Идентификатор задачи
        String
                taskComment;
        String
                taskStatus;

        /**
         * При вызове конструктора надо бы загрузить данные из БД о текущей задаче
         */
        public CurrentTask(MainActivity activity) {
            this.mainActivity = activity;
            logTAG = mainActivity.logTAG + "CurrentTask: Start";
            taskStatus = mainActivity.getString(R.string.STATUS_UNDEF);
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
            String logTAG = mainActivity.currentTask.logTAG + "setToActive";
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
            String logTAG = mainActivity.currentTask.logTAG + "setToUnActive";
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
            String logTAG = mainActivity.currentTask.logTAG + "setToUnActive";
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
            storer.setTaskProperty_Complete(mainActivity.currentTask.taskId);
            storer.setTaskProperty_Current(mainActivity.currentTask.taskId, 0);

        }
    }

    public class CurrentOper {
        /**
         * Описывает операцию - ид, имя и тип (экранное имя)
         */
        MainActivity
                a;
        String
                logTAG;
        String
                operStatus;
        boolean
                operNow;
        String
                operId, operName, operType;
        int
                loadValue;
        ArrayList<OperationParameter>
                operationParameters;

        public CurrentOper(MainActivity activity) {
            this.a = activity;
            operStatus = a.getString(R.string.STATUS_UNDEF);
            logTAG = a.logTAG + "CurrentOper: Щас будет установка setOperData()";
            setOperData();
        }

        void setCurrent() {
            storer.setTaskProperty_Current(operId, 1);
        }

        void setOperData() {
            logTAG = a.logTAG + "setOperData()";
            String[] s = storer.takeCurrentOperData(currentTask.taskId);
            if (s == null) {
                set(null, null, null, "undef");
            } else {
                set(s[0], s[1], s[2], s[3]);

            }
        }

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
            String logTAG = a.currentOper.logTAG + "setToActive";
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
            String logTAG = a.currentOper.logTAG + "setToUnactive";
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
            String logTAG = a.currentOper.logTAG + "setToComplete";
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
            return a.storer.getCurrentOperId(currentTask.taskId);
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

        // Значение параметра операции по его имени
        String getParam(String pName) {
            String retVar = null;
            if (operationParameters.size() != 0) {
                for (OperationParameter p : operationParameters) {
                    if (p.name.equals(pName)) {
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
            Cursor c = db.query(
                    dbHelper.TABLE_OPER_PARAM,
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

    public void makeOperation_Load() {
//        weightDataFromDeviceReader_Start(); // Получение показаний весов от терминала
        weightDataToLoaderSender_Start();   // Передача показаний весов на терминал погрузчика
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

    /**
     * Лог (обратная совместимость)
     *
     * @param s - строка для вывода
     */
    public void log(String s) {
        Log.i("MAIN.LOG: ", s);
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

            case LAYOUT_8_TASK_COMPLETE:
                break;

            case LAYOUT_9_SERV_REQUEST:
                loader.serverServiceRequest();
//                messenger.msg_ToLoader_ServiceRequest();
                break;
        }
    }

    /**
     * Задача для опроса весового терминала
     */
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

}