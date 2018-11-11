package org.and.intex_v2;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by and on 06.09.2018.
 * <p>
 * Для поиска устройства в сети.
 * <p>
 * Задается:
 * имя устройства,
 * подсеть,
 * порт.
 * <p>
 * Определяется:
 * IP-адрес устройства.
 * <p>
 * Определяется путем пребора адресов в подсети.
 * Адрес, с которого придет ответ, содержащий звдвнное имя устройства, является адресом устройства.
 */

public class DevicePing {

    String logTag
            = "Device Ping Class";
    MainActivity
            mainActivity;
    LinearLayout
            mainSwitch;

    /* Тип сообщения - неизвестен, ответ устройства на пинг и пр. */
    enum MsgType {
        unknown,
        devicePingResponse
    }

    public String       /* Имя найденного устройства */
            name;
    public String       /* Адрес устройства */
            address;

    MyMessageReader
            myMsgReader;

    /**
     * Конструктор
     *
     * @param activity
     */
    public DevicePing(MainActivity activity, LinearLayout mainSwitch) {
        mainActivity
                = activity;
        this.mainSwitch
                = mainSwitch;
        myMsgReader
                = new MyMessageReader();
    }

    /**
     * Делает один пинг устройства с заданным адресом и портом.
     * Параметры:
     * 1. IP-адрес
     * 2. Порт
     * 3. Имя устройства
     *
     * @param deviceParams
     */
    public void pingDevice(final String[] deviceParams) {
        Thread thread;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                /* Вызов того, что надо вызвать */
                new DeviceSinglePingTaskClass().execute(deviceParams);
            }
        };
        thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Делает один пинг устройства с заданным адресом и портом.
     * Параметры:
     * 1. IP-адрес
     * 2. Порт
     * 3. Имя устройства
     *
     * @param deviceParams
     */
    public void findDevice(final String[] deviceParams) {

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainSwitch.setVisibility(View.INVISIBLE);
            }
        });

        Thread thread;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                /* Вызов того, что надо вызвать */
                new DeviceFindTaskClass().execute(deviceParams);
            }
        };
        thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Остановить поток
     *
     * @param thread
     */
    void threadStop(Thread thread) {
        /* Остановили поток выполнения, чтобы не мешал */
        if (thread != null) {
            Thread dummy = thread;
            thread = null;
            dummy.interrupt();
        }
    }

    /**
     * Непосредственно выполняет одиночный пинг.
     * Параметры:
     * 1. Маска подсети.
     * 2. Порт.
     * 3. Имя устройства, которое ищем.
     */
    class DeviceFindTaskClass extends AsyncTask<String, Void, String> {
        int buffSize = 128;

        /**
         * Действия в фоновом режиме
         *
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String... params) {
            Socket
                    socket;
            InputStream
                    inputStream;
            OutputStream
                    outputStream;
            boolean                         // Параметр для цикла: Продолжать поиск
                    whileContinue = true;
            String                          // Текущий адрес, по которому ведем поиск
                    ipAddress;
            int                             // Счетчик адресов при поиске, задаем из параметра 3
                    addressCounter = Integer.parseInt(params[3]);
            int                             // До какого адреса смотреть, задаем из параметра 4
                    addressLimit = Integer.parseInt(params[4]);

            while (whileContinue) {
                ipAddress = params[0] + Integer.toString(addressCounter);

                final String finalIpAddress = ipAddress;
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.statusLine.set(finalIpAddress);
                    }
                });

                 /* Пытаемся открыть соединение и провести обмен строками */
                try {
                    socket
                            = new Socket(ipAddress, Integer.parseInt(params[1]));
                    inputStream
                            = socket.getInputStream();
                    outputStream
                            = socket.getOutputStream();

                    /* Отправка строки байт. Строку берем прямо из MessageMaker */
                    byte[] buffer = "who\n".getBytes();
                    outputStream.write(buffer);
                    outputStream.flush();

                    /* Получение строки байт */
                    buffer = new byte[buffSize];
                    int read = inputStream.read(buffer, 0, buffSize);
                    byte[] b = new byte[read];
                    System.arraycopy(buffer, 0, b, 0, read);

                    /* Получили строку - ответ устройтства */
                    myMsgReader.readMsg(new String(b));

                    Log.i(logTag, "myMsgReader.name=" + myMsgReader.name + ", params[2]=" + params[2]);

                    /* Если имя устройства совпало с тем, которое ищем, то запишем адрес устройства */
                    if (myMsgReader.name.equals(params[2].toString())) {
                        name = myMsgReader.name;
                        address = ipAddress;
                        whileContinue = false;
                        Log.i(logTag, ipAddress + ": " + name);
                        /* Возвращаем для onPostExecute имя устройства */
                        return myMsgReader.name;
                    } else return null;

                } catch (Exception e) {
                    Log.i(logTag, ipAddress + ": not found");
                }
                if (addressCounter++ > addressLimit) return null;
            }
            /* Возвращаем для onPostExecute имя устройства */
            return null;
        }

        /**
         * Действия после выполнения обмена данными
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.i("***** onPostExecute", "name= " + name);

//            System.exit(0);
        }
    }

    /**
     * Непосредственно выполняет одиночный пинг
     */
    class DeviceSinglePingTaskClass extends AsyncTask<String, Void, String> {
        int buffSize = 128;

        /**
         * Действия в фоновом режиме
         *
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String... params) {
            Socket
                    socket;
            InputStream
                    inputStream;
            OutputStream
                    outputStream;

            /* Пытаемся открыть соединение и провести обмен строками */
            try {
                socket
                        = new Socket(params[0], Integer.parseInt(params[1]));
                inputStream
                        = socket.getInputStream();
                outputStream
                        = socket.getOutputStream();

                /* Отправка строки байт. Строку берем прямо из MessageMaker */
                byte[] buffer = "who\n".getBytes();
                outputStream.write(buffer);
                outputStream.flush();

                /* Получение строки байт */
                buffer = new byte[buffSize];
                int read = inputStream.read(buffer, 0, buffSize);
                byte[] b = new byte[read];
                System.arraycopy(buffer, 0, b, 0, read);

                /* Получили строку - ответ устройтства */
                myMsgReader.readMsg(new String(b));

                Log.i(logTag, "myMsgReader.name=" + myMsgReader.name + ", params[2]=" + params[2]);

                /* Если имя устройства совпало с тем, которое ищем, то запишем адрес устройства */
                if (myMsgReader.name.equals(params[2].toString())) {
                    name = myMsgReader.name;
                    address = params[0];
                    return myMsgReader.name;
                } else return null;

            } catch (Exception e) {
                Log.i(logTag, "NOT connected!!!");
                return null;
            }
            /* Возвращаем для onPostExecute имя устройства */
        }

        /**
         * Действия после выполнения обмена данными
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            /**
             * Устанавливаем признак обнаружения устройства в сети (DeviceConfig.found):
             * Если null или полученное имя устройства не совпадает с искомым, то - False,
             * если имя совпадает с искомым, то - True.
             */
            Log.i("*****", "name= " + name);
