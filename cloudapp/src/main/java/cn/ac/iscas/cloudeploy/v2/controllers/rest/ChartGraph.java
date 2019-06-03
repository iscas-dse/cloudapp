 package cn.ac.iscas.cloudeploy.v2.controllers.rest;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import cn.ac.iscas.cloudeploy.v2.controllers.rest.ChartGraph.ChartNode;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Chart;
import cn.ac.iscas.cloudeploy.v2.util.DateUtils;
import cn.ac.iscas.cloudeploy.v2.util.TimeUtil;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder;
import io.fabric8.kubernetes.client.internal.SerializationUtils;
import lombok.Data;
@Data
public class ChartGraph {
	public String  name;
	public List<ChartNode> nodes;
	public List<ChartRelation> relations;
	public String  State;
	public ChartGraph(){
		
		this.nodes=new ArrayList<ChartGraph.ChartNode>();
		this.relations=new ArrayList<ChartRelation>();
	}
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ChartNode {
		public String componentId;
		public String nodeId;
		public List<Kv> envs;
		public String image;
		public String containerPort;
		public String nodePort;
		public String serviceName;
		public ChartDependency dependency;
		public String nodeStatus;
		public String xPos;
		public String yPos;
		public String chartId;
		public Chart chart;
		public Deployment getDeployment(){
			Set<EnvVar> envVars=new HashSet<>();
			for(Kv item: envs){
				EnvVar envVar=new EnvVar();
				envVar.setName(item.name);
				envVar.setValue(item.value);
				envVars.add(envVar);
			}
			
			Deployment deployment = new DeploymentBuilder()
					.withNewMetadata()
					.withName(this.serviceName)
					.addToLabels("app", this.serviceName)
					.addToLabels("id",""+this.nodeId)
					.endMetadata()
					.withNewSpec()
					.withReplicas(1)
					.withNewTemplate()
					.withNewMetadata()
					.addToLabels("app", this.serviceName)
					.endMetadata()
					.withNewSpec()
					.addNewContainer()
					.withName(this.serviceName)
					.withImage(this.image)
					.addNewPort()
					.withContainerPort(Integer.parseInt(this.containerPort))
					.endPort()
					.addAllToEnv(envVars)
					.endContainer()
					.endSpec()
					.endTemplate()
					.endSpec()
					.build();
			return deployment;
		}
		public Service getService(){
			Service service = new ServiceBuilder()
					.withNewMetadata()
					.withName(this.serviceName)
					.addToLabels("app", this.serviceName)
					.addToLabels("id",""+this.nodeId)
					.addToAnnotations("app", this.serviceName)
					.addToAnnotations("app", this.serviceName)
					.and()
					.withNewSpec()
					.addToSelector("app", this.serviceName)
					.withType("NodePort")
					.addNewPort()
					.withPort(Integer.parseInt(this.containerPort))
					.withNodePort(Integer.parseInt(this.nodePort))
					.endPort()
					.and()
					.build();
			return service;
		}
		public void getDeployment(Deployment deployment) {
//					deployment.s(this.serviceName)
//					.addToLabels("app", this.serviceName)
//					.addToLabels("id",""+this.nodeId)
//					.endMetadata()
//					.withNewSpec()
//					.withReplicas(1)
//					.withNewTemplate()
//					.withNewMetadata()
//					.addToLabels("app", this.serviceName)
//					.endMetadata()
//					.withNewSpec()
//					.addNewContainer()
//					.withName(this.serviceName)
//					.withImage(this.image)
//					.addNewPort()
//					.withContainerPort(Integer.parseInt(this.containerPort))
//					.endPort()
//					.addAllToEnv(envVars)
//					.endContainer()
//					.endSpec()
//					.endTemplate()
//					.endSpec()
//					.build();
			
		}
		public void getService(Service service) {
			// TODO Auto-generated method stub
			
		}
	}
	public static class Kv {
		public String name;
		public String value;
	}
	public static class ChartRelation {
		public String from;
		public String to;
	}
	public static class ChartDependency {
		public List<Dependency> elements;
	}
	public static class Dependency {
		public String key;
		public ChartNode value;
	}
	//返回部署的顺序队列
	
}
