package com.aca.travelsafe.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aca.travelsafe.Dal.Policy;
import com.aca.travelsafe.FillInsuredActivity;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.SppaDestinationDraft;
import com.aca.travelsafe.database.SppaDomesticDraft;
import com.aca.travelsafe.database.SppaMainDraft;
import com.aca.travelsafe.database.SppaMainDraft_Table;
import com.aca.travelsafe.database.StandardField;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MyPurchaseAdapter extends RecyclerView.Adapter<MyPurchaseAdapter.ViewHolder> {
    public List<SppaMainDraft> sppaMainDraftList;
    public List<SppaDestinationDraft> sppaDestinationDraftList;
    public List<SppaDomesticDraft> sppaDomesticDraftList;

    public Activity activity;
    public View view;

    public int position;


    public MyPurchaseAdapter(Activity activity, View view) {
        this.activity = activity;
        this.view = view;

        sppaMainDraftList = new Select().from(SppaMainDraft.class).queryList();
        sppaDestinationDraftList = new Select().from(SppaDestinationDraft.class).queryList();
        sppaDomesticDraftList = new Select().from(SppaDomesticDraft.class).queryList();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_my_purchase, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final SppaMainDraft sppaMainDraft = sppaMainDraftList.get(position);

        try {
            String departureDate = UtilDate.format(sppaMainDraft.EffectiveDate, UtilDate.ISO_DATE, UtilDate.BASIC_MON_DATE);
            String arrivalDate = UtilDate.format(sppaMainDraft.ExpireDate, UtilDate.ISO_DATE, UtilDate.BASIC_MON_DATE);
            String sppaDate = UtilDate.format(sppaMainDraft.SppaDate, UtilDate.ISO_DATE, UtilDate.BASIC_MON_DATE);

            holder.sppaNo = sppaMainDraft.SppaNo;
            holder.txtNoPolis.setText(TextUtils.isEmpty(sppaMainDraft.PolicyNo) ? "" : sppaMainDraft.PolicyNo);
            holder.txtStatusPolis.setText(fillStatusPolis(sppaMainDraft.SppaStatus.toString()));
            holder.txtCoverage.setText(Policy.getCoverage(sppaMainDraft));
            holder.txtSppaDate.setText(sppaDate);
            holder.txtDepartureDate.setText(departureDate);
            holder.txtArrivalDate.setText(arrivalDate);
            holder.txtDestination.setText(Policy.getDestinationDraft(sppaMainDraft.SppaNo));


            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyPurchaseAdapter.this.position = position;

//                    GeneralSetting.insert(var.GENERAL_SETTING_IS_POLIS_ACTIVITY, String.valueOf(true));
                    Intent intent = new Intent(activity, FillInsuredActivity.class);
//                    Intent intent = new Intent(activity, MyPolisActivity.class);
                    intent.putExtra(var.SPPA_NO, holder.sppaNo);

                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(activity, holder.cardView, activity.getString(R.string.polis));
                    activity.startActivity(intent, options.toBundle());
//                    activity.startActivity(intent);


//                    SaveDraft saveDraft = new SaveDraft(holder.sppaNo, activity);
//                    saveDraft.saveAll();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String fillStatusPolis(String sppaStatus) {
        List<StandardField> standardFieldList = StandardField.getList(var.StatusPolis);
        String fieldName;
        boolean flag = false;
        for (StandardField s : standardFieldList) {
            fieldName = s.FieldNameDt;
            if (sppaStatus.equals(fieldName)) {
                flag = true;
            }
        }

        if (!flag)
            return "Status: " + sppaStatus;
        else
            return "";
    }

    public static PolicyHeaderHolder getPolicyHeader(String SppaNo) {
        SppaMainDraft sppaMainDraft = new Select()
                .from(SppaMainDraft.class)
                .where(SppaMainDraft_Table.SppaNo.eq(SppaNo))
                .querySingle();

        if (sppaMainDraft == null)
            return null;

        String departureDate = UtilDate.format(sppaMainDraft.EffectiveDate, UtilDate.ISO_DATE, UtilDate.BASIC_MON_DATE);
        String arrivalDate = UtilDate.format(sppaMainDraft.ExpireDate, UtilDate.ISO_DATE, UtilDate.BASIC_MON_DATE);
        String sppaDate = UtilDate.format(sppaMainDraft.SppaDate, UtilDate.ISO_DATE, UtilDate.BASIC_MON_DATE);

        PolicyHeaderHolder holder = new PolicyHeaderHolder();
        holder.sppaNo = sppaMainDraft.SppaNo;
        holder.txtNoPolis = (TextUtils.isEmpty(sppaMainDraft.PolicyNo) ? "" : sppaMainDraft.PolicyNo);
        holder.txtStatusPolis = fillStatusPolis(sppaMainDraft.SppaStatus);
                holder.txtCoverage = (Policy.getCoverage(sppaMainDraft));
        holder.txtSppaDate = (sppaDate);
        holder.txtDepartureDate = (departureDate);
        holder.txtArrivalDate = (arrivalDate);
        holder.txtDestination = (Policy.getDestinationDraft(sppaMainDraft.SppaNo));

        return holder;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return sppaMainDraftList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        String sppaNo;

        @Bind(R.id.lblPolicyNo)
        TextView lblPolicyNo;
        @Bind(R.id.txtNoPolis)
        TextView txtNoPolis;
        @Bind(R.id.txtCoverage)
        TextView txtCoverage;
        @Bind(R.id.imgCalendar)
        ImageView imgCalendar;
        @Bind(R.id.txtDepartureDate)
        TextView txtDepartureDate;
        @Bind(R.id.lblUntil)
        TextView lblUntil;
        @Bind(R.id.txtArrivalDate)
        TextView txtArrivalDate;
        @Bind(R.id.imgPlane)
        ImageView imgPlane;
        @Bind(R.id.txtDestination)
        TextView txtDestination;
        @Bind(R.id.txtSppaDate)
        TextView txtSppaDate;
        @Bind(R.id.txtStatusPolis)
        TextView txtStatusPolis;
        @Bind(R.id.cardView)
        CardView cardView;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }

    public static class PolicyHeaderHolder {
        public String sppaNo;
        public String txtNoPolis;
        public String txtStatusPolis;
        public String txtCoverage;
        public String txtDepartureDate;
        public String txtArrivalDate;
        public String txtDestination;
        public String txtSppaDate;
    }


}
