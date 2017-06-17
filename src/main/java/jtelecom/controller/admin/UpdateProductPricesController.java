package jtelecom.controller.admin;

import jtelecom.dao.price.PriceDAO;
import jtelecom.dao.product.Product;
import jtelecom.dao.product.ProductDAO;
import jtelecom.dto.PriceByRegionDTO;
import jtelecom.services.price.PriceService;
import jtelecom.services.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Anna Rysakova
 */
@Controller
@RequestMapping({"admin"})
public class UpdateProductPricesController {

    private static final String ERROR_FILL_IN_PRICE_BY_PRODUCT = "Please, check that the region was selected and price input";
    private static final String ERROR_EXIST = "Sorry, input data is invalid";
    private static Logger logger = LoggerFactory.getLogger(UpdateProductPricesController.class);

    @Resource
    private PriceService priceService;
    @Resource
    private ProductService productService;
    @Resource
    private PriceDAO priceDAO;
    @Resource
    private ProductDAO productDAO;

    /**
     * Method refers to view with update product prices.
     * The method checks that there is an correct object with an {@code ID}. If not - returns to the page
     * with all products. Receives a list of {@code PriceByRegionDTO} with {@code Product} prices in regions
     *
     * @param mav       representation of the model and view
     * @param productId {@code Product} ID
     * @return {@code ModelAndView} with all product prices in regions
     * @see Product
     * @see PriceByRegionDTO
     * @see List
     */
    @RequestMapping(value = {"updateProductPrice"}, method = RequestMethod.GET)
    public ModelAndView getPriceByRegion(@RequestParam(value = "id") Integer productId,
                                         ModelAndView mav) {
        logger.debug("Receive product's id {} ", productId);
        Product validProduct = productService.isValidProduct(productId);
        if (!Objects.nonNull(validProduct)) {
            mav.addObject("message", ERROR_EXIST);
            mav.setViewName("redirect:/admin/getProducts");
            return mav;
        }
        List<PriceByRegionDTO> placesAndPrice = priceDAO.getAllRegionsAndProductPriceInRegionByProductId(productId);
        logger.debug("Get all places and product prices if it exist {} ", placesAndPrice.toString());
        String productType = productDAO.getProductTypeByProductId(productId);

        mav.addObject("placesAndPrice", placesAndPrice);
        mav.addObject("productType", productType);
        mav.setViewName("newPages/admin/updateProductPrices");
        return mav;
    }

    /**
     * Methods update product prices in regions. Redirect to view with all product's
     * prices in regions.
     * Checks that {@code placeId} and {@code PriceByRegion} are correctly entered.
     * If the wrong data - returns to the same page and displays an error message
     *
     * @param productId     {@code Product} ID
     * @param placeId       array of place ID
     * @param priceByRegion array of prices by region
     * @param mav           representation of the model and view
     * @param attributes    needs for sending in form message about success of the operation
     *                      return to controller with info about {@code Product}
     * @return redirect to view with all prices in regions by product.
     * If the wrong data - returns to the same page and displays an error message
     * @see Product
     * @see RedirectAttributes
     */
    @RequestMapping(value = {"updateProductPrices"}, method = RequestMethod.POST)
    public ModelAndView fillPriceByRegion(ModelAndView mav,
                                          @RequestParam(value = "id") Integer productId,
                                          @RequestParam(value = "placeId") Integer[] placeId,
                                          @RequestParam(value = "priceByRegion") BigDecimal[] priceByRegion,
                                          RedirectAttributes attributes
    ) {
        logger.debug("Receive product's id {} ", productId);
        Product validProduct = productService.isValidProduct(productId);
        boolean isValid = priceService.isValid(placeId, priceByRegion);
        if (!Objects.nonNull(validProduct)) {
            mav.addObject("error", ERROR_EXIST);
            mav.setViewName("newPages/admin/products");
        }
        if (isValid) {
            logger.error("Incoming data of place ID and is not correct {} {}",
                    Arrays.toString(placeId), Arrays.toString(priceByRegion));
            List<PriceByRegionDTO> placesAndPrice = priceDAO.getAllRegionsAndProductPriceInRegionByProductId(productId);
            logger.debug("Get all places and product prices if it exist {} ", placesAndPrice.toString());

            mav.addObject("placesAndPrice", placesAndPrice);
            mav.addObject("error", ERROR_FILL_IN_PRICE_BY_PRODUCT);
            mav.setViewName("redirect:admin/updateProductPrice?id=" + productId);

            return mav;
        }

        try {
            priceService.updateProductPriceInRegions(productId, placeId, priceByRegion);
            logger.debug("Update price in regions by product ");
            attributes.addFlashAttribute("msg", "Successfully updating");
        } catch (DataAccessException ex) {
            logger.error("Error with filling database {} ", ex.getMessage());
            List<PriceByRegionDTO> placesAndPrice = priceDAO.getAllRegionsAndProductPriceInRegionByProductId(productId);
            logger.debug("Get all places and product prices if it exist {} ", placesAndPrice.toString());

            mav.addObject("placesAndPrice", placesAndPrice);
            mav.addObject("error", ERROR_FILL_IN_PRICE_BY_PRODUCT);
            attributes.addFlashAttribute("msg", "Sorry, try again later");
            mav.setViewName("newPages/admin/updateProductPrice?id=" + productId);
            return mav;
        }
        mav.setViewName("redirect:/admin/viewProductPriceInRegions?id=" + productId);
        return mav;

    }

}
