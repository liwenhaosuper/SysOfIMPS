<?php

class ActivityAction extends Action
{
  //private $activities;
  private $_Mtype; //活动分类
  private $_Mactivity;
  private $_Mattach;
  private $_Mcomment;
  private $_Marea;
  private $_Mtake;
  public function _initialize()
  {
    if(empty($_SESSION['userid'])||empty($_SESSION['username'])||$_SESSION['usertype']!='user'){
      U('Index/index','',true);
    }
    else{
      $this->activity = D('Activity');	
    }
    $this->_Mtype = D('Activitytype');
    $this->_Mattach = M('Activityattach');
    $this->_Mcomment = M('Activitycomment');
    $this->_Mactivity = M('Activity');
    $this->_Marea = M('Area');
    $this->_Mtake = M('Takeactivity');
  }
  public function showAll() {
    // activity records from database
    // add more fields
    $activities = $this->_Mactivity->findAll();
    $cnt = count($activities);
    foreach ($activities as &$actv) {
      $area = $this->_Marea->where('id='.$actv['are_id'])->find();
      $actv['area'] = $area['name'];
      $type = $this->_Mtype->where('id='.$actv['act_id'])->find();
      $actv['type'] = $type['name'];
      $cover = $this->_Mattach->where('act_id='.$actv['id'])->find();
      $actv['coverPath'] = $cover['path'];
      $actv['coverName'] = $cover['name'];
    }
    $this->assign('actvlist', $activities);
  }

  public function showHot() {
    $activities = $this->_Mactivity->order("attentionCount desc")->limit('5')->select();
    $cnt = count($activities);
    foreach ($activities as &$actv) {
      $area = $this->_Marea->where('id='.$actv['are_id'])->find();
      $actv['area'] = $area['name'];
      $type = $this->_Mtype->where('id='.$actv['act_id'])->find();
      $actv['type'] = $type['name'];
      $cover = $this->_Mattach->where('act_id='.$actv['id'])->find();
      $actv['coverPath'] = $cover['path'];
      $actv['coverName'] = $cover['name'];
    }
    $this->assign('actvlist', $activities);
  }

  public function showData($order) {
    if ($order) {
      $activities = $this->_Mactivity->order($order." desc")->limit('5')->select();
    } else {
      $activities = $this->_Mactivity->findAll();
    }      
    $cnt = count($activities);
    foreach ($activities as &$actv) {
      $area = $this->_Marea->where('id='.$actv['are_id'])->find();
      $actv['area'] = $area['name'];
      $type = $this->_Mtype->where('id='.$actv['act_id'])->find();
      $actv['type'] = $type['name'];
      $cover = $this->_Mattach->where('act_id='.$actv['id'])->find();
      $actv['coverPath'] = $cover['path'];
      $actv['coverName'] = $cover['name'];
    }
    $this->assign('actvlist', $activities);
  }

  // show detailed info of a activity
  public function info() {
    $actv = $this->_Mactivity->where('id='.$_GET['activityId'])->find();
    $area = $this->_Marea->where('id='.$actv['are_id'])->find();
    $actv['area'] = $area['name'];
    $type = $this->_Mtype->where('id='.$actv['act_id'])->find();
    $actv['type'] = $type['name'];
    $cover = $this->_Mattach->where('act_id='.$actv['id'])->find();
    $actv['coverPath'] = $cover['path'];
    $actv['coverName'] = $cover['name'];

    $takeRecord = $this->_Mtake->where('userid='.$_SESSION['userid'].' AND id='.$_GET['activityId'])->find();
    $this->assign('spec', $actv);
    $this->assign('record', $takeRecord);
    $this->display();
  }

  public function join() {
    $takeRecord = $this->_Mtake->where('userid='.$_SESSION['userid'].' AND id='.$_GET['actvid'])->find();
    if (isset($takeRecord)) {
      $result1 = $this->_Mtake->delete($takeRecord);
      if ($result1) {
      	$actv = $this->_Mactivity->where(array('id'=>$_GET['actvid']))->find();
      	$data['attentionCount'] = $actv['attentionCount'] - 1;
	$condition['id'] = $_GET['actvid'];
      	$result2 = $this->_Mactivity->where(array('id'=>$_GET['actvid']))->save(array('attentionCount'=>$count));
      	if ($result2 !== false) {
      	  $this->success("eee");
      	} else {
      	  $this->error("aaa");
      	}
      } else  {
      	$this->error("aaa");
      }
    } else {
      $result1 = $this->_Mtake->add(array('userid'=>$_SESSION['userid'], 'id'=>$_GET['actvid']));
      if ($result1) {
	$actv = $this->_Mactivity->where(array('id'=>$_GET['actvid']))->find();
	$count = $actv['attentionCount'] + 1;
	$result2 = $this->_Mactivity->where(array('id'=>$_GET['actvid']))->save(array('attentionCount'=>$count));
	if ($result2 !== false) {
	  $this->success("eee");
	} else {
	  $this->error("aaa");
	}
      } else  {
	$this->error("aaa");
      }
    }
  }

  //活动主页
  public function index()
  {
    $order = null;
    switch($_GET['order']){
    case 'new': //最新活动
      $this->showData("id");
      $this->display();
      break;
    case 'follow'://关注的人的活动
      break;
    case 'fans'://粉丝活动
      break;
    case 'friend'://好友活动
      break;
    case 'hot'://热门活动
      $this->showData("attentionCount");
      $this->display();
      break;
    default:   //我的活动
      $this->showData();
      $this->display();
      break;
    }
  }

  public function create() {
    $this->assign('type', $this->_Mtype->getAllType());
    $this->display();
  }

  public function doAdd() {
		$activity = array();
		$activity['name'] = $_POST['actvName'];
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
  }
}
