package jtelecom.dao.user;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Anna Rysakova
 */
public class User {
    private Integer id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String address;
    private Integer placeId;
    private Integer customerId;
    private String password;
    private String authority;
    @JsonProperty("role_id")
    private Role role;
    private UserStatus status;

    public User(String name, String surname, String email, String phone, Integer placeId, String password, Role role) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.placeId = placeId;
        this.password = password;
        this.role = role;
    }

    public User(String name, String password, String authorities) {
        this.name = name;
        this.password = password;
        this.authority = authorities;
    }

    public User() {
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Integer placeId) {
        this.placeId = placeId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthorities(String authorities) {
        this.authority = authorities;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", placeId=" + placeId +
                ", customerId=" + customerId +
                ", password='" + password + '\'' +
                ", authority='" + authority + '\'' +
                ", role=" + role +
                ", enable=" + status +
                '}';
    }
}
