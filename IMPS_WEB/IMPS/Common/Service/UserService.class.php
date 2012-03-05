<?php

class UserService
{
	/**
	 * 注册用户的登陆状态 (即: 注册cookie + 注册session + 记录登陆信息)
	 * 
	 * @param array   $user          
	 * @param boolean $remeberMe 
	 */
	public function registerLogin(array $user,$rememberMe = false)
	{
		if(empty($user)){
			return false;
		}
		$_SESSION['userid'] = $user['userid'];
		$_SESSION['username'] = $user['username'];
		$_SESSION['usertype'] = $user['usertype'];
		return true;
	}
	public function unregisterLogin()
	{
		unset($_SESSION['userid']);
		unset($_SESSION['username']);
		unset($_SESSION['usertype']);
		session_unset();
		return true;
	}
}