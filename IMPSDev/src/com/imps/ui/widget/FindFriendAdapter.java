package com.imps.ui.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.imps.R;
import com.imps.basetypes.FindFriendItem;

public class FindFriendAdapter extends BaseAdapter {

	private Context context;
	private List<FindFriendItem> items;
	public FindFriendAdapter(Context context,List<FindFriendItem> items){
		this.context = context;
		this.items = items;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		FindFriendItem item = items.get(position);
		ViewHolder holder = null;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.findfrienditem,null);			
			holder.image = (ImageView)convertView.findViewById(R.id.find_icon);
			holder.btn = (Button)convertView.findViewById(R.id.btn_share);
			holder.text = (TextView)convertView.findViewById(R.id.find_content);
			holder.image.setImageResource(item.image);
			holder.text.setText(item.text);
			convertView.setTag(holder);
		}
		return convertView;
	}
	
	class ViewHolder{
		ImageView image;
		TextView text;
		Button btn;
		byte type;
		 
	}

}
