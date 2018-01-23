package com.dirusso.waves;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Matias Di Russo on 29/3/17.
 */
public class Navigator {

    /**
     * Navigate to fragment
     *
     * @param activity
     * @param fragment
     * @param containerId
     */
    public void navigateToFragment(AppCompatActivity activity, Fragment fragment, int containerId) {
        activity.getSupportFragmentManager().beginTransaction().replace(containerId, fragment).commit();
    }

    /**
     * Navigate to fragment
     *
     * @param activity
     * @param fragment
     * @param containerId
     * @param addToBackStack
     */
    public void navigateToFragment(AppCompatActivity activity, Fragment fragment, int containerId, String addToBackStack) {
        activity.getSupportFragmentManager().beginTransaction().replace(containerId, fragment).addToBackStack(addToBackStack).commit();
    }

    /**
     * Navigate to fragment
     *
     * @param activity
     * @param fragment
     * @param containerId
     * @param transition
     */
    public void navigateToFragment(AppCompatActivity activity, Fragment fragment, int containerId, int transition) {
        activity.getSupportFragmentManager().beginTransaction().replace(containerId, fragment).setTransition(transition).commit();
    }

//    /**
//     * Navigate to fragment
//     *
//     * @param activity
//     * @param fragment
//     * @param containerId
//     */
//    public void navigateToFragmentWithAnim(AppCompatActivity activity, Fragment fragment, int containerId) {
//        activity.getSupportFragmentManager().beginTransaction()
//                //.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_down)
//                .replace(containerId, fragment).commit();
//    }

    /**
     * Navigate to fragment
     *
     * @param activity
     * @param fragment
     * @param containerId
     * @param addToBackStack
     * @param transition
     */
    public void navigateToFragment(AppCompatActivity activity, Fragment fragment, int containerId, String addToBackStack, int transition) {
        activity.getSupportFragmentManager().beginTransaction().replace(containerId, fragment)
                .addToBackStack(addToBackStack).setTransition(transition).commit();
    }

    /**
     * Navigate to activity
     *
     * @param activity
     * @param intent
     */
    public void navigateToActivity(AppCompatActivity activity, Intent intent) {
        activity.startActivity(intent);
    }

    /**
     * Navigate to activity for result
     *
     * @param activity
     * @param intent
     * @param requestCode
     */
    public void navigateToActivityForResult(AppCompatActivity activity, Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
    }
}
