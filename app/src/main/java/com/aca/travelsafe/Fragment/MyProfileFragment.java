package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.aca.travelsafe.BaseActivity;
import com.aca.travelsafe.Dal.LoginDal;
import com.aca.travelsafe.Dal.SaveSppa;
import com.aca.travelsafe.Interface.DrawerListener;
import com.aca.travelsafe.Interface.LoginListener;
import com.aca.travelsafe.Interface.ScrollToListener;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Retrofit.TravelService;
import com.aca.travelsafe.SignInActivity;
import com.aca.travelsafe.Util.UtilPassword;
import com.aca.travelsafe.Util.UtilView;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.Result;
import com.aca.travelsafe.database.Setvar;
import com.aca.travelsafe.database.User;
import com.aca.travelsafe.database.UserDetail;
import com.raizlabs.android.dbflow.sql.language.Select;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static com.aca.travelsafe.Util.UtilView.isEmpty;
import static com.aca.travelsafe.Util.UtilView.setError;

public class MyProfileFragment extends BaseFragment
        implements
        LoginListener,
        ScrollToListener {

    @Bind(R.id.txtEmail)
    EditText txtEmail;
    @Bind(R.id.txtPassword)
    EditText txtPassword;
    @Bind(R.id.txtRetypePassword)
    EditText txtRetypePassword;
    @Bind(R.id.btnUpdate)
    Button btnUpdate;
    @Bind(R.id.frameHeader)
    FrameLayout frameHeader;
    @Bind(R.id.frameDetail)
    FrameLayout frameDetail;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @Bind(R.id.viewParent)
    RelativeLayout viewParent;
    @Bind(R.id.imgLock)
    ImageView imgLock;
    @Bind(R.id.imgLockRetype)
    ImageView imgLockRetype;
    @Bind(R.id.layBottom)
    LinearLayout layBottom;
    @Bind(R.id.lblEmail)
    TextInputLayout lblEmail;
    @Bind(R.id.lblPassword)
    TextInputLayout lblPassword;
    @Bind(R.id.lblReTypePassword)
    TextInputLayout lblReTypePassword;
    @Bind(R.id.viewScroll)
    NestedScrollView viewScroll;
    private CustomerIndividuFragment individuFragment;
    private CustomerDetailFragment detailFragment;

    PasswordHolder passwordHolder;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    public static MyProfileFragment newInstance() {

        Bundle args = new Bundle();

        MyProfileFragment fragment = new MyProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        ButterKnife.bind(this, view);
        initFragment();
        setScrollView(viewScroll);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(false);
            swipeRefresh.invalidate();
        }
    }


    private void initFragment() {
        setFragment(frameHeader.getId(), CustomerIndividuFragment.newInstance());
        setFragment(frameDetail.getId(), CustomerDetailFragment.newInstance());

        individuFragment = (CustomerIndividuFragment) getChildFragmentManager().findFragmentByTag(CustomerIndividuFragment.class.getName());
        detailFragment = (CustomerDetailFragment) getChildFragmentManager().findFragmentByTag(CustomerDetailFragment.class.getName());
    }

    private void getData() {
        try {
            btnUpdate.setEnabled(false);
            swipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefresh.setRefreshing(true);

                    User user = new Select().from(User.class).querySingle();
                    LoginDal loginDal = new LoginDal(MyProfileFragment.this);
                    loginDal.login(user);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void login(boolean status) {

        try {
            swipeRefresh.setRefreshing(false);

            if (status) {
                loadUser();
                btnUpdate.setEnabled(true);
            } else {
                Snackbar.make(getView(), R.string.message_failed_get_account_data, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", loginRetry())
                        .show();
                Intent intent = new Intent(context, SignInActivity.class);
                startActivity(intent);
                BaseActivity.class.cast(getActivity()).transitionSlideEnter();
                ((BaseActivity) getActivity()).popupFragment();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loginError(String message) {
        try {
            swipeRefresh.setRefreshing(false);
            Snackbar.make(getView(), R.string.message_failed_login, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", loginRetry())
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private View.OnClickListener loginRetry() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        };
    }


    public void saveMain() {
        User user;

        try {
            user = new Select().from(User.class).querySingle();

            if (user == null) {
                user = new User();
            }

            user.UserId = txtEmail.getText().toString();
            user.UserPassword = txtPassword.getText().toString();
            user.save();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadMain() {
        User user;

        try {
            user = new Select().from(User.class).querySingle();
            if (user == null)
                return;

            txtEmail.setText(user.UserId);
            txtPassword.setText(user.UserPassword);
            txtRetypePassword.setText(user.UserPassword);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadUser() {
        loadMain();
        individuFragment.loadUser();
        detailFragment.loadUser();

    }

    public void saveUser() {
        saveMain();
        individuFragment.saveUser();
        detailFragment.saveUser();

    }

    @OnClick(R.id.btnUpdate)
    public void performUpdate() {
        try {
            if (!(validate() &&
                    individuFragment.validate() &&
                    detailFragment.validate()))
                return;

            swipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefresh.setRefreshing(true);
                    btnUpdate.setEnabled(false);

                    saveUser();
                    update();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*****VALIDATE*****/

    private void resetErrorState(){
        UtilView.setErrorNull(lblEmail);
        UtilView.setErrorNull(lblPassword);
        UtilView.setErrorNull(lblReTypePassword);
    }

    private boolean validate() {
        resetErrorState();

        if (!validateEmptyField()) return false;
        if (!validateEmail()) return false;
        if (!validatePassword()) return false;

        return true;
    }


    private boolean validateEmptyField() {
        boolean valid = thisValidateEmptyField()
                        && individuFragment.validateEmptyField()
                        && detailFragment.validateEmptyField();

        if (!valid)
            Snackbar.make(getView(), R.string.message_validate_empty_field, Snackbar.LENGTH_SHORT).show();

        return valid;
    }

    private boolean thisValidateEmptyField() {
        if (
                !isEmpty(txtEmail, lblEmail)
                && !isEmpty(txtPassword, lblPassword)
                && !isEmpty(txtRetypePassword, lblReTypePassword)) {
            return true;
        }
        scrollTo(txtRetypePassword);
        return false;
    }

    private boolean validateEmail() {
        String email = txtEmail.getText().toString().trim();
        String at = "@";
        String[] splitEmail = email.split(at);

        boolean valid = false;

        if (splitEmail == null)
            valid = false;
        else  if (splitEmail.length < 2)
            valid = false;
        else if (splitEmail[0].length() > 0 && splitEmail[1].length() > 0)
            valid = true;

        if(!valid) {
            scrollTo(txtEmail);
            lblEmail.setError(context.getString(R.string.message_validate_email));
            lblEmail.setErrorEnabled(true);
        }


        return valid;
    }


    private boolean validatePassword() {
        String pass = txtPassword.getText().toString();
        int maxLength = Integer.parseInt(Setvar.getValue(var.MaxPassLength));
        int minLength = Integer.parseInt(Setvar.getValue(var.MinPassLength));

        if (pass.length() < minLength || pass.length() > maxLength) {
            setError(lblPassword, String.format(getString(R.string.message_validate_password_length),minLength, maxLength));
            scrollTo(txtRetypePassword);

            return false;
        }

        if (!UtilPassword.isValidPassword(pass)) {
            setError(lblPassword, getString(R.string.message_validate_password_complexity));
            scrollTo(txtRetypePassword);

            return false;
        }

        if (!txtPassword.getText().toString().equals(txtRetypePassword.getText().toString())) {
            setError( lblPassword, getString(R.string.message_validate_match_password));
            setError( lblReTypePassword, getString(R.string.message_validate_match_password));
            scrollTo(txtRetypePassword);
            return false;
        }
        return true;
    }

    private void update() {
        User user;
        UserDetail userDetail;

        try {
            user = new Select().from(User.class).querySingle();
            userDetail = new Select().from(UserDetail.class).querySingle();

            Observable.zip(
                    TravelService.createUserService(null).updateMain(user).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()),
                    TravelService.createUserService(null).updateDetail(userDetail).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()),
                    new Func2<Result, Result, Object>() {
                        @Override
                        public Object call(Result result, Result result2) {
                            if (result.message.equalsIgnoreCase(var.TRUE) && result2.message.equalsIgnoreCase(var.TRUE)) {
                                Snackbar.make(getView(), R.string.message_profile_updated, Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(getView(), R.string.message_failed_update, Snackbar.LENGTH_SHORT).show();
                            }

                            return null;
                        }
                    }
            )

                    .subscribe(new Observer<Object>() {
                        @Override
                        public void onCompleted() {
                            swipeRefresh.setRefreshing(false);
                            btnUpdate.setEnabled(true);

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            Snackbar.make(getView(), R.string.message_failed_update, Snackbar.LENGTH_SHORT).show();

                            swipeRefresh.setRefreshing(false);
                            btnUpdate.setEnabled(true);
                        }


                        @Override
                        public void onNext(Object o) {

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DrawerListener) {
            this.context = (Activity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected void init(View view) {
        txtEmail.setEnabled(false);
        passwordHolder = new PasswordHolder();
        SaveSppa.clearSppa();
    }

    @Override
    protected void registerListener() {
        imgLock.setOnClickListener(passClickListener(0, imgLock, txtPassword));
        imgLockRetype.setOnClickListener(passClickListener(1, imgLockRetype, txtRetypePassword));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
            }
        });

    }

    private View.OnClickListener passClickListener(final int order, final ImageView imgLock, final EditText txtPassword) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               First time click visibility is to be true
                boolean visible = !passwordHolder.passVisible[order];
                passwordHolder.passVisible[order] = visible;

                int imgRes = visible ? R.drawable.ic_visibility : R.drawable.ic_visibility_off;
                imgLock.setImageDrawable(getActivity().getResources().getDrawable(imgRes));

                txtPassword.setInputType(visible
                        ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        : 129);
                txtPassword.setSelection(txtPassword.getText().length());

            }
        };
    }



    @Override
    public void scrollTo(View view) {
//        super.scrollTo(view);
        viewScroll.smoothScrollTo(0, view.getBottom());
    }


    private class PasswordHolder {
        boolean[] passVisible;

        public PasswordHolder() {
            passVisible = new boolean[2];
            this.passVisible[0] = false;
            this.passVisible[1] = false;
        }
    }
}
