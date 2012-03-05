<?php
require_once 'Common/Service/UserService.class.php';

class UserAction extends Action
{
	protected $tableName = 'user';
	public function  _initialize()
	{
		if(empty($_SESSION['userid'])||empty($_SESSION['username'])||$_SESSION['usertype']!='user'){
			U('Public/login','',true);
		}
	}
	/**
	 * 个人主页
	 * 
	 */
	public function index()
	{

		$data = array();
		//获取我的状态
		$data['mystate'] = D('State')->getState($_SESSION['userid'],10);
	    //TODO:获取好友动态
	    $friendlist = D('User')->getFriendList($_SESSION['userid'],'Friendrelate');
	    $data['friendstate'] = D('State')->getFriendState($friendlist,20);
	    
	    $this->assign('friendstatuslist',$data['friendstate']);
	    $this->assign('statuslist',$data['mystate']);
	    $this->display();
	}
	public function logout()
	{
		$userservice = new UserService();
		$userservice->unregisterLogin();
		U('Public/login','',true);
	}
	/**
	 * 查找用户
	 * @_POST data: search_key
	 */
	public function searchUser()
	{
		$data['search_key'] = $_POST['search_key'];
	}
	//表情
	function emotions() {
		exit ( json_encode ( M( 'Expression' )->findAll() ) );
	}
}