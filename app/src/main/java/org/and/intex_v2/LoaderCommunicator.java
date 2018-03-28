package org.and.intex_v2;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.and.intex_v2.LoaderCommunicator.DeviceWaitAnswerStatus.LOAD;
import static org.and.intex_v2.LoaderCommunicator.DeviceWaitAnswerStatus.REJECT;

/**
 * Created by Андрей on 06.08.2017.
 */

public class LoaderCommunicator {

    String
            logTAG = "LOADER Communicator: ";
    MainActivity
            activity;
    String
            rs = null;
    String
            deviceName;
    String
            socketAddr;
    int
            socketPort;
    int
            buffSize;
    String
            oper,           // Код операции
            value,          // Необходимо загрузить
            feed;           // Название компонента

    enum DeviceWaitAnswerStatus {
        NONE, REQUEST, REJECT, ACCEPT, LOAD, END
    }

    enum AnswerMessageType {
        OK, ERROR, YES, NO
    }

    DeviceWaitAnswerStatus
            deviceWaitAnswerStatus;

    AnswerMessageType
            answerMessageType;

    boolean
            channelFree,
            tryServiceRequest,
            continueSendWeight;
    String
            messageForSend;
    Timer
            timerServerRequest,         // Запрос на обслуживание
            timerServerSendWeight;      // Отправка данных погрузчику
    String
            msgServiceRequest,          // Запрос на обслуживание
            msgLoadingBegin,            // Сообщение о начале обслуживания
            msgSendWeight,              // Данные об остатке для погрузки
            msgLoadingStop;             // Сообщение об окончании обслуживания


    public LoaderCommunicator(MainActivity mainActivity) {
        this.activity =
                mainActivity;
        // Адрес терминала берем из конфигуратора
        // А вот как он там оказался, надо смотреть в конфигураторе
        socketAddr =
                "192.168.1.44";
//        socketAddr =
//                activity.conf.terminalAddress;
//        socketAddr =
//                mainActivity.conf.getIPaddress(mainActivity.conf.terminalStartAddress);
        socketPort =
                activity.conf.terminalPort;
        buffSize =
                activity.conf.dataLoadBufferSize;
        deviceName =
                activity.conf.deviceName;

        //
        tryServiceRequest = false;
        continueSendWeight = false;
        channelFree = true;
        deviceWaitAnswerStatus = DeviceWaitAnswerStatus.NONE;
    }

    // Остановить таймер управления запросом на обслуживание
    void dropTimerServerRequest() {
        if (timerServerRequest != null) {
            timerServerRequest.cancel();
        }
        tryServiceRequest = true;
        channelFree = true;
    }

//    // Остановить таймер управления отправкой данных погрузчику
//    void dropTimerServerSendWeight() {
//        if (timerServerSendWeight != null) {
//            timerServerSendWeight.cancel();
//        }
//    }

    void serverServiceRequest() {
        //  Начинаем попытки
        msgServiceRequest = activity.messenger.msg_ToLoader_ServiceRequest();
        msgLoadingBegin = activity.messenger.msg_ToLoader_LoadBegin();
        tryServiceRequest = true;
        deviceWaitAnswerStatus = DeviceWaitAnswerStatus.REQUEST;

        // Процедура, вызываемая по таймеру
        SendServiceRequest sendServiceRequest = new SendServiceRequest();

        // Запуcк таймера 1с + 1с
        timerServerRequest = new Timer();
        timerServerRequest.schedule(sendServiceRequest, 1000, 1000);
    }

    void serverSendWeightStop() {
        continueSendWeight = false;
    }

    void serverSendWeight() {
        //  Начинаем попытки
        continueSendWeight = true;
        if (timerServerSendWeight == null) {
            // Процедура, вызываемая по таймеру
            SendWeightData sendWeightData = new SendWeightData();
            // Запуcк таймера 0.5 с + 0.5 с
            timerServerSendWeight = new Timer();
            timerServerSendWeight.schedule(sendWeightData, 500, 500);
        }
    }

    class SendWeightData extends TimerTask {

        @Override
        public void run() {
            msgSendWeight = activity.messenger.msg_ToLoader_SendWeight();
            // Если можно продолжать попытки отправки запроса, то
            if (continueSendWeight == true) {
                // Отправить
                new LoaderSendMessagesClass().execute(activity.messenger.msg_ToLoader_SendWeight());
            } else {
                // Сбросить таймер
                dropTimerServerRequest();
//                mainActivity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mainActivity.b2.callOnClick();
//                    }
//                });
            }
        }
    }

    class SendServiceRequest extends TimerTask {

        @Override
        public void run() {
            // Если можно продолжать попытки отправки запроса, то
            if (tryServiceRequest == true) {
                // Отправить запрос на собслуживание
                new LoaderSendMessagesClass().execute(msgServiceRequest);
            } else {
                // Сбросить таймер
                dropTimerServerRequest();
//                mainActivity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mainActivity.b2.callOnClick();
//                    }
//                });
            }
        }
    }

    public void send(String messageForSend) {
        new LoaderSendMessagesClass().execute(messageForSend,socketAddr);
    }

    // Класс передачи сообщений на сервер
    class LoaderSendMessagesClass extends AsyncTask<String, Void, Void> {
        int bufferSize = 10;
        String[] o;

//        CycleBuffer sourceInputLineBuffer = new CycleBuffer(bufferSize);

