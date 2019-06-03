package cn.ac.iscas.cloudeploy.v2.model.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;
import cn.ac.iscas.cloudeploy.v2.model.entity.application.Chart;

public interface ChartDAO extends PagingAndSortingRepository<Chart, Long> {
}