//            System.exit(0);
        }
    }

    public final class MyMessageReader {

        private String logTag = "Class MyMessageReader";
        /* Сообщение */
        Msg msg;

        /*  */
        String name;

        /* Паттерны разбора входящего сообщения */
        String patternCmdValue = "[\\w\\s\\@.:;%#/\\^\\?\\(\\)\\[\\]\\|\\/\\+\\-\\*]+";
        String patternEndOfLine = "\n";
        String patternCmdName = "\\w+";

        /**
         * Пара "ключ-значение" (КЗ) из сообщения
         */
        class MsgPair {
            String key;
            String value;

            public MsgPair(String key, String value) {
                this.key = key;
                this.value = value;
            }
        }

        /**
         * Сообщение, которое подвергается обработке. Со всеми его приблудами.
         * При создании класса параметром передается строка - исходное сообщение.
         * Она разбирается на пары "КЗ" в MsgBody.
         */
        class Msg {
            boolean
                    empty;          /* Пустое собщение */
            MsgType
                    msgType;        /* Тип сообщения */
            ArrayList<MsgPair>
                    msgBody;        /* Здесь будут после разбора содержаться пары "КЗ" конкретного сообщения */
            String
                    deviceName;     /* Имя устройства (если устройство его сообщило) */

            /**
             * Конструктор
             *
             * @param source
             */
            public Msg(String source) {
                msgBody = new ArrayList<>();
                parceMsg(source);
                empty = msgBody.isEmpty();
                if (!empty) analizeMsg();
            }

            /**
             * Анализирует сообщение на предмет его назначения, типа и пр.
             * на основании имен ключей и их значений
             */
            void analizeMsg() {
                msgType = MsgType.unknown;
                deviceName = null;
                for (MyMessageReader.MsgPair msgPair : msgBody
                        ) {
                    switch (msgPair.key) {
                        case "server":
                            msgType = MsgType.devicePingResponse;
                            name = msgPair.value;
                            break;
                        default:
                            break;
                    }

                    Log.i(logTag, "key=" + msgPair.key + ", value=" + msgPair.value);
                }
            }

            /**
             * Разбирает строку на строки с парами "КЗ".
             */
            void parceMsg(String source) {
                // Выделяем из исходной строки отдельные команды
                Pattern pattern
                        = Pattern.compile(patternCmdName + "=\'" + patternCmdValue + "\'");
                Matcher matcher
                        = pattern.matcher(source);
                while (matcher.find()) {
                    msgBody.add(extractParam(matcher.group()));
                }
            }

            /**
             * Извлекает пары "КЗ"
             *
             * @param inputString
             * @return
             */
            private MsgPair extractParam(String inputString) {
                MsgPair returnValue
                        = null;
                Pattern patternOfName
                        = Pattern.compile("^" + patternCmdName);
                Matcher matcherOfName
                        = patternOfName.matcher(inputString);
                if (matcherOfName.find()) {
                    Pattern patternOfValue
                            = Pattern.compile("=\'" + patternCmdValue + "\'$");
                    Matcher matcherOfValue
                            = patternOfValue.matcher(inputString);
                    if (matcherOfValue.find()) {
                        returnValue = new MsgPair(matcherOfName.group(), matcherOfValue.group().replace("'", "").replace("=", ""));
                    }
                }
                return returnValue;
            }

            /**
             * Добавляет пару "КЗ" в массив для данного сообщения
             */
            void addPair(String key, String value) {
                msgBody.add(new MsgPair(key, value));
            }
        }
    /*
     * Блок "специальных" функций, которые возвращают конкретные параметры, если они есть в
     * полученном сообщении - имя сервера, показания терминала, версия протокола и пр.
     */

        /**
         * "Читает" новую строку. И проводит ее разбор.
         * При чтении старая строка затирается.
         * Тут надо подумать, как с транслятором будут работать несколько абонентов.
         */
        void readMsg(String source) {
            msg = new Msg(source);
        }
    }
}




