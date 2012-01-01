<?php

class AlbumAction extends Action
{
	public function _initialize()
	{
		if(empty($_SESSION['userid'])||empty($_SESSION['username'])||$_SESSION['usertype']!='user'){
			if(!isset($_POST['uploadifykey'])){
				U('Index/index','',true);
			}
		}	
	}
	public function index()
	{
		$model= new Model();
		$sql="select a.*,count(b.id) as imgnum from album as a LEFT JOIN albumphoto as b on a.id=b.pid and a.userid=".$_SESSION['userid']." group by a.id LIMIT 0,30";
		$count= count($model->query("select a.*,count(b.id) as imgnum from album as a LEFT JOIN albumphoto as b on a.id=b.pid and a.userid=".$_SESSION['userid']." group by a.id"));//取得总数据
		//seperate page
	    import ( '@.ORG.Page' );//载入分页类
	    $page =  new Page($sql,5);
	    $sql =  $page->StartPage($count,"a.sortnum asc,a.id",true,true,'down');
        $list=$model->query($sql);
 	    $ButtonArray = array("<","<<",">>",">");
	    $pagebar=$page->EndPage($ButtonArray,"select",true,"green-black");
 	    $this->assign("albumlist",$list);
  	    $this->assign("pagebar",$pagebar);
	    $this->display();
	}
	public function listphoto(){
		//相册图片列表
		$id=$_REQUEST['id'];
		if(!$id){$this->error('系统查找不到该操作,请重试!');return;}
		$model=D("Album");
		$albumarr=$model->where("id=".$id." and userid=".$_SESSION['userid'])->find();
		if(!$albumarr){$this->error('访问禁止!');return;}
		
	    $modelphoto=M("Albumphoto");
	    //$limitnum='30';//每页显示条数
		$sql="select * from albumphoto where pid=".$id;//取到sql
		//$count= $modelphoto->where("pid=$id")->count();//取得总数据
		//import ( '@.ORG.Page' );//载入分页类
	   //$page =  new Page($sql,5);
	   //$sql =  $page->StartPage($count,"sortnum asc,id",true,true,'down');
       $list=$modelphoto->query($sql);
 	   $ButtonArray = array("<","<<",">>",">");
	   //$pagebar=$page->EndPage($ButtonArray,"select",true,"green-black");
	   $this->assign("albumarr",$albumarr);
	   $this->assign("albumlist",$list);
	  // $this->assign("pagebar",$pagebar);
	   $this->display();
	   $model->setinc("hits","id=$id",1);
	}
	public function newalbum(){
		//新建相册
        $model=D("Album");
		if($vo = $model->create()) {
			$result	=$model->add();
			var_dump($result);
			if($result) {
			  $this->success('执行新增相册操作成功');
			}else{
			  $this->error('执行新增相册操作失败');
			}
		}else{
			$this->error($model->getError());
		}
	}
	public function delalbum(){
			//删除相册,同时将删除此相册下所有图片
		$id=$_POST['id'];
		if(!$id){$this->error('系统查找不到该操作,请重试!');}
		$model=M("Album");
		$modelp = M ('Albumphoto'); 
		$delb=$model->where("id=".$id." and userid=".$_SESSION['userid'])->find();
		if(!$delb) {
			$this->error('系统查找不到该操作,请重试!');
		}else{
			$dtitle=$delb['album_name'];
			$model->delete();
			$delp=$modelp->where("pid=$id")->select();
			if($delp){
		        foreach($delp as $k=>$v){
				   unlink('/IMPS/Uploads/album/b_'.$v['pickey']);
			       unlink('/IMPS/Uploads/album/s_'.$v['pickey']);
				}
				$modelp->where("pid=$id")->delete();
			}
			$this->success('删除相册成功');
		}
	}
	public function sortalbum(){
		//排序相册
        $model=M("Album");
		$albumarr=$model->where('userid='.$_SESSION['userid'])->order("sortnum asc,id desc")->findAll();
	    $this->assign("albumlist",$albumarr);
	    $this->display();
	}
	public function _empty() {
      //空操作定义
	  
	}

