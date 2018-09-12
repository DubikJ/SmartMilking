package com.avatlantik.smartmilking.activity;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avatlantik.smartmilking.R;
import com.avatlantik.smartmilking.adapter.MilkingAdapter;
import com.avatlantik.smartmilking.app.SMApplication;
import com.avatlantik.smartmilking.milkingStanchion.MilkingStanchion;
import com.avatlantik.smartmilking.milkingStanchion.MilkingStanchionInterface;
import com.avatlantik.smartmilking.milkingStanchion.MilkingStancionParams;
import com.avatlantik.smartmilking.model.MilkingResult;
import com.avatlantik.smartmilking.model.db.DataBase;
import com.avatlantik.smartmilking.model.db.Milking;
import com.avatlantik.smartmilking.model.db.MilkingRecord;
import com.avatlantik.smartmilking.rfid.JackScan;
import com.avatlantik.smartmilking.rfid.JackScanInterface;
import com.avatlantik.smartmilking.utils.ActivityUtils;
import com.avatlantik.smartmilking.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.avatlantik.smartmilking.common.Consts.MIN_MILK_MASHINE_ID;

public class MilkingActivity extends AppCompatActivity {

    private static final int ANIM_DURATION = 200;
    private boolean doubleBackToExitPressedOnce = false;

    private static final String MILKING_LIST_KEY = "milking_list_key";
    private static final String MILKING_KEY = "milking_key";
    private static final String ISSCANNING_KEY = "isscanning_key";

    @Inject
    DataBase dataBase;
    @Inject
    ActivityUtils activityUtils;
    @Inject
    NetworkUtils networkUtils;

    private List<Milking> milkingList;
    private RecyclerView recyclerView;
    private View container, containerBottom;
    private RelativeLayout layoutRfid, layoutWifi, layoutPump;
    private LinearLayout pumpContainer, vacuumPump, milkPump, layoutLoAD;
    private ImageView statusRfidImage, statusWifiImage, vacuumPumpImage,
            milkPumpImage, loadImage;
    private TextView volumeDevice, statusRfid, connectRfid, statusWifi, connectWifi,
            statusVacuumPump, vacuumPumpText, milkPumpText, scanResult, litresSum;
    private MilkingAdapter adapter;
    private PopupWindow windowScan;
    private JackScan jackScan;

    private MilkingStanchion milkingStanchion;

    private int mLeftDelta;
    private int mTopDelta;
    private float mWidthScale;
    private float mHeightScale;

    private Animation animationScan;
    private Boolean isScanning, scanMashine;

