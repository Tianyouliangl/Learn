package com.learn.agg.txt.act;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bifan.txtreaderlib.bean.TxtMsg;
import com.bifan.txtreaderlib.interfaces.ICenterAreaClickListener;
import com.bifan.txtreaderlib.interfaces.IChapter;
import com.bifan.txtreaderlib.interfaces.ILoadListener;
import com.bifan.txtreaderlib.interfaces.IPageChangeListener;
import com.bifan.txtreaderlib.main.TxtReaderView;
import com.learn.agg.R;
import com.learn.agg.act.contract.ReadContract;
import com.learn.agg.act.presenter.ReadPresenter;
import com.learn.agg.base.BaseMvpActivity;
import com.learn.agg.txt.adapter.CatalogAdapter;
import com.learn.commonalitylibrary.TxtBean;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReadActivity extends BaseMvpActivity<ReadContract.IPresenter> implements ReadContract.IView, ILoadListener, ICenterAreaClickListener, View.OnTouchListener, DrawerLayout.DrawerListener, IPageChangeListener, CatalogAdapter.onClickInterface {

    private String TAG = "ReadActivity";
    private TxtReaderView txt_view;
    private LinearLayout menu_bottom;
    private Boolean isOnTouch = false;
    private Boolean isMenuLeftOpen = false;
    private DrawerLayout drawerLayout;
    private TextView tv_bottom_menu_name;
    private TextView tv_top_section_name;
    private RelativeLayout rl_txt_view;
    private TxtBean txtBean;
    private RecyclerView rl_catalog;
    private CatalogAdapter adapter;
    private LinearLayoutManager manager;

    @Override
    protected Boolean isRequestMission() {
        needPermissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        return true;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_read;
    }

    @Override
    protected void initView() {
        rl_txt_view = findViewById(R.id.rl_txt_view);
        tv_top_section_name = findViewById(R.id.tv_top_section_name);
        txt_view = findViewById(R.id.txt_view);
        menu_bottom = findViewById(R.id.menu_bottom);
        drawerLayout = findViewById(R.id.drawerLayout);
        tv_bottom_menu_name = findViewById(R.id.tv_bottom_menu_name);
        rl_catalog = findViewById(R.id.rl_catalog);
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        txtBean = (TxtBean) bundle.getSerializable("data");
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        tv_bottom_menu_name.setText(txtBean.getTxt_name());
        rl_txt_view.setBackgroundColor(txt_view.getBackgroundColor());
        txt_view.setDefaultBgColor(txt_view.getBackgroundColor());
        txt_view.loadTxtFile(txtBean.getLocal_path(), this);
        manager = new LinearLayoutManager(this);
        rl_catalog.setLayoutManager(manager);
        adapter = new CatalogAdapter(this, new ArrayList<IChapter>());
        rl_catalog.setAdapter(adapter);
        txt_view.setPageChangeListener(this);
        drawerLayout.addDrawerListener(this);
        adapter.setOnClickListener(this);
    }

    @NotNull
    @Override
    public Class<? extends ReadContract.IPresenter> registerPresenter() {
        return ReadPresenter.class;
    }

    @Override
    public void onSuccess() {
        Log.i(TAG, "onSuccess");
        txt_view.setStyle(Color.parseColor("#d4c7a5"), Color.parseColor("#453e33"));
        tv_top_section_name.setTextColor(Color.parseColor("#453e33"));
        tv_top_section_name.setText(txt_view.getCurrentChapter().getTitle());
        txt_view.setOnCenterAreaClickListener(this);
        txt_view.setOnTouchListener(this);
    }

    @Override
    public void onFail(TxtMsg txtMsg) {
        Log.i(TAG, txtMsg.toString());
    }

    @Override
    public void onMessage(String message) {
        Log.i(TAG, message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        txt_view.saveCurrentProgress();
    }

    @Override
    public boolean onCenterClick(float widthPercentInView) {
        if (!isOnTouch) {
            menu_bottom.setVisibility(menu_bottom.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
        isOnTouch = false;
        return true;
    }

    @Override
    public boolean onOutSideCenterClick(float widthPercentInView) {
        if (isOnTouch) {
            isOnTouch = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (menu_bottom.getVisibility() == View.VISIBLE) {
            menu_bottom.setVisibility(View.GONE);
            isOnTouch = true;
            return true;
        }
        return isMenuLeftOpen;
    }

    // 目录
    public void txtMenuCatalog(View view) {
        drawerLayout.openDrawer(Gravity.LEFT);
        menu_bottom.setVisibility(View.GONE);
        List<IChapter> list = txt_view.getChapters();
        IChapter iChapter = txt_view.getCurrentChapter();
        adapter.setList(list);
        adapter.setPath(iChapter.getIndex());
        scrollToPosition(adapter.getIndex());

    }


    private void scrollToPosition(int position) {
        if (position != -1) {
            rl_catalog.scrollToPosition(position);
            LinearLayoutManager mLayoutManager =
                    (LinearLayoutManager) rl_catalog.getLayoutManager();
            mLayoutManager.scrollToPositionWithOffset(position, 0);
        }
    }

    // 亮度
    public void txtMenuBrightness(View view) {

    }

    // 设置
    public void txtMenuSetting(View view) {

    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        isMenuLeftOpen = true;
    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        isMenuLeftOpen = false;
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isMenuLeftOpen) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCurrentPage(float progress) {
        tv_top_section_name.setText(txt_view.getCurrentChapter().getTitle()+"");
    }

    @Override
    public void onClickItem(IChapter iChapter) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        int index = adapter.getIndex();
        if (iChapter.getIndex() != index){
           txt_view.jumpToIndex(iChapter.getIndex());
        }
    }
}
