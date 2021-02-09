package com.jtchen.blob;

import com.jtchen.deprecated.JDBCUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.*;

/**
 * 测试使用PrepareStatement操作Blob类型的数据
 *
 * @author jtchen
 * @version 1.0
 * @date 2021/2/3 21:22
 */
public class BlobTest {

    private static final Logger logger = Logger.getLogger(BlobTest.class);

    // 向数据表customers表中插入Blob类型字段
    @Test
    public void testInsert() {
        // 获取链接
        Connection conn = null;
        PreparedStatement ps = null;
        try (FileInputStream fi = new FileInputStream("./src/main/resources/img/test.jpg")) {
            conn = JDBCUtils.getConnection();

            // 预编译
            String sql = "insert into customers(name, email, birth, photo) values (?, ?, ?, ?)";

            ps = conn.prepareStatement(sql);

            // 填充占位符
            ps.setObject(1, "jtchen");
            ps.setObject(2, "jtc@qq.com");
            ps.setObject(3, "1999-09-16");
            // 用InputStream传进去
            ps.setBlob(4, fi);

            if (ps.executeUpdate() > 0) {
                logger.info("insert successful");
            } else logger.warn("failed");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }
    }

    // 查询数据表中的Blob字段
    @Test
    public void testQuery() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            conn = JDBCUtils.getConnection();

            String sql = "select id,name,email,birth,photo from customers where id = ?;";
            ps = conn.prepareStatement(sql);
            ps.setObject(1, 28);

            rs = ps.executeQuery();
            ResultSetMetaData md = rs.getMetaData();

            if (rs.next()) {
                // 将Blob类型字段下载下来, 以文件的形式保存在本地
                Blob blob = rs.getBlob("photo");
                is = blob.getBinaryStream();
                fos = new FileOutputStream("./src/main/resources/output.jpg");
                int data;
                while ((data = is.read()) != -1) fos.write(data);
            }
            if (is != null) is.close();
            if (fos != null) fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps, rs);
        }
    }

}
