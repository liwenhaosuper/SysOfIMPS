<?php
import('Common/common.php');
class AlbumModel extends Model
{
	protected $userid;
	public function _initialize(){
	    $this->userid = $_SESSION['userid'];
	}
	protected $_validate	 =	 array(
        array('album_name','Checkchar','相册名称不能为空或大于10个字符!',1,"callback"),
        array('album_name','','相同相册名称已经存在',1,'unique',1),
		);
 
    protected $_auto = array(
        array('create_time', 'time', 1, 'function'),
        array('userid','getUserId',self::MODEL_INSERT,'function'),
    );
  	function Checkchar()
    {
  		$cstr=$_POST['album_name'];
  		return true;
  	}
}