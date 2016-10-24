package com.aca.travelsafe.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aca.travelsafe.Dal.LoginDal;
import com.aca.travelsafe.R;
import com.aca.travelsafe.SignInActivity;
import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.Login;

import butterknife.ButterKnife;
import butterknife.OnClick;


public abstract class BaseFragment extends Fragment {
    public Activity context;


    private Snackbar snackbar;
    private NestedScrollView viewScroll;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        registerListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    protected abstract void init(View view);

    protected abstract void registerListener();


    public boolean setFragment(int container, Fragment fragment, int transition, String tag, int backStack) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (transition) {
            case var.TRANSITION_NONE:
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                break;

            case var.TRANSITION_SLIDE:
                ft.setCustomAnimations(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right
                );
                break;

            case var.TRANSITION_FADE:
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                break;

            case var.TRANSITION_MASK:
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                break;
        }
        if (!fm.popBackStackImmediate(tag, 0) && fm.findFragmentByTag(tag) == null) {
            ft.replace(container, fragment, tag);
//            if (backStack == var.BACKSTACK)
            ft.addToBackStack(tag);
            ft.commit();

            return fm.executePendingTransactions();
        }
        return false;
    }

    public void setFragmentNoBackStack(int container, Fragment fragment) {
        setFragment(container, fragment, var.TRANSITION_NONE, fragment.getClass().getName(), var.NO_BACKSTACK);
    }


    public void setFragment(int container, Fragment fragment) {
        setFragment(container, fragment, var.TRANSITION_NONE, fragment.getClass().getName(), var.BACKSTACK);
    }

    public void setFragment(FrameLayout container, Fragment fragment) {
        setFragment(container.getId(), fragment, var.TRANSITION_NONE, fragment.getClass().getName(), var.BACKSTACK);
    }

    public void setFragment(FrameLayout container, Fragment fragment, String tag) {
        setFragment(container.getId(), fragment, var.TRANSITION_NONE, tag, var.BACKSTACK);
    }


    public void setFragment(int container, Fragment fragment, int transition) {
        setFragment(container, fragment, transition, fragment.getClass().getName(), var.BACKSTACK);
    }


    protected void disable (ViewGroup parentLayout) {
        parentLayout.setEnabled(false);
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View child = parentLayout.getChildAt(i);
            if (child instanceof ViewGroup) {
                disable((ViewGroup) child);
            } else {
                child.setEnabled(false);
            }
        }
    }

    public boolean isLogin () {

        if (Login.isLogin())
            return true;

        Intent intent = new Intent(getActivity(), SignInActivity.class);
        startActivity(intent);
        return false;
    }

    public void debugLog(String logMessage) {
        Log.d(getClass().getSimpleName(), logMessage);
    }

    public void debugLog(int resId) {
        debugLog(getString(resId));
    }


    public void createToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void createToast(int resId) {
        createToast(getString(resId));
    }

    public void createToastLong(int resId) {
        Toast.makeText(getContext(), getString(resId), Toast.LENGTH_LONG).show();
    }


    public Snackbar createSnackbar(int resId){
        return createSnackbar(getString(resId));
    }


    public Snackbar createSnackbar(String message){
        snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT);
        snackbar.show();
        return snackbar;
    }


    public Snackbar createSnackbar(String message, boolean indefinite){
        snackbar = Snackbar.make(getView(), message, indefinite ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_SHORT);
        snackbar.show();
        return snackbar;
    }

    public Snackbar createSnackbar(String message, String action, View.OnClickListener listener){
        snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(action, listener);
        snackbar.show();
        return snackbar;
    }
    public Snackbar createSnackbar(int resIdMessage, int resIdAction, View.OnClickListener listener){
        return createSnackbar(getString(resIdMessage), getString(resIdAction), listener);
    }

    /*
    Getter setter
    */

    public Snackbar getSnackbar() {
        return snackbar;
    }

    public void scrollTo(View view) {
        viewScroll.smoothScrollTo(0, view.getBottom());
    }

    public void setScrollView(NestedScrollView scrollView) {
        this.viewScroll = scrollView;
    }

}
