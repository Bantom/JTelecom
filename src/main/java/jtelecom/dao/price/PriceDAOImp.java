package jtelecom.dao.price;

import jtelecom.dto.PriceByRegionDTO;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Anna Rysakova
 */
@Service
public class PriceDAOImp implements PriceDAO {

    private final static String PLACE_ID = "PLACE_ID";
    private final static String PRICE = "PRICE";
    private final static String NAME = "NAME";
    private final static String PLACE = "PLACE";
    private final static String DESCRIPTION = "DESCRIPTION";
    private final static String PRODUCT_ID = "productId";
    private final static String ID_PLACE = "placeId";
    private final static String PRICES = "price";
    private static final String PATTERN = "pattern";
    private static final String LENGTH = "length";
    private static final String START = "start";

    private final static String INSERT_PRICE_OF_PRODUCT_BY_REGION_SQL = "INSERT INTO PRICES\n " +
            "VALUES(:productId,:placeId,:price)";
    private final static String SELECT_PRICE_IN_REGIONS_FOR_ALL_PRODUCTS_SQL = "SELECT *\n" +
            "FROM (SELECT\n" +
            "        a.*,\n" +
            "        rownum rnum\n" +
            "      FROM (SELECT\n" +
            "              product.NAME,\n" +
            "              product.DESCRIPTION,\n" +
            "              place.NAME PLACE,\n" +
            "              price.PRICE\n" +
            "            FROM PRODUCTS product\n" +
            "              JOIN PRICES price ON (product.ID = price.PRODUCT_ID)\n" +
            "              JOIN PLACES place ON (price.PLACE_ID = place.ID)\n" +
            "            WHERE product.ID=:productId AND price.PRICE>0 AND (upper(product.NAME) LIKE upper(:pattern)\n" +
            "                  OR upper(description) LIKE upper(:pattern)\n" +
            "                  OR upper(place.NAME) LIKE upper(:pattern)\n" +
            "                  OR PRICE LIKE :pattern)\n" +
            "            ORDER BY %s ) a\n" +
            "      WHERE rownum <= :length)\n" +
            "WHERE rnum > :start";
    private final static String SELECT_ALL_PLACES_AND_PRICE_AT_PLACES_SQL = "SELECT\n" +
            "  PLACES.ID PLACE_ID,\n" +
            "  PLACES.NAME,\n" +
            "  price.PRICE\n" +
            "FROM places\n" +
            "  LEFT JOIN (SELECT *\n" +
            "             FROM prices\n" +
            "             WHERE PRODUCT_ID = :productId AND PRICES.PRICE>0) price ON (places.id = price.PLACE_ID)\n" +
            "WHERE PLACES.PARENT_ID IS NOT NULL\n" +
            "ORDER BY PLACES.NAME";
    private final static String SELECT_ALL_PRICE_INFO_BY_PRODUCT_SQL = "SELECT * FROM PRICES\n" +
            " WHERE PRODUCT_ID=:productId";
    private final static String DELETE_PRODUCT_PRICE_FROM_REGION_SQL = "DELETE FROM PRICES\n" +
            "WHERE PRODUCT_ID=:productId AND PLACE_ID=:placeId";
    private final static String SELECT_COUNT_PRODUCT_BY_PLACE_SQL = "SELECT count(product.ID)\n" +
            "FROM\n" +
            "  PRODUCTS product\n" +
            "  JOIN PRICES price ON (product.ID = price.PRODUCT_ID)\n" +
            "  JOIN PLACES place ON (price.PLACE_ID = place.ID)\n" +
            "WHERE product.ID = :productId AND price.PRICE>0 AND\n" +
            "      (place.NAME LIKE :pattern\n" +
            "       OR PRICE LIKE :pattern)";
    @Resource
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Resource
    private PriceRowMapper priceRowMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean fillPriceOfProductByRegion(List<Price> priceByRegion) {
        List<Map<String, Object>> batchValues = new ArrayList<>(priceByRegion.size());
        for (Price price : priceByRegion) {
            batchValues.add(
                    new MapSqlParameterSource(PRODUCT_ID, price.getProductId())
                            .addValue(ID_PLACE, price.getPlaceId())
                            .addValue(PRICES, price.getPrice())
                            .getValues());
        }
        int[] isInsert = jdbcTemplate.batchUpdate(INSERT_PRICE_OF_PRODUCT_BY_REGION_SQL, batchValues.toArray(new Map[priceByRegion.size()]));
        return isInsert.length != 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PriceByRegionDTO> getAllRegionsAndProductPriceInRegionByProductId(int productId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PRODUCT_ID, productId);
        return jdbcTemplate.query(SELECT_ALL_PLACES_AND_PRICE_AT_PLACES_SQL, params, (rs, rowNum) -> {
            PriceByRegionDTO priceByRegionDTO = new PriceByRegionDTO();
            priceByRegionDTO.setPlaceId(rs.getInt(PLACE_ID));
            priceByRegionDTO.setProductId(productId);
            priceByRegionDTO.setPlaceName(rs.getString(NAME));
            priceByRegionDTO.setPriceProduct(rs.getBigDecimal(PRICE));
            return priceByRegionDTO;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Price> getPriceInRegionInfoByProduct(int productId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PRODUCT_ID, productId);
        return jdbcTemplate.query(SELECT_ALL_PRICE_INFO_BY_PRODUCT_SQL, params, priceRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean deleteProductPriceInRegion(List<Price> priceInRegion) {
        List<Map<String, Object>> batchValues = new ArrayList<>(priceInRegion.size());
        for (Price price : priceInRegion) {
            batchValues.add(
                    new MapSqlParameterSource(PRODUCT_ID, price.getProductId())
                            .addValue(ID_PLACE, price.getPlaceId())
                            .getValues());
        }
        int[] isInsert = jdbcTemplate.batchUpdate(DELETE_PRODUCT_PRICE_FROM_REGION_SQL, batchValues.toArray(new Map[priceInRegion.size()]));
        return isInsert.length != 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PriceByRegionDTO> getLimitedQuantityProductPricesInRegions(int productId, int start, int length, String sort, String search) {
        if (sort.isEmpty()) {
            sort = PLACE;
        }
        String sql = String.format(SELECT_PRICE_IN_REGIONS_FOR_ALL_PRODUCTS_SQL, sort);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(START, start);
        params.addValue(LENGTH, length);
        params.addValue(PATTERN, "%" + search + "%");
        params.addValue(PRODUCT_ID, productId);
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            PriceByRegionDTO priceByRegionDTO = new PriceByRegionDTO();
            priceByRegionDTO.setProductId(productId);
            priceByRegionDTO.setProductName(rs.getString(NAME));
            priceByRegionDTO.setProductDescription(rs.getString(DESCRIPTION));
            priceByRegionDTO.setPlaceName(rs.getString(PLACE));
            priceByRegionDTO.setPriceProduct(rs.getBigDecimal(PRICE));
            return priceByRegionDTO;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getCountPriceByPlace(String search, int productId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PATTERN, "%" + search + "%");
        params.addValue(PRODUCT_ID, productId);
        return jdbcTemplate.queryForObject(SELECT_COUNT_PRODUCT_BY_PLACE_SQL, params, Integer.class);
    }
}
