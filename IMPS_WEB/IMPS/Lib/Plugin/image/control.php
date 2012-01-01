<?php 
include_once 'function.php';

switch ($do_type){
	case 'before_publish': //发布前检验
    	if( $_FILES['pic'] ){
    		
    		if($_FILES['pic']['size'] > 2*1024*1024 ){
	        	$result['boolen']    = 0;
	        	$result['message']   = '图片太大，不能超过2M';
	        	exit( json_encode( $result ) );
    		}

    	    $imageInfo = getimagesize($_FILES['pic']['tmp_name']);
            $imageType = strtolower(substr(image_type_to_extension($imageInfo[2]),1));
            if( !in_array($imageType,array('jpg','gif','png','jpeg')) ) {
                $result['boolen']    = 0;
                $result['message']   = '图片格式错误';
                exit( json_encode( $result ) );
	        }

    		//执行上传操作
    		$savePath =  getSaveTempPath();
            //$filename = md5( time().$this->mid ).'.'.substr($_FILES['pic']['name'],strpos($_FILES['pic']['name'],'.')+1);
            $filename = md5( time().$this->mid ).'.'.$imageType;
	    	if(@copy($_FILES['pic']['tmp_name'], $savePath.'/'.$filename) || @move_uploaded_file($_FILES['pic']['tmp_name'], $savePath.'/'.$filename)) 
	        {
	        	$result['boolen']    = 1;
	        	$result['type_data'] = 'temp/'.$filename;
	        	$result['file_name'] = $filename;
	        	$result['picurl']    = __UPLOAD__.'/temp/'.$filename;
	        } else {
	        	$result['boolen']    = 0;
	        	$result['message']   = '上传失败';
	        }
    	}else{
        	$result['boolen']    = 0;
        	$result['message']   = '上传失败';
    	}
    	exit( json_encode( $result ) );		
		break;
		
	case 'publish':  //发布处理
			if(!file_exists($type_data)){
				$type_data = '/data/uploads/'.$type_data;
			}else{
				$type_data = preg_replace("/^\./",'',$type_data);
			}
	 		preg_match('|\.(\w+)$|', basename($type_data), $ext);
	 		$fileext  = strtolower($ext[1]);
	 		$filename = md5($type_data) . '.' . $fileext;
	 		$savePath = SITE_PATH.'/data/uploads/miniblog';
        	if( !file_exists( $savePath ) ) mk_dir( $savePath  );
	 		$thumbname = substr( $filename , 0 , strpos( $filename,'.' ) ).'_small.jpg';
	 		$thumbmiddlename = substr( $filename , 0 , strpos( $filename,'.' ) ).'_middle.jpg';
	 		if( copy( SITE_PATH.$type_data , $savePath.'/'.$filename) ){
	 				include_once SITE_PATH.'/addons/libs/Image.class.php';
					Image::thumb( $savePath.'/'.$filename , $savePath.'/'.$thumbname , '' , 120 , 120 );
					Image::thumb( $savePath.'/'.$filename , $savePath.'/'.$thumbmiddlename , '' , 465 ,'auto' );
		        	$typedata['thumburl'] = 'miniblog/'.$thumbname;
		        	$typedata['thumbmiddleurl'] = ($fileext=='gif')?'miniblog/'.$filename:'miniblog/'.$thumbmiddlename;
		        	$typedata['picurl']   = 'miniblog/'.$filename;
	 		}
		break;
		
	case 'after_publish': //发布完成后的处理

		break;
}