/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtelecom.dao.complaint;

import java.util.Calendar;

/**
 * @author Alistratenko Nikita
 */
public class Complaint {

    private Integer id;
    private Calendar creationDate;
    private String description;
    private ComplaintStatus status;
    private Integer pmgId;
    private Integer orderId;

    public Complaint() {
    }

    public Complaint(int id, Calendar creationDate, String description, ComplaintStatus status, int pmgId, int orderId) {
        this.id = id;
        this.creationDate = creationDate;
        this.description = description;
        this.status = status;
        this.pmgId = pmgId;
        this.orderId = orderId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public Integer getPmgId() {
        return pmgId;
    }

    public void setPmgId(Integer pmgId) {
        this.pmgId = pmgId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Complaint{").append("id=").append(id).append(", creationDate=").append(creationDate).append(", description=").append(description).append(", status=").append(status).append(", pmgId=").append(pmgId).append(", orderId=").append(orderId).append('}').toString();
    }

}
