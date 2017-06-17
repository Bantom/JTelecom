package jtelecom.dao.customer;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Nikita Alistratenko
 */
@Component
class CustomerRowMapper implements RowMapper<Customer> {
    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("ID"));
        customer.setName(rs.getString("NAME"));
        customer.setInvoice(rs.getInt("INVOICE"));
        customer.setSecretKey(rs.getString("SECRET_KEY"));
        return customer;
    }
}
