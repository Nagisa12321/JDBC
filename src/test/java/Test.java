import com.jtchen.deprecated.JDBCUtils;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/2/5 0:02
 */
public class Test {
    public Connection getConnection(String name, String password, String url, String className) throws Exception {
        Class.forName(className);
        return DriverManager.getConnection(url, name, password);
    }

    public <T> T getInstance(Class<T> clazz, String sql, Object... args) {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        T t = null;

        try {
            conn = JDBCUtils.getConnection();

            ps = conn.prepareStatement(sql);

            for (int i = 0; i < args.length; i++)
                ps.setObject(i + 1, args[i]);

            rs = ps.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            if (rs.next()) {
                int columnCount = md.getColumnCount();
                t = clazz.getDeclaredConstructor().newInstance();

                for (int i = 0; i < columnCount; i++) {
                    String columnLabel = md.getColumnLabel(i + 1);
                    Object columnValue = rs.getObject(i + 1);

                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t, columnValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps, rs);
        }
        return t;
    }
}
