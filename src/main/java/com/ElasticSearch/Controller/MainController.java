package com.ElasticSearch.Controller;


import org.springframework.web.bind.annotation.RestController;

import com.ElasticSearch.DTO.ChangeManagerRequest;
import com.ElasticSearch.DTO.ManagerResponse;
import com.ElasticSearch.DTO.Response;
import com.ElasticSearch.Model.EmployeeManager;
import com.ElasticSearch.Service.EmployeeService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class MainController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/addEmployee")
    public Response addEmployee(@Valid @RequestBody EmployeeManager staffMember) {
        return employeeService.addEmployee(staffMember);
    }

    @GetMapping("/getEmployee")
    public List<ManagerResponse> getEmployee(
            @RequestParam(required = false) Integer managerId,
            @RequestParam(required = false) Integer yearsOfExperience) {
        return employeeService.getEmployee(managerId, yearsOfExperience);
    }

    @DeleteMapping("/deleteEmployee")
    public Response deleteEmployee(@RequestParam Integer employeeId) {
        return employeeService.deleteEmployee(employeeId);
    }

    @PutMapping("/changeEmployeeManager")
    public Response changeEmployeeManager(
            @RequestBody ChangeManagerRequest request) {
        return employeeService.changeEmployeeManager(request.getEmployeeId(), request.getManagerId());
    }
}