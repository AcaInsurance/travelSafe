package com.aca.travelsafe;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.aca.travelsafe.Dal.Policy;
import com.aca.travelsafe.Dal.SaveDraft;
import com.aca.travelsafe.Fragment.BeneficiaryFragment;
import com.aca.travelsafe.Fragment.CustomerFamilyFragment;
import com.aca.travelsafe.Fragment.FillConfirmationFragment;
import com.aca.travelsafe.Fragment.FillCustomerDetailFragment;
import com.aca.travelsafe.Fragment.FillInsuranceDetailFragment;
import com.aca.travelsafe.Fragment.FillPaymentFragment;
import com.aca.travelsafe.Fragment.MyPolisFragment;
import com.aca.travelsafe.Fragment.TermAndConditionFragment;
import com.aca.travelsafe.Fragment.TripAnnualFragment;
import com.aca.travelsafe.Fragment.TripDomestikFragment;
import com.aca.travelsafe.Fragment.TripRegularFragment;
import com.aca.travelsafe.Interface.ScrollToListener;
import com.aca.travelsafe.Interface.SyncListener;
import com.aca.travelsafe.Util.UtilService;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.GeneralSetting;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FillInsuredActivity extends BaseActivity
        implements
        FillCustomerDetailFragment.OnFragmentInteractionListener,
        FillInsuranceDetailFragment.OnFragmentInteractionListener,
        FillConfirmationFragment.OnFragmentInteractionListener,
        FillPaymentFragment.OnFragmentInteractionListener,
        CustomerFamilyFragment.OnFragmentInteractionListener,
        TripAnnualFragment.OnFragmentInteractionListener,
        TripDomestikFragment.OnFragmentInteractionListener,
        TripRegularFragment.OnFragmentInteractionListener,
        BeneficiaryFragment.OnFragmentInteractionListener,
        SyncListener,
        ScrollToListener,
        TermAndConditionFragment.OnFragmentInteractionListener

{

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.swrLayout)
    SwipeRefreshLayout swrLayout;
    @Bind(R.id.fragment)
    FrameLayout fragment;

    private String sppaNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_inssured);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable drawable = getResources().getDrawable(R.drawable.ic_close);
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(drawable);

        if (getIntent() != null && getIntent().getExtras() != null) {
            sppaNo = getIntent().getExtras().getString(var.SPPA_NO);
            if (sppaNo != null && !sppaNo.isEmpty())
                getSppa(sppaNo);
        } else {
            initFragment();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerListener();
    }


    private void registerListener() {
        swrLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swrLayout.setRefreshing(false);
            }
        });
    }

    private void getSppa(final String sppaNo) {
        try {
            Log.d("sppa", sppaNo);
            if (!UtilService.isOnline(this)) {
                Snackbar.make(getWindow().getDecorView().findViewById(var.ACTIVITY_VIEW), R.string.message_no_connection, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getSppa(sppaNo);
                            }
                        }).show();
                return;
            }
            swrLayout.post(new Runnable() {
                @Override
                public void run() {
                    swrLayout.setRefreshing(true);

                    SaveDraft saveDraft = new SaveDraft(sppaNo, FillInsuredActivity.this, swrLayout);
                    saveDraft.saveAll();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void syncSucceed(boolean status) {
        swrLayout.setRefreshing(false);

        if (status) {
            initFragment();
        } else {
            retry();
        }
    }

    @Override
    public void syncFailed(String message) {
        swrLayout.setRefreshing(false);
        retry();

    }

    private void retry() {
        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.message_failed_load_data), Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSppa(sppaNo);
                    }
                })
                .show();
    }

    protected void initFragment() {
        if (GeneralSetting.getParameterValue(var.GENERAL_SETTING_IS_POLIS_ACTIVITY).equalsIgnoreCase(var.TRUE))
            setFragmentNoBackStack(R.id.fragment, MyPolisFragment.newInstance());
        else
            setFragmentNoBackStack(R.id.fragment, FillCustomerDetailFragment.newInstance());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                showCloseDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (popupFragment())
            return;

        showCloseDialog();
    }

    private void showCloseDialog() {
        if (Policy.isPaid()) {
            supportFinishAfterTransition();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
                .setMessage(getString(R.string.message_dialog_confirm_close))
                .setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        supportFinishAfterTransition();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (swrLayout != null) {
            swrLayout.setRefreshing(false);
            swrLayout.invalidate();
        }

    }

    @Override
    public void scrollTo(View view) {

    }

}
