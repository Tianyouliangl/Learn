package com.learn.agg.net.base;



import com.learn.agg.net.bean.AttnBean;
import com.learn.agg.net.bean.CommonBean;
import com.learn.agg.net.bean.FriendMsgBean;
import com.learn.agg.net.bean.FriendMsgCountBean;
import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.LoginBean;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;


public interface IHttpProtocol {

    @POST("Lg/user")
    @FormUrlEncoded
    Observable<BaseResponseTC<LoginBean>> login(@FieldMap HashMap<String, String> map);

    @POST("friend/findFriend")
    @FormUrlEncoded
    Observable<BaseResponseTC<LoginBean>> findFriend(@FieldMap HashMap<String, String> map);

    @POST("friend/addFriendMsg")
    @FormUrlEncoded
    Observable<BaseResponseTC<LoginBean>> addFriendMsg(@FieldMap HashMap<String, Object> map);

    @POST("friend/allFriendMsg")
    @FormUrlEncoded
    Observable<BaseResponseTC<List<FriendMsgBean>>> getAllFriendMsg(@FieldMap HashMap<String, String> map);

    @POST("/friend/info")
    @FormUrlEncoded
    Observable<BaseResponseTC<LoginBean>> getFriendInfo(@FieldMap HashMap<String, String> map);

    @POST("friend/allFriend")
    @FormUrlEncoded
    Observable<BaseResponseTC<List<LoginBean>>> getAllFriend(@FieldMap HashMap<String, String> map);

    @POST("friend/addFriend")
    @FormUrlEncoded
    Observable<BaseResponseTC<Object>> setFriend(@FieldMap HashMap<String, Object> map);

    @POST("friend/allAddFriendCount")
    @FormUrlEncoded
    Observable<BaseResponseTC<List<FriendMsgCountBean>>> getAllFriendMsgCount(@FieldMap HashMap<String, String> map);

    @POST("rgs/user")
    @FormUrlEncoded
    Observable<BaseResponseTC<Object>> register(@FieldMap HashMap<String, String> map);

    @POST("update/user")
    @FormUrlEncoded
    Observable<BaseResponseTC<String>> updateUserInfo(@FieldMap HashMap<String, String> map);

    @POST("update/user")
    @FormUrlEncoded
    Observable<BaseResponseTC<String>> upUserInfo(@FieldMap HashMap<String, Object> map);

    @POST("updateHistory/user")
    @FormUrlEncoded
    Observable<BaseResponseTC<ChatMessage>> updateHistory(@FieldMap HashMap<String, Object> map);

    @POST("all/user")
    @FormUrlEncoded
    Observable<BaseResponseTC<List<AttnBean>>> getAllAttn(@FieldMap HashMap<String, String> map);

    @POST("info/user")
    @FormUrlEncoded
    Observable<BaseResponseTC<LoginBean>> getUserInfo(@FieldMap HashMap<String, String> map);

    @POST("history/user")
    @FormUrlEncoded
    Observable<BaseResponseTC<List<ChatMessage>>> getHistory(@FieldMap HashMap<String, Object> map);

    @POST("")
    @FormUrlEncoded
    Observable<BaseResponse<CommonBean>> getData(@Url String url, @FieldMap HashMap<String, String> map);
}
