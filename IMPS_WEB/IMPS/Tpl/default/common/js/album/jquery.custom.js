/*
'--- Author:350AM
'--- Mail: 350am@163.com
'--- Date: 2011-10
*/

$(document).ready(function() {
	var d=300;
	$('#navigation a').each(function(){
		$(this).stop().animate({
			'marginTop':'-80px'
		},d+=150);
	});

	$('#navigation > li').hover(
	function () {
		$('a',$(this)).stop().animate({
			'marginTop':'-2px'
		},200);
	},
	function () {
		$('a',$(this)).stop().animate({
			'marginTop':'-80px'
		},200);
	}
);
});

function testKey(o,minnum,maxnum,defaultkey)
{
	var ptn = /^-?\d*$/        
	if (!ptn.exec(o.value)){
		alert('输入错误,请填入数字!');
		o.value=defaultkey;
	}
	if(o.value<minnum || o.value>maxnum){
		alert('请正确填写数值,最小分值为'+minnum+'最大值为:'+maxnum);
		o.value=defaultkey;
	}
	 
}

function JDialog(echotxt,ptype,cb){
    var ntype=parseInt(ptype)
	//alert(cb);
	if(ntype==1){
		$.zxxbox.remind(msgcm+echotxt, null, { 
		fix: true,
		width:"380",
		onclose: function(){ 
		      if(cb==0){
			   $.zxxbox.hide();
			  }else if(cb==1){
			   window.location.reload();
			  }else{
			   window.location=cb;
			  }
			}
		});
		
	}else if(ntype==2){
	  $.zxxbox.remind(msger+echotxt, null, {width:"380"});
	}else if(ntype==3){
	  $.zxxbox.remind(msgcm+echotxt, null, {width:"380",delay: 5000});
	}else{
	  $.zxxbox.remind(msgwn+echotxt, null, {width:"380"});
	}
 }

function loadlayer(){
 //创建Loading层
  $.zxxbox.loading();
}

function loadlayer_close(){
 //半闭Loading层
 $.zxxbox.hide();
}

function ajaxpost(stype,gourl,dataType,formdata){
//AJAX提交表单
      loadlayer();
	  $.ajax({
				type: stype,
				url: gourl,  
				data: formdata,
				dataType: dataType, 
                cache: false,
				error: function(){
				   JDialog('Process Error...Please Retry',2);
		        }, 
				success: function(data){
				   if(data.status==1){
					  JDialog(data.info,1,data.data);
				   }else{
					  JDialog(data.info,data.status);
				   }
				}
            });
}

function removeData(id,gurl,alerttxt,delname){
//提示，确认后通过AJAX提交删除操作 返回json数据	
$.zxxbox.ask(msgqt+alerttxt, function(){
		loadlayer();$.post(gurl,{id:id}, function(data){
							 if(data.status==1){
							 JDialog(data.info,3);
							 $(delname).remove();
							}else{JDialog(data.info,2);}							
							},'json');
		
    }, null,{width:380});		
}