package org.and.intex_v2;

/**
 * Created by Андрей on 02.06.2017.
 * -------------------------------
 * Таблица параметров команд
 * -------------------------------
 * operName     -
 * value    -
 * -------------------------------
 */

public class IncomingMessageLineParams {
    public String
            name;
    public String
            value;

    /**
     * Конструктор
     */
    public IncomingMessageLineParams() {
    }

    public IncomingMessageLineParams(String name, String value) {
        this.name =
                name;
        this.value =
                value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
