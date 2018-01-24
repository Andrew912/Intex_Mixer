package org.and.intex_v2;

/**
 * Created by Андрей on 22.12.2017.
 */

public class Configuration {

    String
            logTag = "Configuration";
    MainActivity
            a;

    /**
     * ============================================================
     * Переменные среды не запоминаемые
     * =============================================================
     */

    String
            ipAddress;                      //
    String
            networkMask;                    //
    int
            terminalPort = 18080;           // Порты подключения весовых терминалов
    int
            terminalFindAddr = 80;          // Адрес в сети, с которого будет начат поиск терминала
    int
            mixerPort = 28080;              // Порты подключения миксеров
    boolean
            is_Connected_to_network;        //
    int
            terminalStartAddress = 150;     // Стартовый адрес для поиска терминала в сети
    String
            deviceName = "mixer.001";       //
    String
            terminalName = "mixerterm.001";       //
    int
            dataLoadBufferSize = 1024;      // Размер буфера для получения даннфх с сервера управления

    /**============================================================
     * Переменные среды, запоминаемые при приостановке приложения
     *=============================================================
     */

    /**
     * ============================================================
     * Переменные, знвачения которых показывают состояние системы,
     * которое надо будет восстановить по окнчании работы программы
     * =============================================================
     */

    boolean
            current_WiFi_status;          // Текущий системный статус Wi-Fi соединения

    /**
     * Конструктор
     *
     * @param mainActivity
     */
    public Configuration(MainActivity mainActivity) {
        a = mainActivity;

    }

    /**
     * IP-адрес
     * @param addr
     * @return
     */
    public String getIPaddress(int addr) {
        return networkMask + String.valueOf(addr);
    }
}
