package com.aca.travelsafe.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aca.travelsafe.Dal.Policy;
import com.aca.travelsafe.FillFlightDetailActivity;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.Utility;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.Widget.MyTextDrawable;
import com.aca.travelsafe.database.SppaFlight;
import com.aca.travelsafe.database.SppaFlight_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PickedFlightAdapter extends RecyclerView.Adapter<PickedFlightAdapter.ViewHolder> {
    public List<SppaFlight> arrList;
    public Context context;
    public Activity activity;

    private boolean disable;

    public PickedFlightAdapter(Activity activity) {
        this.activity = activity;
        this.arrList = new Select().from(SppaFlight.class).queryList();
        disable = Policy.isPaid();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_flight_detail, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            final SppaFlight sppaFlight = arrList.get(position);

            String cdate = UtilDate.format(sppaFlight.FlightDate, UtilDate.ISO_DATE, UtilDate.BASIC_MON_DATE);

            holder.id = sppaFlight.id;
            holder.txtFlightNo.setText(sppaFlight.FlightNo);
            holder.txtFrom.setText(sppaFlight.FlightFrom.substring(0, 3));
            holder.txtTo.setText(sppaFlight.FlightTo.substring(0, 3));
            holder.txtTanggal.setText(cdate);
            holder.imgNumber.setImageDrawable(MyTextDrawable.create(activity, String.valueOf(position + 1)));
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SppaFlight sf = new Select().from(SppaFlight.class)
                            .where(SppaFlight_Table.id.eq(holder.id)).querySingle();

                    if (sf != null) {
                        sf.delete();
                        arrList.remove(position);
                        notifyItemRemoved(position);
                        notifyDataSetChanged();
                    }


                }
            });
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, FillFlightDetailActivity.class);
                    intent.putExtra(var.id, holder.id);
                    activity.startActivity(intent);
                }
            });

            if (disable)
                Utility.disable(holder.viewParent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return arrList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        View view;

        @Bind(R.id.imgNumber)
        ImageView imgNumber;
        @Bind(R.id.txtFlightNo)
        TextView txtFlightNo;
        @Bind(R.id.txtTanggal)
        TextView txtTanggal;
        @Bind(R.id.btnDelete)
        FrameLayout btnDelete;
        @Bind(R.id.imgStepper)
        ImageView imgStepper;
        @Bind(R.id.txtFrom)
        TextView txtFrom;
        @Bind(R.id.txtTo)
        TextView txtTo;
        @Bind(R.id.viewParent)
        RelativeLayout viewParent;

        int id;


        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }


}
