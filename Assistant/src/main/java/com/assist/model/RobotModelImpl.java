package com.assist.model;
import com.assist.contract.RobotContract;

/**
* Created by zhiPeng.S on 2016/11/03
*/

public class RobotModelImpl implements RobotContract.Model{

    /**
     * goodid : 1008
     * timesid : 100622
     * title : 小米移动电源(16000mA)
     * headpic : http://onegoods.nosdn.127.net/goods/509/2be85a474e40537dae5b333cc601fa33.png
     * saled : 157
     * total : 159
     * price : 159
     * persize : 1
     */

    private GoodinfoBean goodinfo;
    /**
     * uidx : 50001
     * nickname : 13800571505
     * headpic : http://192.168.1.188/images/201607/19/1521303514.jpg
     * money : 82199
     * luckcoin : 363
     */

    private MemberinfoBean memberinfo;

    public GoodinfoBean getGoodinfo() {
        return goodinfo;
    }

    public void setGoodinfo(GoodinfoBean goodinfo) {
        this.goodinfo = goodinfo;
    }

    public MemberinfoBean getMemberinfo() {
        return memberinfo;
    }

    public void setMemberinfo(MemberinfoBean memberinfo) {
        this.memberinfo = memberinfo;
    }

    public static class GoodinfoBean {
        private int goodid;
        private int timesid;
        private String title;
        private String headpic;
        private long saled;
        private long total;
        private long price;
        private long persize;

        public int getGoodid() {
            return goodid;
        }

        public void setGoodid(int goodid) {
            this.goodid = goodid;
        }

        public int getTimesid() {
            return timesid;
        }

        public void setTimesid(int timesid) {
            this.timesid = timesid;
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

        public long getSaled() {
            return saled;
        }

        public void setSaled(long saled) {
            this.saled = saled;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public long getPrice() {
            return price;
        }

        public void setPrice(long price) {
            this.price = price;
        }

        public long getPersize() {
            return persize;
        }

        public void setPersize(long persize) {
            this.persize = persize;
        }
    }

    public static class MemberinfoBean {
        private long uidx;
        private String nickname;
        private String headpic;
        private long money;
        private long luckcoin;

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

        public String getHeadpic() {
            return headpic;
        }

        public void setHeadpic(String headpic) {
            this.headpic = headpic;
        }

        public long getMoney() {
            return money;
        }

        public void setMoney(long money) {
            this.money = money;
        }

        public long getLuckcoin() {
            return luckcoin;
        }

        public void setLuckcoin(long luckcoin) {
            this.luckcoin = luckcoin;
        }
    }
}