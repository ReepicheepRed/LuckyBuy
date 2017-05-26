package com.luckybuy.network;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luckybuy.model.AddressModel;
import com.luckybuy.model.AwardFrsModel;
import com.luckybuy.model.AwardModel;
import com.luckybuy.model.BaskSNSModel;
import com.luckybuy.model.BulletinModel;
import com.luckybuy.model.DetailHistoryModel;
import com.luckybuy.model.DetailUnveilModel;
import com.luckybuy.model.DetailWaitModel;
import com.luckybuy.model.DiamondMissionModel;
import com.luckybuy.model.DiscoverModel;
import com.luckybuy.model.FriendsModel;
import com.luckybuy.model.InformationModel;
import com.luckybuy.model.PayMethodModel;
import com.luckybuy.model.PayResultModel;
import com.luckybuy.model.BannerModel;
import com.luckybuy.model.SnatchAwardModel;
import com.luckybuy.model.UnveilAwardModel;
import com.luckybuy.model.VerifyCodeModel;
import com.luckybuy.model.WinRecordModel;
import com.luckybuy.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/5/19.
 */
public class ParseData {

    private static Gson gson = new GsonBuilder().serializeNulls().create();

    public static Map<String, Object> parseBannerInfo(String bannerInfo){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Gson gson = new Gson();
        List<BannerModel> mBannerModel = gson.fromJson(bannerInfo, new TypeToken<List<BannerModel>>(){}.getType());
        resultMap.put(Constant.BANNER_LIST, mBannerModel);
        return resultMap;
    }

    public static Map<String, Object> parseBulletinInfo(String bulletinInfo){
        Map<String, Object> resultMap = new HashMap<>();
        Gson gson = new GsonBuilder().serializeNulls().create();
        List<BulletinModel> model = gson.fromJson(bulletinInfo,new TypeToken<List<BulletinModel>>(){}.getType());
        resultMap.put(Constant.BULLETIN_LIST, model);
        return resultMap;
    }

    /**
     * parse Award data
     * @author Reepicheep
     * Created at 2016/6/1 11:30
     */
    public static Map<String, Object> parseAwardInfo(String awardInfo){
        Map<String, Object> resultMap = new HashMap<>();
        Gson gson = new Gson();
        List<AwardModel> modelData = gson.fromJson(awardInfo, new TypeToken<List<AwardModel>>(){}.getType());
        resultMap.put(Constant.AWARD_LIST, modelData);
        return resultMap;
    }


    public static Map<String, Object> parseAwardFrsInfo(String awardInfo){
        Map<String, Object> resultMap = new HashMap<>();
        Gson gson = new Gson();
        List<AwardFrsModel> modelData = gson.fromJson(awardInfo, new TypeToken<List<AwardFrsModel>>(){}.getType());
        resultMap.put(Constant.AWARD_LIST, modelData);
        return resultMap;
    }

    /**
     * parse Unveil Award data
     * @author Reepicheep
     * Created at 2016/6/8 15:30
     */
    public static Map<String, Object> parseUnveilAwardInfo(String awardInfo){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Gson gson = new GsonBuilder().serializeNulls().create();
        List<UnveilAwardModel> modelData = gson.fromJson(awardInfo, new TypeToken<List<UnveilAwardModel>>(){}.getType());
        resultMap.put(Constant.AWARD_LIST, modelData);
        return resultMap;
    }

    /**
     * parse Award Detail History data
     * @author Reepicheep
     * Created at 2016/6/12 12:22
     */
    public static Map<String, Object> parseDetailHistoryInfo(String info){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Gson gson = new Gson();
        List<DetailHistoryModel> modelData = gson.fromJson(info, new TypeToken<List<DetailHistoryModel>>(){}.getType());
        resultMap.put(Constant.DETAIL_HISTORY_LIST, modelData);
        return resultMap;
    }

