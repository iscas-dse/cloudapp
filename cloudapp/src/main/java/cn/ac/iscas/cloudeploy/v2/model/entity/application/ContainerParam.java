package cn.ac.iscas.cloudeploy.v2.model.entity.application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "d_app_container_param")
public class ContainerParam extends IdEntity {
	@Column
	private String paramKey;
	@Column
	private String paramValue;
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", name = "container_id")
	private Container container;
}
