package jtelecom.dao.plannedTask;


import jtelecom.dao.entity.OperationStatus;
import jtelecom.dto.PlannedTaskDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * @author Yuliya Pedash
 * @since 08.05.2017.
 */
@Service
public class PlannedTaskDAOImpl implements PlannedTaskDAO {
    private final static String INSERT_PLANNED_TASK_SQL = "INSERT INTO PLANNED_TASKS(STATUS_ID, ORDER_ID, ACTION_DATE)\n" +
            "    VALUES (:status_id, :order_id, TO_DATE(:date, 'YYYY-MM-DD'))";
    private final static String SELECT_PLANNED_TASKS_BY_DATES_SQL = "SELECT * FROM PLANNED_TASKS WHERE\n" +
            "ORDER_ID = :order_id AND " +
            "  ACTION_DATE BETWEEN  TO_DATE(:begin_date, 'YYYY-MM-DD') AND TO_DATE(:end_date, 'YYYY-MM-DD') ";
    private final static String DELETE_PLANNED_TASKS_FOR_ORDER_OF_PRODUCT_FOR_USER_SQL = "DELETE FROM PLANNED_TASKS\n" +
            "  WHERE ORDER_ID IN (SELECT  MAX(ID) FROM ORDERS\n" +
            "  WHERE PRODUCT_ID = :product_id AND USER_ID = :user_id\n" +
            "  AND CURRENT_STATUS_ID <> 3 /*Deactivated*/)";
    private final static String DELETE_NEXT_PLANNED_TASK_FOR_ACTIVATION_OF_ORDER_USER_SQL = "DELETE FROM PLANNED_TASKS\n" +
            "WHERE ACTION_DATE = (SELECT MIN(ACTION_DATE) FROM PLANNED_TASKS\n" +
            "WHERE ORDER_ID = :order_id AND STATUS_ID = 1/*Active*/) AND ORDER_ID = :order_id AND STATUS_ID = 1 /*Active*/";
    private final static String SELECT_INTERVAL_PLANNED_TASKS_DTO_FOR_USERS_ORDERS_SQL = "SELECT * FROM (SELECT p.id, p.STATUS_ID, p.ORDER_ID, p.ACTION_DATE, pr.NAME, ROW_NUMBER() OVER (ORDER BY p.ACTION_DATE) rnum\n FROM PLANNED_TASKS p\n" +
            "  INNER JOIN ORDERS o ON p.ORDER_ID = o.ID\n" +
            "  INNER JOIN PRODUCTS pr ON pr.ID = o.PRODUCT_ID\n" +
            "WHERE o.USER_ID = :user_id ORDER BY p.ACTION_DATE)\n" +
            "WHERE rnum < :end AND rnum >=:start\n";
    private final static String SELECT_COUNT_PLANNED_TASKS_FOR_USERS_ORDERS_SQL = "SELECT COUNT(*) FROM PLANNED_TASKS p\n" +
            "INNER JOIN ORDERS o ON p.ORDER_ID = o.ID\n" +
            "WHERE o.USER_ID = :user_id";
    private final static String DELETE_PLANNED_TASK_BY_ID_SQL = "DELETE FROM PLANNED_TASKS WHERE ID = :id";
    private final static String DELETE_ACTIVATION_PLANNED_TASK_FOR_SUSPENSE_SQL = "DELETE FROM PLANNED_TASKS WHERE ID = (SELECT MAX(ID) FROM PLANNED_TASKS WHERE\n" +
            "ORDER_ID = :order_id AND STATUS_ID = 1  /*Activate*/\n" +
            "AND ID < :id)";
    private final static String SELECT_BY_ID_SQL = "SELECT * FROM PLANNED_TASKS WHERE  ID = :id ";
    private final static String DELETE_PLANNED_TASKS_FOR_ORDER = "DELETE FROM PLANNED_TASKS WHERE ORDER_ID = :order_id";
    private final static String SELECT_DEACT_PLANNED_TASK = "SELECT * FROM  PLANNED_TASKS WHERE ORDER_ID = :id AND STATUS_ID = 3 /*Deactivation*/\n";
    private static final String ID = "id";
    private static final String PRODUCT_ID = "product_id";
    private static final String USER_ID = "user_id";
    private static final String STATUS_ID = "status_id";
    private static final String ORDER_ID = "order_id";
    private static final String BEGIN_DATE = "begin_date";
    private static final String END_DATE = "end_date";
    private static final String START = "start";
    private static final String END = "end";
    private static final String DATE = "date";
    @Resource
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    PlannedTaskRowMapper plannedTaskRowMapper;

