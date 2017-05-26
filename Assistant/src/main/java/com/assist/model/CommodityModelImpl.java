package com.assist.model;
import com.assist.contract.CommodityContract;

import java.io.Serializable;

/**
* Created by zhiPeng.S on 2016/11/03
*/

public class CommodityModelImpl implements CommodityContract.Model,Serializable{


    /**
     * goodid : 1000
     * title : Apple iPhone6s Plus 64G 颜色随机
     * headpic : http://onegoods.nosdn.127.net/goods/898/d3dc4b84825a35c50e2b5504d2b636cc.png
     * presize : 1
     * price : 7280
     */

    private long goodid;
    private String title;
    private String headpic;
    private long presize;
    private long price;

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

    public long getPresize() {
        return presize;
    }

    public void setPresize(long presize) {
        this.presize = presize;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}