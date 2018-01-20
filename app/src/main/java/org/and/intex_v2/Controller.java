package org.and.intex_v2;

import android.util.Log;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.and.intex_v2.MainActivity.L0_BUTTON_BACK;
import static org.and.intex_v2.MainActivity.L0_BUTTON_OPER;
import static org.and.intex_v2.MainActivity.L0_BUTTON_SENDMAIL;
import static org.and.intex_v2.MainActivity.L0_BUTTON_TASK;
import static org.and.intex_v2.MainActivity.L0_BUTTON_TASK1;
import static org.and.intex_v2.MainActivity.L1_BUTTON_BEGIN_JOB;
import static org.and.intex_v2.MainActivity.L1_BUTTON_TO_DB;
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
import static org.and.intex_v2.MainActivity.LAYOUT_0_DB;
import static org.and.intex_v2.MainActivity.LAYOUT_1_BEGIN;
import static org.and.intex_v2.MainActivity.LAYOUT_2_NO_TASK;
import static org.and.intex_v2.MainActivity.LAYOUT_3_DO_TASK;
import static org.and.intex_v2.MainActivity.LAYOUT_4_TASK_SELECT;
import static org.and.intex_v2.MainActivity.LAYOUT_5_OPER_SELECT;
import static org.and.intex_v2.MainActivity.LAYOUT_6_SIMPLE_OPER;
import static org.and.intex_v2.MainActivity.LAYOUT_71_LOAD_OPER;
import static org.and.intex_v2.MainActivity.LAYOUT_7_COMPLEX_OPER;
import static org.and.intex_v2.MainActivity.LAYOUT_8_TASK_COMPLETE;
import static org.and.intex_v2.MainActivity.LAYOUT_9_SERV_REQUEST;
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

    /**
     * Конструктор
     * @param activity
     */
    public Controller(MainActivity activity) {
        this.mainActivity = activity;
        messenger = new Messenger(this.mainActivity);
        loadingMayBegin = false;
    }

    void controller(int btn) {

        switch (btn) {
            case L__BUTTON_START:       //
//                mainActivity.server.sendMail();

                // Начальная установка. Активируем первый экран
                mainActivity.gotoLayout(
                        LAYOUT_1_BEGIN,
                        "rec in Task=" + mainActivity.storer.getRecCount_Task() + "\n " +
                                "rec in Oper=" + mainActivity.storer.getRecCount_Oper() + "\n " +
                                "rec in Oper.Par=" + mainActivity.storer.getRecCount_OperPar());
                break;

            case L0_BUTTON_SENDMAIL:     //
//                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                mainActivity.server.sendMail();
                break;

            case L0_BUTTON_BACK:        //
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            case L0_BUTTON_OPER:        // Вывод списка операций из БД
                mainActivity.dbFunctions.oper();
                break;

            case L0_BUTTON_TASK:        // Вывод списка задач из БД
                mainActivity.dbFunctions.task();
                break;

            case L0_BUTTON_TASK1:       // Сообщения для передачи на сервер
                mainActivity.dbFunctions.mail();
                break;

            case L1_BUTTON_TO_DB:       // На экран БД
                mainActivity.gotoLayout(LAYOUT_0_DB, "");
                break;

            case L1_BUTTON_BEGIN_JOB:   // Начало работы

                mainActivity.currentTask.setTaskData();
                if (mainActivity.currentTask.now) {
                    currentOper = mainActivity.storer.getCurrentOperId(String.valueOf(mainActivity.currentTask.taskId));
                    if (currentOper != null) {
                        mainActivity.currentTask.setToActive();
                        mainActivity.currentOper.setToActive();
                        mainActivity.gotoLayout(LAYOUT_6_SIMPLE_OPER, mainActivity.currentOper.getOperationInfoForView());
                        /**
                         * currentOper.operNow() определяет, есть ли в БД операция с признаком
                         * "текущая".
                         * И, кстати, тут же сохраняет параметры текущей операции в объект...
                         *
                         * Если есть текущая операция, то сразу переходим на нее,
                         * если текущей операции нет, то переходим на экран выбора операции.
                         * Хотя какого хрена туда переходить, все равно будет выбрана
                         * первая по списку операция.
                         */
                    } else {
                        mainActivity.gotoLayout(LAYOUT_3_DO_TASK, "Текущая задача: " + mainActivity.currentTask.taskComment);
                        /**
                         * Задача есть, операция не выбрана.
                         * Переход на экран списка операций текущей задачи.
                         */
                    }
                } else {
                    mainActivity.gotoLayout(LAYOUT_2_NO_TASK, "Нет текущей задачи, получите задание от диспетчера");
                    /**
                     * Нет ни текущей задачи, ни операции.
                     * Переходим на экран выбора задачи.
                     */
                    mainActivity.server.readTask();
                }
                break;

            case L2_BUTTON_TASK_SELECT: //
                mainActivity.gotoLayout(LAYOUT_4_TASK_SELECT, "Выберите задачу из списка");
                break;

            case L2_BUTTON_CANCEL:
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            case L3_BUTTON_CANCEL:
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            case L3_BUTTON_TASK_CONTINUE:
                mainActivity.gotoLayout(LAYOUT_5_OPER_SELECT, "");
                break;

            case L4_LIST_TASK_SELECT:
                mainActivity.btn_4_Accept.setVisibility(View.VISIBLE);
                break;

            case L4_BUTTON_CANCEL:
                mainActivity.btn_4_Accept.setVisibility(View.INVISIBLE);
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            case L4_BUTTON_ACCEPT:
                // Задача выбрана
                mainActivity.btn_4_Accept.setVisibility(View.INVISIBLE);
                // Установить текущую задачу
                mainActivity.storer.setTaskProperty_Current(getKeyTaskIdFromListView(), 1);
                mainActivity.currentTask.setTaskData();
                // Переходим к выбору операции
                mainActivity.gotoLayout(LAYOUT_5_OPER_SELECT, "");
                break;

            case L5_LIST_OPER_SELECT:

                break;

            case L5_BUTTON_CANCEL:
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            case L5_BUTTON_ACCEPT:      // Начать выполнение операций задачи
                mainActivity.currentTask.setToActive();
                currentOperParam = new String[4];
                currentOperParam = mainActivity.storer.getFirstOperationForExecution();
                mainActivity.currentOper.set(
                        currentOperParam[0],
                        currentOperParam[1],
                        currentOperParam[2],
                        mainActivity.storer.getOperData(currentOperParam[0])[4]
                );
                mainActivity.currentOper.setCurrent();
                mainActivity.currentOper.setToActive();

                // Если опарция - загрузка без погрузчика, то на экран ???, иначе - проверить "простая" погрузка
                if (mainActivity.currentOper.operIsLoadNoLoader() == true) {
                    // Погрузка будет без испольщования погрузчика
                    mainActivity.currentOper.loadNoLoader = true;
                    // Запускаем Получение показаний весов от терминала
                    mainActivity.weightDataFromDeviceReader_Start();
                    /**
                     * Тут надо поменять переход сразу на начало загрузки
                     */
                    // Загруженный вес
                    mainActivity.storer.weightLoaded = 0;
                    // Стартовый вес в миксере
                    mainActivity.storer.weightStart = mainActivity.storer.weightCurrent;
                    // Вычислить конечный вес в погрузчике
                    mainActivity.storer.weightTarget = mainActivity.currentOper.loadValue + mainActivity.storer.weightCurrent;
                    Log.i(logTAG, "конечный вес в погрузчике = " + mainActivity.storer.weightTarget);
                    // Толеранс +
                    mainActivity.storer.tolerancePlus =
                            (int) (mainActivity.currentOper.loadValue * Float.parseFloat(mainActivity.getString(R.string.LOADING_PERCENT_WEIGHT_TOLERANCE_UP)));
                    // Параметры - на экран
                    mainActivity.displayWeightParameters();

                    mainActivity.gotoLayout(LAYOUT_71_LOAD_OPER, mainActivity.currentOper.getOperationInfoForView());
                    break;
                }

                // Погрузка будет с использованием погрузчика
                mainActivity.currentOper.loadNoLoader = false;

                // Если оперция - загрузка, то на экран 8, иначе - 6
                if (mainActivity.currentOper.operIsLoad() == true) {
                    // Запускаем Получение показаний весов от терминала
                    mainActivity.weightDataFromDeviceReader_Start();
                    mainActivity.gotoLayout(LAYOUT_9_SERV_REQUEST, mainActivity.currentOper.getOperationInfoForView());
                } else {
                    mainActivity.gotoLayout(LAYOUT_6_SIMPLE_OPER, mainActivity.currentOper.getOperationInfoForView());
                }
                break;

            case L6_BUTTON_CANCEL:      // "Простая операция" - отмена
                mainActivity.currentTask.setToUnactive();
                mainActivity.currentOper.setToUnactive();
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");       // Возврат на начальный экран
                break;

            case L6_BUTTON_COMPLETE:    // "Простая операция" - выполнено
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

            case L7_BUTTON_START:     // Начинаем непосредственно погрузку
                mainActivity.displayWeightParameters();
                // Можно передать на погрузчик сигнала "Начать"
                loadingMayBegin = true;
                // Начать передачу данных на погрузчик
                mainActivity.makeOperation_Load_with_Loader();
                // Деактивировать конпку "Начать"
                mainActivity.btn_7_Start.setVisibility(View.INVISIBLE);
                // Активировать конпку "Закончить"
                mainActivity.btn_7_Complete.setVisibility(View.VISIBLE);
                break;

            case L7_BUTTON_CANCEL:      // Погрузка - Отмена
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            case L7_BUTTON_COMPLETE:    // Погрузка - Завершено
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
                }
                break;

            case L71_BUTTON_START:
                // Начинаем непосредственно погрузку
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

            case L71_BUTTON_CANCEL:      // Погрузка - Отмена
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            case L71_BUTTON_COMPLETE:    // Погрузка - Завершено
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
                }
                break;

            case L8_BUTTON_OK:                          // OK - task complete
                new DBFunctions(mainActivity).operClear();
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            case L9_BUTTON_CANCEL:                      // Отказ от обслуживания
                mainActivity.currentTask.setToUnactive();
                mainActivity.currentOper.setToUnactive();
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            case L9_BUTTON_ACCEPT:                      // Сервер подтвердил
                // Загруженный вес
                mainActivity.storer.weightLoaded = 0;
                // Стартовый вес в миксере
                mainActivity.storer.weightStart = mainActivity.storer.weightCurrent;
                // Вычислить конечный вес в погрузчике
                mainActivity.storer.weightTarget = mainActivity.currentOper.loadValue + mainActivity.storer.weightCurrent;
                Log.i(logTAG, "конечный вес в погрузчике = " + mainActivity.storer.weightTarget);
                // Толеранс +
                mainActivity.storer.tolerancePlus =
                        (int) (mainActivity.currentOper.loadValue * Float.parseFloat(mainActivity.getString(R.string.LOADING_PERCENT_WEIGHT_TOLERANCE_UP)));
                // Параметры - на экран
                mainActivity.displayWeightParameters();
                //
                if (mainActivity.currentOper.loadNoLoader == true) {
                    mainActivity.gotoLayout(LAYOUT_71_LOAD_OPER, "");
                } else {
                    mainActivity.gotoLayout(LAYOUT_7_COMPLEX_OPER, "");
                }
                break;

            case L9_BUTTON_REJECT:                      // Сервер отказал
                mainActivity.currentTask.setToUnactive();
                mainActivity.currentOper.setToUnactive();
                mainActivity.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            case L9_BUTTON_REFRESH:                      // Обновить экран

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

}
