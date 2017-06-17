package jtelecom.dao.product;

import jtelecom.dao.entity.CustomerType;
import jtelecom.dao.interfaces.DAO;
import jtelecom.dto.ProductWithTypeNameDTO;
import jtelecom.dto.TariffServiceDTO;

import java.util.List;
import java.util.Map;

/**
 * @author Anna Rysakova
 * @author Yuliya Pedash
 * @author Anton Bulgakov
 * @author Moiseienko Petro
 * @author Alistratenko Nikita
 */
public interface ProductDAO extends DAO<Product> {

    /**
     * Method save Product object
     *
     * @param product Product object
     * @return {@code ID} of current product in database
     * @see Product
     */
    Integer saveProduct(Product product);

    /**
     * Method save ProductCategories object
     *
     * @param categories ProductCategories object
     * @return {@code ID} of current category in database
     * @see ProductCategories
     */
    Integer saveCategory(ProductCategories categories);

    /**
     * Method return product type by product ID
     *
     * @param productId product ID
     * @return product type by product ID
     * @see ProductType
     */
    String getProductTypeByProductId(int productId);

    /**
     * Method return customer type by product ID
     *
     * @param productId product ID
     * @return customer type by product ID
     * @see CustomerType
     */
    String getCustomerTypeByProductId(int productId);

    /**
     * Method return {@link List} of ProductCategories
     *
     * @return {@code List} of ProductCategories
     * @see ProductCategories
     * @see List
     */
    List<ProductCategories> getProductCategories();

    /**
     * The method is returned to the {@link Map},
     * where the key is the category name,
     * the value is the {@link List} of services for this category
     *
     * @return {@code Map}, where the key is the category name,
     * the value is the {@code List} of services for this category
     * @see Map
     * @see List
     */
    Map<String, List<Product>> getAllServicesWithCategory();

    /**
     * Method return {@link List} of services by tariff ID
     *
     * @param tariffId tariff ID
     * @return Method return {@code List} of services by tariff ID
     * @see List
     * @see TariffServiceDTO
     */
    List<TariffServiceDTO> getServicesInfoByTariff(int tariffId);

    /**
     * Method return {@link List} of services {@code ID}
     * by tariff ID
     *
     * @param tariffId tariff ID
     * @return Method return {@code List} of services by tariff ID
     * @see List
     * @see TariffServiceDTO
     */
    List<TariffServiceDTO> getServicesIDByTariff(int tariffId);

    /**
     * The method is returned to the {@link Map},
     * where the key is the category name,
     * the value is the list of services for this category,
     * which is not included in the tariff
     *
     * @param tariffId tariff ID
     * @return {@code Map}, where the key is the category name,
     * the value is the list of services for this category
     * @see Map
     * @see List
     */
    Map<String, List<Product>> getServicesNotInTariff(int tariffId);

    /**
     * Method count the amount of products that match the current {@code search} criteria
     *
     * @param search search criteria
     * @return count the amount of products that match the current {@code search} criteria
     */
    Integer getCountProductsWithSearch(String search);

    /**
     * Method return {@link List} of {@code Product} by criteria {@code start},
     * {@code length},{@code sort}, {@code search}
     *
     * @param start  start line number
     * @param length end line number
     * @param sort   field by which sorting is performed, by default - ID
     * @param search search criteria
     * @return {@code List} of {@code Product} by criteria {@code start},
     * {@code length},{@code sort}, {@code search}
     * @see List
     * @see Product
     */
    List<ProductWithTypeNameDTO> getLimitedQuantityProduct(int start, int length, String sort, String search);

    /**
     * Method save services by tariff ID
     *
     * @param tariffServiceDTO {@code List} of services by tariff with tariff ID
     * @see List
     * @see TariffServiceDTO
     */
    void fillInTariffWithServices(List<TariffServiceDTO> tariffServiceDTO);

    /**
     * Method remove services from tariff
     *
     * @param tariffServiceDTO {@code List} of services by tariff,
     *                         which need to be removed
     * @see List
     * @see TariffServiceDTO
     */
    void deleteServiceFromTariff(List<TariffServiceDTO> tariffServiceDTO);

