<?php if (!defined('THINK_PATH')) exit();?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
          "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>活动详情</title>
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
          mouse_hover(".tab-menu ul li");
      });
    </script>
  </head>
  <body>
    <div id="home-wrap">
      <div id="top">
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
			  <li><a href="<?php echo U('/activity/index');?>">返回活动首页</a></li>
			</ul>
		      </div>
		      <!-- 切换标签 end  -->
		      <div class="groupBox">
			
			<!-- 活动列表 begin  -->
			<div class="boxL mLR15">
			  <div id="actv-wrap">
			    <div class="actv-desc" id="activityName"><?php echo ($spec['name']); ?></div>
			    <div id="actv-avatar"><img src="<?php echo ($spec['coverPath']); ?>" /></div>
			    <div id="actv-info-simp">
			      <div class="actv-desc" id="activitySponsor"><label class="pl">发起人：</label><a><?php echo ($spec['sponsor']); ?></a></div>
			      <div class="actv-desc" id="activityAddress"><label class="pl">地点：</label><?php echo ($spec['area']); ?> <?php echo ($spec['address']); ?></div>
			      <div class="actv-desc" id="activityType"><label class="pl">类型：</label><a><?php echo ($spec['type']); ?></a></div>
			      <div class="actv-desc" id="activitySTime"><label class="pl">开始时间：</label><span id="stime"><?php echo ($spec['sTime']); ?></span>
			      </div>
			      <div class="actv-desc" id="activityETime"><label class="pl">结束时间：</label><span id="etime"><?php echo ($spec['eTime']); ?></span></div>
			      <div class="actv-desc"><span id="attcount"><?php echo ($spec['attentionCount']); ?>人参加</span></div>

			      <br />
			      <div id="join-actv-btn"><span style="color:#f00"><?php if (isset($record)) echo "已参加"; else echo "未参加"?></span>&gt;<a href="<?php echo U('/activity/join', array('actvid'=>$spec['id']));?>"><?php if (isset($record)) echo "我要退出"; else echo "我要参加"?></a></div>
			    </div>
			    <div id="activityDesc">
			      <?php echo ($spec['explaination']); ?>
			    </div>
			  </div>
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