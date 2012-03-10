package com.imps.ui.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	private static Map<String, Integer> faces = new HashMap<String, Integer>();
	
	public ChattingAdapter(Context context,List<ListContentEntity> msg)
	{
		this.context = context;
		this.chatMsgs = msg;
		initFaceMap();
	}
	private void initFaceMap() {
		// TODO Auto-generated method stub
		faces.put("[exp_01]", R.drawable.exp_01);
		faces.put("[exp_02]", R.drawable.exp_02);
		faces.put("[exp_03]", R.drawable.exp_03);
		faces.put("[exp_04]", R.drawable.exp_04);
		faces.put("[exp_05]", R.drawable.exp_05);
		faces.put("[exp_06]", R.drawable.exp_06);
		faces.put("[exp_07]", R.drawable.exp_07);
		faces.put("[exp_08]", R.drawable.exp_08);
		faces.put("[exp_09]", R.drawable.exp_09);
		faces.put("[exp_10]", R.drawable.exp_10);
		faces.put("[exp_11]", R.drawable.exp_11);
		faces.put("[exp_12]", R.drawable.exp_12);
		faces.put("[exp_13]", R.drawable.exp_13);
		faces.put("[exp_14]", R.drawable.exp_14);
		faces.put("[exp_15]", R.drawable.exp_15);
		faces.put("[exp_16]", R.drawable.exp_16);
		faces.put("[exp_17]", R.drawable.exp_17);
		faces.put("[exp_18]", R.drawable.exp_18);
		faces.put("[exp_19]", R.drawable.exp_19);
		faces.put("[exp_20]", R.drawable.exp_20);
		faces.put("[exp_21]", R.drawable.exp_21);
		faces.put("[exp_22]", R.drawable.exp_22);
		faces.put("[exp_23]", R.drawable.exp_23);
		faces.put("[exp_24]", R.drawable.exp_24);
		faces.put("[exp_25]", R.drawable.exp_25);
		faces.put("[exp_26]", R.drawable.exp_26);
		faces.put("[e001]", R.drawable.e001);
		faces.put("[e002]", R.drawable.e002);
		faces.put("[e003]", R.drawable.e003);
		faces.put("[e004]", R.drawable.e004);
		faces.put("[e005]", R.drawable.e005);
		faces.put("[e00d]", R.drawable.e00d);
		faces.put("[e00e]", R.drawable.e00e);
		faces.put("[e00f]", R.drawable.e00f);
		faces.put("[e010]", R.drawable.e010);
		faces.put("[e011]", R.drawable.e011);
		faces.put("[e012]", R.drawable.e012);
		faces.put("[e020]", R.drawable.e020);
		faces.put("[e021]", R.drawable.e021);
		faces.put("[e022]", R.drawable.e022);
		faces.put("[e023]", R.drawable.e023);
		faces.put("[e036]", R.drawable.e036);
		faces.put("[e038]", R.drawable.e038);
		faces.put("[e03e]", R.drawable.e03e);
		faces.put("[e048]", R.drawable.e048);
		faces.put("[e049]", R.drawable.e049);
		faces.put("[e04a]", R.drawable.e04a);
		faces.put("[e04b]", R.drawable.e04b);
		faces.put("[e04c]", R.drawable.e04c);
		faces.put("[e04e]", R.drawable.e04e);
		faces.put("[e056]", R.drawable.e056);
		faces.put("[e057]", R.drawable.e057);
		faces.put("[e058]", R.drawable.e058);
		faces.put("[e059]", R.drawable.e059);
		faces.put("[e05a]", R.drawable.e05a);
		faces.put("[e105]", R.drawable.e105);
		faces.put("[e106]", R.drawable.e106);
		faces.put("[e107]", R.drawable.e107);
		faces.put("[e108]", R.drawable.e108);
		faces.put("[e10c]", R.drawable.e10c);
		faces.put("[e111]", R.drawable.e111);
		faces.put("[e115]", R.drawable.e115);
		faces.put("[e117]", R.drawable.e117);
		faces.put("[e11a]", R.drawable.e11a);
		faces.put("[e11c]", R.drawable.e11c);
		faces.put("[e11d]", R.drawable.e11d);
		faces.put("[e13c]", R.drawable.e13c);
		faces.put("[e13d]", R.drawable.e13d);
		faces.put("[e14c]", R.drawable.e14c);
		faces.put("[e14d]", R.drawable.e14d);
		faces.put("[e152]", R.drawable.e152);
		faces.put("[e153]", R.drawable.e153);
		faces.put("[e155]", R.drawable.e155);
		faces.put("[e156]", R.drawable.e156);
		faces.put("[e157]", R.drawable.e157);
		faces.put("[e201]", R.drawable.e201);
		faces.put("[e21c]", R.drawable.e21c);
		faces.put("[e21d]", R.drawable.e21d);
		faces.put("[e21e]", R.drawable.e21e);
		faces.put("[e21f]", R.drawable.e21f);
		faces.put("[e220]", R.drawable.e220);
		faces.put("[e221]", R.drawable.e221);
		faces.put("[e222]", R.drawable.e222);
		faces.put("[e22e]", R.drawable.e22e);
		faces.put("[e22f]", R.drawable.e22f);
		faces.put("[e230]", R.drawable.e230);
		faces.put("[e231]", R.drawable.e231);
		faces.put("[e253]", R.drawable.e253);
		faces.put("[e31d]", R.drawable.e31d);
		faces.put("[e31e]", R.drawable.e31e);
		faces.put("[e31f]", R.drawable.e31f);
		faces.put("[e326]", R.drawable.e326);
		faces.put("[e327]", R.drawable.e327);
		faces.put("[e328]", R.drawable.e328);
		faces.put("[e329]", R.drawable.e329);
		faces.put("[e32a]", R.drawable.e32a);
		faces.put("[e32b]", R.drawable.e32b);
		faces.put("[e32c]", R.drawable.e32c);
		faces.put("[e32d]", R.drawable.e32d);
		faces.put("[e32e]", R.drawable.e32e);
		faces.put("[e32f]", R.drawable.e32f);
		faces.put("[e330]", R.drawable.e330);
		faces.put("[e331]", R.drawable.e331);
		faces.put("[e334]", R.drawable.e334);
		faces.put("[e401]", R.drawable.e401);
		faces.put("[e402]", R.drawable.e402);
		faces.put("[e403]", R.drawable.e403);
		faces.put("[e404]", R.drawable.e404);
		faces.put("[e405]", R.drawable.e405);
		faces.put("[e406]", R.drawable.e406);
		faces.put("[e407]", R.drawable.e407);
		faces.put("[e408]", R.drawable.e408);
		faces.put("[e409]", R.drawable.e409);
		faces.put("[e40a]", R.drawable.e40a);
		faces.put("[e40b]", R.drawable.e40b);
		faces.put("[e40c]", R.drawable.e40c);
		faces.put("[e40d]", R.drawable.e40d);
		faces.put("[e40e]", R.drawable.e40e);
		faces.put("[e40f]", R.drawable.e40f);
		faces.put("[e410]", R.drawable.e410);
		faces.put("[e411]", R.drawable.e411);
		faces.put("[e412]", R.drawable.e412);
		faces.put("[e413]", R.drawable.e413);
		faces.put("[e414]", R.drawable.e414);
		faces.put("[e415]", R.drawable.e415);
		faces.put("[e416]", R.drawable.e416);
		faces.put("[e417]", R.drawable.e417);
		faces.put("[e418]", R.drawable.e418);
		faces.put("[e419]", R.drawable.e419);
		faces.put("[e41a]", R.drawable.e41a);
		faces.put("[e41b]", R.drawable.e41b);
		faces.put("[e41c]", R.drawable.e41c);
		faces.put("[e41d]", R.drawable.e41d);
		faces.put("[e41e]", R.drawable.e41e);
		faces.put("[e41f]", R.drawable.e41f);
		faces.put("[e420]", R.drawable.e420);
		faces.put("[e421]", R.drawable.e421);
		faces.put("[e422]", R.drawable.e422);
		faces.put("[e423]", R.drawable.e423);
		faces.put("[e424]", R.drawable.e424);
		faces.put("[e425]", R.drawable.e425);
		faces.put("[e426]", R.drawable.e426);
		faces.put("[e427]", R.drawable.e427);
		faces.put("[e428]", R.drawable.e428);
		faces.put("[e429]", R.drawable.e429);
		faces.put("[e436]", R.drawable.e436);
		faces.put("[e437]", R.drawable.e437);
		faces.put("[e438]", R.drawable.e438);
		faces.put("[e439]", R.drawable.e439);
		faces.put("[e43a]", R.drawable.e43a);
		faces.put("[e43b]", R.drawable.e43b);
		faces.put("[e443]", R.drawable.e443);
		faces.put("[e515]", R.drawable.e515);
		faces.put("[e516]", R.drawable.e516);
		faces.put("[e517]", R.drawable.e517);
		faces.put("[e518]", R.drawable.e518);
		faces.put("[e519]", R.drawable.e519);
		faces.put("[e51a]", R.drawable.e51a);
		faces.put("[e51b]", R.drawable.e51b);
		faces.put("[e51c]", R.drawable.e51c);
		faces.put("[e51e]", R.drawable.e51e);
		faces.put("[e51f]", R.drawable.e51f);
		faces.put("[e536]", R.drawable.e536);
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
		
		Pattern pattern = Pattern.compile("\\[e(xp_[0-9][0-9]|[0-9][0-9]([0-9]|[a-f]))\\]");
        Matcher matcher = pattern.matcher(t);
        int preEnd = 0;
        while (matcher.find())
        {
        	int start = matcher.start();
        	int end = matcher.end();
        	if (preEnd != start)
        	{
        		holder.text.append(t.substring(preEnd, start));
        	}
        	preEnd = end;
        	String flag = t.substring(start, end);
        	
        	int id = faces.get(flag);
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
		holder.text.append(t.substring(preEnd));
		
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
	
	class ViewHolder {
		TextView username;
		TextView time;
		TextView text;
		ImageView image;
		ImageView audio;
		int flag;
	}

}
