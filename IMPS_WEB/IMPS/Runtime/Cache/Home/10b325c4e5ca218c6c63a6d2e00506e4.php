<?php if (!defined('THINK_PATH')) exit();?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>
			IMPS 融合地理位置的SNS社区
		</title>
		<link href="/IMPS/Tpl/default/common/css/login.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" type="text/css" href="/IMPS/Tpl/default/common/js/plugins/buttonCaptcha/jquery.buttonCaptcha.styles.css" />
		
		<script language="javascript" type="text/javascript" src="/IMPS/Tpl/default/common/js/jquery-1.5.2.min.js"></script>
		<script type="text/javascript" src="/IMPS/Tpl/default/common/js/jquery-ui-1.8.13.custom.min.js"></script>	
		<script type="text/javascript" language="javascript" src="/IMPS/Tpl/default/common/js/plugins/buttonCaptcha/jquery.buttonCaptcha.min.js"></script>		
		<script src="/IMPS/Tpl/default/common/plugins/booklet/jquery.easing.1.3.js" type="text/javascript"></script>
		<script src="/IMPS/Tpl/default/common/plugins/booklet/jquery.booklet.1.1.0.min.js" type="text/javascript"></script>
       
		
		<link href="/IMPS/Tpl/default/common/plugins/booklet/jquery.booklet.1.1.0.css" type="text/css" rel="stylesheet" media="screen" />
		<link rel="stylesheet" href="/IMPS/Tpl/default/common/plugins/css/style.css" type="text/css" media="screen"/>

		<script src="/IMPS/Tpl/default/common/plugins/cufon/cufon-yui.js" type="text/javascript"></script>

		 <!-- The JavaScript -->

        <script type="text/javascript">
			$(function() {
				var $mybook 		= $('#mybook');
				var $bttn_next		= $('#next_page_button');
				var $bttn_prev		= $('#prev_page_button');
				var $loading		= $('#loading');
				var $mybook_images	= $mybook.find('img');
				var cnt_images		= $mybook_images.length;
				var loaded			= 0;
				//preload all the images in the book,
				//and then call the booklet plugin

				$mybook_images.each(function(){
					var $img 	= $(this);
					var source	= $img.attr('src');
					$('<img/>').load(function(){
						++loaded;
						if(loaded == cnt_images){
							$loading.hide();
							$bttn_next.show();
							$bttn_prev.show();
							$mybook.show().booklet({
								name:               null,                            // name of the booklet to display in the document title bar
								width:              800,                             // container width
								height:             340,                             // container height
								speed:              600,                             // speed of the transition between pages
								direction:          'LTR',                           // direction of the overall content organization, default LTR, left to right, can be RTL for languages which read right to left
								startingPage:       0,                               // index of the first page to be displayed
								easing:             'easeInOutQuad',                 // easing method for complete transition
								easeIn:             'easeInQuad',                    // easing method for first half of transition
								easeOut:            'easeOutQuad',                   // easing method for second half of transition

								closed:             false,                           // start with the book "closed", will add empty pages to beginning and end of book
								closedFrontTitle:   null,                            // used with "closed", "menu" and "pageSelector", determines title of blank starting page
								closedFrontChapter: null,                            // used with "closed", "menu" and "chapterSelector", determines chapter name of blank starting page
								closedBackTitle:    null,                            // used with "closed", "menu" and "pageSelector", determines chapter name of blank ending page
								closedBackChapter:  null,                            // used with "closed", "menu" and "chapterSelector", determines chapter name of blank ending page
								covers:             false,                           // used with  "closed", makes first and last pages into covers, without page numbers (if enabled)

								pagePadding:        10,                              // padding for each page wrapper
								pageNumbers:        true,                            // display page numbers on each page

								hovers:             false,                            // enables preview pageturn hover animation, shows a small preview of previous or next page on hover
								overlays:           false,                            // enables navigation using a page sized overlay, when enabled links inside the content will not be clickable
								tabs:               false,                           // adds tabs along the top of the pages
								tabWidth:           60,                              // set the width of the tabs
								tabHeight:          20,                              // set the height of the tabs
								arrows:             false,                           // adds arrows overlayed over the book edges
								cursor:             'pointer',                       // cursor css setting for side bar areas

								hash:               false,                           // enables navigation using a hash string, ex: #/page/1 for page 1, will affect all booklets with 'hash' enabled
								keyboard:           true,                            // enables navigation with arrow keys (left: previous, right: next)
								next:               $bttn_next,          			// selector for element to use as click trigger for next page
								prev:               $bttn_prev,          			// selector for element to use as click trigger for previous page

								menu:               null,                            // selector for element to use as the menu area, required for 'pageSelector'
								pageSelector:       false,                           // enables navigation with a dropdown menu of pages, requires 'menu'
								chapterSelector:    false,                           // enables navigation with a dropdown menu of chapters, determined by the "rel" attribute, requires 'menu'

								shadows:            true,                            // display shadows on page animations
								shadowTopFwdWidth:  166,                             // shadow width for top forward anim
								shadowTopBackWidth: 166,                             // shadow width for top back anim
								shadowBtmWidth:     50,                              // shadow width for bottom shadow

								before:             function(){},                    // callback invoked before each page turn animation
								after:              function(){}                     // callback invoked after each page turn animation
							});
							Cufon.refresh();
						}
					}).attr('src',source);
				});
				
			});
        </script>
				
				
		<script type="text/javascript">
			Cufon.replace('h1,p,.b-counter');
			Cufon.replace('.book_wrapper a', {hover:true});
			Cufon.replace('.title', {textShadow: '1px 1px #C59471', fontFamily:'ChunkFive'});
			Cufon.replace('.reference a', {textShadow: '1px 1px #C59471', fontFamily:'ChunkFive'});
			Cufon.replace('.loading', {textShadow: '1px 1px #000', fontFamily:'ChunkFive'});
		</script>
		
		<script type="text/javascript">
                $('document').ready(function(){
					$("#username").focus(function () {
						$("#username").css("border", "3px solid #77d5fb");
					}).blur(function () { $("#username").css("border", "1px solid #333");});
					$("#password").focus(function () {
						$("#password").css("border", "3px solid #77d5fb");
					}).blur(function () { $("#password").css("border", "1px solid #333");});
					
					$(function() {
						$("#login-btn").buttonCaptcha({
							codeWord:'IMPS',
							codeZone:'COM'
						});
					});
					
				$("#login-btn").click(function () {
				var username = document.getElementById("username").value;
                var password = document.getElementById("password").value;
			    if(username==''||password==''){
			        username='';password = '';
				     alert("用户名或密码不能为空");
				     return false;
				}				  
			});
                });
		</script>
		<script type="text/javascript">
		    
		</script>
	</head>
	<body>
		<div class="login-wrap">
			<div class="login-wrap-up" style="height: auto;">
	
				<div class="book_wrapper">
			<a id="next_page_button"></a>
			<a id="prev_page_button"></a>
			<div id="loading" class="loading">LOADING...</div>
			<div id="mybook" style="display:none;">
				<div class="b-load">
					<div>
						<img src="/IMPS/Tpl/default/common/plugins/images/1.jpg" alt=""/>
					</div>
					
					<div>
						<h1>看看谁在身边</h1>
						<p>想知道谁在这儿？IMPS为你导航！出席论坛、展会，茫茫人海，有针对性地找到合作伙伴，						谈何容易呀？！挨个儿交换名片？费时费力不说，还弄得自己像个搞推销的。手机装了“在这儿”，
						活动现场有谁，身份、职务是什么，可以一目了然地看到。</p>
						<a href="#"  class="article">赞一个</a>
						<a href="#"  class="demo">如何使用</a>
					</div>
					
					<div>
					   <img src="/IMPS/Tpl/default/common/plugins/images/2.jpg" alt="" />
					</div>
					
					
					<div>
						<h1>交换电子名片</h1>
						<p>传统方式是出席活动现场，面对面才能和感兴趣的人交换名片？IMPS打破了这个传统！						在活动报名、筹备阶段，你就可以通过软件和感兴趣的人即时交换电子名片，实时维系人脉！
						手机在哪儿，IMPS就伴随你到哪儿，你的人脉就在哪儿！</p>
						<a href="#" class="article">赞一个</a>
						<a href="#" class="demo">如何使用</a>
					</div>
					<div>
						<img src="/IMPS/Tpl/default/common/plugins/images/3.jpg" alt="" />
					</div>
					
					<div>
						<h1>维系关系</h1>
						<p>有一种人脉关系维护，只需要隔三差五的一句问候；有种话题，只适合与“同道中”人深度探讨；
						IMPS就是我们要找的那个“工具”；——实时和最新建立的合作伙伴、客户、投资人，进行“有深度”、
						“有层次”的在线聊天，省时省力，何乐而不为呢？</p>
						<a href="#"  class="article">赞一个</a>
						<a href="#"  class="demo">如何使用</a>
					</div>
				</div>
			</div>
		</div>
       
				
			</div>
			<div class="login-wrap-center">
			   <form id="loginForm" action="__URL__/doLogin" method="post">
				<table style="width: 100%; border: 0;">
					<tr>
						<td style="width: 240px;">
							<div class="login-if">
								<div class="interface" id="login">
									<div id="login-table">
										<div class="login-input">
											<label for="username">登录名：</label> <input type="text" trim=true id="username" name="username" tabindex="1" />
										</div>
										<div class="login-input">
											<label for="pswd-input">密码：</label> <input type="password" trim=true class="pswd-input" id="password" name="password" tabindex="2" />
										</div>
									</div>
								</div>								
								<div style="text-align: center;">
									<button type="submit" value="登录" post_click="登录中..." id="login-btn" onclick="checkSubmit()">登录</button>
									<button type="button" value="注册" onclick="javascript:window.location.href='__URL__/register'" id="register-btn" >注册</button>
								</div>
							</div>
						</td>
						<td>
							<div class="splash" >
							</div>
						</td>
					</tr>
				</table>
				</form>
			</div>
			<div id="login-wrap-bottom"></div>
		</div>
	</body>
</html>