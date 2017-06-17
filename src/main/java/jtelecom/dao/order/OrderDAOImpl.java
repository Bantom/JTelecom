package jtelecom.dao.order;

import jtelecom.dao.entity.CustomerType;
import jtelecom.dao.entity.OperationStatus;
import jtelecom.dao.product.ProductType;
import jtelecom.dto.FullInfoOrderDTO;
import jtelecom.dto.OrdersRowDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Yuliya Pedash
 */
@Service
public class OrderDAOImpl implements OrderDAO {

    private static final String CUST_ID = "cust_id";
    private static final String PRODUCT_ID = "product_id";
    private static final String USER_ID = "user_id";
    private static final String START = "start";
    private static final String LENGTH = "length";
    private static final String PATTERN = "pattern";
    private static final String ID = "id";
    private static final String CUR_STATUS_ID = "cur_status_id";

    private static final String PRODUCT_TYPE = "product_type";
    private static final String ORDER_ID = "order_id";
    private static final String CSR_ID = "csrId";
    private static final String PRODUCT_NAME = "product_name";
    private static final String DESCRIPTION = "description";
    private static final String USERID = "userId";
    private static final String ORDERID = "orderId";
    private static final String PRODUCTID = "productId";
    private static final String USER_NAME = "user_name";
    private static final String USER_SURNAME = "user_surname";
    private static final String ADDRESS = "address";
    private static final String PHONE = "user_phone";
    private static final String PLACE = "place";
    private static final String OPERATION_DATE = "operation_date";
    private static final String CUSTOMER_TYPE = "customer_type";
    private static final String CURRENT_STATUS_ID = "current_status_id";
    private static Logger logger = LoggerFactory.getLogger(OrderDAOImpl.class);

    @Resource
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Resource
    private OrderRowMapper orderRowMapper;
    private final static String SELECT_ORDERS_BY_CUST_ID_SQL = "SELECT\n" +
            "  ORDERS.ID,\n" +
            "  ORDERS.PRODUCT_ID,\n" +
            "  ORDERS.USER_ID,\n" +
            "  ORDERS.CURRENT_STATUS_ID " +
            "FROM ORDERS\n" +
            "  INNER JOIN USERS ON USERS.ID = ORDERS.USER_ID\n" +
            "  INNER JOIN CUSTOMERS ON USERS.CUSTOMER_ID = CUSTOMERS.ID\n" +
            "  INNER JOIN OPERATION_STATUS ON ORDERS.CURRENT_STATUS_ID = OPERATION_STATUS.ID\n" +
            "  INNER JOIN PRODUCTS ON PRODUCTS.ID = ORDERS.PRODUCT_ID " +
            "WHERE USERS.CUSTOMER_ID = :cust_id\n" +
            "      AND ORDERS.CURRENT_STATUS_ID <> 3 /*deactivated status id*/\n" +
            "      AND PRODUCTS.TYPE_ID = 2 /*service id*/\n";
    private final static String DEACTIVATE_ORDER_OF_USER_FOR_PRODUCT_SQL = "UPDATE ORDERS " +
            "SET CURRENT_STATUS_ID = 3 /*deactivated operation status id*/ " +
            "WHERE PRODUCT_ID = :product_id " +
            "      AND USER_ID = :user_id";
    private final static String SUSPEND_ORDER_SQL = "UPDATE ORDERS " +
            "SET CURRENT_STATUS_ID = 2 /*suspended operation status id*/ " +
            "WHERE ID = :id ";
    private final static String ACTIVATE_ORDER_SQL = "UPDATE ORDERS " +
            "SET CURRENT_STATUS_ID = 1 /*activated operation status id*/ " +
            "WHERE ID = :id ";
    private final static String DEACTIVATE_ORDER_SQL = "UPDATE ORDERS " +
            "SET CURRENT_STATUS_ID = 3 /*deactivated operation status id*/ " +
            "WHERE ID = :id ";
    private final static String INSERT_ORDER = "INSERT INTO ORDERS (PRODUCT_ID, USER_ID, CURRENT_STATUS_ID) " +
            "VALUES (:product_id, :user_id, :cur_status_id)";
    private final static String SELECT_ORDER_ID_BY_USER_ID_AND_PRODUCT_ID_SQL = "SELECT Orders.id " +
            " FROM Orders " +
            " JOIN Users ON Orders.user_id = Users.id " +
            " WHERE Orders.current_status_id <>3 " +
            " AND Orders.product_id = :productId " +
            " AND Users.customer_id = (SELECT customer_id FROM Users WHERE id = :userId)";
    private final static String DELETE_ORDER_BY_ID_SQL = "DELETE FROM ORDERS WHERE ID = :id;";
    private final static String SELECT_NOT_DIACTIVATED_ORDER_BY_USER_AND_PRODUCT_SQL = "SELECT * FROM ORDERS WHERE" +
            " PRODUCT_ID = :product_id\n" +
            " AND USER_ID = :user_id\n " +
            " AND CURRENT_STATUS_ID <> 3 /*Deactivated status*/";

