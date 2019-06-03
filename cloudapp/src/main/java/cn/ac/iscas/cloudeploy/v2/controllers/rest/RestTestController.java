package cn.ac.iscas.cloudeploy.v2.controllers.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.reflect.ClassPath;

import cn.ac.iscas.cloudeploy.v2.controller.BasicController;
import cn.ac.iscas.cloudeploy.v2.controller.BasicController.DResponseBuilder;
import cn.ac.iscas.cloudeploy.v2.dataview.ApplicationView;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance;
import cn.ac.iscas.cloudeploy.v2.model.service.application.ApplicationService;
import cn.ac.iscas.cloudeploy.v2.model.service.application.TopologyOrchestrater;


@Controller
@RequestMapping(value = "v2/rest")
public class RestTestController {
	/**
	 * 系统的CRUD服务
	 */
	@Autowired
	private TopologyOrchestrater appService;
	/**
	 * 创建应用
	 * 
	 * @param appView
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	//@Transactional
	public ApplicationView.Item createApplication(
			@RequestBody ApplicationView.DetailedItem appView) {
		return ApplicationView.viewOf(appService.createApplication(appView));
	}
	/**
	 * 部署应用
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = { "/{appId}" }, method = RequestMethod.POST)
	@ResponseBody
	//@Transactional
	public ApplicationView.DetailedItem deployApplication(
			@PathVariable("appId") Long id) {
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
	//@Transactional
	public ApplicationView.DetailedItem getApplication(
			@PathVariable("appId") Long id) {
		Application app = appService.getApplication(id);
		return ApplicationView.detailedViewOf(app);
	}
	
	@RequestMapping(value="/apps",method = RequestMethod.GET)
	@ResponseBody
	//@Transactional
	public List<Application> getApplications() {
		return appService.getAllApplications();
	}

	@RequestMapping(value="/appsd",method = RequestMethod.GET)
	@ResponseBody
	//@Transactional
	public List<ApplicationView.DetailedItem> getApplicationsDetail() {
		return ApplicationView.detailedViewListOf(((ApplicationService) appService)
				.getAllApplications());
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
	//@Transactional
	public ApplicationView.DetailedItem modifyApplication(
			@PathVariable("appId") Long appId,
			@RequestBody ApplicationView.DetailedItem appView) {
		return ApplicationView.detailedViewOf(appService.modifyApplication(
				appId, appView));
	}
	/**
	 * 节容器实例上进行操作
	 * 
	 * @param id
	 * @param operation
	 * @return
	 */
	@RequestMapping(value = { "/containers/instances/{instanceId}" }, method = RequestMethod.PUT)
	@ResponseBody
	public Object operationOnContainerInstance(
			@PathVariable("instanceId") Long id,
			@RequestParam("operation") ContainerInstance.Operation operation) {
		boolean success = appService.doOperationOnContainerInstance(id,
				operation);
		return DResponseBuilder.instance()
				.add("result", success ? "success" : "fail").build();
	}

	/**
	 * 获取应用列表 
	 * http://localhost:8080/cloudapp/v2/rest/apps
	 * @return
	 */
	@RequestMapping(value="/read",method=RequestMethod.GET)
	@ResponseBody
	public String read(){
		String path=this.getClass().getClassLoader().getResource("../").getPath();
        StringBuilder sb=new StringBuilder();
		 File file = new File(path+"/components/mysql/Chart.yaml");
		 File f = new File(this.getClass().getResource("/").getPath());
		 System.out.println(f);
	        BufferedReader reader = null;
	        try {
	            reader = new BufferedReader(new FileReader(file));
	            String tmp=null;
	            int line = 1;
	            // 一次读入一行，直到读入null为文件结束
	            while ((tmp = reader.readLine()) != null) {
	                // 显示行号
	            	sb.append(tmp+"<br>");
	                line++;
	            }
	            reader.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	        return sb.toString();
	}

	/**
	 * 移除应用
	 * 
	 * @param appId
	 * @return
	 */
	//@Transactional
	@RequestMapping(value = { "/{appId}" }, method = RequestMethod.DELETE)
	@ResponseBody
	public Object removeApplication(@PathVariable("appId") Long appId) {
		boolean success = appService.deleteApp(appId);
		return DResponseBuilder.instance()
				.add("result", success ? "success" : "fail").build();
	}
}
