package jtelecom.controller.user;

import jtelecom.dao.product.Product;
import jtelecom.dao.user.User;
import jtelecom.dao.user.UserDAO;
import jtelecom.security.SecurityAuthenticationHelper;
import jtelecom.services.product.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

/**
 * This class consists solely of methods, which main goal is to show info
 * about Product for user with different roles.
 *
 * @author Yuliya Pedash
 * @see jtelecom.dao.user.Role
 * @see User
 * @see #showProductInfoForUser(Model, Integer)
 */
@Controller
public class ProductInfoController {
    @Resource
    private SecurityAuthenticationHelper securityAuthenticationHelper;
    @Resource
    private ProductService productService;
    @Resource
    private UserDAO userDAO;


    private void showProductInfoForUser(Model model, Integer productId) {
        User currentUser = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        Product product = productService.getProductForUser(currentUser, productId);
        model.addAttribute("product", product);
    }

    @RequestMapping(value = {"csr/product"}, method = RequestMethod.GET)
    public String showProductInfoForCsr(Model model, @RequestParam(name = "productId") Integer productId) {
        showProductInfoForUser(model, productId);
        return "newPages/csr/Product";
    }

    @RequestMapping(value = {"residential/product"}, method = RequestMethod.GET)
    public String showProductInfoForRes(Model model, @RequestParam(name = "productId") Integer productId) {
        showProductInfoForUser(model, productId);
        return "newPages/residential/Product";
    }

    @RequestMapping(value = {"business/product"}, method = RequestMethod.GET)
    public String showProductInfoForBusiness(Model model, @RequestParam(name = "productId") Integer productId) {
        showProductInfoForUser(model, productId);
        return "newPages/business/Product";
    }

    @RequestMapping(value = {"employee/product"}, method = RequestMethod.GET)
    public String showProductInfoForEmp(Model model, @RequestParam(name = "productId") Integer productId) {
        showProductInfoForUser(model, productId);
        return "newPages/employee/Product";
    }

}
