package org.and.intex_v2;

import android.util.Log;

/**
 * Created by Андрей on 03.08.2017.
 * Кольцевой буфер для временного хранения строк
 */

public class CycleBuffer {

    String logTAG = "CycleBuffer";

    int size;
    int len;
    int pointBeg;
    int pointEnd;

    String[] buffer;

    // Конструктор
    public CycleBuffer(int pSize) {
        if (pSize == 0) {
            size = 100;
        } else {
            size = pSize;
        }
        len = 0;
        pointBeg = 0;
        pointEnd = 0;
        buffer = new String[size];
    }

    // Получить элемент
    public String get() {
        int l = len;
        int p = pointBeg;
        if (pointBeg <= pointEnd - 1) {
            pointBeg++;
        } else {
            if (pointBeg < size - 1) {
                pointBeg++;
            } else {
                pointBeg = 0;
            }
        }
        getLen();
        Log.i(logTAG, "len=" + l + ", pointBeg=" + pointBeg + ", pointEnd=" + pointEnd + ", len=" + len + ", buffer[p]=" + buffer[p]);
        return buffer[p];
    }

    // Записать элемент
    public void put(String s) {
        if (pointEnd < size - 1) {
            pointEnd++;
            buffer[pointEnd] = s;
            getLen();
        } else {
            Log.i(logTAG,"WARNING!!! Buffer overflow!");
            pointEnd = 0;
        }
    }

    // Длина буфера
    public int getLen() {
        if (pointEnd >= pointBeg) {
            len = pointEnd - pointBeg ;
        } else {
            len = size - pointBeg + pointEnd ;
        }
        return len;
    }
}
