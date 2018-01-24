package org.and.intex_v2;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Pattern;

/**
 * Created by Андрей on 29.12.2017.
 */

public class ServerPingClass {

    String
            logTAG = "Server PING class";
    MainActivity
            activity;
    String
            rs = null;
    String
            serverName = null;
    String
            socketAddr;
    String
            socketPort;
    int
            buffSize;
    IncomingMessage
            incomingMessage;
    String
            whatFindParam;                  // Номер (счетчик) запущенных классов ServerPingClass
    int
            whatFindParamI;

    /**
     * Конструктор
     *
     * @param activity
     * @param pAddr
     * @param pPort
     */
    public ServerPingClass(MainActivity activity, String pAddr, int pPort, int whatFind) {
        this.activity =
                activity;
        // Обозначаем свое присутствие
        activity.numOfServerPingClasses
                .add(whatFind, activity.numOfServerPingClasses.get(whatFind) + 1);

        socketAddr =
                pAddr;
        socketPort =
                String.valueOf(pPort);
        buffSize =
                128;
    }

    /**
     * Получение ответа от устройства по заданному адресу
     * Если на том конце находится устройство, которое поддерживает протокол, то оно
     * передаст строку вида "server='имя сервера'"
     *
     * @return
     */
    public String readServerName(int whatFind) {
        new ServerPingTaskClass().execute(new String[]{socketAddr, socketPort, String.valueOf(whatFind)});
        return serverName;
    }

    /**
     * Класс
     */
    class ServerPingTaskClass extends AsyncTask<String, String, Void> {
        int bufferSize = 64;

        @Override
        protected Void doInBackground(String... params) {
            if (activity.endServerFindCondition.get(Integer.parseInt(params[2])) == false) {
                serverName = null;
                Log.i(logTAG, "ServerExchangeClass_getOperations: params[0]=" + params[0] + ", params[1]=" + params[1] + ", params[2]=" + params[2]);
                whatFindParam = params[2];
                whatFindParamI = Integer.parseInt(whatFindParam);
                Socket socket;
                InputStream is;
                OutputStream os;
                String o = "who\n";                // Формирование запроса
                try {
                    InetAddress serverAddr = InetAddress.getByName(params[0]);
                    socket = new Socket(params[0], Integer.parseInt(params[1]));
                    if (socket.isConnected() == true) {
                        is = socket.getInputStream();
                        os = socket.getOutputStream();
                        byte[] buffer = o.getBytes();
                        os.write(buffer);
                        os.flush();
                        buffer = new byte[buffSize];
                        int read = is.read(buffer, 0, buffSize);
                        byte[] b = new byte[read];
                        System.arraycopy(buffer, 0, b, 0, read);
                        rs = new String(b).replace("\"", "--");
                        socket.close();
                        /**
                         * Фиксируем адрес+порт, если сервер ответил
                         * Теоретически можно эти параметры фиксировать и при верхнем вызове класса
                         * Надо посмотреть с точки зрения оптимальности, что удалить
                         */
                        activity.serverFound.get(whatFindParamI)[activity.net.SRV_ADDR] = params[0];
                        activity.serverFound.get(whatFindParamI)[activity.net.SRV_PORT] = params[1];
                    }
                    socket = null;
                } catch (Exception e) {
                    Log.i(logTAG, "Not connect: " + e);
                }
//            System.gc();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Если не объявлен стоп
            if (activity.endServerFindCondition.get(whatFindParamI) == false) {
                // Разбираем входящее сообщение
                incomingMessage = new IncomingMessage(activity, rs);
                // Пытаемся выделить имя сервера
                serverName = incomingMessage.getServerName();
                if (serverName != null) {
                    Log.i(logTAG, "-----------------------------");
                    Log.i(logTAG, "serverName=" + serverName);
                    Log.i(logTAG, "-----------------------------");
                    /**
                     * Вот тут мы фиксируем имя найденного сервера
                     */
                    activity.serverFound.get(whatFindParamI)[activity.net.SRV_NAME] = serverName;
                }
            }
            if (serverName != null) {
                activity.numOfServerPingClasses
                        .set(whatFindParamI, activity.numOfServerPingClasses.get(Integer.parseInt(whatFindParam)) - 1);
                /**
                 * А тут мы резко выводим на экран всякую информацию о происходящем
                 */
                activity.layout[activity.CurrentLayout]
                        .setVisibility(View.INVISIBLE);
                activity.CurrentLayout = activity.savedCurrentLayout;
                activity.layout[activity.CurrentLayout]
                        .setVisibility(View.VISIBLE);
                activity.serverFindResultToStatusLine("Сервер найден");
                activity.toTextView("Сервер " +
                        activity.serverFound.get(whatFindParamI)[activity.net.SRV_NAME] +
                        " найден по адресу " +
                        activity.serverFound.get(whatFindParamI)[activity.net.SRV_ADDR]);
                Log.i(getClass().getSimpleName(), "Сервер найден, БЛЯ!!!");
            }
        }

        // Разбор входящего потока на строки
        public CycleBufferClass ExtractToSourceInputLineBuffer(String inBuffer, int outBufferSize) {
            CycleBufferClass sourceInputLineBuffer = new CycleBufferClass(outBufferSize);
            int i = 0;
            String regEx = activity.getString(R.string.pattern_EndOfLine);
            Pattern ptnLine = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
            String[] lines = ptnLine.split(inBuffer);
            for (String line : lines) {
                if (line != null) {
                    sourceInputLineBuffer.put(line);
                }
            }
            sourceInputLineBuffer.get();
            return sourceInputLineBuffer;
        }
    }}

