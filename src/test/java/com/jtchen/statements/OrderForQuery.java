package com.jtchen.statements;

import com.jtchen.beans.Order;
import com.jtchen.deprecated.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/2/1 23:22
 */
public class OrderForQuery {
    @Test
    public void testQuery1() {
        Order order = queryForOrders("SELECT order_id AS orderId, order_name AS orderName, order_date AS orderDate FROM `order` where order_id = ?;", 4);
        System.out.println(order);
    }

    public Order queryForOrders(String sql, Object... args) {
        Connection conn = null;
        Order order = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++)
                ps.setObject(i + 1, args[i]);

            rs = ps.executeQuery();
            ResultSetMetaData md = rs.getMetaData();

            if (rs.next()) {
                order = new Order();
                int columnCount = md.getColumnCount();
                for (int i = 0; i < columnCount; i++) {
                    Object columnValue = rs.getObject(i + 1);
                    // 针对于表中的字段名和属性名不相同的情况
                    // 1. 必须生命sql时, 用类的属性名来命名字段的别名
                    // 2. 使用ResultSetMetaData时, 使用getColumnLabel来替换getColumnName


                    // String columnName = md.getColumnName(i + 1);
                    String columnLabel = md.getColumnLabel(i + 1);

                    Field field = Order.class.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(order, columnValue);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps, rs);
        }
        return order;
    }
}
