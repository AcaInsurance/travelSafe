package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.method.NumberKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aca.travelsafe.BaseActivity;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Retrofit.TravelService;
import com.aca.travelsafe.SignUpActivity;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.UtilService;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.GcmMapping;
import com.aca.travelsafe.database.Login;
import com.aca.travelsafe.database.Result;
import com.aca.travelsafe.database.User;
import com.aca.travelsafe.database.UserDetail;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;


public class SignInFragment extends BaseFragment {
    @Bind(R.id.txtUserID)
    EditText txtUserID;
    @Bind(R.id.lblEmail)
    TextInputLayout lblEmail;
    @Bind(R.id.txtpassword)
    EditText txtpassword;
    @Bind(R.id.lblPassword)
    TextInputLayout lblPassword;
    @Bind(R.id.btnForgotPassword)
    Button btnForgotPassword;
    @Bind(R.id.btnLogin)
    Button btnLogin;
    @Bind(R.id.btnCreateAccount)
    Button btnCreateAccount;
    @Bind(R.id.login_progress)
    ProgressBar loginProgress;
    @Bind(R.id.email_login_form)
    LinearLayout emailLoginForm;
    @Bind(R.id.lblCopyRight)
    TextView lblCopyRight;
    private SignInListener mListener;
    private Subscription subsUserData;
    private Subscription subsLogin;

    public SignInFragment() {
    }

    public static SignInFragment newInstance() {
        SignInFragment fragment = new SignInFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getString(R.string.Sign_in));

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    protected void init(View view) {
//        txtUserID.setText("marsel.widjaja.it@acains.com");
//        txtpassword.setText("1234");
    }

    @Override
    protected void registerListener() {

    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        try {
            this.context = context;
            mListener = (SignInListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement onFragmentINteraction");
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

        if (subsUserData != null)
            subsUserData.unsubscribe();

        if (subsLogin != null) {
            subsLogin.unsubscribe();
        }
    }

    public interface SignInListener {
        void afterLogin();
    }

    @OnClick(R.id.btnCreateAccount)
    public void createAccount() {
        Intent intent = new Intent(getActivity(), SignUpActivity.class);
        getActivity().startActivity(intent);
        BaseActivity.class.cast(getActivity()).transitionSlideEnter();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getActivity().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context).toBundle());
//        }
//        else
//            getActivity().startActivity(intent);
    }

    private void animateProgress(boolean show) {
        btnLogin.setEnabled(!show);
        btnLogin.setVisibility(!show ? View.VISIBLE : View.GONE);
        loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.btnLogin)
    public void btnLoginClick() {
        if (!UtilService.isOnline(context)) {
            Toast.makeText(context, R.string.message_no_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        final String email = txtUserID.getText().toString().trim();
        final String pass = txtpassword.getText().toString();

        animateProgress(true);

        User user = new User();
        user.UserId = email;
        user.UserPassword = pass;

        doLogin(user);
    }

    private void doLogin(final User user) {

        subsLogin = TravelService.createUserService(null)
                .userLogin(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<Result>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        createToast(R.string.message_failed_login);
                        animateProgress(false);
                    }

                    @Override
                    public void onNext(Result result) {
                        animateProgress(false);

                        if (result == null) {
                            createToast(R.string.message_failed_login);
                            return;
                        }
                        result.log();

                        if (result.message.equalsIgnoreCase(var.TRUE)) {
                            Delete.table(Login.class);

                            Login login = new Login();
                            login.UserId = user.UserId;
                            login.UserPass = user.UserPassword;
                            login.LoginStatus = Login.LOGIN;
                            login.save();

                            getUserData();
                        } else if (result.message.equalsIgnoreCase(context.getString(R.string.message_validate_user_not_active))) {
                            sendVerificationEmail(user.UserId);

                        } else {
                            animateProgress(false);
                            createToast(result.message);
                        }
                    }

                    private void sendVerificationEmail(final String userId) {
                        try {
                            final AlertDialog.Builder builder =
                                    new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                            builder.setTitle(context.getString(R.string.message_validate_user_not_active));
                            builder.setMessage(context.getString(R.string.message_action_send_link_activation));
                            builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    createToast(R.string.message_caption_verification_sent_to_email);
                                    TravelService.createUserService(null)
                                            .activation(userId)
                                            .subscribeOn(Schedulers.newThread())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe();
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
                });
    }

    private void getUserData() {
        GcmMapping gcmMapping = new Select().from(GcmMapping.class).querySingle();

//        if (gcmMapping == null) gcmMapping = new GcmMapping();
//        gcmMapping.RegisteredToken = "testtoken";
//        gcmMapping.UserId = Login.getUserID();
//        gcmMapping.IsLogin = String.valueOf(true);
//        gcmMapping.save();


        animateProgress(true);
        subsUserData = Observable.zip(
//                TravelService.createGCMService(null).submit(gcmMapping).subscribeOn(Schedulers.newThread()),
                TravelService.createUserService(null).userMain(Login.getUserID()).subscribeOn(Schedulers.newThread()),
                TravelService.createUserService(null).userDetail(Login.getUserID()).subscribeOn(Schedulers.newThread()),
                new Func2<
                        //                        GcmMapping,
                        User,
                        UserDetail, Object>() {
                    @Override
                    public Object call(
//                            GcmMapping gcmMapping,
                            User user,
                            UserDetail detail) {

                        Delete.table(User.class);
                        Delete.table(UserDetail.class);
//                        Delete.table(GcmMapping.class);

//                        if (gcmMapping != null) gcmMapping.save();
                        if (user != null) user.save();
                        if (detail != null) {
                            detail.DOB = UtilDate.parseUTC(detail.DOB).toString(UtilDate.ISO_DATE);
                            detail.save();
                        }

                        return true;
                    }


                }
        )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onCompleted() {
                        animateProgress(false);
                        mListener.afterLogin();

                        context.finish();
                        ((BaseActivity) context).transitionSlideExit();

                        String userName = new Select().from(UserDetail.class).querySingle().Name;
                        createToast("Welcome back, " + userName);
                    }


                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        animateProgress(false);
                    }


                    @Override
                    public void onNext(Object o) {

                    }
                });
    }

    @OnClick(R.id.btnForgotPassword)
    public void btnForgotPasswordClick() {

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);
        final ViewHolder holder = new ViewHolder(view);

        final Dialog dialog = new Dialog(context, R.style.AppCompatAlertDialogStyle);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        holder.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = holder.txtEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    createToast(R.string.message_validate_empty_field);
                    return;
                }
                dialog.dismiss();
                createToast(context.getString(R.string.message_caption_new_password_sent_to_email));

                TravelService.createUserService(null)
                        .forgotPassword(email)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(forgotPasswordObserver());

            }

            private Observer<? super Result> forgotPasswordObserver() {
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
                        if (result == null) return;
                        Result.log(result);
                        createToast(result.detail);
                    }
                };
            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    static class ViewHolder {
        @Bind(R.id.txtEmail)
        TextInputEditText txtEmail;
        @Bind(R.id.btnSend)
        Button btnSend;
        @Bind(R.id.btnCancel)
        Button btnCancel;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
