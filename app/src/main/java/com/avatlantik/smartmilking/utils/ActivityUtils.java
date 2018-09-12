package com.avatlantik.smartmilking.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avatlantik.smartmilking.R;

import java.util.Date;
import java.util.List;

public class ActivityUtils {

    public ActivityUtils() {
    }

    public int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int)(dp * (displayMetrics.densityDpi / 160f));
    }

    public void showMessage(String textMessage, Context mContext) {
        if (textMessage == null || textMessage.isEmpty()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.DialogTheme);
        builder.setTitle(mContext.getString(R.string.questions_title_info));
        builder.setMessage(textMessage);

        builder.setNeutralButton(mContext.getString(R.string.questions_answer_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        // TODO (start stub): to set size text in AlertDialog
        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        textView.setTextSize(mContext.getResources().getDimension(R.dimen.text_size_middle));
        Button button1 = (Button) alert.findViewById(android.R.id.button1);
        button1.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        button1.setTextSize(mContext.getResources().getDimension(R.dimen.text_size_middle));
        Button button2 = (Button) alert.findViewById(android.R.id.button2);
        button2.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        button2.setTextSize(mContext.getResources().getDimension(R.dimen.text_size_middle));
        Button button3 = (Button) alert.findViewById(android.R.id.button3);
        button3.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        button3.setTextSize(mContext.getResources().getDimension(R.dimen.text_size_middle));
        // TODO: (end stub) ------------------
    }

    public void showShortToast(Context mContext, String message){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(Context mContext, String message){
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    public void showQuestion(Context mContext, String textTitle, String textMessage, final QuestionAnswer questionAnswer) {
        if (textMessage == null || textMessage.isEmpty()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.DialogTheme);
        builder.setTitle(textTitle != null && !textTitle.isEmpty() ? textTitle : mContext.getString(R.string.questions_title_info));
        builder.setMessage(textMessage);

        builder.setPositiveButton(mContext.getString(R.string.questions_answer_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                questionAnswer.onPositiveAnsver();
            }
        });

        builder.setNegativeButton(mContext.getString(R.string.questions_answer_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                questionAnswer.onNegativeAnsver();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                questionAnswer.onNegativeAnsver();
            }
        });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
//        alert.setCancelable(false);
        alert.show();

        // TODO (start stub): to set size text in AlertDialog
        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        textView.setTextSize(mContext.getResources().getDimension(R.dimen.text_size_middle));
        Button button1 = (Button) alert.findViewById(android.R.id.button1);
        button1.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        button1.setTextSize(mContext.getResources().getDimension(R.dimen.text_size_middle));
        Button button2 = (Button) alert.findViewById(android.R.id.button2);
        button2.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        button2.setTextSize(mContext.getResources().getDimension(R.dimen.text_size_middle));
        Button button3 = (Button) alert.findViewById(android.R.id.button3);
        button3.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        button3.setTextSize(mContext.getResources().getDimension(R.dimen.text_size_middle));
        // TODO: (end stub) ------------------
    }

    public interface QuestionAnswer {

        void onPositiveAnsver();

        void onNegativeAnsver();

        void onNeutralAnsver();

    }

    public void hideKeyboard(Context context){
        ((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    public void showSelectionList(Activity mActivity, String textTitle, final List<String> listString, final ListItemClick listItemClick) {
        if (listString == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.DialogTheme);
        builder.setTitle(textTitle != null && !textTitle.isEmpty() ? textTitle : mActivity.getString(R.string.questions_title_info));
        builder.setAdapter(new ArrayAdapter<String>(mActivity,
                        R.layout.row_item, R.id.textItem, listString),
                                               new DialogInterface.OnClickListener() {
                                                   @Override
                                                   public void onClick(DialogInterface dialog, int which) {
                                                       listItemClick.onItemClik(which, listString.get(which));
                                                   }
                                               });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public interface ListItemClick {

        void onItemClik(int item, String text);
    }

    public interface ListGridItemClick {

        void onItemClik(int item);
    }

    public void showDateTimeSelect(Context context, Date date, final ListSelectDate listSelectDate) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setTitle("");
        final View viewInflated = LayoutInflater.from(context).inflate(R.layout.date_time_picket, null);


        builder.setView(viewInflated);

        builder.setPositiveButton(context.getResources().getString(R.string.questions_answer_ok),
                (dialog, which) -> {


                });

        builder.setNegativeButton(context.getResources().getString(R.string.questions_answer_cancel),
                (dialog, which) ->{});
        builder.setCancelable(true);

        AlertDialog alertQuestion = builder.create();
        alertQuestion.show();
        alertQuestion.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
     //   alertQuestion.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorAccent));
//        alertQuestion.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((v) ->{
//
//        });
    }

    public interface ListSelectDate {

        void onDateTimeSet(Date date) ;

        void onDateTimeCancel();
    }


}
