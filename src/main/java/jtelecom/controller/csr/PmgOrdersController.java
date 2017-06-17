package jtelecom.controller.csr;

import jtelecom.dao.operationHistory.OperationHistoryDAO;
import jtelecom.dao.order.OrderDAO;
import jtelecom.dto.FullInfoOrderDTO;
import jtelecom.dto.grid.GridRequestDTO;
import jtelecom.dto.grid.ListHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author Moiseienko Petro
 * @since 14.05.2017.
 */
@RestController
@RequestMapping({"pmg"})
public class PmgOrdersController {
    @Resource
    private OrderDAO orderDAO;
    @Resource
    private OperationHistoryDAO operationHistoryDAO;


    /**
     * This method gets GridRequestDTO( see the {@link GridRequestDTO}. <br>
     * After method gets list with all orders of client from database.<br>
     * This client's id method gets from the session.
     *
     * @param request -contains indexes for first element and last elements and patterns for search and sort
     * @return class which contains number of all elements with such parameters and some interval of the data
     */
    @RequestMapping(value = "getOrders", method = RequestMethod.GET)
    public ListHolder getOrders(@ModelAttribute GridRequestDTO request, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        int start = request.getStartBorder();
        int length = request.getEndBorder();
        String sort = request.getSort();
        String search = request.getSearch();
        Integer count = orderDAO.getCountOrdersByUserId(userId, search);
        List<FullInfoOrderDTO> orders = orderDAO.getIntervalOrdersByUserId(start, length, sort, search, userId);
        return ListHolder.create(orders, count);
    }

    /**
     * Method gets the interval of full information about operations of the some order
     *
     * @param startIndex first index to search
     * @param endIndex   last index to search
     * @param orderId    id of the order
     * @return class which contains number of all elements with such parameters and some interval of the data
     */
    @RequestMapping(value = "getHistory", method = RequestMethod.GET)
    public ListHolder getData(@RequestParam(name = "start") int startIndex,
                              @RequestParam(name = "end") int endIndex,
                              @RequestParam(name = "orderId") int orderId) {
        Integer amount = operationHistoryDAO.getCountOperationsByOrderId(orderId);
        List<FullInfoOrderDTO> history = operationHistoryDAO.getIntervalOfOperationsByOrderId(startIndex, endIndex, orderId);
        return ListHolder.create(history, amount);


    }
}