    private static final String SELECT_INTERVAL_ORDERS_BY_USER_ID_SQL = "SELECT * FROM( " +
            " SELECT id , " +
            " product_name, " +
            " description, " +
            " product_type, " +
            " current_status_id, " +
            " ROW_NUMBER() OVER (ORDER BY %s) R " +
            " FROM \n" +
            "  (SELECT ORDERS.id id, " +
            "   PRODUCTS.name product_name, \n" +
            "   PRODUCTS.description description, \n" +
            "   PRODUCTS.type_id product_type, \n" +
            "   ORDERS.CURRENT_STATUS_ID current_status_id \n" +
            "   FROM ORDERS JOIN PRODUCTS ON (ORDERS.product_id=PRODUCTS.id) " +
            "   JOIN (SELECT \n" +
            "        OPERATION_DATE, \n" +
            "        ORDER_ID \n" +
            "        FROM OPERATIONS_HISTORY \n" +
            "        WHERE ID IN (SELECT MIN(ID) \n" +
            "                      FROM OPERATIONS_HISTORY \n" +
            "                      GROUP BY ORDER_ID)) OP_HIS ON OP_HIS.order_id = ORDERS.ID \n" +
            "   WHERE USER_ID IN (SELECT id " +
            "                     FROM USERS " +
            "                     WHERE customer_id= " +
            "                      (SELECT customer_id " +
            "                       FROM USERS " +
            "                       WHERE id=:userId)) AND orders.CURRENT_STATUS_ID<>3)) \n" +
            " WHERE R>:start AND R<=:length AND UPPER(product_name) LIKE UPPER(:pattern) ";

    private final String SELECT_LIMITED_ORDERS_DTO_BY_CUSTOMER_ID_SQL = "SELECT * FROM (SELECT\n" +
            "  o.id           order_id,\n" +
            "  p.name,\n" +
            "  p.id           product_id,\n" +
            "  p.type_id      product_type,\n" +
            "  op_status.name operation_status,\n" +
            "  pt.action_date end_date,\n" +
            "  ROW_NUMBER()\n" +
            "        OVER(ORDER BY %s) rnum\n" +
            "FROM ORDERS o\n" +
            "  JOIN PRODUCTS p ON o.PRODUCT_ID = p.ID\n" +
            "  JOIN USERS u ON u.id = o.USER_ID\n" +
            "  JOIN OPERATION_STATUS op_status ON o.CURRENT_STATUS_ID = op_status.id\n" +
            "  LEFT JOIN  PLANNED_TASKS pt ON o.ID = pt.ORDER_ID\n" +
            "WHERE o.CURRENT_STATUS_ID <> 3 /*Deactivation*/ AND u.CUSTOMER_ID = :cust_id\n" +
            "       AND pt.STATUS_ID = 3 /*Deactivation*/  AND LOWER(p.name) LIKE LOWER(:pattern) || '%%')\n" +
            "WHERE rnum < :length AND rnum >= :start";
    private final String SELECT_COUNT_ORDERS_DTO_BY_CUSTOMER_ID_SQL = "SELECT COUNT(*) " +
            "FROM ORDERS o\n" +
            "  JOIN USERS u ON u.id = o.USER_ID\n" +
            "  JOIN PRODUCTS p ON o.PRODUCT_ID = p.ID\n " +
            "  LEFT JOIN  PLANNED_TASKS pt ON o.ID = pt.ORDER_ID\n" +
            "WHERE o.CURRENT_STATUS_ID <> 3 /*Deactivation*/ AND u.CUSTOMER_ID = :cust_id\n" +
            "       AND pt.STATUS_ID = 3 /*Deactivation*/  AND LOWER(p.name) LIKE LOWER(:pattern" +
            "" +
            "" +
            ") || '%%'";
    private static final String SELECT_COUNT_ORDERS_BY_USER_ID_SQL = "SELECT COUNT(ROWNUM) COUNT \n" +
            " FROM ORDERS JOIN PRODUCTS ON (ORDERS.product_id=PRODUCTS.id) " +
            " JOIN (SELECT \n" +
            "       OPERATION_DATE, \n" +
            "       ORDER_ID \n" +
            "       FROM OPERATIONS_HISTORY \n" +
            "       WHERE ID IN (SELECT MIN(ID) \n" +
            "                      FROM OPERATIONS_HISTORY \n" +
            "                      GROUP BY ORDER_ID)) OP_HIS ON OP_HIS.order_id = ORDERS.id \n" +
            " WHERE USER_ID IN (SELECT id " +
            "                   FROM USERS " +
            "                   WHERE customer_id= " +
            "                    (SELECT customer_id " +
            "                     FROM USERS " +
            "                     WHERE id=:userId)) " +
            " AND ORDERS.current_status_id<>3 " +
            " AND upper(PRODUCTS.NAME) LIKE upper(:pattern)";