	public function uploadimgs(){
		//上传图片
		$id=$_REQUEST['id'];
		if(!$id){$this->error('系统查找不到该操作,请重试!');return;}
		$now = date("Ymd_His");
		$code = $now."_".mt_rand(10000, 99999);
		if(!empty($_FILES)) { 
		//如果有文件上传 上传附件
		import("ORG.Net.UploadFile");
        $upload = new UploadFile(); 
        //设置上传文件大小 
        $upload->maxSize  = 2048000 ; 
        //设置上传文件类型 
		$upload -> allowExts  = array("jpg", "gif", "png","jpeg"); 
        //设置附件上传目录 
        $upload->savePath =  "./Uploads/album/"; 
        //设置需要生成缩略图，仅对图像文件有效 
        $upload->thumb =  true; 
		// 设置引用图片类库包路径
		$upload->imageClassPath = '@.ORG.Image'; 
        //设置需要生成缩略图的文件后缀 
        $upload->thumbPrefix   =  'b_,s_';  //生产3张缩略图 
       //设置缩略图最大宽度 
        $upload->thumbMaxWidth =  '600,150'; 
       //设置缩略图最大高度 
        $upload->thumbMaxHeight = '600,150'; 
       //设置上传文件规则 
       $upload->saveRule = $code; 
       //删除原图 
       $upload->thumbRemoveOrigin = true; 
        if(!$upload->upload()) { 
            //捕获上传异常 
            $this->error($upload->getErrorMsg()); 
        }else { 
		  $fileinfo = $upload->getUploadFileInfo();//得到已上传文件的信息数组
        } 
        $model = M ('Albumphoto'); 
        //保存当前数据对象 
        $data['pickey']=$fileinfo[0]['savename']; 
        $data['pid']=$id; 
		$data['name'] =substr($fileinfo[0]['name'], 0, strrpos($fileinfo[0]['name'], '.'));
        $data['create_time']=time(); 
        $list=$model->add($data);
        if($list!==false){
          $this->success ('上传图片成功！'); 
        }else{ 
           $this->error ('上传图片失败!'); 
        }
	}else{
           $this->error ('上传图片失败!'); 
		}
		
	}
	public function setcover(){
	 //设为封面图	
		$modela =M('Album');
		$modelp =M('Albumphoto');
		$id=$_POST['id'];
		$pid=$_POST['pid'];
		if(!$id || !$pid){$this->ajaxReturn(0,'系统查找不到该操作,请重试!',0);return;}
	    $presult=$modelp->where("pid=$pid and id=$id")->find();
		if(!$presult){
		 $this->ajaxReturn(0,'系统查找不到该操作,请重试!',0);
		}else{
		  $mod = $modelp->execute("update albumphoto set iscover=0 where pid=".$pid);
		  $data['iscover'] = 1;
		  $data2['cover']=$presult['pickey'];
		  $pid=$presult['pid'];
		  $result=$modelp->where("id=$id")->save($data);
		  $result2=$modela->where("id=".$pid." and userid=".$_SESSION['userid'])->save($data2);
		  if($result || $result2) {
		   $this->ajaxReturn(1,"操作成功",1);  
		  }else{
		   $this->ajaxReturn(0,"操作失败",2);  
		  }
		}
	}
	public function albumwall(){
		$model= new Model();
		$dbtable1 ="album";
		$dbtable2 ="albumphoto";
		$sql="select a.*,count(b.id) as imgnum from $dbtable1 as a LEFT JOIN $dbtable2 as b on a.id=b.pid and a.userid=".$_SESSION['userid']." group by a.id order by a.sortnum asc,a.id desc";
		$albumlist= $model->query($sql);
		
		$modelphoto=M("Albumphoto");
		$albumphotolist=$modelphoto->order("id desc")->select();
		foreach($albumlist as $k=>$v)
		{   
		   if($v['imgnum']>0){
			$albumarr[] = $v;
		   }
		}
		foreach($albumarr as $k=>$v)
		{
		foreach($albumphotolist as $key=>$val)
		{
		  if($v['id'] == $val['pid'])
		  {
			 $photoarr[] = $val;
		  }
		}
		$array_list[]=array("albumlist"=>$v,"photolist"=>$photoarr);
		unset($photoarr); 	
		}
		$this->assign("vararr",$array_list);
		$this->display();
	}
	public function editpname(){
		//修改图片名称
		$model =M('Albumphoto');
		$imgname=$_POST['photo_name'];
		$oldimgname=$_GET['oldname'];
		$id=$_GET['id'];
		if(!$id){$this->ajaxReturn($oldimgname,'图片名称不能为空!',0);exit;}
		if(iLength($imgname)<1 || iLength($imgname)>10){$this->ajaxReturn($oldimgname,'图片不能为空,最多10字符!',0);exit;}
		$map=array(
		"name"=>$imgname
		);
		$result	=$model->where("id=$id")->save($map);
		if(false===$result) {
		  $this->ajaxReturn($oldimgname,'执行修改图片名称失败!',0);
		}else{
		  $this->ajaxReturn(0,$imgname,1);
		}
	}
	
	public function delimg(){
		 
		$model = M ('Albumphoto');
		$modela =D ('Album');
		$id=$_POST['id'];
		if(!$id){$this->error('系统查找不到该操作,请重试!');return;}
	     $delresult=$model->find($id);
		 if($delresult){
	       unlink('/IMPS/Uploads/album/b_'.$delresult['pickey']);
	       unlink('/IMPS/Uploads/album/s_'.$delresult['pickey']);
		   if($delresult['iscover']==1){
			 $data['cover']="";
			 $pid=$delresult['pid']; 
			 $result2=$modela->where("id=$pid")->save($data);
		   }
		   $model->where("id=$id")->delete();
		   $this->success('删除成功');
		 }else{
		   $this->error('删除相册图片失败');
		 }
	}
	public function counthit(){
		//图片点击统计
		$id=$_REQUEST['id'];
		if($id){
	     $model=M("Albumphoto");
		 $isphoto=$model->where("id=$id")->find();
		 if($isphoto){
			$model->setinc("hits","id=$id",1);
		 }
		}
	}
	public function editalbumname(){
				//修改相册名称
		$model	=	D('Album');
		$albumname=$_POST['album_name'];
		$oldalbumname=$_GET['oldname'];
		$id=$_GET['id'];
		if(!$id){$this->ajaxReturn($oldalbumname,'相册名称不能为空!',0);exit;}
		if(iLength($albumname)<1 || iLength($albumname)>10){$this->ajaxReturn($oldalbumname,'相册名称不能空为,最多10字符!',0);exit;}
		$map=array(
		"album_name"=>$albumname
		);
		$result	=$model->where("id=".$id." and userid=".$_SESSION['userid'])->save($map);
		if(false===$result) {
		  $this->ajaxReturn($oldalbumname,'修改失败!',0);
		}else{
		  $this->ajaxReturn(0,$albumname,1);
		}
		
	}
}