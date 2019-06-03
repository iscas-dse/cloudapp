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
<style type="text/css">
#uploader {
	position: relative;
}

#uploader_queue {
	position: absolute;
	width: 600px;
	left: 200px;
	top: 0;
}
</style>
<body>
	<div id="d-main-content">
		<div class="deploy-bar"></div>
		<div class="intro">
			<h1>组件&nbsp;管理</h1>
			<p class="lead" style="margin-top: 10px">
				<em>组件管理</em> 这里是用户对组件的管理,也是智能化编排配置部署核心组成之一。
				本系统基于K8S系统，研究并总结出微服务服务应用可以具体通过两个配置文件进行刻画描述:service.yaml和deployment.yaml;
				1.这里实现了根据DockerFile自动生成这两个配置文件的功能;
				2.这里实现了组件的上传，模板中心，这两个配置文件构成的组件为智能化编排服务;
				3.在组件管理界面里面可以对组件进行新建上传,一键部署,删除组件等功能.
			</p>
		</div>
		<div class="row content-board">
			<div id="d-charts-list" class="itemlist col-sm-2">
				<div class="nav nav-list col-sm-12">
					<ul>
					</ul>
				</div>
			</div>
			<div id="d-typeCharts-List" class="itemlist col-sm-2 hide">
				<ul>
					<li>
					<li>
				</ul>
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
					<a class="btn btn-default" href="javascript:dCharts.uploadChart()">
						<span class="glyphicon glyphicon-plus"></span> 上传组件
					</a>
				</div>
				<table id="chartsTable" class="table table-condensed table-boxed">
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
				<table id="apiTable" class="table table-condensed table-boxed"
					style="display: none">
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
				<table id="dockerfileTable"
					class="table table-condensed table-boxed" style="display: none">
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
				<!-- 上传Chart组件对话框-->
				<div id="addCustomFileModal" class="modal fade"
					data-backdrop="static">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal">
									<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
								</button>
								<h4 class="modal-title">新组件上传</h4>
							</div>
							<div class="modal-body" style="height: 250px">
								<div role="form" class="col-sm-12">
									<div class="form-group">
										<label class="control-label" for="componentName">组件名称</label>
										<input id="componentName" style="width: 80%"
											class="form-control" type="text" placeholder="请用英文输入名称" /> <br>
									</div>
									<div class="form-group">
										<label class="control-label" for="componentVersion">组件版本</label>
										<input id="componentVersion" style="width: 80%"
											class="form-control" type="text" placeholder="请用英文输入组件版本" />
										<br>
									</div>
									<div class="form-group">
										<label class="control-label" for="componentDes">组件描述</label> <input
											id="componentDes" style="width: 80%" class="form-control"
											type="text" placeholder="请用英文输入组件描述" /> <br>
									</div>

									<input type="file" name="file_upload" id="file_upload" />

									<button id="uploadFileId"
										href="javascript:$('#file_upload').uploadify('upload','*')">上传</button>
									<!--<a href="javascript:$('#file_upload').uploadify('stop')">取消上传</a>-->
									<div id="uploader_queue"></div>
									<div id="uploader_msg"></div>
									<div id="uploader_view"></div>
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal" onclick="javascript:dMain.listCharts()">close</button>
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
		<script>
			$("#file_upload")
					.uploadify(
							{
								'auto' : false,
								'method' : "get",
								'formData' : {
									'folder' : 'file'
								},
								'height' : 30,
								'swf' : dURIs.swfs + '/uploadify.swf', // flash  
								'uploader' : dURIs.chartsURI.uploadChart, //  
								'width' : 120,
								'fileTypeDesc' : 'ֻ支持多种文件格式',
								'fileTypeExts' : '.dat;.yaml;',
								'fileSizeLimit' : '800KB',
								'buttonText' : '选择文件',
								'uploadLimit' : 5,
								'multi' : true,
								'successTimeout' : 5,
								'requeueErrors' : false,
								'removeTimeout' : 10,
								'removeCompleted' : true,
								'queueSizeLimit' : 100,
								'queueID' : 'uploader_queue',
								'progressData' : 'speed',
								'onInit' : function() {

								},
								'onFallback' : function() {
									//alert("您未安装FLASH控件，无法上传图片！请安装FLASH控件后再试。");
								},
								'onUploadStart' : function(file) {
									$('#file_upload')
											.uploadify(
													"settings",
													'formData',
													{
														'comName' : document
																.getElementById('componentName').value,
														'comVersion' : document
																.getElementById('componentVersion').value,
														'comDes' : document
																.getElementById('componentDes').value
													});
								},
								'onUploadSuccess' : function(file, charts,
										response) {
									console.log(charts);

								},
								'onQueueComplete' : function(queueData) {
									$('#uploader_msg').html(
											queueData.uploadsSuccessful
													+ '个文件上传成功<br/>');
									$('#file_upload').uploadify('cancel', '*');
								}
							});
			var upload = document.getElementById('uploadFileId');
			upload.onclick = function() {
				var componentName = $("#componentName").val().replace(/\s+/g,
						"");
				var componentVersion = $("#componentVersion").val().replace(
						/\s+/g, "");
				var componentDes = $("#componentDes").val().replace(/\s+/g, "");
				console.log("上传组件名字:" + componentName + ": "
						+ componentName.length);
				var uploader_queue = document.getElementById("uploader_queue");
				console.log($("#uploader_queue > div").length);
				if (componentName.length == 0 || componentVersion.length == 0
						|| componentDes.length == 0
						|| $("#uploader_queue > div").length == 0
						|| /.*[\u4e00-\u9fa5]+.*$/.test(componentName)
						|| /.*[\u4e00-\u9fa5]+.*$/.test(componentVersion)
						|| /.*[\u4e00-\u9fa5]+.*$/.test(componentDes)) {
					alert("有你必要数据没有填写,或者命名包含非法字符!");
				} else {

					$('#file_upload').uploadify('upload', '*');
				}
			};
		</script>
	</div>
</body>
</html>