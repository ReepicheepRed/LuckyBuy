package com.luckybuy.model;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/7/21.
 */
public class WinInfoModel implements Serializable{

    private WinRecordModel timesinfo;

    private LogisticModel logisticinfo;

    private SignModel signinfo;

    private CompleteModel completeinfo;

    private AddressModel addressinfo;

    public static class LogisticModel{
        private String ldate;

        private String address;

        private String company;

        private String hawb;

        public String getLdate() {
            return ldate;
        }

        public void setLdate(String ldate) {
            this.ldate = ldate;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getHawb() {
            return hawb;
        }

        public void setHawb(String hawb) {
            this.hawb = hawb;
        }

        @Override
        public String toString() {
            return "LogisticModel{" +
                    "ldate='" + ldate + '\'' +
                    ", address='" + address + '\'' +
                    ", company='" + company + '\'' +
                    ", hawb='" + hawb + '\'' +
                    '}';
        }
    }

    public static class SignModel{
        private String cdate;

        public String getCdate() {
            return cdate;
        }

        public void setCdate(String cdate) {
            this.cdate = cdate;
        }

        @Override
        public String toString() {
            return "SignModel{" +
                    "cdate='" + cdate + '\'' +
                    '}';
        }
    }

    public static class CompleteModel{
        private boolean iscomplete;

        public boolean iscomplete() {
            return iscomplete;
        }

        public void setIscomplete(boolean iscomplete) {
            this.iscomplete = iscomplete;
        }

        @Override
        public String toString() {
            return "CompleteModel{" +
                    "iscomplete=" + iscomplete +
                    '}';
        }
    }

    public WinRecordModel getTimesinfo() {
        return timesinfo;
    }

    public void setTimesinfo(WinRecordModel timesinfo) {
        this.timesinfo = timesinfo;
    }

    public LogisticModel getLogisticinfo() {
        return logisticinfo;
    }

    public void setLogisticinfo(LogisticModel logisticinfo) {
        this.logisticinfo = logisticinfo;
    }

    public SignModel getSigninfo() {
        return signinfo;
    }

    public void setSigninfo(SignModel signinfo) {
        this.signinfo = signinfo;
    }

    public CompleteModel getCompleteinfo() {
        return completeinfo;
    }

    public void setCompleteinfo(CompleteModel completeinfo) {
        this.completeinfo = completeinfo;
    }

    public AddressModel getAddressinfo() {
        return addressinfo;
    }

    public void setAddressinfo(AddressModel addressinfo) {
        this.addressinfo = addressinfo;
    }

    @Override
    public String toString() {
        return "WinInfoModel{" +
                "timesinfo=" + timesinfo +
                ", logisticinfo=" + logisticinfo +
                ", signinfo=" + signinfo +
                ", completeinfo=" + completeinfo +
                ", addressinfo=" + addressinfo +
                '}';
    }
}
