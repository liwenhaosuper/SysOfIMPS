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
<div id="container" class="container" >
<ul class="content_box">
   <?php if(is_array($actvlist)): $i = 0; $__LIST__ = $actvlist;if( count($__LIST__)==0 ) : echo "" ;else: foreach($__LIST__ as $key=>$vo): ++$i;$mod = ($i % 2 )?><li class="actv-item" id="activity_<?php echo ($vo['id']); ?>">
       <div class="actv-detail-simp">
         <div class="actv-title-simp"><a href="<?php echo U('/activity/activityDetail',array('activityId'=>$vo['id']));?>"><?php echo ($vo['name']); ?></a></div>
       <div class="actv-avatar-simp"><a href="<?php echo U('/activity/activityDetail',array('activityId'=>$vo['id']));?>"><img src="<?php echo ($vo['coverPath']); ?>" /></a></div>
<!--     <div><span class="ele">开始时间：</span><span><?php echo ($vo['sTime']); ?></span></div>
         <div><span class="ele">结束时间：</span><span><?php echo ($vo['eTime']); ?></span></div>
         <div><span class="ele">活动分类：</span><span><?php echo ($vo['type']); ?></span></div>
         <div><span class="ele">截止报名时间：</span><span><?php echo ($vo['signTime']); ?></span></div>
         <div><span class="ele">活动地点：</span><span><?php echo ($vo['area']); ?> <?php echo ($vo['address']); ?></span></div>
         <div><span class="ele">发起方：</span><span><?php echo ($vo['sponsor']); ?></span></div> -->
         <div><span class="ele"><?php echo ($vo['attentionCount']); ?>人参加</span></div>
         </div>
         <div class="state">
         <?php if(date('Y-m-d H:i:s')>$vo['eTime']){ ?>
              <div>活动已结束</div>
              <!-- <p><a href="<?php echo U('/activity/activityDetail',array('activityId'=>$vo['id']));?>">查看活动情况</a></p> -->
         <?php }else if(date('Y-m-d H:i:s')>$vo['sTime']){ ?>
              <p>活动正在进行</p>
              <p><a href="<?php echo U('/activity/activityDetail',array('activityId'=>$vo['id']));?>">查看活动情况</a></p>
         <?php }else if(date('Y-m-d H:i:s')<$vo['signTime']){ ?>
         	  <p>活动正接受报名</p>
         	  <p><a href="<?php echo U('/activity/activityDetail',array('activityId'=>$vo['id']));?>">我也参加</a></p>
         <?php }else{ ?>
              <div>活动尚未开放</div>
              <!-- <p><a href="<?php echo U('/activity/activityDetail',array('activityId'=>$vo['id']));?>">查看活动情况</a></p> -->
         <?php } ?>
       </div>
     </li><?php endforeach; endif; else: echo "" ;endif; ?>
 </ul>
</div>