package jtelecom.controller.admin;

import jtelecom.dao.product.Product;
import jtelecom.dao.product.ProductDAO;
import jtelecom.services.product.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Nikita on 02.05.2017.
 */
@Controller
@RequestMapping({"admin"})
public class DeleteTariffController {

    @Resource
    private ProductDAO productDAO;
    @Resource
    private ProductService productService;

    private Logger logger = LoggerFactory.getLogger(DeleteTariffController.class);

    @RequestMapping(value = "deleteTariff", method = RequestMethod.GET)
    public String getTariff(Model model) {
        List<Product> products = productDAO.getAllEnabledTariffs();
        model.addAttribute("productList", products);
        return "admin/deleteTariff";
    }

    @RequestMapping(value = "deleteTariff", method = RequestMethod.POST)
    public ModelAndView deleteTariff(@RequestParam(value = "tariff", required = false) Integer tariffID) {
        if (tariffID != null) {
            productService.disableEnableProduct(tariffID);
        }
        return new ModelAndView("redirect:/admin/deleteTariff");
    }

}
