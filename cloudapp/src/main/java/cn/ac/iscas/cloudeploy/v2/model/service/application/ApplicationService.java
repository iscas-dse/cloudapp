package cn.ac.iscas.cloudeploy.v2.model.service.application;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;

import cn.ac.iscas.cloudeploy.v2.controllers.rest.ChartGraph;
import cn.ac.iscas.cloudeploy.v2.dataview.ApplicationView;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Chart;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance;

public interface ApplicationService {
	
	public Application saveApplication(Application app);
	/**
	 * 创建应用
	 * 
	 * @param view
	 * @return
	 */
	
	public Application createApplication(ApplicationView.DetailedItem view);

	/**
	 * 获取包括全部应用的列表
	 * 
	 * @return
	 */
	
	public List<Application> getAllApplications();
	/**
	 * 部署应用
	 * 
	 * @param applicationId
	 * @return
	 */
	public Application deployApplication(Long applicationId);
	/**
	 * 部署组件
	 * @param applicationId
	 * @return
	 */
	public Application deployChart(Long chartId);

	/**
	 * 获取应用详细信息
	 * 
	 * @param applicationId
	 * @return
	 */
	public  Application getApplication(Long applicationId);

	/**
	 * 在容器实例上做操作
	 * 
	 * @param containerInatanceId
	 * @param operation
	 * @return
	 */
	public boolean doOperationOnContainerInstance(Long containerInatanceId,
			ContainerInstance.Operation operation);

	/**
	 * 移除应用
	 * 
	 * @param applicationId
	 * @return
	 */
	public boolean deleteApp(Long applicationId);
	public boolean stopApp(Long applicationId);
	public boolean startApp(Long applicationId);

	/**
	 * 修改应用
	 * 
	 * @param applicationId
	 * @param view
	 * @return
	 */
	public Application modifyApplication(Long applicationId,
			ApplicationView.DetailedItem view);
	public void transGraph(ChartGraph graph);
	public Map<Long, Chart> getCharts();
}
