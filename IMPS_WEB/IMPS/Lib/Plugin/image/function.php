<?php
function getSaveTempPath() {
	$savePath = SITE_PATH . '/data/uploads/temp';
	if (! file_exists ( $savePath ))
		mk_dir ( $savePath );
	return $savePath;
}