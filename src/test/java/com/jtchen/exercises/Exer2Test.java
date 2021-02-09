package com.jtchen.exercises;

import com.jtchen.deprecated.JDBCUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/2/3 12:20
 */
public class Exer2Test {

    public static final Logger logger = Logger.getLogger(Exer2Test.class);

    public static void main(String[] args) {
        System.out.println("<----------欢迎来到信息查询系统---------->");
        System.out.println("请输入你要的操作, 按ENTER确认");
        System.out.println("1. 插入一个新的学生信息");
        System.out.println("2. 输入身份证或者准考证号查询学生信息");
        System.out.println("3. 通过考号删除学生");

        Exer2Test exer2Test = new Exer2Test();
        int type = new Scanner(System.in).nextInt();
        if (type == 1) exer2Test.insertStu();
        else if (type == 2) exer2Test.queryStu();
        else if (type == 3) exer2Test.deleteStu();
        else logger.warn("输入信息有误, 程序退出");
    }

    public void insertStu() {
        Scanner scanner = new Scanner(System.in);
        int Grade;
        int type;
        String IDCard;
        String ExamCard;
        String StudentName;
        String Location;

        System.out.println("===请输入学生信息===");
        System.out.print("type: ");
        type = Integer.parseInt(scanner.nextLine());
        System.out.print("IDCard: ");
        IDCard = scanner.nextLine();
        System.out.print("ExamCard: ");
        ExamCard = scanner.nextLine();
        System.out.print("StudentName: ");
        StudentName = scanner.nextLine();
        System.out.print("Location: ");
        Location = scanner.nextLine();
        System.out.print("Grade: ");
        Grade = Integer.parseInt(scanner.nextLine());

        String sql = "INSERT INTO examstudent (Type, IDCard, ExamCard, StudentName, Location, Grade) values (?, ?, ?, ?, ?, ?);";
        if (update(sql, type, IDCard, ExamCard, StudentName, Location, Grade) != 0) {
            logger.info("添加成功! ");
        } else logger.warn("添加失败! ");
        scanner.close();
    }

    public void queryStu() {
        Scanner scanner = new Scanner(System.in);
        String sql;
        Student student;
        System.out.println("===请选择查询方式===");
        System.out.println("1. 准考证号");
        System.out.println("2. 身份证号");
        int type = Integer.parseInt(scanner.nextLine());
        if (type == 1) {
            sql = "SELECT * FROM examstudent WHERE ExamCard = ?;";
            System.out.println("请输入准考证号(ExamCard)");
            String ExamCard = scanner.nextLine();
            student = getInstance(Student.class, sql, ExamCard);
            if (student != null) System.out.println(student);
            else logger.warn("查无此人!");
        } else if (type == 2) {
            sql = "SELECT * FROM examstudent WHERE IDCard = ?;";
            System.out.println("请输入身份证号(IDCard)");
            String IDCard = scanner.nextLine();
            student = getInstance(Student.class, sql, IDCard);
            if (student != null) System.out.println(student);
            else logger.warn("查无此人!");
        } else logger.warn("输入信息有误, 程序退出");
    }

    public void deleteStu() {
        Scanner scanner = new Scanner(System.in);
        String sql;
        System.out.println("===请选择删除方式===");
        System.out.println("1. 准考证号");
        System.out.println("2. 身份证号");
        int type = Integer.parseInt(scanner.nextLine());
        if (type == 1) {
            sql = "DELETE FROM examstudent WHERE ExamCard = ?;";
            System.out.println("请输入准考证号(ExamCard)");
            String ExamCard = scanner.nextLine();
            if (update(sql, ExamCard) != 0) logger.info("删除成功!");
            else logger.info("删除失败!");
        } else if (type == 2) {
            sql = "DELETE FROM examstudent WHERE IDCard = ?;";
            System.out.println("请输入身份证号(IDCard)");
            String IDCard = scanner.nextLine();
            if (update(sql, IDCard) != 0) logger.info("删除成功!");
            else logger.info("删除失败!");
        } else logger.warn("输入信息有误, 程序退出");
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

    private static class Student {
        private int FlowID;
        private int Type;
        private String IDCard;
        private String ExamCard;
        private String StudentName;
        private String Location;
        private int Grade;


        public Student() {
        }

        public Student(int flowID,
                       int type,
                       String IDCard,
                       String examCard,
                       String studentName,
                       String location,
                       int grade) {
            FlowID = flowID;
            Type = type;
            this.IDCard = IDCard;
            ExamCard = examCard;
            StudentName = studentName;
            Location = location;
            Grade = grade;
        }

        @Override
        public String toString() {

            return "===学生信息如下===\n" +
                    "flowID: " + FlowID + '\n' +
                    "type: " + Type + '\n' +
                    "IDCard: " + IDCard + '\n' +
                    "ExamCard: " + ExamCard + '\n' +
                    "Student name: " + StudentName + '\n' +
                    "Location: " + Location + '\n' +
                    "Grade: " + Grade;
        }
    }
}
