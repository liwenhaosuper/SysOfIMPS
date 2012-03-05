<?php
require_once 'Common/Base/CommentSys/Base.class.php';

class StatecommentModel extends Model implements CommentBehaviour
{
	private $mAttachBehaviour;
	public function ModifyElement($comment)
	{
		$map = array();
		$map['id'] = $comment['id'];
		$map['stateId'] = $comment['stateId'];
		$map['content'] = $comment['content'];
		$map['time'] = $comment['time'];
		$map['isLeaf'] = $comment['isLeaf'];
		$map['friendName'] =$comment['friendName'];
		$map['parent'] =$comment['parent'];
		$this->save($map);
		if(!empty($comment['$accessory'])){
			foreach ($comment['$accessory'] as $key->$value)
			{
				if(empty($value['name']))
				{
					continue;
				}
				$mAttachBehaviour = new StatecommentattachModel();
				$mAttachBehaviour->ModifyElement($value);				
			}
		}
	}
	
	public function DeleteElement($comment){
		$map['id'] = $comment['id'];
		return $this->where($map)->delete();
	}
	public function FindElement($comment){
		return $this->where($comment)->find();
	}
	public function AddElement($comment){
	    $map = array();
		$map['stateId'] = $comment['stateId'];
		$map['content'] = $comment['content'];
		$map['time'] = $comment['time'];
		$map['isLeaf'] = $comment['isLeaf'];
		$map['friendName'] =$comment['friendName'];
		$map['parent'] =$comment['parent'];
		$this->add($map);
		if(!empty($comment['$accessory']))
		{
		    foreach ($comment['$accessory'] as $key->$value)
			{
				if(empty($value['name']))
				{
					continue;
				}
				$mAttachBehaviour = new StatecommentattachModel();
				$mAttachBehaviour->AddElement($value);				
			}
		}
	}
}

/**
 * 
 * 评论附件
 * @author liwenhaosuper
 *
 */
class StatecommentattachModel extends Model implements CommentBehaviour
{
	/**
	 * param $attach: type array,including id, stateComId, type, name, path
	 *  
	 * @see CommentBehaviour::Modify()
	 */
	public function ModifyElement($attach){
	   return $this->save($attach);
	}	
	public function DeleteElement($attach){
		$map['id'] = $attach['id'];
		return $this->where($map)->delete();	
	}
	public function FindElement($attach){
		if(isset($attach['id'])){
			$map['id'] = $attach['id'];
			return $this->where($map)->find();
		}
		if(isset($attach['stateComId'])){
			$map['stateComId'] = $attach['stateComId'];
			return $this->where($map)->find();
		}
		return false;
	}
	public function AddElement($attach){
		return $this->add($attach);
	}
}