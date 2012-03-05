<?php if (!defined('THINK_PATH')) exit();?> 
 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
           "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>相册</title>
    <link href="/IMPS/Tpl/default/common/css/frame.css" rel="stylesheet" type="text/css" />
     <link href="/IMPS/Tpl/default/common/css/home-main.css" rel="stylesheet" type="text/css" />
    <link href="/IMPS/Tpl/default/common/css/page.css" rel="stylesheet" type="text/css" />
<!--      <script type="text/javascript" src="/IMPS/Tpl/default/common/js/jquery.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/common.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/jquery.form.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/tbox/box.js"></script> -->
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/album/jquery.min.js"></script>
    <link rel="stylesheet" type="text/css" href="/IMPS/Tpl/default/common/js/album/msgbox/zxxbox.css" />
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/album/msgbox/jquery.zxxbox-nocss.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/album/jquery.custom.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/album/jquery.akeditable.js"></script>
   
  </head>
  <body>
      <script type="text/javascript">
  	function removealbum(vid){
	    $.post('__URL__/delalbum/',{id:vid},function(txt){
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
            <td id="frame-left" style="width:1000px;">
            <ul id="tabMenu" style="">
            <input type="button" name="index" value="照片首页" class="formbtn" onclick="javascript:window.location='<?php echo U('album/index');?>'" />
 			<input type="button" name="wall" value="照片墙" class="formbtn" onclick="javascript:window.location='<?php echo U('album/albumwall');?>'" />
			</ul>
               
			<form name="Album" id="Album" method="post" action="<?php echo U('album/newalbum');?>">
			<h3>新增相册:<input type="text" name="album_name" size="30" maxlength="20" class="inputtxt_mid" id="album_name" />
			<input type="submit" name="submit" value="提交" class="formbtn" />
			</h3>
			</form>
			<hr />
			<div class="clr"></div>
			
			<ul class="thumb" style="align:float;">
			<?php if(is_array($albumlist)): $i = 0; $__LIST__ = $albumlist;if( count($__LIST__)==0 ) : echo "" ;else: foreach($__LIST__ as $key=>$vo): ++$i;$mod = ($i % 2 )?><li id="list<?php echo ($vo["id"]); ?>">
			<a href="<?php echo U('album/listphoto',array('id'=>$vo['id']));?>">
			<?php if(empty($vo['cover'])): ?><img src="/IMPS/Tpl/default/common/images/Home/album/nocover.jpg" alt="<?php echo ($vo["album_name"]); ?>" />
			<?php else: ?>
			<img src="/IMPS/Uploads/album/s_<?php echo ($vo["cover"]); ?>" alt="<?php echo ($vo["album_name"]); ?>" /><?php endif; ?>
			</a>
			<p><span id="clname<?php echo ($vo["id"]); ?>"><?php echo ($vo["album_name"]); ?></span><BR />相片数:<?php echo ($vo["imgnum"]); ?><BR />点击:<?php echo ($vo["hits"]); ?><BR /><a href="javascript:;" onclick="akedit('<?php echo U('album/editalbumname',array('id'=>$vo['id'],'oldname'=>$vo['album_name']));?>','clname<?php echo ($vo["id"]); ?>',{
					        type : 'text',
							name : 'album_name',
			                cssname:'inputtxt_short',
			                btncss:'eformbtn',
				            submit:'修改'
							});">修改</a> | <a href="javascript:;" onclick="removealbum(<?php echo ($vo["id"]); ?>);">删除</a></p>
			</li><?php endforeach; endif; else: echo "" ;endif; ?>
			</ul>
			<div class="clr"></div>
               
               
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