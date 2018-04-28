package com.winning.monitor.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.winning.monitor.data.api.largerScreen.entity.DataSourceVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;

/**
 * Created by xuehao on 2017/8/3.
 */
public class DataSourceUtils {
    /**
     * 数据库驱动名称
     *
     * @author xuehao
     */
    public enum DataBaseEnum {
        /**
         * url示例：jdbc:sqlserver://{IP}:{PORT};database={数据库名称}
         */
        SqlServer("SqlServer", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://%s%s;DatabaseName=%s"),
//        /**
//         * url示例：jdbc:mysql://{IP}:{PORT}/{数据库名称}
//         */
//        Mysql("Mysql", "com.mysql.jdbc.Driver", "jdbc:mysql://%s%s/%s"),
        /**
         * url示例：jdbc:oracle:thin:@{IP}:{PORT}:{SID}
         */
        Oracle("Oracle", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@%s%s:%s");

        private String code;
        private String clazz;
        private String urlTemplate;

        private DataBaseEnum(String code, String clazz, String urlTemplate) {
            this.code = code;
            this.clazz = clazz;
            this.urlTemplate = urlTemplate;
        }

        public String getCode() {
            return code;
        }

        public String getClazz() {
            return clazz;
        }

        public String getUrlTemplate() {
            return urlTemplate;
        }
    }

    /**
     * 创建Jdbctemplate
     */
    public static DataSourceVO createDataSourceVO(String conn) {
        if (StringUtils.isEmpty(conn)) {
            return null;
        }

        String[] connArray = conn.split(",");
        DataSourceVO model;
        if (connArray != null && connArray.length == 6) {
            model = new DataSourceVO();
            model.setDbType(connArray[0]);
            model.setIp(connArray[1]);
            model.setPort(connArray[2]);
            model.setDbName(connArray[3]);
            model.setUsername(connArray[4]);
            model.setPassword(connArray[5]);
            model.setCtime(new Date());
            model.setDruidDataSource(createDruidDS(model));
            model.setJdbcTemplate(new JdbcTemplate(model.getDruidDataSource()));
        } else {
            model = null;
        }

        return model;
    }

    /**
     * 创建Druid连接池
     */
    public static DruidDataSource createDruidDS(DataSourceVO model) {
        //设置数据源
        DruidDataSource ds = new DruidDataSource();
        DataBaseEnum dbe;
        String dbType = model.getDbType().toUpperCase();
        if (DataBaseEnum.Oracle.toString().toUpperCase().equals(dbType)) {
            dbe = DataBaseEnum.Oracle;
        } else {
            dbe = DataBaseEnum.SqlServer;
        }
        ds.setDriverClassName(dbe.getClazz());
        //设置连接
        String port = model.getPort();
        if (!StringUtils.isEmpty(port)) {
            port = ":" + port;
        }
        model.setUrl(String.format(dbe.getUrlTemplate(), model.getIp(), port, model.getDbName()));
        ds.setUrl(model.getUrl());
        ds.setUsername(model.getUsername());
        ds.setPassword(model.getPassword());
        ds.setInitialSize(model.getInitialSize());
        ds.setMinIdle(model.getMinIdle());
        ds.setMaxActive(model.getMaxActive());
        ds.setMaxWait(60000);
        model.setDruidDataSource(ds);
        return ds;
    }

    /**
     * 测试数据库连接
     */
    public static String testConnectDB(DataSourceVO model) {
        String errinfo = null;
        Connection connection = null;
        try {
            //通过普通方式判断连接是否正确
            DruidDataSource ds = createDruidDS(model);
            //检测数据库驱动类是否可以使用
            Class.forName(ds.getDriverClassName());
            //测试数据库是否可以连接
            connection = DriverManager.getConnection(model.getUrl(),
                    model.getUsername(), model.getPassword());
            if (connection == null) {
                errinfo = "配置错误：数据库连接失败！";
            }

            //二次验证：通过各自数据库脚本测试数据库连接是否正确
            String dbType = model.getDbType().toUpperCase();
            if (DataBaseEnum.Oracle.toString().equals(dbType)) {
                testOracle(model);
            } else {
                testMssql(model);
            }
        } catch (Exception ex) {
            errinfo = "配置错误：数据库连接异常！" + ex.getMessage();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    errinfo = "异常错误：关闭数据库连接发生异常错误！" + ex.getMessage();
                }
            }
        }
        return errinfo;
    }

    /**
     * 测试Mssql数据库是否可用（通过执行一段SQL判断，如果执行没有异常，说明数据库连接正确）
     */
    public static void testMssql(DataSourceVO model) {
        //设置数据源
        DruidDataSource ds = createDruidDS(model);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        jdbcTemplate.execute("SELECT 1 AS testCol");
    }

    /**
     * 测试Oracle数据库是否可用（通过执行一段SQL判断，如果执行没有异常，说明数据库连接正确）
     */
    public static void testOracle(DataSourceVO model) {
        //设置数据源
        DruidDataSource ds = createDruidDS(model);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        jdbcTemplate.execute("SELECT 1 AS testCol FROM DUAL");
    }

    /**
     * 测试Mysql数据库是否可用（通过执行一段SQL判断，如果执行没有异常，说明数据库连接正确）
     */
    public static void testMysql(DataSourceVO model) {
        //设置数据源
        DruidDataSource ds = createDruidDS(model);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        jdbcTemplate.execute("SELECT 1 AS testCol");
    }

    /**
     * 将“mssql”转为“SqlServer”
     */
    public static String transferToSqlserver(String dbType) {
        if (dbType != null && "mssql".equals(dbType.replace(" ", "").toLowerCase())) {
            return DataBaseEnum.SqlServer.getCode();
        }
        return dbType;
    }
}