    private static final String SELECT_ALL_ORDERS_WITHOUT_CSR_SQL = "SELECT * FROM ( \n" +
            " SELECT product_name, " +
            " product_type, " +
            " customer_type, " +
            " order_id, " +
            " TO_CHAR(operation_date,'YYYY-MM-DD') operation_date, " +
            " place, \n" +
            " ROW_NUMBER() OVER (ORDER BY %s) R FROM (\n" +
            "   SELECT  PRODUCTS.name product_name, " +
            "   PRODUCTS.type_id product_type, " +
            "   PRODUCTS.customer_type_id customer_type, \n" +
            "   ORDERS.id order_id, " +
            "   a.operation_date operation_date, " +
            "   PLACES.name place \n" +
            "   FROM ORDERS JOIN \n" +
            "    (SELECT * " +
            "     FROM OPERATIONS_HISTORY " +
            "     WHERE STATUS_ID=4) a " +
            "   ON (ORDERS.id=a.order_id) \n" +
            "   JOIN PRODUCTS ON (ORDERS.product_id=PRODUCTS.id) \n" +
            "   JOIN USERS ON (USERS.id=ORDERS_user_id) " +
            "   JOIN PLACES ON (USERS.place_id=PLACES.id) \n" +
            "   WHERE ORDERS.current_status_id=4 AND ORDERS.csr_id IS NULL)) \n" +
            " WHERE R>:start AND R<=:length AND " +
            " (upper(operation_date) LIKE upper(:pattern) " +
            " OR upper(product_name) LIKE upper(:pattern) OR upper(place) LIKE upper(:pattern))";

