package jtelecom.controller.admin;

import jtelecom.dao.entity.CustomerType;
import jtelecom.dao.product.Product;
import jtelecom.dao.product.ProductDAO;
import jtelecom.dao.product.ProductType;
import jtelecom.services.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Anna Rysakova
 */
@Controller
@RequestMapping({"admin"})
public class FillTariffWithServicesController {

    private static final String ERROR_WITH_DB = "Error with filling database";
    private static final String ERROR_FILL_IN_TARIFF_SERVICES = "Please, select services to tariff";
    private static final String ERROR_EXIST = "Sorry, input data is invalid";
    private static Logger logger = LoggerFactory.getLogger(FillTariffWithServicesController.class);
    @Resource
    private ProductDAO productDAO;
    @Resource
    private ProductService productService;

    /**
     * The method gets {@code Map} where the key is the category name,
     * the value is the {@code List} of services for this category
     * Method refers to the page of tariff filling by services
     * Check that product type is 'Tariff plan'
     *
     * @param mav representation of the model and view
     * @return {@code ModelAndView} with all {@code Place} model attribute
     * @see Product
     * @see List
     * @see Map
     */
    @RequestMapping(value = {"fillTariff"}, method = RequestMethod.GET)
    public ModelAndView fillTariffWithService(@RequestParam(value = "tariffId") Integer tariffId,
                                              ModelAndView mav) {

        String productType = productDAO.getProductTypeByProductId(tariffId);
        if (Objects.equals(productType, ProductType.Service.getName())) {
            mav.setViewName("redirect:/admin/getProducts");
            return mav;
        }
        Map<String, List<Product>> allServicesWithCategory = productDAO.getAllServicesWithCategory();
        logger.debug("Get all service's categories {} ", allServicesWithCategory.toString());

        mav.addObject("allServicesWithCategory", allServicesWithCategory);
        mav.addObject("tariffId", tariffId);
        mav.setViewName("newPages/admin/fillTariff");
        return mav;
    }

    /**
     * The method fills the product type 'tariff plan' by the services.
     * Checks that all fields are set correctly. If the field is set incorrectly - returns
     * to the same page with an error message.
     * Redirect to the page for filling the product with prices if {@code Product} type
     * is 'Residential'. If {@code Product} type 'Business' redirect to view with
     * all products.
     *
     * @param tariffId        {@code Product} ID type 'Tariff plan'
     * @param mav             representation of the model and view
     * @param servicesIdArray array of services ID
     * @return to controller with all products if product customer type 'Business',
     * return to controller with fill prices by regions if product type 'Residential'
     * @see Product
     */
    @RequestMapping(value = {"fillTariff"}, method = RequestMethod.POST)
    public ModelAndView identifyTariff(@RequestParam(value = "tariffId") Integer tariffId,
                                       @RequestParam(value = "selectedService") Integer[] servicesIdArray,
                                       ModelAndView mav) {

        logger.debug("Receive tariff ID {} ", tariffId);
        Product foundProduct = productService.isValidProduct(tariffId);
        if (!Objects.nonNull(foundProduct) || foundProduct.getProductType() == ProductType.Service) {
            logger.error("Product with ID = {}  does not exist in the database ", tariffId);
            mav.addObject("error", ERROR_EXIST);
            mav.setViewName("newPages/admin/products");
        }

        if (!Objects.nonNull(servicesIdArray)) {
            logger.error("Incoming data error with services ");
            mav.addObject("error", ERROR_FILL_IN_TARIFF_SERVICES);
            mav.setViewName("newPages/admin/fillTariff?tariffId=" + tariffId);
            return mav;
        }

        try {
            productService.fillInTariffWithServices(tariffId, servicesIdArray);
            logger.debug("Fill in tariff with services to database");
        } catch (DataAccessException ex) {
            logger.error("Error with filling database {} ", ex.getMessage());
            mav.addObject("error ", ERROR_WITH_DB);
            mav.setViewName("newPages/admin/fillTariff?tariffId=" + tariffId);
            return mav;
        }
        Product product = productDAO.getById(tariffId);
        if (product.getCustomerType() == CustomerType.Business) {
            mav.setViewName("redirect:/admin/getProducts");
        }
        if (product.getCustomerType() == CustomerType.Residential) {
            mav.setViewName("redirect:/admin/fillProductPrices?id=" + tariffId);
        }
        return mav;
    }
}

