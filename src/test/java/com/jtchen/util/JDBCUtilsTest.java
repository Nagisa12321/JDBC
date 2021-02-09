package com.jtchen.util;

import org.junit.Test;

import java.sql.Connection;

import static org.junit.Assert.*;

public class JDBCUtilsTest {

    @Test
    public void getConnection1() throws Exception {
        Connection conn = JDBCUtils.getConnection1();

        System.out.println(conn);
    }
}