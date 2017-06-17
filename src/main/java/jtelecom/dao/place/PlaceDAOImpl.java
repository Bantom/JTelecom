package jtelecom.dao.place;

import jtelecom.dao.product.ProductStatus;
import jtelecom.dao.product.ProductType;
import jtelecom.dto.PriceByRegionDTO;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * This class implements methods from PlaceDAO.
 *
 * @author Revniuk Aleksandr
 * @author Anna Rysakova
 */
@Repository
public class PlaceDAOImpl implements PlaceDAO {

    private final static String ID = "ID";
    private final static String PLACE_ID = "PLACE_ID";
    private final static String NAME = "NAME";
    private final static String PRODUCT_NAME = "PRODUCT_NAME";
    private final static String TYPE_ID = "TYPE_ID";
    private final static String STATUS = "STATUS";
    private final static String PLACE = "PLACE";
    private final static String PRICE = "PRICE";
    private final static String START = "START";
    private final static String LENGTH = "LENGTH";
    private final static String PATTERN = "PATTERN";
    private final static String ID_PLACE = "placeId";
    private static final String PATTERN_INDEX = "pattern";
    private static final String LENGTH_INDEX = "length";
    private static final String START_INDEX = "start";

    private final static String SELECT_ALL_SQL = "SELECT * FROM Places";
    private final static String SELECT_PLACES_FOR_FILL_IN_TARIFF_SQL = "SELECT ID, NAME FROM PLACES WHERE \n" +
            " PARENT_ID IS NOT NULL ORDER BY NAME";
    private final static String SELECT_PLACE_NAME_BY_ID_SQL = "SELECT NAME FROM PLACES WHERE ID=:ID";
    private final static String SELECT_LIMITED_PLACES_SQL = "SELECT *\n" +
            "FROM (SELECT\n" +
            "        a.*,\n" +
            "        rownum rnum\n" +
            "      FROM (SELECT *\n" +
            "            FROM PLACES\n" +
            "            WHERE upper(PLACES.NAME) LIKE upper(:PATTERN) AND PLACES.PARENT_ID IS NOT NULL\n" +
            "            ORDER BY %s) a\n" +
            "      WHERE rownum <= :LENGTH)\n" +
            "WHERE rnum > :START";
    private static final String SELECT_COUNT_SQL = "SELECT count(ID)\n" +
            "  FROM PLACES\n" +
            " WHERE NAME LIKE :PATTERN ";
    private final static String SELECT_LIMITED_PRICES_BY_PLACE_SQL = "SELECT *\n" +
            "FROM (SELECT\n" +
            "        a.*,\n" +
            "        rownum rnum\n" +
            "      FROM (SELECT\n" +
            "              product.ID,\n" +
            "              product.TYPE_ID,\n" +
            "              type.NAME      TYPE,\n" +
            "              product.NAME   PRODUCT_NAME,\n" +
            "              product.STATUS STATUS,\n" +
            "              price.PRICE,\n" +
            "              price.PLACE_ID,\n" +
            "              place.NAME     PLACE\n" +
            "            FROM\n" +
            "              PRODUCTS product\n" +
            "              JOIN PRODUCT_TYPES type ON (product.TYPE_ID = type.ID)\n" +
            "              JOIN PRICES price ON (price.PRODUCT_ID = product.ID)\n" +
            "              JOIN PLACES place ON (price.PLACE_ID = place.ID)\n" +
            "            WHERE place.ID = :placeId AND\n" +
            "                  (upper(product.NAME) LIKE upper(:pattern)\n" +
            "                  OR STATUS LIKE :pattern\n" +
            "                  OR PRICE LIKE :pattern\n" +
            "                  OR upper(type.NAME) LIKE upper(:pattern))\n" +
            "            ORDER BY %s) a\n" +
            "      WHERE rownum <= :length)\n" +
            "WHERE rnum > :start";
    private static final String SELECT_COUNT_PRICE_BY_PLACE_SQL = "SELECT count(product.ID)\n" +
            "FROM\n" +
            "  PRODUCTS product\n" +
            "  JOIN PRODUCT_TYPES type ON (product.TYPE_ID = type.ID)\n" +
            "  JOIN PRICES price ON (price.PRODUCT_ID = product.ID)\n" +
            "  JOIN PLACES place ON (price.PLACE_ID = place.ID)\n" +
            "WHERE place.ID = :PLACE_ID AND\n" +
            "      (product.NAME LIKE :PATTERN\n" +
            "       OR STATUS LIKE :PATTERN\n" +
            "       OR PRICE LIKE :PATTERN OR type.NAME LIKE :PATTERN)";
    @Resource
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Resource
    private PlaceRowMapper placeRowMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Place> getAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL, placeRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Place> getAllPlaces() {
        return jdbcTemplate.query(SELECT_PLACES_FOR_FILL_IN_TARIFF_SQL, (rs, rowNum) -> {
            Place place = new Place();
            place.setId(rs.getInt(ID));
            place.setName(rs.getString(NAME));
            return place;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPlaceNameById(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ID, id);
        return jdbcTemplate.queryForObject(SELECT_PLACE_NAME_BY_ID_SQL, params, String.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Place> getLimitedQuantityPlace(int start, int length, String sort, String search) {
        int rownum = start + length;
        if (sort.isEmpty()) {
            sort = ID;
        }
        String sql = String.format(SELECT_LIMITED_PLACES_SQL, sort);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(START, start);
        params.addValue(LENGTH, rownum);
        params.addValue(PATTERN, "%" + search + "%");
        return jdbcTemplate.query(sql, params, new PlaceRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getCountPlacesWithSearch(String search) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PATTERN, "%" + search + "%");
        return jdbcTemplate.queryForObject(SELECT_COUNT_SQL, params, Integer.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PriceByRegionDTO> getLimitedQuantityPriceByPlace(int placeId, int start, int length, String sort, String search) {
        int rownum = start + length;
        if (sort.isEmpty()) {
            sort = ID;
        }
        String sql = String.format(SELECT_LIMITED_PRICES_BY_PLACE_SQL, sort);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(START_INDEX, start);
        params.addValue(LENGTH_INDEX, rownum);
        params.addValue(PATTERN_INDEX, "%" + search + "%");
        params.addValue(ID_PLACE, placeId);
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            PriceByRegionDTO priceByPlace = new PriceByRegionDTO();
            priceByPlace.setProductId(rs.getInt(ID));
            priceByPlace.setProductName(rs.getString(PRODUCT_NAME));
            priceByPlace.setProductType(ProductType.getProductTypeFromId(rs.getInt(TYPE_ID)).getName());
            priceByPlace.setProductStatus(ProductStatus.getProductStatusFromId(rs.getInt(STATUS)).getName());
            priceByPlace.setPlaceName(rs.getString(PLACE));
            priceByPlace.setPriceProduct(rs.getBigDecimal(PRICE));
            priceByPlace.setPlaceId(placeId);

            return priceByPlace;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getCountPriceByPlace(String search, Integer placeId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(PATTERN, "%" + search + "%");
        params.addValue(PLACE_ID, placeId);
        return jdbcTemplate.queryForObject(SELECT_COUNT_PRICE_BY_PLACE_SQL, params, Integer.class);
    }
}