    /**
     * parse Award Detail Header data
     * @author Reepicheep
     * Created at 2016/6/12 16:48
     */
    public static Map<String, Object> parseDetailHeaderInfo(String info) throws JSONException {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        JSONArray jsonArray = new JSONArray(info);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        String bannerStr = jsonObject.getString(Constant.GOOD_IMG);
        Gson gson = new Gson();
        List<BannerModel> modelData = gson.fromJson(bannerStr, new TypeToken<List<BannerModel>>(){}.getType());
        resultMap.put(Constant.BANNER_LIST, modelData);

        resultMap.put(Constant.IDX, jsonObject.getLong(Constant.IDX));
        resultMap.put(Constant.TIME_ID, jsonObject.getLong(Constant.TIME_ID));
        resultMap.put(Constant.GOOD_ID, jsonObject.getLong(Constant.GOOD_ID));
        resultMap.put(Constant.SALED, jsonObject.getLong(Constant.SALED));
        resultMap.put(Constant.TOTAL, jsonObject.getLong(Constant.TOTAL));
        resultMap.put(Constant.TITLE, jsonObject.getString(Constant.TITLE));
        resultMap.put(Constant.SUBTITLE, jsonObject.getString(Constant.SUBTITLE));
        resultMap.put(Constant.HEADPIC, jsonObject.getString(Constant.HEADPIC));
        resultMap.put(Constant.CHECK_IN_DATE, jsonObject.getString(Constant.CHECK_IN_DATE));
        resultMap.put(Constant.PERSIZE, jsonObject.getString(Constant.PERSIZE));
        return resultMap;
    }


