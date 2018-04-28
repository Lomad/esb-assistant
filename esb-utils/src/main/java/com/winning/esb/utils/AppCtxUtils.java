package com.winning.esb.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;

/**
 * 获取Spring上下文，其中添加“@Component”标签，可使该类自动被扫描
 */
public class AppCtxUtils implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(AppCtxUtils.class);

    private static ApplicationContext appCtx;
    //private static ServletContext servCtx; // Web应用上下文环境
    private static JdbcTemplate jt;

    /**
     * 此方法可以把ApplicationContext对象inject到当前类中作为一个静态成员变量。
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appCtx = applicationContext;
    }

    /**
     * 获取ApplicationContext
     */
    public static ApplicationContext getAppCtx() {
        return appCtx;
    }

    /**
     * 这是一个便利的方法，快速得到一个BEAN
     */
    public static <T> T getBean(String beanName) {
        return (T) appCtx.getBean(beanName);
    }

    /**
     * 这是一个便利的方法，快速得到一个BEAN，附加上数据库类型
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return appCtx.getBeansOfType(clazz);
    }

//    /**
//     * 这是一个很便利的方法，帮助我们快速得到一个BEAN
//     *
//     * @param beanName bean的名字
//     * @return 返回一个bean对象
//     */
//    @SuppressWarnings("unchecked")
//    public static <T> T getBean(String beanName, Class<T> clazz) {
//        return (T) appCtx.getBean(beanName);
//    }


    public static JdbcTemplate getJt() {
        return jt;
    }

    public void setJt(JdbcTemplate jt) {
        AppCtxUtils.jt = jt;
    }

    public static NamedParameterJdbcTemplate getNpjt() {
        return new NamedParameterJdbcTemplate(getJt().getDataSource());
    }

    /*public static ServletContext getServletContext() {
        return servCtx;
    }*/

}