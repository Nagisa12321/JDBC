package com.jtchen.connection.c3p0;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;


/**
 * @author jtchen
 * @version 1.0
 * @date 2021/2/7 15:09
 */
public class C3P0Test {

    @Test
    public void testGetConnection() throws Exception {
        // 获取c3p0数据库连接池
        ComboPooledDataSource cpds = new ComboPooledDataSource("helloc3p0");

        Connection conn = cpds.getConnection();
        System.out.println(conn);

    }
}
