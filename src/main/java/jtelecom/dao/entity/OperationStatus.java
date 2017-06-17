package jtelecom.dao.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Created by Yuliya Pedash on 28.04.2017.
 */
public enum OperationStatus {
    Active(1, "Active"),
    Suspended(2, "Suspended"),
    Deactivated(3, "Deactivated"),
    InProcessing(4, "In Processing");
    private static Logger logger = LoggerFactory.getLogger(CustomerType.class);
    private static String WRONG_ID_ERROR_MSG = "Wrong id: ";
    private Integer id;
    private String name;

    OperationStatus(Integer id, String name) {
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
     * This method gets <code>OperationStatus</code> object by id
     *
     * @param id id operation status
     * @return OperationStatus object  or <code>null</code> if object by this id is not found
     */
    public static OperationStatus getOperationStatusFromId(Integer id) {
        OperationStatus[] operationStatuses = values();
        for (OperationStatus operationStatus : operationStatuses) {
            if (Objects.equals(operationStatus.getId(), id)) {
                return operationStatus;
            }
        }
        logger.error(WRONG_ID_ERROR_MSG + id);
        throw new IllegalArgumentException(WRONG_ID_ERROR_MSG + id);
    }


}


