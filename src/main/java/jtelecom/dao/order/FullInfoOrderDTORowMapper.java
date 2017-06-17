package jtelecom.dao.order;

import jtelecom.dao.entity.CustomerType;
import jtelecom.dao.entity.OperationStatus;
import jtelecom.dao.product.ProductType;
import jtelecom.dto.FullInfoOrderDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Moiseienko Petro
 * @since 21.05.2017.
 */
public class FullInfoOrderDTORowMapper implements RowMapper<FullInfoOrderDTO> {
    @Override
    public FullInfoOrderDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        FullInfoOrderDTO order = new FullInfoOrderDTO();
        order.setProductName(rs.getString("product_name"));
        order.setProductType(ProductType.getProductTypeFromId(rs.getInt("product_type")));
        order.setCustomerType(CustomerType.getCustomerTypeFromId(rs.getInt("customer_type")));
        order.setOrderId(rs.getInt("order_id"));
        order.setActionDate(rs.getString("operation_date"));
        order.setPlace(rs.getString("place"));
        return order;
    }
}
