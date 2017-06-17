package jtelecom.controller.user;

import jtelecom.dao.entity.OperationStatus;
import jtelecom.dao.order.Order;
import jtelecom.dao.product.ProcessingStrategy;
import jtelecom.dao.product.Product;
import jtelecom.dao.product.ProductCategories;
import jtelecom.dao.product.ProductDAO;
import jtelecom.dao.user.Role;
import jtelecom.dao.user.User;
import jtelecom.dao.user.UserDAO;
import jtelecom.dto.ServicesCatalogRowDTO;
import jtelecom.dto.grid.GridRequestDTO;
import jtelecom.dto.grid.ListHolder;
import jtelecom.security.SecurityAuthenticationHelper;
import jtelecom.services.orders.OrderService;
import jtelecom.services.product.ProductService;
import jtelecom.util.SharedVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * This class contains methods, which control presentation, activation, and deactivation
 * for users with different roles.
 * @author Yuliya Pedash
 */
@Controller
@Scope(value = "session")
public class ServiceOrderController implements Serializable {
    @Resource
    private SecurityAuthenticationHelper securityAuthenticationHelper;
    @Resource
    private ProductDAO productDAO;
    @Resource
    private OrderService orderService;
    @Resource
    private UserDAO userDAO;
    private User currentUser = null;
    private Integer categoryId;

    @Resource
    private ProductService productService;
    private static Logger logger = LoggerFactory.getLogger(ServiceOrderController.class);
    private final static String ORDER_IN_PROCESS_MSG = "Order on %s is in process. It will be activated after processing.";
    private final static String SERVICE_WAS_ACTIVATED_MSG = "Service %s has been activated. Thank you!";
    private final static String ERROR_PLACING_ORDER_MSG = "Sorry, mistake while placing this order. Please, try again!";
    private final static String ALL_CATEGORIES = "All Categories";

    /**
     * This method makes preparation for showing view page. It adds list with all categories
     * to view page and name of the current category.
     * @param model model
     * @param categoryId category id
     * @see ProductDAO#getProductCategories()
     * @see ProductDAO#getProductCategoryById(Integer)
     */
    private void orderService(Model model, Integer categoryId) {
        this.categoryId = categoryId;
        String categoryName = categoryId == null ? ALL_CATEGORIES :
                productDAO.getProductCategoryById(categoryId).getCategoryName();
        model.addAttribute("categoryName", categoryName);
        List<ProductCategories> productCategories = productDAO.getProductCategories();
        model.addAttribute("productsCategories", productCategories);
    }

    /**
     * This method adds user role to view and prepares vew page for user
     * @param model model
     * @param categoryId id of category
     */
    private void orderServiceUser(Model model, Integer categoryId) {
        String userRoleLowerCase = currentUser.getRole().getNameInLowwerCase();
        model.addAttribute("userRole", userRoleLowerCase);
        orderService(model, categoryId);
    }
    /**
     * This method shows gets current user with which csr is currently working
     * and prepares view for user with role {@link jtelecom.dao.user.Role#CSR}
     * @param model model  model
     * @param categoryId id of category id of category
     * @return view address
     * @see #orderService
     * @see jtelecom.dao.user.Role
     */
    @RequestMapping(value = {"csr/orderService"}, method = RequestMethod.GET)
    public String orderService(Model model, @RequestParam(required = false) Integer categoryId, HttpSession session){
        this.currentUser = userDAO.getUserById((Integer) session.getAttribute("userId"));
        orderService(model, categoryId);
        model.addAttribute("userRole", "csr");
        return "newPages/csr/Services";
    }
    /**
     * This method  gets current user and performs operation for preparing orderService page
     * for  user with role {@link jtelecom.dao.user.Role#RESIDENTIAL}
     * @param model model
     * @param categoryId id of category
     * @return view address
     * @see #orderService
     * @see jtelecom.dao.user.Role
     */
    @RequestMapping(value = {"residential/orderService"}, method = RequestMethod.GET)
    public String orderServiceResidential(Model model, @RequestParam(required = false) Integer categoryId)  {
        this.currentUser = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        orderServiceUser(model, categoryId);
        return "newPages/residential/Services";
    }

