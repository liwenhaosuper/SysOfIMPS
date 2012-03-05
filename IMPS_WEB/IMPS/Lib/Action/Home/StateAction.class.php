<?php

class StateAction extends Action
{
	public function  _initialize()
	{
		if(empty($_SESSION['userid'])||empty($_SESSION['username'])||$_SESSION['usertype']!='user'){
			U('Public/login','',true);
		}
	}
	//添加一条状态
	public function addState()
	{
		$stateForm = D('State');
		$state['userid'] = $_SESSION['userid'];
		$state['msg'] = $_POST['status'];
		$state['time']= date('Y-m-d H:i:s');
		$state['place'] = 'web side';
		$state['longitude'] = 0;
		$state['latitude'] = 0;
		if($insertedId=$stateForm->add($state)){
			$state['username'] = $_SESSION['username'];
			$state['stateId'] = $insertedId;
		    $this->ajaxReturn($state,'更新成功',1);	
		}else{
			$this->error('更新失败：服务器忙碌中');
		}
	}
	
	function after_publish(){
		
	}
	
	//删除一条状态
	public function deleteState()
	{
		if(!empty($_POST['stateId']))
		{
			$stateForm = D('State');
			$result = $stateForm->delete($_POST['stateId']);
			if($result!=false){
				$this->ajaxReturn($_POST['stateId'],'删除成功',1);
			}
			else{
				$this->error('删除出错');
			}
		}else{
			$this->error("删除项不存在！");
		}
		
	}
	//添加评论
	public function addComment()
	{
		if(!empty($_POST['stateId']))
		{
			$stateCmmt = D('Statecomment');
			$cmmt['stateId'] = $_POST['stateId'];
			$cmmt['parent'] = empty($_POST['parent'])?-1:$_POST['parent'];
			$cmmt['friendName'] = $_SESSION['username'];
			$cmmt['time'] = date('Y-m-d H:i:s');
			$cmmt['content'] = $_POST['msg'];
			$cmmt['isLeaf'] = 1;
			$resid = $stateCmmt->add($cmmt);
			if($resid!=false){
				if($cmmt['parent']!=-1){
					$item = $stateCmmt->where('id='.$cmmt['parent'])->find();
					if($item!=false){
					   $item['isLeaf'] = 0;
					   $stateCmmt->save($item);	
					}
				}
				$cmmt['id'] = $resid;
				$this->ajaxReturn($cmmt,'添加成功',1);
			} else{
				$this->error('添加失败');
			}
		}else{
			$this->error('添加失败');
		}
	}
	
	
	

}