package com.luckybuy.model;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/7/8.
 */
public class WinRecordModel implements Serializable{

    private long goodid;

    private long timesid;

    private String title;

    private String subtitle;

    private String headpic;

    private long total;

    private long copies;

    private long luckid;

    private String lucktime;

    private boolean iscomplete;

    private boolean hasaddress;

    private boolean haslogistic;

    private boolean isvirtual;

    public long getGoodid() {
        return goodid;
    }

    public void setGoodid(long goodid) {
        this.goodid = goodid;
    }

    public long getCopies() {
        return copies;
    }

    public void setCopies(long copies) {
        this.copies = copies;
    }

    public long getTimesid() {
        return timesid;
    }

    public void setTimesid(long timesid) {
        this.timesid = timesid;
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

    public String getLuckytime() {
        return lucktime;
    }

    public void setLuckytime(String luckytime) {
        this.lucktime = luckytime;
    }

    public long getLuckyid() {
        return luckid;
    }

    public void setLuckyid(long luckyid) {
        this.luckid = luckyid;
    }

    public boolean iscomplete() {
        return iscomplete;
    }

    public void setIscomplete(boolean iscomplete) {
        this.iscomplete = iscomplete;
    }

    public boolean isHasaddress() {
        return hasaddress;
    }

    public void setHasaddress(boolean hasaddress) {
        this.hasaddress = hasaddress;
    }

    public boolean isHaslogistic() {
        return haslogistic;
    }

    public void setHaslogistic(boolean haslogistic) {
        this.haslogistic = haslogistic;
    }

    public boolean isvirtual() {
        return isvirtual;
    }

    public void setIsvirtual(boolean isvirtual) {
        this.isvirtual = isvirtual;
    }

    @Override
    public String toString() {
        return "WinRecordModel{" +
                "goodid=" + goodid +
                ", timesid=" + timesid +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", headpic='" + headpic + '\'' +
                ", total=" + total +
                ", copies=" + copies +
                ", luckid=" + luckid +
                ", lucktime='" + lucktime + '\'' +
                ", iscomplete=" + iscomplete +
                ", hasaddress=" + hasaddress +
                ", haslogistic=" + haslogistic +
                ", isvirtual=" + isvirtual +
                '}';
    }
}
