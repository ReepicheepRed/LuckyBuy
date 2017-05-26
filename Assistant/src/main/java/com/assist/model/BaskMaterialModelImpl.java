package com.assist.model;
import com.assist.contract.BaskMaterialContract;

import java.io.Serializable;
import java.util.List;

/**
* Created by zhiPeng.S on 2016/11/08
*/

public class BaskMaterialModelImpl implements BaskMaterialContract.Model,Serializable{

    /**
     * lbidx : 1003
     * goodid : 1021
     * timeid : 0
     * status : false
     * contents : 8888888
     * imginfo : [{"lbidx":1003,"img":"http://192.168.1.188/images/201611/08/1016118120.png","status":false},{"lbidx":1003,"img":"http://192.168.1.188/images/201611/08/1016119000.jpg","status":false},{"lbidx":1003,"img":"http://192.168.1.188/images/201611/08/1016119400.png","status":false}]
     */

    private long lbidx;
    private long goodid;
    private long timeid;
    private boolean status;
    private String contents;
    /**
     * lbidx : 1003
     * img : http://192.168.1.188/images/201611/08/1016118120.png
     * status : false
     */

    private List<ImginfoBean> imginfo;

    public long getLbidx() {
        return lbidx;
    }

    public void setLbidx(long lbidx) {
        this.lbidx = lbidx;
    }

    public long getGoodid() {
        return goodid;
    }

    public void setGoodid(long goodid) {
        this.goodid = goodid;
    }

    public long getTimeid() {
        return timeid;
    }

    public void setTimeid(long timeid) {
        this.timeid = timeid;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public List<ImginfoBean> getImginfo() {
        return imginfo;
    }

    public void setImginfo(List<ImginfoBean> imginfo) {
        this.imginfo = imginfo;
    }

    public static class ImginfoBean {
        private long lbidx;
        private String img;
        private boolean status;

        public long getLbidx() {
            return lbidx;
        }

        public void setLbidx(long lbidx) {
            this.lbidx = lbidx;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }
    }
}