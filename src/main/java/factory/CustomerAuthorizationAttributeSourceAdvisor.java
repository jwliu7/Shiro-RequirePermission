package com.factory;

import com.service.OperationLogService;
import org.apache.shiro.authz.annotation.*;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;

/**
 * Created by wbzhongsy on 2018/8/27.
 */
public class CustomerAuthorizationAttributeSourceAdvisor extends AuthorizationAttributeSourceAdvisor{
    private static final Class<? extends Annotation>[] AUTHZ_ANNOTATION_CLASSES = new Class[]{RequiresPermissions.class, RequiresRoles.class, RequiresUser.class, RequiresGuest.class, RequiresAuthentication.class};
    private static final Logger log = LoggerFactory.getLogger(AuthorizationAttributeSourceAdvisor.class);
    @Resource
    public  OperationLogService operationLogService;
    public CustomerAuthorizationAttributeSourceAdvisor() {
//        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/spring-shiro.xml","classpath:/spring-mvc.xml");
//        //例子：获取dicDao实例
//       OperationLog o = ctx.getBean(OperationLog.class);
        this.setAdvice(new CustomerForAopAllianceAnnotationsAuthorizingMethodInterceptor());
    }



}
