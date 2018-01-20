package org.and.intex_v2;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Строка входяего сообщения
 */

public class IncomingMessageLine {

    MainActivity
            activity;
    int
            lineNo;                                 // Номер строки сообщения
    int
            messageType;                            // Тип строки сообщения
    public ArrayList<IncomingMessageLineParams>
            pairs;                                  // Параметры строки сообщения

    /**
     * Конструктор
     *
     * @param lineContent
     */
    public IncomingMessageLine(MainActivity activity, String lineContent, int lineNo) {
        this.activity =
                activity;
        this.lineNo =
                lineNo;
        pairs =
                recognizeParamsFromInputLine(lineContent);
    }

    /**
     * Разбор строки на пары "имя='значение'"
     *
     * @param inputLine
     */
    public ArrayList<IncomingMessageLineParams> recognizeParamsFromInputLine(String inputLine) {
        // Выделяем отдельные команды
        Pattern pattern =
                Pattern.compile(
                        activity.getString(R.string.pattern_Cmd_Name) + "=\'" +
                                activity.getString(R.string.pattern_Cmd_Value) + "\'");
        Matcher matcher =
                pattern.matcher(inputLine);
        ArrayList<IncomingMessageLineParams> parameters =
                new ArrayList<>();

        // Выделяем все пары "команда='значение'" во входной строке
        while (matcher.find()) {
            parameters.add(extractParamPair(matcher.group()));
        }
        return parameters;
    }

    private IncomingMessageLineParams extractParamPair(String inS) {
        IncomingMessageLineParams returnValue =
                null;
        Pattern patternOfName =
                Pattern.compile("^" + activity.getString(R.string.pattern_Cmd_Name));
        Matcher matcherOfName =
                patternOfName.matcher(inS);
        if (matcherOfName.find()) {
            Pattern patternOfValue = Pattern.compile("=\'" + activity.getString(R.string.pattern_Cmd_Value) + "\'$");
            Matcher matcherOfValue = patternOfValue.matcher(inS);
            if (matcherOfValue.find()) {
                returnValue = new IncomingMessageLineParams(matcherOfName.group(), matcherOfValue.group().replace("'", "").replace("=", ""));
            }
        }
        return returnValue;
    }

}
