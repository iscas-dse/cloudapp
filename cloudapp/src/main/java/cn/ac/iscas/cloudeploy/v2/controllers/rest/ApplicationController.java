package cn.ac.iscas.cloudeploy.v2.controllers.rest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.aspectj.apache.bcel.classfile.Field;
import org.aspectj.util.FileUtil;
import org.aspectj.weaver.ast.Var;
import org.hibernate.procedure.internal.Util.ResultClassesResolutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sun.imageio.spi.RAFImageInputStreamSpi;
import com.sun.org.apache.bcel.internal.generic.RETURN;

import cn.ac.iscas.cloudeploy.v2.controller.BasicController.DResponseBuilder;
import cn.ac.iscas.cloudeploy.v2.controllers.rest.ChartGraph.ChartNode;
import cn.ac.iscas.cloudeploy.v2.dataview.ApplicationView;
import cn.ac.iscas.cloudeploy.v2.dataview.ContainerView;
import cn.ac.iscas.cloudeploy.v2.dataview.ApplicationView.DetailedItem;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Chart;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application.Status;
import cn.ac.iscas.cloudeploy.v2.model.service.application.ApplicationService;
import cn.ac.iscas.cloudeploy.v2.packet.util.JsonUtils;
import cn.ac.iscas.cloudeploy.v2.util.Constants;
import cn.ac.iscas.cloudeploy.v2.util.ConstantsUtil;
import cn.ac.iscas.cloudeploy.v2.util.K8sUtil;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import lombok.extern.log4j.Log4j;

/**
 * Rest接口服务器 返回Rest数据，包括创建、删除、展示、修改、部署应用
 * 
 */
@Controller
@RequestMapping(value = "v2/applications")
@Log4j
public class ApplicationController {
	private static final Logger logger = Logger.getLogger(ApplicationController.class);
	private static final ArrayList<ApplicationView.DetailedItem> Apps_Cache = new ArrayList<>();
	private static final ConcurrentHashMap<String, ApplicationView.DetailedItem> APP_DetailedItem_Cache = new ConcurrentHashMap<>();
	//http://localhost:8080/cloudapp/v2/applications/clear

	private static synchronized void Clear() {
		APP_DetailedItem_Cache.clear();
		Apps_Cache.clear();
	}

	@Autowired
	private ApplicationService appService;

