package com.jtchen.dao.impl;


import com.jtchen.beans.Customer;
import com.jtchen.dao.BaseDAO;
import com.jtchen.dao.CustomerDAO;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/2/6 16:55
 */
public class CustomerDAOImpl extends BaseDAO<Customer> implements CustomerDAO {

    public CustomerDAOImpl() {
        // super(Customer.class);
        super();
    }

    @Override
    public int insert(Connection conn, Customer customer) {
        String sql = "insert customers(name, email, birth)values(?, ?, ?)";
        return update(conn, sql, customer.getName(), customer.getEmail(), customer.getBirth());
    }

    @Override
    public int deleteById(Connection conn, int id) {
        String sql = "delete from customers where id = ?";
        return update(conn, sql, id);
    }

    @Override
    public int update(Connection conn, Customer customer) {
        String sql = "update customers set name = ?, email = ?, birth = ? where id = ?";
        return update(conn, sql, customer.getName(), customer.getEmail(), customer.getBirth(), customer.getId());
    }

    @Override
    public Customer getCustomerById(Connection conn, int id) {
        String sql = "select id, name, email, birth from customers where id = ?";
        return getInstance(conn, sql, id);
    }

    @Override
    public List<Customer> getAll(Connection conn) {
        String sql = "select id, name, email, birth from customers";
        return getForList(conn, sql);
    }

    @Override
    public Long getCount(Connection conn) {
        String sql = "select count(*) from customers";
        return getValue(conn, sql);
    }

    @Override
    public Date getMaxBirth(Connection conn) {
        String sql = "select max(birth) from customers";
        return getValue(conn, sql);
    }
}
