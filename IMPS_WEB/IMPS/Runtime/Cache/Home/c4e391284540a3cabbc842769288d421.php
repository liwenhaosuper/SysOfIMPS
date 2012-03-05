<?php if (!defined('THINK_PATH')) exit();?>﻿<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>IMPS 页面提示</title>
<meta http-equiv='Refresh' content='<?php echo ($waitSecond); ?>;URL=<?php echo ($jumpUrl); ?>'>
<link rel="stylesheet" type="text/css" href ="/IMPS/Tpl/default/common/css/pubsucess.css" />
</head>
<body> 		
            <?php if(($status)  ==  "1"): ?><div class="Prompt" style="height:auto;">
                 <div class="Prompt_top"></div>
                 <div class="Prompt_con">
                 <dl>
                 <dt>提示信息</dt>
                 <dd><span class="Prompt_ok"></span></dd>
                 <dd>
                 <h2><?php echo ($message); ?></h2>
                 <?php if(isset($closeWin)): ?><p>系统将在 <span style="color:blue;font-weight:bold"><?php echo ($waitSecond); ?></span> 秒后自动关闭，如果不想等待,直接点击 <A HREF="<?php echo ($jumpUrl); ?>">这里</A> 关闭</p><?php endif; ?>
                 <?php if(!isset($closeWin)): ?><p>系统将在 <span style="color:blue;font-weight:bold"><?php echo ($waitSecond); ?></span> 秒后自动跳转,如果不想等待,直接点击 <A HREF="<?php echo ($jumpUrl); ?>">这里</A> 跳转<br/>
                  或者 <a href="__ROOT__/">返回首页</a></p><?php endif; ?>
                </dd>
                </dl>
                  <div class="c"></div>
                </div>
                <div class="Prompt_btm"></div> 
                </div><?php endif; ?>
			
			    <?php if(($status)  ==  "0"): ?><div class="Prompt" style="height:auto;">
                 <div class="Prompt_top"></div>
                 <div class="Prompt_con">
                 <dl>
                 <dt>提示信息</dt>
                 <dd><span class="Prompt_x"></span></dd>
                 <dd>
                 <h2><?php echo ($message); ?></h2>
                 <?php if(isset($closeWin)): ?><p>系统将在 <span style="color:blue;font-weight:bold"><?php echo ($waitSecond); ?></span> 秒后自动关闭，如果不想等待,直接点击 <A HREF="<?php echo ($jumpUrl); ?>">这里</A> 关闭</p><?php endif; ?>
                 <?php if(!isset($closeWin)): ?><p>系统将在 <span style="color:blue;font-weight:bold"><?php echo ($waitSecond); ?></span> 秒后自动跳转,如果不想等待,直接点击 <A HREF="<?php echo ($jumpUrl); ?>">这里</A> 跳转<br/>
                  或者 <a href="__ROOT__/">返回首页</a></p><?php endif; ?>
                </dd>
                </dl>
                  <div class="c"></div>
                </div>
                <div class="Prompt_btm"></div> 
                </div><?php endif; ?>
</body>
</html>