	/**
	 * 获取应用列表,JSON数据返回
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ArrayList<ApplicationView.DetailedItem> getApplications() {
		ArrayList<ApplicationView.DetailedItem> apps = Apps_Cache;
		System.out.println(apps.size());
		synchronized (apps) {
			if (!apps.isEmpty()) {
				return apps;
			} else {
				apps.clear();
				for (ApplicationView.DetailedItem app : ApplicationView
						.detailedViewListOf(appService.getAllApplications())) {
					apps.add(app);					
				}
				return apps;
			}
		}
	}

	/**
	 * 部署应用
	 * 
	 * @param graph
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> deployApplication(@RequestBody ChartGraph graph) {
		ArrayList<ApplicationView.DetailedItem> apps = Apps_Cache;

		Map<String, String> res = new HashMap<>();
		if (graph == null || graph.nodes.size() == 0) {
			res.put("IsSuc", "false");
			res.put("reason", "请选择服务进行编排后再部署！");
			return res;
		}
		for (ApplicationView.DetailedItem app : apps) {
			if (StringUtils.equalsIgnoreCase(app.name, graph.name)) {
				res.put("IsSuc", "false");
				res.put("reason", "应用名需要全局唯一，命名重复！");
				return res;
			}
		}

		for (int i = 0; i < graph.nodes.size(); i++) {
			if (K8sUtil.containes(graph.nodes.get(i).serviceName)) {
				res.put("IsSuc", "false");
				res.put("reason", "应用相关服务已经部署，如果镜像版本更高，系统执行镜像更新策略，更新成功！");
				return res;
			}
		}

		Map<Long, Chart> chartsMap = new HashMap<Long, Chart>(appService.getCharts());
		Application application = null;
		if (Constants.DEPLOY.equalsIgnoreCase(graph.State)) {
			try {
				application = K8sUtil.deployGraph(chartsMap, graph);
				// logger.info(application.getContainers().size());
				appService.saveApplication(application);
				logger.info("部署新应用成功:" + application.getId());
				res.put("IsSuc", "true");
				res.put("reason", "部署新应用成功！");
			} catch (Exception e) {
				e.printStackTrace();
				for (ChartNode node : graph.getNodes()) {
					K8sUtil.deleteDeploymentAndServcie(node.serviceName);
				}
				res.put("IsSuc", "true");
				res.put("reason", "部署失败,已经全部回滚！原因:" + e.getMessage());
				return res;
			}
		} else {
			application = K8sUtil.saveGraph(chartsMap, graph);
			appService.saveApplication(application);
			logger.info("保存新的编排方案:" + application.getId());
			res.put("IsSuc", "true");
			res.put("reason", "保存编排方案成功！");
		}
		Apps_Cache.add(ApplicationView.detailedViewOf(application));
		synchronized (apps) {
			apps.clear();
		}
		return res;
	}

	/**
	 * 部署应用
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = { "/{appId}" }, method = RequestMethod.POST)
	@ResponseBody
	// @Transactional
	public ApplicationView.DetailedItem deployApplication(@PathVariable("appId") Long id) {
		Clear();
		return ApplicationView.detailedViewOf(appService.deployApplication(id));
	}

	/**
	 * 获取应用详细信息
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = { "/{appId}" }, method = RequestMethod.GET)
	@ResponseBody
	// @Transactional
	public  ApplicationView.DetailedItem  getApplication(@PathVariable("appId") Long id) {
		ApplicationView.DetailedItem detailedItem = null;
		log.info("获取应用"+id);
		String key = String.valueOf(id);
		detailedItem = APP_DetailedItem_Cache.get(key);
		if (detailedItem != null) {
			return detailedItem;
		} else {
			Application app = appService.getApplication(id);
			detailedItem = ApplicationView.detailedViewOf(app);
			APP_DetailedItem_Cache.put(key, detailedItem);
			return detailedItem;
		}
	}

	/**
	 * 修改应用
	 * 
	 * @param appId
	 * @param appView
	 * @return
	 */
	@RequestMapping(value = { "/{appId}" }, method = RequestMethod.PUT)
	@ResponseBody
	// @Transactional
	public ApplicationView.DetailedItem modifyApplication(@PathVariable("appId") Long appId,
			@RequestBody ApplicationView.DetailedItem appView) {
		Clear();
		return ApplicationView.detailedViewOf(appService.modifyApplication(appId, appView));
	}

	/**
	 * 移除应用
	 * 
	 * @param appId
	 * @return
	 */
	// @Transactional
	@RequestMapping(value = { "/delete/{appId}" }, method = RequestMethod.DELETE)
	@ResponseBody
	public Map<String, Object> deleteApp(@PathVariable("appId") final Long appId) {
		boolean success = appService.deleteApp(appId);
		Map<String, Object> res = new HashMap<>();
		if (success) {
			logger.info("删除成功！");
			ArrayList<ApplicationView.DetailedItem> apps = Apps_Cache;
			Iterator<ApplicationView.DetailedItem> it = apps.iterator();
			while (it.hasNext()) {
				ApplicationView.DetailedItem item = it.next();
				logger.info(item.id + "缓存" + appId);
				if (item.id.longValue() == appId.longValue()) {
					logger.info("移除缓存");
					it.remove();
				}
			}
			APP_DetailedItem_Cache.remove(String.valueOf(appId));
			res.put("result", "success");
		} else {
			res.put("result", "fail");
			logger.info("删除失败！");
		}
		return res;
	}

