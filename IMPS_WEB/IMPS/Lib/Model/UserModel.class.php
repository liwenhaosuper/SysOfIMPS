
<?php
/**
 * 
 * UserModel class
 * @author liwenhaosuper
 *
 */
class UserModel extends Model
{
	protected $tableName = 'user';

	// 自动验证设置
	protected $_validate	 =	 array(
		array('username','','用户名已存在',0,'unique','add'),
		array('email','email','邮箱格式错误',2),
		);
	
	/**
	 * 根据查询条件查询用户
	 * 
	 * @param array|string $map          查询条件
	 * @param string       $field		   字段
	 * @param int 		   $limit		   限制条数
	 * @param string 	   $order		   结果排序
	 * @param boolean 	   $is_find_page 是否分页
	 * @return array
	 */
	public function getUserByMap($map = array(), $field = '*', $limit = '', $order = '', $is_find_page = true) {
		if ($is_find_page) {
			return $this->where($map)->field($field)->order($order)->findPage($limit);
		}else {
			return $this->where($map)->field($field)->order($order)->limit($limit)->findAll();
		}
	}
	
	/**
	 * 获取用户基本信息字段
	 * 
	 * @param string $module 字段类别
	 * @param string $userName 用户名
	 * @return array
	 */
	public function getDataField($module = "",$userName=""){
		if(isset($userName))
		{
			$list = $this->table(C('DB_PREFIX').'user')->where("username={$userName}")->findall();
		}
		else{
			$list = $this->table(C('DB_PREFIX').'user')->findall();
		}       
        foreach ($list as $value){
            $data[$value["module"]][$value["fieldkey"]] = $value["fieldname"];
        }
	    return ($module)?$data[$module]:$data;
	}
	/**
	 * 删除用户
	 * @param string $userName 删除的用户名
	 * @return true 成功 false 失败
	 */
	public function  deleteUser($userName){
		$userId = getDataField("userid","username");
		if(isset($userId)){
			$map["userid"] = $userid;
			$result = M('user')->where($map)->delete();
			if($result>0){
				//TODO: 还需要删除一些东西？
				 return true;
			}			   
		}
		return false;
	}
	

	
/**
	 * 根据标示符(userid或username)获取用户信息
	 * 
	 * 
	 * @param string|int $identifier      标示符内容
	 * @param string     $identifier_type 标示符类型. (userId, userName之一)
	 */
	public function getUserByIdentifier($identifier, $identifier_type = "username")
	{	
		$map = array();
		if ($identifier_type == "userid")
			$map["userid"] = intval($identifier);
		else if($identifier_type == "username"){
			$map[$identifier_type] = $identifier;
		}
		else return false;			
		if ($user = $this->where($map)->find()) {
		    return $user;
		}
		else return false;
	}
	
	/**
	 * 登录，返回用户信息
	 */
	public function getLocalUser($userName,$password){
		$resUser = array();
		$resUser = $this->getUserByIdentifier($userName,"username");
		if($resUser["username"])
		{
			if($password!=$resUser["password"]){
				return false;
			}
			return $resUser;
		}
		return false;
	}
	/**
	 * 添加用户
	 * 返回 userid或false
	 */
	public function register($username,$password,$email,$gender){
		if($this->checkUser($username)==true){
			return false;
		}
		$user = array();
		$user['username'] = $username;
		$user['password'] = $password;
		$user['email'] = $email;
		$user['gender'] = $gender;
		$result = $this->add($user);
		if($result){
			return $result;
		}
		return false;
	}
	/**
	 * 判断用户名是否存在
	 */
	public function checkUser($username){
		$map = array();
		$map['username'] = $username;
		$user = $this->where($map)->find();
		if($user['username']&&$user['username']!=null){
			return true;
		}
		else{
			return false;
		}
	}
	//获取好友列表/fans列表/follow列表
	public function getFriendList($userid,$table = 'Friendrelate')
	{
		$map = array();
		$map['usrid'] = $userid;
		$user = $this->where($map)->find();
		if($user['username']&&$user['username']!=null){
			$Friendrelate = M($table);
			$Friendrelate->where('userid='.$userid)->find();
			$result = array();
			$count = count($Friendrelate);
			for($i = 0;$i<$count;$i++){
				$result[$i] = $Friendrelate[$i]['use_userid'];
			}
			return $result;
		}
		else{
			return false;
		}
	}
	//查询
	public function findByKeyWord($keyword)
	{
		$map = array();
		$where['username'] = array('like','%'.$keyword.'%');
		$where['userid'] = array('like','%'.$keyword.'%');
		$where['email'] = array('like','%'.$keyword.'%');
		$where['_logic'] = 'or';
		$map = $this->where($where)->findAll();
		return $map;
	}

}