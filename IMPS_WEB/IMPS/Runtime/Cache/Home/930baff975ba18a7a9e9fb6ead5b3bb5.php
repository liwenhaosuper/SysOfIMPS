<?php if (!defined('THINK_PATH')) exit();?> 
 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
           "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>个人主页</title>
    <link href="/IMPS/Tpl/default/common/css/frame.css" rel="stylesheet" type="text/css" />
    <link href="/IMPS/Tpl/default/common/css/home-main.css" rel="stylesheet" type="text/css" />
    <link href="/IMPS/Tpl/default/common/js/tbox/box.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/jquery.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/common.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/jquery.form.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/tbox/box.js"></script>
<!--          <script type="text/javascript" src="/IMPS/Tpl/default/common/js/Base.js"></script>        
     <script type="text/javascript" src="/IMPS/Tpl/default/common/js/prototype.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/mootools.js"></script>
    <script type="text/javascript" src="/IMPS/Tpl/default/common/js/Ajax/ThinkAjax.js"></script> -->
  </head>
  <body>
      <script type="text/javascript">
           $(function(){
                   $('#postStatusForm').ajaxForm({
                           beforeSubmit: checkStatusForm,
                           success:              completeAddStatus,
                           dataType:    'json'
                   });
                function checkStatusForm(){
                        $('#result').html('').show();
                        if(''==$.trim($('#status-box').val())){
                                $('#result').html('内容不能为空哦！').show();
                                return false;
                        }
                }
                //添加状态成功
                function completeAddStatus(data){
                        data = data.data;
                        var res = '<div class=\"feed-item\" id=\"feed-item'+data.stateId+'\">'
                        +'<a class=\"avatar\" href=\"#\"><img src=\"/IMPS/Tpl/default/common/images/common/logo.png\" alt=\"avatar\"></a>' 
                        +'<a class=\"author\" href=\"#\">'+data.username+'</a><span class=\"feed-content\">'+data.msg+'</span>'
                        +'<span class=\"stamp\"><a>'+data.time+'</a></span><span class=\"operation\">'
                        +'<a href=\"javascript:void(0)\" onclick=\"deleteState(\'/IMPS/index.php/Home/State/deleteState\','+data.stateId+')\" >删除</a>'
                        +'<a href=\"#\">回复</a></span> </div>';
                        $('#feed-wrap').prepend(res);           
                }
           });
           //删除状态
         function deleteState(url,id)
         {
        	 $.post(url,{stateId:id},function(txt){
        		if (txt.status)
          		{
        			//$('feed-wrap').removeChild($('feed-item'+id));
        			//hide it
        			document.getElementById('feed-item'+id).innerHTML="";
          		}
        		
        	 },'json');
         }
           //添加评论
         function beforeStateComment(parentid)
         {
        	   if(parentid.innerHTML==""){
        		   var prtnode = parentid.parentNode;
        		   var txtarea = document.getElementById('state-comment'+parentid.id);
        	       parentid.innerHTML=" <textarea name=\"statecomment\" id=\"state-comment"+parentid.id+"\" placeholder=\"说说\""
                   +"style=\"width:99.3%;_width:99%; height:50px;padding:5px 0;resize:y;\"></textarea>"
                   +"<button class=\"tweet-button disabled\" type=\"submit\""
                   +"onclick=\"addStateComment(\'/IMPS/index.php/Home/State/addComment\',\'"+prtnode.id+"\',"+"\'state-comment"+parentid.id+"\',-1);\">评论</button>";
        	   }
        	   else{
        		   parentid.innerHTML="";
        	   }
         }
         function addStateComment(url,id,msg,parentid)
         {
        	 var ms = document.getElementById(msg);
        	 var stateid = new String(id);
        	 stateid = stateid.substr(9);
        	 //alert(url+":"+stateid+":"+":"+parentid+":"+ms.value);
        	 $.post(url,{stateId:stateid,parent:parentid,msg:ms.value},function(res){
        		if(res.status){
        			var addres = 
                    "<div id=\"comment"+res.data.id+"\" class=\"q_con\">"
                    +"<a class=\"author\" href=\"#\">"+res.data.friendName+"</a>"
                    +"<span class=\"feed-content\">"+ms.value+"</php></span>"
                    +"<span class=\"stamp\"><a>"+res.data.time+"</a></span>"
                    +"<a href=\"#\">回复</a>"
                    +"<div id=\"fill_comment"+res.data.id+"\"></div>"
                    +"</div>";
                    var txtar = document.getElementById('fill_state_comment'+stateid);
                    txtar.innerHTML="";
                    $('#comment_quote'+stateid).prepend(addres);      	
        		} 
        	 },'json');
       }
         function test(){
        	 alert("test...");
         }
    </script>
  
    <div id="home-wrap">
      <div id="top" style="height:auto;">
        <div id="navigation">
          <div class="menu"><img src="/IMPS/Tpl/default/common/images/common/logo.png" style="padding-right: 20px;"/></div>
          <div class="menu"><a href="<?php echo U('Home/User/index');?>">首页</a></div>
          <div class="menu"><a href="#">个人主页</a></div>
          <div class="menu"><a href="<?php echo U('Home/Friends/index');?>">好友</a></div>
          <div class="menu"><a href="<?php echo U('Activity/index');?>">活动</a></div>
          <div class="menu"><a href="<?php echo U('Album/index');?>">相册</a></div>
          <div class="menu"><a href="#">我的状态</a></div>
          <div class="menu"><a href="#">留言板</a></div> 
          <div class="menu" id="search-top">
            <div><input type="text" autocomplete="off" class="searchtextbox gray" name="textfield" value="搜索好友、活动" /></div>
            <div class="search_go"><a class="go" href="javascript:;">GO</a></div>
          </div> 
          <div class="menu" id="logout"><a href="<?php echo U('User/logout');?>">登出</a></div>    
        </div>
      </div>

      <div style="margin: 0 auto; padding-top: 20px;">
        <table id="home-main">
          <tr>
            <td id="frame-left">
              <div id="home-main-status">               
                <div id="status-control" class="tweet-box condensed post clearfix">
              <div class="status-content">
                <form id="postStatusForm" class="body form_container clearfix" action="<?php echo U('Home/State/addState');?>" method="post" accept-charset="utf-8">
                   <div class="">
                       <h2>正在发生什么了？</h2>
                   </div>
                   <div class="text-area twttr-editor">
                       <textarea name="status" id="status-box" placeholder="说点什么吧"
                       style="width:99.3%;_width:99%; height:50px;padding:5px 0;"></textarea>
                       <div id="result" style="font-family:微软雅黑,Tahoma;letter-spacing:2px;color:#f00"></div>
                   </div>
                   <div>
                   </div>
                   <div id="publish_type_content_before">
                        <span>添加：</span><a href="javascript:void(0)" target_set="content_publish" 
                        onclick="ui.emotions(this)"class="a52"><img class="icon_add_face_d" 
        				src="/IMPS/Tpl/default/common/images/common/zw_img.gif" />表情</a> 
        				<a href="javascript:void(0)" onclick="state.plugin.image.click(169)"
        				class="a52"><img class="icon_add_img_d"
        				src="/IMPS/Tpl/default/common/images/common/zw_img.gif" />图片</a> 
        				<a href="javascript:void(0)" onclick="state.plugin.video.click(221)"
        				class="a52"><img class="icon_add_video_d"
        				src="/IMPS/Tpl/default/common/images/common/zw_img.gif" />视频</a>
        				<a href="javascript:void(0)" onclick="state.plugin.music.click(271)" 
        				class="a52"><img class="icon_add_music_d" 
        				src="/IMPS/Tpl/default/common/images/common/zw_img.gif" />语音/音乐</a>
                    
                   </div>
                   <div class="clearfix"><button class="tweet-button disabled" type="submit">发布 &rarr;</button></div>
                </form>
               </div>
               <div class="result" id="result"></div>
           </div>               
              </div>
              
       <div id="home-main-feed">
       <div id="feed-wrap">
        <?php $statuscnt = count($statuslist);for($i=0;$i<$statuscnt;$i++){ ?>
        <div id=<?php echo 'feed-item'.$statuslist[$i]['state']['stateId'] ?> class="feed-item">
           <a class="avatar" href="#"><img src="/IMPS/Tpl/default/common/images/common/logo.png" alt="avatar"></a>
           <a class="author" href="#"><?php echo $statuslist[$i]['state']['username'] ?></a>
           <span class="feed-content"><?php echo $statuslist[$i]['state']['msg'] ?></span>
           <span class="stamp"><a><?php echo $statuslist[$i]['state']['time'] ?></a></span>
           <span class="operation">
               <?php if($statuslist[$i]['usertype']){ ?>
               <a href="javascript:void(0)" onclick=<?php echo "\"deleteState('".U('Home/State/deleteState')."',".
               $statuslist[$i]['state']['stateId'].")"."\"" ?> >删除</a>
               <a href="javascript:void(0)" onclick=<?php echo "\"beforeStateComment(fill_state_comment".$statuslist[$i]['state']['stateId'].")\"" ?>>回复</a>
               <?php }else{ ?>
               <a href="#">回复</a>
               <?php } ?>
            </span>
            <div id=<?php echo 'fill_state_comment'.$statuslist[$i]['state']['stateId'] ?> ></div>
            <!-- 评论回复 -->
            <?php $cmtcnt = count($statuslist[$i]['comments']);if($cmtcnt>0){ ?>
                	评论
               <?php } ?>
               <div class="comment_quote" id=<?php echo 'comment_quote'.$statuslist[$i]['state']['stateId'] ?> >
               <?php for($j=0;$j<$cmtcnt;$j++){ ?>
                <div id=<?php echo 'comment'.$statuslist[$i]['comments'][$j]['id'] ?> class="q_con">
                <a class="author" href="#"><?php echo $statuslist[$i]['comments'][$j]['friendName'] ?></a>
                  <span class="feed-content"><?php echo $statuslist[$i]['comments'][$j]['content'] ?></span>
                  <span class="stamp"><a><?php echo $statuslist[$i]['comments'][$j]['time'] ?></a></span>
                   <a href="#">回复</a>
                   <div id=<?php echo 'fill_comment'.$statuslist[$i]['comments'][$j]['id'] ?> ></div>
                </div>
              <?php } ?>
              
              </div>
              </div>
        </div>
       <?php } ?>
        </div>
                
              </div>
            </td>
            <td id="frame-right">
              <div id="home-main-sidebar">
              
              <div id="sidebar">
                         <div id="profile">
                        <a id="user-avatar" href="#"><img src="/IMPS/Tpl/default/common/images/common/logo.png" alt="user-avatar"></a>
                        <span id="user-name"><a href="#"><?php echo $_SESSION['username'] ?></a></span>
                         </div>
                         <span id="myneweststate"><a href="#"><?php echo '我是'.$_SESSION['username'].'我怕谁？' ?></a></span>
                         <div id="attention">
                        <ul id="attention-list">
                                <li>好友</li><li><a href="#">1</a></li>
                                <li>关注</li><li><a href="#">2</a></li>
                                <li>粉丝</li><li><a href="#">3</a></li>
                        </ul>
                        </div>
                        <div id="whatshot">
                        <ul id="wh-list">
                                <li>OS - Dec. 18, 2011</li>
                                <li>Nework - Dec. 24, 2011</li>
                                <li>OO - Dec. 10, 2011</li>
                                <li>aaaaaaaaaaaaaaaaa</li>
                                <li>OS - Dec. 18, 2011</li>
                        </ul>
                        </div>
                 </div>
              
            </div>
           </td>
          </tr>
        </table>
   </div>
  </div>
 </body>
</html>