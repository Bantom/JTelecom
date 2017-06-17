package jtelecom.controller.csr;


import jtelecom.dao.customer.Customer;
import jtelecom.dao.customer.CustomerDAO;
import jtelecom.dao.user.User;
import jtelecom.dao.user.UserDAO;
import jtelecom.dto.grid.GridRequestDTO;
import jtelecom.dto.grid.ListHolder;
import jtelecom.security.SecurityAuthenticationHelper;
import jtelecom.services.customer.CustomerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author Moiseienko Petro
 * @author Nikita Alistratenko
 * @since 26.04.2017.
 */
@RestController
@RequestMapping({"csr", "admin"})
public class CustomerController {
    @Resource
    private CustomerDAO customerDAO;
    @Resource
    private UserDAO userDAO;

    private static Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private int customerID = 0;


    @RequestMapping(value = {"getCustomers"}, method = RequestMethod.GET)
    public ModelAndView getProducts() {
        return new ModelAndView("newPages/csr/Customers");
    }

    /**
     * Creates JSON with data for table in csrProducts jsp
     *
     * @param request info about table configuration
     * @return {@link ListHolder list} with data for table as JSON
     */
    @RequestMapping(value = {"allCustomers"}, method = RequestMethod.GET)
    public ListHolder servicesByTariff(@ModelAttribute GridRequestDTO request) {
        logger.debug("Created JSON for products page for csr");
        String sort = request.getSort();
        int start = request.getStartBorder();
        int length = request.getLength();
        String search = request.getSearch();
        List<Customer> data = customerDAO.getLimitedQuantityCustomer(start, length, sort, search);
        int size = customerDAO.getCountCustomersWithSearch(search);
        return ListHolder.create(data, size);
    }

    /**
     *
     * @param customerID
     * @return
     */
    @RequestMapping(value = "getCustomerInfo/{id}", method = RequestMethod.GET)
    public ModelAndView getCustomerInfoPage(@PathVariable("id") int customerID) {
        this.customerID = customerID;
        ModelAndView mw = new ModelAndView();
        Customer customer = customerDAO.getById(customerID);
        logger.debug("Customer {} requests for info", customerID);
        List<User> usersOfCustomer = customerDAO.getAllUsers(customerID);
        logger.debug("users: " + Objects.toString(usersOfCustomer));
        mw.addObject("customer", customer);
        mw.addObject("userList", usersOfCustomer);
        mw.setViewName("newPages/csr/CustomerInfoPage");
        return mw;
    }

    @RequestMapping(value = {"AllUsersOfCustomer"}, method = RequestMethod.GET)
    public ListHolder allUsersOfCustomer(@ModelAttribute GridRequestDTO request) {
        String sort = request.getSort();
        int start = request.getStartBorder();
        int length = request.getLength();
        String search = request.getSearch();
        List<User> data = userDAO.getLimitedQuantityUsersOfCustomer(start, length, sort, search, customerID);
        int size = userDAO.getCountUsersWithSearchOfCustomer(search, customerID);
        return ListHolder.create(data, size);
    }
}
