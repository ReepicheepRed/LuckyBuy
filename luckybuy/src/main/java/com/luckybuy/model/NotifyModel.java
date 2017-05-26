package com.luckybuy.model;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/7/12.
 */
public class NotifyModel implements Serializable{

    private int type;

    private String ctext;

    private String title;

    private AwardModel content;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCtext() {
        return ctext;
    }

    public void setCtext(String ctext) {
        this.ctext = ctext;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AwardModel getContent() {
        return content;
    }

    public void setContent(AwardModel content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "NotifyModel{" +
                "type=" + type +
                ", ctext='" + ctext + '\'' +
                ", title='" + title + '\'' +
                ", content=" + content +
                '}';
    }
}
