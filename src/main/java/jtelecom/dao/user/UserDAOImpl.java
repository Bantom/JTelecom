package jtelecom.dao.user;

import jtelecom.dao.entity.CustomerType;
import jtelecom.security.Md5PasswordEncoder;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Anna Rysakova
 * @author Petr Moiseienko
 */
@Service
public class UserDAOImpl implements UserDAO {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String SURNAME = "surname";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String ADDRESS = "address";
    private static final String PASSWORD = "password";
    private static final String ROLE = "role";
    private static final String USERNAME = "username";

    private static final String CUSTOMER_ID = "customerId";
    private static final String ROLE_ID = "roleId";
    private static final String ENABLE = "enable";
    private static final String PLACE_ID = "placeId";
    private static final String PRODUCT_ID = "productId";
    private static final String ORDER_ID = "orderId";
    private static final String PLACE = "place";
    private static final String STATUS = "status";
    private static final String TYPE_ID = "typeId";
    private static final String COMPLAINT_ID = "complaintId";

    private static final String FIRST_INDEX = "start";
    private static final String LAST_INDEX = "end";
    private final static String PATTERN = "pattern";

    private final static String SELECT_BY_USERNAME = "SELECT * " +
            "FROM AUTHORITIES " +
            "WHERE USERNAME=:username AND ENABLE=1";
    private final static String SELECT_ROLE = "SELECT id FROM ROLES WHERE NAME=:name";
    private final static String INSERT_USER_SQL = "INSERT INTO USERS " +
            " (NAME,SURNAME,EMAIL,PHONE,PASSWORD,ADDRESS,ROLE_ID,PLACE_ID,CUSTOMER_ID,ENABLE) " +
            " VALUES(:name,:surname,:email,:phone,:password, :address, :roleId, :placeId, :customerId, :enable)";
    private final static String SELECT_PLACE_ID = "SELECT ID FROM PLACES WHERE NAME=:place";
    private final static String SELECT_BY_EMAIL_OR_PHONE_SQL = "SELECT EMAIL " +
            " FROM USERS " +
            " WHERE upper(EMAIL)=upper(:email) " +
            " OR PHONE=:phone";
    private final static String SELECT_USER_BY_EMAIL_SQL = "SELECT" +
            "  USERS.ID," +
            "  USERS.NAME," +
            "  USERS.SURNAME," +
            "  USERS.EMAIL," +
            "  USERS.PHONE," +
            "  USERS.PASSWORD," +
            "  USERS.ADDRESS," +
            "  USERS.ROLE_ID," +
            "  USERS.PLACE_ID," +
            "  USERS.CUSTOMER_ID," +
            "  USERS.ENABLE" +
            "    FROM USERS" +
            "  WHERE EMAIL=:email";

    private final static String UPDATE_USER_SQL = "UPDATE USERS " +
            " SET NAME=:name, " +
            " SURNAME=:surname, " +
            " PHONE=:phone, " +
            " ENABLE= :enable, " +
            " PASSWORD= :password, " +
            " ADDRESS= :address " +
            " WHERE ID=:id";
    private final static String SELECT_USER_BY_ID = "SELECT * FROM USERS WHERE ID=:id";
    private final static String SELECT_LIMITED_USERS_SQL = "SELECT * \n" +
            " FROM ( SELECT a.*, rownum rnum\n" +
            " FROM ( SELECT * from USERS " +
            " WHERE ENABLE=1 AND CUSTOMER_ID IS NOT NULL AND " +
            " (upper(name) LIKE upper(:pattern) " +
            " OR upper(surname) LIKE upper(:pattern) " +
            " OR upper(email) LIKE upper(:pattern) " +
            " OR upper(phone) LIKE upper(:pattern) " +
            " OR upper(address) LIKE upper(:pattern) )" +
            " ORDER BY %s) a\n" +
            "       WHERE rownum <= :end )\n" +
            "       WHERE rnum > :start";

    private static final String SELECT_COUNT_SQL = "SELECT COUNT(id)\n" +
            "  FROM USERS " +
            "WHERE ENABLE=1 AND CUSTOMER_ID IS NOT NULL AND " +
            " ( upper(name) LIKE upper(:pattern) " +
            " OR upper(surname) LIKE upper(:pattern) " +
            " OR upper(email) LIKE upper(:pattern) " +
            " OR upper(phone) LIKE upper(:pattern) " +
            " OR upper(address) LIKE upper(:pattern) )";

