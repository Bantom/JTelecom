package jtelecom.dao.price;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Anna Rysakova
 */
@Component
class PriceRowMapper implements RowMapper<Price> {
    @Override
    public Price mapRow(ResultSet resultSet, int i) throws SQLException {
        Price price = new Price();
        price.setPlaceId(resultSet.getInt("PLACE_ID"));
        price.setProductId(resultSet.getInt("PRODUCT_ID"));
        price.setPrice(resultSet.getBigDecimal("PRICE"));
        return price;
    }
}