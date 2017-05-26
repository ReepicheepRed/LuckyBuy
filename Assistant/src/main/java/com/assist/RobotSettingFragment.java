package com.assist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import com.assist.contract.RobotSettingContract;
import com.assist.presenter.RobotSettingPresenterImpl;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 * Created by zhiPeng.S on 2016/11/2.
 */
@ContentView(R.layout.fragment_robot_setting)
public class RobotSettingFragment extends BaseFragment implements RobotSettingContract.View,AdapterView.OnItemSelectedListener,RadioGroup.OnCheckedChangeListener{

    public RobotSettingFragment() {
    }

    public static RobotSettingFragment newInstance(Bundle bundle) {
        RobotSettingFragment fragment = new RobotSettingFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private RobotSettingContract.Presenter robotSettingPresenter;

    @ViewInject(R.id.rSetting_userNum_tv)
    private TextView userNum_tv;
    @ViewInject(R.id.rSetting_commodityNum_tv)
    private TextView goodsNum_tv;

    @ViewInject(R.id.rSetting_rg)
    private RadioGroup rSetting_rg;
    @ViewInject(R.id.rSetting_random_rb)
    private RadioButton rSetting_random_rb;
    @ViewInject(R.id.rSetting_sequence_rb)
    private RadioButton rSetting_sequence_rb;

    @ViewInject(R.id.spinner)
    private Spinner spinner;
    private String[] datas;
    private ArrayAdapter<String> adapter;

    @ViewInject(R.id.spinner2)
    private TextView spinner2;
    @ViewInject(R.id.spinner3)
    private TextView spinner3;

    @Event({
            R.id.rSetting_user_Btn,
            R.id.rSetting_commodity_Btn,
            R.id.rSelect_complete_btn
    })
    private void viewClick(View view){
        Intent intent = new Intent();
        switch (view.getId()){
            case R.id.rSetting_user_Btn:
                intent.setClass(getActivity(), SelectUserActivity.class);
                startActivityForResult(intent,RobotSettingPresenterImpl.RS_RequestCode);
                break;
            case R.id.rSetting_commodity_Btn:
                intent.setClass(getActivity(), CommodityActivity.class);
                startActivityForResult(intent,RobotSettingPresenterImpl.RS_RequestCode);
                break;
            case R.id.rSelect_complete_btn:
                ((MainActivity)getActivity()).getmViewPager().setCurrentItem(0);
                break;
        }
    }

    private void init(){
        robotSettingPresenter = new RobotSettingPresenterImpl(getActivity(),this);
        userNum_tv.setText(getActivity().getString(R.string.user_number_selected,0));
        goodsNum_tv.setText(getActivity().getString(R.string.commodity_number_selected,0));
        rSetting_rg.setOnCheckedChangeListener(this);
//        rSetting_sequence_rb.setOnCheckedChangeListener(this);
        rSetting_sequence_rb.setChecked(true);

        datas = new String[]{"10sec","30sec","1min","5min","10min"};
        adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,datas);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        spinner2.setText("5:00");
        spinner3.setText("23:00");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        robotSettingPresenter.onActivityResult(requestCode,resultCode,data);
    }


    @Override
    public void showUserSelectedCount(String str1) {
        userNum_tv.setText(str1);
    }

    @Override
    public void showGoodsSelectedCount(String str1) {
        goodsNum_tv.setText(str1);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        robotSettingPresenter.onItemSelected(parent, view, position, id);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton radioButton = (RadioButton) getActivity().findViewById(checkedId);
        if(radioButton.equals(rSetting_random_rb))
            ((MainActivity)getActivity()).setRandom(true);
        else
            ((MainActivity)getActivity()).setRandom(false);
    }
}
