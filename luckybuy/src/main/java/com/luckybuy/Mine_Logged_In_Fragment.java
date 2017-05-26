package com.luckybuy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushManager;
import com.luckybuy.login.LoginActivity;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.UserModel;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;
import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/7/28.
 */
@ContentView(R.layout.mine_login)
public class Mine_Logged_In_Fragment extends BaseFragment{

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public static Mine_Logged_In_Fragment newInstance(int sectionNumber) {
        Mine_Logged_In_Fragment fragment = new Mine_Logged_In_Fragment();
        Bundle args = new Bundle();
        args.putInt("section_number", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = LoginUserUtils.getUserSharedPreferences(getActivity());
        editor = preferences.edit();
        long user_id = preferences.getLong(Constant.USER_ID,0);
        if(user_id != 0)
            obtain_user_information(user_id+"");
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            long user_id = preferences.getLong(Constant.USER_ID,0);
            if(user_id != 0)
                obtain_user_information(user_id+"");
        }
    }



    @Event({
            R.id.mine_avatar_iv,
            R.id.setting_btn,
            //R.id.mine_header_rl,
            R.id.buy_record_rl,
            R.id.win_prize_record_rl,
            R.id.bask_record_rl,
            R.id.obtain_diamond_rl,
            R.id.charge_record_rl,
            R.id.service_center_rl,
            R.id.recharge_btn,
            R.id.mine_address_rl,
            R.id.mine_info_iv,
//            R.id.mine_diamond_tv
    })
    private void viewClick(View view){
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.setting_btn:
                intent.setClass(getActivity(),SettingActivity.class);
                startActivityForResult(intent,Constant.REQUEST_CODE);
                break;
            case R.id.mine_avatar_iv:
                intent.setClass(getActivity(),SNS_MineActivity.class);
                startActivityForResult(intent,Constant.REQUEST_CODE);
                break;
            case R.id.mine_header_rl:
                intent.setClass(getActivity(),LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.recharge_btn:
                intent.setClass(getActivity(),ChargeActivity.class);
                startActivityForResult(intent,Constant.REQUEST_CODE);
                break;
            case R.id.buy_record_rl:
                intent.setClass(getActivity(),BuyRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.win_prize_record_rl:
                intent.setClass(getActivity(),WinRecordActivity.class);
                startActivityForResult(intent,Constant.REQUEST_CODE);
                break;
            case R.id.bask_record_rl:
                intent.setClass(getActivity(),BaskMineActivity.class);
                startActivity(intent);
                break;
            case R.id.obtain_diamond_rl:
                intent.setClass(getActivity(),WebActivity.class);
                intent.putExtra(Constant.WEB_H5,Constant.DIAMOND);
                startActivity(intent);
                break;
            case R.id.charge_record_rl:
                intent.setClass(getActivity(),ChargeRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.service_center_rl:
                intent.setClass(getActivity(),CallCenterActivity.class);
                startActivity(intent);
                break;
            case R.id.mine_address_rl:
                intent.setClass(getActivity(),ManagerAddressActivity.class);
                startActivity(intent);
                break;
            case R.id.mine_info_iv:
                intent.setClass(getActivity(),InformationActivity.class);
                startActivity(intent);
                break;
            case R.id.mine_diamond_tv:
                intent.setClass(getActivity(),DiamondMissionActivity.class);
                startActivity(intent);
                break;
        }

    }

    @ViewInject(R.id.mine_avatar_iv)
    private ImageView avatar_iv;

    @ViewInject(R.id.mine_name_tv)
    private TextView nickname_tv;

    @ViewInject(R.id.mine_diamond_tv)
    private TextView diamond_tv;

    @ViewInject(R.id.mine_balance_tv)
    private TextView balance_tv;

    private void obtain_user_information(String uidx){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/ucenter/MemberInfo.ashx");
        params.addQueryStringParameter("uidx", uidx);
        TokenVerify.addToken(getActivity(),params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    //account conflict
                    if(result.equals("")){
                        editor.clear();
                        editor.commit();
                        PushManager.getInstance().initialize(x.app());
                        LuckyBuy_Mine mine_fragment = (LuckyBuy_Mine) getParentFragment();
                        mine_fragment.updateFragment();
                        Utility.toastShow(x.app(),R.string.login_again);
                        return;
                    }

                    TokenVerify.saveCookie(getActivity());

                    ImageOptions imageOptions = new ImageOptions.Builder()
                            .setSize(DensityUtil.dip2px(50),DensityUtil.dip2px(50))
                            .setRadius(DensityUtil.dip2px(30))
                            .setLoadingDrawableId(R.mipmap.gerentouxiang_moren)
                            .setFailureDrawableId(R.mipmap.gerentouxiang_moren)
                            .build();

                    Gson gson = new GsonBuilder().serializeNulls().create();
                    List<UserModel> modelData = gson.fromJson(result, new TypeToken<List<UserModel>>(){}.getType());
                    UserModel model = modelData.get(0);

                    String facebook_id_str = model.getFbuserid().equals("") ? "0" : model.getFbuserid();
                    long facebook_id = Long.valueOf(facebook_id_str);
                    editor.putLong(Constant.USER_ID_FB,facebook_id);
                    editor.commit();

                    x.image().bind(avatar_iv,model.getHeadpic(),imageOptions);
                    nickname_tv.setText(model.getNickname());
                    String diamondStr = getString(R.string.diamond_count);
                    diamondStr = String.format(diamondStr,model.getLuckcoin());
                    diamond_tv.setText(diamondStr);
                    String moneyStr = model.getMoney()+"";
                    balance_tv.setText(moneyStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Utility.toastShow(x.app(),"mine_logged");
        switch (resultCode){
            case Constant.RESULT_CODE_UPDATE:
                LuckyBuy_Mine mine_fragment = (LuckyBuy_Mine) getParentFragment();
                mine_fragment.updateFragment();
                break;
            case Constant.RESULT_CODE_MINE:
                long user_id = preferences.getLong(Constant.USER_ID,0);
                if(user_id != 0)
                    obtain_user_information(user_id+"");
                break;
        }
    }
}
