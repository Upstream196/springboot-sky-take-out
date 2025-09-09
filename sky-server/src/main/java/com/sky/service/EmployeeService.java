package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;

import java.util.List;

public interface EmployeeService {
    //根据用户名查询员工
    Employee getByUsername(String username);
    //员工登录
    Employee login(EmployeeLoginDTO employeeLoginDto);
    //员工批量添加
    //int insertEmployee(List<Employee> employee);

    //员工添加
    void save(EmployeeDTO employeeDTO);
}
