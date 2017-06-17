package jtelecom.dao.operationHistory;

import jtelecom.dao.entity.OperationStatus;
import jtelecom.dto.FullInfoOrderDTO;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Moiseienko Petro
 * @since 10.05.2017.
 */
@Service
public class OperationHistoryDAOImpl implements OperationHistoryDAO {

    private static final String PRODUCT_NAME = "product_name";
    private static final String OPERATION_DATE = "operation_date";
    private static final String STATUS_ID = "status_id";
    private static final String CURRENT_STATUS_ID = "current_status_id";

    private static final String START = "start";
    private static final String END = "end";
    private static final String PATTERN = "pattern";
    private static final String USER_ID ="userId";
    private static final String ORDER_ID ="orderId";

    private final static String SELECT_OPERATION_HISTORY_BY_USER_SQL = "SELECT * FROM( \n" +
            " SELECT product_name, " +
            " operation_date, " +
            " current_status_id, " +
            " ROW_NUMBER() OVER (ORDER BY %s) R  FROM \n" +
            "   (SELECT PRODUCTS.name product_name, " +
            "    TO_CHAR(operation_date,'YYYY-MM-DD') operation_date, " +
            "    status_id current_status_id \n" +
            "    FROM OPERATIONS_HISTORY \n" +
            "    JOIN ORDERS ON (OPERATIONS_HISTORY.order_id = ORDERS.id) \n" +
            "    JOIN PRODUCTS ON ORDERS.product_id=PRODUCTS.id \n" +
            "    WHERE ORDER_ID IN (SELECT id " +
            "                       FROM ORDERS " +
            "                       WHERE USER_ID IN (SELECT id " +
            "                                         FROM USERS " +
            "                                         WHERE customer_id= " +
            "                                          (SELECT customer_id " +
            "                                           FROM USERS " +
            "                                           WHERE id=:userId))))) \n" +
            " WHERE R>:start AND R<=:end AND (upper(product_name) LIKE upper(:pattern)  " +
            " OR upper(operation_date) LIKE upper(:pattern))";

    private final static String SELECT_COUNT_OPERATION_FOR_USER_SQL = "SELECT COUNT(*) FROM ( " +
            " SELECT PRODUCTS.name product_name, " +
            " TO_CHAR(operation_date,'YYYY-MM-DD') operation_date, " +
            " status_id \n" +
            " FROM OPERATIONS_HISTORY \n" +
            " JOIN ORDERS on (OPERATIONS_HISTORY.order_id = ORDERS.id) \n" +
            " JOIN PRODUCTS on (ORDERS.product_id=PRODUCTS.id) \n" +
            " WHERE ORDER_ID IN (SELECT ID " +
            "                    FROM ORDERS " +
            "                    WHERE USER_ID IN (SELECT id " +
            "                                      FROM USERS " +
            "                                      WHERE customer_id= " +
            "                                       (SELECT customer_id " +
            "                                        FROM USERS " +
            "                                        WHERE id=:userId)))) \n" +
            " WHERE upper(product_name) LIKE upper(:pattern) " +
            " OR upper(operation_date) LIKE upper(:pattern)";

    private final static String SELECT_COUNT_OF_OPERATIONS_BY_ORDER_ID_SQL = "SELECT COUNT(ID) \n" +
            "  FROM OPERATIONS_HISTORY \n" +
            "  WHERE ORDER_ID=:orderId";

    private final static String SELECT_INTERVAL_OF_OPERATIONS_BY_ORDER_ID_SQL = "SELECT * FROM \n" +
            " (SELECT TO_CHAR(operation_date,'YYYY-MM-DD') operation_date, " +
            "  status_id, " +
            "  PRODUCTS.name product_name, " +
            "  ROW_NUMBER() OVER (ORDER BY OPERATION_DATE) R\n" +
            "  FROM OPERATIONS_HISTORY " +
            "  JOIN ORDERS ON (OPERATIONS_HISTORY.order_id = ORDERS.ID) \n" +
            "  JOIN PRODUCTS ON (PRODUCTS.id=ORDERS.product_id) \n" +
            "  WHERE ORDER_ID=:orderId ) \n" +
            " WHERE R > :start AND R <= :end";


    @Resource
    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FullInfoOrderDTO> getOperationHistoryByUserId(Integer userId, int start, int length, String order, String search) {
        MapSqlParameterSource params = new MapSqlParameterSource(USER_ID, userId);
        if (order.isEmpty()) {
            order = OPERATION_DATE;
        }
        params.addValue(START, start);
        params.addValue(END, length);
        params.addValue(PATTERN, "%" + search + "%");
        String sql = String.format(SELECT_OPERATION_HISTORY_BY_USER_SQL, order);
        return jdbcTemplate.query(sql, params, (rs, rownum) -> {
            FullInfoOrderDTO history = new FullInfoOrderDTO();
            history.setProductName(rs.getString(PRODUCT_NAME));
            history.setActionDate(rs.getString(OPERATION_DATE));
            history.setOperationStatus(OperationStatus.getOperationStatusFromId(rs.getInt(CURRENT_STATUS_ID)));
            return history;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getCountOperationForUser(Integer userId, String search) {
        MapSqlParameterSource params = new MapSqlParameterSource(USER_ID, userId);
        params.addValue(PATTERN, "%" + search + "%");
        return jdbcTemplate.queryForObject(SELECT_COUNT_OPERATION_FOR_USER_SQL, params, Integer.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getCountOperationsByOrderId(int orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource(ORDER_ID, orderId);
        return jdbcTemplate.queryForObject(SELECT_COUNT_OF_OPERATIONS_BY_ORDER_ID_SQL, params, Integer.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FullInfoOrderDTO> getIntervalOfOperationsByOrderId(int startIndex, int endIndex, int orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(START, startIndex);
        params.addValue(END, endIndex);
        params.addValue(ORDER_ID, orderId);
        List<FullInfoOrderDTO> history = jdbcTemplate.query(SELECT_INTERVAL_OF_OPERATIONS_BY_ORDER_ID_SQL, params, (rs, rownum) -> {
            FullInfoOrderDTO order = new FullInfoOrderDTO();
            order.setActionDate(rs.getString(OPERATION_DATE));
            Integer status = rs.getInt(STATUS_ID);
            order.setOperationStatus(OperationStatus.getOperationStatusFromId(status));
            order.setProductName(rs.getString(PRODUCT_NAME));
            return order;
        });
        return history;
    }
}
