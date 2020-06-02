package com.learn.commonalitylibrary.body;

/**
 * author : fengzhangwei
 * date : 2020/1/7
 */
public class EmoticonBody {

    private String url;

    public EmoticonBody(String image_url) {
        this.url = image_url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
