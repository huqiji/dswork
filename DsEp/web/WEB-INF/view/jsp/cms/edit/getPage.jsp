<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<title></title>
<%@include file="/commons/include/get.jsp" %>
<script type="text/javascript">
$(function(){
	$dswork.page.menu("", "updPage1.htm", "", "${pageModel.currentPage}");
});
$dswork.doAjax = true;
$dswork.callback = function(){if($dswork.result.type == 1){
	location.href = "getPage.htm?id=${po.id}&page=${pageModel.currentPage}";
}};
</script>
</head> 
<body>
<table border="0" cellspacing="0" cellpadding="0" class="listLogo">
	<tr>
		<td class="title">${fn:escapeXml(po.name)}-内容列表</td>
		<td class="menuTool">
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
		<td style="width:50%">标题</td>
		<td style="width:25%">发布时间</td>
		<td style="width:10%">首页推荐</td>
		<td>状态</td>
	</tr>
<c:forEach items="${pageModel.result}" var="d">
	<tr>
		<td>
		<c:if test="${d.status!=1}">
			<input name="keyIndex" type="checkbox" value="${d.id}" />
		</c:if>
		</td>
		<td class="menuTool" keyIndex="${d.id}">&nbsp;</td>
		<td>${fn:escapeXml(d.title)}</td>
		<td>${fn:escapeXml(d.releasetime)}</td>
		<td>${d.pagetop == 1 ? "是" : "否"}</td>
		<td>${d.status==0?'未提交':d.status==1?'审核中':d.status==2?'未通过':d.status==4?'已通过':''}</td>
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
