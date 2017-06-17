package jtelecom.dao.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Created by Yuliya Pedash on 05.05.2017.
 */
public enum ProcessingStrategy {
    NeedProcessing(1, "Needs Processing"),
    DoNotNeedProcessing(0, "Doesn't need Processing");
    private static Logger logger = LoggerFactory.getLogger(ProcessingStrategy.class);
    private static String WRONG_ID_ERROR_MSG = "Wrong id: ";
    private Integer id;
    private String name;

    ProcessingStrategy(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * This method gets <code>ProcessingStrategy</code> object by id
     *
     * @param id id of processing strategy
     * @return ProcessingStrategy object
     */
    public static ProcessingStrategy getProcessingStrategyFromId(Integer id) {
        ProcessingStrategy[] customerTypes = values();
        for (ProcessingStrategy customerType : customerTypes) {
            if (Objects.equals(customerType.getId(), id)) {
                return customerType;
            }
        }
        logger.error(WRONG_ID_ERROR_MSG + id);
        throw new IllegalArgumentException(WRONG_ID_ERROR_MSG + id);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