	@RequestMapping(value = { "/update" }, method = RequestMethod.GET)
	@ResponseBody
	// http://localhost:8080/cloudapp/v2/applications/update/
	// http://localhost:8080/cloudapp/v2/applications/update?service=ts-consign-mongo
	public String updateService(@RequestParam("service") final String name) {
		// boolean success = appService.deleteApp(appId);
		Map<String, Object> res = new HashMap<>();
		System.err.println("服务名：" + name);
		K8sUtil.updateDeploymentAndServcie(name);
		return name;
	}

	// http://localhost:8080/cloudapp/v2/applications/isdeploy?app=a
	// http://39.104.105.27:8181/cloudapp/v2/applications/isdeploy?app=a
	@RequestMapping(value = { "/isdeploy" }, method = RequestMethod.GET)
	public void isDeploy(@RequestParam("app") final String name, HttpServletResponse response) throws IOException {
		if (StringUtils.isEmpty(name)) {
			ResponseUtil.NotFound(response);
			return;
		}
		List<Application> applications = appService.getAllApplications();
		for (Application application : applications) {
			if (application.getName().equals(name)) {
				ResponseUtil.Sucess(name + "状态" + application.getStatus(), response);
				return;
			}
		}
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		ResponseUtil.NotFound(response);
	}

	// http://localhost:8080/cloudapp/v2/applications/reDeploy?app=a
	// http://39.104.105.27:8181/cloudapp/v2/applications/reDeploy?app=a
	@RequestMapping(value = { "/reDeploy" }, method = RequestMethod.GET)
	public void reDeploy(@RequestParam("app") final String name, HttpServletResponse response) {
		if (StringUtils.isEmpty(name)) {
			ResponseUtil.NotFound(response);
			return;
		}
		List<Application> applications = appService.getAllApplications();
		for (Application application : applications) {
			if (application.getName().equals(name)) {
				if (application.getStatus().equals(Status.DEPLOYED)) {
					ResponseUtil.Sucess(name + "已经部署了!", response);
					return;
				} else {
					String path = ConstantsUtil.WEB_INF_PATH + File.separator + ConstantsUtil.APP_PATH + File.separator
							+ name + File.separator + "graph.json";
					String graphJson;
					try {
						graphJson = FileUtils.readFileToString(new File(path));
						logger.info("图数据:\n" + graphJson);
						ChartGraph graph = JsonUtils.convertToObject(graphJson, ChartGraph.class);
						deleteApp(application.getId());
						graph.setState(Constants.DEPLOY);
						deployApplication(graph);
						ResponseUtil.Sucess(name + "重新部署了!", response);
						return;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						ResponseUtil.NotFound(response);
					}
				}
			}
		}
		ResponseUtil.NotFound(response);
		return;
	}

	/**
	 * 启动应用
	 * 
	 * @param appId
	 * @return
	 */
	// @Transactional
	@RequestMapping(value = { "/start/{appId}" }, method = RequestMethod.GET)
	@ResponseBody
	public Object startAPP(@PathVariable("appId") Long appId) {
		boolean success = appService.startApp(appId);
		return DResponseBuilder.instance().add("result", success ? "success" : "fail").build();
	}

	/**
	 * 停止应用
	 * 
	 * @param appId
	 * @return
	 */
	@RequestMapping(value = { "/stop/{appId}" }, method = RequestMethod.GET)
	@ResponseBody
	public Object stopAPP(@PathVariable("appId") Long appId) {
		boolean success = appService.stopApp(appId);
		return DResponseBuilder.instance().add("result", success ? "success" : "fail").build();
	}

	/**
	 * 停止应用
	 * 
	 * @param appId
	 * @return
	 */
	@RequestMapping(value = { "/clear" }, method = RequestMethod.GET)
	@ResponseBody
	public void clear() {
		Clear();
	}
}
