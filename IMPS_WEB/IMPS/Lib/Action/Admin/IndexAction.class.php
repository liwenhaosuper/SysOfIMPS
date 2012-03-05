<?php

class IndexAction extends Action
{
	public $Muser;
	public $Mactivity;
	public function _initialize()
	{
		$this->Muser = D('User');
		$this->Mactivity = D('Activity');
	    if(empty($_SESSION['username']))
		{
			U('Public/login','',true);
		}
		else if($_SESSION['usertype']!='admin')
		{
			U('Public/login','',true);
		}
	}
	
	public function index()
	{
		$data['users'] = $this->Muser->findAll();
		$data['activities'] = $this->Mactivity->findAll();
		$this->assign('data',$data);
	    $this->display();
	}
	//删除用户/活动
	public function deleteEntity($type='user')
	{
		$id = $_POST['id'];
		if($id==null)
		{
			$this->error('错误的删除方法');
		}
		$result = null;
		if($type=='user')
		{
		   $result = $this->Muser->where('userid='.$id)->delete();	
		}
		else{
			$result = $this->Mactivity->where('id='.$id)->delete();
		}
		if($result!=false)
		{
			$this->ajaxReturn($id,'删除成功',1);
		}
		else {
			$this->error('删除失败');
		}
	}
	//添加用户/活动
	public function addEntity($type='user')
	{
		$result = null;
		if($type=='user'){
			$user = array();
			$user['username'] = $_POST['username'];
			$user['password'] = $_POST['password'];
			$user['email'] = $_POST['email'];
			$user['gender'] = $_POST['gender'];
			$result = $this->Muser->add($user);
		}
		else{
			
		}
		if($result!=false)
		{
			$this->ajaxReturn($result,'添加成功',1);
		}
	}
	//修改用户信息
	public function modifyUser()
	{
		$user = array();
		$user['userid'] = $_POST['userid'];
		$user['username'] = $_POST['username'];
		$user['password'] = $_POST['password'];
		$user['email'] = $_POST['email'];
		$user['gender'] = $_POST['gender'];
		$result = $this->Muser->save($user);
		if($result!=false){
			$this->ajaxReturn($result,'修改成功',1);
		}
		else{
			$this->error("修改失败");
		}
	}
}