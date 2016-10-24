package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aca.travelsafe.Dal.Scalar;
import com.aca.travelsafe.FillFlightDetailActivity;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.UtilView;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.SppaFlight;
import com.aca.travelsafe.database.SppaFlight_Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.LocalDate;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aca.travelsafe.Util.UtilView.isEmpty;

/**
 * Created by marsel on 11/4/2016.
 */
public class FillFlightDetailFragment extends BaseFragment {
    public FillFlightDetailFragmentListener mListener;
    @Bind(R.id.lblSubhead)
    TextView lblSubhead;
    @Bind(R.id.txtFlightNo)
    EditText txtFlightNo;
    @Bind(R.id.lblFlightNo)
    TextInputLayout lblFlightNo;
    @Bind(R.id.txtDepartureDate)
    EditText txtDepartureDate;
    @Bind(R.id.lblDepartureDate)
    TextInputLayout lblDepartureDate;
    @Bind(R.id.txtFrom)
    EditText txtFrom;
    @Bind(R.id.lblFrom)
    TextInputLayout lblFrom;
    @Bind(R.id.txtTo)
    EditText txtTo;
    @Bind(R.id.lblTo)
    TextInputLayout lblTo;
    @Bind(R.id.viewScroll)
    NestedScrollView viewScroll;
    @Bind(R.id.btnCancel)
    Button btnCancel;
    @Bind(R.id.btnOk)
    Button btnOk;
    @Bind(R.id.viewButtonFooter)
    LinearLayout viewButtonFooter;


    private DatePickerDialog dppDeparture;
    private SppaFlight sppaFlight;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public interface FillFlightDetailFragmentListener {
        void goBack();
    }

    public static FillFlightDetailFragment newInstance() {

        Bundle args = new Bundle();

        FillFlightDetailFragment fragment = new FillFlightDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public static Fragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(var.id, id);

        FillFlightDetailFragment fragment = new FillFlightDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fill_flight_detail, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getString(R.string.Flight_detail));

        if (getArguments().getString(var.id) != null) {
            String id = getArguments().getString(var.id);
            loadData(id);
        }

        return view;
    }


    @Override
    protected void init(View view) {
        bindDatePicker();
    }


    private void loadData(String id) {
        sppaFlight = new Select().from(SppaFlight.class)
                .where(SppaFlight_Table.id.eq(Integer.parseInt(id)))
                .querySingle();

        txtDepartureDate.setText(UtilDate.format(sppaFlight.FlightDate, UtilDate.ISO_DATE, UtilDate.BASIC_MON_DATE));
        txtFrom.setText(sppaFlight.FlightFrom);
        txtTo.setText(sppaFlight.FlightTo);
        txtFlightNo.setText(sppaFlight.FlightNo);

    }

    public boolean validate() {
        if (!validateEmptyField()) {
            Snackbar.make(getView(), R.string.message_validate_empty_field, Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (!validateCountryLength()) {
            Snackbar.make(getView(), R.string.message_validation_provide_information, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (!validateFlightDate()) {
            Snackbar.make(getView(), R.string.message_validate_invalid_flight_date, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean validateCountryLength() {
        return txtFrom.getText().length() >= 3 && txtTo.getText().length() >= 3;
    }

    public boolean validateEmptyField() {
        return (!isEmpty(txtFlightNo, lblFlightNo)
                && !isEmpty(txtDepartureDate, lblDepartureDate)
                && !isEmpty(txtFrom, lblFrom)
                && !isEmpty(txtTo, lblTo));
    }

    private boolean validateFlightDate() {
        LocalDate flightDate = UtilDate.toDate(txtDepartureDate.getText().toString());
        LocalDate expDate = UtilDate.toDate(Scalar.getExpiredDate());
        LocalDate effDate = UtilDate.toDate(Scalar.getBeginDate());

        if ((flightDate.isBefore(effDate) || flightDate.isAfter(expDate))) {
            return false;
        }
        return true;
    }



    public boolean saveSPPA() {
        if (!validate())
            return false;

        if (sppaFlight == null)
            sppaFlight = new SppaFlight();

        sppaFlight.FlightDate = UtilDate.format(txtDepartureDate.getText().toString(), UtilDate.ISO_DATE);
        sppaFlight.FlightNo = txtFlightNo.getText().toString();
        sppaFlight.FlightFrom = txtFrom.getText().toString();
        sppaFlight.FlightTo = txtTo.getText().toString();
        sppaFlight.save();

        return true;
    }


    private void bindDatePicker() {
        LocalDate beginDate = UtilDate.toDate(Scalar.getBeginDate());
        LocalDate expiredDate = UtilDate.toDate(Scalar.getExpiredDate());
        int year = beginDate.getYear();
        int month = beginDate.getMonthOfYear();
        int day = beginDate.getDayOfMonth();


        if (!txtDepartureDate.getText().toString().isEmpty()) {
            LocalDate depDate = UtilDate.toDate(txtDepartureDate.getText().toString());
            year = depDate.getYear();
            month = depDate.getMonthOfYear();
            day = depDate.getDayOfMonth();
        }

        dppDeparture = DatePickerDialog.newInstance(
                dppDepartureListener(),
                year,
                --month,
                day
        );
        Calendar maxCalendar = Calendar.getInstance();
        Calendar minCalendar = Calendar.getInstance();

        minCalendar.setTime(beginDate.toDate());
        maxCalendar.setTime(expiredDate.toDate());

//        dppDeparture.setSelectableDays(new Calendar[]{minCalendar, maxCalendar});
        dppDeparture.setMinDate(minCalendar);
        dppDeparture.setMaxDate(maxCalendar);
    }

    private DatePickerDialog.OnDateSetListener dppDepartureListener() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                try {
                    monthOfYear++;
                    String tanggal = new LocalDate(year, monthOfYear, dayOfMonth)
                            .toString(UtilDate.BASIC_MON_DATE);

                    txtDepartureDate.setText(tanggal);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
    }

    @OnClick(R.id.txtDepartureDate)
    public void pickDepartureDate() {
        dppDeparture.show(context.getFragmentManager(), "tag");
    }


    @OnClick(R.id.btnOk)
    public void performOK() {
        if (saveSPPA())
            mListener.goBack();
    }

    @OnClick(R.id.btnCancel)
    public void performCancel() {
        mListener.goBack();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FillFlightDetailFragmentListener) {
            this.context = (Activity) context;
            mListener = (FillFlightDetailFragmentListener) context;
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
    protected void registerListener() {
        txtFlightNo.addTextChangedListener(UtilView.inputTextWatcher(lblFlightNo));
        txtFrom.addTextChangedListener(UtilView.inputTextWatcher(lblFrom));
        txtTo.addTextChangedListener(UtilView.inputTextWatcher(lblTo));
        txtDepartureDate.addTextChangedListener(UtilView.inputTextWatcher(lblDepartureDate));
    }

}
