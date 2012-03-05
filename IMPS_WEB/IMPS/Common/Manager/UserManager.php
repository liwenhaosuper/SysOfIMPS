<?php

import("Common/Service/UserService.class.php");

abstract class absUser
{
    protected $username;
    protected $userid;
    protected $pwd;
    public function __construct($username,$pwd){
		$this->username = $username;
		$this->pwd = $pwd;
    }
    public abstract function login();
    public function logout(){
    	$userservice = new UserService();
    	$userservice->unregisterLogin();
    }
}
class User extends absUser
{
	public function login(){
		$model = M('User');
		$res = $model->login($this->usename,$this->pwd);
		if($res){
			$this->userid = $res;
			$userservice = new UserService();
    		$userservice->registerLogin($this);
    		return true;
		}
		return false;
	}
}
class Admin extends absUser{
	public function login(){
		if($this->username!='admin'||$this->pwd!='admin'){
			return false;
		}
		return true;
	}
}
abstract class Creator{
	protected static $user = NULL;
	public abstract function createUser($username,$pwd);
}
class AdminCreator extends Creator{
	public function createUser($username,$pwd){
		$this->user = new Admin($username,$pwd);
		return $this->user;
	}
}
class UserCreator extends Creator{
	public function createUser($username,$pwd){
		$this->user = new User($username,$pwd);
		return $this->user;
	}
}
class UserManager{
	private static $instance = NULL;
	private static $creator;
	public function __construct(){}
	public function __clone()
	{
		throw new Exception("UserManager is a singleton, clone is not permitted!");
	}
	public static function getInstance(){
		if(self::$instance==NULL){
			self::$instance = new UserManager();
		}
		return self::$instance;
	}
	public function createUser($username,$pwd,$type){
		if($type=='admin'){
			self::$creator = new AdminCreator();
		}
		else{
			self::$creator = new UserCreator();
		}
		return self::$creator->createUser($username,$pwd);
	}
}