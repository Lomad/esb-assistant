package com.winning.esb.model.db;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Date;

/**
 * Created by xuehao on 2017/8/3.
 */
public class DataSourceModel {

    private String id;
    private String dbType;
    private String ip;
    private String port;
    private String dbName;
    private String username;
    private String password;
    private Integer initialSize;
    private Integer minIdle;
    private Integer maxActive;
    private String driverClassName;
    private String url;
    private DruidDataSource druidDataSource;
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    /**
     * 创建时间
     */
    private Date ctime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getInitialSize() {
        if (initialSize == null) {
            initialSize = 1;
        }
        return initialSize;
    }

    public void setInitialSize(Integer initialSize) {
        this.initialSize = initialSize;
    }

    public Integer getMinIdle() {
        if (minIdle == null) {
            minIdle = 1;
        }
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public Integer getMaxActive() {
        if (maxActive == null) {
            maxActive = 50;
        }
        return maxActive;
    }

    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DruidDataSource getDruidDataSource() {
        return druidDataSource;
    }

    public void setDruidDataSource(DruidDataSource druidDataSource) {
        this.druidDataSource = druidDataSource;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }

    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
}