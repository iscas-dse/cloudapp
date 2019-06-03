package cn.ac.iscas.cloudeploy.v2.model.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.ContainerParam;

public interface ContainerParamDAO extends PagingAndSortingRepository<ContainerParam, Long> {
}
