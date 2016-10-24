package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aca.travelsafe.Binding.CitizenBind;
import com.aca.travelsafe.Binding.FamilyRelationBind;
import com.aca.travelsafe.Binding.PickerBind;
import com.aca.travelsafe.Dal.Scalar;
import com.aca.travelsafe.Holder.DateHolder;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.UtilView;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.FamilyRelation;
import com.aca.travelsafe.database.FamilyRelation_Table;
import com.aca.travelsafe.database.SppaFamily;
import com.aca.travelsafe.database.SppaFamily_Table;
import com.aca.travelsafe.database.SppaInsured;
import com.aca.travelsafe.database.SppaMain;
import com.aca.travelsafe.database.SubProduct;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.LocalDate;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aca.travelsafe.Util.UtilView.isEmpty;

public class FillFamilyFragment extends BaseFragment {


    @Bind(R.id.lblCaption)
    TextView lblCaption;
    @Bind(R.id.txtName)
    EditText txtName;
    @Bind(R.id.lblName)
    TextInputLayout lblName;
    @Bind(R.id.spnFamilyRelation)
    Spinner spnFamilyRelation;
    @Bind(R.id.txtDOB)
    EditText txtDOB;
    @Bind(R.id.lblDOB)
    TextInputLayout lblDOB;
    @Bind(R.id.txtPassport)
    EditText txtPassport;
    @Bind(R.id.lblPassport)
    TextInputLayout lblPassport;
    @Bind(R.id.spnCitizen)
    Spinner spnCitizen;
    @Bind(R.id.viewScroll)
    NestedScrollView viewScroll;
    @Bind(R.id.btnCancel)
    Button btnCancel;
    @Bind(R.id.btnOk)
    Button btnOk;
    @Bind(R.id.viewButtonFooter)
    LinearLayout viewButtonFooter;
    private OnFragmentInteractionListener mListener;
    private DatePickerDialog dppDOB;
    private SppaFamily sppaFamily;

    public FillFamilyFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static FillFamilyFragment newInstance() {
        FillFamilyFragment fragment = new FillFamilyFragment();
        return fragment;
    }

    public static FillFamilyFragment newInstance(String id) {
        FillFamilyFragment fragment = new FillFamilyFragment();
        Bundle args = new Bundle();
        args.putString(var.id, id);
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fill_family, container, false);
        ButterKnife.bind(this, view);

        return view;
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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    protected void init(View view) {
        bindPicker();
        CitizenBind.bind(context, spnCitizen);
        FamilyRelationBind.bind(context, spnFamilyRelation);

        if (getArguments() != null) {
            String id = getArguments().getString(var.id);
            loadSPPA(id);
        }
    }

