package com.aca.travelsafe.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.aca.travelsafe.R;
import com.aca.travelsafe.database.Country;
import com.aca.travelsafe.database.Country_Table;
import com.aca.travelsafe.database.SppaDestination;
import com.aca.travelsafe.database.SppaMain;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {
    public List<Country> countryList;
    public List<ViewHolderEvent> clickedList;
    public Context context;
    public Activity activity;


    public CountryAdapter(Activity activity, List<Country> countries) {
        this.activity = activity;
        this.countryList = countries;
        initClickedList();

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_country, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        loadSPPA();

        return viewHolder;

    }

    private void initClickedList() {
        ViewHolderEvent viewHolderEvent;
        clickedList = new ArrayList<>();

        for (Country  c: countryList) {
            viewHolderEvent = new ViewHolderEvent(c.CountryId, false);
            clickedList.add(viewHolderEvent);
        }
    }


    private boolean getStateClicked (String countryId){
        for (ViewHolderEvent v: clickedList) {
            if (v.id.equals(countryId))
                return v.clicked;
        }
        return false;
    }

    private void setStateClicked (String countryId){
        for (ViewHolderEvent v: clickedList) {
            if (v.id.equals(countryId)) {
                v.clicked = !v.clicked;
            }
        }
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            final Country country = countryList.get(position);
            holder.cbCountry.setChecked(getStateClicked(country.CountryId));
            holder.cbCountry.setText(country.CountryName);
            holder.cbCountry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.cbCountry.setChecked(!holder.cbCountry.isChecked());
                    setStateClicked(country.CountryId);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<Country> getPickedList() {
        List<Country> countries = new ArrayList<>();

        for (ViewHolderEvent v : clickedList) {
            if (v.clicked) {
                Country country = new Select().from(Country.class)
                        .where(Country_Table.CountryId.eq(v.id))
                        .querySingle();
                countries.add(country);
            }
        }
        return countries;
    }

    public void loadSPPA() {
        List<SppaDestination> sppaDestinations = new Select().from(SppaDestination.class).queryList();

        for (ViewHolderEvent v: clickedList) {
            for (SppaDestination sd : sppaDestinations) {
                if (sd.CountryId.equals(v.id)) {
                    v.clicked = true;
                }
            }
        }
    }

    public void saveSPPA() {
        Delete.table(SppaDestination.class);

        for (ViewHolderEvent v : clickedList) {
            if (v.clicked) {
                saveSppaMain(v.id);

                SppaDestination sppaDestination = new SppaDestination();
                sppaDestination.CountryId = v.id;
                sppaDestination.save();
            }
        }
    }

    private void saveSppaMain(String id) {
        String zoneId = new Select().from(Country.class)
                .where(Country_Table.CountryId.eq(id))
                .querySingle()
                .ZoneId;

        SppaMain sppaMain = new Select().from(SppaMain.class)
                .querySingle();

        String tempZoneId = sppaMain.ZoneId;

        if (TextUtils.isEmpty(tempZoneId)) {
            sppaMain.ZoneId = zoneId;
        } else {
            if (Integer.parseInt(zoneId) > Integer.parseInt(tempZoneId))
                sppaMain.ZoneId = zoneId;
        }

        sppaMain.save();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return countryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View view;

        @Bind(R.id.cbCountry)
        CheckedTextView cbCountry;

        ViewHolder(View view) {
            super(view);
            this.view = view;

            ButterKnife.bind(this, view);
        }
    }


    public static class ViewHolderEvent {
        boolean clicked;
        String id;


        ViewHolderEvent(String countryId, boolean clicked) {
            id = countryId;
            this.clicked = clicked;
        }
    }

}
