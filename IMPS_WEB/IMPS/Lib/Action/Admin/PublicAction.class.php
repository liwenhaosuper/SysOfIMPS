<?php
require_once 'Common/Service/UserService.class.php';

class PublicAction extends Action
{
	public function _initialize()
	{
		if(!empty($_SESSION['username'])&&$_SESSION['usertype']=='admin')
		{
			$this->redirect(U('Index/index','',true));
		}
		else if(!empty($_SESSION['username'])&&$_SESSION['usertype']=='user'){
            U('../Public/login','',true);			
		}	
	}
	
	public function login()
	{
		$this->display();
	}
	public function doLogin()
	{
		$username = $_POST['admin-name'];
		$password = $_POST['admin-pswd'];
		if($username!='admin'||$password!='admin')
		{
		    $this->error('用户名或密码错误');
		}
		else{
			$userservice = new UserService();
			$user = array();
			$user['username'] = $username;
			$user['userid'] = 1;
			$user['usertype'] = 'admin';
			$userservice->registerLogin($user,false);
			$this->redirect(U('Index/index','',true));
		}
		//$this->redirect(U('Admin/Public/login'));
	}
}