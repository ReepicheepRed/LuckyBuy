package com.luckybuy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luckybuy.adapter.FriendsSNSPagerAdapter;
import com.luckybuy.model.FriendsModel;
import com.luckybuy.model.UserModel;
import com.luckybuy.util.Constant;
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
 * Created by zhiPeng.S on 2016/6/13.
 */

@ContentView(R.layout.activity_friends_sns)
public class SNS_FriendsActivity extends BaseActivity{

    private FriendsSNSPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @ViewInject(R.id.container_friends_sns)
    private ViewPager mViewPager;

    @ViewInject(R.id.tabs_friends_sns)
    private TabLayout mTabLayout;

    @ViewInject(R.id.title_activity)
    private TextView title_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title_tv.setText(R.string.title_sns);
        initInfo();
        mSectionsPagerAdapter = new FriendsSNSPagerAdapter(this,getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);


    }

    @Event(R.id.back_iv)
    private void viewClick(View view){
        switch (view.getId()){
            case R.id.back_iv:
                this.finish();
                break;

        }
    }

    private long user_id_friends;

    public long getUser_id_friends() {
        return user_id_friends;
    }

    private void initInfo(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        FriendsModel model = (FriendsModel) bundle.getSerializable("bundle");
        if(model == null) return;
        user_id_friends = model.getUidx();
        obtain_user_information(user_id_friends+"");
    }

    @ViewInject(R.id.sns_friends_avatar_iv)
    private ImageView avatar_iv;

    @ViewInject(R.id.sns_friends_id)
    private TextView id_tv;

    @ViewInject(R.id.sns_friends_nickname)
    private TextView nickame_tv;

    private void obtain_user_information(String uidx){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "Page/Ucenter/FriendMemberInfo.ashx");
        params.addQueryStringParameter("uidx", uidx);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    List<UserModel> modelData = gson.fromJson(result, new TypeToken<List<UserModel>>(){}.getType());
                    UserModel model = modelData.get(0);
                    ImageOptions imageOptions = new ImageOptions.Builder()
                            .setSize(DensityUtil.dip2px(50),DensityUtil.dip2px(50))
                            .setRadius(DensityUtil.dip2px(25))
                            .setLoadingDrawableId(R.mipmap.gerentouxiang_moren)
                            .setFailureDrawableId(R.mipmap.gerentouxiang_moren)
                            .build();
                    x.image().bind(avatar_iv,model.getHeadpic(),imageOptions);
                    String idStr = SNS_FriendsActivity.this.getString(R.string.friends_id);
                    idStr = String.format(idStr,model.getUidx());
                    id_tv.setText(idStr);
                    nickame_tv.setText(model.getNickname());
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
}
