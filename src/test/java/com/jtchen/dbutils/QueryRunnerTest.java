package com.jtchen.dbutils;

import com.jtchen.beans.Customer;
import com.jtchen.beans.Order;
import com.jtchen.util.JDBCUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * comments-dbutils 是 Apache 组织提供的一个开源 JDBC 工具类库
 * 封装了针对于数据库的增删改查操作
 *
 * @author jtchen
 * @version 1.0
 * @date 2021/2/9 14:32
 */
public class QueryRunnerTest {
    private static final Logger logger = Logger.getLogger(QueryRunnerTest.class);

    // 测试插入
    @Test
    public void testInsert() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection2();

            QueryRunner runner = new QueryRunner();

            String sql = "insert customers(name, email, birth)values(?, ?, ?)";
            runner.update(conn, sql, "testInsert", "testInsert", "1999-09-16");
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            JDBCUtils.closeResource(conn);
        }
    }

    // 测试查询
    /*
     *  BeanHandler: 是ResultSetHandler的接口实现类, 用于封装表中一条记录(对象)
     */
    @Test
    public void testGetInstance() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection2();

            String sql = "select order_id as orderId, order_name as orderName, order_date as orderDate from `order` where order_id = ?";
            QueryRunner runner = new QueryRunner();

            BeanHandler<Order> handler = new BeanHandler<>(Order.class);
            Order customer = runner.query(conn, sql, handler, 1);
            System.out.println(customer);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            JDBCUtils.closeResource(conn);
        }
    }

    @Test
    public void testGetList() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection2();

            String sql = "select order_id as orderId, order_name as orderName, order_date as orderDate from `order`";
            QueryRunner runner = new QueryRunner();

            BeanListHandler<Order> handler = new BeanListHandler<>(Order.class);
            List<Order> list = runner.query(conn, sql, handler);
            list.forEach(System.out::println);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            JDBCUtils.closeResource(conn);
        }
    }

    /*
     * MapHandler: 是ResultSetHandler的接口实现类, 对应表中一条记录
     * 将字段及相应的值作为map中的key和value
     *
     * */
    @Test
    public void testMapHandler() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection2();

            String sql = "select order_id as orderId, order_name as orderName, order_date as orderDate from `order`";
            QueryRunner runner = new QueryRunner();

            MapHandler handler = new MapHandler();
            Map<String, Object> map = runner.query(conn, sql, handler);
            System.out.println(map);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            JDBCUtils.closeResource(conn);
        }
    }

    /*
     * MapListHandler: 是ResultSetHandler接口的实现类
     * 对应表中多条记录, 将字段以及相应字段的值作为map中的key和value
     * 将这些表添加到list之中
     *
     * */
    @Test
    public void testMapListHandler() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection2();

            String sql = "select order_id as orderId, order_name as orderName, order_date as orderDate from `order`";
            QueryRunner runner = new QueryRunner();

            MapListHandler handler = new MapListHandler();
            List<Map<String, Object>> maps = runner.query(conn, sql, handler);
            maps.forEach(System.out::println);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            JDBCUtils.closeResource(conn);
        }
    }

    /*
     *
     * ScalarHandler: 是ResultSetHandler接口的实现类 用于查询特殊值
     * */
    @Test
    public void testScalarHandler() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection2();

            String sql = "select count(*) from customers";
            QueryRunner runner = new QueryRunner();

            ScalarHandler<Long> handler = new ScalarHandler<>();
            long res = runner.query(conn, sql, handler);
            System.out.println(res);

        } catch (Exception e) {
            logger.error(e);
        } finally {
            JDBCUtils.closeResource(conn);
        }
    }

    // 自定义ResultSetHandler
    @Test
    public void testMyResultHandler() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection2();

            String sql = "select id, name, email, birth from customers where id = ?";
            QueryRunner runner = new QueryRunner();

            ResultSetHandler<Customer> handler = new ResultSetHandler<>() {
                @Override
                public Customer handle(ResultSet rs) throws SQLException {
                    return null;
                }
            };
            Customer customer = runner.query(conn, sql, handler, 1);
            System.out.println(customer);

        } catch (Exception e) {
            logger.error(e);
        } finally {
            JDBCUtils.closeResource(conn);
        }
    }
}
