package com.luckybuy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.luckybuy.adapter.CartListAdapter;
import com.luckybuy.adapter.ListBaseAdapter;
import com.luckybuy.adapter.RecommendAwardAdapter;
import com.luckybuy.db.DB_Config;
import com.luckybuy.layout.BadgeView;
import com.luckybuy.layout.HorizontalListView;
import com.luckybuy.login.LoginActivity;
import com.luckybuy.login.LoginUserUtils;
import com.luckybuy.model.AwardModel;
import com.luckybuy.network.ParseData;
import com.luckybuy.network.TokenVerify;
import com.luckybuy.util.Constant;
import com.luckybuy.util.Utility;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.HashBag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhiPeng.S on 2016/5/16.
 */
@ContentView(R.layout.fragment_cart)
public class LuckyBuy_Cart extends LazyFragment implements CompoundButton.OnCheckedChangeListener, AdapterView.OnItemClickListener {


    private void updateBottomAndTop(boolean isCartEdit){
        int isEdit = isCartEdit? 0:1;
        switch (isEdit){
            case 0:
                awardAdapter.setDelete(true);
                awardAdapter.notifyDataSetChanged();
                editBtn.setText(R.string.finish);
                cart_delete_rl.setVisibility(View.VISIBLE);
                cart_bottom.setVisibility(View.GONE);
                break;
            case 1:
                awardAdapter.setDelete(false);
                awardAdapter.notifyDataSetChanged();
                editBtn.setText(R.string.edit);
                cart_delete_rl.setVisibility(View.GONE);
                cart_bottom.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Event(value = {R.id.cart_edit_btn, R.id.delete_btn,R.id.settlement_btn,R.id.cart_explore_btn,R.id.cart_all_select_ll})
    private void onCartClick(View view){
        switch (view.getId()){
            case R.id.cart_edit_btn:
                if (editBtn.getText().toString().equals(getResources().getString(R.string.edit))){
                    updateBottomAndTop(true);
                }else{
                    updateBottomAndTop(false);
                }
                break;
            case R.id.delete_btn:
                updateCartAndDb();
                updateBottomAndTop(false);
                updateBadgeView();
                boolean isStock = getAwardDbInfo();
                show_cart(isStock);
                break;
            case R.id.settlement_btn:
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                long user_id = preferences.getLong(Constant.USER_ID,0);
                if (user_id == 0){
                    startActivityForResult(intent,Constant.REQUEST_CODE);
                    return;
                }
                settlement();
                break;
            case R.id.cart_explore_btn:
                ((MainActivity)getActivity()).getViewPager().setCurrentItem(0);
                break;
            case R.id.cart_all_select_ll:
                boolean isChecked = cart_all_cb.isChecked();
                cart_all_cb.setChecked(!isChecked);
                break;
        }
    }

    @ViewInject(R.id.cart_edit_btn)
    private Button editBtn;

    @ViewInject(R.id.cart_all_select)
    private CheckBox cart_all_cb;

    @ViewInject(R.id.cart_delete_rl)
    private RelativeLayout cart_delete_rl;

    @ViewInject(R.id.cart_bottom)
    private RelativeLayout cart_bottom;

    @ViewInject(R.id.cart_amount_tv)
    private TextView cart_amount_tv;

    public Button getEditBtn() {
        return editBtn;
    }

    public void setEditBtn(Button editBtn) {
        this.editBtn = editBtn;
    }

    public CheckBox getCart_all_cb() {
        return cart_all_cb;
    }

    public void setCart_all_cb(CheckBox cart_all_cb) {
        this.cart_all_cb = cart_all_cb;
    }

    public RelativeLayout getCart_delete_rl() {
        return cart_delete_rl;
    }

    public void setCart_delete_rl(RelativeLayout cart_delete_rl) {
        this.cart_delete_rl = cart_delete_rl;
    }

    public RelativeLayout getCart_bottom() {
        return cart_bottom;
    }

    public void setCart_bottom(RelativeLayout cart_bottom) {
        this.cart_bottom = cart_bottom;
    }

    public TextView getCart_amount_tv() {
        return cart_amount_tv;
    }

    public void setCart_amount_tv(TextView cart_amount_tv) {
        this.cart_amount_tv = cart_amount_tv;
    }

    public static LuckyBuy_Cart newInstance(int sectionNumber) {
        LuckyBuy_Cart fragment = new LuckyBuy_Cart();
        Bundle args = new Bundle();
        args.putInt("section_number", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = LoginUserUtils.getUserSharedPreferences(getActivity());
        db = x.getDb(DB_Config.getDaoConfig());
        setAwardList();
        setRecAwardList();

        isPrepared = true;
        lazyLoad();
    }

    private boolean isPrepared;

    @Override
    protected void lazyLoad() {
        if(!isPrepared || !isVisible) {
            return;
        }
        boolean isStock = getAwardDbInfo();
        show_cart(isStock);
        initView();

        long user_id = preferences.getLong(Constant.USER_ID,0);
        if (user_id != 0){
            settlement(commit_cart_url);
        }

    }

    private void initView(){
        cart_all_cb.setOnCheckedChangeListener(this);
//        long cart_amount = 0;
//        for (AwardModel model:datas_award) {
//            long persize = model.getPersize();
//            if(persize == 0) persize = 1;
//            long count = model.getCopies();
//            cart_amount += count*persize;
//        }
//        String amountStr = getActivity().getResources().getString(R.string.cart_amount);
//        amountStr = String.format(amountStr,cart_amount);
//        cart_amount_tv.setText(amountStr);
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) msg.obj;
            switch (msg.what) {
                case R.id.AWARD_SUCCESS:
                    if (!result.isEmpty()) {
                        @SuppressWarnings("unchecked")
                        List<AwardModel> showlist =
                                (List<AwardModel>) result.get(Constant.AWARD_LIST);
                        if (showlist == null) return;
                        if (!showlist.isEmpty()) {
                            datas_award.clear();
                            for (int i = 0; i < showlist.size(); i++) {
                                AwardModel awardModel = showlist.get(i);
                                AwardModel model_db ;
                                try {
                                    model_db = db.selector(AwardModel.class).where("timesid", "=", awardModel.getTimeid()).findFirst();
                                    Log.e("model_db", model_db + "");
                                    if (model_db != null) {
                                        long count = model_db.getCopies();
                                        long surplus = awardModel.getTotal()-awardModel.getSaled();
                                        if (count > surplus){
                                            db.update(AwardModel.class, WhereBuilder.b("timesid", "=", awardModel.getTimeid()),new KeyValue("copies", surplus));
                                            awardModel.setCopies(surplus);
                                        }
                                    }
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }

                                datas_award.add(awardModel);
                            }
                        }
                        awardAdapter.setData(datas_award);
                        awardAdapter.notifyDataSetChanged();
                    } else {
                        String returnContent = (String) result.get(Constant.RETURN_CONTENT);
                        Utility.toastShow(x.app(), returnContent);
                    }
                    break;
                case R.id.SETTLEMENT_SUCCESS:
                    if (!result.isEmpty()) {
                        try {
                            db.delete(AwardModel.class);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("ordernumber", (String) result.get("ordernumber"));
                        bundle.putLong("amount", (long) result.get("amount"));
                        bundle.putLong("money", (long) result.get("money"));
                        bundle.putLong("coin", (long) result.get("coin"));
                        intent.putExtras(bundle);
                        intent.setClass(getActivity(), PaymentActivity.class);
                        startActivityForResult(intent,Constant.REQUEST_CODE);
                    }
                    break;
                case R.id.RECOMMEND_SUCCESS:
                    if (!result.isEmpty()) {
                        @SuppressWarnings("unchecked")
                        List<AwardModel> showlist =
                                (List<AwardModel>) result.get(Constant.AWARD_LIST);
                        if (showlist == null) return;
                        if (!showlist.isEmpty()) {
                            datas_rec.clear();
                            for (int i = 0; i < showlist.size(); i++) {
                                AwardModel awardModel = showlist.get(i);
                                datas_rec.add(awardModel);
                            }
                        }
                        adapter_rec.setData(datas_rec);
                        adapter_rec.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };

    private DbManager db;

    @ViewInject(R.id.cart_award_Lv)
    private PullToRefreshListView listView;
    private CartListAdapter awardAdapter;
    private List<AwardModel> datas_award  = new ArrayList<AwardModel>();
    private void setAwardList(){
        listView.setAdapter(getAwardListAdapter());
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        setListViewListener();
    }


    private ListBaseAdapter<AwardModel> getAwardListAdapter(){
        awardAdapter = new CartListAdapter(getActivity(),datas_award,LuckyBuy_Cart.this);
        return awardAdapter;
    }

    private void setListViewListener(){
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(x.app(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                // Do work to refresh the list here.
                refreshCartListInfo();
            }
        });

        // Add an end-of-list listener
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                Toast.makeText(x.app(), "End of List!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean getAwardDbInfo(){
        datas_award.clear();
        List<AwardModel> list = null;
        try {
            list = db.selector(AwardModel.class).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }

        if (list != null) {
            Log.e("list_size: ", list.size() + "");
            for (AwardModel model : list) {
                datas_award.add(model);
                Log.e("model", model.getIdx()+";"+model.getTimeid() + ";" + model.getCopies());
            }
        }

        if (datas_award.isEmpty()) {
            return false;
        }else {
            return true;
        }
    }

    private void refreshCartListInfo(){
        RequestParams params_ref = new RequestParams(Constant.getBaseUrl() + "page/ucenter/cartfresh.ashx");
        TokenVerify.addToken(getActivity(),params_ref);
        long userid = preferences.getLong(Constant.USER_ID,0);
        if (userid == 0) {
            listView.onRefreshComplete();
            return ;
        }
        params_ref.addBodyParameter("uidx", userid+"");
        x.http().post(params_ref, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                TokenVerify.saveCookie(getActivity());
                Map<String, Object> resultMap = ParseData.parseAwardInfo(result);
                mHandler.obtainMessage(R.id.AWARD_SUCCESS, resultMap).sendToTarget();
                listView.onRefreshComplete();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listView.onRefreshComplete();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                listView.onRefreshComplete();
            }

            @Override
            public void onFinished() {
                listView.onRefreshComplete();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case Constant.RESULT_CODE_UPDATE:
                boolean isStock = getAwardDbInfo();
                show_cart(isStock);
                long user_id = preferences.getLong(Constant.USER_ID,0);
                if (user_id != 0){
                    settlement(commit_cart_url);
                }
                break;
        }

    }

    private SharedPreferences preferences;
    private String commit_cart_url = "page/ucenter/cartadd.ashx";
    private String settlement_url = "page/ucenter/Ordercreate.ashx";
    private void settlement() {
        this.settlement(settlement_url);
    }
    private void settlement(String url) {
        RequestParams params = new RequestParams(Constant.getBaseUrl() + url);
        TokenVerify.addToken(getActivity(),params);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < datas_award.size(); i++) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uidx", preferences.getLong(Constant.USER_ID,0));
                jsonObject.put("goodid",datas_award.get(i).getIdx());
                jsonObject.put("timeid",datas_award.get(i).getTimeid());
                jsonObject.put("copies",awardAdapter.getData().get(i).getCopies());
                jsonArray.put(i,jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        params.addBodyParameter("data", jsonArray.toString());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                TokenVerify.saveCookie(getActivity());
                Log.e(TAG, "onSuccess: "+ result);
                if (result == null) return;
                Map<String, Object> resultMap = ParseData.parseSettlementInfo(result);
                mHandler.obtainMessage(R.id.SETTLEMENT_SUCCESS,resultMap).sendToTarget();
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


    @ViewInject(R.id.cart_commodity_rl)
    private RelativeLayout commodity_rl;

    @ViewInject(R.id.cart_no_commodity_ll)
    private LinearLayout no_commodity_ll;

    private void show_cart(boolean isStock){
        int exits = isStock ? 0 : 1;
        switch (exits){
            case 0:
                editBtn.setVisibility(View.VISIBLE);
                commodity_rl.setVisibility(View.VISIBLE);
                no_commodity_ll.setVisibility(View.GONE);
                awardAdapter.notifyDataSetChanged();
                break;
            case 1:
                editBtn.setVisibility(View.GONE);
                commodity_rl.setVisibility(View.GONE);
                no_commodity_ll.setVisibility(View.VISIBLE);
                getRecAwardListInfo(REQUEST_TYPE, REQUEST_COUNT, LAST_ID);
                break;
        }
    }


    private void updateCartAndDb(){
        List<AwardModel> deleteList = awardAdapter.getDeleteList();
        try {
            for (int i = 0; i < deleteList.size(); i++) {
                AwardModel model = deleteList.get(i);
                db.delete(model);
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
        if(!getAwardDbInfo()) return;
        awardAdapter.setData(datas_award);
        awardAdapter.notifyDataSetChanged();
    }

    private void updateBadgeView(){
        Activity activity = getActivity();
        if(activity instanceof MainActivity){
            int count = datas_award.size();
            BadgeView badgeView = ((MainActivity)activity).getBadgeView();
            badgeView.setVisibility(View.GONE);
            if(count > 0){
                badgeView.setVisibility(View.VISIBLE);
                badgeView.setText(String.valueOf(count));
            }
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            List<AwardModel> deleteList = awardAdapter.getDeleteList();
            for (AwardModel model: awardAdapter.getData()) {
                model.setDeleteAward(true);
                deleteList.add(model);
            }
            awardAdapter.notifyDataSetInvalidated();
        }else{
            awardAdapter.setDelete(false);
            awardAdapter.notifyDataSetInvalidated();
        }
    }



    private String REQUEST_TYPE = "hot";
    private String REQUEST_COUNT = "10";
    private String LAST_ID = "0";
    @ViewInject(R.id.cart_recommend_Lv)
    private HorizontalListView listView_rec;
    private RecommendAwardAdapter adapter_rec;
    private List<AwardModel> datas_rec;
    private void setRecAwardList(){
        listView_rec.setAdapter(getRecAwardListAdapter());
        listView_rec.setOnItemClickListener(this);
    }

    private ListBaseAdapter<AwardModel> getRecAwardListAdapter(){
        datas_rec = new ArrayList<>();
        adapter_rec = new RecommendAwardAdapter(getActivity(),datas_award);
        return adapter_rec;
    }

    private void getRecAwardListInfo(String type, String pagesize, String lastidx){
        RequestParams params = new RequestParams(Constant.getBaseUrl() +"page/good/list.ashx");
        params.addQueryStringParameter("type",type);
        params.addQueryStringParameter("pagesize",pagesize);
        params.addQueryStringParameter("lastidx",lastidx);
        params.setCacheMaxAge(1000 * 60);
        x.http().get(params, new Callback.CacheCallback<String>() {
            private boolean hasError = false;
            private String result = null;
            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false;
            }

            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    this.result = result;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    Map<String, Object> resultMap = ParseData.parseAwardInfo(result);
                    mHandler.obtainMessage(R.id.RECOMMEND_SUCCESS, resultMap).sendToTarget();
                }
            }
        });
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(),DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("bundle",datas_rec.get(position));
        intent.putExtras(bundle);
        startActivityForResult(intent,Constant.REQUEST_CODE);
    }
}
