package org.and.intex_v2;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;

/**
 * Created by Андрей on 29.01.2018.
 */

public class LayoutClass {

    /* Лайаут */
    public LinearLayout
            myLayout;

    /* Таймеры */
    public final int
            DEFAULT = 0;
    public Timer[]
            timer;
    public int
            timer_size = 0;

    /* Кнопки */
    public final int
            OK = 0,
            CANCEL = 1;
    public Button[]
            btn;
    public int
            btn_size = 0;

    /* Текстовые поля */
    public final int
            HEADER = 0,
            INFO = 1;
    public TextView[]
            text;
    public int
            text_size = 0;

    /* Параметры экрана */
    boolean
            active = false;

    /* Результат поиска */
    boolean
            findResult;

    /* Различные переменные */
    int
            currentActiveLayout;        // Активный экран на момент активации
    LayoutClass
            layoutToReturn;             // Экран, который будет активирован по окончании поиска

    /**
     * Конструктор
     *
     * @param pLayout
     * @param pTimers
     * @param pButtons
     * @param pText
     */
    public LayoutClass(
            LinearLayout
                    pLayout,        // Лайоут, на который ссылается объект
            Timer[]
                    pTimers,        // Таймеры
            TextView[]
                    pText,          // Текстовые поля в лайоуте
            String[]
                    pTextText,      // Текст, который надо вывести в соответствующих текстовых полях
            Button[]
                    pButtons,       // Кнопки
            String[]
                    pBtnText        // Текст кнопок
    ) {
        myLayout
                = pLayout;
        timer
                = pTimers;
        timer_size
                = timer.length;
        btn
                = pButtons;
        btn_size
                = btn.length;
        text
                = pText;
        text_size
                = text.length;

        /**
         * Непосредственно инициализация
         */
        // Поля
        Log.i("***", "text= " + text);
        if (pTextText != null) {
            for (int i = 0; i < text_size; i++) {
                text[i].setText(pTextText[i]);
            }
        }
        // Кнопки
        for (int i = 0; i < btn_size; i++) {
            btn[i].setText(pBtnText[i]);
        }
    }

    /**
     * Вывод текста в текстовые поля и надписи кнопок
     *
     * @param pTextText
     * @param pBtnText
     */
    void setText(
            String[]
                    pTextText,
            String[]
                    pBtnText
    ) {
        for (int i = 0; i < text_size; i++)
            if (pTextText[i] != null)
                text[i].setText(pTextText[i]);
        if (pBtnText != null) {
            for (int i = 0; i < btn_size; i++)
                if (pBtnText[i] != null)
                    btn[i].setText(pBtnText[i]);
        }
    }

    /**
     * Активация данного экрана
     *
     * @param pTextText
     * @param pBtnText
     */
    public void Activate(
            String[]
                    pTextText,
            String[]
                    pBtnText,
            LinearLayout[]
                    layouts,
            LayoutClass
                    pLayoutToReturn,
            Button
                    pButtonToClick      // "Нажать" кнопку при возврате управления
    ) {
        // Сохраняем номер текущего активного (видимого) экрана
        currentActiveLayout
                = getCurrentActiveLayout(layouts, myLayout);

        // Лайаут, на который перейдем по окончании поиска
        layoutToReturn
                = pLayoutToReturn;

        // Гасим экраны
        AllLayoutsOff(layouts);

        // Делаем видимым "этот" экран
        myLayout
                .setVisibility(View.VISIBLE);

        Log.i("***", "Видимость: " + String.valueOf(myLayout.getVisibility()));

        // Устанавливаем надписи в тексте и кнопках
        setText(
                pTextText != null ? pTextText : null,
                pBtnText != null ? pBtnText : null);

        // "Нажимаем" кнопку
        if (pButtonToClick != null) {
            pButtonToClick.callOnClick();
        }
    }

    /**
     * Гасим все экраны
     */
    void AllLayoutsOff(LinearLayout[] layouts) {
        // Гасим все экраны
        for (int i = 0; i < layouts.length; i++) {
            layouts[i].setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Определение текущего активного экрана
     *
     * @param layouts
     * @return
     */
    int getCurrentActiveLayout(LinearLayout[] layouts, LinearLayout myLayout) {
        int retV = -1;
        for (int i = 0; i < layouts.length; i++) {
            if (layouts[i] != myLayout) {
                if (layouts[i].getVisibility() == View.VISIBLE) {
                    Log.i("***", "i=" + i);
                    retV = i;
                    break;
                }
            }
        }
        return retV;
    }

    /**
     * Возвращает активность вызывающему экрану
     */
    void Deactivate(
            LinearLayout[]
                    layouts
    ) {
        // Делаем невидимым "этот" экран
        myLayout
                .setVisibility(View.INVISIBLE);

        if (layoutToReturn != null) {
            layoutToReturn.Activate(
                    null,
                    null,
                    layouts,
                    null,
                    null
            );
        } else {
            // Делаем видимым сохраненный экран
            layouts[currentActiveLayout]
                    .setVisibility(View.VISIBLE);
        }
    }
}
