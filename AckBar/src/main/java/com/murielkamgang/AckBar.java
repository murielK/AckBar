package com.murielkamgang;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
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
    private static final int DEFAULT_ACK_BAR_TIME_OUT = 5000;
    private Activity activity;
    private View view;
    private PopupWindow popupWindow;
    private Callback callback;
    private Runnable action;

    private boolean isShowing;
    private int duration;

    private AckBar(Activity context, @StringRes int titleResId, @ColorRes int backgroundColorResId, int duration) {
        this.activity = context;
        this.duration = duration;
        init(titleResId, backgroundColorResId);
    }

    private AckBar(Activity context, String title, int backgroundColor, int duration) {
        this.activity = context;
        this.duration = duration;
        init(title, backgroundColor);
    }

    public static AckBar make(Activity context, @StringRes int titleResId, @ColorRes int backgroundColorResId, int duration) {
        return new AckBar(context, titleResId, backgroundColorResId, duration);
    }

    public static AckBar make(Activity context, @StringRes int titleResId, @ColorRes int backgroundColorResId) {
        return new AckBar(context, titleResId, backgroundColorResId, DEFAULT_ACK_BAR_TIME_OUT);
    }

    public static AckBar make(Activity context, String title, int backgroundColor) {
        return new AckBar(context, title, backgroundColor, DEFAULT_ACK_BAR_TIME_OUT);
    }

    private void init(String title, int color) {
        view = LayoutInflater.from(activity).inflate(R.layout.ackbar_view_layout, null);
        view.findViewById(R.id.image_alertbar_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AckBarService.getInstance().dismiss(AckBar.this);
            }
        });

        ((TextView) view.findViewById(R.id.text_alertbar_title)).setText(title);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(color));
        popupWindow.setOutsideTouchable(false);
    }

    private void init(@StringRes int titleResId, @ColorRes int backgroundColorResId) {
        final Resources res = activity.getResources();
        init(res.getString(titleResId), res.getColor(backgroundColorResId));
    }

    public void setMsg(@StringRes int msgResId) {
        setMsg(activity.getResources().getString(msgResId));
    }

    public void setMsg(String msg) {
        final TextView textView = view.findViewById(R.id.text_alertbar_description);
        if (textView.getVisibility() != View.VISIBLE) {
            textView.setVisibility(View.VISIBLE);
        }

        textView.setText(msg);
    }

    public void clearMsg() {
        final TextView textView = view.findViewById(R.id.text_alertbar_description);
        if (textView.getVisibility() == View.VISIBLE) {
            textView.setVisibility(View.GONE);
        }

        textView.setText(null);
    }

    public void setAction(Runnable r) {
        final ImageView imageView = view.findViewById(R.id.image_arrow);
        action = r;
        if (action != null) {
            imageView.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action.run();
                }
            });
        } else {
            imageView.setVisibility(View.GONE);
            view.setOnClickListener(null);
        }
    }

    public Object getTag() {
        return view.getTag();
    }

    public void setTag(Object object) {
        view.setTag(object);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private boolean isInit() {
        return popupWindow != null;
    }

    public void dismiss() {
        AckBarService.getInstance().dismiss(this);
    }

    public void show() {
        AckBarService.getInstance().show(this, duration);
    }

    private void internalShow() {
        if (!isShowing) {
            final ViewGroup viewGroup = activity
                    .findViewById(android.R.id.content);

            final TypedValue tv = new TypedValue();
            int actionBarHeight = 0;
            if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
            }

            int statusBarSize = 0;
            int statusBarSizeResId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (statusBarSizeResId > 0)
                statusBarSize = activity.getResources().getDimensionPixelSize(statusBarSizeResId);

            popupWindow.setAnimationStyle(R.style.ackBarStyle);
            setWindowLayoutType();
            popupWindow.showAtLocation(viewGroup, Gravity.NO_GRAVITY, 0, actionBarHeight + statusBarSize);

            isShowing = true;
            if (callback != null) {
                callback.onShowed(this);
            }
        }
    }

    private void internalDismiss() {
        if (isShowing) {
            popupWindow.dismiss();
            isShowing = false;
            if (callback != null) {
                callback.onDismissed(this);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setWindowLayoutType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            try {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                Method setWindowLayoutType = PopupWindow.class.getMethod("setWindowLayoutType", new Class[]{int.class});
                setWindowLayoutType.invoke(popupWindow, WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);

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

        void onShowed(AckBar ackBar);

        void onDismissed(AckBar ackBar);
    }

}