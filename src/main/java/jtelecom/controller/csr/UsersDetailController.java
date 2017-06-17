package jtelecom.controller.csr;


import jtelecom.dto.grid.GridRequestDTO;
import jtelecom.dto.grid.ListHolder;
import jtelecom.security.SecurityAuthenticationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import jtelecom.dao.user.User;
import jtelecom.dao.user.UserDAO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author Moiseienko Petro
 * @since 28.04.2017.
 */
@RestController
@RequestMapping({"csr", "pmg"})
public class UsersDetailController {
    @Resource
    private UserDAO userDAO;
    @Resource
    private SecurityAuthenticationHelper securityAuthenticationHelper;
    private static Logger logger = LoggerFactory.getLogger(UsersDetailController.class);


    @RequestMapping(value = "getUsersPage", method = RequestMethod.GET)
    public ModelAndView getUsers() throws IOException {
        User user = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        return new ModelAndView("newPages/" + user.getRole().getNameInLowwerCase() + "/Users");
    }

    /**
     * This method gets GridRequestDTO( see the {@link GridRequestDTO}. <br>
     * After method receives list with all clients from database.<br>
     *
     * @param request contains indexes for first element and last elements and patterns for search and sort
     * @return class which contains number of all elements with such parameters and some interval of the data
     */
    @RequestMapping(value = {"getUsers"}, method = RequestMethod.GET)
    public ListHolder getUsers(@ModelAttribute GridRequestDTO request) {
        String sort = request.getSort();
        int start = request.getStartBorder();
        int length = request.getEndBorder();
        String search = request.getSearch();
        List<User> data = userDAO.getLimitedQuantityUsers(start, length, sort, search);
        int size = userDAO.getCountUsersWithSearch(search);
        logger.debug("Get clients in interval: {} : {}",start ,length);
        return ListHolder.create(data, size);
    }


}
