package jtelecom.dto;

import jtelecom.dao.complaint.ComplaintStatus;

import java.util.Calendar;

/**
 * This class created to transfer detailed information about complaint.
 *
 * @author Revniuk Aleksandr
 */
public class FullComplaintInfoDTO {
    private Integer id;
    private Calendar creatingDate;
    private String description;
    private ComplaintStatus status;
    private Integer pmgId;
    private String productName;
    private String userName;
    private String userSurname;
    private String userPhone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Calendar getCreatingDate() {
        return creatingDate;
    }

    public void setCreatingDate(Calendar creatingDate) {
        this.creatingDate = creatingDate;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    @Override
    public String toString() {
        return "FullComplaintInfoDTO{" +
                "id=" + id +
                ", creatingDate=" + creatingDate +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", pmgId=" + pmgId +
                ", productName='" + productName + '\'' +
                ", userName='" + userName + '\'' +
                ", userSurname='" + userSurname + '\'' +
                ", userPhone='" + userPhone + '\'' +
                '}';
    }
}
