package org.and.intex_v2;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Андрей on 01.01.2018.
 */

public class IncomingMessage {

    String
            logTag = "IncomingMessage class";
    MainActivity
            activity;
    ArrayList<IncomingMessageLine>
            incomingMessageLines;                              // Строки входящего сообщения
    int
            linesCount = 0;

    /**
     * Конструктор
     *
     * @param activity
     * @param incomingMessage - исходный текст входящего сообщения для разбора
     */
    public IncomingMessage(MainActivity activity, String incomingMessage) {

        this.activity =                activity;
        incomingMessageLines =                new ArrayList<>();
        recognize(incomingMessage);
//        Log.i(logTag, "Constructor complete="+incomingMessage);
    }

    public void recognize(String incomingMessage) {
        // Обнуляем массив строк перед распознаванием
        incomingMessageLines.clear();
//        Log.i("incomingMessage", "message=" + incomingMessage);
        if (incomingMessage == null) {
            return;
        }
        // Разделение сообщения на строки
        Pattern ptnLine =
                Pattern.compile(
                        activity.getString(R.string.pattern_EndOfLine),
                        Pattern.CASE_INSENSITIVE);
        // Разделяем сообщение на строки (\n)
        String[] lines =
                ptnLine.split(
                        incomingMessage);
        // Добавляем для каждой строки новый объект, одновременно разбираем строку на пары
        for (String line : lines) {
            if (line != null) {
//                Log.i(logTag, "line=" + line);
                incomingMessageLines.add(new IncomingMessageLine(activity, line, linesCount++));
            }
        }
        // Обнуляем строки - больше не нужны
        lines = null;
    }

    /**
     * Поиск в парах "имя='значение' пары с нужным именем"
     *
     * @param nameToFind - "имя" для поиска
     * @return - "значение" найденного "имени", если не найдено, то null
     */
    String findInPairsName(String nameToFind) {
        for (IncomingMessageLine incomingMessageLine : incomingMessageLines) {
            for (IncomingMessageLineParams incomingMessageLineParams : incomingMessageLine.pairs) {
                if (incomingMessageLineParams.name.equals(nameToFind)) {
                    return incomingMessageLineParams.value;
                }
            }
        }
        return null;
    }

    /**
     * Возвращает имя сервера из параметров, если оно там есть, иначе null
     *
     * @return
     */
    public String getServerName() {
        return findInPairsName(activity.getString(R.string.INCOMING_MESSAGE_PAIR_SERVER_DATA_SERVER));
    }

}
