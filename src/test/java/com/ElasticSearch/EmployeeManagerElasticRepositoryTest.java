package com.ElasticSearch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ElasticSearch.Model.EmployeeManager;
import com.ElasticSearch.Repository.EmployeeManagerElasticRepository;
import com.ElasticSearch.Repository.EmployeeManagerRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class EmployeeManagerElasticRepositoryTest {

    @Mock
    private EmployeeManagerRepository empManRepo;

    @InjectMocks
    private EmployeeManagerElasticRepository employeeManagerSqlRepository;

    private EmployeeManager employeeManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize a sample EmployeeManager object
        employeeManager = new EmployeeManager();
        employeeManager.setId(1);
        employeeManager.setName("John Doe");
        employeeManager.setDesignation("Manager");
        employeeManager.setDepartment("Sales");
        employeeManager.setManagerId(1);
        employeeManager.setYearsOfExperience(5);
    }

    @Test
    public void testFindByManagerIdAndYearsOfExperienceGreaterThanEqual() {
        when(empManRepo.findByManagerIdAndYearsOfExperienceGreaterThanEqual(1, 3))
                .thenReturn(Arrays.asList(employeeManager));

        List<EmployeeManager> result = employeeManagerSqlRepository
                .findByManagerIdAndYearsOfExperienceGreaterThanEqual(1, 3);

        assertEquals(1, result.size());
        assertEquals(employeeManager, result.get(0));
    }

    @Test
    public void testFindByManagerId() {
        when(empManRepo.findByManagerId(1)).thenReturn(Arrays.asList(employeeManager));

        List<EmployeeManager> result = employeeManagerSqlRepository.findByManagerId(1);

        assertEquals(1, result.size());
        assertEquals(employeeManager, result.get(0));
    }

    @Test
    public void testFindByYearsOfExperienceGreaterThanEqual() {
        when(empManRepo.findByYearsOfExperienceGreaterThanEqual(5))
                .thenReturn(Arrays.asList(employeeManager));

        List<EmployeeManager> result = employeeManagerSqlRepository.findByYearsOfExperienceGreaterThanEqual(5);

        assertEquals(1, result.size());
        assertEquals(employeeManager, result.get(0));
    }

    @Test
    public void testFindByDepartment() {
        when(empManRepo.findByDepartment("Sales")).thenReturn(Arrays.asList(employeeManager));

        List<EmployeeManager> result = employeeManagerSqlRepository.findByDepartment("Sales");

        assertEquals(1, result.size());
        assertEquals(employeeManager, result.get(0));
    }

    @Test
    public void testSave() {
        when(empManRepo.save(any(EmployeeManager.class))).thenReturn(employeeManager);

        EmployeeManager savedEmployeeManager = employeeManagerSqlRepository.save(employeeManager);

        assertEquals(employeeManager, savedEmployeeManager);
    }

    @Test
    public void testDelete() {
        // No return type to verify, just ensure the method is called without exceptions
        employeeManagerSqlRepository.delete(employeeManager);
    }

    @Test
    public void testExistsById() {
        when(empManRepo.existsById(1)).thenReturn(true);

        boolean exists = employeeManagerSqlRepository.existsById(1);

        assertTrue(exists);
    }

    @Test
    public void testFindAll() {
        when(empManRepo.findAll()).thenReturn(Arrays.asList(employeeManager));

        List<EmployeeManager> result = employeeManagerSqlRepository.findAll();

        assertEquals(1, result.size());
        assertEquals(employeeManager, result.get(0));
    }

    @Test
    public void testFindById() {
        when(empManRepo.findByIdCustom(1)).thenReturn(employeeManager);

        EmployeeManager result = employeeManagerSqlRepository.findById(1);

        assertEquals(employeeManager, result);
    }
}
