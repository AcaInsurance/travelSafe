package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aca.travelsafe.BaseActivity;
import com.aca.travelsafe.Binding.CoverageBind;
import com.aca.travelsafe.Binding.TypeBind;
import com.aca.travelsafe.Dal.LoginDal;
import com.aca.travelsafe.Dal.Scalar;
import com.aca.travelsafe.DashboardActivity;
import com.aca.travelsafe.FillInsuredActivity;
import com.aca.travelsafe.Interface.DrawerListener;
import com.aca.travelsafe.Interface.LoginListener;
import com.aca.travelsafe.R;
import com.aca.travelsafe.SignInActivity;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.GeneralSetting;
import com.aca.travelsafe.database.Product;
import com.aca.travelsafe.database.Product_Table;
import com.aca.travelsafe.database.SppaBeneficiary;
import com.aca.travelsafe.database.SppaDestination;
import com.aca.travelsafe.database.SppaDomestic;
import com.aca.travelsafe.database.SppaFamily;
import com.aca.travelsafe.database.SppaFlight;
import com.aca.travelsafe.database.SppaInsured;
import com.aca.travelsafe.database.SppaMain;
import com.aca.travelsafe.database.SubProduct;
import com.aca.travelsafe.database.SubProduct_Table;
import com.aca.travelsafe.database.User;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.LocalDate;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class DashboardFragment extends BaseFragment implements
        LoginListener {

    @Bind(R.id.lblCaption)
    TextView lblCaption;
    @Bind(R.id.spnType)
    AppCompatSpinner spnType;
    @Bind(R.id.laySpnType)
    LinearLayout laySpnType;
    @Bind(R.id.spnCoverage)
    AppCompatSpinner spnCoverage;
    @Bind(R.id.laySpnCoverage)
    LinearLayout laySpnCoverage;
    @Bind(R.id.btnDeparture)
    TextView btnDeparture;
    @Bind(R.id.btnReturn)
    TextView btnReturn;
    @Bind(R.id.swiAnnual)
    SwitchCompat swiAnnual;
    @Bind(R.id.btnGetQuote)
    Button btnGetQuote;
    @Bind(R.id.layFrom)
    LinearLayout layFrom;
    @Bind(R.id.layTo)
    LinearLayout layTo;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    private DatePickerDialog dppTo;
    private DatePickerDialog dppFrom;
    private DrawerListener mListener;
    private AnnualHolder holder;

    private class AnnualHolder {
        boolean isAnnual;
        String returnDate, typeId;

        public AnnualHolder() {
            isAnnual = false;
            returnDate = "";
            typeId = null;

        }
    }

    public static DashboardFragment newInstance() {
        Bundle args = new Bundle();

        DashboardFragment fragment = new DashboardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        if (GeneralSetting.getParameterValue(var.GENERAL_SETTING_EDIT_CONFIRMATION).equalsIgnoreCase(var.TRUE)) {
            view = inflater.inflate(R.layout.fragment_dashboard_confirmation, container, false);
            getActivity().setTitle(getString(R.string.change_periode));
        }
        else {
            view = inflater.inflate(R.layout.fragment_dashboard, container, false);
            getActivity().setTitle(getString(R.string.Dashboard));
        }
            ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mListener.setSelectedDrawer(DashboardActivity.nav_home);
        mListener.setAppBarBackground(true);
        getActivity().setTitle(getString(R.string.Dashboard));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.context = (Activity) context;
            mListener = (DrawerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement onFragmentINteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    protected void init(View view) {
        holder = new AnnualHolder();
        if (!GeneralSetting.getParameterValue(var.GENERAL_SETTING_EDIT_CONFIRMATION).equalsIgnoreCase(String.valueOf(true))) {
            GeneralSetting.deleteAll();
            clearSPPA();
        }

        initView();
        bindPicker();
        bindCoverage();
        loadData();
    }


    private void bindPicker() {
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        int month = date.getMonthOfYear();
        int day = date.getDayOfMonth();

        bindFrom(year, month, day);
        bindTo(year, month, day);
    }

    private void bindFrom(int year, int month, int day) {
        dppFrom = DatePickerDialog.newInstance(
                departureDateListener(),
                year,
                month - 1,
                day
        );
        LocalDate date = new LocalDate(year, month, day);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date.toDate());
        dppFrom.setMinDate(calendar);
    }

    private void bindTo(int year, int month, int day) {
        dppTo = DatePickerDialog.newInstance(
                returnDateListener(),
                year,
                month - 1,
                day
        );
//        LocalDate date = new LocalDate(year, month, day);
        LocalDate date;
        if (btnDeparture.getText().toString().isEmpty())
            date = UtilDate.getDate();
        else
            date = UtilDate.toDate(btnDeparture.getText().toString());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date.toDate());
        dppTo.setMinDate(calendar);
    }


    private String getCoverageID() {
        return new Select().from(Product.class)
                .where(Product_Table.Description.eq(spnCoverage.getSelectedItem().toString()))
                .querySingle()
                .ProductCode;
    }

    private String getTypeID() {
        return new Select().from(SubProduct.class)
                .where(SubProduct_Table.Description.eq(spnType.getSelectedItem().toString()))
                .querySingle()
                .SubProductCode;
    }

    private void bindCoverage() {
        try {
            CoverageBind.bind(context, spnCoverage);
            spnCoverage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        String desc = parent.getSelectedItem().toString();
                        Product product = new Select(Product_Table.ProductCode)
                                .from(Product.class)
                                .where(Product_Table.Description.eq(desc))
                                .querySingle();

                        if (product != null) {
                            String value = product.ProductCode;
                            bindType(value);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindType(String productCode) {
        try {
            TypeBind.bind(context, spnType, productCode);

            spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String typeId = TypeBind.getID(spnType);
                    String isAnnual = getIsAnnual(typeId);

                    if (isAnnual.equalsIgnoreCase(var.Annual)) {
                        btnReturn.setEnabled(false);
//                        layTo.setEnabled(false);
                        swiAnnual.setChecked(true);
                        holder.isAnnual = true;

                        if (!TextUtils.isEmpty(btnDeparture.getText().toString())) {
                            String returnSt = getAnnualDate();
                            btnReturn.setText(returnSt);
                        }
                    } else {
                        holder.isAnnual = false;
                        btnReturn.setEnabled(true);
                        swiAnnual.setChecked(false);
//                        layTo.setEnabled(true);
                        btnReturn.setText(holder.returnDate.isEmpty() ? "" : holder.returnDate);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.laySpnCoverage)
    public void openSpnCoverage() {
        spnCoverage.performClick();
    }

    @OnClick(R.id.laySpnType)
    public void openSpnType() {
        spnType.performClick();
    }

    @OnClick(R.id.layFrom)
    public void btnDepartureDateClick() {
        dppFrom.show(context.getFragmentManager(), "tag");
    }


    @OnClick(R.id.layTo)
    public void btnReturnDateClick() {
        try {
            if (swiAnnual.isChecked()) {
                createToast(context.getString(R.string.message_caption_change_date_annual));
                return;
            }

            LocalDate tanggalMulai = UtilDate.getDate();

            if (!TextUtils.isEmpty(btnDeparture.getText().toString())) {
                tanggalMulai = UtilDate.toDate(btnDeparture.getText().toString());
            }

            if (!TextUtils.isEmpty(btnReturn.getText().toString())) {
                LocalDate returnDate = UtilDate.toDate(btnReturn.getText().toString());
                if (returnDate.isAfter(UtilDate.getDate())) {
                    bindTo(returnDate.getYear(), returnDate.getMonthOfYear(), returnDate.getDayOfMonth());
                    dppTo.show(context.getFragmentManager(), "tag");
                    return;
                }
            }
            bindTo(tanggalMulai.getYear(), tanggalMulai.getMonthOfYear(), tanggalMulai.getDayOfMonth());
            dppTo.show(context.getFragmentManager(), "tag");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getIsAnnual(String typeId) {
        return new Select(SubProduct_Table.AnnOrReg).from(SubProduct.class)
                .where(SubProduct_Table.SubProductCode.eq(typeId))
                .querySingle()
                .AnnOrReg;
    }

    private String getAnnualDate() {
        try {
            String mTypeId;
            if (holder.typeId == null)
                mTypeId = TypeBind.getID(spnType);
            else
                mTypeId = holder.typeId;

            int maxDay = Scalar.getMaxDayTravel(mTypeId);
            String returnSt = btnDeparture.getText().toString();
            LocalDate returnDate = UtilDate.toDate(returnSt);

            holder.returnDate = returnSt;
            returnDate = returnDate.plusDays(--maxDay);
            returnSt = returnDate.toString(UtilDate.BASIC_MON_DATE);

            return returnSt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private DatePickerDialog.OnDateSetListener returnDateListener() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int month, int day) {
                try {
                    month++;
                    LocalDate dt = new LocalDate(year, month, day);
                    String tanggal = dt.toString(UtilDate.BASIC_MON_DATE);

                    btnReturn.setText(tanggal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private DatePickerDialog.OnDateSetListener departureDateListener() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int month, int day) {
                try {
                    month++;
                    LocalDate tanggalMulaiDate = new LocalDate(year, month, day);
                    String tanggalMulai = tanggalMulaiDate.toString(UtilDate.BASIC_MON_DATE);
                    btnDeparture.setText(tanggalMulai);

                    if (holder.isAnnual) {
                        String returnDate = getAnnualDate();
                        btnReturn.setText(returnDate);
                    } else {

                        if (!btnReturn.getText().toString().isEmpty()) {
                            LocalDate tanggalBerakhirDate = UtilDate.toDate(btnReturn.getText().toString());

                            if (tanggalMulaiDate.isAfter(tanggalBerakhirDate)) {
                                btnReturn.setText(tanggalMulai);
                            }
                        } else {
                            btnReturn.setText(tanggalMulai);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    protected void registerListener() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnCheckedChanged(R.id.swiAnnual)
    public void swiAnnual() {
     /*   if (swiAnnual.isChecked()) {
            btnReturn.setEnabled(false);
        } else {
            btnReturn.setEnabled(true);
        }*/
    }



    @OnClick(R.id.btnGetQuote)
    public void btnGetQuoteClick() {
        if (!validate()) return;

        if (!GeneralSetting.getParameterValue(var.GENERAL_SETTING_EDIT_CONFIRMATION).equalsIgnoreCase(var.TRUE))
            clearSPPA();

        saveSPPA();
        if (!validateMaxDay()) {
            createToast(context.getString(R.string.message_validate_maximum_days));
            return;
        }


        if (GeneralSetting.getParameterValue(var.GENERAL_SETTING_EDIT_CONFIRMATION).equalsIgnoreCase(var.TRUE)) {
            context.finish();
            return;
        }

        Intent intent = new Intent(getActivity(), FillInsuredActivity.class);
        getActivity().startActivityForResult(intent, getResources().getInteger(R.integer.request_code_payment));
        emptyField();
    }

    private boolean validateMaxDay() {
        try {
            int daysPeriode = Scalar.getDaysPeriode();
            int maxDayTravel = Scalar.getMaxDayTravel(Scalar.getSubProductCode());
            if (daysPeriode > maxDayTravel)
                return false;

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean validate() {
        String depDate = btnDeparture.getText().toString();
        String retDate = btnReturn.getText().toString();

        if (depDate.isEmpty() || retDate.isEmpty()) {
            Snackbar.make(getView(), R.string.message_validate_empty_date, Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (UtilDate.toDate(depDate).isBefore(UtilDate.getDate())) {
            createSnackbar(context.getString(R.string.message_validate_invalid_date));
            return false;
        }

        return true;
    }

    private void initView() {
        if (GeneralSetting.getParameterValue(var.GENERAL_SETTING_EDIT_CONFIRMATION).equalsIgnoreCase(var.TRUE)) {
            btnGetQuote.setText("Ok");
            lblCaption.setVisibility(View.GONE);
        }

    }

    private void emptyField() {
        spnCoverage.setSelection(0);
        spnType.setSelection(0);
        btnDeparture.setText("");
        btnReturn.setText("");
        holder = new AnnualHolder();
        bindPicker();
    }

    public void clearSPPA() {
        Delete.table(SppaBeneficiary.class);
        Delete.table(SppaDestination.class);
        Delete.table(SppaDomestic.class);
        Delete.table(SppaFamily.class);
        Delete.table(SppaFlight.class);
        Delete.table(SppaInsured.class);
        Delete.table(SppaMain.class);
    }

    public void saveSPPA() {
        SppaMain sppaMain;
        sppaMain = new Select().from(SppaMain.class).querySingle();

        if (sppaMain == null)
            sppaMain = new SppaMain();

        if (!GeneralSetting.getParameterValue(var.GENERAL_SETTING_EDIT_CONFIRMATION).equalsIgnoreCase(var.TRUE)) {
            sppaMain.ProductCode = getCoverageID();
            sppaMain.SubProductCode = getTypeID();
        }
        sppaMain.EffectiveDate = UtilDate.format(btnDeparture.getText().toString(),
                UtilDate.BASIC_MON_DATE,
                UtilDate.ISO_DATE);
        sppaMain.ExpireDate = UtilDate.format(btnReturn.getText().toString(),
                UtilDate.BASIC_MON_DATE,
                UtilDate.ISO_DATE);

        sppaMain.save();
    }

    private void loadData () {
        if (!GeneralSetting.getParameterValue(var.GENERAL_SETTING_EDIT_CONFIRMATION).equalsIgnoreCase(var.TRUE))
            return;

        SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();
        if (sppaMain == null)
            return;

        String effDate = UtilDate.format(sppaMain.EffectiveDate, UtilDate.ISO_DATE, UtilDate.BASIC_MON_DATE);
        String expDate = UtilDate.format(sppaMain.ExpireDate, UtilDate.ISO_DATE, UtilDate.BASIC_MON_DATE);

        LocalDate eff = UtilDate.toDate(effDate);
        LocalDate exp = UtilDate.toDate(expDate);

        btnDeparture.setText(effDate);
        btnReturn.setText(expDate);

        bindFrom(eff.getYear(), eff.getMonthOfYear(), eff.getDayOfMonth());
        bindTo(exp.getYear(), exp.getMonthOfYear(), exp.getDayOfMonth());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(UtilDate.getDate().toDate());
        dppFrom.setMinDate(calendar);

        String typeId = sppaMain.SubProductCode;
        String isAnnual = getIsAnnual(typeId);

        if (isAnnual.equalsIgnoreCase(var.Annual)) {
            swiAnnual.setChecked(true);
            holder.isAnnual = true;
        }
        else {
            swiAnnual.setChecked(false);
            holder.isAnnual = false;
        }
        holder.typeId = sppaMain.SubProductCode;


/*        AdapterView.OnItemSelectedListener spnCoverageOnItemSelectedListener = spnCoverage.getOnItemSelectedListener();
        AdapterView.OnItemSelectedListener spnTypeOnItemSelectedListener = spnType.getOnItemSelectedListener();

        spnCoverage.setOnItemSelectedListener(null);
        spnType.setOnItemSelectedListener(null);

        CoverageBind.select(spnCoverage, sppaMain.ProductCode);
        bindType(sppaMain.ProductCode);
        TypeBind.select(spnType, sppaMain.SubProductCode);


        spnCoverage.setOnItemSelectedListener(spnCoverageOnItemSelectedListener);
        spnType.setOnItemSelectedListener(spnTypeOnItemSelectedListener);*/

    }


    private void getLogin() {
        try {
            btnGetQuote.setEnabled(false);
            swipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        swipeRefresh.setRefreshing(true);

                        User user = new Select().from(User.class).querySingle();

                        LoginDal loginDal = new LoginDal(DashboardFragment.this);
                        loginDal.login(user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void login(boolean status) {
        try {
            swipeRefresh.setRefreshing(false);
            btnGetQuote.setEnabled(true);

            if (status) {

                saveSPPA();
                Intent intent = new Intent(getActivity(), FillInsuredActivity.class);
                startActivity(intent);
                emptyField();

            } else {
                Intent intent = new Intent(context, SignInActivity.class);
                startActivity(intent);
                BaseActivity.class.cast(context).transitionSlideEnter();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loginError(String message) {
        try {
            swipeRefresh.setRefreshing(false);
            btnGetQuote.setEnabled(true);

            Snackbar.make(getView(), R.string.message_failed_login, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", loginRetry())
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private View.OnClickListener loginRetry() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLogin();
            }
        };
    }


}