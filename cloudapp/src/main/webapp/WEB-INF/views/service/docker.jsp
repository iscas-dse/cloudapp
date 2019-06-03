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
			<h1>容器镜像管理</h1>
			<p class="lead" style="margin-top: 10px">
				<em>容器镜像管理界面功能:</em> 1.容器镜像管理只提供基本的容器镜像查看功能，完善一站式智能化编排部署的系统必需的一部分子系统
				2.用户可以在容器镜像管理查看系统镜像分布情况，容器分布情况，容器网络情况，容器存储挂载路径情况
				3.用户可以在这里查看相对应Docker的版本，操作系统，内核等数据
		</div>

		<iframe style="margin-top: 50px; display: block;" frameborder="0"
			marginheight="0" marginwidth="0" border="0" id="alimamaifrm"
			name="alimamaifrm" scrolling="no" height="2000px" width="100%"
			src="${url}" allowfullscreen frameborder="0" width="100%"
			height="100%"> </iframe>
	</div>
</body>