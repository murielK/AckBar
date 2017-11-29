package com.murielkamgang;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

/**
 * Created by muriel on 11/29/17.
 */

public class AckBarService {// not to be used in multithreading environment

    private static final int WHAT_DISMISS = 0;
    private static final int MIN_TIME_OUT = 2000;

    private static volatile AckBarService instance;

    private AckBarHolder currentAckBar;
    private LinkedList<AckBarHolder> ackBarHolders = new LinkedList<>();
    private Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_DISMISS:
                    AckBarService.this.dismiss(((AckBarHolder) msg.obj).ackBarCallbackWeakReference.get());
                    return true;
                default:
                    return false;
            }
        }
    });

    private AckBarService() {
    }

    public static AckBarService getInstance() {
        if (instance == null) {
            synchronized (AckBarService.class) {
                if (instance == null) {
                    instance = new AckBarService();
                }
            }
        }

        return instance;
    }

    void show(AckBarCallback callback, int duration) {
        if (isCurrent(callback)) {
            handler.removeCallbacksAndMessages(currentAckBar);
            currentAckBar.duration = duration;
            scheduleTimeOutFor(currentAckBar);
        } else {
            AckBarHolder ackBarHolder = getAckBarHolderFor(callback);
            if (ackBarHolder == null) {
                ackBarHolder = new AckBarHolder(callback, duration);
                ackBarHolders.push(ackBarHolder);
            } else {
                ackBarHolder.duration = duration;
            }

            if (currentAckBar == null) {
                showNextAckBarWindow();
            }
        }
    }

    void dismiss(AckBarCallback callback) {
        if (isCurrent(callback)) {
            callback.onDismiss();
            handler.removeCallbacksAndMessages(currentAckBar);
            currentAckBar = null;
            showNextAckBarWindow();
        } else {
            final AckBarHolder ackBarHolder;
            if ((ackBarHolder = getAckBarHolderFor(callback)) != null) {
                ackBarHolders.remove(ackBarHolder);
            }
        }
    }

    private void scheduleTimeOutFor(AckBarHolder s) {
        handler.removeCallbacksAndMessages(s);
        if (s.duration > 0) {
            handler.sendMessageDelayed(Message.obtain(handler, WHAT_DISMISS, s), s.duration < MIN_TIME_OUT ? MIN_TIME_OUT : s.duration);
        }
    }

    private boolean isCurrent(AckBarCallback callback) {
        return currentAckBar != null && currentAckBar.isAckBar(callback);
    }

    private void showNextAckBarWindow() {
        if (!ackBarHolders.isEmpty()) {
            final AckBarHolder ackBarHolder = ackBarHolders.pop();
            final AckBarCallback callback;
            if ((callback = ackBarHolder.ackBarCallbackWeakReference.get()) != null) {
                callback.onShow();
                currentAckBar = ackBarHolder;
                scheduleTimeOutFor(currentAckBar);
            } else {
                showNextAckBarWindow();
            }
        }
    }

    private AckBarHolder getAckBarHolderFor(AckBarCallback callback) {
        if (callback != null) {
            for (final AckBarHolder sh : ackBarHolders) {
                if (sh.isAckBar(callback)) {
                    return sh;
                }
            }
        }

        return null;
    }

    interface AckBarCallback {

        void onShow();

        void onDismiss();
    }

    private static class AckBarHolder {

        private final WeakReference<AckBarCallback> ackBarCallbackWeakReference;
        private int duration;

        private AckBarHolder(AckBarCallback callback, int duration) {
            this.ackBarCallbackWeakReference = new WeakReference<>(callback);
            this.duration = duration;
        }

        boolean isAckBar(AckBarCallback callback) {
            return callback != null && ackBarCallbackWeakReference.get() == callback;
        }
    }
}

