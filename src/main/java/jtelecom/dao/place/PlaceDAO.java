package jtelecom.dao.place;

import jtelecom.dto.PriceByRegionDTO;

import java.util.List;

/**
 * This interface has methods to work with places.
 *
 * @author Anna Rysakova
 * @author Revniuk Aleksandr
 */
public interface PlaceDAO {
    /**
     * This method returns all places.
     *
     * @return list of places
     */
    List<Place> getAll();

    /**
     * The method returns all regions of Ukraine
     *
     * @return all regions of Ukraine
     */
    List<Place> getAllPlaces();

    /**
     * Method returns {@code Place} name by {@code Place} ID
     *
     * @param id {@code Place} ID
     * @return {@code Place} name by {@code Place} ID
     * @see Place
     */
    String getPlaceNameById(int id);

    /**
     * Method return {@link List} of {@code Place} by criteria {@code start},
     * {@code length},{@code sort}, {@code search}
     *
     * @param start  start line number
     * @param length end line number
     * @param sort   field by which sorting is performed, by default - ID
     * @param search search criteria
     * @return {@code List} of {@code Place} by criteria {@code start},
     * {@code length},{@code sort}, {@code search}
     * @see List
     * @see Place
     */
    List<Place> getLimitedQuantityPlace(int start, int length, String sort, String search);

    /**
     * Method count the amount of places that match the current {@code search} criteria
     * and {@code Product} ID
     *
     * @param search search criteria
     * @return count the amount of places that match the current {@code search} criteria
     */
    Integer getCountPlacesWithSearch(String search);

    /**
     * Method return {@link List} of {@code PriceByRegionDTO} by criteria {@code start},
     * {@code length},{@code sort}, {@code search} and {@code PriceByRegionDTO} ID
     *
     * @param start  start line number
     * @param length end line number
     * @param sort   field by which sorting is performed, by default - ID
     * @param search search criteria
     * @return {@code List} of {@code PriceByRegionDTO}
     * by criteria {@code start},{@code length},
     * {@code sort}, {@code search} and {@code PriceByRegionDTO} ID
     * @see List
     * @see PriceByRegionDTO
     */
    List<PriceByRegionDTO> getLimitedQuantityPriceByPlace(int placeId, int start, int length, String sort, String search);

    /**
     * Method count the amount of prices by place that match the current
     * {@code search} criteria and {@code Product} ID
     *
     * @param search search criteria
     * @return count the amount of prices by place that match the current {@code search} criteria
     */
    Integer getCountPriceByPlace(String search, Integer placeId);

}
