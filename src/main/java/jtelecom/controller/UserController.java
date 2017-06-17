package jtelecom.controller;

import jtelecom.controller.admin.AddProductController;
import jtelecom.dao.user.User;
import jtelecom.dao.user.UserDAO;
import jtelecom.security.SecurityAuthenticationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * @author Rysakova Anna
 */
@Controller
@RequestMapping({""})
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(AddProductController.class);
    @Resource
    private SecurityAuthenticationHelper securityAuthenticationHelper;
    @Resource
    private UserDAO dao;

    @RequestMapping(value = "profile", method = RequestMethod.GET)
    public ModelAndView getProfile() {
        User user = dao.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/userProfile");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @RequestMapping(value = "profile", method = RequestMethod.POST)
    public String editInfo(@ModelAttribute("user") User user) {
        logger.warn(Integer.toString(user.getId()));
        logger.warn(Boolean.toString(dao.update(user)));
        return "redirect:profile";
    }
}
