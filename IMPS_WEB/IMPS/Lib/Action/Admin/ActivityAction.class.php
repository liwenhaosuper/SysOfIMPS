<?php
class ActivityAction extends Action
{
	private $_Mtype; //活动分类
	private $_Mactivity;
	private $_Mattach;
	private $_Mcomment;
	private $_Marea;
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
			$this->_Mtype = D('Activitytype');
			$this->_Mattach = M('Activityattach');
			$this->_Mcomment = M('Activitycomment');
			$this->_Mactivity = M('Activity');
			$this->_Marea = M('Area');
		}	
	}
	public function index()
	{
		$activities = $this->_Mactivity->findAll();
		$cnt = count($activities);
		for($i=0;$i<$cnt;$i++){
			$area = $this->_Marea->where('id='.$activities[$i]['are_id'])->find();
			$activities[$i]['area'] = $area['name'];
			$type = $this->_Mtype->where('id='.$activities[$i]['act_id'])->find();
			$activities[$i]['type'] = $type['name'];
			$cover = $this->_Mattach->where('act_id='.$activities[$i]['id'])->find();
			$activities[$i]['coverPath'] = $cover['path'];
			$activities[$i]['coverName'] = $cover['name'];
		}
		$this->assign('actvlist',$activities);
		$this->display();
	}
	//删除活动
	public function delete()
	{
		$id = $_POST['id'];
		if($id==null)
		{
			$this->error('错误的删除方法');
		}
		$result = null;
		$result = $this->Muser->where('userid='.$id)->delete();	
		if($result!=false)
		{
			$this->ajaxReturn($id,'删除成功',1);
		}
		else {
			$this->error('删除失败');
		}
	}	
	//添加活动
	public function add()
	{
		$this->assign('type',$this->_Mtype->getAllType());
		//var_dump($this->_Mtype->getAllType());
		$this->display();
	}
	public function doAdd()
	{
		$activity = array();
		$activity['name'] = $_POST['activityName'];
		$arearray = array();$arearray = explode(',',$_POST['current']);
		$activity['are_id'] = $arearray[1];
		$activity['act_id'] = $_POST['activityType'];
		$activity['contact'] = $_POST['activityContact'];
		$activity['address'] = $_POST['activityAddress'];
		$activity['sponsor'] = $_POST['activitySponsor'];
		$activity['sTime'] = $_POST['sTime'];
		$activity['eTime'] = $_POST['eTime'];
	    $activity['signTime'] = $_POST['signTime'];
		$activity['explaination'] = $_POST['activityDetail'];
		$activity['attentionCount'] = 0;
		$activity['creator'] = 'Admin';
		$result = $this->_Mactivity->add($activity);
		if($result!=false){
				//上传文件
			if($_FILES){
				import("@.ORG.UploadFile");
				$upload = new UploadFile();
		        $upload->maxSize = 3292200;
		        $upload->allowExts = explode(',', 'jpg,gif,png,jpeg');
		        $upload->savePath = "../IMPS/Uploads/activity/images/".date('Y-m')."/";
		        $upload->thumb = true;
		        $upload->imageClassPath = '@.ORG.Image';
		        $upload->thumbPrefix = 'm_,s_';  
		        $upload->thumbMaxWidth = '400,100';
		        $upload->thumbMaxHeight = '400,100';
		        $upload->saveRule = uniqid;
		        $upload->thumbRemoveOrigin = true;
		        if (!$upload->upload()) {
		            $this->error($upload->getErrorMsg());
		        } else {
		            $uploadList = $upload->getUploadFileInfo();
		            //保存数据库
		            $activityattach['path'] = "/IMPS/Uploads/activity/images/".date('Y-m')."/".'m_'.$uploadList[0]['savename'];
		            $activityattach['name'] = $uploadList[0]['savename'];
		            $activityattach['act_id'] = $result;
		            $activityattach['type'] = 0;
		            $addresult = $this->_Mattach->add($activityattach);
		            
		        }
			}
			$this->success('活动添加成功!');
		}else{
			$this->error('活动添加失败');
		}
		//var_dump($activity);

	}
	//查找活动
	public function find()
	{
		
	} 
	public function doFind()
	{
		
	}
	//活动统计
	public function statistics()
	{
		
	}
	//活动列表
	public function activitylist()
	{
	}
	//活动详情
	public function activityDetail(){
		$id = $_GET['activityId'];
		if(empty($id)||$id==""){
			$data = '该活动不存在或已被删除';
			$this->assign('data',$data);
			$this->display();
		}else{
			$item = $this->_Mactivity->where('id='.$id)->find();
			if($item==false||$item==null){
				$data = '该活动不存在或已被删除';
				$this->assign('data',$data);
				$this->display();
			}else{
				$area = $this->_Marea->where('id='.$item['are_id'])->find();
				$item['area'] = $area['name'];
				$type = $this->_Mtype->where('id='.$item['act_id'])->find();
				$item['type'] = $type['name'];
				$cover = $this->_Mattach->where('act_id='.$item['id'])->find();
				$item['coverPath'] = $cover['path'];
				$item['coverName'] = $cover['name'];
				$this->assign('item',$item);
				$this->display();
			}
		}
	}
}