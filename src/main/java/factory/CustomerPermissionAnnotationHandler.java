package com.factory;

import com.dao.OperationLogMapper;
import com.entity.OperationLog;
import com.service.OperationLogService;
import com.utils.SpringConfigTool;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.aop.PermissionAnnotationHandler;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wbzhongsy on 2018/8/29.
 */
public class CustomerPermissionAnnotationHandler extends PermissionAnnotationHandler {
    public CustomerPermissionAnnotationHandler(){
    }
    @Override
    public void assertAuthorized(Annotation a) throws AuthorizationException {
        if(a instanceof RequiresPermissions) {
            RequiresPermissions r=(RequiresPermissions)a;
            int rlength=r.value().length;
            if(r.value()!=null&&rlength>0){
                String perms[]=r.value();
                String insertPerms[]=new String[rlength];
                Subject subject= SecurityUtils.getSubject();
                List<OperationLog> list=new ArrayList<>();
                if(perms.length == 1) {
                    insertPerms=perms;
                } else if(Logical.AND.equals(r.logical())) {

                    String[] var6 = perms;
                    int var7 = perms.length;

                    for(int var8 = 0; var8 < var7; ++var8) {
                        String permission = var6[var8];
                        if(subject.isPermitted(permission)) {
                            insertPerms[var8]=permission;
                        }
                    }
                } else {
                    if(Logical.OR.equals(r.logical())) {

                        String[] var6 = perms;
                        int var7 = perms.length;

                        for(int var8 = 0; var8 < var7; ++var8) {
                            String permission = var6[var8];
                            if(subject.isPermitted(permission)) {
                                insertPerms[var8]=permission;
                            }
                        }

//                        Content = _description,
//                                OperationTime = DateTime.Now,
//                                ActionType = _action,
//                                UserName = SSOHelper.GetCurrentUser.DisplayName,
//                                Source = _controller

                        //添加日志
//                        if(!hasAtLeastOnePermission) {
//                            subject.checkPermission(perms[0]);
//                        }
                    }
                }

                int var9=insertPerms.length;
                for(int var10=0;var10<var9;++var10){
                    String permissionStr=insertPerms[var10];
                    if(permissionStr!=null){
                        String insertStr[]=permissionStr.split(":");
                        OperationLog o=new OperationLog((String)subject.getSession().getAttribute("domainName"),
                                insertStr[1],insertStr[2],"",
                                "",insertStr[0],new Date());
                        list.add(o);
                    }

                }
                DataSourceTransactionManager transactionManager=(DataSourceTransactionManager)SpringConfigTool.getBean("transactionManager");
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();//开启事务
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 事物隔离级别，开启新事务，这样会比较安全些。
                TransactionStatus status = transactionManager.getTransaction(def);
                boolean isSuccess=true;
                try{
                    OperationLogMapper operationLogMapper=(OperationLogMapper) SpringConfigTool.getBean("operationLogMapper");
                    operationLogMapper.insertList(list);
                }catch (Exception e){
                    isSuccess=false;
                    transactionManager.rollback(status);
                    e.printStackTrace();
                }
                if (isSuccess){
                    transactionManager.commit(status);
                }
            }
            RequiresPermissions rpAnnotation = (RequiresPermissions)a;
            String[] perms = this.getAnnotationValue(a);
            Subject subject = this.getSubject();
            if(perms.length == 1) {
                subject.checkPermission(perms[0]);
            } else if(Logical.AND.equals(rpAnnotation.logical())) {
                this.getSubject().checkPermissions(perms);
            } else {
                if(Logical.OR.equals(rpAnnotation.logical())) {
                    boolean hasAtLeastOnePermission = false;
                    String[] var6 = perms;
                    int var7 = perms.length;

                    for(int var8 = 0; var8 < var7; ++var8) {
                        String permission = var6[var8];
                        if(this.getSubject().isPermitted(permission)) {
                            hasAtLeastOnePermission = true;
                        }
                    }

                    if(!hasAtLeastOnePermission) {
                        this.getSubject().checkPermission(perms[0]);
                    }
                }

            }
        }
    }
}
