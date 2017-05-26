package com.luckybuy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luckybuy.adapter.AwardAdapter;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.layout.HorizontalListView;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.AwardModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.json.JSONArray;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/6/13.
 */

@ContentView(R.layout.activity_search)
public class SearchActivity extends BaseActivity implements View.OnFocusChangeListener,TextView.OnEditorActionListener,AdapterView.OnItemClickListener{

    private final int KEY_WORD = 0;
    private final int LIST_SEARCH = 1;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = LoginUserUtils.getAppSharedPreferences(this,Constant.PREFERENCES_RECORD);
        editor = preferences.edit();
        setShowList();
        initSearchView();
        initGridView();
        obtain_hot_word();
        obtain_history();
    }

    private void initSearchView(){
        search_et.addTextChangedListener(textWatcher);
        search_et.setOnFocusChangeListener(this);
        search_et.setOnEditorActionListener(this);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int state = s.length() <= 0 ? 0 : 1;
            switch (state){
                case 0:
                    remove_iv.setVisibility(View.GONE);
                    showSearchResult(false);
                    obtain_history();
                    break;
                case 1:
                    remove_iv.setVisibility(View.VISIBLE);
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            search_et.setSelection(search_et.getText().length());
        }
    };

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) msg.obj;
            switch (msg.what){
                case R.id.HOT_WORD:
                    break;
                case R.id.SEARCH:
                    showSearchResult(true);
                    if (!result.isEmpty()){
                        @SuppressWarnings("unchecked")
                        List<AwardModel> showlist =
                                (List<AwardModel>) result.get(Constant.AWARD_LIST);
                        boolean orientation = (boolean)result.get("down_or_up");
                        if (!orientation)   datas.clear();

                        int size = showlist == null? 0 : showlist.size();
                        for (int i = 0; i < size; i++) datas.add(showlist.get(i));

                        adapter.setData(datas);
                        adapter.notifyDataSetChanged();
                        if(datas.isEmpty()) Utility.toastShow(x.app(),"No search results");
                    }
                    break;
            }
        }
    };



    @ViewInject(R.id.search_et)
    private EditText search_et;

    @ViewInject(R.id.search_remove_iv)
    private ImageView remove_iv;

    @ViewInject(R.id.search_cancel_btn)
    private Button cancel_btn;

    @Event({R.id.search_remove_iv,R.id.search_cancel_btn,R.id.search_clear_history_iv})
    private void viewClick(View view){
        switch(view.getId()){
            case R.id.search_remove_iv:
                search_et.setText("");
                break;
            case R.id.search_cancel_btn:
                this.finish();
                break;
            case R.id.search_clear_history_iv:
                editor.clear();
                editor.commit();
                obtain_history();
                break;
        }
    }

    @ViewInject(R.id.search_lv)
    private ListView listView;
    private ListBaseAdapter<AwardModel> adapter;
    private List<AwardModel> datas;
    private void setShowList(){
        listView.setAdapter(getShowListAdapter());
        listView.setOnItemClickListener(this);
    }

    private ListBaseAdapter<AwardModel> getShowListAdapter(){
        datas = new ArrayList<>();
        adapter = new AwardAdapter(this, datas);
        ((AwardAdapter)adapter).setSearch(true);
        return adapter;
    }

    @ViewInject(R.id.search_keyword_ll)
    private LinearLayout keyword_ll;

    private void showSearchResult(boolean result){
        int id = result ? LIST_SEARCH :KEY_WORD;
        switch(id){
            case LIST_SEARCH:
                keyword_ll.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                break;
            case KEY_WORD:
                keyword_ll.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                break;
        }
    }


    private void search_commodity(String query){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/search/search.ashx");
        params.addQueryStringParameter("query", query);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Map<String, Object> resultMap = ParseData.parseAwardInfo(result);
                resultMap.put("down_or_up", false);
                mHandler.obtainMessage(R.id.SEARCH, resultMap).sendToTarget();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    @ViewInject(R.id.hot_gv)
    private HorizontalListView gridView_hot;
    private String[] hotWord;

    private void initGridView(){
        gridView_hot.setOnItemClickListener(this);
        gridView.setOnItemClickListener(this);
    }
    private void obtain_hot_word(){
        RequestParams params = new RequestParams(Constant.getBaseUrl() + "page/search/SearchHot.ashx");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                hotWord = gson.fromJson(result, new TypeToken<String[]>(){}.getType());

                @SuppressWarnings("unchecked")
                ArrayAdapter adapter = new ArrayAdapter(SearchActivity.this,R.layout.item_search,hotWord);
                gridView_hot.setAdapter(adapter);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @ViewInject(R.id.search_record_rl)
    private RelativeLayout record_rl;

    @ViewInject(R.id.history_gv)
    private ListView gridView;

    private String[] recordWord;
    List<String> mData= new ArrayList<>();

    private void obtain_history(){
        for(int i =0; i < 5; i++) {
            mData.add("iphone");
            mData.add("6plus");
        }
        Gson gson = new GsonBuilder().serializeNulls().create();

        String record = preferences.getString(Constant.RECORD,"");
        recordWord = gson.fromJson(record, new TypeToken<String[]>(){}.getType());
        if(recordWord == null || recordWord.length == 0){
            record_rl.setVisibility(View.GONE);
            gridView.setVisibility(View.GONE);
            return;
        }

        record_rl.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.VISIBLE);

        @SuppressWarnings("unchecked")
        ArrayAdapter adapter = new ArrayAdapter(SearchActivity.this,android.R.layout.simple_list_item_1, recordWord);
        gridView.setAdapter(adapter);
    }

    private void record_history(String searchStr){
        JSONArray record = new JSONArray();
        record.put(searchStr);
        if(recordWord != null && recordWord.length > 0)
        for (int i = 0; i < recordWord.length; i++) {
            if(!recordWord[i].equals(searchStr))
                record.put(recordWord[i]);
        }
        editor.clear();
        editor.putString(Constant.RECORD,record.toString());
        editor.commit();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
           /*隐藏软键盘*/
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }
            String content = search_et.getText().toString();
            search_commodity(content);
            record_history(content);
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent instanceof ListView){
            if(parent == gridView){
                search_et.setText(recordWord[position]);
                search_commodity(recordWord[position]);
                return;
            }
            Intent intent = new Intent(this,DetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("bundle",datas.get(position));
            intent.putExtras(bundle);
            startActivity(intent);
        }else if( parent instanceof HorizontalListView){
            if(parent == gridView_hot){
                search_et.setText(hotWord[position]);
                search_commodity(hotWord[position]);
                record_history(hotWord[position]);
            }
        }
    }
}
