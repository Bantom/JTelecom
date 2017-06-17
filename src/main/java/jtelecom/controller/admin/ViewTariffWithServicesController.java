package jtelecom.controller.admin;

import jtelecom.dao.product.Product;
import jtelecom.dao.product.ProductDAO;
import jtelecom.dto.TariffServiceDTO;
import jtelecom.services.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author Anna Rysakova
 */
@Controller
@RequestMapping({"admin"})
public class ViewTariffWithServicesController {
    private static final String ERROR_EXIST = "Sorry, product doesn't exist";
    private static Logger logger = LoggerFactory.getLogger(ViewTariffWithServicesController.class);
    @Resource
    private ProductDAO productDAO;
    @Resource
    private ProductService productService;

    /**
     * Method refers to view with info about services in tariff.
     * Check that {@code Product} exist. If doesn't exist refers
     * to view with all products.
     *
     * @param tariffId tariff ID
     * @param mav      representation of the model and view
     * @return refers to view with info about services in tariff.
     * If tariff  doesn't exist - refers to view with all products.
     */
    @RequestMapping(value = {"viewServicesInTariff"}, method = RequestMethod.GET)
    public ModelAndView getServicesInTariffForUpdate(@RequestParam(value = "id") Integer tariffId,
                                                     ModelAndView mav) {

        logger.debug("Receive tariff's id {} ", tariffId);
        Product validProduct = productService.isValidProduct(tariffId);
        if (!Objects.nonNull(validProduct)) {
            mav.addObject("error", ERROR_EXIST);
            mav.setViewName("newPages/admin/products");
        }

        List<TariffServiceDTO> servicesByTariff = productDAO.getServicesInfoByTariff(tariffId);
        logger.debug("Received services that are included in the tariff {}", servicesByTariff.toString());
        String customerType = productDAO.getCustomerTypeByProductId(tariffId);
        logger.debug("Customer type: {} ", customerType);

        mav.addObject("servicesByTariff", servicesByTariff);
        mav.addObject("customerType", customerType);
        mav.addObject("id", tariffId);
        mav.setViewName("newPages/admin/viewServicesInTariff");
        return mav;
    }
}
