package com.assist.model;
import com.assist.contract.SelectUserContract;

import java.io.Serializable;
import java.util.List;

/**
* Created by zhiPeng.S on 2016/11/03
*/

public class SelectUserModelImpl implements SelectUserContract.Model,Serializable {


    /**
     * usercount : 56
     * freeusercount : 52
     * leveL1USERCOUNT : 52
     * leveL2COUNTUSER : 2
     * leveL3USERCOUNT : 2
     * list : []
     */

    private long usercount;
    private long freeusercount;
    private long leveL1USERCOUNT;
    private long leveL2COUNTUSER;
    private long leveL3USERCOUNT;
    private List<DetailInfo> list;

    public long getUsercount() {
        return usercount;
    }

    public void setUsercount(long usercount) {
        this.usercount = usercount;
    }

    public long getFreeusercount() {
        return freeusercount;
    }

    public void setFreeusercount(long freeusercount) {
        this.freeusercount = freeusercount;
    }

    public long getLeveL1USERCOUNT() {
        return leveL1USERCOUNT;
    }

    public void setLeveL1USERCOUNT(long leveL1USERCOUNT) {
        this.leveL1USERCOUNT = leveL1USERCOUNT;
    }

    public long getLeveL2COUNTUSER() {
        return leveL2COUNTUSER;
    }

    public void setLeveL2COUNTUSER(long leveL2COUNTUSER) {
        this.leveL2COUNTUSER = leveL2COUNTUSER;
    }

    public long getLeveL3USERCOUNT() {
        return leveL3USERCOUNT;
    }

    public void setLeveL3USERCOUNT(long leveL3USERCOUNT) {
        this.leveL3USERCOUNT = leveL3USERCOUNT;
    }

    public List<DetailInfo> getList() {
        return list;
    }

    public void setList(List<DetailInfo> list) {
        this.list = list;
    }

    public class DetailInfo implements Serializable{

        /**
         * uidx : 50004
         * isused : false
         * usetype : 1
         * lockdate : 2016-11-03T11:25:39.357
         * expdate : 2016-11-10T11:25:39.357
         * nickname : test4@qq.com
         * headpic : facebooktest4HEADPIC.JPG
         * money : 0
         * luckcoin : 0
         */

        private long uidx;
        private boolean isused;
        private int usetype;
        private String lockdate;
        private String expdate;
        private String nickname;
        private String headpic;
        private long money;
        private int luckcoin;

        public long getUidx() {
            return uidx;
        }

        public void setUidx(long uidx) {
            this.uidx = uidx;
        }

        public boolean isIsused() {
            return isused;
        }

        public void setIsused(boolean isused) {
            this.isused = isused;
        }

        public int getUsetype() {
            return usetype;
        }

        public void setUsetype(int usetype) {
            this.usetype = usetype;
        }

        public String getLockdate() {
            return lockdate;
        }

        public void setLockdate(String lockdate) {
            this.lockdate = lockdate;
        }

        public String getExpdate() {
            return expdate;
        }

        public void setExpdate(String expdate) {
            this.expdate = expdate;
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

        public int getLuckcoin() {
            return luckcoin;
        }

        public void setLuckcoin(int luckcoin) {
            this.luckcoin = luckcoin;
        }
    }
}