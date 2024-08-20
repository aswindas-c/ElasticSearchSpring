package com.ElasticSearch.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ElasticSearch.Model.EmployeeManager;

@Service
public class EmployeeManagerElasticRepository {

    @Autowired
    private EmployeeManagerRepository empManRepo;

    public List<EmployeeManager> findByManagerIdAndYearsOfExperienceGreaterThanEqual(Integer managerId, Integer yearsOfExperience) {
        return empManRepo.findByManagerIdAndYearsOfExperienceGreaterThanEqual(managerId, yearsOfExperience);
    }

    public List<EmployeeManager> findByManagerId(Integer managerId) {
        return empManRepo.findByManagerId(managerId);
    }

    public List<EmployeeManager> findByYearsOfExperienceGreaterThanEqual(int yearsOfExperience) {
        return empManRepo.findByYearsOfExperienceGreaterThanEqual(yearsOfExperience);
    }

    public List<EmployeeManager> findByDepartment(String department) {
        return empManRepo.findByDepartment(department);
    }

    public Integer findMaxId() {
        EmployeeManager employeeManager = empManRepo.findTopByOrderByIdDesc();
        return (employeeManager != null) ? employeeManager.getId() : null;
    }

    public EmployeeManager save(EmployeeManager employeeManager) {
        return empManRepo.save(employeeManager);
    }

    public void delete(EmployeeManager employeeManager) {
        empManRepo.delete(employeeManager);
    }

    public boolean existsById(Integer id) {
        return empManRepo.existsById(id);
    }

    public List<EmployeeManager> findAll() {
        Iterable<EmployeeManager> iterable = empManRepo.findAll();
        return StreamSupport.stream(iterable.spliterator(), false)
                            .collect(Collectors.toList());
    }

    public EmployeeManager findById(Integer id) {
        return empManRepo.findByIdCustom(id);
    }
}
