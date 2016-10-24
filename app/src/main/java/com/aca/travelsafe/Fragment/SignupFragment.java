package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.aca.travelsafe.BaseActivity;
import com.aca.travelsafe.Dal.SaveSppa;
import com.aca.travelsafe.Interface.ScrollToListener;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Retrofit.TravelService;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.UtilPassword;
import com.aca.travelsafe.Util.UtilView;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.Widget.SwipeContainerWidget;
import com.aca.travelsafe.database.GeneralSetting;
import com.aca.travelsafe.database.Result;
import com.aca.travelsafe.database.Setvar;
import com.aca.travelsafe.database.User;
import com.aca.travelsafe.database.UserDetail;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.aca.travelsafe.Util.UtilView.isEmpty;
import static com.aca.travelsafe.Util.UtilView.setError;

public class SignupFragment extends BaseFragment
        implements
        ScrollToListener {

    @Bind(R.id.txtEmail)
    EditText txtEmail;
    @Bind(R.id.txtPassword)
    EditText txtPassword;
    @Bind(R.id.txtRetypePassword)
    EditText txtRetypePassword;
    @Bind(R.id.frameCustomerDetail)
    FrameLayout frameCustomerDetail;
    @Bind(R.id.btnSignUp)
    Button btnSignUp;
    @Bind(R.id.frameCustomerHeader)
    FrameLayout frameCustomerHeader;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    PasswordHolder passwordHolder;
    @Bind(R.id.imgLock)
    ImageView imgLock;
    @Bind(R.id.imgLockRetype)
    ImageView imgLockRetype;
    @Bind(R.id.lblEmail)
    TextInputLayout lblEmail;
    @Bind(R.id.lblPassword)
    TextInputLayout lblPassword;
    @Bind(R.id.lblReTypePassword)
    TextInputLayout lblReTypePassword;
    @Bind(R.id.viewScroll)
    NestedScrollView viewScroll;

    private OnFragmentInteractionListener mListener;
    private Subscription subscribe;
    private CustomerIndividuFragment headerFragment;
    private CustomerDetailFragment detailFragment;

    public SignupFragment() {
    }

    public static SignupFragment newInstance() {

        Bundle args = new Bundle();

        SignupFragment fragment = new SignupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        ButterKnife.bind(this, view);

        clearSppa();
        clearUser();
        setFragmentNoBackStack(R.id.frameCustomerHeader, CustomerIndividuFragment.newInstance());
        setFragmentNoBackStack(R.id.frameCustomerDetail, CustomerDetailFragment.newInstance());

        getActivity().setTitle(getString(R.string.Sign_up));
        setScrollView(viewScroll);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        loadUser();
    }

    private void loadUser() {
        CustomerIndividuFragment individuFragment = (CustomerIndividuFragment) getChildFragmentManager().findFragmentByTag(CustomerIndividuFragment.class.getName());
        CustomerDetailFragment detailFragment = (CustomerDetailFragment) getChildFragmentManager().findFragmentByTag(CustomerDetailFragment.class.getName());

        individuFragment.loadSPPA();
        detailFragment.loadSPPA();

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

        if (subscribe != null) {
            subscribe.unsubscribe();
        }

        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(false);
            swipeRefresh.invalidate();
        }
    }


    @Override
    protected void init(View view) {
        passwordHolder = new PasswordHolder();
        SwipeContainerWidget.create(swipeRefresh);
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

    private View.OnTouchListener passVisibleListener(final int order, final EditText view) {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != MotionEvent.ACTION_DOWN)
                    return false;

                Drawable drawable = ((EditText) v).getCompoundDrawables()[2];

                if (event.getX() >= v.getRight() - drawable.getBounds().width()) {
                    boolean visible = passwordHolder.passVisible[order];
                    passwordHolder.passVisible[order] = !visible;

                    Drawable d = visible ?
                            getActivity().getResources().getDrawable(R.drawable.ic_visibility) :
                            getActivity().getResources().getDrawable(R.drawable.ic_visibility_off);

                    view.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
                    view.setInputType(!visible
                            ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            : 129);

                }
                return false;
            }
        };
    }

    private void clearSppa() {
        String signUpTrigger = GeneralSetting.getParameterValue(var.GENERAL_SETTING_SIGN_UP_TRIGGER);

        if (!signUpTrigger.equalsIgnoreCase(var.SIGN_UP_TRIGGER_ACTIVITY))
            SaveSppa.clearSppa();
    }

    private void clearUser() {
        Delete.table(User.class);
        Delete.table(UserDetail.class);
    }


    public User saveUser() {
        User user;

        try {
            user = new Select().from(User.class).querySingle();

            if (user == null) user = new User();

            user.UserId = txtEmail.getText().toString();
            user.UserName = txtEmail.getText().toString();
            user.UserPassword = txtPassword.getText().toString();
            user.UserType = var.userCode;
            user.IsAllowSubmit = var.TRUE;
            user.IsAllowEndorsment = var.FALSE;
            user.RoleId = var.roleID;
            user.CreateBy = txtEmail.getText().toString();
            user.CreateDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);
            user.ModifyBy = txtEmail.getText().toString();
            user.ModifyDate = UtilDate.getDateTime().toString(UtilDate.ISO_DATE_TIME);
            user.IsActive = String.valueOf(true);
            user.save();

            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @OnClick(R.id.btnSignUp)
    public void btnSignUpClick() {
        try {
            headerFragment = (CustomerIndividuFragment) getChildFragmentManager().findFragmentByTag(CustomerIndividuFragment.class.getName());
            detailFragment = (CustomerDetailFragment) getChildFragmentManager().findFragmentByTag(CustomerDetailFragment.class.getName());

            if (!(validate() && headerFragment.validate() && detailFragment.validate()))
                return;

            final User user = saveUser();
            headerFragment.getUser();
            final UserDetail userDetail = detailFragment.getUser();

            swipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefresh.setRefreshing(true);
                    btnSignUp.setEnabled(false);

                    Observable<Result> postMain =
                            TravelService.createUserService(null).userMain(user)
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnNext(new Action1<Result>() {
                                        @Override
                                        public void call(Result result) {
                                            Result.log(result);
                                        }
                                    });

                    Observable<Result> postDetail = TravelService.createUserService(null)
                            .userDetail(userDetail)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnNext(new Action1<Result>() {
                                @Override
                                public void call(Result result) {
                                    swipeRefresh.setRefreshing(false);
                                    btnSignUp.setEnabled(true);
                                    Result.log(result);

                                    if (result == null) return;
                                    if (!result.message.equalsIgnoreCase(var.TRUE)) {
                                        createToast(result.message);
                                        return;
                                    }
                                    createToast(R.string.message_caption_verification_sent_to_email);
                                    getActivity().finish();
                                    BaseActivity.class.cast(getActivity()).transitionSlideExit();
                                }
                            });

                    Observable
                            .concat(postMain, postDetail)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<Result>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    swipeRefresh.setRefreshing(false);
                                    btnSignUp.setEnabled(true);
                                    e.printStackTrace();
                                    Toast.makeText(context, R.string.message_failed_signup, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNext(Result result) {

                                }
                            });

//
//                    subscribe = Observable.zip(
//                            TravelService.createUserService(null).userMain(user).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()),
//                            TravelService.createUserService(null).userDetail(userDetail).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()),
//                            new Func2<Result, Result, Object>() {
//                                @Override
//                                public Object call(Result result, Result result2) {
//                                    Result.log(result, result2);
//
//                                    if (result.message.equalsIgnoreCase(var.TRUE) &&
//                                        result2.message.equalsIgnoreCase(var.TRUE)) {
//
//                                        Toast.makeText(context, R.string.message_succeed_signup, Toast.LENGTH_SHORT).show();
//
//                                        getActivity().finish();
//                                        BaseActivity.class.cast(getActivity()).transitionSlideExit();
//                                    } else {
//                                        Snackbar.make(getView(), result.message, Snackbar.LENGTH_SHORT).show();
//                                    }
//
//                                    return null;
//                                }
//                            }
//                    )
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(new Observer<Object>() {
//                                @Override
//                                public void onCompleted() {
//                                    swipeRefresh.setRefreshing(false);
//                                    btnSignUp.setEnabled(true);
//                                }
//
//                                @Override
//                                public void onError(Throwable e) {
//                                    swipeRefresh.setRefreshing(false);
//                                    btnSignUp.setEnabled(true);
//                                    e.printStackTrace();
//
//                                    Toast.makeText(context, R.string.message_failed_signup, Toast.LENGTH_SHORT).show();
//                                }
//
//                                @Override
//                                public void onNext(Object o) {
//
//                                }
//                            });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*VALIDATE */

    private void resetErrorState() {
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
                && headerFragment.validateEmptyField()
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
        else if (splitEmail.length < 2)
            valid = false;
        else if (splitEmail[0].length() > 0 && splitEmail[1].length() > 0)
            valid = true;

        if (!valid) {
            scrollTo(txtEmail);
            lblEmail.setError(context.getString(R.string.message_validate_email));
            lblEmail.setErrorEnabled(true);
            createToast(context.getString(R.string.message_validate_email));
        }


        return valid;
    }

    private boolean validatePassword() {
        String pass = txtPassword.getText().toString();
        int maxLength = Integer.parseInt(Setvar.getValue(var.MaxPassLength));
        int minLength = Integer.parseInt(Setvar.getValue(var.MinPassLength));

        if (pass.length() < minLength || pass.length() > maxLength) {
            setError(lblPassword, String.format(getString(R.string.message_validate_password_length), minLength, maxLength));
            createToast(context.getString(R.string.message_validate_invalid_password));
            scrollTo(txtRetypePassword);

            return false;
        }

        if (!UtilPassword.isValidPassword(pass)) {
            setError(lblPassword, getString(R.string.message_validate_password_complexity));
            createToast(context.getString(R.string.message_validate_invalid_password));
            scrollTo(txtRetypePassword);

            return false;
        }

        if (!txtPassword.getText().toString().equals(txtRetypePassword.getText().toString())) {
            setError(lblPassword, getString(R.string.message_validate_match_password));
            setError(lblReTypePassword, getString(R.string.message_validate_match_password));
            scrollTo(txtRetypePassword);
            createToast(context.getString(R.string.message_validate_invalid_password));

            return false;
        }
        return true;
    }


    @Override
    public void scrollTo(View view) {
        super.scrollTo(view);
    }


    public interface OnFragmentInteractionListener {
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
