package org.and.intex_v2;

import android.widget.Toast;

import static org.and.intex_v2.DBHandler.PARAMETER_VALUE;

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
            terminalAddress;                      //
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
            terminalStartAddress = 35;      // Стартовый адрес для поиска терминала в сети
    String
            deviceName = "mixer.001";       //
    String
            terminalName = "mixerterm.001"; //
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
            current_WiFi_status;            // Текущий системный статус Wi-Fi соединения

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
     *
     * @param addr
     * @return
     */
    public String getIPaddress(int addr) {
        return networkMask + String.valueOf(addr);
    }

    /**
     * Обновляет значения параметров конфигурации
     */
    public void paramRefresh() {
        String[] tmp;
        tmp = a.db.paramGet("MixerName");      //
        if (tmp != null) {
            deviceName = tmp[PARAMETER_VALUE];
        }
        tmp = a.db.paramGet("MixerTermName");
        if(tmp != null) {
            terminalName = tmp[PARAMETER_VALUE];
        }
        // Полный адрес терминала в сети
        terminalAddress
                = a.db.get_Device_Addr_from_DB(networkMask, terminalName)[a.net.SRV_ADDR];
        Toast.makeText(a.getApplicationContext(), "Терминал:" + terminalAddress, Toast.LENGTH_LONG).show();
    }

    /**
     * Обновить адрес весового терминала
     */

    public void termAddrRefresh() {
        terminalAddress
                = a.db.get_Device_Addr_from_DB(networkMask, terminalName)[a.net.SRV_ADDR];
        Toast.makeText(a.getApplicationContext(), "Терминал:" + terminalAddress, Toast.LENGTH_LONG).show();
    }

}
