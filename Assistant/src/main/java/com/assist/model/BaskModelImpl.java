package com.assist.model;
import com.assist.contract.BaskContract;

import java.io.Serializable;

/**
* Created by zhiPeng.S on 2016/11/07
*/

public class BaskModelImpl implements BaskContract.Model, Serializable{

    /**
     * timeid : 100009
     * goodid : 1000
     * title : Apple iPhone6s Plus 64G 颜色随机
     * headpic : http://onegoods.nosdn.127.net/goods/898/d3dc4b84825a35c50e2b5504d2b636cc.png
     * uidx : 50001
     * nickname : 13800571505
     * luckuidx : 50001
     * luckid : 1747
     * buycopies : 7280
     */

    private long timeid;
    private long goodid;
    private String title;
    private String headpic;
    private long uidx;
    private String nickname;
    private long luckuidx;
    private long luckid;
    private long buycopies;

    public long getTimeid() {
        return timeid;
    }

    public void setTimeid(long timeid) {
        this.timeid = timeid;
    }

    public long getGoodid() {
        return goodid;
    }

    public void setGoodid(long goodid) {
        this.goodid = goodid;
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

    public long getUidx() {
        return uidx;
    }

    public void setUidx(long uidx) {
        this.uidx = uidx;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getLuckuidx() {
        return luckuidx;
    }

    public void setLuckuidx(long luckuidx) {
        this.luckuidx = luckuidx;
    }

    public long getLuckid() {
        return luckid;
    }

    public void setLuckid(long luckid) {
        this.luckid = luckid;
    }

    public long getBuycopies() {
        return buycopies;
    }

    public void setBuycopies(long buycopies) {
        this.buycopies = buycopies;
    }
}