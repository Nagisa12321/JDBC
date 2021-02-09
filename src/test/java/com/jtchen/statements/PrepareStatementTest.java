package com.jtchen.statements;

import com.jtchen.beans.User;
import org.apache.log4j.Logger;

import java.util.Scanner;


/**
 * PrepareStatement替换Statement解决sql注入问题
 * <p>
 * 请输入用户名:
 * 1' or
 * 请输入密码:
 * =1 or '1' = '1
 * 2021-02-02 15:34:10,237 [main] DEBUG [JDBCUtils] - connection is null or closed, new get.
 * 2021-02-02 15:34:10,595 [main] INFO  [com.jtchen.statements.PrepareStatementTest] - Username does not exist or password is wrong
 * <p>
 * 原理:
 * 占位符起到很大作用
 * 不会改变语句原意
 * <p>
 * 除了解决Statement的拼串, sql诸如问题
 * PrepareStatement有什么好处？
 * 1. PrepareStatement可以操作Blob文件, Statement做不到
 * 2. PrepareStatement可以实现更高效的批量插入, 只校验一个语句, 可以反复填充而不用校验
 *
 * @author jtchen
 * @version 1.0
 * @date 2021/2/2 15:20
 */
public class PrepareStatementTest {
    private static final Logger logger = Logger.getLogger(PrepareStatementTest.class);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("请输入用户名:");
        String user = scanner.nextLine();
        System.out.println("请输入密码: ");
        String pwd = scanner.nextLine();

        //SELECT user,password FROM user_table WHERE user = '1' or ' AND password = '=1 or '1' = '1'
        String sql = "SELECT user,password FROM user_table WHERE user = ? AND password = ?";
        User returnUser = new PreparedStatementQueryTest().getInstance(User.class, sql, user, pwd);
        if (returnUser != null) {
            logger.info("success");
        } else {
            logger.info("Username does not exist or password is wrong");
        }
    }
}
