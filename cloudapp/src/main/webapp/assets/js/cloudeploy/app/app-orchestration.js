function dateFtt(fmt,date)   
{ //author: meizz   
  var o = {   
    "M+" : date.getMonth()+1,                 //月份   
    "d+" : date.getDate(),                    //日   
    "h+" : date.getHours(),                   //小时   
    "m+" : date.getMinutes(),                 //分   
    "s+" : date.getSeconds(),                 //秒   
    "q+" : Math.floor((date.getMonth()+3)/3), //季度   
    "S"  : date.getMilliseconds()             //毫秒   
  };   
  if(/(y+)/.test(fmt))   
    fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length));   
  for(var k in o)   
    if(new RegExp("("+ k +")").test(fmt))   
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
  return fmt;   
}
function dlog(str){
	appPanel.orchelogs+=dateFtt("yyyy-MM-dd hh:mm:ss",new Date())+str+"<br/>";
}
function genNodeId() {
	var mydate=new Date();
	var nodeId = "cloudapp"+mydate.getDay()+ mydate.getHours()+ mydate.getMinutes()+mydate.getSeconds()+mydate.getMilliseconds()+mydate.getMilliseconds()+ Math.round(Math.random() * 10000);
	return nodeId;
}
var appPanel = {
	// cachedHosts : new Map(),
	cachedComponents : new Map(),
	cachedNodes : new Map(),
	currentApp : null,
	orchelogs : "",
	cachedContainers : new Map(),
	cachedContainersById : new Map(),
	cachedCustomFiles : null,
	cachedTemplate : new Map(),
	initReady : 0,// 001 hosts ready,010 components ready
	currentTab : -1,
	
	init : function(param) {
		// this.requestHosts();
		// 初始化组件列表
		this.requestComponentList();
		// 初始化事件
		this.initEvents();
		// this.initCustomFileList();
		this.initReady = 2;
	},

	initForEdit : function(param) {
		// console.log("初始化拓扑图"+appPanel.initReady);
		// if (appPanel.initReady == 2) {
		//console.log("lsx进入初始化");
		appPanel.clearGraph();
		appPanel.requestApp(param.appId);
	},

	initCustomFileList : function(selectedFile) {
		if (null == appPanel.cachedCustomFiles) {
			ajaxGetJsonAuthc(dURIs.customFilesURI, null,
					appPanel.requestCustomFileListCallback, null);
		} else {
			orcheHtml.paintCustomFileList(appPanel.cachedCustomFiles.values(),
					selectedFile);
		}
	},

	/**
	 * 请求文件列表的回掉函数
	 * 
	 * @param data
	 */
	requestCustomFileListCallback : function(data) {
		var files = data;
		appPanel.cachedCustomFiles = data;
		// orcheHtml.paintCustomFileList(files);
	},

	showSaveAsBtn : function() {
		$("#btn-save-as").show();
		$("#btn-save-as").unbind("click");
		$("#btn-save-as").click(function() {
			$("#save-app-btn").unbind("click");
			$("#save-app-btn").click(function() {
				console.log("另存?");
				appPanel.saveAs();
			});
			$('#saveModal').modal('show');
		});
	},
	// 初始化点击部署的事件
	initEvents : function() {
		// 保存按钮
		$("#save-and-deploy").unbind("click");
		$("#save-and-deploy")
				.click(
						function() {
							document.getElementById("saveModalHeader").innerHTML = '<button type="button" class="close" data-dismiss="modal">'
									+ '<span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>'
									+ '<h4 class="modal-title" id="myModalLabel">部署应用</h4>';
							// 模态框的保存按钮(真正提交任务的按钮)
							var saveBtn = $("#save-app-btn");
							saveBtn.unbind("click");
							saveBtn.click(function() {
								appPanel.appSubmit();
							});
							$('#saveModal').modal('show');
						});
		// 保存编排按钮
		$("#btn-save").unbind("click");
		$("#btn-save")
				.click(
						function() {
							document.getElementById("saveModalHeader").innerHTML = '<button type="button" class="close" data-dismiss="modal">'
									+ '<span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>'
									+ '<h4 class="modal-title" id="myModalLabel">保存编排</h4>';

							var saveBtn = $("#save-app-btn");
							saveBtn.unbind("click");
							saveBtn.click(function() {
								appPanel.appSave();

							});
							$('#saveModal').modal('show');
						});
		// 生成编排
		$("#start-orche").unbind("click");
		$("#start-orche").click(function() {
			checks = document.getElementsByName("components-checkbox");
			checkComs = [];
			for (i in checks) {
				if (checks[i].checked)
					checkComs.push(checks[i].value);
			}
			if(checkComs.length==0){
				alert("请选择服务后再进行智能化编排！");
			}else{
				ajaxGetJsonAuthc(dURIs.orche.scores, null,
						appPanel.requestScoresCallback, null);
			}
			// 模态框的保存按钮(真正提交任务的按钮)
		/*	$('#models').modal('show');
			$('#start-fire-btn').unbind("click");
			$('#start-fire-btn').click(function() {
				// 模态框的保存按钮(真正提交任务的按钮)
				checks = document.getElementsByName("components-checkbox");
				checkComs = [];
				for (i in checks) {
					if (checks[i].checked)
						checkComs.push(checks[i].value);
				}
				if(checkComs.length==0){
					alert("请选择服务后再进行智能化编排！");
				}else{
					ajaxGetJsonAuthc(dURIs.orche.scores, null,
							appPanel.requestScoresCallback, null);
				}
			});*/

		});
		// 推导日志
		$("#log-btn").unbind("click");
		$("#log-btn")
				.click(
						function() {
							// 模态框的保存按钮(真正提交任务的按钮)
					
							//document.getElementById("logTable");
							//alert(appPanel.orchelogs);
							var x=document.getElementById("log-txt");
							x.innerHTML=appPanel.orchelogs;
							$('#logTable ').modal('show');
						});
		//全选
		$("#chk-all").unbind("click");
		$("#chk-all")
				.click(
						function() {
							checks = document.getElementsByName("components-checkbox");
							checkComs = [];
							for (i in checks) {
									checks[i].checked=true;
							}
						});
		//全不选
		$("#chk-no").unbind("click");
		$("#chk-no")
				.click(
						function() {
							checks = document.getElementsByName("components-checkbox");
							checkComs = [];
							for (i in checks) {
									checks[i].checked=false;
							}
						});
	},

	nodeFactory : {
		createNode : function(nodeId, component) {
			var node = new ContainerNode(nodeId, component.id);
			return node;
		},
		createNodeFromContainer : function(container) {
			// var hosts = new Array();
			// var host = appPanel.cachedHosts.get(container.masterId);
			// hosts.push(host);
			// var component = appPanel.cachedComponents
			// .get(container.componentId);
			// var containerNode = new ContainerNode(container.nodeId,
			// component,
			// hosts, container.name, container.port, container.initCount,
			// container.maxCount, container.status, container.id);
			// containerNode.setTemplates(container.templates);
			// containerNode.setAttributes(container.attributes);
			console.log("从容器中新建节点对象");
			var containerNode = new ContainerNode(container);
			return containerNode;
		},

		createNodeFromContainerInstance : function(containerInstance,
				containerId, nodeId) {
			return new ContainerInstanceNode(nodeId, containerInstance.name,
					containerInstance.port, containerInstance.status,
					containerInstance.id, containerId);
		}
	},

	/**
	 * 请求主机列表，暂时木有使用
	 */
	requestHosts : function() {
		ajaxGetJsonAuthc(dURIs.hostURI, null, appPanel.requestHostsCallback,
				null);
	},

	requestHostsCallback : function(data) {
		var hosts = data;
		appPanel.cachedHosts.clear();
		for ( var i in hosts) {
			appPanel.cachedHosts.put(hosts[i].id, hosts[i]);
		}
		appPanel.initReady |= 1;
	},

	/**
	 * 请求组件类型,暂时木有使用
	 */
	requestComponentTypes : function() {
		ajaxGetJsonAuthc(dURIs.chartsURI, null,
				appPanel.requestComponentTypesCallback, null);
	},

	requestComponentTypesCallback : function(data) {
		// orcheHtml.paintComponentMeta(data);
		appPanel.requestComponentList();
	},
	
	requestScoresCallback : function(scores) {
		checks = document.getElementsByName("components-checkbox");
		var checkedComIds = new Array();
		for (i in checks) {
			if (checks[i].checked)
				checkedComIds.push(checks[i].value);
		}
		//只编排一个组件
		if(checkedComIds.length==1){
			var componentId=checkedComIds[0];
			console.log("组件ID"+componentId);
			var component = appPanel.cachedComponents.get(componentId);
			var nodeId = ""+myuid();
			var node = appPanel.nodeFactory.createNode(nodeId, component);
			appPanel.cachedNodes.put(nodeId, node);
			orcheHtml.paintNode(nodeId);
			dlog("只编排一个组件:"+component.name);
			return ;
		}
		//计算分数
		var SrcDes=new Map();
		for(var i in scores){
			//console.log("key"+scores[i].src+scores[i].des+":"+scores[i].score);
			SrcDes.put(scores[i].src+scores[i].des,scores[i].score);
		}
		//计算所有组件分数
		var components=new Array();
		var component;
		var ComSequence=new Map();
		var NameToID=new Map();
		for(var i in checkedComIds){
			if(checkedComIds[i]!=null&&checkedComIds[i]!=undefined){
				component=appPanel.cachedComponents.get(checkedComIds[i]);
				if(component!=null&&component!=undefined){
					components.push(component);
					//var nodeId = ""+myuid().toString();
					//console.log(nodeId);
					//ComSequence.put(component.name,nodeId);
					dlog("收集组件编排元素:"+component.name);
					NameToID.put(component.name,component.id);
				}
			}
		}
		dlog("-----------计算组件关联距离分数-----------");
		var score=null;
		var srcA=new Array();
		var desA=new Array();
		var scoreA=new Array();
		var sd=new Map();
		for(i=0;i<components.length;i++){
			for(j=0;j<components.length;j++){
				if(i==j) continue;
				//console.log("目标:"+components[i].name+components[j].name);
				if(SrcDes.containsKey(components[i].name+components[j].name)){
					dlog("组件关联距离分数:"+components[i].name+"-->"+components[j].name+":"
					+SrcDes.get(components[i].name+components[j].name));
					if(parseFloat(SrcDes.get(components[i].name+components[j].name))>50){
						//alert("sadsa");
						srcA.push(components[i].name);
						desA.push(components[j].name);
						if(!sd.containsKey(components[i].name)){
							sd.put(components[i].name,new Array());
						}
						sd.get(components[i].name).push(components[j].name);
						scoreA.push(SrcDes.get(components[i].name+components[j].name));
					}
				}else{
					dlog("组件关联距离分数:"+components[i].name+"-->"+components[j].name+":"+Math.random());
				}
			}
		}
		dlog("-----------过滤组件关联距离分数-----------");
		var maxLevel=0;
		for(var i=0;i<srcA.length;i++){
			dlog(srcA[i]+"-->"+desA[i]+":"+scoreA[i]);
		}
		var levelCom=new Map();
		for(var i=0;i<components.length;i++){
			var level=0;
			var tmp=components[i].name;
			var childs=new Array();
			childs.push(tmp);
			while(childs.length>0){
				level++;
				var tmpArray=new Array();
				for(var ci=0;ci<childs.length;ci++ ){
					if(sd.containsKey(childs[ci])){
						var tmpch=sd.get(childs[ci]);
						for(var k=0;k<tmpch.length;k++){
							tmpArray.push(tmpch[k]);
							console.log("push:"+tmpch[k]+":"+tmpArray.length);
						}
					}
				}
				childs=tmpArray;
			}
			level--;
			if(level>maxLevel) maxLevel=level;
			if(!levelCom.containsKey(level)){
				levelCom.put(level,new Array());
			}
			levelCom.get(level).push(components[i]);
			dlog("计算关联深度:"+level+":"+components[i].name);
		}
		dlog("-----------开始智能化构图编排-----------");
		var left=-50;
		var top=0;
		var guid=1;
		console.log(JSON.stringify(levelCom));
		var cid2nid=new Map();
		for(var i=maxLevel;i>=0;i--){
			left=left+170;
			if(levelCom.containsKey(i)){
				console.log("进入构图："+i);
				var lec=levelCom.get(i);
				//alert(lec.length);
				top=-70;
				for(var j=0;j<lec.length;j++){
					top=top+120;
					console.log("组件名字"+lec[j].name);
					var componentId=lec[j].id;
					console.log("组件ID"+componentId);
					var component = appPanel.cachedComponents.get(componentId);
					var nodeId=genNodeId();
					var node = appPanel.nodeFactory.createNode(nodeId, component);
					appPanel.cachedNodes.put(nodeId, node);
					orcheHtml.paintNodeLT(nodeId,left,top);
					dlog("编排组件:"+component.name+",指定序列号:"+nodeId);
					cid2nid.put(component.name,nodeId);
				}
			}else{
				console.log(levelCom);
			}
		}
		for(var i=0;i<srcA.length;i++){
			dlog("自动机描线法:"+cid2nid.get(srcA[i])+"-->"+cid2nid.get(desA[i]));
			orcheHtml.paintEdge(cid2nid.get(srcA[i]),cid2nid.get(desA[i]));
		}
		checks = document.getElementsByName("components-checkbox");
		checkComs = [];
		for (i in checks) {
				checks[i].checked=false;
		}
	},
	/**
	 * 请求组件列表
	 */
	requestComponentList : function() {
		// http://localhost:8080/cloudapp/v2/charts/listcharts
		ajaxGetJsonAuthc(dURIs.chartsURI.listDetailedCharts, null,
				appPanel.requestComponentsCallback, null);
	},
	requestComponentsCallback : function(components) {
		// 清除组件的缓存
		appPanel.cachedComponents.clear();
		for ( var i in components) {
			// console.log(components[i]);
			
			appPanel.cachedComponents.put(components[i].id, components[i]);
		}
		// 绘制组件列表和绑定事件
		orcheHtml.paintComponentList(components);
		appPanel.initReady |= 2;
	},

	/**
	 * 请求application
	 * 
	 * @param appId
	 */
	requestApp : function(appId) {
		ajaxGetJsonAuthc(dURIs.apps.appList + "/" + appId, null,
				appPanel.requestAppCallback, null);
	},

	requestAppCallback : function(data) {
		console.log("请求一个app的拓扑数据");
		// console.log("------------------");
		console.log(JSON.stringify(data));
		// console.log("------------------");
		var app = data;
		appPanel.currentApp = app;
		appPanel.cachedContainers.clear();
		appPanel.cachedContainersById.clear();
		// set app name
		$("#appName").val(app.name);
		// paint nodes
		for ( var i in app.containers) {
			var container = app.containers[i];
			// console.log("绘制拓扑容器节点:"+JSON.stringify(container));
			if (container.status == "CREATED" || container.status == "DEPLOYED") {
				appPanel.cachedContainers.put(container.nodeId, container);
				appPanel.cachedContainersById.put(container.id, container);
			}
			if (container.status == "CREATED") {
				// var node = appPanel.nodeFactory
				// .createNodeFromContainer(container);
				// node.setParams(container.params);
				// orcheHtml.paintNode(node.component, node.id, node.name,
				// node.port, node.status);
				// appPanel.cachedNodes.put(node.id, node);
				// appPanel.rearrangeElementWithPos(node.id, container.xPos,
				// container.yPos);
				console.log("新建状态:开始绘制容器节点" + container.status);
				var node = appPanel.nodeFactory
						.createNodeFromContainer(container);
				appPanel.cachedNodes.put(node.nodeId, node);
				orcheHtml.paintNode(node.nodeId);
				appPanel.rearrangeElementWithPos(node.nodeId, node.xPos,
						node.yPos);
			} else if (container.status == "DEPLOYED") {
				// console.log("已经部署状态:开始绘制容器节点"+container.status);
				var node = appPanel.nodeFactory
						.createNodeFromContainer(container);
				appPanel.cachedNodes.put(node.nodeId, node);
				orcheHtml.paintNode(node.nodeId);
				appPanel.rearrangeElementWithPos(node.nodeId, node.xPos,
						node.yPos);
				// orcheHtml.paintInstanceNode(node.id, node.name, node.port,
				// node.status);
				// appPanel.cachedNodes.put(node.id, node);

				// for (var j = 0; j < container.instances.length; j++) {
				// var instance = container.instances[j];
				// var node = appPanel.nodeFactory
				// .createNodeFromContainerInstance(instance,
				// container.id, container.nodeId + "-"
				// + instance.seq);
				// orcheHtml.paintInstanceNode(node.id, node.name, node.port,
				// node.status);
				// appPanel.cachedNodes.put(node.id, node);
				// appPanel.rearrangeElementWithPos(node.id, instance.xPos,
				// instance.yPos);
				// }

			}
		}
		// paint edges
		console.log("------paint edges--------");
		console.log(JSON.stringify(app.relations));
		for ( var i in app.relations) {
			var relation = app.relations[i];
			orcheHtml.paintEdge(relation.from, relation.to);
		}

		appInstance.repaintEverything();
	},

	getEndpointIds : function(container) {
		var ids = new Array();
		if (container.status == "CREATED") {
			ids.push(container.nodeId);
		} else if (container.status == "DEPLOYED") {
			for ( var j in container.instances) {
				var instance = container.instances[j];
				ids.push(container.nodeId + "-" + instance.seq);
			}
		}
		return ids;
	},

	rearrangeElementWithPos : function(id, xPos, yPos) {
		var element = $("#" + id);
		var offset = element.offset();
		offset.left = xPos;
		offset.top = yPos;
		element.offset(offset);
	},
	/**
	 * 向绘图区添加一个新的节点
	 * 
	 * @param componentId
	 */
	addNode : function(componentId) {
		var component = appPanel.cachedComponents.get(componentId);
		var nodeId = genNodeId();
		console.log("addNode:" + nodeId);
		var node = appPanel.nodeFactory.createNode(nodeId, component);
		appPanel.cachedNodes.put(nodeId, node);
		orcheHtml.paintNode(nodeId);
	},
	addEnv : function() {
		var envsHtml = "";
		var divid = myuid();
		envsHtml += '<div id="' + divid + '"> ';
		envsHtml += '<input type="text"  placeholder="key">'
				+ '<input  type="text"  placeholder="value">';
		envsHtml += '<button onclick="appPanel.removeEnv(' + divid
				+ ')" type="button" class="btn btn-danger">Delete</button>';
		envsHtml += '</div>';
		$("#envs").append(envsHtml);
	},
	removeEnv : function(divId) {
		$("#" + divId + "").remove();
	},
	/**
	 *  节点的双击事件
	 * 
	 * @param nodeId
	 */
	nodeClick : function(nodeId) {
		var node = appPanel.cachedNodes.get(nodeId);
		console.log("双击节点: " + nodeId + "");
		if (node instanceof ContainerInstanceNode) {
			console.log("ContainerInstanceNode");
			$('.operation-list li').unbind('dblclick');
			$('#operationModal').modal('show');
			$('.operation-list li').bind('dblclick', function() {
				appPanel.doOperationOnInstance(node.getInstanceId(), $(this));
			});
		} else if (node instanceof ContainerNode) {
			console.log("nodeClick:节点双击事件");
			orcheHtml.paintNodeDetail(node);
			$('#detailModal').modal('show');
			// orcheHtml.paintTemplateParams(node);
			// orcheHtml.paintAttributes(node);
			// $("#templates #addTemplate").unbind("click");
			// $("#templates #addTemplate").click(function(){
			// orcheHtml.addTemplate();
			// });
			// $("#attributes #addAttribute").unbind("click");
			// $("#attributes #addAttribute").click(function(){
			// orcheHtml.addAttribute();
			// });
			//			
			// $("#detailModal #initCount").unbind('change');
			// $("#detailModal #initCount").bind(
			// 'change',
			// function() {
			// if (parseInt($(this).val()) > parseInt($(
			// "#detailModal #maxCount").val())) {
			// $(this).val($("#detailModal #maxCount").val());
			// alert("primary instances must be less than max instances！")
			// }
			// });
			$(".editSave").unbind("click");
			$(".editSave").bind("click", function() {
				appPanel.nodeEditSave(nodeId);
				$('#detailModal').modal('hide');
			});
		} else {
			console.log("弹出对话框，进行对话");
			$('#detailModal').modal('show');
			var component = node.component;
			orcheHtml.paintNodeDetailHost(appPanel.cachedHosts.values(),
					node.hosts);
			orcheHtml.paintNodeDetailAction(node);
			orcheHtml.paintTemplateParams(node);
			orcheHtml.paintAttributes(node);

			$("#templates #addTemplate").unbind("click");
			$("#templates #addTemplate").click(function() {
				orcheHtml.addTemplate();
			});

			$("#attributes #addAttribute").unbind("click");
			$("#attributes #addAttribute").click(function() {
				orcheHtml.addAttribute();
			});

			$("#detailModal #initCount").unbind('change');
			$("#detailModal #initCount")
					.bind(
							'change',
							function() {
								if (parseInt($(this).val()) > parseInt($(
										"#detailModal #maxCount").val())) {
									$(this).val(
											$("#detailModal #maxCount").val());
									alert("primary instances must be less than max instances！")
								}
							});
			$(".editSave").unbind("click");
			$(".editSave").bind("click", function() {
				appPanel.nodeEditSave(nodeId);
				$('#detailModal').modal('hide');
			});
		}

	},

	/**
	 * 保存节点的配置信息
	 * 
	 * @param nodeId
	 */
	nodeEditSave : function(nodeId) {
		var node = appPanel.cachedNodes.get(nodeId);
		console.log("----------------nodeEditSave-----------------");
		console.log("保存节点信息" + node.getServiceName());
		node.image = $("#imageName").val();
		node.envs = [];
		node.containerPort = $("#containerPort").val();
		node.nodePort = $("#nodePort").val();
		console.log($("#serviceName").val());
		node.serviceName = $("#serviceName").val();
		node.dependency;
		$('#envs div').each(function(index) {
			// console.log("遍历div" + index);
			var key = "";
			var value = "";
			$(this).children("input").each(function(index) {
				console.log("遍历input" + index + ":" + $(this).val());
				if (index == 0) {
					key = $(this).val();
				}
				if (index == 1) {
					value = $(this).val();
				}
			});
			if (trim(key).length != 0 && trim(value).length != 0) {
				var item = new KV(key, value);
				node.envs.push(item);
			}
		});
		// var node = appPanel.cachedNodes.get(nodeId);
		// console.log("");
		// var component = node.component;
		// var newParams = new Array();
		// var newTemplates = new Array();
		// var newAttributes = new Array();
		// $("#actionParams tbody tr").each(function(index, element) {
		// var value = $(element).find('.paramValue textarea').val();
		// var key = $(element).find('.paramKey').text();
		// if (key != "") {
		// newParams.push({
		// key : key,
		// value : value
		// });
		// }
		// });
		// $("#templates tbody tr").each(function(index, element) {
		// var source = $(element).find('.temSource [name="source"]').val();
		// var target = $(element).find('.temTarget textarea').val();
		// var command = $(element).find('.temCommand textarea').val();
		// if (source != "" && target != "") {
		// newTemplates.push({
		// source : source,
		// target : target,
		// command: command
		// });
		// }
		// });
		// for(var i in newTemplates){
		// alert(newTemplates[i].source)
		// }
		// $("#attributes tbody tr").each(function(index, element) {
		// var attrKey = $(element).find('.attrKey textarea').val();
		// var attrValue = $(element).find('.attrValue textarea').val();
		// if (attrKey != "" && attrValue != "") {
		// newAttributes.push({
		// attrKey : attrKey,
		// attrValue : attrValue
		// });
		// }
		// });
		// node.setParams(newParams);
		// node.setTemplates(newTemplates);
		// node.setAttributes(newAttributes);
		// node.setHosts(appPanel.getTargetHosts());
		// node.setName($("#detailModal #containerName").val());
		// node.setPort(parseInt($("#detailModal #containerPort").val()));
		// node.setInitCount(parseInt($("#detailModal #initCount").val()));
		// node.setMaxCount(parseInt($("#detailModal #maxCount").val()));
		appPanel.cachedNodes.removeByKey(nodeId);
		appPanel.cachedNodes.put(nodeId, node);
		$("#" + nodeId).find(".node-name span").html(node.getServiceName());
		$("#" + nodeId).find(".node-port span").html(node.getNodePort());
		console.log("---------------nodeEditSave-End---------------");
	},

	/**
	 * 获取选中的机器节点
	 * 
	 * @returns {Array}
	 */
	getTargetHosts : function() {
		var hosts = new Array();
		$("#hostNames input[name='hostName']").each(function() {
			if ($(this).prop('checked')) {
				var hostId = parseInt($(this).val());
				var host = appPanel.cachedHosts.get(hostId);
				if (host != false) {
					hosts.push(host);
				}
			}
		});
		return hosts;
	},

	/**
	 * 保存应用并且部署
	 * 
	 * @param appId
	 */
	appSubmit : function() {
		if (!appPanel.validateApp()) {
			showError("应用名不能为空!");
			return;
		}
		console.log("app Name 不空" + appPanel.currentApp);
		// 重置画板的滚动条，保证保存后能正常显示
		appPanel.resetGraphPanelScroll();
		var cachedNodes = appPanel.cachedNodes.values();
		// 新建一个矩阵图
		var nodes = new Map();
		for ( var i in cachedNodes) {
			var node = cachedNodes[i];
			console.log("组件节点服务名:" + node.serviceName);
			// console.log(JSON.stringify(node));
			// if (node instanceof ContainerNode) {
			// var cloneNode=node.clone();
			if (!nodes.containsKey(node.nodeId)) {
				var element = $("#" + node.nodeId);
				node.xPos = element.offset().left;
				node.yPos = element.offset().top;
				console.log("组件节点坐标:" + node.xPos+":"+node.yPos);
				console.log("----------------");
				console.log(JSON.stringify(node));
				console.log("------------------");
				nodes.put(node.nodeId, node);
			}
		}
		var connections = appInstance.getAllConnections();
		var edges = new Map();
		for ( var i in connections) {
			var conn = connections[i];
			var sourceId = conn.sourceId, targetId = conn.targetId;
			console.log(sourceId + "->" + targetId);
			var sourceNode = appPanel.cachedNodes.get(sourceId);
			var targetNode = appPanel.cachedNodes.get(targetId);
			var edge = {
				from : sourceId,
				to : targetId,
			};
			edges.put(edge.from, edge);
		}
		var graph = {
			name : $("#appName").val(),
			nodes : nodes.values(),
			relations : edges.values(),
			state : "DEPLOY"
		};
		console.log("------------------graph------------------");
		console.log(JSON.stringify(graph));
		console.log("------------------graph------------------");
		ajaxPostJsonAuthcWithJsonContent(dURIs.apps.deployApp, graph,
				appPanel.createAppCallBack, defaultErrorFunc, true);
		// if (appPanel.currentApp != null) {
		// ajaxPutJsonAuthcWithJsonContent(dURIs.appURI + "/"
		// + appPanel.currentApp.id, graph,
		// appPanel.createAppCallBack, defaultErrorFunc, true);
		// } else {
		// alert("应用名为空!");
		// // 推送数据到后端
		//			
		// }

	},
	/**
	 * 保存编排不部署
	 */
	appSave : function() {
		if (!appPanel.validateApp()) {
			console.log("APP合法");
			return;
		}
		// return;
		console.log("app Name 不空" + appPanel.currentApp);
		// 重置画板的滚动条，保证保存后能正常显示
		appPanel.resetGraphPanelScroll();
		var cachedNodes = appPanel.cachedNodes.values();
		// 新建一个矩阵图
		var nodes = new Map();
		for ( var i in cachedNodes) {
			var node = cachedNodes[i];
			console.log("组件节点服务名:" + node.serviceName);
			// console.log(JSON.stringify(node));
			// if (node instanceof ContainerNode) {
			// var cloneNode=node.clone();
			if (!nodes.containsKey(node.nodeId)) {
				var element = $("#" + node.nodeId);
				console.log("组件节点坐标:" + element.offset());
				node.xPos = element.offset().left;
				node.yPos = element.offset().top;
				console.log("------------------");
				console.log(JSON.stringify(node));
				console.log("------------------");
				nodes.put(node.nodeId, node);
			}
			// } else {
			// var container = appPanel.cachedContainersById.get(n
			// .getContainerId());
			// if (!nodes.containsKey(container.nodeId)) {
			// nodes.put(container.nodeId, container);
			// }
			// }
		}
		var connections = appInstance.getAllConnections();
		var edges = new Map();
		for ( var i in connections) {
			var conn = connections[i];
			var sourceId = conn.sourceId, targetId = conn.targetId;
			console.log(sourceId + "->" + targetId);
			var sourceNode = appPanel.cachedNodes.get(sourceId);
			var targetNode = appPanel.cachedNodes.get(targetId);
			var edge = {
				from : sourceId,
				to : targetId,
			};
			edges.put(edge.from, edge);
		}
		var graph = {
			name : $("#appName").val(),
			nodes : nodes.values(),
			relations : edges.values(),
			state : "SAVE",
		};
		console.log("------------------graph------------------");
		console.log(JSON.stringify(graph));
		console.log("------------------graph------------------");
		if (appPanel.currentApp != null) {
			ajaxPostJsonAuthcWithJsonContent(dURIs.apps.deployApp, graph,
					appPanel.createAppCallBack, defaultErrorFunc, true);
		} else {
			alert("应用名为空!");
			// 推送数据到后端
		}
		$('#saveModal').modal('hide');
	},

	/**
	 * 另存为新的应用
	 */
	saveAs : function() {
		var cachedNodes = appPanel.cachedNodes.values();
		appPanel.cachedNodes.clear();
		var idMap = new Map();
		for ( var i in cachedNodes) {
			var node = cachedNodes[i];
			idMap.put(node.id, genNodeId());
			node.id = idMap.get(node.id);
			appPanel.cachedNodes.put(node.id, node);
		}
		var edges = new Array();
		var connections = appInstance.getAllConnections();
		// change ids of source&target of each connection
		for ( var i in connections) {
			var conn = connections[i];
			edges.push({
				from : idMap.get(parseInt(conn.sourceId)),
				to : idMap.get(parseInt(conn.targetId)),
			});
		}
		appInstance.deleteEveryEndpoint();
		for ( var i in idMap.keys()) {
			var oldId = idMap.keys()[i];
			var newId = idMap.get(oldId);
			var element = $("#" + oldId);
			element.attr("id", newId);
			element.unbind("dblclick");
			element.dblclick(function() {
				appPanel.nodeClick($(this).attr("id"));
			});

			orcheHtml.paintEndPoint(newId);
		}
		for ( var i in edges) {
			var edge = edges[i];
			orcheHtml.paintEdge(edge.from, edge.to);
		}
		appPanel.currentApp = null;
		appPanel.appSubmit();
		$('#saveModal').modal('hide');
	},

	/**
	 * 在instance上做操作
	 * 
	 * @param instanceId
	 */
	doOperationOnInstance : function(instanceId, element) {
		if (confirm("execute " + element.find(".oper-text").html() + " action?")) {
			ajaxPutJsonAuthc(dURIs.appInstanceURI + "/" + instanceId
					+ "?operation=" + element.attr("data-oper-type"), null,
					appPanel.doOperationSuccess, appPanel.doOperationError,
					true);
			$('#operationModal').modal('hide');
		}
	},

	doOperationSuccess : function() {
		appPanel.initForEdit({
			appId : appPanel.currentApp.id
		});
		defaultSuccessFunc();
	},

	doOperationError : function(data) {
		alert(data.message);
	},

	/**
	 * 创建新应用的回调函数
	 * 
	 * @param data
	 */
	createAppCallBack : function(data) {
		console.log(data.IsSuc);
		console.log(data.reason);
		if(data.IsSuc=='true'){
//			if(){
//				deploySuccessFunc();
//			}else{
			deploySuccessFunc(data.reason);
//			}
			
			$('#saveModal').modal('hide');
		}else{
			showError(data.reason);
			$('#saveModal').modal('hide');
		}
		
		// var app = data;
		// appPanel.initForEdit({
		// appId : app.id
		// });
		// appPanel.clearGraph();
		// //appPanel.requestApp(param.appId);
		// appPanel.showSaveAsBtn();
		// appPanel.currentTab = param.tab;
		// if (appPanel.currentTab == 2 || appPanel.currentTab == 3) {
		// $("#group-list").hide();
		// }
		// $(".operation-list li[data-view-id='" + appPanel.currentTab + "']")
		// .css('display', 'inline-block');
		
		
		// appMain.cleanState();
		// $("#app-list-btn").addClass("active");
		// /v2/views/applications/list
		// loadPage(dURIs.viewsURI.appList, appList.init);
	},

	/**
	 * 参数验证
	 * 
	 * @returns
	 */
	validateApp : function() {
		var appName = $("#appName").val().replace(/ /, "");
		if (appName.length <= 0) {
			
			return false;
		} else {
			appPanel.currentApp = appName;
		}
		// return appPanel.checkHostSelect();
		return true;
	},

	/**
	 * 检查是否所有节点都选择了主机
	 * 
	 * @returns {Boolean}
	 */
	checkHostSelect : function() {
		var cachedNodes = appPanel.cachedNodes.values();
		for ( var i in cachedNodes) {
			var node = cachedNodes[i];
			if (node instanceof ContainerNode
					&& (!verifyParam(node.getHosts()) || node.getHosts().length <= 0)) {
				showError("please select hosts for\"" + node.getName() | +"\"");
				return false;
			}
		}
		return true;
	},

	/**
	 * 清除图
	 */
	clearGraph : function() {
		if (verifyParam(appInstance)) {
			appInstance.deleteEveryEndpoint();
		}
		for ( var i in appPanel.cachedNodes.keys()) {
			var node = $("#" + appPanel.cachedNodes.keys()[i]);
			node.remove();
		}
		appPanel.cachedNodes.clear();
		appPanel.currentApp = null;
	},

	/**
	 * 将graph panel的滚动条滑动到开始位置
	 */
	resetGraphPanelScroll : function() {
		appPanel.resetScroll($("#graph-panel"));
	},

	/**
	 * 滑动scrollTo 到container的左上角
	 * 
	 * @param container
	 * @param scrollTo
	 */
	resetScroll : function(scrollTo) {
		scrollTo.scrollTop(0);
		scrollTo.scrollLeft(0);
	},

	deleteNode : function(node) {
		console.log("deleteNode : " +node.id);
		nodeId=node.id;
		if (confirm("delete this node?")) {
			var node = $("#" + nodeId);
			var endPoints = appInstance.getEndpoints(node);
			for ( var i in endPoints) {
				appInstance.deleteEndpoint(endPoints[i].getUuid());
			}
			appPanel.cachedNodes.removeByKey(nodeId);
			node.remove();
		}
	}
};

