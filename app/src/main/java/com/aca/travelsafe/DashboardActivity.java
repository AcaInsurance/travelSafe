package com.aca.travelsafe;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aca.travelsafe.Fragment.CustomerDetailFragment;
import com.aca.travelsafe.Fragment.DashboardFragment;
import com.aca.travelsafe.Fragment.MyDraftFragment;
import com.aca.travelsafe.Fragment.MyPurchaseFragment;
import com.aca.travelsafe.Fragment.PromoFragment;
import com.aca.travelsafe.Interface.DrawerListener;
import com.aca.travelsafe.Retrofit.TravelService;
import com.aca.travelsafe.Util.UtilImage;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.GcmMapping;
import com.aca.travelsafe.database.GeneralSetting;
import com.aca.travelsafe.database.Login;
import com.aca.travelsafe.database.MasterPromo;
import com.aca.travelsafe.database.Setvar;
import com.aca.travelsafe.database.User;
import com.aca.travelsafe.database.UserDetail;
import com.aca.travelsafe.gcm.QuickstartPreferences;
import com.aca.travelsafe.gcm.RegistrationIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.messaging.FirebaseMessaging;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DashboardActivity extends BaseActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        DrawerListener

{
    public static final int nav_home = R.id.nav_home;
    public static final int nav_log_in = R.id.nav_log_in;
    public static final int nav_profile = R.id.nav_profile;
    public static final int nav_draft = R.id.nav_draft;
    public static final int nav_my_purchase = R.id.nav_my_purchase;
    public static final int nav_promo = R.id.nav_promo;
    public static final int nav_product = 1213;
    public static final int nav_log_out = R.id.nav_log_out;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    @Bind(R.id.viewParent)
    CoordinatorLayout viewParent;
    @Bind(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @Bind(R.id.fragment)
    FrameLayout fragment;
    @Bind(R.id.btnGetToken)
    Button btnGetToken;
    private Toolbar toolbar;


    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;
    private Handler handler;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        handler = new Handler();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setSelectHome();
        }

//        mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
        showPromo();
        GeneralSetting.deleteAll();

    }

    @OnClick(R.id.btnGetToken)
    public void btnGetTokenClick() {
//        registerToken();
        if (checkPlayServices()) {
            FirebaseMessaging.getInstance().subscribeToTopic("news");
            Log.d(TAG, "Subscribed to news topic");
//            Log.d(TAG, "InstanceID token: " + FirebaseInstanceId.getInstance().getToken());
        }

    }


    private void registerToken() {
        try {
            mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
            mRegistrationProgressBar.setVisibility(View.VISIBLE);
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        mRegistrationProgressBar.setVisibility(View.GONE);
                        SharedPreferences sharedPreferences =
                                PreferenceManager.getDefaultSharedPreferences(context);
                        boolean sentToken = sharedPreferences
                                .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                        if (sentToken) {
                            Toast.makeText(DashboardActivity.this, "Token sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DashboardActivity.this, "Token error", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            Intent intent = new Intent(DashboardActivity.this, RegistrationIntentService.class);
            startService(intent);


//            if (checkPlayServices()) {
//                Intent intent = new Intent(DashboardActivity.this, RegistrationIntentService.class);
//                startService(intent);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    @Override
    public void setAppBarBackground(boolean transparent) {
        appBarLayout.setBackgroundColor(getResources().getColor(
                transparent ? R.color.colorToolbarTransparentBlack
                        : R.color.colorPrimary));
    }

    private void setSelectHome() {
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().performIdentifierAction(R.id.nav_home, 0);
        navigationView.getMenu().getItem(0).setChecked(true);
        setTitle(getString(R.string.Dashboard));
    }

    private void setSelectMyPurchase() {
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().performIdentifierAction(navigationView.getMenu().getItem(4).getItemId(), 0);
        navigationView.getMenu().getItem(4).setChecked(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getResources().getInteger(R.integer.request_code_payment) &&
                resultCode == getResources().getInteger(R.integer.result_code_payment)) {
            setSelectMyPurchase();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNavLogin();

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

    }

    @Override
    public void setSelectedDrawer(int navItem) {
        navigationView.getMenu().findItem(navItem).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else if (count > 1) {
                popupFragment();
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        final int id = item.getItemId();
        final String[] title = {""};

        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (navigationView.getMenu().findItem(id).isChecked())
            return false;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                switch (id) {
                    case nav_home:
                        setAppBarBackground(true);
                        setFragment(R.id.fragment, DashboardFragment.newInstance());
                        title[0] = getString(R.string.Dashboard);

                        break;

                    case nav_log_in:

                        Intent intent = new Intent(DashboardActivity.this, SignInActivity.class);
                        startActivity(intent);
                        transitionSlideEnter();
                        break;

                    case nav_profile:
                        intent = new Intent(DashboardActivity.this, MyProfileActivity.class);
                        startActivity(intent);
                        break;

                    case nav_draft:
                        if (!isLogin())
                            break;

                        setAppBarBackground(false);
                        setFragment(R.id.fragment, MyDraftFragment.newInstance());
                        title[0] = getString(R.string.my_draft);

                        break;

                    case nav_my_purchase:

                        if (!isLogin())
                            break;

                        setAppBarBackground(false);
                        setFragment(R.id.fragment, MyPurchaseFragment.newInstance());
                        title[0] = getString(R.string.my_purchase);
                        break;

                    case nav_product:
                        setFragment(R.id.fragment, CustomerDetailFragment.newInstance());
                        title[0] = getString(R.string.Product);

                        break;

                    case nav_promo:
                        setFragment(R.id.fragment, PromoFragment.newInstance());
                        title[0] = getString(R.string.promo);
//                showPromo();

                        break;
                    case nav_log_out:
//                performLogout();
                        Delete.table(Login.class);
                        Delete.table(User.class);
                        Delete.table(UserDetail.class);
                        showNavLogin();
                        Snackbar.make(viewParent, R.string.message_action_sign_out, Snackbar.LENGTH_SHORT).show();
                        setSelectHome();
                        break;
                }
                setTitle(title[0]);

            }
        }, 250);
        return true;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    public void showNavLogin() {
        MenuItem login = navigationView.getMenu().findItem(R.id.nav_log_in);
        MenuItem profile = navigationView.getMenu().findItem(R.id.nav_profile);
        MenuItem logout = navigationView.getMenu().findItem(R.id.nav_log_out);

        boolean isLogin = Login.isLogin();

        login.setVisible(!isLogin);
        profile.setVisible(isLogin);
        logout.setVisible(isLogin);
    }


    private void performLogout() {

        try {
            GcmMapping gcmMapping = new Select().from(GcmMapping.class).querySingle();
            gcmMapping.IsLogin = String.valueOf(false);

            TravelService
                    .createGCMService(null)
                    .submit(gcmMapping)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(logoutObserver());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Observer<? super GcmMapping> logoutObserver() {
        return new Observer<GcmMapping>() {
            @Override
            public void onCompleted() {

                Delete.table(Login.class);
                Delete.table(User.class);
                Delete.table(UserDetail.class);
                showNavLogin();
                Snackbar.make(viewParent, R.string.message_action_sign_out, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                doOnFailed();
            }

            @Override
            public void onNext(GcmMapping gcmMapping) {
                if (gcmMapping != null) {
                    Delete.table(GcmMapping.class);
                    gcmMapping.save();
                } else {
                    doOnFailed();
                }
            }

            private void doOnFailed() {
                Snackbar.make(viewParent, R.string.message_failed_signup, Snackbar.LENGTH_SHORT).show();
            }
        };
    }

    private void showPromo() {
        TravelService.createPromoService(null)
                .getPopup()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<MasterPromo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(MasterPromo masterPromo) {
                        if(masterPromo == null)return;

                        String imageName = masterPromo.PromoImage;
                        String imageUrlPre = Setvar.getValue(var.UrlPromoImage);
                        String imageUrl = imageUrlPre + imageName;

                        Dialog dialog = UtilImage.popupImage(DashboardActivity.this, imageUrl, getString(R.string.message_caption_getting_promo));
                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                setSelectHome();
                            }
                        });
                    }
                });
    }

}
