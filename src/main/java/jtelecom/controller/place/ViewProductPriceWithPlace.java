package jtelecom.controller.place;

import jtelecom.dao.place.Place;
import jtelecom.dao.place.PlaceDAO;
import jtelecom.dao.price.PriceDAO;
import jtelecom.dto.PriceByRegionDTO;
import jtelecom.dto.grid.GridRequestDTO;
import jtelecom.dto.grid.ListHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Anna Rysakova
 */
@RestController
@RequestMapping({"admin"})
public class ViewProductPriceWithPlace {
    private static Logger logger = LoggerFactory.getLogger(ViewProductPriceWithPlace.class);
    @Resource
    private PlaceDAO placeDAO;
    @Resource
    private PriceDAO priceDAO;

    /**
     * This method gets GridRequestDTO( see the {@link GridRequestDTO}.
     * After method gets list with all places.
     *
     * @param request contains indexes for first element and last elements and
     *                patterns for search and sort.
     * @return class which contains number of all elements with such parameters
     * and some interval of the data
     * @see Place
     * @see GridRequestDTO
     */
    @RequestMapping(value = {"allPlace"}, method = RequestMethod.GET)
    public ListHolder allPlaces(@ModelAttribute GridRequestDTO request) {
        String sort = request.getSort();
        int start = request.getStartBorder();
        int length = request.getLength();
        String search = request.getSearch();
        List<Place> data = placeDAO.getLimitedQuantityPlace(start, length, sort, search);
        int size = placeDAO.getCountPlacesWithSearch(search);
        return ListHolder.create(data, size);
    }

    /**
     * Method refers to view with all products that works in region
     * with input {@code ID}
     *
     * @param id  {@code Place} ID
     * @param mav representation of the model and view
     * @return refers to view with all products that works in region
     * with input {@code ID}
     */
    @RequestMapping(value = {"viewPriceByPlace"}, method = RequestMethod.GET)
    public ModelAndView getDetailsPriceByRegion(@RequestParam(value = "id") Integer id, ModelAndView mav) {
        mav.addObject("id", id);
        String placeName = placeDAO.getPlaceNameById(id);
        mav.addObject("placeName", placeName);
        mav.setViewName("newPages/admin/viewProductsPriceByRegion");
        return mav;
    }

    /**
     * This method gets GridRequestDTO( see the {@link GridRequestDTO}.
     * After method gets list with all product prices by regions.
     *
     * @param request contains indexes for first element and last elements and
     *                patterns for search and sort.
     * @return class which contains number of all elements with such parameters
     * and some interval of the data
     * @see Place
     * @see GridRequestDTO
     * @see PriceByRegionDTO
     */
    @RequestMapping(value = {"getDetailsPriceByPlace/{id}"}, method = RequestMethod.GET)
    @ResponseBody
    public ListHolder allPricesByPlace(@ModelAttribute GridRequestDTO request,
                                       @PathVariable(value = "id") Integer placeId) {
        String sort = request.getSort();
        int start = request.getStartBorder();
        int length = request.getLength();
        String search = request.getSearch();
        List<PriceByRegionDTO> data = placeDAO.getLimitedQuantityPriceByPlace(placeId, start, length, sort, search);
        int size = placeDAO.getCountPriceByPlace(search, placeId);
        return ListHolder.create(data, size);
    }

    /**
     * This method gets GridRequestDTO( see the {@link GridRequestDTO}.
     * After method gets list with product prices in regions.
     *
     * @param request contains indexes for first element and last elements and
     *                patterns for search and sort.
     * @return class which contains number of all elements with such parameters
     * and some interval of the data
     * @see Place
     * @see GridRequestDTO
     * @see PriceByRegionDTO
     */
    @RequestMapping(value = {"productsPriceInRegions/{id}"}, method = RequestMethod.GET)
    @ResponseBody
    public ListHolder productsPriceInRegions(@ModelAttribute GridRequestDTO request,
                                             @PathVariable(value = "id") Integer id) {
        String sort = request.getSort();
        int start = request.getStartBorder();
        int length = request.getEndBorder();
        String search = request.getSearch();
        List<PriceByRegionDTO> data = priceDAO.getLimitedQuantityProductPricesInRegions(id, start, length, sort, search);
        int size = priceDAO.getCountPriceByPlace(search, id);
        return ListHolder.create(data, size);
    }

}

