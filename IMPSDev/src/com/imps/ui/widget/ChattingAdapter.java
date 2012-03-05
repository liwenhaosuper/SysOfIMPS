package com.imps.ui.widget;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.basetypes.ListContentEntity;
import com.imps.media.audio.AudioPlay;
import com.imps.media.audio.Track;

public class ChattingAdapter extends BaseAdapter{
	protected static final String TAG = ChattingAdapter.class.getCanonicalName();
	protected static final boolean DEBUG = IMPSDev.isDEBUG();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
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
				convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_from_picture, null);
			}
			else if(msg.getLayoutID()==ListContentEntity.MESSAGE_TO_PICTURE)
			{
				holder.flag = ListContentEntity.MESSAGE_TO_PICTURE;
				convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_to_picture, null);
			}
			else if(msg.getLayoutID()==ListContentEntity.MESSAGE_FROM_AUDIO)
			{
				holder.flag = ListContentEntity.MESSAGE_FROM_AUDIO;
				convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_from_audio, null);
			}
			else if(msg.getLayoutID()==ListContentEntity.MESSAGE_TO_AUDIO)
			{
				holder.flag = ListContentEntity.MESSAGE_TO_AUDIO;
				convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_to_audio, null);
			}
			if(convertView==null)
			{
				//Log.d("ChattingAdapter", "the couvert view is null");
			}
			holder.text = (TextView) convertView.findViewById(R.id.messagedetail_row_text);
			if(holder.text==null){
				holder.text = new TextView(context);
			}
			holder.time = (TextView) convertView.findViewById(R.id.messagedetail_row_date);
			holder.username = (TextView) convertView.findViewById(R.id.messagedetail_row_name);
			holder.image = (ImageView)convertView.findViewById(R.id.message_bitmap);
			if(holder.image==null){
				//Log.d("ChattingAdapter", "holder.image is null");
			}
			holder.audio = (ImageView)convertView.findViewById(R.id.message_audio);
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
		        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);   
		        spannable.setSpan(span, 0, flag.length(), 
		        		Spannable.SPAN_INCLUSIVE_EXCLUSIVE);     
		        holder.text.append(spannable);	
			}
			else {
				holder.text.append(flag);
			}
		}
		holder.text.append(t);
		//System.out.println("date:"+ msg.getDate());
		holder.time.setText(msg.getDate());
		holder.username.setText(msg.getName());
		if(holder.audio!=null){
			holder.audio.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					if(chatMsgs.get(position).status==ListContentEntity.MUTE){
						chatMsgs.get(position).status = ListContentEntity.PLAY;
						((ImageView)v).setBackgroundResource(R.drawable.icon_sound_play);
						AudioPlay track = new AudioPlay(chatMsgs.get(position),(ImageView)v);
						//track.data = chatMsgs.get(position).audioData;
						track.run();
					}else{
						chatMsgs.get(position).status = ListContentEntity.MUTE;
						((ImageView)v).setBackgroundResource(R.drawable.icon_sound_default);
					}
				}
				
			});
		}
		if(msg!=null&&msg.getImagePath()!=null&&!msg.getImagePath().equals("")){
		   	Matrix matrix = new Matrix(); 
		   	if(msg.getLayoutID()==ListContentEntity.MESSAGE_FROM_PICTURE){
		   		matrix.postScale((float)1,(float)1); 
		   	}else{
		   		matrix.postScale((float)0.5,(float)0.5);  
		   	}
	    	Bitmap tmap = BitmapFactory.decodeFile(msg.getImagePath());
	    	Bitmap drawnMap = Bitmap.createBitmap(tmap, 0,0,tmap.getWidth(), tmap.getHeight(), matrix, true);
			//Bitmap drawnMap = BitmapFactory.decodeFile(msg.getImagePath()).copy(Bitmap.Config.ARGB_8888,true);
			if(holder.image!=null&&drawnMap!=null)
			{
				holder.image.setImageBitmap(drawnMap);
			}
			else{
				if(holder.image==null){
					holder.image.setImageDrawable(
							IMPSDev.getContext().getResources().getDrawable(R.drawable.damage_image));
					//Log.d("ChattingAdapter", "holder.image is damage");
				}
				else{ 
					//Log.d("ChattingAdapter", "bitmap is null...path is:"+msg.getImagePath());
				}
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
	
	class ViewHolder {
		TextView username;
		TextView time;
		TextView text;
		ImageView image;
		ImageView audio;
		int flag;
	}

}
