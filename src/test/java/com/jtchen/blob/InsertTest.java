package com.jtchen.blob;

import com.jtchen.deprecated.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * 使用PrepareStatement实现批量数据操作
 * <p>
 * update、delete本身就具有批量操作的效果
 * 此时的批量操作，只要指的是批量插入
 * 使用PrepareStatement如何实现更高翔的批量插入?
 * <p>
 * 题目: 向goods表 中插入两万条数据
 * create table goods(
 * id int primary key auto_increment,
 * name varchar(25)
 * );
 * <p>
 * 方式一: Statement
 * <p>
 * Connection conn = JDBCUtils.getConnection();
 * Statement st = conn.createStatement();
 * for (int i = 1; i <= 20000; i++) {
 * String sql = "insert into goods(name)values('name_" + i + "')";
 * st.execute(sql);
 * }
 *
 * @author jtchen
 * @version 1.0
 * @date 2021/2/4 13:49
 */
public class InsertTest {
    // 批量插入方式二: 使用PrepareStatement
    // successful and the time is 44298
    @Test
    public void testInsert1() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();
            conn = JDBCUtils.getConnection();
            String sql = "insert into goods (name) values (?);";
            ps = conn.prepareStatement(sql);
            for (int i = 1; i <= 20000; i++) {
                ps.setObject(1, "name_" + i);
                ps.execute();
            }
            long end = System.currentTimeMillis();
            System.out.println("successful and the time is " + (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }
    }

    /*  批量插入方式三:
        1. addBatch()、 executeBatch()、 clearBatch()
        2. mysql服务器是默认关闭批处理
            我们需要通过一个参数, 让mysql开启批处理的支持
            ?rewroteBatchedStatements=true
            successful and the time is 47577

        successful and the time is 1156

        note: 不要手贱在你的sql语句之后加上分号!!!!!!!!!!
        否则会
        java.sql.BatchUpdateException: You have an error in your SQL syntax; check the manual that
        corresponds to your MySQL server version for the right syntax to use near ',('name_2');,('
        name_3');,('name_4');,('name_5');,('name_6');,('name_7');,('name_' at line 1

	    at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	    at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAc
	    cessorImpl.java:62)

	    ......
     */
    @Test
    public void testInsert2() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();
            conn = JDBCUtils.getConnection();
            String sql = "insert into goods (name) values (?)";
            ps = conn.prepareStatement(sql);
            for (int i = 1; i <= 1000000; i++) {
                ps.setObject(1, ("name_" + i));

                // 1."攒"sql
                ps.addBatch();
                if (i % 500 == 0) {

                    // 2.执行
                    ps.executeBatch();

                    // 3.清空Batch
                    ps.clearBatch();
                }
            }
            long end = System.currentTimeMillis();
            //1000000:successful and the time is 11971
            System.out.println("successful and the time is " + (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }
    }

    /*
        批量插入方式4: 设置不允许自动提交数据
        successful and the time is 7869
     */
    @Test
    public void testInsert3() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            long start = System.currentTimeMillis();
            conn = JDBCUtils.getConnection();

            // 设置不允许自动提交数据
            conn.setAutoCommit(false);

            String sql = "insert into goods (name) values (?)";
            ps = conn.prepareStatement(sql);
            for (int i = 1; i <= 1000000; i++) {
                ps.setObject(1, ("name_" + i));

                // 1."攒"sql
                ps.addBatch();
                if (i % 500 == 0) {

                    // 2.执行
                    ps.executeBatch();

                    // 3.清空Batch
                    ps.clearBatch();
                }
            }

            // 提交数据
            conn.commit();

            long end = System.currentTimeMillis();
            //1000000:successful and the time is 33293
            System.out.println("successful and the time is " + (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }
    }
}
