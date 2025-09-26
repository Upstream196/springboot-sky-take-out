package com.sky.controller.adimin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/employee")
@Slf4j//lombok的输入日志注解，该信息注解更完善，包含类的完善名，日志打印的时间
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;
    /**
     * 登录
     * @return
     * @Param employeeLoginDTO
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO, HttpSession session) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);
        //将登录数据员工id存储到session会话中
        //session.setAttribute("employee",employee.getId());

        //生成令牌
        Map<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID,employee.getId());//将员工的id写入载荷
        String token= JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);
        log.info("生成的token payload：{}",claims);
        log.info("完整的token：{}",token);
        //由于EmployeeLoginVO 使用@Builder注解就可以使用链式构建对象
        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .username(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }
    /**
     * 退出
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(){
        return Result.success();
    }

    @PostMapping
    @ApiOperation("新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新员工 + {}"+employeeDTO);//->将前端接收的员工数据打印出来->前端发送数据存储在哪里？->请求类中，因此方法需要接收请求类
        employeeService.save(employeeDTO);//->为什么使用employeeService对象调用save方法？
                                         // ->将前端的请求交给service层的接口，通过接口来创建具体的实现类完成新建员工该进行的操作
        return Result.success();
    }







}
