package com.imps.activities;

/* import相关class */
import java.util.List;

import com.imps.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/* 自定义的Adapter，继承android.widget.BaseAdapter */
public class MyAdapter extends BaseAdapter
{
  /* 变量声明 */
  private LayoutInflater mInflater;
  private List<String> items;
  private List<String> values;
 
  /* MyAdapter的构造器，传入三个参数  */  
  public MyAdapter(Context context,List<String> item,
                   List<String> value)
  {
    /* 参数初始化 */
    mInflater = LayoutInflater.from(context);
    items = item;
    values = value;
  
  }
  
  /* 因继承BaseAdapter，需覆盖以下方法 */
  
  public int getCount()
  {
    return items.size();
  }

  
  public Object getItem(int position)
  {
    return items.get(position);
  }
  
  
  public long getItemId(int position)
  {
    return position;
  }
  
  
  public View getView(int position,View convertView,ViewGroup par)
  {
    ViewHolder holder;
  
    if(convertView == null)
    {
      /* 使用自定义的file_row作为Layout */
      convertView = mInflater.inflate(R.layout.row_layout,null);
      /* 初始化holder的text与icon */
      holder = new ViewHolder();
      holder.text1=(TextView)convertView.findViewById(R.id.myText1);
      holder.text2=(TextView)convertView.findViewById(R.id.myText2);
    
      convertView.setTag(holder);
    }
    else
    {
      holder = (ViewHolder) convertView.getTag();
    }
    /* 设置要显示的信息 */
    holder.text1.setText(items.get(position).toString());
    holder.text2.setText(values.get(position).toString());
   
    return convertView;
  }
  
  /* class ViewHolder */
  private class ViewHolder
  {
    /* text1：信息名称
     * text2：信息内容 */
    TextView text1;
    TextView text2;
   
  }
}
