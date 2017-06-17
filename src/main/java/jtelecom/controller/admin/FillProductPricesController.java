package jtelecom.controller.admin;

import jtelecom.dao.entity.CustomerType;
import jtelecom.dao.place.Place;
import jtelecom.dao.place.PlaceDAO;
import jtelecom.dao.price.Price;
import jtelecom.dao.price.PriceDAO;
import jtelecom.dao.product.Product;
import jtelecom.dao.product.ProductDAO;
import jtelecom.services.price.PriceService;
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

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Anna Rysakova
 * @since 9.05.2017
 */
@Controller
@RequestMapping({"admin"})
public class FillProductPricesController {

    private static final String ERROR_WITH_DB = "Error with filling database";
    private static final String ERROR_FILL_IN_PRICE_BY_PRODUCT = "Please, check that the region was selected and price input";
    private static Logger logger = LoggerFactory.getLogger(FillProductPricesController.class);
    @Resource
    private PlaceDAO placeDAO;
    @Resource
    private PriceService priceService;
    @Resource
    private ProductService productService;
    @Resource
    private PriceDAO priceDAO;
    @Resource
    private ProductDAO productDAO;

    /**
     * Method refers to the page of product filling by the price by regions
     * Check that product customer type is "Residential"
     *
     * @param mav representation of the model and view
     * @return {@code ModelAndView} with all {@code Place} model attribute
     * @see Place
     */
    @RequestMapping(value = {"fillProductPrices"}, method = RequestMethod.GET)
    public ModelAndView getRegionForFill(@RequestParam(value = "id") Integer id,
                                         ModelAndView mav) {

        List<Place> placesForFillInTariff = placeDAO.getAllPlaces();
        logger.debug("Get products that do not have a price by region {} ", placesForFillInTariff.toString());

        String customerType = productDAO.getCustomerTypeByProductId(id);
        if (Objects.equals(customerType, CustomerType.Business.getName())) {
            mav.setViewName("redirect:/admin/getProducts");
            return mav;
        }

        mav.addObject("id", id);
        mav.addObject("placesForFillInTariff", placesForFillInTariff);
        mav.setViewName("newPages/admin/fillProductPrices");
        return mav;
    }

    /**
     * The method fills the product type "resident" by the price by region.
     * Checks that all fields are set correctly. If the field is set incorrectly - returns
     * to the same page with an error message. If data set correctly redirect
     * to view with all products.
     *
     * @param productId     {@code Product} ID
     * @param mav           representation of the model and view
     * @param placeId       array of {@code Place} ID
     * @param priceByRegion array of price by region
     * @return to controller with all products
     * @see Product
     * @see Place
     * @see Price
     */
    @RequestMapping(value = {"fillProductPrices/{id}"}, method = RequestMethod.POST)
    public ModelAndView fillPriceByRegion(@PathVariable(value = "id") Integer productId,
                                          ModelAndView mav,
                                          @RequestParam(value = "placeId") Integer[] placeId,
                                          @RequestParam(value = "priceByRegion") BigDecimal[] priceByRegion
    ) {
        Product validProduct = productService.isValidProduct(productId);
        logger.debug("Receive Product object {}", validProduct);
        boolean isValid = priceService.isValid(placeId, priceByRegion);

        if (!Objects.nonNull(validProduct) || isValid) {
            logger.error("Incoming data of place ID and is not correct {} {}",
                    Arrays.toString(placeId), Arrays.toString(priceByRegion));
            List<Place> placesForFillInTariff = placeDAO.getAllPlaces();
            logger.debug("Get products that do not have a price by region {} ", placesForFillInTariff.toString());

            mav.addObject("placesForFillInTariff", placesForFillInTariff);
            mav.addObject("error", ERROR_FILL_IN_PRICE_BY_PRODUCT);
            mav.setViewName("newPages/admin/fillProductPrices?id=" + productId);
            return mav;
        }

        try {
            List<Price> priceArrayList = priceService.fillInListWithProductPriceByRegion(productId, placeId, priceByRegion);
            boolean isFillPrice = priceDAO.fillPriceOfProductByRegion(priceArrayList);
            logger.debug("Fill in tariff with services to database with success {} ", isFillPrice);

        } catch (DataAccessException ex) {
            logger.error("Error with filling database {} ", ex.getMessage());
            List<Place> placesForFillInTariff = placeDAO.getAllPlaces();
            logger.debug("Get products that do not have a price by region {} ", placesForFillInTariff.toString());

            mav.addObject("placesForFillInTariff", placesForFillInTariff);
            mav.addObject("error ", ERROR_WITH_DB);
            mav.setViewName("newPages/admin/fillProductPrices?id=" + productId);
            return mav;
        }
        mav.setViewName("redirect:/admin/getProducts");
        return mav;

    }

}
