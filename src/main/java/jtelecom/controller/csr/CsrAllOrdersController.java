package jtelecom.controller.csr;

import jtelecom.dao.order.OrderDAO;
import jtelecom.dao.user.User;
import jtelecom.dao.user.UserDAO;
import jtelecom.dto.FullInfoOrderDTO;
import jtelecom.dto.grid.GridRequestDTO;
import jtelecom.dto.grid.ListHolder;
import jtelecom.security.SecurityAuthenticationHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Moiseienko Petro
 * @since 15.05.2017.
 */
@RestController
@RequestMapping({"csr"})
public class CsrAllOrdersController {
    @Resource
    private OrderDAO orderDAO;
    @Resource
    private SecurityAuthenticationHelper securityAuthenticationHelper;
    @Resource
    private UserDAO userDAO;

    @RequestMapping(value = "getAllOrdersPage", method = RequestMethod.GET)
    public ModelAndView getAllOrdersPage() {
        return new ModelAndView("newPages/csr/AllOrders");
    }


    /**
     * This method gets GridRequestDTO( see the {@link GridRequestDTO}. <br>
     * After method gets list with all orders that are in processing status <br>
     * from database.<br>
     * @param requestDto  contains indexes for first element and last elements and patterns for search and sort.
     * @return class which contains number of all elements with such parameters and some interval of the data
     */
    @RequestMapping(value = "getAllOrders", method = RequestMethod.GET)
    public ListHolder getAllOrders(@ModelAttribute GridRequestDTO requestDto) {
        Integer start = requestDto.getStartBorder();
        Integer length = requestDto.getEndBorder();
        String sort = requestDto.getSort();
        String search = requestDto.getSearch();
        Integer count = orderDAO.getCountOrdersWithoutCsr(search);
        List<FullInfoOrderDTO> orders = orderDAO.getIntervalOrdersWithoutCsr(start, length, sort, search);
        return ListHolder.create(orders, count);
    }

    /**
     * This method gets full info of the order<br>
     * For more information about full info see {@link jtelecom.dto.FullInfoOrderDTO}
     * @param orderId  id of the order
     * @return string that contains full info
     */
    @RequestMapping(value = "orderInfo", method = RequestMethod.POST)
    public String orderInfo(@RequestParam(value = "orderId") int orderId) {
        FullInfoOrderDTO order = orderDAO.getOrderInfoByOrderId(orderId);
        order.setOrderId(orderId);
        return order.infoMessage();
    }

    /**
     * This method assigns some order to the csr<br>
     * Csr is determined based on the current user<br>
     * @param orderId id of the order
     * @return message about success of the operation
     */
    @RequestMapping(value = "assignOrder", method = RequestMethod.POST)
    public String assignOrder(@RequestParam(value = "orderId") int orderId) {
        User user = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        boolean success = orderDAO.assignToUser(user.getId(), orderId);
        return success ? "success" : "fail";
    }
}
