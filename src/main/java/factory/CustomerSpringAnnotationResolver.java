package com.factory;

import com.dao.OperationLogMapper;
import com.entity.OperationLog;
import com.service.OperationLogService;
import com.service.impl.OperationLogServiceImpl;
import com.utils.SpringConfigTool;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.aop.MethodInvocation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.spring.aop.SpringAnnotationResolver;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wbzhongsy on 2018/8/27.
 */
public class CustomerSpringAnnotationResolver extends SpringAnnotationResolver{
    public CustomerSpringAnnotationResolver(){

    }
    @Override
    public Annotation getAnnotation(MethodInvocation mi, Class<? extends Annotation> clazz) {
        Method m = mi.getMethod();

        Annotation a = AnnotationUtils.findAnnotation(m, clazz);
        if(a != null) {
            return a;
        } else {
            Class<?> targetClass = mi.getThis().getClass();
            m = ClassUtils.getMostSpecificMethod(m, targetClass);
            a = AnnotationUtils.findAnnotation(m, clazz);
            return a != null?a:AnnotationUtils.findAnnotation(mi.getThis().getClass(), clazz);
        }
    }
}
