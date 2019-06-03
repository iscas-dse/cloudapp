<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>组件管理</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
	<!--  <script src="<c:url value='/js/codemirror-5.33.0/codemirror.css' />"></script>
	<script src="<c:url value='/js/codemirror-5.33.0/codemirror.js' />"></script>-->
	<div id="d-main-content">
		<div class="deploy-bar"></div>
		<div class="intro">
			<h1>组件&nbsp;管理</h1>
			<p class="lead" style="margin-top: 10px">
				<em>组件管理&nbsp;</em> define configure file of a component. This is
				used for Expert to create and upload templates which will be used to
				describe parameters dependent correlation. Templates can be
				converted into configure file by specific key-value parameters.
			</p>
		</div>
		<div class="row content-board">
			<div id="d-com-list" class="itemlist col-sm-2">
				<h4>&nbsp&nbsp&nbsp&nbsp&nbsp Components</h4>
				<div class="nav nav-list col-sm-12">
					<ul></ul>
				</div>
			</div>
			<div id="d-container-list" class="itemlist col-sm-2 hide">
				<div class="col-sm-12">
					&nbsp&nbsp<i class="fa fa-cubes" id="d-chart-version">版本:0.3.0</i>
				</div>
				<div class="col-sm-12" style="margin-top: 5px;">
					&nbsp&nbsp<i class="fa fa-cubes"> 说明:</i>
				</div>
				<div class="col-sm-12 " style="margin-top: 5px; margin-left: 30px;">
					<p id="d-chart-des">基础数据库组件，快速，可靠，可伸缩，基于开源.</p>
				</div>
				<div class="col-sm-12">
					&nbsp&nbsp<i class="fa fa-cubes"> 创建时间:</i>
				</div>
				<div class="col-sm-12 " style="margin-top: 5px; margin-left: 30px;">
					<p id="d-chart-create">2017年8月</p>
				</div>
				<div class="col-sm-12">
					&nbsp&nbsp<i class="fa fa-cubes"> 修改时间:</i>
				</div>
				<div class="col-sm-12 " style="margin-top: 5px; margin-left: 30px;">
					<p id="d-chart-update">2017年8月</p>
				</div>
				<div class="col-sm-12" style="margin-left: 30px;">
					<btn class="btn btn-info" id="d-chart-struct"> 组件配置<a
						class="glyphicon glyphicon-hand-right" style="color: white;" /></btn>
				</div>
			</div>
			<div id="d-tempt-list" class="col-sm-10">
				<div id="d-chart-oper" class="menu-bar">
					<!-- 	<a class="btn btn-default"
						href="javascript:dCustomFiles.addFileClick()"> <span
						class="glyphicon glyphicon-plus"></span> 上传模板
					</a> -->
				</div>
				<table id="fileTable" class="table table-condensed table-boxed">
					<thead>
						<tr>
							<th>name</th>
							<th>version</th>
							<th>Description</th>
							<th>create-time</th>
							<th>modify-time</th>
						</tr>
					</thead>
					<tbody></tbody>
				</table>
				<!-- 上传文件对话框-->
				<div id="addCustomFileModal" class="modal fade"
					data-backdrop="static">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal">
									<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
								</button>
								<h4 class="modal-title">新配置文件上传</h4>
							</div>
							<div class="modal-body" style="height: 250px">
								<div role="form" class="col-sm-12">
									<div class="form-group">
										<div class="col-sm-12" style="margin-top: 20px;">
											<a href="#" id="file-upload" class="btn btn-primary">选择文件</a>
										</div>
									</div>
									<div class="form-group">
										<label for="fileName" class="col-sm-12 control-label">文件名字</label>
										<div class="col-sm-12">
											<div class="col-sm-6" style="padding-left: 0px;">
												<input class="form-control" type="text" name="fileName"
													id="fileName" />
											</div>
											<div class="col-sm-6">
												<a id="upload-btn" class="btn btn-default">上传</a>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">close</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!--浏览文件对话框 -->
				<div id="scanFileModal" class="modal fade" data-backdrop="static">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal">
									<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
								</button>
								<h4 class="modal-title">
									template <span id="scanFileName" class="control-label"></span>
								</h4>
							</div>
							<div class="modal-body" style="height: 500px">
								<textarea id="scanFileBody" readonly
									style="width: 100%; height: 100%">
								</textarea>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">close</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
			</div>
		</div>
		<div style="margin-top: 200px;" class="modal fade" id="saveModal"
			tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
			aria-hidden="true" data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title" id="myModalLabel">保存应用</h4>
					</div>
					<div class="modal-body">
						<div style="width: 50%;">
							<div class="form-group">
								<label for="appName">应用名</label> <input type="text"
									class="form-control" id="appName"
									placeholder="application name">
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
						<button type="button" id="save-app-btn" class="btn btn-primary">确定</button>
					</div>
				</div>
			</div>
		</div>

		<script src="<c:url value='/js/cloudeploy/charts/d-chart-main.js' />"></script>
		<script
			src="<c:url value='/js/jquery/uploadify/jquery.uploadify.min.js' />"></script>
	</div>

</body>
</html>