<?php if (!defined('THINK_PATH')) exit();?>﻿<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>IMPS管理平台</title>
<link rel='stylesheet' type='text/css' href='/IMPS/Tpl/default/Admin/Common/css/style.css'>
<base target="main" />
</head>
<body>
<!-- 头部区域 -->
<div id="header" class="header">
<div class="headTitle" style="margin:8pt 10pt">IMPS管理平台 </div>
	<!-- 功能导航区 -->
	<div class="topmenu">
<ul>
<li><span><a href="#" onClick="sethighlight(0); parent.menu.location='__URL__/menu/title/后台首页';parent.main.location='__URL__/main/';return false;">后台首页</a></span></li>
<?php if(is_array($nodeGroupList)): $i = 0; $__LIST__ = $nodeGroupList;if( count($__LIST__)==0 ) : echo "" ;else: foreach($__LIST__ as $key=>$tag): ++$i;$mod = ($i % 2 )?><li><span><a href="#" onClick="sethighlight(<?php echo ($i); ?>); parent.menu.location='__URL__/menu/tag/<?php echo ($key); ?>/title/<?php echo ($tag); ?>';parent.main.location='__URL__/main/tag/<?php echo ($key); ?>';return false;"><?php echo ($tag); ?></a></span></li><?php endforeach; endif; else: echo "" ;endif; ?>
</ul>
</div>
	<div class="nav">
	欢迎你！admin
	<a href="__URL__/password/"><img src="/IMPS/Tpl/default/Admin/Common/images/checked_out.png" width="16" height="16" border="0" alt="" align="absmiddle"> 修改密码</a> <a href="__URL__/profile/"><img SRC="/IMPS/Tpl/default/Admin/Common/images/write.gif" WIDTH="17" HEIGHT="16" BORDER="0" ALT="" align="absmiddle"> 修改资料</a> <a href="__URL__/logout/" target="_top"><img SRC="/IMPS/Tpl/default/Admin/Common/images/error.gif" WIDTH="20" HEIGHT="20" BORDER="0" ALT="" align="absmiddle"> 退 出</a></div>
</div>
<script>
function sethighlight(n) {
	var lis = document.getElementsByTagName('span');
	for(var i = 0; i < lis.length; i++) {
		lis[i].className = '';
	}
	lis[n].className = 'current';
}
sethighlight(0);
</script>
</body>
</html>