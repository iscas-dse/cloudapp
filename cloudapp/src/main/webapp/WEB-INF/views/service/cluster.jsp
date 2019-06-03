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
			<h1>集群资源管理</h1>
			<p class="lead" style="margin-top: 10px">
				<em>集群资源管理界面功能:</em> 1.查看集群资源使用情况，包括命名空间，节点，持久化存储卷，角色，存储类等；
				2.查看应用在物理集群上创建时间，容器的运行状态，资源消耗，包括工作负载，进程集，部署任务，容器组，副本集，副本控制器，有状态副本集等；
				3.查看集群中服务发现与负载均衡运行情况 4.查看K8s集群的配置，存储，保密字典相关信息
			</p>
		</div>
		<iframe style="margin-top: -5px; display: block;" frameborder="0"
			marginheight="0" marginwidth="0" border="0" id="alimamaifrm"
			name="alimamaifrm" scrolling="no" height="2000px" width="100%"
			src="${url}" frameborder="0" width="100%" height="100%"> </iframe>
	</div>
</body>