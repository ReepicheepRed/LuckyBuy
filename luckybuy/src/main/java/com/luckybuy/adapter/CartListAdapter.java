package com.luckybuy.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luckybuy.LuckyBuy_Cart;
import com.luckybuy.MainActivity;
import com.luckybuy.R;
import com.luckybuy.db.DB_Config;
import com.luckybuy.model.AwardModel;
import com.luckybuy.util.StringUtil;
import com.luckybuy.util.Utility;

import org.apache.commons.collections.Bag;
import org.xutils.DbManager;
import org.xutils.common.util.DensityUtil;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhiPeng.S on 2016/6/1.
 */
public class CartListAdapter extends ListBaseAdapter<AwardModel> {


    protected LayoutInflater mInflater;
    protected ImageOptions imageOptions;
    protected AwardHolder holder;
    private boolean isDelete;
    private LuckyBuy_Cart fragment;

    private Button editBtn;
    private CheckBox cart_all_cb;
    private RelativeLayout cart_delete_rl;
    private RelativeLayout cart_bottom;
    private TextView cart_amount_tv;

    private List<AwardModel> deleteList;
    protected DbManager db;
    long cart_amount;
    public CartListAdapter(Context context, List<AwardModel> list, LuckyBuy_Cart fragment) {
        this(context, list);
        this.fragment = fragment;
        this.editBtn = fragment.getEditBtn();
        this.cart_all_cb = fragment.getCart_all_cb();
        this.cart_delete_rl = fragment.getCart_delete_rl();
        this.cart_bottom = fragment.getCart_bottom();
        this.cart_amount_tv = fragment.getCart_amount_tv();
    }

    public CartListAdapter(Context context, List<AwardModel> list) {
        super(context, list);
        db = x.getDb(DB_Config.getDaoConfig());
        deleteList = new ArrayList<>();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                .setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.award_default_120)
                .setFailureDrawableId(R.mipmap.award_default_120)
                .build();
    }

    @Override
    public int getCount() {
        return getData().size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_award_cart, null);
            holder = new AwardHolder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        }else {
            holder = (AwardHolder) convertView.getTag();
        }

        if(getData().size() == 0){
            return convertView;
        }
        final AwardModel awardModel = getData().get(position);

        long goodsNum = awardModel.getCopies();


        x.image().bind(holder.awardIv,awardModel.getHeadpic(),imageOptions);

        final String titleCur = awardModel.getTitle()+ awardModel.getSubtitle();
        holder.awardTitle.setText(titleCur);

        ForegroundColorSpan fcSpan = StringUtil.fcSpan(R.color.light_red);
        String surplusStr = getContext().getResources().getString(R.string.cart_surplus);
        long surplus = awardModel.getTotal() - awardModel.getSaled();
        String surplusCur = String.format(surplusStr,awardModel.getTotal(),surplus);
        int start = surplusCur.lastIndexOf(surplus+"");
        int end = start + String.valueOf(surplus).length();
        SpannableStringBuilder builder = StringUtil.singleSpan(surplusCur,start,end,fcSpan);
        holder.awardTotal.setText(builder);


        try{
            double probability = Double.valueOf(goodsNum)/Double.valueOf(awardModel.getTotal());
            probability = Utility.round(probability*100, 2);
            if(probability == 0) probability = 0.01;
            String prob = probability + "%";
            String probStr = getContext().getString(R.string.win_probability,prob);
            SpannableStringBuilder builder_p = StringUtil.singleSpan(probStr,prob,fcSpan);
            holder.win_prob_tv.setText(builder_p);
        }catch (Exception e){
            e.printStackTrace();
        }


        final TextView.OnEditorActionListener OnEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String countStr = v.getText().toString().trim();

                if(countStr.length() <=0){
                    Utility.toastShow(getContext(),R.string.cart_count_tip);
                    return true;
                }

                if(actionId == EditorInfo.IME_ACTION_NEXT)
                    actionId = EditorInfo.IME_ACTION_DONE;

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService( Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }

                    try {
                        long count = Long.valueOf(countStr);
                        long surplus = awardModel.getTotal()- awardModel.getSaled();
                        if(count > surplus) count = surplus;
                        db.update(AwardModel.class, WhereBuilder.b("timesid", "=", awardModel.getTimeid()),new KeyValue("copies", count));
                        awardModel.setCopies(count);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    CartListAdapter.this.notifyDataSetChanged();
                    return true;
                }
                return false;
            }
        };
        holder.awardNumber.setText(goodsNum +"");
        holder.awardNumber.setOnEditorActionListener(OnEditorActionListener);

        TextWatcher textWatcher = new TextWatcher() {
            EditText editText = holder.awardNumber;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                long count = Long.valueOf(editText.getText().toString().trim());
                long surplus = awardModel.getTotal()- awardModel.getSaled();
                editText.setText(String.valueOf(count));
                if(count > surplus)
                    editText.setText(String.valueOf(surplus));

                OnEditorActionListener.onEditorAction(editText,EditorInfo.IME_ACTION_DONE,null);
            }
        };

