package org.and.intex_v2;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Андрей on 04.08.2017.
 * <p>
 * Формирование строк сообщений-запросов для отправки серверу управления
 */

public class MessageMaker {

    MainActivity a;

    private static final String
            eol = "\r\n\r\n";
    private String
            header;

    /**
     * Конструктор
     *
     * @param activity
     */
    public MessageMaker(MainActivity activity) {
        this.a
                = activity;
        header
                = "ver='" + a.getString(R.string.CONFIG_PROTOCOL) + "'dev='" + a.conf.deviceName + "'pwd='" + a.conf.devicePass + "'";
    }

    public String request_TaskLisk() {
        return header +
                "cmd='gettask'" + eol;
    }

    public String request_OperList(String taskId) {
        return header +
                "cmd='getops'task='" + taskId + "'" + eol;
    }

    public String report_OperBegin(String operId) {
        return header +
                "cmd='oper'oper='" + operId + "'operStatus='begin'begin='" + time() + "'" + eol;
    }

    /**
     * Дата в формате "yyyyMMdd_HHmmss" для формирования протокола
     *
     * @return
     */
    String time() {
        SimpleDateFormat sdf
                = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime
                = sdf.format(new Date());
        return
                currentDateandTime;
    }

    public String report_TaskBegin(String taskId) {
        return header +
                "cmd='task'task='" + taskId + "'operStatus='begin'begin='" + time() + "'" + eol;
    }

    public String report_TaskEnd(String taskId) {
        return header +
                "cmd='task'task='" + taskId + "'operStatus='end'end='" + time() + "'" + eol;
    }

    public String report_TaskSuspend(String taskId) {
        return header +
                "cmd='task'task='" + taskId + "'operStatus='suspend'suspend='" + time() + "'" + eol;
    }

    public String report_TaskResume(String taskId) {
        return header +
                "cmd='task'task='" + taskId + "'operStatus='resume'resume='" + time() + "'" + eol;
    }

    public String report_OperEnd(String operId, String value) {
        return header +
                "cmd='oper'oper='" + operId + "'operStatus='end'end='" + time() + "'value='" + value + "'" + eol;
    }

    public String report_OperSuspend(String operId) {
        return header +
                "cmd='oper'oper='" + operId + "'operStatus='suspend'suspend='" + time() + "'" + eol;
    }

    public String report_OperResume(String operId) {
        return header +
                "cmd='oper'oper='" + operId + "'operStatus='resume'resume='" + time() + "'" + eol;
    }

    public String ping(String msgId) {
        return header +
                "cmd='ping'msgid='" + msgId + "time=" + time() + "'" + eol;
    }
}
