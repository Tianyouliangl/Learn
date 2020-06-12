package com.learn.commonalitylibrary.body;

public class VoiceBody {
    private String fileName;
    private String fileAbsPath;
    private String url;
    private long time;
    private int state; // 1 显示 0 不显示
    private String voice_content;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getVoice_content() {
        return voice_content;
    }

    public void setVoice_content(String voice_content) {
        this.voice_content = voice_content;
    }

    public VoiceBody(String fileAbsPath, String url, long time) {
        this.fileAbsPath = fileAbsPath;
        this.url = url;
        this.time = time;
        this.state = 0;
        this.voice_content = "";
    }

    public VoiceBody(String fileAbsPath, String url, long time, int state, String voice_content) {
        this.fileAbsPath = fileAbsPath;
        this.url = url;
        this.time = time;
        this.state = state;
        this.voice_content = voice_content;
    }

    public VoiceBody(String fileName, String fileAbsPath, String url, long time, int state, String voice_content) {
        this.fileName = fileName;
        this.fileAbsPath = fileAbsPath;
        this.url = url;
        this.time = time;
        this.state = state;
        this.voice_content = voice_content;
    }

    public String getFileAbsPath() {
        return fileAbsPath;
    }

    public void setFileAbsPath(String fileAbsPath) {
        this.fileAbsPath = fileAbsPath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
