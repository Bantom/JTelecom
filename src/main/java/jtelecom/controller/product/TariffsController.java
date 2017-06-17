package jtelecom.controller.product;

import jtelecom.dao.product.Product;
import jtelecom.dao.product.ProductDAO;
import jtelecom.dao.user.Role;
import jtelecom.dao.user.User;
import jtelecom.dao.user.UserDAO;
import jtelecom.dto.TariffsDataPartitionDTO;
import jtelecom.security.SecurityAuthenticationHelper;
import jtelecom.services.orders.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author Anton Bulgakov
 * @since 04.05.2017.
 * <p>
 * Class is responsible for all actions with tariffs on all jsp pages.
 */
@Controller
@RequestMapping({"residential", "business", "csr"})
public class TariffsController {

    @Resource
    private SecurityAuthenticationHelper securityAuthenticationHelper;
    @Resource
    private ProductDAO productDAO;
    @Resource
    private UserDAO userDAO;
    @Resource
    private OrderService orderService;

    private static Logger logger = LoggerFactory.getLogger(TariffsController.class);

    /**
     * Method gets available tariffs for user and show them on the jsp page.
     *
     * @return jsp page with list of available tariffs.
     */
    @RequestMapping(value = "tariffs")
    public String showAvailableTariffsForUser() {
        User currentUser = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        return "newPages/" + currentUser.getRole().getNameInLowwerCase() + "/Tariffs";
    }

    /**
     * Method used for showing available user`s tariffs for CSR.
     *
     * @return jsp page with tariffs.
     */
    @RequestMapping(value = "availableUserTariffs")
    public String showAvailableTariffsForCSR(HttpSession session, RedirectAttributes attributes) {
        Integer userId = (Integer) session.getAttribute("userId");
        User user = userDAO.getUserById(userId);
        if (user.getRole() == Role.EMPLOYEE) {
            attributes.addFlashAttribute("msg", "Employee can't see this page");
            return "redirect:/csr/getUserProfile";
        }
        return "newPages/csr/UserTariffs";
    }

    /**
     * Method used for filling jsp page of tariffs by records of tariffs begin with row number startIndex till endIndex from params.
     *
     * @return dto with tariffs.
     */
    @RequestMapping(value = {"allTariffs"}, method = RequestMethod.GET)
    @ResponseBody
    public TariffsDataPartitionDTO showTariffsForUser(@RequestParam(name = "start") int startIndex, @RequestParam(name = "end") int endIndex) {
        TariffsDataPartitionDTO dto = new TariffsDataPartitionDTO();
        User currentUser = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        logger.debug("Current user: {}", currentUser.toString());
        dto.setCurrentTariff(productDAO.getCurrentCustomerTariff(currentUser.getCustomerId()));
        logger.debug("Current tariff of user: {}", dto.getCurrentTariff());
        if (currentUser.getRole().equals(Role.RESIDENTIAL)) {
            dto.setQuantityOfAllTariffs(productDAO.getQuantityOfAllAvailableTariffsByPlaceId(currentUser.getPlaceId()));
            dto.setPartOfTariffs(productDAO.getIntervalOfTariffsByPlace(currentUser.getPlaceId(), startIndex, endIndex));
        } else {
            dto.setQuantityOfAllTariffs(productDAO.getQuantityOfAllAvailableTariffsForCustomers());
            dto.setPartOfTariffs(productDAO.getIntervalOfTariffsForCustomers(startIndex, endIndex));
        }
        return dto;
    }

    /**
     * Method activates tariff for user with id from params.
     *
     * @param tariffId id of tariff.
     * @param userId   id of user.
     * @return status of activation.
     */
    @RequestMapping(value = {"activateTariff"}, method = RequestMethod.POST)
    @ResponseBody
    public String activateTariff(@RequestParam Integer tariffId, @RequestParam Integer userId) {
        logger.debug("Method activateTariff param tariffId: {}", tariffId);
        User currentUser;
        if (userId == -1) {
            currentUser = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        } else {
            currentUser = userDAO.getUserById(userId);
        }
        logger.debug("Current user: {}", currentUser.toString());
        Boolean statusOperation = orderService.activateTariff(currentUser.getId(), tariffId);
        logger.debug("Status activation of tariff: {}", statusOperation);
        return statusOperation ? "success" : "fail";
    }

    /**
     * Method identified user and deactivate current tariff, if it's exists.
     *
     * @param tariffId id of tariff.
     * @param userId   id of user.
     * @return status of deactivation.
     */
    @RequestMapping(value = {"deactivateTariff"}, method = RequestMethod.POST)
    @ResponseBody
    public String deactivateTariff(@RequestParam Integer tariffId, @RequestParam Integer userId) {
        logger.debug("Method deactivateTariff param tariffId: {}", tariffId);
        User currentUser;
        if (userId == -1) {
            currentUser = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        } else {
            currentUser = userDAO.getUserById(userId);
        }
        logger.debug("Current user: {}", currentUser.toString());
        Boolean statusOperation = orderService.deactivateTariff(currentUser.getId(), tariffId);
        logger.debug("Status deactivation of tariff: {}", statusOperation);
        return statusOperation ? "success" : "fail";
    }

    /**
     * Method gets all services are in tariff and sends it to the ajax function.
     *
     * @param tariffId id of tariff.
     * @return
     */
    @RequestMapping(value = {"showServicesOfTariff"}, method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public List<Product> showServicesOfTariff(@RequestParam Integer tariffId) {
        logger.debug("Method showServicesOfTariff param tariffId: {}", tariffId);
        return productDAO.getServicesOfTariff(tariffId);
    }

    /**
     * Method used for filling jsp page of tariffs by records of tariffs begin with row number startIndex till endIndex from params.
     * It used for showing tariffs of user`s for CSR.
     *
     * @return dto with tariffs.
     */
    @RequestMapping(value = {"userTariffs"})
    @ResponseBody
    public TariffsDataPartitionDTO showTariffsForUser(@RequestParam(name = "start") int startIndex, @RequestParam(name = "end") int endIndex, HttpSession session) {
        TariffsDataPartitionDTO dto = new TariffsDataPartitionDTO();
        Integer userId = (Integer) session.getAttribute("userId");
        User currentUser = userDAO.getUserById(userId);
        logger.debug("Current user: {}", currentUser.toString());
        dto.setCurrentTariff(productDAO.getCurrentCustomerTariff(currentUser.getCustomerId()));
        logger.debug("Current tariff of user: {}", dto.getCurrentTariff());
        if (currentUser.getRole().equals(Role.RESIDENTIAL)) {
            dto.setQuantityOfAllTariffs(productDAO.getQuantityOfAllAvailableTariffsByPlaceId(currentUser.getPlaceId()));
            dto.setPartOfTariffs(productDAO.getIntervalOfTariffsByPlace(currentUser.getPlaceId(), startIndex, endIndex));
            dto.setUserId(userId);
        } else {
            dto.setQuantityOfAllTariffs(productDAO.getQuantityOfAllAvailableTariffsForCustomers());
            dto.setPartOfTariffs(productDAO.getIntervalOfTariffsForCustomers(startIndex, endIndex));
            dto.setUserId(userId);
        }
        return dto;
    }
}