    private static final String SELECT_ALL_COUNT_SQL = "SELECT COUNT(id)\n" +
            "  FROM USERS " +
            "WHERE upper(name) LIKE upper(:pattern) " +
            " OR upper(surname) LIKE upper(:pattern) " +
            " OR upper(email) LIKE upper(:pattern) " +
            " OR upper(phone) LIKE upper(:pattern) " +
            " OR upper(address) LIKE upper(:pattern) ";

    private static final String SELECT_ALL_COUNT_OF_CUSTOMER = "Select count(ID)\n" +
            "  from Users " +
            "WHERE CUSTOMER_ID= :customerId AND ( " +
            " upper(name) like upper(:pattern) " +
            " OR upper(surname) like upper(:pattern) " +
            " OR upper(email) like upper(:pattern) " +
            " OR upper(phone) like upper(:pattern) " +
            " OR upper(address) like upper(:pattern)) ";


    private static final String SELECT_LIMITED_ALL_USERS_SQL = "SELECT *\n" +
            " FROM (SELECT a.*, rownum rnum\n" +
            "       FROM (SELECT * " +
            "             FROM USERS " +
            "             WHERE upper(name) LIKE upper(:pattern) " +
            "             OR upper(surname) LIKE upper(:pattern) " +
            "             OR upper(email) LIKE upper(:pattern) " +
            "             OR upper(phone) LIKE upper(:pattern) " +
            "             OR upper(address) LIKE upper(:pattern) " +
            "             ORDER BY %s) a\n" +
            "       WHERE rownum <= :end )\n" +
            "  WHERE rnum > :start";
    private static final String SELECT_LIMITED_ALL_USERS_OF_CUSTOMER = "select *\n" +
            "from ( select a.*, rownum rnum\n" +
            "       from ( Select * from USERS " +
            " Where customer_id= :customerId and ( " +
            " upper(name) like upper(:pattern) " +
            " OR upper(surname) like upper(:pattern) " +
            " OR upper(email) like upper(:pattern) " +
            " OR upper(phone) like upper(:pattern) " +
            " OR upper(address) like upper(:pattern)) " +
            " ORDER BY %s) a\n" +
            "       where rownum <= :end )\n" +
            "       where rnum > :start";

    private static final String SELECT_COUNT_EMPLOYEES_BY_CUSTOMER_SQL = "SELECT COUNT(ID)\n" +
            " FROM Users " +
            " WHERE ROLE_ID=6 AND CUSTOMER_ID=:customerId " +
            " AND ( upper(name) LIKE upper(:pattern) " +
            " OR upper(surname) LIKE upper(:pattern) " +
            " OR upper(email) LIKE upper(:pattern) " +
            " OR upper(phone) LIKE upper(:pattern) " +
            " OR upper(address) LIKE upper(:pattern) )";

    private static final String SELECT_EMPLOYEES_BY_CUSTOMER_SQL = "SELECT *" +
            " FROM (SELECT a.*, rownum rnum\n" +
            "       FROM (SELECT * " +
            "             FROM USERS " +
            "             WHERE ROLE_ID=6 AND CUSTOMER_ID=:customerId " +
            "             AND (upper(name) LIKE upper(:pattern) " +
            "             OR upper(surname) LIKE upper(:pattern) " +
            "             OR upper(email) LIKE upper(:pattern) " +
            "             OR upper(phone) LIKE upper(:pattern) " +
            "             OR upper(address) LIKE upper(:pattern)) " +
            "             ORDER BY %s) a\n" +
            "       WHERE rownum <= :end )\n" +
            " WHERE rnum > :start";

    private static final String SELECT_USER_BY_COMPLAINT_ID_SQL = "SELECT * " +
            " FROM USERS " +
            " WHERE ID=( " +
            "  SELECT USER_ID " +
            "  FROM ORDERS JOIN COMPLAINTS ON (ORDERS.ID=COMPLAINTS.ORDER_ID) " +
            "  WHERE COMPLAINTS.ID=:complaintId)";

    private static final String SELECT_USER_BY_ORDER_ID_SQL = "SELECT * " +
            " FROM USERS " +
            " WHERE ID=( " +
            "  SELECT USER_ID " +
            "  FROM ORDERS " +
            "  WHERE ID=:orderId)";

    private static final String SELECT_USERS_BY_CUSTOMER_TYPE_SQL = "SELECT * " +
            " FROM USERS " +
            " WHERE CUSTOMER_ID IN ( " +
            "  SELECT ID " +
            "  FROM CUSTOMERS " +
            "  WHERE TYPE_ID=:typeId)";

