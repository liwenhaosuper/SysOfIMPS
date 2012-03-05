<?php
class FriendsAction extends Action {
  private $_Mfriends;
  private $_Musers;
  public function _initialize() {
    if(empty($_SESSION['userid'])||empty($_SESSION['username'])||$_SESSION['usertype']!='user'){
      U('Index/index','',true);
    }
    else{
      $this->_Mfriends = M('Friendsrelate');
      $this->_Musers = M('User');
    }
  }

  public function index() {
    $this->display();
  }

}
?>