    private Milking selectedMilking;
    private Date startDay, endDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milking);

        ((SMApplication) getApplication()).getComponent().inject(this);

        recyclerView = (RecyclerView) findViewById(R.id.milking_list);

        initDayPeriod();

        if(recyclerView!=null) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                LinearLayoutManager horizontalLM
                        = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(horizontalLM);
                litresSum = (TextView) findViewById(R.id.sum_litres);
                litresSum.setText("0 " + getString(R.string.litres_short));

                dataBase.milkingRecordDao().getSumLitresByPeriod(
                            startDay.getTime(), endDay.getTime())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe((litres) -> {
                                litresSum.setText(String.valueOf(litres)
                                        +getString(R.string.litres_short));
                            });
            } else {
                LinearLayoutManager verticalLM
                        = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(verticalLM);
                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            }
        }

        dataBase.milkingDao().getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((List<Milking> milkings) -> {
                    if(milkings.size()==0){
                        milkingList=new ArrayList<>();
                        milkingList.add(Milking.builder().id(1).build());
                        milkingList.add(Milking.builder().id(2).build());
                        milkingList.add(Milking.builder().id(3).build());
                        milkingList.add(Milking.builder().id(4).build());
                        AsyncTask.execute(() -> {dataBase.milkingDao().insertList(milkingList);});
                    }else{
                        milkingList = milkings;
                    }
                    adapter = new MilkingAdapter(this, milkingList, getResources().getConfiguration().orientation);
                    recyclerView.setAdapter(adapter);
                });

        container = (View) findViewById(R.id.container);
        layoutLoAD = (LinearLayout) findViewById(R.id.load_layout);
        layoutLoAD.setVisibility(View.GONE);
        loadImage = (ImageView) findViewById(R.id.load_progress);
        layoutRfid = (RelativeLayout) findViewById(R.id.rfid_layout);
        layoutRfid.setOnClickListener((v)->{
            if(jackScan!=null&&jackScan.isConnected()) jackScan.checkBattery();
        });
        statusRfidImage = (ImageView) findViewById(R.id.rfid_status_image);
        statusRfid = (TextView) findViewById(R.id.rfid_status);
        connectRfid = (TextView) findViewById(R.id.rfid_connect);
        connectRfid.setVisibility(View.GONE);
        connectRfid.setOnClickListener((v)->{
            if(jackScan!=null){
                jackScan.disconnect();
                jackScan.connect();
            }
        });
        volumeDevice = (TextView) findViewById(R.id.volume);

        layoutWifi = (RelativeLayout) findViewById(R.id.wifi_layout);
        statusWifiImage = (ImageView) findViewById(R.id.wifi_status_image);
        statusWifi = (TextView) findViewById(R.id.wifi_status);
        connectWifi = (TextView) findViewById(R.id.connet_wifi);
        connectWifi.setOnClickListener((v)->{
            milkingStanchion.connectToWifi();
        });

        pumpContainer = (LinearLayout) findViewById(R.id.pump_container);
        layoutPump = (RelativeLayout) findViewById(R.id.pump_layout);

        statusVacuumPump = (TextView) findViewById(R.id.pump_status);
        vacuumPump = (LinearLayout) findViewById(R.id.vacuum_pump);
        vacuumPump.setOnClickListener((v)->{
            if(milkingStanchion.isConnected()){
                milkingStanchion.startVacuumPump();
            }
        });
        vacuumPumpImage = (ImageView) findViewById(R.id.vacuum_pump_icon);
        vacuumPumpText = (TextView) findViewById(R.id.vacuum_pump_text);

        milkPump = (LinearLayout) findViewById(R.id.milk_pump);
        milkPump.setOnClickListener((v)->{
            if(milkingStanchion.isConnected()){
                milkingStanchion.startMilkPump();
            }
        });
        milkPumpImage = (ImageView) findViewById(R.id.milk_pump_icon);
        milkPumpText = (TextView) findViewById(R.id.milk_pump_text);

        statusRfidImage.setImageResource(R.drawable.ic_scan_off);
        statusRfid.setText(getString(R.string.scanner_disconnected));
        volumeDevice.setText("0");
        layoutRfid.setBackgroundColor(getResources().getColor(R.color.colorRed));

        jackScan=new JackScan(this, new JackScanInterface() {
            @Override
            public void onConnect(String deviceSn) {}

            @Override
            public void onDisconnect() {}


            @Override
            public void onStatusChange(JackScan.JackScanStatus status, int percentVolume) {

                switch (status) {
                    case scanDetecting:
                        if (percentVolume < 100) {
                            new AlertDialog.Builder(MilkingActivity.this)
                                    .setTitle(getString(R.string.set_volume_max))
                                    .setPositiveButton("OK", (DialogInterface dialog, int which) ->
                                            dialog.dismiss() ).show();

                        }

                        statusRfidImage.setImageResource(R.drawable.ic_scan_off);
                        statusRfid.setText(getString(R.string.scanner_detecting));
                        volumeDevice.setText(String.valueOf(percentVolume));
                        layoutRfid.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                        connectRfid.setVisibility(View.GONE);
                        hideLoadAnimation();
                        break;
                    case scanRecognized:

                        statusRfidImage.setImageResource(R.drawable.ic_scan_on);
                        statusRfid.setText(getString(R.string.scanner_connected));
                        volumeDevice.setText(String.valueOf(percentVolume));
                        layoutRfid.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        connectRfid.setVisibility(View.GONE);
                        if(selectedMilking!=null && isScanning) {
                            callScan(selectedMilking, scanMashine);
                        }
                        break;
                    case scanUnRecognized:

                        statusRfidImage.setImageResource(R.drawable.ic_scan_off);
                        statusRfid.setText(getString(R.string.scanner_unrecognized));
                        volumeDevice.setText(String.valueOf(percentVolume));
                        layoutRfid.setBackgroundColor(getResources().getColor(R.color.colorRed));
                        connectRfid.setVisibility(View.VISIBLE);
                        if(windowScan!=null){
                            if(windowScan.isShowing()) {
                                windowScan.dismiss();
                            }
                        }
                        hideLoadAnimation();

                        activityUtils.showQuestion(MilkingActivity.this, null,
                                getString(R.string.scanner_unrecognized)+". "
                                        +getString(R.string.action_repeat)+"?",
                                new ActivityUtils.QuestionAnswer() {
                                    @Override
                                    public void onPositiveAnsver() {
                                        jackScan.disconnect();
                                        jackScan.connect();
                                    }

                                    @Override
                                    public void onNegativeAnsver() {}

                                    @Override
                                    public void onNeutralAnsver() {}

                                });

                        break;
                    case scanPlugout:
                        statusRfidImage.setImageResource(R.drawable.ic_scan_off);
                        statusRfid.setText(getString(R.string.scanner_disconnected));
                        volumeDevice.setText(String.valueOf(percentVolume));
                        layoutRfid.setBackgroundColor(getResources().getColor(R.color.colorRed));
                        connectRfid.setVisibility(View.GONE);
                        if(windowScan!=null){
                            if(windowScan.isShowing()) {
                                windowScan.dismiss();
                            }
                        }
                        hideLoadAnimation();
                        break;
                }
            }

            @Override
            public void onResult(String result) {
              //  boolean isScanAnimal;
                int res;
                try {
                    res = Integer.parseInt(result.replaceAll( "[^\\d]", "" ));
                } catch (NumberFormatException e) {
//                    windowScan.dismiss();
//                    activityUtils.showMessage(getString(R.string.scanner_result_error), MilkingActivity.this);
                    return;
                }

                if(scanResult!=null) scanResult.setText(String.valueOf(res));
//                if(res==0){
//                    windowScan.dismiss();
//                    activityUtils.showMessage(getString(R.string.scanner_result_error), MilkingActivity.this);
//                    return;
//                }

//                if(res<MIN_MILK_MASHINE_ID){
//                    isScanAnimal=true;
//                }else{
//                    isScanAnimal=false;
//                }

                if(windowScan!=null){
                    if(windowScan.isShowing()){
//                        if(isScanAnimal) {
//                            selectedMilking.setIdCow(res);
//                        }else{
//                            selectedMilking.setIdMachine(res);
//                        }

                        if(scanMashine){
                            if(res>=MIN_MILK_MASHINE_ID){
                                selectedMilking.setIdMachine(res);
                                if(selectedMilking.getIdMachine()!=0) {
                                    runOnUiThread(() -> {
                                        if(jackScan.isStarted()){
                                            jackScan.stop();
                                        }
                                        animationScan.cancel();
                                    if (windowScan != null && windowScan.isShowing()) {
                                        windowScan.dismiss();
                                    }
                                    });
                                }
                            }
                        }else {
                            if(res<MIN_MILK_MASHINE_ID){
                                selectedMilking.setIdCow(res);
                                if (selectedMilking.getIdCow() != 0) {
                                    runOnUiThread(() -> {
                                        milkingStanchion.startMilking(selectedMilking);

                                        if(jackScan.isStarted()){
                                            jackScan.stop();
                                        }
                                        animationScan.cancel();

                                    if (windowScan != null && windowScan.isShowing()) {
                                        windowScan.dismiss();
                                    }
                                    });
                                }
                            }
                        }
                    }
                }

            }

            @Override
            public void onVolumeChange(int percentVolume) {
                volumeDevice.setText(String.valueOf(percentVolume));
            }

            @Override
            public void onStartScan(boolean started, int status) {
                hideLoadAnimation();
                if(started){
                    isScanning = true;
                    showScan();
                }else{
                    activityUtils.showShortToast(MilkingActivity.this,
                            getString(R.string.scanner_error__start_scan)+": "+ status);
                }
            }

            @Override
            public void onEndScan() {
                isScanning = false;
//                if (windowScan != null && windowScan.isShowing()) {
//                    windowScan.dismiss();
//                }
                hideLoadAnimation();
            }

            @Override
            public void onCheckBattery(String result) {
                activityUtils.showMessage(
                        getString(R.string.scanner_battery)+": "
                                + result,
                        MilkingActivity.this);
            }
        });

        milkingStanchion = new MilkingStanchion(this, getLoaderManager(),
                new MilkingStanchionInterface() {
                    @Override
                    public void onStatusChange(MilkingStancionParams.ConnectionStatus status) {
                        runOnUiThread(()-> {
                            switch (status) {
                                case WIFI_OFF:
                                    statusWifiImage.setImageResource(R.drawable.ic_wifi_off);
                                    statusWifi.setText(getString(R.string.error_wifi_switch));
                                    layoutWifi.setBackgroundColor(getResources().getColor(R.color.colorRed));
                                    connectWifi.setVisibility(View.VISIBLE);
                                    pumpContainer.setVisibility(View.GONE);

                                    if(milkingList!=null) {
                                        for (Milking milking : milkingList) {
                                            milking.setMilkingStart(false);
                                            milking.setMilkingEnd(true);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                    break;
                                case NOT_CONNECTED_WIFI:
                                    statusWifiImage.setImageResource(R.drawable.ic_wifi_off);
                                    statusWifi.setText(getString(R.string.error_wifi_milk_stanchion_connection));
                                    layoutWifi.setBackgroundColor(getResources().getColor(R.color.colorRed));
                                    connectWifi.setVisibility(View.VISIBLE);
                                    pumpContainer.setVisibility(View.GONE);

                                    if(milkingList!=null) {
                                        for (Milking milking : milkingList) {
                                            milking.setMilkingStart(false);
                                            milking.setMilkingEnd(true);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                    break;
                                case NOT_CONNECTED:
                                    statusWifiImage.setImageResource(R.drawable.ic_wifi_off);
                                    statusWifi.setText(getString(R.string.error_milk_stanchion_connection));
                                    layoutWifi.setBackgroundColor(getResources().getColor(R.color.colorRed));
                                    connectWifi.setVisibility(View.VISIBLE);
                                    pumpContainer.setVisibility(View.GONE);

                                    if(milkingList!=null) {
                                        for (Milking milking : milkingList) {
                                            milking.setMilkingStart(false);
                                            milking.setMilkingEnd(true);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                    break;
                                case CONNECTION:
                                    statusWifiImage.setImageResource(R.drawable.ic_wifi_off);
                                    statusWifi.setText(getString(R.string.wifi_milk_stanchion_connection));
                                    layoutWifi.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                                    connectWifi.setVisibility(View.GONE);
                                    pumpContainer.setVisibility(View.GONE);

                                    if(milkingList!=null) {
                                        for (Milking milking : milkingList) {
                                            milking.setMilkingStart(false);
                                            milking.setMilkingEnd(true);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                    break;
                                case ACCIDENT:
                                    statusWifiImage.setImageResource(R.drawable.ic_wifi_on);
                                    statusWifi.setText(getString(R.string.accident));
                                    layoutWifi.setBackgroundColor(getResources().getColor(R.color.colorRed));
                                    connectWifi.setVisibility(View.GONE);
                                    pumpContainer.setVisibility(View.GONE);
                                    activityUtils.showMessage(getString(R.string.accident), MilkingActivity.this);

                                    if(milkingList!=null) {
                                        for (Milking milking : milkingList) {
                                            milking.setMilkingStart(false);
                                            milking.setMilkingEnd(true);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                    break;
                                case CONNECTED:
                                    statusWifiImage.setImageResource(R.drawable.ic_wifi_on);
                                    statusWifi.setText(getString(R.string.wifi_milk_stanchion_connected));
                                    layoutWifi.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                    connectWifi.setVisibility(View.GONE);
                                    pumpContainer.setVisibility(View.VISIBLE);
                                    break;

                            }
                        });
                    }

                    @Override
                    public void onResult(MilkingResult milkingResult) {

                        initData(milkingResult.isMilkingStart1(), milkingResult.getLitres1(), milkingList.get(0));

                        initData(milkingResult.isMilkingStart2(), milkingResult.getLitres2(), milkingList.get(1));

                        initData(milkingResult.isMilkingStart3(), milkingResult.getLitres3(), milkingList.get(2));

                        initData(milkingResult.isMilkingStart4(), milkingResult.getLitres4(), milkingList.get(3));

                        runOnUiThread(()-> {
                            adapter.notifyDataSetChanged();
                        });

                    }

                    @Override
                    public void onWishingAllowed(Boolean allowed) {
                        runOnUiThread(()-> {
                            if (allowed) {
                                statusVacuumPump.setText(getString(R.string.washing_allowed));
                                layoutPump.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                //  vacuumPump.setVisibility(View.VISIBLE);
                            } else {
                                statusVacuumPump.setText(getString(R.string.washing_forbidden));
                                layoutPump.setBackgroundColor(getResources().getColor(R.color.colorRed));
                                //  vacuumPump.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onVacuumPumpStart(Boolean started) {
                        runOnUiThread(()-> {
                            if (started) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    vacuumPump.setBackground(getDrawable(R.drawable.shape_button_on));
                                }else{
                                    vacuumPump.setBackground(ContextCompat.getDrawable(MilkingActivity.this, R.drawable.shape_button_on));
                                }
                                vacuumPumpText.setTextColor(getResources().getColor(R.color.colorWhite));
                                vacuumPumpImage.setImageResource(R.drawable.ic_vacuum_pump);
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    vacuumPump.setBackground(getDrawable(R.drawable.shape_button_off));
                                }else{
                                    vacuumPump.setBackground(ContextCompat.getDrawable(MilkingActivity.this, R.drawable.shape_button_off));
                                }
                                vacuumPumpText.setTextColor(getResources().getColor(R.color.colorPrimary));
                                vacuumPumpImage.setImageResource(R.drawable.ic_vacuum_pump_off);
                            }
                        });
                    }

                    @Override
                    public void onMilkPumpStart(Boolean started) {
                        runOnUiThread(()-> {
                            if (started) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    milkPump.setBackground(getDrawable(R.drawable.shape_button_on));
                                }else{
                                    milkPump.setBackground(ContextCompat.getDrawable(MilkingActivity.this, R.drawable.shape_button_on));
                                }
                                milkPumpText.setTextColor(getResources().getColor(R.color.colorWhite));
                                milkPumpImage.setImageResource(R.drawable.ic_pump);
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    milkPump.setBackground(getDrawable(R.drawable.shape_button_off));
                                }else{
                                    milkPump.setBackground(ContextCompat.getDrawable(MilkingActivity.this, R.drawable.shape_button_off));
                                }
                                milkPumpText.setTextColor(getResources().getColor(R.color.colorPrimary));
                                milkPumpImage.setImageResource(R.drawable.ic_pump_off);
                            }
                        });
                    }

                    private void initData(Boolean startedResult, Double litresResult, Milking milkingItem){

                        if(!startedResult &&
                                milkingItem.isMilkingStart() &&
                                !milkingItem.isMilkingEnd()) {
                            AsyncTask.execute(() -> {
                                dataBase.milkingRecordDao().insert(
                                        MilkingRecord.builder()
                                                .idPlace(milkingItem.getId())
                                                .idMachine(milkingItem.getIdMachine())
                                                .idCow(milkingItem.getIdCow())
                                                .litres(milkingItem.getLitres())
                                                .date(new Date().getTime())
                                                .build()
                                );
                            });

                            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                                    && litresSum!=null) {
                                dataBase.milkingRecordDao().getSumLitresByPeriod(
                                         startDay.getTime(), endDay.getTime())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe((litres) -> {
                                             litresSum.setText(String.valueOf(litres)
                                                     +getString(R.string.litres_short));
                                        });
                            }
                        }
                        milkingItem.setLitres(litresResult);
                        milkingItem.setMilkingStart(startedResult);
                        milkingItem.setMilkingEnd(!startedResult);
                    }
                });

        jackScan.connect();

        milkingStanchion.connect();

        isScanning = false;
        scanMashine = false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.milking_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_report) {
            Intent intent = new Intent(MilkingActivity.this, MilkingReportActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(milkingList!=null) {
            AsyncTask.execute(() -> {
                for (Milking milking : milkingList) {
                    dataBase.milkingDao().update(milking);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jackScan.destroy();
        milkingStanchion.disconnect();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
        }

        doubleBackToExitPressedOnce = true;
        activityUtils.showShortToast(this, getString(R.string.double_press_exit));

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (milkingList != null) {
            savedInstanceState.putParcelableArrayList(MILKING_LIST_KEY, new ArrayList<>(milkingList));
        }

        if (selectedMilking != null) {
            savedInstanceState.putParcelable(MILKING_KEY, selectedMilking);
        }

        savedInstanceState.putBoolean(ISSCANNING_KEY, isScanning);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(MILKING_KEY)) {
            selectedMilking = savedInstanceState.getParcelable(MILKING_KEY);
        }

        if (savedInstanceState.containsKey(MILKING_LIST_KEY)) {
            milkingList = savedInstanceState.getParcelableArrayList(MILKING_LIST_KEY);
        }

        if (milkingList != null) {
            adapter = new MilkingAdapter(this, milkingList, getResources().getConfiguration().orientation);
            recyclerView.setAdapter(adapter);
        }

        if (savedInstanceState.containsKey(ISSCANNING_KEY)) {
            isScanning = savedInstanceState.getBoolean(ISSCANNING_KEY);
        }

    }

    public void cancelMilkingn(Milking selectedItem){

        if(!selectedItem.isMilkingStart())return;

        if(!milkingStanchion.isConnected()){
            activityUtils.showMessage(
                    getString(R.string.error_milk_stanchion_connection_message), MilkingActivity.this);
            return;
        }

        activityUtils.showQuestion(MilkingActivity.this, null,
                getString(R.string.questions_complete_milking)+" №"+selectedItem.getId(),
                new ActivityUtils.QuestionAnswer() {
                    @Override
                    public void onPositiveAnsver() {
                        selectedMilking = selectedItem;
                        milkingStanchion.cancelMilking(selectedMilking);
                    }

                    @Override
                    public void onNegativeAnsver() {}

                    @Override
                    public void onNeutralAnsver() {}

                });

    }

    public void callScan(final Milking selectedItem, boolean mashine){

        if(!jackScan.isConnected()){
            activityUtils.showMessage(
                    getString(R.string.scanner_disconnected), MilkingActivity.this);
            return;
        }

        if(!milkingStanchion.isConnected()){
            activityUtils.showMessage(
                    getString(R.string.error_milk_stanchion_connection_message), MilkingActivity.this);
            return;
        }

        scanMashine = mashine;

        selectedMilking = selectedItem;

        if(!isScanning) {
            jackScan.start();
        }
        showLoadAnimation();
    }

    private void showScan(){

        View view = findViewForPosition(milkingList.indexOf(selectedMilking));
        if(view==null) {
            return;
        }

        if(windowScan!=null){
            if(windowScan.isShowing()){
                windowScan.dismiss();
            }
        }

        final View viewScan = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.activity_scan, null);

        windowScan = new PopupWindow(viewScan,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        windowScan.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        windowScan.setFocusable(true);
        windowScan.setOutsideTouchable(true);
        windowScan.update();
        windowScan.setOnDismissListener(()-> {
            adapter.fillData(view, selectedMilking);
            animationScan.cancel();
            showLoadAnimation();
            if(windowScan!=null){
                if(windowScan.isShowing()){
                    hideScanAnimation(viewScan, ()-> {
                        windowScan.dismiss();
                    });
                }
            }

            if(jackScan.isStarted()){
                jackScan.stop();
            }
        });

        windowScan.showAtLocation(view, Gravity.CENTER, 0, 0);
        showScanAnimation(view, viewScan);
        startScan(viewScan, selectedMilking);
    }

    private void startScan(View viewScan, Milking selectedItem){
        TextView name = (TextView) viewScan.findViewById(R.id.id_machine);
        name.setText(String.valueOf(selectedItem.getId()));
        View lineScan = viewScan.findViewById(R.id.bar_scan);
        animationScan = AnimationUtils.loadAnimation(MilkingActivity.this, R.anim.scan);
        animationScan.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                lineScan.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        lineScan.startAnimation(animationScan);
        scanResult = (TextView) viewScan.findViewById(R.id.scan_result);
    }

    private void showScanAnimation(final View viewParent, final View viewChild) {

        ViewTreeObserver observer = viewChild.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                viewChild.getViewTreeObserver().removeOnPreDrawListener(this);

                int[] screenLocation1 = new int[2];
                viewParent.getLocationOnScreen(screenLocation1);

                int thumbnailTop = screenLocation1[1];
                int thumbnailLeft = screenLocation1[0];
                int thumbnailWidth = viewParent.getWidth();
                int thumbnailHeight = viewParent.getHeight();

                int[] screenLocation = new int[2];
                viewChild.getLocationOnScreen(screenLocation);

                mLeftDelta = thumbnailLeft - screenLocation[0];
                mTopDelta = thumbnailTop - screenLocation[1];
                mWidthScale = (float) thumbnailWidth / viewChild.getWidth();
                mHeightScale = (float) thumbnailHeight / viewChild.getHeight();

                viewChild.setPivotX(0);
                viewChild.setPivotY(0);
                viewChild.setScaleX(mWidthScale);
                viewChild.setScaleY(mHeightScale);
                viewChild.setTranslationX(mLeftDelta);
                viewChild.setTranslationY(mTopDelta);

                // interpolator where the rate of change starts out quickly and then decelerates.
                TimeInterpolator sDecelerator = new DecelerateInterpolator();

                // Animate scale and translation to go from thumbnail to full size
                viewChild.animate().setDuration(ANIM_DURATION).scaleX(1).scaleY(1).
                        translationX(0).translationY(0).setInterpolator(sDecelerator);

                ObjectAnimator bgAnim = ObjectAnimator.ofInt(R.color.colorTrans, "alpha", 0, 255);
                bgAnim.setDuration(ANIM_DURATION);
                bgAnim.start();

                return true;
            }
        });
    }

    private void hideScanAnimation(View view, final Runnable endAction) {

        TimeInterpolator sInterpolator = new AccelerateInterpolator();
        view.animate().setDuration(ANIM_DURATION).scaleX(mWidthScale).scaleY(mHeightScale).
                translationX(mLeftDelta).translationY(mTopDelta)
                .setInterpolator(sInterpolator).withEndAction(endAction);

        // Fade out background
        ObjectAnimator bgAnim = ObjectAnimator.ofInt(R.color.colorTrans, "alpha", 0);
        bgAnim.setDuration(ANIM_DURATION);
        bgAnim.start();
    }

    private void showLoadAnimation() {
        container.setEnabled(false);
        layoutLoAD.setVisibility(View.VISIBLE);
        AnimationDrawable anim = (AnimationDrawable)loadImage.getDrawable();
        anim.start();
    }

    private void hideLoadAnimation() {
        container.setEnabled(true);
        layoutLoAD.setVisibility(View.GONE);
        AnimationDrawable anim = (AnimationDrawable) loadImage.getDrawable();
        anim.stop();
    }

    public View findViewForPosition(int position) {
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
        return viewHolder == null ? null : viewHolder.itemView.findViewById(R.id.card_view);
    }

    private void initDayPeriod(){

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        startDay = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        endDay = cal.getTime();
    }
}
