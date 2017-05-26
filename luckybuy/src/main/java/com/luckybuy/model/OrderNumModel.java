package com.luckybuy.model;

import com.luckybuy.share.ShareUtils;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/8/31.
 */
public class OrderNumModel implements Serializable{

    private String ordernumber;
    private long uidx;
    private long amount;
    private long money;

    public String getOrdernumber() {
        return ordernumber;
    }

    public void setOrdernumber(String ordernumber) {
        this.ordernumber = ordernumber;
    }

    public long getUidx() {
        return uidx;
    }

    public void setUidx(long uidx) {
        this.uidx = uidx;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }
}
