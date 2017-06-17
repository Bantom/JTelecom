package jtelecom.controller.csr;

import jtelecom.dao.product.ProductDAO;
import jtelecom.dto.ProductWithTypeNameDTO;
import jtelecom.dto.grid.GridRequestDTO;
import jtelecom.dto.grid.ListHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Moiseienko Petro
 * @since 09.05.2017.
 */
@RestController
@RequestMapping({"csr", "pmg"})
public class ViewProductsController {

    @Resource
    private ProductDAO productDAO;
    private static Logger logger = LoggerFactory.getLogger(ViewProductsController.class);

    @RequestMapping(value = {"getPmgProductsPage"}, method = RequestMethod.GET)
    public ModelAndView getPmgProductsPage() {
        return new ModelAndView("newPages/pmg/Products");
    }

    @RequestMapping(value = {"getCsrProductsPage"}, method = RequestMethod.GET)
    public ModelAndView getCsrProductsPage() {
        return new ModelAndView("newPages/csr/Products");
    }

    @RequestMapping(value = {"getProducts"}, method = RequestMethod.GET)
    public ListHolder getProducts(@ModelAttribute GridRequestDTO request) {
        String sort = request.getSort();
        int start = request.getStartBorder();
        int length = request.getLength();
        String search = request.getSearch();
        logger.debug("Get products in interval: {} : {}",start,length);
        List<ProductWithTypeNameDTO> data = productDAO.getLimitedQuantityProduct(start, length, sort, search);
        int size = productDAO.getCountProductsWithSearch(search);
        return ListHolder.create(data, size);
    }

}
