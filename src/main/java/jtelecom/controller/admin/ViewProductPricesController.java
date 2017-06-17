package jtelecom.controller.admin;

import jtelecom.dao.product.Product;
import jtelecom.dao.product.ProductDAO;
import jtelecom.services.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author Anna Rysakova
 */
@Controller
@RequestMapping({"admin"})
public class ViewProductPricesController {

    private static Logger logger = LoggerFactory.getLogger(ViewProductPricesController.class);
    private static final String ERROR_EXIST = "Sorry, product doesn't exist";
    @Resource
    private ProductDAO productDAO;
    @Resource
    private ProductService productService;

    /**
     * Method refers to view with all places where user may see
     * product price info by link at view.
     *
     * @param mav representation of the model and view
     * @return refers to view with all places where user may see
     * product price info by link at view.
     */
    @RequestMapping(value = "viewAllPlaces", method = RequestMethod.GET)
    public ModelAndView getAllProductPrice(ModelAndView mav) {
        mav.setViewName("newPages/admin/viewAllPlaces");
        return mav;
    }

    /**
     * Method refers to view with product's prices in regions.
     * Check that {@code Product} exist. If doesn't exist refers
     * to view with all products.
     *
     * @param productId {@code Product} ID
     * @param mav       representation of the model and view
     * @return refers to view with product's prices in regions.
     * If product doesn't exist - refers to view with all products.
     * @see Product
     */
    @RequestMapping(value = "viewProductPriceInRegions", method = RequestMethod.GET)
    public ModelAndView getProductPriceForRegions(@RequestParam("id") Integer productId,
                                                  ModelAndView mav) {
        Product validProduct = productService.isValidProduct(productId);
        if (!Objects.nonNull(validProduct)) {
            mav.addObject("error", ERROR_EXIST);
            mav.setViewName("newPages/admin/products");
        }
        String productType = productDAO.getProductTypeByProductId(productId);
        logger.debug("Product type: {} ", productType);
        mav.addObject("id", productId);
        logger.debug("Product id: {}", productId);
        mav.addObject("productType", productType);
        mav.setViewName("newPages/admin/viewRegionPrices");
        return mav;
    }
}