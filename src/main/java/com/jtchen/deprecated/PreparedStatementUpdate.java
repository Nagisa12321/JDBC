package com.jtchen.deprecated;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/2/3 21:21
 */
public class PreparedStatementUpdate {
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

    // 通用增删改操作--- version 2.0 考虑上事务
    public int update(Connection conn, String sql, Object... args) {
        PreparedStatement ps = null;
        try {
            // 1. 预编译SQL语句, 返回prepareStatement实例
            ps = conn.prepareStatement(sql);
            // 2. 填充占位符
            for (int i = 0; i < args.length; i++)
                ps.setObject(i + 1, args[i]);
            // 3.执行
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            // 4. 关闭资源
            JDBCUtils.closeResource(null, ps);
        }
    }
}
