package com.learn.commonalitylibrary;

public class SessionMessage {
    private String conversation;
    private String to_id;
    private String body;
    private Long time;
    private int msg_status;
    private int number;
    private int body_type;
    private LoginBean info;

    public LoginBean getInfo() {
        return info;
    }

    public void setInfo(LoginBean info) {
        this.info = info;
    }

    public int getBody_type() {
        return body_type;
    }

    public void setBody_type(int body_type) {
        this.body_type = body_type;
    }

    public int getMsg_status() {
        return msg_status;
    }

    public String getTo_id() {
        return to_id;
    }

    public void setTo_id(String to_id) {
        this.to_id = to_id;
    }

    public void setMsg_status(int msg_status) {
        this.msg_status = msg_status;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
