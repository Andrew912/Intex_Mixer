package org.and.intex_v2;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Андрей on 22.12.2017.
 */

public class NetworkHandler {


    int                         // Индексы возвращаемого значения String [] в функциях
            SRV_NAME = 0,
            SRV_ADDR = 1,
            SRV_PORT = 2;

    String
            logTag = "NetworkHandler";
    MainActivity
            activity;
    public static final String
            EMPTY_IP = "0.0.0.0";

    public NetworkHandler(MainActivity mainActivity) {
        this.activity = mainActivity;

        turn_WIFI_on();

        if (isConnectedToNetwork()) {
            activity.conf.is_Connected_to_network = true;
            activity.conf.ipAddress = get_My_IP();
            activity.conf.networkMask = get_My_Net_Mask(activity.conf.ipAddress);
            Log.i(logTag, "IPaddr  = " + activity.conf.ipAddress);
            Log.i(logTag, "netmask = " + activity.conf.networkMask);
        } else {
            /**
             * Тут надо подумать, что делать, если к сети устройство не подключилось
             */
            activity.conf.is_Connected_to_network = false;
            activity.sayToast(activity.getString(R.string.NETWORK_NOT_CONNECTED_WIFI));
        }
    }

    /**
     * Определяет, подключено ли устройство к сети Wi-Fi
     *
     * @return
     */
    boolean isConnectedToNetwork() {
        WifiManager wifiMan = (WifiManager) activity.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        if (ipAddress == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Определяет адрес устройства в сети Wi-Fi
     *
     * @return
     */
    String get_My_IP() {
        WifiManager wifiMan = (WifiManager) activity.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        Log.i(logTag, "ipAddress=" + ipAddress);
        String ip;
        if (ipAddress == 0) {
            ip = "0.0.0.0";
        } else {
            ip = String.format(
                    "%s.%s.%s.%s",
                    (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff));
        }
        Log.i(logTag, "ip=" + ip);
        return ip;
    }

    /**
     * Вычисляет маску сети для поиска новых устройств
     *
     * @param ip
     * @return
     */
    String get_My_Net_Mask(String ip) {
        Pattern pattern = Pattern.compile("(\\d+.\\d+.\\d+.)");
        Matcher matcher = pattern.matcher(ip);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return ip;
        }
    }

    /**
     * Wi-Fi turn ON
     */
    void turn_WIFI_on() {
        WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) wifiManager.setWifiEnabled(true);
    }

    /**
     * Wi-Fi turn OFF
     */
    void turn_WIFI_off() {
        WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) wifiManager.setWifiEnabled(false);
    }

    /**
     * Current system Wi-Fi status
     *
     * @return
     */
    boolean get_Current_WiFi_Status() {
        WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * Поиск всех устройств в сети с заданным в Configuration.terminalPort портом
     *
     * @param config
     */
    void findAllServersInNetwork(Configuration config) throws Exception {
        // Выделяем IP-адрес из полного адреса
        int myAddr = extractAddress(config.ipAddress);
        Log.i(logTag, "myAddr=" + myAddr);
        // Проходим по всему диапазону адресов
        for (int i = 66; i < 90; i++) {
            if (i != myAddr) {
                tryConnectToAllServers(config.networkMask + i, config.terminalPort);
            }
        }
    }

    /**
     * Пробуем подключиться к серверу
     *
     * @param serverAddr
     * @param serverPort
     */
    void tryConnectToAllServers(String serverAddr, int serverPort) throws Exception {
        String retVar;
        Log.i("tryConnectToAllServers", "serverAddr=" + serverAddr + ", serverPort=" + serverPort);
        ServerPingClass serverPing = new ServerPingClass(activity, serverAddr, serverPort);
        activity.toStatusLineNoBlink("ServerPingClass=" + activity.numOfServerPingClasses);
        if (serverPing != null) {
            retVar = serverPing.readServerName();
            Log.i(logTag, "retVar=" + retVar);
        }
    }

    /**
     * Поиск в сети указанного устройства
     *
     * @param config     - класс-конфигуратор: из него поллучаются собственный адрес и маска сети
     * @param serverName - имя
     * @param serverAddr - стартовый адрес для поиска, полученный из DNS
     * @param serverPort - порт
     * @return String[2]
     * [0] - адрес
     * [1] - порт
     */
    public String[] findServerInNetwork(Configuration config, String serverName, String serverAddr, int serverPort) throws Exception {
        activity.toStatusLineBlink("Find server = " + serverName);
        Log.i("Поиск сервера в сетке", "Find server = " + serverName + " at " + serverAddr);
        String[] serverParameters = {null, null, null};

        /**
         * Проверяем, нет ли сервера с заданным именем по указанному адресу
         */
        serverParameters[0] = tryConnectToServer(serverName, serverAddr, serverPort);
        if (serverParameters[SRV_NAME] != null) {
            if (serverParameters[SRV_NAME].equals(serverName)) {
                Log.i("Поиск сервера в сетке 0", "Server name = " + serverParameters[SRV_NAME]);
                return serverParameters;
            }
        }
        /**
         * Если сервер с указанными параметрами не найден, то проводим поиск по всей подсети
         */
        int tryAddr = 0;
        // Выделяем IP-адрес из полного адреса
        int myAddr = extractAddress(serverAddr);
        // А теперь проходим по всем адресам, если отвечает, то типа нашли
        for (int i = 1; i < 254; i++) {
            // На адрес вниз
            tryAddr = myAddr - i;
            if (tryAddr > 1) {
                serverParameters[0] = tryConnectToServer(serverName, config.networkMask + tryAddr, serverPort);
                if (serverParameters[SRV_NAME] != null) {
                    if (serverParameters[SRV_NAME].equals(serverName)) {
                        Log.i("Поиск сервера в сетке 1", "Server name = " + serverParameters[SRV_NAME]);
                        return serverParameters;
                    }
                }
            }
            // На адрес вверх
            tryAddr = myAddr + i;
            if (tryAddr < 255) {
                serverParameters[0] = tryConnectToServer(serverName, config.networkMask + tryAddr, serverPort);
                if (serverParameters[SRV_NAME] != null) {
                    if (serverParameters[SRV_NAME].equals(serverName)) {
                        Log.i("Поиск сервера в сетке 2", "Server name = " + serverParameters[SRV_NAME]);
                        return serverParameters;
                    }
                }
            }
        }
        // В данном случае ничего нужного в сетке не нашлось
        Log.i("Поиск сервера в сетке 3", "Server name = " + serverParameters[SRV_NAME]);
        return serverParameters;
    }

    /**
     * Пытаемся подключиться к конкретному серверу
     *
     * @param serverName
     * @param serverAddr
     * @param serverPort
     * @return
     */
    public String tryConnectToServer(String serverName, String serverAddr, int serverPort) {
        String returnVar;
        ServerPingClass serverPing =
                new ServerPingClass(activity, serverAddr, serverPort);
        returnVar = serverPing.readServerName();
        return returnVar;
    }

    /**
     * Выделяет последниюю группу цифр IP-адреса, то есть адрес устройства в сети
     *
     * @param address
     * @return
     */

    public int extractAddress(String address) {
        int myAddr = 0;
        Pattern pattern = Pattern.compile(activity.getString(R.string.pattern_extract_My_IP_address));
        Matcher matcher = pattern.matcher(address);
        if (matcher.find()) {
            myAddr = Integer.parseInt(matcher.group());
        }
        return myAddr;
    }
}
