package com.learn.agg.net.bean;

public class FriendMsgBean {
    private String from_id;
    private String to_id;
    private String pid;
    private int friend_type;
    private int source;
    private String content;

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

    public int getFriend_type() {
        return friend_type;
    }

    public void setFriend_type(int friend_type) {
        this.friend_type = friend_type;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
