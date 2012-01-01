<?php
/**
 * 
 * index action for initializing some of the user specified framework
 * @author liwenhaosuper
 *
 */

class IndexAction extends Action
{
	/**
	 * URL转发至public action 登录或转至首页
	 * 
	 */
	public function index(){
		if (isset($_SESSION['username'])&&isset($_SESSION['userid'])&&isset($_SESSION['usertype']))
		{
			//administrator
			if($_SESSION['usertype']=='admin')
			{
				U('../Admin/Public/login','',true);
			}
			//user
			else{
				U('User/index','',true);
			}
		}
        else{
        	// log in
        	U('Public/login', '', true);
        }
		
	}
}