package jtelecom.dao.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Created by Yuliya Pedash on 05.05.2017.
 */
public enum ProductStatus {
    Available(1, "Available"),
    Disable(0, "Disable");
    private static Logger logger = LoggerFactory.getLogger(ProductStatus.class);
    private static String WRONG_ID_ERROR_MSG = "Wrong id: ";
    private Integer id;
    private String name;

    ProductStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * This method gets <code>ProductStatus</code> object by id
     *
     * @param id id of product type
     * @return ProductStatus object
     */
    public static ProductStatus getProductStatusFromId(Integer id) {
        ProductStatus[] productStatuses = values();
        for (ProductStatus productStatus : productStatuses) {
            if (Objects.equals(productStatus.getId(), id)) {
                return productStatus;
            }
        }
        logger.error(WRONG_ID_ERROR_MSG + id);
        throw new IllegalArgumentException(WRONG_ID_ERROR_MSG + id);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
