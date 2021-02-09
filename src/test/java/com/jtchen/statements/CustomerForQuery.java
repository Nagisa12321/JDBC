package com.jtchen.statements;

import com.jtchen.beans.Customer;
import com.jtchen.deprecated.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * 针对于Customer表的查询操作
 *
 * @author jtchen
 * @version 1.0
 * @date 2021/2/1 15:08
 */
public class CustomerForQuery {

    @Test
    public void testQuery3() {
        Customer customer1 = (Customer) query(Customer.class, "SELECT * FROM customers WHERE id = ?;", 18);
        Customer customer2 = (Customer) query(Customer.class, "SELECT * FROM customers WHERE name = ?;", "成龙");

        System.out.println(customer1);
        System.out.println(customer2);
    }

    public Object query(Class<?> clazz, String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Object o = null;

        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);

            for (int i = 0; i < args.length; i++)
                ps.setObject(i + 1, args[i]);

            rs = ps.executeQuery();

            ResultSetMetaData md = rs.getMetaData();
            if (rs.next()) {
                o = clazz.getDeclaredConstructor().newInstance();
                int columnCount = md.getColumnCount();

                for (int i = 0; i < columnCount; i++) {
                    Object columnValue = rs.getObject(i + 1);
                    String columnName = md.getColumnName(i + 1);

                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(o, columnValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps, rs);
        }
        return o;
    }

    @Test
    public void testQuery2() {
        Customer customer1 = queryForCustomers("SELECT * FROM customers WHERE id = ?;", 18);
        Customer customer2 = queryForCustomers("SELECT * FROM customers WHERE name = ?;", "成龙");

        System.out.println(customer1);
        System.out.println(customer2);
    }

    /**
     * 针对于Customer表的通用查询操作
     *
     * @param sql  sql语句
     * @param args 占位符填充项
     */
    public Customer queryForCustomers(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet set = null;
        Customer customer = null;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);

            for (int i = 0; i < args.length; i++)
                ps.setObject(i + 1, args[i]);

            set = ps.executeQuery();

            // 获取结果集的元数据
            ResultSetMetaData md = set.getMetaData();
            // 通过ResultSetMetaData获取结果集的列数
            int columnCount = md.getColumnCount();

            if (set.next()) {
                customer = new Customer();
                // 处理结果集一行数据中的每一个列
                for (int i = 0; i < columnCount; i++) {
                    Object columnValue = set.getObject(i + 1);

                    // 获取每个列的列名
                    String columnName = md.getColumnName(i + 1);

                    // 给customer对象指定的columnName属性, 赋值为columnValue
                    Field field = Customer.class.getDeclaredField(columnName);
                    field.setAccessible(true);// 可访问私有数据
                    field.set(customer, columnValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps, set);
        }

        return customer;
    }

    @Test
    public void testQuery1() throws Exception {
        Connection conn = JDBCUtils.getConnection();
        String sql = "select * from customers where id = ?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setObject(1, 18);

        // 执行, 并返回结果集
        ResultSet set = ps.executeQuery();
        // next的作用: 判断结果集的下一条是否有数据?
        // 如果有数据, 返回true, 指针下移
        // 如果没数据, 返回false, 指针不下移
        if (set.next()) {
            // 获取当前这条数据的各个字段
            int id = set.getInt(1);
            String name = set.getString(2);
            String email = set.getString(3);
            Date date = set.getDate(4);

            // 方式一:
            /*
            System.out.println("id = " + id);
            System.out.println("name = " + name);
            System.out.println("email = " + email);
            System.out.println("date = " + date);
             */

            // 方式二:
            /*
            Object[] data = new Object[]{id, name, email, date};
             */

            // 方式三: 采用类存储
            Customer customer = new Customer(id, name, email, date, null);
            System.out.println(customer);
        }

        JDBCUtils.closeResource(conn, ps, set);
    }
}