var orcheHtml = {
	/**
	 * 绘制配置对话框中的节点信息细节
	 * 
	 * @param node
	 */
	paintNodeDetail : function(node) {
		console.log("paintNodeDetail:" + node);
		var html = '';
		console.log("绘制节点,节点信息:" + node.serviceName);
		var serviceName = node.serviceName;
		var imageName = node.image;
		console.log("imageName:" + imageName);
		var nodePort = node.nodePort;
		var containerPort = node.containerPort;
		$("#detailModal #serviceName").val(serviceName);
		$("#detailModal #imageName").val(imageName);
		$("#detailModal #nodePort").val(nodePort);
		$("#detailModal #containerPort").val(containerPort);
		// 获取环境变量
		var envs = node.envs;
		var envsHtml = "";
		// 第一个add,其他delete
		for ( var i in envs) {
			var divid = myuid();
			envsHtml += '<div id="' + divid + '" > ';
			envsHtml += '<input type="text"  value="' + envs[i].name + '">'
					+ '<input type="text"  value="' + envs[i].value + '">';
			if (i == 0) {
				envsHtml += '<button onclick="javascript:appPanel.addEnv()" type="button"  class="btn btn-success">Add</button>';
			} else {
				envsHtml += '<button onclick="javascript:appPanel.removeEnv('
						+ divid
						+ ')" type="button" class="btn btn-danger">Delete</button>';
			}
			envsHtml += "</div>";

		}
		// 第一个add
		if (envs.length == 0) {
			var divid = myuid();
			envsHtml += '<div id="' + divid + '"> ';
			envsHtml += '<input type="text"  placeholder="key">'
					+ '<input  type="text"  placeholder="value">';
			envsHtml += '<button   onclick="javascript:appPanel.addEnv()"  type="button" class="btn btn-success">Add</button>';
			envsHtml += '</div>';
		}

		$("#envs").html(envsHtml);
		// $("#detailModal #containerPort").val(node.getPort());
		// $("#detailModal #initCount").val(node.getInitCount());
		// $("#detailModal #maxCount").val(node.getMaxCount());
		// orcheHtml.paintActionParams(node.action.params, node.params);

		var dependency = node.dependency;
		var dependencyHtml = "";
		if (dependency.size() != 0) {
			var values = dependency.values();
			console.log("values");
			for ( var i in values) {
				if (appPanel.cachedNodes.containsKey(values[i].nodeId)) {
					console.log("i is " + i);
					dependencyHtml += '<div>';
					dependencyHtml += '<input   type="text"  value="'
							+ values[i].serviceName + '">';
					dependencyHtml += '</div>';
				} else {
					values[i].dependency.removeByKey(values[i].nodeId);
				}
			}
			$("#dependent").show();
			$("#dependents").html(dependencyHtml);
		} else {
			$("#dependent").hide();
			$("#dependents").html(dependencyHtml);
		}

	},
	/**
	 * 暂时不需要，以前用来分类绘制组件的
	 * 
	 * @param componentTypes
	 */
	paintComponentMeta : function(componentTypes) {
		var types = componentTypes;
		console.log("paintComponentMeta " + componentTypes);
		var html = '';
		for ( var i in types) {
			var type = types[i];
			if (type.name != "PACKAGE::DOCKER") {
				continue;
			}
			var typeName = type.name.replace(/::/g, "_");
			var headingId = 'heading-' + typeName, bodyId = typeName + '-list';
			html += '<div class="panel panel-default">'
					+ '<div class="panel-heading" role="tab" id="'
					+ headingId
					+ '">'
					+ '<h4 class="panel-title">'
					+ '<a data-toggle="collapse" data-parent="#panel-group-operations"'
					+ ' href="#'
					+ bodyId
					+ '" aria-expanded="true" aria-controls="'
					+ bodyId
					+ '">'
					+ type.displayName
					+ ' <span class="badge">0</span></a>'
					+ '</h4></div>'
					+ '<div id="'
					+ bodyId
					+ '" class="panel-collapse collapse" role="tabpanel" aria-labelledby="'
					+ headingId + '">' + '<ul class="list-group"></ul>'
					+ '</div></div>';
		}
		$("#panel-group-operations").html(html);
	},

	/**
	 * 绘制组件列表
	 * 
	 * @param components
	 */
	paintComponentList : function(components) {
		//var next='<div class="dropdown" data-control="checkbox-dropdown"> <label class="dropdown-label">Select</label> <div class="dropdown-list"> <a href="#" data-toggle="check-all" class="dropdown-option"> Check All </a> <label class="dropdown-option"> <input type="checkbox" name="dropdown-group" value="Selection 1" /> Selection One </label> <label class="dropdown-option"> <input type="checkbox" name="dropdown-group" value="Selection 2" /> Selection Two </label> <label class="dropdown-option"> <input type="checkbox" name="dropdown-group" value="Selection 3" /> Selection Three </label> <label class="dropdown-option"> <input type="checkbox" name="dropdown-group" value="Selection 4" /> Selection Four </label> <label class="dropdown-option"> <input type="checkbox" name="dropdown-group" value="Selection 5" /> Selection Five </label> </div> ';
		var group = $("#panel-group-operations");
		//group.append(next);
		for ( var i in components) {
			var component = components[i];
			console.log("paintComponentList:" + component.name);
			dlog("初始化组件列表："+ component.name);
			var html = '<li class="list-group-item" ><input type="checkbox" name="components-checkbox" value="'
					+ component.id
					+ '" >&nbsp&nbsp'
					+ component.name
					+ '</input>'
					+ '<i class="fa fa-plus pull-right" onclick="javascript:appPanel.addNode('
					+ component.id + ')" ></i>' + '</li>';
			group.append(html);
		}
		//group.style.display="none";
	},
	/**
	 * 绘制节点
	 * 
	 * @param component
	 * @param nodeId
	 * @param actionName
	 * @returns
	 */
	paintNodeLT : function(nodeId,left,top) {
		// 根据节点
		var node = appPanel.cachedNodes.get(nodeId);
		console.log("paintNode:nodeId " + node.nodeId);
		console.log("paintNode:cId " + node.componentId+"left"+left+"top"+top);
		//console.log("绘制node节点信息" + JSON.stringify(node));
		var nodeName = node.serviceName;
		var containerPort = node.containerPort;
		var nodeStatus = node.nodeStatus;
		var html = '<div id='
				+ nodeId
				+' style="left:' +left+'px; top: '+top+'px;"'
				+ ' data-component-id="'
				+ node.componentId
				+ '" class="graph-node" data-toggle="tooltip" title="'
				+ nodeName
				+ '">'
				+ '<div class="node-title"><i class="fa fa-minus-circle node-del-btn" '+
				'onclick="javascript:appPanel.deleteNode('
				+this  +  ')"></i></div>'
				+ '<div class="node-name">组件名:<span>' + nodeName
				+ '</span></div>' + '<div class="node-port">容器端口:<span>'
				+ containerPort + '</span></div>' + '<div class="node-status">状态:'
				+ nodeStatus + '</div>' + '</div>';
		$("#tmp-panel").append(html);
		$("#" + nodeId).unbind("dblclick");
		$("#" + nodeId).dblclick(function() {
			// 节点双击事件
			appPanel.nodeClick(nodeId);
		});
		orcheHtml.paintEndPoint(nodeId);
		return nodeId;
	},
	/**
	 * 绘制节点
	 * 
	 * @param component
	 * @param nodeId
	 * @param actionName
	 * @returns
	 */
	paintNode : function(nodeId) {
		// 根据节点
		var node = appPanel.cachedNodes.get(nodeId);
		console.log("paintNode:nodeId " + node.nodeId);
		console.log("paintNode:cId " + node.componentId);
		//console.log("绘制node节点信息" + JSON.stringify(node));
		var nodeName = node.serviceName;
		var containerPort = node.containerPort;
		if(containerPort==undefined)
			containerPort="";
		var nodeStatus = node.nodeStatus;
		console.log(nodePort);
		var html = '<div id='
				+ nodeId
				+ ' data-component-id="'
				+ node.componentId
				+ '" class="graph-node" data-toggle="tooltip" data-placement="left" title="'
				+ nodeName
				+ '">'
				+ '<div class="node-title"><i class="fa fa-minus-circle node-del-btn" onclick="javascript:appPanel.deleteNode('
				+ nodeId + ')"></i></div>'
				+ '<div class="node-name">组件名:<span>' + nodeName
				+ '</span></div>' + '<div class="node-port">容器端口:<span>'
				+ containerPort + '</span></div>' + '<div class="node-status">状态:'
				+ nodeStatus + '</div>' + '</div>';
		$("#tmp-panel").append(html);
		$("#" + nodeId).unbind("dblclick");
		$("#" + nodeId).dblclick(function() {
			// 节点双击事件
			appPanel.nodeClick(nodeId);
		});
		orcheHtml.paintEndPoint(nodeId);
		return nodeId;
	},

	/**
	 * 绘制部署实例节点
	 * 
	 * @param nodeId
	 * @param nodeName
	 * @param nodePort
	 * @param nodeStatus
	 * @returns
	 */
	paintInstanceNode : function(nodeId, nodeName, nodePort, nodeStatus) {
		var id = verifyParam(nodeId) ? nodeId : genNodeId();
		var nodeName = verifyParam(nodeName) ? nodeName : component.displayName;
		var nodePort = verifyParam(nodePort) ? nodePort : "";
		var statusStyle = "graph-node-" + nodeStatus.toLowerCase();
		var nodeStatus = appMain.statusMap
				.get(verifyParam(nodeStatus) ? nodeStatus : "RUNNING");
		var html = '<div id=' + id + ' class="graph-node graph-node-deployed '
				+ statusStyle
				+ '" data-toggle="tooltip" data-placement="left" title="'
				+ nodeName + '">' + '<div class="node-title"></div>'
				+ '<div class="node-name">name:<span>' + nodeName
				+ '</span></div>' + '<div class="node-port">port:<span>'
				+ nodePort + '</span></div>'
				+ '<div class="node-status">status:<span>' + nodeStatus
				+ '</span></div></div>';
		$("#tmp-panel").append(html);
		$("#" + id).unbind("dblclick");
		$("#" + id).dblclick(function() {
			appPanel.nodeClick(id);
		});
		orcheHtml.paintEndPoint(id);
		return id;
	},

	/**
	 * 绘制连接点
	 * 
	 * @param id
	 */
	paintEndPoint : function(id) {
		var instance = appInstance;
		instance.doWhileSuspended(function() {

			instance.addEndpoint('' + id, targetEndpoint, {
				anchor : [ "Left" ],
				uuid : id + "Left"
			});
			instance.addEndpoint('' + id, sourceEndpoint, {
				anchor : [ "Right" ],
				uuid : id + "Right"
			});
			instance.draggable($("#" + id));
		});
	},

	/**
	 * 绘制配置对话框中的主机列表
	 * 
	 * @param hosts
	 * @param currentHosts
	 */
	paintNodeDetailHost : function(hosts, currentHosts) {
		var html = '';
		for ( var i in hosts) {
			var host = hosts[i];
			html += '<li><div class="checkbox"><label><input type="checkbox" name="hostName" value="'
					+ host.id
					+ '"> '
					+ host.hostName
					+ ' ('
					+ host.hostIP
					+ ')</label></div></li>';
		}
		$("#detailModal #hostNames ul").html(html);
		for ( var i in currentHosts) {
			var host = currentHosts[i];
			$("#hostNames input[name='hostName'][value='" + host.id + "']")
					.prop("checked", true);
		}
	},

	/**
	 * 绘制配置对话框中的节点参数信息
	 * 
	 * @param params
	 * @param userParams
	 */
	paintActionParams : function(params, userParams) {
		var html = '';
		if (params.length > 0) {
			for ( var i in params) {
				var param = params[i];
				var value = param.defaultValue;
				if (verifyParam(userParams)) {
					for ( var j in userParams) {
						if (userParams[j].key == param.paramKey) {
							value = userParams[j].value;
						}
					}
				}
				value = escapeToHtml(value);
				html += '<tr style="'
						+ (param.paramKey == "port" ? "display:none" : "")
						+ '"><td class="paramKey">' + param.paramKey
						+ '</td><td class="paramValue">'
						+ '<textarea class="form-control" rows="1">' + value
						+ '</textarea></td><td>' + param.description
						+ '</td></tr>';
			}
		} else {
			html = DHtml.emptyRow(3);
		}

		$("#detailModal #actionParams tbody").html(html);
	},
	/**
	 * 画边
	 * 
	 * @param from
	 * @param to
	 * @param label
	 */
	paintEdge : function(from, to) {
		console.log("绘制在");
		var conn = appInstance.connect({
			uuids : [ from + "Right", to + "Left" ]
		});
		conn.unbind("dblclick");
		conn.bind("dblclick", function(connection, originalEvent) {
			var srcId = connInfo.connection.sourceId;
			var tarId = connInfo.connection.targetId;
			console.log(connInfo.connection.sourceId + "->"
					+ connInfo.connection.targetId);
			if (confirm("delete edge? " + connInfo.connection.sourceId + "->"
					+ connInfo.connection.targetId)) {
				jsPlumb.detach(conn);
			}
		});

	},

	/**
	 * 增加templates
	 */
	paintTemplateParams : function(node) {
		var html = '';
		var thead = '';
		var templates = node.templates;
		if (templates.length > 0) {
			thead += "<tr>" + "<th>template</th>" + "<th>target path</th>"
					+ "<th>reload cmd(optional)</th>"
					+ '<th class="deleterow">'
					+ '<a href="#" role="button" class="text-warning">'
					+ '<i class="fa fa-minus-circle"></i>'
			'</a>' + '</th>' + "</tr>";
			$("#detailModal #templates thead").html(thead);

			for ( var i in templates) {
				var template = templates[i];
				html += "<tr>" + '<td class="temSource">'
						+ '<textarea class="form-control" name="source">'
						+ template.source + '</textarea>' + "</td>"
						+ '<td class="temTarget">'
						+ '<textarea class="form-control" name="target">'
						+ template.target + '</textarea>' + "</td>"
						+ '<td class="temCommand">'
						+ '<textarea class="form-control" name="command">'
						+ template.command + '</textarea>' + "</td>"
						+ '<td class="deleterow">'
						+ '<a href="#" role="button" class="text-warning">'
						+ '<i class="fa fa-minus-circle"></i>'
				'</a>' + "</td>"
				"</tr>";
			}
			$(".deleterow").unbind("click");
			$(".deleterow").bind('click', function() {
				var $killrow = $(this).parent('tr');
				$killrow.addClass("danger");
				$killrow.fadeOut(1000, function() {
					$(this).remove();
				});
			});
		}
		$("#detailModal #templates tbody").html(html);
	},

	addTemplate : function() {
		var html = "";
		var thead = "";
		var fileSelect = '<select class="form-control" name="source">';
		for ( var i in appPanel.cachedCustomFiles) {
			fileSelect += '<option value="'
					+ appPanel.cachedCustomFiles[i].fileKey + '">'
					+ appPanel.cachedCustomFiles[i].name + '</option>';
		}
		fileSelect += "</select>";
		if ($("#detailModal #templates thead tr").length <= 0) {
			thead += "<tr>" + "<th>template</th>" + "<th>target path</th>"
					+ "<th>reload command(optional)</th>"
					+ '<th class="deleterow">'
					+ '<a href="#" role="button" class="text-warning">'
					+ '<i class="fa fa-minus-circle"></i>'
			'</a>' + '</th>' + "</tr>";
			$("#detailModal #templates thead").html(thead);
		}
		html += "<tr>" + '<td class="temSource">' + fileSelect + "</td>"
				+ '<td class="temTarget">'
				+ '<textarea class="form-control" name="target">'
				+ '</textarea>' + "</td>" + '<td class="temCommand">'
				+ '<textarea class="form-control" name="command">'
				+ '</textarea>' + "</td>" + '<td class="deleterow">'
				+ '<a href="#" role="button" class="text-warning">'
				+ '<i class="fa fa-minus-circle"></i>'
		'</a>' + "</td>"
		"</tr>";
		$("#detailModal #templates tbody").append(html);
		$(".deleterow").unbind("click");
		$(".deleterow").bind('click', function() {
			var $killrow = $(this).parent('tr');
			$killrow.addClass("danger");
			$killrow.fadeOut(1000, function() {
				$(this).remove();
			});
		});
	},

	paintAttributes : function(node) {
		var attributes = node.attributes;
		var html = '';
		var thead = '';
		if (attributes.length > 0) {
			thead += "<tr>" + "<th>AttrKey</th>" + "<th>AttrValue</th>"
					+ '<th class="deleterow">'
					+ '<a href="#" role="button" class="text-warning">'
					+ '<i class="fa fa-minus-circle"></i>'
			'</a>' + '</th>' + "</tr>";
			$("#detailModal #attributes thead").html(thead);
			for ( var i in attributes) {
				var attribute = attributes[i];
				html += "<tr>" + '<td class="attrKey">'
						+ '<textarea class="form-control" name="attrKey">'
						+ attribute.attrKey + '</textarea>' + "</td>"
						+ '<td class="attrValue">'
						+ '<textarea class="form-control" name="attrValue">'
						+ attribute.attrValue + '</textarea>' + "</td>"
						+ '<td class="deleterow">'
						+ '<a href="#" role="button" class="text-warning">'
						+ '<i class="fa fa-minus-circle"></i>'
				'</a>' + "</td>"
				"</tr>";
			}
			$(".deleterow").unbind("click");
			$(".deleterow").bind('click', function() {
				var $killrow = $(this).parent('tr');
				$killrow.addClass("danger");
				$killrow.fadeOut(1000, function() {
					$(this).remove();
				});
			});
		}
		$("#detailModal #attributes tbody").html(html);
	},

	addAttribute : function() {
		var html = "";
		var thead = "";
		if ($("#detailModal #attributes thead tr").length <= 0) {
			thead += "<tr>" + "<th>AttrKey</th>" + "<th>AttrValue</th>"
					+ '<th class="deleterow">'
					+ '<a href="#" role="button" class="text-warning">'
					+ '<i class="fa fa-minus-circle"></i>'
			'</a>' + '</th>' + "</tr>";
			$("#detailModal #attributes thead").html(thead);
		}
		html += "<tr>" + '<td class="attrKey">'
				+ '<textarea class="form-control" name="attrKey">'
				+ '</textarea>' + "</td>" + '<td class="attrValue">'
				+ '<textarea class="form-control" name="attrValue">'
				+ '</textarea>' + "</td>" + '<td class="deleterow">'
				+ '<a href="#" role="button" class="text-warning">'
				+ '<i class="fa fa-minus-circle"></i>'
		'</a>' + "</td>"
		"</tr>";
		$("#detailModal #attributes tbody").append(html);
		$(".deleterow").unbind("click");
		$(".deleterow").bind('click', function() {
			var $killrow = $(this).parent('tr');
			$killrow.addClass("danger");
			$killrow.fadeOut(1000, function() {
				$(this).remove();
			});
		});
	},

	paintCustomFileList : function(files, selectedFile) {

	}
};
(function($) {
    var CheckboxDropdown = function(el) {
        var _this = this;
        this.isOpen = false;
        this.areAllChecked = false;
        this.$el = $(el);
        this.$label = this.$el.find('.dropdown-label');
        this.$checkAll = this.$el.find('[data-toggle="check-all"]').first();
        this.$inputs = this.$el.find('[type="checkbox"]');

        this.onCheckBox();

        this.$label.on('click', function(e) {
            e.preventDefault();
            _this.toggleOpen();
        });

        this.$checkAll.on('click', function(e) {
            e.preventDefault();
            _this.onCheckAll();
        });

        this.$inputs.on('change', function(e) {
            _this.onCheckBox();
        });
    };

    CheckboxDropdown.prototype.onCheckBox = function() {
        this.updateStatus();
    };

    CheckboxDropdown.prototype.updateStatus = function() {
        var checked = this.$el.find(':checked');

        this.areAllChecked = false;
        this.$checkAll.html('Check All');

        if (checked.length <= 0) {
            this.$label.html('Select Options');
        } else if (checked.length === 1) {
            this.$label.html(checked.parent('label').text());
        } else if (checked.length === this.$inputs.length) {
            this.$label.html('All Selected');
            this.areAllChecked = true;
            this.$checkAll.html('Uncheck All');
        } else {
            this.$label.html(checked.length + ' Selected');
        }
    };

    CheckboxDropdown.prototype.onCheckAll = function(checkAll) {
        if (!this.areAllChecked || checkAll) {
            this.areAllChecked = true;
            this.$checkAll.html('Uncheck All');
            this.$inputs.prop('checked', true);
        } else {
            this.areAllChecked = false;
            this.$checkAll.html('Check All');
            this.$inputs.prop('checked', false);
        }

        this.updateStatus();
    };

    CheckboxDropdown.prototype.toggleOpen = function(forceOpen) {
        var _this = this;

        if (!this.isOpen || forceOpen) {
            this.isOpen = true;
            this.$el.addClass('on');
            $(document).on('click', function(e) {
                if (!$(e.target).closest('[data-control]').length) {
                    _this.toggleOpen();
                }
            });
        } else {
            this.isOpen = false;
            this.$el.removeClass('on');
            $(document).off('click');
        }
    };

    var checkboxesDropdowns = document.querySelectorAll('[data-control="checkbox-dropdown"]');
    for (var i = 0, length = checkboxesDropdowns.length; i < length; i++) {
        new CheckboxDropdown(checkboxesDropdowns[i]);
    }
})(jQuery);
$(document).ready(function() {
	appPanel.init();
});
