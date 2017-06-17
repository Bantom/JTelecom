package jtelecom.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jtelecom.dao.entity.CustomerType;
import jtelecom.dao.entity.OperationStatus;
import jtelecom.dao.product.ProductType;

import java.util.Calendar;

/**
 * Class contains full order info
 *
 * @author Moiseienko Petro
 * @since 15.05.2017.
 */
public class FullInfoOrderDTO {
    @JsonProperty("order_id")
    private Integer orderId;
    @JsonProperty("product_name")
    private String productName;
    private String description;
    @JsonProperty("product_type")
    private ProductType productType;
    private Integer productId;
    @JsonProperty("operation_date")
    private String actionDate;
    @JsonProperty("current_status_id")
    private OperationStatus operationStatus;
    private String place;
    private String userName;
    private String userSurname;
    private String phone;
    private String address;
    @JsonProperty("customer_type")
    private CustomerType customerType;

    public FullInfoOrderDTO(Integer orderId, String name, String description, ProductType productType, OperationStatus operationStatus) {
        this.orderId = orderId;
        this.productName = name;
        this.description = description;
        this.productType = productType;
        this.operationStatus = operationStatus;
    }

    public FullInfoOrderDTO() {

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String name) {
        this.productName = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getActionDate() {
        return actionDate;
    }

    public void setActionDate(String actionDate) {
        this.actionDate = actionDate;
    }

    public OperationStatus getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(OperationStatus operationStatus) {
        this.operationStatus = operationStatus;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    @Override
    public String toString() {
        return "FullInfoOrderDTO{" +
                "orderId=" + orderId +
                ", productName='" + productName + '\'' +
                ", description='" + description + '\'' +
                ", productType=" + productType +
                ", productId=" + productId +
                ", actionDate=" + actionDate +
                ", operationStatus=" + operationStatus +
                ", place='" + place + '\'' +
                ", userName='" + userName + '\'' +
                ", userSurname='" + userSurname + '\'' +
                ", phone='" + phone + '\'' +
                ", customerType=" + customerType +
                '}';
    }

    public String infoMessage() {
        String message = "Order's info: " +
                "\nProduct name: " + productName +
                "\nProduct description: " + description +
                "\nType of product: " + productType.getName() +
                "\nType of customer: " + customerType.getName() +
                "\nRegion: " + place +
                "\nOrdering date: " + actionDate +
                "\nCustomer's info: " +
                "\nName: " + userName +
                "\nSurname: " + userSurname +
                "\nPhone: " + phone +
                "\nAddress: " + address + " ";
        return message;
    }
}
