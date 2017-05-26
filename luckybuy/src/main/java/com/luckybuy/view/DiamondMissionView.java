package com.luckybuy.view;

import com.luckybuy.model.DiamondMissionModel;

import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/10/14.
 */

public interface DiamondMissionView {
    void loginOut(boolean flag);
    void showIntroduction(String comment);
    void updateInfo();
    void updateDiamond(String diamond);
    void updateMission(List<DiamondMissionModel> showlist);
}
