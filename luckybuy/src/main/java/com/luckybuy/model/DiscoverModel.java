package com.luckybuy.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/7/19.
 */
public class DiscoverModel implements Serializable{
    private long sidx;

    private long goodid;

    private long timesid;

    private String title;

    private String subtitle;

    private String intro;

    private long uidx;

    private List<BaskImage> images;

    private boolean isliked;

    private long likes;

    private String udate;

    private long luckid;

    private String headpic;

    private String nickname;

    public static class BaskImage{
        private long sidx;

        private String img;

        public long getSidx() {
            return sidx;
        }

        public void setSidx(long sidx) {
            this.sidx = sidx;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        @Override
        public String toString() {
            return "BaskImage{" +
                    "sidx=" + sidx +
                    ", img='" + img + '\'' +
                    '}';
        }
    }

    public long getSidx() {
        return sidx;
    }

    public void setSidx(long sidx) {
        this.sidx = sidx;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getGoodid() {
        return goodid;
    }

    public void setGoodid(long goodid) {
        this.goodid = goodid;
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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public long getUidx() {
        return uidx;
    }

    public void setUidx(long uidx) {
        this.uidx = uidx;
    }

    public List<BaskImage> getImages() {
        return images;
    }

    public void setImages(List<BaskImage> images) {
        this.images = images;
    }

    public boolean isliked() {
        return isliked;
    }

    public void setIsliked(boolean isliked) {
        this.isliked = isliked;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public String getUdate() {
        return udate;
    }

    public void setUdate(String udate) {
        this.udate = udate;
    }

    public long getLuckid() {
        return luckid;
    }

    public void setLuckid(long luckid) {
        this.luckid = luckid;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    @Override
    public String toString() {
        return "DiscoverModel{" +
                "sidx=" + sidx +
                ", goodid=" + goodid +
                ", timesid=" + timesid +
                ", intro='" + intro + '\'' +
                ", uidx=" + uidx +
                ", images=" + images +
                ", isliked=" + isliked +
                ", likes=" + likes +
                ", udate='" + udate + '\'' +
                ", luckid=" + luckid +
                ", headpic='" + headpic + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
