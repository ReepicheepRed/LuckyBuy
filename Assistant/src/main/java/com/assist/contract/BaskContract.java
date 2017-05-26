package com.assist.contract;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.assist.BaskFrgment;
import com.assist.adapter.ListBaseAdapter;
import com.assist.model.BaskModelImpl;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/7.
 */

public class BaskContract {
    
public interface View{
    void showBaskList(List<BaskModelImpl> data,int optype);
    List<BaskModelImpl> getData();
    ListBaseAdapter<BaskModelImpl> getAdapter();
}

public interface Presenter{
    void setFragment(BaskFrgment fragment);
    void getWillBaskListInfo(int optype);
    void onActivityResult(int requestCode, int resultCode, Intent data);
    void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id);
}

public interface Model{
}


}