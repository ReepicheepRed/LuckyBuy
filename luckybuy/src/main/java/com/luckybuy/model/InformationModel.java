package com.luckybuy.model;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/10/14.
 */

public class InformationModel implements Serializable {


    /**
     * idx : 3
     * title : Hello Edit
     * ctype : 1
     * fdi : 1047
     * content : 其他消息，查看内容this is a edit notice.No Show
     * udate : 2016-05-11T10:00:23.507
     * link: http://www.10bbuy.com,
     */

    private long idx;
    private String title;
    private long ctype;
    private long fdi;
    private String content;
    private String udate;
    private String link;

    public long getIdx() {
        return idx;
    }

    public void setIdx(long idx) {
        this.idx = idx;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCtype() {
        return ctype;
    }

    public void setCtype(long ctype) {
        this.ctype = ctype;
    }

    public long getFdi() {
        return fdi;
    }

    public void setFdi(long fdi) {
        this.fdi = fdi;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUdate() {
        return udate;
    }

    public void setUdate(String udate) {
        this.udate = udate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
