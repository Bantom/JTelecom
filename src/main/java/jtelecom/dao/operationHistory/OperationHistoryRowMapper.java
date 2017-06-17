package jtelecom.dao.operationHistory;

import jtelecom.dao.entity.OperationStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * @author Moiseienko Petro
 * @since 10.05.2017.
 */
@Component
public class OperationHistoryRowMapper implements RowMapper<OperationHistoryRecord> {
    @Override
    public OperationHistoryRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        OperationHistoryRecord record=new OperationHistoryRecord();
        record.setId(rs.getInt("id"));
        record.setOrderId(rs.getInt("order_id"));
        Calendar calendar = new GregorianCalendar();
        rs.getDate("operation_date", calendar);
        record.setOperationDate(calendar);
        record.setStatus(OperationStatus.getOperationStatusFromId(rs.getInt("status_id")));
        return record;
    }
}