//        holder.awardNumber.addTextChangedListener(textWatcher);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CheckBox checkBox = holder.cart_cb;
                final ImageButton plus_ib = holder.plus_ib;
                final ImageButton minus_ib = holder.minus_ib;
                switch (v.getId()){
                    case R.id.cart_plus_ib:
                        try {
                            long count = awardModel.getCopies();
                            count++;
                            if(count > 0) {
                                minus_ib.setEnabled(true);
                            }
                            if(count > awardModel.getTotal()-awardModel.getSaled()) {
                                plus_ib.setEnabled(false);
                                Utility.toastShow(x.app(),R.string.count_max);
                                return;
                            }
                            db.update(AwardModel.class, WhereBuilder.b("timesid", "=", awardModel.getTimeid()),new KeyValue("copies", count));
                            awardModel.setCopies(count);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        CartListAdapter.this.notifyDataSetChanged();
                        break;
                    case R.id.cart_minus_ib:
                        try {
                            long count = awardModel.getCopies();
                            count--;
                            if(count < awardModel.getTotal()-awardModel.getSaled()) {
                                plus_ib.setEnabled(true);
                            }
                            if(count <= 0) {
                                minus_ib.setEnabled(false);
                                return;
                            }
                            db.update(AwardModel.class, WhereBuilder.b("timesid", "=", awardModel.getTimeid()),new KeyValue("copies", count));
                            awardModel.setCopies(count);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        CartListAdapter.this.notifyDataSetChanged();
                        break;
                    case R.id.cart_list_item_rl:
                        //wait deal for checkbox
                        /*if (false) {
                            int isCheck = checkBox.isChecked()? 0 : 1;
                            switch (isCheck){
                                case 0:
                                    awardModel.setDeleteAward(false);
                                    CartListAdapter.this.notifyDataSetChanged();
                                    break;
                                case 1:
                                    awardModel.setDeleteAward(true);
                                    CartListAdapter.this.notifyDataSetChanged();
                                    break;
                            }
                        }*/
                        break;
                }
            }
        };
        holder.plus_ib.setOnClickListener(onClickListener);
        holder.minus_ib.setOnClickListener(onClickListener);
        holder.cart_item_rl.setOnClickListener(onClickListener);

        if (isDelete) {

            holder.cart_cb.setVisibility(View.VISIBLE);
            if(awardModel.isDeleteAward()){
                holder.cart_cb.setChecked(true);
            }else{
                holder.cart_cb.setChecked(false);
            }
            CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int isCheck = isChecked ? 0 : 1;
                    switch (isCheck) {
                        case 0:
                            awardModel.setDeleteAward(true);
                            deleteList.add(awardModel);
                            if(deleteList.size() == getCount()){
                                updateCartBottomAndTop(deleteList.size());
                            }
                            break;
                        case 1:
                            awardModel.setDeleteAward(false);
                            deleteList.remove(awardModel);
                            if(deleteList.size() == 0) {
                                CartListAdapter.this.setDelete(false);
                            }
                            break;
                    }

                    CartListAdapter.this.notifyDataSetChanged();
                }
            };
            holder.cart_cb.setOnCheckedChangeListener(checkedChangeListener);



        } else {
            awardModel.setDeleteAward(false);
            holder.cart_cb.setChecked(false);
            holder.cart_cb.setVisibility(View.GONE);
            deleteList.clear();
            updateCartBottomAndTop(deleteList.size());
        }

        return convertView;
    }



    public  void updateCartBottomAndTop(int totalNum){
        int state = totalNum;
        if (state > 0){
            state = totalNum == getData().size()? 1 : -1;
        }

        switch(state){
            case 0:
                editBtn.setText(R.string.edit);
                cart_delete_rl.setVisibility(View.GONE);
                cart_bottom.setVisibility(View.VISIBLE);
                cart_all_cb.setChecked(false);
                long cart_amount = 0;
                for (AwardModel model:getData()) {
                    long persize = model.getPersize();
                    if(persize == 0) persize = 1;
                    long count = model.getCopies();
                    cart_amount += count*persize;
                }
                String amountStr = getContext().getResources().getString(R.string.cart_amount);
                amountStr = String.format(amountStr,cart_amount);
                cart_amount_tv.setText(amountStr);
                break;
            case 1:
                cart_all_cb.setChecked(true);
                break;
            default:
                cart_all_cb.setChecked(false);
                break;
        }
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public List<AwardModel> getDeleteList() {
        return deleteList;
    }

    public void setDeleteList(List<AwardModel> deleteList) {
        this.deleteList = deleteList;
    }


    protected class AwardHolder {
        @ViewInject(R.id.cart_award_iv)
        public ImageView awardIv;

        @ViewInject(R.id.cart_award_name_tv)
        public TextView awardTitle;

        @ViewInject(R.id.cart_total_tv)
        public TextView awardTotal;

        @ViewInject(R.id.cart_single_num)
        public EditText awardNumber;

        @ViewInject(R.id.cart_plus_ib)
        public ImageButton plus_ib;

        @ViewInject(R.id.cart_minus_ib)
        public ImageButton minus_ib;

        @ViewInject(R.id.cart_cb)
        public CheckBox cart_cb;

        @ViewInject(R.id.cart_list_item_rl)
        public RelativeLayout cart_item_rl;

        @ViewInject(R.id.win_prob_tv)
        public TextView win_prob_tv;
    }
}