    private static final String SELECT_COUNT_ORDERS_WITHOUT_CSR_SQL = "SELECT COUNT(rownum) FROM " +
            " (SELECT  PRODUCTS.name product_name," +
            "  PRODUCTS.type_id, " +
            "  PRODUCTS.customer_type_id customer_type, \n" +
            "  ORDERS.id order_id, " +
            "  TO_CHAR(a.operation_date, 'YYYY-MM-DD') operation_date, " +
            "  PLACES.name place \n" +
            "  FROM ORDERS JOIN \n" +
            "   (SELECT * " +
            "    FROM OPERATIONS_HISTORY " +
            "    WHERE STATUS_ID=4) a " +
            "  ON (ORDERS.id=a.order_id) \n" +
            "  JOIN PRODUCTS ON (ORDERS.product_id=PRODUCTS.id) \n" +
            "  JOIN USERS ON (users.id=orders.user_id) " +
            "  JOIN PLACES ON (users.PLACE_ID=PLACES.ID) \n" +
            "  WHERE ORDERS.current_status_id=4 AND ORDERS.csr_id IS NULL) \n" +
            " WHERE upper(product_name) LIKE upper(:pattern) OR upper(operation_date) LIKE upper(:pattern) " +
            " OR upper(place) LIKE upper(:pattern)";
    private static String SELECT_ORDER_INFO_BY_ORDER_ID_SQL = "SELECT  \n" +
            " PRODUCTS.name product_name, " +
            " PRODUCTS.type_id product_type, " +
            " PRODUCTS.customer_type_id customer_type, \n" +
            " PRODUCTS.description description, " +
            " ORDERS.id order_id, " +
            " TO_CHAR(a.OPERATION_DATE, 'YYYY-MM-DD') operation_date, \n" +
            " PLACES.name place, " +
            " USERS.name user_name, " +
            " USERS.surname user_surname, " +
            " USERS.phone user_phone, " +
            " USERS.address address \n" +
            " FROM ORDERS JOIN \n" +
            "  (SELECT MIN(operation_date) operation_date,order_id " +
            "   FROM OPERATIONS_HISTORY " +
            "   WHERE STATUS_ID=4 GROUP BY order_id) a " +
            " ON (ORDERS.id=a.order_id) \n" +
            "  JOIN PRODUCTS ON (ORDERS.product_id=PRODUCTS.id) \n" +
            "  JOIN USERS ON (USERS.id=ORDERS.user_id) " +
            "  JOIN PLACES ON (USERS.place_id=PLACES.id) \n" +
            " WHERE ORDERS.id=:orderId";

    private final static String UPDATE_CSR_ID_SQL = "UPDATE ORDERS " +
            " SET CSR_ID=:csrId " +
            " WHERE ID=:orderId AND CSR_ID IS NULL";

    private final static String SELECT_PROCESSING_ORDERS_BY_CSR_ID_SQL = "SELECT * FROM ( \n" +
            " SELECT product_name, " +
            " product_type, " +
            " customer_type, " +
            " order_id, \n" +
            " TO_CHAR(operation_date,'YYYY-MM-DD') operation_date," +
            " place, \n" +
            " ROW_NUMBER() OVER (ORDER BY %s) R FROM  ( \n" +
            "  SELECT  PRODUCTS.name product_name, " +
            "  PRODUCTS.type_id product_type, " +
            "  PRODUCTS.customer_type_id customer_type, \n" +
            "  ORDERS.id order_id, " +
            "  a.operation_date operation_date, " +
            "  PLACES.name place \n" +
            "  FROM ORDERS JOIN \n" +
            "    (SELECT MIN(operation_date) operation_date,order_id " +
            "     FROM OPERATIONS_HISTORY WHERE STATUS_ID=4 GROUP BY order_id) a " +
            "  ON (ORDERS.id=a.order_id) \n" +
            "  JOIN PRODUCTS ON (ORDERS.product_id=PRODUCTS.id) \n" +
            "  JOIN USERS ON (USERS.id=ORDERS.user_id) " +
            "  JOIN PLACES ON (USERS.place_id=PLACES.id) \n" +
            "  WHERE ORDERS.CURRENT_STATUS_ID=4 AND ORDERS.csr_id=:csrId)) \n" +
            " WHERE R>:start AND R<=:length AND (upper(operation_date) LIKE upper(:pattern) \n" +
            " OR upper(product_name) LIKE upper(:pattern) OR upper(place) LIKE upper(:pattern))";
    private final static String SELECT_COUNT_PROCESSING_ORDERS_BY_CSR_ID_SQL = "SELECT COUNT(rownum) FROM " +
            " (SELECT  PRODUCTS.name product_name, " +
            "  PRODUCTS.type_id, " +
            "  PRODUCTS.customer_type_id customer_type, \n" +
            "  ORDERS.id order_id, " +
            "  TO_CHAR(a.operation_date, 'YYYY-MM-DD') operation_date, " +
            "  PLACES.name place \n" +
            "  FROM ORDERS JOIN \n" +
            "   (SELECT MIN(operation_date) operation_date,order_id " +
            "    FROM OPERATIONS_HISTORY " +
            "    WHERE STATUS_ID=4 GROUP BY order_id) a " +
            "   ON (ORDERS.id=a.order_id) \n" +
            "  JOIN PRODUCTS ON (ORDERS.product_id=PRODUCTS.id) \n" +
            "  JOIN USERS ON (USERS.id=ORDERS.user_id) " +
            "  JOIN PLACES ON (USERS.place_id=PLACES.id) \n" +
            "  WHERE ORDERS.current_status_id=4 AND ORDERS.csr_id=:csrId) \n" +
            " WHERE upper(product_name) LIKE upper(:pattern) OR upper(operation_date) LIKE upper(:pattern) " +
            " OR upper(place) LIKE upper(:pattern)";
    private final static String SELECT_PROCESSED_ORDERS_BY_CSR_ID_SQL = "SELECT * FROM ( \n" +
            "  SELECT product_name, " +
            "  product_type, " +
            "  customer_type, " +
            "  order_id, \n" +
            "  TO_CHAR(operation_date,'YYYY-MM-DD') operation_date, " +
            "  place, \n" +
            "  ROW_NUMBER() OVER (ORDER BY %s) R FROM  ( \n" +
            "    SELECT  PRODUCTS.name product_name, " +
            "    PRODUCTS.type_id product_type, " +
            "    PRODUCTS.customer_type_id customer_type, \n" +
            "    ORDERS.id order_id, " +
            "    a.operation_date operation_date, " +
            "    PLACES.name place \n" +
            "    FROM ORDERS JOIN \n" +
            "     (SELECT MIN(operation_date) operation_date,order_id " +
            "      FROM OPERATIONS_HISTORY " +
            "      WHERE STATUS_ID=1 GROUP BY ORDER_ID) a " +
            "    ON (ORDERS.id=a.order_id) \n" +
            "    JOIN PRODUCTS ON (ORDERS.product_id=PRODUCTS.id) \n" +
            "    JOIN USERS ON (USERS.id=ORDERS.user_id) " +
            "    JOIN PLACES ON (USERS.place_id=PLACES.id) \n" +
            "    WHERE  ORDERS.csr_id=:csrId)) \n" +
            " WHERE R>:start AND R<=:length AND (upper(operation_date) LIKE upper(:pattern) \n" +
            " OR upper(product_name) LIKE upper(:pattern) OR upper(place) LIKE upper(:pattern))";

