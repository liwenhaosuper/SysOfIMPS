package com.imps.util;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imps.R;

public class ChattingAdapter extends BaseAdapter{
	protected static final String TAG = "ChattingAdapter";
	private Context context;
	private List<ListContentEntity> chatMsgs;
	public ChattingAdapter(Context context,List<ListContentEntity> msg)
	{
		this.context = context;
		this.chatMsgs = msg;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return chatMsgs.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return chatMsgs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		ListContentEntity msg = chatMsgs.get(position);
		if (convertView == null || (holder = (ViewHolder) convertView.getTag()).flag != msg.getLayoutID()) {
			holder = new ViewHolder();
			if(msg.getLayoutID()==ListContentEntity.MESSAGE_FROM)
			{
				holder.flag =ListContentEntity.MESSAGE_FROM;
				convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_from, null);
			}				
			else if(msg.getLayoutID()==ListContentEntity.MESSAGE_TO)
			{
				holder.flag = ListContentEntity.MESSAGE_TO;
				convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_to, null);
			}
			else if(msg.getLayoutID()==ListContentEntity.MESSAGE_FROM_PICTURE)
			{
				holder.flag = ListContentEntity.MESSAGE_FROM_PICTURE;
				convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_from, null);
			}
			else if(msg.getLayoutID()==ListContentEntity.MESSAGE_TO_PICTURE)
			{
				holder.flag = ListContentEntity.MESSAGE_TO_PICTURE;
				convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_to_picture, null);
			}
			if(convertView==null)
			{
				Log.d("ChattingAdapter", "the couvert view is null");
			}
			holder.text = (TextView) convertView.findViewById(R.id.messagedetail_row_text);
			if(holder.text==null){
				holder.text = new TextView(context);
			}
			holder.time = (TextView) convertView.findViewById(R.id.messagedetail_row_date);
			holder.username = (TextView) convertView.findViewById(R.id.messagedetail_row_name);
			holder.image = (ImageView)convertView.findViewById(R.id.message_bitmap);
			if(holder.image==null)
				Log.d("ChattingAdapter", "holder.image is null");
			convertView.setTag(holder);
		}
		String t = msg.getText();
		holder.text.setText("");
		int pos = 0;
		while ((pos = t.indexOf("[exp_")) != -1){
			holder.text.append(t.substring(0, pos));
			t = t.substring(pos);
			if (t.length() < 8){
				holder.text.append(t);
				break;
			}
			String flag = t.substring(0, 8);
			t = t.substring(8);
			if (!flag.endsWith("]")){
				holder.text.append(flag);
				continue;
			}
			int id = changeStrToId(flag.substring(1, 7));
			if (id != -1){
				Drawable drawable = context.getResources().getDrawable(id);   
		        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());   
		        SpannableString spannable = new SpannableString(flag);   
		        //要让图片替代指定的文字就要用ImageSpan   
		        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);   
		        //开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）   
		        //最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12 
		        spannable.setSpan(span, 0, flag.length(), 
		        		Spannable.SPAN_INCLUSIVE_EXCLUSIVE);     
		        holder.text.append(spannable);	
			}
			else {
				holder.text.append(flag);
			}
		}
		holder.text.append(t);
		System.out.println("date:"+ msg.getDate());
		holder.time.setText(msg.getDate());
		holder.username.setText(msg.getName());
		if(msg.getImagePath()!=null&msg.getImagePath()!=""){
			Bitmap drawnMap = BitmapFactory.decodeFile(msg.getImagePath()).copy(Bitmap.Config.ARGB_8888,true);
			if(holder.image!=null&&drawnMap!=null)
			{
				holder.image.setImageBitmap(drawnMap);
			}
			else{
				if(holder.image==null)
					Log.d("ChattingAdapter", "holder.image is null");
				else 
					Log.d("ChattingAdapter", "bitmap is null...path is:"+msg.getImagePath());
			}
				
		}
		return convertView;
	}
	private int changeStrToId(String str){
		if ("exp_01".equals(str)){
			return R.drawable.exp_01;
		}
		else if ("exp_02".equals(str)){
			return R.drawable.exp_02;
		}
		else if ("exp_03".equals(str)){
			return R.drawable.exp_03;
		}
		else if ("exp_04".equals(str)){
			return R.drawable.exp_04;
		}
		else if ("exp_05".equals(str)){
			return R.drawable.exp_05;
		}
		else if ("exp_06".equals(str)){
			return R.drawable.exp_06;
		}
		else if ("exp_07".equals(str)){
			return R.drawable.exp_07;
		}
		else if ("exp_08".equals(str)){
			return R.drawable.exp_08;
		}
		else if ("exp_09".equals(str)){
			return R.drawable.exp_09;
		}
		else if ("exp_10".equals(str)){
			return R.drawable.exp_10;
		}
		else if ("exp_11".equals(str)){
			return R.drawable.exp_11;
		}
		else if ("exp_12".equals(str)){
			return R.drawable.exp_12;
		}
		else if ("exp_13".equals(str)){
			return R.drawable.exp_13;
		}
		else if ("exp_14".equals(str)){
			return R.drawable.exp_14;
		}
		else if ("exp_15".equals(str)){
			return R.drawable.exp_15;
		}
		else if ("exp_16".equals(str)){
			return R.drawable.exp_16;
		}
		else if ("exp_17".equals(str)){
			return R.drawable.exp_17;
		}
		else if ("exp_18".equals(str)){
			return R.drawable.exp_18;
		}
		else if ("exp_19".equals(str)){
			return R.drawable.exp_19;
		}
		else if ("exp_20".equals(str)){
			return R.drawable.exp_20;
		}
		else if ("exp_21".equals(str)){
			return R.drawable.exp_21;
		}
		else if ("exp_22".equals(str)){
			return R.drawable.exp_22;
		}
		else if ("exp_23".equals(str)){
			return R.drawable.exp_23;
		}
		else if ("exp_24".equals(str)){
			return R.drawable.exp_24;
		}
		else if ("exp_25".equals(str)){
			return R.drawable.exp_25;
		}
		else if ("exp_26".equals(str)){
			return R.drawable.exp_26;
		}
		return -1;
	}
	
	//优化listview的Adapter
	class ViewHolder {
		TextView username;
		TextView time;
		TextView text;
		ImageView image;
		int flag;
	}

}
