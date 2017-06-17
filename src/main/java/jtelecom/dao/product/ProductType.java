package jtelecom.dao.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Created by Yuliya Pedash on 28.04.2017.
 */
public enum ProductType {
    Tariff(1, "Tariff plan"),
    Service(2, "Service");
    private static Logger logger = LoggerFactory.getLogger(ProcessingStrategy.class);
    private static String WRONG_ID_ERROR_MSG = "Wrong id: ";
    private Integer id;
    private String name;

    ProductType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * This method gets <code>ProductType</code> object by id
     *
     * @param id id of product type
     * @return ProductType object
     */
    public static ProductType getProductTypeFromId(Integer id) {
        ProductType[] productTypes = values();
        for (ProductType productType : productTypes) {
            if (Objects.equals(productType.getId(), id)) {
                return productType;
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
