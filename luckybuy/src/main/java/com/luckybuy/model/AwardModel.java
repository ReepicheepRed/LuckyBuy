package com.luckybuy.model;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/6/1.
 */
@Table(name = "cart")
public class AwardModel implements Serializable{

    @Column(name = "id", isId = true)
    private long id;

    @Column(name = "idx")
    private long idx;

    @Column(name = "title")
    private String title;

    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "headpic")
    private String headpic;

    @Column(name = "total")
    private long total;

    @Column(name = "saled")
    private long saled;

    @Column(name = "timesid")
    private long timesid;

    @Column(name = "copies")
    private long copies;

    @Column(name = "persize")
    private long persize;

    private boolean isDeleteAward;

    public boolean isDeleteAward() {
        return isDeleteAward;
    }

    public void setDeleteAward(boolean deleteAward) {
        isDeleteAward = deleteAward;
    }


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

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getSaled() {
        return saled;
    }

    public void setSaled(long saled) {
        this.saled = saled;
    }

    public long getTimeid() {
        return timesid;
    }

    public void setTimeid(long timesid) {
        this.timesid = timesid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCopies() {
        return copies;
    }

    public void setCopies(long copies) {
        this.copies = copies;
    }

    public long getPersize() {
        return persize;
    }

    public void setPersize(long persize) {
        this.persize = persize;
    }

    @Override
    public String toString() {
        return "AwardModel{" +
                "id=" + id +
                ", idx=" + idx +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", headpic='" + headpic + '\'' +
                ", total=" + total +
                ", saled=" + saled +
                ", timesid=" + timesid +
                ", copies=" + copies +
                ", isDeleteAward=" + isDeleteAward +
                '}';
    }


}
