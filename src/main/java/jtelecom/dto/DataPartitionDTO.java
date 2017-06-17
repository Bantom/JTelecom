package jtelecom.dto;


import java.util.List;

/**
 * This class created to send part of data and length of all in one object, need for pagination.
 *
 * @author Revniuk Aleksandr
 */
public class DataPartitionDTO<T> {
    private Integer amount;
    private List<T> partOfData;

    public DataPartitionDTO() {
    }

    public DataPartitionDTO(Integer amount, List<T> complaints) {
        this.amount = amount;
        this.partOfData = complaints;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public List<T> getPartOfData() {
        return partOfData;
    }

    public void setPartOfData(List<T> partOfData) {
        this.partOfData = partOfData;
    }

    @Override
    public String toString() {
        return "DataPartitionDTO{" +
                "amount=" + amount +
                ", partOfData=" + partOfData +
                '}';
    }
}
