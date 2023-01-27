package com.updateservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequest {

    private int employeeId;
    private String employeeName;
    private String employeeCity;
    private String employeePhone;
    private double javaExperience;
    private double springExperience;

}
