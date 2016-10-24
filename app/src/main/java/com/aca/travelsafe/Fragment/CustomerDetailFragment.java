package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.text.BoringLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.aca.travelsafe.Binding.CityBind;
import com.aca.travelsafe.Interface.ScrollToListener;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Util.UtilView;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.GeneralSetting;
import com.aca.travelsafe.database.SppaInsured;
import com.aca.travelsafe.database.User;
import com.aca.travelsafe.database.UserDetail;
import com.raizlabs.android.dbflow.sql.language.Select;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.aca.travelsafe.Util.UtilView.inputTextWatcher;
import static com.aca.travelsafe.Util.UtilView.isEmpty;

public class CustomerDetailFragment extends BaseFragment {


    @Bind(R.id.txtAddress)
    EditText txtAddress;
    @Bind(R.id.lblAddress)
    TextInputLayout lblAddress;
    @Bind(R.id.spnCity)
    AppCompatSpinner spnCity;
    @Bind(R.id.txtPhone)
    EditText txtPhone;
    @Bind(R.id.lblPhone)
    TextInputLayout lblPhone;
    @Bind(R.id.txtMobile)
    EditText txtMobile;
    @Bind(R.id.lblMobile)
    TextInputLayout lblMobile;
    @Bind(R.id.lytParent)
    LinearLayout lytParent;
    private ScrollToListener mListener;
    private ViewHolder holder;

    public CustomerDetailFragment() {
    }


    public static CustomerDetailFragment newInstance() {

        Bundle args = new Bundle();

        CustomerDetailFragment fragment = new CustomerDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int resLayout;
        String parameterValue = GeneralSetting.getParameterValue(var.GENERAL_SETTING_IS_POLIS_ACTIVITY);

        if (parameterValue.equalsIgnoreCase(var.TRUE))
            resLayout = R.layout.fragment_polis_customer_detail;
        else
            resLayout = R.layout.fragment_customer_detail;

        View view = inflater.inflate(resLayout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ScrollToListener) {
            mListener = (ScrollToListener) context;
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
    }

    @Override
    protected void registerListener() {
//        txtMobile.addTextChangedListener(inputTextWatcher(lblMobile));
//        txtPhone.addTextChangedListener(inputTextWatcher(lblMobile));
//        txtAddress.addTextChangedListener(inputTextWatcher(lblMobile));
    }

    public void disableView() {
        disable(lytParent);
    }

    public UserDetail getUser() {
        try {
            UserDetail userDetail;
            userDetail = new Select().from(UserDetail.class).querySingle();

            if (userDetail == null) {
                userDetail = new UserDetail();
            }

            userDetail.UserId = "";
            userDetail.MobilePhoneNo = txtMobile.getText().toString();
            userDetail.Address = txtAddress.getText().toString();
            userDetail.City = CityBind.getID(spnCity);
            userDetail.Phone = txtPhone.getText().toString();
            userDetail.Email = "";

            User user;
            user = new Select().from(User.class).querySingle();

            if (user == null) {
                userDetail.save();
                return userDetail;
            }

            userDetail.UserId = user.UserId;
            userDetail.Email = user.UserId;
            userDetail.CreateBy = user.UserId;
            userDetail.ModifyBy = user.UserId;

            userDetail.save();

            return userDetail;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveUser() {
        UserDetail userDetail;

        try {
            userDetail = new Select().from(UserDetail.class).querySingle();
            if (userDetail == null) {
                userDetail = new UserDetail();
            }
            userDetail.Address = txtAddress.getText().toString();
            userDetail.City = CityBind.getID(spnCity);
            userDetail.Phone = txtPhone.getText().toString();
            userDetail.MobilePhoneNo = txtMobile.getText().toString();
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

            txtAddress.setText(userDetail.Address);
            CityBind.select(spnCity, userDetail.City);
            txtPhone.setText(userDetail.Phone);
            txtMobile.setText(userDetail.MobilePhoneNo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loadSPPA() {
        try {
            SppaInsured sppaInsured = new Select().from(SppaInsured.class).querySingle();

            if (sppaInsured != null) {

                txtAddress.setText(sppaInsured.getAddress(sppaInsured.Address));
                CityBind.select(spnCity, sppaInsured.CityId);
                txtPhone.setText(sppaInsured.PhoneNo);
                txtMobile.setText(sppaInsured.MobileNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean validate() {
        removeErrorState();

        if (!validateEmptyField()) return false;

        return true;

    }

    public boolean validateEmptyField() {
        boolean valid = (
                   !isEmpty(txtAddress, lblAddress)
                && !isEmpty(txtMobile, lblMobile)
//                && !isEmpty(txtPhone, lblPhone)
        );

        if (!valid) {
            Snackbar.make(getView(), R.string.message_validate_empty_field, Snackbar.LENGTH_SHORT).show();
//            mListener.scrollTo(txtMobile);
        }
        return valid;
    }

    public void removeErrorState() {
        UtilView.setErrorNull(lblAddress);
        UtilView.setErrorNull(lblMobile);
        UtilView.setErrorNull(lblPhone);
    }

    public void saveSPPA() {
        try {
            SppaInsured sppaInsured = new Select().from(SppaInsured.class).querySingle();

            if (sppaInsured == null)
                sppaInsured = new SppaInsured();

            sppaInsured.Address = sppaInsured.setAddress(txtAddress.getText().toString());
            sppaInsured.CityId = CityBind.getID(spnCity);
            sppaInsured.PhoneNo = txtPhone.getText().toString();
            sppaInsured.MobileNo = txtMobile.getText().toString();
            sppaInsured.Email = "";
            sppaInsured.save();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveState() {
        holder = new ViewHolder();

        holder.address = txtAddress.getText().toString();
        holder.city = CityBind.getID(spnCity);
        holder.phone = txtPhone.getText().toString();
        holder.mobileNo = txtMobile.getText().toString();
        holder.email = "";
    }

    public void loadState() {
        if (holder == null)
            return;

        txtAddress.setText(holder.address);
        txtPhone.setText(holder.phone);
        txtMobile.setText(holder.mobileNo);
        CityBind.select(spnCity, holder.city);
    }

    public class ViewHolder {
        String address,
                city,
                phone,
                mobileNo,
                email;

        public ViewHolder() {
            address = "";
            city = "";
            phone = "";
            mobileNo = "";
            email = "";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
