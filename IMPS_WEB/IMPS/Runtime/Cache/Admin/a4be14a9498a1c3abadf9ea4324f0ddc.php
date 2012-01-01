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
<script  type="text/javascript" src="/IMPS/Tpl/default/common/js/rcalendar.js" ></script>
<script type="text/javascript" src="/IMPS/Tpl/default/common/js/UbbEditor.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/jquery.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/common.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/jquery.form.js"></script>
 <link rel="stylesheet" type="text/css" href="/IMPS/Tpl/default/common/css/jquery-ui.css" />
  <script type="text/javascript" src="/IMPS/Tpl/default/common/js/jquery-ui-1.8.13.custom.min.js"></script>
<script type="text/javascript" >
   function checkActivityForm(){
	   $('#nameCheck').html("").show();$('#areaCheck').html("").show();$('#typeCheck').html("").show();
	   $('#timeCheck').html("").show();$('#signinCheck').html("").show();$('#contantCheck').html("").show();
	   $('#sponsorCheck').html("").show();$('#contactCheck').html("").show();
	   if($('#activityName').val()==""){
		   $('#nameCheck').html("活动名称不能为空!").show();
		   return false;
	   }	   
	   if($('#activityArea').val()==""){
		   $('#areaCheck').html("活动城市不能为空!").show();
		   return false;
	   }
	   if($('#activityAddress').val()==""){
		   $('#addressCheck').html("活动详细地点不能为空!").show();
		   return false;
	   }
	   if($('#activityType').val()==-1){
		   $('#typeCheck').html("活动分类不能为空!").show();
		   return false;
	   }
	   if($('#sTime').val()==""||$('#eTime').val()==""){
		   $('#timeCheck').html("活动时间不能为空!").show();
		   return false;
	   }
	   if($('#signTime').val()==""){
		   $('#signinCheck').html("活动截止报名时间不能为空!").show();
		   return false;
	   }
	   if($('#sTime').val()>=$('#eTime').val()){
		   $('#timeCheck').html("活动开始时间不能晚于结束时间").show();
		   return false;
	   }
	   if($('#eTime').val()<$('#signTime').val()){
		   $('#signinCheck').html("活动报名时间不能晚于活动结束时间");
		   return false;
	   }
	   if($('#activityDetail').val()==""){
		   $('#contentCheck').html("活动内容不能为空!").show();
		   return false;
	   }
	   if($('#activitySponsor').val()==""){
		   $('#sponsorCheck').html("活动发起方不能为空!").show();
		   return false;
	   }
	   if($('#activityContact').val()==""){
		   $('#contactCheck').html("活动联系方式不能为空!").show();
		   return false;
	   }
	   return true;
   }
   function selectArea(){
	   $('#areaDialog').dialog({ width: 650});
	   var typevalue = $("#current").val();
	   var ajax = {
               url: '/IMPS/Admin/Area/area/selected='+typevalue, type: 'GET', dataType: 'html', cache: false, success: function(html) {
            	   $('#areaDialog').html(html).show();
               }
           };
	   $('#areaDialog').html('正在加载中...').show();
	   jQuery.ajax(ajax);
	}
</script>
<div id="main" class="main" >
<div class="content">
<div class="title">发起活动 [ <a href="__URL__/index">返回活动首页</a> ]</div>
<form action="__URL__/doAdd" method="post" onsubmit="return checkActivityForm()" enctype="multipart/form-data">
<table cellpadding=3 cellspacing=3>
   <tr>
     <td class="tRight" >活动名称：</td>
     <td class="tLeft" ><input type="text" class="medium bLeftRequire" id="activityName" name="activityName"/></td>
      <td class="result" id="nameCheck" style="color:#F63;"></td>
   </tr>
   <tr>
     <td class="tRight" >活动城市：</td>
     <td class="tLeft" ><input type="text" class="medium bLeftRequire" id="activityArea" name="activityArea" readonly/>
        <input type="button" class="submit hMargin" id="selectarea" name="selectarea" value="选择城市" onclick="selectArea()"/>
         <input type="hidden" id="current" name="current" />
         <div id="areaDialog" title="选择地区" style="width:auto;"></div>
     </td>
      <td class="result" id="areaCheck" style="color:#F63;"></td>
   </tr>
   <tr>
     <td class="tRight" >活动详细地点：</td>
     <td class="tLeft" ><input type="text" class="large bLeftRequire" id="activityAddress" name = "activityAddress" />
         <input type="button" class="submit hMargin" value="在地图上标记地点" />
     </td>
      <td class="result" id="addressCheck" style="color:#F63;"></td>
   </tr>
   <tr>
	 <td class="tRight">活动分类：</td>
	 <td class="tLeft"><SELECT class="large bLeft"  name="activityType" id="activityType">
	 <option selected value="-1">请选择分类</option>
	  <?php if(is_array($type)): $i = 0; $__LIST__ = $type;if( count($__LIST__)==0 ) : echo "" ;else: foreach($__LIST__ as $key=>$item): ++$i;$mod = ($i % 2 )?><option value="<?php echo ($item["id"]); ?>"><?php echo ($item["name"]); ?></option><?php endforeach; endif; else: echo "" ;endif; ?>
	    </SELECT>
	 </td>
	  <td class="result" id="typeCheck" style="color:#F63;"></td>
   </tr>
   <tr>
     <td class="tRight">活动时间:</td>
     <td class="tLeft">
        <input name="sTime" type="text" class="text" id="sTime" onfocus="rcalendar(this,'full');" readonly />
                      -
        <input name="eTime" type="text" class="text" id="eTime" onfocus="rcalendar(this,'full');" readonly />
     </td>
      <td class="result" id="timeCheck" style="color:#F63;"></td>
   </tr>
   <tr>
    <td class="rRight">截止报名时间：</td>
    <td class="tLeft">
      <input name="signTime" type="text" class="text" id="signTime" onfocus="rcalendar(this,'full');" readonly />
    </td>
     <td class="result" id="signinCheck" style="color:#F63;"></td>
   </tr>
   <tr>
     <td class="rRight">活动介绍：</td>
     <td>
         <script type="text/javascript" src="/IMPS/Tpl/default/common/js/KindEditor/kindeditor.js"></script><script type="text/javascript"> KE.show({ id : 'activityDetail'  ,urlType : "absolute"});</script><textarea id="activityDetail" style="width:550px;height:245px" name="activityDetail" ></textarea>
     </td>
     <td class="result" id="contentCheck" style="color:#F63;"></td>             
   </tr>
   <tr>
      <td class="rRight">活动发起单位：</td>
       <td class="tLeft" >
          <input type="text" class="large bLeftRequire" id="activitySponsor" name = "activitySponsor" />
      </td>
      <td class="result" id="sponsorCheck" style="color:#F63;"></td>
   </tr>
   <tr>
      <td class="rRight">联系方式：</td>
       <td class="tLeft" >
          <input type="text" class="large bLeft" id="activityContact" name = "activityContact" />
      </td>
       <td class="result" id="contactCheck" style="color:#F63;"></td>
   </tr>
   <tr>
     <td class="tRight" >活动海报：</td>
     <td class="tLeft">
         <input type="file"  name="cover" />
     </td>
      <td class="tLeft" id="file" name="file" style="color:#063;"></td>
   </tr>
   <tr>
     <td class="rRight">
       </td>
     <td class="tLeft">
     <input type="submit" class="submit hMargin" value="发起活动" />
     </td>
   </tr>
</table>
</form>
</div>
</div>