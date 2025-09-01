package com.sky.service;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public Employee getByUsername(String username) {
        return null;
    }

    @Override
    public Employee login(EmployeeLoginDTO employeeLoginDto) {
        String username=employeeLoginDto.getUsername();
        String password=employeeLoginDto.getPassword();

        //1.根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2.处理各种异常情况(用户名不存在,密码不对，账号被锁定)
        if(employee==null){
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        //密码比对
        //TODO后期需要进行md5加密，然后再进行比对
        //对用户输入的明文密码加密
        password= DigestUtils.md5DigestAsHex(password.getBytes());

        if(!password.equals(employee.getPassword())){
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if(employee.getStatus()==StatusConstant.DISABLE){
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //返回实体对象
        return employee;
    }

}
