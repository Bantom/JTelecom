package jtelecom.controller;

import jtelecom.controller.product.ViewAllProductsController;
import jtelecom.dao.price.PriceDAO;
import jtelecom.dao.product.Product;
import jtelecom.dao.product.ProductDAO;
import jtelecom.dto.grid.GridRequestDTO;
import jtelecom.dto.grid.ListHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Nikita Alistratenko
 * @since 02.05.2017
 */
@Controller
public class AboutController {

    private static Logger logger = LoggerFactory.getLogger(ViewAllProductsController.class);

    @Resource
    private ProductDAO productDAO;

    @RequestMapping(value = "/forBusiness")
    public String forBusiness() {
        return "newPages/ForBusiness";
    }

    @RequestMapping(value = "/forCustomers")
    public String forPrivateCustomers() {
        return "newPages/ForPrivateCustomers";
    }

    /**
     * Creates JSON with data for table in ForBusiness.jsp
     *
     * @param request info about table configuration
     * @return {@link ListHolder list} with data for table as JSON
     */
    @RequestMapping(value = {"productForBusiness"}, method = RequestMethod.GET)
    @ResponseBody
    public ListHolder getProductListForBusiness(@ModelAttribute GridRequestDTO request) {
        logger.debug("Created JSON for ForBusiness.jsp");
        String sort = request.getSort();
        int start = request.getStartBorder();
        int length = request.getEndBorder();
        String search = request.getSearch();
        List<Product> data = productDAO.getLimitedActiveProductsForBusiness(start, length, sort, search);
        int size = productDAO.getCountForLimitedActiveProductsForBusiness(search);
        return ListHolder.create(data, size);
    }


    /**
     * Creates JSON with data for table in ForResidential.jsp
     *
     * @param request info about table configuration
     * @return {@link ListHolder list} with data for table as JSON
     */
    @RequestMapping(value = {"productForResidential"}, method = RequestMethod.GET)
    @ResponseBody
    public ListHolder getProductListForResidential(@ModelAttribute GridRequestDTO request) {
        logger.debug("Created JSON for ForResidential.jsp");
        String sort = request.getSort();
        int start = request.getStartBorder();
        int length = request.getEndBorder();
        String search = request.getSearch();
        List<Product> data = productDAO.getLimitedActiveProductsForResidential(start, length, sort, search);
        int size = productDAO.getCountForLimitedActiveProductsForResidential(search);
        return ListHolder.create(data, size);
    }

}