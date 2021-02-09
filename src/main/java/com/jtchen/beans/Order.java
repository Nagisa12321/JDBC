package com.jtchen.beans;

import java.sql.Date;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/2/1 23:22
 */
public class Order {
    private int orderId;
    private String orderName;
    private Date orderDate;

    public Order() {
    }

    public Order(int order_id, String order_name, Date order_date) {
        this.orderId = order_id;
        this.orderName = order_name;
        this.orderDate = order_date;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "order_id=" + orderId +
                ", order_name='" + orderName + '\'' +
                ", order_date=" + orderDate +
                '}';
    }
}