        @Override
        protected Void doInBackground(String... params) {
//            Log.i(logTAG, "Loader Send Mail Messages Class: run");
            channelFree = false;
            Socket socket;
            InputStream is;
            OutputStream os;
            o = params;
            int oSize = o.length;
            try {
                Log.i("LoaderSendMessagesClass", "socketAddr=" + socketAddr);
                InetAddress serverAddr = InetAddress.getByName(socketAddr);
                socket = new Socket(serverAddr, socketPort);
                if (socket.isConnected() == true) {
                    is = socket.getInputStream();
                    os = socket.getOutputStream();
//                    for (int i = 0; i < oSize; i++) {
//                        Log.i(logTAG, "message to send= " + o[i]);
//                        byte[] buffer = o[i].getBytes();
                        Log.i(logTAG, "message to send= " + o[0]);
                        byte[] buffer = o[0].getBytes();
                        os.write(buffer);
                        os.flush();
                        buffer = new byte[buffSize];
                        int read = is.read(buffer, 0, buffSize);
                        byte[] b = new byte[read];
                        System.arraycopy(buffer, 0, b, 0, read);
                        rs = new String(b);
                        answerMessageType = statusReceivedMessage(rs);
                        Log.i(logTAG, "-----------------------------");
//                        Log.i(logTAG, "read=" + read);
//                        Log.i(logTAG, "-----------------------------");
                        Log.i(logTAG, rs);
                        Log.i(logTAG, "-----------------------------");
                        Log.i(logTAG, "answerMessageType=" + answerMessageType);
//                    }
                    socket.close();
                }
            } catch (Exception e) {
                Log.i(logTAG, "Ошибка подключения: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.i(logTAG, "onPostExecute...");
            channelFree = true;
            localController(answerMessageType, deviceWaitAnswerStatus);
        }
    }

    void localController(AnswerMessageType amType, DeviceWaitAnswerStatus dvaStatus) {
        Log.i("LOCAL", "localController: amType=" + amType + ", dvaStatus=" + dvaStatus);
        switch (dvaStatus) {
            case NONE:
                break;
            case REQUEST:
                if (amType == AnswerMessageType.NO) {
                    tryServiceRequest = false;
                    deviceWaitAnswerStatus = REJECT;
                    activity.btn_9_Reject.callOnClick();
                }
                if (amType == AnswerMessageType.YES) {
                    tryServiceRequest = false;
                    deviceWaitAnswerStatus = LOAD;

                    // Кнопка "Accept"
                    activity.btn_9_Accept.callOnClick();

                    // Если дано разрешение на начало погрузки
                    if (activity.controller.loadingMayBegin == true) {
                        // Сообщение о начале погрузки
                        send(msgLoadingBegin);
                    }
                }
                break;
            case ACCEPT:
                tryServiceRequest = false;
                break;
            case REJECT:
                break;
            case LOAD:
                if (amType == AnswerMessageType.YES) {
                    tryServiceRequest = false;
                    deviceWaitAnswerStatus = LOAD;
                    // Сообщение о начале погрузки
                    send(msgLoadingBegin);
                    // Кнопка "Accept"
                    activity.btn_9_Accept.callOnClick();
                }
                break;
            case END:
                break;
        }

//
//        switch (amType) {
//            case OK:
//                break;
//            case ERROR:
//                break;
//            case YES:
//                break;
//            case NO:
//                break;
//        }

    }

    // Статус отправки сообщения
    AnswerMessageType statusReceivedMessage(String resivedString) {
        String r = "0";
        Pattern pattern = Pattern.compile("st=\'\\d\'");
        Matcher matcher = pattern.matcher(resivedString);
        if (matcher.find()) {
            if (matcher.group().substring(4, 5).equals("0")) {
                return AnswerMessageType.OK;
            } else {
                return AnswerMessageType.ERROR;
            }
        }
        pattern = Pattern.compile("\'yes\'");
        matcher = pattern.matcher(resivedString);
        if (matcher.find()) {
            return AnswerMessageType.YES;
        }
        pattern = Pattern.compile("\'no\'");
        matcher = pattern.matcher(resivedString);
        if (matcher.find()) {
            return AnswerMessageType.NO;
        }
        return AnswerMessageType.OK;
    }

    String pair(String pKey, String pValue) {
        return pKey + "=\'" + pValue + "\'";
    }

    String msg_header() {
        return pair("device", deviceName);
    }

    public String msg_serviceRequest(String operId, String feed, String value) {
        return msg_header() +
                pair("cmd", "load") +
                pair("request", operId) +
                pair("feed", feed) +
                pair("value", value)
                ;
    }

    public String msg_LoadBegin(String operId, String weight) {
        return msg_header()
                + pair("cmd", "load")
                + pair("request", operId)
                + pair("feed", feed)
                + pair("status", "begin")
//                + pair("weight", mainActivity.weightCalc(Integer.parseInt(value)))
                + pair("value", value)
                ;
    }

    public String msg_Weight(String operId, String weight) {
        return msg_header()
                + pair("cmd", "weight")
//                + pair("weight", mainActivity.device.weightCalc(Integer.parseInt(value)))
                ;
    }

    public String msg_LoadStop(String operId) {
        return msg_header()
                + pair("cmd", "stop")
                + pair("oper", operId)
                ;
    }
}