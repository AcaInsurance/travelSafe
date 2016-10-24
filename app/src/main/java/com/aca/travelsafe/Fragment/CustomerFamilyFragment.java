package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.aca.travelsafe.Adapter.PickedFamilyAdapter;
import com.aca.travelsafe.FillFamilyActivity;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.Widget.RecyclerViewWidget;
import com.aca.travelsafe.database.GeneralSetting;
import com.aca.travelsafe.database.SppaFamily;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerFamilyFragment extends BaseFragment {
    @Bind(R.id.btnAddFamily)
    Button btnAddFamily;
    @Bind(R.id.listFamily)
    RecyclerView listFamily;
    @Bind(R.id.lytParent)
    LinearLayout lytParent;
    private OnFragmentInteractionListener mListener;

    public CustomerFamilyFragment() {
        // Required empty public constructor
    }

    public static CustomerFamilyFragment newInstance() {
        CustomerFamilyFragment fragment = new CustomerFamilyFragment();
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
        int resLayout;
        String parameterValue = GeneralSetting.getParameterValue(var.GENERAL_SETTING_IS_POLIS_ACTIVITY);

        if (parameterValue.equalsIgnoreCase(var.TRUE))
            resLayout = R.layout.fragment_polis_family;
        else
            resLayout = R.layout.fragment_family;

        View view = inflater.inflate(resLayout, container, false);
        ButterKnife.bind(this, view);
        return view;
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

    @Override
    protected void init(View view) {

    }

    @Override
    protected void registerListener() {

    }

    @Override
    public void onResume() {
        super.onResume();
        bindFamily();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @OnClick(R.id.btnAddFamily)
    public void addFamilyMember() {
        Intent intent = new Intent(context, FillFamilyActivity.class);
        startActivity(intent);
    }

    public void disableView() {
        disable(lytParent);
    }

    private void bindFamily() {
        try {
            PickedFamilyAdapter adapter = new PickedFamilyAdapter(context);
            RecyclerViewWidget.create(context, listFamily);
            listFamily.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveSPPA() {

    }

    public void loadSPPA() {

    }
    public boolean validate() {
        if (!validateFamCount()) return false;
        if (!recountFamAge()) return false;
        return true;
    }

    private boolean validateFamCount() {
        int famCount = new Select().from(SppaFamily.class).query().getCount();
        boolean valid =  famCount >= 2 ? true : false;
        if (!valid)
            createToast(context.getString(R.string.message_validation_family_number));
        return valid;
    }

    private boolean recountFamAge() {
        try {
            FillFamilyFragment familyFragment = new FillFamilyFragment();

            List<SppaFamily> familyList = new Select().from(SppaFamily.class).queryList();
            for (SppaFamily s:familyList) {
                String famCode = s.FamilyCode;
                if (famCode.equalsIgnoreCase(var.TertanggungUtama))
                    continue;

                String dob = UtilDate.toString(s.DOB);
                String famId = String.valueOf(s.id);
                boolean isValidAge = familyFragment.validateAge(famCode, dob);

                if (!isValidAge) {
                    createToast(context.getString(R.string.message_validate_invalid_age));
                    Intent intent = new Intent(context, FillFamilyActivity.class);
                    intent.putExtra(var.id, famId);
                    context.startActivity(intent);
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
