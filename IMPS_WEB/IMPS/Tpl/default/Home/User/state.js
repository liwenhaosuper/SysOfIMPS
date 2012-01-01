function callback(fun,argum){
	fun(argum);
}

// 删除类型框
function delTypeBox(){
	$('input[name="publish_type"]').val( 0 );
	$('.talkPop').remove();
}

$(document).ready(function(){
	  // 评论切换
	  $("a[rel='comment']").live('click',function(){
	      var id = $(this).attr('minid');
	      var $comment_list = $("#comment_list_"+id);
		  if( $comment_list.html() == '' ){
			  $comment_list.html('<div class="feed_quote feed_wb" style="text-align:center"><img src="IMPS/Tpl/default/common/images/icon_waiting.gif" width="15"></div>');
			$.post( U("state/Index/loadcomment"),{id:id},function(txt){
				$comment_list.html( txt ) ;
			});
		  }else{
			  $comment_list.html('');
		  }
	  });


	// 发布评论
	$("form[rel='miniblog_comment']").live("submit", function(){
		var _this = $(this);
		var callbackfun = _this.attr('callback');
		var _comment_content = _this.find("textarea[name='comment_content']");
		if( _comment_content.val()=='' ){
			ui.error('内容不能为空');
			return false;
		}
		_this.find("input[type='submit']").val( '评论中...').attr('disabled','true') ;
		var options = {
		    success: function(txt) {
				txt = eval('('+txt+')');
				_this.find("input[type='submit']").val( '确定');
			       _this.find("input[type='submit']").removeAttr('disabled') ;
				   _comment_content.val('');
				if(callbackfun){
					callback(eval(callbackfun),txt);
				}else{
					_comment_content.css('height','');
			       $("#comment_list_before_"+txt.data['weibo_id']).after( txt.html );

				   $("#replyid_" + txt.data['weibo_id'] ).val('');
				   //更新评论数
				   $("a[rel='comment'][minid='"+txt.data['weibo_id']+"']").html("评论("+txt.data['comment']+")");
				 //  _this.find("textarea[name='comment_content']").focus();
				   
				}
		    }
		};
		_this.ajaxSubmit( options );
	    return false;
	});
});

