package com.avatlantik.smartmilking.activity;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avatlantik.smartmilking.R;
import com.avatlantik.smartmilking.adapter.MilkingReportAdapter;
import com.avatlantik.smartmilking.app.SMApplication;
import com.avatlantik.smartmilking.model.db.DataBase;
import com.avatlantik.smartmilking.model.db.MilkingRecord;
import com.avatlantik.smartmilking.ui.widget.DateTimeView;
import com.avatlantik.smartmilking.utils.ActivityUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class MilkingReportActivity extends AppCompatActivity {


    @Inject
    DataBase dataBase;
    @Inject
    ActivityUtils activityUtils;


    private LinearLayout dataContainer;
    private TextView noData;
    private RecyclerView milkingReportList;
    private List<MilkingRecord> milkingRecordList;
    private  MilkingReportAdapter adapter;
    private Date dateS, dateE;
    private String placeT, machineT, cowT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milking);

        ((SMApplication) getApplication()).getComponent().inject(this);

        setTitle(getResources().getString(R.string.action_report));
        setContentView(R.layout.activity_report);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        dataContainer = (LinearLayout) findViewById(R.id.data_container);
        dataContainer.setVisibility(View.GONE);

        noData = (TextView) findViewById(R.id.no_data);
        milkingReportList = (RecyclerView) findViewById(R.id.milking_report_list);
        milkingReportList.setLayoutManager(new LinearLayoutManager(this));

        schowSettings();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            schowSettings();
            return true;
        }

        if (id == R.id.action_save) {
            createExcelSheet();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void schowSettings(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setTitle(getResources().getString(R.string.action_setting));
        final View viewInflated = LayoutInflater.from(this).inflate(R.layout.activity_report_settings, null);

        DateTimeView dateStart = (DateTimeView) viewInflated.findViewById(R.id.date_start);
        if (dateS!=null) {
            dateStart.setDate(dateS);
        }

        DateTimeView dateEnd = (DateTimeView) viewInflated.findViewById(R.id.date_end);
        if (dateE!=null) {
            dateEnd.setDate(dateE);
        }

        EditText place = (EditText) viewInflated.findViewById(R.id.place);
        if (!TextUtils.isEmpty(placeT)) {
            place.setText(placeT);
        }
        EditText machine = (EditText) viewInflated.findViewById(R.id.machine);
        if (!TextUtils.isEmpty(machineT)) {
            machine.setText(machineT);
        }
        EditText cow = (EditText) viewInflated.findViewById(R.id.cow);
        if (!TextUtils.isEmpty(cowT)) {
            cow.setText(cowT);
        }

        builder.setView(viewInflated);

        builder.setPositiveButton(getResources().getString(R.string.questions_answer_ok),
                (dialog, which) -> {

                    Boolean addedIf = false;
                    String text = "WHERE";

                    if(dateStart.getDate()!=null) {
                        text = text + " date >= " + dateStart.getDate().getTime();
                        addedIf = true;
                    }
                    dateS = dateStart.getDate();

                    if(dateEnd.getDate()!=null) {
                        text = text + (addedIf ?" AND " : "")+ " date <= " +dateEnd.getDate().getTime();
                        addedIf = true;
                    }
                    dateE = dateEnd.getDate();

                    if (!TextUtils.isEmpty(place.getText().toString())) {
                        text = text + (addedIf ?" AND " : "")+ " id_place = " +place.getText().toString();
                        addedIf = true;
                    }
                    placeT = place.getText().toString();

                    if (!TextUtils.isEmpty(machine.getText().toString())) {
                        text = text + (addedIf ?" AND " : "")+ " id_mashine = " +machine.getText().toString();
                        addedIf = true;
                    }
                    machineT = machine.getText().toString();

                    if (!TextUtils.isEmpty(cow.getText().toString())) {
                        text = text + (addedIf ?" AND " : "")+" id_cow = " +cow.getText().toString();
                        addedIf = true;
                    }
                    cowT = cow.getText().toString();

                    dataBase.milkingRecordDao().getByQuery(
                            new SimpleSQLiteQuery("SELECT * FROM milking_record " + (addedIf ? text : "")))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe((List<MilkingRecord> milkings) -> {
                                milkingRecordList = milkings;
                                if(milkings.size()>0) {
                                    noData.setVisibility(View.GONE);
                                    dataContainer.setVisibility(View.VISIBLE);
                                    MilkingReportAdapter adapter = new MilkingReportAdapter(
                                            this,
                                            milkings);
                                    milkingReportList.setAdapter(adapter);
                                }else{
                                    noData.setVisibility(View.VISIBLE);
                                    dataContainer.setVisibility(View.GONE);
                                }
                            });

                });

        builder.setNegativeButton(getResources().getString(R.string.questions_answer_cancel),
                (dialog, which) ->{});
        builder.setCancelable(true);

        AlertDialog alertQuestion = builder.create();
        alertQuestion.show();
        alertQuestion.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertQuestion.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
        alertQuestion.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));



    }

    private void createExcelSheet() {

        if(milkingRecordList==null || milkingRecordList.size()==0){
            activityUtils.showShortToast(this, getString(R.string.table_report_no_data));
            return;
        }

        String Fnamexls="SmartMilking"+new SimpleDateFormat("dd.MM.yy hh:mm").format(new Date())+ ".xls";
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + "/SmartMilking");
        directory.mkdirs();
        File file = new File(directory, Fnamexls);

        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook;
        try {
            int a = 1;
            workbook = Workbook.createWorkbook(file, wbSettings);
            WritableSheet sheet = workbook.createSheet("First Sheet", 0);

            Label labelcap1 = new Label(0,0,getString(R.string.table_report_data));
            Label labelcap2 = new Label(1,0,getString(R.string.table_report_place));
            Label labelcap3 = new Label(2,0,getString(R.string.table_report_machine));
            Label labelcap4 = new Label(3,0,getString(R.string.table_report_cow));
            Label labelcap5 = new Label(4,0,getString(R.string.table_report_litres));
            try {
                sheet.addCell(labelcap1);
                sheet.addCell(labelcap2);
                sheet.addCell(labelcap3);
                sheet.addCell(labelcap4);
                sheet.addCell(labelcap5);
            } catch (RowsExceededException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                activityUtils.showShortToast(this, e.toString());
            } catch (WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                activityUtils.showShortToast(this, e.toString());
            }

            int i=1;
            for (MilkingRecord milkingRecord : milkingRecordList){
                Label label1 = new Label(0,i,new SimpleDateFormat("dd.MM.yy hh:mm").format(new Date(milkingRecord.getDate())));
                Label label2 = new Label(1,i,String.valueOf(milkingRecord.getIdPlace()));
                Label label3 = new Label(2,i,String.valueOf(milkingRecord.getIdMachine()));
                Label label4 = new Label(3,i,String.valueOf(milkingRecord.getIdCow()));
                Label label5 = new Label(4,i,String.valueOf(milkingRecord.getLitres()));
                try {
                    sheet.addCell(label1);
                    sheet.addCell(label2);
                    sheet.addCell(label3);
                    sheet.addCell(label4);
                    sheet.addCell(label5);
                    i++;
                } catch (RowsExceededException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    activityUtils.showShortToast(this, e.toString());
                } catch (WriteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    activityUtils.showShortToast(this, e.toString());
                }
            }

            workbook.write();
            try {
                workbook.close();
                activityUtils.showLongToast(this, getString(R.string.file_writed)
                          +": "+file.getAbsolutePath());
            } catch (WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                activityUtils.showShortToast(this, e.toString());
            }
            //createExcel(excelSheet);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            activityUtils.showShortToast(this, e.toString());
        }


    }

}
