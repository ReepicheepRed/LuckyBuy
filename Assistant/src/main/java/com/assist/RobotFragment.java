package com.assist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.assist.contract.RobotContract;
import com.assist.presenter.RobotPresenterImpl;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhiPeng.S on 2016/11/2.
 */
@ContentView(R.layout.fragment_robot)
public class RobotFragment extends BaseFragment implements RobotContract.View{
    private RobotContract.Presenter robotPresenter;

    @ViewInject(R.id.robot_userNum_tv)
    private TextView userNum_tv;
    @ViewInject(R.id.robot_goodsNum_tv)
    private TextView goodsNum_tv;

    @ViewInject(R.id.robot_initiate_btn)
    private Button initiate_btn;
    @ViewInject(R.id.robot_send_btn)
    private Button send_btn;

    @ViewInject(R.id.robot_log_et)
    private TextView log_et;

    public static RobotFragment newInstance(Bundle bundle) {
        RobotFragment fragment = new RobotFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }
    
    @Event({
            R.id.robot_initiate_btn,
            R.id.robot_send_btn
    })
    private void viewClick(View view){
        int interval = ((MainActivity)getActivity()).getInterval();
        switch (view.getId()){
            case R.id.robot_initiate_btn:
                if(initiate_btn.getText().toString().equals(getString(R.string.initiate)))
                    robotPresenter.launchRobot_interval(interval);
                else
                    robotPresenter.cancelRobot();
                break;
            case R.id.robot_send_btn:
                robotPresenter.sendLog();
                break;
        }
    }

    private void init(){
        robotPresenter = new RobotPresenterImpl(getActivity(),this);
        log_et.setSingleLine(false);
        log_et.setHorizontallyScrolling(false);
        log_et.setText("");
        userNum_tv.setText(getString(R.string.user_number,0));
        goodsNum_tv.setText(getString(R.string.commodity_number,0));
        robotPresenter.getCount();
    }

    public  void showCount(String uStr, String gStr){
        userNum_tv.setText(uStr);
        goodsNum_tv.setText(gStr);
    }

    public void showLog(CharSequence charSequence){
        StringBuilder builder = new StringBuilder(charSequence);
        builder.append("\n -------------------------");
        builder.append("\n" + log_et.getText());
        log_et.setText(builder);
    }

    @Override
    public String getLog() {
        return log_et.getText().toString();
    }

    public void setLaunchState(CharSequence charSequence){
        initiate_btn.setText(charSequence);
    }
}
