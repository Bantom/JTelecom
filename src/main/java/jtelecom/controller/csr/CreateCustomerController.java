package jtelecom.controller.csr;


import jtelecom.dao.customer.Customer;
import jtelecom.dao.entity.CustomerType;
import jtelecom.dao.user.Role;
import jtelecom.dao.user.User;
import jtelecom.dao.user.UserDAO;
import jtelecom.security.SecurityAuthenticationHelper;
import jtelecom.services.customer.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;

/**
 * @author Moiseienko Petro
 * @since 12.05.2017.
 */
@Controller
@RequestMapping({"csr", "admin"})
public class CreateCustomerController {

    @Resource
    private SecurityAuthenticationHelper securityAuthenticationHelper;
    @Resource
    private UserDAO userDAO;
    @Resource
    private CustomerService customerService;

    private static final String MSG="msg";

    /**
     * This method refers to a some page of the creating customer.<br>
     * Address of this page determines based on the role of the user
     * @return model which contains Customer object and page's address
     */
    @RequestMapping(value = {"getCreateCustomer"}, method = RequestMethod.GET)
    public ModelAndView getCreateCustomer() {
        ModelAndView modelAndView = new ModelAndView();
        User user = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        if (user.getRole().equals(Role.Admin)) {
            modelAndView.setViewName("newPages/admin/RegNewCustomer");
        } else {
            modelAndView.setViewName("newPages/csr/RegNewCustomer");
        }
        modelAndView.addObject("customer", new Customer());

        return modelAndView;
    }


    /**
     * This method gets customer from the page and save it.<br>
     * Then redirects from the address based on the role of current user
     * @param customer - customer with filled fields "name" and "field"
     * @param attributes -needs for sending inform message about success of the operation
     * @return redirect to the method {@link CreateCustomerController#getCreateCustomer()}
     */
    @RequestMapping(value = {"createCustomer"}, method = RequestMethod.POST)
    public String createCustomer(Customer customer, RedirectAttributes attributes) {
        String redirect = "";
        User user = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        if (user.getRole() == Role.Admin) {
            redirect = "redirect:/admin/getCreateCustomer";
        }
        if (user.getRole() == Role.CSR) {
            redirect = "redirect:/csr/getCreateCustomer";
        }
        customer.setCustomerType(CustomerType.Business);
        String message = customerService.save(customer);
        if (message == null) {
            attributes.addFlashAttribute(MSG, "Customer created successfully");
        } else {
            attributes.addFlashAttribute(MSG, message);
        }
        return redirect;
    }
}
