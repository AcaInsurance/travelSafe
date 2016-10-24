package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.aca.travelsafe.BaseActivity;
import com.aca.travelsafe.Dal.Policy;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.SppaMain;
import com.aca.travelsafe.database.SubProduct;
import com.aca.travelsafe.database.SubProduct_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FillInsuranceDetailFragment extends BaseFragment {


    @Bind(R.id.frameInsured)
    FrameLayout frameInsured;
    @Bind(R.id.viewScroll)
    NestedScrollView viewScroll;
    @Bind(R.id.btnBack)
    Button btnBack;
    @Bind(R.id.btnNext)
    Button btnNext;
    @Bind(R.id.viewButtonNavigation)
    LinearLayout viewButtonNavigation;
    private OnFragmentInteractionListener mListener;
    private TripAnnualFragment annualFragment;
    private TripRegularFragment regularFragment;
    private TripDomestikFragment domestikFragment;

    public FillInsuranceDetailFragment() {
    }

    public static FillInsuranceDetailFragment newInstance() {

        Bundle args = new Bundle();

        FillInsuranceDetailFragment fragment = new FillInsuranceDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fill_insurance_detail, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getString(R.string.Insurance_detail));
        initFragment();

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

    public void disableView (){
        try {
            if (Policy.isPaid()) {
                if (annualFragment != null)
                    annualFragment.disableView();

                if (domestikFragment != null)
                    domestikFragment.disableView();

                if (regularFragment != null)
                    regularFragment.disableView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initFragment() {
        try {
            String subProductCode = new Select()
                    .from(SppaMain.class)
                    .querySingle()
                    .SubProductCode;

            SubProduct subProduct =  new Select()
                    .from(SubProduct.class)
                    .where(SubProduct_Table.SubProductCode.eq(subProductCode))
                    .querySingle();

            switch (subProduct.ProductCode) {
                case var.INT :

                    switch (subProduct.AnnOrReg) {
                        case var.Annual:
                            setFragment(frameInsured, TripAnnualFragment.newInstance());
                            annualFragment = (TripAnnualFragment) getChildFragmentManager().findFragmentByTag(TripAnnualFragment.class.getName());
                            break;
                        case var.Regular:
                            setFragment(frameInsured, TripRegularFragment.newInstance());
                            regularFragment = (TripRegularFragment) getChildFragmentManager().findFragmentByTag(TripRegularFragment.class.getName());

                            break;
                    }
                    break;

                case var.DOM:
                    setFragment(frameInsured, TripDomestikFragment.newInstance());
                    domestikFragment = (TripDomestikFragment) getChildFragmentManager().findFragmentByTag(TripDomestikFragment.class.getName());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        loadSPPA();
        disableView();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

        if (getSnackbar() != null) {
            getSnackbar().dismiss();
        }
    }


    @Override
    protected void registerListener() {

    }

    private void loadSPPA() {
        try {
            String subProductCode = new Select()
                    .from(SppaMain.class)
                    .querySingle()
                    .SubProductCode;

            SubProduct subProduct =  new Select()
                    .from(SubProduct.class)
                    .where(SubProduct_Table.SubProductCode.eq(subProductCode))
                    .querySingle();

            switch (subProduct.ProductCode) {
                case var.INT :

                    switch (subProduct.AnnOrReg) {
                        case var.Annual:
                            TripAnnualFragment annualFragment = (TripAnnualFragment) getChildFragmentManager().findFragmentByTag(TripAnnualFragment.class.getName());
                            annualFragment.loadSPPA();

                            break;
                        case var.Regular:
                            TripRegularFragment regularFragment = (TripRegularFragment) getChildFragmentManager().findFragmentByTag(TripRegularFragment.class.getName());
                            regularFragment.loadSPPA();
                            break;
                    }
                    break;

                case var.DOM:
                    TripDomestikFragment domestikFragment = (TripDomestikFragment) getChildFragmentManager().findFragmentByTag(TripDomestikFragment.class.getName());
                    domestikFragment.loadSPPA();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean saveSPPA() {
        try {
            String subProductCode = new Select()
                    .from(SppaMain.class)
                    .querySingle()
                    .SubProductCode;

            SubProduct subProduct =  new Select()
                    .from(SubProduct.class)
                    .where(SubProduct_Table.SubProductCode.eq(subProductCode))
                    .querySingle();

            switch (subProduct.ProductCode) {
                case var.INT :
                    switch (subProduct.AnnOrReg) {
                        case var.Annual:
                            return annualFragment.saveSPPA();

                        case var.Regular:
                            return regularFragment.saveSPPA();
                    }
                    break;

                case var.DOM:
                    return domestikFragment.saveSPPA();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }


    @OnClick(R.id.btnBack)
    public void goBack() {
        if (saveSPPA())
            ((BaseActivity) getActivity()).popupFragment();
    }

    @OnClick(R.id.btnNext)
    public void goNext() {
        if (saveSPPA())
            ((BaseActivity) getActivity()).setFragment(R.id.fragment, FillConfirmationFragment.newInstance());
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
