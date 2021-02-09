package com.jtchen.statements;

import com.jtchen.beans.Customer;
import com.jtchen.beans.Order;
import com.jtchen.deprecated.JDBCUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用PreparedStatement来实现针对于不同表的通用查询操作
 *
 * @author jtchen
 * @version 1.0
 * @date 2021/2/2 14:30
 */
public class PreparedStatementQueryTest {
    private static final Logger logger = Logger.getLogger(PreparedStatementQueryTest.class);

    @Test
    public void getInstanceTest() {
        Customer customer1 = getInstance(Customer.class, "SELECT * FROM customers WHERE id = ?;", 18);
        Customer customer2 = getInstance(Customer.class, "SELECT * FROM customers WHERE name = ?;", "成龙");

        System.out.println(customer1);
        System.out.println(customer2);
    }

    @Test
    public void getListTest() {
        List<Order> list = getForList(Order.class, "select order_name AS orderName, Order_id as orderId, order_date as orderDate from `order`");
        list.forEach(System.out::println);
    }

    /**
     * 针对不同表的通用查询操作, 返回一条记录
     *
     * @param clazz 类
     * @param sql   sql语句
     * @param args  ?填充
     * @param <T>   类
     * @return 对象
     */
    public <T> T getInstance(Class<T> clazz, String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        T res = null;
        try {
            // 获取链接
            conn = JDBCUtils.getConnection();

            // 获取PreparedStatement实例
            ps = conn.prepareStatement(sql);

            // 填充占位符
            for (int i = 0; i < args.length; i++)
                ps.setObject(i + 1, args[i]);

            // 获取结果集
            rs = ps.executeQuery();

            // 结果集填入对象中
            if (rs.next()) {
                res = clazz.getDeclaredConstructor().newInstance();
                ResultSetMetaData md = rs.getMetaData();
                int columnCount = md.getColumnCount();

                for (int i = 0; i < columnCount; i++) {
                    Object columnValue = rs.getObject(i + 1);
                    String columnLabel = md.getColumnLabel(i + 1);

                    // 填入对象
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(res, columnValue);
                }
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            JDBCUtils.closeResource(conn, ps, rs);
        }
        return res;
    }

    /**
     * 返回多个对象构成的集合
     */
    public <T> List<T> getForList(Class<T> clazz, String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<T> list = new ArrayList<>();
        try {
            conn = JDBCUtils.getConnection();

            ps = conn.prepareStatement(sql);

            for (int i = 0; i < args.length; i++)
                ps.setObject(i + 1, args[i]);

            rs = ps.executeQuery();

            while (rs.next()) {
                T t = clazz.getDeclaredConstructor().newInstance();
                ResultSetMetaData md = rs.getMetaData();
                int columnCount = md.getColumnCount();

                for (int i = 0; i < columnCount; i++) {
                    Object columnValue = rs.getObject(i + 1);
                    String columnLabel = md.getColumnLabel(i + 1);

                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t, columnValue);
                }
                list.add(t);
            }
        } catch (Exception e) {
            logger.error(e);
            return null;
        } finally {
            JDBCUtils.closeResource(conn, ps, rs);
        }
        return list;
    }
}