state = function(){
	
}
state.prototype = {
	//初始化微博发布
	init:function(option){
		var __THEME__ = '/IMPS/Tpl/default/common;?>';
		var Interval;

		$("#publish_type_content_before").html("" +
				"<span>添加：</span><a href=\"javascript:void(0)\"" +
				" target_set=\"content_publish\" onclick=\"ui.emotions(this)\" " +
				"class=\"a52\"><img class=\"icon_add_face_d\" " +
				"src=\""+__THEME__+"/images/zw_img.gif\" />表情</a> " +
				"<a href=\"javascript:void(0)\" onclick=\"state.plugin.image.click(169)\"" +
				"class=\"a52\"><img class=\"icon_add_img_d\" " +
				"src=\""+__THEME__+"/images/zw_img.gif\" />图片</a> " +
				"<a href=\"javascript:void(0)\" onclick=\"state.plugin.video.click(221)\" " +
				"class=\"a52\"><img class=\"icon_add_video_d\" " +
				"src=\""+__THEME__+"/images/zw_img.gif\" />视频</a> " +
				"<a href=\"javascript:void(0)\" onclick=\"state.plugin.music.click(271)\" " +
				"class=\"a52\"><img class=\"icon_add_music_d\" " +
				"src=\""+__THEME__+"/images/zw_img.gif\" />语音/音乐</a>");

		$("#content_publish").keypress(function(event){
			var key = event.keyCode?event.keyCode:event.which?event.which:event.charCode;
	        if (key == 27) {
	        	clearInterval(Interval);
	        }
			state.checkInputLength(this,140);
		}).blur(function(){
			clearInterval(Interval);
			state.checkInputLength(this,140);
		}).focus(function(){
			//字数监控
			clearInterval(Interval);
		    Interval = setInterval(function(){
		    	state.checkInputLength('#content_publish',140);
			},300);
		});
		state.checkInputLength('#content_publish',140);
		shortcut('ctrl+return',	function(){state.do_publish();clearInterval(Interval);},{'target':'miniblog_publish'});
	},
	//发布前的检测
	before_publish:function(){
		
		if( $.trim( $('#content_publish').val() ) == '' ){
            ui.error('内容不能为空');		
			return false;
		}
		return true;
	},
	//发布操作
	do_publish:function(){
		if( state.before_publish() ){
			state.textareaStatus('sending');
			var options = {
			    success: function(txt) {
			      if(txt){
			    	   state.after_publish(txt);
			      }else{
	                  alert( '发布失败' );
			      }
				}
			};		
			$('#miniblog_publish').ajaxSubmit( options );
		    return false;
		}
	},
	//发布后的处理
	after_publish:function(txt){
		if(txt==0) {
			ui.success('您发布的微博含有敏感词，请等待审核！');
		}else {
			delTypeBox();
		    $("#feed_list").prepend( txt ).slideDown('slow');
		    var sina_sync = $('#sina_sync').attr('checked');
		    $('#miniblog_publish').clearForm();
		    if (sina_sync) {
		    	$('#sina_sync').attr('checked', true);
		    }
		    state.upCount('state');
		    ui.success('微博发布成功');
		    state.checkInputLength('#content_publish',140);
		}
	},
	//发布按钮状态
	textareaStatus:function(type){
		var obj = $('#publish_handle');
		if(type=='on'){
			obj.removeAttr('disabled').attr('class','btn_big hand');
		//}else if( type=='sending'){
		//	obj.attr('disabled','true').attr('class','btn_big_disable hand');
		}else{
			obj.attr('disabled','true').attr('class','btn_big_disable hand');
		}
	},
	//删除一条微博
	deleted:function(weibo_id){
		$.post(U("state/Operate/delete"),{id:weibo_id},function(txt){
			if( txt ){
				$("#list_li_"+weibo_id).slideUp('fast');
				state.downCount('state');
			}else{
				alert('删除失败');
			}
		});
	},
	//收藏
	favorite:function(id,o){
		$.post( U("state/Operate/stow") ,{id:id},function(txt){
			if( txt ){
				$(o).wrap('<span id=content_'+id+'></span>');
				$('#content_'+id).html('已收藏');
			}else{
				alert('收藏失败');
			}
		});
	},
	//取消收藏
	unFavorite:function(id,o){
		$.post( U("state/Operate/unstow") ,{id:id},function(txt){
			if( txt ){
				$('#list_li_'+id).slideUp('slow');
			}else{
				alert('取消失败');
			}
		});
	},
	//转发
	transpond:function(id,upcontent){
		upcontent = ( upcontent == undefined ) ? 1 : 0;
		ui.box.load( U("state/operate/transpond",["id="+id,"upcontent="+upcontent] ),{title:'转发',closeable:true});
	},
	//关注话题
	followTopic:function(name){
		$.post(U('state/operate/followtopic'),{name:name},function(txt){
			txt = eval( '(' + txt + ')' );
			if(txt.code==12){
				$('#followTopic').html('<a href="javascript:void(0)" onclick="state.unfollowTopic(\''+txt.topicId+'\',\''+name+'\')">取消该话题</a>');
			}
		});
	},
	unfollowTopic:function(id,name){
		$.post(U('state/operate/unfollowtopic'),{topicId:id},function(txt){
			if(txt=='01'){
				$('#followTopic').html('<a href="javascript:void(0)" onclick="state.followTopic(\''+name+'\')">关注该话题</a>');
			}
		});	
	},
	quickpublish:function(text){
		$.post(U('state/operate/quickpublish'),{text:text},function(txt){
			ui.box.show(txt,{title:'说几句',closeable:true});
		});
	},
	//更新计数器
	upCount:function(type){
		if(type=='state'){
			$("#miniblog_count").html( parseInt($('#miniblog_count').html())+1 );
		}
	},
	downCount:function(type){
		if(type=='state'){
			$("#miniblog_count").html( parseInt($('#miniblog_count').html())-1 );
		}
	},
	//检查字数输入
	checkInputLength:function(obj,num){
		var len = getLength($(obj).val(), true);
		var wordNumObj = $('.wordNum');
		
		if(len==0){
			wordNumObj.css('color','').html('你还可以输入<strong id="strconunt">'+ (num-len) + '</strong>字');
			state.textareaStatus('off');
		}else if( len > num ){
			wordNumObj.css('color','red').html('已超出<strong id="strconunt">'+ (len-num) +'</strong>字');
			state.textareaStatus('off');
		}else if( len <= num ){
			wordNumObj.css('color','').html('你还可以输入<strong id="strconunt">'+ (num-len) + '</strong>字');
			state.textareaStatus('on');
		}
	},
	publish_type_box:function(type_num,content,mg_left){
		var __THEME__ = "<?php echo '/IMPS/Tpl/default/common';?>";
		var html = '<div class="talkPop"><div  style="position: relative; height: 7px; line-height: 3px;">'
		     + '<img class="talkPop_arrow" style="margin-left:'+ mg_left +'px;position:absolute;" src="'+__THEME__+'/images/zw_img.gif" /></div>'
             + '<div class="talkPop_box">'
			 + '<div class="close" id="weibo_close_handle"><a href="javascript:void(0)" class="del" onclick=" delTypeBox()" > </a></div>'
			 + '<div id="publish_type_content">'+content+'</div>'
			 + '</div></div>';
		$('input[name="publish_type"]').val( type_num );
		$('div.talkPop').remove();
		$("#publish_type_content_before").after( html );
	}
}

state = new state();

state.plugin = {};

