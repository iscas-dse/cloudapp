package cn.ac.iscas.cloudeploy.v2.model.service.application;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;

import cn.ac.iscas.cloudeploy.v2.controllers.rest.ChartGraph;
import cn.ac.iscas.cloudeploy.v2.controllers.rest.ChartGraph.ChartNode;
import cn.ac.iscas.cloudeploy.v2.dataview.ContainerAttributeView;
import cn.ac.iscas.cloudeploy.v2.dataview.ContainerParamView;
import cn.ac.iscas.cloudeploy.v2.dataview.ContainerTemplateView;
import cn.ac.iscas.cloudeploy.v2.dataview.ContainerView;
import cn.ac.iscas.cloudeploy.v2.dataview.RelationView;
import cn.ac.iscas.cloudeploy.v2.dataview.ApplicationView.DetailedItem;
import cn.ac.iscas.cloudeploy.v2.exception.InternalServerErrorException;
import cn.ac.iscas.cloudeploy.v2.model.dao.ApplicationDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.ChartDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.ContainerAttributeDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.ContainerDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.ContainerInstanceDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.ContainerParamDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.RelationDAO;
import cn.ac.iscas.cloudeploy.v2.model.dao.component.ComponentDAO;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Chart;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Container;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerAttribute;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Relation;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.TemplateParam;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application.Status;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerInstance.Operation;
import cn.ac.iscas.cloudeploy.v2.model.service.topology.STModelService;
import cn.ac.iscas.cloudeploy.v2.model.service.user.UserService;
import cn.ac.iscas.cloudeploy.v2.packet.Packet.PacketStrategy;
import cn.ac.iscas.cloudeploy.v2.util.ConstantsUtil;
import cn.ac.iscas.cloudeploy.v2.util.K8sUtil;
import io.fabric8.kubernetes.api.model.NodeSelector;
//Container与Application是与Web界面对应的概念
//STM模型。
//ContainerInstance对应运行时的概念。
@Service
public class TopologyOrchestrater implements ApplicationService{
	private Logger logger = LoggerFactory.getLogger(TopologyOrchestrater.class);
	@Autowired
	private ChartDAO chartDAO;
	@Autowired
	private ApplicationDAO appDAO;
	@Autowired
	private ComponentDAO componentDao;
	@Autowired
	private ContainerDAO containerDAO;

	@Autowired
	private ContainerParamDAO cParamDAO;
	
	
	@Autowired
	private ContainerAttributeDAO cAttrDAO;
	@Autowired
	private RelationDAO relationDAO;
	@Autowired
	private ContainerInstanceDAO cInstanceDAO;

	@Autowired
	private STModelService stmService;

	@Autowired
	private UserService userService;
	/**
	 * 获取所有应用信息
	 */
	@Override
	public List<Application> getAllApplications() {
		return (List<Application>) appDAO.findAll();
	}
	
	@Override
	public Application createApplication(DetailedItem view) {
		logger.info("create application for " + view.name);
		Application app = new Application(view.name);
		try {
			Optional<String> md5 = stmService.transViewToSTM(view);
			if(md5.isPresent()){
				//app.setDefinedFileKey(md5.get());
			}
		} catch (IOException e) {
			logger.error("create stm model for view failed",e);
			throw new InternalServerErrorException("create stm model for view failed",e);
		}
		storeGraph(view, app);
		appDAO.save(app);
	//	createDeploymentPlan(app.getId(), PacketStrategy.DOCKER);
		return app;
	}
	public synchronized Application saveApplication(Application app) {
		appDAO.save(app);
		if(app.getStatus().equals("MODIFIED")){
			return app;
		}
		for(Container container:app.getContainers()){
			logger.info("save container "+container.getIdentifier()+":"+container.getService()+":"+container.getDeployment());
			//container.setChart(chartDAO.findOne(Long.parseLong(componentId.componentId)));
			containerDAO.save(container);
		}
		for(Relation relation:app.getRelations()){
			logger.info("save relation "+relation.getFrom().getService()+"-->"+relation.getTo().getService());
			relationDAO.save(relation);
		}
		
		return app;
	}
	/**
	 * store the edge and node as well as their positions of the Graph
	 * @param view
	 * @param app
	 */
	private void storeGraph(DetailedItem view, Application app) {
		Map<String, Container> nodeToContainer = new HashMap<String, Container>();
		for (ContainerView.DetailedItem cView : view.containers) {
			Container container = addContainerToApp(cView, app);
			nodeToContainer.put(cView.nodeId, container);
		}
		for (RelationView.Item rView : view.relations) {
			Relation relation = new Relation(nodeToContainer.get(rView.from),
					nodeToContainer.get(rView.to), app);
			relationDAO.save(relation);
		}
	}
	
