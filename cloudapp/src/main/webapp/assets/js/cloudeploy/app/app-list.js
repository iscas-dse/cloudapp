var appList = {
	init : function() {
		appList.requestAppList();
	},

	requestAppList : function() {
		ajaxGetJsonWithMask(dURIs.apps.appList, null, appList.genAppListTable,
				null);
	},

	genAppListTable : function(data) {
		var apps = data;
		if (!(apps instanceof Array)) {
			return;
		}
		var html = '';
		if (apps.length > 0) {
			for ( var i in apps) {
				var app = apps[i];
				//consoloe.log(app);
				html += '<div class="panel panel-default">'
						+ '<div class="panel-heading" role="tab" id="app-'
						+ app.id
						+ '">'
						+ '<table class="table table-bordered table-condensed deploy-table">'
						+ '<tr><td style="width: 20%;">'
						+ app.name
						+ '</td><td style="width: 20%;">'
						+ app.status
						+ '</td><td>'
						+ appList.getOperationBtnHtml(app.id, 1)  //应用概览
				    	+ appList.getOperationBtnHtml(app.id, 2)  //拓扑
						+ appList.getOperationBtnHtml(app.id, 3)  //启动
						+ appList.getOperationBtnHtml(app.id, 4)  //停止
						//+ appList.getOperationBtnHtml(app.id, 5)  
						+ appList.getOperationBtnHtml(app.id, 6)  //删除
//						+ appList.getOperationBtnHtml(app.id, 7)
						+ '</td></tr></table></div>'
						+ '<div id="detail-app-'
						+ app.id
						+ '" class="panel-collapse collapse" role="tabpanel" aria-labelledby="app-'
						+ app.id
						+ '">'
						+ '<div class="panel-body"><table class="table table-bordered table-condensed deploy-table">'
						+ '<tbody><tr><td style="width:30%;">创建时间：'
						+ getFormatDateFromLong(app.createdAt,"yyyy-MM-dd HH:mm:ss")
						+ '</td><td colspan="3">修改时间：'
						+ getFormatDateFromLong(app.updatedAt,"yyyy-MM-dd HH:mm:ss")
						+ '</td></tr>'
						+ '<tr><td>服务数量：'
						+ app.containers.length
						+ '</td><td colspan="3">容器实例数量：';
				var instanceCnt = 0;
				/*for ( var i in app.containers) {
					instanceCnt += app.containers[i].length;
				}*/
				html += app.containers.length*2 + '</td></tr>'; //实例数量，以后修改
				for ( var i in app.containers) {
					
					var container = app.containers[i];
					if(container.deployment==null){
						console.log("deployment为null");
					html += '<tr><td style="font-weight:bold;">middleware-name：'
							+ container.name + '</td><td>middleware-port:'
							+ container.port + '<br />';
					for ( var k in container.params) {
						var param = container.params[k];
						if (param.key == "jvm_args") {
							html += 'JVM args:' + param.value + '<br />';
						}
						if (param.key == 'session_shared') {
							html += 'session shared:' + param.value + '<br />';
						}
					}
					html += '</td><td>middlewarestatus：'
							+ container.status
							+ '</td><td>primary-instances：' + container.initCount
							+ '<br />max-instances：' + container.maxCount
							+ ' <br />current-instances：' + container.instances.length
							+ '</td> </tr>';
					for ( var j in container.instances) {
						var instance = container.instances[j];
						html += ' <tr><td>instance name：' + instance.name + '</td><td>instance port：'
								+ instance.port + '</td><td colspan="2">instance-status：'
								+ instance.status
								+ '</td></tr>';
					}
					}
				}
				for ( var i in app.containers) {
					var container = app.containers[i];
					if(container.deployment!=null){
						var deployment= container.deployment;
						var service= container.service;
						var env=container.deployment.spec.template.spec.containers[0].env[0];
						//console.log(env);
						var demo="";
						if(container.deployment.spec.template.spec.containers[0].image=="docker.io/kubeguide/tomcat-app:v1"){
							demo="/demo";
						}
						var envString="";
						for(var key in env){
							  envString=envString+(key+":"+env[key])+"<br/>";
							}
						//console.log("deployment "+container.deployment);
						//console.log("service "+container.service);
						html += '<tr>'
							      +'<td style="font-weight:bold;">服务类型：'+ container.name + '</td>'
						          +'<td>服务序列号:'+ container.service.metadata.name + '<br /> </td> ';
					   html += '<td>镜像：'+container.deployment.spec.template.spec.containers[0].image
					        + '<br/>容器端口：' + container.deployment.spec.template.spec.containers[0].ports[0].containerPort
					        + '</td>'
					        +  '<td>' 
							+  '环境变量：<br/>' +envString
							+  '<br/>'
							+  '</td> '
							+'</tr>';
					   var nodePort=service.spec.ports[0].nodePort;
					   if(nodePort==undefined) nodePort="不需要分配";
						html += '<tr>'
							    +'<td>'+'节点IP:39.104.62.233<br/>' 
							    +'集群IP：'+service.spec.clusterIP+'<br/>'  
							    +'<a target="view_window" href="http://39.104.62.233:'+service.spec.ports[0].nodePort+demo+'">网络地址</a>'
							    +'</td>'
						        +'<td>外网端口：'+ nodePort+'<br/>ClusterPort:'+service.spec.ports[0].port+'</td>'
						        +'<td colspan="2">协议类型：TCP'+ '</td>'
						        +'</tr>';
						}
				}
				html += ' </tbody></table></div></div></div>';
			}
		} else {
			html = DHtml.emptyRow(6);
		}
		$("#app-list").html(html);
	},

	getOperationBtnHtml : function(appId, operationId) {
		var html = '';
		var style = "fa-circle-o-notch";
		var text = "actions";
		var func = undefined;
		switch (operationId) {
		case 1:
			break;
		case 2:
			text = "拓扑";//建模
			func = "appList.orchestration(" + appId + ",1)";
			break;
		case 3:
			text = "启动";//建模
			func = "appList.startApp(" + appId + ")";
			break;
		case 4:
			text = "停止";//组件建模
			func = "appList.stopApp(" + appId + ")";
			break;
		case 5:
			text = "容错";//容错
			func = "appList.orchestration(" + appId + ",3)";
			break;
		case 6:
			text = "删除";
			func = "appList.deleteApp(" + appId +")";
			break;
		case 7:
			text = "启动";
			func = "appList.startApp(" + appId +")";
			break;
//		case 7:
//			text = "部署编排";
//			func = "appList.deployOrchestration(" + appId + ")";
		default:
			break;
		}
		if (operationId == 1) {
			html += '<i class="fa '
					+ style
					+ ' text-success small"></i> '
					+ '<a class="link-btn" data-toggle="collapse" data-parent="#app-list" href="#detail-app-'
					+ appId
					+ '" aria-expanded="true" aria-controls="detail-app-'
					+ appId + '"> 应用概览 </a>';
		} else {
			html += '<i class="fa ' + style + ' text-success small"></i> '
					+ '<a class="link-btn" href="javascript:'
					+ (func == undefined ? 'void(0)' : func) + '">' + text
					+ '</a>';
		}
		return html;
	},

	orchestration : function(appId, tab) {
		console.log("拓扑化:"+appId+":"+tab);
		loadPage(dURIs.viewsURI.appOrchestration, function() {
			appPanel.initForEdit({
				appId : appId,
				tab : tab
			});
		});
	},
	startApp: function(appId){
		if(confirm("您确定启动应用?")){
			ajaxGetJson(dURIs.apps.startApp + "/" + appId, null, appList.startSuccess, defaultErrorFunc, true);
		}
	},
	startSuccess : function() {
		loadPage(dURIs.viewsURI.appList, null);
		showSuccess("应用启动成功！");
	},
	stopApp: function(appId){
		if(confirm("您确定停止应用?")){
			ajaxGetJson(dURIs.apps.stopApp + "/" + appId, null, appList.stopSuccess, defaultErrorFunc, true);
		}
	},
	stopSuccess : function() {
		loadPage(dURIs.viewsURI.appList, null);
		showSuccess("停止应用成功！");
	},
	deploy : function(appId) {
		ajaxPostJsonAuthc(dURIs.appURI + "/" + appId, null,
				appList.deploySuccess, defaultErrorFunc, true);
	},

	deploySuccess : function() {
		loadPage(dURIs.viewsURI.appList, null);
		startSuccess();
	},
	
	deleteSuccess : function() {
		loadPage(dURIs.viewsURI.appList, null);
		deleteSuccessFunc();
	},
	deleteApp: function(appId){
		if(confirm("您是否确定删除应用？过程中需要移除相关容器，请耐心等待！")){
			ajaxDeleteJsonAuthc(dURIs.apps.deleteApp + "/" + appId, null, appList.deleteSuccess, defaultErrorFunc, true);
		}
	}
};

$(document).ready(function() {
	appList.init();
	$("#clear").unbind("click");
	$("#clear")
			.click(
					function() {
						ajaxGetJson(dURIs.apps.clear, null, null, null, true);
					});
});