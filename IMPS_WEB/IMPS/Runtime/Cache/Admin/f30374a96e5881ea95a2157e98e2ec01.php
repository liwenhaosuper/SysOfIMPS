<?php if (!defined('THINK_PATH')) exit();?>﻿﻿<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>IMPS管理平台</title>
<link rel="stylesheet" type="text/css" href="/IMPS/Tpl/default/Admin/Common/css/style.css" />
<script type="text/javascript" src="/IMPS/Tpl/default/Admin/Common/js/Base.js"></script>
<script type="text/javascript" src="/IMPS/Tpl/default/Admin/Common/js/prototype.js"></script>
<script type="text/javascript" src="/IMPS/Tpl/default/Admin/Common/js/mootools.js"></script>
<script type="text/javascript" src="/IMPS/Tpl/default/Admin/Common/js/Ajax/ThinkAjax.js"></script>
<script type="text/javascript" src="/IMPS/Tpl/default/Admin/Common/js/Form/CheckForm.js"></script>
<script type="text/javascript" src="/IMPS/Tpl/default/Admin/Common/js/common.js"></script>
<script type="text/javascript" src="/IMPS/Tpl/default/Admin/Common/js/Util/ImageLoader.js"></script>
<script language="JavaScript">
<!--
//指定当前组模块URL地址 
var URL = '.';
var APP	 =	 '.';
var PUBLIC = '.';
ThinkAjax.image = ['/IMPS/Tpl/default/Admin/Common/images/loading2.gif', '/IMPS/Tpl/default/Admin/Common/images/ok.gif','/IMPS/Tpl/default/Admin/Common/images/update.gif' ]
ImageLoader.add("/IMPS/Tpl/default/Admin/Common/images/bgline.gif","/IMPS/Tpl/default/Admin/Common/images/bgcolor.gif","/IMPS/Tpl/default/Admin/Common/images/titlebg.gif");
ImageLoader.startLoad();
//-->
</script>
</head>

<body onload="loadBar(0)">
<div id="loader" >页面加载中...</div>
<div id="main" class="main" >
<div class="content">
<div id="result" class="result none"></div>
<div class="title">编辑资料</div>
<div class="fLeft" style="width:90%;float:left">
<form method='post'  id="form1" >
<table cellpadding=3 cellspacing=3>
<tr>
	<td class="tRight" >昵称：</td>
	<td class="tLeft" ><input type="text" class="medium bLeft"  name="nickname" value="<?php echo ($vo["nickname"]); ?>"></td>
</tr>
<tr>
	<td class="tRight">Email：</td>
	<td class="tLeft"><input type="text" class="large bLeft"  name="email" value="<?php echo ($vo["email"]); ?>"></td>
</tr>
<tr>
	<td class="tRight tTop">备  注：</td>
	<td class="tLeft"><TEXTAREA class="large bLeft"  name="remark" rows="5" cols="57"><?php echo ($vo["remark"]); ?></textarea></td>
</tr>
<tr>
	<td></td>
	<td class="center">
	<input type="hidden" name="id" value="<?php echo ($vo["id"]); ?>">
	<input type="hidden" name="ajax" value="1">
	<input type="button" value="保 存" onclick="sendForm('form1','__URL__/change/')" class="submit small">
	<input type="reset" class="small submit hMargin" value="清 空" >
	</td>
</tr>
</table>
</form>
</div>
</div>
</div>