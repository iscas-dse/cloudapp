package cn.ac.iscas.cloudeploy.v2.model.entity.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.google.common.collect.ImmutableList;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;
import cn.ac.iscas.cloudeploy.v2.model.entity.component.Component;
import cn.ac.iscas.cloudeploy.v2.model.entity.host.DHost;
import cn.ac.iscas.cloudeploy.v2.util.ConstantsUtil;
import lombok.Data;
import lombok.Setter;

@Entity
@Table(name = "d_app_container")
public class Container extends IdEntity {
	@Column
	@Setter private String name;
	@Column
	private int port;
	@OneToMany(mappedBy = "container")
	private List<ContainerParam> params;
//	@OneToMany(mappedBy = "container")
//	private List<TemplateParam> templates;
//	@OneToMany(mappedBy = "container")
//	private List<ContainerAttribute> attributes;
	@Column
	private int maxCount;
	@Column
	private int initCount;
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "app_id")
	private Application application;
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "component_id")
	private Component component;
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "chart_id")
	private Chart chart;
	@Enumerated(EnumType.STRING)
	private Status status;
	@OneToMany(mappedBy = "to")
	private List<Relation> inRelations;
	@OneToMany(mappedBy = "from")
	private List<Relation> outRelations;
	@Column(columnDefinition = "INT(11) DEFAULT 0")
	private Integer xPos;
	@Column(columnDefinition = "INT(11) DEFAULT 0")
	private Integer yPos;
	@Column
	private String identifier;
	@OneToMany(mappedBy = "container")
	private List<ContainerInstance> instances;
	@Column
	private String service;
	@Column
	private String version;
	@Column
	private String deployment;
	public enum Status {
		CREATED, DEPLOYED, REMOVED
	}
	public  Container() {
		this.params = new ArrayList<>();
		this.status = Status.CREATED;
		this.inRelations = new ArrayList<>();
		this.outRelations = new ArrayList<>();
		this.instances = new ArrayList<>();
		this.version=ConstantsUtil.CONTAINER_VERSION;
	}
	public Container(String name, int port, int maxCount,
			int initCount, Application application, Component component,
			int xPos, int yPos, String identifier) {
		reset(name, port, maxCount, initCount, application, component,
				xPos, yPos, identifier);
		this.params = new ArrayList<>();
		this.status = Status.CREATED;
		this.inRelations = new ArrayList<>();
		this.outRelations = new ArrayList<>();
		this.instances = new ArrayList<>();
	}
	public void reset(String name, int port, int maxCount,
			int initCount, Application application, Component component,
			int xPos, int yPos, String identifier) {
		this.name = name;
		this.port = port;
		this.maxCount = maxCount;
		this.initCount = initCount;
		this.application = application;
		this.component = component;
		this.xPos = xPos;
		this.yPos = yPos;
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public int getPort() {
		return port;
	}

	public List<ContainerParam> getParams() {
		return ImmutableList.copyOf(params);
	}

	public int getMaxCount() {
		return maxCount;
	}

	public Application getApplication() {
		return application;
	}

	public Component getComponent() {
		return component;
	}

	public Status getStatus() {
		return status;
	}

	public List<Relation> getInRelations() {
		return ImmutableList.copyOf(inRelations);
	}

	public List<Relation> getOutRelations() {
		return ImmutableList.copyOf(outRelations);
	}

	public Integer getxPos() {
		return xPos;
	}

	public Integer getyPos() {
		return yPos;
	}

	public List<ContainerInstance> getInstances() {
		return ImmutableList.copyOf(instances);
	}

	public int getInitCount() {
		return initCount;
	}

//	public List<TemplateParam> getTemplates() {
//		if(templates == null) return Collections.emptyList();
//		return templates;
//	}
//
//	public void setTemplates(List<TemplateParam> templates) {
//		this.templates = templates;
//	}
//
//	public List<ContainerAttribute> getAttributes() {
//		if(attributes == null) return Collections.emptyList();
//		return attributes;
//	}
//
//	public void setAttributes(List<ContainerAttribute> attributes) {
//		this.attributes = attributes;
//	}

	public void addInRelation(Relation relation) {
		inRelations.add(relation);
	}

	public void addInRelations(List<Relation> relations) {
		inRelations.addAll(relations);
	}

	public void addOutRelation(Relation relation) {
		outRelations.add(relation);
	}

	public void addOutRelations(List<Relation> relations) {
		outRelations.addAll(relations);
	}

	public void addInstance(ContainerInstance instance) {
		instances.add(instance);
	}

	public void removeInstance(ContainerInstance instance) {
		instances.remove(instance);
	}

	public void addInstances(List<ContainerInstance> instances) {
		this.instances.addAll(instances);
	}

	public void addParam(ContainerParam param) {
		params.add(param);
	}

	public void addParams(List<ContainerParam> params) {
		this.params.addAll(params);
	}

	public void changeStatusToDeployed() {
		status = Status.DEPLOYED;
	}

	public void changeStatusToRemoved() {
		status = Status.REMOVED;
	}

	public void changeStatusToCreated() {
		status = Status.CREATED;
	}

	public String getIdentifier() {
		return identifier;
	}
	public void setDeployment(String deploymentName) {
		// TODO Auto-generated method stub
		this.deployment=deploymentName;
	}
	public void setService(String serviceName) {
		// TODO Auto-generated method stub
		this.service=serviceName;
	}
	public void setIdentifier(String nodeId) {
		// TODO Auto-generated method stub
		this.identifier=nodeId;
	}
	public void setXPos(int xPos) {
		// TODO Auto-generated method stub
		this.xPos=xPos;
	}
	public void setYPos(int yPos) {
		// TODO Auto-generated method stub
		this.yPos=yPos;
	}
	public void setApplication(Application application) {
		// TODO Auto-generated method stub
		this.application=application;
		
	}
	public Chart getChart() {
		// TODO Auto-generated method stub
		return this.chart;
	}
	public void setChart(Chart chart) {
		// TODO Auto-generated method stub
		this.chart=chart;
	}
	public String getService() {
		// TODO Auto-generated method stub
		return this.service;
	}
	public String getDeployment() {
		return deployment;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
}
