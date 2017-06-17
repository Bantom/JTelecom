package jtelecom.dto.grid;

/**
 * Class contains information about next part of the objects
 * Need for object tables
 */

public class GridRequestDTO {
    private int start;
    private int length;
    private String search = "";
    private String sort = "";

    public GridRequestDTO() {
    }

    public GridRequestDTO(int start, int length) {
        this.start = start;
        this.length = length;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getEndBorder() {
        return getStartBorder() + length;
    }

    public int getStartBorder() {
        return length * start;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public int getBegin() {
        return getStart() * getLength();
    }

    @Override
    public String toString() {
        String boundsInfo = start + ": " + length;
        if (!search.isEmpty()) {
            boundsInfo += ", s: " + search;
        }
        if (!sort.isEmpty()) {
            boundsInfo += ", sorting: " + sort;
        }
        return boundsInfo;
    }

}