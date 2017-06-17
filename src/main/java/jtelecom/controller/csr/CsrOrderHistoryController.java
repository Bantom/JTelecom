package jtelecom.controller.csr;

import jtelecom.dao.order.OrderDAO;
import jtelecom.dao.user.User;
import jtelecom.dao.user.UserDAO;
import jtelecom.dto.FullInfoOrderDTO;
import jtelecom.dto.grid.GridRequestDTO;
import jtelecom.dto.grid.ListHolder;
import jtelecom.security.SecurityAuthenticationHelper;
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
@RequestMapping({"csr"})
public class CsrOrderHistoryController {
    @Resource
    private OrderDAO orderDAO;
    @Resource
    private SecurityAuthenticationHelper securityAuthenticationHelper;
    @Resource
    private UserDAO userDAO;

    @RequestMapping(value = "getHistoryOrdersPage", method = RequestMethod.GET)
    public ModelAndView getMyOrdersPage() {
        return new ModelAndView("newPages/csr/OrderHistory");
    }


    /**
     * This method gets GridRequestDTO( see the {@link GridRequestDTO} <br>.
     * After method gets list with csr's all activated orders  from database.<br>
     * Method determines csr from the security current user.
     *
     * @param requestDto -contains indexes for first element and last elements and patterns for search and sort
     * @return class which contains number of all elements with such parameters and some interval of the data
     */
    @RequestMapping(value = "getHistoryOrders", method = RequestMethod.GET)
    public ListHolder getAllOrders(@ModelAttribute GridRequestDTO requestDto) {
        User user = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        Integer csrId = user.getId();
        Integer start = requestDto.getStartBorder();
        Integer length = requestDto.getEndBorder();
        String sort = requestDto.getSort();
        String search = requestDto.getSearch();
        Integer count = orderDAO.getCountOfProcessedOrdersByCsrId(csrId, search);
        System.out.println(count);
        List<FullInfoOrderDTO> orders = orderDAO.getIntervalProccesedOrdersByCsrId(start, length, sort, search, csrId);
        System.out.println(orders.size());
        return ListHolder.create(orders, count);
    }
}