    @Override
    protected void registerListener() {
        txtDOB.addTextChangedListener(UtilView.inputTextWatcher(lblDOB));
        txtName.addTextChangedListener(UtilView.inputTextWatcher(lblName));
        txtPassport.addTextChangedListener(UtilView.inputTextWatcher(lblPassport));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void loadSPPA(String id) {
        sppaFamily = new Select()
                .from(SppaFamily.class)
                .where(SppaFamily_Table.id.eq(Integer.parseInt(id)))
                .querySingle();

        if (sppaFamily != null) {
            txtName.setText(sppaFamily.Name);
            txtPassport.setText(sppaFamily.PassportNo);
//            txtDOB.setText(UtilDate.dateDbToDate(sppaFamily.DOB));
//            txtDOB.setText(LocalDateTime.fromDateFields(sppaFamily.DOB).toString(UtilDate.BASIC_MON_DATE));
            txtDOB.setText(UtilDate.toString(sppaFamily.DOB));
            FamilyRelationBind.select(spnFamilyRelation, sppaFamily.FamilyCode);
            CitizenBind.select(spnCitizen, sppaFamily.CitizenshipId);

            spnFamilyRelation.setEnabled(false);
            bindPicker();
        }
    }


    public boolean validate() {
        if (!validateEmptyField()) return false;
        if (!validateAge(null, null)) return false;

        return true;
    }

    public boolean validateAge(String pFamCode, String pDob) {
        FamilyRelation familyRelation;
        SubProduct subProduct;
        int minAge, maxAge, age;
        String expDate;
        String dob;
        String famCode;
        String message;

        try {
            if (TextUtils.isEmpty(pFamCode)) {
                famCode = FamilyRelationBind.getID(spnFamilyRelation);
            }
            else {
                famCode = pFamCode;
            }

            familyRelation = new Select().from(FamilyRelation.class)
                    .where(FamilyRelation_Table.FamilyCode.eq(famCode))
                    .querySingle();

            subProduct = SubProduct.get();

            expDate = Scalar.getExpiredDate();
            dob = TextUtils.isEmpty(pDob) ? txtDOB.getText().toString() : pDob;
            age = Scalar.getAge(expDate, dob);
            maxAge = Integer.parseInt(familyRelation.MaxAge);
            minAge = Integer.parseInt(subProduct.MinAge);

            if (age == 0) {
                int ageInMonth = UtilDate.monthDiffInPeriode(UtilDate.toDate(dob), UtilDate.toDate(expDate));
                if (ageInMonth >= minAge)
                    return true;
            }
            if ((double) age >= (double) minAge / 12 && age < maxAge) {
                return true;
            }
            else if (age == maxAge) {
                LocalDate minusOneDayDate = UtilDate.toDate(expDate).minusDays(1);
                int newAge = Scalar.getAge(minusOneDayDate.toString(UtilDate.BASIC_MON_DATE), dob);

                if (newAge < maxAge)
                    return true;
                else {
                    if (txtDOB != null || lblDOB != null) {
                        message = String.format(context.getString(R.string.message_validate_age), minAge, maxAge);
                        scrollTo(txtDOB);
                        UtilView.setError(lblDOB, message);
                    }
                    return false;
                }
            }
            else {
                if (txtDOB != null || lblDOB != null) {
                    message = String.format(context.getString(R.string.message_validate_age), minAge, maxAge);
                    scrollTo(txtDOB);
                    UtilView.setError(lblDOB, message);
                }
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public void scrollTo(View view) {
        viewScroll.smoothScrollTo(0, view.getBottom());
    }


    public boolean validateEmptyField() {
        boolean valid = (
                !isEmpty(txtName, lblName) &&
                        !isEmpty(txtDOB, lblDOB) &&
                        !isEmpty(txtPassport, lblPassport));

        if (!valid) {
            Snackbar.make(getView(), R.string.message_validate_empty_field, Snackbar.LENGTH_SHORT).show();
        }

        return valid;
    }

    public void saveSPPA() {
        if (!validate()) return;
        if (saveSppaFamily()) context.finish();
    }

    private void saveSppaInsured() {
        try {
            SppaInsured sppaInsured = new Select().from(SppaInsured.class).querySingle();

            if (sppaInsured == null)
                sppaInsured = new SppaInsured();

            sppaInsured.InsuredName = txtName.getText().toString();
            sppaInsured.DateOFBirth = UtilDate.toIsoDate(txtDOB.getText().toString());
            sppaInsured.Age = String.valueOf(
                    Scalar.getAge(
                            Scalar.getExpiredDate(),
                            txtDOB.getText().toString()));

            sppaInsured.CitizenshipId = CitizenBind.getID(spnCitizen);
            sppaInsured.PassportNo = txtPassport.getText().toString();
            sppaInsured.save();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String getSeqNumber() {
        String seqTemp = null;
        try {
            SppaFamily sppaFamily = new Select().from(SppaFamily.class).querySingle();
            if (sppaFamily == null) {
                seqTemp = var.SEQUENCE_NUMBER_TU;
            } else {
                sppaFamily = new Select(Method.ALL_PROPERTY, Method.max(SppaFamily_Table.SequenceNo))
                        .from(SppaFamily.class)
                        .querySingle();
                seqTemp = sppaFamily.SequenceNo;
            }

            seqTemp = String.format("%03d", Integer.parseInt(seqTemp) + 1);
        } catch (Exception e) {
            e.printStackTrace();
            return "000";
        }

        return seqTemp;
    }

    private boolean saveSppaFamily() {
        try {
            if (sppaFamily == null)
                sppaFamily = new SppaFamily();

//            sppaFamily.SequenceNo = getSeqNumber();
            sppaFamily.Name = txtName.getText().toString();
            sppaFamily.FamilyCode = FamilyRelationBind.getID(spnFamilyRelation);
            sppaFamily.DOB = UtilDate.toDate(txtDOB.getText().toString()).toDate();
            sppaFamily.Age = String.valueOf(
                    Scalar.getAge(
                            Scalar.getExpiredDate(),
                            txtDOB.getText().toString()));
            sppaFamily.PassportNo = txtPassport.getText().toString();
            sppaFamily.CitizenshipId = CitizenBind.getID(spnCitizen);

            if (spnFamilyRelation.isEnabled()) {
                SppaFamily sf = new Select().from(SppaFamily.class)
                        .where(SppaFamily_Table.FamilyCode.eq(sppaFamily.FamilyCode))
                        .querySingle();

                if (sf != null) {
                    String famRelation = spnFamilyRelation.getSelectedItem().toString();
                    String message = famRelation + context.getString(R.string.message_validate_already_exist);

                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            sppaFamily.save();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveTertanggungUtama() {
        if (FamilyRelationBind.getID(spnFamilyRelation).equals(var.TertanggungUtama)) {
            SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();
            sppaMain.Name = txtName.getText().toString();
            sppaMain.save();

            saveSppaInsured();
        }
    }


    @OnClick(R.id.txtDOB)
    public void pickDOB() {
        dppDOB.show(context.getFragmentManager(), "tag");
    }

    private void bindPicker() {
        String dob = txtDOB.getText().toString();
        DateHolder dateHolder = UtilDate.splitDate(dob);

        dppDOB = DatePickerDialog.newInstance(
                pickerDOBListener(),
                dateHolder.year,
                --dateHolder.month,
                dateHolder.day
        );
        PickerBind.setMax(dppDOB, UtilDate.getDate().toString(UtilDate.BASIC_MON_DATE));
    }

    private DatePickerDialog.OnDateSetListener pickerDOBListener() {

        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int month, int day) {
                try {
                    month++;
                    LocalDate dt = new LocalDate(year, month, day);
                    String tanggal = dt.toString(UtilDate.BASIC_MON_DATE);

                    txtDOB.setText(tanggal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @OnClick(R.id.btnCancel)
    public void cancelClick() {
        context.finish();
    }


    @OnClick(R.id.btnOk)
    public void btnOkClick() {
        saveSPPA();
    }


}
