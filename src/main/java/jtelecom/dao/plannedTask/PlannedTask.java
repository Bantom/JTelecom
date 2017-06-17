/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtelecom.dao.plannedTask;

import jtelecom.dao.entity.OperationStatus;

import java.util.Calendar;

/**
 * @author Alistratenko Nikita
 */
public class PlannedTask {

    private Integer id;
    private OperationStatus status;
    private Integer orderId;
    private Calendar actionDate;

    public PlannedTask() {
    }

    public PlannedTask(OperationStatus status, int orderId, Calendar actionDate) {

        this.status = status;
        this.orderId = orderId;
        this.actionDate = actionDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OperationStatus getStatus() {
        return status;
    }

    public void setStatus(OperationStatus status) {
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Calendar getActionDate() {
        return actionDate;
    }

    public void setActionDate(Calendar actionDate) {
        this.actionDate = actionDate;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("PlannedTask{").append("id=").append(id).append(", status=").append(status).append(", orderId=").append(orderId).append(", actionDate=").append(actionDate).append('}').toString();
    }

}
