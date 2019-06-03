package cn.ac.iscas.cloudeploy.v2.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ImportResource;

import com.fasterxml.jackson.core.JsonProcessingException;

import cn.ac.iscas.cloudeploy.v2.controllers.rest.ChartGraph;
import cn.ac.iscas.cloudeploy.v2.controllers.rest.ChartGraph.ChartNode;
import cn.ac.iscas.cloudeploy.v2.controllers.rest.ChartGraph.ChartRelation;
import cn.ac.iscas.cloudeploy.v2.controllers.rest.ChartGraph.Dependency;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Chart;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Container;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Relation;
import cn.ac.iscas.cloudeploy.v2.packet.util.JsonUtils;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.NodeList;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.fabric8.kubernetes.api.model.ServiceAccountBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.extensions.DeploymentList;
import io.fabric8.kubernetes.api.model.extensions.DeploymentSpec;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.internal.SerializationUtils;

public final class K8sUtil {
	// http://39.104.112.98:8080/
	private static final Config config = new ConfigBuilder().withMasterUrl(ConstantsUtil.getConstant().K8S_API_SERVER)
			.build();
	private static final Logger logger = LoggerFactory.getLogger(K8sUtil.class);
	private static final KubernetesClient client = new DefaultKubernetesClient(config);
	private static ConcurrentHashMap<String, Service> servicesCache = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, Deployment> deploymentsCache = new ConcurrentHashMap<>();

	public final static void main(String[] args) throws InterruptedException {
		// list();
		// createNginx();
		// deleteDeploymentByLabel();
		// createMysql();
		// deployCom("mysql");
		// getServiceDetaile("mysqltest");
		// deleteDeploymentAndServcie("t-180109-112640");
		// getDeploymentDetaile("t-180109-161826");
		// getServiceDetaile("t-180109-161826");
		// createSvc();
		dumpAllDeploymentService("default");
	}

