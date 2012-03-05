jQuery.extend(state.plugin, {
	image:function(element, options){
	   
	    
	}
});

var stopUploadPic = 0;
jQuery.extend(state.plugin.image, {
	html:'<div id="upload_selectpic"><div class="btn_green" href="javascript:void(0);" >从电脑选择图片'+
	'<form action="'+U("Home/state/before_publish")+'" enctype="multipart/form-data" method="post" id="uploadpic">'+
	'<input type="hidden" name="plugin_id" value="1"><input type="file" hidefoucs="true" name="pic" onchange="state.plugin.image.upload(this)">'+
	'</form></div><div>仅支持JPG、GIF、PNG、JPEG图片文件，且文件小于2M</div></div><div class="alC pt10 pb10 f14px" id="upload_loading" style="display:none"><img src="'+ _THEME_+'/images/icon_waiting.gif" width="20" class="alM"> 正在上传中...<br /><a class="btn_w mt10" href="javascript:void(0)" onclick="$(\'div .talkPop\').remove();state.plugin.image.stopAjax();">取消上传</a></div>',
	click:function(options){
		if (1 != $('div.talkPop').data('type')) {
			state.publish_type_box(1,this.html,options);
		}
	},
	upload:function(o){
		var allowext = ['jpg','jpeg','gif','png'];
		var ext = /\.[^\.]+$/.exec( $(o).val() );
		ext = ext.toString().replace('.','');
		if( jQuery.inArray( ext.toLowerCase() , allowext )==-1 ){
			alert('只允许上传jpg、jpeg、gif、png格式的图片');
			return false;
		}
		state.textareaStatus('off');
		$('#upload_selectpic').hide();
		$('#upload_loading').show();
		$('#weibo_close_handle').hide();
		var options = {
			    success: function(txt) {
				if(stopUploadPic==1){
					return false;
				}
			      txt = eval( '(' + txt + ')' );
			      if(txt.boolen==1){
						var img = new Image;
						img.src = txt.picurl;
						img.onload = function(){
							if( this.width>100 || this.height>100 ){
								var style;
								if( this.height >  this.width ){
									style = "height:100px;width:"+this.width*(100/this.height)+"px";
								}else{
									style = "width:100px;height:"+this.height*(100/this.width)+"px";
								}
								
								var html = "<img src='"+txt.picurl+"' style='"+style+"'><input name='publish_type_data' type='hidden' style='width:86%' value="+txt.type_data+" />";
							}else{
								var html = "<img src='"+txt.picurl+"'><input name='publish_type_data' type='hidden' style='width:86%' value="+txt.type_data+" />";					}
							if($('#content_publish').val()==''){
								$('#content_publish').val('图片分享 ');
							}
							$("#publish_type_content").html( txt.file_name+'&nbsp;&nbsp;&nbsp;<a href="javascript:void(0)" onclick="$(\'div .talkPop\').remove();">删除图片</a><BR>'+html );
							$('div.talkPop').data('type', 1);
							$('#upload_loading').hide();
							$('#upload_selectpic').show();
							state.checkInputLength('#content_publish',140);
						};
								
				  }else{
					alert( txt.message );
					$('.talkPop').remove();
			      }
			    } 
			};
//			$('#publish_type_content').html('<div style="width:400px;text-align:center;height:150px;"><img src="__THEME__/images/icon_waiting.gif" width="30"></div>');
			var httpRespondHandle = $('#uploadpic').ajaxSubmit( options );
		    return false;
	},
	stopAjax:function(){
		stopUploadPic=1;
	}

});

//切换图片
function switchPic(id,type,picurl){
	if( type=='close' ){
		$("#pic_show_"+id).hide();
		$("#pic_mini_show_"+id).show();
	}else{
		
		if( $("#pic_show_"+id).find('.imgSmall').attr('src')==''){
			$("#pic_mini_show_"+id).find('.loadimg').show();
			var img = new Image;
			img.src = picurl+'?time='+new Date();
			img.onload = function(){
				if( this.width>460 ){
					$("#pic_show_"+id).find('.imgSmall').css('width','460px');
				}
				$("#pic_show_"+id).find('.imgSmall').attr('src',this.src);
				$("#pic_mini_show_"+id).find('.loadimg').hide();
				$("#pic_show_"+id).show();
				$("#pic_mini_show_"+id).hide();	
			};
		}else{
			$("#pic_show_"+id).show();
			$("#pic_mini_show_"+id).hide();	
		}
	}
}

