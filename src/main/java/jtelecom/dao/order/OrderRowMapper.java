package jtelecom.dao.order;


import jtelecom.dao.entity.OperationStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Yuliya Pedash
 * @since 27.04.2017.
 */
@Component
public class OrderRowMapper implements RowMapper<Order> {
    @Override
    public Order mapRow(ResultSet resultSet, int i) throws SQLException {
        Order order = new Order();
        order.setId(resultSet.getInt("id"));
        order.setProductId(resultSet.getInt("product_id"));
        order.setUserId(resultSet.getInt("user_id"));
        int currentStatus = resultSet.getInt("CURRENT_STATUS_ID");
        order.setCurrentStatus(OperationStatus.getOperationStatusFromId(currentStatus));
        return order;
    }
}

