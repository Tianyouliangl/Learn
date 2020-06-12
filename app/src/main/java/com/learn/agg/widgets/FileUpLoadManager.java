package com.learn.agg.widgets;

import android.util.Log;

import com.codebear.keyboard.fragment.CBVoice;
import com.google.gson.Gson;
import com.learn.agg.net.bean.UpLoadFileBean;
import com.learn.commonalitylibrary.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * author : fengzhangwei
 * date : 2019/11/14
 */
public class FileUpLoadManager {

    private String FileType_Image = "image";
    private String FileType_Video = "video";

    public interface FileUpLoadCallBack {
        void onError(Throwable e);

        void onSuccess(String url);

        void onProgress(int pro);
    }

    public interface FileDownloadCallBack {

        void onError(Throwable e);

        void onSuccess(String url);

    }

    private String UPLOAD_URL = Constant.BASE_GROUP_URL + "uploadFile";


    public OkHttpClient getHttpClient() {
        return new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .connectTimeout(1000L * 60 * 3, TimeUnit.MILLISECONDS)
                .readTimeout(1000L * 60 * 3, TimeUnit.MILLISECONDS)
                .build();
    }

    public void upLoadFile(String path, final FileUpLoadCallBack callBack) {
        MultipartBody.Builder muBuilder = new MultipartBody.Builder();
        muBuilder.setType(MultipartBody.FORM);
        File file = new File(path);
        if (path.endsWith(".PNG") || path.endsWith(".png") ||
                path.endsWith(".JPG") || path.endsWith(".jpg") ||
                path.endsWith(".JPEG") || path.endsWith(".jpeg")) {
            RequestBody   fileBody = RequestBody.create(MediaType.parse("image/jpg"), file);
            ProgressRequestBody requestBody = new ProgressRequestBody(fileBody, new ProgressRequestListener() {
                @Override
                public void onRequestProgress(int pro, long contentLength, boolean done) {
                    Log.d("TAG", "pro=====" + pro );
                    if (callBack != null) {
                        callBack.onProgress(pro);
                    }
                }
            });
            muBuilder.addFormDataPart(FileType_Image, file.getName(), requestBody);
        } else if (path.endsWith(".rm") || path.endsWith(".rmvb") ||
                path.endsWith(".mpeg1-4") || path.endsWith(".mov") ||
                path.endsWith(".dat") || path.endsWith(".wmv") ||
                path.endsWith(".avi") || path.endsWith(".3gp") ||
                path.endsWith(".amv") || path.endsWith(".dmv") ||
                path.endsWith(".flv") || path.endsWith(".mp3")||path.endsWith(".amr")) {
            RequestBody  fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
            ProgressRequestBody requestBody = new ProgressRequestBody(fileBody, new ProgressRequestListener() {
                @Override
                public void onRequestProgress(int pro, long contentLength, boolean done) {
                    Log.d("TAG", "pro=====" + pro + "--------position-------");
                    if (callBack != null) {
                        callBack.onProgress(pro);
                    }
                }
            });
            muBuilder.addFormDataPart(FileType_Video, file.getName(), requestBody);
        }
        Log.d("TAG", "参数设置完毕");
        sendRequest(muBuilder.build(), callBack);
    }

    private void sendRequest(MultipartBody build, FileUpLoadCallBack callBack) {
        final Request request = new Request.Builder().url(UPLOAD_URL).post(build).build();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Response response = getHttpClient().newCall(request).execute();
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Log.d("TAG", "====body========" + json);
                    UpLoadFileBean bean = new Gson().fromJson(json, UpLoadFileBean.class);
                    if (bean.getCode() == 1) {
                        emitter.onNext(bean.getUrl());
                    }
                } else {
                    emitter.onError(new IllegalStateException(response.message()));
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver(callBack));
    }

    private Observer<String> getObserver(final FileUpLoadCallBack callBack) {
        Observer<String> observer = new Observer<String>() {

            @Override
            public void onSubscribe(Disposable d) {

            }
            @Override
            public void onNext(String strings) {
                if (callBack != null) {
                    callBack.onSuccess(strings);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (callBack != null) {
                    callBack.onError(e);
                }
            }

            @Override
            public void onComplete() {

            }
        };
        return observer;
    }

    private Observer<String> getObserverDownload(final FileDownloadCallBack callBack) {
        Observer<String> observer = new Observer<String>() {

            @Override
            public void onSubscribe(Disposable d) {

            }
            @Override
            public void onNext(String strings) {
                if (callBack != null) {
                    callBack.onSuccess(strings);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (callBack != null) {
                    callBack.onError(e);
                }
            }

            @Override
            public void onComplete() {

            }
        };
        return observer;
    }

    public void downloadFile(String url, final String fileName, final FileDownloadCallBack callBack){
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Response response = getHttpClient().newCall(request).execute();
                if (response.isSuccessful()) {
                    InputStream is = null;
                    byte[] buf = new byte[2048];
                    int len = 0;
                    FileOutputStream fos = null;

                    //储存下载文件的目录
                    File dir = new File(CBVoice.DEF_FILEPATH);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir,fileName);
                   try {
                       is = response.body().byteStream();
                       long total = response.body().contentLength();
                       fos = new FileOutputStream(file);
                       long sum = 0;
                       while ((len = is.read(buf)) != -1) {
                           fos.write(buf, 0, len);
                           sum += len;
                           int progress = (int) (sum * 1.0f / total * 100);
                           //下载中更新进度条
                       }
                       fos.flush();
                       //下载完成
                       callBack.onSuccess(file.getAbsolutePath());
                   }catch (Exception e){
                       callBack.onError(e);
                   }finally {
                       try {
                           if (is != null) {
                               is.close();
                           }
                           if (fos != null) {
                               fos.close();
                           }
                       } catch (IOException e) {

                       }
                   }
                } else {
                    emitter.onError(new IllegalStateException(response.message()));
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserverDownload(callBack));
    }

    public static Boolean whereExists(String filePath){
        if (filePath.isEmpty()){return false;}
        File file = new File(filePath);
        return file.exists() ? true: false;
    }

}
