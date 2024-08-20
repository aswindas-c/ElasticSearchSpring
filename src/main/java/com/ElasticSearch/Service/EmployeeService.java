package com.ElasticSearch.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ElasticSearch.DTO.ManagerResponse;
import com.ElasticSearch.DTO.Response;
import com.ElasticSearch.Model.EmployeeManager;
import com.ElasticSearch.Repository.EmployeeManagerElasticRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.management.openmbean.KeyAlreadyExistsException;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeManagerElasticRepository employeeElasticRepo;

    public Response addEmployee(EmployeeManager employee) {

        String email = employee.getEmail();
        String designation = employee.getDesignation();
        String mobileNumber = employee.getMobile();
        String department = employee.getDepartment();


        // Validate employee data
        validateEmployeeData(email, designation, mobileNumber, department);

        // Find the maximum ID of employee
        Integer maxId = employeeElasticRepo.findMaxId();
        if(maxId != null) {
            employee.setId(maxId + 1);
        } 
        else {
            employee.setId(1);
        }

        // Check if employee with the same ID already exists
        if (employeeElasticRepo.existsById(employee.getId())) {
            throw new KeyAlreadyExistsException("Employee ID already exists.");
        }

        // Set created time and updated time to the current time
        OffsetDateTime currentTime = OffsetDateTime.now();
        employee.setCreatedTime(currentTime);
        employee.setUpdatedTime(currentTime);

        calculateYearsOfExperience(employee);

        // Handle special case for Account Manager
        if ("Account Manager".equalsIgnoreCase(employee.getDesignation())) {
            if (employee.getManagerId() != 0) {
                throw new IllegalArgumentException("Account Manager must have Manager ID set to 0. Employee cannot be added.");
            }

            // Check if a manager already exists in the department            
            List<EmployeeManager> existingManagers = employeeElasticRepo.findByDepartment(employee.getDepartment());

            if (!existingManagers.isEmpty()) {
                throw new KeyAlreadyExistsException("A manager already exists in the department: " + employee.getDepartment());
            }

            employeeElasticRepo.save(employee);
            return new Response("Employee added as Manager successfully with ID: " + employee.getId());
        } else {
            // Handle non-Account Manager 
            if (employee.getManagerId() == 0) {
                throw new IllegalArgumentException("Manager ID 0 should have designation as Account Manager. Employee cannot be added.");
            }
        }

        // Handle normal employee
        EmployeeManager manager = employeeElasticRepo.findById(employee.getManagerId());
        if (manager == null) {
                throw new NoSuchElementException("Manager with ID " + employee.getManagerId() + " not found. Employee cannot be added.");
        }

        if (manager.getManagerId() != 0) {
            throw new NoSuchElementException("Employee with ID " + employee.getManagerId() + " is not a manager. Employee cannot be added.");
        }
        // Check if employee and manager are in the same department
        if (!employee.getDepartment().equalsIgnoreCase(manager.getDepartment())) {
            throw new IllegalArgumentException("Employee and manager must belong to the same department. Employee cannot be added.");
        }

        // Save the employee
        employeeElasticRepo.save(employee);

        return new Response("Employee added successfully under Manager with ID: " + manager.getId());
    }

    public void validateEmployeeData(String email, String designation, String mobileNumber, String department) {
        List<String> errors = new ArrayList<>();
        
        if (!"Account Manager".equalsIgnoreCase(designation) && !"associate".equalsIgnoreCase(designation)) {
            errors.add("Designation can only be Account Manager or associate.");
        }
     
        if (!"sales".equalsIgnoreCase(department) &&
                !"delivery".equalsIgnoreCase(department) &&
                !"QA".equalsIgnoreCase(department) &&
                !"engineering".equalsIgnoreCase(department) &&
                !"BA".equalsIgnoreCase(department)) {
            errors.add("Invalid department. Must be one of: sales, delivery, QA, engineering, BA.");
        }
     
        if (!isValidEmail(email)) {
            errors.add("Invalid email format.");
        }
        
        if (mobileNumber.length() != 10 || !mobileNumber.matches("\\d+")) {
            errors.add("Invalid mobile number. It must be a 10-digit number.");
        }
        
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }
    }

    private void calculateYearsOfExperience(EmployeeManager employee) {
        if (employee.getDateOfJoining() != null) {
            LocalDate joiningDate = employee.getDateOfJoining().toLocalDate();
            LocalDate currentDate = LocalDate.now();
            employee.setYearsOfExperience(Period.between(joiningDate, currentDate).getYears());
        }
    }

    public List<ManagerResponse> getEmployee(Integer managerId, Integer yearsOfExperience) {
        List<EmployeeManager> employees;

        if (managerId != null && yearsOfExperience != null) {
            employees = employeeElasticRepo.findByManagerIdAndYearsOfExperienceGreaterThanEqual(managerId, yearsOfExperience);
        } else if (managerId != null) {
            employees = employeeElasticRepo.findByManagerId(managerId);
        } else if (yearsOfExperience != null) {
            employees = employeeElasticRepo.findByYearsOfExperienceGreaterThanEqual(yearsOfExperience);
        } else {
            employees = employeeElasticRepo.findAll();
        }

        // Filter out employee with managerId of 0
        employees = employees.stream()
                .filter(emp -> emp.getManagerId() != 0)
                .collect(Collectors.toList());

        if (employees.isEmpty()) {
            throw new NoSuchElementException("No Employee found.");
        }

        // Group employees by manager ID
        List<ManagerResponse> responseList = employees.stream()
                .collect(Collectors.groupingBy(EmployeeManager::getManagerId))
                .entrySet().stream()
                .map(entry -> {
                    Integer currentManagerId = entry.getKey();
                    List<EmployeeManager> employeeList = entry.getValue();
                    EmployeeManager manager = employeeElasticRepo.findById(currentManagerId);

                    return new ManagerResponse(
                            "Successfully fetched",
                            manager.getName(),
                            manager.getDepartment(),
                            currentManagerId,
                            employeeList
                    );
                })
                .collect(Collectors.toList());

        return responseList;
    }

    public Response changeEmployeeManager(Integer employeeId, Integer newManagerId) {
        // Fetch the employee
        EmployeeManager employee = employeeElasticRepo.findById(employeeId);
        if (employee == null) {
            throw new NoSuchElementException("Employee with ID " + employeeId + " not found.");
        }
    
        if (employee.getManagerId().equals(newManagerId)) {
            throw new IllegalStateException("Employee is currently under the given manager. No changes required.");
        }
    
        // Fetch the new manager
        EmployeeManager newManager = employeeElasticRepo.findById(newManagerId);
        if (newManager == null) {
            throw new NoSuchElementException("New manager with ID " + newManagerId + " not found.");
        }
    
        if (!employee.getDepartment().equalsIgnoreCase(newManager.getDepartment())) {
            employee.setDepartment(newManager.getDepartment());
        }
    
        // Build the response
        String originalManagerName = employeeElasticRepo.findById(employee.getManagerId()).getName();
        String newManagerName = newManager.getName();

        // Update the employee manager ID and updatedTime
        employee.setManagerId(newManagerId);
        employee.setUpdatedTime(OffsetDateTime.now());
        employeeElasticRepo.save(employee);
    
        return new Response(
                employee.getName() + "'s manager has been successfully changed from " + originalManagerName + " to " + newManagerName + "."
        );
    }
    
    public Response deleteEmployee(Integer employeeId) {
        // Check if the employee exists
        EmployeeManager employee = employeeElasticRepo.findById(employeeId);
        if (employee == null) {
            throw new NoSuchElementException("Employee with ID " + employeeId + " not found.");
        }
    
        List<EmployeeManager> subordinates = employeeElasticRepo.findByManagerId(employeeId);
        if (!subordinates.isEmpty()) {
            throw new IllegalStateException("Cannot delete Employee with ID " + employeeId + " as they are a manager with subordinates.");
        }
    
        // Delete the employee
        employeeElasticRepo.delete(employee);
        return new Response("Successfully deleted " + employee.getName() + " from the organization.");
    }    
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
