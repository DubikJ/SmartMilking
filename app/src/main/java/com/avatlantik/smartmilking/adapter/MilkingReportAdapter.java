package com.avatlantik.smartmilking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avatlantik.smartmilking.R;
import com.avatlantik.smartmilking.model.db.MilkingRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MilkingReportAdapter extends RecyclerView.Adapter{

    private List<MilkingRecord> list;
    private LayoutInflater layoutInflater;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yy hh:mm");;

    public MilkingReportAdapter(Context context, List<MilkingRecord> list) {
        this.list = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class MilkingViewHolder extends RecyclerView.ViewHolder {

        TextView reportDate, reportPlace, reportMachine, reportCow, reportLitres;

        public MilkingViewHolder(View itemView) {
            super(itemView);
            this.reportDate = (TextView) itemView.findViewById(R.id.report_date);
            this.reportPlace = (TextView) itemView.findViewById(R.id.report_place);
            this.reportMachine = (TextView) itemView.findViewById(R.id.report_machine);
            this.reportCow = (TextView) itemView.findViewById(R.id.report_cow);
            this.reportLitres = (TextView) itemView.findViewById(R.id.report_litres);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_report_table, parent, false);
        return new MilkingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MilkingRecord selectedItem = getMilking(position);
        if (selectedItem != null) {
            MilkingViewHolder vHolder = ((MilkingViewHolder) holder);

            vHolder.reportDate.setText(dateFormatter.format(new Date(selectedItem.getDate())));

            vHolder.reportPlace.setText(String.valueOf(selectedItem.getIdPlace()));

            vHolder.reportMachine.setText(String.valueOf(selectedItem.getIdMachine()));

            vHolder.reportCow.setText(String.valueOf(selectedItem.getIdCow()));

            vHolder.reportLitres.setText(String.valueOf(selectedItem.getLitres()));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private MilkingRecord getMilking(int position) {
        return (MilkingRecord) list.get(position);
    }

}
