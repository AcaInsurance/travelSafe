package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.aca.travelsafe.Interface.ScrollToListener;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Util.UtilView;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.GeneralSetting;
import com.aca.travelsafe.database.SppaBeneficiary;
import com.raizlabs.android.dbflow.sql.language.Select;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.aca.travelsafe.Util.UtilView.isEmpty;

public class BeneficiaryFragment extends BaseFragment {
    @Bind(R.id.txtBeneName)
    EditText txtBeneName;
    @Bind(R.id.txtBeneRelation)
    TextInputEditText txtBeneRelation;
    @Bind(R.id.lytParent)
    LinearLayout lytParent;
    @Bind(R.id.lblBeneName)
    TextInputLayout lblBeneName;
    @Bind(R.id.lblBeneRelation)
    TextInputLayout lblBeneRelation;

    public BeneficiaryFragment() {
        // Required empty public constructor
    }

    public static BeneficiaryFragment newInstance() {
        BeneficiaryFragment fragment = new BeneficiaryFragment();
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
        int resLayout;
        String parameterValue = GeneralSetting.getParameterValue(var.GENERAL_SETTING_IS_POLIS_ACTIVITY);

        if (parameterValue.equalsIgnoreCase(var.TRUE))
            resLayout = R.layout.fragment_polis_beneficiary;
        else
            resLayout = R.layout.fragment_beneficiary;

        View view = inflater.inflate(resLayout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            this.context = (Activity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected void init(View view) {

    }

    @Override
    protected void registerListener() {
        txtBeneName.addTextChangedListener(UtilView.inputTextWatcher(lblBeneName));
        txtBeneRelation.addTextChangedListener(UtilView.inputTextWatcher(lblBeneRelation));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void disableView() {
        disable(lytParent);
    }

    public void loadSPPA() {
        try {
            SppaBeneficiary sppaBeneficiary = new Select().from(SppaBeneficiary.class).querySingle();

            if (sppaBeneficiary != null) {
                txtBeneName.setText(sppaBeneficiary.BeneficiaryName);
                txtBeneRelation.setText(sppaBeneficiary.BeneficiaryRelation);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean validate() {
        if (!validateEmptyField()) return false;

        return true;
    }

    public boolean validateEmptyField() {
        boolean valid = (!isEmpty(txtBeneName, lblBeneName) && !isEmpty(txtBeneRelation, lblBeneRelation));

        if (!valid) {
            Snackbar.make(getView(), R.string.message_validate_empty_field, Snackbar.LENGTH_SHORT).show();
//            mListener.scrollTo(txtBeneRelation);
        }
        return valid;
    }

    public void saveSPPA() {
        try {
            SppaBeneficiary sppaBeneficiary = new Select().from(SppaBeneficiary.class).querySingle();

            if (sppaBeneficiary == null)
                sppaBeneficiary = new SppaBeneficiary();

            sppaBeneficiary.BeneficiaryName = txtBeneName.getText().toString();
            sppaBeneficiary.BeneficiaryRelation = txtBeneRelation.getText().toString();
            sppaBeneficiary.save();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
