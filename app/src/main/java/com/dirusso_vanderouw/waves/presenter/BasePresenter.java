package com.dirusso_vanderouw.waves.presenter;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dirusso_vanderouw.waves.view.BaseView;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Base presenter for MVP architecture
 *
 * @param <V>
 */
public abstract class BasePresenter<V extends BaseView> implements Serializable {


    private WeakReference<V> view;
    private CompositeSubscription compositeSubscription;

    public BasePresenter() {
        compositeSubscription = new CompositeSubscription();
    }

    public BasePresenter(@NonNull V view) {
        this.view = new WeakReference<>(view);
    }

    /**
     * Binds the view. Must be called onResume()
     *
     * @param view
     */
    public void bindView(@NonNull V view) {
        if (view == null) {
            throw new IllegalArgumentException("View cannot be null");
        }
        this.view = new WeakReference<>(view);
    }

    /**
     * Unbinds the view. Method must be called onPause()
     */
    public void unBindView() {
        view = null;
    }

    /**
     * Is view null
     *
     * @return
     */
    public boolean isViewNull() {
        return view == null || view.get() == null;
    }

    /**
     * Must be call on view onDestroy to unscuscribe from composite subscriptions (reactive)
     */
    public void onDestroy() {
        compositeSubscription.unsubscribe();
    }

    /**
     * Get view attached to the presenter
     *
     * @return
     */
    @Nullable
    protected V getView() {
        return isViewNull() ? null : view.get();
    }

    protected void addSubscription(@NonNull Subscription subscription) {
        if (!compositeSubscription.isUnsubscribed()) {
            compositeSubscription.add(subscription);
        }
    }

    public void removeAllSubscriptions() {
        if (compositeSubscription.hasSubscriptions()) {
            compositeSubscription.clear();
        }
    }

}
