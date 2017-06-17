package jtelecom.controller.admin;

import jtelecom.dao.user.User;
import jtelecom.dao.user.UserDAO;
import jtelecom.dao.user.UserStatus;
import jtelecom.dto.grid.GridRequestDTO;
import jtelecom.dto.grid.ListHolder;
import jtelecom.security.SecurityAuthenticationHelper;
import jtelecom.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author Moiseienko Petro
 * @since 09.05.2017.
 */
@RestController
@RequestMapping({"admin", "business"})
public class ViewUsersController {
    private static Logger logger = LoggerFactory.getLogger(ViewUsersController.class);
    @Resource
    private UserDAO userDAO;
    @Resource
    private UserService userService;
    @Resource
    private SecurityAuthenticationHelper securityAuthenticationHelper;

    @RequestMapping(value = "getUsersPage", method = RequestMethod.GET)
    public ModelAndView getUsersPage() throws IOException {
        return new ModelAndView("newPages/admin/Users");
    }

    @RequestMapping(value = "getEmployeesPage", method = RequestMethod.GET)
    public ModelAndView getEmployeesPage() throws IOException {
        return new ModelAndView("newPages/business/Employees");
    }

    /**
     * This method gets GridRequestDTO( see the {@link GridRequestDTO} <br>.
     * After method gets list with all users from database
     *
     * @param request -contains indexes for first element and last elements and patterns for search and sort
     * @return class which contains number of all elements with such parameters and some interval of the data
     */
    @RequestMapping(value = {"getUsers"}, method = RequestMethod.GET)
    public ListHolder getUsers(@ModelAttribute GridRequestDTO request) {
        String sort = request.getSort();
        int start = request.getStartBorder();
        int length = request.getEndBorder();
        String search = request.getSearch();
        List<User> data = userDAO.getLimitedQuantityAllUsers(start, length, sort, search);
        int size = userDAO.getCountAllUsersWithSearch(search);
        logger.debug("Get users in interval:" + start + " : " + length);
        return ListHolder.create(data, size);
    }

    /**
     * This method gets GridRequestDTO( see the {@link GridRequestDTO} <br>.
     * After method gets list with all employees of business client from database.<br>
     * This client's id method gets from the security current user.
     *
     * @param requestDto -contains indexes for first element and last elements and patterns for search and sort
     * @return class which contains number of all elements with such parameters and some interval of the data
     */
    @RequestMapping(value = {"getEmployees"}, method = RequestMethod.GET)
    public ListHolder getEmployees(@ModelAttribute GridRequestDTO requestDto) {
        User user = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        Integer customerId = user.getCustomerId();
        String sort = requestDto.getSort();
        int start = requestDto.getStartBorder();
        int length = requestDto.getEndBorder();
        String search = requestDto.getSearch();
        List<User> data = userDAO.getLimitedQuantityEmployeesOfCustomer(start, length, sort, search, customerId);
        int size = userDAO.getCountEmployeesWithSearchOfCustomer(search, customerId);
        logger.debug("Get users in interval:" + start + " : " + length);
        return ListHolder.create(data, size);
    }

    /**
     * This method activates user using his id
     *
     * @param userId- id of the user
     * @return message which determines the success of the activating
     */
    @RequestMapping(value = {"/activateUser"}, method = RequestMethod.POST)
    public String activateUser(@RequestParam Integer userId) {
        User user = userDAO.getUserById(userId);
        String message;
        user.setStatus(UserStatus.ENABLE);
        boolean success = userService.enableDisableUser(user);
        if (success) {
            message = "success";
            logger.debug("User activated: " + userId);
        } else {
            logger.error("Error in activating user:" + userId);
            message = "Sorry, please try again";
        }
        return message;
    }

    /**
     * This method deactivates user using his id
     *
     * @param userId- id of the user
     * @return message which determines the success of the deactivating
     */
    @RequestMapping(value = {"/deactivateUser"}, method = RequestMethod.POST)
    public String deactivateUser(@RequestParam Integer userId) {
        User user = userDAO.getUserById(userId);
        String message;
        user.setStatus(UserStatus.DISABLE);
        boolean success = userService.enableDisableUser(user);
        if (success) {
            message = "success";
            logger.debug("User deactivated: " + userId);
        } else {
            logger.error("Error in deactivating user:" + userId);
            message = "Sorry, please try again";
        }
        return message;
    }
}
