/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtelecom.dao.price;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author Alistratenko Nikita
 */
public class Price {

    private Integer placeId;
    private Integer productId;
    private BigDecimal price;

    public Price() {
    }

    public Price(Integer placeId, Integer productId, BigDecimal price) {
        this.placeId = placeId;
        this.productId = productId;
        this.price = price;
    }

    public Integer getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Integer placeId) {
        this.placeId = placeId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Price{")
                .append("placeId=").append(placeId)
                .append(", product_id=").append(productId)
                .append(", price=").append(price)
                .append('}').toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Price)) return false;
        Price price1 = (Price) o;
        return Objects.equals(getPlaceId(), price1.getPlaceId()) &&
                Objects.equals(getProductId(), price1.getProductId()) &&
                Objects.equals(getPrice(), price1.getPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlaceId(), getProductId(), getPrice());
    }
}
