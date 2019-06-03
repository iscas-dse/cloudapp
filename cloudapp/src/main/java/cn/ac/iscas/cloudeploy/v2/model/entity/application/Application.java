package cn.ac.iscas.cloudeploy.v2.model.entity.application;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.cache.annotation.Cacheable;

import com.google.common.collect.ImmutableList;
import com.typesafe.config.ConfigException.Null;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;

@Entity
@Table(name = "d_application")
public class Application extends IdEntity {
	@Column
	private String name;
//	private String definedFileKey; 
//	private String deployFileKey;
	@OneToMany(mappedBy = "application")
	private List<Container> containers;  //服务实例
	@OneToMany(mappedBy = "application") //服务实例关系
	private List<Relation> relations;
	@Enumerated(EnumType.STRING)
	private Status status;
	public enum Status {
		CREATED, DEPLOYED, MODIFIED,STABLE;
		public static Status of(String input) {
			if (input != null) {
				for (Status res : values()) {
					if (res.name().equalsIgnoreCase(input)) {
						return res;
					}
				}
			}
			return null;
		}
	}
	
	private Application() {
		
	}

	public Application(String name) {
		this.name = name;
		this.containers = new ArrayList<>();
		this.relations = new ArrayList<>();
		this.status = Status.CREATED;
	}

	public String getName() {
		return name;
	}

	public List<Container> getContainers() {
		return containers;
	}

	public List<Relation> getRelations() {
		return relations;
	}

	public Status getStatus() {
		return status;
	}

	public void addContainer(Container container) {
		containers.add(container);
	}

	public void addContainers(List<Container> containers) {
		this.containers.addAll(containers);
	}

	public void addRelation(Relation relation) {
		this.relations.add(relation);
	}

	public void addRelations(List<Relation> relations) {
		this.relations.addAll(relations);
	}

	public void changeStatusToDeployed() {
		this.status = Status.DEPLOYED;
	}

	public void changeStatusToModified() {
		this.status = Status.MODIFIED;
	}

/*	public String getDefinedFileKey() {
		return definedFileKey;
	}

	public void setDefinedFileKey(String definedFileKey) {
		this.definedFileKey = definedFileKey;
	}

	public String getDeployFileKey() {
		return deployFileKey;
	}

	public void setDeployFileKey(String deployFileKey) {
		this.deployFileKey = deployFileKey;
	}
	*/
}
