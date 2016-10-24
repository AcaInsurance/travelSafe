package com.aca.travelsafe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.aca.travelsafe.Util.var;
import com.aca.travelsafe.database.Login;

/**
 * Created by Marsel on 14/3/2016.
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    public void setFragment(int container, Fragment fragment, int transition, String tag, int backStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (transition) {
            case var.TRANSITION_NONE:
                ft.setTransition(FragmentTransaction.TRANSIT_NONE);
                break;

            case var.TRANSITION_SLIDE:
                ft.setCustomAnimations (android.R.anim.slide_out_right, android.R.anim.slide_in_left);
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

            if (backStack == var.BACKSTACK)
                ft.addToBackStack(tag);

            ft.commit();
            fm.executePendingTransactions();

        }
    }

    public void setFragmentNoBackStack(int container, Fragment fragment) {
        setFragment(container, fragment, var.TRANSITION_NONE, fragment.getClass().getName(), var.NO_BACKSTACK);
    }


    public void setFragment(int container, Fragment fragment) {
        setFragment(container, fragment, var.TRANSITION_NONE, fragment.getClass().getName(), var.BACKSTACK);
    }

    public void setFragment(int container, Fragment fragment, int transition) {
        setFragment(container, fragment, transition, fragment.getClass().getName(), var.BACKSTACK);
    }

    public boolean popupFragment () {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            int count = fm.getBackStackEntryCount();
            Log.d("BaseActivity", "popupFragment count " + count);
            return true;
        }
        return false;
    }


    /******
     * SHOW PROGRESS
     *******/

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show, final View formView, final View progressView) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

                formView.setVisibility(show ? View.GONE : View.VISIBLE);
                formView.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        formView.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

                progressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                progressView.animate().setDuration(shortAnimTime).alpha(
                        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                    }
                });
            } else {
                progressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                formView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }


    public boolean isLogin () {
        if (Login.isLogin())
            return true;

        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        transitionSlideEnter();
        return false;
    }

    public void transitionSlideExit() {
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
    public void transitionSlideEnter() {
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
}
