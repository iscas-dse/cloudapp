<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
	<div id="d-main-content">
		<div class="intro" style="display: block; position: absolute;">
			<h1>机器健康度检测管理</h1>
			<p class="lead" style="margin-top: 10px">
				<em>机器健康度检测管理界面功能:&nbsp;</em> 1.可以查看集群中机器节点的健康程度， 其中绿色代表机器节点呈现健康状态，
				黄色代表机器节点呈现警告状态， 红色代表机器节点呈现失败或者错误状态 2.查看服务在consul中存储的键值对，可以查看键值对的情况
			</p>
		</div>
		<iframe style="margin-top: 100px; display: block;" frameborder="0"
			marginheight="0" marginwidth="0" border="0" id="alimamaifrm"
			name="alimamaifrm" scrolling="no" height="2000px" width="100%"
			src="${url}" frameborder="0" width="100%" height="100%"> </iframe>
	</div>
</body>