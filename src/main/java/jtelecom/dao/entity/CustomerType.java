package jtelecom.dao.entity;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Created by Yuliya Pedash on 29.04.2017.
 */
public enum CustomerType {
    Business(1, "BUSINESS"),
    Residential(2, "RESIDENTIAL");
    private static Logger logger = LoggerFactory.getLogger(CustomerType.class);
    private static String WRONG_ID_ERROR_MSG = "Wrong id: ";
    private Integer id;
    private String name;

    CustomerType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * This method gets <code>CustomerType</code> object by id
     *
     * @param id id of customer type
     * @return CustomerType object or <code>null</code> if object by this id is not found
     */
    public static CustomerType getCustomerTypeFromId(Integer id) {
        CustomerType[] customerTypes = values();
        for (CustomerType customerType : customerTypes) {
            if (Objects.equals(customerType.getId(), id)) {
                return customerType;
            }
        }
        logger.error(WRONG_ID_ERROR_MSG + id);
        throw new IllegalArgumentException(WRONG_ID_ERROR_MSG + id);
    }
}
