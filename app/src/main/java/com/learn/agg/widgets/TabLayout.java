package com.learn.agg.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.fragment.app.Fragment;

import com.learn.agg.R;

import java.util.ArrayList;

/**
 * Created by Long
 * on 2018/10/4.
 */
public class TabLayout extends LinearLayout implements View.OnClickListener {
    private ArrayList<Tab> tabs;
    private OnTabClickListener listener;
    private View selectView;
    private int tabCount;
    private int index;

    public TabLayout(Context context) {
        super(context);
        setUpView();
    }


    public TabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpView();
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUpView();
    }

    private void setUpView() {
        setOrientation(HORIZONTAL);
        setClickable(false);
    }

    public void setUpData(ArrayList<Tab> tabs, OnTabClickListener listener) {
        this.tabs = tabs;
        this.listener = listener;

        if (tabs != null && tabs.size() > 0) {
            tabCount = tabs.size();
            TabView mTabView;
            LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            params.weight = 1;
            for (int i = 0; i < tabs.size(); i++) {
                mTabView = new TabView(getContext());
                mTabView.setTag(tabs.get(i));
                mTabView.setUpData(tabs.get(i));
                mTabView.setOnClickListener(this);
                addView(mTabView, params);
            }
        } else {
            throw new IllegalArgumentException("tabs can't be empty");
        }
    }

    public void setCurrentTab(int i) {
        if (i < tabCount && i >= 0) {
            index = i;
            View view = getChildAt(i);
            onClick(view);
        }
    }

    public int findFragmentIndex(String fragmentName){
        for (int i=0;i<tabs.size();i++){
            Tab tab = tabs.get(i);
            String simpleName = tab.targetFragmentClz.getSimpleName();
            if (simpleName.equals(fragmentName)){
                return i;
            }
        }
        return -1;
    }

    public void onDataChanged(int i, int badgeCount) {
        if (i < tabCount && i >= 0) {
            TabView view = (TabView) getChildAt(i);
            view.onDataChanged(badgeCount);
        }
    }

    @Override
    public void onClick(View v) {
        if (selectView != v) {
            listener.onTabClick((Tab) v.getTag());
            v.setSelected(true);
            v.setPressed(true);
            v.setFocusable(true);
            if (selectView != null) {
                selectView.setSelected(false);
            }
            selectView = v;
        }
    }

    public interface OnTabClickListener {
        void onTabClick(Tab tab);
    }

    public class TabView extends LinearLayout {
        private ImageView mTabImg;
        private TextView mTabLabel;
        private BadgeView badge_view;

        public TabView(Context context) {
            super(context);
            setUpView();
        }

        public TabView(Context context, AttributeSet attrs) {
            super(context, attrs);
            setUpView();
        }

        public TabView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setUpView();
        }


        private void setUpView() {
            LayoutInflater.from(getContext()).inflate(R.layout.widget_tab_view, this, true);
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);
            setClickable(false);
            mTabImg = (ImageView) findViewById(R.id.mTabImg);
            mTabLabel = (TextView) findViewById(R.id.mTabLabel);
            badge_view = findViewById(R.id.badge_view);
        }

        public void setUpData(Tab tab) {
            mTabImg.setBackgroundResource(tab.imgResId);
            mTabLabel.setText(tab.labelResId);
        }


        public void onDataChanged(int badgeCount) {
            if (badge_view != null){
                badge_view.showBadge(badgeCount);
            }
            mTabImg.setSelected(true);
        }
    }

    public static class Tab {
        public int imgResId;
        public int labelResId;
        public int badgeCount;
        public int menuResId;
        public Class<? extends Fragment> targetFragmentClz;

        public Tab(int imgResId, int labelResId) {
            this.imgResId = imgResId;
            this.labelResId = labelResId;
        }

        public Tab(int imgResId, int labelResId, int badgeCount) {
            this.imgResId = imgResId;
            this.labelResId = labelResId;
            this.badgeCount = badgeCount;
        }

        public Tab(int imgResId, int labelResId, Class<? extends Fragment> targetFragmentClz) {
            this.imgResId = imgResId;
            this.labelResId = labelResId;
            this.targetFragmentClz = targetFragmentClz;
        }

        public Tab(int imgResId, int labelResId, int menuResId, Class<? extends Fragment> targetFragmentClz) {
            this.imgResId = imgResId;
            this.labelResId = labelResId;
            this.menuResId = menuResId;
            this.targetFragmentClz = targetFragmentClz;
        }
    }
}
