<?php

include_once 'Common/Service/UserService.class.php';

class CommonAction extends Action
{
	public function _initialize()
	{
		if(empty($_SESSION['username'])||empty($_SESSION['usertype'])||$_SESSION['usertype']!='admin'){
            U('Public/login','',true);			
		}	
	}
	// 顶部页面
	public function top() {
		C('SHOW_RUN_TIME',false);			// 运行时间显示
		C('SHOW_PAGE_TRACE',false);
		$model	=	M("Group");
		$list	= array(
		                0 => '用户管理',
		                1 => '活动管理'
		                );
		$this->assign('nodeGroupList',$list);
		$this->display();
	}
	// 尾部页面
	public function footer() {
		C('SHOW_RUN_TIME',false);			// 运行时间显示
		C('SHOW_PAGE_TRACE',false);
		$this->display();
	}
	// 菜单页面
	public function menu() {
		C('SHOW_RUN_TIME',false);			// 运行时间显示
		C('SHOW_PAGE_TRACE',false);
		$tag = $_GET['tag'];
		if($tag==null)
		    $tag = -1;
		$menu = array();
		if($tag==0)//用户管理
		{
			$menu = array(
			          0 => array('tagid'=>$tag,'tagIndex'=>'index','name' => '用户列表','action' =>'User'),
			          1 => array('tagid'=>$tag,'tagIndex'=>'add','name'=>'添加用户','action' =>'User'),
			          2 => array('tagid'=>$tag,'tagIndex'=>'index','name'=>'查找用户','action' =>'User'),
			          3 => array('tagid'=>$tag,'tagIndex'=>'statistics','name'=>'用户统计','action' =>'User')
			          );
		}
		else if($tag==1)//活动管理
		{
			$menu = array(
          0 => array('tagid'=>$tag,'tagIndex'=>'index','name' => '活动列表','action' =>'Activity'),
          1 => array('tagid'=>$tag,'tagIndex'=>'add','name'=>'发起活动','action' =>'Activity'),
          2 => array('tagid'=>$tag,'tagIndex'=>'find','name'=>'查找活动','action' =>'Activity'),
          3 => array('tagid'=>$tag,'tagIndex'=>'statistics','name'=>'活动统计','action' =>'Activity')
          );
		}
		$this->assign('tag',$tag);
		$this->assign('menu',$menu);
		$this->display();
	}
    // 后台首页 查看系统信息
    public function main() {
    	$tag = -1;
    	$tag = $_GET['tag'];
    	if($tag==null)
    	{
    		$tag = -1;
    	}
    	if($tag==0)//用户管理
    	{
    		U('User/index','',true);
    	}
    	else if($tag==1) //活动管理
    	{
    		U('Activity/index','',true);
    	}
    	else{
	       $info = array(
	            '操作系统'=>PHP_OS,
	            '运行环境'=>$_SERVER["SERVER_SOFTWARE"],
	            'PHP运行方式'=>php_sapi_name(),
	            '上传附件限制'=>ini_get('upload_max_filesize'),
	            '执行时间限制'=>ini_get('max_execution_time').'秒',
	            '服务器时间'=>date("Y年n月j日 H:i:s"),
	            '北京时间'=>gmdate("Y年n月j日 H:i:s",time()+8*3600),
	            '服务器域名/IP'=>$_SERVER['SERVER_NAME'].' [ '.gethostbyname($_SERVER['SERVER_NAME']).' ]',
	            '剩余空间'=>round((@disk_free_space(".")/(1024*1024)),2).'M',
	            'register_globals'=>get_cfg_var("register_globals")=="1" ? "ON" : "OFF",
	            'magic_quotes_gpc'=>(1===get_magic_quotes_gpc())?'YES':'NO',
	            'magic_quotes_runtime'=>(1===get_magic_quotes_runtime())?'YES':'NO',
	            );
	        $this->assign('info',$info);
	        $this->display();
    	}

    }
    //头部
    public function header()
    {
    	$this->display();
    }
    // 更换密码
    public function changePwd()
    {
        //对表单提交处理进行处理或者增加非表单数据
		if(md5($_POST['verify'])	!= $_SESSION['verify']) {
			$this->error('验证码错误！');
		}
		$map	=	array();
        $map['password']= $_POST['oldpassword'];
        if(isset($_POST['account'])) {
            $map['account']	 =	 $_POST['account'];
        }elseif(isset($_SESSION['userid'])) {
            $map['id']		=	$_SESSION['userid'];
        }
        //检查用户
        $User    =   M("User");
        if(!$User->where($map)->field('id')->find()) {
            $this->error('旧密码不符或者用户名错误！');
        }else {
			$User->password	= $_POST['password'];
			$User->save();
			$this->success('密码修改成功！');
         }
    }
    //生成验证码
	public function verify()
    {
		$type	 =	 isset($_GET['type'])?$_GET['type']:'gif';
        import("@.ORG.Image");
        Image::buildImageVerify(4,1,$type);
    }
    // 修改资料
	public function change() { 
		$User	 =	 D("User");
		if(!$User->create()) {
			$this->error($User->getError());
		}
		$result	=	$User->save();
		if(false !== $result) {
			$this->success('资料修改成功！');
		}else{
			$this->error('资料修改失败!');
		}
	}
    public function profile() {
		$User	 =	 M("User");
		$vo	=	$User->getById($_SESSION['userid']);
		$this->assign('vo',$vo);
		$this->display();
	}
	public function logout()
	{
		$userservice = new UserService();
		$userservice->unregisterLogin();
		U('Public/login','',true);
	}
}