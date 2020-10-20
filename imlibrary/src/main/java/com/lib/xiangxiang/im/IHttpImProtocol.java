package com.lib.xiangxiang.im;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IHttpImProtocol {

    @POST("file/existFile")
    @FormUrlEncoded
    Observable<BaseResponse<String>> existFileUrl(@FieldMap HashMap<String, String> map);
}
