package org.and.intex_v2;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Андрей on 22.07.2017.
 */

public class Messenger {

    enum MessageType {
        task,
        oper,
        gettask,
        getops
    }

    String
            messageHeader,          // Для сообщений на сервер
            messageHeader0;         // Для сообщений на погрузчик

    MainActivity activity;

    public Messenger(MainActivity activity) {
        this.activity = activity;
        messageHeader = pair("ver", this.activity.configurator.protocol()) +
                pair("dev", this.activity.configurator.deviceid()) +
                pair("pwd", this.activity.configurator.password());

        messageHeader0 = pair("device", this.activity.configurator.deviceid());
    }

    String time() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    String pair(String key, String val) {
        return key + "='" + val + "'";
    }

    public String msg_TaskReport_begin(String taskId) {
        /**
         * Task: begin
         */
        String retVar;
        retVar = messageHeader +
                pair(activity.getString(R.string.MESSAGE_COMMAND_TYPE), activity.getString(R.string.MESSAGE_CMD_REPORT_TASK)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_TASKID), taskId) +
                pair(activity.getString(R.string.MESSAGE_PARAM_STATUS), activity.getString(R.string.MESSAGE_STATUS_BEGIN)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_BEGIN), time());
        return retVar;
    }

    public String msg_TaskReport_end(String taskId) {
        /**
         * Task: end
         */
        String retVar;
        retVar = messageHeader +
                pair(activity.getString(R.string.MESSAGE_COMMAND_TYPE), activity.getString(R.string.MESSAGE_CMD_REPORT_TASK)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_TASKID), taskId) +
                pair(activity.getString(R.string.MESSAGE_PARAM_STATUS), activity.getString(R.string.MESSAGE_STATUS_END)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_END), time());
        return retVar;
    }

    public String msg_TaskReport_resume(String taskId) {
        /**
         * Task: resume
         */
        String retVar;
        retVar = messageHeader +
                pair(activity.getString(R.string.MESSAGE_COMMAND_TYPE), activity.getString(R.string.MESSAGE_CMD_REPORT_TASK)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_TASKID), taskId) +
                pair(activity.getString(R.string.MESSAGE_PARAM_STATUS), activity.getString(R.string.MESSAGE_STATUS_RESUME)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_RESUME), time());
        return retVar;
    }

    public String msg_TaskReport_suspend(String taskId) {
        /**
         * Task: suspend
         */
        String retVar;
        retVar = messageHeader +
                pair(activity.getString(R.string.MESSAGE_COMMAND_TYPE), activity.getString(R.string.MESSAGE_CMD_REPORT_TASK)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_TASKID), taskId) +
                pair(activity.getString(R.string.MESSAGE_PARAM_STATUS), activity.getString(R.string.MESSAGE_STATUS_SUSPEND)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_SUSPEND), time());
        return retVar;
    }

    public String msg_TaskReport_cancel(String taskId) {
        /**
         * Task: cancel
         */
        String retVar;
        retVar = messageHeader +
                pair(activity.getString(R.string.MESSAGE_COMMAND_TYPE), activity.getString(R.string.MESSAGE_CMD_REPORT_TASK)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_TASKID), taskId) +
                pair(activity.getString(R.string.MESSAGE_PARAM_STATUS), activity.getString(R.string.MESSAGE_STATUS_CANCEL)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_CANCEL), time());
        return retVar;
    }

    public String msg_OperReport_cancel(String operId) {
        /**
         * CurrentOper: cancel
         */
        String retVar;
        retVar = messageHeader +
                pair(activity.getString(R.string.MESSAGE_COMMAND_TYPE), activity.getString(R.string.MESSAGE_CMD_REPORT_OPER)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_OPERID), operId) +
                pair(activity.getString(R.string.MESSAGE_PARAM_STATUS), activity.getString(R.string.MESSAGE_STATUS_CANCEL)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_CANCEL), time());
        return retVar;
    }

    public String msg_OperReport_suspend(String operId) {
        /**
         * CurrentOper: suspend
         */
        String retVar;
        retVar = messageHeader +
                pair(activity.getString(R.string.MESSAGE_COMMAND_TYPE), activity.getString(R.string.MESSAGE_CMD_REPORT_OPER)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_OPERID), operId) +
                pair(activity.getString(R.string.MESSAGE_PARAM_STATUS), activity.getString(R.string.MESSAGE_STATUS_SUSPEND)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_SUSPEND), time());
        return retVar;
    }

    public String msg_OperReport_resume(String operId) {
        /**
         * CurrentOper: resume
         */
        String retVar;
        retVar = messageHeader +
                pair(activity.getString(R.string.MESSAGE_COMMAND_TYPE), activity.getString(R.string.MESSAGE_CMD_REPORT_OPER)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_OPERID), operId) +
                pair(activity.getString(R.string.MESSAGE_PARAM_STATUS), activity.getString(R.string.MESSAGE_STATUS_RESUME)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_RESUME), time());
        return retVar;
    }

    public String msg_OperReport_begin(String operId) {
        /**
         * CurrentOper: suspend
         */
        String retVar;
        retVar = messageHeader +
                pair(activity.getString(R.string.MESSAGE_COMMAND_TYPE), activity.getString(R.string.MESSAGE_CMD_REPORT_OPER)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_OPERID), operId) +
                pair(activity.getString(R.string.MESSAGE_PARAM_STATUS), activity.getString(R.string.MESSAGE_STATUS_BEGIN)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_BEGIN), time());
        return retVar;
    }

    /**
     * Отчет о загрузке
     * @param operId
     * @return
     */
    public String msg_OperReport_end(String operId) {
        /**
         * CurrentOper: end
         */
        String retVar;
        retVar = messageHeader +
                pair(activity.getString(R.string.MESSAGE_COMMAND_TYPE), activity.getString(R.string.MESSAGE_CMD_REPORT_OPER)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_OPERID), operId) +
                pair(activity.getString(R.string.MESSAGE_PARAM_STATUS), activity.getString(R.string.MESSAGE_STATUS_END)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_END), time());
        if (activity.currentOper.operIsLoad()) {
            retVar = retVar +
                    pair("value", String.valueOf(activity.storer.weightLoaded));
        }
        return retVar;
    }

    public String msg_OperReport_load(String operId) {
        /**
         * CurrentOper: load
         */
        String retVar;
        retVar = messageHeader +
                pair(activity.getString(R.string.MESSAGE_COMMAND_TYPE), activity.getString(R.string.MESSAGE_CMD_REPORT_OPER)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_OPERID), operId) +
                pair(activity.getString(R.string.MESSAGE_PARAM_STATUS), activity.getString(R.string.MESSAGE_STATUS_LOAD)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_LOAD), time());
        return retVar;
    }

    public String msg_OperReport_deploy(String operId) {
        /**
         * CurrentOper: deploy
         */
        String retVar;
        retVar = messageHeader +
                pair(activity.getString(R.string.MESSAGE_COMMAND_TYPE), activity.getString(R.string.MESSAGE_CMD_REPORT_OPER)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_OPERID), operId) +
                pair(activity.getString(R.string.MESSAGE_PARAM_STATUS), activity.getString(R.string.MESSAGE_STATUS_DEPLOY)) +
                pair(activity.getString(R.string.MESSAGE_PARAM_DEPLOY), time());
        return retVar;
    }

    String messageHeaderForDevice() {
        return "" +
                pair("dev", activity.currentOper.getParam("dev")) +
                pair("username", activity.currentOper.getParam("dev")) +
                pair("task", activity.currentOper.getParam("task")) +
                pair("server", activity.currentOper.getParam("server"));
    }

    public String msg_ToLoader_ServiceRequest() {
        /**
         * Запрос на обслуживание сервером (погрузчиком)
         */
        String retVar;
        retVar = messageHeader0
                + pair("cmd", "load")
                + pair(activity.getString(R.string.MESSAGE_PARAM_OPERID), activity.currentOper.getParam("oper"))
                + pair("feed", activity.currentOper.getParam("feedn"))
                + pair("value", activity.currentOper.getParam("value"))
        ;

        return retVar;
    }

    public String msg_ToLoader_LoadBegin() {
        /**
         * Начало погрузки
         */
        String retVar;
        retVar = messageHeader0
                + pair(activity.getString(R.string.MESSAGE_COMMAND_TYPE), "load")
                + pair(activity.getString(R.string.MESSAGE_PARAM_OPERID), activity.currentOper.getParam("oper"))
                + pair("feed", activity.currentOper.getParam("feedn"))
                + pair("weight", "----")
                + pair("status", "begin")
        ;

        return retVar;
    }

    public String msg_ToLoader_LoadStop() {
        /**
         * Окончание погрузки
         */
        String retVar;
        retVar = messageHeader0
                + pair(activity.getString(R.string.MESSAGE_COMMAND_TYPE), "stop")
                + pair(activity.getString(R.string.MESSAGE_PARAM_OPERID), activity.currentOper.getParam("oper"))
                + pair("status", "stop")
        ;
        return retVar;
    }

    public String msg_ToLoader_SendWeight() {
        /**
         * Данные весов
         */
        String retVar;
        retVar = messageHeader0
                + pair("weight", String.valueOf(activity.storer.weightRemain))
                + pair(activity.getString(R.string.MESSAGE_COMMAND_TYPE), "weight")
        ;

        return retVar;
    }

}
