package com.ElasticSearch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.ElasticSearch.Controller.MainController;
import com.ElasticSearch.DTO.ManagerResponse;
import com.ElasticSearch.DTO.Response;
import com.ElasticSearch.Model.EmployeeManager;
import com.ElasticSearch.Repository.EmployeeManagerElasticRepository;
import com.ElasticSearch.Service.EmployeeService;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EmployeeServiceTest {

    @Mock
    private EmployeeManagerElasticRepository employeeManagerRepo;

    @InjectMocks
    private MainController mainController;


    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    public void testAddEmployee_Success() {
        EmployeeManager employee = new EmployeeManager();
        employee.setName("John Doe");
        employee.setDesignation("Associate");
        employee.setDepartment("Sales");
        employee.setEmail("John@gmail.com");
        employee.setMobile("5678345678");
        employee.setDateOfJoining(OffsetDateTime.now());
        employee.setManagerId(2);

        EmployeeManager manager = new EmployeeManager();
        manager.setId(2);
        manager.setDepartment("Sales");
        manager.setDesignation("Account Manager");
        manager.setManagerId(0);

        when(employeeManagerRepo.findMaxId()).thenReturn(1);
        when(employeeManagerRepo.existsById(1)).thenReturn(false);
        when(employeeManagerRepo.findById(2)).thenReturn(manager);

        Response response = employeeService.addEmployee(employee);
        assertEquals("Employee added successfully under Manager with ID: 2", response.getMessage());
        verify(employeeManagerRepo).save(employee);
    }
    @Test
    public void testAddEmployee_Success_Manager() {
        EmployeeManager employee = new EmployeeManager();
        employee.setName("John Doe");
        employee.setDesignation("Account Manager");
        employee.setDepartment("QA");
        employee.setEmail("John@gmail.com");
        employee.setMobile("5678345678");
        employee.setDateOfJoining(OffsetDateTime.now());
        employee.setManagerId(0);

        when(employeeManagerRepo.findMaxId()).thenReturn(1);
        when(employeeManagerRepo.existsById(1)).thenReturn(false);

        Response response = employeeService.addEmployee(employee);
        assertEquals("Employee added as Manager successfully with ID: 2", response.getMessage());
        verify(employeeManagerRepo).save(employee);
    }

    @Test
    public void testAddEmployee_Failure_NotManager() {
        EmployeeManager employee = new EmployeeManager();
        employee.setName("John Doe");
        employee.setDesignation("Associate");
        employee.setDepartment("engineering");
        employee.setEmail("John@gmail.com");
        employee.setMobile("5678345678");
        employee.setDateOfJoining(OffsetDateTime.now());
        employee.setManagerId(2);

        EmployeeManager manager = new EmployeeManager();
        manager.setId(2);
        manager.setDepartment("engineering");
        manager.setDesignation("Associate");
        manager.setManagerId(5);

        when(employeeManagerRepo.findMaxId()).thenReturn(1);
        when(employeeManagerRepo.existsById(1)).thenReturn(false);
        when(employeeManagerRepo.findById(2)).thenReturn(manager);

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            employeeService.addEmployee(employee);
        });

        assertEquals("Employee with ID 2 is not a manager. Employee cannot be added.", exception.getMessage());
    }

    @Test
    public void testAddEmployee_Failure_DifferentDepartment() {
        EmployeeManager employee = new EmployeeManager();
        employee.setName("John Doe");
        employee.setDesignation("Associate");
        employee.setDepartment("BA");
        employee.setEmail("John@gmail.com");
        employee.setMobile("5678345678");
        employee.setDateOfJoining(OffsetDateTime.now());
        employee.setManagerId(2);

        EmployeeManager manager = new EmployeeManager();
        manager.setId(2);
        manager.setDepartment("sales");
        manager.setDesignation("Account manager");
        manager.setManagerId(0);

        when(employeeManagerRepo.findMaxId()).thenReturn(1);
        when(employeeManagerRepo.existsById(1)).thenReturn(false);
        when(employeeManagerRepo.findById(2)).thenReturn(manager);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.addEmployee(employee);
        });

        assertEquals("Employee and manager must belong to the same department. Employee cannot be added.", exception.getMessage());
    }

    @Test
    public void testAddEmployee_Failure_ManagerNotFound() {
        EmployeeManager employee = new EmployeeManager();
        employee.setName("John Doe");
        employee.setDesignation("Associate");
        employee.setDepartment("Sales");
        employee.setEmail("John@gmail.com");
        employee.setMobile("5678345678");
        employee.setDateOfJoining(OffsetDateTime.now());
        employee.setManagerId(2);

        when(employeeManagerRepo.findMaxId()).thenReturn(1);
        when(employeeManagerRepo.existsById(1)).thenReturn(false);
        when(employeeManagerRepo.findById(2)).thenReturn(null);

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            employeeService.addEmployee(employee);
        });

        assertEquals("Manager with ID 2 not found. Employee cannot be added.", exception.getMessage());
    }

    @Test
    public void testAddEmployee_Failure_ManagerId() {
        EmployeeManager employee = new EmployeeManager();
        employee.setName("John Doe");
        employee.setDesignation("Account Manager");
        employee.setDepartment("delivery");
        employee.setEmail("John@gmail.com");
        employee.setMobile("5678345678");
        employee.setDateOfJoining(OffsetDateTime.now());
        employee.setManagerId(2);

        when(employeeManagerRepo.findMaxId()).thenReturn(1);
        when(employeeManagerRepo.existsById(1)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.addEmployee(employee);
        });

        assertEquals("Account Manager must have Manager ID set to 0. Employee cannot be added.", exception.getMessage());
    }

    @Test
    public void testAddEmployee_Failure_ManagerDesignation() {
        EmployeeManager employee = new EmployeeManager();
        employee.setName("John Doe");
        employee.setDesignation("Associate");
        employee.setDepartment("Sales");
        employee.setEmail("John@gmail.com");
        employee.setMobile("5678345678");
        employee.setDateOfJoining(OffsetDateTime.now());
        employee.setManagerId(0);

        when(employeeManagerRepo.findMaxId()).thenReturn(1);
        when(employeeManagerRepo.existsById(1)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.addEmployee(employee);
        });

        assertEquals("Manager ID 0 should have designation as Account Manager. Employee cannot be added.", exception.getMessage());
    }

    @Test
    public void testAddEmployee_Failue_SameId() {
        EmployeeManager employee = new EmployeeManager();
        employee.setName("John Doe");
        employee.setDesignation("Account Manager");
        employee.setDepartment("Sales");
        employee.setEmail("John@gmail.com");
        employee.setMobile("5678345678");
        employee.setDateOfJoining(OffsetDateTime.now());
        employee.setManagerId(2);

        when(employeeManagerRepo.findMaxId()).thenReturn(null);
        when(employeeManagerRepo.existsById(1)).thenReturn(true);

        Exception exception = assertThrows(KeyAlreadyExistsException.class, () -> {
            employeeService.addEmployee(employee);
        });

        assertEquals("Employee ID already exists.", exception.getMessage());
    }

    @Test
    public void testAddEmployee_failure() {
        EmployeeManager employee = new EmployeeManager();
        employee.setName("John Doe");
        employee.setDesignation("Assiate");
        employee.setDepartment("Sas");
        employee.setEmail("Johmail.com");
        employee.setMobile("5675678");
        employee.setDateOfJoining(OffsetDateTime.now());
        employee.setManagerId(2);

        EmployeeManager manager = new EmployeeManager();
        manager.setId(2);
        manager.setDepartment("Sales");
        manager.setDesignation("Account Manager");
        manager.setManagerId(0);

        when(employeeManagerRepo.findMaxId()).thenReturn(1);
        when(employeeManagerRepo.existsById(1)).thenReturn(false);
        when(employeeManagerRepo.findById(2)).thenReturn(manager);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.addEmployee(employee);
        });

        assertEquals("Designation can only be Account Manager or associate., Invalid department. Must be one of: sales, delivery, QA, engineering, BA., Invalid email format., Invalid mobile number. It must be a 10-digit number.", exception.getMessage());
    }

    @Test
    public void testAddEmployee_ManagerAlreadyExists() {
        EmployeeManager employee = new EmployeeManager();
        employee.setName("Jane Doe");
        employee.setDesignation("Account Manager");
        employee.setDepartment("Sales");
        employee.setEmail("John@gmail.com");
        employee.setMobile("5678345678");
        employee.setDateOfJoining(OffsetDateTime.now());
        employee.setManagerId(0);

        when(employeeManagerRepo.findMaxId()).thenReturn(1);
        when(employeeManagerRepo.existsById(1)).thenReturn(false);
        when(employeeManagerRepo.findByDepartment("Sales")).thenReturn(Arrays.asList(new EmployeeManager()));

        Exception exception = assertThrows(KeyAlreadyExistsException.class, () -> {
            employeeService.addEmployee(employee);
        });

        assertEquals("A manager already exists in the department: Sales", exception.getMessage());
    }

    //Get Employee By ManagerId Only

    @Test
    public void testGetEmployee_ByManagerIdOnly() {
        EmployeeManager manager = new EmployeeManager();
        manager.setId(1);
        manager.setName("John Manager");
        manager.setDepartment("sales");

        EmployeeManager employee = new EmployeeManager();
        employee.setId(2);
        employee.setManagerId(1);
        employee.setDepartment("sales");

        when(employeeManagerRepo.findByManagerId(1)).thenReturn(Arrays.asList(employee));
        when(employeeManagerRepo.findById(1)).thenReturn(manager);

        List<ManagerResponse> response = employeeService.getEmployee(1, null);
        assertFalse(response.isEmpty());
        assertEquals(1, response.get(0).getManagerId());
        assertEquals("John Manager", response.get(0).getManagerName());

    }

    //Get Employee by YearsOfExperience only

    @Test
    public void testGetEmployee_ByYearsOfExperienceOnly() {
        Integer yearsOfExperience = 5;

        EmployeeManager manager = new EmployeeManager();
        manager.setId(1);
        manager.setName("John Manager");
        manager.setDepartment("sales");

        EmployeeManager employee = new EmployeeManager();
        employee.setId(2);
        employee.setManagerId(1);
        employee.setDepartment("sales");
        employee.setYearsOfExperience(yearsOfExperience);

        when(employeeManagerRepo.findByYearsOfExperienceGreaterThanEqual(yearsOfExperience))
                .thenReturn(Arrays.asList(employee));
        when(employeeManagerRepo.findById(1)).thenReturn(manager);

        List<ManagerResponse> response = employeeService.getEmployee(null, yearsOfExperience);
        assertFalse(response.isEmpty());
        assertEquals(1, response.get(0).getManagerId());
        assertEquals("John Manager", response.get(0).getManagerName());
        assertEquals(1, response.get(0).getEmployeeList().size());
        assertEquals(2, response.get(0).getEmployeeList().get(0).getId());
    }

    //Get Employee By ManagerId and YearsOfExperience

    @Test
    public void testGetEmployee_ByManagerIdAndYearsOfExperience() {
        Integer managerId = 1;
        Integer yearsOfExperience = 5;

        EmployeeManager manager = new EmployeeManager();
        manager.setId(managerId);
        manager.setName("John Manager");
        manager.setDepartment("sales");

        EmployeeManager employee = new EmployeeManager();
        employee.setId(2);
        employee.setManagerId(managerId);
        employee.setDepartment("sales");
        employee.setYearsOfExperience(yearsOfExperience);

        when(employeeManagerRepo.findByManagerIdAndYearsOfExperienceGreaterThanEqual(managerId, yearsOfExperience))
                .thenReturn(Arrays.asList(employee));
        when(employeeManagerRepo.findById(managerId)).thenReturn(manager);

        List<ManagerResponse> response = employeeService.getEmployee(managerId, yearsOfExperience);
        assertFalse(response.isEmpty());
        assertEquals(managerId, response.get(0).getManagerId());
        assertEquals("John Manager", response.get(0).getManagerName());
        assertEquals(1, response.get(0).getEmployeeList().size());
        assertEquals(2, response.get(0).getEmployeeList().get(0).getId());
    }

    //Get all Employee   
    
    @Test
    public void testGetEmployee_AllEmployees() {
        EmployeeManager manager = new EmployeeManager();
        manager.setId(1);
        manager.setName("John Manager");
        manager.setDepartment("sales");

        EmployeeManager employee = new EmployeeManager();
        employee.setId(2);
        employee.setManagerId(1);
        employee.setDepartment("sales");

        when(employeeManagerRepo.findAll()).thenReturn(Arrays.asList(employee));
        when(employeeManagerRepo.findById(1)).thenReturn(manager);

        List<ManagerResponse> response = employeeService.getEmployee(null, null);
        assertFalse(response.isEmpty());
        assertEquals(1, response.get(0).getManagerId());
        assertEquals("John Manager", response.get(0).getManagerName());
        assertEquals(1, response.get(0).getEmployeeList().size());
        assertEquals(2, response.get(0).getEmployeeList().get(0).getId());
    }

    @Test
    public void testGetEmployee_NoEmployee() {
        EmployeeManager manager = new EmployeeManager();
        manager.setId(1);
        manager.setName("John Manager");
        manager.setDepartment("sales");

        when(employeeManagerRepo.findAll()).thenReturn(Arrays.asList());
        when(employeeManagerRepo.findById(1)).thenReturn(manager);

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            employeeService.getEmployee(null, null);
        });
        assertEquals("No Employee found.", exception.getMessage());
    }

    @Test
    public void testChangeEmployeeManager_Success() {
        EmployeeManager employee = new EmployeeManager();
        employee.setId(1);
        employee.setName("John Doe");
        employee.setManagerId(2);
        employee.setDepartment("sales");

        EmployeeManager newManager = new EmployeeManager();
        newManager.setId(3);
        newManager.setName("Jane Manager");
        newManager.setDepartment("BA");

        EmployeeManager oldManager = new EmployeeManager();
        oldManager.setId(2);
        oldManager.setName("Old Manager");
        oldManager.setDepartment("sales");

        when(employeeManagerRepo.findById(1)).thenReturn(employee);
        when(employeeManagerRepo.findById(3)).thenReturn(newManager);
        when(employeeManagerRepo.findById(2)).thenReturn(oldManager);

        Response response = employeeService.changeEmployeeManager(1, 3);
        assertEquals("John Doe's manager has been successfully changed from Old Manager to Jane Manager.", response.getMessage());
        verify(employeeManagerRepo).save(employee);
    }

    @Test
    public void testChangeEmployeeManager_SameManager() {
        EmployeeManager employee = new EmployeeManager();
        employee.setId(1);
        employee.setName("John Doe");
        employee.setManagerId(2);
        employee.setDepartment("sales");

        EmployeeManager oldManager = new EmployeeManager();
        oldManager.setId(2);
        oldManager.setName("Old Manager");
        oldManager.setDepartment("sales");

        when(employeeManagerRepo.findById(1)).thenReturn(employee);
        when(employeeManagerRepo.findById(2)).thenReturn(oldManager);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            employeeService.changeEmployeeManager(1, 2);
        });
        assertEquals("Employee is currently under the given manager. No changes required.", exception.getMessage());
    }

    @Test
    public void testChangeEmployeeManager_NoEmployee() {

        EmployeeManager newManager = new EmployeeManager();
        newManager.setId(3);
        newManager.setName("Jane Manager");
        newManager.setDepartment("sales");

        when(employeeManagerRepo.findById(1)).thenReturn(null);
        when(employeeManagerRepo.findById(3)).thenReturn(newManager);

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            employeeService.changeEmployeeManager(1, 3);
        });
        assertEquals("Employee with ID 1 not found.", exception.getMessage());
    }

    @Test
    public void testChangeEmployeeManager_NoManager() {

        EmployeeManager employee = new EmployeeManager();
        employee.setId(1);
        employee.setName("John Doe");
        employee.setManagerId(2);
        employee.setDepartment("sales");

        when(employeeManagerRepo.findById(1)).thenReturn(employee);
        when(employeeManagerRepo.findById(3)).thenReturn(null);

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            employeeService.changeEmployeeManager(1, 3);
        });
        assertEquals("New manager with ID 3 not found.", exception.getMessage());
    }

    @Test
    public void testDeleteEmployee_Success() {
        EmployeeManager employee = new EmployeeManager();
        employee.setId(1);
        employee.setName("John Doe");

        when(employeeManagerRepo.findById(1)).thenReturn(employee);
        when(employeeManagerRepo.findByManagerId(1)).thenReturn(Arrays.asList());

        Response response = employeeService.deleteEmployee(1);
        assertEquals("Successfully deleted John Doe from the organization.", response.getMessage());
        verify(employeeManagerRepo).delete(employee);
    }

    @Test
    public void testDeleteEmployee_employeeNotFound() {
        EmployeeManager employee = new EmployeeManager();
        employee.setId(1);
        employee.setName("John Doe");

        when(employeeManagerRepo.existsById(1)).thenReturn(false);
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            employeeService.deleteEmployee(1);
        });
        assertEquals("Employee with ID 1 not found.", exception.getMessage());
    }

    @Test
    public void testDeleteEmployee_ManagerWithSubordinates() {
        EmployeeManager employee = new EmployeeManager();
        employee.setId(1);
        employee.setName("John Manager");

        when(employeeManagerRepo.findById(1)).thenReturn(employee);
        when(employeeManagerRepo.findByManagerId(1)).thenReturn(Arrays.asList(new EmployeeManager()));

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            employeeService.deleteEmployee(1);
        });

        assertEquals("Cannot delete Employee with ID 1 as they are a manager with subordinates.", exception.getMessage());
    }
}

