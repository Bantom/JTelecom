package jtelecom.dao.order;

import jtelecom.dao.product.ProductType;
import jtelecom.dto.OrdersRowDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;


/**
 * Created by Yuliya Pedash on 07.05.2017.
 */
public class OrdersRowDTORowMapper implements RowMapper<OrdersRowDTO> {
    @Override
    public OrdersRowDTO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        OrdersRowDTO ordersRowDTO = new OrdersRowDTO();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        ordersRowDTO.setName(resultSet.getString("name"));
        ordersRowDTO.setOperationStatus(resultSet.getString("operation_status"));
        ordersRowDTO.setOrderId(resultSet.getInt("order_id"));
        ordersRowDTO.setProductId(resultSet.getInt("product_id"));
        ordersRowDTO.setProductType(ProductType.getProductTypeFromId(resultSet.getInt("product_type")));
        ordersRowDTO.setEndDate(simpleDateFormat.format(resultSet.getDate("end_date")));
        return ordersRowDTO;

    }
}
