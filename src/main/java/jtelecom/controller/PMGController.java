package jtelecom.controller;

import jtelecom.dao.complaint.Complaint;
import jtelecom.dao.complaint.ComplaintDAO;
import jtelecom.dao.user.User;
import jtelecom.dao.user.UserDAO;
import jtelecom.dto.DataPartitionDTO;
import jtelecom.repositories.FullComplaintInfoRepository;
import jtelecom.security.SecurityAuthenticationHelper;
import jtelecom.services.complaint.ComplaintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * This controller has main methods for PMG.
 *
 * @author Revniuk Aleksandr
 * @author Rysakova Anna
 */
@Controller
@RequestMapping("pmg")
public class PMGController {
    @Resource
    private ComplaintDAO complaintDAO;

    @Resource
    private SecurityAuthenticationHelper securityAuthenticationHelper;

    @Resource
    private UserDAO userDAO;

    @Resource
    private FullComplaintInfoRepository fullComplaintInfoRepository;

    @Resource
    private ComplaintService complaintService;

    private static Logger logger = LoggerFactory.getLogger(PMGController.class);

    /**
     * This method return page with all complaints for PMG.
     *
     * @return name of view
     */
    @RequestMapping(value = "allComplaints")
    public String getAllComplaints() {
        return "newPages/pmg/allComplaints";
    }

    /**
     * This method return page with complaints assigned for concrete PMG.
     *
     * @return view name
     */
    @RequestMapping(value = "myComplaints")
    public String getMyComplaints() {
        return "newPages/pmg/myComplaints";
    }

    /**
     * This method return part of complaints for pagination.
     *
     * @param startIndex start index
     * @param endIndex end index
     * @return DTO with part of complaints
     */
    @RequestMapping(value = "getData")
    @ResponseBody
    public DataPartitionDTO getData(@RequestParam(name = "start") int startIndex, @RequestParam(name = "end") int endIndex) {
        DataPartitionDTO<Complaint> dto = new DataPartitionDTO<>();
        dto.setAmount(complaintDAO.countUnassignedComplaintsToUser());
        dto.setPartOfData(complaintDAO.getIntervalOfUnassignedComplaints(startIndex, endIndex));
        return dto;
    }

    /**
     * This method return page with full complaint information by id of complaint.
     *
     * @param model used for adding attributes to the model
     * @param id id of complaint
     * @return view name
     */
    @RequestMapping(value = "complaintInfo")
    public String compalaintInfo(Model model, @RequestParam(name = "id") int id) {
        User user = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        model.addAttribute("complaint", fullComplaintInfoRepository.getById(id));
        model.addAttribute("currentUserId", user.getId());
        return "newPages/pmg/complaintInfo";
    }

    /**
     * This method return part of complaints for pagination using concrete PMG id.
     *
     * @param startIndex start index
     * @param endIndex end index
     * @return DTO with part of complaints
     */
    @RequestMapping(value = "getDataByPMG")
    @ResponseBody
    public DataPartitionDTO getDataByPMG(@RequestParam(name = "start") int startIndex, @RequestParam(name = "end") int endIndex) {
        User user = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        DataPartitionDTO<Complaint> dto = new DataPartitionDTO<>();
        dto.setAmount(complaintDAO.countAssignedComplaintsToUser(user.getId()));
        dto.setPartOfData(complaintDAO.getIntervalOfAssignedComplaints(user.getId(), startIndex, endIndex));
        return dto;
    }

    /**
     * This method assign complaint to concrete PMG.
     *
     * @param model used for adding attributes to the model
     * @param complaintId  id of complaint
     * @return forward to page with complaint info
     */
    @RequestMapping(value = "assignTo")
    public String assignTo(Model model, @RequestParam(name = "id") int complaintId) {
        User user = userDAO.findByEmail(securityAuthenticationHelper.getCurrentUser().getUsername());
        boolean success = complaintDAO.assignToUser(complaintId, user.getId());
        if (success) {
            logger.debug("Complaint with id {} successful assigned to user with id {}", complaintId, user.getId());
            return "forward:complaintInfo?id=" + complaintId;
        } else {
            logger.error("Can't assign complaint with id {} to user with id {}", complaintId, user.getId());
            model.addAttribute("msg", "Can't be assinged to you");
            return "forward:complaintInfo?id=" + complaintId;
        }
    }

    /**
     * This method change status for complaint.
     *
     * @param model used for adding attributes to the model
     * @param id id of complaint
     * @param statusId id of status
     * @return forward to page with complaint info or to page with user complaints if status id was 3(Processed).
     */
    @RequestMapping(value = "changeStatus")
    public String changeStatus(Model model, @RequestParam(name = "id") int id, @RequestParam(name = "statusId") int statusId) {
        boolean success = complaintService.changeStatus(id, statusId);
        if (success) {
            logger.debug("Status of complaint with id {} successful changed to status {}", id, statusId);
            if (statusId == 3) {
                return "redirect:myComplaints";
            } else {
                return "forward:complaintInfo?id=" + id;
            }
        } else {
            logger.error("Can't change status for complaint with id {}", id);
            model.addAttribute("msg", "Can't change status");
            return "forward:complaintInfo?id=" + id;
        }
    }
}
