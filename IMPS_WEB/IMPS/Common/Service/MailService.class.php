<?php

include_once 'Common/Libs/phpmailer/class.phpmailer.php';
include_once 'Common/Libs/phpmailer/class.pop3.php';
include_once 'Common/Libs/phpmailer/class.smtp.php';

class MailService
{
    private $option = array();
	
	
	public function __construct() {
		$this->init();
	}
	
	/**
	 * 加载phpmailer, 初始化默认参数
	 */
	public function init() {
		$this->option = array(
			'email_sendtype'		=> 'SMTP',
			'email_host'			=> 'smtp.126.com',
			'email_port'			=> 25,
			'email_ssl'				=> false,
			'email_account'			=> 'impsweb@126.com',
			'email_password'		=> 'impsadmin',
			'email_sender_name'		=> 'IMPS',
			'email_sender_email'	=> 'impsweb@126.com',
			'email_reply_account'	=> 'impsweb@126.com'
		);
	}

	/**
	 * 发送邮件
	 * 
	 * @param string $sendto_email 收信人的Email
	 * @param string $subject      主题
	 * @param string $body         正文
	 * @param array  $senderInfo   发件人信息 array('email_sender_name'=>'发件人姓名', 'email_account'=>'发件人Email地址')
	 * @return boolean
	 */
	public function send_email( $sendto_email, $subject, $body, $senderInfo = '' ) {
        $mail = new PHPMailer();
		if(empty($senderInfo)) {
			$sender_name  = $this->option['email_sender_name'];
			$sender_email = $this->option['email_account'];
		}else {
			$sender_name = $senderInfo['email_sender_name'];
			$sender_email =$senderInfo['email_account'];
		}

		//$mail->Mailer = "SMTP";   //smtp发送
		$mail->IsSMTP();
		$mail->Host	= $this->option['email_host'];	// sets GMAIL as the SMTP server
		$mail->Port	= $this->option['email_port'];	// set the SMTP port

		if($this->option['email_ssl']){
			$mail->SMTPSecure	=	"ssl";	// sets the prefix to the servier  tls,ssl
		}

		$mail->SMTPAuth = true;						 // turn on SMTP authentication
		$mail->Username = $this->option['email_account'];	 // SMTP username
		$mail->Password = $this->option['email_password']; // SMTP password

		$mail->FromName	= $sender_name;  // 发件人姓名
		$mail->From		= $sender_email; // 发件人邮箱


		$mail->CharSet	= "UTF-8"; // 这里指定字符集！
		$mail->Encoding	= "base64";

		if(is_array($sendto_email)){
			foreach($sendto_email as $v){
				$mail->AddAddress($v);
			}
		}else{
			$mail->AddAddress($sendto_email);
		}
		//$mail->AddAddress('liwenhaosuper@126.com');

//		if(!empty($senderInfo)) {
//			$mail->AddReplyTo($sender_email, $sender_name); // 收件人邮箱和姓名
//		}

		//以HTML方式发送
		$mail->IsHTML(true); // send as HTML
		// 邮件主题
		$mail->Subject	 = $subject;
		// 邮件内容
		$mail->Body		 =	$body;
		$mail->AltBody	 =	"text/html";
		$mail->SMTPDebug =	false;
		return $mail->Send();
	}
}