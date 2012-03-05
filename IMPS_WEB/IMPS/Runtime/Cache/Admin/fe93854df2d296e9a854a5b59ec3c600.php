<?php if (!defined('THINK_PATH')) exit();?>﻿<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
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
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/jquery.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/common.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/jquery.form.js"></script>
<script type="text/javascript">
     $(function(){
    	$('#updateUserForm').ajaxForm({
            beforeSubmit: checkStatusForm,
            success:      completeUpdateStatus,
            dataType:    'json'
    	});
        function checkStatusForm(){
            $('#result').html('').show();
            if(''==$.trim($('#username').val())||''==$.trim($('#email').val())){
                    $('#result').html('不允许空内容哦！').show();
                    return false;
            }
        }
        function completeUpdateStatus(data){
        	if(data.status==1){
        		$('#result').html('更新成功').show();
        	}else{
        		$('#result').html('更新失败'+data.info).show();
        	}
        	
        }
    }); 
</script>
<script language="JavaScript">
<!--
function resetPwd(){
    if(document.getElementById('resetPwd').value=="")
    {
         alert("密码不能为空");
         return false;
    }
    var id = document.getElementById('userid').value;
    var pwd = document.getElementById('resetPwd').value;
    $.post('__URL__/resetPwd/',{userid:id,password:pwd},function(txt){
    	if(txt.status==1){
    		$('#result').html('密码更新成功').show();
    	}else{
    		$('#result').html('密码更新失败').show();
    	}},'json');
}
//-->
</script>
<div class="content">
<div class="title">编辑帐号 [ <a href="__URL__/index">返回列表</a> ]</div>
<table cellpadding=3 cellspacing=3>
<tr>
	<td class="tRight">重置密码：</td>
	<td class="tLeft" ><input type="text" name="resetPwd" id="resetPwd" > <INPUT type="button" value="重置密码" onclick="resetPwd()" class="submit" style="height:25px"></td>
</tr>
</table>
<form method='post' id="updateUserForm" action="__URL__/update">
<table cellpadding=3 cellspacing=3>
<tr>
	<td class="tRight" >用户名：</td>
	<td class="tLeft" ><input type="text" class="medium bLeftRequire"  check='^\S+$' warning="用户名不能为空,且不能含有空格" name="username" id="username" value="<?php echo ($vo["username"]); ?>"></td>
</tr>
<tr>
	<td class="tRight" >邮箱：</td>
	<td class="tLeft" ><input type="text" class="medium bLeftRequire" id="email" name="email" check="^\S+@\S+$" warning="邮箱不能为空,且必须符合格式" value="<?php echo ($vo["email"]); ?>"></td>
</tr>
<tr>
	<td class="tRight" >性别：</td>
	<td class="tLeft" >
          女<input type="radio" class="small bLeft" id="gender" name="gender" <?php if($vo['gender']==0){ echo "checked";} ?> value="0" />
	男<input type="radio" class="small bLeft" id="gender" name="gender" <?php if($vo['gender']==1){ echo "checked";} ?> value="1" />
	</td>
</tr>

<tr>
	<td class="tRight">状态：</td>
	<td class="tLeft"><SELECT class="medium bLeft" id="status" name="status">
	<option <?php if(($vo["status"])  ==  "1"): ?>selected<?php endif; ?> value="1">已验证</option>
	<option <?php if(($vo["status"])  ==  "0"): ?>selected<?php endif; ?> value="0">尚未验证</option>
	</SELECT></td>
</tr>
<div class="result" id="result"></div>
<tr>
	<td></td>
	<td class="center"><input type="hidden" name="userid" id="userid" value="<?php echo ($vo["userid"]); ?>" >
	<input type="hidden" name="ajax" value="1">
	<input type="submit" value="保 存"  class="small submit">
	<input type="reset" class="submit  small" value="清 空" >
	</td>
</tr>
</table>
</form>
</div>
</div>