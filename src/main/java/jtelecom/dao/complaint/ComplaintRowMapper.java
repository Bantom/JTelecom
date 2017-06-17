package jtelecom.dao.complaint;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This class implements RowMapper interface.
 *
 * @author Revniuk Aleksandr
 */
@Component
public class ComplaintRowMapper implements RowMapper<Complaint> {
    @Override
    public Complaint mapRow(ResultSet resultSet, int i) throws SQLException {
        Complaint complaint = new Complaint();
        complaint.setId(resultSet.getInt("ID"));
        complaint.setOrderId(resultSet.getInt("ORDER_ID"));
        complaint.setPmgId(resultSet.getInt("PMG_ID"));
        Calendar calendar = new GregorianCalendar();
        resultSet.getDate("CREATING_DATE", calendar);
        complaint.setCreationDate(calendar);
        Integer statusId = resultSet.getInt("STATUS_ID");
        complaint.setStatus(ComplaintStatus.getOperationStatusById(statusId));
        complaint.setDescription(resultSet.getString("DESCRIPTION"));
        return complaint;
    }
}
