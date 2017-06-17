package jtelecom.dao.plannedTask;

import jtelecom.dao.interfaces.DAO;
import jtelecom.dto.PlannedTaskDTO;

import java.util.Calendar;
import java.util.List;

/**
 * @author Yuliya Pedash
 */
public interface PlannedTaskDAO extends DAO<PlannedTask> {

    /**
     * This method deletes all planed tasks from database for particular order.
     * @param productId id of product
     * @param userId id of user
     * @return  <code>true</code> if any rows were deleted, <code>false</code> otherwise
     */
    boolean deleteAllPlannedTasksForProductOfUser(Integer productId, Integer userId);

    /**
     * This method gets all planned tasks, which have action date scheduled from <code>beginDate</code>
     * to <code>endDate</code>
     *
     * @param beginDate begin date
     * @param endDate   end date
     * @return list of <code>PlannedTask</code> objects
     */
    List<PlannedTask> getAllPlannedTaskForDates(Calendar beginDate, Calendar endDate, Integer orderId);

    /**
     * This method returns next planned task with status 'Active' from database for this order.
     * @param orderId id of order
     * @return <code>true</code> if any rows were deleted, <code>false</code> otherwise
     */
    boolean deleteNextPlannedTaskForActivationForThisOrder(Integer orderId);

    /**
     * This method removes planned task by id
     *
     * @param plannedTaskId id of Planned task
     * @return <code>true</code> if operation was successful, <code>false</code> otherwise
     */
    boolean deletePlannedTaskById(Integer plannedTaskId);

    /**
     * This method deletes next planned task for activation this order after suspense planned task.
     *
     * @param suspensePlannedTask planed tasks for suspense of this order
     * @return <code>true</code> if operation was successful, <code>false</code> otherwise
     */
    boolean deletePlannedTaskForActivationOfThisSuspense(PlannedTask suspensePlannedTask);

    /**
     * This method gets limited list panned tasks for orders of user
     *@see PlannedTaskDTO
     * @param userId id of user
     * @param start  start index
     * @param end   end Index
     * @return list of limited Planned Tasks Dto objects
     */
    List<PlannedTaskDTO> getLimitedPlannedTasksForUsersOrders(Integer userId, Integer start, Integer end);

    /**
     * Returns count of all planned tasks for user
     * @param userId id of user
     * @return number of all planned tasks
     */
    Integer getCountPlannedTasksForUserOrders(Integer userId);

    /**
     * This method deletes all planned tasks for order
     * @param orderId order id
     * @return <code>true</code> if planned tasks were deleted, <code>false</code> otherwise
     */
    boolean deleteAllPlannedTasksForOrder(Integer orderId);

    /**
     * This method gets planned task with Status 'Deactivate' for this order.
     *
     * @param orderId id of order
     * @return planned task object
     * @see jtelecom.dao.entity.OperationStatus
     */
    PlannedTask getDeactivationPlannedTaskForOrder(Integer orderId);
}
