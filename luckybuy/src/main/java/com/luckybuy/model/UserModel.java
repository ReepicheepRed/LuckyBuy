package com.luckybuy.model;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/6/23.
 */
public class UserModel implements Serializable {

    private long uidx;

    private String fbuserid;

    private String headpic;

    private String nickname;

    private long timesid;

    private long money;

    private String mobile;

    private long luckcoin;

    public long getUidx() {
        return uidx;
    }

    public void setUidx(long uidx) {
        this.uidx = uidx;
    }

    public String getFbuserid() {
        return fbuserid;
    }

    public void setFbuserid(String fbuserid) {
        this.fbuserid = fbuserid;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public long getLuckcoin() {
        return luckcoin;
    }

    public void setLuckcoin(long luckcoin) {
        this.luckcoin = luckcoin;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "uidx=" + uidx +
                ", headpic='" + headpic + '\'' +
                ", nickname='" + nickname + '\'' +
                ", timesid=" + timesid +
                ", money=" + money +
                ", mobile='" + mobile + '\'' +
                ", luckcoin=" + luckcoin +
                '}';
    }
}
