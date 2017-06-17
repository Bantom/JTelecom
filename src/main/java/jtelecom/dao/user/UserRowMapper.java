package jtelecom.dao.user;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Yuliya Pedash on 29.04.2017.
 */
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setName(resultSet.getString("name"));
        user.setSurname(resultSet.getString("surname"));
        user.setAddress(resultSet.getString("address"));
        user.setEmail(resultSet.getString("email"));
        user.setPhone(resultSet.getString("phone"));
        user.setPassword(resultSet.getString("password"));
        user.setPlaceId(resultSet.getInt("place_id"));
        user.setCustomerId(resultSet.getInt("customer_id"));
        user.setRole(Role.getRoleFromId(resultSet.getInt("role_id")));
        user.setStatus(UserStatus.getStatusFromId(resultSet.getInt("enable")));

        return user;
    }
}
