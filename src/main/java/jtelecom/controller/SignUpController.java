package jtelecom.controller;


import jtelecom.dao.customer.Customer;
import jtelecom.dao.customer.CustomerDAO;
import jtelecom.dao.user.Role;
import jtelecom.dao.user.User;
import jtelecom.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Moiseienko Petro
 * @since 17.04.2017.
 */
@Controller
@RequestMapping({"", "csr", "admin"})
public class SignUpController {
    @Resource
    private CustomerDAO customerDAO;
    @Resource
    private UserService userService;
    private static Logger logger = LoggerFactory.getLogger(SignUpController.class);
    private final static String SUCCESS_MESSAGE = "User successfully saved";
    private final static String FAIL_LOG = "Registration failed to {} {}";
    private final static String SUCCESS_LOG = "User saved: {} {}";
    private final static String USER = "user";
    private final static String MSG = "msg";

    @RequestMapping(method = RequestMethod.GET, value = "/registration")
    public ModelAndView registration() {
        ModelAndView model = new ModelAndView("newPages/Registration");
        model.addObject(USER, new User());
        return model;
    }

    @RequestMapping(value = "registrationFromCsr", method = RequestMethod.GET)
    public ModelAndView registrationFromCsr() {
        ModelAndView model = new ModelAndView("newPages/csr/RegNewUser");
        List<Customer> customers = customerDAO.getAllBusinessCustomers();
        model.addObject("customers", customers);
        model.addObject(USER, new User());
        return model;
    }

    @RequestMapping(value = "registrationFromAdmin", method = RequestMethod.GET)
    public ModelAndView registrationFromAdmin() {
        ModelAndView model = new ModelAndView("newPages/admin/RegNewUser");
        model.addObject(USER, new User());
        return model;
    }

    /**
     * Method signs up the csr or admin or pmg
     *
     * @param user       user to sign up
     * @param userType   see {@link jtelecom.dao.user.Role}
     * @param attributes needs to redirect message about success of the operation
     * @return redirect to {@link SignUpController#registrationFromAdmin()}
     */
    @RequestMapping(value = "/signUpCoworker", method = RequestMethod.POST)
    public String signUpCoworker(User user,
                                 @RequestParam(value = "userType") String userType, RedirectAttributes attributes) {
        user.setRole(Role.getRoleByName(userType));
        String message = userService.saveWithGeneratingPassword(user);
        if (SUCCESS_MESSAGE.equals(message)) {
            logger.debug(SUCCESS_LOG, user.getRole().getName(), user.getEmail());
        } else {
            logger.error(FAIL_LOG, user.getRole().getName(), user.getEmail());
        }
        attributes.addFlashAttribute(MSG, message);
        return "redirect:/admin/registrationFromAdmin";
    }

    /**
     * Method signs up user from csr
     *
     * @param user        user to sign up
     * @param userType    see {@link jtelecom.dao.user.Role}
     * @param companyName customer's name of the business client
     * @param secretKey   customer's secret key for business client
     * @param attributes  needs to redirect message about success of the operation
     * @return redirect to {@link SignUpController#registrationFromCsr()}
     */
    @RequestMapping(value = "signUpUser", method = RequestMethod.POST)
    public String signUpUser(User user,
                             @RequestParam(value = "userType") String userType,
                             @RequestParam(value = "companyName") String companyName,
                             @RequestParam(value = "secretKey") String secretKey, RedirectAttributes attributes) {
        user.setRole(Role.getRoleByName(userType));
        String message = "";
        if (user.getRole() == Role.RESIDENTIAL) {
            message = userService.saveResidentialWithPasswordGenerating(user);
            logger.info(message);
        } else if (Role.BUSINESS == user.getRole()) {
            message = userService.saveBusinessUser(user, companyName, secretKey);
        }
        if (SUCCESS_MESSAGE.equals(message)) {
            logger.debug(SUCCESS_LOG, user.getRole().getName(), user.getEmail());
        } else {
            logger.error(FAIL_LOG, user.getRole().getName(), user.getEmail());
        }
        attributes.addFlashAttribute(MSG, message);
        return "redirect:/csr/registrationFromCsr";

    }


    /**
     * Methods signs up the residential ser
     *
     * @param user       user to save
     * @param attributes needs to redirect message about success of the operation
     * @return redirect to {@link SignUpController#registration()}
     */
    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    public String signUp(User user, RedirectAttributes attributes) {
        String message = userService.saveResidentialWithoutPasswordGenerating(user);
        if (SUCCESS_MESSAGE.equals(message)) {
            logger.debug("User saved {}", user.getEmail());
            return "newPages/login";
        } else {
            logger.error("Registration failed {}", user.getEmail());
            attributes.addFlashAttribute(MSG, message);
            return "redirect:/registration";
        }
    }
}
