package com.luckybuy.presenter;

import com.luckybuy.model.DiamondMissionModel;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/10/14.
 */

public interface DiamondMissionPresenter extends BasePresenter{

    void getShowListInfo();
    void getDiamondNumber();
    void missionGo();
    void missionWill(List<DiamondMissionModel> datas, int position);
    void commitShareRsult();
}
