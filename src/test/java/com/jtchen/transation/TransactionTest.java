package com.jtchen.transation;

import com.jtchen.transation.beans.User;
import com.jtchen.deprecated.JDBCUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * 1. 什么叫数据库事务?
 * 事务: 一组逻辑操作单元, 使数据从一种状态变换到另一种状态
 * >> 一组逻辑数据单元: 一个或者多个DML操作(增删改)
 * <p>
 * 2. 事务处理的原则
 * 要么都提交, 要么回滚到最初状态, 不能结束在中间状态
 * 一个事务中的几个DML操作要么都执行, 要么都不执行
 * <p>
 * 3. 数据一旦提交就不可回滚!
 * <p>
 * 4. 哪些操作会导致数据的自动提交
 * >> DDL操作一旦执行都会自动提交
 * >> DML操作(增删查改) 默认情况下一旦执行会自动提交
 * >> 我们可以通过 set autocommit = false 的方式取消DML操作的自动提交
 * >> set autocommit = false 对 DDL 操作失效
 * >> 默认在我们关闭连接的时候也会将没有提交的数据自动提交
 *
 * 5. 事务的acid属性
 * >> 原子性: 事务是一个不可分割的工作单位, 事务中的操作要不都发生要么不发生
 * >> 一致性: 事务必须从一个一致性状态变换到另一个一致性状态
 * >> 隔离性: 事务的隔离是指一个事务的执行不能被其他事务干扰, 即一个事务的内部操作及使用数据
 *          对并发的其他事务是隔离的, 并发执行的各个事务之间不能互相干扰
 * >> 持久性: 持久性是指一个事务一旦被提交, 他对数据库的影响是永久性的
 *          接下来的其他操纵和数据库故障不应该对他有任何影响
 *
 *
 * 6. 数据库的并发问题
 * >> 脏读 对于两个事务T1 T2, T1读取了T2已修改但未提交的数据, 之后T2回滚, T1的数据就是临时且无效的
 * >> 不可重复读 对于两个事务T1, T2.T1读取了该字段, 然后T2更新了该字段, 之后T1在读取该字段, 值就不同了
 * >> 幻读 对于两个事务T1 T2, T1从表中读取一个数据, 然后T2在表中插入一些新的行, 之后如果T1再去读取这个表则会多处几行
 *
 * 7. 四大隔离级别
 *
 * @author jtchen
 * @version 1.0
 * @date 2021/2/5 12:00
 */
public class TransactionTest {
    private static final Logger logger = Logger.getLogger(TransactionTest.class);

    //***********************未考虑数据库事务的转账操作****************************
    /*
        针对于数据表user_table来说
        演示AA给BB转账100

        update user_table set balance = balance - 100 where user = 'AA';
        update user_table set balance = balance + 100 where user = 'BB';
     */
    @Test
    public void testUpdate() {
        String sql1 = "update user_table set balance = balance - 100 where user = ?";
        update(sql1, "AA");

        // 模拟网络异常
        System.out.println(10 / 0);

        String sql2 = "update user_table set balance = balance + 100 where user = ?";
        update(sql2, "BB");

        System.out.println("转账成功");
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
        } finally {
            // 5. 关闭资源
            JDBCUtils.closeResource(conn, ps);
        }
    }

    //***********************考虑数据库事务的转账操作****************************

    @Test
    public void testUpdateWithTransaction() {

        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            conn.setAutoCommit(false);

            String sql1 = "update user_table set balance = balance - 100 where user = ?";
            update(conn, sql1, "AA");

            // 模拟网络异常
            System.out.println(10 / 0);

            String sql2 = "update user_table set balance = balance + 100 where user = ?";
            update(conn, sql2, "BB");

            System.out.println("转账成功");


        } catch (Exception e) {
            try {
                assert conn != null;
                conn.rollback();
            } catch (SQLException throwables) {
                logger.error(throwables);
                return;
            }
            logger.debug(e);
            logger.warn("catch an exception in the transaction, now roll back");
        } finally {
            // 若此时Connection没有被关闭, 还可能被继续使用
            // 因此setAutoCommit要设置为true
            try {
                assert conn != null;
                conn.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            JDBCUtils.closeResource(conn);
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

    // *****************************************************

    @Test
    public void testTransactionSelect() throws Exception{
        Connection conn = JDBCUtils.getConnection();

        // 获取当前连接的隔离级别
        logger.info(conn.getTransactionIsolation());

        // 设置数据库的隔离级别
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

        // 取消自动提交数据
        conn.setAutoCommit(false);

        String sql = "select user, password, balance from user_table where user = ?";
        User user = getInstance(conn, User.class, sql, "CC");

        // JDBCUtils.closeResource(conn);

        System.out.println(user);

    }

    @Test
    public void testTransactionUpdate() throws Exception{
        Connection conn = JDBCUtils.getConnection();
        // 取消自动提交数据
        conn.setAutoCommit(false);

        String sql = "update user_table set balance = ? where user = ?";
        update(conn, sql, 6000, "CC");

        // Thread.sleep(10000);
        conn.commit();
        JDBCUtils.closeResource(conn);
        logger.info("修改结束");
    }

    /**
     * 针对不同表的通用查询操作, 返回一条记录
     * @param clazz 类
     * @param sql   sql语句
     * @param args  ?填充
     * @param <T>   类
     * @return 对象
     */
    public <T> T getInstance(Connection conn, Class<T> clazz, String sql, Object... args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        T res = null;
        try {
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
            JDBCUtils.closeResource(null, ps, rs);
        }
        return res;
    }

}
