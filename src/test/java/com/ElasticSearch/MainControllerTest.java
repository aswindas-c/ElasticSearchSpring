package com.ElasticSearch;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ElasticSearch.Controller.MainController;
import com.ElasticSearch.DTO.ChangeManagerRequest;
import com.ElasticSearch.DTO.ManagerResponse;
import com.ElasticSearch.DTO.Response;
import com.ElasticSearch.Model.EmployeeManager;
import com.ElasticSearch.Service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;


public class MainControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private MainController mainController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mainController).build();
    }

    @Test
    public void testAddEmployee() throws Exception {
        // Register JavaTimeModule to handle LocalDateTime serialization
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    
        EmployeeManager employee = new EmployeeManager();
        employee.setName("John Doe");
        employee.setDesignation("Associate");
        employee.setDepartment("Sales");
        employee.setEmail("john.doe@example.com");
        employee.setMobile("1234567890");
        employee.setDateOfJoining(OffsetDateTime.of(2014, 6, 28, 12, 57, 59, 447000000, ZoneOffset.UTC));
        employee.setManagerId(1);
    
        Response response = new Response("Employee added successfully");
    
        // Mock the service response to return the expected Response object
        when(employeeService.addEmployee(employee)).thenReturn(response);
    
        // Perform the assertions
        mockMvc.perform(post("/addEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee))) 
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee added successfully"));
    }

    @Test
    public void testGetEmployee() throws Exception {
        EmployeeManager employee1 = new EmployeeManager();
        employee1.setId(2);
        employee1.setName("Alice Smith");
        employee1.setDesignation("Developer");
        employee1.setDepartment("IT");
        employee1.setEmail("alice.smith@example.com");
        employee1.setMobile("0987654321");
        employee1.setDateOfJoining(OffsetDateTime.now().minusYears(2));
        employee1.setManagerId(1);

        EmployeeManager employee2 = new EmployeeManager();
        employee2.setId(3);
        employee2.setName("Bob Johnson");
        employee2.setDesignation("Developer");
        employee2.setDepartment("IT");
        employee2.setEmail("bob.johnson@example.com");
        employee2.setMobile("1122334455");
        employee2.setDateOfJoining(OffsetDateTime.now().minusYears(3));
        employee2.setManagerId(1);

        List<EmployeeManager> employeeList = Arrays.asList(employee1, employee2);

        ManagerResponse managerResponse = new ManagerResponse(
                "Manager details with employees",
                "John Doe",
                "IT",
                1,
                employeeList
        );

        when(employeeService.getEmployee(null, null)).thenReturn(Arrays.asList(managerResponse));

        mockMvc.perform(get("/getEmployee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Manager details with employees"))
                .andExpect(jsonPath("$[0].managerName").value("John Doe"))
                .andExpect(jsonPath("$[0].department").value("IT"))
                .andExpect(jsonPath("$[0].managerId").value(1))
                .andExpect(jsonPath("$[0].employeeList[0].name").value("Alice Smith"))
                .andExpect(jsonPath("$[0].employeeList[1].name").value("Bob Johnson"));
    }

    @Test
    public void testDeleteEmployee() throws Exception {
        Response response = new Response("Employee deleted successfully");

        when(employeeService.deleteEmployee(1)).thenReturn(response);

        mockMvc.perform(delete("/deleteEmployee")
                .param("employeeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee deleted successfully"));
    }

    @Test
    public void testChangeEmployeeManager() throws Exception {
        ChangeManagerRequest request = new ChangeManagerRequest(2, 3);
        Response response = new Response("Employee manager changed successfully");

        when(employeeService.changeEmployeeManager(2, 3)).thenReturn(response);

        mockMvc.perform(put("/changeEmployeeManager")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee manager changed successfully"));
    }
}
