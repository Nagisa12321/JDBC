package com.jtchen.statements;

import com.jtchen.connection.ConnectionTest;
import com.jtchen.deprecated.JDBCUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 使用PreparedStatements来替换Statements, 实现数据的增删查改操作
 * <p>
 * 增删改; 查
 * 根据是否需要结果集来分类
 *
 * @author jtchen
 * @version 1.0
 * @date 2021/1/30 15:25
 */
public class PreparedStatementUpdateTest/*Statements: 信使*/ {
    private static final Logger logger = Logger.getLogger(PreparedStatementUpdateTest.class);

    /**
     * 有效防止sql注入
     */
    @Test
    public void testUpdate1() {
        // update("DELETE FROM customers WHERE id = ?", 12);

        String sql = "update `order` set order_name = ? where order_id = ?";
        update(sql, "haha", 2);
    }

    // 通用增删改操作
    public int update(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            // 1. 获取数据库链接
            conn = JDBCUtils.getConnection();
            // 2. 预编译SQL语句, 返回prepareStatement实例
            ps = conn.prepareStatement(sql);
            // 3. 填充占位符
            for (int i = 0; i < args.length; i++)
                ps.setObject(i + 1, args[i]);
            // 4.执行
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {// 5. 关闭资源
            JDBCUtils.closeResource(conn, ps);
        }
    }

    // 修改customer表的一条记录
    @Test
    public void testUpdate() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            // 1. 获取数据库链接
            conn = JDBCUtils.getConnection();
            // 2. 预编译sql语句, 返回PreparedStatement实例
            String sql = "UPDATE customers SET name = ? where id = ?";
            ps = conn.prepareStatement(sql);
            // 3. 填充占位符
            ps.setString(1, "JAY");
            ps.setInt(2, 10);
            // 4. 执行
            if (ps.executeUpdate() != 0) {
                System.out.println("success");
            } else System.out.println("failed");
        } catch (Exception e) {

        } finally {
            // 5. 资源的关闭
            JDBCUtils.closeResource(conn, ps);
        }
    }


    // 向customers表中添加一条记录
    @Test
    public void testInsert() throws SQLException, IOException, ClassNotFoundException {
        // 1. 2. 3. 获取链接
        var conn = ConnectionTest.getConnection();

        // 4. 预编译sql语句, 返回PrepareStatements实例
        String sql = "insert into user_table(user, password, balance) values (?, ?, ?);";
        PreparedStatement ps = conn.prepareStatement(sql);
        // 5. 填充占位符
        ps.setString(1, "jtchen2");
        ps.setString(2, "807916");
        ps.setInt(3, 502222);
        // 6.执行sql语句
        if (ps.executeUpdate() != 0) {
            System.out.println("success");
        } else {
            System.err.println("error");
        }

        // 7.关闭资源
        conn.close();
        ps.close();
    }
}