	private Container addContainerToApp(
			ContainerView.DetailedItem cView,
			Application app) {
		//TODO 修改component
		Container container = new Container(cView.name, cView.port,
				cView.maxCount,
				cView.initCount, app, componentDao.findOne(cView.componentId),
				cView.xPos, cView.yPos, cView.nodeId);
		containerDAO.save(container);
		for (ContainerParamView.Item cParamView : cView.params) {
			ContainerParam param = new ContainerParam(cParamView.key,
					cParamView.value, container);
			cParamDAO.save(param);
		}
	
		for(ContainerAttributeView.Item cAttrView : cView.attributes){
			ContainerAttribute attr = new ContainerAttribute(cAttrView.attrKey, cAttrView.attrValue, container);
			cAttrDAO.save(attr);
		}
		return container;
	}

	


	
	@Override
	public  Application getApplication(Long applicationId) {
		Application app = appDAO.findOne(applicationId);
		for(Container container :app.getContainers()){
			getInstanceOfContainer(container);
		}
		return app;
	}



	@Override
	public boolean deleteApp(Long applicationId) {
		Application app = appDAO.findOne(applicationId);
		if(app==null) return true;
		for(Relation relation: app.getRelations()){
			if(relation.getId()!=null)
				relationDAO.delete(relation.getId());
		}
		for(Container container: app.getContainers()){
			if(container!=null){
				if(container.getIdentifier()!=null){
					//System.err.println("service:"+current.getService());
					///System.err.println("deployment:"+current.getDeployment());
					logger.info("删除容器:"+container.getIdentifier());
					K8sUtil.deleteDeploymentAndServcie(container.getIdentifier());
					K8sUtil.deleteDeploymentAndServcie(container.getService());
				}
				if(container.getId()!=null) containerDAO.delete(container.getId());
			}
		}
		appDAO.delete(app.getId());
		return true;
	}

	private void removeContainer(Container cur) {
		if(cur.getStatus().equals(Container.Status.DEPLOYED)){
			for(ContainerInstance instance: cur.getInstances()){
				//runtimeService.removeInstance(instance);
			}
		}
		cParamDAO.delete(cur.getParams());
		//cTemDAO.delete(cur.getTemplates());
		//cAttrDAO.delete(cur.getAttributes());
		containerDAO.delete(cur);
	}

	@Override
	public Application modifyApplication(Long applicationId, DetailedItem view) {
		logger.info("modify application");
		Application app = appDAO.findOne(applicationId);
		Map<Long, Container> idToContainers = new HashMap<>();
		Map<Long, Relation> idToRelations = new HashMap<>();
		Map<String, Relation> fromAndToRelations = new HashMap<>();
		for (Container container : app.getContainers()) {
			idToContainers.put(container.getId(), container);
		}
		for (Relation relation : app.getRelations()) {
			idToRelations.put(relation.getId(), relation);
			fromAndToRelations.put(relation.getFrom().getIdentifier()
					+ relation.getTo().getIdentifier(), relation);
		}
		return null;
	}
	
	public void getInstanceOfContainer(Container container){
		//runtimeService.instancesOfContainer(container);
		if(container.getInstances().size() > 0) container.changeStatusToDeployed();
	}
	@Override
	public Application deployChart(Long chartId) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void transGraph(ChartGraph graph) {
		// TODO Auto-generated method stub
		List<ChartNode> nodes = graph.nodes;
		ChartNode node=null;
		for(int i=0;i<nodes.size();++i){
			node=nodes.get(i);
			node.chart=chartDAO.findOne(Long.parseLong(node.componentId));
			logger.info("当前Chart:"+node.chart.getDescription());
		}
	}
	@Override
	public Map<Long, Chart> getCharts() {
		// TODO Auto-generated method stub
		Map<Long, Chart> chartsMap=new HashMap<>();
		for(Chart chart: chartDAO.findAll()){
			chartsMap.put(chart.getId(), chart);
		}
		return  chartsMap;
	}
	@Override
	public boolean stopApp(Long applicationId) {
		Application app = appDAO.findOne(applicationId);
//		for(Relation relation: app.getRelations()){
//			relationDAO.delete(relation.getId());
//		}
	
		for(Container current: app.getContainers()){
			if(current!=null){
				if(current.getIdentifier()!=null){
					//System.err.println("service:"+current.getService());
					///System.err.println("deployment:"+current.getDeployment());
					logger.info("identifier:"+current.getIdentifier());
					//K8sUtil.deleteDeploymentAndServcie(current.getIdentifier());
					if(null!=(current.getService())){
						K8sUtil.stop(current.getService());
					}
				}
			
			}
			//removeContainer(current);
		}
	
		return true;
	}
	@Override
	public boolean startApp(Long applicationId) {
		Application app = appDAO.findOne(applicationId);
//		for(Relation relation: app.getRelations()){
//			relationDAO.delete(relation.getId());
//		}
	
		for(Container current: app.getContainers()){
			if(current!=null){
				if(current.getIdentifier()!=null){
					//System.err.println("service:"+current.getService());
					///System.err.println("deployment:"+current.getDeployment());
					logger.info("identifier:"+current.getIdentifier());
					//K8sUtil.deleteDeploymentAndServcie(current.getIdentifier());
					if(null!=current.getService()) K8sUtil.start(current.getService());
				}
			
			}
			//removeContainer(current);
		}
	
		return true;
	}

	@Override
	public Application deployApplication(Long applicationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean doOperationOnContainerInstance(Long containerInatanceId, Operation operation) {
		// TODO Auto-generated method stub
		return false;
	}
}
