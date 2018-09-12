package com.avatlantik.smartmilking.ui.widget;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.avatlantik.smartmilking.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeView extends RelativeLayout{
    private ImageView openDateIV;
    private TextInputLayout textInputLayout;
    private EditText dateET;
    private ImageView clearDateIV;
    private Calendar dateTime;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:MM:SS");
    private boolean manually, clear;

    private DatePickerDialog.OnDateSetListener dateDialog = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            initDateToView();

            manually = false;

        }
    };

    private TimePickerDialog.OnTimeSetListener timedialog = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTime.set(Calendar.MINUTE, minute);

            initDateToView();

        }
    };

    public DateTimeView(Context context) {
        super(context);
        init();
        proseccingEvents();
    }

    public DateTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        proseccingEvents();
        initAttributes(attrs);
    }

    public DateTimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        proseccingEvents();
        initAttributes(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DateTimeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        proseccingEvents();
        initAttributes(attrs);
    }

    public Calendar getDateCalendar() {
        return dateTime;
    }

    public void setDateCalendar(Calendar dateTime) {
        this.dateTime = dateTime;
    }

    public void setTitle(String title) {
        textInputLayout.setHint(title);
    }

    public Date getDate() {
        if(dateTime == null || dateTime.get(Calendar.YEAR)<1000) {
           return null;
        }
        return dateTime.getTime();
    }

    public void setDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        this.dateTime = cal;
        initDateToView();
    }

    private void init() {
        inflate(getContext(), R.layout.date_time_picket, this);
        this.openDateIV = (ImageView)findViewById(R.id.date_open);
        this.textInputLayout = (TextInputLayout) findViewById(R.id.date_layout);
        this.dateET = (EditText)findViewById(R.id.date);
        this.clearDateIV = (ImageView)findViewById(R.id.clear);

        dateTime = Calendar.getInstance();
        dateTime.set(999,0,0,0,0,0);

    }

    private void initAttributes(AttributeSet attrs) {

            if (attrs != null) {
                TypedArray a = getContext().getTheme().obtainStyledAttributes(
                        attrs,
                        R.styleable.DateTimeView,
                        0, 0);
                try {

                    setTitle(
                            a.getString(
                                    R.styleable.DateTimeView_title)
                    );
                } finally {
                    a.recycle();
                }
            }
        }


    private void proseccingEvents() {

        openDateIV.setOnClickListener((v)->{

            showDateDialog();

        });

        clearDateIV.setOnClickListener((v)->{
            dateET.setText("  .  .       :  :  ");
            dateTime.set(999,0,0,0,0,0);
            manually = false;
        });

        dateET.setOnFocusChangeListener((v, hasFocus) ->{
            if(hasFocus){
                if(TextUtils.isEmpty(dateET.getText())){
                    dateET.setText("  .  .       :  :  ");
                    dateTime.set(999,0,0,0,0,0);
                    manually = false;
                }
            }else{
//                if(manually){
//                    dateTime = stringToDate(dateET.getText().toString());
//                    manually = false;
//                }
                if(dateTime.get(Calendar.YEAR)<1000){
                    clear =true;
                    dateET.setText("");
                }
            }
        });
        dateET.addTextChangedListener(new TextWatcher() {
            int length_before = 0;
            int start = 0;
            int after = 0;
            int count = 0;
            int before = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                length_before = s.length();
                this.start = start;
                this.after = after;
                this.count = count;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                this.before = before;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(clear){
                    clear = false;
                    return;
                }
                String text = s.toString().replaceAll( "[^\\d]", "" );
                String[] array = text.split("");
                dateET.removeTextChangedListener(this);
                s.clear();
                int i;
                String textRes ="";
                for(i=1; i<15; i++){
                    String textNumber;
                    try {
                        textNumber = array[i];
                        int number = Integer.valueOf(textNumber);
                        if(i==1 && number>3) {
                            textNumber = "3";
                        }else if(i==2 &&
                                number>1 &&
                                Integer.valueOf(array[1])==3){
                                textNumber = "1";
                        }else if(i==3&&number>1){
                            textNumber = "1";
                        }else if(i==4 &&
                                number>2 &&
                                Integer.valueOf(array[3])==1){
                            textNumber = "2";
                        }else if(i==5&&(number>2||number<2)){
                            textNumber = "2";
                        }else if(i==6 &&
                                number>0 &&
                                Integer.valueOf(array[5])>1){
                            textNumber = "0";
                        }else if(i==9&&number>2){
                            textNumber = "2";
                        }else if(i==10 &&
                                number>4 &&
                                Integer.valueOf(array[9])==2){
                            textNumber = "4";
                        }else if(i==11&&number>6){
                            textNumber = "5";
                        }else if(i==13&&number>6){
                            textNumber = "5";
                        }
                    }catch (Exception e){
                        textNumber = " ";
                    }
                    textRes = textRes + textNumber;
                    if(i==2 || i==4){
                        textRes = textRes + ".";
                    }
                    if(i==8){
                        textRes = textRes + " ";
                    }
                    if(i==10){
                        textRes = textRes + ":";
                    }
                    if(i==12){
                        textRes = textRes + ":";
                    }
                    manually =true;
                }
                s.append(textRes);
                dateTime = stringToDate(textRes);
                dateET.addTextChangedListener(this);
            }
        });
    }

    private void initDateToView(){
        dateET.setText(dateFormatter.format(dateTime.getTime()));
    }

    private void showDateDialog(){

        Calendar date = dateTime;
        if(manually){
            date = stringToDate(dateET.getText().toString());
        }else if(date == null || date.get(Calendar.YEAR)<1000) {
            date = Calendar.getInstance();
        }

        new DatePickerDialog(getContext(),
                dateDialog,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimeDialog(){

        Calendar date = dateTime;
        if(date == null || date.getTime() == new Date(0)) {
            date = Calendar.getInstance();
        }

        new TimePickerDialog(getContext(),
                timedialog,
                date.get(Calendar.HOUR_OF_DAY),
                date.get(Calendar.MINUTE), true).show();
    }

    private Calendar stringToDate(String text){

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyy HH:mm:ss", Locale.getDefault());
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(text));
            return cal;
        } catch (ParseException e) {
            e.printStackTrace();
            Calendar cal = Calendar.getInstance();
            cal.set(0,0,0,0,0,0);
            return cal;
        }
    }

}
