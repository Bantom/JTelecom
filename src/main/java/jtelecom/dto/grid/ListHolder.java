package jtelecom.dto.grid;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Class created for send part of data and it's total count in the database
 * @param <T> type of the data
 */

public class ListHolder<T> {
    @JsonProperty("data")
    List<T> data = new ArrayList<T>();

    @JsonProperty("total")
    int totalCount;

    public ListHolder() {

    }

    private ListHolder(List<T> data, long totalCount) {
        this.data.addAll(data);
        this.totalCount = (int) totalCount;
    }

    public static <T> ListHolder<T> create(List<T> data, long totalCount) {
        return new ListHolder<>(data, totalCount);
    }

    public List<T> getData() {
        return new ArrayList<>(data);
    }

    public int getTotalCount() {
        return totalCount;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("ListHolder{")
                .append("data size=").append(data.size())
                .append(", totalCount=").append(totalCount)
                .append('}').toString();
    }
}