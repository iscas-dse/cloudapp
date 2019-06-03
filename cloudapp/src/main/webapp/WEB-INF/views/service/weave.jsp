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
			<h1>服务链路追踪管理</h1>
			<p class="lead" style="margin-top: 10px">
				<em>服务链路追踪管理功能:</em> 1.针对微服务实现链路追踪，CPU,内存，磁盘等资源使用实时监控治理功能;
				2.可视化呈现应用在底层基础设施运行的服务链路关系图; 3.按照进程，容器，POD，主机，名字，DNS，控制器,镜像等分类进行服务治理
				3.显示服务资源使用,运行状态等功能情况
			</p>
		</div>

		<iframe style="margin-top: 100px; display: block;" frameborder="0"
			marginheight="0" marginwidth="0" border="0" id="alimamaifrm"
			name="alimamaifrm" scrolling="no" height="2000px" width="100%"
			src="${url}" frameborder="0" width="100%" height="100%"> </iframe>
	</div>
</body>