package com.avatlantik.smartmilking.milkingStanchion;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.util.Log;

import com.avatlantik.smartmilking.milkingStanchion.MilkingStancionParams.ConnectionStatus;
import com.avatlantik.smartmilking.model.MilkingResult;
import com.avatlantik.smartmilking.model.db.Milking;
import com.avatlantik.smartmilking.utils.NetworkUtils;
import com.avatlantik.smartmilking.utils.PropertyUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static com.avatlantik.smartmilking.common.Consts.TAGLOG_MASHINE;
import static com.avatlantik.smartmilking.model.MilkingResult.STATUS_ACCIDENT;
import static com.avatlantik.smartmilking.model.MilkingResult.STATUS_ClOSED;
import static com.avatlantik.smartmilking.model.MilkingResult.STATUS_WORKED;
import static com.avatlantik.smartmilking.model.MilkingResult.WISHING_ALLOWED;
import static com.avatlantik.smartmilking.model.MilkingResult.WISHING_FORBIDDEN;

public class MilkingStanchion {
    private static final String APPLICATION_PROPERTIES = "application.properties";

    private static final String MILKMASHINE_CONNECT_ACTION = "com.android.milkmashine.CONNECT_ACTION";
    private static final String MILKMASHINE_MILKING_ACTION = "com.android.milkmashine.MILKING_ACTION";
    private static final String MILKMASHINE_VACUUM_PUMP_ACTION = "com.android.milkmashine.VACUUM_PUMP_ACTION";
    private static final String MILKMASHINE_MILK_PUMP_ACTION = "com.android.milkmashine.MILK_PUMP_ACTION";
    private static final String MILKMASHINE_CANCEL_MILKING_ACTION = "com.android.milkmashine.CANCEL_MILKING_ACTION";

    private static final int READ_TIMEOUT_MILLIS = 5000;
    private static final int PUSH_TIMEOUT_MILLIS = 30000;

    private static SocketConfig socketConfig;
    private static String ssidWIFI, ssidWIFIConnect, passWIFIConnect;

    private static Context context;
    private LoaderManager loaderManager;
    private BroadcastReceiver connectionBroadcast;
    private static NetworkUtils networkUtils;
    private static MilkingStanchionInterface milkingStanchionInterface;
    private static Socket socket;
    private CheckResultThread checkResultThread;
    private static Boolean connection, connected, wishingAllowed, vacuumPumpStart, milkPumpStart;

