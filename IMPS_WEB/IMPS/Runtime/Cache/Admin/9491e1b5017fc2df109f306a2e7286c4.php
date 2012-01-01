<?php if (!defined('THINK_PATH')) exit();?><script>
var json = eval(<?php echo ($list); ?>);
var arrArea = new Array();
var arrAreaTitle = new Array();
var selected = '<?php echo ($selectedarea); ?>';

//初始化
function init(){
	var selectarr = selected.split(',');
	$.each( json, function(i,n){
		$("#list_0").append(addnode(n));
		if(selectarr[0]==i){
			$("#node_"+i).attr('class','on');
			arrArea = selectarr;
			arrAreaTitle[0] = n.title;
			$("#select_0").html(n.title);
			$.each( json[i]['child'], function(j,m){
				$("#list_1").append(addnode(m));
				if(selectarr[1]==j){
					$("#node_"+j).attr('class','on');
					arrAreaTitle[1] = m.title;
					$("#select_"+m.level).html(m.title);
				}
			});
		}
	});
}

//选择下级地区
function selectarea(id,level,title){
	$("#select_"+level).html(title);
	$("#node_"+arrArea[level]).attr('class','noon');
	$("#node_"+id).attr('class','on');
	
	arrArea[level]    = id;
	arrAreaTitle[level]    = title;
	
	level++;
	var jsonchild = json;
	//var arrlength = arrArea.length;	
	for(var i=0;i<1;i++){
		if(i>=level-1){
			arrAreaTitle[level] = null;
			var num = i+1;
			$("#list_"+num).html('');
			$("#select_"+num).html('');		    	
		}
		if(i<level){
			jsonchild= jsonchild[arrArea[i]].child;
		}
	}
	arrArea.splice(level);
	arrAreaTitle.splice(level);

	var html = '';
	if(jsonchild){
		$.each( jsonchild, function(i,n){
		   		html+=addnode(n);
		}); 
		$("#list_"+level).html(html);
	}
}

function addnode(n){
	return "<li id='node_"+n.id+"'><a href='javascript:void(0);' onclick=selectarea("+n.id+","+n.level+",\'"+n.title+"\') >"+n.title+"</a></li>";
}

function save(){
	if(arrArea.length==0){
		alert('请选择地区');
	}else{
		$('#current').val(arrArea);
		$('#activityArea').val(arrAreaTitle.join(' '));
		$('#areaDialog').dialog("close");
	}
}
init();
</script>
<style>
.ullist{
	list-style-type:none;
	white-space: nowrap;
}

.ullist li{
	margin:5px;
	float:left;
}
.ullist li a{
	margin:1px;
}

.ullist .on a{
	margin:0;
	border:1px solid #3385CA;
}

</style>
<div style="width:600px;heigh:290px;">
    <div>
      <ul class="ullist" id="list_0">
    
      </ul>
    </div>    
    <div style="clear:both;border-top:1px solid #D1D1D1;height:90px;margin-top:0px;">
      <ul class="ullist" id="list_1">    
      </ul>
    </div> 
    <div style="margin-left:10px;margin-top:0px;margin-bottom:3px;color:red;clear:both;text-align:left;" id="selectmessage">
    <span id="select_0" style="margin-left:5px;"></span><span style="margin-left:5px;" id="select_1"></span><span  style="margin-left:5px;" id="select_2"></span>&nbsp;
    </div>
    <div >
        <input type="button" class="btn_b" value="确 定"  onclick="save()"/>
    </div>
</div>