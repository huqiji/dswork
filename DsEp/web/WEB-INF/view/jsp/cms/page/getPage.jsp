<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<title></title>
<%@include file="/commons/include/get.jsp" %>
<script type="text/javascript">
function build(pageid){
	$dswork.doAjaxObject.autoDelayHide("发布中", 2000);
	var v = {"siteid":"${po.siteid}"};
	if(pageid != null){
		v.categoryid = "${po.id}";
		v.pageid = pageid;
	}
	$.post("build.htm",v,function(data){$dswork.doAjaxShow(data, function(){});});
}
function unbuild(pageid){
	$dswork.doAjaxObject.autoDelayHide("删除中", 2000);
	var v = {"siteid":"${po.siteid}"};
	if(pageid != null){
		v.categoryid = "${po.id}";
		v.pageid = pageid;
	}
	$.post("unbuild.htm",v,function(data){$dswork.doAjaxShow(data, function(){});});
}
$dswork.page.join = function(td, menu, id){
	$(menu).append($('<div iconCls="menuTool-graph">预览</div>').bind("click", function(){
		window.open("buildHTML.chtml?view=true&siteid=${po.siteid}&categoryid=${po.id}&pageid=" + id);
	}));
	$(menu).append($('<div iconCls="menuTool-graph">发布</div>').bind("click", function(){
		if(confirm("是否发布内容")){build(id);}
	}));
	$(menu).append($('<div iconCls="menuTool-graph">发布【并发布栏目首页】</div>').bind("click", function(){
		if(confirm("是否发布内容【并发布栏目\"${fn:escapeXml(po.name)}\"】")){build(id);build(-1);}
	}));
	$(menu).append($('<div iconCls="menuTool-delete">删除发布</div>').bind("click", function(){
		if(confirm("是否删除已发布内容")){unbuild(id);}
	}));
};
$(function(){
	$dswork.page.menu("delPage.htm?id=${po.id}", "updPage1.htm", "", "${pageModel.currentPage}");
});
$dswork.doAjax = true;
$dswork.callback = function(){if($dswork.result.type == 1){
	location.href = "getPage.htm?id=${po.id}&page=${pageModel.currentPage}";
}};
$(function(){
	$("#btn_category_d").bind("click", function(){
		var m = $("#category").find("option:selected");
		if(confirm("是否删除栏目\"${fn:escapeXml(po.name)}\"已发布首页")){unbuild(0);}
	});
	$("#btn_page_d").bind("click", function(){
		var m = $("#category").find("option:selected");
		if(confirm("是否删除栏目\"${fn:escapeXml(po.name)}\"所有已发布内容")){unbuild(-1);}
	});
	$("#btn_page").bind("click", function(){
		if(confirm("是否发布栏目\"${fn:escapeXml(po.name)}\"所有内容")){build(0);}
	});
	$("#btn_category").bind("click", function(){
		if(confirm("是否发布栏目\"${fn:escapeXml(po.name)}\"首页")){build(-1);}
	});
	$("#btn_site").bind("click", function(){
		if(confirm("是否发布首页")){build(null);}
	});
});
</script>
</head> 
<body>
<table border="0" cellspacing="0" cellpadding="0" class="listLogo">
	<tr>
		<td class="title">${fn:escapeXml(po.name)}-内容列表</td>
		<td class="menuTool">
			<a class="delete" id="btn_category_d" href="#">删除栏目首页</a>
			<a class="delete" id="btn_page_d" href="#">删除发布内容</a>
			&nbsp;<a class="vline" href="#"></a>&nbsp;
			<a class="graph" id="btn_category" href="#">发布栏目首页</a>
			<a class="look" target="_blank" href="buildHTML.chtml?view=true&siteid=${po.siteid}&categoryid=${po.id}">预览栏目首页</a>
			&nbsp;<a class="vline" href="#"></a>&nbsp;
			<a class="graph" id="btn_page" href="#">发布栏目内容</a>
			&nbsp;<a class="vline" href="#"></a>&nbsp;
			<a class="graph" id="btn_site" href="#">发布首页</a>
			<a class="look" target="_blank" href="buildHTML.chtml?view=true&siteid=${po.siteid}">预览首页</a>
			&nbsp;<a class="vline" href="#"></a>&nbsp;
			<a class="insert" href="addPage1.htm?categoryid=${po.id}&page=${pageModel.currentPage}">添加</a>
			<a class="delete" id="listFormDelAll" href="#">删除所选</a>
		</td>
	</tr>
</table>
<div class="line"></div>
<form id="queryForm" method="post" action="getPage.htm?id=${po.id}">
<table border="0" cellspacing="0" cellpadding="0" class="queryTable">
	<tr>
		<td class="input">
			&nbsp;关键字：<input type="text" class="text" name="keyvalue" style="width:300px;" value="${fn:escapeXml(param.keyvalue)}" />
		</td>
		<td class="query"><input id="_querySubmit_" type="button" class="button" value="查询" /></td>
	</tr>
</table>
</form>
<div class="line"></div>
<form id="listForm" method="post" action="delPage.htm?id=${po.id}">
<table id="dataTable" border="0" cellspacing="1" cellpadding="0" class="listTable">
	<tr class="list_title">
		<td style="width:2%"><input id="chkall" type="checkbox" /></td>
		<td style="width:5%">操作</td>
		<td style="width:60%">标题</td>
		<td style="width:22%">发布时间</td>
		<td>首页推荐</td>
	</tr>
<c:forEach items="${pageModel.result}" var="d">
	<tr>
		<td><input name="keyIndex" type="checkbox" value="${d.id}" /></td>
		<td class="menuTool" keyIndex="${d.id}">&nbsp;</td>
		<td>${fn:escapeXml(d.title)}</td>
		<td>${fn:escapeXml(d.releasetime)}</td>
		<td>${d.pagetop == 1 ? "是" : "否"}</td>
	</tr>
</c:forEach>
</table>
<input name="page" type="hidden" value="${pageModel.currentPage}" />
</form>
<table border="0" cellspacing="0" cellpadding="0" class="bottomTable">
	<tr><td>${pageNav.page}</td></tr>
</table>
</body>
</html>
