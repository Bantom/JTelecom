package jtelecom.dao.product;

import java.util.Objects;

/**
 * @author Anna Rysakova
 */
public class ProductCategories {

    private Integer id;
    private String categoryName;
    private String categoryDescription;

    public ProductCategories() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String newCategory) {
        this.categoryName = newCategory;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String newCategoryDesc) {
        this.categoryDescription = newCategoryDesc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductCategories)) return false;
        ProductCategories that = (ProductCategories) o;
        return getId() == that.getId() &&
                Objects.equals(getCategoryName(), that.getCategoryName()) &&
                Objects.equals(getCategoryDescription(), that.getCategoryDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCategoryName(), getCategoryDescription());
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("ProductCategories{")
                .append("id=").append(id)
                .append(", categoryName='").append(categoryName).append('\'')
                .append(", categoryDescription='").append(categoryDescription).append('\'')
                .append('}').toString();
    }
}