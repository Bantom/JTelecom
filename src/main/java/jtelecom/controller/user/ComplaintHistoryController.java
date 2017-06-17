package jtelecom.controller.user;

import jtelecom.dao.user.User;
import jtelecom.dao.user.UserDAO;
import jtelecom.dto.FullComplaintInfoDTO;
import jtelecom.dto.grid.GridRequestDTO;
import jtelecom.dto.grid.ListHolder;
import jtelecom.repositories.FullComplaintInfoRepository;
import jtelecom.security.SecurityAuthenticationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Moiseienko Petro
 * @since 16.05.2017.
 */
@RestController
@RequestMapping({"business", "residential"})
public class ComplaintHistoryController {
    @Resource
    private UserDAO userDAO;
    @Resource
    private SecurityAuthenticationHelper securityAuthenticationHelper;
    @Resource
    private FullComplaintInfoRepository fullComplaintInfoRepository;
    private static Logger logger = LoggerFactory.getLogger(ComplaintHistoryController.class);

    @RequestMapping(value = "complaintHistory", method = RequestMethod.GET)
    public ModelAndView complaintHistory() {
        User user = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        return new ModelAndView("newPages/" + user.getRole().getName().toLowerCase() + "/ComplaintHistory");
    }

    /**
     * This method gets GridRequestDTO( see the {@link GridRequestDTO} <br>.
     * After method gets list with all client's complaints from database.<br>
     * This client's id method gets from the security current user.
     *
     * @param request contains indexes for first element and last elements and patterns for search and sort
     * @return class which contains number of all elements with such parameters and some interval of the data
     */
    @RequestMapping(value = "getComplaintHistory", method = RequestMethod.GET)
    public ListHolder getComplaintHistory(@ModelAttribute GridRequestDTO request) {
        User user = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        Integer userId = user.getId();
        String sort = request.getSort();
        String search = request.getSearch();
        int start = request.getStartBorder();
        int length = request.getEndBorder();
        List<FullComplaintInfoDTO> data = fullComplaintInfoRepository.getIntervalComplaintsByUserId(start, length, sort, search, userId);
        int size = fullComplaintInfoRepository.getCountComplaintsByUserId(userId, search);
        logger.debug("Get users in interval: {} {}", start, length);
        return ListHolder.create(data, size);
    }

}
