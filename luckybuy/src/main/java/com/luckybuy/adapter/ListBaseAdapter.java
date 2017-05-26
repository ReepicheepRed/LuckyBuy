package com.luckybuy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class ListBaseAdapter<T> extends BaseAdapter {

	private  Context mContext;
	private  List<T> mData;
	public ListBaseAdapter(Context context, List<T> list) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mData = list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.mData != null ? mData.size() : 0;
	}
	
	public Context getContext(){
		return mContext;
	}
	
	public void setData(List<T> list){
		this.mData = list;
	}
	
	public List<T> getData(){
		return mData;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void clear(){
		if(mData != null){
			mData.clear();
			notifyDataSetInvalidated();
		}
	}


	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

	protected long lastId;

	public long getLastid() {
		return lastId;
	}

}
