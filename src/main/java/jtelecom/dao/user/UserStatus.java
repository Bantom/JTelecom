package jtelecom.dao.user;

import jtelecom.dao.product.ProductStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author Moiseienko Petro
 * @since 21.05.2017.
 */
public enum UserStatus {
    ENABLE(1, "enable"),
    DISABLE(0, "disable");

    private Integer id;
    private String name;
    private static Logger logger = LoggerFactory.getLogger(UserStatus.class);
    private static String WRONG_ID_ERROR_MSG = "Wrong id: ";

    UserStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
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

    public static UserStatus getStatusFromId(Integer id){
        UserStatus[] statuses = values();
        for (UserStatus status : statuses) {
            if (Objects.equals(status.getId(), id)) {
                return status;
            }
        }
        logger.error(WRONG_ID_ERROR_MSG + id);
        throw new IllegalArgumentException(WRONG_ID_ERROR_MSG + id);
    }
}
