package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.aca.travelsafe.Adapter.MyPurchaseAdapter;
import com.aca.travelsafe.BaseActivity;
import com.aca.travelsafe.Dal.LoginDal;
import com.aca.travelsafe.DashboardActivity;
import com.aca.travelsafe.Interface.DrawerListener;
import com.aca.travelsafe.Interface.LoginListener;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Retrofit.TravelService;
import com.aca.travelsafe.SignInActivity;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.UtilService;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.Widget.RecyclerViewWidget;
import com.aca.travelsafe.database.GeneralSetting;
import com.aca.travelsafe.database.Login;
import com.aca.travelsafe.database.SppaDestinationDraft;
import com.aca.travelsafe.database.SppaDomesticDraft;
import com.aca.travelsafe.database.SppaMainDraft;
import com.aca.travelsafe.database.User;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MyPurchaseFragment extends BaseFragment implements LoginListener {


    @Bind(R.id.listMyPurchase)
    RecyclerView listMyPurchase;
    @Bind(R.id.btnFilter)
    Button btnFilter;
    @Bind(R.id.btnOrder)
    Button btnOrder;
    @Bind(R.id.viewButtonFooter)
    LinearLayout viewButtonFooter;
    @Bind(R.id.swrLayout)
    SwipeRefreshLayout swrLayout;
    @Bind(R.id.viewEmptyContent)
    RelativeLayout viewEmptyContent;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private DrawerListener mListener;
    private MyPurchaseAdapter adapter;
    private Parcelable parcelable;
    private Subscription subsGetPurchased;
    private Subscription subsDestination;

    public MyPurchaseFragment() {
    }

    public static MyPurchaseFragment newInstance() {
        Bundle args = new Bundle();

        MyPurchaseFragment fragment = new MyPurchaseFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.my_purchase));

        View view = inflater.inflate(R.layout.fragment_my_purchase, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DrawerListener) {
            this.context = (Activity) context;
            mListener = (DrawerListener) context;
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
        GeneralSetting.insert(var.GENERAL_SETTING_IS_POLIS_ACTIVITY, String.valueOf(true));
        RecyclerViewWidget.createNoDivider(context, listMyPurchase);
    }

    @Override
    protected void registerListener() {
        try {
            swrLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    parcelable = null;
                    swrLayout.setRefreshing(false);
                    getPurchased();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

        if (subsGetPurchased != null)
            subsGetPurchased.unsubscribe();

        if (subsDestination != null)
            subsDestination.unsubscribe();

        if (swrLayout != null) {
            swrLayout.setRefreshing(false);
        }
        GeneralSetting.delete(var.GENERAL_SETTING_IS_POLIS_ACTIVITY);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.my_purchase));
        getData();
        mListener.setSelectedDrawer(DashboardActivity.nav_my_purchase);
        GeneralSetting.insert(var.GENERAL_SETTING_IS_POLIS_ACTIVITY, String.valueOf(true));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MyPurchaseFragment", " call onpause ");
        parcelable = listMyPurchase.getLayoutManager().onSaveInstanceState();
    }



    private void getData() {
        try {
            if (!UtilService.isOnline(context)) {
                UtilService.showSnackbar(getView(), retryListener());
                return;
            }


            viewEmptyContent.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            User user = new Select().from(User.class).querySingle();
            LoginDal loginDal = new LoginDal(MyPurchaseFragment.this);
            loginDal.login(user);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void login(boolean status) {
        try {
            swrLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);

            if (status) {
                getPurchased();
            } else {
                Snackbar.make(getView(), R.string.message_failed_get_account_data, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", retryListener())
                        .show();
                Intent intent = new Intent(context, SignInActivity.class);
                startActivity(intent);
                ((BaseActivity) getActivity()).transitionSlideEnter();
                ((BaseActivity) getActivity()).popupFragment();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loginError(String message) {
        try {
            swrLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);

            Snackbar.make(getView(), R.string.message_failed_login, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", retryListener())
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private View.OnClickListener retryListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        };
    }


    private void getPurchased() {
        try {

          /*  swrLayout.post(new Runnable() {
                @Override
                public void run() {
                    swrLayout.setRefreshing(true);

                    subsGetPurchased = TravelService.createSPPAService(null)
                            .sppaMainPurchased(Login.getUserID())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.newThread())
                            .subscribe(new Observer<List<SppaMainDraft>>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    swrLayout.setRefreshing(false);
                                    e.printStackTrace();
                                    Snackbar.make(getView(), R.string.message_failed_load_data, Snackbar.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onNext(List<SppaMainDraft> sppaMainDrafts) {
                                    if (sppaMainDrafts == null || sppaMainDrafts.size() == 0) {
                                        swrLayout.setRefreshing(false);
                                        viewEmptyContent.setVisibility(View.VISIBLE);
                                        return;
                                    }

                                    List<String> sppaNo = new ArrayList<>();

                                    Delete.table(SppaMainDraft.class);
                                    for (SppaMainDraft s : sppaMainDrafts) {
                                        sppaNo.add(s.SppaNo);
                                        s.SppaDate = UtilDate.parseUTC(s.SppaDate).toString(UtilDate.ISO_DATE);
                                        s.EffectiveDate = UtilDate.parseUTC(s.EffectiveDate).toString(UtilDate.ISO_DATE);
                                        s.ExpireDate = UtilDate.parseUTC(s.ExpireDate).toString(UtilDate.ISO_DATE);
                                        s.save();
                                    }

                                    getDestination(sppaNo);

                                }
                            });

                }
            });*/

            progressBar.setVisibility(View.VISIBLE);
            subsGetPurchased = TravelService.createSPPAService(null)
                    .sppaMainPurchased(Login.getUserID())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Observer<List<SppaMainDraft>>() {
                        @Override
                        public void onCompleted() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            swrLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                            Snackbar.make(getView(), R.string.message_failed_load_data, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.retry, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getPurchased();
                                        }
                                    })
                                    .show();
                        }

                        @Override
                        public void onNext(List<SppaMainDraft> sppaMainDrafts) {
                            if (sppaMainDrafts == null || sppaMainDrafts.size() == 0) {
                                swrLayout.setRefreshing(false);
                                viewEmptyContent.setVisibility(View.VISIBLE);
                                return;
                            }

                            List<String> sppaNo = new ArrayList<>();

                            Delete.table(SppaMainDraft.class);
                            for (SppaMainDraft s : sppaMainDrafts) {
                                sppaNo.add(s.SppaNo);
                                s.SppaDate = UtilDate.parseUTC(s.SppaDate).toString(UtilDate.ISO_DATE);
                                s.EffectiveDate = UtilDate.parseUTC(s.EffectiveDate).toString(UtilDate.ISO_DATE);
                                s.ExpireDate = UtilDate.parseUTC(s.ExpireDate).toString(UtilDate.ISO_DATE);
                                s.save();
                            }

                            getDestination(sppaNo);

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getDestination(final List<String> sppaNo) {
        try {
            progressBar.setVisibility(View.VISIBLE);

            subsDestination = Observable.zip(
                    TravelService.createSPPAService(null).sppaDomesticDraft(sppaNo).subscribeOn(Schedulers.newThread()),
                    TravelService.createSPPAService(null).sppaDestinationDraft(sppaNo).subscribeOn(Schedulers.newThread()),
                    new Func2<List<SppaDomesticDraft>, List<SppaDestinationDraft>, Object>() {
                        @Override
                        public Object call(List<SppaDomesticDraft> sppaDomesticDrafts,
                                           List<SppaDestinationDraft> sppaDestinationDrafts) {

                            saveDestination(sppaDestinationDrafts);
                            saveDomestic(sppaDomesticDrafts);

                            return null;
                        }

                        private void saveDomestic(List<SppaDomesticDraft> sppaDomesticDrafts) {
                            if (sppaDomesticDrafts == null || sppaDomesticDrafts.size() == 0)
                                return;

                            Delete.table(SppaDomesticDraft.class);
                            for (SppaDomesticDraft s : sppaDomesticDrafts) {
                                s.save();
                            }
                        }

                        private void saveDestination(List<SppaDestinationDraft> sppaDestinationDrafts) {
                            if (sppaDestinationDrafts == null || sppaDestinationDrafts.size() == 0)
                                return;

                            Log.d("MyDraftFragment", " destionation list count " + sppaDestinationDrafts.size());
                            Delete.table(SppaDestinationDraft.class);
                            for (SppaDestinationDraft s : sppaDestinationDrafts) {
                                s.save();
                            }
                        }
                    }
            )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Object>() {
                        @Override
                        public void onCompleted() {
                            bindPurchased();
                            swrLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);
                        }


                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            swrLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(getView(), R.string.message_failed_load_data, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.retry, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getPurchased();
                                        }
                                    })
                                    .show();
                        }

                        @Override
                        public void onNext(Object o) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindPurchased() {
        try {
            adapter = new MyPurchaseAdapter(context, getView());
            listMyPurchase.setAdapter(adapter);

            if (parcelable != null)
                listMyPurchase.getLayoutManager().onRestoreInstanceState(parcelable);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
