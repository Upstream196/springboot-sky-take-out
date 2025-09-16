package com.sky.service;

import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.mapper.EmployeeMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;

import java.time.LocalDateTime;
import java.util.List;

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


    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //拷贝属性值->即将请求对象接收的数据传递给实体类对象
        BeanUtils.copyProperties(employeeDTO,employee);
        //为新员工设置设置状态
        employee.setStatus(StatusConstant.ENABLE);
        //为新员工设置默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        //属性创建的时间与修改的时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        //属性创建的操作者与修改者
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.insertEmployee(employee);
    }


}
