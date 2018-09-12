package com.avatlantik.smartmilking.rfid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.xminnov.ivrjack.ru01.IvrJackAdapter;
import com.xminnov.ivrjack.ru01.IvrJackService;
import com.xminnov.ivrjack.ru01.IvrJackStatus;

import static com.avatlantik.smartmilking.common.Consts.TAGLOG_SCAN;

public class JackScan implements IvrJackAdapter {


    private Context context;
    private IvrJackService service;
    private JackScanInterface jackScanInterface;
    private AudioManager audioManager;
    private Boolean connected;
    private Boolean started;
    private MainHandler handler;
    private BroadcastReceiver volumeBroadcast;

//    public JackScan(Context context) {
//        this.context = context;
//        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        this.connected = false;
//        this.started = false;
//        this.service = new IvrJackService(context, this);
//
//        BroadcastReceiver volumeBroadcast = new VolumnBroadcast();
//        context.registerReceiver(volumeBroadcast, new IntentFilter("android.media.VOLUME_CHANGED_ACTION"));
//    }

    public JackScan(Context context, JackScanInterface jackScanInterface) {
        this.context = context;
        this.jackScanInterface = jackScanInterface;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.connected = false;
        this.started = false;
        this.service = new IvrJackService(context, this);
        this.handler = new MainHandler();

        volumeBroadcast = new VolumnBroadcast();
        context.registerReceiver(volumeBroadcast, new IntentFilter("android.media.VOLUME_CHANGED_ACTION"));
    }

    public void setJackScanInterface(JackScanInterface jackScanInterface) {
        this.jackScanInterface = jackScanInterface;
    }

    public Boolean isStarted() {
        return started;
    }

    public Boolean isConnected() {
        return connected;
    }

    @Override
    public void onConnect(String deviceSn) {
        Log.i(TAGLOG_SCAN, "on connect");
        jackScanInterface.onConnect(deviceSn);
    }

    @Override
    public void onDisconnect() {
        Log.i(TAGLOG_SCAN, "on disconnect");
        jackScanInterface.onDisconnect();
    }

    @Override
    public void onStatusChange(IvrJackStatus status) {
        Log.i(TAGLOG_SCAN, "on status change: " + status);
        int volumetMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int volumePercent = (int)(((double)volume/ volumetMax)*100);
        switch (status) {
            case ijsDetecting:
                connected = false;
                jackScanInterface.onStatusChange(JackScanStatus.scanDetecting, volumePercent);
//                jackScanInterface.onStatusChange(context.getString(R.string.scanner_detecting),
//                        volumePercent, false, false);
                break;
            case ijsRecognized:
                connected = true;
                jackScanInterface.onStatusChange(JackScanStatus.scanRecognized, volumePercent);
//                jackScanInterface.onStatusChange(context.getString(R.string.scanner_connected),
//                        volumePercent, false, true);
                break;
            case ijsUnRecognized:
                connected = false;
                jackScanInterface.onStatusChange(JackScanStatus.scanUnRecognized, volumePercent);
//                jackScanInterface.onStatusChange(context.getString(R.string.scanner_unrecognized),
//                        volumePercent, true, false);
                break;
            case ijsPlugout:
                connected = false;
                jackScanInterface.onStatusChange(JackScanStatus.scanPlugout, volumePercent);
//                jackScanInterface.onStatusChange(context.getString(R.string.scanner_disconnected),
//                        volumePercent, true, false);
                break;
        }
    }

    @Override
    public void onInventory(byte[] epc) {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<epc.length; i++) {
            builder.append(String.format("%02X", epc[i]));
            if ((i+1)%4==0) builder.append(" ");
        }
        jackScanInterface.onResult(builder.toString());
    }

    public void start() {
        new Thread(new StartTask()).start();
        started = true;
    }

    public void stop() {
        new Thread(new StopTask()).start();
        started = false;
    }

    public void checkBattery() {
        new Thread(new BatteryTask()).start();
        started = false;
    }

    public void connect() {

        service.open();
    }

    public void disconnect() {
        service.close();
    }

    public void destroy() {
        disconnect();
        service = null;
        context.unregisterReceiver(volumeBroadcast);
    }

    class StartTask implements Runnable {

        @Override
        public void run() {
            int ret = service.setReadEpcStatus((byte) 1);
            handler.obtainMessage(0, ret).sendToTarget();
        }
    }

    class StopTask implements Runnable {

        @Override
        public void run() {
            int ret = service.setReadEpcStatus((byte) 0);
            handler.obtainMessage(1, ret).sendToTarget();
        }
    }

    class BatteryTask implements Runnable {

        @Override
        public void run() {
            final IvrJackService.BatteryBuzzerStatus status = new IvrJackService.BatteryBuzzerStatus();
            final int ret = service.getBatteryBuzzerStatus(status);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (ret == 0) {
                        jackScanInterface.onCheckBattery(String.format("%02d%%", status.battery));
                    }
                }
            });
        }
    }


    class MainHandler extends Handler {

        public MainHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    int ret = (Integer) msg.obj;
                    if (ret == 0) {
                        jackScanInterface.onStartScan(true, 0);
                    } else {
                        jackScanInterface.onStartScan(false, ret);
                    }
                    break;
                }
                case 1: {
                    jackScanInterface.onEndScan();
                    break;
                }
            }
        }
    }

    private class VolumnBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int volumetMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            jackScanInterface.onVolumeChange((int)(((double)volume/ volumetMax)*100));
        }

    }

    public enum JackScanStatus {
        scanDetecting,
        scanRecognized,
        scanUnRecognized,
        scanPlugout;
    }

}
