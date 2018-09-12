package com.avatlantik.smartmilking.milkingStanchion;

public class MilkingStanchion2 {
//    private static final String APPLICATION_PROPERTIES = "application.properties";
//
//    private static final String MILKMASHINE_CONNECT_ACTION = "com.android.milkmashine.CONNECT_ACTION";
//    private static final String MILKMASHINE_MILKING_ACTION = "com.android.milkmashine.MILKING_ACTION";
//
//    private static final int READ_TIMEOUT_MILLIS = 5000;
//
//    private static final int MILKMASHINE_DETECTOR_ID = 1;
//    private static final int MILKMASHINE_LOADER_ID = 2;
//
//    private static SocketConfig socketConfig;
//    private static String ssidWIFI, ssidWIFIConnect, passWIFIConnect;
//
//    private static Context context;
//    private LoaderManager loaderManager;
//    private BroadcastReceiver connectionBroadcast;
//    private static NetworkUtils networkUtils;
//    private static MilkingStanchionInterface milkingStanchionInterface;
//    private static Socket socket;
//    private CheckResultThread checkResultThread;
//    private static Boolean connection, connected, reset;
//
//    public MilkingStanchion2(Context context, LoaderManager loaderManager,
//                             MilkingStanchionInterface milkingStanchionInterface) {
//        this.loaderManager = loaderManager;
//        this.context = context;
//        this.milkingStanchionInterface = milkingStanchionInterface;
//        this.networkUtils = new NetworkUtils(context);
//        this.connected = false;
//        this.connection = false;
//
//        connectionBroadcast = new ConnectionBroadcast();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        context.registerReceiver(connectionBroadcast, intentFilter);
//        milkingStanchionInterface.onStatusChange(checkWIFIconnectionToMilkMashine());
//
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        try {
//            Properties properties = PropertyUtils.getProperties(APPLICATION_PROPERTIES, context);
//            socketConfig = new SocketConfig();
//            socketConfig.server = properties.getProperty("black-box.server");
//            socketConfig.port = Integer.parseInt(properties.getProperty("black-box.port"));
//            ssidWIFI = properties.getProperty("wifimilkiiingid");
//            ssidWIFIConnect = String.format("\"%s\"", ssidWIFI);
//            passWIFIConnect =String.format("\"%s\"", properties.getProperty("wifimilkiiingpass"));
//
//        } catch (IOException e) {
//            throw new IllegalStateException("Cannot read auth properties", e);
//        }
//    }
//
//    public Boolean isConnected() {
//        return connected;
//    }
//
//    public Boolean isReset() {
//        return reset;
//    }
//
//    public void connect() {
//
//        reset = false;
//
//        milkingStanchionInterface.onStatusChange(ConnectionStatus.CONNECTION);
//
//        connectToWifi();
//
//        initLoader(loaderManager, MILKMASHINE_DETECTOR_ID, new Bundle(), new MilkingStanchionDetectorCallbacks());
//
//        checkResultThread = new CheckResultThread();
//        checkResultThread.start();
//
//    }
//
//    public void reConnect() {
//
//        reset = false;
//
//        milkingStanchionInterface.onStatusChange(ConnectionStatus.CONNECTION);
//
//        connectToWifi();
//
//        if(socket!=null && socket.isConnected()){
//            initLoader(loaderManager, MILKMASHINE_LOADER_ID, new Bundle(), new MilkMashineCallbacks(context, MILKMASHINE_CONNECT_ACTION));
//        }else {
//            initLoader(loaderManager, MILKMASHINE_DETECTOR_ID, new Bundle(), new MilkingStanchionDetectorCallbacks());
//        }
//    }
//
//    public void refreshStatus() {
//
//        milkingStanchionInterface.onStatusChange(checkWIFIconnectionToMilkMashine());
//    }
//
//
//    public void resetConnect() {
//
//        reset = true;
//    }
//
//    public void startMilking(Milking milking) {
//
//        loaderManager.restartLoader(MILKMASHINE_LOADER_ID, new Bundle(), new MilkMashineCallbacks(context, MILKMASHINE_MILKING_ACTION, milking)).forceLoad();
//    }
//
//    public void disconnect(){
//        checkResultThread.disconn();
//        try {
//            closeSocket();
//        } catch (IOException e) {
//            Log.e(TAGLOG_MASHINE, "Cannot close soket. Possible memory leaks.", e);
//        }
//        context.unregisterReceiver(connectionBroadcast);
//    }
//
//    public void connectToWifi(){
//
//        ConnectionStatus wifiStatus = checkWIFIconnectionToMilkMashine();
//
//        if(wifiStatus != ConnectionStatus.NOT_CONNECTED_WIFI
//                && wifiStatus != ConnectionStatus.WIFI_OFF){
//            return;
//        }
//
//        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        wifiManager.setWifiEnabled(true);
//
//        WifiConfiguration wifiConfig = new WifiConfiguration();
//        wifiConfig.SSID = ssidWIFIConnect;
//        wifiConfig.preSharedKey = passWIFIConnect;
//        wifiConfig.priority = 99999;
//
//        wifiManager.updateNetwork(wifiConfig);
//        wifiManager.saveConfiguration();
//
//        wifiManager.setWifiEnabled(true);
//        int netId = wifiManager.addNetwork(wifiConfig);
//        if (netId == -1) {
//            List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
//            if (configuredNetworks != null) {
//                for (WifiConfiguration existingConfig : configuredNetworks) {
//                    if (existingConfig.SSID.equals(ssidWIFIConnect)) {
//                        netId = existingConfig.networkId;
//                    }
//                }
//            }
//        }
//        wifiManager.disconnect();
//        wifiManager.enableNetwork(netId, true);
//        wifiManager.reconnect();
//
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            Log.e(TAGLOG_MASHINE, e.toString());
//        }
//    }
//
//
//    public static <T> void initLoader(final LoaderManager loaderManager, final int loaderId,
//                                      final Bundle args, final LoaderManager.LoaderCallbacks<T> callbacks) {
//        final Loader<T> loader = loaderManager.getLoader(loaderId);
//        if (loader != null && loader.isReset()) {
//            loaderManager.restartLoader(loaderId, args, callbacks).forceLoad();
//        } else {
//            loaderManager.initLoader(loaderId, args, callbacks).forceLoad();
//        }
//    }
//
//    private class MilkingStanchionDetectorCallbacks implements LoaderManager.LoaderCallbacks<Socket> {
//
//        protected Bundle bundle;
//
//        MilkingStanchionDetectorCallbacks() {}
//
//        @Override
//        public Loader<Socket> onCreateLoader(int id, Bundle args) {
//            this.bundle = args;
//            if (id == MILKMASHINE_DETECTOR_ID) {
//                connection = true;
//                return new MilkingStanchionDetector(context);
//            } else {
//                return null;
//            }
//        }
//
//        @Override
//        public void onLoadFinished(Loader<Socket> loader, Socket s) {
//            socket = s;
//            if(!socket.isConnected()) return;
//            initLoader(loaderManager, MILKMASHINE_LOADER_ID, new Bundle(), new MilkMashineCallbacks(context, MILKMASHINE_CONNECT_ACTION));
//            connection = false;
//        }
//
//        @Override
//        public void onLoaderReset(Loader<Socket> loader) {
//        }
//    }
//
//    private static class MilkingStanchionDetector<T> extends AsyncTaskLoader<Socket> {
//
//        MilkingStanchionDetector(Context context) {
//            super(context);
//        }
//
//        @Override
//        public Socket loadInBackground() {
//            Socket device;
//            while ((device = scanDevice()) == null && !isLoadInBackgroundCanceled()) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    return null;
//                }
//            }
//            return device;
//        }
//
//        protected Socket scanDevice() {
//            try {
//                Socket socket = new Socket();
//                socket.connect(new InetSocketAddress(socketConfig.server, socketConfig.port), 500);
//                socket.setSoTimeout(READ_TIMEOUT_MILLIS);
//                return socket;
//            } catch (IOException e) {
//                Log.e(TAGLOG_MASHINE, e.toString());
//                return null;
//            }
//        }
//    }
//
//
//    private class CheckResultThread extends Thread {
//
//        boolean goOut = false;
//
//        @Override
//        public void run() {
//            while (!goOut) {
//                if(socket!=null && socket.isConnected()) {
//                    milkingStanchionInterface.onResult(pushData());
//                }
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    Log.d(TAGLOG_MASHINE, "Reading is interrupted");
//                }
//            }
//
//        }
//
//        private void disconn(){
//            goOut = true;
//        }
//    }
//
//    private static class MilkMashineCallbacks<T> implements LoaderManager.LoaderCallbacks<Milking> {
//        private Context context;
//        private String action;
//        private Milking milking;
//
//        MilkMashineCallbacks(Context context, String action) {
//            this.context = context;
//            this.action = action;
//        }
//
//        MilkMashineCallbacks(Context context, String action, Milking milking) {
//            this.context = context;
//            this.action = action;
//            this.milking = milking;
//        }
//
//        @Override
//        public Loader<Milking> onCreateLoader(int id, Bundle args) {
//            if (id == MILKMASHINE_LOADER_ID) {
//                return new MilkMashineLoader(context, action, milking);
//            } else {
//                return null;
//            }
//        }
//
//        @Override
//        public void onLoadFinished(Loader<Milking> loader, Milking data) {}
//
//        @Override
//        public void onLoaderReset(Loader<Milking> loader) {}
//
//    }
//
//    private static class MilkMashineLoader<T> extends AsyncTaskLoader<Milking>{
//
//        private String action;
//        private Milking milking;
//
//        MilkMashineLoader(Context context, String action, Milking milking) {
//            super(context);
//            this.action = action;
//            this.milking = milking;
//        }
//
//        @Override
//        public Milking loadInBackground() {
//            try {
//                switch (action) {
//                    case MILKMASHINE_CONNECT_ACTION:
//                        pullConnectRequesrt();
//                        break;
//                    case MILKMASHINE_MILKING_ACTION:
//                        pullMilkingRequesrt(milking);
//                        break;
//                }
//            } catch (IOException e) {
//                Log.e(TAGLOG_MASHINE, "Cannot communicate with the device", e);
//                milkingStanchionInterface.onStatusChange(checkWIFIconnectionToMilkMashine());
//                return null;
//            } finally {
////                try {
////                    closeSocket();
////                } catch (IOException e) {
////                    Log.e(TAGLOG_MASHINE, "Cannot close resources. Possible memory leaks.", e);
////                }
//            }
//            return null;
//        }
//    }
//
//    private static void pullConnectRequesrt() throws IOException {
//        connected = false;
//        PullDateRequest pullDateRequest = new PullDateRequest();
//        writeDevice(pullDateRequest.toBytes());
//        byte[] readed;
//        if (BaseRequest.equals(BaseRequest.OK, readed = readDevice(2))) {
//            connected = true;
//        }else{
//            connected = false;
//            Log.d(TAGLOG_MASHINE, "Acknowledge = " + new String(readed, Charset.forName("UTF-8")));
//            Log.d(TAGLOG_MASHINE, "Acknowledge = " + Integer.toBinaryString(readed[0]));
//        }
//        milkingStanchionInterface.onStatusChange(checkWIFIconnectionToMilkMashine());
//
//    }
//
//    private static void pullMilkingRequesrt(Milking milking) throws IOException {
//
//        Boolean started = false;
//        PullRequest pullRequest = new PullRequest(milking.getId(), milking.getIdCow());
//        writeDevice(pullRequest.toBytes());
//        byte[] readed;
//        if (BaseRequest.equals(BaseRequest.OK, readed = readDevice(2))) {
//            started = true;
//        }else{
//            started = false;
//            Log.d(TAGLOG_MASHINE, "Acknowledge = " + new String(readed, Charset.forName("UTF-8")));
//            Log.d(TAGLOG_MASHINE, "Acknowledge = " + Integer.toBinaryString(readed[0]));
//            throw new IOException("No acknowledge response from the device on write");
//        }
//        milkingStanchionInterface.onMilkingStarted(milking, started);
//
//    }
//
//    private static double[] pushData(){
//
//        byte[] response;
//        try {
//            response = readDevice(milkMashineResponseSize());
//        } catch (IOException e) {
//            Log.e(TAGLOG_MASHINE, e.toString());
//            return null;
//        }
//        if (isNullResponse(response)) {
//            response = new byte[0];
//        } else if (isErrorResponse(response)) {
//            Log.e(TAGLOG_MASHINE, "Error occurs while reading from Milk MAshine");
//            return null;
//        }
//
//        return parseMilkMashine(response);
//    }
//
//
//
//    private static int milkMashineResponseSize() {
//        return 18 * 2;
//    }
//
//    private static byte[] readDevice(int size) throws IOException {
//        byte[] buffer = new byte[size];
//        if(socket==null || socket.isClosed()) return buffer;
//        DataInputStream input = new DataInputStream(socket.getInputStream());
//      //  input.read(buffer);
//
//        long opStartMillis = System.currentTimeMillis();
//        while(System.currentTimeMillis() - opStartMillis < READ_TIMEOUT_MILLIS) {
//            if(input.available() > 0) {
//                input.read(buffer);
//                if(input.available() == 0) {
//                    break;
//                }
//            }
//        }
//        return buffer;
//    }
//
//    private static void writeDevice(byte[] buffer) throws IOException {
//        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
//        outputStream.write(buffer);
//    }
//
//    private static double[] parseMilkMashine(byte[] bytes) {
//        double[] values = new double[milkMashineResponseSize() / 4];
//        for (int i = 0; i < bytes.length; i += 4) {
//            values[i / 4] = asciiToInt(bytes[i]) * 10 + asciiToInt(bytes[i + 1])
//                    + (asciiToInt(bytes[i + 2]) * 10 + asciiToInt(bytes[i + 3])) / 100d;
//        }
//        return values;
//    }
//
//    private static boolean isNullResponse(byte[] bytes) {
//        for (byte oneByte : bytes) {
//            if (asciiToInt(oneByte) != 0) return false;
//        }
//        return true;
//    }
//
//    protected static boolean isErrorResponse(byte[] bytes) {
//        return isNullResponse(Arrays.copyOf(bytes, bytes.length - 1)) && bytes[bytes.length - 1] == 0x09;
//    }
//
//    private static int asciiToInt(byte ascii) {
//        return Character.getNumericValue((int) ascii);
//    }
//
//    private static void closeSocket() throws IOException {
//        if (socket == null) return;
//        if (socket.isConnected()) socket.close();
//    }
//
//
//    private class ConnectionBroadcast extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            ConnectionStatus connectionStatus = checkWIFIconnectionToMilkMashine();
//            milkingStanchionInterface.onStatusChange(connectionStatus);
//
//        }
//
//    }
//
//    private static ConnectionStatus checkWIFIconnectionToMilkMashine() {
//
//        if (!networkUtils.WIFISwitch()) {
//            connected = false;
//            return ConnectionStatus.WIFI_OFF;
//        }
//
//        final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//
//        if (wifiInfo != null) {
//            if(!wifiInfo.getSSID().equals("\"" + ssidWIFI + "\"")){
//                connected = false;
//                return ConnectionStatus.NOT_CONNECTED_WIFI;
//            }
//        }
//
//        if(socket==null){
//            connected = false;
//            return ConnectionStatus.NOT_CONNECTED;
//        }else if(!socket.isConnected()){
//            connected = false;
//            return ConnectionStatus.NOT_CONNECTED;
//        }
//
//        if(!connected){
//            return ConnectionStatus.NOT_CONNECTED;
//        }
//
//
//        return ConnectionStatus.CONNECTED;
//
//    }
//
//    private static class SocketConfig {
//        String server;
//        int port;
//    }


}
