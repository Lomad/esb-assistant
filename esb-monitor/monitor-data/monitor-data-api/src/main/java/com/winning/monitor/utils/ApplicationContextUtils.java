package com.winning.monitor.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by xuehao on 2017/8/3.
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {
    private static ApplicationContext appCtx; // Spring应用上下文环境

    /**
     * 此方法可以把ApplicationContext对象inject到当前类中作为一个静态成员变量。
     *
     * @param applicationContext ApplicationContext 对象.
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        appCtx = applicationContext;
    }

    /**
     * 获取ApplicationContext
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return appCtx;
    }

    /**
     * 这是一个很便利的方法，帮助我们快速得到一个BEAN
     *
     * @param beanName bean的名字
     * @return 返回一个bean对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return (T) appCtx.getBean(beanName);
    }

}