<?php
/**
 * PublicAction class
 * defines the public interface for the subsystem
 * @author liwenhaosuper
 */
  require_once 'Common/Service/ValidationService.class.php';
  require_once 'Common/Service/MailService.class.php';
  require_once 'Common/Service/UserService.class.php';

class PublicAction extends Action
{
	public function  _initialize()
	{
		if(!empty($_SESSION['userid'])&&!empty($_SESSION['username'])){
			if($_SESSION['usertype']=='user'){
			    U('User/index','',true);	
			}
			else if($_SESSION['usertype']=='admin'){
				 U('../Admin/Public/login','',true);	
			}
		}
	}
	/**
	 * 
	 * 查看用户名是否存在
	 */
	public function checkUserName()
	{
		if(!empty($_POST['username']))
		{
			$res = D('Home.User')->checkUser($_POST['username']);
			if($res==true){
				$this->error('用户名已存在');
			}
			else{
			    $this->success('用户名可用');	
			}
		}
		else{
			$this->error('用户名不能为空');
		}
	}
	
	
	/**
	 * prepare work before showing the login page
	 * 
	 */
	public function login(){
		//TODO: retrieve data for showing
		$this->display();	
	}
	/**
	 * do the login request by post method
	 * @_POST data includes: username/password
	 */
	public function doLogin(){
		$data = array();
		$data['username'] = $_POST["username"];
		$data['password'] = $_POST["password"];
		$user = D('User','Home')->getLocalUser($data['username'],$data['password']);
		if($user['username']&&$user['userid']){//login success
			$validates = D('Home.Validation');
			$status = $validates->getStatus($user['userid']);
			if($status==0){
				$this->assign('jumpUrl',U('Home/Public/login'));
			    $this->error('该账户已经注册，但是没有通过邮箱验证。通过邮箱验证后即可激活帐号');
			}else if($status==1){
				$userservice = new UserService();
				$user['usertype'] = 'user';
				if($userservice->registerLogin($user,false))
				{
					U('Home/User/index','',true);
				}
				else{
					$this->assign('jumpUrl',U('Home/Public/login'));
			        $this->error('用户名或密码错误');
				}
			}else{
				$this->assign('jumpUrl',U('Home/Index/index'));
			    $this->error('非法账户！');
			}
			
		}
		else{//login fail
			$this->assign('jumpUrl',U('Home/Public/login'));
			$this->error('用户名或密码错误');
		}
	}
	/**
	 * register for user
	 * 
	 */
	public function register(){
		$this->display();
	}
	/**
	 * do register work by post method
	 * @_POST data includes:
	 *    username
	 *    password
	 *    email
	 *    gender
	 */	
	public function doRegister(){
		$resUser = array();
		$resUser['username'] =$_POST['username'];
		$resUser['password']=$_POST['password'];
		$resUser['email']=$_POST['email'];
		$resUser['gender'] = intval($_POST['gender'])==0?'M':'F';
		$result = D('Home.User')->register(
		    $resUser['username'],$resUser['password'],$resUser['email'],$resUser['gender']
		    );
		if($result){
		    $validateCode = $this->activate($result, $_POST['email']);	
		    if(!isset($validateCode)||!$validateCode)
		    {
		    	$this->assign('jumpUrl',U('Home/Public/register'));
			    $this->error('注册失败！原因：激活邮件发送失败。请返回重新填写注册信息');
		    }
		    else{
		       //信息添加进validation
		        $validation = M('validation');
		        $validateData = array();
		        $validateData['userid'] = $result;
		        $validateData['code'] = $validateCode;
		        $validateData['status'] = 0;
		        $validation->add($validateData);
		        //跳转
		        $this->assign('jumpUrl',U('Home/Index/index'));
		        $this->success('注册成功！注册码已发送至您邮箱，请通过注册码激活您的账户！');
		    }	    
		}
		else{
			$this->assign('jumpUrl',U('Home/Public/register'));
			$this->error('注册失败！原因：用户名已存在。请返回重新填写注册信息');
		}
	}
	/**
	 * 
	 * activate an account by sending an email
	 * @userid the user id that already registered 
	 * @username ...
	 * @email the email address to send email to 
	 * @returned validation id
	 */
	public function activate($userid,$email,$invite = ''){
		$service = new ValidationService();
		$activate_url = $service->addValidation(
		    $userid, 'http://localhost:8080'.U('Home/Public/doActivate'), 'register_activate', serialize($invite));
		if($invite){
			$this->assign('invite', $invite);
		}
		$this->assign('url',$activate_url);
		
		$body = <<<EOD
感谢您注册IMPS!<br>

请马上点击以下注册确认链接，激活您的帐号！<br>

<a href="$activate_url" target='_blank'>$activate_url</a><br/>

如果通过点击以上链接无法访问，请将该网址复制并粘贴至新的浏览器窗口中。<br/>

如果你错误地收到了此电子邮件，你无需执行任何操作来取消帐号！此帐号将不会启动。
EOD;

		
	// 发送邮件
		global $ts;
		$mailService = new MailService();
		$email_sent = $mailService->send_email($email, "激活{$ts['site']['site_name']}帐号",$body);
		
		// 渲染输出
		if ($email_sent) {
			$email_info = explode("@", $email);
			switch ($email_info[1]) {
				case "qq.com"    : $email_url = "mail.qq.com";break;
				case "163.com"   : $email_url = "mail.163.com";break;
				case "126.com"   : $email_url = "mail.126.com";break;
				case "gmail.com" : $email_url = "mail.google.com";break;
				default          : $email_url = "mail.".$email_info[1];
			}
			$res = substr($activate_url, strripos($activate_url,"validationcode=")+15);						
			return $res;
		}else {
			return false;
			//$this->assign('jumpUrl', U('Home/Public/register'));
			//$this->error('抱歉: 邮件发送失败，请稍后重试');
		}
	}
	/**
	 * check the validation
	 */
	public function doActivate(){
		$validationService = new ValidationService();
		$invite = $validationService->getValidation();
	    if ($invite==false) {	    	
			$this->assign('jumpUrl',U('Home/Public/register'));
        	$this->error('抱歉: 链接错误，请重新注册');
		}else{
			//验证
			$vres['userid'] = $invite['userid'];
			$vres['code'] = $invite['validationcode'];
			$validatemodel = D('Home.Validation');
			$res = $validatemodel->where($vres)->find();
			if($res['id']&&$res['userid']){
				$res['status'] = 1;
				$validatemodel->save($res);
				$this->assign('jumpUrl',U('Home/Index/index'));
			    $this->success('恭喜！邮箱验证成功，请登录进站！');
			}
			else{
				$this->assign('jumpUrl',U('Home/Public/register'));
			    $this->error('错误！邮箱验证失败，请重新注册！'.$res);
			}
			
		}    
	}
	
	
	/**
	 * 
	 * empty operations here
	 */
	public function _empty(){
		$this->display();
	}
} 