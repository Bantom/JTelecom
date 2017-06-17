package jtelecom.dao.complaint;

import jtelecom.dao.interfaces.DAO;

import java.util.List;

/**
 * This interface has methods to work with complaints.
 *
 * @author Revniuk Aleksandr
 */
public interface ComplaintDAO extends DAO<Complaint> {

    /**
     * This method returns list of complaints assigned to concrete user.
     *
     * @param pmgId      user id
     * @param startIndex index of first row
     * @param endIndex   index of last row
     * @return list of complaints
     */
    List<Complaint> getIntervalOfAssignedComplaints(int pmgId, int startIndex, int endIndex);

    /**
     * This method returns all complaints by concrete order.
     *
     * @param id id of order
     * @return list of complaints
     */
    List<Complaint> getByOrderId(int id);

    /**
     * This method returns list of unassigned complaints.
     *
     * @param startIndex index of first row
     * @param endIndex   index of last row
     * @return list of complaints
     */
    List<Complaint> getIntervalOfUnassignedComplaints(int startIndex, int endIndex);

    /**
     * This method returns all complaints by place.
     *
     * @param id id of place
     * @return list of complaints
     */
    List<Complaint> getByPlaceId(int id);

    /**
     * This method returns all complaints by their status.
     *
     * @param id id of status
     * @return list of complaints
     */
    List<Complaint> getByStatusID(int id);

    /**
     * This method assign user to concrete complaint.
     *
     * @param complaintId id of complaint
     * @param pmgId       id of PMG
     * @return <code>true</code> if operation was successful, <code>false</code> otherwise.
     */
    boolean assignToUser(int complaintId, int pmgId);

    /**
     * This method changes status of complaint.
     *
     * @param complaintId id of complaint
     * @param statusId    new id of status
     * @return <code>true</code> if operation was successful, <code>false</code> otherwise.
     */
    boolean changeStatus(int complaintId, int statusId);

    /**
     * This method changes description of complaint.
     *
     * @param complaintId id of complaint
     * @param description new description
     * @return <code>true</code> if operation was successful, <code>false</code> otherwise.
     */
    boolean changeDescription(int complaintId, String description);

    /**
     * This method returns amount of unassigned complaints.
     *
     * @return amount of complaints
     */
    int countUnassignedComplaintsToUser();

    /**
     * This method returns amount of complaints assigned to concrete user.
     *
     * @return amount of complaints
     */
    int countAssignedComplaintsToUser(int pmgId);

    /**
     * Method saves complaint
     *
     * @param complaint this complaint
     * @return id of the saved complaint
     */
    Integer saveComplaint(Complaint complaint);


}
