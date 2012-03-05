<?php if (!defined('THINK_PATH')) exit();?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
          "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>活动</title>
    <link href="/IMPS/Tpl/default/common/css/frame.css" rel="stylesheet" type="text/css" />
    <link href="/IMPS/Tpl/default/common/css/home-main.css" rel="stylesheet" type="text/css" />
    <link href="/IMPS/Tpl/default/common/css/event.css" rel="stylesheet" type="text/css" />
    <link href="/IMPS/Tpl/default/common/js/tbox/box.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/jquery.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/common.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/jquery.form.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/tbox/box.js"></script>
    <script type="text/javascript">
      $(document).ready(function() {
          mouse_hover(".tab-menu ul li, .actv-item");
      });
    </script>
  </head>
  <body>
    <script type="text/javascript">
      
    </script>
    
    <div id="home-wrap">
      <div id="top">
        <div id="navigation">
          <div class="menu"><img src="/IMPS/Tpl/default/common/images/common/logo.png" style="padding-right: 20px;"/></div>
          <div class="menu"><a href="<?php echo U('Home/User/index');?>">首页</a></div>
          <div class="menu"><a href="<?php echo U('Home/User/index');?>">个人主页</a></div>
          <div class="menu"><a href="<?php echo U('Home/Friends/index');?>">好友</a></div>
          <div class="menu"><a href="<?php echo U('Home/Activity/index');?>">活动</a></div>
          <div class="menu"><a href="#">相册</a></div>
          <div class="menu"><a href="#">我的状态</a></div>
          <div class="menu"><a href="#">留言板</a></div> 
          <div class="menu" id="search-top">
            <div><input type="text" autocomplete="off" class="searchtextbox gray" name="textfield" value="搜索好友、活动" /></div>
            <div class="search_go"><a class="go" href="javascript:;">GO</a></div>
            <div class="menu" id="logout"><a href="<?php echo U('User/logout');?>">登出</a></div>          </div>
        </div>
      </div>
      <div style="margin: 0 auto; padding-top: 20px;">
        <table id="home-main">
          <tr>
            <td id="frame-left">
              <div class="content no_bg">
		<div class="main no_l">
		  <!-- 画布 begin  -->
		  <div class="mainbox">
		    
		    <div class="mainbox_C">
		      <div class="tab-menu">
			<!-- 切换标签 begin  -->
			<ul>
			  <li><a href="<?php echo U('/activity/index');?>" <?php echo (empty($_GET['order']) || $_GET['order']=='hot')?'class="on"':NULL; ?>><span>全部<?php echo ($ts['app']['app_alias']); ?></span></a></li>
			  <li><a href="<?php echo U('/activity/index', array('order' => 'hot'));?>" <?php echo (empty($_GET['order']) || $_GET['order']=='hot')?'class="on"':NULL; ?>><span>热门活动<?php echo ($ts['app']['app_alias']); ?></span></a></li>
			  <li><a href="<?php echo U('/activity/index', array('order' => 'new'));?>" <?php echo ($_GET['order']=='new')?'class="on"':NULL; ?>><span>最新<?php echo ($ts['app']['app_alias']); ?></span></a></li>
			  <li><a href="<?php echo U('/activity/index', array('order' => 'follow'));?>" <?php echo ($_GET['order']=='follow')?'class="on"':NULL; ?>><span>我关注的人的<?php echo ($ts['app']['app_alias']); ?></span></a></li>
			</ul>
		      </div>
		      <!-- 切换标签 end  -->
		      <div class="groupBox">

			<!-- 活动列表 begin  -->
			<div class="boxL mLR15">
			  <table id="actv-table">
			    <?php if(is_array($actvlist)): $i = 0; $__LIST__ = $actvlist;if( count($__LIST__)==0 ) : echo "" ;else: foreach($__LIST__ as $key=>$actv): ++$i;$mod = ($i % 2 )?><tr>
				<td>
				  <div class="actv-item">
				    <a class="actv-logo" href="<?php echo U('/activity/info',array('activityId'=>$actv['id']));?>"><img src="<?php echo ($actv['coverPath']); ?>" /></a>
				    <div class="actv-name"><a href="<?php echo U('/activity/info',array('activityId'=>$actv['id']));?>" ><?php echo ($actv['name']); ?></a><span>[<?php echo ($actv['type']); ?>]</span></div>
				    <div class="actv-time"><span><?php echo ($actv['eTime']); ?></span></div>
				    <div class="disp-status"><span><?php if ($actv['checked'] == 1) {echo "已审核";} else {echo "未审核";} ?></span></div>
				    <div class="disp-count"><?php echo ($actv['attentionCount']); ?>人参加</div>
				  </div>
				</td>
			      </tr><?php endforeach; endif; else: echo "" ;endif; ?>
			  </table>
			</div>
			<!-- 活动列表 end  -->
			<div class="c"></div>
		      </div>
		    </div>
		    <div class="c"></div>
		  </div>
		</div>
	      </div>
            </td>
            <td id="frame-right">
	      <div id="rp">
		<div id="addactvp"><a href="<?php echo U('/activity/create');?>">创建活动</a></div>
	      </div>
            </td>
          </tr>
        </table>
      </div>
    </div>
    
  </body>
</html>