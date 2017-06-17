package jtelecom.dao.reports;

import java.util.Map;

/**
 * Created by Yuliya Pedash on 24.05.2017.
 */
public interface ReportDataDAO {
    /**
     * This method returns <code>Map</code> filled with information about
     * number of complaints in particular time period and place from begin date
     * to end date. Some time periods can be omitted, because there are no
     * complaints made in this period.
     *
     * @param dateBeginStr begin date String
     * @param dateEndStr   end date String
     * @param placeId      id of place
     * @param stepPattern  step pattern
     * @return <code>Map</code> with key - time period, value - number of complaints
     */
    Map<String, Integer> getComplaintsReportsDataMap(String dateBeginStr, String dateEndStr, Integer placeId, String stepPattern);

    /**
     * This method returns <code>Map</code> filled with information about
     * number of orders in particular time period and place from begin date
     * to end date. Some time periods can be omitted, because there are no
     * orders made in this period.
     *
     * @param dateEndStr  end date String
     * @param placeId     id of place
     * @param stepPattern step pattern
     * @return <code>Map</code> with key - time period, value - number of complaints
     */
    Map<String, Integer> getOrdersReportsDataMap(String dateBeginStr, String dateEndStr, Integer placeId, String stepPattern);
}
