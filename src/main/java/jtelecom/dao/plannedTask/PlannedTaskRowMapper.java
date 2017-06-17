package jtelecom.dao.plannedTask;


import jtelecom.dao.entity.OperationStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

/**
 * @author  Yuliya Pedash
 * @since 09.05.2017.
 */
@Component
public class PlannedTaskRowMapper implements RowMapper<PlannedTask> {
    @Override
    public PlannedTask mapRow(ResultSet resultSet, int i) throws SQLException {
        PlannedTask plannedTask = new PlannedTask();
        plannedTask.setId(resultSet.getInt("id"));
        Integer statusId = resultSet.getInt("status_id");
        plannedTask.setStatus(OperationStatus.getOperationStatusFromId(statusId));
        plannedTask.setOrderId(resultSet.getInt("order_id"));
        Date date = resultSet.getDate("action_date");
        Calendar actionDate = Calendar.getInstance();
        actionDate.setTime(date);
        plannedTask.setActionDate(actionDate);
        return plannedTask;
    }
}
