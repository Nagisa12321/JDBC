package com.jtchen.deprecated;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 操作数据库的工具类
 *
 * @author jtchen
 * @version 1.0
 * @date 2021/1/30 16:27
 */
public class JDBCUtils {
    private static final Logger logger = Logger.getLogger(JDBCUtils.class);
    private static Connection connection = null;

    /**
     * 获取数据库链接
     *
     * @return 数据库链接
     * @throws IOException            -
     * @throws ClassNotFoundException -
     * @throws SQLException           -
     */
    public static Connection getConnection() throws IOException, ClassNotFoundException, SQLException {
        // 需要重新获得链接与两个情况
        // 1. null
        // 2. is closed
        if (connection == null || connection.isClosed()) {
            logger.debug("connection is null or closed, new get.");
            // 1.读取配置文件中的四个基本信息
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");

            Properties pros = new Properties();
            pros.load(is);

            String user = pros.getProperty("user");
            String pwd = pros.getProperty("password");
            String url = pros.getProperty("url");
            String driverClass = pros.getProperty("driverClass");

            // 2. 加载驱动
            Class.forName(driverClass);

            // 3. 获取链接
            connection = DriverManager.getConnection(url, user, pwd);
        } else {
            logger.debug("connection is exist and not closed, new return.");
        }

        return connection;
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
