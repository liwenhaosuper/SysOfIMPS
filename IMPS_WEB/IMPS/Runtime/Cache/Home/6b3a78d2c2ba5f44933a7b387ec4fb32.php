<?php if (!defined('THINK_PATH')) exit();?> 
 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
           "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>相册</title>
    <link href="/IMPS/Tpl/default/common/css/frame.css" rel="stylesheet" type="text/css" />
     <link href="/IMPS/Tpl/default/common/css/home-main.css" rel="stylesheet" type="text/css" />
    <link href="/IMPS/Tpl/default/common/css/page.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="/IMPS/Tpl/default/common/js/album/jquery.min.js"></script>
    <link rel="stylesheet" type="text/css" href="/IMPS/Tpl/default/common/js/album/msgbox/zxxbox.css" />
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/album/msgbox/jquery.zxxbox-nocss.js"></script>
  <script type="text/javascript" src="/IMPS/Tpl/default/common/js/album/jquery.custom.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/album/jquery.akeditable.js"></script>
    <link rel="stylesheet" type="text/css" href="/IMPS/Tpl/default/common/js/album/uploadify/uploadify.css" />
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/album/uploadify/swfobject.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/album/uploadify/jquery.uploadify.v2.1.4.min.js"></script>
    <link rel="stylesheet" type="text/css" href="/IMPS/Tpl/default/common/images/Home/album/css_pirobox/style/style.css" />
	<script type="text/javascript" src="/IMPS/Tpl/default/common/images/Home/album/css_pirobox/jquery.pirobox.js"></script>
    <script>
	$(document).ready(function() {
	  $('#file_upload').uploadify({
	  'uploader'    : '/IMPS/Tpl/default/common/js/album/uploadify/uploadify.swf',
	  'script'      : '<?php echo U('Album/uploadimgs',array('id'=>$albumarr['id'])); ?>',
	  'scriptData'  : {'upattsid' : '<?=session_id()?>','uploadifykey':'d909e88672fa5a5c58e819c211a101b4'},
	  'cancelImg'   : '/IMPS/Tpl/default/common/js/album/uploadify/cancel.png',
	  'folder'      : '/IMPS/Uploads/album/',
	  'fileExt'     : '*.jpg;*.gif;*.png;',
	  'fileDesc'    : '图片文件jpg,gif,png',
	  'queueSizeLimit' : 5,
	  'sizeLimit'   : 2048000,
	  'multi'       : true,
	  'onError'     : function (event,ID,fileObj,errorObj) {
	      alert(errorObj.type + ' Error: ' + errorObj.info);
	    },
	  'onAllComplete':function(event,data){
		  alert(data.filesUploaded + ' 个文件上传成功!');
		  window.location.reload();
		  }
	 }); 
	  $().piroBox({
			my_speed: 400, //animation speed
			bg_alpha: 0.3, //background opacity
			slideShow : true, // true == slideshow on, false == slideshow off
			slideSpeed : 4, //slideshow duration in seconds(3 to 6 Recommended)
			close_all : '.piro_close,.piro_overlay',// add class .piro_overlay(with comma)if you want overlay click close piroBox
			count_hit:true,
			count_hit_url:'<?php echo U('album/counthit');?>'

	});
	$(".loadImage").zxxbox({
	    bar: false,
	    bgclose:true
	}); 
	});
	function setcover(ppid,pid){
	    $.post('__URL__/setcover/',{pid:ppid,id:pid},function(txt){
	    	if(txt.status==1){
	    		window.location.reload();
	    		return true;
	    	}else{
	    		alert("设置失败");
	    		return false;
	    	}
	    	},'json');
	}
	function removephoto(vid){
	    $.post('__URL__/delimg/',{id:vid},function(txt){
	    	if(txt.status==1){
	    		window.location.reload();
	    		return true;
	    	}else{
	    		alert("删除失败");
	    		return false;
	    	}
	    	},'json');
	}
	</script>
  </head>
  <body>
  
    <div id="home-wrap">
      <div id="top" style="height:auto;">
        <div id="navigation">
          <div class="menu"><img src="/IMPS/Tpl/default/common/images/common/logo.png" style="padding-right: 20px;"/></div>
          <div class="menu"><a href="<?php echo U('Home/User/index');?>">首页</a></div>
          <div class="menu"><a href="#">个人主页</a></div>
          <div class="menu"><a href="#">好友</a></div>
          <div class="menu"><a href="<?php echo U('Home/Activity/index');?>">活动</a></div>
          <div class="menu"><a href="#">相册</a></div>
          <div class="menu"><a href="#">我的状态</a></div>
          <div class="menu"><a href="#">留言板</a></div> 
          <div class="menu" id="search-top">
            <div><input type="text" autocomplete="off" class="searchtextbox gray" name="textfield" value="搜索好友、活动" /></div>
            <div class="search_go"><a class="go" href="javascript:;">GO</a></div>
          </div>     
        </div>
      </div>
      <div style="margin: 0 auto; padding-top: 20px;">
        <table id="home-main">
            <tr>
            <td id="frame-left"  style="width:1000px;">
            
			<h3><a href="javascript:window.location='<?php echo U('album/index');?>'">相册列表</a>>><?php echo ($albumarr["album_name"]); ?>图片管理
			</h3>
			<hr />
			<div class="clr"></div>
			说明</b>：上传允许文件类型：gif png jpg 图像文件,最多可同时上传5个文件,每文件最大为2M</a>
			<div style="width:500px; border:1px solid #D0D0D0; padding:5px;margin:0 auto;">
			<input type="file" id="file_upload" name="file_upload" />
			<input type="button" class="formbtn" onclick="javascript:$('#file_upload').uploadifyUpload();" value="上传图片" />
			</div>
			<hr />
			<div class="clr"></div>
			
			<ul class="thumb" style="align:float;">
			<?php if(is_array($albumlist)): $i = 0; $__LIST__ = $albumlist;if( count($__LIST__)==0 ) : echo "" ;else: foreach($__LIST__ as $key=>$vo): ++$i;$mod = ($i % 2 )?><li id="list<?php echo ($vo["id"]); ?>"><?php if(($vo['iscover'])  ==  "1"): ?><div class="covertitle"></div><?php endif; ?>
			<a href="/IMPS/Uploads/album/b_<?php echo ($vo["pickey"]); ?>" class="pirobox_gall" id="<?php echo ($vo["id"]); ?>" title="<?php echo ($vo["name"]); ?>">
			<img src="/IMPS/Uploads/album/s_<?php echo ($vo["pickey"]); ?>" alt="<?php echo ($vo["name"]); ?>" />
			</a>
			<p><span id="clname<?php echo ($vo["id"]); ?>"><?php echo ($vo["name"]); ?></span><BR />点击:<?php echo ($vo["hits"]); ?><BR /><a href="javascript:;" onclick="akedit('<?php echo U('album/editpname',array('id'=>$vo['id'],'oldname'=>$vo['name']));?>','clname<?php echo ($vo["id"]); ?>',{
					        type : 'text',
							name : 'photo_name',
			                cssname:'inputtxt_short',
			                btncss:'eformbtn',
				            submit:'修改'
							});">修改</a>|<a href="javascript:;" onclick="removephoto(<?php echo ($vo["id"]); ?>);">删除</a>|
							<a href="javascript:void(0);" onclick="setcover(<?php echo ($vo["pid"]); ?>,<?php echo ($vo["id"]); ?>)" title="设置为相册封面图" id="cover">设为封面图</a></p>
			</li><?php endforeach; endif; else: echo "" ;endif; ?>
			</ul>
			
            
            </td>
            </tr>
            <tr>
            <td id="frame-right">
            </td>
            </tr>
        </table>
      </div>
  </div>
 </body>
</html>