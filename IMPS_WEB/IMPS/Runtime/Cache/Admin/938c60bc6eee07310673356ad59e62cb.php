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
<div id="container" class="container" >
<ul class="content_box">
   <?php if(!empty($item)){ ?>
     <li class="actv-item" id="activity_<?php echo ($item["id"]); ?>">
       <div class="actv-avatar"><a href="#"><img src="<?php echo ($item["coverPath"]); ?>" /></a></div>
       <div class="actv-detail">
         <div class="actv-title"><a href="#"><?php echo ($item["name"]); ?></a></div>
         <div><span class="ele">开始时间：</span><span><?php echo ($item["sTime"]); ?></span></div>
         <div><span class="ele">结束时间：</span><span><?php echo ($item["eTime"]); ?></span></div>
         <div><span class="ele">活动分类：</span><span><?php echo ($item["type"]); ?></span></div>
         <div><span class="ele">截止报名时间：</span><span><?php echo ($item["signTime"]); ?></span></div>
         <div><span class="ele">活动地点：</span><span><?php echo ($item["area"]); ?> <?php echo ($item["address"]); ?></span></div>
         <div><span class="ele">发起方：</span><span><?php echo ($item["sponsor"]); ?></span></div>
         <div><span class="ele"><?php echo ($item["attentionCount"]); ?>人参加</span></div>
         <div><span class="ele">活动详情:</span></div>
         <div><span class="ele"><?php echo ($item["explaination"]); ?></span></div>
         </div>
         <div class="state">
         <?php if(date('Y-m-d H:i:s')>$item.eTime){ ?>
              <p>活动已结束</p>
              <p><a href="<?php echo U('__URL__/activityDetail/activityId/'.$item.id);?>">查看活动情况</a></p>
         <?php }else if(date('Y-m-d H:i:s')>$item.sTime){ ?>
              <p>活动正在进行</p>
              <p><a href="<?php echo U('__URL__/activityDetail/activityId/'.$item.id);?>">查看活动情况</a></p>
         <?php }else if(date('Y-m-d H:i:s')<$item.signTime){ ?>
         	  <p>活动正接受报名</p>
         	  <p><a href="<?php echo U('__URL__/activityDetail/activityId/'.$item.id);?>">我也参加</a></p>
         <?php }else{ ?>
              <p>活动尚未开放</p>
              <p><a href="<?php echo U('__URL__/activityDetail/activityId/'.$item.id);?>">查看活动情况</a></p>
         <?php } ?>
       </div>
     </li>
     <?php }else{ ?>
     <li><div class="detail"><?php echo ($data); ?></div></li>
     <?php } ?>
 </ul>
</div>