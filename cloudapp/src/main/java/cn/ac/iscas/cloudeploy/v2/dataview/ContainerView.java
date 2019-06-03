package cn.ac.iscas.cloudeploy.v2.dataview;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.aspectj.util.FileUtil;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Chart;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Container;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Container.Status;
import cn.ac.iscas.cloudeploy.v2.packet.util.JsonUtils;
import cn.ac.iscas.cloudeploy.v2.util.ConstantsUtil;
import cn.ac.iscas.cloudeploy.v2.util.K8sUtil;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import lombok.extern.log4j.Log4j;
@Log4j
public class ContainerView {
	private static final Logger logger = Logger.getLogger(ContainerView.class); 
	public static class Item {
		public String name;
		public int port;
		public Integer xPos;
		public Integer yPos;
		public Long masterId;
		public int maxCount;
		public int initCount;
		public Long componentId;
		public String nodeId;
		public Long id;
		public Status status;
	}

	public static class DetailedItem extends Item {
		public List<ContainerParamView.Item> params;
		public List<ContainerTemplateView.Item> templates;
		public List<ContainerAttributeView.Item> attributes;
		public List<ContainerInstanceView.Item> instances;
		public Chart chart;
		protected String identifier;
		public Deployment deployment;
		public Service service;
	}

	private static Function<Container, Item> ITEM_VIEW_TRANSFORMER = new Function<Container, Item>() {
		@Override
		public Item apply(Container input) {
			if (input == null)
				return null;
			Item view = new Item();
			view.id = input.getId();
			view.name = input.getName();
			view.port = input.getPort();
			view.xPos = input.getxPos();
			view.yPos = input.getyPos();
			view.status = input.getStatus();
			view.maxCount = input.getMaxCount();
			view.initCount = input.getInitCount();
			view.componentId = input.getComponent() == null ? null : input
					.getComponent().getId();
			view.nodeId = input.getIdentifier();
			return view;
		}
	};

	public static Item viewOf(Container input) {
		return input == null ? null : ITEM_VIEW_TRANSFORMER.apply(input);
	}

	public static List<Item> viewListOf(List<Container> input) {
		return input == null ? ImmutableList.<Item> of() : Lists.transform(
				input, ITEM_VIEW_TRANSFORMER);
	}

	private static  Function<Container, DetailedItem> DTAILED_ITEM_VIEW_TRANSFORMER = new Function<Container, DetailedItem>() {
		@Override
		public synchronized DetailedItem apply(Container input) {
			if (input == null)
				return null;
			DetailedItem view = new DetailedItem();
			view.id = input.getId();
			view.name = input.getName();
			view.port = input.getPort();
			view.xPos = input.getxPos()==null?200:input.getxPos();
			view.yPos = input.getyPos()==null?200:input.getyPos();
			view.maxCount = input.getMaxCount();
			view.initCount = input.getInitCount();
			view.componentId = input.getComponent() == null ? null : input
					.getComponent().getId();
			view.params = ContainerParamView.viewListOf(input.getParams());
			//view.templates = ContainerTemplateView.viewListOf(input.getTemplates());
			//view.attributes = ContainerAttributeView.viewListOf(input.getAttributes());
			view.instances = ContainerInstanceView.viewListOf(input
					.getInstances());
			view.nodeId = input.getIdentifier();
			view.status = input.getStatus();
			view.chart=input.getChart();
			if(input.getVersion()!=null&&input.getVersion().equals(ConstantsUtil.CONTAINER_VERSION)){
				if(input.getStatus().equals(Status.CREATED)){
					String containerFolderPath=ConstantsUtil.WEB_INF_PATH + File.separator 
							+"chartnodes"+File.separator 
							+ File.separator+ input.getIdentifier();
					try {
						File tmpDpFile=new File(containerFolderPath+File.separator+"deployment.json");
						File tmpSvcFile=new File(containerFolderPath+File.separator+"service.json");
						if(tmpDpFile.exists()&&tmpSvcFile.exists()){
							String deploymenString=FileUtils.readFileToString(tmpDpFile);
							String serviceString=FileUtils.readFileToString(tmpSvcFile);
							view.deployment=JsonUtils.convertToObject(deploymenString, Deployment.class);
							//logger.info("k8s deployment 状态:"+view.deployment==null);
							view.service=JsonUtils.convertToObject(serviceString, Service.class);
						}else{
							log.info(view.name+"ds配置文件不存在");
						}
						
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
				}else{
					if(input==null){
						logger.error("input is null!");
					}
					view.deployment=K8sUtil.getDeploymentDetaile(input.getDeployment());
					//logger.info("k8s deployment 状态:"+view.deployment==null);
					view.service=K8sUtil.getServiceDetaile(input.getService());
				}
				
			}
			view.identifier=input.getIdentifier();
			//logger.info("iden: "+view.identifier);
			return view;
		}
	};

	public static DetailedItem detailedViewOf(Container input) {
		return input == null ? null : DTAILED_ITEM_VIEW_TRANSFORMER
				.apply(input);
	}

	public static List<DetailedItem> detailedViewListOf(List<Container> input,Application app) {
		return input == null ? ImmutableList.<DetailedItem> of() : Lists
				.transform(input, DTAILED_ITEM_VIEW_TRANSFORMER);
	}
}
