package com.learn.commonalitylibrary;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * author : fengzhangwei
 * date : 2019/11/14
 */
public class LoginBean implements Serializable {
    /**
     * birthday : 1999-01-01
     * sex : 男
     * imageUrl : https://www.baidu.com
     * mobile : 17600463503
     * sign : 退一步海阔天空
     * location : 北京丰台区角门西
     * age : 20
     * email : fengzhangwei399@yeah.net
     * username : 张三
     */

    private String birthday;
    private String sex;
    private String imageUrl;
    private String mobile;
    private String sign;
    private String location;
    private int age;
    private String email;
    private String username;
    private String uid;
    private BigDecimal money;
    private int online;
    private Boolean isFriend = false;
    private int source = -1;
    private String remark = "";
    private String token;

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getFriend() {
        return isFriend;
    }

    public void setFriend(Boolean friend) {
        isFriend = friend;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
