package com.learn.agg.msg.act;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.learn.agg.R;
import com.learn.agg.base.BaseMvpActivity;
import com.learn.agg.msg.contract.FindFriendContract;
import com.learn.agg.msg.presenter.FindFriendPresenter;
import com.learn.commonalitylibrary.Constant;
import com.white.easysp.EasySP;

import org.jetbrains.annotations.NotNull;


public class FindFriendActivity extends BaseMvpActivity<FindFriendContract.IPresenter> implements FindFriendContract.IView, View.OnClickListener, View.OnFocusChangeListener, TextWatcher, TextView.OnEditorActionListener {


    private TextView tv_cancel;
    private EditText ed_search;
    private ImageView iv_search;
    private TextView tv_null;

    @Override
    protected Boolean isRequestMission() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_friend;
    }

    @NotNull
    @Override
    public Class<? extends FindFriendContract.IPresenter> registerPresenter() {
        return FindFriendPresenter.class;
    }


    @Override
    protected void initView() {
        initToolbar(true,true,false);
        initToolbar("添加");
        tv_cancel = findViewById(R.id.tv_cancel);
        ed_search = findViewById(R.id.ed_search);
        iv_search = findViewById(R.id.iv_search);
        tv_null = findViewById(R.id.tv_null);
        setDialogIsBack(false);
    }

    @Override
    protected void initData() {
        tv_cancel.setOnClickListener(this);
        ed_search.setOnFocusChangeListener(this);
        ed_search.setOnEditorActionListener(this);
        ed_search.addTextChangedListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            ed_search.setSelection(ed_search.getText().toString().trim().length());
            showSoftInputFromWindow(this,ed_search);
        }
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.requestFocus();
        //显示软键盘
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        //如果上面的代码没有弹出软键盘 可以使用下面另一种方式
        //InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.showSoftInput(editText, 0);
    }

    /**
     * EditText失去焦点并隐藏软键盘
     */
    public static void hindSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.clearFocus();
        //隐藏软键盘
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().trim().isEmpty()){
            iv_search.setImageResource(R.drawable.icon_search_false);
        }else{
            iv_search.setImageResource(R.drawable.icon_search_true);
        }
    }

    /**
     * 回车键
     * @param v
     * @param actionId
     * @param event
     * @return true 软键盘不关闭
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // 开始搜索
        ed_search.clearFocus();
        showLoadingDialog();
        getPresenter().getFriend();
        return true;
    }

    @Override
    public String getMobile() {
        return ed_search.getText().toString().trim();
    }

    @Override
    public String getUid() {
        return EasySP.init(this).getString(Constant.SPKey_UID);
    }

    @Override
    public void onSuccess(String json) {
        tv_null.setVisibility(View.GONE);
        hindSoftInputFromWindow(this,ed_search);
        dismissDialog();
        Bundle bundle = new Bundle();
        bundle.putString("data",json);
        goActivity(FriendInfoActivity.class,bundle);
    }

    @Override
    public void onSuccessNull() {
        tv_null.setVisibility(View.VISIBLE);
        hindSoftInputFromWindow(this,ed_search);
        dismissDialog();
    }

    @Override
    public void onError(String msg) {
        hindSoftInputFromWindow(this,ed_search);
        dismissDialog();
    }
}
