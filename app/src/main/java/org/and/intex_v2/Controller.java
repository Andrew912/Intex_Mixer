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
import static org.and.intex_v2.MainActivity.LAYOUT_0_DB;
import static org.and.intex_v2.MainActivity.LAYOUT_1_BEGIN;
import static org.and.intex_v2.MainActivity.LAYOUT_2_NO_TASK;
import static org.and.intex_v2.MainActivity.LAYOUT_3_DO_TASK;
import static org.and.intex_v2.MainActivity.LAYOUT_6_SIMPLE_OPER;
import static org.and.intex_v2.MainActivity.L__BUTTON_START;


/**
 * Created by Андрей on 15.07.2017.
 */

public class Controller {

    String
            logTAG = "Controller";
    MainActivity
            a;
    String
            currentTask;
    String
            currentOper;
    String[]
            currentOperParam;
    Messenger
            m;

    boolean
            loadingMayBegin;

    public Controller(MainActivity activity) {
        this.a = activity;
        m = new Messenger(a);
        loadingMayBegin = false;
    }

    void controller(int btn) {

        switch (btn) {
            case L__BUTTON_START:       //

//                activity.server.sendMail();

                // Начальная установка. Активируем первый экран
                a.gotoLayout(
                        LAYOUT_1_BEGIN,
                        "rec in Task=" + a.storer.getRecCount_Task() + "\n " +
                                "rec in Oper=" + a.storer.getRecCount_Oper() + "\n " +
                                "rec in Oper.Par=" + a.storer.getRecCount_OperPar());
                break;

            case L0_BUTTON_SENDMAIL:     //
//                activity.gotoLayout(LAYOUT_1_BEGIN, "");
                a.server.sendMail();
                break;

            case L0_BUTTON_BACK:        //
                a.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            case L0_BUTTON_OPER:        // Вывод списка операций из БД
                a.dbFunctions.oper();
                break;

            case L0_BUTTON_TASK:        // Вывод списка задач из БД
                a.dbFunctions.task();
                break;

            case L0_BUTTON_TASK1:       // Сообщения для передачи на сервер
                a.dbFunctions.mail();
                break;

            case L1_BUTTON_TO_DB:       // На экран БД
                a.gotoLayout(LAYOUT_0_DB, "");
                break;

            case L1_BUTTON_BEGIN_JOB:   // Начало работы

                a.currentTask.setTaskData();
                if (a.currentTask.now) {
                    currentOper = a.storer.getCurrentOperId(String.valueOf(a.currentTask.taskId));
                    if (currentOper != null) {
                        a.currentTask.setToActive();
                        a.currentOper.setToActive();
                        a.gotoLayout(LAYOUT_6_SIMPLE_OPER, a.currentOper.getOperationInfoForView());
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
                        a.gotoLayout(LAYOUT_3_DO_TASK, "Текущая задача: " + a.currentTask.taskComment);
                        /**
                         * Задача есть, операция не выбрана.
                         * Переход на экран списка операций текущей задачи.
                         */
                    }
                } else {
                    a.gotoLayout(LAYOUT_2_NO_TASK, "Нет текущей задачи, получите задание от диспетчера");
                    /**
                     * Нет ни текущей задачи, ни операции.
                     * Переходим на экран выбора задачи.
                     */
                    a.server.readTask();
                }
                break;

            case L2_BUTTON_TASK_SELECT: //
                a.gotoLayout(LAYOUT_4_TASK_SELECT, "Выберите задачу из списка");
                break;

            case L2_BUTTON_CANCEL:
                a.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            case L3_BUTTON_CANCEL:
                a.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            case L3_BUTTON_TASK_CONTINUE:
                a.gotoLayout(LAYOUT_5_OPER_SELECT, "");
                break;

            case L4_LIST_TASK_SELECT:
                a.btn_4_Accept.setVisibility(View.VISIBLE);
                break;

            case L4_BUTTON_CANCEL:
                a.btn_4_Accept.setVisibility(View.INVISIBLE);
                a.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            case L4_BUTTON_ACCEPT:      // Задача выбрана
                a.btn_4_Accept.setVisibility(View.INVISIBLE);
                // Установить текущую задачу
                a.storer.setTaskProperty_Current(getKeyTaskIdFromListView(), 1);
                a.currentTask.setTaskData();
                // Переходим к выбору операции
                a.gotoLayout(LAYOUT_5_OPER_SELECT, "");
                break;

            case L5_LIST_OPER_SELECT:

                break;

            case L5_BUTTON_CANCEL:
                a.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            case L5_BUTTON_ACCEPT:      // Начать выполнение операций задачи
                a.currentTask.setToActive();
                currentOperParam = new String[4];
                currentOperParam = a.storer.getFirstOperationForExecution();
                a.currentOper.set(
                        currentOperParam[0],
                        currentOperParam[1],
                        currentOperParam[2],
                        a.storer.getOperData(currentOperParam[0])[4]
                );
                a.currentOper.setCurrent();
                a.currentOper.setToActive();

                // Если опарция - загрузка, то на экран 8, иначе - 6
                if (a.currentOper.operIsLoad() == true) {
                    // Запускаем Получение показаний весов от терминала
                    a.weightDataFromDeviceReader_Start();
                    a.gotoLayout(LAYOUT_9_SERV_REQUEST, a.currentOper.getOperationInfoForView());

                } else {
                    a.gotoLayout(LAYOUT_6_SIMPLE_OPER, a.currentOper.getOperationInfoForView());
                }
                break;

            case L6_BUTTON_CANCEL:      // "Простая операция" - отмена
                a.currentTask.setToUnactive();
                a.currentOper.setToUnactive();
                a.gotoLayout(LAYOUT_1_BEGIN, "");       // Возврат на начальный экран
                break;

            case L6_BUTTON_COMPLETE:    // "Простая операция" - выполнено
                // Отчет о выполнении операции
                a.currentOper.setToComplete();
                // Проверить оставшиеся операции текущей задачи
                if (a.storer.getListOperationsForExecution() == null) {
                    a.currentTask.setToComplete();
                    a.gotoLayout(LAYOUT_8_TASK_COMPLETE, "");      // Все операции задачи завершены
                } else {
                    a.gotoLayout(LAYOUT_1_BEGIN, "");   // Если есть невыполненые операции
                }
                break;

            case L7_BUTTON_START:     // Начинаем непосредственно погрузку

                a.displayWeightParameters();

                // Можно передать на погрузчик сигнала "Начать"
                loadingMayBegin = true;
                // Начать передачу данных на погрузчик
                a.makeOperation_Load();
                // Деактивировать конпку "Начать"
                a.btn_7_Start.setVisibility(View.INVISIBLE);
                // Активировать конпку "Закончить"
                a.btn_7_Complete.setVisibility(View.VISIBLE);
                break;

            case L7_BUTTON_CANCEL:      // Погрузка - Отмена
                a.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            case L7_BUTTON_COMPLETE:    // Погрузка - Завершено
                a.weightDataToLoaderSender_Stop();
                a.weightDataFromDeviceReader_Stop();  // Остановить получение показаний весов
                a.loader.send(a.loader.msg_LoadStop(a.currentOper.operId));

                // Отчет о выполнении операции
                a.currentOper.setToComplete();

                // Проверить оставшиеся операции текущей задачи
                if (a.storer.getListOperationsForExecution() == null) {
                    a.currentTask.setToComplete();
                    a.gotoLayout(LAYOUT_8_TASK_COMPLETE, "");       // Все операции задачи завершены
                } else {
                    a.gotoLayout(LAYOUT_1_BEGIN, "");               // Если есть невыполненые операции
                }
                break;

            case L8_BUTTON_OK:                          // OK - task complete

                //
                new DBFunctions(a).operClear();
//                new DBFunctions(activity).taskClear();

                a.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            case L9_BUTTON_CANCEL:                      // Отказ от обслуживания
                a.currentTask.setToUnactive();
                a.currentOper.setToUnactive();
                a.gotoLayout(LAYOUT_1_BEGIN, "");
                break;

            case L9_BUTTON_ACCEPT:                      // Сервер подтвердил

                // Загруженный вес
                a.storer.weightLoaded=0;

                // Стартовый вес в миксере
                a.storer.weightStart = a.storer.weightCurrent;

                // Вычислить конечный вес в погрузчике
                a.storer.weightTarget = a.currentOper.loadValue + a.storer.weightCurrent;
                Log.i(logTAG, "конечный вес в погрузчике = " + a.storer.weightTarget);

                // Толеранс +
                a.storer.tolerancePlus =
                        (int) (a.currentOper.loadValue * Float.parseFloat(a.getString(R.string.LOADING_PERCENT_WEIGHT_TOLERANCE_UP)));

                // Параметры - на экран
                a.displayWeightParameters();

                //
                a.gotoLayout(LAYOUT_7_COMPLEX_OPER, ""); // Todo
                break;

            case L9_BUTTON_REJECT:                      // Сервер отказал
                a.currentTask.setToUnactive();
                a.currentOper.setToUnactive();
                a.gotoLayout(LAYOUT_1_BEGIN, "");
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
        Matcher m = p.matcher(a.taskSelect_SelectedValue);
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
        a.log("operSelect_SelectedValue=" + a.operSelect_SelectedValue);
        Pattern p = Pattern.compile("\\[([\\d]+)\\]");
        Matcher m = p.matcher(a.operSelect_ListItems[0]);
        if (m.find()) {
            return Long.parseLong(m.group().replace("[", "").replace("]", ""));
        } else {
            return 0;
        }
    }

}
