package jtelecom.dao.operationHistory;

import jtelecom.dto.FullInfoOrderDTO;

import java.util.List;

/**
 * @author Moiseienko Petro
 * @since 10.05.2017.
 */
public interface OperationHistoryDAO {

    /**
     * Method finds part of the operation history by the user
     * where data satisfy to search pattern and sorted by column name
     *
     * @param userId id of the user
     * @param start  first element
     * @param length last element
     * @param order  column name for sorting
     * @param search search pattern
     * @return list of the history
     */
    List<FullInfoOrderDTO> getOperationHistoryByUserId(Integer userId, int start, int length, String order, String search);

    /**
     * Method finds total count of the operation by user
     * and satisfy search pattern
     *
     * @param userId id of the user
     * @param search search pattern
     * @return total count of the operations
     */
    Integer getCountOperationForUser(Integer userId, String search);

    /**
     * Method finds total count of the operation by order
     *
     * @param orderId id of the order
     * @return total count of the operations
     */
    Integer getCountOperationsByOrderId(int orderId);

    /**
     * Method finds part of the operation history by the order
     *
     * @param startIndex first element
     * @param endIndex   last element
     * @param orderId    id of the order
     * @return list of the history
     */
    List<FullInfoOrderDTO> getIntervalOfOperationsByOrderId(int startIndex, int endIndex, int orderId);
}