    private static final String SELECT_COUNT_PROCESSED_ORDERS_BY_CSR_ID_SQL = "SELECT COUNT(rownum) FROM " +
            " (SELECT  PRODUCTS.name product_name, " +
            "  PRODUCTS.type_id, " +
            "  PRODUCTS.customer_type_id customer_type, \n" +
            "  ORDERS.id order_id, " +
            "  TO_CHAR(a.operation_date, 'YYYY-MM-DD') operation_date, " +
            "  PLACES.name place \n" +
            "  FROM ORDERS JOIN \n" +
            "    (SELECT MIN(operation_date) operation_date,order_id " +
            "     FROM OPERATIONS_HISTORY " +
            "     WHERE STATUS_ID=1 GROUP BY ORDER_ID) a " +
            "  ON (ORDERS.id=a.order_id) \n" +
            "  JOIN PRODUCTS ON (ORDERS.product_id=PRODUCTS.id) \n" +
            "  JOIN USERS ON (USERS.id=ORDERS.user_id) " +
            "  JOIN PLACES ON (USERS.place_id=PLACES.id) \n" +
            "  WHERE ORDERS.csr_id=:csrId) \n" +
            " WHERE upper(product_name) LIKE upper(:pattern) OR " +
            " upper(operation_date) LIKE upper(:pattern) OR upper(place) LIKE upper(:pattern)";

    private final static String SELECT_BY_ID = "SELECT * FROM ORDERS WHERE id =  :id";

