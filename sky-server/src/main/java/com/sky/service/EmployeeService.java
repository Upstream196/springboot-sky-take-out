package com.sky.service;

import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;

public interface EmployeeService {
    //根据用户名查询员工
    Employee getByUsername(String username);
    //员工登录
    Employee login(EmployeeLoginDTO employeeLoginDto);
}
