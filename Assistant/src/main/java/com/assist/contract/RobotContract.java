package com.assist.contract;

import android.os.Bundle;

/**
 * Created by zhiPeng.S on 2016/11/3.
 */

public class RobotContract {
    
public interface View{
    void showCount(String uStr, String gStr);
    void showLog(CharSequence charSequence);
    String getLog();
    void setLaunchState(CharSequence charSequence);
}

public interface Presenter{
    void launchRobot(Bundle bundle);
    void queryState(String goodId, String uidx);
    void sequenceBuy(int position);
    void getCount();
    void launchRobot_interval(int time);
    void cancelRobot();
    void sendLog();
}

public interface Model{
}


}