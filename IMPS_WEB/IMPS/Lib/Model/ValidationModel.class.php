<?php

class ValidationModel extends Model
{
	public function getStatus($userid)
	{
		$data['userid'] = $userid;
		$res = $this->where($data)->find();
		if(isset($res['id'])){
			if($res['status']==0){
				return 0;
			}
			else return 1;
		}else{
			return -1;
		}
		
	}
}
