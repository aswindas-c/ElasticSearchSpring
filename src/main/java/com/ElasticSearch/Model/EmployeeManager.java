package com.ElasticSearch.Model;

import java.time.OffsetDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(indexName = "employeeye")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeManager {
    
    @Id
    private Integer id;

    private String name;

    private String designation;

    private String email;

    private String department;

    private String mobile;

    private String location;

    private Integer managerId;

    
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private OffsetDateTime dateOfJoining;

    
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private OffsetDateTime createdTime;
    
   
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private OffsetDateTime updatedTime;

    private Integer yearsOfExperience;

}
