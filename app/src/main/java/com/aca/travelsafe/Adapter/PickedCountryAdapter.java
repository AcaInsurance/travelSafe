package com.aca.travelsafe.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aca.travelsafe.Dal.Policy;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Util.Utility;
import com.aca.travelsafe.Widget.MyTextDrawable;
import com.aca.travelsafe.database.Country;
import com.aca.travelsafe.database.Country_Table;
import com.aca.travelsafe.database.SppaDestination;
import com.aca.travelsafe.database.SppaDestination_Table;
import com.aca.travelsafe.database.SppaMain;
import com.aca.travelsafe.database.Zone;
import com.aca.travelsafe.database.Zone_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PickedCountryAdapter extends RecyclerView.Adapter<PickedCountryAdapter.ViewHolder> {
    public List<Country> arrList;
    public Context context;
    public Activity activity;
    public boolean disable;


    public PickedCountryAdapter(Activity activity) {
        this.activity = activity;
        this.arrList = new ArrayList<>();

        List<SppaDestination> sppaDestinations = new Select().from(SppaDestination.class).queryList();
        for (SppaDestination sd : sppaDestinations) {
            Country country = new Select().from(Country.class).where(Country_Table.CountryId.eq(sd.CountryId)).querySingle();
            arrList.add(country);
        }

        disable = Policy.isPaid();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_country_detail, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Country country = arrList.get(position);
        holder.countryId = country.CountryId;
        holder.txtCountry.setText(country.CountryName);
        holder.txtTypeCountry.setText(getTypeCountry(country.ZoneId));
        holder.imgLetter.setImageDrawable(MyTextDrawable.create(activity, String.valueOf(country.CountryName.charAt(0))));
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    arrList.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();

                    SppaDestination sppaDestination = new Select().from(SppaDestination.class)
                            .where(SppaDestination_Table.CountryId.eq(holder.countryId))
                            .querySingle();

                    if (sppaDestination != null) {
                        sppaDestination.delete();
                        updateSppaMain();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (disable)
            Utility.disable(holder.viewParent);
    }

    private void updateSppaMain() {
        List<SppaDestination> sppaDestinationList= new Select().from(SppaDestination.class).queryList();
        int zone = 0;
        for (SppaDestination s: sppaDestinationList) {
            Country country = new Select().from(Country.class)
                    .where(Country_Table.CountryId .eq(s.CountryId))
                    .querySingle();

            if (Integer.parseInt(country.ZoneId) > zone)
                zone = Integer.parseInt(country.ZoneId);
        }
        SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();
        sppaMain.ZoneId = String.valueOf(zone);
        sppaMain.save();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return arrList.size();
    }

    public String getTypeCountry(String zoneId) {
        Zone zone = new Select().from(Zone.class).where(Zone_Table.ZoneId.eq(zoneId)).querySingle();
        return zone.ZoneName;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        View view;

        @Bind(R.id.imgLetter)
        ImageView imgLetter;
        @Bind(R.id.txtCountry)
        TextView txtCountry;
        @Bind(R.id.txtTypeCountry)
        TextView txtTypeCountry;
        @Bind(R.id.btnRemove)
        FrameLayout btnRemove;
        @Bind(R.id.viewParent)
        RelativeLayout viewParent;

        String countryId;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }


}
