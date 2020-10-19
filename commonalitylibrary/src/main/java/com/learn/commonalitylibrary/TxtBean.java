package com.learn.commonalitylibrary;

import java.io.Serializable;

public class TxtBean implements Serializable {
    String local_path;
    String txt_name;
    String cover_print;
    Boolean isChecked = false;

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public String getLocal_path() {
        return local_path;
    }

    public void setLocal_path(String local_path) {
        this.local_path = local_path;
    }

    public String getTxt_name() {
        return txt_name;
    }

    public void setTxt_name(String txt_name) {
        this.txt_name = txt_name;
    }

    public String getCover_print() {
        return cover_print;
    }

    public void setCover_print(String cover_print) {
        this.cover_print = cover_print;
    }
}
