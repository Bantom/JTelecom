package jtelecom.dao.product;

import jtelecom.dao.entity.CustomerType;
import jtelecom.dto.ProductWithTypeNameDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Anna Rysakova
 */
@Component
class ProductRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        ProductWithTypeNameDTO product = new ProductWithTypeNameDTO();
        product.setId(rs.getInt("ID"));
        product.setName(rs.getString("NAME"));
        product.setDescription(rs.getString("DESCRIPTION"));
        Integer processingStrategyId = rs.getInt("NEED_PROCESSING");
        product.setProcessingStrategy(ProcessingStrategy.getProcessingStrategyFromId(processingStrategyId));
        Integer statusId = rs.getInt("STATUS");
        product.setStatus(ProductStatus.getProductStatusFromId(statusId));
        product.setBasePrice(rs.getBigDecimal("BASE_PRICE"));
        product.setCategoryId(rs.getInt("CATEGORY_ID"));
        product.setDurationInDays(rs.getInt("DURATION"));
        product.setProductType(ProductType.getProductTypeFromId(rs.getInt("TYPE_ID")));
        product.setCustomerType(CustomerType.getCustomerTypeFromId(rs.getInt("CUSTOMER_TYPE_ID")));
        product.setProductTypeName(ProductType.getProductTypeFromId(rs.getInt("TYPE_ID")).getName());
        return product;
    }
}
