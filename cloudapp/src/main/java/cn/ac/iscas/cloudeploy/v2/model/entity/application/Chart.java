package cn.ac.iscas.cloudeploy.v2.model.entity.application;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.repository.NoRepositoryBean;

import cn.ac.iscas.cloudeploy.v2.model.entity.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "d_chart")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Chart extends IdEntity {
	public Chart(Chart chart) {
		this.createTime=chart.createTime;
		this.description=chart.description;
		this.id=chart.id;
		this.updateTime=chart.updateTime;
		this.version=chart.version;
		this.name=chart.name;
	}
	@Column
	private String name;
	@Column
	private String version;
	@Column
	private String description;
	@Column
	private String type;
}
