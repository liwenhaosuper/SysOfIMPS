<?php
require_once './BaseService.php';

interface BaseSubject
{
	public function attach(BaseService $service);
	public function detach(BaseService $service);
	public function notifyObservers();
}