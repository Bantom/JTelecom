package jtelecom.dao.price;

import jtelecom.dto.PriceByRegionDTO;

import java.util.List;


/**
 * @author Anna Rysakova
 */
public interface PriceDAO {

    /**
     * Method save product price by region ID
     *
     * @param priceByRegionDTO {@code List} of product prices by regions
     * @see List
     * @see Price
     */
    boolean fillPriceOfProductByRegion(List<Price> priceByRegionDTO);

    /**
     * Method return {@link List} with all regions and prices for these regions,
     * if price in region exists.
     *
     * @param productId {@code Product} ID
     * @return {@code List} with all regions and prices for these regions,
     * if price in region exists.
     * @see List
     */
    List<PriceByRegionDTO> getAllRegionsAndProductPriceInRegionByProductId(int productId);

    /**
     * Method returns price in regions by {@code Product} ID
     *
     * @param productId {@code Product} ID
     * @return price in regions by {@code Product} ID
     * @see Price
     * @see List
     */
    List<Price> getPriceInRegionInfoByProduct(int productId);

    /**
     * Method delete price in regions by {@code Product} ID
     *
     * @param priceInRegion {@code Product} ID
     * @return result after delete price in regions by {@code Product} ID
     * @see Price
     * @see List
     */
    boolean deleteProductPriceInRegion(List<Price> priceInRegion);

    /**
     * Method return {@link List} of {@code PriceByRegionDTO} by criteria {@code start},
     * {@code length},{@code sort}, {@code search}
     *
     * @param start  start line number
     * @param length end line number
     * @param sort   field by which sorting is performed, by default - ID
     * @param search search criteria
     * @return {@code List} of {@code PriceByRegionDTO} by criteria {@code start},
     * {@code length},{@code sort}, {@code search}
     * @see List
     * @see PriceByRegionDTO
     */
    List<PriceByRegionDTO> getLimitedQuantityProductPricesInRegions(int productId, int start, int length, String sort, String search);

    /**
     * Method count the amount of products that match the current {@code search} criteria
     * and {@code Product} ID
     *
     * @param search search criteria
     * @return count the amount of prices by place that match the current {@code search} criteria
     */
    Integer getCountPriceByPlace(String search, int productId);

}
