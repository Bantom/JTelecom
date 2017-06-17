package jtelecom.dao.user;

import jtelecom.dao.entity.CustomerType;
import jtelecom.dao.interfaces.DAO;

import java.util.List;

/**
 * @author Anna Rysakova
 */
public interface UserDAO extends DAO<User> {

    /**
     * Method return {@code User} by username
     *
     * @param username {@code User} username
     * @return {@code User} by username
     * @see User
     */
    User findByUsername(String username);

    List<User> getAll();

    Integer findRole(String name);

    Integer findPlaceId(String name);

    User findByEmail(String email);

    /**
     * Method finds user by his id
     * @param id this id
     * @return user
     */
    User getUserById(Integer id);

    /**
     * Method find total count of the clients( business, residential
     * and employee) which satisfy the search condition
     * in the database for csr
     *
     * @param search search condition
     * @return total count
     */
    Integer getCountUsersWithSearch(String search);

    /**
     * Method find part of the clients (business, residential and
     * employee) which satisfy the search condition
     *
     * @param start  first element
     * @param length last element
     * @param sort   name of the column to sorting data
     * @param search search condition
     * @return list of the users
     */
    List<User> getLimitedQuantityUsers(int start, int length, String sort, String search);

    /**
     * Method find total count of the all type of users
     * (see {@link jtelecom.dao.user.Role})
     * which satisfy the search condition
     * in the database for admin
     *
     * @param search search condition
     * @return total count
     */
    Integer getCountAllUsersWithSearch(String search);

    /**
     * Method find part of the all type of users
     * (see {@link jtelecom.dao.user.Role})
     * which satisfy the search condition
     *
     * @param start  first element
     * @param length last element
     * @param sort   name of the column to sorting data
     * @param search search condition
     * @return list of the users
     */
    List<User> getLimitedQuantityAllUsers(int start, int length, String sort, String search);

    Integer getCountUsersWithSearchOfCustomer(String search, int custID);

    List<User> getLimitedQuantityUsersOfCustomer(int start, int length, String sort, String search, int custID);

    List<User> getLimitedQuantityEmployeesOfCustomer(int start, int length, String sort, String search, int customerId);

    Integer getCountEmployeesWithSearchOfCustomer(String search, int customerId);

    /**
     * Method check is user's email is unique
     *
     * @param user this user
     * @return if user unique it returns true
     */
    boolean isUnique(User user);

    /**
     * Method finds user which wrote complaint with this id
     *
     * @param complaintId complaint's id
     * @return user
     */
    User getUserByComplaintId(int complaintId);

    /**
     * Method finds a user whose order has such id
     *
     * @param orderId order's id
     * @return user
     */
    User getUserByOrderId(int orderId);

    /**
     * Method finds all users which customer type
     * (see {@link jtelecom.dao.entity.CustomerType} such as this type
     *
     * @param customerType this type
     * @return list of users
     */
    List<User> getUsersByCustomerType(CustomerType customerType);

    /**
     * Method find all the users which have
     * connected this product
     *
     * @param productId this product
     * @return list of the users
     */
    List<User> getUsersByProductId(int productId);

    /**
     * Method updates status of the user
     * (see {@link jtelecom.dao.user.UserStatus}
     *
     * @param user this user
     * @return success of the operation
     */
    boolean enableDisableUser(User user);

    /**
     * Method updates password of the user
     *
     * @param user this user
     * @return success of the operation
     */
    boolean updatePassword(User user);
}