    @Override
    public PlannedTask getById(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ID, id);
        return jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, params, plannedTaskRowMapper);
    }

    @Override
    public boolean update(PlannedTask object) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    public boolean save(PlannedTask plannedTask) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(STATUS_ID, plannedTask.getStatus().getId());
        params.addValue(ORDER_ID, plannedTask.getOrderId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        params.addValue(DATE, dateFormat.format(plannedTask.getActionDate().getTime()));
        return jdbcTemplate.update(INSERT_PLANNED_TASK_SQL, params) > 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean deleteAllPlannedTasksForProductOfUser(Integer productId, Integer userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PRODUCT_ID, productId);
        params.addValue(USER_ID, userId);
        return jdbcTemplate.update(DELETE_PLANNED_TASKS_FOR_ORDER_OF_PRODUCT_FOR_USER_SQL, params) > 0;
    }

    @Override
    public boolean delete(PlannedTask object) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlannedTask> getAllPlannedTaskForDates(Calendar beginDate, Calendar endDate, Integer orderId) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(BEGIN_DATE, simpleDateFormat.format(beginDate.getTime()));
        params.addValue(END_DATE, simpleDateFormat.format(endDate.getTime()));
        params.addValue(ORDER_ID, orderId);
        return jdbcTemplate.query(SELECT_PLANNED_TASKS_BY_DATES_SQL, params, plannedTaskRowMapper);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteNextPlannedTaskForActivationForThisOrder(Integer orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORDER_ID, orderId);
        return jdbcTemplate.update(DELETE_NEXT_PLANNED_TASK_FOR_ACTIVATION_OF_ORDER_USER_SQL, params) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlannedTaskDTO> getLimitedPlannedTasksForUsersOrders(Integer userId, Integer start, Integer end) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        params.addValue(START, start + 1);
        params.addValue(END, end + 1);
        return jdbcTemplate.query(SELECT_INTERVAL_PLANNED_TASKS_DTO_FOR_USERS_ORDERS_SQL, params, (rs, rownum) -> {
            PlannedTaskDTO plannedTaskDTO = new PlannedTaskDTO();
            plannedTaskDTO.setProductName(rs.getString("name"));
            Date date = rs.getDate("action_date");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            plannedTaskDTO.setActionDate(simpleDateFormat.format(date));
            plannedTaskDTO.setId(rs.getInt(ID));
            plannedTaskDTO.setStatus(OperationStatus.getOperationStatusFromId(rs.getInt("status_id")));
            return plannedTaskDTO;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getCountPlannedTasksForUserOrders(Integer userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        return jdbcTemplate.queryForObject(SELECT_COUNT_PLANNED_TASKS_FOR_USERS_ORDERS_SQL, params, Integer.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deletePlannedTaskById(Integer plannedTaskId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ID, plannedTaskId);
        return jdbcTemplate.update(DELETE_PLANNED_TASK_BY_ID_SQL, params) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deletePlannedTaskForActivationOfThisSuspense(PlannedTask suspensePlannedTask) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ID, suspensePlannedTask.getId());
        params.addValue(ORDER_ID, suspensePlannedTask.getOrderId());
        return jdbcTemplate.update(DELETE_ACTIVATION_PLANNED_TASK_FOR_SUSPENSE_SQL, params) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteAllPlannedTasksForOrder(Integer orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORDER_ID, orderId);
        return jdbcTemplate.update(DELETE_PLANNED_TASKS_FOR_ORDER, params) > 0;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public PlannedTask getDeactivationPlannedTaskForOrder(Integer orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ID, orderId);
        return jdbcTemplate.queryForObject(SELECT_DEACT_PLANNED_TASK, params, plannedTaskRowMapper);
    }
}
