package com.jtchen.dao.impl;

import com.jtchen.beans.Customer;
import com.jtchen.util.JDBCUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

public class CustomerDAOImplTest {

    private static final Logger logger = Logger.getLogger(CustomerDAOImplTest.class);
    private CustomerDAOImpl customerDAO = null;

    @Before
    public void setUp() throws Exception {
        customerDAO = new CustomerDAOImpl();
    }

    @Test
    public void insert() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection2();

            List<Customer> all = customerDAO.getAll(conn);
            logger.info("***********before insert***********");
            all.forEach(logger::debug);

            Customer customer = new Customer(123, "druid insert", "druid@qq.com", Date.valueOf("1999-09-16"));
            logger.info("**************insert***************");
            if (customerDAO.insert(conn, customer) != 0) {
                logger.info("insert success");
                all = customerDAO.getAll(conn);
                logger.info("***********after insert***********");
                all.forEach(logger::debug);
            } else logger.error("failed");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn);
        }
    }

    @Test
    public void deleteById() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();

            List<Customer> all = customerDAO.getAll(conn);
            logger.info("***********before delete***********");
            all.forEach(logger::debug);

            logger.info("**************delete***************");
            if (customerDAO.deleteById(conn, 30) != 0) {
                logger.info("delete success");
                all = customerDAO.getAll(conn);
                logger.info("***********after insert***********");
                all.forEach(logger::debug);
            } else logger.error("failed");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn);
        }
    }

    @Test
    public void update() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();

            List<Customer> all = customerDAO.getAll(conn);
            logger.info("***********before update***********");
            all.forEach(logger::debug);

            logger.info("**************update***************");
            Customer customer = new Customer(1, "test update", "1216414009@qq.com", Date.valueOf("1999-09-16"));
            if (customerDAO.update(conn, customer) != 0) {
                logger.info("update success");
                all = customerDAO.getAll(conn);
                logger.info("***********after update***********");
                all.forEach(logger::debug);
            } else logger.error("failed");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn);
        }
    }

    @Test
    public void getCustomerById() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            Customer customer = customerDAO.getCustomerById(conn, 13);
            System.out.println(customer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn);
        }
    }


    @Test
    public void getCount() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();

            logger.info("**************get count***************");
            Long count = customerDAO.getCount(conn);
            logger.info("the count is " + count);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn);
        }
    }

    @Test
    public void getMaxBirth() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();

            logger.info("**************get max birth***************");
            Date maxBirth = customerDAO.getMaxBirth(conn);
            logger.info("the max birth is " + maxBirth);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn);
        }
    }
}