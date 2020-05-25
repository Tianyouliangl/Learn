package com.learn.commonalitylibrary.body;

/**
 * author : fengzhangwei
 * date : 2020/1/13
 */
public class ChatRedEnvelopeBean {

    /**
     * from_id : 50cf3279-db28-49f7-bc87-797a8cc7836d
     * to_id : 77aa7031-9fab-4199-84a2-a180c399b0bb
     * pid : AN49T185YR286881
     * time : 1578900726882
     * body : {"money":"2"}
     * conversation : 0a1b2c3d456786a97f9
     * status : 5
     */

    private String from_id;
    private String to_id;
    private String pid;
    private long time;
    private String body;
    private String conversation;
    private int status;

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getTo_id() {
        return to_id;
    }

    public void setTo_id(String to_id) {
        this.to_id = to_id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
