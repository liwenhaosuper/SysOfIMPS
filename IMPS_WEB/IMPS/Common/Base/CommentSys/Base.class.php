<?php
 require_once 'Common/Base/Base/Base.class.php';
 
define("tIMAGE",0,true);
define("tVIDEO",1,true);
define("tAUDIO",2,true);
define("tFILE",3,true);

class Accessory
{
	public $accessoryid;
	public $name;
	public $type;
	public $path;
}
 
/**
 * 
 * 评论类
 * @author liwenhaosuper
 *
 */
abstract class Comment{
	protected $_model;
	abstract function save();
	abstract function add();
	abstract function build();
}
class StateComment extends Comment{
   public $id;//                   bigint not null auto_increment,
   public $stateId;//              bigint,
   public $content;//              text,
   public $time;//                 timestamp not null default CURRENT_TIMESTAMP,
   public $isLeaf;//               bool default true,
   public $friendName;//           varchar(20),
   public $parent;//               bigint,
   public function save();
   public function add();
   public function build();
}
class ActivityComment extends Comment{
   public $id        ;          
   public $act_id     ;        
   public $title       ;      
   public $msg          ;    
   public $time          ; 
   public $friendName     ;  
   public $isLeaf          ;    
   public $parent           ;   
   public function save();
   public function add();
   public function build();
}
interface CommentBuilder{
	public function build($node); 
}
class StateCommentBuilder implements CommentBuilder
{
	public function build($node){
		
	}
}
class CommentSys{
	//public function 
}
/**
 * 
 * 评论节点
 * @author liwenhaosuper
 *
 */
abstract class CommentNode
{
	protected $_mnode;//info
	abstract function add(Comment $obj);
	abstract function remove(Comment $obj);
	abstract function getChildren();
	abstract function setParent(Comment $obj);
	abstract function getParent();
	abstract function save();
}
class BranchComment extends CommentNode{
	
	private $_children;
	private $_parent;
	public function __construct(Comment $node){
		$this->_mnode = $node;
		$this->_children = array();
	}
	public function add(Comment $obj){
		$obj->setParent($this);
		$obj->$isLeaf = false;
		array_push($this->_children,$obj);
	}
	public function remove(Comment $obj){
		$index = array_search($obj, $this->_children);
		if($index==false||!array_key_exists($index, $this->_children)){
			return false;
		}
		unset($this->_children[$index]);
		return true;
	}
	public function getChildren(){
		return $this->_children;
	}
	public function setParent(Comment $obj){
		$this->_parent = $obj;
	}
	public function getParent(){
		return $this->_parent;
	}
	public function save(){
		if(!is_array($this->_children))
		{
			return false;
		}
		foreach($this->_children as $comment)
        {
        	$comment->save();
        }
	}
}


