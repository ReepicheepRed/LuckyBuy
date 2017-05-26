package com.assist.contract;

import android.view.View;
import android.widget.AdapterView;

import com.assist.model.BaskMaterialModelImpl;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/8.
 */

public class BaskMaterialContract {
    
public interface View{
    void showBaskMaterialList(List<Long> data);
}

public interface Presenter{
    void getBaskMaterialInfo();
    void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id);
}

public interface Model{
}


}