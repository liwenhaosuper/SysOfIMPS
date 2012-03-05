<?php
// +----------------------------------------------------------------------
// | ThinkPHP
// +----------------------------------------------------------------------
// | Copyright (c) 2007 http://thinkphp.cn All rights reserved.
// +----------------------------------------------------------------------

//日期格式化
/*
function toDate($time, $format = 'Y-m-d H:i:s') {
	if (empty ( $time )) {
		return '';
	}
	$format = str_replace ( '#', ':', $format );
	return date ($format, $time );
}*/


// 计算字符长度，含中文计算处理
function iLength($str,$chtml=0)
{
	$num   =   0;
	$s   =   false;
	if ($chtml=1){
	$str=strip_tags($str);//去掉html
	}
	$len   =   strlen($str);
	for($i   =   0;$i   <   $len;$i++)   {
		if   ($str{$i}=="\t"   ||   $str{$i}=="\r"   ||   $str{$i}=="\n")continue;
		if   (ord($str{$i})   >   127)   {
			$s   =   !$s;
			if   ($s   ==   false)$num++;
			continue;
		}
		$num++;
	}
	return   $num;
}

function request_string($str) {
   //得到安全的数据
	  $nstr=Input::getVar(trim($str));
	  return $nstr;
  }

function removehtml($str) {
   //去掉html
      $string=Input::getVar($str);
      $string=preg_replace("/&nbsp;/i", '', $str);
	  $nstr=Input::deleteHtmlTags(trim(strip_tags($string)));
	  return $nstr;
  }
function getUserId(){
	return $_SESSION['userid'];
}
function valididvalue($str){
//取到正确ID值
    $vstr=request_string($str);
	if(str_type($vstr)!="f" || empty($vstr) ){
	 return false;
	}else{
	 return $vstr;	
	}
}

/*  检测字符串类型   */
//通过返回字符判断类型
function str_type( $str )
{
    $a=ereg('['.chr(0xa1).'-'.chr(0xff).']', $str); //匹配汉字
    $b=ereg('[0-9]', $str); //匹配数字
    $c=ereg('[a-zA-Z]', $str); //匹配英文
    
	if($a && $b && $c)
		return "a"; //汉字数字英文的混合字符串
    elseif($a && $b && !$c)
		return "b"; //汉字数字的混合字符串
    elseif($a && !$b && $c)
		return "c"; //汉字英文的混合字符串
    elseif(!$a && $b && $c)
		return "d"; //数字英文的混合字符串
    elseif($a && !$b && !$c)
		return "e"; //纯汉字
    elseif(!$a && $b && !$c)
		return "f"; //纯数字
    elseif(!$a && !$b && $c)
		return "g"; //纯英文
    else
		return "h"; //特殊字符
}

?>