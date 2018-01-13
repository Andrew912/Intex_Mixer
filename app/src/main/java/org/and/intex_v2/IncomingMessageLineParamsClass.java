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

public class IncomingMessageLineParamsClass {
    private String name;
    private String value;

    public IncomingMessageLineParamsClass() {
    }

    public IncomingMessageLineParamsClass(String name, String value) {
        this.name = name;
        this.value = value;
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
