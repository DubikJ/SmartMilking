//package com.avatlantik.smartmilking.milkingStanchion;
//
//
//import android.app.LoaderManager;
//import android.content.AsyncTaskLoader;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.Loader;
//import android.os.Bundle;
//import android.util.Log;
//
//import com.avatlantik.smartmilking.model.Milking;
//import com.avatlantik.smartmilking.service.SettingsService.ConnectionType;
//import com.avatlantik.smartmilking.utils.Consumer;
//import com.avatlantik.smartmilking.utils.NetworkUtils;
//
//import java.io.IOException;
//import java.nio.charset.Charset;
//import java.util.Arrays;
//
//import static com.avatlantik.smartmilking.common.Consts.TAGLOG_MASHINE;
//
//public abstract class MilkingStanchion1 {
//
//    static final String MILKMASHINE_INIT_LOADER = "com.android.milkmashine.INIT_LOADER";
//    static final String MILKMASHINE_CONNECT_ACTION = "com.android.milkmashine.CONNECT_ACTION";
//    static final String MILKMASHINE_MILKING_ACTION = "com.android.milkmashine.MILKING_ACTION";
//    static final String MILKMASHINE_STATUS_ACTION = "com.android.milkmashine.STATUS_ACTION";
//    static final String MILKMASHINE_LOAD_ACTION = "com.android.milkmashine.LOAD_ACTION";
//    static final String MILKMASHINE_CANCEL_ACTION = "com.android.milkmashine.CANCEL_ACTION";
//    static final String MILKMASHINE_STOP_ACTION = "com.android.milkmashine.STOP_ACTION";
//
//    protected static final String OK = "1";
//
//    private static final int MILKMASHINE_DETECTOR_ID = 1;
//    static final int MILKMASHINE_LOADER_ID = 2;
//
//    private ConnectionType connectionType;
//    private static MilkingStanchionInterface milkingStanchionInterface;
//    protected Context context;
//    private NetworkUtils networkUtils;
//    private static Boolean connected;
//    Consumer<Milking> dataConsumer;
//    LoaderManager loaderManager;
//
//    public MilkingStanchion1(Context context) {
//        this.context = context;
//    }
//
//    public static MilkingStanchion1 via(Context context, ConnectionType connectionType) {
//        if (connectionType == null) {
//            throw new IllegalArgumentException("Connection type is null");
//        }
//
//        switch (connectionType) {
//            case WIFI:
//                return null;//new WifiMilkingStanchion(context);
////            case USB:
////                return new UsbEkoMilk(context);
//            default:
//                throw new IllegalStateException("Connection type is undefined");
//        }
//    }
//
////    public void initLoaderManager(final LoaderManager loaderManager, Consumer<Boolean> connected) {
////       // dataConsumer = onData;
////        this.loaderManager = loaderManager;
////        loaderManager.initLoader(MILKMASHINE_DETECTOR_ID, new Bundle(), getDetectorCallbacks(MILKMASHINE_INIT_LOADER)).forceLoad();
////    }
//
//    public void init(LoaderManager loaderManager, ConnectionType connectionType, MilkingStanchionInterface milkingStanchionInterface) {
//        this.loaderManager = loaderManager;
//        this.milkingStanchionInterface = milkingStanchionInterface;
//        this.connectionType = connectionType;
//        this.networkUtils = new NetworkUtils(context);
//        this.connected = false;
//
//        loaderManager.initLoader(MILKMASHINE_DETECTOR_ID, new Bundle(), getDetectorCallbacks(MILKMASHINE_INIT_LOADER)).forceLoad();
//
//        BroadcastReceiver connectionBroadcast = new ConnectionBroadcast();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        context.registerReceiver(connectionBroadcast, intentFilter);
//
//        milkingStanchionInterface.onStatusChange(networkUtils.checkWIFIconnectionToMilkMashine());
//    }
//
//    public void connect() {
//        loaderManager.restartLoader(MILKMASHINE_DETECTOR_ID, new Bundle(), getDetectorCallbacks(MILKMASHINE_CONNECT_ACTION)).forceLoad();
//    }
//
//    public void start(Consumer<Milking> onData) {
//        dataConsumer = onData;
//        loaderManager.restartLoader(MILKMASHINE_DETECTOR_ID, new Bundle(), getDetectorCallbacks(MILKMASHINE_LOAD_ACTION)).forceLoad();
//    }
//
//    public void cancel() {
//        if (loaderManager == null) return;
//        loaderManager.restartLoader(MILKMASHINE_DETECTOR_ID, new Bundle(), getDetectorCallbacks(MILKMASHINE_CANCEL_ACTION)).forceLoad();
//        if (loaderManager.getLoader(MILKMASHINE_LOADER_ID) != null) {
//            loaderManager.getLoader(MILKMASHINE_LOADER_ID).cancelLoad();
//        }
//    }
//
//    public void stop() {
//        if (loaderManager == null) return;
//        loaderManager.restartLoader(MILKMASHINE_DETECTOR_ID, new Bundle(), getDetectorCallbacks(MILKMASHINE_STOP_ACTION)).forceLoad();
//        if (loaderManager.getLoader(MILKMASHINE_DETECTOR_ID) != null) {
//            loaderManager.getLoader(MILKMASHINE_DETECTOR_ID).cancelLoad();
//        }
//        if (loaderManager.getLoader(MILKMASHINE_LOADER_ID) != null) {
//            loaderManager.getLoader(MILKMASHINE_LOADER_ID).cancelLoad();
//        }
//    }
//
//
//    protected abstract MilkingStanchionDetectorCallbacks getDetectorCallbacks(String action);
//
//    abstract class MilkingStanchionDetectorCallbacks<T> implements LoaderManager.LoaderCallbacks<T> {
//
//        protected String action;
//        protected Bundle bundle;
//
//        MilkingStanchionDetectorCallbacks(String action) {
//            this.action = action;
//        }
//
//        @Override
//        public Loader<T> onCreateLoader(int id, Bundle args) {
//            this.bundle = args;
//            if (id == MILKMASHINE_DETECTOR_ID) {
//                return getDetector();
//            } else {
//                return null;
//            }
//        }
//
//        @Override
//        public abstract void onLoadFinished(Loader<T> loader, T device);
//
//        @Override
//        public void onLoaderReset(Loader<T> loader) {
//        }
//
//        protected abstract MilkingStanchionDetector<T> getDetector();
//    }
//
//    abstract static class MilkingStanchionDetector<T> extends AsyncTaskLoader<T> {
//
//        MilkingStanchionDetector(Context context) {
//            super(context);
//        }
//
//        @Override
//        public T loadInBackground() {
//            T device;
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
//        protected abstract T scanDevice();
//    }
//
//    abstract static class MilkingStanchionLoaderCallbacks<T> implements LoaderManager.LoaderCallbacks<Milking> {
//
//        T device;
//        Consumer<Milking> consumer;
//        protected String action;
//
//        MilkingStanchionLoaderCallbacks(T device, Consumer<Milking> consumer, String action) {
//            this.device = device;
//            this.consumer = consumer;
//            this.action = action;
//        }
//
//        @Override
//        public Loader<Milking> onCreateLoader(int id, Bundle args) {
//            if (id == MILKMASHINE_LOADER_ID) {
//                try {
//                    return getLoader(action);
//                } catch (IOException e) {
//                    Log.e(TAGLOG_MASHINE, "Cannot create loader", e);
//                    return null;
//                }
//            } else {
//                return null;
//            }
//        }
//
//        @Override
//        public void onLoadFinished(Loader<Milking> loader, Milking data) {
//            consumer.apply(data);
//        }
//
//        @Override
//        public void onLoaderReset(Loader<Milking> loader) {
//        }
//
//        protected abstract MilkingStanchionLoader<T> getLoader(String action) throws IOException;
//    }
//
//    abstract static class MilkingStanchionLoader<T> extends AsyncTaskLoader<Milking> {
//
//        protected final static String TAGLOG = "MilkingStanchionLoader";
//        protected Context context;
//        protected String action;
//        T device;
//
//        MilkingStanchionLoader(Context context, T device, String action) {
//            super(context);
//            this.context = context;
//            this.device = device;
//            this.action = action;
//        }
//
//        protected abstract int milkingMashResponseSize();
//
//        protected abstract byte[] readDevice(int size) throws IOException;
//
//        protected abstract int writeDevice(byte[] buffer) throws IOException;
//
//        protected abstract double[] parseMilkingMash(byte[] bytes);
//
//        protected abstract void close() throws IOException;
//
//        protected boolean isNullResponse(byte[] bytes) {
//            for (byte oneByte : bytes) {
//                if (oneByte != 0) return false;
//            }
//            return true;
//        }
//
//        protected boolean isErrorResponse(byte[] bytes) {
//            return isNullResponse(Arrays.copyOf(bytes, bytes.length - 1)) && bytes[bytes.length - 1] == 0x09;
//        }
//
//        @Override
//        public Milking loadInBackground() {
//            byte buffer[] = new byte[0];
//            if(action == MILKMASHINE_INIT_LOADER){
//                return null;
//            }
//            try {
//
////                ScanRequest scanRequest = new ScanRequest(EKOMILK_START_ANALYSE);
////                if(action == EKOMILK_LOAD_ACTION_FLOWMETER) {
////                    scanRequest = new ScanRequest(FLOWMETER_START_ANALYSE);
////                }else {
////                    scanRequest = new ScanRequest(EKOMILK_START_AUTO_ANALYSE);
////                }
////                writeDevice(scanRequest.toBytes());
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    Log.d(TAGLOG, "Reading is interrupted");
//                    return null;
//                }
//
//                switch (action) {
//                    case MILKMASHINE_CONNECT_ACTION:
//                        writeDevice(new PullDateRequest().toBytes());
//                        byte[] readed;
//                        if (!OK.equals(new String(readed = readDevice(1), Charset.forName("UTF-8")))) {
//                            Log.d(TAGLOG, "Acknowledge = " + new String(readed, Charset.forName("UTF-8")));
//                            Log.d(TAGLOG, "Acknowledge = " + Integer.toBinaryString(readed[0]));
//                            throw new IOException("No acknowledge response from the device on write");
//                        }
//                        connected = true;
//                        return null;
//                    case MILKMASHINE_LOAD_ACTION:
//                        while (!connected && !isLoadInBackgroundCanceled()) {
//                            buffer = pull(milkingMashResponseSize());
//                            try {
//                                Thread.sleep(3000);
//                            } catch (InterruptedException e) {
//                                Log.d(TAGLOG, "Reading is interrupted");
//                                return null;
//                            }
//                        }
//                }
//
////                byte[] readed;
////                if (!OK.equals(new String(readed = readDevice(1), Charset.forName("UTF-8")))) {
////                    Log.d(TAGLOG, "Acknowledge = " + new String(readed, Charset.forName("UTF-8")));
////                    Log.d(TAGLOG, "Acknowledge = " + Integer.toBinaryString(readed[0]));
////                    throw new IOException("No acknowledge response from the device on write");
////                }
//
//            } catch (IOException e) {
//                Log.e(TAGLOG, "Cannot communicate with the device", e);
//                return null;
////            } finally {
////                try {
////                    close();
////                } catch (IOException e) {
////                    Log.e(TAGLOG, "Cannot close resources. Possible memory leaks.", e);
////                }
//            }
//            milkingStanchionInterface.onResult(parseMilkingMash(buffer));
////
////            if(values[8] == EKOMILK_EXEPTION){
////                return null;
////            }
//
//            return null;
//        }
//
//        private byte[] pull(int size) throws IOException {
//            byte[] pullRequest = new PullRequest(1, 550).toBytes();
//            writeDevice(pullRequest);
//            byte[] response = readDevice(size);
//            if (isNullResponse(response)) {
//                response = new byte[0];
//            } else if (isErrorResponse(response)) {
//                throw new IOException("Error occurs while reading from Ekomilk");
//            }
//            return response;
//        }
//
//    }
//
//    private class ConnectionBroadcast extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(connectionType == ConnectionType.WIFI) {
//                milkingStanchionInterface.onStatusChange(networkUtils.checkWIFIconnectionToMilkMashine());
//            }
//
//        }
//
//    }
//
//
//}
