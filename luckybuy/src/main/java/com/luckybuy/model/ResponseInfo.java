package com.luckybuy.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/5/19.
 */
public class ResponseInfo implements Serializable {
    private String requestMethod;

    private String returnContent;

    private String returnCode;

    public String getReturnContent() {
        return returnContent;
    }

    public void setReturnContent(String returnContent) {
        this.returnContent = returnContent;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }
}
