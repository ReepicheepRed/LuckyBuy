package com.luckybuy.model;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/6/2.
 */
public class AwardFrsModel extends AwardModel{


    private boolean isFold = true;

    private  List<FriendsModel> user;

    public List<FriendsModel> getFriends() {
        return user;
    }

    public void setFriends(List<FriendsModel> user) {
        this.user = user;
    }

    public boolean isFold() {
        return isFold;
    }

    public void setFold(boolean fold) {
        isFold = fold;
    }
}
