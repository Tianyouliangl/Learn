package com.learn.agg.net.bean;

/**
 * author : fengzhangwei
 * date : 2019/12/23
 */
public class AttnBean {
    /**
     * uid : 776ec439-be0f-4df5-b492-39ac7cd360fd
     * imageUrl : http://172.16.200.235:8080/upload/images/201912181028300590.jpg
     * sign : ...........
     * username : nice好吧
     */

    private String uid;
    private String imageUrl;
    private String sign;
    private String username;
    private int online;

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "AttnBean{" +
                "uid='" + uid + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", sign='" + sign + '\'' +
                ", username='" + username + '\'' +
                ", online=" + online +
                '}';
    }
}
