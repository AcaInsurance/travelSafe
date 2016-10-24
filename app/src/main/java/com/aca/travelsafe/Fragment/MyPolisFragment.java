package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aca.travelsafe.Dal.Policy;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.Setvar;
import com.aca.travelsafe.database.SppaFamily;
import com.aca.travelsafe.database.SppaMain;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.aca.travelsafe.Adapter.MyPurchaseAdapter.PolicyHeaderHolder;
import static com.aca.travelsafe.Adapter.MyPurchaseAdapter.getPolicyHeader;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyPolisFragment extends BaseFragment {

    @Bind(R.id.lblPolicyNo)
    TextView lblPolicyNo;
    @Bind(R.id.txtNoPolis)
    TextView txtNoPolis;
    @Bind(R.id.txtCoverage)
    TextView txtCoverage;
    @Bind(R.id.imgCalendar)
    ImageView imgCalendar;
    @Bind(R.id.txtDepartureDate)
    TextView txtDepartureDate;
    @Bind(R.id.lblUntil)
    TextView lblUntil;
    @Bind(R.id.txtArrivalDate)
    TextView txtArrivalDate;
    @Bind(R.id.imgPlane)
    ImageView imgPlane;
    @Bind(R.id.txtDestination)
    TextView txtDestination;
    @Bind(R.id.txtSppaDate)
    TextView txtSppaDate;
    @Bind(R.id.fragmentIndividu)
    FrameLayout fragmentIndividu;
    @Bind(R.id.fragmentFamily)
    FrameLayout fragmentFamily;
    @Bind(R.id.fragmentDetail)
    FrameLayout fragmentDetail;
    @Bind(R.id.fragmentBene)
    FrameLayout fragmentBene;
    @Bind(R.id.fragmentConfirmation)
    FrameLayout fragmentConfirmation;
    @Bind(R.id.cardView)
    CardView cardView;

    CustomerIndividuFragment individuFragment;
    CustomerDetailFragment detailFragment;
    CustomerFamilyFragment familyFragment;
    BeneficiaryFragment beneficiaryFragment;
    FillConfirmationFragment confirmationFragment;
    @Bind(R.id.txtStatusPolis)
    TextView txtStatusPolis;

    private MenuItem itemSend;


    public MyPolisFragment() {
    }

    public static MyPolisFragment newInstance() {
        Bundle args = new Bundle();

        MyPolisFragment fragment = new MyPolisFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_polis, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("Policy");
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    protected void init(View view) {
        initHeader();
        initFragment();
        hideFragment();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSppa();
        disableView();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    protected void registerListener() {

    }

    private void initHeader() {
        SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();
        if (sppaMain == null) return;

        String sppaNo = sppaMain.SppaNo;
        PolicyHeaderHolder headerHolder = getPolicyHeader(sppaNo);

        txtNoPolis.setText(headerHolder.txtNoPolis);
        txtStatusPolis.setText(headerHolder.txtStatusPolis);
        txtCoverage.setText(headerHolder.txtCoverage);
        txtSppaDate.setText(headerHolder.txtSppaDate);
        txtDepartureDate.setText(headerHolder.txtDepartureDate);
        txtArrivalDate.setText(headerHolder.txtArrivalDate);
        txtDestination.setText(headerHolder.txtDestination);
    }


    private void initFragment() {
        setFragment(fragmentIndividu.getId(), CustomerIndividuFragment.newInstance());
        setFragment(fragmentFamily.getId(), CustomerFamilyFragment.newInstance());
        setFragment(fragmentDetail.getId(), CustomerDetailFragment.newInstance());
        setFragment(fragmentBene.getId(), BeneficiaryFragment.newInstance());
        setFragment(fragmentConfirmation.getId(), FillConfirmationFragment.newInstance());

        individuFragment = (CustomerIndividuFragment) getChildFragmentManager().findFragmentByTag(CustomerIndividuFragment.class.getName());
        familyFragment = (CustomerFamilyFragment) getChildFragmentManager().findFragmentByTag(CustomerFamilyFragment.class.getName());
        detailFragment = (CustomerDetailFragment) getChildFragmentManager().findFragmentByTag(CustomerDetailFragment.class.getName());
        beneficiaryFragment = (BeneficiaryFragment) getChildFragmentManager().findFragmentByTag(BeneficiaryFragment.class.getName());
        confirmationFragment = (FillConfirmationFragment) getChildFragmentManager().findFragmentByTag(FillConfirmationFragment.class.getName());
    }

    private void hideFragment() {
        SppaFamily sppaFamily = new Select().from(SppaFamily.class).querySingle();

        if (sppaFamily == null)
            fragmentFamily.setVisibility(View.GONE);
    }

    private void loadSppa() {
        individuFragment.loadSPPA();
        familyFragment.loadSPPA();
        detailFragment.loadSPPA();
        beneficiaryFragment.loadSPPA();
    }


    private void disableView() {
        individuFragment.disableView();
        familyFragment.disableView();
        detailFragment.disableView();
        beneficiaryFragment.disableView();
    }

    public void sendEmail() {
        try {
            int delay = Integer.parseInt(Setvar.getValue(var.DelaySendEmail));
            SppaMain sppaMain = SppaMain.get();
            if (sppaMain == null) return;

            Policy.sendEmail(sppaMain.SppaNo, sppaMain.SppaSubmitBy);
            createToast(R.string.message_caption_sending_policy);

            itemSend.setEnabled(false);

            Observable
//                    .timer(delay, TimeUnit.MINUTES)
                    .timer(5, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            itemSend.setEnabled(true);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void dialogSendPolis() {
        try {
            final AlertDialog.Builder builder =
                    new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(context.getString(R.string.message_dialog_send_policy));
            builder.setPositiveButton(context.getString(R.string.send), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendEmail();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideItemSend () {
        if (Policy.isCancelAndVoid())
            itemSend.setVisible(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_my_polis, menu);
        itemSend = menu.findItem(R.id.action_send);
        hideItemSend();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                context.finish();
                break;

            case R.id.action_send:
                dialogSendPolis();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
