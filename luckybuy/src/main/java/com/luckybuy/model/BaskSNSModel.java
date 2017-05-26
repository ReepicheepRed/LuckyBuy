package com.luckybuy.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/7/16.
 */
public class BaskSNSModel implements Serializable {

    /**
     * pos : 1
     * timesid :
     * uidx :
     * sidx :
     * title : 百草味 2袋组合装 | 香辣/五香牛肉粒100g/袋
     * subtitle : 超值
     * goodheadpic : http://onegoods.nosdn.127.net/goods/1232/b579bf18c87f838804663f9998cb5b25.jpg
     * intro : 是我中奖了)
     * liked : 4
     * udate : 2016-06-30T10:00:00
     * img : http://192.168.1.188/images/default/good_head1.png
     * imgidx : 8
     * auditstatus : 1
     * headpic : http://192.168.1.188/images/default/member_head.png
     * nickname : testuser2
     */

    private long pos;
    private long timesid;
    private long uidx;
    private long sidx;
    private String title;
    private String subtitle;
    private String goodheadpic;
    private String intro;
    private long liked;
    private String udate;
    private String img;
    private List<DiscoverModel.BaskImage> imgUrl;
    private long imgidx;
    private long auditstatus;
    private String headpic;
    private String nickname;
    private long luckid;

    public long getPos() {
        return pos;
    }

    public void setPos(long pos) {
        this.pos = pos;
    }

    public long getTimesid() {
        return timesid;
    }

    public void setTimesid(long timesid) {
        this.timesid = timesid;
    }

    public long getUidx() {
        return uidx;
    }

    public void setUidx(long uidx) {
        this.uidx = uidx;
    }

    public long getSidx() {
        return sidx;
    }

    public void setSidx(long sidx) {
        this.sidx = sidx;
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

    public String getGoodheadpic() {
        return goodheadpic;
    }

    public void setGoodheadpic(String goodheadpic) {
        this.goodheadpic = goodheadpic;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public long getLiked() {
        return liked;
    }

    public void setLiked(long liked) {
        this.liked = liked;
    }

    public String getUdate() {
        return udate;
    }

    public void setUdate(String udate) {
        this.udate = udate;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<DiscoverModel.BaskImage> getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(List<DiscoverModel.BaskImage> imgUrl) {
        this.imgUrl = imgUrl;
    }

    public long getImgidx() {
        return imgidx;
    }

    public void setImgidx(long imgidx) {
        this.imgidx = imgidx;
    }

    public long getAuditstatus() {
        return auditstatus;
    }

    public void setAuditstatus(long auditstatus) {
        this.auditstatus = auditstatus;
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

    public long getLuckid() {
        return luckid;
    }

    public void setLuckid(long luckid) {
        this.luckid = luckid;
    }

    @Override
    public String toString() {
        return "BaskSNSModel{" +
                "pos=" + pos +
                ", timesid=" + timesid +
                ", uidx=" + uidx +
                ", sidx=" + sidx +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", goodheadpic='" + goodheadpic + '\'' +
                ", intro='" + intro + '\'' +
                ", liked=" + liked +
                ", udate='" + udate + '\'' +
                ", img='" + img + '\'' +
                ", imgUrl=" + imgUrl +
                ", imgidx=" + imgidx +
                ", auditstatus=" + auditstatus +
                ", headpic='" + headpic + '\'' +
                ", nickname='" + nickname + '\'' +
                ", luckid=" + luckid +
                '}';
    }
}
