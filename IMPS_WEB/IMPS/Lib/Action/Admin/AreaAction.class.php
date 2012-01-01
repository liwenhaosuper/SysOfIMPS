<?php

class AreaAction extends Action {
	//地区网络
    public function area() {
        //已选地区
        $selectedArea = explode(',',$_GET['selected']);
        if(!empty($selectedArea[0])) {
            $this->assign('selectedArea',$_GET['selected']);
        }
        $pNetwork = D('Area');
        $list = $pNetwork->getNetworkList(0);
        $this->assign('list',json_encode($list));
        $this->display();
    }
}