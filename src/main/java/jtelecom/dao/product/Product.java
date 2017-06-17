package jtelecom.dao.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import jtelecom.dao.entity.CustomerType;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author Anna Rysakova
 */
public class Product {
    private Integer id;
    private Integer categoryId;
    @JsonProperty("duration_in_days")
    private Integer durationInDays;
    @JsonProperty("type_id")
    private ProductType productType;
    private ProcessingStrategy processingStrategy;
    private String name;
    private String description;
    private ProductStatus status;
    @JsonProperty("base_price")
    private BigDecimal basePrice;
    @JsonProperty("customer_type_id")
    private CustomerType customerType;

    public Product() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(Integer durationInDays) {
        this.durationInDays = durationInDays;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType typeId) {
        this.productType = typeId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public ProcessingStrategy getProcessingStrategy() {
        return processingStrategy;
    }

    public void setProcessingStrategy(ProcessingStrategy processingStrategy) {
        this.processingStrategy = processingStrategy;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return Objects.equals(getId(), product.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Product{")
                .append("id=").append(id)
                .append(", categoryId=").append(categoryId)
                .append(", durationInDays=").append(durationInDays)
                .append(", productType=").append(productType)
                .append(", needProcessing=").append(processingStrategy)
                .append(", name='").append(name).append('\'')
                .append(", description='").append(description).append('\'')
                .append(", status=").append(status)
                .append(", basePrice=").append(basePrice)
                .append(", customerTypeId=").append(customerType)
                .append('}').toString();
    }
}
