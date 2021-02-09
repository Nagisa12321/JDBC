package com.jtchen.dao;

import com.jtchen.beans.Customer;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

/**
 * 此接口用于规范针对于customers的常用操作
 * @author jtchen
 * @version 1.0
 * @date 2021/2/6 16:42
 */
public interface CustomerDAO{

    /**
     * 将customer对象添加到数据库中
     * @param conn 数据库链接
     * @param customer customer对象
     */
    int insert(Connection conn, Customer customer);

    /**
     * 根据指定的id删除一条记录
     * @param conn 数据库链接
     * @param id customer 的 id
     */
    int deleteById(Connection conn, int id);

    /**
     * 针对指定的id, 更新表中的相应id值为新的对象(内存中的customer对象)
     * @param conn 数据库链接
     * @param id customer id
     * @param customer 新的对象
     */
    int update(Connection conn, Customer customer);

    /**
     * 根据指定的id查询客户customer
     * @param conn 数据库链接
     * @param id 指定的id
     */
    Customer getCustomerById(Connection conn, int id);

    /**
     * 查询表中所有记录构成的集合
     * @param conn 数据库链接
     * @return 表中多又记录构成的集合
     */
    List<Customer> getAll(Connection conn);

    /**
     * 返回数据表中数据的条目数
     * @param conn 数据库链接
     * @return 数据条目数
     */
    Long getCount(Connection conn);

    /**
     * 返回数据表中的最大生日
     * @param conn 数据库连接
     * @return 最大生日
     */
    Date getMaxBirth(Connection conn);

}
