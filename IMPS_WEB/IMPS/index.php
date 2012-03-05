<?php
// 定义ThinkPHP框架路径   

define('THINK_PATH', './ThinkPHP/');   

//定义项目名称和路径   

define('APP_NAME', 'IMPS');   

define('APP_PATH', '.');   


if (isset($_POST['upattsid']) && isset($_POST['uploadifykey'])){
	if($_POST['uploadifykey']=="d909e88672fa5a5c58e819c211a101b4"){
	 session_id($_POST['upattsid']);
	}
}
 //加载框架入口文件    

require(THINK_PATH."/ThinkPHP.php");   

 //实例化一个网站应用实例   

App::run();   
//echo phpinfo();
?>