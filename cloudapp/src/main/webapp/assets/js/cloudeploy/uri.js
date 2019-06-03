var rootURI = function() {
	return "/cloudapp";
};

var dURIs = {
	deployChartURI : rootURI() + "/v2/charts/deploy",
	appInstanceURI : rootURI() + "/v2/applications/containers/instances",
	componentURI : rootURI() + "/v2/components",
	componentTypeURI : rootURI() + "/v2/components/types",
	domainURI : rootURI() + "/v2/domains",
	hostURI : rootURI() + "/v2/hosts",
	customFilesURI : rootURI() + "/v2/resources/files/custom",
	filesURI : rootURI() + "/v2/files",
	templateURI : rootURI() + "/v2/templates",
	orche:{
		scores: rootURI() + "/v2/orche",
	},
	apps:{
		appList : rootURI() + "/v2/applications",
		deployApp: rootURI() + "/v2/applications",
		deleteApp: rootURI() + "/v2/applications/delete/",
		startApp: rootURI() + "/v2/applications/start/",
		stopApp: rootURI() + "/v2/applications/stop/",
		clear:rootURI() + "/v2/applications/clear/",
	},
	chartsURI:{
		listCharts:rootURI() + "/v2/charts/listcharts",
		listDetailedCharts:rootURI() + "/v2/charts/listDetailedcharts",
		getChart:rootURI() + "/v2/charts/get/",
		deleteChart:rootURI() + "/v2/charts/delete",
		deployChart:rootURI() + "/v2/charts/deploy",
		uploadChart:rootURI() + "/v2/charts/upload",
	},
	viewsURI : {
		appList : rootURI() + "/v2/views/applications/list",
		charts: rootURI() + "/v2/views/charts",
		appOrchestration : rootURI() + "/v2/views/applications/panel",
		domains : rootURI() + "/v2/views/domains",
		cluster : rootURI() + "/v2/views/service/cluster",
		consul:rootURI() + "/v2/views/service/consul",
		docker : rootURI() + "/v2/views/service/docker",
		weave : rootURI() + "/v2/views/service/weave",
		temptManager : rootURI() + "/v2/views/templates",
		configManager: rootURI() + "/v2/views/config"
	},
	swfs : rootURI() + "/swf",
};