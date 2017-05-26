package com.luckybuy.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/6/30.
 */
public class PayResultModel implements Serializable {

    public Fail failobject;

    public Success successobject;

    public static class Fail{
        public long failmoney;

        public List<ResultItemModel> faillist;

        public long getFailmoney() {
            return failmoney;
        }

        public void setFailmoney(long failmoney) {
            this.failmoney = failmoney;
        }

        public List<ResultItemModel> getFaillist() {
            return faillist;
        }

        public void setFaillist(List<ResultItemModel> faillist) {
            this.faillist = faillist;
        }

        @Override
        public String toString() {
            return "Fail{" +
                    "failmoney=" + failmoney +
                    ", faillist=" + faillist +
                    '}';
        }
    }

    public static class Success{
        public long successmoney;

        public List<ResultItemModel> successlist;

        public long getSuccessmoney() {
            return successmoney;
        }

        public void setSuccessmoney(long successmoney) {
            this.successmoney = successmoney;
        }

        public List<ResultItemModel> getSuccesslist() {
            return successlist;
        }

        public void setSuccesslist(List<ResultItemModel> successlist) {
            this.successlist = successlist;
        }

        @Override
        public String toString() {
            return "Success{" +
                    "successmoney=" + successmoney +
                    ", successlist=" + successlist +
                    '}';
        }
    }

    public Fail getFailobject() {
        return failobject;
    }

    public void setFailobject(Fail failobject) {
        this.failobject = failobject;
    }

    public Success getSuccessobject() {
        return successobject;
    }

    public void setSuccessobject(Success successobject) {
        this.successobject = successobject;
    }

    @Override
    public String toString() {
        return "PayResultModel{" +
                "failobject=" + failobject +
                ", successobject=" + successobject +
                '}';
    }
}
