package com.jtchen.beans;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Date;

/**
 * ORM编程思想 (object relational mapping)
 * <p>
 * - 一个数据表对应一个java类
 * - 表中一条记录对应java类的一个对象
 * - 表中的一个字段对应java类的一个属性
 *
 * @author jtchen
 * @version 1.0
 * @date 2021/2/1 15:26
 */
public class Customer {
    private int id;
    private String name;
    private String email;
    private Date birth;
    private Blob photo;

    public Customer(int id, String name, String email, Date birth) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birth = birth;
    }

    public Customer(int id, String name, String email, Date birth, Blob photo) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birth = birth;
        this.photo = photo;
    }

    public Customer() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Date getBirth() {
        return birth;
    }

    public Blob getPhoto() {
        return photo;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", birth=" + birth +
                ", photo=" + photo +
                '}';
    }
}
