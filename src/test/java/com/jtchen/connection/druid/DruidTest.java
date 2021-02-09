package com.jtchen.connection.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Properties;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/2/9 11:22
 */
public class DruidTest {

    // 方式一
    @Test
    public void testGetConnection() throws Exception {
        DruidDataSource source = new DruidDataSource();

        // 四个基本信息
        source.setDriverClassName("com.mysql.cj.jdbc.Driver");
        source.setUrl("jdbc:mysql://localhost:3306/test?serverTimezone=UTC&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true");
        source.setUsername("root");
        source.setPassword("1216414009");

        // 其他设置
        source.setInitialSize(10);
        source.setMaxActive(20);
        // ...

        Connection conn = source.getConnection();
        System.out.println(conn);
    }

    // 方式二 : 通过配置文件加载
    @Test
    public void testGetConnection1() throws Exception {
        // 加载配置文件
        FileInputStream fis = new FileInputStream("src/test/resources/druid.properties");
        Properties properties = new Properties();
        properties.load(fis);

        DataSource source = DruidDataSourceFactory.createDataSource(properties);

        // 获得链接
        Connection connection = source.getConnection();
        System.out.println(connection);
    }
}
