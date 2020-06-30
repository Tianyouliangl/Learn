package com.learn.agg.msg.contract;

import android.content.Context;

import com.learn.commonalitylibrary.ChatMessage;
import com.senyint.ihospital.contract.IPresenterContract;
import com.senyint.ihospital.contract.IViewContract;

import java.util.List;

public interface BaseChatContract {
    interface IView extends IViewContract {
        String getUid();
        String getConversation();
        Context getContent();
        void onSuccess(List<ChatMessage> list);
        void onSuccess();
        void onError(String msg);
    }

    interface IPresenter extends IPresenterContract {
        void getHistory(final int pageNo, int pageSize);
        void updateHistory(String pid);
    }
}
