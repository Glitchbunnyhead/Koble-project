package com.koble.koble.persistence;

import java.util.List;

public interface Crudl<T> {

    public T create(T entity);

    public String delete(long idEntity);

    public T update(long idEntity, T entity);

    public T read(long idEntity);

    public List<T> listAll();
    
}

