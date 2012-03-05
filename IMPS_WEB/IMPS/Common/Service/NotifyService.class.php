<?php
require_once './BaseService.php';

class NotifyService implements BaseService
{
	private $userid;
	private $container;
	public function update()
	{
		$info = new UpdateInfo();
		$data = $info->getData();
		array_push($this->contianer,$data);
	}
	
}