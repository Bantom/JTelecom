package jtelecom.controller.user;

import jtelecom.dao.user.Role;
import jtelecom.dao.user.User;
import jtelecom.dao.user.UserDAO;
import jtelecom.security.SecurityAuthenticationHelper;
import jtelecom.services.user.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.annotation.Resource;

/**
 * @author Anton Bulgakov
 * @author Nikita Alistratenko
 * @since 13.05.2017.
 */
@Controller
@RequestMapping({"business"})
public class RegistrationEmployeeController {

    @Resource
    private SecurityAuthenticationHelper securityAuthenticationHelper;
    @Resource
    private UserServiceImpl userService;
    @Resource
    private UserDAO userDAO;

    private static Logger logger = LoggerFactory.getLogger(RegistrationEmployeeController.class);

    @RequestMapping(value = {"/regEmployee"}, method = RequestMethod.GET)
    public ModelAndView showRegEmployeePage() {
        return new ModelAndView("newPages/business/RegEmployee").addObject("user", new User());
    }

    /**
     * Registers new employee for business user and redirects to regEmployee jsp
     *
     * @param employee   employee to register
     * @param attributes attribute to write registration result in
     * @return regEmployee jsp
     */
    @RequestMapping(value = "/regEmployee", method = RequestMethod.POST)
    public String registrationEmployee(@ModelAttribute("user") User employee, RedirectAttributes attributes) {
        String result;
        User businessUser = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        logger.debug("User {} tries to create an employee.", businessUser.getId());
        if (isUserInfoValid(employee)) {
            employee.setRole(Role.EMPLOYEE);
            employee.setCustomerId(businessUser.getCustomerId());
            result = userService.saveWithGeneratingPassword(employee);
            if (result.equals("User was successfully saved.")) {
                result = "Employee was successfully created.";
            } else {
                result = "Employee was not created. Please, try later.";
                logger.error("User {} could not create an employee.", businessUser.getId());
            }
        } else {
            result = "User info is invalid. Please, try again.";
        }
        logger.debug("Result of employee registration by user {}: {}", businessUser.getId(), result);
        attributes.addFlashAttribute("result", result);
        return "redirect:/business/regEmployee";
    }

    /**
     * Checks if users info is valid
     *
     * @param user user to check
     * @return result of checking
     */
    private boolean isUserInfoValid(User user) {
        if (user.getName() == null || user.getName().trim().equals("")) {
            return false;
        }
        if (user.getSurname() == null || user.getSurname().trim().equals("")) {
            return false;
        }
        if (user.getEmail() == null || user.getEmail().trim().equals("")) {
            return false;
        }
        if (user.getPhone() == null || user.getPhone().trim().equals("")) {
            return false;
        }
        if (user.getAddress() == null || user.getAddress().trim().equals("")) {
            return false;
        }
        return true;
    }
}
