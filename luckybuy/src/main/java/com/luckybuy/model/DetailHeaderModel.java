package com.luckybuy.model;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/6/22.
 */
public class DetailHeaderModel implements Serializable {

    private long idx;

    private long timeid;

    private long goodid;

    private long saled;

    private long total;

    private String title;

    private String subtitle;

    private String headpic;

    private String checkindate;

    private DetailBannerModel goodIMG;

    private long persize;

    class DetailBannerModel{
        private long goodid;

        private String img;

        public long getGoodid() {
            return goodid;
        }

        public void setGoodid(long goodid) {
            this.goodid = goodid;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }
}
