package com.jtchen.dao;

import java.sql.Connection;
import java.util.List;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/2/6 16:02
 */
public interface DAO<T> {

    int update(Connection conn, String sql, Object... args);

    List<T> getForList(Connection conn, String sql, Object... args);

    T getInstance(Connection conn, String sql, Object... args);

    <E> E getValue(Connection conn, String sql, Object ...args);
}