	public static void dumpAllDeploymentService(String namespace) {
		DeploymentList deploymentList = client.extensions().deployments().inNamespace(namespace).list();
		ServiceList serviceList = client.services().inNamespace(namespace).list();
		System.err.println("Deployments  count:" + deploymentList.getItems().size());
		System.err.println("Services  count:" + serviceList.getItems().size());
		String parentPath = "D:/template/";
		String compName = null;
		File comFile = null;
		Deployment deploymentTemplate = new Deployment();
		deploymentTemplate.setApiVersion("extensions/v1beta1");
		deploymentTemplate.setKind("Deployment");
		ObjectMeta objectMeta = new ObjectMeta();
		deploymentTemplate.setMetadata(objectMeta);
		DeploymentSpec deploymentSpec = new DeploymentSpec();
		deploymentSpec.setReplicas(1);
		PodTemplateSpec podTemplateSpec = new PodTemplateSpec();
		ObjectMeta podMeta = new ObjectMeta();
		podTemplateSpec.setMetadata(podMeta);
		PodSpec podSpec = new PodSpec();
		podTemplateSpec.setSpec(podSpec);
		List<io.fabric8.kubernetes.api.model.Container> containers = new ArrayList<>();
		io.fabric8.kubernetes.api.model.Container container = new io.fabric8.kubernetes.api.model.Container();
		containers.add(container);
		// container.setName(name);
		container.setImagePullPolicy("Always");
		List<ContainerPort> ports = new ArrayList<>();
		ContainerPort containerPort = new ContainerPort();
		ports.add(containerPort);
		// containerPort.setContainerPort(containerPort);
		container.setPorts(ports);
		podSpec.setContainers(containers);
		deploymentSpec.setTemplate(podTemplateSpec);
		deploymentTemplate.setSpec(deploymentSpec);

		for (Deployment deployment : deploymentList.getItems()) {
			compName = deployment.getMetadata().getName();
			// parentPath+""
			// deployment.getSpec().getTemplate().getSpec().getHostAliases()
			comFile = new File(parentPath + compName + "/deployment/deployment.yaml");
			if (comFile.exists())
				comFile.delete();
			comFile.getParentFile().mkdirs();
			deploymentTemplate.setApiVersion(deployment.getApiVersion());
			deploymentTemplate.setKind(deployment.getKind());
			objectMeta.setName(deployment.getMetadata().getName());
			podMeta.setLabels(deployment.getSpec().getTemplate().getMetadata().getLabels());
			io.fabric8.kubernetes.api.model.Container tmp = deployment.getSpec().getTemplate().getSpec().getContainers()
					.get(0);
			container.setName(tmp.getName());
			container.setImage(tmp.getImage());
			containerPort.setContainerPort(tmp.getPorts().get(0).getContainerPort());
			try {
				FileUtils.write(comFile, SerializationUtils.dumpAsYaml(deploymentTemplate));
				List<String> lines = FileUtils.readLines(comFile);
				for (int i = 0; i < 5; i++) {
					lines.remove(lines.size() - 1);
				}
				FileUtils.writeLines(comFile, lines);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.out.println(deployment);
		}

		System.err.println("\n-------------------------------------------------------------");
		Service serviceTemplate = new Service();
		ObjectMeta serviceMata = new ObjectMeta();
		serviceTemplate.setMetadata(serviceMata);
		ServiceSpec serviceSpec = new ServiceSpec();
		serviceTemplate.setSpec(serviceSpec);
		List<ServicePort> servicePorts = new ArrayList<>();
		serviceSpec.setPorts(servicePorts);
		servicePorts.add(new ServicePort());
		/// Map<String, String> selector=new HashMap<String, String>();
		// serviceSpec.setSelector(selector);
		for (Service service : serviceList.getItems()) {
			compName = service.getMetadata().getName();
			serviceTemplate.setApiVersion(service.getApiVersion());
			serviceTemplate.setKind(service.getKind());
			serviceMata.setName(compName);
			servicePorts.get(0).setPort(service.getSpec().getPorts().get(0).getPort());
			serviceSpec.setSelector(service.getSpec().getSelector());
			comFile = new File(parentPath + compName + "/service/service.yaml");
			comFile.getParentFile().mkdirs();
			// System.out.println(service);
			try {
				FileUtils.write(comFile, SerializationUtils.dumpAsYaml(serviceTemplate));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public final synchronized static void deployCom(String comName) {
		try {
			ServiceAccount fabric8 = new ServiceAccountBuilder().withNewMetadata().withName("fabric8").endMetadata()
					.build();
			client.serviceAccounts().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).createOrReplace(fabric8);
			File deploymentFiles = new File(
					ConstantsUtil.COM_PATH + "/" + comName + "/" + ConstantsUtil.DEPLOYMENT + "/");
			for (File file : deploymentFiles.listFiles()) {
				info(file.getName());
				// 30000-32767
				client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE)
						.load(new FileInputStream(file)).createOrReplace();
			}
			File serviceFiles = new File(ConstantsUtil.COM_PATH + "/" + comName + "/" + ConstantsUtil.SERVICE + "/");
			for (File file : serviceFiles.listFiles()) {
				info(file.getName());
				client.services().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).load(new FileInputStream(file))
						.createOrReplace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			client.close();
		}
	}

	public static ServiceList getAllServices(String namespace) {
		// DeploymentList
		// deploymentList=client.extensions().deployments().inNamespace(namespace).list();
		return client.services().inNamespace(namespace).list();
	}

	public final synchronized static InputStream replaceName(File file, String oldName, String newName) {
		FileReader in = null;
		try {
			in = new FileReader(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader bufIn = new BufferedReader(in);
		// 替换
		String line = null;
		StringBuffer sb = new StringBuffer();
		try {
			while ((line = bufIn.readLine()) != null) {
				// 替换每行中, 符合条件的字符串
				line = line.replaceAll(oldName, newName);
				sb.append(line);
				sb.append(System.getProperty("line.separator"));
			}
			bufIn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info(sb.toString());
		// 关闭 输入流
		return new ByteArrayInputStream(sb.toString().getBytes());

	}

	/**
	 * 一键部署组件
	 * 
	 * @param comName
	 */
	public final synchronized static Application deployComByName(String comName, String appName, Chart chart) {
		Application application = new Application(appName);
		try {
			ServiceAccount fabric8 = new ServiceAccountBuilder().withNewMetadata().withName("fabric8").endMetadata()
					.build();
			client.serviceAccounts().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).createOrReplace(fabric8);
			// 应用由容器组成，容器的ID如下，容器分为两部分，一部分是Deployment，一部分是Service
			String identifier = "t-" + TimeUtil.getCurTime();
			// 部署deployment
			File deploymentFiles = new File(
					ConstantsUtil.COM_PATH + "/" + comName + "/" + ConstantsUtil.DEPLOYMENT + "/");
			for (File file : deploymentFiles.listFiles()) {
				info(file.getName());
				// 30000-32767
				client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE)
						.load(replaceName(file, "com-" + comName, identifier)).createOrReplace();
			}
			// 部署services
			File serviceFiles = new File(ConstantsUtil.COM_PATH + "/" + comName + "/" + ConstantsUtil.SERVICE + "/");
			for (File file : serviceFiles.listFiles()) {
				info(file.getName());
				client.services().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE)
						.load(replaceName(file, "com-" + comName, identifier)).createOrReplace();
			}
			// 保存应用,容器关系
			Container container = new Container();
			container.setService(identifier);
			container.setIdentifier(identifier);
			container.setDeployment(identifier);
			container.setChart(chart);
			container.setName(chart.getName());
			container.changeStatusToDeployed();
			container.setApplication(application);
			application.addContainer(container);
			application.changeStatusToDeployed();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			client.close();
		}
		return application;
	}

	/**
	 * 一键部署组件
	 * 
	 * @param chartsMap
	 * 
	 * @param comName
	 */
	// public final static Container deployNode(ChartNode node){
	// 部署deployment
	// 30000-32767
	// client.extensions().deployments().inNamespace(ConstantsUtil.DEFAULT_NAMESPACE).
	// 部署services

	// client.services().inNamespace(ConstantsUtil.DEFAULT_NAMESPACE).load(replaceName(file,
	// "com-"+comName, identifier)).createOrReplace();
	//
	// //保存应用,容器关系
	// Container container=new Container();
	// container.setServiceName(identifier);
	// container.setIdentifier(identifier);
	// container.setDeploymentName(identifier);
	// container.setChart(chart);
	// container.changeStatusToDeployed();
	// container.setApplication(application);
	// return container;
	// }

	public final synchronized static Application deployGraph(Map<Long, Chart> chartsMap, ChartGraph graph) {
		Application application = new Application(graph.name);

		// 标识是否已经部署
		HashSet<String> deployedSet = new HashSet<>();
		HashMap<String, Container> deployedMap = new HashMap<>();
		// 部署队列
		List<ChartNode> deployes = new ArrayList<>();
		// 获取依赖队列
		Queue<ChartNode> queue = new LinkedList<>();
		List<Dependency> dependencies = null;
		ChartNode cur = null;
		Chart chart = null;
		deployes.clear();// 清空部署队列
		try {
			for (ChartNode chartNode : graph.getNodes()) {

				// logger.info("获取队首节点--->"+chartNode.nodeId);
				cur = chartNode;
				// 如果节点已经加入部署队列或者当前节点为空,则不需要加
				if (cur != null && !deployedSet.contains(cur.nodeId)) {
					queue.add(cur);
					while (!queue.isEmpty()) {
						// 计划弹出多少节点
						int size = queue.size();
						while (size != 0) {
							cur = queue.poll();// 获取队列第一个元素,如果没有部署,添加部署队列
							if (!deployedSet.contains(cur.nodeId)) {
								// logger.info("增加一个部署节点--->"+cur.nodeId);
								deployes.add(cur); // 添加一个部署元素
								deployedSet.add(cur.nodeId);

								dependencies = cur.dependency.elements;
								if (dependencies != null && !dependencies.isEmpty()) {
									for (Dependency dependency : dependencies) {
										queue.add(dependency.value);
										logger.info(cur.nodeId + "--->" + dependency.value.nodeId);
										// logger.info("增加queue:"+dependency.value.nodeId);
									}
								}
							}
							size--;
						}
					}
					logger.info("----------------开始部署------------------");
					for (int i = deployes.size() - 1; i >= 0; i--) {
						ChartNode deploy = deployes.get(i);
						if (!deployedMap.containsKey(deploy.nodeId)) {
							logger.info("部署-----" + deploy.nodeId);
							logger.info("部署状态----" + graph.State);
							Deployment deployment = null;
							Service service = null;
							logger.info("部署的服务名：" + deploy.serviceName);

							try {
								deployment = client.extensions().deployments()
										.inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE)
										.load(new File(ConstantsUtil.COM_PATH + File.separator + deploy.serviceName
												+ File.separator + ConstantsUtil.DEPLOYMENT + File.separator
												+ "deployment.yaml"))
										.get();
								service = client.services().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE)
										.load(new File(ConstantsUtil.COM_PATH + File.separator + deploy.serviceName
												+ File.separator + ConstantsUtil.SERVICE + File.separator
												+ "service.yaml"))
										.get();
								// deploy.getDeployment(deployment);
								// deploy.getService(service);
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							if (deployment == null) {
								deployment = deploy.getDeployment();
							}
							if (service == null) {
								service = deploy.getService();
							}

							deployment.getMetadata().setName(deploy.serviceName);
							service.getMetadata().setName(deploy.serviceName);

							service.getMetadata().setNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE);
							deployment.getMetadata().setNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE);
//							if(service.getMetadata().getName().equalsIgnoreCase("ts-ui-dashboard")){
//								service.getSpec().setPorts(ports);
//							}
							client.services().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).createOrReplace(service);
							client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE)
									.createOrReplace(deployment);

							Container container = new Container();
							logger.info("deploy graph " + deploy.serviceName);
							container.setService(deploy.serviceName);// 部署在k8s的服务名
							container.setDeployment(deploy.serviceName);// 部署在K8s的Deployment名
							container.setName(deploy.serviceName); // 组件的类型
							container.setIdentifier(deploy.nodeId);// 组件的名
							container.setXPos((int) Double.parseDouble(deploy.xPos));
							container.setYPos((int) Double.parseDouble(deploy.yPos));
							chart = chartsMap.get(Long.parseLong(deploy.componentId));
							logger.info("添加到部署中" + chart.getDescription());
							container.setChart(chart);
							if (Constants.DEPLOY.equals(graph.State)) {
								container.changeStatusToDeployed();
							} else {
								container.changeStatusToCreated();
							}
							// 部署过的
							deployedMap.put(deploy.nodeId, container);
							container.setApplication(application);
							application.addContainer(container);
						}
					}
					logger.info("----------------部署完成------------------");
				}
			}
			logger.info("que 最终状态:" + queue.size());
		} catch (Exception e) {
			for (ChartNode node : graph.getNodes()) {
				K8sUtil.deleteDeploymentAndServcie(node.serviceName);
			}
			e.printStackTrace();
			return null;
		} finally {
			client.close();
		}
		for (ChartRelation relation : graph.getRelations()) {
			Relation containerRelation = new Relation(deployedMap.get(relation.from), deployedMap.get(relation.to),
					application);
			application.addRelation(containerRelation);
		}
		application.changeStatusToDeployed();
		if (Constants.DEPLOY.equals(graph.State)) {

		} else {
			application.changeStatusToModified();
		}
		return application;
	}

	public final synchronized static Application saveGraph(Map<Long, Chart> chartsMap, ChartGraph graph) {

		Application application = new Application(graph.name);
		String path = ConstantsUtil.WEB_INF_PATH + File.separator + ConstantsUtil.APP_PATH + File.separator
				+ graph.getName();
		File appFolder = new File(path);
		appFolder.mkdirs();
		File graphJsonFile = new File(path + File.separator + "graph.json");
		if (graphJsonFile.exists())
			graphJsonFile.delete();
		try {
			String graphJson = JsonUtils.convertToJson(graph);
			logger.info("图数据:\n" + graphJson);
			FileUtils.write(graphJsonFile, graphJson);
			logger.info("文件路径:" + graphJsonFile.getAbsolutePath());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 标识是否已经部署
		HashSet<String> deployedSet = new HashSet<>();
		HashMap<String, Container> deployedMap = new HashMap<>();
		// 部署队列
		List<ChartNode> deployes = new ArrayList<>();
		// 获取依赖队列
		Queue<ChartNode> queue = new LinkedList<>();
		List<Dependency> dependencies = null;
		ChartNode cur = null;
		Chart chart = null;
		deployes.clear();// 清空部署队列
		try {
			for (ChartNode chartNode : graph.getNodes()) {

				// logger.info("获取队首节点--->"+chartNode.nodeId);
				cur = chartNode;
				// 如果节点已经加入部署队列或者当前节点为空,则不需要加
				if (cur != null && !deployedSet.contains(cur.nodeId)) {
					queue.add(cur);
					while (!queue.isEmpty()) {
						// 计划弹出多少节点
						int size = queue.size();
						while (size != 0) {
							cur = queue.poll();// 获取队列第一个元素,如果没有部署,添加部署队列
							if (!deployedSet.contains(cur.nodeId)) {
								// logger.info("增加一个部署节点--->"+cur.nodeId);
								deployes.add(cur); // 添加一个部署元素
								deployedSet.add(cur.nodeId);

								dependencies = cur.dependency.elements;
								if (dependencies != null && !dependencies.isEmpty()) {
									for (Dependency dependency : dependencies) {
										queue.add(dependency.value);
										logger.info(cur.nodeId + "--->" + dependency.value.nodeId);
										// logger.info("增加queue:"+dependency.value.nodeId);
									}
								}
							}
							size--;
						}
					}
					logger.info("----------------开始部署------------------");
					for (int i = deployes.size() - 1; i >= 0; i--) {
						ChartNode deploy = deployes.get(i);
						if (!deployedMap.containsKey(deploy.nodeId)) {
							logger.info("部署-----" + deploy.nodeId);
							logger.info("部署状态----" + graph.State);
							logger.info("部署的服务名：" + deploy.serviceName);
							Container container = new Container();
							logger.info("deploy graph " + deploy.serviceName);
							container.setService(deploy.serviceName);// 部署在k8s的服务名
							container.setDeployment(deploy.serviceName);// 部署在K8s的Deployment名
							container.setName(deploy.serviceName); // 组件的类型
							container.setIdentifier(deploy.nodeId);// 组件的名
							container.setXPos((int) Double.parseDouble(deploy.xPos));
							container.setYPos((int) Double.parseDouble(deploy.yPos));
							chart = chartsMap.get(Long.parseLong(deploy.componentId));
							logger.info("添加到部署中" + chart.getDescription());
							container.setChart(chart);
							container.changeStatusToCreated();
							//
							Deployment deployment=deploy.getDeployment();
							Service service=deploy.getService();
							
							String containerFolderPath=ConstantsUtil.WEB_INF_PATH + File.separator 
									+"chartnodes"+File.separator 
									+ File.separator+ container.getIdentifier();
							File containerFolder=new File(containerFolderPath);
							if (containerFolder.exists())
								containerFolder.delete();
							File deploymentJsonFile = new File(containerFolder+File.separator + "deployment.json");
							File serviceJsonFile = new File(containerFolder + File.separator + "service.json");
							try {
								String deploymentJson = JsonUtils.convertToJson(deployment);
								String serviceJson = JsonUtils.convertToJson(service);
								logger.info("deployment数据:\n" + deploymentJson);
								logger.info("service数据:\n" + serviceJson);
								FileUtils.write(deploymentJsonFile, deploymentJson);
								FileUtils.write(serviceJsonFile, serviceJson);
								logger.info("文件路径:" + deploymentJsonFile.getAbsolutePath());

							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (JsonProcessingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// 部署过的
							deployedMap.put(deploy.nodeId, container);
							container.setApplication(application);
							application.addContainer(container);
						}
					}
					logger.info("----------------部署完成------------------");
				}
			}
			logger.info("que 最终状态:" + queue.size());
		} catch (Exception e) {
			for (ChartNode node : graph.getNodes()) {
				K8sUtil.deleteDeploymentAndServcie(node.serviceName);
			}
			e.printStackTrace();
			return null;
		} finally {
			client.close();
		}
		for (ChartRelation relation : graph.getRelations()) {
			Relation containerRelation = new Relation(deployedMap.get(relation.from), deployedMap.get(relation.to),
					application);
			application.addRelation(containerRelation);
		}
		application.changeStatusToDeployed();
		if (Constants.DEPLOY.equals(graph.State)) {

		} else {
			application.changeStatusToModified();
		}
		return application;
	}

	public final synchronized static void updateDeploymentAndServcie(String name) {
		if (name == null)
			return;
		try {
			// ServiceAccount fabric8 = new
			// ServiceAccountBuilder().withNewMetadata().withName("fabric8").endMetadata()
			// .build();
			// client.serviceAccounts().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).createOrReplace(fabric8);
			if (client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).withName(name)
					.get() != null) {
				client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).withName(name)
						.delete();
				deploymentsCache.remove(name);
			}
			if (client.services().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).withName(name).get() != null) {
				client.services().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).withName(name).delete();
				servicesCache.remove(name);
			}
			Deployment deployment = client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE)
					.load(new File(ConstantsUtil.COM_PATH + File.separator + name + File.separator
							+ ConstantsUtil.DEPLOYMENT + File.separator + "deployment.yaml"))
					.get();
			Service service = client.services().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE)
					.load(new File(ConstantsUtil.COM_PATH + File.separator + name + File.separator
							+ ConstantsUtil.SERVICE + File.separator + "service.yaml"))
					.get();

			client.services().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).createOrReplace(service);
			client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE)
					.createOrReplace(deployment);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public final static synchronized Service getServiceDetaile(String serviceName) {
		try {
			// ServiceAccount fabric8 = new
			// ServiceAccountBuilder().withNewMetadata().withName("fabric8").endMetadata()
			// .build();
			// client.serviceAccounts().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).createOrReplace(fabric8);
			Service service = null;
			service = servicesCache.get(serviceName);
			if (service == null) {

				service = client.services().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).withName(serviceName)
						.get();
				if (service != null)
					servicesCache.put(serviceName, service);
			}
			// logger.info(SerializationUtils.dumpAsYaml(service));
			return service;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			client.close();
		}
		return null;
	}

	public final static synchronized Deployment getDeploymentDetaile(String deploymentName) {
		try {
			// ServiceAccount fabric8 = new
			// ServiceAccountBuilder().withNewMetadata().endMetadata().build();
			// client.serviceAccounts().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).createOrReplace(fabric8);
			//
			Deployment deployment = deploymentsCache.get(deploymentName);

			if (deployment == null) {
				deployment = client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE)
						.withName(deploymentName).get();
				if (deployment != null)
					deploymentsCache.put(deploymentName, deployment);
			}
			// logger.info(SerializationUtils.dumpAsYaml(deployment));
			return deployment;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
		return null;
	}

	public final static synchronized boolean containes(String deploymentName) {
		try {
			Deployment deployment = client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE)
					.withName(deploymentName).get();
			Service service = client.services().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE)
					.withName(deploymentName).get();
			return deployment != null || service != null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.close();
		}
		return false;
	}

	public final static void createNginx() {
		try {
			// Create a namespace for all our stuff
			ServiceAccount fabric8 = new ServiceAccountBuilder().withNewMetadata().withName("fabric8").endMetadata()
					.build();
			client.serviceAccounts().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).createOrReplace(fabric8);
			Deployment deployment = new DeploymentBuilder().withNewMetadata().withName("nginx").endMetadata()
					.withNewSpec().withReplicas(1).withNewTemplate().withNewMetadata().addToLabels("app", "nginx")
					.endMetadata().withNewSpec().addNewContainer().withName("nginx").withImage("nginx").addNewPort()
					.withContainerPort(80).endPort().endContainer().endSpec().endTemplate().endSpec().build();
			deployment = client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE)
					.create(deployment);
			info("Created deployment", deployment);
			logger.info("Scaling up:" + deployment.getMetadata().getName());
			client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).withName("nginx")
					.scale(2, true);
			info("Created replica sets:", client.extensions().replicaSets()
					.inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).list().getItems());
			info("Done.");
		} finally {
			// client.namespaces().withName(ConstantsUtil.DEFAULT_NAMESPACE).delete();
			// client.close();
		}
	}

	public final static void createMysql() {
		try {
			// Create a namespace for all our stuff
			ServiceAccount fabric8 = new ServiceAccountBuilder().withNewMetadata().withName("fabric8").endMetadata()
					.build();
			client.serviceAccounts().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).createOrReplace(fabric8);
			Deployment deployment = new DeploymentBuilder().withNewMetadata().withName("mysql").endMetadata()
					.withNewSpec().withReplicas(1).withNewTemplate().withNewMetadata().addToLabels("app", "mysql")
					.endMetadata().withNewSpec().addNewContainer().withName("mysql").withImage("mysql:5.7.14")
					.withEnv(new EnvVar("MYSQL_ROOT_PASSWORD", "test", null)).addNewPort().withContainerPort(3306)
					.endPort().endContainer().endSpec().endTemplate().endSpec().build();
			deployment = client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE)
					.create(deployment);
			info("Created deployment", deployment);
			logger.info("Scaling up:" + deployment.getMetadata().getName());
			// client.extensions().deployments().inNamespace(ConstantsUtil.DEFAULT_NAMESPACE).withName("nginx").scale(2,
			// true);
			info("Created replica sets:", client.extensions().replicaSets()
					.inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).list().getItems());
			info("Done.");
		} finally {
			// client.namespaces().withName(ConstantsUtil.DEFAULT_NAMESPACE).delete();
			// client.close();
		}
	}

	public final static void createSvc() {
		try {
			// Create a namespace for all our stuff
			// ServiceAccount fabric8 = new
			// ServiceAccountBuilder().withNewMetadata().withName("fabric8").endMetadata()
			// .build();
			// client.serviceAccounts().inNamespace("default").createOrReplace(fabric8);
			Random random = new Random();
			Service service = new ServiceBuilder().withNewMetadata().withName("myweb").endMetadata().withNewSpec()
					.withType("NodePort").addNewPort().withPort(8080).withNodePort(30004).endPort().endSpec().build();

			client.services().inNamespace("default").createOrReplace(service);
			info("Done.");
		} finally {
			// client.namespaces().withName(ConstantsUtil.DEFAULT_NAMESPACE).delete();
			// client.close();
		}
	}

	public final synchronized static void deleteDeploymentAndServcie(String name) {
		if (name == null)
			return;
		try {
			// ServiceAccount fabric8 = new
			// ServiceAccountBuilder().withNewMetadata().withName("fabric8").endMetadata()
			// .build();
			// client.serviceAccounts().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).createOrReplace(fabric8);
			if (client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).withName(name)
					.get() != null) {
				client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).withName(name)
						.delete();
			}
			if (client.services().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).withName(name).get() != null) {
				client.services().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).withName(name).delete();
			}
			servicesCache.remove(name);
			deploymentsCache.remove(name);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public final synchronized static void stop(String name) {

		try {
			/*
			 * ServiceAccount fabric8 = new
			 * ServiceAccountBuilder().withNewMetadata().withName("fabric8").
			 * endMetadata() .build();
			 */
			// client.serviceAccounts().inNamespace(ConstantsUtil.DEFAULT_NAMESPACE).createOrReplace(fabric8);
			System.err.println(client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE)
					.withName(name).isReady());

			client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).withName(name).scale(0);
			System.err.println(client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE)
					.withName(name).isReady());

			// client.services().inNamespace(ConstantsUtil.DEFAULT_NAMESPACE).withName(name);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public final synchronized static void start(String name) {

		try {
			/*
			 * ServiceAccount fabric8 = new
			 * ServiceAccountBuilder().withNewMetadata().withName("fabric8").
			 * endMetadata() .build();
			 */
			// client.serviceAccounts().inNamespace(ConstantsUtil.DEFAULT_NAMESPACE).createOrReplace(fabric8);
			System.err.println(client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE)
					.withName(name).isReady());

			client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE).withName(name).scale(1);
			System.err.println(client.extensions().deployments().inNamespace(ConstantsUtil.K8S_DEFAULT_NAMESPACE)
					.withName(name).isReady());
			// client.services().inNamespace(ConstantsUtil.DEFAULT_NAMESPACE).withName(name);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public final synchronized static void getNameSpace() {
		List<Namespace> nameSpaceList = client.namespaces().list().getItems();
		for (Namespace name : nameSpaceList) {
			System.out.println(name);
		}
	}

	public final synchronized static void createNameSpace(String namespace) {
		Namespace ns = new NamespaceBuilder().withNewMetadata().withName(namespace).addToLabels("this", "rocks")
				.endMetadata().build();
		info("Created namespace", client.namespaces().createOrReplace(ns));
	}

	/**
	 * 创建pod
	 * 
	 * @throws Exception
	 *
	 ***/
	public final synchronized static Pod createPod(Pod pod) throws Exception {
		try {
			return client.pods().create(pod);
		} catch (KubernetesClientException e) {
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * 创建service
	 * 
	 * @throws Exception
	 **/
	public final synchronized static Service createService(Service service) throws Exception {
		if (service == null) {
			return null;
		}
		try {
			return client.services().create(service);
		} catch (KubernetesClientException e) {
			throw new Exception(e.getMessage());
		}
	}

	/** 创建RC **/
	public final synchronized static ReplicationController createReplicationController(ReplicationController rc)
			throws Exception {
		if (rc == null) {
			return null;
		}
		try {
			return client.replicationControllers().create(rc);
		} catch (KubernetesClientException e) {
			throw new Exception(e.getMessage());
		}

	}

	/** 创建deployment **/
	public final synchronized static Deployment createDeployment(Deployment deployment) throws Exception {
		if (deployment == null) {
			return null;
		}
		try {
			return client.extensions().deployments().create(deployment);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	/** 按条件列出所有node **/
	public final synchronized static NodeList listNode(Map<String, String> labelSelector) throws Exception {
		try {
			return client.nodes().withLabels(labelSelector).list();
		} catch (KubernetesClientException e) {
			throw new Exception(e.getMessage());
		}
	}

	public final static void list() {
		// 获取命名空间列表
		NamespaceList namespaceList = client.namespaces().list();
		for (Namespace namespace : namespaceList.getItems()) {
			info(namespace.getMetadata().getName());
		}
		logger.info("--------------------------------------------------------------");
		// 获取服务列表
		ServiceList serviceList = client.services().list();
		//
		// for(Service service:serviceList.getItems()){
		// try {
		// info(SerializationUtils.dumpAsYaml(service));
		// } catch (JsonProcessingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

		Service service = new ServiceBuilder().withNewMetadata().withName("helm").endMetadata().build();
		try {
			info(SerializationUtils.dumpAsYaml(service));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void info(String action, Object obj) {
		logger.info("{}: {}", action, obj);
	}

	private static void info(String action) {
		logger.info(action);
	}
}
