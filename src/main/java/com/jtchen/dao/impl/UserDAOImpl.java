package com.jtchen.dao.impl;

import com.jtchen.beans.User;
import com.jtchen.dao.BaseDAO;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/2/6 15:39
 */
public class UserDAOImpl extends BaseDAO<User> {

    public UserDAOImpl(Class<User> clazz) {
        super(clazz);
    }
}
