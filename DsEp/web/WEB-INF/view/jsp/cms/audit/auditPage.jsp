<%@page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<title></title>
<%@include file="/commons/include/updAjax.jsp" %>
<%@include file="/commons/include/editor.jsp" %>
<script type="text/javascript">
$dswork.callback = function(){if($dswork.result.type == 1){
	location.href = "getPage.htm?id=${po.categoryid}&page=${param.page}";
}};
$(function(){
	$(".form_title").css("width", "8%");
});
</script>
</head>
<body>
<table border="0" cellspacing="0" cellpadding="0" class="listLogo">
	<tr>
		<td class="title">审核页面</td>
	<c:if test="${po.status==1}">
	</c:if>
		<td class="menuTool">
			<a class="submit" id="_pass" href="javascript:void(0);">通过</a>
			<a class="close" id="_nopass" href="javascript:void(0);">不通过</a>
			<a class="back" href="getPage.htm?id=${po.categoryid}&page=${param.page}">返回</a>
			<script type="text/javascript">
			$("#_pass").click(function(){
				if(confirm("确认通过？")){
					$("input[name='status']").val(4);
					$("#dataForm").ajaxSubmit($dswork.doAjaxOption);
				}
			});
			$("#_nopass").click(function(){
				if(confirm("确认不通过？")){
					$("input[name='status']").val(2);
					$("#dataForm").ajaxSubmit($dswork.doAjaxOption);
				}
			})
			</script>
		</td>
	</tr>
</table>
<div class="line"></div>
<c:if test="${po.status!=1}">
<table border="0" cellspacing="1" cellpadding="0" class="listTable">
	<tr><td class="form_input" style="color:red;text-align:center;">没有新提交的页面</td></tr>
</table>
</c:if>
<c:if test="${po.status==1}">
<form id="dataForm" method="post" action="auditPage2.htm">
<table border="0" cellspacing="1" cellpadding="0" class="listTable">
	<tr>
		<td class="form_title">标题</td>
		<td class="form_input">${fn:escapeXml(po.title)}</td>
	</tr>
	<tr>
		<td class="form_title">摘要</td>
		<td class="form_input">${fn:escapeXml(po.summary)}</td>
	</tr>
	<tr>
		<td class="form_title">meta关键词</td>
		<td class="form_input">${fn:escapeXml(po.metakeywords)}</td>
	</tr>
	<tr>
		<td class="form_title">meta描述</td>
		<td class="form_input">${fn:escapeXml(po.metadescription)}</td>
	</tr>
	<tr>
		<td class="form_title">来源</td>
		<td class="form_input">${fn:escapeXml(po.releasesource)}</td>
	</tr>
	<tr>
		<td class="form_title">作者</td>
		<td class="form_input">${fn:escapeXml(po.releaseuser)}</td>
	</tr>
	<tr>
		<td class="form_title">操作</td>
		<td class="form_input menuTool">
			<a class="look" target="_blank" href="${ctx}/cms/preview.chtml?siteid=${po.siteid}&categoryid=${po.categoryid}&pageid=${po.id}">查看栏目内容</a>
		</td>
	</tr>
	<tr>
		<td class="form_title">图片</td>
		<td class="form_input">
			图片地址：${fn:escapeXml(po.img)}<br/>
			焦点图：${po.imgtop==1?'是':'否'}
		</td>
	</tr>
	<tr>
		<td class="form_title">发布</td>
		<td class="form_input">
			首页推荐：${po.pagetop==1?'是':'否'}<br/>
			发布时间：${fn:escapeXml(po.releasetime)}
		</td>
	</tr>
</table>
<div class="line"></div>
<table border="0" cellspacing="1" cellpadding="0" class="listTable">
	<tr>
		<td class="form_title">审核意见</td>
		<td class="form_input"><textarea name="msg" style="width:99%;height:100px;"></textarea></td>
	</tr>
</table>
<input type="hidden" name="id" value="${po.id}" />
<input type="hidden" name="status" value="${po.status}" />
</form>
</c:if>
</body>
</html>
