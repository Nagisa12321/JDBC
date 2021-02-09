package com.jtchen.connection;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/1/29 22:00
 */
public class ConnectionTest {

    // 方便后续学习
    public static Connection getConnection() throws IOException, ClassNotFoundException, SQLException {
        // 1.读取配置文件中的四个基本信息
        InputStream is = ConnectionTest.class.getClassLoader().getResourceAsStream("jdbc.properties");

        Properties pros = new Properties();
        pros.load(is);

        String user = pros.getProperty("user");
        String pwd = pros.getProperty("password");
        String url = pros.getProperty("url");
        String driverClass = pros.getProperty("driverClass");

        // 2. 加载驱动
        Class.forName(driverClass);

        // 3. 获取链接
        return DriverManager.getConnection(url, user, pwd);
    }

    // 方式一
    @Test
    public void testConnection1() throws SQLException {
        // 获取Driver实现类对象
        Driver driver = new com.mysql.cj.jdbc.Driver();

        // jdbc:mysql: 协议
        // localhost: IP
        // 3306: 端口号
        // test: test数据库
        String url = "jdbc:mysql://localhost:3306/test?" +
                "serverTimezone=UTC&allowPublicKeyRetrieval=true";
        // 将用户名和密码封装在Properties中
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("password", "807916");
        // 获取链接
        Connection conn = driver.connect(url, info);

        System.out.println(conn);
    }

    // 方式二: 对于方式一的迭代, 在如下的程序中不出现第三方api使得程序具有更高的可移植性
    @Test
    public void testConnection2() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        // 1. 获取Java实现类对象, 采用反射实现
        Class<?> clazz = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();

        // 2. 提供要链接的数据库
        String url = "jdbc:mysql://localhost:3306/test?" +
                "serverTimezone=UTC&allowPublicKeyRetrieval=true";

        // 3. 提供链接需要的用户名和密码
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("password", "807916");

        // 4. 获取链接
        Connection conn = driver.connect(url, info);
        System.out.println(conn);
    }

    // 方式三: 采用DriverManager替换Driver
    @Test
    public void testConnection3() throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        // 1. 获取Driver实现类对象
        Class<?> clazz = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();

        // 2. 提供另外三个链接的基本信息
        String url = "jdbc:mysql://localhost:3306/test?" +
                "serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String pwd = "807916";

        // 3. 注册驱动
        DriverManager.registerDriver(driver);

        // 4. 获取链接
        Connection conn = DriverManager.getConnection(url, user, pwd);
        System.out.println(conn);
    }

    // 方式四: 在方式3基础上做优化, 可以只是加载驱动不用显式注册驱动
    @Test
    public void testConnection4() throws ClassNotFoundException, InstantiationException, SQLException {
        // 1. 提供三个链接的基本信息
        String url = "jdbc:mysql://localhost:3306/test?" +
                "serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String pwd = "807916";

        // 2. 加载Driver
        // 为什么不用手动加载类? Driver类中存在静态代码块

        /*
            static {
                try {
                    java.sql.DriverManager.registerDriver(new Driver());
                } catch (SQLException E) {
                    throw new RuntimeException("Can't register driver!");
                }
            }
        */

        // 相较于方式三可以省略如下操作
        /*
            Class<?> clazz = Class.forName("com.mysql.cj.jdbc.Driver");
            Driver driver = (Driver) clazz.newInstance();
            DriverManager.registerDriver(driver);
        */

        // 代之的是
        Class.forName("com.mysql.cj.jdbc.Driver");

        // 3. 获取链接
        Connection conn = DriverManager.getConnection(url, user, pwd);
        System.out.println(conn);
    }

    // 方式五: 将数据库链接需要的四个基本信息放在配置文件中, 通过读取配置文件的方式, 获取链接
    /*
     * 此种方式的好处:
     *      实现数据和代码的分离, 实现了解耦
     */
    @Test
    public void testConnection5() throws IOException, ClassNotFoundException, SQLException {

        // 1.读取配置文件中的四个基本信息
        InputStream is = ConnectionTest.class.getClassLoader().getResourceAsStream("jdbc.properties");

        Properties pros = new Properties();
        pros.load(is);

        String user = pros.getProperty("user");
        String pwd = pros.getProperty("password");
        String url = pros.getProperty("url");
        String driverClass = pros.getProperty("driverClass");

        // 2. 加载驱动
        Class.forName(driverClass);

        // 3. 获取链接
        Connection conn = DriverManager.getConnection(url, user, pwd);
        System.out.println(conn);

    }
}
