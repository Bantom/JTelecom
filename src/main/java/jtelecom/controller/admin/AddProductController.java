package jtelecom.controller.admin;

import jtelecom.dao.entity.CustomerType;
import jtelecom.dao.product.Product;
import jtelecom.dao.product.ProductCategories;
import jtelecom.dao.product.ProductDAO;
import jtelecom.dao.product.ProductType;
import jtelecom.services.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Anna Rysakova
 * @since 23.04.2017
 */
@Controller
@RequestMapping({"admin"})
public class AddProductController {

    private final static String ERROR_WRONG_FIELDS = "Please, check the correctness of the input data";
    private final static String ERROR_EXIST_OF_CATEGORY = "Category already exists";
    private final static String ERROR_EXIST_PRODUCT_NAME = "Product name already exists";
    private static Logger logger = LoggerFactory.getLogger(AddProductController.class);
    @Resource
    private ProductDAO productDAO;
    @Resource
    private ProductService productService;

    /**
     * Method refers to the tariff creation page
     *
     * @param mav representation of the model and view
     * @return {@code ModelAndView} with {@code Product} model attribute
     * @see Product
     */
    @RequestMapping(value = {"addTariff"}, method = RequestMethod.GET)
    public ModelAndView addProduct(ModelAndView mav) {
        mav.addObject("product", new Product());
        mav.setViewName("newPages/admin/addTariff");
        return mav;
    }

    /**
     * Method refers to the service creation page. Get list of
     * all {@code ProductCategories}. Refers to the service creation page
     *
     * @param mav representation of the model and view
     * @return {@code ModelAndView} with {@code Product} model attribute
     * @see Product
     */
    @RequestMapping(value = {"addService"}, method = RequestMethod.GET)
    public ModelAndView addService(ModelAndView mav) {
        List<ProductCategories> productCategories = productDAO.getProductCategories();
        logger.debug("Get all service's categories");
        mav.addObject("productCategories", productCategories);
        mav.addObject("product", new Product());
        mav.addObject("newProductCategories", new ProductCategories());
        mav.setViewName("newPages/admin/addService");
        return mav;
    }

    /**
     * Method check that all fields are correct and save new tariff to database.
     * After redirect to another controller to fill tariff with services.
     * If there are errors - method returns the same page and displays an error message
     * Redirect to the view for filling the tariff with services
     *
     * @param product {@code Product} object
     * @param mav     representation of the model and view
     * @return redirect to another controller to fill tariff with services
     * @see Product
     */
    @RequestMapping(value = {"addTariff"}, method = RequestMethod.POST)
    public ModelAndView createTariff(Product product, ModelAndView mav) {
        product.setProductType(ProductType.Tariff);
        boolean isExistProductName = productService.isExistProductName(product);
        if (isExistProductName) {
            logger.error("Tariff name already exist");
            mav.addObject("error", ERROR_EXIST_PRODUCT_NAME);
            mav.setViewName("newPages/admin/addTariff");
            return mav;
        }
        boolean isEmptyFieldsOfTariff = productService.isEmptyFieldOfProduct(product);
        logger.debug("Check that the incoming tariff fields are not empty {} ", isEmptyFieldsOfTariff);
        productService.validateBasePriceByCustomerType(product);
        if (isEmptyFieldsOfTariff) {
            logger.error("Incoming request has empty fields");
            mav.addObject("error", ERROR_WRONG_FIELDS);
            mav.setViewName("newPages/admin/addTariff");
            return mav;
        }
        Integer isSave = productService.saveProduct(product);
        mav.addObject("tariffId", isSave);
        logger.debug("Save product was success {} ", isSave);
        mav.setViewName("redirect:/admin/fillTariff");
        return mav;
    }

    /**
     * Method check that all fields are correct and save new service to database.
     * After redirect to another controller to fill tariff with services.
     * If there are new category - method check that it' s unique.
     * If there are errors - method returns the same page and displays an error message.
     * Redirect to the page for filling the product with prices if {@code Product} type
     * is 'Residential'. If {@code Product} type 'Business' redirect to view with
     * all products.
     *
     * @param product {@code Product} object
     * @param mav     representation of the model and view
     * @return redirect to controller to fill tariff with services
     * @see Product
     * @see ProductCategories
     */
    @RequestMapping(value = {"addService"}, method = RequestMethod.POST)
    public ModelAndView createService(ModelAndView mav,
                                      ProductCategories productCategory,
                                      Product product) {
        product.setProductType(ProductType.Service);
        boolean isExistProductName = productService.isExistProductName(product);
        if (isExistProductName) {
            logger.error("Service name already exist");
            List<ProductCategories> productCategories = productDAO.getProductCategories();
            logger.debug("Get all service's categories");
            mav.addObject("productCategories", productCategories);
            mav.addObject("error", ERROR_EXIST_PRODUCT_NAME);
            mav.setViewName("newPages/admin/addService");
            return mav;
        }
        boolean isEmptyFieldsOfService = productService.isEmptyFieldOfProduct(product);
        logger.debug("Check that the incoming tariff fields are not empty {} ", isEmptyFieldsOfService);
        boolean isEmptyFieldsOfNewCategory = productService.isEmptyFieldsOfNewCategory(productCategory);
        logger.debug("Check that the incoming new category fields are not empty {} ", isEmptyFieldsOfNewCategory);
        productService.validateBasePriceByCustomerType(product);

        if (isEmptyFieldsOfService || isEmptyFieldsOfNewCategory) {
            mav.addObject("error", ERROR_WRONG_FIELDS);
            logger.error("Incoming request has empty fields");
            List<ProductCategories> productCategories = productDAO.getProductCategories();
            logger.debug("Get all service's categories {} ", productCategories.toString());
            mav.addObject("productCategories", productCategories);
            mav.setViewName("newPages/admin/addService");
            return mav;
        }
        try {
            product = productService.getCategory(productCategory, product);
            logger.debug("Set category for tariff {} ", product.getCategoryId());

        } catch (DuplicateKeyException e) {
            logger.error(ERROR_EXIST_OF_CATEGORY, e);
            List<ProductCategories> productCategories = productDAO.getProductCategories();
            logger.debug("Get all service's categories {} ", productCategories.toString());
            mav.addObject("productCategories", productCategories);
            mav.addObject("error", ERROR_EXIST_OF_CATEGORY);
            mav.setViewName("newPages/admin/addService");
            return mav;
        }
        Integer isSave = productService.saveProduct(product);
        logger.debug("Save product was success with id {} ", isSave);
        if (product.getCustomerType() == CustomerType.Residential) {
            mav.setViewName("redirect:/admin/fillProductPrices?id=" + isSave);
        }
        if (product.getCustomerType() == CustomerType.Business) {
            mav.setViewName("redirect:/admin/getProducts");
        }
        return mav;
    }
}
