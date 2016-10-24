package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aca.travelsafe.BaseActivity;
import com.aca.travelsafe.Dal.Policy;
import com.aca.travelsafe.Dal.Scalar;
import com.aca.travelsafe.PaymentActivity;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Retrofit.TravelPolisService;
import com.aca.travelsafe.Retrofit.TravelService;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.Utility;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.Result;
import com.aca.travelsafe.database.SppaMain;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.joda.time.LocalDate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FillPaymentFragment extends BaseFragment {

    @Bind(R.id.lblCaption)
    TextView lblCaption;
    @Bind(R.id.btnPayClickAca)
    FrameLayout btnPayClickAca;
    @Bind(R.id.btnBack)
    Button btnBack;
    @Bind(R.id.viewButtonNavigation)
    LinearLayout viewButtonNavigation;
    @Bind(R.id.rbPayKlikAca)
    RadioButton rbPayKlikAca;
    @Bind(R.id.btnNext)
    Button btnNext;
    private OnFragmentInteractionListener mListener;
    private ProgressDialog progressDialog;

    public FillPaymentFragment() {
        // Required empty public constructor
    }

    public static FillPaymentFragment newInstance() {

        Bundle args = new Bundle();

        FillPaymentFragment fragment = new FillPaymentFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fill_payment, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getString(R.string.Payment));
        setHasOptionsMenu(true);
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    @Override
    protected void init(View view) {

    }

    @Override
    protected void registerListener() {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_payment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pay_later:
                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btnBack)
    public void btnBackClick() {
        ((BaseActivity) getActivity()).popupFragment();
    }

    @OnClick(R.id.btnNext)
    public void btnNextClick() {
        if (!validasi()) return;

        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        startActivityForResult(intent, getResources().getInteger(R.integer.request_code_payment));
    }

    @OnClick(R.id.btnPayClickAca)
    public void btnPayKlikAca() {
        SppaMain sppaMain;

        if (!validasi()) return;

        try {
            sppaMain = new Select().from(SppaMain.class).querySingle();
            sppaMain.SppaStatus = var.SUBMITTED;


            TravelService.createSPPAService(null)
                    .sppaMainAdd(sppaMain)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Observer<List<Result>>() {
                        @Override
                        public void onCompleted() {
                        }


                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), R.string.message_failed_paid, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(List<Result> results) {
                            boolean succeed;
                            String message;
                            String detail = null;

                            if (results.get(0) != null) {
                                message = results.get(0).message;
                                detail = results.get(0).detail;

                                if (message.equalsIgnoreCase(var.TRUE)) {
                                    succeed = true;
                                } else {
                                    succeed = false;
                                }
                            } else {
                                succeed = false;
                            }

                            if (succeed) {
                                Toast.makeText(getActivity(), R.string.message_succeed_pay, Toast.LENGTH_SHORT).show();
                                context.finish();
                            } else {
                                Toast.makeText(getActivity(), "Paid failed due to " + detail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validasi() {
        try {
            if (!rbPayKlikAca.isChecked()) {
                createSnackbar(R.string.message_validate_pick_payment);
                return false;
            }

            String exp = Scalar.getExpiredDate();
            LocalDate expDate = UtilDate.toDate(exp);
//            if (expDate.isAfter(UtilDate.getDate())) {
//                Snackbar.make(getView(), R.string.message_validate_arrival_date, Snackbar.LENGTH_SHORT).show();
//                return false;
//            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    /*    if (requestCode == getResources().getInteger(R.integer.request_code_payment) &&
            resultCode == getActivity().RESULT_OK) {
            fetchSppaMain();
        }*/
        fetchSppaMain();
    }

    private void stopProgressDialog(){
        progressDialog.dismiss();
    }

    private void fetchSppaMain() {
        SppaMain sppaMain = new Select().from(SppaMain.class).querySingle();
        if (sppaMain == null) {
            return;
        }
        progressDialog = Utility.showProgressDialog(context);
        TravelService
                .createSPPAService(null)
                .sppaMain(sppaMain.SppaNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fetchSppaMainObserver());
    }

    private Observer<? super List<SppaMain>> fetchSppaMainObserver() {
        return new Observer<List<SppaMain>>() {
            @Override
            public void onCompleted() {
                stopProgressDialog();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                stopProgressDialog();
            }

            @Override
            public void onNext(List<SppaMain> sppaMains) {
                if (sppaMains == null || sppaMains.size() == 0) {
                    debugLog(getString(R.string.message_null));
                    createToast(R.string.message_failed_load_data);
                    return;
                }
                String paymentStatus = sppaMains.get(0).KdPaymentStatus;
                if (TextUtils.isEmpty(paymentStatus))return;
                if (!paymentStatus.equalsIgnoreCase(var.GAGAL_BAYAR)) {
//                    Policy.sendEmail(sppaMains.get(0).SppaNo, sppaMains.get(0).CreateBy);
                    createToast(context.getString(R.string.message_caption_sending_policy));
                    getActivity().setResult(getResources().getInteger(R.integer.result_code_payment));
                    getActivity().finish();
                    return;
                }
            }
        };
    }

}
