package com.aca.travelsafe.Dal;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.aca.travelsafe.Fragment.DashboardFragment;
import com.aca.travelsafe.Fragment.FillConfirmationFragment;
import com.aca.travelsafe.Fragment.FillCustomerDetailFragment;
import com.aca.travelsafe.Fragment.MyDraftFragment;
import com.aca.travelsafe.Fragment.MyProfileFragment;
import com.aca.travelsafe.Fragment.MyPurchaseFragment;
import com.aca.travelsafe.Interface.LoginListener;
import com.aca.travelsafe.R;
import com.aca.travelsafe.Retrofit.TravelService;
import com.aca.travelsafe.Util.UtilDate;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.GcmMapping;
import com.aca.travelsafe.database.Login;
import com.aca.travelsafe.database.Result;
import com.aca.travelsafe.database.User;
import com.aca.travelsafe.database.UserDetail;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by marsel on 23/5/2016.
 */
public class LoginDal {
    private Fragment context;
    private LoginListener loginListener;

    public LoginDal(MyProfileFragment context) {
        loginListener = context;
        this.context = context;

    }

    public LoginDal(MyDraftFragment context) {
        loginListener = context;
        this.context = context;
    }

    public LoginDal(MyPurchaseFragment context) {
        loginListener = context;
        this.context = context;
    }

    public LoginDal(DashboardFragment context) {
        loginListener = context;
        this.context = context;

    }

    public LoginDal(FillConfirmationFragment context) {
        loginListener = context;
        this.context = context;
    }

    public Subscription login (final User user) {
        if(user == null) {
            loginListener.login(false);
            return null;
        }

        Subscription subscription = TravelService.createUserService(null)
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
                        loginListener.loginError(e.getMessage());
                    }

                    @Override
                    public void onNext(Result result) {
                        Log.d("SignInFragment", "onNext " + result.detail);
                        Log.d("SignInFragment", "onNext " + result.message);

                        Delete.table(Login.class);

                        if (result.message.equalsIgnoreCase(var.TRUE)) {

                            Login login = new  Login();
                            login.UserId = user.UserId;
                            login.UserPass = user.UserPassword;
                            login.LoginStatus = com.aca.travelsafe.database.Login.LOGIN;
                            login.save();

                            getUserData();
                        }
                        else {
                            loginListener.login(false);
                        }
                    }
                });

        return subscription;
    }


    private void concatmap(){
        final Observable<Result> resultObservable = TravelService.createUserService(null)
                .userLogin(null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread());

        resultObservable.concatMap(new Func1<Result, Observable<?>>() {
            @Override
            public Observable<?> call(Result result) {
                return resultObservable;
            }
        }).subscribe();
    }


    private void getUserData() {
        Observable.zip(
                TravelService.createUserService(null).userMain(com.aca.travelsafe.database.Login.getUserID()).subscribeOn(Schedulers.newThread()),
                TravelService.createUserService(null).userDetail(com.aca.travelsafe.database.Login.getUserID()).subscribeOn(Schedulers.newThread()),
                new Func2<User, UserDetail, Object>() {
                    @Override
                    public Object call(User user, UserDetail detail) {
                        Delete.table(User.class);
                        Delete.table(UserDetail.class);

                        user.save();

                        detail.DOB = UtilDate.parseUTC(detail.DOB).toString(UtilDate.ISO_DATE);
                        detail.save();

                        return true;
                    }
                }
        )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onCompleted() {
                        loginListener.login(true);

                    }


                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loginListener.loginError(e.getMessage());
                    }


                    @Override
                    public void onNext(Object o) {
                    }
                });
    }

}
