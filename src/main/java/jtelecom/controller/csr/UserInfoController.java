package jtelecom.controller.csr;

import jtelecom.dao.user.User;
import jtelecom.dao.user.UserDAO;
import jtelecom.security.SecurityAuthenticationHelper;
import jtelecom.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @author Moiseienko Petro
 * @since 11.05.2017.
 */
@RestController
@RequestMapping({"csr", "pmg"})
public class UserInfoController {

    @Resource
    private UserDAO userDAO;
    @Resource
    private SecurityAuthenticationHelper securityAuthenticationHelper;
    @Resource
    private UserService userService;
    private static Logger logger = LoggerFactory.getLogger(UserInfoController.class);
    private final static String USER_ID = "userId";
    private static final String USER = "user";
    private final static String GO_TO_USER_PAGE = "Go to user page, id {}";

    /**
     * Method finds information about client and redirect it to the some page.<br>
     * Page's address determines based to current user role
     *
     * @param id      if of the client
     * @param session needs for writing user's id to the session
     * @return model with this client
     */
    @RequestMapping(value = "getDetails", method = RequestMethod.GET)
    public ModelAndView getDetails(@RequestParam(value = "id") int id, HttpSession session) {
        ModelAndView model = new ModelAndView();
        User sessionUser = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        User user = userDAO.getUserById(id);
        session.setAttribute(USER_ID, id);
        model.setViewName("newPages/" + sessionUser.getRole().getNameInLowwerCase() + "/UserInfo");
        model.addObject(USER, user);
        logger.debug(GO_TO_USER_PAGE, id);
        return model;
    }

    @RequestMapping(value = "sendPassword", method = RequestMethod.POST)
    public String sendPassword(@RequestParam(value = "userId") int userId) {
        boolean success = userService.generateNewPassword(userId);
        String message;
        if (success) {
            message = "New password successfully sent";
        } else {
            message = "Sorry, an error occurred while sending new password!";
        }
        return message;
    }

    /**
     * see {@link jtelecom.controller.csr.UserInfoController#getDetails(int, HttpSession)}
     *
     * @param session it contains user id
     * @return model with client
     */
    @RequestMapping(value = "getUserProfile", method = RequestMethod.GET)
    public ModelAndView getProfile(HttpSession session) {
        Integer userId = (Integer) session.getAttribute(USER_ID);
        User user = userDAO.getUserById(userId);
        ModelAndView model = new ModelAndView();
        model.setViewName("newPages/csr/UserInfo");
        model.addObject(USER, user);
        logger.debug(GO_TO_USER_PAGE, userId);
        return model;

    }


}