    /**
     * parse Detail Unveil Info
     * @author Reepicheep
     * Created at 2016/6/28 14:58
     */
    public static Map<String, Object> parseDetailUnveilInfo(String info) {
        Map<String, Object> resultMap = new HashMap<>();
        final int ALL = 0;
        final int ING = 1;
        final int END = 2;
        try {
            JSONArray jsonArray = new JSONArray(info);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String statusStr = jsonObject.getString(Constant.STATUS);
            int status = statusStr.equals("ALL")? ALL : (statusStr.equals("ING")? ING : END);
            Gson gson = new Gson();
            switch(status){
                case ALL:
                    List<DetailWaitModel> modelData = gson.fromJson(info, new TypeToken<List<DetailWaitModel>>(){}.getType());
                    resultMap.put(Constant.DETAIL_UNVEIL_SOON, modelData);
                    resultMap.put(Constant.STATUS, ALL);
                    break;
                case ING:
                    List<DetailUnveilModel> modelData_ing = gson.fromJson(info, new TypeToken<List<DetailUnveilModel>>(){}.getType());
                    resultMap.put(Constant.DETAIL_UNVEIL_ING, modelData_ing);
                    resultMap.put(Constant.STATUS, ING);
                    break;
                case END:
                    List<DetailUnveilModel> modelData_end = gson.fromJson(info, new TypeToken<List<DetailUnveilModel>>(){}.getType());
                    resultMap.put(Constant.DETAIL_UNVEIL_END, modelData_end);
                    resultMap.put(Constant.STATUS, END);
                    break;
            }

        }catch (JSONException e){

        }
        return resultMap;
    }

    
    /**
     * parse friends info
     * @author Reepicheep
     * Created at 2016/6/28 15:01
     */
    public static Map<String, Object> parseFriendsInfo(String info) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Gson gson = new GsonBuilder().serializeNulls().create();
        List<FriendsModel> modelData = gson.fromJson(info, new TypeToken<List<FriendsModel>>(){}.getType());
        resultMap.put(Constant.FRIENDS_LIST, modelData);
        return resultMap;
    }

    /**
     * parse discover info
     * @author Reepicheep
     * Created at 2016/7/19 19:09
     */
    public static Map<String, Object> parseDiscoverInfo(String info) {
        Map<String, Object> resultMap = new HashMap<>();
        Gson gson = new GsonBuilder().serializeNulls().create();
        List<DiscoverModel> modelData = gson.fromJson(info, new TypeToken<List<DiscoverModel>>(){}.getType());
        resultMap.put(Constant.DISCOVER_LIST, modelData);
        return resultMap;
    }

    /**
     * Parse Snatch Award Info
     * @author Reepicheep
     * Created at 2016/6/28 17:02
     */
    public static Map<String, Object> parseSnatchAwardInfo(String awardInfo){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Gson gson = new GsonBuilder().serializeNulls().create();
        List<SnatchAwardModel> modelData = gson.fromJson(awardInfo, new TypeToken<List<SnatchAwardModel>>(){}.getType());
        resultMap.put(Constant.AWARD_LIST, modelData);
        return resultMap;
    }

    public static Map<String, Object> parseSettlementInfo(String info){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            JSONObject jsonObject = new JSONObject(info);
            resultMap.put("ordernumber", jsonObject.getString("ordernumber"));
            resultMap.put("amount", jsonObject.getLong("amount"));
            resultMap.put("money", jsonObject.getLong("money"));
            resultMap.put("coin", jsonObject.getLong("coin"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    public static Map<String, Object> parsePayResultdInfo(String awardInfo){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Gson gson = new GsonBuilder().serializeNulls().create();
        PayResultModel modelData = gson.fromJson(awardInfo, PayResultModel.class);
        resultMap.put(Constant.AWARD_LIST, modelData);
        return resultMap;
    }

    public static Map<String, Object> parseWinRecordInfo(String awardInfo){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Gson gson = new GsonBuilder().serializeNulls().create();
        List<WinRecordModel> modelData = gson.fromJson(awardInfo, new TypeToken<List<WinRecordModel>>(){}.getType());
        resultMap.put(Constant.AWARD_LIST, modelData);
        return resultMap;
    }

    public static Map<String, Object> parseBaskInfo(String info){
        Map<String, Object> resultMap = new HashMap<>();
        Gson gson = new GsonBuilder().serializeNulls().create();
        List<BaskSNSModel> model = gson.fromJson(info, new TypeToken<List<BaskSNSModel>>(){}.getType());
        for (int i = 0; i < model.size(); i++) {
            long sidx = model.get(i).getSidx();
            if(sidx != 0) {
                String img = model.get(i).getImg();
                List<DiscoverModel.BaskImage> list = new ArrayList<>();
                DiscoverModel.BaskImage baskImage = new DiscoverModel.BaskImage();
                baskImage.setImg(img);
                list.add(baskImage);
                model.get(i).setImgUrl(list);
                if(i == 0) continue;
                long pre = model.get(i - 1).getPos();
                long cur = model.get(i).getPos();
                if (pre == cur) {
                    List<DiscoverModel.BaskImage> origin = model.get(i-1).getImgUrl();
                    origin.addAll(list);
                    model.remove(i);
                    i--;
                }
            }
        }
        resultMap.put(Constant.AWARD_LIST, model);
        return resultMap;
    }

    public static Map<String, Object> parseAddressInfo(String info){
        Map<String, Object> resultMap = new HashMap<>();
        Gson gson = new GsonBuilder().serializeNulls().create();
        List<AddressModel> modelData = gson.fromJson(info, new TypeToken<List<AddressModel>>(){}.getType());
        resultMap.put(Constant.AWARD_LIST, modelData);
        return resultMap;
    }

    public static Map<String, Object> parseVerifyCodeInfo(String info){
        Map<String, Object> resultMap = new HashMap<>();
        Gson gson = new GsonBuilder().serializeNulls().create();
        VerifyCodeModel modelData = gson.fromJson(info, new TypeToken<VerifyCodeModel>(){}.getType());
        resultMap.put(Constant.AWARD_LIST, modelData);
        return resultMap;
    }

    public static Map<String, Object> parsePayMethodInfo(String info){
        Map<String, Object> resultMap = new HashMap<>();
        Gson gson = new GsonBuilder().serializeNulls().create();
        List<PayMethodModel> modelData = gson.fromJson(info, new TypeToken<List<PayMethodModel>>(){}.getType());
        resultMap.put(Constant.AWARD_LIST, modelData);
        return resultMap;
    }

    public static Map<String, Object> parseNotificationInfo(String info){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<InformationModel> modelData = gson.fromJson(info, new TypeToken<List<InformationModel>>(){}.getType());
        resultMap.put(Constant.AWARD_LIST, modelData);
        return resultMap;
    }

    public static Map<String, Object> parseDiamondMissionInfo(String info){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<DiamondMissionModel> modelData = gson.fromJson(info, new TypeToken<List<DiamondMissionModel>>(){}.getType());
        resultMap.put(Constant.AWARD_LIST, modelData);
        return resultMap;
    }
}
