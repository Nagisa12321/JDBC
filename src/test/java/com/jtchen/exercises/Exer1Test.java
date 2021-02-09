package com.jtchen.exercises;

import com.jtchen.deprecated.JDBCUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/2/2 16:25
 */
public class Exer1Test {
    private static final Logger logger = Logger.getLogger(Exer1Test.class);

    @Test
    public void testInsert() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入用户名:");
        String name = scanner.nextLine();
        System.out.print("请输入邮箱:");
        String email = scanner.nextLine();
        System.out.print("请输入生日:");
        String birth = scanner.nextLine();
        String sql = "INSERT INTO customers (name, email, birth) VALUES (?, ?, ?);";
        logger.info(update(sql, name, email, birth) != 0 ? "update successful" : "update failed");
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
}
