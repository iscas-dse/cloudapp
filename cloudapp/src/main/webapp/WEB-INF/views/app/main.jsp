<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>Cloudeploy</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link href="<c:url value='/css/bootstrap/bootstrap.min.css' />"
	type="text/css" rel="stylesheet">
<link
	href="<c:url value='/css/font-awesome-4.2.0/css/font-awesome.min.css' />"
	type="text/css" rel="stylesheet">
<link href="<c:url value='/css/cloudeploy/cloudeploy.css' />"
	type="text/css" rel="stylesheet">
<link href="<c:url value='/css/new/dashboard.css' />" type="text/css"
	rel="stylesheet">
<link href="<c:url value='/css/cloudeploy/app-list.css' />"
	type="text/css" rel="stylesheet">
<link href="<c:url value='/css/cloudeploy/app-panel.css' />"
	type="text/css" rel="stylesheet">
<link href="<c:url value='/css/cloudeploy/template.css' />"
	type="text/css" rel="stylesheet">
<link href="<c:url value='/css/uploadify/uploadify.css' />"
	type="text/css" rel="stylesheet">
<link href="<c:url value='/css/new/style.css'/>" type="text/css"
	rel="stylesheet" />
<%@ include file="../share/head.jsp"%>
</head>
<body bgcolor="rgb(238,238,238)">
	<script src="<c:url value='/js/jquery/jquery-1.11.0.js' />"></script>
	<script src="<c:url value='/js/jquery/json/jquery.json-2.4.min.js' />"></script>
	<script
		src="<c:url value='/js/jquery/dateFormat/jquery.dateFormat.js' />"></script>
	<script src="<c:url value='/js/bootstrap/bootstrap.min.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/uri.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/map.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/common.js' />"></script>
	<script src="<c:url value='/js/cloudeploy/app/app-main.js' />"></script>

	<div class="d-main"=>
		<div class="navigation">
			<div class="profile">
				<div class="avartar">
					<div id="avartar-top"></div>
					<div id="avartar-bottom"></div>
				</div>
				<p class="user-name">
					<a href="javascript:void(0)"></a>
				</p>
				<div class="user-link">
					<a title="index"><span class="glyphicon glyphicon-home"></span></a>
					<a id="clear" href="#" title="dashboard"><span
						class="glyphicon glyphicon-dashboard"></span></a> <a title="help"><span
						class="glyphicon glyphicon-book"></span></a> <a href="#"
						title="logout"><span class="glyphicon glyphicon-off"></span></a>
				</div>
			</div>
			<div class="sidebar">
				<ul id="sidebar-list" class="nav nav-list">
					<li id="sys-list-btn"><a><span
							class="glyphicon glyphicon-bookmark cool-orange"></span><span
							class="name">功能</span><span class="title"></span></a>
						<div class="cool-border"></div></li>
					<li id="app-list-btn"><a><span
							class="glyphicon glyphicon-th-list cool-blue"></span><span
							class="name">应用管理</span><span class="title"></span></a>
						<div class="cool-border"></div></li>
					<li id="charts-btn"><a><span
							class="glyphicon glyphicon-road cool-blue"></span><span
							class="name">组件管理</span><span class="title"></span></a>
						<div class="cool-border"></div></li>
					<li id="app-orchestration-btn"><a><span
							class="glyphicon glyphicon-retweet cool-blue"></span><span
							class="name">智能编排</span><span class="title"></span></a>
						<div class="cool-border"></div></li>

					<!-- <li id="inf-list-btn"><a><span
							class="glyphicon glyphicon-bookmark cool-orange"></span><span
							class="name">基础设施</span><span class="title"></span></a></li>
					<div class="cool-border"></div>
					<li id="cluster-list-btn"><a><span
							class="glyphicon glyphicon-cloud cool-blue"></span><span
							class="name">集群资源管理</span><span class="title"></span></a>
						<div class="cool-border"></div></li>
					<li id="docker-btn"><a><span
							class="glyphicon glyphicon-th-large cool-blue "></span><span
							class="name">容器镜像管理</span><span class="title"></span></a>
						<div class="cool-border"></div></li>
					<li id="consul-btn"><a><span
							class="glyphicon glyphicon-th-large cool-blue "></span><span
							class="name">机器健康度检测管理</span><span class="title"></span></a>
						<div class="cool-border"></div></li>
					<li id="weave-btn"><a><span
							class="glyphicon glyphicon-random cool-blue "></span><span
							class="name">服务链路追踪管理</span><span class="title"></span></a>
						<div class="cool-border"></div></li> -->
					<!-- 注释内容 -->
					<!--  <li id="config-btn"><a><span class="glyphicon glyphicon-cog cool-purple"></span><span class="name">Configure</span><span class="title">配置管理</span></a><div class="cool-border"></div></li>
				-->
					<!-- <li id="config-btn"><a><span class="glyphicon glyphicon-th cool-orange"></span><span class="name">Apptype</span><span class="title">应用类型</span></a><div class="cool-border"></div></li>
				 -->
					<!-- 
				 <li id="tempt-btn"><a><span class="glyphicon glyphicon-road cool-blue"></span><span class="name">Templates</span><span class="title">模板管理</span></a><div class="cool-border"></div></li>
				  -->
				</ul>
			</div>
		</div>
		<div class="d-main-content"></div>
	</div>
</body>
</html>