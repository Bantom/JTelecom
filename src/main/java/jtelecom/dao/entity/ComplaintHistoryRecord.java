/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtelecom.dao.entity;

import java.util.Calendar;

/**
 * @author Alistratenko Nikita
 */
public class ComplaintHistoryRecord {

    private Integer id;
    private Integer complaintId;
    private Calendar operationDate;
    private String status;

    public ComplaintHistoryRecord() {
    }

    public ComplaintHistoryRecord(int id, int complaintId, Calendar operationDate, String status) {
        this.id = id;
        this.complaintId = complaintId;
        this.operationDate = operationDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(int complaintId) {
        this.complaintId = complaintId;
    }

    public Calendar getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Calendar operationDate) {
        this.operationDate = operationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("ComplaintHistoryRecord{").append("id=").append(id).append(", complaintId=").append(complaintId).append(", operationDate=").append(operationDate).append(", status=").append(status).append('}').toString();
    }

}
