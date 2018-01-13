package org.and.intex_v2;

/**
 * Created by Андрей on 21.07.2017.
 */

public class Configurator {

    MainActivity a;

    int CONTROL = 0, LOADER = 1, DEVICE = 2;

    public Configurator(MainActivity activity) {
        this.a = activity;
    }

    public String password() {
        return a.getString(R.string.CONFIG_PASSWORD);
    }

    public String deviceid() {
        return a.getString(R.string.CONFIG_DEVICE_ID);
    }

    public String protocol() {
        return a.getString(R.string.CONFIG_PROTOCOL);
    }

    public String CServer_Addr() {
        return new Param_Server(CONTROL).getIP();
    }

    public int CServer_Port() {
        return new Param_Server(CONTROL).getPort();
    }

    public int CServer_Buff() {
        return new Param_Server(CONTROL).getBuffSize();
    }

    public int CServer_Time() {
        return new Param_Server(CONTROL).getTime();
    }

    public int CServer_Ping() {
        return new Param_Server(CONTROL).getPing();
    }

    public String Loader_Addr() {
        return new Param_Server(LOADER).getIP();
    }

    public int Loader_Port() {
        return new Param_Server(LOADER).getPort();
    }

    public int Loader_Buff() {
        return new Param_Server(LOADER).getBuffSize();
    }

    public int Loader_Time() {
        return new Param_Server(LOADER).getTime();
    }

    public String Device_Addr() {
        return new Param_Server(DEVICE).getIP();
    }

    public int Device_Port() {
        return new Param_Server(DEVICE).getPort();
    }

    public int Device_Buff() {
        return new Param_Server(DEVICE).getBuffSize();
    }

    public int Device_Time() {
        return new Param_Server(DEVICE).getTime();
    }

}
