package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.aca.travelsafe.Adapter.PickedCountryAdapter;
import com.aca.travelsafe.Adapter.PickedFlightAdapter;
import com.aca.travelsafe.Binding.PlanBind;
import com.aca.travelsafe.ChooseCountryActivity;
import com.aca.travelsafe.Dal.Scalar;
import com.aca.travelsafe.FillFlightDetailActivity;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.UtilImage;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.Widget.RecyclerViewWidget;
import com.aca.travelsafe.database.SppaFlight;
import com.aca.travelsafe.database.SppaMain;
import com.aca.travelsafe.database.SubProduct;
import com.aca.travelsafe.database.SubProduct_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.joda.time.LocalDate;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TripRegularFragment extends BaseFragment {

    @Bind(R.id.spnPlan)
    Spinner spnPlan;
    @Bind(R.id.btnDestination)
    Button btnDestination;
    @Bind(R.id.listNegara)
    RecyclerView listNegara;
    @Bind(R.id.btnFlightDetail)
    Button btnFlightDetail;
    @Bind(R.id.listFlightDetail)
    RecyclerView listFlightDetail;
    @Bind(R.id.viewParent)
    LinearLayout viewParent;
    @Bind(R.id.imgPlanDescription)
    ImageView imgPlanDescription;

    private OnFragmentInteractionListener mListener;

    public TripRegularFragment() {
    }

    public static TripRegularFragment newInstance() {
        TripRegularFragment fragment = new TripRegularFragment();
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
        View view = inflater.inflate(R.layout.fragment_trip_regular, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        loadSPPA();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            this.context = (Activity) context;
            mListener = (OnFragmentInteractionListener) context;
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


    public void disableView() {
        disable(viewParent);
    }

    @Override
    protected void init(View view) {
        PlanBind.bind(context, spnPlan);
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

    public void loadSPPA() {
        bindPickedPlan();
        bindPickedCountry();
        bindFlight();
    }

    public boolean saveSPPA() {
        try {
            if (!validate())
                return false;


            SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();

            if (sppaMain == null)
                sppaMain = new SppaMain();

            sppaMain.PlanCode = PlanBind.getID(spnPlan);
            sppaMain.save();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean validate() {
        if (!validateDestination()) return false;
        if (!validateFlight()) return false;
        if (!validateFlightDate()) return false;

        return true;
    }

    private boolean validateFlight() {
        SubProduct subProduct = null;
        String subProductCode;

        try {
            subProductCode = Scalar.getSubProductCode();
            subProduct = new Select().from(SubProduct.class)
                    .where(SubProduct_Table.SubProductCode.eq(subProductCode))
                    .querySingle();

            if (subProduct == null) {
                return true;
            }
            if (Boolean.parseBoolean(subProduct.IsNeedFlight)) {
                if (listFlightDetail.getAdapter().getItemCount() == 0) {
                    Snackbar.make(getView(), R.string.message_validate_flight, Snackbar.LENGTH_SHORT).show();
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private boolean validateFlightDate() {
        List<SppaFlight> flightList = new Select().from(SppaFlight.class).queryList();

        LocalDate flightDate;
        int id;

        LocalDate expDate = UtilDate.toDate(Scalar.getExpiredDate());
        LocalDate effDate = UtilDate.toDate(Scalar.getBeginDate());
        for (SppaFlight s : flightList) {
            flightDate = UtilDate.toDate(s.FlightDate, UtilDate.ISO_DATE);
            id = s.id;
            if ((flightDate.isBefore(effDate) || flightDate.isAfter(expDate))) {
                createToast(context.getString(R.string.message_validate_invalid_flight_date));
                Intent intent = new Intent(context, FillFlightDetailActivity.class);
                intent.putExtra(var.id, id);
                context.startActivity(intent);
                return false;
            }
        }
        return true;
    }

    private boolean validateDestination() {
        if (listNegara.getAdapter().getItemCount() == 0) {
            Snackbar.make(getView(), R.string.message_validate_destination, Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @OnClick(R.id.btnDestination)
    public void pickDestination() {
        Intent intent = new Intent(context, ChooseCountryActivity.class);
        startActivityForResult(intent, var.REQUEST_CODE_CHOOSE_COUNTRY);
    }

    @OnClick(R.id.btnFlightDetail)
    public void addFlightDetail() {
        Intent intent = new Intent(context, FillFlightDetailActivity.class);
        startActivityForResult(intent, var.REQUEST_CODE_ADD_FLIGHT_DETAIL);
    }

    private void bindPickedCountry() {
        try {
            PickedCountryAdapter adapter = new PickedCountryAdapter(context);
            RecyclerViewWidget.create(context, listNegara);
            listNegara.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindFlight() {
        try {
            PickedFlightAdapter adapter = new PickedFlightAdapter(context);
            RecyclerViewWidget.create(context, listFlightDetail);
            listFlightDetail.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindPickedPlan() {
        String planCode = new Select().from(SppaMain.class).querySingle().PlanCode;
        if (TextUtils.isEmpty(planCode))
            return;

        PlanBind.select(spnPlan, planCode);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
