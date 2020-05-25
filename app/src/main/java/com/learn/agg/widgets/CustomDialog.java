package com.learn.agg.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.learn.agg.R;


/**
 * author : fengzhangwei
 * date : 2019/9/12
 */
public class CustomDialog extends Dialog {


    private  Boolean mIsBack;
    private View view;

    public CustomDialog(@NonNull Context context, Boolean isBack) {
        super(context, R.style.custom_dialog);
        this.mIsBack = isBack;
        view = View.inflate(getContext(), R.layout.dialog_loading, null);
        setContentView(view);
        setCancelable(mIsBack);
        setCanceledOnTouchOutside(isBack);
    }

    public CustomDialog(@NonNull Context context, Boolean isBack, int view_id) {
        super(context,R.style.custom_dialog);
        this.mIsBack = isBack;
        view = View.inflate(getContext(), view_id, null);
        setContentView(view);
        setCancelable(mIsBack);
        setCanceledOnTouchOutside(isBack);
    }

    public View getView(){
        return view;
    }
}