    /**
     * Method search {@code Product} name by incoming object {@code Product}
     * Return {@code true} if object doesn't exist, {@code false} if exist.
     *
     * @param product {@code Product} object.
     * @return {@code true} if object doesn't exist, {@code false} if exist.
     * @see Product
     */
    boolean getProductName(Product product);

    /**
     * Method search {@code Product} {@code Product} by incoming object {@code Product}
     * Return {@code Product} object if object  exist, {@code null} if doesn't exist.
     *
     * @param product {@code Product} object.
     * @return {@code true} if object doesn't exist, {@code false} if exist.
     * @see Product
     */
    boolean getProductByName(Product product, int productId);

    /**
     * Gets category of admin by id
     *
     * @param categoryId id of category
     * @return ProductCategory
     */

    ProductCategories getProductCategoryById(Integer categoryId);

    /**
     * Method returns all tariffs are available in place with id from params. If there are no tariffs in this place, method returns empty list.
     *
     * @param placeId id of place.
     * @return list of tariffs.
     */
    List<Product> getAvailableTariffsByPlace(Integer placeId);

    /**
     * Method returns list of tariffs from interval with first and end number from params.
     * If there are no tariffs in this place, method returns empty list.
     *
     * @param placeId    id of place.
     * @param startIndex number of the first tariff.
     * @param endIndex   number of the end tariff.
     * @return list with Tariffs from interval.
     */
    List<Product> getIntervalOfTariffsByPlace(Integer placeId, Integer startIndex, Integer endIndex);

    /**
     * Method returns quantity of all tariffs are available in place with id from params. If there are no tariffs in this place, method returns null.
     *
     * @param placeId id of place.
     * @return quantity of tariffs.
     */
    Integer getQuantityOfAllAvailableTariffsByPlaceId(Integer placeId);


    boolean disableEnableProduct(Product product);

    List<Product> getProductsByUserId(int id);

    List<Product> getActiveProductsByUserId(Integer id);

    /**
     * Method update status of current tariff of user on Deactivate.
     *
     * @param userId   user id.
     * @param tariffId tariff id.
     * @return status of deactivation.
     */
    boolean deactivateTariff(Integer userId, Integer tariffId);

    /**
     * Method returns all tariffs are available for customers. If there are no tariffs in this place, method returns empty list.
     *
     * @return list of tariffs.
     */
    List<Product> getAvailableTariffsForCustomers();

    /**
     * Method returns list of tariffs for customers from interval with borders from params.
     * If there are no tariffs in this place, method returns empty list.
     *
     * @param startIndex number of first tariff.
     * @param endIndex   number of end tariff.
     * @return list of tariff from interval.
     */
    List<Product> getIntervalOfTariffsForCustomers(Integer startIndex, Integer endIndex);

    /**
     * Method returns quantity of all tariffs are available for customers. If there are no tariffs in this place, method returns null.
     *
     * @return quantity of tariffs.
     */
    Integer getQuantityOfAllAvailableTariffsForCustomers();

    /**
     * Method returns all services that are available for customers.
     * If there are no services for customer, method returns empty list.
     *
     * @return
     */
    List<Product> getServicesAvailableForCustomer();

    /**
     * Method returns tariff of customer according to customer id from params.
     * If no such customer or customer doesn`t have active tariff, method returns null.
     *
     * @param customerId id of customer.
     * @return tariff of customer.
     */
    Product getCurrentCustomerTariff(Integer customerId);

    /**
     * Method creates order for activation new tariff for user with id from params.
     * If user has already had tariff, old tariff will be deactivated.
     *
     * @param userId   id of user.
     * @param tariffId id of tariff.
     * @return status of operation.
     */
    boolean activateTariff(Integer userId, Integer tariffId);

    /**
     * Method returns list of services are in tariff with id from params.
     * If no tariff with such id or no services, method returns empty list.
     *
     * @param tariffId id of tariff.
     * @return list of services.
     */
    List<Product> getServicesOfTariff(Integer tariffId);

