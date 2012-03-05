<?php if (!defined('THINK_PATH')) exit();?>
﻿<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
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
<script type="text/javascript" src="/IMPS/Tpl/default/common/js/jquery.js"></script>
<script language="javascript">
<!--
function checkName()
{   
	$('#result').html("").show();
	var name = document.getElementById('username').value;
    $.post('__URL__/checkUser/',{username:name},function(txt){
    	if(txt.status==1){
    		$('#result').html("用户名可以使用").show();
    		return true;
    	}else{
    		$('#result').html("用户名已存在").show();
    		return false;
    	}
    	},'json');
}

//-->
</script>
<div id="main" class="main" >
<div class="content">
<div class="title">新增用户 [ <a href="__URL__/index">返回列表</a> ]</div>
<table cellpadding=3 cellspacing=3>
<form method='post' id="postAddUser" action="__URL__/doAdd/" >
<tr>
	<td class="tRight" >用户名：</td>
	<td class="tLeft" ><input type="text" class="medium bLeftRequire"  check='Require' warning="用户名不能为空" id="username" name="username" >
	<input type="button" value="检测帐号" onclick="checkName()" class="submit hMargin"></td>
    <div class="result" id="result" ></div>
</tr>
<tr>
	<td class="tRight" >密码：</td>
	<td class="tLeft" ><input type="text" class="medium bLeft" name="password" id="password" value=""></td>
</tr>
<tr>
	<td class="tRight" >邮箱：</td>
	<td class="tLeft" ><input type="text" class="medium bLeft" name="email" id="email" value=""></td>
</tr>
<tr>
	<td class="tRight" >性别：</td>
	<td class="tLeft" >
          女<input type="radio" class="small bLeft" id="gender" name="gender" checked value="0" />
	男<input type="radio" class="small bLeft" id="gender" name="gender" value="1" />
	</td>
</tr>
<tr>
	<td class="tRight">账户状态：</td>
	<td class="tLeft"><SELECT class="medium bLeft"  name="status" id="status">
	<option value="1">启用</option>
	<option selected value="0">尚待验证</option>
	</SELECT></td>
</tr>

<tr>
	<td></td>
	<td class="center">
	<input type="submit" value="保 存" class="small submit" >
	<input type="reset" class="submit  small" value="清 空" >
	</td>
</tr>
</table>
</form>
</div>
</div>