package com.mredrock.cyxbs.subscriber;

import android.content.Context;
import android.widget.Toast;

import com.mredrock.cyxbs.BuildConfig;
import com.mredrock.cyxbs.component.task.progress.ProgressCancelListener;
import com.mredrock.cyxbs.component.task.progress.ProgressDialogHandler;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import rx.Subscriber;

/**
 * Created by cc on 16/3/19.
 */
public class SimpleSubscriber<T> extends Subscriber<T> implements ProgressCancelListener {
    private Context               context;
    protected SubscriberListener<T> listener;
    private ProgressDialogHandler mProgressDialogHandler;

    public SimpleSubscriber(Context context, SubscriberListener<T> listener) {
        this(context, false, listener);
    }

    public SimpleSubscriber(Context context, boolean shouldShowProgressDialog, SubscriberListener<T> listener) {
        this(context, shouldShowProgressDialog, false, listener);
    }

    public SimpleSubscriber(Context context, boolean shouldShowProgressDialog, boolean isProgressDialogCancelable, SubscriberListener<T> listener) {
        this.context = context;
        this.listener = listener;
        if (shouldShowProgressDialog) {
            mProgressDialogHandler = new ProgressDialogHandler(context, this, isProgressDialogCancelable);
        }
    }

    @Override
    public void onStart() {
        showProgressDialog();
        if (listener != null) {
            listener.onStart();
        }
    }

    @Override
    public void onCompleted() {
        dismissProgressDialog();
        if (listener != null) {
            listener.onCompleted();
        }
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof SocketTimeoutException) {
            Toast.makeText(context, "网络中断，请检查您的网络状态", Toast.LENGTH_SHORT).show();
        } else if (e instanceof ConnectException) {
            Toast.makeText(context, "网络异常，请检查您的网络状态", Toast.LENGTH_SHORT).show();
        } else if (e instanceof UnknownHostException) {
            Toast.makeText(context, "网络异常，请检查您的网络状态", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
        dismissProgressDialog();
        if (listener != null) {
            listener.onError(e);
        }
    }

    @Override
    public void onNext(T t) {
        if (listener != null) {
            listener.onNext(t);
        }
    }

    @Override
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }

    protected Context getContext() {
        return this.context;
    }

    private void showProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG)
                                  .sendToTarget();
        }
    }

    protected void dismissProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG)
                                  .sendToTarget();
            mProgressDialogHandler = null;
        }
    }

}
