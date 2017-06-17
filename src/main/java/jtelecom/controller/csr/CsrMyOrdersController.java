package jtelecom.controller.csr;

import jtelecom.dao.order.OrderDAO;
import jtelecom.dao.user.User;
import jtelecom.dao.user.UserDAO;
import jtelecom.dto.FullInfoOrderDTO;
import jtelecom.dto.grid.GridRequestDTO;
import jtelecom.dto.grid.ListHolder;
import jtelecom.security.SecurityAuthenticationHelper;
import jtelecom.services.orders.OrderService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Moiseienko Petro
 * @since 16.05.2017.
 */
@RestController
@RequestMapping({"csr"})
public class CsrMyOrdersController {

    @Resource
    private OrderDAO orderDAO;
    @Resource
    private SecurityAuthenticationHelper securityAuthenticationHelper;
    @Resource
    private UserDAO userDAO;
    @Resource
    private OrderService orderService;
    private final static String SUCCESS = "success";
    private final static String FAIL = "fail";

    @RequestMapping(value = "getMyOrdersPage", method = RequestMethod.GET)
    public ModelAndView getMyOrdersPage() {
        return new ModelAndView("newPages/csr/MyOrders");
    }


    /**
     * This method gets GridRequestDTO( see the {@link GridRequestDTO} <br>.
     * After method gets list with all orders that assigned to the csr from database.<br>.
     * Method gets csr from the security current user.
     *
     * @param requestDto -contains indexes for first element and last elements and patterns for search and sort
     * @return class which contains number of all elements with such parameters and some interval of the data
     */
    @RequestMapping(value = "getMyOrders", method = RequestMethod.GET)
    public ListHolder getAllOrders(@ModelAttribute GridRequestDTO requestDto) {
        User user = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        Integer csrId = user.getId();
        Integer start = requestDto.getStartBorder();
        Integer length = requestDto.getEndBorder();
        String sort = requestDto.getSort();
        String search = requestDto.getSearch();
        Integer count = orderDAO.getCountOfInprocessingOrdersByCsrId(csrId, search);
        List<FullInfoOrderDTO> orders = orderDAO.getIntervalProcessingOrdersByCsrId(start, length, sort, search, csrId);
        return ListHolder.create(orders, count);
    }

    /**
     * Method gets id of the order, gets this order's full info <br>
     * and redirects to the order's page
     *
     * @param orderId id of the order
     * @return model with this order's info
     */
    @RequestMapping(value = "getOrderPage")
    public ModelAndView getOrderPage(@RequestParam(name = "id") Integer orderId) {
        ModelAndView modelAndView = new ModelAndView("newPages/csr/OrderPage");
        FullInfoOrderDTO order = orderDAO.getOrderInfoByOrderId(orderId);
        modelAndView.addObject("order", order);
        return modelAndView;
    }

    /**
     * This method activates order
     *
     * @param orderId id of the order
     * @return message about success of the operation
     */
    @RequestMapping(value = "activateOrder", method = RequestMethod.POST)
    public String activateOrder(@RequestParam(value = "orderId") int orderId) {
        boolean success = orderService.activateOrderFromCsr(orderId);
        return success ? SUCCESS : FAIL;
    }

    /**
     * This method sends custom email from csr to client
     *
     * @param orderId id of the order which assigned to the csr
     * @param text    text of the message
     * @return message about success of the operation
     */
    @RequestMapping(value = "sendEmail", method = RequestMethod.POST)
    public String sendEmail(@RequestParam(value = "orderId") int orderId,
                            @RequestParam(value = "text") String text) {
        String email = securityAuthenticationHelper.getCurrentUser().getUsername();
        boolean success = orderService.sendEmail(orderId, text, email);
        return success ? SUCCESS : FAIL;
    }


}
