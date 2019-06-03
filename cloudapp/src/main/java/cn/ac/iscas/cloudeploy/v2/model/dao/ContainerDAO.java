package cn.ac.iscas.cloudeploy.v2.model.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.Container;

public interface ContainerDAO extends PagingAndSortingRepository<Container, Long> {
}
