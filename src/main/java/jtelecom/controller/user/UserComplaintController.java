package jtelecom.controller.user;


import jtelecom.dao.complaint.Complaint;
import jtelecom.dao.complaint.ComplaintStatus;
import jtelecom.dao.order.OrderDAO;
import jtelecom.dao.product.Product;
import jtelecom.dao.product.ProductDAO;
import jtelecom.dao.user.User;
import jtelecom.dao.user.UserDAO;
import jtelecom.security.SecurityAuthenticationHelper;
import jtelecom.services.complaint.ComplaintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.List;

/**
 * @author Moiseienko Petro
 * @since 02.05.2017.
 */
@RestController
@RequestMapping({"residential", "business", "csr", "employee"})
public class UserComplaintController {
    @Resource
    private SecurityAuthenticationHelper securityAuthenticationHelper;
    @Resource
    private UserDAO userDAO;
    @Resource
    private ProductDAO productDAO;
    @Resource
    private ComplaintService complaintService;
    @Resource
    private OrderDAO orderDAO;
    private static Logger logger = LoggerFactory.getLogger(UserComplaintController.class);

    /**
     * Method find user from the security current user.<br>
     * After gets all active products that connected to the customer of this user
     *
     * @return model with list of active products
     */
    @RequestMapping(value = "getComplaint", method = RequestMethod.GET)
    public ModelAndView getComplaint() {
        User user = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        ModelAndView modelAndView = getProducts(user.getId());
        logger.debug("Get product for complaint to {}", user.getId());
        modelAndView.setViewName("newPages/" + user.getRole().getName().toLowerCase() + "/WriteToSupport");
        return modelAndView;
    }

    /**
     * Method find user from the session.<br>
     * After gets all active products that connected to the customer of this user
     *
     * @param session contains user's id
     * @return model with list of active products
     */
    @RequestMapping(value = "getCsrComplaint", method = RequestMethod.GET)
    public ModelAndView getCsrComplaint(HttpSession session) {
        Integer id = (Integer) session.getAttribute("userId");
        ModelAndView modelAndView = getProducts(id);
        logger.debug("Get product for complaint to {}", id);
        modelAndView.setViewName("newPages/csr/UserWriteComplaint");
        return modelAndView;
    }

    /**
     * Method save the user's complaint. User's id gets from the security current user.
     *
     * @param productId   id of the connected product
     * @param description text of the complaint
     * @return message about success of the operation
     */
    @RequestMapping(value = "writeComplaint", method = RequestMethod.POST)
    public String writeComplaint(@RequestParam(value = "productId") int productId,
                                 @RequestParam(value = "description") String description) {
        User user = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        logger.debug("Writing complaint from {}", user.getId());
        return save(user.getId(), productId, description);
    }

    /**
     * Method save the user's complaint. User's id gets from the session
     *
     * @param productId   id of the connected product
     * @param description text of the complaint
     * @param session     contains user's id
     * @return message about success of the operation
     */
    @RequestMapping(value = "saveComplaint", method = RequestMethod.POST)
    public String saveComplaint(@RequestParam(value = "productId") int productId,
                                @RequestParam(value = "description") String description, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        logger.debug("Writing complaint from {}", userId);
        return save(userId, productId, description);

    }

    /**
     * Method filled the complaint
     *
     * @param userId      id of the user
     * @param productId   id of the complaint
     * @param description text of the complaint
     * @return message about success of the operation
     */
    private String save(int userId, int productId, String description) {
        String message;
        Calendar calendar = Calendar.getInstance();
        Integer orderId = orderDAO.getOrderIdByUserIdAndProductId(userId, productId);
        Complaint complaint = new Complaint();
        complaint.setOrderId(orderId);
        complaint.setCreationDate(calendar);
        complaint.setDescription(description);
        complaint.setStatus(ComplaintStatus.Send);
        boolean success = complaintService.save(complaint, userId);
        if (success) {
            logger.debug("Complaint saved: orderId " + orderId + " user id: " + userId);

            message = "success";
        } else {
            logger.error("Complaint didn't saved: orderId " + orderId + " user id: " + userId);
            message = "fail";
        }
        return message;
    }


    /**
     * Method finds user's products from database
     *
     * @param userId id of the user
     * @return list of the products
     */
    private ModelAndView getProducts(Integer userId) {
        ModelAndView modelAndView = new ModelAndView();
        List<Product> products = productDAO.getActiveProductsByUserId(userId);
        modelAndView.addObject("productList", products);
        return modelAndView;
    }


}
