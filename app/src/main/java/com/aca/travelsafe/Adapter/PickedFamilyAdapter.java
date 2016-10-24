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
import com.aca.travelsafe.FillFamilyActivity;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.Utility;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.Widget.MyTextDrawable;
import com.aca.travelsafe.database.FamilyRelation;
import com.aca.travelsafe.database.FamilyRelation_Table;
import com.aca.travelsafe.database.SppaFamily;
import com.aca.travelsafe.database.SppaFamily_Table;
import com.aca.travelsafe.database.StandardField;
import com.aca.travelsafe.database.StandardField_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PickedFamilyAdapter extends RecyclerView.Adapter<PickedFamilyAdapter.ViewHolder> {
    public List<SppaFamily> arrList;
    public Context context;
    public Activity activity;

    private boolean disable;


    public PickedFamilyAdapter(Activity activity) {
        this.activity = activity;
        this.arrList = new Select().from(SppaFamily.class)
                .where(SppaFamily_Table.FamilyCode.notEq(var.TertanggungUtama))
                .queryList();
        disable = Policy.isPaid();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_family_member, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            final SppaFamily sppaFamily = arrList.get(position);
            holder.id = sppaFamily.id;

            holder.txtNama.setText(sppaFamily.Name);
            holder.txtDOB.setText(sppaFamily.DOB.toString());
            holder.txtFamilyRelation.setText(getFamilyRelation(sppaFamily.FamilyCode));
            holder.txtCitizen.setText(getCitizenDescription(sppaFamily.CitizenshipId));
            holder.txtPassportNo.setText(sppaFamily.PassportNo);
            holder.imgNumber.setImageDrawable(MyTextDrawable.create(activity, position + 1 + ""));

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SppaFamily sf = new Select()
                                .from(SppaFamily.class)
                                .where(SppaFamily_Table.id.eq(holder.id))
                                .querySingle();

                        if (sf != null) {
                            sf.delete();

                            notifyItemRemoved(arrList.indexOf(sppaFamily));
                            arrList.remove(sppaFamily);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, FillFamilyActivity.class);
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

    private String getCitizenDescription(String citizenshipId) {
        return new Select().from(StandardField.class)
                .where(StandardField_Table.FieldCodeDt.eq(citizenshipId))
                .querySingle()
                .FieldNameDt
                ;
    }

    private String getFamilyRelation(String familyCode) {
        return new Select().from(FamilyRelation.class)
                .where(FamilyRelation_Table.FamilyCode.eq(familyCode))
                .querySingle()
                .Description;
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
        int id;

        @Bind(R.id.imgNumber)
        ImageView imgNumber;
        @Bind(R.id.txtNama)
        TextView txtNama;
        @Bind(R.id.txtFamilyRelation)
        TextView txtFamilyRelation;
        @Bind(R.id.txtCitizen)
        TextView txtCitizen;
        @Bind(R.id.txtDOB)
        TextView txtDOB;
        @Bind(R.id.txtPassportNo)
        TextView txtPassportNo;
        @Bind(R.id.btnDelete)
        FrameLayout btnDelete;
        @Bind(R.id.viewParent)
        RelativeLayout viewParent;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }


}
