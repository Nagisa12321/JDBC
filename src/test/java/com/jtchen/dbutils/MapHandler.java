package com.jtchen.dbutils;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/2/9 15:10
 */
public class MapHandler implements ResultSetHandler<Map<String, Object>> {
    private static final Logger logger = Logger.getLogger(MapHandler.class);

    public MapHandler() {
    }

    @Override
    public Map<String, Object> handle(ResultSet rs) throws SQLException {
        HashMap<String, Object> map = new HashMap<>();
        try {
            if (rs.next()) {
                ResultSetMetaData md = rs.getMetaData();
                int columnCount = md.getColumnCount();


                for (int i = 0; i < columnCount; i++) {
                    String columnLabel = md.getColumnLabel(i + 1);
                    Object columnValue = rs.getObject(i + 1);

                    map.put(columnLabel, columnValue);
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return map;
    }
}
