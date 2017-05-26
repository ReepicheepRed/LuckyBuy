package com.luckybuy.model;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/8/12.
 */
public class PayMethodModel implements Serializable{

    private int payidx;
    private String title;
    private String headpic;
    private String bodypic;

    public int getPayidx() {
        return payidx;
    }

    public void setPayidx(int payidx) {
        this.payidx = payidx;
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

    public String getBodypic() {
        return bodypic;
    }

    public void setBodypic(String bodypic) {
        this.bodypic = bodypic;
    }
}
