package com.sky.config;

import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.interceptor.JwtTokenUserInterceptor;
import com.sky.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.print.Doc;
import java.util.List;

@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;
    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;


   protected void addInterceptors(InterceptorRegistry registry){
       log.info("开始注册自定义拦截器...");
       //注册管理员自定义拦截器
       registry.addInterceptor(jwtTokenAdminInterceptor)
               //定义要拦截的URL路径
               .addPathPatterns("/admin/**")
               //定义需要从拦截规则排除的URL路径
               .excludePathPatterns("/admin/employee/login");

       //注册用户自定义拦截器
       registry.addInterceptor(jwtTokenUserInterceptor)
               .addPathPatterns("/user/**")
               .excludePathPatterns("/user/user/login")
               .excludePathPatterns("/user/user/status");
   }


//    protected void addInterceptor(InterceptorRegistry registry) {
//       log.info("开始注册自定义拦截器...");
//
//
//    }

    //通过knife4j生成接口文档
    @Bean
    public Docket docket1(){
        ApiInfo apiInfo=new ApiInfoBuilder()
                .title("苍穹外卖项目接口文档")
                .version("2.0")
                .description("苍穹外卖项目接口文档")
                .build();
        Docket docket=new Docket(DocumentationType.SWAGGER_2)
                .groupName("管理端接口")
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sky.controller.admin"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    @Bean
    public Docket docket2(){
       log.info("准备生成接口文档...");
       ApiInfo apiInfo = new ApiInfoBuilder()
               .title("苍穹外卖项目接口文档")
               .version("2.0")
               .description("苍穹外卖项目接口文档")
               .build();

        Docket docket =new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户端接口")
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sky.controller.user"))
                .paths(PathSelectors.any())
                .build();

        return docket;
    }

    //设置静态资源映射
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 扩展 Spring MVC 框架的消息转换器
     */
    @Override // 注意是 @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");

        // 1. 创建一个消息转换器对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        // 2. 为消息转换器设置 JacksonObjectMapper (它已配置好时间格式)
        converter.setObjectMapper(new JacksonObjectMapper());

        // 3. 将我们自己的消息转换器加入到容器中，并设置索引为 0 (排在第一位，优先使用)
        converters.add(0, converter);
    }

}
