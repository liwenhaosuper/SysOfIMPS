<?php
class AreaModel extends Model{
	//var $tableName = 'network';
    function getNetworkList($pid='0') {
		return $this->_MakeTree($pid);
	}
	
	function _MakeTree($pid,$level='0') {
		$result = $this->where('pid='.$pid)->findall();
		if($result){
			foreach ($result as $key => $value){
				$id = $value['id'];
				$list[$id]['id']    = $value['id'];
				$list[$id]['pid']    = $value['pid'];
				$list[$id]['title']  = $value['name'];
				$list[$id]['level']  = $level;
				$list[$id]['child'] = $this->_MakeTree($value['id'],$level+1);
			}
		}
		return $list;
	}
}