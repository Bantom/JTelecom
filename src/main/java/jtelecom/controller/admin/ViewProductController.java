package jtelecom.controller.admin;

import jtelecom.dao.entity.CustomerType;
import jtelecom.dao.product.Product;
import jtelecom.dao.product.ProductCategories;
import jtelecom.dao.product.ProductDAO;
import jtelecom.dao.product.ProductType;
import jtelecom.services.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author Anna Rysakova
 * @author Nikita Alistratenko
 */
@Controller
@RequestMapping({"admin"})
public class ViewProductController {

    private static Logger logger = LoggerFactory.getLogger(ViewProductController.class);
    private static final String ERROR_EXIST = "Sorry, product doesn't exist";
    @Resource
    private ProductDAO productDAO;
    @Resource
    private ProductService productService;

    /**
     * Method refers to view with all exist products
     *
     * @return refers to view with all exist products
     */
    @RequestMapping(value = {"getProducts"}, method = RequestMethod.GET)
    public ModelAndView getProducts() {
        return new ModelAndView("newPages/admin/products");
    }

    /**
     * Method refers to view with product details. Checks that product exist.
     * If product doesn't exist refers to view with all products.
     *
     * @param id  {@code Product} ID
     * @param mav representation of the model and view
     * @return view with product details.
     * If product doesn't exist refers to view with all products.
     */
    @RequestMapping(value = "getDetailsProduct", method = RequestMethod.GET)
    public ModelAndView getDetailsProduct(@RequestParam(value = "id") int id,
                                          ModelAndView mav) {
        Product foundProduct = productService.isValidProduct(id);
        logger.debug("Product found in database, id={} ", id);
        mav.addObject("product", foundProduct);

        if (!Objects.nonNull(foundProduct)) {
            logger.error("Product with ID = {}  does not exist in the database ", id);
            mav.addObject("error", ERROR_EXIST);
            mav.setViewName("newPages/admin/products");
        }
        if (foundProduct.getProductType() == ProductType.Service) {
            ProductCategories category = productDAO.getProductCategoryById(foundProduct.getCategoryId());
            logger.debug("Service category is {} ", category.getCategoryName());
            mav.addObject("category", category);
        }

        if (foundProduct.getCustomerType() == CustomerType.Residential) {
            logger.debug("Product customer type is {} ", CustomerType.Residential.getName());
            mav.setViewName("newPages/admin/updateProduct");
        }
        if (foundProduct.getCustomerType() == CustomerType.Business) {
            logger.debug("Product customer type is {} ", CustomerType.Business.getName());
            mav.setViewName("newPages/admin/updateProductBusiness");
        }
        return mav;
    }

    /**
     * Changes product status to opposite
     * @param id id of the product to update
     * @param attributes attribute to add result of updating in
     * @return redirects to getProduct page
     */
    @RequestMapping("disableEnableProduct")
    public ModelAndView setProductDisabledEnabled(@RequestParam(value = "id") int id, RedirectAttributes attributes) {
        logger.debug("Product sent to get status changed, id = ", id);
        ModelAndView mw = new ModelAndView();
        if (productService.disableEnableProduct(id)) {
            attributes.addFlashAttribute("msg", "Product status has been changed");
            logger.debug("Product status has been changed, id = ", id);
        } else {
            attributes.addFlashAttribute("msg", "Product status has not been changed");
            logger.error("Product status has not been changed, id = ", id);
        }
        mw.setViewName("redirect:/admin/getProducts");
        return mw;
    }
}

