<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>Applications</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
	<div id="d-main-content">
		<div class="intro">
			<h1>应用&nbsp;管理</h1>
			<p class="lead" style="margin-top: 10px">
				<em>应用管理&nbsp;</em> 在应用管理界面里:
				1.应用状态:编排/部署情况:如果是MODIFIED,应用正处于编排情况,可继续编排,如果是DEPLOYED,说明应用是部署状态;
				2.应用概览:可以查看应用的创建时间，服务数量，容器实例数量，组成服务的详细信息，包括IP地址，端口，访问链接等
				3.拓扑:可视化呈现应用的拓扑结构; 4.启动:启动应用; 5.删除:删除应用; 6.停止:停止应用.
			</p>
		</div>

		<div class="content-board">
			<table style="margin-bottom: 0px; border-bottom: 0px;"
				class="table table-bordered table-condensed deploy-table">
				<thead>
					<tr>
						<th style="width: 20%;">应用名</th>
						<th style="width: 20%;">应用状态</th>
						<th>应用操作</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div style="margin-top: 0px;" class="panel-group" id="app-list"
				role="tablist" aria-multiselectable="true"></div>
		</div>
		<script src="<c:url value='/js/cloudeploy/app/app-list.js' />"></script>
	</div>
</body>
</html>