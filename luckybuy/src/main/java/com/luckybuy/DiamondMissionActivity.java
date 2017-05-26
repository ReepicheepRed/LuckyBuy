package com.luckybuy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.adjust.sdk.Util;
import com.facebook.CallbackManager;
import com.facebook.share.widget.ShareDialog;
import com.luckybuy.adapter.DiamondMissionAdapter;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.DiamondMissionModel;
import com.luckybuy.model.DiamondMissionModel;
import com.luckybuy.model.UserModel;
import com.luckybuy.presenter.DiamondMissionPresenter;
import com.luckybuy.presenter.Impl.DiamondMissionPresenterImpl;
import com.luckybuy.presenter.Impl.InformationPresenterImpl;
import com.luckybuy.share.FaceBookShare;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;
import com.luckybuy.view.DiamondMissionView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.luckybuy.presenter.Impl.DiamondMissionPresenterImpl.REQUESTCODE_MISSION;

/**
 * Created by zhiPeng.S on 2016/10/14.
 */
@ContentView(R.layout.activity_diamond_mission)
public class DiamondMissionActivity extends BaseActivity implements DiamondMissionView,AdapterView.OnItemClickListener,DialogInterface.OnClickListener{

    private DiamondMissionPresenter diamondMissionPresenter;

    @ViewInject(R.id.title_activity)
    private TextView title;
    @ViewInject(R.id.mission_diamond_num_tv)
    private TextView diamond_num_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        init_Share();
        updateInfo();
    }

    @Event({R.id.back_iv})
    private void viewClick(View view){
        switch (view.getId()){
            case R.id.back_iv:
                finish();
                break;
        }
    }

    private void initView(){
        diamondMissionPresenter = new DiamondMissionPresenterImpl(this,this);
        title.setText(R.string.title_diamond_mission);
        gridView.setAdapter(getShowListAdapter());
        gridView.setOnItemClickListener(this);
        dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.mission_introduction)
                .setPositiveButton(R.string.determine, this);
    }

    @Override
    public void updateInfo(){
            diamondMissionPresenter.getDiamondNumber();
            diamondMissionPresenter.getShowListInfo();
    }

    private void init_Share(){
        FaceBookShare.callbackManager = CallbackManager.Factory.create();
        FaceBookShare.shareDialog = new ShareDialog(this);
        FaceBookShare.shareDialog.registerCallback(FaceBookShare.callbackManager, FaceBookShare.facebookCallback_share);
        FaceBookShare.setDiamondMissionPresenter(diamondMissionPresenter);
    }

    @ViewInject(R.id.mission_gv)
    private GridView gridView;
    private List<DiamondMissionModel> datas;
    private ListBaseAdapter<DiamondMissionModel> adapter;

    private ListBaseAdapter<DiamondMissionModel> getShowListAdapter(){
        datas = new ArrayList<>();
        adapter = new DiamondMissionAdapter(this,datas);
        return adapter;
    }

    @Override
    public void loginOut(boolean flag) {
        Utility.toastShow(x.app(),R.string.login_again);
        LoginUserUtils.LoginOut();
        setResult(4);
        DiamondMissionActivity.this.finish();
    }



    private AlertDialog.Builder dialog;
    public void showIntroduction(String comment){
        dialog.setMessage(comment);
        dialog.show();
    }

    @Override
    public void updateDiamond(String diamond) {
        diamond_num_tv.setText(diamond);
    }

    @Override
    public void updateMission(List<DiamondMissionModel> showlist) {
        datas.clear();
        int size = showlist == null ? 0 : showlist.size();
        for (int i = 0; i < size; i++) datas.add(showlist.get(i));

        adapter.setData(datas);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        diamondMissionPresenter.missionWill(datas,position);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        diamondMissionPresenter.missionGo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUESTCODE_MISSION:
                updateInfo();
                break;
        }
        FaceBookShare.callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
