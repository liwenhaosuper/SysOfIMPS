<?php

include_once 'Common/Service/ValidationService.class.php';

class UserAction extends Action
{
	public function _initialize()
	{
        if(!empty($_SESSION['username'])&&$_SESSION['usertype']=='user'){
           U('Public/login','',true);		
		}
		else if(empty($_SESSION['username'])||empty($_SESSION['userid'])||empty($_SESSION['usertype']))
		{
			U('Public/login','',true);
		}	
		else{
			$this->_Muser = D('User');
		    $this->_Mvalidation = D('Validation');
		}
	}
	public function index()
	{
	   $list = array();
	   $keyword = $_POST['keyword'];
	   if($keyword!=null&&$keyword!='')
	   {
	   	     $list = $this->_Muser->findByKeyWord($keyword);
	   }
	   else{
	   	    $list = $this->_Muser->findAll();
	   }
	   $cnt = count($list);
	   for($i=0;$i<$cnt;$i++)
	   {
	   	   $val = $this->_Mvalidation->where('userid='.$list[$i]['userid'])->find();
	   	   if($val['status']!=0)
	   	   {
	   	   	   $list[$i]['status'] = '正常使用';
	   	   }
	   	   else{
	   	   	   $list[$i]['status'] = '尚未通过验证';
	   	   }
	   	   $list[$i]['id'] = $list[$i]['userid'];
	   }
	   $this->assign("list",$list);
	   $this->display();	
	}
	public function edit()
	{
		$userid = $_GET['id'];
		if($userid==null)
		{
			$this->error("编辑错误");
		}else{
			$vo = $this->_Muser->where('userid='.$userid)->find();
			if($vo!=false){
				$val = $this->_Mvalidation->where('userid='.$userid)->find();
				if($val['status']==0)
				{
					$vo['status'] = 0;
				}
				else{
					$vo['status'] = 1;
				}
				$vo['gender'] = ($vo['gender']=='M'?1:0);
				$this->assign('vo',$vo);
				$this->display();
			}else{
				$this->error("该用户不存在");
			}
		}
	}
	
	//删除用户
	public function delete()
	{
		$id = $_POST['userid'];
		if($id==null)
		{
			$this->ajaxReturn(null,'错误的删除方法',0);
		}
		else{
			$result = null;
		    $result = $this->_Muser->where('userid='.$id)->delete();	
			if($result!=false)
			{
				$this->ajaxReturn($id,'删除成功',1);
			}
			else {
				$this->ajaxReturn($id,'删除失败',0);
			}
		}

	}	
	//添加用户
	public function add()
	{
		$this->display();
	}
	public function doAdd()
	{
		$user = array();
		$user['username'] = $_POST['username'];
		$user['password'] = $_POST['password'];
		$user['email'] = $_POST['email'];
		$user['gender'] = $_POST['gender']==1?'M':'F';
		if($user['username']==null||$user['username']==""||$user['password']==null||
		$user['password']==""||$user['email']==null||$user['email']==""){
			$this->error('参数为空');
		}else{
		    $result = $this->_Muser->checkUser($user['username']);
			if($result!=false){
				$this->error('用户名已存在');
			}else{
			    $ret = $this->_Muser->add($user);
			    if($ret!=false){
			    	$data=array();
			    	$data['status'] = $_POST['status'];
			    	$data['userid'] = $ret;
			    	$service = new ValidationService();
			    	$data['code'] = $service->generatePublicCode($ret);
			    	$re = $this->_Mvalidation->add($data);
			    	$this->success('添加成功');
			    }	
			}
		}
		
	}
	public function checkUser()
	{
		
	    $username = null;
		$username = $_POST['username'];
		if($username==null||$username==""){
			$this->ajaxReturn(null,'参数为空',0);
		}else{
		    $result = $this->_Muser->checkUser($username);
			if($result==false){
				$this->ajaxReturn($username,'用户名可以使用',1);
			}else{
				$this->ajaxReturn($username,'用户名已被占有',0);
			}
		}
		

	}

	//用户统计
	public function statistics()
	{
		
	}

	public function resetPwd()
	{
		$userid = $_POST['userid'];
		$pwd = $_POST['password'];
		if($userid==null||$pwd==null){
			$this->ajaxReturn($userid,'请求数据出错',0);
		}
		else{
			$result = $this->_Muser->where('userid='.$userid)->find();
			if($result!=false){
			       $result['password'] = $pwd;
			       $value = $this->_Muser->save($result);
			       if($value!=false){
			       	 $this->ajaxReturn($userid,'密码修改成功',1);
			       }else{
			       	 $this->ajaxReturn($userid,'密码修改失败',0);
			       }
			}
		}
	}
	//更新用户信息
	public function update()
	{
		$user=array();
		$user['userid'] = $_POST['userid'];
		$user['username'] = $_POST['username'];
		$user['email'] = $_POST["email"];
		$user['gender'] = ($_POST['gender']==1?'M':'F');
		$status = $_POST['status'];
		if(empty($user['userid'])||empty($user['username'])||empty($user['email'])||empty($user['gender']))
		{
			$this->ajaxReturn($user,'更新信息有误'.$user['userid'].$user['username'].$user['email'].$user['gender'].$status,0);
		}
		else{
			$this->_Muser->save($user);
			$val = $this->_Mvalidation->where('userid='.$user['userid'])->find();
			$val['status'] = $_POST['status'];
			$this->_Mvalidation->save($val);
			$this->ajaxReturn($user,'更新成功',1);
		}
	}
}