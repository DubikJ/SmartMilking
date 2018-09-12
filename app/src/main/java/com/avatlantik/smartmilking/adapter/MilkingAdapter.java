package com.avatlantik.smartmilking.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avatlantik.smartmilking.R;
import com.avatlantik.smartmilking.activity.MilkingActivity;
import com.avatlantik.smartmilking.model.db.Milking;
import com.avatlantik.smartmilking.ui.LoadMilkView;

import java.util.List;

public class MilkingAdapter extends RecyclerView.Adapter{

    private List<Milking> list;
    private Context context;
    private LayoutInflater layoutInflater;
    private int orientationDisplay;

    public MilkingAdapter(Context context, List<Milking> list, int orientationDisplay) {
        this.context = context;
        this.list = list;
        this.orientationDisplay = orientationDisplay;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class MilkingViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;

        public MilkingViewHolder(View itemView) {
            super(itemView);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(orientationDisplay == Configuration.ORIENTATION_LANDSCAPE){
            view = layoutInflater.inflate(R.layout.item_milking_landskape, parent, false);
        }else {
            view = layoutInflater.inflate(R.layout.item_milking_portrait, parent, false);
        }
        return new MilkingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Milking selectedItem = getMilking(position);
        if (selectedItem != null) {
            fillData(((MilkingViewHolder) holder).cardView, selectedItem);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private Milking getMilking(int position) {
        return (Milking) list.get(position);
    }

    public View fillData(final View view, final Milking selectedItem) {


        TextView name = (TextView) view.findViewById(R.id.name_machine);
        name.setText(String.valueOf(selectedItem.getId()));
        name.setTextColor(selectedItem.getIdMachine()==0 ?
                    context.getResources().getColor(R.color.colorGrey):
                    context.getResources().getColor(R.color.colorAccent));

        final ImageView imageMaschine = (ImageView) view.findViewById(R.id.image_maschine);
        imageMaschine.setImageDrawable(selectedItem.getIdMachine()==0 ?
                context.getResources().getDrawable(R.drawable.ic_milkmashine_false):
                context.getResources().getDrawable(R.drawable.ic_milkmashine));

        RelativeLayout layoutMachine = (RelativeLayout) view.findViewById(R.id.layout_machine);
        layoutMachine.setOnClickListener(v -> {
            ((MilkingActivity)context).callScan(selectedItem, true);
        });

        FrameLayout layoutCow = (FrameLayout) view.findViewById(R.id.layout_cow);
        layoutCow.setOnClickListener(v -> {
            ((MilkingActivity)context).callScan(selectedItem, false);
        });
        ImageView imageCow = (ImageView) view.findViewById(R.id.image_cow);
        TextView idCow = (TextView) view.findViewById(R.id.id_cow);
        if(selectedItem.getIdCow()==0) {
            imageCow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cow_false));
            idCow.setText("");
        }else{
            imageCow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cow));
            idCow.setText(String.valueOf(selectedItem.getIdCow()));
        }

        FrameLayout milkingContainer = (FrameLayout) view.findViewById(R.id.milking_container);
        milkingContainer.setOnClickListener((v)->{
            ((MilkingActivity)context).cancelMilkingn(selectedItem);
        });

        LoadMilkView imageLoad = (LoadMilkView) view.findViewById(R.id.image_load);
        imageLoad.setVisibility(selectedItem.isMilkingStart()? View.VISIBLE : View.GONE);

        ImageView imageCan = (ImageView) view.findViewById(R.id.image_can);
        TextView litres = (TextView) view.findViewById(R.id.litres);

        if(selectedItem.isMilkingEnd()){
            imageCan.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_can_milk));
            litres.setText(String.valueOf(selectedItem.getLitres())
                    +context.getString(R.string.litres_short));
            litres.setTextColor(context.getResources().getColor(R.color.colorWhite));
        }else{
            imageCan.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_can_empty));
            litres.setText(String.valueOf(selectedItem.getLitres())
                    +context.getString(R.string.litres_short));
            litres.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }

        return view;
    }
}
