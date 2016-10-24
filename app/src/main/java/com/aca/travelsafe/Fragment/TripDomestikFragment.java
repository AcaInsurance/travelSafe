package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.aca.travelsafe.Binding.CityBind;
import com.aca.travelsafe.Binding.PlanBind;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Util.UtilImage;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.SppaDomestic;
import com.aca.travelsafe.database.SppaMain;
import com.raizlabs.android.dbflow.sql.language.Select;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TripDomestikFragment extends BaseFragment {

    @Bind(R.id.spnCity)
    Spinner spnCity;

    @Bind(R.id.spnPlan)
    Spinner spnPlan;
    @Bind(R.id.viewParent)
    LinearLayout viewParent;
    @Bind(R.id.imgPlanDescription)
    ImageView imgPlanDescription;

    private OnFragmentInteractionListener mListener;

    public TripDomestikFragment() {
    }

    public static TripDomestikFragment newInstance() {
        TripDomestikFragment fragment = new TripDomestikFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_domestik, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        loadSPPA();
    }

    public void disableView() {
        disable(viewParent);
    }

    public void loadSPPA() {
        bindPickedPlan();
        bindPickedCity();
    }

    public boolean saveSPPA() {
        try {
            SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();
            SppaDomestic sppaDomestic = new Select().from(SppaDomestic.class).querySingle();

            if (sppaMain == null)
                sppaMain = new SppaMain();

            if (sppaDomestic == null)
                sppaDomestic = new SppaDomestic();


            sppaMain.PlanCode = PlanBind.getID(spnPlan);
            sppaMain.ZoneId = var.ASIA;
            sppaMain.save();

            sppaDomestic.CityId = CityBind.getID(spnCity);
            sppaDomestic.save();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            this.context = (Activity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    protected void init(View view) {
        CityBind.bind(context, spnCity);
        PlanBind.bind(context, spnPlan);
    }


    private void bindPickedPlan() {
        String planCode = new Select().from(SppaMain.class).querySingle().PlanCode;
        if (TextUtils.isEmpty(planCode))
            return;

        PlanBind.select(spnPlan, planCode);
    }

    private void bindPickedCity() {
        SppaDomestic sppaDomestic = new Select().from(SppaDomestic.class).querySingle();

        if (sppaDomestic == null)
            return;

        if (TextUtils.isEmpty(sppaDomestic.CityId))
            return;

        CityBind.select(spnCity, sppaDomestic.CityId);
    }


    @Override
    protected void registerListener() {
        spnPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
//                    if (view == null)
//                        return;
//                    String plan = ((TextView) view).getText().toString().toLowerCase().replace(" ", "_");
                    String plan = PlanBind.getID(spnPlan);
                    UtilImage.setImgPlanDescription(viewParent, context, imgPlanDescription, plan);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