    /**
     * This method  gets current user and performs operation for preparing orderService page
     * for user with role {@link jtelecom.dao.user.Role#BUSINESS}
     * @param model model
     * @param categoryId id of category
     * @return view address
     * @see #orderService
     * @see jtelecom.dao.user.Role
     */
    @RequestMapping(value = {"business/orderService"}, method = RequestMethod.GET)
    public String orderServiceBusiness(Model model, @RequestParam(required = false) Integer categoryId) {
        this.currentUser = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        orderServiceUser(model, categoryId);
        return "newPages/business/Services";
    }

    /**
     * This method {@link GridRequestDTO} object and returns all services
     * limited by parameters in {@link GridRequestDTO} object, such as
     * start, sort, length and search.
     * @param request {@link GridRequestDTO} object
     * @return {@link ListHolder} object which contains data and total count of data
     * @see  ProductService#getLimitedServicesForUser(User, Integer, Integer, String, String, Integer)
     * @see  ProductService#getCountForServicesWithSearch(User, String, Integer)
     * @see GridRequestDTO
     */
    @RequestMapping(value = {"csr/Services", "residential/Services", "business/Services"}, method = RequestMethod.GET)
    @ResponseBody
    public ListHolder showServices(@ModelAttribute GridRequestDTO request) {
        String sort = request.getSort();
        int start = request.getStartBorder();
        int length = request.getEndBorder();
        String search = request.getSearch();
        List<ServicesCatalogRowDTO> products = productService.getLimitedServicesForUser(currentUser, start, length, sort, search, categoryId);
        int size = productService.getCountForServicesWithSearch(currentUser, search, categoryId);
        logger.debug("Get products in interval:" + start + " : " + length);
        return ListHolder.create(products, size);
    }


    /**
     * This method controls activation of service.
     * It takes id of service and creates order for this service for user.
     *
     * @param serviceId id of service
     * @return message with result of operation
     * @see OrderService#activateOrder(Product, Order)
     */
    @RequestMapping(value = {"csr/activateService", "residential/activateService", "business/activateService"}, method = RequestMethod.POST)
    @ResponseBody
    public String activateService(@RequestParam Integer serviceId) {
        Product product = productDAO.getById(serviceId);
        Order order = new Order();
        String msg;
        order.setProductId(serviceId);
        order.setUserId(currentUser.getId());
        if (product.getProcessingStrategy() == ProcessingStrategy.NeedProcessing) {
            order.setCurrentStatus(OperationStatus.InProcessing);
            msg = String.format(ORDER_IN_PROCESS_MSG, product.getName());
        } else {
            order.setCurrentStatus(OperationStatus.Active);
            msg = String.format(SERVICE_WAS_ACTIVATED_MSG, product.getName());

        }
        boolean isActivated = orderService.activateOrder(product, order);
        if (!isActivated) {
            msg = ERROR_PLACING_ORDER_MSG;
            logger.warn("Error while placing order: {} ", order.toString());
        }
        logger.debug("Order for service: {} was placed", serviceId);
        return msg;

    }


    /**
     * This method controls deactivation of particular product for user.
     *
     * @param serviceId id of service
     * @return success of operation
     * @see SharedVariables
     * @see OrderService#deactivateOrderCompletely(Integer)
     */
    @RequestMapping(value = {"csr/deactivateService", "business/deactivateService", "residential/deactivateService"}, method = RequestMethod.POST)
    @ResponseBody
    public String deactivateOrder(@RequestParam Integer serviceId) {
        Boolean wasDeactivated = orderService.deactivateOrderForProductOfUserCompletely(serviceId, currentUser.getId());
        if (wasDeactivated) {
            logger.info("Successful deactivation of order (product_id : {}, user_id: {})", serviceId,
                    currentUser.getId());
            return SharedVariables.SUCCESS;
        } else {
            logger.error("Error while deactivating order(product_id : {}, user_id: {})", serviceId,
                    currentUser.getId());
        }
        return SharedVariables.FAIL;

    }
}
