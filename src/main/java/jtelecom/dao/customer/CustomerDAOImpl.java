package jtelecom.dao.customer;

import jtelecom.dao.user.User;
import jtelecom.security.Md5PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Moiseienko Petro, Nikita Alistratenko
 * @since 24.04.2017.
 */
@Service
public class CustomerDAOImpl implements CustomerDAO {

    private final static String SELECT_COMPANY_SQL = "SELECT ID " +
            " FROM CUSTOMERS " +
            " WHERE NAME=:name AND SECRET_KEY=:secretKey";
    private final static String INSERT_CUSTOMER_SQL = "INSERT INTO CUSTOMERS(NAME,SECRET_KEY,TYPE_ID) " +
            "VALUES(:name, :secretKey,:typeId)";

    private final static String SELECT_BUSINESS_CUSTOMERS_SQL = "SELECT * FROM CUSTOMERS " +
            "WHERE TYPE_ID=1";
    private final static String SELECT_CUSTOMERS_BY_NAME_SQL = "SELECT ID FROM CUSTOMERS " +
            " WHERE upper(NAME)=upper(:name)";

    private final static String SELECT_LIMITED_CUSTOMERS_SQL = "select *\n" +
            "from ( select a.*, rownum rnum\n" +
            "       from ( Select * from CUSTOMERS " +
            " Where upper(name) like upper(:pattern) " +
            " OR invoice like :pattern) a\n" +
            "       where rownum <= :length )\n" +
            "       where rnum > :start";

    private static final String SELECT_COUNT_SQL = "SELECT count(ID)\n" +
            "  FROM CUSTOMERS" +
            " WHERE upper(name) LIKE upper(:pattern) " +
            " OR invoice LIKE :pattern ";

    private final static String SELECT_CUSTOMER_BY_ID = "SELECT * FROM CUSTOMERS where id= :id ";

    @Resource
    private NamedParameterJdbcTemplate jdbcTemplate;

    private Md5PasswordEncoder encoder = new Md5PasswordEncoder();

    @Override
    public Integer getCustomerId(String name, String secretKey) {
        String password = encoder.encode(secretKey);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);
        params.addValue("secretKey", password);
        return jdbcTemplate.queryForObject(SELECT_COMPANY_SQL, params, Integer.class);
    }

    @Override
    public boolean changeSecretKey(int customerId, String newSecretKey) {
        return false;
    }

    @Override
    public boolean changeInvoice(int customerId, int newInvoice) {
        return false;
    }

    @Override
    public boolean changeName(int customerId, String newName) {
        return false;
    }

    @Override
    public List<User> getAllUsers(int customerId) {
        return null;
    }

    @Override
    public Customer getById(int id) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("id", id);
        return jdbcTemplate.queryForObject(SELECT_CUSTOMER_BY_ID, map, new CustomerRowMapper());
    }

    @Override
    public boolean update(Customer object) {
        return false;
    }

    @Override
    public boolean save(Customer customer) {
        String password = encoder.encode(customer.getSecretKey());
        Integer type = customer.getCustomerType().getId();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", customer.getName());
        params.addValue("secretKey", password);
        params.addValue("typeId", type);
        return jdbcTemplate.update(INSERT_CUSTOMER_SQL, params) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer saveCustomer(Customer customer){
        String password = encoder.encode(customer.getSecretKey());
        Integer type = customer.getCustomerType().getId();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", customer.getName());
        params.addValue("secretKey", password);
        params.addValue("typeId", type);
        KeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(INSERT_CUSTOMER_SQL,params,key,new String[]{"ID"});
        return key.getKey().intValue();
    }

    @Override
    public boolean delete(Customer object) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Customer> getAllBusinessCustomers() {
        return jdbcTemplate.query(SELECT_BUSINESS_CUSTOMERS_SQL, new CustomerRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUnique(Customer customer) {
        MapSqlParameterSource params = new MapSqlParameterSource("name", customer.getName());
        List<Integer> customers = jdbcTemplate.query(SELECT_CUSTOMERS_BY_NAME_SQL, params, (rs, rownum) -> rs.getInt("id"));
        return customers.isEmpty();
    }

    @Override
    public List<Customer> getLimitedQuantityCustomer(int start, int length, String sort, String search) {
        int rownum = start + length;
        if (sort.isEmpty()) {
            sort = "ID";
        }
        String sql = String.format(SELECT_LIMITED_CUSTOMERS_SQL, sort);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("start", start);
        params.addValue("length", rownum);
        params.addValue("pattern", "%" + search + "%");
        List<Customer> customers = jdbcTemplate.query(sql, params, new CustomerRowMapper());
        return customers;
    }

    @Override
    public Integer getCountCustomersWithSearch(String search) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("pattern", "%" + search + "%");
        return jdbcTemplate.queryForObject(SELECT_COUNT_SQL, params, Integer.class);
    }

}
