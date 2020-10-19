package com.learn.agg.txt;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.agg.R;
import com.learn.agg.base.BaseMvpFragment;
import com.learn.agg.txt.act.ReadActivity;
import com.learn.agg.txt.adapter.TxtAdapter;
import com.learn.agg.txt.contract.ReadContract;
import com.learn.agg.txt.presenter.ReadPresenter;
import com.learn.agg.util.FileChooseUtil;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.TxtBean;
import com.learn.commonalitylibrary.sqlite.DataBaseHelp;
import com.learn.commonalitylibrary.util.NotificationUtils;
import com.white.easysp.EasySP;
import com.zyyoona7.popup.EasyPopup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ReadFragment extends BaseMvpFragment<ReadContract.IPresenter> implements ReadContract.IView, View.OnClickListener, TxtAdapter.BookListener {

    private RecyclerView rl_books;
    private final int GradView_Count = 3;
    private GridLayoutManager layoutManager;
    private List<TxtBean> bookList = new ArrayList<>();
    private TxtAdapter adapter;
    private TextView tv_delete;
    private EasyPopup easyPopup;
    private LinearLayout rl_group;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_read_txt;
    }

    @NotNull
    @Override
    public Class<? extends ReadContract.IPresenter> registerPresenter() {
        return ReadPresenter.class;
    }

    @Override
    protected void initView() {
        super.initView();
        rl_books = view.findViewById(R.id.rl_books);
        tv_delete = view.findViewById(R.id.tv_delete);
        rl_group = view.findViewById(R.id.rl_group);
    }

    @Override
    protected void initData() {
        super.initData();
        createPopup();
        layoutManager = new GridLayoutManager(getContext(), GradView_Count);
        adapter = new TxtAdapter(bookList, getContext());
        rl_books.setLayoutManager(layoutManager);
        rl_books.setAdapter(adapter);
        adapter.setData(DataBaseHelp.getInstance(getActivity()).getTxtList());
        adapter.setOnBooksClickListener(this);
        tv_delete.setOnClickListener(this);
    }

    private void createPopup() {
        easyPopup = EasyPopup.create(getContext())
                .setContentView(getContext(), R.layout.pop_txt)
//                .setAnimationStyle(R.style.RightPopAnim)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(true)
                //允许背景变暗
                .setBackgroundDimEnable(false)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .apply();
        easyPopup.getContentView().findViewById(R.id.tv_input).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                easyPopup.dismiss();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_delete:
                if (adapter.getCheckedList().size() > 0){

                }else {
                    showToast("请选择!");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(TxtBean booksBean) {
        Boolean delete = adapter.getDelete();
        if (!delete) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("data",booksBean);
            goActivity(ReadActivity.class,bundle);
        }
    }

    @Override
    public void onLongItemClick(TxtBean booksBean) {
        adapter.setIsDelete(true);
        tv_delete.setVisibility(View.VISIBLE);
    }

    public Boolean isDelete() {
        return adapter.getDelete();
    }

    public void setIsDelete(Boolean delete) {
        adapter.setIsDelete(delete);
        if (!delete) {
            tv_delete.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAddBook() {
        if (easyPopup != null && view != null) {
            easyPopup.showAsDropDown(rl_group, Gravity.BOTTOM, 0, 0);
        }
    }

    @Override
    public void onCheckedChange() {
        List<TxtBean> list = adapter.getCheckedList();
        if (list.size() > 0){
            tv_delete.setText("删除(" + list.size() + ")");
        }else {
            tv_delete.setText("删除");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String path = null;
                Uri uri = data.getData();
                if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                    path = uri.getPath();
                    return;
                }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                    path = FileChooseUtil.getPath(getContext(), uri);
                } else {//4.4以下下系统调用方法
                    path = FileChooseUtil.getRealPathFromURI(uri);
                }
                Log.i("ttt", path);
                if (path != null) {
                    if (!path.endsWith(".txt")) {
                        showToast("不支持的类型!");
                    } else {
                        int start = path.lastIndexOf("/");
                        int end = path.lastIndexOf(".");
                        if (start != -1 && end != -1) {
                            String file_name = path.substring(start + 1, end);
                            Boolean addTxt = DataBaseHelp.getInstance(getActivity()).addTxt(path, file_name, "");
                            if (addTxt){
                                List<TxtBean> list = DataBaseHelp.getInstance(getActivity()).getTxtList();
                                adapter.setData(list);
                            }else {
                                showToast("文件已存在!");
                            }
                        }
                    }
                }
            }
        }
    }

}
