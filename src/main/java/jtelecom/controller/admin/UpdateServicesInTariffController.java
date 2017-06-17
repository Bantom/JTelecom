package jtelecom.controller.admin;

import jtelecom.dao.product.Product;
import jtelecom.dao.product.ProductDAO;
import jtelecom.dao.product.ProductType;
import jtelecom.dto.TariffServiceDTO;
import jtelecom.services.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Anna Rysakova
 */
@Controller
@RequestMapping({"admin"})
public class UpdateServicesInTariffController {

    private static final String ERROR_FILL_IN_TARIFF_SERVICES = "Please, select services to tariff";
    private static final String ERROR_WITH_DB = "Error with filling database";
    private static final String ERROR_TYPE = "Wrong type of input data";
    private static final String ERROR_EXIST = "Sorry, input data is invalid";
    private static final String SUCCESS_UPDATE = "Successfully updating";
    private static Logger logger = LoggerFactory.getLogger(UpdateServicesInTariffController.class);
    @Resource
    private ProductDAO productDAO;
    @Resource
    private ProductService productService;

    /**
     * Method refers to view with update services by tariff.
     * Checks that there is an correct object with an {@code tariffId}. If not - returns to the page
     * with all products. Receives a list of {@code TariffServiceDTO} with services by {code tariffId}
     *
     * @param mav      representation of the model and view
     * @param tariffId {@code Product} tariff ID
     * @return {@code ModelAndView} with {@code List} services by tariff
     * @see Product
     * @see TariffServiceDTO
     * @see List
     */
    @RequestMapping(value = {"updateServicesInTariff/{id}"}, method = RequestMethod.GET)
    public ModelAndView getServicesInTariffForUpdate(@PathVariable(value = "id") Integer tariffId,
                                                     ModelAndView mav) {

        logger.debug("Receive product ID {} ", tariffId);
        Product foundProduct = productService.isValidProduct(tariffId);
        String productType = productDAO.getProductTypeByProductId(tariffId);
        if (!Objects.nonNull(foundProduct) || Objects.equals(productType, ProductType.Service.getName())) {
            logger.error("Product with ID = {}  does not exist in the database ", tariffId);
            mav.addObject("error", ERROR_EXIST);
            mav.setViewName("redirect:/admin/getProducts");
        }
        logger.debug("Receive tariff's id {} ", tariffId);
        List<TariffServiceDTO> servicesByTariff = productDAO.getServicesInfoByTariff(tariffId);
        logger.debug("Received services that are included in the tariff {}", servicesByTariff.toString());
        Map<String, List<Product>> allServicesWithCategory = productDAO.getServicesNotInTariff(tariffId);
        logger.debug("Get all service's categories {} ", allServicesWithCategory.toString());
        String customerType = productDAO.getCustomerTypeByProductId(tariffId);

        mav.addObject("allServicesWithCategory", allServicesWithCategory);
        mav.addObject("servicesByTariff", servicesByTariff);
        mav.addObject("customerType", customerType);
        mav.addObject("id", tariffId);
        mav.setViewName("newPages/admin/updateServicesInTariff");
        return mav;
    }

    /**
     * Method update {@code List} services in tariff. Redirect to view with all
     * services in tariff.
     * Checks that {@code ID} and {@code servicesIdArray} are correctly entered.
     * If the wrong data - returns to the same page and displays an error message
     *
     * @param id              {@code Product} ID
     * @param servicesIdArray array of place ID
     * @param mav             representation of the model and view
     * @param attributes      needs for sending in form message about success of the operation
     *                        return to controller with info about {@code Product}
     * @return redirect to view with all prices in regions by product.
     * If the wrong data - returns to the same page and displays an error message
     * @see Product
     * @see RedirectAttributes
     */
    @RequestMapping(value = {"updateServicesInTariff"}, method = RequestMethod.POST)
    public ModelAndView updateServicesInTariff(@RequestParam(value = "id") Integer id,
                                               @RequestParam(value = "selectedService") Integer[] servicesIdArray,
                                               RedirectAttributes attributes,
                                               ModelAndView mav) {

        logger.debug("Receive tariff's id {} ", id);
        Product foundProduct = productService.isValidProduct(id);
        if (!Objects.nonNull(foundProduct)) {
            logger.error("Product with ID = {}  does not exist in the database ", id);
            mav.addObject("error", ERROR_EXIST);
            mav.setViewName("newPages/admin/updateServicesInTariff/" + id);
        }

        if (!Objects.nonNull(servicesIdArray)) {
            logger.error("Incoming data error with services ");
            mav.addObject("error", ERROR_FILL_IN_TARIFF_SERVICES);
            mav.setViewName("newPages/admin/updateServicesInTariff/" + id);
            return mav;
        }

        try {
            logger.debug("Convert a string array of service's ID to an integer array {} ", Arrays.toString(servicesIdArray));
            productService.updateFillingOfTariffsWithServices(servicesIdArray, id);
            logger.debug("Update tariff: removed unnecessary services, added new services");
            attributes.addFlashAttribute("msg", SUCCESS_UPDATE);
            
        } catch (NumberFormatException e) {
            logger.error("Wrong parameter's type ", e.getMessage());
            attributes.addFlashAttribute("msg", ERROR_TYPE);
            mav.setViewName("newPages/admin/updateServicesInTariff/" + id);

        } catch (DataAccessException ex) {
            logger.error("Error with filling database {}", ex.getMessage());
            attributes.addFlashAttribute("msg", ERROR_WITH_DB);
            mav.setViewName("newPages/admin/updateServicesInTariff/" + id);
            return mav;
        }

        mav.setViewName("redirect:/admin/viewServicesInTariff?id=" + id);
        return mav;
    }

}
