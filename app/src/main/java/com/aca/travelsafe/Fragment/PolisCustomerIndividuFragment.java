package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.aca.travelsafe.Binding.CitizenBind;
import com.aca.travelsafe.Dal.Scalar;
import com.aca.travelsafe.Interface.ScrollToListener;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.UtilView;
import com.aca.travelsafe.Util.Utility;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.GeneralSetting;
import com.aca.travelsafe.database.SppaFamily;
import com.aca.travelsafe.database.SppaFamily_Table;
import com.aca.travelsafe.database.SppaInsured;
import com.aca.travelsafe.database.SppaMain;
import com.aca.travelsafe.database.SubProduct;
import com.aca.travelsafe.database.SubProduct_Table;
import com.aca.travelsafe.database.UserDetail;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.LocalDate;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aca.travelsafe.Util.UtilView.isEmpty;

public class PolisCustomerIndividuFragment extends BaseFragment {


    public ScrollToListener mListener;
    @Bind(R.id.txtFullName)
    EditText txtFullName;
    @Bind(R.id.lblFullName)
    TextInputLayout lblFullName;
    @Bind(R.id.txtDOB)
    EditText txtDOB;
    @Bind(R.id.lblDOB)
    TextInputLayout lblDOB;
    @Bind(R.id.txtPassport)
    EditText txtPassport;
    @Bind(R.id.lblPassport)
    TextInputLayout lblPassport;
    @Bind(R.id.txtKTP)
    EditText txtKTP;
    @Bind(R.id.lblKTP)
    TextInputLayout lblKTP;
    @Bind(R.id.spnCitizen)
    AppCompatSpinner spnCitizen;
    @Bind(R.id.lytParent)
    LinearLayout lytParent;
    private DatePickerDialog dpp;
    private ViewHolder holder;

    public PolisCustomerIndividuFragment() {
    }

