/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtelecom.dao.place;

import java.util.Objects;

/**
 * @author Alistratenko Nikita
 */
public class Place {

    private Integer id;
    private Integer parentId;
    private String name;

    public Place() {
    }

    public Place(int id, int parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Place{")
                .append("id=").append(id)
                .append(", parentId=").append(parentId)
                .append(", name=").append(name)
                .append('}').toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Place)) return false;
        Place place = (Place) o;
        return Objects.equals(getId(), place.getId()) &&
                Objects.equals(getParentId(), place.getParentId()) &&
                Objects.equals(getName(), place.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getParentId(), getName());
    }
}
