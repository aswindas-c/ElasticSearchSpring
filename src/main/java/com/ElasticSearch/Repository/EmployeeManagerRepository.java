package com.ElasticSearch.Repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.ElasticSearch.Model.EmployeeManager;

@Repository
public interface EmployeeManagerRepository extends ElasticsearchRepository<EmployeeManager, Integer> {

    List<EmployeeManager> findByManagerIdAndYearsOfExperienceGreaterThanEqual(Integer managerId, Integer yearsOfExperience);

    List<EmployeeManager> findByManagerId(Integer managerId);

    List<EmployeeManager> findByYearsOfExperienceGreaterThanEqual(int yearsOfExperience);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"department\": \"?0\"}}, {\"term\": {\"managerId\": 0}}]}}")
    List<EmployeeManager> findByDepartment(String department);

    EmployeeManager findTopByOrderByIdDesc();

    @Query("{\"match\": {\"id\": \"?0\"}}")
    EmployeeManager findByIdCustom(Integer id);
}
