package com.learn.agg.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.learn.agg.R;
import com.shehuan.niv.NiceImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.MissingResourceException;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public abstract class BaseFragment extends Fragment {
    public View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(getLayoutId(), container, false);
        return view;
    }

    protected abstract int getLayoutId();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    protected void initView() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    protected void initData() {

    }

    protected void initToolbar(Boolean l, Boolean c, Boolean r) {
        NiceImageView back = view.findViewById(R.id.toolbar_back);
        TextView title = view.findViewById(R.id.toolbar_title);
        TextView right_content = view.findViewById(R.id.toolbar_action);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (back == null || title == null || right_content == null || toolbar == null) {
            throw new MissingResourceException("not find toolbar view", this.getClass().getName(), "toolbar");
        }
        getActivity().setActionBar(toolbar);
        back.setVisibility(l ? VISIBLE : GONE);
        title.setVisibility(c ? VISIBLE : GONE);
        right_content.setVisibility(r ? VISIBLE : GONE);
        toolbar.setVisibility(l && c && r ? VISIBLE : GONE);
    }

    protected void initToolbar(String titleContent) {
        NiceImageView back = view.findViewById(R.id.toolbar_back);
        TextView title = view.findViewById(R.id.toolbar_title);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        getActivity().setActionBar(toolbar);
        if (back == null || title == null) {
            throw new MissingResourceException("not find toolbar view", this.getClass().getName(), "toolbar");
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        title.setText(titleContent);
    }

    protected void initToolbar(String titleContent, String right) {
        NiceImageView back = view.findViewById(R.id.toolbar_back);
        TextView title = view.findViewById(R.id.toolbar_title);
        TextView right_content = view.findViewById(R.id.toolbar_action);
        right_content.setText(right);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        getActivity().setActionBar(toolbar);
        if (back == null || title == null || right_content == null) {
            throw new MissingResourceException("not find toolbar view", this.getClass().getName(), "toolbar");
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        title.setText(titleContent);
    }

    protected void initToolbar(Bitmap bitmap, String titleContent, String right, View.OnClickListener onClickListener) {
        NiceImageView back = view.findViewById(R.id.toolbar_back);
        TextView title = view.findViewById(R.id.toolbar_title);
        TextView right_content = view.findViewById(R.id.toolbar_action);
        right_content.setText(right);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        getActivity().setActionBar(toolbar);
        if (back == null || title == null || right_content == null) {
            throw new MissingResourceException("not find toolbar view", this.getClass().getName(), "toolbar");
        }
        back.setImageBitmap(bitmap);
        back.setOnClickListener(onClickListener);
        title.setText(titleContent);
        right_content.setOnClickListener(onClickListener);
    }

    protected void goActivity(Class cls){
        goActivity(cls,null);
    }

    protected void goActivity(Class cls,Bundle bundle){
        Intent intent = new Intent(getActivity(), cls);
        if (bundle != null){
            intent.putExtra("bundle",bundle);
        }
        this.startActivity(intent);
    }
}
