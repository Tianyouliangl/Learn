package com.codebear.keyboard.net;

import com.codebear.keyboard.data.SearchHeatMapBean;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.body.GifBean;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface IHttpProtocol {
    @POST("photo/likePhotoList")
    @FormUrlEncoded
    Observable<BaseResponseTC<List<GifBean>>> getPhotoList(@FieldMap HashMap<String, String> map);

    @POST("")
    @FormUrlEncoded
    Observable<Object> getAnon_id(@Url String url, @FieldMap HashMap<String, String> map);

    @POST("")
    @FormUrlEncoded
    Observable<SearchHeatMapBean> getHeatMapList(@Url String url, @FieldMap HashMap<String, Object> map);
}
