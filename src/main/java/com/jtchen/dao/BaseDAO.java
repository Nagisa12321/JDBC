package com.jtchen.dao;

import com.jtchen.util.JDBCUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装了针对于数据表的通用操作
 * 增删查改
 * <p>
 * DAO: data(base) access object
 *
 * @author jtchen
 * @version 1.0
 * @date 2021/2/6 15:25
 */
public abstract class BaseDAO<T> implements DAO<T> {
    private static final Logger logger = Logger.getLogger(BaseDAO.class);
    private final Class<T> clazz;

    public BaseDAO(Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    public BaseDAO() {
        // 获取当前BaseDAO的子类继承的父类中的泛型
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        ParameterizedType type = (ParameterizedType) genericSuperclass;
        Type[] actualTypeArguments = type.getActualTypeArguments(); // 获取父类泛型参数
        clazz = (Class<T>) actualTypeArguments[0]; // 泛型第一个参数
    }

    @Override
    public int update(Connection conn, String sql, Object... args) {
        PreparedStatement ps = null;
        try {
            // 获得PreparedStatement实例
            ps = conn.prepareStatement(sql);
            // 填充占位符
            for (int i = 0; i < args.length; i++)
                ps.setObject(i + 1, args[i]);
            // 执行
            return ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
            return 0;
        } finally {
            JDBCUtils.closeResource(null, ps);
        }
    }

    @Override
    public T getInstance(Connection conn, String sql, Object... args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        T t = null;

        try {
            // 获得PreparedStatement实例
            ps = conn.prepareStatement(sql);
            // 填充占位符
            for (int i = 0; i < args.length; i++)
                ps.setObject(i + 1, args[i]);
            // 获取结果集
            rs = ps.executeQuery();
            if (rs.next()) {
                // 获得元数据
                ResultSetMetaData md = rs.getMetaData();
                int columnCount = md.getColumnCount();
                // 通过类构造器获得实例
                t = clazz.getDeclaredConstructor().newInstance();

                // 通过反射填充实例中字段
                setField(rs, t, md, columnCount);
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            JDBCUtils.closeResource(null, ps, rs);
        }
        return t;
    }

    @Override
    public List<T> getForList(Connection conn, String sql, Object... args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<T> list = new ArrayList<>();
        try {
            ps = conn.prepareStatement(sql);

            for (int i = 0; i < args.length; i++)
                ps.setObject(i + 1, args[i]);

            rs = ps.executeQuery();

            while (rs.next()) {
                T t = clazz.getDeclaredConstructor().newInstance();
                ResultSetMetaData md = rs.getMetaData();
                int columnCount = md.getColumnCount();
                setField(rs, t, md, columnCount);
                list.add(t);
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            JDBCUtils.closeResource(null, ps, rs);
        }
        return list;
    }

    // 用于查询特殊值的通用方法
    @SuppressWarnings("unchecked")
    @Override
    public <E> E getValue(Connection conn, String sql, Object... args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++)
                ps.setObject(i + 1, args[i]);
            rs = ps.executeQuery();

            if (rs.next())
                return (E) rs.getObject(1);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            JDBCUtils.closeResource(null, ps, rs);
        }
        return null;
    }

    private void setField(ResultSet rs, T t, ResultSetMetaData md, int columnCount) throws SQLException, NoSuchFieldException, IllegalAccessException {
        for (int i = 0; i < columnCount; i++) {
            String columnLabel = md.getColumnLabel(i + 1);
            Object columnValue = rs.getObject(i + 1);

            Field field = clazz.getDeclaredField(columnLabel);
            field.setAccessible(true);
            field.set(t, columnValue);
        }
    }
}
