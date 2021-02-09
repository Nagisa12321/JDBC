package com.jtchen.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/2/7 23:10
 */
public class JDBCUtils {
    // 数据库连接池只需提供一个即可
    private static final DataSource c3p0source;
    private static DataSource dbcpSource;
    private static DataSource druidSource;
    private static final Logger logger;
    static {
        /* logger */
        logger = Logger.getLogger(JDBCUtils.class);
        /* c3p0 */
        c3p0source = new ComboPooledDataSource("helloc3p0");
        /* dbcp */
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/dbcp.properties");
            Properties properties = new Properties();
            properties.load(fis);
            dbcpSource = BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            logger.error(e);
        }
        /* druid */
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/druid.properties");
            Properties properties = new Properties();
            properties.load(fis);
            druidSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            logger.error(e);
        }

    }

    /**
     * 使用C3P0 数据库连接池技术
     *
     * @return 数据库链接
     * @throws SQLException -
     */
    public static Connection getConnection() throws SQLException {
        return c3p0source.getConnection();
    }

    public static Connection getConnection1() throws SQLException {
        return dbcpSource.getConnection();
    }

    public static Connection getConnection2() throws SQLException {
        return dbcpSource.getConnection();
    }

    public static void closeResource(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 资源的关闭
     *
     * @param conn 数据库链接
     * @param ps   预编译Statement
     */
    public static void closeResource(Connection conn, Statement ps) {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
            if (ps != null && !ps.isClosed()) ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void closeResource(Connection conn, Statement ps, ResultSet set) {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
            if (ps != null && !ps.isClosed()) ps.close();
            if (set != null && !set.isClosed()) set.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
