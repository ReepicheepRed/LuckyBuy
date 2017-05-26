package com.luckybuy.model;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/10/17.
 */

public class DiamondMissionModel implements Serializable {

    /**
     * title : 绑定Facebook
     * sortid : 1
     * comment : 绑定Facebook就送50个钻石
     * isdaily : false
     * luckcoin : 50
     * headpic : http://192.168.1.188/common/image/task/1.png
     * iscomplete : 0
     * headpic2 : http://192.168.1.188/common/image/task/1_c.png
     */

    private String title;
    private long sortid;
    private String comment;
    private boolean isdaily;
    private long luckcoin;
    private String headpic;
    private long iscomplete;
    private String headpic2;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getSortid() {
        return sortid;
    }

    public void setSortid(long sortid) {
        this.sortid = sortid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isIsdaily() {
        return isdaily;
    }

    public void setIsdaily(boolean isdaily) {
        this.isdaily = isdaily;
    }

    public long getLuckcoin() {
        return luckcoin;
    }

    public void setLuckcoin(long luckcoin) {
        this.luckcoin = luckcoin;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public long getIscomplete() {
        return iscomplete;
    }

    public void setIscomplete(long iscomplete) {
        this.iscomplete = iscomplete;
    }

    public String getHeadpic2() {
        return headpic2;
    }

    public void setHeadpic2(String headpic2) {
        this.headpic2 = headpic2;
    }
}
