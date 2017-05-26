package com.luckybuy.model;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/6/30.
 */
public class ResultItemModel implements Serializable {

    private String ordernumber;

    private long timesid;

    private long money;

    private long copies;

    private String title;

    private String headpic;

    private String luckid;

    private int  status;

    public String getOrdernumber() {
        return ordernumber;
    }

    public void setOrdernumber(String ordernumber) {
        this.ordernumber = ordernumber;
    }

    public long getTimesid() {
        return timesid;
    }

    public void setTimesid(long timesid) {
        this.timesid = timesid;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public long getCopies() {
        return copies;
    }

    public void setCopies(long copies) {
        this.copies = copies;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getLuckid() {
        return luckid;
    }

    public void setLuckid(String luckid) {
        this.luckid = luckid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ResultItemModel{" +
                "ordernumber='" + ordernumber + '\'' +
                ", timesid=" + timesid +
                ", money=" + money +
                ", copies=" + copies +
                ", title='" + title + '\'' +
                ", headpic='" + headpic + '\'' +
                ", luckid='" + luckid + '\'' +
                ", status=" + status +
                '}';
    }
}
