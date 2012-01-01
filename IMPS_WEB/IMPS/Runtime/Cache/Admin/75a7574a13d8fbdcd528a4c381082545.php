<?php if (!defined('THINK_PATH')) exit();?><!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" dir="ltr" lang="en">
  <head>
    <!-- <link type="text/css" rel="stylesheet" href="/IMPS/Tpl/default/common/css/admin-login.css" /> -->
    <link type="text/css" rel="stylesheet" href="/IMPS/Tpl/default/common/css/admin-login.css" />
  </head>
  <body>
    <div id="admin-login">
      <h3>管理员登陆入口</h3>
      <form name="admin-login-form" id="login-form" method="post" action="__URL__/doLogin">
	<p>
	  <label for="admin-name">用户名</label>
	  <br />
	  <input type="text" name="admin-name" id="admin-name" class="input" placeholder="输入用户名" />
	</p>
	<p>
	  <label for="admin-pswd">密码</label>
	  <br />
	  <input type="password" name="admin-pswd" id="admin-pswd" class="input" placeholder="输入密码"/>
	</p>
	<p>
	  <input type="submit" name="admin-submit" id="admin-submit" value="登录" />
	</p>
      </form>
    </div>
  </body>
</html>