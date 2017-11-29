package com.murielkamgang;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.murielkamgang.internal.R;

import java.lang.reflect.Method;

/**
 * Created by muriel on 11/29/17.
 */

public class AckBar implements AckBarService.AckBarCallback {

    private static final String TAG = AckBar.class.getSimpleName();
    private static final int DEFAULT_SNACK_TIME_OUT = 5000;
    private Activity context;
    private View snackView;
    private PopupWindow snackWindow;
    private Callback callback;
    private Runnable action;

    private boolean isShowing;
    private int duration;

    private AckBar(Activity context, @StringRes int text, @ColorRes int color, int duration) {
        this.context = context;
        this.duration = duration;
        initView(text, color);
    }

    public static AckBar make(Activity context, @StringRes int text, @ColorRes int color, int duration) {
        return new AckBar(context, text, color, duration);
    }

    public static AckBar make(Activity context, @StringRes int text, @ColorRes int color) {
        return new AckBar(context, text, color, DEFAULT_SNACK_TIME_OUT);
    }

    private void initView(@StringRes int textId, @ColorRes int color) {
        snackView = LayoutInflater.from(context).inflate(R.layout.ackbar_view_layout, null);
        ((TextView) snackView.findViewById(R.id.text_alertbar_title)).setText(textId);
        snackView.findViewById(R.id.image_alertbar_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AckBarService.getInstance().dismiss(AckBar.this);
            }
        });
        snackWindow = new PopupWindow(snackView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        snackWindow.setFocusable(false);
        snackWindow.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(color)));
        snackWindow.setOutsideTouchable(false);
    }

    public void setMsg(@StringRes int textId) {
        final TextView textView = snackView.findViewById(R.id.text_alertbar_description);
        if (textView.getVisibility() != View.VISIBLE) {
            textView.setVisibility(View.VISIBLE);
        }

        textView.setText(textId);
    }

    public void clearMsg() {
        final TextView textView = snackView.findViewById(R.id.text_alertbar_description);
        if (textView.getVisibility() == View.VISIBLE) {
            textView.setVisibility(View.GONE);
        }

        textView.setText(null);
    }

    public void setAction(Runnable r) {
        final ImageView imageView = snackView.findViewById(R.id.image_arrow);
        action = r;
        if (action != null) {
            imageView.setVisibility(View.VISIBLE);
            snackView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action.run();
                }
            });
        } else {
            imageView.setVisibility(View.GONE);
            snackView.setOnClickListener(null);
        }
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private boolean isInit() {
        return snackWindow != null;
    }

    public void dismiss() {
        AckBarService.getInstance().dismiss(this);
    }

    public void show() {
        AckBarService.getInstance().show(this, duration);
    }

    private void internalShow() {
        if (!isShowing) {
            final ViewGroup viewGroup = context
                    .findViewById(android.R.id.content);

            final TypedValue tv = new TypedValue();
            int actionBarHeight = 0;
            if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
            }

            int statusBarSize = 0;
            int statusBarSizeResId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (statusBarSizeResId > 0)
                statusBarSize = context.getResources().getDimensionPixelSize(statusBarSizeResId);

            snackWindow.setAnimationStyle(R.style.ackBarStyle);
            setWindowLayoutType();
            snackWindow.showAtLocation(viewGroup, Gravity.NO_GRAVITY, 0, actionBarHeight + statusBarSize);

            isShowing = true;
            if (callback != null) {
                callback.onShowed();
            }
        }
    }

    private void internalDismiss() {
        if (isInit() && isShowing) {
            snackWindow.dismiss();
            isShowing = false;
            if (callback != null) {
                callback.onDismissed();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setWindowLayoutType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                snackView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                Method setWindowLayoutType = PopupWindow.class.getMethod("setWindowLayoutType", new Class[]{int.class});
                setWindowLayoutType.invoke(snackWindow, WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);

            } catch (Exception e) {
                android.util.Log.e(TAG, "", e);
            }
        }
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onShow() {
        internalShow();
    }

    @Override
    public void onDismiss() {
        internalDismiss();
    }

    public interface Callback {

        void onShowed();

        void onDismissed();
    }

}