    private static final String SELECT_USER_BY_PRODUCT_ID_SQL = "SELECT * " +
            " FROM USERS " +
            " WHERE ID IN ( " +
            "   SELECT USER_ID " +
            "   FROM ORDERS WHERE PRODUCT_ID=:productId OR PRODUCT_ID IN ( " +
            "     SELECT TARIFF_ID " +
            "     FROM TARIFF_SERVICES " +
            "     WHERE SERVICE_ID=:productId))";

    private static final String UPDATE_ENABLE_OR_DISABLE_SQL = "UPDATE USERS " +
            " SET ENABLE=:status " +
            " WHERE ID=:id";

    private static final String UPDATE_PASSWORD_SQL = "UPDATE USERS " +
            " SET PASSWORD=:password " +
            " WHERE ID=:id";

    @Resource
    private NamedParameterJdbcTemplate jdbcTemplate;

    private Md5PasswordEncoder encoder = new Md5PasswordEncoder();

    /**
     * {@inheritDoc}
     */
    @Override
    public User findByUsername(String username) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USERNAME, username);
        List<User> users = jdbcTemplate.query(SELECT_BY_USERNAME, params, (rs, rowNum) -> {
            String userName = rs.getString(USERNAME);
            String password = rs.getString(PASSWORD);
            String authorities = rs.getString(ROLE);
            return new User(userName, password, authorities);
        });
        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public User getById(int id) {
        return null;
    }

    @Override
    public boolean delete(User object) {
        return false;
    }

    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public Integer findRole(String name) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(NAME, name);
        List<Integer> id = jdbcTemplate.query(SELECT_ROLE, params, (rs, rowNum) -> rs.getInt(ID));
        return id.isEmpty() ? null : id.get(0);
    }

    @Override
    public Integer findPlaceId(String name) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PLACE, name);
        List<Integer> id = jdbcTemplate.query(SELECT_PLACE_ID, params, (rs, rowNum) -> rs.getInt(ID));
        return id.isEmpty() ? null : id.get(0);
    }

    /**
     * This method finds user by his email
     * created by Yuliya Pedash
     *
     * @param email email of user
     * @return found user
     */
    @Override
    public User findByEmail(String email) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(EMAIL, email);
        return jdbcTemplate.queryForObject(SELECT_USER_BY_EMAIL_SQL, params, new UserRowMapper());
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Integer getCountUsersWithSearchOfCustomer(String search, int custID) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PATTERN, "%" + search + "%");
        params.addValue(CUSTOMER_ID, custID);
        return jdbcTemplate.queryForObject(SELECT_ALL_COUNT_OF_CUSTOMER, params, Integer.class);
    }

    @Override
    public List<User> getLimitedQuantityUsersOfCustomer(int start, int length, String sort, String search, int custID) {
        if (sort.isEmpty()) {
            sort = ID;
        }
        String sql = String.format(SELECT_LIMITED_ALL_USERS_OF_CUSTOMER, sort);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(FIRST_INDEX, start);
        params.addValue(LAST_INDEX, length);
        params.addValue(CUSTOMER_ID, custID);
        params.addValue(PATTERN, "%" + search + "%");
        return jdbcTemplate.query(sql, params, new UserRowMapper());
    }
    //////////////////

    /**
     * Method updates user
     *
     * @param user this user
     * @return success of the operation
     */
    @Override
    public boolean update(User user) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(NAME, user.getName());
        params.addValue(SURNAME, user.getSurname());
        params.addValue(SURNAME, user.getStatus().getId());
        params.addValue(PHONE, user.getPhone());
        params.addValue(PASSWORD, user.getPassword());
        params.addValue(ADDRESS, user.getAddress());
        params.addValue(ID, user.getId());
        int rows = jdbcTemplate.update(UPDATE_USER_SQL, params);
        return rows > 0;

    }

    /**
     * Method saves user
     *
     * @param user this user
     * @return success of the operation
     */
    @Override
    public boolean save(User user) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        String encodePassword = encoder.encode(user.getPassword());
        params.addValue(NAME, user.getName());
        params.addValue(SURNAME, user.getSurname());
        params.addValue(EMAIL, user.getEmail());
        params.addValue(PHONE, user.getPhone());
        params.addValue(PASSWORD, encodePassword);
        params.addValue(ROLE_ID, user.getRole().getId());
        params.addValue(PLACE_ID, user.getPlaceId());
        params.addValue(CUSTOMER_ID, user.getCustomerId());
        params.addValue(ADDRESS, user.getAddress());
        params.addValue(ENABLE, 1);
        int save = jdbcTemplate.update(INSERT_USER_SQL, params);
        return save > 0;
    }


    public boolean isUnique(User user) {
        String email = user.getEmail();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(EMAIL, email);
        params.addValue(PHONE, user.getPhone());
        List<String> users = jdbcTemplate.query(SELECT_BY_EMAIL_OR_PHONE_SQL, params, (rs, rowNum) -> rs.getString(EMAIL));
        return users.isEmpty();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getCountAllUsersWithSearch(String search) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PATTERN, "%" + search + "%");
        return jdbcTemplate.queryForObject(SELECT_ALL_COUNT_SQL, params, Integer.class);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserById(Integer id) {
        MapSqlParameterSource params = new MapSqlParameterSource(ID, id);
        return jdbcTemplate.queryForObject(SELECT_USER_BY_ID, params, new UserRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getCountUsersWithSearch(String search) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PATTERN, "%" + search + "%");
        return jdbcTemplate.queryForObject(SELECT_COUNT_SQL, params, Integer.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getLimitedQuantityUsers(int start, int length, String sort, String search) {
        if (sort.isEmpty()) {
            sort = ID;
        }
        String sql = String.format(SELECT_LIMITED_USERS_SQL, sort);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(FIRST_INDEX, start);
        params.addValue(LAST_INDEX, length);
        params.addValue(PATTERN, "%" + search + "%");
        return jdbcTemplate.query(sql, params, new UserRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getLimitedQuantityAllUsers(int start, int length, String sort, String search) {
        if (sort.isEmpty()) {
            sort = ID;
        }
        String sql = String.format(SELECT_LIMITED_ALL_USERS_SQL, sort);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(FIRST_INDEX, start);
        params.addValue(LAST_INDEX, length);
        params.addValue(PATTERN, "%" + search + "%");
        return jdbcTemplate.query(sql, params, new UserRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getLimitedQuantityEmployeesOfCustomer(int start, int length, String sort, String search, int customerId) {
        if (sort.isEmpty()) {
            sort = ID;
        }
        String sql = String.format(SELECT_EMPLOYEES_BY_CUSTOMER_SQL, sort);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(CUSTOMER_ID, customerId);
        params.addValue(FIRST_INDEX, start);
        params.addValue(LAST_INDEX, length);
        params.addValue(PATTERN, "%" + search + "%");
        return jdbcTemplate.query(sql, params, new UserRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getCountEmployeesWithSearchOfCustomer(String search, int customerId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PATTERN, "%" + search + "%");
        params.addValue(CUSTOMER_ID, customerId);
        return jdbcTemplate.queryForObject(SELECT_COUNT_EMPLOYEES_BY_CUSTOMER_SQL, params, Integer.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserByComplaintId(int complaintId) {
        MapSqlParameterSource params = new MapSqlParameterSource(COMPLAINT_ID, complaintId);
        return jdbcTemplate.queryForObject(SELECT_USER_BY_COMPLAINT_ID_SQL, params, new UserRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserByOrderId(int orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource(ORDER_ID, orderId);
        return jdbcTemplate.queryForObject(SELECT_USER_BY_ORDER_ID_SQL, params, new UserRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getUsersByCustomerType(CustomerType customerType) {
        Integer typeId = customerType.getId();
        MapSqlParameterSource params = new MapSqlParameterSource(TYPE_ID, typeId);
        return jdbcTemplate.query(SELECT_USERS_BY_CUSTOMER_TYPE_SQL, params, new UserRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getUsersByProductId(int productId) {
        MapSqlParameterSource params = new MapSqlParameterSource(PRODUCT_ID, productId);
        return jdbcTemplate.query(SELECT_USER_BY_PRODUCT_ID_SQL, params, new UserRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean enableDisableUser(User user) {
        Integer status = user.getStatus().getId();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ID, user.getId());
        params.addValue(STATUS, status);
        return jdbcTemplate.update(UPDATE_ENABLE_OR_DISABLE_SQL, params) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updatePassword(User user) {
        String password = encoder.encode(user.getPassword());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PASSWORD, password);
        params.addValue(ID, user.getId());
        return jdbcTemplate.update(UPDATE_PASSWORD_SQL, params) > 0;
    }
}
