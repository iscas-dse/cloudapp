var appInstance = null;
var connectorPaintStyle = {
	lineWidth : 2,
	strokeStyle : "#61B7CF",
	joinstyle : "round",
	outlineColor : "white",
	outlineWidth : 2
};
var connectorHoverStyle = {
	lineWidth : 4,
	strokeStyle : "#216477",
	outlineWidth : 2,
	outlineColor : "white"
};
var endpointHoverStyle = {
	fillStyle : "#216477",
	strokeStyle : "#216477"
};
var sourceEndpoint = {
	endpoint : "Dot",
	paintStyle : {
		strokeStyle : "#7AB02C",
		fillStyle : "transparent",
		radius : 0,
		lineWidth : 2,
		radius : 6
	},
	isSource : true,
	connector : [ "StateMachine", {
		stub : [ 4, 6 ],
		gap : 5,
		cornerRadius : 1,
		alwaysRespectStubs : true
	} ],
	connectorStyle : connectorPaintStyle,
	hoverPaintStyle : endpointHoverStyle,
	connectorHoverStyle : connectorHoverStyle,
	dragOptions : {},
	overlays : [ [ "Arrow", {
		location : [ 0.5, 1.5 ],
		label : "",
		cssClass : "endpointSourceLabel"
	} ] ],
	maxConnections : -1
};
var targetEndpoint = {
	endpoint : "Dot",
	paintStyle : {
		fillStyle : "#7AB02C",
		radius : 6
	},
	hoverPaintStyle : endpointHoverStyle,
	maxConnections : -1,
	dropOptions : {
		hoverClass : "hover",
		activeClass : "active"
	},
	isTarget : true,
	overlays : [ [ "Label", {
		location : [ 0.5, -0.5 ],
		label : "",
		cssClass : "endpointTargetLabel"
	} ] ]
};

jsPlumb.ready(function() {
	var instance = jsPlumb.getInstance({
		DragOptions : {
			cursor : 'pointer',
			zIndex : 2000
		},
		ConnectionOverlays : [ [ "Arrow", {
			location : 1
		} ] ],
		Container : "graph-panel"
	});
	instance.bind("connection", function(connInfo, originalEvent) {
		console.log(connInfo.connection.sourceId+"->"+connInfo.connection.targetId);
		var src=appPanel.cachedNodes.get(connInfo.connection.sourceId);
		var tar=appPanel.cachedNodes.get(connInfo.connection.targetId);
		console.log("增加依赖项"+tar.serviceName);
		src.dependency.put(connInfo.connection.targetId,tar);
		var conn = connInfo.connection;
		conn.bind("dblclick", function(connection, originalEvent) {
			if (confirm("delete edge?")) {
				var src=appPanel.cachedNodes.get(connInfo.connection.sourceId);
				var tar=appPanel.cachedNodes.get(connInfo.connection.targetId);
				console.log("删除依赖"+tar.serviceName);
				src.dependency.removeByKey(connInfo.connection.targetId);
				jsPlumb.detach(conn);
			}
		});
	});

	appInstance = instance;
});