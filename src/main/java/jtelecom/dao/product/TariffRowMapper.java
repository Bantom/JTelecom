package jtelecom.dao.product;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Anton Bulgakov
 * @since 08.05.2017.
 */
@Component
class TariffRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        Integer processingStrategyId = rs.getInt("need_processing");
        product.setProcessingStrategy(ProcessingStrategy.getProcessingStrategyFromId(processingStrategyId));
        Integer statusId = rs.getInt("status");
        product.setStatus(ProductStatus.getProductStatusFromId(statusId));
        product.setDurationInDays(rs.getInt("duration"));
        product.setProductType(ProductType.getProductTypeFromId(rs.getInt("type_id")));
        product.setBasePrice(rs.getBigDecimal("base_price"));
        return product;
    }
}