//旋转图片
function revolving(id,type){
	var img = $("#pic_show_"+id).find('.imgSmall');
	img.rotate(type);
}


$.fn.rotate = function(p){

	var img = $(this)[0],
		n = img.getAttribute('step');
	// 保存图片大小数据
	if (!this.data('width') && !$(this).data('height')) {
		this.data('width', img.width);
		this.data('height', img.height);
	};
	this.data('maxWidth',img.getAttribute('maxWidth'))

	if(n == null) n = 0;
	if(p == 'left'){
		(n == 0)? n = 3 : n--;
	}else if(p == 'right'){
		(n == 3) ? n = 0 : n++;
	};
	img.setAttribute('step', n);

	// IE浏览器使用滤镜旋转
	if(document.all) {
		if(this.data('height')>this.data('maxWidth') && (n==1 || n==3) ){
			if(!this.data('zoomheight')){
				this.data('zoomwidth',this.data('maxWidth'));
				this.data('zoomheight',(this.data('maxWidth')/this.data('height'))*this.data('width'));
			}
			img.height = this.data('zoomwidth');
			img.width  = this.data('zoomheight');
			
		}else{
			img.height = this.data('height');
			img.width  = this.data('width');
		}
		
		img.style.filter = 'progid:DXImageTransform.Microsoft.BasicImage(rotation='+ n +')';
		// IE8高度设置
		if ($.browser.version == 8) {
			switch(n){
				case 0:
					this.parent().height('');
					//this.height(this.data('height'));
					break;
				case 1:
					this.parent().height(this.data('width') + 10);
					//this.height(this.data('width'));
					break;
				case 2:
					this.parent().height('');
					//this.height(this.data('height'));
					break;
				case 3:
					this.parent().height(this.data('width') + 10);
					//this.height(this.data('width'));
					break;
			};
		};
	// 对现代浏览器写入HTML5的元素进行旋转： canvas
	}else{
		var c = this.next('canvas')[0];
		if(this.next('canvas').length == 0){
			this.css({'visibility': 'hidden', 'position': 'absolute'});
			c = document.createElement('canvas');
			c.setAttribute('class', 'maxImg canvas');
			img.parentNode.appendChild(c);
		}
		var canvasContext = c.getContext('2d');
		switch(n) {
			default :
			case 0 :
				img.setAttribute('height',this.data('height'));
				img.setAttribute('width',this.data('width'));
				c.setAttribute('width', img.width);
				c.setAttribute('height', img.height);
				canvasContext.rotate(0 * Math.PI / 180);
				canvasContext.drawImage(img, 0, 0);
				break;
			case 1 :
				if(img.height>this.data('maxWidth') ){
					h = this.data('maxWidth');
					w = (this.data('maxWidth')/img.height)*img.width;
				}else{
					h = this.data('height');
					w = this.data('width');
				}
				c.setAttribute('width', h);
				c.setAttribute('height', w);
				canvasContext.rotate(90 * Math.PI / 180);
				canvasContext.drawImage(img, 0, -h, w ,h );
				break;
			case 2 :
				img.setAttribute('height',this.data('height'));
				img.setAttribute('width',this.data('width'));
				c.setAttribute('width', img.width);
				c.setAttribute('height', img.height);
				canvasContext.rotate(180 * Math.PI / 180);
				canvasContext.drawImage(img, -img.width, -img.height);
				break;
			case 3 :
				if(img.height>this.data('maxWidth') ){
					h = this.data('maxWidth');
					w = (this.data('maxWidth')/img.height)*img.width;
				}else{
					h = this.data('height');
					w = this.data('width');
				}
				c.setAttribute('width', h);
				c.setAttribute('height', w);
				canvasContext.rotate(270 * Math.PI / 180);
				canvasContext.drawImage(img, -w, 0,w,h);
				break;
		};
	};
};