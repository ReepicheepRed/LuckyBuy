package com.assist.contract;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by zhiPeng.S on 2016/11/2.
 */

public class RobotSettingContract {
    
public interface View{
    void showUserSelectedCount(String str1);
    void showGoodsSelectedCount(String str1);
}

public interface Presenter{
    void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id);
    void onActivityResult(int requestCode, int resultCode, Intent data);
}

public interface Model{
}


}