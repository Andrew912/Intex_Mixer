package org.and.intex_v2;

import android.database.Cursor;
import android.util.Log;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.and.intex_v2.DBHelper.KEY_MAIL_COMPLETE;
import static org.and.intex_v2.DBHelper.TABLE_MAIL;
import static org.and.intex_v2.MainActivity.L00_BUTTON_DNS;
import static org.and.intex_v2.MainActivity.L00_BUTTON_BACK;
import static org.and.intex_v2.MainActivity.L00_DATA_CLEAR;
import static org.and.intex_v2.MainActivity.L0_BUTTON_BACK;
import static org.and.intex_v2.MainActivity.L00_BUTTON_OPER;
import static org.and.intex_v2.MainActivity.L0_BUTTON_PARAM_SAVE;
import static org.and.intex_v2.MainActivity.L00_BUTTON_SENDMAIL;
import static org.and.intex_v2.MainActivity.L00_BUTTON_TASK;
import static org.and.intex_v2.MainActivity.L00_BUTTON_MAIL;
import static org.and.intex_v2.MainActivity.L0_TO_CLEARING;
import static org.and.intex_v2.MainActivity.L11_BUTTON_BEGIN_JOB_NEXT;
import static org.and.intex_v2.MainActivity.L1_BUTTON_BEGIN_JOB;
import static org.and.intex_v2.MainActivity.L1_BUTTON_TO_PARAMS;
import static org.and.intex_v2.MainActivity.L2_BUTTON_CANCEL;
import static org.and.intex_v2.MainActivity.L2_BUTTON_TASK_SELECT;
import static org.and.intex_v2.MainActivity.L3_BUTTON_CANCEL;
import static org.and.intex_v2.MainActivity.L3_BUTTON_TASK_CONTINUE;
import static org.and.intex_v2.MainActivity.L4_BUTTON_ACCEPT;
import static org.and.intex_v2.MainActivity.L4_BUTTON_CANCEL;
import static org.and.intex_v2.MainActivity.L4_LIST_TASK_SELECT;
import static org.and.intex_v2.MainActivity.L5_BUTTON_ACCEPT;
import static org.and.intex_v2.MainActivity.L5_BUTTON_CANCEL;
import static org.and.intex_v2.MainActivity.L5_LIST_OPER_SELECT;
import static org.and.intex_v2.MainActivity.L6_BUTTON_CANCEL;
import static org.and.intex_v2.MainActivity.L6_BUTTON_COMPLETE;
import static org.and.intex_v2.MainActivity.L71_BUTTON_CANCEL;
import static org.and.intex_v2.MainActivity.L71_BUTTON_COMPLETE;
import static org.and.intex_v2.MainActivity.L71_BUTTON_START;
import static org.and.intex_v2.MainActivity.L7_BUTTON_CANCEL;
import static org.and.intex_v2.MainActivity.L7_BUTTON_COMPLETE;
import static org.and.intex_v2.MainActivity.L7_BUTTON_START;
import static org.and.intex_v2.MainActivity.L8_BUTTON_OK;
import static org.and.intex_v2.MainActivity.L9_BUTTON_ACCEPT;
import static org.and.intex_v2.MainActivity.L9_BUTTON_CANCEL;
import static org.and.intex_v2.MainActivity.L9_BUTTON_REFRESH;
import static org.and.intex_v2.MainActivity.L9_BUTTON_REJECT;
import static org.and.intex_v2.MainActivity.LAYOUT_00_CLEARING;
import static org.and.intex_v2.MainActivity.LAYOUT_0_PARAMS;
import static org.and.intex_v2.MainActivity.LAYOUT_1_BEGIN;
import static org.and.intex_v2.MainActivity.LAYOUT_4_TASK_SELECT;
import static org.and.intex_v2.MainActivity.LAYOUT_5_OPER_SELECT;
import static org.and.intex_v2.MainActivity.LAYOUT_71_LOAD_OPER;
import static org.and.intex_v2.MainActivity.LAYOUT_7_COMPLEX_OPER;
import static org.and.intex_v2.MainActivity.LAYOUT_8_TASK_COMPLETE;
import static org.and.intex_v2.MainActivity.L__BUTTON_START;


