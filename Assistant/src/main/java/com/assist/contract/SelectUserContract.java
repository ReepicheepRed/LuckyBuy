package com.assist.contract;

import com.assist.model.SelectUserModelImpl;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/3.
 */

public class SelectUserContract {
    
public interface View{
    void showUserInfo(String[] userStrA);
    void showUserDetailInfo(String[] smaStrA, List<SelectUserModelImpl.DetailInfo> datas);
}

public interface Presenter{
    void getUserInfo();
    void getAvailableUserInfo();
    long getSelectedUserCount();
    List<SelectUserModelImpl.DetailInfo> getDatas();
}

public interface Model{
}

}