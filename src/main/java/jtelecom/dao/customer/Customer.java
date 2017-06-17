package jtelecom.dao.customer;

import jtelecom.dao.entity.CustomerType;

/**
 * @author Moiseienko Petro , Alistratenko Nikite
 * @since 24.04.2017.
 */
public class Customer {
    private Integer id;
    private String name;
    private String secretKey;
    private CustomerType customerType;
    private Integer invoice;


    public Customer(Integer id, String name, String secretKey) {
        this.id = id;
        this.name = name;
        this.secretKey = secretKey;
    }

    public Customer(String name, String secretKey) {
        this.name = name;
        this.secretKey = secretKey;
    }

    public Customer(String name) {
        this.name = name;
    }

    public Customer() {

    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Integer getInvoice() {
        return invoice;
    }

    public void setInvoice(Integer invoice) {
        this.invoice = invoice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (!id.equals(customer.id)) return false;
        if (secretKey != null ? !secretKey.equals(customer.secretKey) : customer.secretKey != null) return false;
        return invoice != null ? invoice.equals(customer.invoice) : customer.invoice == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (secretKey != null ? secretKey.hashCode() : 0);
        result = 31 * result + (invoice != null ? invoice.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Customer{").append("id=").append(id).append(", name='").append(name).append('\'').append(", secretKey='").append(secretKey).append('\'').append(", customerType=").append(customerType).append(", invoice=").append(invoice).append('}').toString();
    }
}
