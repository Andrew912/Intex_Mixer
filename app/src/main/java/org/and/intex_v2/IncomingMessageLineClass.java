package org.and.intex_v2;

/**
 * -------------------------------------------------------------------
 * Created by Андрей on 26.05.2017.
 * -------------------------------------------------------------------
 * Таблица, содерджащая строки команд после разбора входного потока на строки
 * num  - номер строки
 * line - текст строки
 * -------------------------------------------------------------------
 */

public class IncomingMessageLineClass {

    private int num;
    private String line;

    public IncomingMessageLineClass(int pNum, String pLine) {
        num = pNum;
        line = pLine;
    }

    public int Num() {
        return num;
    }

    public String Line() {
        return line;
    }
}

