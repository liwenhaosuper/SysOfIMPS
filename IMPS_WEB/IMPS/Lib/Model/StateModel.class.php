<?php

class StateModel extends Model{
	
	/**
	 * 
	 * 获取状态
	 * @param bigint $userid  用户id
	 * @param int $count    状态数量
	 * return array
	 */
	function getState($userid,$count)
	{
		$result = array();
		$statemap = $this->where('userid='.$userid)->order('time desc')->limit($count)->findAll();
		if(!empty($statemap)&&$statemap!=false&&$statemap!=null)
		{
			$bkey = 0;
		    foreach ($statemap as $bvalue){
		    	$tmpuser =M('User')->where('userid='.$bvalue['userid'])->find() ;
		    	$bvalue['username'] = $tmpuser['username'];
			    $attachs = M('Stateattach')->where('stateId='.$bvalue['stateId'])->findAll();
			    $comments = M('Statecomment')->where('stateId='.$bvalue['stateId'])->order('time')->findAll();
			    $result[$bkey] = array(
			        'state' => $bvalue ,
			        'attach' => $attachs,
			        'comments' => $comments,
			        'usertype' =>1
			        
			);
			$bkey++;
		}
		}
		
		return $result;
	}
	/**
	 * 获取好友状态，按照时间排序
	 */
    public function getFriendState($friendlist,$count)
    {
    	$size = count($friendlist);
    	$total = array();
    	$totalCnt = 0;
    	$curCnt = 0;
    	for($i=0;$i<$size;$i++)
    	{
    		$result[$i] = $this->getState($friendlist[$i],$count);
    		$totalCnt+=count($result[$i]);
    		for($j=0;$j<count($result[$i]);$j++){
    			$total[$curCnt] = $result[$i][$j];
    			$curCnt++;
    		}
    	}
        for($i=0;$i<$totalCnt;$i++){
        	for($j=0;$j<$i;$j++){
        		if($total[$i]['state']['time']<$total[$j]['state']['time']){
        			$res = $total[$j];
    			    $total[$j]= $total[$i];
    			    $total[$i] = $res; 
        		}
        	}
        }
        $resarray = array();
        for($i=0;$i<$count;$i++){
        	$resarray[$i] = $total[$i];
        	$resarray[$i]['usertype'] = 0;
        }
        return $resarray;
    }
    	
}