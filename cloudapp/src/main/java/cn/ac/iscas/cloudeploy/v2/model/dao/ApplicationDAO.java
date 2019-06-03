package cn.ac.iscas.cloudeploy.v2.model.dao;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;

import cn.ac.iscas.cloudeploy.v2.model.entity.application.Application;

public interface ApplicationDAO extends PagingAndSortingRepository<Application, Long> {
}
