package org.and.intex_v2;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Андрей on 27.06.2017.
 */

public class Parsed_Message {
    MainActivity activity;
    ArrayList<IncomingMessageLineParamsClass> parameters;
    String key;
    String dev;
    int num;
    boolean logging = false;

    public Parsed_Message(MainActivity activity, String inS) {
        this.activity = activity;
        extractParams(inS);

        for (IncomingMessageLineParamsClass temp : parameters) {
            activity.log(logging, temp.getName() + "=" + temp.getValue());
        }
        key = getParam(activity.getString(R.string.MSG_COMMAND_KEYWORD));
        dev = getParam(activity.getString(R.string.MESSAGE_USER_ID_KEYWORD));

        activity.log(logging, "key=" + key);
        activity.log(logging, "dev=" + dev);
    }

    public boolean getStop() {
        return getParam(activity.getString(R.string.MSG_STATUS_KEYWORD)).equals(activity.getString(R.string.MSG_STATUS_IS_STOP));
    }

    public boolean getBegin() {
        return getParam(activity.getString(R.string.MSG_STATUS_KEYWORD)).equals(activity.getString(R.string.MSG_STATUS_IS_BEGIN));
    }

    public String getAccept() {
        return getParam(activity.getString(R.string.MSG_REQUEST_ACCEPT_KEYWORD));
    }

    public String getCommand() {
        return getParam(activity.getString(R.string.MSG_COMMAND_KEYWORD));
    }

    public String getDeviceId() {
        return getParam(activity.getString(R.string.MESSAGE_DEVICE_ID_KEYWORD));
    }

    public String getDeviceName() {
        return getParam(activity.getString(R.string.MESSAGE_DEVICE_NAME_KEYWORD));
    }

    public String getServerId() {
        return getParam(activity.getString(R.string.MESSAGE_SERVER_ID_KEYWORD));
    }

    public String getServerName() {
        return getParam(activity.getString(R.string.MESSAGE_SERVER_NAME_KEYWORD));
    }

    public String getStatus() {
        return getParam(activity.getString(R.string.MSG_STATUS_KEYWORD));
    }

    public String getTaskId() {
        return getParam(activity.getString(R.string.MESSAGE_TASK_ID_KEYWORD));
    }

    public String getValue() {
        return getParam(activity.getString(R.string.MESSAGE_VALUE_KEYWORD));
    }

    public String getWeight() {
        return getParam(activity.getString(R.string.MESSAGE_WEIGHT_KEYWORD));
    }

    public String getFeedId() {
        return getParam(activity.getString(R.string.MESSAGE_FEED_ID_KEYWORD));
    }

    public String getFeedName() {
        return getParam(activity.getString(R.string.MESSAGE_FEED_NAME_KEYWORD));
    }

    public String getParam(String parameterName) {
        for (IncomingMessageLineParamsClass p : parameters) {
            if (p.getName().equals(parameterName)) {
                return p.getValue();
            }
        }
        return "";
    }

    public boolean getParamNow(String parameterName) {
        for (IncomingMessageLineParamsClass p : parameters) {
            if (p.getName().equals(parameterName)) {
                return true;
            }
        }
        return false;
    }

    public void extractParams(String inS) {
        activity.log(logging, inS);
        // Выделяем отдельные команды
        Pattern pattern = Pattern.compile(activity.getString(R.string.pattern_Cmd_Name) + "=\'" + activity.getString(R.string.pattern_Cmd_Value) + "\'");
        Matcher matcher = pattern.matcher(inS);
        parameters = new ArrayList<>();
        while (matcher.find()) {
            parameters.add(extractParam(matcher.group()));
        }
    }

    private IncomingMessageLineParamsClass extractParam(String inS) {
        IncomingMessageLineParamsClass retV = null;
        Pattern patternOfName = Pattern.compile("^" + activity.getString(R.string.pattern_Cmd_Name));
        Matcher matcherOfName = patternOfName.matcher(inS);
        if (matcherOfName.find()) {
            Pattern patternOfValue = Pattern.compile("=\'" + activity.getString(R.string.pattern_Cmd_Value) + "\'$");
            Matcher matcherOfValue = patternOfValue.matcher(inS);
            if (matcherOfValue.find()) {
                retV = new IncomingMessageLineParamsClass(matcherOfName.group(), matcherOfValue.group().replace("'", "").replace("=", ""));
            }
        }
        return retV;
    }
}
