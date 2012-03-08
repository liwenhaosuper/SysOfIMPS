package com.imps.ui.widget;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.basetypes.SystemMsgType;
import com.imps.services.impl.ServiceManager;

public class SystemMsgAdapter extends BaseAdapter{
	private Context context;
	private List<SystemMsgType> mItems;
	public SystemMsgAdapter(Context context,List<SystemMsgType> items){
		this.context = context;
		this.mItems = items;
	}
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		SystemMsgType item = mItems.get(position);
		ViewHolder holder = null;
		if(convertView==null||item.type==SystemMsgType.FROM){
			holder = new ViewHolder();
			if(item.type==SystemMsgType.FROM){
				convertView = LayoutInflater.from(context).inflate(R.layout.systemmsg_addfrireq,null);	
				holder.username = (TextView)convertView.findViewById(R.id.messagedetail_row_name);
				holder.time = (TextView)convertView.findViewById(R.id.messagedetail_row_date);
				holder.text = (TextView)convertView.findViewById(R.id.messagedetail_row_text);
				holder.accept = (Button)convertView.findViewById(R.id.addreq_accept);
				holder.deny = (Button)convertView.findViewById(R.id.addreq_deny);
				holder.status = (TextView)convertView.findViewById(R.id.addreq_status);
				if(item.status==SystemMsgType.ACCEPTED){
					holder.accept.setVisibility(View.INVISIBLE);
					holder.deny.setVisibility(View.INVISIBLE);
					holder.status.setText(IMPSDev.getContext().getResources().getString(R.string.accepted));
				}else if(item.status==SystemMsgType.DENIED){
					holder.accept.setVisibility(View.INVISIBLE);
					holder.deny.setVisibility(View.INVISIBLE);
					holder.status.setText(IMPSDev.getContext().getResources().getString(R.string.denied));
				}else{
					holder.status.setVisibility(View.INVISIBLE);
					holder.accept.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							ServiceManager.getmContact().sendAddFriendRsp(mItems.get(position).name, true);
							mItems.get(position).status=SystemMsgType.ACCEPTED;
							ServiceManager.getmContact().sendFriListReq();
						}
					});
					holder.deny.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							ServiceManager.getmContact().sendAddFriendRsp(mItems.get(position).name, false);
							mItems.get(position).status=SystemMsgType.DENIED;
						}
						
					});
				}
			}else if(item.type==SystemMsgType.TO){
				convertView = LayoutInflater.from(context).inflate(R.layout.systemmsg_addfrirsp,null);	
				holder.username = (TextView)convertView.findViewById(R.id.messagedetail_row_name);
				holder.time = (TextView)convertView.findViewById(R.id.messagedetail_row_date);
				holder.text = (TextView)convertView.findViewById(R.id.messagedetail_row_text);
				holder.accept = (Button)convertView.findViewById(R.id.addreq_accept);
				holder.deny = (Button)convertView.findViewById(R.id.addreq_deny);
				holder.status = (TextView)convertView.findViewById(R.id.addreq_status);
				if(item.status==SystemMsgType.NONE){
					holder.accept.setVisibility(View.INVISIBLE);
					holder.deny.setVisibility(View.INVISIBLE);
					holder.status.setVisibility(View.INVISIBLE);
				}
			}
			holder.username.setText(item.name);
			holder.text.setText(item.text);
			holder.time.setText(item.time);
			convertView.setTag(holder);
		}
		return convertView;
	}
	private class ViewHolder{
		TextView username;
		TextView time;
		TextView text;
		Button accept;
		Button deny;
		TextView status;
	}

}
