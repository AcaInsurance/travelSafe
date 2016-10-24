package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aca.travelsafe.BaseActivity;
import com.aca.travelsafe.Dal.ExchangeRateDal;
import com.aca.travelsafe.Dal.LoginDal;
import com.aca.travelsafe.Dal.Policy;
import com.aca.travelsafe.Dal.Premi;
import com.aca.travelsafe.Dal.SaveSppa;
import com.aca.travelsafe.Dal.Scalar;
import com.aca.travelsafe.Dal.SubmitSppa;
import com.aca.travelsafe.EditConfirmationActivity;
import com.aca.travelsafe.Holder.PolicyHolder;
import com.aca.travelsafe.Holder.PremiHolder;
import com.aca.travelsafe.Interface.LoginListener;
import com.aca.travelsafe.Interface.SyncListener;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Retrofit.TravelService;
import com.aca.travelsafe.SignInActivity;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.UtilMath;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.GeneralSetting;
import com.aca.travelsafe.database.Promo;
import com.aca.travelsafe.database.Promo_Table;
import com.aca.travelsafe.database.Result;
import com.aca.travelsafe.database.SppaMain;
import com.aca.travelsafe.database.User;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.joda.time.LocalDate;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FillConfirmationFragment extends BaseFragment implements SyncListener, ExchangeRateDal.ExchangeRateDalListener, LoginListener {

    @Bind(R.id.txtCoverage)
    TextView txtCoverage;
    @Bind(R.id.txtPlan)
    TextView txtPlan;
    @Bind(R.id.lblPeriode)
    TextView lblPeriode;
    @Bind(R.id.txtPeriode)
    TextView txtPeriode;
    @Bind(R.id.txtDestination)
    TextView txtDestination;
    @Bind(R.id.txtTotalPremium)
    TextView txtTotalPremium;
    @Bind(R.id.txtAdditionalPremium)
    TextView txtAdditionalPremium;
    @Bind(R.id.txtLoadingPremium)
    TextView txtLoadingPremium;
    @Bind(R.id.txtPolicyCharge)
    TextView txtPolicyCharge;
    @Bind(R.id.txtTotalPaid)
    TextView txtTotalPaid;
    @Bind(R.id.btnBack)
    Button btnBack;
    @Bind(R.id.btnNext)
    Button btnNext;
    @Bind(R.id.viewButtonNavigation)
    LinearLayout viewButtonNavigation;
    @Bind(R.id.txtDays)
    TextView txtDays;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @Bind(R.id.txtZone)
    TextView txtZone;
    @Bind(R.id.pbLoading)
    ProgressBar pbLoading;
    @Bind(R.id.viewLoading)
    RelativeLayout viewLoading;
    @Bind(R.id.txtPromoCode)
    EditText txtPromoCode;
    @Bind(R.id.btnUsePromo)
    Button btnUsePromo;
    @Bind(R.id.lblPromoCode)
    TextInputLayout lblPromoCode;
    @Bind(R.id.txtPersenDiskon)
    TextView txtPersenDiskon;
    @Bind(R.id.txtDiscountAmount)
    TextView txtDiscountAmount;
    @Bind(R.id.viewDiscount)
    LinearLayout viewDiscount;
    @Bind(R.id.viewParent)
    LinearLayout viewParent;
    @Bind(R.id.btnClearPromo)
    Button btnClearPromo;
    @Bind(R.id.btnChangePeriode)
    ImageView btnChangePeriode;
    @Bind(R.id.txtTotalPaidInIdr)
    TextView txtTotalPaidInIdr;
    @Bind(R.id.lblTotalPaidInIdr)
    TextView lblTotalPaidInIdr;


    private OnFragmentInteractionListener mListener;
    private PremiHolder premiHolder;
    private PolicyHolder policyHolder;

    private Snackbar snackbar;

    private Subscription subsExchangeRate;
    private Subscription subsLogin;
    private Subscription subsSubmitSppa;

    private boolean isPolisActivity;

    public FillConfirmationFragment() {
        // Required empty public constructor
    }

    public static FillConfirmationFragment newInstance() {
        Bundle args = new Bundle();

        FillConfirmationFragment fragment = new FillConfirmationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if (GeneralSetting.getParameterValue(var.GENERAL_SETTING_IS_POLIS_ACTIVITY).equalsIgnoreCase(var.TRUE)) {
            view = inflater.inflate(R.layout.fragment_polis_fill_confirmation, container, false);
            isPolisActivity = true;
        } else {
            view = inflater.inflate(R.layout.fragment_fill_confirmation, container, false);
            getActivity().setTitle(getString(R.string.Confirmation));
            isPolisActivity = false;
        }
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            getExchangeRate();
            disableView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.context = (Activity) context;
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement onFragmentINteraction");
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

        if (subsExchangeRate != null) subsExchangeRate.unsubscribe();
        if (subsLogin != null) subsLogin.unsubscribe();
        if (subsSubmitSppa != null) subsSubmitSppa.unsubscribe();
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(false);
            swipeRefresh.invalidate();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (snackbar != null) {
            snackbar.dismiss();
        }
        if (getSnackbar() != null) {
            getSnackbar().dismiss();
        }

        GeneralSetting.delete(var.GENERAL_SETTING_SIGN_UP_TRIGGER);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    protected void init(View view) {
        btnNext.setVisibility(!Policy.isPaid() ? View.VISIBLE : View.GONE);
        if (isPolisActivity) btnChangePeriode.setVisibility(View.GONE);
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

    private void setPromo() {
        try {
            premiHolder.resetPotongan();
            txtPromoCode.setText("");

            SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();
            if (TextUtils.isEmpty(sppaMain.PromoCode)) return;

            Promo promo = new Select().from(Promo.class)
                    .where(Promo_Table.PromoCode.eq(sppaMain.PromoCode))
                    .querySingle();

            if (promo == null) {
                return;
            }
            premiHolder.setPotongan(promo.PromoAmount);
            txtPromoCode.setText(promo.PromoCode);
            txtPromoCode.setEnabled(false);
            btnClearPromo.setVisibility(View.VISIBLE);
            btnUsePromo.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/*

    private void usePromo(boolean use) {
        try {
            Promo promo = new Select().from(Promo.class).querySingle();

            if (!use || promo == null) {
                Delete.table(Promo.class);
                txtPromoCode.setText("");

                viewDiscount.setVisibility(View.GONE);
                btnClearPromo.setVisibility(View.GONE);

                return;
            }

            viewDiscount.setVisibility(View.VISIBLE);
            btnClearPromo.setVisibility(View.VISIBLE);

            String persenDiskon = String.valueOf(promo.PromoAmount) + "%";
            txtPersenDiskon.setText(persenDiskon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

    @OnClick(R.id.btnUsePromo)
    public void btnUsePromoClick() {
        String promoCode = txtPromoCode.getText().toString();

        if (TextUtils.isEmpty(promoCode)) {
            setPremi(premiHolder);
            return;
        }

        TravelService.createPromoService(null)
                .use(promoCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(usePromoObserver());

    }

    private Observer<? super Promo> usePromoObserver() {
        return new Observer<Promo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Promo promo) {
                lblPromoCode.setError(null);
                lblPromoCode.setErrorEnabled(false);

                if (promo.PromoCode == null) {
                    debugLog(R.string.message_null);
                    lblPromoCode.setError(context.getString(R.string.message_validation_invalid_promo_code));
                    lblPromoCode.setErrorEnabled(true);

                    removePromo();
                    setPromo();
                    setPremi(premiHolder);
                    return;
                }

                savePromo(promo);
                setPromo();
                setPremi(premiHolder);
                btnClearPromo.setVisibility(View.VISIBLE);
                btnUsePromo.setVisibility(View.GONE);
                txtPromoCode.setEnabled(false);
            }
        };
    }

    private void removePromo() {
        try {
            Delete.table(Promo.class);

            SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();
            sppaMain.PromoCode = null;
            sppaMain.DiscRate = String.valueOf(0.0);
            sppaMain.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void savePromo(Promo promo) {
        try {
            Delete.table(Promo.class);
            promo.save();

            SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();
            sppaMain.PromoCode = promo.PromoCode;
            sppaMain.DiscRate = String.valueOf(promo.PromoAmount);
            sppaMain.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnClearPromo)
    public void btnClearPromoClick() {
        TravelService.createPromoService(null)
                .remove(txtPromoCode.getText().toString())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(clearPromoObserver());
    }

    private Observer<? super Result> clearPromoObserver() {
        return new Observer<Result>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Result result) {
                if (result == null) {
                    debugLog(R.string.message_null);
                }
                Result.log(result);

                removePromo();
                setPromo();
                setPremi(premiHolder);

                btnClearPromo.setVisibility(View.GONE);
                btnUsePromo.setVisibility(View.VISIBLE);
                txtPromoCode.setEnabled(true);
            }
        };
    }

    @OnClick(R.id.btnBack)
    public void goBack() {
        ((BaseActivity) getActivity()).popupFragment();
    }

    @OnClick(R.id.btnNext)
    public void goNext() {
        if (!validate()) return;
        cekLogin();
    }

    private boolean validate() {
        LocalDate beginDate = UtilDate.toDate(Scalar.getBeginDate());

        if (beginDate.isBefore(UtilDate.getDate())) {
            createSnackbar(R.string.message_validate_invalid_date);
            return false;
        }
    /*    if (!validateFlightDate()) {
            createToast("Please change date of flight detail");
            getActivity().onBackPressed();
            return false;
        }*/
        return true;
    }


    private void cekLogin() {
        disableNavigation(true);
        createSnackbar(context.getString(R.string.message_action_checking_login), true);

        User user = new Select().from(User.class).querySingle();
        LoginDal loginDal = new LoginDal(FillConfirmationFragment.this);
        subsLogin = loginDal.login(user);
    }


    @Override
    public void login(boolean status) {
        try {
            getSnackbar().dismiss();
            disableNavigation(false);

            if (status) {
                saveSPPA();
                sendSPPA();
            } else {
                GeneralSetting.insert(var.GENERAL_SETTING_SIGN_UP_TRIGGER, getActivity().getClass().getName());

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
            disableNavigation(false);
            getSnackbar().dismiss();

            createSnackbar(getString(R.string.message_failed_login),
                    getString(R.string.retry),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cekLogin();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getExchangeRate() {
        try {
            showProgress();
            ExchangeRateDal exchangeRateDal = new ExchangeRateDal(context);
            exchangeRateDal.mListener = this;

            if (Policy.isPaid()) {
                loadRateComplete();
            } else
                subsExchangeRate = exchangeRateDal.getExchangeRate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void saveSPPA() {
        try {
            SaveSppa saveSppa = new SaveSppa();
            saveSppa.saveSPPAMain(premiHolder, policyHolder);
            saveSppa.saveSPPAInsured();
            saveSppa.saveSPPABeneficiary();
            saveSppa.saveSPPADestination();
            saveSppa.saveSPPADomestic();
            saveSppa.saveSPPAFamily();
            saveSppa.saveSPPAFlight();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendSPPA() {
        try {
            disableNavigation(true);
            createSnackbar(getString(R.string.message_action_sending), true);
            SubmitSppa submitSppa = new SubmitSppa(FillConfirmationFragment.this);
            subsSubmitSppa = submitSppa.sendSppaMain();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void syncSucceed(boolean status) {
        try {
            disableNavigation(false);
            getSnackbar().dismiss();

            if (status) {
//                ((BaseActivity) context).setFragment(R.id.fragment, FillPaymentFragment.newInstance());
                ((BaseActivity) context).setFragment(R.id.fragment, TermAndConditionFragment.newInstance());
            } else {
                createSnackbar(getString(R.string.message_failed_submit_sppa)
                        , getString(R.string.retry)
                        , sendListener());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void syncFailed(String message) {
        try {
            getSnackbar().dismiss();
            disableNavigation(false);

            createSnackbar(getString(R.string.message_failed_submit_sppa)
                    , getString(R.string.retry)
                    , sendListener());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void disableNavigation(boolean disable) {
        btnNext.setEnabled(!disable);
        btnBack.setEnabled(!disable);
    }

    private void disableView() {
        if (Policy.isPaid()) disable(viewParent);
    }

    private View.OnClickListener sendListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSPPA();
            }
        };
    }

    private void loadPolicy() {
        try {
            Policy policy = new Policy();
            policyHolder = policy.fillPolicy();

            txtCoverage.setText(policyHolder.coverage + " - " + policyHolder.type);
            txtPlan.setText(policyHolder.plan);
            txtDays.setText(policyHolder.days);
            txtPeriode.setText(policyHolder.periode);
            txtDestination.setText(policyHolder.destination);
            txtZone.setText(policyHolder.zone);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPremi() {
        try {
            Premi premi = new Premi();

            if (Policy.isPaid())
                premiHolder = premi.loadPremi();
            else
                premiHolder = premi.countPremi();

            setPromo();
            setPremi(premiHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPremi(PremiHolder premiHolder) {
        double basicPremi = this.premiHolder.premiBasic;
        double addPremi = this.premiHolder.getAddPremi();
        double loadingPremi = this.premiHolder.premiLoading;
        double charge = this.premiHolder.getTotalCharge();
        double totalPremi = this.premiHolder.getTotal();
        double totalPremiInIdr = this.premiHolder.getTotalInIdr();
        double totalDiskon = this.premiHolder.getDiskonAmount();
        double persenDiskon = this.premiHolder.getDiskon();

        String currency = this.premiHolder.currency;


        txtTotalPremium.setText(currency + " " + UtilMath.toString(basicPremi));
        txtAdditionalPremium.setText(currency + " " + UtilMath.toString(addPremi));
        txtLoadingPremium.setText(currency + " " + UtilMath.toString(loadingPremi));
        txtPolicyCharge.setText(currency + " " + UtilMath.toString(charge));
        txtTotalPaid.setText(currency + " " + UtilMath.toString(totalPremi));
        txtDiscountAmount.setText(currency + " " + UtilMath.toString(totalDiskon));
        txtPersenDiskon.setText(persenDiskon + "%");
        txtTotalPaidInIdr.setText("IDR " + UtilMath.toString(totalPremiInIdr));

        if (currency.equalsIgnoreCase(var.IDR)) {
            hidePremirupiah();
        }

        showDiskon(persenDiskon);
    }

    private void hidePremirupiah(){
        txtTotalPaidInIdr.setVisibility(View.GONE);
        lblTotalPaidInIdr.setVisibility(View.GONE);
    }
    private void showDiskon(double persenDiskon) {
        if (persenDiskon != 0.0) {
            viewDiscount.setVisibility(View.VISIBLE);
        } else {
            viewDiscount.setVisibility(View.GONE);
        }
    }

    @Override
    public void loadRateError(String message) {
        hideProgress();
        snackbar = createSnackbar(R.string.message_failed_get_exchange_rate, R.string.retry, getExhangeRateOnClick());
    }

    @Override
    public void loadRateComplete() {
        loadPolicy();
        loadPremi();
        hideProgress();
    }

    public View.OnClickListener getExhangeRateOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExchangeRate();
            }
        };
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void hideProgress() {
        viewLoading.setVisibility(View.GONE);
        swipeRefresh.setVisibility(View.VISIBLE);
    }

    public void showProgress() {
        viewLoading.setVisibility(View.VISIBLE);
        swipeRefresh.setVisibility(View.GONE);
    }


    @OnClick(R.id.btnChangePeriode)
    public void btnChangePeriodeClick() {
        GeneralSetting.insert(var.GENERAL_SETTING_EDIT_CONFIRMATION, String.valueOf(true));
        Intent intent = new Intent(context, EditConfirmationActivity.class);
        startActivity(intent);
    }
}
