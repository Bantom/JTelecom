package jtelecom.controller.admin;

import jtelecom.dao.product.Product;
import jtelecom.services.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author Anna Rysakova
 */
@Controller
@RequestMapping({"admin"})
public class UpdateProductController {

    private static final String ERROR_WITH_DB = "Error with filling database";
    private static final String ERROR_EXIST = "Sorry, input data is invalid";
    private final static String ERROR_EXIST_PRODUCT_NAME = "Product name already exists";
    private static Logger logger = LoggerFactory.getLogger(UpdateProductController.class);
    @Resource
    private ProductService productService;

    /**
     * Method update product and redirect to view with product info.
     * Checks that all {@code Product} fields are correctly entered.
     * If the wrong data - returns to the same page and displays an error message
     *
     * @param product    {@code Product} object
     * @param id         {@code Product} ID
     * @param mav        representation of the model and view
     * @param attributes needs for sending in form message about success of the operation
     *                   return to controller with info about {@code Product}
     * @return redirect to controller with product's details
     * If the wrong data - returns to the same page and displays an error message
     * @see Product
     * @see RedirectAttributes
     */
    @RequestMapping(value = {"updateProduct"}, method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView updateTariff(Product product, @RequestParam(value = "id") Integer id,
                                     ModelAndView mav, RedirectAttributes attributes) {

        logger.debug("Receive product ID {} ", id);
        Product foundProduct = productService.isValidProduct(id);
        if (!Objects.nonNull(foundProduct)) {
            logger.error("Product with ID = {}  does not exist in the database ", id);
            mav.addObject("error", ERROR_EXIST);
            mav.setViewName("redirect:/admin/getProducts");
        }
        product.setId(id);
        logger.debug("Write ID to product {} ", product.getId());

        boolean isExistProductName = productService.isExistProductNameForUpdate(product, id);
        if (isExistProductName) {
            logger.error("Tariff name already exist in database");
            mav.setViewName("redirect:/admin/getDetailsProduct?id=" + id);
            return mav;
        }

        try {
            boolean isUpdate = productService.updateProduct(product);
            logger.debug("Update fields of product with success {} ", isUpdate);
            attributes.addFlashAttribute("msg", "Successfully updating");
        } catch (DataAccessException ex) {
            logger.error("Error with filling database {}", ex.getMessage());
            mav.addObject("error ", ERROR_WITH_DB);
            attributes.addFlashAttribute("msg", "Sorry, try again later");
            return mav;
        }

        mav.setViewName("redirect:/admin/getDetailsProduct?id=" + id);
        return mav;
    }
}
