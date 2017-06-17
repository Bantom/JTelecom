package jtelecom.controller;


import jtelecom.dao.user.Role;
import jtelecom.dao.user.User;
import jtelecom.dao.user.UserDAO;
import jtelecom.googleMaps.ServiceGoogleMaps;
import jtelecom.security.Md5PasswordEncoder;
import jtelecom.security.SecurityAuthenticationHelper;
import jtelecom.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Moiseienko Petro
 * @author Nikita Alistratenko
 * @author Anton Bulgakov
 * @since 05.05.2017.
 */
@Controller
@RequestMapping({"admin", "csr", "pmg", "business", "employee", "residential"})
public class EditProfileController {
    private static Logger logger = LoggerFactory.getLogger(EditProfileController.class);

    @Resource
    private SecurityAuthenticationHelper securityAuthenticationHelper;
    @Resource
    private UserDAO userDAO;
    @Resource
    private UserService userService;
    @Resource
    private ServiceGoogleMaps serviceGoogleMaps;
    private Map<Role, String> roleMap;

    @RequestMapping(value = "/getProfile", method = RequestMethod.GET)
    public ModelAndView getProfile() {
        User user = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        ModelAndView model = new ModelAndView();
        model.addObject("user", user);
        String urlBegin;
        if (roleMap == null) {
            roleMap = new HashMap<>();
            roleMap.put(Role.Admin, "admin");
            roleMap.put(Role.CSR, "csr");
            roleMap.put(Role.PMG, "pmg");
            roleMap.put(Role.BUSINESS, "business");
            roleMap.put(Role.EMPLOYEE, "employee");
            roleMap.put(Role.RESIDENTIAL, "residential");
        }
        urlBegin = roleMap.getOrDefault(user.getRole(), "user");
        String view = "newPages/" + urlBegin + "/profile";
        model.addObject("pattern", urlBegin);
        model.setViewName(view);
        return model;
    }

    /**
     * Updates info about user of any role
     *
     * @param editedUser new user info
     * @param attributes needs to  pass messages to jsp
     * @param request    to get additional parameters like old pass
     * @return redirect to profile view
     */
    @RequestMapping(value = "/editProfile", method = RequestMethod.POST)
    public String editProfile(@ModelAttribute("user") User editedUser, RedirectAttributes attributes, HttpServletRequest request) {
        logger.debug("User sent info to update = {} ", editedUser);
        //Message of the updating result
        String errorMessage = null;
        //user that sent info to update
        User sessionUser = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        //needs to set placeID to new copy of user
        Integer placeId = sessionUser.getPlaceId();
        label:
        {
            if (!isUserInfoValid(editedUser)) {
                errorMessage = "Users info is invalid.";
                break label;
            }
            //if address was changed
            if (!editedUser.getAddress().equals(sessionUser.getAddress())) {
                String place = serviceGoogleMaps.getRegion(editedUser.getAddress());
                placeId = userDAO.findPlaceId(place);
                //checks if entered address isn't in database
                if (placeId == null || placeId == 0) {
                    errorMessage = "Our company does not provide service for this region";
                    logger.debug("User address is not supported = ", editedUser);
                    break label;
                }
            }
            editedUser.setPlaceId(placeId);
            //if new password was entered
            if (!editedUser.getPassword().isEmpty()) {
                //checks if old pass is wrong
                if (!sessionUser.getPassword().equals(new Md5PasswordEncoder().encode(request.getParameter("oldPassword")))) {
                    errorMessage = "You have entered wrong old password. Please, try again.";
                    logger.debug("User old password is not valid = {} ", editedUser);
                    break label;
                }
            }
            editedUser.setId(sessionUser.getId());
            if (!userService.updateUser(editedUser)) {
                errorMessage = "User has not been updated";
                logger.error("User has not been updated = {} ", editedUser);
            } else {
                logger.debug("User has been updated = {} ", editedUser);
            }
        }
        attributes.addFlashAttribute("msg", errorMessage);
        return "redirect:/" + sessionUser.getRole().toString().toLowerCase() + "/getProfile";
    }

    @RequestMapping(value = "editUser", method = RequestMethod.POST)
    public ModelAndView editUserProfile(User user, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("newPages/csr/UserInfo");
        String message = "";
        Integer id = (Integer) session.getAttribute("userId");
        user.setId(id);
        String place = serviceGoogleMaps.getRegion(user.getAddress());
        Integer placeId = userDAO.findPlaceId(place);
        user.setPlaceId(placeId);
        boolean success = userService.updateUser(user);
        if (success) {
            message = "User successfully updated";
            logger.debug("user updated: " + id);
        } else {
            message = "User updating failed";
            logger.error("user updating failed, userId " + id);
        }
        modelAndView.addObject("msg", message);
        modelAndView.addObject("user", user);
        return modelAndView;
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
