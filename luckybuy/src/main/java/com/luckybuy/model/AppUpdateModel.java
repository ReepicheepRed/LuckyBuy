package com.luckybuy.model;

import java.io.Serializable;

/**
 * Created by zhiPeng.S on 2016/8/24.
 */
public class AppUpdateModel implements Serializable{

    private String productname;
    private String versionnumber;
    private long edtiontype;
    private String downloadaddress;
    private String downloadpic;
    private String description;
    private boolean iscompelupgrade;
    private String packagename;
    private String starttime;
    private int versioncode;

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getVersionnumber() {
        return versionnumber;
    }

    public void setVersionnumber(String versionnumber) {
        this.versionnumber = versionnumber;
    }

    public long getEdtiontype() {
        return edtiontype;
    }

    public void setEdtiontype(long edtiontype) {
        this.edtiontype = edtiontype;
    }

    public String getDownloadaddress() {
        return downloadaddress;
    }

    public void setDownloadaddress(String downloadaddress) {
        this.downloadaddress = downloadaddress;
    }

    public String getDownloadpic() {
        return downloadpic;
    }

    public void setDownloadpic(String downloadpic) {
        this.downloadpic = downloadpic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean iscompelupgrade() {
        return iscompelupgrade;
    }

    public void setIscompelupgrade(boolean iscompelupgrade) {
        this.iscompelupgrade = iscompelupgrade;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public int getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(int versioncode) {
        this.versioncode = versioncode;
    }
}