    /**
     * Method deletes all planned tasks according to userId and tariffId from params.
     *
     * @param userid   id of user.
     * @param tariffId id of tariff.
     * @return status of operation.
     */
    boolean deletePlannedTasks(Integer userid, Integer tariffId);

    /**
     * This method returns all services that are included in current user's Tariff.
     *
     * @param userId id of user
     * @return list of products with service type.
     */
    List<Product> getAllServicesByCurrentUserTariff(Integer userId);

    /**
     * This method returns Product object in which <code>basePrice</code>
     * will be determined by price configured for place in prices table.
     *
     * @param productId id of product
     * @param placeId   id of place
     * @return found Product
     */
    Product findProductWithPriceSetByPlace(Integer productId, Integer placeId);

    /**
     * This method returns list of available products with  type 'Service' for user with role 'Business.
     * limited by start index, end index, category, and search and sorted by sort.
     *
     * @param start      start index
     * @param end        end index
     * @param sort       sort column
     * @param search     search pattern
     * @param categoryId id of category
     * @return list of services
     * @see jtelecom.dao.user.Role
     * @see ProductType
     */
    List<Product> getLimitedServicesForBusiness(Integer start, Integer end, String sort, String search, Integer categoryId);

    /**
     * This method returns list of available products with  type 'Service' for user with role 'Residential.
     * limited by start index, end index, category, place and search and sorted by sort.
     *
     * @param start      start index
     * @param end        end index
     * @param sort       sort column
     * @param search     search pattern
     * @param categoryId id of category
     * @param placeId    id o place
     * @return list of services
     * @see jtelecom.dao.user.Role
     * @see ProductType
     */
    List<Product> getLimitedServicesForResidential(Integer start, Integer end, String sort, String search, Integer categoryId, Integer placeId);

    /**
     * This method returns total count for products with  type 'Service' for user with role 'Business'.
     * limited by search and category
     *
     * @param search search pattern
     * @param categoryId id of category
     * @return total count
     * @see jtelecom.dao.user.Role
     * @see ProductType
     */
    Integer getCountForLimitedServicesForBusiness(String search, Integer categoryId);

    /**
     * This method returns total count for products with  type 'Service' for user with role 'Residential'.
     * limited by search, place and category
     * @param search search pattern
     * @param categoryId id of category
     * @param placeId if of place
     * @return total count
     */
    Integer getCountForLimitedServicesForResidential(String search, Integer categoryId, Integer placeId);

    /**
     * Returns list of available (status=1) products for business type user
     *
     * @param start  index of first element
     * @param length the length of the elements list to select
     * @param sort   name of the column to sort by
     * @param search search pattern
     * @return list of products
     */
    List<Product> getLimitedActiveProductsForBusiness(Integer start, Integer length, String sort, String search);

    /**
     * Returns list of available (status=1) products for residential type user
     *
     * @param start  index of first element
     * @param length the length of the elements list to select
     * @param sort   name of the column to sort by
     * @param search search pattern
     * @return list of products
     */
    List<Product> getLimitedActiveProductsForResidential(Integer start, Integer length, String sort, String search);

    /**
     * Returns total number of elements that might be get with
     * {@link ProductDAO#getLimitedActiveProductsForBusiness(Integer, Integer, String, String) getLimitedActiveProductsForBusiness} method
     *
     * @param search search pattern
     * @return total count of products
     */
    Integer getCountForLimitedActiveProductsForBusiness(String search);

    /**
     * Returns total number of elements that might be get with
     * {@link ProductDAO#getLimitedActiveProductsForResidential(Integer, Integer, String, String)}  getLimitedActiveProductsForResidential} method
     *
     * @param search search pattern
     * @return total count of products
     */
    Integer getCountForLimitedActiveProductsForResidential(String search);

    List<Product> getAllEnabledTariffs();

    /**
     * Get Product object by id of order
     *
     * @param orderId id of order
     * @return Product object
     */
    Product getProductByOrderId(int orderId);

}