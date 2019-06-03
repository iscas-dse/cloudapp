<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>Orchestration</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

</head>
<body>
	<div id="d-main-content">
		<div class="intro">
			<h1>智能&nbsp;编排</h1>
			<p class="lead">
				<em>智能编排&nbsp;</em>在智能编排界面里：
				1.模型编排配置部署,用户从组建列表直接拖拽节点,绘制关联线,进行建模编排配置部署应用;
				2.智能化编排配置部署,系统实现机器学习系统，离线挖掘组建之间API文档，DockerFile,命名规则等数据学习并计算组件关联分数,排序筛选组件关联的分数值，
				然后实现智能化构图算法和智能化描线算法,自动编排服务应用
			</p>
		</div>

		<div id="app-panel" class="content-board">
			<div id="graph-panel" class="col-sm-10" style="overflow: auto;">
				<div id="tmp-panel"></div>
			</div>
			<div id="group-list" class="col-sm-2" style="overflow: auto;">
				<div>
					<button type="button"
						style="background-color: #ec971f; border: none; border-Radius: 0px; margin-bottom: 2px;"
						class="btn btn-info btn-block ">智能编排</button>

					<button type="button" id="start-orche"
						style="background-color: #31b0d5; border: none; margin-left: 10px; margin-top: 10px;"
						class="btn btn-primary">生成编排</button>

					<button type="button" id="log-btn"
						style="background-color: #31b0d5; border: none; margin-left: 10px; margin-top: 10px;"
						class="btn btn-primary">编排日志</button>

					<button type="button" id="btn-save"
						style="background-color: #31b0d5; border: none; margin-left: 10px; margin-top: 10px;"
						class="btn btn-primary">保存编排</button>

					<button type="button" id="save-and-deploy"
						style="background-color: #31b0d5; border: none; margin-left: 10px; margin-top: 10px;"
						class="btn btn-primary">部署应用</button>

				</div>
				<button type="button"
					style="background-color: #4CAF50; border: none; border-Radius: 0px; margin-top: 10px;"
					class="btn btn-info btn-block">组件列表</button>
				<button type="button" id="chk-all"
					style="background-color: #31b0d5; border: none; margin-left: 10px; margin-top: 10px;"
					class="btn btn-primary">全选</button>
				<button type="button" id="chk-no"
					style="background-color: #31b0d5; border: none; margin-left: 10px; margin-top: 10px;"
					class="btn btn-primary">全不选</button>
				<!-- 组件列表 -->

				<div id="panel-group-operations" class="panel-group"
					style="margin-top: 10px; display: block;" />
			</div>
		</div>
		<!-- node details start -->
		<div class="modal fade" id="detailModal" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true"
			data-backdrop="static">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close editCancel"
							data-dismiss="modal">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title" id="myModalLabel">服务编排</h4>
					</div>
					<div class="modal-body">
						<form role="form ">
							<div class="row" id="detail9">
								<div class="col-sm-5">
									<div class="form-group">
										<label for="serviceName" class="control-label pull-left"
											style="padding-right: 1em;">服务名字:</label> <input
											id="serviceName" name="serviceName" class="form-control"
											placeholder="mysqlService">
									</div>
									<!-- <div class="form-group">
										<label for="containerPort" class="control-label pull-left"
											style="padding-right: 2em;">服务网络类型:</label>
										<div class="dropdown">
											<button class="btn btn-default dropdown-toggle" type="button"
												id="dropdownMenu2" data-toggle="dropdown"
												aria-haspopup="true" aria-expanded="false">
												ClusterIP <span class="caret"></span>
											</button>
											<ul class="dropdown-menu" aria-labelledby="dropdownMenu2">
												<li><a href="#">ClusterIP</a></li>
												<li><a href="#">NodePort</a></li>
											</ul>
										</div>
									</div>
									 -->
									<div class="form-group">
										<label for="imageName" class="control-label pull-left"
											style="padding-right: 1em;">镜像地址:</label> <input
											id="imageName" name="imageName" class="form-control"
											placeholder="mysql:5.7.4">
									</div>
									<div class="form-group">
										<label for="nodePort" class="control-label pull-left">节点端口:</label>
										<input type="number" id="nodePort" name="nodePort"
											class="form-control" placeholder="30303">
									</div>
									<div class="form-group">
										<label for="containerPort" class="control-label pull-left"
											style="padding-right: 2em;">容器端口:</label> <input
											type="number" id="containerPort" name="containerPort"
											class="form-control" placeholder="3306">
									</div>
									<div class="form-group">
										<label for="instanceNum" class="control-label pull-left"
											style="padding-right: 2em;">实例数量:</label> <input
											type="number" id="instanceNum" name="containerPort"
											class="form-control" placeholder="1">
									</div>
									<HR
										style="FILTER: alpha(opacity= 100, finishopacity=0, style= 2)"
										width="80%" color=#987 cb 9 SIZE=10>
									<div class="form-group">
										<label for="CPU" class="control-label pull-left"
											style="padding-right: 2em;">CPU核数分配(单位:个):</label> <input
											type="number" id="CPU" name="containerPort"
											class="form-control" placeholder="1"> <label
											for="MEM" class="control-label pull-left"
											style="padding-right: 2em;">内存分配(单位:MB):</label> <input
											type="number" id="MEM" name="containerPort"
											class="form-control" placeholder="128"> <label
											for="DISK" class="control-label pull-left"
											style="padding-right: 2em;">磁盘供给(单位:MB):</label> <input
											type="number" id="DISK" name="containerPort"
											class="form-control" placeholder="512">
									</div>

								</div>
								<!-- 环境变量-->
								<div class="col-sm-7" id="right">
									<label class="col-sm-9">环境变量</label>
									<div id="envs"></div>
									<label id="dependent" class="col-sm-9">依赖项</label>
									<div id="dependents"></div>
								</div>
								<!-- 依赖项-->

								<!-- end right col -->
							</div>
						</form>

					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default editCancel"
							data-dismiss="modal">取消</button>
						<button type="button" class="btn btn-primary editSave">确定</button>
					</div>
				</div>
			</div>
		</div>
		<!-- task commit start -->
		<div style="margin-top: 200px;" class="modal fade" id="saveModal"
			tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
			aria-hidden="true" data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header" id="saveModalHeader">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title" id="myModalLabel">保存应用</h4>
					</div>
					<div class="modal-body">
						<div style="width: 50%;">
							<div class="form-group">
								<label for="appName">设置应用名</label> <input type="text"
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

		<div style="margin-top: 200px;" class="modal fade" id="logTable"
			tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
			aria-hidden="true" data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header" id="saveModalHeader">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title" id="myModalLabel">编排日志</h4>
					</div>
					<div class="modal-body">
						<p id="log-txt"></p>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					</div>
				</div>
			</div>
		</div>
		<!-- task commit end -->
		<!-- operation start -->
		<div style="margin-top: 200px;" class="modal fade" id="operationModal"
			tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
			aria-hidden="true" data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title" id="myModalLabel">actions</h4>
					</div>
					<div class="modal-body">
						<ul class="operation-list">
							<li id="start" data-oper-type="START" data-view-id="1">
								<div class="oper-icon">
									<i class="fa fa-play text-success"></i>
								</div>
								<div class="oper-text">start</div>
							</li>
							<li id="stop" data-oper-type="STOP" data-view-id="1">
								<div class="oper-icon">
									<i class="fa fa-stop text-danger"></i>
								</div>
								<div class="oper-text">stop</div>
							</li>
							<li id="remove" data-oper-type="REMOVE" data-view-id="2">
								<div class="oper-icon">
									<i class="fa fa-minus text-danger"></i>
								</div>
								<div class="oper-text">remove</div>
							</li>
							<li id="copy" data-oper-type="COPY" data-view-id="2">
								<div class="oper-icon">
									<i class="fa fa-plus text-primary"></i>
								</div>
								<div class="oper-text">copy</div>
							</li>
							<li id="fail" data-oper-type="FAIL" data-view-id="3">
								<div class="oper-icon">
									<i class="fa fa-times text-danger"></i>
								</div>
								<div class="oper-text">fail test</div>
							</li>
							<li id="fail" data-oper-type="START" data-view-id="3">
								<div class="oper-icon">
									<i class="fa fa-play text-success"></i>
								</div>
								<div class="oper-text">recover</div>
							</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
		<!-- 模型选择 -->
		<div style="margin-top: 200px;" class="modal fade" id="models"
			tabindex="1" role="dialog" aria-labelledby="myModalLabel"
			aria-hidden="true" data-backdrop="static">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title" id="myModalLabel">智能化推导规则</h4>
					</div>
					<div class="modal-body">
						<ol>
							<li class="list-group-item">挖掘命名规则&nbsp&nbsp</li>
							<li class="list-group-item">挖掘api文档规则&nbsp&nbsp</li>
							<li class="list-group-item">挖掘DockerFile文档规则&nbsp&nbsp <!-- <input type="checkbox"
								checked="checked" /> -->
							</li>
						</ol>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
						<button type="button" id="start-fire-btn" data-dismiss="modal"
							class="btn btn-primary">生成编排</button>
					</div>
				</div>
			</div>
		</div>
		<!-- operation end -->
		<!-- node details end <script src="<c:url value='/js/jsPlumb/dom.jsPlumb-1.7.2.js' />"></script>-->
		<script src="<c:url value='/js/jsPlumb/dom.jsPlumb-1.7.2.js' />"></script>
		<script src="<c:url value='/js/cloudeploy/app/graph/j-class.js' />"></script>
		<script src="<c:url value='/js/cloudeploy/app/graph/node.js' />"></script>
		<script src="<c:url value='/js/cloudeploy/app/app-plumb.js' />"></script>
		<script
			src="<c:url value='/js/cloudeploy/app/app-orchestration.js' />"></script>
	</div>
</body>
</html>