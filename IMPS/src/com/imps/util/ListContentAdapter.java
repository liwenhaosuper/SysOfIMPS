package com.imps.util;

import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.imps.R;

public class ListContentAdapter implements ListAdapter{
	private ArrayList<ListContentEntity> coll;
	private Context ctx;
	private final DataSetObservable mDataSetObservable = new DataSetObservable();
	public ListContentAdapter(Context context ,ArrayList<ListContentEntity> coll) {
		ctx = context;
		this.coll = coll;
	}
	
	public boolean areAllItemsEnabled() {
		return false;
	}
	public boolean isEnabled(int arg0) {
		return false;
	}
	public int getCount() {
		return coll.size();
	}
	public Object getItem(int position) {
		return coll.get(position);
	}
	public long getItemId(int position) {
		return position;
	}
	public int getItemViewType(int position) {
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		ListContentEntity entity = coll.get(position);
		int itemLayout = entity.getLayoutID();
		
		LinearLayout layout = new LinearLayout(ctx);
		LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		vi.inflate(itemLayout, layout,true);
		
		TextView tvName = (TextView) layout.findViewById(R.id.messagedetail_row_name);
		tvName.setText(entity.getName());
		
		TextView tvDate = (TextView) layout.findViewById(R.id.messagedetail_row_date);
		tvDate.setText(entity.getDate());
		
		TextView tvText = (TextView) layout.findViewById(R.id.messagedetail_row_text);
		tvText.setText(entity.getText());
		return layout;
	}
	public int getViewTypeCount() {
		return 2;
	}
	public boolean hasStableIds() {
		return false;
	}
	public boolean isEmpty() {
		return false;
	}
	public void registerDataSetObserver(DataSetObserver observer) {
		mDataSetObservable.registerObserver(observer);
	}
	public void unregisterDataSetObserver(DataSetObserver observer) {
		mDataSetObservable.unregisterObserver(observer);
	}
    public void notifyDataSetChanged()
    {
    	mDataSetObservable.notifyChanged();
    }
}
