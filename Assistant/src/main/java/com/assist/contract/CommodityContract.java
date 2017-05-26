package com.assist.contract;

import com.assist.model.CommodityModelImpl;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/3.
 */

public class CommodityContract {
    
public interface View{
    void showCommodityList(List<CommodityModelImpl> datas);
}

public interface Presenter{
    void getShowList();
    long getSelectedGoodsCount();
    List<CommodityModelImpl> getDatas();
}

public interface Model{
}


}