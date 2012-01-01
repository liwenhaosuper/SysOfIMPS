package com.imps.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.imps.R;

public class About extends Activity {

	private TextView aboutView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);
		aboutView = (TextView)findViewById(R.id.about_content);
		aboutView.setText(
				"				关于IMPS			\n" +
				"	IMPS是一款交友社区类与生活应用类紧密结合在一起的基于地理位置的实时互动通信应用。" +
				"IMPS用户能够基于位置服务进行基于CS模式的包括文字、图片、语音对讲机在内的聊天实时互" +
				"动和基于P2P模式的多媒体通讯（包括视频共享，语音视频通讯等），并能够使用手写与涂鸦分" +
				"享功能增加互动的趣味性。另外还能让用户利用移动设备上的多种定位手段来获取更丰富的位置" +
				"信息服务。\n" +
				"	IMPS构建了一个基于多协议（包括tcp,udp,rtp等）的多媒体传输基础架构服务，能够作为平台服" +
				"务为不同程序提供多媒体传输与处理服务。在媒体处理上实现了用PCMU编解码音频，用H264、H263" +
				"压缩率很高、在工业界得到广泛认可的算法编解码视频的功能，同时在服务通信并发进行了很大的优化。\n" +
				"	IMPS主要具有如下特点：\n" +
				"	1. 构建了一个基于多协议（包括tcp,udp,rtp等）的多媒体传输基础架构服务，能够作为平台服务为" +
				"不同程序提供多媒体传输与压缩处理服务。\n" +
				"   2. 实现了一个拓展型NIO服务通信框架，对通信并发进行了较多优化。\n" +
				"	3. 具备基于手机设备的多种自适应定位（GPS、基站定位、wifi定位）功能。\n" +
				"	4. 具备基于手机地理位置的包括文字、图片、语音、视频等在内的即时通信功能，融合了社区交友与生活" +
				"元素，实现趣味性互动。\n" +
				"	IMPS主要由利文浩完成，同时刘海波、贾云瀚、廖咏翔在项目中给予了大力的支持\n" +
				"	IMPS目前仍处在重要研发阶段，作者正在拓展交友社区部分内容，有关问题可发邮件至liwenhaosuper@126.com" +
				"中与作者交流\n\n"
				);		
		//aboutView.setTextColor(color.darker_gray);
	}

}
