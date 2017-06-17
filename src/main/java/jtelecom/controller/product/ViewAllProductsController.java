package jtelecom.controller.product;

import jtelecom.dao.price.PriceDAO;
import jtelecom.dao.product.ProductDAO;
import jtelecom.dto.PriceByRegionDTO;
import jtelecom.dto.ProductWithTypeNameDTO;
import jtelecom.dto.grid.GridRequestDTO;
import jtelecom.dto.grid.ListHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Anna Rysakova
 */
@RestController
@RequestMapping({"admin", "csr", "pmg"})
public class ViewAllProductsController {
    private static Logger logger = LoggerFactory.getLogger(ViewAllProductsController.class);
    @Resource
    private ProductDAO productDAO;
    @Resource
    private PriceDAO priceDAO;

    /**
     * This method gets GridRequestDTO( see the {@link GridRequestDTO}.
     * After method gets list with all product info.
     *
     * @param request contains indexes for first element and last elements and
     *                patterns for search and sort.
     * @return class which contains number of all elements with such parameters
     * and some interval of the data
     * @see ProductWithTypeNameDTO
     * @see GridRequestDTO
     */
    @RequestMapping(value = {"all"}, method = RequestMethod.GET)
    public ListHolder servicesByTariff(@ModelAttribute GridRequestDTO request) {
        String sort = request.getSort();
        int start = request.getStartBorder();
        int length = request.getEndBorder();
        String search = request.getSearch();
        List<ProductWithTypeNameDTO> data = productDAO.getLimitedQuantityProduct(start, length, sort, search);
        int size = productDAO.getCountProductsWithSearch(search);
        return ListHolder.create(data, size);
    }

    /**
     * Method get product's prices in regions info.
     *
     * @param startIndex index of first element
     * @param endIndex   index of last element
     * @param productId  {@code Product} ID
     * @return product's prices in regions info
     * @see PriceByRegionDTO
     */
    @RequestMapping(value = {"getPrices"}, method = RequestMethod.GET)
    public ListHolder getPrices(@RequestParam(name = "start") int startIndex,
                                @RequestParam(name = "end") int endIndex,
                                @RequestParam(name = "productId") int productId) {
        List<PriceByRegionDTO> data = priceDAO.getLimitedQuantityProductPricesInRegions(productId, startIndex, endIndex, "", "");
        int size = priceDAO.getCountPriceByPlace("", productId);
        return ListHolder.create(data, size);
    }

}

