package com.luckybuy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/6/14.
 */
@ContentView(R.layout.activity_general_no_refresh)
public class AddressSelectActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title_tv.setText(R.string.select_address);
        init();
    }

    @ViewInject(R.id.title_activity)
    private TextView title_tv;


    @Event({R.id.back_iv})
    private void viewClick(View view){
        switch(view.getId()){
            case R.id.back_iv:
                this.finish();
                break;
        }
    }

    int level;
    private void init(){
        try {
            init_Address_Info();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<String> datas;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        level = bundle.getInt("level",-1);
        switch (level){
            //obtain province
            case 0:
                datas = obtain_address_province();
                showAddressInfo(datas);
                break;
            //obtain street
            case 1:
                String province_en = bundle.getString("en");
                String province_th = bundle.getString("th");
                datas = obtain_address_street(province_en,province_th);
                showAddressInfo(datas);
                break;
        }
    }

    @ViewInject(R.id.listView)
    private ListView listView;
    private ArrayAdapter<String> adapter;

    private void showAddressInfo(List<String> data){
        adapter = new ArrayAdapter<>(this,R.layout.item_address_select,R.id.address_name_tv,data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }


    private LinkedHashMap<String, Object> linkedHashMap_en;
    private JSONObject jsonObject_en;
    private JSONArray jsonArray_th;
    private void init_Address_Info() throws JSONException {
        final String address_en = "thailand_district_en.txt";
        final String address_th = "thailand_district_th.txt";
        String addressStr_en = Utility.getFileContent(this,address_en);
        linkedHashMap_en = JSON.parseObject(addressStr_en, new TypeReference<LinkedHashMap<String, Object>>(){});
        jsonObject_en = new JSONObject(addressStr_en);

        String addressStr_th = Utility.getFileContent(this,address_th);
        jsonArray_th = new JSONArray(addressStr_th);
    }


    private List<String> obtain_address_province(){
        Iterator<String> it_en = linkedHashMap_en.keySet().iterator();
        List<String> list_en = new ArrayList<>();
        while (it_en.hasNext()){
            list_en.add(it_en.next());
        }

        List<String> list_th = new ArrayList<>();
        for (int i = 0; i < jsonArray_th.length(); i++) {
            try {
                JSONObject jsonObject = (JSONObject) jsonArray_th.get(i);
                list_th.add(jsonObject.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        List<String> list_province = new ArrayList<>();
        for (int i = 0; i < list_en.size(); i++) {
            for (int j = 0; j < list_th.size(); j++) {
                if(i == j){
                    String province = list_th.get(j) + "/" + list_en.get(i);
                    list_province.add(province);
                }
            }
        }

        return list_province;
    }

    private List<String> obtain_address_street(String key_en,String key_th){
        List<String> list_street = new ArrayList<>();
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            String street_en = jsonObject_en.getString(key_en);
            String[] list_en = gson.fromJson(street_en, new TypeToken<String[]>(){}.getType());


            String street_th = "";
            for (int i = 0; i < jsonArray_th.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray_th.get(i);
                String province = jsonObject.getString("name");
                if(province.equals(key_th)){
                    street_th = jsonObject.getString("district");
                }
            }
            String[] list_th = gson.fromJson(street_th, new TypeToken<String[]>(){}.getType());

            for (int i = 0; i < list_en.length; i++) {
                for (int j = 0; j < list_th.length; j++) {
                    if(i == j){
                        String street = list_th[i] + "/" + list_en[i];
                        list_street.add(street);
                    }
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return list_street;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra("level",level);
        intent.putExtra("address",adapter.getItem(position));
        setResult(Constant.RESULT_CODE,intent);
        this.finish();
    }
}