    public MilkingStanchion(Context context, LoaderManager loaderManager,
                             MilkingStanchionInterface milkingStanchionInterface) {
        this.loaderManager = loaderManager;
        this.context = context;
        this.milkingStanchionInterface = milkingStanchionInterface;
        this.networkUtils = new NetworkUtils(context);
        this.connected = false;
        this.connection = false;
        this.wishingAllowed = false;
        this.vacuumPumpStart = false;
        this.milkPumpStart = false;

        connectionBroadcast = new ConnectionBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(connectionBroadcast, intentFilter);
        milkingStanchionInterface.onStatusChange(checkWIFIconnectionToMilkMashine());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Properties properties = PropertyUtils.getProperties(APPLICATION_PROPERTIES, context);
            socketConfig = new SocketConfig();
            socketConfig.server = properties.getProperty("black-box.server");
            socketConfig.port = Integer.parseInt(properties.getProperty("black-box.port"));
            ssidWIFI = properties.getProperty("wifimilkiiingid");
            ssidWIFIConnect = String.format("\"%s\"", ssidWIFI);
            passWIFIConnect =String.format("\"%s\"", properties.getProperty("wifimilkiiingpass"));

        } catch (IOException e) {
            throw new IllegalStateException("Cannot read auth properties", e);
        }
    }

    public Boolean isConnected() {
        return connected;
    }

    public void connect() {
      milkingStanchionInterface.onStatusChange(ConnectionStatus.CONNECTION);
      connectToWifi();

        checkResultThread = new CheckResultThread();
        checkResultThread.start();
    }

    public void disconnect(){
        checkResultThread.disconn();
        try {
            closeSocket();
        } catch (IOException e) {
            Log.e(TAGLOG_MASHINE, "Cannot close soket. Possible memory leaks.", e);
        }
        context.unregisterReceiver(connectionBroadcast);
    }


    public void startMilking(Milking milking) {
        new SendCommandThread(MILKMASHINE_MILKING_ACTION, milking).start();
    }

    public void cancelMilking(Milking milking) {
        new SendCommandThread(MILKMASHINE_CANCEL_MILKING_ACTION, milking).start();
    }

    public void startMilkPump() {
        new SendCommandThread(MILKMASHINE_MILK_PUMP_ACTION).start();
    }

    public void startVacuumPump() {
        new SendCommandThread(MILKMASHINE_VACUUM_PUMP_ACTION).start();
    }

    public void connectToWifi(){

        ConnectionStatus wifiStatus = checkWIFIconnectionToMilkMashine();

        if(wifiStatus != ConnectionStatus.NOT_CONNECTED_WIFI
                && wifiStatus != ConnectionStatus.WIFI_OFF){
            return;
        }

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = ssidWIFIConnect;
        wifiConfig.preSharedKey = passWIFIConnect;
        wifiConfig.priority = 99999;

        wifiManager.updateNetwork(wifiConfig);
        wifiManager.saveConfiguration();

        wifiManager.setWifiEnabled(true);
        int netId = wifiManager.addNetwork(wifiConfig);
        if (netId == -1) {
            List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
            if (configuredNetworks != null) {
                for (WifiConfiguration existingConfig : configuredNetworks) {
                    if (existingConfig.SSID.equals(ssidWIFIConnect)) {
                        netId = existingConfig.networkId;
                    }
                }
            }
        }
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.e(TAGLOG_MASHINE, e.toString());
        }
    }

    private class CheckResultThread extends Thread {

        boolean goOut = false;

        @Override
        public void run() {
            while (!goOut) {

//                long opStartMillis = System.currentTimeMillis();
//                while(System.currentTimeMillis() - opStartMillis < PUSH_TIMEOUT_MILLIS) {

                    if(socket!=null && socket.isConnected()) {
                        try {
                            MilkingResult milkingResult = pushData();

                            if(milkingResult==null){
                               continue;
                            }

                            Calendar cal = Calendar.getInstance();
                            cal.setTime(new Date());

                            int yearDevice = cal.get(Calendar.YEAR);
                            int monthDevice = cal.get(Calendar.MONTH)+1;
                            int dayDevice = cal.get(Calendar.DAY_OF_MONTH);
                            int hourDevice = cal.get(Calendar.HOUR_OF_DAY);

                            cal.setTime(milkingResult.getDate());
                            int yearMilkMashine = cal.get(Calendar.YEAR);
                            int monthMilkMashine = cal.get(Calendar.MONTH);
                            int dayMilkMashine = cal.get(Calendar.DAY_OF_MONTH);
                            int hourMilkMashine = cal.get(Calendar.HOUR_OF_DAY);

                            if(yearDevice!=yearMilkMashine) {
                                new SendCommandThread(MILKMASHINE_CONNECT_ACTION).start();
                            }else if(monthDevice!=monthMilkMashine){
                                new SendCommandThread(MILKMASHINE_CONNECT_ACTION).start();
                            }else if(dayDevice!=dayMilkMashine){
                                new SendCommandThread(MILKMASHINE_CONNECT_ACTION).start();
                            }else if((hourDevice - hourMilkMashine) > 1 || (hourDevice - hourMilkMashine) < -1) {
                                new SendCommandThread(MILKMASHINE_CONNECT_ACTION).start();
                            }

                            if(milkingResult.getStatus()==STATUS_ACCIDENT ){
                                try {
                                    closeSocket();
                                } catch (IOException e) {
                                    Log.e(TAGLOG_MASHINE, "Cannot close soket. Possible memory leaks.", e);
                                }
                                socket = null;
                                connected = false;
                                milkingStanchionInterface.onStatusChange(ConnectionStatus.ACCIDENT);
                            }else if(milkingResult.getStatus()==STATUS_ClOSED){
                                try {
                                    closeSocket();
                                } catch (IOException e) {
                                    Log.e(TAGLOG_MASHINE, "Cannot close soket. Possible memory leaks.", e);
                                }
                                socket = null;
                                connected = false;
                                milkingStanchionInterface.onStatusChange(ConnectionStatus.NOT_CONNECTED);
                            }else{

                                if(milkingResult.getStatus()==STATUS_WORKED && !connected){
                                    connected = (milkingResult.getStatus()==STATUS_WORKED);
                                    milkingStanchionInterface.onStatusChange(ConnectionStatus.CONNECTED);
                                }

                                if(milkingResult.getWishingAloowed()==WISHING_ALLOWED && !wishingAllowed){
                                    wishingAllowed = true;
                                    milkingStanchionInterface.onWishingAllowed(wishingAllowed);
                                }

                                if(milkingResult.getWishingAloowed()==WISHING_FORBIDDEN && wishingAllowed){
                                    wishingAllowed = false;
                                    milkingStanchionInterface.onWishingAllowed(wishingAllowed);
                                }

                                if(milkingResult.isVacuumPumpStart()!= vacuumPumpStart){
                                    vacuumPumpStart = milkingResult.isVacuumPumpStart();
                                    milkingStanchionInterface.onVacuumPumpStart(vacuumPumpStart);
                                }

                                if(milkingResult.isMilkPumpStart()!= milkPumpStart){
                                    milkPumpStart = milkingResult.isMilkPumpStart();
                                    milkingStanchionInterface.onMilkPumpStart(milkPumpStart);
                                }

                                milkingStanchionInterface.onResult(milkingResult);
                            }

                        } catch (IOException e) {
                            Log.e(TAGLOG_MASHINE, "Cannot communicate with the device", e);
                        }
                    }else {
                       // connected = false;
//                        long opStartMillis = System.currentTimeMillis();
//                        while (System.currentTimeMillis() - opStartMillis < PUSH_TIMEOUT_MILLIS) {
                           // milkingStanchionInterface.onStatusChange(ConnectionStatus.CONNECTION);
                            while (socket == null) {
                                socket = scanDevice();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                }
                            }
//                            connected = true;
//                            milkingStanchionInterface.onStatusChange(ConnectionStatus.CONNECTED);
//                        }
                    }

                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    Log.d(TAGLOG_MASHINE, "Reading is interrupted");
                }

            }

        }

        private void disconn(){
            goOut = true;
        }
    }

    protected Socket scanDevice() {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(socketConfig.server, socketConfig.port), 500);
            socket.setSoTimeout(READ_TIMEOUT_MILLIS);
            return socket;
        } catch (IOException e) {
            Log.e(TAGLOG_MASHINE, e.toString());
            return null;
        }
    }

    private class SendCommandThread extends Thread {

        private String command;
        private Milking milking;

        SendCommandThread(String command) {
            this.command = command;
        }

        SendCommandThread(String command, Milking milking) {
            this.command = command;
            this.milking = milking;
        }

        @Override
        public void run() {
            sendCommand(command);

        }

        private void sendCommand(String command){
            try {
                switch (command) {
                    case MILKMASHINE_CONNECT_ACTION:
                        PullDateRequest pullDateRequest = new PullDateRequest();
                        writeDevice(pullDateRequest.toBytes());
                        break;
                    case MILKMASHINE_MILKING_ACTION:
                        MilkingRequest milkingRequest = new MilkingRequest(milking.getId(), milking.getIdCow());
                        writeDevice(milkingRequest.toBytes());
                        break;
                    case MILKMASHINE_VACUUM_PUMP_ACTION:
                        VacuumPumpRequest vacuumPumpRequest = new VacuumPumpRequest(!vacuumPumpStart);
                        writeDevice(vacuumPumpRequest.toBytes());
                        break;
                    case MILKMASHINE_MILK_PUMP_ACTION:
                        MilkPumpRequest milkPumpRequest = new MilkPumpRequest(!milkPumpStart);
                        writeDevice(milkPumpRequest.toBytes());
                        break;
                    case MILKMASHINE_CANCEL_MILKING_ACTION:
                        CancelMilkingRequest cancelMilkingRequest = new CancelMilkingRequest(milking.getId());
                        writeDevice(cancelMilkingRequest.toBytes());
                        break;
                }
            } catch (IOException e) {
                Log.e(TAGLOG_MASHINE, "Cannot communicate with the device", e);
                milkingStanchionInterface.onStatusChange(checkWIFIconnectionToMilkMashine());
            }
        }

    }

    private MilkingResult pushData()  throws IOException {

        PullRequest pullRequest = new PullRequest();
        writeDevice(pullRequest.toBytes());

        byte[] response;
        try {
            response = readDevice(milkMashineResponseSize());
        } catch (IOException e) {
            Log.e(TAGLOG_MASHINE, e.toString());
            return null;
        }
        if (isNullResponse(response)) {
            response = new byte[0];
        } else if (isErrorResponse(response)) {
            Log.e(TAGLOG_MASHINE, "Error occurs while reading from Milk MAshine");
            return null;
        }

        return parseMilkMashine(response);
    }



    private static int milkMashineResponseSize() {
        return 18 * 2;
    }

    private static byte[] readDevice(int size) throws IOException {
        byte[] buffer = new byte[size];
        if(socket==null || socket.isClosed()) return buffer;
        DataInputStream input = new DataInputStream(socket.getInputStream());
      //  input.read(buffer);

        long opStartMillis = System.currentTimeMillis();
        while(System.currentTimeMillis() - opStartMillis < READ_TIMEOUT_MILLIS) {
            if(input.available() > 0) {
                input.read(buffer);
                if(input.available() == 0) {
                    break;
                }
            }
        }
        return buffer;
    }

    private static void writeDevice(byte[] buffer) throws IOException {
        if(socket==null || socket.isClosed()) return;
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.write(buffer);
    }

    private static MilkingResult parseMilkMashine(byte[] bytes) {
        int sum = 0;
        for (int i = 0; i < bytes.length; i += 1) {
            if(i >= 28) continue;
            sum +=asciiToInt(bytes[i]);
        }
        if(sum%10!=asciiToInt(bytes[28])) return null;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2000 + asciiToInt(bytes[6])* 10 +asciiToInt(bytes[7]));
        cal.set(Calendar.MONTH, asciiToInt(bytes[4])* 10 +asciiToInt(bytes[5]));
        cal.set(Calendar.DAY_OF_MONTH, asciiToInt(bytes[2])* 10 +asciiToInt(bytes[3]));
        cal.set(Calendar.HOUR_OF_DAY, asciiToInt(bytes[8])* 10 +asciiToInt(bytes[9]));

        return MilkingResult.builder()
                .status(asciiToInt(bytes[0]))
                .wishingAloowed(asciiToInt(bytes[1]))
                .date(cal.getTime())
                .vacuumPumpStart(asciiToInt(bytes[10])==1)
                .milkPumpStart(asciiToInt(bytes[11])==1)
                .milkingStart1(asciiToInt(bytes[12])==1)
                .litres1(asciiToInt(bytes[13])*10+asciiToInt(bytes[14])+asciiToInt(bytes[15])/10d)
                .milkingStart2(asciiToInt(bytes[16])==1)
                .litres2(asciiToInt(bytes[17])*10+asciiToInt(bytes[18])+asciiToInt(bytes[19])/10d)
                .milkingStart3(asciiToInt(bytes[20])==1)
                .litres3(asciiToInt(bytes[21])*10+asciiToInt(bytes[22])+asciiToInt(bytes[23])/10d)
                .milkingStart4(asciiToInt(bytes[24])==1)
                .litres4(asciiToInt(bytes[25])*10+asciiToInt(bytes[26])+asciiToInt(bytes[27])/10d)
                .build();
    }

    private static boolean isNullResponse(byte[] bytes) {
        for (byte oneByte : bytes) {
            if (asciiToInt(oneByte) != 0) return false;
        }
        return true;
    }

    protected static boolean isErrorResponse(byte[] bytes) {
        return isNullResponse(Arrays.copyOf(bytes, bytes.length - 1)) && bytes[bytes.length - 1] == 0x09;
    }

    private static int asciiToInt(byte ascii) {
        return Character.getNumericValue((int) ascii);
    }

    private static void closeSocket() throws IOException {
        if (socket == null) return;
        if (socket.isConnected()) socket.close();
    }


    private class ConnectionBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectionStatus connectionStatus = checkWIFIconnectionToMilkMashine();
            milkingStanchionInterface.onStatusChange(connectionStatus);

        }

    }

    private static ConnectionStatus checkWIFIconnectionToMilkMashine() {

        if (!networkUtils.WIFISwitch()) {
            try {
                closeSocket();
            } catch (IOException e) {
                Log.e(TAGLOG_MASHINE, "Cannot close soket. Possible memory leaks.", e);
            }
            socket = null;
            connected = false;
            return MilkingStancionParams.ConnectionStatus.WIFI_OFF;
        }

        final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        final WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        if (wifiInfo != null) {
            if(!wifiInfo.getSSID().equals("\"" + ssidWIFI + "\"")){
                try {
                    closeSocket();
                } catch (IOException e) {
                    Log.e(TAGLOG_MASHINE, "Cannot close soket. Possible memory leaks.", e);
                }
                socket = null;
                connected = false;
                return ConnectionStatus.NOT_CONNECTED_WIFI;
            }
        }

        if(socket==null){
            connected = false;
            return ConnectionStatus.NOT_CONNECTED;
        }else if(!socket.isConnected()){
            connected = false;
            return ConnectionStatus.NOT_CONNECTED;
        }

        if(!connected){
            return ConnectionStatus.NOT_CONNECTED;
        }


        return ConnectionStatus.CONNECTED;

    }

    private static class SocketConfig {
        String server;
        int port;
    }


}
