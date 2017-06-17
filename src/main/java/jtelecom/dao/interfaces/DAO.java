package jtelecom.dao.interfaces;

/**
 * @author Yuliya Pedash
 * @author Anna Rysakova
 */
public interface DAO<T> {
    T getById(int id);

    boolean update(T object);

    boolean save(T object);

    boolean delete(T object);
}
