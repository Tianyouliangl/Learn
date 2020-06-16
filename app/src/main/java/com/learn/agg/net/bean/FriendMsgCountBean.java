package com.learn.agg.net.bean;

public class FriendMsgCountBean{
    /**
     * from_id : 39c036c5-1fa7-4f6d-86f9-3dcd5eb8b0f6
     * friend_type : 2
     * to_id : 241c311f-be28-4417-9203-c5fae65d23d5
     * pid : 0a1b2c3d4e56785f53f9
     * source : 1
     * content : 我是吴成亚
     */

    private String from_id;
    private int friend_type;
    private String to_id;
    private String pid;
    private int source;
    private String content;

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public int getFriend_type() {
        return friend_type;
    }

    public void setFriend_type(int friend_type) {
        this.friend_type = friend_type;
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