    public static PolisCustomerIndividuFragment newInstance() {
        PolisCustomerIndividuFragment fragment = new PolisCustomerIndividuFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int resLayout = R.layout.fragment_polis_invididu;
        View view = inflater.inflate(resLayout, container, false);
        ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ScrollToListener) {
            this.context = (Activity) context;
            mListener = (ScrollToListener) context;
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    protected void init(View view) {
        CitizenBind.bind(context, spnCitizen);
        bindPickerDOB();
        initView();
//        setImeOption();
    }

    private void setImeOption() {
        txtDOB.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        txtKTP.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        txtPassport.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        txtFullName.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        Utility.showSoftKeyboard(txtFullName, getActivity());
    }

    @Override
    protected void registerListener() {
//        txtFullName.addTextChangedListener(inputTextWatcher(lblFullName));
//        txtKTP.addTextChangedListener(inputTextWatcher(lblKTP));
//        txtPassport.addTextChangedListener(inputTextWatcher(lblPassport));
//        txtDOB.addTextChangedListener(inputTextWatcher(lblDOB));
    }


    public void disableView() {
        if (!GeneralSetting.getParameterValue(var.GENERAL_SETTING_IS_POLIS_ACTIVITY).equalsIgnoreCase(var.TRUE))
            disable(lytParent);
    }

    public void loadState() {
        if (holder == null)
            return;

        txtFullName.setText(holder.name);
        txtDOB.setText(holder.dob);
        CitizenBind.select(spnCitizen, holder.citizen);
        txtPassport.setText(holder.passport);
        txtKTP.setText(holder.ktp);

    }

    public void saveState() {
        holder = new ViewHolder();
        holder.name = txtFullName.getText().toString();
        holder.dob = txtDOB.getText().toString();
        holder.citizen = CitizenBind.getID(spnCitizen);
        holder.passport = txtPassport.getText().toString();
        holder.ktp = txtKTP.getText().toString();
    }

    private void initView() {
        try {
            SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();

            if (sppaMain == null) {
                txtKTP.setVisibility(View.VISIBLE);
                txtPassport.setVisibility(View.VISIBLE);
                return;
            }

            String productCode = sppaMain.ProductCode;

            if (productCode.equalsIgnoreCase(var.INT)) {
                txtKTP.setVisibility(View.GONE);
                txtPassport.setVisibility(View.VISIBLE);
            } else {
                txtKTP.setVisibility(View.VISIBLE);
                txtPassport.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnClick(R.id.txtDOB)
    public void pickerDOB() {
        dpp.show(context.getFragmentManager(), "tag");
    }


    public void saveUser() {
        UserDetail userDetail;

        try {
            userDetail = new Select().from(UserDetail.class).querySingle();

            if (userDetail == null)
                userDetail = new UserDetail();

            userDetail.Name = txtFullName.getText().toString();
            userDetail.DOB = UtilDate.format(txtDOB.getText().toString(), UtilDate.BASIC_MON_DATE, UtilDate.ISO_DATE);
            userDetail.Country = CitizenBind.getID(spnCitizen);
            userDetail.PassportNo = txtPassport.getText().toString();
            userDetail.IdentityNo = txtKTP.getText().toString();

            userDetail.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadUser() {
        UserDetail userDetail;

        try {
            userDetail = new Select().from(UserDetail.class).querySingle();

            if (userDetail == null) {
                return;
            }
            saveState();

            String dob = UtilDate.format(userDetail.DOB, UtilDate.ISO_DATE, UtilDate.BASIC_MON_DATE);

            txtFullName.setText(userDetail.Name);
            txtDOB.setText(dob);
            CitizenBind.select(spnCitizen, userDetail.Country);
            txtPassport.setText(userDetail.PassportNo);
            txtKTP.setText(userDetail.IdentityNo);

            bindPickerDOB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadSPPA() {
        SppaInsured sppaInsured = new Select().from(SppaInsured.class).querySingle();

        if (sppaInsured != null) {
            String dob = UtilDate.format(sppaInsured.DateOFBirth, UtilDate.ISO_DATE, UtilDate.BASIC_MON_DATE);

            txtFullName.setText(sppaInsured.InsuredName);
            txtDOB.setText(dob);
            CitizenBind.select(spnCitizen, sppaInsured.CitizenshipId);
            txtPassport.setText(sppaInsured.PassportNo);
            txtKTP.setText(sppaInsured.IdNo);

            bindPickerDOB();
        }
    }


    /******
     * VALIDASI
     *********/

    private void removeErrorState() {
        UtilView.setErrorNull(lblDOB);
        UtilView.setErrorNull(lblFullName);
        UtilView.setErrorNull(lblKTP);
        UtilView.setErrorNull(lblPassport);
    }


    public boolean validate() {
        removeErrorState();

        if (!validateEmptyField()) return false;
        if (!validateAge()) return false;


        return true;
    }

    public boolean validateIdentityID() {
        boolean valid = !txtPassport.getText().toString().isEmpty() ||
                !txtKTP.getText().toString().isEmpty();

        if (!valid) {
            createToast("Fill at least 1 information ID");
        }
        return valid;
    }

    public boolean validateEmptyField() {
        boolean valid;

        String fragmentCaller = GeneralSetting.getParameterValue(var.GENERAL_SETTING_CUSTOMER_FRAGMENT_USED);

        if (fragmentCaller.equalsIgnoreCase(var.CUSTOMER_INDIVIDU_FRAGMENT_USED_ACTIVITY)) {
            valid = !isEmpty(txtFullName, lblFullName)
                    && !isEmpty(txtDOB, lblDOB)
                    && !isEmpty(txtPassport, lblPassport)
                    && !isEmpty(txtKTP, lblKTP);
        } else {
            valid = !isEmpty(txtFullName, lblFullName)
                    && !isEmpty(txtDOB, lblDOB)
                    && validateIdentityID();
        }

        if (!valid) {
            Snackbar.make(getView(), R.string.message_validate_empty_field, Snackbar.LENGTH_SHORT).show();
        }
        return valid;
    }


    private boolean validateAge() {
        SubProduct subProduct;
        int maxAgeFrom, maxAgeTo, minAge, age;
        String expDate;
        String dob;
        String subProductCode;
        String message;

        try {
            subProductCode = Scalar.getSubProductCode();

            if (subProductCode.isEmpty())
                return true;

            subProduct = new Select().from(SubProduct.class)
                    .where(SubProduct_Table.SubProductCode.eq(subProductCode))
                    .querySingle();


            expDate = Scalar.getExpiredDate();
            dob = txtDOB.getText().toString();
            age = Scalar.getAge(expDate, dob);
            maxAgeFrom = Integer.parseInt(subProduct.MaxAgeFrom);
            maxAgeTo = Integer.parseInt(subProduct.MaxAgeTo);
            minAge = Integer.parseInt(subProduct.MinAge);

            message = String.format(context.getString(R.string.message_validate_age), minAge, maxAgeTo);

            if ((double) age >= (double) minAge / 12 && age <= maxAgeTo) {
                return true;
            } else {
//                mListener.scrollTo(txtDOB);
                UtilView.setError(lblDOB, message);
                createToast(message);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /*********
     * SAVING DATA
     ***********/

    public void saveSPPA() {
        try {
            SppaInsured sppaInsured = new Select().from(SppaInsured.class).querySingle();

            if (sppaInsured == null)
                sppaInsured = new SppaInsured();

            sppaInsured.InsuredName = txtFullName.getText().toString();
            sppaInsured.DateOFBirth = UtilDate.toIsoDate(txtDOB.getText().toString());
            sppaInsured.Age = String.valueOf(
                    Scalar.getAge(
                            Scalar.getExpiredDate(),
                            txtDOB.getText().toString()));

            sppaInsured.CitizenshipId = CitizenBind.getID(spnCitizen);
            sppaInsured.PassportNo = txtPassport.getText().toString();
            sppaInsured.IdNo = txtKTP.getText().toString();
            sppaInsured.save();

            SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();

            if (sppaMain == null)
                sppaMain = new SppaMain();

            sppaMain.Name = txtFullName.getText().toString();
            sppaMain.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTertanggungUtama() {
        SppaFamily sppaFamily;

        try {
            sppaFamily = new Select().from(SppaFamily.class)
                    .where(SppaFamily_Table.FamilyCode.eq(var.TertanggungUtama))
                    .querySingle();

            if (sppaFamily == null) {
                sppaFamily = new SppaFamily();
            }

            sppaFamily.SequenceNo = var.SEQUENCE_NUMBER_TU;
            sppaFamily.Name = txtFullName.getText().toString();
            sppaFamily.FamilyCode = var.TertanggungUtama;
            sppaFamily.DOB = UtilDate.toDate(txtDOB.getText().toString()).toDate();
            sppaFamily.Age = String.valueOf(
                    Scalar.getAge(
                            Scalar.getExpiredDate(),
                            txtDOB.getText().toString()));
            sppaFamily.PassportNo = txtPassport.getText().toString();
            sppaFamily.CitizenshipId = CitizenBind.getID(spnCitizen);

            sppaFamily.save();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUser() {
        try {
            UserDetail userDetail;
            userDetail = new Select().from(UserDetail.class).querySingle();

            if (userDetail == null) {
                userDetail = new UserDetail();
            }

            userDetail.Name = txtFullName.getText().toString();
            userDetail.DOB = UtilDate.format(txtDOB.getText().toString(),
                    UtilDate.BASIC_MON_DATE,
                    UtilDate.ISO_DATE_TIME);
            userDetail.PassportNo = txtPassport.getText().toString();
            userDetail.IdentityNo = txtKTP.getText().toString();
            userDetail.Country = CitizenBind.getID(spnCitizen);

            userDetail.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindPickerDOB() {
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        int month = date.getMonthOfYear();
        int day = date.getDayOfMonth();

        if (!txtDOB.getText().toString().isEmpty()) {
            String tanggal = txtDOB.getText().toString();
            LocalDate tanggalDate = UtilDate.toDate(tanggal);

            year = tanggalDate.getYear();
            month = tanggalDate.getMonthOfYear();
            day = tanggalDate.getDayOfMonth();
        }

        dpp = DatePickerDialog.newInstance(
                dobPickerListener(),
                year,
                --month,
                day
        );

        Calendar c = Calendar.getInstance();
        c.setTime(LocalDate.now().toDate());

        dpp.setMaxDate(c);

    }

    private DatePickerDialog.OnDateSetListener dobPickerListener() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int month, int day) {
                try {
                    String tgl = new LocalDate(year, ++month, day)
                            .toString(UtilDate.BASIC_MON_DATE);

                    txtDOB.setText(tgl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private class ViewHolder {
        String name,
                dob,
                citizen,
                passport,
                ktp;

        public ViewHolder() {
            name = "";
            dob = "";
            citizen = "";
            passport = "";
            ktp = "";
        }
    }


}
