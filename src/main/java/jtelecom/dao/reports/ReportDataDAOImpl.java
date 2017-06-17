package jtelecom.dao.reports;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yuliya Pedash on 20.05.2017.
 */
@Service
public class ReportDataDAOImpl implements ReportDataDAO {
    private final static String SELECT_NUMBERS_OF_ORDERS_FOR_TIME_PERIODS_BY_PLACE_SQL = "SELECT\n" +
            "  COUNT(*)                        COUNT,\n" +
            "  TO_CHAR(OPERATION_DATE, '%s') TIME_PERIOD\n" +
            "FROM OPERATIONS_HISTORY op_his\n" +
            "  JOIN ORDERS o ON o.ID = op_his.ORDER_ID\n" +
            "  JOIN USERS u ON u.ID = o.USER_ID\n" +
            "WHERE OPERATION_DATE BETWEEN TO_DATE(:date_begin, 'YYYY/MM/DD') AND TO_DATE(:date_end, 'YYYY/MM/DD') + 1\n" +
            "      AND (u.PLACE_ID IN (SELECT plL3.id\n" +
            "                          FROM Places plL1\n" +
            "                            JOIN Places plL2 ON plL1.id = plL2.parent_id\n" +
            "                            JOIN Places plL3 ON plL2.id = plL3.PARENT_ID\n" +
            "                          WHERE plL1.id = :place_id\n" +
            "                          UNION\n" +
            "                          SELECT id\n" +
            "                          FROM Places\n" +
            "                          WHERE id = :place_id OR parent_id = :place_id))\n" +
            "GROUP BY TO_CHAR(OPERATION_DATE, '%s')\n";
    private final static String SELECT_NUMBER_OF_COMPLAINTS_FOR_TIME_PERIOD_BY_PLACE_SQL = "SELECT\n" +
            "  COUNT(*)                                                COUNT,\n" +
            "  TO_CHAR(CREATING_DATE, '%s') TIME_PERIOD\n" +
            "FROM COMPLAINTS C\n" +
            "  JOIN ORDERS o ON o.ID = C.ORDER_ID\n" +
            "  JOIN USERS u ON u.ID = o.USER_ID\n" +
            "WHERE CREATING_DATE BETWEEN TO_DATE(:date_begin, 'YYYY/MM/DD') AND TO_DATE(:date_end, 'YYYY/MM/DD') + 1\n" +
            "      AND (u.PLACE_ID IN (SELECT plL3.id\n" +
            "                          FROM Places plL1\n" +
            "                            JOIN Places plL2 ON plL1.id = plL2.parent_id\n" +
            "                            JOIN Places plL3 ON plL2.id = plL3.PARENT_ID\n" +
            "                          WHERE plL1.id = :place_id\n" +
            "                          UNION\n" +
            "                          SELECT id\n" +
            "                          FROM Places\n" +
            "                          WHERE id = :place_id OR parent_id = :place_id))\n" +
            "GROUP BY TO_CHAR(CREATING_DATE, '%s')";
    @Resource
    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * {@inheritDoc}
     */
    public Map<String, Integer> getOrdersReportsDataMap(String dateBeginStr, String dateEndStr, Integer placeId, String stepPattern) {
        return getReportDataMap(SELECT_NUMBERS_OF_ORDERS_FOR_TIME_PERIODS_BY_PLACE_SQL, dateBeginStr, dateEndStr, placeId, stepPattern);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Integer> getComplaintsReportsDataMap(String dateBeginStr, String dateEndStr, Integer placeId, String stepPattern) {
        return getReportDataMap(SELECT_NUMBER_OF_COMPLAINTS_FOR_TIME_PERIOD_BY_PLACE_SQL, dateBeginStr, dateEndStr, placeId, stepPattern);
    }

    /**
     * This method returns <code>Map</code> filled with information about
     * number of items in particular time period and place from begin date
     * to end date. Some time periods can be omitted, because there are no
     * items in this time period.
     *
     * @param selectSql    sql query
     * @param dateBeginStr begin date String
     * @param dateEndStr   end date String
     * @param placeId      id of place
     * @param stepPattern  step pattern
     * @return  <code>Map</code> with key - time period, value - number of complaints
     */
    protected Map<String, Integer> getReportDataMap(String selectSql, String dateBeginStr, String dateEndStr, Integer placeId, String stepPattern) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("date_begin", dateBeginStr);
        params.addValue("date_end", dateEndStr);
        params.addValue("place_id", placeId);
        selectSql = String.format(selectSql, stepPattern, stepPattern);
        return jdbcTemplate.query(selectSql, params, rs -> {
            Map<String, Integer> mapRet = new HashMap<>();
            while (rs.next()) {
                mapRet.put(rs.getString("time_period"), rs.getInt("count"));
            }
            return mapRet;
        });
    }
}
