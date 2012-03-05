package com.imps.ui;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.imps.R;
/**
 * Just for a better look of the dialog
 * @author liwenhaosuper
 *
 */
public class BubbleDialog extends Dialog{

	private View bubbleView;
	private TextView tvBubContent;
	private TextView tvCancel;
	private TextView tvOK;
	public BubbleDialog(Context context) {
		super(context,R.style.bubble_dialog);
		
	}
	public void build(String content,String OKStr){
		bubbleView = getLayoutInflater().inflate(R.layout.bubbledialog, null);
		tvOK = (TextView)bubbleView.findViewById(R.id.bubble_ok);
		tvBubContent = (TextView)bubbleView.findViewById(R.id.bubble_text);
		tvBubContent.setText(content);
		tvOK.setText(Html.fromHtml("<u>"+OKStr+"</u>"));
		tvOK.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				BubbleDialog.this.cancel();
			}			
		});
		this.setContentView(bubbleView);
	}
	public void build(String content,String OKStr,String CancelStr,View.OnClickListener OK,View.OnClickListener Cancel){
		bubbleView = getLayoutInflater().inflate(R.layout.bubbledialog, null);
		tvOK = (TextView)bubbleView.findViewById(R.id.bubble_ok);
		tvCancel = (TextView)bubbleView.findViewById(R.id.bubble_cancel);
		tvBubContent = (TextView)bubbleView.findViewById(R.id.bubble_text);
		tvBubContent.setText(content);
		tvOK.setText(Html.fromHtml("<u>"+OKStr+"</u>"));
		tvCancel.setText(Html.fromHtml("<u>"+CancelStr+"</u>"));
		tvCancel.setVisibility(View.VISIBLE);
		if(Cancel==null){
			tvCancel.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					BubbleDialog.this.cancel();
				}			
			});
		}else{
			tvCancel.setOnClickListener(Cancel);
		}
		if(OK==null){
			tvOK.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					BubbleDialog.this.cancel();
				}			
			});
		}else{
			tvOK.setOnClickListener(OK);
		}
		this.setContentView(bubbleView);
	}
}
