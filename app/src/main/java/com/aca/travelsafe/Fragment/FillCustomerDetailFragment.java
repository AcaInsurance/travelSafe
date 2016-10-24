package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.aca.travelsafe.BaseActivity;
import com.aca.travelsafe.Dal.LoginDal;
import com.aca.travelsafe.Dal.Policy;
import com.aca.travelsafe.EditConfirmationActivity;
import com.aca.travelsafe.Interface.LoginListener;
import com.aca.travelsafe.Interface.ScrollToListener;
import com.aca.travelsafe.R;
import com.aca.travelsafe.SignInActivity;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.GeneralSetting;
import com.aca.travelsafe.database.SppaMain;
import com.aca.travelsafe.database.SubProduct;
import com.aca.travelsafe.database.SubProduct_Table;
import com.aca.travelsafe.database.User;
import com.aca.travelsafe.database.UserDetail;
import com.raizlabs.android.dbflow.sql.language.Select;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class FillCustomerDetailFragment extends BaseFragment
        implements
        ScrollToListener

{

    @Bind(R.id.frameHeader)
    FrameLayout frameHeader;
    @Bind(R.id.frameCustomerDetail)
    FrameLayout frameCustomerDetail;
    @Bind(R.id.btnNext)
    Button btnNext;
    @Bind(R.id.viewButtonNavigation)
    LinearLayout viewButtonNavigation;
    @Bind(R.id.frameBene)
    FrameLayout frameBene;
    @Bind(R.id.swiUserData)
    SwitchCompat swiUserData;
    @Bind(R.id.viewSwitch)
    RelativeLayout viewSwitch;
    @Bind(R.id.frameFamily)
    FrameLayout frameFamily;
    @Bind(R.id.viewParent)
    RelativeLayout viewParent;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @Bind(R.id.viewScroll)
    NestedScrollView viewScroll;


    private OnFragmentInteractionListener mListener;
    private CustomerIndividuFragment individuFragment;
    private CustomerFamilyFragment familyFragment;
    private CustomerDetailFragment detailFragment;
    private BeneficiaryFragment beneficiaryFragment;

    public FillCustomerDetailFragment() {
        // Required empty public constructor
    }

    public static FillCustomerDetailFragment newInstance() {

        Bundle args = new Bundle();

        FillCustomerDetailFragment fragment = new FillCustomerDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment newInstance(String sppaNo) {
        Bundle args = new Bundle();
        args.putString(var.SPPA_NO, sppaNo);

        FillCustomerDetailFragment fragment = new FillCustomerDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if (GeneralSetting.getParameterValue(var.GENERAL_SETTING_IS_POLIS_ACTIVITY).equalsIgnoreCase(var.TRUE))
            view = inflater.inflate(R.layout.fragment_polis_cutomer_detail, container, false);
        else {
            view = inflater.inflate(R.layout.fragment_fill_cutomer_detail, container, false);
            setHasOptionsMenu(true);
        }
        ButterKnife.bind(this, view);
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
    public void onResume() {
        super.onResume();
        loadSPPA();
        disableView();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(false);
            swipeRefresh.invalidate();
        }

        if (getSnackbar() != null) {
            getSnackbar().dismiss();
        }

        GeneralSetting.delete(var.GENERAL_SETTING_CUSTOMER_FRAGMENT_USED);

    }

    private void initFragment() {
        try {
            String subProductCode = getSubProductCode();
            String idOrFam = getIdOrFam(subProductCode);

            switch (idOrFam) {
                case var.Individu:
                    setFragment(frameHeader.getId(), CustomerIndividuFragment.newInstance());
                    individuFragment = (CustomerIndividuFragment) getChildFragmentManager().findFragmentByTag(CustomerIndividuFragment.class.getName());
                    break;

                case var.Family:
                    setFragment(frameHeader.getId(), CustomerIndividuFragment.newInstance());
                    setFragment(frameFamily.getId(), CustomerFamilyFragment.newInstance());

                    individuFragment = (CustomerIndividuFragment) getChildFragmentManager().findFragmentByTag(CustomerIndividuFragment.class.getName());
                    familyFragment = (CustomerFamilyFragment) getChildFragmentManager().findFragmentByTag(CustomerFamilyFragment.class.getName());
                    break;
            }
            setFragment(frameCustomerDetail.getId(), CustomerDetailFragment.newInstance());
            setFragment(frameBene.getId(), BeneficiaryFragment.newInstance());

            detailFragment = (CustomerDetailFragment) getChildFragmentManager().findFragmentByTag(CustomerDetailFragment.class.getName());
            beneficiaryFragment = (BeneficiaryFragment) getChildFragmentManager().findFragmentByTag(BeneficiaryFragment.class.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void init(View view) {
        getActivity().setTitle(getString(R.string.Customer_detail));
        GeneralSetting.insert(var.GENERAL_SETTING_CUSTOMER_FRAGMENT_USED, getActivity().getClass().getName());
    }

    @Override
    protected void registerListener() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
            }
        });

    }


    @OnClick(R.id.btnNext)
    public void goNext() {
        try {
            if (!saveSPPA())
                return;

            ((BaseActivity) context).setFragment(R.id.fragment, FillInsuranceDetailFragment.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnCheckedChanged(R.id.swiUserData)
    public void swiUsingUserDataCheckedChanged() {
        if (swiUserData.isChecked()) {
            UserDetail userDetail;

            try {
                userDetail = new Select().from(UserDetail.class).querySingle();
                if (userDetail == null) {
                    createToast(context.getString(R.string.message_caption_login_first));
                    return;
                }

                individuFragment.loadUser();
                detailFragment.loadUser();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            individuFragment.loadState();
            detailFragment.loadState();
        }
    }

    private void disable() {
        viewSwitch.setEnabled(false);
        swiUserData.setEnabled(false);
    }

    private void disableView() {
        try {
            if (Policy.isPaid()) {

                disable();

                String subProductCode = getSubProductCode();
                String idOrFam = getIdOrFam(subProductCode);


                CustomerIndividuFragment custIndividu = (CustomerIndividuFragment) getChildFragmentManager().findFragmentByTag(CustomerIndividuFragment.class.getName());
                custIndividu.disableView();

                switch (idOrFam) {
                    case var.Family:
                        CustomerFamilyFragment customerFamilyFragment = (CustomerFamilyFragment) getChildFragmentManager().findFragmentByTag(CustomerFamilyFragment.class.getName());
                        customerFamilyFragment.disableView();
                        break;
                }

                CustomerDetailFragment customerDetailFragment = (CustomerDetailFragment) getChildFragmentManager().findFragmentByTag(CustomerDetailFragment.class.getName());
                customerDetailFragment.disableView();

                BeneficiaryFragment beneficiaryFragment = (BeneficiaryFragment) getChildFragmentManager().findFragmentByTag(BeneficiaryFragment.class.getName());
                beneficiaryFragment.disableView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getIdOrFam(String subProductCode) {
        return new Select()
                .from(SubProduct.class)
                .where(SubProduct_Table.SubProductCode.eq(subProductCode))
                .querySingle()
                .IdOrFa;
    }

    public String getSubProductCode() {
        String subProductCode = new Select()
                .from(SppaMain.class)
                .querySingle()
                .SubProductCode;
        return subProductCode;
    }

    public void loadSPPA() {
        try {
            String subProductCode = getSubProductCode();
            String idOrFam = getIdOrFam(subProductCode);

            CustomerIndividuFragment custIndividu = (CustomerIndividuFragment) getChildFragmentManager().findFragmentByTag(CustomerIndividuFragment.class.getName());
            custIndividu.loadSPPA();

            switch (idOrFam) {
                case var.Family:
                    CustomerFamilyFragment customerFamilyFragment = (CustomerFamilyFragment) getChildFragmentManager().findFragmentByTag(CustomerFamilyFragment.class.getName());
                    customerFamilyFragment.loadSPPA();
                    break;
            }

            CustomerDetailFragment customerDetailFragment = (CustomerDetailFragment) getChildFragmentManager().findFragmentByTag(CustomerDetailFragment.class.getName());
            customerDetailFragment.loadSPPA();

            BeneficiaryFragment beneficiaryFragment = (BeneficiaryFragment) getChildFragmentManager().findFragmentByTag(BeneficiaryFragment.class.getName());
            beneficiaryFragment.loadSPPA();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public boolean saveSPPA() {

        try {
            if (!validate()) return false;

            String subProductCode = getSubProductCode();
            String idOrFam = getIdOrFam(subProductCode);

            individuFragment.saveSPPA();
            detailFragment.saveSPPA();
            beneficiaryFragment.saveSPPA();

            if (idOrFam.equalsIgnoreCase(var.Family)) {
                individuFragment.saveTertanggungUtama();
                familyFragment.saveSPPA();
            }
            boolean valid = validateFamily();
            return valid;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean validateFamily() {
        if (familyFragment != null) {
            return familyFragment.validate();
        }
        return true;
    }

    private boolean validate() {
        return (individuFragment.validate()
                && detailFragment.validate()
                && beneficiaryFragment.validate());
    }


    @Override
    public void scrollTo(View view) {
        super.scrollTo(view);
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_customer_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case  R.id.action_change_date:
                GeneralSetting.insert(var.GENERAL_SETTING_EDIT_CONFIRMATION, String.valueOf(true));
                Intent intent = new Intent(context, EditConfirmationActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
