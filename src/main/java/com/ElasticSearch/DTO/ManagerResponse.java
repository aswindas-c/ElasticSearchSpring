package com.ElasticSearch.DTO;

import java.util.List;

import com.ElasticSearch.Model.EmployeeManager;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ManagerResponse {
    private String message;
    private String managerName;
    private String department;
    private Integer managerId;
    private List<EmployeeManager> employeeList;

    public ManagerResponse(String message, String managerName, String department, Integer managerId, List<EmployeeManager> employeeList) {
        this.message = message;
        this.managerName = managerName;
        this.department = department;
        this.managerId = managerId;
        this.employeeList = employeeList;
    }
}