    @Override
    public Order getById(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ID, id);
        return jdbcTemplate.queryForObject(SELECT_BY_ID, params, orderRowMapper);
    }

    @Override
    public boolean update(Order object) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    public boolean save(Order order) {
        MapSqlParameterSource paramsForOrder = new MapSqlParameterSource();
        paramsForOrder.addValue(PRODUCT_ID, order.getProductId());
        paramsForOrder.addValue(USER_ID, order.getUserId());
        paramsForOrder.addValue(CUR_STATUS_ID, order.getCurrentStatus().getId());
        return jdbcTemplate.update(INSERT_ORDER, paramsForOrder) > 0;
    }

    /**
     * {@inheritDoc}
     */
    public Integer saveAndGetGeneratedId(Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource paramsForOrder = new MapSqlParameterSource();
        paramsForOrder.addValue(PRODUCT_ID, order.getProductId());
        paramsForOrder.addValue(USER_ID, order.getUserId());
        paramsForOrder.addValue(CUR_STATUS_ID, order.getCurrentStatus().getId());
        jdbcTemplate.update(INSERT_ORDER, paramsForOrder, keyHolder, new String[]{"ID"});
        return new Integer(keyHolder.getKey().intValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Order order) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ID, order.getId());
        return jdbcTemplate.update(DELETE_ORDER_BY_ID_SQL, params) > 0;
    }

    /**
     * {@inheritDoc}
     *
     */
    public List<Order> getOrdersByCustomerId(Integer customerId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(CUST_ID, customerId);
        return jdbcTemplate.query(SELECT_ORDERS_BY_CUST_ID_SQL, params, new OrderRowMapper());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deactivateOrderOfUserForProduct(Integer productId, Integer userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PRODUCT_ID, productId);
        params.addValue(USER_ID, userId);
        return jdbcTemplate.update(DEACTIVATE_ORDER_OF_USER_FOR_PRODUCT_SQL, params) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OrdersRowDTO> getLimitedOrderRowsDTOByCustomerId(Integer start, Integer length, String search, String sort, Integer customerId) {
        if (sort.isEmpty()) {
            sort = "p.name";
        }
        String query = String.format(SELECT_LIMITED_ORDERS_DTO_BY_CUSTOMER_ID_SQL, sort);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(START, start);
        params.addValue(LENGTH, length + 1);
        params.addValue(CUST_ID, customerId);
        params.addValue(PATTERN, search);
        return jdbcTemplate.query(query, params, new OrdersRowDTORowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getCountOrdersByCustomerId(String search, String sort, Integer customerId) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(CUST_ID, customerId);
        params.addValue(PATTERN, search);
        return jdbcTemplate.queryForObject(SELECT_COUNT_ORDERS_DTO_BY_CUSTOMER_ID_SQL, params, Integer.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean suspendOrder(Integer orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ID, orderId);
        return jdbcTemplate.update(SUSPEND_ORDER_SQL, params) > 0;

    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deactivateOrder(Integer orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ID, orderId);
        return jdbcTemplate.update(DEACTIVATE_ORDER_SQL, params) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean activateOrder(Integer orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ID, orderId);
        return jdbcTemplate.update(ACTIVATE_ORDER_SQL, params) > 0;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getOrderIdByUserIdAndProductId(Integer userId, Integer productId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USERID, userId);
        params.addValue(PRODUCTID, productId);
        try {
            return jdbcTemplate.queryForObject(SELECT_ORDER_ID_BY_USER_ID_AND_PRODUCT_ID_SQL, params, Integer.class);
        } catch (DataAccessException e) {
            logger.debug("There are no user`s orders with such params.");
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getCountOrdersByUserId(Integer userId, String search) {
        MapSqlParameterSource params = new MapSqlParameterSource(USERID, userId);
        params.addValue(PATTERN, "%" + search + "%");
        return jdbcTemplate.queryForObject(SELECT_COUNT_ORDERS_BY_USER_ID_SQL, params, Integer.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FullInfoOrderDTO> getIntervalOrdersByUserId(int start, int length, String sort, String search, int userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (sort.isEmpty()) {
            sort = ID;
        }
        params.addValue(START, start);
        params.addValue(LENGTH, length);
        params.addValue(PATTERN, "%" + search + "%");
        params.addValue(USERID, userId);
        String sql = String.format(SELECT_INTERVAL_ORDERS_BY_USER_ID_SQL, sort);
        List<FullInfoOrderDTO> orders = jdbcTemplate.query(sql, params, (resultSet, rownum) -> {
            String name = resultSet.getString(PRODUCT_NAME);
            Integer orderId = resultSet.getInt(ID);
            ProductType productType = ProductType.getProductTypeFromId(resultSet.getInt(PRODUCT_TYPE));
            OperationStatus operationStatus = OperationStatus.getOperationStatusFromId(resultSet.getInt(CURRENT_STATUS_ID));
            String description = resultSet.getString(DESCRIPTION);
            return new FullInfoOrderDTO(orderId, name, description, productType, operationStatus);
        });
        return orders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getCountOrdersWithoutCsr(String search) {
        MapSqlParameterSource params = new MapSqlParameterSource(PATTERN, "%" + search + "%");
        return jdbcTemplate.queryForObject(SELECT_COUNT_ORDERS_WITHOUT_CSR_SQL, params, Integer.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FullInfoOrderDTO> getIntervalOrdersWithoutCsr(int start, int length, String sort, String search) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (sort.isEmpty()) {
            sort = ORDER_ID;
        }
        params.addValue(START, start);
        params.addValue(LENGTH, length);
        params.addValue(PATTERN, "%" + search + "%");
        String sql = String.format(SELECT_ALL_ORDERS_WITHOUT_CSR_SQL, sort);
        return jdbcTemplate.query(sql, params, new FullInfoOrderDTORowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FullInfoOrderDTO getOrderInfoByOrderId(Integer orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource(ORDERID, orderId);
        return jdbcTemplate.queryForObject(SELECT_ORDER_INFO_BY_ORDER_ID_SQL, params, (rs, rownum) -> {
            FullInfoOrderDTO order = new FullInfoOrderDTO();
            order.setProductName(rs.getString(PRODUCT_NAME));
            order.setDescription(rs.getString(DESCRIPTION));
            order.setProductType(ProductType.getProductTypeFromId(rs.getInt(PRODUCT_TYPE)));
            order.setCustomerType(CustomerType.getCustomerTypeFromId(rs.getInt(CUSTOMER_TYPE)));
            order.setOrderId(rs.getInt(ORDER_ID));
            order.setActionDate(rs.getString(OPERATION_DATE));
            order.setPlace(rs.getString(PLACE));
            order.setUserName(rs.getString(USER_NAME));
            order.setUserSurname(rs.getString(USER_SURNAME));
            order.setPhone(rs.getString(PHONE));
            order.setAddress(rs.getString(ADDRESS));
            return order;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean assignToUser(int csrId, int orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(CSR_ID, csrId);
        params.addValue(ORDERID, orderId);
        return jdbcTemplate.update(UPDATE_CSR_ID_SQL, params) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getCountOfInprocessingOrdersByCsrId(int csrId, String search) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PATTERN, "%" + search + "%");
        params.addValue(CSR_ID, csrId);
        return jdbcTemplate.queryForObject(SELECT_COUNT_PROCESSING_ORDERS_BY_CSR_ID_SQL, params, Integer.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FullInfoOrderDTO> getIntervalProcessingOrdersByCsrId(int start, int length, String sort, String search, int csrId) {
        if (sort.isEmpty()) {
            sort = ORDER_ID;
        }
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(START, start);
        params.addValue(LENGTH, length);
        params.addValue(PATTERN, "%" + search + "%");
        params.addValue(CSR_ID, csrId);
        String sql = String.format(SELECT_PROCESSING_ORDERS_BY_CSR_ID_SQL, sort);
        return jdbcTemplate.query(sql, params, new FullInfoOrderDTORowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FullInfoOrderDTO> getIntervalProccesedOrdersByCsrId(int start, int length, String sort, String search, int csrId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (sort.isEmpty()) {
            sort = ORDER_ID;
        }
        params.addValue(START, start);
        params.addValue(LENGTH, length);
        params.addValue(PATTERN, "%" + search + "%");
        params.addValue(CSR_ID, csrId);
        String sql = String.format(SELECT_PROCESSED_ORDERS_BY_CSR_ID_SQL, sort);
        return jdbcTemplate.query(sql, params, new FullInfoOrderDTORowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getCountOfProcessedOrdersByCsrId(int csrId, String search) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PATTERN, "%" + search + "%");
        params.addValue(CSR_ID, csrId);
        return jdbcTemplate.queryForObject(SELECT_COUNT_PROCESSED_ORDERS_BY_CSR_ID_SQL, params, Integer.class);
    }


}

