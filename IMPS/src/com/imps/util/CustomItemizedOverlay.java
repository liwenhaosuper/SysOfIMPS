package com.imps.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.imps.R;
import com.imps.handler.UserManager;

public class CustomItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	   
	   private ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();
	   private Context context;
	   private String m_friName = null;
	   private String m_lngText = null;
	   private String m_latText = null;
	   private String m_status = null;
	   private EditText edt;
	   public CustomItemizedOverlay(Drawable defaultMarker) {
	        super(boundCenterBottom(defaultMarker));
	   }
	   public CustomItemizedOverlay(Drawable defaultMarker, Context context,String friName,String status,String lat,String lng) {
	        this(defaultMarker);
	        this.context = context;
	        m_friName = friName;
	        m_status = status;
	        m_lngText = lng;
	        m_latText = lat;
	   }

	   @Override
	   protected OverlayItem createItem(int i) {
	      return mapOverlays.get(i);
	   }

	   @Override
	   public int size() {
	      return mapOverlays.size();
	   }
	   
	   @Override
	   protected boolean onTap(int index) {
	      OverlayItem item = mapOverlays.get(index);
	      final Dialog dialog = new Dialog(context);
	      dialog.setTitle(item.getTitle());
	      dialog.setContentView(R.layout.chatdialog);
	      TextView vLat = (TextView)dialog.findViewById(R.id.ch_lat);
	      TextView vLng = (TextView)dialog.findViewById(R.id.ch_lng);
	      TextView vStatus = (TextView)dialog.findViewById(R.id.fristatus);
	      vLat.setText(m_latText);
	      vLng.setText(m_lngText);
	      vStatus.setText(m_status);
	      Button btn=(Button)dialog.findViewById(R.id.ch_commit);
	      edt=(EditText)dialog.findViewById(R.id.ch_edt);
	      btn.setOnClickListener(new Button.OnClickListener(){
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					String msg=edt.getText().toString();
					if(msg==null||"".equals(msg)||"发送信息".equals(msg)||m_friName.equals(UserManager.getGlobaluser().getUsername()))
						return;
					System.out.println("message has been sent to "+m_friName);
					UserManager.getInstance().SendMsg(m_friName, msg);
					msg ="你说："+msg;
					//加入当前会话
					if(UserManager.CurSessionFriList.containsKey(m_friName))
					    UserManager.CurSessionFriList.get(m_friName).add(msg);
					else{
						List<String> newmsgbox = new ArrayList<String>();
						newmsgbox.add(msg);
						UserManager.CurSessionFriList.put(m_friName, newmsgbox);
					}
					Toast.makeText(CustomItemizedOverlay.this.context, "信息已发送", Toast.LENGTH_SHORT).show();
					edt.setText("");
					android.os.Handler hander= new android.os.Handler();
					//设定定时器并在设定时间后使对话框关闭
					    hander.postDelayed(new Runnable() {     
					     @Override
					     public void run() {
					      dialog.dismiss();
					     }
					    }, 2 *1000);
				}		      
		      });
	      dialog.show();
	      return true;
	   }
	   
	   public void addOverlay(OverlayItem overlay) {
	      mapOverlays.add(overlay);
	       this.populate();
	   }
	   
	}
