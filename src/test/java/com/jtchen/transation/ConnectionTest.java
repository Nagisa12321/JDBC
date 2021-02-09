package com.jtchen.transation;

import com.jtchen.deprecated.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/2/5 0:34
 */
public class ConnectionTest {
    @Test
    public void testGetConnection() throws Exception{
        Connection conn = JDBCUtils.getConnection();
        System.out.println(conn);
    }
}
