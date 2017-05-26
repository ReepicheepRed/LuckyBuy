package com.luckybuy.network;

import org.json.JSONObject;

/**
 * Created by zhiPeng.S on 2016/5/19.
 */
public class UploadData {

    public static String UploadData_Banner(){
        JSONObject mJsonObject = new JSONObject();
        try {
            mJsonObject.put("adPicType", "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mJsonObject.toString();
    }
}
