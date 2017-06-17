package jtelecom.controller;

import jtelecom.dao.user.UserDAO;
import jtelecom.security.SecurityAuthenticationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 /**
 * @author Anna Rysakova
 * Nikita Alistratenko
 */
@Controller
@RequestMapping
public class LoginController {
    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserDAO userDao;

    @Resource
    private SecurityAuthenticationHelper securityAuthenticationHelper;

    @RequestMapping({"/"})
    public String index() {
        User currentUser = securityAuthenticationHelper.getCurrentUser();
        if (currentUser != null) {
            return "redirect:" + userDao.findByEmail(currentUser.getUsername()).getRole().getNameInLowwerCase() + "/getProfile";
        }
        return "newPages/about";
    }

    @RequestMapping({"/login"})
    public String login() {
        User currentUser = securityAuthenticationHelper.getCurrentUser();
        if (currentUser != null) {
            return "redirect:" + userDao.findByEmail(currentUser.getUsername()).getRole().getNameInLowwerCase() + "/getProfile";
        }
        return "newPages/login";
    }

    @RequestMapping("accessDenied")
    public String accessDenied() {
        return "newPages/accessDenied";
    }

    @RequestMapping("/failure")
    public String failure(HttpServletRequest request, Model model) {
        Object authenticationException = WebUtils.getSessionAttribute(request, WebAttributes.AUTHENTICATION_EXCEPTION);

        if (authenticationException instanceof AuthenticationException) {
            AuthenticationException ex = (AuthenticationException) authenticationException;

            logger.error("Redirected after unsuccessful authentication, details: {}", ex);

            WebUtils.setSessionAttribute(request, WebAttributes.AUTHENTICATION_EXCEPTION, null);

            if (authenticationException instanceof BadCredentialsException) {
                model.addAttribute("error", "Invalid username or password");
            } else {
                model.addAttribute("error", "Error");
            }
        }
        return "newPages/login";
    }

}