/**
 * Created by Андрей on 15.07.2017.
 */

public class Controller {

    String
            logTAG = "Controller";
    MainActivity
            mainActivity;
    String
            currentTask;
    String
            currentOper;
    String[]
            currentOperParam;
    Messenger
            messenger;
    boolean
            loadingMayBegin;

    Timer
            startButtonPresser;

    /**
     * Конструктор
     *
     * @param activity
     */
    public Controller(MainActivity activity) {
        this.mainActivity
                = activity;
        messenger
                = new Messenger(this.mainActivity);
        loadingMayBegin
                = false;
        startButtonPresser
                = new Timer();
    }

    class TimerTask_PressStartButton extends TimerTask {
        @Override
        public void run() {
            mainActivity.runOnUiThread(new Runnable() {
               @Override
                public void run(){
                   mainActivity.btn_1_Begin.callOnClick();
               }
            });
        }
    }

    void controller(int btn) {

        switch (btn) {

            case L__BUTTON_START:       //

                startButtonPresser.schedule(new TimerTask_PressStartButton(),4000);
                startButtonPresser.schedule(new TimerTask_PressStartButton(),9000);

                /* Для начала надо распечатать все параметры... */
//                mainActivity.dbHandler.getTableColumns("objects");
//                Log.i("printTableData", mainActivity.dbHandler.printTableData("PARAMETERS"));
//                Log.i("printTableData", mainActivity.dbHandler.printTableData("objects"));
//                mainActivity.dbHandler.dbTableList_OBJECTS();

                /* Отправить на сервер протокол */
//                mainActivity.server.sendMail();

                /* Проверим координаты */
                // check if GPS enabled
//                if(mainActivity.gps.canGetLocation()){
//
//                    double latitude = mainActivity.gps.getLatitude();
//                    double longitude = mainActivity.gps.getLongitude();
//                    double altitude = mainActivity.gps.getAltitude();
//                    double speed = mainActivity.gps.getSpeed();
//                    double bearing = mainActivity.gps.getBearing();
//                    double speedLimitA = ((10*1000)/(60*60));
//
//                    // \n is for new line
//
//                    Log.i("GPS", "Your Location is - \nLat: " + latitude
//                            + "\nLong: " + longitude
//                            + "\nAlt:" + altitude);
//
//                    Toast.makeText(mainActivity.getApplicationContext(), "Your Location is - \nLat: " + latitude
//                            + "\nLong: " + longitude
//                            + "\nAlt:" + altitude, Toast.LENGTH_LONG).show();
////                    Intent intent = new Intent(this, Logik_Activity.class);
////                    intent.putExtra("alt",altitude);
////                    intent.putExtra("speed",speed);
////                    intent.putExtra("bearing",bearing);
////                    if (speed < speedLimitA){
////                        startActivity(intent);
////                    }
//                    // if speed > 0 then AAA else BBB
//
//                }else{
//                    // can't get location
//                    // GPS or Network is not enabled
//                    // Ask user to enable GPS/network in settings
//                    mainActivity.gps.showSettingsAlert();
//                }


                Log.i("controller", "L__BUTTON_START: Let's begin!");
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "Терминал миксера");
                break;

            /* Распечатка таблицы DNS */
            case L00_BUTTON_DNS:

                mainActivity.dbHandler.dbTableList_OBJECTS();

                break;

            /* Очистка данных */
            case L00_DATA_CLEAR:
                mainActivity.dbFunctions
                        .clearMail();
                mainActivity.dbFunctions
                        .clearOper();
                mainActivity.dbFunctions
                        .clearTask();
                break;

            /* Возврат из экрана очистки */
            case L00_BUTTON_BACK:
                mainActivity.gotoLayout(LAYOUT_0_PARAMS, "");
                break;

            /* Переход в экран очистки*/
            case L0_TO_CLEARING:
                mainActivity.gotoLayout(LAYOUT_00_CLEARING, null);
                break;

            case L0_BUTTON_PARAM_SAVE:
                // Сохранение параметров
                mainActivity.paramSave();
                break;

            case L00_BUTTON_SENDMAIL:
                // Отправка статистики по операциям

                /* Надо будет включить отправку почты обратно потом */
//                mainActivity.server.sendMail();
                break;

            case L0_BUTTON_BACK:        //
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            /* Вывод списка ОПЕРАЦИЙ из БД */
            case L00_BUTTON_OPER:
                mainActivity
                        .setTextInLayout(LAYOUT_00_CLEARING, mainActivity.dbFunctions.oper());
                break;

            /* Вывод списка ЗАДАЧ из БД */
            case L00_BUTTON_TASK:
                mainActivity
                        .setTextInLayout(LAYOUT_00_CLEARING, mainActivity.dbFunctions.task());
                break;

            case L00_BUTTON_MAIL:       // Сообщения для передачи на сервер
                mainActivity
                        .setTextInLayout(LAYOUT_00_CLEARING, mainActivity.dbHandler.printTableData("mail"));
//                        .setTextInLayout(LAYOUT_00_CLEARING, mainActivity.dbFunctions.mail());
                break;

            case L1_BUTTON_TO_PARAMS:       // На экран БД
                startButtonPresser.cancel();
                mainActivity.gotoLayout(LAYOUT_0_PARAMS, "");
                break;

            /* Начало работы самое что ни на есть */
            case L1_BUTTON_BEGIN_JOB:

                // Отправляем почту, если есть, что отправлять
//                if (dbMailRecNotEmpty()) {
//                    mainActivity.server.sendMail();
//                    Log.i("MAIL", "Почта отправлена!");
//                } else {
//                    Log.i("MAIL", "Нет почты для отправки...");
//                }

                /* Проверяем подключение весового терминала если он еще не подключен */
                if (mainActivity.ifServerFound(mainActivity.conf.terminalName) == false) {
                    mainActivity.CheckConnection(
                            mainActivity.conf.terminalName,
                            null,
                            mainActivity.L[LAYOUT_4_TASK_SELECT],
                            mainActivity.btn_11_Next);
                    mainActivity.conf.termAddrRefresh();
                    break;
                } else {
                    mainActivity.printServerFound();
                }

            case L11_BUTTON_BEGIN_JOB_NEXT:
                /* Если весовой терминал подключен, то работаем дальше как положено
                *
                * 1. Включить получение данных от весового терминала.
                * 2. Включить получение геоданных.
                * 3. Запустить "обход" каждые 10 с - проверка изменения параметров и запись в протокол
                *
                * */

                // Запускаем Получение показаний весов от терминала
                mainActivity.weightDataFromDeviceReader_Start();

                // Параметры - на экран
                mainActivity.displayWeightParameters();
                mainActivity.displayWeightParameters1();

                mainActivity.gotoLayout(LAYOUT_71_LOAD_OPER, "Миксер");

//                mainActivity.currentTask.setTaskData();
//
//                if (mainActivity.currentTask.now) {
//                    currentOper = mainActivity.storer.getCurrentOperId(String.valueOf(mainActivity.currentTask.taskId));
//                    if (currentOper != null) {
//                        mainActivity.currentTask.setToActive();
//                        mainActivity.currentOper.setToActive();
//                        mainActivity.gotoLayout(LAYOUT_6_SIMPLE_OPER, mainActivity.currentOper.getOperationInfoForView());
//                        /**
//                         * currentOper.operNow() определяет, есть ли в БД операция с признаком
//                         * "текущая".
//                         * И, кстати, тут же сохраняет параметры текущей операции в объект...
//                         *
//                         * Если есть текущая операция, то сразу переходим на нее,
//                         * если текущей операции нет, то переходим на экран выбора операции.
//                         * Хотя какого хрена туда переходить, все равно будет выбрана
//                         * первая по списку операция.
//                         */
//                    } else {
//                        mainActivity.gotoLayout(LAYOUT_3_DO_TASK, "Текущая задача: " + mainActivity.currentTask.taskComment);
//                        /**
//                         * Задача есть, операция не выбрана.
//                         * Переход на экран списка операций текущей задачи.
//                         */
//                    }
//                } else {
//                    mainActivity.gotoLayout(LAYOUT_2_NO_TASK, "Нет текущей задачи, получите задание от диспетчера");
//                    /**
//                     * Нет ни текущей задачи, ни операции.
//                     * ??? Переходим на экран выбора задачи.
//                     * Скорее, пытаемся прочитать список задач с сервера
//                     */
//                    mainActivity.server.readTask();
//                }
                break;

            /*  */
            case L2_BUTTON_TASK_SELECT:
                mainActivity.gotoLayout(LAYOUT_4_TASK_SELECT, "Выберите задачу из списка");
                break;

            /*  */
            case L2_BUTTON_CANCEL:
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            /*  */
            case L3_BUTTON_CANCEL:
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            /*  */
            case L3_BUTTON_TASK_CONTINUE:
                mainActivity.gotoLayout(LAYOUT_5_OPER_SELECT, "");
                break;

            /*  */
            case L4_LIST_TASK_SELECT:
                mainActivity.btn_4_Accept.setVisibility(View.VISIBLE);
                break;

            /*  */
            case L4_BUTTON_CANCEL:
                mainActivity.btn_4_Accept.setVisibility(View.INVISIBLE);
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            /*  */
            case L4_BUTTON_ACCEPT:
                // Задача выбрана
                mainActivity.btn_4_Accept
                        .setVisibility(View.INVISIBLE);
                // Установить текущую задачу
                mainActivity.storer
                        .setTaskProperty_Current(getKeyTaskIdFromListView(), 1);
                mainActivity.currentTask
                        .setTaskData();
                // Переходим к выбору операции
                mainActivity.gotoLayout(LAYOUT_5_OPER_SELECT, "");
                break;

            /*  */
            case L5_LIST_OPER_SELECT:

                break;

            /*  */
            case L5_BUTTON_CANCEL:
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            /* Начать выполнение операций задачи */
            case L5_BUTTON_ACCEPT:
//                mainActivity.currentTask
//                        .setToActive();
//                currentOperParam
//                        = new String[4];
//                currentOperParam
//                        = mainActivity.storer.getFirstOperationForExecution();
//                mainActivity.currentOper
//                        .set(
//                                currentOperParam[0],
//                                currentOperParam[1],
//                                currentOperParam[2],
//                                mainActivity.storer.getOperData(currentOperParam[0])[4]
//                        );
//                mainActivity.currentOper
//                        .setCurrent();
//                mainActivity.currentOper
//                        .setToActive();
//
//                // Если операция - "загрузка без погрузчика", то на экран ???, иначе - проверить "простая" погрузка
//                if (mainActivity.currentOper.operIsLoadNoLoader() == true) {
//                    // Погрузка будет без испольщования погрузчика
//                    mainActivity.currentOper.loadNoLoader = true;
//
//                    /* Запускаем Получение показаний весов от терминала */
//                    mainActivity.weightDataFromDeviceReader_Start();
//
//                    /* Тут надо поменять переход сразу на начало загрузки */
//                    // Загруженный вес
//                    mainActivity.storer.weightLoaded
//                            = 0;
//                    // Стартовый вес в миксере
//                    mainActivity.storer.weightStart
//                            = mainActivity.storer.weightCurrent;
//                    // Вычислить конечный вес в погрузчике
//                    mainActivity.storer.weightTarget
//                            = mainActivity.currentOper.loadValue + mainActivity.storer.weightCurrent;
//                    Log.i(logTAG, "конечный вес в погрузчике = " + mainActivity.storer.weightTarget);
//                    // Толеранс +
//                    mainActivity.storer.tolerancePlus
//                            = (int) (mainActivity.currentOper.loadValue * Float.parseFloat(mainActivity.getString(R.string.LOADING_PERCENT_WEIGHT_TOLERANCE_UP)));
//                    // Параметры - на экран
//                    mainActivity.displayWeightParameters();
//                    mainActivity.displayWeightParameters1();
//
//                    mainActivity.gotoLayout(LAYOUT_71_LOAD_OPER, mainActivity.currentOper.getOperationInfoForView());
//                    break;
//                }
//
//                // Погрузка будет с использованием погрузчика
//                mainActivity.currentOper.loadNoLoader = false;
//
//                // Если оперция - загрузка, то на экран 8, иначе - 6
//                if (mainActivity.currentOper.operIsLoad() == true) {
//
//                    /* Надо проверить подключние погрузчика */
//
//                    Log.i("L5_BUTTON_ACCEPT",
//                            "servern=" + mainActivity.currentOper.getParam("servern") +
//                                    ", servera=" + mainActivity.currentOper.getParam("servera"));
//                    Log.i("L5_BUTTON_ACCEPT", "==============================");
//
//                    /* Попытаемся найти погрузчик в сети */
//
//                    mainActivity.CheckConnection(
//                            mainActivity.currentOper.getParam("servern"),
//                            null,
//                            mainActivity.L[LAYOUT_9_SERV_REQUEST],
//                            null);
//
////                    mainActivity.conf.loaderAddrRefresh(mainActivity.currentOper.getParam("servern"));
//
//                    mainActivity.gotoLayout(LAYOUT_9_SERV_REQUEST, mainActivity.currentOper.getOperationInfoForView());
//
//                } else {
//                    mainActivity.gotoLayout(LAYOUT_6_SIMPLE_OPER, mainActivity.currentOper.getOperationInfoForView());
//                }
                break;

            /* "Простая операция" - отмена */
            case L6_BUTTON_CANCEL:
                mainActivity.currentTask.setToUnactive();
                mainActivity.currentOper.setToUnactive();
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");       // Возврат на начальный экран
                break;

            /* "Простая операция" - выполнено */
            case L6_BUTTON_COMPLETE:
                // Отчет о выполнении операции
                mainActivity.currentOper.setToComplete();
                // Проверить оставшиеся операции текущей задачи
                if (mainActivity.storer.getListOperationsForExecution() == null) {
                    mainActivity.currentTask.setToComplete();
                    mainActivity.gotoLayout(LAYOUT_8_TASK_COMPLETE, "");      // Все операции задачи завершены
                } else {
                    mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");   // Если есть невыполненые операции
                }
                break;

            /* Начинаем непосредственно погрузку */
            case L7_BUTTON_START:
                mainActivity.displayWeightParameters();
                // Можно передать на погрузчик сигнал "Начать"
                loadingMayBegin = true;
                // Начать передачу данных на погрузчик
                mainActivity.makeOperation_Load_with_Loader();
                // Деактивировать кнопку "Начать"
                mainActivity.btn_7_Start.setVisibility(View.INVISIBLE);
                // Активировать кнопку "Закончить"
                mainActivity.btn_7_Complete.setVisibility(View.VISIBLE);
                break;

            /* Погрузка - Отмена */
            case L7_BUTTON_CANCEL:
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            /* Погрузка - Завершено */
            case L7_BUTTON_COMPLETE:
                mainActivity.weightDataToLoaderSender_Stop();
                mainActivity.weightDataFromDeviceReader_Stop();  // Остановить получение показаний весов
                mainActivity.loader.send(mainActivity.loader.msg_LoadStop(mainActivity.currentOper.operId));
                // Отчет о выполнении операции
                mainActivity.currentOper.setToComplete();
                // Проверить оставшиеся операции текущей задачи
                if (mainActivity.storer.getListOperationsForExecution() == null) {
                    mainActivity.currentTask.setToComplete();
                    mainActivity.gotoLayout(LAYOUT_8_TASK_COMPLETE, "");       // Все операции задачи завершены
                } else {
                    mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");               // Если есть невыполненые операции
                    /**
                     * Здесь пока ставим выход, т.к. по неизвестной пока причине при повторном
                     * обращении к погрузчику он не слышит запрос на обслуживание.
                     * Надо будет найти причину и тогда можно будет этот выход убрать.
                     */
                    System.exit(0);
                }
                break;

            /* Начинаем непосредственно погрузку */
            case L71_BUTTON_START:
                mainActivity.displayWeightParameters1();
                //
                loadingMayBegin = true;
                //
                mainActivity.makeOperation_Load_No_Loader();
                // Деактивировать конпку "Начать"
                mainActivity.btn_71_Start.setVisibility(View.INVISIBLE);
                // Активировать конпку "Закончить"
                mainActivity.btn_71_Complete.setVisibility(View.VISIBLE);
                break;

            /* Погрузка - Отмена */
            case L71_BUTTON_CANCEL:
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            /* Погрузка - Завершено */
            case L71_BUTTON_COMPLETE:
                mainActivity
                        .weightDataToLoaderSender_Stop();
                mainActivity
                        .weightDataFromDeviceReader_Stop();  // Остановить получение показаний весов
                mainActivity.loader
                        .send(mainActivity.loader.msg_LoadStop(mainActivity.currentOper.operId));
                // Отчет о выполнении операции
                mainActivity.currentOper.setToComplete();
                // Проверить оставшиеся операции текущей задачи
                if (mainActivity.storer.getListOperationsForExecution() == null) {
                    mainActivity
                            .currentTask.setToComplete();
                    mainActivity
                            .gotoLayout(LAYOUT_8_TASK_COMPLETE, "");       // Все операции задачи завершены
                } else {
                    mainActivity
                            .gotoLayout(LAYOUT_1_BEGIN, "");               // Если есть невыполненые операции
                }
                break;

            /* OK - task complete */
            case L8_BUTTON_OK:
                new DBFunctions(mainActivity, mainActivity.dbHelper.getWritableDatabase()).clearOper();
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            /* Отказ от обслуживания */
            case L9_BUTTON_CANCEL:
                mainActivity.currentTask.setToUnactive();
                mainActivity.currentOper.setToUnactive();
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            /* Сервер подтвердил */
            case L9_BUTTON_ACCEPT:
                // Загруженный вес
                mainActivity.storer.weightLoaded
                        = 0;
                // Стартовый вес в миксере
                mainActivity.storer.weightStart
                        = mainActivity.storer.weightCurrent;
                // Вычислить конечный вес в погрузчике
                mainActivity.storer.weightTarget
                        = mainActivity.currentOper.loadValue + mainActivity.storer.weightCurrent;
                Log.i(logTAG, "конечный вес в погрузчике = " + mainActivity.storer.weightTarget);
                // Толеранс +
                mainActivity.storer.tolerancePlus
                        = (int) (mainActivity.currentOper.loadValue * Float.parseFloat(mainActivity.getString(R.string.LOADING_PERCENT_WEIGHT_TOLERANCE_UP)));
                // Параметры - на экран
                mainActivity.displayWeightParameters();
                mainActivity.displayWeightParameters1();
                //
                if (mainActivity.currentOper.loadNoLoader == true) {
                    mainActivity.gotoLayout(LAYOUT_71_LOAD_OPER, "");
                } else {
                    mainActivity.gotoLayout(LAYOUT_7_COMPLEX_OPER, "");
                }
                break;

            /* Сервер отказал */
            case L9_BUTTON_REJECT:
                mainActivity.currentTask.setToUnactive();
                mainActivity.currentOper.setToUnactive();
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            /* Обновить экран */
            case L9_BUTTON_REFRESH:
                break;
        }
    }

    // Id задачи из строки
    public String getKeyTaskIdFromListView() {
        /**
         * Получение идентификатора задачи из строки спика ListView при выборе
         */
        Pattern p = Pattern.compile("\\[([\\d]+)\\]");
        Matcher m = p.matcher(mainActivity.taskSelect_SelectedValue);
        if (m.find()) {
            return m.group().replace("[", "").replace("]", "");
        } else {
            return null;
        }
    }

    // Id задачи из строки
    public long getKeyOperIdFromListView() {
        /**
         * Получение идентификатора операции из строки спика ListView при выборе
         */
        mainActivity.log("operSelect_SelectedValue=" + mainActivity.operSelect_SelectedValue);
        Pattern p = Pattern.compile("\\[([\\d]+)\\]");
        Matcher m = p.matcher(mainActivity.operSelect_ListItems[0]);
        if (m.find()) {
            return Long.parseLong(m.group().replace("[", "").replace("]", ""));
        } else {
            return 0;
        }
    }

    /**
     * Количество записей в таблице с отчетами по операциям
     *
     * @return
     */
    boolean dbMailRecNotEmpty() {
        int r;
        DBHelper dbh = new DBHelper(mainActivity.context);
        Cursor cursor = dbh.getReadableDatabase().query(
                TABLE_MAIL,
                new String[]{"COUNT (_id) AS counter"},
                KEY_MAIL_COMPLETE + "=?",
                new String[]{String.valueOf(0)},
                null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }


}
