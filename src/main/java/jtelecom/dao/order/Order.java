/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtelecom.dao.order;

import jtelecom.dao.entity.OperationStatus;

import java.util.Objects;

/**
 * @author Alistratenko Nikita
 */
public class Order {


    private Integer id;
    private Integer productId;
    private Integer userId;
    private OperationStatus currentStatus;

    public Order(Integer productId, Integer userId, OperationStatus currentStatus) {
        this.productId = productId;
        this.userId = userId;
        this.currentStatus = currentStatus;
    }

    public Order(Integer id, Integer productId, Integer userId, OperationStatus currentStatus) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.currentStatus = currentStatus;
    }

    public Order() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public OperationStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(OperationStatus current_status) {
        this.currentStatus = current_status;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Order{").append("id=").append(id).append(", productId=").append(productId).append(", userId=").append(userId).append(", currentStatus=").append(currentStatus).append('}').toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return Objects.equals(getId(), order.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
