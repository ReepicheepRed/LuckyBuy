package com.luckybuy.model;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/6/22.
 */
public class DetailWaitModel implements Serializable {

    private String status;

    private long timesid;

    private long saled;

    private long total;

    private String lucktime;

    private long buycopies;

    private String buyid;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimeid() {
        return timesid;
    }

    public void setTimeid(long timesid) {
        this.timesid = timesid;
    }

    public long getSaled() {
        return saled;
    }

    public void setSaled(long saled) {
        this.saled = saled;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getLucktime() {
        return lucktime;
    }

    public void setLucktime(String lucktime) {
        this.lucktime = lucktime;
    }

    public long getBuycopies() {
        return buycopies;
    }

    public void setBuycopies(long buycopies) {
        this.buycopies = buycopies;
    }

    public String getBuyid() {
        return buyid;
    }

    public void setBuyid(String buyid) {
        this.buyid = buyid;
    }
}
