<?php
require_once './BaseSubject.class.php';

class NotifyManager implements BaseSubject
{
	private $_services;
	
	public function __construct()
	{
		$this->_services = array();
	}
	
	public function attach(BaseService $service)
	{
		return array_push($this->_services, $service);
	}
	public function detach(BaseService $service)
	{
		$index = array_search($service, $this->_services);
		if($index==false||!array_key_exists($index, $this->_services))
		{
			return false;
		}
		unset($this->_services[$index]);
		return true;
	}
	public function notifyObservers()
	{
		if(!is_array($this->_services))
		{
			return false;
		}
		foreach($this->_services as $service)
        {
        	$service->update();
        }
	}

}