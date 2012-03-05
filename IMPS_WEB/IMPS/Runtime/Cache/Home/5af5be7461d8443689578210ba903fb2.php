<?php if (!defined('THINK_PATH')) exit();?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
          "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>我的好友</title>
    <link href="/IMPS/Tpl/default/common/css/frame.css" rel="stylesheet" type="text/css" />
    <link href="/IMPS/Tpl/default/common/css/friends.css" rel="stylesheet" type="text/css" />
    <link href="/IMPS/Tpl/default/common/css/home-main.css" rel="stylesheet" type="text/css" />
    <link href="/IMPS/Tpl/default/common/css/frame.css" rel="stylesheet" type="text/css" />
    <link href="/IMPS/Tpl/default/common/css/home-main.css" rel="stylesheet" type="text/css" />
    <link href="/IMPS/Tpl/default/common/js/tbox/box.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/jquery.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/common.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/jquery.form.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/tbox/box.js"></script>
    <script type="text/javascript">
      $(document).ready(function() {
      $("#group ul li, .disp-item").mouseover(function() { $(this).addClass("over"); }).mouseout(function() { $(this).removeClass("over");});
      });
    </script>
  </head>
  <body>
    <div id="home-wrap">
      <div id="top" style="height:auto;">
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
            <div><input type="text" autocomplete="off" class="searchtextbox gray"                                                                                              name="textfield" value="搜索好友、活动" /></div>
            <div class="search_go"><a class="go" href="javascript:;">GO</a></div>
          </div> 
          <div class="menu" id="logout"><a href="<?php echo U('User/logout');?>">登出</a></div>    
        </div>
      </div>

      <div style="margin: 0 auto; padding-top: 20px;">
	<div id="friends-top">
	  <span>好友</span>
	</div>
        <table id="home-main">
          <tr>
            <td id="frame-left">
	      <table>
		<tr>
		  <td>
		    <div id="search-left">
		      <div><input type="text" autocomplete="off" class="searchtextbox gray" name="textfield" value="搜索好友" style="width:120px;"/></div>
		      <div id="group-search-friend"><a href="javascript:;">GO</a></div>
		    </div>
		    <div id="group">
		      <ul>
			<li><span>全部</span></li>
			<li><span>我关注的</span></li>
		      </ul>
		    </div>
		  </td>
		  <td>
		    <div id="disp-list">
		      <table>
			<tr><td>
			    <div class="disp-item">
			      <a class="avatar" href="#"><img src="/IMPS/Tpl/default/common/images/common/logo.png" />
			      </a>
			      <a class="name">someone</a>
			      <div class="disp-status"><span>hahahhahahaha</span></div>
			    </div>
			</td></tr>
			<tr><td>
			    <div class="disp-item">
			      <a class="avatar" href="#"><img src="/IMPS/Tpl/default/common/images/common/logo.png" />
			      </a>
			      <a class="name">someone</a>
			      <div class="disp-status"><span>hahahhahahaha</span></div>
			    </div>
			</td></tr>
			<tr><td>
			    <div class="disp-item">
			      <a class="avatar" href="#"><img src="/IMPS/Tpl/default/common/images/common/logo.png" />
			      </a>
			      <a class="name">someone</a>
			      <div class="disp-status"><span>hahahhahahaha</span></div>
			    </div>
			</td></tr>
			<tr><td>
			    <div class="disp-item">
			      <a class="avatar" href="#"><img src="/IMPS/Tpl/default/common/images/common/logo.png" />
			      </a>
			      <a class="name">someone</a>
			      <div class="disp-status"><span>hahahhahahaha</span></div>
			    </div>
			</td></tr>
			<tr><td>
			    <div class="disp-item">
			      <a class="avatar" href="#"><img src="/IMPS/Tpl/default/common/images/common/logo.png" />
			      </a>
			      <a class="name">someone</a>
			      <div class="disp-status"><span>hahahhahahaha</span></div>
			    </div>
			</td></tr>
			<tr><td>
			    <div class="disp-item">
			      <a class="avatar" href="#"><img src="/IMPS/Tpl/default/common/images/common/logo.png" />
			      </a>
			      <a class="name">someone</a>
			      <div class="disp-status"><span>hahahhahahaha</span></div>
			    </div>
			</td></tr>
		      </table>
		    </div>
		  </td>
		</tr>
	      </table>
	    </td>
	    <td id="frame-right">
	      <div>
		<p>dfadsf</p>
		<br />
		<p>adsfasdf</p>
	      </div>
	    </td>
	  </tr>
	</table>
      </div>
    </div>
  </body>
</html>