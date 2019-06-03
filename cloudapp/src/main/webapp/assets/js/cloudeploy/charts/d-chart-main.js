//Author:Lee
//Chart,代表组件，数据填充代码
//------------------------------------------------------------------------
var chartCache = new Map(); // 数据缓存chartCache
var typeCharts=new Map();//string,type
var dMain = {
	curChartName : null,
	init : function() {
		// 列举Charts列表
		dMain.listCharts();
	},
	listCharts : function() {
		ajaxGetJsonAuthc(dURIs.chartsURI.listCharts, null,
				dMain.initChartsListUIAndEvents);
	},
	cacheCharts:function(charts){
		for ( var i in charts) {
			var chart = charts[i];
			if(!typeCharts.containsKey(chart.type)){
				console.log(chart.type);
				typeCharts.put(chart.type,new Array());
			}
			typeCharts.get(chart.type).push(chart);
		}
	},
	initChartsListUIAndEvents : function(charts) {
		// 缓存数据
		dMain.cacheCharts(charts);
		var components = $("#d-charts-list ul");
		//绘制分类面板
		components.html(dHtml.genTypeListHtml());
		//填充组件的详细信息
		dHtml.genChartsTableHtml(charts);
	}

};
var dHtml = {
	genTypeListHtml : function() {
		var html = '';
		var type;
		for (var type of typeCharts.keys()) {
			//console.log(typeCharts.);
			html += '<li><a name="'
				+type
				+'" style="margin: 3px;" class="app-item list-group-item" href="javascript:void(0)" '
				+'onclick="dCharts.getTypeCharts(this,\''
				+ type + '\')" >' 
				+ type 
				+ '</a></li>';
		}
		type="组件分类";
		html='<li><a style="margin: 3px;color:white; background-color: black;"   class="app-item list-group-item" >' + type + '</a></li>'+html;
		return html;
	},
	/**
	 * 生成Chart组件列表的网页UI
	 * 
	 * @param hosts
	 * @returns {String}
	 */
	genChartListHtml : function(charts) {
		var html = '';
		for ( var i in charts) {
			var chart = charts[i];
			chartCache.put(chart.id, chart);
			html += '<li><a name="'+chart.name+'" class="app-item list-group-item" href="javascript:void(0)" onclick="dCharts.getChartDetail(this,'
					+ chart.id + ')">' + chart.name + '</a></li>';
		}
		return html;
	},
	/**
	 * 生成Chart组件细节表格UI
	 * 
	 * @param apps
	 */
	genChartsTableHtml : function(charts) {
		var html = '';
		for ( var i in charts) {
			var chart = charts[i];
			console.log("genChartsTableHtml"+chart);
			html += '<tr data-id="' + chart.id + '">' + '<td >' + chart.name
					+ '</td> ' + '<td >' + chart.version + '</td> ' + '<td >'
					+ chart.description + '</td> ' + '<td>'
					+ getFormatDateFromLong(chart.createTime) + '</td> ' + '<td>'
					+ getFormatDateFromLong(chart.updateTime) + '</td> </tr>';
		}
		$("#chartsTable tbody").html(html);
	},
	// 操作面板
	genOperHtml : function(chart) {
		html = '<a class="btn btn-default"href="javascript:dCharts.uploadChart()"> <span class="glyphicon glyphicon-plus" ></span> 上传组件</a> '
				+ ''
				+ ' <a class="btn btn-default"href="javascript:dCharts.deployCom('
				+ chart.id
				+ ')"> <span class="glyphicon glyphicon-forward"></span> 一键部署</a> '
				+ ' <a class="btn btn-default"href="javascript:dCharts.deleteCom('
				+ chart.id
				+ ')"> <span class="glyphicon glyphicon-remove"></span> 删除组件</a> ';
		return html;
	},
	scanFileClick : function(s) {
		var v = $(s).attr('key');
		// var filekey = $(temp).attr('key');
		// var filename = $(temp).attr('name');
		// var sources = dURIs.filesURI+"/content/"+filekey;
		$('#scanFileModal').modal('show');
		// $("#scanFileName").html(filename);
		console.log("scan: "+v);
		$("#scanFileBody").html(chartCache.get(v));
		// ajaxGetJsonAuthc(sources, null, dHtml.showContext);
	},
	showContext : function(data) {
		$("#scanFileBody").html(data);
	}
};
//------------------------------------------------------------------------
//组件代码
var dCharts = {
	currentFileId : -1,
	init : function() {
		$('#upload-btn').click(function() {
			$('#file-upload').uploadify('upload');
		});
	},

	initUploadify : function(uploadCallBack) {
		var uploadifyOptions = {
			'auto' : false,
			'multi' : false,
			'uploadLimit' : 1,
			'buttonText' : 'choose file',
			'swf' : dURIs.swfs + '/uploadify.swf',
			'uploader' : dURIs.filesURI,
			'fileObjName' : 'file',
			'formData' : {
				"d-token" : getToken()
			},
			'onUploadSuccess' : function(file, data, response) {
				data = $.parseJSON(data);
				if (verifyParam(uploadCallBack)) {
					uploadCallBack(data.fileKey, $("input[name='fileName']")
							.val());
				}
			},
			'onSelect' : function(file) {
				if ($("input[name='fileName']").attr("disabled") == "disabled") {
					return;
				}
				$("input[name='fileName']").val(file.name);
			}
		};
		$('#file-upload').uploadify(uploadifyOptions);
	},
	//删除一个Chart后刷新界面
	refreshChart: function(charts){
		// 填充组件列表
		var components = $("#d-charts-list ul");
		components.html(dHtml.genChartListHtml(charts));
		// 在表格处填充组件的详细信息
		dHtml.genChartsTableHtml(charts);
		var oper = $("#d-chart-oper");
		oper.html('<a class="btn btn-default" href="javascript:dCharts.uploadChart()">'+' <span class="glyphicon glyphicon-plus"></span> 上传组件 </a>');
	},
	uploadChart : function() {
		$('#addCustomFileModal').modal('show');
		$("#fileName").val("");
		$("#fileName").removeAttr("disabled");
		dCharts.initUploadify(dCharts.addFile);
	},
	deployCom : function(chartId) {
		var saveBtn = $("#save-app-btn");
		saveBtn.unbind("click");
		saveBtn.click(function() {
			var deployItemInfo = {
				id : chartId,
				name : $("#appName").val()
			};
			console.log($.toJSON(deployItemInfo));
			ajaxPostJsonAuthcWithJsonContent(dURIs.chartsURI.deployChart, deployItemInfo,
					dCharts.deploySuc, dCharts.deployError, true);
		});
		$('#saveModal').modal('show');
	},
	deploySuc : function(data) {
		console.log(data);
		showSuccess("Opration completed");
		$("#appName").val("");
		$('#saveModal').modal('hide');
	},
	deployError : function(data) {
		defaultErrorFunc();
		$('#saveModal').modal('hide');
	},
	deleteCom : function(id) {
		var r = confirm("Delete this Component:"+dMain.curChartName+"?")
		if (r == true) {
			ajaxDeleteJsonAuthc(dURIs.chartsURI.deleteChart + "/" + id, null,
					 dCharts.refreshChart,null, false);
		}
		console.log(id);
	},

	addFile : function(fileKey, fileName) {
		ajaxPostJsonAuthc(dURIs.customFilesURI, {
			name : fileName,
			fileKey : fileKey
		}, dCharts.getFileList, null, false);
	},

	replaceFileClick : function(fileId) {
		$('#addCustomFileModal').modal('show');
		var fileName = $('#chartsTable tbody tr[data-id="' + fileId + '"]').find(
				"td")[1].innerHTML;
		$("#fileName").val(fileName);
		$("#fileName").attr("disabled", "disabled");
		dCharts.currentFileId = fileId;
		dCharts.initUploadify(dCharts.replaceFile);
	},

	replaceFile : function(fileKey, file) {
		ajaxPutJsonAuthc(dURIs.customFilesURI + "/" + dCharts.currentFileId
				+ "?fileKey=" + fileKey, null, null, null, false);
		dCharts.currentFileId = -1;
	},
	getTypeCharts : function(typeBtn, type) {
		$(".app-item").removeClass("active");
		$(typeBtn).addClass("active");
		console.log("当前type " + type);
		//dMain.curChartName=chartCache.get(chartId).name;
		//console.log(chartCache);
		//console.log("当前chart name:"+dMain.curChartName);
		$("#d-typeCharts-List").removeClass('hide');
		var chtml = '';
		var charts=typeCharts.get(type);
		for ( var i in charts) {
			var chart = charts[i];
			console.log(chart);
			chartCache.put(chart.id, chart);
			chtml += '<li><a name="'+chart.name+'" class="app-item list-group-item" href="javascript:void(0)" onclick="dCharts.getChartDetail(this,'
					+ chart.id + ')">' + chart.name + '</a></li>';
		}
		$("#d-typeCharts-List  ul").html(chtml);
		$("#d-tempt-list").removeClass('col-sm-10').addClass('col-sm-8');
		
		// 填充组件详细
		//$("#d-chart-version").html('版本:' + chart.version);
		//$("#d-chart-des").html(chart.description);
	
		//$("#d-chart-create").html(getFormatDateFromLong(chart.createdAt));
		//$("#d-chart-update").html(getFormatDateFromLong(chart.updatedAt));
		// 填充操作
		/**
		var oper = $("#d-chart-oper");
		oper.html(dHtml.genOperHtml(chart));
		// 填充文件列表
		var html = '';
		for ( var key in chart.path2File) {
			var nkey = chart.name + key;
			chartCache.put(chart.name + key, chart.path2File[key]);
			html += '<tr data-id="' + chart.id + '"><td class="hide"></td><td>'
					+ key + '</td>';
			html += '<td>'
				+ '<a key="'
				+ nkey
				+ '" style="margin-right:15px;" onclick="dHtml.scanFileClick(this)"><i class="fa fa-eye"></i> view</a>'
				
			html += '</td></tr>';
		}
		// 填充文件路径
		$("#chartsTable thead").html('<tr> <th>path</th> <th>action</th> </tr>');
		$("#chartsTable tbody").html(html);
		**/
	},
	getChartDetail : function(chartLi, chartId) {
		$(".app-item").removeClass("active");
		$(chartLi).addClass("active");
		console.log("当前chart id " + chartId);
		dMain.curChartName=chartCache.get(chartId).name;
		console.log(chartCache);
		console.log("当前chart name:"+dMain.curChartName);
		ajaxGetJsonAuthc(dURIs.chartsURI.getChart + "/" + chartId, null,
				dCharts.showChartDetail);
	},
	getFileList : function() {
		ajaxGetJsonAuthc(dURIs.customFilesURI, null, dCharts.refreshFileList,
				null);
	},
	// 在显示组件详细
	showChartDetail : function(chart) {
		//$("#d-container-list").removeClass('hide');
		$("#d-tempt-list").removeClass('col-sm-10').addClass('col-sm-8');
		// 填充组件详细
		/*$("#d-chart-version").html('版本:' + chart.version);
		$("#d-chart-des").html(chart.description);
		console.log(chart);
		$("#d-chart-create").html(getFormatDateFromLong(chart.createdAt));
		$("#d-chart-update").html(getFormatDateFromLong(chart.updatedAt));*/
		// 填充操作
		var oper = $("#d-chart-oper");
		oper.html(dHtml.genOperHtml(chart));
		// 填充文件列表
		var html = '';
		for ( var key in chart.path2File) {
			var nkey = chart.name + key;
			chartCache.put(chart.name + key, chart.path2File[key]);
			html += '<tr data-id="' + chart.id + '"><td class="hide"></td><td>'
					+ key + '</td>';
//			html += '<td>'
//					
//					+ '<a style="margin-right:15px;" href="javascript:dCustomFiles.replaceFileClick('
//					+ chart.id
//					+ ')"><i class="fa fa-upload"></i> edit</a>'
//					
//					+ '<a key="'
//					+ nkey
//					+ '" style="margin-right:15px;" onclick="dHtml.scanFileClick(this)"><i class="fa fa-eye"></i> view</a>'
//					+ '<a style="margin-right:15px;" href="javascript:dCustomFiles.removeFile('
//					+ chart.id + ')"><i class="fa fa-remove"></i> delete</a>';
			html += '<td>'
				+ '<a key="'
				+ nkey
				+ '" style="margin-right:15px;" onclick="dHtml.scanFileClick(this)"><i class="fa fa-eye"></i> view</a>'
				
			html += '</td></tr>';
		}
		// 填充文件路径
		$("#chartsTable thead").html('<tr> <th>path</th> <th>action</th> </tr>');
		$("#chartsTable tbody").html(html);
		

	},
	showTemplates : function(container, index) {
		$(".container-item").removeClass("active");
		$(container).addClass("active");
		dHtml.genTemplateHtml(currApp.containers[index].templates);
	}

};
$(document).ready(function() {
	dMain.init();
});

$(document).ready(function() {
	//dCharts.init();
});
//------------------------------------------------------------------------