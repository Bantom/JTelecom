package jtelecom.dao.complaint;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * This class implements methods from ComplaintDAO.
 *
 * @author Revniuk Aleksandr
 */
@Repository
public class ComplaintDAOImpl implements ComplaintDAO {

    private final static String SELECT_ALL_BY_ORDER_ID_SQL = "SELECT * FROM COMPLAINTS WHERE ORDER_ID = :orderId";
    private final static String SELECT_ALL_BY_STATUS_ID_SQL = "SELECT * FROM COMPLAINTS WHERE STATUS_ID = :statusId";
    private final static String SET_PMG_ID_SQL = "UPDATE COMPLAINTS SET PMG_ID = :pmgId WHERE ID = :id AND PMG_ID IS NULL";
    private final static String SELECT_INTERVAL_WHERE_PMG_ID_IS_NULL_SORTED_BY_DATE_SQL = "SELECT * FROM\n" +
            "  (SELECT ID, ORDER_ID, CREATING_DATE, STATUS_ID, DESCRIPTION, PMG_ID, ROW_NUMBER() OVER (ORDER BY CREATING_DATE) R FROM COMPLAINTS WHERE PMG_ID IS NULL)\n" +
            "WHERE R > :startIndex AND R <= :endIndex";
    private final static String SELECT_INTERVAL_BY_PMG_ID_SQL = "SELECT * FROM\n" +
            "  (SELECT ID, ORDER_ID, CREATING_DATE, STATUS_ID, DESCRIPTION, PMG_ID, ROW_NUMBER() OVER (ORDER BY CREATING_DATE) R FROM COMPLAINTS WHERE PMG_ID = :pmgId)\n" +
            "WHERE R > :startIndex AND R <= :endIndex";
    private final static String SELECT_BY_ID_SQL = "SELECT * FROM COMPLAINTS WHERE ID = :id";
    private final static String INSERT_COMPLAINT_SQL = "INSERT INTO Complaints(ORDER_ID, CREATING_DATE, STATUS_ID, DESCRIPTION) \n" +
            "VALUES(:orderId, :creatingDate, :statusId, :description)";
    private final static String UPDATE_STATUS_ID_SQL = "UPDATE COMPLAINTS SET STATUS_ID = :statusId WHERE ID = :id";
    private final static String UPDATE_DESCRIPTION_SQL = "UPDATE COMPLAINTS SET DESCRIPTION = :description WHERE ID = :id";
    private final static String SELECT_ALL_BY_PLACE_ID_SQL = "SELECT " +
            "   COMPLAINTS.ID, \n" +
            "   COMPLAINTS.ORDER_ID, \n" +
            "   COMPLAINTS.PMG_ID, \n" +
            "   COMPLAINTS.CREATING_DATE, \n" +
            "   COMPLAINTS.STATUS_ID, \n" +
            "   COMPLAINTS.DESCRIPTION\n" +
            "FROM COMPLAINTS\n" +
            "   INNER JOIN ORDERS ON ORDERS.ID = COMPLAINTS.ORDER_ID\n" +
            "   INNER JOIN USERS ON USERS.ID = ORDERS.USER_ID\n" +
            "   INNER JOIN PLACES ON USERS.PLACE_ID = PLACES.ID\n" +
            "   WHERE PLACES.ID = :placeId";
    private final static String COUNT_COMPLAINTS_WHERE_PMG_ID_IS_NULL_SQL = "SELECT COUNT(ID)\n" +
            "  FROM COMPLAINTS\n" +
            "WHERE PMG_ID is NULL";
    private final static String COUNT_COMPLAINTS_BY_PMG_ID_SQL = "SELECT COUNT(ID)\n" +
            "  FROM COMPLAINTS\n" +
            "WHERE PMG_ID = :pmgId";

