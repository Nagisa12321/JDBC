package com.jtchen.connection.dbcp;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Test;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Properties;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/2/8 15:02
 */
public class DBCPTest {
    /**
     * 方式一: 不推荐
     * 测试dbcp的数据库连接池技术
     */
    @Test
    public void testGetConnection() throws Exception {
        // 创建了DBCP的数据库连接池
        BasicDataSource sources = new BasicDataSource();

        // 设置基本信息
        sources.setDriverClassName("com.mysql.cj.jdbc.Driver");
        sources.setUsername("root");
        sources.setPassword("1216414009");
        sources.setUrl("jdbc:mysql://localhost:3306/test?serverTimezone=UTC&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true");

        // 还可以设置其他设计数据库连接池管理的相关属性
        sources.setInitialSize(10);
        // ...

        // 获取链接
        Connection conn = sources.getConnection();
        System.out.println(conn);
    }

    /**
     * 方式二: 使用配置文件
     */
    @Test
    public void testGetConnection1() throws Exception {
        FileInputStream fis = new FileInputStream("src/test/resources/dbcp.properties");
        Properties properties = new Properties();
        properties.load(fis);
        BasicDataSource dataSource = BasicDataSourceFactory.createDataSource(properties);
        Connection conn = dataSource.getConnection();
        System.out.println(conn);
    }
}
