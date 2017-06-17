package jtelecom.dao.product;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Anna Rysakova
 */
@Component
class ProductCategoriesRowMapper implements RowMapper<ProductCategories> {
    @Override
    public ProductCategories mapRow(ResultSet rs, int rowNum) throws SQLException {
        ProductCategories categories = new ProductCategories();
        categories.setId(rs.getInt("ID"));
        categories.setCategoryName(rs.getString("NAME"));
        categories.setCategoryDescription(rs.getString("DESCRIPTION"));

        return categories;
    }
}