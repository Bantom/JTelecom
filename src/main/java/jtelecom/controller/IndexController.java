package jtelecom.controller;

import jtelecom.security.Authority;
import jtelecom.security.SecurityAuthenticationHelper;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author Anna Rysakova
 */
@Controller
public class IndexController {
    @Resource
    private SecurityAuthenticationHelper securityAuthenticationHelper;

    @RequestMapping({"index"})
    public String index() {
        User currentUser = securityAuthenticationHelper.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        String roleUrl = currentUser.getAuthorities().toString().toLowerCase().replaceAll("[\\[\\]]", "");

        String[] roles = Authority.valueStrings();
        for (String role : roles) {
            if (Objects.equals(roleUrl, role)) {
                return "redirect:/" + roleUrl + "/getProfile";
            }
        }
        return "redirect:/login";
    }
}
