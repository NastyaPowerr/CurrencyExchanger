package org.roadmap.currencyexchanger.dao;

import java.util.List;

public interface CrudDao<T> {
    T save(T entity);

    List<T> findAll();
}