    @Resource
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Resource
    private ComplaintRowMapper complaintRowMapper;
    private static final String ID = "id";
    private static final String CREATION_DATE = "creatingDate";
    private static final String DESCRIPTION = "description";
    private static final String STATUS_ID = "statusId";
    private static final String PMG_ID = "pmgId";
    private static final String ORDER_ID = "orderId";
    private static final String START_INDEX = "startIndex";
    private static final String END_INDEX = "endIndex";
    private static final String PLACE_ID = "placeId";


    /**
     * This method returns complaint by id.
     *
     * @param id id of complaint
     * @return complaint
     */
    @Override
    public Complaint getById(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ID, id);
        return jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, params, complaintRowMapper);
    }

    @Override
    public boolean update(Complaint object) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method save complaint.
     *
     * @param object object of complaint
     * @return <code>true</code> if operation was successful, <code>false</code> otherwise.
     */
    @Override
    public boolean save(Complaint object) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORDER_ID, object.getOrderId());
        params.addValue(CREATION_DATE, object.getCreationDate());
        params.addValue(STATUS_ID, object.getStatus().getId());
        params.addValue(DESCRIPTION, object.getDescription());
        return jdbcTemplate.update(INSERT_COMPLAINT_SQL, params) > 0;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer saveComplaint(Complaint complaint) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORDER_ID, complaint.getOrderId());
        params.addValue(CREATION_DATE, complaint.getCreationDate());
        params.addValue(STATUS_ID, complaint.getStatus().getId());
        params.addValue(DESCRIPTION, complaint.getDescription());
        KeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(INSERT_COMPLAINT_SQL, params, key, new String[]{"ID"});
        return key.getKey().intValue();
    }

    @Override
    public boolean delete(Complaint object) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Complaint> getIntervalOfAssignedComplaints(int pmgId, int startIndex, int endIndex) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(START_INDEX, startIndex);
        params.addValue(END_INDEX, endIndex);
        params.addValue(PMG_ID, pmgId);
        return jdbcTemplate.query(SELECT_INTERVAL_BY_PMG_ID_SQL, params, complaintRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countAssignedComplaintsToUser(int pmgId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PMG_ID, pmgId);
        return jdbcTemplate.queryForObject(COUNT_COMPLAINTS_BY_PMG_ID_SQL, params, Integer.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Complaint> getByOrderId(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORDER_ID, id);
        return jdbcTemplate.query(SELECT_ALL_BY_ORDER_ID_SQL, params, complaintRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Complaint> getIntervalOfUnassignedComplaints(int startIndex, int endIndex) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(START_INDEX, startIndex);
        params.addValue(END_INDEX, endIndex);
        return jdbcTemplate.query(SELECT_INTERVAL_WHERE_PMG_ID_IS_NULL_SORTED_BY_DATE_SQL, params, complaintRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Complaint> getByPlaceId(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PLACE_ID, id);
        return jdbcTemplate.query(SELECT_ALL_BY_PLACE_ID_SQL, params, complaintRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Complaint> getByStatusID(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(STATUS_ID, id);
        return jdbcTemplate.query(SELECT_ALL_BY_STATUS_ID_SQL, params, complaintRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean assignToUser(int complaintId, int pmgId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PMG_ID, pmgId);
        params.addValue(ID, complaintId);
        return jdbcTemplate.update(SET_PMG_ID_SQL, params) > 0;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean changeStatus(int complaintId, int statusId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(STATUS_ID, statusId);
        params.addValue(ID, complaintId);
        return jdbcTemplate.update(UPDATE_STATUS_ID_SQL, params) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean changeDescription(int complaintId, String description) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(DESCRIPTION, description);
        params.addValue(ID, complaintId);
        return jdbcTemplate.update(UPDATE_DESCRIPTION_SQL, params) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countUnassignedComplaintsToUser() {
        return jdbcTemplate.queryForObject(COUNT_COMPLAINTS_WHERE_PMG_ID_IS_NULL_SQL, new MapSqlParameterSource(), Integer.class);
    }

}
