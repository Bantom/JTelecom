package jtelecom.dao.user;

import jtelecom.dao.product.ProductStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Created by Yuliya Pedash on 04.05.2017.
 */
public enum Role {
    Admin(1, "ADMIN"),
    PMG(2, "PMG"),
    CSR(3, "CSR"),
    RESIDENTIAL(4, "RESIDENTIAL"),
    BUSINESS(5, "BUSINESS"),
    EMPLOYEE(6, "EMPLOYEE");

    private Integer id;
    private String name;
    private static Logger logger = LoggerFactory.getLogger(ProductStatus.class);
    private static String WRONG_ID_ERROR_MSG = "Wrong id: ";

    Role(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getNameInLowwerCase() {
        return name.toLowerCase();
    }

    public Integer getId() {
        return id;
    }

    /**
     * This method gets <code>Role</code> object by id
     *
     * @param id id of role
     * @return Role object
     */
    public static Role getRoleFromId(Integer id) {
        Role[] roles = values();
        for (Role role : roles) {
            if (Objects.equals(role.getId(), id)) {
                return role;
            }
        }
        logger.error(WRONG_ID_ERROR_MSG + id);
        throw new IllegalArgumentException(WRONG_ID_ERROR_MSG + id);
    }

    public static Role getRoleByName(String name) {
        Role[] roles = values();
        for (Role role : roles) {
            if (Objects.equals(role.getName(), name.toUpperCase())) {
                return role;
            }
        }
        return null;
    }

}
